/*
 *
 * @(#)BillBoard.java
 *
 *
 *
 * Copyright (c)
 *
 */

import imcode.server.*;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import imcode.external.diverse.*;
import imcode.util.*;
import imcode.util.IMCServiceRMI;

/**
 * superclas for billboard servlets.
 *
 * Html template in use:
 * BillBoard_Admin_Button.htm
 * BillBoard_Unadmin_Button.htm
 *
 * Html parstags in use:
 * #IMAGE_URL#
 * #SERVLET_URL#
 * #ADMIN_LINK_HTML#
 * #SECTION_ADMIN_LINK#
 * #SECTION_UNADMIN_LINK#
 * #UNADMIN_LINK_HTML#
 # #UNADMIN_BUTTON#
 *
 * stored procedures in use:
 * CheckAdminRights
 * B_GetTemplateLib
 * B_GetFirstSection
 * B_GetLastDiscussionId
 *
 * @version 1.2 20 Aug 2001
 * @author Rickard Larsson, Jerker Drottenmyr, REBUILD TO BILLBOARD BY Peter Östergren
 */


public class BillBoard extends HttpServlet { //Conference
    private final static String ADMIN_BUTTON_TEMPLATE = "BillBoard_Admin_Button.htm";
    private final static String UNADMIN_BUTTON_TEMPLATE = "BillBoard_Unadmin_Button.htm";

    /**
     Returns the metaId from a request object, if not found, we will
     get the one from our session object. If still not found then null is returned.
     */
    public String getMetaId( HttpServletRequest req )
            throws ServletException, IOException {

        String metaId = req.getParameter( "meta_id" );
        if ( metaId == null ) {
            HttpSession session = req.getSession( false );
            if ( session != null ) {
                metaId = (String)session.getAttribute( "BillBoard.meta_id" );
            }
        }
        if ( metaId == null ) {
            log( "No meta_id could be found! Error in BillBoard.class" );
            return null;
        }
        return metaId;
    }


    /**
     Returns an user object. If an error occurs, an errorpage will be generated.
     */

    protected imcode.server.User getUserObj( HttpServletRequest req,
                                             HttpServletResponse res ) throws ServletException, IOException {

        if ( checkSession( req, res ) == true ) {

            // Get the session
            HttpSession session = req.getSession( true );
            // Does the session indicate this user already logged in?
            Object done = session.getAttribute( "logon.isDone" );  // marker object
            imcode.server.User user = (imcode.server.User)done;

            return user;
        } else {
            String header = "BillBoard servlet.";
            BillBoardError err = new BillBoardError( req, res, header, 2 );
            log( err.getErrorMsg() );
            return null;
        }
    }



    // *************** LETS HANDLE THE SESSION META PARAMETERS *********************


    /**
     Collects the standard parameters from the session object
     **/

    public MetaInfo.Parameters getBillBoardSessionParameters( HttpServletRequest req )
            throws ServletException, IOException {

        // Get the session
        HttpSession session = req.getSession( true );
        String metaIdStr = ( (String)session.getAttribute( "BillBoard.meta_id" ) == null ) ? "" : ( (String)session.getAttribute( "BillBoard.meta_id" ) );//Conference.meta_id
        String parentIdStr = ( (String)session.getAttribute( "BillBoard.parent_meta_id" ) == null ) ? "" : ( (String)session.getAttribute( "BillBoard.parent_meta_id" ) );//Conference.parent_meta_id

        //Properties params = new Properties() ;
        //params.setProperty("META_ID", metaIdStr) ;
        //params.setProperty("PARENT_META_ID", parentIdStr) ;

        int metaId = Integer.parseInt(metaIdStr) ;
        int parentMetaId = Integer.parseInt(parentIdStr) ;

        return new MetaInfo.Parameters(metaId, parentMetaId) ;
    }


    /**
     Collects the EXTENDED parameters from the session object. As extended paramters are we
     counting:

     Conference.forum_id
     Conference.discussion_id

     @param params if a properties object is passed, we will fill the object with the extended paramters, otherwise we will create one.
     **/

    public void addExtSessionParametersToProperties( HttpServletRequest req, Properties params )
            throws ServletException, IOException {

        // Get the session
        HttpSession session = req.getSession( true );
        String sectionId = ( (String)session.getAttribute( "BillBoard.section_id" ) == null ) ? "" : ( (String)session.getAttribute( "BillBoard.section_id" ) );//"Conference.forum_id"
        String discId = ( (String)session.getAttribute( "BillBoard.disc_id" ) == null ) ? "" : ( (String)session.getAttribute( "BillBoard.disc_id" ) );//"Conference.disc_id"

        if ( params == null )
            params = new Properties();
        params.setProperty( "SECTION_ID", sectionId );
        params.setProperty( "DISC_ID", discId );
    }


    /**
     Verifies that the user has logged in. If he hasnt, he will be redirected to
     an url which we get from a init file name conference.
     */

    protected boolean checkSession( HttpServletRequest req, HttpServletResponse res )
            throws ServletException, IOException {

        // Get the session
        HttpSession session = req.getSession( true );
        // Does the session indicate this user already logged in?

        Object done = session.getAttribute( "logon.isDone" );  // marker object

        // Lets get serverinformation
        IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface( req );

        if ( done == null ) {
            // No logon.isDone means he hasn't logged in.
            // Save the request URL as the true target and redirect to the login page.
            session.setAttribute( "login.target", HttpUtils.getRequestURL( req ).toString() );
            String startUrl = imcref.getStartUrl();
            res.sendRedirect( startUrl );

            return false;
        }
        return true;
    }


    protected boolean getAdminRights( IMCServiceInterface imcref, String metaId, imcode.server.User user ) {

        try {
            return userHasAdminRights( imcref, Integer.parseInt( metaId ), user );
        } catch ( IOException e ) {
            log( "GetAdminRights failed!!!" );
            return false;
        }

    } // End GetAdminRights

    /**
     CheckAdminRights, returns true if the user is an superadmin. Only an superadmin
     is allowed to create new users
     False if the user isn't an administrator.
     1 = administrator
     0 = superadministrator
     */

    protected boolean checkAdminRights( IMCServiceInterface imcref, imcode.server.User user ) {

        // Lets verify that the user who tries to add a new user is an SUPERADMIN
        int currUser_id = user.getUserId();
        String checkAdminSql = "CheckAdminRights " + currUser_id;
        String[] roles = imcref.sqlProcedure( checkAdminSql );
        boolean returnValue = false;

        for ( int i = 0; i < roles.length; i++ ) {
            String aRole = roles[i];
            if ( aRole.equalsIgnoreCase( "0" ) )
                returnValue = true;
        }

        return returnValue;

    } // checkAdminRights

    /**
     CheckAdminRights, returns true if the user is an admin.
     False if the user isn't an administrator
     */

    protected boolean checkAdminRights( HttpServletRequest req, HttpServletResponse res )
            throws ServletException, IOException {

        // Lets get serverinformation
        IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface( req );

        imcode.server.User user = getUserObj( req, res );
        if ( user == null ) {
            this.log( "CheckadminRights: an error occured, getUserObj" );
            return false;
        } else {
            return checkAdminRights( imcref, user );
        }
    }


    // *********************** GETEXTERNAL TEMPLATE FUNCTIONS *********************

    /**
     Gives the folder to the root external folder,Example /templates/se/102/
     */

    public File getExternalTemplateRootFolder( HttpServletRequest req )//p ok
            throws ServletException, IOException {
        // Lets get serverinformation
        IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface( req );

        String metaId = this.getMetaId( req );
        if ( metaId == null ) {
            log( "No meta_id could be found! Error in BillBoard.class" );
            throw new IllegalArgumentException();
        }
        return imcref.getExternalTemplateFolder( Integer.parseInt( metaId ) );
    }


    /**
     Gives the folder where All the html templates for a language are located.
     This method will call its helper method getTemplateLibName to get the
     name of the folder which contains the templates for a certain meta id
     */

    public File getExternalTemplateFolder( HttpServletRequest req )//p ok
            throws ServletException, IOException {
        String metaId = this.getMetaId( req );
        if ( metaId == null ) {
            log( "No meta_id could be found! Error in BillBoard.class" );
            throw new IllegalArgumentException();
        }
        // Lets get serverinformation
        IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface( req );
        IMCPoolInterface billref = IMCServiceRMI.getBillboardIMCPoolInterface( req );

        return new File( this.getExternalTemplateFolder( imcref, metaId ), this.getTemplateLibName( billref, metaId ) );
    }

    /**
     Gives the folder where All the html templates for a language are located.
     This method will call its helper method getTemplateLibName to get the
     name of the folder which contains the templates for a certain meta id
     */

    public File getExternalTemplateFolder( IMCServiceInterface imcref, String metaId )
            throws ServletException, IOException {

        if ( metaId == null ) {
            log( "No meta_id could be found! Error in BillBoard.class" );
            throw new IllegalArgumentException();
        }
        return imcref.getExternalTemplateFolder( Integer.parseInt( metaId ) );
    }


    /**
     Returns the foldername where the templates are situated for a certain metaid.
     **/
    protected String getTemplateLibName( IMCPoolInterface billref, String meta_id )
            throws ServletException, IOException {
        String sqlQ = "B_GetTemplateLib " + meta_id;
        String libName = billref.sqlProcedureStr( sqlQ );
        if ( libName == null ) {
            libName = "original";
        }
        libName += "/";
        return libName;

    } // End of getTemplateLibName


    /**
     Collects the parameters from the request object. This function will get all the possible
     parameters this servlet will be able to get. If a parameter wont be found, the session
     parameter will be used instead, or if no such parameter exist in the session object,
     a key with no value = "" will be used instead.
     Since this method is used. it means
     that this servlet will take more arguments than the standard ones.
     **/

    public Properties getRequestParameters( HttpServletRequest req )
            throws ServletException, IOException {

        Properties reqParams = new Properties();

        // Lets get our own variables. We will first look for the discussion_id
        //	 in the request object, if not found, we will get the one from our session object
        String billBoardSectionId = req.getParameter( "section_id" );//"forum_id"

        if ( billBoardSectionId == null ) {
            HttpSession session = req.getSession( false );
            if ( session != null ) {
                billBoardSectionId = (String)session.getAttribute( "BillBoard.section_id" );//"Conference.forum_id"
            }
        }
        reqParams.setProperty( "SECTION_ID", billBoardSectionId );
        return reqParams;
    }



    //************************ END GETEXTERNAL TEMPLATE FUNCTIONS ***************

    /**
     SendHtml. Generates the html page to the browser. Uses the templatefolder
     by taking the metaid from the request object to determind the templatefolder.
     Will by default handle maximum 3 servletadresses.
     */

    public void sendHtml( HttpServletRequest req, HttpServletResponse res,
                          VariableManager vm, String htmlFile ) throws ServletException, IOException {
        imcode.server.User user = getUserObj( req, res );

        String metaId = this.getMetaId( req );
        if ( metaId == null ) {
            log( "NO metaid could be found in the passed request object" );
            String header = "BillBoard servlet. ";
            new BillBoardError( req, res, header, 5 );
            return;
        }

        // Lets get serverinformation
        // Lets get the TemplateFolder  and the foldername used for this certain metaid
        File templateLib = this.getExternalTemplateFolder( req );

        // Lets get the path to the imagefolder.
        String imagePath = this.getExternalImageFolder( req );

        VariableManager adminButtonVM = new VariableManager();
        adminButtonVM.addProperty( "IMAGE_URL", imagePath );
        adminButtonVM.addProperty( "SERVLET_URL", "" );
        adminButtonVM.addProperty( "ADMIN_LINK_HTML", vm.getProperty( "ADMIN_LINK_HTML" ) );

        VariableManager unAdminButtonVM = new VariableManager();
        unAdminButtonVM.addProperty( "IMAGE_URL", imagePath );
        unAdminButtonVM.addProperty( "SERVLET_URL", "" );
        unAdminButtonVM.addProperty( "UNADMIN_LINK_HTML", vm.getProperty( "UNADMIN_LINK_HTML" ) );

        vm.addProperty( "IMAGE_URL", imagePath );
        vm.addProperty( "SERVLET_URL", "" );

        String adminBtn = this.getAdminButtonLink( req, user, adminButtonVM );
        vm.addProperty( "SECTION_ADMIN_LINK", adminBtn );

        String unAdminBtn = this.getUnAdminButtonLink( req, user, unAdminButtonVM );
        vm.addProperty( "SECTION_UNADMIN_LINK", unAdminBtn );

        HtmlGenerator htmlObj = new HtmlGenerator( templateLib, htmlFile );
        String html = htmlObj.createHtmlString( vm, req );
        htmlObj.sendToBrowser( req, res, html );

    }

    /**
     Log function. Logs the message to the log file and console
     */

    public void log( String msg ) {
        super.log( msg );

    }

    /**
     Date function. Returns the current date and time in the swedish style
     */


    public static String getDateToday() {
        java.util.Calendar cal = java.util.Calendar.getInstance();

        String year = Integer.toString( cal.get( Calendar.YEAR ) );
        int month = Integer.parseInt( Integer.toString( cal.get( Calendar.MONTH ) ) ) + 1;
        int day = Integer.parseInt( Integer.toString( cal.get( Calendar.DAY_OF_MONTH ) ) );

        String dateToDay = year;
        dateToDay += "-";
        dateToDay += month < 10 ? "0" + Integer.toString( month ) : Integer.toString( month );
        dateToDay += "-";
        dateToDay += day < 10 ? "0" + Integer.toString( day ) : Integer.toString( day );

        return dateToDay;
    }

    /**
     Date function. Returns the current time in the swedish style
     */

    public static String getTimeNow() {
        java.util.Calendar cal = java.util.Calendar.getInstance();

        int hour = Integer.parseInt( Integer.toString( cal.get( Calendar.HOUR_OF_DAY ) ) );
        int min = Integer.parseInt( Integer.toString( cal.get( Calendar.MINUTE ) ) );
        int sec = Integer.parseInt( Integer.toString( cal.get( Calendar.SECOND ) ) );

        String timeNow = "";
        timeNow += hour < 10 ? "0" + Integer.toString( hour ) : Integer.toString( hour );
        timeNow += ":";
        timeNow += min < 10 ? "0" + Integer.toString( min ) : Integer.toString( min );
        timeNow += ":";
        timeNow += sec < 10 ? "0" + Integer.toString( sec ) : Integer.toString( sec );
        // timeNow += ".000" ;

        return timeNow;
    }


    /**
     Converts array to vector
     */

    public Vector convert2Vector( String[] arr ) {
        Vector rolesV = new Vector();
        for ( int i = 0; i < arr.length; i++ )
            rolesV.add( arr[i] );
        return rolesV;
    }


    /**
     Creates Sql characters. Encapsulates a string with ' ' signs. + an comma.
     And surrounding space.
     Example. this.sqlP("myString")  --> " 'myString', "
     */

    public static String sqlPDelim( String s ) {
        return " '" + s + "', ";
    }

    /**
     Creates Sql characters. Encapsulates a string with ' ' signs. And surrounding
     Space.
     Example. this.sqlP("myString")  --> " 'myString' "
     */

    public static String sqlP( String s ) {
        return " '" + s + "' ";
    }


    /**
     Prepare user for the conference
     **/

    public boolean prepareUserForBillBoard( HttpServletRequest req, HttpServletResponse res,
                                            MetaInfo.Parameters params, String loginUserId ) throws ServletException, IOException {

        // Lets get the user object
        imcode.server.User user = this.getUserObj( req, res );
        if ( user == null ) return false;

        // Lets get serverinformation
        IMCPoolInterface billref = IMCServiceRMI.getBillboardIMCPoolInterface( req );

        // Lets store some values in his session object
        HttpSession session = req.getSession( false );
        if ( session != null ) {
            setBillBoardSessionAttributes(session,params);
            session.setAttribute( "BillBoard.viewedDiscList", new Properties() );//Conference.viewedDiscList
            session.setAttribute( "BillBoard.user_id", loginUserId );//Conference.user_id
            session.setAttribute( "BillBoard.disc_index", "0" );

            // Ok, we need to catch a forum_id. Lets get the first one for this meta_id.
            // if not a forumid exists, the sp will return -1
            String aSectionId = billref.sqlProcedureStr( "B_GetFirstSection", new String[] { ""+params.getMetaId() } );
            session.setAttribute( "BillBoard.section_id", aSectionId );//Conference.forum_id

            // Lets get the lastdiscussionid for that forum
            // if not a aDiscId exists, then the  sp will return -1
            session.setAttribute( "BillBoard.disc_id", "-1" );

            res.sendRedirect( "BillBoardViewer" );

            return true;
        }
        return false;

    } // End prepare user for conference

    // ****************** GetImageFolder Functions *********************

    /**
     Gives the folder where All the html templates for a language are located.
     This method will call its helper method getTemplateLibName to get the
     name of the folder which contains the templates for a certain meta id
     */

    public String getExternalImageFolder( HttpServletRequest req ) throws ServletException, IOException {
        String metaId = this.getMetaId( req );
        if ( metaId == null ) {
            log( "No meta_id could be found! Error in BillBoard.class" );
            return "No meta_id could be found!";
        }
        HttpSession session = req.getSession( true );
        imcode.server.User user = (imcode.server.User)session.getAttribute( "logon.isDone" );
        if ( user == null ) {
            return null;
        }

        // Lets get serverinformation
        IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface( req );
        IMCPoolInterface billref = IMCServiceRMI.getBillboardIMCPoolInterface( req );

        String extFolder = RmiConf.getExternalImageFolder( imcref, metaId );
        extFolder += this.getTemplateLibName( billref, metaId );

        return extFolder;
    }


    /**
     Returns the foldername where the templates are situated for a certain metaid.
     **/
    protected String getImageLibName( IMCPoolInterface billref, String meta_id ) throws ServletException, IOException {
        String sqlQ = "B_GetTemplateLib " + meta_id;
        String libName = "" + billref.sqlProcedureStr( sqlQ ) + "/";
        return libName;

    } // End of getImageLibName


    // ***************** RETURNS THE HTML CODE TO THE ADMINIMAGE **************
    /**
     * Checks whether or not the user is an administrator and
     * Creates the html code, used to view the adminimage and an appropriate link
     * to the adminservlet.
     *
     * @param req requestobject
     * @param user userobject
     * @param adminButtonVM hashtabele of tags to replace
     *
     * @return returns string of html code for adminlink
     */

    public String getAdminButtonLink( HttpServletRequest req, imcode.server.User user, VariableManager adminButtonVM )
            throws ServletException, IOException {
        // Lets get serverinformation
        IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface( req );

        String adminLink = "&nbsp;";
        String metaId = getMetaId( req );
        int intMetaId = Integer.parseInt( metaId );

        //log("before getAdminRights") ;
        //lets generat adminbutton if user has administrator rights and rights to edit
        if ( userHasAdminRights( imcref, intMetaId, user ) ) {

            //lets save tags we need later
            VariableManager adminLinkVM = new VariableManager();
            adminLinkVM.addProperty( "SERVLET_URL", adminButtonVM.getProperty( "SERVLET_URL" ) );
            String adminLinkFile = adminButtonVM.getProperty( "ADMIN_LINK_HTML" );

            //lets create adminbuttonhtml
            File templateLib = this.getExternalTemplateFolder( req );
            HtmlGenerator htmlObj = new HtmlGenerator( templateLib, this.ADMIN_BUTTON_TEMPLATE );
            String adminBtn = htmlObj.createHtmlString( adminButtonVM, req );

            //lets create adminlink
            adminLinkVM.addProperty( "ADMIN_BUTTON", adminBtn );
            if ( !adminLinkFile.equals( "" ) ) {
                HtmlGenerator linkHtmlObj = new HtmlGenerator( templateLib, adminLinkFile );
                adminLink = linkHtmlObj.createHtmlString( adminLinkVM, req );
            }
        }
        //log("After getAdminRights") ;
        return adminLink;
    } // End CreateAdminHtml

    /**
     Checks whether or not the user is an administrator and
     Creates the html code, used to view the adminimage and an appropriate link
     to the adminservlet.
     */
    public String getUnAdminButtonLink( HttpServletRequest req, imcode.server.User user, VariableManager unAdminButtonVM )
            throws ServletException, IOException {
        // Lets get serverinformation
        IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface( req );

        String unAdminLink = "&nbsp;";
        String metaId = getMetaId( req );
        int intMetaId = Integer.parseInt( metaId );

        //lets generat unadminbutton if user has administrator rights and rights to edit
        if ( userHasAdminRights( imcref, intMetaId, user ) ) {
            //lets save tags we need later
            VariableManager unAdminLinkVM = new VariableManager();
            unAdminLinkVM.addProperty( "SERVLET_URL", unAdminButtonVM.getProperty( "SERVLET_URL" ) );
            String unAdminLinkFile = unAdminButtonVM.getProperty( "UNADMIN_LINK_HTML" );

            //lets create unadminbuttonhtml
            File templateLib = this.getExternalTemplateFolder( req );
            HtmlGenerator htmlObj = new HtmlGenerator( templateLib, this.UNADMIN_BUTTON_TEMPLATE );
            String unAdminBtn = htmlObj.createHtmlString( unAdminButtonVM, req );

            //lets create unadminlink
            unAdminLinkVM.addProperty( "UNADMIN_BUTTON", unAdminBtn );
            if ( !unAdminLinkFile.equals( "" ) ) {
                HtmlGenerator linkHtmlObj = new HtmlGenerator( templateLib, unAdminLinkFile );
                unAdminLink = linkHtmlObj.createHtmlString( unAdminLinkVM, req );
            }
        }
        return unAdminLink;
    } // End CreateAdminHtml

    /**
     Examines a text, and watches for ' signs, which will extended with another ' sign
     */
    public String verifySqlText( String str ) {
        StringBuffer buf = new StringBuffer( str );
        // log("Innan: " + str) ;
        char apostrof = '\'';
        for ( int i = 0; i < buf.length(); i++ ) {
            //log(""+ buf.charAt(i)) ;
            if ( buf.charAt( i ) == apostrof ) {
                buf.insert( i, apostrof );
                i += 1;
            }
        }
        str = buf.toString();
        // log("Efter: " + str) ;
        return str;

    } // End CreateAdminHtml


    /**
     Checks for illegal sql parameters.
     **/
    public Properties verifyForSql( Properties aPropObj ) {
        // Ok, Lets find all apostrofes and if any,add another one
        Enumeration enumValues = aPropObj.elements();
        Enumeration enumKeys = aPropObj.keys();
        while ( ( enumValues.hasMoreElements() && enumKeys.hasMoreElements() ) ) {
            Object oKeys = ( enumKeys.nextElement() );
            Object oValue = ( enumValues.nextElement() );
            String theVal = oValue.toString();
            String theKey = oKeys.toString();
            aPropObj.setProperty( theKey, verifySqlText( theVal ) );
        }
        // log(aPropObj.toString()) ;
        return aPropObj;
    } // verifyForSql

    /**
     Checks for illegal sql parameters.
     **/
    public String props2String( Properties p ) {

        Enumeration enumValues = p.elements();
        Enumeration enumKeys = p.keys();
        String aLine = "";
        while ( ( enumValues.hasMoreElements() && enumKeys.hasMoreElements() ) ) {
            String oKeys = (String)( enumKeys.nextElement() );
            String oValue = (String)( enumValues.nextElement() );
            if ( oValue == null ) {
                oValue = "NULL";

            }
            aLine += oKeys.toString() + "=" + oValue.toString() + '\n';
        }
        return aLine;
    }

    /**
     * checks if user is authorized
     * @param req
     * @param res is used if error (send user to conference_starturl )
     * @param user
     */
    protected boolean isUserAuthorized( HttpServletRequest req, HttpServletResponse res, imcode.server.User user )
            throws ServletException, IOException {

        // Lets get serverinformation
        IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface( req );

        HttpSession session = req.getSession( true );

        //lets get if user authorized or not
        boolean authorized = true;
        String stringMetaId = (String)session.getAttribute( "BillBoard.meta_id" );//Conference.meta_id
        if ( stringMetaId == null ) {
            authorized = false;
            //lets send unauthorized users out
            String startUrl = imcref.getStartUrl();
            res.sendRedirect( startUrl );
        } else {
            int metaId = Integer.parseInt( stringMetaId );
            authorized = isUserAuthorized( req, res, metaId, user );
        }

        return authorized;
    }

    /**
     * checks if user is authorized
     * @param req is used for collecting serverinfo and session
     * @param res is used if error (send user to conference_starturl )
     * @param metaId conference metaId
     * @param user
     */
    protected boolean isUserAuthorized( HttpServletRequest req, HttpServletResponse res, int metaId, imcode.server.User user )
            throws ServletException, IOException {

        // Lets get serverinformation
        IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface( req );


        //is user authorized?
        boolean authorized = imcref.checkDocRights( metaId, user );

        //lets send unauthorized users out
        if ( !authorized ) {
            String startUrl = imcref.getStartUrl();
            res.sendRedirect( startUrl );
        }

        return authorized;
    }

    /**
     * check if user has right to edit
     * @param imcref imCMS IMCServiceInterface instance
     * @param metaId metaId for conference
     * @param user
     */
    protected boolean userHasRightToEdit( IMCServiceInterface imcref, int metaId,
                                          imcode.server.User user ) throws java.io.IOException {

        return ( imcref.checkDocRights( metaId, user ) &&
                imcref.checkDocAdminRights( metaId, user ) );
    }

    /**
     * check if user is admin and has rights to edit
     * @param imcref imCMS IMCServiceInterface instance
     * @param metaId metaId for conference
     * @param user
     */
    protected boolean userHasAdminRights( IMCServiceInterface imcref, int metaId,
                                          imcode.server.User user ) throws java.io.IOException {
        return ( imcref.checkDocAdminRights( metaId, user ) &&
                imcref.checkDocAdminRights( metaId, user, 65536 ) );

    }

    /**
     Parses one record.
     */
    public String parseOneRecord( String[] tags, String[] data, File htmlCodeFile ) {

        Vector tagsV = convert2Vector( tags );
        Vector dataV = convert2Vector( data );
        return this.parseOneRecord( tagsV, dataV, htmlCodeFile );
    }


    /**
     Parses one record.
     */
    public String parseOneRecord( Vector tagsV, Vector dataV, File htmlCodeFile ) {
        // Lets parse one aHref reference
        ParseServlet parser = new ParseServlet( htmlCodeFile, tagsV, dataV );
        String oneRecordsHtmlCode = parser.getHtmlDoc();
        return oneRecordsHtmlCode;
    } // End of parseOneRecord


    protected void setBillBoardSessionAttributes( HttpSession session, Properties params ) {
        int metaId = Integer.parseInt(params.getProperty("META_ID")) ;
        int parentMetaId = Integer.parseInt(params.getProperty("PARENT_META_ID")) ;

        setBillBoardSessionAttributes(session, new MetaInfo.Parameters(metaId,parentMetaId));
    }

    protected void setBillBoardSessionAttributes( HttpSession session, MetaInfo.Parameters params) {
        session.setAttribute("BillBoard.meta_id", ""+params.getMetaId()) ;
        session.setAttribute("BillBoard.parent_meta_id", ""+params.getParentMetaId()) ;
    }


} // End class
