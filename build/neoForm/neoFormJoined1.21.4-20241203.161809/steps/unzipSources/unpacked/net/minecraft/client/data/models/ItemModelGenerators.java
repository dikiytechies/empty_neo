package net.minecraft.client.data.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiConsumer;
import net.minecraft.client.color.item.Dye;
import net.minecraft.client.color.item.Firework;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.color.item.MapColor;
import net.minecraft.client.color.item.Potion;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelInstance;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.renderer.item.BundleSelectedItemSpecialRenderer;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.RangeSelectItemModel;
import net.minecraft.client.renderer.item.SelectItemModel;
import net.minecraft.client.renderer.item.properties.conditional.Broken;
import net.minecraft.client.renderer.item.properties.conditional.BundleHasSelectedItem;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.client.renderer.item.properties.conditional.FishingRodCast;
import net.minecraft.client.renderer.item.properties.numeric.CompassAngle;
import net.minecraft.client.renderer.item.properties.numeric.CompassAngleState;
import net.minecraft.client.renderer.item.properties.numeric.CrossbowPull;
import net.minecraft.client.renderer.item.properties.numeric.Time;
import net.minecraft.client.renderer.item.properties.numeric.UseCycle;
import net.minecraft.client.renderer.item.properties.numeric.UseDuration;
import net.minecraft.client.renderer.item.properties.select.Charge;
import net.minecraft.client.renderer.item.properties.select.DisplayContext;
import net.minecraft.client.renderer.item.properties.select.TrimMaterialProperty;
import net.minecraft.client.renderer.special.ShieldSpecialRenderer;
import net.minecraft.client.renderer.special.TridentSpecialRenderer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.minecraft.world.item.equipment.trim.TrimMaterials;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ItemModelGenerators {
    public static final ItemTintSource BLANK_LAYER = ItemModelUtils.constantTint(-1);
    public static final String SLOT_HELMET = "helmet";
    public static final String SLOT_CHESTPLATE = "chestplate";
    public static final String SLOT_LEGGINS = "leggings";
    public static final String SLOT_BOOTS = "boots";
    public static final List<ItemModelGenerators.TrimMaterialData> TRIM_MATERIAL_MODELS = List.of(
        new ItemModelGenerators.TrimMaterialData("quartz", TrimMaterials.QUARTZ, Map.of()),
        new ItemModelGenerators.TrimMaterialData("iron", TrimMaterials.IRON, Map.of(EquipmentAssets.IRON, "iron_darker")),
        new ItemModelGenerators.TrimMaterialData("netherite", TrimMaterials.NETHERITE, Map.of(EquipmentAssets.NETHERITE, "netherite_darker")),
        new ItemModelGenerators.TrimMaterialData("redstone", TrimMaterials.REDSTONE, Map.of()),
        new ItemModelGenerators.TrimMaterialData("copper", TrimMaterials.COPPER, Map.of()),
        new ItemModelGenerators.TrimMaterialData("gold", TrimMaterials.GOLD, Map.of(EquipmentAssets.GOLD, "gold_darker")),
        new ItemModelGenerators.TrimMaterialData("emerald", TrimMaterials.EMERALD, Map.of()),
        new ItemModelGenerators.TrimMaterialData("diamond", TrimMaterials.DIAMOND, Map.of(EquipmentAssets.DIAMOND, "diamond_darker")),
        new ItemModelGenerators.TrimMaterialData("lapis", TrimMaterials.LAPIS, Map.of()),
        new ItemModelGenerators.TrimMaterialData("amethyst", TrimMaterials.AMETHYST, Map.of()),
        new ItemModelGenerators.TrimMaterialData("resin", TrimMaterials.RESIN, Map.of())
    );
    public final ItemModelOutput itemModelOutput;
    public final BiConsumer<ResourceLocation, ModelInstance> modelOutput;

    public ItemModelGenerators(ItemModelOutput p_387620_, BiConsumer<ResourceLocation, ModelInstance> p_387848_) {
        this.itemModelOutput = p_387620_;
        this.modelOutput = p_387848_;
    }

    public void declareCustomModelItem(Item p_388024_) {
        this.itemModelOutput.accept(p_388024_, ItemModelUtils.plainModel(ModelLocationUtils.getModelLocation(p_388024_)));
    }

    public ResourceLocation createFlatItemModel(Item p_387022_, ModelTemplate p_388315_) {
        return p_388315_.create(ModelLocationUtils.getModelLocation(p_387022_), TextureMapping.layer0(p_387022_), this.modelOutput);
    }

    public void generateFlatItem(Item p_388771_, ModelTemplate p_387425_) {
        this.itemModelOutput.accept(p_388771_, ItemModelUtils.plainModel(this.createFlatItemModel(p_388771_, p_387425_)));
    }

    public ResourceLocation createFlatItemModel(Item p_387652_, String p_387410_, ModelTemplate p_386738_) {
        return p_386738_.create(
            ModelLocationUtils.getModelLocation(p_387652_, p_387410_),
            TextureMapping.layer0(TextureMapping.getItemTexture(p_387652_, p_387410_)),
            this.modelOutput
        );
    }

    public ResourceLocation createFlatItemModel(Item p_387136_, Item p_386454_, ModelTemplate p_388608_) {
        return p_388608_.create(ModelLocationUtils.getModelLocation(p_387136_), TextureMapping.layer0(p_386454_), this.modelOutput);
    }

    public void generateFlatItem(Item p_388277_, Item p_388078_, ModelTemplate p_387994_) {
        this.itemModelOutput.accept(p_388277_, ItemModelUtils.plainModel(this.createFlatItemModel(p_388277_, p_388078_, p_387994_)));
    }

    public void generateItemWithTintedOverlay(Item p_386979_, ItemTintSource p_388847_) {
        this.generateItemWithTintedOverlay(p_386979_, "_overlay", p_388847_);
    }

    public void generateItemWithTintedOverlay(Item p_388114_, String p_387438_, ItemTintSource p_387225_) {
        ResourceLocation resourcelocation = this.generateLayeredItem(
            p_388114_, TextureMapping.getItemTexture(p_388114_), TextureMapping.getItemTexture(p_388114_, p_387438_)
        );
        this.itemModelOutput.accept(p_388114_, ItemModelUtils.tintedModel(resourcelocation, BLANK_LAYER, p_387225_));
    }

    public List<RangeSelectItemModel.Entry> createCompassModels(Item p_386730_) {
        List<RangeSelectItemModel.Entry> list = new ArrayList<>();
        ItemModel.Unbaked itemmodel$unbaked = ItemModelUtils.plainModel(this.createFlatItemModel(p_386730_, "_16", ModelTemplates.FLAT_ITEM));
        list.add(ItemModelUtils.override(itemmodel$unbaked, 0.0F));

        for (int i = 1; i < 32; i++) {
            int j = Mth.positiveModulo(i - 16, 32);
            ItemModel.Unbaked itemmodel$unbaked1 = ItemModelUtils.plainModel(
                this.createFlatItemModel(p_386730_, String.format(Locale.ROOT, "_%02d", j), ModelTemplates.FLAT_ITEM)
            );
            list.add(ItemModelUtils.override(itemmodel$unbaked1, (float)i - 0.5F));
        }

        list.add(ItemModelUtils.override(itemmodel$unbaked, 31.5F));
        return list;
    }

    public void generateStandardCompassItem(Item p_388204_) {
        List<RangeSelectItemModel.Entry> list = this.createCompassModels(p_388204_);
        this.itemModelOutput
            .accept(
                p_388204_,
                ItemModelUtils.conditional(
                    ItemModelUtils.hasComponent(DataComponents.LODESTONE_TRACKER),
                    ItemModelUtils.rangeSelect(new CompassAngle(true, CompassAngleState.CompassTarget.LODESTONE), 32.0F, list),
                    ItemModelUtils.inOverworld(
                        ItemModelUtils.rangeSelect(new CompassAngle(true, CompassAngleState.CompassTarget.SPAWN), 32.0F, list),
                        ItemModelUtils.rangeSelect(new CompassAngle(true, CompassAngleState.CompassTarget.NONE), 32.0F, list)
                    )
                )
            );
    }

    public void generateRecoveryCompassItem(Item p_386605_) {
        this.itemModelOutput
            .accept(
                p_386605_,
                ItemModelUtils.rangeSelect(new CompassAngle(true, CompassAngleState.CompassTarget.RECOVERY), 32.0F, this.createCompassModels(p_386605_))
            );
    }

    public void generateClockItem(Item p_387705_) {
        List<RangeSelectItemModel.Entry> list = new ArrayList<>();
        ItemModel.Unbaked itemmodel$unbaked = ItemModelUtils.plainModel(this.createFlatItemModel(p_387705_, "_00", ModelTemplates.FLAT_ITEM));
        list.add(ItemModelUtils.override(itemmodel$unbaked, 0.0F));

        for (int i = 1; i < 64; i++) {
            ItemModel.Unbaked itemmodel$unbaked1 = ItemModelUtils.plainModel(
                this.createFlatItemModel(p_387705_, String.format(Locale.ROOT, "_%02d", i), ModelTemplates.FLAT_ITEM)
            );
            list.add(ItemModelUtils.override(itemmodel$unbaked1, (float)i - 0.5F));
        }

        list.add(ItemModelUtils.override(itemmodel$unbaked, 63.5F));
        this.itemModelOutput
            .accept(
                p_387705_,
                ItemModelUtils.inOverworld(
                    ItemModelUtils.rangeSelect(new Time(true, Time.TimeSource.DAYTIME), 64.0F, list),
                    ItemModelUtils.rangeSelect(new Time(true, Time.TimeSource.RANDOM), 64.0F, list)
                )
            );
    }

    public ResourceLocation generateLayeredItem(Item p_388230_, ResourceLocation p_388087_, ResourceLocation p_386610_) {
        return ModelTemplates.TWO_LAYERED_ITEM.create(p_388230_, TextureMapping.layered(p_388087_, p_386610_), this.modelOutput);
    }

    public ResourceLocation generateLayeredItem(ResourceLocation p_386992_, ResourceLocation p_386528_, ResourceLocation p_386966_) {
        return ModelTemplates.TWO_LAYERED_ITEM.create(p_386992_, TextureMapping.layered(p_386528_, p_386966_), this.modelOutput);
    }

    public void generateLayeredItem(ResourceLocation p_386617_, ResourceLocation p_388707_, ResourceLocation p_387768_, ResourceLocation p_388354_) {
        ModelTemplates.THREE_LAYERED_ITEM.create(p_386617_, TextureMapping.layered(p_388707_, p_387768_, p_388354_), this.modelOutput);
    }

    public void generateTrimmableItem(Item p_386877_, ResourceKey<EquipmentAsset> p_388083_, String p_387979_, boolean p_387785_) {
        ResourceLocation resourcelocation = ModelLocationUtils.getModelLocation(p_386877_);
        ResourceLocation resourcelocation1 = TextureMapping.getItemTexture(p_386877_);
        ResourceLocation resourcelocation2 = TextureMapping.getItemTexture(p_386877_, "_overlay");
        List<SelectItemModel.SwitchCase<ResourceKey<TrimMaterial>>> list = new ArrayList<>(TRIM_MATERIAL_MODELS.size());

        for (ItemModelGenerators.TrimMaterialData itemmodelgenerators$trimmaterialdata : TRIM_MATERIAL_MODELS) {
            ResourceLocation resourcelocation3 = resourcelocation.withSuffix("_" + itemmodelgenerators$trimmaterialdata.name() + "_trim");
            ResourceLocation resourcelocation4 = ResourceLocation.withDefaultNamespace(
                "trims/items/" + p_387979_ + "_trim_" + itemmodelgenerators$trimmaterialdata.textureName(p_388083_)
            );
            ItemModel.Unbaked itemmodel$unbaked;
            if (p_387785_) {
                this.generateLayeredItem(resourcelocation3, resourcelocation1, resourcelocation2, resourcelocation4);
                itemmodel$unbaked = ItemModelUtils.tintedModel(resourcelocation3, new Dye(-6265536));
            } else {
                this.generateLayeredItem(resourcelocation3, resourcelocation1, resourcelocation4);
                itemmodel$unbaked = ItemModelUtils.plainModel(resourcelocation3);
            }

            list.add(ItemModelUtils.when(itemmodelgenerators$trimmaterialdata.materialKey, itemmodel$unbaked));
        }

        ItemModel.Unbaked itemmodel$unbaked1;
        if (p_387785_) {
            ModelTemplates.TWO_LAYERED_ITEM.create(resourcelocation, TextureMapping.layered(resourcelocation1, resourcelocation2), this.modelOutput);
            itemmodel$unbaked1 = ItemModelUtils.tintedModel(resourcelocation, new Dye(-6265536));
        } else {
            ModelTemplates.FLAT_ITEM.create(resourcelocation, TextureMapping.layer0(resourcelocation1), this.modelOutput);
            itemmodel$unbaked1 = ItemModelUtils.plainModel(resourcelocation);
        }

        this.itemModelOutput.accept(p_386877_, ItemModelUtils.select(new TrimMaterialProperty(), itemmodel$unbaked1, list));
    }

    public void generateBundleModels(Item p_388570_) {
        ItemModel.Unbaked itemmodel$unbaked = ItemModelUtils.plainModel(this.createFlatItemModel(p_388570_, ModelTemplates.FLAT_ITEM));
        ResourceLocation resourcelocation = this.generateBundleCoverModel(p_388570_, ModelTemplates.BUNDLE_OPEN_BACK_INVENTORY, "_open_back");
        ResourceLocation resourcelocation1 = this.generateBundleCoverModel(p_388570_, ModelTemplates.BUNDLE_OPEN_FRONT_INVENTORY, "_open_front");
        ItemModel.Unbaked itemmodel$unbaked1 = ItemModelUtils.composite(
            ItemModelUtils.plainModel(resourcelocation), new BundleSelectedItemSpecialRenderer.Unbaked(), ItemModelUtils.plainModel(resourcelocation1)
        );
        ItemModel.Unbaked itemmodel$unbaked2 = ItemModelUtils.conditional(new BundleHasSelectedItem(), itemmodel$unbaked1, itemmodel$unbaked);
        this.itemModelOutput
            .accept(p_388570_, ItemModelUtils.select(new DisplayContext(), itemmodel$unbaked, ItemModelUtils.when(ItemDisplayContext.GUI, itemmodel$unbaked2)));
    }

    public ResourceLocation generateBundleCoverModel(Item p_387153_, ModelTemplate p_388214_, String p_387206_) {
        ResourceLocation resourcelocation = TextureMapping.getItemTexture(p_387153_, p_387206_);
        return p_388214_.create(p_387153_, TextureMapping.layer0(resourcelocation), this.modelOutput);
    }

    public void generateBow(Item p_387215_) {
        ItemModel.Unbaked itemmodel$unbaked = ItemModelUtils.plainModel(ModelLocationUtils.getModelLocation(p_387215_));
        ItemModel.Unbaked itemmodel$unbaked1 = ItemModelUtils.plainModel(this.createFlatItemModel(p_387215_, "_pulling_0", ModelTemplates.BOW));
        ItemModel.Unbaked itemmodel$unbaked2 = ItemModelUtils.plainModel(this.createFlatItemModel(p_387215_, "_pulling_1", ModelTemplates.BOW));
        ItemModel.Unbaked itemmodel$unbaked3 = ItemModelUtils.plainModel(this.createFlatItemModel(p_387215_, "_pulling_2", ModelTemplates.BOW));
        this.itemModelOutput
            .accept(
                p_387215_,
                ItemModelUtils.conditional(
                    ItemModelUtils.isUsingItem(),
                    ItemModelUtils.rangeSelect(
                        new UseDuration(false),
                        0.05F,
                        itemmodel$unbaked1,
                        ItemModelUtils.override(itemmodel$unbaked2, 0.65F),
                        ItemModelUtils.override(itemmodel$unbaked3, 0.9F)
                    ),
                    itemmodel$unbaked
                )
            );
    }

    public void generateCrossbow(Item p_387491_) {
        ItemModel.Unbaked itemmodel$unbaked = ItemModelUtils.plainModel(ModelLocationUtils.getModelLocation(p_387491_));
        ItemModel.Unbaked itemmodel$unbaked1 = ItemModelUtils.plainModel(this.createFlatItemModel(p_387491_, "_pulling_0", ModelTemplates.CROSSBOW));
        ItemModel.Unbaked itemmodel$unbaked2 = ItemModelUtils.plainModel(this.createFlatItemModel(p_387491_, "_pulling_1", ModelTemplates.CROSSBOW));
        ItemModel.Unbaked itemmodel$unbaked3 = ItemModelUtils.plainModel(this.createFlatItemModel(p_387491_, "_pulling_2", ModelTemplates.CROSSBOW));
        ItemModel.Unbaked itemmodel$unbaked4 = ItemModelUtils.plainModel(this.createFlatItemModel(p_387491_, "_arrow", ModelTemplates.CROSSBOW));
        ItemModel.Unbaked itemmodel$unbaked5 = ItemModelUtils.plainModel(this.createFlatItemModel(p_387491_, "_firework", ModelTemplates.CROSSBOW));
        this.itemModelOutput
            .accept(
                p_387491_,
                ItemModelUtils.conditional(
                    ItemModelUtils.isUsingItem(),
                    ItemModelUtils.rangeSelect(
                        new CrossbowPull(),
                        itemmodel$unbaked1,
                        ItemModelUtils.override(itemmodel$unbaked2, 0.58F),
                        ItemModelUtils.override(itemmodel$unbaked3, 1.0F)
                    ),
                    ItemModelUtils.select(
                        new Charge(),
                        itemmodel$unbaked,
                        ItemModelUtils.when(CrossbowItem.ChargeType.ARROW, itemmodel$unbaked4),
                        ItemModelUtils.when(CrossbowItem.ChargeType.ROCKET, itemmodel$unbaked5)
                    )
                )
            );
    }

    public void generateBooleanDispatch(Item p_387110_, ConditionalItemModelProperty p_388865_, ItemModel.Unbaked p_387060_, ItemModel.Unbaked p_388146_) {
        this.itemModelOutput.accept(p_387110_, ItemModelUtils.conditional(p_388865_, p_387060_, p_388146_));
    }

    public void generateElytra(Item p_387928_) {
        ItemModel.Unbaked itemmodel$unbaked = ItemModelUtils.plainModel(this.createFlatItemModel(p_387928_, ModelTemplates.FLAT_ITEM));
        ItemModel.Unbaked itemmodel$unbaked1 = ItemModelUtils.plainModel(this.createFlatItemModel(p_387928_, "_broken", ModelTemplates.FLAT_ITEM));
        this.generateBooleanDispatch(p_387928_, new Broken(), itemmodel$unbaked1, itemmodel$unbaked);
    }

    public void generateBrush(Item p_387911_) {
        ItemModel.Unbaked itemmodel$unbaked = ItemModelUtils.plainModel(ModelLocationUtils.getModelLocation(p_387911_));
        ItemModel.Unbaked itemmodel$unbaked1 = ItemModelUtils.plainModel(ModelLocationUtils.getModelLocation(p_387911_, "_brushing_0"));
        ItemModel.Unbaked itemmodel$unbaked2 = ItemModelUtils.plainModel(ModelLocationUtils.getModelLocation(p_387911_, "_brushing_1"));
        ItemModel.Unbaked itemmodel$unbaked3 = ItemModelUtils.plainModel(ModelLocationUtils.getModelLocation(p_387911_, "_brushing_2"));
        this.itemModelOutput
            .accept(
                p_387911_,
                ItemModelUtils.rangeSelect(
                    new UseCycle(10.0F),
                    0.1F,
                    itemmodel$unbaked,
                    ItemModelUtils.override(itemmodel$unbaked1, 0.25F),
                    ItemModelUtils.override(itemmodel$unbaked2, 0.5F),
                    ItemModelUtils.override(itemmodel$unbaked3, 0.75F)
                )
            );
    }

    public void generateFishingRod(Item p_387412_) {
        ItemModel.Unbaked itemmodel$unbaked = ItemModelUtils.plainModel(this.createFlatItemModel(p_387412_, ModelTemplates.FLAT_HANDHELD_ROD_ITEM));
        ItemModel.Unbaked itemmodel$unbaked1 = ItemModelUtils.plainModel(this.createFlatItemModel(p_387412_, "_cast", ModelTemplates.FLAT_HANDHELD_ROD_ITEM));
        this.generateBooleanDispatch(p_387412_, new FishingRodCast(), itemmodel$unbaked1, itemmodel$unbaked);
    }

    public void generateGoatHorn(Item p_387137_) {
        ItemModel.Unbaked itemmodel$unbaked = ItemModelUtils.plainModel(ModelLocationUtils.getModelLocation(p_387137_));
        ItemModel.Unbaked itemmodel$unbaked1 = ItemModelUtils.plainModel(ModelLocationUtils.decorateItemModelLocation("tooting_goat_horn"));
        this.generateBooleanDispatch(p_387137_, ItemModelUtils.isUsingItem(), itemmodel$unbaked1, itemmodel$unbaked);
    }

    public void generateShield(Item p_386530_) {
        ItemModel.Unbaked itemmodel$unbaked = ItemModelUtils.specialModel(ModelLocationUtils.getModelLocation(p_386530_), new ShieldSpecialRenderer.Unbaked());
        ItemModel.Unbaked itemmodel$unbaked1 = ItemModelUtils.specialModel(
            ModelLocationUtils.getModelLocation(p_386530_, "_blocking"), new ShieldSpecialRenderer.Unbaked()
        );
        this.generateBooleanDispatch(p_386530_, ItemModelUtils.isUsingItem(), itemmodel$unbaked1, itemmodel$unbaked);
    }

    public static ItemModel.Unbaked createFlatModelDispatch(ItemModel.Unbaked p_388864_, ItemModel.Unbaked p_388747_) {
        return ItemModelUtils.select(
            new DisplayContext(),
            p_388747_,
            ItemModelUtils.when(List.of(ItemDisplayContext.GUI, ItemDisplayContext.GROUND, ItemDisplayContext.FIXED), p_388864_)
        );
    }

    public void generateSpyglass(Item p_387067_) {
        ItemModel.Unbaked itemmodel$unbaked = ItemModelUtils.plainModel(this.createFlatItemModel(p_387067_, ModelTemplates.FLAT_ITEM));
        ItemModel.Unbaked itemmodel$unbaked1 = ItemModelUtils.plainModel(ModelLocationUtils.getModelLocation(p_387067_, "_in_hand"));
        this.itemModelOutput.accept(p_387067_, createFlatModelDispatch(itemmodel$unbaked, itemmodel$unbaked1));
    }

    public void generateTrident(Item p_387274_) {
        ItemModel.Unbaked itemmodel$unbaked = ItemModelUtils.plainModel(this.createFlatItemModel(p_387274_, ModelTemplates.FLAT_ITEM));
        ItemModel.Unbaked itemmodel$unbaked1 = ItemModelUtils.specialModel(
            ModelLocationUtils.getModelLocation(p_387274_, "_in_hand"), new TridentSpecialRenderer.Unbaked()
        );
        ItemModel.Unbaked itemmodel$unbaked2 = ItemModelUtils.specialModel(
            ModelLocationUtils.getModelLocation(p_387274_, "_throwing"), new TridentSpecialRenderer.Unbaked()
        );
        ItemModel.Unbaked itemmodel$unbaked3 = ItemModelUtils.conditional(ItemModelUtils.isUsingItem(), itemmodel$unbaked2, itemmodel$unbaked1);
        this.itemModelOutput.accept(p_387274_, createFlatModelDispatch(itemmodel$unbaked, itemmodel$unbaked3));
    }

    public void addPotionTint(Item p_388495_, ResourceLocation p_386558_) {
        this.itemModelOutput.accept(p_388495_, ItemModelUtils.tintedModel(p_386558_, new Potion()));
    }

    public void generatePotion(Item p_387941_) {
        ResourceLocation resourcelocation = this.generateLayeredItem(
            p_387941_, ModelLocationUtils.decorateItemModelLocation("potion_overlay"), ModelLocationUtils.getModelLocation(p_387941_)
        );
        this.addPotionTint(p_387941_, resourcelocation);
    }

    public void generateTippedArrow(Item p_387880_) {
        ResourceLocation resourcelocation = this.generateLayeredItem(
            p_387880_, ModelLocationUtils.getModelLocation(p_387880_, "_head"), ModelLocationUtils.getModelLocation(p_387880_, "_base")
        );
        this.addPotionTint(p_387880_, resourcelocation);
    }

    public void generateDyedItem(Item p_388875_, int p_388379_) {
        ResourceLocation resourcelocation = this.createFlatItemModel(p_388875_, ModelTemplates.FLAT_ITEM);
        this.itemModelOutput.accept(p_388875_, ItemModelUtils.tintedModel(resourcelocation, new Dye(p_388379_)));
    }

    public void generateSpawnEgg(Item p_387114_, int p_387737_, int p_387138_) {
        ResourceLocation resourcelocation = ModelLocationUtils.decorateItemModelLocation("template_spawn_egg");
        this.itemModelOutput
            .accept(p_387114_, ItemModelUtils.tintedModel(resourcelocation, ItemModelUtils.constantTint(p_387737_), ItemModelUtils.constantTint(p_387138_)));
    }

    public void generateWolfArmor(Item p_388321_) {
        ResourceLocation resourcelocation = TextureMapping.getItemTexture(p_388321_);
        ResourceLocation resourcelocation1 = TextureMapping.getItemTexture(p_388321_, "_overlay");
        ResourceLocation resourcelocation2 = ModelTemplates.FLAT_ITEM.create(p_388321_, TextureMapping.layer0(resourcelocation), this.modelOutput);
        ResourceLocation resourcelocation3 = ModelLocationUtils.getModelLocation(p_388321_, "_dyed");
        ModelTemplates.TWO_LAYERED_ITEM.create(resourcelocation3, TextureMapping.layered(resourcelocation, resourcelocation1), this.modelOutput);
        this.itemModelOutput
            .accept(
                p_388321_,
                ItemModelUtils.conditional(
                    ItemModelUtils.hasComponent(DataComponents.DYED_COLOR),
                    ItemModelUtils.tintedModel(resourcelocation3, BLANK_LAYER, new Dye(0)),
                    ItemModelUtils.plainModel(resourcelocation2)
                )
            );
    }

    public void run() {
        this.generateFlatItem(Items.ACACIA_BOAT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.CHERRY_BOAT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.ACACIA_CHEST_BOAT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.CHERRY_CHEST_BOAT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.AMETHYST_SHARD, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.APPLE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.ARMADILLO_SCUTE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.ARMOR_STAND, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.ARROW, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.BAKED_POTATO, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.BAMBOO, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.BEEF, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.BEETROOT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.BEETROOT_SOUP, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.BIRCH_BOAT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.BIRCH_CHEST_BOAT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.BLACK_DYE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.BLAZE_POWDER, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.BLAZE_ROD, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.BLUE_DYE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.BONE_MEAL, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.BORDURE_INDENTED_BANNER_PATTERN, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.BOOK, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.BOWL, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.BREAD, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.BRICK, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.BREEZE_ROD, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.BROWN_DYE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.BUCKET, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.CARROT_ON_A_STICK, ModelTemplates.FLAT_HANDHELD_ROD_ITEM);
        this.generateFlatItem(Items.WARPED_FUNGUS_ON_A_STICK, ModelTemplates.FLAT_HANDHELD_ROD_ITEM);
        this.generateFlatItem(Items.CHARCOAL, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.CHEST_MINECART, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.CHICKEN, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.CHORUS_FRUIT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.CLAY_BALL, ModelTemplates.FLAT_ITEM);
        this.generateClockItem(Items.CLOCK);
        this.generateFlatItem(Items.COAL, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.COD_BUCKET, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.COMMAND_BLOCK_MINECART, ModelTemplates.FLAT_ITEM);
        this.generateStandardCompassItem(Items.COMPASS);
        this.generateRecoveryCompassItem(Items.RECOVERY_COMPASS);
        this.generateFlatItem(Items.COOKED_BEEF, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.COOKED_CHICKEN, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.COOKED_COD, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.COOKED_MUTTON, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.COOKED_PORKCHOP, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.COOKED_RABBIT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.COOKED_SALMON, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.COOKIE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.RAW_COPPER, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.COPPER_INGOT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.CREEPER_BANNER_PATTERN, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.CYAN_DYE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.DARK_OAK_BOAT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.DARK_OAK_CHEST_BOAT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.DIAMOND, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.DIAMOND_AXE, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.DIAMOND_HOE, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.DIAMOND_HORSE_ARMOR, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.DIAMOND_PICKAXE, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.DIAMOND_SHOVEL, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.DIAMOND_SWORD, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.DRAGON_BREATH, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.DRIED_KELP, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.EGG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.EMERALD, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.ENCHANTED_BOOK, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.ENDER_EYE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.ENDER_PEARL, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.END_CRYSTAL, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.EXPERIENCE_BOTTLE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.FERMENTED_SPIDER_EYE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.FIELD_MASONED_BANNER_PATTERN, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.FIREWORK_ROCKET, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.FIRE_CHARGE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.FLINT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.FLINT_AND_STEEL, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.FLOW_BANNER_PATTERN, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.FLOWER_BANNER_PATTERN, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.FURNACE_MINECART, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.GHAST_TEAR, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.GLASS_BOTTLE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.GLISTERING_MELON_SLICE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.GLOBE_BANNER_PATTERN, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.GLOW_BERRIES, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.GLOWSTONE_DUST, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.GLOW_INK_SAC, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.GLOW_ITEM_FRAME, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.RAW_GOLD, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.GOLDEN_APPLE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.GOLDEN_AXE, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.GOLDEN_CARROT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.GOLDEN_HOE, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.GOLDEN_HORSE_ARMOR, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.GOLDEN_PICKAXE, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.GOLDEN_SHOVEL, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.GOLDEN_SWORD, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.GOLD_INGOT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.GOLD_NUGGET, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.GRAY_DYE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.GREEN_DYE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.GUNPOWDER, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.GUSTER_BANNER_PATTERN, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.HEART_OF_THE_SEA, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.HONEYCOMB, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.HONEY_BOTTLE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.HOPPER_MINECART, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.INK_SAC, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.RAW_IRON, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.IRON_AXE, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.IRON_HOE, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.IRON_HORSE_ARMOR, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.IRON_INGOT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.IRON_NUGGET, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.IRON_PICKAXE, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.IRON_SHOVEL, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.IRON_SWORD, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.ITEM_FRAME, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.JUNGLE_BOAT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.JUNGLE_CHEST_BOAT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.KNOWLEDGE_BOOK, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.LAPIS_LAZULI, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.LAVA_BUCKET, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.LEATHER, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.LIGHT_BLUE_DYE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.LIGHT_GRAY_DYE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.LIME_DYE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.MAGENTA_DYE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.MAGMA_CREAM, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.MANGROVE_BOAT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.MANGROVE_CHEST_BOAT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.BAMBOO_RAFT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.BAMBOO_CHEST_RAFT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.MAP, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.MELON_SLICE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.MILK_BUCKET, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.MINECART, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.MOJANG_BANNER_PATTERN, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.MUSHROOM_STEW, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.DISC_FRAGMENT_5, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.MUSIC_DISC_11, ModelTemplates.MUSIC_DISC);
        this.generateFlatItem(Items.MUSIC_DISC_13, ModelTemplates.MUSIC_DISC);
        this.generateFlatItem(Items.MUSIC_DISC_BLOCKS, ModelTemplates.MUSIC_DISC);
        this.generateFlatItem(Items.MUSIC_DISC_CAT, ModelTemplates.MUSIC_DISC);
        this.generateFlatItem(Items.MUSIC_DISC_CHIRP, ModelTemplates.MUSIC_DISC);
        this.generateFlatItem(Items.MUSIC_DISC_CREATOR, ModelTemplates.MUSIC_DISC);
        this.generateFlatItem(Items.MUSIC_DISC_CREATOR_MUSIC_BOX, ModelTemplates.MUSIC_DISC);
        this.generateFlatItem(Items.MUSIC_DISC_FAR, ModelTemplates.MUSIC_DISC);
        this.generateFlatItem(Items.MUSIC_DISC_MALL, ModelTemplates.MUSIC_DISC);
        this.generateFlatItem(Items.MUSIC_DISC_MELLOHI, ModelTemplates.MUSIC_DISC);
        this.generateFlatItem(Items.MUSIC_DISC_PIGSTEP, ModelTemplates.MUSIC_DISC);
        this.generateFlatItem(Items.MUSIC_DISC_PRECIPICE, ModelTemplates.MUSIC_DISC);
        this.generateFlatItem(Items.MUSIC_DISC_STAL, ModelTemplates.MUSIC_DISC);
        this.generateFlatItem(Items.MUSIC_DISC_STRAD, ModelTemplates.MUSIC_DISC);
        this.generateFlatItem(Items.MUSIC_DISC_WAIT, ModelTemplates.MUSIC_DISC);
        this.generateFlatItem(Items.MUSIC_DISC_WARD, ModelTemplates.MUSIC_DISC);
        this.generateFlatItem(Items.MUSIC_DISC_OTHERSIDE, ModelTemplates.MUSIC_DISC);
        this.generateFlatItem(Items.MUSIC_DISC_RELIC, ModelTemplates.MUSIC_DISC);
        this.generateFlatItem(Items.MUSIC_DISC_5, ModelTemplates.MUSIC_DISC);
        this.generateFlatItem(Items.MUTTON, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.NAME_TAG, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.NAUTILUS_SHELL, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.NETHERITE_AXE, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.NETHERITE_HOE, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.NETHERITE_INGOT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.NETHERITE_PICKAXE, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.NETHERITE_SCRAP, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.NETHERITE_SHOVEL, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.NETHERITE_SWORD, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.NETHER_BRICK, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.RESIN_BRICK, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.NETHER_STAR, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.OAK_BOAT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.OAK_CHEST_BOAT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.ORANGE_DYE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.PAINTING, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.PALE_OAK_BOAT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.PALE_OAK_CHEST_BOAT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.PAPER, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.PHANTOM_MEMBRANE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.PIGLIN_BANNER_PATTERN, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.PINK_DYE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.POISONOUS_POTATO, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.POPPED_CHORUS_FRUIT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.PORKCHOP, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.POWDER_SNOW_BUCKET, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.PRISMARINE_CRYSTALS, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.PRISMARINE_SHARD, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.PUFFERFISH, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.PUFFERFISH_BUCKET, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.PUMPKIN_PIE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.PURPLE_DYE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.QUARTZ, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.RABBIT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.RABBIT_FOOT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.RABBIT_HIDE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.RABBIT_STEW, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.RED_DYE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.ROTTEN_FLESH, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.SADDLE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.SALMON, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.SALMON_BUCKET, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.TURTLE_SCUTE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.SHEARS, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.SHULKER_SHELL, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.SKULL_BANNER_PATTERN, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.SLIME_BALL, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.SNOWBALL, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.ECHO_SHARD, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.SPECTRAL_ARROW, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.SPIDER_EYE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.SPRUCE_BOAT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.SPRUCE_CHEST_BOAT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.STICK, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.STONE_AXE, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.STONE_HOE, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.STONE_PICKAXE, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.STONE_SHOVEL, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.STONE_SWORD, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.SUGAR, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.SUSPICIOUS_STEW, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.TNT_MINECART, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.TOTEM_OF_UNDYING, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.TROPICAL_FISH, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.TROPICAL_FISH_BUCKET, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.AXOLOTL_BUCKET, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.TADPOLE_BUCKET, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.WATER_BUCKET, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.WHEAT, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.WHITE_DYE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.WIND_CHARGE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.MACE, ModelTemplates.FLAT_HANDHELD_MACE_ITEM);
        this.generateFlatItem(Items.WOODEN_AXE, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.WOODEN_HOE, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.WOODEN_PICKAXE, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.WOODEN_SHOVEL, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.WOODEN_SWORD, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.WRITABLE_BOOK, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.WRITTEN_BOOK, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.YELLOW_DYE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.COAST_ARMOR_TRIM_SMITHING_TEMPLATE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.WILD_ARMOR_TRIM_SMITHING_TEMPLATE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.WARD_ARMOR_TRIM_SMITHING_TEMPLATE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.EYE_ARMOR_TRIM_SMITHING_TEMPLATE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.VEX_ARMOR_TRIM_SMITHING_TEMPLATE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.RIB_ARMOR_TRIM_SMITHING_TEMPLATE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.RAISER_ARMOR_TRIM_SMITHING_TEMPLATE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.HOST_ARMOR_TRIM_SMITHING_TEMPLATE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.FLOW_ARMOR_TRIM_SMITHING_TEMPLATE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.BOLT_ARMOR_TRIM_SMITHING_TEMPLATE, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.DEBUG_STICK, Items.STICK, ModelTemplates.FLAT_HANDHELD_ITEM);
        this.generateFlatItem(Items.ENCHANTED_GOLDEN_APPLE, Items.GOLDEN_APPLE, ModelTemplates.FLAT_ITEM);
        this.generateTrimmableItem(Items.TURTLE_HELMET, EquipmentAssets.TURTLE_SCUTE, "helmet", false);
        this.generateTrimmableItem(Items.LEATHER_HELMET, EquipmentAssets.LEATHER, "helmet", true);
        this.generateTrimmableItem(Items.LEATHER_CHESTPLATE, EquipmentAssets.LEATHER, "chestplate", true);
        this.generateTrimmableItem(Items.LEATHER_LEGGINGS, EquipmentAssets.LEATHER, "leggings", true);
        this.generateTrimmableItem(Items.LEATHER_BOOTS, EquipmentAssets.LEATHER, "boots", true);
        this.generateTrimmableItem(Items.CHAINMAIL_HELMET, EquipmentAssets.CHAINMAIL, "helmet", false);
        this.generateTrimmableItem(Items.CHAINMAIL_CHESTPLATE, EquipmentAssets.CHAINMAIL, "chestplate", false);
        this.generateTrimmableItem(Items.CHAINMAIL_LEGGINGS, EquipmentAssets.CHAINMAIL, "leggings", false);
        this.generateTrimmableItem(Items.CHAINMAIL_BOOTS, EquipmentAssets.CHAINMAIL, "boots", false);
        this.generateTrimmableItem(Items.IRON_HELMET, EquipmentAssets.IRON, "helmet", false);
        this.generateTrimmableItem(Items.IRON_CHESTPLATE, EquipmentAssets.IRON, "chestplate", false);
        this.generateTrimmableItem(Items.IRON_LEGGINGS, EquipmentAssets.IRON, "leggings", false);
        this.generateTrimmableItem(Items.IRON_BOOTS, EquipmentAssets.IRON, "boots", false);
        this.generateTrimmableItem(Items.DIAMOND_HELMET, EquipmentAssets.DIAMOND, "helmet", false);
        this.generateTrimmableItem(Items.DIAMOND_CHESTPLATE, EquipmentAssets.DIAMOND, "chestplate", false);
        this.generateTrimmableItem(Items.DIAMOND_LEGGINGS, EquipmentAssets.DIAMOND, "leggings", false);
        this.generateTrimmableItem(Items.DIAMOND_BOOTS, EquipmentAssets.DIAMOND, "boots", false);
        this.generateTrimmableItem(Items.GOLDEN_HELMET, EquipmentAssets.GOLD, "helmet", false);
        this.generateTrimmableItem(Items.GOLDEN_CHESTPLATE, EquipmentAssets.GOLD, "chestplate", false);
        this.generateTrimmableItem(Items.GOLDEN_LEGGINGS, EquipmentAssets.GOLD, "leggings", false);
        this.generateTrimmableItem(Items.GOLDEN_BOOTS, EquipmentAssets.GOLD, "boots", false);
        this.generateTrimmableItem(Items.NETHERITE_HELMET, EquipmentAssets.NETHERITE, "helmet", false);
        this.generateTrimmableItem(Items.NETHERITE_CHESTPLATE, EquipmentAssets.NETHERITE, "chestplate", false);
        this.generateTrimmableItem(Items.NETHERITE_LEGGINGS, EquipmentAssets.NETHERITE, "leggings", false);
        this.generateTrimmableItem(Items.NETHERITE_BOOTS, EquipmentAssets.NETHERITE, "boots", false);
        this.generateDyedItem(Items.LEATHER_HORSE_ARMOR, -6265536);
        this.generateFlatItem(Items.ANGLER_POTTERY_SHERD, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.ARCHER_POTTERY_SHERD, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.ARMS_UP_POTTERY_SHERD, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.BLADE_POTTERY_SHERD, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.BREWER_POTTERY_SHERD, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.BURN_POTTERY_SHERD, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.DANGER_POTTERY_SHERD, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.EXPLORER_POTTERY_SHERD, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.FLOW_POTTERY_SHERD, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.FRIEND_POTTERY_SHERD, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.GUSTER_POTTERY_SHERD, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.HEART_POTTERY_SHERD, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.HEARTBREAK_POTTERY_SHERD, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.HOWL_POTTERY_SHERD, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.MINER_POTTERY_SHERD, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.MOURNER_POTTERY_SHERD, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.PLENTY_POTTERY_SHERD, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.PRIZE_POTTERY_SHERD, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.SCRAPE_POTTERY_SHERD, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.SHEAF_POTTERY_SHERD, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.SHELTER_POTTERY_SHERD, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.SKULL_POTTERY_SHERD, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.SNORT_POTTERY_SHERD, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.TRIAL_KEY, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.OMINOUS_TRIAL_KEY, ModelTemplates.FLAT_ITEM);
        this.generateFlatItem(Items.OMINOUS_BOTTLE, ModelTemplates.FLAT_ITEM);
        this.generateItemWithTintedOverlay(Items.FIREWORK_STAR, new Firework());
        this.generateItemWithTintedOverlay(Items.FILLED_MAP, "_markings", new MapColor());
        this.generateBundleModels(Items.BUNDLE);
        this.generateBundleModels(Items.BLACK_BUNDLE);
        this.generateBundleModels(Items.WHITE_BUNDLE);
        this.generateBundleModels(Items.GRAY_BUNDLE);
        this.generateBundleModels(Items.LIGHT_GRAY_BUNDLE);
        this.generateBundleModels(Items.LIGHT_BLUE_BUNDLE);
        this.generateBundleModels(Items.BLUE_BUNDLE);
        this.generateBundleModels(Items.CYAN_BUNDLE);
        this.generateBundleModels(Items.YELLOW_BUNDLE);
        this.generateBundleModels(Items.RED_BUNDLE);
        this.generateBundleModels(Items.PURPLE_BUNDLE);
        this.generateBundleModels(Items.MAGENTA_BUNDLE);
        this.generateBundleModels(Items.PINK_BUNDLE);
        this.generateBundleModels(Items.GREEN_BUNDLE);
        this.generateBundleModels(Items.LIME_BUNDLE);
        this.generateBundleModels(Items.BROWN_BUNDLE);
        this.generateBundleModels(Items.ORANGE_BUNDLE);
        this.generateSpyglass(Items.SPYGLASS);
        this.generateTrident(Items.TRIDENT);
        this.generateWolfArmor(Items.WOLF_ARMOR);
        this.generateBow(Items.BOW);
        this.generateCrossbow(Items.CROSSBOW);
        this.generateElytra(Items.ELYTRA);
        this.generateBrush(Items.BRUSH);
        this.generateFishingRod(Items.FISHING_ROD);
        this.generateGoatHorn(Items.GOAT_HORN);
        this.generateShield(Items.SHIELD);
        this.generateTippedArrow(Items.TIPPED_ARROW);
        this.generatePotion(Items.POTION);
        this.generatePotion(Items.SPLASH_POTION);
        this.generatePotion(Items.LINGERING_POTION);
        this.generateSpawnEgg(Items.ARMADILLO_SPAWN_EGG, 11366765, 8538184);
        this.generateSpawnEgg(Items.ALLAY_SPAWN_EGG, 56063, 44543);
        this.generateSpawnEgg(Items.AXOLOTL_SPAWN_EGG, 16499171, 10890612);
        this.generateSpawnEgg(Items.BAT_SPAWN_EGG, 4996656, 986895);
        this.generateSpawnEgg(Items.BEE_SPAWN_EGG, 15582019, 4400155);
        this.generateSpawnEgg(Items.BLAZE_SPAWN_EGG, 16167425, 16775294);
        this.generateSpawnEgg(Items.BOGGED_SPAWN_EGG, 9084018, 3231003);
        this.generateSpawnEgg(Items.BREEZE_SPAWN_EGG, 11506911, 9529055);
        this.generateSpawnEgg(Items.CAT_SPAWN_EGG, 15714446, 9794134);
        this.generateSpawnEgg(Items.CAMEL_SPAWN_EGG, 16565097, 13341495);
        this.generateSpawnEgg(Items.CAVE_SPIDER_SPAWN_EGG, 803406, 11013646);
        this.generateSpawnEgg(Items.CHICKEN_SPAWN_EGG, 10592673, 16711680);
        this.generateSpawnEgg(Items.COD_SPAWN_EGG, 12691306, 15058059);
        this.generateSpawnEgg(Items.COW_SPAWN_EGG, 4470310, 10592673);
        this.generateSpawnEgg(Items.CREEPER_SPAWN_EGG, 894731, 0);
        this.generateSpawnEgg(Items.DOLPHIN_SPAWN_EGG, 2243405, 16382457);
        this.generateSpawnEgg(Items.DONKEY_SPAWN_EGG, 5457209, 8811878);
        this.generateSpawnEgg(Items.DROWNED_SPAWN_EGG, 9433559, 7969893);
        this.generateSpawnEgg(Items.ELDER_GUARDIAN_SPAWN_EGG, 13552826, 7632531);
        this.generateSpawnEgg(Items.ENDER_DRAGON_SPAWN_EGG, 1842204, 14711290);
        this.generateSpawnEgg(Items.ENDERMAN_SPAWN_EGG, 1447446, 0);
        this.generateSpawnEgg(Items.ENDERMITE_SPAWN_EGG, 1447446, 7237230);
        this.generateSpawnEgg(Items.EVOKER_SPAWN_EGG, 9804699, 1973274);
        this.generateSpawnEgg(Items.FOX_SPAWN_EGG, 14005919, 13396256);
        this.generateSpawnEgg(Items.FROG_SPAWN_EGG, 13661252, 16762748);
        this.generateSpawnEgg(Items.GHAST_SPAWN_EGG, 16382457, 12369084);
        this.generateSpawnEgg(Items.GLOW_SQUID_SPAWN_EGG, 611926, 8778172);
        this.generateSpawnEgg(Items.GOAT_SPAWN_EGG, 10851452, 5589310);
        this.generateSpawnEgg(Items.GUARDIAN_SPAWN_EGG, 5931634, 15826224);
        this.generateSpawnEgg(Items.HOGLIN_SPAWN_EGG, 13004373, 6251620);
        this.generateSpawnEgg(Items.HORSE_SPAWN_EGG, 12623485, 15656192);
        this.generateSpawnEgg(Items.HUSK_SPAWN_EGG, 7958625, 15125652);
        this.generateSpawnEgg(Items.IRON_GOLEM_SPAWN_EGG, 14405058, 7643954);
        this.generateSpawnEgg(Items.LLAMA_SPAWN_EGG, 12623485, 10051392);
        this.generateSpawnEgg(Items.MAGMA_CUBE_SPAWN_EGG, 3407872, 16579584);
        this.generateSpawnEgg(Items.MOOSHROOM_SPAWN_EGG, 10489616, 12040119);
        this.generateSpawnEgg(Items.MULE_SPAWN_EGG, 1769984, 5321501);
        this.generateSpawnEgg(Items.OCELOT_SPAWN_EGG, 15720061, 5653556);
        this.generateSpawnEgg(Items.PANDA_SPAWN_EGG, 15198183, 1776418);
        this.generateSpawnEgg(Items.PARROT_SPAWN_EGG, 894731, 16711680);
        this.generateSpawnEgg(Items.PHANTOM_SPAWN_EGG, 4411786, 8978176);
        this.generateSpawnEgg(Items.PIG_SPAWN_EGG, 15771042, 14377823);
        this.generateSpawnEgg(Items.PIGLIN_SPAWN_EGG, 10051392, 16380836);
        this.generateSpawnEgg(Items.PIGLIN_BRUTE_SPAWN_EGG, 5843472, 16380836);
        this.generateSpawnEgg(Items.PILLAGER_SPAWN_EGG, 5451574, 9804699);
        this.generateSpawnEgg(Items.POLAR_BEAR_SPAWN_EGG, 15658718, 14014157);
        this.generateSpawnEgg(Items.PUFFERFISH_SPAWN_EGG, 16167425, 3654642);
        this.generateSpawnEgg(Items.RABBIT_SPAWN_EGG, 10051392, 7555121);
        this.generateSpawnEgg(Items.RAVAGER_SPAWN_EGG, 7697520, 5984329);
        this.generateSpawnEgg(Items.SALMON_SPAWN_EGG, 10489616, 951412);
        this.generateSpawnEgg(Items.SHEEP_SPAWN_EGG, 15198183, 16758197);
        this.generateSpawnEgg(Items.SHULKER_SPAWN_EGG, 9725844, 5060690);
        this.generateSpawnEgg(Items.SILVERFISH_SPAWN_EGG, 7237230, 3158064);
        this.generateSpawnEgg(Items.SKELETON_SPAWN_EGG, 12698049, 4802889);
        this.generateSpawnEgg(Items.SKELETON_HORSE_SPAWN_EGG, 6842447, 15066584);
        this.generateSpawnEgg(Items.SLIME_SPAWN_EGG, 5349438, 8306542);
        this.generateSpawnEgg(Items.SNIFFER_SPAWN_EGG, 8855049, 2468720);
        this.generateSpawnEgg(Items.SNOW_GOLEM_SPAWN_EGG, 14283506, 8496292);
        this.generateSpawnEgg(Items.SPIDER_SPAWN_EGG, 3419431, 11013646);
        this.generateSpawnEgg(Items.SQUID_SPAWN_EGG, 2243405, 7375001);
        this.generateSpawnEgg(Items.STRAY_SPAWN_EGG, 6387319, 14543594);
        this.generateSpawnEgg(Items.STRIDER_SPAWN_EGG, 10236982, 5065037);
        this.generateSpawnEgg(Items.TADPOLE_SPAWN_EGG, 7164733, 1444352);
        this.generateSpawnEgg(Items.TRADER_LLAMA_SPAWN_EGG, 15377456, 4547222);
        this.generateSpawnEgg(Items.TROPICAL_FISH_SPAWN_EGG, 15690005, 16775663);
        this.generateSpawnEgg(Items.TURTLE_SPAWN_EGG, 15198183, 44975);
        this.generateSpawnEgg(Items.VEX_SPAWN_EGG, 8032420, 15265265);
        this.generateSpawnEgg(Items.VILLAGER_SPAWN_EGG, 5651507, 12422002);
        this.generateSpawnEgg(Items.VINDICATOR_SPAWN_EGG, 9804699, 2580065);
        this.generateSpawnEgg(Items.WANDERING_TRADER_SPAWN_EGG, 4547222, 15377456);
        this.generateSpawnEgg(Items.WARDEN_SPAWN_EGG, 1001033, 3790560);
        this.generateSpawnEgg(Items.WITCH_SPAWN_EGG, 3407872, 5349438);
        this.generateSpawnEgg(Items.WITHER_SPAWN_EGG, 1315860, 5075616);
        this.generateSpawnEgg(Items.WITHER_SKELETON_SPAWN_EGG, 1315860, 4672845);
        this.generateSpawnEgg(Items.WOLF_SPAWN_EGG, 14144467, 13545366);
        this.generateSpawnEgg(Items.ZOGLIN_SPAWN_EGG, 13004373, 15132390);
        this.generateSpawnEgg(Items.CREAKING_SPAWN_EGG, 6250335, 16545810);
        this.generateSpawnEgg(Items.ZOMBIE_SPAWN_EGG, 44975, 7969893);
        this.generateSpawnEgg(Items.ZOMBIE_HORSE_SPAWN_EGG, 3232308, 9945732);
        this.generateSpawnEgg(Items.ZOMBIE_VILLAGER_SPAWN_EGG, 5651507, 7969893);
        this.generateSpawnEgg(Items.ZOMBIFIED_PIGLIN_SPAWN_EGG, 15373203, 5009705);
        this.declareCustomModelItem(Items.AIR);
        this.declareCustomModelItem(Items.AMETHYST_CLUSTER);
        this.declareCustomModelItem(Items.SMALL_AMETHYST_BUD);
        this.declareCustomModelItem(Items.MEDIUM_AMETHYST_BUD);
        this.declareCustomModelItem(Items.LARGE_AMETHYST_BUD);
        this.declareCustomModelItem(Items.SMALL_DRIPLEAF);
        this.declareCustomModelItem(Items.BIG_DRIPLEAF);
        this.declareCustomModelItem(Items.HANGING_ROOTS);
        this.declareCustomModelItem(Items.POINTED_DRIPSTONE);
        this.declareCustomModelItem(Items.BONE);
        this.declareCustomModelItem(Items.COD);
        this.declareCustomModelItem(Items.FEATHER);
        this.declareCustomModelItem(Items.LEAD);
    }

    @OnlyIn(Dist.CLIENT)
    public static record TrimMaterialData(String name, ResourceKey<TrimMaterial> materialKey, Map<ResourceKey<EquipmentAsset>, String> overrideArmorMaterials) {
        public String textureName(ResourceKey<EquipmentAsset> p_387088_) {
            return this.overrideArmorMaterials.getOrDefault(p_387088_, this.name);
        }
    }
}
