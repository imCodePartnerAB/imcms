package imcode.server.parser ;

import java.util.Map ;

import org.apache.oro.text.regex.* ;

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

    public void appendSubstitution( StringBuffer sb, MatchResult matres, int sc, String originalInput, PatternMatcher patMat, Pattern pat) {
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

