package com.imcode.imcms.mapping.orm;

import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;

@MappedSuperclass
public class TextDocTextBase extends DocVersionedI18nContent {

    @NotNull
    private Integer no;

    @NotNull
    private TextDocTextType type;

    private String text;

    private TextDocLoopEntryRef loopEntryRef;

    public Integer getNo() {
        return no;
    }

    public void setNo(Integer no) {
        this.no = no;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public TextDocTextType getType() {
        return type;
    }

    public void setType(TextDocTextType type) {
        this.type = type;
    }

    public TextDocLoopEntryRef getLoopEntryRef() {
        return loopEntryRef;
    }

    public void setLoopEntryRef(TextDocLoopEntryRef loopEntry) {
        this.loopEntryRef = loopEntry;
    }
}