package net.minecraft.client.gui.components.toasts;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RecipeToast implements Toast {
    private static final ResourceLocation BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace("toast/recipe");
    private static final long DISPLAY_TIME = 5000L;
    private static final Component TITLE_TEXT = Component.translatable("recipe.toast.title");
    private static final Component DESCRIPTION_TEXT = Component.translatable("recipe.toast.description");
    private final List<RecipeToast.Entry> recipeItems = new ArrayList<>();
    private long lastChanged;
    private boolean changed;
    private Toast.Visibility wantedVisibility = Toast.Visibility.HIDE;
    private int displayedRecipeIndex;

    private RecipeToast() {
    }

    @Override
    public Toast.Visibility getWantedVisibility() {
        return this.wantedVisibility;
    }

    @Override
    public void update(ToastManager p_360532_, long p_360389_) {
        if (this.changed) {
            this.lastChanged = p_360389_;
            this.changed = false;
        }

        if (this.recipeItems.isEmpty()) {
            this.wantedVisibility = Toast.Visibility.HIDE;
        } else {
            this.wantedVisibility = (double)(p_360389_ - this.lastChanged) >= 5000.0 * p_360532_.getNotificationDisplayTimeMultiplier()
                ? Toast.Visibility.HIDE
                : Toast.Visibility.SHOW;
        }

        this.displayedRecipeIndex = (int)(
            (double)p_360389_
                / Math.max(1.0, 5000.0 * p_360532_.getNotificationDisplayTimeMultiplier() / (double)this.recipeItems.size())
                % (double)this.recipeItems.size()
        );
    }

    @Override
    public void render(GuiGraphics p_281667_, Font p_364091_, long p_281779_) {
        p_281667_.blitSprite(RenderType::guiTextured, BACKGROUND_SPRITE, 0, 0, this.width(), this.height());
        p_281667_.drawString(p_364091_, TITLE_TEXT, 30, 7, -11534256, false);
        p_281667_.drawString(p_364091_, DESCRIPTION_TEXT, 30, 18, -16777216, false);
        RecipeToast.Entry recipetoast$entry = this.recipeItems.get(this.displayedRecipeIndex);
        p_281667_.pose().pushPose();
        p_281667_.pose().scale(0.6F, 0.6F, 1.0F);
        p_281667_.renderFakeItem(recipetoast$entry.categoryItem(), 3, 3);
        p_281667_.pose().popPose();
        p_281667_.renderFakeItem(recipetoast$entry.unlockedItem(), 8, 8);
    }

    private void addItem(ItemStack p_361817_, ItemStack p_363076_) {
        this.recipeItems.add(new RecipeToast.Entry(p_361817_, p_363076_));
        this.changed = true;
    }

    public static void addOrUpdate(ToastManager p_362446_, RecipeDisplay p_380080_) {
        RecipeToast recipetoast = p_362446_.getToast(RecipeToast.class, NO_TOKEN);
        if (recipetoast == null) {
            recipetoast = new RecipeToast();
            p_362446_.addToast(recipetoast);
        }

        ContextMap contextmap = SlotDisplayContext.fromLevel(p_362446_.getMinecraft().level);
        ItemStack itemstack = p_380080_.craftingStation().resolveForFirstStack(contextmap);
        ItemStack itemstack1 = p_380080_.result().resolveForFirstStack(contextmap);
        recipetoast.addItem(itemstack, itemstack1);
    }

    @OnlyIn(Dist.CLIENT)
    static record Entry(ItemStack categoryItem, ItemStack unlockedItem) {
    }
}
