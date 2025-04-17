package net.minecraft.world.item.crafting;

import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.HolderSetCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.ItemLike;

public final class Ingredient implements StackedContents.IngredientInfo<Holder<Item>>, Predicate<ItemStack> {
    public static final StreamCodec<RegistryFriendlyByteBuf, Ingredient> CONTENTS_STREAM_CODEC = net.neoforged.neoforge.common.crafting.IngredientCodecs.streamCodec(ByteBufCodecs.holderSet(Registries.ITEM)
        .map(Ingredient::new, p_360055_ -> p_360055_.getValuesForSync()));
    public static final StreamCodec<RegistryFriendlyByteBuf, Optional<Ingredient>> OPTIONAL_CONTENTS_STREAM_CODEC = net.neoforged.neoforge.common.crafting.IngredientCodecs.optionalStreamCodec(ByteBufCodecs.holderSet(Registries.ITEM)
        .map(
            p_360058_ -> p_360058_.size() == 0 ? Optional.empty() : Optional.of(new Ingredient((HolderSet<Item>)p_360058_)),
            p_360056_ -> p_360056_.map(p_360062_ -> p_360062_.getValuesForSync()).orElse(HolderSet.direct())
        ));
    public static final Codec<HolderSet<Item>> NON_AIR_HOLDER_SET_CODEC = HolderSetCodec.create(Registries.ITEM, Item.CODEC, false);
    public static final Codec<Ingredient> CODEC = net.neoforged.neoforge.common.crafting.IngredientCodecs.codec(ExtraCodecs.nonEmptyHolderSet(NON_AIR_HOLDER_SET_CODEC).xmap(Ingredient::new, p_360061_ -> p_360061_.values));
    private final HolderSet<Item> values;
    @org.jetbrains.annotations.Nullable
    private net.neoforged.neoforge.common.crafting.ICustomIngredient customIngredient = null;
    @org.jetbrains.annotations.Nullable
    private List<Holder<Item>> customIngredientValues;

    private Ingredient(HolderSet<Item> p_365027_) {
        p_365027_.unwrap().ifRight(p_360057_ -> {
            if (p_360057_.isEmpty()) {
                throw new UnsupportedOperationException("Ingredients can't be empty");
            } else if (p_360057_.contains(Items.AIR.builtInRegistryHolder())) {
                throw new UnsupportedOperationException("Ingredient can't contain air");
            }
        });
        this.values = p_365027_;
    }

    public Ingredient(net.neoforged.neoforge.common.crafting.ICustomIngredient customIngredient) {
        this.values = HolderSet.empty();
        this.customIngredient = customIngredient;
    }

    public static boolean testOptionalIngredient(Optional<Ingredient> p_362504_, ItemStack p_363604_) {
        return p_362504_.<Boolean>map(p_360060_ -> p_360060_.test(p_363604_)).orElseGet(p_363604_::isEmpty);
    }

    @Deprecated
    public Stream<Holder<Item>> items() {
        if (this.customIngredient != null) {
            return updateCustomIngredientValues().stream();
        }
        return this.values.stream();
    }

    public boolean isEmpty() {
        if (this.customIngredient != null) {
            return updateCustomIngredientValues().isEmpty();
        }
        return this.values.size() == 0;
    }

    public boolean test(ItemStack p_43914_) {
        if (this.customIngredient != null) {
            return this.customIngredient.test(p_43914_);
        }
        return p_43914_.is(this.values);
    }

    public boolean acceptsItem(Holder<Item> p_389400_) {
        if (this.customIngredient != null) {
            return updateCustomIngredientValues().contains(p_389400_);
        }
        return this.values.contains(p_389400_);
    }

    @Override
    public boolean equals(Object p_301003_) {
        return p_301003_ instanceof Ingredient ingredient ? java.util.Objects.equals(this.customIngredient, ingredient.customIngredient) && Objects.equals(this.values, ingredient.values) : false;
    }

    @Override
    public int hashCode() {
        if (this.customIngredient != null) {
            return this.customIngredient.hashCode();
        }
        return this.values.hashCode();
    }

    /**
      * Retrieves the underlying values of this ingredient.
      * If this is a {@linkplain #isCustom custom ingredient}, an exception is thrown.
      */
    public HolderSet<Item> getValues() {
        if (isCustom()) {
            throw new IllegalStateException("Cannot retrieve values from custom ingredient!");
        }
        return this.values;
    }

    /**
     * Retrieves the holder set to use for syncing {@linkplain #isSimple() simple} ingredients
     */
    private HolderSet<Item> getValuesForSync() {
        if (isCustom()) {
            return HolderSet.direct(this.items().toList());
        }
        return this.values;
    }

    public boolean isSimple() {
        return this.customIngredient == null || this.customIngredient.isSimple();
    }

    @org.jetbrains.annotations.Nullable
    public net.neoforged.neoforge.common.crafting.ICustomIngredient getCustomIngredient() {
        return this.customIngredient;
    }

    public boolean isCustom() {
        return this.customIngredient != null;
    }

    private List<Holder<Item>> updateCustomIngredientValues() {
        if (this.customIngredientValues == null) {
            this.customIngredientValues = this.customIngredient.items().toList();
        }
        return this.customIngredientValues;
    }

    public static Ingredient of(ItemLike p_364285_) {
        return new Ingredient(HolderSet.direct(p_364285_.asItem().builtInRegistryHolder()));
    }

    public static Ingredient of(ItemLike... p_43930_) {
        return of(Arrays.stream(p_43930_));
    }

    public static Ingredient of(Stream<? extends ItemLike> p_43922_) {
        return new Ingredient(HolderSet.direct(p_43922_.map(p_360054_ -> p_360054_.asItem().builtInRegistryHolder()).toList()));
    }

    public static Ingredient of(HolderSet<Item> p_361145_) {
        return new Ingredient(p_361145_);
    }

    public SlotDisplay display() {
        if (this.customIngredient != null) {
            return this.customIngredient.display();
        }
        return (SlotDisplay)this.values
            .unwrap()
            .map(SlotDisplay.TagSlotDisplay::new, p_380837_ -> new SlotDisplay.Composite(p_380837_.stream().map(Ingredient::displayForSingleItem).toList()));
    }

    public static SlotDisplay optionalIngredientToDisplay(Optional<Ingredient> p_381167_) {
        return p_381167_.map(Ingredient::display).orElse(SlotDisplay.Empty.INSTANCE);
    }

    public static SlotDisplay displayForSingleItem(Holder<Item> p_380940_) {
        SlotDisplay slotdisplay = new SlotDisplay.ItemSlotDisplay(p_380940_);
        ItemStack itemstack = p_380940_.value().getCraftingRemainder();
        if (!itemstack.isEmpty()) {
            SlotDisplay slotdisplay1 = new SlotDisplay.ItemStackSlotDisplay(itemstack);
            return new SlotDisplay.WithRemainder(slotdisplay, slotdisplay1);
        } else {
            return slotdisplay;
        }
    }
}
