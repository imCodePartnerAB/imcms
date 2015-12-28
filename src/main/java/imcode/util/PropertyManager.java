package imcode.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Intended to load and cache properties from properties files.
 */
public class PropertyManager {

	/**
	 * Default properties config file name.
	 */
	private static final String SERVER_PROPERTIES_FILENAME = "server.properties";

	/**
	 * Default properties config path relative to deployment path.
	 */
	private static final String DEFAULT_PROPERTIES_CONFIG_PATH = "WEB-INF/conf";

	private final static Map<File, Properties> CACHE = Collections.synchronizedMap(new HashMap<>());

	private static volatile String serverPropertiesFileName = SERVER_PROPERTIES_FILENAME;

	/**
	 * The configuration path, composed by webApp root and default properties config path.
	 *
	 * @see PropertyManager#DEFAULT_PROPERTIES_CONFIG_PATH
	 * @see javax.servlet.ServletContext#getRealPath(String)
	 */
	private static File configPath;

	/**
	 * The server properties file path, composed by configuration path and server properties file name.
	 *
	 * @see PropertyManager#configPath
	 * @see PropertyManager#SERVER_PROPERTIES_FILENAME
	 */
	private static File serverPropertiesPath;

	private PropertyManager() {
	}

	/**
	 * This static method must be called before any of the other static methods
	 *
	 * @param rootPath the root path of web application
	 */
	public static void setConfigPathsByRoot(String rootPath) {
		setConfigPath(new File(rootPath, DEFAULT_PROPERTIES_CONFIG_PATH));
		setServerPropertiesPath(new File(configPath, SERVER_PROPERTIES_FILENAME));
	}

	public static void setConfigPath(File confPath) {
		configPath = confPath;
	}

	/**
	 * It is equals {@code Integer.valueOf(getPropertyFrom(path, property))}
	 *
	 * @param path     Path to properties file.
	 * @param property The property which needs to be read.
	 * @return {@code Integer} property
	 * @throws IOException Throws {@code IOException} if file not found.
	 */
	public static Integer getIntegerPropertyFrom(String path, String property) throws NumberFormatException, IOException {
		return Integer.valueOf(getPropertyFrom(path, property));
	}

	/**
	 * Gets the value of asked property.
	 *
	 * @param path     Path to properties file.
	 * @param property The property which needs to be read.
	 * @return {@code String} type of asked property
	 * @throws IOException Throws {@code IOException} if file not found.
	 */
	public static String getPropertyFrom(String path, String property) throws IOException {
		return getProperties(new File(path)).getProperty(property);
	}

	/**
	 * Gets the {@code Property} representation of file or path to file due to configuration path.
	 *
	 * @param path Path to properties file.
	 * @return {@code String} type of asked property
	 * @throws IOException Throws {@code IOException} if file not found.
	 */
	public static Properties getPropertiesFrom(String path) throws IOException {
		return getProperties(new File(configPath, path));
	}

	/**
	 * Gets the value of property from server properties file.
	 *
	 * @param property The property which needs to be read from .
	 * @return {@code String} value of asked property.
	 * @throws IOException Throws {@code IOException} if file not found.
	 */
	public static String getServerConfProperty(String property) throws IOException {
		return getServerConfProperties().getProperty(property);
	}

	/**
	 * Flushes the cache, causing the files to be loaded again, when they are needed.
	 */
	public static void flush() {
		CACHE.clear();
	}

	/**
	 * Get {@code Properties} from a config file.
	 *
	 * @return The properties in the file.
	 * @throws IOException Throws {@code IOException} if file not found.
	 */
	public static Properties getServerConfProperties() throws IOException {
		return getProperties(serverPropertiesPath);
	}

	/**
	 * Get {@code Properties} from a file and puts it in cache if it is not yet there.
	 *
	 * @param file The file to load from.
	 * @return The properties in the file.
	 * @throws IOException Throws {@code IOException} if file not found.
	 */
	private static Properties getProperties(File file) throws IOException {
		Properties properties = CACHE.get(file);
		if (properties == null) {
			try (FileInputStream in = new FileInputStream(file)) {
				properties = new Properties();
				properties.load(in);
				CACHE.put(file, properties);
			} catch (IOException ex) {
				throw new IOException("PropertyManager: File not found: " + file.getAbsolutePath());
			}
		}
		return properties;
	}

	@SuppressWarnings("unused")
	public static File getServerPropertiesPath() {
		return serverPropertiesPath;
	}

	public static void setServerPropertiesPath(File serverPropertiesPath) {
		PropertyManager.serverPropertiesPath = serverPropertiesPath;
	}

	@SuppressWarnings("unused")
	public static String getServerPropertiesFileName() {
		return serverPropertiesFileName;
	}

	public static void setServerPropertiesFileName(String serverPropertiesFilename) {
		PropertyManager.serverPropertiesFileName = serverPropertiesFilename;
	}
}
