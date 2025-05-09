/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.neoforged.neoforge.common.extensions;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.neoforged.neoforge.fluids.FluidType;

public interface ILivingEntityExtension extends IEntityExtension {
    default LivingEntity self() {
        return (LivingEntity) this;
    }

    @Override
    default boolean canSwimInFluidType(FluidType type) {
        if (type == NeoForgeMod.WATER_TYPE.value()) return !self().isSensitiveToWater();
        else return IEntityExtension.super.canSwimInFluidType(type);
    }

    /**
     * Performs what to do when an entity attempts to go up or "jump" in a fluid.
     *
     * @param type the type of the fluid
     */
    default void jumpInFluid(FluidType type) {
        // Apply swim speed only to WATER fluid types
        double multiplier = type == NeoForgeMod.WATER_TYPE.value() ? self().getAttributeValue(NeoForgeMod.SWIM_SPEED) : 1.0D;
        self().setDeltaMovement(self().getDeltaMovement().add(0.0D, (double) 0.04F * multiplier, 0.0D));
    }

    /**
     * Performs what to do when an entity attempts to go down or "sink" in a fluid.
     *
     * @param type the type of the fluid
     */
    default void sinkInFluid(FluidType type) {
        // Apply swim speed only to WATER fluid types
        double multiplier = type == NeoForgeMod.WATER_TYPE.value() ? self().getAttributeValue(NeoForgeMod.SWIM_SPEED) : 1.0D;
        self().setDeltaMovement(self().getDeltaMovement().add(0.0D, (double) -0.04F * multiplier, 0.0D));
    }

    /**
     * Returns whether the entity can drown in the fluid.
     *
     * @param type the type of the fluid
     * @return {@code true} if the entity can drown in the fluid, {@code false} otherwise
     */
    default boolean canDrownInFluidType(FluidType type) {
        if (type == NeoForgeMod.WATER_TYPE.value()) return !self().canBreatheUnderwater();
        return type.canDrownIn(self());
    }

    /**
     * Performs how an entity moves when within the fluid. If using custom
     * movement logic, the method should return {@code true}. Otherwise, the
     * movement logic will default to water.
     *
     * @param state          the state of the fluid
     * @param movementVector the velocity of how the entity wants to move
     * @param gravity        the gravity to apply to the entity
     * @return {@code true} if custom movement logic is performed, {@code false} otherwise
     */
    default boolean moveInFluid(FluidState state, Vec3 movementVector, double gravity) {
        return state.move(self(), movementVector, gravity);
    }

    /**
     * Executes in {@link LivingEntity#hurt(DamageSource, float)} after all damage and
     * effects have applied. Overriding this method is preferred over overriding the
     * hurt method in custom entities where special behavior is desired after vanilla
     * logic.
     *
     * @param damageContainer The aggregated damage details preceding this hook, which
     *                        includes changes made to the damage sequence by events.
     */
    default void onDamageTaken(DamageContainer damageContainer) {}
}
