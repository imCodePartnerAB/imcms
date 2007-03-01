package com.imcode.net.ldap;

import javax.naming.NamingException;

public class LdapAuthenticationException extends LdapClientException {

    public LdapAuthenticationException(String message, NamingException cause) {
        super(message, cause);
    }
}
