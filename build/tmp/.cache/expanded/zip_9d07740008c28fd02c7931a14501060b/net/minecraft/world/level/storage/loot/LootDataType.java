package net.minecraft.world.level.storage.loot;

import com.mojang.serialization.Codec;
import java.util.stream.Stream;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public record LootDataType<T>(ResourceKey<Registry<T>> registryKey, Codec<T> codec, LootDataType.Validator<T> validator, @org.jetbrains.annotations.Nullable T defaultValue, Codec<java.util.Optional<T>> conditionalCodec, java.util.function.BiConsumer<T, net.minecraft.resources.ResourceLocation> idSetter) {
    public static final LootDataType<LootItemCondition> PREDICATE = new LootDataType<>(
        Registries.PREDICATE, LootItemCondition.DIRECT_CODEC, createSimpleValidator()
    );
    public static final LootDataType<LootItemFunction> MODIFIER = new LootDataType<>(
        Registries.ITEM_MODIFIER, LootItemFunctions.ROOT_CODEC, createSimpleValidator()
    );
    public static final LootDataType<LootTable> TABLE = new LootDataType<>(Registries.LOOT_TABLE, LootTable.DIRECT_CODEC, createLootTableValidator(), LootTable.EMPTY, LootTable::setLootTableId);

    /**
     * @deprecated Neo: use the constructor {@link #LootDataType(ResourceKey, Codec, Validator, T, java.util.function.BiConsumer) with a default value and id setter} to support conditions
     */
    @Deprecated
    private LootDataType(ResourceKey<Registry<T>> registryKey, Codec<T> codec, LootDataType.Validator<T> validator) {
        this(registryKey, codec, validator, null, (it, id) -> {});
    }

    private LootDataType(ResourceKey<Registry<T>> registryKey, Codec<T> codec, LootDataType.Validator<T> validator, @org.jetbrains.annotations.Nullable T defaultValue, java.util.function.BiConsumer<T, net.minecraft.resources.ResourceLocation> idSetter) {
        this(registryKey, codec, validator, defaultValue, net.neoforged.neoforge.common.conditions.ConditionalOps.createConditionalCodec(codec), idSetter);
    }

    public void runValidation(ValidationContext p_279366_, ResourceKey<T> p_336149_, T p_279124_) {
        this.validator.run(p_279366_, p_336149_, p_279124_);
    }

    public static Stream<LootDataType<?>> values() {
        return Stream.of(PREDICATE, MODIFIER, TABLE);
    }

    private static <T extends LootContextUser> LootDataType.Validator<T> createSimpleValidator() {
        return (p_339560_, p_339561_, p_339562_) -> p_339562_.validate(
                p_339560_.enterElement("{" + p_339561_.registry() + "/" + p_339561_.location() + "}", p_339561_)
            );
    }

    private static LootDataType.Validator<LootTable> createLootTableValidator() {
        return (p_279333_, p_279227_, p_279406_) -> {
            p_279406_.validate(
                    p_279333_.setContextKeySet(p_279406_.getParamSet()).enterElement("{" + p_279227_.registry() + ":" + p_279227_.location() + "}", p_279227_)
            );
        };
    }

    @FunctionalInterface
    public interface Validator<T> {
        void run(ValidationContext p_279419_, ResourceKey<T> p_335916_, T p_279326_);
    }
}
