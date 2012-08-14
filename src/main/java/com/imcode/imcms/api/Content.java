package com.imcode.imcms.api;

import javax.persistence.*;

/**
 * Content is a facet of a content loop.
 * 
 * @see com.imcode.imcms.api.ContentLoop
 */
@Embeddable
public class Content implements Cloneable {

    @Column(name="no")
    private Integer no;

    /**
     * Contents are never deleted - they are disabled.
     */
    private boolean enabled = true;

    Content() {}

    @Override
	public Content clone() {
		try {
			return (Content)super.clone();
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