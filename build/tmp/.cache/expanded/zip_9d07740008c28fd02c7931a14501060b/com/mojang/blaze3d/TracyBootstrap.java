package com.mojang.blaze3d;

import com.mojang.jtracy.TracyClient;
import com.mojang.logging.LogListeners;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.slf4j.event.Level;

@OnlyIn(Dist.CLIENT)
public class TracyBootstrap {
    private static boolean setup;

    public static void setup() {
        if (!setup) {
            TracyClient.load();
            if (TracyClient.isAvailable()) {
                LogListeners.addListener("Tracy", (p_372959_, p_373056_) -> TracyClient.message(p_372959_, messageColor(p_373056_)));
                setup = true;
            }
        }
    }

    private static int messageColor(Level p_373043_) {
        return switch (p_373043_) {
            case DEBUG -> 11184810;
            case WARN -> 16777130;
            case ERROR -> 16755370;
            default -> 16777215;
        };
    }
}
