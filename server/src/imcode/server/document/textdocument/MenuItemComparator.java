package imcode.server.document.textdocument;

import com.imcode.imcms.api.util.ChainableReversibleNullComparator;
import imcode.server.document.DocumentDomainObject;

public abstract class MenuItemComparator extends ChainableReversibleNullComparator {

    static final MenuItemComparator ID = new DocumentComparator( DocumentDomainObject.DocumentComparator.ID );
    static final MenuItemComparator HEADLINE = new DocumentComparator( DocumentDomainObject.DocumentComparator.HEADLINE );
    static final MenuItemComparator MODIFIED_DATETIME = new DocumentComparator( DocumentDomainObject.DocumentComparator.MODIFIED_DATETIME );
    static final MenuItemComparator SORT_KEY = new SortKeyComparator();
    static final MenuItemComparator TREE_SORT_KEY = new TreeSortKeyComparator();

    public int compare( Object o1, Object o2 ) {
        return compareMenuItems( (MenuItemDomainObject)o1, (MenuItemDomainObject)o2 );
    }

    abstract int compareMenuItems( MenuItemDomainObject menuItem1, MenuItemDomainObject menuItem2 );

    private static class TreeSortKeyComparator extends MenuItemComparator {

        int compareMenuItems( MenuItemDomainObject menuItem1, MenuItemDomainObject menuItem2 ) {
            return menuItem1.getTreeSortKey().compareTo( menuItem2.getTreeSortKey() );
        }
    }

    private static class SortKeyComparator extends MenuItemComparator {

        int compareMenuItems( MenuItemDomainObject menuItem1, MenuItemDomainObject menuItem2 ) {
            return menuItem1.getSortKey().compareTo( menuItem2.getSortKey() );
        }
    }

    private static class DocumentComparator extends MenuItemComparator {

        private DocumentDomainObject.DocumentComparator documentComparator;

        private DocumentComparator( DocumentDomainObject.DocumentComparator documentComparator ) {
            this.documentComparator = documentComparator;
        }

        int compareMenuItems( MenuItemDomainObject menuItem1,
                              MenuItemDomainObject menuItem2 ) {
            return documentComparator.compare( menuItem1.getDocument(), menuItem2.getDocument() );
        }

    }
}