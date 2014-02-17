package com.imcode.imcms.mapping.orm;

import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;

@MappedSuperclass
public class TextDocTextBase extends DocVersionedI18nContent {

    @NotNull
    private Integer no;

    @NotNull
    private TextDocType type;

    private String text;

    private TextDocLoopEntry loopEntry;

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

    public TextDocType getType() {
        return type;
    }

    public void setType(TextDocType type) {
        this.type = type;
    }

    public TextDocLoopEntry getLoopEntry() {
        return loopEntry;
    }

    public void setLoopEntry(TextDocLoopEntry loopItemRef) {
        this.loopEntry = loopItemRef;
    }
}