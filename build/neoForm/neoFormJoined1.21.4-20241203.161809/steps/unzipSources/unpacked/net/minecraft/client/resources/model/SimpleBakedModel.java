package net.minecraft.client.resources.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.block.model.TextureSlots;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SimpleBakedModel implements BakedModel {
    public static final String PARTICLE_TEXTURE_REFERENCE = "particle";
    private final List<BakedQuad> unculledFaces;
    private final Map<Direction, List<BakedQuad>> culledFaces;
    private final boolean hasAmbientOcclusion;
    private final boolean isGui3d;
    private final boolean usesBlockLight;
    private final TextureAtlasSprite particleIcon;
    private final ItemTransforms transforms;
    @Nullable protected final net.neoforged.neoforge.client.ChunkRenderTypeSet blockRenderTypes;
    @Nullable protected final net.minecraft.client.renderer.RenderType itemRenderType;

    /** @deprecated Forge: Use {@linkplain #SimpleBakedModel(List, Map, boolean, boolean, boolean, TextureAtlasSprite, ItemTransforms, net.neoforged.neoforge.client.RenderTypeGroup) variant with RenderTypeGroup} **/
    @Deprecated
    public SimpleBakedModel(
        List<BakedQuad> p_119489_,
        Map<Direction, List<BakedQuad>> p_119490_,
        boolean p_119491_,
        boolean p_119492_,
        boolean p_119493_,
        TextureAtlasSprite p_119494_,
        ItemTransforms p_119495_
    ) {
        this(p_119489_, p_119490_, p_119491_, p_119492_, p_119493_, p_119494_, p_119495_, net.neoforged.neoforge.client.RenderTypeGroup.EMPTY);
    }

    public SimpleBakedModel(
            List<BakedQuad> p_119489_,
            Map<Direction, List<BakedQuad>> p_119490_,
            boolean p_119491_,
            boolean p_119492_,
            boolean p_119493_,
            TextureAtlasSprite p_119494_,
            ItemTransforms p_119495_,
            net.neoforged.neoforge.client.RenderTypeGroup renderTypes
    ) {
        this.unculledFaces = p_119489_;
        this.culledFaces = p_119490_;
        this.hasAmbientOcclusion = p_119491_;
        this.isGui3d = p_119493_;
        this.usesBlockLight = p_119492_;
        this.particleIcon = p_119494_;
        this.transforms = p_119495_;
        this.blockRenderTypes = !renderTypes.isEmpty() ? net.neoforged.neoforge.client.ChunkRenderTypeSet.of(renderTypes.block()) : null;
        this.itemRenderType = !renderTypes.isEmpty() ? renderTypes.entity() : null;
    }

    /**
     * @deprecated Neo: Use {@linkplain #bakeElements(List, TextureSlots, SpriteGetter, ModelState, boolean, boolean, boolean, ItemTransforms, com.mojang.math.Transformation, net.neoforged.neoforge.client.RenderTypeGroup) variant with Transformation and RenderTypeGroup support}
     */
    @Deprecated
    public static BakedModel bakeElements(
        List<BlockElement> p_387963_,
        TextureSlots p_388507_,
        SpriteGetter p_387357_,
        ModelState p_388846_,
        boolean p_386975_,
        boolean p_388143_,
        boolean p_386706_,
        ItemTransforms p_388032_
    ) {
        return bakeElements(p_387963_, p_388507_, p_387357_, p_388846_, p_386975_, p_388143_, p_386706_, p_388032_, com.mojang.math.Transformation.identity(), net.neoforged.neoforge.client.RenderTypeGroup.EMPTY);
    }

    public static BakedModel bakeElements(
        List<BlockElement> p_387963_,
        TextureSlots p_388507_,
        SpriteGetter p_387357_,
        ModelState p_388846_,
        boolean p_386975_,
        boolean p_388143_,
        boolean p_386706_,
        ItemTransforms p_388032_,
        com.mojang.math.Transformation rootTransform,
        net.neoforged.neoforge.client.RenderTypeGroup renderTypes
    ) {
        TextureAtlasSprite textureatlassprite = findSprite(p_387357_, p_388507_, "particle");
        SimpleBakedModel.Builder simplebakedmodel$builder = new SimpleBakedModel.Builder(p_386975_, p_388143_, p_386706_, p_388032_)
            .particle(textureatlassprite);

        if (!rootTransform.isIdentity()) {
            p_388846_ = net.neoforged.neoforge.client.model.UnbakedElementsHelper.composeRootTransformIntoModelState(p_388846_, rootTransform);
        }

        for (BlockElement blockelement : p_387963_) {
            for (Direction direction : blockelement.faces.keySet()) {
                BlockElementFace blockelementface = blockelement.faces.get(direction);
                TextureAtlasSprite textureatlassprite1 = findSprite(p_387357_, p_388507_, blockelementface.texture());
                if (blockelementface.cullForDirection() == null) {
                    simplebakedmodel$builder.addUnculledFace(bakeFace(blockelement, blockelementface, textureatlassprite1, direction, p_388846_));
                } else {
                    simplebakedmodel$builder.addCulledFace(
                        Direction.rotate(p_388846_.getRotation().getMatrix(), blockelementface.cullForDirection()),
                        bakeFace(blockelement, blockelementface, textureatlassprite1, direction, p_388846_)
                    );
                }
            }
        }

        return simplebakedmodel$builder.build(renderTypes);
    }

    public static BakedQuad bakeFace(
        BlockElement p_388164_, BlockElementFace p_388456_, TextureAtlasSprite p_388731_, Direction p_386700_, ModelState p_386475_
    ) {
        return FaceBakery.bakeQuad(
            p_388164_.from, p_388164_.to, p_388456_, p_388731_, p_386700_, p_386475_, p_388164_.rotation, p_388164_.shade, p_388164_.lightEmission
        );
    }

    private static TextureAtlasSprite findSprite(SpriteGetter p_386599_, TextureSlots p_388750_, String p_386831_) {
        Material material = p_388750_.getMaterial(p_386831_);
        return material != null ? p_386599_.get(material) : p_386599_.reportMissingReference(p_386831_);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState p_235054_, @Nullable Direction p_235055_, RandomSource p_235056_) {
        return p_235055_ == null ? this.unculledFaces : this.culledFaces.get(p_235055_);
    }

    @Override
    public boolean useAmbientOcclusion() {
        return this.hasAmbientOcclusion;
    }

    @Override
    public boolean isGui3d() {
        return this.isGui3d;
    }

    @Override
    public boolean usesBlockLight() {
        return this.usesBlockLight;
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return this.particleIcon;
    }

    @Override
    public ItemTransforms getTransforms() {
        return this.transforms;
    }

    @Override
    public net.neoforged.neoforge.client.ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource rand, net.neoforged.neoforge.client.model.data.ModelData data) {
        if (blockRenderTypes != null)
            return blockRenderTypes;
        return BakedModel.super.getRenderTypes(state, rand, data);
    }

    @Override
    public net.minecraft.client.renderer.RenderType getRenderType(net.minecraft.world.item.ItemStack itemStack) {
        if (itemRenderType != null)
            return itemRenderType;
        return BakedModel.super.getRenderType(itemStack);
    }

    @OnlyIn(Dist.CLIENT)
    public static class Builder {
        private final ImmutableList.Builder<BakedQuad> unculledFaces = ImmutableList.builder();
        private final EnumMap<Direction, ImmutableList.Builder<BakedQuad>> culledFaces = Maps.newEnumMap(Direction.class);
        private final boolean hasAmbientOcclusion;
        @Nullable
        private TextureAtlasSprite particleIcon;
        private final boolean usesBlockLight;
        private final boolean isGui3d;
        private final ItemTransforms transforms;

        public Builder(boolean p_119519_, boolean p_371851_, boolean p_371825_, ItemTransforms p_371619_) {
            this.hasAmbientOcclusion = p_119519_;
            this.usesBlockLight = p_371851_;
            this.isGui3d = p_371825_;
            this.transforms = p_371619_;

            for (Direction direction : Direction.values()) {
                this.culledFaces.put(direction, ImmutableList.builder());
            }
        }

        public SimpleBakedModel.Builder addCulledFace(Direction p_119531_, BakedQuad p_119532_) {
            this.culledFaces.get(p_119531_).add(p_119532_);
            return this;
        }

        public SimpleBakedModel.Builder addUnculledFace(BakedQuad p_119527_) {
            this.unculledFaces.add(p_119527_);
            return this;
        }

        public SimpleBakedModel.Builder particle(TextureAtlasSprite p_119529_) {
            this.particleIcon = p_119529_;
            return this;
        }

        public SimpleBakedModel.Builder item() {
            return this;
        }

        /** @deprecated Forge: Use {@linkplain #build(net.neoforged.neoforge.client.RenderTypeGroup) variant with RenderTypeGroup} **/
        @Deprecated
        public BakedModel build() {
            return build(net.neoforged.neoforge.client.RenderTypeGroup.EMPTY);
        }

        public BakedModel build(net.neoforged.neoforge.client.RenderTypeGroup renderTypes) {
            if (this.particleIcon == null) {
                throw new RuntimeException("Missing particle!");
            } else {
                Map<Direction, List<BakedQuad>> map = Maps.transformValues(this.culledFaces, ImmutableList.Builder::build);
                return new SimpleBakedModel(
                    this.unculledFaces.build(),
                    new EnumMap<>(map),
                    this.hasAmbientOcclusion,
                    this.usesBlockLight,
                    this.isGui3d,
                    this.particleIcon,
                    this.transforms,
                    renderTypes
                );
            }
        }
    }
}
