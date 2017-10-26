package imcode.server;

import com.imcode.db.Database;
import com.imcode.db.commands.SqlQueryCommand;
import com.imcode.db.commands.SqlUpdateCommand;
import com.imcode.imcms.api.DatabaseService;
import com.imcode.imcms.api.DocumentLanguages;
import com.imcode.imcms.api.MailService;
import com.imcode.imcms.db.DefaultProcedureExecutor;
import com.imcode.imcms.db.ProcedureExecutor;
import com.imcode.imcms.domain.service.api.TemplateService;
import com.imcode.imcms.mapping.CategoryMapper;
import com.imcode.imcms.mapping.DocumentMapper;
import com.imcode.imcms.servlet.LoginPasswordManager;
import com.imcode.imcms.util.l10n.LocalizedMessageProvider;
import com.imcode.net.ldap.LdapClientException;
import imcode.server.document.TemplateMapper;
import imcode.server.document.index.DocumentIndex;
import imcode.server.document.index.DocumentIndexFactory;
import imcode.server.kerberos.KerberosLoginService;
import imcode.server.user.*;
import imcode.util.CachingFileLoader;
import imcode.util.DateConstants;
import imcode.util.Parser;
import imcode.util.Utility;
import imcode.util.io.FileUtility;
import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang.UnhandledException;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.NDC;
import org.springframework.context.ApplicationContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DefaultImcmsServices implements ImcmsServices {

    private final static Logger mainLog = Logger.getLogger(ImcmsConstants.MAIN_LOG);
    private final static Logger log = Logger.getLogger(DefaultImcmsServices.class.getName());
    private static final String EXTERNAL_AUTHENTICATOR_LDAP = "LDAP";
    private static final String EXTERNAL_USER_AND_ROLE_MAPPER_LDAP = "LDAP";

    static {
        mainLog.info("Main log started.");
    }

    private final Database database;
    private final LocalizedMessageProvider localizedMessageProvider;
    private final Properties properties;
    private Config config;
    private SystemData sysData;
    private CachingFileLoader fileLoader;
    private ImcmsAuthenticatorAndUserAndRoleMapper imcmsAuthenticatorAndUserAndRoleMapper;
    private ExternalizedImcmsAuthenticatorAndUserRegistry externalizedImcmsAuthAndMapper;
    private DocumentMapper documentMapper;
    private TemplateMapper templateMapper;
    private KeyStore keyStore;
    private KerberosLoginService kerberosLoginService;
    private LanguageMapper languageMapper;
    private ProcedureExecutor procedureExecutor;
    private DocumentLanguages documentLanguages;
    private ApplicationContext applicationContext;

    private DatabaseService databaseService;
    private MailService mailService;
    private TemplateService templateService;

    /**
     * Constructs an DefaultImcmsServices object.
     */
    public DefaultImcmsServices(Database database, Properties props, LocalizedMessageProvider localizedMessageProvider,
                                CachingFileLoader fileLoader, ApplicationContext applicationContext, Config config,
                                DocumentLanguages documentLanguages, DatabaseService databaseService,
                                MailService mailService, TemplateService templateService) {
        this.database = database;
        this.localizedMessageProvider = localizedMessageProvider;
        this.fileLoader = fileLoader;
        this.applicationContext = applicationContext;
        this.documentLanguages = documentLanguages;
        this.config = config;
        this.properties = props;
        this.databaseService = databaseService;
        this.mailService = mailService;
        this.templateService = templateService;

        this.procedureExecutor = new DefaultProcedureExecutor(database, fileLoader);
        this.languageMapper = new LanguageMapper(this.database, config.getDefaultLanguage());
        this.kerberosLoginService = new KerberosLoginService(config);
    }

    public void init() {
        initSso();
        initKeyStore();
        initSysData();
        initSessionCounter();
        initAuthenticatorsAndUserAndRoleMappers(properties);
        initDocumentMapper();
        initTemplateMapper();
    }

    public synchronized int getSessionCounter() {
        return getSessionCounterFromDb();
    }

    /**
     * Set session counter.
     */
    public synchronized void setSessionCounter(int value) {
        setSessionCounterInDb(value);
    }

    public UserDomainObject verifyUserByIpOrDefault(String remoteAddr) {
        UserDomainObject user = imcmsAuthenticatorAndUserAndRoleMapper.getUserByIpAddress(remoteAddr);
        if (null == user) {
            user = imcmsAuthenticatorAndUserAndRoleMapper.getDefaultUser();
        }
        UserDomainObject result = null;
        if (!user.isActive()) {
            logUserDeactivated(user);
        } else {
            result = user;
            logUserLoggedIn(user);
        }
        return result;
    }

    public LocalizedMessageProvider getLocalizedMessageProvider() {
        return localizedMessageProvider;
    }

    public UserDomainObject verifyUser(String login, String password) {
        NDC.push("verifyUser");
        try {
            UserDomainObject result = null;

            boolean userAuthenticates = externalizedImcmsAuthAndMapper.authenticate(login, password);
            UserDomainObject user = externalizedImcmsAuthAndMapper.getUser(login);
            if (null == user) {
                mainLog.info("->User '" + login + "' failed to log in: User not found.");
            } else if (!user.isActive()) {
                logUserDeactivated(user);
            } else if (!userAuthenticates) {
                mainLog.info("->User '" + login + "' failed to log in: Wrong password.");
            } else {
                result = user;
                logUserLoggedIn(user);
            }
            return result;
        } finally {
            NDC.pop();
        }
    }

    public UserDomainObject verifyUser(String clientPrincipalName) {
        String login = clientPrincipalName.substring(0, clientPrincipalName.lastIndexOf('@'));

        NDC.push("verifyUser");
        try {
            UserDomainObject result = null;

            UserDomainObject user = externalizedImcmsAuthAndMapper.getUser(login);
            if (null == user) {
                mainLog.info("->User '" + login + "' failed to log in: User not found.");
            } else if (!user.isActive()) {
                logUserDeactivated(user);
            } else {
                result = user;
                logUserLoggedIn(user);
            }
            return result;
        } finally {
            NDC.pop();
        }
    }

    public void incrementSessionCounter() {
        getDatabase().execute(new SqlUpdateCommand("UPDATE sys_data SET value = value + 1 WHERE type_id = 1", new Object[]{}));
    }

    public void updateMainLog(String event) {
        mainLog.info(event);
    }

    public DocumentMapper getDocumentMapper() {
        return documentMapper;
    }

    public void setDocumentMapper(DocumentMapper documentMapper) {
        this.documentMapper = documentMapper;
    }

    public TemplateMapper getTemplateMapper() {
        return templateMapper;
    }

    public ImcmsAuthenticatorAndUserAndRoleMapper getImcmsAuthenticatorAndUserAndRoleMapper() {
        return imcmsAuthenticatorAndUserAndRoleMapper;
    }

    /**
     * Parse doc replace variables with data , use template
     */
    public String getAdminTemplate(String adminTemplateName, UserDomainObject user,
                                   List<String> tagsWithReplacements) {
        return getTemplateFromDirectory(adminTemplateName, user, tagsWithReplacements, "admin");
    }

    /**
     * Parse doc replace variables with data , use template
     */
    public String getTemplateFromDirectory(String adminTemplateName, UserDomainObject user, List<String> variables,
                                           String directory) {
        if (null == user) {
            throw new NullArgumentException("user");
        }
        String langPrefix = user.getLanguageIso639_2();
        return getTemplate(langPrefix + "/" + directory + "/" + adminTemplateName, variables);
    }

    public Config getConfig() {
        return config;
    }

    private File getRealContextPath() {
        return Imcms.getPath();
    }

    public KeyStore getKeyStore() {
        return keyStore;
    }

    /**
     * Get session counter date.
     */
    public Date getSessionCounterDate() {
        return getSessionCounterDateFromDb();
    }

    /**
     * Set session counter date.
     */
    public void setSessionCounterDate(Date date) {
        setSessionCounterDateInDb(date);
    }

    public SystemData getSystemData() {
        return sysData;
    }

    public void setSystemData(SystemData sd) {
        String[] sqlParams;

        sqlParams = new String[]{"" + sd.getStartDocument()};
        getProcedureExecutor().executeUpdateProcedure("StartDocSet", sqlParams);

        database.execute(new SqlUpdateCommand("UPDATE sys_data SET value = ? WHERE type_id = 4", new Object[]{
                sd.getServerMaster()}));
        database.execute(new SqlUpdateCommand("UPDATE sys_data SET value = ? WHERE type_id = 5", new Object[]{
                sd.getServerMasterAddress()}));

        database.execute(new SqlUpdateCommand("UPDATE sys_data SET value = ? WHERE type_id = 6", new Object[]{
                sd.getWebMaster()}));
        database.execute(new SqlUpdateCommand("UPDATE sys_data SET value = ? WHERE type_id = 7", new Object[]{
                sd.getWebMasterAddress()}));

        sqlParams = new String[]{sd.getSystemMessage()};
        getProcedureExecutor().executeUpdateProcedure("SystemMessageSet", sqlParams);

        /* Update the local copy last, so we stay aware of any database errors */
        this.sysData = sd;
    }

    public Database getDatabase() {
        return database;
    }

    public CategoryMapper getCategoryMapper() {
        return documentMapper.getCategoryMapper();
    }

    public LanguageMapper getLanguageMapper() {
        return this.languageMapper;
    }

    public CachingFileLoader getFileCache() {
        return fileLoader;
    }

    public RoleGetter getRoleGetter() {
        return imcmsAuthenticatorAndUserAndRoleMapper;
    }

    public ProcedureExecutor getProcedureExecutor() {
        return procedureExecutor;
    }

    public KerberosLoginService getKerberosLoginService() {
        return kerberosLoginService;
    }

    public DocumentLanguages getDocumentLanguages() {
        return documentLanguages;
    }

    public <T> T getManagedBean(Class<T> requiredType) {
        return applicationContext.getBean(requiredType);
    }

    public DatabaseService getDatabaseService() {
        return databaseService;
    }

    @Override
    public MailService getMailService() {
        return mailService;
    }

    @Override
    public TemplateService getTemplateService() {
        return templateService;
    }

    private Object chooseInstance(String strToCompare, String mapperName, Properties propertiesSubset) {
        try {
            if (null == mapperName) {
                return null;
            } else if (strToCompare.equalsIgnoreCase(mapperName)) {
                return new LdapUserAndRoleRegistry(propertiesSubset);
            } else {
                return Class.forName(mapperName).newInstance();
            }
        } catch (LdapClientException e) {
            log.error("LdapUserAndRoleRegistry could not be created, using default user and role documentMapper.", e);
        } catch (ClassNotFoundException e) {
            log.error("Could not create instance of class '" + mapperName + "'.", e);
        } catch (Exception e) {
            log.error("Error", e);
        }
        return null;
    }

    private void initSso() {
        if (!config.isSsoEnabled()) {
            return;
        }

        if (config.isSsoUseLocalJaasConfig()) {
            File jaasConfigFile = new File(getRealContextPath(), "WEB-INF/conf/jaas.conf");

            System.setProperty("java.security.auth.login.config", jaasConfigFile.getAbsolutePath());
        }

        if (config.isSsoUseLocalKrbConfig()) {
            File krbConfigFile = new File(getRealContextPath(), "WEB-INF/conf/krb.conf");

            System.setProperty("java.security.krb5.conf", krbConfigFile.getAbsolutePath());
        }

        if (config.isSsoKerberosDebug()) {
            System.setProperty("sun.security.krb5.debug", "true");
        }
    }

    private void initKeyStore() {
        String keyStoreType = config.getKeyStoreType();
        if (StringUtils.isBlank(keyStoreType)) {
            keyStoreType = KeyStore.getDefaultType();
        }
        try {
            keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
        } catch (GeneralSecurityException | IOException e) {
            throw new UnhandledException(e);
        }
        String keyStorePath = config.getKeyStorePath();
        if (StringUtils.isNotBlank(keyStorePath)) {
            File keyStoreFile = FileUtility.getFileFromWebappRelativePath(keyStorePath);
            try {
                keyStore.load(new FileInputStream(keyStoreFile), null);
            } catch (Exception e) {
                log.error("Failed to load keystore from path " + keyStoreFile, e);
            }
        }
    }

    private void initSysData() {
        sysData = getSystemDataFromDb();
    }

    private void initSessionCounter() {
        int sessionCounter;
        Date sessionCounterDate;

        try {
            sessionCounter = getSessionCounterFromDb();
            sessionCounterDate = getSessionCounterDateFromDb();
        } catch (NumberFormatException ex) {
            log.fatal("Failed to get SessionCounter from db.", ex);
            throw ex;
        }

        log.info("SessionCounter: " + sessionCounter);
        log.info("SessionCounterDate: " + sessionCounterDate);
    }

    private Date getSessionCounterDateFromDb() {
        try {
            DateFormat dateFormat = new SimpleDateFormat(DateConstants.DATE_FORMAT_STRING);
            final Object[] parameters = new String[0];
            return dateFormat.parse((String) getDatabase().execute(new SqlQueryCommand("SELECT value FROM sys_data WHERE type_id = 2", parameters, Utility.SINGLE_STRING_HANDLER)));
        } catch (ParseException ex) {
            log.fatal("Failed to get SessionCounterDate from db.", ex);
            throw new UnhandledException(ex);
        }
    }

    private int getSessionCounterFromDb() {
        final Object[] parameters = new String[0];
        return Integer.parseInt((String) getDatabase().execute(new SqlQueryCommand("SELECT value FROM sys_data WHERE type_id = 1", parameters, Utility.SINGLE_STRING_HANDLER)));
    }

    // todo: implement rebuild scheduler ...getConfig().getIndexingSchedulePeriodInMinutes()...
    // todo: Search Terms Logging: Do not parse and write query term into db every time - queue and write in a separate worker
    private void initDocumentMapper() {
        documentMapper = getManagedBean(DocumentMapper.class);
        documentMapper.init(database, config, documentLanguages);

        DocumentIndex documentIndexService = new LoggingDocumentIndex(database,
                new PhaseQueryFixingDocumentIndex(DocumentIndexFactory.create(this)));

        documentMapper.setDocumentIndex(documentIndexService);
    }

    private void initTemplateMapper() {
        templateMapper = new TemplateMapper(this);
    }

    private void initAuthenticatorsAndUserAndRoleMappers(Properties props) {
        String externalAuthenticatorName = props.getProperty("ExternalAuthenticator");
        String externalUserAndRoleMapperName = props.getProperty("ExternalUserAndRoleMapper");

        Authenticator externalAuthenticator = null;
        UserAndRoleRegistry externalUserAndRoleRegistry = null;

        boolean externalAuthenticatorIsSet = StringUtils.isNotBlank(externalAuthenticatorName);
        boolean externalUserAndRoleRegistryIsSet = StringUtils.isNotBlank(externalUserAndRoleMapperName);
        if (externalAuthenticatorIsSet && externalUserAndRoleRegistryIsSet) {
            log.info("ExternalAuthenticator: " + externalAuthenticatorName);
            log.info("ExternalUserAndRoleMapper: " + externalUserAndRoleMapperName);
            externalAuthenticator =
                    initExternalAuthenticator(externalAuthenticatorName, props);
            externalUserAndRoleRegistry =
                    initExternalUserAndRoleMapper(externalUserAndRoleMapperName, props);
            if (null == externalAuthenticator || null == externalUserAndRoleRegistry) {
                log.error("Failed to initialize both authenticator and user-and-role-documentMapper, using default implementations.");
                externalAuthenticator = null;
                externalUserAndRoleRegistry = null;
            }
        } else if (!externalAuthenticatorIsSet && !externalUserAndRoleRegistryIsSet) {
            log.info("ExternalAuthenticator not set.");
            log.info("ExternalUserAndRoleMapper not set.");
        } else {
            log.error("External authenticator and external usermapper should both be either set or not set. Using default implementation.");
        }

        // TODO: problem if primary LDAP classes are not instantiatd,
        // because of conf error, secondary LDAP also will not be instantiated
        if (externalAuthenticator != null
//				&& externalUserAndRoleRegistry != null // always true
                && externalAuthenticator instanceof LdapUserAndRoleRegistry
                && externalUserAndRoleRegistry instanceof LdapUserAndRoleRegistry)
        {

            ChainedLdapUserAndRoleRegistry chainedLdapUserAndRoleRegistry
                    = new ChainedLdapUserAndRoleRegistry(externalAuthenticator, externalUserAndRoleRegistry);


            initAndAddSecondaryLdapUserAndRoleRegistry(chainedLdapUserAndRoleRegistry,
                    props);

            externalAuthenticator = chainedLdapUserAndRoleRegistry;
            externalUserAndRoleRegistry = chainedLdapUserAndRoleRegistry;
        }

        imcmsAuthenticatorAndUserAndRoleMapper = new ImcmsAuthenticatorAndUserAndRoleMapper(
                this,
                new LoginPasswordManager(StringUtils.trimToNull(config.getLoginPasswordEncryptionSalt())));
        externalizedImcmsAuthAndMapper =
                new ExternalizedImcmsAuthenticatorAndUserRegistry(imcmsAuthenticatorAndUserAndRoleMapper, externalAuthenticator,
                        externalUserAndRoleRegistry, getLanguageMapper().getDefaultLanguage());
        externalizedImcmsAuthAndMapper.synchRolesWithExternal();
        imcmsAuthenticatorAndUserAndRoleMapper.encryptUnencryptedUsersLoginPasswords();
    }

    /**
     * Inits and adds secondary LdapUserAndRoleRegistry to the ChainedLdapUserAndRoleRegistry.
     *
     * @param chainedLdapUserAndRoleRegistry instance of ChainedLdapUserAndRoleRegistry
     * @param props                          configuration properties
     */
    private void initAndAddSecondaryLdapUserAndRoleRegistry(
            ChainedLdapUserAndRoleRegistry chainedLdapUserAndRoleRegistry,
            Properties props) {

        final String secondaryLdapPrefix = "Secondary";
        final int secondaryLdapPrefixLength = secondaryLdapPrefix.length();

        log.info("Searching for secondary LDAP configuration parameters.");

        Properties secondaryLdapProperties = new Properties();

        Enumeration names = props.propertyNames();

        while (names.hasMoreElements()) {
            String name = (String) names.nextElement();

            if (name.startsWith(secondaryLdapPrefix)) {
                String newName = name.substring(secondaryLdapPrefixLength);
                String value = props.getProperty(name);

                secondaryLdapProperties.setProperty(newName, value);
            }
        }

        if (secondaryLdapProperties.size() == 0) {
            log.info("Secondary LDAP configuration parameters not found.");
        } else {
            log.info("Found secondary LDAP configuration parameters. " +
                    "Initializing secondary LDAP user and role registry");

            // Copied from method initAuthenticatorsAndUserAndRoleMappers
            // TODO refactor
            String externalAuthenticatorName = secondaryLdapProperties.getProperty("ExternalAuthenticator");
            String externalUserAndRoleMapperName = secondaryLdapProperties.getProperty("ExternalUserAndRoleMapper");

            Authenticator externalAuthenticator;
            UserAndRoleRegistry externalUserAndRoleRegistry;

            boolean externalAuthenticatorIsSet = StringUtils.isNotBlank(externalAuthenticatorName);
            boolean externalUserAndRoleRegistryIsSet = StringUtils.isNotBlank(externalUserAndRoleMapperName);

            if (!externalAuthenticatorIsSet || !externalUserAndRoleRegistryIsSet) {
                log.error("Secondary LDAP configuration ignored. External authenticator and external usermapper should both be either set or not set.");
            } else {
                log.info("SecondaryExternalAuthenticator: " + externalAuthenticatorName);
                log.info("SecondaryExternalUserAndRoleMapper: " + externalUserAndRoleMapperName);
                externalAuthenticator =
                        initExternalAuthenticator(externalAuthenticatorName, secondaryLdapProperties);
                externalUserAndRoleRegistry =
                        initExternalUserAndRoleMapper(externalUserAndRoleMapperName, secondaryLdapProperties);

                if (null == externalAuthenticator || null == externalUserAndRoleRegistry) {
                    log.error("Secondary LDAP configuration ignored. Failed to initialize both authenticator and user-and-role-documentMapper.");
                } else if (!(externalAuthenticator instanceof LdapUserAndRoleRegistry)
                        || !(externalUserAndRoleRegistry instanceof LdapUserAndRoleRegistry))
                {
                    log.error("Secondary LDAP configuration ignored. Both SecondaryExternalAuthenticator and SecondaryExternalUserAndRoleMapper properties should be set to LDAP.");
                } else {
                    chainedLdapUserAndRoleRegistry.addLink(externalAuthenticator, externalUserAndRoleRegistry);
                }
            }
        }
    }

    private void logUserDeactivated(UserDomainObject user) {
        mainLog.info("->User '" + user.getLoginName() + "' failed to log in: User deactivated.");
    }

    private void logUserLoggedIn(UserDomainObject user) {
        if (!user.isDefaultUser()) {
            mainLog.info("->User '" + user.getLoginName() + "' successfully logged in.");
        }
    }

    private UserAndRoleRegistry initExternalUserAndRoleMapper(String externalUserAndRoleMapperName,
                                                              Properties userAndRoleMapperPropertiesSubset) {

        return (UserAndRoleRegistry) chooseInstance(EXTERNAL_USER_AND_ROLE_MAPPER_LDAP,
                externalUserAndRoleMapperName, userAndRoleMapperPropertiesSubset);
    }

    private Authenticator initExternalAuthenticator(String externalAuthenticatorName,
                                                    Properties authenticatorPropertiesSubset) {

        return (Authenticator) chooseInstance(EXTERNAL_AUTHENTICATOR_LDAP,
                externalAuthenticatorName, authenticatorPropertiesSubset);
    }

    private String getTemplate(String path, List<String> variables) {
        try {
            if (null != variables) {
                List<String> parseDocVariables = new ArrayList<>(variables.size());
                for (Iterator<String> iterator = variables.iterator(); iterator.hasNext(); ) {
                    String key = iterator.next();
                    String value = iterator.next();
                    boolean isVelocityVariable = StringUtils.isAlpha(key) || (value == null);
                    if (!isVelocityVariable) {
                        parseDocVariables.add(key);
                        parseDocVariables.add(value);
                    }
                }
                variables = parseDocVariables;
            }
            StringWriter stringWriter = new StringWriter();
            String result = stringWriter.toString();
            if (null != variables) {
                result = Parser.parseDoc(result, variables.toArray(new String[variables.size()]));
            }
            return result;
        } catch (Exception e) {
            throw new UnhandledException("getTemplate(\"" + path + "\") : " + e.getMessage(), e);
        }
    }

    private void setSessionCounterInDb(int value) {
        final Object[] parameters = new String[]{""
                + value};
        getProcedureExecutor().executeUpdateProcedure("SetSessionCounterValue", parameters);
    }

    private void setSessionCounterDateInDb(Date date) {
        DateFormat dateFormat = new SimpleDateFormat(DateConstants.DATE_FORMAT_STRING);
        final Object[] parameters = new String[]{dateFormat.format(date)};
        getProcedureExecutor().executeUpdateProcedure("SetSessionCounterDate", parameters);
    }

    private SystemData getSystemDataFromDb() {

        SystemData sd = new SystemData();

        final Object[] parameters5 = new String[0];
        String startDocument = (String) getDatabase().execute(new SqlQueryCommand("SELECT value FROM sys_data WHERE type_id = 0", parameters5, Utility.SINGLE_STRING_HANDLER));
        sd.setStartDocument(startDocument == null ? ImcmsConstants.DEFAULT_START_DOC_ID : Integer.parseInt(startDocument));

        final Object[] parameters4 = new String[0];
        String systemMessage = (String) getDatabase().execute(new SqlQueryCommand("SELECT value FROM sys_data WHERE type_id = 3", parameters4, Utility.SINGLE_STRING_HANDLER));
        sd.setSystemMessage(systemMessage);

        final Object[] parameters3 = new String[0];
        String serverMasterName = (String) getDatabase().execute(new SqlQueryCommand("SELECT value FROM sys_data WHERE type_id = 4", parameters3, Utility.SINGLE_STRING_HANDLER));
        sd.setServerMaster(serverMasterName);

        final Object[] parameters2 = new String[0];
        String serverMasterAddress = (String) getDatabase().execute(new SqlQueryCommand("SELECT value FROM sys_data WHERE type_id = 5", parameters2, Utility.SINGLE_STRING_HANDLER));
        sd.setServerMasterAddress(serverMasterAddress);

        final Object[] parameters1 = new String[0];
        String webMasterName = (String) getDatabase().execute(new SqlQueryCommand("SELECT value FROM sys_data WHERE type_id = 6", parameters1, Utility.SINGLE_STRING_HANDLER));
        sd.setWebMaster(webMasterName);

        final Object[] parameters = new String[0];
        String webMasterAddress = (String) getDatabase().execute(new SqlQueryCommand("SELECT value FROM sys_data WHERE type_id = 7", parameters, Utility.SINGLE_STRING_HANDLER));
        sd.setWebMasterAddress(webMasterAddress);

        final Object[] parameter9 = new String[0];
        String userLoginPasswordExpirationInterval = (String) getDatabase().execute(new SqlQueryCommand("SELECT value FROM sys_data WHERE type_id = 9", parameter9, Utility.SINGLE_STRING_HANDLER));
        if (userLoginPasswordExpirationInterval == null) {
            log.warn("System property userLoginPasswordResetExpirationInterval is not set; using default");
        } else {
            try {
                int interval = Integer.parseInt(userLoginPasswordExpirationInterval);

                if (interval > 0) {
                    sd.setUserLoginPasswordResetExpirationInterval(interval);
                } else {
                    log.warn(String.format(
                            "System property userLoginPasswordResetExpirationInterval must be  '> 0' but set to %d; using default.",
                            interval));
                }
            } catch (Throwable t) {
                log.warn(String.format(
                        "System property userLoginPasswordResetExpirationInterval value must be positive integer but set to %s; using default.",
                        userLoginPasswordExpirationInterval));
            }
        }

        return sd;
    }
}
