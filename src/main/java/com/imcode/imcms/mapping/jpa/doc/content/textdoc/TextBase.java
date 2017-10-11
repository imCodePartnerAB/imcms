package com.imcode.imcms.mapping.jpa.doc.content.textdoc;

import com.imcode.imcms.mapping.jpa.doc.content.VersionedI18nContent;
import com.imcode.imcms.persistence.entity.LoopEntryRef;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@MappedSuperclass
class TextBase extends VersionedI18nContent {

    @NotNull
    @Column(name = "`index`")
    private Integer index;

    @NotNull
    private TextType type;

    @Column(columnDefinition = "longtext")
    private String text;

    private LoopEntryRef loopEntryRef;

}
