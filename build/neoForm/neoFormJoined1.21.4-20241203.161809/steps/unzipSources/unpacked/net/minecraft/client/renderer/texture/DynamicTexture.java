package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.file.Path;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class DynamicTexture extends AbstractTexture implements Dumpable {
    private static final Logger LOGGER = LogUtils.getLogger();
    @Nullable
    private NativeImage pixels;

    public DynamicTexture(NativeImage p_117984_) {
        this.pixels = p_117984_;
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> {
                TextureUtil.prepareImage(this.getId(), this.pixels.getWidth(), this.pixels.getHeight());
                this.upload();
            });
        } else {
            TextureUtil.prepareImage(this.getId(), this.pixels.getWidth(), this.pixels.getHeight());
            this.upload();
        }
    }

    public DynamicTexture(int p_117980_, int p_117981_, boolean p_117982_) {
        this.pixels = new NativeImage(p_117980_, p_117981_, p_117982_);
        TextureUtil.prepareImage(this.getId(), this.pixels.getWidth(), this.pixels.getHeight());
    }

    public void upload() {
        if (this.pixels != null) {
            this.bind();
            this.pixels.upload(0, 0, 0, false);
        } else {
            LOGGER.warn("Trying to upload disposed texture {}", this.getId());
        }
    }

    @Nullable
    public NativeImage getPixels() {
        return this.pixels;
    }

    public void setPixels(NativeImage p_117989_) {
        if (this.pixels != null) {
            this.pixels.close();
        }

        this.pixels = p_117989_;
    }

    @Override
    public void close() {
        if (this.pixels != null) {
            this.pixels.close();
            this.releaseId();
            this.pixels = null;
        }
    }

    @Override
    public void dumpContents(ResourceLocation p_276119_, Path p_276105_) throws IOException {
        if (this.pixels != null) {
            String s = p_276119_.toDebugFileName() + ".png";
            Path path = p_276105_.resolve(s);
            this.pixels.writeToFile(path);
        }
    }
}
