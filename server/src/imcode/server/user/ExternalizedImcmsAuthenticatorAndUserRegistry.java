package imcode.server.user;

import com.google.common.collect.Sets;
import imcode.server.ImcmsConstants;
import org.apache.commons.lang.UnhandledException;
import org.apache.log4j.Logger;
import org.apache.log4j.NDC;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ExternalizedImcmsAuthenticatorAndUserRegistry implements UserAndRoleRegistry, Authenticator {

    private ImcmsAuthenticatorAndUserAndRoleMapper imcmsAuthenticatorAndUserMapperAndRole;
    private Authenticator externalAuthenticator;
    private UserAndRoleRegistry externalUserRegistry;
    private String defaultLanguage;

    private Logger log = Logger.getLogger(ExternalizedImcmsAuthenticatorAndUserRegistry.class);

    public ExternalizedImcmsAuthenticatorAndUserRegistry(ImcmsAuthenticatorAndUserAndRoleMapper imcmsAndRole,
                                                         Authenticator externalAuthenticator,
                                                         UserAndRoleRegistry externalUserRegistry,
                                                         String defaultLanguage) throws IllegalArgumentException {
        if ((null == externalAuthenticator) != (null == externalUserRegistry)) {
            throw new IllegalArgumentException("External authenticator and external usermapper should both be either set or not set.");
        }

        this.imcmsAuthenticatorAndUserMapperAndRole = imcmsAndRole;
        this.externalAuthenticator = externalAuthenticator;
        this.externalUserRegistry = externalUserRegistry;
        this.defaultLanguage = defaultLanguage;
    }

    public void synchRolesWithExternal() {
        if (null != externalUserRegistry) {
            String[] externalRoleNames = externalUserRegistry.getAllRoleNames();
            imcmsAuthenticatorAndUserMapperAndRole.addRoleNames(externalRoleNames);
        }
    }

    public boolean authenticate(String loginName, String password) {
        NDC.push("authenticate");
        boolean userAuthenticatesInImcms = false;
        if (password.length() >= ImcmsConstants.PASSWORD_MINIMUM_LENGTH) {
            userAuthenticatesInImcms = imcmsAuthenticatorAndUserMapperAndRole.authenticate(loginName, password);
        }
        boolean userAuthenticatesInExternal = false;
        if (!userAuthenticatesInImcms && null != externalAuthenticator && password.length() > 0) {
            userAuthenticatesInExternal = externalAuthenticator.authenticate(loginName, password);
        }
        NDC.pop();
        return userAuthenticatesInImcms || userAuthenticatesInExternal;
    }

    public UserDomainObject getUser(String loginName) {
        NDC.push("getUser");
        UserDomainObject imcmsUser = imcmsAuthenticatorAndUserMapperAndRole.getUser(loginName);
        boolean imcmsUserExists = null != imcmsUser;
        boolean imcmsUserIsInternal = imcmsUserExists && !imcmsUser.isImcmsExternal();
        UserDomainObject result;

        if (imcmsUserIsInternal) {
            result = imcmsUser;
        } else {
            result = getExternalUser(loginName, imcmsUser);
        }
        NDC.pop();
        return result;
    }

    private UserDomainObject getExternalUser(String loginName, UserDomainObject imcmsUser) {
        UserDomainObject result;
        UserDomainObject externalUser = getUserFromOtherUserMapper(loginName);
        boolean externalUserExists = null != externalUser;

        if (externalUserExists) {
            result = synchExternalUserInImcms(loginName, externalUser, imcmsUser);
        } else {
            if (null != imcmsUser) {
                deactivateExternalUserInImcms(loginName, imcmsUser);
            }
            result = null;
        }
        return result;
    }

    private UserDomainObject getUserFromOtherUserMapper(String loginName) {
        UserDomainObject result = null;
        if (null != externalUserRegistry) {
            result = externalUserRegistry.getUser(loginName);
        }
        if (null != result && null == result.getLanguageIso639_2()) {
            result.setLanguageIso639_2(defaultLanguage);
        }
        return result;
    }

    private UserDomainObject synchExternalUserInImcms(String loginName, UserDomainObject externalUser,
                                                      UserDomainObject internalUser) {
        externalUser.setImcmsExternal(true);

        syncUserExternalRoles(externalUser, internalUser);

        if (internalUser != null) {
            imcmsAuthenticatorAndUserMapperAndRole.saveUser(loginName, externalUser);
        } else {
            try {
                imcmsAuthenticatorAndUserMapperAndRole.addUser(externalUser);
            } catch (UserAlreadyExistsException shouldNotBeThrown) {
                throw new UnhandledException(shouldNotBeThrown);
            }
        }

        return imcmsAuthenticatorAndUserMapperAndRole.getUser(loginName);
    }

    private void syncUserExternalRoles(UserDomainObject externalUser, UserDomainObject internalUser) {
        Set<String> externalRolesNamesLCase = Sets.newHashSet();
        Set<String> userExternalRolesNamesLCase = Sets.newHashSet();

        for (String roleName: externalUserRegistry.getAllRoleNames()) {
            externalRolesNamesLCase.add(roleName.toLowerCase());
        }

        for (String roleName: externalUserRegistry.getRoleNames(externalUser)) {
            userExternalRolesNamesLCase.add(roleName.toLowerCase());
        }


        log.debug(String.format("Syncing user %s externally mapped roles.", externalUser));

        for (String roleName : userExternalRolesNamesLCase) {
            RoleDomainObject role = imcmsAuthenticatorAndUserMapperAndRole.getRoleByName(roleName);
            log.debug(String.format("Syncing user %s external role %s.", externalUser, role));

            if (null == role) {
                log.debug(String.format("Role %s is new and will be stored internally.", role));
                role = imcmsAuthenticatorAndUserMapperAndRole.addRole(roleName);
            }

            if (role.isAdminRole()) {
                log.debug(String.format("External role %s is marked as an admin role and can not be granted to user %s.", role, externalUser));
            } else {
                log.debug(String.format("User %s is a member-of externally mapped role %s. The role will be granted.", externalUser, role));
                externalUser.addRoleId(role.getId());
            }
        }


        if (internalUser != null) {
            log.debug(String.format("Syncing user %s all previously assigned roles.", externalUser));

            for (RoleId roleId : internalUser.getRoleIds()) {
                RoleDomainObject role = imcmsAuthenticatorAndUserMapperAndRole.getRole(roleId);
                String roleName = role.getName();

                log.debug(String.format("Syncing user %s previously assigned role %s.", externalUser, role));
                boolean add = false;

                if (!externalRolesNamesLCase.contains(roleName.toLowerCase())) {
                    log.debug(String.format("User %s role %s is not mapped externally and not need to be synced. The role will be granted.", externalUser, role));
                    add = true;
                } else {
                    log.debug(String.format("User %s role %s is mapped externally and need to be synced.", externalUser, role));
                    if (!userExternalRolesNamesLCase.contains(roleName.toLowerCase())) {
                        log.debug(String.format("User %s is not more a member-of externally mapped role %s. The role will be revoked.", externalUser, role));
                    } else {
                        log.debug(String.format("User %s is a member-of mapped role %s. The role will be granted.", externalUser, role));
                    }
                }

                if (add) externalUser.addRoleId(roleId);
            }
        }

        log.debug(String.format("User %s roles have been synced. Granted roles ids %s.", externalUser, Arrays.toString(externalUser.getRoleIds())));
    }

    private void deactivateExternalUserInImcms(String loginName, UserDomainObject imcmsUser) {
        imcmsUser.setActive(false);
        imcmsAuthenticatorAndUserMapperAndRole.saveUser(loginName, imcmsUser);
    }

    public String[] getRoleNames(UserDomainObject user) {
        String[] imcmsRoleNames = imcmsAuthenticatorAndUserMapperAndRole.getRoleNames(user);
        String[] externalRoleNames = externalUserRegistry.getRoleNames(user);

        String[] result = mergeAndDeleteDuplicates(imcmsRoleNames, externalRoleNames);
        log.debug("Roles from imcms and external: " + Arrays.asList(result));
        return result;
    }

    private String[] mergeAndDeleteDuplicates(String[] imcmsRoleNames, String[] externalRoleNames) {
        HashSet roleNames = new HashSet(Arrays.asList(imcmsRoleNames));
        roleNames.addAll(Arrays.asList(externalRoleNames));
        return (String[]) roleNames.toArray(new String[roleNames.size()]);
    }

    public String[] getAllRoleNames() {
        String[] imcmsRoleNames = imcmsAuthenticatorAndUserMapperAndRole.getAllRoleNames();
        String[] externalRoleNames = externalUserRegistry.getAllRoleNames();
        return mergeAndDeleteDuplicates(imcmsRoleNames, externalRoleNames);
    }

}
