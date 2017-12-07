package com.imcode.imcms.persistence.entity;

import com.imcode.imcms.model.LoopEntryRef;
import com.imcode.imcms.model.Text;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@EqualsAndHashCode
@MappedSuperclass
@NoArgsConstructor
class TextJPABase extends Text {

    @NotNull
    @Column(name = "`index`")
    private Integer index;

    @NotNull
    private Type type;

    @Column(columnDefinition = "longtext")
    private String text;

    private LoopEntryRefJPA loopEntryRef;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "language_id")
    private LanguageJPA language;

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

    TextJPABase(Text from) {
        super(from);
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
