package imcode.server.user;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.ExtendedProperties;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.UnhandledException;
import org.apache.log4j.Logger;

import javax.naming.*;
import javax.naming.directory.*;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * The documentMapper maps LDAP attributes to Imcms internal user object.
 * A default way to map is to use the popular inetOrgPerson (2.16.840.1.113730.3.2.2) schema found in Netscape Directory Server <br>
 * The inetOrgPerson is based on organizationalPerson (2.5.6.7) that is based on person (2.5.6.7) that is based on top (2.5.6.0)<br>
 * See for example * @http://www.cio.ufl.edu/projects/directory/ldap-schema/oc-INETORGPERSON.html
 * or @link http://ldap.akbkhome.com/objectclasstree/inetOrgPerson.html for details witch attributes that exists.
 */

public class LdapUserAndRoleRegistry implements Authenticator, UserAndRoleRegistry {

    private final static Logger log = Logger.getLogger( LdapUserAndRoleRegistry.class );

    public static final String DEFAULT_LDAP_ROLE = "LDAP";
    public final static String AUTHENTICATION_TYPE_SIMPLE = "simple";

    private static final String DISTINGUISHED_NAME = "dn";

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
    private static final String INETORGPERSON_ORGANIZATION = "o";
    static final String INETORGPERSON_USER_IDENTITY = "uid";

    private DirContext ctx = null;

    private String ldapUrl;
    private String ldapAuthenticationType;
    private String ldapUserObjectClass = null;
    private String ldapBindDn;
    private String ldapPassword;
    private String[] ldapAttributesAutoMappedToRoles;

    private Properties userPropertyNameToLdapAttributeNameMap = new Properties();

    private final static Map defaultUserPropertyNameToLdapAttributeNameMap = ArrayUtils.toMap( new String[][]{
        {"LoginName", INETORGPERSON_USER_IDENTITY},
        {"FirstName", INETORGPERSON_GIVEN_NAME},
        {"LastName", PERSON_SURNAME},
        {"Title", ORGANIZATIONALPERSON_TITLE},
        {"Company", INETORGPERSON_ORGANIZATION},
        {"Address", ORGANIZATIONALPERSON_STREET_ADRESS},
        {"City", INETORGPERSON_LOCALITY_NAME},
        {"Zip", ORGANIZATIONALPERSON_POSTAL_CODE},
        {"CountyCouncil", ORGANIZATIONALPERSON_STATE_OR_PROVINCE_NAME},
        {"EmailAddress", INETORGPERSON_MAIL},
        {"WorkPhone", PERSON_TELEPHONE_NUMBER},
        {"MobilePhone", INETORGPERSON_MOBILE},
        {"HomePhone", INETORGPERSON_HOME_PHONE},
    } );

    private static final String LDAP_USER_OBJECTCLASS__INETORGPERSON = "inetOrgPerson";
    private static final String LDAP_USER_OBJECTCLASS_DEFAULT = LDAP_USER_OBJECTCLASS__INETORGPERSON;

    public LdapUserAndRoleRegistry( Properties ldapConfig ) throws LdapInitException {
        String ldapStringOfAttributesMappedToRoles = ldapConfig.getProperty( "LdapAttributesMappedToRoles", "" );
        String[] ldapAttributesMappedToRoles = splitStringOnCommasAndSpaces( ldapStringOfAttributesMappedToRoles );
        Properties ldapUserAttributes = new Properties();
        ExtendedProperties ldapUserAttributesSubset = ExtendedProperties.convertProperties( ldapConfig ).subset( "LdapUserAttribute" );
        if ( null != ldapUserAttributesSubset ) {
            ldapUserAttributes.putAll( ldapUserAttributesSubset );
        }
        init( ldapConfig.getProperty( "LdapUrl", "ldap://localhost/" ),
              LdapUserAndRoleRegistry.AUTHENTICATION_TYPE_SIMPLE,
              ldapConfig.getProperty( "LdapUserObjectClass", LDAP_USER_OBJECTCLASS_DEFAULT ),
              ldapConfig.getProperty( "LdapBindDn", "" ),
              ldapConfig.getProperty( "LdapPassword", "" ),
              ldapAttributesMappedToRoles,
              ldapUserAttributes );
    }

    /**
     * @param ldapUrl                The full path to where the ldap-service is located _and_ the node where to start the searches, e.g.  "ldap://computername:389/CN=Users,DC=companyName,DC=com"
     * @param ldapAuthenticationType Curently only AUTHENTICATION_TYPE_SIMPLE is suported
     * @param ldapUserName           A name that is used to log in (bind) to the ldap server
     * @param ldapPassword           A password that i used to log in (bind) to the ldap server
     */

    public LdapUserAndRoleRegistry( String ldapUrl,
                                  String ldapAuthenticationType,
                                  String ldapUserObjectClass,
                                  String ldapUserName,
                                  String ldapPassword,
                                  String[] ldapAttributesMappedToRoles ) throws LdapInitException {
        init( ldapUrl,
              ldapAuthenticationType,
              ldapUserObjectClass,
              ldapUserName,
              ldapPassword,
              ldapAttributesMappedToRoles,
              new Properties() );
    }

    public String[] getAllRoleNames() {
        return new String[]{DEFAULT_LDAP_ROLE};
    }

    private void init( String ldapUrl,
                       String ldapAuthenticationType,
                       String ldapUserObjectClass,
                       String ldapUserName,
                       String ldapPassword,
                       String[] ldapAttributesAutoMappedToRoles,
                       Properties ldapUserAttributes ) throws LdapInitException {
        this.ldapAttributesAutoMappedToRoles = ldapAttributesAutoMappedToRoles;
        initLdapUserAttributesMap( ldapUserAttributes );

        this.ldapUrl = ldapUrl;
        this.ldapUserObjectClass = ldapUserObjectClass;
        this.ldapAuthenticationType = ldapAuthenticationType;
        this.ldapBindDn = ldapUserName;
        this.ldapPassword = ldapPassword;

        setupInitialDirContext();
    }

    private void initLdapUserAttributesMap( Properties ldapUserAttributes ) throws LdapInitException {
        userPropertyNameToLdapAttributeNameMap.putAll( defaultUserPropertyNameToLdapAttributeNameMap );
        userPropertyNameToLdapAttributeNameMap.putAll( ldapUserAttributes );
        Set badUserAttributes = new TreeSet( userPropertyNameToLdapAttributeNameMap.keySet() );
        String[] capitalizedSettableUserPropertyNames = getCapitalizedSettableBeanPropertyNames( UserDomainObject.class );
        badUserAttributes.removeAll( Arrays.asList( capitalizedSettableUserPropertyNames ) );
        if ( !badUserAttributes.isEmpty() ) {
            throw new LdapInitException( "Unrecognized LdapUserAttributes: "
                                         + StringUtils.join( badUserAttributes.iterator(), ", " ) );
        }
    }

    private void setupInitialDirContext() throws LdapInitException {
        try {
            ctx = loginAndGetInitialDirContext( ldapUrl, ldapAuthenticationType, ldapBindDn, ldapPassword );
        } catch ( AuthenticationException ex ) {
            throw new LdapInitException( "Authentication failed, using login: '" + ldapBindDn + "', password: '"
                                         + ldapPassword
                                         + "'", ex );
        } catch ( NameNotFoundException ex ) {
            throw new LdapInitException( "Root not found: " + ldapUrl, ex );
        } catch ( NamingException ex ) {
            throw new LdapInitException( "Failed to create LDAP context " + ldapUrl + ": " + ex.getExplanation(), ex );
        }
    }

    public boolean authenticate( String loginName, String password ) {
        boolean userAuthenticates = false;
        try {
            Map userAttributes = searchForUserAttributes( loginName,
                                                          new String[]{DISTINGUISHED_NAME} );
            if ( null != userAttributes ) {

                String userDistinguishedName = (String)userAttributes.get( DISTINGUISHED_NAME );

                if ( null != userDistinguishedName ) {
                    loginAndGetInitialDirContext( ldapUrl,
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

        Properties attributeMap = searchForUserAttributes( loginName, null );

        if ( null != attributeMap ) {
            result = createUserFromLdapAttributes( attributeMap );

            result.setLoginName( loginName );
            result.setActive( true );
        }
        return result;
    }

    private Properties createMapFromSearchResult( SearchResult searchResult, String[] attributesToReturn ) {
        NamingEnumeration attribEnum = searchResult.getAttributes().getAll();
        Properties userAttributes = new Properties();
        try {
            if ( !attribEnum.hasMore() ) {
                boolean includeDistinguishedName = null == attributesToReturn
                                                   || ArrayUtils.contains( attributesToReturn, DISTINGUISHED_NAME );
                if ( includeDistinguishedName  ) {
                    DirContext dirContext = (DirContext)searchResult.getObject();
                    String distinguishedName = dirContext.getNameInNamespace();
                    userAttributes.put( DISTINGUISHED_NAME, distinguishedName );
                }
            }
        } catch ( NamingException e ) {
            log.error( e );
        }

        while ( attribEnum.hasMoreElements() ) {
            Attribute attribute = (Attribute)attribEnum.nextElement();
            String attributeName = attribute.getID();
            String attributeValue = null;
            try {
                attributeValue = attribute.get().toString();
            } catch ( NamingException e ) {
                log.error( e );
            }
            userAttributes.setProperty( attributeName, attributeValue );
        }
        return userAttributes;
    }

    private UserDomainObject createUserFromLdapAttributes( Properties ldapAttributeValues ) {
        UserDomainObject newlyFoundLdapUser = new UserDomainObject();

        PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors( newlyFoundLdapUser );
        try {
            for ( int i = 0; i < propertyDescriptors.length; i++ ) {
                PropertyDescriptor propertyDescriptor = propertyDescriptors[i];
                if ( null == propertyDescriptor.getWriteMethod() ) {
                    continue;
                }
                String uncapitalizedPropertyName = propertyDescriptor.getName();
                String capitalizedPropertyName = StringUtils.capitalize( uncapitalizedPropertyName );
                String ldapAttributeName = userPropertyNameToLdapAttributeNameMap.getProperty( capitalizedPropertyName );
                if ( null != ldapAttributeName ) {
                    String ldapAttributeValue = ldapAttributeValues.getProperty( ldapAttributeName );
                    if ( null != ldapAttributeValue ) {
                        BeanUtils.setProperty( newlyFoundLdapUser, uncapitalizedPropertyName, ldapAttributeValue );
                    }
                }
            }
        } catch ( IllegalAccessException e ) {
            throw new UnhandledException( e );
        } catch ( InvocationTargetException e ) {
            throw new UnhandledException( e );
        }

        return newlyFoundLdapUser;
    }

    private static DirContext loginAndGetInitialDirContext( String ldapUrl, String ldapAuthenticationType,
                                                            String ldapUserName, String ldapPassword )
            throws NamingException {

        Hashtable env = new Hashtable();
        env.put( Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory" );
        env.put( Context.PROVIDER_URL, ldapUrl );
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

    private Properties searchForUserAttributes( String loginName, String[] attributesToReturn ) {
        Properties attributeMap = null;

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

    private Properties reconnectAndRetrySearchForUserAttributes( String loginName, String[] attributesToReturn )
            throws NamingException {
        try {
            setupInitialDirContext();
        } catch ( LdapInitException e ) {
            log.fatal( "Could not reconnect to LDAP server.", e );
        }
        return trySearchForUserAttributes( loginName, attributesToReturn );
    }

    private Properties trySearchForUserAttributes( String loginName, String[] attributesToReturn ) throws NamingException {
        String ldapUserIdentifyingAttribute = userPropertyNameToLdapAttributeNameMap.getProperty( "LoginName" );

        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope( SearchControls.SUBTREE_SCOPE );
        searchControls.setReturningAttributes( attributesToReturn );
        searchControls.setReturningObjFlag( true );

        String searchFilterExpr = "(&(objectClass={0})({1}={2}))";

        NamingEnumeration enumeration = ctx.search( "", searchFilterExpr, new Object[] { ldapUserObjectClass, ldapUserIdentifyingAttribute, loginName }, searchControls );

        boolean foundUser = enumeration != null && enumeration.hasMore();

        Properties attributeMap = null;

        if ( foundUser ) {
            SearchResult searchResult = (SearchResult)enumeration.nextElement();
            attributeMap = createMapFromSearchResult( searchResult, attributesToReturn );
        } else {
            log.debug( "Could not find user " + loginName );
        }
        return attributeMap;
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

    private static String[] getCapitalizedSettableBeanPropertyNames( Class beanClass ) {
        PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors( beanClass );
        List settableBeanPropertyNames = new ArrayList();
        for ( int i = 0; i < propertyDescriptors.length; i++ ) {
            PropertyDescriptor propertyDescriptor = propertyDescriptors[i];
            if ( null == propertyDescriptor.getWriteMethod() ) {
                continue;
            }
            String uncapitalizedPropertyName = propertyDescriptor.getName();
            String capitalizedPropertyName = StringUtils.capitalize( uncapitalizedPropertyName );
            settableBeanPropertyNames.add( capitalizedPropertyName );
        }
        return (String[])settableBeanPropertyNames.toArray( new String[settableBeanPropertyNames.size()] );
    }

    public class LdapInitException extends Exception {

        public LdapInitException( String message ) {
            super( message );
        }

        private LdapInitException( String message, Throwable cause ) {
            super( message, cause );
        }

    }

}
