package com.imcode.imcms.util;

import java.util.function.Consumer;

@FunctionalInterface
public interface Cell<T> {

    T get();

    default T updateAndGet(Consumer<T> consumer) {
        return update(consumer).get();
    }

    default Cell<T> update(Consumer<T> consumer) {
        consumer.accept(get());
        return this;
    }
}
