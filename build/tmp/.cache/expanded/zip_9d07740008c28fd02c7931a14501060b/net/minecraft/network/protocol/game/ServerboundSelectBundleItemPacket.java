package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ServerboundSelectBundleItemPacket(int slotId, int selectedItemIndex) implements Packet<ServerGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ServerboundSelectBundleItemPacket> STREAM_CODEC = Packet.codec(
        ServerboundSelectBundleItemPacket::write, ServerboundSelectBundleItemPacket::new
    );

    private ServerboundSelectBundleItemPacket(FriendlyByteBuf p_365065_) {
        this(p_365065_.readVarInt(), p_365065_.readVarInt());
    }

    private void write(FriendlyByteBuf p_363816_) {
        p_363816_.writeVarInt(this.slotId);
        p_363816_.writeVarInt(this.selectedItemIndex);
    }

    @Override
    public PacketType<ServerboundSelectBundleItemPacket> type() {
        return GamePacketTypes.SERVERBOUND_BUNDLE_ITEM_SELECTED;
    }

    public void handle(ServerGamePacketListener p_363439_) {
        p_363439_.handleBundleItemSelectedPacket(this);
    }
}
