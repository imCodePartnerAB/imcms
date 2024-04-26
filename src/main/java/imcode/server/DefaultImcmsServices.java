package imcode.server;

import com.imcode.db.Database;
import com.imcode.db.commands.SqlQueryCommand;
import com.imcode.db.commands.SqlUpdateCommand;
import com.imcode.imcms.api.*;
import com.imcode.imcms.components.ImageCompressor;
import com.imcode.imcms.db.ProcedureExecutor;
import com.imcode.imcms.domain.component.UserLockValidator;
import com.imcode.imcms.domain.component.azure.AzureAuthenticationProvider;
import com.imcode.imcms.domain.service.UserService;
import com.imcode.imcms.domain.service.*;
import com.imcode.imcms.mapping.CategoryMapper;
import com.imcode.imcms.mapping.DocumentMapper;
import com.imcode.imcms.servlet.LoginPasswordManager;
import com.imcode.imcms.util.l10n.LocalizedMessageProvider;
import com.imcode.net.ldap.LdapClientException;
import imcode.server.document.TemplateMapper;
import imcode.server.document.index.ImageFileIndex;
import imcode.server.kerberos.KerberosLoginService;
import imcode.server.user.*;
import imcode.util.CachingFileLoader;
import imcode.util.DateConstants;
import imcode.util.Parser;
import imcode.util.Utility;
import imcode.util.io.FileUtility;
import lombok.Getter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang.UnhandledException;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class DefaultImcmsServices implements ImcmsServices {

    private final static Logger mainLog = LogManager.getLogger(ImcmsConstants.MAIN_LOG);
    private final static Logger log = LogManager.getLogger(DefaultImcmsServices.class.getName());
    private static final String EXTERNAL_AUTHENTICATOR_LDAP = "ldap";
    private static final String EXTERNAL_USER_AND_ROLE_MAPPER_LDAP = "ldap";

    static {
        mainLog.info("Main log started.");
    }

    @Getter
    private final DocumentMapper documentMapper;
    @Getter
    private final Database database;
    @Getter
    private final LocalizedMessageProvider localizedMessageProvider;
    @Getter
    private final AccessService accessService;
    @Getter
    private final MenuService menuService;
    @Getter
    private final AuthenticationProvidersService authenticationProvidersService;
    @Getter
    private final Config config;
    @Getter
    private final CachingFileLoader fileLoader;
    @Getter
    private final KerberosLoginService kerberosLoginService;
    @Getter
    private final LanguageMapper languageMapper;
    @Getter
    private final ProcedureExecutor procedureExecutor;
    @Getter
    private final DocumentLanguages documentLanguages;
    @Getter
    private final LanguageService languageService;
    @Getter
    private final DatabaseService databaseService;
    @Getter
    private final MailService mailService;
	@Getter
	private final SmsService smsService;
    @Getter
    private final TemplateService templateService;

	@Getter
	private final TemplateCSSService templateCSSService;
    @Getter
    private final TextService textService;
    private final Properties properties;

    private ImcmsAuthenticatorAndUserAndRoleMapper imcmsAuthenticatorAndUserAndRoleMapper;
    private ExternalizedImcmsAuthenticatorAndUserRegistry externalizedImcmsAuthAndMapper;
    private ApplicationContext applicationContext;
    @Getter
    private TemplateMapper templateMapper;
    @Getter
    private KeyStore keyStore;
    @Getter
    private SystemData systemData;

    @Getter
    @Autowired
    private DelegatingByTypeDocumentService documentService;

    @Getter
    @Autowired
    private CommonContentService commonContentService;

    @Getter
    @Autowired
    private DocumentUrlService documentUrlService;

    @Getter
    @Autowired
    private ImageService imageService;

    @Getter
    @Autowired
    private LoopService loopService;

    @Getter
    @Autowired
    private TextDocumentTemplateService textDocumentTemplateService;

    @Getter
    @Autowired
    private UserService userService;

    @Getter
    @Autowired
    private VersionService versionService;

    @Getter
    @Autowired
    private DocumentRolesService documentRolesService;

    @Getter
    @Autowired
    private UserPropertyService userPropertyService;

    @Getter
    @Autowired
    private DocumentDataService documentDataService;

    @Getter
    @Autowired
    private SearchDocumentService searchDocumentService;

    @Getter
    @Autowired
    private DocumentWasteBasketService documentWasteBasketService;

    @Getter
    @Autowired
    private UserLockValidator userLockValidator;

    @Getter
    @Autowired
    private ImageCompressor imageCompressor;

	@Getter
	@Autowired
	private MultiFactorAuthenticationService multiFactorAuthenticationService;

    @Getter
    private ImageFileIndex imageFileIndex;

    @Autowired
    public DefaultImcmsServices(@Qualifier("databaseWithAutoCommit") Database database,
                                Properties imcmsProperties,
                                LocalizedMessageProvider localizedMessageProvider,
                                CachingFileLoader fileLoader,
                                SmsService smsService, ApplicationContext applicationContext,
                                Config config,
                                DocumentLanguages documentLanguages,
                                DatabaseService databaseService,
                                MailService mailService,
                                TemplateService templateService,
                                DocumentMapper documentMapper,
                                ProcedureExecutor procedureExecutor,
                                LanguageMapper languageMapper,
                                AccessService accessService,
                                MenuService menuService,
                                AuthenticationProvidersService authenticationProvidersService,
                                LanguageService languageService,
                                TemplateCSSService templateCSSService,
                                TextService textService,
                                ImageFileIndex imageFileIndex) {

        this.database = database;
        this.localizedMessageProvider = localizedMessageProvider;
        this.fileLoader = fileLoader;
	    this.smsService = smsService;
	    this.applicationContext = applicationContext;
        this.documentLanguages = documentLanguages;
        this.config = config;
        this.properties = imcmsProperties;
        this.databaseService = databaseService;
        this.mailService = mailService;
        this.templateService = templateService;
        this.procedureExecutor = procedureExecutor;
        this.documentMapper = documentMapper;
        this.languageMapper = languageMapper;
	    this.templateCSSService = templateCSSService;
	    this.textService = textService;

        this.kerberosLoginService = new KerberosLoginService(config);
        this.accessService = accessService;
        this.menuService = menuService;
        this.authenticationProvidersService = authenticationProvidersService;
        this.languageService = languageService;
	    this.imageFileIndex = imageFileIndex;
    }

    @PostConstruct
    private void init() {
        initSso();
        initKeyStore();
        initSysData();
        initSessionCounter();
        initAuthenticatorsAndUserAndRoleMappers(properties);
        initTemplateMapper();
    }

    public CachingFileLoader getFileCache() {
        return fileLoader;
    }

    public RoleGetter getRoleGetter() {
        return imcmsAuthenticatorAndUserAndRoleMapper;
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

    public UserDomainObject verifyUser(String login, String password) {
        UserDomainObject result = null;

        boolean userAuthenticates = externalizedImcmsAuthAndMapper.authenticate(login, password);
        UserDomainObject user = externalizedImcmsAuthAndMapper.getUser(login);

        if (null == user) {
            mainLog.info("->User '" + login + "' failed to log in: User not found.");

        } else if (!user.isActive()) {
            logUserDeactivated(user);

        } else if(userLockValidator.isUserBlocked(user)) {
            mainLog.info("->User " + user.getId() + " failed to log in: User is blocked to login.");

        } else if (!userAuthenticates) {
            mainLog.info("->User " + user.getId() + " failed to log in: Wrong password.");
            final Integer userAttemptsToLogin = userLockValidator.increaseAttempts(user);

            if (userLockValidator.isAmountAttemptsMorePropValue(userAttemptsToLogin)) {
                mainLog.info("->User " + user.getId() + " User has exceeded the norm amount attempts to login.");
                userLockValidator.lockUserForLogin(user.getId());
            }
        } else if (multiFactorAuthenticationService.isRequired(user)) {
	        userLockValidator.unlockingUserForLogin(user);
	        multiFactorAuthenticationService.initSecondFactor(externalizedImcmsAuthAndMapper.getUser(login));
        } else {
            result = user;

            userLockValidator.unlockingUserForLogin(user);

            final Date currentDate = new Date(System.currentTimeMillis());
            userService.updateLastLoginDate(currentDate, user.getId());
            result.setLastLoginDate(currentDate);

            logUserLoggedIn(user);
        }
        return result;
    }

    public UserDomainObject verifyUser(String clientPrincipalName) {
        String login = clientPrincipalName.substring(0, clientPrincipalName.lastIndexOf('@'));

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
    }

    public void incrementSessionCounter() {
        getDatabase().execute(new SqlUpdateCommand(
                "UPDATE sys_data SET value = value + 1 WHERE type_id = 1", new Object[]{}
        ));
    }

    public void updateMainLog(String event) {
        mainLog.info(event);
    }

    public ImcmsAuthenticatorAndUserAndRoleMapper getImcmsAuthenticatorAndUserAndRoleMapper() {
        return imcmsAuthenticatorAndUserAndRoleMapper;
    }

    public String getAdminTemplatePath(String adminTemplateName) {
        return "/" + config.getTemplatePath().getPath() + "/" + Imcms.getUser().getLanguage() + "/admin/"
                + adminTemplateName;
    }

    /**
     * Parse doc replace variables with data, use only for HTML files
     */
    public String getAdminTemplate(String adminTemplateName, UserDomainObject user, List<String> tagsWithReplacements) {
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
        String langPrefix = user.getLanguage();
        return getTemplate(langPrefix + "/" + directory + "/" + adminTemplateName, variables);
    }

    private File getRealContextPath() {
        return Imcms.getPath();
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

    public void setSystemData(SystemData sd) {
        String[] sqlParams;

        sqlParams = new String[]{"" + sd.getStartDocument()};
        getProcedureExecutor().executeUpdateProcedure("StartDocSet", sqlParams);

        database.execute(new SqlUpdateCommand(
                "UPDATE sys_data SET value = ? WHERE type_id = 4", new Object[]{sd.getServerMaster()}
        ));
        database.execute(new SqlUpdateCommand(
                "UPDATE sys_data SET value = ? WHERE type_id = 5", new Object[]{sd.getServerMasterAddress()}
        ));

        database.execute(new SqlUpdateCommand(
                "UPDATE sys_data SET value = ? WHERE type_id = 6", new Object[]{sd.getWebMaster()}
        ));
        database.execute(new SqlUpdateCommand(
                "UPDATE sys_data SET value = ? WHERE type_id = 7", new Object[]{sd.getWebMasterAddress()}
        ));

        sqlParams = new String[]{sd.getSystemMessage()};
        getProcedureExecutor().executeUpdateProcedure("SystemMessageSet", sqlParams);

        /* Update the local copy last, so we stay aware of any database errors */
        this.systemData = sd;
    }

    public void reloadSystemData() {
        initSysData();
    }

    public CategoryMapper getCategoryMapper() {
        return documentMapper.getCategoryMapper();
    }

    public <T> T getManagedBean(Class<T> requiredType) {
        return applicationContext.getBean(requiredType);
    }

    public <T> T getManagedBean(String name, Class<T> requiredType) {
        return applicationContext.getBean(name, requiredType);
    }

    @SuppressWarnings("unchecked")
    private <T> T instantiate(String mapperName) {
        try {
            return (null == mapperName) ? null : (T) Class.forName(mapperName).newInstance();
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
        systemData = getSystemDataFromDb();
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
            return dateFormat.parse(getDatabase().execute(new SqlQueryCommand<>("SELECT value FROM sys_data WHERE type_id = 2", parameters, Utility.SINGLE_STRING_HANDLER)));
        } catch (ParseException ex) {
            log.fatal("Failed to get SessionCounterDate from db.", ex);
            throw new UnhandledException(ex);
        }
    }

    private int getSessionCounterFromDb() {
        final Object[] parameters = new String[0];
        return Integer.parseInt(getDatabase().execute(new SqlQueryCommand<>("SELECT value FROM sys_data WHERE type_id = 1", parameters, Utility.SINGLE_STRING_HANDLER)));
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

            externalAuthenticator = initExternalAuthenticator(externalAuthenticatorName, props);
            externalUserAndRoleRegistry = initExternalUserAndRoleMapper(externalUserAndRoleMapperName, props);

            if (null == externalAuthenticator || null == externalUserAndRoleRegistry) {
                log.error("Failed to initialize both authenticator and user-and-role-documentMapper, using default implementations.");
                externalAuthenticator = null;
                externalUserAndRoleRegistry = null;
            }
        } else if (externalAuthenticatorIsSet || externalUserAndRoleRegistryIsSet) {
            log.error("External authenticator and external user mapper should both be either set or not set. Using default implementation.");

        } else {
            log.info("ExternalAuthenticator not set.");
            log.info("ExternalUserAndRoleMapper not set.");
        }

        // TODO: problem if primary LDAP classes are not instantiated,
        // because of conf error, secondary LDAP also will not be instantiated
        if (externalAuthenticator instanceof LdapUserAndRoleRegistry
                && externalUserAndRoleRegistry instanceof LdapUserAndRoleRegistry) {

            ChainedLdapUserAndRoleRegistry chainedLdapUserAndRoleRegistry = new ChainedLdapUserAndRoleRegistry(
                    externalAuthenticator, externalUserAndRoleRegistry
            );

            initAndAddSecondaryLdapUserAndRoleRegistry(chainedLdapUserAndRoleRegistry, props);

            externalAuthenticator = chainedLdapUserAndRoleRegistry;
            externalUserAndRoleRegistry = chainedLdapUserAndRoleRegistry;
        }

        imcmsAuthenticatorAndUserAndRoleMapper = new ImcmsAuthenticatorAndUserAndRoleMapper(
                this,
                new LoginPasswordManager(StringUtils.trimToNull(config.getLoginPasswordEncryptionSalt()))
        );
        externalizedImcmsAuthAndMapper = new ExternalizedImcmsAuthenticatorAndUserRegistry(
                imcmsAuthenticatorAndUserAndRoleMapper,
                externalAuthenticator,
                externalUserAndRoleRegistry,
                getLanguageMapper().getDefaultLanguage()
        );
        externalizedImcmsAuthAndMapper.synchRolesWithExternal();
        imcmsAuthenticatorAndUserAndRoleMapper.encryptUnencryptedUsersLoginPasswords();
    }

    /**
     * Init and adds secondary LdapUserAndRoleRegistry to the ChainedLdapUserAndRoleRegistry.
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

            boolean externalAuthenticatorIsNotSet = StringUtils.isBlank(externalAuthenticatorName);
            boolean externalUserAndRoleRegistryIsNotSet = StringUtils.isBlank(externalUserAndRoleMapperName);

            if (externalAuthenticatorIsNotSet || externalUserAndRoleRegistryIsNotSet) {
                log.error("Secondary LDAP configuration ignored. External authenticator and external usermapper should both be either set or not set.");
                return;
            }

            log.info("SecondaryExternalAuthenticator: " + externalAuthenticatorName);
            log.info("SecondaryExternalUserAndRoleMapper: " + externalUserAndRoleMapperName);

            externalAuthenticator = initExternalAuthenticator(externalAuthenticatorName, secondaryLdapProperties);
            externalUserAndRoleRegistry = initExternalUserAndRoleMapper(
                    externalUserAndRoleMapperName, secondaryLdapProperties
            );

            if (null == externalAuthenticator || null == externalUserAndRoleRegistry) {
                log.error("Secondary LDAP configuration ignored. Failed to initialize both authenticator and user-and-role-documentMapper.");

            } else if (externalAuthenticator instanceof LdapUserAndRoleRegistry
                    && externalUserAndRoleRegistry instanceof LdapUserAndRoleRegistry) {
                chainedLdapUserAndRoleRegistry.addLink(externalAuthenticator, externalUserAndRoleRegistry);

            } else {
                log.error("Secondary LDAP configuration ignored. Both SecondaryExternalAuthenticator and SecondaryExternalUserAndRoleMapper properties should be set to LDAP.");
            }
        }
    }

    private void logUserDeactivated(UserDomainObject user) {
        mainLog.info("->User " + user.getId() + " failed to log in: User deactivated.");
    }

    private void logUserLoggedIn(UserDomainObject user) {
        if (!user.isDefaultUser()) {
            mainLog.info("->User " + user.getId() + " successfully logged in.");
        }
    }

    private UserAndRoleRegistry initExternalUserAndRoleMapper(String externalUserAndRoleMapperName,
                                                              Properties userAndRoleMapperPropertiesSubset) {
        switch (externalUserAndRoleMapperName.toLowerCase()) {
            case EXTERNAL_USER_AND_ROLE_MAPPER_LDAP:
                return initLdapUserAndRoleRegistry(userAndRoleMapperPropertiesSubset);

            case AzureAuthenticationProvider.EXTERNAL_USER_AND_ROLE_AZURE_AD:
                return initAzureActiveDirectoryUserAndRoleRegistry(userAndRoleMapperPropertiesSubset);

            default:
                return instantiate(externalUserAndRoleMapperName);
        }
    }

    private UserAndRoleRegistry initAzureActiveDirectoryUserAndRoleRegistry(Properties userAndRoleMapperPropertiesSubset) {
        return null;
    }

    private Authenticator initExternalAuthenticator(String externalAuthenticatorName,
                                                    Properties authenticatorPropertiesSubset) {
        switch (externalAuthenticatorName.toLowerCase()) {
            case EXTERNAL_AUTHENTICATOR_LDAP:
                return initLdapUserAndRoleRegistry(authenticatorPropertiesSubset);

            case AzureAuthenticationProvider.EXTERNAL_USER_AND_ROLE_AZURE_AD:
                return initAzureActiveDirectoryAuthenticator(authenticatorPropertiesSubset);

            default:
                return instantiate(externalAuthenticatorName);
        }
    }

    private Authenticator initAzureActiveDirectoryAuthenticator(Properties authenticatorPropertiesSubset) {
        return null;
    }

    private LdapUserAndRoleRegistry initLdapUserAndRoleRegistry(Properties userAndRoleMapperPropertiesSubset) {
        try {
            return new LdapUserAndRoleRegistry(userAndRoleMapperPropertiesSubset);

        } catch (LdapClientException e) {
            log.error("LdapUserAndRoleRegistry could not be created, using default user and role documentMapper.", e);
            return null;
        }
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

            final String templateFolderPath = getTemplateFolderPath();
            final File templateFile = new File(templateFolderPath, path);
            String result = FileUtils.readFileToString(templateFile, Imcms.DEFAULT_ENCODING);

            if (null != variables) {
                result = Parser.parseDoc(result, variables.toArray(new String[0]));
            }
            return result;

        } catch (Exception e) {
            throw new UnhandledException("getTemplate(\"" + path + "\") : " + e.getMessage(), e);
        }
    }

    private String getTemplateFolderPath() throws IOException {
        return new File(Imcms.getPath(), config.getTemplatePath().getPath()).getCanonicalPath();
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

        String startDocument = getDatabase().execute(new SqlQueryCommand<>(
                "SELECT value FROM sys_data WHERE type_id = 0", new String[0], Utility.SINGLE_STRING_HANDLER
        ));
        sd.setStartDocument(StringUtils.isBlank(startDocument) ? ImcmsConstants.DEFAULT_START_DOC_ID : Integer.parseInt(startDocument));

        String systemMessage = getDatabase().execute(new SqlQueryCommand<>(
                "SELECT value FROM sys_data WHERE type_id = 3", new String[0], Utility.SINGLE_STRING_HANDLER
        ));
        sd.setSystemMessage(systemMessage);

        String serverMasterName = getDatabase().execute(new SqlQueryCommand<>(
                "SELECT value FROM sys_data WHERE type_id = 4", new String[0], Utility.SINGLE_STRING_HANDLER
        ));
        sd.setServerMaster(serverMasterName);

        String serverMasterAddress = getDatabase().execute(new SqlQueryCommand<>(
                "SELECT value FROM sys_data WHERE type_id = 5", new String[0], Utility.SINGLE_STRING_HANDLER
        ));
        sd.setServerMasterAddress(serverMasterAddress);

        String webMasterName = getDatabase().execute(new SqlQueryCommand<>(
                "SELECT value FROM sys_data WHERE type_id = 6", new String[0], Utility.SINGLE_STRING_HANDLER
        ));
        sd.setWebMaster(webMasterName);

        String webMasterAddress = getDatabase().execute(new SqlQueryCommand<>(
                "SELECT value FROM sys_data WHERE type_id = 7", new String[0], Utility.SINGLE_STRING_HANDLER
        ));
        sd.setWebMasterAddress(webMasterAddress);

        String userLoginPasswordExpirationInterval = getDatabase().execute(new SqlQueryCommand<>(
                "SELECT value FROM sys_data WHERE type_id = 9", new String[0], Utility.SINGLE_STRING_HANDLER
        ));
        if (userLoginPasswordExpirationInterval == null) {
            log.warn("System property userLoginPasswordResetExpirationInterval is not set; using default");
        } else {
            try {
                int interval = Integer.parseInt(userLoginPasswordExpirationInterval);

                if (interval > 0) {
                    sd.setUserLoginPasswordResetExpirationInterval(interval);
                } else {
                    log.warn(String.format(
                            "System property userLoginPasswordResetExpirationInterval must be '> 0' but set to %d; using default.",
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
