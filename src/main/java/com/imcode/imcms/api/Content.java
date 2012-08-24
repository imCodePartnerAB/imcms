package com.imcode.imcms.api;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Content is a facet of a content loop.
 * <p/>
 * A Content is distinguished by no which is assigned automatically when a new content is added into a content loop.
 *
 * @see com.imcode.imcms.api.ContentLoop
 */
@Embeddable
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


    @Column(name = "no")
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
        if (this == o) return true;
        if (!(o instanceof Content)) return false;

        Content content = (Content) o;

        if (enabled != content.enabled) return false;
        if (no != content.no) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = no;
        result = 31 * result + (enabled ? 1 : 0);
        return result;
    }
}

