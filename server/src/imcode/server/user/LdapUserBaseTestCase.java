package imcode.server.user;

import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.io.FileInputStream;
import java.util.Properties;

/**
 * @author kreiger
 */
public class LdapUserBaseTestCase extends UserBaseTestCase {

    private Properties ldapProperties = new Properties();
    private String ldapUrl ;
    private String ldapBindDN ;
    String ldapUsername ;
    String ldapPassword;
    private static final String LDAP_PROPERTIES_SYSTEM_PROPERTY = "test.ldap.properties";
    private static final String DEFAULT_LDAP_PROPERTIES_FILE = "build.properties";

    LdapUserAndRoleMapper getLdapUserAndRoleMapper( String[] ldapAttributesMappedToRoles ) throws IOException {
        ldapProperties.load( new FileInputStream( System.getProperty( LDAP_PROPERTIES_SYSTEM_PROPERTY, DEFAULT_LDAP_PROPERTIES_FILE ) ) );
        ldapUrl = ldapProperties.getProperty( "ldap-url");
        ldapPassword = ldapProperties.getProperty( "ldap-pass" ) ;
        ldapBindDN = ldapProperties.getProperty( "ldap-user" ) ;
        // For a Microsoft ldap server, the bind dn can be of the form chrham@imcode.com or the form imcode\chrham
        ldapUsername = StringUtils.substringBefore( ldapBindDN, "@") ;
        ldapUsername = ldapUsername.substring( ldapUsername.indexOf('\\')+1 ) ; //

        try {
            String ldapUserObjectClass = "person";
            String ldapUserIdentifyingAttribute = "samaccountname";
            return new LdapUserAndRoleMapper( ldapUrl, LdapUserAndRoleMapper.AUTHENTICATION_TYPE_SIMPLE,
                                                        ldapUserObjectClass,
                                              ldapBindDN, ldapPassword, ldapAttributesMappedToRoles );
        } catch ( LdapUserAndRoleMapper.LdapInitException e ) {
            System.err.println( e );
            fail();
        }
        return null ;
    }

}
