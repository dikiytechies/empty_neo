package net.minecraft.data;

import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonWriter;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import org.slf4j.Logger;

public interface DataProvider {
    /**
     * Neo: Allows changing the indentation width used by {@link #saveStable}.
     */
    java.util.concurrent.atomic.AtomicInteger INDENT_WIDTH = new java.util.concurrent.atomic.AtomicInteger(2);

    ToIntFunction<String> FIXED_ORDER_FIELDS = Util.make(new Object2IntOpenHashMap<>(), p_236070_ -> {
        // Neo: conditions go first
        p_236070_.put("neoforge:conditions", -1);
        p_236070_.put("neoforge:ingredient_type", 0);
        p_236070_.put("type", 0);
        p_236070_.put("parent", 1);
        p_236070_.defaultReturnValue(2);
    });
    Comparator<String> KEY_COMPARATOR = Comparator.comparingInt(FIXED_ORDER_FIELDS).thenComparing(p_236077_ -> (String)p_236077_);
    Logger LOGGER = LogUtils.getLogger();

    CompletableFuture<?> run(CachedOutput p_236071_);

    String getName();

    static <T> CompletableFuture<?> saveAll(CachedOutput p_371210_, Codec<T> p_371684_, PackOutput.PathProvider p_371226_, Map<ResourceLocation, T> p_371316_) {
        return saveAll(p_371210_, p_371684_, p_371226_::json, p_371316_);
    }

    static <T, E> CompletableFuture<?> saveAll(CachedOutput p_387918_, Codec<E> p_387221_, Function<T, Path> p_386916_, Map<T, E> p_388894_) {
        return saveAll(p_387918_, p_386289_ -> p_387221_.encodeStart(JsonOps.INSTANCE, (E)p_386289_).getOrThrow(), p_386916_, p_388894_);
    }

    static <T, E> CompletableFuture<?> saveAll(CachedOutput p_388708_, Function<E, JsonElement> p_387101_, Function<T, Path> p_386894_, Map<T, E> p_388538_) {
        return CompletableFuture.allOf(p_388538_.entrySet().stream().map(p_386293_ -> {
            Path path = p_386894_.apply(p_386293_.getKey());
            JsonElement jsonelement = p_387101_.apply(p_386293_.getValue());
            return saveStable(p_388708_, jsonelement, path);
        }).toArray(CompletableFuture[]::new));
    }

    static <T> CompletableFuture<?> saveStable(CachedOutput p_298323_, HolderLookup.Provider p_323556_, Codec<T> p_299231_, T p_298793_, Path p_298236_) {
        RegistryOps<JsonElement> registryops = p_323556_.createSerializationContext(JsonOps.INSTANCE);
        return saveStable(p_298323_, registryops, p_299231_, p_298793_, p_298236_);
    }

    static <T> CompletableFuture<?> saveStable(CachedOutput p_371223_, Codec<T> p_371552_, T p_371267_, Path p_371229_) {
        return saveStable(p_371223_, JsonOps.INSTANCE, p_371552_, p_371267_, p_371229_);
    }

    private static <T> CompletableFuture<?> saveStable(
        CachedOutput p_371845_, DynamicOps<JsonElement> p_371225_, Codec<T> p_371373_, T p_371681_, Path p_371818_
    ) {
        JsonElement jsonelement = p_371373_.encodeStart(p_371225_, p_371681_).getOrThrow();
        return saveStable(p_371845_, jsonelement, p_371818_);
    }

    static CompletableFuture<?> saveStable(CachedOutput p_253653_, JsonElement p_254542_, Path p_254467_) {
        return CompletableFuture.runAsync(() -> {
            try {
                ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
                HashingOutputStream hashingoutputstream = new HashingOutputStream(Hashing.sha1(), bytearrayoutputstream);

                try (JsonWriter jsonwriter = new JsonWriter(new OutputStreamWriter(hashingoutputstream, StandardCharsets.UTF_8))) {
                    jsonwriter.setSerializeNulls(false);
                    jsonwriter.setIndent(" ".repeat(java.lang.Math.max(0, INDENT_WIDTH.get()))); // Neo: Allow changing the indent width without needing to mixin this lambda.
                    GsonHelper.writeValue(jsonwriter, p_254542_, KEY_COMPARATOR);
                }

                p_253653_.writeIfNeeded(p_254467_, bytearrayoutputstream.toByteArray(), hashingoutputstream.hash());
            } catch (IOException ioexception) {
                LOGGER.error("Failed to save file to {}", p_254467_, ioexception);
            }
        }, Util.backgroundExecutor().forName("saveStable"));
    }

    @FunctionalInterface
    public interface Factory<T extends DataProvider> {
        T create(PackOutput p_253851_);
    }
}
