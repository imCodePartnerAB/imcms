package imcode.server.user;

import com.imcode.imcms.domain.dto.PasswordResetDTO;
import com.imcode.imcms.domain.dto.UserFormData;
import com.imcode.imcms.domain.exception.DocumentNotExistException;
import com.imcode.imcms.mapping.DocGetterCallback;
import com.imcode.imcms.model.Roles;
import com.imcode.imcms.model.UserData;
import com.imcode.imcms.persistence.entity.Meta;
import com.imcode.imcms.persistence.entity.Meta.Permission;
import imcode.server.Imcms;
import imcode.server.LanguageMapper;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.RoleIdToDocumentPermissionSetTypeMappings;
import imcode.server.document.TemplateGroupDomainObject;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.functors.NotPredicate;
import org.apache.commons.lang.UnhandledException;

import java.io.Serializable;
import java.util.*;

@Getter
@Setter
@SuppressWarnings({"unused", "WeakerAccess"})
public class UserDomainObject extends UserData implements Cloneable, Serializable {

    public static final int DEFAULT_USER_ID = 2;
    private static final long serialVersionUID = -9176465092502055012L;
    private final DocGetterCallback docGetterCallback = new DocGetterCallback(this);
    protected volatile int id;
    private volatile String loginName = "";

    @Getter
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

    private String ref = "";

    private volatile boolean active;

    private volatile Date createDate;

    private volatile String languageIso639_2;

    private volatile TemplateGroupDomainObject templateGroup;

    private volatile boolean imcmsExternal;

	private volatile String oneTimePassword;
	private volatile boolean twoFactoryAuthenticationEnabled;
    private String externalProviderId = "";

    private volatile HashSet<PhoneNumber> phoneNumbers = new HashSet<>();

    private Set<Integer> roleIds = createRolesSetWithUserRole();

    private Date blockedDate; //date when user was blocked
    private Integer attempts = 0; // count possible attempts log in again
    private Date lastLoginDate;
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
    @Getter
    private volatile PasswordResetDTO passwordReset = null;

    public UserDomainObject() {
    }

    public UserDomainObject(int id) {
        this.id = id;
    }

    public UserDomainObject(UserData userData) {
        super(userData);
    }

    private static Set<Integer> createRolesSetWithUserRole() {
        Set<Integer> newRoleIds = new HashSet<>();
        newRoleIds.add(Roles.USER.getId());

        return newRoleIds;
    }

    @Override
    public UserDomainObject clone() {
        try {
            UserDomainObject clone = (UserDomainObject) super.clone();
            clone.roleIds = new HashSet<>(roleIds);
            clone.phoneNumbers = (HashSet<PhoneNumber>) phoneNumbers.clone();

            return clone;

        } catch (CloneNotSupportedException e) {
            throw new UnhandledException(e);
        }
    }

    public String getFullName() {
        return getFirstName() + " " + getLastName();
    }

    /**
     * @deprecated Use {@link #getPhoneNumbersOfType(PhoneNumberType)}
     */
    @Deprecated
    public String getWorkPhone() {
        return getFirstPhoneNumberOfTypeAsString(PhoneNumberType.WORK);
    }

    /**
     * @deprecated Use {@link #addPhoneNumber(PhoneNumber)}
     */
    @Deprecated
    public void setWorkPhone(String workPhone) {
        replacePhoneNumbersOfType(workPhone, PhoneNumberType.WORK);
    }

    public String getFirstPhoneNumberOfTypeAsString(PhoneNumberType phoneNumberType) {
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
        return new HashSet<>(CollectionUtils.select(phoneNumbers, new PhoneNumberOfTypePredicate(phoneNumberType)));
    }

    public void replacePhoneNumbersOfType(String number, PhoneNumberType type) {
        removePhoneNumbersOfType(type);
        addPhoneNumber(new PhoneNumber(number, type));
    }

    private void removePhoneNumbersOfType(PhoneNumberType phoneNumberType) {
        CollectionUtils.filter(phoneNumbers, new NotPredicate<>(new PhoneNumberOfTypePredicate(phoneNumberType)));
    }

    /**
     * @deprecated Use {@link #getPhoneNumbersOfType(PhoneNumberType)}
     */
    @Deprecated
    public String getMobilePhone() {
        return getFirstPhoneNumberOfTypeAsString(PhoneNumberType.MOBILE);
    }

    /**
     * @deprecated Use {@link #addPhoneNumber(PhoneNumber)}
     */
    @Deprecated
    public void setMobilePhone(String mobilePhone) {
        replacePhoneNumbersOfType(mobilePhone, PhoneNumberType.MOBILE);
    }

    /**
     * @deprecated Use {@link #getPhoneNumbersOfType(PhoneNumberType)}
     */
    @Deprecated
    public String getHomePhone() {
        return getFirstPhoneNumberOfTypeAsString(PhoneNumberType.HOME);
    }

    /**
     * @deprecated Use {@link #addPhoneNumber(PhoneNumber)}
     */
    @Deprecated
    public void setHomePhone(String homePhone) {
        replacePhoneNumbersOfType(homePhone, PhoneNumberType.HOME);
    }

    /**
     * @deprecated Use {@link #getPhoneNumbersOfType(PhoneNumberType)}
     */
    @Deprecated
    public String getFaxPhone() {
        return getFirstPhoneNumberOfTypeAsString(PhoneNumberType.FAX);
    }

    /**
     * @deprecated Use {@link #addPhoneNumber(PhoneNumber)}
     */
    @Deprecated
    public void setFaxPhone(String faxPhone) {
        replacePhoneNumbersOfType(faxPhone, PhoneNumberType.FAX);
    }

    /**
     * @deprecated Use {@link #getPhoneNumbersOfType(PhoneNumberType)}
     */
    @Deprecated
    public String getOtherPhone() {
        return getFirstPhoneNumberOfTypeAsString(PhoneNumberType.OTHER);
    }

    /**
     * @deprecated Use {@link #addPhoneNumber(PhoneNumber)}
     */
    @Deprecated
    public void setOtherPhone(String otherPhone) {
        replacePhoneNumbersOfType(otherPhone, PhoneNumberType.OTHER);
    }

    public Date getCreateDate() {
        return (Date) (null == createDate ? null : createDate.clone());
    }

    public void setCreateDate(Date createDate) {
        this.createDate = (Date) createDate.clone();
    }

    public String getLanguage() {
        return LanguageMapper.convert639_2to639_1(languageIso639_2);
    }

    public void addRoleId(Integer roleId) {
        roleIds.add(roleId);
    }

    public void removeRoleId(Integer roleId) {
        if (!Roles.USER.getId().equals(roleId)) {
            roleIds.remove(roleId);
        }
    }

    public boolean hasRoleId(Integer roleId) {
        return roleIds.contains(roleId);
    }

    public Set<Integer> getRoleIds() {
        return new HashSet<>(roleIds);
    }

    public void setRoleIds(Set<Integer> roleIds) {
        this.roleIds = new HashSet<>(roleIds);
        this.roleIds.add(Roles.USER.getId());
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
        return hasRoleId(Roles.SUPER_ADMIN.getId());
    }

    public boolean canEdit(DocumentDomainObject document) {
        return hasAtLeastPermissionSetIdOn(Permission.RESTRICTED_2, document);
    }

    public boolean canAccess(DocumentDomainObject document) {
        return document.isVisible() || hasAtLeastPermissionSetIdOn(Permission.VIEW, document);
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

        for (Integer roleId : getRoleIds()) {
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

    public boolean hasRoleWithPermission(RolePermissionDomainObject rolePermission) {
        ImcmsAuthenticatorAndUserAndRoleMapper imcmsAuthenticatorAndUserAndRoleMapper = Imcms.getServices().getImcmsAuthenticatorAndUserAndRoleMapper();
        for (Integer roleId : roleIds) {
            if (imcmsAuthenticatorAndUserAndRoleMapper.getRole(roleId).hasPermission(rolePermission)) {
                return true;
            }
        }
        return false;
    }

    public boolean canSeeDocumentWhenEditingMenus(DocumentDomainObject document) {
        return !isDefaultUser() && document.isLinkableByOtherUsers();
    }

    public Set<PhoneNumber> getPhoneNumbers() {
        return Collections.unmodifiableSet(phoneNumbers);
    }

    public boolean canEdit(UserDomainObject editedUser) {
        return equals(editedUser) || isSuperAdmin();
    }

    public boolean isNew() {
        return 0 == id;
    }

    public boolean hasUserAccessToDoc(Meta meta) {
        if (meta == null) throw new DocumentNotExistException();

        if (meta.getVisible() || isSuperAdmin()) return true;

        final Map<Integer, Permission> docPermissions = meta.getRoleIdToPermission();

        return getRoleIds().stream()
                .map(docPermissions::get)
                .filter(Objects::nonNull)
                .anyMatch(documentPermissionSetTypeDomainObject
                        -> documentPermissionSetTypeDomainObject.isAtLeastAsPrivilegedAs(Permission.VIEW));
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
    public boolean hasPasswordReset() {
        return passwordReset != null;
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
        this.passwordReset = new PasswordResetDTO(resetId, time);
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String getLogin() {
        return getLoginName();
    }

    @Override
    public void setLogin(String login) {
        setLoginName(login);
    }

    @Override
    public String getEmail() {
        return getEmailAddress();
    }

    @Override
    public void setEmail(String email) {
        setEmailAddress(email);
    }

    /**
     * @since 4.0.7
     */
    enum PasswordType {
        UNENCRYPTED, ENCRYPTED
    }

    private static class PhoneNumberOfTypePredicate implements Predicate<PhoneNumber> {
        private final PhoneNumberType phoneNumberType;

        PhoneNumberOfTypePredicate(PhoneNumberType phoneNumberType) {
            this.phoneNumberType = phoneNumberType;
        }

        public boolean evaluate(PhoneNumber phoneNumber) {
            return phoneNumber.getType().equals(phoneNumberType);
        }
    }
}
