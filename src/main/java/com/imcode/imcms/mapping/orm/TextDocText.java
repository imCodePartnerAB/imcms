package com.imcode.imcms.mapping.orm;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "imcms_text_doc_texts")
public class TextDocText extends TextDocTextBase {

    public TextDocText() {

    }

    public TextDocText(DocVersion docVersion, DocLanguage docLanguage, TextDocTextType type, int no, TextDocLoopEntryRef loopEntryRef, String text) {
        setDocVersion(docVersion);
        setDocLanguage(docLanguage);
        setType(type);
        setNo(no);
        setLoopEntryRef(loopEntryRef);
    }

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
                && Objects.equals(getLoopEntryRef(), that.getLoopEntryRef());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getDocVersion(), getDocLanguage(), getText(), getType(), getNo(), getLoopEntryRef());
    }
}