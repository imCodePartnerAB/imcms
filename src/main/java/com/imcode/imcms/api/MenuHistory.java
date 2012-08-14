package com.imcode.imcms.api;

import imcode.server.document.textdocument.DocIdentity;
import imcode.server.document.textdocument.MenuDomainObject;
import imcode.server.document.textdocument.MenuItemDomainObject;
import imcode.server.user.UserDomainObject;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
@Table(name="imcms_text_doc_menus_history")
public class MenuHistory implements Serializable {

    @Embeddable
    public static class MenuItemHistory implements Serializable {

        @Column(name="manual_sort_order")
        private Integer sortKey;

        @Column(name="tree_sort_index")
        private String treeSortIndex;

        public MenuItemHistory() {}

        public MenuItemHistory(MenuItemDomainObject item) {
            setSortKey(item.getSortKey());
            setTreeSortIndex(item.getTreeSortIndex());
        }

        public Integer getSortKey() {
            return sortKey;
        }

        public void setSortKey(Integer sortKey) {
            this.sortKey = sortKey;
        }

        public String getTreeSortIndex() {
            return treeSortIndex;
        }

        public void setTreeSortIndex(String treeSortIndex) {
            this.treeSortIndex = treeSortIndex;
        }
    }

	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

	@Column(name="menu_id")
    private Long menuId;

	@Column(name="sort_order")
    private int sortOrder;

	private Integer no;

	private DocIdentity docIdentity;


	@ElementCollection(fetch=FetchType.EAGER)
	@JoinTable(
	    name = "imcms_text_doc_menu_items_history",
	    joinColumns = {@JoinColumn(name="menu_id", referencedColumnName = "menu_id")}
	)
	@MapKeyColumn(name="to_doc_id")
    private Map<Integer, MenuItemHistory> menuItems = new HashMap<Integer, MenuItemHistory>();


    @Column(name="user_id")
    private Integer userId;


    @Column(name="modified_dt")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedDt;
    
    public MenuHistory() {}

    public MenuHistory(MenuDomainObject menu, UserDomainObject user) {
        setDocIdentity(menu.getDocIdentity());
        setMenuId(menu.getId());
        setNo(menu.getNo());        
        setModifiedDt(new Date());
        setUserId(user.getId());

        for (Map.Entry<Integer, MenuItemDomainObject> entry: menu.getItemsMap().entrySet()) {
            MenuItemHistory itemHistory = new MenuItemHistory(entry.getValue());
            menuItems.put(entry.getKey(), itemHistory);
        }
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Date getModifiedDt() {
        return modifiedDt;
    }

    public void setModifiedDt(Date modifiedDt) {
        this.modifiedDt = modifiedDt;
    }

    public Long getMenuId() {
        return menuId;
    }

    public void setMenuId(Long menuId) {
        this.menuId = menuId;
    }

    public Long getId() {
        return id;
    }

    public void setId( Long id ) {
        this.id = id;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public Integer getNo() {
        return no;
    }

    public void setNo(Integer no) {
        this.no = no;
    }

    public DocIdentity getDocIdentity() {
        return docIdentity;
    }

    public void setDocIdentity(DocIdentity docRef) {
        this.docIdentity = docRef;
    }
}