package net.minecraft.util.profiling;

import com.mojang.jtracy.TracyClient;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public final class Profiler {
    private static final ThreadLocal<TracyZoneFiller> TRACY_FILLER = ThreadLocal.withInitial(TracyZoneFiller::new);
    private static final ThreadLocal<ProfilerFiller> ACTIVE = new ThreadLocal<>();
    private static final AtomicInteger ACTIVE_COUNT = new AtomicInteger();

    private Profiler() {
    }

    public static Profiler.Scope use(ProfilerFiller p_373040_) {
        startUsing(p_373040_);
        return Profiler::stopUsing;
    }

    private static void startUsing(ProfilerFiller p_373003_) {
        if (ACTIVE.get() != null) {
            throw new IllegalStateException("Profiler is already active");
        } else {
            ProfilerFiller profilerfiller = decorateFiller(p_373003_);
            ACTIVE.set(profilerfiller);
            ACTIVE_COUNT.incrementAndGet();
            profilerfiller.startTick();
        }
    }

    private static void stopUsing() {
        ProfilerFiller profilerfiller = ACTIVE.get();
        if (profilerfiller == null) {
            throw new IllegalStateException("Profiler was not active");
        } else {
            ACTIVE.remove();
            ACTIVE_COUNT.decrementAndGet();
            profilerfiller.endTick();
        }
    }

    private static ProfilerFiller decorateFiller(ProfilerFiller p_373124_) {
        return ProfilerFiller.combine(getDefaultFiller(), p_373124_);
    }

    public static ProfilerFiller get() {
        return ACTIVE_COUNT.get() == 0 ? getDefaultFiller() : Objects.requireNonNullElseGet(ACTIVE.get(), Profiler::getDefaultFiller);
    }

    private static ProfilerFiller getDefaultFiller() {
        return (ProfilerFiller)(TracyClient.isAvailable() ? TRACY_FILLER.get() : InactiveProfiler.INSTANCE);
    }

    public interface Scope extends AutoCloseable {
        @Override
        void close();
    }
}
