package com.imcode.imcms.persistence.entity;

import com.imcode.imcms.model.LoopEntryRef;
import com.imcode.imcms.model.Text;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "imcms_text_doc_texts")
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class TextJPA extends Text {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "doc_id", referencedColumnName = "doc_id"),
            @JoinColumn(name = "doc_version_no", referencedColumnName = "no")
    })
    private Version version;

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

    public TextJPA(Text from, Version version, LanguageJPA language) {
        super(from);
        setVersion(version);
        setLanguage(language);
    }

    @Override
    public Integer getDocId() {
        return version.getDocId();
    }

    @Override
    public String getLangCode() {
        return language.getCode();
    }

    @Override
    public void setLoopEntryRef(LoopEntryRef loopEntryRef) {
        this.loopEntryRef = (loopEntryRef == null) ? null : new LoopEntryRefJPA(loopEntryRef);
    }
}