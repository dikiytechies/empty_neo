/*
 * Copyright (c) NeoForged and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.neoforged.neoforge.items;

import net.minecraft.world.Container;
import org.jetbrains.annotations.Nullable;

public record ContainerOrHandler(
        @Nullable Container container,
        @Nullable IItemHandler itemHandler) {
    public ContainerOrHandler {
        if (container != null && itemHandler != null) {
            throw new IllegalArgumentException("Cannot have both a container and an item handler.");
        }
    }

    public static final ContainerOrHandler EMPTY = new ContainerOrHandler(null, null);

    public boolean isEmpty() {
        return container == null && itemHandler == null;
    }
}
