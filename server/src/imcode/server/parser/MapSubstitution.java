package imcode.server.parser ;

import java.util.Map ;

import org.apache.oro.text.regex.* ;

/**

This class is a generic Substitiution to be used with
the jakarta-oro regexp classes.
It uses a java.util.Map as a Substitution source.
This allows you to use a HashMap or some other Map for
the substitutions.

Example:
<code>
// Create the matcher needed for the substitution
Perl5Matcher  matcher  = new Perl5Matcher()  ;

// Create a pattern that will match words
Perl5Compiler compiler = new Perl5Compiler() ;
Pattern pattern = compiler.compile("\\b\\w+\\b") ;

// Create and populate the map
HashMap map = new HashMap() ;
map.put("foo","bar") ;  // Replace 'foo' with 'bar'.
map put("baz","quux") ; // Replace 'baz' with 'quux'.

// Create the MapSubstitution
MapSubstitution substitution = new MapSubstitution(map, false) ;

// Create the input
String input = "foo bar baz quux" ;

// Do the substitution
String output = Util.substitute(matcher, pattern, substitution, input) ;

// 'output' will now contain "bar bar quux quux".
</code>

**/
public class MapSubstitution implements Substitution {
    private final static String CVS_REV = "$Revision$" ;
    private final static String CVS_DATE = "$Date$" ;

    Map map ;
    boolean removeNulls ;

    public MapSubstitution() {

    }

    public MapSubstitution(Map map, boolean removeNulls) {
	setMap(map,removeNulls) ;
    }

    public void setMap (Map map, boolean removeNulls) {
	this.map = map ;
	this.removeNulls = removeNulls ;
    }

    public Map getMap () {
	return map ;
    }

    public void appendSubstitution( StringBuffer sb, MatchResult matres, int sc, PatternMatcherInput originalInput, PatternMatcher patMat, Pattern pat) {
	String match = matres.group(0) ;
	String replace = (String)map.get(match) ;
	if (replace == null ) {
	    if (removeNulls) {
		replace = "" ;
	    } else {
		replace = match ;
	    }
	}
	sb.append(replace) ;
    }

}

