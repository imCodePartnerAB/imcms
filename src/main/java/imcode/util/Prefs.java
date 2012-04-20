package imcode.util ;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Intended to load and cache preferences from properties files.
 */
public class Prefs {

    private final static Map<File, Properties> CACHE = Collections.synchronizedMap(new HashMap<File, Properties>());
    private static File configPath;

    private Prefs() {
    }

    /*
     * This static method must be called before any of the other static methods
     */
    public static void setConfigPath(File confPath) {
        configPath = confPath;
    }

    /** Flushes the cache, causing the files to be loaded again, when they are needed. */
    public static void flush() {
        CACHE.clear();
    }

    /**
     * Get Properties from a config file.
     *
     * @param file The file in the config directory to load from.
     * @return The properties in the file.
     */
    public static Properties getProperties(String file) throws IOException {
        return getProperties(new File(configPath, file));
    }

    /**
     * Get Properties from a config file.
     *
     * @param file The file to load from.
     * @return The properties in the file.
     */

    private static Properties getProperties(File file) throws IOException {
        Properties properties = CACHE.get(file);
        if ( properties == null ) {
            FileInputStream in = null;
            try {
                in = new FileInputStream(file);
                properties = new Properties();
                properties.load(in);
                CACHE.put(file, properties);
            } catch ( IOException ex ) {
                throw new IOException("Prefs: File not found: " + file.getAbsolutePath());
            } finally {
                if (null != in) {
                    in.close();
                }
            }
        }
        
        return properties;
    }
}
