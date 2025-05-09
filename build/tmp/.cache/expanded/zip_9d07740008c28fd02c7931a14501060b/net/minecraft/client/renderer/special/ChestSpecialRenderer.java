package net.minecraft.client.renderer.special;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import net.minecraft.client.model.ChestModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ChestSpecialRenderer implements NoDataSpecialModelRenderer {
    public static final ResourceLocation GIFT_CHEST_TEXTURE = ResourceLocation.withDefaultNamespace("christmas");
    public static final ResourceLocation NORMAL_CHEST_TEXTURE = ResourceLocation.withDefaultNamespace("normal");
    public static final ResourceLocation TRAPPED_CHEST_TEXTURE = ResourceLocation.withDefaultNamespace("trapped");
    public static final ResourceLocation ENDER_CHEST_TEXTURE = ResourceLocation.withDefaultNamespace("ender");
    private final ChestModel model;
    private final Material material;
    private final float openness;

    public ChestSpecialRenderer(ChestModel p_386863_, Material p_388350_, float p_386750_) {
        this.model = p_386863_;
        this.material = p_388350_;
        this.openness = p_386750_;
    }

    @Override
    public void render(ItemDisplayContext p_388647_, PoseStack p_387780_, MultiBufferSource p_386788_, int p_387736_, int p_387546_, boolean p_387869_) {
        VertexConsumer vertexconsumer = this.material.buffer(p_386788_, RenderType::entitySolid);
        this.model.setupAnim(this.openness);
        this.model.renderToBuffer(p_387780_, vertexconsumer, p_387736_, p_387546_);
    }

    @OnlyIn(Dist.CLIENT)
    public static record Unbaked(ResourceLocation texture, float openness) implements SpecialModelRenderer.Unbaked {
        public static final MapCodec<ChestSpecialRenderer.Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(
            p_388545_ -> p_388545_.group(
                        ResourceLocation.CODEC.fieldOf("texture").forGetter(ChestSpecialRenderer.Unbaked::texture),
                        Codec.FLOAT.optionalFieldOf("openness", Float.valueOf(0.0F)).forGetter(ChestSpecialRenderer.Unbaked::openness)
                    )
                    .apply(p_388545_, ChestSpecialRenderer.Unbaked::new)
        );

        public Unbaked(ResourceLocation p_387139_) {
            this(p_387139_, 0.0F);
        }

        @Override
        public MapCodec<ChestSpecialRenderer.Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public SpecialModelRenderer<?> bake(EntityModelSet p_387681_) {
            ChestModel chestmodel = new ChestModel(p_387681_.bakeLayer(ModelLayers.CHEST));
            Material material = Sheets.chestMaterial(this.texture);
            return new ChestSpecialRenderer(chestmodel, material, this.openness);
        }
    }
}
