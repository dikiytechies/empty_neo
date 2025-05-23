package com.mojang.blaze3d.platform;

import net.minecraft.Util;
import net.minecraft.client.InactivityFpsLimit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FramerateLimitTracker {
    private static final int OUT_OF_LEVEL_MENU_LIMIT = 60;
    private static final int ICONIFIED_WINDOW_LIMIT = 10;
    private static final int AFK_LIMIT = 30;
    private static final int LONG_AFK_LIMIT = 10;
    private static final long AFK_THRESHOLD_MS = 60000L;
    private static final long LONG_AFK_THRESHOLD_MS = 600000L;
    private final Options options;
    private final Minecraft minecraft;
    private int framerateLimit;
    private long latestInputTime;

    public FramerateLimitTracker(Options p_360283_, Minecraft p_361436_) {
        this.options = p_360283_;
        this.minecraft = p_361436_;
        this.framerateLimit = p_360283_.framerateLimit().get();
    }

    public int getFramerateLimit() {
        InactivityFpsLimit inactivityfpslimit = this.options.inactivityFpsLimit().get();
        if (this.minecraft.getWindow().isIconified()) {
            return 10;
        } else {
            if (inactivityfpslimit == InactivityFpsLimit.AFK) {
                long i = Util.getMillis() - this.latestInputTime;
                if (i > 600000L) {
                    return 10;
                }

                if (i > 60000L) {
                    return Math.min(this.framerateLimit, 30);
                }
            }

            return this.minecraft.level != null || this.minecraft.screen == null && this.minecraft.getOverlay() == null ? this.framerateLimit : 60;
        }
    }

    public void setFramerateLimit(int p_362214_) {
        this.framerateLimit = p_362214_;
    }

    public void onInputReceived() {
        this.latestInputTime = Util.getMillis();
    }
}
