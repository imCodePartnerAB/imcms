package com.imcode.imcms.mapping.orm;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class TextDocLoopEntry {

    private int no;

    private boolean enabled;

    public TextDocLoopEntry() {}

    public TextDocLoopEntry(int no) {
        this(no, true);
    }

    public TextDocLoopEntry(int no, boolean enabled) {
        this.no = no;
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return com.google.common.base.Objects.toStringHelper(this)
                .add("no", no)
                .add("enabled", enabled).toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(no, enabled);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || (o instanceof TextDocLoopEntry && equals((TextDocLoopEntry) o));
    }

    private boolean equals(TextDocLoopEntry that) {
        return this.enabled == that.enabled && this.no == that.no;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}