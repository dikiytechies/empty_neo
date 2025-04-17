package net.minecraft.resources;

@FunctionalInterface
public interface DependantName<T, V> {
    V get(ResourceKey<T> p_368744_);

    static <T, V> DependantName<T, V> fixed(V p_368602_) {
        return p_368695_ -> p_368602_;
    }
}
