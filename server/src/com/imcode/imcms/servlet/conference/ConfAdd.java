package com.imcode.imcms.servlet.conference;

import imcode.server.*;
import imcode.server.user.UserDomainObject;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import imcode.external.diverse.*;
import imcode.util.Utility;

public class ConfAdd extends Conference {

    private final static String HTML_TEMPLATE = "conf_add.htm";
    private final static String SERVLET_NAME = "ConfAdd";

    public void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        // Lets get all parameters for this servlet
        Properties params = this.getParameters(req);

        UserDomainObject user = Utility.getLoggedOnUser(req);
        if (!isUserAuthorized(req, res, user)) {
            return;
        }

        // Lets detect which addtype we have
        String addType;
        addType = req.getParameter("ADDTYPE");

        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();

        int metaId = Integer.parseInt(params.getProperty("META_ID"));
        if (userHasRightToEdit(imcref, metaId, user)) {

            // ********* CANCEL ********
            if (req.getParameter("CANCEL") != null || req.getParameter("CANCEL.x") != null) {
                // Lets redirect to the servlet which holds in us.
                res.sendRedirect("ConfDiscView");
                return;
            }

            // ********* ADD DISCUSSION ********
            if (addType.equalsIgnoreCase("DISCUSSION") && (req.getParameter("ADD") != null || req.getParameter("ADD.x") != null)) {

                // Lets add a new discussion to the database
                String aForumId = params.getProperty("FORUM_ID");
                String userId = "";
                HttpSession session = req.getSession(false);
                if (session != null) {
                    userId = (String) session.getAttribute("Conference.user_id");
                }

                // Lets get the users reply level
                String level = imcref.sqlProcedureStr("A_ConfUsersGetUserLevel", new String[]{params.getProperty("META_ID"), userId});
                if (level.equalsIgnoreCase("-1")) {
                    log("An error occured in reading the users level");
                    level = "0";
                }

                // Lets verify the fields the user have had to write freetext in
                // to verify that the sql questions wont go mad.
                String addHeader = super.verifySqlText(params.getProperty("ADD_HEADER"));
                String addText = super.verifySqlText(params.getProperty("ADD_TEXT"));

                // Ok, Lets add the discussion to DB
                imcref.sqlUpdateProcedure("A_AddNewDisc", new String[]{aForumId, userId, addHeader, addText, level});

                // Lets add the new discussion id to the session object
                // Ok, Lets get the last discussion in that forum
                if (session != null) {
                    String latestDiscId = imcref.sqlProcedureStr("A_GetLastDiscussionId", new String[]{params.getProperty("META_ID"), aForumId});
                    session.setAttribute("Conference.disc_id", latestDiscId);
                }


                // Lets redirect to the servlet which holds in us.
                res.sendRedirect("ConfDiscView");
                return;
            }

            // ********* ADD REPLY ********
            // This is a workaround to fix the possibility to use gifs OR submit buttons

            if (addType.equalsIgnoreCase("REPLY") && (req.getParameter("ADD") != null || req.getParameter("ADD.x") != null)) {

                // Lets add a new Reply to the database
                String discId = params.getProperty("DISC_ID");
                String userId = "";
                HttpSession session = req.getSession(false);
                if (session != null) {
                    userId = (String) session.getAttribute("Conference.user_id");
                }

                // Lets get the users reply level
                String level = imcref.sqlProcedureStr("A_ConfUsersGetUserLevel", new String[]{params.getProperty("META_ID"), userId});
                if (level.equalsIgnoreCase("-1")) {
                    log("An error occured in reading the users level");
                    level = "0";
                }

                // Lets verify the textfields
                String addHeader = super.verifySqlText(params.getProperty("ADD_HEADER"));
                String addText = super.verifySqlText(params.getProperty("ADD_TEXT"));

                // Lets check the data size
                if (addText.length() > 32000) {
                    String header = SERVLET_NAME + " servlet. ";
                    new ConfError(req, res, header, 74, user );
                    return;
                }


                // Ok, Lets add the reply
                imcref.sqlUpdateProcedure("A_AddReply", new String[]{userId, discId, addHeader, addText, level});

                // Lets redirect to the servlet which holds in us.
                res.sendRedirect("ConfDiscView");
                return;
            }
        } else {
            String header = SERVLET_NAME + " servlet. ";
            new ConfError(req, res, header, 100, user );
            return;
        }
    } // DoPost

    /**
     * DoGet
     */
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        // Lets get all parameters for this servlet
        Properties params = this.getParameters(req);

        UserDomainObject user = Utility.getLoggedOnUser( req );
        if (!isUserAuthorized(req, res, user)) {
            return;
        }

        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();

        int metaId = Integer.parseInt(params.getProperty("META_ID"));
        if (userHasRightToEdit(imcref, metaId, user)) {
            // Lets Get the session user id
            // Ok, Lets get the last discussion in that forum
            String loginUserId = "";
            HttpSession session = req.getSession(false);
            if (session != null) {
                loginUserId = (String) session.getAttribute("Conference.user_id");
            }

            // Lets get a VariableManager
            VariableManager vm = new VariableManager();

            // Lets get the users first and last names
            String firstName = imcref.sqlProcedureStr("A_GetConfLoginNames", new String[]{params.getProperty("META_ID"), loginUserId, "1"});
            String lastName = imcref.sqlProcedureStr("A_GetConfLoginNames", new String[]{params.getProperty("META_ID"), loginUserId, "2"});

            vm.addProperty("FIRST_NAME", firstName);
            vm.addProperty("LAST_NAME", lastName);
            vm.addProperty("ADD_TYPE", params.getProperty("ADD_TYPE"));

            // Lets add the current forum name
            String currForum = imcref.sqlProcedureStr("A_GetForumName", new String[]{params.getProperty("FORUM_ID")});
            vm.addProperty("CURRENT_FORUM_NAME", currForum);

            // Lets get the addtype and add it to the page
            String addTypeHeader;
            if (params.getProperty("ADD_TYPE").equalsIgnoreCase("REPLY")) {
                ConfError err = new ConfError();
                addTypeHeader = err.getErrorMessage(req, 72);
            } else {
                ConfError err = new ConfError();
                addTypeHeader = err.getErrorMessage(req, 73);
            }

            vm.addProperty("ADD_TYPE_HEADER", addTypeHeader);

            // If addtype is reply, then lets get the header for the discussion
            // from the db and suggest it to the user
            String discHeader = "";
            if (params.getProperty("ADD_TYPE").equalsIgnoreCase("REPLY")) {
                String aDiscId = params.getProperty("DISC_ID");
                String[] arr = imcref.sqlProcedure("A_GetDiscussionHeader", new String[]{aDiscId});
                if (arr != null) {
                    if (arr.length > 0) {
                        discHeader = arr[0];
                    }
                }
            }
            vm.addProperty("DISC_HEADER", discHeader);
            this.sendHtml(req, res, vm, HTML_TEMPLATE);
            //	log("ConfAdd OK") ;
            return;
        } else {
            String header = SERVLET_NAME + " servlet. ";
            new ConfError(req, res, header, 100, user );
            return;
        }

    } //DoGet

    /**
     * Collects all the parameters used by this servlet
     */

    private Properties getParameters(HttpServletRequest req) {

        // Lets get the standard SESSION metainformation
        MetaInfo.Parameters metaParams = super.getConferenceSessionParameters(req);

        Properties params = MetaInfo.createPropertiesFromMetaInfoParameters(metaParams);

        // Lets get the EXTENDED SESSION PARAMETERS
        super.addExtSessionParametersToProperties(req, params);

        // Lets get our REQUESTPARAMETERS
        String addType = (req.getParameter("ADDTYPE") == null) ? "" : (req.getParameter("ADDTYPE"));
        String addHeader = (req.getParameter("ADDHEADER") == null) ? "" : (req.getParameter("ADDHEADER"));
        String addText = (req.getParameter("ADDTEXT") == null) ? "" : (req.getParameter("ADDTEXT"));

        // Alright, these parameters are userdefined text, and if the user hasnt filled something in them
        // then the checkparamters will warn for this. The thing is that we dont care if the
        // user passes a text or not, so lets look if the variable is empty, and if it is
        // just put " " in it!

        // from now on, we do care if the user has added a text or something. If the user hasnt
        // then get defalult values from the errmsg file

        if (addHeader.equals("")) {
            ConfError err = new ConfError();
            addHeader = err.getErrorMessage(req, 70);
        }
        if (addText.equals("")) {
            ConfError err = new ConfError();
            addText = err.getErrorMessage(req, 71);
        }

        params.setProperty("ADD_HEADER", addHeader);
        params.setProperty("ADD_TEXT", addText);
        params.setProperty("ADD_TYPE", addType);

        return params;
    }

} // End of class
