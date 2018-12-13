package imcode.server;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import java.util.Hashtable;

public class IsMemberOfTest {
    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            System.out.println("Usage: IsMemberOf <user-id> <group-dn> <recursive: true|false>");
            System.exit(0);
        }

        Hashtable<String, String> env = new Hashtable<>();

        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, "ldap://imadsrv.d01.imcode.com:389/CN=Users,DC=d01,DC=imcode,DC=com");
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, "CN=administrator,CN=Users,DC=d01,DC=imcode,DC=com");
        env.put(Context.SECURITY_CREDENTIALS, "nonac-ad01");
        env.put(Context.REFERRAL, "follow");

        InitialDirContext ctx = new InitialDirContext(env);

        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        searchControls.setReturningAttributes(new String[]{"sAMAccountName"});

        String filter = Boolean.valueOf(args[2])
                ? String.format("(&(objectClass=user)(sAMAccountName=%s)(memberOf:1.2.840.113556.1.4.1941:=%s))", args)
                : String.format("(&(objectClass=user)(sAMAccountName=%s)(memberOf=%s))", args);

        System.out.println(">> Searching using filter: " + filter);

        NamingEnumeration<SearchResult> results = ctx.search(
                "",
                filter,
                searchControls);

        System.out.println(">> " + (results.hasMore() ? "YES" : "NO"));
    }
}