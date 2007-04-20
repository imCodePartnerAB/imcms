package imcode.server.user;

import imcode.server.ImcmsConstants;
import org.apache.commons.lang.UnhandledException;
import org.apache.log4j.Logger;
import org.apache.log4j.NDC;

import java.util.Arrays;
import java.util.HashSet;

public class ExternalizedImcmsAuthenticatorAndUserRegistry implements UserAndRoleRegistry, Authenticator {

    private ImcmsAuthenticatorAndUserAndRoleMapper imcmsAuthenticatorAndUserMapperAndRole;
    private Authenticator externalAuthenticator;
    private UserAndRoleRegistry externalUserRegistry;
    private String defaultLanguage;

    private Logger log = Logger.getLogger( ExternalizedImcmsAuthenticatorAndUserRegistry.class );

    public ExternalizedImcmsAuthenticatorAndUserRegistry( ImcmsAuthenticatorAndUserAndRoleMapper imcmsAndRole,
                                                          Authenticator externalAuthenticator,
                                                          UserAndRoleRegistry externalUserRegistry,
                                                          String defaultLanguage ) throws IllegalArgumentException {
        if ( ( null == externalAuthenticator ) != ( null == externalUserRegistry ) ) {
            throw new IllegalArgumentException( "External authenticator and external usermapper should both be either set or not set." );
        }

        this.imcmsAuthenticatorAndUserMapperAndRole = imcmsAndRole;
        this.externalAuthenticator = externalAuthenticator;
        this.externalUserRegistry = externalUserRegistry;
        this.defaultLanguage = defaultLanguage;
    }

    public void synchRolesWithExternal() {
        if ( null != externalUserRegistry ) {
            String[] externalRoleNames = externalUserRegistry.getAllRoleNames();
            imcmsAuthenticatorAndUserMapperAndRole.addRoleNames( externalRoleNames );
        }
    }

    public boolean authenticate( String loginName, String password ) {
        NDC.push( "authenticate" );
        boolean userAuthenticatesInImcms = false;
        if ( password.length() >= ImcmsConstants.PASSWORD_MINIMUM_LENGTH ) {
            userAuthenticatesInImcms = imcmsAuthenticatorAndUserMapperAndRole.authenticate( loginName, password );
        }
        boolean userAuthenticatesInExternal = false;
        if ( !userAuthenticatesInImcms && null != externalAuthenticator && password.length() > 0 ) {
            userAuthenticatesInExternal = externalAuthenticator.authenticate( loginName, password );
        }
        NDC.pop();
        return userAuthenticatesInImcms || userAuthenticatesInExternal;
    }

    public UserDomainObject getUser( String loginName ) {
        NDC.push( "getUser" );
        UserDomainObject imcmsUser = imcmsAuthenticatorAndUserMapperAndRole.getUser( loginName );
        boolean imcmsUserExists = null != imcmsUser;
        boolean imcmsUserIsInternal = imcmsUserExists && !imcmsUser.isImcmsExternal();
        UserDomainObject result;

        if ( imcmsUserIsInternal ) {
            result = imcmsUser;
        } else {
            result = getExternalUser( loginName, imcmsUser );
        }
        NDC.pop();
        return result;
    }

    private UserDomainObject getExternalUser( String loginName, UserDomainObject imcmsUser ) {
        UserDomainObject result;
        UserDomainObject externalUser = getUserFromOtherUserMapper( loginName );
        boolean externalUserExists = null != externalUser;

        if ( externalUserExists ) {
            result = synchExternalUserInImcms( loginName, externalUser, imcmsUser );
        } else {
            if ( null != imcmsUser ) {
                deactivateExternalUserInImcms( loginName, imcmsUser );
            }
            result = null;
        }
        return result;
    }

    private UserDomainObject getUserFromOtherUserMapper( String loginName ) {
        UserDomainObject result = null;
        if ( null != externalUserRegistry ) {
            result = externalUserRegistry.getUser( loginName );
        }
        if ( null != result && null == result.getLanguageIso639_2() ) {
            result.setLanguageIso639_2( defaultLanguage );
        }
        return result;
    }

    private UserDomainObject synchExternalUserInImcms( String loginName, UserDomainObject externalUser,
                                                       UserDomainObject imcmsUser ) {
        externalUser.setImcmsExternal( true );
        addExternalRolesToUser( externalUser );

        if ( null != imcmsUser ) {
            externalUser.setRoleIds( imcmsUser.getRoleIds() );
            imcmsAuthenticatorAndUserMapperAndRole.saveUser( loginName, externalUser);
        } else {
            try {
                imcmsAuthenticatorAndUserMapperAndRole.addUser( externalUser);
            } catch ( UserAlreadyExistsException shouldNotBeThrown ) {
                throw new UnhandledException( shouldNotBeThrown );
            }
        }

        return imcmsAuthenticatorAndUserMapperAndRole.getUser( loginName );
    }

    private void addExternalRolesToUser( UserDomainObject user ) {
        String[] externalRoleNames = externalUserRegistry.getRoleNames( user );
        for ( int i = 0; i < externalRoleNames.length; i++ ) {
            String externalRoleName = externalRoleNames[i];
            RoleDomainObject externalRole = imcmsAuthenticatorAndUserMapperAndRole.getRoleByName( externalRoleName );
            if ( null == externalRole ) {
                externalRole = imcmsAuthenticatorAndUserMapperAndRole.addRole( externalRoleName );
            }
            if ( !externalRole.isAdminRole() ) {
                user.addRoleId(externalRole.getId());
            }
        }
    }

    private void deactivateExternalUserInImcms( String loginName, UserDomainObject imcmsUser ) {
        imcmsUser.setActive( false );
        imcmsAuthenticatorAndUserMapperAndRole.saveUser( loginName, imcmsUser);
    }

    public String[] getRoleNames( UserDomainObject user ) {
        String[] imcmsRoleNames = imcmsAuthenticatorAndUserMapperAndRole.getRoleNames( user );
        String[] externalRoleNames = externalUserRegistry.getRoleNames( user );

        String[] result = mergeAndDeleteDuplicates( imcmsRoleNames, externalRoleNames );
        log.debug( "Roles from imcms and external: " + Arrays.asList( result ) );
        return result;
    }

    private String[] mergeAndDeleteDuplicates( String[] imcmsRoleNames, String[] externalRoleNames ) {
        HashSet roleNames = new HashSet( Arrays.asList( imcmsRoleNames ) );
        roleNames.addAll( Arrays.asList( externalRoleNames ) );
        return (String[])roleNames.toArray( new String[roleNames.size()] );
    }

    public String[] getAllRoleNames() {
        String[] imcmsRoleNames = imcmsAuthenticatorAndUserMapperAndRole.getAllRoleNames();
        String[] externalRoleNames = externalUserRegistry.getAllRoleNames();
        return mergeAndDeleteDuplicates( imcmsRoleNames, externalRoleNames );
    }

}
