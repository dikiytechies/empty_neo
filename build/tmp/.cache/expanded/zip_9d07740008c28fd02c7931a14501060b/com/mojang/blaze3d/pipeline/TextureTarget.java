package com.mojang.blaze3d.pipeline;

import com.mojang.blaze3d.systems.RenderSystem;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TextureTarget extends RenderTarget {
    public TextureTarget(int p_166213_, int p_166214_, boolean p_166215_) {
        this(p_166213_, p_166214_, p_166215_, false);
    }
    public TextureTarget(int p_166213_, int p_166214_, boolean p_166215_, boolean useStencil) {
        super(p_166215_, useStencil);
        RenderSystem.assertOnRenderThreadOrInit();
        this.resize(p_166213_, p_166214_);
    }
}
