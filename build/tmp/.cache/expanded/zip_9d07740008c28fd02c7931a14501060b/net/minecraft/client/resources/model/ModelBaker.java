package net.minecraft.client.resources.model;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.VisibleForDebug;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface ModelBaker extends net.neoforged.neoforge.client.extensions.IModelBakerExtension {
    BakedModel bake(ResourceLocation p_250776_, ModelState p_251280_);

    SpriteGetter sprites();

    @VisibleForDebug
    ModelDebugName rootName();
}
