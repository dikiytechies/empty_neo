package net.minecraft.network.protocol.game;

import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public class ClientboundSoundPacket implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundSoundPacket> STREAM_CODEC = Packet.codec(
        ClientboundSoundPacket::write, ClientboundSoundPacket::new
    );
    public static final float LOCATION_ACCURACY = 8.0F;
    private final Holder<SoundEvent> sound;
    private final SoundSource source;
    private final int x;
    private final int y;
    private final int z;
    private final float volume;
    private final float pitch;
    private final long seed;

    public ClientboundSoundPacket(
        Holder<SoundEvent> p_263366_,
        SoundSource p_263375_,
        double p_263378_,
        double p_263367_,
        double p_263394_,
        float p_263415_,
        float p_263399_,
        long p_263409_
    ) {
        this.sound = p_263366_;
        this.source = p_263375_;
        this.x = (int)(p_263378_ * 8.0);
        this.y = (int)(p_263367_ * 8.0);
        this.z = (int)(p_263394_ * 8.0);
        this.volume = p_263415_;
        this.pitch = p_263399_;
        this.seed = p_263409_;
    }

    private ClientboundSoundPacket(RegistryFriendlyByteBuf p_320320_) {
        this.sound = SoundEvent.STREAM_CODEC.decode(p_320320_);
        this.source = p_320320_.readEnum(SoundSource.class);
        this.x = p_320320_.readInt();
        this.y = p_320320_.readInt();
        this.z = p_320320_.readInt();
        this.volume = p_320320_.readFloat();
        this.pitch = p_320320_.readFloat();
        this.seed = p_320320_.readLong();
    }

    private void write(RegistryFriendlyByteBuf p_320266_) {
        SoundEvent.STREAM_CODEC.encode(p_320266_, this.sound);
        p_320266_.writeEnum(this.source);
        p_320266_.writeInt(this.x);
        p_320266_.writeInt(this.y);
        p_320266_.writeInt(this.z);
        p_320266_.writeFloat(this.volume);
        p_320266_.writeFloat(this.pitch);
        p_320266_.writeLong(this.seed);
    }

    @Override
    public PacketType<ClientboundSoundPacket> type() {
        return GamePacketTypes.CLIENTBOUND_SOUND;
    }

    public void handle(ClientGamePacketListener p_133454_) {
        p_133454_.handleSoundEvent(this);
    }

    public Holder<SoundEvent> getSound() {
        return this.sound;
    }

    public SoundSource getSource() {
        return this.source;
    }

    public double getX() {
        return (double)((float)this.x / 8.0F);
    }

    public double getY() {
        return (double)((float)this.y / 8.0F);
    }

    public double getZ() {
        return (double)((float)this.z / 8.0F);
    }

    public float getVolume() {
        return this.volume;
    }

    public float getPitch() {
        return this.pitch;
    }

    public long getSeed() {
        return this.seed;
    }
}
