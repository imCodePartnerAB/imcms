package com.imcode.imcms.servlet;

import imcode.server.ApplicationServer;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentMapper;
import imcode.server.document.SectionDomainObject;
import imcode.server.document.index.DocumentIndex;
import imcode.server.user.UserDomainObject;
import imcode.util.HttpSessionUtils;
import imcode.util.Utility;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.TransformerUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SearchDocumentsPage {

    public static final String REQUEST_PARAMETER__SECTION_ID = "section_id";
    public static final String REQUEST_PARAMETER__DOCUMENTS_PER_PAGE = "num";
    public static final String REQUEST_PARAMETER__QUERY_STRING = "q";
    public static final String REQUEST_PARAMETER__FIRST_DOCUMENT_INDEX = "start";
    public static final String REQUEST_ATTRIBUTE__PAGE = "page";
    public static final String REQUEST_PARAMETER__SELECTED_DOCUMENT_ID = "select";
    public static final String REQUEST_PARAMETER__SEARCH_BUTTON = "search";

    private static final int DEFAULT_DOCUMENTS_PER_PAGE = 10;
    private final static Logger log = Logger.getLogger( SearchDocumentsPage.class.getName() );

    private SectionDomainObject section;
    private DocumentDomainObject[] documentsFound;
    private int firstDocumentIndex;
    private int documentsPerPage = DEFAULT_DOCUMENTS_PER_PAGE ;
    private String queryString;
    private DocumentDomainObject selectedDocument;
    private Query query;
    private boolean searchButtonPressed;

    static SearchDocumentsPage fromRequest( HttpServletRequest request ) {
        SearchDocumentsPage page = new SearchDocumentsPage();
        page.setFromRequest( request );
        return page;
    }

    private void setFromRequest( HttpServletRequest request ) {

        DocumentMapper documentMapper = ApplicationServer.getIMCServiceInterface().getDocumentMapper();

        DocumentFinder documentFinder = DocumentFinder.getInstance(request);

        if (documentFinder.isDocumentsSelectable()) {
            try {
                selectedDocument = documentMapper.getDocument( Integer.parseInt(request.getParameter( REQUEST_PARAMETER__SELECTED_DOCUMENT_ID )));
            } catch( NumberFormatException nfe ) {
            }
        }

        try {
            section = documentMapper.getSectionById( Integer.parseInt( request.getParameter( REQUEST_PARAMETER__SECTION_ID ) ) );
        } catch ( NumberFormatException nfe ) {
        }

        setDocumentsPerPage(NumberUtils.stringToInt( request.getParameter( REQUEST_PARAMETER__DOCUMENTS_PER_PAGE ), DEFAULT_DOCUMENTS_PER_PAGE ));
        firstDocumentIndex = Math.max( 0, NumberUtils.stringToInt( request.getParameter( REQUEST_PARAMETER__FIRST_DOCUMENT_INDEX ) ) );
        queryString = StringUtils.defaultString( request.getParameter( REQUEST_PARAMETER__QUERY_STRING ) );
        searchButtonPressed = null != request.getParameter( REQUEST_PARAMETER__SEARCH_BUTTON );
        query = createQuery();
    }

    public String getParameterString( HttpServletRequest request ) {
        return Utility.createQueryStringFromMap( getParameterMap(request) ) ;
    }

    public String getParameterStringWithParameter( HttpServletRequest request,
                                                 String parameterName, String parameterValue ) {
        Map parameters = getParameterMap(request) ;
        parameters.put( parameterName, parameterValue ) ;
        return Utility.createQueryStringFromMap( parameters ) ;
    }

    private Map getParameterMap( HttpServletRequest request ) {
        Map parameters = MapUtils.orderedMap( MapUtils.transformedMap( new HashMap(), TransformerUtils.nopTransformer(), new Transformer() {
            public Object transform( Object input ) {
                return new String[] { (String)input } ;
            }
        }) ) ;
        if (StringUtils.isNotBlank( queryString )) {
            parameters.put(REQUEST_PARAMETER__QUERY_STRING, queryString) ;
        }
        if (null != section) {
            parameters.put( REQUEST_PARAMETER__SECTION_ID, ""+section.getId()) ;
        }
        if (0 != firstDocumentIndex) {
            parameters.put( REQUEST_PARAMETER__FIRST_DOCUMENT_INDEX, ""+firstDocumentIndex) ;
        }
        if (DEFAULT_DOCUMENTS_PER_PAGE != documentsPerPage) {
            parameters.put( REQUEST_PARAMETER__DOCUMENTS_PER_PAGE, ""+documentsPerPage) ;
        }
        String documentFinderSessionAttributeName = HttpSessionUtils.getSessionAttributeNameFromRequest( request, DocumentFinder.REQUEST_ATTRIBUTE_OR_PARAMETER__DOCUMENT_FINDER);
        if (null != documentFinderSessionAttributeName) {
            parameters.put(DocumentFinder.REQUEST_ATTRIBUTE_OR_PARAMETER__DOCUMENT_FINDER, documentFinderSessionAttributeName) ;
        }
        return parameters;
    }

    private Query createQuery() {
        DocumentIndex index = ApplicationServer.getIMCServiceInterface().getDocumentMapper().getDocumentIndex();

        BooleanQuery query = new BooleanQuery();
        if ( StringUtils.isNotBlank( queryString ) ) {
            try {
                Query textQuery = index.parseLucene( queryString );
                query.add( textQuery, true, false );
            } catch ( ParseException e ) {
                log.warn( e.getMessage() + " in search-string " + queryString );
            }
        }

        if ( null != section ) {
            Query sectionQuery = new TermQuery( new Term( DocumentIndex.FIELD__SECTION, section.getName().toLowerCase() ) );
            query.add( sectionQuery, true, false );
        }

        if (0 == query.getClauses().length) {
            return null ;
        }
        return query;
    }

    void forward( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
        request.setAttribute( REQUEST_ATTRIBUTE__PAGE, this );
        UserDomainObject user = Utility.getLoggedOnUser( request );
        request.getRequestDispatcher( "/imcms/" + user.getLanguageIso639_2() + "/jsp/search_documents.jsp" ).forward( request, response );
    }

    public SectionDomainObject getSection() {
        return section;
    }

    public DocumentDomainObject[] getDocumentsFound() {
        return documentsFound;
    }

    public void setDocumentsFound( DocumentDomainObject[] documentsFound ) {
        this.documentsFound = documentsFound;
    }

    public int getFirstDocumentIndex() {
        return firstDocumentIndex;
    }

    public int getDocumentsPerPage() {
        return documentsPerPage;
    }

    public String getQueryString() {
        return queryString;
    }

    public DocumentDomainObject getSelectedDocument() {
        return selectedDocument;
    }

    public Query getQuery() {
        return query;
    }

    public boolean isSearchButtonPressed() {
        return searchButtonPressed;
    }

    private void setDocumentsPerPage( int documentsPerPage ) {
        if ( documentsPerPage <= 0 ) {
            documentsPerPage = DEFAULT_DOCUMENTS_PER_PAGE ;
        }
        this.documentsPerPage = documentsPerPage;
    }

}
