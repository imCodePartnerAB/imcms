package com.imcode.imcms.servlet.billboard;

import imcode.server.*;
import imcode.server.user.UserDomainObject;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import imcode.external.diverse.*;
import imcode.util.Utility;

/**
 * Html template in use:
 * BILLBOARD_CREATOR.HTM
 * <p/>
 * Html parstags in use:
 * #BILLBOARD_NAME#
 * #SECTION_NAME#
 * <p/>
 * stored procedures in use:
 * B_AddNewBillBoard
 * B_AddNewSection
 *
 * @author Rickard Larsson
 * @author Jerker Drottenmyr
 * @author REBUILD TO BillBoardCreator BY Peter Östergren
 * @version 1.2 20 Aug 2001
 */

public class BillBoardCreator extends BillBoard {//BillBoardCreator

    private final static String HTML_TEMPLATE = "billboard_creator.htm";

    /**
     * The POST method creates the html page when this side has been
     * redirected from somewhere else.
     */

    public void doPost( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {

        // Lets get the standard parameters and validate them
        Properties params = MetaInfo.createPropertiesFromMetaInfoParameters( super.getBillBoardSessionParameters( req ) );

        // Lets get the new conference parameters
        Properties confParams = this.getNewConfParameters( req );

        // Lets get an user object

        imcode.server.user.UserDomainObject user = Utility.getLoggedOnUser( req );
        if ( user == null ) return;

        if ( !isUserAuthorized( req, res, user ) ) {
            return;
        }

        String action = req.getParameter( "action" );
        if ( action == null ) {
            action = "";
            String header = "BillBoardCreator servlet. ";
            BillBoardError err = new BillBoardError( req, res, header, 3, user.getLanguageIso639_2(), user );
            log( header + err.getErrorMsg() );
            return;
        }

        // Lets get serverinformation

        ImcmsServices imcref = Imcms.getServices();

        // ********* NEW ********
        if ( action.equalsIgnoreCase( "ADD_BILLBOARD" ) ) {
            //log("OK, nu skapar vi anslagstavlan") ;

            // Added 000608
            // Ok, Since the billboard db can be used from different servers
            // we have to check when we add a new billboard that such an meta_id
            // doesnt already exists.
            String metaId = params.getProperty( "META_ID" );
            String foundMetaId = imcref.sqlProcedureStr( "B_FindMetaId", new String[]{metaId} );
            if ( !foundMetaId.equals( "1" ) ) {
                action = "";
                String header = "BillBoardCreator servlet. ";
                BillBoardError err = new BillBoardError( req, res, header, 90, user.getLanguageIso639_2(), user );
                log( header + err.getErrorMsg() );
                return;
            }

            // Lets add a new billboard to DB
            // AddNewConf @meta_id int, @billboardName varchar(255)

            String confName = confParams.getProperty( "BILLBOARD_NAME" );//BILLBOARD NAME

            String subject = confParams.getProperty( "SUBJECT_NAME" );

            imcref.sqlUpdateProcedure( "B_AddNewBillBoard", new String[]{metaId, confName, subject} );

            // Lets add a new section to the billBoard
            // B_AddNewSection @meta_id int, @section_name varchar(255), @archive_mode char, @archive_time int

            final String archiveMode = "A";
            final String archiveTime = "30";
            final String daysToShow = "14";
            imcref.sqlUpdateProcedure( "B_AddNewSection", new String[]{metaId, confParams.getProperty( "SECTION_NAME" ), archiveMode, archiveTime, daysToShow} );

            // Ok, Were done adding the billBoard, Lets go back to the Manager
            String loginPage = "BillBoardLogin?login_type=login";
            res.sendRedirect( loginPage );
            return;
        }

    } // End POST

    /**
     * The GET method creates the html page when this side has been
     * redirected from somewhere else.
     */

    public void doGet( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {

        // Lets get an user object

        imcode.server.user.UserDomainObject user = Utility.getLoggedOnUser( req );
        if ( user == null ) return;

        if ( !isUserAuthorized( req, res, user ) ) {
            return;
        }

        String action = req.getParameter( "action" );
        if ( action == null ) {
            action = "";
            String header = "BillBoardCreator servlet. ";
            BillBoardError err = new BillBoardError( req, res, header, 3, user.getLanguageIso639_2(), user );
            log( header + err.getErrorMsg() );
            return;
        }

        // ********* NEW ********
        if ( action.equalsIgnoreCase( "NEW" ) ) {
            // Lets build the Responsepage to the loginpage
            VariableManager vm = new VariableManager();
            vm.addProperty( "SERVLET_URL", "" );
            sendHtml( req, res, vm, HTML_TEMPLATE );
            return;
        }
    } // End doGet

    /**
     * Collects the parameters from the request object
     */

    private Properties getNewConfParameters( HttpServletRequest req ) {

        Properties confP = new Properties();
        String billBoard_name = ( req.getParameter( "billBoard_name" ) == null ) ? "" : ( req.getParameter( "billBoard_name" ) );//billboard_name
        String section_name = ( req.getParameter( "section_name" ) == null ) ? "" : ( req.getParameter( "section_name" ) );//section_name
        String subject_name = ( req.getParameter( "subject_name" ) == null ) ? "" : ( req.getParameter( "subject_name" ) );

        confP.setProperty( "BILLBOARD_NAME", billBoard_name.trim() );
        confP.setProperty( "SECTION_NAME", section_name.trim() );
        confP.setProperty( "SUBJECT_NAME", subject_name.trim() );

        //log("BillBoard paramters:" + confP.toString()) ;
        return confP;
    }

    /**
     * Log function, will work for both servletexec and Apache
     */

    public void log( String msg ) {
        super.log( "BillBoardCreator: " + msg );
    }

} // End class
