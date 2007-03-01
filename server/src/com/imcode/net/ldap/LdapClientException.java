package com.imcode.net.ldap;

import javax.naming.NamingException;

public class LdapClientException extends Exception {

    public LdapClientException( String message ) {
        super( message );
    }

    public LdapClientException( String message, NamingException cause ) {
        super( message, cause );
    }

}
    