package com.imcode.imcms.mapping.orm;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Objects;

@Entity(name = "Text")
@Table(name = "imcms_text_doc_texts")
public class TextDocText extends TextDocTextBase {

    @Override
    public boolean equals(Object obj) {
        return obj == this || (obj instanceof TextDocText && equals((TextDocText) obj));
    }

    private boolean equals(TextDocText that) {
        return Objects.equals(getId(), that.getId())
                && Objects.equals(getDocVersion(), that.getDocVersion())
                && Objects.equals(getDocLanguage(), that.getDocLanguage())
                && Objects.equals(getType(), that.getType())
                && Objects.equals(getNo(), that.getNo())
                && Objects.equals(getLoopEntry(), that.getLoopEntry());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getDocVersion(), getDocLanguage(), getText(), getType(), getNo(), getLoopEntry());
    }
}