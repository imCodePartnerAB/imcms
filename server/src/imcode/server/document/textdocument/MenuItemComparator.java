package imcode.server.document.textdocument;

import com.imcode.imcms.api.util.ChainableReversibleNullComparator;
import imcode.server.document.DocumentDomainObject;

public abstract class MenuItemComparator extends ChainableReversibleNullComparator {

    static final DocumentComparator ID = new DocumentComparator( DocumentDomainObject.DocumentComparator.ID );
    static final DocumentComparator HEADLINE = new DocumentComparator( DocumentDomainObject.DocumentComparator.HEADLINE );
    static final DocumentComparator MODIFIED_DATETIME = new DocumentComparator( DocumentDomainObject.DocumentComparator.MODIFIED_DATETIME );
    static final SortKeyComparator SORT_KEY = new SortKeyComparator();
    static final TreeSortKeyComparator TREE_SORT_KEY = new TreeSortKeyComparator();

    public int compare( Object o1, Object o2 ) {
        return compareMenuItems( (MenuItemDomainObject)o1, (MenuItemDomainObject)o2 );
    }

    abstract int compareMenuItems( MenuItemDomainObject menuItem1, MenuItemDomainObject menuItem2 );

    static class TreeSortKeyComparator extends MenuItemComparator {

        int compareMenuItems( MenuItemDomainObject menuItem1, MenuItemDomainObject menuItem2 ) {
            return menuItem1.getTreeSortKey().compareTo( menuItem2.getTreeSortKey() );
        }
    }

    static class SortKeyComparator extends MenuItemComparator {

        int compareMenuItems( MenuItemDomainObject menuItem1, MenuItemDomainObject menuItem2 ) {
            return menuItem1.getSortKey().compareTo( menuItem2.getSortKey() );
        }
    }

    static class DocumentComparator extends MenuItemComparator {

        private DocumentDomainObject.DocumentComparator documentComparator;

        public DocumentComparator( DocumentDomainObject.DocumentComparator documentComparator ) {
            this.documentComparator = documentComparator;
        }

        int compareMenuItems( MenuItemDomainObject menuItem1,
                              MenuItemDomainObject menuItem2 ) {
            return documentComparator.compare( menuItem1.getDocument(), menuItem2.getDocument() );
        }

    }
}