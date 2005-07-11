package com.imcode.imcms.servlet.superadmin;

import com.imcode.imcms.flow.DispatchCommand;
import com.imcode.imcms.flow.OkCancelPage;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.user.UserDomainObject;
import imcode.server.user.PhoneNumberType;
import imcode.server.user.PhoneNumber;
import imcode.server.user.RoleDomainObject;
import imcode.server.user.ImcmsAuthenticatorAndUserAndRoleMapper;
import imcode.util.Utility;
import imcode.util.Html;
import imcode.util.LocalizedMessage;
import imcode.util.ArraySet;
import imcode.util.ToStringPairTransformer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.util.Arrays;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;

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

    public static final String REQUEST_PARAMETER__ADD_PHONE_NUMBER = "add_phone_number";
    public static final String REQUEST_PARAMETER__EDIT_PHONE_NUMBER = "edit_phone_number";
    public static final String REQUEST_PARAMETER__REMOVE_PHONE_NUMBER = "delete_phone_number";

    public static final String REQUEST_PARAMETER__PHONE_NUMBER_TYPE_ID = "phone_number_type_id";
    public static final String REQUEST_PARAMETER__EDITED_PHONE_NUMBER = "edited_phone_number";
    public static final String REQUEST_PARAMETER__SELECTED_PHONE_NUMBER = "selected_phone_number";

    private static final LocalizedMessage ERROR__PASSWORDS_DID_NOT_MATCH = new LocalizedMessage("error/passwords_did_not_match");
    private static final LocalizedMessage ERROR__PASSWORD_LENGTH = new LocalizedMessage("error/password_length");

    private UserDomainObject editedUser;
    private PhoneNumber currentPhoneNumber = new PhoneNumber("", PhoneNumberType.OTHER);
    private LocalizedMessage errorMessage;
    private ImcmsServices services;
    private static final int MAXIMUM_PASSWORD_LENGTH = 15;
    private static final int MINIMUM_PASSWORD_LENGTH = 4;

    public UserEditorPage(UserDomainObject user, DispatchCommand okDispatchCommand,
                          DispatchCommand cancelDispatchCommand) {
        super(okDispatchCommand, cancelDispatchCommand);
        this.editedUser = user;
        services = Imcms.getServices();
    }

    protected void updateFromRequest(HttpServletRequest request) {
        updateUserFromRequest(editedUser, request);
    }

    private void updateUserFromRequest(UserDomainObject user, HttpServletRequest request) {
        errorMessage = null;
        updateUserPasswordFromRequest(user, request);
        user.setLoginName(request.getParameter(REQUEST_PARAMETER__LOGIN_NAME));
        user.setFirstName(request.getParameter(REQUEST_PARAMETER__FIRST_NAME));
        user.setLastName(request.getParameter(REQUEST_PARAMETER__LAST_NAME));
        user.setTitle(request.getParameter(REQUEST_PARAMETER__TITLE));
        user.setCompany(request.getParameter(REQUEST_PARAMETER__COMPANY));
        user.setAddress(request.getParameter(REQUEST_PARAMETER__ADDRESS));
        user.setCity(request.getParameter(REQUEST_PARAMETER__CITY));
        user.setZip(request.getParameter(REQUEST_PARAMETER__ZIP));
        user.setCountry(request.getParameter(REQUEST_PARAMETER__COUNTRY));
        user.setDistrict(request.getParameter(REQUEST_PARAMETER__DISTRICT));
        user.setEmailAddress(request.getParameter(REQUEST_PARAMETER__EMAIL));
        user.setLanguageIso639_2(request.getParameter(REQUEST_PARAMETER__LANGUAGE));
        user.setActive(null != request.getParameter(REQUEST_PARAMETER__ACTIVE));

        updateUserRolesFromRequest(user, request);
    }

    private void updateUserRolesFromRequest(UserDomainObject user, HttpServletRequest request) {
        if ( Utility.getLoggedOnUser(request).isSuperAdmin() ) {
            ImcmsAuthenticatorAndUserAndRoleMapper imcmsAuthenticatorAndUserAndRoleMapper = services.getImcmsAuthenticatorAndUserAndRoleMapper();
            String[] roleIdStrings = request.getParameterValues(REQUEST_PARAMETER__ROLE_IDS);
            user.removeAllRoles();
            for ( int i = 0; i < roleIdStrings.length; i++ ) {
                int roleId = Integer.parseInt(roleIdStrings[i]);
                RoleDomainObject role = imcmsAuthenticatorAndUserAndRoleMapper.getRoleById(roleId);
                user.addRole(role);
            }
        }
    }

    private void updateUserPasswordFromRequest(UserDomainObject user, HttpServletRequest request) {
        String password1 = getPassword1FromRequest(request);
        if ( StringUtils.isNotBlank(password1) ) {
            if ( password1.length() < MINIMUM_PASSWORD_LENGTH
                 || password1.length() > MAXIMUM_PASSWORD_LENGTH ) {
                errorMessage = ERROR__PASSWORD_LENGTH;
            } else if ( !passwordsMatch(request) ) {
                errorMessage = ERROR__PASSWORDS_DID_NOT_MATCH;
            } else {
                user.setPassword(password1);
            }
        }
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
                super.dispatchOk(request, response);
                return ;
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
        return services.getLanguageMapper().createLanguagesOptionList(user, userToChange.getLanguageIso639_2());
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

    public String createRolesHtmlOptionList() {
        RoleDomainObject[] allRoles = Imcms.getServices().getImcmsAuthenticatorAndUserAndRoleMapper().getAllRoles();
        Arrays.sort(allRoles);
        RoleDomainObject[] usersRoles = editedUser.getRoles();
        return Html.createOptionList(Arrays.asList(allRoles), new ArraySet(usersRoles), new ToStringPairTransformer() {
            protected String[] transformToStringPair(Object object) {
                RoleDomainObject role = (RoleDomainObject) object;
                return new String[] { "" + role.getId(), role.getName() };
            }
        });
    }
}
