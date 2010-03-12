package com.imcode.imcms.api;

import javax.persistence.*;

/**
 * Content is a part of a content loop.
 * It is never instantiated directly.
 *
 * 
 * @see com.imcode.imcms.api.ContentLoop
 */
@Embeddable
public class Content implements Cloneable {

    @Column(name="no")
    private Integer no;

    /**
     * To support history, contents are never deleted physically - they are disabled. 
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
        return String.format("{no: %s, enabled: %s}", no, enabled);
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