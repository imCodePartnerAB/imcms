package imcode.server.user;

import imcode.server.IMCConstants;
import org.apache.log4j.Logger;
import org.apache.log4j.NDC;

import java.util.Arrays;
import java.util.HashSet;

public class ExternalizedImcmsAuthenticatorAndUserMapper implements UserAndRoleMapper, Authenticator {

    private ImcmsAuthenticatorAndUserMapper imcmsAuthenticatorAndUserMapper;
    private Authenticator externalAuthenticator;
    private UserAndRoleMapper externalUserMapper;
    private String defaultLanguage;

    private Logger log = Logger.getLogger( ExternalizedImcmsAuthenticatorAndUserMapper.class );

    public ExternalizedImcmsAuthenticatorAndUserMapper( ImcmsAuthenticatorAndUserMapper imcms,
                                                        Authenticator externalAuthenticator,
                                                        UserAndRoleMapper externalUserMapper, String defaultLanguage ) throws IllegalArgumentException {
        if ( ( null == externalAuthenticator ) != ( null == externalUserMapper ) ) {
            throw new IllegalArgumentException( "External authenticator and external usermapper should both be either set or not set." );
        }

        this.imcmsAuthenticatorAndUserMapper = imcms;
        this.externalAuthenticator = externalAuthenticator;
        this.externalUserMapper = externalUserMapper;
        this.defaultLanguage = defaultLanguage;
    }

    public void synchRolesWithExternal() {
        if ( null != externalUserMapper ) {
            String[] externalRoleNames = externalUserMapper.getAllRoleNames();
            imcmsAuthenticatorAndUserMapper.addRoleNames( externalRoleNames );
        }
    }

    public boolean authenticate( String loginName, String password ) {
        NDC.push( "authenticate" );
        // this is a fix. Because external users gets the password empty string "" we need to check that the
        // password exeedes 0 lenght.
        boolean userAuthenticatesInImcms = false;
        if ( password.length() >= IMCConstants.PASSWORD_MINIMUM_LENGTH ) {
            userAuthenticatesInImcms = imcmsAuthenticatorAndUserMapper.authenticate( loginName, password );
        }
        boolean userAuthenticatesInExternal = false;
        if ( !userAuthenticatesInImcms && null != externalAuthenticator ) {
            userAuthenticatesInExternal = externalAuthenticator.authenticate( loginName, password );
        }
        NDC.pop();
        return userAuthenticatesInImcms || userAuthenticatesInExternal;
    }

    public UserDomainObject getUser( String loginName ) {
        NDC.push( "getUser" );
        UserDomainObject imcmsUser = imcmsAuthenticatorAndUserMapper.getUser( loginName );
        boolean imcmsUserExists = null != imcmsUser;
        boolean imcmsUserIsInternal = imcmsUserExists && !imcmsUser.isImcmsExternal();
        UserDomainObject result ;

        if ( imcmsUserIsInternal ) {
            result = imcmsUser;
        } else {
            result = getExternalUser( loginName, imcmsUserExists, imcmsUser );
        }
        NDC.pop();
        return result;
    }

    private UserDomainObject getExternalUser( String loginName, boolean imcmsUserExists, UserDomainObject imcmsUser ) {
        UserDomainObject result;
        UserDomainObject externalUser = getUserFromOtherUserMapper( loginName );
        boolean externalUserExists = null != externalUser;

        if ( externalUserExists ) {
            result = synchExternalUserInImcms( loginName, externalUser, imcmsUserExists );
        } else {
            if ( imcmsUserExists ) {
                deactivateExternalUserInImcms( loginName, imcmsUser );
            }
            result = null;
        }
        return result;
    }

    private UserDomainObject getUserFromOtherUserMapper( String loginName ) {
        UserDomainObject result = null;
        if ( null != externalUserMapper ) {
            result = externalUserMapper.getUser( loginName );
        }
        if ( null != result && null == result.getLanguageIso639_2() ) {
            result.setLanguageIso639_2( defaultLanguage );
            if ( "swe".equalsIgnoreCase( defaultLanguage ) ) {
                result.setLangId( 1 );
            } else if ( "eng".equalsIgnoreCase( defaultLanguage ) ) {
                result.setLangId( 2 );
            } else {
                // FIXME: Make this general for all languages
                log.error( "Language is not properly implemented!" );
                throw new RuntimeException( "Language is not properly implemented!" );
            }
        }
        return result;
    }

    private UserDomainObject synchExternalUserInImcms( String loginName, UserDomainObject externalUser,
                                                       boolean imcmsUserExists ) {
        externalUser.setImcmsExternal( true );

        if ( imcmsUserExists ) {
            imcmsAuthenticatorAndUserMapper.updateUser( loginName, externalUser );
        } else {
            imcmsAuthenticatorAndUserMapper.addUser( externalUser );
        }
        addExternalRolesToUser( externalUser );
        imcmsAuthenticatorAndUserMapper.sqlUpdateUserRoles( externalUser );

        return imcmsAuthenticatorAndUserMapper.getUser( loginName );
    }

    private void addExternalRolesToUser( UserDomainObject user ) {
        String[] externalRoleNames = externalUserMapper.getRoleNames( user );
        for ( int i = 0; i < externalRoleNames.length; i++ ) {
            String externalRoleName = externalRoleNames[i];
            RoleDomainObject externalRole = imcmsAuthenticatorAndUserMapper.getRoleByName( externalRoleName );
            if ( null == externalRole ) {
                externalRole = imcmsAuthenticatorAndUserMapper.addRole( externalRoleName );
            }
            if ( !externalRole.isAdminRole() ) {
                user.addRole( externalRole );
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

    public RoleDomainObject getRoleByName( String roleName ) {
        return imcmsAuthenticatorAndUserMapper.getRoleByName( roleName );
    }

}
