package com.imcode.imcms.servlet.superadmin;

import com.imcode.imcms.api.util.ChainableReversibleNullComparator;
import com.imcode.imcms.servlet.AdminManagerSearchPage;
import com.imcode.imcms.servlet.DocumentFinder;
import com.imcode.imcms.servlet.beans.AdminManagerExpandableDatesBean;
import com.imcode.imcms.servlet.beans.AdminManagerSubreport;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.document.DocumentComparator;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.index.DocumentIndex;
import imcode.server.user.UserDomainObject;
import imcode.util.LocalizedMessage;
import imcode.util.Utility;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

public class AdminManager extends Administrator {

    private final static Logger log = Logger.getLogger( AdminManager.class.getName() );

    private final static String HTML_ADMINTASK = "AdminManager_adminTask_element.htm";
    private final static String HTML_USERADMINTASK = "AdminManager_useradminTask_element.htm";
    public final static String REQUEST_PARAMETER__SHOW = "show";
    private final static String PARAMETER_VALUE__SHOW_NEW = "new";
    private final static String PARAMETER_VALUE__SHOW_REMINDERS = "reminders";
    private final static String PARAMETER_VALUE__SHOW_SUMMARY = "summary";
    private final static String PARAMETER_VALUE__SHOW_SEARCH = "search";
    public final static String LIST_TYPE__list_new_not_approved = "list_new_not_approved";
    public final static String LIST_TYPE__list_documents_archived_less_then_one_week = "list_documents_archived_less_then_one_week";
    public final static String LIST_TYPE__list_documents_publication_end_less_then_one_week = "list_documents_publication_end_less_then_one_week";
    public final static String LIST_TYPE__list_documents_not_changed_in_six_month = "list_documents_not_changed_in_six_month";
    public final static String LIST_TYPE__list_documents_changed = "list_documents_changed";
    public final static String LIST_TYPE__SEARCH_LIST = "search_list";

    public static final int DEFAULT_DOCUMENTS_PER_LIST = 5;
    public static final String REQUEST_PARAMETER__list_new_not_approved_current_sortorder = "list_new_not_approved_current_sortorder";
    public static final String REQUEST_PARAMETER__list_documents_archived_less_then_one_week_current_sortorder = "list_documents_archived_less_then_one_week_current_sortorder";
    public static final String REQUEST_PARAMETER__list_documents_publication_end_less_then_one_week_current_sortorder = "list_documents_publication_end_less_then_one_week_current_sortorder";
    public static final String REQUEST_PARAMETER__list_documents_not_changed_in_six_month_current_sortorder = "list_documents_not_changed_in_six_month_current_sortorder";
    public static final String REQUEST_PARAMETER__list_documents_changed_current_sortorder = "list_documents_changed_current_sortorder";
    public static final String REQUEST_PARAMETER__SEARCH_LIST_CURRENT_SORTORDER = "search_list_current_sortorder";
    private static final String REQUEST_PARAMETER__NEW_SORTORDER = "new_sortorder";
    private static final String REQUEST_PARAMETER__LIST_TYPE = "list_type";
    public static final String REQUEST_PARAMETER__list_new_not_approved_current_expand = "list_new_not_approved_current_expand";
    public static final String REQUEST_PARAMETER__list_documents_changed_current_expand = "list_documents_changed_current_expand";
    public static final String REQUEST_PARAMETER__list_documents_archived_less_then_one_week_current_expand = "list_documents_archived_less_then_one_week_current_expand";
    public static final String REQUEST_PARAMETER__list_documents_publication_end_less_then_one_week_current_expand = "list_documents_publication_end_less_then_one_week_current_expand";
    public static final String REQUEST_PARAMETER__list_documents_not_changed_in_six_month_current_expand = "list_documents_not_changed_in_six_month_current_expand";
    public static final String REQUEST_PARAMETER__showAll = "showAll";
    public static final String REQUEST_PARAMETER__hideAll = "hideAll";
    public static final String REQUEST_PARAMETER__SEARCH_BTN = "search_btn";
    public static final String REQUEST_PARAMETER__SEARCH_STRING = "search_string";
    public static final String REQUEST_PARAMETER__RESET_BTN = "reset_btn";
    public static final String REQUEST_PARAMETER__PERMISSION = "permission";
    public static final String REQUEST_PARAMETER__DATE_TYPE = "date_type";
    public static final String REQUEST_PARAMETER__DATE_START = "date_start";
    public static final String REQUEST_PARAMETER__DATE_END = "date_end";
    public static final String REQUEST_PARAMETER__HITS_PER_PAGE = "hits_per_page";
    public static final String REQUEST_PARAMETER__FROMPAGE = "frompage";
    public static final String PAGE_SEARCH = "search";

    public void doGet( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
        this.doPost( req, res );
    }

    public void doPost( HttpServletRequest req, HttpServletResponse res )
            throws ServletException, IOException {

        ImcmsServices service = Imcms.getServices();
        UserDomainObject loggedOnUser = Utility.getLoggedOnUser( req );

        String whichButton = req.getParameter( "AdminTask" );
        if ( null != whichButton ) {

            if ( !loggedOnUser.isSuperAdmin() && !loggedOnUser.isUserAdmin() ) {
                String header = "Error in AdminManager.";
                Properties langproperties = service.getLanguageProperties( loggedOnUser );
                String msg = langproperties.getProperty( "error/servlet/global/no_administrator" ) + "<br>";
                log.debug( header + "- user is not an administrator" );

                new AdminError( req, res, header, msg );
                return;
            }

            String url = getAdminTaskUrl( whichButton );
            if ( StringUtils.isNotBlank( url ) ) {
                res.sendRedirect( url );
                return;
            }
        } else if ( PAGE_SEARCH.equals( req.getParameter( REQUEST_PARAMETER__FROMPAGE ) ) ) {
            req.getRequestDispatcher( "/servlet/SearchDocuments" ).forward( req, res );
            return;
        }

        String tabToShow = null != req.getParameter( REQUEST_PARAMETER__SHOW )
                           ? req.getParameter( REQUEST_PARAMETER__SHOW ) : PARAMETER_VALUE__SHOW_NEW;

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

        DocumentIndex index = service.getDocumentMapper().getDocumentIndex();
        BooleanQuery booleanQuery = new BooleanQuery();
        Query restrictingQuery = new TermQuery( new Term( DocumentIndex.FIELD__CREATOR_ID, loggedOnUser.getId() + "" ) );
        booleanQuery.add( restrictingQuery, true, false );

        HashMap current_sortorderMap = new HashMap();
        HashMap expand_listMap = new HashMap();
        String sortorder;
        String new_sortorder = "";
        String list_toChange_sortorder = "";
        DocumentDomainObject[] documentsFound = index.search( booleanQuery, loggedOnUser );

        AdminManagerPage adminManagerPage = null ;
        if ( tabToShow.equals( PARAMETER_VALUE__SHOW_NEW ) ) {

            addNewNotApprovedDocumentsToList( booleanQuery, documents_new, index, loggedOnUser );

            if ( null != req.getParameter( REQUEST_PARAMETER__NEW_SORTORDER ) ) {
                new_sortorder = req.getParameter( REQUEST_PARAMETER__NEW_SORTORDER );
                list_toChange_sortorder = LIST_TYPE__list_new_not_approved;
            }
            sortorder = getSortorderForListType( list_toChange_sortorder, new_sortorder, req.getParameter( REQUEST_PARAMETER__list_new_not_approved_current_sortorder ), LIST_TYPE__list_new_not_approved, "MOD" );
            current_sortorderMap.put( LIST_TYPE__list_new_not_approved, sortorder );
            Collections.sort( documents_new, getComparator( sortorder ) );
            setNewExpandStatusForList( req, expand_listMap, LIST_TYPE__list_new_not_approved, REQUEST_PARAMETER__list_new_not_approved_current_expand );

            // documents_new_not_approved = new AdminManagerSubreport(LIST_TYPE__list_new_not_approved, "", "", DEFAULT_DOCUMENTS_PER_LIST, documents_new  );
            adminManagerPage = createNewDocumentsAdminManagerPage( documents_new );

        } else if ( tabToShow.equals( PARAMETER_VALUE__SHOW_REMINDERS ) ) {


            if ( null != req.getParameter( REQUEST_PARAMETER__NEW_SORTORDER ) ) {
                new_sortorder = req.getParameter( REQUEST_PARAMETER__NEW_SORTORDER );
                list_toChange_sortorder = req.getParameter( REQUEST_PARAMETER__LIST_TYPE );
            }
            sortorder = getSortorderForListType( list_toChange_sortorder, new_sortorder, req.getParameter( REQUEST_PARAMETER__list_documents_archived_less_then_one_week_current_sortorder ), LIST_TYPE__list_documents_archived_less_then_one_week, "ARCR" );
            current_sortorderMap.put( LIST_TYPE__list_documents_archived_less_then_one_week, sortorder );

            sortorder = getSortorderForListType( list_toChange_sortorder, new_sortorder, req.getParameter( REQUEST_PARAMETER__list_documents_publication_end_less_then_one_week_current_sortorder ), LIST_TYPE__list_documents_publication_end_less_then_one_week, "PUBER" );
            current_sortorderMap.put( LIST_TYPE__list_documents_publication_end_less_then_one_week, sortorder );

            sortorder = getSortorderForListType( list_toChange_sortorder, new_sortorder, req.getParameter( REQUEST_PARAMETER__list_documents_not_changed_in_six_month_current_sortorder ), LIST_TYPE__list_documents_not_changed_in_six_month, "MOD" );
            current_sortorderMap.put( LIST_TYPE__list_documents_not_changed_in_six_month, sortorder );

            addFoundDocumentsToCorrespondingList( documentsFound, documents_archived_less_then_one_week, documents_publication_end_less_then_one_week, documents_not_changed_in_six_month, null, current_sortorderMap );

            setNewExpandStatusForList( req, expand_listMap, LIST_TYPE__list_documents_archived_less_then_one_week, REQUEST_PARAMETER__list_documents_archived_less_then_one_week_current_expand );
            setNewExpandStatusForList( req, expand_listMap, LIST_TYPE__list_documents_publication_end_less_then_one_week, REQUEST_PARAMETER__list_documents_publication_end_less_then_one_week_current_sortorder );
            setNewExpandStatusForList( req, expand_listMap, LIST_TYPE__list_documents_not_changed_in_six_month, REQUEST_PARAMETER__list_documents_not_changed_in_six_month_current_sortorder );

            adminManagerPage = createReminderAdminManagerPage( documents_archived_less_then_one_week, documents_publication_end_less_then_one_week, documents_not_changed_in_six_month, documents_modified );

        } else if ( tabToShow.equals( PARAMETER_VALUE__SHOW_SUMMARY ) ) {

            addNewNotApprovedDocumentsToList( booleanQuery, documents_new, index, loggedOnUser );

            if ( null != req.getParameter( REQUEST_PARAMETER__NEW_SORTORDER ) ) {
                new_sortorder = req.getParameter( REQUEST_PARAMETER__NEW_SORTORDER );
                list_toChange_sortorder = req.getParameter( REQUEST_PARAMETER__LIST_TYPE );
            }
            sortorder = getSortorderForListType( list_toChange_sortorder, new_sortorder, req.getParameter( REQUEST_PARAMETER__list_new_not_approved_current_sortorder ), LIST_TYPE__list_new_not_approved, "MOD" );
            current_sortorderMap.put( LIST_TYPE__list_new_not_approved, sortorder );
            Collections.sort( documents_new, getComparator( sortorder ) );

            sortorder = getSortorderForListType( list_toChange_sortorder, new_sortorder, req.getParameter( REQUEST_PARAMETER__list_documents_changed_current_sortorder ), LIST_TYPE__list_documents_changed, "MOD" );
            current_sortorderMap.put( LIST_TYPE__list_documents_changed, sortorder );

            sortorder = getSortorderForListType( list_toChange_sortorder, new_sortorder, req.getParameter( REQUEST_PARAMETER__list_documents_publication_end_less_then_one_week_current_sortorder ), LIST_TYPE__list_documents_publication_end_less_then_one_week, "PUBER" );
            current_sortorderMap.put( LIST_TYPE__list_documents_publication_end_less_then_one_week, sortorder );

            sortorder = getSortorderForListType( list_toChange_sortorder, new_sortorder, req.getParameter( REQUEST_PARAMETER__list_documents_archived_less_then_one_week_current_sortorder ), LIST_TYPE__list_documents_archived_less_then_one_week, "ARCR" );
            current_sortorderMap.put( LIST_TYPE__list_documents_archived_less_then_one_week, sortorder );

            sortorder = getSortorderForListType( list_toChange_sortorder, new_sortorder, req.getParameter( REQUEST_PARAMETER__list_documents_not_changed_in_six_month_current_sortorder ), LIST_TYPE__list_documents_not_changed_in_six_month, "MOD" );
            current_sortorderMap.put( LIST_TYPE__list_documents_not_changed_in_six_month, sortorder );

            addFoundDocumentsToCorrespondingList( documentsFound, documents_archived_less_then_one_week, documents_publication_end_less_then_one_week, documents_not_changed_in_six_month, documents_modified, current_sortorderMap );

            expand_listMap.put( LIST_TYPE__list_new_not_approved, null
                                                                  != req.getParameter( REQUEST_PARAMETER__list_new_not_approved_current_expand )
                                                                  ? req.getParameter( REQUEST_PARAMETER__list_new_not_approved_current_expand )
                                                                  : "hide" );
            expand_listMap.put( LIST_TYPE__list_documents_changed, null
                                                                   != req.getParameter( REQUEST_PARAMETER__list_documents_changed_current_expand )
                                                                   ? req.getParameter( REQUEST_PARAMETER__list_documents_changed_current_expand )
                                                                   : "hide" );
            expand_listMap.put( LIST_TYPE__list_documents_publication_end_less_then_one_week, null
                                                                                              != req.getParameter( REQUEST_PARAMETER__list_documents_publication_end_less_then_one_week_current_expand )
                                                                                              ? req.getParameter( REQUEST_PARAMETER__list_documents_publication_end_less_then_one_week_current_expand )
                                                                                              : "hide" );
            expand_listMap.put( LIST_TYPE__list_documents_archived_less_then_one_week, null
                                                                                       != req.getParameter( REQUEST_PARAMETER__list_documents_archived_less_then_one_week_current_expand )
                                                                                       ? req.getParameter( REQUEST_PARAMETER__list_documents_archived_less_then_one_week_current_expand )
                                                                                       : "hide" );
            expand_listMap.put( LIST_TYPE__list_documents_not_changed_in_six_month, null
                                                                                    != req.getParameter( REQUEST_PARAMETER__list_documents_not_changed_in_six_month_current_expand )
                                                                                    ? req.getParameter( REQUEST_PARAMETER__list_documents_not_changed_in_six_month_current_expand )
                                                                                    : "hide" );

            setNewExpandStatusForList( req, expand_listMap, LIST_TYPE__list_new_not_approved, REQUEST_PARAMETER__list_new_not_approved_current_expand );
            setNewExpandStatusForList( req, expand_listMap, LIST_TYPE__list_documents_changed, REQUEST_PARAMETER__list_documents_changed_current_expand );
            setNewExpandStatusForList( req, expand_listMap, LIST_TYPE__list_documents_publication_end_less_then_one_week, REQUEST_PARAMETER__list_documents_publication_end_less_then_one_week_current_sortorder );
            setNewExpandStatusForList( req, expand_listMap, LIST_TYPE__list_documents_archived_less_then_one_week, REQUEST_PARAMETER__list_documents_archived_less_then_one_week_current_expand );
            setNewExpandStatusForList( req, expand_listMap, LIST_TYPE__list_documents_not_changed_in_six_month, REQUEST_PARAMETER__list_documents_not_changed_in_six_month_current_sortorder );

            adminManagerPage = createSummaryAdminManagerPage( documents_new, documents_modified, documents_archived_less_then_one_week, documents_publication_end_less_then_one_week, documents_not_changed_in_six_month );
        } else if ( tabToShow.equals( PARAMETER_VALUE__SHOW_SEARCH ) ) {
            DocumentFinder documentFinder = new DocumentFinder( new AdminManagerSearchPage() );
            documentFinder.addExtraSearchResultColumn( new DatesSummarySearchResultColumn() );
            documentFinder.forward( req, res );
        }

        if (!res.isCommitted()) {
            adminManagerPage.setHtmlAdminPart( "".equals( html_admin_part ) ? null : html_admin_part );
            adminManagerPage.forward( req, res, loggedOnUser );
        }
    }

    private AdminManagerPage createSummaryAdminManagerPage( List documents_new, List documents_modified,
                                                                         List documents_archived_less_then_one_week,
                                                                         List documents_publication_end_less_then_one_week,
                                                                         List documents_not_changed_in_six_month ) {
        AdminManagerPage summaryAdminManagerPage = new AdminManagerPage();
        summaryAdminManagerPage.setName( "admin_manager_summary.jsp" );
        summaryAdminManagerPage.setTabName( "summary" );
        summaryAdminManagerPage.setHeading( new LocalizedMessage( "web/imcms/lang/jsp/admin/admin_manager.jsp/tab_name/2" ) );

        AdminManagerSubreport newDocumentsSubreport = createNewDocumentsSubreport( documents_new ) ;
        summaryAdminManagerPage.addSubreport( newDocumentsSubreport );

        AdminManagerSubreport modifiedDocumentsSubreport = createModifiedDocumentsSubreport( documents_modified );
        summaryAdminManagerPage.addSubreport( modifiedDocumentsSubreport );

        AdminManagerSubreport documentsArchivedWithinOneWeekSubreport = createDocumentsArchivedWithinOneWeekSubreport( documents_archived_less_then_one_week );
        summaryAdminManagerPage.addSubreport( documentsArchivedWithinOneWeekSubreport );

        AdminManagerSubreport documentsUnpublishedWithinOneWeekSubreport = createDocumentsUnpublishedWithinOneWeekSubreport( documents_publication_end_less_then_one_week );
        summaryAdminManagerPage.addSubreport( documentsUnpublishedWithinOneWeekSubreport );

        AdminManagerSubreport documentsUnmodifiedForSixMonthsSubreport = createDocumentsUnmodifiedForSixMonthsSubreport( documents_not_changed_in_six_month );
        summaryAdminManagerPage.addSubreport( documentsUnmodifiedForSixMonthsSubreport );
        return summaryAdminManagerPage;
    }

    private AdminManagerSubreport createModifiedDocumentsSubreport( List documents_modified ) {
        AdminManagerSubreport modifiedDocumentsSubreport = new AdminManagerSubreport();
        modifiedDocumentsSubreport.setDocuments( documents_modified );
        modifiedDocumentsSubreport.setHeading( new LocalizedMessage( "web/imcms/lang/jsp/admin/admin_manager.jsp/subreport_heading/5" ));
        return modifiedDocumentsSubreport;
    }

    private AdminManagerPage createNewDocumentsAdminManagerPage( List documents_new ) {
        AdminManagerSubreport newDocumentsSubreport = createNewDocumentsSubreport( documents_new );
        newDocumentsSubreport.setMaxDocumentCount( 0 );

        AdminManagerPage newDocumentsAdminManagerPage = new AdminManagerPage();
        newDocumentsAdminManagerPage.setName( "admin_manager_new.jsp");
        newDocumentsAdminManagerPage.setTabName( "new" );
        newDocumentsAdminManagerPage.setHeading( new LocalizedMessage( "web/imcms/lang/jsp/admin/admin_manager.jsp/tab_name/0" ) );
        newDocumentsAdminManagerPage.addSubreport(newDocumentsSubreport) ;
        return newDocumentsAdminManagerPage;
    }

    private AdminManagerSubreport createNewDocumentsSubreport( List documents_new ) {
        AdminManagerSubreport newDocumentsSubreport = new AdminManagerSubreport();
        newDocumentsSubreport.setDocuments( documents_new );
        newDocumentsSubreport.setHeading( new LocalizedMessage( "web/imcms/lang/jsp/admin/admin_manager.jsp/subreport_heading/1" ) );
        return newDocumentsSubreport;
    }

    private AdminManagerPage createReminderAdminManagerPage( List documents_archived_less_then_one_week,
                                                             List documents_publication_end_less_then_one_week,
                                                             List documents_not_changed_in_six_month,
                                                             List documents_modified ) {
        AdminManagerPage reminderAdminManagerPage = new AdminManagerPage();
        reminderAdminManagerPage.setName( "admin_manager_reminders.jsp" );
        reminderAdminManagerPage.setTabName( "reminders" );
        reminderAdminManagerPage.setHeading( new LocalizedMessage( "web/imcms/lang/jsp/admin/admin_manager.jsp/tab_name/1" ) );

        AdminManagerSubreport documentsArchivedWithinOneWeekSubreport = createDocumentsArchivedWithinOneWeekSubreport( documents_archived_less_then_one_week );
        documentsArchivedWithinOneWeekSubreport.setMaxDocumentCount( 0 );
        reminderAdminManagerPage.addSubreport(documentsArchivedWithinOneWeekSubreport);

        AdminManagerSubreport modifiedDocumentsSubreport = createModifiedDocumentsSubreport( documents_modified );
        //modifiedDocumentsSubreport.setMaxDocumentCount( 0 );
        reminderAdminManagerPage.addSubreport( modifiedDocumentsSubreport );

        AdminManagerSubreport documentsUnpublishedWithinOneWeekSubreport = createDocumentsUnpublishedWithinOneWeekSubreport( documents_publication_end_less_then_one_week );
        documentsUnpublishedWithinOneWeekSubreport.setMaxDocumentCount( 0 );
        reminderAdminManagerPage.addSubreport( documentsUnpublishedWithinOneWeekSubreport );

        AdminManagerSubreport documentsUnchangedForSixMonthsSubreport = createDocumentsUnmodifiedForSixMonthsSubreport( documents_not_changed_in_six_month );
        documentsUnchangedForSixMonthsSubreport.setMaxDocumentCount( 0 );
        reminderAdminManagerPage.addSubreport( documentsUnchangedForSixMonthsSubreport );

        return reminderAdminManagerPage;
    }

    private AdminManagerSubreport createDocumentsUnmodifiedForSixMonthsSubreport(
            List documents_not_changed_in_six_month ) {
        AdminManagerSubreport documentsUnchangedForSixMonthsSubreport = new AdminManagerSubreport();
        documentsUnchangedForSixMonthsSubreport.setDocuments( documents_not_changed_in_six_month );
        documentsUnchangedForSixMonthsSubreport.setHeading( new LocalizedMessage( "web/imcms/lang/jsp/admin/admin_manager.jsp/subreport_heading/4" ) );
        return documentsUnchangedForSixMonthsSubreport;
    }

    private AdminManagerSubreport createDocumentsArchivedWithinOneWeekSubreport(
            List documents_archived_less_then_one_week ) {
        AdminManagerSubreport documentsArchivedWithinOneWeekSubreport = new AdminManagerSubreport();
        documentsArchivedWithinOneWeekSubreport.setDocuments( documents_archived_less_then_one_week );
        documentsArchivedWithinOneWeekSubreport.setHeading( new LocalizedMessage( "web/imcms/lang/jsp/admin/admin_manager.jsp/subreport_heading/2" ));
        return documentsArchivedWithinOneWeekSubreport;
    }

    private AdminManagerSubreport createDocumentsUnpublishedWithinOneWeekSubreport(
            List documents_publication_end_less_then_one_week ) {
        AdminManagerSubreport documentsUnpublishedWithinOneWeekSubreport = new AdminManagerSubreport();
        documentsUnpublishedWithinOneWeekSubreport.setDocuments( documents_publication_end_less_then_one_week );
        documentsUnpublishedWithinOneWeekSubreport.setHeading( new LocalizedMessage( "web/imcms/lang/jsp/admin/admin_manager.jsp/subreport_heading/3" ) );
        return documentsUnpublishedWithinOneWeekSubreport;
    }

    private void setNewExpandStatusForList( HttpServletRequest req, HashMap expand_listMap, String list,
                                            String request_parameter ) {

        String expand_status = null != req.getParameter( request_parameter )
                               ? req.getParameter( request_parameter ) : "hide";

        if ( list.equals( req.getParameter( "list_type" ) ) ) {
            if ( null != req.getParameter( REQUEST_PARAMETER__showAll ) ) {
                expand_status = "expand";
            }
            if ( null != req.getParameter( REQUEST_PARAMETER__hideAll ) ) {
                expand_status = "hide";
            }
        }

        expand_listMap.put( list, expand_status );
    }

    private String getSortorderForListType( String list_toChange_sortorder, String new_sortorder,
                                            String current_sortorder, String list_type, String default_sortorder ) {
        String sortorder;
        if ( list_toChange_sortorder.equals( list_type ) ) {
            sortorder = new_sortorder;
        } else {
            sortorder = null != current_sortorder ? current_sortorder : default_sortorder;
        }
        return sortorder;
    }

    private static class DatesSummarySearchResultColumn implements DocumentFinder.SearchResultColumn {

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

    public static class AdminManagerPage {
        String name ;
        LocalizedMessage heading ;
        String tabName ;
        List subreports = new ArrayList() ;
        String htmlAdminPart ;
        public static final String REQUEST_ATTRIBUTE__PAGE = "ampage";

        public LocalizedMessage getHeading() {
            return heading;
        }

        public void setHeading( LocalizedMessage heading ) {
            this.heading = heading;
        }

        public void setName( String name ) {
            this.name = name;
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
            request.setAttribute( REQUEST_ATTRIBUTE__PAGE, this );
            String forwardPath = "/imcms/" + user.getLanguageIso639_2() + "/jsp/admin/admin_manager.jsp";
            request.getRequestDispatcher( forwardPath ).forward( request, response );
        }

    }

    private Date getDate( int days ) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime( new Date() );
        calendar.add( Calendar.DATE, days );
        return calendar.getTime();
    }

    private void addFoundDocumentsToCorrespondingList( DocumentDomainObject[] documentsFound,
                                                       List documents_archived_less_then_one_week,
                                                       List documents_publication_end_less_then_one_week,
                                                       List documents_not_changed_in_six_month, List documents_changed,
                                                       HashMap sortorderMap ) {
        Date now = new Date();
        Date oneWeekAhead = getDate( +7 );
        Date sixMonthAgo = getDate( -182 );

        for ( int i = 0; i < documentsFound.length; i++ ) {
            if ( null != documents_archived_less_then_one_week && null != documentsFound[i].getArchivedDatetime()
                 && documentsFound[i].getArchivedDatetime().after( now )
                 && documentsFound[i].getArchivedDatetime().before( oneWeekAhead ) ) {
                documents_archived_less_then_one_week.add( documentsFound[i] );
                Collections.sort( documents_archived_less_then_one_week, getComparator( sortorderMap.get( LIST_TYPE__list_documents_archived_less_then_one_week ).toString() ) );
            }

            if ( null != documents_publication_end_less_then_one_week
                 && null != documentsFound[i].getPublicationEndDatetime()
                 && documentsFound[i].getPublicationEndDatetime().after( now )
                 && documentsFound[i].getPublicationEndDatetime().before( oneWeekAhead ) ) {
                documents_publication_end_less_then_one_week.add( documentsFound[i] );
                Collections.sort( documents_publication_end_less_then_one_week, getComparator( sortorderMap.get( LIST_TYPE__list_documents_publication_end_less_then_one_week ).toString() ) );
            }

            if ( null != documents_not_changed_in_six_month && null != documentsFound[i].getModifiedDatetime()
                 && documentsFound[i].getModifiedDatetime().before( sixMonthAgo ) ) {
                documents_not_changed_in_six_month.add( documentsFound[i] );
                Collections.sort( documents_not_changed_in_six_month, getComparator( sortorderMap.get( LIST_TYPE__list_documents_not_changed_in_six_month ).toString() ) );
            }

            if ( null != documents_changed && null != documentsFound[i].getModifiedDatetime()
                 && documentsFound[i].getModifiedDatetime().after( documentsFound[i].getCreatedDatetime() ) ) {
                documents_changed.add( documentsFound[i] );
                Collections.sort( documents_changed, getComparator( sortorderMap.get( LIST_TYPE__list_documents_changed ).toString() ) );
            }
        }
    }

    private void addNewNotApprovedDocumentsToList( BooleanQuery booleanQuery, List documents_new, DocumentIndex index,
                                                   UserDomainObject loggedOnUser ) {
        Query query = new TermQuery( new Term( DocumentIndex.FIELD__STATUS, DocumentDomainObject.STATUS_NEW + "" ) );
        booleanQuery.add( query, true, false );
        documents_new.addAll( Arrays.asList( index.search( booleanQuery, loggedOnUser ) ) );
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

} // End of class
