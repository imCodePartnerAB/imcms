package imcode.util ;

import java.util.* ;

import imcode.server.parser.* ;
import imcode.server.* ;
import imcode.util.* ;

import org.apache.oro.text.regex.* ;
import org.apache.log4j.Category;


public class ReadrunnerFilter {

    private static Category log = Category.getInstance("server");

    private static Pattern READRUNNER_FILTER_STOPSEP_PATTERN = null ;
    private static Pattern READRUNNER_FILTER_STOP_PATTERN = null ;
    private static Pattern READRUNNER_FILTER_SEP_PATTERN = null ;
    private static Pattern READRUNNER_FILTER_PATTERN = null ;
    private static Pattern HTML_ESCAPE_HIDE_PATTERN  = null ;
    private static Pattern HTML_ESCAPE_UNHIDE_PATTERN  = null ;

    private final Substitution HTML_ESCAPE_HIDE_SUBSTITUTION = new Perl5Substitution("_$1_") ;
    private final Substitution HTML_ESCAPE_UNHIDE_SUBSTITUTION = new Perl5Substitution("$1") ;

    private final ReadrunnerQuoteSubstitution readrunnerQuoteSubstitution = new ReadrunnerQuoteSubstitution() ;

    private class ReadrunnerQuoteSubstitution implements Substitution {
	private int readrunnerQuoteSubstitutionCount = 0 ;

	public void appendSubstitution(java.lang.StringBuffer appendBuffer,
				       MatchResult match,
				       int substitutionCount,
				       PatternMatcherInput originalInput,
				       PatternMatcher matcher,
				       Pattern pattern) {
	    appendBuffer.append("<q>") ;
	    appendBuffer.append(match.group(1)) ;
	    appendBuffer.append("</q>") ;
	    ++readrunnerQuoteSubstitutionCount ;
	}

	private int getReadrunnerQuoteSubstitutionCount() {
	    return readrunnerQuoteSubstitutionCount ;
	}
    } ;

    static {
	Perl5Compiler patComp = new Perl5Compiler() ;

	try {
	    final String READRUNNER_PATTERN_START = "([^\\s](?:.*?" ;
	    final String READRUNNER_PATTERN_END = "\\B\\S*|.*\\S))\\s*$?" ;

	    READRUNNER_FILTER_STOPSEP_PATTERN = patComp.compile(READRUNNER_PATTERN_START+"[.?!,:;\\\\/-]"+READRUNNER_PATTERN_END , Perl5Compiler.SINGLELINE_MASK|Perl5Compiler.READ_ONLY_MASK) ;
	    READRUNNER_FILTER_STOP_PATTERN    = patComp.compile(READRUNNER_PATTERN_START+"[.?!]"+READRUNNER_PATTERN_END, Perl5Compiler.SINGLELINE_MASK|Perl5Compiler.READ_ONLY_MASK) ;
	    READRUNNER_FILTER_SEP_PATTERN     = patComp.compile(READRUNNER_PATTERN_START+"[,:;\\\\/-]"+READRUNNER_PATTERN_END, Perl5Compiler.SINGLELINE_MASK|Perl5Compiler.READ_ONLY_MASK) ;
	    READRUNNER_FILTER_PATTERN         = patComp.compile("([^\\s].*?)\\s*$" , Perl5Compiler.SINGLELINE_MASK|Perl5Compiler.READ_ONLY_MASK) ;

	    HTML_ESCAPE_HIDE_PATTERN = patComp.compile("(&#?\\w+;)",Perl5Compiler.READ_ONLY_MASK) ;
	    HTML_ESCAPE_UNHIDE_PATTERN = patComp.compile("_(&#?\\w+;)_",Perl5Compiler.READ_ONLY_MASK) ;
	} catch (MalformedPatternException ignored) {
	    // I ignore the exception because i know that these patterns work, and that the exception will never be thrown.
	    log.fatal("Danger, Will Robinson!",ignored) ;
	}
    }

    public String filter(String text, PatternMatcher patMat, ReadrunnerParameters readrunnerParameters) {

	if (null == readrunnerParameters) {
	    readrunnerParameters = new ReadrunnerParameters(false, false) ;
	}
	org.apache.oro.text.perl.Perl5Util perl5util = new org.apache.oro.text.perl.Perl5Util() ;
	List firsttaglist = new ArrayList() ;

	// Split on <html-tags>
	perl5util.split(firsttaglist,"m!(<[^>]+?>)!",text) ;

	// Create a set of our linebreak-tags.
	Set linebreakTags = new HashSet() ;
	linebreakTags.add("a") ;
	linebreakTags.add("p") ;
	linebreakTags.add("h1") ;
	linebreakTags.add("h2") ;
	linebreakTags.add("h3") ;
	linebreakTags.add("h4") ;
	linebreakTags.add("h5") ;
	linebreakTags.add("h6") ;
	linebreakTags.add("br") ;
	linebreakTags.add("ul") ;
	linebreakTags.add("ol") ;
	linebreakTags.add("li") ;
	linebreakTags.add("div") ;

	List secondtaglist = new ArrayList() ;

	StringBuffer nextListItem = new StringBuffer() ;

	// Loop through all parts, html-tags and not.
	for (Iterator i = firsttaglist.iterator(); i.hasNext() ; ) {

	    String part = (String)i.next() ;

	    if (perl5util.match("m!</?(\\w+)[^>]*?>!", part)) {
		String tagName = perl5util.group(1).toLowerCase() ;

		if (linebreakTags.contains(tagName)) {
		    secondtaglist.add(nextListItem.toString()) ;
		    nextListItem.setLength(0) ;
		    secondtaglist.add(part) ;
		    continue ;
		}
	    }
	    nextListItem.append(part) ;
	}

	secondtaglist.add(nextListItem.toString()) ;

	// Set up a buffer for storing the result
	StringBuffer resultBuffer = new StringBuffer() ;

	// Loop through all parts, html-tags and not.
	for (Iterator i = secondtaglist.iterator(); i.hasNext() ; ) {

	    String part = (String)i.next() ;

	    // For each part, check if it looks like a html-tag.
	    if (!perl5util.match("/^<[^>]+?>$/",part)) {
		// Not a html-tag

		// Change all "&abc;" and "&#123;" to "_&abc;_" and "_&#123;_"
		part = org.apache.oro.text.regex.Util.substitute(patMat,
								 HTML_ESCAPE_HIDE_PATTERN,
								 HTML_ESCAPE_HIDE_SUBSTITUTION,
								 part,
								 org.apache.oro.text.regex.Util.SUBSTITUTE_ALL) ;


		// Choose a nice readrunner-pattern.
		Pattern readrunnerFilterPattern = null ;
		boolean useStopChars = readrunnerParameters.getUseStopChars() ;
		boolean useSepChars  = readrunnerParameters.getUseSepChars() ;

		if (useStopChars && useSepChars) {
		    readrunnerFilterPattern = READRUNNER_FILTER_STOPSEP_PATTERN ;
		} else if (useStopChars) {
		    readrunnerFilterPattern = READRUNNER_FILTER_STOP_PATTERN ;
		} else if (useSepChars) {
		    readrunnerFilterPattern = READRUNNER_FILTER_SEP_PATTERN ;
		} else {
		    readrunnerFilterPattern = READRUNNER_FILTER_PATTERN ;
		}

		// <q>Quote</q> all sentences.
		part = org.apache.oro.text.regex.Util.substitute(patMat,
								 readrunnerFilterPattern,
								 readrunnerQuoteSubstitution,
								 part,
								 org.apache.oro.text.regex.Util.SUBSTITUTE_ALL) ;

		// Change all "_&abc;_" and "_&#123;_" to "&abc;" and "&#123;"
		part = org.apache.oro.text.regex.Util.substitute(patMat,
								 HTML_ESCAPE_UNHIDE_PATTERN,
								 HTML_ESCAPE_UNHIDE_SUBSTITUTION,
								 part,
								 org.apache.oro.text.regex.Util.SUBSTITUTE_ALL) ;

	    }
	    resultBuffer.append(part) ;
	}

	if (resultBuffer.length()>0) {
	    resultBuffer.insert(0,"<div id='RR1'>") ;
	    resultBuffer.append("</div>") ;
	}

	return resultBuffer.toString() ;
    }

    public int getReadrunnerQuoteSubstitutionCount() {
	return readrunnerQuoteSubstitution.getReadrunnerQuoteSubstitutionCount() ;
    }


}
