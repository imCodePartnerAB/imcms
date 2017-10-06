package com.imcode.imcms.mapping.jpa.doc.content.textdoc;

import com.imcode.imcms.mapping.jpa.User;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "imcms_text_doc_texts_history")
public class TextHistory extends TextBase {

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User modifiedBy;

    @Column(name = "modified_dt")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedDt;

    public TextHistory() {
    }

    public TextHistory(Text text, User modifiedBy) {
        setVersion(text.getVersion());
        setLanguage(text.getLanguage());
        setIndex(text.getIndex());
        setText(text.getText());
        setType(text.getType());
        setLoopEntryRef(text.getLoopEntryRef());
        setModifiedBy(modifiedBy);
        setModifiedDt(new Date());
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this || (obj instanceof TextHistory && equals((TextHistory) obj));
    }

    private boolean equals(TextHistory that) {
        return Objects.equals(getId(), that.getId())
                && Objects.equals(getVersion(), that.getVersion())
                && Objects.equals(getLanguage(), that.getLanguage())
                && Objects.equals(getType(), that.getType())
                && Objects.equals(getIndex(), that.getIndex())
                && Objects.equals(getLoopEntryRef(), that.getLoopEntryRef())
                && Objects.equals(modifiedBy, that.modifiedBy)
                && Objects.equals(modifiedDt, that.modifiedDt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getVersion(), getLanguage(), getText(), getType(), getIndex(),
                getLoopEntryRef(), modifiedBy, modifiedDt);
    }

    public User getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(User createdBy) {
        this.modifiedBy = createdBy;
    }

    public Date getModifiedDt() {
        return modifiedDt;
    }

    public void setModifiedDt(Date modifiedDt) {
        this.modifiedDt = modifiedDt;
    }
}