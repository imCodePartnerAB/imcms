/*
 *
 * @(#)ErrorMessageGenerator.java
 *
 *
 * 2000-10-12
 *
 * Copyright (c)
 *
 */

import java.util.Vector;
import java.io.File;
import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import imcode.external.diverse.SettingsAccessor;
import imcode.server.IMCServiceInterface;
import imcode.server.user.UserDomainObject;

/**
 * Generats language errormessage from a templatefile.
 * The message can be hard coded, but should be fetched by code from file ErrMsg.ini.
 * <p/>
 * Error file name ErrMsg.ini
 * <p/>
 * Tags used in html template:
 * #ERROR_HEADER#
 * #ERROR_MESSAGE#
 * #EMAIL_SERVER_MASTER#
 *
 * @author Jerker Drottenmyr
 * @version 1.2 19 Oct 2000
 */

class ErrorMessageGenerator {

    /**
     * File name for errorCodes
     */
    private static final String ERROR_CODE_FILE = "ErrMsg.ini";

    private IMCServiceInterface imcref;
    private String emailServerMaster;
    private UserDomainObject user;
    private String errorHeader;
    private String errorMessage;
    private String htmlErrorTemplate;

    /**
     * Creats an error message with pased heade and message.
     *
     * @param imcref
     * @param emailServerMaster
     * @param user
     * @param errorHeader
     * @param errorMessage
     * @param htmlErrorTemplate documnet to pars
     */
    private ErrorMessageGenerator(IMCServiceInterface imcref, String emailServerMaster,
                                  UserDomainObject user, String errorHeader,
                                  String errorMessage, String htmlErrorTemplate) {

        this.imcref = imcref;
        this.emailServerMaster = emailServerMaster;
        this.user = user;
        this.errorHeader = errorHeader;
        this.errorMessage = errorMessage;
        this.htmlErrorTemplate = htmlErrorTemplate;
    }

    /**
     * Creats an error message with pased heade. The message is fetched from ErrMsg.ini.
     *
     * @param imcref
     * @param emailServerMaster
     * @param user
     * @param errorHeader
     * @param htmlErrorTemplate documnet to pars
     * @param errorCode         - errorCode to look upp in ErrMsg.ini
     */
    ErrorMessageGenerator(IMCServiceInterface imcref, String emailServerMaster,
                          UserDomainObject user, String errorHeader,
                          String htmlErrorTemplate, int errorCode) {

        this(imcref, emailServerMaster, user, errorHeader, "", htmlErrorTemplate);
        this.errorMessage = getErrorMessage(errorCode);
    }

    /**
     * sends back html error page to client
     */
    public void sendHtml(HttpServletResponse response) throws IOException {

        response.setContentType("text/html");
        ServletOutputStream out = response.getOutputStream();

        out.print(createErrorString());
    }

    /**
     * creats error page as string object
     */
    private String createErrorString() {

        Vector tagParsList = new Vector();

        tagParsList.add("#ERROR_HEADER#");
        tagParsList.add(errorHeader);
        tagParsList.add("#ERROR_MESSAGE#");
        tagParsList.add(errorMessage);
        tagParsList.add("#EMAIL_SERVER_MASTER#");
        tagParsList.add(emailServerMaster);

        return imcref.parseDoc(tagParsList, htmlErrorTemplate, user);
    }

    /**
     * Extracts error from error file ErrMsg.ini
     */
    private String getErrorMessage(int errCode) {
        String errorMessage = "";
        try {
            // Lets get the path to the template library
            File errorTemplatePath = imcref.getTemplateHome();

            // Lets get the error code
            SettingsAccessor setObj = new SettingsAccessor(new java.io.File(errorTemplatePath, imcref.getUserLangPrefixOrDefaultLanguage(user) + "/admin/" + ErrorMessageGenerator.ERROR_CODE_FILE));
            setObj.setDelimiter("=");
            setObj.loadSettings();
            errorMessage = setObj.getSetting("" + errCode);
            if (errorMessage == null) {
                errorMessage = "Missing Errorcode";
            }

        } catch (Exception e) {
            errorMessage = "An error occured while reading the errorCode file";
        }
        return errorMessage;
    }
}
