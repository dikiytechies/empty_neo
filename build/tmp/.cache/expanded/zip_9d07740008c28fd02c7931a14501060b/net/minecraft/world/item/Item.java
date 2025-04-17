package net.minecraft.world.item;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.DependantName;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureElement;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.component.DamageResistant;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.component.UseCooldown;
import net.minecraft.world.item.component.UseRemainder;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantable;
import net.minecraft.world.item.enchantment.Repairable;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public class Item implements FeatureElement, ItemLike, net.neoforged.neoforge.common.extensions.IItemExtension {
    public static final Codec<Holder<Item>> CODEC = BuiltInRegistries.ITEM
        .holderByNameCodec()
        .validate(
            p_381630_ -> p_381630_.is(Items.AIR.builtInRegistryHolder())
                    ? DataResult.error(() -> "Item must not be minecraft:air")
                    : DataResult.success(p_381630_)
        );
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final Map<Block, Item> BY_BLOCK = net.neoforged.neoforge.registries.GameData.getBlockItemMap();
    public static final ResourceLocation BASE_ATTACK_DAMAGE_ID = ResourceLocation.withDefaultNamespace("base_attack_damage");
    public static final ResourceLocation BASE_ATTACK_SPEED_ID = ResourceLocation.withDefaultNamespace("base_attack_speed");
    public static final int DEFAULT_MAX_STACK_SIZE = 64;
    public static final int ABSOLUTE_MAX_STACK_SIZE = 99;
    public static final int MAX_BAR_WIDTH = 13;
    private final Holder.Reference<Item> builtInRegistryHolder = BuiltInRegistries.ITEM.createIntrusiveHolder(this);
    private DataComponentMap components;
    @Nullable
    private final Item craftingRemainingItem;
    protected final String descriptionId;
    private final FeatureFlagSet requiredFeatures;

    public static int getId(Item p_41394_) {
        return p_41394_ == null ? 0 : BuiltInRegistries.ITEM.getId(p_41394_);
    }

    public static Item byId(int p_41446_) {
        return BuiltInRegistries.ITEM.byId(p_41446_);
    }

    @Deprecated
    public static Item byBlock(Block p_41440_) {
        return BY_BLOCK.getOrDefault(p_41440_, Items.AIR);
    }

    public Item(Item.Properties p_41383_) {
        this.descriptionId = p_41383_.effectiveDescriptionId();
        this.components = p_41383_.buildAndValidateComponents(Component.translatable(this.descriptionId), p_41383_.effectiveModel());
        this.craftingRemainingItem = p_41383_.craftingRemainingItem;
        this.requiredFeatures = p_41383_.requiredFeatures;
        if (SharedConstants.IS_RUNNING_IN_IDE && false) {
            String s = this.getClass().getSimpleName();
            if (!s.endsWith("Item")) {
                LOGGER.error("Item classes should end with Item and {} doesn't.", s);
            }
        }
        this.canCombineRepair = p_41383_.canCombineRepair;
    }

    @Deprecated
    public Holder.Reference<Item> builtInRegistryHolder() {
        return this.builtInRegistryHolder;
    }

    public DataComponentMap components() {
        return this.components;
    }

    /** @deprecated Neo: do not use, use {@link net.neoforged.neoforge.event.ModifyDefaultComponentsEvent the event} instead */
    @org.jetbrains.annotations.ApiStatus.Internal @Deprecated
    public void modifyDefaultComponentsFrom(net.minecraft.core.component.DataComponentPatch patch) {
        if (!net.neoforged.neoforge.internal.RegistrationEvents.canModifyComponents()) throw new IllegalStateException("Default components cannot be modified now!");
        var builder = DataComponentMap.builder().addAll(components);
        patch.entrySet().forEach(entry -> builder.set((DataComponentType)entry.getKey(), entry.getValue().orElse(null)));
        components = Properties.validateComponents(builder.build());
    }

    public int getDefaultMaxStackSize() {
        return this.components.getOrDefault(DataComponents.MAX_STACK_SIZE, 1);
    }

    public void onUseTick(Level p_41428_, LivingEntity p_41429_, ItemStack p_41430_, int p_41431_) {
    }

    /** @deprecated Forge: {@link net.neoforged.neoforge.common.extensions.IItemExtension#onDestroyed(ItemEntity, DamageSource) Use damage source sensitive version} */
    @Deprecated
    public void onDestroyed(ItemEntity p_150887_) {
    }

    public void verifyComponentsAfterLoad(ItemStack p_331627_) {
    }

    public boolean canAttackBlock(BlockState p_41441_, Level p_41442_, BlockPos p_41443_, Player p_41444_) {
        return true;
    }

    @Override
    public Item asItem() {
        return this;
    }

    public InteractionResult useOn(UseOnContext p_41427_) {
        return InteractionResult.PASS;
    }

    public float getDestroySpeed(ItemStack p_41425_, BlockState p_41426_) {
        Tool tool = p_41425_.get(DataComponents.TOOL);
        return tool != null ? tool.getMiningSpeed(p_41426_) : 1.0F;
    }

    public InteractionResult use(Level p_41432_, Player p_41433_, InteractionHand p_41434_) {
        ItemStack itemstack = p_41433_.getItemInHand(p_41434_);
        Consumable consumable = itemstack.get(DataComponents.CONSUMABLE);
        if (consumable != null) {
            return consumable.startConsuming(p_41433_, itemstack, p_41434_);
        } else {
            Equippable equippable = itemstack.get(DataComponents.EQUIPPABLE);
            return (InteractionResult)(equippable != null && equippable.swappable()
                ? equippable.swapWithEquipmentSlot(itemstack, p_41433_)
                : InteractionResult.PASS);
        }
    }

    public ItemStack finishUsingItem(ItemStack p_41409_, Level p_41410_, LivingEntity p_41411_) {
        Consumable consumable = p_41409_.get(DataComponents.CONSUMABLE);
        return consumable != null ? consumable.onConsume(p_41410_, p_41411_, p_41409_) : p_41409_;
    }

    public boolean isBarVisible(ItemStack p_150899_) {
        return p_150899_.isDamaged();
    }

    public int getBarWidth(ItemStack p_150900_) {
        return Math.round(13.0F - (float)p_150900_.getDamageValue() * 13.0F / (float)this.getMaxDamage(p_150900_));
    }

    public int getBarColor(ItemStack p_150901_) {
        int i = p_150901_.getMaxDamage();
        float stackMaxDamage = this.getMaxDamage(p_150901_);
        float f = Math.max(0.0F, (stackMaxDamage - (float)p_150901_.getDamageValue()) / stackMaxDamage);
        return Mth.hsvToRgb(f / 3.0F, 1.0F, 1.0F);
    }

    public boolean overrideStackedOnOther(ItemStack p_150888_, Slot p_150889_, ClickAction p_150890_, Player p_150891_) {
        return false;
    }

    public boolean overrideOtherStackedOnMe(
        ItemStack p_150892_, ItemStack p_150893_, Slot p_150894_, ClickAction p_150895_, Player p_150896_, SlotAccess p_150897_
    ) {
        return false;
    }

    public float getAttackDamageBonus(Entity p_345249_, float p_336179_, DamageSource p_345403_) {
        return 0.0F;
    }

    @Nullable
    public DamageSource getDamageSource(LivingEntity p_373049_) {
        return null;
    }

    public boolean hurtEnemy(ItemStack p_41395_, LivingEntity p_41396_, LivingEntity p_41397_) {
        return false;
    }

    public void postHurtEnemy(ItemStack p_346136_, LivingEntity p_346250_, LivingEntity p_346014_) {
    }

    public boolean mineBlock(ItemStack p_41416_, Level p_41417_, BlockState p_41418_, BlockPos p_41419_, LivingEntity p_41420_) {
        Tool tool = p_41416_.get(DataComponents.TOOL);
        if (tool == null) {
            return false;
        } else {
            if (!p_41417_.isClientSide && p_41418_.getDestroySpeed(p_41417_, p_41419_) != 0.0F && tool.damagePerBlock() > 0) {
                p_41416_.hurtAndBreak(tool.damagePerBlock(), p_41420_, EquipmentSlot.MAINHAND);
            }

            return true;
        }
    }

    public boolean isCorrectToolForDrops(ItemStack p_336002_, BlockState p_41450_) {
        Tool tool = p_336002_.get(DataComponents.TOOL);
        return tool != null && tool.isCorrectForDrops(p_41450_);
    }

    public InteractionResult interactLivingEntity(ItemStack p_41398_, Player p_41399_, LivingEntity p_41400_, InteractionHand p_41401_) {
        return InteractionResult.PASS;
    }

    @Override
    public String toString() {
        return BuiltInRegistries.ITEM.wrapAsHolder(this).getRegisteredName();
    }

    @Deprecated // Use ItemStack sensitive version.
    public final ItemStack getCraftingRemainder() {
        return this.craftingRemainingItem == null ? ItemStack.EMPTY : new ItemStack(this.craftingRemainingItem);
    }

    public void inventoryTick(ItemStack p_41404_, Level p_41405_, Entity p_41406_, int p_41407_, boolean p_41408_) {
    }

    public void onCraftedBy(ItemStack p_41447_, Level p_41448_, Player p_41449_) {
        this.onCraftedPostProcess(p_41447_, p_41448_);
    }

    public void onCraftedPostProcess(ItemStack p_307483_, Level p_307537_) {
    }

    public ItemUseAnimation getUseAnimation(ItemStack p_41452_) {
        Consumable consumable = p_41452_.get(DataComponents.CONSUMABLE);
        return consumable != null ? consumable.animation() : ItemUseAnimation.NONE;
    }

    public int getUseDuration(ItemStack p_41454_, LivingEntity p_344979_) {
        Consumable consumable = p_41454_.get(DataComponents.CONSUMABLE);
        return consumable != null ? consumable.consumeTicks() : 0;
    }

    public boolean releaseUsing(ItemStack p_41412_, Level p_41413_, LivingEntity p_41414_, int p_41415_) {
        return false;
    }

    public void appendHoverText(ItemStack p_41421_, Item.TooltipContext p_339594_, List<Component> p_41423_, TooltipFlag p_41424_) {
    }

    public Optional<TooltipComponent> getTooltipImage(ItemStack p_150902_) {
        return Optional.empty();
    }

    @VisibleForTesting
    public final String getDescriptionId() {
        return this.descriptionId;
    }

    public final Component getName() {
        return this.components.getOrDefault(DataComponents.ITEM_NAME, CommonComponents.EMPTY);
    }

    public Component getName(ItemStack p_41458_) {
        return p_41458_.getComponents().getOrDefault(DataComponents.ITEM_NAME, CommonComponents.EMPTY);
    }

    public boolean isFoil(ItemStack p_41453_) {
        return p_41453_.isEnchanted();
    }

    public static BlockHitResult getPlayerPOVHitResult(Level p_41436_, Player p_41437_, ClipContext.Fluid p_41438_) {
        Vec3 vec3 = p_41437_.getEyePosition();
        Vec3 vec31 = vec3.add(p_41437_.calculateViewVector(p_41437_.getXRot(), p_41437_.getYRot()).scale(p_41437_.blockInteractionRange()));
        return p_41436_.clip(new ClipContext(vec3, vec31, ClipContext.Block.OUTLINE, p_41438_, p_41437_));
    }

    public boolean useOnRelease(ItemStack p_41464_) {
        return p_41464_.getItem() == Items.CROSSBOW;
    }

    protected final boolean canCombineRepair;

    @Override
    public boolean isCombineRepairable(ItemStack stack) {
        return canCombineRepair && isDamageable(stack);
    }

    public ItemStack getDefaultInstance() {
        return new ItemStack(this);
    }

    public SoundEvent getBreakingSound() {
        return SoundEvents.ITEM_BREAK;
    }

    public boolean canFitInsideContainerItems() {
        return true;
    }

    @Override
    public FeatureFlagSet requiredFeatures() {
        return this.requiredFeatures;
    }

    public boolean shouldPrintOpWarning(ItemStack p_390446_, @Nullable Player p_390503_) {
        return false;
    }

    public static class Properties implements net.neoforged.neoforge.common.extensions.IItemPropertiesExtensions {
        private static final DependantName<Item, String> BLOCK_DESCRIPTION_ID = p_371954_ -> Util.makeDescriptionId("block", p_371954_.location());
        private static final DependantName<Item, String> ITEM_DESCRIPTION_ID = p_371511_ -> Util.makeDescriptionId("item", p_371511_.location());
        private final DataComponentMap.Builder components = DataComponentMap.builder().addAll(DataComponents.COMMON_ITEM_COMPONENTS);
        @Nullable
        Item craftingRemainingItem;
        FeatureFlagSet requiredFeatures = FeatureFlags.VANILLA_SET;
        @Nullable
        private ResourceKey<Item> id;
        private DependantName<Item, String> descriptionId = ITEM_DESCRIPTION_ID;
        private DependantName<Item, ResourceLocation> model = ResourceKey::location;
        private boolean canCombineRepair = true;

        public Item.Properties setNoCombineRepair() {
            canCombineRepair = false;
            return this;
        }

        public Item.Properties food(FoodProperties p_41490_) {
            return this.food(p_41490_, Consumables.DEFAULT_FOOD);
        }

        public Item.Properties food(FoodProperties p_366775_, Consumable p_366862_) {
            return this.component(DataComponents.FOOD, p_366775_).component(DataComponents.CONSUMABLE, p_366862_);
        }

        public Item.Properties usingConvertsTo(Item p_366596_) {
            return this.component(DataComponents.USE_REMAINDER, new UseRemainder(new ItemStack(p_366596_)));
        }

        public Item.Properties useCooldown(float p_366451_) {
            return this.component(DataComponents.USE_COOLDOWN, new UseCooldown(p_366451_));
        }

        public Item.Properties stacksTo(int p_41488_) {
            return this.component(DataComponents.MAX_STACK_SIZE, p_41488_);
        }

        public Item.Properties durability(int p_41504_) {
            this.component(DataComponents.MAX_DAMAGE, p_41504_);
            this.component(DataComponents.MAX_STACK_SIZE, 1);
            this.component(DataComponents.DAMAGE, 0);
            return this;
        }

        public Item.Properties craftRemainder(Item p_41496_) {
            this.craftingRemainingItem = p_41496_;
            return this;
        }

        public Item.Properties rarity(Rarity p_41498_) {
            return this.component(DataComponents.RARITY, p_41498_);
        }

        public Item.Properties fireResistant() {
            return this.component(DataComponents.DAMAGE_RESISTANT, new DamageResistant(DamageTypeTags.IS_FIRE));
        }

        public Item.Properties jukeboxPlayable(ResourceKey<JukeboxSong> p_350862_) {
            return this.component(DataComponents.JUKEBOX_PLAYABLE, new JukeboxPlayable(new EitherHolder<>(p_350862_), true));
        }

        public Item.Properties enchantable(int p_363982_) {
            return this.component(DataComponents.ENCHANTABLE, new Enchantable(p_363982_));
        }

        public Item.Properties repairable(Item p_364906_) {
            return this.component(DataComponents.REPAIRABLE, new Repairable(HolderSet.direct(p_364906_.builtInRegistryHolder())));
        }

        public Item.Properties repairable(TagKey<Item> p_361677_) {
            HolderGetter<Item> holdergetter = BuiltInRegistries.acquireBootstrapRegistrationLookup(BuiltInRegistries.ITEM);
            return this.component(DataComponents.REPAIRABLE, new Repairable(holdergetter.getOrThrow(p_361677_)));
        }

        public Item.Properties equippable(EquipmentSlot p_371906_) {
            return this.component(DataComponents.EQUIPPABLE, Equippable.builder(p_371906_).build());
        }

        public Item.Properties equippableUnswappable(EquipmentSlot p_372855_) {
            return this.component(DataComponents.EQUIPPABLE, Equippable.builder(p_372855_).setSwappable(false).build());
        }

        public Item.Properties requiredFeatures(FeatureFlag... p_250948_) {
            this.requiredFeatures = FeatureFlags.REGISTRY.subset(p_250948_);
            return this;
        }

        public Item.Properties setId(ResourceKey<Item> p_371930_) {
            this.id = p_371930_;
            return this;
        }

        public Item.Properties overrideDescription(String p_371287_) {
            this.descriptionId = DependantName.fixed(p_371287_);
            return this;
        }

        public Item.Properties useBlockDescriptionPrefix() {
            this.descriptionId = BLOCK_DESCRIPTION_ID;
            return this;
        }

        public Item.Properties useItemDescriptionPrefix() {
            this.descriptionId = ITEM_DESCRIPTION_ID;
            return this;
        }

        protected String effectiveDescriptionId() {
            return this.descriptionId.get(Objects.requireNonNull(this.id, "Item id not set"));
        }

        public ResourceLocation effectiveModel() {
            return this.model.get(Objects.requireNonNull(this.id, "Item id not set"));
        }

        public <T> Item.Properties component(DataComponentType<T> p_330871_, T p_330323_) {
            net.neoforged.neoforge.common.CommonHooks.validateComponent(p_330323_);
            this.components.set(p_330871_, p_330323_);
            return this;
        }

        public Item.Properties attributes(ItemAttributeModifiers p_331533_) {
            return this.component(DataComponents.ATTRIBUTE_MODIFIERS, p_331533_);
        }

        DataComponentMap buildAndValidateComponents(Component p_371796_, ResourceLocation p_371450_) {
            DataComponentMap datacomponentmap = this.components.set(DataComponents.ITEM_NAME, p_371796_).set(DataComponents.ITEM_MODEL, p_371450_).build();
            return validateComponents(datacomponentmap);
        }

        public static DataComponentMap validateComponents(DataComponentMap datacomponentmap) {
            if (datacomponentmap.has(DataComponents.DAMAGE) && datacomponentmap.getOrDefault(DataComponents.MAX_STACK_SIZE, 1) > 1) {
                throw new IllegalStateException("Item cannot have both durability and be stackable");
            } else {
                return datacomponentmap;
            }
        }
    }

    public interface TooltipContext {
        Item.TooltipContext EMPTY = new Item.TooltipContext() {
            @Nullable
            @Override
            public HolderLookup.Provider registries() {
                return null;
            }

            @Override
            public float tickRate() {
                return 20.0F;
            }

            @Nullable
            @Override
            public MapItemSavedData mapData(MapId p_339618_) {
                return null;
            }
        };

        @Nullable
        HolderLookup.Provider registries();

        float tickRate();

        @Nullable
        MapItemSavedData mapData(MapId p_339670_);

        /**
         * Neo: Returns the level if it's available.
         */
        @Nullable
        default Level level() {
            return null;
        }

        static Item.TooltipContext of(@Nullable final Level p_339599_) {
            return p_339599_ == null ? EMPTY : new Item.TooltipContext() {
                @Override
                public HolderLookup.Provider registries() {
                    return p_339599_.registryAccess();
                }

                @Override
                public float tickRate() {
                    return p_339599_.tickRateManager().tickrate();
                }

                @Override
                public MapItemSavedData mapData(MapId p_339628_) {
                    return p_339599_.getMapData(p_339628_);
                }

                @Override
                public Level level() {
                    return p_339599_;
                }
            };
        }

        static Item.TooltipContext of(final HolderLookup.Provider p_339633_) {
            return new Item.TooltipContext() {
                @Override
                public HolderLookup.Provider registries() {
                    return p_339633_;
                }

                @Override
                public float tickRate() {
                    return 20.0F;
                }

                @Nullable
                @Override
                public MapItemSavedData mapData(MapId p_339679_) {
                    return null;
                }
            };
        }
    }
}
