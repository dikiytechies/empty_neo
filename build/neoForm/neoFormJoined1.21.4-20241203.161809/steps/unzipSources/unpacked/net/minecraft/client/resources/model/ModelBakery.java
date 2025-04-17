package net.minecraft.client.resources.model;

import com.mojang.logging.LogUtils;
import com.mojang.math.Transformation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.UnbakedBlockStateModel;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.MissingItemModel;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ModelBakery {
    public static final Material FIRE_0 = new Material(TextureAtlas.LOCATION_BLOCKS, ResourceLocation.withDefaultNamespace("block/fire_0"));
    public static final Material FIRE_1 = new Material(TextureAtlas.LOCATION_BLOCKS, ResourceLocation.withDefaultNamespace("block/fire_1"));
    public static final Material LAVA_FLOW = new Material(TextureAtlas.LOCATION_BLOCKS, ResourceLocation.withDefaultNamespace("block/lava_flow"));
    public static final Material WATER_FLOW = new Material(TextureAtlas.LOCATION_BLOCKS, ResourceLocation.withDefaultNamespace("block/water_flow"));
    public static final Material WATER_OVERLAY = new Material(TextureAtlas.LOCATION_BLOCKS, ResourceLocation.withDefaultNamespace("block/water_overlay"));
    public static final Material BANNER_BASE = new Material(Sheets.BANNER_SHEET, ResourceLocation.withDefaultNamespace("entity/banner_base"));
    public static final Material SHIELD_BASE = new Material(Sheets.SHIELD_SHEET, ResourceLocation.withDefaultNamespace("entity/shield_base"));
    public static final Material NO_PATTERN_SHIELD = new Material(Sheets.SHIELD_SHEET, ResourceLocation.withDefaultNamespace("entity/shield_base_nopattern"));
    public static final int DESTROY_STAGE_COUNT = 10;
    public static final List<ResourceLocation> DESTROY_STAGES = IntStream.range(0, 10)
        .mapToObj(p_349912_ -> ResourceLocation.withDefaultNamespace("block/destroy_stage_" + p_349912_))
        .collect(Collectors.toList());
    public static final List<ResourceLocation> BREAKING_LOCATIONS = DESTROY_STAGES.stream()
        .map(p_349910_ -> p_349910_.withPath(p_349911_ -> "textures/" + p_349911_ + ".png"))
        .collect(Collectors.toList());
    public static final List<RenderType> DESTROY_TYPES = BREAKING_LOCATIONS.stream().map(RenderType::crumbling).collect(Collectors.toList());
    static final Logger LOGGER = LogUtils.getLogger();
    private final EntityModelSet entityModelSet;
    final Map<ModelBakery.BakedCacheKey, BakedModel> bakedCache = new HashMap<>();
    private final Map<ModelResourceLocation, UnbakedBlockStateModel> unbakedBlockStateModels;
    private final Map<ResourceLocation, ClientItem> clientInfos;
    final Map<ResourceLocation, UnbakedModel> unbakedPlainModels;
    final UnbakedModel missingModel;
    private final Map<ResourceLocation, UnbakedModel> standaloneModels;

    /**
     * @deprecated Neo: use {@link #ModelBakery(EntityModelSet, Map, Map, Map, UnbakedModel, Map)} ModelBakery instead}
     */
    @Deprecated
    public ModelBakery(
            EntityModelSet p_388903_,
            Map<ModelResourceLocation, UnbakedBlockStateModel> p_251087_,
            Map<ResourceLocation, ClientItem> p_250416_,
            Map<ResourceLocation, UnbakedModel> p_388404_,
            UnbakedModel p_360944_
    ) {
        this(p_388903_, p_251087_, p_250416_, p_388404_, p_360944_, Map.of());
    }

    public ModelBakery(
        EntityModelSet p_388903_,
        Map<ModelResourceLocation, UnbakedBlockStateModel> p_251087_,
        Map<ResourceLocation, ClientItem> p_250416_,
        Map<ResourceLocation, UnbakedModel> p_388404_,
        UnbakedModel p_360944_,
        Map<ResourceLocation, UnbakedModel> standaloneModels
    ) {
        this.entityModelSet = p_388903_;
        this.unbakedBlockStateModels = p_251087_;
        this.clientInfos = p_250416_;
        this.unbakedPlainModels = p_388404_;
        this.missingModel = p_360944_;
        this.standaloneModels = standaloneModels;
    }

    public ModelBakery.BakingResult bakeModels(ModelBakery.TextureGetter p_352431_) {
        BakedModel bakedmodel = UnbakedModel.bakeWithTopModelValues(
            this.missingModel, new ModelBakery.ModelBakerImpl(p_352431_, () -> "missing"), BlockModelRotation.X0_Y0
        );
        Map<ModelResourceLocation, BakedModel> map = new HashMap<>(this.unbakedBlockStateModels.size());
        this.unbakedBlockStateModels.forEach((p_386257_, p_386258_) -> {
            try {
                BakedModel bakedmodel1 = p_386258_.bake(new ModelBakery.ModelBakerImpl(p_352431_, p_386257_::toString));
                map.put(p_386257_, bakedmodel1);
            } catch (Exception exception) {
                LOGGER.warn("Unable to bake model: '{}': {}", p_386257_, exception);
            }
        });
        ItemModel itemmodel = new MissingItemModel(bakedmodel);
        Map<ResourceLocation, ItemModel> map1 = new HashMap<>(this.clientInfos.size());
        Map<ResourceLocation, ClientItem.Properties> map2 = new HashMap<>(this.clientInfos.size());
        this.clientInfos.forEach((p_390101_, p_390102_) -> {
            ModelDebugName modeldebugname = () -> p_390101_ + "#inventory";
            ModelBakery.ModelBakerImpl modelbakery$modelbakerimpl = new ModelBakery.ModelBakerImpl(p_352431_, modeldebugname);
            ItemModel.BakingContext itemmodel$bakingcontext = new ItemModel.BakingContext(modelbakery$modelbakerimpl, this.entityModelSet, itemmodel);

            try {
                ItemModel itemmodel1 = p_390102_.model().bake(itemmodel$bakingcontext);
                map1.put(p_390101_, itemmodel1);
                if (!p_390102_.properties().equals(ClientItem.Properties.DEFAULT)) {
                    map2.put(p_390101_, p_390102_.properties());
                }
            } catch (Exception exception) {
                LOGGER.warn("Unable to bake item model: '{}'", p_390101_, exception);
            }
        });
        Map<ResourceLocation, BakedModel> bakedStandaloneModels = new HashMap<>(this.standaloneModels.size());
        this.standaloneModels.forEach((location, model) -> {
            try {
                ModelDebugName debugName = () -> location + "#standalone";
                ModelBakerImpl modelBaker = new ModelBakerImpl(p_352431_, debugName);
                BakedModel bakedModel = UnbakedModel.bakeWithTopModelValues(model, modelBaker, BlockModelRotation.X0_Y0);
                bakedStandaloneModels.put(location, bakedModel);
            } catch (Exception exception) {
                LOGGER.warn("Unable to bake standalone model: '{}': {}", location, exception);
            }
        });
        return new ModelBakery.BakingResult(bakedmodel, map, itemmodel, map1, map2, bakedStandaloneModels);
    }

    @OnlyIn(Dist.CLIENT)
    static record BakedCacheKey(ResourceLocation id, Transformation transformation, boolean isUvLocked) {
    }

    @OnlyIn(Dist.CLIENT)
    public static record BakingResult(
        BakedModel missingModel,
        Map<ModelResourceLocation, BakedModel> blockStateModels,
        ItemModel missingItemModel,
        Map<ResourceLocation, ItemModel> itemStackModels,
        Map<ResourceLocation, ClientItem.Properties> itemProperties,
        Map<ResourceLocation, BakedModel> standaloneModels
    ) {
        /**
         * @deprecated Neo: use {@link #BakingResult(BakedModel, Map, ItemModel, Map, Map, Map)}} instead
         */
        @Deprecated
        public BakingResult(
                BakedModel missingModel,
                Map<ModelResourceLocation, BakedModel> blockStateModels,
                ItemModel missingItemModel,
                Map<ResourceLocation, ItemModel> itemStackModels,
                Map<ResourceLocation, ClientItem.Properties> itemProperties
        ) {
            this(missingModel, blockStateModels, missingItemModel, itemStackModels, itemProperties, Map.of());
        }
    }

    @OnlyIn(Dist.CLIENT)
    class ModelBakerImpl implements ModelBaker {
        private final ModelDebugName rootName;
        private final SpriteGetter modelTextureGetter;

        ModelBakerImpl(ModelBakery.TextureGetter p_352124_, ModelDebugName p_387467_) {
            this.modelTextureGetter = p_352124_.bind(p_387467_);
            this.rootName = p_387467_;
        }

        @Override
        public SpriteGetter sprites() {
            return this.modelTextureGetter;
        }

        public UnbakedModel getModel(ResourceLocation p_248568_) {
            UnbakedModel unbakedmodel = ModelBakery.this.unbakedPlainModels.get(p_248568_);
            if (unbakedmodel == null) {
                ModelBakery.LOGGER.warn("Requested a model that was not discovered previously: {}", p_248568_);
                return ModelBakery.this.missingModel;
            } else {
                return unbakedmodel;
            }
        }

        @Override
        public BakedModel bake(ResourceLocation p_252176_, ModelState p_249765_) {
            ModelBakery.BakedCacheKey modelbakery$bakedcachekey = new ModelBakery.BakedCacheKey(p_252176_, p_249765_.getRotation(), p_249765_.isUvLocked());
            BakedModel bakedmodel = ModelBakery.this.bakedCache.get(modelbakery$bakedcachekey);
            if (bakedmodel != null) {
                return bakedmodel;
            } else {
                UnbakedModel unbakedmodel = this.getModel(p_252176_);
                BakedModel bakedmodel1 = UnbakedModel.bakeWithTopModelValues(unbakedmodel, this, p_249765_);
                ModelBakery.this.bakedCache.put(modelbakery$bakedcachekey, bakedmodel1);
                return bakedmodel1;
            }
        }

        @Override
        public ModelDebugName rootName() {
            return this.rootName;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public interface TextureGetter {
        TextureAtlasSprite get(ModelDebugName p_388528_, Material p_352128_);

        TextureAtlasSprite reportMissingReference(ModelDebugName p_388282_, String p_388569_);

        default SpriteGetter bind(final ModelDebugName p_386468_) {
            return new SpriteGetter() {
                @Override
                public TextureAtlasSprite get(Material p_388259_) {
                    return TextureGetter.this.get(p_386468_, p_388259_);
                }

                @Override
                public TextureAtlasSprite reportMissingReference(String p_386776_) {
                    return TextureGetter.this.reportMissingReference(p_386468_, p_386776_);
                }
            };
        }
    }
}
