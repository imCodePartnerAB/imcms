package imcode.server.user;

import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.HashSet;

import com.imcode.imcms.api.RoleConstants;
import imcode.server.IMCConstants;

public class ExternalizedImcmsAuthenticatorAndUserMapper implements UserAndRoleMapper, Authenticator {
    private ImcmsAuthenticatorAndUserMapper imcmsAuthenticatorAndUserMapper;
    private Authenticator externalAuthenticator;
    private UserAndRoleMapper externalUserMapper;
    private String defaultLanguage;

    private Logger log = Logger.getLogger( ExternalizedImcmsAuthenticatorAndUserMapper.class );

    public ExternalizedImcmsAuthenticatorAndUserMapper( ImcmsAuthenticatorAndUserMapper imcms, Authenticator externalAuthenticator, UserAndRoleMapper externalUserMapper, String defaultLanguage ) throws IllegalArgumentException {
        if( (null == externalAuthenticator) != (null == externalUserMapper) ) {
            throw new IllegalArgumentException( "External authenticator and external usermapper should both be either set or not set." );
        }

        this.imcmsAuthenticatorAndUserMapper = imcms;
        this.externalAuthenticator = externalAuthenticator;
        this.externalUserMapper = externalUserMapper;
        this.defaultLanguage = defaultLanguage;
    }

    public void synchRolesWithExternal() {
        if( null != externalUserMapper ) {
            String[] externalRoleNames = externalUserMapper.getAllRoleNames();
            imcmsAuthenticatorAndUserMapper.addRoleNames( externalRoleNames );
        }
    }

    public boolean authenticate( String loginName, String password ) {
        // this is a fix. Because external users gets the password empty string "" we need to check that the
        // password exeedes 0 lenght.
        boolean userAuthenticatesInImcms = false;
        if( password.length() >= IMCConstants.PASSWORD_MINIMUM_LENGTH ) {
            userAuthenticatesInImcms = imcmsAuthenticatorAndUserMapper.authenticate( loginName, password );
        }
        boolean userAuthenticatesInExternal = false;
        if( !userAuthenticatesInImcms && null != externalAuthenticator ) {
            userAuthenticatesInExternal = externalAuthenticator.authenticate( loginName, password );
        }
        return userAuthenticatesInImcms || userAuthenticatesInExternal;
    }

    public UserDomainObject getUser( String loginName ) {
        UserDomainObject imcmsUser = imcmsAuthenticatorAndUserMapper.getUser( loginName );
        UserDomainObject externalUser = getUserFromOtherUserMapper( loginName );
        boolean imcmsUserExists = null != imcmsUser;
        boolean externalUserExists = null != externalUser;
        boolean imcmsUserIsInternal = (null != imcmsUser) && !imcmsUser.isImcmsExternal();

        UserDomainObject result = null;

        if( !imcmsUserIsInternal && !externalUserExists && !imcmsUserExists ) {
            result = null;
        } else if( !imcmsUserIsInternal && !externalUserExists && imcmsUserExists ) {
            deactivateExternalUserInImcms( loginName, imcmsUser );
            result = null;
        } else if( !imcmsUserIsInternal && externalUserExists && !imcmsUserExists ) {
            result = synchExternalUserInImcms( loginName, externalUser, imcmsUserExists );
        } else if( !imcmsUserIsInternal && externalUserExists && imcmsUserExists ) {
            result = synchExternalUserInImcms( loginName, externalUser, imcmsUserExists );
        } else if( imcmsUserIsInternal && !externalUserExists && !imcmsUserExists ) {
            throw new InternalError( "Impossible condition. 'Internal' user doesn't exist in imcms." );
        } else if( imcmsUserIsInternal && !externalUserExists && imcmsUserExists ) {
            result = imcmsUser;
        } else if( imcmsUserIsInternal && externalUserExists && !imcmsUserExists ) {
            throw new InternalError( "Impossible condition. 'Internal' user doesn't exist in imcms." );
        } else if( imcmsUserIsInternal && externalUserExists && imcmsUserExists ) {
            result = imcmsUser;
            //throw new UserConflictException( "An imcmsAuthenticatorAndUserMapper-internal user was found in external directory.", null );
        }
        return result;
    }

    private UserDomainObject getUserFromOtherUserMapper( String loginName ) {
        UserDomainObject result = null;
        if( null != externalUserMapper ) {
            result = externalUserMapper.getUser( loginName );
        }
        if( null != result && null == result.getLangPrefix() ) {
            result.setLangPrefix( defaultLanguage );
            if( "swe".equalsIgnoreCase( defaultLanguage ) ) {
                result.setLangId( 1 );
            } else {
                log.error( "Language is not properly implemented!" );
                throw new RuntimeException( "Language is not properly implemented!" );
            }
        }
        return result;
    }

    private UserDomainObject synchExternalUserInImcms( String loginName, UserDomainObject externalUser, boolean imcmsUserExists ) {
        externalUser.setImcmsExternal( true );

        if( imcmsUserExists ) {
            imcmsAuthenticatorAndUserMapper.updateUser( loginName, externalUser );
        } else {
            imcmsAuthenticatorAndUserMapper.addUser( externalUser );
        }

        UserDomainObject synchedUser = imcmsAuthenticatorAndUserMapper.getUser( loginName );
        updateRoleAssignments( synchedUser );
        return synchedUser;
    }

    private void updateRoleAssignments( UserDomainObject user ) {
        imcmsAuthenticatorAndUserMapper.addRoleToUser( user, RoleConstants.USERS );

        String[] externalRoleNames = externalUserMapper.getRoleNames( user );
        for( int i = 0; i < externalRoleNames.length; i++ ) {
            String externalRoleName = externalRoleNames[i];
            boolean hasNameConflictWithSuperAdmin = RoleConstants.SUPER_ADMIN.equalsIgnoreCase( externalRoleName );
            boolean hasNameConflictWithUserAdmin = RoleConstants.USER_ADMIN.equalsIgnoreCase( externalRoleName );
            if( !hasNameConflictWithSuperAdmin && !hasNameConflictWithUserAdmin ) {
                imcmsAuthenticatorAndUserMapper.addRoleToUser( user, externalRoleName );
            }
        }
    }

    private void deactivateExternalUserInImcms( String loginName, UserDomainObject imcmsUser ) {
        imcmsUser.setActive( false );
        imcmsAuthenticatorAndUserMapper.updateUser( loginName, imcmsUser );
    }

    public String[] getRoleNames( UserDomainObject user ) {
        String[] imcmsRoleNames = imcmsAuthenticatorAndUserMapper.getRoleNames( user );
        String[] externalRoleNames = externalUserMapper.getRoleNames( user );

        String[] result = mergeAndDeleteDuplicates( imcmsRoleNames, externalRoleNames );
        log.debug( "Roles from imcms and external: " + Arrays.asList( result ) );
        return result;
    }

    private String[] mergeAndDeleteDuplicates( String[] imcmsRoleNames, String[] externalRoleNames ) {
        HashSet roleNames = new HashSet( Arrays.asList( imcmsRoleNames ) );
        roleNames.addAll( Arrays.asList( externalRoleNames ) );
        String[] result = (String[])roleNames.toArray( new String[imcmsRoleNames.length + externalRoleNames.length] );
        return result;
    }

    public String[] getAllRoleNames() {
        String[] imcmsRoleNames = imcmsAuthenticatorAndUserMapper.getAllRoleNames();
        String[] externalRoleNames = externalUserMapper.getAllRoleNames();
        String[] result = mergeAndDeleteDuplicates( imcmsRoleNames, externalRoleNames );
        return result;
    }

    public RoleDomainObject getRole( String roleName ) {
        return imcmsAuthenticatorAndUserMapper.getRole(roleName) ;
    }

    public class UserConflictException extends RuntimeException {
        UserConflictException( String message, Throwable cause ) {
            super( message, cause );
        }

    }
}
