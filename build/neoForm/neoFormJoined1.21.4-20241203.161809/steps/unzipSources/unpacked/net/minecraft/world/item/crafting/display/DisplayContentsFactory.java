package net.minecraft.world.item.crafting.display;

import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public interface DisplayContentsFactory<T> {
    public interface ForRemainders<T> extends DisplayContentsFactory<T> {
        T addRemainder(T p_381031_, List<T> p_381095_);
    }

    public interface ForStacks<T> extends DisplayContentsFactory<T> {
        default T forStack(Holder<Item> p_380989_) {
            return this.forStack(new ItemStack(p_380989_));
        }

        default T forStack(Item p_381065_) {
            return this.forStack(new ItemStack(p_381065_));
        }

        T forStack(ItemStack p_381163_);
    }
}
