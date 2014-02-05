package com.imcode.imcms.mapping.orm;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Menu is a one-level navigation control between documents.
 * A menu can contain any number of items - links to other documents.
 */
@Entity(name = "Menu")
@Table(name = "imcms_text_doc_menus")
public class TextDocMenu extends DocVersionedContent implements Cloneable, Serializable {

    @NotNull
    @Column(name = "no")
    private Integer no;

    @Column(name = "sort_order")
    private int sortOrder;

    /**
     * Map of included meta_id to included DocumentDomainObject.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "imcms_text_doc_menu_items",
            joinColumns = @JoinColumn(name = "menu_id")
    )
    @MapKeyColumn(name = "to_doc_id")
    private Map<Integer, TextDocMenuItem> items = new HashMap<>();

    public int getSortOrder() {
        return sortOrder;
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this || (obj instanceof TextDocMenu && equals((TextDocMenu) obj));
    }

    private boolean equals(TextDocMenu that) {
        return Objects.equals(getId(), that.getNo())
                && Objects.equals(getContentVersion(), that.getContentVersion())
                && Objects.equals(no, that.no)
                && Objects.equals(sortOrder, that.sortOrder)
                && Objects.equals(items, that.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getContentVersion(), no, sortOrder, items);
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Integer getNo() {
        return no;
    }

    public void setNo(Integer no) {
        this.no = no;
    }

    public Map<Integer, TextDocMenuItem> getItems() {
        return items;
    }

    public void setItems(Map<Integer, TextDocMenuItem> items) {
        this.items = items;
    }
}