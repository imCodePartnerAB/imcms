package com.imcode.imcms.mapping.jpa.doc.content.textdoc;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "imcms_text_doc_menus")
public class Menu extends MenuBase {

    @ElementCollection
    @CollectionTable(
            name = "imcms_text_doc_menu_items",
            joinColumns = @JoinColumn(name = "menu_id")
    )
    @MapKeyColumn(name = "to_doc_id")
    private Map<Integer, MenuItem> items = new HashMap<>();

    public Map<Integer, MenuItem> getItems() {
        return items;
    }

    public void setItems(Map<Integer, MenuItem> items) {
        this.items = items;
    }
}