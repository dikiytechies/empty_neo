package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.gui.components.SubtitleOverlay;
import net.minecraft.client.gui.components.spectator.SpectatorGui;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.MobEffectTextureManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.numbers.NumberFormat;
import net.minecraft.network.chat.numbers.StyledFormat;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringUtil;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PlayerRideableJumping;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerScoreEntry;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix4fStack;

// Neo: Exceptionally add a static wildcard import to make the patch bearable, and comments to avoid the detection by spotless rules.
/* space for import change */ import static net.neoforged.neoforge.client.gui.VanillaGuiLayers.* /* space for wildcard import */;

@OnlyIn(Dist.CLIENT)
public class Gui {
    private static final ResourceLocation CROSSHAIR_SPRITE = ResourceLocation.withDefaultNamespace("hud/crosshair");
    private static final ResourceLocation CROSSHAIR_ATTACK_INDICATOR_FULL_SPRITE = ResourceLocation.withDefaultNamespace("hud/crosshair_attack_indicator_full");
    private static final ResourceLocation CROSSHAIR_ATTACK_INDICATOR_BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace(
        "hud/crosshair_attack_indicator_background"
    );
    private static final ResourceLocation CROSSHAIR_ATTACK_INDICATOR_PROGRESS_SPRITE = ResourceLocation.withDefaultNamespace(
        "hud/crosshair_attack_indicator_progress"
    );
    private static final ResourceLocation EFFECT_BACKGROUND_AMBIENT_SPRITE = ResourceLocation.withDefaultNamespace("hud/effect_background_ambient");
    private static final ResourceLocation EFFECT_BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace("hud/effect_background");
    private static final ResourceLocation HOTBAR_SPRITE = ResourceLocation.withDefaultNamespace("hud/hotbar");
    private static final ResourceLocation HOTBAR_SELECTION_SPRITE = ResourceLocation.withDefaultNamespace("hud/hotbar_selection");
    private static final ResourceLocation HOTBAR_OFFHAND_LEFT_SPRITE = ResourceLocation.withDefaultNamespace("hud/hotbar_offhand_left");
    private static final ResourceLocation HOTBAR_OFFHAND_RIGHT_SPRITE = ResourceLocation.withDefaultNamespace("hud/hotbar_offhand_right");
    private static final ResourceLocation HOTBAR_ATTACK_INDICATOR_BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace(
        "hud/hotbar_attack_indicator_background"
    );
    private static final ResourceLocation HOTBAR_ATTACK_INDICATOR_PROGRESS_SPRITE = ResourceLocation.withDefaultNamespace(
        "hud/hotbar_attack_indicator_progress"
    );
    private static final ResourceLocation JUMP_BAR_BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace("hud/jump_bar_background");
    private static final ResourceLocation JUMP_BAR_COOLDOWN_SPRITE = ResourceLocation.withDefaultNamespace("hud/jump_bar_cooldown");
    private static final ResourceLocation JUMP_BAR_PROGRESS_SPRITE = ResourceLocation.withDefaultNamespace("hud/jump_bar_progress");
    private static final ResourceLocation EXPERIENCE_BAR_BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace("hud/experience_bar_background");
    private static final ResourceLocation EXPERIENCE_BAR_PROGRESS_SPRITE = ResourceLocation.withDefaultNamespace("hud/experience_bar_progress");
    private static final ResourceLocation ARMOR_EMPTY_SPRITE = ResourceLocation.withDefaultNamespace("hud/armor_empty");
    private static final ResourceLocation ARMOR_HALF_SPRITE = ResourceLocation.withDefaultNamespace("hud/armor_half");
    private static final ResourceLocation ARMOR_FULL_SPRITE = ResourceLocation.withDefaultNamespace("hud/armor_full");
    private static final ResourceLocation FOOD_EMPTY_HUNGER_SPRITE = ResourceLocation.withDefaultNamespace("hud/food_empty_hunger");
    private static final ResourceLocation FOOD_HALF_HUNGER_SPRITE = ResourceLocation.withDefaultNamespace("hud/food_half_hunger");
    private static final ResourceLocation FOOD_FULL_HUNGER_SPRITE = ResourceLocation.withDefaultNamespace("hud/food_full_hunger");
    private static final ResourceLocation FOOD_EMPTY_SPRITE = ResourceLocation.withDefaultNamespace("hud/food_empty");
    private static final ResourceLocation FOOD_HALF_SPRITE = ResourceLocation.withDefaultNamespace("hud/food_half");
    private static final ResourceLocation FOOD_FULL_SPRITE = ResourceLocation.withDefaultNamespace("hud/food_full");
    private static final ResourceLocation AIR_SPRITE = ResourceLocation.withDefaultNamespace("hud/air");
    private static final ResourceLocation AIR_POPPING_SPRITE = ResourceLocation.withDefaultNamespace("hud/air_bursting");
    private static final ResourceLocation AIR_EMPTY_SPRITE = ResourceLocation.withDefaultNamespace("hud/air_empty");
    private static final ResourceLocation HEART_VEHICLE_CONTAINER_SPRITE = ResourceLocation.withDefaultNamespace("hud/heart/vehicle_container");
    private static final ResourceLocation HEART_VEHICLE_FULL_SPRITE = ResourceLocation.withDefaultNamespace("hud/heart/vehicle_full");
    private static final ResourceLocation HEART_VEHICLE_HALF_SPRITE = ResourceLocation.withDefaultNamespace("hud/heart/vehicle_half");
    private static final ResourceLocation VIGNETTE_LOCATION = ResourceLocation.withDefaultNamespace("textures/misc/vignette.png");
    public static final ResourceLocation NAUSEA_LOCATION = ResourceLocation.withDefaultNamespace("textures/misc/nausea.png");
    private static final ResourceLocation SPYGLASS_SCOPE_LOCATION = ResourceLocation.withDefaultNamespace("textures/misc/spyglass_scope.png");
    private static final ResourceLocation POWDER_SNOW_OUTLINE_LOCATION = ResourceLocation.withDefaultNamespace("textures/misc/powder_snow_outline.png");
    private static final Comparator<PlayerScoreEntry> SCORE_DISPLAY_ORDER = Comparator.comparing(PlayerScoreEntry::value)
        .reversed()
        .thenComparing(PlayerScoreEntry::owner, String.CASE_INSENSITIVE_ORDER);
    private static final Component DEMO_EXPIRED_TEXT = Component.translatable("demo.demoExpired");
    private static final Component SAVING_TEXT = Component.translatable("menu.savingLevel");
    private static final float MIN_CROSSHAIR_ATTACK_SPEED = 5.0F;
    private static final int NUM_HEARTS_PER_ROW = 10;
    private static final int LINE_HEIGHT = 10;
    private static final String SPACER = ": ";
    private static final float PORTAL_OVERLAY_ALPHA_MIN = 0.2F;
    private static final int HEART_SIZE = 9;
    private static final int HEART_SEPARATION = 8;
    private static final int NUM_AIR_BUBBLES = 10;
    private static final int AIR_BUBBLE_SIZE = 9;
    private static final int AIR_BUBBLE_SEPERATION = 8;
    private static final int AIR_BUBBLE_POPPING_DURATION = 2;
    private static final int EMPTY_AIR_BUBBLE_DELAY_DURATION = 1;
    private static final float AIR_BUBBLE_POP_SOUND_VOLUME_BASE = 0.5F;
    private static final float AIR_BUBBLE_POP_SOUND_VOLUME_INCREMENT = 0.1F;
    private static final float AIR_BUBBLE_POP_SOUND_PITCH_BASE = 1.0F;
    private static final float AIR_BUBBLE_POP_SOUND_PITCH_INCREMENT = 0.1F;
    private static final int NUM_AIR_BUBBLE_POPPED_BEFORE_SOUND_VOLUME_INCREASE = 3;
    private static final int NUM_AIR_BUBBLE_POPPED_BEFORE_SOUND_PITCH_INCREASE = 5;
    private static final float AUTOSAVE_FADE_SPEED_FACTOR = 0.2F;
    private static final int SAVING_INDICATOR_WIDTH_PADDING_RIGHT = 5;
    private static final int SAVING_INDICATOR_HEIGHT_PADDING_BOTTOM = 5;
    private final RandomSource random = RandomSource.create();
    private final Minecraft minecraft;
    private final ChatComponent chat;
    private int tickCount;
    @Nullable
    private Component overlayMessageString;
    private int overlayMessageTime;
    private boolean animateOverlayMessageColor;
    private boolean chatDisabledByPlayerShown;
    public float vignetteBrightness = 1.0F;
    private int toolHighlightTimer;
    private ItemStack lastToolHighlight = ItemStack.EMPTY;
    private final DebugScreenOverlay debugOverlay;
    private final SubtitleOverlay subtitleOverlay;
    private final SpectatorGui spectatorGui;
    private final PlayerTabOverlay tabList;
    private final BossHealthOverlay bossOverlay;
    private int titleTime;
    @Nullable
    private Component title;
    @Nullable
    private Component subtitle;
    private int titleFadeInTime;
    private int titleStayTime;
    private int titleFadeOutTime;
    private int lastHealth;
    private int displayHealth;
    private long lastHealthTime;
    private long healthBlinkTime;
    private int lastBubblePopSoundPlayed;
    private float autosaveIndicatorValue;
    private float lastAutosaveIndicatorValue;
    /** Neo: This is empty and unused, rendering goes through {@link #layerManager} instead. */
    @Deprecated
    private final LayeredDraw layers = new LayeredDraw();
    private final net.neoforged.neoforge.client.gui.GuiLayerManager layerManager = new net.neoforged.neoforge.client.gui.GuiLayerManager();
    private float scopeScale;

    /**
     * Neo: This variable controls the height of overlays on the left of the hotbar (e.g. health, armor).
     */
    public int leftHeight;
    /**
     * Neo: This variable controls the height of overlays on the right of the hotbar (e.g. food, vehicle health, air).
     */
    public int rightHeight;

    public Gui(Minecraft p_232355_) {
        this.minecraft = p_232355_;
        this.debugOverlay = new DebugScreenOverlay(p_232355_);
        this.spectatorGui = new SpectatorGui(p_232355_);
        this.chat = new ChatComponent(p_232355_);
        this.tabList = new PlayerTabOverlay(p_232355_, this);
        this.bossOverlay = new BossHealthOverlay(p_232355_);
        this.subtitleOverlay = new SubtitleOverlay(p_232355_);
        this.resetTitleTimes();
        var playerHealthComponents = new net.neoforged.neoforge.client.gui.GuiLayerManager()
                .add(PLAYER_HEALTH, (guiGraphics, partialTick) -> renderHealthLevel(guiGraphics))
                .add(ARMOR_LEVEL, (guiGraphics, partialTick) -> renderArmorLevel(guiGraphics))
                .add(FOOD_LEVEL, (guiGraphics, partialTick) -> renderFoodLevel(guiGraphics));
        var layereddraw = new net.neoforged.neoforge.client.gui.GuiLayerManager()
            .add(CAMERA_OVERLAYS, this::renderCameraOverlays)
            .add(CROSSHAIR, this::renderCrosshair)
            .add(HOTBAR, this::renderHotbar)
            .add(JUMP_METER, this::maybeRenderJumpMeter)
            .add(EXPERIENCE_BAR, this::maybeRenderExperienceBar)
            .add(playerHealthComponents, () -> this.minecraft.gameMode.canHurtPlayer())
            .add(VEHICLE_HEALTH, this::maybeRenderVehicleHealth)
            // Air goes above vehicle health, it must render after it for `rightHeight` to work!
            .add(AIR_LEVEL, (guiGraphics, partialTick) -> { if (this.minecraft.gameMode.canHurtPlayer()) renderAirLevel(guiGraphics); })
            .add(SELECTED_ITEM_NAME, this::maybeRenderSelectedItemName)
            .add(SPECTATOR_TOOLTIP, this::maybeRenderSpectatorTooltip)
            .add(EXPERIENCE_LEVEL, this::renderExperienceLevel)
            .add(EFFECTS, this::renderEffects)
            .add(BOSS_OVERLAY, (p_315814_, p_315815_) -> this.bossOverlay.render(p_315814_));
        var layereddraw1 = new net.neoforged.neoforge.client.gui.GuiLayerManager()
            .add(DEMO_OVERLAY, this::renderDemoOverlay)
            .add(DEBUG_OVERLAY, (p_315812_, p_315813_) -> {
                if (this.debugOverlay.showDebugScreen()) {
                    this.debugOverlay.render(p_315812_);
                }
            })
            .add(SCOREBOARD_SIDEBAR, this::renderScoreboardSidebar)
            .add(OVERLAY_MESSAGE, this::renderOverlayMessage)
            .add(TITLE, this::renderTitle)
            .add(CHAT, this::renderChat)
            .add(TAB_LIST, this::renderTabList)
            .add(SUBTITLE_OVERLAY, (p_315816_, p_315817_) -> this.subtitleOverlay.render(p_315816_))
            .add(SAVING_INDICATOR, this::renderSavingIndicator);
        this.layerManager.add(layereddraw, () -> !p_232355_.options.hideGui).add(SLEEP_OVERLAY, this::renderSleepOverlay).add(layereddraw1, () -> !p_232355_.options.hideGui);
    }

    public void resetTitleTimes() {
        this.titleFadeInTime = 10;
        this.titleStayTime = 70;
        this.titleFadeOutTime = 20;
    }

    public void render(GuiGraphics p_282884_, DeltaTracker p_348630_) {
        leftHeight = 39;
        rightHeight = 39;
        this.layerManager.render(p_282884_, p_348630_);
    }

    private void renderCameraOverlays(GuiGraphics p_316735_, DeltaTracker p_348538_) {
        if (Minecraft.useFancyGraphics()) {
            this.renderVignette(p_316735_, this.minecraft.getCameraEntity());
        }

        float f = p_348538_.getGameTimeDeltaTicks();
        this.scopeScale = Mth.lerp(0.5F * f, this.scopeScale, 1.125F);
        if (this.minecraft.options.getCameraType().isFirstPerson()) {
            if (this.minecraft.player.isScoping()) {
                this.renderSpyglassOverlay(p_316735_, this.scopeScale);
            } else {
                this.scopeScale = 0.5F;

                for (EquipmentSlot equipmentslot : EquipmentSlot.values()) {
                    ItemStack itemstack = this.minecraft.player.getItemBySlot(equipmentslot);
                    Equippable equippable = itemstack.get(DataComponents.EQUIPPABLE);
                    if (equippable != null && equippable.slot() == equipmentslot) {
                        if (equippable.cameraOverlay().isPresent()) {
                            this.renderTextureOverlay(p_316735_, equippable.cameraOverlay().get().withPath(p_380782_ -> "textures/" + p_380782_ + ".png"), 1.0F);
                        }

                        net.neoforged.neoforge.client.extensions.common.IClientItemExtensions.of(itemstack).renderFirstPersonOverlay(itemstack, equipmentslot, this.minecraft.player, p_316735_, p_348538_);
                    }
                }
            }
        }

        if (this.minecraft.player.getTicksFrozen() > 0) {
            this.renderTextureOverlay(p_316735_, POWDER_SNOW_OUTLINE_LOCATION, this.minecraft.player.getPercentFrozen());
        }

        float f1 = Mth.lerp(
            p_348538_.getGameTimeDeltaPartialTick(false), this.minecraft.player.oSpinningEffectIntensity, this.minecraft.player.spinningEffectIntensity
        );
        if (f1 > 0.0F) {
            if (!this.minecraft.player.hasEffect(MobEffects.CONFUSION)) {
                this.renderPortalOverlay(p_316735_, f1);
            } else {
                float f2 = this.minecraft.options.screenEffectScale().get().floatValue();
                if (f2 < 1.0F) {
                    float f3 = f1 * (1.0F - f2);
                    this.renderConfusionOverlay(p_316735_, f3);
                }
            }
        }
    }

    private void renderSleepOverlay(GuiGraphics p_316466_, DeltaTracker p_348533_) {
        if (this.minecraft.player.getSleepTimer() > 0) {
            Profiler.get().push("sleep");
            float f = (float)this.minecraft.player.getSleepTimer();
            float f1 = f / 100.0F;
            if (f1 > 1.0F) {
                f1 = 1.0F - (f - 100.0F) / 10.0F;
            }

            int i = (int)(220.0F * f1) << 24 | 1052704;
            p_316466_.fill(RenderType.guiOverlay(), 0, 0, p_316466_.guiWidth(), p_316466_.guiHeight(), i);
            Profiler.get().pop();
        }
    }

    private void renderOverlayMessage(GuiGraphics p_316291_, DeltaTracker p_348653_) {
        Font font = this.getFont();
        if (this.overlayMessageString != null && this.overlayMessageTime > 0) {
            Profiler.get().push("overlayMessage");
            float f = (float)this.overlayMessageTime - p_348653_.getGameTimeDeltaPartialTick(false);
            int i = (int)(f * 255.0F / 20.0F);
            if (i > 255) {
                i = 255;
            }

            if (i > 8) {
                //Include a shift based on the bar height plus the difference between the height that renderSelectedItemName
                // renders at (59) and the height that the overlay/status bar renders at (68) by default
                int yShift = Math.max(leftHeight, rightHeight) + (68 - 59);
                p_316291_.pose().pushPose();
                //If y shift is smaller less than the default y level, just render it at the base y level
                p_316291_.pose().translate((float)(p_316291_.guiWidth() / 2), (float)(p_316291_.guiHeight() - Math.max(yShift, 68)), 0.0F);
                int j;
                if (this.animateOverlayMessageColor) {
                    j = Mth.hsvToArgb(f / 50.0F, 0.7F, 0.6F, i);
                } else {
                    j = ARGB.color(i, -1);
                }

                int k = font.width(this.overlayMessageString);
                p_316291_.drawStringWithBackdrop(font, this.overlayMessageString, -k / 2, -4, k, j);
                p_316291_.pose().popPose();
            }

            Profiler.get().pop();
        }
    }

    private void renderTitle(GuiGraphics p_316629_, DeltaTracker p_348613_) {
        if (this.title != null && this.titleTime > 0) {
            Font font = this.getFont();
            Profiler.get().push("titleAndSubtitle");
            float f = (float)this.titleTime - p_348613_.getGameTimeDeltaPartialTick(false);
            int i = 255;
            if (this.titleTime > this.titleFadeOutTime + this.titleStayTime) {
                float f1 = (float)(this.titleFadeInTime + this.titleStayTime + this.titleFadeOutTime) - f;
                i = (int)(f1 * 255.0F / (float)this.titleFadeInTime);
            }

            if (this.titleTime <= this.titleFadeOutTime) {
                i = (int)(f * 255.0F / (float)this.titleFadeOutTime);
            }

            i = Mth.clamp(i, 0, 255);
            if (i > 8) {
                p_316629_.pose().pushPose();
                p_316629_.pose().translate((float)(p_316629_.guiWidth() / 2), (float)(p_316629_.guiHeight() / 2), 0.0F);
                p_316629_.pose().pushPose();
                p_316629_.pose().scale(4.0F, 4.0F, 4.0F);
                int l = font.width(this.title);
                int j = ARGB.color(i, -1);
                p_316629_.drawStringWithBackdrop(font, this.title, -l / 2, -10, l, j);
                p_316629_.pose().popPose();
                if (this.subtitle != null) {
                    p_316629_.pose().pushPose();
                    p_316629_.pose().scale(2.0F, 2.0F, 2.0F);
                    int k = font.width(this.subtitle);
                    p_316629_.drawStringWithBackdrop(font, this.subtitle, -k / 2, 5, k, j);
                    p_316629_.pose().popPose();
                }

                p_316629_.pose().popPose();
            }

            Profiler.get().pop();
        }
    }

    private void renderChat(GuiGraphics p_316307_, DeltaTracker p_348631_) {
        if (!this.chat.isChatFocused()) {
            Window window = this.minecraft.getWindow();
            // Neo: Allow customizing position of chat component
            var chatBottomMargin = 40; // See ChatComponent#BOTTOM_MARGIN (used in translate calls in ChatComponent#render)
            var event = net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(
                    new net.neoforged.neoforge.client.event.CustomizeGuiOverlayEvent.Chat(window, p_316307_, p_348631_, 0, p_316307_.guiHeight() - chatBottomMargin)
            );

            // The event is given the absolute Y position; account for chat component's own offsetting here
            p_316307_.pose().pushPose();
            p_316307_.pose().translate(event.getPosX(), (event.getPosY() - p_316307_.guiHeight() + chatBottomMargin) / this.chat.getScale(), 0.0F);
            int i = Mth.floor(this.minecraft.mouseHandler.xpos() * (double)window.getGuiScaledWidth() / (double)window.getScreenWidth());
            int j = Mth.floor(this.minecraft.mouseHandler.ypos() * (double)window.getGuiScaledHeight() / (double)window.getScreenHeight());
            this.chat.render(p_316307_, this.tickCount, i, j, false);
            p_316307_.pose().popPose();
        }
    }

    private void renderScoreboardSidebar(GuiGraphics p_316834_, DeltaTracker p_348514_) {
        Scoreboard scoreboard = this.minecraft.level.getScoreboard();
        Objective objective = null;
        PlayerTeam playerteam = scoreboard.getPlayersTeam(this.minecraft.player.getScoreboardName());
        if (playerteam != null) {
            DisplaySlot displayslot = DisplaySlot.teamColorToSlot(playerteam.getColor());
            if (displayslot != null) {
                objective = scoreboard.getDisplayObjective(displayslot);
            }
        }

        Objective objective1 = objective != null ? objective : scoreboard.getDisplayObjective(DisplaySlot.SIDEBAR);
        if (objective1 != null) {
            this.displayScoreboardSidebar(p_316834_, objective1);
        }
    }

    private void renderTabList(GuiGraphics p_316182_, DeltaTracker p_348611_) {
        Scoreboard scoreboard = this.minecraft.level.getScoreboard();
        Objective objective = scoreboard.getDisplayObjective(DisplaySlot.LIST);
        if (!this.minecraft.options.keyPlayerList.isDown()
            || this.minecraft.isLocalServer() && this.minecraft.player.connection.getListedOnlinePlayers().size() <= 1 && objective == null) {
            this.tabList.setVisible(false);
        } else {
            this.tabList.setVisible(true);
            this.tabList.render(p_316182_, p_316182_.guiWidth(), scoreboard, objective);
        }
    }

    private void renderCrosshair(GuiGraphics p_282828_, DeltaTracker p_348625_) {
        Options options = this.minecraft.options;
        if (options.getCameraType().isFirstPerson()) {
            if (this.minecraft.gameMode.getPlayerMode() != GameType.SPECTATOR || this.canRenderCrosshairForSpectator(this.minecraft.hitResult)) {
                if (this.debugOverlay.showDebugScreen() && !this.minecraft.player.isReducedDebugInfo() && !options.reducedDebugInfo().get()) {
                    Camera camera = this.minecraft.gameRenderer.getMainCamera();
                    Matrix4fStack matrix4fstack = RenderSystem.getModelViewStack();
                    matrix4fstack.pushMatrix();
                    matrix4fstack.mul(p_282828_.pose().last().pose());
                    matrix4fstack.translate((float)(p_282828_.guiWidth() / 2), (float)(p_282828_.guiHeight() / 2), 0.0F);
                    matrix4fstack.rotateX(-camera.getXRot() * (float) (Math.PI / 180.0));
                    matrix4fstack.rotateY(camera.getYRot() * (float) (Math.PI / 180.0));
                    matrix4fstack.scale(-1.0F, -1.0F, -1.0F);
                    RenderSystem.renderCrosshair(10);
                    matrix4fstack.popMatrix();
                } else {
                    int i = 15;
                    p_282828_.blitSprite(RenderType::crosshair, CROSSHAIR_SPRITE, (p_282828_.guiWidth() - 15) / 2, (p_282828_.guiHeight() - 15) / 2, 15, 15);
                    if (this.minecraft.options.attackIndicator().get() == AttackIndicatorStatus.CROSSHAIR) {
                        float f = this.minecraft.player.getAttackStrengthScale(0.0F);
                        boolean flag = false;
                        if (this.minecraft.crosshairPickEntity != null && this.minecraft.crosshairPickEntity instanceof LivingEntity && f >= 1.0F) {
                            flag = this.minecraft.player.getCurrentItemAttackStrengthDelay() > 5.0F;
                            flag &= this.minecraft.crosshairPickEntity.isAlive();
                        }

                        int j = p_282828_.guiHeight() / 2 - 7 + 16;
                        int k = p_282828_.guiWidth() / 2 - 8;
                        if (flag) {
                            p_282828_.blitSprite(RenderType::crosshair, CROSSHAIR_ATTACK_INDICATOR_FULL_SPRITE, k, j, 16, 16);
                        } else if (f < 1.0F) {
                            int l = (int)(f * 17.0F);
                            p_282828_.blitSprite(RenderType::crosshair, CROSSHAIR_ATTACK_INDICATOR_BACKGROUND_SPRITE, k, j, 16, 4);
                            p_282828_.blitSprite(RenderType::crosshair, CROSSHAIR_ATTACK_INDICATOR_PROGRESS_SPRITE, 16, 4, 0, 0, k, j, l, 4);
                        }
                    }
                }
            }
        }
    }

    private boolean canRenderCrosshairForSpectator(@Nullable HitResult p_93025_) {
        if (p_93025_ == null) {
            return false;
        } else if (p_93025_.getType() == HitResult.Type.ENTITY) {
            return ((EntityHitResult)p_93025_).getEntity() instanceof MenuProvider;
        } else if (p_93025_.getType() == HitResult.Type.BLOCK) {
            BlockPos blockpos = ((BlockHitResult)p_93025_).getBlockPos();
            Level level = this.minecraft.level;
            return level.getBlockState(blockpos).getMenuProvider(level, blockpos) != null;
        } else {
            return false;
        }
    }

    private void renderEffects(GuiGraphics p_282812_, DeltaTracker p_348654_) {
        Collection<MobEffectInstance> collection = this.minecraft.player.getActiveEffects();
        if (!collection.isEmpty() && (this.minecraft.screen == null || !this.minecraft.screen.showsActiveEffects())) {
            int i = 0;
            int j = 0;
            MobEffectTextureManager mobeffecttexturemanager = this.minecraft.getMobEffectTextures();
            List<Runnable> list = Lists.newArrayListWithExpectedSize(collection.size());

            for (MobEffectInstance mobeffectinstance : Ordering.natural().reverse().sortedCopy(collection)) {
                var renderer = net.neoforged.neoforge.client.extensions.common.IClientMobEffectExtensions.of(mobeffectinstance);
                if (!renderer.isVisibleInGui(mobeffectinstance)) continue;
                Holder<MobEffect> holder = mobeffectinstance.getEffect();
                if (mobeffectinstance.showIcon()) {
                    int k = p_282812_.guiWidth();
                    int l = 1;
                    if (this.minecraft.isDemo()) {
                        l += 15;
                    }

                    if (holder.value().isBeneficial()) {
                        i++;
                        k -= 25 * i;
                    } else {
                        j++;
                        k -= 25 * j;
                        l += 26;
                    }

                    float f = 1.0F;
                    if (mobeffectinstance.isAmbient()) {
                        p_282812_.blitSprite(RenderType::guiTextured, EFFECT_BACKGROUND_AMBIENT_SPRITE, k, l, 24, 24);
                    } else {
                        p_282812_.blitSprite(RenderType::guiTextured, EFFECT_BACKGROUND_SPRITE, k, l, 24, 24);
                        if (mobeffectinstance.endsWithin(200)) {
                            int i1 = mobeffectinstance.getDuration();
                            int j1 = 10 - i1 / 20;
                            f = Mth.clamp((float)i1 / 10.0F / 5.0F * 0.5F, 0.0F, 0.5F)
                                + Mth.cos((float)i1 * (float) Math.PI / 5.0F) * Mth.clamp((float)j1 / 10.0F * 0.25F, 0.0F, 0.25F);
                            f = Mth.clamp(f, 0.0F, 1.0F);
                        }
                    }

                    if (renderer.renderGuiIcon(mobeffectinstance, this, p_282812_, k, l, 0, f)) continue;
                    TextureAtlasSprite textureatlassprite = mobeffecttexturemanager.get(holder);
                    int l1 = k;
                    int k1 = l;
                    float f1 = f;
                    list.add(() -> {
                        int i2 = ARGB.white(f1);
                        p_282812_.blitSprite(RenderType::guiTextured, textureatlassprite, l1 + 3, k1 + 3, 18, 18, i2);
                    });
                }
            }

            list.forEach(Runnable::run);
        }
    }

    @Deprecated // Neo: Split up into different layers
    private void renderHotbarAndDecorations(GuiGraphics p_316628_, DeltaTracker p_348543_) {
        renderHotbar(p_316628_, p_348543_);
        maybeRenderJumpMeter(p_316628_, p_348543_);
        maybeRenderExperienceBar(p_316628_, p_348543_);
        maybeRenderPlayerHealth(p_316628_, p_348543_);
        maybeRenderVehicleHealth(p_316628_, p_348543_);
        maybeRenderSelectedItemName(p_316628_, p_348543_);
        maybeRenderSpectatorTooltip(p_316628_, p_348543_);
    }

    private void renderHotbar(GuiGraphics p_316628_, DeltaTracker p_348543_) {
        if (this.minecraft.gameMode.getPlayerMode() == GameType.SPECTATOR) {
            this.spectatorGui.renderHotbar(p_316628_);
        } else {
            this.renderItemHotbar(p_316628_, p_348543_);
        }
    }

    private void maybeRenderJumpMeter(GuiGraphics p_316628_, DeltaTracker p_348543_) {
        int i = p_316628_.guiWidth() / 2 - 91;
        PlayerRideableJumping playerrideablejumping = this.minecraft.player.jumpableVehicle();
        if (playerrideablejumping != null) {
            this.renderJumpMeter(playerrideablejumping, p_316628_, i);
        }

    }

    private void maybeRenderExperienceBar(GuiGraphics p_316628_, DeltaTracker p_348543_) {
        int i = p_316628_.guiWidth() / 2 - 91;
        if (this.minecraft.player.jumpableVehicle() == null && this.isExperienceBarVisible()) {
            this.renderExperienceBar(p_316628_, i);
        }
    }

    private void maybeRenderPlayerHealth(GuiGraphics p_316628_, DeltaTracker p_348543_) {
        if (this.minecraft.gameMode.canHurtPlayer()) {
            this.renderPlayerHealth(p_316628_);
        }
    }

    private void maybeRenderVehicleHealth(GuiGraphics p_316628_, DeltaTracker p_348543_) {
        this.renderVehicleHealth(p_316628_);
    }

    private void maybeRenderSelectedItemName(GuiGraphics p_316628_, DeltaTracker p_348543_) {
        if (this.minecraft.gameMode.getPlayerMode() != GameType.SPECTATOR) {
            this.renderSelectedItemName(p_316628_, Math.max(this.leftHeight, this.rightHeight));
        }
    }

    private void maybeRenderSpectatorTooltip(GuiGraphics p_316628_, DeltaTracker p_348543_) {
        if (this.minecraft.gameMode.getPlayerMode() == GameType.SPECTATOR && this.minecraft.player.isSpectator()) {
            this.spectatorGui.renderTooltip(p_316628_);
        }
    }

    private void renderItemHotbar(GuiGraphics p_316896_, DeltaTracker p_348464_) {
        Player player = this.getCameraPlayer();
        if (player != null) {
            ItemStack itemstack = player.getOffhandItem();
            HumanoidArm humanoidarm = player.getMainArm().getOpposite();
            int i = p_316896_.guiWidth() / 2;
            int j = 182;
            int k = 91;
            p_316896_.pose().pushPose();
            p_316896_.pose().translate(0.0F, 0.0F, -90.0F);
            p_316896_.blitSprite(RenderType::guiTextured, HOTBAR_SPRITE, i - 91, p_316896_.guiHeight() - 22, 182, 22);
            p_316896_.blitSprite(
                RenderType::guiTextured, HOTBAR_SELECTION_SPRITE, i - 91 - 1 + player.getInventory().selected * 20, p_316896_.guiHeight() - 22 - 1, 24, 23
            );
            if (!itemstack.isEmpty()) {
                if (humanoidarm == HumanoidArm.LEFT) {
                    p_316896_.blitSprite(RenderType::guiTextured, HOTBAR_OFFHAND_LEFT_SPRITE, i - 91 - 29, p_316896_.guiHeight() - 23, 29, 24);
                } else {
                    p_316896_.blitSprite(RenderType::guiTextured, HOTBAR_OFFHAND_RIGHT_SPRITE, i + 91, p_316896_.guiHeight() - 23, 29, 24);
                }
            }

            p_316896_.pose().popPose();
            int l = 1;

            for (int i1 = 0; i1 < 9; i1++) {
                int j1 = i - 90 + i1 * 20 + 2;
                int k1 = p_316896_.guiHeight() - 16 - 3;
                this.renderSlot(p_316896_, j1, k1, p_348464_, player, player.getInventory().items.get(i1), l++);
            }

            if (!itemstack.isEmpty()) {
                int i2 = p_316896_.guiHeight() - 16 - 3;
                if (humanoidarm == HumanoidArm.LEFT) {
                    this.renderSlot(p_316896_, i - 91 - 26, i2, p_348464_, player, itemstack, l++);
                } else {
                    this.renderSlot(p_316896_, i + 91 + 10, i2, p_348464_, player, itemstack, l++);
                }
            }

            if (this.minecraft.options.attackIndicator().get() == AttackIndicatorStatus.HOTBAR) {
                float f = this.minecraft.player.getAttackStrengthScale(0.0F);
                if (f < 1.0F) {
                    int j2 = p_316896_.guiHeight() - 20;
                    int k2 = i + 91 + 6;
                    if (humanoidarm == HumanoidArm.RIGHT) {
                        k2 = i - 91 - 22;
                    }

                    int l1 = (int)(f * 19.0F);
                    p_316896_.blitSprite(RenderType::guiTextured, HOTBAR_ATTACK_INDICATOR_BACKGROUND_SPRITE, k2, j2, 18, 18);
                    p_316896_.blitSprite(RenderType::guiTextured, HOTBAR_ATTACK_INDICATOR_PROGRESS_SPRITE, 18, 18, 0, 18 - l1, k2, j2 + 18 - l1, 18, l1);
                }
            }
        }
    }

    private void renderJumpMeter(PlayerRideableJumping p_282774_, GuiGraphics p_282939_, int p_283351_) {
        Profiler.get().push("jumpBar");
        float f = this.minecraft.player.getJumpRidingScale();
        int i = 182;
        int j = (int)(f * 183.0F);
        int k = p_282939_.guiHeight() - 32 + 3;
        p_282939_.blitSprite(RenderType::guiTextured, JUMP_BAR_BACKGROUND_SPRITE, p_283351_, k, 182, 5);
        if (p_282774_.getJumpCooldown() > 0) {
            p_282939_.blitSprite(RenderType::guiTextured, JUMP_BAR_COOLDOWN_SPRITE, p_283351_, k, 182, 5);
        } else if (j > 0) {
            p_282939_.blitSprite(RenderType::guiTextured, JUMP_BAR_PROGRESS_SPRITE, 182, 5, 0, 0, p_283351_, k, j, 5);
        }

        Profiler.get().pop();
    }

    private void renderExperienceBar(GuiGraphics p_281906_, int p_282731_) {
        Profiler.get().push("expBar");
        int i = this.minecraft.player.getXpNeededForNextLevel();
        if (i > 0) {
            int j = 182;
            int k = (int)(this.minecraft.player.experienceProgress * 183.0F);
            int l = p_281906_.guiHeight() - 32 + 3;
            p_281906_.blitSprite(RenderType::guiTextured, EXPERIENCE_BAR_BACKGROUND_SPRITE, p_282731_, l, 182, 5);
            if (k > 0) {
                p_281906_.blitSprite(RenderType::guiTextured, EXPERIENCE_BAR_PROGRESS_SPRITE, 182, 5, 0, 0, p_282731_, l, k, 5);
            }
        }

        Profiler.get().pop();
    }

    private void renderExperienceLevel(GuiGraphics p_320582_, DeltaTracker p_348622_) {
        int i = this.minecraft.player.experienceLevel;
        if (this.isExperienceBarVisible() && i > 0) {
            Profiler.get().push("expLevel");
            String s = i + "";
            int j = (p_320582_.guiWidth() - this.getFont().width(s)) / 2;
            int k = p_320582_.guiHeight() - 31 - 4;
            p_320582_.drawString(this.getFont(), s, j + 1, k, 0, false);
            p_320582_.drawString(this.getFont(), s, j - 1, k, 0, false);
            p_320582_.drawString(this.getFont(), s, j, k + 1, 0, false);
            p_320582_.drawString(this.getFont(), s, j, k - 1, 0, false);
            p_320582_.drawString(this.getFont(), s, j, k, 8453920, false);
            Profiler.get().pop();
        }
    }

    private boolean isExperienceBarVisible() {
        return this.minecraft.player.jumpableVehicle() == null && this.minecraft.gameMode.hasExperience();
    }

    private void renderSelectedItemName(GuiGraphics p_283501_) {
        renderSelectedItemName(p_283501_, 0);
    }

    public void renderSelectedItemName(GuiGraphics p_283501_, int yShift) {
        Profiler.get().push("selectedItemName");
        if (this.toolHighlightTimer > 0 && !this.lastToolHighlight.isEmpty()) {
            MutableComponent mutablecomponent = Component.empty()
                .append(this.lastToolHighlight.getHoverName())
                .withStyle(this.lastToolHighlight.getRarity().getStyleModifier());
            if (this.lastToolHighlight.has(DataComponents.CUSTOM_NAME)) {
                mutablecomponent.withStyle(ChatFormatting.ITALIC);
            }

            Component highlightTip = this.lastToolHighlight.getHighlightTip(mutablecomponent);
            int i = this.getFont().width(highlightTip);
            int j = (p_283501_.guiWidth() - i) / 2;
            int k = p_283501_.guiHeight() - Math.max(yShift, 59);
            if (!this.minecraft.gameMode.canHurtPlayer()) {
                k += 14;
            }

            int l = (int)((float)this.toolHighlightTimer * 256.0F / 10.0F);
            if (l > 255) {
                l = 255;
            }

            if (l > 0) {
                Font font = net.neoforged.neoforge.client.extensions.common.IClientItemExtensions.of(lastToolHighlight).getFont(lastToolHighlight, net.neoforged.neoforge.client.extensions.common.IClientItemExtensions.FontContext.SELECTED_ITEM_NAME);
                if (font == null) {
                    p_283501_.drawStringWithBackdrop(this.getFont(), highlightTip, j, k, i, ARGB.color(l, -1));
                } else {
                    j = (p_283501_.guiWidth() - font.width(highlightTip)) / 2;
                    p_283501_.drawStringWithBackdrop(font, highlightTip, j, k, i, ARGB.color(l, -1));
                }
            }
        }

        Profiler.get().pop();
    }

    private void renderDemoOverlay(GuiGraphics p_281825_, DeltaTracker p_348679_) {
        if (this.minecraft.isDemo() && !this.getDebugOverlay().showDebugScreen()) { // Neo: Hide demo timer when F3 debug overlay is open; fixes MC-271166
            Profiler.get().push("demo");
            Component component;
            if (this.minecraft.level.getGameTime() >= 120500L) {
                component = DEMO_EXPIRED_TEXT;
            } else {
                component = Component.translatable(
                    "demo.remainingTime",
                    StringUtil.formatTickDuration((int)(120500L - this.minecraft.level.getGameTime()), this.minecraft.level.tickRateManager().tickrate())
                );
            }

            int i = this.getFont().width(component);
            int j = p_281825_.guiWidth() - i - 10;
            int k = 5;
            p_281825_.drawStringWithBackdrop(this.getFont(), component, j, 5, i, -1);
            Profiler.get().pop();
        }
    }

    private void displayScoreboardSidebar(GuiGraphics p_282008_, Objective p_283455_) {
        Scoreboard scoreboard = p_283455_.getScoreboard();
        NumberFormat numberformat = p_283455_.numberFormatOrDefault(StyledFormat.SIDEBAR_DEFAULT);

        @OnlyIn(Dist.CLIENT)
        record DisplayEntry(Component name, Component score, int scoreWidth) {
        }

        DisplayEntry[] agui$1displayentry = scoreboard.listPlayerScores(p_283455_)
            .stream()
            .filter(p_313419_ -> !p_313419_.isHidden())
            .sorted(SCORE_DISPLAY_ORDER)
            .limit(15L)
            .map(p_313418_ -> {
                PlayerTeam playerteam = scoreboard.getPlayersTeam(p_313418_.owner());
                Component component1 = p_313418_.ownerName();
                Component component2 = PlayerTeam.formatNameForTeam(playerteam, component1);
                Component component3 = p_313418_.formatValue(numberformat);
                int k3 = this.getFont().width(component3);
                return new DisplayEntry(component2, component3, k3);
            })
            .toArray(DisplayEntry[]::new);
        Component component = p_283455_.getDisplayName();
        int i = this.getFont().width(component);
        int j = i;
        int k = this.getFont().width(": ");

        for (DisplayEntry gui$1displayentry : agui$1displayentry) {
            j = Math.max(j, this.getFont().width(gui$1displayentry.name) + (gui$1displayentry.scoreWidth > 0 ? k + gui$1displayentry.scoreWidth : 0));
        }

        int l2 = agui$1displayentry.length;
        int i3 = l2 * 9;
        int j3 = p_282008_.guiHeight() / 2 + i3 / 3;
        int l = 3;
        int i1 = p_282008_.guiWidth() - j - 3;
        int j1 = p_282008_.guiWidth() - 3 + 2;
        int k1 = this.minecraft.options.getBackgroundColor(0.3F);
        int l1 = this.minecraft.options.getBackgroundColor(0.4F);
        int i2 = j3 - l2 * 9;
        p_282008_.fill(i1 - 2, i2 - 9 - 1, j1, i2 - 1, l1);
        p_282008_.fill(i1 - 2, i2 - 1, j1, j3, k1);
        p_282008_.drawString(this.getFont(), component, i1 + j / 2 - i / 2, i2 - 9, -1, false);

        for (int j2 = 0; j2 < l2; j2++) {
            DisplayEntry gui$1displayentry1 = agui$1displayentry[j2];
            int k2 = j3 - (l2 - j2) * 9;
            p_282008_.drawString(this.getFont(), gui$1displayentry1.name, i1, k2, -1, false);
            p_282008_.drawString(this.getFont(), gui$1displayentry1.score, j1 - gui$1displayentry1.scoreWidth, k2, -1, false);
        }
    }

    @Nullable
    private Player getCameraPlayer() {
        return this.minecraft.getCameraEntity() instanceof Player player ? player : null;
    }

    @Nullable
    private LivingEntity getPlayerVehicleWithHealth() {
        Player player = this.getCameraPlayer();
        if (player != null) {
            Entity entity = player.getVehicle();
            if (entity == null) {
                return null;
            }

            if (entity instanceof LivingEntity) {
                return (LivingEntity)entity;
            }
        }

        return null;
    }

    private int getVehicleMaxHearts(@Nullable LivingEntity p_93023_) {
        if (p_93023_ != null && p_93023_.showVehicleHealth()) {
            float f = p_93023_.getMaxHealth();
            int i = (int)(f + 0.5F) / 2;
            if (i > 30) {
                i = 30;
            }

            return i;
        } else {
            return 0;
        }
    }

    private int getVisibleVehicleHeartRows(int p_93013_) {
        return (int)Math.ceil((double)p_93013_ / 10.0);
    }

    @Deprecated // Neo: Split up into different layers
    private void renderPlayerHealth(GuiGraphics p_283143_) {
        renderHealthLevel(p_283143_);
        renderArmorLevel(p_283143_);
        renderFoodLevel(p_283143_);
        renderAirLevel(p_283143_);
    }

    private void renderHealthLevel(GuiGraphics p_283143_) {
        Player player = this.getCameraPlayer();
        if (player != null) {
            int i = Mth.ceil(player.getHealth());
            boolean flag = this.healthBlinkTime > (long)this.tickCount && (this.healthBlinkTime - (long)this.tickCount) / 3L % 2L == 1L;
            long j = Util.getMillis();
            if (i < this.lastHealth && player.invulnerableTime > 0) {
                this.lastHealthTime = j;
                this.healthBlinkTime = (long)(this.tickCount + 20);
            } else if (i > this.lastHealth && player.invulnerableTime > 0) {
                this.lastHealthTime = j;
                this.healthBlinkTime = (long)(this.tickCount + 10);
            }

            if (j - this.lastHealthTime > 1000L) {
                this.displayHealth = i;
                this.lastHealthTime = j;
            }

            this.lastHealth = i;
            int k = this.displayHealth;
            this.random.setSeed((long)(this.tickCount * 312871));
            int l = p_283143_.guiWidth() / 2 - 91;
            int i1 = p_283143_.guiWidth() / 2 + 91;
            int j1 = p_283143_.guiHeight() - leftHeight;
            float f = Math.max((float)player.getAttributeValue(Attributes.MAX_HEALTH), (float)Math.max(k, i));
            int k1 = Mth.ceil(player.getAbsorptionAmount());
            int l1 = Mth.ceil((f + (float)k1) / 2.0F / 10.0F);
            int i2 = Math.max(10 - (l1 - 2), 3);
            int j2 = j1 - 10;
            leftHeight += (l1 - 1) * i2 + 10;
            int k2 = -1;
            if (player.hasEffect(MobEffects.REGENERATION)) {
                k2 = this.tickCount % Mth.ceil(f + 5.0F);
            }
            Profiler.get().push("health");
            this.renderHearts(p_283143_, player, l, j1, i2, k2, f, i, k, k1, flag);
            Profiler.get().pop();
        }
    }

    private void renderArmorLevel(GuiGraphics p_283143_) {
        Player player = this.getCameraPlayer();
        if (player != null) {
            int l = p_283143_.guiWidth() / 2 - 91;
            Profiler.get().push("armor");
            renderArmor(p_283143_, player, p_283143_.guiHeight() - leftHeight + 10, 1, 0, l);
            Profiler.get().pop();
            if (player.getArmorValue() > 0) {
                leftHeight += 10;
            }
        }
    }

    private void renderFoodLevel(GuiGraphics p_283143_) {
        Player player = this.getCameraPlayer();
        if (player != null) {
            LivingEntity livingentity = this.getPlayerVehicleWithHealth();
            int l2 = this.getVehicleMaxHearts(livingentity);
            if (l2 == 0) {
                Profiler.get().push("food");
                int i1 = p_283143_.guiWidth() / 2 + 91;
                int j1 = p_283143_.guiHeight() - rightHeight;
                this.renderFood(p_283143_, player, j1, i1);
                rightHeight += 10;
                Profiler.get().pop();
            }
        }
    }

    private void renderAirLevel(GuiGraphics p_283143_) {
        Player player = this.getCameraPlayer();
        if (player != null) {
            int i1 = p_283143_.guiWidth() / 2 + 91;
            int j2 = p_283143_.guiHeight() - rightHeight;
            Profiler.get().push("air");
            this.renderAirBubbles(p_283143_, player, 10, j2, i1);
            Profiler.get().pop();
        }
    }

    private static void renderArmor(GuiGraphics p_335393_, Player p_335672_, int p_335452_, int p_335846_, int p_335778_, int p_335859_) {
        int i = p_335672_.getArmorValue();
        if (i > 0) {
            int j = p_335452_ - (p_335846_ - 1) * p_335778_ - 10;

            for (int k = 0; k < 10; k++) {
                int l = p_335859_ + k * 8;
                if (k * 2 + 1 < i) {
                    p_335393_.blitSprite(RenderType::guiTextured, ARMOR_FULL_SPRITE, l, j, 9, 9);
                }

                if (k * 2 + 1 == i) {
                    p_335393_.blitSprite(RenderType::guiTextured, ARMOR_HALF_SPRITE, l, j, 9, 9);
                }

                if (k * 2 + 1 > i) {
                    p_335393_.blitSprite(RenderType::guiTextured, ARMOR_EMPTY_SPRITE, l, j, 9, 9);
                }
            }
        }
    }

    private void renderHearts(
        GuiGraphics p_282497_,
        Player p_168690_,
        int p_168691_,
        int p_168692_,
        int p_168693_,
        int p_168694_,
        float p_168695_,
        int p_168696_,
        int p_168697_,
        int p_168698_,
        boolean p_168699_
    ) {
        Gui.HeartType gui$hearttype = Gui.HeartType.forPlayer(p_168690_);
        boolean flag = p_168690_.level().getLevelData().isHardcore();
        int i = Mth.ceil((double)p_168695_ / 2.0);
        int j = Mth.ceil((double)p_168698_ / 2.0);
        int k = i * 2;

        for (int l = i + j - 1; l >= 0; l--) {
            int i1 = l / 10;
            int j1 = l % 10;
            int k1 = p_168691_ + j1 * 8;
            int l1 = p_168692_ - i1 * p_168693_;
            if (p_168696_ + p_168698_ <= 4) {
                l1 += this.random.nextInt(2);
            }

            if (l < i && l == p_168694_) {
                l1 -= 2;
            }

            this.renderHeart(p_282497_, Gui.HeartType.CONTAINER, k1, l1, flag, p_168699_, false);
            int i2 = l * 2;
            boolean flag1 = l >= i;
            if (flag1) {
                int j2 = i2 - k;
                if (j2 < p_168698_) {
                    boolean flag2 = j2 + 1 == p_168698_;
                    this.renderHeart(p_282497_, gui$hearttype == Gui.HeartType.WITHERED ? gui$hearttype : Gui.HeartType.ABSORBING, k1, l1, flag, false, flag2);
                }
            }

            if (p_168699_ && i2 < p_168697_) {
                boolean flag3 = i2 + 1 == p_168697_;
                this.renderHeart(p_282497_, gui$hearttype, k1, l1, flag, true, flag3);
            }

            if (i2 < p_168696_) {
                boolean flag4 = i2 + 1 == p_168696_;
                this.renderHeart(p_282497_, gui$hearttype, k1, l1, flag, false, flag4);
            }
        }
    }

    private void renderHeart(
        GuiGraphics p_283024_, Gui.HeartType p_281393_, int p_283636_, int p_283279_, boolean p_283440_, boolean p_282496_, boolean p_294129_
    ) {
        p_283024_.blitSprite(RenderType::guiTextured, p_281393_.getSprite(p_283440_, p_294129_, p_282496_), p_283636_, p_283279_, 9, 9);
    }

    private void renderAirBubbles(GuiGraphics p_381066_, Player p_380979_, int p_380967_, int p_381100_, int p_381034_) {
        int i = p_380979_.getMaxAirSupply();
        int j = Math.clamp((long)p_380979_.getAirSupply(), 0, i);
        boolean flag = p_380979_.isEyeInFluid(FluidTags.WATER);
        if (flag || j < i) {
            p_381100_ = this.getAirBubbleYLine(p_380967_, p_381100_);
            int k = getCurrentAirSupplyBubble(j, i, -2);
            int l = getCurrentAirSupplyBubble(j, i, 0);
            int i1 = 10 - getCurrentAirSupplyBubble(j, i, getEmptyBubbleDelayDuration(j, flag));
            boolean flag1 = k != l;
            if (!flag) {
                this.lastBubblePopSoundPlayed = 0;
            }

            for (int j1 = 1; j1 <= 10; j1++) {
                int k1 = p_381034_ - (j1 - 1) * 8 - 9;
                if (j1 <= k) {
                    p_381066_.blitSprite(RenderType::guiTextured, AIR_SPRITE, k1, p_381100_, 9, 9);
                } else if (flag1 && j1 == l && flag) {
                    p_381066_.blitSprite(RenderType::guiTextured, AIR_POPPING_SPRITE, k1, p_381100_, 9, 9);
                    this.playAirBubblePoppedSound(j1, p_380979_, i1);
                } else if (j1 > 10 - i1) {
                    int l1 = i1 == 10 && this.tickCount % 2 == 0 ? this.random.nextInt(2) : 0;
                    p_381066_.blitSprite(RenderType::guiTextured, AIR_EMPTY_SPRITE, k1, p_381100_ + l1, 9, 9);
                }
            }

            rightHeight += 10;
        }
    }

    private int getAirBubbleYLine(int p_381118_, int p_380948_) {
        int i = this.getVisibleVehicleHeartRows(p_381118_) - 1;
        return p_380948_ - i * 10;
    }

    private static int getCurrentAirSupplyBubble(int p_381135_, int p_381127_, int p_381060_) {
        return Mth.ceil((float)((p_381135_ + p_381060_) * 10) / (float)p_381127_);
    }

    private static int getEmptyBubbleDelayDuration(int p_381083_, boolean p_381137_) {
        return p_381083_ != 0 && p_381137_ ? 1 : 0;
    }

    private void playAirBubblePoppedSound(int p_381005_, Player p_380939_, int p_381115_) {
        if (this.lastBubblePopSoundPlayed != p_381005_) {
            float f = 0.5F + 0.1F * (float)Math.max(0, p_381115_ - 3 + 1);
            float f1 = 1.0F + 0.1F * (float)Math.max(0, p_381115_ - 5 + 1);
            p_380939_.playSound(SoundEvents.BUBBLE_POP, f, f1);
            this.lastBubblePopSoundPlayed = p_381005_;
        }
    }

    private void renderFood(GuiGraphics p_335615_, Player p_336082_, int p_335399_, int p_335589_) {
        FoodData fooddata = p_336082_.getFoodData();
        int i = fooddata.getFoodLevel();

        for (int j = 0; j < 10; j++) {
            int k = p_335399_;
            ResourceLocation resourcelocation;
            ResourceLocation resourcelocation1;
            ResourceLocation resourcelocation2;
            if (p_336082_.hasEffect(MobEffects.HUNGER)) {
                resourcelocation = FOOD_EMPTY_HUNGER_SPRITE;
                resourcelocation1 = FOOD_HALF_HUNGER_SPRITE;
                resourcelocation2 = FOOD_FULL_HUNGER_SPRITE;
            } else {
                resourcelocation = FOOD_EMPTY_SPRITE;
                resourcelocation1 = FOOD_HALF_SPRITE;
                resourcelocation2 = FOOD_FULL_SPRITE;
            }

            if (p_336082_.getFoodData().getSaturationLevel() <= 0.0F && this.tickCount % (i * 3 + 1) == 0) {
                k = p_335399_ + (this.random.nextInt(3) - 1);
            }

            int l = p_335589_ - j * 8 - 9;
            p_335615_.blitSprite(RenderType::guiTextured, resourcelocation, l, k, 9, 9);
            if (j * 2 + 1 < i) {
                p_335615_.blitSprite(RenderType::guiTextured, resourcelocation2, l, k, 9, 9);
            }

            if (j * 2 + 1 == i) {
                p_335615_.blitSprite(RenderType::guiTextured, resourcelocation1, l, k, 9, 9);
            }
        }
    }

    private void renderVehicleHealth(GuiGraphics p_283368_) {
        LivingEntity livingentity = this.getPlayerVehicleWithHealth();
        if (livingentity != null) {
            int i = this.getVehicleMaxHearts(livingentity);
            if (i != 0) {
                int j = (int)Math.ceil((double)livingentity.getHealth());
                Profiler.get().popPush("mountHealth");
                int k = p_283368_.guiHeight() - rightHeight;
                int l = p_283368_.guiWidth() / 2 + 91;
                int i1 = k;

                for (int j1 = 0; i > 0; j1 += 20) {
                    int k1 = Math.min(i, 10);
                    i -= k1;

                    for (int l1 = 0; l1 < k1; l1++) {
                        int i2 = l - l1 * 8 - 9;
                        p_283368_.blitSprite(RenderType::guiTextured, HEART_VEHICLE_CONTAINER_SPRITE, i2, i1, 9, 9);
                        if (l1 * 2 + 1 + j1 < j) {
                            p_283368_.blitSprite(RenderType::guiTextured, HEART_VEHICLE_FULL_SPRITE, i2, i1, 9, 9);
                        }

                        if (l1 * 2 + 1 + j1 == j) {
                            p_283368_.blitSprite(RenderType::guiTextured, HEART_VEHICLE_HALF_SPRITE, i2, i1, 9, 9);
                        }
                    }

                    i1 -= 10;
                    rightHeight += 10;
                }
            }
        }
    }

    private void renderTextureOverlay(GuiGraphics p_282304_, ResourceLocation p_281622_, float p_281504_) {
        int i = ARGB.white(p_281504_);
        p_282304_.blit(
            RenderType::guiTexturedOverlay,
            p_281622_,
            0,
            0,
            0.0F,
            0.0F,
            p_282304_.guiWidth(),
            p_282304_.guiHeight(),
            p_282304_.guiWidth(),
            p_282304_.guiHeight(),
            i
        );
    }

    private void renderSpyglassOverlay(GuiGraphics p_282069_, float p_283442_) {
        float f = (float)Math.min(p_282069_.guiWidth(), p_282069_.guiHeight());
        float f1 = Math.min((float)p_282069_.guiWidth() / f, (float)p_282069_.guiHeight() / f) * p_283442_;
        int i = Mth.floor(f * f1);
        int j = Mth.floor(f * f1);
        int k = (p_282069_.guiWidth() - i) / 2;
        int l = (p_282069_.guiHeight() - j) / 2;
        int i1 = k + i;
        int j1 = l + j;
        p_282069_.blit(RenderType::guiTextured, SPYGLASS_SCOPE_LOCATION, k, l, 0.0F, 0.0F, i, j, i, j);
        p_282069_.fill(RenderType.guiOverlay(), 0, j1, p_282069_.guiWidth(), p_282069_.guiHeight(), -90, -16777216);
        p_282069_.fill(RenderType.guiOverlay(), 0, 0, p_282069_.guiWidth(), l, -90, -16777216);
        p_282069_.fill(RenderType.guiOverlay(), 0, l, k, j1, -90, -16777216);
        p_282069_.fill(RenderType.guiOverlay(), i1, l, p_282069_.guiWidth(), j1, -90, -16777216);
    }

    private void updateVignetteBrightness(Entity p_93021_) {
        BlockPos blockpos = BlockPos.containing(p_93021_.getX(), p_93021_.getEyeY(), p_93021_.getZ());
        float f = LightTexture.getBrightness(p_93021_.level().dimensionType(), p_93021_.level().getMaxLocalRawBrightness(blockpos));
        float f1 = Mth.clamp(1.0F - f, 0.0F, 1.0F);
        this.vignetteBrightness = this.vignetteBrightness + (f1 - this.vignetteBrightness) * 0.01F;
    }

    private void renderVignette(GuiGraphics p_283063_, @Nullable Entity p_283439_) {
        WorldBorder worldborder = this.minecraft.level.getWorldBorder();
        float f = 0.0F;
        if (p_283439_ != null) {
            float f1 = (float)worldborder.getDistanceToBorder(p_283439_);
            double d0 = Math.min(
                worldborder.getLerpSpeed() * (double)worldborder.getWarningTime() * 1000.0, Math.abs(worldborder.getLerpTarget() - worldborder.getSize())
            );
            double d1 = Math.max((double)worldborder.getWarningBlocks(), d0);
            if ((double)f1 < d1) {
                f = 1.0F - (float)((double)f1 / d1);
            }
        }

        int i;
        if (f > 0.0F) {
            f = Mth.clamp(f, 0.0F, 1.0F);
            i = ARGB.colorFromFloat(1.0F, 0.0F, f, f);
        } else {
            float f2 = this.vignetteBrightness;
            f2 = Mth.clamp(f2, 0.0F, 1.0F);
            i = ARGB.colorFromFloat(1.0F, f2, f2, f2);
        }

        p_283063_.blit(
            RenderType::vignette,
            VIGNETTE_LOCATION,
            0,
            0,
            0.0F,
            0.0F,
            p_283063_.guiWidth(),
            p_283063_.guiHeight(),
            p_283063_.guiWidth(),
            p_283063_.guiHeight(),
            i
        );
    }

    private void renderPortalOverlay(GuiGraphics p_283375_, float p_283296_) {
        if (p_283296_ < 1.0F) {
            p_283296_ *= p_283296_;
            p_283296_ *= p_283296_;
            p_283296_ = p_283296_ * 0.8F + 0.2F;
        }

        int i = ARGB.white(p_283296_);
        TextureAtlasSprite textureatlassprite = this.minecraft
            .getBlockRenderer()
            .getBlockModelShaper()
            .getParticleIcon(Blocks.NETHER_PORTAL.defaultBlockState());
        p_283375_.blitSprite(RenderType::guiTexturedOverlay, textureatlassprite, 0, 0, p_283375_.guiWidth(), p_283375_.guiHeight(), i);
    }

    private void renderConfusionOverlay(GuiGraphics p_361440_, float p_363322_) {
        int i = p_361440_.guiWidth();
        int j = p_361440_.guiHeight();
        p_361440_.pose().pushPose();
        float f = Mth.lerp(p_363322_, 2.0F, 1.0F);
        p_361440_.pose().translate((float)i / 2.0F, (float)j / 2.0F, 0.0F);
        p_361440_.pose().scale(f, f, f);
        p_361440_.pose().translate((float)(-i) / 2.0F, (float)(-j) / 2.0F, 0.0F);
        float f1 = 0.2F * p_363322_;
        float f2 = 0.4F * p_363322_;
        float f3 = 0.2F * p_363322_;
        p_361440_.blit(p_359082_ -> RenderType.guiNauseaOverlay(), NAUSEA_LOCATION, 0, 0, 0.0F, 0.0F, i, j, i, j, ARGB.colorFromFloat(1.0F, f1, f2, f3));
        p_361440_.pose().popPose();
    }

    private void renderSlot(GuiGraphics p_283283_, int p_283213_, int p_281301_, DeltaTracker p_348541_, Player p_283644_, ItemStack p_283317_, int p_283261_) {
        if (!p_283317_.isEmpty()) {
            float f = (float)p_283317_.getPopTime() - p_348541_.getGameTimeDeltaPartialTick(false);
            if (f > 0.0F) {
                float f1 = 1.0F + f / 5.0F;
                p_283283_.pose().pushPose();
                p_283283_.pose().translate((float)(p_283213_ + 8), (float)(p_281301_ + 12), 0.0F);
                p_283283_.pose().scale(1.0F / f1, (f1 + 1.0F) / 2.0F, 1.0F);
                p_283283_.pose().translate((float)(-(p_283213_ + 8)), (float)(-(p_281301_ + 12)), 0.0F);
            }

            p_283283_.renderItem(p_283644_, p_283317_, p_283213_, p_281301_, p_283261_);
            if (f > 0.0F) {
                p_283283_.pose().popPose();
            }

            p_283283_.renderItemDecorations(this.minecraft.font, p_283317_, p_283213_, p_281301_);
        }
    }

    public void tick(boolean p_193833_) {
        this.tickAutosaveIndicator();
        if (!p_193833_) {
            this.tick();
        }
    }

    private void tick() {
        if (this.overlayMessageTime > 0) {
            this.overlayMessageTime--;
        }

        if (this.titleTime > 0) {
            this.titleTime--;
            if (this.titleTime <= 0) {
                this.title = null;
                this.subtitle = null;
            }
        }

        this.tickCount++;
        Entity entity = this.minecraft.getCameraEntity();
        if (entity != null) {
            this.updateVignetteBrightness(entity);
        }

        if (this.minecraft.player != null) {
            ItemStack itemstack = this.minecraft.player.getInventory().getSelected();
            if (itemstack.isEmpty()) {
                this.toolHighlightTimer = 0;
            } else if (this.lastToolHighlight.isEmpty()
                || !itemstack.is(this.lastToolHighlight.getItem())
                || (!itemstack.getHoverName().equals(this.lastToolHighlight.getHoverName()) || !itemstack.getHighlightTip(itemstack.getHoverName()).equals(this.lastToolHighlight.getHighlightTip(this.lastToolHighlight.getHoverName())))) {
                this.toolHighlightTimer = (int)(40.0 * this.minecraft.options.notificationDisplayTime().get());
            } else if (this.toolHighlightTimer > 0) {
                this.toolHighlightTimer--;
            }

            this.lastToolHighlight = itemstack;
        }

        this.chat.tick();
    }

    private void tickAutosaveIndicator() {
        MinecraftServer minecraftserver = this.minecraft.getSingleplayerServer();
        boolean flag = minecraftserver != null && minecraftserver.isCurrentlySaving();
        this.lastAutosaveIndicatorValue = this.autosaveIndicatorValue;
        this.autosaveIndicatorValue = Mth.lerp(0.2F, this.autosaveIndicatorValue, flag ? 1.0F : 0.0F);
    }

    public void setNowPlaying(Component p_93056_) {
        Component component = Component.translatable("record.nowPlaying", p_93056_);
        this.setOverlayMessage(component, true);
        this.minecraft.getNarrator().sayNow(component);
    }

    public void setOverlayMessage(Component p_93064_, boolean p_93065_) {
        this.setChatDisabledByPlayerShown(false);
        this.overlayMessageString = p_93064_;
        this.overlayMessageTime = 60;
        this.animateOverlayMessageColor = p_93065_;
    }

    public void setChatDisabledByPlayerShown(boolean p_238398_) {
        this.chatDisabledByPlayerShown = p_238398_;
    }

    public boolean isShowingChatDisabledByPlayer() {
        return this.chatDisabledByPlayerShown && this.overlayMessageTime > 0;
    }

    public void setTimes(int p_168685_, int p_168686_, int p_168687_) {
        if (p_168685_ >= 0) {
            this.titleFadeInTime = p_168685_;
        }

        if (p_168686_ >= 0) {
            this.titleStayTime = p_168686_;
        }

        if (p_168687_ >= 0) {
            this.titleFadeOutTime = p_168687_;
        }

        if (this.titleTime > 0) {
            this.titleTime = this.titleFadeInTime + this.titleStayTime + this.titleFadeOutTime;
        }
    }

    public void setSubtitle(Component p_168712_) {
        this.subtitle = p_168712_;
    }

    public void setTitle(Component p_168715_) {
        this.title = p_168715_;
        this.titleTime = this.titleFadeInTime + this.titleStayTime + this.titleFadeOutTime;
    }

    public void clearTitles() {
        this.title = null;
        this.subtitle = null;
        this.titleTime = 0;
    }

    public ChatComponent getChat() {
        return this.chat;
    }

    public int getGuiTicks() {
        return this.tickCount;
    }

    public Font getFont() {
        return this.minecraft.font;
    }

    public SpectatorGui getSpectatorGui() {
        return this.spectatorGui;
    }

    public PlayerTabOverlay getTabList() {
        return this.tabList;
    }

    public void onDisconnected() {
        this.tabList.reset();
        this.bossOverlay.reset();
        this.minecraft.getToastManager().clear();
        this.debugOverlay.reset();
        this.chat.clearMessages(true);
        this.clearTitles();
        this.resetTitleTimes();
    }

    public BossHealthOverlay getBossOverlay() {
        return this.bossOverlay;
    }

    public DebugScreenOverlay getDebugOverlay() {
        return this.debugOverlay;
    }

    public void clearCache() {
        this.debugOverlay.clearChunkCache();
    }

    public void renderSavingIndicator(GuiGraphics p_282761_, DeltaTracker p_348592_) {
        if (this.minecraft.options.showAutosaveIndicator().get() && (this.autosaveIndicatorValue > 0.0F || this.lastAutosaveIndicatorValue > 0.0F)) {
            int i = Mth.floor(
                255.0F * Mth.clamp(Mth.lerp(p_348592_.getRealtimeDeltaTicks(), this.lastAutosaveIndicatorValue, this.autosaveIndicatorValue), 0.0F, 1.0F)
            );
            if (i > 8) {
                Font font = this.getFont();
                int j = font.width(SAVING_TEXT);
                int k = ARGB.color(i, -1);
                int l = p_282761_.guiWidth() - j - 5;
                int i1 = p_282761_.guiHeight() - 9 - 5;
                p_282761_.drawStringWithBackdrop(font, SAVING_TEXT, l, i1, j, k);
            }
        }
    }

    @org.jetbrains.annotations.ApiStatus.Internal
    public void initModdedOverlays() {
        this.layerManager.initModdedLayers();
    }

    public int getLayerCount() {
        return this.layerManager.getLayerCount();
    }

    @OnlyIn(Dist.CLIENT)
    public static enum HeartType implements net.neoforged.fml.common.asm.enumextension.IExtensibleEnum {
        CONTAINER(
            ResourceLocation.withDefaultNamespace("hud/heart/container"),
            ResourceLocation.withDefaultNamespace("hud/heart/container_blinking"),
            ResourceLocation.withDefaultNamespace("hud/heart/container"),
            ResourceLocation.withDefaultNamespace("hud/heart/container_blinking"),
            ResourceLocation.withDefaultNamespace("hud/heart/container_hardcore"),
            ResourceLocation.withDefaultNamespace("hud/heart/container_hardcore_blinking"),
            ResourceLocation.withDefaultNamespace("hud/heart/container_hardcore"),
            ResourceLocation.withDefaultNamespace("hud/heart/container_hardcore_blinking")
        ),
        NORMAL(
            ResourceLocation.withDefaultNamespace("hud/heart/full"),
            ResourceLocation.withDefaultNamespace("hud/heart/full_blinking"),
            ResourceLocation.withDefaultNamespace("hud/heart/half"),
            ResourceLocation.withDefaultNamespace("hud/heart/half_blinking"),
            ResourceLocation.withDefaultNamespace("hud/heart/hardcore_full"),
            ResourceLocation.withDefaultNamespace("hud/heart/hardcore_full_blinking"),
            ResourceLocation.withDefaultNamespace("hud/heart/hardcore_half"),
            ResourceLocation.withDefaultNamespace("hud/heart/hardcore_half_blinking")
        ),
        POISIONED(
            ResourceLocation.withDefaultNamespace("hud/heart/poisoned_full"),
            ResourceLocation.withDefaultNamespace("hud/heart/poisoned_full_blinking"),
            ResourceLocation.withDefaultNamespace("hud/heart/poisoned_half"),
            ResourceLocation.withDefaultNamespace("hud/heart/poisoned_half_blinking"),
            ResourceLocation.withDefaultNamespace("hud/heart/poisoned_hardcore_full"),
            ResourceLocation.withDefaultNamespace("hud/heart/poisoned_hardcore_full_blinking"),
            ResourceLocation.withDefaultNamespace("hud/heart/poisoned_hardcore_half"),
            ResourceLocation.withDefaultNamespace("hud/heart/poisoned_hardcore_half_blinking")
        ),
        WITHERED(
            ResourceLocation.withDefaultNamespace("hud/heart/withered_full"),
            ResourceLocation.withDefaultNamespace("hud/heart/withered_full_blinking"),
            ResourceLocation.withDefaultNamespace("hud/heart/withered_half"),
            ResourceLocation.withDefaultNamespace("hud/heart/withered_half_blinking"),
            ResourceLocation.withDefaultNamespace("hud/heart/withered_hardcore_full"),
            ResourceLocation.withDefaultNamespace("hud/heart/withered_hardcore_full_blinking"),
            ResourceLocation.withDefaultNamespace("hud/heart/withered_hardcore_half"),
            ResourceLocation.withDefaultNamespace("hud/heart/withered_hardcore_half_blinking")
        ),
        ABSORBING(
            ResourceLocation.withDefaultNamespace("hud/heart/absorbing_full"),
            ResourceLocation.withDefaultNamespace("hud/heart/absorbing_full_blinking"),
            ResourceLocation.withDefaultNamespace("hud/heart/absorbing_half"),
            ResourceLocation.withDefaultNamespace("hud/heart/absorbing_half_blinking"),
            ResourceLocation.withDefaultNamespace("hud/heart/absorbing_hardcore_full"),
            ResourceLocation.withDefaultNamespace("hud/heart/absorbing_hardcore_full_blinking"),
            ResourceLocation.withDefaultNamespace("hud/heart/absorbing_hardcore_half"),
            ResourceLocation.withDefaultNamespace("hud/heart/absorbing_hardcore_half_blinking")
        ),
        FROZEN(
            ResourceLocation.withDefaultNamespace("hud/heart/frozen_full"),
            ResourceLocation.withDefaultNamespace("hud/heart/frozen_full_blinking"),
            ResourceLocation.withDefaultNamespace("hud/heart/frozen_half"),
            ResourceLocation.withDefaultNamespace("hud/heart/frozen_half_blinking"),
            ResourceLocation.withDefaultNamespace("hud/heart/frozen_hardcore_full"),
            ResourceLocation.withDefaultNamespace("hud/heart/frozen_hardcore_full_blinking"),
            ResourceLocation.withDefaultNamespace("hud/heart/frozen_hardcore_half"),
            ResourceLocation.withDefaultNamespace("hud/heart/frozen_hardcore_half_blinking")
        );

        private final ResourceLocation full;
        private final ResourceLocation fullBlinking;
        private final ResourceLocation half;
        private final ResourceLocation halfBlinking;
        private final ResourceLocation hardcoreFull;
        private final ResourceLocation hardcoreFullBlinking;
        private final ResourceLocation hardcoreHalf;
        private final ResourceLocation hardcoreHalfBlinking;

        private HeartType(
            ResourceLocation p_294435_,
            ResourceLocation p_294438_,
            ResourceLocation p_295036_,
            ResourceLocation p_295439_,
            ResourceLocation p_296249_,
            ResourceLocation p_295479_,
            ResourceLocation p_296219_,
            ResourceLocation p_296437_
        ) {
            this.full = p_294435_;
            this.fullBlinking = p_294438_;
            this.half = p_295036_;
            this.halfBlinking = p_295439_;
            this.hardcoreFull = p_296249_;
            this.hardcoreFullBlinking = p_295479_;
            this.hardcoreHalf = p_296219_;
            this.hardcoreHalfBlinking = p_296437_;
        }

        public ResourceLocation getSprite(boolean p_295909_, boolean p_295387_, boolean p_294486_) {
            if (!p_295909_) {
                if (p_295387_) {
                    return p_294486_ ? this.halfBlinking : this.half;
                } else {
                    return p_294486_ ? this.fullBlinking : this.full;
                }
            } else if (p_295387_) {
                return p_294486_ ? this.hardcoreHalfBlinking : this.hardcoreHalf;
            } else {
                return p_294486_ ? this.hardcoreFullBlinking : this.hardcoreFull;
            }
        }

        static Gui.HeartType forPlayer(Player p_168733_) {
            Gui.HeartType gui$hearttype;
            if (p_168733_.hasEffect(MobEffects.POISON)) {
                gui$hearttype = POISIONED;
            } else if (p_168733_.hasEffect(MobEffects.WITHER)) {
                gui$hearttype = WITHERED;
            } else if (p_168733_.isFullyFrozen()) {
                gui$hearttype = FROZEN;
            } else {
                gui$hearttype = NORMAL;
            }
            gui$hearttype = net.neoforged.neoforge.event.EventHooks.firePlayerHeartTypeEvent(p_168733_, gui$hearttype);

            return gui$hearttype;
        }

        public static net.neoforged.fml.common.asm.enumextension.ExtensionInfo getExtensionInfo() {
            return net.neoforged.fml.common.asm.enumextension.ExtensionInfo.nonExtended(Gui.HeartType.class);
        }
    }
}
