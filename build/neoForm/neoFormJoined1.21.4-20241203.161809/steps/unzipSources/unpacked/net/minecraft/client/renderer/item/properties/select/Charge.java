package net.minecraft.client.renderer.item.properties.select;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public record Charge() implements SelectItemModelProperty<CrossbowItem.ChargeType> {
    public static final SelectItemModelProperty.Type<Charge, CrossbowItem.ChargeType> TYPE = SelectItemModelProperty.Type.create(
        MapCodec.unit(new Charge()), CrossbowItem.ChargeType.CODEC
    );

    public CrossbowItem.ChargeType get(
        ItemStack p_387321_, @Nullable ClientLevel p_387482_, @Nullable LivingEntity p_387912_, int p_387536_, ItemDisplayContext p_387489_
    ) {
        ChargedProjectiles chargedprojectiles = p_387321_.get(DataComponents.CHARGED_PROJECTILES);
        if (chargedprojectiles == null || chargedprojectiles.isEmpty()) {
            return CrossbowItem.ChargeType.NONE;
        } else {
            return chargedprojectiles.contains(Items.FIREWORK_ROCKET) ? CrossbowItem.ChargeType.ROCKET : CrossbowItem.ChargeType.ARROW;
        }
    }

    @Override
    public SelectItemModelProperty.Type<Charge, CrossbowItem.ChargeType> type() {
        return TYPE;
    }
}
