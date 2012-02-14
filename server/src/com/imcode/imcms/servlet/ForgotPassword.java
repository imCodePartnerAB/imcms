package com.imcode.imcms.servlet;

import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// todo: synchronize
public class ForgotPassword extends HttpServlet {

    public enum Op {
        RESET,
        SEND_RESET_URL,
        SAVE_NEW_PASSWORD,
        UNDEFINED
    }

    public static final String
            REQUEST_PARAM_OP = "op",
            REQUEST_PARAM_RESET_ID = "rid",
            REQUEST_PARAM_EMAIL = "email",
            REQUEST_PARAM_PASSWORD = "password",
            REQUEST_PARAM_PASSWORD_CHECK = "password_check";

    /*
     WELCOME PAGE: FORM
     Enter the e-mail address associated with your account, then click Continue.
     We'll email you a link to a page where you can easily create a new password.
     <EMAIL>
     <CAPTCHA>

     CHECK YOUR EMAIL PAGE: INFO
     Check your e-mail.
     If the e-mail address you entered john.smith@acme.cim is associated with a customer account in our records,
     you will receive an e-mail from us with instructions for resetting your password.
     If you don't receive this e-mail, please check your junk mail folder
     or visit our Help pages to contact Customer Service for further assistance.

     CREATE NEW PASSWORD PAGE: FORM
     Create your new password.
     We'll ask you for this password when you place an order, check on an order's status, and access other account information.
     <NEW PASSWORD>
     <RE-ENTER NEW PASSWORD>

     CONFIRMATION PAGE: INFO
     You have successfully changed your password.
     ?link: login      ?
     ?link: front page ?
     */

    private static final String
            account_email_form_view = "account_email_form.jsp",
            email_sent_confirmation_view = "email_sent_confirmation.jsp",
            new_password_form_view = "password_reset_form.jsp",
            password_changed_confirmation_view = "password_changed_confirmation.jsp";


    /**
     * Forwards requests to the password editing view.
     * Expects valid password reset-id parameter.
     * Redirects to 404 if reset-id parameter is not provided or its value is invalid (or expired).
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Op op = getOp(request);
        String view = null;
        if (op == Op.RESET) {
            view = account_email_form_view;
        } else if (getUserByPasswordResetId(request) != null) {
            view = new_password_form_view;
        }

        render(request, response, view);
    }

    // todo: add checks
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Op op = getOp(request);
        String view = null;

        if (op == Op.RESET) {
            view = account_email_form_view;
        } else if (op == Op.SEND_RESET_URL) {
            String email = request.getParameter(REQUEST_PARAM_EMAIL);
            UserDomainObject user = Imcms.getServices().getImcmsAuthenticatorAndUserAndRoleMapper().createPasswordReset(email);
            // if email has valid value
            if (user != null) {
                view = email_sent_confirmation_view;
                System.out.println("Password id: " + user.getPasswordReset().getId());
            }
        } else if (op == Op.SAVE_NEW_PASSWORD) {
            UserDomainObject user = getUserByPasswordResetId(request);

            if (user != null) {
                System.out.println("Password id: " + user.getPasswordReset().getId());
                String password = request.getParameter(REQUEST_PARAM_PASSWORD);
                String passwordCheck = request.getParameter(REQUEST_PARAM_PASSWORD_CHECK);

                user.setPassword(password);
                Imcms.getServices().getImcmsAuthenticatorAndUserAndRoleMapper().saveUser(user);
                // forward You have successfully changed your password;
                // send notification email
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
            return Op.UNDEFINED;
        }
    }
}