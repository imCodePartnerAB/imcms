package com.imcode.imcms.mapping.jpa.doc.content.textdoc;

import com.imcode.imcms.mapping.jpa.doc.content.VersionedDocI18nContent;

import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;

@MappedSuperclass
public class TextBase extends VersionedDocI18nContent {

    @NotNull
    private Integer no;

    @NotNull
    private TextType type;

    private String text;

    private LoopEntryRef loopEntryRef;

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

    public TextType getType() {
        return type;
    }

    public void setType(TextType type) {
        this.type = type;
    }

    public LoopEntryRef getLoopEntryRef() {
        return loopEntryRef;
    }

    public void setLoopEntryRef(LoopEntryRef loopEntry) {
        this.loopEntryRef = loopEntry;
    }
}