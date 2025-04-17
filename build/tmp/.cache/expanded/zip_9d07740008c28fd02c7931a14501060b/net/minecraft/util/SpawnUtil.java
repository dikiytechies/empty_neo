package net.minecraft.util;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.StainedGlassBlock;
import net.minecraft.world.level.block.StainedGlassPaneBlock;
import net.minecraft.world.level.block.state.BlockState;

public class SpawnUtil {
    public static <T extends Mob> Optional<T> trySpawnMob(
        EntityType<T> p_216404_,
        EntitySpawnReason p_364255_,
        ServerLevel p_216406_,
        BlockPos p_216407_,
        int p_216408_,
        int p_216409_,
        int p_216410_,
        SpawnUtil.Strategy p_216411_,
        boolean p_382971_
    ) {
        BlockPos.MutableBlockPos blockpos$mutableblockpos = p_216407_.mutable();

        for (int i = 0; i < p_216408_; i++) {
            int j = Mth.randomBetweenInclusive(p_216406_.random, -p_216409_, p_216409_);
            int k = Mth.randomBetweenInclusive(p_216406_.random, -p_216409_, p_216409_);
            blockpos$mutableblockpos.setWithOffset(p_216407_, j, p_216410_, k);
            if (p_216406_.getWorldBorder().isWithinBounds(blockpos$mutableblockpos)
                && moveToPossibleSpawnPosition(p_216406_, p_216410_, blockpos$mutableblockpos, p_216411_)
                && (
                    !p_382971_
                        || p_216406_.noCollision(
                            p_216404_.getSpawnAABB(
                                (double)blockpos$mutableblockpos.getX() + 0.5,
                                (double)blockpos$mutableblockpos.getY(),
                                (double)blockpos$mutableblockpos.getZ() + 0.5
                            )
                        )
                )) {
                T t = (T)p_216404_.create(p_216406_, null, blockpos$mutableblockpos, p_364255_, false, false);
                if (t != null) {
                    if (net.neoforged.neoforge.event.EventHooks.checkSpawnPosition(t, p_216406_, p_364255_)) {
                        p_216406_.addFreshEntityWithPassengers(t);
                        t.playAmbientSound();
                        return Optional.of(t);
                    }

                    t.discard();
                }
            }
        }

        return Optional.empty();
    }

    private static boolean moveToPossibleSpawnPosition(ServerLevel p_216399_, int p_216400_, BlockPos.MutableBlockPos p_216401_, SpawnUtil.Strategy p_216402_) {
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos().set(p_216401_);
        BlockState blockstate = p_216399_.getBlockState(blockpos$mutableblockpos);

        for (int i = p_216400_; i >= -p_216400_; i--) {
            p_216401_.move(Direction.DOWN);
            blockpos$mutableblockpos.setWithOffset(p_216401_, Direction.UP);
            BlockState blockstate1 = p_216399_.getBlockState(p_216401_);
            if (p_216402_.canSpawnOn(p_216399_, p_216401_, blockstate1, blockpos$mutableblockpos, blockstate)) {
                p_216401_.move(Direction.UP);
                return true;
            }

            blockstate = blockstate1;
        }

        return false;
    }

    public interface Strategy {
        @Deprecated
        SpawnUtil.Strategy LEGACY_IRON_GOLEM = (p_289751_, p_289752_, p_289753_, p_289754_, p_289755_) -> !p_289753_.is(Blocks.COBWEB)
                    && !p_289753_.is(Blocks.CACTUS)
                    && !p_289753_.is(Blocks.GLASS_PANE)
                    && !(p_289753_.getBlock() instanceof StainedGlassPaneBlock)
                    && !(p_289753_.getBlock() instanceof StainedGlassBlock)
                    && !(p_289753_.getBlock() instanceof LeavesBlock)
                    && !p_289753_.is(Blocks.CONDUIT)
                    && !p_289753_.is(Blocks.ICE)
                    && !p_289753_.is(Blocks.TNT)
                    && !p_289753_.is(Blocks.GLOWSTONE)
                    && !p_289753_.is(Blocks.BEACON)
                    && !p_289753_.is(Blocks.SEA_LANTERN)
                    && !p_289753_.is(Blocks.FROSTED_ICE)
                    && !p_289753_.is(Blocks.TINTED_GLASS)
                    && !p_289753_.is(Blocks.GLASS)
                ? (p_289755_.isAir() || p_289755_.liquid()) && (p_289753_.isSolid() || p_289753_.is(Blocks.POWDER_SNOW))
                : false;
        SpawnUtil.Strategy ON_TOP_OF_COLLIDER = (p_359662_, p_359663_, p_359664_, p_359665_, p_359666_) -> p_359666_.getCollisionShape(p_359662_, p_359665_)
                    .isEmpty()
                && Block.isFaceFull(p_359664_.getCollisionShape(p_359662_, p_359663_), Direction.UP);
        SpawnUtil.Strategy ON_TOP_OF_COLLIDER_NO_LEAVES = (p_379064_, p_379065_, p_379066_, p_379067_, p_379068_) -> p_379068_.getCollisionShape(
                        p_379064_, p_379067_
                    )
                    .isEmpty()
                && !p_379066_.is(BlockTags.LEAVES)
                && Block.isFaceFull(p_379066_.getCollisionShape(p_379064_, p_379065_), Direction.UP);

        boolean canSpawnOn(ServerLevel p_216428_, BlockPos p_216429_, BlockState p_216430_, BlockPos p_216431_, BlockState p_216432_);
    }
}
