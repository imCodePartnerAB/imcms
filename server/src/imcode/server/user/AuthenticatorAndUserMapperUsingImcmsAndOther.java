package imcode.server.user;

import imcode.server.User;
import org.apache.log4j.Logger;

public class AuthenticatorAndUserMapperUsingImcmsAndOther implements UserMapper, Authenticator {
   private ImcmsAuthenticatorAndUserMapper imcms;
   private UserMapper ldap;

   public AuthenticatorAndUserMapperUsingImcmsAndOther(ImcmsAuthenticatorAndUserMapper imcms, UserMapper ldap) {
      this.imcms = imcms;
      this.ldap = ldap;
   }

   public boolean authenticate( String loginName, String password ) {
      boolean userExistsInImcms = imcms.authenticate(loginName,password);
      return  userExistsInImcms ;
   }

   public User getUser( String loginName ) {
      User imcmsUser = imcms.getUser(loginName);
      User ldapUser  = ldap.getUser(loginName) ;
      boolean imcmsUserExists = null != imcmsUser ;
      boolean ldapUserExists  = null != ldapUser ;
      boolean imcmsUserIsInternal = (null != imcmsUser) && imcmsUser.isImcmsInternal() ;

      if ( imcmsUserExists && ldapUserExists && imcmsUserIsInternal ) {
         throw new InternalUserCollisionException("An imcms-internal user was found in external directory.",null) ;
      } else if( !imcmsUserExists && !ldapUserExists ) {
         return null;
      } else if (ldapUserExists) {
         imcms.update( loginName, ldapUser );
         User updated = imcms.getUser( loginName );
         return updated;
      } else if( imcmsUserExists && !ldapUserExists ) {
         imcmsUser.setActive( false );
         imcms.update( loginName, imcmsUser );
         return null;
      } else if( imcmsUserIsInternal ) {
         return imcmsUser;
      }
      getLogger().fatal("BUG!, check " + getClass().getName() + ".getUser() for fallthrough");
      return null;
   }

   private Logger getLogger() {
      return Logger.getLogger( this.getClass().getName() );
   }

   public void update( String loginName, User newUserData ) {
   }

   public class InternalUserCollisionException extends RuntimeException {
      InternalUserCollisionException(String message, Throwable cause){
         super( message, cause );
      }

   }
}
