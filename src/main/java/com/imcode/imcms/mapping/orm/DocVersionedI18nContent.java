package com.imcode.imcms.mapping.orm;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;

@MappedSuperclass
public abstract class DocVersionedI18nContent extends DocVersionedContent {

    @NotNull
    @ManyToOne
    @JoinColumn(name = "language_id")
    private DocLanguage docLanguage;

    public DocLanguage getDocLanguage() {
        return docLanguage;
    }

    public void setDocLanguage(DocLanguage contentLanguage) {
        this.docLanguage = contentLanguage;
    }
}
