package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.HolderGetter;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class PlayerTrigger extends SimpleCriterionTrigger<PlayerTrigger.TriggerInstance> {
    @Override
    public Codec<PlayerTrigger.TriggerInstance> codec() {
        return PlayerTrigger.TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer p_222619_) {
        this.trigger(p_222619_, p_222625_ -> true);
    }

    public static record TriggerInstance(Optional<ContextAwarePredicate> player) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<PlayerTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create(
            p_337390_ -> p_337390_.group(EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(PlayerTrigger.TriggerInstance::player))
                    .apply(p_337390_, PlayerTrigger.TriggerInstance::new)
        );

        public static Criterion<PlayerTrigger.TriggerInstance> located(LocationPredicate.Builder p_301139_) {
            return CriteriaTriggers.LOCATION
                .createCriterion(new PlayerTrigger.TriggerInstance(Optional.of(EntityPredicate.wrap(EntityPredicate.Builder.entity().located(p_301139_)))));
        }

        public static Criterion<PlayerTrigger.TriggerInstance> located(EntityPredicate.Builder p_300962_) {
            return CriteriaTriggers.LOCATION.createCriterion(new PlayerTrigger.TriggerInstance(Optional.of(EntityPredicate.wrap(p_300962_.build()))));
        }

        public static Criterion<PlayerTrigger.TriggerInstance> located(Optional<EntityPredicate> p_301141_) {
            return CriteriaTriggers.LOCATION.createCriterion(new PlayerTrigger.TriggerInstance(EntityPredicate.wrap(p_301141_)));
        }

        public static Criterion<PlayerTrigger.TriggerInstance> sleptInBed() {
            return CriteriaTriggers.SLEPT_IN_BED.createCriterion(new PlayerTrigger.TriggerInstance(Optional.empty()));
        }

        public static Criterion<PlayerTrigger.TriggerInstance> raidWon() {
            return CriteriaTriggers.RAID_WIN.createCriterion(new PlayerTrigger.TriggerInstance(Optional.empty()));
        }

        public static Criterion<PlayerTrigger.TriggerInstance> avoidVibration() {
            return CriteriaTriggers.AVOID_VIBRATION.createCriterion(new PlayerTrigger.TriggerInstance(Optional.empty()));
        }

        public static Criterion<PlayerTrigger.TriggerInstance> tick() {
            return CriteriaTriggers.TICK.createCriterion(new PlayerTrigger.TriggerInstance(Optional.empty()));
        }

        public static Criterion<PlayerTrigger.TriggerInstance> walkOnBlockWithEquipment(
            HolderGetter<Block> p_361038_, HolderGetter<Item> p_365016_, Block p_222638_, Item p_222639_
        ) {
            return located(
                EntityPredicate.Builder.entity()
                    .equipment(EntityEquipmentPredicate.Builder.equipment().feet(ItemPredicate.Builder.item().of(p_365016_, p_222639_)))
                    .steppingOn(LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(p_361038_, p_222638_)))
            );
        }
    }
}
