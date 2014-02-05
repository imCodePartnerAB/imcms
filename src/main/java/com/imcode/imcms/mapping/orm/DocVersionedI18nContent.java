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
    private DocLanguage contentLanguage;

    public DocLanguage getContentLanguage() {
        return contentLanguage;
    }

    public void setContentLanguage(DocLanguage contentLanguage) {
        this.contentLanguage = contentLanguage;
    }
}
