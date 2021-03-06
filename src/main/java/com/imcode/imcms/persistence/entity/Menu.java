package com.imcode.imcms.persistence.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Where;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "imcms_menu")
@Setter
@Getter
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Menu extends VersionedContent {

    private static final long serialVersionUID = 1481827218337826982L;

    @NotNull
    private Integer no;

    @OneToMany(cascade = {CascadeType.ALL}, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "menu_id")
    @Where(clause = "menu_id is not null")
    @OrderBy("sortOrder")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<MenuItem> menuItems;

    @NotNull
    private boolean nested;

    public Set<MenuItem> getMenuItems() {
        if (menuItems == null) menuItems = new LinkedHashSet<>();
        return menuItems;
    }

    public void setMenuItems(Set<MenuItem> menuItems) {
        if (this.menuItems == null) {
            this.menuItems = menuItems;
            return;
        }

        this.menuItems.clear();
        this.menuItems.addAll(menuItems);
    }
}
