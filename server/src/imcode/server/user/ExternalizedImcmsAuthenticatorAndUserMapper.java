package imcode.server.user;

import org.apache.log4j.Logger;

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
                                                       UserAndRoleMapper externalUserMapper,
                                                       String defaultLanguage ) throws IllegalArgumentException {
      if( (null == externalAuthenticator) != (null == externalUserMapper) ) {
         throw new IllegalArgumentException("External authenticator and external usermapper should both be either set or not set.");
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
      boolean result = false;
      if (null != externalAuthenticator) {
         boolean userExistsInOther = externalAuthenticator.authenticate( loginName, password );
         result = userExistsInOther;
      }
      if( !result ) {
         boolean userExistsInImcms = imcmsAuthenticatorAndUserMapper.authenticate( loginName, password );
         result = userExistsInImcms;
      }
      return result;
   }

   public User getUser( String loginName ) {
      User imcmsUser = imcmsAuthenticatorAndUserMapper.getUser( loginName );
      User externalUser = getUserFromOtherUserMapper( loginName );
      boolean imcmsUserExists = null != imcmsUser;
      boolean externalUserExists = null != externalUser;
      boolean imcmsUserIsInternal = (null != imcmsUser) && !imcmsUser.isImcmsExternal();

      User result = null;

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
         throw new UserConflictException( "An imcmsAuthenticatorAndUserMapper-internal user was found in external directory.", null );
      }
      return result;
   }

   private User getUserFromOtherUserMapper( String loginName ) {
      User result = null;
      if( null != externalUserMapper ) {
         result = externalUserMapper.getUser( loginName );
      }
      if( null != result && null == result.getLangPrefix() ) {
         result.setLangPrefix( defaultLanguage );
      }
      return result;
   }

   private User synchExternalUserInImcms( String loginName, User externalUser, boolean imcmsUserExists ) {
      externalUser.setImcmsExternal( true );

      if( imcmsUserExists ) {
         imcmsAuthenticatorAndUserMapper.updateUser( loginName, externalUser );
      } else {
         imcmsAuthenticatorAndUserMapper.addUser( externalUser );
      }

      User synchedUser = imcmsAuthenticatorAndUserMapper.getUser( loginName );
      updateRoleAssignments( synchedUser );
      return synchedUser;
   }


   private void updateRoleAssignments( User user ) {
      imcmsAuthenticatorAndUserMapper.assignRoleToUser( user, ImcmsAuthenticatorAndUserMapper.ALWAYS_EXISTING_USERS_ROLE );

      String[] roleNames = externalUserMapper.getRoleNames( user );
      for( int i = 0; i < roleNames.length; i++ ) {
         String roleName = roleNames[i];
         imcmsAuthenticatorAndUserMapper.assignRoleToUser( user, roleName );
      }
   }

   private void deactivateExternalUserInImcms( String loginName, User imcmsUser ) {
      imcmsUser.setActive( false );
      imcmsAuthenticatorAndUserMapper.updateUser( loginName, imcmsUser );
   }

   public String[] getRoleNames( User user ) {
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

   public class UserConflictException extends RuntimeException {
      UserConflictException( String message, Throwable cause ) {
         super( message, cause );
      }

   }
}
