package com.imcode.imcms.servlet.superadmin;

import junit.framework.*;
import org.apache.oro.text.perl.Perl5Util;
import org.apache.oro.text.regex.PatternMatcherInput;

public class TestLinkCheck extends TestCase {

    LinkCheck linkCheck = new LinkCheck() ;

    public void testMatchesUrl() throws Exception {
        Perl5Util perl5Util = new Perl5Util() ;
        String string = "<a href=\"http://test\">test</a>" ;
        PatternMatcherInput pmi = new PatternMatcherInput( string );
        assertTrue(linkCheck.matchesUrl(perl5Util, pmi)) ;
        assertEquals( "http://test", perl5Util.group(1) ) ;
    }
}
