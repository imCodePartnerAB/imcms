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
    String getMetaId( HttpServletRequest req ) {

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

    imcode.server.User getUserObj( HttpServletRequest req,
                                             HttpServletResponse res ) throws IOException {

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

    MetaInfo.Parameters getBillBoardSessionParameters( HttpServletRequest req ) {

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

    void addExtSessionParametersToProperties( HttpServletRequest req, Properties params ) {

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

    boolean checkSession( HttpServletRequest req, HttpServletResponse res )
            throws IOException {

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

    /**
     Gives the folder to the root external folder,Example /templates/se/102/
     */

    File getExternalTemplateRootFolder( HttpServletRequest req )//p ok
            throws IOException {
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

    File getExternalTemplateFolder( HttpServletRequest req )//p ok
            throws IOException {
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

    private File getExternalTemplateFolder( IMCServiceInterface imcref, String metaId ) {

        if ( metaId == null ) {
            log( "No meta_id could be found! Error in BillBoard.class" );
            throw new IllegalArgumentException();
        }
        return imcref.getExternalTemplateFolder( Integer.parseInt( metaId ) );
    }


    /**
     Returns the foldername where the templates are situated for a certain metaid.
     **/
    private String getTemplateLibName( IMCPoolInterface billref, String meta_id ) {
        String libName = billref.sqlProcedureStr( "B_GetTemplateLib", new String[]{meta_id} );
        if ( libName == null ) {
            libName = "original";
        }
        libName += "/";
        return libName;

    } // End of getTemplateLibName



    //************************ END GETEXTERNAL TEMPLATE FUNCTIONS ***************

    /**
     SendHtml. Generates the html page to the browser. Uses the templatefolder
     by taking the metaid from the request object to determind the templatefolder.
     Will by default handle maximum 3 servletadresses.
     */

    void sendHtml( HttpServletRequest req, HttpServletResponse res,
                          VariableManager vm, String htmlFile ) throws IOException {
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
     Converts array to vector
     */

    Vector convert2Vector( String[] arr ) {
        Vector rolesV = new Vector();
        for ( int i = 0; i < arr.length; i++ )
            rolesV.add( arr[i] );
        return rolesV;
    }

    /**
     Prepare user for the conference
     **/

    boolean prepareUserForBillBoard( HttpServletRequest req, HttpServletResponse res,
                                            MetaInfo.Parameters params, String loginUserId ) throws IOException {

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

    String getExternalImageFolder( HttpServletRequest req ) throws IOException {
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

    private String getAdminButtonLink( HttpServletRequest req, imcode.server.User user, VariableManager adminButtonVM )
            throws IOException {
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
            HtmlGenerator htmlObj = new HtmlGenerator( templateLib, ADMIN_BUTTON_TEMPLATE );
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
    private String getUnAdminButtonLink( HttpServletRequest req, imcode.server.User user, VariableManager unAdminButtonVM )
            throws IOException {
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
            HtmlGenerator htmlObj = new HtmlGenerator( templateLib, UNADMIN_BUTTON_TEMPLATE );
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
    String verifySqlText( String str ) {
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
    Properties verifyForSql( Properties aPropObj ) {
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
     * checks if user is authorized
     * @param req
     * @param res is used if error (send user to conference_starturl )
     * @param user
     */
    boolean isUserAuthorized( HttpServletRequest req, HttpServletResponse res, imcode.server.User user )
            throws IOException {

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
    boolean isUserAuthorized( HttpServletRequest req, HttpServletResponse res, int metaId, imcode.server.User user )
            throws IOException {

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
    boolean userHasRightToEdit( IMCServiceInterface imcref, int metaId,
                                          imcode.server.User user ) {

        return ( imcref.checkDocRights( metaId, user ) &&
                imcref.checkDocAdminRights( metaId, user ) );
    }

    /**
     * check if user is admin and has rights to edit
     * @param imcref imCMS IMCServiceInterface instance
     * @param metaId metaId for conference
     * @param user
     */
    boolean userHasAdminRights( IMCServiceInterface imcref, int metaId,
                                          imcode.server.User user ) {
        return ( imcref.checkDocAdminRights( metaId, user ) &&
                imcref.checkDocAdminRights( metaId, user, 65536 ) );

    }

    /**
     Parses one record.
     */
    String parseOneRecord( Vector tagsV, Vector dataV, File htmlCodeFile ) {
        // Lets parse one aHref reference
        ParseServlet parser = new ParseServlet( htmlCodeFile, tagsV, dataV );
        String oneRecordsHtmlCode = parser.getHtmlDoc();
        return oneRecordsHtmlCode;
    } // End of parseOneRecord

    void setBillBoardSessionAttributes( HttpSession session, MetaInfo.Parameters params) {
        session.setAttribute("BillBoard.meta_id", ""+params.getMetaId()) ;
        session.setAttribute("BillBoard.parent_meta_id", ""+params.getParentMetaId()) ;
    }


} // End class
