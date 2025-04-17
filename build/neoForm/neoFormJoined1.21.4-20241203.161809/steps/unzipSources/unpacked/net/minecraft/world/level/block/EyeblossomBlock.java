package net.minecraft.world.level.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.TrailParticleOption;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public class EyeblossomBlock extends FlowerBlock {
    public static final MapCodec<EyeblossomBlock> CODEC = RecordCodecBuilder.mapCodec(
        p_383166_ -> p_383166_.group(Codec.BOOL.fieldOf("open").forGetter(p_383069_ -> p_383069_.type.open), propertiesCodec())
                .apply(p_383166_, EyeblossomBlock::new)
    );
    private static final int EYEBLOSSOM_XZ_RANGE = 3;
    private static final int EYEBLOSSOM_Y_RANGE = 2;
    private final EyeblossomBlock.Type type;

    @Override
    public MapCodec<? extends EyeblossomBlock> codec() {
        return CODEC;
    }

    public EyeblossomBlock(EyeblossomBlock.Type p_382834_, BlockBehaviour.Properties p_383158_) {
        super(p_382834_.effect, p_382834_.effectDuration, p_383158_);
        this.type = p_382834_;
    }

    public EyeblossomBlock(boolean p_383090_, BlockBehaviour.Properties p_382887_) {
        super(EyeblossomBlock.Type.fromBoolean(p_383090_).effect, EyeblossomBlock.Type.fromBoolean(p_383090_).effectDuration, p_382887_);
        this.type = EyeblossomBlock.Type.fromBoolean(p_383090_);
    }

    @Override
    public void animateTick(BlockState p_382850_, Level p_382933_, BlockPos p_382838_, RandomSource p_383190_) {
        if (this.type.emitSounds() && p_383190_.nextInt(700) == 0) {
            BlockState blockstate = p_382933_.getBlockState(p_382838_.below());
            if (blockstate.is(Blocks.PALE_MOSS_BLOCK)) {
                p_382933_.playLocalSound(
                    (double)p_382838_.getX(),
                    (double)p_382838_.getY(),
                    (double)p_382838_.getZ(),
                    SoundEvents.EYEBLOSSOM_IDLE,
                    SoundSource.BLOCKS,
                    1.0F,
                    1.0F,
                    false
                );
            }
        }
    }

    @Override
    protected void randomTick(BlockState p_382824_, ServerLevel p_382831_, BlockPos p_382957_, RandomSource p_382888_) {
        if (this.tryChangingState(p_382824_, p_382831_, p_382957_, p_382888_)) {
            p_382831_.playSound(null, p_382957_, this.type.transform().longSwitchSound, SoundSource.BLOCKS, 1.0F, 1.0F);
        }

        super.randomTick(p_382824_, p_382831_, p_382957_, p_382888_);
    }

    @Override
    protected void tick(BlockState p_382808_, ServerLevel p_383005_, BlockPos p_383211_, RandomSource p_383088_) {
        if (this.tryChangingState(p_382808_, p_383005_, p_383211_, p_383088_)) {
            p_383005_.playSound(null, p_383211_, this.type.transform().shortSwitchSound, SoundSource.BLOCKS, 1.0F, 1.0F);
        }

        super.tick(p_382808_, p_383005_, p_383211_, p_383088_);
    }

    private boolean tryChangingState(BlockState p_383235_, ServerLevel p_383091_, BlockPos p_383073_, RandomSource p_383038_) {
        if (!p_383091_.dimensionType().natural()) {
            return false;
        } else if (p_383091_.isDay() != this.type.open) {
            return false;
        } else {
            EyeblossomBlock.Type eyeblossomblock$type = this.type.transform();
            p_383091_.setBlock(p_383073_, eyeblossomblock$type.state(), 3);
            p_383091_.gameEvent(GameEvent.BLOCK_CHANGE, p_383073_, GameEvent.Context.of(p_383235_));
            eyeblossomblock$type.spawnTransformParticle(p_383091_, p_383073_, p_383038_);
            BlockPos.betweenClosed(p_383073_.offset(-3, -2, -3), p_383073_.offset(3, 2, 3)).forEach(p_383198_ -> {
                BlockState blockstate = p_383091_.getBlockState(p_383198_);
                if (blockstate == p_383235_) {
                    double d0 = Math.sqrt(p_383073_.distSqr(p_383198_));
                    int i = p_383038_.nextIntBetweenInclusive((int)(d0 * 5.0), (int)(d0 * 10.0));
                    p_383091_.scheduleTick(p_383198_, p_383235_.getBlock(), i);
                }
            });
            return true;
        }
    }

    @Override
    protected void entityInside(BlockState p_382817_, Level p_383060_, BlockPos p_383146_, Entity p_383054_) {
        if (!p_383060_.isClientSide()
            && p_383060_.getDifficulty() != Difficulty.PEACEFUL
            && p_383054_ instanceof Bee bee
            && Bee.attractsBees(p_382817_)
            && !bee.hasEffect(MobEffects.POISON)) {
            bee.addEffect(this.getBeeInteractionEffect());
        }
    }

    @Override
    public MobEffectInstance getBeeInteractionEffect() {
        return new MobEffectInstance(MobEffects.POISON, 25);
    }

    public static enum Type {
        OPEN(true, MobEffects.BLINDNESS, 11.0F, SoundEvents.EYEBLOSSOM_OPEN_LONG, SoundEvents.EYEBLOSSOM_OPEN, 16545810),
        CLOSED(false, MobEffects.CONFUSION, 7.0F, SoundEvents.EYEBLOSSOM_CLOSE_LONG, SoundEvents.EYEBLOSSOM_CLOSE, 6250335);

        final boolean open;
        final Holder<MobEffect> effect;
        final float effectDuration;
        final SoundEvent longSwitchSound;
        final SoundEvent shortSwitchSound;
        private final int particleColor;

        private Type(boolean p_383223_, Holder<MobEffect> p_383070_, float p_382883_, SoundEvent p_383157_, SoundEvent p_383205_, int p_383152_) {
            this.open = p_383223_;
            this.effect = p_383070_;
            this.effectDuration = p_382883_;
            this.longSwitchSound = p_383157_;
            this.shortSwitchSound = p_383205_;
            this.particleColor = p_383152_;
        }

        public Block block() {
            return this.open ? Blocks.OPEN_EYEBLOSSOM : Blocks.CLOSED_EYEBLOSSOM;
        }

        public BlockState state() {
            return this.block().defaultBlockState();
        }

        public EyeblossomBlock.Type transform() {
            return fromBoolean(!this.open);
        }

        public boolean emitSounds() {
            return this.open;
        }

        public static EyeblossomBlock.Type fromBoolean(boolean p_382912_) {
            return p_382912_ ? OPEN : CLOSED;
        }

        public void spawnTransformParticle(ServerLevel p_383084_, BlockPos p_383042_, RandomSource p_382825_) {
            Vec3 vec3 = p_383042_.getCenter();
            double d0 = 0.5 + p_382825_.nextDouble();
            Vec3 vec31 = new Vec3(p_382825_.nextDouble() - 0.5, p_382825_.nextDouble() + 1.0, p_382825_.nextDouble() - 0.5);
            Vec3 vec32 = vec3.add(vec31.scale(d0));
            TrailParticleOption trailparticleoption = new TrailParticleOption(vec32, this.particleColor, (int)(20.0 * d0));
            p_383084_.sendParticles(trailparticleoption, vec3.x, vec3.y, vec3.z, 1, 0.0, 0.0, 0.0, 0.0);
        }

        public SoundEvent longSwitchSound() {
            return this.longSwitchSound;
        }
    }
}
