package imcode.server.parser ;

import imcode.util.log.* ;
import java.util.Properties ;
import org.apache.oro.text.regex.* ;

/** Stores all info about a menuitem **/
public class MenuItem extends Document implements imcode.server.IMCConstants {
    private final static String CVS_REV = "$Revision$" ;
    private final static String CVS_DATE = "$Date$" ;

    private int sortKey ;
    private Menu parentMenu;
    private static Pattern HASHTAG_PATTERN  = null ;

    Log log = Log.getLog("server") ;
    
    static {
	Perl5Compiler patComp = new Perl5Compiler() ;

	try {
	    HASHTAG_PATTERN = patComp.compile("#[^#\"<>\\s]+#",Perl5Compiler.READ_ONLY_MASK) ;
	} catch (MalformedPatternException ignored) {
	    // I ignore the exception because i know that these patterns work, and that the exception will never be thrown.
	    Log log = Log.getLog("server") ;
	    log.log(Log.CRITICAL, "Danger, Will Robinson!") ;
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

	Properties tags = new Properties() ;
	tags.setProperty("#childMetaId#",""+getMetaId()) ;
	tags.setProperty("#childMetaHeadline#",headline) ;
	tags.setProperty("#childMetaText#",getText()) ;
	tags.setProperty("#childMetaImage#",image) ;
	tags.setProperty("#childCreatedDate#",getCreatedDatetime().toString()) ;
	tags.setProperty("#childModifiedDate#",getModifiedDatetime().toString()) ;
	tags.setProperty("#menuitemmetaid#",""+getMetaId()) ;
	tags.setProperty("#menuitemheadline#",headline) ;
	tags.setProperty("#menuitemtext#",getText()) ;
	tags.setProperty("#menuitemimage#",image) ;
	tags.setProperty("#menuitemdatecreated#",getCreatedDatetime().toString()) ;
	tags.setProperty("#menuitemdatemodified#",getModifiedDatetime().toString()) ;

	// If this doc is a file, we'll want to put in the filename
	// as an escaped translated path
	// For example: /servlet/GetDoc/filename.ext?meta_id=1234
	//                             ^^^^^^^^^^^^^
	String template = parameters.getProperty("template") ;
	String href = "GetDoc"+(getFilename() == null || getFilename().length() == 0 ? "" : "/"+java.net.URLEncoder.encode(getFilename()))+"?meta_id="+getMetaId()+(template!=null ? "&template="+java.net.URLEncoder.encode(template) : "");
	String a_href = "<a href=\""+href+(!"_self".equals(getTarget()) ? "\" target=\""+getTarget() : "")+"\">" ;

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

}
