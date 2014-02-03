package com.imcode.imcms.mapping.orm;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/**
 * Content is a facet of a content loop.
 * <p/>
 * A Content is distinguished by no which is assigned automatically when a new content is added into a content loop.
 *
 * @see ContentLoop
 */
@Embeddable
@Access(AccessType.FIELD)
public class Content implements Serializable, Cloneable {

    private int no;

    private boolean enabled = true;

    @Override
    public Content clone() {
        try {
            return (Content) super.clone();
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
        return o == this || (o instanceof Content && equals((Content) o));
    }

    private boolean equals(Content that) {
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