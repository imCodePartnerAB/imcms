package com.imcode.imcms.mapping.jpa.doc.content.textdoc;

import com.imcode.imcms.mapping.jpa.doc.DocVersion;
import com.imcode.imcms.mapping.jpa.doc.Language;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "imcms_text_doc_texts")
public class Text extends TextBase {

    public Text() {

    }

    public Text(DocVersion docVersion, Language language, TextType type, int no, LoopEntryRef loopEntryRef, String text) {
        setDocVersion(docVersion);
        setLanguage(language);
        setType(type);
        setNo(no);
        setLoopEntryRef(loopEntryRef);
    }

    @Override
        public boolean equals(Object obj) {
        return obj == this || (obj instanceof Text && equals((Text) obj));
    }

    private boolean equals(Text that) {
        return Objects.equals(getId(), that.getId())
                && Objects.equals(getDocVersion(), that.getDocVersion())
                && Objects.equals(getLanguage(), that.getLanguage())
                && Objects.equals(getType(), that.getType())
                && Objects.equals(getNo(), that.getNo())
                && Objects.equals(getLoopEntryRef(), that.getLoopEntryRef());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getDocVersion(), getLanguage(), getText(), getType(), getNo(), getLoopEntryRef());
    }
}