package net.minecraft.client.renderer.item.properties.numeric;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CrossbowPull implements RangeSelectItemModelProperty {
    public static final MapCodec<CrossbowPull> MAP_CODEC = MapCodec.unit(new CrossbowPull());

    @Override
    public float get(ItemStack p_387470_, @Nullable ClientLevel p_387947_, @Nullable LivingEntity p_388564_, int p_388371_) {
        if (p_388564_ == null) {
            return 0.0F;
        } else if (CrossbowItem.isCharged(p_387470_)) {
            return 0.0F;
        } else {
            int i = CrossbowItem.getChargeDuration(p_387470_, p_388564_);
            return (float)UseDuration.useDuration(p_387470_, p_388564_) / (float)i;
        }
    }

    @Override
    public MapCodec<CrossbowPull> type() {
        return MAP_CODEC;
    }
}
