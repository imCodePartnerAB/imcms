package com.imcode.imcms.servlet.conference;

/*
 *
 * @(#)ConfDisc.java
 *
 *
 *
 * Copyright (c)
 *
*/

import imcode.external.diverse.MetaInfo;
import imcode.external.diverse.ParsedTextFile;
import imcode.external.diverse.VariableManager;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentMapper;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.text.SimpleDateFormat;

/**
 * Html template in use:
 * Conf_Disc_List_New.htm
 * Conf_Disc_List_Previous.htm
 * Conf_Disc_List_Next.htm
 * Conf_Disc_New_Button.htm
 * <p/>
 * Html parstags in use:
 * #ADMIN_TYPE#
 * #TARGET#
 * #PREVIOUS_BUTTON#
 * #NEXT_BUTTON#
 * #PREVIOUS_BUTTON#
 * #NEW_DISC_BUTTON#
 * #A_HREF_LIST#
 * <p/>
 * stored procedures in use:
 * -
 *
 * @author Rickard Larsson
 * @author Jerker Drottenmyr
 * @version 1.6 21 Nov 2000
 */

public class ConfDisc extends Conference {

    private final static String NEW_DISC_FLAG_TEMPLATE = "conf_disc_list_new.htm";
    private final static String PREVIOUS_DISC_LIST_TEMPLATE = "conf_disc_list_previous.htm";
    private final static String NEXT_DISC_LIST_TEMPLATE = "conf_disc_list_next.htm";
    private final static String NEW_DISC_TEMPLATE = "conf_disc_new_button.htm";
    private final static String ADMIN_LINK_TEMPLATE = "conf_disc_admin_link.htm";

    private final static String HTML_TEMPLATE = "conf_disc.htm";
    private final static String A_HREF_HTML = "conf_disc_list.htm";   // The code snippet where the aHref list with all discussions
    // will be placed.

    public void doPost( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {

        UserDomainObject user = Utility.getLoggedOnUser( req );
        if ( !isUserAuthorized( req, res, user ) ) {
            return;
        }

        // Lets add the standard SESSION parameters
        Properties params = this.getPropertiesOfConferenceSessionParameters( req );

        // Lets get serverinformation
        ImcmsServices imcref = Imcms.getServices();


        if(req.getParameter("Quit") !=null){
            res.sendRedirect( "BackDoc" );
            return;
        }

        // ********* UPDATE DISCUSSIONS ********
        if ( req.getParameter( "UPDATE" ) != null ) {
            // log("NU uppdaterar vi discussions") ;
            // Lets get the forum_id and set our session object before updating
            Properties reqParams = this.getRequestParameters( req );
            String aForumId = reqParams.getProperty( "FORUM_ID" );
            String discIndex = params.getProperty( "DISC_INDEX" );
            String changeForum = req.getParameter( "CHANGE_FORUM" );

            HttpSession session = req.getSession( false );
            if ( session != null ) {
                String latestDiscId = imcref.sqlProcedureStr( "A_GetLastDiscussionId", new String[]{
                    params.getProperty( "META_ID" ), aForumId
                } );

                if ( latestDiscId == null ) {
                    log( "LatestDiscID saknas, det kan saknas diskussioner i forumet:" + aForumId );
                    latestDiscId = "-1";
                }
                if ( discIndex == null ) {
                    log( "DiscIndex var null:" + discIndex );
                    discIndex = "0";
                } else {
                    //lets set disc index to 0 then changing forum
                    if ( changeForum != null ) {
                        discIndex = "0";
                    }
                }
                session.setAttribute( "Conference.disc_id", latestDiscId );
                session.setAttribute( "Conference.forum_id", aForumId );
                session.setAttribute( "Conference.disc_index", discIndex );
            }

            res.sendRedirect( "ConfDiscView" );
            return;
        }

        // ********* ADD DISCUSSIONS ********
        if ( req.getParameter( "ADD" ) != null ) {
            // log("Nu redirectar vi till ConfAdd") ;
            res.sendRedirect( "ConfAdd?ADDTYPE=Discussion" );
            return;
        }

        // ********* VIEW NEXT DISCUSSIONS ********
        if ( ( req.getParameter( "NEXT" ) != null || req.getParameter( "NEXT.x" ) != null ) ) {
            //if (req.getParameter("NEXT") != null) {

            // Lets get the total nbr of discs in the forum
            // RmiConf rmi = new RmiConf(user) ;
            String nbrOfDiscsStr = imcref.sqlProcedureStr( "A_GetNbrOfDiscs", new String[]{
                params.getProperty( "FORUM_ID" )
            } );
            int nbrOfDiscs;

            // Lets get the nbr of discussions to show. If it does not contain any
            // discussions, 20 will be returned by default from db
            String showDiscsStr = imcref.sqlProcedureStr( "A_GetNbrOfDiscsToShow", new String[]{
                params.getProperty( "FORUM_ID" )
            } );
            int showDiscsCounter = Integer.parseInt( showDiscsStr );

            try {
                nbrOfDiscs = Integer.parseInt( nbrOfDiscsStr );
            } catch ( NumberFormatException e ) {
                nbrOfDiscs = 0;
                log( "GetNbrOfDiscs returned null" );
            }

            int currIndex = this.getDiscIndex( req );
            if ( currIndex + showDiscsCounter < nbrOfDiscs ) {
                this.increaseDiscIndex( req, showDiscsCounter );
            }

            // Lets redirect to the servlet which holds in us.
            res.sendRedirect( "ConfDiscView" );
            return;
        }

        // ********* VIEW PREVIOUS DISCUSSIONS ********
        if ( ( req.getParameter( "PREVIOUS" ) != null || req.getParameter( "PREVIOUS.x" ) != null ) ) {
            //if (req.getParameter("PREVIOUS") != null) {
            //RmiConf rmi = new RmiConf(user) ;

            // Lets get the nbr of discussions to show. If it does not contain any
            // discussions, 20 will be returned by default from db
            String showDiscsStr = imcref.sqlProcedureStr( "A_GetNbrOfDiscsToShow", new String[]{
                params.getProperty( "FORUM_ID" )
            } );
            int showDiscsCounter = Integer.parseInt( showDiscsStr );

            this.decreaseDiscIndex( req, showDiscsCounter );

            // Lets redirect to the servlet which holds in us.
            res.sendRedirect( "ConfDiscView" );
            return;
        }

        // ********* SEARCH ********
        if ( req.getParameter( "SEARCH" ) != null ) {
            params = this.getSearchParameters( req, params );
            String searchMsg = "";
            String[][] sqlAnswer = null;
            boolean searchParamsOk = true;
            String currForum;

            // Lets get the forumname for the current forum

            String aForumId = params.getProperty( "FORUM_ID" );
            currForum = imcref.sqlProcedureStr( "A_GetForumName", new String[]{aForumId} );

            //lets get metaId befor buildSearchDateParams destroys that info (happens if error in DATE_FORMAT)
            String metaId = params.getProperty( "META_ID" );

            // Lets validate the searchdates. If not correct then get a message and show user
            // 42=En sökdatumsträng var felaktig!
            params = this.buildSearchDateParams( params );

            if ( params == null ) {
                log( "An illegal searchdateparameter was sent to server" );
                ConfError msgErr = new ConfError();
                searchMsg = msgErr.getErrorMessage( req, 42 );
                searchParamsOk = false;
            }


            // Lets validate the searchwords. If not correct then get a message and show user
            // 40=En sökparameter saknades! Du måste ange minst ett sökord!
            if ( searchParamsOk ) {
                boolean itsOk = this.checkSearchWords( params );
                if ( !itsOk ) {
                    ConfError msgErr = new ConfError();
                    searchMsg = msgErr.getErrorMessage( req, 40 );
                    searchParamsOk = false;
                }
            }

            // Lets check if everything is alright
            if ( searchParamsOk ) {
                String searchW = params.getProperty( "SEARCH" );
                String category = params.getProperty( "CATEGORY" );
                String frDate = params.getProperty( "FR_DATE" );
                String toDate = params.getProperty( "TO_DATE" );


                // IF WE ARE LOOKING FOR USERS ACTIVITY
                if ( params.getProperty( "CATEGORY" ).equals( "2" ) ) {

                    StringBuffer sqlQ = new StringBuffer( "SELECT DISTINCT '0' as 'newflag', disc.discussion_id, SUBSTRING( CONVERT(char(16), rep.create_date,20), 6, 16) AS 'create_date',\n"
                                                          + "rep.headline, disc.count_replies, usr.first_name, usr.last_name, SUBSTRING( CONVERT(char(20), disc.last_mod_date,20),1, 20) as 'updated_date'\n"
                                                          + "FROM A_replies rep, A_discussion disc, A_conf_users usr, A_conference conf, A_conf_forum cf, A_forum, A_conf_users_crossref crossref \n"
                                                          + "WHERE  rep.parent_id = disc.discussion_id \n"
                                                          + "AND disc.forum_id = A_forum.forum_id \n"
                                                          + "AND A_forum.forum_id = ? \n"
                                                          + "AND A_forum.forum_id = cf.forum_id \n"
                                                          + "AND cf.conf_id = ? \n"
                                                          // Lets check for the date
                                                          + "AND rep.create_date > ? AND rep.create_date <= ? \n"

                                                          + "AND rep.user_id = usr.user_id \n"
                                                          + "AND usr.user_id = crossref.user_id \n"
                                                          + "AND crossref.conf_id = "
                                                          + metaId
                                                          + "\n"
                                                          + "AND (\n" );

                    ArrayList sqlParameters = new ArrayList();
                    sqlParameters.add( aForumId );
                    sqlParameters.add( metaId );
                    sqlParameters.add( frDate );
                    sqlParameters.add( toDate + " 23:59:59" );

                    StringTokenizer st = new StringTokenizer( searchW );
                    while ( st.hasMoreTokens() ) {
                        String tmpItem = st.nextToken();
                        sqlQ.append( "usr.first_name LIKE ? OR usr.last_name LIKE ? \n" );
                        sqlParameters.add( tmpItem + '%' );
                        sqlParameters.add( tmpItem + '%' );
                        if ( st.hasMoreTokens() ) {
                            sqlQ.append( " OR " );
                        }
                    }
                    sqlQ.append( ')' );
                    sqlAnswer = imcref.sqlQueryMulti( sqlQ.toString(), (String[])sqlParameters.toArray( new String[sqlParameters.size()] ) );
                } else {
                    // Ok, Lets build the search string

                    sqlAnswer = imcref.sqlProcedureMulti( "A_SearchText", new String[]{
                        metaId, aForumId, category, searchW, frDate, toDate + " 23:59:59"
                    } );
                } // End if
            } // End if

            // Lets get the part of an html page, wich will be parsed for every a Href reference
            File templateLib = super.getExternalTemplateFolder( req );
            //	templateLib += getTemplateSetDirectoryName(params.getProperty("META_ID")) ;
            File aHreHtmlFile = new File( templateLib, A_HREF_HTML );


            // Lets build our tags vector.
            Vector tagsV = this.buildTags();

            // Lets preparse all records, if any returned get an error mesage
            String allRecs = "";
            //	log("SqlAnswer: " + sqlAnswer) ;
            if ( sqlAnswer != null ) {
                if ( sqlAnswer.length > 0 ) {
                    allRecs = preParse( req, sqlAnswer, tagsV, aHreHtmlFile, "", user );
                    if ( allRecs == null ) {
                        ConfError msgErr = new ConfError();
                        allRecs = msgErr.getErrorMessage( req, 41 );
                    }
                } else {
                }
            } else {
                // log("SqlAnswer = null") ;
                // Ok, we coulnt find anything
                if ( searchParamsOk ) {
                    ConfError msgErr = new ConfError();
                    allRecs = msgErr.getErrorMessage( req, 41 );
                }
            }

            //log("Ok, we passed the sqlquestioning") ;
            //log("ALLRECS: " + allRecs) ;
            //log("searchMsg: " + searchMsg) ;

            // Lets build the Responsepage
            VariableManager vm = new VariableManager();
            if ( allRecs == null || allRecs.equals( "" ) ) {
                vm.addProperty( "A_HREF_LIST", searchMsg );
            } else {
                vm.addProperty( "A_HREF_LIST", allRecs );
            }

            //lets show newbutton if user has more than readrights
            String newDiscButton = "&nbsp;";
            int intMetaId = Integer.parseInt( metaId );
            DocumentMapper documentMapper = imcref.getDocumentMapper();
            DocumentDomainObject document = documentMapper.getDocument( intMetaId );
            if ( user.canAccess( document )
                 && imcref.checkDocAdminRights( intMetaId, user ) ) {

                VariableManager vmButtons = new VariableManager();
                vmButtons.addProperty( "#SERVLET_URL#", "" );
                vmButtons.addProperty( "#IMAGE_URL#", this.getExternalImageFolder( req ) );

                newDiscButton = getTemplate( NEW_DISC_TEMPLATE, user, vmButtons.getTagsAndData() );
            }

            vm.addProperty( "CURRENT_FORUM_NAME", currForum );
            vm.addProperty( "PREVIOUS_BUTTON", "&nbsp;" );
            vm.addProperty( "NEXT_BUTTON", "&nbsp;" );
            vm.addProperty( "NEW_DISC_BUTTON", newDiscButton );
            vm.addProperty( "ADMIN_LINK_HTML", ADMIN_LINK_TEMPLATE );
            this.sendHtml( req, res, vm, HTML_TEMPLATE );

            return;
        }
    } // DoPost

    /**
     * doGet
     */
    public void doGet( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {

        // Lets get the standard SESSION parameters and validate them
        Properties params = this.getPropertiesOfConferenceSessionParameters( req );

        UserDomainObject user = Utility.getLoggedOnUser( req );
        if ( !isUserAuthorized( req, res, user ) ) {
            return;
        }

        // Lets get serverinformation
        ImcmsServices imcref = Imcms.getServices();

        // Lets get parameters
        String aMetaId = params.getProperty( "META_ID" );
        int metaId = Integer.parseInt( aMetaId );
        String aForumId = params.getProperty( "FORUM_ID" );
        String aLoginDate = params.getProperty( "LAST_LOGIN_DATE" );
        // log("GetLastLoginDate: " + aLoginDate ) ;

        String imagePath = super.getExternalImageFolder( req ) + "ConfDiscNew.gif";

        // Lets get the part of an html page, wich will be parsed for every a Href reference
        File aHrefHtmlFile = new File( super.getExternalTemplateFolder( req ), A_HREF_HTML );

        // Lets get all Discussions
        String[][] sqlAnswer = imcref.sqlProcedureMulti( "A_GetAllDiscussions", new String[]{
            aMetaId, aForumId, aLoginDate
        } );

        //lets generate the buttons that should appear
        File templateLib = this.getExternalTemplateFolder( req );
        String previousButton = "&nbsp;";
        String nextButton = "&nbsp;";
        String newDiscButton = "&nbsp;";

        // Lets bee ready to create button and new flags
        VariableManager vmButtons = new VariableManager();
        vmButtons.addProperty( "#SERVLET_URL#", "" );
        vmButtons.addProperty( "#IMAGE_URL#", this.getExternalImageFolder( req ) );

        // Lets preparse all records
        String allRecs = "";
        // Lets get the start record position in the array
        int discIndexPos = this.getDiscIndex( req );
        int showDiscsCounter;
        if ( sqlAnswer.length > 0 ) {
            // Lets build our tags vector.
            Vector tagsV = this.buildTags();

            // Lets get the start record position in the array

            // Lets get the nbr of discussions to show. If it does not contain any
            // discussions, 20 will be returned by default from db
            String showDiscsStr = imcref.sqlProcedureStr( "A_GetNbrOfDiscsToShow", new String[]{
                params.getProperty( "FORUM_ID" )
            } );
            showDiscsCounter = Integer.parseInt( showDiscsStr );

            // Lets create an array
            String[][] newArr = sqlAnswer;

            allRecs = preParse( req, newArr, tagsV, aHrefHtmlFile, imagePath, user );

            //lets show previousbutton if not first set of discussions
            if ( discIndexPos != 0 ) {
                previousButton = getTemplate( PREVIOUS_DISC_LIST_TEMPLATE, user, vmButtons.getTagsAndData() );
            }
            //lets show nextbutton if not last set of discussions
            if ( ( sqlAnswer.length / 8 - 1 ) > ( discIndexPos + showDiscsCounter ) ) {
                nextButton = getTemplate( NEXT_DISC_LIST_TEMPLATE, user, vmButtons.getTagsAndData() );
            }

        }
        // Lets get the forumname for the current forum
        String currForum = imcref.sqlProcedureStr( "A_GetForumName", new String[]{params.getProperty( "FORUM_ID" )} );

        //lets show newdiscbutton if user has more than readrights
        DocumentMapper documentMapper = imcref.getDocumentMapper();
        DocumentDomainObject document = documentMapper.getDocument( metaId );
        if ( user.canAccess( document )
             && imcref.checkDocAdminRights( metaId, user ) ) {
            newDiscButton = getTemplate( NEW_DISC_TEMPLATE, user, vmButtons.getTagsAndData() );
        }

        VariableManager vm = new VariableManager();
        vm.addProperty( "PREVIOUS_BUTTON", previousButton );
        vm.addProperty( "NEXT_BUTTON", nextButton );
        vm.addProperty( "NEW_DISC_BUTTON", newDiscButton );
        vm.addProperty( "A_HREF_LIST", allRecs );
        vm.addProperty( "CURRENT_FORUM_NAME", currForum );
        vm.addProperty( "ADMIN_LINK_HTML", ADMIN_LINK_TEMPLATE );
        this.sendHtml( req, res, vm, HTML_TEMPLATE );
    } //DoGet

    /**
     * Parses the Extended array with the htmlcode, which will be parsed
     * for all records in the array
     */
    private String preParse( HttpServletRequest req, String[][] DBArr, Vector tagsV, File htmlCodeFile,
                             String imagePath, UserDomainObject user ) throws IOException {
        String htmlStr = "";
        for ( int i = 0; i < DBArr.length; i++ ) {
            Vector dataV = new Vector();

            // Lets create one record... Get all fields for that record
            for ( int j = 0; j < DBArr[i].length; j++ ) {
                dataV.add( DBArr[i][j] );
            }
            // Lets insert the aHrefCode to the end of the vector
            dataV.add( "ConfReply?" );

            // Lets check if the discussions should have a new bitmap in front of them
            // Lets first check against the logindate in the sql, then check the
            // list with users clicked ones
            if ( this.discViewStatus( req, dataV ) ) {
                VariableManager newFlagVM = new VariableManager();
                newFlagVM.addProperty( "#IMAGE_URL#", imagePath );
                String newFlag = getTemplate( NEW_DISC_FLAG_TEMPLATE, user, newFlagVM.getTagsAndData() );

                dataV.setElementAt( newFlag, 0 );
            } else {
                dataV.setElementAt( "", 0 );
            }

            htmlStr += this.parseOneRecord( tagsV, dataV, htmlCodeFile );
        } // end of the big for
        return htmlStr;
    } // End of

    /**
     * Returns true if we have seen the discussion before, otherwise false.
     * Updates the sessionobject as well
     */

    private boolean discViewStatus( HttpServletRequest req, Vector dataV ) {

        // Get the session and the list
        HttpSession session = req.getSession( true );
        Properties viewedDiscs = (Properties)session.getAttribute( "Conference.viewedDiscList" );

        // Lets get info from the db. the format on the vector is:
        // newFlag, discussion_id, create_date, headline, count_replies, first_name, last_name , updated_date
        String sqlNewDiscFlag = dataV.get( 0 ).toString();
        String sqlDiscId = dataV.get( 1 ).toString();
        String sqlDiscDate = dataV.get( 7 ).toString();

        if ( sqlNewDiscFlag.equals( "1" ) ) {
            // log("NY diskussion") ;
            // Lets check if we have seen the discussion in this session, if we have
            // not seen it, return true
            if ( viewedDiscs.get( sqlDiscId ) == null ) {
                return true;
            } else { // we have seen it in this session, lets check the date when we
                // saw it against when it was updated
                // Lets get the date when we viewed the discussion
                boolean newerDisc = compareDates( viewedDiscs.getProperty( sqlDiscId ), sqlDiscDate );
                if ( newerDisc ) {
                    // log("Gamlare diskussion") ;
                    return true;
                }
                //log("SAG: " + sqlDiscId + ": " + sqlDiscDate) ;
                return false;
            }
        }
        return false;
    }

    /**
     * Compare dates. Takes an string of the expected form and compares if its newer or later
     * than the other. returns true if the firstdate is before the second date. If its
     * equals or later it returns false. Observe the precision which is down to
     * minutes, not seconds. It means that comparing two dates with the same minute wil
     * l return false!
     */

    private boolean compareDates( String date1, String date2 ) {
        // Lets fix the date
        SimpleDateFormat formatter = new java.text.SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
        GregorianCalendar firstDate = new GregorianCalendar();
        GregorianCalendar secDate = new GregorianCalendar();

        try {
            // Create a calendar, by parse to a date and then create calendar
            firstDate.setTime( formatter.parse( date1 ) );
            secDate.setTime( formatter.parse( date2 ) );

            // Get the seconds from the datestring,
            firstDate.set( Calendar.SECOND, this.getDateItem( date1, ":", 3 ) );
            secDate.set( Calendar.SECOND, this.getDateItem( date2, ":", 3 ) );
            //secDate = formatter.parse(date2) ;

            //log("firstDate: " + firstDate.toString() ) ;
            //log("secDate: " + firstDate.toString() ) ;
        } catch ( java.text.ParseException e ) {
            log( e.getMessage() );
            log( "Invalid date1: " + date1 );
            log( "Invalid date2: " + date2 );
            return true;
        }
        //
        if ( firstDate.before( secDate ) ) {
            //log( date1 + " innan " + date2) ;
            return true;
        }

        return false;
        // return firstDate.before(secDate) ;
    }

    /**
     * Returns wanted item from string, returns empty if itemnumber not found
     */

    private int getDateItem( String str, String delim, int itemNbr ) {
        String tmpVal = "";
        itemNbr = itemNbr - 1;
        StringTokenizer st = new StringTokenizer( str, delim );
        int counter = 0;
        int retVal;

        while ( st.hasMoreTokens() ) {
            String tmp = st.nextToken();
            if ( counter == itemNbr ) {
                tmpVal = tmp;
            }
            counter = counter + 1;
        }

        try {
            retVal = Integer.parseInt( tmpVal );
        } catch ( NumberFormatException e ) {
            log( "Error in getDateItem!" );
            retVal = 0;
        }
        return retVal;
    }

    /**
     * Increases the current discussion index. If somethings happens, zero will be set.
     */
    private void increaseDiscIndex( HttpServletRequest req, int incFactor ) {
        HttpSession session = null;
        session = req.getSession( false );
        if ( session != null ) {
            String indexStr = (String)session.getAttribute( "Conference.disc_index" );
            int anInt = Integer.parseInt( indexStr ) + incFactor;
            session.setAttribute( "Conference.disc_index", "" + anInt );
        }
    }

    /**
     * Decreases the current discussion index. If somethings happens, zero will be set.
     */
    private void decreaseDiscIndex( HttpServletRequest req, int incFactor ) {
        HttpSession session = null;
        session = req.getSession( false );
        if ( session != null ) {
            String indexStr = (String)session.getAttribute( "Conference.disc_index" );
            int anInt = Integer.parseInt( indexStr ) - incFactor;
            if ( anInt < 0 ) {
                anInt = 0;
            }
            session.setAttribute( "Conference.disc_index", "" + anInt );
        }
    }

    /**
     * Returns the current discussion index. If somethings happens, zero will be returned.
     */
    private int getDiscIndex( HttpServletRequest req ) {
        try {
            HttpSession session = req.getSession( false );
            if ( session != null ) {
                String indexStr = (String)session.getAttribute( "Conference.disc_index" );
                int anInt = Integer.parseInt( indexStr );
                return anInt;
            }
        } catch ( Exception e ) {
            log( "GetDiscIndex failed!" );
            return 0;
        }
        return 0;
    }

    /**
     * Parses one record.
     */
    String parseOneRecord( Vector tagsV, Vector dataV, File htmlCodeFile ) throws IOException {

        // Lets parse one aHref reference
        ParsedTextFile parser = new ParsedTextFile( htmlCodeFile, tagsV, dataV );
        String oneRecordsHtmlCode = parser.toString();
        return oneRecordsHtmlCode;
    } // End of parseOneRecord

    /**
     * Collects the standard parameters from the SESSION object.
     */

    private Properties getPropertiesOfConferenceSessionParameters( HttpServletRequest req ) {

        // Lets get the standard metainformation
        Properties reqParams = MetaInfo.createPropertiesFromMetaInfoParameters( super.getConferenceSessionParameters( req ) );

        // Lets get the session
        HttpSession session = req.getSession( false );
        if ( session != null ) {
            // Lets get the parameters we know we are supposed to get from the request object
            String forumId = ( (String)session.getAttribute( "Conference.forum_id" ) == null )
                             ? "" : ( (String)session.getAttribute( "Conference.forum_id" ) );
            //	String discId = (	(String) session.getAttribute("Conference.forum_id")==null) ? "" : ((String) session.getAttribute("Conference.forum_id")) ;
            String discId = ( (String)session.getAttribute( "Conference.disc_id" ) == null )
                            ? "" : ( (String)session.getAttribute( "Conference.disc_id" ) );
            String lastLogindate = ( (String)session.getAttribute( "Conference.last_login_date" ) == null )
                                   ? "" : ( (String)session.getAttribute( "Conference.last_login_date" ) );
            String discIndex = ( (String)session.getAttribute( "Conference.disc_index" ) == null )
                               ? "" : ( (String)session.getAttribute( "Conference.disc_index" ) );

            reqParams.setProperty( "DISC_INDEX", discIndex );
            reqParams.setProperty( "LAST_LOGIN_DATE", lastLogindate );
            reqParams.setProperty( "FORUM_ID", forumId );
            reqParams.setProperty( "DISC_ID", discId );

        }
        return reqParams;
    }

    /**
     * Collects the parameters from the request object. This function will get all the possible
     * parameters this servlet will be able to get. If a parameter wont be found, the session
     * parameter will be used instead, or if no such parameter exist in the session object,
     * a key with no value = "" will be used instead.
     */

    private Properties getRequestParameters( HttpServletRequest req ) {

        Properties reqParams = new Properties();

        // Lets get our own variables. We will first look for the discussion_id
        // in the request object, if not found, we will get the one from our session object

        String confForumId = req.getParameter( "forum_id" );
        String discIndex = "";
        HttpSession session = req.getSession( false );
        if ( session != null ) {
            if ( confForumId == null ) {
                confForumId = (String)session.getAttribute( "Conference.forum_id" );
            }
            discIndex = (String)session.getAttribute( "Conference.disc_index" );
            if ( discIndex == null || discIndex.equalsIgnoreCase( "null" ) ) {
                discIndex = "0";
            }
        }
        reqParams.setProperty( "FORUM_ID", confForumId );
        reqParams.setProperty( "DISC_INDEX", discIndex );
        return reqParams;
    }

    /**
     * Collects the parameters used to detect the buttons from the request object. Checks
     * if the Properties object is null, if so it creates one, otherwise it uses the
     * object passed to it.
     */

    private Properties getSearchParameters( HttpServletRequest req, Properties params ) {

        //Lets get the search criterias
        String cat = ( req.getParameter( "CATEGORY" ) == null ) ? "" : ( req.getParameter( "CATEGORY" ) );
        String search = ( req.getParameter( "SEARCH" ) == null ) ? "" : ( req.getParameter( "SEARCH" ) );
        String fromDate = ( req.getParameter( "FR_DATE" ) == null ) ? "" : ( req.getParameter( "FR_DATE" ) );
        String fromVal = ( req.getParameter( "FR_VALUE" ) == null ) ? "" : ( req.getParameter( "FR_VALUE" ) );

        String toDate = ( req.getParameter( "TO_DATE" ) == null ) ? "" : ( req.getParameter( "TO_DATE" ) );
        String toVal = ( req.getParameter( "TO_VALUE" ) == null ) ? "" : ( req.getParameter( "TO_VALUE" ) );
        //	String searchButton = (req.getParameter("BUTTON_SEARCH")==null) ? "" : (req.getParameter("BUTTON_SEARCH")) ;

        params.setProperty( "CATEGORY", super.verifySqlText( cat.trim() ) );
        params.setProperty( "SEARCH", super.verifySqlText( search.trim() ) );
        params.setProperty( "FR_DATE", super.verifySqlText( fromDate.trim() ) );
        params.setProperty( "TO_DATE", super.verifySqlText( toDate.trim() ) );
        params.setProperty( "FR_VALUE", super.verifySqlText( fromVal.trim() ) );
        params.setProperty( "TO_VALUE", super.verifySqlText( toVal.trim() ) );

        //	params.setProperty("BUTTON_SEARCH", searchButton) ;
        return params;
    }

    /**
     * Builds the tagvector used for parse one record.
     */
    private Vector buildTags() {

        // Lets build our tags vector.
        Vector tagsV = new Vector();
        tagsV.add( "#NEW_DISC_FLAG#" );
        tagsV.add( "#DISC_ID#" );
        tagsV.add( "#A_DATE#" );
        tagsV.add( "#HEADLINE#" );
        tagsV.add( "#C_REPLIES#" );
        tagsV.add( "#FIRST_NAME#" );
        tagsV.add( "#LAST_NAME#" );
        tagsV.add( "#LAST_UPDATED#" );    // The discussion_update date
        tagsV.add( "#REPLY_URL#" );
        return tagsV;
    } // End of buildstags

    /**
     * check the Search date Parameters
     * - if startdate is empty, OR the standardvalue is sent, yesterdays date will be used
     * - if enddate is empty, OR the standardvalue is sent,todays date will be used instead.
     * Wont fix time parameters!!!
     * does not fix form '2000-02--04'
     */

    private Properties buildSearchDateParams( Properties p ) {

        // Lets take care of "igår" and "idag" todays date in a GregorianCalendar object
        // if "idag" is 2000-05-01, then it means at the time 00.00.
        // So thats why we add a day, so 2000-05-01 -> 2000-05-02 to cover the hole day!
        GregorianCalendar today = new GregorianCalendar();
        today.set( Calendar.DATE, ( today.get( Calendar.DATE ) ) );
        int tYear = today.get( Calendar.YEAR );
        int tMonth = 1 + today.get( Calendar.MONTH );
        int tDay = today.get( Calendar.DATE );
        //log("tDay is: " + tDay) ;

        // Lets change to yesterday
        today.set( Calendar.DATE, ( today.get( Calendar.DATE ) - 2 ) );
        int yYear = today.get( Calendar.YEAR );
        int yMonth = 1 + today.get( Calendar.MONTH );
        int yDay = today.get( Calendar.DATE );

        // Lets analyze the startdate params. if it is "idag" resp "igår"
        if ( p.getProperty( "TO_DATE" ).equals( "" )
             || p.getProperty( "TO_DATE" ).equalsIgnoreCase( p.getProperty( "TO_VALUE" ) ) ) {
            p.setProperty( "TO_DATE", "" + tYear + "-" + tMonth + "-" + tDay  /*+ " 23:59:59"*/ );
        }
        if ( p.getProperty( "FR_DATE" ).equals( "" )
             || p.getProperty( "FR_DATE" ).equalsIgnoreCase( p.getProperty( "FR_VALUE" ) ) ) {
            p.setProperty( "FR_DATE", "" + yYear + "-" + yMonth + "-" + yDay /*+ " 00:00"*/ );
        }

        // Lets check if we can create a valid sql date from our date params
        java.sql.Date fromDate = null;
        java.sql.Date toDate = null;

        try {
            fromDate = java.sql.Date.valueOf( p.getProperty( "FR_DATE" ) );
            toDate = java.sql.Date.valueOf( p.getProperty( "TO_DATE" ) );
            //log("fromdate: " + fromDate) ;
            // log("toDate: " + toDate) ;
        } catch ( Exception e ) {
            // log("Exception: " + e.getMessage()) ;
            log( "Invalid FROM date: " + fromDate );
            log( "Invalid TO date: " + toDate );
            // p = null ;
            return null;
        }

        //log("FROM: " + p.getProperty("FR_DATE")) ;
        //log("TO: " + p.getProperty("TO_DATE")) ;

        return p;
    }

    /**
     * check the SearchWord Parameters
     */

    private boolean checkSearchWords( Properties p ) {
        // Lets analyze the searchword
        String str = p.getProperty( "SEARCH" ).trim();
        //this.log("SEARCHWORD: " + str) ;
        if ( str.equalsIgnoreCase( "" ) ) {
            //this.log("No searchword was entered!") ;
            return false;
        }

        if ( str.length() <= 2 ) {
            //this.log("No searchword was entered!") ;
            return false;
        }

        return true;
    }

} // End of class
