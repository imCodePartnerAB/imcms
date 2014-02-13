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

    private TextDocContentRef loopItemRef;

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

    public TextDocContentRef getLoopItemRef() {
        return loopItemRef;
    }

    public void setLoopItemRef(TextDocContentRef loopItemRef) {
        this.loopItemRef = loopItemRef;
    }
}