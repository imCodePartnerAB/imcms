package imcode.server;

import com.imcode.imcms.api.*;
import com.imcode.imcms.api.GetDocumentCallback;
import com.imcode.imcms.dao.SystemDao;
import com.imcode.imcms.util.clojure.ClojureUtils;
import imcode.util.CachingFileLoader;
import imcode.util.Prefs;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.*;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
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
import com.imcode.imcms.servlet.ImcmsMode;
import com.imcode.imcms.servlet.ImcmsListener;

/**
 * Singleton registry.
 * 
 * Path and ApplicationContext must be set before the start method invocation.
 */
public class Imcms {

    private static final String SERVER_PROPERTIES_FILENAME = "server.properties";
    public static final String ASCII_ENCODING = "US-ASCII";
    public static final String ISO_8859_1_ENCODING = "ISO-8859-1";
    public static final String UTF_8_ENCODING = "UTF-8";
    public static final String DEFAULT_ENCODING = UTF_8_ENCODING;

    /** Absolute deployment path. */
    private static File path;
    
    /** Prefs config path relative to deployment path. */
    private static final String DEFAULT_RELATIVE_PREFS_CONFIG_PATH = "WEB-INF/conf";

    private static Logger logger = Logger.getLogger(Imcms.class);

    /** Services. */
    private static ImcmsServices services;

    private static BasicDataSource apiDataSource;
    private static BasicDataSource dataSource;

    /**
     * Used to disable db init/upgrade on start-up.
     */
    private static boolean prepareDatabaseOnStart = true;

    private static ImcmsMode mode = ImcmsMode.MAINTENANCE;
    private static List<ImcmsListener> listeners = new LinkedList<ImcmsListener>();
    private static Exception startEx;

    /** Springframework application context. */
    public static ApplicationContext applicationContext;


	/**
     * Document requests bound to HttpRequestSession.
     *
     * Since session is not always available in legacy API (such as DocumentMapper) docRequest is also bound to
     * this thread local.
     *
     * @see com.imcode.imcms.servlet.ImcmsFilter
     */
	private static ThreadLocal<GetDocumentCallback> getDocumentCallbacks;

    
    /**
     * Internalization support.
     */
    private static I18nSupport i18nSupport;


    /** Can not be instantiated directly. */
    private Imcms() {}

    
    /**
     * @return ImcmsServices interface implementation.
     */
    public static ImcmsServices getServices() {
        return services;
    }

    //  TODO: Refactor.
    public static void start() throws StartupException {
        if (path == null) {
            throw new IllegalStateException("Imcms path is not set.");
        }        

        if (applicationContext == null) {
            throw new IllegalStateException("Spring application context is not set.");
        }
        
        setStartEx(null);

        try {
            getDocumentCallbacks = new ThreadLocal<GetDocumentCallback>();

            if (prepareDatabaseOnStart) {
                prepareDatabase();
            }
            
            initI18nSupport();
            
            services = createServices();

            for (ImcmsListener listener: listeners) {
                listener.onImcmsStart();
            }
        } catch (Exception e) {
            logger.error(e, e);
            setStartEx(e);

            throw new StartupException("" +
                    "Application could not be started. Please see the log file in WEB-INF/logs/ for details.", e);
        }
    }

    public static void setPath(File path) {
        File prefsConfigPath = new File(path, DEFAULT_RELATIVE_PREFS_CONFIG_PATH);

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
        Database database = createDatabase(serverprops);
        LocalizedMessageProvider localizedMessageProvider = new CachingLocalizedMessageProvider(new ImcmsPrefsLocalizedMessageProvider());

        final CachingFileLoader fileLoader = new CachingFileLoader();
        return new DefaultImcmsServices(database, serverprops, localizedMessageProvider, fileLoader, new DefaultProcedureExecutor(database, fileLoader));
    }

    private static Database createDatabase(Properties serverprops) {
        dataSource = createDataSource(serverprops);
        return new DataSourceDatabase(dataSource);
    }

    public synchronized static DataSource getApiDataSource() {
        if ( null == apiDataSource ) {
            Properties serverprops = getServerProperties();
            logger.debug("Creating API DataSource.");
            apiDataSource = createDataSource(serverprops);
        }
        return apiDataSource;
    }

    public static Properties getServerProperties() {
        try {
            return Prefs.getProperties(SERVER_PROPERTIES_FILENAME);
        } catch ( IOException e ) {
            logger.fatal("Failed to initialize imCMS", e);
            throw new UnhandledException(e);
        }
    }

    private static BasicDataSource createDataSource(Properties props) {

        String jdbcDriver = props.getProperty("JdbcDriver");
        String jdbcUrl = props.getProperty("JdbcUrl");
        String user = props.getProperty("User");
        String password = props.getProperty("Password");
        int maxConnectionCount = Integer.parseInt(props.getProperty("MaxConnectionCount"));

        logger.debug("JdbcDriver = " + jdbcDriver);
        logger.debug("JdbcUrl = " + jdbcUrl);
        logger.debug("User = " + user);
        logger.debug("MaxConnectionCount = " + maxConnectionCount);

        return createDataSource(jdbcDriver, jdbcUrl, user, password, maxConnectionCount);
    }

    public synchronized static void restartCms() {
        stop();
        start();
    }


    // TODO - print stack trace to imcms logger not app logger.
    public static void stop() {
        if ( null != apiDataSource ) {
            try {
                logger.debug("Closing API DataSource.");
                apiDataSource.close();
            } catch ( SQLException e ) {
                logger.error(e, e);
            }
        }

        if ( null != dataSource ) {
            try {
                logger.debug("Closing main DataSource.");
                dataSource.close();
            } catch ( SQLException e ) {
                logger.error(e, e);
            }
        }

        Prefs.flush();

        services = null;

        for (ImcmsListener listener: listeners) {
            listener.onImcmsStop();
        }
    }

    private static void logDatabaseVersion(BasicDataSource basicDataSource) throws SQLException {
        Connection connection = basicDataSource.getConnection();
        DatabaseMetaData metaData = connection.getMetaData();
        logger.info("Database product version = " + metaData.getDatabaseProductVersion());
        connection.close();
    }

    public static BasicDataSource createDataSource(String jdbcDriver, String jdbcUrl,
                                                   String user, String password,
                                                   int maxConnectionCount) {
        try {
            BasicDataSource basicDataSource = new BasicDataSource();
            basicDataSource.setDriverClassName(jdbcDriver);
            basicDataSource.setUsername(user);
            basicDataSource.setPassword(password);
            basicDataSource.setUrl(jdbcUrl);

            basicDataSource.setMaxActive(maxConnectionCount);
            basicDataSource.setMaxIdle(maxConnectionCount);
            basicDataSource.setDefaultAutoCommit(true);
            basicDataSource.setPoolPreparedStatements(true);
            basicDataSource.setTestOnBorrow(true);
            basicDataSource.setValidationQuery("select 1");

            logDatabaseVersion(basicDataSource);

            return basicDataSource;
        } catch ( SQLException ex ) {
            String message = "Could not connect to database "+ jdbcUrl + " with driver " + jdbcDriver + ": "+ex.getMessage()+" Error code: "
                             + ex.getErrorCode() + " SQL GroupData: " + ex.getSQLState();
            logger.fatal(message, ex);
            throw new RuntimeException(message, ex);
        }
    }

    public static class StartupException extends RuntimeException {

        public StartupException(String message, Exception e) {
            super(message, e) ;
        }
    }

    /**
     * @param getDocumentCallback document request bound to user's session.
     */
    public static void setGetDocumentCallback(GetDocumentCallback getDocumentCallback) {
    	getDocumentCallbacks.set(getDocumentCallback);
    }

    /**
     * Returns GetDocumentCallback instance
     * @return
     */
    public static GetDocumentCallback getGetDocumentCallback() {
    	return getDocumentCallbacks.get();
    }


    public static ImcmsMode setMode(ImcmsMode mode) {
        Imcms.mode = mode;
        for (ImcmsListener listener: listeners) {
            listener.onImcmsModeChange(mode);
        }

        return mode;
    }

    public static ImcmsMode setMaintenanceMode() {
        return setMode(ImcmsMode.MAINTENANCE);
    }


    public static ImcmsMode setNormalMode() {
        return setMode(ImcmsMode.NORMAL);
    }

    public static ImcmsMode getMode() {
        return mode;
    }


    public static void setStartEx(Exception startEx) {
        Imcms.startEx = startEx;
        
        for (ImcmsListener listener: listeners) {
            listener.onImcmsStartEx(startEx);
        }
    }


	public static Object getSpringBean(String beanName) {
        if (applicationContext == null) {
            throw new IllegalStateException("Spring application context is not set.");
        }

		return applicationContext.getBean(beanName);
	}
    

    /**
     * Initializes I18N support.
     * Reads languages from the database.
     * Please note that one (and only one) language in the database table i18n_languages must be set as default.
     */
	private static void initI18nSupport() {
    	logger.info("Initializing i18n support.");

    	LanguageDao languageDao = (LanguageDao) Imcms.getSpringBean("languageDao");
    	List<I18nLanguage> languages = languageDao.getAllLanguages();

    	if (languages.size() == 0) {
    		String msg = "I18n configuration error. Database table i18n_languages must contain at least one record.";
    		logger.fatal(msg);
    		throw new I18nException(msg);
    	}

        SystemDao systemDao = (SystemDao)getSpringBean("systemDao");
        SystemProperty languageIdProperty =  systemDao.getProperty("DefaultLanguageId");

        if (languageIdProperty == null) {
    		String msg = "I18n configuration error. Default language is not set.";
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

        I18nSupport i18nSupport = new I18nSupport();

    	i18nSupport.setDefaultLanguage(defaultLanguage);
    	i18nSupport.setLanguages(languages);

        // Read "virtual" hosts mapped to languages.
    	String prefix = "i18n.host.";
    	int prefixLength = prefix.length();
        Properties properties = Imcms.getServerProperties();

        Map<String, I18nLanguage> i18nHosts = new HashMap<String, I18nLanguage>();
        i18nSupport.setHosts(i18nHosts);

    	for (Map.Entry entry: properties.entrySet()) {
    		String key = (String)entry.getKey();

    		if (!key.startsWith(prefix)) {
    			continue;
    		}

			String languageCode = key.substring(prefixLength);
			String value = (String)entry.getValue();

    		logger.info("I18n configurtion: language code [" + languageCode + "] mapped to host(s) [" + value + "].");

			I18nLanguage language = i18nSupport.getByCode(languageCode);

			if (language == null) {
				String msg = "I18n configuration error. Language with code [" + languageCode + "] is not defined in database.";
        		logger.fatal(msg);
        		throw new I18nException(msg);
			}

			String hosts[] = value.split("[ \\t]*,[ \\t]*");

			for (String host: hosts) {
				i18nHosts.put(host.trim(), language);
			}
    	}

        Imcms.i18nSupport = i18nSupport;
	}



    /**
     * Init/upgrades database if necessary.
     */
    public static void prepareDatabase() {
        ClojureUtils.prepareDB();
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static void setApplicationContext(ApplicationContext applicationContext) {
        Imcms.applicationContext = applicationContext;
    }

    public static Exception getStartEx() {
        return startEx;
    }

    public static I18nSupport getI18nSupport() {
        return i18nSupport;
    }

    public static void setI18nSupport(I18nSupport i18nSupport) {
        Imcms.i18nSupport = i18nSupport;
    }

    public static void addListener(ImcmsListener listener) {
        listeners.add(listener);
    }

    public static boolean isPrepareDatabaseOnStart() {
        return prepareDatabaseOnStart;
    }

    public static void setPrepareDatabaseOnStart(boolean prepareDatabaseOnStart) {
        Imcms.prepareDatabaseOnStart = prepareDatabaseOnStart;
    }
}