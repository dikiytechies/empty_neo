package net.minecraft.server;

import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.Commands;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.Registry;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleReloadInstance;
import net.minecraft.util.Unit;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.crafting.RecipeManager;
import org.slf4j.Logger;

public class ReloadableServerResources {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final CompletableFuture<Unit> DATA_RELOAD_INITIAL_TASK = CompletableFuture.completedFuture(Unit.INSTANCE);
    private final ReloadableServerRegistries.Holder fullRegistryHolder;
    private final Commands commands;
    private final RecipeManager recipes;
    private final ServerAdvancementManager advancements;
    private final ServerFunctionLibrary functionLibrary;
    private final List<Registry.PendingTags<?>> postponedTags;

    private ReloadableServerResources(
        LayeredRegistryAccess<RegistryLayer> p_362982_,
        HolderLookup.Provider p_361583_,
        FeatureFlagSet p_250695_,
        Commands.CommandSelection p_206858_,
        List<Registry.PendingTags<?>> p_362624_,
        int p_206859_
    ) {
        this.fullRegistryHolder = new ReloadableServerRegistries.Holder(p_362982_.compositeAccess());
        this.postponedTags = p_362624_;
        this.recipes = new RecipeManager(p_361583_);
        this.commands = new Commands(p_206858_, CommandBuildContext.simple(p_361583_, p_250695_));
        this.advancements = new ServerAdvancementManager(p_361583_);
        this.functionLibrary = new ServerFunctionLibrary(p_206859_, this.commands.getDispatcher());
        // Neo: Store registries and create context object
        this.registryLookup = p_361583_;
        this.context = new net.neoforged.neoforge.common.conditions.ConditionContext(this.postponedTags, p_362982_.compositeAccess(), p_250695_);
    }

    public ServerFunctionLibrary getFunctionLibrary() {
        return this.functionLibrary;
    }

    public ReloadableServerRegistries.Holder fullRegistries() {
        return this.fullRegistryHolder;
    }

    public RecipeManager getRecipeManager() {
        return this.recipes;
    }

    public Commands getCommands() {
        return this.commands;
    }

    public ServerAdvancementManager getAdvancements() {
        return this.advancements;
    }

    public List<PreparableReloadListener> listeners() {
        return List.of(this.recipes, this.functionLibrary, this.advancements);
    }

    private final HolderLookup.Provider registryLookup;
    private final net.neoforged.neoforge.common.conditions.ConditionContext context;

    /**
     * Exposes the current condition context for usage in other reload listeners.<br>
     * This is not useful outside the reloading stage.
     * @return The condition context for the currently active reload.
     */
    public net.neoforged.neoforge.common.conditions.ICondition.IContext getConditionContext() {
        return this.context;
    }

    /**
      * {@return the lookup provider access for the currently active reload}
      */
    public HolderLookup.Provider getRegistryLookup() {
        return this.registryLookup;
    }

    public static CompletableFuture<ReloadableServerResources> loadResources(
        ResourceManager p_248588_,
        LayeredRegistryAccess<RegistryLayer> p_335667_,
        List<Registry.PendingTags<?>> p_363739_,
        FeatureFlagSet p_250212_,
        Commands.CommandSelection p_249301_,
        int p_251126_,
        Executor p_249136_,
        Executor p_249601_
    ) {
        return ReloadableServerRegistries.reload(p_335667_, p_363739_, p_248588_, p_249136_)
            .thenCompose(
                p_359514_ -> {
                    ReloadableServerResources reloadableserverresources = new ReloadableServerResources(
                        p_359514_.layers(), p_359514_.lookupWithUpdatedTags(), p_250212_, p_249301_, p_363739_, p_251126_
                    );

                    // Neo: Fire the AddServerReloadListenersEvent and use the resulting listeners instead of the vanilla listener list.
                    List<PreparableReloadListener> listeners = net.neoforged.neoforge.event.EventHooks.onResourceReload(reloadableserverresources, p_359514_.layers().compositeAccess());

                    // Neo: Inject the ConditionContext and RegistryLookup to any resource listener that requests it.
                    for (PreparableReloadListener rl : listeners) {
                        if (rl instanceof net.neoforged.neoforge.resource.ContextAwareReloadListener carl) {
                            carl.injectContext(reloadableserverresources.context, reloadableserverresources.registryLookup);
                        }
                    }

                    return SimpleReloadInstance.create(
                            p_248588_, listeners, p_249136_, p_249601_, DATA_RELOAD_INITIAL_TASK, LOGGER.isDebugEnabled()
                        )
                        .done()
                        .thenRun(() -> {
                            // Neo: Clear context after reload completes
                            reloadableserverresources.context.clear();
                            listeners.forEach(rl -> {
                                if (rl instanceof net.neoforged.neoforge.resource.ContextAwareReloadListener srl) {
                                    srl.injectContext(net.neoforged.neoforge.common.conditions.ICondition.IContext.EMPTY, net.minecraft.core.RegistryAccess.EMPTY);
                                }
                            });
                        })
                        .thenApply(p_214306_ -> reloadableserverresources);
                }
            );
    }

    public void updateStaticRegistryTags() {
        this.postponedTags.forEach(Registry.PendingTags::apply);
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(new net.neoforged.neoforge.event.TagsUpdatedEvent(registryLookup, false, false));
    }
}
