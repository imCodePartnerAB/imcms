package com.imcode.imcms.servlet;


import com.imcode.imcms.servlet.superadmin.UserEditorPage;
import com.imcode.imcms.util.l10n.LocalizedMessage;
import com.imcode.imcms.util.l10n.LocalizedMessageFormat;
import imcode.server.Imcms;
import imcode.server.SystemData;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import imcode.util.net.SMTP;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ForgotPassword extends HttpServlet {

    private static final Logger logger = org.apache.log4j.Logger.getLogger(ForgotPassword.class);

    // Ops
    public enum Op {
        REQUEST_RESET_URL,
        SEND_RESET_URL,
        RESET,
        SAVE_NEW_PASSWORD,
        UNDEFINED
    }


    // Request parameters associated with ops.
    public static final String
            REQUEST_PARAM_OP = "op",
            REQUEST_PARAM_RESET_ID = "id",
            REQUEST_PARAM_EMAIL = "email",
            REQUEST_PARAM_PASSWORD = "password",
            REQUEST_PARAM_PASSWORD_CHECK = "password_check";

    // Attribute is bounded to List<String> of error messages.
    public static final String REQUEST_ATTR_VALIDATION_ERRORS = "validation_errors";


    // Views
    private static final String
            account_email_form_view = "account_email_form.jsp",
            email_sent_confirmation_view = "email_sent_confirmation.jsp",
            new_password_form_view = "password_reset_form.jsp",
            password_changed_confirmation_view = "password_changed_confirmation.jsp";


    private ExecutorService emailSender = Executors.newFixedThreadPool(5);

    private final LocalizedMessage validationErrorNoEmail = new LocalizedMessage("forgotpassord.error.missing_email");


    /**
     * Forwards request to password reset or password edit view.
     *
     * Password reset request does not expect any parameters.
     * Password edit request required valid reset-id parameter.
     *
     * Forwards to 404 if request parameters do not met requirements.
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Op op = getOp(request);
        String view = op == Op.REQUEST_RESET_URL
                ? account_email_form_view
                : op == Op.RESET && getUserByPasswordResetId(request) != null
                    ? new_password_form_view
                    : null;

        render(request, response, view);
    }


    /**
     * Handles password recovery email sending and new password saving.
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Op op = getOp(request);
        String view = null;

        if (op == Op.SEND_RESET_URL) {
            String userEmail = StringUtils.trimToEmpty(request.getParameter(REQUEST_PARAM_EMAIL));

            if (userEmail.isEmpty()) {
                setValidationErrors(request, validationErrorNoEmail.toLocalizedString(request));
                view = account_email_form_view;
            } else {
                UserDomainObject receiver = Imcms.getServices().getImcmsAuthenticatorAndUserAndRoleMapper().createPasswordReset(userEmail);

                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("Requested password reset using email: '%s', created reset: '%s'",
                            userEmail, receiver == null ? "no reset where created" : receiver.getPasswordReset()));
                }

                if (receiver != null) {
                    String url = String.format("%s?%s=%s&%s=%s",
                            request.getRequestURL(),
                            REQUEST_PARAM_OP, Op.RESET.toString().toLowerCase(),
                            REQUEST_PARAM_RESET_ID, receiver.getPasswordReset().getId());

                    asyncSendPasswordResetEMail(Utility.getLoggedOnUser(request), receiver, request.getServerName(), url);
                }

                view = email_sent_confirmation_view;
            }
        } else if (op == Op.SAVE_NEW_PASSWORD) {
            UserDomainObject user = getUserByPasswordResetId(request);

            if (user != null) {
                String password = StringUtils.trimToEmpty(request.getParameter(REQUEST_PARAM_PASSWORD));
                String passwordCheck = StringUtils.trimToEmpty(request.getParameter(REQUEST_PARAM_PASSWORD_CHECK));

                LocalizedMessage errorMsg = UserEditorPage.validatePassword(user.getLoginName(), password, passwordCheck);

                if (errorMsg != null) {
                    setValidationErrors(request, errorMsg.toLocalizedString(user));
                    view = new_password_form_view;
                } else {
                    user.setPassword(password);
                    Imcms.getServices().getImcmsAuthenticatorAndUserAndRoleMapper().saveUser(user);
                    view = password_changed_confirmation_view;
                }
            }
        }

        render(request, response, view);
    }


    private static void render(HttpServletRequest request, HttpServletResponse response, String view)
            throws IOException, ServletException {

        if (view == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        } else {
            request.getRequestDispatcher("/WEB-INF/forgotpassword/" + view).forward(request, response);
        }
    }


    private static UserDomainObject getUserByPasswordResetId(HttpServletRequest request) {
        String resetId = StringUtils.trimToEmpty(request.getParameter(REQUEST_PARAM_RESET_ID));

        return Imcms.getServices().getImcmsAuthenticatorAndUserAndRoleMapper().getUserByPasswordResetId(resetId);
    }


    private Op getOp(HttpServletRequest request) {
        String opValue = StringUtils.trimToEmpty(request.getParameter(REQUEST_PARAM_OP)).toUpperCase();

        try {
            return opValue.isEmpty() ? Op.REQUEST_RESET_URL : Op.valueOf(opValue);
        } catch (Exception e) {
            return Op.UNDEFINED;
        }
    }


    private void setValidationErrors(HttpServletRequest request, String first, String... rest) {
        List<String> errors = new LinkedList<String>();

        errors.add(first);

        for (String error: rest) {
            errors.add(error);
        }

        request.setAttribute(REQUEST_ATTR_VALIDATION_ERRORS, errors);
    }


    private void asyncSendPasswordResetEMail(final UserDomainObject user, final UserDomainObject receiver, final String serverName, final String url) {
        emailSender.submit(new Runnable() {
            public void run() {
                String emailAddress = receiver.getEmailAddress();

                if (!Utility.isValidEmail(emailAddress)) {
                    logger.debug(String.format(
                            "Receiver (user login: %s) email address %s is invalid.",
                            receiver.getLoginName(), emailAddress));
                } else {
                    try {
                        logger.debug(String.format(
                                "Sending password reset email. User login: %s, user email address: %s.",
                                receiver.getLoginName(), emailAddress));

                        String subject = new LocalizedMessageFormat("forgotpassord.password_reset_email.subject", serverName).toLocalizedString(receiver);
                        String body = new LocalizedMessageFormat("forgotpassord.password_reset_email.body", serverName, url).toLocalizedString(receiver);

                        SystemData sysData = Imcms.getServices().getSystemData();
                        String eMailServerMaster = sysData.getServerMasterAddress();
                        SMTP smtp = Imcms.getServices().getSMTP();

                        smtp.sendMail(new SMTP.Mail( eMailServerMaster, new String[] { receiver.getEmailAddress() }, subject, body));

//                        Email email = new SimpleEmail();
//                        email.setDebug(true);
//                        email.setHostName("smtp.gmail.com");
//                        email.setSmtpPort(587);
//                        email.setDebug(true);
//                        email.setAuthenticator(new DefaultAuthenticator("@gmail.com", ""));
//                        email.setTLS(true);
//                        email.setFrom("admin@imcode.com");
//                        email.setSubject(subject);
//                        email.setMsg(body);
//                        email.addTo("@gmail.com");
//                        email.send();
                    } catch (Exception e) {
                        logger.error(
                                String.format(
                                        "Failed to send password reset email. User login: %s User email address: %s.",
                                        receiver.getLoginName(), receiver.getEmailAddress()),
                                e);
                    }
                }
            }
        });
    }


    @Override
    public void destroy() {
        super.destroy();
        emailSender.shutdownNow();
    }
}