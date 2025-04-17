package net.minecraft.client.renderer.chunk;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.buffers.BufferUsage;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexSorting;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.TracingExecutor;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SectionBufferBuilderPack;
import net.minecraft.client.renderer.SectionBufferBuilderPool;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.Zone;
import net.minecraft.util.thread.ConsecutiveExecutor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SectionRenderDispatcher {
    private final CompileTaskDynamicQueue compileQueue = new CompileTaskDynamicQueue();
    private final Queue<Runnable> toUpload = Queues.newConcurrentLinkedQueue();
    final SectionBufferBuilderPack fixedBuffers;
    private final SectionBufferBuilderPool bufferPool;
    private volatile int toBatchCount;
    private volatile boolean closed;
    private final ConsecutiveExecutor consecutiveExecutor;
    private final TracingExecutor executor;
    ClientLevel level;
    final LevelRenderer renderer;
    private Vec3 camera = Vec3.ZERO;
    final SectionCompiler sectionCompiler;

    public SectionRenderDispatcher(
        ClientLevel p_295274_,
        LevelRenderer p_295323_,
        TracingExecutor p_373128_,
        RenderBuffers p_307511_,
        BlockRenderDispatcher p_350514_,
        BlockEntityRenderDispatcher p_350550_
    ) {
        this.level = p_295274_;
        this.renderer = p_295323_;
        this.fixedBuffers = p_307511_.fixedBufferPack();
        this.bufferPool = p_307511_.sectionBufferPool();
        this.executor = p_373128_;
        this.consecutiveExecutor = new ConsecutiveExecutor(p_373128_, "Section Renderer");
        this.consecutiveExecutor.schedule(this::runTask);
        this.sectionCompiler = new SectionCompiler(p_350514_, p_350550_);
    }

    public void setLevel(ClientLevel p_295112_) {
        this.level = p_295112_;
    }

    private void runTask() {
        if (!this.closed && !this.bufferPool.isEmpty()) {
            SectionRenderDispatcher.RenderSection.CompileTask sectionrenderdispatcher$rendersection$compiletask = this.compileQueue
                .poll(this.getCameraPosition());
            if (sectionrenderdispatcher$rendersection$compiletask != null) {
                SectionBufferBuilderPack sectionbufferbuilderpack = Objects.requireNonNull(this.bufferPool.acquire());
                this.toBatchCount = this.compileQueue.size();
                CompletableFuture.<CompletableFuture<SectionRenderDispatcher.SectionTaskResult>>supplyAsync(
                        () -> sectionrenderdispatcher$rendersection$compiletask.doTask(sectionbufferbuilderpack),
                        this.executor.forName(sectionrenderdispatcher$rendersection$compiletask.name())
                    )
                    .thenCompose(p_296185_ -> (CompletionStage<SectionRenderDispatcher.SectionTaskResult>)p_296185_)
                    .whenComplete((p_370310_, p_370311_) -> {
                        if (p_370311_ != null) {
                            Minecraft.getInstance().delayCrash(CrashReport.forThrowable(p_370311_, "Batching sections"));
                        } else {
                            sectionrenderdispatcher$rendersection$compiletask.isCompleted.set(true);
                            this.consecutiveExecutor.schedule(() -> {
                                if (p_370310_ == SectionRenderDispatcher.SectionTaskResult.SUCCESSFUL) {
                                    sectionbufferbuilderpack.clearAll();
                                } else {
                                    sectionbufferbuilderpack.discardAll();
                                }

                                this.bufferPool.release(sectionbufferbuilderpack);
                                this.runTask();
                            });
                        }
                    });
            }
        }
    }

    public String getStats() {
        return String.format(Locale.ROOT, "pC: %03d, pU: %02d, aB: %02d", this.toBatchCount, this.toUpload.size(), this.bufferPool.getFreeBufferCount());
    }

    public int getToBatchCount() {
        return this.toBatchCount;
    }

    public int getToUpload() {
        return this.toUpload.size();
    }

    public int getFreeBufferCount() {
        return this.bufferPool.getFreeBufferCount();
    }

    public void setCamera(Vec3 p_296331_) {
        this.camera = p_296331_;
    }

    public Vec3 getCameraPosition() {
        return this.camera;
    }

    public void uploadAllPendingUploads() {
        Runnable runnable;
        while ((runnable = this.toUpload.poll()) != null) {
            runnable.run();
        }
    }

    public void rebuildSectionSync(SectionRenderDispatcher.RenderSection p_296309_, RenderRegionCache p_294139_) {
        p_296309_.compileSync(p_294139_);
    }

    public void blockUntilClear() {
        this.clearBatchQueue();
    }

    public void schedule(SectionRenderDispatcher.RenderSection.CompileTask p_295825_) {
        if (!this.closed) {
            this.consecutiveExecutor.schedule(() -> {
                if (!this.closed) {
                    this.compileQueue.add(p_295825_);
                    this.toBatchCount = this.compileQueue.size();
                    this.runTask();
                }
            });
        }
    }

    public CompletableFuture<Void> uploadSectionLayer(MeshData p_350732_, VertexBuffer p_294163_) {
        return this.closed ? CompletableFuture.completedFuture(null) : CompletableFuture.runAsync(() -> {
            if (p_294163_.isInvalid()) {
                p_350732_.close();
            } else {
                try (Zone zone = Profiler.get().zone("Upload Section Layer")) {
                    p_294163_.bind();
                    p_294163_.upload(p_350732_);
                    VertexBuffer.unbind();
                }
            }
        }, this.toUpload::add);
    }

    public CompletableFuture<Void> uploadSectionIndexBuffer(ByteBufferBuilder.Result p_350933_, VertexBuffer p_350643_) {
        return this.closed ? CompletableFuture.completedFuture(null) : CompletableFuture.runAsync(() -> {
            if (p_350643_.isInvalid()) {
                p_350933_.close();
            } else {
                try (Zone zone = Profiler.get().zone("Upload Section Indices")) {
                    p_350643_.bind();
                    p_350643_.uploadIndexBuffer(p_350933_);
                    VertexBuffer.unbind();
                }
            }
        }, this.toUpload::add);
    }

    private void clearBatchQueue() {
        this.compileQueue.clear();
        this.toBatchCount = 0;
    }

    public boolean isQueueEmpty() {
        return this.toBatchCount == 0 && this.toUpload.isEmpty();
    }

    public void dispose() {
        this.closed = true;
        this.clearBatchQueue();
        this.uploadAllPendingUploads();
    }

    @OnlyIn(Dist.CLIENT)
    public static class CompiledSection {
        public static final SectionRenderDispatcher.CompiledSection UNCOMPILED = new SectionRenderDispatcher.CompiledSection() {
            @Override
            public boolean facesCanSeeEachother(Direction p_296238_, Direction p_294727_) {
                return false;
            }
        };
        public static final SectionRenderDispatcher.CompiledSection EMPTY = new SectionRenderDispatcher.CompiledSection() {
            @Override
            public boolean facesCanSeeEachother(Direction p_351039_, Direction p_350415_) {
                return true;
            }
        };
        final Set<RenderType> hasBlocks = new ObjectArraySet<>(RenderType.chunkBufferLayers().size());
        final List<BlockEntity> renderableBlockEntities = Lists.newArrayList();
        VisibilitySet visibilitySet = new VisibilitySet();
        @Nullable
        MeshData.SortState transparencyState;

        public boolean hasRenderableLayers() {
            return !this.hasBlocks.isEmpty();
        }

        public boolean isEmpty(RenderType p_296192_) {
            return !this.hasBlocks.contains(p_296192_);
        }

        public List<BlockEntity> getRenderableBlockEntities() {
            return this.renderableBlockEntities;
        }

        public boolean facesCanSeeEachother(Direction p_295753_, Direction p_294424_) {
            return this.visibilitySet.visibilityBetween(p_295753_, p_294424_);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public class RenderSection implements net.neoforged.neoforge.client.IRenderableSection {
        public static final int SIZE = 16;
        public final int index;
        public final AtomicReference<SectionRenderDispatcher.CompiledSection> compiled = new AtomicReference<>(
            SectionRenderDispatcher.CompiledSection.UNCOMPILED
        );
        public final AtomicReference<SectionRenderDispatcher.TranslucencyPointOfView> pointOfView = new AtomicReference<>(null);
        @Nullable
        private SectionRenderDispatcher.RenderSection.RebuildTask lastRebuildTask;
        @Nullable
        private SectionRenderDispatcher.RenderSection.ResortTransparencyTask lastResortTransparencyTask;
        private final Set<BlockEntity> globalBlockEntities = Sets.newHashSet();
        private final Map<RenderType, VertexBuffer> buffers = RenderType.chunkBufferLayers()
            .stream()
            .collect(Collectors.toMap(p_295245_ -> (RenderType)p_295245_, p_373684_ -> new VertexBuffer(BufferUsage.STATIC_WRITE)));
        private AABB bb;
        private boolean dirty = true;
        long sectionNode = SectionPos.asLong(-1, -1, -1);
        final BlockPos.MutableBlockPos origin = new BlockPos.MutableBlockPos(-1, -1, -1);
        private boolean playerChanged;

        public RenderSection(int p_295197_, long p_366428_) {
            this.index = p_295197_;
            this.setSectionNode(p_366428_);
        }

        private boolean doesChunkExistAt(long p_366835_) {
            ChunkAccess chunkaccess = SectionRenderDispatcher.this.level.getChunk(SectionPos.x(p_366835_), SectionPos.z(p_366835_), ChunkStatus.FULL, false);
            return chunkaccess != null && SectionRenderDispatcher.this.level.getLightEngine().lightOnInColumn(SectionPos.getZeroNode(p_366835_));
        }

        public boolean hasAllNeighbors() {
            int i = 24;
            return !(this.getDistToPlayerSqr() > 576.0)
                ? true
                : this.doesChunkExistAt(SectionPos.offset(this.sectionNode, Direction.WEST))
                    && this.doesChunkExistAt(SectionPos.offset(this.sectionNode, Direction.NORTH))
                    && this.doesChunkExistAt(SectionPos.offset(this.sectionNode, Direction.EAST))
                    && this.doesChunkExistAt(SectionPos.offset(this.sectionNode, Direction.SOUTH))
                    && this.doesChunkExistAt(SectionPos.offset(this.sectionNode, -1, 0, -1))
                    && this.doesChunkExistAt(SectionPos.offset(this.sectionNode, -1, 0, 1))
                    && this.doesChunkExistAt(SectionPos.offset(this.sectionNode, 1, 0, -1))
                    && this.doesChunkExistAt(SectionPos.offset(this.sectionNode, 1, 0, 1));
        }

        public AABB getBoundingBox() {
            return this.bb;
        }

        public VertexBuffer getBuffer(RenderType p_294497_) {
            return this.buffers.get(p_294497_);
        }

        public void setSectionNode(long p_366482_) {
            this.reset();
            this.sectionNode = p_366482_;
            int i = SectionPos.sectionToBlockCoord(SectionPos.x(p_366482_));
            int j = SectionPos.sectionToBlockCoord(SectionPos.y(p_366482_));
            int k = SectionPos.sectionToBlockCoord(SectionPos.z(p_366482_));
            this.origin.set(i, j, k);
            this.bb = new AABB((double)i, (double)j, (double)k, (double)(i + 16), (double)(j + 16), (double)(k + 16));
        }

        protected double getDistToPlayerSqr() {
            Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
            double d0 = this.bb.minX + 8.0 - camera.getPosition().x;
            double d1 = this.bb.minY + 8.0 - camera.getPosition().y;
            double d2 = this.bb.minZ + 8.0 - camera.getPosition().z;
            return d0 * d0 + d1 * d1 + d2 * d2;
        }

        public SectionRenderDispatcher.CompiledSection getCompiled() {
            return this.compiled.get();
        }

        private void reset() {
            this.cancelTasks();
            this.compiled.set(SectionRenderDispatcher.CompiledSection.UNCOMPILED);
            this.pointOfView.set(null);
            this.dirty = true;
        }

        public void releaseBuffers() {
            this.reset();
            this.buffers.values().forEach(VertexBuffer::close);
        }

        public BlockPos getOrigin() {
            return this.origin;
        }

        public long getSectionNode() {
            return this.sectionNode;
        }

        public void setDirty(boolean p_295417_) {
            boolean flag = this.dirty;
            this.dirty = true;
            this.playerChanged = p_295417_ | (flag && this.playerChanged);
        }

        public void setNotDirty() {
            this.dirty = false;
            this.playerChanged = false;
        }

        public boolean isDirty() {
            return this.dirty;
        }

        public boolean isDirtyFromPlayer() {
            return this.dirty && this.playerChanged;
        }

        public long getNeighborSectionNode(Direction p_366736_) {
            return SectionPos.offset(this.sectionNode, p_366736_);
        }

        public void resortTransparency(SectionRenderDispatcher p_294363_) {
            this.lastResortTransparencyTask = new SectionRenderDispatcher.RenderSection.ResortTransparencyTask(this.getCompiled());
            p_294363_.schedule(this.lastResortTransparencyTask);
        }

        public boolean hasTranslucentGeometry() {
            return this.getCompiled().hasBlocks.contains(RenderType.translucent());
        }

        public boolean transparencyResortingScheduled() {
            return this.lastResortTransparencyTask != null && !this.lastResortTransparencyTask.isCompleted.get();
        }

        protected void cancelTasks() {
            if (this.lastRebuildTask != null) {
                this.lastRebuildTask.cancel();
                this.lastRebuildTask = null;
            }

            if (this.lastResortTransparencyTask != null) {
                this.lastResortTransparencyTask.cancel();
                this.lastResortTransparencyTask = null;
            }
        }

        public SectionRenderDispatcher.RenderSection.CompileTask createCompileTask(RenderRegionCache p_295324_) {
            this.cancelTasks();
            var additionalRenderers = net.neoforged.neoforge.client.ClientHooks.gatherAdditionalRenderers(this.origin, SectionRenderDispatcher.this.level);
            RenderChunkRegion renderchunkregion = p_295324_.createRegion(SectionRenderDispatcher.this.level, SectionPos.of(this.sectionNode), additionalRenderers.isEmpty());
            boolean flag = this.compiled.get() != SectionRenderDispatcher.CompiledSection.UNCOMPILED;
            this.lastRebuildTask = new SectionRenderDispatcher.RenderSection.RebuildTask(renderchunkregion, flag, additionalRenderers);
            return this.lastRebuildTask;
        }

        public void rebuildSectionAsync(SectionRenderDispatcher p_295901_, RenderRegionCache p_294660_) {
            SectionRenderDispatcher.RenderSection.CompileTask sectionrenderdispatcher$rendersection$compiletask = this.createCompileTask(p_294660_);
            p_295901_.schedule(sectionrenderdispatcher$rendersection$compiletask);
        }

        void updateGlobalBlockEntities(Collection<BlockEntity> p_296155_) {
            Set<BlockEntity> set = Sets.newHashSet(p_296155_);
            Set<BlockEntity> set1;
            synchronized (this.globalBlockEntities) {
                set1 = Sets.newHashSet(this.globalBlockEntities);
                set.removeAll(this.globalBlockEntities);
                set1.removeAll(p_296155_);
                this.globalBlockEntities.clear();
                this.globalBlockEntities.addAll(p_296155_);
            }

            SectionRenderDispatcher.this.renderer.updateGlobalBlockEntities(set1, set);
        }

        public void compileSync(RenderRegionCache p_296018_) {
            SectionRenderDispatcher.RenderSection.CompileTask sectionrenderdispatcher$rendersection$compiletask = this.createCompileTask(p_296018_);
            sectionrenderdispatcher$rendersection$compiletask.doTask(SectionRenderDispatcher.this.fixedBuffers);
        }

        void setCompiled(SectionRenderDispatcher.CompiledSection p_350692_) {
            this.compiled.set(p_350692_);
            SectionRenderDispatcher.this.renderer.addRecentlyCompiledSection(this);
        }

        VertexSorting createVertexSorting() {
            Vec3 vec3 = SectionRenderDispatcher.this.getCameraPosition();
            return VertexSorting.byDistance(
                (float)(vec3.x - (double)this.origin.getX()), (float)(vec3.y - (double)this.origin.getY()), (float)(vec3.z - (double)this.origin.getZ())
            );
        }

        // Neo: start

        @Override
        public boolean isEmpty() {
            return !getCompiled().hasRenderableLayers();
        }

        // Neo: end

        @OnlyIn(Dist.CLIENT)
        public abstract class CompileTask {
            protected final AtomicBoolean isCancelled = new AtomicBoolean(false);
            protected final AtomicBoolean isCompleted = new AtomicBoolean(false);
            protected final boolean isRecompile;

            public CompileTask(boolean p_295051_) {
                this.isRecompile = p_295051_;
            }

            public abstract CompletableFuture<SectionRenderDispatcher.SectionTaskResult> doTask(SectionBufferBuilderPack p_294622_);

            public abstract void cancel();

            protected abstract String name();

            public boolean isRecompile() {
                return this.isRecompile;
            }

            public BlockPos getOrigin() {
                return RenderSection.this.origin;
            }
        }

        @OnlyIn(Dist.CLIENT)
        class RebuildTask extends SectionRenderDispatcher.RenderSection.CompileTask {
            @Nullable
            protected volatile RenderChunkRegion region;
            private final List<net.neoforged.neoforge.client.event.AddSectionGeometryEvent.AdditionalSectionRenderer> additionalRenderers;

            @Deprecated
            public RebuildTask(@Nullable RenderChunkRegion p_294382_, boolean p_295207_) {
                this(p_294382_, p_295207_, List.of());
            }

            public RebuildTask(@Nullable RenderChunkRegion p_294382_, boolean p_295207_, List<net.neoforged.neoforge.client.event.AddSectionGeometryEvent.AdditionalSectionRenderer> additionalRenderers) {
                super(p_295207_);
                this.region = p_294382_;
                this.additionalRenderers = additionalRenderers;
            }

            @Override
            protected String name() {
                return "rend_chk_rebuild";
            }

            @Override
            public CompletableFuture<SectionRenderDispatcher.SectionTaskResult> doTask(SectionBufferBuilderPack p_296138_) {
                if (this.isCancelled.get()) {
                    return CompletableFuture.completedFuture(SectionRenderDispatcher.SectionTaskResult.CANCELLED);
                } else {
                    RenderChunkRegion renderchunkregion = this.region;
                    this.region = null;
                    if (renderchunkregion == null) {
                        // Neo: Fix MC-279596 (global block entities not being updated for empty sections)
                        RenderSection.this.updateGlobalBlockEntities(Set.of());
                        RenderSection.this.setCompiled(SectionRenderDispatcher.CompiledSection.EMPTY);
                        return CompletableFuture.completedFuture(SectionRenderDispatcher.SectionTaskResult.SUCCESSFUL);
                    } else {
                        SectionPos sectionpos = SectionPos.of(RenderSection.this.origin);
                        if (this.isCancelled.get()) {
                            return CompletableFuture.completedFuture(SectionRenderDispatcher.SectionTaskResult.CANCELLED);
                        } else {
                            SectionCompiler.Results sectioncompiler$results;
                            try (Zone zone = Profiler.get().zone("Compile Section")) {
                                sectioncompiler$results = SectionRenderDispatcher.this.sectionCompiler
                                    .compile(sectionpos, renderchunkregion, RenderSection.this.createVertexSorting(), p_296138_, this.additionalRenderers);
                            }

                            SectionRenderDispatcher.TranslucencyPointOfView sectionrenderdispatcher$translucencypointofview = SectionRenderDispatcher.TranslucencyPointOfView.of(
                                SectionRenderDispatcher.this.getCameraPosition(), RenderSection.this.sectionNode
                            );
                            RenderSection.this.updateGlobalBlockEntities(sectioncompiler$results.globalBlockEntities);
                            if (this.isCancelled.get()) {
                                sectioncompiler$results.release();
                                return CompletableFuture.completedFuture(SectionRenderDispatcher.SectionTaskResult.CANCELLED);
                            } else {
                                SectionRenderDispatcher.CompiledSection sectionrenderdispatcher$compiledsection = new SectionRenderDispatcher.CompiledSection();
                                sectionrenderdispatcher$compiledsection.visibilitySet = sectioncompiler$results.visibilitySet;
                                sectionrenderdispatcher$compiledsection.renderableBlockEntities.addAll(sectioncompiler$results.blockEntities);
                                sectionrenderdispatcher$compiledsection.transparencyState = sectioncompiler$results.transparencyState;
                                List<CompletableFuture<Void>> list = new ArrayList<>(sectioncompiler$results.renderedLayers.size());
                                sectioncompiler$results.renderedLayers.forEach((p_349884_, p_349885_) -> {
                                    list.add(SectionRenderDispatcher.this.uploadSectionLayer(p_349885_, RenderSection.this.getBuffer(p_349884_)));
                                    sectionrenderdispatcher$compiledsection.hasBlocks.add(p_349884_);
                                });
                                return Util.sequenceFailFast(list).handle((p_370314_, p_370315_) -> {
                                    if (p_370315_ != null && !(p_370315_ instanceof CancellationException) && !(p_370315_ instanceof InterruptedException)) {
                                        Minecraft.getInstance().delayCrash(CrashReport.forThrowable(p_370315_, "Rendering section"));
                                    }

                                    if (this.isCancelled.get()) {
                                        return SectionRenderDispatcher.SectionTaskResult.CANCELLED;
                                    } else {
                                        RenderSection.this.setCompiled(sectionrenderdispatcher$compiledsection);
                                        RenderSection.this.pointOfView.set(sectionrenderdispatcher$translucencypointofview);
                                        return SectionRenderDispatcher.SectionTaskResult.SUCCESSFUL;
                                    }
                                });
                            }
                        }
                    }
                }
            }

            @Override
            public void cancel() {
                this.region = null;
                if (this.isCancelled.compareAndSet(false, true)) {
                    RenderSection.this.setDirty(false);
                }
            }
        }

        @OnlyIn(Dist.CLIENT)
        class ResortTransparencyTask extends SectionRenderDispatcher.RenderSection.CompileTask {
            private final SectionRenderDispatcher.CompiledSection compiledSection;

            public ResortTransparencyTask(SectionRenderDispatcher.CompiledSection p_294601_) {
                super(true);
                this.compiledSection = p_294601_;
            }

            @Override
            protected String name() {
                return "rend_chk_sort";
            }

            @Override
            public CompletableFuture<SectionRenderDispatcher.SectionTaskResult> doTask(SectionBufferBuilderPack p_295160_) {
                if (this.isCancelled.get()) {
                    return CompletableFuture.completedFuture(SectionRenderDispatcher.SectionTaskResult.CANCELLED);
                } else {
                    MeshData.SortState meshdata$sortstate = this.compiledSection.transparencyState;
                    if (meshdata$sortstate != null && !this.compiledSection.isEmpty(RenderType.translucent())) {
                        VertexSorting vertexsorting = RenderSection.this.createVertexSorting();
                        SectionRenderDispatcher.TranslucencyPointOfView sectionrenderdispatcher$translucencypointofview = SectionRenderDispatcher.TranslucencyPointOfView.of(
                            SectionRenderDispatcher.this.getCameraPosition(), RenderSection.this.sectionNode
                        );
                        if (sectionrenderdispatcher$translucencypointofview.equals(RenderSection.this.pointOfView.get())
                            && !sectionrenderdispatcher$translucencypointofview.isAxisAligned()) {
                            return CompletableFuture.completedFuture(SectionRenderDispatcher.SectionTaskResult.CANCELLED);
                        } else {
                            ByteBufferBuilder.Result bytebufferbuilder$result = meshdata$sortstate.buildSortedIndexBuffer(
                                p_295160_.buffer(RenderType.translucent()), vertexsorting
                            );
                            if (bytebufferbuilder$result == null) {
                                return CompletableFuture.completedFuture(SectionRenderDispatcher.SectionTaskResult.CANCELLED);
                            } else if (this.isCancelled.get()) {
                                bytebufferbuilder$result.close();
                                return CompletableFuture.completedFuture(SectionRenderDispatcher.SectionTaskResult.CANCELLED);
                            } else {
                                CompletableFuture<SectionRenderDispatcher.SectionTaskResult> completablefuture = SectionRenderDispatcher.this.uploadSectionIndexBuffer(
                                        bytebufferbuilder$result, RenderSection.this.getBuffer(RenderType.translucent())
                                    )
                                    .thenApply(p_294714_ -> SectionRenderDispatcher.SectionTaskResult.CANCELLED);
                                return completablefuture.handle((p_370317_, p_370318_) -> {
                                    if (p_370318_ != null && !(p_370318_ instanceof CancellationException) && !(p_370318_ instanceof InterruptedException)) {
                                        Minecraft.getInstance().delayCrash(CrashReport.forThrowable(p_370318_, "Rendering section"));
                                    }

                                    if (this.isCancelled.get()) {
                                        return SectionRenderDispatcher.SectionTaskResult.CANCELLED;
                                    } else {
                                        RenderSection.this.pointOfView.set(sectionrenderdispatcher$translucencypointofview);
                                        return SectionRenderDispatcher.SectionTaskResult.SUCCESSFUL;
                                    }
                                });
                            }
                        }
                    } else {
                        return CompletableFuture.completedFuture(SectionRenderDispatcher.SectionTaskResult.CANCELLED);
                    }
                }
            }

            @Override
            public void cancel() {
                this.isCancelled.set(true);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    static enum SectionTaskResult {
        SUCCESSFUL,
        CANCELLED;
    }

    @OnlyIn(Dist.CLIENT)
    public static final class TranslucencyPointOfView {
        private int x;
        private int y;
        private int z;

        public static SectionRenderDispatcher.TranslucencyPointOfView of(Vec3 p_371341_, long p_371400_) {
            return new SectionRenderDispatcher.TranslucencyPointOfView().set(p_371341_, p_371400_);
        }

        public SectionRenderDispatcher.TranslucencyPointOfView set(Vec3 p_371567_, long p_371572_) {
            this.x = getCoordinate(p_371567_.x(), SectionPos.x(p_371572_));
            this.y = getCoordinate(p_371567_.y(), SectionPos.y(p_371572_));
            this.z = getCoordinate(p_371567_.z(), SectionPos.z(p_371572_));
            return this;
        }

        private static int getCoordinate(double p_371545_, int p_371464_) {
            int i = SectionPos.blockToSectionCoord(p_371545_) - p_371464_;
            return Mth.clamp(i, -1, 1);
        }

        public boolean isAxisAligned() {
            return this.x == 0 || this.y == 0 || this.z == 0;
        }

        @Override
        public boolean equals(Object p_371211_) {
            if (p_371211_ == this) {
                return true;
            } else {
                return !(p_371211_ instanceof SectionRenderDispatcher.TranslucencyPointOfView sectionrenderdispatcher$translucencypointofview)
                    ? false
                    : this.x == sectionrenderdispatcher$translucencypointofview.x
                        && this.y == sectionrenderdispatcher$translucencypointofview.y
                        && this.z == sectionrenderdispatcher$translucencypointofview.z;
            }
        }
    }
}
