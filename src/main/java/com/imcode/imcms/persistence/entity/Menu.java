package com.imcode.imcms.persistence.entity;

import lombok.*;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity(name = "com.imcode.imcms.persistence.entity.Menu")
@Table(name = "imcms_menu")
@Setter
@Getter
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Menu extends VersionedContent {

    @NotNull
    private Integer no;

    @OneToMany(cascade = {CascadeType.ALL}, orphanRemoval = true)
    @JoinColumn(name = "menu_id")
    @Where(clause = "menu_id is not null")
    @OrderBy("sortOrder")
    private List<MenuItem> menuItems;

}
