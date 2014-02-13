package com.imcode.imcms.api;

public final class LoopContent {

    public static LoopContent of(int no) {
        return new LoopContent(no, true);
    }

    public static LoopContent of(int no, boolean enabled) {
        return new LoopContent(no, enabled);
    }

    private final int no;
    private final boolean enabled;

    public LoopContent(int no, boolean enabled) {
        if (no < 1) {
            throw new IllegalArgumentException(String.format("content no must be >= 1 but was %d.", no));
        }

        this.no = no;
        this.enabled = enabled;
    }

    public int getNo() {
        return no;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
