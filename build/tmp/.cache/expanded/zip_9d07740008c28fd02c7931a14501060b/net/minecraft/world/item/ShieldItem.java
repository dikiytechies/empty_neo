package net.minecraft.world.item;

import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class ShieldItem extends Item {
    public static final int EFFECTIVE_BLOCK_DELAY = 5;
    public static final float MINIMUM_DURABILITY_DAMAGE = 3.0F;

    public ShieldItem(Item.Properties p_43089_) {
        super(p_43089_);
    }

    @Override
    public Component getName(ItemStack p_371901_) {
        DyeColor dyecolor = p_371901_.get(DataComponents.BASE_COLOR);
        return (Component)(dyecolor != null ? Component.translatable(this.descriptionId + "." + dyecolor.getName()) : super.getName(p_371901_));
    }

    @Override
    public void appendHoverText(ItemStack p_43094_, Item.TooltipContext p_339613_, List<Component> p_43096_, TooltipFlag p_43097_) {
        BannerItem.appendHoverTextFromBannerBlockEntityTag(p_43094_, p_43096_);
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack p_43105_) {
        return ItemUseAnimation.BLOCK;
    }

    @Override
    public int getUseDuration(ItemStack p_43107_, LivingEntity p_346168_) {
        return 72000;
    }

    @Override
    public InteractionResult use(Level p_43099_, Player p_43100_, InteractionHand p_43101_) {
        p_43100_.startUsingItem(p_43101_);
        return InteractionResult.CONSUME;
    }

    @Override
    public boolean canPerformAction(ItemStack stack, net.neoforged.neoforge.common.ItemAbility itemAbility) {
        return net.neoforged.neoforge.common.ItemAbilities.DEFAULT_SHIELD_ACTIONS.contains(itemAbility);
    }
}
