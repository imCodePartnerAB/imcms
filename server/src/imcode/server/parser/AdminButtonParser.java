package imcode.server.parser ;

/**
	This class extends imcode.util.FileTagReplacer to provide support for the imCMS-server's
	permissions (v.1.3). Used for parsing the adminbuttons.
*/
public class AdminButtonParser extends imcode.util.FileTagReplacer {

    protected int set_id ;
	protected int set ;

	/**
		Creates an AdminButtonParser.
		@param perm_set_id The user's permission-set-id
		@param perm_set A bitvector containing the user's permission-set. (A bunch of on-off permissions.)
	*/
	public AdminButtonParser ( String prefix, String suffix, int perm_set_id, int perm_set ) {
		super(prefix,suffix) ;
		set_id = perm_set_id ;
		set = perm_set ;
	}

	/**
		Returns the file for this key, if the user is privileged enough.
	*/
	public Object get(Object key) {
		try {
			int key_int = Integer.parseInt((String)key) ;
			// If user's set-id is 0, or the value of this key is 0 (normal), or user has the bit for this permission set.
            boolean isFullAdmin = set_id == 0;
            boolean isAdminButtonNormalView = key_int == 0;
            boolean hasPermissionBitForThisButtonSet = ((key_int & set) != 0);
            if ( isFullAdmin || isAdminButtonNormalView || hasPermissionBitForThisButtonSet ) {
				// Then return it! (w00p!)
				return super.get(key) ;
			} else {
				// The user doesn't have permission to get this tag parsed.
				return null ;
			}
		} catch ( NumberFormatException ex ) {
			return super.get(key) ;
		}
	}
}
