package net.minecraft.world.flag;

public class FeatureFlag {
    final FeatureFlagUniverse universe;
    final long mask;
    final int extMaskIndex;
    final boolean modded;

    /**
     * @deprecated Neo: use {@link #FeatureFlag(FeatureFlagUniverse, int, int, boolean)} instead
     */
    @Deprecated
    FeatureFlag(FeatureFlagUniverse p_249115_, int p_251067_) {
        this(p_249115_, p_251067_, 0, false);
    }

    FeatureFlag(FeatureFlagUniverse p_249115_, int p_251067_, int offset, boolean modded) {
        this.universe = p_249115_;
        this.mask = 1L << p_251067_;
        this.extMaskIndex = offset - 1;
        this.modded = modded;
    }

    public boolean isModded() {
        return modded;
    }
}
