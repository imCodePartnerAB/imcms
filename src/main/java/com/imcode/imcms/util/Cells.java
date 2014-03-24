package com.imcode.imcms.util;

import java.util.function.Consumer;

// workaround: scala 2.10.3 does not support static methods in interfaces
public class Cells {

    public static <T> T updateAndGet(T value, Consumer<T> consumer) {
        return Cells.of(value).updateAndGet(consumer);
    }

    public static <T> void with(T value, Consumer<T> consumer) {
        consumer.accept(value);
    }

    public static <T> Cell<T> of(T value) {
        return () -> value;
    }
}
