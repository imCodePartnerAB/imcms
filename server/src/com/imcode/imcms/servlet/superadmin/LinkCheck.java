package com.imcode.imcms.servlet.superadmin;

import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.document.*;
import imcode.server.document.index.DocumentIndex;
import imcode.server.document.textdocument.ImageDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.document.textdocument.TextDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnection;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.oro.text.perl.Perl5Util;
import org.apache.oro.text.regex.PatternMatcherInput;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.*;

public class LinkCheck extends HttpServlet {

    private static final String USER_AGENT = "imCMS LinkCheck - http://www.imcms.net/";
    private static final String HTTP_REQUEST_HEADER__USER_AGENT = "user-agent";

    private static final int CONNECTION_TIMEOUT_MILLISECONDS = 5000;
    private static final int READ_TIMEOUT_MILLISECONDS = 5000;

    private final static Logger log = Logger.getLogger( LinkCheck.class.getName() );
    public static final String REQUEST_ATTRIBUTE__LINKS_ITERATOR = "linksIterator";
    public static final String REQUEST_PARAMETER__BROKEN_ONLY = "broken_only";

    public void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {

        UserDomainObject user = Utility.getLoggedOnUser( request );
        if ( !user.isSuperAdmin() ) {
            Utility.redirectToStartDocument( request, response );
            return;
        }

        List links = new ArrayList();
        ImcmsServices imcref = Imcms.getServices();
        DocumentMapper documentMapper = imcref.getDocumentMapper();
        DocumentIndex reindexingIndex = documentMapper.getDocumentIndex();
        addUrlDocumentLinks( links, reindexingIndex, user, request );
        addTextAndImageLinks( links, reindexingIndex, user, request );

        request.setAttribute( REQUEST_ATTRIBUTE__LINKS_ITERATOR, links.iterator() );

        LinkCheckPage linkCheckPage = new LinkCheckPage();
        linkCheckPage.setLinksIterator(links.iterator());
        linkCheckPage.setDoCheckLinks( null != request.getParameter(LinkCheckPage.REQUEST_PARAMETER__START_BUTTON) );
        linkCheckPage.setBrokenOnly( null != request.getParameter(REQUEST_PARAMETER__BROKEN_ONLY) );
        linkCheckPage.forward(request, response, user);

    }

    public static class LinkCheckPage implements Serializable {

        public static final String REQUEST_ATTRIBUTE__PAGE = "linkpage";
        public static final String REQUEST_PARAMETER__START_BUTTON = "start_check";
        boolean brokenOnly;
        boolean doCheckLinks;
        private Iterator linksIterator;

        public void forward( HttpServletRequest request, HttpServletResponse response, UserDomainObject user ) throws IOException, ServletException {
            putInRequest( request );
            String forwardPath = "/imcms/" + user.getLanguageIso639_2() + "/jsp/linkcheck/linkcheck.jsp";
            request.getRequestDispatcher( forwardPath ).forward( request, response );
        }

        public void putInRequest( HttpServletRequest request ) {
            request.setAttribute( REQUEST_ATTRIBUTE__PAGE, this );
        }
        public boolean isDoCheckLinks() {
            return doCheckLinks;
        }

        public void setDoCheckLinks(boolean doCheckLinks) {
            this.doCheckLinks = doCheckLinks;
        }

        public boolean isBrokenOnly() {
            return brokenOnly;
        }

        public void setBrokenOnly(boolean brokenOnly) {
            this.brokenOnly = brokenOnly;
        }

        public void setLinksIterator(Iterator linksIterator) {
            this.linksIterator = linksIterator;
        }

        public Iterator getLinksIterator() {
            return linksIterator;
        }
    }


    private void addUrlDocumentLinks( List links, DocumentIndex reindexingIndex, UserDomainObject user,
                                      HttpServletRequest request ) throws IOException {
        DocumentDomainObject[] urlDocuments = reindexingIndex.search( new TermQuery( new Term( DocumentIndex.FIELD__DOC_TYPE_ID, ""
                                                                                                                               + DocumentDomainObject.DOCTYPE_URL.getId() ) ), user );
        Arrays.sort( urlDocuments, DocumentComparator.ID );

        for ( int i = 0; i < urlDocuments.length; i++ ) {
            UrlDocumentDomainObject urlDocument = (UrlDocumentDomainObject)urlDocuments[i];
            Link link = new UrlDocumentLink( urlDocument, request );
            links.add( link );
        }
    }

    private void addTextAndImageLinks( List links, DocumentIndex reindexingIndex, UserDomainObject user,
                                       HttpServletRequest request ) throws IOException {
        BooleanQuery query = new BooleanQuery();
        query.add( new PrefixQuery( new Term( DocumentIndex.FIELD__TEXT, "http" ) ), false, false );
        query.add( new PrefixQuery( new Term( DocumentIndex.FIELD__TEXT, "href" ) ), false, false );
        query.add( new PrefixQuery( new Term( DocumentIndex.FIELD__IMAGE_LINK_URL, "http" ) ), false, false );

        DocumentDomainObject[] textDocuments = reindexingIndex.search( query, user );

        for ( int i = 0; i < textDocuments.length; i++ ) {
            TextDocumentDomainObject textDocument = (TextDocumentDomainObject)textDocuments[i];
            addTextLinks( links, textDocument, request );
            addImageLinks( links, textDocument, request );
        }
    }

    private void addTextLinks( List links, TextDocumentDomainObject textDocument, HttpServletRequest request ) {
        Map texts = textDocument.getTexts();
        for ( Iterator iterator = texts.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry entry = (Map.Entry)iterator.next();
            Integer textIndex = (Integer)entry.getKey();
            TextDomainObject text = (TextDomainObject)entry.getValue();
            String textString = text.getText();
            PatternMatcherInput patternMatcherInput = new PatternMatcherInput( textString );
            Perl5Util perl5Util = new Perl5Util();
            String urlWithoutSchemePattern = "[^\\s\"'()]+";
            while ( perl5Util.match( "m,\\bhttp://" + urlWithoutSchemePattern + "|\\bhref=[\"']?("
                                     + urlWithoutSchemePattern
                                     + "),i", patternMatcherInput ) ) {
                String url = perl5Util.group( 1 );
                if ( null == url ) {
                    url = perl5Util.group( 0 );
                }
                Link link = new TextLink( textDocument, textIndex.intValue(), url, request );
                links.add( link );
            }
        }
    }

    private void addImageLinks( List links, TextDocumentDomainObject textDocument, HttpServletRequest request ) {
        Map images = textDocument.getImages();
        for ( Iterator iterator = images.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry entry = (Map.Entry)iterator.next();
            Integer imageIndex = (Integer)entry.getKey();
            ImageDomainObject image = (ImageDomainObject)entry.getValue();
            String imageLinkUrl = image.getLinkUrl();
            if ( null != imageLinkUrl && imageLinkUrl.length() > 0 ) {
                Link link = new ImageLink( textDocument, imageIndex.intValue(), imageLinkUrl, request );
                links.add( link );
            }
        }
    }

    public abstract static class Link {

        private boolean ok;
        private boolean hostFound;
        private boolean hostReachable;
        private boolean checked;

        private HttpServletRequest request;

        public Link( HttpServletRequest request ) {
            this.request = request;
        }

        public abstract String getUrl();

        public boolean isHostFound() {
            check();
            return hostFound;
        }

        public boolean isHostReachable() {
            check();
            return hostReachable;
        }

        public boolean isOk() {
            check();
            return ok;
        }

        private void check() {
            if ( checked ) {
                return;
            }
            checked = true;
            checkUrl( getUrlToCheck() );
        }

        private String getUrlToCheck() {
            String url = getUrl();
            if ( !url.toLowerCase().startsWith( "http://" ) ) {
                String requestUrl = request.getRequestURL().toString();
                int requestUrlStartOfHost = requestUrl.indexOf( "://" ) + 3;
                int requestUrlStartOfPath = requestUrl.indexOf( '/', requestUrlStartOfHost );
                String requestUrlWithoutPath = StringUtils.left( requestUrl, requestUrlStartOfPath );
                if ( url.startsWith( "/" ) ) {
                    url = requestUrlWithoutPath + url;
                } else {
                    url = requestUrlWithoutPath + request.getContextPath() + "/servlet/" + url;
                }
            }
            return url;
        }

        private void checkUrl( String url ) {
            log.debug( "checkUrl(" + url + ")" );
            HttpClient httpClient = new HttpClient();
            httpClient.setConnectionTimeout( CONNECTION_TIMEOUT_MILLISECONDS );
            httpClient.setTimeout( READ_TIMEOUT_MILLISECONDS );
            GetMethod getMethod = null;
            try {
                getMethod = new GetMethod( url );
            } catch ( IllegalArgumentException e ) {
                return;
            }
            getMethod.setRequestHeader( HTTP_REQUEST_HEADER__USER_AGENT, USER_AGENT );
            try {
                int status = httpClient.executeMethod( getMethod );
                hostFound = true;
                hostReachable = true;
                getMethod.releaseConnection();
                if (HttpStatus.SC_OK == status) {
                    ok = true;
                }
            } catch ( IllegalArgumentException e ) {
            } catch ( UnknownHostException e ) {
            } catch ( HttpConnection.ConnectionTimeoutException e ) {
                hostFound = true;
            } catch ( ConnectException e ) {
                hostFound = true;
            } catch ( HttpException e ) {
                hostFound = true;
                hostReachable = true;
            } catch ( IOException e ) {
                log.warn( "Unknown IOException in LinkCheck.", e );
            }
        }

        public abstract DocumentDomainObject getDocument() ;
    }

    public final static class UrlDocumentLink extends Link {

        private UrlDocumentDomainObject urlDocument;
        private DocumentMapper.TextDocumentMenuIndexPair[] documentMenuPairsContainingUrlDocument;

        public UrlDocumentLink( UrlDocumentDomainObject urlDocument, HttpServletRequest request ) {
            super( request );
            this.urlDocument = urlDocument;
            documentMenuPairsContainingUrlDocument = Imcms.getServices().getDocumentMapper().getDocumentMenuPairsContainingDocument( urlDocument );
        }

        public String getUrl() {
            return urlDocument.getUrl();
        }

        public DocumentDomainObject getDocument() {
            return urlDocument;
        }

        public DocumentMapper.TextDocumentMenuIndexPair[] getDocumentMenuPairsContainingUrlDocument() {
            return documentMenuPairsContainingUrlDocument;
        }

    }

    public abstract static class TextDocumentElementLink extends Link {

        protected TextDocumentDomainObject textDocument;
        protected int index;
        protected String url;

        public TextDocumentElementLink(  TextDocumentDomainObject textDocument, int index, String url, HttpServletRequest request ) {
            super( request );
            this.textDocument = textDocument;
            this.index = index;
            this.url = url;
        }

        public String getUrl() {
            return url;
        }

        public int getIndex() {
            return index;
        }

        public DocumentDomainObject getDocument() {
            return textDocument;
        }

    }

    public static class TextLink extends TextDocumentElementLink {

        public TextLink( TextDocumentDomainObject textDocument, int index, String url,
                         HttpServletRequest request ) {
            super( textDocument, index, url, request );
        }

    }

    public static class ImageLink extends TextDocumentElementLink {

        public ImageLink( TextDocumentDomainObject textDocument, int index, String url,
                          HttpServletRequest request ) {
            super( textDocument, index, url, request );
        }

    }
}
