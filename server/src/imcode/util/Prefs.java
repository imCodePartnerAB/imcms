package imcode.util ;

import java.io.* ;
import java.util.* ;

/**
   Used to load preferences from file.
   Caches the preferences, so each file is opened only once.
   Config location specified in systemproperty "com.imcode.netserver.config".
*/
public class Prefs {

    private final static Hashtable hash = new Hashtable () ;
    private static File configPath = null ;

    /*
     * This static method must be called before any of the other static methods
     */
    public static void setConfigPath( File confPath ) {
	configPath = confPath ;
    }

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
	return get (key,new File(configPath,file)) ;
    }

    /**
       Get a property from a config file. Reloads the file once and tries again if the property is not found the first time.

       @param key The property to get.
       @param file The file to load from.
       @return The value of the preference, or null if none is found after the second try.
    */
    private static String get (String key, File file) throws IOException {
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
	return getProperties (new File(configPath,file)) ;
    }

    /**
       Get Properties from a config file.

       @param file The file to load from.
       @return The properties in the file.
    */

    private static Properties getProperties (File file) throws IOException  {
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
