package net.minecraft.world.item.equipment.trim;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;

public record TrimMaterial(String assetName, Holder<Item> ingredient, Map<ResourceKey<EquipmentAsset>, String> overrideArmorAssets, Component description) {
    public static final Codec<TrimMaterial> DIRECT_CODEC = RecordCodecBuilder.create(
        p_386417_ -> p_386417_.group(
                    ExtraCodecs.RESOURCE_PATH_CODEC.fieldOf("asset_name").forGetter(TrimMaterial::assetName),
                    Item.CODEC.fieldOf("ingredient").forGetter(TrimMaterial::ingredient),
                    Codec.unboundedMap(ResourceKey.codec(EquipmentAssets.ROOT_ID), Codec.STRING)
                        .optionalFieldOf("override_armor_assets", Map.of())
                        .forGetter(TrimMaterial::overrideArmorAssets),
                    ComponentSerialization.CODEC.fieldOf("description").forGetter(TrimMaterial::description)
                )
                .apply(p_386417_, TrimMaterial::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, TrimMaterial> DIRECT_STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.STRING_UTF8,
        TrimMaterial::assetName,
        ByteBufCodecs.holderRegistry(Registries.ITEM),
        TrimMaterial::ingredient,
        ByteBufCodecs.map(Object2ObjectOpenHashMap::new, ResourceKey.streamCodec(EquipmentAssets.ROOT_ID), ByteBufCodecs.STRING_UTF8),
        TrimMaterial::overrideArmorAssets,
        ComponentSerialization.STREAM_CODEC,
        TrimMaterial::description,
        TrimMaterial::new
    );
    public static final Codec<Holder<TrimMaterial>> CODEC = RegistryFileCodec.create(Registries.TRIM_MATERIAL, DIRECT_CODEC);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<TrimMaterial>> STREAM_CODEC = ByteBufCodecs.holder(
        Registries.TRIM_MATERIAL, DIRECT_STREAM_CODEC
    );

    public static TrimMaterial create(String p_371690_, Item p_371621_, Component p_371808_, Map<ResourceKey<EquipmentAsset>, String> p_371491_) {
        return new TrimMaterial(p_371690_, BuiltInRegistries.ITEM.wrapAsHolder(p_371621_), p_371491_, p_371808_);
    }
}
