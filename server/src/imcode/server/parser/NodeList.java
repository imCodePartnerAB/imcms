package imcode.server.parser;

import org.apache.oro.text.regex.*;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedList;

class NodeList extends LinkedList {

    private static Pattern ELEMENT_PATTERN;

    static {
        try {
            Perl5Compiler patternCompiler = new Perl5Compiler();
            ELEMENT_PATTERN = patternCompiler.compile( "<\\?imcms:(\\w+)\\b(.*?)\\s*\\?>(.*?)<\\?\\/imcms:\\1\\s*\\?>", Perl5Compiler.SINGLELINE_MASK
                                                                                                                    | Perl5Compiler.READ_ONLY_MASK );
        } catch ( MalformedPatternException ignored ) {
            // I ignore the exception because i know that these patterns work, and that the exception will never be thrown.
        }
    }

    /**
     * Parse a String of data into nodes. *
     */
    NodeList( String data, HttpServletRequest request ) {
        PatternMatcher patternMatcher = new Perl5Matcher();
        PatternMatcherInput input = new PatternMatcherInput( data );
        int lastEndOffset = 0;
        while ( patternMatcher.contains( input, ELEMENT_PATTERN ) ) {
            MatchResult matchResult = patternMatcher.getMatch();
            if ( matchResult.beginOffset( 0 ) > lastEndOffset ) { // If the part before the first child element has a length longer than 0...
                add( new SimpleText( data.substring( lastEndOffset, matchResult.beginOffset( 0 ) ) ) ); // ... put it in a text node.
            }
            lastEndOffset = matchResult.endOffset( 0 );
            add( createElementNode( patternMatcher, request ) );
        }
        if ( data.length() > lastEndOffset ) { // Add whatever was left after the last element, and the whole piece if there wasn't any elements.
            add( new SimpleText( data.substring( lastEndOffset ) ) );
        }
    }

    private Element createElementNode( PatternMatcher patternMatcher, HttpServletRequest request ) {
        MatchResult matchResult = patternMatcher.getMatch();

        String name = matchResult.group( 1 );
        String attributes_string = matchResult.group( 2 );
        String content = matchResult.group( 3 );

        return new SimpleElement( name, TagParser.parseAttributes( attributes_string, patternMatcher, request ), new NodeList( content, request ) );
    }

}
