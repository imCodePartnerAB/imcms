package com.imcode.imcms.servlet.superadmin;

import com.imcode.imcms.controller.exception.NoPermissionInternalException;
import com.imcode.imcms.flow.DispatchCommand;
import com.imcode.imcms.flow.OkCancelPage;
import com.imcode.imcms.util.l10n.LocalizedMessage;
import imcode.server.Imcms;
import imcode.server.ImcmsConstants;
import imcode.server.LanguageMapper;
import imcode.server.user.PhoneNumber;
import imcode.server.user.PhoneNumberType;
import imcode.server.user.RoleDomainObject;
import imcode.server.user.RoleId;
import imcode.server.user.UserDomainObject;
import imcode.util.ShouldHaveCheckedPermissionsEarlierException;
import imcode.util.Utility;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserEditorPage extends OkCancelPage {
    public static final String REQUEST_PARAMETER__PHONE_NUMBER_TYPE_ID = "phone_number_type_id";
    public static final String REQUEST_PARAMETER__EDITED_PHONE_NUMBER = "edited_phone_number";
    private static final String REQUEST_PARAMETER__LOGIN_NAME = "login_name";
    private static final String REQUEST_PARAMETER__PASSWORD1 = "password1";
    private static final String REQUEST_PARAMETER__FIRST_NAME = "first_name";
    private static final String REQUEST_PARAMETER__LAST_NAME = "last_name";
    private static final String REQUEST_PARAMETER__TITLE = "title";
    private static final String REQUEST_PARAMETER__COMPANY = "company";
    private static final String REQUEST_PARAMETER__ADDRESS = "address";
    private static final String REQUEST_PARAMETER__CITY = "city";
    private static final String REQUEST_PARAMETER__ZIP = "zip";
    private static final String REQUEST_PARAMETER__COUNTRY = "country";
    private static final String REQUEST_PARAMETER__DISTRICT = "county";
    private static final String REQUEST_PARAMETER__EMAIL = "email";
    private static final String REQUEST_PARAMETER__LANGUAGE = "lang_id";
    private static final String REQUEST_PARAMETER__ACTIVE = "active";
    private static final String REQUEST_PARAMETER__PASSWORD2 = "password2";
    private static final String REQUEST_PARAMETER__ROLE_IDS = "role_ids";
    private static final String REQUEST_PARAMETER__USER_ADMIN_ROLE_IDS = "user_admin_role_ids";
    private static final String REQUEST_PARAMETER__ADD_PHONE_NUMBER = "add_phone_number";
    private static final String REQUEST_PARAMETER__EDIT_PHONE_NUMBER = "edit_phone_number";
    private static final String REQUEST_PARAMETER__REMOVE_PHONE_NUMBER = "delete_phone_number";
    private static final String REQUEST_PARAMETER__SELECTED_PHONE_NUMBER = "selected_phone_number";
    private static final String REQUEST_PARAMETER__USER_PHONE_NUMBER_TYPE = "user_phone_number_type";
    private static final String REQUEST_PARAMETER__USER_PHONE_NUMBER = "user_phone_number";

    private static final LocalizedMessage ERROR__PASSWORDS_DID_NOT_MATCH = new LocalizedMessage("error/passwords_did_not_match");
    private static final LocalizedMessage ERROR__PASSWORD_LENGTH = new LocalizedMessage("error/password_length");
    private static final LocalizedMessage ERROR__PASSWORD_TOO_WEAK = new LocalizedMessage("error/password_too_weak");
    private static final LocalizedMessage ERROR__EDITED_USER_MUST_HAVE_AT_LEAST_ONE_ROLE = new LocalizedMessage("error/user_must_have_at_least_one_role");

    private static final LocalizedMessage ERROR__EMAIL_IS_EMPTY = new LocalizedMessage("error/email_is_missing");
    private static final LocalizedMessage ERROR__EMAIL_IS_INVALID = new LocalizedMessage("error/email_is_invalid");
    private static final LocalizedMessage ERROR__EMAIL_IS_TAKEN = new LocalizedMessage("error/email_is_taken");
    private static final long serialVersionUID = 8794785851493625993L;
    private UserDomainObject editedUser;
    private UserDomainObject uneditedUser;
    private PhoneNumber currentPhoneNumber = new PhoneNumber("", PhoneNumberType.OTHER);
    private LocalizedMessage errorMessage;

    public UserEditorPage(UserDomainObject user, DispatchCommand okDispatchCommand,
                          DispatchCommand cancelDispatchCommand) {
        super(okDispatchCommand, cancelDispatchCommand);
        editedUser = user;
        uneditedUser = user.clone();
    }

    /**
     * @since 4.0.7
     */
    public static LocalizedMessage validatePassword(String login, String password, String passwordCheck) {
        if (StringUtils.isBlank(password)) return ERROR__PASSWORD_LENGTH;
        else if (Objects.equals(password, passwordCheck))
            return login.equalsIgnoreCase(password) ? ERROR__PASSWORD_TOO_WEAK : null;
        else return ERROR__PASSWORDS_DID_NOT_MATCH;
    }

    private static boolean passwordPassesLengthRequirements(String password1) {
        return (password1.length() >= ImcmsConstants.MINIMUM_PASSWORD_LENGTH)
                && (password1.length() <= ImcmsConstants.MAXIMUM_PASSWORD_LENGTH);
    }

    protected void updateFromRequest(HttpServletRequest request) {
        errorMessage = null;
        editedUser.setLoginName(request.getParameter(REQUEST_PARAMETER__LOGIN_NAME));
        editedUser.setFirstName(request.getParameter(REQUEST_PARAMETER__FIRST_NAME));
        editedUser.setLastName(request.getParameter(REQUEST_PARAMETER__LAST_NAME));
        editedUser.setTitle(request.getParameter(REQUEST_PARAMETER__TITLE));
        editedUser.setCompany(request.getParameter(REQUEST_PARAMETER__COMPANY));
        editedUser.setAddress(request.getParameter(REQUEST_PARAMETER__ADDRESS));
        editedUser.setCity(request.getParameter(REQUEST_PARAMETER__CITY));
        editedUser.setZip(request.getParameter(REQUEST_PARAMETER__ZIP));
        editedUser.setCountry(request.getParameter(REQUEST_PARAMETER__COUNTRY));
        editedUser.setProvince(request.getParameter(REQUEST_PARAMETER__DISTRICT));
        editedUser.setEmailAddress(StringUtils.trimToEmpty(request.getParameter(REQUEST_PARAMETER__EMAIL)));
        editedUser.setLanguageIso639_2(LanguageMapper.convert639_1to639_2(request.getParameter(REQUEST_PARAMETER__LANGUAGE)));
        editedUser.setActive(null != request.getParameter(REQUEST_PARAMETER__ACTIVE));

        updateUserPhones(request);
        updateUserRolesFromRequest(request);
        updateUserAdminRolesFromRequest(request);
        updateUserPasswordFromRequest(editedUser, request);

        if (getErrorMessage() == null) {
            setErrorMessage(validateUserEmail());
        }
    }

    private void updateUserPhones(HttpServletRequest request) {
        final String[] userPhoneNumbers = request.getParameterValues(REQUEST_PARAMETER__USER_PHONE_NUMBER);
        final String[] userPhoneNumberTypes = request.getParameterValues(REQUEST_PARAMETER__USER_PHONE_NUMBER_TYPE);

        if ((userPhoneNumbers == null)
                || (userPhoneNumberTypes == null)
                || (userPhoneNumbers.length <= 0)
                || (userPhoneNumberTypes.length <= 0)
                || (userPhoneNumbers.length != userPhoneNumberTypes.length))
            return;

        editedUser.removePhoneNumbers();

        for (int i = 0; i < userPhoneNumbers.length; i++) {
            try {
                final String userPhoneNumber = userPhoneNumbers[i];
                final int userPhoneType = Integer.parseInt(userPhoneNumberTypes[i]);
                final PhoneNumberType numberType = PhoneNumberType.getPhoneNumberTypeById(userPhoneType);

                editedUser.addPhoneNumber(new PhoneNumber(userPhoneNumber, numberType));
            } catch (Exception e) {
                // TODO: 20.06.18 set errorMessage - wrong phone or phone type
            }
        }
    }

    /**
     * @since 4.0.7
     */
    private LocalizedMessage validateUserEmail() {
        String email = editedUser.getEmailAddress();
        LocalizedMessage msg = null;

        if (email.isEmpty()) {
            msg = ERROR__EMAIL_IS_EMPTY;
        } else if (!Utility.isValidEmail(email)) {
            msg = ERROR__EMAIL_IS_INVALID;
        } else {
            UserDomainObject user = Imcms.getServices().getImcmsAuthenticatorAndUserAndRoleMapper().getUserByEmail(email);
            if (user != null && user.getId() != uneditedUser.getId()) {
                msg = ERROR__EMAIL_IS_TAKEN;
            }
        }

        return msg;
    }

    private void updateUserAdminRolesFromRequest(HttpServletRequest request) {
        if (Utility.getLoggedOnUser(request).isSuperAdmin() || editedUser.isUserAdmin()) {
            editedUser.setUserAdminRolesIds(getRoleIdsFromRequestParameterValues(request));
            editedUser.removeUserAdminRoleId(RoleId.SUPERADMIN);
            editedUser.removeUserAdminRoleId(RoleId.USERADMIN);
        }
    }

    private RoleId[] getRoleIdsFromRequestParameterValues(HttpServletRequest request) {
        return getRoleIdsSetFromRequestParameterValues(request, UserEditorPage.REQUEST_PARAMETER__USER_ADMIN_ROLE_IDS)
                .toArray(new RoleId[0]);
    }

    private Set<RoleId> getRoleIdsSetFromRequestParameterValues(HttpServletRequest request, String requestParameter) {
        Set<RoleId> roleIds = new HashSet<>();
        String[] roleIdStrings = request.getParameterValues(requestParameter);
        if (null != roleIdStrings) {
            for (String roleIdString : roleIdStrings) {
                RoleId roleId = new RoleId(Integer.parseInt(roleIdString));
                roleIds.add(roleId);
            }
        }
        return roleIds;
    }

    private void updateUserRolesFromRequest(HttpServletRequest request) {
        UserDomainObject loggedOnUser = Utility.getLoggedOnUser(request);
        if (loggedOnUser.canEditRolesFor(uneditedUser)) {
            Set<RoleId> roleIdsSetFromRequest = getRoleIdsSetFromRequestParameterValues(request, REQUEST_PARAMETER__ROLE_IDS);
            RoleId[] userRoleIdsArray;
            if (loggedOnUser.isUserAdminAndNotSuperAdmin()) {
                List<RoleId> userAdminRoleIds = Arrays.asList(loggedOnUser.getUserAdminRoleIds());
                roleIdsSetFromRequest.retainAll(userAdminRoleIds);

                Set<RoleId> userRoleIds = new HashSet<>(Arrays.asList(editedUser.getRoleIds()));
                userRoleIds.removeAll(userAdminRoleIds);
                userRoleIds.addAll(roleIdsSetFromRequest);
                userRoleIdsArray = userRoleIds.toArray(new RoleId[0]);
            } else {
                userRoleIdsArray = roleIdsSetFromRequest.toArray(new RoleId[0]);
            }
            editedUser.setRoleIds(userRoleIdsArray);
        }
    }

    private void updateUserPasswordFromRequest(UserDomainObject user, HttpServletRequest request) {
        String password1 = getPassword1FromRequest(request);
        if (StringUtils.isNotBlank(password1)) {
            if (!passwordPassesLengthRequirements(password1)) {
                errorMessage = ERROR__PASSWORD_LENGTH;
            } else if (!passwordsMatch(request)) {
                errorMessage = ERROR__PASSWORDS_DID_NOT_MATCH;
            } else if (!user.isDefaultUser() && password1.equalsIgnoreCase(user.getLoginName())) {
                errorMessage = ERROR__PASSWORD_TOO_WEAK;
            } else {
                user.setPassword(password1);
            }
        }
    }

    public String getPath(HttpServletRequest request) {
        return "/imcms/jsp/usereditor.jsp";
    }

    protected void dispatchOther(HttpServletRequest request,
                                 HttpServletResponse response) throws IOException, ServletException {
        PhoneNumber editedPhoneNumber = getEditedPhoneNumberFromRequest(request);
        PhoneNumber selectedPhoneNumber = getSelectedPhoneNumberFromRequest(request);

        if (null != request.getParameter(REQUEST_PARAMETER__ADD_PHONE_NUMBER) && null != editedPhoneNumber) {
            if (!editedPhoneNumber.equals(currentPhoneNumber)) {
                editedUser.removePhoneNumber(currentPhoneNumber);
            }
            editedUser.removePhoneNumber(editedPhoneNumber);
            editedUser.addPhoneNumber(editedPhoneNumber);
            currentPhoneNumber = new PhoneNumber("", PhoneNumberType.OTHER);
        } else if (null != request.getParameter(REQUEST_PARAMETER__REMOVE_PHONE_NUMBER)
                && null != selectedPhoneNumber)
        {
            editedUser.removePhoneNumber(selectedPhoneNumber);
            currentPhoneNumber = selectedPhoneNumber;
        } else if (null != request.getParameter(REQUEST_PARAMETER__EDIT_PHONE_NUMBER)
                && null != selectedPhoneNumber)
        {
            currentPhoneNumber = selectedPhoneNumber;
        }
        forward(request, response);
    }

    protected void dispatchOk(HttpServletRequest request,
                              HttpServletResponse response) throws IOException, ServletException {
        if (null == errorMessage) {
            if (StringUtils.isBlank(editedUser.getPassword())) {
                errorMessage = ERROR__PASSWORD_LENGTH;
            } else {
                boolean editedUserHasOnlyTheUsersRole = 1 == editedUser.getRoleIds().length;
                UserDomainObject loggedOnUser = Utility.getLoggedOnUser(request);
                if (editedUserHasOnlyTheUsersRole || loggedOnUser.isUserAdminAndNotSuperAdmin()
                        && !loggedOnUser.canEditRolesAccordingToUserAdminRoles(editedUser))
                {
                    errorMessage = ERROR__EDITED_USER_MUST_HAVE_AT_LEAST_ONE_ROLE;
                } else {
                    super.dispatchOk(request, response);
                    return;
                }
            }
        }
        forward(request, response);
    }

    private boolean passwordsMatch(HttpServletRequest request) {
        String password1 = getPassword1FromRequest(request);
        String password2 = request.getParameter(REQUEST_PARAMETER__PASSWORD2);
        return password1.equals(password2);
    }

    private String getPassword1FromRequest(HttpServletRequest request) {
        return request.getParameter(REQUEST_PARAMETER__PASSWORD1);
    }

    private PhoneNumber getEditedPhoneNumberFromRequest(HttpServletRequest request) {
        PhoneNumber editedPhoneNumber = null;
        String editedPhoneNumberString = request.getParameter(REQUEST_PARAMETER__EDITED_PHONE_NUMBER);
        if (StringUtils.isNotBlank(editedPhoneNumberString)) {
            int editedPhoneNumberTypeId = Integer.parseInt(request.getParameter(REQUEST_PARAMETER__PHONE_NUMBER_TYPE_ID));
            PhoneNumberType editedPhoneNumberType = PhoneNumberType.getPhoneNumberTypeById(editedPhoneNumberTypeId);
            editedPhoneNumber = new PhoneNumber(editedPhoneNumberString, editedPhoneNumberType);
        }
        return editedPhoneNumber;
    }

    private PhoneNumber getSelectedPhoneNumberFromRequest(HttpServletRequest request) {
        PhoneNumber selectedPhoneNumber = null;
        String selectedPhoneNumberString = request.getParameter(REQUEST_PARAMETER__SELECTED_PHONE_NUMBER);
        if (StringUtils.isNotBlank(selectedPhoneNumberString)) {
            Matcher matcher = Pattern.compile("(\\d+) (.*)").matcher(selectedPhoneNumberString);
            if (matcher.matches()) {
                int selectedPhoneNumberTypeId = Integer.parseInt(matcher.group(1));
                PhoneNumberType selectedPhoneNumberType = PhoneNumberType.getPhoneNumberTypeById(selectedPhoneNumberTypeId);
                selectedPhoneNumber = new PhoneNumber(matcher.group(2), selectedPhoneNumberType);
            }
        }
        return selectedPhoneNumber;
    }

    private LocalizedMessage getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(LocalizedMessage errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void setOkCommand(DispatchCommand okCommand) {
        this.okCommand = okCommand;
    }

    public void forward(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final UserDomainObject loggedOnUser = Utility.getLoggedOnUser(request);

        if (!uneditedUser.isNew() && !loggedOnUser.canEdit(uneditedUser)) {
            throw new ShouldHaveCheckedPermissionsEarlierException(new NoPermissionInternalException(
                    "User " + loggedOnUser + " does not have the permission to edit " + editedUser
            ));
        }

        if ((editedUser != null) && ((editedUser.getLanguageIso639_2() == null) || editedUser.getLanguageIso639_2().equals(""))) {
            String defaultLanguage = Imcms.getServices().getLanguageMapper().getDefaultLanguage();
            editedUser.setLanguageIso639_2(defaultLanguage);
        }

        request.setAttribute("editedUser", editedUser);
        request.setAttribute("uneditedUser", uneditedUser);
        request.setAttribute("isAdmin", loggedOnUser.isSuperAdmin());
        request.setAttribute("userEditorPage", this);
        request.setAttribute("loggedOnUser", loggedOnUser);
        request.setAttribute("errorMessage", errorMessage);
        request.setAttribute("userLanguage", Imcms.getUser().getLanguage());

        super.forward(request, response);
    }

    public static class RoleToStringPairTransformer implements Function<RoleDomainObject, String[]> {
        public String[] apply(RoleDomainObject role) {
            return new String[]{"" + role.getId(), role.getName()};
        }
    }
}
