package net.minecraft.world.item.consume_effects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public record RemoveStatusEffectsConsumeEffect(HolderSet<MobEffect> effects) implements ConsumeEffect {
    public static final MapCodec<RemoveStatusEffectsConsumeEffect> CODEC = RecordCodecBuilder.mapCodec(
        p_366786_ -> p_366786_.group(
                    RegistryCodecs.homogeneousList(Registries.MOB_EFFECT).fieldOf("effects").forGetter(RemoveStatusEffectsConsumeEffect::effects)
                )
                .apply(p_366786_, RemoveStatusEffectsConsumeEffect::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, RemoveStatusEffectsConsumeEffect> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.holderSet(Registries.MOB_EFFECT), RemoveStatusEffectsConsumeEffect::effects, RemoveStatusEffectsConsumeEffect::new
    );

    public RemoveStatusEffectsConsumeEffect(Holder<MobEffect> p_366636_) {
        this(HolderSet.direct(p_366636_));
    }

    @Override
    public ConsumeEffect.Type<RemoveStatusEffectsConsumeEffect> getType() {
        return ConsumeEffect.Type.REMOVE_EFFECTS;
    }

    @Override
    public boolean apply(Level p_366881_, ItemStack p_366460_, LivingEntity p_366649_) {
        boolean flag = false;

        for (Holder<MobEffect> holder : this.effects) {
            if (p_366649_.removeEffect(holder)) {
                flag = true;
            }
        }

        return flag;
    }
}
