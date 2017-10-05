package com.imcode.imcms.mapping.jpa.doc.content.textdoc;

import com.imcode.imcms.mapping.jpa.doc.content.VersionedI18nContent;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;

@MappedSuperclass
public class TextBase extends VersionedI18nContent {

    @NotNull
    private Integer no;

    @NotNull
    private TextType type;

    @Column(columnDefinition = "longtext")
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