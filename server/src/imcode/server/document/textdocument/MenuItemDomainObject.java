package imcode.server.document.textdocument;

import imcode.server.document.DocumentDomainObject;

public class MenuItemDomainObject {

    private DocumentDomainObject document;
    private Integer sortKey;
    private TreeSortKeyDomainObject treeSortKey;

    public MenuItemDomainObject( DocumentDomainObject document ) {
        this( document, null, "" );
    }

    public MenuItemDomainObject( DocumentDomainObject document,
                                 Integer sortKey, String treeSortKey ) {
        this.document = document;
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

        if ( document != null
             ? !document.equals( menuItem.document ) : menuItem.document != null ) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        return ( document != null ? document.hashCode() : 0 );
    }

    public DocumentDomainObject getDocument() {
        return document;
    }

    public Integer getSortKey() {
        return sortKey ;
    }

    public TreeSortKeyDomainObject getTreeSortKey() {
        return treeSortKey;
    }

    public void setSortKey( Integer sortKey ) {
        this.sortKey = sortKey;
    }

}