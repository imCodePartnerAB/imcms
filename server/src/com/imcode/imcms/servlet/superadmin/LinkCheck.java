package com.imcode.imcms.servlet.superadmin;

import imcode.server.ApplicationServer;
import imcode.server.IMCServiceInterface;
import imcode.server.document.*;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.document.textdocument.TextDomainObject;
import imcode.server.document.textdocument.ImageDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnection;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.oro.text.perl.Perl5Util;
import org.apache.oro.text.regex.PatternMatcherInput;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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

    public void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {

        UserDomainObject user = Utility.getLoggedOnUser( request );
        if ( !user.isSuperAdmin() ) {
            Utility.redirectToStartDocument( request, response );
            return;
        }
        List links = new ArrayList();
        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();
        DocumentMapper documentMapper = imcref.getDocumentMapper();
        DocumentIndex documentIndex = documentMapper.getDocumentIndex();

        addUrlDocumentLinks( links, documentIndex, user );
        addTextAndImageLinks( links, documentIndex, user, request );

        request.setAttribute( REQUEST_ATTRIBUTE__LINKS_ITERATOR, links.iterator() );

        request.getRequestDispatcher( "/imcms/" + user.getLanguageIso639_2() + "/jsp/linkcheck/linkcheck.jsp" ).forward( request, response );
    }

    private void addUrlDocumentLinks( List links, DocumentIndex documentIndex, UserDomainObject user ) throws IOException {
        DocumentDomainObject[] urlDocuments = documentIndex.search( new TermQuery( new Term( DocumentIndex.FIELD__DOC_TYPE_ID, ""
                                                                                                                               + DocumentDomainObject.DOCTYPE_URL ) ), user );
        Arrays.sort( urlDocuments, DocumentDomainObject.DocumentComparator.ID );

        for ( int i = 0; i < urlDocuments.length; i++ ) {
            UrlDocumentDomainObject urlDocument = (UrlDocumentDomainObject)urlDocuments[i];
            Link link = new UrlDocumentLink( urlDocument );
            links.add( link );
        }
    }

    private void addTextAndImageLinks( List links, DocumentIndex documentIndex, UserDomainObject user,
                                       HttpServletRequest request ) throws IOException {
        Query textUrlQuery = new PrefixQuery( new Term( DocumentIndex.FIELD__TEXT, "http" ) );
        Query imageLinkUrlQuery = new PrefixQuery( new Term( DocumentIndex.FIELD__IMAGE_LINK_URL, "http" ) );
        BooleanQuery query = new BooleanQuery();
        query.add( textUrlQuery, false, false );
        query.add( imageLinkUrlQuery, false, false );

        DocumentDomainObject[] textDocuments = documentIndex.search( query, user );

        for ( int i = 0; i < textDocuments.length; i++ ) {
            TextDocumentDomainObject textDocument = (TextDocumentDomainObject)textDocuments[i];
            addTextLinks( links, textDocument );
            addImageLinks( links, textDocument, request );
        }
    }

    private void addTextLinks( List links, TextDocumentDomainObject textDocument ) {
        Map texts = textDocument.getTexts();
        for ( Iterator iterator = texts.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry entry = (Map.Entry)iterator.next();
            Integer textIndex = (Integer)entry.getKey();
            TextDomainObject text = (TextDomainObject)entry.getValue();
            String textString = text.getText();
            PatternMatcherInput patternMatcherInput = new PatternMatcherInput( textString );
            Perl5Util perl5Util = new Perl5Util();
            while ( perl5Util.match( "m,http://[^\\s\"'()]+,i", patternMatcherInput ) ) {
                String url = perl5Util.group( 0 );
                Link link = new TextLink( textDocument, textIndex.intValue(), url );
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
        private boolean validUrl = true;

        public abstract String getUrl();

        public boolean isValidUrl() {
            check();
            return validUrl;
        }

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
            String url = getUrl();
            if ( !url.toLowerCase().startsWith( "http:" ) ) {
                url = "http://" + url;
                validUrl = false;
            }
            checkUrl( url );
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
                validUrl = false;
                return;
            }
            getMethod.setRequestHeader( HTTP_REQUEST_HEADER__USER_AGENT, USER_AGENT );
            try {
                httpClient.executeMethod( getMethod );
                hostFound = true;
                hostReachable = true;
                getMethod.getResponseBody();
                getMethod.releaseConnection();
                ok = true;
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
    }

    public final static class UrlDocumentLink extends Link {

        private UrlDocumentDomainObject urlDocument;
        private DocumentMapper.TextDocumentMenuIndexPair[] documentMenuPairsContainingUrlDocument;

        public UrlDocumentLink( UrlDocumentDomainObject urlDocument ) {
            this.urlDocument = urlDocument;
            documentMenuPairsContainingUrlDocument = ApplicationServer.getIMCServiceInterface().getDocumentMapper().getDocumentsMenuPairsContainingDocument(urlDocument) ;
        }

        public String getUrl() {
            return urlDocument.getUrl();
        }

        public UrlDocumentDomainObject getUrlDocument() {
            return urlDocument;
        }

        public DocumentMapper.TextDocumentMenuIndexPair[] getDocumentMenuPairsContainingUrlDocument() {
            return documentMenuPairsContainingUrlDocument ;
        }

    }

    public static class TextLink extends Link {

        private TextDocumentDomainObject textDocument;
        private int textIndex;
        private String url;

        public TextLink( TextDocumentDomainObject textDocument, int textIndex, String url ) {
            this.textDocument = textDocument;
            this.textIndex = textIndex;
            this.url = url;
        }

        public String getUrl() {
            return url;
        }

        public TextDocumentDomainObject getTextDocument() {
            return textDocument;
        }

        public int getTextIndex() {
            return textIndex;
        }
    }

    public static class ImageLink extends Link {

        private TextDocumentDomainObject textDocument;
        private int imageIndex;
        private String url;

        public ImageLink( TextDocumentDomainObject textDocument, int imageIndex, String url, HttpServletRequest request ) {
            this.textDocument = textDocument;
            this.imageIndex = imageIndex;
            if (!url.toLowerCase().startsWith("http://")) {
                String requestUrl = request.getRequestURL().toString();
                int requestUrlStartOfHost = requestUrl.indexOf("://")+3;
                int requestUrlStartOfPath = requestUrl.indexOf( '/', requestUrlStartOfHost ) ;
                url = StringUtils.left( requestUrl, requestUrlStartOfPath ) + request.getContextPath()+"/servlet/" + url ;
            }
            this.url = url;
        }

        public String getUrl() {
            return url;
        }

        public TextDocumentDomainObject getTextDocument() {
            return textDocument;
        }

        public int getImageIndex() {
            return imageIndex;
        }

    }
}
