package com.imcode.imcms.servlet;

import com.imcode.imcms.flow.*;
import com.imcode.imcms.servlet.superadmin.AdminManager;
import imcode.server.Imcms;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentMapper;
import imcode.server.document.SectionDomainObject;
import imcode.server.document.index.DocumentIndex;
import imcode.server.user.UserDomainObject;
import imcode.util.DateConstants;
import imcode.util.Utility;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.TransformerUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.document.DateField;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

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
    public static final String REQUEST_PARAMETER__DATE_TYPE = "date_type";
    public static final String REQUEST_PARAMETER__START_DATE = "start_date";
    public static final String REQUEST_PARAMETER__END_DATE = "end_date";
    public static final String REQUEST_PARAMETER__SORT_ORDER = "sort_order";

    private static final int DEFAULT_DOCUMENTS_PER_PAGE = 10;
    private final static Logger log = Logger.getLogger( SearchDocumentsPage.class.getName() );

    private String queryString;
    private SectionDomainObject section;
    private int[] statusIds;
    private int userDocumentsRestriction;
    private int dateTypeRestriction;
    private Date startDate;
    private Date endDate;
    private String sortOrder;

    private DocumentDomainObject[] documentsFound;
    private int firstDocumentIndex;
    private int documentsPerPage = DEFAULT_DOCUMENTS_PER_PAGE;
    private DocumentDomainObject selectedDocument;
    private DocumentDomainObject documentSelectedForEditing;
    private Query query;
    private boolean searchButtonPressed;

    DocumentFinder documentFinder;

    public static final String REQUEST_PARAMETER__STATUS = "status";

    public static final int USER_DOCUMENTS_RESTRICTION__NONE = 0;
    public static final int USER_DOCUMENTS_RESTRICTION__DOCUMENTS_CREATED_BY_USER = 1;
    public static final int USER_DOCUMENTS_RESTRICTION__DOCUMENTS_PUBLISHED_BY_USER = 2;
    public static final int USER_DOCUMENTS_RESTRICTION__DOCUMENTS_EDITABLE_BY_USER = 3;

    public static final int DATE_TYPE__PUBLICATION_START = 1;
    public static final int DATE_TYPE__PUBLICATION_END = 2;
    public static final int DATE_TYPE__CREATED = 3;
    public static final int DATE_TYPE__ARCHIVED = 4;
    public static final int DATE_TYPE__MODIFIED = 5;

    public SearchDocumentsPage() {
        super( null, null );
    }

    protected void updateFromRequest( HttpServletRequest request ) {

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

        statusIds = Utility.getParameterInts( request, REQUEST_PARAMETER__STATUS );

        String userDocumentsRestrictionParameter = request.getParameter( REQUEST_PARAMETER__PERMISSION );
        if ( null != userDocumentsRestrictionParameter ) {
            userDocumentsRestriction = Integer.parseInt( userDocumentsRestrictionParameter );
        }

        String dateTypeRestrictionParameter = request.getParameter( REQUEST_PARAMETER__DATE_TYPE );
        if ( null != dateTypeRestrictionParameter ) {
            dateTypeRestriction = Integer.parseInt( dateTypeRestrictionParameter );
        }

        DateFormat dateFormat = createDateFormat();
        String startDateParameter = request.getParameter( REQUEST_PARAMETER__START_DATE );
        if ( null != startDateParameter ) {
            try {
                startDate = dateFormat.parse( startDateParameter );
            } catch ( java.text.ParseException ignored ) {}
        }
        String endDateParameter = request.getParameter( REQUEST_PARAMETER__END_DATE );
        if ( null != endDateParameter ) {
            try {
                endDate = dateFormat.parse( endDateParameter );
            } catch ( java.text.ParseException ignored ) {}
        }

        sortOrder = request.getParameter( REQUEST_PARAMETER__SORT_ORDER ) ;
        if (null != sortOrder) {
            Comparator documentComparator = AdminManager.getComparator(sortOrder) ;
            documentFinder.setDocumentComparator(documentComparator) ;
        }

        setDocumentsPerPage( NumberUtils.stringToInt( request.getParameter( REQUEST_PARAMETER__DOCUMENTS_PER_PAGE ), DEFAULT_DOCUMENTS_PER_PAGE ) );
        firstDocumentIndex = Math.max( 0, NumberUtils.stringToInt( request.getParameter( REQUEST_PARAMETER__FIRST_DOCUMENT_INDEX ) ) );
        queryString = StringUtils.defaultString( request.getParameter( REQUEST_PARAMETER__QUERY_STRING ) );
        searchButtonPressed = null != request.getParameter( REQUEST_PARAMETER__SEARCH_BUTTON );

        query = createQuery( documentFinder, Utility.getLoggedOnUser( request ) );
    }

    private SimpleDateFormat createDateFormat() {
        return new SimpleDateFormat( DateConstants.DATE_FORMAT_STRING );
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
            Query statusQuery = new TermQuery( new Term( DocumentIndex.FIELD__STATUS, "" + statusId ) );
            statusQueries.add( statusQuery, false, false );
        }
        if ( statusIds.length > 0 ) {
            query.add( statusQueries, true, false );
        }

        if ( USER_DOCUMENTS_RESTRICTION__DOCUMENTS_CREATED_BY_USER == userDocumentsRestriction ) {
            Query createdByUserQuery = new TermQuery( new Term( DocumentIndex.FIELD__CREATOR_ID, "" + user.getId() ) );
            query.add( createdByUserQuery, true, false );
        }

        if ( USER_DOCUMENTS_RESTRICTION__DOCUMENTS_PUBLISHED_BY_USER == userDocumentsRestriction ) {
            Query publishedByUserQuery = new TermQuery( new Term( DocumentIndex.FIELD__PUBLISHER_ID, "" + user.getId() ) );
            query.add( publishedByUserQuery, true, false );
        }


        if ( null != startDate ) {
            String dateField ;
            switch (dateTypeRestriction) {
                case DATE_TYPE__PUBLICATION_END:
                    dateField = DocumentIndex.FIELD__PUBLICATION_END_DATETIME;
                    break;
                case DATE_TYPE__ARCHIVED:
                    dateField = DocumentIndex.FIELD__ARCHIVED_DATETIME;
                    break;
                case DATE_TYPE__CREATED:
                    dateField = DocumentIndex.FIELD__CREATED_DATETIME;
                    break;
                case DATE_TYPE__MODIFIED:
                    dateField = DocumentIndex.FIELD__MODIFIED_DATETIME;
                    break;
                case DATE_TYPE__PUBLICATION_START:
                default:
                    dateField = DocumentIndex.FIELD__PUBLICATION_START_DATETIME;
                    break;
            }

            Date calculatedEndDate = null == endDate
                                     ? new Date() : new Date( endDate.getTime() + DateUtils.MILLIS_IN_DAY );

            Term lowerTerm = new Term( dateField, DateField.dateToString( startDate ) );
            Term upperTerm = new Term( dateField, DateField.dateToString( calculatedEndDate ) );
            Query publicationStartedQuery = new RangeQuery( lowerTerm, upperTerm, true );
            query.add( publicationStartedQuery, true, false );
        }

        if ( 0 == query.getClauses().length ) {
            return null;
        }
        return query;
    }

    protected boolean wasCanceled( HttpServletRequest request ) {
        return documentFinder.isCancelable() && super.wasCanceled( request );
    }

    protected void dispatchCancel( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
        documentFinder.cancel( request, response );
    }

    protected void dispatchOther( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
        if ( null != getDocumentSelectedForEditing() ) {
            goToEditDocumentInformation( this, documentFinder, request, response );
        } else if ( null != getSelectedDocument() ) {
            documentFinder.selectDocument( getSelectedDocument(), request, response );
        } else {
            documentFinder.forwardWithPage( request, response, this );
        }
    }

    private void goToEditDocumentInformation( final SearchDocumentsPage page, final DocumentFinder documentFinder,
                                              HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
        DocumentDomainObject documentSelectedForEditing = page.getDocumentSelectedForEditing();
        DispatchCommand returnCommand = new DispatchCommand() {
            public void dispatch( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
                documentFinder.forwardWithPage( request, response, page );
            }
        };
        EditDocumentInformationPageFlow editDocumentInformationPageFlow = new EditDocumentInformationPageFlow( documentSelectedForEditing, returnCommand, new DocumentMapper.SaveEditedDocumentCommand() );
        editDocumentInformationPageFlow.setAdminButtonsHidden( true );
        editDocumentInformationPageFlow.dispatch( request, response );
    }

    public String getParameterString( HttpServletRequest request ) {
        return Utility.createQueryStringFromParameterMap( getParameterMap( request ) );
    }

    public String getParameterStringWithParameter( HttpServletRequest request,
                                                   String parameterName, String parameterValue ) {
        Map parameters = getParameterMap( request );
        parameters.put( parameterName, parameterValue );
        return Utility.createQueryStringFromParameterMap( parameters );
    }

    private Map getParameterMap( HttpServletRequest request ) {
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
        String pageSessionNameFromRequest = Page.getPageSessionNameFromRequest( request );
        if ( null != pageSessionNameFromRequest ) {
            parameters.put( Page.IN_REQUEST, pageSessionNameFromRequest );
        }
        return parameters;
    }

    public void forward( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
        UserDomainObject user = Utility.getLoggedOnUser( request );
        putInSessionAndForwardToPath( "/imcms/" + user.getLanguageIso639_2() + "/jsp/search_documents.jsp", request, response );
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

    public DocumentFinder getDocumentFinder() {
        return documentFinder;
    }

    public void setDocumentFinder( DocumentFinder documentFinder ) {
        this.documentFinder = documentFinder;
    }

    public int[] getStatusIds() {
        return statusIds;
    }

    public String getFormattedStartDate() {
        return formatDate( startDate );
    }

    public String getFormattedEndDate() {
        return formatDate( endDate );
    }

    private String formatDate( Date date ) {
        DateFormat dateFormat = createDateFormat();
        if ( null != date ) {
            return dateFormat.format( date );
        } else {
            return "";
        }
    }

    public int getDateTypeRestriction() {
        return dateTypeRestriction;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public int getUserDocumentsRestriction() {
        return userDocumentsRestriction;
    }

    private static class ToStringArrayTransformer implements Transformer {

        public Object transform( Object input ) {
            return new String[]{(String)input};
        }
    }
}
