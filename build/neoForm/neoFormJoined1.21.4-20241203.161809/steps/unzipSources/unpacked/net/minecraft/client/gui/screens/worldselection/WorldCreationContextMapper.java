package net.minecraft.client.gui.screens.worldselection;

import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.ReloadableServerResources;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@FunctionalInterface
@OnlyIn(Dist.CLIENT)
public interface WorldCreationContextMapper {
    WorldCreationContext apply(ReloadableServerResources p_372907_, LayeredRegistryAccess<RegistryLayer> p_373063_, DataPackReloadCookie p_373074_);
}
