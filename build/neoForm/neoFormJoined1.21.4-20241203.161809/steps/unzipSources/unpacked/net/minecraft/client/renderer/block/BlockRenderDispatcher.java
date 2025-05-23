package net.minecraft.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.function.Supplier;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SpecialBlockModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BlockRenderDispatcher implements ResourceManagerReloadListener {
    private final BlockModelShaper blockModelShaper;
    private final ModelBlockRenderer modelRenderer;
    private final Supplier<SpecialBlockModelRenderer> specialBlockModelRenderer;
    private final LiquidBlockRenderer liquidBlockRenderer;
    private final RandomSource random = RandomSource.create();
    private final BlockColors blockColors;

    public BlockRenderDispatcher(BlockModelShaper p_173399_, Supplier<SpecialBlockModelRenderer> p_386717_, BlockColors p_173401_) {
        this.blockModelShaper = p_173399_;
        this.specialBlockModelRenderer = p_386717_;
        this.blockColors = p_173401_;
        this.modelRenderer = new net.neoforged.neoforge.client.model.lighting.LightPipelineAwareModelBlockRenderer(this.blockColors);
        this.liquidBlockRenderer = new LiquidBlockRenderer();
    }

    public BlockModelShaper getBlockModelShaper() {
        return this.blockModelShaper;
    }

    @Deprecated //Forge: Model data parameter
    public void renderBreakingTexture(BlockState p_110919_, BlockPos p_110920_, BlockAndTintGetter p_110921_, PoseStack p_110922_, VertexConsumer p_110923_) {
         renderBreakingTexture(p_110919_, p_110920_, p_110921_, p_110922_, p_110923_, net.neoforged.neoforge.client.model.data.ModelData.EMPTY);
    }
    public void renderBreakingTexture(BlockState p_110919_, BlockPos p_110920_, BlockAndTintGetter p_110921_, PoseStack p_110922_, VertexConsumer p_110923_, net.neoforged.neoforge.client.model.data.ModelData modelData) {
        if (p_110919_.getRenderShape() == RenderShape.MODEL) {
            BakedModel bakedmodel = this.blockModelShaper.getBlockModel(p_110919_);
            long i = p_110919_.getSeed(p_110920_);
            modelData = bakedmodel.getModelData(p_110921_, p_110920_, p_110919_, modelData);
            this.modelRenderer
                .tesselateBlock(p_110921_, bakedmodel, p_110919_, p_110920_, p_110922_, p_110923_, true, this.random, i, OverlayTexture.NO_OVERLAY, modelData, null);
        }
    }

    public void renderBatched(
            BlockState p_234356_,
            BlockPos p_234357_,
            BlockAndTintGetter p_234358_,
            PoseStack p_234359_,
            VertexConsumer p_234360_,
            boolean p_234361_,
            RandomSource p_234362_
    ) {
        renderBatched(p_234356_, p_234357_, p_234358_, p_234359_, p_234360_, p_234361_, p_234362_, net.neoforged.neoforge.client.model.data.ModelData.EMPTY, null);
    }

    public void renderBatched(
        BlockState p_234356_,
        BlockPos p_234357_,
        BlockAndTintGetter p_234358_,
        PoseStack p_234359_,
        VertexConsumer p_234360_,
        boolean p_234361_,
        RandomSource p_234362_,
        net.neoforged.neoforge.client.model.data.ModelData modelData,
        net.minecraft.client.renderer.RenderType renderType
    ) {
        try {
            this.modelRenderer
                .tesselateBlock(
                    p_234358_,
                    this.getBlockModel(p_234356_),
                    p_234356_,
                    p_234357_,
                    p_234359_,
                    p_234360_,
                    p_234361_,
                    p_234362_,
                    p_234356_.getSeed(p_234357_),
                    OverlayTexture.NO_OVERLAY,
                    modelData,
                    renderType
                );
        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.forThrowable(throwable, "Tesselating block in world");
            CrashReportCategory crashreportcategory = crashreport.addCategory("Block being tesselated");
            CrashReportCategory.populateBlockDetails(crashreportcategory, p_234358_, p_234357_, p_234356_);
            throw new ReportedException(crashreport);
        }
    }

    public void renderLiquid(BlockPos p_234364_, BlockAndTintGetter p_234365_, VertexConsumer p_234366_, BlockState p_234367_, FluidState p_234368_) {
        try {
            if (net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions.of(p_234368_).renderFluid(p_234368_, p_234365_, p_234364_, p_234366_, p_234367_)) return;
            this.liquidBlockRenderer.tesselate(p_234365_, p_234364_, p_234366_, p_234367_, p_234368_);
        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.forThrowable(throwable, "Tesselating liquid in world");
            CrashReportCategory crashreportcategory = crashreport.addCategory("Block being tesselated");
            CrashReportCategory.populateBlockDetails(crashreportcategory, p_234365_, p_234364_, null);
            throw new ReportedException(crashreport);
        }
    }

    public ModelBlockRenderer getModelRenderer() {
        return this.modelRenderer;
    }

    public BakedModel getBlockModel(BlockState p_110911_) {
        return this.blockModelShaper.getBlockModel(p_110911_);
    }

    @Deprecated //Forge: Model data and render type parameter
    public void renderSingleBlock(BlockState p_110913_, PoseStack p_110914_, MultiBufferSource p_110915_, int p_110916_, int p_110917_) {
        renderSingleBlock(p_110913_, p_110914_, p_110915_, p_110916_, p_110917_, net.neoforged.neoforge.client.model.data.ModelData.EMPTY, null);
    }

    public void renderSingleBlock(BlockState p_110913_, PoseStack p_110914_, MultiBufferSource p_110915_, int p_110916_, int p_110917_, net.neoforged.neoforge.client.model.data.ModelData modelData, @org.jetbrains.annotations.Nullable net.minecraft.client.renderer.RenderType renderType) {
        RenderShape rendershape = p_110913_.getRenderShape();
        if (rendershape != RenderShape.INVISIBLE) {
            BakedModel bakedmodel = this.getBlockModel(p_110913_);
            int i = this.blockColors.getColor(p_110913_, null, null, 0);
            float f = (float) (i >> 16 & 0xFF) / 255.0F;
            float f1 = (float) (i >> 8 & 0xFF) / 255.0F;
            float f2 = (float) (i & 0xFF) / 255.0F;
            for (net.minecraft.client.renderer.RenderType rt : bakedmodel.getRenderTypes(p_110913_, RandomSource.create(42), modelData))
            this.modelRenderer
                .renderModel(
                    p_110914_.last(),
                    p_110915_.getBuffer(renderType != null ? renderType : net.neoforged.neoforge.client.RenderTypeHelper.getEntityRenderType(rt)),
                    p_110913_,
                    bakedmodel,
                    f,
                    f1,
                    f2,
                    p_110916_,
                    p_110917_,
                    modelData,
                    rt
                );
            this.specialBlockModelRenderer.get().renderByBlock(p_110913_.getBlock(), ItemDisplayContext.NONE, p_110914_, p_110915_, p_110916_, p_110917_);
        }
    }

    @Override
    public void onResourceManagerReload(ResourceManager p_110909_) {
        this.liquidBlockRenderer.setupSprites();
    }

    public LiquidBlockRenderer getLiquidBlockRenderer() {
        return this.liquidBlockRenderer;
    }
}
