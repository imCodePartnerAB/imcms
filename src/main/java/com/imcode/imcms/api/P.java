package com.imcode.imcms.api;

// Product
public final class P {

    public abstract static class P2<A, B> {
        public abstract A _1();
        public abstract B _2();
    }

    public static <A, B> P2<A, B> of(final A a, final B b) {
        return new P2<A, B>() {
            public A _1() { return a; }
            public B _2() { return b; }
        };
    }
}


