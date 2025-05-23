package net.minecraft.world.entity.boss.enderdragon.phases;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.windcharge.WindCharge;

public abstract class AbstractDragonSittingPhase extends AbstractDragonPhaseInstance {
    public AbstractDragonSittingPhase(EnderDragon p_31196_) {
        super(p_31196_);
    }

    @Override
    public boolean isSitting() {
        return true;
    }

    @Override
    public float onHurt(DamageSource p_31199_, float p_31200_) {
        if (!(p_31199_.getDirectEntity() instanceof AbstractArrow) && !(p_31199_.getDirectEntity() instanceof WindCharge)) {
            return super.onHurt(p_31199_, p_31200_);
        } else {
            p_31199_.getDirectEntity().igniteForSeconds(1.0F);
            return 0.0F;
        }
    }
}
