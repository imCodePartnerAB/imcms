package com.imcode.imcms.servlet.billboard;

import imcode.external.diverse.*;
import imcode.server.IMCServiceInterface;
import imcode.server.ApplicationServer;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.Vector;

/**
 * Html template in use:
 * BillBoard_Section_Unadmin_Link.htm
 * BillBoard_Section_Template1_Unadmin_Link.htm
 * BillBoard_Section_Template2_Unadmin_Link.htm
 * BillBoard_Disc_Unadmin_Link.htm
 * BillBoard_Reply_Unadmin_Link.htm
 * <p/>
 * Html parstags in use:
 * #EXTERNAL_PATH#
 * #UNADMIN_LINK_HTML#
 * #TEMPLATE_LIST#
 * #A_META_ID#
 * #CURRENT_TEMPLATE_SET#
 * #SECTION_LIST#
 * #NBR_OF_DISCS_TO_SHOW_LIST#
 * #NBR_OF_DAYS_TO_SHOW_LIST#
 * #NEW_A_HREF_LIST#
 * #REPLY_ID#
 * #REPLY_HEADER#
 * #REPLY_TEXT#
 * #REPLY_COUNT#
 * #REPLY_EMAIL#
 * #REPLY_DATE#
 * #REPLY_IPADR#
 * #REPLIES_RECORDS#
 * #DISC_DEL_ID#
 * #HEADLINE#
 * #COUNT_REPLIES#
 * #ARCHIVE_DATE#
 * <p/>
 * stored procedures in use:
 * B_AddNewSection
 * B_DeleteBill
 * B_DeleteSection
 * B_UpdateBill
 * B_FindSectionName
 * B_GetAllTemplateLibs
 * B_GetAllSection
 * B_GetAllNbrOfDaysToShow
 * B_GetAllNbrOfDiscsToShow
 * B_GetAllBillsToShow
 * B_GetAllOldBills
 * B_GetAdminBill
 * B_GetSectionName
 * B_GetNbrOfDiscsToShow
 * B_GetTemplateIdFromName
 * B_RenameSection
 * B_SetNbrOfDiscsToShow
 * B_SetNbrOfDaysToShow
 * B_SetTemplateLib
 *
 * @author Rickard Larsson
 * @author Jerker Drottenmyr
 * @author REBUILD TO BillBoardAdmin BY Peter Östergren
 * @version 1.2 20 Aug 2001
 */

public class BillBoardAdmin extends BillBoard {//ConfAdmin

    private final static String FORUM_UNADMIN_LINK_TEMPLATE = "BillBoard_Section_Unadmin_Link.htm";//"Conf_Forum_Unadmin_Link.htm";
    private final static String FORUM_TEMPLATE1_UNADMIN_LINK_TEMPLATE = "BillBoard_Section_Template1_Unadmin_Link.htm";//"Conf_Forum_Template1_Unadmin_Link.htm";
    private final static String FORUM_TEMPLATE2_UNADMIN_LINK_TEMPLATE = "BillBoard_Section_Template2_Unadmin_Link.htm";//"Conf_Forum_Template2_Unadmin_Link.htm";
    private final static String DISC_UNADMIN_LINK_TEMPLATE = "BillBoard_Disc_Unadmin_Link.htm";//"Conf_Disc_Unadmin_Link.htm";
    private final static String REPLY_UNADMIN_LINK_TEMPLATE = "BillBoard_Reply_Unadmin_Link.htm";//"Conf_Reply_Unadmin_Link.htm";

    /**
     * DoPost
     */
    public void doPost( HttpServletRequest req, HttpServletResponse res )
            throws ServletException, IOException {

        // Lets get the user object

        imcode.server.user.UserDomainObject user = Utility.getLoggedOnUser( req );
        if ( user == null ) return;

        if ( !isUserAuthorized( req, res, user ) ) {
            return;
        }

        // Lets get the standard SESSION parameters
        Properties params = this.getStandardParameters( req );

        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();

        // Lets check that the user is an administrator
        if ( userHasAdminRights( imcref, Integer.parseInt( params.getProperty( "META_ID" ) ), user ) == false ) {
            String header = "BillBoardAdmin servlet. ";
            new BillBoardError( req, res, header, 6, user.getLanguageIso639_2(), user );
            return;
        }

        // ******** SHOW ARCHIVES BILLS *********
        if ( req.getParameter( "ADMIN_OLD_BILLS" ) != null ) {
            res.sendRedirect( "BillBoardAdmin?ADMIN_TYPE=OLD_DISCUSSION" );
            return;
        }
        if ( req.getParameter( "ADMIN_NEW_BILLS" ) != null ) {
            res.sendRedirect( "BillBoardAdmin?ADMIN_TYPE=DISCUSSION" );
            return;
        }


        // ********* REGISTER NEW TEMPLATE ********
        if ( req.getParameter( "REGISTER_TEMPLATE_LIB" ) != null ) {
            // Lets get the new library name and validate it
            String newLibName = req.getParameter( "TEMPLATE_LIB_NAME" );
            if ( newLibName == null ) {
                String header = "BillBoardAdmin servlet. ";
                new BillBoardError( req, res, header, 80, user.getLanguageIso639_2(), user );
                return;
            }
            newLibName = super.verifySqlText( newLibName );

            // Lets check if we already have a templateset with that name
            String libNameExists = imcref.sqlProcedureStr( "B_FindTemplateLib", new String[]{newLibName} );
            if ( !libNameExists.equalsIgnoreCase( "-1" ) ) {
                String header = "BillBoardAdmin servlet. ";
                new BillBoardError( req, res, header, 84, user.getLanguageIso639_2(), user );
                return;
            }

            imcref.sqlUpdateProcedure( "B_AddTemplateLib", new String[]{newLibName} );

            // Lets copy the original folders to the new foldernames
            int metaId = getMetaId( req );
            FileManager fileObj = new FileManager();

            File templateSrc = new File( imcref.getExternalTemplateFolder( metaId, user), "original" );
            File imageSrc = new File( RmiConf.getImagePathForExternalDocument( imcref, metaId, user), "original" );
            File templateTarget = new File( imcref.getExternalTemplateFolder( metaId, user), newLibName );
            File imageTarget = new File( RmiConf.getImagePathForExternalDocument( imcref, metaId, user), newLibName );

            fileObj.copyDirectory( templateSrc, templateTarget );
            fileObj.copyDirectory( imageSrc, imageTarget );

            res.sendRedirect( "BillBoardAdmin?ADMIN_TYPE=META" );
            return;
        }

        // ********* PREPARE ADMIN TEMPLATES ********
        if ( req.getParameter( "UPLOAD_CONF" ) != null ) {

            String libName = ( req.getParameter( "TEMPLATE_NAME" ) == null ) ? "" : ( req.getParameter( "TEMPLATE_NAME" ) );
            String uploadType = ( req.getParameter( "UPLOAD_TYPE" ) == null ) ? "" : ( req.getParameter( "UPLOAD_TYPE" ) );

            params.setProperty( "TEMPLATE_NAME", libName );
            params.setProperty( "UPLOAD_TYPE", uploadType );

            String url = "BillBoardAdmin?ADMIN_TYPE=META";
            url += "&setname=" + libName + "&UPLOAD_TYPE=" + uploadType;
            res.sendRedirect( url );
            return;
        }

        // ********* SET TEMPLATE LIB FOR A CONFERENCE  ********
        if ( req.getParameter( "SET_TEMPLATE_LIB" ) != null ) {
            // Lets get the new library name and validate it
            String newLibName = req.getParameter( "TEMPLATE_ID" );
            if ( newLibName == null ) {
                String header = "BillBoardAdmin servlet. ";
                new BillBoardError( req, res, header, 80, user.getLanguageIso639_2(), user );
                return;
            }

            // Lets find the selected template in the database and get its id
            // if not found, -1 will be returned
            String templateId = imcref.sqlProcedureStr( "B_GetTemplateIdFromName", new String[]{newLibName} );
            if ( templateId.equalsIgnoreCase( "-1" ) ) {
                String header = "BillBoardAdmin servlet. ";
                new BillBoardError( req, res, header, 81, user.getLanguageIso639_2(), user );
                return;
            }
            // Ok, lets update the conference with this new templateset.
            imcref.sqlUpdateProcedure( "B_SetTemplateLib", new String[]{params.getProperty( "META_ID" ), newLibName} );

            res.sendRedirect( "BillBoardAdmin?ADMIN_TYPE=META" );
            return;
        } // SET TEMPLATE LIB

        // ********* DELETE REPLY ********  Peter says OK!!!!
        if ( req.getParameter( "DELETE_REPLY" ) != null ) {

            // Lets get all the replies id:s
            String repliesId = req.getParameter( "bill_id" );

            // Lets delete all marked replies. Observe that the first one wont be deleted!
            // if the user wants to delete the first one then he has to delete the discussion
            if ( repliesId != null ) {
                imcref.sqlUpdateProcedure( "B_DeleteBill", new String[]{repliesId} );
            }

            //***
            HttpSession session = req.getSession( false );
            String aSectionId = (String)session.getAttribute( "BillBoard.section_id" );

            String aDiscId = imcref.sqlProcedureStr( "B_GetLastDiscussionId", new String[]{params.getProperty( "META_ID" ), aSectionId} );

            session.setAttribute( "BillBoard.disc_id", aDiscId );
            String param = "";
            if ( !aDiscId.equals( "-1" ) ) {
                param = "?DISC_ID=" + aDiscId;
            }

            res.sendRedirect( "BillBoardDiscView" + param );
            return;
        }

        // ********* RESAVE REPLY ********  Peter says OK!!!!
        if ( req.getParameter( "RESAVE_REPLY" ) != null ) {

            // Lets get all the replies id:s
            String updateId = req.getParameter( "bill_id" );


            // Lets get the seleted textboxes headers and texts values.
            if ( updateId != null ) {
                String newText = req.getParameter( "TEXT_BOX" );
                if ( newText.equals( "" ) || newText == null ) {
                    BillBoardError err = new BillBoardError();
                    newText = err.getErrorMessage( req, 70 );
                }

                String newHeader = req.getParameter( "REPLY_HEADER" );
                if ( newHeader.equals( "" ) || newHeader == null ) {
                    BillBoardError err = new BillBoardError();
                    newHeader = err.getErrorMessage( req, 71 );
                }

                String newEmail = req.getParameter( "EPOST" );
                if ( newEmail.equals( "" ) || newEmail == null ) {
                    BillBoardError err = new BillBoardError();
                    newEmail = err.getErrorMessage( req, 74 );
                }

                // Lets validate the new text for the sql question
                newHeader = super.verifySqlText( newHeader );
                newText = super.verifySqlText( newText );

                imcref.sqlUpdateProcedure( "B_UpdateBill", new String[]{updateId, newHeader, newText, newEmail} );
            }
            res.sendRedirect( "BillBoardDiscView" );
            return;
        }

        // ********* DELETE DISCUSSION ********   Peter says OK!!!!
        if ( req.getParameter( "DELETE_DISCUSSION" ) != null ) {

            // Lets get all the discussion id:s
            String[] discIds = this.getDelDiscParameters( req );

            // Lets delete all the bills and all the replies to that bill.
            if ( discIds != null ) {

                for ( int i = 0; i < discIds.length; i++ ) {
                    imcref.sqlUpdateProcedure( "B_DeleteBill", new String[]{discIds[i]} );
                }
            }
            res.sendRedirect( "BillBoardAdmin?ADMIN_TYPE=DISCUSSION" );
            return;
        }

        // ********* MOVE BILLS TO ANITHER SECTION ******** Peter says OK!!!!
        if ( req.getParameter( "MOVE_BILLS" ) != null ) {
            String redirectParam = req.getParameter( "BILLTYPES" );
            if ( redirectParam.equalsIgnoreCase( "OLD_ONES" ) ) {
                redirectParam = "OLD_DISCUSSION";
            } else {
                redirectParam = "DISCUSSION";
            }
            // Lets get the section_id and set our session object before updating
            String aSectionId = params.getProperty( "SECTION_ID" );

            String[] discIds = this.getDelDiscParameters( req );
            if ( discIds == null ) {
                discIds = new String[0];
            }

            String moveToId = req.getParameter( "MOVE_TO_SECTION" ) == null ? aSectionId : req.getParameter( "MOVE_TO_SECTION" );

            //Lets move all the bills to the section admin wants
            for ( int i = 0; i < discIds.length; i++ ) {
                imcref.sqlUpdateProcedure( "B_ChangeSection", new String[]{discIds[i] + ", " + moveToId} );
            }

            //Lets update the session in case we moved the shown bill
            HttpSession session = req.getSession( false );
            String aDiscId = imcref.sqlProcedureStr( "B_GetLastDiscussionId", new String[]{params.getProperty( "META_ID" ), aSectionId} );
            session.setAttribute( "BillBoard.disc_id", aDiscId );

            //ok lets rebuild the page
            res.sendRedirect( "BillBoardAdmin?ADMIN_TYPE=" + redirectParam );
            return;

        }

        // ********* MOVE A BILL TO ANITHER SECTION ******** Peter says OK!!!!
        if ( req.getParameter( "MOVE_A_BILL" ) != null ) {
            // Lets get the section_id and set our session object before updating
            String aSectionId = params.getProperty( "SECTION_ID" );

            //lets get the bill_id
            String repliesId = req.getParameter( "bill_id" );

            String moveToId = req.getParameter( "MOVE_TO_SECTION" ) == null ? aSectionId : req.getParameter( "MOVE_TO_SECTION" );

            //Lets move all the bills to the section admin wants

            imcref.sqlUpdateProcedure( "B_ChangeSection", new String[]{repliesId, moveToId} );


            //Lets update the session in case we moved the shown bill
            HttpSession session = req.getSession( false );
            String sqlStr = "B_GetLastDiscussionId";
            String aDiscId = imcref.sqlProcedureStr( sqlStr, new String[]{params.getProperty( "META_ID" ), aSectionId} );
            session.setAttribute( "BillBoard.disc_id", aDiscId );

            //ok lets rebuild the page
            res.sendRedirect( "BillBoardDiscView" );
            return;

        }


        // ********* DELETE SECTION ********	Peter says OK!!!!
        if ( req.getParameter( "DELETE_SECTION" ) != null ) {

            params = this.getDelSectionParameters( req, params );
            // Lets get the section_id and set our session object before updating
            String aSectionId = params.getProperty( "SECTION_ID" );

            // Lets get all discussions for that setion and delete those before deleting the section
            // B_GetAllBillsInSection @aSectionId int
            String[] discs = imcref.sqlProcedure( "B_GetAllBillsInSection", new String[]{aSectionId} );
            if ( discs != null ) {
                for ( int i = 0; i < discs.length; i++ ) {
                    imcref.sqlUpdateProcedure( "B_DeleteBill", new String[]{discs[i]} );
                }
            }

            // B_DeleteSection @aSectionId int
            imcref.sqlUpdateProcedure( "B_DeleteSection", new String[]{params.getProperty( "SECTION_ID" )} );

            //ok lets update the session incase we deleted the current one
            String first = imcref.sqlProcedureStr( "B_GetFirstSection", new String[]{params.getProperty( "META_ID" )} );
            HttpSession session = req.getSession( false );
            session.setAttribute( "BillBoard.section_id", first );

            this.doGet( req, res );
            return;
        }

        // ********* ADD SECTION ********		Peter says OK!!!!
        if ( req.getParameter( "ADD_SECTION" ) != null ) {
            // Lets get addForum parameters
            params = this.getAddSectionParameters( req, params );
            // Lets verify the parameters for the sql questions.
            params = super.verifyForSql( params );

            // Lets check if a forum with that name exists

            String foundIt = imcref.sqlProcedureStr( "B_FindSectionName", new String[]{params.getProperty( "META_ID" ), params.getProperty( "NEW_SECTION_NAME" )} );

            if ( !foundIt.equalsIgnoreCase( "-1" ) ) {
                String header = "BillBoardAdmin servlet. ";
                new BillBoardError( req, res, header, 85, user.getLanguageIso639_2(), user );
                return;
            }

            final String archiveMode = "A";
            final String discussionsToShow = "30";
            final String daysToShow = "14";
            imcref.sqlUpdateProcedure( "B_AddNewSection", new String[]{params.getProperty( "META_ID" ),
                                                                        params.getProperty( "NEW_SECTION_NAME" ), archiveMode, discussionsToShow, daysToShow} );

            this.doGet( req, res );
            return;
        }

        // ********* CHANGE SECTION NAME ********  Peter says OK!!!!
        if ( req.getParameter( "CHANGE_SECTION_NAME" ) != null ) {
            // Lets get addForum parameters
            params = this.getRenameSectionParameters( req, params );
            // Lets verify the parameters for the sql questions.
            params = super.verifyForSql( params );

            // Lets check if a forum with that name exists

            String foundIt = imcref.sqlProcedureStr( "B_FindSectionName", new String[]{params.getProperty( "META_ID" ), params.getProperty( "NEW_SECTION_NAME" )} );

            if ( !foundIt.equalsIgnoreCase( "-1" ) ) {
                String header = "BillBoardAdmin servlet. ";
                new BillBoardError( req, res, header, 85, user.getLanguageIso639_2(), user );
                return;
            }

            imcref.sqlUpdateProcedure( "B_RenameSection", new String[]{params.getProperty( "SECTION_ID" ), params.getProperty( "NEW_SECTION_NAME" )} );
            this.doGet( req, res );
            return;
        }

        // ********* CHANGE SUBJECT STRING ********
        if ( req.getParameter( "CHANGE_SUBJECT_NAME" ) != null ) {
            // Lets get the meta_id and the new subject string
            String meta_id = req.getParameter( "meta_id" );
            String new_subject = req.getParameter( "NEW_SUBJECT_NAME" );
            if ( meta_id == null || new_subject == null ) {
                return;
            }

            String sqlSubjAddQ = "B_SetNewSubjectString";

            imcref.sqlUpdateProcedure( sqlSubjAddQ, new String[]{meta_id, new_subject} );
            res.sendRedirect( "BillBoardAdmin?ADMIN_TYPE=SECTION" );
            return;
        }

        // ********* SET SHOW_DISCUSSION_COUNTER ******** Peter says OK!!!!
        if ( req.getParameter( "SHOW_DISCUSSION_NBR" ) != null ) {
            // Lets get addForum parameters
            params = this.getShowDiscussionNbrParameters( req, params );

            imcref.sqlUpdateProcedure( "B_SetNbrOfDiscsToShow", new String[]{params.getProperty( "SECTION_ID" ), params.getProperty( "NBR_OF_DISCS_TO_SHOW" )} );
            res.sendRedirect( "BillBoardAdmin?ADMIN_TYPE=SECTION" );
            return;
        }
        // ********* SET SHOW NUMBERS OF DAYS ********	Peter says OK!!!!
        if ( req.getParameter( "SHOW_DISCUSSION_DAYS" ) != null ) {
            // Lets get addForum parameters
            params = this.getShowDiscussionDaysParameters( req, params );

            imcref.sqlUpdateProcedure( "B_SetNbrOfDaysToShow", new String[]{params.getProperty( "SECTION_ID" ), params.getProperty( "NBR_OF_DAYS_TO_SHOW" )} );
            res.sendRedirect( "BillBoardAdmin?ADMIN_TYPE=SECTION" );
            return;
        }

    } // DoPost

    /**
     * doGet
     */
    public void doGet( HttpServletRequest req, HttpServletResponse res )
            throws ServletException, IOException {

        // Lets get the standard SESSION parameters
        Properties params = this.getStandardParameters( req );

        // Lets get the user object

        imcode.server.user.UserDomainObject user = Utility.getLoggedOnUser( req );
        if ( user == null ) return;

        if ( !isUserAuthorized( req, res, user ) ) {
            return;
        }

        // Lets get serverinformation

        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();

        // Lets check that the user is an administrator
        if ( super.userHasAdminRights( imcref, Integer.parseInt( params.getProperty( "META_ID" ) ), user ) == false ) {
            String header = "BillBoardAdmin servlet. ";
            new BillBoardError( req, res, header, 6, user.getLanguageIso639_2(), user );
            log( "nu small det i BillBoardAdmin doGet super.getAdminRights" );
            return;
        }

        // Lets get the admintype from the requestobject
        String adminWhat = ( req.getParameter( "ADMIN_TYPE" ) == null ) ? "" : ( req.getParameter( "ADMIN_TYPE" ) );
        //	log("GET: AdminType=" + adminWhat) ;

        VariableManager vm = new VariableManager();
        String htmlFile = "";


        // *********** ADMIN META *************
        if ( adminWhat.equalsIgnoreCase( "META" ) ) {

            // Lets check which page we should show, the standard meta page or
            // the upload image/template page

            String setName = req.getParameter( "setname" );
            if ( setName != null ) {

                String uploadType = req.getParameter( "UPLOAD_TYPE" );
                if ( uploadType == null ) {
                    String header = "BillBoardAdmin servlet. ";
                    new BillBoardError( req, res, header, 83, user.getLanguageIso639_2(), user );
                    return;
                }

                // Ok, Lets get root path to the external type
                String metaId = params.getProperty( "META_ID" );

                // Lets build the Responsepage
                vm.addProperty( "UPLOAD_TYPE", uploadType );
                vm.addProperty( "FOLDER_NAME", setName );
                vm.addProperty( "META_ID", metaId );
                vm.addProperty( "UNADMIN_LINK_HTML", FORUM_TEMPLATE2_UNADMIN_LINK_TEMPLATE );

                htmlFile = "BillBoard_admin_template2.htm";

                // Ok, were gonna show our standard meta page
            } else {

                // Lets get the current template set for this metaid
                String currTemplateSet = imcref.sqlProcedureStr( "B_GetTemplateLib", new String[]{params.getProperty( "META_ID" )} );

                // Lets get all current template sets
                String[] sqlAnswer = imcref.sqlProcedure( "B_GetAllTemplateLibs", new String[]{} );
                Vector templateV = super.convert2Vector( sqlAnswer );

                // Lets fill the select box	with forums
                String templateList = Html.createOptionList( "", templateV );

                // Lets build the Responsepage
                //VariableManager vm = new VariableManager() ;
                vm.addProperty( "TEMPLATE_LIST", templateList );
                vm.addProperty( "A_META_ID", params.getProperty( "META_ID" ) );
                vm.addProperty( "CURRENT_TEMPLATE_SET", currTemplateSet );
                vm.addProperty( "UNADMIN_LINK_HTML", FORUM_TEMPLATE1_UNADMIN_LINK_TEMPLATE );

                htmlFile = "BillBoard_admin_template1.htm" ;
            }
        }

        // *********** ADMIN SECTION ************* Peter says ok!!!!
        if ( adminWhat.equalsIgnoreCase( "SECTION" ) ) {//FORUM

            // Lets get the information from DB
            String[] sqlAnswer = imcref.sqlProcedure( "B_GetAllSection", new String[]{params.getProperty( "META_ID" )} );
            Vector sectionV = super.convert2Vector( sqlAnswer );

            // Lets fill the select box with forums
            String forumList = Html.createOptionList( "", sectionV );

            //lets get all the daysnumber values
            String[] sqlAllDays = imcref.sqlProcedure( "B_GetAllNbrOfDaysToShow", new String[]{params.getProperty( "META_ID" )} );

            //lets get the startstring of the mail subject
            String subject_name = imcref.sqlProcedureStr( "B_GetStartSubjectString", new String[]{params.getProperty( "META_ID" )} );
            if ( subject_name == null ) subject_name = "";

            Vector sqlAllDaysV = new Vector();
            if ( sqlAllDays != null ) {
                sqlAllDaysV = super.convert2Vector( sqlAllDays );
            }
            String daysToShowList = Html.createOptionList( "", sqlAllDaysV );

            // Lets get all the showDiscs values
            String[] sqlAllDiscs = imcref.sqlProcedure( "B_GetAllNbrOfDiscsToShow", new String[]{params.getProperty( "META_ID" )} );

            Vector sqlAllDiscsV = new Vector();
            if ( sqlAllDiscs != null ) {
                sqlAllDiscsV = super.convert2Vector( sqlAllDiscs );
            }
            String discToShowList = Html.createOptionList( "", sqlAllDiscsV );

            // Lets build the Responsepage
            //VariableManager vm = new VariableManager() ;
            vm.addProperty( "SUBJECT_NAME", subject_name );
            vm.addProperty( "META_ID", params.getProperty( "META_ID" ) );
            vm.addProperty( "SECTION_LIST", forumList );//FORUM_LIST", forumList
            vm.addProperty( "NBR_OF_DISCS_TO_SHOW_LIST", discToShowList );
            vm.addProperty( "NBR_OF_DAYS_TO_SHOW_LIST", daysToShowList );
            vm.addProperty( "UNADMIN_LINK_HTML", FORUM_UNADMIN_LINK_TEMPLATE );
            //this.sendHtml(req,res,vm, HTML_TEMPLATE) ;
            htmlFile = "BillBoard_admin_section.htm";
            //return ;
        }

        // *********** ADMIN DISCUSSION *************
        if ( adminWhat.equalsIgnoreCase( "DISCUSSION" ) ) {
            String adminDiscList = "BillBoard_admin_disc_list.htm";//Conf_admin_disc_list.htm

            // Lets get parameters
            String aMetaId = params.getProperty( "META_ID" );
            String aSectionId = params.getProperty( "SECTION_ID" );

            // Lets get the part of an html page, wich will be parsed for every a Href reference
            File aHrefHtmlFile = new File( super.getExternalTemplateFolder( req ), adminDiscList );

            // Lets get all New Discussions

            String[][] sqlAnswerNew = imcref.sqlProcedureMulti( "B_GetAllBillsToShow", new String[]{aMetaId, aSectionId} );

            //lets get all the sections and the code for the selectlist
            String[] sqlSections = imcref.sqlProcedure( "B_GetAllSection", new String[]{aMetaId} );
            Vector sectionV = super.convert2Vector( sqlSections );
            String sectionListStr = Html.createOptionList( aSectionId, sectionV );

            // Lets build our tags vector.
            Vector tagsV = this.buildAdminTags();

            // Lets preparse all NEW records
            String allNewRecs = "";
            if ( sqlAnswerNew != null ) {
                if ( sqlAnswerNew.length > 0 )
                    allNewRecs = discPreParse( sqlAnswerNew, tagsV, aHrefHtmlFile );
            }

            // Lets build the Responsepage

            vm.addProperty( "SECTION_LIST", sectionListStr );
            vm.addProperty( "NEW_A_HREF_LIST", allNewRecs );
            vm.addProperty( "UNADMIN_LINK_HTML", DISC_UNADMIN_LINK_TEMPLATE );

            htmlFile = "BillBoard_admin_disc.htm";
        } // End admin discussion


        // *********** ADMIN OLD DISCUSSION *************
        if ( adminWhat.equalsIgnoreCase( "OLD_DISCUSSION" ) ) {
            String adminDiscList = "BillBoard_admin_disc_list.htm";//Conf_admin_disc_list.htm

            // Lets get parameters
            String aMetaId = params.getProperty( "META_ID" );
            String aSectionId = params.getProperty( "SECTION_ID" );

            // Lets get the part of an html page, wich will be parsed for every a Href reference
            File aHrefHtmlFile = new File( super.getExternalTemplateFolder( req ), adminDiscList );

            // Lets get all New Discussions

            String[][] sqlAnswerNew = imcref.sqlProcedureMulti( "B_GetAllOldBills", new String[]{aMetaId, aSectionId} );

            //lets get all the sections and the code for the selectlist
            String[] sqlSections = imcref.sqlProcedure( "B_GetAllSection", new String[]{aMetaId} );
            Vector sectionV = super.convert2Vector( sqlSections );
            String sectionListStr = Html.createOptionList( aSectionId, sectionV );

            // Lets build our tags vector.
            Vector tagsV = this.buildAdminTags();

            // Lets preparse all OLD records
            String allNewRecs = "";
            if ( sqlAnswerNew != null ) {
                if ( sqlAnswerNew.length > 0 )
                    allNewRecs = discPreParse( sqlAnswerNew, tagsV, aHrefHtmlFile );
            }


            // Lets build the Responsepage
            vm.addProperty( "SECTION_LIST", sectionListStr );
            vm.addProperty( "NEW_A_HREF_LIST", allNewRecs );
            vm.addProperty( "UNADMIN_LINK_HTML", DISC_UNADMIN_LINK_TEMPLATE );

            //this.sendHtml(req,res,vm, HTML_TEMPLATE) ;
            htmlFile = "BillBoard_admin_disc2.htm";
            //return ;
        } // End admin discussion



        // *********** ADMIN REPLIES *************
        if ( adminWhat.equalsIgnoreCase( "REPLY" ) ) {
            String adminReplyList = "BillBoard_admin_reply_list.htm";//Conf_admin_reply_list.htm
            log( "OK, Administrera replies" );

            // Lets get the replylist from DB
            String discId = params.getProperty( "DISC_ID" );

            String[][] sqlAnswer = imcref.sqlProcedureMulti( "B_GetAdminBill", new String[]{discId} );//GetAllRepliesInDiscAdmin

            // SYNTAX: date  first_name  last_name  headline   text reply_level
            // Lets build our variable list
            Vector tagsV = new Vector();
            tagsV.add( "#REPLY_ID#" );
            //tagsV.add("#REPLY_ID2#");
            tagsV.add( "#REPLY_HEADER#" );
            tagsV.add( "#REPLY_TEXT#" );
            tagsV.add( "#REPLY_COUNT#" );
            tagsV.add( "#REPLY_EMAIL#" );
            tagsV.add( "#REPLY_DATE#" );
            tagsV.add( "#REPLY_IPADR#" );


            // Lets get the part of an html page, wich will be parsed for every a Href reference
            File templateLib = super.getExternalTemplateFolder( req );
            File aSnippetFile = new File( templateLib, adminReplyList );

            //lets get all the sections and the code for the selectlist
            String[] sqlSections = imcref.sqlProcedure( "B_GetAllSection", new String[]{params.getProperty( "META_ID" )} );
            Vector sectionV = super.convert2Vector( sqlSections );
            String sectionListStr = Html.createOptionList( params.getProperty( "SECTION_ID" ), sectionV );

            // Lets preparse all records
            String allRecs = " ";
            if ( sqlAnswer != null ) allRecs = replyPreParse( sqlAnswer, tagsV, aSnippetFile );

            // Lets build the Responsepage
            vm.addProperty( "SECTION_LIST", sectionListStr );
            vm.addProperty( "REPLIES_RECORDS", allRecs );
            vm.addProperty( "UNADMIN_LINK_HTML", REPLY_UNADMIN_LINK_TEMPLATE );

            htmlFile = "BillBoard_admin_reply.htm";
        } // End admin Reply

        this.sendHtml( req, res, vm, htmlFile );

    } //DoGet


    // ****************** PARSE REPLIES FUNCTIONS ************************
    /**
     * Parses the Extended array with the htmlcode, which will be parsed
     * for all records in the array
     */
    private String replyPreParse( String[][] DBArr, Vector tagsV,
                                  File htmlCodeFile ) {

        return discPreParse( DBArr, tagsV, htmlCodeFile );
    }


    // ****************** END OF PARSE REPLIES FUNCTIONS ****************

    // ****************** PARSE DISCUSSIONS FUNCTIONS *******************
    /**
     * Parses the Extended array with the htmlcode, which will be parsed
     * for all records in the array
     * <p/>
     * (HttpServletRequest req, String[] DBArr, Vector tagsV,String htmlCodeFile)
     * <p/>
     * (HttpServletRequest req, String[] DBArr, Vector tagsV,String htmlCodeFile)
     * <p/>
     * (HttpServletRequest req, String[] DBArr, Vector tagsV,String htmlCodeFile)
     * <p/>
     * (HttpServletRequest req, String[] DBArr, Vector tagsV,String htmlCodeFile)
     * <p/>
     * (HttpServletRequest req, String[] DBArr, Vector tagsV,String htmlCodeFile)
     * <p/>
     * (HttpServletRequest req, String[] DBArr, Vector tagsV,String htmlCodeFile)
     */
    //(HttpServletRequest req, String[] DBArr, Vector tagsV,String htmlCodeFile)
    private String discPreParse( String[][] DBArr, Vector tagsV,
                                 File htmlCodeFile ) {

        String htmlStr = "";

        for ( int i = 0; i < DBArr.length; i++ ) {
            Vector dataV = new Vector();

            // Lets do for one record... Get all fields for that record
            for ( int j = 0; j < DBArr[i].length; j++ ) {
                dataV.add( DBArr[i][j] );
            }

            // Lets parse one record
            htmlStr += this.parseOneRecord( tagsV, dataV, htmlCodeFile );
        }

        return htmlStr;
    } // End of

    // ****************** END OF PARSE REPLIES FUNCTIONS ****************

    /**
     * Parses one record.
     */
    String parseOneRecord( Vector tagsV, Vector dataV, File htmlCodeFile ) {

        // Lets parse one aHref reference
        ParseServlet parser = new ParseServlet( htmlCodeFile, tagsV, dataV );
        String oneRecordsHtmlCode = parser.getHtmlDoc();
        return oneRecordsHtmlCode;
    } // End of parseOneRecord


    // ****************** GET PARAMETER FUNCTIONS ************************



    /**
     * Collects the parameters used for setting the number of days to show in a forum
     */

    private Properties getShowDiscussionDaysParameters( HttpServletRequest req, Properties params ) {
        // Lets check if we shall create a new properties

        if ( params == null ) params = new Properties();
        // Lets get the standard metainformation
        String newNbr = ( req.getParameter( "NBR_OF_DAYS_TO_SHOW" ) == null ) ? "" : ( req.getParameter( "NBR_OF_DAYS_TO_SHOW" ) );//NBR_OF_DISCS_TO_SHOW
        String sectionId = ( req.getParameter( "SECTION_ID" ) == null ) ? "" : ( req.getParameter( "SECTION_ID" ) );//FORUM_ID
        params.setProperty( "SECTION_ID", sectionId );
        params.setProperty( "NBR_OF_DAYS_TO_SHOW", newNbr );
        return params;
    }

    /**
     * Collects the parameters used for setting the number of discussions to show in a section. OBS Petsr says ok!!!
     */

    private Properties getShowDiscussionNbrParameters( HttpServletRequest req, Properties params ) {
        // Lets check if we shall create a new properties

        if ( params == null ) params = new Properties();
        // Lets get the standard metainformation
        String newNbr = ( req.getParameter( "NBR_OF_DISCS_TO_SHOW" ) == null ) ? "" : ( req.getParameter( "NBR_OF_DISCS_TO_SHOW" ) );//NBR_OF_DISCS_TO_SHOW
        String sectionId = ( req.getParameter( "SECTION_ID" ) == null ) ? "" : ( req.getParameter( "SECTION_ID" ) );//FORUM_ID
        params.setProperty( "SECTION_ID", sectionId );
        params.setProperty( "NBR_OF_DISCS_TO_SHOW", newNbr );
        return params;
    }

    /**
     * Collects the parameters used for adding a forum parametersfrom the SESSION object.
     */

    private Properties getAddSectionParameters( HttpServletRequest req, Properties params ) {
        // Lets check if we shall create a new properties

        if ( params == null ) params = new Properties();
        // Lets get the standard metainformation
        String newSectionName = ( req.getParameter( "NEW_SECTION_NAME" ) == null ) ? "" : ( req.getParameter( "NEW_SECTION_NAME" ) );//NEW_FORUM_NAME
        params.setProperty( "NEW_SECTION_NAME", newSectionName );
        return params;
    }

    /**
     * Collects the standard parameters from the SESSION object.
     */

    private Properties getRenameSectionParameters( HttpServletRequest req, Properties params ) {
        // Lets check if we shall create a new properties

        if ( params == null ) params = new Properties();
        // Lets get the standard metainformation
        String newConfName = ( req.getParameter( "NEW_SECTION_NAME" ) == null ) ? "" : ( req.getParameter( "NEW_SECTION_NAME" ) );
        String renForumId = ( req.getParameter( "SECTION_ID" ) == null ) ? "" : ( req.getParameter( "SECTION_ID" ) );
        params.setProperty( "NEW_SECTION_NAME", newConfName );
        params.setProperty( "SECTION_ID", renForumId );
        return params;
    }

    /**
     * Collects the parameters used to delete a forum
     */

    private Properties getDelSectionParameters( HttpServletRequest req, Properties params ) {
        // Lets check if we shall create a new properties

        if ( params == null ) params = new Properties();
        // Lets get the standard metainformation
        String forumId = ( req.getParameter( "SECTION_ID" ) == null ) ? "" : ( req.getParameter( "SECTION_ID" ) );
        params.setProperty( "SECTION_ID", forumId );
        return params;
    }

    /**
     * Collects the standard parameters from the SESSION object.
     */

    private String[] getDelDiscParameters( HttpServletRequest req ) {

        // Lets get the standard discussion_id to delete
        String[] discId = ( req.getParameterValues( "DISC_DEL_BOX" ) );//DISC_DEL_BOX
        return discId;
    }

    /**
     * Collects the standard parameters from the SESSION object.
     */

    private Properties getStandardParameters( HttpServletRequest req ) {

        // Lets get the standard metainformation
        Properties reqParams = MetaInfo.createPropertiesFromMetaInfoParameters( super.getBillBoardSessionParameters( req ) );

        // Lets get the session
        HttpSession session = req.getSession( false );
        if ( session != null ) {
            // Lets get the parameters we know we are supposed to get from the request object
            String sectionId = ( (String)session.getAttribute( "BillBoard.section_id" ) == null ) ? "" : ( (String)session.getAttribute( "BillBoard.section_id" ) );//Conference.forum_id
            String discId = ( (String)session.getAttribute( "BillBoard.disc_id" ) == null ) ? "" : ( (String)session.getAttribute( "BillBoard.disc_id" ) );//Conference.disc_id
            //			String lastLogindate = (	(String) session.getAttribute("BillBoard.last_login_date")==null) ? "" : ((String) session.getAttribute("BillBoard.last_login_date")) ;//Conference.last_login_date"
            //			reqParams.setProperty("LAST_LOGIN_DATE", lastLogindate) ;
            reqParams.setProperty( "SECTION_ID", sectionId );
            reqParams.setProperty( "DISC_ID", discId );
        }
        return reqParams;
    }

    // ******************	END GET PARAMETER FUNCTIONS *******************

    /**
     * Builds the tagvector used for parse one record.
     */
    private Vector buildAdminTags() {

        // Lets build our tags vector.
        Vector tagsV = new Vector();
        tagsV.add( "#DISC_DEL_ID#" );
        tagsV.add( "#HEADLINE#" );
        tagsV.add( "#COUNT_REPLIES#" );
        tagsV.add( "#ARCHIVE_DATE#" );

        return tagsV;
    } // End of buildstags

    /**
     * Log function, will work for both servletexec and Apache
     */

    public void log( String msg ) {
        super.log( "BillBoardAdmin: " + msg );
    }

} // End of class
