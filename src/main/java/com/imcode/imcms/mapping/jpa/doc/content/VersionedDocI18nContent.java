package com.imcode.imcms.mapping.jpa.doc.content;

import com.imcode.imcms.mapping.jpa.doc.Language;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;

@MappedSuperclass
public abstract class VersionedDocI18nContent extends VersionedDocContent {

    @NotNull
    @ManyToOne
    @JoinColumn(name = "language_id")
    private Language language;

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }
}
