package imcode.server.document.textdocument;

import com.imcode.util.ChainableReversibleNullComparator;
import imcode.server.document.DocumentComparator;

public abstract class MenuItemComparator extends ChainableReversibleNullComparator<MenuItemDomainObject> {

    public String toString() {
        return getClass().getName();
    }

    public int compare(MenuItemDomainObject o1, MenuItemDomainObject o2) {
        return compareMenuItems(o1, o2);
    }

    abstract int compareMenuItems(MenuItemDomainObject menuItem1, MenuItemDomainObject menuItem2);

    static class TreeSortKeyComparator extends MenuItemComparator {

        int compareMenuItems(MenuItemDomainObject menuItem1, MenuItemDomainObject menuItem2) {
            return menuItem1.getTreeSortKey().compareTo(menuItem2.getTreeSortKey());
        }
    }

    static class SortKeyComparator extends MenuItemComparator {

        int compareMenuItems(MenuItemDomainObject menuItem1, MenuItemDomainObject menuItem2) {
            return menuItem1.getSortKey().compareTo(menuItem2.getSortKey());
        }
    }

    static class MenuItemDocumentComparator extends MenuItemComparator {

        private DocumentComparator documentComparator;

        MenuItemDocumentComparator(DocumentComparator documentComparator) {
            this.documentComparator = documentComparator;
        }

        int compareMenuItems(MenuItemDomainObject menuItem1,
                             MenuItemDomainObject menuItem2) {
            return documentComparator.compare(menuItem1.getDocument(), menuItem2.getDocument());
        }

        public String toString() {
            return getClass().getName() + ": " + documentComparator.toString();
        }

    }
}