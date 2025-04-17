package net.minecraft.world.entity.animal;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.pathfinder.PathType;

public abstract class AgeableWaterCreature extends AgeableMob {
    protected AgeableWaterCreature(EntityType<? extends AgeableWaterCreature> p_364107_, Level p_360956_) {
        super(p_364107_, p_360956_);
        this.setPathfindingMalus(PathType.WATER, 0.0F);
    }

    @Override
    public boolean checkSpawnObstruction(LevelReader p_364382_) {
        return p_364382_.isUnobstructed(this);
    }

    @Override
    public int getAmbientSoundInterval() {
        return 120;
    }

    @Override
    public int getBaseExperienceReward(ServerLevel p_376882_) {
        return 1 + this.random.nextInt(3);
    }

    protected void handleAirSupply(int p_362351_) {
        if (this.isAlive() && !this.isInWaterOrBubble()) {
            this.setAirSupply(p_362351_ - 1);
            if (this.getAirSupply() == -20) {
                this.setAirSupply(0);
                this.hurt(this.damageSources().drown(), 2.0F);
            }
        } else {
            this.setAirSupply(300);
        }
    }

    @Override
    public void baseTick() {
        int i = this.getAirSupply();
        super.baseTick();
        this.handleAirSupply(i);
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    public boolean canBeLeashed() {
        return false;
    }

    public static boolean checkSurfaceAgeableWaterCreatureSpawnRules(
        EntityType<? extends AgeableWaterCreature> p_361277_, LevelAccessor p_360404_, EntitySpawnReason p_361898_, BlockPos p_362596_, RandomSource p_363955_
    ) {
        int i = p_360404_.getSeaLevel();
        int j = i - 13;
        return p_362596_.getY() >= j
            && p_362596_.getY() <= i
            && p_360404_.getFluidState(p_362596_.below()).is(FluidTags.WATER)
            && p_360404_.getBlockState(p_362596_.above()).is(Blocks.WATER);
    }
}
