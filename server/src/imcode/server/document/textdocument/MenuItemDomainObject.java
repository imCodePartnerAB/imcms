package imcode.server.document.textdocument;

import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentReference;

public class MenuItemDomainObject {

    private DocumentReference documentReference ;
    private Integer sortKey;
    private TreeSortKeyDomainObject treeSortKey;

    public MenuItemDomainObject( DocumentReference documentReference ) {
        this( documentReference, null, "" );
    }

    public MenuItemDomainObject( DocumentReference documentReference,
                                 Integer sortKey, String treeSortKey ) {
        this.documentReference = documentReference ;
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

        final MenuItemDomainObject menuItem = (MenuItemDomainObject)o;

        DocumentDomainObject document = documentReference.getDocument();
        DocumentDomainObject otherDocument = menuItem.documentReference.getDocument();
        if ( document != null
             ? !document.equals( otherDocument ) : otherDocument != null ) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        return documentReference.getDocumentId() ;
    }

    public DocumentDomainObject getDocument() {
        return documentReference.getDocument();
    }

    public Integer getSortKey() {
        return sortKey ;
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
}