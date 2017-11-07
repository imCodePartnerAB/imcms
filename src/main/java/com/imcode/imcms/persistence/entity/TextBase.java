package com.imcode.imcms.persistence.entity;

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
public class TextBase extends VersionedI18nContent {

    @NotNull
    @Column(name = "`index`")
    private Integer index;

    @NotNull
    private Type type;

    @Column(columnDefinition = "longtext")
    private String text;

    private LoopEntryRef loopEntryRef;

    public enum Type {
        PLAIN_TEXT, HTML
    }
}
