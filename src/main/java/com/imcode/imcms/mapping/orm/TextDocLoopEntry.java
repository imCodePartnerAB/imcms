package com.imcode.imcms.mapping.orm;

import com.google.common.base.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class TextDocLoopEntry implements Serializable, Cloneable {

    @Column(name = "content_loop_no")
    private int loopNo;

    @Column(name = "content_no")
    private int contentNo;

    public TextDocLoopEntry() {}

    public TextDocLoopEntry(int loopNo, int contentNo) {
        this.loopNo = loopNo;
        this.contentNo = contentNo;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof TextDocLoopEntry && equals((TextDocLoopEntry) o));
    }

    private boolean equals(TextDocLoopEntry that) {
        return loopNo == that.loopNo && contentNo == that.contentNo;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(loopNo, contentNo);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("contentNo", contentNo).add("loopNo", loopNo).toString();
    }

    public int getLoopNo() {
        return loopNo;
    }

    public int getContentNo() {
        return contentNo;
    }
}
