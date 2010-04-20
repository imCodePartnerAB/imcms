package com.imcode.imcms.api;

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

    @Column(name="doc_id")
	private Integer docId;

    
    @Column(name="doc_version_no")
	private Integer docVersionNo;


	@org.hibernate.annotations.CollectionOfElements(fetch=FetchType.EAGER)
	@JoinTable(
	    name = "imcms_text_doc_menu_items_history",
	    joinColumns = {@JoinColumn(name="menu_id", referencedColumnName = "menu_id")}
	)
	@org.hibernate.annotations.MapKey(
	   columns = @Column(name="to_doc_id")
	)
    private Map<Integer, MenuItemHistory> menuItems = new HashMap<Integer, MenuItemHistory>();


    @Column(name="user_id")
    private Integer userId;


    @Column(name="modified_dt")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedDt;
    
    public MenuHistory() {}

    public MenuHistory(MenuDomainObject menu, UserDomainObject user) {
        setDocId(menu.getDocId());
        setDocVersionNo(menu.getDocVersionNo());
        setMenuId(menu.getId());
        setNo(menu.getNo());        
        setModifiedDt(new Date());
        setUserId(user.getId());

        for (MenuItemDomainObject item: menu.getMenuItems()) {
            MenuItemHistory itemHistory = new MenuItemHistory(item);
            menuItems.put(item.getDocumentReference().getDocumentId(), itemHistory);
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

	public Integer getDocId() {
		return docId;
	}

	public void setDocId(Integer docId) {
		this.docId = docId;
	}

    public Integer getDocVersionNo() {
        return docVersionNo;
    }

    public void setDocVersionNo(Integer docVersionNo) {
        this.docVersionNo = docVersionNo;
    }

    public Integer getNo() {
        return no;
    }

    public void setNo(Integer no) {
        this.no = no;
    }
}