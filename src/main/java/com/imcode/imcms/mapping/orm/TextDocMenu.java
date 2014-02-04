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
public class TextDocMenu implements Cloneable, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Column(name = "no")
    private Integer no;

    @NotNull
    private DocRef docRef;

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

    public TextDocMenu() {
    }

    public TextDocMenu clone() {
        try {
            TextDocMenu clone = (TextDocMenu) super.clone();
            clone.items = new HashMap<>();
            for (Map.Entry<Integer, TextDocMenuItem> entry : items.entrySet()) {
                clone.items.put(entry.getKey(), entry.getValue().clone());
            }
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    public TextDocMenu(Integer id, int sortOrder) {
        this.id = id;
        this.sortOrder = sortOrder;
        items = new HashMap<>();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this || (obj instanceof TextDocMenu && equals((TextDocMenu) obj));
    }

    private boolean equals(TextDocMenu that) {
        return Objects.equals(id, that.id)
                && Objects.equals(no, that.no)
                && Objects.equals(docRef, that.docRef)
                && Objects.equals(sortOrder, that.sortOrder)
                && Objects.equals(items, that.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, sortOrder, docRef, items);
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

    public DocRef getDocRef() {
        return docRef;
    }

    public void setDocRef(DocRef docRef) {
        this.docRef = docRef;
    }

    public Map<Integer, TextDocMenuItem> getItems() {
        return items;
    }

    public void setItems(Map<Integer, TextDocMenuItem> items) {
        this.items = items;
    }
}