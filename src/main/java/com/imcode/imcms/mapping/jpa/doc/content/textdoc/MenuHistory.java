package com.imcode.imcms.mapping.jpa.doc.content.textdoc;

import javax.persistence.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "imcms_text_doc_menus_history")
public class MenuHistory extends MenuBase {

    @ElementCollection(fetch = FetchType.EAGER)
    @JoinTable(
            name = "imcms_text_doc_menu_items_history",
            joinColumns = {@JoinColumn(name = "menu_id", referencedColumnName = "menu_id")}
    )
    @MapKeyColumn(name = "to_doc_id")
    private Map<Integer, MenuItem> menuItems = new HashMap<>();


    @Column(name = "user_id")
    private Integer userId;


    @Column(name = "modified_dt")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedDt;


}
