package imcode.server.parser ;

import org.apache.oro.text.regex.* ;
import java.util.* ;

import imcode.util.log.* ;

public class HashTagSubstitution implements Substitution {
	
    private static Pattern HASHTAGNUMBER_PATTERN  = null ;

    Properties tags ;
    Properties numberedtags ;
    Perl5Compiler patComp = new Perl5Compiler() ;
    Log log = Log.getLog("server") ;

    static {
	Perl5Compiler patComp = new Perl5Compiler() ;

	try {
	    HASHTAGNUMBER_PATTERN = patComp.compile("(\\d+)#$", Perl5Compiler.READ_ONLY_MASK) ;
	
	} catch (MalformedPatternException ignored) {
	    // I ignore the exception because i know that these patterns work, and that the exception will never be thrown.
	    Log log = Log.getLog("server") ;
	    log.log(Log.CRITICAL, "Danger, Will Robinson!") ;
	}
    }

    public HashTagSubstitution (Properties tags, Properties numberedtags) {
	this.tags = tags ;
	this.numberedtags = numberedtags ;
    }

    public void appendSubstitution( StringBuffer sb, MatchResult matres, int sc, String originalInput, PatternMatcher patMat, Pattern pat) {
	sb.append(hashTagHandler(patMat,patComp,tags,numberedtags)) ;
    }

    protected String hashTagHandler(PatternMatcher patMat, PatternCompiler patComp, Properties tags, Properties numberedtags) {
	MatchResult matres = patMat.getMatch() ;
	String tag = matres.group(0) ;
	String tagdata = tags.getProperty(tag) ;	// Get value of tag from hash
	if ( tagdata == null ) {
	    if (patMat.contains(tag,HASHTAGNUMBER_PATTERN) ) {
		String numbertag ;
		matres = patMat.getMatch() ;
		String tagnumber = matres.group(1) ;
		String tagexp = tag.substring(0,matres.beginOffset(0))+"*#" ;
		tagdata = tags.getProperty(tagexp) ;
		if (tagdata == null) {
		    tagdata = "" ;
		} else if ( (numbertag = numberedtags.getProperty(tagexp))!=null ) {	// Is it a numbered tag which has data to insert the number in? (Is the four first chars listed in "numberedtags"?) Get the tag to replace with the number.
		    String qm = Perl5Compiler.quotemeta(numbertag) ; // FIXME: Run quotemeta on them before putting them in numberedtags, instead of doing it every iteration.
		    try {
			tagdata = org.apache.oro.text.regex.Util.substitute(patMat,patComp.compile(qm),new StringSubstitution(tagnumber),tagdata,org.apache.oro.text.regex.Util.SUBSTITUTE_ALL) ;
		    } catch (MalformedPatternException ex) {
			log.log(Log.WARNING, "Dynamic Pattern-compilation failed in IMCService.hashTagHandler(). Suspected bug in jakarta-oro Perl5Compiler.quotemeta(). The String was '"+numbertag+"'",ex) ;
		    }
		}
	    } else {
		tagdata = "" ;
	    }
	}
	return tagdata ;
    }
    
}

