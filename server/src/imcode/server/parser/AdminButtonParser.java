package imcode.server.parser;

import imcode.server.ApplicationServer;
import imcode.server.user.UserDomainObject;

import java.util.*;

import org.apache.log4j.Logger;

public class AdminButtonParser extends AbstractMap {

    private final static Logger log = Logger.getLogger( AdminButtonParser.class.getName() );

    private int set_id;
    private int set;
    private String prefix;
    private String suffix;
    private UserDomainObject user;

    private Map cache = new HashMap() ;

    public AdminButtonParser( UserDomainObject user, String prefix, String suffix,
                              int perm_set_id, int perm_set ) {
        this.user = user ;
        this.prefix = prefix ;
        this.suffix = suffix ;
        set_id = perm_set_id;
        set = perm_set;
    }

    public Set entrySet() {
        return null ;
    }

    public Object put( Object key, Object value ) {
        return cache.put(key, value) ;
    }

    /**
     * Returns the file for this key, if the user is privileged enough.
     */
    public Object get( Object key ) {
        String cacheHit = (String)cache.get(key) ;
        if (null != cacheHit) {
            return cacheHit ;
        }
        try {
            int key_int = Integer.parseInt( (String)key );
            // If user's set-id is 0, or the value of this key is 0 (normal), or user has the bit for this permission set.
            boolean isFullAdmin = set_id == 0;
            boolean isAdminButtonNormalView = key_int == 0;
            boolean hasPermissionBitForThisButtonSet = ( ( key_int & set ) != 0 );
            if ( isFullAdmin || isAdminButtonNormalView || hasPermissionBitForThisButtonSet ) {
                // Then return it! (w00p!)
                return getAdminTemplate( (String)key );
            } else {
                // The user doesn't have permission to get this tag parsed.
                return null;
            }
        } catch ( NumberFormatException ex ) {
            return getAdminTemplate( (String)key );
        }
    }

    private String getAdminTemplate( String key ) {
        String realKey = prefix+key+suffix;
        log.debug("getAdminTemplate(\""+realKey+"\")") ;
        return ApplicationServer.getIMCServiceInterface().getAdminTemplate( realKey, user, Arrays.asList( new Object[] { "user", user } ) ) ;
    }

    public void setPrefix( String prefix ) {
        this.prefix = prefix;
    }
}
