package com.imcode.imcms.util;

import java.util.function.Consumer;
import java.util.function.Function;

public class Value {

    public static <V, R> R apply(V v, Function<? super V, ? extends R> function) {
        return function.apply(v);
    }

    public static <V> V update(V v, Consumer<? super V> consumer) {
        consumer.accept(v);

        return v;
    }

    public static <V> void with(V v, Consumer<? super V> consumer) {
        consumer.accept(v);
    }
}
