package com.imcode.imcms.servlet.billboard;

import imcode.external.diverse.*;
import imcode.server.ApplicationServer;
import imcode.server.IMCServiceInterface;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentMapper;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;
import java.util.List;

public class BillBoard extends HttpServlet {

    private final static String ADMIN_BUTTON_TEMPLATE = "billboard_admin_button.htm";
    private final static String UNADMIN_BUTTON_TEMPLATE = "billboard_unadmin_button.htm";

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
     * Collects the standard parameters from the session object
     */

    MetaInfo.Parameters getBillBoardSessionParameters( HttpServletRequest req ) {

        // Get the session
        HttpSession session = req.getSession( true );
        String metaIdStr = ( (String)session.getAttribute( "BillBoard.meta_id" ) == null )
                           ? "" : ( (String)session.getAttribute( "BillBoard.meta_id" ) );//Conference.meta_id

        int metaId = Integer.parseInt( metaIdStr );

        return new MetaInfo.Parameters( metaId );
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
        String sectionId = ( (String)session.getAttribute( "BillBoard.section_id" ) == null )
                           ? "" : ( (String)session.getAttribute( "BillBoard.section_id" ) );//"Conference.forum_id"
        String discId = ( (String)session.getAttribute( "BillBoard.disc_id" ) == null )
                        ? "" : ( (String)session.getAttribute( "BillBoard.disc_id" ) );//"Conference.disc_id"

        if ( params == null ) {
            params = new Properties();
        }
        params.setProperty( "SECTION_ID", sectionId );
        params.setProperty( "DISC_ID", discId );
    }

    /**
     * Gives the folder to the root external folder
     */

    File getExternalTemplateRootFolder( HttpServletRequest req ) {

        UserDomainObject user = Utility.getLoggedOnUser( req );

        // Lets get serverinformation
        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();

        int metaId = this.getMetaId( req );
        return imcref.getExternalTemplateFolder( metaId, user );
    }

    /**
     * Gives the folder where All the html templates for a language are located.
     * This method will call its helper method getTemplateSetDirectoryName to get the
     * name of the folder which contains the templates for a certain meta id
     */

    File getExternalTemplateFolder( HttpServletRequest req )//p ok
    {
        int metaId = this.getMetaId( req );
        // Lets get serverinformation
        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();
        UserDomainObject user = Utility.getLoggedOnUser( req );
        return new File( imcref.getExternalTemplateFolder( metaId, user ), this.getTemplateLibName( imcref, metaId ) );
    }

    /**
     * Returns the foldername where the templates are situated for a certain metaid.
     */
    private String getTemplateLibName( IMCServiceInterface imcref, int meta_id ) {
        String libName = imcref.sqlProcedureStr( "B_GetTemplateLib", new String[]{"" + meta_id} );
        if ( libName == null ) {
            libName = "original";
        }
        libName += "/";
        return libName;

    } // End of getTemplateSetDirectoryName



    //************************ END GETEXTERNAL TEMPLATE FUNCTIONS ***************

    /**
     * SendHtml. Generates the html page to the browser. Uses the templatefolder
     * by taking the metaid from the request object to determind the templatefolder.
     * Will by default handle maximum 3 servletadresses.
     */

    void sendHtml( HttpServletRequest req, HttpServletResponse res,
                   VariableManager vm, String htmlFile ) throws IOException {

        imcode.server.user.UserDomainObject user = Utility.getLoggedOnUser( req );

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

        String html = getTemplate( htmlFile, user, vm.getTagsAndData() );

        // Lets send settings to a browser
        PrintWriter out = res.getWriter();
        Utility.setDefaultHtmlContentType( res );
        out.println(html);

    }

    /**
     * Converts array to vector
     */

    Vector convert2Vector( String[] arr ) {
        Vector rolesV = new Vector();
        for ( int i = 0; i < arr.length; i++ ) {
            rolesV.add( arr[i] );
        }
        return rolesV;
    }

    /**
     * Prepare user for the conference
     */

    boolean prepareUserForBillBoard( HttpServletRequest req, HttpServletResponse res,
                                     MetaInfo.Parameters params, String loginUserId ) throws IOException {

        // Lets get the user object

        UserDomainObject user = Utility.getLoggedOnUser( req );
        if ( user == null ) {
            return false;
        }

        // Lets store some values in his session object
        HttpSession session = req.getSession( false );
        if ( session != null ) {
            setBillBoardSessionAttributes( session, params );
            session.setAttribute( "BillBoard.viewedDiscList", new Properties() );//Conference.viewedDiscList
            session.setAttribute( "BillBoard.user_id", loginUserId );//Conference.user_id
            session.setAttribute( "BillBoard.disc_index", "0" );

            // Ok, we need to catch a forum_id. Lets get the first one for this meta_id.
            // if not a forumid exists, the sp will return -1
            String aSectionId = ApplicationServer.getIMCServiceInterface().sqlProcedureStr( "B_GetFirstSection", new String[]{"" + params.getMetaId()} );
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
     * This method will call its helper method getTemplateSetDirectoryName to get the
     * name of the folder which contains the templates for a certain meta id
     */

    String getExternalImageFolder( HttpServletRequest req ) {
        int metaId = this.getMetaId( req );

        // Lets get serverinformation
        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();

        UserDomainObject user = Utility.getLoggedOnUser( req );
        String extFolder = "/imcms/" + user.getLanguageIso639_2() + "/images/"
                        + imcref.getDocType(metaId) + '/';
        extFolder += this.getTemplateLibName( imcref, metaId );

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

    private String getAdminButtonLink( HttpServletRequest req, imcode.server.user.UserDomainObject user,
                                       VariableManager adminButtonVM ) {
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

            String adminBtn = getTemplate( ADMIN_BUTTON_TEMPLATE, user, adminButtonVM.getTagsAndData() );

            //lets create adminlink
            adminLinkVM.addProperty( "ADMIN_BUTTON", adminBtn );
            if ( !adminLinkFile.equals( "" ) ) {
                adminLink = getTemplate( adminLinkFile, user, adminLinkVM.getTagsAndData() );
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
    private String getUnAdminButtonLink( HttpServletRequest req, imcode.server.user.UserDomainObject user,
                                         VariableManager unAdminButtonVM ) {
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

            String unAdminBtn = getTemplate( UNADMIN_BUTTON_TEMPLATE, user, unAdminButtonVM.getTagsAndData() );

            //lets create unadminlink
            unAdminLinkVM.addProperty( "UNADMIN_BUTTON", unAdminBtn );
            if ( !unAdminLinkFile.equals( "" ) ) {
                unAdminLink = getTemplate( unAdminLinkFile, user, unAdminLinkVM.getTagsAndData() );
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
    boolean isUserAuthorized( HttpServletRequest req, HttpServletResponse res,
                              imcode.server.user.UserDomainObject user )
            throws IOException {

        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();

        HttpSession session = req.getSession( true );

        //lets get if user authorized or not
        boolean authorized;
        String stringMetaId = (String)session.getAttribute( "BillBoard.meta_id" );//Conference.meta_id
        if ( stringMetaId == null ) {
            authorized = false;
            Utility.redirectToStartDocument( req, res );
        } else {
            int metaId = Integer.parseInt( stringMetaId );
            authorized = isUserAuthorized( res, metaId, user, req );
        }

        return authorized;
    }

    /**
     * checks if user is authorized
     *
     * @param res    is used if error (send user to conference_starturl )
     * @param metaId conference metaId
     * @param user
     */
    boolean isUserAuthorized( HttpServletResponse res, int metaId,
                              UserDomainObject user, HttpServletRequest req )
            throws IOException {

        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();

        //is user authorized?
        DocumentMapper documentMapper = imcref.getDocumentMapper();
        DocumentDomainObject document = documentMapper.getDocument( metaId );
        boolean authorized = documentMapper.userHasAtLeastDocumentReadPermission( user, document );

        //lets send unauthorized users out
        if ( !authorized ) {
            Utility.redirectToStartDocument( req, res );
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

        DocumentMapper documentMapper = imcref.getDocumentMapper();
        DocumentDomainObject document = documentMapper.getDocument( metaId );
        return ( documentMapper.userHasAtLeastDocumentReadPermission( user, document ) &&
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
    }

    String getTemplate(String template, UserDomainObject user, List tagsAndData ) {
        return ApplicationServer.getIMCServiceInterface().getTemplateFromSubDirectoryOfDirectory( template, user, tagsAndData, "104", "original" ) ;
    }

} // End class
