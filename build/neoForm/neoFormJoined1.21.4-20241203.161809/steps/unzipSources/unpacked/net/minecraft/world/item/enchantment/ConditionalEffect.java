package net.minecraft.world.item.enchantment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public record ConditionalEffect<T>(T effect, Optional<LootItemCondition> requirements) {
    public static Codec<LootItemCondition> conditionCodec(ContextKeySet p_381056_) {
        return LootItemCondition.DIRECT_CODEC
            .validate(
                p_380866_ -> {
                    ProblemReporter.Collector problemreporter$collector = new ProblemReporter.Collector();
                    ValidationContext validationcontext = new ValidationContext(problemreporter$collector, p_381056_);
                    p_380866_.validate(validationcontext);
                    return problemreporter$collector.getReport()
                        .map(p_344978_ -> DataResult.<LootItemCondition>error(() -> "Validation error in enchantment effect condition: " + p_344978_))
                        .orElseGet(() -> DataResult.success(p_380866_));
                }
            );
    }

    public static <T> Codec<ConditionalEffect<T>> codec(Codec<T> p_345918_, ContextKeySet p_381103_) {
        return RecordCodecBuilder.create(
            p_380864_ -> p_380864_.group(
                        p_345918_.fieldOf("effect").forGetter(ConditionalEffect::effect),
                        conditionCodec(p_381103_).optionalFieldOf("requirements").forGetter(ConditionalEffect::requirements)
                    )
                    .apply(p_380864_, ConditionalEffect::new)
        );
    }

    public boolean matches(LootContext p_344977_) {
        return this.requirements.isEmpty() ? true : this.requirements.get().test(p_344977_);
    }
}
