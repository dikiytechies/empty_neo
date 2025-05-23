package net.minecraft.network.protocol.configuration;

import io.netty.buffer.ByteBuf;
import java.util.List;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.server.packs.repository.KnownPack;

public record ServerboundSelectKnownPacks(List<KnownPack> knownPacks) implements Packet<ServerConfigurationPacketListener> {
    public static final StreamCodec<ByteBuf, ServerboundSelectKnownPacks> STREAM_CODEC = StreamCodec.composite(
        // Neo: increase cap on number of KnownPacks to sync back to the server. This is safe even on vanilla connections, as no more packs will be synced here than were received in the S2C packet
        KnownPack.STREAM_CODEC.apply(ByteBufCodecs.list(1024)), ServerboundSelectKnownPacks::knownPacks, ServerboundSelectKnownPacks::new
    );

    @Override
    public PacketType<ServerboundSelectKnownPacks> type() {
        return ConfigurationPacketTypes.SERVERBOUND_SELECT_KNOWN_PACKS;
    }

    public void handle(ServerConfigurationPacketListener p_326477_) {
        p_326477_.handleSelectKnownPacks(this);
    }
}
