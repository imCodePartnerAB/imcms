package imcode.server.document;

import java.util.*;

public class MenuItemDomainObject {
    public MenuItemDomainObject( DocumentDomainObject parent, DocumentDomainObject child, int menuSort, int manualSortOrder, String treeSortIndex ) {
        this.parent = parent;
        this.child = child;
        this.menuSort = menuSort;
        this.manualSortOrder = manualSortOrder;
        this.treeSortIndex = treeSortIndex;
    }

    DocumentDomainObject parent;
    DocumentDomainObject child;
    int menuSort;
    int manualSortOrder;
    String treeSortIndex;

    public DocumentDomainObject getParentDocument() {
        return parent;
    }

    public DocumentDomainObject getDocument() {
        return child;
    }

    public int getManualNumber() {
        return manualSortOrder;
    }

    public TreeKeyDomainObject getTreeKey() {
        if( null == treeSortIndex || "".equals( treeSortIndex ) ) {
            return null;
        }
        return new TreeKeyDomainObject( treeSortIndex );
    }

    static class TreeKeyCoparator implements Comparator {

        public int compare( Object o1, Object o2 ) {
            TreeKeyDomainObject treeSortKey1 = ( (MenuItemDomainObject)o1 ).getTreeKey();
            TreeKeyDomainObject treeSortKey2 = ( (MenuItemDomainObject)o2 ).getTreeKey();

            int difference = 0;
            if( null != treeSortKey1 && null != treeSortKey2 ) {
                difference = compareTreeSortKeys( treeSortKey1, treeSortKey2, 1 );
            } else {
                if( null == treeSortKey1 && null == treeSortKey2 ) {
                    difference = 0;
                } else if( null == treeSortKey1 ) {
                    difference = -1;
                } else
                    difference = 1;
            }
// todo:
/*
            if ( 0 == difference ) {
                return dateComparator.compare( o1, o2 );
            }
 */
            return difference;
        }

        private int compareTreeSortKeys( TreeKeyDomainObject key1, TreeKeyDomainObject key2, int level ) {
            boolean hasKeyOnThisLevel1 = key1.getLevel() >= level;
            boolean hasKeyOnThisLevel2 = key2.getLevel() >= level;
            if( hasKeyOnThisLevel1 && hasKeyOnThisLevel2 ) {
                int firstNumber1 = key1.getSortNumber( level );
                int firstNumber2 = key2.getSortNumber( level );
                if ( firstNumber1 != firstNumber2 ) {
                    return firstNumber1 - firstNumber2;
                }
                return compareTreeSortKeys( key1, key2, level+1 );
            } else if( !hasKeyOnThisLevel1 && !hasKeyOnThisLevel2) {
                return 0;
            } else if( hasKeyOnThisLevel1 ) {
                return 1;
            } else {
                return -1;
            }
        }
    }
}
