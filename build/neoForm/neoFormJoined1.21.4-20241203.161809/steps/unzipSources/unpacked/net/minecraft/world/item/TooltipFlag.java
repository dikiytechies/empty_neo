package net.minecraft.world.item;

public interface TooltipFlag {
    TooltipFlag.Default NORMAL = new TooltipFlag.Default(false, false);
    TooltipFlag.Default ADVANCED = new TooltipFlag.Default(true, false);

    boolean isAdvanced();

    boolean isCreative();

    /**
     * Neo: Returns the state of the Control key (as reported by Screen) on the client, or {@code false} on the server.
     */
    default boolean hasControlDown() {
        return false;
    }

    /**
     * Neo: Returns the state of the Shift key (as reported by Screen) on the client, or {@code false} on the server.
     */
    default boolean hasShiftDown() {
        return false;
    }

    /**
     * Neo: Returns the state of the Alt key (as reported by Screen) on the client, or {@code false} on the server.
     */
    default boolean hasAltDown() {
        return false;
    }

    public static record Default(boolean advanced, boolean creative) implements TooltipFlag {
        @Override
        public boolean isAdvanced() {
            return this.advanced;
        }

        @Override
        public boolean isCreative() {
            return this.creative;
        }

        public TooltipFlag.Default asCreative() {
            return new TooltipFlag.Default(this.advanced, true);
        }
    }
}
