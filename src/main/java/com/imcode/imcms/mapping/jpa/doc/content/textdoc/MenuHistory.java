package com.imcode.imcms.mapping.jpa.doc.content.textdoc;

import com.imcode.imcms.mapping.jpa.User;

import javax.persistence.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "imcms_text_doc_menus_history")
public class MenuHistory extends MenuBase {

    @Column(name = "menu_id")
    private Integer menuId;

    @ElementCollection
    @CollectionTable(
            name = "imcms_text_doc_menu_items_history",
            joinColumns = @JoinColumn(name = "menu_id", referencedColumnName = "menu_id")
    )
    @MapKeyColumn(name = "to_doc_id")
    private Map<Integer, MenuItem> items = new HashMap<>();

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User modifiedBy;

    @Column(name = "modified_dt")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedDt;

    public MenuHistory() {
    }

    public MenuHistory(Menu menu, User modifiedBy) {
        setNo(menu.getNo());
        setSortOrder(menu.getSortOrder());
        setVersion(menu.getVersion());

        this.menuId = menu.getId();
        this.items = menu.getItems();
        this.modifiedBy = modifiedBy;
        this.modifiedDt = new Date();
    }

    public User getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(User modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public Date getModifiedDt() {
        return modifiedDt;
    }

    public void setModifiedDt(Date modifiedDt) {
        this.modifiedDt = modifiedDt;
    }

    public Integer getMenuId() {
        return menuId;
    }

    public void setMenuId(Integer menuId) {
        this.menuId = menuId;
    }

    public Map<Integer, MenuItem> getItems() {
        return items;
    }

    public void setItems(Map<Integer, MenuItem> items) {
        this.items = items;
    }
}
