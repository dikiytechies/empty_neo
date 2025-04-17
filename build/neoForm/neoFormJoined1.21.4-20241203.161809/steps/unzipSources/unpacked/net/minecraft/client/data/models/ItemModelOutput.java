package net.minecraft.client.data.models;

import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface ItemModelOutput {
    void accept(Item p_387543_, ItemModel.Unbaked p_386880_);

    void copy(Item p_387316_, Item p_387995_);

    /**
     * Neo: Pulled up from {@link ModelProvider.ItemInfoCollector} to give modders full control over the {@link net.minecraft.client.renderer.item.ClientItem} instead of just the {@link ItemModel.Unbaked} in {@link #accept(Item, ItemModel.Unbaked)
     */
    default void register(Item item, net.minecraft.client.renderer.item.ClientItem clientItem) {
    }
}
