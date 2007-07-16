package com.imcode.imcms.servlet.superadmin;

import com.imcode.imcms.flow.DispatchCommand;
import com.imcode.imcms.flow.DocumentPageFlow;
import com.imcode.imcms.mapping.DocumentMapper;
import com.imcode.imcms.mapping.DocumentSaveException;
import com.imcode.imcms.servlet.AdminManagerSearchPage;
import com.imcode.imcms.servlet.DocumentFinder;
import com.imcode.imcms.servlet.SearchDocumentsPage;
import com.imcode.imcms.servlet.admin.DocumentCreator;
import com.imcode.imcms.servlet.beans.AdminManagerExpandableDatesBean;
import com.imcode.imcms.servlet.beans.AdminManagerSubreport;
import com.imcode.imcms.util.l10n.LocalizedMessage;
import com.imcode.util.ChainableReversibleNullComparator;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.document.DocumentComparator;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.NoPermissionToCreateDocumentException;
import imcode.server.document.NoPermissionToEditDocumentException;
import imcode.server.document.LifeCyclePhase;
import imcode.server.document.index.DocumentIndex;
import imcode.server.document.index.SimpleDocumentQuery;
import imcode.server.document.textdocument.NoPermissionToAddDocumentToMenuException;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import imcode.util.ShouldNotBeThrownException;
import imcode.util.jscalendar.JSCalendar;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.UnhandledException;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

public class AdminManager extends HttpServlet {

    private final static String HTML_ADMINTASK = "AdminManager_adminTask_element.htm";
    private final static String HTML_USERADMINTASK = "AdminManager_useradminTask_element.htm";
    public final static String REQUEST_PARAMETER__SHOW = "show";
    public final static String PARAMETER_VALUE__SHOW_CREATE = "create";
    public final static String PARAMETER_VALUE__SHOW_RECENT = "recent";
    public final static String PARAMETER_VALUE__SHOW_REMINDERS = "reminders";
    public final static String PARAMETER_VALUE__SHOW_SUMMARY = "summary";
    public final static String PARAMETER_VALUE__SHOW_SEARCH = "search";

    public static final int DEFAULT_DOCUMENTS_PER_LIST = 5;
    public static final String REQUEST_PARAMETER__FROMPAGE = "frompage";
    public static final String REQUEST_PARAMETER__CREATE_NEW_DOCUMENT = "create_new_document";
    public static final String REQUEST_PARAMETER__NEW_DOCUMENT_PARENT_ID = "parent_id";
    public static final String REQUEST_PARAMETER__CREATE_DOCUMENT_ACTION = "new_document_type_id";
    public static final String REQUEST_PARAMETER__ACTION__COPY = "copy";

    public static final String PAGE_SEARCH = "search";
    private static final LocalizedMessage ERROR_MESSAGE__NO_CREATE_PERMISSION = new LocalizedMessage( "error/servlet/AdminManager/no_create_permission" );
    private static final LocalizedMessage ERROR_MESSAGE__NO_PARENT_ID = new LocalizedMessage( "error/servlet/AdminManager/no_parent_id" );
    private static final LocalizedMessage ERROR_MESSAGE__PARENT_MUST_BE_TEXT_DOCUMENT = new LocalizedMessage( "error/servlet/AdminManager/parent_must_be_text_document" );

    public void doGet( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
        this.doPost( req, res );
    }

    public void doPost( HttpServletRequest request, HttpServletResponse response )
            throws ServletException, IOException {

        ImcmsServices service = Imcms.getServices();
        UserDomainObject user = Utility.getLoggedOnUser( request );
        Utility.setDefaultHtmlContentType( response );

        String whichButton = request.getParameter( "AdminTask" );
        if ( null != whichButton ) {

            String url = getAdminTaskUrl( whichButton, request );
            if ( !user.isSuperAdmin() && !user.isUserAdminAndCanEditAtLeastOneRole() ) {
                Utility.forwardToLogin( request, response );
                return;
            }

            if ( StringUtils.isNotBlank( url ) ) {
                response.sendRedirect( url );
                return;
            }
        }

        if ( !user.canAccessAdminPages() ) {
            Utility.forwardToLogin( request, response );
            return;
        }

        final DocumentMapper documentMapper = service.getDocumentMapper();
        if ( Utility.parameterIsSet( request, REQUEST_PARAMETER__CREATE_NEW_DOCUMENT ) ) {
        String parentPageId =  request.getParameter( REQUEST_PARAMETER__NEW_DOCUMENT_PARENT_ID );
            try {
                DocumentDomainObject parentDocument = documentMapper.getDocument(parentPageId.toLowerCase().trim());
                String createDocumentAction = request.getParameter( REQUEST_PARAMETER__CREATE_DOCUMENT_ACTION );
                if ( REQUEST_PARAMETER__ACTION__COPY.equals( createDocumentAction ) && parentDocument != null ) {
                    documentMapper.copyDocument( parentDocument, user );
                    createAndShowAdminManagerPage( request, response, null, PARAMETER_VALUE__SHOW_RECENT);
                } else {
                    if (!(parentDocument instanceof TextDocumentDomainObject)) {
                        createAndShowAdminManagerPage( request, response, ERROR_MESSAGE__PARENT_MUST_BE_TEXT_DOCUMENT, PARAMETER_VALUE__SHOW_CREATE);
                        return ;
                    }
                    int documentTypeId = Integer.parseInt( createDocumentAction );

                    DocumentPageFlow.SaveDocumentCommand saveNewDocumentCommand = new SaveNewDocumentCommand();
                    DispatchCommand returnCommand = new ShowRecentChangesPageCommand();

                    DocumentCreator documentCreator = new DocumentCreator( saveNewDocumentCommand, returnCommand, getServletContext() );
                    documentCreator.createDocumentAndDispatchToCreatePageFlow( documentTypeId, parentDocument, request, response );
                }
            } catch ( NumberFormatException nfe ) {
                createAndShowAdminManagerPage( request, response, ERROR_MESSAGE__NO_PARENT_ID, PARAMETER_VALUE__SHOW_CREATE);
            } catch ( NoPermissionToCreateDocumentException ex ) {
                createAndShowAdminManagerPage( request, response, ERROR_MESSAGE__NO_CREATE_PERMISSION, PARAMETER_VALUE__SHOW_CREATE);
            } catch ( NoPermissionToAddDocumentToMenuException e ) {
                throw new UnhandledException(e);
            } catch (DocumentSaveException e) {
                throw new ShouldNotBeThrownException(e);
            }
        } else {
            createAndShowAdminManagerPage( request, response, null);
        }
    }

    private void createAndShowAdminManagerPage( HttpServletRequest request, HttpServletResponse response,
                                                LocalizedMessage errorMessage ) throws IOException, ServletException {

        String tabToShow = null != request.getParameter( REQUEST_PARAMETER__SHOW )
                           ? request.getParameter( REQUEST_PARAMETER__SHOW ) : PARAMETER_VALUE__SHOW_CREATE;

        createAndShowAdminManagerPage(request, response, errorMessage, tabToShow);
    }

    private void createAndShowAdminManagerPage(HttpServletRequest request, HttpServletResponse response, LocalizedMessage errorMessage, String tabToShow) throws IOException, ServletException {
        UserDomainObject loggedOnUser = Utility.getLoggedOnUser( request );
        ImcmsServices service = Imcms.getServices();
        final DocumentMapper documentMapper = service.getDocumentMapper();

        String html_admin_part = "";

        if ( loggedOnUser.isSuperAdmin() ) {
            html_admin_part = service.getAdminTemplate( HTML_ADMINTASK, loggedOnUser, null ); // if superadmin
        } else if ( loggedOnUser.isUserAdminAndCanEditAtLeastOneRole() ) { //if user is useradmin
            html_admin_part = service.getAdminTemplate( HTML_USERADMINTASK, loggedOnUser, null ); //if useradmin
        }

        DocumentIndex index = documentMapper.getDocumentIndex();

        Query query = new TermQuery( new Term( DocumentIndex.FIELD__CREATOR_ID, loggedOnUser.getId() + "" ) );

        List documentsFound = Collections.EMPTY_LIST;
        if ( tabToShow.equals( PARAMETER_VALUE__SHOW_RECENT ) || tabToShow.equals(PARAMETER_VALUE__SHOW_REMINDERS)
             || tabToShow.equals(PARAMETER_VALUE__SHOW_SUMMARY) ) {
            documentsFound = index.search( new SimpleDocumentQuery(query), loggedOnUser );
        }

        AdminManagerSubreport newDocumentsSubreport = new AdminManagerSubreport();
        AdminManagerSubreport modifiedDocumentsSubreport = new AdminManagerSubreport();
        AdminManagerSubreport documentsArchivedWithinOneWeekSubreport = new AdminManagerSubreport();
        AdminManagerSubreport documentsUnpublishedWithinOneWeekSubreport = new AdminManagerSubreport();
        AdminManagerSubreport documentsUnmodifiedForSixMonthsSubreport = new AdminManagerSubreport();


        if ( tabToShow.equals(PARAMETER_VALUE__SHOW_RECENT) || tabToShow.equals(PARAMETER_VALUE__SHOW_SUMMARY) ) {
            newDocumentsSubreport = createNewDocumentsSubreport( documentsFound );
            sortAndSetExpandedSubreport(newDocumentsSubreport, request);

            modifiedDocumentsSubreport = createModifiedDocumentsSubreport( documentsFound );
            sortAndSetExpandedSubreport(modifiedDocumentsSubreport, request);
        }

        if ( tabToShow.equals(PARAMETER_VALUE__SHOW_REMINDERS) || tabToShow.equals(PARAMETER_VALUE__SHOW_SUMMARY) ) {
            documentsArchivedWithinOneWeekSubreport = createDocumentsArchivedWithinOneWeekSubreport( documentsFound );
            sortAndSetExpandedSubreport(documentsArchivedWithinOneWeekSubreport, request);

            documentsUnpublishedWithinOneWeekSubreport = createDocumentsUnpublishedWithinOneWeekSubreport( documentsFound );
            sortAndSetExpandedSubreport(documentsUnpublishedWithinOneWeekSubreport, request);

            documentsUnmodifiedForSixMonthsSubreport = createDocumentsUnmodifiedForSixMonthsSubreport( documentsFound );
            sortAndSetExpandedSubreport(documentsUnmodifiedForSixMonthsSubreport, request);
        }

        AdminManagerPage adminManagerPage = null;
        if ( tabToShow.equals( PARAMETER_VALUE__SHOW_CREATE ) ) {
            AdminManagerPage newDocumentsAdminManagerPage = new AdminManagerPage();
            newDocumentsAdminManagerPage.setTabName( PARAMETER_VALUE__SHOW_CREATE );
            newDocumentsAdminManagerPage.setHeading( new LocalizedMessage( "web/imcms/lang/jsp/admin/admin_manager.jsp/tab_name/0" ) );
            adminManagerPage = newDocumentsAdminManagerPage;

        } else if ( tabToShow.equals( PARAMETER_VALUE__SHOW_RECENT ) ) {

            newDocumentsSubreport.setMaxDocumentCount( 10 );

            AdminManagerPage newDocumentsAdminManagerPage = new AdminManagerPage();
            newDocumentsAdminManagerPage.setTabName( PARAMETER_VALUE__SHOW_RECENT );
            newDocumentsAdminManagerPage.setHeading( new LocalizedMessage( "web/imcms/lang/jsp/admin/admin_manager.jsp/tab_name/1" ) );

            newDocumentsAdminManagerPage.addSubreport( newDocumentsSubreport );

            modifiedDocumentsSubreport.setMaxDocumentCount( 10 );
            newDocumentsAdminManagerPage.addSubreport( modifiedDocumentsSubreport );

            adminManagerPage = newDocumentsAdminManagerPage;

        } else if ( tabToShow.equals( PARAMETER_VALUE__SHOW_REMINDERS ) ) {

            AdminManagerPage reminderAdminManagerPage = new AdminManagerPage();
            reminderAdminManagerPage.setTabName( PARAMETER_VALUE__SHOW_REMINDERS );
            reminderAdminManagerPage.setHeading( new LocalizedMessage( "web/imcms/lang/jsp/admin/admin_manager.jsp/tab_name/2" ) );

            documentsArchivedWithinOneWeekSubreport.setMaxDocumentCount( 10 );
            reminderAdminManagerPage.addSubreport( documentsArchivedWithinOneWeekSubreport );

            documentsUnpublishedWithinOneWeekSubreport.setMaxDocumentCount( 10 );
            reminderAdminManagerPage.addSubreport( documentsUnpublishedWithinOneWeekSubreport );

            documentsUnmodifiedForSixMonthsSubreport.setMaxDocumentCount( 10 );
            reminderAdminManagerPage.addSubreport( documentsUnmodifiedForSixMonthsSubreport );

            adminManagerPage = reminderAdminManagerPage;

        } else if ( tabToShow.equals( PARAMETER_VALUE__SHOW_SUMMARY ) ) {

            AdminManagerPage summaryAdminManagerPage = new AdminManagerPage();
            summaryAdminManagerPage.setTabName( PARAMETER_VALUE__SHOW_SUMMARY );
            summaryAdminManagerPage.setHeading( new LocalizedMessage( "web/imcms/lang/jsp/admin/admin_manager.jsp/tab_name/3" ) );

            summaryAdminManagerPage.addSubreport( newDocumentsSubreport );

            summaryAdminManagerPage.addSubreport( modifiedDocumentsSubreport );

            summaryAdminManagerPage.addSubreport( documentsArchivedWithinOneWeekSubreport );

            summaryAdminManagerPage.addSubreport( documentsUnpublishedWithinOneWeekSubreport );

            summaryAdminManagerPage.addSubreport( documentsUnmodifiedForSixMonthsSubreport );
            adminManagerPage = summaryAdminManagerPage;

        } else if ( tabToShow.equals( PARAMETER_VALUE__SHOW_SEARCH ) ) {

            AdminManagerPage searchAdminManagerPage = new AdminManagerPage() {
                public void forward( HttpServletRequest request, HttpServletResponse response, UserDomainObject user ) throws IOException, ServletException {
                    AdminManagerSearchPage page = new AdminManagerSearchPage( this );
                    DocumentFinder documentFinder = new DocumentFinder( page );
                    documentFinder.setDocumentComparator( getComparator( null ) );
                    page.updateFromRequest( request );
                    documentFinder.addExtraSearchResultColumn( new DatesSummarySearchResultColumn() );
                    documentFinder.forward( request, response );
                }
            };
            searchAdminManagerPage.setTabName( PARAMETER_VALUE__SHOW_SEARCH );
            searchAdminManagerPage.setHeading( new LocalizedMessage( "global/Search" ) );
            adminManagerPage = searchAdminManagerPage;
        }
        if (null != adminManagerPage) {
            adminManagerPage.setErrorMessage( errorMessage  );
            adminManagerPage.setHtmlAdminPart( "".equals( html_admin_part ) ? null : html_admin_part );
            adminManagerPage.forward( request, response, loggedOnUser );
        }
    }

    private void sortAndSetExpandedSubreport (AdminManagerSubreport subreport, HttpServletRequest request ) {

            String newSortOrder = request.getParameter( subreport.getName() + "_sortorder" );
            if ( null != newSortOrder ) {
                subreport.setSortorder( newSortOrder );
            }
            Collections.sort( subreport.getDocuments(), getComparator( subreport.getSortorder() ) );
            boolean expanded = Utility.parameterIsSet( request, subreport.getName() + "_expand" )
                               && !Utility.parameterIsSet( request, subreport.getName() + "_unexpand" );
            subreport.setExpanded( expanded );


    }

    private AdminManagerSubreport createModifiedDocumentsSubreport( List documentsFound ) {
        List modifiedDocuments = new ArrayList();
        Date oneWeekAgo = getDateOneWeekAgo();
        for ( Iterator iterator = documentsFound.iterator(); iterator.hasNext(); ) {
            DocumentDomainObject document = (DocumentDomainObject) iterator.next();
            boolean createdInPastWeek = !document.getCreatedDatetime().before( oneWeekAgo );
            boolean modifiedInPastWeek = !document.getModifiedDatetime().before( oneWeekAgo );
            if ( modifiedInPastWeek && !createdInPastWeek ) {
                modifiedDocuments.add( document );
            }
        }

        AdminManagerSubreport modifiedDocumentsSubreport = new AdminManagerSubreport();
        modifiedDocumentsSubreport.setName( "modified" );
        modifiedDocumentsSubreport.setDocuments( modifiedDocuments );
        modifiedDocumentsSubreport.setHeading( new LocalizedMessage( "web/imcms/lang/jsp/admin/admin_manager.jsp/subreport_heading/5" ) );
        String dateSearchQueryString = createDateSearchQueryString( SearchDocumentsPage.DATE_TYPE__MODIFIED, oneWeekAgo, null, null );
        modifiedDocumentsSubreport.setSearchQueryString( dateSearchQueryString );
        return modifiedDocumentsSubreport;
    }

    private AdminManagerSubreport createNewDocumentsSubreport( List documentsFound ) {
        List newDocuments = new ArrayList();

        Date oneWeekAgo = getDateOneWeekAgo();
        for ( Iterator iterator = documentsFound.iterator(); iterator.hasNext(); ) {
            DocumentDomainObject document = (DocumentDomainObject) iterator.next();
            boolean createdInPastWeek = !document.getCreatedDatetime().before( oneWeekAgo );
            if ( createdInPastWeek ) {
                newDocuments.add( document );
            }
        }

        AdminManagerSubreport newDocumentsSubreport = new AdminManagerSubreport();
        newDocumentsSubreport.setName( "new" );
        newDocumentsSubreport.setDocuments( newDocuments );
        newDocumentsSubreport.setHeading( new LocalizedMessage( "web/imcms/lang/jsp/admin/admin_manager.jsp/subreport_heading/1" ) );
        String dateSearchQueryString = createDateSearchQueryString( SearchDocumentsPage.DATE_TYPE__CREATED, oneWeekAgo, null, null );
        newDocumentsSubreport.setSearchQueryString( dateSearchQueryString );
        return newDocumentsSubreport;
    }

    private AdminManagerSubreport createDocumentsUnmodifiedForSixMonthsSubreport(
            List documentsFound ) {
        LifeCyclePhase[] phases = new LifeCyclePhase[]{
            LifeCyclePhase.APPROVED, LifeCyclePhase.NEW,
            LifeCyclePhase.PUBLISHED, LifeCyclePhase.ARCHIVED,
        };
        Date sixMonthsAgo = getDateSixMonthsAgo();
        List documentsUnchangedForSixMonths = new ArrayList();

        for ( Iterator iterator = documentsFound.iterator(); iterator.hasNext(); ) {
            DocumentDomainObject document = (DocumentDomainObject) iterator.next();
            LifeCyclePhase phase = document.getLifeCyclePhase();
            if ( ArrayUtils.contains( phases, phase ) && document.getModifiedDatetime().before( sixMonthsAgo ) ) {
                documentsUnchangedForSixMonths.add( document );
            }
        }

        AdminManagerSubreport documentsUnchangedForSixMonthsSubreport = new AdminManagerSubreport();
        documentsUnchangedForSixMonthsSubreport.setName( "unchangedForSixMonths" );
        documentsUnchangedForSixMonthsSubreport.setDocuments( documentsUnchangedForSixMonths );
        documentsUnchangedForSixMonthsSubreport.setHeading( new LocalizedMessage( "web/imcms/lang/jsp/admin/admin_manager.jsp/subreport_heading/4" ) );
        documentsUnchangedForSixMonthsSubreport.setSortorder( "MODR" );
        String dateSearchQueryString = createDateSearchQueryString( SearchDocumentsPage.DATE_TYPE__MODIFIED, null, sixMonthsAgo, phases );
        documentsUnchangedForSixMonthsSubreport.setSearchQueryString( dateSearchQueryString );
        return documentsUnchangedForSixMonthsSubreport;
    }

    private AdminManagerSubreport createDocumentsArchivedWithinOneWeekSubreport( List documentsFound ) {
        LifeCyclePhase[] phases = new LifeCyclePhase[]{
            LifeCyclePhase.APPROVED,
            LifeCyclePhase.PUBLISHED
        };
        List documentsArchivedWithinOneWeek = new ArrayList();
        Date lastMidnight = getDateLastMidnight();
        Date oneWeekAhead = getDateOneWeekAhead();
        for ( Iterator iterator = documentsFound.iterator(); iterator.hasNext(); ) {
            DocumentDomainObject document = (DocumentDomainObject) iterator.next();
            LifeCyclePhase phase = document.getLifeCyclePhase();
            Date archivedDatetime = document.getArchivedDatetime();
            if ( ArrayUtils.contains( phases, phase ) && null != archivedDatetime
                 && !archivedDatetime.before( lastMidnight )
                 && archivedDatetime.before( oneWeekAhead ) ) {
                documentsArchivedWithinOneWeek.add( document );
            }
        }
        AdminManagerSubreport documentsArchivedWithinOneWeekSubreport = new AdminManagerSubreport();
        documentsArchivedWithinOneWeekSubreport.setName( "archivedWithinOneWeek" );
        documentsArchivedWithinOneWeekSubreport.setDocuments( documentsArchivedWithinOneWeek );
        documentsArchivedWithinOneWeekSubreport.setHeading( new LocalizedMessage( "web/imcms/lang/jsp/admin/admin_manager.jsp/subreport_heading/2" ) );
        documentsArchivedWithinOneWeekSubreport.setSortorder( "ARCR" );
        String dateSearchQueryString = createDateSearchQueryString( SearchDocumentsPage.DATE_TYPE__ARCHIVED, lastMidnight, oneWeekAhead, phases );
        documentsArchivedWithinOneWeekSubreport.setSearchQueryString( dateSearchQueryString );
        return documentsArchivedWithinOneWeekSubreport;
    }

    private AdminManagerSubreport createDocumentsUnpublishedWithinOneWeekSubreport(
            List documentsFound ) {
        LifeCyclePhase[] phases = new LifeCyclePhase[]{
            LifeCyclePhase.APPROVED,
            LifeCyclePhase.ARCHIVED, LifeCyclePhase.PUBLISHED
        };
        Date lastMidnight = getDateLastMidnight();
        Date oneWeekAhead = getDateOneWeekAhead();
        List documentsUnpublishedWithinOneWeek = new ArrayList();
        for ( Iterator iterator = documentsFound.iterator(); iterator.hasNext(); ) {
            DocumentDomainObject document = (DocumentDomainObject) iterator.next();
            LifeCyclePhase phase = document.getLifeCyclePhase();
            Date publicationEndDatetime = document.getPublicationEndDatetime();
            if ( ArrayUtils.contains( phases, phase ) && null != publicationEndDatetime
                 && !publicationEndDatetime.before( lastMidnight )
                 && publicationEndDatetime.before( oneWeekAhead ) ) {
                documentsUnpublishedWithinOneWeek.add( document );
            }
        }

        AdminManagerSubreport documentsUnpublishedWithinOneWeekSubreport = new AdminManagerSubreport();
        documentsUnpublishedWithinOneWeekSubreport.setName( "unpublishedWithinOneWeek" );
        documentsUnpublishedWithinOneWeekSubreport.setDocuments( documentsUnpublishedWithinOneWeek );
        documentsUnpublishedWithinOneWeekSubreport.setHeading( new LocalizedMessage( "web/imcms/lang/jsp/admin/admin_manager.jsp/subreport_heading/3" ) );
        documentsUnpublishedWithinOneWeekSubreport.setSortorder( "PUBER" );
        String dateSearchQueryString = createDateSearchQueryString( SearchDocumentsPage.DATE_TYPE__PUBLICATION_END, lastMidnight, oneWeekAhead, phases );
        documentsUnpublishedWithinOneWeekSubreport.setSearchQueryString( dateSearchQueryString );
        return documentsUnpublishedWithinOneWeekSubreport;
    }

    private String createDateSearchQueryString( String dateType, Date startDate, Date endDate,
                                                LifeCyclePhase[] lifeCyclePhases ) {
        String result = SearchDocumentsPage.REQUEST_PARAMETER__DATE_TYPE + "="
                        + dateType;

        result += "&"+SearchDocumentsPage.REQUEST_PARAMETER__USER_RESTRICTION+"="+SearchDocumentsPage.USER_DOCUMENTS_RESTRICTION__DOCUMENTS_CREATED_BY_USER ;

        if ( null != startDate ) {
            result += "&"
                      + SearchDocumentsPage.REQUEST_PARAMETER__START_DATE
                      + "="
                      + Utility.formatDate( startDate );
        }

        if ( null != endDate ) {
            result += "&"
                      + SearchDocumentsPage.REQUEST_PARAMETER__END_DATE
                      + "=" + Utility.formatDate( endDate );
        }
        if ( null != lifeCyclePhases ) {
            for ( int i = 0; i < lifeCyclePhases.length; i++ ) {
                LifeCyclePhase phase = lifeCyclePhases[i];
                result += "&" + SearchDocumentsPage.REQUEST_PARAMETER__PHASE + "=" + phase;
            }
        }

        return result;
    }

    public static class DatesSummarySearchResultColumn implements DocumentFinder.SearchResultColumn {

        public String render( DocumentDomainObject document, HttpServletRequest request,
                              HttpServletResponse response ) throws IOException, ServletException {
            UserDomainObject user = Utility.getLoggedOnUser( request );
            AdminManagerExpandableDatesBean expandableDatesBean = new AdminManagerExpandableDatesBean();
            expandableDatesBean.setExpanded( true );
            expandableDatesBean.setDocument( document );
            request.setAttribute( "expandableDatesBean", expandableDatesBean );
            return Utility.getContents( "/imcms/" + user.getLanguageIso639_2()
                                        + "/jsp/admin/admin_manager_expandable_dates.jsp", request, response );
        }

        public LocalizedMessage getName() {
            return new LocalizedMessage( "global/Dates" );
        }
    }

    public static class AdminManagerPage implements Serializable {

        LocalizedMessage heading;
        String tabName;
        List subreports = new ArrayList();
        String htmlAdminPart;
        public static final String REQUEST_ATTRIBUTE__PAGE = "ampage";

        private LocalizedMessage errorMessage;

        public LocalizedMessage getHeading() {
            return heading;
        }

        public void setHeading( LocalizedMessage heading ) {
            this.heading = heading;
        }

        public List getSubreports() {
            return subreports;
        }

        public String getTabName() {
            return tabName;
        }

        public void setTabName( String tabName ) {
            this.tabName = tabName;
        }

        public void addSubreport( AdminManagerSubreport newDocumentsSubreport ) {
            subreports.add( newDocumentsSubreport );
        }

        public String getHtmlAdminPart() {
            return htmlAdminPart;
        }

        public void setHtmlAdminPart( String htmlAdminPart ) {
            this.htmlAdminPart = htmlAdminPart;
        }

        public void forward( HttpServletRequest request, HttpServletResponse response, UserDomainObject user ) throws IOException, ServletException {
            putInRequest( request );
            String forwardPath = "/imcms/" + user.getLanguageIso639_2() + "/jsp/admin/admin_manager.jsp";
            request.getRequestDispatcher( forwardPath ).forward( request, response );
        }

        public void putInRequest( HttpServletRequest request ) {
            request.setAttribute( REQUEST_ATTRIBUTE__PAGE, this );
        }

        public LocalizedMessage getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage( LocalizedMessage errorMessage ) {
            this.errorMessage = errorMessage;
        }

        public JSCalendar getJSCalendar(HttpServletRequest request) {
            return new JSCalendar( Utility.getLoggedOnUser(request).getLanguageIso639_2(), request ) ;
        }

    }

    private Date getDateTruncated( int days ) {
        Calendar calendar = Calendar.getInstance();
        calendar.add( Calendar.DATE, days );
        calendar.set( Calendar.HOUR_OF_DAY, 0 );
        calendar.set( Calendar.MINUTE, 0 );
        calendar.set( Calendar.SECOND, 0 );
        calendar.set( Calendar.MILLISECOND, 0 );
        return calendar.getTime();
    }

    private Date getDateLastMidnight() {
        return getDateTruncated( 0 );
    }

    private Date getDateSixMonthsAgo() {
        return getDateTruncated( -182 );
    }

    private Date getDateOneWeekAhead() {
        return getDateTruncated( +7 );
    }

    private Date getDateOneWeekAgo() {
        return getDateTruncated( -7 );
    }

    public static ChainableReversibleNullComparator getComparator( String sortorder ) {

        ChainableReversibleNullComparator comparator = DocumentComparator.MODIFIED_DATETIME.reversed();
        if ( "MODR".equals( sortorder ) ) {
            comparator = DocumentComparator.MODIFIED_DATETIME;
        } else if ( "PUBS".equals( sortorder ) ) {
            comparator = DocumentComparator.PUBLICATION_START_DATETIME.reversed();
        } else if ( "PUBSR".equals( sortorder ) ) {
            comparator = DocumentComparator.PUBLICATION_START_DATETIME;
        } else if ( "PUBE".equals( sortorder ) ) {
            comparator = DocumentComparator.PUBLICATION_END_DATETIME.reversed().nullsLast();
        } else if ( "PUBER".equals( sortorder ) ) {
            comparator = DocumentComparator.PUBLICATION_END_DATETIME.nullsLast();
        } else if ( "ARC".equals( sortorder ) ) {
            comparator = DocumentComparator.ARCHIVED_DATETIME.reversed().nullsLast();
        } else if ( "ARCR".equals( sortorder ) ) {
            comparator = DocumentComparator.ARCHIVED_DATETIME.nullsLast();
        } else if ( "HEADL".equals( sortorder ) ) {
            comparator = DocumentComparator.HEADLINE;
        } else if ( "HEADLR".equals( sortorder ) ) {
            comparator = DocumentComparator.HEADLINE.reversed();
        } else if ( "ID".equals( sortorder ) ) {
            comparator = DocumentComparator.ID;
        } else if ( "IDR".equals( sortorder ) ) {
            comparator = DocumentComparator.ID.reversed();
        }
        return comparator;
    }

    private String getAdminTaskUrl(String whichButton, HttpServletRequest request) {
        String url = "";
        if ( whichButton.equalsIgnoreCase( "UserStart" ) ) {
            url += "AdminUser";
        } else if ( whichButton.equalsIgnoreCase( "CounterStart" ) ) {
            url += "AdminCounter";
        } else if ( whichButton.equalsIgnoreCase( "AdminSearchTerms" ) ) {
            url += "AdminSearchTerms";
        } else if ( whichButton.equalsIgnoreCase( "AddTemplates" ) ) {
            url += "TemplateAdmin";
        } else if ( whichButton.equalsIgnoreCase( "DeleteDocs" ) ) {
            url += "AdminDeleteDoc";
        } else if ( whichButton.equalsIgnoreCase( "IP-access" ) ) {
            url += "AdminIpAccess";
        } else if ( whichButton.equalsIgnoreCase( "SystemMessage" ) ) {
            url += "AdminSystemInfo";
        } else if ( whichButton.equalsIgnoreCase( "AdminRoles" ) ) {
            url += "AdminRoles";
        } else if ( whichButton.equalsIgnoreCase( "LinkCheck" ) ) {
            url += "LinkCheck";
        } else if ( whichButton.equalsIgnoreCase( "ListDocuments" ) ) {
            url += "ListDocuments";
        } else if ( whichButton.equalsIgnoreCase( "FileAdmin" ) ) {
            url += "FileAdmin";
        } else if ( whichButton.equalsIgnoreCase( "AdminSection" ) ) {
            url += "AdminSection";
        } else if ( whichButton.equalsIgnoreCase( "AdminCategories" ) ) {
            url += "AdminCategories";
        } else if ( whichButton.equals( "AdminProfiles" ) ) {
            url = request.getContextPath()+"/imcms/admin/profile/list";
        }
        return url;

    }

    private static class SaveNewDocumentCommand implements DocumentPageFlow.SaveDocumentCommand {

        public void saveDocument( DocumentDomainObject document, UserDomainObject user ) throws NoPermissionToEditDocumentException, NoPermissionToAddDocumentToMenuException, DocumentSaveException {
            Imcms.getServices().getDocumentMapper().saveNewDocument( document, user, false);
        }
    }

    private class ShowRecentChangesPageCommand implements DispatchCommand {

        public void dispatch( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
            createAndShowAdminManagerPage( request, response, null, PARAMETER_VALUE__SHOW_RECENT );
        }
    }
} // End of class
