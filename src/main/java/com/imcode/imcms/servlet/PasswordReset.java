package com.imcode.imcms.servlet;


import com.imcode.imcms.api.Mail;
import com.imcode.imcms.servlet.superadmin.UserEditorPage;
import com.imcode.imcms.util.l10n.LocalizedMessage;
import com.imcode.imcms.util.l10n.LocalizedMessageFormat;
import imcode.server.Imcms;
import imcode.server.SystemData;
import imcode.server.user.ImcmsAuthenticatorAndUserAndRoleMapper;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PasswordReset extends HttpServlet {

    // Request parameters associated with ops.
    public static final String
            REQUEST_PARAM_OP = "op",
            REQUEST_PARAM_RESET_ID = "id",
            REQUEST_USER_IDENTITY = "uid",
            REQUEST_PARAM_PASSWORD = "password",
            REQUEST_PARAM_PASSWORD_CHECK = "password_check";
    // Attribute is bounded to List<String> of error messages.
    public static final String REQUEST_ATTR_VALIDATION_ERRORS = "validation_errors";
    private static final Logger logger = LogManager.getLogger(PasswordReset.class);
    // Views
    private static final String
            identity_form_view = "identity_form.jsp",
            email_sent_confirmation_view = "email_sent_confirmation.jsp",
            password_reset_form_view = "password_reset_form.jsp",
            password_changed_confirmation_view = "password_changed_confirmation.jsp",
            no_email_permission_view = "no_email_permission.jsp";
    private final LocalizedMessage validationErrorMissingUserId = new LocalizedMessage("passwordreset.error.missing_identity");
    private ExecutorService emailSender = Executors.newSingleThreadExecutor();

    private static void render(HttpServletRequest request, HttpServletResponse response, String view)
            throws IOException, ServletException {

        if (view == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        } else {
		    request.setAttribute("userLanguage", Utility.getUserLanguageFromCookie(request.getCookies()).getCode());
		    request.getRequestDispatcher("/WEB-INF/passwordreset/" + view).forward(request, response);
        }
    }

    private static UserDomainObject getUserByPasswordResetId(HttpServletRequest request) {
        String resetId = StringUtils.trimToEmpty(request.getParameter(REQUEST_PARAM_RESET_ID));

        return Imcms.getServices().getImcmsAuthenticatorAndUserAndRoleMapper().getUserByPasswordResetId(resetId);
    }

    /**
     * Forwards request to password reset or password edit view.
     *
     * Password reset request does not expect any parameters.
     * Password edit request required valid reset-id parameter.
     *
     * Forwards to 404 if request parameters do not met requirements.
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
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Op op = getOp(request);
        String view = null;

        if (op == Op.SEND_RESET_URL) {
            String identity = StringUtils.trimToEmpty(request.getParameter(REQUEST_USER_IDENTITY));

            if (identity.isEmpty()) {
	            request.setAttribute(REQUEST_ATTR_VALIDATION_ERRORS, validationErrorMissingUserId);
                view = identity_form_view;
            } else {
                UserIdentity userAndEmail = createPasswordReset(identity);
                if (userAndEmail != null) {
                    String url = String.format("%s?%s=%s&%s=%s",
                            request.getRequestURL(),
                            REQUEST_PARAM_OP, Op.RESET.toString().toLowerCase(),
                            REQUEST_PARAM_RESET_ID, userAndEmail.user().getPasswordReset().getId());

                    asyncSendPasswordResetURL(userAndEmail, request.getServerName(), url);

                    view = email_sent_confirmation_view;
                }else{
                    view = no_email_permission_view;
                }
            }
        } else if (op == Op.SAVE_NEW_PASSWORD) {
            UserDomainObject user = getUserByPasswordResetId(request);

            if (user != null) {
                String password = StringUtils.trimToEmpty(request.getParameter(REQUEST_PARAM_PASSWORD));
                String passwordCheck = StringUtils.trimToEmpty(request.getParameter(REQUEST_PARAM_PASSWORD_CHECK));

                LocalizedMessage errorMsg = UserEditorPage.validatePassword(user.getLoginName(), password, passwordCheck);

                if (errorMsg != null) {
	                request.setAttribute(REQUEST_ATTR_VALIDATION_ERRORS, errorMsg);
                    view = password_reset_form_view;
                } else {
                    user.setPassword(password);
                    Imcms.getServices().getImcmsAuthenticatorAndUserAndRoleMapper().saveUser(user);
                    Utility.logGDPR(user.getId(), "Password changed");
                    view = password_changed_confirmation_view;
                }
            }
        }

        render(request, response, view);
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
        List<String> errors = new LinkedList<>();

        errors.add(first);
        errors.addAll(Arrays.asList(rest));

        request.setAttribute(REQUEST_ATTR_VALIDATION_ERRORS, errors);
    }

    private UserIdentity createPasswordReset(String identity) {
        UserIdentity result = null;
        ImcmsAuthenticatorAndUserAndRoleMapper urm = Imcms.getServices().getImcmsAuthenticatorAndUserAndRoleMapper();
        Map<Integer, UserIdentity> userIdToUserIdentity = new HashMap<>();
        boolean identityIsValidEmail = Utility.isValidEmail(identity);
        UserDomainObject userByLogin = identityIsValidEmail
                ? urm.getUserByLoginIgnoreCase(identity)
                : urm.getUser(identity);

        if (userByLogin != null) {
            if (identityIsValidEmail) {
                userIdToUserIdentity.put(userByLogin.getId(), UserIdentity.of(userByLogin, identity));
            } else {
                String email = StringUtils.trimToNull(userByLogin.getEmailAddress());
                if (Utility.isValidEmail(email)) {
                    userIdToUserIdentity.put(userByLogin.getId(), UserIdentity.of(userByLogin, email));
                    // check if user's e-mail is valid and unique
                    // i.e not used by different user as a login or an e-mail.
                    UserDomainObject userByLogin2 = urm.getUserByLoginIgnoreCase(email);
                    if (userByLogin2 != null) {
                        userIdToUserIdentity.put(userByLogin2.getId(), UserIdentity.of(userByLogin2, email));
                    }

                    for (UserDomainObject user : urm.getUsersByEmail(email)) {
                        userIdToUserIdentity.put(user.getId(), UserIdentity.of(user, email));
                    }
                }
            }
        }

        if (identityIsValidEmail) {
            // find all users who use the same e-mail
            for (UserDomainObject user : urm.getUsersByEmail(identity)) {
                userIdToUserIdentity.put(user.getId(), UserIdentity.of(user, identity));
            }
        }

        int usersCount = userIdToUserIdentity.size();

        if (usersCount == 0) {
            logger.warn(String.format("Can't create password reset. No user with identity '%s' were found or e-mail address is invalid.", identity));
        } else if (usersCount == 1) {
            UserIdentity userAndEmail = userIdToUserIdentity.values().iterator().next();
            UserDomainObject user = userAndEmail.user();

            try {
                result = UserIdentity.of(urm.createPasswordReset(user.getId()), userAndEmail.identity());
                Utility.logGDPR(user.getId(), "Password reset request");
            } catch (Exception e) {
                logger.error(String.format("Failed to create password reset for user %s.", user), e);
            }
        } else {
            int usersToDisplay = Math.min(usersCount, 5);
            int index = 1;
            StringBuilder sb = new StringBuilder("[");

            for (UserIdentity userAndEmail : userIdToUserIdentity.values()) {
                UserDomainObject user = userAndEmail.user();
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

    private void asyncSendPasswordResetURL(final UserIdentity userAndEmail, final String serverName, final String url) {
        emailSender.submit(() -> {
            final UserDomainObject user = userAndEmail.user();
            final String emailAddress = userAndEmail.identity();
            final String loginName = user.getLoginName();

            try {
                logger.debug(String.format(
                        "Sending password reset URL to the user %s, using e-mail address %s.", user, emailAddress));

                final String passResetMessageSubjectKey = "passwordreset.password_reset_email.subject";
                final String subject = new LocalizedMessageFormat(passResetMessageSubjectKey, serverName)
                        .toLocalizedString(user.getLanguageIso639_2());

                final String passResetMessageBodyKey = "passwordreset.password_reset_email.body";
                final String body = new LocalizedMessageFormat(passResetMessageBodyKey, serverName, url, loginName)
                        .toLocalizedString(user.getLanguageIso639_2());

                final SystemData sysData = Imcms.getServices().getSystemData();
                final String eMailServerMaster = sysData.getServerMasterAddress();

                Mail mail = new Mail(eMailServerMaster);
                mail.setSubject(subject);
                mail.setBody(body);
                mail.setToAddresses(new String[]{emailAddress});
                Imcms.getServices().getMailService().sendMail(mail);

            } catch (Exception e) {
                logger.error(String.format(
                        "Failed to send password reset URL to the user %s, using e-mail address %s.",
                        user, emailAddress),
                        e);
            }
        });
    }

    @Override
    public void destroy() {
        super.destroy();
        emailSender.shutdownNow();
    }


    // Ops
    public enum Op {
        REQUEST_RESET,
        SEND_RESET_URL,
        RESET,
        SAVE_NEW_PASSWORD,
        UNDEFINED
    }

    public static abstract class UserIdentity {
        public static UserIdentity of(final UserDomainObject user, final String identity) {
            return new UserIdentity() {
                @Override
                public UserDomainObject user() {
                    return user;
                }

                @Override
                public String identity() {
                    return identity;
                }
            };
        }

        public abstract UserDomainObject user();

        public abstract String identity();
    }
}
