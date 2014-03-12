package imcode.server;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.imcode.imcms.api.*;
import com.imcode.imcms.db.DB;
import com.imcode.imcms.db.Schema;
import com.imcode.imcms.mapping.DocumentLanguageMapper;
import imcode.server.user.UserDomainObject;
import imcode.util.CachingFileLoader;
import imcode.util.Prefs;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import javax.sql.DataSource;

import org.apache.commons.lang.UnhandledException;
import org.apache.log4j.Logger;

import org.springframework.context.ApplicationContext;

import com.imcode.db.DataSourceDatabase;
import com.imcode.db.Database;
import com.imcode.imcms.db.DefaultProcedureExecutor;
import com.imcode.imcms.util.l10n.CachingLocalizedMessageProvider;
import com.imcode.imcms.util.l10n.ImcmsPrefsLocalizedMessageProvider;
import com.imcode.imcms.util.l10n.LocalizedMessageProvider;

/**
 * Singleton registry.
 */
public class Imcms {

    private static final String SERVER_PROPERTIES_FILENAME = "server.properties";
    public static final String ASCII_ENCODING = "US-ASCII";
    public static final String ISO_8859_1_ENCODING = "ISO-8859-1";
    public static final String UTF_8_ENCODING = "UTF-8";
    public static final String DEFAULT_ENCODING = UTF_8_ENCODING;

    /**
     * Default prefs config path relative to deployment path.
     */
    private static final String DEFAULT_PREFS_CONFIG_PATH = "WEB-INF/conf";

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

    private static volatile String solrHome = DEFAULT_SQLR_HOME;

    private static volatile String serverPropertiesFilename = SERVER_PROPERTIES_FILENAME;

    /**
     * Used to disable db init/upgrade on start.
     */
    private static volatile boolean prepareDatabaseOnStart = true;

    /**
     * Users associated with servlet requests.
     *
     * @see com.imcode.imcms.servlet.ImcmsFilter
     */
    private static InheritableThreadLocal<UserDomainObject> users = new InheritableThreadLocal<UserDomainObject>();


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
     * <p/>
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

    public static void setPath(File path) {
        File prefsConfigPath = new File(path, DEFAULT_PREFS_CONFIG_PATH);

        setPath(path, prefsConfigPath);
    }

    public static void setPath(File path, File prefsConfigPath) {
        Imcms.path = path;
        Prefs.setConfigPath(prefsConfigPath);
    }

    public static File getPath() {
        return path;
    }


    private static ImcmsServices createServices() throws Exception {
        Properties serverprops = getServerProperties();
        logger.debug("Creating main DataSource.");
        Database database = new DataSourceDatabase(getApiDataSource());
        LocalizedMessageProvider localizedMessageProvider = new CachingLocalizedMessageProvider(new ImcmsPrefsLocalizedMessageProvider());

        final CachingFileLoader fileLoader = new CachingFileLoader();
        DefaultImcmsServices services = new DefaultImcmsServices(
                database,
                serverprops,
                localizedMessageProvider,
                fileLoader,
                new DefaultProcedureExecutor(database, fileLoader),
                applicationContext,
                createDocumentLanguageSupport());

        services.getImcmsAuthenticatorAndUserAndRoleMapper().encryptUnencryptedUsersLoginPasswords();
        return services;
    }


    public static DataSource getApiDataSource() {
        return applicationContext.getBean("dataSourceWithAutoCommit", DataSource.class);
    }

    public static Properties getServerProperties() {
        try {
            Properties properties = Prefs.getProperties(serverPropertiesFilename);

            properties.setProperty("SolrHome", getSolrHome());

            return properties;
        } catch (IOException e) {
            logger.fatal("Failed to initialize imCMS", e);
            throw new UnhandledException(e);
        }
    }

    public static synchronized void restartCms() {
        stop();
        start();
    }


    public static synchronized void stop() {
        Prefs.flush();

        if (services != null) {
            services.getDocumentMapper().getDocumentIndex().getService().shutdown();
        }

        services = null;
    }


    public static class StartupException extends RuntimeException {
        public StartupException(String message, Exception e) {
            super(message, e);
        }
    }


    /**
     * Associates a user with a current request thread.
     * Must not be called from a client code.
     */
    public static void setUser(UserDomainObject user) {
        users.set(user);
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
     * Creates and initializes languages.
     *
     * Reads languages from the database.
     * Adds a new language if there are no languages in the database.
     * Sets default language if it is not already set.
     * todo: use language property defined in the conf file as default.
     */
    private static DocumentLanguageSupport createDocumentLanguageSupport() {
        logger.info("Creating document languages support.");

        DocumentLanguageMapper dlm = applicationContext.getBean(DocumentLanguageMapper.class);
        List<DocumentLanguage> languages = dlm.getAll();

        if (languages.size() == 0) {
            logger.warn("No document languages defined. Adding new (default) language.");
            DocumentLanguage language = DocumentLanguage.builder()
                    .code("eng")
                    .name("English")
                    .nativeName("English")
                    .build();

            dlm.save(language);
            dlm.setDefault(language);
        } else {
            DocumentLanguage defaultLanguage = dlm.getDefault();
            if (defaultLanguage == null) {
                defaultLanguage = Optional.fromNullable(dlm.findByCode("eng")).or(languages.get(0));

                logger.warn("Default document language is not set. Setting it to " + defaultLanguage);

                dlm.setDefault(defaultLanguage);
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

        for (Map.Entry entry : properties.entrySet()) {
            String key = (String) entry.getKey();

            if (!key.startsWith(prefix)) {
                continue;
            }

            String languageCode = key.substring(prefixLength);
            String value = (String) entry.getValue();

            logger.info("I18n configuration: language code [" + languageCode + "] mapped to host(s) [" + value + "].");

            DocumentLanguage language = languagesByCodes.get(languageCode);

            if (language == null) {
                String msg = "I18n configuration error. Language with code [" + languageCode + "] is not defined in the database.";
                logger.fatal(msg);
                throw new DocumentLanguageException(msg);
            }

            String hosts[] = value.split("[ \\t]*,[ \\t]*");

            for (String host : hosts) {
                languagesByHosts.put(host.trim(), language);
            }
        }

        return new DocumentLanguageSupport(languages, languagesByHosts, dlm.getDefault());
    }

    /**
     * Inits and/or updates database if necessary.
     */
    public static void prepareDatabase() {
        String sqlScriptsPath = getSQLScriptsPath();

        URL schemaConfFileURL = Imcms.class.getResource("/schema.xml");

        if (schemaConfFileURL == null) {
            String errMsg = "Database schema config file 'schema.xml' can not be found in the classpath.";
            logger.fatal(errMsg);
            throw new RuntimeException(errMsg);
        }

        logger.info(String.format("Loading database schema config from %s.", schemaConfFileURL));
        Schema schema = Schema.load(schemaConfFileURL);

        DataSource dataSource = applicationContext.getBean("dataSource", DataSource.class);
        DB db = new DB(dataSource);

        db.prepare(schema.setScriptsDir(sqlScriptsPath));
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
        if (solrHome == null) throw new IllegalStateException("Embedded SOLr home is not set.");

        return solrHome.startsWith("/")
                ? solrHome
                : new File(path.getAbsolutePath(), solrHome).getAbsolutePath();
    }

    public static String getServerPropertiesFilename() {
        return serverPropertiesFilename;
    }

    public static void setServerPropertiesFilename(String serverPropertiesFilename) {
        Imcms.serverPropertiesFilename = serverPropertiesFilename;
    }
}