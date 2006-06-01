package imcode.server.document.textdocument;

import com.imcode.util.ChainableReversibleNullComparator;
import imcode.server.document.DocumentComparator;

public abstract class MenuItemComparator extends ChainableReversibleNullComparator {

    static final MenuItemComparator ID = new MenuItemDocumentComparator( DocumentComparator.ID );
    static final MenuItemComparator HEADLINE = new MenuItemDocumentComparator( DocumentComparator.HEADLINE );
    static final MenuItemComparator MODIFIED_DATETIME = new MenuItemDocumentComparator( DocumentComparator.MODIFIED_DATETIME );
    static final MenuItemComparator PUBLISHED_DATETIME = new MenuItemDocumentComparator( DocumentComparator.PUBLICATION_START_DATETIME );
    static final MenuItemComparator SORT_KEY = new SortKeyComparator();
    static final MenuItemComparator TREE_SORT_KEY = new TreeSortKeyComparator();

    public String toString() {
        return getClass().getName() ;
    }

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

    private static class MenuItemDocumentComparator extends MenuItemComparator {

        private DocumentComparator documentComparator;

        private MenuItemDocumentComparator( DocumentComparator documentComparator ) {
            this.documentComparator = documentComparator;
        }

        int compareMenuItems( MenuItemDomainObject menuItem1,
                              MenuItemDomainObject menuItem2 ) {
            return documentComparator.compare( menuItem1.getDocument(), menuItem2.getDocument() );
        }

        public String toString() {
            return getClass().getName()+": "+documentComparator.toString() ;
        }

    }
}