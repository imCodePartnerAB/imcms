package imcode.server.parser ;

import org.apache.oro.text.regex.* ;
import java.util.* ;

import org.apache.log4j.Category;

public class HashTagSubstitution implements Substitution {
	private final static String CVS_REV = "$Revision$" ;
	private final static String CVS_DATE = "$Date$" ;
	
    private static Pattern HASHTAGNUMBER_PATTERN  = null ;
	
	private static Category log = Category.getInstance("server");

    Properties tags ;
    Properties numberedtags ;
    Perl5Compiler patComp = new Perl5Compiler() ;

    static {
	Perl5Compiler patComp = new Perl5Compiler() ;

	try {
	    HASHTAGNUMBER_PATTERN = patComp.compile("(\\d+)#$", Perl5Compiler.READ_ONLY_MASK) ;
	
	} catch (MalformedPatternException ignored) {
	    // I ignore the exception because i know that these patterns work, and that the exception will never be thrown.
	    log.fatal("Danger, Will Robinson!",ignored) ;
	}
    }

    public HashTagSubstitution (Properties tags, Properties numberedtags) {
	this.tags = tags ;
	this.numberedtags = numberedtags ;
    }

    public void appendSubstitution( StringBuffer sb, MatchResult matres, int sc, PatternMatcherInput originalInput, PatternMatcher patMat, Pattern pat) {
	sb.append(hashTagHandler(patMat,patComp,tags,numberedtags)) ;
    }

    protected String hashTagHandler(PatternMatcher patMat, PatternCompiler patComp, Properties tags, Properties numberedtags) {
	MatchResult matres = patMat.getMatch() ;
	String tag = matres.group(0) ;
	String tagdata = tags.getProperty(tag) ;	// Get value of tag from hash
	if ( tagdata == null ) {
	    if (patMat.contains(tag,HASHTAGNUMBER_PATTERN) ) { // If the tag is of the form "#abc123#" (contains a number before the last hash)...
		String numbertag ;
		matres = patMat.getMatch() ;
		String tagnumber = matres.group(1) ; // Retrieve the number (123)
		String tagprefix = tag.substring(0,matres.beginOffset(0)) ; // Get the "#abc"-part
		String tagexp = tagprefix+"*#" ; // Make a string of the form "#abc*#"
		tagdata = tags.getProperty(tagexp) ; // Look for data for "#abc*#".
		if (tagdata == null) {
		    tagdata = "" ;
		} else if ( (numbertag = numberedtags.getProperty(tagexp))!=null ) {	// Is it a numbered tag which has data to insert the number in? (Are the first chars listed in "numberedtags"?) Get the tag to replace with the number.
		    String qm = Perl5Compiler.quotemeta(numbertag) ; // FIXME: Run quotemeta on them before putting them in numberedtags, instead of doing it every iteration.
		    try { // Replace the number in all the tagdata
			tagdata = org.apache.oro.text.regex.Util.substitute(patMat,patComp.compile(qm),new StringSubstitution(tagnumber),tagdata,org.apache.oro.text.regex.Util.SUBSTITUTE_ALL) ;
		    } catch (MalformedPatternException ex) {
			log.warn("Dynamic Pattern-compilation failed in HashTagSubstitution.hashTagHandler(). Suspected bug in jakarta-oro Perl5Compiler.quotemeta(). The String was '"+numbertag+"'",ex) ;
		    }
		}
	    } else {
		tagdata = "" ;
	    }
	}
	return tagdata ;
    }

}

