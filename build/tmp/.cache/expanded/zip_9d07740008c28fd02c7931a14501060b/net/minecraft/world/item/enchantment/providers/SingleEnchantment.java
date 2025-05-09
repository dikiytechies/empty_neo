package net.minecraft.world.item.enchantment.providers;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public record SingleEnchantment(Holder<Enchantment> enchantment, IntProvider level) implements EnchantmentProvider {
    public static final MapCodec<SingleEnchantment> CODEC = RecordCodecBuilder.mapCodec(
        p_346042_ -> p_346042_.group(
                    Enchantment.CODEC.fieldOf("enchantment").forGetter(SingleEnchantment::enchantment),
                    IntProvider.CODEC.fieldOf("level").forGetter(SingleEnchantment::level)
                )
                .apply(p_346042_, SingleEnchantment::new)
    );

    @Override
    public void enchant(ItemStack p_346218_, ItemEnchantments.Mutable p_345570_, RandomSource p_345742_, DifficultyInstance p_348562_) {
        p_345570_.upgrade(
            this.enchantment, Mth.clamp(this.level.sample(p_345742_), this.enchantment.value().getMinLevel(), this.enchantment.value().getMaxLevel())
        );
    }

    @Override
    public MapCodec<SingleEnchantment> codec() {
        return CODEC;
    }
}
