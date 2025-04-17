package net.minecraft.world.entity.projectile;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.OminousItemSpawner;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class AbstractArrow extends Projectile {
    private static final double ARROW_BASE_DAMAGE = 2.0;
    private static final int SHAKE_TIME = 7;
    private static final float WATER_INERTIA = 0.6F;
    private static final float INERTIA = 0.99F;
    private static final EntityDataAccessor<Byte> ID_FLAGS = SynchedEntityData.defineId(AbstractArrow.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Byte> PIERCE_LEVEL = SynchedEntityData.defineId(AbstractArrow.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Boolean> IN_GROUND = SynchedEntityData.defineId(AbstractArrow.class, EntityDataSerializers.BOOLEAN);
    private static final int FLAG_CRIT = 1;
    private static final int FLAG_NOPHYSICS = 2;
    @Nullable
    private BlockState lastState;
    protected int inGroundTime;
    public AbstractArrow.Pickup pickup = AbstractArrow.Pickup.DISALLOWED;
    public int shakeTime;
    private int life;
    private double baseDamage = 2.0;
    private SoundEvent soundEvent = this.getDefaultHitGroundSoundEvent();
    @Nullable
    private IntOpenHashSet piercingIgnoreEntityIds;
    @Nullable
    private List<Entity> piercedAndKilledEntities;
    private ItemStack pickupItemStack = this.getDefaultPickupItem();
    @Nullable
    private ItemStack firedFromWeapon = null;

    protected AbstractArrow(EntityType<? extends AbstractArrow> p_331098_, Level p_331626_) {
        super(p_331098_, p_331626_);
    }

    protected AbstractArrow(
        EntityType<? extends AbstractArrow> p_36717_,
        double p_346045_,
        double p_344855_,
        double p_345999_,
        Level p_36719_,
        ItemStack p_309031_,
        @Nullable ItemStack p_345487_
    ) {
        this(p_36717_, p_36719_);
        this.pickupItemStack = p_309031_.copy();
        this.setCustomName(p_309031_.get(DataComponents.CUSTOM_NAME));
        Unit unit = p_309031_.remove(DataComponents.INTANGIBLE_PROJECTILE);
        if (unit != null) {
            this.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
        }

        this.setPos(p_346045_, p_344855_, p_345999_);
        if (p_345487_ != null && p_36719_ instanceof ServerLevel serverlevel) {
            if (p_345487_.isEmpty()) {
                throw new IllegalArgumentException("Invalid weapon firing an arrow");
            }

            this.firedFromWeapon = p_345487_.copy();
            int i = EnchantmentHelper.getPiercingCount(serverlevel, p_345487_, this.pickupItemStack);
            if (i > 0) {
                this.setPierceLevel((byte)i);
            }
        }
    }

    protected AbstractArrow(
        EntityType<? extends AbstractArrow> p_36721_, LivingEntity p_345310_, Level p_36722_, ItemStack p_309145_, @Nullable ItemStack p_345000_
    ) {
        this(p_36721_, p_345310_.getX(), p_345310_.getEyeY() - 0.1F, p_345310_.getZ(), p_36722_, p_309145_, p_345000_);
        this.setOwner(p_345310_);
    }

    public void setSoundEvent(SoundEvent p_36741_) {
        this.soundEvent = p_36741_;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double p_36726_) {
        double d0 = this.getBoundingBox().getSize() * 10.0;
        if (Double.isNaN(d0)) {
            d0 = 1.0;
        }

        d0 *= 64.0 * getViewScale();
        return p_36726_ < d0 * d0;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder p_325945_) {
        p_325945_.define(ID_FLAGS, (byte)0);
        p_325945_.define(PIERCE_LEVEL, (byte)0);
        p_325945_.define(IN_GROUND, false);
    }

    @Override
    public void shoot(double p_36775_, double p_36776_, double p_36777_, float p_36778_, float p_36779_) {
        super.shoot(p_36775_, p_36776_, p_36777_, p_36778_, p_36779_);
        this.life = 0;
    }

    @Override
    public void lerpTo(double p_36728_, double p_36729_, double p_36730_, float p_36731_, float p_36732_, int p_36733_) {
        this.setPos(p_36728_, p_36729_, p_36730_);
        this.setRot(p_36731_, p_36732_);
    }

    @Override
    public void lerpMotion(double p_36786_, double p_36787_, double p_36788_) {
        super.lerpMotion(p_36786_, p_36787_, p_36788_);
        this.life = 0;
        if (this.isInGround() && Mth.lengthSquared(p_36786_, p_36787_, p_36788_) > 0.0) {
            this.setInGround(false);
        }
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> p_381707_) {
        super.onSyncedDataUpdated(p_381707_);
        if (!this.firstTick && this.shakeTime <= 0 && p_381707_.equals(IN_GROUND) && this.isInGround()) {
            this.shakeTime = 7;
        }
    }

    @Override
    public void tick() {
        boolean flag = !this.isNoPhysics();
        Vec3 vec3 = this.getDeltaMovement();
        BlockPos blockpos = this.blockPosition();
        BlockState blockstate = this.level().getBlockState(blockpos);
        if (!blockstate.isAir() && flag) {
            VoxelShape voxelshape = blockstate.getCollisionShape(this.level(), blockpos);
            if (!voxelshape.isEmpty()) {
                Vec3 vec31 = this.position();

                for (AABB aabb : voxelshape.toAabbs()) {
                    if (aabb.move(blockpos).contains(vec31)) {
                        this.setInGround(true);
                        break;
                    }
                }
            }
        }

        if (this.shakeTime > 0) {
            this.shakeTime--;
        }

        if (this.isInWaterOrRain() || blockstate.is(Blocks.POWDER_SNOW) || this.isInFluidType((fluidType, height) -> this.canFluidExtinguish(fluidType))) {
            this.clearFire();
        }

        if (this.isInGround() && flag) {
            if (!this.level().isClientSide()) {
                if (this.lastState != blockstate && this.shouldFall()) {
                    this.startFalling();
                } else {
                    this.tickDespawn();
                }
            }

            this.inGroundTime++;
            if (this.isAlive()) {
                this.applyEffectsFromBlocks();
            }
        } else {
            this.inGroundTime = 0;
            Vec3 vec32 = this.position();
            if (this.isInWater()) {
                this.applyInertia(this.getWaterInertia());
                this.addBubbleParticles(vec32);
            }

            if (this.isCritArrow()) {
                for (int i = 0; i < 4; i++) {
                    this.level()
                        .addParticle(
                            ParticleTypes.CRIT,
                            vec32.x + vec3.x * (double)i / 4.0,
                            vec32.y + vec3.y * (double)i / 4.0,
                            vec32.z + vec3.z * (double)i / 4.0,
                            -vec3.x,
                            -vec3.y + 0.2,
                            -vec3.z
                        );
                }
            }

            float f;
            if (!flag) {
                f = (float)(Mth.atan2(-vec3.x, -vec3.z) * 180.0F / (float)Math.PI);
            } else {
                f = (float)(Mth.atan2(vec3.x, vec3.z) * 180.0F / (float)Math.PI);
            }

            float f1 = (float)(Mth.atan2(vec3.y, vec3.horizontalDistance()) * 180.0F / (float)Math.PI);
            this.setXRot(lerpRotation(this.getXRot(), f1));
            this.setYRot(lerpRotation(this.getYRot(), f));
            if (flag) {
                BlockHitResult blockhitresult = this.level()
                    .clipIncludingBorder(new ClipContext(vec32, vec32.add(vec3), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
                this.stepMoveAndHit(blockhitresult);
            } else {
                this.setPos(vec32.add(vec3));
                this.applyEffectsFromBlocks();
            }

            if (!this.isInWater()) {
                this.applyInertia(0.99F);
            }

            if (flag && !this.isInGround()) {
                this.applyGravity();
            }

            super.tick();
        }
    }

    private void stepMoveAndHit(BlockHitResult p_371761_) {
        while (this.isAlive()) {
            Vec3 vec3 = this.position();
            EntityHitResult entityhitresult = this.findHitEntity(vec3, p_371761_.getLocation());
            Vec3 vec31 = Objects.requireNonNullElse(entityhitresult, p_371761_).getLocation();
            this.setPos(vec31);
            this.applyEffectsFromBlocks(vec3, vec31);
            if (this.portalProcess != null && this.portalProcess.isInsidePortalThisTick()) {
                this.handlePortal();
            }

            if (entityhitresult == null) {
                if (this.isAlive() && p_371761_.getType() != HitResult.Type.MISS) {
                    if (net.neoforged.neoforge.event.EventHooks.onProjectileImpact(this, p_371761_))
                        break;
                    this.hitTargetOrDeflectSelf(p_371761_);
                    this.hasImpulse = true;
                }
                break;
            } else if (this.isAlive() && !this.noPhysics && entityhitresult.getType() != HitResult.Type.MISS) {
                if (net.neoforged.neoforge.event.EventHooks.onProjectileImpact(this, entityhitresult))
                    break;
                ProjectileDeflection projectiledeflection = this.hitTargetOrDeflectSelf(entityhitresult);
                this.hasImpulse = true;
                if (this.getPierceLevel() > 0 && projectiledeflection == ProjectileDeflection.NONE) {
                    continue;
                }
                break;
            }
        }
    }

    private void applyInertia(float p_383121_) {
        Vec3 vec3 = this.getDeltaMovement();
        this.setDeltaMovement(vec3.scale((double)p_383121_));
    }

    private void addBubbleParticles(Vec3 p_380279_) {
        Vec3 vec3 = this.getDeltaMovement();

        for (int i = 0; i < 4; i++) {
            float f = 0.25F;
            this.level()
                .addParticle(
                    ParticleTypes.BUBBLE, p_380279_.x - vec3.x * 0.25, p_380279_.y - vec3.y * 0.25, p_380279_.z - vec3.z * 0.25, vec3.x, vec3.y, vec3.z
                );
        }
    }

    @Override
    protected double getDefaultGravity() {
        return 0.05;
    }

    private boolean shouldFall() {
        return this.isInGround() && this.level().noCollision(new AABB(this.position(), this.position()).inflate(0.06));
    }

    private void startFalling() {
        this.setInGround(false);
        Vec3 vec3 = this.getDeltaMovement();
        this.setDeltaMovement(
            vec3.multiply((double)(this.random.nextFloat() * 0.2F), (double)(this.random.nextFloat() * 0.2F), (double)(this.random.nextFloat() * 0.2F))
        );
        this.life = 0;
    }

    protected boolean isInGround() {
        return this.entityData.get(IN_GROUND);
    }

    protected void setInGround(boolean p_381705_) {
        this.entityData.set(IN_GROUND, p_381705_);
    }

    @Override
    public void move(MoverType p_36749_, Vec3 p_36750_) {
        super.move(p_36749_, p_36750_);
        if (p_36749_ != MoverType.SELF && this.shouldFall()) {
            this.startFalling();
        }
    }

    protected void tickDespawn() {
        this.life++;
        if (this.life >= 1200) {
            this.discard();
        }
    }

    private void resetPiercedEntities() {
        if (this.piercedAndKilledEntities != null) {
            this.piercedAndKilledEntities.clear();
        }

        if (this.piercingIgnoreEntityIds != null) {
            this.piercingIgnoreEntityIds.clear();
        }
    }

    @Override
    protected void onItemBreak(Item p_365372_) {
        this.firedFromWeapon = null;
    }

    @Override
    public void onInsideBubbleColumn(boolean p_382819_) {
        if (!this.isInGround()) {
            super.onInsideBubbleColumn(p_382819_);
        }
    }

    @Override
    public void push(double p_383096_, double p_383174_, double p_383161_) {
        if (!this.isInGround()) {
            super.push(p_383096_, p_383174_, p_383161_);
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult p_36757_) {
        super.onHitEntity(p_36757_);
        Entity entity = p_36757_.getEntity();
        float f = (float)this.getDeltaMovement().length();
        double d0 = this.baseDamage;
        Entity entity1 = this.getOwner();
        DamageSource damagesource = this.damageSources().arrow(this, (Entity)(entity1 != null ? entity1 : this));
        if (this.getWeaponItem() != null && this.level() instanceof ServerLevel serverlevel) {
            d0 = (double)EnchantmentHelper.modifyDamage(serverlevel, this.getWeaponItem(), entity, damagesource, (float)d0);
        }

        int j = Mth.ceil(Mth.clamp((double)f * d0, 0.0, 2.147483647E9));
        if (this.getPierceLevel() > 0) {
            if (this.piercingIgnoreEntityIds == null) {
                this.piercingIgnoreEntityIds = new IntOpenHashSet(5);
            }

            if (this.piercedAndKilledEntities == null) {
                this.piercedAndKilledEntities = Lists.newArrayListWithCapacity(5);
            }

            if (this.piercingIgnoreEntityIds.size() >= this.getPierceLevel() + 1) {
                this.discard();
                return;
            }

            this.piercingIgnoreEntityIds.add(entity.getId());
        }

        if (this.isCritArrow()) {
            long k = (long)this.random.nextInt(j / 2 + 2);
            j = (int)Math.min(k + (long)j, 2147483647L);
        }

        if (entity1 instanceof LivingEntity livingentity1) {
            livingentity1.setLastHurtMob(entity);
        }

        boolean flag = entity.getType() == EntityType.ENDERMAN;
        int i = entity.getRemainingFireTicks();
        if (this.isOnFire() && !flag) {
            entity.igniteForSeconds(5.0F);
        }

        if (entity.hurtOrSimulate(damagesource, (float)j)) {
            if (flag) {
                return;
            }

            if (entity instanceof LivingEntity livingentity) {
                if (!this.level().isClientSide && this.getPierceLevel() <= 0) {
                    livingentity.setArrowCount(livingentity.getArrowCount() + 1);
                }

                this.doKnockback(livingentity, damagesource);
                if (this.level() instanceof ServerLevel serverlevel1) {
                    EnchantmentHelper.doPostAttackEffectsWithItemSource(serverlevel1, livingentity, damagesource, this.getWeaponItem());
                }

                this.doPostHurtEffects(livingentity);
                if (livingentity != entity1 && livingentity instanceof Player && entity1 instanceof ServerPlayer && !this.isSilent()) {
                    ((ServerPlayer)entity1).connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.ARROW_HIT_PLAYER, 0.0F));
                }

                if (!entity.isAlive() && this.piercedAndKilledEntities != null) {
                    this.piercedAndKilledEntities.add(livingentity);
                }

                if (!this.level().isClientSide && entity1 instanceof ServerPlayer serverplayer) {
                    if (this.piercedAndKilledEntities != null) {
                        CriteriaTriggers.KILLED_BY_ARROW.trigger(serverplayer, this.piercedAndKilledEntities, this.firedFromWeapon);
                    } else if (!entity.isAlive()) {
                        CriteriaTriggers.KILLED_BY_ARROW.trigger(serverplayer, List.of(entity), this.firedFromWeapon);
                    }
                }
            }

            this.playSound(this.soundEvent, 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
            if (this.getPierceLevel() <= 0) {
                this.discard();
            }
        } else {
            entity.setRemainingFireTicks(i);
            this.deflect(ProjectileDeflection.REVERSE, entity, this.getOwner(), false);
            this.setDeltaMovement(this.getDeltaMovement().scale(0.2));
            if (this.level() instanceof ServerLevel serverlevel2 && this.getDeltaMovement().lengthSqr() < 1.0E-7) {
                if (this.pickup == AbstractArrow.Pickup.ALLOWED) {
                    this.spawnAtLocation(serverlevel2, this.getPickupItem(), 0.1F);
                }

                this.discard();
            }
        }
    }

    protected void doKnockback(LivingEntity p_346111_, DamageSource p_346412_) {
        double d0 = (double)(
            this.firedFromWeapon != null && this.level() instanceof ServerLevel serverlevel
                ? EnchantmentHelper.modifyKnockback(serverlevel, this.firedFromWeapon, p_346111_, p_346412_, 0.0F)
                : 0.0F
        );
        if (d0 > 0.0) {
            double d1 = Math.max(0.0, 1.0 - p_346111_.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
            Vec3 vec3 = this.getDeltaMovement().multiply(1.0, 0.0, 1.0).normalize().scale(d0 * 0.6 * d1);
            if (vec3.lengthSqr() > 0.0) {
                p_346111_.push(vec3.x, 0.1, vec3.z);
            }
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult p_36755_) {
        this.lastState = this.level().getBlockState(p_36755_.getBlockPos());
        super.onHitBlock(p_36755_);
        ItemStack itemstack = this.getWeaponItem();
        if (this.level() instanceof ServerLevel serverlevel && itemstack != null) {
            this.hitBlockEnchantmentEffects(serverlevel, p_36755_, itemstack);
        }

        Vec3 vec31 = this.getDeltaMovement();
        Vec3 vec32 = new Vec3(Math.signum(vec31.x), Math.signum(vec31.y), Math.signum(vec31.z));
        Vec3 vec3 = vec32.scale(0.05F);
        this.setPos(this.position().subtract(vec3));
        this.setDeltaMovement(Vec3.ZERO);
        this.playSound(this.getHitGroundSoundEvent(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
        this.setInGround(true);
        this.shakeTime = 7;
        this.setCritArrow(false);
        this.setPierceLevel((byte)0);
        this.setSoundEvent(SoundEvents.ARROW_HIT);
        this.resetPiercedEntities();
    }

    protected void hitBlockEnchantmentEffects(ServerLevel p_345462_, BlockHitResult p_345204_, ItemStack p_345083_) {
        Vec3 vec3 = p_345204_.getBlockPos().clampLocationWithin(p_345204_.getLocation());
        EnchantmentHelper.onHitBlock(
            p_345462_,
            p_345083_,
            this.getOwner() instanceof LivingEntity livingentity ? livingentity : null,
            this,
            null,
            vec3,
            p_345462_.getBlockState(p_345204_.getBlockPos()),
            p_348569_ -> this.firedFromWeapon = null
        );
    }

    @Override
    public ItemStack getWeaponItem() {
        return this.firedFromWeapon;
    }

    protected SoundEvent getDefaultHitGroundSoundEvent() {
        return SoundEvents.ARROW_HIT;
    }

    protected final SoundEvent getHitGroundSoundEvent() {
        return this.soundEvent;
    }

    protected void doPostHurtEffects(LivingEntity p_36744_) {
    }

    @Nullable
    protected EntityHitResult findHitEntity(Vec3 p_36758_, Vec3 p_36759_) {
        return ProjectileUtil.getEntityHitResult(
            this.level(), this, p_36758_, p_36759_, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0), this::canHitEntity
        );
    }

    @Override
    protected boolean canHitEntity(Entity p_36743_) {
        return p_36743_ instanceof Player && this.getOwner() instanceof Player player && !player.canHarmPlayer((Player)p_36743_)
            ? false
            : super.canHitEntity(p_36743_) && (this.piercingIgnoreEntityIds == null || !this.piercingIgnoreEntityIds.contains(p_36743_.getId()));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag p_36772_) {
        super.addAdditionalSaveData(p_36772_);
        p_36772_.putShort("life", (short)this.life);
        if (this.lastState != null) {
            p_36772_.put("inBlockState", NbtUtils.writeBlockState(this.lastState));
        }

        p_36772_.putByte("shake", (byte)this.shakeTime);
        p_36772_.putBoolean("inGround", this.isInGround());
        p_36772_.putByte("pickup", (byte)this.pickup.ordinal());
        p_36772_.putDouble("damage", this.baseDamage);
        p_36772_.putBoolean("crit", this.isCritArrow());
        p_36772_.putByte("PierceLevel", this.getPierceLevel());
        p_36772_.putString("SoundEvent", BuiltInRegistries.SOUND_EVENT.getKey(this.soundEvent).toString());
        p_36772_.put("item", this.pickupItemStack.save(this.registryAccess()));
        if (this.firedFromWeapon != null) {
            p_36772_.put("weapon", this.firedFromWeapon.save(this.registryAccess(), new CompoundTag()));
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag p_36761_) {
        super.readAdditionalSaveData(p_36761_);
        this.life = p_36761_.getShort("life");
        if (p_36761_.contains("inBlockState", 10)) {
            this.lastState = NbtUtils.readBlockState(this.level().holderLookup(Registries.BLOCK), p_36761_.getCompound("inBlockState"));
        }

        this.shakeTime = p_36761_.getByte("shake") & 255;
        this.setInGround(p_36761_.getBoolean("inGround"));
        if (p_36761_.contains("damage", 99)) {
            this.baseDamage = p_36761_.getDouble("damage");
        }

        this.pickup = AbstractArrow.Pickup.byOrdinal(p_36761_.getByte("pickup"));
        this.setCritArrow(p_36761_.getBoolean("crit"));
        this.setPierceLevel(p_36761_.getByte("PierceLevel"));
        if (p_36761_.contains("SoundEvent", 8)) {
            this.soundEvent = BuiltInRegistries.SOUND_EVENT
                .getOptional(ResourceLocation.parse(p_36761_.getString("SoundEvent")))
                .orElse(this.getDefaultHitGroundSoundEvent());
        }

        if (p_36761_.contains("item", 10)) {
            this.setPickupItemStack(ItemStack.parse(this.registryAccess(), p_36761_.getCompound("item")).orElse(this.getDefaultPickupItem()));
        } else {
            this.setPickupItemStack(this.getDefaultPickupItem());
        }

        if (p_36761_.contains("weapon", 10)) {
            this.firedFromWeapon = ItemStack.parse(this.registryAccess(), p_36761_.getCompound("weapon")).orElse(null);
        } else {
            this.firedFromWeapon = null;
        }
    }

    @Override
    public void setOwner(@Nullable Entity p_36770_) {
        super.setOwner(p_36770_);

        this.pickup = switch (p_36770_) {
            case Player player when this.pickup == AbstractArrow.Pickup.DISALLOWED -> AbstractArrow.Pickup.ALLOWED;
            case OminousItemSpawner ominousitemspawner -> AbstractArrow.Pickup.DISALLOWED;
            case null, default -> this.pickup;
        };
    }

    @Override
    public void playerTouch(Player p_36766_) {
        if (!this.level().isClientSide && (this.isInGround() || this.isNoPhysics()) && this.shakeTime <= 0) {
            if (this.tryPickup(p_36766_)) {
                p_36766_.take(this, 1);
                this.discard();
            }
        }
    }

    protected boolean tryPickup(Player p_150121_) {
        return switch (this.pickup) {
            case DISALLOWED -> false;
            case ALLOWED -> p_150121_.getInventory().add(this.getPickupItem());
            case CREATIVE_ONLY -> p_150121_.hasInfiniteMaterials();
        };
    }

    protected ItemStack getPickupItem() {
        return this.pickupItemStack.copy();
    }

    protected abstract ItemStack getDefaultPickupItem();

    @Override
    protected Entity.MovementEmission getMovementEmission() {
        return Entity.MovementEmission.NONE;
    }

    public ItemStack getPickupItemStackOrigin() {
        return this.pickupItemStack;
    }

    public void setBaseDamage(double p_36782_) {
        this.baseDamage = p_36782_;
    }

    public double getBaseDamage() {
        return this.baseDamage;
    }

    @Override
    public boolean isAttackable() {
        return this.getType().is(EntityTypeTags.REDIRECTABLE_PROJECTILE);
    }

    public void setCritArrow(boolean p_36763_) {
        this.setFlag(1, p_36763_);
    }

    private void setPierceLevel(byte p_36768_) {
        this.entityData.set(PIERCE_LEVEL, p_36768_);
    }

    private void setFlag(int p_36738_, boolean p_36739_) {
        byte b0 = this.entityData.get(ID_FLAGS);
        if (p_36739_) {
            this.entityData.set(ID_FLAGS, (byte)(b0 | p_36738_));
        } else {
            this.entityData.set(ID_FLAGS, (byte)(b0 & ~p_36738_));
        }
    }

    protected void setPickupItemStack(ItemStack p_331486_) {
        if (!p_331486_.isEmpty()) {
            this.pickupItemStack = p_331486_;
        } else {
            this.pickupItemStack = this.getDefaultPickupItem();
        }
    }

    public boolean isCritArrow() {
        byte b0 = this.entityData.get(ID_FLAGS);
        return (b0 & 1) != 0;
    }

    public byte getPierceLevel() {
        return this.entityData.get(PIERCE_LEVEL);
    }

    public void setBaseDamageFromMob(float p_345515_) {
        this.setBaseDamage((double)(p_345515_ * 2.0F) + this.random.triangle((double)this.level().getDifficulty().getId() * 0.11, 0.57425));
    }

    protected float getWaterInertia() {
        return 0.6F;
    }

    public void setNoPhysics(boolean p_36791_) {
        this.noPhysics = p_36791_;
        this.setFlag(2, p_36791_);
    }

    public boolean isNoPhysics() {
        return !this.level().isClientSide ? this.noPhysics : (this.entityData.get(ID_FLAGS) & 2) != 0;
    }

    @Override
    public boolean isPickable() {
        return super.isPickable() && !this.isInGround();
    }

    @Override
    public SlotAccess getSlot(int p_341328_) {
        return p_341328_ == 0 ? SlotAccess.of(this::getPickupItemStackOrigin, this::setPickupItemStack) : super.getSlot(p_341328_);
    }

    @Override
    protected boolean shouldBounceOnWorldBorder() {
        return true;
    }

    public static enum Pickup {
        DISALLOWED,
        ALLOWED,
        CREATIVE_ONLY;

        public static AbstractArrow.Pickup byOrdinal(int p_36809_) {
            if (p_36809_ < 0 || p_36809_ > values().length) {
                p_36809_ = 0;
            }

            return values()[p_36809_];
        }
    }
}
