package imcode.server;

import imcode.server.db.*;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentMapper;
import imcode.server.document.TemplateMapper;
import imcode.server.document.textdocument.TextDomainObject;
import imcode.server.parser.ParserParameters;
import imcode.server.parser.TextDocumentParser;
import imcode.server.user.*;
import imcode.util.*;
import imcode.util.fortune.*;
import imcode.util.net.SMTP;
import imcode.util.poll.PollHandlingSystem;
import imcode.util.poll.PollHandlingSystemImpl;
import imcode.util.shop.ShoppingOrderSystem;
import imcode.util.shop.ShoppingOrderSystemImpl;
import org.apache.commons.beanutils.*;
import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.UnhandledException;
import org.apache.log4j.Logger;
import org.apache.log4j.NDC;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.beans.PropertyDescriptor;
import java.io.*;
import java.text.*;
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

    private ImcmsAuthenticatorAndUserAndRoleMapper imcmsAuthenticatorAndUserMapperAndRole;
    private ExternalizedImcmsAuthenticatorAndUserRegistry externalizedImcmsAuthAndMapper = null;
    private DocumentMapper documentMapper;
    private TemplateMapper templateMapper;
    private Map languagePropertiesMap = new HashMap();

    private Map velocityEngines = new TreeMap();

    static {
        mainLog.info( "Main log started." );
    }

    /**
     * Contructs an DefaultImcmsServices object.
     */
    public DefaultImcmsServices( Database database, Properties props ) {
        this.database = database;
        initConfig( props );
        initSysData();
        initSessionCounter();
        initAuthenticatorsAndUserAndRoleMappers( props );
        initDocumentMapper();
        initTemplateMapper();
        initTextDocParser();
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
            return dateFormat.parse( this.sqlProcedureStr( "GetCurrentSessionCounterDate", new String[0] ) );
        } catch ( ParseException ex ) {
            log.fatal( "Failed to get SessionCounterDate from db.", ex );
            throw new UnhandledException( ex );
        }
    }

    private int getSessionCounterFromDb() {
        return Integer.parseInt( this.sqlProcedureStr( "GetCurrentSessionCounter", new String[0] ) );
    }

    private void initDocumentMapper() {
        documentMapper = new DocumentMapper( this );
    }

    private void initTemplateMapper() {
        templateMapper = new TemplateMapper( this );
    }

    private void initAuthenticatorsAndUserAndRoleMappers( Properties props ) {
        String externalAuthenticatorName = props.getProperty( "ExternalAuthenticator" );
        String externalUserAndRoleMapperName = props.getProperty( "ExternalUserAndRoleMapper" );

        Authenticator externalAuthenticator = null;
        UserAndRoleRegistry externalUserAndRoleRegistry = null;

        if ( null != externalAuthenticatorName && null != externalUserAndRoleMapperName ) {
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
        } else if ( null == externalAuthenticatorName && null == externalUserAndRoleMapperName ) {
            log.info( "ExternalAuthenticator not set." );
            log.info( "ExternalUserAndRoleMapper not set." );
        } else {
            log.error( "External authenticator and external usermapper should both be either set or not set. Using default implementation." );
            log.error( "External authenticator and external usermapper should both be either set or not set. Using default implementation." );
        }
        imcmsAuthenticatorAndUserMapperAndRole = new ImcmsAuthenticatorAndUserAndRoleMapper( this, this );
        externalizedImcmsAuthAndMapper =
        new ExternalizedImcmsAuthenticatorAndUserRegistry( imcmsAuthenticatorAndUserMapperAndRole, externalAuthenticator,
                                                           externalUserAndRoleRegistry, getDefaultLanguageAsIso639_2() );
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
        if ( userAuthenticates ) {
            result = user;
            mainLog.info( "->User '" + login + "' successfully logged in." );
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
        sqlUpdateProcedure( "IncSessionCounter", new String[0] );
        sessionCounter = getSessionCounterFromDb();
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

    /**
     * Retrieve a text from the db.
     *
     * @param meta_id The id of the page.
     * @param no      The id of the text in the page.
     * @return The text from the db, or null if there was none.
     */
    public TextDomainObject getText( int meta_id, int no ) {
        return documentMapper.getText( meta_id, no );
    }

    /**
     * Check if url doc.
     */
    public String isUrlDoc( int meta_id ) {
        String url_ref = null;
        if ( DocumentDomainObject.DOCTYPE_ID_URL == getDocType( meta_id ) ) {
            String sqlStr = "select url_ref from url_docs where meta_id = ?";
            url_ref = sqlQueryStr( sqlStr, new String[]{"" + meta_id} );
        }

        return url_ref;
    }

    public void updateMainLog( String event ) {
        mainLog.info( event );
    }

    public String isFramesetDoc( int meta_id ) {

        String htmlStr = null;
        if ( DocumentDomainObject.DOCTYPE_ID_HTML == getDocType( meta_id ) ) {
            String sqlStr = "select frame_set from frameset_docs where meta_id = ?";
            htmlStr = sqlQueryStr( sqlStr, new String[]{"" + meta_id} );
        }
        return htmlStr;

    }

    public String[] sqlQuery( String sqlQuery, String[] parameters ) {
        return database.sqlQuery( sqlQuery, parameters );
    }

    public String sqlQueryStr( String sqlStr, String[] params ) {
        return database.sqlQueryStr( sqlStr, params );
    }

    /**
     * Send a sql update query to the database
     */
    public int sqlUpdateQuery( String sqlStr, String[] params ) {
        return database.sqlUpdateQuery( sqlStr, params );
    }

    /**
     * The preferred way of getting data from the db.
     * String.trim()'s the results.
     *
     * @param procedure The name of the procedure
     * @param params    The parameters of the procedure
     */
    public String[] sqlProcedure( String procedure, String[] params ) {
        return database.sqlProcedure( procedure, params );
    }

    /**
     * The preferred way of getting data to the db.
     *
     * @param procedure The name of the procedure
     * @param params    The parameters of the procedure
     * @return updateCount or -1 if error
     */
    public int sqlUpdateProcedure( String procedure, String[] params ) {
        return database.sqlUpdateProcedure( procedure, params );
    }

    public String sqlProcedureStr( String procedure, String[] params ) {
        return database.sqlProcedureStr( procedure, params );
    }

    public DocumentMapper getDocumentMapper() {
        return documentMapper;
    }

    public TemplateMapper getTemplateMapper() {
        return templateMapper;
    }

    public SMTP getSMTP() {
        return new SMTP( config.getSmtpServer(), config.getSmtpPort() );
    }

    public ImcmsAuthenticatorAndUserAndRoleMapper getImcmsAuthenticatorAndUserAndRoleMapper() {
        return imcmsAuthenticatorAndUserMapperAndRole;
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

    /**
     * Parse doc replace variables with data , use template
     */
    public String getTemplateFromSubDirectoryOfDirectory( String adminTemplateName, UserDomainObject user, List variables,
                                                          String directory, String subDirectory ) {

        if ( null == user ) {
            throw new NullArgumentException( "user" );
        }
        String langPrefix = this.getUserLangPrefixOrDefaultLanguage( user );

        return getTemplate( langPrefix + "/" + directory + "/" + subDirectory
                            + "/"
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

    public Database getDatabase() {
        return database;
    }

    /**
     * @deprecated Ugly use {@link ImcmsServices#getTemplateFromDirectory(String,imcode.server.user.UserDomainObject,java.util.List,String)}
     *             or something else instead.
     */
    public File getExternalTemplateFolder( int meta_id, UserDomainObject user ) {
        int docType = getDocType( meta_id );
        String langPrefix = getUserLangPrefixOrDefaultLanguage( user );
        return new File( config.getTemplatePath(), langPrefix + "/" + docType + "/" );
    }

    /**
     * Return  templatehome.
     */
    public File getTemplatePath() {
        return config.getTemplatePath();
    }

    /**
     * @return file-path to imcmsimages
     *         <p/>
     *         Return file-path to imcmsimages
     *         <p/>
     *         Return file-path to imcmsimages
     *         <p/>
     *         Return file-path to imcmsimages
     *         <p/>
     *         Return file-path to imcmsimages
     */
    // Return file-path to imcmsimages
    public File getImcmsPath() {
        return config.getImcmsPath();
    }

    public String getDefaultLanguageAsIso639_2() {
        return config.getDefaultLanguage();
    }

    public String getLanguagePrefixByLangId( int lang_id ) {
        String lang_prefix = sqlProcedureStr( "GetLangPrefixFromId", new String[]{"" + lang_id} );
        return lang_prefix;
    }

    // get language prefix for user
    public String getUserLangPrefixOrDefaultLanguage( UserDomainObject user ) {
        String lang_prefix = this.getDefaultLanguageAsIso639_2();
        if ( user != null ) {
            return user.getLanguageIso639_2();
        } else {
            return lang_prefix;
        }
    }

    /**
     * Set session counter.
     */
    public synchronized void setSessionCounter( int value ) {
        setSessionCounterInDb( value );
        sessionCounter = getSessionCounterFromDb();
    }

    private void setSessionCounterInDb( int value ) {
        this.sqlUpdateProcedure( "SetSessionCounterValue", new String[]{"" + value} );
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
        this.sqlUpdateProcedure( "SetSessionCounterDate", new String[]{dateFormat.format( date )} );
    }

    /**
     * Get session counter date.
     */
    public Date getSessionCounterDate() {
        return sessionCounterDate;
    }

    public Map sqlProcedureHash( String procedure, String[] params ) {
        return database.sqlProcedureHash( procedure, params );
    }

    /**
     * Send a procedure to the database and return a multi string array
     */
    public String[][] sqlProcedureMulti( String procedure, String[] params ) {
        return database.sqlProcedureMulti( procedure, params );
    }

    public String[][] sqlQueryMulti( String sqlQuery, String[] params ) {
        return database.sqlQueryMulti( sqlQuery, params );
    }

    public void executeTransaction( DatabaseCommand databaseCommand ) {
        database.executeTransaction( databaseCommand );
    }

    /**
     * get doctype
     */
    public int getDocType( int meta_id ) {
        String[] data = sqlProcedure( "GetDocType", new String[]{"" + meta_id} );
        if ( data.length > 0 ) {
            return Integer.parseInt( data[0] );
        } else {
            return 0;
        }
    }

    /**
     * checkDocAdminRights
     */
    public boolean checkDocAdminRights( int meta_id, UserDomainObject user ) {
        return user.canEdit( documentMapper.getDocument( meta_id ) );
    }

    /**
     * Checks to see if a user has any permission of a particular set of permissions for a document.
     *
     * @param meta_id    The document-id
     * @param user       The user
     * @param permission A bitmap containing the permissions.
     */
    public boolean checkDocAdminRightsAny( int meta_id, UserDomainObject user, int permission ) {
        try {
            String[] perms = sqlProcedure( "GetUserPermissionSet",
                                           new String[]{String.valueOf( meta_id ), String.valueOf( user.getId() )} );

            int set_id = Integer.parseInt( perms[0] );
            int set = Integer.parseInt( perms[1] );

            boolean userHasFullPermission = set_id == 0;
            if ( perms.length > 0
                 && userHasFullPermission		// User has full permission for this document
                 || set_id < 3 && ( set & permission ) > 0	// User has at least one of the permissions given.
            ) {
                return true;
            } else {
                return false;
            }
        } catch ( RuntimeException ex ) {
            log.error( "Exception in checkDocAdminRightsAny(int,User,int)", ex );
            throw ex;
        }
    }

    /**
     * Checks to see if a user has a particular set of permissions for a document.
     *
     * @param meta_id    The document-id
     * @param user       The user
     * @param permission A bitmap containing the permissions.
     */
    public boolean checkDocAdminRights( int meta_id, UserDomainObject user, int permission ) {
        try {
            String[] perms = sqlProcedure( "GetUserPermissionSet",
                                           new String[]{String.valueOf( meta_id ), String.valueOf( user.getId() )} );

            if ( perms.length == 0 ) {
                return false;
            }

            int set_id = Integer.parseInt( perms[0] );
            int set = Integer.parseInt( perms[1] );

            if ( set_id == 0		// User has full permission for this document
                 || ( set_id < 3 && ( ( set & permission ) == permission ) )	// User has all the permissions given.
            ) {
                return true;
            } else {
                return false;
            }
        } catch ( RuntimeException ex ) {
            log.error( "Exception in checkDocAdminRights(int,User,int)", ex );
            throw ex;
        }
    }

    public int saveTemplate( String name, String file_name, byte[] template, boolean overwrite, String lang_prefix ) {
        String sqlStr;
        // check if template exists
        sqlStr = "select template_id from templates where simple_name = ?";
        String templateId = sqlQueryStr( sqlStr, new String[]{name} );
        if ( null == templateId ) {

            // get new template_id
            sqlStr = "select max(template_id) + 1 from templates\n";
            templateId = sqlQueryStr( sqlStr, new String[0] );

            sqlStr = "insert into templates values (?,?,?,?,0,0,0)";
            sqlUpdateQuery( sqlStr, new String[]{templateId, file_name, name, lang_prefix} );
        } else { //update
            if ( !overwrite ) {
                return -1;
            }

            sqlStr = "update templates set template_name = ? where template_id = ?";
            sqlUpdateQuery( sqlStr, new String[]{file_name, templateId} );
        }

        File f = new File( config.getTemplatePath(), "text/" + templateId + ".html" );

        try {
            FileOutputStream fw = new FileOutputStream( f );
            fw.write( template );
            fw.flush();
            fw.close();

        } catch ( IOException e ) {
            return -2;
        }

        //  0 = OK
        // -1 = file exist
        // -2 = write error
        return 0;
    }

    /**
     * get demo template
     */
    public Object[] getDemoTemplate( int template_id ) throws IOException {
        //String str = "" ;
        StringBuffer str = new StringBuffer();
        BufferedReader fr = null;
        String suffix = null;
        String[] suffixList =
                {"jpg", "jpeg", "gif", "png", "html", "htm"};

        for ( int i = 0; i < suffixList.length; i++ ) { // Looking for a template with one of six suffixes
            File fileObj = new File( config.getTemplatePath(), "/text/demo/" + template_id + "." + suffixList[i] );
            long date = 0;
            long fileDate = fileObj.lastModified();
            if ( fileObj.exists() && fileDate > date ) {
                // if a template was not properly removed, the template
                // with the most recens modified-date is returned
                try {
                    fr = new BufferedReader( new InputStreamReader( new FileInputStream( fileObj ), "8859_1" ) );
                    suffix = suffixList[i];
                } catch ( IOException e ) {
                    return null; //Could not read
                }
            } // end IF
        } // end FOR

        char[] buffer = new char[4096];
        try {
            int read;
            while ( ( read = fr.read( buffer, 0, 4096 ) ) != -1 ) {
                str.append( buffer, 0, read );
            }
            fr.close();
        } catch ( IOException e ) {
            return null;
        } catch ( NullPointerException e ) {
            return null;
        }

        return new Object[]{suffix, str.toString().getBytes( "8859_1" )}; //return the buffer
    }

    /**
     * get template
     */
    public String getTemplateData( int template_id ) throws IOException {
        return fileCache.getCachedFileString( new File( config.getTemplatePath(), "/text/" + template_id + ".html" ) );
    }

    /**
     * save demo template
     */
    public void saveDemoTemplate( int template_id, byte[] data, String suffix ) throws IOException {

        deleteDemoTemplate( template_id );

        FileOutputStream fw = new FileOutputStream( config.getTemplatePath() + "/text/demo/" + template_id + "."
                                                    + suffix );
        fw.write( data );
        fw.flush();
        fw.close();
    }

    /**
     * get server date
     */
    public Date getCurrentDate() {
        return new Date();
    }

    private final static FileFilter DEMOTEMPLATEFILTER = new FileFilter() {
        public boolean accept( File file ) {
            return file.length() > 0;
        }
    };

    // get demotemplates
    public String[] getDemoTemplateIds() {
        File demoDir = new File( config.getTemplatePath() + "/text/demo/" );

        File[] file_list = demoDir.listFiles( DEMOTEMPLATEFILTER );

        String[] name_list = new String[file_list.length];

        if ( file_list != null ) {
            for ( int i = 0; i < name_list.length; i++ ) {
                String filename = file_list[i].getName();
                int dot = filename.indexOf( "." );
                name_list[i] = dot > -1 ? filename.substring( 0, dot ) : filename;
            }
        } else {
            return new String[0];

        }

        return name_list;

    }

    // delete demotemplate
    public void deleteDemoTemplate( int template_id ) throws IOException {

        File demoTemplateDirectory = new File( new File( config.getTemplatePath(), "text" ), "demo" );
        File[] demoTemplates = demoTemplateDirectory.listFiles();
        for ( int i = 0; i < demoTemplates.length; i++ ) {
            File demoTemplate = demoTemplates[i];
            String demoTemplateFileName = demoTemplate.getName();
            if ( demoTemplateFileName.startsWith( template_id + "." ) ) {
                if ( !demoTemplate.delete() ) {
                    throw new IOException( "fail to deleate" );
                }
            }
        }
    }

    /**
     * Fetch the systemdata from the db
     */
    private SystemData getSystemDataFromDb() {

        /** Fetch everything from the DB */
        String startDocument = sqlProcedureStr( "StartDocGet", new String[0] );
        String[] serverMaster = sqlProcedure( "ServerMasterGet", new String[0] );
        String[] webMaster = sqlProcedure( "WebMasterGet", new String[0] );
        String systemMessage = sqlProcedureStr( "SystemMessageGet", new String[0] );

        /** Create a new SystemData object */
        SystemData sd = new SystemData();

        /** Store everything in the object */

        sd.setStartDocument( startDocument == null ? DEFAULT_STARTDOCUMENT : Integer.parseInt( startDocument ) );
        sd.setSystemMessage( systemMessage );

        if ( serverMaster.length > 0 ) {
            sd.setServerMaster( serverMaster[0] );
            if ( serverMaster.length > 1 ) {
                sd.setServerMasterAddress( serverMaster[1] );
            }
        }
        if ( webMaster.length > 0 ) {
            sd.setWebMaster( webMaster[0] );
            if ( webMaster.length > 1 ) {
                sd.setWebMasterAddress( webMaster[1] );
            }
        }

        return sd;
    }

    public SystemData getSystemData() {
        return sysData;
    }

    public void setSystemData( SystemData sd ) {
        String[] sqlParams;

        sqlParams = new String[]{"" + sd.getStartDocument()};
        sqlUpdateProcedure( "StartDocSet", sqlParams );

        sqlParams = new String[]{sd.getWebMaster(), sd.getWebMasterAddress()};
        sqlUpdateProcedure( "WebMasterSet", sqlParams );

        sqlParams = new String[]{sd.getServerMaster(), sd.getServerMasterAddress()};
        sqlUpdateProcedure( "ServerMasterSet", sqlParams );

        sqlParams = new String[]{sd.getSystemMessage()};
        sqlUpdateProcedure( "SystemMessageSet", sqlParams );

        /* Update the local copy last, so we stay aware of any database errors */
        this.sysData = sd;
    }

    /**
     * Set a user flag
     */
    public void setUserFlag( UserDomainObject user, String flagName ) {
        int userId = user.getId();

        sqlUpdateProcedure( "SetUserFlag", new String[]{"" + userId, flagName} );
    }

    /**
     * Returns an array with with all the documenttypes stored in the database
     * the array consists of pairs of id:, value. Suitable for parsing into select boxes etc.
     */
    public String[][] getAllDocumentTypes( String langPrefixStr ) {
        return sqlProcedureMulti( "GetDocTypes", new String[]{langPrefixStr} );
    }

    /**
     * Unset a user flag
     */
    public void unsetUserFlag( UserDomainObject user, String flagName ) {
        int userId = user.getId();

        sqlUpdateProcedure( "UnsetUserFlag", new String[]{"" + userId, flagName} );
    }

    /**
     * Return a file relative to the fortune-path.
     */
    public String getFortune( String path ) throws IOException {
        return fileCache.getCachedFileString( new File( config.getFortunePath(), path ) );
    }

    /**
     * Get a list of quotes
     *
     * @param quoteListName The name of the quote-List.
     * @return the quote-List.
     */
    public List getQuoteList( String quoteListName ) {
        List theList = new LinkedList();
        try {
            File file = new File( config.getFortunePath(), quoteListName );
            StringReader reader = new StringReader( fileCache.getUncachedFileString( file ) );
            QuoteReader quoteReader = new QuoteReader( reader );
            for ( Quote quote; null != ( quote = quoteReader.readQuote() ); ) {
                theList.add( quote );
            }
            reader.close();
        } catch ( IOException ignored ) {
            log.debug( "Failed to load quote-list " + quoteListName );
        }
        return theList;
    }

    /**
     * Set a quote-list
     *
     * @param quoteListName The name of the quote-List.
     * @param quoteList     The quote-List
     */
    public void setQuoteList( String quoteListName, List quoteList ) throws IOException {
        FileWriter writer = new FileWriter( new File( config.getFortunePath(), quoteListName ) );
        QuoteWriter quoteWriter = new QuoteWriter( writer );
        Iterator quotesIterator = quoteList.iterator();
        while ( quotesIterator.hasNext() ) {
            quoteWriter.writeQuote( (Quote)quotesIterator.next() );
        }
        writer.flush();
        writer.close();
    }

    /**
     * @return a List of Polls
     */
    public List getPollList( String pollListName ) {
        List theList = new LinkedList();
        try {
            File file = new File( config.getFortunePath(), pollListName );
            StringReader reader = new StringReader( fileCache.getUncachedFileString( file ) );
            PollReader pollReader = new PollReader( reader );
            for ( Poll poll; null != ( poll = pollReader.readPoll() ); ) {
                theList.add( poll );
            }
            reader.close();
        } catch ( IOException ignored ) {
            log.debug( "Failed to load poll-list " + pollListName );
        }
        return theList;
    }

    /**
     * Set a poll-list
     *
     * @param pollListName The name of the poll-List.
     * @param pollList     The poll-List
     */
    public void setPollList( String pollListName, List pollList ) throws IOException {
        FileWriter writer = new FileWriter( new File( config.getFortunePath(), pollListName ) );
        PollWriter pollWriter = new PollWriter( writer );
        Iterator pollIterator = pollList.iterator();
        while ( pollIterator.hasNext() ) {
            pollWriter.writePoll( (Poll)pollIterator.next() );
        }
        writer.flush();
        writer.close();
    }

    /**
     * Get all possible userflags
     */
    public Map getUserFlags() {
        String[] dbData = sqlProcedure( "GetUserFlags", new String[0] );

        return getUserFlags( dbData );
    }

    /**
     * Get all userflags for a single user
     */
    public Map getUserFlags( UserDomainObject user ) {
        int userId = user.getId();
        String[] dbData = sqlProcedure( "GetUserFlagsForUser", new String[]{String.valueOf( userId )} );

        return getUserFlags( dbData );
    }

    /**
     * Get all userflags of a single type
     */
    public Map getUserFlags( int type ) {
        String[] dbData = sqlProcedure( "GetUserFlagsOfType", new String[]{String.valueOf( type )} );

        return getUserFlags( dbData );
    }

    /**
     * Get all userflags for a single user of a single type
     */
    public Map getUserFlags( UserDomainObject user, int type ) {
        int userId = user.getId();
        String[] dbData = sqlProcedure( "GetUserFlagsForUserOfType",
                                        new String[]{String.valueOf( userId ), String.valueOf( type )} );

        return getUserFlags( dbData );
    }

    /**
     * Used by the other getUserFlags*-methods to put the database-data in a Set *
     */
    private Map getUserFlags( String[] dbData ) {
        Map theFlags = new HashMap();

        for ( int i = 0; i < dbData.length; i += 4 ) {
            String flagName = dbData[i + 1];
            int flagType = Integer.parseInt( dbData[i + 2] );
            String flagDescription = dbData[i + 3];

            UserFlag flag = new UserFlag();
            flag.setName( flagName );
            flag.setType( flagType );
            flag.setDescription( flagDescription );

            theFlags.put( flagName, flag );
        }
        return theFlags;
    }

    public PollHandlingSystem getPollHandlingSystem() {
        return new PollHandlingSystemImpl( this );
    }

    public ShoppingOrderSystem getShoppingOrderSystem() {
        return new ShoppingOrderSystemImpl( this );
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

    private static class WebappRelativeFileConverter implements Converter {

        public Object convert( Class type, Object value ) {
            return FileUtility.getFileFromWebappRelativePath( (String)value );
        }
    }
}
