package imcode.server.user;

import junit.framework.TestCase;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.FileAppender;
import org.apache.log4j.SimpleLayout;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

public class TestLdapUserAndRoleMapper extends TestCase {

    private Properties ldapProperties = new Properties();
    private String ldapUrl;
    private String ldapBindDN;
    String ldapUsername ;
    String ldapPassword;
    private static final String LDAP_PROPERTIES_SYSTEM_PROPERTY = "test.ldap.properties";
    private static final String DEFAULT_LDAP_PROPERTIES_FILE = "build.properties";
    private LdapUserAndRoleMapper ldapUserAndRoleMapper;

    private void initLog4J() throws IOException {
        String tmpDir = System.getProperty( "java.io.tmpdir" );
        File tmpFile = new File( tmpDir, "log4joutput.log" );
        BasicConfigurator.configure( new FileAppender( new SimpleLayout(), tmpFile.toString() ) );
    }

    LdapUserAndRoleMapper getLdapUserAndRoleMapper( String[] ldapAttributesMappedToRoles ) throws Exception {
        String propertyFileName = System.getProperty( LDAP_PROPERTIES_SYSTEM_PROPERTY, DEFAULT_LDAP_PROPERTIES_FILE );
        ldapProperties.load( new FileInputStream( propertyFileName ) );
        ldapUrl = ldapProperties.getProperty( "ldap-url" );
        ldapPassword = ldapProperties.getProperty( "ldap-password" );
        ldapBindDN = ldapProperties.getProperty( "ldap-bind-dn" );
        ldapUsername = ldapProperties.getProperty( "ldap-test-user" ) ;
        if (StringUtils.isBlank( ldapUsername )) {
            throw new Exception( "Set ldap-test-user in "+propertyFileName) ;
        }

        String ldapUserObjectClass = "person";
        return new LdapUserAndRoleMapper( ldapUrl, LdapUserAndRoleMapper.AUTHENTICATION_TYPE_SIMPLE,
                                          ldapUserObjectClass,
                                          ldapBindDN, ldapPassword, ldapAttributesMappedToRoles );
    }

    public void setUp() throws Exception {
        super.setUp();
        initLog4J();
        this.ldapUserAndRoleMapper = getLdapUserAndRoleMapper( new String[]{} );
    }

    public void testExistingUser() {
        UserDomainObject user = findUser( ldapUsername );
        assertEquals( ldapUsername, user.getLoginName() );
        assertNull( user.getPassword() );
    }

    private UserDomainObject findUser( String username ) {
        UserDomainObject user = ldapUserAndRoleMapper.getUser( username );
        assertNotNull( "Search for user \""+username+"\"", user );
        return user;
    }

    public void testAuthenticate() {
        boolean userAuthenticates = ldapUserAndRoleMapper.authenticate( ldapUsername, ldapPassword );
        assertTrue( userAuthenticates );
    }

    public void testInvalidName() {
        UserDomainObject user = ldapUserAndRoleMapper.getUser( "" );
        assertNull( user );
    }

    public void testNonExistingUser() {
        UserDomainObject user = ldapUserAndRoleMapper.getUser( "non-existing user" );
        assertNull( user );
    }

    public void testGetRolesForUserLdapService() {
        UserDomainObject user = findUser( ldapUsername );
        String[] roleNames = ldapUserAndRoleMapper.getRoleNames( user );
        assertNotNull( roleNames );
        assertTrue( Arrays.asList( roleNames ).contains( LdapUserAndRoleMapper.DEFAULT_LDAP_ROLE ) );
    }

    public void testGetAllRoleNames() {
        String[] roleNames = ldapUserAndRoleMapper.getAllRoleNames();
        assertNotNull( roleNames );
        assertTrue( Arrays.asList( roleNames ).contains( LdapUserAndRoleMapper.DEFAULT_LDAP_ROLE ) );
    }
}
