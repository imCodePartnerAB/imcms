/*
 *
 * @(#)BillBoardDisc.java
 *
 *
 *
 * Copyright (c)
 *
 */

import imcode.external.diverse.HtmlGenerator;
import imcode.external.diverse.MetaInfo;
import imcode.external.diverse.VariableManager;
import imcode.server.ApplicationServer;
import imcode.server.IMCPoolInterface;
import imcode.server.IMCServiceInterface;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Properties;
import java.util.Vector;

/**
 * Html template in use:
 * BillBoard_Disc_List_New.htm
 * BillBoard_Disc_List_Previous.htm
 * BillBoard_Disc_List_Next.htm
 * BillBoard_Disc_New_Button.htm
 * BillBoard_Disc_Admin_Link
 * <p/>
 * Html parstags in use:
 * #SERVLET_URL#
 * #IMAGE_URL#
 * #NEXT_BUTTON#
 * #PREVIOUS_BUTTON#
 * #NEW_DISC_BUTTON#
 * #A_HREF_LIST#
 * #CURRENT_SECTION_NAME#
 * #ADMIN_LINK_HTML#
 * <p/>
 * stored procedures in use:
 * B_GetLastDiscussionId
 * B_GetNbrOfDiscsToShow
 * B_GetSectionName
 * B_SearchText
 * B_GetAllBillsToShow
 * B_GetFirstSection
 * 
 * @author Rickard Larsson
 * @author Jerker Drottenmyr
 * @author REBUILD TO BILLBOARD BY Peter Östergren
 * @version 1.2 20 Aug 2001
 */

public class BillBoardDisc extends BillBoard {//ConfDisc

    private final static String PREVIOUS_DISC_LIST_TEMPLATE = "BillBoard_Disc_List_Previous.htm";//"Conf_Disc_List_Previous.htm";
    private final static String NEXT_DISC_LIST_TEMPLATE = "BillBoard_Disc_List_Next.htm";//"Conf_Disc_List_Next.htm";
    private final static String NEW_DISC_TEMPLATE = "BillBoard_Disc_New_Button.htm";//"Conf_Disc_New_Button.htm";
    private final static String ADMIN_LINK_TEMPLATE = "BillBoard_Disc_Admin_Link.htm";//"Conf_Disc_Admin_Link.htm";

    private String HTML_TEMPLATE = "BillBoard_Disc.htm";
    private String A_HREF_HTML = "BillBoard_Disc_List.htm";   // The code snippet where the aHref list with all discussions

    public void doPost( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
        //log("START BillBoardDisc doPost");
        // Lets validate the session, e.g has the user logged in to Janus?
        if ( super.checkSession( req, res ) == false ) return;

        // Lets get the user object
        imcode.server.user.UserDomainObject user = super.getUserObj( req, res );
        if ( user == null ) return;

        if ( !isUserAuthorized( req, res, user ) ) {
            return;
        }

        // Lets add the standard SESSION parameters
        Properties params = this.getPropertiesOfBillBoardSessionParameters( req );

        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();
        IMCPoolInterface billref = ApplicationServer.getIMCPoolInterface();

        // ********* UPDATE DISCUSSIONS ********
        if ( req.getParameter( "UPDATE" ) != null ) {
            // Lets get the forum_id and set our session object before updating
            Properties reqParams = this.getRequestParameters( req );
            String aSectionId = reqParams.getProperty( "SECTION_ID" );//
            String discIndex = params.getProperty( "DISC_INDEX" );

            HttpSession session = req.getSession( false );
            if ( session != null ) {
                String latestDiscId = "-1";
                discIndex = "0";
                session.setAttribute( "BillBoard.disc_id", latestDiscId );//
                session.setAttribute( "BillBoard.section_id", aSectionId );//
                session.setAttribute( "BillBoard.disc_index", discIndex );//
            }

            // Lets redirect to the servlet which holds in us.

            res.sendRedirect( "BillBoardDiscView" );
            return;
        }

        // ********* ADD DISCUSSIONS ********
        if ( req.getParameter( "ADD" ) != null ) {
            //lets clean up ev old stuff in the session
            if ( req.getParameter( "ADDNEW" ) != null ) {
                HttpSession session = req.getSession( false );
                if ( session != null ) {
                    session.removeAttribute( "billPrevData" );
                }
            }
            res.sendRedirect( "BillBoardAdd?ADDTYPE=Discussion" );
            return;
        }

        // ********* VIEW NEXT DISCUSSIONS ********
        if ( ( req.getParameter( "NEXT" ) != null || req.getParameter( "NEXT.x" ) != null ) ) {

            // Lets get the total nbr of discs in the forum
            String nbrOfDiscsStr = billref.sqlProcedureStr( "B_GetNbrOfDiscs", new String[]{params.getProperty( "SECTION_ID" )} );
            int nbrOfDiscs = 0;

            // Lets get the nbr of discussions to show. If it does not contain any
            // discussions, 20 will be returned by default from db
            String showDiscsStr = billref.sqlProcedureStr( "B_GetNbrOfDiscsToShow", new String[]{params.getProperty( "SECTION_ID" )} );//GetNbrOfDiscsToShow, FORUM_ID

            int showDiscsCounter = Integer.parseInt( showDiscsStr );

            try {
                nbrOfDiscs = Integer.parseInt( nbrOfDiscsStr );
            } catch ( Exception e ) {
                nbrOfDiscs = 0;
                log( "GetNbrOfDiscs returned null" );
            }

            int currIndex = this.getDiscIndex( req );
            //log("currIndex ="+currIndex);
            //log("if satsen: "+currIndex+" + "+ showDiscsCounter +" < "+nbrOfDiscs);
            if ( currIndex + showDiscsCounter < nbrOfDiscs ) {
                this.increaseDiscIndex( req, showDiscsCounter );
                // log("Ok, vi höjer indexräknaren") ;
            }

            // Lets redirect to the servlet which holds in us.
            res.sendRedirect( "BillBoardDiscView" );
            return;
        }

        // ********* VIEW PREVIOUS DISCUSSIONS ********
        if ( ( req.getParameter( "PREVIOUS" ) != null || req.getParameter( "PREVIOUS.x" ) != null ) ) {
            // Lets get the nbr of discussions to show. If it does not contain any
            // discussions, 20 will be returned by default from db
            String showDiscsStr = billref.sqlProcedureStr( "B_GetNbrOfDiscsToShow", new String[]{params.getProperty( "SECTION_ID" )} );

            int showDiscsCounter = Integer.parseInt( showDiscsStr );

            this.decreaseDiscIndex( req, showDiscsCounter );

            // Lets redirect to the servlet which holds in us.
            res.sendRedirect( "BillBoardDiscView" );
            return;
        }

        // ********* SEARCH ********
        if ( req.getParameter( "SEARCH" ) != null ) {
            //log("Nu är vi i search") ;
            params = this.getSearchParameters( req, params );
            String searchMsg = "";
            boolean searchParamsOk = true;
            String currSection = "";

            // Lets get the forumname for the current forum

            String aSectionId = params.getProperty( "SECTION_ID" );
            currSection = "" + billref.sqlProcedureStr( "B_GetSectionName", new String[]{aSectionId} );
            //log("S currSection ="+currSection);
            //lets get metaId befor buildSearchDateParams destroys that info (happens if error in DATE_FORMAT)
            String metaId = params.getProperty( "META_ID" );
            //log("S metaid= "+metaId);
            // Lets validate the searchdates. If not correct then get a message and show user
            // 42=En sökdatumsträng var felaktig!
            params = this.buildSearchDateParams( params );
            //log("Efter buildSearchDateParams: " + params) ;

            if ( params == null ) {
                log( "An illegal searchdateparameter was sent to server" );
                BillBoardError msgErr = new BillBoardError();
                searchMsg = msgErr.getErrorMessage( req, 42 );
                searchParamsOk = false;
            }


            // Lets validate the searchwords. If not correct then get a message and show user
            // 40=En sökparameter saknades! Du måste ange minst ett sökord!
            if ( searchParamsOk ) {
                boolean itsOk = this.checkSearchWords( params );
                //this.log("ItsOk: " + itsOk) ;
                if ( !itsOk ) {
                    BillBoardError msgErr = new BillBoardError();
                    searchMsg = msgErr.getErrorMessage( req, 40 );
                    //log("searchMsg: " + searchMsg) ;
                    searchParamsOk = false;
                }
            }

            //log("Ok, we have passed test 1 and 2") ;
            //log("searchParamsOk: " + searchParamsOk) ;
            //this.log("SEARCHWORD: " + params.getProperty("SEARCH").trim()) ;


            // Lets check if everything is alright
            String[][] sqlAnswer = null;
            if ( searchParamsOk ) {
                //String metaId = params.getProperty("META_ID") ;
                //aSectionId = params.getProperty("FORUM_ID") ;
                String searchW = params.getProperty( "SEARCH" );
                String category = params.getProperty( "CATEGORY" );
                String frDate = params.getProperty( "FR_DATE" );
                String toDate = params.getProperty( "TO_DATE" );


                // Ok, Lets build the search string
                sqlAnswer = billref.sqlProcedureMulti( "B_SearchText", new String[]{metaId, aSectionId, category, searchW, frDate, toDate + " 23:59:59"} );

            } // End if

            //log("Ok, we have done a search!") ;


            // Lets get the part of an html page, wich will be parsed for every a Href reference
            File templateLib = super.getExternalTemplateFolder( req );
            //	templateLib += getTemplateLibName(params.getProperty("META_ID")) ;
            File aHreHtmlFile = new File( templateLib, A_HREF_HTML );


            // Lets build our tags vector.
            Vector tagsV = this.buildTags();

            // Lets preparse all records, if any returned get an error mesage
            String allRecs = "";
            if ( sqlAnswer != null ) {
                if ( sqlAnswer.length > 0 ) {
                    allRecs = preParse( sqlAnswer, tagsV, aHreHtmlFile );
                    if ( allRecs == null ) {
                        BillBoardError msgErr = new BillBoardError();
                        allRecs = msgErr.getErrorMessage( req, 41 );
                        msgErr = null;
                    }
                }
            } else {
                // log("SqlAnswer = null") ;
                // Ok, we coulnt find anything
                if ( searchParamsOk ) {
                    BillBoardError msgErr = new BillBoardError();
                    allRecs = msgErr.getErrorMessage( req, 41 );
                    msgErr = null;
                }
            }

            //log("Ok, we passed the sqlquestioning") ;
            //log("ALLRECS: " + allRecs) ;
            //log("searchMsg: " + searchMsg) ;

            // Lets build the Responsepage
            VariableManager vm = new VariableManager();
            if ( allRecs == null || allRecs.equals( "" ) )
                vm.addProperty( "A_HREF_LIST", searchMsg );
            else
                vm.addProperty( "A_HREF_LIST", allRecs );

            //lets show newbutton if user has more than readrights
            String newDiscButton = "&nbsp;";
            int intMetaId = Integer.parseInt( metaId );
            if ( imcref.checkDocRights( intMetaId, user ) && imcref.checkDocAdminRights( intMetaId, user ) ) {

                VariableManager vmButtons = new VariableManager();
                vmButtons.addProperty( "#SERVLET_URL#", "" );
                vmButtons.addProperty( "#IMAGE_URL#", this.getExternalImageFolder( req ) );
                HtmlGenerator newButtonHtmlObj = new HtmlGenerator( templateLib, NEW_DISC_TEMPLATE );
                newDiscButton = newButtonHtmlObj.createHtmlString( vmButtons, req );
            }

            vm.addProperty( "CURRENT_SECTION_NAME", currSection );//CURRENT_FORUM_NAME
            vm.addProperty( "PREVIOUS_BUTTON", "&nbsp;" );
            vm.addProperty( "NEXT_BUTTON", "&nbsp;" );
            vm.addProperty( "NEW_DISC_BUTTON", newDiscButton );
            vm.addProperty( "ADMIN_LINK_HTML", ADMIN_LINK_TEMPLATE );
            this.sendHtml( req, res, vm, HTML_TEMPLATE );
            // log("ConfDisc doPost är färdig") ;
            return;
        }
    } // DoPost

    /**
     * doGet
     */
    public void doGet( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
        //log("START BillBoardDisc doGet");
        // Lets validate the session, e.g has the user logged in to Janus?
        if ( super.checkSession( req, res ) == false ) {
            log( "super.checkSession(req,res) == false so return" );
            return;
        }

        // Lets get the standard SESSION parameters and validate them
        Properties params = this.getPropertiesOfBillBoardSessionParameters( req );

        // Lets get the user object
        imcode.server.user.UserDomainObject user = super.getUserObj( req, res );
        if ( user == null ) {
            log( "user == null so return" );
            return;
        }

        if ( !isUserAuthorized( req, res, user ) ) {
            log( "user == null so return" );
            return;
        }

        // Lets get serverinformation

        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();
        IMCPoolInterface billref = ApplicationServer.getIMCPoolInterface();

        // Lets get parameters
        String aMetaId = params.getProperty( "META_ID" );
        int metaId = Integer.parseInt( aMetaId );
        String aSectionId = params.getProperty( "SECTION_ID" );
        //log("aSectionId= "+aSectionId);


        // Lets get the part of an html page, wich will be parsed for every a Href reference
        File aHrefHtmlFile = new File( super.getExternalTemplateFolder( req ), A_HREF_HTML );

        // Lets get all Discussions

        String sqlAnswer[][] = billref.sqlProcedureMulti( "B_GetAllBillsToShow", new String[]{aMetaId, aSectionId} );

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
        //log("DOGET discindex: " + discIndexPos) ;
        int showDiscsCounter = 0;
        if ( sqlAnswer.length > 0 ) {

            // Lets build our tags vector.
            Vector tagsV = this.buildTags();

            // Lets get the nbr of discussions to show. If it does not contain any
            // discussions, 20 will be returned by default from db
            String showDiscsStr = billref.sqlProcedureStr( "B_GetNbrOfDiscsToShow", new String[]{params.getProperty( "SECTION_ID" )} );
            //int showDiscsCounter = Integer.parseInt(showDiscsStr) ;
            showDiscsCounter = Integer.parseInt( showDiscsStr );

            // Lets create an array
            String[][] newArr = new String[][]{sqlAnswer[discIndexPos]};

            if ( newArr.length > 0 )
                allRecs = preParse( newArr, tagsV, aHrefHtmlFile );

            //lets show previousbutton if not first set of discussions
            if ( discIndexPos != 0 ) {
                HtmlGenerator previousButtonHtmlObj = new HtmlGenerator( templateLib, PREVIOUS_DISC_LIST_TEMPLATE );
                previousButton = previousButtonHtmlObj.createHtmlString( vmButtons, req );
            }

            //lets show nextbutton if not last set of discussions
            if ( ( sqlAnswer.length / 4 - 1 ) > ( discIndexPos + showDiscsCounter ) ) {
                HtmlGenerator nextButtonHtmlObj = new HtmlGenerator( templateLib, NEXT_DISC_LIST_TEMPLATE );
                nextButton = nextButtonHtmlObj.createHtmlString( vmButtons, req );
            }

        }

        // Lets get the forumname for the current forum
        String currSection = "" + billref.sqlProcedureStr( "B_GetSectionName", new String[]{params.getProperty( "SECTION_ID" )} );
        //log("currSection: " + currSection) ;

        //lets show newdiscbutton if user has more than readrights
        if ( imcref.checkDocRights( metaId, user ) && imcref.checkDocAdminRights( metaId, user ) ) {
            HtmlGenerator newButtonHtmlObj = new HtmlGenerator( templateLib, NEW_DISC_TEMPLATE );
            newDiscButton = newButtonHtmlObj.createHtmlString( vmButtons, req );
        }

        VariableManager vm = new VariableManager();
        vm.addProperty( "PREVIOUS_BUTTON", previousButton );
        vm.addProperty( "NEXT_BUTTON", nextButton );
        vm.addProperty( "NEW_DISC_BUTTON", newDiscButton );
        vm.addProperty( "A_HREF_LIST", allRecs );
        vm.addProperty( "CURRENT_SECTION_NAME", currSection );
        vm.addProperty( "ADMIN_LINK_HTML", ADMIN_LINK_TEMPLATE );
        this.sendHtml( req, res, vm, HTML_TEMPLATE );
        //	this.showSession(req) ;
        //log("BillBoardDisc doGet är färdig") ;
    } //DoGet

    /**
     * Parses the Extended array with the htmlcode, which will be parsed
     * for all records in the array
     */
    private String preParse( String[][] DBArr, Vector tagsV, File htmlCodeFile ) {
        String htmlStr = "";
 
        // Lets do for all records...
        for ( int i = 0; i < DBArr.length; i++ ) {
            Vector dataV = new Vector( 9 );

            // Lets create one record... Get all fields for that record
            for ( int j = 0; j < DBArr[i].length; j++ ) {
                dataV.add( DBArr[i][j] );
            }
            // Lets insert the aHrefCode to the end of the vector
            dataV.add( "BillBoardReply?" );

            // Lets parse one record
            htmlStr += this.parseOneRecord( tagsV, dataV, htmlCodeFile );
        } // end of the big for
        return htmlStr;
    } // End of

    /**
     * Increases the current discussion index. If somethings happens, zero will be set.
     */
    private boolean increaseDiscIndex( HttpServletRequest req, int incFactor ) {
        HttpSession session = null;
        try {
            session = req.getSession( false );
            if ( session != null ) {
                String indexStr = (String)session.getAttribute( "BillBoard.disc_index" );
                int anInt = Integer.parseInt( indexStr ) + incFactor;
                session.setAttribute( "BillBoard.disc_index", "" + anInt );
            }
        } catch ( Exception e ) {
            session.setAttribute( "BillBoard.disc_index", "0" );
            log( "IncreaseIndex failed!" );
            return false;
        }
        return true;
    }

    /**
     * Decreases the current discussion index. If somethings happens, zero will be set.
     */
    private boolean decreaseDiscIndex( HttpServletRequest req, int incFactor ) {
        HttpSession session = null;
        try {
            session = req.getSession( false );
            if ( session != null ) {
                String indexStr = (String)session.getAttribute( "BillBoard.disc_index" );
                int anInt = Integer.parseInt( indexStr ) - incFactor;
                if ( anInt < 0 ) anInt = 0;
                session.setAttribute( "BillBoard.disc_index", "" + anInt );
            }
        } catch ( Exception e ) {
            session.setAttribute( "BillBoard.disc_index", "0" );
            log( "DecreaseIndex failed!" );
            return false;
        }
        return true;
    }

    /**
     * Returns the current discussion index. If somethings happens, zero will be returned.
     */
    private int getDiscIndex( HttpServletRequest req ) {
        try {
            HttpSession session = req.getSession( false );
            if ( session != null ) {
                String indexStr = (String)session.getAttribute( "BillBoard.disc_index" );
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
     * Collects the standard parameters from the SESSION object.
     */

    private Properties getPropertiesOfBillBoardSessionParameters( HttpServletRequest req ) {

        // Lets get the standard metainformation
        Properties reqParams = MetaInfo.createPropertiesFromMetaInfoParameters( super.getBillBoardSessionParameters( req ) );

        // Lets get the session
        HttpSession session = req.getSession( false );
        if ( session != null ) {
            // Lets get the parameters we know we are supposed to get from the request object
            String sectionId = ( (String)session.getAttribute( "BillBoard.section_id" ) == null ) ? "" : ( (String)session.getAttribute( "BillBoard.section_id" ) );
            String discIndex = ( (String)session.getAttribute( "BillBoard.disc_index" ) == null ) ? "" : ( (String)session.getAttribute( "BillBoard.disc_index" ) );
            reqParams.setProperty( "DISC_INDEX", discIndex );
            reqParams.setProperty( "SECTION_ID", sectionId );

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

        String confForumId = req.getParameter( "section_id" );
        String discIndex = "";
        HttpSession session = req.getSession( false );
        if ( session != null ) {
            if ( confForumId == null )
                confForumId = (String)session.getAttribute( "BillBoard.section_id" );
            discIndex = (String)session.getAttribute( "BillBoard.disc_index" );
            if ( discIndex == null || discIndex.equalsIgnoreCase( "null" ) ) discIndex = "0";
        }
        reqParams.setProperty( "SECTION_ID", confForumId );
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
        tagsV.add( "#DISC_ID#" );
        tagsV.add( "#HEADLINE#" );
        tagsV.add( "#C_REPLIES#" );
        tagsV.add( "#A_DATE#" );
        tagsV.add( "#REPLY_URL#" );
        return tagsV;
    } // End of buildstags

    /**
     * Log function, will work for both servletexec and Apache
     */

    public void log( String msg ) {
        super.log( "BillBoardDisc: " + msg );

    }

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
        today.set( Calendar.DATE, ( today.get( Calendar.DATE ) + 1 ) );
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
        if ( p.getProperty( "TO_DATE" ).equals( "" ) || p.getProperty( "TO_DATE" ).equalsIgnoreCase( p.getProperty( "TO_VALUE" ) ) )
            p.setProperty( "TO_DATE", "" + tYear + "-" + tMonth + "-" + tDay  /*+ " 23:59:59"*/ );
        if ( p.getProperty( "FR_DATE" ).equals( "" ) || p.getProperty( "FR_DATE" ).equalsIgnoreCase( p.getProperty( "FR_VALUE" ) ) )
            p.setProperty( "FR_DATE", "" + yYear + "-" + yMonth + "-" + yDay /*+ " 00:00"*/ );

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
