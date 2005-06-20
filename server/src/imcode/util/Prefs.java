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
