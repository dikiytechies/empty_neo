package net.minecraft.data.worldgen.features;

import java.util.List;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class FeatureUtils {
    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> p_321722_) {
        AquaticFeatures.bootstrap(p_321722_);
        CaveFeatures.bootstrap(p_321722_);
        EndFeatures.bootstrap(p_321722_);
        MiscOverworldFeatures.bootstrap(p_321722_);
        NetherFeatures.bootstrap(p_321722_);
        OreFeatures.bootstrap(p_321722_);
        PileFeatures.bootstrap(p_321722_);
        TreeFeatures.bootstrap(p_321722_);
        VegetationFeatures.bootstrap(p_321722_);
    }

    private static BlockPredicate simplePatchPredicate(List<Block> p_195009_) {
        BlockPredicate blockpredicate;
        if (!p_195009_.isEmpty()) {
            blockpredicate = BlockPredicate.allOf(BlockPredicate.ONLY_IN_AIR_PREDICATE, BlockPredicate.matchesBlocks(Direction.DOWN.getUnitVec3i(), p_195009_));
        } else {
            blockpredicate = BlockPredicate.ONLY_IN_AIR_PREDICATE;
        }

        return blockpredicate;
    }

    public static RandomPatchConfiguration simpleRandomPatchConfiguration(int p_206471_, Holder<PlacedFeature> p_206472_) {
        return new RandomPatchConfiguration(p_206471_, 7, 3, p_206472_);
    }

    public static <FC extends FeatureConfiguration, F extends Feature<FC>> RandomPatchConfiguration simplePatchConfiguration(
        F p_206481_, FC p_206482_, List<Block> p_206483_, int p_206484_
    ) {
        return simpleRandomPatchConfiguration(p_206484_, PlacementUtils.filtered(p_206481_, p_206482_, simplePatchPredicate(p_206483_)));
    }

    public static <FC extends FeatureConfiguration, F extends Feature<FC>> RandomPatchConfiguration simplePatchConfiguration(
        F p_206477_, FC p_206478_, List<Block> p_206479_
    ) {
        return simplePatchConfiguration(p_206477_, p_206478_, p_206479_, 96);
    }

    public static <FC extends FeatureConfiguration, F extends Feature<FC>> RandomPatchConfiguration simplePatchConfiguration(F p_206474_, FC p_206475_) {
        return simplePatchConfiguration(p_206474_, p_206475_, List.of(), 96);
    }

    public static ResourceKey<ConfiguredFeature<?, ?>> createKey(String p_255643_) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.withDefaultNamespace(p_255643_));
    }

    public static void register(
        BootstrapContext<ConfiguredFeature<?, ?>> p_321616_, ResourceKey<ConfiguredFeature<?, ?>> p_256555_, Feature<NoneFeatureConfiguration> p_255921_
    ) {
        register(p_321616_, p_256555_, p_255921_, FeatureConfiguration.NONE);
    }

    public static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(
        BootstrapContext<ConfiguredFeature<?, ?>> p_321618_, ResourceKey<ConfiguredFeature<?, ?>> p_255983_, F p_255949_, FC p_256398_
    ) {
        p_321618_.register(p_255983_, new ConfiguredFeature(p_255949_, p_256398_));
    }
}
