package imcode.server.user;

import org.apache.log4j.Logger;

public class AuthenticatorAndUserMapperUsingImcmsAndOther implements UserMapper, Authenticator {
   private ImcmsAuthenticatorAndUserMapper imcmsAuthenticatorAndUserMapper;
   private Authenticator otherAuthenticator;
   private UserMapper otherUserMapper;

   public AuthenticatorAndUserMapperUsingImcmsAndOther( ImcmsAuthenticatorAndUserMapper imcms, Authenticator otherAuthenticator, UserMapper otherUserMapper ) {
      this.imcmsAuthenticatorAndUserMapper = imcms;
      this.otherAuthenticator = otherAuthenticator;
      this.otherUserMapper = otherUserMapper;
   }

   public boolean authenticate( String loginName, String password ) {
      boolean result = false;
      boolean userExistsInOther = otherAuthenticator.authenticate( loginName, password );
      result = userExistsInOther;
      if( !userExistsInOther ) {
         boolean userExistsInImcms = imcmsAuthenticatorAndUserMapper.authenticate( loginName, password );
         result = userExistsInImcms;
      }
      return result;
   }

   public User getUser( String loginName ) {
      User imcmsUser = imcmsAuthenticatorAndUserMapper.getUser( loginName );
      User otherUser = otherUserMapper.getUser( loginName );
      boolean imcmsUserExists = null != imcmsUser;
      boolean otherUserExists = null != otherUser;
      boolean imcmsUserIsInternal = (null != imcmsUser) && !imcmsUser.isImcmsExternal();

      User result = null;

      if( !imcmsUserIsInternal && !otherUserExists && !imcmsUserExists ) {
         result = null;
      } else if( !imcmsUserIsInternal && !otherUserExists && imcmsUserExists ) {
         deactivateExternalUserInImcms( loginName, imcmsUser );
         result = null;
      } else if( !imcmsUserIsInternal && otherUserExists && !imcmsUserExists ) {
         result = addExternalUserToImcms( loginName, otherUser );
      } else if( !imcmsUserIsInternal && otherUserExists && imcmsUserExists ) {
         result = updateExternalUserInImcms( loginName, otherUser );
      } else if( imcmsUserIsInternal && !otherUserExists && !imcmsUserExists ) {
         throw new InternalError( "Impossible condition. 'Internal' user doesn't exist in imcms." );
      } else if( imcmsUserIsInternal && !otherUserExists && imcmsUserExists ) {
         result = imcmsUser;
      } else if( imcmsUserIsInternal && otherUserExists && !imcmsUserExists ) {
         throw new InternalError( "Impossible condition. 'Internal' user doesn't exist in imcms." );
      } else if( imcmsUserIsInternal && otherUserExists && imcmsUserExists ) {
         throw new UserConflictException( "An imcmsAuthenticatorAndUserMapper-internal user was found in external directory.", null );
      }
      return result;
   }

   private User updateExternalUserInImcms( String loginName, User otherUser ) {
      otherUser.setImcmsExternal( true );
      imcmsAuthenticatorAndUserMapper.updateUser( loginName, otherUser );
      User updatedUser = imcmsAuthenticatorAndUserMapper.getUser( loginName );
      return updatedUser;
   }

   private void deactivateExternalUserInImcms( String loginName, User imcmsUser ) {
      imcmsUser.setActive( false );
      imcmsAuthenticatorAndUserMapper.updateUser( loginName, imcmsUser );
   }

   private User addExternalUserToImcms( String loginName, User otherUser ) {
      otherUser.setImcmsExternal( true );
      imcmsAuthenticatorAndUserMapper.addUser( otherUser );
      User addedUser = imcmsAuthenticatorAndUserMapper.getUser( loginName );
      return addedUser;
   }

   public User getUser( int id ) {
      // todo
      return null;
   }

   public void updateUser( String loginName, User newUserData ) {
      // todo
      throw new UnsupportedOperationException( "Not implemented yet" );
   }

   public void addUser( User newUser ) {
      // todo
      throw new UnsupportedOperationException( "Not implemented yet" );
   }

   public class UserConflictException extends RuntimeException {
      UserConflictException( String message, Throwable cause ) {
         super( message, cause );
      }

   }
}
