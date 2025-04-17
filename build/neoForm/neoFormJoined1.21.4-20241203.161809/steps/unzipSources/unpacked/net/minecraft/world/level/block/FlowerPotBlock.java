package net.minecraft.world.level.block;

import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FlowerPotBlock extends Block {
    public static final MapCodec<FlowerPotBlock> CODEC = RecordCodecBuilder.mapCodec(
        p_368424_ -> p_368424_.group(BuiltInRegistries.BLOCK.byNameCodec().fieldOf("potted").forGetter(p_304928_ -> p_304928_.potted), propertiesCodec())
                .apply(p_368424_, FlowerPotBlock::new)
    );
    private static final Map<Block, Block> POTTED_BY_CONTENT = Maps.newHashMap();
    public static final float AABB_SIZE = 3.0F;
    protected static final VoxelShape SHAPE = Block.box(5.0, 0.0, 5.0, 11.0, 6.0, 11.0);
    /** Neo: Field accesses are redirected to {@link #getPotted()} with a coremod. */
    private final Block potted;

    @Override
    public MapCodec<FlowerPotBlock> codec() {
        return CODEC;
    }

    @Deprecated // Mods should use the constructor below
    public FlowerPotBlock(Block p_53528_, BlockBehaviour.Properties p_53529_) {
        this(Blocks.FLOWER_POT == null ? null : () -> (FlowerPotBlock) Blocks.FLOWER_POT, () -> p_53528_, p_53529_);
        if (Blocks.FLOWER_POT != null) {
            ((FlowerPotBlock)Blocks.FLOWER_POT).addPlant(net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(p_53528_), () -> this);
        }
    }

    /**
     * For mod use, eliminates the need to extend this class, and prevents modded
     * flower pots from altering vanilla behavior.
     *
     * @param emptyPot The empty pot for this pot, or null for self.
     */
    public FlowerPotBlock(@org.jetbrains.annotations.Nullable java.util.function.Supplier<FlowerPotBlock> emptyPot, java.util.function.Supplier<? extends Block> p_53528_, BlockBehaviour.Properties properties) {
        super(properties);
        this.potted = null; // Unused, redirected by coremod
        this.flowerDelegate = p_53528_;
        if (emptyPot == null) {
            this.fullPots = Maps.newHashMap();
            this.emptyPot = null;
        } else {
            this.fullPots = java.util.Collections.emptyMap();
            this.emptyPot = emptyPot;
        }
    }

    @Override
    protected VoxelShape getShape(BlockState p_53556_, BlockGetter p_53557_, BlockPos p_53558_, CollisionContext p_53559_) {
        return SHAPE;
    }

    @Override
    protected InteractionResult useItemOn(
        ItemStack p_316610_, BlockState p_316240_, Level p_316456_, BlockPos p_316502_, Player p_316491_, InteractionHand p_316444_, BlockHitResult p_316826_
    ) {
        BlockState blockstate = (p_316610_.getItem() instanceof BlockItem blockitem
                ? getEmptyPot().fullPots.getOrDefault(net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(blockitem.getBlock()), () -> Blocks.AIR).get()
                : Blocks.AIR)
            .defaultBlockState();
        if (blockstate.isAir()) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        } else if (!this.isEmpty()) {
            return InteractionResult.CONSUME;
        } else {
            p_316456_.setBlock(p_316502_, blockstate, 3);
            p_316456_.gameEvent(p_316491_, GameEvent.BLOCK_CHANGE, p_316502_);
            p_316491_.awardStat(Stats.POT_FLOWER);
            p_316610_.consume(1, p_316491_);
            return InteractionResult.SUCCESS;
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState p_316363_, Level p_316655_, BlockPos p_316654_, Player p_316338_, BlockHitResult p_316518_) {
        if (this.isEmpty()) {
            return InteractionResult.CONSUME;
        } else {
            ItemStack itemstack = new ItemStack(this.potted);
            if (!p_316338_.addItem(itemstack)) {
                p_316338_.drop(itemstack, false);
            }

            p_316655_.setBlock(p_316654_, getEmptyPot().defaultBlockState(), 3);
            p_316655_.gameEvent(p_316338_, GameEvent.BLOCK_CHANGE, p_316654_);
            return InteractionResult.SUCCESS;
        }
    }

    @Override
    protected ItemStack getCloneItemStack(LevelReader p_304505_, BlockPos p_53532_, BlockState p_53533_, boolean p_388306_) {
        return this.isEmpty() ? super.getCloneItemStack(p_304505_, p_53532_, p_53533_, p_388306_) : new ItemStack(this.potted);
    }

    private boolean isEmpty() {
        return this.potted == Blocks.AIR;
    }

    @Override
    protected BlockState updateShape(
        BlockState p_53547_,
        LevelReader p_374311_,
        ScheduledTickAccess p_374506_,
        BlockPos p_53551_,
        Direction p_53548_,
        BlockPos p_53552_,
        BlockState p_53549_,
        RandomSource p_374435_
    ) {
        return p_53548_ == Direction.DOWN && !p_53547_.canSurvive(p_374311_, p_53551_)
            ? Blocks.AIR.defaultBlockState()
            : super.updateShape(p_53547_, p_374311_, p_374506_, p_53551_, p_53548_, p_53552_, p_53549_, p_374435_);
    }

    public Block getPotted() {
        return flowerDelegate.get();
    }

    @Override
    protected boolean isPathfindable(BlockState p_53535_, PathComputationType p_53538_) {
        return false;
    }

    // Neo: Maps flower blocks to the filled flower pot equivalent
    private final Map<net.minecraft.resources.ResourceLocation, java.util.function.Supplier<? extends Block>> fullPots;

    @org.jetbrains.annotations.Nullable
    private final java.util.function.Supplier<FlowerPotBlock> emptyPot;

    private final java.util.function.Supplier<? extends Block> flowerDelegate;

    public FlowerPotBlock getEmptyPot() {
         return emptyPot == null ? this : emptyPot.get();
    }

    /**
     * Maps the given flower to the filled pot it is for.
     * Call this on the empty pot block. Attempting to call this on a filled pot will throw an exception.
     *
     * @param flower The ResourceLocation of the flower block. Not flower item
     * @param fullPot The filled flower pot to map the flower block to
     */
    public void addPlant(net.minecraft.resources.ResourceLocation flower, java.util.function.Supplier<? extends Block> fullPot) {
         if (getEmptyPot() != this) {
              throw new IllegalArgumentException("Cannot add plant to non-empty pot: " + this + " (Please call addPlant on the empty pot instead)");
         }
         fullPots.put(flower, fullPot);
    }

    /**
     * Returns all the filled pots that can be spawned from filling this pot. (If this pot is filled, returned map will be empty)
     */
    public Map<net.minecraft.resources.ResourceLocation, java.util.function.Supplier<? extends Block>> getFullPotsView() {
        return java.util.Collections.unmodifiableMap(fullPots);
    }

    @Override
    protected boolean isRandomlyTicking(BlockState p_383068_) {
        return p_383068_.is(Blocks.POTTED_OPEN_EYEBLOSSOM) || p_383068_.is(Blocks.POTTED_CLOSED_EYEBLOSSOM);
    }

    @Override
    protected void randomTick(BlockState p_382880_, ServerLevel p_383169_, BlockPos p_382955_, RandomSource p_383144_) {
        if (this.isRandomlyTicking(p_382880_) && p_383169_.dimensionType().natural()) {
            boolean flag = this.potted == Blocks.OPEN_EYEBLOSSOM;
            boolean flag1 = CreakingHeartBlock.isNaturalNight(p_383169_);
            if (flag != flag1) {
                p_383169_.setBlock(p_382955_, this.opposite(p_382880_), 3);
                EyeblossomBlock.Type eyeblossomblock$type = EyeblossomBlock.Type.fromBoolean(flag).transform();
                eyeblossomblock$type.spawnTransformParticle(p_383169_, p_382955_, p_383144_);
                p_383169_.playSound(null, p_382955_, eyeblossomblock$type.longSwitchSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
            }
        }

        super.randomTick(p_382880_, p_383169_, p_382955_, p_383144_);
    }

    public BlockState opposite(BlockState p_383006_) {
        if (p_383006_.is(Blocks.POTTED_OPEN_EYEBLOSSOM)) {
            return Blocks.POTTED_CLOSED_EYEBLOSSOM.defaultBlockState();
        } else {
            return p_383006_.is(Blocks.POTTED_CLOSED_EYEBLOSSOM) ? Blocks.POTTED_OPEN_EYEBLOSSOM.defaultBlockState() : p_383006_;
        }
    }
}
