package imcode.server.document.textdocument;

import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentReference;

public class MenuItemDomainObject implements Cloneable {

    private DocumentReference documentReference;
    private Integer sortKey;
    private TreeSortKeyDomainObject treeSortKey;

    public MenuItemDomainObject( DocumentReference documentReference ) {
        this( documentReference, null, "" );
    }

    public MenuItemDomainObject( DocumentReference documentReference,
                                 Integer sortKey, String treeSortKey ) {
        this.documentReference = documentReference;
        this.sortKey = sortKey;
        this.treeSortKey = new TreeSortKeyDomainObject( treeSortKey );
    }

    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof MenuItemDomainObject ) ) {
            return false;
        }

        final MenuItemDomainObject otherMenuItem = (MenuItemDomainObject)o;

        return sortKey.equals( otherMenuItem.sortKey )
               && treeSortKey.equals( otherMenuItem.treeSortKey )
               && documentReference.equals( otherMenuItem.documentReference );
    }

    public int hashCode() {
        return sortKey.hashCode() + treeSortKey.hashCode() + documentReference.hashCode();
    }

    public DocumentDomainObject getDocument() {
        return documentReference.getDocument();
    }

    public Integer getSortKey() {
        return sortKey;
    }

    public void setSortKey( Integer sortKey ) {
        this.sortKey = sortKey;
    }

    public TreeSortKeyDomainObject getTreeSortKey() {
        return treeSortKey;
    }

    public void setTreeSortKey( TreeSortKeyDomainObject treeSortKey ) {
        this.treeSortKey = treeSortKey;
    }

    public DocumentReference getDocumentReference() {
        return documentReference;
    }

    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}