package com.imcode.imcms.servlet;

import com.imcode.imcms.flow.DispatchCommand;
import com.imcode.imcms.flow.EditDocumentInformationPageFlow;
import com.imcode.imcms.flow.OkCancelPage;
import com.imcode.imcms.flow.Page;
import com.imcode.imcms.mapping.DocumentMapper;
import com.imcode.imcms.servlet.superadmin.AdminManager;
import imcode.server.Imcms;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.SectionDomainObject;
import imcode.server.document.index.DocumentIndex;
import imcode.server.user.UserDomainObject;
import imcode.util.DateConstants;
import imcode.util.Utility;
import imcode.util.jscalendar.JSCalendar;
import org.apache.commons.collections.MultiHashMap;
import org.apache.commons.collections.MultiMap;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.document.DateField;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.RangeQuery;
import org.apache.lucene.search.TermQuery;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class SearchDocumentsPage extends OkCancelPage implements DocumentFinderPage {

    public static final String REQUEST_PARAMETER__SECTION_ID = "section_id";
    public static final String REQUEST_PARAMETER__DOCUMENTS_PER_PAGE = "num";
    public static final String REQUEST_PARAMETER__QUERY_STRING = "q";
    public static final String REQUEST_PARAMETER__FIRST_DOCUMENT_INDEX = "start";
    public static final String REQUEST_PARAMETER__SELECTED_DOCUMENT_ID = "select";
    public static final String REQUEST_PARAMETER__TO_EDIT_DOCUMENT_ID = "toedit";
    public static final String REQUEST_PARAMETER__SEARCH_BUTTON = "search";
    public static final String REQUEST_PARAMETER__CANCEL_BUTTON = "cancel";
    public static final String REQUEST_PARAMETER__USER_RESTRICTION = "permission";
    public static final String REQUEST_PARAMETER__DATE_TYPE = "date_type";
    public static final String REQUEST_PARAMETER__START_DATE = "start_date";
    public static final String REQUEST_PARAMETER__END_DATE = "end_date";
    public static final String REQUEST_PARAMETER__SORT_ORDER = "sort_order";
    public static final String REQUEST_PARAMETER__PHASE = "phase";
    public static final String REQUEST_PARAMETER__DOCUMENT_TYPE_ID = "document_type_id";

    private static final int DEFAULT_DOCUMENTS_PER_PAGE = 10;
    private final static Logger log = Logger.getLogger( SearchDocumentsPage.class.getName() );

    private String queryString;
    private Set sections = new HashSet();
    private String[] phases;
    private int[] documentTypeIds;
    private String userDocumentsRestriction;
    private String dateTypeRestriction;
    private Date startDate;
    private Date endDate;
    private String sortOrder;

    private List documentsFound;
    private int firstDocumentIndex;
    private int documentsPerPage = DEFAULT_DOCUMENTS_PER_PAGE;
    private DocumentDomainObject selectedDocument;
    private Query query;

    private DocumentFinder documentFinder;

    public static final String USER_DOCUMENTS_RESTRICTION__NONE = "";
    public static final String USER_DOCUMENTS_RESTRICTION__DOCUMENTS_CREATED_BY_USER = "created";
    public static final String USER_DOCUMENTS_RESTRICTION__DOCUMENTS_PUBLISHED_BY_USER = "published";

    public static final String DATE_TYPE__PUBLICATION_START = "publication_start";
    public static final String DATE_TYPE__PUBLICATION_END = "publication_end" ;
    public static final String DATE_TYPE__CREATED = "created";
    public static final String DATE_TYPE__ARCHIVED = "archived";
    public static final String DATE_TYPE__MODIFIED = "modified";

    public SearchDocumentsPage() {
        super( null, null );
    }

    public void updateFromRequest( HttpServletRequest request ) {

        DocumentMapper documentMapper = Imcms.getServices().getDocumentMapper();

        if ( documentFinder.isDocumentsSelectable() ) {
            try {
                selectedDocument = documentMapper.getDocument( Integer.parseInt( request.getParameter( REQUEST_PARAMETER__SELECTED_DOCUMENT_ID ) ) );
            } catch ( NumberFormatException nfe ) {
            }
        }

        firstDocumentIndex = Math.max( 0, NumberUtils.stringToInt( request.getParameter( REQUEST_PARAMETER__FIRST_DOCUMENT_INDEX ) ) );

        boolean gotNewFirstDocumentIndex = Utility.parameterIsSet( request, REQUEST_PARAMETER__FIRST_DOCUMENT_INDEX );
        boolean notBrowsingResultList = !gotNewFirstDocumentIndex || documentsFound == null ;
        if ( notBrowsingResultList ) {
            try {
                sections.clear();
                int[] sectionIds = Utility.getParameterInts( request, REQUEST_PARAMETER__SECTION_ID );
                for ( int i = 0; i < sectionIds.length; i++ ) {
                    int sectionId = sectionIds[i];
                    SectionDomainObject section = documentMapper.getSectionById( sectionId );
                    sections.add( section );
                }
            } catch ( NumberFormatException nfe ) {
            }

            phases = Utility.getParameterValues( request, REQUEST_PARAMETER__PHASE );

            documentTypeIds = Utility.getParameterInts(request, REQUEST_PARAMETER__DOCUMENT_TYPE_ID);
        }

        String userDocumentsRestrictionParameter = request.getParameter( REQUEST_PARAMETER__USER_RESTRICTION );
        if ( null != userDocumentsRestrictionParameter ) {
            userDocumentsRestriction = userDocumentsRestrictionParameter;
        }

        String dateTypeRestrictionParameter = request.getParameter( REQUEST_PARAMETER__DATE_TYPE );
        if ( null != dateTypeRestrictionParameter ) {
            dateTypeRestriction = dateTypeRestrictionParameter;
        }

        DateFormat dateFormat = createDateFormat();
        String startDateParameter = request.getParameter( REQUEST_PARAMETER__START_DATE );
        if ( null != startDateParameter ) {
            try {
                startDate = dateFormat.parse( startDateParameter );
            } catch ( java.text.ParseException pe ) {
                startDate = null;
            }
        }
        String endDateParameter = request.getParameter( REQUEST_PARAMETER__END_DATE );
        if ( null != endDateParameter ) {
            try {
                endDate = dateFormat.parse( endDateParameter );
            } catch ( java.text.ParseException ignored ) {
                endDate = null;
            }
        }

        String sortOrderParameter = request.getParameter( REQUEST_PARAMETER__SORT_ORDER );
        if ( null != sortOrderParameter ) {
            sortOrder = sortOrderParameter ;
            Comparator documentComparator = AdminManager.getComparator( sortOrder );
            documentFinder.setDocumentComparator( documentComparator );
        }

        try {
            documentsPerPage = Integer.parseInt( request.getParameter( REQUEST_PARAMETER__DOCUMENTS_PER_PAGE ) );
        } catch ( NumberFormatException ignored ) {}
        if ( documentsPerPage <= 0 ) {
            documentsPerPage = DEFAULT_DOCUMENTS_PER_PAGE;
        }
        queryString = StringUtils.defaultString( request.getParameter( REQUEST_PARAMETER__QUERY_STRING ) );

        query = createQuery( documentFinder, Utility.getLoggedOnUser( request ) );
    }

    private SimpleDateFormat createDateFormat() {
        return new SimpleDateFormat( DateConstants.DATE_FORMAT_STRING );
    }

    private Query createQuery( DocumentFinder documentFinder, UserDomainObject user ) {

        BooleanQuery newQuery = new BooleanQuery();
        if ( StringUtils.isNotBlank( queryString ) ) {
            try {
                Query textQuery = documentFinder.parse( queryString );
                newQuery.add( textQuery, true, false );
            } catch ( ParseException e ) {
                log.debug( e.getMessage() + " in search-string " + queryString, e );
            }
        }

        if ( !sections.isEmpty() ) {
            BooleanQuery sectionQueries = new BooleanQuery();
            for ( Iterator iterator = sections.iterator(); iterator.hasNext(); ) {
                SectionDomainObject section = (SectionDomainObject)iterator.next();
                sectionQueries.add( new TermQuery( new Term( DocumentIndex.FIELD__SECTION, section.getName().toLowerCase() ) ), false, false );
            }
            newQuery.add( sectionQueries, true, false );
        }

        if ( null != phases && phases.length > 0 ) {
            BooleanQuery phaseQueries = new BooleanQuery();
            for ( int i = 0; i < phases.length; i++ ) {
                String phase = phases[i];
                Query phaseQuery = new TermQuery( new Term( DocumentIndex.FIELD__PHASE, phase ) );
                phaseQueries.add( phaseQuery, false, false );
            }
            newQuery.add( phaseQueries, true, false );
        }

        if ( null != documentTypeIds && documentTypeIds.length > 0 ) {
            BooleanQuery documentTypeQueries = new BooleanQuery();
            for ( int i = 0; i < documentTypeIds.length; i++ ) {
                int documentTypeId = documentTypeIds[i];
                Query documentTypeQuery = new TermQuery( new Term(DocumentIndex.FIELD__DOC_TYPE_ID, ""+documentTypeId)) ;
                documentTypeQueries.add( documentTypeQuery, false, false );
            }
            newQuery.add( documentTypeQueries, true, false ) ;
        }

        if ( USER_DOCUMENTS_RESTRICTION__DOCUMENTS_CREATED_BY_USER.equals( userDocumentsRestriction ) ) {
            Query createdByUserQuery = new TermQuery( new Term( DocumentIndex.FIELD__CREATOR_ID, "" + user.getId() ) );
            newQuery.add( createdByUserQuery, true, false );
        }

        if ( USER_DOCUMENTS_RESTRICTION__DOCUMENTS_PUBLISHED_BY_USER.equals( userDocumentsRestriction ) ) {
            Query publishedByUserQuery = new TermQuery( new Term( DocumentIndex.FIELD__PUBLISHER_ID, "" + user.getId() ) );
            newQuery.add( publishedByUserQuery, true, false );
        }

        if ( null != startDate || null != endDate ) {
            Date luceneMinDate = new Date( 0 );
            Date luceneMaxDate = new Date( 1000L * 365 * 24 * 60 * 60 * 1000 );
            Date calculatedStartDate = null == startDate ? luceneMinDate : startDate;
            Date calculatedEndDate = null == endDate ? luceneMaxDate : new Date( endDate.getTime() + DateUtils.MILLIS_IN_DAY );

            String dateField;
            if ( DATE_TYPE__PUBLICATION_END.equals( dateTypeRestriction ) ) {
                dateField = DocumentIndex.FIELD__PUBLICATION_END_DATETIME;
            } else if ( DATE_TYPE__ARCHIVED.equals( dateTypeRestriction ) ) {
                dateField = DocumentIndex.FIELD__ARCHIVED_DATETIME;
            } else if ( DATE_TYPE__CREATED.equals( dateTypeRestriction ) ) {
                dateField = DocumentIndex.FIELD__CREATED_DATETIME;
            } else if ( DATE_TYPE__MODIFIED.equals( dateTypeRestriction ) ) {
                dateField = DocumentIndex.FIELD__MODIFIED_DATETIME;
            } else {
                dateField = DocumentIndex.FIELD__PUBLICATION_START_DATETIME;
            }

            Term lowerTerm = new Term( dateField, DateField.dateToString( calculatedStartDate ) );
            Term upperTerm = new Term( dateField, DateField.dateToString( calculatedEndDate ) );
            Query publicationStartedQuery = new RangeQuery( lowerTerm, upperTerm, true );
            newQuery.add( publicationStartedQuery, true, false );
        }

        if ( 0 == newQuery.getClauses().length ) {
            return null;
        }
        return newQuery;
    }

    protected boolean wasCanceled( HttpServletRequest request ) {
        return documentFinder.isCancelable() && super.wasCanceled( request );
    }

    protected void dispatchCancel( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
        documentFinder.cancel( request, response );
    }

    protected void dispatchOther( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
        DocumentDomainObject documentSelectedForEditing = null;
        try {
            DocumentMapper documentMapper = Imcms.getServices().getDocumentMapper();
            documentSelectedForEditing = documentMapper.getDocument( Integer.parseInt( request.getParameter( REQUEST_PARAMETER__TO_EDIT_DOCUMENT_ID ) ) );
        } catch ( NumberFormatException nfe ) {
        }

        if ( null != documentSelectedForEditing ) {
            goToEditDocumentInformation( request, response, documentSelectedForEditing );
        } else if ( null != getSelectedDocument() ) {
            documentFinder.selectDocument( getSelectedDocument());
            documentFinder.dispatchReturn(request, response);
        } else {
            documentFinder.forwardWithPage( request, response, this );
        }
    }

    private void goToEditDocumentInformation( HttpServletRequest request, HttpServletResponse response,
                                              DocumentDomainObject documentSelectedForEditing ) throws IOException, ServletException {
        DispatchCommand returnCommand = new DispatchCommand() {
            public void dispatch( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
                documentFinder.forwardWithPage( request, response, SearchDocumentsPage.this );
            }
        };
        EditDocumentInformationPageFlow editDocumentInformationPageFlow = new EditDocumentInformationPageFlow( documentSelectedForEditing, returnCommand, new DocumentMapper.SaveEditedDocumentCommand() );
        editDocumentInformationPageFlow.setAdminButtonsHidden( true );
        editDocumentInformationPageFlow.dispatch( request, response );
    }

    public String getParameterStringWithParameter( HttpServletRequest request,
                                                   String parameterName, String parameterValue ) {
        MultiMap parameters = getParameterMap( request );
        parameters.put( parameterName, parameterValue );
        return Utility.createQueryStringFromParameterMultiMap( parameters );
    }

    private MultiMap getParameterMap( HttpServletRequest request ) {
        MultiMap parameters = new MultiHashMap();
        Page page = Page.fromRequest(request);
        String pageSessionNameFromRequest = page.getSessionAttributeName();
        if ( null != pageSessionNameFromRequest ) {
            parameters.put( Page.IN_REQUEST, pageSessionNameFromRequest );
        }
        return parameters;
    }

    public String getPath(HttpServletRequest request) {
        UserDomainObject user = Utility.getLoggedOnUser( request );
        return "/imcms/" + user.getLanguageIso639_2() + "/jsp/search_documents.jsp";
    }

    public Set getSections() {
        return Collections.unmodifiableSet( sections );
    }

    public List getDocumentsFound() {
        return documentsFound;
    }

    public void setDocumentsFound( List documentsFound ) {
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

    public DocumentFinder getDocumentFinder() {
        return documentFinder;
    }

    public void setDocumentFinder( DocumentFinder documentFinder ) {
        this.documentFinder = documentFinder;
    }

    public String[] getPhases() {
        return phases;
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

    public String getDateTypeRestriction() {
        return dateTypeRestriction;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public String getUserDocumentsRestriction() {
        return userDocumentsRestriction;
    }

    public int[] getDocumentTypeIds() {
        return documentTypeIds;
    }
    public JSCalendar getJSCalender (HttpServletRequest request) {
        return new JSCalendar( Utility.getLoggedOnUser(request).getLanguageIso639_2(), request ) ;
    }
}
