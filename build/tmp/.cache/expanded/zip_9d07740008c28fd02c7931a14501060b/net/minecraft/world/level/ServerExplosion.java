package net.minecraft.world.level;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class ServerExplosion implements Explosion {
    private static final ExplosionDamageCalculator EXPLOSION_DAMAGE_CALCULATOR = new ExplosionDamageCalculator();
    private static final int MAX_DROPS_PER_COMBINED_STACK = 16;
    private static final float LARGE_EXPLOSION_RADIUS = 2.0F;
    private final boolean fire;
    private final Explosion.BlockInteraction blockInteraction;
    private final ServerLevel level;
    private final Vec3 center;
    @Nullable
    private final Entity source;
    private final float radius;
    private final DamageSource damageSource;
    private final ExplosionDamageCalculator damageCalculator;
    private final Map<Player, Vec3> hitPlayers = new HashMap<>();

    public ServerExplosion(
        ServerLevel p_361323_,
        @Nullable Entity p_365060_,
        @Nullable DamageSource p_360713_,
        @Nullable ExplosionDamageCalculator p_360818_,
        Vec3 p_365404_,
        float p_365227_,
        boolean p_365067_,
        Explosion.BlockInteraction p_363174_
    ) {
        this.level = p_361323_;
        this.source = p_365060_;
        this.radius = p_365227_;
        this.center = p_365404_;
        this.fire = p_365067_;
        this.blockInteraction = p_363174_;
        this.damageSource = p_360713_ == null ? p_361323_.damageSources().explosion(this) : p_360713_;
        this.damageCalculator = p_360818_ == null ? this.makeDamageCalculator(p_365060_) : p_360818_;
    }

    private ExplosionDamageCalculator makeDamageCalculator(@Nullable Entity p_364459_) {
        return (ExplosionDamageCalculator)(p_364459_ == null ? EXPLOSION_DAMAGE_CALCULATOR : new EntityBasedExplosionDamageCalculator(p_364459_));
    }

    public static float getSeenPercent(Vec3 p_363930_, Entity p_363796_) {
        AABB aabb = p_363796_.getBoundingBox();
        double d0 = 1.0 / ((aabb.maxX - aabb.minX) * 2.0 + 1.0);
        double d1 = 1.0 / ((aabb.maxY - aabb.minY) * 2.0 + 1.0);
        double d2 = 1.0 / ((aabb.maxZ - aabb.minZ) * 2.0 + 1.0);
        double d3 = (1.0 - Math.floor(1.0 / d0) * d0) / 2.0;
        double d4 = (1.0 - Math.floor(1.0 / d2) * d2) / 2.0;
        if (!(d0 < 0.0) && !(d1 < 0.0) && !(d2 < 0.0)) {
            int i = 0;
            int j = 0;

            for (double d5 = 0.0; d5 <= 1.0; d5 += d0) {
                for (double d6 = 0.0; d6 <= 1.0; d6 += d1) {
                    for (double d7 = 0.0; d7 <= 1.0; d7 += d2) {
                        double d8 = Mth.lerp(d5, aabb.minX, aabb.maxX);
                        double d9 = Mth.lerp(d6, aabb.minY, aabb.maxY);
                        double d10 = Mth.lerp(d7, aabb.minZ, aabb.maxZ);
                        Vec3 vec3 = new Vec3(d8 + d3, d9, d10 + d4);
                        if (p_363796_.level().clip(new ClipContext(vec3, p_363930_, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, p_363796_)).getType()
                            == HitResult.Type.MISS) {
                            i++;
                        }

                        j++;
                    }
                }
            }

            return (float)i / (float)j;
        } else {
            return 0.0F;
        }
    }

    @Override
    public float radius() {
        return this.radius;
    }

    @Override
    public Vec3 center() {
        return this.center;
    }

    private List<BlockPos> calculateExplodedPositions() {
        Set<BlockPos> set = new HashSet<>();
        int i = 16;

        for (int j = 0; j < 16; j++) {
            for (int k = 0; k < 16; k++) {
                for (int l = 0; l < 16; l++) {
                    if (j == 0 || j == 15 || k == 0 || k == 15 || l == 0 || l == 15) {
                        double d0 = (double)((float)j / 15.0F * 2.0F - 1.0F);
                        double d1 = (double)((float)k / 15.0F * 2.0F - 1.0F);
                        double d2 = (double)((float)l / 15.0F * 2.0F - 1.0F);
                        double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                        d0 /= d3;
                        d1 /= d3;
                        d2 /= d3;
                        float f = this.radius * (0.7F + this.level.random.nextFloat() * 0.6F);
                        double d4 = this.center.x;
                        double d5 = this.center.y;
                        double d6 = this.center.z;

                        for (float f1 = 0.3F; f > 0.0F; f -= 0.22500001F) {
                            BlockPos blockpos = BlockPos.containing(d4, d5, d6);
                            BlockState blockstate = this.level.getBlockState(blockpos);
                            FluidState fluidstate = this.level.getFluidState(blockpos);
                            if (!this.level.isInWorldBounds(blockpos)) {
                                break;
                            }

                            Optional<Float> optional = this.damageCalculator.getBlockExplosionResistance(this, this.level, blockpos, blockstate, fluidstate);
                            if (optional.isPresent()) {
                                f -= (optional.get() + 0.3F) * 0.3F;
                            }

                            if (f > 0.0F && this.damageCalculator.shouldBlockExplode(this, this.level, blockpos, blockstate, f)) {
                                set.add(blockpos);
                            }

                            d4 += d0 * 0.3F;
                            d5 += d1 * 0.3F;
                            d6 += d2 * 0.3F;
                        }
                    }
                }
            }
        }

        return new ObjectArrayList<>(set);
    }

    @Deprecated
    private void hurtEntities() {
        this.hurtEntities(List.of());
    }

    private void hurtEntities(List<BlockPos> blocks) {
        float f = this.radius * 2.0F;
        int i = Mth.floor(this.center.x - (double)f - 1.0);
        int j = Mth.floor(this.center.x + (double)f + 1.0);
        int k = Mth.floor(this.center.y - (double)f - 1.0);
        int l = Mth.floor(this.center.y + (double)f + 1.0);
        int i1 = Mth.floor(this.center.z - (double)f - 1.0);
        int j1 = Mth.floor(this.center.z + (double)f + 1.0);

        List<Entity> list = this.level.getEntities(this.source, new AABB((double)i, (double)k, (double)i1, (double)j, (double)l, (double)j1));
        net.neoforged.neoforge.event.EventHooks.onExplosionDetonate(this.level, this, list, blocks);
        for (Entity entity : list) {
            if (!entity.ignoreExplosion(this)) {
                double d0 = Math.sqrt(entity.distanceToSqr(this.center)) / (double)f;
                if (d0 <= 1.0) {
                    double d1 = entity.getX() - this.center.x;
                    double d2 = (entity instanceof PrimedTnt ? entity.getY() : entity.getEyeY()) - this.center.y;
                    double d3 = entity.getZ() - this.center.z;
                    double d4 = Math.sqrt(d1 * d1 + d2 * d2 + d3 * d3);
                    if (d4 != 0.0) {
                        d1 /= d4;
                        d2 /= d4;
                        d3 /= d4;
                        boolean flag = this.damageCalculator.shouldDamageEntity(this, entity);
                        float f1 = this.damageCalculator.getKnockbackMultiplier(entity);
                        float f2 = !flag && f1 == 0.0F ? 0.0F : getSeenPercent(this.center, entity);
                        if (flag) {
                            entity.hurtServer(this.level, this.damageSource, this.damageCalculator.getEntityDamageAmount(this, entity, f2));
                        }

                        double d5 = (1.0 - d0) * (double)f2 * (double)f1;
                        double d6;
                        if (entity instanceof LivingEntity livingentity) {
                            d6 = d5 * (1.0 - livingentity.getAttributeValue(Attributes.EXPLOSION_KNOCKBACK_RESISTANCE));
                        } else {
                            d6 = d5;
                        }

                        d1 *= d6;
                        d2 *= d6;
                        d3 *= d6;
                        Vec3 vec3 = new Vec3(d1, d2, d3);
                        vec3 = net.neoforged.neoforge.event.EventHooks.getExplosionKnockback(this.level, this, entity, vec3, blocks);
                        entity.push(vec3);
                        if (entity instanceof Player) {
                            Player player = (Player)entity;
                            if (!player.isSpectator() && (!player.isCreative() || !player.getAbilities().flying)) {
                                this.hitPlayers.put(player, vec3);
                            }
                        }

                        entity.onExplosionHit(this.source);
                    }
                }
            }
        }
    }

    private void interactWithBlocks(List<BlockPos> p_362013_) {
        List<ServerExplosion.StackCollector> list = new ArrayList<>();
        Util.shuffle(p_362013_, this.level.random);

        for (BlockPos blockpos : p_362013_) {
            this.level
                .getBlockState(blockpos)
                .onExplosionHit(this.level, blockpos, this, (p_365090_, p_360493_) -> addOrAppendStack(list, p_365090_, p_360493_));
        }

        for (ServerExplosion.StackCollector serverexplosion$stackcollector : list) {
            Block.popResource(this.level, serverexplosion$stackcollector.pos, serverexplosion$stackcollector.stack);
        }
    }

    private void createFire(List<BlockPos> p_362665_) {
        for (BlockPos blockpos : p_362665_) {
            if (this.level.random.nextInt(3) == 0 && this.level.getBlockState(blockpos).isAir() && this.level.getBlockState(blockpos.below()).isSolidRender()) {
                this.level.setBlockAndUpdate(blockpos, BaseFireBlock.getState(this.level, blockpos));
            }
        }
    }

    public void explode() {
        this.level.gameEvent(this.source, GameEvent.EXPLODE, this.center);
        List<BlockPos> list = this.calculateExplodedPositions();
        this.hurtEntities(list);
        if (this.interactsWithBlocks()) {
            ProfilerFiller profilerfiller = Profiler.get();
            profilerfiller.push("explosion_blocks");
            this.interactWithBlocks(list);
            profilerfiller.pop();
        }

        if (this.fire) {
            this.createFire(list);
        }
    }

    private static void addOrAppendStack(List<ServerExplosion.StackCollector> p_363272_, ItemStack p_362153_, BlockPos p_361248_) {
        for (ServerExplosion.StackCollector serverexplosion$stackcollector : p_363272_) {
            serverexplosion$stackcollector.tryMerge(p_362153_);
            if (p_362153_.isEmpty()) {
                return;
            }
        }

        p_363272_.add(new ServerExplosion.StackCollector(p_361248_, p_362153_));
    }

    private boolean interactsWithBlocks() {
        return this.blockInteraction != Explosion.BlockInteraction.KEEP;
    }

    public Map<Player, Vec3> getHitPlayers() {
        return this.hitPlayers;
    }

    @Override
    public ServerLevel level() {
        return this.level;
    }

    @Nullable
    @Override
    public LivingEntity getIndirectSourceEntity() {
        return Explosion.getIndirectSourceEntity(this.source);
    }

    @Nullable
    @Override
    public Entity getDirectSourceEntity() {
        return this.source;
    }

    public DamageSource getDamageSource() {
        return this.damageSource;
    }

    @Override
    public Explosion.BlockInteraction getBlockInteraction() {
        return this.blockInteraction;
    }

    @Override
    public boolean canTriggerBlocks() {
        if (this.blockInteraction != Explosion.BlockInteraction.TRIGGER_BLOCK) {
            return false;
        } else {
            return this.source != null && this.source.getType() == EntityType.BREEZE_WIND_CHARGE
                ? this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)
                : true;
        }
    }

    @Override
    public boolean shouldAffectBlocklikeEntities() {
        boolean flag = this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING);
        boolean flag1 = this.source == null || !this.source.isInWater();
        boolean flag2 = this.source == null || this.source.getType() != EntityType.BREEZE_WIND_CHARGE && this.source.getType() != EntityType.WIND_CHARGE;
        return flag ? flag1 && flag2 : this.blockInteraction.shouldAffectBlocklikeEntities() && flag1 && flag2;
    }

    public boolean isSmall() {
        return this.radius < 2.0F || !this.interactsWithBlocks();
    }

    static class StackCollector {
        final BlockPos pos;
        ItemStack stack;

        StackCollector(BlockPos p_361929_, ItemStack p_365092_) {
            this.pos = p_361929_;
            this.stack = p_365092_;
        }

        public void tryMerge(ItemStack p_364896_) {
            if (ItemEntity.areMergable(this.stack, p_364896_)) {
                this.stack = ItemEntity.merge(this.stack, p_364896_, 16);
            }
        }
    }
}
