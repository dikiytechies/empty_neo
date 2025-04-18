package net.minecraft.client.renderer.entity.state;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class StriderRenderState extends LivingEntityRenderState implements SaddleableRenderState {
    public boolean isSaddled;
    public boolean isSuffocating;
    public boolean isRidden;

    @Override
    public boolean isSaddled() {
        return this.isSaddled;
    }
}
