package net.minecraft.client.resources.model;

import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EquipmentAssetManager extends SimpleJsonResourceReloadListener<EquipmentClientInfo> {
    public static final EquipmentClientInfo MISSING = new EquipmentClientInfo(Map.of());
    private static final FileToIdConverter ASSET_LISTER = FileToIdConverter.json("equipment");
    private Map<ResourceKey<EquipmentAsset>, EquipmentClientInfo> equipmentAssets = Map.of();

    public EquipmentAssetManager() {
        super(EquipmentClientInfo.CODEC, ASSET_LISTER);
    }

    protected void apply(Map<ResourceLocation, EquipmentClientInfo> p_388124_, ResourceManager p_387311_, ProfilerFiller p_387076_) {
        this.equipmentAssets = p_388124_.entrySet()
            .stream()
            .collect(Collectors.toUnmodifiableMap(p_386999_ -> ResourceKey.create(EquipmentAssets.ROOT_ID, p_386999_.getKey()), Entry::getValue));
    }

    public EquipmentClientInfo get(ResourceKey<EquipmentAsset> p_388632_) {
        return this.equipmentAssets.getOrDefault(p_388632_, MISSING);
    }
}
