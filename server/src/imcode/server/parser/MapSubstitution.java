package imcode.server.parser;

import org.apache.oro.text.regex.*;

import java.util.Map;

/**
 * This class is a generic Substitiution to be used with
 * the jakarta-oro regexp classes.
 * It uses a java.util.Map as a Substitution source.
 * This allows you to use a HashMap or some other Map for
 * the substitutions.
 * <p/>
 * Example:
 * <code>
 * // Create the matcher needed for the substitution
 * Perl5Matcher  matcher  = new Perl5Matcher()  ;
 * <p/>
 * // Create a pattern that will match words
 * Perl5Compiler compiler = new Perl5Compiler() ;
 * Pattern pattern = compiler.compile("\\b\\w+\\b") ;
 * <p/>
 * // Create and populate the map
 * HashMap map = new HashMap() ;
 * map.put("foo","bar") ;  // Replace 'foo' with 'bar'.
 * map put("baz","quux") ; // Replace 'baz' with 'quux'.
 * <p/>
 * // Create the MapSubstitution
 * MapSubstitution substitution = new MapSubstitution(map, false) ;
 * <p/>
 * // Create the input
 * String input = "foo bar baz quux" ;
 * <p/>
 * // Do the substitution
 * String output = Util.substitute(matcher, pattern, substitution, input) ;
 * <p/>
 * // 'output' will now contain "bar bar quux quux".
 * </code>
 */
public class MapSubstitution implements Substitution {

    private Map map;
    private boolean removeNulls;

    public MapSubstitution( Map map, boolean removeNulls ) {
        setMap( map, removeNulls );
    }

    private void setMap( Map map, boolean removeNulls ) {
        this.map = map;
        this.removeNulls = removeNulls;
    }

    public void appendSubstitution( StringBuffer sb, MatchResult matres, int sc, PatternMatcherInput originalInput,
                                    PatternMatcher patMat, Pattern pat ) {
        String match = matres.group( 0 );
        String replace = (String)map.get( match );
        if ( replace == null ) {
            if ( removeNulls ) {
                replace = "";
            } else {
                replace = match;
            }
        }
        sb.append( replace );
    }

}

