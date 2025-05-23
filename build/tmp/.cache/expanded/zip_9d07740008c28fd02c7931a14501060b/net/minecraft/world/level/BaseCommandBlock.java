package net.minecraft.world.level;

import java.text.SimpleDateFormat;
import java.util.Date;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringUtil;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

public abstract class BaseCommandBlock implements CommandSource {
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
    private static final Component DEFAULT_NAME = Component.literal("@");
    private long lastExecution = -1L;
    private boolean updateLastExecution = true;
    private int successCount;
    private boolean trackOutput = true;
    @Nullable
    private Component lastOutput;
    private String command = "";
    @Nullable
    private Component customName;

    public int getSuccessCount() {
        return this.successCount;
    }

    public void setSuccessCount(int p_45411_) {
        this.successCount = p_45411_;
    }

    public Component getLastOutput() {
        return this.lastOutput == null ? CommonComponents.EMPTY : this.lastOutput;
    }

    public CompoundTag save(CompoundTag p_45422_, HolderLookup.Provider p_330850_) {
        p_45422_.putString("Command", this.command);
        p_45422_.putInt("SuccessCount", this.successCount);
        if (this.customName != null) {
            p_45422_.putString("CustomName", Component.Serializer.toJson(this.customName, p_330850_));
        }

        p_45422_.putBoolean("TrackOutput", this.trackOutput);
        if (this.lastOutput != null && this.trackOutput) {
            p_45422_.putString("LastOutput", Component.Serializer.toJson(this.lastOutput, p_330850_));
        }

        p_45422_.putBoolean("UpdateLastExecution", this.updateLastExecution);
        if (this.updateLastExecution && this.lastExecution > 0L) {
            p_45422_.putLong("LastExecution", this.lastExecution);
        }

        return p_45422_;
    }

    public void load(CompoundTag p_45432_, HolderLookup.Provider p_331513_) {
        this.command = p_45432_.getString("Command");
        this.successCount = p_45432_.getInt("SuccessCount");
        if (p_45432_.contains("CustomName", 8)) {
            this.setCustomName(BlockEntity.parseCustomNameSafe(p_45432_.getString("CustomName"), p_331513_));
        } else {
            this.setCustomName(null);
        }

        if (p_45432_.contains("TrackOutput", 1)) {
            this.trackOutput = p_45432_.getBoolean("TrackOutput");
        }

        if (p_45432_.contains("LastOutput", 8) && this.trackOutput) {
            try {
                this.lastOutput = Component.Serializer.fromJson(p_45432_.getString("LastOutput"), p_331513_);
            } catch (Throwable throwable) {
                this.lastOutput = Component.literal(throwable.getMessage());
            }
        } else {
            this.lastOutput = null;
        }

        if (p_45432_.contains("UpdateLastExecution")) {
            this.updateLastExecution = p_45432_.getBoolean("UpdateLastExecution");
        }

        if (this.updateLastExecution && p_45432_.contains("LastExecution")) {
            this.lastExecution = p_45432_.getLong("LastExecution");
        } else {
            this.lastExecution = -1L;
        }
    }

    public void setCommand(String p_45420_) {
        this.command = p_45420_;
        this.successCount = 0;
    }

    public String getCommand() {
        return this.command;
    }

    public boolean performCommand(Level p_45415_) {
        if (p_45415_.isClientSide || p_45415_.getGameTime() == this.lastExecution) {
            return false;
        } else if ("Searge".equalsIgnoreCase(this.command)) {
            this.lastOutput = Component.literal("#itzlipofutzli");
            this.successCount = 1;
            return true;
        } else {
            this.successCount = 0;
            MinecraftServer minecraftserver = this.getLevel().getServer();
            if (minecraftserver.isCommandBlockEnabled() && !StringUtil.isNullOrEmpty(this.command)) {
                try {
                    this.lastOutput = null;
                    CommandSourceStack commandsourcestack = this.createCommandSourceStack().withCallback((p_45418_, p_45419_) -> {
                        if (p_45418_) {
                            this.successCount++;
                        }
                    });
                    minecraftserver.getCommands().performPrefixedCommand(commandsourcestack, this.command);
                } catch (Throwable throwable) {
                    CrashReport crashreport = CrashReport.forThrowable(throwable, "Executing command block");
                    CrashReportCategory crashreportcategory = crashreport.addCategory("Command to be executed");
                    crashreportcategory.setDetail("Command", this::getCommand);
                    crashreportcategory.setDetail("Name", () -> this.getName().getString());
                    throw new ReportedException(crashreport);
                }
            }

            if (this.updateLastExecution) {
                this.lastExecution = p_45415_.getGameTime();
            } else {
                this.lastExecution = -1L;
            }

            return true;
        }
    }

    public Component getName() {
        return this.customName != null ? this.customName : DEFAULT_NAME;
    }

    @Nullable
    public Component getCustomName() {
        return this.customName;
    }

    public void setCustomName(@Nullable Component p_331531_) {
        this.customName = p_331531_;
    }

    @Override
    public void sendSystemMessage(Component p_220330_) {
        if (this.trackOutput) {
            this.lastOutput = Component.literal("[" + TIME_FORMAT.format(new Date()) + "] ").append(p_220330_);
            this.onUpdated();
        }
    }

    public abstract ServerLevel getLevel();

    public abstract void onUpdated();

    public void setLastOutput(@Nullable Component p_45434_) {
        this.lastOutput = p_45434_;
    }

    public void setTrackOutput(boolean p_45429_) {
        this.trackOutput = p_45429_;
    }

    public boolean isTrackOutput() {
        return this.trackOutput;
    }

    public InteractionResult usedBy(Player p_45413_) {
        if (!p_45413_.canUseGameMasterBlocks()) {
            return InteractionResult.PASS;
        } else {
            if (p_45413_.getCommandSenderWorld().isClientSide) {
                p_45413_.openMinecartCommandBlock(this);
            }

            return InteractionResult.SUCCESS;
        }
    }

    public abstract Vec3 getPosition();

    public abstract CommandSourceStack createCommandSourceStack();

    @Override
    public boolean acceptsSuccess() {
        return this.getLevel().getGameRules().getBoolean(GameRules.RULE_SENDCOMMANDFEEDBACK) && this.trackOutput;
    }

    @Override
    public boolean acceptsFailure() {
        return this.trackOutput;
    }

    @Override
    public boolean shouldInformAdmins() {
        return this.getLevel().getGameRules().getBoolean(GameRules.RULE_COMMANDBLOCKOUTPUT);
    }

    public abstract boolean isValid();
}
