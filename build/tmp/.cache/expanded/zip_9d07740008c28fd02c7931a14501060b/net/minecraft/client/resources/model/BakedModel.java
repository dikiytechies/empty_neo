package net.minecraft.client.resources.model;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface BakedModel extends net.neoforged.neoforge.client.extensions.IBakedModelExtension {
    /**@deprecated Forge: Use {@link #getQuads(BlockState, Direction, RandomSource, net.neoforged.neoforge.client.model.data.ModelData, net.minecraft.client.renderer.RenderType)}*/
    @Deprecated
    List<BakedQuad> getQuads(@Nullable BlockState p_235039_, @Nullable Direction p_235040_, RandomSource p_235041_);

    boolean useAmbientOcclusion();

    boolean isGui3d();

    boolean usesBlockLight();

    /**@deprecated Forge: Use {@link #getParticleIcon(net.neoforged.neoforge.client.model.data.ModelData)}*/
    @Deprecated
    TextureAtlasSprite getParticleIcon();

    /**@deprecated Forge: Use {@link #applyTransform(net.minecraft.world.item.ItemDisplayContext, com.mojang.blaze3d.vertex.PoseStack, boolean)} instead */
    @Deprecated
    ItemTransforms getTransforms();
}
