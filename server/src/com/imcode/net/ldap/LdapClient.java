package com.imcode.net.ldap;

public class LdapClient {

    private String ldapUrl;

    public LdapClient( String ldapUrl) {
        this.ldapUrl = ldapUrl;
    }

    public boolean canBind(String userDistinguishedName, String password) throws LdapClientException {
        try {
            bind(userDistinguishedName, password).close();
            return true;
        } catch ( LdapAuthenticationException ex ) {
            return false ;
        }
    }

    public LdapConnection bind(String ldapBindDn, String ldapPassword) throws LdapClientException {
        return new LdapConnection(ldapUrl, ldapBindDn, ldapPassword);
    }

    public String getLdapUrl() {
        return ldapUrl;
    }

}
