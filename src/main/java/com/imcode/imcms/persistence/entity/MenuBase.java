package com.imcode.imcms.persistence.entity;

import com.imcode.imcms.mapping.jpa.doc.content.VersionedContent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@MappedSuperclass
public abstract class MenuBase extends VersionedContent {

    @NotNull
    private Integer no;

}
