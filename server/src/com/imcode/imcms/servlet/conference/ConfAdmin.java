package com.imcode.imcms.servlet.conference;

import imcode.server.*;
import imcode.server.user.UserDomainObject;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import imcode.external.diverse.*;
import imcode.util.Utility;
import com.imcode.imcms.servlet.superadmin.Administrator;

public class ConfAdmin extends Conference {

    private final static String FORUM_UNADMIN_LINK_TEMPLATE = "Conf_Forum_Unadmin_Link.htm";
    private final static String FORUM_TEMPLATE1_UNADMIN_LINK_TEMPLATE = "Conf_Forum_Template1_Unadmin_Link.htm";
    private final static String FORUM_TEMPLATE2_UNADMIN_LINK_TEMPLATE = "Conf_Forum_Template2_Unadmin_Link.htm";
    private final static String DISC_UNADMIN_LINK_TEMPLATE = "Conf_Disc_Unadmin_Link.htm";
    private final static String REPLY_UNADMIN_LINK_TEMPLATE = "Conf_Reply_Unadmin_Link.htm";

    /**
     * DoPost
     */
    public void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        UserDomainObject user = Utility.getLoggedOnUser(req);

        if (!isUserAuthorized(req, res, user)) {
            return;
        }

        // Lets get the standard SESSION parameters
        Properties params = this.getStandardParameters(req);

        // Lets get serverinformation
        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();

        // Lets check that the user is an administrator
        if (super.userHasAdminRights(imcref, Integer.parseInt(params.getProperty("META_ID")), user) == false) {
            String header = "ConfAdmin servlet. ";
            new ConfError(req, res, header, 6);
            return;
        }

        // ********* ADD SELF_REGISTERED USERS ********
        if (req.getParameter("ADD_SELF_REG_ROLE") != null) {

            // Lets check if the user is a superadmin
            if (Administrator.checkAdminRights(req) == false) {
                new ConfError(req, res, "ConfAdmin", 64);
                return;
            }

            // Lets get the role id the user wants to add
            String selfRegRoleId = req.getParameter("ALL_SELF_REGISTER_ROLES");
            if (selfRegRoleId == null) {
                String header = "ConfAdmin servlet. ";
                new ConfError(req, res, header, 86);
                return;
            }

            String roleName = imcref.sqlProcedureStr("RoleGetName", new String[]{selfRegRoleId});

            imcref.sqlUpdateProcedure("A_SelfRegRoles_AddNew", new String[]{params.getProperty("META_ID"), selfRegRoleId, roleName});

            res.sendRedirect("ConfAdmin?ADMIN_TYPE=SELF_REGISTER");
            return;
        }

        // ********* DELETE A SELF_REGISTERED ROLE ********
        if (req.getParameter("DEL_SELF_REG_ROLE") != null) {

            // Lets check if the user is a superadmin
            if (Administrator.checkAdminRights(req) == false) {
                new ConfError(req, res, "ConfAdmin", 64);
                return;
            }


            // Lets get the role id the user wants to delete
            String selfRegRoleId = req.getParameter("CURR_SELF_REGISTER_ROLES");
            if (selfRegRoleId == null) {
                String header = "ConfAdmin servlet. ";
                new ConfError(req, res, header, 87);
                return;
            }

            imcref.sqlUpdateProcedure("A_SelfRegRoles_Delete", new String[]{params.getProperty("META_ID"), selfRegRoleId});
            res.sendRedirect("ConfAdmin?ADMIN_TYPE=SELF_REGISTER");
            return;
        }

        // ********* REGISTER NEW TEMPLATE ********
        if (req.getParameter("REGISTER_TEMPLATE_LIB") != null) {
            log("Nu lägger vi till ett nytt set");

            // Lets get the new library name and validate it
            String newLibName = req.getParameter("TEMPLATE_LIB_NAME");
            if (newLibName == null) {
                String header = "ConfAdmin servlet. ";
                new ConfError(req, res, header, 80);
                return;
            }
            newLibName = super.verifySqlText(newLibName);

            // Lets check if we already have a templateset with that name
            String libNameExists = imcref.sqlProcedureStr("A_FindTemplateLib", new String[]{newLibName});
            if (!libNameExists.equalsIgnoreCase("-1")) {
                String header = "ConfAdmin servlet. ";
                new ConfError(req, res, header, 84);
                return;
            }

            imcref.sqlUpdateProcedure("A_AddTemplateLib", new String[]{newLibName});

            // Lets copy the original folders to the new foldernames
            int metaId = getMetaId(req);
            FileManager fileObj = new FileManager();

            File templateSrc = new File(imcref.getExternalTemplateFolder(metaId, user), "original");
            File imageSrc = new File(RmiConf.getImagePathForExternalDocument(imcref, metaId, user), "original");
            File templateTarget = new File(imcref.getExternalTemplateFolder(metaId, user), newLibName);
            File imageTarget = new File(RmiConf.getImagePathForExternalDocument(imcref, metaId, user), newLibName);

            fileObj.copyDirectory(templateSrc, templateTarget);
            fileObj.copyDirectory(imageSrc, imageTarget);

            res.sendRedirect("ConfAdmin?ADMIN_TYPE=META");
            return;
        }

        // ********* PREPARE ADMIN TEMPLATES ********
        if (req.getParameter("UPLOAD_CONF") != null) {
            String libName = (req.getParameter("TEMPLATE_NAME") == null) ? "" : (req.getParameter("TEMPLATE_NAME"));
            String uploadType = (req.getParameter("UPLOAD_TYPE") == null) ? "" : (req.getParameter("UPLOAD_TYPE"));

            params.setProperty("TEMPLATE_NAME", libName);
            params.setProperty("UPLOAD_TYPE", uploadType);

            String url = "ConfAdmin?ADMIN_TYPE=META";
            url += "&setname=" + libName + "&UPLOAD_TYPE=" + uploadType;
            res.sendRedirect(url);
            return;
        }

        // ********* SET TEMPLATE LIB FOR A CONFERENCE  ********
        if (req.getParameter("SET_TEMPLATE_LIB") != null) {
            log("Lets set a new template set for the conference");

            // Lets get the new library name and validate it
            String newLibName = req.getParameter("TEMPLATE_ID");
            if (newLibName == null) {
                String header = "ConfAdmin servlet. ";
                new ConfError(req, res, header, 80);
                return;
            }

            // Lets find the selected template in the database and get its id
            // if not found, -1 will be returned
            String templateId = imcref.sqlProcedureStr("A_GetTemplateIdFromName", new String[]{newLibName});
            if (templateId.equalsIgnoreCase("-1")) {
                String header = "ConfAdmin servlet. ";
                new ConfError(req, res, header, 81);
                return;
            }
            // Ok, lets update the conference with this new templateset.
            imcref.sqlUpdateProcedure("A_SetTemplateLib", new String[]{params.getProperty("META_ID"), templateId});

            res.sendRedirect("ConfAdmin?ADMIN_TYPE=META");
            return;
        } // SET TEMPLATE LIB

        // ********* DELETE REPLY ********
        if (req.getParameter("DELETE_REPLY") != null) {
            log("Nu tar vi bort inlägg");
            // Lets get the discusssion id
            String discId = params.getProperty("DISC_ID");

            // Lets get all the replies id:s
            String[] repliesIds = this.getDelReplyParameters(req);

            // Lets delete all marked replies. Observe that the first one wont be deleted!
            // if the user wants to delete the first one then he has to delete the discussion
            if (repliesIds != null) {
                for (int i = 0; i < repliesIds.length; i++) {
                    imcref.sqlUpdateProcedure("A_DeleteReply", new String[]{discId, repliesIds[i]});
                }
            }
            res.sendRedirect("ConfAdmin?ADMIN_TYPE=REPLY");
            return;
        }

        // ********* RESAVE REPLY ********
        if (req.getParameter("RESAVE_REPLY") != null) {
            // Lets get all the replies id:s
            String[] repliesIds = this.getDelReplyParameters(req);

            // Lets get the seleted textboxes headers and texts values.
            if (repliesIds != null) {
                for (int i = 0; i < repliesIds.length; i++) {
                    String newText = req.getParameter("TEXT_BOX_" + repliesIds[i]);
                    if (newText == null || newText.equals( "" )) {
                        ConfError err = new ConfError();
                        newText = err.getErrorMessage(req, 70);
                    }

                    String newHeader = req.getParameter("REPLY_HEADER_" + repliesIds[i]);
                    if (newHeader == null || newHeader.equals( "" )) {
                        ConfError err = new ConfError();
                        newHeader = err.getErrorMessage(req, 71);
                    }

                    // Lets validate the new text for the sql question
                    newHeader = super.verifySqlText(newHeader);
                    newText = super.verifySqlText(newText);

                    imcref.sqlUpdateProcedure("A_UpdateReply", new String[]{repliesIds[i], newHeader, newText});

                }
            }
            res.sendRedirect("ConfAdmin?ADMIN_TYPE=REPLY");
            return;
        }

        // ********* DELETE DISCUSSION ********
        if (req.getParameter("DELETE_DISCUSSION") != null) {

            // Lets get all the discussion id:s
            String[] discIds = this.getDelDiscParameters(req);

            // Lets delete all the discussion and all the replies in that discussion.
            if (discIds != null) {
                for (int i = 0; i < discIds.length; i++) {
                    imcref.sqlUpdateProcedure("A_DeleteDiscussion", new String[]{discIds[i]});
                }
            }
            res.sendRedirect("ConfAdmin?ADMIN_TYPE=DISCUSSION");
            return;
        }

        // ********* DELETE FORUM ********
        if (req.getParameter("DELETE_FORUM") != null) {
            log("Nu tar vi bort ett forum");
            params = this.getDelForumParameters(req, params);

            // Lets get the forum_id and set our session object before updating
            String aForumId = params.getProperty("FORUM_ID");

            // Lets get all discussions for that forum and delete those before deleting the forum
            // GetAllDiscsInForum @aForumId int
            String[] discs = imcref.sqlProcedure("A_GetAllDiscsInForum", new String[]{aForumId});
            if (discs != null) {
                for (int i = 0; i < discs.length; i++) {
                    imcref.sqlUpdateProcedure("A_DeleteDiscussion", new String[]{discs[i]});
                }
            }

            // DeleteForum @aForumId int
            imcref.sqlUpdateProcedure("A_DeleteForum", new String[]{params.getProperty("FORUM_ID")});
            this.doGet(req, res);
            return;
        }

        // ********* ADD FORUM ********
        if (req.getParameter("ADD_FORUM") != null) {
            log("Lets add a forum");
            // Lets get addForum parameters
            params = this.getAddForumParameters(req, params);

            // Lets verify the parameters for the sql questions.
            params = super.verifyForSql(params);

            // Lets check if a forum with that name exists

            String foundIt = imcref.sqlProcedureStr("A_FindForumName", new String[]{params.getProperty("META_ID"), params.getProperty("NEW_FORUM_NAME")});

            if (!foundIt.equalsIgnoreCase("-1")) {
                String header = "ConfAdmin servlet. ";
                new ConfError(req, res, header, 85);
                return;
            }

            //	AddNewForum @meta_id int, @forum_name varchar(255), @archive_mode char, @archive_time int

            final String archiveMode = "A";
            final String archiveTime = "30";
            imcref.sqlUpdateProcedure("A_AddNewForum", new String[]{params.getProperty("META_ID"), params.getProperty("NEW_FORUM_NAME"), archiveMode, archiveTime});
            this.doGet(req, res);
            return;
        }

        // ********* CHANGE FORUM NAME ********
        if (req.getParameter("CHANGE_FORUM_NAME") != null) {

            // Lets get addForum parameters
            params = this.getRenameForumParameters(req, params);

            // Lets verify the parameters for the sql questions.
            params = super.verifyForSql(params);

            imcref.sqlUpdateProcedure("A_RenameForum", new String[]{params.getProperty("FORUM_ID"), params.getProperty("NEW_FORUM_NAME")});
            this.doGet(req, res);
            return;
        }
        // ********* SET SHOW_DISCUSSION_COUNTER ********
        if (req.getParameter("SHOW_DISCUSSION_NBR") != null) {
            log("Lets set the nbr of discussions to show");
            // Lets get addForum parameters
            params = this.getShowDiscussionNbrParameters(req, params);

            imcref.sqlUpdateProcedure("A_SetNbrOfDiscsToShow", new String[]{params.getProperty("FORUM_ID"), params.getProperty("NBR_OF_DISCS_TO_SHOW")});

            res.sendRedirect("ConfAdmin?ADMIN_TYPE=FORUM");
            return;
        }

    } // DoPost

    /**
     * doGet
     */
    public void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        // Lets get the standard SESSION parameters
        Properties params = this.getStandardParameters(req);

        UserDomainObject user = Utility.getLoggedOnUser( req );
        if (!isUserAuthorized(req, res, user)) {
            return;
        }

        // Lets get serverinformation
        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();

        // Lets check that the user is an administrator
        if (super.userHasAdminRights(imcref, Integer.parseInt(params.getProperty("META_ID")), user) == false) {
            String header = "ConfAdmin servlet. ";
            new ConfError(req, res, header, 6);
            return;
        }

        // Lets get the admintype from the requestobject
        String adminWhat = (req.getParameter("ADMIN_TYPE") == null) ? "" : (req.getParameter("ADMIN_TYPE"));

        VariableManager vm = new VariableManager();
        String htmlFile = "";

        // *********** ADMIN SELF_REGISTER *************
        // Lets build the selfregister page to the user
        if (adminWhat.equalsIgnoreCase("SELF_REGISTER")) {

            // Lets check if the user is a superadmin
            if (Administrator.checkAdminRights(req) == false) {
                new ConfError(req, res, "ConfAdmin", 64);
                return;
            }


            // Lets get the current self register roles from DB
            String[] sqlAnswer = imcref.sqlProcedure("A_SelfRegRoles_GetAll", new String[]{params.getProperty("META_ID")});
            Vector selfRegV = super.convert2Vector(sqlAnswer);
            String selfRegList = Html.createOptionList("", selfRegV);

            // Lets ALL avaible self_register roles from DB
            String langPrefix = user.getLanguageIso639_2();

            String[] sqlAnswer2 = imcref.sqlProcedure("RoleGetConferenceAllowed", new String[]{langPrefix});
            Vector allSelfRegV = super.convert2Vector(sqlAnswer2);
            String allSelfRegList = Html.createOptionList("", allSelfRegV);

            // Lets build the Responsepage

            vm.addProperty("SELFREG_ROLES_LIST", selfRegList);
            vm.addProperty("ALL_SELFREG_ROLES_LIST", allSelfRegList);
            vm.addProperty("UNADMIN_LINK_HTML", FORUM_TEMPLATE2_UNADMIN_LINK_TEMPLATE);

            htmlFile = "Conf_Admin_Template3.htm";
            //return ;
        }

        // *********** ADMIN META *************
        if (adminWhat.equalsIgnoreCase("META")) {

            // Lets check which page we should show, the standard meta page or
            // the upload image/template page

            String setName = req.getParameter("setname");
            if (setName != null) {

                String uploadType = req.getParameter("UPLOAD_TYPE");
                if (uploadType == null) {
                    String header = "ConfAdmin servlet. ";
                    new ConfError(req, res, header, 83);
                    return;
                }

                // Ok, Lets get root path to the external type
                String metaId = params.getProperty("META_ID");

                vm.addProperty("UPLOAD_TYPE", uploadType);
                vm.addProperty("FOLDER_NAME", setName);
                vm.addProperty("META_ID", metaId);
                vm.addProperty("UNADMIN_LINK_HTML", FORUM_TEMPLATE2_UNADMIN_LINK_TEMPLATE);

                htmlFile = "Conf_Admin_Template2.htm";

                // Ok, were gonna show our standard meta page
            } else {

                // Lets get the current template set for this metaid
                String currTemplateSet = imcref.sqlProcedureStr("A_GetTemplateLib", new String[]{params.getProperty("META_ID")});

                // Lets get all current template sets
                String[] sqlAnswer = imcref.sqlProcedure("A_GetAllTemplateLibs", new String[]{});
                Vector templateV = super.convert2Vector(sqlAnswer);

                // Lets fill the select box	with forums
                String templateList = Html.createOptionList("", templateV);

                // Lets build the Responsepage
                //VariableManager vm = new VariableManager() ;
                vm.addProperty("TEMPLATE_LIST", templateList);
                vm.addProperty("A_META_ID", params.getProperty("META_ID"));
                vm.addProperty("CURRENT_TEMPLATE_SET", currTemplateSet);
                vm.addProperty("UNADMIN_LINK_HTML", FORUM_TEMPLATE1_UNADMIN_LINK_TEMPLATE);

                htmlFile = "Conf_Admin_Template1.htm";
                //return ;
            }
        }

        // *********** ADMIN FORUM *************
        if (adminWhat.equalsIgnoreCase("FORUM")) {

            // Lets get the information from DB
            String[] sqlAnswer = imcref.sqlProcedure("A_GetAllForum", new String[]{params.getProperty("META_ID")});
            Vector forumV = super.convert2Vector(sqlAnswer);

            // Lets fill the select box with forums
            String forumList = Html.createOptionList("", forumV);

            // Lets get all the showDiscs values
            String[] sqlAllDiscs = imcref.sqlProcedure("A_GetAllNbrOfDiscsToShow", new String[]{params.getProperty("META_ID")});

            Vector sqlAllDiscsV = new Vector();
            if (sqlAllDiscs != null) {
                sqlAllDiscsV = super.convert2Vector(sqlAllDiscs);
            }
            String discToShowList = Html.createOptionList("", sqlAllDiscsV);

            // Lets build the Responsepage
            vm.addProperty("FORUM_LIST", forumList);
            vm.addProperty("NBR_OF_DISCS_TO_SHOW_LIST", discToShowList);
            vm.addProperty("UNADMIN_LINK_HTML", FORUM_UNADMIN_LINK_TEMPLATE);
            htmlFile = "Conf_Admin_Forum.htm";
        }

        // *********** ADMIN DISCUSSION *************
        if (adminWhat.equalsIgnoreCase("DISCUSSION")) {
            String adminDiscList = "Conf_Admin_Disc_List.htm";
            log("OK, Administrera Discussions");

            // Lets get parameters
            String aMetaId = params.getProperty("META_ID");
            String aForumId = params.getProperty("FORUM_ID");
            String aLoginDate = params.getProperty("LAST_LOGIN_DATE");

            // Lets get the part of an html page, wich will be parsed for every a Href reference
            File aHrefHtmlFile = new File(super.getExternalTemplateFolder(req), adminDiscList);

            // Lets get all New Discussions
            String[][] sqlAnswerNew = imcref.sqlProcedureMulti("A_GetAllNewDiscussions", new String[]{aMetaId, aForumId, aLoginDate});

            // Lets get all Old Discussions
            String[][] sqlAnswerOld = imcref.sqlProcedureMulti("A_GetAllOldDiscussions", new String[]{aMetaId, aForumId, aLoginDate});

            // Lets build our tags vector.
            Vector tagsV = this.buildAdminTags();

            // Lets preparse all NEW records
            String allNewRecs = "";
            if (sqlAnswerNew != null) {
                if (sqlAnswerNew.length > 0)
                    allNewRecs = discPreParse(sqlAnswerNew, tagsV, aHrefHtmlFile);
            }
            // Lets preparse all OLD records
            String allOldRecs = "";
            if (sqlAnswerOld != null) {
                if (sqlAnswerOld.length > 0)
                    allOldRecs = discPreParse(sqlAnswerOld, tagsV, aHrefHtmlFile);
            }

            // Lets build the Responsepage
            vm.addProperty("NEW_A_HREF_LIST", allNewRecs);
            vm.addProperty("OLD_A_HREF_LIST", allOldRecs);
            vm.addProperty("UNADMIN_LINK_HTML", DISC_UNADMIN_LINK_TEMPLATE);

            htmlFile = "Conf_Admin_Disc.htm";
        } // End admin discussion

        // *********** ADMIN REPLIES *************
        if (adminWhat.equalsIgnoreCase("REPLY")) {
            String adminReplyList = "Conf_Admin_Reply_List.htm";

            // Lets get the users userId
            String userId = "" + user.getId();

            // Lets get the replylist from DB
            String discId = params.getProperty("DISC_ID");

            String[][] sqlAnswer = imcref.sqlProcedureMulti("A_GetAllRepliesInDiscAdmin", new String[]{discId, userId});

            // Lets get the users sortorder from DB
            String metaId = params.getProperty("META_ID");

            String sortOrderVal = imcref.sqlProcedureStr("A_ConfUsersGetReplyOrderSel", new String[]{metaId, userId});
            String checkBoxStr = "";

            if (sortOrderVal.equalsIgnoreCase("1")) checkBoxStr = "checked";

            // SYNTAX: date  first_name  last_name  headline   text reply_level
            // Lets build our variable list
            Vector tagsV = new Vector();
            tagsV.add("#REPLY_DATE#");
            tagsV.add("#FIRST_NAME#");
            tagsV.add("#LAST_NAME#");
            tagsV.add("#REPLY_HEADER#");
            tagsV.add("#REPLY_TEXT#");
            tagsV.add("#REPLY_LEVEL#");
            tagsV.add("#REPLY_ID#");
            tagsV.add("#REPLY_ID2#");
            tagsV.add("#REPLY_ID3#");

            // Lets get path to the imagefolder. http://dev.imcode.com/images/102/ConfDiscNew.gif
            String imagePath = super.getExternalImageFolder(req) + "ConfExpert.gif";
            // log("ImagePath: " + imagePath) ;

            // Lets get the part of an html page, wich will be parsed for every a Href reference
            File templateLib = super.getExternalTemplateFolder(req);
            File aSnippetFile = new File(templateLib, adminReplyList);

            // Lets preparse all records
            String allRecs = " ";
            if (sqlAnswer != null) allRecs = replyPreParse(sqlAnswer, tagsV, aSnippetFile, imagePath);

            // Lets build the Responsepage
            //VariableManager vm = new VariableManager() ;
            vm.addProperty("USER_SORT_ORDER", sortOrderVal);
            vm.addProperty("CHECKBOX_STATE", checkBoxStr);
            vm.addProperty("REPLIES_RECORDS", allRecs);
            vm.addProperty("UNADMIN_LINK_HTML", REPLY_UNADMIN_LINK_TEMPLATE);
            //return ;
            htmlFile = "Conf_Admin_Reply.htm";
        } // End admin Reply

        this.sendHtml(req, res, vm, htmlFile);

    } //DoGet


    // ****************** PARSE REPLIES FUNCTIONS ************************
    /**
     * Parses the Extended array with the htmlcode, which will be parsed
     * for all records in the array
     */
    private String replyPreParse(String[][] DBArr, Vector tagsV,
                                 File htmlCodeFile, String imagePath) {

        String htmlStr = "";
        // Lets do for all records...
        for (int i = 0; i < DBArr.length; i++) {
            Vector dataV = new Vector();

            // Lets do for one record... Get all fields for that record
            for (int j = 0; j < DBArr[i].length; j++) {
                dataV.add(DBArr[i][j]);
            } // End of one records for

            // Lets check if the user is some kind of "Master" eg. if he's
            // reply_level is equal to 1 and add the code returned to data.
            dataV.set(5, this.getReplyLevelCode(dataV, imagePath));

            // Lets the id two more times, since our parser cant replace one tag
            // more than once
            dataV.add(dataV.get(6));
            dataV.add(dataV.get(6));

            // Lets parse one record
            htmlStr += this.parseOneRecord(tagsV, dataV, htmlCodeFile);
        } // end of the big for
        return htmlStr;
    } // End of

    /**
     * Returns the users Replylevel htmlcode. If the user is marked with something
     * a bitmap will occur, otherwise nothing will occur.
     */
    private String getReplyLevelCode(Vector dataV, String ImagePath) {

        // Lets get the information regarding the replylevel
        int index = 5;
        String replyLevel = (String) dataV.elementAt(index);
        String htmlCode;
        String imageStart = "<img src=\"";
        String imageEnd = "\">";

        if (replyLevel.equals("1"))
            htmlCode = imageStart + ImagePath + imageEnd;
        else
            htmlCode = "";
        return htmlCode;
    }

    // ****************** END OF PARSE REPLIES FUNCTIONS ****************

    // ****************** PARSE DISCUSSIONS FUNCTIONS *******************
    /**
     * Parses the Extended array with the htmlcode, which will be parsed
     * for all records in the array
     */
    private String discPreParse(String[][] DBArr, Vector tagsV,
                                File htmlCodeFile) {

        String htmlStr = "";
        // Lets do for all records...
        for (int i = 0; i < DBArr.length; i++) {
            // Lets prepare the dataVector with some values before were read
            // what we got in the db

            // Lets check if the discussions should have a new bitmap in front of them
            Vector dataV = new Vector();

            // Lets do for one record... Get all fields for that record
            for (int j = 0; j < DBArr[i].length; j++) {
                dataV.add(DBArr[i][j]);
            }

            htmlStr += this.parseOneRecord(tagsV, dataV, htmlCodeFile);
        } // end of the big for
        return htmlStr;
    } // End of

    // ****************** END OF PARSE REPLIES FUNCTIONS ****************


    // ****************** GET PARAMETER FUNCTIONS ************************

    /**
     * Collects the parameters used for setting the number of discussions to show in a forum
     */

    private Properties getShowDiscussionNbrParameters(HttpServletRequest req, Properties params) {
        // Lets check if we shall create a new properties

        if (params == null) params = new Properties();
        // Lets get the standard metainformation
        String newNbr = (req.getParameter("NBR_OF_DISCS_TO_SHOW") == null) ? "" : (req.getParameter("NBR_OF_DISCS_TO_SHOW"));
        String forumId = (req.getParameter("FORUM_ID") == null) ? "" : (req.getParameter("FORUM_ID"));
        params.setProperty("FORUM_ID", forumId);
        params.setProperty("NBR_OF_DISCS_TO_SHOW", newNbr);
        return params;
    }

    /**
     * Collects the parameters used for adding a forum parametersfrom the SESSION object.
     */

    private Properties getAddForumParameters(HttpServletRequest req, Properties params) {
        // Lets check if we shall create a new properties

        if (params == null) params = new Properties();
        // Lets get the standard metainformation
        String newConfName = (req.getParameter("NEW_FORUM_NAME") == null) ? "" : (req.getParameter("NEW_FORUM_NAME"));
        params.setProperty("NEW_FORUM_NAME", newConfName);
        return params;
    }

    /**
     * Collects the standard parameters from the SESSION object.
     */

    private Properties getRenameForumParameters(HttpServletRequest req, Properties params) {
        // Lets check if we shall create a new properties

        if (params == null) params = new Properties();
        // Lets get the standard metainformation
        String newConfName = (req.getParameter("NEW_FORUM_NAME") == null) ? "" : (req.getParameter("NEW_FORUM_NAME"));
        String renForumId = (req.getParameter("FORUM_ID") == null) ? "" : (req.getParameter("FORUM_ID"));
        params.setProperty("NEW_FORUM_NAME", newConfName);
        params.setProperty("FORUM_ID", renForumId);
        return params;
    }

    /**
     * Collects the parameters used to delete a forum
     */

    private Properties getDelForumParameters(HttpServletRequest req, Properties params) {
        // Lets check if we shall create a new properties

        if (params == null) params = new Properties();
        // Lets get the standard metainformation
        String forumId = (req.getParameter("FORUM_ID") == null) ? "" : (req.getParameter("FORUM_ID"));
        params.setProperty("FORUM_ID", forumId);
        return params;
    }

    /**
     * Collects the parameters used to delete a discussion
     */

    private String[] getDelDiscParameters(HttpServletRequest req) {

        // Lets get the standard discussion_id to delete
        String[] discId = (req.getParameterValues("DISC_DEL_BOX"));
        return discId;
    }

    /**
     * Collects the parameters used to delete a reply
     */

    private String[] getDelReplyParameters(HttpServletRequest req) {

        // Lets get the standard discussion_id to delete
        String[] replyId = (req.getParameterValues("REPLY_DEL_BOX"));
        /*		for(int j=i; j<i+nbrOfCols ; j++) {
                dataV.add(DBArr[j]) ;
                //log("VÄRDE: " + j + " : " +  DBArr[j]) ;
                }
        */return replyId;
    }

    /**
     * Collects the standard parameters from the SESSION object.
     */

    private Properties getStandardParameters(HttpServletRequest req) {

        // Lets get the standard metainformation
        Properties reqParams = MetaInfo.createPropertiesFromMetaInfoParameters(super.getConferenceSessionParameters(req));

        // Lets get the session
        HttpSession session = req.getSession(false);
        if (session != null) {
            // Lets get the parameters we know we are supposed to get from the request object
            String forumId = ((String) session.getAttribute("Conference.forum_id") == null) ? "" : ((String) session.getAttribute("Conference.forum_id"));
            String discId = ((String) session.getAttribute("Conference.disc_id") == null) ? "" : ((String) session.getAttribute("Conference.disc_id"));
            String lastLogindate = ((String) session.getAttribute("Conference.last_login_date") == null) ? "" : ((String) session.getAttribute("Conference.last_login_date"));
            reqParams.setProperty("LAST_LOGIN_DATE", lastLogindate);
            reqParams.setProperty("FORUM_ID", forumId);
            reqParams.setProperty("DISC_ID", discId);
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
        tagsV.add("#DISC_DEL_ID#");
        tagsV.add("#ARCHIVE_DATE#");
        tagsV.add("#HEADLINE#");
        tagsV.add("#COUNT_REPLIES#");
        tagsV.add("#FIRST_NAME#");
        tagsV.add("#LAST_NAME#");
        return tagsV;
    } // End of buildstags

    /**
     * Log function, will work for both servletexec and Apache
     */

    public void log(String str) {
        super.log(str);
        System.out.println("ConfAdmin: " + str);
    }

} // End of class
