package net.minecraft.world.level.material;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class EmptyFluid extends Fluid {
    @Override
    public Item getBucket() {
        return Items.AIR;
    }

    @Override
    public boolean canBeReplacedWith(FluidState p_75930_, BlockGetter p_75931_, BlockPos p_75932_, Fluid p_75933_, Direction p_75934_) {
        return true;
    }

    @Override
    public Vec3 getFlow(BlockGetter p_75918_, BlockPos p_75919_, FluidState p_75920_) {
        return Vec3.ZERO;
    }

    @Override
    public int getTickDelay(LevelReader p_75922_) {
        return 0;
    }

    @Override
    protected boolean isEmpty() {
        return true;
    }

    @Override
    protected float getExplosionResistance() {
        return 0.0F;
    }

    @Override
    public float getHeight(FluidState p_75926_, BlockGetter p_75927_, BlockPos p_75928_) {
        return 0.0F;
    }

    @Override
    public float getOwnHeight(FluidState p_75924_) {
        return 0.0F;
    }

    @Override
    protected BlockState createLegacyBlock(FluidState p_75937_) {
        return Blocks.AIR.defaultBlockState();
    }

    @Override
    public boolean isSource(FluidState p_75944_) {
        return false;
    }

    @Override
    public int getAmount(FluidState p_75946_) {
        return 0;
    }

    @Override
    public VoxelShape getShape(FluidState p_75939_, BlockGetter p_75940_, BlockPos p_75941_) {
        return Shapes.empty();
    }
}
