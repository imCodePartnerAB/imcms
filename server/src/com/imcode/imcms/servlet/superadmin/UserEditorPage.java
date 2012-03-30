package com.imcode.imcms.servlet.superadmin;

import com.imcode.imcms.api.NoPermissionException;
import com.imcode.imcms.flow.DispatchCommand;
import com.imcode.imcms.flow.OkCancelPage;
import com.imcode.imcms.util.l10n.LocalizedMessage;
import com.imcode.imcms.mapping.NoPermissionInternalException;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.user.PhoneNumber;
import imcode.server.user.PhoneNumberType;
import imcode.server.user.RoleDomainObject;
import imcode.server.user.RoleId;
import imcode.server.user.UserDomainObject;
import imcode.util.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserEditorPage extends OkCancelPage {
    public static final String REQUEST_PARAMETER__LOGIN_NAME = "login_name";
    public static final String REQUEST_PARAMETER__PASSWORD1 = "password1";
    public static final String REQUEST_PARAMETER__FIRST_NAME = "first_name";
    public static final String REQUEST_PARAMETER__LAST_NAME = "last_name";
    public static final String REQUEST_PARAMETER__TITLE = "title";
    public static final String REQUEST_PARAMETER__COMPANY = "company";
    public static final String REQUEST_PARAMETER__ADDRESS = "address";
    public static final String REQUEST_PARAMETER__CITY = "city";
    public static final String REQUEST_PARAMETER__ZIP = "zip";
    public static final String REQUEST_PARAMETER__COUNTRY = "country";
    public static final String REQUEST_PARAMETER__DISTRICT = "county";
    public static final String REQUEST_PARAMETER__EMAIL = "email";
    public static final String REQUEST_PARAMETER__LANGUAGE = "lang_id";
    public static final String REQUEST_PARAMETER__ACTIVE = "active";
    public static final String REQUEST_PARAMETER__PASSWORD2 = "password2";
    public static final String REQUEST_PARAMETER__ROLE_IDS = "role_ids";
    public static final String REQUEST_PARAMETER__USER_ADMIN_ROLE_IDS = "user_admin_role_ids";

    public static final String REQUEST_PARAMETER__ADD_PHONE_NUMBER = "add_phone_number";
    public static final String REQUEST_PARAMETER__EDIT_PHONE_NUMBER = "edit_phone_number";
    public static final String REQUEST_PARAMETER__REMOVE_PHONE_NUMBER = "delete_phone_number";

    public static final String REQUEST_PARAMETER__PHONE_NUMBER_TYPE_ID = "phone_number_type_id";
    public static final String REQUEST_PARAMETER__EDITED_PHONE_NUMBER = "edited_phone_number";
    public static final String REQUEST_PARAMETER__SELECTED_PHONE_NUMBER = "selected_phone_number";

    private static final LocalizedMessage ERROR__PASSWORDS_DID_NOT_MATCH = new LocalizedMessage("error/passwords_did_not_match");
    private static final LocalizedMessage ERROR__PASSWORD_LENGTH = new LocalizedMessage("error/password_length");
    private static final LocalizedMessage ERROR__PASSWORD_TOO_WEAK = new LocalizedMessage("error/password_too_weak");
    private static final LocalizedMessage ERROR__EDITED_USER_MUST_HAVE_AT_LEAST_ONE_ROLE = new LocalizedMessage("error/user_must_have_at_least_one_role");

    private UserDomainObject editedUser;
    private UserDomainObject uneditedUser;
    private PhoneNumber currentPhoneNumber = new PhoneNumber("", PhoneNumberType.OTHER);
    private LocalizedMessage errorMessage;
    private static final int MAXIMUM_PASSWORD_LENGTH = 15;
    private static final int MINIMUM_PASSWORD_LENGTH = 4;

    public UserEditorPage(UserDomainObject user, DispatchCommand okDispatchCommand,
                          DispatchCommand cancelDispatchCommand) {
        super(okDispatchCommand, cancelDispatchCommand);
        editedUser = user;
        uneditedUser = (UserDomainObject) user.clone() ;
    }

    protected void updateFromRequest(HttpServletRequest request) {
        updateUserFromRequest(request);
    }

    private void updateUserFromRequest(HttpServletRequest request) {
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
        editedUser.setEmailAddress(request.getParameter(REQUEST_PARAMETER__EMAIL));
        editedUser.setLanguageIso639_2(request.getParameter(REQUEST_PARAMETER__LANGUAGE));
        editedUser.setActive(null != request.getParameter(REQUEST_PARAMETER__ACTIVE));

        updateUserPasswordFromRequest(editedUser, request);

        updateUserRolesFromRequest(request);
        updateUserAdminRolesFromRequest(request);
    }

    private void updateUserAdminRolesFromRequest(HttpServletRequest request) {
        if ( Utility.getLoggedOnUser(request).isSuperAdmin() && editedUser.isUserAdmin() ) {
            editedUser.setUserAdminRolesIds(getRoleIdsFromRequestParameterValues(request, REQUEST_PARAMETER__USER_ADMIN_ROLE_IDS));
            editedUser.removeUserAdminRoleId(RoleId.SUPERADMIN) ;
            editedUser.removeUserAdminRoleId(RoleId.USERADMIN) ;
        }
    }

    private RoleId[] getRoleIdsFromRequestParameterValues(HttpServletRequest request, String requestParameter) {
        Set roleIds = getRoleIdsSetFromRequestParameterValues(request, requestParameter);
        return (RoleId[]) roleIds.toArray(new RoleId[roleIds.size()]);
    }

    private Set getRoleIdsSetFromRequestParameterValues(HttpServletRequest request, String requestParameter) {
        Set roleIds = new HashSet();
        String[] roleIdStrings = request.getParameterValues(requestParameter);
        if ( null != roleIdStrings ) {
            for ( int i = 0; i < roleIdStrings.length; i++ ) {
                RoleId roleId = new RoleId(Integer.parseInt(roleIdStrings[i]));
                roleIds.add(roleId);
            }
        }
        return roleIds;
    }

    private ImcmsServices getImcmsServices() {
        return Imcms.getServices() ;
    }

    private void updateUserRolesFromRequest(HttpServletRequest request) {
        UserDomainObject loggedOnUser = Utility.getLoggedOnUser(request);
        if ( loggedOnUser.canEditRolesFor(uneditedUser) ) {
            Set roleIdsSetFromRequest = getRoleIdsSetFromRequestParameterValues(request, REQUEST_PARAMETER__ROLE_IDS);
            RoleId[] userRoleIdsArray ;
            if (loggedOnUser.isUserAdminAndNotSuperAdmin()) {
                List userAdminRoleIds = Arrays.asList(loggedOnUser.getUserAdminRoleIds());
                roleIdsSetFromRequest.retainAll(userAdminRoleIds) ;

                Set userRoleIds = new HashSet(Arrays.asList(editedUser.getRoleIds())) ;
                userRoleIds.removeAll(userAdminRoleIds) ;
                userRoleIds.addAll(roleIdsSetFromRequest) ;
                userRoleIdsArray = (RoleId[]) userRoleIds.toArray(new RoleId[userRoleIds.size()]);
            } else {
                userRoleIdsArray = (RoleId[]) roleIdsSetFromRequest.toArray(new RoleId[roleIdsSetFromRequest.size()]);
            }
            editedUser.setRoleIds(userRoleIdsArray);
        }
    }

    private void updateUserPasswordFromRequest(UserDomainObject user, HttpServletRequest request) {
        String password1 = getPassword1FromRequest(request);
        if ( StringUtils.isNotBlank(password1) ) {
            if ( !passwordPassesLengthRequirements(password1) ) {
                errorMessage = ERROR__PASSWORD_LENGTH;
            } else if ( !passwordsMatch(request) ) {
                errorMessage = ERROR__PASSWORDS_DID_NOT_MATCH;
            } else if ( !user.isDefaultUser() && password1.equalsIgnoreCase(user.getLoginName()) ) {
                errorMessage = ERROR__PASSWORD_TOO_WEAK ;
            } else {
                user.setPassword(password1);
            }
        }
    }

    /**
     * @param login
     * @param password
     * @param passwordCheck
     * @since 4.0.7
     *
     * @return
     */
    public static LocalizedMessage validatePassword(String login, String password, String passwordCheck) {
        return StringUtils.isBlank(password)
                ? ERROR__PASSWORD_LENGTH
                : !password.equals(passwordCheck)
                    ? ERROR__PASSWORDS_DID_NOT_MATCH
                    : login.equalsIgnoreCase(password)
                        ? ERROR__PASSWORD_TOO_WEAK
                        : null;
    }

    private boolean passwordPassesLengthRequirements(String password1) {
        return password1.length() >= MINIMUM_PASSWORD_LENGTH
               && password1.length() <= MAXIMUM_PASSWORD_LENGTH;
    }

    public String getPath(HttpServletRequest request) {
        UserDomainObject loggedOnUser = Utility.getLoggedOnUser(request);
        return "/imcms/" + loggedOnUser.getLanguageIso639_2() + "/jsp/usereditor.jsp";
    }

    protected void dispatchOther(HttpServletRequest request,
                                 HttpServletResponse response) throws IOException, ServletException {
        PhoneNumber editedPhoneNumber = getEditedPhoneNumberFromRequest(request);
        PhoneNumber selectedPhoneNumber = getSelectedPhoneNumberFromRequest(request);

        if ( null != request.getParameter(REQUEST_PARAMETER__ADD_PHONE_NUMBER) && null != editedPhoneNumber ) {
            if ( !editedPhoneNumber.equals(currentPhoneNumber) ) {
                editedUser.removePhoneNumber(currentPhoneNumber);
            }
            editedUser.removePhoneNumber(editedPhoneNumber);
            editedUser.addPhoneNumber(editedPhoneNumber);
            currentPhoneNumber = new PhoneNumber("", PhoneNumberType.OTHER);
        } else if ( null != request.getParameter(REQUEST_PARAMETER__REMOVE_PHONE_NUMBER)
                    && null != selectedPhoneNumber ) {
            editedUser.removePhoneNumber(selectedPhoneNumber);
            currentPhoneNumber = selectedPhoneNumber;
        } else if ( null != request.getParameter(REQUEST_PARAMETER__EDIT_PHONE_NUMBER)
                    && null != selectedPhoneNumber ) {
            currentPhoneNumber = selectedPhoneNumber;
        }
        forward(request, response);
    }

    protected void dispatchOk(HttpServletRequest request,
                              HttpServletResponse response) throws IOException, ServletException {
        if ( null == errorMessage ) {
            if ( StringUtils.isBlank(editedUser.getPassword()) ) {
                errorMessage = ERROR__PASSWORD_LENGTH;
            } else {
                boolean editedUserHasOnlyTheUsersRole = 1 == editedUser.getRoleIds().length;
                UserDomainObject loggedOnUser = Utility.getLoggedOnUser(request);
                if (editedUserHasOnlyTheUsersRole || loggedOnUser.isUserAdminAndNotSuperAdmin()
                                                     && !loggedOnUser.canEditRolesAccordingToUserAdminRoles(editedUser) ) {
                    errorMessage = ERROR__EDITED_USER_MUST_HAVE_AT_LEAST_ONE_ROLE ;
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
        if ( StringUtils.isNotBlank(editedPhoneNumberString) ) {
            int editedPhoneNumberTypeId = Integer.parseInt(request.getParameter(REQUEST_PARAMETER__PHONE_NUMBER_TYPE_ID));
            PhoneNumberType editedPhoneNumberType = PhoneNumberType.getPhoneNumberTypeById(editedPhoneNumberTypeId);
            editedPhoneNumber = new PhoneNumber(editedPhoneNumberString, editedPhoneNumberType);
        }
        return editedPhoneNumber;
    }

    private PhoneNumber getSelectedPhoneNumberFromRequest(HttpServletRequest request) {
        PhoneNumber selectedPhoneNumber = null;
        String selectedPhoneNumberString = request.getParameter(REQUEST_PARAMETER__SELECTED_PHONE_NUMBER);
        if ( StringUtils.isNotBlank(selectedPhoneNumberString) ) {
            Matcher matcher = Pattern.compile("(\\d+) (.*)").matcher(selectedPhoneNumberString);
            if ( matcher.matches() ) {
                int selectedPhoneNumberTypeId = Integer.parseInt(matcher.group(1));
                PhoneNumberType selectedPhoneNumberType = PhoneNumberType.getPhoneNumberTypeById(selectedPhoneNumberTypeId);
                selectedPhoneNumber = new PhoneNumber(matcher.group(2), selectedPhoneNumberType);
            }
        }
        return selectedPhoneNumber;
    }

    public UserDomainObject getEditedUser() {
        return editedUser;
    }

    public String createLanguagesHtmlOptionList(UserDomainObject user,
                                                UserDomainObject userToChange) {
        return getImcmsServices().getLanguageMapper().createLanguagesOptionList(user, userToChange.getLanguageIso639_2());
    }

    public String createPhoneTypesHtmlOptionList(final UserDomainObject loggedOnUser, PhoneNumberType selectedType) {
        return Html.createOptionList(Arrays.asList(PhoneNumberType.getAllPhoneNumberTypes()), selectedType, new ToStringPairTransformer() {
            public String[] transformToStringPair(Object object) {
                PhoneNumberType phoneType = (PhoneNumberType) object;
                return new String[] { "" + phoneType.getId(), phoneType.getName().toLocalizedString(loggedOnUser) };
            }
        });
    }

    public PhoneNumber getCurrentPhoneNumber() {
        return currentPhoneNumber;
    }

    public String getUserPhoneNumbersHtmlOptionList(final HttpServletRequest request) {
        Set phoneNumbers = editedUser.getPhoneNumbers();
        return Html.createOptionList(phoneNumbers, currentPhoneNumber, new ToStringPairTransformer() {
            protected String[] transformToStringPair(Object object) {
                PhoneNumber phoneNumber = (PhoneNumber) object;
                return new String[] { phoneNumber.getType().getId() + " " + phoneNumber.getNumber(), "("
                                                                                                     + phoneNumber.getType().getName().toLocalizedString(request)
                                                                                                     + ") "
                                                                                                     + phoneNumber.getNumber() };
            }
        });
    }

    public LocalizedMessage getErrorMessage() {
        return errorMessage;
    }

    public String createRolesHtmlOptionList(HttpServletRequest request) {
        UserDomainObject loggedOnUser = Utility.getLoggedOnUser(request) ;
        RoleDomainObject[] roles = loggedOnUser.isUserAdminAndNotSuperAdmin() ? getRoles(loggedOnUser.getUserAdminRoleIds()) : getAllRolesExceptUsersRole();
        RoleDomainObject[] usersRoles = getRoles(editedUser.getRoleIds());
        return createRolesHtmlOptionList(roles, usersRoles);
    }

    private RoleDomainObject[] getRoles(RoleId[] roleIds) {
        RoleDomainObject[] roles = new RoleDomainObject[roleIds.length];
        for ( int i = 0; i < roleIds.length; i++ ) {
            roles[i] = Imcms.getServices().getImcmsAuthenticatorAndUserAndRoleMapper().getRole(roleIds[i]) ;
        }
        return roles ;
    }

    private String createRolesHtmlOptionList(RoleDomainObject[] allRoles, RoleDomainObject[] usersRoles) {
        return Html.createOptionList(Arrays.asList(allRoles), new ArraySet(usersRoles), new RoleToStringPairTransformer());
    }

    public String createUserAdminRolesHtmlOptionList() {
        RoleDomainObject[] allRoles = getAllRolesExceptUsersRole();
        Set allRolesSet = new HashSet(Arrays.asList(allRoles)) ;
        CollectionUtils.filter(allRolesSet, new Predicate() {
            public boolean evaluate(Object o) {
                RoleId roleId = ( (RoleDomainObject) o ).getId();
                return !(roleId.equals(RoleId.SUPERADMIN) || roleId.equals(RoleId.USERADMIN)) ; 
            }
        });
        RoleDomainObject[] allUserAdminRoles = (RoleDomainObject[]) allRolesSet.toArray(new RoleDomainObject[allRolesSet.size()]);
        RoleDomainObject[] usersUserAdminRoles = getRoles(editedUser.getUserAdminRoleIds());

        return createRolesHtmlOptionList(allUserAdminRoles, usersUserAdminRoles);
    }

    private RoleDomainObject[] getAllRolesExceptUsersRole() {
        RoleDomainObject[] allRoles = getImcmsServices().getImcmsAuthenticatorAndUserAndRoleMapper().getAllRolesExceptUsersRole();
        Arrays.sort(allRoles);
        return allRoles;
    }

    public void setErrorMessage(LocalizedMessage errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void setOkCommand(DispatchCommand okCommand) {
        this.okCommand = okCommand;
    }

    public UserDomainObject getUneditedUser() {
        return uneditedUser;
    }

    public static class RoleToStringPairTransformer extends ToStringPairTransformer {
        protected String[] transformToStringPair(Object object) {
            RoleDomainObject role = (RoleDomainObject) object;
            return new String[] { "" + role.getId(), role.getName() };
        }
    }

    public void forward(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserDomainObject loggedOnUser = Utility.getLoggedOnUser(request);
        if (!uneditedUser.isNew() && !loggedOnUser.canEdit(uneditedUser)) {
            throw new ShouldHaveCheckedPermissionsEarlierException(new NoPermissionInternalException("User "+loggedOnUser+" does not have the permission to edit "+editedUser));
        }

        super.forward(request, response);
    }
}
