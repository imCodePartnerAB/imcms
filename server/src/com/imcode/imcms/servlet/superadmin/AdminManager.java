package com.imcode.imcms.servlet.superadmin;

import com.imcode.imcms.flow.DispatchCommand;
import com.imcode.imcms.flow.DocumentPageFlow;
import com.imcode.imcms.servlet.AdminManagerSearchPage;
import com.imcode.imcms.servlet.DocumentFinder;
import com.imcode.imcms.servlet.SearchDocumentsPage;
import com.imcode.imcms.servlet.admin.AddDoc;
import com.imcode.imcms.servlet.beans.AdminManagerExpandableDatesBean;
import com.imcode.imcms.servlet.beans.AdminManagerSubreport;
import com.imcode.util.ChainableReversibleNullComparator;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.document.DocumentComparator;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentMapper;
import imcode.server.document.index.DocumentIndex;
import imcode.server.user.UserDomainObject;
import imcode.util.LocalizedMessage;
import imcode.util.Utility;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

public class AdminManager extends Administrator {

    private final static String HTML_ADMINTASK = "AdminManager_adminTask_element.htm";
    private final static String HTML_USERADMINTASK = "AdminManager_useradminTask_element.htm";
    public final static String REQUEST_PARAMETER__SHOW = "show";
    public final static String PARAMETER_VALUE__SHOW_NEW = "new";
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

    public void doGet( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
        this.doPost( req, res );
    }

    public void doPost( HttpServletRequest request, HttpServletResponse response )
            throws ServletException, IOException {

        ImcmsServices service = Imcms.getServices();
        UserDomainObject user = Utility.getLoggedOnUser( request );

        String whichButton = request.getParameter( "AdminTask" );
        if ( null != whichButton ) {

            String url = getAdminTaskUrl( whichButton );
            if ( !user.isSuperAdmin() && !user.isUserAdmin() ) {
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
            try {
                int parentId = Integer.parseInt( request.getParameter( REQUEST_PARAMETER__NEW_DOCUMENT_PARENT_ID ) );
                DocumentDomainObject parentDocument = documentMapper.getDocument( parentId );
                String createDocumentAction = request.getParameter( REQUEST_PARAMETER__CREATE_DOCUMENT_ACTION );
                if ( REQUEST_PARAMETER__ACTION__COPY.equals( createDocumentAction ) ) {
                    documentMapper.copyDocument( parentDocument, user );
                    createAndShowAdminManagerPage( request, response, null );
                } else {
                    int documentTypeId = Integer.parseInt( createDocumentAction );

                    DocumentPageFlow.SaveDocumentCommand saveNewDocumentCommand = new SaveNewDocumentCommand();
                    DispatchCommand returnCommand = new ShowAdminManagerPageCommand();

                    AddDoc.DocumentCreator documentCreator = new AddDoc.DocumentCreator( saveNewDocumentCommand, returnCommand, getServletContext() );
                    documentCreator.createDocumentAndDispatchToCreatePageFlow( documentTypeId, parentDocument, request, response );
                }
            } catch ( NumberFormatException nfe ) {
                createAndShowAdminManagerPage( request, response, ERROR_MESSAGE__NO_PARENT_ID );
            } catch ( SecurityException ex ) {
                createAndShowAdminManagerPage( request, response, ERROR_MESSAGE__NO_CREATE_PERMISSION );
            }
        } else {
            createAndShowAdminManagerPage( request, response, null );
        }
    }

    private void createAndShowAdminManagerPage( HttpServletRequest request, HttpServletResponse response,
                                                LocalizedMessage errorMessage ) throws IOException, ServletException {
        UserDomainObject loggedOnUser = Utility.getLoggedOnUser( request );
        ImcmsServices service = Imcms.getServices();
        final DocumentMapper documentMapper = service.getDocumentMapper();
        String tabToShow = null != request.getParameter( REQUEST_PARAMETER__SHOW )
                           ? request.getParameter( REQUEST_PARAMETER__SHOW ) : PARAMETER_VALUE__SHOW_NEW;

        String html_admin_part = "";

        if ( loggedOnUser.isSuperAdmin() ) {
            html_admin_part = service.getAdminTemplate( HTML_ADMINTASK, loggedOnUser, null ); // if superadmin
        } else if ( loggedOnUser.isUserAdmin() ) { //if user is useradmin
            html_admin_part = service.getAdminTemplate( HTML_USERADMINTASK, loggedOnUser, null ); //if useradmin
        }

        List documents_new = new LinkedList();        // STATUS = NEW
        List documents_modified = new LinkedList();    //MODIFIED_DATETIME > CREATED_DATETIME
        List documents_archived_less_then_one_week = new LinkedList();    //ARCHIVED_DATETIME < 7 days
        List documents_publication_end_less_then_one_week = new LinkedList();  //PUBLICATION_END_DATETIME < 7 days
        List documents_not_changed_in_six_month = new LinkedList();

        DocumentIndex index = documentMapper.getDocumentIndex();
        BooleanQuery booleanQuery = new BooleanQuery();
        Query restrictingQuery = new TermQuery( new Term( DocumentIndex.FIELD__CREATOR_ID, loggedOnUser.getId() + "" ) );
        booleanQuery.add( restrictingQuery, true, false );

        DocumentDomainObject[] documentsFound = index.search( booleanQuery, loggedOnUser );

        addFoundDocumentsToCorrespondingList( documentsFound, documents_archived_less_then_one_week, documents_publication_end_less_then_one_week, documents_not_changed_in_six_month, documents_modified, documents_new );

        AdminManagerSubreport newDocumentsSubreport = createNewDocumentsSubreport( documents_new );
        AdminManagerSubreport modifiedDocumentsSubreport = createModifiedDocumentsSubreport( documents_modified );
        AdminManagerSubreport documentsArchivedWithinOneWeekSubreport = createDocumentsArchivedWithinOneWeekSubreport( documents_archived_less_then_one_week );
        AdminManagerSubreport documentsUnpublishedWithinOneWeekSubreport = createDocumentsUnpublishedWithinOneWeekSubreport( documents_publication_end_less_then_one_week );
        AdminManagerSubreport documentsUnmodifiedForSixMonthsSubreport = createDocumentsUnmodifiedForSixMonthsSubreport( documents_not_changed_in_six_month );

        AdminManagerSubreport[] subreports = {
            newDocumentsSubreport,
            modifiedDocumentsSubreport,
            documentsArchivedWithinOneWeekSubreport,
            documentsUnpublishedWithinOneWeekSubreport,
            documentsUnmodifiedForSixMonthsSubreport,
        };
        for ( int i = 0; i < subreports.length; i++ ) {
            AdminManagerSubreport subreport = subreports[i];
            String newSortOrder = request.getParameter( subreport.getName() + "_sortorder" );
            if ( null != newSortOrder ) {
                subreport.setSortorder( newSortOrder );
            }
            Collections.sort( subreport.getDocuments(), getComparator( subreport.getSortorder() ) );
            boolean expanded = Utility.parameterIsSet( request, subreport.getName() + "_expand" )
                               && !Utility.parameterIsSet( request, subreport.getName() + "_unexpand" );
            subreport.setExpanded( expanded );
        }

        AdminManagerPage adminManagerPage = null;
        if ( tabToShow.equals( PARAMETER_VALUE__SHOW_NEW ) ) {

            newDocumentsSubreport.setMaxDocumentCount( 10 );

            AdminManagerPage newDocumentsAdminManagerPage = new AdminManagerPage();
            newDocumentsAdminManagerPage.setTabName( "new" );
            newDocumentsAdminManagerPage.setHeading( new LocalizedMessage( "web/imcms/lang/jsp/admin/admin_manager.jsp/tab_name/0" ) );

            newDocumentsAdminManagerPage.addSubreport( newDocumentsSubreport );

            modifiedDocumentsSubreport.setMaxDocumentCount( 10 );
            newDocumentsAdminManagerPage.addSubreport( modifiedDocumentsSubreport );

            adminManagerPage = newDocumentsAdminManagerPage;

        } else if ( tabToShow.equals( PARAMETER_VALUE__SHOW_REMINDERS ) ) {

            AdminManagerPage reminderAdminManagerPage = new AdminManagerPage();
            reminderAdminManagerPage.setTabName( "reminders" );
            reminderAdminManagerPage.setHeading( new LocalizedMessage( "web/imcms/lang/jsp/admin/admin_manager.jsp/tab_name/1" ) );

            documentsArchivedWithinOneWeekSubreport.setMaxDocumentCount( 10 );
            reminderAdminManagerPage.addSubreport( documentsArchivedWithinOneWeekSubreport );

            documentsUnpublishedWithinOneWeekSubreport.setMaxDocumentCount( 10 );
            reminderAdminManagerPage.addSubreport( documentsUnpublishedWithinOneWeekSubreport );

            documentsUnmodifiedForSixMonthsSubreport.setMaxDocumentCount( 10 );
            reminderAdminManagerPage.addSubreport( documentsUnmodifiedForSixMonthsSubreport );

            adminManagerPage = reminderAdminManagerPage;

        } else if ( tabToShow.equals( PARAMETER_VALUE__SHOW_SUMMARY ) ) {

            AdminManagerPage summaryAdminManagerPage = new AdminManagerPage();
            summaryAdminManagerPage.setTabName( "summary" );
            summaryAdminManagerPage.setHeading( new LocalizedMessage( "web/imcms/lang/jsp/admin/admin_manager.jsp/tab_name/2" ) );

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
            searchAdminManagerPage.setTabName( "search" );
            searchAdminManagerPage.setHeading( new LocalizedMessage( "global/Search" ) );
            adminManagerPage = searchAdminManagerPage;
        }

        adminManagerPage.setErrorMessage( errorMessage );
        adminManagerPage.setHtmlAdminPart( "".equals( html_admin_part ) ? null : html_admin_part );
        adminManagerPage.forward( request, response, loggedOnUser );
    }

    private AdminManagerSubreport createModifiedDocumentsSubreport( List documents_modified ) {
        AdminManagerSubreport modifiedDocumentsSubreport = new AdminManagerSubreport();
        modifiedDocumentsSubreport.setName( "modified" );
        modifiedDocumentsSubreport.setDocuments( documents_modified );
        modifiedDocumentsSubreport.setHeading( new LocalizedMessage( "web/imcms/lang/jsp/admin/admin_manager.jsp/subreport_heading/5" ) );
        Date oneWeekAgo = getDateOneWeekAgo();
        String dateSearchQueryString = createDateSearchQueryString( SearchDocumentsPage.DATE_TYPE__MODIFIED, oneWeekAgo, null );
        modifiedDocumentsSubreport.setSearchQueryString( dateSearchQueryString );
        return modifiedDocumentsSubreport;
    }

    private AdminManagerSubreport createNewDocumentsSubreport( List documents_new ) {
        AdminManagerSubreport newDocumentsSubreport = new AdminManagerSubreport();
        newDocumentsSubreport.setName( "new" );
        newDocumentsSubreport.setDocuments( documents_new );
        newDocumentsSubreport.setHeading( new LocalizedMessage( "web/imcms/lang/jsp/admin/admin_manager.jsp/subreport_heading/1" ) );
        Date oneWeekAgo = getDateOneWeekAgo();
        String dateSearchQueryString = createDateSearchQueryString( SearchDocumentsPage.DATE_TYPE__CREATED, oneWeekAgo, null );
        newDocumentsSubreport.setSearchQueryString( dateSearchQueryString );
        return newDocumentsSubreport;
    }

    private AdminManagerSubreport createDocumentsUnmodifiedForSixMonthsSubreport(
            List documents_not_changed_in_six_month ) {
        AdminManagerSubreport documentsUnchangedForSixMonthsSubreport = new AdminManagerSubreport();
        documentsUnchangedForSixMonthsSubreport.setName( "unchangedForSixMonths" );
        documentsUnchangedForSixMonthsSubreport.setDocuments( documents_not_changed_in_six_month );
        documentsUnchangedForSixMonthsSubreport.setHeading( new LocalizedMessage( "web/imcms/lang/jsp/admin/admin_manager.jsp/subreport_heading/4" ) );
        documentsUnchangedForSixMonthsSubreport.setSortorder( "MODR" );
        Date sixMonthsAgo = getDateSixMonthsAgo();
        String dateSearchQueryString = createDateSearchQueryString( SearchDocumentsPage.DATE_TYPE__MODIFIED, null, sixMonthsAgo );
        documentsUnchangedForSixMonthsSubreport.setSearchQueryString( dateSearchQueryString );
        return documentsUnchangedForSixMonthsSubreport;
    }

    private AdminManagerSubreport createDocumentsArchivedWithinOneWeekSubreport(
            List documents_archived_less_then_one_week ) {
        AdminManagerSubreport documentsArchivedWithinOneWeekSubreport = new AdminManagerSubreport();
        documentsArchivedWithinOneWeekSubreport.setName( "archivedWithinOneWeek" );
        documentsArchivedWithinOneWeekSubreport.setDocuments( documents_archived_less_then_one_week );
        documentsArchivedWithinOneWeekSubreport.setHeading( new LocalizedMessage( "web/imcms/lang/jsp/admin/admin_manager.jsp/subreport_heading/2" ) );
        documentsArchivedWithinOneWeekSubreport.setSortorder( "ARCR" );
        Date lastMidnight = getDateLastMidnight();
        Date oneWeekAhead = getDateOneWeekAhead();
        String dateSearchQueryString = createDateSearchQueryString( SearchDocumentsPage.DATE_TYPE__ARCHIVED, lastMidnight, oneWeekAhead );
        documentsArchivedWithinOneWeekSubreport.setSearchQueryString( dateSearchQueryString );
        return documentsArchivedWithinOneWeekSubreport;
    }

    private String createDateSearchQueryString( String dateType, Date startDate, Date endDate ) {
        String result = SearchDocumentsPage.REQUEST_PARAMETER__DATE_TYPE + "="
                        + dateType;
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
        return result;
    }

    private AdminManagerSubreport createDocumentsUnpublishedWithinOneWeekSubreport(
            List documents_publication_end_less_then_one_week ) {
        AdminManagerSubreport documentsUnpublishedWithinOneWeekSubreport = new AdminManagerSubreport();
        documentsUnpublishedWithinOneWeekSubreport.setName( "unpublishedWithinOneWeek" );
        documentsUnpublishedWithinOneWeekSubreport.setDocuments( documents_publication_end_less_then_one_week );
        documentsUnpublishedWithinOneWeekSubreport.setHeading( new LocalizedMessage( "web/imcms/lang/jsp/admin/admin_manager.jsp/subreport_heading/3" ) );
        documentsUnpublishedWithinOneWeekSubreport.setSortorder( "PUBER" );
        Date lastMidnight = getDateLastMidnight();
        Date oneWeekAhead = getDateOneWeekAhead();
        String dateSearchQueryString = createDateSearchQueryString( SearchDocumentsPage.DATE_TYPE__PUBLICATION_END, lastMidnight, oneWeekAhead );
        documentsUnpublishedWithinOneWeekSubreport.setSearchQueryString( dateSearchQueryString );
        return documentsUnpublishedWithinOneWeekSubreport;
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

    private void addFoundDocumentsToCorrespondingList( DocumentDomainObject[] documentsFound,
                                                       List documents_archived_less_then_one_week,
                                                       List documents_publication_end_less_then_one_week,
                                                       List documents_not_changed_in_six_month, List modifiedDocuments,
                                                       List newDocuments ) {
        Date lastMidnight = getDateLastMidnight();
        Date oneWeekAhead = getDateOneWeekAhead();
        Date oneWeekAgo = getDateOneWeekAgo();
        Date sixMonthAgo = getDateSixMonthsAgo();

        for ( int i = 0; i < documentsFound.length; i++ ) {
            DocumentDomainObject document = documentsFound[i];

            Date archivedDatetime = document.getArchivedDatetime();
            Date publicationEndDatetime = document.getPublicationEndDatetime();
            Date modifiedDatetime = document.getModifiedDatetime();
            Date createdDatetime = document.getCreatedDatetime();

            if ( null != archivedDatetime
                 && !archivedDatetime.before( lastMidnight )
                 && archivedDatetime.before( oneWeekAhead ) ) {
                documents_archived_less_then_one_week.add( document );
            }

            if ( null != publicationEndDatetime
                 && !publicationEndDatetime.before( lastMidnight )
                 && publicationEndDatetime.before( oneWeekAhead ) ) {
                documents_publication_end_less_then_one_week.add( document );
            }

            if ( modifiedDatetime.before( sixMonthAgo ) ) {
                documents_not_changed_in_six_month.add( document );
            }

            boolean createdInPastWeek = !createdDatetime.before( oneWeekAgo );
            if ( createdInPastWeek ) {
                newDocuments.add( document );
            }

            boolean modifiedInPastWeek = !modifiedDatetime.before( oneWeekAgo );
            if ( modifiedInPastWeek && !createdInPastWeek ) {
                modifiedDocuments.add( document );
            }

        }
    }

    private Date getDateLastMidnight() {
        return getDateTruncated( 0 );
    }

    private Date getDateSixMonthsAgo() {
        return getDateTruncated( -182 );
    }

    private Date getDateOneWeekAhead() {
        return getDateTruncated( +8 );
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

    private String getAdminTaskUrl( String whichButton ) {
        String url = "";
        if ( whichButton.equalsIgnoreCase( "UserStart" ) ) {
            url += "AdminUser";
        } else if ( whichButton.equalsIgnoreCase( "CounterStart" ) ) {
            url += "AdminCounter";
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
        } else if ( whichButton.equalsIgnoreCase( "AdminListDocs" ) ) {
            url += "AdminListDocs";
        } else if ( whichButton.equalsIgnoreCase( "AdminConference" ) ) {
            url += "AdminConference";
        } else if ( whichButton.equalsIgnoreCase( "AdminRandomTexts" ) ) {
            url += "AdminRandomTexts";
        } else if ( whichButton.equalsIgnoreCase( "AdminQuestions" ) ) {
            url += "AdminQuestions";
        } else if ( whichButton.equalsIgnoreCase( "AdminSection" ) ) {
            url += "AdminSection";
        } else if ( whichButton.equalsIgnoreCase( "AdminCategories" ) ) {
            url += "AdminCategories";
        }
        return url;

    }

    private static class SaveNewDocumentCommand implements DocumentPageFlow.SaveDocumentCommand {

        public void saveDocument( DocumentDomainObject document, UserDomainObject user ) {
            Imcms.getServices().getDocumentMapper().saveNewDocument( document, user );
        }
    }

    private class ShowAdminManagerPageCommand implements DispatchCommand {

        public void dispatch( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
            createAndShowAdminManagerPage( request, response, null );
        }
    }
} // End of class
