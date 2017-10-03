package com.imcode.imcms.persistence.entity;

import com.imcode.imcms.mapping.jpa.doc.content.VersionedContent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity(name = "com.imcode.imcms.persistence.entity.Menu")
@Table(name = "imcms_menu")
@Setter
@Getter
@NoArgsConstructor
public class Menu extends VersionedContent {

    @NotNull
    private Integer no;

}
