package imcode.server.parser ;

public class Menu extends java.util.LinkedList {

    // todo: Replace inheritance with delegation

    private int sortOrder ;
    private boolean menuMode ;

    public Menu (int menuId, int sortOrder, boolean menumode, String imageUrl) {
        this.sortOrder = sortOrder ;
    	this.menuMode = menumode ;
    }

    /**
     * Gets the value of sortOrder
     *
     * @return the value of sortOrder
     */
    public int getSortOrder() {
	return this.sortOrder;
    }

    /**
     * Gets the value of menuMode
     *
     * @return the value of menuMode
     */
    public boolean isMenuMode() {
	return this.menuMode;
    }

}
