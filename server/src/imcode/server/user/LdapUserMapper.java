package imcode.server.user;

import imcode.server.User;

import javax.naming.directory.*;
import javax.naming.*;
import java.util.Hashtable;

import org.apache.log4j.Logger;

public class LdapUserMapper implements UserMapper {
   private static final String GIVEN_NAME = "GivenName";

   private DirContext ctx = null;

   LdapUserMapper() throws LdapInitException {

      String ldapAuthenticationType = "simple";
      String ldapUserName = "imcode\\hasbra";
      String ldapPassword = "hasbra";
      String ldapServerURL = "ldap://loke:389/CN=Users,DC=imcode,DC=com";

      try {
         ctx = s_setupInitialDirContext( ldapServerURL, ldapAuthenticationType, ldapUserName, ldapPassword );
      } catch( AuthenticationException ex ) {
         throw new LdapInitException( "Authentication failed, using login: '" + ldapUserName + "', password: '" + ldapPassword + "'", ex );
      } catch( NameNotFoundException ex ) {
         throw new LdapInitException( "Root not found: " + ldapServerURL, ex );
      } catch( NamingException ex ) {
         throw new LdapInitException( "Failed to create LDAP context " + ldapServerURL, ex );
      }
   }

   public class LdapInitException extends Exception {
      LdapInitException(String message, Throwable cause){
         super( message, cause );
      }
   }

   private Logger getLogger() {
      return Logger.getLogger( this.getClass().getName() );
   }

   public User getUser( String loginName ) {
      User result = null;

      final String attributeName = "samaccountname=";
      final String mappingString = attributeName + loginName;
      NamingEnumeration enum = null;
      try {
         enum = ctx.search( "", mappingString, null );
         if( enum != null && enum.hasMore() ) {
            User result11 = null;
            SearchResult searchResult = (SearchResult)enum.nextElement();
            NamingEnumeration attribEnum = searchResult.getAttributes().getAll();
            result11 = mapAllPossibleAttributes( attribEnum );
            result11.setLoginName( loginName );
            result = result11;
         }
      } catch( NamingException e ) {
         result = null;
         getLogger().warn( "Could not find user", e );
      }
      return result;
   }

   public void update( String loginName, User user ) {
      // read only for now
      throw new UnsupportedOperationException();
   }

   private User mapAllPossibleAttributes( NamingEnumeration attribEnum ) {
      User user = new User();
      while( attribEnum.hasMoreElements() ) {
         Attribute attribute = (Attribute)attribEnum.nextElement();
         String attributeName = attribute.getID();
         String attributeValue = null;
         try {
            attributeValue = attribute.get().toString();
         } catch( NamingException e ) {
            getLogger().error( e );
         }
         if( GIVEN_NAME.equalsIgnoreCase( attributeName ) ) {
            user.setFirstName( attributeValue );
         }
      }
      return user;
   }

   private static DirContext s_setupInitialDirContext( String ldapServerURL, String ldapAuthenticationType, String ldapUserName, String ldapPassword ) throws NamingException {
      String ContextFactory = "com.sun.jndi.ldap.LdapCtxFactory";
      Hashtable env = new Hashtable();
      env.put( Context.INITIAL_CONTEXT_FACTORY, ContextFactory );
      env.put( Context.PROVIDER_URL, ldapServerURL );
      env.put( Context.SECURITY_AUTHENTICATION, ldapAuthenticationType );
      env.put( Context.SECURITY_PRINCIPAL, ldapUserName ); // specify the username
      env.put( Context.SECURITY_CREDENTIALS, ldapPassword );           // specify the password
      DirContext ctx = new InitialDirContext( env );
      return ctx;
   }
}
