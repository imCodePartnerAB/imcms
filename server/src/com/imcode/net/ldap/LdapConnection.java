package com.imcode.net.ldap;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;

import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.directory.Attribute;
import javax.naming.directory.InitialDirContext;
import javax.naming.*;
import java.util.*;

public class LdapConnection {

    private final static Logger LOG = Logger.getLogger(LdapConnection.class);

    private static final String AUTHENTICATION_TYPE_SIMPLE = "simple";

    private final DirContext ctx;
    public static final String DISTINGUISHED_NAME = "dn";

    public LdapConnection(String ldapUrl, String ldapBindDn, String ldapPassword) throws LdapClientException {
        try {
            ctx = new InitialDirContext(createLdapJndiEnvironment(ldapUrl, ldapBindDn, ldapPassword));
        } catch ( AuthenticationException ex ) {
            throw new LdapAuthenticationException( "Authentication failed, using login: '" + ldapBindDn + "'", ex );
        } catch ( NameNotFoundException ex ) {
            throw new LdapClientException( "Root not found: " + ldapUrl, ex );
        } catch ( NamingException ex ) {
            throw wrapNamingException(ldapUrl, ex);
        }
    }

    private static Hashtable createLdapJndiEnvironment(String ldapUrl, String ldapBindDn,
                                                       String ldapPassword
    ) {
        Hashtable env = new Hashtable();
        env.put( Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory" );
        env.put( Context.PROVIDER_URL, ldapUrl );
        env.put( Context.SECURITY_AUTHENTICATION, AUTHENTICATION_TYPE_SIMPLE );
        env.put( Context.SECURITY_PRINCIPAL, ldapBindDn );
        env.put( Context.SECURITY_CREDENTIALS, ldapPassword );
        return env;
    }


    private Map<String,String> trySearch(String searchFilterExpr, Object[] parameters, SearchControls searchControls) throws NamingException {
        NamingEnumeration enumeration = ctx.search( "", searchFilterExpr, parameters, searchControls );

        if ( enumeration != null && enumeration.hasMore() ) {
            SearchResult searchResult = (SearchResult)enumeration.nextElement();
            return createMapFromSearchResult( searchResult, searchControls.getReturningAttributes() );
        }
        return null;
    }

    private Map<String,String> createMapFromSearchResult( SearchResult searchResult, String[] attributesToReturn ) {

        NamingEnumeration attribEnum = searchResult.getAttributes().getAll();
        Map<String, String> attributes = new HashMap<String,String>();
        try {
            if ( !attribEnum.hasMore() ) {
                boolean includeDistinguishedName = null == attributesToReturn
                                                   || ArrayUtils.contains(attributesToReturn, DISTINGUISHED_NAME );
                if ( includeDistinguishedName  ) {
                    DirContext dirContext = (DirContext)searchResult.getObject();
                    String distinguishedName = dirContext.getNameInNamespace();
                    attributes.put( DISTINGUISHED_NAME, distinguishedName );
                }
            }
        } catch ( NamingException e ) {
            LOG.error( e );
        }

        while ( attribEnum.hasMoreElements() ) {
            Attribute attribute = (Attribute)attribEnum.nextElement();
            String attributeName = attribute.getID();
            String attributeValue = null;
            try {
                attributeValue = attribute.get().toString();
            } catch ( NamingException e ) {
                LOG.error( e );
            }
            attributes.put( attributeName, attributeValue );
        }
        return attributes;
    }

    public Map<String,String> search(String searchFilterExpr, Object[] parameters,
                                     SearchControls searchControls) throws LdapClientException {
        try {
            try {
                return trySearch(searchFilterExpr, parameters, searchControls);
            } catch( CommunicationException ce) {
                LOG.warn( "Problem communicating with LDAP server, retrying.", ce );
                return trySearch(searchFilterExpr, parameters, searchControls);
            }
        } catch (NamingException ne) {
            throw new LdapClientException("LDAP search failed.",ne);
        }
    }

    public void close() {
        try {
            ctx.close();
        } catch ( NamingException ne ) {
            LOG.debug("Closing context failed.", ne);
        }
    }

    protected void finalize() throws Throwable {
        super.finalize();
        close();
    }

    private static LdapClientException wrapNamingException(String ldapUrl, NamingException ex) {
        return new LdapClientException( "Failed to create LDAP context " + ldapUrl + ": " + ex.getExplanation(), ex );
    }
}
