package com.imcode.imcms.servlet;

import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import imcode.util.net.SMTP;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PasswordMailReminder extends HttpServlet {

    /* filnames for templates */
    private final static String USER_DONT_EXIST = "password_no_user.txt";
    private final static String USER_HAS_NO_EMAIL = "password_no_email.txt";
    private final static String USER_HAS_NOT_RIGHT = "password_no_right.txt";
    private final static String SENT_USER_PASSWORD = "password_sent.txt";
    private final static String USER_MAIL_BODY = "password_user_mail.txt";

    /* filnames for errors */
    private final static String ERROR_STRING = "password_error_input.txt";

    /* returning document */
    private final static String RETURNING_DOCUMENT_NO_USER_NAME = "password_no_user.html";
    private final static String RETURNING_DOCUMENT_NO_EMAIL = "password_no_email.html";
    private final static String RETURNING_DOCUMENT_NO_RIGHT = "password_no_right.html";
    private final static String RETURNING_DOCUMENT_SENT = "password_sent.html";
    private final static String RETURNING_DOCUMENT_INPUT = "password_submit.html";

    private final static int PASSWORD_PERMISSION_ID = 1;

    /**
     * showing input document whit out error
     */
    public void doGet( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {

        /* server info */

        ImcmsServices imcref = Imcms.getServices();

        Utility.setDefaultHtmlContentType( res );
        ServletOutputStream out = res.getOutputStream();

        List tags = new ArrayList();

        tags.add( "#errorininput#" );
        tags.add( "" );

        UserDomainObject user = Utility.getLoggedOnUser( req );
        String returnString = imcref.getAdminTemplate( PasswordMailReminder.RETURNING_DOCUMENT_INPUT, user, tags );
        out.print( returnString );
    }

    /**
     * proces submit
     */
    public void doPost( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {

        /* server info */

        ImcmsServices imcref = Imcms.getServices();

        /* mailserver info */
        imcode.server.SystemData sysData = imcref.getSystemData();
        String eMailServerMaster = sysData.getServerMasterAddress();
        String emailFromServer = sysData.getServerMasterAddress();

        String mailFrom = eMailServerMaster;

        String emptyString = "";

        /* user info */
        String postedLoginName;
        String firstName = emptyString;
        String lastName = emptyString;
        String password = emptyString;
        String userEmail = emptyString;

        String returnFileBody = emptyString;
        String serverMasterMailBody = emptyString;
        String returnString = emptyString;
        List tags = new ArrayList();

        /* condition variabels */
        boolean validLoginName = false;
        boolean sendMailToUser = false;

        postedLoginName = req.getParameter( "login_name" );
        validLoginName = !( !( postedLoginName != null ) || postedLoginName.length() == 0 );

        if ( validLoginName ) {

            String[] queryResult = imcref.sqlQuery( "select login_password, first_name, last_name, email, min(permissions & ?), lang_prefix \n"
                                                        + "from users u \n"
                                                        + "join lang_prefixes lp \n"
                                                        + "    on u.language = lp.lang_prefix\n"
                                                        + "join user_roles_crossref urc \n"
                                                        + "    on u.user_id = urc.user_id left \n"
                                                        + "join roles r \n"
                                                        + "    on r.role_id = urc.role_id\n"
                                                        + "where login_name = ?\n"
                                                        + "group by login_password, first_name, last_name, email, lang_prefix", new String[]{
                "" + PasswordMailReminder.PASSWORD_PERMISSION_ID, postedLoginName
            } );
            UserDomainObject user = imcref.getImcmsAuthenticatorAndUserAndRoleMapper().getUser( postedLoginName );
            user.setCurrentContextPath( req.getContextPath() );
            if ( ( queryResult != null ) && ( queryResult.length > 0 ) ) {

                firstName = queryResult[1];
                lastName = queryResult[2];
                userEmail = queryResult[3];

                boolean userHasOnlyRolesWithPermissionToGetPasswordSentByMail = !( "0".equals( queryResult[4] ) );

                if ( userHasOnlyRolesWithPermissionToGetPasswordSentByMail ) {

                    if ( ( userEmail != null ) && ( Utility.isValidEmail( userEmail ) ) ) {
                        sendMailToUser = true;
                        password = queryResult[0];

                        returnFileBody = PasswordMailReminder.RETURNING_DOCUMENT_SENT;
                        serverMasterMailBody = PasswordMailReminder.SENT_USER_PASSWORD;

                    } else {
                        returnFileBody = PasswordMailReminder.RETURNING_DOCUMENT_NO_EMAIL;
                        serverMasterMailBody = PasswordMailReminder.USER_HAS_NO_EMAIL;
                    }

                } else {
                    returnFileBody = PasswordMailReminder.RETURNING_DOCUMENT_NO_RIGHT;
                    serverMasterMailBody = PasswordMailReminder.USER_HAS_NOT_RIGHT;
                }

            } else {
                returnFileBody = PasswordMailReminder.RETURNING_DOCUMENT_NO_USER_NAME;
                serverMasterMailBody = PasswordMailReminder.USER_DONT_EXIST;
            }

            /* send mail */
            //			try {
            SMTP smtp = imcref.getSMTP();

            if ( sendMailToUser ) {
                List mailTags = new ArrayList();

                mailTags.add( "#firstname#" );
                mailTags.add( firstName );
                mailTags.add( "#lastname#" );
                mailTags.add( lastName );
                mailTags.add( "#password#" );
                mailTags.add( password );

                String userMessage = imcref.getAdminTemplate( PasswordMailReminder.USER_MAIL_BODY, user, mailTags );

                smtp.sendMail( new SMTP.Mail( mailFrom, new String[] { userEmail }, null, userMessage ) );

            }

            List parsVector = new ArrayList();
            String host = req.getServerName();

            parsVector.add( "#username#" );
            parsVector.add( postedLoginName );
            parsVector.add( "#email#" );
            parsVector.add( userEmail );
            parsVector.add( "#host#" );
            parsVector.add( host );

            String serverMasterMessage = imcref.getAdminTemplate( serverMasterMailBody, user, parsVector );

            smtp.sendMail( new SMTP.Mail( emailFromServer, new String[] { eMailServerMaster }, null, serverMasterMessage ) ) ;

            returnString = imcref.getAdminTemplate( returnFileBody, user, null );

        } else {
            UserDomainObject user = Utility.getLoggedOnUser( req );
            String errorString = imcref.getAdminTemplate( PasswordMailReminder.ERROR_STRING, user, null );

            tags.add( "#errorininput#" );
            tags.add( errorString );
            returnString = imcref.getAdminTemplate( PasswordMailReminder.RETURNING_DOCUMENT_INPUT, user, tags );
        }

        Utility.setDefaultHtmlContentType( res );
        ServletOutputStream out = res.getOutputStream();

        out.print( returnString );

    }

}
