package net.minecraft.client.data.models;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EquipmentAssetProvider implements DataProvider {
    private final PackOutput.PathProvider pathProvider;

    public EquipmentAssetProvider(PackOutput p_387559_) {
        this.pathProvider = p_387559_.createPathProvider(PackOutput.Target.RESOURCE_PACK, "equipment");
    }

    protected void registerModels(BiConsumer<ResourceKey<EquipmentAsset>, EquipmentClientInfo> output) {
        bootstrap(output);
    }

    private static void bootstrap(BiConsumer<ResourceKey<EquipmentAsset>, EquipmentClientInfo> p_387865_) {
        p_387865_.accept(
            EquipmentAssets.LEATHER,
            EquipmentClientInfo.builder()
                .addHumanoidLayers(ResourceLocation.withDefaultNamespace("leather"), true)
                .addHumanoidLayers(ResourceLocation.withDefaultNamespace("leather_overlay"), false)
                .addLayers(
                    EquipmentClientInfo.LayerType.HORSE_BODY, EquipmentClientInfo.Layer.leatherDyeable(ResourceLocation.withDefaultNamespace("leather"), true)
                )
                .build()
        );
        p_387865_.accept(EquipmentAssets.CHAINMAIL, onlyHumanoid("chainmail"));
        p_387865_.accept(EquipmentAssets.IRON, humanoidAndHorse("iron"));
        p_387865_.accept(EquipmentAssets.GOLD, humanoidAndHorse("gold"));
        p_387865_.accept(EquipmentAssets.DIAMOND, humanoidAndHorse("diamond"));
        p_387865_.accept(
            EquipmentAssets.TURTLE_SCUTE,
            EquipmentClientInfo.builder().addMainHumanoidLayer(ResourceLocation.withDefaultNamespace("turtle_scute"), false).build()
        );
        p_387865_.accept(EquipmentAssets.NETHERITE, onlyHumanoid("netherite"));
        p_387865_.accept(
            EquipmentAssets.ARMADILLO_SCUTE,
            EquipmentClientInfo.builder()
                .addLayers(
                    EquipmentClientInfo.LayerType.WOLF_BODY,
                    EquipmentClientInfo.Layer.onlyIfDyed(ResourceLocation.withDefaultNamespace("armadillo_scute"), false)
                )
                .addLayers(
                    EquipmentClientInfo.LayerType.WOLF_BODY,
                    EquipmentClientInfo.Layer.onlyIfDyed(ResourceLocation.withDefaultNamespace("armadillo_scute_overlay"), true)
                )
                .build()
        );
        p_387865_.accept(
            EquipmentAssets.ELYTRA,
            EquipmentClientInfo.builder()
                .addLayers(
                    EquipmentClientInfo.LayerType.WINGS, new EquipmentClientInfo.Layer(ResourceLocation.withDefaultNamespace("elytra"), Optional.empty(), true)
                )
                .build()
        );

        for (Entry<DyeColor, ResourceKey<EquipmentAsset>> entry : EquipmentAssets.CARPETS.entrySet()) {
            DyeColor dyecolor = entry.getKey();
            ResourceKey<EquipmentAsset> resourcekey = entry.getValue();
            p_387865_.accept(
                resourcekey,
                EquipmentClientInfo.builder()
                    .addLayers(
                        EquipmentClientInfo.LayerType.LLAMA_BODY,
                        new EquipmentClientInfo.Layer(ResourceLocation.withDefaultNamespace(dyecolor.getSerializedName()))
                    )
                    .build()
            );
        }

        p_387865_.accept(
            EquipmentAssets.TRADER_LLAMA,
            EquipmentClientInfo.builder()
                .addLayers(EquipmentClientInfo.LayerType.LLAMA_BODY, new EquipmentClientInfo.Layer(ResourceLocation.withDefaultNamespace("trader_llama")))
                .build()
        );
    }

    public static EquipmentClientInfo onlyHumanoid(String p_388505_) {
        return EquipmentClientInfo.builder().addHumanoidLayers(ResourceLocation.parse(p_388505_)).build();
    }

    public static EquipmentClientInfo humanoidAndHorse(String p_386720_) {
        return EquipmentClientInfo.builder()
            .addHumanoidLayers(ResourceLocation.parse(p_386720_))
            .addLayers(
                EquipmentClientInfo.LayerType.HORSE_BODY, EquipmentClientInfo.Layer.leatherDyeable(ResourceLocation.parse(p_386720_), false)
            )
            .build();
    }

    @Override
    public CompletableFuture<?> run(CachedOutput p_387304_) {
        Map<ResourceKey<EquipmentAsset>, EquipmentClientInfo> map = new HashMap<>();
        registerModels((p_386976_, p_388942_) -> {
            if (map.putIfAbsent(p_386976_, p_388942_) != null) {
                throw new IllegalStateException("Tried to register equipment asset twice for id: " + p_386976_);
            }
        });
        return DataProvider.saveAll(p_387304_, EquipmentClientInfo.CODEC, this.pathProvider::json, map);
    }

    @Override
    public String getName() {
        return "Equipment Asset Definitions";
    }
}
