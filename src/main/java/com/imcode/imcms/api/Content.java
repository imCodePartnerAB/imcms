package com.imcode.imcms.api;

public final class Content {

    public static Content of(int no) {
        return new Content(no, true);
    }

    public static Content of(int no, boolean enabled) {
        return new Content(no, enabled);
    }

    private final int no;
    private final boolean enabled;

    public Content(int no, boolean enabled) {
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
