package com.imcode.imcms.servlet.superadmin;

import com.imcode.imcms.api.util.ChainableReversibleNullComparator;
import com.imcode.imcms.servlet.AdminManagerSearchPage;
import com.imcode.imcms.servlet.DocumentFinder;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentComparator;
import imcode.server.document.index.DocumentIndex;
import imcode.server.user.UserDomainObject;
import imcode.util.DateConstants;
import imcode.util.Utility;
import imcode.util.LocalizedMessage;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class AdminManager extends Administrator {

    private final static Logger log = Logger.getLogger( AdminManager.class.getName() );

    private final static String JSP__ADMIN_MANAGER_NEW = "admin_manager_new.jsp";
    private final static String JSP__ADMIN_MANAGER_REMINDERS = "admin_manager_reminders.jsp";
    private final static String JSP__ADMIN_MANAGER_SUMMARY = "admin_manager_summary.jsp";
    private final static String JSP__ADMIN_MANAGER_SEARCH = "admin_manager_search.jsp";
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

    public static final int DEFAULT_DOCUMENTS_PER_PAGE = 20;
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
        String fileToForwardTo = JSP__ADMIN_MANAGER_NEW;

        // parse and return the html_admin_part
        Vector vec = new Vector();
        String html_admin_part = "";

        if ( loggedOnUser.isSuperAdmin() ) {
            html_admin_part = service.getAdminTemplate( HTML_ADMINTASK, loggedOnUser, vec ); // if superadmin
        } else if ( loggedOnUser.isUserAdmin() ) { //if user is useradmin
            html_admin_part = service.getAdminTemplate( HTML_USERADMINTASK, loggedOnUser, vec ); //if useradmin
        }

        List documents_new = new LinkedList();        // STATUS = NEW
        List documents_changed = new LinkedList();    //MODIFIED_DATETIME > CREATED_DATETIME
        List documents_archived_less_then_one_week = new LinkedList();    //ARCHIVED_DATETIME < 7 days
        List documents_publication_end_less_then_one_week = new LinkedList();  //PUBLICATION_END_DATETIME < 7 days
        List documents_not_changed_in_six_month = new LinkedList();

        DocumentIndex index = service.getDocumentMapper().getDocumentIndex();
        BooleanQuery booleanQuery = new BooleanQuery();
        Query restrictingQuery = new TermQuery( new Term( DocumentIndex.FIELD__CREATOR_ID, loggedOnUser.getId() + "" ) );
        booleanQuery.add( restrictingQuery, true, false );

        HashMap current_sortorderMap = new HashMap();
        HashMap expand_listMap = new HashMap();
        HashMap subreports = new HashMap();
        String sortorder;
        String new_sortorder = "";
        String list_toChange_sortorder = "";
        DocumentDomainObject[] documentsFound = index.search( booleanQuery, loggedOnUser );

        if ( tabToShow.equals( PARAMETER_VALUE__SHOW_NEW ) ) {

            fileToForwardTo = JSP__ADMIN_MANAGER_NEW;
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

        } else if ( tabToShow.equals( PARAMETER_VALUE__SHOW_REMINDERS ) ) {

            fileToForwardTo = JSP__ADMIN_MANAGER_REMINDERS;

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

        } else if ( tabToShow.equals( PARAMETER_VALUE__SHOW_SUMMARY ) ) {

            fileToForwardTo = JSP__ADMIN_MANAGER_SUMMARY;
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

            addFoundDocumentsToCorrespondingList( documentsFound, documents_archived_less_then_one_week, documents_publication_end_less_then_one_week, documents_not_changed_in_six_month, documents_changed, current_sortorderMap );

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

        } else if ( tabToShow.equals( PARAMETER_VALUE__SHOW_SEARCH ) ) {
            fileToForwardTo = JSP__ADMIN_MANAGER_SEARCH;
        }

        AdminManagerPage page = new AdminManagerPage( "".equals( html_admin_part ) ? null : html_admin_part,
                                                      documents_new,
                                                      documents_changed, documents_archived_less_then_one_week,
                                                      documents_publication_end_less_then_one_week,
                                                      documents_not_changed_in_six_month,
                                                      fileToForwardTo,
                                                      current_sortorderMap,
                                                      expand_listMap,
                                                      subreports );

        page.forward( req, res, loggedOnUser );
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

    public static class AdminManagerPage {

        public static final String REQUEST_ATTRIBUTE__PAGE = "ampage";
        String html_admin_part;
        List documents_new;
        List documents_changed;
        List documents_archived_less_then_one_week;
        List documents_publication_end_less_then_one_week;
        List documents_not_changed_in_six_month;
        String fileToForwardTo;
        HashMap current_sortorderMap;
        HashMap expand_listMap;
        HashMap subreports;

        DocumentFinder documentFinder;

        public AdminManagerPage( String html_admin_part,
                                 List documents_new,
                                 List documents_changed,
                                 List documents_archived_less_then_one_week,
                                 List documents_publication_end_less_then_one_week,
                                 List documents_not_changed_in_six_month,
                                 String filename,
                                 HashMap current_sortorderMap,
                                 HashMap expand_listMap,
                                 HashMap subreports ) {
            this.html_admin_part = html_admin_part;
            this.documents_new = documents_new;
            this.documents_changed = documents_changed;
            this.documents_archived_less_then_one_week = documents_archived_less_then_one_week;
            this.documents_publication_end_less_then_one_week = documents_publication_end_less_then_one_week;
            this.documents_not_changed_in_six_month = documents_not_changed_in_six_month;
            this.fileToForwardTo = filename;
            this.current_sortorderMap = current_sortorderMap;
            this.expand_listMap = expand_listMap;
            this.subreports = subreports;
            documentFinder = new DocumentFinder( new AdminManagerSearchPage( this ) );
            documentFinder.addExtraSearchResultColumn( new DocumentFinder.SearchResultColumn() {
                public String render( DocumentDomainObject document, HttpServletRequest request ) {
                    DateFormat dateFormat = new SimpleDateFormat( DateConstants.DATETIME_NO_SECONDS_FORMAT_STRING ) ;
                    String formattedDatetime = dateFormat.format( document.getModifiedDatetime() );
                    return StringEscapeUtils.escapeHtml( formattedDatetime ).replaceAll( " ", "&nbsp;" ) ;
                }

                public LocalizedMessage getName() {
                    return new LocalizedMessage( "global/changed" );
                }
            } );
        }

        public String getHtml_admin_part() {
            return html_admin_part;
        }

        public List getDocuments_new() {
            return documents_new;
        }

        public List getDocuments_changed() {
            return documents_changed;
        }

        public List getDocuments_archived_less_then_one_week() {
            return documents_archived_less_then_one_week;
        }

        public List getDocuments_publication_end_less_then_one_week() {
            return documents_publication_end_less_then_one_week;
        }

        public List getDocuments_not_changed_in_six_month() {
            return documents_not_changed_in_six_month;
        }

        public void forward( HttpServletRequest request, HttpServletResponse response, UserDomainObject user ) throws IOException, ServletException {
            if ( JSP__ADMIN_MANAGER_SEARCH.equals( fileToForwardTo ) ) {
                documentFinder.forward( request, response );
            } else {
                request.setAttribute( REQUEST_ATTRIBUTE__PAGE, this );
                String forwardPath = "/imcms/" + user.getLanguageIso639_2() + "/jsp/admin/" + fileToForwardTo;
                request.getRequestDispatcher( forwardPath ).forward( request, response );
            }
        }

        public HashMap getCurrent_sortorderMap() {
            return current_sortorderMap;
        }

        public HashMap getExpand_listMap() {
            return expand_listMap;
        }

        public HashMap getSubreports() {
            return subreports;
        }

        public DocumentFinder getDocumentFinder() {
            return documentFinder;
        }
    }

    public static class AdminManagerSubreport {

        String name;
        String sortorder;
        String expand_status;
        int hits_per_page;
        List documentsFound;

        public AdminManagerSubreport( String name,
                                      String sortorder,
                                      String expand_status,
                                      int hits_per_page,
                                      List documentsFound ) {
            this.name = name;
            this.sortorder = sortorder;
            this.expand_status = expand_status;
            this.hits_per_page = hits_per_page;
            this.documentsFound = documentsFound;
        }

        public String getName() {
            return name;
        }

        public String getSortorder() {
            return sortorder;
        }

        public String getExpand_status() {
            return expand_status;
        }

        public int getHits_per_page() {
            return hits_per_page;
        }
    }

    public String formatDatetime( Date datetime ) {
        if ( null == datetime ) {
            return "";
        }
        DateFormat dateFormat = new SimpleDateFormat( DateConstants.DATE_FORMAT_STRING + "'&nbsp;'"
                                                      + DateConstants.TIME_NO_SECONDS_FORMAT_STRING );
        return dateFormat.format( datetime );
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
