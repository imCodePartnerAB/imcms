package imcode.util;

import imcode.server.Imcms;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Intended to load and cache properties from *.properties files. Be sure that
 * {@link PropertyManager#setRoot(String)} are called before server's configurations was read.
 */
public class PropertyManager {

    /**
     * Default server properties file path.
     */
    private static final String SERVER_PROPERTIES_FILE = "WEB-INF/conf/server.properties";

    private static final String DEFAULT_PROPERTY_VALUE = "";

    private static final String ERR_MESSAGE = "ImCMS not initialized yet, root path not set. Wait for ImCMS do it "
            + "automatically or set it manually. Use this to read properties ONLY when setRoot() was called before.";

    private static final String NULL_MESS = "Properties can not be loaded, smth wrong with file or path.";

    private static final Map<File, Properties> CACHE = Collections.synchronizedMap(new HashMap<>());

    /**
     * Root of Imcms. Needs to be initialised.
     */
    @NotNull
    private static File root;

    @NotNull
    private static File serverPropertiesFile;

    private PropertyManager() {
    }

    public static File getRoot() {
        return root;
    }

    /**
     * This method must be called before any other methods of this manager
     *
     * @param newRoot the root path of web application
     */
    @NotNull
    public static void setRoot(String newRoot) {
        if (StringUtils.isEmpty(newRoot)) {
            throw new NullPointerException();
        } else {
            setRoot(new File(newRoot));
        }
    }

    /**
     * This method must be called before any other methods of this manager
     *
     * @param newRoot the root path of web application
     */
    @NotNull
    public static void setRoot(File newRoot) {
        if (null == newRoot) {
            throw new NullPointerException(ERR_MESSAGE);
        } else {
            root = newRoot;
            serverPropertiesFile = new File(root, SERVER_PROPERTIES_FILE);
        }
    }

    /**
     * Gets the value of asked property.
     *
     * @param path     Path to properties file.
     * @param property The property which needs to be read.
     * @return {@code String} type of asked property
     */
    public static String getPropertyFrom(String path, String property) {
        return getPropertiesFrom(path).getProperty(property);
    }

    /**
     * Get {@code Properties} by specified path.
     *
     * @param path Path to properties file.
     * @return Asked properties.
     */
    public static Properties getPropertiesFrom(String path) {
        checkPaths();
        return getProperties(new File(root, path));
    }

    /**
     * Gets the value of property from server properties file or returns empty
     * string if there is no such property
     *
     * @param property The property which needs to be read from server properties file
     * @return {@code String} value of asked property or empty string
     */
    public static String getServerProperty(String property) {
        return getServerProperty(property, DEFAULT_PROPERTY_VALUE);
    }

    /**
     * Gets the value of property from server properties file or returns
     * default value if there is no such server property.
     *
     * @param property     The property which needs to be read from server properties file
     * @param defaultValue default value that will be returned if there is no such server property
     * @return {@code String} value of asked property or default value
     */
    public static String getServerProperty(String property, String defaultValue) {
        return getServerProperties().getProperty(property, defaultValue);
    }

    /**
     * Get {@code Properties} from a config file.
     *
     * @return The properties in the file.
     */
    public static Properties getServerProperties() {
        checkPaths();
        return getProperties(serverPropertiesFile);
    }

    /**
     * Try get root path from ImCMS. Will throw {@link NullPointerException} if path is {@code null}.
     */
    private static void checkPaths() {
        if (null == root || null == serverPropertiesFile) {
            setRoot(Imcms.getPath());
        }
    }

    /**
     * Flushes the cache, causing the files to be loaded again, when they are needed.
     */
    public static void flush() {
        CACHE.clear();
    }

    /**
     * Get {@code Properties} from a file and puts it in cache if it is not yet there.
     *
     * @param file The file to load from.
     * @return The properties in the file.
     */
    private static Properties getProperties(File file) {
        Properties properties = CACHE.get(file);
        if (properties == null) {
            try (FileInputStream in = new FileInputStream(file)) {
                properties = new Properties();
                properties.load(in);
                CACHE.put(file, properties);
            } catch (IOException ex) {
                throw new NullPointerException(NULL_MESS);
            }
        }
        return properties;
    }
}
