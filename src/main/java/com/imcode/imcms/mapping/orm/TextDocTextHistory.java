package com.imcode.imcms.mapping.orm;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "imcms_text_doc_texts_history")
public class TextDocTextHistory extends TextDocTextBase {

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User modifiedBy;

    @Column(name = "modified_dt")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedDt;

    @Override
    public boolean equals(Object obj) {
        return obj == this || (obj instanceof TextDocTextHistory && equals((TextDocTextHistory) obj));
    }

    private boolean equals(TextDocTextHistory that) {
        return Objects.equals(getId(), that.getId())
                && Objects.equals(getDocVersion(), that.getDocVersion())
                && Objects.equals(getDocLanguage(), that.getDocLanguage())
                && Objects.equals(getType(), that.getType())
                && Objects.equals(getNo(), that.getNo())
                && Objects.equals(getLoopItemRef(), that.getLoopItemRef())
                && Objects.equals(modifiedBy, that.modifiedBy)
                && Objects.equals(modifiedDt, that.modifiedDt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getDocVersion(), getDocLanguage(), getText(), getType(), getNo(),
                getLoopItemRef(), modifiedBy, modifiedDt);
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