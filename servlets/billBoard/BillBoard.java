import imcode.server.*;
import imcode.server.user.UserDomainObject;

import java.io.*;
import java.util.*;
import javax.servlet.http.*;

import imcode.external.diverse.*;
import imcode.util.Utility;

public class BillBoard extends HttpServlet {
    private final static String ADMIN_BUTTON_TEMPLATE = "BillBoard_Admin_Button.htm";
    private final static String UNADMIN_BUTTON_TEMPLATE = "BillBoard_Unadmin_Button.htm";

    /**
     * Returns the metaId from a request object, if not found, we will
     * get the one from our session object. If still not found then null is returned.
     */
    int getMetaId( HttpServletRequest req ) {

        String metaId = req.getParameter( "meta_id" );
        if ( metaId == null ) {
            HttpSession session = req.getSession( false );
            if ( session != null ) {
                metaId = (String)session.getAttribute( "BillBoard.meta_id" );
            }
        }
        return Integer.parseInt( metaId );
    }

    /**
     * Returns an user object. If an error occurs, an errorpage will be generated.
     */
     UserDomainObject getUserObj( HttpServletRequest req,
                                                    HttpServletResponse res ) throws IOException {

        // Lets get serverinformation
        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();

        String default_lang_prefix = imcref.getDefaultLanguageAsIso639_2();

        if ( checkSession( req, res ) == true ) {
            UserDomainObject user = Utility.getLoggedOnUser( req );
            return user;
        } else {
            String header = "BillBoard servlet.";
            new BillBoardError( req, res, header, 2, default_lang_prefix );
            return null;
        }
    }

    /**
     * Collects the standard parameters from the session object
     */

    MetaInfo.Parameters getBillBoardSessionParameters( HttpServletRequest req ) {

        // Get the session
        HttpSession session = req.getSession( true );
        String metaIdStr = ( (String)session.getAttribute( "BillBoard.meta_id" ) == null ) ? "" : ( (String)session.getAttribute( "BillBoard.meta_id" ) );//Conference.meta_id
        String parentIdStr = ( (String)session.getAttribute( "BillBoard.parent_meta_id" ) == null ) ? "" : ( (String)session.getAttribute( "BillBoard.parent_meta_id" ) );//Conference.parent_meta_id

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
     * @param params if a properties object is passed, we will fill the object with the extended paramters, otherwise we will create one.
     */

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
     * Verifies that the user has logged in. If he hasnt, he will be redirected to
     * an url which we get from a init file name conference.
     */

    boolean checkSession( HttpServletRequest req, HttpServletResponse res )
            throws IOException {
        return true;
    }

    /**
     * Gives the folder to the root external folder
     */

    File getExternalTemplateRootFolder( HttpServletRequest req )//p ok
            throws IOException {

        UserDomainObject user = Utility.getLoggedOnUser( req );

        // Lets get serverinformation
        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();

        int metaId = this.getMetaId( req );
        return imcref.getExternalTemplateFolder( metaId, user);
    }

    /**
     * Gives the folder where All the html templates for a language are located.
     * This method will call its helper method getTemplateLibName to get the
     * name of the folder which contains the templates for a certain meta id
     */

    File getExternalTemplateFolder( HttpServletRequest req )//p ok
            throws IOException {
        int metaId = this.getMetaId( req );
        // Lets get serverinformation
        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();
        IMCPoolInterface billref = ApplicationServer.getIMCPoolInterface();
        UserDomainObject user = Utility.getLoggedOnUser( req );
        return new File( imcref.getExternalTemplateFolder( metaId, user ), this.getTemplateLibName( billref, metaId ) );
    }

    /**
     * Returns the foldername where the templates are situated for a certain metaid.
     */
    private String getTemplateLibName( IMCPoolInterface billref, int meta_id ) {
        String libName = billref.sqlProcedureStr( "B_GetTemplateLib", new String[]{"" + meta_id} );
        if ( libName == null ) {
            libName = "original";
        }
        libName += "/";
        return libName;

    } // End of getTemplateLibName



    //************************ END GETEXTERNAL TEMPLATE FUNCTIONS ***************

    /**
     * SendHtml. Generates the html page to the browser. Uses the templatefolder
     * by taking the metaid from the request object to determind the templatefolder.
     * Will by default handle maximum 3 servletadresses.
     */

    void sendHtml( HttpServletRequest req, HttpServletResponse res,
                   VariableManager vm, String htmlFile ) throws IOException {
        imcode.server.user.UserDomainObject user = getUserObj( req, res );

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
     * Log function. Logs the message to the log file and console
     */

    public void log( String msg ) {
        super.log( msg );

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

    boolean prepareUserForBillBoard( HttpServletRequest req, HttpServletResponse res,
                                     MetaInfo.Parameters params, String loginUserId ) throws IOException {

        // Lets get the user object
        UserDomainObject user = this.getUserObj( req, res );
        if ( user == null ) return false;

        // Lets get serverinformation
        IMCPoolInterface billref = ApplicationServer.getIMCPoolInterface();

        // Lets store some values in his session object
        HttpSession session = req.getSession( false );
        if ( session != null ) {
            setBillBoardSessionAttributes( session, params );
            session.setAttribute( "BillBoard.viewedDiscList", new Properties() );//Conference.viewedDiscList
            session.setAttribute( "BillBoard.user_id", loginUserId );//Conference.user_id
            session.setAttribute( "BillBoard.disc_index", "0" );

            // Ok, we need to catch a forum_id. Lets get the first one for this meta_id.
            // if not a forumid exists, the sp will return -1
            String aSectionId = billref.sqlProcedureStr( "B_GetFirstSection", new String[]{"" + params.getMetaId()} );
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
     * Gives the folder where All the html templates for a language are located.
     * This method will call its helper method getTemplateLibName to get the
     * name of the folder which contains the templates for a certain meta id
     */

    String getExternalImageFolder( HttpServletRequest req ) throws IOException {
        int metaId = this.getMetaId( req );

        // Lets get serverinformation
        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();
        IMCPoolInterface billref = ApplicationServer.getIMCPoolInterface();

        UserDomainObject user = Utility.getLoggedOnUser( req );
        String extFolder = RmiConf.getExternalImageFolder( imcref, metaId, user.getLangPrefix());
        extFolder += this.getTemplateLibName( billref, metaId );

        return extFolder;
    }

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
        int metaId = getMetaId( req );

        //log("before getAdminRights") ;
        //lets generat adminbutton if user has administrator rights and rights to edit
        if ( userHasAdminRights( imcref, metaId, user ) ) {

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
        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();

        String unAdminLink = "&nbsp;";
        int metaId = getMetaId( req );

        //lets generat unadminbutton if user has administrator rights and rights to edit
        if ( userHasAdminRights( imcref, metaId, user ) ) {
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

        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();

        HttpSession session = req.getSession( true );

        //lets get if user authorized or not
        boolean authorized;
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
     *
     * @param req    is used for collecting serverinfo and session
     * @param res    is used if error (send user to conference_starturl )
     * @param metaId conference metaId
     * @param user
     */
    boolean isUserAuthorized( HttpServletRequest req, HttpServletResponse res, int metaId, imcode.server.user.UserDomainObject user )
            throws IOException {

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
                                imcode.server.user.UserDomainObject user ) throws java.io.IOException {

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

    void setBillBoardSessionAttributes( HttpSession session, MetaInfo.Parameters params ) {
        session.setAttribute( "BillBoard.meta_id", "" + params.getMetaId() );
        session.setAttribute( "BillBoard.parent_meta_id", "" + params.getParentMetaId() );
    }

} // End class
