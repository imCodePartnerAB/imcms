package com.imcode.imcms.util;

// Product
public final class P {

    public static <A, B> P2<A, B> of(final A a, final B b) {
        return new P2<A, B>() {
            public A _1() { return a; }
            public B _2() { return b; }
        };
    }
}


