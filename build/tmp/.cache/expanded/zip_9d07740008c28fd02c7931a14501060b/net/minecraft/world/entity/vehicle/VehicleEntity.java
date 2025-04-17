package net.minecraft.world.entity.vehicle;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

public abstract class VehicleEntity extends Entity {
    protected static final EntityDataAccessor<Integer> DATA_ID_HURT = SynchedEntityData.defineId(VehicleEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> DATA_ID_HURTDIR = SynchedEntityData.defineId(VehicleEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Float> DATA_ID_DAMAGE = SynchedEntityData.defineId(VehicleEntity.class, EntityDataSerializers.FLOAT);

    public VehicleEntity(EntityType<?> p_306130_, Level p_306167_) {
        super(p_306130_, p_306167_);
    }

    @Override
    public boolean hurtClient(DamageSource p_376811_) {
        return true;
    }

    @Override
    public boolean hurtServer(ServerLevel p_376703_, DamageSource p_376603_, float p_376371_) {
        if (this.isRemoved()) {
            return true;
        } else if (this.isInvulnerableToBase(p_376603_)) {
            return false;
        } else {
            boolean flag1;
            label32: {
                this.setHurtDir(-this.getHurtDir());
                this.setHurtTime(10);
                this.markHurt();
                this.setDamage(this.getDamage() + p_376371_ * 10.0F);
                this.gameEvent(GameEvent.ENTITY_DAMAGE, p_376603_.getEntity());
                if (p_376603_.getEntity() instanceof Player player && player.getAbilities().instabuild) {
                    flag1 = true;
                    break label32;
                }

                flag1 = false;
            }

            boolean flag = flag1;
            if ((flag || !(this.getDamage() > 40.0F)) && !this.shouldSourceDestroy(p_376603_)) {
                if (flag) {
                    this.discard();
                }
            } else {
                this.destroy(p_376703_, p_376603_);
            }

            return true;
        }
    }

    boolean shouldSourceDestroy(DamageSource p_312875_) {
        return false;
    }

    @Override
    public boolean ignoreExplosion(Explosion p_368563_) {
        return p_368563_.getIndirectSourceEntity() instanceof Mob && !p_368563_.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING);
    }

    public void destroy(ServerLevel p_376749_, Item p_376292_) {
        this.kill(p_376749_);
        if (p_376749_.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            ItemStack itemstack = new ItemStack(p_376292_);
            itemstack.set(DataComponents.CUSTOM_NAME, this.getCustomName());
            this.spawnAtLocation(p_376749_, itemstack);
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder p_326046_) {
        p_326046_.define(DATA_ID_HURT, 0);
        p_326046_.define(DATA_ID_HURTDIR, 1);
        p_326046_.define(DATA_ID_DAMAGE, 0.0F);
    }

    public void setHurtTime(int p_306126_) {
        this.entityData.set(DATA_ID_HURT, p_306126_);
    }

    public void setHurtDir(int p_306138_) {
        this.entityData.set(DATA_ID_HURTDIR, p_306138_);
    }

    public void setDamage(float p_306297_) {
        this.entityData.set(DATA_ID_DAMAGE, p_306297_);
    }

    public float getDamage() {
        return this.entityData.get(DATA_ID_DAMAGE);
    }

    public int getHurtTime() {
        return this.entityData.get(DATA_ID_HURT);
    }

    public int getHurtDir() {
        return this.entityData.get(DATA_ID_HURTDIR);
    }

    protected void destroy(ServerLevel p_376193_, DamageSource p_376354_) {
        this.destroy(p_376193_, this.getDropItem());
    }

    @Override
    public int getDimensionChangingDelay() {
        return 10;
    }

    protected abstract Item getDropItem();
}
