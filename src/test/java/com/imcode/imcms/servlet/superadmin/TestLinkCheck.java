package com.imcode.imcms.servlet.superadmin;

//import test.mock.MockHttpServletRequest;

import imcode.server.document.textdocument.TextDocumentDomainObject;
import junit.framework.TestCase;

import org.apache.oro.text.perl.Perl5Util;
import org.apache.oro.text.regex.PatternMatcherInput;

public class TestLinkCheck extends TestCase {

//    LinkCheck linkCheck = new LinkCheck() ;
//
//    public void testMatchesUrl() throws Exception {
//        Perl5Util perl5Util = new Perl5Util() ;
//        String string = "<a href=\"http://test\">test</a>" ;
//        PatternMatcherInput pmi = new PatternMatcherInput( string );
//        assertTrue(linkCheck.matchesUrl(perl5Util, pmi)) ;
//        assertEquals( "http://test", perl5Util.group(1) ) ;
//    }
//
//    public void testFixSchemelessUrlWithFtpUrl() {
//        MockHttpServletRequest request = new MockHttpServletRequest();
//        request.setupRequestURL("http://localhost/imcms/servlet/LinkCheck") ;
//        request.setupContextPath("/imcms") ;
//        String url = "ftp://ftp.example.invalid";
//        LinkCheck.Link link = new LinkCheck.TextLink(new TextDocumentDomainObject(), 1, url, request ) ;
//        assertEquals(url, link.fixSchemeLessUrl()) ;
//    }
}
