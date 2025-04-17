package net.minecraft.client.gui.screens.recipebook;

import java.util.List;
import java.util.Objects;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.Component;
import net.minecraft.recipebook.PlaceRecipeHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.AbstractCraftingMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapedCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapelessCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CraftingRecipeBookComponent extends RecipeBookComponent<AbstractCraftingMenu> {
    private static final WidgetSprites FILTER_BUTTON_SPRITES = new WidgetSprites(
        ResourceLocation.withDefaultNamespace("recipe_book/filter_enabled"),
        ResourceLocation.withDefaultNamespace("recipe_book/filter_disabled"),
        ResourceLocation.withDefaultNamespace("recipe_book/filter_enabled_highlighted"),
        ResourceLocation.withDefaultNamespace("recipe_book/filter_disabled_highlighted")
    );
    private static final Component ONLY_CRAFTABLES_TOOLTIP = Component.translatable("gui.recipebook.toggleRecipes.craftable");
    private static final List<RecipeBookComponent.TabInfo> TABS = List.of(
        new RecipeBookComponent.TabInfo(SearchRecipeBookCategory.CRAFTING),
        new RecipeBookComponent.TabInfo(Items.IRON_AXE, Items.GOLDEN_SWORD, RecipeBookCategories.CRAFTING_EQUIPMENT),
        new RecipeBookComponent.TabInfo(Items.BRICKS, RecipeBookCategories.CRAFTING_BUILDING_BLOCKS),
        new RecipeBookComponent.TabInfo(Items.LAVA_BUCKET, Items.APPLE, RecipeBookCategories.CRAFTING_MISC),
        new RecipeBookComponent.TabInfo(Items.REDSTONE, RecipeBookCategories.CRAFTING_REDSTONE)
    );

    public CraftingRecipeBookComponent(AbstractCraftingMenu p_362638_) {
        super(p_362638_, TABS);
    }

    @Override
    protected boolean isCraftingSlot(Slot p_364400_) {
        return this.menu.getResultSlot() == p_364400_ || this.menu.getInputGridSlots().contains(p_364400_);
    }

    private boolean canDisplay(RecipeDisplay p_379470_) {
        int i = this.menu.getGridWidth();
        int j = this.menu.getGridHeight();
        Objects.requireNonNull(p_379470_);

        return switch (p_379470_) {
            case ShapedCraftingRecipeDisplay shapedcraftingrecipedisplay -> i >= shapedcraftingrecipedisplay.width()
            && j >= shapedcraftingrecipedisplay.height();
            case ShapelessCraftingRecipeDisplay shapelesscraftingrecipedisplay -> i * j >= shapelesscraftingrecipedisplay.ingredients().size();
            default -> false;
        };
    }

    @Override
    protected void fillGhostRecipe(GhostSlots p_379930_, RecipeDisplay p_379870_, ContextMap p_380956_) {
        p_379930_.setResult(this.menu.getResultSlot(), p_380956_, p_379870_.result());
        Objects.requireNonNull(p_379870_);
        switch (p_379870_) {
            case ShapedCraftingRecipeDisplay shapedcraftingrecipedisplay:
                List<Slot> list1 = this.menu.getInputGridSlots();
                PlaceRecipeHelper.placeRecipe(
                    this.menu.getGridWidth(),
                    this.menu.getGridHeight(),
                    shapedcraftingrecipedisplay.width(),
                    shapedcraftingrecipedisplay.height(),
                    shapedcraftingrecipedisplay.ingredients(),
                    (p_380786_, p_380787_, p_380788_, p_380789_) -> {
                        Slot slot = list1.get(p_380787_);
                        p_379930_.setInput(slot, p_380956_, p_380786_);
                    }
                );
                break;
            case ShapelessCraftingRecipeDisplay shapelesscraftingrecipedisplay:
                    List<Slot> list = this.menu.getInputGridSlots();
                    int i = Math.min(shapelesscraftingrecipedisplay.ingredients().size(), list.size());

                    for (int j = 0; j < i; j++) {
                        p_379930_.setInput(list.get(j), p_380956_, shapelesscraftingrecipedisplay.ingredients().get(j));
                    }
                    break;
            default:
        }
    }

    @Override
    protected void initFilterButtonTextures() {
        this.filterButton.initTextureValues(FILTER_BUTTON_SPRITES);
    }

    @Override
    protected Component getRecipeFilterName() {
        return ONLY_CRAFTABLES_TOOLTIP;
    }

    @Override
    protected void selectMatchingRecipes(RecipeCollection p_363827_, StackedItemContents p_362085_) {
        p_363827_.selectRecipes(p_362085_, this::canDisplay);
    }
}
