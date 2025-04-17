package net.minecraft.world.item.crafting.display;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SmithingTrimRecipe;
import net.minecraft.world.level.block.entity.FuelValues;

public interface SlotDisplay {
    Codec<SlotDisplay> CODEC = BuiltInRegistries.SLOT_DISPLAY.byNameCodec().dispatch(SlotDisplay::type, SlotDisplay.Type::codec);
    StreamCodec<RegistryFriendlyByteBuf, SlotDisplay> STREAM_CODEC = ByteBufCodecs.registry(Registries.SLOT_DISPLAY)
        .dispatch(SlotDisplay::type, SlotDisplay.Type::streamCodec);

    <T> Stream<T> resolve(ContextMap p_381155_, DisplayContentsFactory<T> p_381077_);

    SlotDisplay.Type<? extends SlotDisplay> type();

    default boolean isEnabled(FeatureFlagSet p_379569_) {
        return true;
    }

    default List<ItemStack> resolveForStacks(ContextMap p_380953_) {
        return this.resolve(p_380953_, SlotDisplay.ItemStackContentsFactory.INSTANCE).toList();
    }

    default ItemStack resolveForFirstStack(ContextMap p_381094_) {
        return this.resolve(p_381094_, SlotDisplay.ItemStackContentsFactory.INSTANCE).findFirst().orElse(ItemStack.EMPTY);
    }

    public static class AnyFuel implements SlotDisplay {
        public static final SlotDisplay.AnyFuel INSTANCE = new SlotDisplay.AnyFuel();
        public static final MapCodec<SlotDisplay.AnyFuel> MAP_CODEC = MapCodec.unit(INSTANCE);
        public static final StreamCodec<RegistryFriendlyByteBuf, SlotDisplay.AnyFuel> STREAM_CODEC = StreamCodec.unit(INSTANCE);
        public static final SlotDisplay.Type<SlotDisplay.AnyFuel> TYPE = new SlotDisplay.Type<>(MAP_CODEC, STREAM_CODEC);

        private AnyFuel() {
        }

        @Override
        public SlotDisplay.Type<SlotDisplay.AnyFuel> type() {
            return TYPE;
        }

        @Override
        public String toString() {
            return "<any fuel>";
        }

        @Override
        public <T> Stream<T> resolve(ContextMap p_380999_, DisplayContentsFactory<T> p_381006_) {
            if (p_381006_ instanceof DisplayContentsFactory.ForStacks<T> forstacks) {
                FuelValues fuelvalues = p_380999_.getOptional(SlotDisplayContext.FUEL_VALUES);
                if (fuelvalues != null) {
                    return fuelvalues.fuelItems().stream().map(forstacks::forStack);
                }
            }

            return Stream.empty();
        }
    }

    public static record Composite(List<SlotDisplay> contents) implements SlotDisplay {
        public static final MapCodec<SlotDisplay.Composite> MAP_CODEC = RecordCodecBuilder.mapCodec(
            p_379498_ -> p_379498_.group(SlotDisplay.CODEC.listOf().fieldOf("contents").forGetter(SlotDisplay.Composite::contents))
                    .apply(p_379498_, SlotDisplay.Composite::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, SlotDisplay.Composite> STREAM_CODEC = StreamCodec.composite(
            SlotDisplay.STREAM_CODEC.apply(ByteBufCodecs.list()), SlotDisplay.Composite::contents, SlotDisplay.Composite::new
        );
        public static final SlotDisplay.Type<SlotDisplay.Composite> TYPE = new SlotDisplay.Type<>(MAP_CODEC, STREAM_CODEC);

        @Override
        public SlotDisplay.Type<SlotDisplay.Composite> type() {
            return TYPE;
        }

        @Override
        public <T> Stream<T> resolve(ContextMap p_381068_, DisplayContentsFactory<T> p_380997_) {
            return this.contents.stream().flatMap(p_380856_ -> p_380856_.resolve(p_381068_, p_380997_));
        }

        @Override
        public boolean isEnabled(FeatureFlagSet p_379563_) {
            return this.contents.stream().allMatch(p_380321_ -> p_380321_.isEnabled(p_379563_));
        }
    }

    public static class Empty implements SlotDisplay {
        public static final SlotDisplay.Empty INSTANCE = new SlotDisplay.Empty();
        public static final MapCodec<SlotDisplay.Empty> MAP_CODEC = MapCodec.unit(INSTANCE);
        public static final StreamCodec<RegistryFriendlyByteBuf, SlotDisplay.Empty> STREAM_CODEC = StreamCodec.unit(INSTANCE);
        public static final SlotDisplay.Type<SlotDisplay.Empty> TYPE = new SlotDisplay.Type<>(MAP_CODEC, STREAM_CODEC);

        private Empty() {
        }

        @Override
        public SlotDisplay.Type<SlotDisplay.Empty> type() {
            return TYPE;
        }

        @Override
        public String toString() {
            return "<empty>";
        }

        @Override
        public <T> Stream<T> resolve(ContextMap p_381154_, DisplayContentsFactory<T> p_380978_) {
            return Stream.empty();
        }
    }

    public static record ItemSlotDisplay(Holder<Item> item) implements SlotDisplay {
        public static final MapCodec<SlotDisplay.ItemSlotDisplay> MAP_CODEC = RecordCodecBuilder.mapCodec(
            p_381584_ -> p_381584_.group(Item.CODEC.fieldOf("item").forGetter(SlotDisplay.ItemSlotDisplay::item))
                    .apply(p_381584_, SlotDisplay.ItemSlotDisplay::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, SlotDisplay.ItemSlotDisplay> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.holderRegistry(Registries.ITEM), SlotDisplay.ItemSlotDisplay::item, SlotDisplay.ItemSlotDisplay::new
        );
        public static final SlotDisplay.Type<SlotDisplay.ItemSlotDisplay> TYPE = new SlotDisplay.Type<>(MAP_CODEC, STREAM_CODEC);

        public ItemSlotDisplay(Item p_379935_) {
            this(p_379935_.builtInRegistryHolder());
        }

        @Override
        public SlotDisplay.Type<SlotDisplay.ItemSlotDisplay> type() {
            return TYPE;
        }

        @Override
        public <T> Stream<T> resolve(ContextMap p_381162_, DisplayContentsFactory<T> p_381086_) {
            return p_381086_ instanceof DisplayContentsFactory.ForStacks<T> forstacks ? Stream.of(forstacks.forStack(this.item)) : Stream.empty();
        }

        @Override
        public boolean isEnabled(FeatureFlagSet p_379445_) {
            return this.item.value().isEnabled(p_379445_);
        }
    }

    public static class ItemStackContentsFactory implements DisplayContentsFactory.ForStacks<ItemStack> {
        public static final SlotDisplay.ItemStackContentsFactory INSTANCE = new SlotDisplay.ItemStackContentsFactory();

        public ItemStack forStack(ItemStack p_381043_) {
            return p_381043_;
        }
    }

    public static record ItemStackSlotDisplay(ItemStack stack) implements SlotDisplay {
        public static final MapCodec<SlotDisplay.ItemStackSlotDisplay> MAP_CODEC = RecordCodecBuilder.mapCodec(
            p_379658_ -> p_379658_.group(ItemStack.STRICT_CODEC.fieldOf("item").forGetter(SlotDisplay.ItemStackSlotDisplay::stack))
                    .apply(p_379658_, SlotDisplay.ItemStackSlotDisplay::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, SlotDisplay.ItemStackSlotDisplay> STREAM_CODEC = StreamCodec.composite(
            ItemStack.STREAM_CODEC, SlotDisplay.ItemStackSlotDisplay::stack, SlotDisplay.ItemStackSlotDisplay::new
        );
        public static final SlotDisplay.Type<SlotDisplay.ItemStackSlotDisplay> TYPE = new SlotDisplay.Type<>(MAP_CODEC, STREAM_CODEC);

        @Override
        public SlotDisplay.Type<SlotDisplay.ItemStackSlotDisplay> type() {
            return TYPE;
        }

        @Override
        public <T> Stream<T> resolve(ContextMap p_381143_, DisplayContentsFactory<T> p_381015_) {
            return p_381015_ instanceof DisplayContentsFactory.ForStacks<T> forstacks ? Stream.of(forstacks.forStack(this.stack)) : Stream.empty();
        }

        @Override
        public boolean equals(Object p_379392_) {
            if (this == p_379392_) {
                return true;
            } else {
                if (p_379392_ instanceof SlotDisplay.ItemStackSlotDisplay slotdisplay$itemstackslotdisplay
                    && ItemStack.matches(this.stack, slotdisplay$itemstackslotdisplay.stack)) {
                    return true;
                }

                return false;
            }
        }

        @Override
        public boolean isEnabled(FeatureFlagSet p_380009_) {
            return this.stack.getItem().isEnabled(p_380009_);
        }
    }

    public static record SmithingTrimDemoSlotDisplay(SlotDisplay base, SlotDisplay material, SlotDisplay pattern) implements SlotDisplay {
        public static final MapCodec<SlotDisplay.SmithingTrimDemoSlotDisplay> MAP_CODEC = RecordCodecBuilder.mapCodec(
            p_381120_ -> p_381120_.group(
                        SlotDisplay.CODEC.fieldOf("base").forGetter(SlotDisplay.SmithingTrimDemoSlotDisplay::base),
                        SlotDisplay.CODEC.fieldOf("material").forGetter(SlotDisplay.SmithingTrimDemoSlotDisplay::material),
                        SlotDisplay.CODEC.fieldOf("pattern").forGetter(SlotDisplay.SmithingTrimDemoSlotDisplay::pattern)
                    )
                    .apply(p_381120_, SlotDisplay.SmithingTrimDemoSlotDisplay::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, SlotDisplay.SmithingTrimDemoSlotDisplay> STREAM_CODEC = StreamCodec.composite(
            SlotDisplay.STREAM_CODEC,
            SlotDisplay.SmithingTrimDemoSlotDisplay::base,
            SlotDisplay.STREAM_CODEC,
            SlotDisplay.SmithingTrimDemoSlotDisplay::material,
            SlotDisplay.STREAM_CODEC,
            SlotDisplay.SmithingTrimDemoSlotDisplay::pattern,
            SlotDisplay.SmithingTrimDemoSlotDisplay::new
        );
        public static final SlotDisplay.Type<SlotDisplay.SmithingTrimDemoSlotDisplay> TYPE = new SlotDisplay.Type<>(MAP_CODEC, STREAM_CODEC);

        @Override
        public SlotDisplay.Type<SlotDisplay.SmithingTrimDemoSlotDisplay> type() {
            return TYPE;
        }

        @Override
        public <T> Stream<T> resolve(ContextMap p_381017_, DisplayContentsFactory<T> p_381079_) {
            if (p_381079_ instanceof DisplayContentsFactory.ForStacks<T> forstacks) {
                HolderLookup.Provider holderlookup$provider = p_381017_.getOptional(SlotDisplayContext.REGISTRIES);
                if (holderlookup$provider != null) {
                    RandomSource randomsource = RandomSource.create((long)System.identityHashCode(this));
                    List<ItemStack> list = this.base.resolveForStacks(p_381017_);
                    if (list.isEmpty()) {
                        return Stream.empty();
                    }

                    List<ItemStack> list1 = this.material.resolveForStacks(p_381017_);
                    if (list1.isEmpty()) {
                        return Stream.empty();
                    }

                    List<ItemStack> list2 = this.pattern.resolveForStacks(p_381017_);
                    if (list2.isEmpty()) {
                        return Stream.empty();
                    }

                    return Stream.<ItemStack>generate(() -> {
                        ItemStack itemstack = Util.getRandom(list, randomsource);
                        ItemStack itemstack1 = Util.getRandom(list1, randomsource);
                        ItemStack itemstack2 = Util.getRandom(list2, randomsource);
                        return SmithingTrimRecipe.applyTrim(holderlookup$provider, itemstack, itemstack1, itemstack2);
                    }).limit(256L).filter(p_381123_ -> !p_381123_.isEmpty()).limit(16L).map(forstacks::forStack);
                }
            }

            return Stream.empty();
        }
    }

    public static record TagSlotDisplay(TagKey<Item> tag) implements SlotDisplay {
        public static final MapCodec<SlotDisplay.TagSlotDisplay> MAP_CODEC = RecordCodecBuilder.mapCodec(
            p_379704_ -> p_379704_.group(TagKey.codec(Registries.ITEM).fieldOf("tag").forGetter(SlotDisplay.TagSlotDisplay::tag))
                    .apply(p_379704_, SlotDisplay.TagSlotDisplay::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, SlotDisplay.TagSlotDisplay> STREAM_CODEC = StreamCodec.composite(
            TagKey.streamCodec(Registries.ITEM), SlotDisplay.TagSlotDisplay::tag, SlotDisplay.TagSlotDisplay::new
        );
        public static final SlotDisplay.Type<SlotDisplay.TagSlotDisplay> TYPE = new SlotDisplay.Type<>(MAP_CODEC, STREAM_CODEC);

        @Override
        public SlotDisplay.Type<SlotDisplay.TagSlotDisplay> type() {
            return TYPE;
        }

        @Override
        public <T> Stream<T> resolve(ContextMap p_381013_, DisplayContentsFactory<T> p_381072_) {
            if (p_381072_ instanceof DisplayContentsFactory.ForStacks<T> forstacks) {
                HolderLookup.Provider holderlookup$provider = p_381013_.getOptional(SlotDisplayContext.REGISTRIES);
                if (holderlookup$provider != null) {
                    return holderlookup$provider.lookupOrThrow(Registries.ITEM)
                        .get(this.tag)
                        .map(p_380858_ -> p_380858_.stream().map(forstacks::forStack))
                        .stream()
                        .flatMap(p_380859_ -> p_380859_);
                }
            }

            return Stream.empty();
        }
    }

    public static record Type<T extends SlotDisplay>(MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {
    }

    public static record WithRemainder(SlotDisplay input, SlotDisplay remainder) implements SlotDisplay {
        public static final MapCodec<SlotDisplay.WithRemainder> MAP_CODEC = RecordCodecBuilder.mapCodec(
            p_380938_ -> p_380938_.group(
                        SlotDisplay.CODEC.fieldOf("input").forGetter(SlotDisplay.WithRemainder::input),
                        SlotDisplay.CODEC.fieldOf("remainder").forGetter(SlotDisplay.WithRemainder::remainder)
                    )
                    .apply(p_380938_, SlotDisplay.WithRemainder::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, SlotDisplay.WithRemainder> STREAM_CODEC = StreamCodec.composite(
            SlotDisplay.STREAM_CODEC,
            SlotDisplay.WithRemainder::input,
            SlotDisplay.STREAM_CODEC,
            SlotDisplay.WithRemainder::remainder,
            SlotDisplay.WithRemainder::new
        );
        public static final SlotDisplay.Type<SlotDisplay.WithRemainder> TYPE = new SlotDisplay.Type<>(MAP_CODEC, STREAM_CODEC);

        @Override
        public SlotDisplay.Type<SlotDisplay.WithRemainder> type() {
            return TYPE;
        }

        @Override
        public <T> Stream<T> resolve(ContextMap p_381035_, DisplayContentsFactory<T> p_380959_) {
            if (p_380959_ instanceof DisplayContentsFactory.ForRemainders<T> forremainders) {
                List<T> list = this.remainder.resolve(p_381035_, p_380959_).toList();
                return this.input.resolve(p_381035_, p_380959_).map(p_381158_ -> forremainders.addRemainder((T)p_381158_, list));
            } else {
                return this.input.resolve(p_381035_, p_380959_);
            }
        }

        @Override
        public boolean isEnabled(FeatureFlagSet p_381096_) {
            return this.input.isEnabled(p_381096_) && this.remainder.isEnabled(p_381096_);
        }
    }
}
