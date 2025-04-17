package net.minecraft.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Map;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderers;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SpecialBlockModelRenderer {
    public static final SpecialBlockModelRenderer EMPTY = new SpecialBlockModelRenderer(Map.of());
    private final Map<Block, SpecialModelRenderer<?>> renderers;

    public SpecialBlockModelRenderer(Map<Block, SpecialModelRenderer<?>> p_386857_) {
        this.renderers = p_386857_;
    }

    public static SpecialBlockModelRenderer vanilla(EntityModelSet p_387429_) {
        return new SpecialBlockModelRenderer(SpecialModelRenderers.createBlockRenderers(p_387429_));
    }

    public void renderByBlock(Block p_386618_, ItemDisplayContext p_386576_, PoseStack p_386646_, MultiBufferSource p_386540_, int p_388855_, int p_388672_) {
        SpecialModelRenderer<?> specialmodelrenderer = this.renderers.get(p_386618_);
        if (specialmodelrenderer != null) {
            specialmodelrenderer.render(null, p_386576_, p_386646_, p_386540_, p_388855_, p_388672_, false);
        }
    }
}
