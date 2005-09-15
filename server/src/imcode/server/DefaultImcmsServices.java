package imcode.server;

import com.imcode.db.Database;
import com.imcode.db.DatasourceDatabase;
import com.imcode.db.commands.SqlUpdateDatabaseCommand;
import com.imcode.imcms.db.DatabaseUtils;
import com.imcode.imcms.db.DefaultProcedureExecutor;
import com.imcode.imcms.db.ProcedureExecutor;
import com.imcode.imcms.db.StringArrayArrayResultSetHandler;
import com.imcode.imcms.mapping.CategoryMapper;
import com.imcode.imcms.mapping.DatabaseDocumentGetter;
import com.imcode.imcms.mapping.DefaultDocumentMapper;
import com.imcode.imcms.mapping.DocumentPermissionSetMapper;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentTypeDomainObject;
import imcode.server.document.TemplateMapper;
import imcode.server.document.index.AutorebuildingDirectoryIndex;
import imcode.server.document.index.DocumentIndex;
import imcode.server.parser.ParserParameters;
import imcode.server.parser.TextDocumentParser;
import imcode.server.user.*;
import imcode.util.Clock;
import imcode.util.DateConstants;
import imcode.util.FileCache;
import imcode.util.Parser;
import imcode.util.Prefs;
import imcode.util.io.FileUtility;
import imcode.util.net.SMTP;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.UnhandledException;
import org.apache.log4j.Logger;
import org.apache.log4j.NDC;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import javax.sql.DataSource;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.text.Collator;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

final public class DefaultImcmsServices implements ImcmsServices {

    private final Database database;
    private TextDocumentParser textDocParser;
    private Config config;

    private static final int DEFAULT_STARTDOCUMENT = 1001;

    private SystemData sysData;

    private Date sessionCounterDate;
    private int sessionCounter = 0;
    private FileCache fileCache = new FileCache();

    private final static Logger mainLog = Logger.getLogger( ImcmsConstants.MAIN_LOG );

    private final static Logger log = Logger.getLogger( DefaultImcmsServices.class.getName() );

    private static final String EXTERNAL_AUTHENTICATOR_LDAP = "LDAP";
    private static final String EXTERNAL_USER_AND_ROLE_MAPPER_LDAP = "LDAP";

    private ImcmsAuthenticatorAndUserAndRoleMapper imcmsAuthenticatorAndUserAndRoleMapper;
    private ExternalizedImcmsAuthenticatorAndUserRegistry externalizedImcmsAuthAndMapper;
    private DefaultDocumentMapper documentMapper;
    private TemplateMapper templateMapper;
    private Map languagePropertiesMap = new HashMap();
    private KeyStore keyStore;

    private Map velocityEngines = new TreeMap();
    private CategoryMapper categoryMapper;
    private LanguageMapper languageMapper;
    private ProcedureExecutor procedureExecutor;

    static {
        mainLog.info( "Main log started." );
    }

    /**
     * Contructs an DefaultImcmsServices object.
     */
    public DefaultImcmsServices(DataSource dataSource, Properties props) {
        database = new DatasourceDatabase(dataSource);
        procedureExecutor = new DefaultProcedureExecutor(database);
        initConfig( props );
        initKeyStore();
        initSysData();
        initSessionCounter();
        languageMapper = new LanguageMapper(database, config.getDefaultLanguage());
        categoryMapper = new CategoryMapper(getDatabase());
        initAuthenticatorsAndUserAndRoleMappers( props );
        initDocumentMapper();
        initTemplateMapper();
        initTextDocParser();
    }

    private void initKeyStore() {
        String keyStoreType = config.getKeyStoreType();
        if (StringUtils.isBlank( keyStoreType ) ) {
            keyStoreType = KeyStore.getDefaultType() ;
        }
        try {
            keyStore = KeyStore.getInstance( keyStoreType ) ;
            keyStore.load( null, null );
        } catch ( GeneralSecurityException e ) {
            throw new UnhandledException( e );
        } catch ( IOException e ) {
            throw new UnhandledException( e );
        }
        String keyStorePath = config.getKeyStorePath();
        if ( StringUtils.isNotBlank( keyStorePath ) ) {
            File keyStoreFile = FileUtility.getFileFromWebappRelativePath( keyStorePath ) ;
            try {
                keyStore.load( new FileInputStream( keyStoreFile ), null );
            } catch ( Exception e ) {
                log.error( "Failed to load keystore from path " + keyStoreFile, e );
            }
        }
    }

    private void initTextDocParser() {
        textDocParser = new TextDocumentParser( this );
    }

    private void initSysData() {
        sysData = getSystemDataFromDb();
    }

    private void initConfig( Properties props ) {
        this.config = createConfigFromProperties( props );
    }

    private static Config createConfigFromProperties( Properties props ) {
        Config config = new Config();
        ConvertUtils.register( new WebappRelativeFileConverter(), File.class );
        PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors( config );
        for ( int i = 0; i < propertyDescriptors.length; i++ ) {
            PropertyDescriptor propertyDescriptor = propertyDescriptors[i];
            if ( null == propertyDescriptor.getWriteMethod() ) {
                continue;
            }
            String uncapitalizedPropertyName = propertyDescriptor.getName();
            String capitalizedPropertyName = StringUtils.capitalize( uncapitalizedPropertyName );
            String propertyValue = props.getProperty( capitalizedPropertyName );
            if ( null != propertyValue ) {
                try {
                    BeanUtils.setProperty( config, uncapitalizedPropertyName, propertyValue );
                } catch ( Exception e ) {
                    log.error( "Failed to set property " + capitalizedPropertyName, e.getCause() );
                    continue;
                }
            }
            try {
                String setPropertyValue = BeanUtils.getProperty( config, uncapitalizedPropertyName );
                if ( null != setPropertyValue ) {
                    log.info( capitalizedPropertyName + " = " + setPropertyValue );
                } else {
                    log.warn( capitalizedPropertyName + " not set." );
                }
            } catch ( Exception e ) {
                log.error( e, e );
            }
        }
        return config;
    }

    private void initSessionCounter() {
        try {
            sessionCounter = getSessionCounterFromDb();
            sessionCounterDate = getSessionCounterDateFromDb();
        } catch ( NumberFormatException ex ) {
            log.fatal( "Failed to get SessionCounter from db.", ex );
            throw ex;
        }

        log.info( "SessionCounter: " + sessionCounter );
        log.info( "SessionCounterDate: " + sessionCounterDate );
    }

    private Date getSessionCounterDateFromDb() {
        try {
            DateFormat dateFormat = new SimpleDateFormat( DateConstants.DATE_FORMAT_STRING );
            final Object[] parameters = new String[0];
            return dateFormat.parse( DatabaseUtils.executeStringQuery(getDatabase(), "SELECT value FROM sys_data WHERE type_id = 2", parameters) );
        } catch ( ParseException ex ) {
            log.fatal( "Failed to get SessionCounterDate from db.", ex );
            throw new UnhandledException( ex );
        }
    }

    private int getSessionCounterFromDb() {
        final Object[] parameters = new String[0];
        return Integer.parseInt( DatabaseUtils.executeStringQuery(getDatabase(), "SELECT value FROM sys_data WHERE type_id = 1", parameters) );
    }

    private void initDocumentMapper() {
        File indexDirectory = new File( getRealContextPath(), "WEB-INF/index" );
        DocumentIndex documentIndex = new AutorebuildingDirectoryIndex( indexDirectory, getConfig().getIndexingSchedulePeriodInMinutes() );
        documentMapper = new DefaultDocumentMapper( this, this.getDatabase(), new DatabaseDocumentGetter(this.getDatabase(), this), new DocumentPermissionSetMapper( database, this ), documentIndex, this.getClock(), this.getConfig(), categoryMapper);
    }

    private void initTemplateMapper() {
        templateMapper = new TemplateMapper( this );
    }

    private void initAuthenticatorsAndUserAndRoleMappers( Properties props ) {
        String externalAuthenticatorName = props.getProperty( "ExternalAuthenticator" );
        String externalUserAndRoleMapperName = props.getProperty( "ExternalUserAndRoleMapper" );

        Authenticator externalAuthenticator = null;
        UserAndRoleRegistry externalUserAndRoleRegistry = null;

        boolean externalAuthenticatorIsSet = StringUtils.isNotBlank( externalAuthenticatorName );
        boolean externalUserAndRoleRegistryIsSet = StringUtils.isNotBlank( externalUserAndRoleMapperName );
        if ( externalAuthenticatorIsSet && externalUserAndRoleRegistryIsSet ) {
            log.info( "ExternalAuthenticator: " + externalAuthenticatorName );
            log.info( "ExternalUserAndRoleMapper: " + externalUserAndRoleMapperName );
            externalAuthenticator =
            initExternalAuthenticator( externalAuthenticatorName, props );
            externalUserAndRoleRegistry =
            initExternalUserAndRoleMapper( externalUserAndRoleMapperName, props );
            if ( null == externalAuthenticator || null == externalUserAndRoleRegistry ) {
                log.error( "Failed to initialize both authenticator and user-and-role-documentMapper, using default implementations." );
                externalAuthenticator = null;
                externalUserAndRoleRegistry = null;
            }
        } else if ( !externalAuthenticatorIsSet && !externalUserAndRoleRegistryIsSet ) {
            log.info( "ExternalAuthenticator not set." );
            log.info( "ExternalUserAndRoleMapper not set." );
        } else {
            log.error( "External authenticator and external usermapper should both be either set or not set. Using default implementation." );
            log.error( "External authenticator and external usermapper should both be either set or not set. Using default implementation." );
        }
        imcmsAuthenticatorAndUserAndRoleMapper = new ImcmsAuthenticatorAndUserAndRoleMapper(this);
        externalizedImcmsAuthAndMapper =
        new ExternalizedImcmsAuthenticatorAndUserRegistry( imcmsAuthenticatorAndUserAndRoleMapper, externalAuthenticator,
                                                           externalUserAndRoleRegistry, getLanguageMapper().getDefaultLanguage() );
        externalizedImcmsAuthAndMapper.synchRolesWithExternal();
    }

    public synchronized int getSessionCounter() {
        return sessionCounter;
    }

    public String getSessionCounterDateAsString() {
        DateFormat dateFormat = new SimpleDateFormat( DateConstants.DATE_FORMAT_STRING );

        return dateFormat.format( sessionCounterDate );
    }

    public UserDomainObject verifyUser( String login, String password ) {
        NDC.push( "verifyUser" );
        UserDomainObject result = null;

        boolean userAuthenticates = externalizedImcmsAuthAndMapper.authenticate( login, password );
        UserDomainObject user = externalizedImcmsAuthAndMapper.getUser( login );
        if ( userAuthenticates  ) {
            result = user;
            if ( !user.isDefaultUser() ) {
                mainLog.info( "->User '" + login + "' successfully logged in." );
            }
        } else if ( null == user ) {
            mainLog.info( "->User '" + login + "' failed to log in: User not found." );
        } else if ( !user.isActive() ) {
            mainLog.info( "->User '" + login + "' failed to log in: User deactivated." );
        } else {
            mainLog.info( "->User '" + login + "' failed to log in: Wrong password." );
        }

        NDC.pop();
        return result;
    }

    public synchronized void incrementSessionCounter() {
        sessionCounter++ ;
        final Object[] parameters = new String[] {""
                                                  + sessionCounter};
        DatabaseUtils.executeUpdate(getDatabase(), "UPDATE sys_data SET value = ? WHERE type_id = 1", parameters);
    }

    private UserAndRoleRegistry initExternalUserAndRoleMapper( String externalUserAndRoleMapperName,
                                                               Properties userAndRoleMapperPropertiesSubset ) {
        UserAndRoleRegistry externalUserAndRoleRegistry = null;
        if ( null == externalUserAndRoleMapperName ) {
            externalUserAndRoleRegistry = null;
        } else if ( EXTERNAL_USER_AND_ROLE_MAPPER_LDAP.equalsIgnoreCase( externalUserAndRoleMapperName ) ) {
            try {
                externalUserAndRoleRegistry = new LdapUserAndRoleRegistry( userAndRoleMapperPropertiesSubset );
            } catch ( LdapUserAndRoleRegistry.LdapInitException e ) {
                log.error( "LdapUserAndRoleRegistry could not be created, using default user and role documentMapper.",
                           e );
            }
        } else {
            externalUserAndRoleRegistry = (UserAndRoleRegistry)createInstanceOfClass( externalUserAndRoleMapperName );
        }
        return externalUserAndRoleRegistry;
    }

    private Authenticator initExternalAuthenticator( String externalAuthenticatorName,
                                                     Properties authenticatorPropertiesSubset ) {
        Authenticator externalAuthenticator = null;
        try {
            if ( null == externalAuthenticatorName ) {
                externalAuthenticator = null;
            } else if ( EXTERNAL_AUTHENTICATOR_LDAP.equalsIgnoreCase( externalAuthenticatorName ) ) {
                try {
                    externalAuthenticator = new LdapUserAndRoleRegistry( authenticatorPropertiesSubset );
                } catch ( LdapUserAndRoleRegistry.LdapInitException e ) {
                    log.error( "LdapUserAndRoleRegistry could not be created, using default user and role documentMapper.",
                               e );
                }
            } else {
                externalAuthenticator = (Authenticator)createInstanceOfClass( externalAuthenticatorName );
            }
        } catch ( Exception e ) {
            log.error( "Failed to initialize external authenticator.", e );
        }
        return externalAuthenticator;
    }

    private static Object createInstanceOfClass( String className ) {
        Object instance = null;
        try {
            instance = Class.forName( className ).newInstance();
        } catch ( Exception e ) {
            log.error( "Could not create instance of class '" + className + "'.", e );
        }
        return instance;
    }

    public String parsePage( ParserParameters paramsToParse )
            throws IOException {
        return textDocParser.parsePage( paramsToParse );
    }

    public void updateMainLog( String event ) {
        mainLog.info( event );
    }

    public String getHtmlDocumentData( int meta_id ) {

        String htmlStr = null;
        if ( DocumentTypeDomainObject.HTML_ID == getDocType( meta_id ) ) {
            String sqlStr = "select frame_set from frameset_docs where meta_id = ?";
            final Object[] parameters = new String[] {"" + meta_id};
            htmlStr = DatabaseUtils.executeStringQuery(getDatabase(), sqlStr, parameters);
        }
        return htmlStr;

    }

    public DefaultDocumentMapper getDefaultDocumentMapper() {
        return documentMapper;
    }

    public TemplateMapper getTemplateMapper() {
        return templateMapper;
    }

    public SMTP getSMTP() {
        return new SMTP( config.getSmtpServer(), config.getSmtpPort() );
    }

    public ImcmsAuthenticatorAndUserAndRoleMapper getImcmsAuthenticatorAndUserAndRoleMapper() {
        return imcmsAuthenticatorAndUserAndRoleMapper;
    }

    /**
     * Parse doc replace variables with data , use template
     */
    public String getAdminTemplate( String adminTemplateName, UserDomainObject user,
                                    List tagsWithReplacements ) {
        return getTemplateFromDirectory( adminTemplateName, user, tagsWithReplacements, "admin" );
    }

    /**
     * Parse doc replace variables with data , use template
     */
    public String getTemplateFromDirectory( String adminTemplateName, UserDomainObject user, List variables,
                                            String directory ) {
        if ( null == user ) {
            throw new NullArgumentException( "user" );
        }
        String langPrefix = user.getLanguageIso639_2();
        return getTemplate( langPrefix + "/" + directory + "/"
                            + adminTemplateName, user, variables );
    }

    private String getTemplate( String path, UserDomainObject user, List variables ) {
        try {
            VelocityEngine velocity = getVelocityEngine( user );
            VelocityContext context = getVelocityContext( user );
            if ( null != variables ) {
                List parseDocVariables = new ArrayList( variables.size() );
                for ( Iterator iterator = variables.iterator(); iterator.hasNext(); ) {
                    String key = (String)iterator.next();
                    Object value = iterator.next();
                    context.put( key, value );
                    boolean isVelocityVariable = StringUtils.isAlpha( key ) || !( value instanceof String );
                    if ( !isVelocityVariable ) {
                        parseDocVariables.add( key );
                        parseDocVariables.add( value );
                    }
                }
                variables = parseDocVariables;
            }
            StringWriter stringWriter = new StringWriter();
            velocity.mergeTemplate( path, WebAppGlobalConstants.DEFAULT_ENCODING_WINDOWS_1252, context, stringWriter );
            String result = stringWriter.toString();
            if ( null != variables ) {
                result = Parser.parseDoc( result, (String[])variables.toArray( new String[variables.size()] ) );
            }
            return result;
        } catch ( Exception e ) {
            throw new UnhandledException( "getTemplate(\"" + path + "\") : " + e.getMessage(), e );
        }
    }

    private synchronized VelocityEngine createVelocityEngine( String languageIso639_2 ) throws Exception {
        VelocityEngine velocity = new VelocityEngine();
        velocity.setProperty( VelocityEngine.FILE_RESOURCE_LOADER_PATH, config.getTemplatePath().getCanonicalPath() );
        velocity.setProperty( VelocityEngine.VM_LIBRARY, languageIso639_2 + "/gui.vm" );
        velocity.setProperty( VelocityEngine.VM_LIBRARY_AUTORELOAD, "true" );
        velocity.setProperty( VelocityEngine.RUNTIME_LOG_LOGSYSTEM_CLASS, "org.apache.velocity.runtime.log.SimpleLog4JLogSystem" );
        velocity.setProperty( "runtime.log.logsystem.log4j.category", "org.apache.velocity" );
        velocity.init();
        return velocity;
    }

    public VelocityEngine getVelocityEngine( UserDomainObject user ) {
        try {
            String languageIso639_2 = user.getLanguageIso639_2();
            VelocityEngine velocityEngine = (VelocityEngine)velocityEngines.get( languageIso639_2 );
            if ( velocityEngine == null ) {
                velocityEngine = createVelocityEngine( languageIso639_2 );
                velocityEngines.put( languageIso639_2, velocityEngine );
            }
            return velocityEngine;
        } catch ( Exception e ) {
            throw new UnhandledException( e );
        }
    }

    public VelocityContext getVelocityContext( UserDomainObject user ) {
        VelocityContext context = new VelocityContext();
        // FIXME: This method needs an HttpServletRequest in, to get the context path from
        context.put( "contextPath", user.getCurrentContextPath() );
        context.put( "language", user.getLanguageIso639_2() );
        return context;
    }

    public Config getConfig() {
        return config;
    }

    private Clock getClock() {
        return this;
    }

    public File getRealContextPath() {
        return WebAppGlobalConstants.getInstance().getAbsoluteWebAppPath();
    }

    public KeyStore getKeyStore() {
        return keyStore;
    }

    /**
     * Set session counter.
     */
    public synchronized void setSessionCounter( int value ) {
        setSessionCounterInDb( value );
        sessionCounter = getSessionCounterFromDb();
    }

    private void setSessionCounterInDb( int value ) {
        final Object[] parameters = new String[] {""
                                                  + value};
        getProcedureExecutor().executeUpdateProcedure("SetSessionCounterValue", parameters);
    }

    /**
     * Set session counter date.
     */
    public void setSessionCounterDate( Date date ) {
        setSessionCounterDateInDb( date );
        sessionCounterDate = getSessionCounterDateFromDb();
    }

    private void setSessionCounterDateInDb( Date date ) {
        DateFormat dateFormat = new SimpleDateFormat( DateConstants.DATE_FORMAT_STRING );
        final Object[] parameters = new String[] {dateFormat.format( date )};
        getProcedureExecutor().executeUpdateProcedure("SetSessionCounterDate", parameters);
    }

    /**
     * Get session counter date.
     */
    public Date getSessionCounterDate() {
        return sessionCounterDate;
    }

    /**
     * get doctype
     */
    public int getDocType( int meta_id ) {
        DocumentDomainObject document = documentMapper.getDocument( meta_id );
        if ( null != document ) {
            return document.getDocumentTypeId();
        } else {
            return 0;
        }
    }

    public Date getCurrentDate() {
        return new Date();
    }

    private SystemData getSystemDataFromDb() {

        SystemData sd = new SystemData();

        final Object[] parameters5 = new String[0];
        String startDocument = DatabaseUtils.executeStringQuery(getDatabase(), "SELECT value FROM sys_data WHERE sys_id = 0", parameters5);
        sd.setStartDocument( startDocument == null ? DEFAULT_STARTDOCUMENT : Integer.parseInt( startDocument ) );

        final Object[] parameters4 = new String[0];
        String systemMessage = DatabaseUtils.executeStringQuery(getDatabase(), "SELECT value FROM sys_data WHERE type_id = 3", parameters4);
        sd.setSystemMessage( systemMessage );

        final Object[] parameters3 = new String[0];
        String serverMasterName = DatabaseUtils.executeStringQuery(getDatabase(), "SELECT value FROM sys_data WHERE type_id = 4", parameters3);
        sd.setServerMaster( serverMasterName );

        final Object[] parameters2 = new String[0];
        String serverMasterAddress = DatabaseUtils.executeStringQuery(getDatabase(), "SELECT value FROM sys_data WHERE type_id = 5", parameters2);
        sd.setServerMasterAddress( serverMasterAddress );

        final Object[] parameters1 = new String[0];
        String webMasterName = DatabaseUtils.executeStringQuery(getDatabase(), "SELECT value FROM sys_data WHERE type_id = 6", parameters1);
        sd.setWebMaster( webMasterName );

        final Object[] parameters = new String[0];
        String webMasterAddress = DatabaseUtils.executeStringQuery(getDatabase(), "SELECT value FROM sys_data WHERE type_id = 7", parameters);
        sd.setWebMasterAddress( webMasterAddress );

        return sd;
    }

    public SystemData getSystemData() {
        return sysData;
    }

    public void setSystemData( SystemData sd ) {
        String[] sqlParams;

        sqlParams = new String[]{"" + sd.getStartDocument()};
        getProcedureExecutor().executeUpdateProcedure("StartDocSet", sqlParams);

        database.executeCommand(new SqlUpdateDatabaseCommand("UPDATE sys_data SET value = ? WHERE type_id = 4", new Object[] {sd.getServerMaster()})) ;
        database.executeCommand(new SqlUpdateDatabaseCommand("UPDATE sys_data SET value = ? WHERE type_id = 5", new Object[] {sd.getServerMasterAddress()})) ;

        database.executeCommand(new SqlUpdateDatabaseCommand("UPDATE sys_data SET value = ? WHERE type_id = 6", new Object[] {sd.getWebMaster()})) ;
        database.executeCommand(new SqlUpdateDatabaseCommand("UPDATE sys_data SET value = ? WHERE type_id = 7", new Object[] {sd.getWebMasterAddress()})) ;

        sqlParams = new String[]{sd.getSystemMessage()};
        getProcedureExecutor().executeUpdateProcedure("SystemMessageSet", sqlParams);

        /* Update the local copy last, so we stay aware of any database errors */
        this.sysData = sd;
    }

    /**
     * Returns an array with with all the documenttypes stored in the database
     * the array consists of pairs of id:, value. Suitable for parsing into select boxes etc.
     */
    public String[][] getAllDocumentTypes( String langPrefixStr ) {
        final Object[] parameters = new String[] {langPrefixStr};
        return (String[][]) getProcedureExecutor().executeProcedure("GetDocTypes", parameters, new StringArrayArrayResultSetHandler());
    }

    public Properties getLanguageProperties( UserDomainObject user ) {
        String languageIso639_2 = user.getLanguageIso639_2();
        Properties languageProperties = (Properties)languagePropertiesMap.get( languageIso639_2 );
        if ( null == languageProperties ) {
            String propertiesFilename = languageIso639_2 + ".properties";
            try {
                languageProperties = Prefs.getProperties( propertiesFilename );
                languagePropertiesMap.put( languageIso639_2, languageProperties );
            } catch ( IOException e ) {
                log.fatal( "Failed to read language properties from " + propertiesFilename, e );
                throw new UnhandledException( e );
            }
        }
        return languageProperties;
    }

    public File getIncludePath() {
        return config.getIncludePath();
    }

    public Collator getDefaultLanguageCollator() {
        try {
            return Collator.getInstance( new Locale( LanguageMapper.convert639_2to639_1( config.getDefaultLanguage() ) ) );
        } catch ( LanguageMapper.LanguageNotSupportedException e ) {
            throw new RuntimeException( e );
        }
    }

    public Database getDatabase() {
        return database;
    }

    public CategoryMapper getCategoryMapper() {
        return categoryMapper;
    }

    public LanguageMapper getLanguageMapper() {
        return this.languageMapper ;
    }

    public FileCache getFileCache() {
        return fileCache ;
    }

    public RoleGetter getRoleGetter() {
        return imcmsAuthenticatorAndUserAndRoleMapper;
    }

    public ProcedureExecutor getProcedureExecutor() {
        return procedureExecutor;
    }

    private static class WebappRelativeFileConverter implements Converter {

        public Object convert( Class type, Object value ) {
            return FileUtility.getFileFromWebappRelativePath( (String)value );
        }
    }
}
