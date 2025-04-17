package net.minecraft.world.item.consume_effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface ConsumeEffect {
    Codec<ConsumeEffect> CODEC = BuiltInRegistries.CONSUME_EFFECT_TYPE.byNameCodec().dispatch(ConsumeEffect::getType, ConsumeEffect.Type::codec);
    StreamCodec<RegistryFriendlyByteBuf, ConsumeEffect> STREAM_CODEC = ByteBufCodecs.registry(Registries.CONSUME_EFFECT_TYPE)
        .dispatch(ConsumeEffect::getType, ConsumeEffect.Type::streamCodec);

    ConsumeEffect.Type<? extends ConsumeEffect> getType();

    boolean apply(Level p_366583_, ItemStack p_366466_, LivingEntity p_366759_);

    public static record Type<T extends ConsumeEffect>(MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {
        public static final ConsumeEffect.Type<ApplyStatusEffectsConsumeEffect> APPLY_EFFECTS = register(
            "apply_effects", ApplyStatusEffectsConsumeEffect.CODEC, ApplyStatusEffectsConsumeEffect.STREAM_CODEC
        );
        public static final ConsumeEffect.Type<RemoveStatusEffectsConsumeEffect> REMOVE_EFFECTS = register(
            "remove_effects", RemoveStatusEffectsConsumeEffect.CODEC, RemoveStatusEffectsConsumeEffect.STREAM_CODEC
        );
        public static final ConsumeEffect.Type<ClearAllStatusEffectsConsumeEffect> CLEAR_ALL_EFFECTS = register(
            "clear_all_effects", ClearAllStatusEffectsConsumeEffect.CODEC, ClearAllStatusEffectsConsumeEffect.STREAM_CODEC
        );
        public static final ConsumeEffect.Type<TeleportRandomlyConsumeEffect> TELEPORT_RANDOMLY = register(
            "teleport_randomly", TeleportRandomlyConsumeEffect.CODEC, TeleportRandomlyConsumeEffect.STREAM_CODEC
        );
        public static final ConsumeEffect.Type<PlaySoundConsumeEffect> PLAY_SOUND = register(
            "play_sound", PlaySoundConsumeEffect.CODEC, PlaySoundConsumeEffect.STREAM_CODEC
        );

        private static <T extends ConsumeEffect> ConsumeEffect.Type<T> register(
            String p_366473_, MapCodec<T> p_366539_, StreamCodec<RegistryFriendlyByteBuf, T> p_366490_
        ) {
            return Registry.register(BuiltInRegistries.CONSUME_EFFECT_TYPE, p_366473_, new ConsumeEffect.Type<>(p_366539_, p_366490_));
        }
    }
}
