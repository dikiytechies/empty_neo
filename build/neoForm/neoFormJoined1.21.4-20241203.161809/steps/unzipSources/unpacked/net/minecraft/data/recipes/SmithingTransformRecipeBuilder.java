package net.minecraft.data.recipes;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;

public class SmithingTransformRecipeBuilder {
    private final Ingredient template;
    private final Ingredient base;
    private final Ingredient addition;
    private final RecipeCategory category;
    private final Item result;
    private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

    public SmithingTransformRecipeBuilder(Ingredient p_266973_, Ingredient p_267047_, Ingredient p_267009_, RecipeCategory p_266694_, Item p_267183_) {
        this.category = p_266694_;
        this.template = p_266973_;
        this.base = p_267047_;
        this.addition = p_267009_;
        this.result = p_267183_;
    }

    public static SmithingTransformRecipeBuilder smithing(
        Ingredient p_267071_, Ingredient p_266959_, Ingredient p_266803_, RecipeCategory p_266757_, Item p_267256_
    ) {
        return new SmithingTransformRecipeBuilder(p_267071_, p_266959_, p_266803_, p_266757_, p_267256_);
    }

    public SmithingTransformRecipeBuilder unlocks(String p_266919_, Criterion<?> p_300923_) {
        this.criteria.put(p_266919_, p_300923_);
        return this;
    }

    public void save(RecipeOutput p_301163_, String p_300906_) {
        this.save(p_301163_, ResourceKey.create(Registries.RECIPE, ResourceLocation.parse(p_300906_)));
    }

    public void save(RecipeOutput p_301291_, ResourceKey<Recipe<?>> p_379837_) {
        this.ensureValid(p_379837_);
        Advancement.Builder advancement$builder = p_301291_.advancement()
            .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(p_379837_))
            .rewards(AdvancementRewards.Builder.recipe(p_379837_))
            .requirements(AdvancementRequirements.Strategy.OR);
        this.criteria.forEach(advancement$builder::addCriterion);
        SmithingTransformRecipe smithingtransformrecipe = new SmithingTransformRecipe(
            Optional.of(this.template), Optional.of(this.base), Optional.of(this.addition), new ItemStack(this.result)
        );
        p_301291_.accept(
            p_379837_, smithingtransformrecipe, advancement$builder.build(p_379837_.location().withPrefix("recipes/" + this.category.getFolderName() + "/"))
        );
    }

    private void ensureValid(ResourceKey<Recipe<?>> p_379303_) {
        if (this.criteria.isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + p_379303_.location());
        }
    }
}
