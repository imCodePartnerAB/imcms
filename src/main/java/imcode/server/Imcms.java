package imcode.server;

import com.google.common.collect.Maps;
import com.imcode.imcms.api.*;
import com.imcode.imcms.dao.SystemDao;
import com.imcode.imcms.db.DB;
import com.imcode.imcms.db.Schema;
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
import com.imcode.imcms.dao.LanguageDao;

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
     * Absolute application (deployment) path.
     */
    private static File path;

    /**
     * Prefs config path relative to deployment path.
     */
    private static final String DEFAULT_PREFS_CONFIG_PATH = "WEB-INF/conf";

    /**
     * SQL scripts directory path relative to deployment path
     */
    private static final String DEFAULT_SQL_SCRIPTS_PATH = "WEB-INF/sql";

    /**
     * Embedded solr home directory, elative to deployment path
     */
    private static final String DEFAULT_SQLR_HOME = "WEB-INF/solr";

    private static Logger logger = Logger.getLogger(Imcms.class);

    /**
     * Core services.
     */
    private static ImcmsServices services;

    /**
     * Used to disable db init/upgrade on start.
     */
    private static boolean prepareDatabaseOnStart = true;

    /**
     * Spring-framework application context.
     */
    public static ApplicationContext applicationContext;

    private static String sqlScriptsPath = DEFAULT_SQL_SCRIPTS_PATH;
    private static String solrHome = DEFAULT_SQLR_HOME;

    /**
     * Users associated with servlet requests.
     *
     * @see com.imcode.imcms.servlet.ImcmsFilter
     */
    private static InheritableThreadLocal<UserDomainObject> users = new InheritableThreadLocal<UserDomainObject>();

    private static String serverPropertiesFilename = SERVER_PROPERTIES_FILENAME;


    /**
     * Can not be instantiated directly.
     */
    private Imcms() {
    }


    /**
     * @return ImcmsServices interface implementation.
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
    public static void start() throws StartupException {
        try {
            if (path == null) {
                throw new IllegalStateException("Imcms path is not set.");
            }

            if (applicationContext == null) {
                throw new IllegalStateException("Spring application context is not set.");
            }

            users = new InheritableThreadLocal<UserDomainObject>();

            if (prepareDatabaseOnStart) {
                prepareDatabase();
            }

            services = createServices();
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
                createI18nSupport());

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

    public synchronized static void restartCms() {
        stop();
        start();
    }


    public static void stop() {
        Prefs.flush();

        if (services != null) {
            services.getDocumentMapper().getDocumentIndex().shutdown();
        }

        services = null;
    }


    public static class StartupException extends RuntimeException {
        public StartupException(String message, Exception e) {
            super(message, e);
        }
    }


    /**
     * Associates a user with a current thread.
     * This is a system call - must be never used in a client code.
     */
    public static void setUser(UserDomainObject user) {
        users.set(user);
    }

    /**
     * Removes a user from a current thread.
     * This is a system call - must be never used in a client code.
     */
    public static void removeUser() {
        users.set(null);
    }

    /**
     * @return a user associated with a current thread.
     */
    public static UserDomainObject getUser() {
        return users.get();
    }

    /**
     * Initializes I18N support.
     * Reads languages from the database.
     */
    private static I18nSupport createI18nSupport() {
        logger.info("Creating i18n support.");

        LanguageDao languageDao = applicationContext.getBean(LanguageDao.class);
        SystemDao systemDao = applicationContext.getBean(SystemDao.class);
        SystemProperty languageIdProperty = systemDao.getProperty("DefaultLanguageId");

        Map<String, I18nLanguage> languagesByCodes = Maps.newHashMap();
        Map<String, I18nLanguage> languagesByHosts = Maps.newHashMap();

        for (I18nLanguage language: languageDao.getAllLanguages()) {
            languagesByCodes.put(language.getCode(), language);
        }

        if (languagesByCodes.size() == 0) {
            String msg = "I18n configuration error. Database table i18n_languages must contain at least one record.";
            logger.fatal(msg);
            throw new I18nException(msg);
        }

        if (languageIdProperty == null) {
            String msg = "I18n configuration error. Default language (DefaultLanguageId system property) is not set.";
            logger.fatal(msg);
            throw new I18nException(msg);
        }


        String languageId = languageIdProperty.getValue();
        I18nLanguage defaultLanguage = languageDao.getById(Integer.parseInt(languageId));

        if (defaultLanguage == null) {
            String msg = String.format("I18n configuration error. Default language can not be set. There is no language with id %s.", languageId);
            logger.fatal(msg);
            throw new I18nException(msg);
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

            I18nLanguage language = languagesByCodes.get(languageCode);

            if (language == null) {
                String msg = "I18n configuration error. Language with code [" + languageCode + "] is not defined in database.";
                logger.fatal(msg);
                throw new I18nException(msg);
            }

            String hosts[] = value.split("[ \\t]*,[ \\t]*");

            for (String host : hosts) {
                languagesByHosts.put(host.trim(), language);
            }
        }


        return new I18nSupport(languagesByCodes, languagesByHosts, defaultLanguage);
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