package imcode.server.parser ;

import org.apache.oro.text.regex.* ;

import org.apache.log4j.Category;
import imcode.server.document.Document;

/** Stores all info about a menuitem **/
public class MenuItem extends Document {
    private final static String CVS_REV = "$Revision$" ;
    private final static String CVS_DATE = "$Date$" ;

    private final static Category log = Category.getInstance( "server" );

    private boolean editable ;
    private int sortKey ;
    private Menu parentMenu;
    private static Pattern HASHTAG_PATTERN  = null ;

    static {
	Perl5Compiler patComp = new Perl5Compiler() ;

	try {
	    HASHTAG_PATTERN = patComp.compile("#[^#\"<>\\s]+#",Perl5Compiler.READ_ONLY_MASK) ;
	} catch (MalformedPatternException ignored) {
	    // I ignore the exception because i know that these patterns work, and that the exception will never be thrown.
	    log.fatal( "Danger, Will Robinson!",ignored ) ;
	}
    }

    public MenuItem (Menu parent) {

	this.parentMenu = parent ;
    }

    /**
     * Get the value of parentMenu.
     * @return value of parentMenu.
     */
    public Menu getParentMenu() {
	return parentMenu;
    }

    /**
     * Get the value of sortKey.
     * @return value of sortKey.
     */
    public int getSortKey() {
	return sortKey;
    }

    /**
     * Set the value of sortKey.
     * @param v  Value to assign to sortKey.
     */
    public void setSortKey(int  v) {
	this.sortKey = v;
    }


    /**
     * Get the value of editable.
     * @return value of editable.
     */
    public boolean isEditable() {
	return editable;
    }

    /**
     * Set the value of editable.
     * @param v  Value to assign to editable.
     */
    public void setEditable(boolean  v) {
	this.editable = v;
    }

}
