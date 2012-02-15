package com.imcode.imcms.servlet;

import imcode.server.Imcms;
import imcode.server.SystemData;
import imcode.server.user.UserDomainObject;
import imcode.util.net.SMTP;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.SimpleEmail;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class ForgotPassword extends HttpServlet {

    // Available commands.
    public enum Op {
        REQUEST_RESET,
        SEND_RESET_URL,
        RESET,
        SAVE_NEW_PASSWORD,
    }


    // Request parameters associated with commands.
    public static final String
            REQUEST_PARAM_OP = "op",
            REQUEST_PARAM_RESET_ID = "id",
            REQUEST_PARAM_EMAIL = "email",
            REQUEST_PARAM_PASSWORD = "password",
            REQUEST_PARAM_PASSWORD_CHECK = "password_check";

    // Attribute is bounded to List<String> of error messages.
    public static final String REQUEST_ATTR_FORM_ERRORS = "form_errors";


    // Views
    private static final String
            account_email_form_view = "account_email_form.jsp",
            email_sent_confirmation_view = "email_sent_confirmation.jsp",
            new_password_form_view = "password_reset_form.jsp",
            password_changed_confirmation_view = "password_changed_confirmation.jsp";


    /**
     * Forwards request to password reset or password edit view.
     *
     * Password reset request does not require any additional parameters.
     * Password edit request expects valid reset-id parameter.
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
        String view = op == Op.REQUEST_RESET
                ? account_email_form_view
                : op == Op.RESET && getUserByPasswordResetId(request) != null
                    ? new_password_form_view
                    : null;

        render(request, response, view);
    }


    /**
     * Handles email sending and new password saving.
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
            String email = StringUtils.trimToEmpty(request.getParameter(REQUEST_PARAM_EMAIL));

            if (email.isEmpty()) {
                List<String> formErrors = new LinkedList<String>();

                formErrors.add("Missing email address");
                request.setAttribute(REQUEST_ATTR_FORM_ERRORS, formErrors);
                view = account_email_form_view;
            } else {
                // todo: only by email ???
                UserDomainObject user = Imcms.getServices().getImcmsAuthenticatorAndUserAndRoleMapper().createPasswordReset(email);

                if (user != null) {
                    System.out.println(user.getPasswordReset());

                    SimpleEmail email = new SimpleEmail();
                    email.setHostName("smtp.gmail.com");
                    email.setSmtpPort(587);
                    email.setAuthenticator(new DefaultAuthenticator("username", "password"));
                    email.setTLS(true);
                    email.setFrom("user@gmail.com");
                    email.setSubject("TestMail");
                    email.setMsg("This is a test mail ... :-)");
                    email.addTo("foo@bar.com");
                    email.send();

                    // todo: check email format is valid and send out an email.
                    //SystemData sysData = Imcms.getServices().getSystemData();
                    //String eMailServerMaster = sysData.getServerMasterAddress();
                    //SMTP smtp = Imcms.getServices().getSMTP();
                    // imcode.util.Utility.isValidEmail(userEmail)
                    //smtp.sendMail(new SMTP.Mail( eMailServerMaster, new String[] { user.getEmailAddress() }, "subject", "body"));
                }

                view = email_sent_confirmation_view;
            }
        } else if (op == Op.SAVE_NEW_PASSWORD) {
            UserDomainObject user = getUserByPasswordResetId(request);

            if (user != null) {
                String password = request.getParameter(REQUEST_PARAM_PASSWORD);
                String passwordCheck = request.getParameter(REQUEST_PARAM_PASSWORD_CHECK);

                // todo: check password

                user.setPassword(password);
                Imcms.getServices().getImcmsAuthenticatorAndUserAndRoleMapper().saveUser(user);

                view = password_changed_confirmation_view;
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
        try {
            return Op.valueOf(StringUtils.trimToEmpty(request.getParameter(REQUEST_PARAM_OP)).toUpperCase());
        } catch (Exception e) {
            return Op.REQUEST_RESET;
        }
    }
}