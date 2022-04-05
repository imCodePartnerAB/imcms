package com.imcode.imcms.test.external;

import imcode.server.user.LdapUserAndRoleRegistry;
import imcode.server.user.UserDomainObject;
import junit.framework.TestCase;
import org.apache.commons.lang.StringUtils;

import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Properties;

public class TestLdapUserAndRoleRegistry extends TestCase {

    private Properties ldapProperties = new Properties();
    String testUserName;
    String testPassword;
    private static final String LDAP_PROPERTIES_SYSTEM_PROPERTY = "test.ldap.properties";
    private static final String DEFAULT_LDAP_PROPERTIES_FILE = "build.properties";
    private LdapUserAndRoleRegistry ldapUserAndRoleRegistry;

    LdapUserAndRoleRegistry getLdapUserAndRoleRegistry( String[] ldapAttributesMappedToRoles ) throws Exception {
        String propertyFileName = System.getProperty( LDAP_PROPERTIES_SYSTEM_PROPERTY, DEFAULT_LDAP_PROPERTIES_FILE );
        ldapProperties.load( new FileInputStream( propertyFileName ) );
        String ldapUrl = ldapProperties.getProperty( "ldap-url" );
        String ldapPassword = ldapProperties.getProperty( "ldap-password" );
        String ldapBindDN = ldapProperties.getProperty( "ldap-bind-dn" );
        String ldapReadTimeoutMillis = ldapProperties.getProperty("ldap-read-timeout-millis");
        String ldapMaxConnections = ldapProperties.getProperty("ldap-max-connections");
        String ldapConnectionExpirySeconds = ldapProperties.getProperty( "ldap-connection-expiry-seconds" );
        String ldapUserObjectClass = ldapProperties.getProperty( "ldap-user-object-class") ;
        String ldapAttributeLoginName = ldapProperties.getProperty( "ldap-attribute-login-name" );

        testUserName = ldapProperties.getProperty( "ldap-test-user" );
        testPassword = ldapProperties.getProperty( "ldap-test-password" );
        if ( StringUtils.isBlank( testUserName ) ) {
            throw new Exception( "Set ldap-test-user in " + propertyFileName );
        }

        LdapUserAndRoleRegistry ldapUserAndRoleRegistry = new LdapUserAndRoleRegistry(ldapUrl,
                ldapUserObjectClass,
                ldapBindDN, ldapPassword,
                ldapReadTimeoutMillis, ldapMaxConnections, ldapConnectionExpirySeconds,
                ldapAttributesMappedToRoles, new Properties());
        ldapUserAndRoleRegistry.setUserPropertyLdapAttribute("LoginName", ldapAttributeLoginName) ;
        return ldapUserAndRoleRegistry;
    }

    public void setUp() throws Exception {
        super.setUp();
        this.ldapUserAndRoleRegistry = getLdapUserAndRoleRegistry( new String[]{} );
    }

    public void testExistingUser() {
        UserDomainObject user = findUser( testUserName );
        assertEquals( testUserName, user.getLoginName() );
        assertNull( user.getPassword() );
    }

    private UserDomainObject findUser( String username ) {
        UserDomainObject user = ldapUserAndRoleRegistry.getUser( username );
        assertNotNull( "Search for user \"" + username + "\"", user );
        return user;
    }

    public void testAuthenticate() {
        boolean userAuthenticates = ldapUserAndRoleRegistry.authenticate( testUserName, testPassword );
        assertTrue( userAuthenticates );
    }

    public void testInvalidName() {
        UserDomainObject user = ldapUserAndRoleRegistry.getUser( "" );
        assertNull( user );
    }

    public void testNonExistingUser() {
        UserDomainObject user = ldapUserAndRoleRegistry.getUser( "non-existing user" );
        assertNull( user );
    }

    public void testGetRolesForUserLdapService() {
        UserDomainObject user = findUser( testUserName );
        String[] roleNames = ldapUserAndRoleRegistry.getRoleNames( user );
        assertNotNull( roleNames );
        assertTrue( Arrays.asList( roleNames ).contains( LdapUserAndRoleRegistry.DEFAULT_LDAP_ROLE ) );
    }

    public void testGetAllRoleNames() {
        String[] roleNames = ldapUserAndRoleRegistry.getAllRoleNames();
        assertNotNull( roleNames );
        assertTrue( Arrays.asList( roleNames ).contains( LdapUserAndRoleRegistry.DEFAULT_LDAP_ROLE ) );
    }
}
