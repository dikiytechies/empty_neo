package net.minecraft.client.data.models.blockstates;

import com.google.gson.JsonElement;
import java.util.function.Supplier;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface BlockStateGenerator extends Supplier<JsonElement> {
    Block getBlock();
}
