package net.minecraft.world.flag;

import it.unimi.dsi.fastutil.HashCommon;
import java.util.Arrays;
import java.util.Collection;
import javax.annotation.Nullable;

public final class FeatureFlagSet {
    private static final FeatureFlagSet EMPTY = new FeatureFlagSet(null, 0L);
    private static final long[] EMPTY_EXT_MASK = new long[0];
    public static final int MAX_CONTAINER_SIZE = 64;
    @Nullable
    private final FeatureFlagUniverse universe;
    private final long mask;
    private final long[] extendedMask;

    private FeatureFlagSet(@Nullable FeatureFlagUniverse p_250433_, long p_251523_) {
        this(p_250433_, p_251523_, EMPTY_EXT_MASK);
    }

    private FeatureFlagSet(@Nullable FeatureFlagUniverse p_250433_, long p_251523_, long[] extendedMask) {
        this.universe = p_250433_;
        this.mask = p_251523_;
        this.extendedMask = extendedMask;
    }

    static FeatureFlagSet create(FeatureFlagUniverse p_251573_, Collection<FeatureFlag> p_251037_) {
        if (p_251037_.isEmpty()) {
            return EMPTY;
        } else {
            long i = computeMask(p_251573_, 0L, p_251037_);
            long[] extMask = computeExtendedMask(p_251573_, 0, 0L, p_251037_);
            return new FeatureFlagSet(p_251573_, i, extMask);
        }
    }

    public static FeatureFlagSet of() {
        return EMPTY;
    }

    public static FeatureFlagSet of(FeatureFlag p_252331_) {
        long[] extMask = computeExtendedMask(p_252331_.universe, p_252331_.extMaskIndex, p_252331_.mask, java.util.List.of());
        return new FeatureFlagSet(p_252331_.universe, p_252331_.extMaskIndex >= 0 ? 0L : p_252331_.mask, extMask);
    }

    public static FeatureFlagSet of(FeatureFlag p_251008_, FeatureFlag... p_249805_) {
        long i = p_249805_.length == 0 ? (p_251008_.extMaskIndex >= 0 ? 0L : p_251008_.mask) : computeMask(p_251008_.universe, p_251008_.extMaskIndex >= 0 ? 0L : p_251008_.mask, Arrays.asList(p_249805_));
        long[] extMask = computeExtendedMask(p_251008_.universe, p_251008_.extMaskIndex, p_251008_.mask, p_249805_.length == 0 ? java.util.List.of() : Arrays.asList(p_249805_));
        return new FeatureFlagSet(p_251008_.universe, i, extMask);
    }

    private static long computeMask(FeatureFlagUniverse p_249684_, long p_250982_, Iterable<FeatureFlag> p_251734_) {
        for (FeatureFlag featureflag : p_251734_) {
            if (featureflag.extMaskIndex >= 0) continue;
            if (p_249684_ != featureflag.universe) {
                throw new IllegalStateException("Mismatched feature universe, expected '" + p_249684_ + "', but got '" + featureflag.universe + "'");
            }

            p_250982_ |= featureflag.mask;
        }

        return p_250982_;
    }

    private static long[] computeExtendedMask(FeatureFlagUniverse universe, int firstExtIndex, long firstMask, Iterable<FeatureFlag> otherFlags) {
        long[] extMask = EMPTY_EXT_MASK;
        if (firstExtIndex >= 0) {
            extMask = new long[firstExtIndex + 1];
            extMask[firstExtIndex] |= firstMask;
        }
        for (FeatureFlag flag : otherFlags) {
            if (flag.extMaskIndex < 0) continue;
            if (universe != flag.universe) {
                throw new IllegalStateException("Mismatched feature universe, expected '" + universe + "', but got '" + flag.universe + "'");
            }
            if (flag.extMaskIndex >= extMask.length) {
                extMask = Arrays.copyOfRange(extMask, 0, flag.extMaskIndex + 1);
            }
            extMask[flag.extMaskIndex] |= flag.mask;
        }
        return extMask;
    }

    public boolean contains(FeatureFlag p_249521_) {
        if (this.universe != p_249521_.universe) {
            return false;
        }
        if (p_249521_.extMaskIndex < 0) {
            return (this.mask & p_249521_.mask) != 0L;
        }
        if (this.extendedMask.length > p_249521_.extMaskIndex) {
            return (this.extendedMask[p_249521_.extMaskIndex] & p_249521_.mask) != 0L;
        }
        return false;
    }

    public boolean isEmpty() {
        return this.equals(EMPTY);
    }

    public boolean isSubsetOf(FeatureFlagSet p_249164_) {
        if (this.universe == null) {
            return true;
        } else if (this.universe == p_249164_.universe) {
            int len = Math.max(this.extendedMask.length, p_249164_.extendedMask.length);
            for (int i = 0; i < len; i++) {
                long thisMask = i < this.extendedMask.length ? this.extendedMask[i] : 0L;
                long otherMask = i < p_249164_.extendedMask.length ? p_249164_.extendedMask[i] : 0L;
                if ((thisMask & ~otherMask) != 0L) {
                    return false;
                }
            }
            return (this.mask & ~p_249164_.mask) == 0L;
        }
        return false;
    }

    public boolean intersects(FeatureFlagSet p_341635_) {
        if (this.universe == null || p_341635_.universe == null || this.universe != p_341635_.universe) {
            return false;
        }
        int len = Math.min(this.extendedMask.length, p_341635_.extendedMask.length);
        for (int i = 0; i < len; i++) {
            long thisMask = this.extendedMask[i];
            long otherMask = p_341635_.extendedMask[i];
            if ((thisMask & otherMask) != 0L) {
                return true;
            }
        }
       return (this.mask & p_341635_.mask) != 0L;
    }

    public FeatureFlagSet join(FeatureFlagSet p_251527_) {
        if (this.universe == null) {
            return p_251527_;
        } else if (p_251527_.universe == null) {
            return this;
        } else if (this.universe != p_251527_.universe) {
            throw new IllegalArgumentException("Mismatched set elements: '" + this.universe + "' != '" + p_251527_.universe + "'");
        } else {
            long[] extMask = EMPTY_EXT_MASK;
            if (this.extendedMask.length > 0 || p_251527_.extendedMask.length > 0) {
                extMask = new long[Math.max(this.extendedMask.length, p_251527_.extendedMask.length)];
                for (int i = 0; i < extMask.length; i++) {
                    long thisMask = i < this.extendedMask.length ? this.extendedMask[i] : 0L;
                    long otherMask = i < p_251527_.extendedMask.length ? p_251527_.extendedMask[i] : 0L;
                    extMask[i] = thisMask | otherMask;
                }
            }
            return new FeatureFlagSet(this.universe, this.mask | p_251527_.mask, extMask);
        }
    }

    public FeatureFlagSet subtract(FeatureFlagSet p_341688_) {
        if (this.universe == null || p_341688_.universe == null) {
            return this;
        } else if (this.universe != p_341688_.universe) {
            throw new IllegalArgumentException("Mismatched set elements: '" + this.universe + "' != '" + p_341688_.universe + "'");
        } else {
            long i = this.mask & ~p_341688_.mask;
            long[] extMask = EMPTY_EXT_MASK;
            if (this.extendedMask.length > 0 || p_341688_.extendedMask.length > 0) {
                extMask = new long[this.extendedMask.length];
                for (int idx = 0; idx < extMask.length; idx++) {
                    long otherMask = idx < p_341688_.extendedMask.length ? p_341688_.extendedMask[idx] : 0L;
                    extMask[idx] = this.extendedMask[idx] & ~otherMask;
                }
            }
            return i == 0L && extMask.length == 0 ? EMPTY : new FeatureFlagSet(this.universe, i, extMask);
        }
    }

    @Override
    public boolean equals(Object p_248691_) {
        if (this == p_248691_) {
            return true;
        } else {
            if (p_248691_ instanceof FeatureFlagSet featureflagset && this.universe == featureflagset.universe && this.mask == featureflagset.mask && Arrays.equals(this.extendedMask, featureflagset.extendedMask)) {
                return true;
            }

            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = (int)HashCommon.mix(this.mask);
        for (long extMask : this.extendedMask) {
            hash = 13 * hash + (int) HashCommon.mix(extMask);
        }
        return hash;
    }
}
