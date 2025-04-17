package net.minecraft.world.level.storage.loot;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Optional;
import java.util.Set;
import net.minecraft.core.HolderGetter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.context.ContextKey;
import net.minecraft.util.context.ContextKeySet;

public class ValidationContext {
    private final ProblemReporter reporter;
    private final ContextKeySet contextKeySet;
    private final Optional<HolderGetter.Provider> resolver;
    private final Set<ResourceKey<?>> visitedElements;

    public ValidationContext(ProblemReporter p_311875_, ContextKeySet p_380950_, HolderGetter.Provider p_335890_) {
        this(p_311875_, p_380950_, Optional.of(p_335890_), Set.of());
    }

    public ValidationContext(ProblemReporter p_312319_, ContextKeySet p_381121_) {
        this(p_312319_, p_381121_, Optional.empty(), Set.of());
    }

    private ValidationContext(ProblemReporter p_352229_, ContextKeySet p_381144_, Optional<HolderGetter.Provider> p_381133_, Set<ResourceKey<?>> p_380961_) {
        this.reporter = p_352229_;
        this.contextKeySet = p_381144_;
        this.resolver = p_381133_;
        this.visitedElements = p_380961_;
    }

    public ValidationContext forChild(String p_79366_) {
        return new ValidationContext(this.reporter.forChild(p_79366_), this.contextKeySet, this.resolver, this.visitedElements);
    }

    public ValidationContext enterElement(String p_279180_, ResourceKey<?> p_335771_) {
        Set<ResourceKey<?>> set = ImmutableSet.<ResourceKey<?>>builder().addAll(this.visitedElements).add(p_335771_).build();
        return new ValidationContext(this.reporter.forChild(p_279180_), this.contextKeySet, this.resolver, set);
    }

    public boolean hasVisitedElement(ResourceKey<?> p_335626_) {
        return this.visitedElements.contains(p_335626_);
    }

    public void reportProblem(String p_79358_) {
        this.reporter.report(p_79358_);
    }

    public void validateContextUsage(LootContextUser p_381132_) {
        Set<ContextKey<?>> set = p_381132_.getReferencedContextParams();
        Set<ContextKey<?>> set1 = Sets.difference(set, this.contextKeySet.allowed());
        if (!set1.isEmpty()) {
            this.reporter.report("Parameters " + set1 + " are not provided in this context");
        }
    }

    public HolderGetter.Provider resolver() {
        return this.resolver.orElseThrow(() -> new UnsupportedOperationException("References not allowed"));
    }

    public boolean allowsReferences() {
        return this.resolver.isPresent();
    }

    public ValidationContext setContextKeySet(ContextKeySet p_381099_) {
        return new ValidationContext(this.reporter, p_381099_, this.resolver, this.visitedElements);
    }

    public ProblemReporter reporter() {
        return this.reporter;
    }
}
