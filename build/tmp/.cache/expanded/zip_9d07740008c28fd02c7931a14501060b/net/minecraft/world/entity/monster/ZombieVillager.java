package net.minecraft.world.entity.monster;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ConversionParams;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.village.ReputationEventType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerDataHolder;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;

public class ZombieVillager extends Zombie implements VillagerDataHolder {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final EntityDataAccessor<Boolean> DATA_CONVERTING_ID = SynchedEntityData.defineId(ZombieVillager.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<VillagerData> DATA_VILLAGER_DATA = SynchedEntityData.defineId(
        ZombieVillager.class, EntityDataSerializers.VILLAGER_DATA
    );
    private static final int VILLAGER_CONVERSION_WAIT_MIN = 3600;
    private static final int VILLAGER_CONVERSION_WAIT_MAX = 6000;
    private static final int MAX_SPECIAL_BLOCKS_COUNT = 14;
    private static final int SPECIAL_BLOCK_RADIUS = 4;
    private int villagerConversionTime;
    @Nullable
    private UUID conversionStarter;
    @Nullable
    private Tag gossips;
    @Nullable
    private MerchantOffers tradeOffers;
    private int villagerXp;

    public ZombieVillager(EntityType<? extends ZombieVillager> p_34368_, Level p_34369_) {
        super(p_34368_, p_34369_);
        BuiltInRegistries.VILLAGER_PROFESSION
            .getRandom(this.random)
            .ifPresent(p_255550_ -> this.setVillagerData(this.getVillagerData().setProfession(p_255550_.value())));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder p_326075_) {
        super.defineSynchedData(p_326075_);
        p_326075_.define(DATA_CONVERTING_ID, false);
        p_326075_.define(DATA_VILLAGER_DATA, new VillagerData(VillagerType.PLAINS, VillagerProfession.NONE, 1));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag p_34397_) {
        super.addAdditionalSaveData(p_34397_);
        VillagerData.CODEC
            .encodeStart(NbtOps.INSTANCE, this.getVillagerData())
            .resultOrPartial(LOGGER::error)
            .ifPresent(p_204072_ -> p_34397_.put("VillagerData", p_204072_));
        if (this.tradeOffers != null) {
            p_34397_.put(
                "Offers", MerchantOffers.CODEC.encodeStart(this.registryAccess().createSerializationContext(NbtOps.INSTANCE), this.tradeOffers).getOrThrow()
            );
        }

        if (this.gossips != null) {
            p_34397_.put("Gossips", this.gossips);
        }

        p_34397_.putInt("ConversionTime", this.isConverting() ? this.villagerConversionTime : -1);
        if (this.conversionStarter != null) {
            p_34397_.putUUID("ConversionPlayer", this.conversionStarter);
        }

        p_34397_.putInt("Xp", this.villagerXp);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag p_34387_) {
        super.readAdditionalSaveData(p_34387_);
        if (p_34387_.contains("VillagerData", 10)) {
            DataResult<VillagerData> dataresult = VillagerData.CODEC.parse(new Dynamic<>(NbtOps.INSTANCE, p_34387_.get("VillagerData")));
            dataresult.resultOrPartial(LOGGER::error).ifPresent(this::setVillagerData);
        }

        if (p_34387_.contains("Offers")) {
            MerchantOffers.CODEC
                .parse(this.registryAccess().createSerializationContext(NbtOps.INSTANCE), p_34387_.get("Offers"))
                .resultOrPartial(Util.prefix("Failed to load offers: ", LOGGER::warn))
                .ifPresent(p_323336_ -> this.tradeOffers = p_323336_);
        }

        if (p_34387_.contains("Gossips", 9)) {
            this.gossips = p_34387_.getList("Gossips", 10);
        }

        if (p_34387_.contains("ConversionTime", 99) && p_34387_.getInt("ConversionTime") > -1) {
            this.startConverting(p_34387_.hasUUID("ConversionPlayer") ? p_34387_.getUUID("ConversionPlayer") : null, p_34387_.getInt("ConversionTime"));
        }

        if (p_34387_.contains("Xp", 3)) {
            this.villagerXp = p_34387_.getInt("Xp");
        }
    }

    @Override
    public void tick() {
        if (!this.level().isClientSide && this.isAlive() && this.isConverting()) {
            int i = this.getConversionProgress();
            this.villagerConversionTime -= i;
            if (this.villagerConversionTime <= 0 && net.neoforged.neoforge.event.EventHooks.canLivingConvert(this, EntityType.VILLAGER, (timer) -> this.villagerConversionTime = timer)) {
                this.finishConversion((ServerLevel)this.level());
            }
        }

        super.tick();
    }

    @Override
    public InteractionResult mobInteract(Player p_34394_, InteractionHand p_34395_) {
        ItemStack itemstack = p_34394_.getItemInHand(p_34395_);
        if (itemstack.is(Items.GOLDEN_APPLE)) {
            if (this.hasEffect(MobEffects.WEAKNESS)) {
                itemstack.consume(1, p_34394_);
                if (!this.level().isClientSide) {
                    this.startConverting(p_34394_.getUUID(), this.random.nextInt(2401) + 3600);
                }

                return InteractionResult.SUCCESS_SERVER;
            } else {
                return InteractionResult.CONSUME;
            }
        } else {
            return super.mobInteract(p_34394_, p_34395_);
        }
    }

    @Override
    protected boolean convertsInWater() {
        return false;
    }

    @Override
    public boolean removeWhenFarAway(double p_34414_) {
        return !this.isConverting() && this.villagerXp == 0;
    }

    public boolean isConverting() {
        return this.getEntityData().get(DATA_CONVERTING_ID);
    }

    private void startConverting(@Nullable UUID p_34384_, int p_34385_) {
        this.conversionStarter = p_34384_;
        this.villagerConversionTime = p_34385_;
        this.getEntityData().set(DATA_CONVERTING_ID, true);
        this.removeEffect(MobEffects.WEAKNESS);
        this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, p_34385_, Math.min(this.level().getDifficulty().getId() - 1, 0)));
        this.level().broadcastEntityEvent(this, (byte)16);
    }

    @Override
    public void handleEntityEvent(byte p_34372_) {
        if (p_34372_ == 16) {
            if (!this.isSilent()) {
                this.level()
                    .playLocalSound(
                        this.getX(),
                        this.getEyeY(),
                        this.getZ(),
                        SoundEvents.ZOMBIE_VILLAGER_CURE,
                        this.getSoundSource(),
                        1.0F + this.random.nextFloat(),
                        this.random.nextFloat() * 0.7F + 0.3F,
                        false
                    );
            }
        } else {
            super.handleEntityEvent(p_34372_);
        }
    }

    private void finishConversion(ServerLevel p_34399_) {
        this.convertTo(
            EntityType.VILLAGER,
            ConversionParams.single(this, false, false),
            p_375894_ -> {
                for (EquipmentSlot equipmentslot : this.dropPreservedEquipment(
                    p_34399_, p_351901_ -> !EnchantmentHelper.has(p_351901_, EnchantmentEffectComponents.PREVENT_ARMOR_CHANGE)
                )) {
                    SlotAccess slotaccess = p_375894_.getSlot(equipmentslot.getIndex() + 300);
                    slotaccess.set(this.getItemBySlot(equipmentslot));
                }

                p_375894_.setVillagerData(this.getVillagerData());
                if (this.gossips != null) {
                    p_375894_.setGossips(this.gossips);
                }

                if (this.tradeOffers != null) {
                    p_375894_.setOffers(this.tradeOffers.copy());
                }

                p_375894_.setVillagerXp(this.villagerXp);
                p_375894_.finalizeSpawn(p_34399_, p_34399_.getCurrentDifficultyAt(p_375894_.blockPosition()), EntitySpawnReason.CONVERSION, null);
                p_375894_.refreshBrain(p_34399_);
                if (this.conversionStarter != null) {
                    Player player = p_34399_.getPlayerByUUID(this.conversionStarter);
                    if (player instanceof ServerPlayer) {
                        CriteriaTriggers.CURED_ZOMBIE_VILLAGER.trigger((ServerPlayer)player, this, p_375894_);
                        p_34399_.onReputationEvent(ReputationEventType.ZOMBIE_VILLAGER_CURED, player, p_375894_);
                    }
                }

                p_375894_.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0));
                if (!this.isSilent()) {
                    p_34399_.levelEvent(null, 1027, this.blockPosition(), 0);
                }

                net.neoforged.neoforge.event.EventHooks.onLivingConvert(this, p_375894_);
            }
        );
    }

    @VisibleForTesting
    public void setVillagerConversionTime(int p_371735_) {
        this.villagerConversionTime = p_371735_;
    }

    private int getConversionProgress() {
        int i = 1;
        if (this.random.nextFloat() < 0.01F) {
            int j = 0;
            BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

            for (int k = (int)this.getX() - 4; k < (int)this.getX() + 4 && j < 14; k++) {
                for (int l = (int)this.getY() - 4; l < (int)this.getY() + 4 && j < 14; l++) {
                    for (int i1 = (int)this.getZ() - 4; i1 < (int)this.getZ() + 4 && j < 14; i1++) {
                        BlockState blockstate = this.level().getBlockState(blockpos$mutableblockpos.set(k, l, i1));
                        if (blockstate.is(Blocks.IRON_BARS) || blockstate.getBlock() instanceof BedBlock) {
                            if (this.random.nextFloat() < 0.3F) {
                                i++;
                            }

                            j++;
                        }
                    }
                }
            }
        }

        return i;
    }

    @Override
    public float getVoicePitch() {
        return this.isBaby()
            ? (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 2.0F
            : (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F;
    }

    @Override
    public SoundEvent getAmbientSound() {
        return SoundEvents.ZOMBIE_VILLAGER_AMBIENT;
    }

    @Override
    public SoundEvent getHurtSound(DamageSource p_34404_) {
        return SoundEvents.ZOMBIE_VILLAGER_HURT;
    }

    @Override
    public SoundEvent getDeathSound() {
        return SoundEvents.ZOMBIE_VILLAGER_DEATH;
    }

    @Override
    public SoundEvent getStepSound() {
        return SoundEvents.ZOMBIE_VILLAGER_STEP;
    }

    @Override
    protected ItemStack getSkull() {
        return ItemStack.EMPTY;
    }

    public void setTradeOffers(MerchantOffers p_324128_) {
        this.tradeOffers = p_324128_;
    }

    public void setGossips(Tag p_34392_) {
        this.gossips = p_34392_;
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(
        ServerLevelAccessor p_34378_, DifficultyInstance p_34379_, EntitySpawnReason p_362634_, @Nullable SpawnGroupData p_34381_
    ) {
        this.setVillagerData(this.getVillagerData().setType(VillagerType.byBiome(p_34378_.getBiome(this.blockPosition()))));
        return super.finalizeSpawn(p_34378_, p_34379_, p_362634_, p_34381_);
    }

    @Override
    public void setVillagerData(VillagerData p_34376_) {
        VillagerData villagerdata = this.getVillagerData();
        if (villagerdata.getProfession() != p_34376_.getProfession()) {
            this.tradeOffers = null;
        }

        this.entityData.set(DATA_VILLAGER_DATA, p_34376_);
    }

    @Override
    public VillagerData getVillagerData() {
        return this.entityData.get(DATA_VILLAGER_DATA);
    }

    public int getVillagerXp() {
        return this.villagerXp;
    }

    public void setVillagerXp(int p_34374_) {
        this.villagerXp = p_34374_;
    }
}
