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
        return new TreeKeyDomainObject( treeSortIndex );
    }
}
