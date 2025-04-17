package net.minecraft.world.phys.shapes;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;

public class MinecartCollisionContext extends EntityCollisionContext {
    @Nullable
    private BlockPos ingoreBelow;
    @Nullable
    private BlockPos slopeIgnore;

    protected MinecartCollisionContext(AbstractMinecart p_366667_, boolean p_366484_) {
        super(p_366667_, p_366484_);
        this.setupContext(p_366667_);
    }

    private void setupContext(AbstractMinecart p_366450_) {
        BlockPos blockpos = p_366450_.getCurrentBlockPosOrRailBelow();
        BlockState blockstate = p_366450_.level().getBlockState(blockpos);
        boolean flag = BaseRailBlock.isRail(blockstate);
        if (flag) {
            this.ingoreBelow = blockpos.below();
            RailShape railshape = ((BaseRailBlock)blockstate.getBlock()).getRailDirection(blockstate, p_366450_.level(), blockpos, p_366450_);
            if (railshape.isSlope()) {
                this.slopeIgnore = switch (railshape) {
                    case ASCENDING_EAST -> blockpos.east();
                    case ASCENDING_WEST -> blockpos.west();
                    case ASCENDING_NORTH -> blockpos.north();
                    case ASCENDING_SOUTH -> blockpos.south();
                    default -> null;
                };
            }
        }
    }

    @Override
    public VoxelShape getCollisionShape(BlockState p_366641_, CollisionGetter p_366650_, BlockPos p_366424_) {
        return !p_366424_.equals(this.ingoreBelow) && !p_366424_.equals(this.slopeIgnore)
            ? super.getCollisionShape(p_366641_, p_366650_, p_366424_)
            : Shapes.empty();
    }
}
