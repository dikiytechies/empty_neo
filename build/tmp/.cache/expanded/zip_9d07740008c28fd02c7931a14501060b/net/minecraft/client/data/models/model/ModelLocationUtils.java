package net.minecraft.client.data.models.model;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelLocationUtils {
    @Deprecated
    public static ResourceLocation decorateBlockModelLocation(String p_388520_) {
        // Neo: Use ResourceLocation.parse to support modded paths
        return ResourceLocation.parse(p_388520_).withPrefix("block/");
    }

    public static ResourceLocation decorateItemModelLocation(String p_387226_) {
        // Neo: Use ResourceLocation.parse to support modded paths
        return ResourceLocation.parse(p_387226_).withPrefix("item/");
    }

    public static ResourceLocation getModelLocation(Block p_387758_, String p_388221_) {
        ResourceLocation resourcelocation = BuiltInRegistries.BLOCK.getKey(p_387758_);
        return resourcelocation.withPath(p_388420_ -> "block/" + p_388420_ + p_388221_);
    }

    public static ResourceLocation getModelLocation(Block p_387471_) {
        ResourceLocation resourcelocation = BuiltInRegistries.BLOCK.getKey(p_387471_);
        return resourcelocation.withPrefix("block/");
    }

    public static ResourceLocation getModelLocation(Item p_388071_) {
        ResourceLocation resourcelocation = BuiltInRegistries.ITEM.getKey(p_388071_);
        return resourcelocation.withPrefix("item/");
    }

    public static ResourceLocation getModelLocation(Item p_387888_, String p_388435_) {
        ResourceLocation resourcelocation = BuiltInRegistries.ITEM.getKey(p_387888_);
        return resourcelocation.withPath(p_386751_ -> "item/" + p_386751_ + p_388435_);
    }
}
