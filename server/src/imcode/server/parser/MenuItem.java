package imcode.server.parser ;


import java.util.Properties ;
import java.text.SimpleDateFormat ;
import org.apache.oro.text.regex.* ;

import org.apache.log4j.Category;

/** Stores all info about a menuitem **/
public class MenuItem extends Document implements imcode.server.IMCConstants {
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
       Parse this menuitem into a template with the correct tags.
    **/
    public Substitution getSubstitution(Properties parameters) {

	String image = getImage() ;
	image = (image != null && image.length() > 0) ? ("<img src=\""+image+"\" border=\"0\">") : "" ;
	String headline = getHeadline() ;
	if ( headline.length() == 0 ) {
	    headline = "&nbsp;" ;
	} else {
	    if ( !isActive() ) {
		headline = "<em><i>" + headline ;
		headline += "</i></em>" ;
	    }
	    if ( isArchived() ) {
		headline = "<strike>"+headline ;
		headline += "</strike>" ;
	    }
	}
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd") ;
	String createdDate = dateFormat.format(getCreatedDatetime()) ;
	String modifiedDate = dateFormat.format(getModifiedDatetime()) ;

	Properties tags = new Properties() ;
	tags.setProperty("#childMetaId#",""+getMetaId()) ;
	tags.setProperty("#childMetaHeadline#",headline) ;
	tags.setProperty("#childMetaText#",getText()) ;
	tags.setProperty("#childMetaImage#",image) ;
	tags.setProperty("#childCreatedDate#",createdDate) ;
	tags.setProperty("#childModifiedDate#",modifiedDate) ;
	tags.setProperty("#menuitemmetaid#",""+getMetaId()) ;
	tags.setProperty("#menuitemheadline#",headline) ;
	tags.setProperty("#menuitemtext#",getText()) ;
	tags.setProperty("#menuitemimage#",image) ;
	tags.setProperty("#menuitemtarget#",getTarget()) ;
	tags.setProperty("#menuitemdatecreated#",createdDate) ;
	tags.setProperty("#menuitemdatemodified#",modifiedDate) ;

	// If this doc is a file, we'll want to put in the filename
	// as an escaped translated path
	// For example: /servlet/GetDoc/filename.ext?meta_id=1234
	//                             ^^^^^^^^^^^^^
	String template = parameters.getProperty("template") ;
	String href = "GetDoc"+(getFilename() == null || getFilename().length() == 0 ? "" : "/"+java.net.URLEncoder.encode(getFilename()))+"?meta_id="+getMetaId()+(template!=null ? "&template="+java.net.URLEncoder.encode(template) : "");
	String a_href = "<a href=\""+href+(!"_self".equals(getTarget()) ? "\" target=\""+getTarget() : "")+"\">" ;

	tags.setProperty("#menuitemlinkonly#", a_href ) ;
	tags.setProperty("#/menuitemlinkonly#", "</a>") ;

	if ( getParentMenu().isMenuMode() ) {
	    if (getParentMenu().getSortOrder() == MENU_SORT_BY_MANUAL_ORDER) {
		a_href = "<input type=\"text\" name=\""+getMetaId()+"\" value=\""+getSortKey()+"\" size=\"4\" maxlength=\"4\">" + a_href ;
	    }
	    a_href = "<input type=\"checkbox\" name=\"archiveDelBox\" value=\""+getMetaId()+"\">" + a_href ;
	}

	tags.setProperty("#menuitemlink#", a_href ) ;
	tags.setProperty("#/menuitemlink#",
			 getParentMenu().isMenuMode() && isEditable()
			 ? "</a>&nbsp;<a href=\"AdminDoc?meta_id="+getMetaId()+"\"><img src=\""+getParentMenu().getImageUrl()+"txt.gif\" border=\"0\"></a>"
			 : "</a>") ;

	return new MapSubstitution(tags,true) ;
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
