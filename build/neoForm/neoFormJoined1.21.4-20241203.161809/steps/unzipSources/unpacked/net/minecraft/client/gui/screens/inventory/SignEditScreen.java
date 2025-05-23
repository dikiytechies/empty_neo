package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.vertex.VertexConsumer;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class SignEditScreen extends AbstractSignEditScreen {
    public static final float MAGIC_SCALE_NUMBER = 62.500004F;
    public static final float MAGIC_TEXT_SCALE = 0.9765628F;
    private static final Vector3f TEXT_SCALE = new Vector3f(0.9765628F, 0.9765628F, 0.9765628F);
    @Nullable
    private Model signModel;

    public SignEditScreen(SignBlockEntity p_277919_, boolean p_277579_, boolean p_277693_) {
        super(p_277919_, p_277579_, p_277693_);
    }

    @Override
    protected void init() {
        super.init();
        boolean flag = this.sign.getBlockState().getBlock() instanceof StandingSignBlock;
        this.signModel = SignRenderer.createSignModel(this.minecraft.getEntityModels(), this.woodType, flag);
    }

    @Override
    protected void offsetSign(GuiGraphics p_282503_, BlockState p_282571_) {
        super.offsetSign(p_282503_, p_282571_);
        boolean flag = p_282571_.getBlock() instanceof StandingSignBlock;
        if (!flag) {
            p_282503_.pose().translate(0.0F, 35.0F, 0.0F);
        }
    }

    @Override
    protected void renderSignBackground(GuiGraphics p_281440_) {
        if (this.signModel != null) {
            p_281440_.pose().translate(0.0F, 31.0F, 0.0F);
            p_281440_.pose().scale(62.500004F, 62.500004F, -62.500004F);
            p_281440_.drawSpecial(p_371725_ -> {
                Material material = Sheets.getSignMaterial(this.woodType);
                VertexConsumer vertexconsumer = material.buffer(p_371725_, this.signModel::renderType);
                this.signModel.renderToBuffer(p_281440_.pose(), vertexconsumer, 15728880, OverlayTexture.NO_OVERLAY);
            });
        }
    }

    @Override
    protected Vector3f getSignTextScale() {
        return TEXT_SCALE;
    }
}
