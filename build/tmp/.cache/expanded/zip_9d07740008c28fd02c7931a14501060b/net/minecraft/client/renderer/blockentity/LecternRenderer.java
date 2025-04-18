package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.BookModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LecternRenderer implements BlockEntityRenderer<LecternBlockEntity> {
    private final BookModel bookModel;

    public LecternRenderer(BlockEntityRendererProvider.Context p_173621_) {
        this.bookModel = new BookModel(p_173621_.bakeLayer(ModelLayers.BOOK));
    }

    public void render(LecternBlockEntity p_112435_, float p_112436_, PoseStack p_112437_, MultiBufferSource p_112438_, int p_112439_, int p_112440_) {
        BlockState blockstate = p_112435_.getBlockState();
        if (blockstate.getValue(LecternBlock.HAS_BOOK)) {
            p_112437_.pushPose();
            p_112437_.translate(0.5F, 1.0625F, 0.5F);
            float f = blockstate.getValue(LecternBlock.FACING).getClockWise().toYRot();
            p_112437_.mulPose(Axis.YP.rotationDegrees(-f));
            p_112437_.mulPose(Axis.ZP.rotationDegrees(67.5F));
            p_112437_.translate(0.0F, -0.125F, 0.0F);
            this.bookModel.setupAnim(0.0F, 0.1F, 0.9F, 1.2F);
            VertexConsumer vertexconsumer = EnchantTableRenderer.BOOK_LOCATION.buffer(p_112438_, RenderType::entitySolid);
            this.bookModel.renderToBuffer(p_112437_, vertexconsumer, p_112439_, p_112440_);
            p_112437_.popPose();
        }
    }

    @Override
    public net.minecraft.world.phys.AABB getRenderBoundingBox(LecternBlockEntity blockEntity) {
        net.minecraft.core.BlockPos pos = blockEntity.getBlockPos();
        return new net.minecraft.world.phys.AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1.0, pos.getY() + 1.5, pos.getZ() + 1.0);
    }
}
