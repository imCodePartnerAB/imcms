package imcode.server.parser;

public class Menu extends java.util.LinkedList {

    // todo: Replace inheritance with delegation

    private int menuIndex;
    private boolean menuMode;
    private int sortOrder;

    public Menu( int menuIndex, boolean menumode, int sortOrder ) {
        this.menuIndex = menuIndex;
        this.menuMode = menumode;
        this.sortOrder = sortOrder ;
    }

    /**
     * Gets the value of menuMode
     *
     * @return the value of menuMode
     */
    public boolean isMenuMode() {
        return this.menuMode;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public int getMenuIndex() {
        return menuIndex;
    }
}
