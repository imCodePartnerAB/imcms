package imcode.util ;

import java.io.* ;
import java.util.* ;

/**
	Used to load preferences from file.
	Caches the preferences, so each file is opened only once.
	Config location specified in systemproperty "com.imcode.netserver.config".
*/
public class Prefs {
	protected final static Hashtable hash = new Hashtable () ;
	protected final static String conf_path = System.getProperty("com.imcode.netserver.config") ;

	/**
		Flushes the cache, causing the files to be loaded again, when they are needed.
	*/
	public static void flush() {
	    hash.clear() ;
	}
	
	/**
		Get a property from a config file. Reloads the file once and tries again if the property is not found the first time.
		
		@param key The preference to get.
		@param file The file in the config directory to load from.		
		@return The value of the preference.
	*/	
	public static String get (String key, String file) throws IOException {
		return get (key,new File(conf_path,file)) ;
	}

	/**
		Get a property from a config file. Reloads the file once and tries again if the property is not found the first time.
		
		@param key The preference to get.
		@param path The path to the directory to load from.
		@param file The file in the directory to load from.		
		@return The value of the preference.
	*/
	public static String get (String key, String path, String file) throws IOException {
		return get (key,new File(path,file)) ;
	}

	/**
		Get a property from a config file. Reloads the file once and tries again if the property is not found the first time.
		
		@param key The property to get.
		@param file The file to load from.		
		@return The value of the preference, or null if none is found after the second try.
	*/
	public static String get (String key, File file) throws IOException {
		String temp = getProperties(file).getProperty(key) ;
		if (temp == null ) {
			hash.remove(file) ;
		}
		return getProperties(file).getProperty(key) ;
	}

	/**
		Get Properties from a config file.
		
		@param file The file in the config directory to load from.		
		@return The properties in the file.
	*/
	public static Properties getProperties (String file) throws IOException {
		return getProperties (new File(conf_path,file)) ;
	}

	/**
		Get Properties from a config file.
		
		@param path The path to the directory to load from.
		@param file The file in the directory to load from.		
		@return The properties in the file.
	*/
	public static Properties getProperties (String path, String file) throws IOException {
		return getProperties (new File(path,file)) ;
	}
	/**
		Get Properties from a config file.
		
		@param file The file to load from.		
		@return The properties in the file.
	*/

	public static Properties getProperties (File file) throws IOException  {
		Properties prop ;
		prop = (Properties)hash.get(file) ;
		if (prop == null) {
			FileInputStream in ;
			try {
				in = new FileInputStream(file) ;
			} catch ( IOException ex ) {
				throw new IOException("Prefs: File not found: "+file.getAbsolutePath()) ;
			}
			prop = new Properties() ;
			prop.load(in) ;
			hash.put(file,prop) ;
		}
		return prop ;
	}
}
