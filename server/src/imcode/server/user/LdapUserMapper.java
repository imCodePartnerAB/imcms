package imcode.server.user;

import org.apache.log4j.Logger;

import javax.naming.*;
import javax.naming.directory.*;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/*
The use of LDAP simple auth for secret data or update is NOT recommended.
If you want to use LDAP for that, check out RFC 2829 and RFC 2830.
*/

/**
 * The mapper maps LDAP attributes to Imcms internal user objekt.
 * This mapper is based on the popular inetOrgPerson (2.16.840.1.113730.3.2.2) schema found in Netscape Directory Server <br>
 * The inetOrgPerson is based on rganizationalPerson (2.5.6.7) that is based on person (2.5.6.7) that is based on top (2.5.6.0)<br>
 * See for example * @http://www.cio.ufl.edu/projects/directory/ldap-schema/oc-INETORGPERSON.html
 * or @link http://ldap.akbkhome.com/objectclasstree/inetOrgPerson.html
 * for details witch attributes that exists.
 */

public class LdapUserMapper implements UserMapper {

   /** The following constanst are mapped to the Imcms internal user tables.
    /* Where the loginname is mapped to the attribute attribute
    /* and the password is ignored */

   /** From person oid=2.5.6.7 */
   private static final String PERSON_SURNAME = "sn";
   private static final String PERSON_TELEPHONE_NUMBER = "telephoneNumber";

   /** From organizationalPerson oid=2.5.6.7 */
   private static final String ORGANIZATIONALPERSON_TITLE = "title";
   private static final String ORGANIZATIONALPERSON_STATE_OR_PROVINCE_NAME = "st";
   private static final String ORGANIZATIONALPERSON_POSTAL_CODE = "postalCode";
   private static final String ORGANIZATIONALPERSON_STREET_ADRESS = "streetAddress";
   //   private static final String ORGANIZATIONALPERSON_ORGANIZATIONAL_UNIT_NAME = "ou";

   /** From inetOrgPerson oid=2.16.840.1.113730.3.2.2 */
   private static final String INETORGPERSON_GIVEN_NAME = "givenName";
   private static final String INETORGPERSON_MAIL = "mail";
   private static final String INETORGPERSON_HOME_PHONE = "homePhone";
   private static final String INETORGPERSON_MOBILE = "mobile";
   private static final String INETORGPERSON_LOCALITY_NAME = "l";
   //   private static final String INETORGPERSON_PREFERED_LANGUAGE = "preferredLanguage";
   //   private static final String INETORGPERSON_USER_IDENTITY = "uid";

   /** Non standard */
   private static final String NONSTANDARD_USERID = "sAMAccountName";
   private static final String NONSTANDARD_COMPANY = "company";
   private static final String NONSTANDARD_COUNTRY = "co";

   protected static final String DEFAULT_LDAP_ROLE = "LDAP";

   private DirContext ctx = null;
   private String userIdentifier = null;
   private HashMap userFieldLdapMappings = null;
   private String[] ldapAttributesAutoMappedToRoles;

   public LdapUserMapper( String ldapServerURL, String ldapAuthenticationType, String ldapUserName, String ldapPassword, String[] ldapAttributesAutoMappedToRoles ) throws LdapInitException {
      this.ldapAttributesAutoMappedToRoles = ldapAttributesAutoMappedToRoles;
      this.userIdentifier = NONSTANDARD_USERID;
      this.userFieldLdapMappings = createLdapMappings();

      try {
         ctx = staticSetupInitialDirContext( ldapServerURL, ldapAuthenticationType, ldapUserName, ldapPassword );
      } catch( AuthenticationException ex ) {
         throw new LdapInitException( "Authentication failed, using login: '" + ldapUserName + "', password: '" + ldapPassword + "'", ex );
      } catch( NameNotFoundException ex ) {
         throw new LdapInitException( "Root not found: " + ldapServerURL, ex );
      } catch( NamingException ex ) {
         throw new LdapInitException( "Failed to create LDAP context " + ldapServerURL, ex );
      }
   }

   public class LdapInitException extends Exception {
      LdapInitException( String message, Throwable cause ) {
         super( message, cause );
      }
   }

   public User getUser( String loginName ) {
      User result = null;

      Map attributeMap = searchForUserAttributes(loginName,null) ;

      if (null != attributeMap) {
         result = createUserFromLdapAttributes( attributeMap );

         result.setLoginName( loginName );
         result.setActive( true );
      }

      return result;
   }

   private Map createMapFromSearchResult( SearchResult searchResult ) {
      NamingEnumeration attribEnum = searchResult.getAttributes().getAll();

      HashMap ldapAttributeValues = new HashMap();
      while( attribEnum.hasMoreElements() ) {
         Attribute attribute = (Attribute)attribEnum.nextElement();
         String attributeName1 = attribute.getID();
         String attributeValue = null;
         try {
            attributeValue = attribute.get().toString();
         } catch( NamingException e ) {
            getLogger().error( e );
         }
         ldapAttributeValues.put( attributeName1, attributeValue );
      }
      return ldapAttributeValues;
   }

   private User createUserFromLdapAttributes( Map ldapAttributeValues ) {
      User newlyFoundLdapUser = new User();

      String value = getValueForUserField( "lastName", ldapAttributeValues );
      newlyFoundLdapUser.setLastName( value );

      value = getValueForUserField( "address", ldapAttributeValues );
      newlyFoundLdapUser.setAddress( value );

      value = getValueForUserField( "city", ldapAttributeValues );
      newlyFoundLdapUser.setCity( value );

      value = getValueForUserField( "county_council", ldapAttributeValues );
      newlyFoundLdapUser.setCountyCouncil( value );

      value = getValueForUserField( "emailAddress", ldapAttributeValues );
      newlyFoundLdapUser.setEmailAddress( value );

      value = getValueForUserField( "firstName", ldapAttributeValues );
      newlyFoundLdapUser.setFirstName( value );

      value = getValueForUserField( "homePhone", ldapAttributeValues );
      newlyFoundLdapUser.setHomePhone( value );

      value = getValueForUserField( "title", ldapAttributeValues );
      newlyFoundLdapUser.setTitle( value );

      value = getValueForUserField( "mobilePhone", ldapAttributeValues );
      newlyFoundLdapUser.setMobilePhone( value );

      value = getValueForUserField( "workPhone", ldapAttributeValues );
      newlyFoundLdapUser.setWorkPhone( value );

      value = getValueForUserField( "zip", ldapAttributeValues );
      newlyFoundLdapUser.setZip( value );

      value = getValueForUserField( "city", ldapAttributeValues );
      newlyFoundLdapUser.setCity( value );

      value = getValueForUserField( "company", ldapAttributeValues );
      newlyFoundLdapUser.setCompany( value );

      value = getValueForUserField( "country", ldapAttributeValues );
      newlyFoundLdapUser.setCountry( value );
      return newlyFoundLdapUser;
   }

   public User getUser( int id ) {
      // todo
      return null;
   }

   private HashMap createLdapMappings() {
      HashMap result = new HashMap();
      result.put( "loginName", NONSTANDARD_USERID );
      result.put( "firstName", INETORGPERSON_GIVEN_NAME );
      result.put( "lastName", PERSON_SURNAME );
      result.put( "title", ORGANIZATIONALPERSON_TITLE );
      result.put( "company", NONSTANDARD_COMPANY );
      result.put( "address", ORGANIZATIONALPERSON_STREET_ADRESS );
      result.put( "city", INETORGPERSON_LOCALITY_NAME );
      result.put( "zip", ORGANIZATIONALPERSON_POSTAL_CODE );
      result.put( "country", NONSTANDARD_COUNTRY );
      result.put( "county_council", ORGANIZATIONALPERSON_STATE_OR_PROVINCE_NAME );
      result.put( "emailAddress", INETORGPERSON_MAIL );
      result.put( "workPhone", PERSON_TELEPHONE_NUMBER );
      result.put( "mobilePhone", INETORGPERSON_MOBILE );
      result.put( "homePhone", INETORGPERSON_HOME_PHONE );
      // todo:
      //      result.put("lang_id",);
      //      result.put("user_type",);
      //      result.put("active",);
      //      result.put("create_date",);
      return result;
   }

   private String getValueForUserField( String userFieldName, Map ldapAttributeValues ) {
      String ldapAttribute = (String)userFieldLdapMappings.get( userFieldName );
      String value = (String)ldapAttributeValues.get( ldapAttribute );
      return value;
   }

   private static DirContext staticSetupInitialDirContext( String ldapServerURL, String ldapAuthenticationType, String ldapUserName, String ldapPassword ) throws NamingException {
      String ContextFactory = "com.sun.jndi.ldap.LdapCtxFactory";
      Hashtable env = new Hashtable();
      env.put( Context.INITIAL_CONTEXT_FACTORY, ContextFactory );
      env.put( Context.PROVIDER_URL, ldapServerURL );
      env.put( Context.SECURITY_AUTHENTICATION, ldapAuthenticationType );
      env.put( Context.SECURITY_PRINCIPAL, ldapUserName );
      env.put( Context.SECURITY_CREDENTIALS, ldapPassword );
      DirContext ctx = new InitialDirContext( env );
      return ctx;
   }

   public void updateUser( String loginName, User user ) {
      // read only for now
      throw new UnsupportedOperationException();
   }

   public void addUser( User newUser ) {
      // read only for now
      throw new UnsupportedOperationException();
   }

   private Logger getLogger() {
      return Logger.getLogger( this.getClass() );
   }

   public String[] getRoleNames( User user ) {
      String loginName = user.getLoginName();
      String[] attributesToReturn = ldapAttributesAutoMappedToRoles;

      searchForUserAttributes( loginName, attributesToReturn );

      String[] result = new String[]{DEFAULT_LDAP_ROLE};
      return result;
   }

   private Map searchForUserAttributes( String loginName, String[] attributesToReturn ) {
      Map attributeMap = null;

      NamingEnumeration enum;

      Attributes loginNameAttribute = new BasicAttributes( userIdentifier, loginName );
      boolean foundUser = false;
      try {
         enum = ctx.search( "", loginNameAttribute, attributesToReturn );
         foundUser = enum != null && enum.hasMore();
         if( foundUser ) {
            SearchResult searchResult = (SearchResult)enum.nextElement();
            attributeMap = createMapFromSearchResult( searchResult );
         } else {
            getLogger().warn( "Could not find user " + enum );
         }
      } catch( NamingException e ) {
         getLogger().warn( "Could not find user", e );
      }
      return attributeMap;
   }

   public String[] getAllRoleNames() {
      return new String[]{DEFAULT_LDAP_ROLE};
   }

}
