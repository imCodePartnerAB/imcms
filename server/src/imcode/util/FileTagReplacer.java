package imcode.util ;

import java.util.Hashtable ;
import java.io.* ;

/**
	A nice little class that is useful for Parser.parseTags().
	When it gets a request for a key, 
	it will prepend the prefix and append the suffix, 
	and search for a file with that name.
	That file is then loaded, cached, and returned.
	The next time that file is requested, 
	it will return the file in the cache.
*/
public class FileTagReplacer extends Hashtable {
	private final static String CVS_REV="$Revision$" ;
	private final static String CVS_DATE = "$Date$" ;
	
	/**
		The file-prefix
	*/
	protected String prefix ;
	
	/**
		The file-suffix
	*/
	protected String suffix ;

	/**
		Creates a FileTagReplacer.
		@param prefix The file-prefix.
		@param suffix The file-suffix.
	*/
	public FileTagReplacer(String prefix, String suffix) {
		super() ;
		this.prefix = prefix ;
		this.suffix = suffix ;
	}

	public void setPrefix (String prefix) {
		this.prefix = prefix ;
	}

	public void setSuffix (String prefix) {
		this.suffix = suffix ;
	}

	public String getPrefix () {
		return prefix ;
	}

	public String getSuffix () {
		return suffix ;
	}

	/**
		Creates a FileTagReplacer with empty -fixes.
	*/
	public FileTagReplacer() {
		super() ;
		prefix = "" ;
		suffix = "" ;
	}
	
	/**
		Returns the file given by prefix+key+suffix, which may have been cached.
		@param key The part of the filename that is subject to change.
	*/
	public Object get(Object key) {
		String name = (String)key ;
		String replacement = (String)super.get(name) ;
		if ( replacement == null ) {
			name = prefix+name+suffix ;
			replacement = (String)super.get(name) ;
		}
		if (replacement == null) {
			StringBuffer buffer ;
			try {
				buffer = getContent(name) ;
			} catch ( IOException ex ) {
				return null ;
			}
			if (buffer == null) {
			    return null ;
			}
			replacement = buffer.toString() ;
			super.put(name, replacement) ;
		}
		return replacement ;
	}
	
	/**
		Returns the real content that is supposed to be returned.
		If you want the content to come from something other than a file,
		you should overload this.
		@param name The (file)name/address of the content, that is, the information needed to find the content.
	**/
	protected StringBuffer getContent(String name) throws IOException {
		File file = new File(name) ;
		StringBuffer buffer = new StringBuffer((int)file.length()) ;
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file),"8859_1")) ;
		char[] cbuf = new char[4096] ;
		int read ;
		while (-1 != (read = in.read(cbuf,0,4096))) {
			buffer.append(cbuf,0,read) ;
		}
		return buffer ;
	}
}
