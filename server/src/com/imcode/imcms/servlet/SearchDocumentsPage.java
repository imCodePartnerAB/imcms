package com.imcode.imcms.servlet;

import com.imcode.imcms.flow.DispatchCommand;
import com.imcode.imcms.flow.OkCancelPage;
import imcode.server.Imcms;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentMapper;
import imcode.server.document.SectionDomainObject;
import imcode.server.document.index.DocumentIndex;
import imcode.server.user.UserDomainObject;
import imcode.util.Html;
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

public class SearchDocumentsPage extends OkCancelPage {

    public static final String REQUEST_PARAMETER__SECTION_ID = "section_id";
    public static final String REQUEST_PARAMETER__DOCUMENTS_PER_PAGE = "num";
    public static final String REQUEST_PARAMETER__QUERY_STRING = "q";
    public static final String REQUEST_PARAMETER__FIRST_DOCUMENT_INDEX = "start";
    public static final String REQUEST_ATTRIBUTE__PAGE = "sp";
    public static final String REQUEST_PARAMETER__SELECTED_DOCUMENT_ID = "select";
    public static final String REQUEST_PARAMETER__TO_EDIT_DOCUMENT_ID = "toedit";
    public static final String REQUEST_PARAMETER__SEARCH_BUTTON = "search";
    public static final String REQUEST_PARAMETER__CANCEL_BUTTON = "cancel";
    public static final String REQUEST_PARAMETER__PERMISSION = "permission";

    private static final int DEFAULT_DOCUMENTS_PER_PAGE = 10;
    private final static Logger log = Logger.getLogger( SearchDocumentsPage.class.getName() );

    private SectionDomainObject section;
    private int[] statusIds;
    private int userDocumentsRestriction;

    private DocumentDomainObject[] documentsFound;
    private int firstDocumentIndex;
    private int documentsPerPage = DEFAULT_DOCUMENTS_PER_PAGE;
    private String queryString;
    private DocumentDomainObject selectedDocument;
    private DocumentDomainObject documentSelectedForEditing;
    private Query query;
    private boolean searchButtonPressed;
    private boolean cancelButtonPressed;

    private String documentFinderSessionAttributeName;
    DocumentFinder documentFinder ;

    private static final String REQUEST_ATTRIBUTE_OR_PARAMETER__DOCUMENT_FINDER = "finder";
    public static final String REQUEST_PARAMETER__STATUS = "status";

    public static final int USER_DOCUMENTS_RESTRICTION__NONE = 0 ;
    public static final int USER_DOCUMENTS_RESTRICTION__DOCUMENTS_CREATED_BY_USER = 1;
    public static final int USER_DOCUMENTS_RESTRICTION__DOCUMENTS_PUBLISHED_BY_USER = 2;
    public static final int USER_DOCUMENTS_RESTRICTION__DOCUMENTS_MODIFIABLE_BY_USER = 3;

    public SearchDocumentsPage(DispatchCommand okDispatchCommand, DispatchCommand cancelDispatchCommand) {
        super(okDispatchCommand, cancelDispatchCommand);
    }

    protected void updateFromRequest(HttpServletRequest request) {

        setDocumentFinderFromRequest( request );

        if ( documentFinder.isCancelable() ) {
            cancelButtonPressed = null != request.getParameter( REQUEST_PARAMETER__CANCEL_BUTTON );
        }

        DocumentMapper documentMapper = Imcms.getServices().getDocumentMapper();

        try {
            documentSelectedForEditing = documentMapper.getDocument( Integer.parseInt( request.getParameter( REQUEST_PARAMETER__TO_EDIT_DOCUMENT_ID ) ) );
        } catch ( NumberFormatException nfe ) {
        }

        if ( documentFinder.isDocumentsSelectable() ) {
            try {
                selectedDocument = documentMapper.getDocument( Integer.parseInt( request.getParameter( REQUEST_PARAMETER__SELECTED_DOCUMENT_ID ) ) );
            } catch ( NumberFormatException nfe ) {
            }
        }

        try {
            section = documentMapper.getSectionById( Integer.parseInt( request.getParameter( REQUEST_PARAMETER__SECTION_ID ) ) );
        } catch ( NumberFormatException nfe ) {
        }

        statusIds = Utility.getParameterInts( request, REQUEST_PARAMETER__STATUS ) ;

        userDocumentsRestriction = Integer.parseInt( request.getParameter( REQUEST_PARAMETER__PERMISSION ) ) ;

        setDocumentsPerPage( NumberUtils.stringToInt( request.getParameter( REQUEST_PARAMETER__DOCUMENTS_PER_PAGE ), DEFAULT_DOCUMENTS_PER_PAGE ) );
        firstDocumentIndex = Math.max( 0, NumberUtils.stringToInt( request.getParameter( REQUEST_PARAMETER__FIRST_DOCUMENT_INDEX ) ) );
        queryString = StringUtils.defaultString( request.getParameter( REQUEST_PARAMETER__QUERY_STRING ) );
        searchButtonPressed = null != request.getParameter( REQUEST_PARAMETER__SEARCH_BUTTON );

        query = createQuery( documentFinder, Utility.getLoggedOnUser( request ) );
    }

    protected void dispatchOther(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        documentFinder.forwardWithPage(request, response, this);
    }

    private void setDocumentFinderFromRequest( HttpServletRequest request ) {
        documentFinderSessionAttributeName = HttpSessionUtils.getSessionAttributeNameFromRequest( request, REQUEST_ATTRIBUTE_OR_PARAMETER__DOCUMENT_FINDER );
        DocumentFinder documentFinderFromSession = (DocumentFinder)HttpSessionUtils.getSessionAttributeWithNameInRequest( request, REQUEST_ATTRIBUTE_OR_PARAMETER__DOCUMENT_FINDER) ;
        if ( null != documentFinderFromSession ) {
            documentFinder = documentFinderFromSession;
        } else {
            documentFinderSessionAttributeName = null ;
        }
    }

    public String getParameterString( HttpServletRequest request ) {
        return Utility.createQueryStringFromParameterMap( getParameterMap() );
    }

    public String getParameterStringWithParameter( HttpServletRequest request,
                                                   String parameterName, String parameterValue ) {
        Map parameters = getParameterMap();
        parameters.put( parameterName, parameterValue );
        return Utility.createQueryStringFromParameterMap( parameters );
    }

    private Map getParameterMap() {
        Map parameters = MapUtils.orderedMap( MapUtils.transformedMap( new HashMap(), TransformerUtils.nopTransformer(), new ToStringArrayTransformer() ) );
        if ( StringUtils.isNotBlank( queryString ) ) {
            parameters.put( REQUEST_PARAMETER__QUERY_STRING, queryString );
        }
        if ( null != section ) {
            parameters.put( REQUEST_PARAMETER__SECTION_ID, "" + section.getId() );
        }
        if ( 0 != firstDocumentIndex ) {
            parameters.put( REQUEST_PARAMETER__FIRST_DOCUMENT_INDEX, "" + firstDocumentIndex );
        }
        if ( DEFAULT_DOCUMENTS_PER_PAGE != documentsPerPage ) {
            parameters.put( REQUEST_PARAMETER__DOCUMENTS_PER_PAGE, "" + documentsPerPage );
        }
        if ( null != documentFinderSessionAttributeName ) {
            parameters.put( REQUEST_ATTRIBUTE_OR_PARAMETER__DOCUMENT_FINDER, documentFinderSessionAttributeName );
        }
        return parameters;
    }

    private Query createQuery( DocumentFinder documentFinder, UserDomainObject user ) {

        BooleanQuery query = new BooleanQuery();
        if ( StringUtils.isNotBlank( queryString ) ) {
            try {
                Query textQuery = documentFinder.parse( queryString );
                query.add( textQuery, true, false );
            } catch ( ParseException e ) {
                log.debug( e.getMessage() + " in search-string " + queryString, e );
            }
        }

        if ( null != section ) {
            Query sectionQuery = new TermQuery( new Term( DocumentIndex.FIELD__SECTION, section.getName().toLowerCase() ) );
            query.add( sectionQuery, true, false );
        }

        BooleanQuery statusQueries = new BooleanQuery();
        for ( int i = 0; i < statusIds.length; i++ ) {
            int statusId = statusIds[i];
            Query statusQuery = new TermQuery( new Term( DocumentIndex.FIELD__STATUS, ""+statusId) ) ;
            statusQueries.add( statusQuery, false, false ) ;
        }
        if (statusIds.length > 0) {
            query.add( statusQueries, true, false ) ;
        }

        if (USER_DOCUMENTS_RESTRICTION__DOCUMENTS_CREATED_BY_USER == userDocumentsRestriction) {
            Query createdByUserQuery = new TermQuery( new Term( DocumentIndex.FIELD__CREATOR_ID, ""+user.getId())) ;
            query.add(createdByUserQuery, true, false) ;
        }

        if ( USER_DOCUMENTS_RESTRICTION__DOCUMENTS_PUBLISHED_BY_USER == userDocumentsRestriction ) {
            Query publishedByUserQuery = new TermQuery( new Term( DocumentIndex.FIELD__PUBLISHER_ID, "" + user.getId() ) );
            query.add( publishedByUserQuery, true, false );
        }

        if ( 0 == query.getClauses().length ) {
            return null;
        }
        return query;
    }

    public void forward(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        UserDomainObject user = Utility.getLoggedOnUser( request );
        putInSessionAndForwardToPath("/imcms/" + user.getLanguageIso639_2() + "/jsp/search_documents.jsp", request, response );
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

    public DocumentDomainObject getDocumentSelectedForEditing() {
        return documentSelectedForEditing;
    }

    public Query getQuery() {
        return query;
    }

    public boolean isSearchButtonPressed() {
        return searchButtonPressed;
    }

    private void setDocumentsPerPage( int documentsPerPage ) {
        if ( documentsPerPage <= 0 ) {
            documentsPerPage = DEFAULT_DOCUMENTS_PER_PAGE;
        }
        this.documentsPerPage = documentsPerPage;
    }

    public boolean isCancelButtonPressed() {
        return cancelButtonPressed;
    }

    public String getDocumentFinderHiddenInputHtml() {
        if ( null == documentFinderSessionAttributeName ) {
            return "";
        }
        return Html.hidden( REQUEST_ATTRIBUTE_OR_PARAMETER__DOCUMENT_FINDER, documentFinderSessionAttributeName );
    }

    public DocumentFinder getDocumentFinder() {
        return documentFinder;
    }

    public void setDocumentFinder( DocumentFinder documentFinder ) {
        documentFinderSessionAttributeName = HttpSessionUtils.createUniqueNameForObject(documentFinder) ;
        this.documentFinder = documentFinder;
    }

    public int[] getStatusIds() {
        return statusIds;
    }

    private static class ToStringArrayTransformer implements Transformer {

        public Object transform( Object input ) {
            return new String[]{(String)input};
        }
    }
}
