package imcode.server.user;

import org.apache.log4j.Logger;

import java.util.Collection;
import java.util.LinkedList;

public class ChainedLdapUserAndRoleRegistry implements Authenticator, UserAndRoleRegistry {

    public static final String[] DEFAULT_ROLES = {LdapUserAndRoleRegistry.DEFAULT_LDAP_ROLE};

    private Collection<Authenticator> authenticators = new LinkedList<Authenticator>();

    private Collection<UserAndRoleRegistry> userAndRoleRegistries = new LinkedList<UserAndRoleRegistry>();

    private Logger logger = Logger.getLogger(getClass());

    public ChainedLdapUserAndRoleRegistry() {}

    public ChainedLdapUserAndRoleRegistry(Authenticator authenticator, UserAndRoleRegistry userAndRoleRegistry) {
        addLink(authenticator, userAndRoleRegistry);
    }

    public void addLink(Authenticator authenticator, UserAndRoleRegistry userAndRoleRegistry) {
        authenticators.add(authenticator);
        userAndRoleRegistries.add(userAndRoleRegistry);
    }


    public boolean authenticate(String loginName, String password) {
        boolean result = false;

        for (Authenticator authenticator : authenticators) {
            
            if (result = authenticator.authenticate(loginName, password)) {
                break;
            }
        }

        return result;
    }


    public UserDomainObject getUser(String loginName) {
        UserDomainObject user = null;

        for (UserAndRoleRegistry userAndRoleRegistry : userAndRoleRegistries) {

            if ((user = userAndRoleRegistry.getUser(loginName)) != null) {
                break;
            }
        }

        return user;
    }


    public String[] getRoleNames(UserDomainObject user) {
        String[] roleNames = DEFAULT_ROLES;
        
        for (UserAndRoleRegistry userAndRoleRegistry : userAndRoleRegistries) {
            try {
                roleNames = userAndRoleRegistry.getRoleNames(user);
            } catch (NullPointerException e) {
                // no roles for user
            }

            if (roleNames.length > 1) {
                break;
            }
        }

        return roleNames;
    }

    
    public String[] getAllRoleNames() {
        for (UserAndRoleRegistry userAndRoleRegistry : userAndRoleRegistries) {
            try {
                return userAndRoleRegistry.getAllRoleNames();
            } catch (Exception e) {
                // ignore
            }
        }

        return DEFAULT_ROLES;
    }
}
