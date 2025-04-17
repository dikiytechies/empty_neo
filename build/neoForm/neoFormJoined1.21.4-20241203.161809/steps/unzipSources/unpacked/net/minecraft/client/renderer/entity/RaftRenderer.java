package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.RaftModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.state.BoatRenderState;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RaftRenderer extends AbstractBoatRenderer {
    private final EntityModel<BoatRenderState> model;
    private final ResourceLocation texture;

    public RaftRenderer(EntityRendererProvider.Context p_376701_, ModelLayerLocation p_376645_) {
        super(p_376701_);
        this.texture = p_376645_.model().withPath(p_376133_ -> "textures/entity/" + p_376133_ + ".png");
        this.model = new RaftModel(p_376701_.bakeLayer(p_376645_));
    }

    @Override
    protected EntityModel<BoatRenderState> model() {
        return this.model;
    }

    @Override
    protected RenderType renderType() {
        return this.model.renderType(this.texture);
    }
}
