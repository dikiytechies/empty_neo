package com.mojang.blaze3d.resource;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public record RenderTargetDescriptor(int width, int height, boolean useDepth, boolean useStencil) implements ResourceDescriptor<RenderTarget> {
    public RenderTargetDescriptor(int width, int height, boolean useDepth) {
        this(width, height, useDepth, false);
    }

    public RenderTarget allocate() {
        return new TextureTarget(this.width, this.height, this.useDepth, this.useStencil);
    }

    public void free(RenderTarget p_363223_) {
        p_363223_.destroyBuffers();
    }
}
