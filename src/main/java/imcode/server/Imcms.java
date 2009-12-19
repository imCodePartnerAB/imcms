package imcode.server;

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
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.log4j.Logger;

import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.context.ApplicationContext;
import org.hibernate.Session;
import org.hibernate.HibernateException;

import com.imcode.db.DataSourceDatabase;
import com.imcode.db.Database;
import com.imcode.imcms.db.DefaultProcedureExecutor;
import com.imcode.imcms.util.l10n.CachingLocalizedMessageProvider;
import com.imcode.imcms.util.l10n.ImcmsPrefsLocalizedMessageProvider;
import com.imcode.imcms.util.l10n.LocalizedMessageProvider;
import com.imcode.imcms.schema.SchemaUpgrade;
import com.imcode.imcms.dao.LanguageDao;
import com.imcode.imcms.api.I18nLanguage;
import com.imcode.imcms.api.I18nException;
import com.imcode.imcms.api.I18nSupport;
import com.imcode.imcms.api.RequestInfo;
import com.imcode.imcms.servlet.ImcmsMode;
import com.imcode.imcms.servlet.ImcmsListener;

/**
 * Runtime.
 *
 * Path and ApplicationContext must be set before the start method invocation.
 */
public class Imcms {

    private static final String SERVER_PROPERTIES_FILENAME = "server.properties";
    public static final String ASCII_ENCODING = "US-ASCII";
    public static final String ISO_8859_1_ENCODING = "ISO-8859-1";
    public static final String UTF_8_ENCODING = "UTF-8";
    public static final String DEFAULT_ENCODING = UTF_8_ENCODING;

    /** Absolute application path. */
    private static File path;
    
    /** Prefs config path relative to application path. */
    public static final String DEFAULT_PREFS_CONFIG_PATH = "WEB-INF/conf";

    private static Logger logger = Logger.getLogger(Imcms.class);

    /** Services. */
    private static ImcmsServices services;

    private static BasicDataSource apiDataSource;
    private static BasicDataSource dataSource;

    private static boolean upgradeDatabaseSchemaOnStart = true;

    private static ImcmsMode mode = ImcmsMode.MAINTENANCE;
    private static List<ImcmsListener> listeners = new LinkedList<ImcmsListener>();
    private static Exception startEx;

    /** Springframework application context. */
    public static ApplicationContext applicationContext;


	/** Request info. */
	private static ThreadLocal<RequestInfo> requestInfos;

    private static I18nSupport i18nSupport;

    private static String prefsConfigPath = DEFAULT_PREFS_CONFIG_PATH;


    /** Can not be instantiated directly. */
    private Imcms() {}

    public static ImcmsServices getServices() {
        return services;
    }

    //  TODO: Refactor.
    public static void start() throws StartupException {
        if (path == null) {
            throw new IllegalStateException("Application path is not set.");
        }        

        if (applicationContext == null) {
            throw new IllegalStateException("Spring application context is not set.");
        }
        
        setStartEx(null);

        try {
            File configPath = new File(path, prefsConfigPath);
            Prefs.setConfigPath(configPath);

            requestInfos = new ThreadLocal<RequestInfo>();

            if (upgradeDatabaseSchemaOnStart) {
                upgradeDatabaseSchema();
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
        Imcms.path = path;
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

    public static void setRequestInfo(RequestInfo requestInfo) {
    	requestInfos.set(requestInfo);
    }

    public static RequestInfo getRequestInfo() {
    	return requestInfos.get();
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

        int defaultLanguageRecordCount = CollectionUtils.countMatches(languages, new Predicate() {
            public boolean evaluate(Object language) {
                return ((I18nLanguage)language).isDefault();
            }
        });

        if (defaultLanguageRecordCount == 0) {
    		String msg = "I18n configuration error. Default language is not set.";
    		logger.fatal(msg);
    		throw new I18nException(msg);
        } else if (defaultLanguageRecordCount > 1) {
            String msg = "I18n configuration error. Only one language must be set default.";
            logger.fatal(msg);
            throw new I18nException(msg);
        }

        I18nSupport i18nSupport = new I18nSupport();

        I18nLanguage defaultLanguage = languageDao.getDefaultLanguage();

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
     * Upgrades database schema if necessary.
     */
    private static void upgradeDatabaseSchema() {
        File confXmlFile = new File(Imcms.getPath(), "WEB-INF/conf/schema-upgrade.xml");
        File confXsdFile = new File(Imcms.getPath(), "WEB-INF/conf/schema-upgrade.xsd");
        File scriptsDir = new File(Imcms.getPath(), "WEB-INF/sql");

        final SchemaUpgrade schemaUpgrade = SchemaUpgrade.createInstance(confXmlFile, confXsdFile, scriptsDir);

        // todo: replace with datasource get connection.
        HibernateTemplate template = (HibernateTemplate)Imcms.getSpringBean("hibernateTemplate");

        template.execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                schemaUpgrade.upgrade(session.connection());

                return null;
            }
        });
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

    public static boolean isUpgradeDatabaseSchemaOnStart() {
        return upgradeDatabaseSchemaOnStart;
    }

    public static void setUpgradeDatabaseSchemaOnStart(boolean upgradeDatabaseSchemaOnStart) {
        Imcms.upgradeDatabaseSchemaOnStart = upgradeDatabaseSchemaOnStart;
    }

    public static String getPrefsConfigPath() {
        return prefsConfigPath;
    }

    public static void setPrefsConfigPath(String prefsConfigPath) {
        Imcms.prefsConfigPath = prefsConfigPath;
    }
}