package net.minecraft.world.entity.ai.behavior;

import java.util.Optional;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.behavior.declarative.MemoryAccessor;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class StartAttacking {
    public static <E extends Mob> BehaviorControl<E> create(StartAttacking.TargetFinder<E> p_376450_) {
        return create((p_376522_, p_24212_) -> true, p_376450_);
    }

    public static <E extends Mob> BehaviorControl<E> create(StartAttacking.StartAttackingCondition<E> p_376721_, StartAttacking.TargetFinder<E> p_376774_) {
        return BehaviorBuilder.create(
            p_258782_ -> p_258782_.group(p_258782_.absent(MemoryModuleType.ATTACK_TARGET), p_258782_.registered(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE))
                    .apply(p_258782_, (p_258778_, p_258779_) -> (p_375684_, p_375685_, p_375686_) -> {
                            if (!p_376721_.test(p_375684_, p_375685_)) {
                                return false;
                            } else {
                                Optional<? extends LivingEntity> optional = p_376774_.get(p_375684_, p_375685_);
                                if (optional.isEmpty()) {
                                    return false;
                                } else {
                                    LivingEntity livingentity = optional.get();
                                    if (!p_375685_.canAttack(livingentity)) {
                                        return false;
                                    } else {
                                        net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent changeTargetEvent = net.neoforged.neoforge.common.CommonHooks.onLivingChangeTarget(p_375685_, livingentity, net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent.LivingTargetType.BEHAVIOR_TARGET);
                                        if (changeTargetEvent.isCanceled() || changeTargetEvent.getNewAboutToBeSetTarget() == null)
                                            return false;

                                        p_258778_.set(changeTargetEvent.getNewAboutToBeSetTarget());
                                        p_258779_.erase();
                                        return true;
                                    }
                                }
                            }
                        })
        );
    }

    @FunctionalInterface
    public interface StartAttackingCondition<E> {
        boolean test(ServerLevel p_376866_, E p_376367_);
    }

    @FunctionalInterface
    public interface TargetFinder<E> {
        Optional<? extends LivingEntity> get(ServerLevel p_376683_, E p_376320_);
    }
}
