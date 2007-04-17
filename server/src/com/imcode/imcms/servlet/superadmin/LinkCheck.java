package com.imcode.imcms.servlet.superadmin;

import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.document.*;
import com.imcode.imcms.mapping.DocumentMapper;
import imcode.server.document.index.DocumentIndex;
import imcode.server.document.index.SimpleDocumentQuery;
import imcode.server.document.textdocument.ImageDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.document.textdocument.TextDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.apache.commons.lang.math.IntRange;
import org.apache.commons.lang.math.NumberUtils;
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
import java.util.regex.Pattern;

public class LinkCheck extends HttpServlet {

    private static final String USER_AGENT = "imCMS LinkCheck - http://www.imcms.net/";
    private static final String HTTP_REQUEST_HEADER__USER_AGENT = "user-agent";

    private static final int CONNECTION_TIMEOUT_MILLISECONDS = 10000;
    private static final int READ_TIMEOUT_MILLISECONDS = 10000;

    private final static Logger log = Logger.getLogger( LinkCheck.class.getName() );

    public static final String REQUEST_PARAMETER__START_BUTTON = "start_check";
    public static final String REQUEST_PARAMETER__BROKEN_ONLY = "broken_only";
    public static final String REQUEST_PARAMETER__START_ID = "start_id";
    public static final String REQUEST_PARAMETER__END_ID = "end_id";

    public void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {

        UserDomainObject user = Utility.getLoggedOnUser( request );
        if ( !user.isSuperAdmin() ) {
            Utility.forwardToLogin( request, response );
            return;
        }

        List links = new ArrayList();
        ImcmsServices imcref = Imcms.getServices();
        DocumentMapper documentMapper = imcref.getDocumentMapper();
        DocumentIndex reindexingIndex = documentMapper.getDocumentIndex();

        int lowestDocumentId = documentMapper.getLowestDocumentId();
        int highestDocumentId = documentMapper.getHighestDocumentId();
        int startId = NumberUtils.stringToInt( request.getParameter( REQUEST_PARAMETER__START_ID ), lowestDocumentId );
        int endId =   NumberUtils.stringToInt( request.getParameter( REQUEST_PARAMETER__END_ID ),   highestDocumentId );
        IntRange range = new IntRange( startId,endId );
        range = new IntRange( Math.max(range.getMinimumInteger(), lowestDocumentId), Math.min(range.getMaximumInteger(), highestDocumentId));

        addUrlDocumentLinks( links, reindexingIndex, user, request, range );
        addTextAndImageLinks( links, reindexingIndex, user, request, range );
        Collections.sort( links, new Comparator() {
            public int compare( Object o1, Object o2 ) {
                Link l1 = (Link)o1 ;
                Link l2 = (Link)o2 ;
                return l1.getDocument().getId() - l2.getDocument().getId() ;
            }
        } );

        LinkCheckPage linkCheckPage = new LinkCheckPage();
        linkCheckPage.setLinksIterator( links.iterator() );
        boolean doCheckLinks = null != request.getParameter( REQUEST_PARAMETER__START_BUTTON );
        linkCheckPage.setDoCheckLinks( doCheckLinks );
        linkCheckPage.setStartId( range.getMinimumInteger() ) ;
        linkCheckPage.setEndId( range.getMaximumInteger() ) ;
        linkCheckPage.setBrokenOnly( null != request.getParameter( REQUEST_PARAMETER__BROKEN_ONLY ) );
        if ( doCheckLinks ) {
            Iterator iterator = links.iterator();
            for ( int i = 0; i < 10; ++i ) {
                new LinkCheckThread( iterator ).start();
            }
        }
        linkCheckPage.forward( request, response, user );

    }

    public static class LinkCheckThread extends Thread {

        private Iterator iterator;

        public LinkCheckThread( Iterator iterator ) {
            this.iterator = iterator;
        }

        public void run() {
            while ( iterator.hasNext() ) {
                try {
                    Link link = (Link)iterator.next();
                    link.check();
                } catch ( NoSuchElementException ignored ) {}
            }
        }
    }

    public static class LinkCheckPage implements Serializable {

        public static final String REQUEST_ATTRIBUTE__PAGE = "linkpage";

        boolean brokenOnly;
        boolean doCheckLinks;
        private Iterator linksIterator;
        private int startId;
        private int endId;

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

        public void setDoCheckLinks( boolean doCheckLinks ) {
            this.doCheckLinks = doCheckLinks;
        }

        public boolean isBrokenOnly() {
            return brokenOnly;
        }

        public void setBrokenOnly( boolean brokenOnly ) {
            this.brokenOnly = brokenOnly;
        }

        public void setLinksIterator( Iterator linksIterator ) {
            this.linksIterator = linksIterator;
        }

        public Iterator getLinksIterator() {
            return linksIterator;
        }

        public int getStartId() {
            return startId;
        }

        public int getEndId() {
            return endId;
        }

        public void setStartId( int startId ) {
            this.startId = startId;
        }

        public void setEndId( int endId ) {
            this.endId = endId;
        }
    }

    private void addUrlDocumentLinks( List links, DocumentIndex reindexingIndex, UserDomainObject user,
                                      HttpServletRequest request, IntRange range ) {
        TermQuery urlDocumentsQuery = new TermQuery( new Term( DocumentIndex.FIELD__DOC_TYPE_ID, ""
                                                                                     + DocumentTypeDomainObject.URL_ID ) );
        List urlDocuments = reindexingIndex.search( new SimpleDocumentQuery(urlDocumentsQuery), user );

        for ( Iterator iterator = urlDocuments.iterator(); iterator.hasNext(); ) {
            UrlDocumentDomainObject urlDocument = (UrlDocumentDomainObject) iterator.next();
            if ( !range.containsInteger( urlDocument.getId() )) {
                continue ;
            }
            Link link = new UrlDocumentLink( urlDocument, request );
            links.add( link );
        }
    }

    private void addTextAndImageLinks( List links, DocumentIndex reindexingIndex, UserDomainObject user,
                                       HttpServletRequest request, IntRange range ) {
        BooleanQuery query = new BooleanQuery();
        query.add( new PrefixQuery( new Term( DocumentIndex.FIELD__NONSTRIPPED_TEXT, "http" ) ), false, false );
        query.add( new PrefixQuery( new Term( DocumentIndex.FIELD__NONSTRIPPED_TEXT, "href" ) ), false, false );
        query.add( new PrefixQuery( new Term( DocumentIndex.FIELD__IMAGE_LINK_URL, "http" ) ), false, false );

        List textDocuments = reindexingIndex.search( new SimpleDocumentQuery(query), user );

        for ( Iterator iterator = textDocuments.iterator(); iterator.hasNext(); ) {
            TextDocumentDomainObject textDocument = (TextDocumentDomainObject) iterator.next();
            if (!range.containsInteger( textDocument.getId() )) {
                continue;
            }
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
            Perl5Util perl5Util = new Perl5Util();
            PatternMatcherInput patternMatcherInput = new PatternMatcherInput( textString );
            while ( matchesUrl( perl5Util, patternMatcherInput ) ) {
                String url = perl5Util.group( 1 );
                if ( null == url ) {
                    url = perl5Util.group( 0 );
                }
                Link link = new TextLink( textDocument, textIndex.intValue(), url, request );
                links.add( link );
            }
        }
    }

    boolean matchesUrl( Perl5Util perl5Util,
                                PatternMatcherInput patternMatcherInput ) {
        String urlWithoutSchemePattern = "[^\\s\"'()]+";
        return perl5Util.match( "m,\\bhttp://" + urlWithoutSchemePattern + "|\\bhref=[\"']?("
                                 + urlWithoutSchemePattern
                                 + "),i", patternMatcherInput );
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

        private boolean checkable = true ;

        private boolean ok;
        private boolean hostFound;
        private boolean hostReachable;
        private boolean checked;

        private HttpServletRequest request;

        public Link( HttpServletRequest request ) {
            this.request = request;
        }

        public abstract String getUrl();

        public boolean isCheckable() {
            check() ;
            return checkable;
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

        private synchronized void check() {
            if ( checked ) {
                return;
            }
            checked = true;
            checkUrl( fixSchemeLessUrl() );
        }

        public String fixSchemeLessUrl() {
            String url = getUrl();
            boolean hasScheme = Pattern.compile( "^(\\w+):" ).matcher( url ).find();
            if (!hasScheme) {
                String requestUrlWithoutPath = Utility.getRequestURLWithoutPath(request);
                if ( url.startsWith( "/" ) ) {
                    url = requestUrlWithoutPath + url;
                } else {
                    url = requestUrlWithoutPath + request.getContextPath() + "/" + url;
                }
            }
            return url;
        }

        private void checkUrl( String url ) {
            log.debug( "checkUrl(" + url + ")" );
            HttpClient httpClient = new HttpClient();
            httpClient.setConnectionTimeout( CONNECTION_TIMEOUT_MILLISECONDS );
            httpClient.setTimeout( READ_TIMEOUT_MILLISECONDS );
            HttpMethod httpMethod;
            try {
                httpMethod = new HeadMethod( url );
            } catch ( Exception e ) {
                checkable = false ;
                return;
            }
            httpMethod.setRequestHeader( HTTP_REQUEST_HEADER__USER_AGENT, USER_AGENT );
            try {
                int status = httpClient.executeMethod( httpMethod );
                hostFound = true;
                hostReachable = true;
                httpMethod.releaseConnection();
                if ( HttpStatus.SC_OK == status ) {
                    ok = true;
                }
            } catch ( IllegalArgumentException e ) {
                log.debug( "Error testing url " + url, e );
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

        public abstract DocumentDomainObject getDocument();
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

        public TextDocumentElementLink( TextDocumentDomainObject textDocument, int index, String url,
                                        HttpServletRequest request ) {
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
