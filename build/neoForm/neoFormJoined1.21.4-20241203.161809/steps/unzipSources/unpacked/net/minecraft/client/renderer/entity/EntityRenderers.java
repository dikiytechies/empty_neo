package net.minecraft.client.renderer.entity;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import net.minecraft.client.model.SquidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class EntityRenderers {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Map<EntityType<?>, EntityRendererProvider<?>> PROVIDERS = new Object2ObjectOpenHashMap<>();
    private static final Map<PlayerSkin.Model, EntityRendererProvider<AbstractClientPlayer>> PLAYER_PROVIDERS = Map.of(
        PlayerSkin.Model.WIDE, p_174098_ -> new PlayerRenderer(p_174098_, false), PlayerSkin.Model.SLIM, p_174096_ -> new PlayerRenderer(p_174096_, true)
    );

    public static <T extends Entity> void register(EntityType<? extends T> p_174037_, EntityRendererProvider<T> p_174038_) {
        PROVIDERS.put(p_174037_, p_174038_);
    }

    public static Map<EntityType<?>, EntityRenderer<?, ?>> createEntityRenderers(EntityRendererProvider.Context p_174050_) {
        Builder<EntityType<?>, EntityRenderer<?, ?>> builder = ImmutableMap.builder();
        PROVIDERS.forEach((p_339310_, p_339311_) -> {
            try {
                builder.put((EntityType<?>)p_339310_, p_339311_.create(p_174050_));
            } catch (Exception exception) {
                throw new IllegalArgumentException("Failed to create model for " + BuiltInRegistries.ENTITY_TYPE.getKey((EntityType<?>)p_339310_), exception);
            }
        });
        return builder.build();
    }

    public static Map<PlayerSkin.Model, EntityRenderer<? extends Player, ?>> createPlayerRenderers(EntityRendererProvider.Context p_174052_) {
        Builder<PlayerSkin.Model, EntityRenderer<? extends Player, ?>> builder = ImmutableMap.builder();
        PLAYER_PROVIDERS.forEach((p_339306_, p_339307_) -> {
            try {
                builder.put(p_339306_, p_339307_.create(p_174052_));
            } catch (Exception exception) {
                throw new IllegalArgumentException("Failed to create player model for " + p_339306_, exception);
            }
        });
        return builder.build();
    }

    public static boolean validateRegistrations() {
        boolean flag = true;

        for (EntityType<?> entitytype : BuiltInRegistries.ENTITY_TYPE) {
            if (entitytype != EntityType.PLAYER && !PROVIDERS.containsKey(entitytype)) {
                LOGGER.warn("No renderer registered for {}", BuiltInRegistries.ENTITY_TYPE.getKey(entitytype));
                flag = false;
            }
        }

        return !flag;
    }

    static {
        register(EntityType.ALLAY, AllayRenderer::new);
        register(EntityType.AREA_EFFECT_CLOUD, NoopRenderer::new);
        register(EntityType.ARMADILLO, ArmadilloRenderer::new);
        register(EntityType.ARMOR_STAND, ArmorStandRenderer::new);
        register(EntityType.ARROW, TippableArrowRenderer::new);
        register(EntityType.AXOLOTL, AxolotlRenderer::new);
        register(EntityType.BAT, BatRenderer::new);
        register(EntityType.BEE, BeeRenderer::new);
        register(EntityType.BLAZE, BlazeRenderer::new);
        register(EntityType.BLOCK_DISPLAY, DisplayRenderer.BlockDisplayRenderer::new);
        register(EntityType.OAK_BOAT, p_375463_ -> new BoatRenderer(p_375463_, ModelLayers.OAK_BOAT));
        register(EntityType.SPRUCE_BOAT, p_375457_ -> new BoatRenderer(p_375457_, ModelLayers.SPRUCE_BOAT));
        register(EntityType.BIRCH_BOAT, p_375449_ -> new BoatRenderer(p_375449_, ModelLayers.BIRCH_BOAT));
        register(EntityType.JUNGLE_BOAT, p_375452_ -> new BoatRenderer(p_375452_, ModelLayers.JUNGLE_BOAT));
        register(EntityType.ACACIA_BOAT, p_375456_ -> new BoatRenderer(p_375456_, ModelLayers.ACACIA_BOAT));
        register(EntityType.CHERRY_BOAT, p_375455_ -> new BoatRenderer(p_375455_, ModelLayers.CHERRY_BOAT));
        register(EntityType.DARK_OAK_BOAT, p_375462_ -> new BoatRenderer(p_375462_, ModelLayers.DARK_OAK_BOAT));
        register(EntityType.PALE_OAK_BOAT, p_378832_ -> new BoatRenderer(p_378832_, ModelLayers.PALE_OAK_BOAT));
        register(EntityType.MANGROVE_BOAT, p_375465_ -> new BoatRenderer(p_375465_, ModelLayers.MANGROVE_BOAT));
        register(EntityType.BAMBOO_RAFT, p_375464_ -> new RaftRenderer(p_375464_, ModelLayers.BAMBOO_RAFT));
        register(EntityType.BOGGED, BoggedRenderer::new);
        register(EntityType.BREEZE, BreezeRenderer::new);
        register(EntityType.BREEZE_WIND_CHARGE, WindChargeRenderer::new);
        register(EntityType.CAT, CatRenderer::new);
        register(EntityType.CAMEL, CamelRenderer::new);
        register(EntityType.CAVE_SPIDER, CaveSpiderRenderer::new);
        register(EntityType.OAK_CHEST_BOAT, p_375454_ -> new BoatRenderer(p_375454_, ModelLayers.OAK_CHEST_BOAT));
        register(EntityType.SPRUCE_CHEST_BOAT, p_375459_ -> new BoatRenderer(p_375459_, ModelLayers.SPRUCE_CHEST_BOAT));
        register(EntityType.BIRCH_CHEST_BOAT, p_375451_ -> new BoatRenderer(p_375451_, ModelLayers.BIRCH_CHEST_BOAT));
        register(EntityType.JUNGLE_CHEST_BOAT, p_375458_ -> new BoatRenderer(p_375458_, ModelLayers.JUNGLE_CHEST_BOAT));
        register(EntityType.ACACIA_CHEST_BOAT, p_375450_ -> new BoatRenderer(p_375450_, ModelLayers.ACACIA_CHEST_BOAT));
        register(EntityType.CHERRY_CHEST_BOAT, p_375453_ -> new BoatRenderer(p_375453_, ModelLayers.CHERRY_CHEST_BOAT));
        register(EntityType.DARK_OAK_CHEST_BOAT, p_375448_ -> new BoatRenderer(p_375448_, ModelLayers.DARK_OAK_CHEST_BOAT));
        register(EntityType.PALE_OAK_CHEST_BOAT, p_378833_ -> new BoatRenderer(p_378833_, ModelLayers.PALE_OAK_CHEST_BOAT));
        register(EntityType.MANGROVE_CHEST_BOAT, p_375460_ -> new BoatRenderer(p_375460_, ModelLayers.MANGROVE_CHEST_BOAT));
        register(EntityType.BAMBOO_CHEST_RAFT, p_375461_ -> new RaftRenderer(p_375461_, ModelLayers.BAMBOO_CHEST_RAFT));
        register(EntityType.CHEST_MINECART, p_174090_ -> new MinecartRenderer(p_174090_, ModelLayers.CHEST_MINECART));
        register(EntityType.CHICKEN, ChickenRenderer::new);
        register(EntityType.COD, CodRenderer::new);
        register(EntityType.COMMAND_BLOCK_MINECART, p_174088_ -> new MinecartRenderer(p_174088_, ModelLayers.COMMAND_BLOCK_MINECART));
        register(EntityType.COW, CowRenderer::new);
        register(EntityType.CREAKING, CreakingRenderer::new);
        register(EntityType.CREEPER, CreeperRenderer::new);
        register(EntityType.DOLPHIN, DolphinRenderer::new);
        register(EntityType.DONKEY, p_382555_ -> new DonkeyRenderer<>(p_382555_, ModelLayers.DONKEY, ModelLayers.DONKEY_BABY, false));
        register(EntityType.DRAGON_FIREBALL, DragonFireballRenderer::new);
        register(EntityType.DROWNED, DrownedRenderer::new);
        register(EntityType.EGG, ThrownItemRenderer::new);
        register(EntityType.ELDER_GUARDIAN, ElderGuardianRenderer::new);
        register(EntityType.ENDERMAN, EndermanRenderer::new);
        register(EntityType.ENDERMITE, EndermiteRenderer::new);
        register(EntityType.ENDER_DRAGON, EnderDragonRenderer::new);
        register(EntityType.ENDER_PEARL, ThrownItemRenderer::new);
        register(EntityType.END_CRYSTAL, EndCrystalRenderer::new);
        register(EntityType.EVOKER, EvokerRenderer::new);
        register(EntityType.EVOKER_FANGS, EvokerFangsRenderer::new);
        register(EntityType.EXPERIENCE_BOTTLE, ThrownItemRenderer::new);
        register(EntityType.EXPERIENCE_ORB, ExperienceOrbRenderer::new);
        register(EntityType.EYE_OF_ENDER, p_174084_ -> new ThrownItemRenderer<>(p_174084_, 1.0F, true));
        register(EntityType.FALLING_BLOCK, FallingBlockRenderer::new);
        register(EntityType.FIREBALL, p_174060_ -> new ThrownItemRenderer<>(p_174060_, 3.0F, true));
        register(EntityType.FIREWORK_ROCKET, FireworkEntityRenderer::new);
        register(EntityType.FISHING_BOBBER, FishingHookRenderer::new);
        register(EntityType.FOX, FoxRenderer::new);
        register(EntityType.FROG, FrogRenderer::new);
        register(EntityType.FURNACE_MINECART, p_174080_ -> new MinecartRenderer(p_174080_, ModelLayers.FURNACE_MINECART));
        register(EntityType.GHAST, GhastRenderer::new);
        register(EntityType.GIANT, p_174078_ -> new GiantMobRenderer(p_174078_, 6.0F));
        register(EntityType.GLOW_ITEM_FRAME, ItemFrameRenderer::new);
        register(
            EntityType.GLOW_SQUID,
            p_359279_ -> new GlowSquidRenderer(
                    p_359279_, new SquidModel(p_359279_.bakeLayer(ModelLayers.GLOW_SQUID)), new SquidModel(p_359279_.bakeLayer(ModelLayers.GLOW_SQUID_BABY))
                )
        );
        register(EntityType.GOAT, GoatRenderer::new);
        register(EntityType.GUARDIAN, GuardianRenderer::new);
        register(EntityType.HOGLIN, HoglinRenderer::new);
        register(EntityType.HOPPER_MINECART, p_174074_ -> new MinecartRenderer(p_174074_, ModelLayers.HOPPER_MINECART));
        register(EntityType.HORSE, HorseRenderer::new);
        register(EntityType.HUSK, HuskRenderer::new);
        register(EntityType.ILLUSIONER, IllusionerRenderer::new);
        register(EntityType.INTERACTION, NoopRenderer::new);
        register(EntityType.IRON_GOLEM, IronGolemRenderer::new);
        register(EntityType.ITEM, ItemEntityRenderer::new);
        register(EntityType.ITEM_DISPLAY, DisplayRenderer.ItemDisplayRenderer::new);
        register(EntityType.ITEM_FRAME, ItemFrameRenderer::new);
        register(EntityType.OMINOUS_ITEM_SPAWNER, OminousItemSpawnerRenderer::new);
        register(EntityType.LEASH_KNOT, LeashKnotRenderer::new);
        register(EntityType.LIGHTNING_BOLT, LightningBoltRenderer::new);
        register(EntityType.LLAMA, p_359283_ -> new LlamaRenderer(p_359283_, ModelLayers.LLAMA, ModelLayers.LLAMA_BABY));
        register(EntityType.LLAMA_SPIT, LlamaSpitRenderer::new);
        register(EntityType.MAGMA_CUBE, MagmaCubeRenderer::new);
        register(EntityType.MARKER, NoopRenderer::new);
        register(EntityType.MINECART, p_174070_ -> new MinecartRenderer(p_174070_, ModelLayers.MINECART));
        register(EntityType.MOOSHROOM, MushroomCowRenderer::new);
        register(EntityType.MULE, p_382554_ -> new DonkeyRenderer<>(p_382554_, ModelLayers.MULE, ModelLayers.MULE_BABY, true));
        register(EntityType.OCELOT, OcelotRenderer::new);
        register(EntityType.PAINTING, PaintingRenderer::new);
        register(EntityType.PANDA, PandaRenderer::new);
        register(EntityType.PARROT, ParrotRenderer::new);
        register(EntityType.PHANTOM, PhantomRenderer::new);
        register(EntityType.PIG, PigRenderer::new);
        register(
            EntityType.PIGLIN,
            p_359282_ -> new PiglinRenderer(
                    p_359282_,
                    ModelLayers.PIGLIN,
                    ModelLayers.PIGLIN_BABY,
                    ModelLayers.PIGLIN_INNER_ARMOR,
                    ModelLayers.PIGLIN_OUTER_ARMOR,
                    ModelLayers.PIGLIN_BABY_INNER_ARMOR,
                    ModelLayers.PIGLIN_BABY_OUTER_ARMOR
                )
        );
        register(
            EntityType.PIGLIN_BRUTE,
            p_359278_ -> new PiglinRenderer(
                    p_359278_,
                    ModelLayers.PIGLIN_BRUTE,
                    ModelLayers.PIGLIN_BRUTE,
                    ModelLayers.PIGLIN_BRUTE_INNER_ARMOR,
                    ModelLayers.PIGLIN_BRUTE_OUTER_ARMOR,
                    ModelLayers.PIGLIN_BRUTE_INNER_ARMOR,
                    ModelLayers.PIGLIN_BRUTE_OUTER_ARMOR
                )
        );
        register(EntityType.PILLAGER, PillagerRenderer::new);
        register(EntityType.POLAR_BEAR, PolarBearRenderer::new);
        register(EntityType.POTION, ThrownItemRenderer::new);
        register(EntityType.PUFFERFISH, PufferfishRenderer::new);
        register(EntityType.RABBIT, RabbitRenderer::new);
        register(EntityType.RAVAGER, RavagerRenderer::new);
        register(EntityType.SALMON, SalmonRenderer::new);
        register(EntityType.SHEEP, SheepRenderer::new);
        register(EntityType.SHULKER, ShulkerRenderer::new);
        register(EntityType.SHULKER_BULLET, ShulkerBulletRenderer::new);
        register(EntityType.SILVERFISH, SilverfishRenderer::new);
        register(EntityType.SKELETON, SkeletonRenderer::new);
        register(EntityType.SKELETON_HORSE, p_359285_ -> new UndeadHorseRenderer(p_359285_, ModelLayers.SKELETON_HORSE, ModelLayers.SKELETON_HORSE_BABY, true));
        register(EntityType.SLIME, SlimeRenderer::new);
        register(EntityType.SMALL_FIREBALL, p_174082_ -> new ThrownItemRenderer<>(p_174082_, 0.75F, true));
        register(EntityType.SNIFFER, SnifferRenderer::new);
        register(EntityType.SNOWBALL, ThrownItemRenderer::new);
        register(EntityType.SNOW_GOLEM, SnowGolemRenderer::new);
        register(EntityType.SPAWNER_MINECART, p_174058_ -> new MinecartRenderer(p_174058_, ModelLayers.SPAWNER_MINECART));
        register(EntityType.SPECTRAL_ARROW, SpectralArrowRenderer::new);
        register(EntityType.SPIDER, SpiderRenderer::new);
        register(
            EntityType.SQUID,
            p_359286_ -> new SquidRenderer<>(
                    p_359286_, new SquidModel(p_359286_.bakeLayer(ModelLayers.SQUID)), new SquidModel(p_359286_.bakeLayer(ModelLayers.SQUID_BABY))
                )
        );
        register(EntityType.STRAY, StrayRenderer::new);
        register(EntityType.STRIDER, StriderRenderer::new);
        register(EntityType.TADPOLE, TadpoleRenderer::new);
        register(EntityType.TEXT_DISPLAY, DisplayRenderer.TextDisplayRenderer::new);
        register(EntityType.TNT, TntRenderer::new);
        register(EntityType.TNT_MINECART, TntMinecartRenderer::new);
        register(EntityType.TRADER_LLAMA, p_359276_ -> new LlamaRenderer(p_359276_, ModelLayers.TRADER_LLAMA, ModelLayers.TRADER_LLAMA_BABY));
        register(EntityType.TRIDENT, ThrownTridentRenderer::new);
        register(EntityType.TROPICAL_FISH, TropicalFishRenderer::new);
        register(EntityType.TURTLE, TurtleRenderer::new);
        register(EntityType.VEX, VexRenderer::new);
        register(EntityType.VILLAGER, VillagerRenderer::new);
        register(EntityType.VINDICATOR, VindicatorRenderer::new);
        register(EntityType.WARDEN, WardenRenderer::new);
        register(EntityType.WANDERING_TRADER, WanderingTraderRenderer::new);
        register(EntityType.WIND_CHARGE, WindChargeRenderer::new);
        register(EntityType.WITCH, WitchRenderer::new);
        register(EntityType.WITHER, WitherBossRenderer::new);
        register(EntityType.WITHER_SKELETON, WitherSkeletonRenderer::new);
        register(EntityType.WITHER_SKULL, WitherSkullRenderer::new);
        register(EntityType.WOLF, WolfRenderer::new);
        register(EntityType.ZOGLIN, ZoglinRenderer::new);
        register(EntityType.ZOMBIE, ZombieRenderer::new);
        register(EntityType.ZOMBIE_HORSE, p_359284_ -> new UndeadHorseRenderer(p_359284_, ModelLayers.ZOMBIE_HORSE, ModelLayers.ZOMBIE_HORSE_BABY, false));
        register(EntityType.ZOMBIE_VILLAGER, ZombieVillagerRenderer::new);
        register(
            EntityType.ZOMBIFIED_PIGLIN,
            p_359281_ -> new ZombifiedPiglinRenderer(
                    p_359281_,
                    ModelLayers.ZOMBIFIED_PIGLIN,
                    ModelLayers.ZOMBIFIED_PIGLIN_BABY,
                    ModelLayers.ZOMBIFIED_PIGLIN_INNER_ARMOR,
                    ModelLayers.ZOMBIFIED_PIGLIN_OUTER_ARMOR,
                    ModelLayers.ZOMBIFIED_PIGLIN_BABY_INNER_ARMOR,
                    ModelLayers.ZOMBIFIED_PIGLIN_BABY_OUTER_ARMOR
                )
        );
    }
}
