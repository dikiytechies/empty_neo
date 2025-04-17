package net.minecraft.world.level.block.entity;

import com.mojang.datafixers.util.Either;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.TrailParticleOption;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.SpawnUtil;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.creaking.Creaking;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CreakingHeartBlock;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;

public class CreakingHeartBlockEntity extends BlockEntity {
    private static final int PLAYER_DETECTION_RANGE = 32;
    public static final int CREAKING_ROAMING_RADIUS = 32;
    private static final int DISTANCE_CREAKING_TOO_FAR = 34;
    private static final int SPAWN_RANGE_XZ = 16;
    private static final int SPAWN_RANGE_Y = 8;
    private static final int ATTEMPTS_PER_SPAWN = 5;
    private static final int UPDATE_TICKS = 20;
    private static final int UPDATE_TICKS_VARIANCE = 5;
    private static final int HURT_CALL_TOTAL_TICKS = 100;
    private static final int NUMBER_OF_HURT_CALLS = 10;
    private static final int HURT_CALL_INTERVAL = 10;
    private static final int HURT_CALL_PARTICLE_TICKS = 50;
    private static final int MAX_DEPTH = 2;
    private static final int MAX_COUNT = 64;
    private static final int TICKS_GRACE_PERIOD = 30;
    private static final Optional<Creaking> NO_CREAKING = Optional.empty();
    @Nullable
    private Either<Creaking, UUID> creakingInfo;
    private long ticksExisted;
    private int ticker;
    private int emitter;
    @Nullable
    private Vec3 emitterTarget;
    private int outputSignal;

    public CreakingHeartBlockEntity(BlockPos p_380287_, BlockState p_379335_) {
        super(BlockEntityType.CREAKING_HEART, p_380287_, p_379335_);
    }

    public static void serverTick(Level p_379321_, BlockPos p_379679_, BlockState p_379831_, CreakingHeartBlockEntity p_379534_) {
        p_379534_.ticksExisted++;
        if (p_379321_ instanceof ServerLevel serverlevel) {
            int $$6 = p_379534_.computeAnalogOutputSignal();
            if (p_379534_.outputSignal != $$6) {
                p_379534_.outputSignal = $$6;
                p_379321_.updateNeighbourForOutputSignal(p_379679_, Blocks.CREAKING_HEART);
            }

            if (p_379534_.emitter > 0) {
                if (p_379534_.emitter > 50) {
                    p_379534_.emitParticles(serverlevel, 1, true);
                    p_379534_.emitParticles(serverlevel, 1, false);
                }

                if (p_379534_.emitter % 10 == 0 && p_379534_.emitterTarget != null) {
                    p_379534_.getCreakingProtector().ifPresent(p_389387_ -> p_379534_.emitterTarget = p_389387_.getBoundingBox().getCenter());
                    Vec3 vec3 = Vec3.atCenterOf(p_379679_);
                    float f = 0.2F + 0.8F * (float)(100 - p_379534_.emitter) / 100.0F;
                    Vec3 vec31 = vec3.subtract(p_379534_.emitterTarget).scale((double)f).add(p_379534_.emitterTarget);
                    BlockPos blockpos = BlockPos.containing(vec31);
                    float f1 = (float)p_379534_.emitter / 2.0F / 100.0F + 0.5F;
                    serverlevel.playSound(null, blockpos, SoundEvents.CREAKING_HEART_HURT, SoundSource.BLOCKS, f1, 1.0F);
                }

                p_379534_.emitter--;
            }

            if (p_379534_.ticker-- < 0) {
                p_379534_.ticker = p_379534_.level == null ? 20 : p_379534_.level.random.nextInt(5) + 20;
                if (p_379534_.creakingInfo == null) {
                    if (!CreakingHeartBlock.hasRequiredLogs(p_379831_, p_379321_, p_379679_)) {
                        p_379321_.setBlock(p_379679_, p_379831_.setValue(CreakingHeartBlock.ACTIVE, Boolean.valueOf(false)), 3);
                    } else if (p_379831_.getValue(CreakingHeartBlock.ACTIVE)) {
                        if (CreakingHeartBlock.isNaturalNight(p_379321_)) {
                            if (p_379321_.getDifficulty() != Difficulty.PEACEFUL) {
                                if (serverlevel.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)) {
                                    Player player = p_379321_.getNearestPlayer(
                                        (double)p_379679_.getX(), (double)p_379679_.getY(), (double)p_379679_.getZ(), 32.0, false
                                    );
                                    if (player != null) {
                                        Creaking creaking1 = spawnProtector(serverlevel, p_379534_);
                                        if (creaking1 != null) {
                                            p_379534_.setCreakingInfo(creaking1);
                                            creaking1.makeSound(SoundEvents.CREAKING_SPAWN);
                                            p_379321_.playSound(null, p_379534_.getBlockPos(), SoundEvents.CREAKING_HEART_SPAWN, SoundSource.BLOCKS, 1.0F, 1.0F);
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Optional<Creaking> optional = p_379534_.getCreakingProtector();
                    if (optional.isPresent()) {
                        Creaking creaking = optional.get();
                        if (!CreakingHeartBlock.isNaturalNight(p_379321_) || p_379534_.distanceToCreaking() > 34.0 || creaking.playerIsStuckInYou()) {
                            p_379534_.removeProtector(null);
                            return;
                        }

                        if (!CreakingHeartBlock.hasRequiredLogs(p_379831_, p_379321_, p_379679_) && p_379534_.creakingInfo == null) {
                            p_379321_.setBlock(p_379679_, p_379831_.setValue(CreakingHeartBlock.ACTIVE, Boolean.valueOf(false)), 3);
                        }
                    }
                }
            }
        }
    }

    private double distanceToCreaking() {
        return this.getCreakingProtector().map(p_390335_ -> Math.sqrt(p_390335_.distanceToSqr(Vec3.atBottomCenterOf(this.getBlockPos())))).orElse(0.0);
    }

    private void clearCreakingInfo() {
        this.creakingInfo = null;
        this.setChanged();
    }

    public void setCreakingInfo(Creaking p_390482_) {
        this.creakingInfo = Either.left(p_390482_);
        this.setChanged();
    }

    public void setCreakingInfo(UUID p_390523_) {
        this.creakingInfo = Either.right(p_390523_);
        this.ticksExisted = 0L;
        this.setChanged();
    }

    private Optional<Creaking> getCreakingProtector() {
        if (this.creakingInfo == null) {
            return NO_CREAKING;
        } else {
            if (this.creakingInfo.left().isPresent()) {
                Creaking creaking = this.creakingInfo.left().get();
                if (!creaking.isRemoved()) {
                    return Optional.of(creaking);
                }

                this.setCreakingInfo(creaking.getUUID());
            }

            if (this.level instanceof ServerLevel serverlevel && this.creakingInfo.right().isPresent()) {
                UUID uuid = this.creakingInfo.right().get();
                if (serverlevel.getEntity(uuid) instanceof Creaking creaking1) {
                    this.setCreakingInfo(creaking1);
                    return Optional.of(creaking1);
                }

                if (this.ticksExisted >= 30L) {
                    this.clearCreakingInfo();
                }

                return NO_CREAKING;
            }

            return NO_CREAKING;
        }
    }

    @Nullable
    private static Creaking spawnProtector(ServerLevel p_379873_, CreakingHeartBlockEntity p_379806_) {
        BlockPos blockpos = p_379806_.getBlockPos();
        Optional<Creaking> optional = SpawnUtil.trySpawnMob(
            EntityType.CREAKING, EntitySpawnReason.SPAWNER, p_379873_, blockpos, 5, 16, 8, SpawnUtil.Strategy.ON_TOP_OF_COLLIDER_NO_LEAVES, true
        );
        if (optional.isEmpty()) {
            return null;
        } else {
            Creaking creaking = optional.get();
            p_379873_.gameEvent(creaking, GameEvent.ENTITY_PLACE, creaking.position());
            p_379873_.broadcastEntityEvent(creaking, (byte)60);
            creaking.setTransient(blockpos);
            return creaking;
        }
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider p_379306_) {
        return this.saveCustomOnly(p_379306_);
    }

    public void creakingHurt() {
        if (this.getCreakingProtector().orElse(null) instanceof Creaking creaking) {
            if (this.level instanceof ServerLevel serverlevel) {
                if (this.emitter <= 0) {
                    this.emitParticles(serverlevel, 20, false);
                    int j = this.level.getRandom().nextIntBetweenInclusive(2, 3);

                    for (int i = 0; i < j; i++) {
                        this.spreadResin().ifPresent(p_386422_ -> {
                            this.level.playSound(null, p_386422_, SoundEvents.RESIN_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
                            this.level.gameEvent(GameEvent.BLOCK_PLACE, p_386422_, GameEvent.Context.of(this.level.getBlockState(p_386422_)));
                        });
                    }

                    this.emitter = 100;
                    this.emitterTarget = creaking.getBoundingBox().getCenter();
                }
            }
        }
    }

    private Optional<BlockPos> spreadResin() {
        Mutable<BlockPos> mutable = new MutableObject<>(null);
        BlockPos.breadthFirstTraversal(this.worldPosition, 2, 64, (p_389388_, p_389389_) -> {
            for (Direction direction : Util.shuffledCopy(Direction.values(), this.level.random)) {
                BlockPos blockpos = p_389388_.relative(direction);
                if (this.level.getBlockState(blockpos).is(BlockTags.PALE_OAK_LOGS)) {
                    p_389389_.accept(blockpos);
                }
            }
        }, p_389384_ -> {
            if (!this.level.getBlockState(p_389384_).is(BlockTags.PALE_OAK_LOGS)) {
                return BlockPos.TraversalNodeStatus.ACCEPT;
            } else {
                for (Direction direction : Util.shuffledCopy(Direction.values(), this.level.random)) {
                    BlockPos blockpos = p_389384_.relative(direction);
                    BlockState blockstate = this.level.getBlockState(blockpos);
                    Direction direction1 = direction.getOpposite();
                    if (blockstate.isAir()) {
                        blockstate = Blocks.RESIN_CLUMP.defaultBlockState();
                    } else if (blockstate.is(Blocks.WATER) && blockstate.getFluidState().isSource()) {
                        blockstate = Blocks.RESIN_CLUMP.defaultBlockState().setValue(MultifaceBlock.WATERLOGGED, Boolean.valueOf(true));
                    }

                    if (blockstate.is(Blocks.RESIN_CLUMP) && !MultifaceBlock.hasFace(blockstate, direction1)) {
                        this.level.setBlock(blockpos, blockstate.setValue(MultifaceBlock.getFaceProperty(direction1), Boolean.valueOf(true)), 3);
                        mutable.setValue(blockpos);
                        return BlockPos.TraversalNodeStatus.STOP;
                    }
                }

                return BlockPos.TraversalNodeStatus.ACCEPT;
            }
        });
        return Optional.ofNullable(mutable.getValue());
    }

    private void emitParticles(ServerLevel p_379473_, int p_380023_, boolean p_379802_) {
        if (this.getCreakingProtector().orElse(null) instanceof Creaking creaking) {
            int i = p_379802_ ? 16545810 : 6250335;
            RandomSource randomsource = p_379473_.random;

            for (double d0 = 0.0; d0 < (double)p_380023_; d0++) {
                AABB aabb = creaking.getBoundingBox();
                Vec3 vec3 = aabb.getMinPosition()
                    .add(randomsource.nextDouble() * aabb.getXsize(), randomsource.nextDouble() * aabb.getYsize(), randomsource.nextDouble() * aabb.getZsize());
                Vec3 vec31 = Vec3.atLowerCornerOf(this.getBlockPos()).add(randomsource.nextDouble(), randomsource.nextDouble(), randomsource.nextDouble());
                if (p_379802_) {
                    Vec3 vec32 = vec3;
                    vec3 = vec31;
                    vec31 = vec32;
                }

                TrailParticleOption trailparticleoption = new TrailParticleOption(vec31, i, randomsource.nextInt(40) + 10);
                p_379473_.sendParticles(trailparticleoption, true, true, vec3.x, vec3.y, vec3.z, 1, 0.0, 0.0, 0.0, 0.0);
            }
        }
    }

    public void removeProtector(@Nullable DamageSource p_379965_) {
        if (this.getCreakingProtector().orElse(null) instanceof Creaking creaking) {
            if (p_379965_ == null) {
                creaking.tearDown();
            } else {
                creaking.creakingDeathEffects(p_379965_);
                creaking.setTearingDown();
                creaking.setHealth(0.0F);
            }

            this.clearCreakingInfo();
        }
    }

    public boolean isProtector(Creaking p_380040_) {
        return this.getCreakingProtector().map(p_389391_ -> p_389391_ == p_380040_).orElse(false);
    }

    public int getAnalogOutputSignal() {
        return this.outputSignal;
    }

    public int computeAnalogOutputSignal() {
        if (this.creakingInfo != null && !this.getCreakingProtector().isEmpty()) {
            double d0 = this.distanceToCreaking();
            double d1 = Math.clamp(d0, 0.0, 32.0) / 32.0;
            return 15 - (int)Math.floor(d1 * 15.0);
        } else {
            return 0;
        }
    }

    @Override
    protected void loadAdditional(CompoundTag p_389460_, HolderLookup.Provider p_389519_) {
        super.loadAdditional(p_389460_, p_389519_);
        if (p_389460_.contains("creaking")) {
            this.setCreakingInfo(p_389460_.getUUID("creaking"));
        } else {
            this.clearCreakingInfo();
        }
    }

    @Override
    protected void saveAdditional(CompoundTag p_389474_, HolderLookup.Provider p_389560_) {
        super.saveAdditional(p_389474_, p_389560_);
        if (this.creakingInfo != null) {
            p_389474_.putUUID("creaking", this.creakingInfo.map(Entity::getUUID, p_389392_ -> (UUID)p_389392_));
        }
    }
}
