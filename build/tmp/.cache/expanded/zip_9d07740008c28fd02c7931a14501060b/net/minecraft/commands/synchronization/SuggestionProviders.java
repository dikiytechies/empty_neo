package net.minecraft.commands.synchronization;

import com.google.common.collect.Maps;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

public class SuggestionProviders {
    private static final Map<ResourceLocation, SuggestionProvider<SharedSuggestionProvider>> PROVIDERS_BY_NAME = Maps.newHashMap();
    private static final ResourceLocation DEFAULT_NAME = ResourceLocation.withDefaultNamespace("ask_server");
    public static final SuggestionProvider<SharedSuggestionProvider> ASK_SERVER = register(
        DEFAULT_NAME, (p_121673_, p_121674_) -> p_121673_.getSource().customSuggestion(p_121673_)
    );
    public static final SuggestionProvider<CommandSourceStack> AVAILABLE_SOUNDS = register(
        ResourceLocation.withDefaultNamespace("available_sounds"),
        (p_121667_, p_121668_) -> SharedSuggestionProvider.suggestResource(p_121667_.getSource().getAvailableSounds(), p_121668_)
    );
    public static final SuggestionProvider<CommandSourceStack> SUMMONABLE_ENTITIES = register(
        ResourceLocation.withDefaultNamespace("summonable_entities"),
        (p_359328_, p_359329_) -> SharedSuggestionProvider.suggestResource(
                BuiltInRegistries.ENTITY_TYPE
                    .stream()
                    .filter(p_247987_ -> p_247987_.isEnabled(p_359328_.getSource().enabledFeatures()) && p_247987_.canSummon()),
                p_359329_,
                EntityType::getKey,
                p_212436_ -> Component.translatable(Util.makeDescriptionId("entity", EntityType.getKey(p_212436_)))
            )
    );

    public static <S extends SharedSuggestionProvider> SuggestionProvider<S> register(
        ResourceLocation p_121659_, SuggestionProvider<SharedSuggestionProvider> p_121660_
    ) {
        if (PROVIDERS_BY_NAME.containsKey(p_121659_)) {
            throw new IllegalArgumentException("A command suggestion provider is already registered with the name " + p_121659_);
        } else {
            PROVIDERS_BY_NAME.put(p_121659_, p_121660_);
            return (SuggestionProvider<S>)new SuggestionProviders.Wrapper(p_121659_, p_121660_);
        }
    }

    public static SuggestionProvider<SharedSuggestionProvider> getProvider(ResourceLocation p_121657_) {
        return PROVIDERS_BY_NAME.getOrDefault(p_121657_, ASK_SERVER);
    }

    public static ResourceLocation getName(SuggestionProvider<SharedSuggestionProvider> p_121655_) {
        return p_121655_ instanceof SuggestionProviders.Wrapper ? ((SuggestionProviders.Wrapper)p_121655_).name : DEFAULT_NAME;
    }

    public static SuggestionProvider<SharedSuggestionProvider> safelySwap(SuggestionProvider<SharedSuggestionProvider> p_121665_) {
        return p_121665_ instanceof SuggestionProviders.Wrapper ? p_121665_ : ASK_SERVER;
    }

    protected static class Wrapper implements SuggestionProvider<SharedSuggestionProvider> {
        private final SuggestionProvider<SharedSuggestionProvider> delegate;
        final ResourceLocation name;

        public Wrapper(ResourceLocation p_121678_, SuggestionProvider<SharedSuggestionProvider> p_121679_) {
            this.delegate = p_121679_;
            this.name = p_121678_;
        }

        @Override
        public CompletableFuture<Suggestions> getSuggestions(CommandContext<SharedSuggestionProvider> p_121683_, SuggestionsBuilder p_121684_) throws CommandSyntaxException {
            return this.delegate.getSuggestions(p_121683_, p_121684_);
        }
    }
}
