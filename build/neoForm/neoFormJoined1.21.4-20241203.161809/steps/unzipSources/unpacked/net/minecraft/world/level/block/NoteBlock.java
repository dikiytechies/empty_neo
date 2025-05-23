package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;

public class NoteBlock extends Block {
    public static final MapCodec<NoteBlock> CODEC = simpleCodec(NoteBlock::new);
    public static final EnumProperty<NoteBlockInstrument> INSTRUMENT = BlockStateProperties.NOTEBLOCK_INSTRUMENT;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final IntegerProperty NOTE = BlockStateProperties.NOTE;
    public static final int NOTE_VOLUME = 3;

    @Override
    public MapCodec<NoteBlock> codec() {
        return CODEC;
    }

    public NoteBlock(BlockBehaviour.Properties p_55016_) {
        super(p_55016_);
        this.registerDefaultState(
            this.stateDefinition
                .any()
                .setValue(INSTRUMENT, NoteBlockInstrument.HARP)
                .setValue(NOTE, Integer.valueOf(0))
                .setValue(POWERED, Boolean.valueOf(false))
        );
    }

    private BlockState setInstrument(LevelReader p_374305_, BlockPos p_261908_, BlockState p_262130_) {
        NoteBlockInstrument noteblockinstrument = p_374305_.getBlockState(p_261908_.above()).instrument();
        if (noteblockinstrument.worksAboveNoteBlock()) {
            return p_262130_.setValue(INSTRUMENT, noteblockinstrument);
        } else {
            NoteBlockInstrument noteblockinstrument1 = p_374305_.getBlockState(p_261908_.below()).instrument();
            NoteBlockInstrument noteblockinstrument2 = noteblockinstrument1.worksAboveNoteBlock() ? NoteBlockInstrument.HARP : noteblockinstrument1;
            return p_262130_.setValue(INSTRUMENT, noteblockinstrument2);
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext p_55018_) {
        return this.setInstrument(p_55018_.getLevel(), p_55018_.getClickedPos(), this.defaultBlockState());
    }

    @Override
    protected BlockState updateShape(
        BlockState p_55048_,
        LevelReader p_374298_,
        ScheduledTickAccess p_374153_,
        BlockPos p_55052_,
        Direction p_55049_,
        BlockPos p_55053_,
        BlockState p_55050_,
        RandomSource p_374540_
    ) {
        boolean flag = p_55049_.getAxis() == Direction.Axis.Y;
        return flag
            ? this.setInstrument(p_374298_, p_55052_, p_55048_)
            : super.updateShape(p_55048_, p_374298_, p_374153_, p_55052_, p_55049_, p_55053_, p_55050_, p_374540_);
    }

    @Override
    protected void neighborChanged(BlockState p_55041_, Level p_55042_, BlockPos p_55043_, Block p_55044_, @Nullable Orientation p_361888_, boolean p_55046_) {
        boolean flag = p_55042_.hasNeighborSignal(p_55043_);
        if (flag != p_55041_.getValue(POWERED)) {
            if (flag) {
                this.playNote(null, p_55041_, p_55042_, p_55043_);
            }

            p_55042_.setBlock(p_55043_, p_55041_.setValue(POWERED, Boolean.valueOf(flag)), 3);
        }
    }

    private void playNote(@Nullable Entity p_261664_, BlockState p_261606_, Level p_261819_, BlockPos p_262042_) {
        if (p_261606_.getValue(INSTRUMENT).worksAboveNoteBlock() || p_261819_.getBlockState(p_262042_.above()).isAir()) {
            p_261819_.blockEvent(p_262042_, this, 0, 0);
            p_261819_.gameEvent(p_261664_, GameEvent.NOTE_BLOCK_PLAY, p_262042_);
        }
    }

    @Override
    protected InteractionResult useItemOn(
        ItemStack p_316716_, BlockState p_316688_, Level p_316672_, BlockPos p_316355_, Player p_316822_, InteractionHand p_316505_, BlockHitResult p_316667_
    ) {
        return (InteractionResult)(p_316716_.is(ItemTags.NOTE_BLOCK_TOP_INSTRUMENTS) && p_316667_.getDirection() == Direction.UP
            ? InteractionResult.PASS
            : super.useItemOn(p_316716_, p_316688_, p_316672_, p_316355_, p_316822_, p_316505_, p_316667_));
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState p_316441_, Level p_316774_, BlockPos p_316344_, Player p_316884_, BlockHitResult p_316631_) {
        if (!p_316774_.isClientSide) {
            int _new = net.neoforged.neoforge.common.CommonHooks.onNoteChange(p_316774_, p_316344_, p_316441_, p_316441_.getValue(NOTE), p_316441_.cycle(NOTE).getValue(NOTE));
            if (_new == -1) return InteractionResult.FAIL;
            p_316441_ = p_316441_.setValue(NOTE, _new);
            p_316774_.setBlock(p_316344_, p_316441_, 3);
            this.playNote(p_316884_, p_316441_, p_316774_, p_316344_);
            p_316884_.awardStat(Stats.TUNE_NOTEBLOCK);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    protected void attack(BlockState p_55029_, Level p_55030_, BlockPos p_55031_, Player p_55032_) {
        if (!p_55030_.isClientSide) {
            this.playNote(p_55032_, p_55029_, p_55030_, p_55031_);
            p_55032_.awardStat(Stats.PLAY_NOTEBLOCK);
        }
    }

    public static float getPitchFromNote(int p_277409_) {
        return (float)Math.pow(2.0, (double)(p_277409_ - 12) / 12.0);
    }

    @Override
    protected boolean triggerEvent(BlockState p_55023_, Level p_55024_, BlockPos p_55025_, int p_55026_, int p_55027_) {
        net.neoforged.neoforge.event.level.NoteBlockEvent.Play e = new net.neoforged.neoforge.event.level.NoteBlockEvent.Play(p_55024_, p_55025_, p_55023_, p_55023_.getValue(NOTE), p_55023_.getValue(INSTRUMENT));
        if (net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(e).isCanceled()) return false;
        p_55023_ = p_55023_.setValue(NOTE, e.getVanillaNoteId()).setValue(INSTRUMENT, e.getInstrument());
        NoteBlockInstrument noteblockinstrument = p_55023_.getValue(INSTRUMENT);
        float f;
        if (noteblockinstrument.isTunable()) {
            int i = p_55023_.getValue(NOTE);
            f = getPitchFromNote(i);
            p_55024_.addParticle(
                ParticleTypes.NOTE, (double)p_55025_.getX() + 0.5, (double)p_55025_.getY() + 1.2, (double)p_55025_.getZ() + 0.5, (double)i / 24.0, 0.0, 0.0
            );
        } else {
            f = 1.0F;
        }

        Holder<SoundEvent> holder;
        if (noteblockinstrument.hasCustomSound()) {
            ResourceLocation resourcelocation = this.getCustomSoundId(p_55024_, p_55025_);
            if (resourcelocation == null) {
                return false;
            }

            holder = Holder.direct(SoundEvent.createVariableRangeEvent(resourcelocation));
        } else {
            holder = noteblockinstrument.getSoundEvent();
        }

        p_55024_.playSeededSound(
            null,
            (double)p_55025_.getX() + 0.5,
            (double)p_55025_.getY() + 0.5,
            (double)p_55025_.getZ() + 0.5,
            holder,
            SoundSource.RECORDS,
            3.0F,
            f,
            p_55024_.random.nextLong()
        );
        return true;
    }

    @Nullable
    private ResourceLocation getCustomSoundId(Level p_263070_, BlockPos p_262999_) {
        return p_263070_.getBlockEntity(p_262999_.above()) instanceof SkullBlockEntity skullblockentity ? skullblockentity.getNoteBlockSound() : null;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_55055_) {
        p_55055_.add(INSTRUMENT, POWERED, NOTE);
    }
}
