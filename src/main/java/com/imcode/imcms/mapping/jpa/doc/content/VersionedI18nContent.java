package com.imcode.imcms.mapping.jpa.doc.content;

import com.imcode.imcms.persistence.entity.Language;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@MappedSuperclass
public abstract class VersionedI18nContent extends VersionedContent {

    @NotNull
    @ManyToOne
    @JoinColumn(name = "language_id")
    private Language language;

}
