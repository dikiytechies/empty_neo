package net.minecraft.world.level.block.entity;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Clearable;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public class CampfireBlockEntity extends BlockEntity implements Clearable {
    private static final int BURN_COOL_SPEED = 2;
    private static final int NUM_SLOTS = 4;
    private final NonNullList<ItemStack> items = NonNullList.withSize(4, ItemStack.EMPTY);
    private final int[] cookingProgress = new int[4];
    private final int[] cookingTime = new int[4];

    public CampfireBlockEntity(BlockPos p_155301_, BlockState p_155302_) {
        super(BlockEntityType.CAMPFIRE, p_155301_, p_155302_);
    }

    public static void cookTick(
        ServerLevel p_380207_,
        BlockPos p_155308_,
        BlockState p_155309_,
        CampfireBlockEntity p_155310_,
        RecipeManager.CachedCheck<SingleRecipeInput, CampfireCookingRecipe> p_380193_
    ) {
        boolean flag = false;

        for (int i = 0; i < p_155310_.items.size(); i++) {
            ItemStack itemstack = p_155310_.items.get(i);
            if (!itemstack.isEmpty()) {
                flag = true;
                p_155310_.cookingProgress[i]++;
                if (p_155310_.cookingProgress[i] >= p_155310_.cookingTime[i]) {
                    SingleRecipeInput singlerecipeinput = new SingleRecipeInput(itemstack);
                    ItemStack itemstack1 = p_380193_.getRecipeFor(singlerecipeinput, p_380207_)
                        .map(p_379274_ -> p_379274_.value().assemble(singlerecipeinput, p_380207_.registryAccess()))
                        .orElse(itemstack);
                    if (itemstack1.isItemEnabled(p_380207_.enabledFeatures())) {
                        Containers.dropItemStack(p_380207_, (double)p_155308_.getX(), (double)p_155308_.getY(), (double)p_155308_.getZ(), itemstack1);
                        p_155310_.items.set(i, ItemStack.EMPTY);
                        p_380207_.sendBlockUpdated(p_155308_, p_155309_, p_155309_, 3);
                        p_380207_.gameEvent(GameEvent.BLOCK_CHANGE, p_155308_, GameEvent.Context.of(p_155309_));
                    }
                }
            }
        }

        if (flag) {
            setChanged(p_380207_, p_155308_, p_155309_);
        }
    }

    public static void cooldownTick(Level p_155314_, BlockPos p_155315_, BlockState p_155316_, CampfireBlockEntity p_155317_) {
        boolean flag = false;

        for (int i = 0; i < p_155317_.items.size(); i++) {
            if (p_155317_.cookingProgress[i] > 0) {
                flag = true;
                p_155317_.cookingProgress[i] = Mth.clamp(p_155317_.cookingProgress[i] - 2, 0, p_155317_.cookingTime[i]);
            }
        }

        if (flag) {
            setChanged(p_155314_, p_155315_, p_155316_);
        }
    }

    public static void particleTick(Level p_155319_, BlockPos p_155320_, BlockState p_155321_, CampfireBlockEntity p_155322_) {
        RandomSource randomsource = p_155319_.random;
        if (randomsource.nextFloat() < 0.11F) {
            for (int i = 0; i < randomsource.nextInt(2) + 2; i++) {
                CampfireBlock.makeParticles(p_155319_, p_155320_, p_155321_.getValue(CampfireBlock.SIGNAL_FIRE), false);
            }
        }

        int l = p_155321_.getValue(CampfireBlock.FACING).get2DDataValue();

        for (int j = 0; j < p_155322_.items.size(); j++) {
            if (!p_155322_.items.get(j).isEmpty() && randomsource.nextFloat() < 0.2F) {
                Direction direction = Direction.from2DDataValue(Math.floorMod(j + l, 4));
                float f = 0.3125F;
                double d0 = (double)p_155320_.getX()
                    + 0.5
                    - (double)((float)direction.getStepX() * 0.3125F)
                    + (double)((float)direction.getClockWise().getStepX() * 0.3125F);
                double d1 = (double)p_155320_.getY() + 0.5;
                double d2 = (double)p_155320_.getZ()
                    + 0.5
                    - (double)((float)direction.getStepZ() * 0.3125F)
                    + (double)((float)direction.getClockWise().getStepZ() * 0.3125F);

                for (int k = 0; k < 4; k++) {
                    p_155319_.addParticle(ParticleTypes.SMOKE, d0, d1, d2, 0.0, 5.0E-4, 0.0);
                }
            }
        }
    }

    public NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    protected void loadAdditional(CompoundTag p_155312_, HolderLookup.Provider p_323804_) {
        super.loadAdditional(p_155312_, p_323804_);
        this.items.clear();
        ContainerHelper.loadAllItems(p_155312_, this.items, p_323804_);
        if (p_155312_.contains("CookingTimes", 11)) {
            int[] aint = p_155312_.getIntArray("CookingTimes");
            System.arraycopy(aint, 0, this.cookingProgress, 0, Math.min(this.cookingTime.length, aint.length));
        }

        if (p_155312_.contains("CookingTotalTimes", 11)) {
            int[] aint1 = p_155312_.getIntArray("CookingTotalTimes");
            System.arraycopy(aint1, 0, this.cookingTime, 0, Math.min(this.cookingTime.length, aint1.length));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag p_187486_, HolderLookup.Provider p_324005_) {
        super.saveAdditional(p_187486_, p_324005_);
        ContainerHelper.saveAllItems(p_187486_, this.items, true, p_324005_);
        p_187486_.putIntArray("CookingTimes", this.cookingProgress);
        p_187486_.putIntArray("CookingTotalTimes", this.cookingTime);
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider p_324612_) {
        CompoundTag compoundtag = new CompoundTag();
        ContainerHelper.saveAllItems(compoundtag, this.items, true, p_324612_);
        return compoundtag;
    }

    public boolean placeFood(ServerLevel p_380019_, @Nullable LivingEntity p_347582_, ItemStack p_238286_) {
        for (int i = 0; i < this.items.size(); i++) {
            ItemStack itemstack = this.items.get(i);
            if (itemstack.isEmpty()) {
                Optional<RecipeHolder<CampfireCookingRecipe>> optional = p_380019_.recipeAccess()
                    .getRecipeFor(RecipeType.CAMPFIRE_COOKING, new SingleRecipeInput(p_238286_), p_380019_);
                if (optional.isEmpty()) {
                    return false;
                }

                this.cookingTime[i] = optional.get().value().cookingTime();
                this.cookingProgress[i] = 0;
                this.items.set(i, p_238286_.consumeAndReturn(1, p_347582_));
                p_380019_.gameEvent(GameEvent.BLOCK_CHANGE, this.getBlockPos(), GameEvent.Context.of(p_347582_, this.getBlockState()));
                this.markUpdated();
                return true;
            }
        }

        return false;
    }

    private void markUpdated() {
        this.setChanged();
        this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }

    public void dowse() {
        if (this.level != null) {
            this.markUpdated();
        }
    }

    @Override
    protected void applyImplicitComponents(BlockEntity.DataComponentInput p_338534_) {
        super.applyImplicitComponents(p_338534_);
        p_338534_.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(this.getItems());
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder p_338620_) {
        super.collectImplicitComponents(p_338620_);
        p_338620_.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(this.getItems()));
    }

    @Override
    public void removeComponentsFromTag(CompoundTag p_332690_) {
        p_332690_.remove("Items");
    }
}
