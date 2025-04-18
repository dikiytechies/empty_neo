package net.minecraft.server.level;

import javax.annotation.Nullable;
import net.minecraft.world.level.chunk.status.ChunkPyramid;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.chunk.status.ChunkStep;
import org.jetbrains.annotations.Contract;

public class ChunkLevel {
    private static final int FULL_CHUNK_LEVEL = 33;
    private static final int BLOCK_TICKING_LEVEL = 32;
    private static final int ENTITY_TICKING_LEVEL = 31;
    private static final ChunkStep FULL_CHUNK_STEP = ChunkPyramid.GENERATION_PYRAMID.getStepTo(ChunkStatus.FULL);
    public static final int RADIUS_AROUND_FULL_CHUNK = FULL_CHUNK_STEP.accumulatedDependencies().getRadius();
    public static final int MAX_LEVEL = 33 + RADIUS_AROUND_FULL_CHUNK;

    @Nullable
    public static ChunkStatus generationStatus(int p_287738_) {
        return getStatusAroundFullChunk(p_287738_ - 33, null);
    }

    @Nullable
    @Contract("_,!null->!null;_,_->_")
    public static ChunkStatus getStatusAroundFullChunk(int p_347593_, @Nullable ChunkStatus p_347451_) {
        if (p_347593_ > RADIUS_AROUND_FULL_CHUNK) {
            return p_347451_;
        } else {
            return p_347593_ <= 0 ? ChunkStatus.FULL : FULL_CHUNK_STEP.accumulatedDependencies().get(p_347593_);
        }
    }

    public static ChunkStatus getStatusAroundFullChunk(int p_347662_) {
        return getStatusAroundFullChunk(p_347662_, ChunkStatus.EMPTY);
    }

    public static int byStatus(ChunkStatus p_331231_) {
        return 33 + FULL_CHUNK_STEP.getAccumulatedRadiusOf(p_331231_);
    }

    public static FullChunkStatus fullStatus(int p_287750_) {
        if (p_287750_ <= 31) {
            return FullChunkStatus.ENTITY_TICKING;
        } else if (p_287750_ <= 32) {
            return FullChunkStatus.BLOCK_TICKING;
        } else {
            return p_287750_ <= 33 ? FullChunkStatus.FULL : FullChunkStatus.INACCESSIBLE;
        }
    }

    public static int byStatus(FullChunkStatus p_287601_) {
        return switch (p_287601_) {
            case INACCESSIBLE -> MAX_LEVEL;
            case FULL -> 33;
            case BLOCK_TICKING -> 32;
            case ENTITY_TICKING -> 31;
        };
    }

    public static boolean isEntityTicking(int p_287767_) {
        return p_287767_ <= 31;
    }

    public static boolean isBlockTicking(int p_287696_) {
        return p_287696_ <= 32;
    }

    public static boolean isLoaded(int p_287635_) {
        return p_287635_ <= MAX_LEVEL;
    }
}
