package imcode.server.user;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import java.util.Hashtable;

public class Program {
   public static void main( String[] args ) throws NamingException {

      String ldapServerURL = "ldap://ldap-vcn1.vtd.volvo.se:389/dc=vcn,dc=ds,dc=volvo,dc=net" ;
      //String ldapServerURL = "ldap://ldap-vcn1.vtd.volvo.se:389/" ;
      String ldapAuthenticationType = "simple";
      String ldapUserName = "CN=cs-ad-ldapquery,OU=ServiceAccounts,OU=AdOperation,OU=CS,DC=vcn,DC=ds,DC=volvo,DC=net" ;
      String ldapPassword = "#D8leYS" ;

      String userName = "user" ;

      DirContext ctx = s_setaupInitialDirContext( ldapServerURL, ldapAuthenticationType, ldapUserName, ldapPassword );

      SearchControls searchControls = new SearchControls();
      searchControls.setSearchScope( SearchControls.SUBTREE_SCOPE );
      String name = "";
      // See http://www.cis.ohio-state.edu/cs/Services/rfc/rfc-text/rfc2254.txt
      String filter = "(&(objectClass=Person)(cn="+userName+"))" ;

      NamingEnumeration namingEnumeration = ctx.search( name, filter, searchControls );
      while( namingEnumeration.hasMoreElements() ) {
         SearchResult searchResult = (SearchResult)namingEnumeration.nextElement();
         System.out.println( searchResult.getAttributes().get("DistinguishedName").toString() );
         System.out.println( searchResult );
         String[] attributes = searchResult.toString().split(", ") ;
         for (int i=0; i < attributes.length; ++i) {
           System.out.println(attributes[i]) ;
         }
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
