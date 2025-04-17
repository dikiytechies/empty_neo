package net.minecraft.world.entity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;

/**
 * @deprecated Neo: Use {@link net.neoforged.neoforge.common.IShearable} instead.
 */
@Deprecated
public interface Shearable extends net.neoforged.neoforge.common.IShearable {
    /**
     * @deprecated Neo: Use {@link net.neoforged.neoforge.common.IShearable#onSheared(net.minecraft.world.entity.player.Player, net.minecraft.world.item.ItemStack, net.minecraft.world.level.Level, net.minecraft.core.BlockPos)} instead.
     */
    @Deprecated
    void shear(ServerLevel p_376429_, SoundSource p_21749_, ItemStack p_372963_);

    /**
     * @deprecated Neo: Use {@link net.neoforged.neoforge.common.IShearable#isShearable(net.minecraft.world.entity.player.Player, net.minecraft.world.item.ItemStack, net.minecraft.world.level.Level, net.minecraft.core.BlockPos)} instead.
     */
    @Deprecated
    boolean readyForShearing();
}
