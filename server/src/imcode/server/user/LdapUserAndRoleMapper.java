package imcode.server.user;

import org.apache.log4j.Logger;

import javax.naming.*;
import javax.naming.directory.*;
import java.util.*;

/**
 * The documentMapper maps LDAP attributes to Imcms internal user object.
 * A default way to map is to use the popular inetOrgPerson (2.16.840.1.113730.3.2.2) schema found in Netscape Directory Server <br>
 * The inetOrgPerson is based on organizationalPerson (2.5.6.7) that is based on person (2.5.6.7) that is based on top (2.5.6.0)<br>
 * See for example * @http://www.cio.ufl.edu/projects/directory/ldap-schema/oc-INETORGPERSON.html
 * or @link http://ldap.akbkhome.com/objectclasstree/inetOrgPerson.html for details witch attributes that exists.
 */

public class LdapUserAndRoleMapper implements Authenticator, UserAndRoleMapper {

    private final static Logger log = Logger.getLogger( "imcode.server.user.LdapUserAndRoleMapper" );

    public static final String DEFAULT_LDAP_ROLE = "LDAP";
    final static String AUTHENTICATION_TYPE_SIMPLE = "simple";

    private static final String LDAP_ATTRIBUTE_DISTINGUISHED_NAME = "distinguishedName";

    /** The following constanst are mapped to the Imcms internal user tables.
     /* Where the loginname is mapped to the attribute attribute
     /* and the password is ignored */

    /**
     * From person oid=2.5.6.7
     */
    private static final String PERSON_SURNAME = "sn";
    private static final String PERSON_TELEPHONE_NUMBER = "telephoneNumber";

    /**
     * From organizationalPerson oid=2.5.6.7
     */
    private static final String ORGANIZATIONALPERSON_TITLE = "title";
    private static final String ORGANIZATIONALPERSON_STATE_OR_PROVINCE_NAME = "st";
    private static final String ORGANIZATIONALPERSON_POSTAL_CODE = "postalCode";
    private static final String ORGANIZATIONALPERSON_STREET_ADRESS = "streetAddress";
    //   static final String ORGANIZATIONALPERSON_ORGANIZATIONAL_UNIT_NAME = "ou";

    /**
     * From inetOrgPerson oid=2.16.840.1.113730.3.2.2
     */
    private static final String INETORGPERSON_GIVEN_NAME = "givenName";
    private static final String INETORGPERSON_MAIL = "mail";
    private static final String INETORGPERSON_HOME_PHONE = "homePhone";
    private static final String INETORGPERSON_MOBILE = "mobile";
    private static final String INETORGPERSON_LOCALITY_NAME = "l";
    //   static final String INETORGPERSON_PREFERED_LANGUAGE = "preferredLanguage";
    //   static final String INETORGPERSON_USER_IDENTITY = "uid";

    /**
     * Non standard taken from microsofts user oid=1.2.840.113556.1.5.9
     * http://www.unav.es/cti/ldap-smb/ldap-smb-AD-schemas.html
     */
    private static final String NONSTANDARD_USERID = "sAMAccountName";

    /**
     * nonstandard, probably Microsoft specific
     */
    static final String NONSTANDARD_COMPANY = "company";
    private static final String NONSTANDARD_COUNTRY = "co";

    private DirContext ctx = null;
    private HashMap userFieldLdapMappings = null;
    private static final int USER_TYPE_AUTHENTICATED = 1;

    private String ldapURL;
    private String ldapAuthenticationType;
    private String ldapUserObjectClass = null;
    private String ldapUserIdentifyingAttribute = null;
    private String ldapUserName;
    private String ldapPassword;
    private String[] ldapAttributesAutoMappedToRoles;

    public LdapUserAndRoleMapper( Properties ldapConfig ) throws LdapInitException {
        String ldapUrl = ldapConfig.getProperty( "LdapUrl" );
        String ldapAuthenticationType = LdapUserAndRoleMapper.AUTHENTICATION_TYPE_SIMPLE;
        String ldapUserName = ldapConfig.getProperty( "LdapUserName" );
        String ldapPassword = ldapConfig.getProperty( "LdapPassword" );
        String ldapUserObjectClass = ldapConfig.getProperty( "LdapUserObjectClass" );
        String ldapUserIdentifyingAttribute = ldapConfig.getProperty( "LdapUserIdentifyingAttribute" );
        String ldapStringOfAttributesMappedToRoles = ldapConfig.getProperty( "LdapAttributesMappedToRoles" );
        String[] ldapAttributesMappedToRoles = splitStringOnCommasAndSpaces( ldapStringOfAttributesMappedToRoles );

        init( ldapUrl,
              ldapAuthenticationType,
              ldapUserObjectClass,
              ldapUserIdentifyingAttribute,
              ldapUserName,
              ldapPassword,
              ldapAttributesMappedToRoles );
    }

    /**
     * @param ldapUrl                The full path to where the ldap-service is located _and_ the node where to start the searches, e.g.  "ldap://computername:389/CN=Users,DC=companyName,DC=com"
     * @param ldapAuthenticationType Curently only AUTHENTICATION_TYPE_SIMPLE is suported
     * @param ldapUserName           A name that is used to log in (bind) to the ldap server
     * @param ldapPassword           A password that i used to log in (bind) to the ldap server
     */

    public LdapUserAndRoleMapper( String ldapUrl,
                                  String ldapAuthenticationType,
                                  String ldapUserObjectClass,
                                  String ldapUserIdentifyingAttribute,
                                  String ldapUserName,
                                  String ldapPassword,
                                  String[] ldapAttributesMappedToRoles ) throws LdapInitException {
        init( ldapUrl,
              ldapAuthenticationType,
              ldapUserObjectClass,
              ldapUserIdentifyingAttribute,
              ldapUserName,
              ldapPassword,
              ldapAttributesMappedToRoles );
    }

    private void init( String ldapURL,
                       String ldapAuthenticationType,
                       String ldapUserObjectClass,
                       String ldapUserIdentifyingAttribute,
                       String ldapUserName,
                       String ldapPassword,
                       String[] ldapAttributesAutoMappedToRoles ) throws LdapInitException {
        this.ldapAttributesAutoMappedToRoles = ldapAttributesAutoMappedToRoles;
        this.ldapUserIdentifyingAttribute = ldapUserIdentifyingAttribute;
        this.userFieldLdapMappings = createLdapMappings();

        this.ldapURL = ldapURL;
        this.ldapUserObjectClass = ldapUserObjectClass;
        this.ldapAuthenticationType = ldapAuthenticationType;
        this.ldapUserName = ldapUserName;
        this.ldapPassword = ldapPassword;

        setupInitialDirContext();
    }

    private void setupInitialDirContext() throws LdapInitException {
        try {
            ctx = loginAndGetInitialDirContext( ldapURL, ldapAuthenticationType, ldapUserName, ldapPassword );
        } catch ( AuthenticationException ex ) {
            throw new LdapInitException(
                    "Authentication failed, using login: '" + ldapUserName + "', password: '" + ldapPassword + "'", ex );
        } catch ( NameNotFoundException ex ) {
            throw new LdapInitException( "Root not found: " + ldapURL, ex );
        } catch ( NamingException ex ) {
            throw new LdapInitException( "Failed to create LDAP context " + ldapURL, ex );
        }
    }

    public class LdapInitException extends Exception {

        private LdapInitException( String message, Throwable cause ) {
            super( message, cause );
        }
    }

    public boolean authenticate( String loginName, String password ) {
        boolean userAuthenticates = false;
        try {
            Map userAttributes = searchForUserAttributes( loginName,
                                                          new String[]{LDAP_ATTRIBUTE_DISTINGUISHED_NAME} );
            if ( null != userAttributes ) {

                String userDistinguishedName = (String)userAttributes.get( LDAP_ATTRIBUTE_DISTINGUISHED_NAME );

                if ( null != userDistinguishedName ) {
                    loginAndGetInitialDirContext( ldapURL,
                                                ldapAuthenticationType,
                                                userDistinguishedName,
                                                password );
                    userAuthenticates = true;
                }
            }
        } catch ( AuthenticationException ex ) {
            userAuthenticates = false;
        } catch ( NamingException ex ) {
            log.warn( "Failed to get ldap context.", ex );
            userAuthenticates = false;
        }
        return userAuthenticates;
    }

    public UserDomainObject getUser( String loginName ) {
        UserDomainObject result = null;

        Map attributeMap = searchForUserAttributes( loginName, null );

        if ( null != attributeMap ) {
            result = createUserFromLdapAttributes( attributeMap );

            result.setLoginName( loginName );
            result.setActive( true );
        }
        return result;
    }

    private Map createMapFromSearchResult( SearchResult searchResult ) {
        NamingEnumeration attribEnum = searchResult.getAttributes().getAll();

        Map ldapAttributeValues = new HashMap();
        try {
            if(!attribEnum.hasMore()){
              String dn = searchResult.getName();
              ldapAttributeValues.put(LDAP_ATTRIBUTE_DISTINGUISHED_NAME,dn);
            }
        } catch ( NamingException e ) {
            log.error( e );
        }

        while ( attribEnum.hasMoreElements() ) {
            Attribute attribute = (Attribute)attribEnum.nextElement();
            String attributeName1 = attribute.getID();
            String attributeValue = null;
            try {
                attributeValue = attribute.get().toString();
            } catch ( NamingException e ) {
                log.error( e );
            }
            ldapAttributeValues.put( attributeName1, attributeValue );
        }
        return ldapAttributeValues;
    }

    private UserDomainObject createUserFromLdapAttributes( Map ldapAttributeValues ) {
        UserDomainObject newlyFoundLdapUser = new UserDomainObject();

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
        // result.put("lang_id",);

        return result;
    }

    private String getValueForUserField( String userFieldName, Map ldapAttributeValues ) {
        String ldapAttribute = (String)userFieldLdapMappings.get( userFieldName );
        String value = (String)ldapAttributeValues.get( ldapAttribute );
        if ( null == value ) {
            value = "";
        }
        return value;
    }

    private static DirContext loginAndGetInitialDirContext( String ldapURL, String ldapAuthenticationType,
                                                          String ldapUserName, String ldapPassword )
            throws NamingException {
        String ContextFactory = "com.sun.jndi.ldap.LdapCtxFactory";

        Hashtable env = new Hashtable();
        env.put( Context.INITIAL_CONTEXT_FACTORY, ContextFactory );
        env.put( Context.PROVIDER_URL, ldapURL );
        env.put( Context.SECURITY_AUTHENTICATION, ldapAuthenticationType );
        env.put( Context.SECURITY_PRINCIPAL, ldapUserName );
        env.put( Context.SECURITY_CREDENTIALS, ldapPassword );
        DirContext ctx = new InitialDirContext( env );
        return ctx;
    }

    public String[] getRoleNames( UserDomainObject user ) {
        String loginName = user.getLoginName();

        Map attributeMappedRoles = searchForUserAttributes( loginName, ldapAttributesAutoMappedToRoles );
        HashSet roles = new HashSet( attributeMappedRoles.values() );

        String[] rolesArray = new String[roles.size() + 1];
        roles.toArray( rolesArray );

        rolesArray[rolesArray.length - 1] = DEFAULT_LDAP_ROLE;

        return rolesArray;
    }

    private Map searchForUserAttributes( String loginName, String[] attributesToReturn ) {
        Map attributeMap = null;

        try {
            try {
                attributeMap = trySearchForUserAttributes( loginName, attributesToReturn );
            } catch ( CommunicationException e ) {
                log.warn( "Problem communicating with LDAP server, reconnecting.", e );
                attributeMap = reconnectAndRetrySearchForUserAttributes( loginName, attributesToReturn );
            }
        } catch ( NamingException e ) {
            log.warn( "Could not find user " + loginName, e );
        }
        return attributeMap;
    }

    private Map reconnectAndRetrySearchForUserAttributes( String loginName, String[] attributesToReturn )
            throws NamingException {
        Map attributeMap = null;
        try {
            setupInitialDirContext();
        } catch ( LdapInitException e ) {
            log.fatal( "Could not reconnect to LDAP server.", e );
        }
        attributeMap = trySearchForUserAttributes( loginName, attributesToReturn );
        return attributeMap;
    }

    private Map trySearchForUserAttributes( String loginName, String[] attributesToReturn ) throws NamingException {
        String searchFilter = "(&(objectClass=" + ldapUserObjectClass + ")(" + ldapUserIdentifyingAttribute + "="
                              + loginName
                              + "))";
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope( SearchControls.SUBTREE_SCOPE );
        searchControls.setReturningAttributes( attributesToReturn );

        NamingEnumeration enum = ctx.search( "", searchFilter, searchControls );
        boolean foundUser = enum != null && enum.hasMore();

        Map attributeMap = null;

        if ( foundUser ) {
            SearchResult searchResult = (SearchResult)enum.nextElement();
            attributeMap = createMapFromSearchResult( searchResult );
        } else {
            log.debug( "Could not find user " + loginName );
        }
        return attributeMap;
    }

    public String[] getAllRoleNames() {
        return new String[]{DEFAULT_LDAP_ROLE};
    }

    public RoleDomainObject getRoleByName( String roleName ) {
        // FIXME Return RoleDomainObject for DEFAULT_LDAP_ROLE? Needs ID that doesn't exist until it's in the database.
        throw new UnsupportedOperationException( "getRoleByName(String roleName) not supported for " + getClass() );
    }

    private static String[] splitStringOnCommasAndSpaces( String stringToSplit ) {
        StringTokenizer attributesTokenizer = new StringTokenizer( stringToSplit, ", " );
        String[] tokens = new String[attributesTokenizer.countTokens()];
        for ( int i = 0; i < tokens.length; ++i ) {
            tokens[i] = attributesTokenizer.nextToken();
        }
        return tokens;
    }

}
