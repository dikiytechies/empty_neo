/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.neoforged.neoforge.client.event;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.ApiStatus;

/**
 * Fired after the field of vision (FOV) modifier for the player is calculated to allow developers to adjust it further.
 *
 * <p>This event is not {@linkplain ICancellableEvent cancellable}, and does not {@linkplain HasResult have a result}.</p>
 *
 * <p>This event is fired on the {@linkplain NeoForge#EVENT_BUS main Forge event bus},
 * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
 *
 * @see ViewportEvent.ComputeFov
 */
public class ComputeFovModifierEvent extends Event {
    private final Player player;
    private final float fovModifier;
    private final float fovScale;
    private float newFovModifier;

    @ApiStatus.Internal
    public ComputeFovModifierEvent(Player player, float fovModifier, float fovScale) {
        this.player = player;
        this.fovModifier = fovModifier;
        this.fovScale = fovScale;
        this.setNewFovModifier(Mth.lerp(fovScale, 1.0F, fovModifier));
    }

    /**
     * {@return the player affected by this event}
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * {@return the original field of vision (FOV) of the player, before any modifications or interpolation}
     */
    public float getFovModifier() {
        return fovModifier;
    }

    /**
     * {@return the FOV scale to use for interpolating the final FOV modifier}
     */
    public float getFovScale() {
        return fovScale;
    }

    /**
     * {@return the current field of vision (FOV) of the player}
     */
    public float getNewFovModifier() {
        return newFovModifier;
    }

    /**
     * Sets the new field of vision (FOV) of the player.
     *
     * @param newFovModifier the new field of vision (FOV)
     */
    public void setNewFovModifier(float newFovModifier) {
        this.newFovModifier = newFovModifier;
    }
}
