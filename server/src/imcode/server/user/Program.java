package imcode.server.user;

import javax.naming.directory.*;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.NamingEnumeration;
import javax.naming.Name;
import java.util.Hashtable;

public class Program {
   public static void main( String[] args ) throws NamingException {

      String ldapServerURL = "ldap://loke:389/";
      String ldapAuthenticationType = "simple";
      String ldapUserName = "imcode\\hasbra";
      String ldapPassword = "hasbra";

      DirContext ctx = s_setaupInitialDirContext( ldapServerURL, ldapAuthenticationType, ldapUserName, ldapPassword );

      SearchControls searchControls = new SearchControls();
      searchControls.setSearchScope( SearchControls.ONELEVEL_SCOPE );
      String name = "CN=Users, DC=imcode, DC=com";
      String filter = "samaccountname=*"; // See http://www.cis.ohio-state.edu/cs/Services/rfc/rfc-text/rfc2254.txt

      NamingEnumeration namingEnumeration = ctx.search(name, filter, searchControls);
      while( namingEnumeration.hasMoreElements() ) {
         Object o = namingEnumeration.nextElement();
         System.out.println( o.toString() );
      }

      /*
      String[] attrIDs = {"uid", "dn", "name", "cn", "givenName", "sn", "telephonenumber", "mail", "objectClass", "title", "distinguishedName", "department"};
      Attributes matchAttrs = new BasicAttributes( true );
      NamingEnumeration enum = ctx.search( "CN=Users,DC=imcode,DC=com", matchAttrs, attrIDs );

      //Enumeration enum = ctx.list("CN=Hasse Brattberg,CN=Users,DC=imcode,DC=com") ;
      System.out.println( enum );
      while( enum.hasMoreElements() ) {
         System.out.println( enum.nextElement() );
      }
      */
   }

   private static DirContext s_setaupInitialDirContext( String ldapServerURL, String ldapAuthenticationType, String ldapUserName, String ldapPassword ) throws NamingException {
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
