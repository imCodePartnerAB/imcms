package imcode.server;

import com.google.common.collect.Maps;
import com.imcode.db.DataSourceDatabase;
import com.imcode.db.Database;
import com.imcode.imcms.api.ContentManagementSystem;
import com.imcode.imcms.api.DocumentLanguage;
import com.imcode.imcms.api.DocumentLanguageException;
import com.imcode.imcms.api.DocumentLanguages;
import com.imcode.imcms.db.DB;
import com.imcode.imcms.db.DefaultProcedureExecutor;
import com.imcode.imcms.db.Schema;
import com.imcode.imcms.mapping.DocumentLanguageMapper;
import com.imcode.imcms.util.l10n.CachingLocalizedMessageProvider;
import com.imcode.imcms.util.l10n.ImcmsPrefsLocalizedMessageProvider;
import com.imcode.imcms.util.l10n.LocalizedMessageProvider;
import imcode.server.user.UserDomainObject;
import imcode.util.CachingFileLoader;
import imcode.util.PropertyManager;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

import javax.servlet.ServletRequest;
import javax.sql.DataSource;
import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

/**
 * Singleton registry.
 */
public class Imcms {

	public static final String ASCII_ENCODING = "US-ASCII";
	public static final String ISO_8859_1_ENCODING = "ISO-8859-1";
	public static final String UTF_8_ENCODING = "UTF-8";
	public static final String DEFAULT_ENCODING = UTF_8_ENCODING;

	public static final String ERROR_LOGGER_URL = "https://errors.imcode.com/ErrorLogger";

	/**
	 * Default SQL scripts directory path relative to deployment path
	 */
	private static final String DEFAULT_SQL_SCRIPTS_PATH = "WEB-INF/sql";

	/**
	 * Default Embedded SOLr home directory relative to deployment path
	 */
	private static final String DEFAULT_SQLR_HOME = "WEB-INF/solr";

	private static final Logger logger = Logger.getLogger(Imcms.class);

	/**
	 * imCMS deployment (real context) path.
	 */
	private static volatile File path;

	/**
	 * Core services.
	 */
	private static volatile ImcmsServices services;

	/**
	 * Spring-framework application context.
	 */
	private static volatile ApplicationContext applicationContext;

	private static volatile String sqlScriptsPath = DEFAULT_SQL_SCRIPTS_PATH;

	/**
	 * Used to disable db init/upgrade on start.
	 */
	private static volatile boolean prepareDatabaseOnStart = true;

	/**
	 * Users associated with servlet requests.
	 *
	 * @see com.imcode.imcms.servlet.ImcmsSetupFilter
	 */
	private static InheritableThreadLocal<UserDomainObject> users = new InheritableThreadLocal<>();
	@SuppressWarnings("unused")
	private static volatile String solrHome = DEFAULT_SQLR_HOME;

    private static final String DOCUMENT_VERSIONING_PROPERTY = "document.versioning";

    /**
     * Flag variable that shows is document versioning feature are turned on in server properties
     */
    private static boolean isVersioningAllowed;

    private Imcms() {
	}

	/**
	 * @return ImcmsServices
	 */
	public static ImcmsServices getServices() {
		return services;
	}

	/**
	 * Initializes services.
	 * <p>
	 * Path and ApplicationContext must be set.
	 *
	 * @throws StartupException
	 */
	public static synchronized void start() throws StartupException {
		try {
			if (path == null) {
				throw new IllegalStateException("Imcms path is not set.");
			}

			if (applicationContext == null) {
				throw new IllegalStateException("Spring application context is not set.");
			}

			users = new InheritableThreadLocal<>();

			if (prepareDatabaseOnStart) {
				prepareDatabase();
			}

			services = createServices();
			if (services.getDocumentMapper().getDocumentIndex().getService().rebuildIfEmpty().isDefined()) {
				logger.info("Document index is empty, initiated index rebuild.");
			}
		} catch (Exception e) {
			String msg = "Application could not be started. Please see the log file in WEB-INF/logs/ for details.";
			logger.error(msg, e);
			throw new StartupException(msg, e);
		}
	}

	public static void setRootPath(String path) {
		PropertyManager.setRoot(path);
		setPath(new File(path));
	}

	public static File getPath() {
		return path;
	}

	public static void setPath(File path) {
		Imcms.path = path;
	}

	private static ImcmsServices createServices() throws Exception {
		Properties serverProperties = getServerProperties();

        final String versioningProperty = serverProperties.getProperty(DOCUMENT_VERSIONING_PROPERTY, "true");
        isVersioningAllowed = Boolean.parseBoolean(versioningProperty);

		logger.debug("Creating main DataSource.");
		Database database = new DataSourceDatabase(getApiDataSource());
		LocalizedMessageProvider localizedMessageProvider = new CachingLocalizedMessageProvider(new ImcmsPrefsLocalizedMessageProvider());

		final CachingFileLoader fileLoader = new CachingFileLoader();
		DefaultImcmsServices services = new DefaultImcmsServices(
				database,
				serverProperties,
				localizedMessageProvider,
				fileLoader,
				new DefaultProcedureExecutor(database, fileLoader),
				applicationContext,
				createDocumentLanguages());

		services.getImcmsAuthenticatorAndUserAndRoleMapper().encryptUnencryptedUsersLoginPasswords();
		return services;
	}

	public static DataSource getApiDataSource() {
		return applicationContext.getBean("dataSourceWithAutoCommit", DataSource.class);
	}

	public static Properties getServerProperties() {
		Properties properties = PropertyManager.getServerProperties();
		properties.setProperty("SolrHome", getSolrHome());
		return properties;
	}

	public static synchronized void restartCms() {
		stop();
		start();
	}

	public static synchronized void stop() {
		PropertyManager.flush();

		if (services != null) {
			services.getDocumentMapper().getDocumentIndex().getService().shutdown();
		}

		services = null;
	}

	/**
	 * Removes a user from a current request thread.
	 * Must not be called from a client code.
	 */
	public static void removeUser() {
		users.remove();
	}

	/**
	 * @return a user associated with a current request thread.
	 */
	public static UserDomainObject getUser() {
		return users.get();
	}

	/**
	 * Associates a user with a current request thread.
	 * Must not be called from a client code.
	 */
	public static void setUser(UserDomainObject user) {
		users.set(user);
	}

	/**
	 * Creates and initializes languages.
	 * <p>
	 * Reads languages from the database.
	 * Adds a new language if there are no languages in the database.
	 * Sets default language if it is not already set.
	 * todo: use language property defined in the conf file as default.
	 */
	private static DocumentLanguages createDocumentLanguages() {
		logger.info("Creating document languages support.");

		DocumentLanguageMapper languageMapper = applicationContext.getBean(DocumentLanguageMapper.class);
		List<DocumentLanguage> languages = languageMapper.getAll();

		if (languages.size() == 0) {
			logger.warn("No document languages defined. Adding new (default) language.");
			DocumentLanguage language = DocumentLanguage.builder()
					.code("eng")
					.name("English")
					.nativeName("English")
					.build();

			languageMapper.save(language);
			languageMapper.setDefault(language);
		} else {
			DocumentLanguage defaultLanguage = languageMapper.getDefault();
			if (defaultLanguage == null) {
				defaultLanguage = Optional.ofNullable(languageMapper.findByCode("eng")).orElseGet(() -> languages.get(0));

				logger.warn("Default document language is not set. Setting it to " + defaultLanguage);

				languageMapper.setDefault(defaultLanguage);
			}
		}

		Map<String, DocumentLanguage> languagesByCodes = Maps.newHashMap();
		Map<String, DocumentLanguage> languagesByHosts = Maps.newHashMap();

		for (DocumentLanguage language : languages) {
			languagesByCodes.put(language.getCode(), language);
		}

		// Read "virtual" hosts mapped to languages.
		String prefix = "i18n.host.";
		int prefixLength = prefix.length();
		Properties properties = Imcms.getServerProperties();

		for (Map.Entry propertyEntry : properties.entrySet()) {
			String propName = (String) propertyEntry.getKey();

			if (!propName.startsWith(prefix)) {
				continue;
			}

			String languageCode = propName.substring(prefixLength);
			String propertyVal = (String) propertyEntry.getValue();

			logger.info("I18n configuration: language code [" + languageCode + "] mapped to host(s) [" + propertyVal + "].");

			DocumentLanguage language = languagesByCodes.get(languageCode);

			if (language == null) {
				String msg = "I18n configuration error. Language with code [" + languageCode + "] is not defined in the database.";
				logger.fatal(msg);
				throw new DocumentLanguageException(msg);
			}

			String hosts[] = propertyVal.split("[ \\t]*,[ \\t]*");

			for (String host : hosts) {
				languagesByHosts.put(host.trim(), language);
			}
		}
		return new DocumentLanguages(languages, languagesByHosts, languageMapper.getDefault());
	}

	/**
	 * Inits and/or updates database if necessary.
	 */
	public static void prepareDatabase() {
		String sqlScriptsPath = getSQLScriptsPath();

		InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("schema.xml");

		if (inputStream == null) {
			String errMsg = "Database schema config file 'schema.xml' can not be found in the classpath.";
			logger.fatal(errMsg);
			throw new RuntimeException(errMsg);
		}

		logger.info("Loading database schema config from stream");
		Schema schema = Schema.fromInputStream(inputStream);

		DataSource dataSource = applicationContext.getBean("dataSource", DataSource.class);
		DB db = new DB(dataSource);

		db.prepare(schema.setScriptsDir(sqlScriptsPath));
	}

	public static String getSQLScriptsPath() {
		if (path == null) throw new IllegalStateException("Application path is not set.");
		if (sqlScriptsPath == null) throw new IllegalStateException("SQL scripts path is not set.");

		return sqlScriptsPath.startsWith("/")
				? sqlScriptsPath
				: new File(path.getAbsolutePath(), sqlScriptsPath).getAbsolutePath();
	}

	public static void setSQLScriptsPath(String sqlScriptsPath) {
		Imcms.sqlScriptsPath = sqlScriptsPath;
	}

	public static String getSolrHome() {
		if (path == null) throw new IllegalStateException("Application path is not set.");
		String solrHome = DEFAULT_SQLR_HOME;

		return solrHome.startsWith("/")
				? solrHome
				: new File(path.getAbsolutePath(), solrHome).getAbsolutePath();
	}

	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public static void setApplicationContext(ApplicationContext applicationContext) {
		Imcms.applicationContext = applicationContext;
	}

	public static boolean isPrepareDatabaseOnStart() {
		return prepareDatabaseOnStart;
	}

	public static void setPrepareDatabaseOnStart(boolean prepareDatabaseOnStart) {
		Imcms.prepareDatabaseOnStart = prepareDatabaseOnStart;
	}

    public static ContentManagementSystem fromRequest(ServletRequest request) {
        return ContentManagementSystem.fromRequest(request);
    }

    /**
     * Returns is document versioning feature are turned on in server properties or not
     */
    public static boolean isVersioningAllowed() {
        return isVersioningAllowed;
    }

    public static class StartupException extends RuntimeException {
		public StartupException(String message, Exception e) {
			super(message, e);
		}
	}
}