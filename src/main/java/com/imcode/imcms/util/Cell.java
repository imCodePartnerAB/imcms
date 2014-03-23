package com.imcode.imcms.util;

import java.util.function.Consumer;

@FunctionalInterface
public interface Cell<T> {

    static <T> T updateAndGet(T value, Consumer<T> consumer) {
        return Cell.of(value).updateAndGet(consumer);
    }

    static <T> void with(T value, Consumer<T> consumer) {
        consumer.accept(value);
    }

    static <T> Cell<T> of(T value) {
        return () -> value;
    }

    T get();

    default T updateAndGet(Consumer<T> consumer) {
        T value = get();
        consumer.accept(value);
        return value;
    }

    default void update(Consumer<T> consumer) {
        consumer.accept(get());
    }
}
