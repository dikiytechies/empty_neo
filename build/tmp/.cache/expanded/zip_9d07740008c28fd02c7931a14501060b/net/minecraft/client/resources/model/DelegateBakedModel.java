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
public abstract class DelegateBakedModel implements BakedModel {
    protected final BakedModel parent;

    public DelegateBakedModel(BakedModel p_371910_) {
        this.parent = p_371910_;
    }

    @Override
    @Deprecated
    public List<BakedQuad> getQuads(@Nullable BlockState p_371320_, @Nullable Direction p_371369_, RandomSource p_371947_) {
        return this.parent.getQuads(p_371320_, p_371369_, p_371947_);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState p_371320_, @Nullable Direction p_371369_, RandomSource p_371947_, net.neoforged.neoforge.client.model.data.ModelData modelData, @org.jetbrains.annotations.Nullable net.minecraft.client.renderer.RenderType renderType) {
        return this.parent.getQuads(p_371320_, p_371369_, p_371947_, modelData, renderType);
    }

    @Override
    public boolean useAmbientOcclusion() {
        return this.parent.useAmbientOcclusion();
    }

    @Override
    public net.neoforged.neoforge.common.util.TriState useAmbientOcclusion(BlockState state, net.neoforged.neoforge.client.model.data.ModelData modelData, net.minecraft.client.renderer.RenderType renderType) {
        return this.parent.useAmbientOcclusion(state, modelData, renderType);
    }

    @Override
    public boolean isGui3d() {
        return this.parent.isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        return this.parent.usesBlockLight();
    }

    @Override
    @Deprecated
    public TextureAtlasSprite getParticleIcon() {
        return this.parent.getParticleIcon();
    }

    @Override
    public TextureAtlasSprite getParticleIcon(net.neoforged.neoforge.client.model.data.ModelData modelData) {
        return this.parent.getParticleIcon(modelData);
    }

    @Override
    @Deprecated
    public ItemTransforms getTransforms() {
        return this.parent.getTransforms();
    }

    @Override
    public void applyTransform(net.minecraft.world.item.ItemDisplayContext transformType, com.mojang.blaze3d.vertex.PoseStack poseStack, boolean applyLeftHandTransform) {
        this.parent.applyTransform(transformType, poseStack, applyLeftHandTransform);
    }

    @Override
    public net.neoforged.neoforge.client.model.data.ModelData getModelData(net.minecraft.world.level.BlockAndTintGetter level, net.minecraft.core.BlockPos pos, BlockState state, net.neoforged.neoforge.client.model.data.ModelData modelData) {
        return this.parent.getModelData(level, pos, state, modelData);
    }

    @Override
    public net.neoforged.neoforge.client.ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource rand, net.neoforged.neoforge.client.model.data.ModelData data) {
        return this.parent.getRenderTypes(state, rand, data);
    }

    @Override
    public net.minecraft.client.renderer.RenderType getRenderType(net.minecraft.world.item.ItemStack itemStack) {
        return this.parent.getRenderType(itemStack);
    }

    @Override
    public List<BakedModel> getRenderPasses(net.minecraft.world.item.ItemStack itemStack) {
        return this.parent.getRenderPasses(itemStack);
    }
}
