package net.minecraft.world.level.block.state.properties;

import net.minecraft.util.StringRepresentable;

public enum PistonType implements StringRepresentable {
    DEFAULT("normal"),
    STICKY("sticky");

    private final String name;

    private PistonType(String p_61680_) {
        this.name = p_61680_;
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}
