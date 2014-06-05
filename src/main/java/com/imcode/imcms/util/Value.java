package com.imcode.imcms.util;

import java.util.function.Consumer;
import java.util.function.Function;

// Scala parser does not support static interface methods - fix version 2.12
// https://issues.scala-lang.org/browse/SI-8355
public class Value {

    public static <V, R> R apply(V v, Function<? super V, ? extends R> function) {
        return function.apply(v);
    }

    public static <V> V with(V v, Consumer<? super V> consumer) {
        consumer.accept(v);

        return v;
    }
}
