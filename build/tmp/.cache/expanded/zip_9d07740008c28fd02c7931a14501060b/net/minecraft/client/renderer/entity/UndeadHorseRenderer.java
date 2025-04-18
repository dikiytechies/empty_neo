package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.AbstractEquineModel;
import net.minecraft.client.model.HorseModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.state.EquineRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class UndeadHorseRenderer extends AbstractHorseRenderer<AbstractHorse, EquineRenderState, AbstractEquineModel<EquineRenderState>> {
    private static final ResourceLocation ZOMBIE_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/horse/horse_zombie.png");
    private static final ResourceLocation SKELETON_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/horse/horse_skeleton.png");
    private final ResourceLocation texture;

    public UndeadHorseRenderer(EntityRendererProvider.Context p_174432_, ModelLayerLocation p_174433_, ModelLayerLocation p_362093_, boolean p_362891_) {
        super(p_174432_, new HorseModel(p_174432_.bakeLayer(p_174433_)), new HorseModel(p_174432_.bakeLayer(p_362093_)));
        this.texture = p_362891_ ? SKELETON_TEXTURE : ZOMBIE_TEXTURE;
    }

    public ResourceLocation getTextureLocation(EquineRenderState p_362480_) {
        return this.texture;
    }

    public EquineRenderState createRenderState() {
        return new EquineRenderState();
    }
}
