package com.imcode.imcms.mapping.orm;

import javax.persistence.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name="imcms_text_doc_menus_history")
public class TextDocMenuHistory extends TextDocMenuBase {

	@ElementCollection(fetch=FetchType.EAGER)
	@JoinTable(
	    name = "imcms_text_doc_menu_items_history",
	    joinColumns = {@JoinColumn(name="menu_id", referencedColumnName = "menu_id")}
	)
	@MapKeyColumn(name="to_doc_id")
    private Map<Integer, TextDocMenuItem> menuItems = new HashMap<>();


    @Column(name="user_id")
    private Integer userId;


    @Column(name="modified_dt")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedDt;


}
