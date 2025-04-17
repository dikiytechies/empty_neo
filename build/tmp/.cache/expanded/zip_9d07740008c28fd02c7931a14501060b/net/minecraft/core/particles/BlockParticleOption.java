package net.minecraft.core.particles;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class BlockParticleOption implements ParticleOptions {
    private static final Codec<BlockState> BLOCK_STATE_CODEC = Codec.withAlternative(
        BlockState.CODEC, BuiltInRegistries.BLOCK.byNameCodec(), Block::defaultBlockState
    );
    private final ParticleType<BlockParticleOption> type;
    private final BlockState state;
    /** Neo: Position of the block this particle was spawned for, if available, to provide model data for the particle texture selection */
    @org.jetbrains.annotations.Nullable
    private final net.minecraft.core.BlockPos pos;

    public static MapCodec<BlockParticleOption> codec(ParticleType<BlockParticleOption> p_123635_) {
        return BLOCK_STATE_CODEC.xmap(p_123638_ -> new BlockParticleOption(p_123635_, p_123638_), p_123633_ -> p_123633_.state).fieldOf("block_state");
    }

    public static StreamCodec<? super RegistryFriendlyByteBuf, BlockParticleOption> streamCodec(ParticleType<BlockParticleOption> p_320740_) {
        return StreamCodec.composite(
                ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY),
                option -> option.state,
                net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs.connectionAware(
                        ByteBufCodecs.optional(net.minecraft.core.BlockPos.STREAM_CODEC),
                        net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs.uncheckedUnit(java.util.Optional.empty())
                ),
                option -> java.util.Optional.ofNullable(option.pos),
                (state, pos) -> new BlockParticleOption(p_320740_, state, pos.orElse(null))
        );
    }

    public BlockParticleOption(ParticleType<BlockParticleOption> p_123629_, BlockState p_123630_) {
        this(p_123629_, p_123630_, null);
    }

    /**
     * Neo: construct a {@link BlockParticleOption} for the given type and {@link BlockState} and optionally the position
     * of the block this particle is being spawned for
     */
    public BlockParticleOption(ParticleType<BlockParticleOption> p_123629_, BlockState p_123630_, @org.jetbrains.annotations.Nullable net.minecraft.core.BlockPos pos) {
        this.type = p_123629_;
        this.state = p_123630_;
        this.pos = pos;
    }

    @Override
    public ParticleType<BlockParticleOption> getType() {
        return this.type;
    }

    public BlockState getState() {
        return this.state;
    }

    /**
     * Neo: returns the position of the block this particle was spawned for, if available
     */
    @org.jetbrains.annotations.Nullable
    public net.minecraft.core.BlockPos getPos() {
        return pos;
    }
}
