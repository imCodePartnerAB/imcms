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
        private Content vo;

        public Builder() {
            vo = new Content();
        }

        public Builder(Content vo) {
            this.vo = vo.clone();
        }

        public Builder no(Integer no) {
            vo.no = no;
            return this;
        }

        public Builder enabled(boolean enabled) {
            vo.enabled = enabled;
            return this;
        }

        public Content build() {
            return vo.clone();
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(Content vo) {
        return new Builder(vo);
    }

    @Column(name = "no")
    private Integer no;

    /**
     * Contents are never deleted - they are disabled.
     */
    private boolean enabled = true;

    Content() {
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

    public Integer getNo() {
        return no;
    }

    void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    void setNo(Integer no) {
        this.no = no;
    }
}