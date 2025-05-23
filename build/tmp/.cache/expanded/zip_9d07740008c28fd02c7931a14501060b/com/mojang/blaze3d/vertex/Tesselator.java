package com.mojang.blaze3d.vertex;

import javax.annotation.Nullable;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Tesselator {
    private static final int MAX_BYTES = 786432;
    private final ByteBufferBuilder buffer;
    @Nullable
    private static Tesselator instance;

    public static void init() {
        if (instance != null) {
            throw new IllegalStateException("Tesselator has already been initialized");
        } else {
            instance = new Tesselator();
        }
    }

    public static Tesselator getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Tesselator has not been initialized");
        } else {
            return instance;
        }
    }

    public Tesselator(int p_85912_) {
        this.buffer = new ByteBufferBuilder(p_85912_);
    }

    public Tesselator() {
        this(786432);
    }

    public BufferBuilder begin(VertexFormat.Mode p_350571_, VertexFormat p_350837_) {
        return new BufferBuilder(this.buffer, p_350571_, p_350837_);
    }

    public void clear() {
        this.buffer.clear();
    }
}
