package imcode.server.user;

import com.google.common.collect.ImmutableSet;
import com.imcode.net.ldap.LdapClient;
import com.imcode.net.ldap.LdapClientException;
import com.imcode.net.ldap.LdapConnection;
import com.imcode.net.ldap.LdapConnectionPool;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.ExtendedProperties;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.UnhandledException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.naming.directory.SearchControls;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * The documentMapper maps LDAP attributes to Imcms internal user object.
 * The default mapping uses the popular inetOrgPerson (2.16.840.1.113730.3.2.2) schema found in Netscape Directory Server <br>
 * The inetOrgPerson is based on organizationalPerson (2.5.6.7) that is based on person (2.5.6.6) that is based on top (2.5.6.0)<br>
 * See for example * @http://www.cio.ufl.edu/projects/directory/ldap-schema/oc-INETORGPERSON.html
 * or @link http://ldap.akbkhome.com/objectclasstree/inetOrgPerson.html for details witch attributes that exists.
 */

public class LdapUserAndRoleRegistry implements Authenticator, UserAndRoleRegistry {

    public static final String DEFAULT_LDAP_ROLE = "LDAP";
    static final String INETORGPERSON_USER_IDENTITY = "uid";

    /** The following constanst are mapped to the Imcms internal user tables.
     /* Where the loginname is mapped to the attribute attribute
     /* and the password is ignored */
    private final static Logger LOG = LogManager.getLogger(LdapUserAndRoleRegistry.class);
    /**
     * From person oid=2.5.6.6
     */
    private static final String PERSON_SURNAME = "sn";
    private static final String PERSON_TELEPHONE_NUMBER = "telephoneNumber";
    /**
     * From organizationalPerson oid=2.5.6.7
     */
    private static final String ORGANIZATIONALPERSON_TITLE = "title";
    private static final String ORGANIZATIONALPERSON_STATE_OR_PROVINCE_NAME = "st";
    private static final String ORGANIZATIONALPERSON_POSTAL_CODE = "postalCode";
    //   static final String ORGANIZATIONALPERSON_ORGANIZATIONAL_UNIT_NAME = "ou";
    private static final String ORGANIZATIONALPERSON_STREET_ADRESS = "streetAddress";
    /**
     * From inetOrgPerson oid=2.16.840.1.113730.3.2.2
     */
    private static final String INETORGPERSON_GIVEN_NAME = "givenName";
    private static final String INETORGPERSON_MAIL = "mail";
    private static final String INETORGPERSON_HOME_PHONE = "homePhone";
    private static final String INETORGPERSON_MOBILE = "mobile";
    private static final String INETORGPERSON_LOCALITY_NAME = "l";
    private static final String INETORGPERSON_ORGANIZATION = "o";
    private final static Map DEFAULT_USER_PROPERTY_NAME_TO_LDAP_ATTRIBUTE_NAME_MAP = ArrayUtils.toMap(new String[][]{
            {"LoginName", INETORGPERSON_USER_IDENTITY},
            {"FirstName", INETORGPERSON_GIVEN_NAME},
            {"LastName", PERSON_SURNAME},
            {"Title", ORGANIZATIONALPERSON_TITLE},
            {"Company", INETORGPERSON_ORGANIZATION},
            {"Address", ORGANIZATIONALPERSON_STREET_ADRESS},
            {"City", INETORGPERSON_LOCALITY_NAME},
            {"Zip", ORGANIZATIONALPERSON_POSTAL_CODE},
            {"Province", ORGANIZATIONALPERSON_STATE_OR_PROVINCE_NAME},
            {"EmailAddress", INETORGPERSON_MAIL},
            {"WorkPhone", PERSON_TELEPHONE_NUMBER},
            {"MobilePhone", INETORGPERSON_MOBILE},
            {"HomePhone", INETORGPERSON_HOME_PHONE},
    });
    private static final String LDAP_USER_OBJECTCLASS__INETORGPERSON = "inetOrgPerson";
    private static final String LDAP_USER_OBJECTCLASS_DEFAULT = LDAP_USER_OBJECTCLASS__INETORGPERSON;
    private final LdapClient ldapClient;
    private final String ldapReadTimeoutMillis;
    private final int ldapMaxConnections;
    private final int ldapConnectionExpirySeconds;
    private final String ldapUserObjectClass;
    private final String[] ldapAttributesAutoMappedToRoles;
    private LdapConnectionPool ldapConnectionPool;
    private Properties userPropertyNameToLdapAttributeNameMap = new Properties();
    private String ldapUsername;
    private String ldapPassword;

    public LdapUserAndRoleRegistry(Properties ldapConfig) throws LdapClientException {
        this(ldapConfig.getProperty("LdapUrl", "ldap://localhost/"),
                ldapConfig.getProperty("LdapUserObjectClass", LDAP_USER_OBJECTCLASS_DEFAULT),
                ldapConfig.getProperty("LdapBindDn", ""),
                ldapConfig.getProperty("LdapPassword", ""),
                ldapConfig.getProperty("LdapReadTimeoutMillis", "5000"),
                ldapConfig.getProperty("LdapMaxConnections", "20"),
                ldapConfig.getProperty("LdapConnectionExpirySeconds", "120"),
                buildAttributesMappedToRoles(ldapConfig),
                buildUserAttributes(ldapConfig)
        );
    }

    /**
     * @param ldapUrl                     The full path to where the ldap-service is located _and_ the node where to start the searches, e.g.  "ldap://computername:389/CN=Users,DC=companyName,DC=com"
     * @param ldapUserName                A name that is used to log in (bind) to the ldap server
     * @param ldapPassword                A password that i used to log in (bind) to the ldap server
     * @param ldapReadTimeoutMillis
     * @param ldapMaxConnections
     * @param ldapConnectionExpirySeconds
     * @param ldapUserAttributes
     */
    public LdapUserAndRoleRegistry(String ldapUrl,
                                   String ldapUserObjectClass,
                                   String ldapUserName,
                                   String ldapPassword,
                                   String ldapReadTimeoutMillis,
                                   String ldapMaxConnections,
                                   String ldapConnectionExpirySeconds,
                                   String[] ldapAttributesMappedToRoles,
                                   Properties ldapUserAttributes) throws LdapClientException {

        ldapAttributesAutoMappedToRoles = ldapAttributesMappedToRoles;
        initLdapUserAttributesMap(ldapUserAttributes);

        this.ldapUserObjectClass = ldapUserObjectClass;
        ldapClient = new LdapClient(ldapUrl);

        this.ldapUsername = ldapUserName;
        this.ldapPassword = ldapPassword;
        this.ldapReadTimeoutMillis = ldapReadTimeoutMillis;
        this.ldapMaxConnections = Integer.parseInt(ldapMaxConnections);
        this.ldapConnectionExpirySeconds = Integer.parseInt(ldapConnectionExpirySeconds);

        createLdapConnection();
    }

    private static String[] buildAttributesMappedToRoles(Properties ldapConfig) {
        String ldapStringOfAttributesMappedToRoles = ldapConfig.getProperty("LdapAttributesMappedToRoles", "");
        return splitStringOnCommasAndSpaces(ldapStringOfAttributesMappedToRoles);
    }

    private static Properties buildUserAttributes(Properties ldapConfig) {
        Properties ldapUserAttributes = new Properties();
        ExtendedProperties ldapUserAttributesSubset = ExtendedProperties.convertProperties(ldapConfig).subset("LdapUserAttribute");
        if (null != ldapUserAttributesSubset) {
            ldapUserAttributes.putAll(ldapUserAttributesSubset);
        }
        return ldapUserAttributes;
    }

    private static String[] splitStringOnCommasAndSpaces(String stringToSplit) {
        StringTokenizer attributesTokenizer = new StringTokenizer(stringToSplit, ", ");
        String[] tokens = new String[attributesTokenizer.countTokens()];
        for (int i = 0; i < tokens.length; ++i) {
            tokens[i] = attributesTokenizer.nextToken();
        }
        return tokens;
    }

    private static String[] getCapitalizedSettableBeanPropertyNames(Class beanClass) {
        PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors(beanClass);
        List settableBeanPropertyNames = new ArrayList();
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            if (null == propertyDescriptor.getWriteMethod()) {
                continue;
            }
            String uncapitalizedPropertyName = propertyDescriptor.getName();
            String capitalizedPropertyName = StringUtils.capitalize(uncapitalizedPropertyName);
            settableBeanPropertyNames.add(capitalizedPropertyName);
        }
        return (String[]) settableBeanPropertyNames.toArray(new String[settableBeanPropertyNames.size()]);
    }

    private void createLdapConnection()
            throws LdapClientException {
        ldapConnectionPool = ldapClient.bind(ldapUsername, ldapPassword, ldapReadTimeoutMillis, ldapMaxConnections, ldapConnectionExpirySeconds, TimeUnit.SECONDS);
    }

    public String[] getAllRoleNames() {
        return new String[]{DEFAULT_LDAP_ROLE};
    }

    @SuppressWarnings("unchecked")
    private void initLdapUserAttributesMap(Properties ldapUserAttributes) throws LdapClientException {
        userPropertyNameToLdapAttributeNameMap.putAll(DEFAULT_USER_PROPERTY_NAME_TO_LDAP_ATTRIBUTE_NAME_MAP);
        userPropertyNameToLdapAttributeNameMap.putAll(ldapUserAttributes);
        Set<String> badUserAttributes = new TreeSet(userPropertyNameToLdapAttributeNameMap.keySet());
        String[] capitalizedSettableUserPropertyNames = getCapitalizedSettableBeanPropertyNames(UserDomainObject.class);
        badUserAttributes.removeAll(Arrays.asList(capitalizedSettableUserPropertyNames));

        if (!badUserAttributes.isEmpty()) {
            throw new LdapClientException("Unrecognized LdapUserAttributes: "
                    + StringUtils.join(badUserAttributes.iterator(), ", "));
        }
    }

    public boolean authenticate(String loginName, String password) {
        try {
            Map userAttributes = searchForUserAttributes(loginName,
                    new String[]{LdapConnection.DISTINGUISHED_NAME});
            if (null != userAttributes) {
                String userDistinguishedName = (String) userAttributes.get(LdapConnection.DISTINGUISHED_NAME);
                return ldapClient.canBind(userDistinguishedName, password);
            }
        } catch (LdapClientException ex) {
            LOG.warn("Failed to get ldap context.", ex);
        }
        return false;
    }

    public UserDomainObject getUser(String loginName) {
        UserDomainObject result = null;

        Map<String, String> attributeMap = searchForUserAttributes(loginName, null);

        if (null != attributeMap) {
            result = createUserFromLdapAttributes(attributeMap);

            result.setLoginName(loginName);
            result.setActive(true);
        }
        return result;
    }

    private UserDomainObject createUserFromLdapAttributes(Map<String, String> ldapAttributeValues) {
        UserDomainObject newlyFoundLdapUser = new UserDomainObject();

        PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors(newlyFoundLdapUser);
        try {
            for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                if (null == propertyDescriptor.getWriteMethod()) {
                    continue;
                }
                String uncapitalizedPropertyName = propertyDescriptor.getName();
                String capitalizedPropertyName = StringUtils.capitalize(uncapitalizedPropertyName);
                String ldapAttributeName = userPropertyNameToLdapAttributeNameMap.getProperty(capitalizedPropertyName);
                if (null != ldapAttributeName) {
                    String ldapAttributeValue = ldapAttributeValues.get(ldapAttributeName);
                    if (null != ldapAttributeValue) {
                        BeanUtils.setProperty(newlyFoundLdapUser, uncapitalizedPropertyName, ldapAttributeValue);
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new UnhandledException(e);
        } catch (InvocationTargetException e) {
            throw new UnhandledException(e);
        }

        return newlyFoundLdapUser;
    }

    public String[] getRoleNames(UserDomainObject user) {
        String loginName = user.getLoginName();
        Map<String, String> attributeMappedRoles = searchForUserAttributes(loginName, ldapAttributesAutoMappedToRoles);
        Set<String> roles = new HashSet<>(attributeMappedRoles.values());
        roles.add(DEFAULT_LDAP_ROLE);
        return roles.toArray(new String[roles.size()]);
    }

    /**
     * @since 4.1.6
     */
//    private Set<String> getMappedRoleNames(String loginName) {
//        Map<String, Set<String>> mappedAttributes = searchForUserMultiAttributes(
//                loginName, mappedRoles.rolesToAttributes().attributesNames().toArray(new String[]{}));
//
//        List<P.P2<String, String>> attributeNameValuePairs = Lists.newLinkedList();
//
//        for (Map.Entry<String, Set<String>> mappedAttribute : mappedAttributes.entrySet()) {
//            String attributeName = mappedAttribute.getKey();
//            for (String attributeValue : mappedAttribute.getValue()) {
//                attributeNameValuePairs.add(P.of(attributeName, attributeValue));
//            }
//        }
//
//        if (LOG.isDebugEnabled()) {
//            LOG.debug(String.format("Found %s LDAP roles mapped to attributes for user %s: %s.",
//                    attributeNameValuePairs.size(), loginName, Joiner.on(", ").join(attributeNameValuePairs)));
//        }
//
//        Set<String> usersAdGroups = getADUserGroups(loginName, mappedRoles.rolesToAdGroups().groups());
//        Set<String> rolesNamesMappedToAttributes = mappedRoles.rolesToAttributes().roles(attributeNameValuePairs);
//        Set<String> rolesNamesMappedToAdGroups = mappedRoles.rolesToAdGroups().roles(usersAdGroups);
//        ImmutableSet.Builder<String> rolesNamesBuilder = ImmutableSet.builder();
//
//        rolesNamesBuilder.addAll(rolesNamesMappedToAttributes);
//        rolesNamesBuilder.addAll(rolesNamesMappedToAdGroups);
//
//        return rolesNamesBuilder.build();
//    }

    private Map<String, String> searchForUserAttributes(String loginName, String[] attributesToReturn) {
        Map<String, String> attributeMap = null;

        try {
            String ldapUserIdentifyingAttribute = userPropertyNameToLdapAttributeNameMap.getProperty("LoginName");

            SearchControls searchControls = new SearchControls();
            searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            searchControls.setReturningAttributes(attributesToReturn);
            searchControls.setReturningObjFlag(true);
            LdapConnection connection = null;
            try {
                connection = ldapConnectionPool.getConnection();
                Iterator<Map<String, String>> searchResult = connection.search("(&(objectClass={0})({1}={2}))",
                        new Object[]{ldapUserObjectClass, ldapUserIdentifyingAttribute, loginName},
                        searchControls);

                if (searchResult.hasNext()) attributeMap = searchResult.next();
            } finally {
                if (connection != null) {
                    connection.close();
                }
            }
        } catch (LdapClientException e) {
            LOG.warn("Could not find user " + loginName, e);
        }

        return attributeMap;
    }

    /**
     * @return a map containing attribute name mapped to values.
     * @since 4.1.6
     */
    private Map<String, Set<String>> searchForUserMultiAttributes(String loginName, String[] attributesToReturn) {
        Map<String, Set<String>> attributeMap = null;

        try {
            String ldapUserIdentifyingAttribute = userPropertyNameToLdapAttributeNameMap.getProperty("LoginName");

            SearchControls searchControls = new SearchControls();
            searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            searchControls.setReturningAttributes(attributesToReturn);

            LdapConnection connection = null;
            try {
                connection = ldapConnectionPool.getConnection();
                Iterator<Map<String, Set<String>>> searchResult = connection.searchMultivalues(
                        "(&(objectClass={0})({1}={2}))",
                        new Object[]{ldapUserObjectClass, ldapUserIdentifyingAttribute, loginName},
                        searchControls);
                if (searchResult.hasNext()) attributeMap = searchResult.next();
            } finally {
                if (connection != null) {
                    connection.close();
                }
            }

        } catch (LdapClientException e) {
            LOG.error("LDAP search error. User: " + loginName, e);
        }

        return attributeMap;
    }

    /**
     * Returns user's Active Directory groups sAMAccountName-s.
     * If LDAP service provider is not an AD then the empty set will be returned.
     *
     * @param returningGroups groups sAMAccountName-s to return.
     * @return groups sAMAccountName-s
     * @since 4.1.11
     */
    private Set<String> getADUserGroups(String loginName, Set<String> returningGroups) {
        if (userPropertyNameToLdapAttributeNameMap.getProperty("LoginName").trim().compareToIgnoreCase("sAMAccountName") != 0) {
            LOG.debug("Underlying LDAP service is not an Active Directory. No search will be performed.");
            return Collections.emptySet();
        }

        ImmutableSet.Builder<String> groupsBuilder = ImmutableSet.builder();

        try {

            LdapConnection connection = null;
            Set<String> adUserGroups;
            try {
                connection = ldapConnectionPool.getConnection();
                adUserGroups = connection.getADUserGroups(loginName);
            } finally {
                if (connection != null) {
                    connection.close();
                }
            }

            for (String group : adUserGroups) {
                if (returningGroups.contains(group.toLowerCase())) {
                    groupsBuilder.add(group);
                }

                if (LOG.isDebugEnabled()) {
                    LOG.info(String.format("User '%s' is a member of AD group '%s'.", loginName, group));
                }
            }
        } catch (LdapClientException e) {
            LOG.error("LDAP search error. User: " + loginName, e);
        }

        return groupsBuilder.build();
    }

    public void setUserPropertyLdapAttribute(String userPropertyName, String ldapAttribute) {
        userPropertyNameToLdapAttributeNameMap.put(userPropertyName, ldapAttribute);
    }
}
