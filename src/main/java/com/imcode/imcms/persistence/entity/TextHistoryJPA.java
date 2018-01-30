package com.imcode.imcms.persistence.entity;

import com.imcode.imcms.mapping.jpa.User;
import com.imcode.imcms.model.LoopEntryRef;
import com.imcode.imcms.model.TextHistory;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@NoArgsConstructor
@Table(name = "imcms_text_doc_texts_history")
public class TextHistoryJPA extends TextHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Column(name = "`index`")
    private Integer index;

    @Column(columnDefinition = "longtext")
    private String text;

    @NotNull
    private Type type;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "language_id")
    private LanguageJPA language;

    private LoopEntryRefJPA loopEntryRef;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User modifiedBy;

    @Column(name = "modified_dt")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedDt;

    public TextHistoryJPA(TextJPA text, User modifiedBy) {
        setLanguage(text.getLanguage());
        setIndex(text.getIndex());
        setText(text.getText());
        setType(text.getType());
        setLoopEntryRef(text.getLoopEntryRef());
        setModifiedBy(modifiedBy);
        setModifiedDt(new Date());
    }

    @Override
    public Integer getDocId() {
        return null;
    }

    @Override
    public String getLangCode() {
        return this.language.getCode();
    }

    @Override
    public void setLoopEntryRef(LoopEntryRef loopEntryRef) {
        this.loopEntryRef = (loopEntryRef == null) ? null : new LoopEntryRefJPA(loopEntryRef);
    }
}
