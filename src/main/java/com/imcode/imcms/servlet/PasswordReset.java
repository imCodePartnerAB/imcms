package com.imcode.imcms.servlet;


import com.imcode.imcms.api.P;
import com.imcode.imcms.servlet.superadmin.UserEditorPage;
import com.imcode.imcms.util.l10n.LocalizedMessage;
import com.imcode.imcms.util.l10n.LocalizedMessageFormat;
import imcode.server.Imcms;
import imcode.server.SystemData;
import imcode.server.user.ImcmsAuthenticatorAndUserAndRoleMapper;
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
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PasswordReset extends HttpServlet {

    private static final Logger logger = org.apache.log4j.Logger.getLogger(PasswordReset.class);

    // Ops
    public enum Op {
        REQUEST_RESET,
        SEND_RESET_URL,
        RESET,
        SAVE_NEW_PASSWORD,
        UNDEFINED
    }


    // Request parameters associated with ops.
    public static final String
            REQUEST_PARAM_OP = "op",
            REQUEST_PARAM_RESET_ID = "id",
            REQUEST_USER_IDENTITY = "uid",
            REQUEST_PARAM_PASSWORD = "password",
            REQUEST_PARAM_PASSWORD_CHECK = "password_check";

    // Attribute is bounded to List<String> of error messages.
    public static final String REQUEST_ATTR_VALIDATION_ERRORS = "validation_errors";


    // Views
    private static final String
            identity_form_view = "identity_form.jsp",
            email_sent_confirmation_view = "email_sent_confirmation.jsp",
            password_reset_form_view = "password_reset_form.jsp",
            password_changed_confirmation_view = "password_changed_confirmation.jsp";


    private ExecutorService emailSender = Executors.newSingleThreadExecutor();

    private final LocalizedMessage validationErrorMissingUserId = new LocalizedMessage("passwordreset.error.missing_identity");


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
        String view = op == Op.REQUEST_RESET
                ? identity_form_view
                : op == Op.RESET && getUserByPasswordResetId(request) != null
                    ? password_reset_form_view
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
            String identity = StringUtils.trimToEmpty(request.getParameter(REQUEST_USER_IDENTITY));

            if (identity.isEmpty()) {
                setValidationErrors(request, validationErrorMissingUserId.toLocalizedString(request));
                view = identity_form_view;
            } else {
                P.P2<UserDomainObject, String> userAndEmail = createPasswordReset(identity);
                if (userAndEmail != null) {
                    String url = String.format("%s?%s=%s&%s=%s",
                            request.getRequestURL(),
                            REQUEST_PARAM_OP, Op.RESET.toString().toLowerCase(),
                            REQUEST_PARAM_RESET_ID, userAndEmail._1().getPasswordReset().getId());

                    asyncSendPasswordResetURL(userAndEmail, request.getServerName(), url);
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
                    view = password_reset_form_view;
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
            request.getRequestDispatcher("/WEB-INF/passwordreset/" + view).forward(request, response);
        }
    }


    private static UserDomainObject getUserByPasswordResetId(HttpServletRequest request) {
        String resetId = StringUtils.trimToEmpty(request.getParameter(REQUEST_PARAM_RESET_ID));

        return Imcms.getServices().getImcmsAuthenticatorAndUserAndRoleMapper().getUserByPasswordResetId(resetId);
    }


    private Op getOp(HttpServletRequest request) {
        String opValue = StringUtils.trimToEmpty(request.getParameter(REQUEST_PARAM_OP)).toUpperCase();

        try {
            return opValue.isEmpty() ? Op.REQUEST_RESET : Op.valueOf(opValue);
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


    private P.P2<UserDomainObject, String> createPasswordReset(String identity) {
        P.P2<UserDomainObject, String> result = null;
        ImcmsAuthenticatorAndUserAndRoleMapper urm = Imcms.getServices().getImcmsAuthenticatorAndUserAndRoleMapper();
        Map<Integer, P.P2<UserDomainObject, String>> idToUser = new HashMap<Integer, P.P2<UserDomainObject, String>>();
        boolean identityIsValidEmail = Utility.isValidEmail(identity);
        UserDomainObject userByLogin = identityIsValidEmail
                ? urm.getUserByLoginIgnoreCase(identity)
                : urm.getUser(identity);

        if (userByLogin != null) {
            if (identityIsValidEmail) {
                idToUser.put(userByLogin.getId(), P.of(userByLogin, identity));
            } else {
                String email = userByLogin.getEmailAddress().trim();
                if (Utility.isValidEmail(email)) {
                    idToUser.put(userByLogin.getId(), P.of(userByLogin, email));
                    // check if user's e-mail is valid and unique
                    // i.e not used by different user as a login or an e-mail.
                    UserDomainObject userByLogin2 = urm.getUserByLoginIgnoreCase(email);
                    if (userByLogin2 != null) {
                        idToUser.put(userByLogin2.getId(), P.of(userByLogin2, email));
                    }

                    for (UserDomainObject user: urm.getUsersByEmail(email)) {
                        idToUser.put(user.getId(), P.of(user, email));
                    }
                }
            }
        }

        if (identityIsValidEmail) {
            // find all users who use the same e-mail
            for (UserDomainObject user: urm.getUsersByEmail(identity)) {
                idToUser.put(user.getId(), P.of(user, identity));
            }
        }

        int usersCount = idToUser.size();

        if (usersCount == 0) {
            logger.warn(String.format("Can't create password reset. No user with identity '%s' were found or e-mail address is invalid.", identity));
        } else if (usersCount == 1) {
            P.P2<UserDomainObject, String> userAndEmail = idToUser.values().iterator().next();
            UserDomainObject user = userAndEmail._1();

            try {
                result = P.of(urm.createPasswordReset(user.getId()), userAndEmail._2());
            } catch (Exception e) {
                logger.error(String.format("Failed to create password reset for user %s.", user), e);
            }
        } else {
            int usersToDisplay = Math.min(usersCount, 5);
            int index = 1;
            StringBuilder sb = new StringBuilder("[");

            for (P.P2<UserDomainObject, String> userAndEmail: idToUser.values()) {
                 UserDomainObject user = userAndEmail._1();
                sb.append(String.format("User(login: '%s', email: '%s')", user.getLoginName(), user.getEmailAddress()));

                if (index < usersToDisplay) {
                    sb.append(", ");
                } else {
                    if (usersToDisplay < usersCount) {
                        sb.append(", ...");
                    }

                    sb.append("]");
                    break;
                }

                index += 1;
            }

            logger.warn(String.format(
                    "Can't create password reset. More than one (%s) user with identity '%s' were found: %s.",
                    usersCount, identity, sb));
        }

        return result;
    }


    private void asyncSendPasswordResetURL(final P.P2<UserDomainObject, String> userAndEmail, final String serverName, final String url) {
        emailSender.submit(new Runnable() {
            public void run() {
                UserDomainObject user = userAndEmail._1();
                String emailAddress = userAndEmail._2();

                try {
                    logger.debug(String.format(
                            "Sending password reset URL to the user %s, using e-mail address %s.", user, emailAddress));

                    String subject = new LocalizedMessageFormat("passwordreset.password_reset_email.subject", serverName).toLocalizedString(user);
                    String body = new LocalizedMessageFormat("passwordreset.password_reset_email.body", serverName, url).toLocalizedString(user);

                    SystemData sysData = Imcms.getServices().getSystemData();
                    String eMailServerMaster = sysData.getServerMasterAddress();
                    SMTP smtp = Imcms.getServices().getSMTP();

                    smtp.sendMail(new SMTP.Mail(eMailServerMaster, new String[] { emailAddress }, subject, body));

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
                    logger.error(String.format(
                                    "Failed to send password reset URL to the user %s, using e-mail address %s.",
                                    user, emailAddress),
                                e);
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