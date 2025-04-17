package com.mojang.blaze3d.buffers;

import com.mojang.blaze3d.platform.GlStateManager;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GpuFence implements AutoCloseable {
    private long handle = GlStateManager._glFenceSync(37143, 0);

    @Override
    public void close() {
        if (this.handle != 0L) {
            GlStateManager._glDeleteSync(this.handle);
            this.handle = 0L;
        }
    }

    public boolean awaitCompletion(long p_374551_) {
        if (this.handle == 0L) {
            return true;
        } else {
            int i = GlStateManager._glClientWaitSync(this.handle, 0, p_374551_);
            if (i == 37147) {
                return false;
            } else if (i == 37149) {
                throw new IllegalStateException("Failed to complete gpu fence");
            } else {
                return true;
            }
        }
    }
}
