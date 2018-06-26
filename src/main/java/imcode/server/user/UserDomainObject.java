package imcode.server.user;

import com.imcode.imcms.domain.exception.DocumentNotExistException;
import com.imcode.imcms.mapping.DocGetterCallback;
import com.imcode.imcms.persistence.entity.Meta;
import com.imcode.imcms.persistence.entity.Meta.Permission;
import imcode.server.Imcms;
import imcode.server.LanguageMapper;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.RoleIdToDocumentPermissionSetTypeMappings;
import imcode.server.document.TemplateGroupDomainObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.functors.NotPredicate;
import org.apache.commons.lang.UnhandledException;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class UserDomainObject implements Cloneable, Serializable {

    public static final int DEFAULT_USER_ID = 2;
    private final DocGetterCallback docGetterCallback = new DocGetterCallback(this);
    protected volatile int id;
    protected volatile RoleIds userAdminRoleIds = new RoleIds();
    private volatile String loginName = "";

    private volatile String password;

    private volatile String firstName = "";

    private volatile String lastName = "";

    private volatile String title = "";
    private volatile String company = "";
    private volatile String address = "";
    private volatile String city = "";
    private volatile String zip = "";
    private volatile String country = "";

    private volatile String province = "";

    private volatile String emailAddress = "";

    private volatile boolean active = true;

    private volatile Date createDate;

    private volatile String languageIso639_2;

    private volatile TemplateGroupDomainObject templateGroup;

    private volatile boolean imcmsExternal;

    private volatile HashSet<PhoneNumber> phoneNumbers = new HashSet<>();

    private volatile RoleIds roleIds = UserDomainObject.createRolesSetWithUserRole();
    /**
     * Http session id.
     */
    private volatile String sessionId;
    private volatile boolean authenticatedByIp;

    /**
     * @since 4.0.7
     */
    private volatile PasswordType passwordType = PasswordType.UNENCRYPTED;
    /**
     * @since 4.0.7
     */
    private volatile PasswordReset passwordReset = null;

    public UserDomainObject() {
    }

    public UserDomainObject(int id) {
        this.id = id;
    }

    private static RoleIds createRolesSetWithUserRole() {
        RoleIds newRoleIds = new RoleIds();
        newRoleIds.add(RoleId.USERS);

        return newRoleIds;
    }

    @Override
    public UserDomainObject clone() {
        try {
            UserDomainObject clone = (UserDomainObject) super.clone();
            clone.roleIds = (RoleIds) roleIds.clone();
            clone.userAdminRoleIds = (RoleIds) userAdminRoleIds.clone();
            clone.phoneNumbers = (HashSet) phoneNumbers.clone();

            return clone;
        } catch (CloneNotSupportedException e) {
            throw new UnhandledException(e);
        }
    }

    /**
     * get user-id
     */
    public int getId() {
        return id;
    }

    /**
     * set user-id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * get login name (username)
     */
    public String getLoginName() {
        return loginName;
    }

    /**
     * set login name (username)
     */
    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    /**
     * get full name
     */
    public String getFullName() {
        return getFirstName() + " " + getLastName();
    }

    /**
     * get first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * set first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * get last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * set last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * get title
     */
    public String getTitle() {
        return title;
    }

    /**
     * set title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * get company
     */
    public String getCompany() {
        return company;
    }

    /**
     * set company
     */
    public void setCompany(String company) {
        this.company = company;
    }

    /**
     * get address
     */
    public String getAddress() {
        return address;
    }

    /**
     * set address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * get city
     */
    public String getCity() {
        return city;
    }

    /**
     * set city
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * get zip
     */
    public String getZip() {
        return zip;
    }

    /**
     * set zip
     */
    public void setZip(String zip) {
        this.zip = zip;
    }

    /**
     * get country
     */
    public String getCountry() {
        return country;
    }

    /**
     * set country
     */
    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    /**
     * Return the users e-mail address
     */
    public String getEmailAddress() {
        return emailAddress;
    }

    /**
     * Set the users e-mail address
     */
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    /**
     * @return document getter callback associated with this user.
     */
    public DocGetterCallback getDocGetterCallback() {
        return docGetterCallback;
    }

    /**
     * Get the users workphone
     */
    @Deprecated
    public String getWorkPhone() {
        return getFirstPhoneNumberOfTypeAsString(PhoneNumberType.WORK);
    }

    /**
     * Set the users workphone
     *
     * @deprecated Use {@link #addPhoneNumber(PhoneNumber)}
     */
    public void setWorkPhone(String workphone) {
        replacePhoneNumbersOfType(workphone, PhoneNumberType.WORK);
    }

    private String getFirstPhoneNumberOfTypeAsString(PhoneNumberType phoneNumberType) {
        PhoneNumber firstPhoneNumberOfType = getFirstPhoneNumberOfType(phoneNumberType);
        String number = null;
        if (null != firstPhoneNumberOfType) {
            number = firstPhoneNumberOfType.getNumber();
        }
        return number;
    }

    private PhoneNumber getFirstPhoneNumberOfType(PhoneNumberType phoneNumberType) {
        Collection<PhoneNumber> phoneNumbersOfType = getPhoneNumbersOfType(phoneNumberType);
        return phoneNumbersOfType.size() == 0 ? null : phoneNumbersOfType.iterator().next();
    }

    public Set<PhoneNumber> getPhoneNumbersOfType(final PhoneNumberType phoneNumberType) {
        return new HashSet<PhoneNumber>(CollectionUtils.select(phoneNumbers, new PhoneNumberOfTypePredicate(phoneNumberType)));
    }

    public void replacePhoneNumbersOfType(String number, PhoneNumberType type) {
        removePhoneNumbersOfType(type);
        addPhoneNumber(new PhoneNumber(number, type));
    }

    private void removePhoneNumbersOfType(PhoneNumberType phoneNumberType) {
        CollectionUtils.filter(phoneNumbers, new NotPredicate(new PhoneNumberOfTypePredicate(phoneNumberType)));
    }

    /**
     * Get the users mobilephone
     *
     * @deprecated Use {@link #getPhoneNumbersOfType(PhoneNumberType)}
     */
    public String getMobilePhone() {
        return getFirstPhoneNumberOfTypeAsString(PhoneNumberType.MOBILE);
    }

    /**
     * Set the users mobilephone
     *
     * @deprecated Use {@link #addPhoneNumber(PhoneNumber)}
     */
    public void setMobilePhone(String mobilephone) {
        replacePhoneNumbersOfType(mobilephone, PhoneNumberType.MOBILE);
    }

    /**
     * Get the users homephone
     *
     * @deprecated Use {@link #getPhoneNumbersOfType(PhoneNumberType)}
     */
    public String getHomePhone() {
        return getFirstPhoneNumberOfTypeAsString(PhoneNumberType.HOME);
    }

    /**
     * Set the users homephone
     *
     * @deprecated Use {@link #addPhoneNumber(PhoneNumber)}
     */
    public void setHomePhone(String homephone) {
        replacePhoneNumbersOfType(homephone, PhoneNumberType.HOME);
    }

    /**
     * Get the users faxphone
     *
     * @deprecated Use {@link #getPhoneNumbersOfType(PhoneNumberType)}
     */
    public String getFaxPhone() {
        return getFirstPhoneNumberOfTypeAsString(PhoneNumberType.FAX);
    }

    /**
     * Set the users faxpohne
     *
     * @deprecated Use {@link #addPhoneNumber(PhoneNumber)}
     */
    public void setFaxPhone(String faxphone) {
        replacePhoneNumbersOfType(faxphone, PhoneNumberType.FAX);
    }

    /**
     * Get the users otherphone
     *
     * @deprecated Use {@link #getPhoneNumbersOfType(PhoneNumberType)}
     */
    public String getOtherPhone() {
        return getFirstPhoneNumberOfTypeAsString(PhoneNumberType.OTHER);
    }

    /**
     * Set the users otherpohne
     *
     * @deprecated Use {@link #addPhoneNumber(PhoneNumber)}
     */
    public void setOtherPhone(String otherphone) {
        replacePhoneNumbersOfType(otherphone, PhoneNumberType.OTHER);
    }

    /**
     * Check whether the user is allowed to log in
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Set whether the user is allowed to log in
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * get create_date
     */
    public Date getCreateDate() {
        return (Date) (null == createDate ? null : createDate.clone());
    }

    /**
     * set create_date
     *
     * @param createDate
     */
    public void setCreateDate(Date createDate) {
        this.createDate = (Date) createDate.clone();
    }

    /**
     * get template group
     */
    public TemplateGroupDomainObject getTemplateGroup() {
        return templateGroup;
    }

    /**
     * set template group
     */
    public void setTemplateGroup(TemplateGroupDomainObject templateGroup) {
        this.templateGroup = templateGroup;
    }

    public String getLanguage() {
        return LanguageMapper.convert639_2to639_1(languageIso639_2);
    }

    /**
     * Return the users language
     */
    public String getLanguageIso639_2() {
        return languageIso639_2;
    }

    /**
     * Set the users language
     */
    public void setLanguageIso639_2(String languageIso639_2) {
        this.languageIso639_2 = languageIso639_2;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public boolean isImcmsExternal() {
        return imcmsExternal;
    }

    public void setImcmsExternal(boolean imcmsExternal) {
        this.imcmsExternal = imcmsExternal;
    }

    public void addRoleId(RoleId role) {
        roleIds.add(role);
    }

    public void removeRoleId(RoleId roleId) {
        if (!RoleId.USERS.equals(roleId)) {
            roleIds.remove(roleId);
        }
    }

    public boolean hasRoleId(RoleId roleId) {
        return roleIds.contains(roleId);
    }

    public RoleId[] getRoleIds() {
        return roleIds.toArray();
    }

    public void setRoleIds(RoleId[] roleIds) {
        this.roleIds = new RoleIds(roleIds);
        this.roleIds.add(RoleId.USERS);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserDomainObject)) {
            return false;
        }

        final UserDomainObject userDomainObject = (UserDomainObject) o;

        return id == userDomainObject.id;

    }

    public int hashCode() {
        return id;
    }

    public boolean isDefaultUser() {
        return DEFAULT_USER_ID == id;
    }

    public boolean isSuperAdmin() {
        return hasRoleId(RoleId.SUPERADMIN);
    }

    public boolean isUserAdminAndCanEditAtLeastOneRole() {
        return isUserAdmin() && !userAdminRoleIds.isEmpty();
    }

    public boolean canEdit(DocumentDomainObject document) {
        return hasAtLeastPermissionSetIdOn(Permission.RESTRICTED_2, document);
    }

    public boolean canAccess(DocumentDomainObject document) {
        return hasAtLeastPermissionSetIdOn(Permission.VIEW, document);
    }

    public boolean isSuperAdminOrHasFullPermissionOn(DocumentDomainObject document) {
        return isSuperAdminOrHasAtLeastPermissionSetIdOn(Permission.EDIT, document);
    }

    public String toString() {
        return "(user " + id + " \"" + loginName + "\")";
    }

    public void addPhoneNumber(PhoneNumber number) {
        phoneNumbers.add(number);
    }

    public void removePhoneNumber(PhoneNumber number) {
        phoneNumbers.remove(number);
    }

    public void removePhoneNumbers() {
        phoneNumbers.clear();
    }

    public boolean isSuperAdminOrHasAtLeastPermissionSetIdOn(Permission documentPermissionSetType, DocumentDomainObject document) {
        return isSuperAdmin() || hasAtLeastPermissionSetIdOn(documentPermissionSetType, document);
    }

    /**
     * Returns most privileged permission set type for the provided doc.
     * <p>
     * If doc is null returns {@link Permission#NONE}
     * If user is in a SUPER_ADMIN role returns {@link Permission#EDIT}
     * Otherwise searches for most privileged perm set in the intersection of user roles and doc's roles.
     *
     * @param document
     * @return most privileged permission set for the provided doc.
     */
    public Permission getDocumentPermissionSetTypeFor(DocumentDomainObject document) {
        if (null == document)
            return Permission.NONE;

        if (isSuperAdmin())
            return Permission.EDIT;

        RoleIdToDocumentPermissionSetTypeMappings roleIdsMappedToDocumentPermissionSetTypes =
                document.getRoleIdsMappedToDocumentPermissionSetTypes();

        Permission mostPrivilegedPermissionSetIdFoundYet =
                Permission.NONE;

        for (RoleId roleId : getRoleIds()) {
            Permission documentPermissionSetType =
                    roleIdsMappedToDocumentPermissionSetTypes.getPermissionSetTypeForRole(roleId);

            if (documentPermissionSetType.isMorePrivilegedThan(mostPrivilegedPermissionSetIdFoundYet)) {
                mostPrivilegedPermissionSetIdFoundYet = documentPermissionSetType;

                if (mostPrivilegedPermissionSetIdFoundYet == Permission.EDIT) {
                    break;
                }
            }
        }

        return mostPrivilegedPermissionSetIdFoundYet;
    }

    public boolean hasAtLeastPermissionSetIdOn(Permission leastPrivilegedPermissionSetIdWanted,
                                               DocumentDomainObject document) {
        Permission usersDocumentPermissionSetType = getDocumentPermissionSetTypeFor(document);
        return usersDocumentPermissionSetType.isAtLeastAsPrivilegedAs(leastPrivilegedPermissionSetIdWanted);
    }

    public boolean canAccessAdminPages() {
        RolePermissionDomainObject rolePermissionToAccessAdminPages = RoleDomainObject.ADMIN_PAGES_PERMISSION;
        return isSuperAdmin() || isUserAdminAndCanEditAtLeastOneRole() || hasRoleWithPermission(rolePermissionToAccessAdminPages);
    }

    public boolean hasRoleWithPermission(RolePermissionDomainObject rolePermission) {
        ImcmsAuthenticatorAndUserAndRoleMapper imcmsAuthenticatorAndUserAndRoleMapper = Imcms.getServices().getImcmsAuthenticatorAndUserAndRoleMapper();
        RoleId[] roleReferencesArray = roleIds.toArray();
        for (int i = 0; i < roleReferencesArray.length; i++) {
            RoleId roleId = roleReferencesArray[i];
            if (imcmsAuthenticatorAndUserAndRoleMapper.getRole(roleId).hasPermission(rolePermission)) {
                return true;
            }
        }
        return false;
    }

    public boolean canSeeDocumentWhenEditingMenus(DocumentDomainObject document) {
        return document.isLinkedForUnauthorizedUsers() || canAccess(document);
    }

    public Set<PhoneNumber> getPhoneNumbers() {
        return Collections.unmodifiableSet(phoneNumbers);
    }

    /**
     * @return roles this user can administrate if he is Useradmin.
     */
    public RoleId[] getUserAdminRoleIds() {
        return userAdminRoleIds.toArray();
    }

    /**
     * @param userAdminRoleReferences roles this user can administrate if he is Useradmin.
     */
    public void setUserAdminRolesIds(RoleId[] userAdminRoleReferences) {
        userAdminRoleIds = new RoleIds(userAdminRoleReferences);
    }

    public boolean isUserAdminAndNotSuperAdmin() {
        return isUserAdmin() && !isSuperAdmin();
    }

    public boolean canEditRolesFor(UserDomainObject editedUser) {
        return isSuperAdmin() || canEditAsUserAdmin(editedUser) && !equals(editedUser);
    }

    public void removeUserAdminRoleId(RoleId role) {
        userAdminRoleIds.remove(role);
    }

    public boolean canEdit(UserDomainObject editedUser) {
        return equals(editedUser) || isSuperAdmin() || canEditAsUserAdmin(editedUser);
    }

    public boolean canEditAsUserAdmin(UserDomainObject editedUser) {
        return isUserAdminAndNotSuperAdmin() && (editedUser.isNew() || canEditRolesAccordingToUserAdminRoles(editedUser));
    }

    public boolean canEditRolesAccordingToUserAdminRoles(UserDomainObject editedUser) {
        return CollectionUtils.containsAny(editedUser.roleIds.asSet(), userAdminRoleIds.asSet());
    }

    public boolean isUserAdmin() {
        return hasRoleId(RoleId.USERADMIN);
    }

    public boolean isNew() {
        return 0 == id;
    }

    public boolean hasAdminPanelForDocument(DocumentDomainObject document) {
        return !(null == document || !(canEdit(document) || isUserAdminAndCanEditAtLeastOneRole() || canAccessAdminPages()));
    }

    public boolean hasUserAccessToDoc(Meta meta) {
        if (meta == null) throw new DocumentNotExistException();

        if (meta.getLinkedForUnauthorizedUsers()) {
            return true;
        }

        final Map<Integer, Permission> docPermissions = meta.getRoleIdToPermission();

        return Arrays.stream(getRoleIds())
                .map(RoleId::getRoleId)
                .map(docPermissions::get)
                .filter(Objects::nonNull)
                .anyMatch(documentPermissionSetTypeDomainObject
                        -> documentPermissionSetTypeDomainObject.isAtLeastAsPrivilegedAs(Permission.VIEW));
    }

    /**
     * @return if this user was authenticated by IP.
     * @see imcode.server.Config#isDenyMultipleUserLogin()
     * @see com.imcode.imcms.servlet.ImcmsSetupFilter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    public boolean isAuthenticatedByIp() {
        return authenticatedByIp;
    }

    public void setAuthenticatedByIp(boolean authenticatedByIp) {
        this.authenticatedByIp = authenticatedByIp;
    }

    /**
     * @since 4.0.7
     */
    public boolean isPasswordEncrypted() {
        return passwordType == PasswordType.ENCRYPTED;
    }

    /**
     * @since 4.0.7
     */
    public PasswordReset getPasswordReset() {
        return passwordReset;
    }

    /**
     * @since 4.0.7
     */
    public boolean hasPasswordReset() {
        return passwordReset != null;
    }

    /**
     * Returns password.
     * The password might be a plain text or encrypted.
     * Use {@link #isPasswordEncrypted()} to test if password is encrypted.
     *
     * @since 4.0.7
     */
    public String getPassword() {
        return password;
    }

    /**
     * Assign a new unencrypted password to the user and clears password reset (if present).
     *
     * @param password plain text password.
     * @see #setPassword(String, PasswordType)
     * @since 4.0.7
     */
    public void setPassword(String password) {
        setPassword(password, PasswordType.UNENCRYPTED);
        this.passwordReset = null;
    }

    /**
     * @since 4.0.7
     */
    void setPassword(String password, PasswordType passwordType) {
        this.password = password;
        this.passwordType = passwordType;
    }

    /**
     * @since 4.0.7
     */
    void setPasswordReset(String resetId, long time) {
        this.passwordReset = new PasswordReset(resetId, time);
    }

    public boolean isAdmin() {
        return isSuperAdmin() || isUserAdmin();
    }

    /**
     * @since 4.0.7
     */
    enum PasswordType {
        UNENCRYPTED, ENCRYPTED
    }

    //------------------------------------------------------------------------------------------------------------------
    // The following package private methods are used internally by ImcmsAuthenticatorAndUserAndRoleMapper
    //------------------------------------------------------------------------------------------------------------------

    /**
     * @since 4.0.7
     */
    public static final class PasswordReset {
        private final String id;
        private final long time;

        private PasswordReset(String id, long time) {
            this.id = id;
            this.time = time;
        }

        public String getId() {
            return id;
        }

        public long getTime() {
            return time;
        }

        @Override
        public String toString() {
            return String.format("User.PasswordReset(id=%s, time=%s)", id, time);
        }
    }

    private static class PhoneNumberOfTypePredicate implements Predicate {
        private final PhoneNumberType phoneNumberType;

        PhoneNumberOfTypePredicate(PhoneNumberType phoneNumberType) {
            this.phoneNumberType = phoneNumberType;
        }

        public boolean evaluate(Object object) {
            PhoneNumber phoneNumber = (PhoneNumber) object;
            return phoneNumber.getType().equals(phoneNumberType);
        }
    }
}
