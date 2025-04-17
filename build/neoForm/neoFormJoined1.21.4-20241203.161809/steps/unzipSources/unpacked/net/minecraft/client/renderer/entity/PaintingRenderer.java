package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.state.PaintingRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.PaintingTextureManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PaintingRenderer extends EntityRenderer<Painting, PaintingRenderState> {
    public PaintingRenderer(EntityRendererProvider.Context p_174332_) {
        super(p_174332_);
    }

    public void render(PaintingRenderState p_363740_, PoseStack p_115533_, MultiBufferSource p_115534_, int p_115535_) {
        PaintingVariant paintingvariant = p_363740_.variant;
        if (paintingvariant != null) {
            p_115533_.pushPose();
            p_115533_.mulPose(Axis.YP.rotationDegrees((float)(180 - p_363740_.direction.get2DDataValue() * 90)));
            PaintingTextureManager paintingtexturemanager = Minecraft.getInstance().getPaintingTextures();
            TextureAtlasSprite textureatlassprite = paintingtexturemanager.getBackSprite();
            VertexConsumer vertexconsumer = p_115534_.getBuffer(RenderType.entitySolidZOffsetForward(textureatlassprite.atlasLocation()));
            this.renderPainting(
                p_115533_,
                vertexconsumer,
                p_363740_.lightCoords,
                paintingvariant.width(),
                paintingvariant.height(),
                paintingtexturemanager.get(paintingvariant),
                textureatlassprite
            );
            p_115533_.popPose();
            super.render(p_363740_, p_115533_, p_115534_, p_115535_);
        }
    }

    public PaintingRenderState createRenderState() {
        return new PaintingRenderState();
    }

    public void extractRenderState(Painting p_360735_, PaintingRenderState p_364263_, float p_360460_) {
        super.extractRenderState(p_360735_, p_364263_, p_360460_);
        Direction direction = p_360735_.getDirection();
        PaintingVariant paintingvariant = p_360735_.getVariant().value();
        p_364263_.direction = direction;
        p_364263_.variant = paintingvariant;
        int i = paintingvariant.width();
        int j = paintingvariant.height();
        if (p_364263_.lightCoords.length != i * j) {
            p_364263_.lightCoords = new int[i * j];
        }

        float f = (float)(-i) / 2.0F;
        float f1 = (float)(-j) / 2.0F;
        Level level = p_360735_.level();

        for (int k = 0; k < j; k++) {
            for (int l = 0; l < i; l++) {
                float f2 = (float)l + f + 0.5F;
                float f3 = (float)k + f1 + 0.5F;
                int i1 = p_360735_.getBlockX();
                int j1 = Mth.floor(p_360735_.getY() + (double)f3);
                int k1 = p_360735_.getBlockZ();
                switch (direction) {
                    case NORTH:
                        i1 = Mth.floor(p_360735_.getX() + (double)f2);
                        break;
                    case WEST:
                        k1 = Mth.floor(p_360735_.getZ() - (double)f2);
                        break;
                    case SOUTH:
                        i1 = Mth.floor(p_360735_.getX() - (double)f2);
                        break;
                    case EAST:
                        k1 = Mth.floor(p_360735_.getZ() + (double)f2);
                }

                p_364263_.lightCoords[l + k * i] = LevelRenderer.getLightColor(level, new BlockPos(i1, j1, k1));
            }
        }
    }

    private void renderPainting(
        PoseStack p_115559_,
        VertexConsumer p_115560_,
        int[] p_363896_,
        int p_115562_,
        int p_115563_,
        TextureAtlasSprite p_115564_,
        TextureAtlasSprite p_115565_
    ) {
        PoseStack.Pose posestack$pose = p_115559_.last();
        float f = (float)(-p_115562_) / 2.0F;
        float f1 = (float)(-p_115563_) / 2.0F;
        float f2 = 0.03125F;
        float f3 = p_115565_.getU0();
        float f4 = p_115565_.getU1();
        float f5 = p_115565_.getV0();
        float f6 = p_115565_.getV1();
        float f7 = p_115565_.getU0();
        float f8 = p_115565_.getU1();
        float f9 = p_115565_.getV0();
        float f10 = p_115565_.getV(0.0625F);
        float f11 = p_115565_.getU0();
        float f12 = p_115565_.getU(0.0625F);
        float f13 = p_115565_.getV0();
        float f14 = p_115565_.getV1();
        double d0 = 1.0 / (double)p_115562_;
        double d1 = 1.0 / (double)p_115563_;

        for (int i = 0; i < p_115562_; i++) {
            for (int j = 0; j < p_115563_; j++) {
                float f15 = f + (float)(i + 1);
                float f16 = f + (float)i;
                float f17 = f1 + (float)(j + 1);
                float f18 = f1 + (float)j;
                int k = p_363896_[i + j * p_115562_];
                float f19 = p_115564_.getU((float)(d0 * (double)(p_115562_ - i)));
                float f20 = p_115564_.getU((float)(d0 * (double)(p_115562_ - (i + 1))));
                float f21 = p_115564_.getV((float)(d1 * (double)(p_115563_ - j)));
                float f22 = p_115564_.getV((float)(d1 * (double)(p_115563_ - (j + 1))));
                this.vertex(posestack$pose, p_115560_, f15, f18, f20, f21, -0.03125F, 0, 0, -1, k);
                this.vertex(posestack$pose, p_115560_, f16, f18, f19, f21, -0.03125F, 0, 0, -1, k);
                this.vertex(posestack$pose, p_115560_, f16, f17, f19, f22, -0.03125F, 0, 0, -1, k);
                this.vertex(posestack$pose, p_115560_, f15, f17, f20, f22, -0.03125F, 0, 0, -1, k);
                this.vertex(posestack$pose, p_115560_, f15, f17, f4, f5, 0.03125F, 0, 0, 1, k);
                this.vertex(posestack$pose, p_115560_, f16, f17, f3, f5, 0.03125F, 0, 0, 1, k);
                this.vertex(posestack$pose, p_115560_, f16, f18, f3, f6, 0.03125F, 0, 0, 1, k);
                this.vertex(posestack$pose, p_115560_, f15, f18, f4, f6, 0.03125F, 0, 0, 1, k);
                this.vertex(posestack$pose, p_115560_, f15, f17, f7, f9, -0.03125F, 0, 1, 0, k);
                this.vertex(posestack$pose, p_115560_, f16, f17, f8, f9, -0.03125F, 0, 1, 0, k);
                this.vertex(posestack$pose, p_115560_, f16, f17, f8, f10, 0.03125F, 0, 1, 0, k);
                this.vertex(posestack$pose, p_115560_, f15, f17, f7, f10, 0.03125F, 0, 1, 0, k);
                this.vertex(posestack$pose, p_115560_, f15, f18, f7, f9, 0.03125F, 0, -1, 0, k);
                this.vertex(posestack$pose, p_115560_, f16, f18, f8, f9, 0.03125F, 0, -1, 0, k);
                this.vertex(posestack$pose, p_115560_, f16, f18, f8, f10, -0.03125F, 0, -1, 0, k);
                this.vertex(posestack$pose, p_115560_, f15, f18, f7, f10, -0.03125F, 0, -1, 0, k);
                this.vertex(posestack$pose, p_115560_, f15, f17, f12, f13, 0.03125F, -1, 0, 0, k);
                this.vertex(posestack$pose, p_115560_, f15, f18, f12, f14, 0.03125F, -1, 0, 0, k);
                this.vertex(posestack$pose, p_115560_, f15, f18, f11, f14, -0.03125F, -1, 0, 0, k);
                this.vertex(posestack$pose, p_115560_, f15, f17, f11, f13, -0.03125F, -1, 0, 0, k);
                this.vertex(posestack$pose, p_115560_, f16, f17, f12, f13, -0.03125F, 1, 0, 0, k);
                this.vertex(posestack$pose, p_115560_, f16, f18, f12, f14, -0.03125F, 1, 0, 0, k);
                this.vertex(posestack$pose, p_115560_, f16, f18, f11, f14, 0.03125F, 1, 0, 0, k);
                this.vertex(posestack$pose, p_115560_, f16, f17, f11, f13, 0.03125F, 1, 0, 0, k);
            }
        }
    }

    private void vertex(
        PoseStack.Pose p_323991_,
        VertexConsumer p_254114_,
        float p_254164_,
        float p_254459_,
        float p_254183_,
        float p_253615_,
        float p_254448_,
        int p_253660_,
        int p_254342_,
        int p_253757_,
        int p_254101_
    ) {
        p_254114_.addVertex(p_323991_, p_254164_, p_254459_, p_254448_)
            .setColor(-1)
            .setUv(p_254183_, p_253615_)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(p_254101_)
            .setNormal(p_323991_, (float)p_253660_, (float)p_254342_, (float)p_253757_);
    }
}
