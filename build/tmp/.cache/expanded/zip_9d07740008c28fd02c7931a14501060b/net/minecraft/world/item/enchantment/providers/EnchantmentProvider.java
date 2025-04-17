package net.minecraft.world.item.enchantment.providers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.function.Function;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public interface EnchantmentProvider {
    Codec<EnchantmentProvider> DIRECT_CODEC = BuiltInRegistries.ENCHANTMENT_PROVIDER_TYPE
        .byNameCodec()
        .dispatch(EnchantmentProvider::codec, Function.identity());

    void enchant(ItemStack p_345974_, ItemEnchantments.Mutable p_344824_, RandomSource p_346040_, DifficultyInstance p_348672_);

    MapCodec<? extends EnchantmentProvider> codec();
}
