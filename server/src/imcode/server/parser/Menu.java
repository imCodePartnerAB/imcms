package imcode.server.parser;

public class Menu extends java.util.LinkedList {

    // todo: Replace inheritance with delegation

    private boolean menuMode;
    private int sortOrder;

    public Menu( boolean menumode, int sortOrder ) {
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

}
