package net.minecraft.world.item;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BannerPatternTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.InstrumentTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.DamageResistant;
import net.minecraft.world.item.component.DeathProtection;
import net.minecraft.world.item.component.DebugStickState;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.component.MapDecorations;
import net.minecraft.world.item.component.MapItemColor;
import net.minecraft.world.item.component.OminousBottleAmplifier;
import net.minecraft.world.item.component.SuspiciousStewEffects;
import net.minecraft.world.item.component.WritableBookContent;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.equipment.ArmorMaterials;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.minecraft.world.level.block.entity.PotDecorations;
import net.minecraft.world.level.material.Fluids;

public class Items {
    public static final Item AIR = registerBlock(Blocks.AIR, AirItem::new);
    public static final Item STONE = registerBlock(Blocks.STONE);
    public static final Item GRANITE = registerBlock(Blocks.GRANITE);
    public static final Item POLISHED_GRANITE = registerBlock(Blocks.POLISHED_GRANITE);
    public static final Item DIORITE = registerBlock(Blocks.DIORITE);
    public static final Item POLISHED_DIORITE = registerBlock(Blocks.POLISHED_DIORITE);
    public static final Item ANDESITE = registerBlock(Blocks.ANDESITE);
    public static final Item POLISHED_ANDESITE = registerBlock(Blocks.POLISHED_ANDESITE);
    public static final Item DEEPSLATE = registerBlock(Blocks.DEEPSLATE);
    public static final Item COBBLED_DEEPSLATE = registerBlock(Blocks.COBBLED_DEEPSLATE);
    public static final Item POLISHED_DEEPSLATE = registerBlock(Blocks.POLISHED_DEEPSLATE);
    public static final Item CALCITE = registerBlock(Blocks.CALCITE);
    public static final Item TUFF = registerBlock(Blocks.TUFF);
    public static final Item TUFF_SLAB = registerBlock(Blocks.TUFF_SLAB);
    public static final Item TUFF_STAIRS = registerBlock(Blocks.TUFF_STAIRS);
    public static final Item TUFF_WALL = registerBlock(Blocks.TUFF_WALL);
    public static final Item CHISELED_TUFF = registerBlock(Blocks.CHISELED_TUFF);
    public static final Item POLISHED_TUFF = registerBlock(Blocks.POLISHED_TUFF);
    public static final Item POLISHED_TUFF_SLAB = registerBlock(Blocks.POLISHED_TUFF_SLAB);
    public static final Item POLISHED_TUFF_STAIRS = registerBlock(Blocks.POLISHED_TUFF_STAIRS);
    public static final Item POLISHED_TUFF_WALL = registerBlock(Blocks.POLISHED_TUFF_WALL);
    public static final Item TUFF_BRICKS = registerBlock(Blocks.TUFF_BRICKS);
    public static final Item TUFF_BRICK_SLAB = registerBlock(Blocks.TUFF_BRICK_SLAB);
    public static final Item TUFF_BRICK_STAIRS = registerBlock(Blocks.TUFF_BRICK_STAIRS);
    public static final Item TUFF_BRICK_WALL = registerBlock(Blocks.TUFF_BRICK_WALL);
    public static final Item CHISELED_TUFF_BRICKS = registerBlock(Blocks.CHISELED_TUFF_BRICKS);
    public static final Item DRIPSTONE_BLOCK = registerBlock(Blocks.DRIPSTONE_BLOCK);
    public static final Item GRASS_BLOCK = registerBlock(Blocks.GRASS_BLOCK);
    public static final Item DIRT = registerBlock(Blocks.DIRT);
    public static final Item COARSE_DIRT = registerBlock(Blocks.COARSE_DIRT);
    public static final Item PODZOL = registerBlock(Blocks.PODZOL);
    public static final Item ROOTED_DIRT = registerBlock(Blocks.ROOTED_DIRT);
    public static final Item MUD = registerBlock(Blocks.MUD);
    public static final Item CRIMSON_NYLIUM = registerBlock(Blocks.CRIMSON_NYLIUM);
    public static final Item WARPED_NYLIUM = registerBlock(Blocks.WARPED_NYLIUM);
    public static final Item COBBLESTONE = registerBlock(Blocks.COBBLESTONE);
    public static final Item OAK_PLANKS = registerBlock(Blocks.OAK_PLANKS);
    public static final Item SPRUCE_PLANKS = registerBlock(Blocks.SPRUCE_PLANKS);
    public static final Item BIRCH_PLANKS = registerBlock(Blocks.BIRCH_PLANKS);
    public static final Item JUNGLE_PLANKS = registerBlock(Blocks.JUNGLE_PLANKS);
    public static final Item ACACIA_PLANKS = registerBlock(Blocks.ACACIA_PLANKS);
    public static final Item CHERRY_PLANKS = registerBlock(Blocks.CHERRY_PLANKS);
    public static final Item DARK_OAK_PLANKS = registerBlock(Blocks.DARK_OAK_PLANKS);
    public static final Item PALE_OAK_PLANKS = registerBlock(Blocks.PALE_OAK_PLANKS);
    public static final Item MANGROVE_PLANKS = registerBlock(Blocks.MANGROVE_PLANKS);
    public static final Item BAMBOO_PLANKS = registerBlock(Blocks.BAMBOO_PLANKS);
    public static final Item CRIMSON_PLANKS = registerBlock(Blocks.CRIMSON_PLANKS);
    public static final Item WARPED_PLANKS = registerBlock(Blocks.WARPED_PLANKS);
    public static final Item BAMBOO_MOSAIC = registerBlock(Blocks.BAMBOO_MOSAIC);
    public static final Item OAK_SAPLING = registerBlock(Blocks.OAK_SAPLING);
    public static final Item SPRUCE_SAPLING = registerBlock(Blocks.SPRUCE_SAPLING);
    public static final Item BIRCH_SAPLING = registerBlock(Blocks.BIRCH_SAPLING);
    public static final Item JUNGLE_SAPLING = registerBlock(Blocks.JUNGLE_SAPLING);
    public static final Item ACACIA_SAPLING = registerBlock(Blocks.ACACIA_SAPLING);
    public static final Item CHERRY_SAPLING = registerBlock(Blocks.CHERRY_SAPLING);
    public static final Item DARK_OAK_SAPLING = registerBlock(Blocks.DARK_OAK_SAPLING);
    public static final Item PALE_OAK_SAPLING = registerBlock(Blocks.PALE_OAK_SAPLING);
    public static final Item MANGROVE_PROPAGULE = registerBlock(Blocks.MANGROVE_PROPAGULE);
    public static final Item BEDROCK = registerBlock(Blocks.BEDROCK);
    public static final Item SAND = registerBlock(Blocks.SAND);
    public static final Item SUSPICIOUS_SAND = registerBlock(Blocks.SUSPICIOUS_SAND);
    public static final Item SUSPICIOUS_GRAVEL = registerBlock(Blocks.SUSPICIOUS_GRAVEL);
    public static final Item RED_SAND = registerBlock(Blocks.RED_SAND);
    public static final Item GRAVEL = registerBlock(Blocks.GRAVEL);
    public static final Item COAL_ORE = registerBlock(Blocks.COAL_ORE);
    public static final Item DEEPSLATE_COAL_ORE = registerBlock(Blocks.DEEPSLATE_COAL_ORE);
    public static final Item IRON_ORE = registerBlock(Blocks.IRON_ORE);
    public static final Item DEEPSLATE_IRON_ORE = registerBlock(Blocks.DEEPSLATE_IRON_ORE);
    public static final Item COPPER_ORE = registerBlock(Blocks.COPPER_ORE);
    public static final Item DEEPSLATE_COPPER_ORE = registerBlock(Blocks.DEEPSLATE_COPPER_ORE);
    public static final Item GOLD_ORE = registerBlock(Blocks.GOLD_ORE);
    public static final Item DEEPSLATE_GOLD_ORE = registerBlock(Blocks.DEEPSLATE_GOLD_ORE);
    public static final Item REDSTONE_ORE = registerBlock(Blocks.REDSTONE_ORE);
    public static final Item DEEPSLATE_REDSTONE_ORE = registerBlock(Blocks.DEEPSLATE_REDSTONE_ORE);
    public static final Item EMERALD_ORE = registerBlock(Blocks.EMERALD_ORE);
    public static final Item DEEPSLATE_EMERALD_ORE = registerBlock(Blocks.DEEPSLATE_EMERALD_ORE);
    public static final Item LAPIS_ORE = registerBlock(Blocks.LAPIS_ORE);
    public static final Item DEEPSLATE_LAPIS_ORE = registerBlock(Blocks.DEEPSLATE_LAPIS_ORE);
    public static final Item DIAMOND_ORE = registerBlock(Blocks.DIAMOND_ORE);
    public static final Item DEEPSLATE_DIAMOND_ORE = registerBlock(Blocks.DEEPSLATE_DIAMOND_ORE);
    public static final Item NETHER_GOLD_ORE = registerBlock(Blocks.NETHER_GOLD_ORE);
    public static final Item NETHER_QUARTZ_ORE = registerBlock(Blocks.NETHER_QUARTZ_ORE);
    public static final Item ANCIENT_DEBRIS = registerBlock(Blocks.ANCIENT_DEBRIS, new Item.Properties().fireResistant());
    public static final Item COAL_BLOCK = registerBlock(Blocks.COAL_BLOCK);
    public static final Item RAW_IRON_BLOCK = registerBlock(Blocks.RAW_IRON_BLOCK);
    public static final Item RAW_COPPER_BLOCK = registerBlock(Blocks.RAW_COPPER_BLOCK);
    public static final Item RAW_GOLD_BLOCK = registerBlock(Blocks.RAW_GOLD_BLOCK);
    public static final Item HEAVY_CORE = registerBlock(Blocks.HEAVY_CORE, new Item.Properties().rarity(Rarity.EPIC));
    public static final Item AMETHYST_BLOCK = registerBlock(Blocks.AMETHYST_BLOCK);
    public static final Item BUDDING_AMETHYST = registerBlock(Blocks.BUDDING_AMETHYST);
    public static final Item IRON_BLOCK = registerBlock(Blocks.IRON_BLOCK);
    public static final Item COPPER_BLOCK = registerBlock(Blocks.COPPER_BLOCK);
    public static final Item GOLD_BLOCK = registerBlock(Blocks.GOLD_BLOCK);
    public static final Item DIAMOND_BLOCK = registerBlock(Blocks.DIAMOND_BLOCK);
    public static final Item NETHERITE_BLOCK = registerBlock(Blocks.NETHERITE_BLOCK, new Item.Properties().fireResistant());
    public static final Item EXPOSED_COPPER = registerBlock(Blocks.EXPOSED_COPPER);
    public static final Item WEATHERED_COPPER = registerBlock(Blocks.WEATHERED_COPPER);
    public static final Item OXIDIZED_COPPER = registerBlock(Blocks.OXIDIZED_COPPER);
    public static final Item CHISELED_COPPER = registerBlock(Blocks.CHISELED_COPPER);
    public static final Item EXPOSED_CHISELED_COPPER = registerBlock(Blocks.EXPOSED_CHISELED_COPPER);
    public static final Item WEATHERED_CHISELED_COPPER = registerBlock(Blocks.WEATHERED_CHISELED_COPPER);
    public static final Item OXIDIZED_CHISELED_COPPER = registerBlock(Blocks.OXIDIZED_CHISELED_COPPER);
    public static final Item CUT_COPPER = registerBlock(Blocks.CUT_COPPER);
    public static final Item EXPOSED_CUT_COPPER = registerBlock(Blocks.EXPOSED_CUT_COPPER);
    public static final Item WEATHERED_CUT_COPPER = registerBlock(Blocks.WEATHERED_CUT_COPPER);
    public static final Item OXIDIZED_CUT_COPPER = registerBlock(Blocks.OXIDIZED_CUT_COPPER);
    public static final Item CUT_COPPER_STAIRS = registerBlock(Blocks.CUT_COPPER_STAIRS);
    public static final Item EXPOSED_CUT_COPPER_STAIRS = registerBlock(Blocks.EXPOSED_CUT_COPPER_STAIRS);
    public static final Item WEATHERED_CUT_COPPER_STAIRS = registerBlock(Blocks.WEATHERED_CUT_COPPER_STAIRS);
    public static final Item OXIDIZED_CUT_COPPER_STAIRS = registerBlock(Blocks.OXIDIZED_CUT_COPPER_STAIRS);
    public static final Item CUT_COPPER_SLAB = registerBlock(Blocks.CUT_COPPER_SLAB);
    public static final Item EXPOSED_CUT_COPPER_SLAB = registerBlock(Blocks.EXPOSED_CUT_COPPER_SLAB);
    public static final Item WEATHERED_CUT_COPPER_SLAB = registerBlock(Blocks.WEATHERED_CUT_COPPER_SLAB);
    public static final Item OXIDIZED_CUT_COPPER_SLAB = registerBlock(Blocks.OXIDIZED_CUT_COPPER_SLAB);
    public static final Item WAXED_COPPER_BLOCK = registerBlock(Blocks.WAXED_COPPER_BLOCK);
    public static final Item WAXED_EXPOSED_COPPER = registerBlock(Blocks.WAXED_EXPOSED_COPPER);
    public static final Item WAXED_WEATHERED_COPPER = registerBlock(Blocks.WAXED_WEATHERED_COPPER);
    public static final Item WAXED_OXIDIZED_COPPER = registerBlock(Blocks.WAXED_OXIDIZED_COPPER);
    public static final Item WAXED_CHISELED_COPPER = registerBlock(Blocks.WAXED_CHISELED_COPPER);
    public static final Item WAXED_EXPOSED_CHISELED_COPPER = registerBlock(Blocks.WAXED_EXPOSED_CHISELED_COPPER);
    public static final Item WAXED_WEATHERED_CHISELED_COPPER = registerBlock(Blocks.WAXED_WEATHERED_CHISELED_COPPER);
    public static final Item WAXED_OXIDIZED_CHISELED_COPPER = registerBlock(Blocks.WAXED_OXIDIZED_CHISELED_COPPER);
    public static final Item WAXED_CUT_COPPER = registerBlock(Blocks.WAXED_CUT_COPPER);
    public static final Item WAXED_EXPOSED_CUT_COPPER = registerBlock(Blocks.WAXED_EXPOSED_CUT_COPPER);
    public static final Item WAXED_WEATHERED_CUT_COPPER = registerBlock(Blocks.WAXED_WEATHERED_CUT_COPPER);
    public static final Item WAXED_OXIDIZED_CUT_COPPER = registerBlock(Blocks.WAXED_OXIDIZED_CUT_COPPER);
    public static final Item WAXED_CUT_COPPER_STAIRS = registerBlock(Blocks.WAXED_CUT_COPPER_STAIRS);
    public static final Item WAXED_EXPOSED_CUT_COPPER_STAIRS = registerBlock(Blocks.WAXED_EXPOSED_CUT_COPPER_STAIRS);
    public static final Item WAXED_WEATHERED_CUT_COPPER_STAIRS = registerBlock(Blocks.WAXED_WEATHERED_CUT_COPPER_STAIRS);
    public static final Item WAXED_OXIDIZED_CUT_COPPER_STAIRS = registerBlock(Blocks.WAXED_OXIDIZED_CUT_COPPER_STAIRS);
    public static final Item WAXED_CUT_COPPER_SLAB = registerBlock(Blocks.WAXED_CUT_COPPER_SLAB);
    public static final Item WAXED_EXPOSED_CUT_COPPER_SLAB = registerBlock(Blocks.WAXED_EXPOSED_CUT_COPPER_SLAB);
    public static final Item WAXED_WEATHERED_CUT_COPPER_SLAB = registerBlock(Blocks.WAXED_WEATHERED_CUT_COPPER_SLAB);
    public static final Item WAXED_OXIDIZED_CUT_COPPER_SLAB = registerBlock(Blocks.WAXED_OXIDIZED_CUT_COPPER_SLAB);
    public static final Item OAK_LOG = registerBlock(Blocks.OAK_LOG);
    public static final Item SPRUCE_LOG = registerBlock(Blocks.SPRUCE_LOG);
    public static final Item BIRCH_LOG = registerBlock(Blocks.BIRCH_LOG);
    public static final Item JUNGLE_LOG = registerBlock(Blocks.JUNGLE_LOG);
    public static final Item ACACIA_LOG = registerBlock(Blocks.ACACIA_LOG);
    public static final Item CHERRY_LOG = registerBlock(Blocks.CHERRY_LOG);
    public static final Item PALE_OAK_LOG = registerBlock(Blocks.PALE_OAK_LOG);
    public static final Item DARK_OAK_LOG = registerBlock(Blocks.DARK_OAK_LOG);
    public static final Item MANGROVE_LOG = registerBlock(Blocks.MANGROVE_LOG);
    public static final Item MANGROVE_ROOTS = registerBlock(Blocks.MANGROVE_ROOTS);
    public static final Item MUDDY_MANGROVE_ROOTS = registerBlock(Blocks.MUDDY_MANGROVE_ROOTS);
    public static final Item CRIMSON_STEM = registerBlock(Blocks.CRIMSON_STEM);
    public static final Item WARPED_STEM = registerBlock(Blocks.WARPED_STEM);
    public static final Item BAMBOO_BLOCK = registerBlock(Blocks.BAMBOO_BLOCK);
    public static final Item STRIPPED_OAK_LOG = registerBlock(Blocks.STRIPPED_OAK_LOG);
    public static final Item STRIPPED_SPRUCE_LOG = registerBlock(Blocks.STRIPPED_SPRUCE_LOG);
    public static final Item STRIPPED_BIRCH_LOG = registerBlock(Blocks.STRIPPED_BIRCH_LOG);
    public static final Item STRIPPED_JUNGLE_LOG = registerBlock(Blocks.STRIPPED_JUNGLE_LOG);
    public static final Item STRIPPED_ACACIA_LOG = registerBlock(Blocks.STRIPPED_ACACIA_LOG);
    public static final Item STRIPPED_CHERRY_LOG = registerBlock(Blocks.STRIPPED_CHERRY_LOG);
    public static final Item STRIPPED_DARK_OAK_LOG = registerBlock(Blocks.STRIPPED_DARK_OAK_LOG);
    public static final Item STRIPPED_PALE_OAK_LOG = registerBlock(Blocks.STRIPPED_PALE_OAK_LOG);
    public static final Item STRIPPED_MANGROVE_LOG = registerBlock(Blocks.STRIPPED_MANGROVE_LOG);
    public static final Item STRIPPED_CRIMSON_STEM = registerBlock(Blocks.STRIPPED_CRIMSON_STEM);
    public static final Item STRIPPED_WARPED_STEM = registerBlock(Blocks.STRIPPED_WARPED_STEM);
    public static final Item STRIPPED_OAK_WOOD = registerBlock(Blocks.STRIPPED_OAK_WOOD);
    public static final Item STRIPPED_SPRUCE_WOOD = registerBlock(Blocks.STRIPPED_SPRUCE_WOOD);
    public static final Item STRIPPED_BIRCH_WOOD = registerBlock(Blocks.STRIPPED_BIRCH_WOOD);
    public static final Item STRIPPED_JUNGLE_WOOD = registerBlock(Blocks.STRIPPED_JUNGLE_WOOD);
    public static final Item STRIPPED_ACACIA_WOOD = registerBlock(Blocks.STRIPPED_ACACIA_WOOD);
    public static final Item STRIPPED_CHERRY_WOOD = registerBlock(Blocks.STRIPPED_CHERRY_WOOD);
    public static final Item STRIPPED_DARK_OAK_WOOD = registerBlock(Blocks.STRIPPED_DARK_OAK_WOOD);
    public static final Item STRIPPED_PALE_OAK_WOOD = registerBlock(Blocks.STRIPPED_PALE_OAK_WOOD);
    public static final Item STRIPPED_MANGROVE_WOOD = registerBlock(Blocks.STRIPPED_MANGROVE_WOOD);
    public static final Item STRIPPED_CRIMSON_HYPHAE = registerBlock(Blocks.STRIPPED_CRIMSON_HYPHAE);
    public static final Item STRIPPED_WARPED_HYPHAE = registerBlock(Blocks.STRIPPED_WARPED_HYPHAE);
    public static final Item STRIPPED_BAMBOO_BLOCK = registerBlock(Blocks.STRIPPED_BAMBOO_BLOCK);
    public static final Item OAK_WOOD = registerBlock(Blocks.OAK_WOOD);
    public static final Item SPRUCE_WOOD = registerBlock(Blocks.SPRUCE_WOOD);
    public static final Item BIRCH_WOOD = registerBlock(Blocks.BIRCH_WOOD);
    public static final Item JUNGLE_WOOD = registerBlock(Blocks.JUNGLE_WOOD);
    public static final Item ACACIA_WOOD = registerBlock(Blocks.ACACIA_WOOD);
    public static final Item CHERRY_WOOD = registerBlock(Blocks.CHERRY_WOOD);
    public static final Item PALE_OAK_WOOD = registerBlock(Blocks.PALE_OAK_WOOD);
    public static final Item DARK_OAK_WOOD = registerBlock(Blocks.DARK_OAK_WOOD);
    public static final Item MANGROVE_WOOD = registerBlock(Blocks.MANGROVE_WOOD);
    public static final Item CRIMSON_HYPHAE = registerBlock(Blocks.CRIMSON_HYPHAE);
    public static final Item WARPED_HYPHAE = registerBlock(Blocks.WARPED_HYPHAE);
    public static final Item OAK_LEAVES = registerBlock(Blocks.OAK_LEAVES);
    public static final Item SPRUCE_LEAVES = registerBlock(Blocks.SPRUCE_LEAVES);
    public static final Item BIRCH_LEAVES = registerBlock(Blocks.BIRCH_LEAVES);
    public static final Item JUNGLE_LEAVES = registerBlock(Blocks.JUNGLE_LEAVES);
    public static final Item ACACIA_LEAVES = registerBlock(Blocks.ACACIA_LEAVES);
    public static final Item CHERRY_LEAVES = registerBlock(Blocks.CHERRY_LEAVES);
    public static final Item DARK_OAK_LEAVES = registerBlock(Blocks.DARK_OAK_LEAVES);
    public static final Item PALE_OAK_LEAVES = registerBlock(Blocks.PALE_OAK_LEAVES);
    public static final Item MANGROVE_LEAVES = registerBlock(Blocks.MANGROVE_LEAVES);
    public static final Item AZALEA_LEAVES = registerBlock(Blocks.AZALEA_LEAVES);
    public static final Item FLOWERING_AZALEA_LEAVES = registerBlock(Blocks.FLOWERING_AZALEA_LEAVES);
    public static final Item SPONGE = registerBlock(Blocks.SPONGE);
    public static final Item WET_SPONGE = registerBlock(Blocks.WET_SPONGE);
    public static final Item GLASS = registerBlock(Blocks.GLASS);
    public static final Item TINTED_GLASS = registerBlock(Blocks.TINTED_GLASS);
    public static final Item LAPIS_BLOCK = registerBlock(Blocks.LAPIS_BLOCK);
    public static final Item SANDSTONE = registerBlock(Blocks.SANDSTONE);
    public static final Item CHISELED_SANDSTONE = registerBlock(Blocks.CHISELED_SANDSTONE);
    public static final Item CUT_SANDSTONE = registerBlock(Blocks.CUT_SANDSTONE);
    public static final Item COBWEB = registerBlock(Blocks.COBWEB);
    public static final Item SHORT_GRASS = registerBlock(Blocks.SHORT_GRASS);
    public static final Item FERN = registerBlock(Blocks.FERN);
    public static final Item AZALEA = registerBlock(Blocks.AZALEA);
    public static final Item FLOWERING_AZALEA = registerBlock(Blocks.FLOWERING_AZALEA);
    public static final Item DEAD_BUSH = registerBlock(Blocks.DEAD_BUSH);
    public static final Item SEAGRASS = registerBlock(Blocks.SEAGRASS);
    public static final Item SEA_PICKLE = registerBlock(Blocks.SEA_PICKLE);
    public static final Item WHITE_WOOL = registerBlock(Blocks.WHITE_WOOL);
    public static final Item ORANGE_WOOL = registerBlock(Blocks.ORANGE_WOOL);
    public static final Item MAGENTA_WOOL = registerBlock(Blocks.MAGENTA_WOOL);
    public static final Item LIGHT_BLUE_WOOL = registerBlock(Blocks.LIGHT_BLUE_WOOL);
    public static final Item YELLOW_WOOL = registerBlock(Blocks.YELLOW_WOOL);
    public static final Item LIME_WOOL = registerBlock(Blocks.LIME_WOOL);
    public static final Item PINK_WOOL = registerBlock(Blocks.PINK_WOOL);
    public static final Item GRAY_WOOL = registerBlock(Blocks.GRAY_WOOL);
    public static final Item LIGHT_GRAY_WOOL = registerBlock(Blocks.LIGHT_GRAY_WOOL);
    public static final Item CYAN_WOOL = registerBlock(Blocks.CYAN_WOOL);
    public static final Item PURPLE_WOOL = registerBlock(Blocks.PURPLE_WOOL);
    public static final Item BLUE_WOOL = registerBlock(Blocks.BLUE_WOOL);
    public static final Item BROWN_WOOL = registerBlock(Blocks.BROWN_WOOL);
    public static final Item GREEN_WOOL = registerBlock(Blocks.GREEN_WOOL);
    public static final Item RED_WOOL = registerBlock(Blocks.RED_WOOL);
    public static final Item BLACK_WOOL = registerBlock(Blocks.BLACK_WOOL);
    public static final Item DANDELION = registerBlock(Blocks.DANDELION);
    public static final Item OPEN_EYEBLOSSOM = registerBlock(Blocks.OPEN_EYEBLOSSOM);
    public static final Item CLOSED_EYEBLOSSOM = registerBlock(Blocks.CLOSED_EYEBLOSSOM);
    public static final Item POPPY = registerBlock(Blocks.POPPY);
    public static final Item BLUE_ORCHID = registerBlock(Blocks.BLUE_ORCHID);
    public static final Item ALLIUM = registerBlock(Blocks.ALLIUM);
    public static final Item AZURE_BLUET = registerBlock(Blocks.AZURE_BLUET);
    public static final Item RED_TULIP = registerBlock(Blocks.RED_TULIP);
    public static final Item ORANGE_TULIP = registerBlock(Blocks.ORANGE_TULIP);
    public static final Item WHITE_TULIP = registerBlock(Blocks.WHITE_TULIP);
    public static final Item PINK_TULIP = registerBlock(Blocks.PINK_TULIP);
    public static final Item OXEYE_DAISY = registerBlock(Blocks.OXEYE_DAISY);
    public static final Item CORNFLOWER = registerBlock(Blocks.CORNFLOWER);
    public static final Item LILY_OF_THE_VALLEY = registerBlock(Blocks.LILY_OF_THE_VALLEY);
    public static final Item WITHER_ROSE = registerBlock(Blocks.WITHER_ROSE);
    public static final Item TORCHFLOWER = registerBlock(Blocks.TORCHFLOWER);
    public static final Item PITCHER_PLANT = registerBlock(Blocks.PITCHER_PLANT);
    public static final Item SPORE_BLOSSOM = registerBlock(Blocks.SPORE_BLOSSOM);
    public static final Item BROWN_MUSHROOM = registerBlock(Blocks.BROWN_MUSHROOM);
    public static final Item RED_MUSHROOM = registerBlock(Blocks.RED_MUSHROOM);
    public static final Item CRIMSON_FUNGUS = registerBlock(Blocks.CRIMSON_FUNGUS);
    public static final Item WARPED_FUNGUS = registerBlock(Blocks.WARPED_FUNGUS);
    public static final Item CRIMSON_ROOTS = registerBlock(Blocks.CRIMSON_ROOTS);
    public static final Item WARPED_ROOTS = registerBlock(Blocks.WARPED_ROOTS);
    public static final Item NETHER_SPROUTS = registerBlock(Blocks.NETHER_SPROUTS);
    public static final Item WEEPING_VINES = registerBlock(Blocks.WEEPING_VINES);
    public static final Item TWISTING_VINES = registerBlock(Blocks.TWISTING_VINES);
    public static final Item SUGAR_CANE = registerBlock(Blocks.SUGAR_CANE);
    public static final Item KELP = registerBlock(Blocks.KELP);
    public static final Item PINK_PETALS = registerBlock(Blocks.PINK_PETALS);
    public static final Item MOSS_CARPET = registerBlock(Blocks.MOSS_CARPET);
    public static final Item MOSS_BLOCK = registerBlock(Blocks.MOSS_BLOCK);
    public static final Item PALE_MOSS_CARPET = registerBlock(Blocks.PALE_MOSS_CARPET);
    public static final Item PALE_HANGING_MOSS = registerBlock(Blocks.PALE_HANGING_MOSS);
    public static final Item PALE_MOSS_BLOCK = registerBlock(Blocks.PALE_MOSS_BLOCK);
    public static final Item HANGING_ROOTS = registerBlock(Blocks.HANGING_ROOTS);
    public static final Item BIG_DRIPLEAF = registerBlock(Blocks.BIG_DRIPLEAF, Blocks.BIG_DRIPLEAF_STEM);
    public static final Item SMALL_DRIPLEAF = registerBlock(Blocks.SMALL_DRIPLEAF, DoubleHighBlockItem::new);
    public static final Item BAMBOO = registerBlock(Blocks.BAMBOO);
    public static final Item OAK_SLAB = registerBlock(Blocks.OAK_SLAB);
    public static final Item SPRUCE_SLAB = registerBlock(Blocks.SPRUCE_SLAB);
    public static final Item BIRCH_SLAB = registerBlock(Blocks.BIRCH_SLAB);
    public static final Item JUNGLE_SLAB = registerBlock(Blocks.JUNGLE_SLAB);
    public static final Item ACACIA_SLAB = registerBlock(Blocks.ACACIA_SLAB);
    public static final Item CHERRY_SLAB = registerBlock(Blocks.CHERRY_SLAB);
    public static final Item DARK_OAK_SLAB = registerBlock(Blocks.DARK_OAK_SLAB);
    public static final Item PALE_OAK_SLAB = registerBlock(Blocks.PALE_OAK_SLAB);
    public static final Item MANGROVE_SLAB = registerBlock(Blocks.MANGROVE_SLAB);
    public static final Item BAMBOO_SLAB = registerBlock(Blocks.BAMBOO_SLAB);
    public static final Item BAMBOO_MOSAIC_SLAB = registerBlock(Blocks.BAMBOO_MOSAIC_SLAB);
    public static final Item CRIMSON_SLAB = registerBlock(Blocks.CRIMSON_SLAB);
    public static final Item WARPED_SLAB = registerBlock(Blocks.WARPED_SLAB);
    public static final Item STONE_SLAB = registerBlock(Blocks.STONE_SLAB);
    public static final Item SMOOTH_STONE_SLAB = registerBlock(Blocks.SMOOTH_STONE_SLAB);
    public static final Item SANDSTONE_SLAB = registerBlock(Blocks.SANDSTONE_SLAB);
    public static final Item CUT_STANDSTONE_SLAB = registerBlock(Blocks.CUT_SANDSTONE_SLAB);
    public static final Item PETRIFIED_OAK_SLAB = registerBlock(Blocks.PETRIFIED_OAK_SLAB);
    public static final Item COBBLESTONE_SLAB = registerBlock(Blocks.COBBLESTONE_SLAB);
    public static final Item BRICK_SLAB = registerBlock(Blocks.BRICK_SLAB);
    public static final Item STONE_BRICK_SLAB = registerBlock(Blocks.STONE_BRICK_SLAB);
    public static final Item MUD_BRICK_SLAB = registerBlock(Blocks.MUD_BRICK_SLAB);
    public static final Item NETHER_BRICK_SLAB = registerBlock(Blocks.NETHER_BRICK_SLAB);
    public static final Item QUARTZ_SLAB = registerBlock(Blocks.QUARTZ_SLAB);
    public static final Item RED_SANDSTONE_SLAB = registerBlock(Blocks.RED_SANDSTONE_SLAB);
    public static final Item CUT_RED_SANDSTONE_SLAB = registerBlock(Blocks.CUT_RED_SANDSTONE_SLAB);
    public static final Item PURPUR_SLAB = registerBlock(Blocks.PURPUR_SLAB);
    public static final Item PRISMARINE_SLAB = registerBlock(Blocks.PRISMARINE_SLAB);
    public static final Item PRISMARINE_BRICK_SLAB = registerBlock(Blocks.PRISMARINE_BRICK_SLAB);
    public static final Item DARK_PRISMARINE_SLAB = registerBlock(Blocks.DARK_PRISMARINE_SLAB);
    public static final Item SMOOTH_QUARTZ = registerBlock(Blocks.SMOOTH_QUARTZ);
    public static final Item SMOOTH_RED_SANDSTONE = registerBlock(Blocks.SMOOTH_RED_SANDSTONE);
    public static final Item SMOOTH_SANDSTONE = registerBlock(Blocks.SMOOTH_SANDSTONE);
    public static final Item SMOOTH_STONE = registerBlock(Blocks.SMOOTH_STONE);
    public static final Item BRICKS = registerBlock(Blocks.BRICKS);
    public static final Item BOOKSHELF = registerBlock(Blocks.BOOKSHELF);
    public static final Item CHISELED_BOOKSHELF = registerBlock(
        Blocks.CHISELED_BOOKSHELF, p_332742_ -> p_332742_.component(DataComponents.CONTAINER, ItemContainerContents.EMPTY)
    );
    public static final Item DECORATED_POT = registerBlock(
        Blocks.DECORATED_POT,
        new Item.Properties().component(DataComponents.POT_DECORATIONS, PotDecorations.EMPTY).component(DataComponents.CONTAINER, ItemContainerContents.EMPTY)
    );
    public static final Item MOSSY_COBBLESTONE = registerBlock(Blocks.MOSSY_COBBLESTONE);
    public static final Item OBSIDIAN = registerBlock(Blocks.OBSIDIAN);
    public static final Item TORCH = registerBlock(
        Blocks.TORCH, (p_370971_, p_370972_) -> new StandingAndWallBlockItem(p_370971_, Blocks.WALL_TORCH, Direction.DOWN, p_370972_)
    );
    public static final Item END_ROD = registerBlock(Blocks.END_ROD);
    public static final Item CHORUS_PLANT = registerBlock(Blocks.CHORUS_PLANT);
    public static final Item CHORUS_FLOWER = registerBlock(Blocks.CHORUS_FLOWER);
    public static final Item PURPUR_BLOCK = registerBlock(Blocks.PURPUR_BLOCK);
    public static final Item PURPUR_PILLAR = registerBlock(Blocks.PURPUR_PILLAR);
    public static final Item PURPUR_STAIRS = registerBlock(Blocks.PURPUR_STAIRS);
    public static final Item SPAWNER = registerBlock(Blocks.SPAWNER);
    public static final Item CREAKING_HEART = registerBlock(Blocks.CREAKING_HEART);
    public static final Item CHEST = registerBlock(Blocks.CHEST, p_332711_ -> p_332711_.component(DataComponents.CONTAINER, ItemContainerContents.EMPTY));
    public static final Item CRAFTING_TABLE = registerBlock(Blocks.CRAFTING_TABLE);
    public static final Item FARMLAND = registerBlock(Blocks.FARMLAND);
    public static final Item FURNACE = registerBlock(Blocks.FURNACE, p_332745_ -> p_332745_.component(DataComponents.CONTAINER, ItemContainerContents.EMPTY));
    public static final Item LADDER = registerBlock(Blocks.LADDER);
    public static final Item COBBLESTONE_STAIRS = registerBlock(Blocks.COBBLESTONE_STAIRS);
    public static final Item SNOW = registerBlock(Blocks.SNOW);
    public static final Item ICE = registerBlock(Blocks.ICE);
    public static final Item SNOW_BLOCK = registerBlock(Blocks.SNOW_BLOCK);
    public static final Item CACTUS = registerBlock(Blocks.CACTUS);
    public static final Item CLAY = registerBlock(Blocks.CLAY);
    public static final Item JUKEBOX = registerBlock(Blocks.JUKEBOX);
    public static final Item OAK_FENCE = registerBlock(Blocks.OAK_FENCE);
    public static final Item SPRUCE_FENCE = registerBlock(Blocks.SPRUCE_FENCE);
    public static final Item BIRCH_FENCE = registerBlock(Blocks.BIRCH_FENCE);
    public static final Item JUNGLE_FENCE = registerBlock(Blocks.JUNGLE_FENCE);
    public static final Item ACACIA_FENCE = registerBlock(Blocks.ACACIA_FENCE);
    public static final Item CHERRY_FENCE = registerBlock(Blocks.CHERRY_FENCE);
    public static final Item DARK_OAK_FENCE = registerBlock(Blocks.DARK_OAK_FENCE);
    public static final Item PALE_OAK_FENCE = registerBlock(Blocks.PALE_OAK_FENCE);
    public static final Item MANGROVE_FENCE = registerBlock(Blocks.MANGROVE_FENCE);
    public static final Item BAMBOO_FENCE = registerBlock(Blocks.BAMBOO_FENCE);
    public static final Item CRIMSON_FENCE = registerBlock(Blocks.CRIMSON_FENCE);
    public static final Item WARPED_FENCE = registerBlock(Blocks.WARPED_FENCE);
    public static final Item PUMPKIN = registerBlock(Blocks.PUMPKIN);
    public static final Item CARVED_PUMPKIN = registerBlock(
        Blocks.CARVED_PUMPKIN,
        p_380836_ -> p_380836_.component(
                DataComponents.EQUIPPABLE,
                Equippable.builder(EquipmentSlot.HEAD).setSwappable(false).setCameraOverlay(ResourceLocation.withDefaultNamespace("misc/pumpkinblur")).build()
            )
    );
    public static final Item JACK_O_LANTERN = registerBlock(Blocks.JACK_O_LANTERN);
    public static final Item NETHERRACK = registerBlock(Blocks.NETHERRACK);
    public static final Item SOUL_SAND = registerBlock(Blocks.SOUL_SAND);
    public static final Item SOUL_SOIL = registerBlock(Blocks.SOUL_SOIL);
    public static final Item BASALT = registerBlock(Blocks.BASALT);
    public static final Item POLISHED_BASALT = registerBlock(Blocks.POLISHED_BASALT);
    public static final Item SMOOTH_BASALT = registerBlock(Blocks.SMOOTH_BASALT);
    public static final Item SOUL_TORCH = registerBlock(
        Blocks.SOUL_TORCH, (p_370841_, p_370842_) -> new StandingAndWallBlockItem(p_370841_, Blocks.SOUL_WALL_TORCH, Direction.DOWN, p_370842_)
    );
    public static final Item GLOWSTONE = registerBlock(Blocks.GLOWSTONE);
    public static final Item INFESTED_STONE = registerBlock(Blocks.INFESTED_STONE);
    public static final Item INFESTED_COBBLESTONE = registerBlock(Blocks.INFESTED_COBBLESTONE);
    public static final Item INFESTED_STONE_BRICKS = registerBlock(Blocks.INFESTED_STONE_BRICKS);
    public static final Item INFESTED_MOSSY_STONE_BRICKS = registerBlock(Blocks.INFESTED_MOSSY_STONE_BRICKS);
    public static final Item INFESTED_CRACKED_STONE_BRICKS = registerBlock(Blocks.INFESTED_CRACKED_STONE_BRICKS);
    public static final Item INFESTED_CHISELED_STONE_BRICKS = registerBlock(Blocks.INFESTED_CHISELED_STONE_BRICKS);
    public static final Item INFESTED_DEEPSLATE = registerBlock(Blocks.INFESTED_DEEPSLATE);
    public static final Item STONE_BRICKS = registerBlock(Blocks.STONE_BRICKS);
    public static final Item MOSSY_STONE_BRICKS = registerBlock(Blocks.MOSSY_STONE_BRICKS);
    public static final Item CRACKED_STONE_BRICKS = registerBlock(Blocks.CRACKED_STONE_BRICKS);
    public static final Item CHISELED_STONE_BRICKS = registerBlock(Blocks.CHISELED_STONE_BRICKS);
    public static final Item PACKED_MUD = registerBlock(Blocks.PACKED_MUD);
    public static final Item MUD_BRICKS = registerBlock(Blocks.MUD_BRICKS);
    public static final Item DEEPSLATE_BRICKS = registerBlock(Blocks.DEEPSLATE_BRICKS);
    public static final Item CRACKED_DEEPSLATE_BRICKS = registerBlock(Blocks.CRACKED_DEEPSLATE_BRICKS);
    public static final Item DEEPSLATE_TILES = registerBlock(Blocks.DEEPSLATE_TILES);
    public static final Item CRACKED_DEEPSLATE_TILES = registerBlock(Blocks.CRACKED_DEEPSLATE_TILES);
    public static final Item CHISELED_DEEPSLATE = registerBlock(Blocks.CHISELED_DEEPSLATE);
    public static final Item REINFORCED_DEEPSLATE = registerBlock(Blocks.REINFORCED_DEEPSLATE);
    public static final Item BROWN_MUSHROOM_BLOCK = registerBlock(Blocks.BROWN_MUSHROOM_BLOCK);
    public static final Item RED_MUSHROOM_BLOCK = registerBlock(Blocks.RED_MUSHROOM_BLOCK);
    public static final Item MUSHROOM_STEM = registerBlock(Blocks.MUSHROOM_STEM);
    public static final Item IRON_BARS = registerBlock(Blocks.IRON_BARS);
    public static final Item CHAIN = registerBlock(Blocks.CHAIN);
    public static final Item GLASS_PANE = registerBlock(Blocks.GLASS_PANE);
    public static final Item MELON = registerBlock(Blocks.MELON);
    public static final Item VINE = registerBlock(Blocks.VINE);
    public static final Item GLOW_LICHEN = registerBlock(Blocks.GLOW_LICHEN);
    public static final Item RESIN_CLUMP = registerItem("resin_clump", createBlockItemWithCustomItemName(Blocks.RESIN_CLUMP));
    public static final Item RESIN_BLOCK = registerBlock(Blocks.RESIN_BLOCK);
    public static final Item RESIN_BRICKS = registerBlock(Blocks.RESIN_BRICKS);
    public static final Item RESIN_BRICK_STAIRS = registerBlock(Blocks.RESIN_BRICK_STAIRS);
    public static final Item RESIN_BRICK_SLAB = registerBlock(Blocks.RESIN_BRICK_SLAB);
    public static final Item RESIN_BRICK_WALL = registerBlock(Blocks.RESIN_BRICK_WALL);
    public static final Item CHISELED_RESIN_BRICKS = registerBlock(Blocks.CHISELED_RESIN_BRICKS);
    public static final Item BRICK_STAIRS = registerBlock(Blocks.BRICK_STAIRS);
    public static final Item STONE_BRICK_STAIRS = registerBlock(Blocks.STONE_BRICK_STAIRS);
    public static final Item MUD_BRICK_STAIRS = registerBlock(Blocks.MUD_BRICK_STAIRS);
    public static final Item MYCELIUM = registerBlock(Blocks.MYCELIUM);
    public static final Item LILY_PAD = registerBlock(Blocks.LILY_PAD, PlaceOnWaterBlockItem::new);
    public static final Item NETHER_BRICKS = registerBlock(Blocks.NETHER_BRICKS);
    public static final Item CRACKED_NETHER_BRICKS = registerBlock(Blocks.CRACKED_NETHER_BRICKS);
    public static final Item CHISELED_NETHER_BRICKS = registerBlock(Blocks.CHISELED_NETHER_BRICKS);
    public static final Item NETHER_BRICK_FENCE = registerBlock(Blocks.NETHER_BRICK_FENCE);
    public static final Item NETHER_BRICK_STAIRS = registerBlock(Blocks.NETHER_BRICK_STAIRS);
    public static final Item SCULK = registerBlock(Blocks.SCULK);
    public static final Item SCULK_VEIN = registerBlock(Blocks.SCULK_VEIN);
    public static final Item SCULK_CATALYST = registerBlock(Blocks.SCULK_CATALYST);
    public static final Item SCULK_SHRIEKER = registerBlock(Blocks.SCULK_SHRIEKER);
    public static final Item ENCHANTING_TABLE = registerBlock(Blocks.ENCHANTING_TABLE);
    public static final Item END_PORTAL_FRAME = registerBlock(Blocks.END_PORTAL_FRAME);
    public static final Item END_STONE = registerBlock(Blocks.END_STONE);
    public static final Item END_STONE_BRICKS = registerBlock(Blocks.END_STONE_BRICKS);
    public static final Item DRAGON_EGG = registerBlock(Blocks.DRAGON_EGG, new Item.Properties().rarity(Rarity.EPIC));
    public static final Item SANDSTONE_STAIRS = registerBlock(Blocks.SANDSTONE_STAIRS);
    public static final Item ENDER_CHEST = registerBlock(Blocks.ENDER_CHEST);
    public static final Item EMERALD_BLOCK = registerBlock(Blocks.EMERALD_BLOCK);
    public static final Item OAK_STAIRS = registerBlock(Blocks.OAK_STAIRS);
    public static final Item SPRUCE_STAIRS = registerBlock(Blocks.SPRUCE_STAIRS);
    public static final Item BIRCH_STAIRS = registerBlock(Blocks.BIRCH_STAIRS);
    public static final Item JUNGLE_STAIRS = registerBlock(Blocks.JUNGLE_STAIRS);
    public static final Item ACACIA_STAIRS = registerBlock(Blocks.ACACIA_STAIRS);
    public static final Item CHERRY_STAIRS = registerBlock(Blocks.CHERRY_STAIRS);
    public static final Item DARK_OAK_STAIRS = registerBlock(Blocks.DARK_OAK_STAIRS);
    public static final Item PALE_OAK_STAIRS = registerBlock(Blocks.PALE_OAK_STAIRS);
    public static final Item MANGROVE_STAIRS = registerBlock(Blocks.MANGROVE_STAIRS);
    public static final Item BAMBOO_STAIRS = registerBlock(Blocks.BAMBOO_STAIRS);
    public static final Item BAMBOO_MOSAIC_STAIRS = registerBlock(Blocks.BAMBOO_MOSAIC_STAIRS);
    public static final Item CRIMSON_STAIRS = registerBlock(Blocks.CRIMSON_STAIRS);
    public static final Item WARPED_STAIRS = registerBlock(Blocks.WARPED_STAIRS);
    public static final Item COMMAND_BLOCK = registerBlock(Blocks.COMMAND_BLOCK, GameMasterBlockItem::new, new Item.Properties().rarity(Rarity.EPIC));
    public static final Item BEACON = registerBlock(Blocks.BEACON, new Item.Properties().rarity(Rarity.RARE));
    public static final Item COBBLESTONE_WALL = registerBlock(Blocks.COBBLESTONE_WALL);
    public static final Item MOSSY_COBBLESTONE_WALL = registerBlock(Blocks.MOSSY_COBBLESTONE_WALL);
    public static final Item BRICK_WALL = registerBlock(Blocks.BRICK_WALL);
    public static final Item PRISMARINE_WALL = registerBlock(Blocks.PRISMARINE_WALL);
    public static final Item RED_SANDSTONE_WALL = registerBlock(Blocks.RED_SANDSTONE_WALL);
    public static final Item MOSSY_STONE_BRICK_WALL = registerBlock(Blocks.MOSSY_STONE_BRICK_WALL);
    public static final Item GRANITE_WALL = registerBlock(Blocks.GRANITE_WALL);
    public static final Item STONE_BRICK_WALL = registerBlock(Blocks.STONE_BRICK_WALL);
    public static final Item MUD_BRICK_WALL = registerBlock(Blocks.MUD_BRICK_WALL);
    public static final Item NETHER_BRICK_WALL = registerBlock(Blocks.NETHER_BRICK_WALL);
    public static final Item ANDESITE_WALL = registerBlock(Blocks.ANDESITE_WALL);
    public static final Item RED_NETHER_BRICK_WALL = registerBlock(Blocks.RED_NETHER_BRICK_WALL);
    public static final Item SANDSTONE_WALL = registerBlock(Blocks.SANDSTONE_WALL);
    public static final Item END_STONE_BRICK_WALL = registerBlock(Blocks.END_STONE_BRICK_WALL);
    public static final Item DIORITE_WALL = registerBlock(Blocks.DIORITE_WALL);
    public static final Item BLACKSTONE_WALL = registerBlock(Blocks.BLACKSTONE_WALL);
    public static final Item POLISHED_BLACKSTONE_WALL = registerBlock(Blocks.POLISHED_BLACKSTONE_WALL);
    public static final Item POLISHED_BLACKSTONE_BRICK_WALL = registerBlock(Blocks.POLISHED_BLACKSTONE_BRICK_WALL);
    public static final Item COBBLED_DEEPSLATE_WALL = registerBlock(Blocks.COBBLED_DEEPSLATE_WALL);
    public static final Item POLISHED_DEEPSLATE_WALL = registerBlock(Blocks.POLISHED_DEEPSLATE_WALL);
    public static final Item DEEPSLATE_BRICK_WALL = registerBlock(Blocks.DEEPSLATE_BRICK_WALL);
    public static final Item DEEPSLATE_TILE_WALL = registerBlock(Blocks.DEEPSLATE_TILE_WALL);
    public static final Item ANVIL = registerBlock(Blocks.ANVIL);
    public static final Item CHIPPED_ANVIL = registerBlock(Blocks.CHIPPED_ANVIL);
    public static final Item DAMAGED_ANVIL = registerBlock(Blocks.DAMAGED_ANVIL);
    public static final Item CHISELED_QUARTZ_BLOCK = registerBlock(Blocks.CHISELED_QUARTZ_BLOCK);
    public static final Item QUARTZ_BLOCK = registerBlock(Blocks.QUARTZ_BLOCK);
    public static final Item QUARTZ_BRICKS = registerBlock(Blocks.QUARTZ_BRICKS);
    public static final Item QUARTZ_PILLAR = registerBlock(Blocks.QUARTZ_PILLAR);
    public static final Item QUARTZ_STAIRS = registerBlock(Blocks.QUARTZ_STAIRS);
    public static final Item WHITE_TERRACOTTA = registerBlock(Blocks.WHITE_TERRACOTTA);
    public static final Item ORANGE_TERRACOTTA = registerBlock(Blocks.ORANGE_TERRACOTTA);
    public static final Item MAGENTA_TERRACOTTA = registerBlock(Blocks.MAGENTA_TERRACOTTA);
    public static final Item LIGHT_BLUE_TERRACOTTA = registerBlock(Blocks.LIGHT_BLUE_TERRACOTTA);
    public static final Item YELLOW_TERRACOTTA = registerBlock(Blocks.YELLOW_TERRACOTTA);
    public static final Item LIME_TERRACOTTA = registerBlock(Blocks.LIME_TERRACOTTA);
    public static final Item PINK_TERRACOTTA = registerBlock(Blocks.PINK_TERRACOTTA);
    public static final Item GRAY_TERRACOTTA = registerBlock(Blocks.GRAY_TERRACOTTA);
    public static final Item LIGHT_GRAY_TERRACOTTA = registerBlock(Blocks.LIGHT_GRAY_TERRACOTTA);
    public static final Item CYAN_TERRACOTTA = registerBlock(Blocks.CYAN_TERRACOTTA);
    public static final Item PURPLE_TERRACOTTA = registerBlock(Blocks.PURPLE_TERRACOTTA);
    public static final Item BLUE_TERRACOTTA = registerBlock(Blocks.BLUE_TERRACOTTA);
    public static final Item BROWN_TERRACOTTA = registerBlock(Blocks.BROWN_TERRACOTTA);
    public static final Item GREEN_TERRACOTTA = registerBlock(Blocks.GREEN_TERRACOTTA);
    public static final Item RED_TERRACOTTA = registerBlock(Blocks.RED_TERRACOTTA);
    public static final Item BLACK_TERRACOTTA = registerBlock(Blocks.BLACK_TERRACOTTA);
    public static final Item BARRIER = registerBlock(Blocks.BARRIER, new Item.Properties().rarity(Rarity.EPIC));
    public static final Item LIGHT = registerBlock(
        Blocks.LIGHT,
        p_386406_ -> p_386406_.rarity(Rarity.EPIC).component(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY.with(LightBlock.LEVEL, 15))
    );
    public static final Item HAY_BLOCK = registerBlock(Blocks.HAY_BLOCK);
    public static final Item WHITE_CARPET = registerBlock(
        Blocks.WHITE_CARPET, p_370877_ -> p_370877_.component(DataComponents.EQUIPPABLE, Equippable.llamaSwag(DyeColor.WHITE))
    );
    public static final Item ORANGE_CARPET = registerBlock(
        Blocks.ORANGE_CARPET, p_371076_ -> p_371076_.component(DataComponents.EQUIPPABLE, Equippable.llamaSwag(DyeColor.ORANGE))
    );
    public static final Item MAGENTA_CARPET = registerBlock(
        Blocks.MAGENTA_CARPET, p_370970_ -> p_370970_.component(DataComponents.EQUIPPABLE, Equippable.llamaSwag(DyeColor.MAGENTA))
    );
    public static final Item LIGHT_BLUE_CARPET = registerBlock(
        Blocks.LIGHT_BLUE_CARPET, p_370808_ -> p_370808_.component(DataComponents.EQUIPPABLE, Equippable.llamaSwag(DyeColor.LIGHT_BLUE))
    );
    public static final Item YELLOW_CARPET = registerBlock(
        Blocks.YELLOW_CARPET, p_370791_ -> p_370791_.component(DataComponents.EQUIPPABLE, Equippable.llamaSwag(DyeColor.YELLOW))
    );
    public static final Item LIME_CARPET = registerBlock(
        Blocks.LIME_CARPET, p_371071_ -> p_371071_.component(DataComponents.EQUIPPABLE, Equippable.llamaSwag(DyeColor.LIME))
    );
    public static final Item PINK_CARPET = registerBlock(
        Blocks.PINK_CARPET, p_370957_ -> p_370957_.component(DataComponents.EQUIPPABLE, Equippable.llamaSwag(DyeColor.PINK))
    );
    public static final Item GRAY_CARPET = registerBlock(
        Blocks.GRAY_CARPET, p_370751_ -> p_370751_.component(DataComponents.EQUIPPABLE, Equippable.llamaSwag(DyeColor.GRAY))
    );
    public static final Item LIGHT_GRAY_CARPET = registerBlock(
        Blocks.LIGHT_GRAY_CARPET, p_370913_ -> p_370913_.component(DataComponents.EQUIPPABLE, Equippable.llamaSwag(DyeColor.LIGHT_GRAY))
    );
    public static final Item CYAN_CARPET = registerBlock(
        Blocks.CYAN_CARPET, p_370900_ -> p_370900_.component(DataComponents.EQUIPPABLE, Equippable.llamaSwag(DyeColor.CYAN))
    );
    public static final Item PURPLE_CARPET = registerBlock(
        Blocks.PURPLE_CARPET, p_370992_ -> p_370992_.component(DataComponents.EQUIPPABLE, Equippable.llamaSwag(DyeColor.PURPLE))
    );
    public static final Item BLUE_CARPET = registerBlock(
        Blocks.BLUE_CARPET, p_370781_ -> p_370781_.component(DataComponents.EQUIPPABLE, Equippable.llamaSwag(DyeColor.BLUE))
    );
    public static final Item BROWN_CARPET = registerBlock(
        Blocks.BROWN_CARPET, p_370998_ -> p_370998_.component(DataComponents.EQUIPPABLE, Equippable.llamaSwag(DyeColor.BROWN))
    );
    public static final Item GREEN_CARPET = registerBlock(
        Blocks.GREEN_CARPET, p_370986_ -> p_370986_.component(DataComponents.EQUIPPABLE, Equippable.llamaSwag(DyeColor.GREEN))
    );
    public static final Item RED_CARPET = registerBlock(
        Blocks.RED_CARPET, p_370870_ -> p_370870_.component(DataComponents.EQUIPPABLE, Equippable.llamaSwag(DyeColor.RED))
    );
    public static final Item BLACK_CARPET = registerBlock(
        Blocks.BLACK_CARPET, p_371034_ -> p_371034_.component(DataComponents.EQUIPPABLE, Equippable.llamaSwag(DyeColor.BLACK))
    );
    public static final Item TERRACOTTA = registerBlock(Blocks.TERRACOTTA);
    public static final Item PACKED_ICE = registerBlock(Blocks.PACKED_ICE);
    public static final Item DIRT_PATH = registerBlock(Blocks.DIRT_PATH);
    public static final Item SUNFLOWER = registerBlock(Blocks.SUNFLOWER, DoubleHighBlockItem::new);
    public static final Item LILAC = registerBlock(Blocks.LILAC, DoubleHighBlockItem::new);
    public static final Item ROSE_BUSH = registerBlock(Blocks.ROSE_BUSH, DoubleHighBlockItem::new);
    public static final Item PEONY = registerBlock(Blocks.PEONY, DoubleHighBlockItem::new);
    public static final Item TALL_GRASS = registerBlock(Blocks.TALL_GRASS, DoubleHighBlockItem::new);
    public static final Item LARGE_FERN = registerBlock(Blocks.LARGE_FERN, DoubleHighBlockItem::new);
    public static final Item WHITE_STAINED_GLASS = registerBlock(Blocks.WHITE_STAINED_GLASS);
    public static final Item ORANGE_STAINED_GLASS = registerBlock(Blocks.ORANGE_STAINED_GLASS);
    public static final Item MAGENTA_STAINED_GLASS = registerBlock(Blocks.MAGENTA_STAINED_GLASS);
    public static final Item LIGHT_BLUE_STAINED_GLASS = registerBlock(Blocks.LIGHT_BLUE_STAINED_GLASS);
    public static final Item YELLOW_STAINED_GLASS = registerBlock(Blocks.YELLOW_STAINED_GLASS);
    public static final Item LIME_STAINED_GLASS = registerBlock(Blocks.LIME_STAINED_GLASS);
    public static final Item PINK_STAINED_GLASS = registerBlock(Blocks.PINK_STAINED_GLASS);
    public static final Item GRAY_STAINED_GLASS = registerBlock(Blocks.GRAY_STAINED_GLASS);
    public static final Item LIGHT_GRAY_STAINED_GLASS = registerBlock(Blocks.LIGHT_GRAY_STAINED_GLASS);
    public static final Item CYAN_STAINED_GLASS = registerBlock(Blocks.CYAN_STAINED_GLASS);
    public static final Item PURPLE_STAINED_GLASS = registerBlock(Blocks.PURPLE_STAINED_GLASS);
    public static final Item BLUE_STAINED_GLASS = registerBlock(Blocks.BLUE_STAINED_GLASS);
    public static final Item BROWN_STAINED_GLASS = registerBlock(Blocks.BROWN_STAINED_GLASS);
    public static final Item GREEN_STAINED_GLASS = registerBlock(Blocks.GREEN_STAINED_GLASS);
    public static final Item RED_STAINED_GLASS = registerBlock(Blocks.RED_STAINED_GLASS);
    public static final Item BLACK_STAINED_GLASS = registerBlock(Blocks.BLACK_STAINED_GLASS);
    public static final Item WHITE_STAINED_GLASS_PANE = registerBlock(Blocks.WHITE_STAINED_GLASS_PANE);
    public static final Item ORANGE_STAINED_GLASS_PANE = registerBlock(Blocks.ORANGE_STAINED_GLASS_PANE);
    public static final Item MAGENTA_STAINED_GLASS_PANE = registerBlock(Blocks.MAGENTA_STAINED_GLASS_PANE);
    public static final Item LIGHT_BLUE_STAINED_GLASS_PANE = registerBlock(Blocks.LIGHT_BLUE_STAINED_GLASS_PANE);
    public static final Item YELLOW_STAINED_GLASS_PANE = registerBlock(Blocks.YELLOW_STAINED_GLASS_PANE);
    public static final Item LIME_STAINED_GLASS_PANE = registerBlock(Blocks.LIME_STAINED_GLASS_PANE);
    public static final Item PINK_STAINED_GLASS_PANE = registerBlock(Blocks.PINK_STAINED_GLASS_PANE);
    public static final Item GRAY_STAINED_GLASS_PANE = registerBlock(Blocks.GRAY_STAINED_GLASS_PANE);
    public static final Item LIGHT_GRAY_STAINED_GLASS_PANE = registerBlock(Blocks.LIGHT_GRAY_STAINED_GLASS_PANE);
    public static final Item CYAN_STAINED_GLASS_PANE = registerBlock(Blocks.CYAN_STAINED_GLASS_PANE);
    public static final Item PURPLE_STAINED_GLASS_PANE = registerBlock(Blocks.PURPLE_STAINED_GLASS_PANE);
    public static final Item BLUE_STAINED_GLASS_PANE = registerBlock(Blocks.BLUE_STAINED_GLASS_PANE);
    public static final Item BROWN_STAINED_GLASS_PANE = registerBlock(Blocks.BROWN_STAINED_GLASS_PANE);
    public static final Item GREEN_STAINED_GLASS_PANE = registerBlock(Blocks.GREEN_STAINED_GLASS_PANE);
    public static final Item RED_STAINED_GLASS_PANE = registerBlock(Blocks.RED_STAINED_GLASS_PANE);
    public static final Item BLACK_STAINED_GLASS_PANE = registerBlock(Blocks.BLACK_STAINED_GLASS_PANE);
    public static final Item PRISMARINE = registerBlock(Blocks.PRISMARINE);
    public static final Item PRISMARINE_BRICKS = registerBlock(Blocks.PRISMARINE_BRICKS);
    public static final Item DARK_PRISMARINE = registerBlock(Blocks.DARK_PRISMARINE);
    public static final Item PRISMARINE_STAIRS = registerBlock(Blocks.PRISMARINE_STAIRS);
    public static final Item PRISMARINE_BRICK_STAIRS = registerBlock(Blocks.PRISMARINE_BRICK_STAIRS);
    public static final Item DARK_PRISMARINE_STAIRS = registerBlock(Blocks.DARK_PRISMARINE_STAIRS);
    public static final Item SEA_LANTERN = registerBlock(Blocks.SEA_LANTERN);
    public static final Item RED_SANDSTONE = registerBlock(Blocks.RED_SANDSTONE);
    public static final Item CHISELED_RED_SANDSTONE = registerBlock(Blocks.CHISELED_RED_SANDSTONE);
    public static final Item CUT_RED_SANDSTONE = registerBlock(Blocks.CUT_RED_SANDSTONE);
    public static final Item RED_SANDSTONE_STAIRS = registerBlock(Blocks.RED_SANDSTONE_STAIRS);
    public static final Item REPEATING_COMMAND_BLOCK = registerBlock(
        Blocks.REPEATING_COMMAND_BLOCK, GameMasterBlockItem::new, new Item.Properties().rarity(Rarity.EPIC)
    );
    public static final Item CHAIN_COMMAND_BLOCK = registerBlock(
        Blocks.CHAIN_COMMAND_BLOCK, GameMasterBlockItem::new, new Item.Properties().rarity(Rarity.EPIC)
    );
    public static final Item MAGMA_BLOCK = registerBlock(Blocks.MAGMA_BLOCK);
    public static final Item NETHER_WART_BLOCK = registerBlock(Blocks.NETHER_WART_BLOCK);
    public static final Item WARPED_WART_BLOCK = registerBlock(Blocks.WARPED_WART_BLOCK);
    public static final Item RED_NETHER_BRICKS = registerBlock(Blocks.RED_NETHER_BRICKS);
    public static final Item BONE_BLOCK = registerBlock(Blocks.BONE_BLOCK);
    public static final Item STRUCTURE_VOID = registerBlock(Blocks.STRUCTURE_VOID, new Item.Properties().rarity(Rarity.EPIC));
    public static final Item SHULKER_BOX = registerBlock(
        Blocks.SHULKER_BOX, new Item.Properties().stacksTo(1).component(DataComponents.CONTAINER, ItemContainerContents.EMPTY)
    );
    public static final Item WHITE_SHULKER_BOX = registerBlock(
        Blocks.WHITE_SHULKER_BOX, new Item.Properties().stacksTo(1).component(DataComponents.CONTAINER, ItemContainerContents.EMPTY)
    );
    public static final Item ORANGE_SHULKER_BOX = registerBlock(
        Blocks.ORANGE_SHULKER_BOX, new Item.Properties().stacksTo(1).component(DataComponents.CONTAINER, ItemContainerContents.EMPTY)
    );
    public static final Item MAGENTA_SHULKER_BOX = registerBlock(
        Blocks.MAGENTA_SHULKER_BOX, new Item.Properties().stacksTo(1).component(DataComponents.CONTAINER, ItemContainerContents.EMPTY)
    );
    public static final Item LIGHT_BLUE_SHULKER_BOX = registerBlock(
        Blocks.LIGHT_BLUE_SHULKER_BOX, new Item.Properties().stacksTo(1).component(DataComponents.CONTAINER, ItemContainerContents.EMPTY)
    );
    public static final Item YELLOW_SHULKER_BOX = registerBlock(
        Blocks.YELLOW_SHULKER_BOX, new Item.Properties().stacksTo(1).component(DataComponents.CONTAINER, ItemContainerContents.EMPTY)
    );
    public static final Item LIME_SHULKER_BOX = registerBlock(
        Blocks.LIME_SHULKER_BOX, new Item.Properties().stacksTo(1).component(DataComponents.CONTAINER, ItemContainerContents.EMPTY)
    );
    public static final Item PINK_SHULKER_BOX = registerBlock(
        Blocks.PINK_SHULKER_BOX, new Item.Properties().stacksTo(1).component(DataComponents.CONTAINER, ItemContainerContents.EMPTY)
    );
    public static final Item GRAY_SHULKER_BOX = registerBlock(
        Blocks.GRAY_SHULKER_BOX, new Item.Properties().stacksTo(1).component(DataComponents.CONTAINER, ItemContainerContents.EMPTY)
    );
    public static final Item LIGHT_GRAY_SHULKER_BOX = registerBlock(
        Blocks.LIGHT_GRAY_SHULKER_BOX, new Item.Properties().stacksTo(1).component(DataComponents.CONTAINER, ItemContainerContents.EMPTY)
    );
    public static final Item CYAN_SHULKER_BOX = registerBlock(
        Blocks.CYAN_SHULKER_BOX, new Item.Properties().stacksTo(1).component(DataComponents.CONTAINER, ItemContainerContents.EMPTY)
    );
    public static final Item PURPLE_SHULKER_BOX = registerBlock(
        Blocks.PURPLE_SHULKER_BOX, new Item.Properties().stacksTo(1).component(DataComponents.CONTAINER, ItemContainerContents.EMPTY)
    );
    public static final Item BLUE_SHULKER_BOX = registerBlock(
        Blocks.BLUE_SHULKER_BOX, new Item.Properties().stacksTo(1).component(DataComponents.CONTAINER, ItemContainerContents.EMPTY)
    );
    public static final Item BROWN_SHULKER_BOX = registerBlock(
        Blocks.BROWN_SHULKER_BOX, new Item.Properties().stacksTo(1).component(DataComponents.CONTAINER, ItemContainerContents.EMPTY)
    );
    public static final Item GREEN_SHULKER_BOX = registerBlock(
        Blocks.GREEN_SHULKER_BOX, new Item.Properties().stacksTo(1).component(DataComponents.CONTAINER, ItemContainerContents.EMPTY)
    );
    public static final Item RED_SHULKER_BOX = registerBlock(
        Blocks.RED_SHULKER_BOX, new Item.Properties().stacksTo(1).component(DataComponents.CONTAINER, ItemContainerContents.EMPTY)
    );
    public static final Item BLACK_SHULKER_BOX = registerBlock(
        Blocks.BLACK_SHULKER_BOX, new Item.Properties().stacksTo(1).component(DataComponents.CONTAINER, ItemContainerContents.EMPTY)
    );
    public static final Item WHITE_GLAZED_TERRACOTTA = registerBlock(Blocks.WHITE_GLAZED_TERRACOTTA);
    public static final Item ORANGE_GLAZED_TERRACOTTA = registerBlock(Blocks.ORANGE_GLAZED_TERRACOTTA);
    public static final Item MAGENTA_GLAZED_TERRACOTTA = registerBlock(Blocks.MAGENTA_GLAZED_TERRACOTTA);
    public static final Item LIGHT_BLUE_GLAZED_TERRACOTTA = registerBlock(Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA);
    public static final Item YELLOW_GLAZED_TERRACOTTA = registerBlock(Blocks.YELLOW_GLAZED_TERRACOTTA);
    public static final Item LIME_GLAZED_TERRACOTTA = registerBlock(Blocks.LIME_GLAZED_TERRACOTTA);
    public static final Item PINK_GLAZED_TERRACOTTA = registerBlock(Blocks.PINK_GLAZED_TERRACOTTA);
    public static final Item GRAY_GLAZED_TERRACOTTA = registerBlock(Blocks.GRAY_GLAZED_TERRACOTTA);
    public static final Item LIGHT_GRAY_GLAZED_TERRACOTTA = registerBlock(Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA);
    public static final Item CYAN_GLAZED_TERRACOTTA = registerBlock(Blocks.CYAN_GLAZED_TERRACOTTA);
    public static final Item PURPLE_GLAZED_TERRACOTTA = registerBlock(Blocks.PURPLE_GLAZED_TERRACOTTA);
    public static final Item BLUE_GLAZED_TERRACOTTA = registerBlock(Blocks.BLUE_GLAZED_TERRACOTTA);
    public static final Item BROWN_GLAZED_TERRACOTTA = registerBlock(Blocks.BROWN_GLAZED_TERRACOTTA);
    public static final Item GREEN_GLAZED_TERRACOTTA = registerBlock(Blocks.GREEN_GLAZED_TERRACOTTA);
    public static final Item RED_GLAZED_TERRACOTTA = registerBlock(Blocks.RED_GLAZED_TERRACOTTA);
    public static final Item BLACK_GLAZED_TERRACOTTA = registerBlock(Blocks.BLACK_GLAZED_TERRACOTTA);
    public static final Item WHITE_CONCRETE = registerBlock(Blocks.WHITE_CONCRETE);
    public static final Item ORANGE_CONCRETE = registerBlock(Blocks.ORANGE_CONCRETE);
    public static final Item MAGENTA_CONCRETE = registerBlock(Blocks.MAGENTA_CONCRETE);
    public static final Item LIGHT_BLUE_CONCRETE = registerBlock(Blocks.LIGHT_BLUE_CONCRETE);
    public static final Item YELLOW_CONCRETE = registerBlock(Blocks.YELLOW_CONCRETE);
    public static final Item LIME_CONCRETE = registerBlock(Blocks.LIME_CONCRETE);
    public static final Item PINK_CONCRETE = registerBlock(Blocks.PINK_CONCRETE);
    public static final Item GRAY_CONCRETE = registerBlock(Blocks.GRAY_CONCRETE);
    public static final Item LIGHT_GRAY_CONCRETE = registerBlock(Blocks.LIGHT_GRAY_CONCRETE);
    public static final Item CYAN_CONCRETE = registerBlock(Blocks.CYAN_CONCRETE);
    public static final Item PURPLE_CONCRETE = registerBlock(Blocks.PURPLE_CONCRETE);
    public static final Item BLUE_CONCRETE = registerBlock(Blocks.BLUE_CONCRETE);
    public static final Item BROWN_CONCRETE = registerBlock(Blocks.BROWN_CONCRETE);
    public static final Item GREEN_CONCRETE = registerBlock(Blocks.GREEN_CONCRETE);
    public static final Item RED_CONCRETE = registerBlock(Blocks.RED_CONCRETE);
    public static final Item BLACK_CONCRETE = registerBlock(Blocks.BLACK_CONCRETE);
    public static final Item WHITE_CONCRETE_POWDER = registerBlock(Blocks.WHITE_CONCRETE_POWDER);
    public static final Item ORANGE_CONCRETE_POWDER = registerBlock(Blocks.ORANGE_CONCRETE_POWDER);
    public static final Item MAGENTA_CONCRETE_POWDER = registerBlock(Blocks.MAGENTA_CONCRETE_POWDER);
    public static final Item LIGHT_BLUE_CONCRETE_POWDER = registerBlock(Blocks.LIGHT_BLUE_CONCRETE_POWDER);
    public static final Item YELLOW_CONCRETE_POWDER = registerBlock(Blocks.YELLOW_CONCRETE_POWDER);
    public static final Item LIME_CONCRETE_POWDER = registerBlock(Blocks.LIME_CONCRETE_POWDER);
    public static final Item PINK_CONCRETE_POWDER = registerBlock(Blocks.PINK_CONCRETE_POWDER);
    public static final Item GRAY_CONCRETE_POWDER = registerBlock(Blocks.GRAY_CONCRETE_POWDER);
    public static final Item LIGHT_GRAY_CONCRETE_POWDER = registerBlock(Blocks.LIGHT_GRAY_CONCRETE_POWDER);
    public static final Item CYAN_CONCRETE_POWDER = registerBlock(Blocks.CYAN_CONCRETE_POWDER);
    public static final Item PURPLE_CONCRETE_POWDER = registerBlock(Blocks.PURPLE_CONCRETE_POWDER);
    public static final Item BLUE_CONCRETE_POWDER = registerBlock(Blocks.BLUE_CONCRETE_POWDER);
    public static final Item BROWN_CONCRETE_POWDER = registerBlock(Blocks.BROWN_CONCRETE_POWDER);
    public static final Item GREEN_CONCRETE_POWDER = registerBlock(Blocks.GREEN_CONCRETE_POWDER);
    public static final Item RED_CONCRETE_POWDER = registerBlock(Blocks.RED_CONCRETE_POWDER);
    public static final Item BLACK_CONCRETE_POWDER = registerBlock(Blocks.BLACK_CONCRETE_POWDER);
    public static final Item TURTLE_EGG = registerBlock(Blocks.TURTLE_EGG);
    public static final Item SNIFFER_EGG = registerBlock(Blocks.SNIFFER_EGG, p_360038_ -> p_360038_.rarity(Rarity.UNCOMMON));
    public static final Item DEAD_TUBE_CORAL_BLOCK = registerBlock(Blocks.DEAD_TUBE_CORAL_BLOCK);
    public static final Item DEAD_BRAIN_CORAL_BLOCK = registerBlock(Blocks.DEAD_BRAIN_CORAL_BLOCK);
    public static final Item DEAD_BUBBLE_CORAL_BLOCK = registerBlock(Blocks.DEAD_BUBBLE_CORAL_BLOCK);
    public static final Item DEAD_FIRE_CORAL_BLOCK = registerBlock(Blocks.DEAD_FIRE_CORAL_BLOCK);
    public static final Item DEAD_HORN_CORAL_BLOCK = registerBlock(Blocks.DEAD_HORN_CORAL_BLOCK);
    public static final Item TUBE_CORAL_BLOCK = registerBlock(Blocks.TUBE_CORAL_BLOCK);
    public static final Item BRAIN_CORAL_BLOCK = registerBlock(Blocks.BRAIN_CORAL_BLOCK);
    public static final Item BUBBLE_CORAL_BLOCK = registerBlock(Blocks.BUBBLE_CORAL_BLOCK);
    public static final Item FIRE_CORAL_BLOCK = registerBlock(Blocks.FIRE_CORAL_BLOCK);
    public static final Item HORN_CORAL_BLOCK = registerBlock(Blocks.HORN_CORAL_BLOCK);
    public static final Item TUBE_CORAL = registerBlock(Blocks.TUBE_CORAL);
    public static final Item BRAIN_CORAL = registerBlock(Blocks.BRAIN_CORAL);
    public static final Item BUBBLE_CORAL = registerBlock(Blocks.BUBBLE_CORAL);
    public static final Item FIRE_CORAL = registerBlock(Blocks.FIRE_CORAL);
    public static final Item HORN_CORAL = registerBlock(Blocks.HORN_CORAL);
    public static final Item DEAD_BRAIN_CORAL = registerBlock(Blocks.DEAD_BRAIN_CORAL);
    public static final Item DEAD_BUBBLE_CORAL = registerBlock(Blocks.DEAD_BUBBLE_CORAL);
    public static final Item DEAD_FIRE_CORAL = registerBlock(Blocks.DEAD_FIRE_CORAL);
    public static final Item DEAD_HORN_CORAL = registerBlock(Blocks.DEAD_HORN_CORAL);
    public static final Item DEAD_TUBE_CORAL = registerBlock(Blocks.DEAD_TUBE_CORAL);
    public static final Item TUBE_CORAL_FAN = registerBlock(
        Blocks.TUBE_CORAL_FAN, (p_370910_, p_370911_) -> new StandingAndWallBlockItem(p_370910_, Blocks.TUBE_CORAL_WALL_FAN, Direction.DOWN, p_370911_)
    );
    public static final Item BRAIN_CORAL_FAN = registerBlock(
        Blocks.BRAIN_CORAL_FAN, (p_370942_, p_370943_) -> new StandingAndWallBlockItem(p_370942_, Blocks.BRAIN_CORAL_WALL_FAN, Direction.DOWN, p_370943_)
    );
    public static final Item BUBBLE_CORAL_FAN = registerBlock(
        Blocks.BUBBLE_CORAL_FAN, (p_370891_, p_370892_) -> new StandingAndWallBlockItem(p_370891_, Blocks.BUBBLE_CORAL_WALL_FAN, Direction.DOWN, p_370892_)
    );
    public static final Item FIRE_CORAL_FAN = registerBlock(
        Blocks.FIRE_CORAL_FAN, (p_370982_, p_370983_) -> new StandingAndWallBlockItem(p_370982_, Blocks.FIRE_CORAL_WALL_FAN, Direction.DOWN, p_370983_)
    );
    public static final Item HORN_CORAL_FAN = registerBlock(
        Blocks.HORN_CORAL_FAN, (p_370874_, p_370875_) -> new StandingAndWallBlockItem(p_370874_, Blocks.HORN_CORAL_WALL_FAN, Direction.DOWN, p_370875_)
    );
    public static final Item DEAD_TUBE_CORAL_FAN = registerBlock(
        Blocks.DEAD_TUBE_CORAL_FAN,
        (p_370887_, p_370888_) -> new StandingAndWallBlockItem(p_370887_, Blocks.DEAD_TUBE_CORAL_WALL_FAN, Direction.DOWN, p_370888_)
    );
    public static final Item DEAD_BRAIN_CORAL_FAN = registerBlock(
        Blocks.DEAD_BRAIN_CORAL_FAN,
        (p_370752_, p_370753_) -> new StandingAndWallBlockItem(p_370752_, Blocks.DEAD_BRAIN_CORAL_WALL_FAN, Direction.DOWN, p_370753_)
    );
    public static final Item DEAD_BUBBLE_CORAL_FAN = registerBlock(
        Blocks.DEAD_BUBBLE_CORAL_FAN,
        (p_371050_, p_371051_) -> new StandingAndWallBlockItem(p_371050_, Blocks.DEAD_BUBBLE_CORAL_WALL_FAN, Direction.DOWN, p_371051_)
    );
    public static final Item DEAD_FIRE_CORAL_FAN = registerBlock(
        Blocks.DEAD_FIRE_CORAL_FAN,
        (p_370822_, p_370823_) -> new StandingAndWallBlockItem(p_370822_, Blocks.DEAD_FIRE_CORAL_WALL_FAN, Direction.DOWN, p_370823_)
    );
    public static final Item DEAD_HORN_CORAL_FAN = registerBlock(
        Blocks.DEAD_HORN_CORAL_FAN,
        (p_371092_, p_371093_) -> new StandingAndWallBlockItem(p_371092_, Blocks.DEAD_HORN_CORAL_WALL_FAN, Direction.DOWN, p_371093_)
    );
    public static final Item BLUE_ICE = registerBlock(Blocks.BLUE_ICE);
    public static final Item CONDUIT = registerBlock(Blocks.CONDUIT, new Item.Properties().rarity(Rarity.UNCOMMON));
    public static final Item POLISHED_GRANITE_STAIRS = registerBlock(Blocks.POLISHED_GRANITE_STAIRS);
    public static final Item SMOOTH_RED_SANDSTONE_STAIRS = registerBlock(Blocks.SMOOTH_RED_SANDSTONE_STAIRS);
    public static final Item MOSSY_STONE_BRICK_STAIRS = registerBlock(Blocks.MOSSY_STONE_BRICK_STAIRS);
    public static final Item POLISHED_DIORITE_STAIRS = registerBlock(Blocks.POLISHED_DIORITE_STAIRS);
    public static final Item MOSSY_COBBLESTONE_STAIRS = registerBlock(Blocks.MOSSY_COBBLESTONE_STAIRS);
    public static final Item END_STONE_BRICK_STAIRS = registerBlock(Blocks.END_STONE_BRICK_STAIRS);
    public static final Item STONE_STAIRS = registerBlock(Blocks.STONE_STAIRS);
    public static final Item SMOOTH_SANDSTONE_STAIRS = registerBlock(Blocks.SMOOTH_SANDSTONE_STAIRS);
    public static final Item SMOOTH_QUARTZ_STAIRS = registerBlock(Blocks.SMOOTH_QUARTZ_STAIRS);
    public static final Item GRANITE_STAIRS = registerBlock(Blocks.GRANITE_STAIRS);
    public static final Item ANDESITE_STAIRS = registerBlock(Blocks.ANDESITE_STAIRS);
    public static final Item RED_NETHER_BRICK_STAIRS = registerBlock(Blocks.RED_NETHER_BRICK_STAIRS);
    public static final Item POLISHED_ANDESITE_STAIRS = registerBlock(Blocks.POLISHED_ANDESITE_STAIRS);
    public static final Item DIORITE_STAIRS = registerBlock(Blocks.DIORITE_STAIRS);
    public static final Item COBBLED_DEEPSLATE_STAIRS = registerBlock(Blocks.COBBLED_DEEPSLATE_STAIRS);
    public static final Item POLISHED_DEEPSLATE_STAIRS = registerBlock(Blocks.POLISHED_DEEPSLATE_STAIRS);
    public static final Item DEEPSLATE_BRICK_STAIRS = registerBlock(Blocks.DEEPSLATE_BRICK_STAIRS);
    public static final Item DEEPSLATE_TILE_STAIRS = registerBlock(Blocks.DEEPSLATE_TILE_STAIRS);
    public static final Item POLISHED_GRANITE_SLAB = registerBlock(Blocks.POLISHED_GRANITE_SLAB);
    public static final Item SMOOTH_RED_SANDSTONE_SLAB = registerBlock(Blocks.SMOOTH_RED_SANDSTONE_SLAB);
    public static final Item MOSSY_STONE_BRICK_SLAB = registerBlock(Blocks.MOSSY_STONE_BRICK_SLAB);
    public static final Item POLISHED_DIORITE_SLAB = registerBlock(Blocks.POLISHED_DIORITE_SLAB);
    public static final Item MOSSY_COBBLESTONE_SLAB = registerBlock(Blocks.MOSSY_COBBLESTONE_SLAB);
    public static final Item END_STONE_BRICK_SLAB = registerBlock(Blocks.END_STONE_BRICK_SLAB);
    public static final Item SMOOTH_SANDSTONE_SLAB = registerBlock(Blocks.SMOOTH_SANDSTONE_SLAB);
    public static final Item SMOOTH_QUARTZ_SLAB = registerBlock(Blocks.SMOOTH_QUARTZ_SLAB);
    public static final Item GRANITE_SLAB = registerBlock(Blocks.GRANITE_SLAB);
    public static final Item ANDESITE_SLAB = registerBlock(Blocks.ANDESITE_SLAB);
    public static final Item RED_NETHER_BRICK_SLAB = registerBlock(Blocks.RED_NETHER_BRICK_SLAB);
    public static final Item POLISHED_ANDESITE_SLAB = registerBlock(Blocks.POLISHED_ANDESITE_SLAB);
    public static final Item DIORITE_SLAB = registerBlock(Blocks.DIORITE_SLAB);
    public static final Item COBBLED_DEEPSLATE_SLAB = registerBlock(Blocks.COBBLED_DEEPSLATE_SLAB);
    public static final Item POLISHED_DEEPSLATE_SLAB = registerBlock(Blocks.POLISHED_DEEPSLATE_SLAB);
    public static final Item DEEPSLATE_BRICK_SLAB = registerBlock(Blocks.DEEPSLATE_BRICK_SLAB);
    public static final Item DEEPSLATE_TILE_SLAB = registerBlock(Blocks.DEEPSLATE_TILE_SLAB);
    public static final Item SCAFFOLDING = registerBlock(Blocks.SCAFFOLDING, ScaffoldingBlockItem::new);
    public static final Item REDSTONE = registerItem("redstone", createBlockItemWithCustomItemName(Blocks.REDSTONE_WIRE));
    public static final Item REDSTONE_TORCH = registerBlock(
        Blocks.REDSTONE_TORCH, (p_370955_, p_370956_) -> new StandingAndWallBlockItem(p_370955_, Blocks.REDSTONE_WALL_TORCH, Direction.DOWN, p_370956_)
    );
    public static final Item REDSTONE_BLOCK = registerBlock(Blocks.REDSTONE_BLOCK);
    public static final Item REPEATER = registerBlock(Blocks.REPEATER);
    public static final Item COMPARATOR = registerBlock(Blocks.COMPARATOR);
    public static final Item PISTON = registerBlock(Blocks.PISTON);
    public static final Item STICKY_PISTON = registerBlock(Blocks.STICKY_PISTON);
    public static final Item SLIME_BLOCK = registerBlock(Blocks.SLIME_BLOCK);
    public static final Item HONEY_BLOCK = registerBlock(Blocks.HONEY_BLOCK);
    public static final Item OBSERVER = registerBlock(Blocks.OBSERVER);
    public static final Item HOPPER = registerBlock(Blocks.HOPPER, p_332746_ -> p_332746_.component(DataComponents.CONTAINER, ItemContainerContents.EMPTY));
    public static final Item DISPENSER = registerBlock(
        Blocks.DISPENSER, p_332667_ -> p_332667_.component(DataComponents.CONTAINER, ItemContainerContents.EMPTY)
    );
    public static final Item DROPPER = registerBlock(Blocks.DROPPER, p_332799_ -> p_332799_.component(DataComponents.CONTAINER, ItemContainerContents.EMPTY));
    public static final Item LECTERN = registerBlock(Blocks.LECTERN);
    public static final Item TARGET = registerBlock(Blocks.TARGET);
    public static final Item LEVER = registerBlock(Blocks.LEVER);
    public static final Item LIGHTNING_ROD = registerBlock(Blocks.LIGHTNING_ROD);
    public static final Item DAYLIGHT_DETECTOR = registerBlock(Blocks.DAYLIGHT_DETECTOR);
    public static final Item SCULK_SENSOR = registerBlock(Blocks.SCULK_SENSOR);
    public static final Item CALIBRATED_SCULK_SENSOR = registerBlock(Blocks.CALIBRATED_SCULK_SENSOR);
    public static final Item TRIPWIRE_HOOK = registerBlock(Blocks.TRIPWIRE_HOOK);
    public static final Item TRAPPED_CHEST = registerBlock(
        Blocks.TRAPPED_CHEST, p_332815_ -> p_332815_.component(DataComponents.CONTAINER, ItemContainerContents.EMPTY)
    );
    public static final Item TNT = registerBlock(Blocks.TNT);
    public static final Item REDSTONE_LAMP = registerBlock(Blocks.REDSTONE_LAMP);
    public static final Item NOTE_BLOCK = registerBlock(Blocks.NOTE_BLOCK);
    public static final Item STONE_BUTTON = registerBlock(Blocks.STONE_BUTTON);
    public static final Item POLISHED_BLACKSTONE_BUTTON = registerBlock(Blocks.POLISHED_BLACKSTONE_BUTTON);
    public static final Item OAK_BUTTON = registerBlock(Blocks.OAK_BUTTON);
    public static final Item SPRUCE_BUTTON = registerBlock(Blocks.SPRUCE_BUTTON);
    public static final Item BIRCH_BUTTON = registerBlock(Blocks.BIRCH_BUTTON);
    public static final Item JUNGLE_BUTTON = registerBlock(Blocks.JUNGLE_BUTTON);
    public static final Item ACACIA_BUTTON = registerBlock(Blocks.ACACIA_BUTTON);
    public static final Item CHERRY_BUTTON = registerBlock(Blocks.CHERRY_BUTTON);
    public static final Item DARK_OAK_BUTTON = registerBlock(Blocks.DARK_OAK_BUTTON);
    public static final Item PALE_OAK_BUTTON = registerBlock(Blocks.PALE_OAK_BUTTON);
    public static final Item MANGROVE_BUTTON = registerBlock(Blocks.MANGROVE_BUTTON);
    public static final Item BAMBOO_BUTTON = registerBlock(Blocks.BAMBOO_BUTTON);
    public static final Item CRIMSON_BUTTON = registerBlock(Blocks.CRIMSON_BUTTON);
    public static final Item WARPED_BUTTON = registerBlock(Blocks.WARPED_BUTTON);
    public static final Item STONE_PRESSURE_PLATE = registerBlock(Blocks.STONE_PRESSURE_PLATE);
    public static final Item POLISHED_BLACKSTONE_PRESSURE_PLATE = registerBlock(Blocks.POLISHED_BLACKSTONE_PRESSURE_PLATE);
    public static final Item LIGHT_WEIGHTED_PRESSURE_PLATE = registerBlock(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE);
    public static final Item HEAVY_WEIGHTED_PRESSURE_PLATE = registerBlock(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE);
    public static final Item OAK_PRESSURE_PLATE = registerBlock(Blocks.OAK_PRESSURE_PLATE);
    public static final Item SPRUCE_PRESSURE_PLATE = registerBlock(Blocks.SPRUCE_PRESSURE_PLATE);
    public static final Item BIRCH_PRESSURE_PLATE = registerBlock(Blocks.BIRCH_PRESSURE_PLATE);
    public static final Item JUNGLE_PRESSURE_PLATE = registerBlock(Blocks.JUNGLE_PRESSURE_PLATE);
    public static final Item ACACIA_PRESSURE_PLATE = registerBlock(Blocks.ACACIA_PRESSURE_PLATE);
    public static final Item CHERRY_PRESSURE_PLATE = registerBlock(Blocks.CHERRY_PRESSURE_PLATE);
    public static final Item DARK_OAK_PRESSURE_PLATE = registerBlock(Blocks.DARK_OAK_PRESSURE_PLATE);
    public static final Item PALE_OAK_PRESSURE_PLATE = registerBlock(Blocks.PALE_OAK_PRESSURE_PLATE);
    public static final Item MANGROVE_PRESSURE_PLATE = registerBlock(Blocks.MANGROVE_PRESSURE_PLATE);
    public static final Item BAMBOO_PRESSURE_PLATE = registerBlock(Blocks.BAMBOO_PRESSURE_PLATE);
    public static final Item CRIMSON_PRESSURE_PLATE = registerBlock(Blocks.CRIMSON_PRESSURE_PLATE);
    public static final Item WARPED_PRESSURE_PLATE = registerBlock(Blocks.WARPED_PRESSURE_PLATE);
    public static final Item IRON_DOOR = registerBlock(Blocks.IRON_DOOR, DoubleHighBlockItem::new);
    public static final Item OAK_DOOR = registerBlock(Blocks.OAK_DOOR, DoubleHighBlockItem::new);
    public static final Item SPRUCE_DOOR = registerBlock(Blocks.SPRUCE_DOOR, DoubleHighBlockItem::new);
    public static final Item BIRCH_DOOR = registerBlock(Blocks.BIRCH_DOOR, DoubleHighBlockItem::new);
    public static final Item JUNGLE_DOOR = registerBlock(Blocks.JUNGLE_DOOR, DoubleHighBlockItem::new);
    public static final Item ACACIA_DOOR = registerBlock(Blocks.ACACIA_DOOR, DoubleHighBlockItem::new);
    public static final Item CHERRY_DOOR = registerBlock(Blocks.CHERRY_DOOR, DoubleHighBlockItem::new);
    public static final Item DARK_OAK_DOOR = registerBlock(Blocks.DARK_OAK_DOOR, DoubleHighBlockItem::new);
    public static final Item PALE_OAK_DOOR = registerBlock(Blocks.PALE_OAK_DOOR, DoubleHighBlockItem::new);
    public static final Item MANGROVE_DOOR = registerBlock(Blocks.MANGROVE_DOOR, DoubleHighBlockItem::new);
    public static final Item BAMBOO_DOOR = registerBlock(Blocks.BAMBOO_DOOR, DoubleHighBlockItem::new);
    public static final Item CRIMSON_DOOR = registerBlock(Blocks.CRIMSON_DOOR, DoubleHighBlockItem::new);
    public static final Item WARPED_DOOR = registerBlock(Blocks.WARPED_DOOR, DoubleHighBlockItem::new);
    public static final Item COPPER_DOOR = registerBlock(Blocks.COPPER_DOOR, DoubleHighBlockItem::new);
    public static final Item EXPOSED_COPPER_DOOR = registerBlock(Blocks.EXPOSED_COPPER_DOOR, DoubleHighBlockItem::new);
    public static final Item WEATHERED_COPPER_DOOR = registerBlock(Blocks.WEATHERED_COPPER_DOOR, DoubleHighBlockItem::new);
    public static final Item OXIDIZED_COPPER_DOOR = registerBlock(Blocks.OXIDIZED_COPPER_DOOR, DoubleHighBlockItem::new);
    public static final Item WAXED_COPPER_DOOR = registerBlock(Blocks.WAXED_COPPER_DOOR, DoubleHighBlockItem::new);
    public static final Item WAXED_EXPOSED_COPPER_DOOR = registerBlock(Blocks.WAXED_EXPOSED_COPPER_DOOR, DoubleHighBlockItem::new);
    public static final Item WAXED_WEATHERED_COPPER_DOOR = registerBlock(Blocks.WAXED_WEATHERED_COPPER_DOOR, DoubleHighBlockItem::new);
    public static final Item WAXED_OXIDIZED_COPPER_DOOR = registerBlock(Blocks.WAXED_OXIDIZED_COPPER_DOOR, DoubleHighBlockItem::new);
    public static final Item IRON_TRAPDOOR = registerBlock(Blocks.IRON_TRAPDOOR);
    public static final Item OAK_TRAPDOOR = registerBlock(Blocks.OAK_TRAPDOOR);
    public static final Item SPRUCE_TRAPDOOR = registerBlock(Blocks.SPRUCE_TRAPDOOR);
    public static final Item BIRCH_TRAPDOOR = registerBlock(Blocks.BIRCH_TRAPDOOR);
    public static final Item JUNGLE_TRAPDOOR = registerBlock(Blocks.JUNGLE_TRAPDOOR);
    public static final Item ACACIA_TRAPDOOR = registerBlock(Blocks.ACACIA_TRAPDOOR);
    public static final Item CHERRY_TRAPDOOR = registerBlock(Blocks.CHERRY_TRAPDOOR);
    public static final Item DARK_OAK_TRAPDOOR = registerBlock(Blocks.DARK_OAK_TRAPDOOR);
    public static final Item PALE_OAK_TRAPDOOR = registerBlock(Blocks.PALE_OAK_TRAPDOOR);
    public static final Item MANGROVE_TRAPDOOR = registerBlock(Blocks.MANGROVE_TRAPDOOR);
    public static final Item BAMBOO_TRAPDOOR = registerBlock(Blocks.BAMBOO_TRAPDOOR);
    public static final Item CRIMSON_TRAPDOOR = registerBlock(Blocks.CRIMSON_TRAPDOOR);
    public static final Item WARPED_TRAPDOOR = registerBlock(Blocks.WARPED_TRAPDOOR);
    public static final Item COPPER_TRAPDOOR = registerBlock(Blocks.COPPER_TRAPDOOR);
    public static final Item EXPOSED_COPPER_TRAPDOOR = registerBlock(Blocks.EXPOSED_COPPER_TRAPDOOR);
    public static final Item WEATHERED_COPPER_TRAPDOOR = registerBlock(Blocks.WEATHERED_COPPER_TRAPDOOR);
    public static final Item OXIDIZED_COPPER_TRAPDOOR = registerBlock(Blocks.OXIDIZED_COPPER_TRAPDOOR);
    public static final Item WAXED_COPPER_TRAPDOOR = registerBlock(Blocks.WAXED_COPPER_TRAPDOOR);
    public static final Item WAXED_EXPOSED_COPPER_TRAPDOOR = registerBlock(Blocks.WAXED_EXPOSED_COPPER_TRAPDOOR);
    public static final Item WAXED_WEATHERED_COPPER_TRAPDOOR = registerBlock(Blocks.WAXED_WEATHERED_COPPER_TRAPDOOR);
    public static final Item WAXED_OXIDIZED_COPPER_TRAPDOOR = registerBlock(Blocks.WAXED_OXIDIZED_COPPER_TRAPDOOR);
    public static final Item OAK_FENCE_GATE = registerBlock(Blocks.OAK_FENCE_GATE);
    public static final Item SPRUCE_FENCE_GATE = registerBlock(Blocks.SPRUCE_FENCE_GATE);
    public static final Item BIRCH_FENCE_GATE = registerBlock(Blocks.BIRCH_FENCE_GATE);
    public static final Item JUNGLE_FENCE_GATE = registerBlock(Blocks.JUNGLE_FENCE_GATE);
    public static final Item ACACIA_FENCE_GATE = registerBlock(Blocks.ACACIA_FENCE_GATE);
    public static final Item CHERRY_FENCE_GATE = registerBlock(Blocks.CHERRY_FENCE_GATE);
    public static final Item DARK_OAK_FENCE_GATE = registerBlock(Blocks.DARK_OAK_FENCE_GATE);
    public static final Item PALE_OAK_FENCE_GATE = registerBlock(Blocks.PALE_OAK_FENCE_GATE);
    public static final Item MANGROVE_FENCE_GATE = registerBlock(Blocks.MANGROVE_FENCE_GATE);
    public static final Item BAMBOO_FENCE_GATE = registerBlock(Blocks.BAMBOO_FENCE_GATE);
    public static final Item CRIMSON_FENCE_GATE = registerBlock(Blocks.CRIMSON_FENCE_GATE);
    public static final Item WARPED_FENCE_GATE = registerBlock(Blocks.WARPED_FENCE_GATE);
    public static final Item POWERED_RAIL = registerBlock(Blocks.POWERED_RAIL);
    public static final Item DETECTOR_RAIL = registerBlock(Blocks.DETECTOR_RAIL);
    public static final Item RAIL = registerBlock(Blocks.RAIL);
    public static final Item ACTIVATOR_RAIL = registerBlock(Blocks.ACTIVATOR_RAIL);
    public static final Item SADDLE = registerItem("saddle", SaddleItem::new, new Item.Properties().stacksTo(1));
    public static final Item MINECART = registerItem(
        "minecart", p_373937_ -> new MinecartItem(EntityType.MINECART, p_373937_), new Item.Properties().stacksTo(1)
    );
    public static final Item CHEST_MINECART = registerItem(
        "chest_minecart", p_373928_ -> new MinecartItem(EntityType.CHEST_MINECART, p_373928_), new Item.Properties().stacksTo(1)
    );
    public static final Item FURNACE_MINECART = registerItem(
        "furnace_minecart", p_373947_ -> new MinecartItem(EntityType.FURNACE_MINECART, p_373947_), new Item.Properties().stacksTo(1)
    );
    public static final Item TNT_MINECART = registerItem(
        "tnt_minecart", p_373932_ -> new MinecartItem(EntityType.TNT_MINECART, p_373932_), new Item.Properties().stacksTo(1)
    );
    public static final Item HOPPER_MINECART = registerItem(
        "hopper_minecart", p_373935_ -> new MinecartItem(EntityType.HOPPER_MINECART, p_373935_), new Item.Properties().stacksTo(1)
    );
    public static final Item CARROT_ON_A_STICK = registerItem(
        "carrot_on_a_stick", p_371087_ -> new FoodOnAStickItem<>(EntityType.PIG, 7, p_371087_), new Item.Properties().durability(25)
    );
    public static final Item WARPED_FUNGUS_ON_A_STICK = registerItem(
        "warped_fungus_on_a_stick", p_370981_ -> new FoodOnAStickItem<>(EntityType.STRIDER, 1, p_370981_), new Item.Properties().durability(100)
    );
    public static final Item PHANTOM_MEMBRANE = registerItem("phantom_membrane");
    public static final Item ELYTRA = registerItem(
        "elytra",
        new Item.Properties()
            .durability(432)
            .rarity(Rarity.EPIC)
            .component(DataComponents.GLIDER, Unit.INSTANCE)
            .component(
                DataComponents.EQUIPPABLE,
                Equippable.builder(EquipmentSlot.CHEST)
                    .setEquipSound(SoundEvents.ARMOR_EQUIP_ELYTRA)
                    .setAsset(EquipmentAssets.ELYTRA)
                    .setDamageOnHurt(false)
                    .build()
            )
            .repairable(PHANTOM_MEMBRANE)
    );
    public static final Item OAK_BOAT = registerItem("oak_boat", p_376002_ -> new BoatItem(EntityType.OAK_BOAT, p_376002_), new Item.Properties().stacksTo(1));
    public static final Item OAK_CHEST_BOAT = registerItem(
        "oak_chest_boat", p_375994_ -> new BoatItem(EntityType.OAK_CHEST_BOAT, p_375994_), new Item.Properties().stacksTo(1)
    );
    public static final Item SPRUCE_BOAT = registerItem(
        "spruce_boat", p_375996_ -> new BoatItem(EntityType.SPRUCE_BOAT, p_375996_), new Item.Properties().stacksTo(1)
    );
    public static final Item SPRUCE_CHEST_BOAT = registerItem(
        "spruce_chest_boat", p_375989_ -> new BoatItem(EntityType.SPRUCE_CHEST_BOAT, p_375989_), new Item.Properties().stacksTo(1)
    );
    public static final Item BIRCH_BOAT = registerItem(
        "birch_boat", p_376003_ -> new BoatItem(EntityType.BIRCH_BOAT, p_376003_), new Item.Properties().stacksTo(1)
    );
    public static final Item BIRCH_CHEST_BOAT = registerItem(
        "birch_chest_boat", p_376005_ -> new BoatItem(EntityType.BIRCH_CHEST_BOAT, p_376005_), new Item.Properties().stacksTo(1)
    );
    public static final Item JUNGLE_BOAT = registerItem(
        "jungle_boat", p_375988_ -> new BoatItem(EntityType.JUNGLE_BOAT, p_375988_), new Item.Properties().stacksTo(1)
    );
    public static final Item JUNGLE_CHEST_BOAT = registerItem(
        "jungle_chest_boat", p_376004_ -> new BoatItem(EntityType.JUNGLE_CHEST_BOAT, p_376004_), new Item.Properties().stacksTo(1)
    );
    public static final Item ACACIA_BOAT = registerItem(
        "acacia_boat", p_376000_ -> new BoatItem(EntityType.ACACIA_BOAT, p_376000_), new Item.Properties().stacksTo(1)
    );
    public static final Item ACACIA_CHEST_BOAT = registerItem(
        "acacia_chest_boat", p_375999_ -> new BoatItem(EntityType.ACACIA_CHEST_BOAT, p_375999_), new Item.Properties().stacksTo(1)
    );
    public static final Item CHERRY_BOAT = registerItem(
        "cherry_boat", p_375995_ -> new BoatItem(EntityType.CHERRY_BOAT, p_375995_), new Item.Properties().stacksTo(1)
    );
    public static final Item CHERRY_CHEST_BOAT = registerItem(
        "cherry_chest_boat", p_375993_ -> new BoatItem(EntityType.CHERRY_CHEST_BOAT, p_375993_), new Item.Properties().stacksTo(1)
    );
    public static final Item DARK_OAK_BOAT = registerItem(
        "dark_oak_boat", p_376008_ -> new BoatItem(EntityType.DARK_OAK_BOAT, p_376008_), new Item.Properties().stacksTo(1)
    );
    public static final Item DARK_OAK_CHEST_BOAT = registerItem(
        "dark_oak_chest_boat", p_376006_ -> new BoatItem(EntityType.DARK_OAK_CHEST_BOAT, p_376006_), new Item.Properties().stacksTo(1)
    );
    public static final Item PALE_OAK_BOAT = registerItem(
        "pale_oak_boat", p_379209_ -> new BoatItem(EntityType.PALE_OAK_BOAT, p_379209_), new Item.Properties().stacksTo(1)
    );
    public static final Item PALE_OAK_CHEST_BOAT = registerItem(
        "pale_oak_chest_boat", p_379210_ -> new BoatItem(EntityType.PALE_OAK_CHEST_BOAT, p_379210_), new Item.Properties().stacksTo(1)
    );
    public static final Item MANGROVE_BOAT = registerItem(
        "mangrove_boat", p_376001_ -> new BoatItem(EntityType.MANGROVE_BOAT, p_376001_), new Item.Properties().stacksTo(1)
    );
    public static final Item MANGROVE_CHEST_BOAT = registerItem(
        "mangrove_chest_boat", p_375990_ -> new BoatItem(EntityType.MANGROVE_CHEST_BOAT, p_375990_), new Item.Properties().stacksTo(1)
    );
    public static final Item BAMBOO_RAFT = registerItem(
        "bamboo_raft", p_375997_ -> new BoatItem(EntityType.BAMBOO_RAFT, p_375997_), new Item.Properties().stacksTo(1)
    );
    public static final Item BAMBOO_CHEST_RAFT = registerItem(
        "bamboo_chest_raft", p_376007_ -> new BoatItem(EntityType.BAMBOO_CHEST_RAFT, p_376007_), new Item.Properties().stacksTo(1)
    );
    public static final Item STRUCTURE_BLOCK = registerBlock(Blocks.STRUCTURE_BLOCK, GameMasterBlockItem::new, new Item.Properties().rarity(Rarity.EPIC));
    public static final Item JIGSAW = registerBlock(Blocks.JIGSAW, GameMasterBlockItem::new, new Item.Properties().rarity(Rarity.EPIC));
    public static final Item TURTLE_HELMET = registerItem("turtle_helmet", p_370921_ -> new ArmorItem(ArmorMaterials.TURTLE_SCUTE, ArmorType.HELMET, p_370921_));
    public static final Item TURTLE_SCUTE = registerItem("turtle_scute");
    public static final Item ARMADILLO_SCUTE = registerItem("armadillo_scute");
    public static final Item WOLF_ARMOR = registerItem(
        "wolf_armor", p_370990_ -> new AnimalArmorItem(ArmorMaterials.ARMADILLO_SCUTE, AnimalArmorItem.BodyType.CANINE, p_370990_)
    );
    public static final Item FLINT_AND_STEEL = registerItem("flint_and_steel", FlintAndSteelItem::new, new Item.Properties().durability(64));
    public static final Item BOWL = registerItem("bowl");
    public static final Item APPLE = registerItem("apple", new Item.Properties().food(Foods.APPLE));
    public static final Item BOW = registerItem("bow", BowItem::new, new Item.Properties().durability(384).enchantable(1));
    public static final Item ARROW = registerItem("arrow", ArrowItem::new);
    public static final Item COAL = registerItem("coal");
    public static final Item CHARCOAL = registerItem("charcoal");
    public static final Item DIAMOND = registerItem("diamond");
    public static final Item EMERALD = registerItem("emerald");
    public static final Item LAPIS_LAZULI = registerItem("lapis_lazuli");
    public static final Item QUARTZ = registerItem("quartz");
    public static final Item AMETHYST_SHARD = registerItem("amethyst_shard");
    public static final Item RAW_IRON = registerItem("raw_iron");
    public static final Item IRON_INGOT = registerItem("iron_ingot");
    public static final Item RAW_COPPER = registerItem("raw_copper");
    public static final Item COPPER_INGOT = registerItem("copper_ingot");
    public static final Item RAW_GOLD = registerItem("raw_gold");
    public static final Item GOLD_INGOT = registerItem("gold_ingot");
    public static final Item NETHERITE_INGOT = registerItem("netherite_ingot", new Item.Properties().fireResistant());
    public static final Item NETHERITE_SCRAP = registerItem("netherite_scrap", new Item.Properties().fireResistant());
    public static final Item WOODEN_SWORD = registerItem("wooden_sword", p_370788_ -> new SwordItem(ToolMaterial.WOOD, 3.0F, -2.4F, p_370788_));
    public static final Item WOODEN_SHOVEL = registerItem("wooden_shovel", p_370807_ -> new ShovelItem(ToolMaterial.WOOD, 1.5F, -3.0F, p_370807_));
    public static final Item WOODEN_PICKAXE = registerItem("wooden_pickaxe", p_370925_ -> new PickaxeItem(ToolMaterial.WOOD, 1.0F, -2.8F, p_370925_));
    public static final Item WOODEN_AXE = registerItem("wooden_axe", p_370924_ -> new AxeItem(ToolMaterial.WOOD, 6.0F, -3.2F, p_370924_));
    public static final Item WOODEN_HOE = registerItem("wooden_hoe", p_371086_ -> new HoeItem(ToolMaterial.WOOD, 0.0F, -3.0F, p_371086_));
    public static final Item STONE_SWORD = registerItem("stone_sword", p_370772_ -> new SwordItem(ToolMaterial.STONE, 3.0F, -2.4F, p_370772_));
    public static final Item STONE_SHOVEL = registerItem("stone_shovel", p_370867_ -> new ShovelItem(ToolMaterial.STONE, 1.5F, -3.0F, p_370867_));
    public static final Item STONE_PICKAXE = registerItem("stone_pickaxe", p_370890_ -> new PickaxeItem(ToolMaterial.STONE, 1.0F, -2.8F, p_370890_));
    public static final Item STONE_AXE = registerItem("stone_axe", p_370885_ -> new AxeItem(ToolMaterial.STONE, 7.0F, -3.2F, p_370885_));
    public static final Item STONE_HOE = registerItem("stone_hoe", p_370774_ -> new HoeItem(ToolMaterial.STONE, -1.0F, -2.0F, p_370774_));
    public static final Item GOLDEN_SWORD = registerItem("golden_sword", p_370795_ -> new SwordItem(ToolMaterial.GOLD, 3.0F, -2.4F, p_370795_));
    public static final Item GOLDEN_SHOVEL = registerItem("golden_shovel", p_371016_ -> new ShovelItem(ToolMaterial.GOLD, 1.5F, -3.0F, p_371016_));
    public static final Item GOLDEN_PICKAXE = registerItem("golden_pickaxe", p_371012_ -> new PickaxeItem(ToolMaterial.GOLD, 1.0F, -2.8F, p_371012_));
    public static final Item GOLDEN_AXE = registerItem("golden_axe", p_371075_ -> new AxeItem(ToolMaterial.GOLD, 6.0F, -3.0F, p_371075_));
    public static final Item GOLDEN_HOE = registerItem("golden_hoe", p_370883_ -> new HoeItem(ToolMaterial.GOLD, 0.0F, -3.0F, p_370883_));
    public static final Item IRON_SWORD = registerItem("iron_sword", p_370765_ -> new SwordItem(ToolMaterial.IRON, 3.0F, -2.4F, p_370765_));
    public static final Item IRON_SHOVEL = registerItem("iron_shovel", p_370831_ -> new ShovelItem(ToolMaterial.IRON, 1.5F, -3.0F, p_370831_));
    public static final Item IRON_PICKAXE = registerItem("iron_pickaxe", p_371024_ -> new PickaxeItem(ToolMaterial.IRON, 1.0F, -2.8F, p_371024_));
    public static final Item IRON_AXE = registerItem("iron_axe", p_370879_ -> new AxeItem(ToolMaterial.IRON, 6.0F, -3.1F, p_370879_));
    public static final Item IRON_HOE = registerItem("iron_hoe", p_370951_ -> new HoeItem(ToolMaterial.IRON, -2.0F, -1.0F, p_370951_));
    public static final Item DIAMOND_SWORD = registerItem("diamond_sword", p_371054_ -> new SwordItem(ToolMaterial.DIAMOND, 3.0F, -2.4F, p_371054_));
    public static final Item DIAMOND_SHOVEL = registerItem("diamond_shovel", p_370997_ -> new ShovelItem(ToolMaterial.DIAMOND, 1.5F, -3.0F, p_370997_));
    public static final Item DIAMOND_PICKAXE = registerItem("diamond_pickaxe", p_370858_ -> new PickaxeItem(ToolMaterial.DIAMOND, 1.0F, -2.8F, p_370858_));
    public static final Item DIAMOND_AXE = registerItem("diamond_axe", p_370775_ -> new AxeItem(ToolMaterial.DIAMOND, 5.0F, -3.0F, p_370775_));
    public static final Item DIAMOND_HOE = registerItem("diamond_hoe", p_371013_ -> new HoeItem(ToolMaterial.DIAMOND, -3.0F, 0.0F, p_371013_));
    public static final Item NETHERITE_SWORD = registerItem(
        "netherite_sword", p_370863_ -> new SwordItem(ToolMaterial.NETHERITE, 3.0F, -2.4F, p_370863_), new Item.Properties().fireResistant()
    );
    public static final Item NETHERITE_SHOVEL = registerItem(
        "netherite_shovel", p_370959_ -> new ShovelItem(ToolMaterial.NETHERITE, 1.5F, -3.0F, p_370959_), new Item.Properties().fireResistant()
    );
    public static final Item NETHERITE_PICKAXE = registerItem(
        "netherite_pickaxe", p_370958_ -> new PickaxeItem(ToolMaterial.NETHERITE, 1.0F, -2.8F, p_370958_), new Item.Properties().fireResistant()
    );
    public static final Item NETHERITE_AXE = registerItem(
        "netherite_axe", p_370794_ -> new AxeItem(ToolMaterial.NETHERITE, 5.0F, -3.0F, p_370794_), new Item.Properties().fireResistant()
    );
    public static final Item NETHERITE_HOE = registerItem(
        "netherite_hoe", p_370937_ -> new HoeItem(ToolMaterial.NETHERITE, -4.0F, 0.0F, p_370937_), new Item.Properties().fireResistant()
    );
    public static final Item STICK = registerItem("stick");
    public static final Item MUSHROOM_STEW = registerItem("mushroom_stew", new Item.Properties().stacksTo(1).food(Foods.MUSHROOM_STEW).usingConvertsTo(BOWL));
    public static final Item STRING = registerItem("string", createBlockItemWithCustomItemName(Blocks.TRIPWIRE));
    public static final Item FEATHER = registerItem("feather");
    public static final Item GUNPOWDER = registerItem("gunpowder");
    public static final Item WHEAT_SEEDS = registerItem("wheat_seeds", createBlockItemWithCustomItemName(Blocks.WHEAT));
    public static final Item WHEAT = registerItem("wheat");
    public static final Item BREAD = registerItem("bread", new Item.Properties().food(Foods.BREAD));
    public static final Item LEATHER_HELMET = registerItem("leather_helmet", p_370828_ -> new ArmorItem(ArmorMaterials.LEATHER, ArmorType.HELMET, p_370828_));
    public static final Item LEATHER_CHESTPLATE = registerItem(
        "leather_chestplate", p_371038_ -> new ArmorItem(ArmorMaterials.LEATHER, ArmorType.CHESTPLATE, p_371038_)
    );
    public static final Item LEATHER_LEGGINGS = registerItem(
        "leather_leggings", p_370985_ -> new ArmorItem(ArmorMaterials.LEATHER, ArmorType.LEGGINGS, p_370985_)
    );
    public static final Item LEATHER_BOOTS = registerItem("leather_boots", p_370904_ -> new ArmorItem(ArmorMaterials.LEATHER, ArmorType.BOOTS, p_370904_));
    public static final Item CHAINMAIL_HELMET = registerItem(
        "chainmail_helmet", p_372771_ -> new ArmorItem(ArmorMaterials.CHAINMAIL, ArmorType.HELMET, p_372771_), new Item.Properties().rarity(Rarity.UNCOMMON)
    );
    public static final Item CHAINMAIL_CHESTPLATE = registerItem(
        "chainmail_chestplate",
        p_372774_ -> new ArmorItem(ArmorMaterials.CHAINMAIL, ArmorType.CHESTPLATE, p_372774_),
        new Item.Properties().rarity(Rarity.UNCOMMON)
    );
    public static final Item CHAINMAIL_LEGGINGS = registerItem(
        "chainmail_leggings",
        p_372775_ -> new ArmorItem(ArmorMaterials.CHAINMAIL, ArmorType.LEGGINGS, p_372775_),
        new Item.Properties().rarity(Rarity.UNCOMMON)
    );
    public static final Item CHAINMAIL_BOOTS = registerItem(
        "chainmail_boots", p_372772_ -> new ArmorItem(ArmorMaterials.CHAINMAIL, ArmorType.BOOTS, p_372772_), new Item.Properties().rarity(Rarity.UNCOMMON)
    );
    public static final Item IRON_HELMET = registerItem("iron_helmet", p_370894_ -> new ArmorItem(ArmorMaterials.IRON, ArmorType.HELMET, p_370894_));
    public static final Item IRON_CHESTPLATE = registerItem("iron_chestplate", p_371084_ -> new ArmorItem(ArmorMaterials.IRON, ArmorType.CHESTPLATE, p_371084_));
    public static final Item IRON_LEGGINGS = registerItem("iron_leggings", p_371035_ -> new ArmorItem(ArmorMaterials.IRON, ArmorType.LEGGINGS, p_371035_));
    public static final Item IRON_BOOTS = registerItem("iron_boots", p_370835_ -> new ArmorItem(ArmorMaterials.IRON, ArmorType.BOOTS, p_370835_));
    public static final Item DIAMOND_HELMET = registerItem("diamond_helmet", p_370756_ -> new ArmorItem(ArmorMaterials.DIAMOND, ArmorType.HELMET, p_370756_));
    public static final Item DIAMOND_CHESTPLATE = registerItem(
        "diamond_chestplate", p_370853_ -> new ArmorItem(ArmorMaterials.DIAMOND, ArmorType.CHESTPLATE, p_370853_)
    );
    public static final Item DIAMOND_LEGGINGS = registerItem(
        "diamond_leggings", p_370948_ -> new ArmorItem(ArmorMaterials.DIAMOND, ArmorType.LEGGINGS, p_370948_)
    );
    public static final Item DIAMOND_BOOTS = registerItem("diamond_boots", p_370819_ -> new ArmorItem(ArmorMaterials.DIAMOND, ArmorType.BOOTS, p_370819_));
    public static final Item GOLDEN_HELMET = registerItem("golden_helmet", p_370915_ -> new ArmorItem(ArmorMaterials.GOLD, ArmorType.HELMET, p_370915_));
    public static final Item GOLDEN_CHESTPLATE = registerItem(
        "golden_chestplate", p_371001_ -> new ArmorItem(ArmorMaterials.GOLD, ArmorType.CHESTPLATE, p_371001_)
    );
    public static final Item GOLDEN_LEGGINGS = registerItem("golden_leggings", p_370975_ -> new ArmorItem(ArmorMaterials.GOLD, ArmorType.LEGGINGS, p_370975_));
    public static final Item GOLDEN_BOOTS = registerItem("golden_boots", p_371055_ -> new ArmorItem(ArmorMaterials.GOLD, ArmorType.BOOTS, p_371055_));
    public static final Item NETHERITE_HELMET = registerItem(
        "netherite_helmet", p_371046_ -> new ArmorItem(ArmorMaterials.NETHERITE, ArmorType.HELMET, p_371046_), new Item.Properties().fireResistant()
    );
    public static final Item NETHERITE_CHESTPLATE = registerItem(
        "netherite_chestplate", p_371025_ -> new ArmorItem(ArmorMaterials.NETHERITE, ArmorType.CHESTPLATE, p_371025_), new Item.Properties().fireResistant()
    );
    public static final Item NETHERITE_LEGGINGS = registerItem(
        "netherite_leggings", p_371072_ -> new ArmorItem(ArmorMaterials.NETHERITE, ArmorType.LEGGINGS, p_371072_), new Item.Properties().fireResistant()
    );
    public static final Item NETHERITE_BOOTS = registerItem(
        "netherite_boots", p_370871_ -> new ArmorItem(ArmorMaterials.NETHERITE, ArmorType.BOOTS, p_370871_), new Item.Properties().fireResistant()
    );
    public static final Item FLINT = registerItem("flint");
    public static final Item PORKCHOP = registerItem("porkchop", new Item.Properties().food(Foods.PORKCHOP));
    public static final Item COOKED_PORKCHOP = registerItem("cooked_porkchop", new Item.Properties().food(Foods.COOKED_PORKCHOP));
    public static final Item PAINTING = registerItem("painting", p_371070_ -> new HangingEntityItem(EntityType.PAINTING, p_371070_));
    public static final Item GOLDEN_APPLE = registerItem("golden_apple", new Item.Properties().food(Foods.GOLDEN_APPLE, Consumables.GOLDEN_APPLE));
    public static final Item ENCHANTED_GOLDEN_APPLE = registerItem(
        "enchanted_golden_apple",
        new Item.Properties()
            .rarity(Rarity.RARE)
            .food(Foods.ENCHANTED_GOLDEN_APPLE, Consumables.ENCHANTED_GOLDEN_APPLE)
            .component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)
    );
    public static final Item OAK_SIGN = registerBlock(
        Blocks.OAK_SIGN, (p_371040_, p_371041_) -> new SignItem(p_371040_, Blocks.OAK_WALL_SIGN, p_371041_), new Item.Properties().stacksTo(16)
    );
    public static final Item SPRUCE_SIGN = registerBlock(
        Blocks.SPRUCE_SIGN, (p_370944_, p_370945_) -> new SignItem(p_370944_, Blocks.SPRUCE_WALL_SIGN, p_370945_), new Item.Properties().stacksTo(16)
    );
    public static final Item BIRCH_SIGN = registerBlock(
        Blocks.BIRCH_SIGN, (p_370864_, p_370865_) -> new SignItem(p_370864_, Blocks.BIRCH_WALL_SIGN, p_370865_), new Item.Properties().stacksTo(16)
    );
    public static final Item JUNGLE_SIGN = registerBlock(
        Blocks.JUNGLE_SIGN, (p_370792_, p_370793_) -> new SignItem(p_370792_, Blocks.JUNGLE_WALL_SIGN, p_370793_), new Item.Properties().stacksTo(16)
    );
    public static final Item ACACIA_SIGN = registerBlock(
        Blocks.ACACIA_SIGN, (p_370767_, p_370768_) -> new SignItem(p_370767_, Blocks.ACACIA_WALL_SIGN, p_370768_), new Item.Properties().stacksTo(16)
    );
    public static final Item CHERRY_SIGN = registerBlock(
        Blocks.CHERRY_SIGN, (p_370872_, p_370873_) -> new SignItem(p_370872_, Blocks.CHERRY_WALL_SIGN, p_370873_), new Item.Properties().stacksTo(16)
    );
    public static final Item DARK_OAK_SIGN = registerBlock(
        Blocks.DARK_OAK_SIGN, (p_370820_, p_370821_) -> new SignItem(p_370820_, Blocks.DARK_OAK_WALL_SIGN, p_370821_), new Item.Properties().stacksTo(16)
    );
    public static final Item PALE_OAK_SIGN = registerBlock(
        Blocks.PALE_OAK_SIGN, (p_379207_, p_379208_) -> new SignItem(p_379207_, Blocks.PALE_OAK_WALL_SIGN, p_379208_), new Item.Properties().stacksTo(16)
    );
    public static final Item MANGROVE_SIGN = registerBlock(
        Blocks.MANGROVE_SIGN, (p_370809_, p_370810_) -> new SignItem(p_370809_, Blocks.MANGROVE_WALL_SIGN, p_370810_), new Item.Properties().stacksTo(16)
    );
    public static final Item BAMBOO_SIGN = registerBlock(
        Blocks.BAMBOO_SIGN, (p_370802_, p_370803_) -> new SignItem(p_370802_, Blocks.BAMBOO_WALL_SIGN, p_370803_), new Item.Properties().stacksTo(16)
    );
    public static final Item CRIMSON_SIGN = registerBlock(
        Blocks.CRIMSON_SIGN, (p_370965_, p_370966_) -> new SignItem(p_370965_, Blocks.CRIMSON_WALL_SIGN, p_370966_), new Item.Properties().stacksTo(16)
    );
    public static final Item WARPED_SIGN = registerBlock(
        Blocks.WARPED_SIGN, (p_371089_, p_371090_) -> new SignItem(p_371089_, Blocks.WARPED_WALL_SIGN, p_371090_), new Item.Properties().stacksTo(16)
    );
    public static final Item OAK_HANGING_SIGN = registerBlock(
        Blocks.OAK_HANGING_SIGN,
        (p_371043_, p_371044_) -> new HangingSignItem(p_371043_, Blocks.OAK_WALL_HANGING_SIGN, p_371044_),
        new Item.Properties().stacksTo(16)
    );
    public static final Item SPRUCE_HANGING_SIGN = registerBlock(
        Blocks.SPRUCE_HANGING_SIGN,
        (p_370930_, p_370931_) -> new HangingSignItem(p_370930_, Blocks.SPRUCE_WALL_HANGING_SIGN, p_370931_),
        new Item.Properties().stacksTo(16)
    );
    public static final Item BIRCH_HANGING_SIGN = registerBlock(
        Blocks.BIRCH_HANGING_SIGN,
        (p_370769_, p_370770_) -> new HangingSignItem(p_370769_, Blocks.BIRCH_WALL_HANGING_SIGN, p_370770_),
        new Item.Properties().stacksTo(16)
    );
    public static final Item JUNGLE_HANGING_SIGN = registerBlock(
        Blocks.JUNGLE_HANGING_SIGN,
        (p_370895_, p_370896_) -> new HangingSignItem(p_370895_, Blocks.JUNGLE_WALL_HANGING_SIGN, p_370896_),
        new Item.Properties().stacksTo(16)
    );
    public static final Item ACACIA_HANGING_SIGN = registerBlock(
        Blocks.ACACIA_HANGING_SIGN,
        (p_371003_, p_371004_) -> new HangingSignItem(p_371003_, Blocks.ACACIA_WALL_HANGING_SIGN, p_371004_),
        new Item.Properties().stacksTo(16)
    );
    public static final Item CHERRY_HANGING_SIGN = registerBlock(
        Blocks.CHERRY_HANGING_SIGN,
        (p_371014_, p_371015_) -> new HangingSignItem(p_371014_, Blocks.CHERRY_WALL_HANGING_SIGN, p_371015_),
        new Item.Properties().stacksTo(16)
    );
    public static final Item DARK_OAK_HANGING_SIGN = registerBlock(
        Blocks.DARK_OAK_HANGING_SIGN,
        (p_370933_, p_370934_) -> new HangingSignItem(p_370933_, Blocks.DARK_OAK_WALL_HANGING_SIGN, p_370934_),
        new Item.Properties().stacksTo(16)
    );
    public static final Item PALE_OAK_HANGING_SIGN = registerBlock(
        Blocks.PALE_OAK_HANGING_SIGN,
        (p_379202_, p_379203_) -> new HangingSignItem(p_379202_, Blocks.PALE_OAK_WALL_HANGING_SIGN, p_379203_),
        new Item.Properties().stacksTo(16)
    );
    public static final Item MANGROVE_HANGING_SIGN = registerBlock(
        Blocks.MANGROVE_HANGING_SIGN,
        (p_371007_, p_371008_) -> new HangingSignItem(p_371007_, Blocks.MANGROVE_WALL_HANGING_SIGN, p_371008_),
        new Item.Properties().stacksTo(16)
    );
    public static final Item BAMBOO_HANGING_SIGN = registerBlock(
        Blocks.BAMBOO_HANGING_SIGN,
        (p_370762_, p_370763_) -> new HangingSignItem(p_370762_, Blocks.BAMBOO_WALL_HANGING_SIGN, p_370763_),
        new Item.Properties().stacksTo(16)
    );
    public static final Item CRIMSON_HANGING_SIGN = registerBlock(
        Blocks.CRIMSON_HANGING_SIGN,
        (p_371029_, p_371030_) -> new HangingSignItem(p_371029_, Blocks.CRIMSON_WALL_HANGING_SIGN, p_371030_),
        new Item.Properties().stacksTo(16)
    );
    public static final Item WARPED_HANGING_SIGN = registerBlock(
        Blocks.WARPED_HANGING_SIGN,
        (p_370805_, p_370806_) -> new HangingSignItem(p_370805_, Blocks.WARPED_WALL_HANGING_SIGN, p_370806_),
        new Item.Properties().stacksTo(16)
    );
    public static final Item BUCKET = registerItem("bucket", p_370796_ -> new BucketItem(Fluids.EMPTY, p_370796_), new Item.Properties().stacksTo(16));
    public static final Item WATER_BUCKET = registerItem(
        "water_bucket", p_370817_ -> new BucketItem(Fluids.WATER, p_370817_), new Item.Properties().craftRemainder(BUCKET).stacksTo(1)
    );
    public static final Item LAVA_BUCKET = registerItem(
        "lava_bucket", p_370973_ -> new BucketItem(Fluids.LAVA, p_370973_), new Item.Properties().craftRemainder(BUCKET).stacksTo(1)
    );
    public static final Item POWDER_SNOW_BUCKET = registerItem(
        "powder_snow_bucket",
        p_370902_ -> new SolidBucketItem(Blocks.POWDER_SNOW, SoundEvents.BUCKET_EMPTY_POWDER_SNOW, p_370902_),
        new Item.Properties().stacksTo(1).useItemDescriptionPrefix()
    );
    public static final Item SNOWBALL = registerItem("snowball", SnowballItem::new, new Item.Properties().stacksTo(16));
    public static final Item LEATHER = registerItem("leather");
    public static final Item MILK_BUCKET = registerItem(
        "milk_bucket",
        new Item.Properties().craftRemainder(BUCKET).component(DataComponents.CONSUMABLE, Consumables.MILK_BUCKET).usingConvertsTo(BUCKET).stacksTo(1)
    );
    public static final Item PUFFERFISH_BUCKET = registerItem(
        "pufferfish_bucket",
        p_370838_ -> new MobBucketItem(EntityType.PUFFERFISH, Fluids.WATER, SoundEvents.BUCKET_EMPTY_FISH, p_370838_),
        new Item.Properties().stacksTo(1).component(DataComponents.BUCKET_ENTITY_DATA, CustomData.EMPTY)
    );
    public static final Item SALMON_BUCKET = registerItem(
        "salmon_bucket",
        p_370830_ -> new MobBucketItem(EntityType.SALMON, Fluids.WATER, SoundEvents.BUCKET_EMPTY_FISH, p_370830_),
        new Item.Properties().stacksTo(1).component(DataComponents.BUCKET_ENTITY_DATA, CustomData.EMPTY)
    );
    public static final Item COD_BUCKET = registerItem(
        "cod_bucket",
        p_370976_ -> new MobBucketItem(EntityType.COD, Fluids.WATER, SoundEvents.BUCKET_EMPTY_FISH, p_370976_),
        new Item.Properties().stacksTo(1).component(DataComponents.BUCKET_ENTITY_DATA, CustomData.EMPTY)
    );
    public static final Item TROPICAL_FISH_BUCKET = registerItem(
        "tropical_fish_bucket",
        p_370780_ -> new MobBucketItem(EntityType.TROPICAL_FISH, Fluids.WATER, SoundEvents.BUCKET_EMPTY_FISH, p_370780_),
        new Item.Properties().stacksTo(1).component(DataComponents.BUCKET_ENTITY_DATA, CustomData.EMPTY)
    );
    public static final Item AXOLOTL_BUCKET = registerItem(
        "axolotl_bucket",
        p_370771_ -> new MobBucketItem(EntityType.AXOLOTL, Fluids.WATER, SoundEvents.BUCKET_EMPTY_AXOLOTL, p_370771_),
        new Item.Properties().stacksTo(1).component(DataComponents.BUCKET_ENTITY_DATA, CustomData.EMPTY)
    );
    public static final Item TADPOLE_BUCKET = registerItem(
        "tadpole_bucket",
        p_370963_ -> new MobBucketItem(EntityType.TADPOLE, Fluids.WATER, SoundEvents.BUCKET_EMPTY_TADPOLE, p_370963_),
        new Item.Properties().stacksTo(1).component(DataComponents.BUCKET_ENTITY_DATA, CustomData.EMPTY)
    );
    public static final Item BRICK = registerItem("brick");
    public static final Item CLAY_BALL = registerItem("clay_ball");
    public static final Item DRIED_KELP_BLOCK = registerBlock(Blocks.DRIED_KELP_BLOCK);
    public static final Item PAPER = registerItem("paper");
    public static final Item BOOK = registerItem("book", new Item.Properties().enchantable(1));
    public static final Item SLIME_BALL = registerItem("slime_ball");
    public static final Item EGG = registerItem("egg", EggItem::new, new Item.Properties().stacksTo(16));
    public static final Item COMPASS = registerItem("compass", CompassItem::new);
    public static final Item RECOVERY_COMPASS = registerItem("recovery_compass", new Item.Properties().rarity(Rarity.UNCOMMON));
    public static final Item BUNDLE = registerItem(
        "bundle", BundleItem::new, new Item.Properties().stacksTo(1).component(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY)
    );
    public static final Item WHITE_BUNDLE = registerItem(
        "white_bundle", BundleItem::new, new Item.Properties().stacksTo(1).component(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY)
    );
    public static final Item ORANGE_BUNDLE = registerItem(
        "orange_bundle", BundleItem::new, new Item.Properties().stacksTo(1).component(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY)
    );
    public static final Item MAGENTA_BUNDLE = registerItem(
        "magenta_bundle", BundleItem::new, new Item.Properties().stacksTo(1).component(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY)
    );
    public static final Item LIGHT_BLUE_BUNDLE = registerItem(
        "light_blue_bundle", BundleItem::new, new Item.Properties().stacksTo(1).component(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY)
    );
    public static final Item YELLOW_BUNDLE = registerItem(
        "yellow_bundle", BundleItem::new, new Item.Properties().stacksTo(1).component(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY)
    );
    public static final Item LIME_BUNDLE = registerItem(
        "lime_bundle", BundleItem::new, new Item.Properties().stacksTo(1).component(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY)
    );
    public static final Item PINK_BUNDLE = registerItem(
        "pink_bundle", BundleItem::new, new Item.Properties().stacksTo(1).component(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY)
    );
    public static final Item GRAY_BUNDLE = registerItem(
        "gray_bundle", BundleItem::new, new Item.Properties().stacksTo(1).component(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY)
    );
    public static final Item LIGHT_GRAY_BUNDLE = registerItem(
        "light_gray_bundle", BundleItem::new, new Item.Properties().stacksTo(1).component(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY)
    );
    public static final Item CYAN_BUNDLE = registerItem(
        "cyan_bundle", BundleItem::new, new Item.Properties().stacksTo(1).component(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY)
    );
    public static final Item PURPLE_BUNDLE = registerItem(
        "purple_bundle", BundleItem::new, new Item.Properties().stacksTo(1).component(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY)
    );
    public static final Item BLUE_BUNDLE = registerItem(
        "blue_bundle", BundleItem::new, new Item.Properties().stacksTo(1).component(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY)
    );
    public static final Item BROWN_BUNDLE = registerItem(
        "brown_bundle", BundleItem::new, new Item.Properties().stacksTo(1).component(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY)
    );
    public static final Item GREEN_BUNDLE = registerItem(
        "green_bundle", BundleItem::new, new Item.Properties().stacksTo(1).component(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY)
    );
    public static final Item RED_BUNDLE = registerItem(
        "red_bundle", BundleItem::new, new Item.Properties().stacksTo(1).component(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY)
    );
    public static final Item BLACK_BUNDLE = registerItem(
        "black_bundle", BundleItem::new, new Item.Properties().stacksTo(1).component(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY)
    );
    public static final Item FISHING_ROD = registerItem("fishing_rod", FishingRodItem::new, new Item.Properties().durability(64).enchantable(1));
    public static final Item CLOCK = registerItem("clock");
    public static final Item SPYGLASS = registerItem("spyglass", SpyglassItem::new, new Item.Properties().stacksTo(1));
    public static final Item GLOWSTONE_DUST = registerItem("glowstone_dust");
    public static final Item COD = registerItem("cod", new Item.Properties().food(Foods.COD));
    public static final Item SALMON = registerItem("salmon", new Item.Properties().food(Foods.SALMON));
    public static final Item TROPICAL_FISH = registerItem("tropical_fish", new Item.Properties().food(Foods.TROPICAL_FISH));
    public static final Item PUFFERFISH = registerItem("pufferfish", new Item.Properties().food(Foods.PUFFERFISH, Consumables.PUFFERFISH));
    public static final Item COOKED_COD = registerItem("cooked_cod", new Item.Properties().food(Foods.COOKED_COD));
    public static final Item COOKED_SALMON = registerItem("cooked_salmon", new Item.Properties().food(Foods.COOKED_SALMON));
    public static final Item INK_SAC = registerItem("ink_sac", InkSacItem::new);
    public static final Item GLOW_INK_SAC = registerItem("glow_ink_sac", GlowInkSacItem::new);
    public static final Item COCOA_BEANS = registerItem("cocoa_beans", createBlockItemWithCustomItemName(Blocks.COCOA));
    public static final Item WHITE_DYE = registerItem("white_dye", p_370979_ -> new DyeItem(DyeColor.WHITE, p_370979_));
    public static final Item ORANGE_DYE = registerItem("orange_dye", p_370977_ -> new DyeItem(DyeColor.ORANGE, p_370977_));
    public static final Item MAGENTA_DYE = registerItem("magenta_dye", p_370764_ -> new DyeItem(DyeColor.MAGENTA, p_370764_));
    public static final Item LIGHT_BLUE_DYE = registerItem("light_blue_dye", p_370953_ -> new DyeItem(DyeColor.LIGHT_BLUE, p_370953_));
    public static final Item YELLOW_DYE = registerItem("yellow_dye", p_370949_ -> new DyeItem(DyeColor.YELLOW, p_370949_));
    public static final Item LIME_DYE = registerItem("lime_dye", p_370782_ -> new DyeItem(DyeColor.LIME, p_370782_));
    public static final Item PINK_DYE = registerItem("pink_dye", p_370922_ -> new DyeItem(DyeColor.PINK, p_370922_));
    public static final Item GRAY_DYE = registerItem("gray_dye", p_370849_ -> new DyeItem(DyeColor.GRAY, p_370849_));
    public static final Item LIGHT_GRAY_DYE = registerItem("light_gray_dye", p_370908_ -> new DyeItem(DyeColor.LIGHT_GRAY, p_370908_));
    public static final Item CYAN_DYE = registerItem("cyan_dye", p_370905_ -> new DyeItem(DyeColor.CYAN, p_370905_));
    public static final Item PURPLE_DYE = registerItem("purple_dye", p_370826_ -> new DyeItem(DyeColor.PURPLE, p_370826_));
    public static final Item BLUE_DYE = registerItem("blue_dye", p_371059_ -> new DyeItem(DyeColor.BLUE, p_371059_));
    public static final Item BROWN_DYE = registerItem("brown_dye", p_371026_ -> new DyeItem(DyeColor.BROWN, p_371026_));
    public static final Item GREEN_DYE = registerItem("green_dye", p_370886_ -> new DyeItem(DyeColor.GREEN, p_370886_));
    public static final Item RED_DYE = registerItem("red_dye", p_371049_ -> new DyeItem(DyeColor.RED, p_371049_));
    public static final Item BLACK_DYE = registerItem("black_dye", p_371069_ -> new DyeItem(DyeColor.BLACK, p_371069_));
    public static final Item BONE_MEAL = registerItem("bone_meal", BoneMealItem::new);
    public static final Item BONE = registerItem("bone");
    public static final Item SUGAR = registerItem("sugar");
    public static final Item CAKE = registerBlock(Blocks.CAKE, new Item.Properties().stacksTo(1));
    public static final Item WHITE_BED = registerBlock(Blocks.WHITE_BED, BedItem::new, new Item.Properties().stacksTo(1));
    public static final Item ORANGE_BED = registerBlock(Blocks.ORANGE_BED, BedItem::new, new Item.Properties().stacksTo(1));
    public static final Item MAGENTA_BED = registerBlock(Blocks.MAGENTA_BED, BedItem::new, new Item.Properties().stacksTo(1));
    public static final Item LIGHT_BLUE_BED = registerBlock(Blocks.LIGHT_BLUE_BED, BedItem::new, new Item.Properties().stacksTo(1));
    public static final Item YELLOW_BED = registerBlock(Blocks.YELLOW_BED, BedItem::new, new Item.Properties().stacksTo(1));
    public static final Item LIME_BED = registerBlock(Blocks.LIME_BED, BedItem::new, new Item.Properties().stacksTo(1));
    public static final Item PINK_BED = registerBlock(Blocks.PINK_BED, BedItem::new, new Item.Properties().stacksTo(1));
    public static final Item GRAY_BED = registerBlock(Blocks.GRAY_BED, BedItem::new, new Item.Properties().stacksTo(1));
    public static final Item LIGHT_GRAY_BED = registerBlock(Blocks.LIGHT_GRAY_BED, BedItem::new, new Item.Properties().stacksTo(1));
    public static final Item CYAN_BED = registerBlock(Blocks.CYAN_BED, BedItem::new, new Item.Properties().stacksTo(1));
    public static final Item PURPLE_BED = registerBlock(Blocks.PURPLE_BED, BedItem::new, new Item.Properties().stacksTo(1));
    public static final Item BLUE_BED = registerBlock(Blocks.BLUE_BED, BedItem::new, new Item.Properties().stacksTo(1));
    public static final Item BROWN_BED = registerBlock(Blocks.BROWN_BED, BedItem::new, new Item.Properties().stacksTo(1));
    public static final Item GREEN_BED = registerBlock(Blocks.GREEN_BED, BedItem::new, new Item.Properties().stacksTo(1));
    public static final Item RED_BED = registerBlock(Blocks.RED_BED, BedItem::new, new Item.Properties().stacksTo(1));
    public static final Item BLACK_BED = registerBlock(Blocks.BLACK_BED, BedItem::new, new Item.Properties().stacksTo(1));
    public static final Item COOKIE = registerItem("cookie", new Item.Properties().food(Foods.COOKIE));
    public static final Item CRAFTER = registerBlock(Blocks.CRAFTER, p_332827_ -> p_332827_.component(DataComponents.CONTAINER, ItemContainerContents.EMPTY));
    public static final Item FILLED_MAP = registerItem(
        "filled_map",
        MapItem::new,
        new Item.Properties().component(DataComponents.MAP_COLOR, MapItemColor.DEFAULT).component(DataComponents.MAP_DECORATIONS, MapDecorations.EMPTY)
    );
    public static final Item SHEARS = registerItem(
        "shears", ShearsItem::new, new Item.Properties().durability(238).component(DataComponents.TOOL, ShearsItem.createToolProperties())
    );
    public static final Item MELON_SLICE = registerItem("melon_slice", new Item.Properties().food(Foods.MELON_SLICE));
    public static final Item DRIED_KELP = registerItem("dried_kelp", new Item.Properties().food(Foods.DRIED_KELP, Consumables.DRIED_KELP));
    public static final Item PUMPKIN_SEEDS = registerItem(net.minecraft.references.Items.PUMPKIN_SEEDS, createBlockItemWithCustomItemName(Blocks.PUMPKIN_STEM));
    public static final Item MELON_SEEDS = registerItem(net.minecraft.references.Items.MELON_SEEDS, createBlockItemWithCustomItemName(Blocks.MELON_STEM));
    public static final Item BEEF = registerItem("beef", new Item.Properties().food(Foods.BEEF));
    public static final Item COOKED_BEEF = registerItem("cooked_beef", new Item.Properties().food(Foods.COOKED_BEEF));
    public static final Item CHICKEN = registerItem("chicken", new Item.Properties().food(Foods.CHICKEN, Consumables.CHICKEN));
    public static final Item COOKED_CHICKEN = registerItem("cooked_chicken", new Item.Properties().food(Foods.COOKED_CHICKEN));
    public static final Item ROTTEN_FLESH = registerItem("rotten_flesh", new Item.Properties().food(Foods.ROTTEN_FLESH, Consumables.ROTTEN_FLESH));
    public static final Item ENDER_PEARL = registerItem("ender_pearl", EnderpearlItem::new, new Item.Properties().stacksTo(16).useCooldown(1.0F));
    public static final Item BLAZE_ROD = registerItem("blaze_rod");
    public static final Item GHAST_TEAR = registerItem("ghast_tear");
    public static final Item GOLD_NUGGET = registerItem("gold_nugget");
    public static final Item NETHER_WART = registerItem("nether_wart", createBlockItemWithCustomItemName(Blocks.NETHER_WART));
    public static final Item GLASS_BOTTLE = registerItem("glass_bottle", BottleItem::new);
    public static final Item POTION = registerItem(
        "potion",
        PotionItem::new,
        new Item.Properties()
            .stacksTo(1)
            .component(DataComponents.POTION_CONTENTS, PotionContents.EMPTY)
            .component(DataComponents.CONSUMABLE, Consumables.DEFAULT_DRINK)
            .usingConvertsTo(GLASS_BOTTLE)
    );
    public static final Item SPIDER_EYE = registerItem("spider_eye", new Item.Properties().food(Foods.SPIDER_EYE, Consumables.SPIDER_EYE));
    public static final Item FERMENTED_SPIDER_EYE = registerItem("fermented_spider_eye");
    public static final Item BLAZE_POWDER = registerItem("blaze_powder");
    public static final Item MAGMA_CREAM = registerItem("magma_cream");
    public static final Item BREWING_STAND = registerBlock(
        Blocks.BREWING_STAND, p_332734_ -> p_332734_.component(DataComponents.CONTAINER, ItemContainerContents.EMPTY)
    );
    public static final Item CAULDRON = registerBlock(Blocks.CAULDRON, Blocks.WATER_CAULDRON, Blocks.LAVA_CAULDRON, Blocks.POWDER_SNOW_CAULDRON);
    public static final Item ENDER_EYE = registerItem("ender_eye", EnderEyeItem::new);
    public static final Item GLISTERING_MELON_SLICE = registerItem("glistering_melon_slice");
    public static final Item ARMADILLO_SPAWN_EGG = registerItem("armadillo_spawn_egg", p_386366_ -> new SpawnEggItem(EntityType.ARMADILLO, p_386366_));
    public static final Item ALLAY_SPAWN_EGG = registerItem("allay_spawn_egg", p_386385_ -> new SpawnEggItem(EntityType.ALLAY, p_386385_));
    public static final Item AXOLOTL_SPAWN_EGG = registerItem("axolotl_spawn_egg", p_386411_ -> new SpawnEggItem(EntityType.AXOLOTL, p_386411_));
    public static final Item BAT_SPAWN_EGG = registerItem("bat_spawn_egg", p_386365_ -> new SpawnEggItem(EntityType.BAT, p_386365_));
    public static final Item BEE_SPAWN_EGG = registerItem("bee_spawn_egg", p_386352_ -> new SpawnEggItem(EntityType.BEE, p_386352_));
    public static final Item BLAZE_SPAWN_EGG = registerItem("blaze_spawn_egg", p_386379_ -> new SpawnEggItem(EntityType.BLAZE, p_386379_));
    public static final Item BOGGED_SPAWN_EGG = registerItem("bogged_spawn_egg", p_386398_ -> new SpawnEggItem(EntityType.BOGGED, p_386398_));
    public static final Item BREEZE_SPAWN_EGG = registerItem("breeze_spawn_egg", p_386388_ -> new SpawnEggItem(EntityType.BREEZE, p_386388_));
    public static final Item CAT_SPAWN_EGG = registerItem("cat_spawn_egg", p_386364_ -> new SpawnEggItem(EntityType.CAT, p_386364_));
    public static final Item CAMEL_SPAWN_EGG = registerItem("camel_spawn_egg", p_386408_ -> new SpawnEggItem(EntityType.CAMEL, p_386408_));
    public static final Item CAVE_SPIDER_SPAWN_EGG = registerItem("cave_spider_spawn_egg", p_386380_ -> new SpawnEggItem(EntityType.CAVE_SPIDER, p_386380_));
    public static final Item CHICKEN_SPAWN_EGG = registerItem("chicken_spawn_egg", p_386394_ -> new SpawnEggItem(EntityType.CHICKEN, p_386394_));
    public static final Item COD_SPAWN_EGG = registerItem("cod_spawn_egg", p_386377_ -> new SpawnEggItem(EntityType.COD, p_386377_));
    public static final Item COW_SPAWN_EGG = registerItem("cow_spawn_egg", p_386392_ -> new SpawnEggItem(EntityType.COW, p_386392_));
    public static final Item CREEPER_SPAWN_EGG = registerItem("creeper_spawn_egg", p_386340_ -> new SpawnEggItem(EntityType.CREEPER, p_386340_));
    public static final Item DOLPHIN_SPAWN_EGG = registerItem("dolphin_spawn_egg", p_386376_ -> new SpawnEggItem(EntityType.DOLPHIN, p_386376_));
    public static final Item DONKEY_SPAWN_EGG = registerItem("donkey_spawn_egg", p_386336_ -> new SpawnEggItem(EntityType.DONKEY, p_386336_));
    public static final Item DROWNED_SPAWN_EGG = registerItem("drowned_spawn_egg", p_386407_ -> new SpawnEggItem(EntityType.DROWNED, p_386407_));
    public static final Item ELDER_GUARDIAN_SPAWN_EGG = registerItem(
        "elder_guardian_spawn_egg", p_386382_ -> new SpawnEggItem(EntityType.ELDER_GUARDIAN, p_386382_)
    );
    public static final Item ENDER_DRAGON_SPAWN_EGG = registerItem("ender_dragon_spawn_egg", p_386405_ -> new SpawnEggItem(EntityType.ENDER_DRAGON, p_386405_));
    public static final Item ENDERMAN_SPAWN_EGG = registerItem("enderman_spawn_egg", p_386372_ -> new SpawnEggItem(EntityType.ENDERMAN, p_386372_));
    public static final Item ENDERMITE_SPAWN_EGG = registerItem("endermite_spawn_egg", p_386355_ -> new SpawnEggItem(EntityType.ENDERMITE, p_386355_));
    public static final Item EVOKER_SPAWN_EGG = registerItem("evoker_spawn_egg", p_386384_ -> new SpawnEggItem(EntityType.EVOKER, p_386384_));
    public static final Item FOX_SPAWN_EGG = registerItem("fox_spawn_egg", p_386356_ -> new SpawnEggItem(EntityType.FOX, p_386356_));
    public static final Item FROG_SPAWN_EGG = registerItem("frog_spawn_egg", p_386387_ -> new SpawnEggItem(EntityType.FROG, p_386387_));
    public static final Item GHAST_SPAWN_EGG = registerItem("ghast_spawn_egg", p_386383_ -> new SpawnEggItem(EntityType.GHAST, p_386383_));
    public static final Item GLOW_SQUID_SPAWN_EGG = registerItem("glow_squid_spawn_egg", p_386345_ -> new SpawnEggItem(EntityType.GLOW_SQUID, p_386345_));
    public static final Item GOAT_SPAWN_EGG = registerItem("goat_spawn_egg", p_386397_ -> new SpawnEggItem(EntityType.GOAT, p_386397_));
    public static final Item GUARDIAN_SPAWN_EGG = registerItem("guardian_spawn_egg", p_386349_ -> new SpawnEggItem(EntityType.GUARDIAN, p_386349_));
    public static final Item HOGLIN_SPAWN_EGG = registerItem("hoglin_spawn_egg", p_386342_ -> new SpawnEggItem(EntityType.HOGLIN, p_386342_));
    public static final Item HORSE_SPAWN_EGG = registerItem("horse_spawn_egg", p_386381_ -> new SpawnEggItem(EntityType.HORSE, p_386381_));
    public static final Item HUSK_SPAWN_EGG = registerItem("husk_spawn_egg", p_386375_ -> new SpawnEggItem(EntityType.HUSK, p_386375_));
    public static final Item IRON_GOLEM_SPAWN_EGG = registerItem("iron_golem_spawn_egg", p_386395_ -> new SpawnEggItem(EntityType.IRON_GOLEM, p_386395_));
    public static final Item LLAMA_SPAWN_EGG = registerItem("llama_spawn_egg", p_386404_ -> new SpawnEggItem(EntityType.LLAMA, p_386404_));
    public static final Item MAGMA_CUBE_SPAWN_EGG = registerItem("magma_cube_spawn_egg", p_386343_ -> new SpawnEggItem(EntityType.MAGMA_CUBE, p_386343_));
    public static final Item MOOSHROOM_SPAWN_EGG = registerItem("mooshroom_spawn_egg", p_386396_ -> new SpawnEggItem(EntityType.MOOSHROOM, p_386396_));
    public static final Item MULE_SPAWN_EGG = registerItem("mule_spawn_egg", p_386361_ -> new SpawnEggItem(EntityType.MULE, p_386361_));
    public static final Item OCELOT_SPAWN_EGG = registerItem("ocelot_spawn_egg", p_386371_ -> new SpawnEggItem(EntityType.OCELOT, p_386371_));
    public static final Item PANDA_SPAWN_EGG = registerItem("panda_spawn_egg", p_386413_ -> new SpawnEggItem(EntityType.PANDA, p_386413_));
    public static final Item PARROT_SPAWN_EGG = registerItem("parrot_spawn_egg", p_386359_ -> new SpawnEggItem(EntityType.PARROT, p_386359_));
    public static final Item PHANTOM_SPAWN_EGG = registerItem("phantom_spawn_egg", p_386403_ -> new SpawnEggItem(EntityType.PHANTOM, p_386403_));
    public static final Item PIG_SPAWN_EGG = registerItem("pig_spawn_egg", p_386393_ -> new SpawnEggItem(EntityType.PIG, p_386393_));
    public static final Item PIGLIN_SPAWN_EGG = registerItem("piglin_spawn_egg", p_386350_ -> new SpawnEggItem(EntityType.PIGLIN, p_386350_));
    public static final Item PIGLIN_BRUTE_SPAWN_EGG = registerItem("piglin_brute_spawn_egg", p_386369_ -> new SpawnEggItem(EntityType.PIGLIN_BRUTE, p_386369_));
    public static final Item PILLAGER_SPAWN_EGG = registerItem("pillager_spawn_egg", p_386346_ -> new SpawnEggItem(EntityType.PILLAGER, p_386346_));
    public static final Item POLAR_BEAR_SPAWN_EGG = registerItem("polar_bear_spawn_egg", p_386337_ -> new SpawnEggItem(EntityType.POLAR_BEAR, p_386337_));
    public static final Item PUFFERFISH_SPAWN_EGG = registerItem("pufferfish_spawn_egg", p_386374_ -> new SpawnEggItem(EntityType.PUFFERFISH, p_386374_));
    public static final Item RABBIT_SPAWN_EGG = registerItem("rabbit_spawn_egg", p_386357_ -> new SpawnEggItem(EntityType.RABBIT, p_386357_));
    public static final Item RAVAGER_SPAWN_EGG = registerItem("ravager_spawn_egg", p_386360_ -> new SpawnEggItem(EntityType.RAVAGER, p_386360_));
    public static final Item SALMON_SPAWN_EGG = registerItem("salmon_spawn_egg", p_386378_ -> new SpawnEggItem(EntityType.SALMON, p_386378_));
    public static final Item SHEEP_SPAWN_EGG = registerItem("sheep_spawn_egg", p_386362_ -> new SpawnEggItem(EntityType.SHEEP, p_386362_));
    public static final Item SHULKER_SPAWN_EGG = registerItem("shulker_spawn_egg", p_386415_ -> new SpawnEggItem(EntityType.SHULKER, p_386415_));
    public static final Item SILVERFISH_SPAWN_EGG = registerItem("silverfish_spawn_egg", p_386399_ -> new SpawnEggItem(EntityType.SILVERFISH, p_386399_));
    public static final Item SKELETON_SPAWN_EGG = registerItem("skeleton_spawn_egg", p_386358_ -> new SpawnEggItem(EntityType.SKELETON, p_386358_));
    public static final Item SKELETON_HORSE_SPAWN_EGG = registerItem(
        "skeleton_horse_spawn_egg", p_386338_ -> new SpawnEggItem(EntityType.SKELETON_HORSE, p_386338_)
    );
    public static final Item SLIME_SPAWN_EGG = registerItem("slime_spawn_egg", p_386410_ -> new SpawnEggItem(EntityType.SLIME, p_386410_));
    public static final Item SNIFFER_SPAWN_EGG = registerItem("sniffer_spawn_egg", p_386400_ -> new SpawnEggItem(EntityType.SNIFFER, p_386400_));
    public static final Item SNOW_GOLEM_SPAWN_EGG = registerItem("snow_golem_spawn_egg", p_386354_ -> new SpawnEggItem(EntityType.SNOW_GOLEM, p_386354_));
    public static final Item SPIDER_SPAWN_EGG = registerItem("spider_spawn_egg", p_386416_ -> new SpawnEggItem(EntityType.SPIDER, p_386416_));
    public static final Item SQUID_SPAWN_EGG = registerItem("squid_spawn_egg", p_386347_ -> new SpawnEggItem(EntityType.SQUID, p_386347_));
    public static final Item STRAY_SPAWN_EGG = registerItem("stray_spawn_egg", p_386390_ -> new SpawnEggItem(EntityType.STRAY, p_386390_));
    public static final Item STRIDER_SPAWN_EGG = registerItem("strider_spawn_egg", p_386353_ -> new SpawnEggItem(EntityType.STRIDER, p_386353_));
    public static final Item TADPOLE_SPAWN_EGG = registerItem("tadpole_spawn_egg", p_386386_ -> new SpawnEggItem(EntityType.TADPOLE, p_386386_));
    public static final Item TRADER_LLAMA_SPAWN_EGG = registerItem("trader_llama_spawn_egg", p_386344_ -> new SpawnEggItem(EntityType.TRADER_LLAMA, p_386344_));
    public static final Item TROPICAL_FISH_SPAWN_EGG = registerItem(
        "tropical_fish_spawn_egg", p_386414_ -> new SpawnEggItem(EntityType.TROPICAL_FISH, p_386414_)
    );
    public static final Item TURTLE_SPAWN_EGG = registerItem("turtle_spawn_egg", p_386412_ -> new SpawnEggItem(EntityType.TURTLE, p_386412_));
    public static final Item VEX_SPAWN_EGG = registerItem("vex_spawn_egg", p_386368_ -> new SpawnEggItem(EntityType.VEX, p_386368_));
    public static final Item VILLAGER_SPAWN_EGG = registerItem("villager_spawn_egg", p_386363_ -> new SpawnEggItem(EntityType.VILLAGER, p_386363_));
    public static final Item VINDICATOR_SPAWN_EGG = registerItem("vindicator_spawn_egg", p_386339_ -> new SpawnEggItem(EntityType.VINDICATOR, p_386339_));
    public static final Item WANDERING_TRADER_SPAWN_EGG = registerItem(
        "wandering_trader_spawn_egg", p_386348_ -> new SpawnEggItem(EntityType.WANDERING_TRADER, p_386348_)
    );
    public static final Item WARDEN_SPAWN_EGG = registerItem("warden_spawn_egg", p_386341_ -> new SpawnEggItem(EntityType.WARDEN, p_386341_));
    public static final Item WITCH_SPAWN_EGG = registerItem("witch_spawn_egg", p_386370_ -> new SpawnEggItem(EntityType.WITCH, p_386370_));
    public static final Item WITHER_SPAWN_EGG = registerItem("wither_spawn_egg", p_386367_ -> new SpawnEggItem(EntityType.WITHER, p_386367_));
    public static final Item WITHER_SKELETON_SPAWN_EGG = registerItem(
        "wither_skeleton_spawn_egg", p_386401_ -> new SpawnEggItem(EntityType.WITHER_SKELETON, p_386401_)
    );
    public static final Item WOLF_SPAWN_EGG = registerItem("wolf_spawn_egg", p_386351_ -> new SpawnEggItem(EntityType.WOLF, p_386351_));
    public static final Item ZOGLIN_SPAWN_EGG = registerItem("zoglin_spawn_egg", p_386389_ -> new SpawnEggItem(EntityType.ZOGLIN, p_386389_));
    public static final Item CREAKING_SPAWN_EGG = registerItem("creaking_spawn_egg", p_386409_ -> new SpawnEggItem(EntityType.CREAKING, p_386409_));
    public static final Item ZOMBIE_SPAWN_EGG = registerItem("zombie_spawn_egg", p_386391_ -> new SpawnEggItem(EntityType.ZOMBIE, p_386391_));
    public static final Item ZOMBIE_HORSE_SPAWN_EGG = registerItem("zombie_horse_spawn_egg", p_386402_ -> new SpawnEggItem(EntityType.ZOMBIE_HORSE, p_386402_));
    public static final Item ZOMBIE_VILLAGER_SPAWN_EGG = registerItem(
        "zombie_villager_spawn_egg", p_386373_ -> new SpawnEggItem(EntityType.ZOMBIE_VILLAGER, p_386373_)
    );
    public static final Item ZOMBIFIED_PIGLIN_SPAWN_EGG = registerItem(
        "zombified_piglin_spawn_egg", p_386335_ -> new SpawnEggItem(EntityType.ZOMBIFIED_PIGLIN, p_386335_)
    );
    public static final Item EXPERIENCE_BOTTLE = registerItem(
        "experience_bottle",
        ExperienceBottleItem::new,
        new Item.Properties().rarity(Rarity.UNCOMMON).component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)
    );
    public static final Item FIRE_CHARGE = registerItem("fire_charge", FireChargeItem::new);
    public static final Item WIND_CHARGE = registerItem("wind_charge", WindChargeItem::new, new Item.Properties().useCooldown(0.5F));
    public static final Item WRITABLE_BOOK = registerItem(
        "writable_book", WritableBookItem::new, new Item.Properties().stacksTo(1).component(DataComponents.WRITABLE_BOOK_CONTENT, WritableBookContent.EMPTY)
    );
    public static final Item WRITTEN_BOOK = registerItem(
        "written_book", WrittenBookItem::new, new Item.Properties().stacksTo(16).component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)
    );
    public static final Item BREEZE_ROD = registerItem("breeze_rod");
    public static final Item MACE = registerItem(
        "mace",
        MaceItem::new,
        new Item.Properties()
            .rarity(Rarity.EPIC)
            .durability(500)
            .component(DataComponents.TOOL, MaceItem.createToolProperties())
            .repairable(BREEZE_ROD)
            .attributes(MaceItem.createAttributes())
            .enchantable(15)
    );
    public static final Item ITEM_FRAME = registerItem("item_frame", p_370869_ -> new ItemFrameItem(EntityType.ITEM_FRAME, p_370869_));
    public static final Item GLOW_ITEM_FRAME = registerItem("glow_item_frame", p_371036_ -> new ItemFrameItem(EntityType.GLOW_ITEM_FRAME, p_371036_));
    public static final Item FLOWER_POT = registerBlock(Blocks.FLOWER_POT);
    public static final Item CARROT = registerItem("carrot", createBlockItemWithCustomItemName(Blocks.CARROTS), new Item.Properties().food(Foods.CARROT));
    public static final Item POTATO = registerItem("potato", createBlockItemWithCustomItemName(Blocks.POTATOES), new Item.Properties().food(Foods.POTATO));
    public static final Item BAKED_POTATO = registerItem("baked_potato", new Item.Properties().food(Foods.BAKED_POTATO));
    public static final Item POISONOUS_POTATO = registerItem(
        "poisonous_potato", new Item.Properties().food(Foods.POISONOUS_POTATO, Consumables.POISONOUS_POTATO)
    );
    public static final Item MAP = registerItem("map", EmptyMapItem::new);
    public static final Item GOLDEN_CARROT = registerItem("golden_carrot", new Item.Properties().food(Foods.GOLDEN_CARROT));
    public static final Item SKELETON_SKULL = registerBlock(
        Blocks.SKELETON_SKULL,
        (p_370812_, p_370813_) -> new StandingAndWallBlockItem(p_370812_, Blocks.SKELETON_WALL_SKULL, Direction.DOWN, p_370813_),
        new Item.Properties().rarity(Rarity.UNCOMMON).equippableUnswappable(EquipmentSlot.HEAD)
    );
    public static final Item WITHER_SKELETON_SKULL = registerBlock(
        Blocks.WITHER_SKELETON_SKULL,
        (p_370881_, p_370882_) -> new StandingAndWallBlockItem(p_370881_, Blocks.WITHER_SKELETON_WALL_SKULL, Direction.DOWN, p_370882_),
        new Item.Properties().rarity(Rarity.RARE).equippableUnswappable(EquipmentSlot.HEAD)
    );
    public static final Item PLAYER_HEAD = registerBlock(
        Blocks.PLAYER_HEAD,
        (p_370839_, p_370840_) -> new PlayerHeadItem(p_370839_, Blocks.PLAYER_WALL_HEAD, p_370840_),
        new Item.Properties().rarity(Rarity.UNCOMMON).equippableUnswappable(EquipmentSlot.HEAD)
    );
    public static final Item ZOMBIE_HEAD = registerBlock(
        Blocks.ZOMBIE_HEAD,
        (p_371078_, p_371079_) -> new StandingAndWallBlockItem(p_371078_, Blocks.ZOMBIE_WALL_HEAD, Direction.DOWN, p_371079_),
        new Item.Properties().rarity(Rarity.UNCOMMON).equippableUnswappable(EquipmentSlot.HEAD)
    );
    public static final Item CREEPER_HEAD = registerBlock(
        Blocks.CREEPER_HEAD,
        (p_370757_, p_370758_) -> new StandingAndWallBlockItem(p_370757_, Blocks.CREEPER_WALL_HEAD, Direction.DOWN, p_370758_),
        new Item.Properties().rarity(Rarity.UNCOMMON).equippableUnswappable(EquipmentSlot.HEAD)
    );
    public static final Item DRAGON_HEAD = registerBlock(
        Blocks.DRAGON_HEAD,
        (p_370927_, p_370928_) -> new StandingAndWallBlockItem(p_370927_, Blocks.DRAGON_WALL_HEAD, Direction.DOWN, p_370928_),
        new Item.Properties().rarity(Rarity.EPIC).equippableUnswappable(EquipmentSlot.HEAD)
    );
    public static final Item PIGLIN_HEAD = registerBlock(
        Blocks.PIGLIN_HEAD,
        (p_370851_, p_370852_) -> new StandingAndWallBlockItem(p_370851_, Blocks.PIGLIN_WALL_HEAD, Direction.DOWN, p_370852_),
        new Item.Properties().rarity(Rarity.UNCOMMON).equippableUnswappable(EquipmentSlot.HEAD)
    );
    public static final Item NETHER_STAR = registerItem(
        "nether_star",
        new Item.Properties()
            .rarity(Rarity.RARE)
            .component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)
            .component(DataComponents.DAMAGE_RESISTANT, new DamageResistant(DamageTypeTags.IS_EXPLOSION))
    );
    public static final Item PUMPKIN_PIE = registerItem("pumpkin_pie", new Item.Properties().food(Foods.PUMPKIN_PIE));
    public static final Item FIREWORK_ROCKET = registerItem(
        "firework_rocket", FireworkRocketItem::new, new Item.Properties().component(DataComponents.FIREWORKS, new Fireworks(1, List.of()))
    );
    public static final Item FIREWORK_STAR = registerItem("firework_star", FireworkStarItem::new);
    public static final Item ENCHANTED_BOOK = registerItem(
        "enchanted_book",
        new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.UNCOMMON)
            .component(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY)
            .component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)
    );
    public static final Item NETHER_BRICK = registerItem("nether_brick");
    public static final Item RESIN_BRICK = registerItem("resin_brick");
    public static final Item PRISMARINE_SHARD = registerItem("prismarine_shard");
    public static final Item PRISMARINE_CRYSTALS = registerItem("prismarine_crystals");
    public static final Item RABBIT = registerItem("rabbit", new Item.Properties().food(Foods.RABBIT));
    public static final Item COOKED_RABBIT = registerItem("cooked_rabbit", new Item.Properties().food(Foods.COOKED_RABBIT));
    public static final Item RABBIT_STEW = registerItem("rabbit_stew", new Item.Properties().stacksTo(1).food(Foods.RABBIT_STEW).usingConvertsTo(BOWL));
    public static final Item RABBIT_FOOT = registerItem("rabbit_foot");
    public static final Item RABBIT_HIDE = registerItem("rabbit_hide");
    public static final Item ARMOR_STAND = registerItem("armor_stand", ArmorStandItem::new, new Item.Properties().stacksTo(16));
    public static final Item IRON_HORSE_ARMOR = registerItem(
        "iron_horse_armor",
        p_381577_ -> new AnimalArmorItem(ArmorMaterials.IRON, AnimalArmorItem.BodyType.EQUESTRIAN, SoundEvents.HORSE_ARMOR, false, p_381577_),
        new Item.Properties().stacksTo(1)
    );
    public static final Item GOLDEN_HORSE_ARMOR = registerItem(
        "golden_horse_armor",
        p_381576_ -> new AnimalArmorItem(ArmorMaterials.GOLD, AnimalArmorItem.BodyType.EQUESTRIAN, SoundEvents.HORSE_ARMOR, false, p_381576_),
        new Item.Properties().stacksTo(1)
    );
    public static final Item DIAMOND_HORSE_ARMOR = registerItem(
        "diamond_horse_armor",
        p_381575_ -> new AnimalArmorItem(ArmorMaterials.DIAMOND, AnimalArmorItem.BodyType.EQUESTRIAN, SoundEvents.HORSE_ARMOR, false, p_381575_),
        new Item.Properties().stacksTo(1)
    );
    public static final Item LEATHER_HORSE_ARMOR = registerItem(
        "leather_horse_armor",
        p_381574_ -> new AnimalArmorItem(ArmorMaterials.LEATHER, AnimalArmorItem.BodyType.EQUESTRIAN, SoundEvents.HORSE_ARMOR, false, p_381574_),
        new Item.Properties().stacksTo(1)
    );
    public static final Item LEAD = registerItem("lead", LeadItem::new);
    public static final Item NAME_TAG = registerItem("name_tag", NameTagItem::new);
    public static final Item COMMAND_BLOCK_MINECART = registerItem(
        "command_block_minecart",
        p_373930_ -> new MinecartItem(EntityType.COMMAND_BLOCK_MINECART, p_373930_),
        new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)
    );
    public static final Item MUTTON = registerItem("mutton", new Item.Properties().food(Foods.MUTTON));
    public static final Item COOKED_MUTTON = registerItem("cooked_mutton", new Item.Properties().food(Foods.COOKED_MUTTON));
    public static final Item WHITE_BANNER = registerBlock(
        Blocks.WHITE_BANNER,
        (p_371066_, p_371067_) -> new BannerItem(p_371066_, Blocks.WHITE_WALL_BANNER, p_371067_),
        new Item.Properties().stacksTo(16).component(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY)
    );
    public static final Item ORANGE_BANNER = registerBlock(
        Blocks.ORANGE_BANNER,
        (p_370995_, p_370996_) -> new BannerItem(p_370995_, Blocks.ORANGE_WALL_BANNER, p_370996_),
        new Item.Properties().stacksTo(16).component(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY)
    );
    public static final Item MAGENTA_BANNER = registerBlock(
        Blocks.MAGENTA_BANNER,
        (p_370777_, p_370778_) -> new BannerItem(p_370777_, Blocks.MAGENTA_WALL_BANNER, p_370778_),
        new Item.Properties().stacksTo(16).component(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY)
    );
    public static final Item LIGHT_BLUE_BANNER = registerBlock(
        Blocks.LIGHT_BLUE_BANNER,
        (p_370832_, p_370833_) -> new BannerItem(p_370832_, Blocks.LIGHT_BLUE_WALL_BANNER, p_370833_),
        new Item.Properties().stacksTo(16).component(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY)
    );
    public static final Item YELLOW_BANNER = registerBlock(
        Blocks.YELLOW_BANNER,
        (p_370843_, p_370844_) -> new BannerItem(p_370843_, Blocks.YELLOW_WALL_BANNER, p_370844_),
        new Item.Properties().stacksTo(16).component(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY)
    );
    public static final Item LIME_BANNER = registerBlock(
        Blocks.LIME_BANNER,
        (p_371082_, p_371083_) -> new BannerItem(p_371082_, Blocks.LIME_WALL_BANNER, p_371083_),
        new Item.Properties().stacksTo(16).component(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY)
    );
    public static final Item PINK_BANNER = registerBlock(
        Blocks.PINK_BANNER,
        (p_371047_, p_371048_) -> new BannerItem(p_371047_, Blocks.PINK_WALL_BANNER, p_371048_),
        new Item.Properties().stacksTo(16).component(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY)
    );
    public static final Item GRAY_BANNER = registerBlock(
        Blocks.GRAY_BANNER,
        (p_371005_, p_371006_) -> new BannerItem(p_371005_, Blocks.GRAY_WALL_BANNER, p_371006_),
        new Item.Properties().stacksTo(16).component(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY)
    );
    public static final Item LIGHT_GRAY_BANNER = registerBlock(
        Blocks.LIGHT_GRAY_BANNER,
        (p_370960_, p_370961_) -> new BannerItem(p_370960_, Blocks.LIGHT_GRAY_WALL_BANNER, p_370961_),
        new Item.Properties().stacksTo(16).component(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY)
    );
    public static final Item CYAN_BANNER = registerBlock(
        Blocks.CYAN_BANNER,
        (p_370935_, p_370936_) -> new BannerItem(p_370935_, Blocks.CYAN_WALL_BANNER, p_370936_),
        new Item.Properties().stacksTo(16).component(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY)
    );
    public static final Item PURPLE_BANNER = registerBlock(
        Blocks.PURPLE_BANNER,
        (p_371063_, p_371064_) -> new BannerItem(p_371063_, Blocks.PURPLE_WALL_BANNER, p_371064_),
        new Item.Properties().stacksTo(16).component(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY)
    );
    public static final Item BLUE_BANNER = registerBlock(
        Blocks.BLUE_BANNER,
        (p_370845_, p_370846_) -> new BannerItem(p_370845_, Blocks.BLUE_WALL_BANNER, p_370846_),
        new Item.Properties().stacksTo(16).component(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY)
    );
    public static final Item BROWN_BANNER = registerBlock(
        Blocks.BROWN_BANNER,
        (p_370789_, p_370790_) -> new BannerItem(p_370789_, Blocks.BROWN_WALL_BANNER, p_370790_),
        new Item.Properties().stacksTo(16).component(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY)
    );
    public static final Item GREEN_BANNER = registerBlock(
        Blocks.GREEN_BANNER,
        (p_370993_, p_370994_) -> new BannerItem(p_370993_, Blocks.GREEN_WALL_BANNER, p_370994_),
        new Item.Properties().stacksTo(16).component(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY)
    );
    public static final Item RED_BANNER = registerBlock(
        Blocks.RED_BANNER,
        (p_370754_, p_370755_) -> new BannerItem(p_370754_, Blocks.RED_WALL_BANNER, p_370755_),
        new Item.Properties().stacksTo(16).component(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY)
    );
    public static final Item BLACK_BANNER = registerBlock(
        Blocks.BLACK_BANNER,
        (p_371027_, p_371028_) -> new BannerItem(p_371027_, Blocks.BLACK_WALL_BANNER, p_371028_),
        new Item.Properties().stacksTo(16).component(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY)
    );
    public static final Item END_CRYSTAL = registerItem(
        "end_crystal", EndCrystalItem::new, new Item.Properties().component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)
    );
    public static final Item CHORUS_FRUIT = registerItem(
        "chorus_fruit", new Item.Properties().food(Foods.CHORUS_FRUIT, Consumables.CHORUS_FRUIT).useCooldown(1.0F)
    );
    public static final Item POPPED_CHORUS_FRUIT = registerItem("popped_chorus_fruit");
    public static final Item TORCHFLOWER_SEEDS = registerItem("torchflower_seeds", createBlockItemWithCustomItemName(Blocks.TORCHFLOWER_CROP));
    public static final Item PITCHER_POD = registerItem("pitcher_pod", createBlockItemWithCustomItemName(Blocks.PITCHER_CROP));
    public static final Item BEETROOT = registerItem("beetroot", new Item.Properties().food(Foods.BEETROOT));
    public static final Item BEETROOT_SEEDS = registerItem("beetroot_seeds", createBlockItemWithCustomItemName(Blocks.BEETROOTS));
    public static final Item BEETROOT_SOUP = registerItem("beetroot_soup", new Item.Properties().stacksTo(1).food(Foods.BEETROOT_SOUP).usingConvertsTo(BOWL));
    public static final Item DRAGON_BREATH = registerItem("dragon_breath", new Item.Properties().craftRemainder(GLASS_BOTTLE).rarity(Rarity.UNCOMMON));
    public static final Item SPLASH_POTION = registerItem(
        "splash_potion", SplashPotionItem::new, new Item.Properties().stacksTo(1).component(DataComponents.POTION_CONTENTS, PotionContents.EMPTY)
    );
    public static final Item SPECTRAL_ARROW = registerItem("spectral_arrow", SpectralArrowItem::new);
    public static final Item TIPPED_ARROW = registerItem(
        "tipped_arrow", TippedArrowItem::new, new Item.Properties().component(DataComponents.POTION_CONTENTS, PotionContents.EMPTY)
    );
    public static final Item LINGERING_POTION = registerItem(
        "lingering_potion", LingeringPotionItem::new, new Item.Properties().stacksTo(1).component(DataComponents.POTION_CONTENTS, PotionContents.EMPTY)
    );
    public static final Item SHIELD = registerItem(
        "shield",
        ShieldItem::new,
        new Item.Properties()
            .durability(336)
            .component(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY)
            .repairable(ItemTags.WOODEN_TOOL_MATERIALS)
            .equippableUnswappable(EquipmentSlot.OFFHAND)
    );
    public static final Item TOTEM_OF_UNDYING = registerItem(
        "totem_of_undying",
        new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).component(DataComponents.DEATH_PROTECTION, DeathProtection.TOTEM_OF_UNDYING)
    );
    public static final Item SHULKER_SHELL = registerItem("shulker_shell");
    public static final Item IRON_NUGGET = registerItem("iron_nugget");
    public static final Item KNOWLEDGE_BOOK = registerItem(
        "knowledge_book", KnowledgeBookItem::new, new Item.Properties().stacksTo(1).rarity(Rarity.EPIC).component(DataComponents.RECIPES, List.of())
    );
    public static final Item DEBUG_STICK = registerItem(
        "debug_stick",
        DebugStickItem::new,
        new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.EPIC)
            .component(DataComponents.DEBUG_STICK_STATE, DebugStickState.EMPTY)
            .component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)
    );
    public static final Item MUSIC_DISC_13 = registerItem(
        "music_disc_13", new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).jukeboxPlayable(JukeboxSongs.THIRTEEN)
    );
    public static final Item MUSIC_DISC_CAT = registerItem(
        "music_disc_cat", new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).jukeboxPlayable(JukeboxSongs.CAT)
    );
    public static final Item MUSIC_DISC_BLOCKS = registerItem(
        "music_disc_blocks", new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).jukeboxPlayable(JukeboxSongs.BLOCKS)
    );
    public static final Item MUSIC_DISC_CHIRP = registerItem(
        "music_disc_chirp", new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).jukeboxPlayable(JukeboxSongs.CHIRP)
    );
    public static final Item MUSIC_DISC_CREATOR = registerItem(
        "music_disc_creator", new Item.Properties().stacksTo(1).rarity(Rarity.RARE).jukeboxPlayable(JukeboxSongs.CREATOR)
    );
    public static final Item MUSIC_DISC_CREATOR_MUSIC_BOX = registerItem(
        "music_disc_creator_music_box", new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).jukeboxPlayable(JukeboxSongs.CREATOR_MUSIC_BOX)
    );
    public static final Item MUSIC_DISC_FAR = registerItem(
        "music_disc_far", new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).jukeboxPlayable(JukeboxSongs.FAR)
    );
    public static final Item MUSIC_DISC_MALL = registerItem(
        "music_disc_mall", new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).jukeboxPlayable(JukeboxSongs.MALL)
    );
    public static final Item MUSIC_DISC_MELLOHI = registerItem(
        "music_disc_mellohi", new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).jukeboxPlayable(JukeboxSongs.MELLOHI)
    );
    public static final Item MUSIC_DISC_STAL = registerItem(
        "music_disc_stal", new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).jukeboxPlayable(JukeboxSongs.STAL)
    );
    public static final Item MUSIC_DISC_STRAD = registerItem(
        "music_disc_strad", new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).jukeboxPlayable(JukeboxSongs.STRAD)
    );
    public static final Item MUSIC_DISC_WARD = registerItem(
        "music_disc_ward", new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).jukeboxPlayable(JukeboxSongs.WARD)
    );
    public static final Item MUSIC_DISC_11 = registerItem(
        "music_disc_11", new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).jukeboxPlayable(JukeboxSongs.ELEVEN)
    );
    public static final Item MUSIC_DISC_WAIT = registerItem(
        "music_disc_wait", new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).jukeboxPlayable(JukeboxSongs.WAIT)
    );
    public static final Item MUSIC_DISC_OTHERSIDE = registerItem(
        "music_disc_otherside", new Item.Properties().stacksTo(1).rarity(Rarity.RARE).jukeboxPlayable(JukeboxSongs.OTHERSIDE)
    );
    public static final Item MUSIC_DISC_RELIC = registerItem(
        "music_disc_relic", new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).jukeboxPlayable(JukeboxSongs.RELIC)
    );
    public static final Item MUSIC_DISC_5 = registerItem(
        "music_disc_5", new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).jukeboxPlayable(JukeboxSongs.FIVE)
    );
    public static final Item MUSIC_DISC_PIGSTEP = registerItem(
        "music_disc_pigstep", new Item.Properties().stacksTo(1).rarity(Rarity.RARE).jukeboxPlayable(JukeboxSongs.PIGSTEP)
    );
    public static final Item MUSIC_DISC_PRECIPICE = registerItem(
        "music_disc_precipice", new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).jukeboxPlayable(JukeboxSongs.PRECIPICE)
    );
    public static final Item DISC_FRAGMENT_5 = registerItem("disc_fragment_5", DiscFragmentItem::new, new Item.Properties().rarity(Rarity.UNCOMMON));
    public static final Item TRIDENT = registerItem(
        "trident",
        TridentItem::new,
        new Item.Properties()
            .rarity(Rarity.RARE)
            .durability(250)
            .attributes(TridentItem.createAttributes())
            .component(DataComponents.TOOL, TridentItem.createToolProperties())
            .enchantable(1)
    );
    public static final Item NAUTILUS_SHELL = registerItem("nautilus_shell", new Item.Properties().rarity(Rarity.UNCOMMON));
    public static final Item HEART_OF_THE_SEA = registerItem("heart_of_the_sea", new Item.Properties().rarity(Rarity.UNCOMMON));
    public static final Item CROSSBOW = registerItem(
        "crossbow",
        CrossbowItem::new,
        new Item.Properties().stacksTo(1).durability(465).component(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY).enchantable(1)
    );
    public static final Item SUSPICIOUS_STEW = registerItem(
        "suspicious_stew",
        new Item.Properties()
            .stacksTo(1)
            .food(Foods.SUSPICIOUS_STEW)
            .component(DataComponents.SUSPICIOUS_STEW_EFFECTS, SuspiciousStewEffects.EMPTY)
            .usingConvertsTo(BOWL)
    );
    public static final Item LOOM = registerBlock(Blocks.LOOM);
    public static final Item FLOWER_BANNER_PATTERN = registerItem(
        "flower_banner_pattern", p_370766_ -> new BannerPatternItem(BannerPatternTags.PATTERN_ITEM_FLOWER, p_370766_), new Item.Properties().stacksTo(1)
    );
    public static final Item CREEPER_BANNER_PATTERN = registerItem(
        "creeper_banner_pattern",
        p_370861_ -> new BannerPatternItem(BannerPatternTags.PATTERN_ITEM_CREEPER, p_370861_),
        new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)
    );
    public static final Item SKULL_BANNER_PATTERN = registerItem(
        "skull_banner_pattern",
        p_371061_ -> new BannerPatternItem(BannerPatternTags.PATTERN_ITEM_SKULL, p_371061_),
        new Item.Properties().stacksTo(1).rarity(Rarity.RARE)
    );
    public static final Item MOJANG_BANNER_PATTERN = registerItem(
        "mojang_banner_pattern",
        p_370919_ -> new BannerPatternItem(BannerPatternTags.PATTERN_ITEM_MOJANG, p_370919_),
        new Item.Properties().stacksTo(1).rarity(Rarity.RARE)
    );
    public static final Item GLOBE_BANNER_PATTERN = registerItem(
        "globe_banner_pattern", p_370837_ -> new BannerPatternItem(BannerPatternTags.PATTERN_ITEM_GLOBE, p_370837_), new Item.Properties().stacksTo(1)
    );
    public static final Item PIGLIN_BANNER_PATTERN = registerItem(
        "piglin_banner_pattern",
        p_370862_ -> new BannerPatternItem(BannerPatternTags.PATTERN_ITEM_PIGLIN, p_370862_),
        new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)
    );
    public static final Item FLOW_BANNER_PATTERN = registerItem(
        "flow_banner_pattern",
        p_370834_ -> new BannerPatternItem(BannerPatternTags.PATTERN_ITEM_FLOW, p_370834_),
        new Item.Properties().stacksTo(1).rarity(Rarity.RARE)
    );
    public static final Item GUSTER_BANNER_PATTERN = registerItem(
        "guster_banner_pattern",
        p_370799_ -> new BannerPatternItem(BannerPatternTags.PATTERN_ITEM_GUSTER, p_370799_),
        new Item.Properties().stacksTo(1).rarity(Rarity.RARE)
    );
    public static final Item FIELD_MASONED_BANNER_PATTERN = registerItem(
        "field_masoned_banner_pattern",
        p_370978_ -> new BannerPatternItem(BannerPatternTags.PATTERN_ITEM_FIELD_MASONED, p_370978_),
        new Item.Properties().stacksTo(1)
    );
    public static final Item BORDURE_INDENTED_BANNER_PATTERN = registerItem(
        "bordure_indented_banner_pattern",
        p_370859_ -> new BannerPatternItem(BannerPatternTags.PATTERN_ITEM_BORDURE_INDENTED, p_370859_),
        new Item.Properties().stacksTo(1)
    );
    public static final Item GOAT_HORN = registerItem(
        "goat_horn", p_370939_ -> new InstrumentItem(InstrumentTags.GOAT_HORNS, p_370939_), new Item.Properties().rarity(Rarity.UNCOMMON).stacksTo(1)
    );
    public static final Item COMPOSTER = registerBlock(Blocks.COMPOSTER);
    public static final Item BARREL = registerBlock(Blocks.BARREL, p_332802_ -> p_332802_.component(DataComponents.CONTAINER, ItemContainerContents.EMPTY));
    public static final Item SMOKER = registerBlock(Blocks.SMOKER, p_332697_ -> p_332697_.component(DataComponents.CONTAINER, ItemContainerContents.EMPTY));
    public static final Item BLAST_FURNACE = registerBlock(
        Blocks.BLAST_FURNACE, p_332832_ -> p_332832_.component(DataComponents.CONTAINER, ItemContainerContents.EMPTY)
    );
    public static final Item CARTOGRAPHY_TABLE = registerBlock(Blocks.CARTOGRAPHY_TABLE);
    public static final Item FLETCHING_TABLE = registerBlock(Blocks.FLETCHING_TABLE);
    public static final Item GRINDSTONE = registerBlock(Blocks.GRINDSTONE);
    public static final Item SMITHING_TABLE = registerBlock(Blocks.SMITHING_TABLE);
    public static final Item STONECUTTER = registerBlock(Blocks.STONECUTTER);
    public static final Item BELL = registerBlock(Blocks.BELL);
    public static final Item LANTERN = registerBlock(Blocks.LANTERN);
    public static final Item SOUL_LANTERN = registerBlock(Blocks.SOUL_LANTERN);
    public static final Item SWEET_BERRIES = registerItem(
        "sweet_berries", createBlockItemWithCustomItemName(Blocks.SWEET_BERRY_BUSH), new Item.Properties().food(Foods.SWEET_BERRIES)
    );
    public static final Item GLOW_BERRIES = registerItem(
        "glow_berries", createBlockItemWithCustomItemName(Blocks.CAVE_VINES), new Item.Properties().food(Foods.GLOW_BERRIES)
    );
    public static final Item CAMPFIRE = registerBlock(Blocks.CAMPFIRE, p_332753_ -> p_332753_.component(DataComponents.CONTAINER, ItemContainerContents.EMPTY));
    public static final Item SOUL_CAMPFIRE = registerBlock(
        Blocks.SOUL_CAMPFIRE, p_332794_ -> p_332794_.component(DataComponents.CONTAINER, ItemContainerContents.EMPTY)
    );
    public static final Item SHROOMLIGHT = registerBlock(Blocks.SHROOMLIGHT);
    public static final Item HONEYCOMB = registerItem("honeycomb", HoneycombItem::new);
    public static final Item BEE_NEST = registerBlock(Blocks.BEE_NEST, new Item.Properties().component(DataComponents.BEES, List.of()));
    public static final Item BEEHIVE = registerBlock(Blocks.BEEHIVE, new Item.Properties().component(DataComponents.BEES, List.of()));
    public static final Item HONEY_BOTTLE = registerItem(
        "honey_bottle",
        new Item.Properties().craftRemainder(GLASS_BOTTLE).food(Foods.HONEY_BOTTLE, Consumables.HONEY_BOTTLE).usingConvertsTo(GLASS_BOTTLE).stacksTo(16)
    );
    public static final Item HONEYCOMB_BLOCK = registerBlock(Blocks.HONEYCOMB_BLOCK);
    public static final Item LODESTONE = registerBlock(Blocks.LODESTONE);
    public static final Item CRYING_OBSIDIAN = registerBlock(Blocks.CRYING_OBSIDIAN);
    public static final Item BLACKSTONE = registerBlock(Blocks.BLACKSTONE);
    public static final Item BLACKSTONE_SLAB = registerBlock(Blocks.BLACKSTONE_SLAB);
    public static final Item BLACKSTONE_STAIRS = registerBlock(Blocks.BLACKSTONE_STAIRS);
    public static final Item GILDED_BLACKSTONE = registerBlock(Blocks.GILDED_BLACKSTONE);
    public static final Item POLISHED_BLACKSTONE = registerBlock(Blocks.POLISHED_BLACKSTONE);
    public static final Item POLISHED_BLACKSTONE_SLAB = registerBlock(Blocks.POLISHED_BLACKSTONE_SLAB);
    public static final Item POLISHED_BLACKSTONE_STAIRS = registerBlock(Blocks.POLISHED_BLACKSTONE_STAIRS);
    public static final Item CHISELED_POLISHED_BLACKSTONE = registerBlock(Blocks.CHISELED_POLISHED_BLACKSTONE);
    public static final Item POLISHED_BLACKSTONE_BRICKS = registerBlock(Blocks.POLISHED_BLACKSTONE_BRICKS);
    public static final Item POLISHED_BLACKSTONE_BRICK_SLAB = registerBlock(Blocks.POLISHED_BLACKSTONE_BRICK_SLAB);
    public static final Item POLISHED_BLACKSTONE_BRICK_STAIRS = registerBlock(Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS);
    public static final Item CRACKED_POLISHED_BLACKSTONE_BRICKS = registerBlock(Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS);
    public static final Item RESPAWN_ANCHOR = registerBlock(Blocks.RESPAWN_ANCHOR);
    public static final Item CANDLE = registerBlock(Blocks.CANDLE);
    public static final Item WHITE_CANDLE = registerBlock(Blocks.WHITE_CANDLE);
    public static final Item ORANGE_CANDLE = registerBlock(Blocks.ORANGE_CANDLE);
    public static final Item MAGENTA_CANDLE = registerBlock(Blocks.MAGENTA_CANDLE);
    public static final Item LIGHT_BLUE_CANDLE = registerBlock(Blocks.LIGHT_BLUE_CANDLE);
    public static final Item YELLOW_CANDLE = registerBlock(Blocks.YELLOW_CANDLE);
    public static final Item LIME_CANDLE = registerBlock(Blocks.LIME_CANDLE);
    public static final Item PINK_CANDLE = registerBlock(Blocks.PINK_CANDLE);
    public static final Item GRAY_CANDLE = registerBlock(Blocks.GRAY_CANDLE);
    public static final Item LIGHT_GRAY_CANDLE = registerBlock(Blocks.LIGHT_GRAY_CANDLE);
    public static final Item CYAN_CANDLE = registerBlock(Blocks.CYAN_CANDLE);
    public static final Item PURPLE_CANDLE = registerBlock(Blocks.PURPLE_CANDLE);
    public static final Item BLUE_CANDLE = registerBlock(Blocks.BLUE_CANDLE);
    public static final Item BROWN_CANDLE = registerBlock(Blocks.BROWN_CANDLE);
    public static final Item GREEN_CANDLE = registerBlock(Blocks.GREEN_CANDLE);
    public static final Item RED_CANDLE = registerBlock(Blocks.RED_CANDLE);
    public static final Item BLACK_CANDLE = registerBlock(Blocks.BLACK_CANDLE);
    public static final Item SMALL_AMETHYST_BUD = registerBlock(Blocks.SMALL_AMETHYST_BUD);
    public static final Item MEDIUM_AMETHYST_BUD = registerBlock(Blocks.MEDIUM_AMETHYST_BUD);
    public static final Item LARGE_AMETHYST_BUD = registerBlock(Blocks.LARGE_AMETHYST_BUD);
    public static final Item AMETHYST_CLUSTER = registerBlock(Blocks.AMETHYST_CLUSTER);
    public static final Item POINTED_DRIPSTONE = registerBlock(Blocks.POINTED_DRIPSTONE);
    public static final Item OCHRE_FROGLIGHT = registerBlock(Blocks.OCHRE_FROGLIGHT);
    public static final Item VERDANT_FROGLIGHT = registerBlock(Blocks.VERDANT_FROGLIGHT);
    public static final Item PEARLESCENT_FROGLIGHT = registerBlock(Blocks.PEARLESCENT_FROGLIGHT);
    public static final Item FROGSPAWN = registerBlock(Blocks.FROGSPAWN, PlaceOnWaterBlockItem::new);
    public static final Item ECHO_SHARD = registerItem("echo_shard", new Item.Properties().rarity(Rarity.UNCOMMON));
    public static final Item BRUSH = registerItem("brush", BrushItem::new, new Item.Properties().durability(64));
    public static final Item NETHERITE_UPGRADE_SMITHING_TEMPLATE = registerItem(
        "netherite_upgrade_smithing_template", SmithingTemplateItem::createNetheriteUpgradeTemplate, new Item.Properties().rarity(Rarity.UNCOMMON)
    );
    public static final Item SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE = registerItem(
        "sentry_armor_trim_smithing_template", SmithingTemplateItem::createArmorTrimTemplate, new Item.Properties().rarity(Rarity.UNCOMMON)
    );
    public static final Item DUNE_ARMOR_TRIM_SMITHING_TEMPLATE = registerItem(
        "dune_armor_trim_smithing_template", SmithingTemplateItem::createArmorTrimTemplate, new Item.Properties().rarity(Rarity.UNCOMMON)
    );
    public static final Item COAST_ARMOR_TRIM_SMITHING_TEMPLATE = registerItem(
        "coast_armor_trim_smithing_template", SmithingTemplateItem::createArmorTrimTemplate, new Item.Properties().rarity(Rarity.UNCOMMON)
    );
    public static final Item WILD_ARMOR_TRIM_SMITHING_TEMPLATE = registerItem(
        "wild_armor_trim_smithing_template", SmithingTemplateItem::createArmorTrimTemplate, new Item.Properties().rarity(Rarity.UNCOMMON)
    );
    public static final Item WARD_ARMOR_TRIM_SMITHING_TEMPLATE = registerItem(
        "ward_armor_trim_smithing_template", SmithingTemplateItem::createArmorTrimTemplate, new Item.Properties().rarity(Rarity.RARE)
    );
    public static final Item EYE_ARMOR_TRIM_SMITHING_TEMPLATE = registerItem(
        "eye_armor_trim_smithing_template", SmithingTemplateItem::createArmorTrimTemplate, new Item.Properties().rarity(Rarity.RARE)
    );
    public static final Item VEX_ARMOR_TRIM_SMITHING_TEMPLATE = registerItem(
        "vex_armor_trim_smithing_template", SmithingTemplateItem::createArmorTrimTemplate, new Item.Properties().rarity(Rarity.RARE)
    );
    public static final Item TIDE_ARMOR_TRIM_SMITHING_TEMPLATE = registerItem(
        "tide_armor_trim_smithing_template", SmithingTemplateItem::createArmorTrimTemplate, new Item.Properties().rarity(Rarity.UNCOMMON)
    );
    public static final Item SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE = registerItem(
        "snout_armor_trim_smithing_template", SmithingTemplateItem::createArmorTrimTemplate, new Item.Properties().rarity(Rarity.UNCOMMON)
    );
    public static final Item RIB_ARMOR_TRIM_SMITHING_TEMPLATE = registerItem(
        "rib_armor_trim_smithing_template", SmithingTemplateItem::createArmorTrimTemplate, new Item.Properties().rarity(Rarity.UNCOMMON)
    );
    public static final Item SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE = registerItem(
        "spire_armor_trim_smithing_template", SmithingTemplateItem::createArmorTrimTemplate, new Item.Properties().rarity(Rarity.RARE)
    );
    public static final Item WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE = registerItem(
        "wayfinder_armor_trim_smithing_template", SmithingTemplateItem::createArmorTrimTemplate, new Item.Properties().rarity(Rarity.UNCOMMON)
    );
    public static final Item SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE = registerItem(
        "shaper_armor_trim_smithing_template", SmithingTemplateItem::createArmorTrimTemplate, new Item.Properties().rarity(Rarity.UNCOMMON)
    );
    public static final Item SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE = registerItem(
        "silence_armor_trim_smithing_template", SmithingTemplateItem::createArmorTrimTemplate, new Item.Properties().rarity(Rarity.EPIC)
    );
    public static final Item RAISER_ARMOR_TRIM_SMITHING_TEMPLATE = registerItem(
        "raiser_armor_trim_smithing_template", SmithingTemplateItem::createArmorTrimTemplate, new Item.Properties().rarity(Rarity.UNCOMMON)
    );
    public static final Item HOST_ARMOR_TRIM_SMITHING_TEMPLATE = registerItem(
        "host_armor_trim_smithing_template", SmithingTemplateItem::createArmorTrimTemplate, new Item.Properties().rarity(Rarity.UNCOMMON)
    );
    public static final Item FLOW_ARMOR_TRIM_SMITHING_TEMPLATE = registerItem(
        "flow_armor_trim_smithing_template", SmithingTemplateItem::createArmorTrimTemplate, new Item.Properties().rarity(Rarity.UNCOMMON)
    );
    public static final Item BOLT_ARMOR_TRIM_SMITHING_TEMPLATE = registerItem(
        "bolt_armor_trim_smithing_template", SmithingTemplateItem::createArmorTrimTemplate, new Item.Properties().rarity(Rarity.UNCOMMON)
    );
    public static final Item ANGLER_POTTERY_SHERD = registerItem("angler_pottery_sherd", new Item.Properties().rarity(Rarity.UNCOMMON));
    public static final Item ARCHER_POTTERY_SHERD = registerItem("archer_pottery_sherd", new Item.Properties().rarity(Rarity.UNCOMMON));
    public static final Item ARMS_UP_POTTERY_SHERD = registerItem("arms_up_pottery_sherd", new Item.Properties().rarity(Rarity.UNCOMMON));
    public static final Item BLADE_POTTERY_SHERD = registerItem("blade_pottery_sherd", new Item.Properties().rarity(Rarity.UNCOMMON));
    public static final Item BREWER_POTTERY_SHERD = registerItem("brewer_pottery_sherd", new Item.Properties().rarity(Rarity.UNCOMMON));
    public static final Item BURN_POTTERY_SHERD = registerItem("burn_pottery_sherd", new Item.Properties().rarity(Rarity.UNCOMMON));
    public static final Item DANGER_POTTERY_SHERD = registerItem("danger_pottery_sherd", new Item.Properties().rarity(Rarity.UNCOMMON));
    public static final Item EXPLORER_POTTERY_SHERD = registerItem("explorer_pottery_sherd", new Item.Properties().rarity(Rarity.UNCOMMON));
    public static final Item FLOW_POTTERY_SHERD = registerItem("flow_pottery_sherd", new Item.Properties().rarity(Rarity.UNCOMMON));
    public static final Item FRIEND_POTTERY_SHERD = registerItem("friend_pottery_sherd", new Item.Properties().rarity(Rarity.UNCOMMON));
    public static final Item GUSTER_POTTERY_SHERD = registerItem("guster_pottery_sherd", new Item.Properties().rarity(Rarity.UNCOMMON));
    public static final Item HEART_POTTERY_SHERD = registerItem("heart_pottery_sherd", new Item.Properties().rarity(Rarity.UNCOMMON));
    public static final Item HEARTBREAK_POTTERY_SHERD = registerItem("heartbreak_pottery_sherd", new Item.Properties().rarity(Rarity.UNCOMMON));
    public static final Item HOWL_POTTERY_SHERD = registerItem("howl_pottery_sherd", new Item.Properties().rarity(Rarity.UNCOMMON));
    public static final Item MINER_POTTERY_SHERD = registerItem("miner_pottery_sherd", new Item.Properties().rarity(Rarity.UNCOMMON));
    public static final Item MOURNER_POTTERY_SHERD = registerItem("mourner_pottery_sherd", new Item.Properties().rarity(Rarity.UNCOMMON));
    public static final Item PLENTY_POTTERY_SHERD = registerItem("plenty_pottery_sherd", new Item.Properties().rarity(Rarity.UNCOMMON));
    public static final Item PRIZE_POTTERY_SHERD = registerItem("prize_pottery_sherd", new Item.Properties().rarity(Rarity.UNCOMMON));
    public static final Item SCRAPE_POTTERY_SHERD = registerItem("scrape_pottery_sherd", new Item.Properties().rarity(Rarity.UNCOMMON));
    public static final Item SHEAF_POTTERY_SHERD = registerItem("sheaf_pottery_sherd", new Item.Properties().rarity(Rarity.UNCOMMON));
    public static final Item SHELTER_POTTERY_SHERD = registerItem("shelter_pottery_sherd", new Item.Properties().rarity(Rarity.UNCOMMON));
    public static final Item SKULL_POTTERY_SHERD = registerItem("skull_pottery_sherd", new Item.Properties().rarity(Rarity.UNCOMMON));
    public static final Item SNORT_POTTERY_SHERD = registerItem("snort_pottery_sherd", new Item.Properties().rarity(Rarity.UNCOMMON));
    public static final Item COPPER_GRATE = registerBlock(Blocks.COPPER_GRATE);
    public static final Item EXPOSED_COPPER_GRATE = registerBlock(Blocks.EXPOSED_COPPER_GRATE);
    public static final Item WEATHERED_COPPER_GRATE = registerBlock(Blocks.WEATHERED_COPPER_GRATE);
    public static final Item OXIDIZED_COPPER_GRATE = registerBlock(Blocks.OXIDIZED_COPPER_GRATE);
    public static final Item WAXED_COPPER_GRATE = registerBlock(Blocks.WAXED_COPPER_GRATE);
    public static final Item WAXED_EXPOSED_COPPER_GRATE = registerBlock(Blocks.WAXED_EXPOSED_COPPER_GRATE);
    public static final Item WAXED_WEATHERED_COPPER_GRATE = registerBlock(Blocks.WAXED_WEATHERED_COPPER_GRATE);
    public static final Item WAXED_OXIDIZED_COPPER_GRATE = registerBlock(Blocks.WAXED_OXIDIZED_COPPER_GRATE);
    public static final Item COPPER_BULB = registerBlock(Blocks.COPPER_BULB);
    public static final Item EXPOSED_COPPER_BULB = registerBlock(Blocks.EXPOSED_COPPER_BULB);
    public static final Item WEATHERED_COPPER_BULB = registerBlock(Blocks.WEATHERED_COPPER_BULB);
    public static final Item OXIDIZED_COPPER_BULB = registerBlock(Blocks.OXIDIZED_COPPER_BULB);
    public static final Item WAXED_COPPER_BULB = registerBlock(Blocks.WAXED_COPPER_BULB);
    public static final Item WAXED_EXPOSED_COPPER_BULB = registerBlock(Blocks.WAXED_EXPOSED_COPPER_BULB);
    public static final Item WAXED_WEATHERED_COPPER_BULB = registerBlock(Blocks.WAXED_WEATHERED_COPPER_BULB);
    public static final Item WAXED_OXIDIZED_COPPER_BULB = registerBlock(Blocks.WAXED_OXIDIZED_COPPER_BULB);
    public static final Item TRIAL_SPAWNER = registerBlock(Blocks.TRIAL_SPAWNER);
    public static final Item TRIAL_KEY = registerItem("trial_key");
    public static final Item OMINOUS_TRIAL_KEY = registerItem("ominous_trial_key");
    public static final Item VAULT = registerBlock(Blocks.VAULT);
    public static final Item OMINOUS_BOTTLE = registerItem(
        "ominous_bottle",
        new Item.Properties()
            .rarity(Rarity.UNCOMMON)
            .component(DataComponents.CONSUMABLE, Consumables.OMINOUS_BOTTLE)
            .component(DataComponents.OMINOUS_BOTTLE_AMPLIFIER, new OminousBottleAmplifier(0))
    );

    private static Function<Item.Properties, Item> createBlockItemWithCustomItemName(Block p_371720_) {
        return p_371097_ -> new BlockItem(p_371720_, p_371097_.useItemDescriptionPrefix());
    }

    private static ResourceKey<Item> vanillaItemId(String p_371531_) {
        return ResourceKey.create(Registries.ITEM, ResourceLocation.withDefaultNamespace(p_371531_));
    }

    private static ResourceKey<Item> blockIdToItemId(ResourceKey<Block> p_371546_) {
        return ResourceKey.create(Registries.ITEM, p_371546_.location());
    }

    public static Item registerBlock(Block p_42806_) {
        return registerBlock(p_42806_, BlockItem::new);
    }

    public static Item registerBlock(Block p_371407_, Item.Properties p_371547_) {
        return registerBlock(p_371407_, BlockItem::new, p_371547_);
    }

    public static Item registerBlock(Block p_332741_, UnaryOperator<Item.Properties> p_332641_) {
        return registerBlock(p_332741_, (p_371022_, p_371023_) -> new BlockItem(p_371022_, p_332641_.apply(p_371023_)));
    }

    public static Item registerBlock(Block p_252092_, Block... p_248886_) {
        Item item = registerItem(
                blockIdToItemId(p_252092_.builtInRegistryHolder().key()),
                p -> new BlockItem(p_252092_, p) {
                    @Override
                    public void registerBlocks(java.util.Map<Block, Item> map, Item self) {
                        super.registerBlocks(map, self);
                        for (Block block : p_248886_) {
                            map.put(block, self);
                        }
                    }
                },
                new Item.Properties().useBlockDescriptionPrefix()
        );

        return item;
    }

    public static Item registerBlock(Block p_42811_, BiFunction<Block, Item.Properties, Item> p_371673_) {
        return registerBlock(p_42811_, p_371673_, new Item.Properties());
    }

    public static Item registerBlock(Block p_371299_, BiFunction<Block, Item.Properties, Item> p_371473_, Item.Properties p_371739_) {
        return registerItem(
            blockIdToItemId(p_371299_.builtInRegistryHolder().key()), p_370785_ -> p_371473_.apply(p_371299_, p_370785_), p_371739_.useBlockDescriptionPrefix()
        );
    }

    public static Item registerItem(String p_371307_, Function<Item.Properties, Item> p_371863_) {
        return registerItem(vanillaItemId(p_371307_), p_371863_, new Item.Properties());
    }

    public static Item registerItem(String p_42814_, Function<Item.Properties, Item> p_371934_, Item.Properties p_371605_) {
        return registerItem(vanillaItemId(p_42814_), p_371934_, p_371605_);
    }

    public static Item registerItem(String p_371659_, Item.Properties p_371323_) {
        return registerItem(vanillaItemId(p_371659_), Item::new, p_371323_);
    }

    public static Item registerItem(String p_371438_) {
        return registerItem(vanillaItemId(p_371438_), Item::new, new Item.Properties());
    }

    public static Item registerItem(ResourceKey<Item> p_371338_, Function<Item.Properties, Item> p_371900_) {
        return registerItem(p_371338_, p_371900_, new Item.Properties());
    }

    public static Item registerItem(ResourceKey<Item> p_281502_, Function<Item.Properties, Item> p_371300_, Item.Properties p_371376_) {
        Item item = p_371300_.apply(p_371376_.setId(p_281502_));
        if (item instanceof BlockItem blockitem) {
            blockitem.registerBlocks(Item.BY_BLOCK, item);
        }

        return Registry.register(BuiltInRegistries.ITEM, p_281502_, item);
    }
}
