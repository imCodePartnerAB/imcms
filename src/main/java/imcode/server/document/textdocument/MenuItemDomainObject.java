package imcode.server.document.textdocument;

import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentReference;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.aspectj.bridge.MessageWriter;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;

/**
 * Menu item descriptor.
 *
 * @see imcode.server.document.textdocument.MenuDomainObject
 */

@Embeddable
public class MenuItemDomainObject implements Cloneable, Serializable {

    @Column(name = "manual_sort_order")
    private Integer sortKey;

    @Column(name = "tree_sort_index")
    private String treeSortIndex;

    @Transient
    private DocumentReference documentReference;

    @Transient
    private TreeSortKeyDomainObject treeSortKey;

    public MenuItemDomainObject() {
    }

    public MenuItemDomainObject(DocumentReference documentReference,
                                Integer sortKey, TreeSortKeyDomainObject treeSortKey) {
        this.documentReference = documentReference;
        this.sortKey = sortKey;
        this.treeSortKey = treeSortKey;
    }

    public MenuItemDomainObject(DocumentReference documentReference) {
        this(documentReference, null, new TreeSortKeyDomainObject(""));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof MenuItemDomainObject)) {
            return false;
        }

        final MenuItemDomainObject otherMenuItem = (MenuItemDomainObject) o;

        return new EqualsBuilder()
                .append(sortKey, otherMenuItem.sortKey)
                .append(documentReference, otherMenuItem.documentReference)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(5, 7).append(documentReference).toHashCode();
    }

    public Integer getSortKey() {
        return sortKey;
    }

    public TreeSortKeyDomainObject getTreeSortKey() {
        return treeSortKey;
    }

    public DocumentReference getDocumentReference() {
        return documentReference;
    }

    public MenuItemDomainObject clone() throws CloneNotSupportedException {
        MenuItemDomainObject clone = (MenuItemDomainObject) super.clone();
        if (treeSortKey != null) {
            clone.treeSortKey = new TreeSortKeyDomainObject(treeSortKey.getTreeSortKey());
        }

        if (documentReference != null) {
            clone.documentReference = documentReference.clone();
        }

        return clone;
    }

    public DocumentDomainObject getDocument() {
        if (documentReference == null)
            throw new IllegalStateException("Document reference is not initialized.");

        return documentReference.getDocument();
    }

    public int getDocumentId() {
        return documentReference.getDocumentId();
    }

    public void setSortKey(Integer sortKey) {
        this.sortKey = sortKey;
    }

    public void setTreeSortKey(TreeSortKeyDomainObject treeSortKey) {
        this.treeSortKey = treeSortKey;
    }

    public String getTreeSortIndex() {
        return treeSortIndex;
    }

    public void setTreeSortIndex(String treeSortIndex) {
        this.treeSortIndex = treeSortIndex;
    }

    public void setDocumentReference(DocumentReference documentReference) {
        this.documentReference = documentReference;
    }
}