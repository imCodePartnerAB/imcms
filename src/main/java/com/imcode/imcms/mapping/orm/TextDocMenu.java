package com.imcode.imcms.mapping.orm;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Entity(name = "Menu")
@Table(name = "imcms_text_doc_menus")
public class TextDocMenu extends TextDocMenuBase {

    @ElementCollection
    @CollectionTable(
            name = "imcms_text_doc_menu_items",
            joinColumns = @JoinColumn(name = "menu_id")
    )
    @MapKeyColumn(name = "to_doc_id")
    private Map<Integer, TextDocMenuItem> items = new HashMap<>();

    public Map<Integer, TextDocMenuItem> getItems() {
        return items;
    }

    public void setItems(Map<Integer, TextDocMenuItem> items) {
        this.items = items;
    }
}