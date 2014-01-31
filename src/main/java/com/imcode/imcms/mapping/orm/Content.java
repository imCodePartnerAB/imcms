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

    public static final class Builder {
        private Content content;

        public Builder() {
            content = new Content();
        }

        public Builder(Content content) {
            this.content = content.clone();
        }

        public Builder no(int no) {
            content.no = no;
            return this;
        }

        public Builder enabled(boolean enabled) {
            content.enabled = enabled;
            return this;
        }

        public Content build() {
            return content.clone();
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(Content content) {
        return new Builder(content);
    }

    private int no;

    private boolean enabled = true;

    protected Content() {
    }

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
        return String.format("Content{no=%s, enabled=%s}", no, enabled);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isDisabled() {
        return !enabled;
    }

    public int getNo() {
        return no;
    }

    @Override
    public boolean equals(Object o) {
        return o == this || (o instanceof Content && equals((Content) o));
    }

    private boolean equals(Content that) {
        return this.enabled == that.enabled && this.no == that.no;
    }

    @Override
    public int hashCode() {
        return Objects.hash(no, enabled);
    }
}