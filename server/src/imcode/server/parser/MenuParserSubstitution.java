package imcode.server.parser ;

import java.util.* ;
import org.apache.oro.text.regex.* ;
import imcode.util.log.* ;
import imcode.util.* ;

public class MenuParserSubstitution implements Substitution {
	private final static String CVS_REV = "$Revision$" ;
	private final static String CVS_DATE = "$Date$" ;

    private static Pattern HASHTAG_PATTERN  = null ;
    private static Pattern MENULOOP_PATTERN  = null ;
    private static Pattern MENUITEM_PATTERN  = null ;
    private static Pattern MENUITEMHIDE_PATTERN  = null ;
    private static Pattern MENUITEMHIDETAG_PATTERN  = null ;
    private static Pattern MENUITEMLINK_PATTERN  = null ;
    private static Pattern MENU_NO_PATTERN  = null ;

    private FileCache fileCache = new FileCache() ;

    Log log = Log.getLog("server") ;

    static {

	Perl5Compiler patComp = new Perl5Compiler() ;

	try {

	    MENULOOP_PATTERN = patComp.compile("<\\?imcms:menuloop\\?>(.*?)<\\?\\/imcms:menuloop\\?>", Perl5Compiler.SINGLELINE_MASK|Perl5Compiler.READ_ONLY_MASK) ;
	    MENUITEM_PATTERN = patComp.compile("<\\?imcms:menuitem\\?>(.*?)<\\?\\/imcms:menuitem\\?>", Perl5Compiler.SINGLELINE_MASK|Perl5Compiler.READ_ONLY_MASK) ;
	    MENUITEMHIDE_PATTERN = patComp.compile("<\\?imcms:menuitemhide\\?>(.*?)<\\?\\/imcms:menuitemhide\\?>", Perl5Compiler.SINGLELINE_MASK|Perl5Compiler.READ_ONLY_MASK) ;
	    MENUITEMHIDETAG_PATTERN = patComp.compile("<\\?\\/?imcms:menuitemhide\\?>", Perl5Compiler.READ_ONLY_MASK) ;
	    MENUITEMLINK_PATTERN = patComp.compile("<\\?imcms:menuitemlink\\?>(.*?)<\\?\\/imcms:menuitemlink\\?>", Perl5Compiler.READ_ONLY_MASK) ;
	    HASHTAG_PATTERN = patComp.compile("#[^#\"<> \\t\\r\\n]+#",Perl5Compiler.READ_ONLY_MASK) ;
	    MENU_NO_PATTERN = patComp.compile("#doc_menu_no#",Perl5Compiler.READ_ONLY_MASK) ;
	} catch (MalformedPatternException ignored) {
	    // I ignore the exception because i know that these patterns work, and that the exception will never be thrown.
	    Log log = Log.getLog("server") ;
	    log.log(Log.CRITICAL, "Danger, Will Robinson!") ;
	}
    }

    Map menus ;
    boolean menumode ;
    Properties tags ;
    int[] implicitMenus = {1} ;

    public MenuParserSubstitution (Map menus,boolean menumode, Properties tags ) {
	this.menumode = menumode ;
	this.menus = menus ;
	this.tags = tags ;
    }

    private LinkedList getMenuById(Map menus, int id) {
	return (LinkedList)menus.get(new Integer(id)) ;
    }

    private String getMenuModePrefix(PatternMatcher patMat, int menu_id, Properties tags) {
	String temp = tags.getProperty("addDoc") +
	    tags.getProperty("saveSortStart") ;

	return org.apache.oro.text.regex.Util.substitute(patMat,MENU_NO_PATTERN,new StringSubstitution(""+menu_id),temp,org.apache.oro.text.regex.Util.SUBSTITUTE_ALL) ;
    }

    private String getMenuModeSuffix(Properties tags) {
	return tags.getProperty("saveSortStop") ;
    }

    /**

       Invoked when you have found a block of data that is within menu-tags.

    */
    protected String menuParser (String input, PatternMatcher patMat,  Map menus, int[] implicitMenus, boolean menumode, Properties tags) {
	try {
	    MatchResult menuMatres = patMat.getMatch() ;
	    StringBuffer result = new StringBuffer() ; // FIXME: Optimize size?
	    // Get the id of the menu
	    int menu_id = 0 ;
	    try {
		menu_id = Integer.parseInt(menuMatres.group(1)) ;
	    } catch (NumberFormatException ex) {
		menu_id = implicitMenus[0]++ ;
	    }

	    // Get the linked list that is the menu
	    LinkedList currentMenu = getMenuById(menus,menu_id) ;
	    // Get the data between the menutags.
	    String menutemplate = menuMatres.group(2) ;
	    String menustarttemplate = "" , menustoptemplate = "" ;
	    String looptemplate ;
	    boolean looptags_present ;
	    // Check if the looptags are present
	    if (looptags_present = patMat.contains(menutemplate,MENULOOP_PATTERN)) {
		MatchResult menuloopMatres = patMat.getMatch() ;
		// Get the data between the looptags.
		looptemplate = menuloopMatres.group(1) ;
		menustarttemplate = menutemplate.substring(0,menuloopMatres.beginOffset(0)) ;
		menustoptemplate = menutemplate.substring(menuloopMatres.endOffset(0)) ;
	    } else {
		// No looptags are present. The whole menu will loop.
		looptemplate = menutemplate ;
	    }
	    // Create a list of menuitemtemplates
	    LinkedList menuitemtemplatelist = new LinkedList() ;
	    // Create a list of menuitemtemplate-betweens. (Everything between menuitemtemplate-tags.)
	    LinkedList menuitemtemplatebetweenlist = new LinkedList() ;

	    // Loop over the list and insert the menuitemtemplates
	    PatternMatcherInput pmin = new PatternMatcherInput(looptemplate) ;
	    int betweenindex = 0;
	    for (; patMat.contains(pmin, MENUITEM_PATTERN); betweenindex = patMat.getMatch().endOffset(0)) {
		MatchResult menuitemMatres = patMat.getMatch() ;
		String menuitemtemplatebetween = looptemplate.substring(betweenindex, menuitemMatres.beginOffset(0)) ; // Pick out the foo in "foo<?imcms:menuitem?>bar<?/imcms:menuitem?>baz".
		menuitemtemplatebetweenlist.add(menuitemtemplatebetween) ;
		String menuitemtemplate = menuitemMatres.group(1) ;  // Pick out the bar in "foo<?imcms:menuitem?>bar<?/imcms:menuitem?>baz".
		menuitemtemplatelist.add(menuitemtemplate) ;
	    }

	    if (menuitemtemplatelist.isEmpty()) { // Well, were there any menuitemtags present?
		menuitemtemplatelist.add(looptemplate) ; // No? Use the looptemplate. (Which will be the entire menu if the looptags are missing.)

		// menuitemtemplatelist now contains one element, menuitemtemplatebetweenlist none
	    } else {
		menuitemtemplatebetweenlist.add(looptemplate.substring(betweenindex)) ; // The were menuitemtags? Then pick out the baz in "foo<?imcms:menuitem?>bar<?/imcms:menuitem?>baz".

		// menuitemtemplatebetweenlist now contains one more element than menuitemtemplatelist
	    }

	    if (currentMenu != null && currentMenu.size() > 0) {
		// Now i begin outputting the results.
		result.append(menustarttemplate) ;
		// Create an iterator over the menuitemtemplates
		Iterator mitit = menuitemtemplatelist.iterator() ;
		Iterator mitb_it = menuitemtemplatebetweenlist.iterator() ;
		// Loop over the menus
		imcode.server.parser.MapSubstitution mapsubstitution = new imcode.server.parser.MapSubstitution() ;
		Substitution NULL_SUBSTITUTION = new StringSubstitution("") ;

		for (Iterator mit = currentMenu.iterator() ; mit.hasNext() ; ) {
		    
		    // Make sure we _loop_ over the templates.
		    if (!mitit.hasNext()) { // If we're out of menuitemtemplates in the loop
			mitit = menuitemtemplatelist.iterator() ; // Reset the iterator to the first menuitemtemplate. (A new iterator.)

			if (mitb_it.hasNext()) { // If we hade any menuitemtemplatebetween's there should be exactly one left...
			    result.append((String)mitb_it.next()) ; // append it.
			    mitb_it = menuitemtemplatebetweenlist.iterator() ; // And renew the iterator.
			}
		    }

		    String menuitemtemplate = (String)mitit.next() ;
		    Properties menuitemprops = (Properties)mit.next() ;
		    // Now i need to replace all tags in this template.
		    mapsubstitution.setMap(menuitemprops, true) ;
		    String menuitemresult = org.apache.oro.text.regex.Util.substitute(patMat,HASHTAG_PATTERN,mapsubstitution,menuitemtemplate,org.apache.oro.text.regex.Util.SUBSTITUTE_ALL) ;
		    // Since we wanted this menuitem, we don't need the menuitemhide-tags, so remove them.
		    menuitemresult = org.apache.oro.text.regex.Util.substitute(patMat,MENUITEMHIDETAG_PATTERN, NULL_SUBSTITUTION, menuitemresult,org.apache.oro.text.regex.Util.SUBSTITUTE_ALL) ;
		    if (mitb_it.hasNext()) { // If we have any menuitemtemplatebetween's...
			result.append((String)mitb_it.next()) ; // append one.
		    }
		    result.append(menuitemresult) ;
		}
		// If we still have menuitemtemplates left, loop over them, and hide everything that is supposed to be hidden.
		while (mitit.hasNext()) {
		    if (mitb_it.hasNext()) { // If we hade any menuitemtemplatebetween's...
			result.append((String)mitb_it.next()) ; // append one.
		    }
		    String menuitemresult = org.apache.oro.text.regex.Util.substitute(patMat,MENUITEMHIDE_PATTERN, NULL_SUBSTITUTION, (String)mitit.next(), org.apache.oro.text.regex.Util.SUBSTITUTE_ALL) ;
		    result.append(menuitemresult) ;
		}
		if (mitb_it.hasNext()) { // If we hade any menuitemtemplatebetween's there should be exactly one left...
		    result.append((String)mitb_it.next()) ; // append it.
		}
		result.append(menustoptemplate) ;
	    }
	    String resultstring = result.toString() ;
	    if (menumode) { // If in menumode, make sure to include all the stuff from the proper admintemplates.
		resultstring = getMenuModePrefix(patMat,menu_id,tags)+resultstring+getMenuModeSuffix(tags) ;
	    }
	    return resultstring ;
	} catch ( RuntimeException ex ) {
	    log.log(Log.ERROR, "Error during parsing.", ex) ;
	    return null ;
	}
    }



    public void appendSubstitution( StringBuffer sb, MatchResult matres, int sc, String originalInput, PatternMatcher patMat, Pattern pat) {
	sb.append(menuParser(originalInput,patMat,menus,implicitMenus,menumode,tags)) ;
    }

}
