package imcode.server.parser;

import imcode.server.document.DocumentDomainObject;

/** Stores all info about a menuitem **/
public class MenuItem extends DocumentDomainObject {

// todo: Replace inheritance with delegation

    private boolean editable;
    private int sortKey;
    private String treeSortKey;
    private Menu parentMenu;

    MenuItem(Menu parent) {
        this.parentMenu = parent;
    }

    /**
     * Get the value of parentMenu.
     *
     * @return value of parentMenu.
     */
    public Menu getParentMenu() {
        return parentMenu;
    }

    /**
     * Get the value of sortKey.
     * 
     * @return value of sortKey.
     */
    public int getSortKey() {
        return sortKey;
    }

    /**
     * Set the value of sortKey.
     * 
     * @param v Value to assign to sortKey.
     */
    public void setSortKey(int v) {
        this.sortKey = v;
    }


    /**
     * Get the value of editable.
     * 
     * @return value of editable.
     */
    public boolean isEditable() {
        return editable;
    }

    /**
     * Set the value of editable.
     * 
     * @param v Value to assign to editable.
     */
    public void setEditable(boolean v) {
        this.editable = v;
    }

    public String getTreeSortKey() {
        return treeSortKey;
    }

    public void setTreeSortKey(String treeSortKey) {
        this.treeSortKey = treeSortKey;
    }

}
