package com.imcode.imcms.servlet.conference;

import imcode.external.diverse.*;
import imcode.server.ApplicationServer;
import imcode.server.IMCPoolInterface;
import imcode.server.IMCServiceInterface;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;

import javax.servlet.http.*;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

/**
 * superclas for conference servlets.
 * <p/>
 * Html template in use:
 * Conf_Admin_Button.htm
 * <p/>
 * Html parstags in use:
 * #ADMIN_TYPE#
 * #TARGET#
 * <p/>
 * stored procedures in use:
 * -
 *
 * @author Rickard Larsson, Jerker Drottenmyr
 * @version 1.5 20 Nov 2000
 */

public class Conference extends HttpServlet {

    private final static String ADMIN_BUTTON_TEMPLATE = "Conf_Admin_Button.htm";
    private final static String UNADMIN_BUTTON_TEMPLATE = "Conf_Unadmin_Button.htm";

    /**
     * Returns the metaId from a request object, if not found, we will
     * get the one from our session object. If still not found then null is returned.
     */

    int getMetaId( HttpServletRequest req ) {

        String metaId = req.getParameter( "meta_id" );
        if ( metaId == null ) {
            HttpSession session = req.getSession( false );
            if ( session != null ) {
                metaId = (String)session.getAttribute( "Conference.meta_id" );
            }
        }
        return Integer.parseInt( metaId );
    }

    // *************** LETS HANDLE THE SESSION META PARAMETERS *********************


    /**
     * Collects the standard parameters from the session object
     */

    MetaInfo.Parameters getConferenceSessionParameters( HttpServletRequest req ) {

        // Get the session
        HttpSession session = req.getSession( true );
        String metaIdStr = ( (String)session.getAttribute( "Conference.meta_id" ) == null ) ? "" : ( (String)session.getAttribute( "Conference.meta_id" ) );
        String parentIdStr = ( (String)session.getAttribute( "Conference.parent_meta_id" ) == null ) ? "" : ( (String)session.getAttribute( "Conference.parent_meta_id" ) );

        int metaId = Integer.parseInt( metaIdStr );
        int parentMetaId = Integer.parseInt( parentIdStr );

        return new MetaInfo.Parameters( metaId, parentMetaId );
    }

    /**
     * Collects the EXTENDED parameters from the session object. As extended paramters are we
     * counting:
     * <p/>
     * Conference.forum_id
     * Conference.discussion_id
     *
     * @param params if a properties object is passed, we will fill the
     *               object with the extended paramters, otherwise we will create one.
     */

    void addExtSessionParametersToProperties( HttpServletRequest req, Properties params ) {

        // Get the session
        HttpSession session = req.getSession( true );
        String forumId = ( (String)session.getAttribute( "Conference.forum_id" ) == null ) ? "" : ( (String)session.getAttribute( "Conference.forum_id" ) );
        String discId = ( (String)session.getAttribute( "Conference.disc_id" ) == null ) ? "" : ( (String)session.getAttribute( "Conference.disc_id" ) );

        if ( params == null )
            params = new Properties();
        params.setProperty( "FORUM_ID", forumId );
        params.setProperty( "DISC_ID", discId );
    }

    /**
     * Gives the folder to the root external folder
     */

    File getExternalTemplateRootFolder( HttpServletRequest req ) throws IOException {
        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();

        int metaId = this.getMetaId( req );
        UserDomainObject user = Utility.getLoggedOnUser( req );

        return imcref.getExternalTemplateFolder( metaId, user);
    }

    /**
     * Gives the folder where All the html templates for a language are located.
     * This method will call its helper method getTemplateLibName to get the
     * name of the folder which contains the templates for a certain meta id
     */

    File getExternalTemplateFolder( HttpServletRequest req )
            throws IOException {

        // Lets get serverinformation
        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();
        IMCPoolInterface confref = ApplicationServer.getIMCPoolInterface();

        UserDomainObject user = Utility.getLoggedOnUser( req );
        int metaId = this.getMetaId( req );

        File extFolder = imcref.getExternalTemplateFolder( metaId, user);
        return new File( extFolder, this.getTemplateLibName( confref, metaId ) );
    }

    /**
     * Returns the foldername where the templates are situated for a certain metaid.
     */
    private String getTemplateLibName( IMCPoolInterface confref, int meta_id ) {
        String libName = confref.sqlProcedureStr( "A_GetTemplateLib", new String[]{"" + meta_id} );
        if ( libName == null ) {
            libName = "original";
        }
        libName += "/";
        return libName;
    } // End of getTemplateLibName

    /**
     * SendHtml. Generates the html page to the browser. Uses the templatefolder
     * by taking the metaid from the request object to determind the templatefolder.
     * Will by default handle maximum 3 servletadresses.
     */

    void sendHtml( HttpServletRequest req, HttpServletResponse res,
                   VariableManager vm, String htmlFile ) throws IOException {

        // Lets get the TemplateFolder  and the foldername used for this certain metaid
        File templateLib = this.getExternalTemplateFolder( req );

        // Lets get the path to the imagefolder.
        String imagePath = this.getExternalImageFolder( req );
        //log("ImagePath: " + imagePath) ;

        VariableManager adminButtonVM = new VariableManager();
        adminButtonVM.addProperty( "IMAGE_URL", imagePath );
        adminButtonVM.addProperty( "SERVLET_URL", "" );
        adminButtonVM.addProperty( "ADMIN_LINK_HTML", vm.getProperty( "ADMIN_LINK_HTML" ) );

        //log("vm: " + vm.toString()) ;
        VariableManager unAdminButtonVM = new VariableManager();
        unAdminButtonVM.addProperty( "IMAGE_URL", imagePath );
        unAdminButtonVM.addProperty( "SERVLET_URL", "" );
        unAdminButtonVM.addProperty( "UNADMIN_LINK_HTML", vm.getProperty( "UNADMIN_LINK_HTML" ) );

        vm.addProperty( "IMAGE_URL", imagePath );
        vm.addProperty( "SERVLET_URL", "" );

        //String adminBtn = this.getAdminButtonLink( req, user, adminButtonVM ) ;
        UserDomainObject user = Utility.getLoggedOnUser( req );
        String adminBtn = this.getAdminButtonLink( req, user, adminButtonVM );
        vm.addProperty( "CONF_ADMIN_LINK", adminBtn );

        // log("before UNadminBUttonlink: " + imagePath) ;
        String unAdminBtn = this.getUnAdminButtonLink( req, user, unAdminButtonVM );
        vm.addProperty( "CONF_UNADMIN_LINK", unAdminBtn );

        // log("Before HTmlgenerator: ") ;
        HtmlGenerator htmlObj = new HtmlGenerator( templateLib, htmlFile );
        String html = htmlObj.createHtmlString( vm, req );
        //log("Before sendToBrowser: ") ;

        htmlObj.sendToBrowser( req, res, html );
        //log("after sendToBrowser: ") ;

    }

    /**
     * Log function. Logs the message to the log file and console
     */

    public void log( String msg ) {
        super.log( msg );
        System.out.println( "Conference: " + msg );

    }

    /**
     * Converts array to vector
     */

    Vector convert2Vector( String[] arr ) {
        Vector rolesV = new Vector();
        for ( int i = 0; i < arr.length; i++ )
            rolesV.add( arr[i] );
        return rolesV;
    }

    /**
     * Prepare user for the conference
     */

    boolean prepareUserForConf( HttpServletRequest req, HttpServletResponse res,
                                MetaInfo.Parameters params, String loginUserId ) throws IOException {

        // Lets get userparameters
        String metaId = "" + params.getMetaId();

        // Lets get serverinformation
        IMCPoolInterface confref = ApplicationServer.getIMCPoolInterface();

        // Ok, Lets prepare the user for the conference.
        // Lets get his lastLoginDate and update it to today
        String lastLoginDate = confref.sqlProcedureStr( "A_GetLastLoginDate2", new String[]{metaId, loginUserId} );
        String firstName;
        String lastName;

        // Ok, if lastlogindate is null, then it has to be the a user who has logged in
        // to the system and comes here to the conference for the first time
        // Lets add the user to conference db.
        if ( lastLoginDate == null ) {
            // Ok, det är första gången användaren är här.

            UserDomainObject user = Utility.getLoggedOnUser( req );
            firstName = user.getFirstName();
            lastName = user.getLastName();

            confref.sqlUpdateProcedure( "A_ConfUsersAdd", new String[]{loginUserId, metaId, firstName, lastName} );

            // Ok, try to get the lastLoginDate now and validate it
            lastLoginDate = confref.sqlProcedureStr( "A_GetLastLoginDate2", new String[]{} );

            if ( lastLoginDate == null ) {
                String header = "ConfManager servlet. ";
                ConfError err = new ConfError( req, res, header, 30 );
                log( header + err.getErrorMsg() );
                return false;
            }	// End lastLoginCheck
            // Exta add 2000-09-14, Lets set the lastlogindate in the users
            // object to an old date so all discussions will have a new flag
            // so all flags will be shown
            lastLoginDate = "1997-01-01 00:00";
            // log("Nytt last login date:" + lastLoginDate) ;

        } else {
            // Ok, the user has logged in to the conference by the loginpage
            // for the conference, he has a logindate. Lets get his names
            // Lets get the users first and last names
            firstName = confref.sqlProcedureStr( "A_GetConfLoginNames", new String[]{metaId, loginUserId, "1"} );
            lastName = confref.sqlProcedureStr( "A_GetConfLoginNames", new String[]{metaId, loginUserId, "2"} );
        } // end else

        // Lets update his logindate and usernames
        confref.sqlUpdateProcedure( "A_ConfUsersUpdate", new String[]{metaId, loginUserId, firstName, lastName} );
        // Lets store some values in his session object
        HttpSession session = req.getSession( false );
        if ( session != null ) {
            setSessionAttributes( session, params );
            session.setAttribute( "Conference.viewedDiscList", new Properties() );
            session.setAttribute( "Conference.last_login_date", lastLoginDate );
            session.setAttribute( "Conference.user_id", loginUserId );
            session.setAttribute( "Conference.disc_index", "0" );


            // Ok, we need to catch a forum_id. Lets get the first one for this meta_id.
            // if not a forumid exists, the sp will return -1
            String aForumId = confref.sqlProcedureStr( "A_GetFirstForum", new String[]{"" + params.getMetaId()} );
            session.setAttribute( "Conference.forum_id", aForumId );

            // Ok, Lets get the last discussion in that forum
            String aDiscId = confref.sqlProcedureStr( "A_GetLastDiscussionId", new String[]{"" + params.getMetaId(), aForumId} );

            // Lets get the lastdiscussionid for that forum
            // if not a aDiscId exists, then the  sp will return -1
            session.setAttribute( "Conference.disc_id", aDiscId );

            res.sendRedirect( "ConfViewer" );
            return true;
        }
        return false;

    } // End prepare user for conference

    // ****************** GetImageFolder Functions *********************

    /**
     * Gives the folder where All the html templates for a language are located.
     * This method will call its helper method getTemplateLibName to get the
     * name of the folder which contains the templates for a certain meta id
     */

    String getExternalImageFolder( HttpServletRequest req ) throws IOException {
        int metaId = this.getMetaId( req );

        // Lets get serverinformation
        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();
        IMCPoolInterface confref = ApplicationServer.getIMCPoolInterface();

        UserDomainObject user = Utility.getLoggedOnUser( req );
        String lang_prefix = imcref.getDefaultLanguageAsIso639_2();

        if( user != null){
            lang_prefix = user.getLanguageIso639_2();
        }

        String extFolder = RmiConf.getExternalImageFolder( imcref, metaId, lang_prefix);

        return extFolder += this.getTemplateLibName( confref, metaId );
    }


    // ***************** RETURNS THE HTML CODE TO THE ADMINIMAGE **************
    /**
     * Checks whether or not the user is an administrator and
     * Creates the html code, used to view the adminimage and an appropriate link
     * to the adminservlet.
     *
     * @param req           requestobject
     * @param user          userobject
     * @param adminButtonVM hashtabele of tags to replace
     * @return returns string of html code for adminlink
     */

    private String getAdminButtonLink( HttpServletRequest req, imcode.server.user.UserDomainObject user, VariableManager adminButtonVM )
            throws IOException {

        // Lets get serverinformation
        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();

        String adminLink = "&nbsp;";

        int intMetaId = getMetaId( req );

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
     * Checks whether or not the user is an administrator and
     * Creates the html code, used to view the adminimage and an appropriate link
     * to the adminservlet.
     */
    private String getUnAdminButtonLink( HttpServletRequest req, imcode.server.user.UserDomainObject user, VariableManager unAdminButtonVM )
            throws IOException {

        // Lets get serverinformation
        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();

        String unAdminLink = "&nbsp;";
        int intMetaId = getMetaId( req );

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
     * Examines a text, and watches for ' signs, which will extended with another ' sign
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
     * Checks for illegal sql parameters.
     */
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
     *
     * @param req
     * @param res  is used if error (send user to conference_starturl )
     * @param user
     */
    boolean isUserAuthorized( HttpServletRequest req, HttpServletResponse res, imcode.server.user.UserDomainObject user )
            throws IOException {

        HttpSession session = req.getSession( true );

        //lets get if user authorized or not
        boolean authorized;
        String stringMetaId = (String)session.getAttribute( "Conference.meta_id" );
        if ( stringMetaId == null ) {
            authorized = false;
            //lets send unauthorized users out
            IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();
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
     *
     * @param req    is used for collecting serverinfo and session
     * @param res    is used if error (send user to conference_starturl )
     * @param metaId conference metaId
     * @param user
     */
    boolean isUserAuthorized( HttpServletRequest req, HttpServletResponse res, int metaId, imcode.server.user.UserDomainObject user )
            throws IOException {

        // Lets get serverinformation
        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();

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
     *
     * @param imcref imCMS IMCServiceInterface instance
     * @param metaId metaId for conference
     * @param user
     */
    boolean userHasRightToEdit( IMCServiceInterface imcref, int metaId,
                                imcode.server.user.UserDomainObject user ) {

        return ( imcref.checkDocRights( metaId, user ) &&
                imcref.checkDocAdminRights( metaId, user ) );
    }

    /**
     * check if user is admin and has rights to edit
     *
     * @param imcref imCMS IMCServiceInterface instance
     * @param metaId metaId for conference
     * @param user
     */
    boolean userHasAdminRights( IMCServiceInterface imcref, int metaId,
                                imcode.server.user.UserDomainObject user ) {
        return ( imcref.checkDocAdminRights( metaId, user ) &&
                imcref.checkDocAdminRights( metaId, user, 65536 ) );
    }

    /**
     * Parses one record.
     */
    String parseOneRecord( Vector tagsV, Vector dataV, File htmlCodeFile ) {

        // Lets parse one aHref reference
        ParseServlet parser = new ParseServlet( htmlCodeFile, tagsV, dataV );
        String oneRecordsHtmlCode = parser.getHtmlDoc();
        return oneRecordsHtmlCode;
    } // End of parseOneRecord

    void setSessionAttributes( HttpSession session, MetaInfo.Parameters params ) {
        session.setAttribute( "Conference.meta_id", "" + params.getMetaId() );
        session.setAttribute( "Conference.parent_meta_id", "" + params.getParentMetaId() );
    }

} // End class
