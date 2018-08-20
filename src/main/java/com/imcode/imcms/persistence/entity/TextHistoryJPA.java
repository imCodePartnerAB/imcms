package com.imcode.imcms.persistence.entity;

import com.imcode.imcms.model.Language;
import com.imcode.imcms.model.LoopEntryRef;
import com.imcode.imcms.model.Text;
import com.imcode.imcms.model.TextHistory;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Optional;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "imcms_text_doc_texts_history")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class TextHistoryJPA extends TextHistory {

    private static final long serialVersionUID = -1635531214924772651L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "doc_id", updatable = false, nullable = false)
    private Integer docId;

    @NotNull
    @Column(name = "`index`")
    private Integer index;

    @Column(columnDefinition = "longtext")
    private String text;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Type type;

    @NotNull
    @Column(nullable = false, name = "html_filtering_policy")
    @Enumerated(EnumType.STRING)
    private HtmlFilteringPolicy htmlFilteringPolicy;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "language_id")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private LanguageJPA language;

    private LoopEntryRefJPA loopEntryRef;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private User modifiedBy;

    @Column(name = "modified_dt")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedDt;

    public TextHistoryJPA() {
        htmlFilteringPolicy = HtmlFilteringPolicy.RESTRICTED;
    }

    public TextHistoryJPA(Text text, Language language, User modifiedBy) {
        setIndex(text.getIndex());
        setText(text.getText());
        setType(text.getType());
        setDocId(text.getDocId());
        setLoopEntryRef(text.getLoopEntryRef());
        setHtmlFilteringPolicy(text.getHtmlFilteringPolicy());
        setModifiedBy(modifiedBy);
        setModifiedDt(new Date());
        setLanguage(new LanguageJPA(language));
    }

    public TextHistoryJPA(TextHistory from) {
        super(from);
    }

    @Override
    public String getLangCode() {
        return this.language.getCode();
    }

    @Override
    public void setLoopEntryRef(LoopEntryRef loopEntryRef) {
        this.loopEntryRef = (loopEntryRef == null) ? null : new LoopEntryRefJPA(loopEntryRef);
    }

    @Override
    public Integer getModifierId() {
        return this.modifiedBy.getId();
    }

    @Override
    public HtmlFilteringPolicy getHtmlFilteringPolicy() {
        return Optional.ofNullable(htmlFilteringPolicy).orElse(HtmlFilteringPolicy.RESTRICTED);
    }

}
