package imcode.server.parser ;

public class Menu extends java.util.LinkedList {
    private final static String CVS_REV = "$Revision$" ;
    private final static String CVS_DATE = "$Date$" ;

    private int sortOrder ;
    private int menuId ;
    private boolean menuMode ;
    private String imageUrl ;

    public Menu (int menuId, int sortOrder, boolean menumode, String imageUrl) {
	this.menuId = menuId ;
	this.sortOrder = sortOrder ;
	this.menuMode = menumode ;
	this.imageUrl = imageUrl ;
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
     * Gets the value of menuId
     *
     * @return the value of menuId
     */
    public int getMenuId() {
	return this.menuId;
    }

    /**
     * Gets the value of imageUrl
     *
     * @return the value of imageUrl
     */
    public String getImageUrl() {
	return this.imageUrl;
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
