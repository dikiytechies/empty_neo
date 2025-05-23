package net.minecraft.world.item.crafting;

import com.mojang.datafixers.util.Pair;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;

public class RepairItemRecipe extends CustomRecipe {
    public RepairItemRecipe(CraftingBookCategory p_248679_) {
        super(p_248679_);
    }

    @Nullable
    private static Pair<ItemStack, ItemStack> getItemsToCombine(CraftingInput p_345543_) {
        if (p_345543_.ingredientCount() != 2) {
            return null;
        } else {
            ItemStack itemstack = null;

            for (int i = 0; i < p_345543_.size(); i++) {
                ItemStack itemstack1 = p_345543_.getItem(i);
                if (!itemstack1.isEmpty()) {
                    if (itemstack != null) {
                        return canCombine(itemstack, itemstack1) ? Pair.of(itemstack, itemstack1) : null;
                    }

                    itemstack = itemstack1;
                }
            }

            return null;
        }
    }

    private static boolean canCombine(ItemStack p_336139_, ItemStack p_335795_) {
        return p_335795_.is(p_336139_.getItem())
            && p_336139_.getCount() == 1
            && p_335795_.getCount() == 1
            && p_336139_.has(DataComponents.MAX_DAMAGE)
            && p_335795_.has(DataComponents.MAX_DAMAGE)
            && p_336139_.has(DataComponents.DAMAGE)
            && p_335795_.has(DataComponents.DAMAGE)
            && p_336139_.isCombineRepairable()
            && p_335795_.isCombineRepairable();
    }

    public boolean matches(CraftingInput p_345243_, Level p_44139_) {
        return getItemsToCombine(p_345243_) != null;
    }

    public ItemStack assemble(CraftingInput p_346224_, HolderLookup.Provider p_335610_) {
        Pair<ItemStack, ItemStack> pair = getItemsToCombine(p_346224_);
        if (pair == null) {
            return ItemStack.EMPTY;
        } else {
            ItemStack itemstack = pair.getFirst();
            ItemStack itemstack1 = pair.getSecond();
            int i = Math.max(itemstack.getMaxDamage(), itemstack1.getMaxDamage());
            int j = itemstack.getMaxDamage() - itemstack.getDamageValue();
            int k = itemstack1.getMaxDamage() - itemstack1.getDamageValue();
            int l = j + k + i * 5 / 100;
            ItemStack itemstack2 = new ItemStack(itemstack.getItem());
            itemstack2.set(DataComponents.MAX_DAMAGE, i);
            itemstack2.setDamageValue(Math.max(i - l, 0));
            ItemEnchantments itemenchantments = EnchantmentHelper.getEnchantmentsForCrafting(itemstack);
            ItemEnchantments itemenchantments1 = EnchantmentHelper.getEnchantmentsForCrafting(itemstack1);
            EnchantmentHelper.updateEnchantments(
                itemstack2,
                p_367961_ -> p_335610_.lookupOrThrow(Registries.ENCHANTMENT)
                        .listElements()
                        .filter(p_344414_ -> p_344414_.is(EnchantmentTags.CURSE))
                        .forEach(p_344418_ -> {
                            int i1 = Math.max(itemenchantments.getLevel(p_344418_), itemenchantments1.getLevel(p_344418_));
                            if (i1 > 0) {
                                p_367961_.upgrade(p_344418_, i1);
                            }
                        })
            );
            return itemstack2;
        }
    }

    @Override
    public RecipeSerializer<RepairItemRecipe> getSerializer() {
        return RecipeSerializer.REPAIR_ITEM;
    }
}
