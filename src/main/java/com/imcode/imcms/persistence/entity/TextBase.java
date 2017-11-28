package com.imcode.imcms.persistence.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@EqualsAndHashCode
@MappedSuperclass
public class TextBase {

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

    public enum Type {
        PLAIN_TEXT, HTML
    }
}
