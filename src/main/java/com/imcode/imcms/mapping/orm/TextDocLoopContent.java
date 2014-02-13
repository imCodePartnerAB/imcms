package com.imcode.imcms.mapping.orm;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Access(AccessType.FIELD)
public class TextDocLoopContent implements Serializable, Cloneable {

    private int no;

    private boolean enabled;

    public TextDocLoopContent() {}

    public TextDocLoopContent(int no) {
        this(no, true);
    }

    public TextDocLoopContent(int no, boolean enabled) {
        this.no = no;
        this.enabled = enabled;
    }

    @Override
    public TextDocLoopContent clone() {
        try {
            return (TextDocLoopContent) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
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
        return o == this || (o instanceof TextDocLoopContent && equals((TextDocLoopContent) o));
    }

    private boolean equals(TextDocLoopContent that) {
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