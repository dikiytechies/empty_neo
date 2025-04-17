package net.minecraft.client.renderer.entity.state;

import net.minecraft.world.entity.animal.TropicalFish;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TropicalFishRenderState extends LivingEntityRenderState {
    public TropicalFish.Pattern variant = TropicalFish.Pattern.FLOPPER;
    public int baseColor = -1;
    public int patternColor = -1;
}
