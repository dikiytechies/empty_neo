package net.minecraft.world.level.gameevent;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public interface PositionSourceType<T extends PositionSource> {
    PositionSourceType<BlockPositionSource> BLOCK = register("block", new BlockPositionSource.Type());
    PositionSourceType<EntityPositionSource> ENTITY = register("entity", new EntityPositionSource.Type());

    MapCodec<T> codec();

    StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec();

    static <S extends PositionSourceType<T>, T extends PositionSource> S register(String p_157878_, S p_157879_) {
        return Registry.register(BuiltInRegistries.POSITION_SOURCE_TYPE, p_157878_, p_157879_);
    }
}
