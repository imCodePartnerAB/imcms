package imcode.server.parser ;

import java.util.LinkedList ;
import java.util.Properties ;
import org.apache.oro.text.regex.* ;

public class NodeList extends LinkedList {
    final static String CVS_REV = "$Revision$" ;
    final static String CVS_DATE = "$Date$" ;

    private static Pattern ELEMENT_PATTERN ;
    private static Pattern ATTRIBUTES_PATTERN ;
    static {
	try {
	    Perl5Compiler patternCompiler = new Perl5Compiler() ;
	    ELEMENT_PATTERN = patternCompiler.compile("<\\?imcms:([-\\w]+)(.*?)\\s*\\?>(.*?)<\\?\\/imcms:\\1\\?>", Perl5Compiler.SINGLELINE_MASK|Perl5Compiler.READ_ONLY_MASK) ;
	    ATTRIBUTES_PATTERN = patternCompiler.compile("\\s+(\\w+)\\s*=\\s*([\"'])(.*?)\\2", Perl5Compiler.READ_ONLY_MASK) ;
	} catch (MalformedPatternException ignored) {
	    // I ignore the exception because i know that these patterns work, and that the exception will never be thrown.
	}
    }
	
    /** Parse a String of data into nodes. **/
    public NodeList(String data) {
	PatternMatcher patternMatcher = new Perl5Matcher() ;
	PatternMatcherInput input = new PatternMatcherInput(data) ;
	int lastEndOffset = 0 ;
	while (patternMatcher.contains(input,ELEMENT_PATTERN)) {
	    MatchResult matchResult = patternMatcher.getMatch() ;
	    if (matchResult.beginOffset(0) > lastEndOffset) { // If the part before the first child element has a length longer than 0...
		add( new SimpleText( data.substring( lastEndOffset,matchResult.beginOffset(0) ) ) ) ; // ... put it in a text node.
	    }
	    lastEndOffset = matchResult.endOffset(0) ;
	    add( createElementNode( patternMatcher ) ) ;
	}
	if (data.length() > lastEndOffset) { // Add whatever was left after the last element, no matter if there were any elements.
	    add( new SimpleText( data.substring(lastEndOffset) ) ) ;
	}
    }
   
    private Element createElementNode( PatternMatcher patternMatcher ) {
	MatchResult matchResult = patternMatcher.getMatch() ;

	String name = matchResult.group(1) ;
	String attributes_string = matchResult.group(2) ;
	String content = matchResult.group(3) ;

	return new SimpleElement(name, createAttributes(attributes_string, patternMatcher), new NodeList(content)) ;
	
    }

    Properties createAttributes(String attributes_string, PatternMatcher patternMatcher) {
	Properties attributes = new Properties();

	PatternMatcherInput attributes_input = new PatternMatcherInput(attributes_string) ;
	while(patternMatcher.contains(attributes_input,ATTRIBUTES_PATTERN)) {
	    MatchResult attribute_matres = patternMatcher.getMatch() ;
	    attributes.setProperty(attribute_matres.group(1), attribute_matres.group(3)) ;
	}

	return attributes ;
    }

}
