package net.minecraft.client.gui.components;

import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface Renderable {
    void render(GuiGraphics p_281245_, int p_253973_, int p_254325_, float p_254004_);
}
