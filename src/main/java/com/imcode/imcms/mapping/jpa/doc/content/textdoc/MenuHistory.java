package com.imcode.imcms.mapping.jpa.doc.content.textdoc;

import com.imcode.imcms.mapping.jpa.User;

import javax.persistence.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "imcms_text_doc_menus_history")
public class MenuHistory extends MenuBase {

    @JoinColumn(name = "user_id")
    private User modifiedBy;

    @Column(name = "modified_dt")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedDt;

    @ElementCollection
    @CollectionTable(
            name = "imcms_text_doc_menu_items_history",
            joinColumns = @JoinColumn(name = "menu_id", referencedColumnName = "menu_id")
    )
    @MapKeyColumn(name = "to_doc_id")
    private Map<Integer, MenuItem> items = new HashMap<>();

    public MenuHistory() {
    }

    public MenuHistory(Menu menu, User modifiedBy) {
        setNo(menu.getNo());
        setSortOrder(menu.getSortOrder());
        setVersion(menu.getVersion());

        this.items = menu.getItems();
        this.modifiedBy = modifiedBy;
        this.modifiedDt = new Date();
    }
}
