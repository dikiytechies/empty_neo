package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class BaseCoralPlantTypeBlock extends Block implements SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private static final VoxelShape AABB = Block.box(2.0, 0.0, 2.0, 14.0, 4.0, 14.0);

    public BaseCoralPlantTypeBlock(BlockBehaviour.Properties p_49161_) {
        super(p_49161_);
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, Boolean.valueOf(true)));
    }

    @Override
    protected abstract MapCodec<? extends BaseCoralPlantTypeBlock> codec();

    protected void tryScheduleDieTick(BlockState p_49165_, BlockGetter p_374382_, ScheduledTickAccess p_374116_, RandomSource p_374427_, BlockPos p_49167_) {
        if (!scanForWater(p_49165_, p_374382_, p_49167_)) {
            p_374116_.scheduleTick(p_49167_, this, 60 + p_374427_.nextInt(40));
        }
    }

    protected static boolean scanForWater(BlockState p_49187_, BlockGetter p_49188_, BlockPos p_49189_) {
        if (p_49187_.getValue(WATERLOGGED)) {
            return true;
        } else {
            for (Direction direction : Direction.values()) {
                if (p_49188_.getFluidState(p_49189_.relative(direction)).is(FluidTags.WATER)) {
                    return true;
                }
            }

            return false;
        }
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext p_49163_) {
        FluidState fluidstate = p_49163_.getLevel().getFluidState(p_49163_.getClickedPos());
        return this.defaultBlockState().setValue(WATERLOGGED, Boolean.valueOf(fluidstate.is(FluidTags.WATER) && fluidstate.getAmount() == 8));
    }

    @Override
    protected VoxelShape getShape(BlockState p_49182_, BlockGetter p_49183_, BlockPos p_49184_, CollisionContext p_49185_) {
        return AABB;
    }

    @Override
    protected BlockState updateShape(
        BlockState p_49173_,
        LevelReader p_374072_,
        ScheduledTickAccess p_374103_,
        BlockPos p_49177_,
        Direction p_49174_,
        BlockPos p_49178_,
        BlockState p_49175_,
        RandomSource p_374124_
    ) {
        if (p_49173_.getValue(WATERLOGGED)) {
            p_374103_.scheduleTick(p_49177_, Fluids.WATER, Fluids.WATER.getTickDelay(p_374072_));
        }

        return p_49174_ == Direction.DOWN && !this.canSurvive(p_49173_, p_374072_, p_49177_)
            ? Blocks.AIR.defaultBlockState()
            : super.updateShape(p_49173_, p_374072_, p_374103_, p_49177_, p_49174_, p_49178_, p_49175_, p_374124_);
    }

    @Override
    protected boolean canSurvive(BlockState p_49169_, LevelReader p_49170_, BlockPos p_49171_) {
        BlockPos blockpos = p_49171_.below();
        return p_49170_.getBlockState(blockpos).isFaceSturdy(p_49170_, blockpos, Direction.UP);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_49180_) {
        p_49180_.add(WATERLOGGED);
    }

    @Override
    protected FluidState getFluidState(BlockState p_49191_) {
        return p_49191_.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(p_49191_);
    }
}
