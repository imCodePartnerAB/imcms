package imcode.server;

import imcode.server.db.ConnectionPool;
import imcode.server.db.SqlHelpers;
import imcode.server.document.*;
import imcode.server.document.textdocument.TextDomainObject;
import imcode.server.document.textdocument.ImageDomainObject;
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
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.NullArgumentException;
import org.apache.log4j.Logger;
import org.apache.log4j.NDC;
import org.apache.oro.text.perl.Perl5Util;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.*;
import java.text.Collator;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

final public class IMCService implements IMCServiceInterface {

    public ConnectionPool getConnectionPool() {
        return m_conPool;
    }

    private final ConnectionPool m_conPool;
    private TextDocumentParser textDocParser;

    private File templatePath;           // template home

    private File includePath;
    private File fortunePath;
    private File imcmsPath;             //  folder  /imcms
    private File filePath;
    private String imageUrl;            //  folder  /images
    private String defaultLanguageAsIso639_2;
    private static final int DEFAULT_STARTDOCUMENT = 1001;

    private String smtpServer;
    private int smtpPort;

    private SystemData sysData;

    private Date sessionCounterDate;
    private int sessionCounter = 0;
    private FileCache fileCache = new FileCache();

    private final static Logger mainLog = Logger.getLogger( IMCConstants.MAIN_LOG );
    private final static Logger log = Logger.getLogger( IMCService.class.getName() );
    private static final String EXTERNAL_AUTHENTICATOR_SMB = "SMB";
    private static final String EXTERNAL_AUTHENTICATOR_LDAP = "LDAP";
    private static final String EXTERNAL_USER_AND_ROLE_MAPPER_LDAP = "LDAP";
    private ImcmsAuthenticatorAndUserMapper imcmsAuthenticatorAndUserMapper;
    private ExternalizedImcmsAuthenticatorAndUserMapper externalizedImcmsAuthAndMapper = null;
    private DocumentMapper documentMapper;
    private TemplateMapper templateMapper;
    private Properties langproperties_swe;
    private Properties langproperties_eng;

    private Map velocityEngines = new TreeMap() ;

    static {
        mainLog.info( "Main log started." );
    }

    /**
     * Contructs an IMCService object.
     */
    public IMCService( ConnectionPool conPool, Properties props ) {
        m_conPool = conPool;
        initMemberFields( props );
        initAuthenticatorsAndUserAndRoleMappers( props );
        initDocumentMapper();
        initTemplateMapper();
    }

    private void initMemberFields( Properties props ) {
        sysData = getSystemDataFromDb();

        templatePath = getFilePropertyAndLogIt( props, "TemplatePath" );
        includePath = getFilePropertyAndLogIt( props, "IncludePath" );
        fortunePath = getFilePropertyAndLogIt( props, "FortunePath" );
        filePath = getFilePropertyAndLogIt( props, "FilePath" );
        imcmsPath = getFilePropertyAndLogIt( props, "ImcmsPath" );

        imageUrl = getPropertyAndLogIt( props, "ImageUrl" );
        smtpServer = getPropertyAndLogIt( props, "SmtpServer" );
        smtpPort = getIntPropertyAndLogIt( props, "SmtpPort", 25 );

        defaultLanguageAsIso639_2 = props.getProperty( "DefaultLanguage" ).trim(); //FIXME: Get from DB
        try {
            if ( defaultLanguageAsIso639_2.length() < 3 ) {
                defaultLanguageAsIso639_2 = LanguageMapper.convert639_1to639_2( defaultLanguageAsIso639_2 );
            }
        } catch ( LanguageMapper.LanguageNotSupportedException e1 ) {
            log.fatal( "Configured default language " + defaultLanguageAsIso639_2 + " is not supported either." );
            defaultLanguageAsIso639_2 = null;
        }
        log.info( "DefaultLanguage: " + defaultLanguageAsIso639_2 );

        initSessionCounter();

        textDocParser = new TextDocumentParser( this );

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
            throw new RuntimeException( ex );
        }
    }

    private int getSessionCounterFromDb() {
        return Integer.parseInt( this.sqlProcedureStr( "GetCurrentSessionCounter", new String[0] ) );
    }

    private void initLangProperties( String LanguageIso639_2 ) {

        if ( "swe".equals( LanguageIso639_2 ) ) {
            try {
                langproperties_swe = Prefs.getProperties( "swe.properties" );
            } catch ( IOException e ) {
                log.fatal( "Failed to initialize swe.properties", e );
            }
        }
        if ( "eng".equals( LanguageIso639_2 ) ) {
            try {
                langproperties_eng = Prefs.getProperties( "eng.properties" );
            } catch ( IOException e ) {
                log.fatal( "Failed to initialize eng.properties", e );
            }
        }

    }

    private int getIntPropertyAndLogIt( Properties props, final String property, int defaultValue ) {
        final String propertyValueString = props.getProperty( property ).trim();
        int result = defaultValue;
        try {
            result = Integer.parseInt( propertyValueString );
        } catch ( NumberFormatException nfe ) {
            log.warn( "Illegal value for " + property + ": " + propertyValueString + ". Using default: "
                      + defaultValue );
        }
        log.info( property + ": " + result );
        return result;
    }

    private String getPropertyAndLogIt( Properties props, final String property ) {
        String propertyValue = props.getProperty( property ).trim();
        log.info( property + ": " + propertyValue );
        return propertyValue;
    }

    private File getFilePropertyAndLogIt( Properties props, final String pathProperty ) {
        String templatePathString = props.getProperty( pathProperty ).trim();
        File absolutePath = Utility.getAbsolutePathFromString( templatePathString );
        log.info( pathProperty + ": " + absolutePath );
        return absolutePath;
    }

    private void initDocumentMapper() {
        documentMapper = new DocumentMapper( this, imcmsAuthenticatorAndUserMapper );
    }

    private void initTemplateMapper() {
        templateMapper = new TemplateMapper( this );
    }

    private void initAuthenticatorsAndUserAndRoleMappers( Properties props ) {
        String externalAuthenticatorName = props.getProperty( "ExternalAuthenticator" );
        String externalUserAndRoleMapperName = props.getProperty( "ExternalUserAndRoleMapper" );

        Authenticator externalAuthenticator = null;
        UserAndRoleMapper externalUserAndRoleMapper = null;

        if ( null != externalAuthenticatorName && null != externalUserAndRoleMapperName ) {
            log.info( "ExternalAuthenticator: " + externalAuthenticatorName );
            log.info( "ExternalUserAndRoleMapper: " + externalUserAndRoleMapperName );
            externalAuthenticator =
            initExternalAuthenticator( externalAuthenticatorName, props );
            externalUserAndRoleMapper =
            initExternalUserAndRoleMapper( externalUserAndRoleMapperName, props );
            if ( null == externalAuthenticator || null == externalUserAndRoleMapper ) {
                log.error( "Failed to initialize both authenticator and user-and-role-documentMapper, using default implementations." );
                externalAuthenticator = null;
                externalUserAndRoleMapper = null;
            }
        } else if ( null == externalAuthenticatorName && null == externalUserAndRoleMapperName ) {
            log.info( "ExternalAuthenticator not set." );
            log.info( "ExternalUserAndRoleMapper not set." );
        } else {
            log.error( "External authenticator and external usermapper should both be either set or not set. Using default implementation." );
            log.error( "External authenticator and external usermapper should both be either set or not set. Using default implementation." );
        }
        imcmsAuthenticatorAndUserMapper = new ImcmsAuthenticatorAndUserMapper( this );
        externalizedImcmsAuthAndMapper =
        new ExternalizedImcmsAuthenticatorAndUserMapper( imcmsAuthenticatorAndUserMapper, externalAuthenticator,
                                                         externalUserAndRoleMapper, getDefaultLanguageAsIso639_2() );
        externalizedImcmsAuthAndMapper.synchRolesWithExternal();
    }

    public synchronized int getSessionCounter() {
        return sessionCounter;
    }

    public String getSessionCounterDateAsString() {
        DateFormat dateFormat = new SimpleDateFormat( DateConstants.DATE_FORMAT_STRING );

        return dateFormat.format( sessionCounterDate );
    }

    /**
     * Verify a Internet/Intranet user. User data retrived from SQL Database.
     */
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

    private UserAndRoleMapper initExternalUserAndRoleMapper( String externalUserAndRoleMapperName,
                                                             Properties userAndRoleMapperPropertiesSubset ) {
        UserAndRoleMapper externalUserAndRoleMapper = null;
        if ( null == externalUserAndRoleMapperName ) {
            externalUserAndRoleMapper = null;
        } else if ( EXTERNAL_USER_AND_ROLE_MAPPER_LDAP.equalsIgnoreCase( externalUserAndRoleMapperName ) ) {
            try {
                externalUserAndRoleMapper = new LdapUserAndRoleMapper( userAndRoleMapperPropertiesSubset );
            } catch ( LdapUserAndRoleMapper.LdapInitException e ) {
                log.error( "LdapUserAndRoleMapper could not be created, using default user and role documentMapper.",
                           e );
            }
        } else {
            externalUserAndRoleMapper = (UserAndRoleMapper)createInstanceOfClass( externalUserAndRoleMapperName );
        }
        return externalUserAndRoleMapper;
    }

    private Authenticator initExternalAuthenticator( String externalAuthenticatorName,
                                                     Properties authenticatorPropertiesSubset ) {
        Authenticator externalAuthenticator = null;
        try {
            if ( null == externalAuthenticatorName ) {
                externalAuthenticator = null;
            } else if ( EXTERNAL_AUTHENTICATOR_SMB.equalsIgnoreCase( externalAuthenticatorName ) ) {
                externalAuthenticator = new SmbAuthenticator( authenticatorPropertiesSubset );
            } else if ( EXTERNAL_AUTHENTICATOR_LDAP.equalsIgnoreCase( externalAuthenticatorName ) ) {
                try {
                    externalAuthenticator = new LdapUserAndRoleMapper( authenticatorPropertiesSubset );
                } catch ( LdapUserAndRoleMapper.LdapInitException e ) {
                    log.error( "LdapUserAndRoleMapper could not be created, using default user and role documentMapper.",
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
     * Returns the menubuttonrow
     */
    public String getAdminButtons( UserDomainObject user, DocumentDomainObject document ) {
        int user_permission_set_id = documentMapper.getUsersMostPrivilegedPermissionSetIdOnDocument( user, document );
        if ( user_permission_set_id >= DocumentPermissionSetDomainObject.TYPE_ID__READ && !user.isUserAdmin() ) {
            return "";
        }

        DocumentPermissionSetDomainObject documentPermissionSet = documentMapper.getUsersMostPrivilegedPermissionSetOnDocument( user, document ) ;
        String documentTypeName = sqlQueryStr( "select type from doc_types where doc_type = ?", new String[]{"" + document.getDocumentTypeId()} );
        List parseVariables = Arrays.asList( new Object[] {
            "user", user,
            "document", document,
            "documentPermissionSet", documentPermissionSet,
            "statusicon", documentMapper.getStatusIconTemplate( document, user ),
            "documentTypeName", documentTypeName
        } ) ;

        return getAdminTemplate( "adminbuttons/adminbuttons.html", user, parseVariables ) ;
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
     * Save an imageref.
     */
    public void saveImage( int meta_id, UserDomainObject user, int img_no, ImageDomainObject image ) {
        documentMapper.saveDocumentImage( meta_id, img_no, image, user );
    }

    /**
     * Delete a doc and all data related. Delete from db and file system.
     * Fixme:  delete doc from plugin db
     */
    public void deleteDocAll( int meta_id, UserDomainObject user ) {

        String filename = meta_id + "_se";
        File file = new File( filePath, filename );

        //If meta_id is a file document we have to delete the file from file system
        if ( file.exists() ) {
            file.delete();
        }

        // Create a db connection and execte sp DocumentDelete on meta_id
        sqlUpdateProcedure( "DocumentDelete", new String[]{"" + meta_id} );
        this.updateLogs( "Document  " + "[" + meta_id + "] ALL deleted by user: [" +
                         user.getFullName() + "]" );
    }

    public void saveManualSort( int meta_id, UserDomainObject user, List childs,
                                List sort_no, int menuNumber ) {
        String columnName = "manual_sort_order";
        saveChildSortOrder( columnName, childs, sort_no, meta_id, user, menuNumber );
    }

    public void saveTreeSortIndex( int meta_id, UserDomainObject user, List childs, List sort_no, int menuNumber ) {
        String columnName = "tree_sort_index";
        for ( ListIterator iterator = sort_no.listIterator(); iterator.hasNext(); ) {
            String menuItemTreeSortKey = (String)iterator.next();
            Perl5Util perl5util = new Perl5Util();
            menuItemTreeSortKey = perl5util.substitute( "s/\\D+/./g", menuItemTreeSortKey );
            iterator.set( menuItemTreeSortKey );
        }
        saveChildSortOrder( columnName, childs, sort_no, meta_id, user, menuNumber );
    }

    private void saveChildSortOrder( String columnName, List childs, List sort_no, int meta_id, UserDomainObject user,
                                     int menuIndex ) {
        for ( int i = 0; i < childs.size(); i++ ) {
            String columnValue = sort_no.get( i ).toString();
            String to_meta_id = childs.get( i ).toString();
            String sql = "UPDATE childs SET " + columnName
                         + " = ? WHERE to_meta_id = ? AND menu_id = (SELECT menu_id FROM menus WHERE meta_id = ? AND menu_index = ?)";
            sqlUpdateQuery( sql, new String[]{columnValue, to_meta_id, "" + meta_id, "" + menuIndex} );
        }

        updateLogs( "Child manualsort for [" + meta_id + "] updated by user: [" +
                    user.getFullName() + "]" );
    }

    /**
     * Archive childs for a menu.
     */
    public void archiveChilds( int meta_id, UserDomainObject user, String[] childsThisMenu ) {

        Date now = getCurrentDate();
        for ( int i = 0; i < childsThisMenu.length; i++ ) {
            DocumentDomainObject document = documentMapper.getDocument( Integer.parseInt( childsThisMenu[i] ) );
            document.setArchivedDatetime( now );
            try {
                documentMapper.saveDocument( document, user );
            } catch ( MaxCategoryDomainObjectsOfTypeExceededException e ) {
                throw new RuntimeException( e );
            }
        }

        this.updateLogs( "Childs [" + StringUtils.join( childsThisMenu, ", " ) + "] from " +
                         "[" + meta_id + "] archived by user: [" +
                         user.getFullName() + "]" );
    }

    /**
     * Check if url doc.
     */
    public String isUrlDoc( int meta_id ) {
        String url_ref = null;
        if ( DocumentDomainObject.DOCTYPE_URL == getDocType( meta_id ) ) {
            String sqlStr = "select url_ref from url_docs where meta_id = ?";
            url_ref = sqlQueryStr( sqlStr, new String[]{"" + meta_id} );
        }

        return url_ref;
    }

    /**
     * Update logs.
     */
    public void updateLogs( String event ) {
        mainLog.info( event );
    }

    /**
     * Check if frameset doc.                                                                        *
     */
    public String isFramesetDoc( int meta_id ) {

        String htmlStr = null;
        if ( DocumentDomainObject.DOCTYPE_HTML == getDocType( meta_id ) ) {
            String sqlStr = "select frame_set from frameset_docs where meta_id = ?";
            htmlStr = sqlQueryStr( sqlStr, new String[]{"" + meta_id} );
        }
        return htmlStr;

    }

    /**
     * Activate child to child-table.
     */
    public void activateChild( int meta_id, imcode.server.user.UserDomainObject user ) {

        String sqlStr = "update meta set activate = 1 where meta_id = ?";
        sqlUpdateQuery( sqlStr, new String[]{"" + meta_id} );

        this.updateLogs( "Child [" + meta_id + "] activated  " + "by user: [" + user.getFullName() + "]" );

    }

    public String[] sqlQuery( String sqlQuery, String[] parameters ) {
        return SqlHelpers.sqlQuery( m_conPool, sqlQuery, parameters );
    }

    public String sqlQueryStr( String sqlStr, String[] params ) {
        return SqlHelpers.sqlQueryStr( m_conPool, sqlStr, params );
    }

    /**
     * Send a sql update query to the database
     */
    public int sqlUpdateQuery( String sqlStr, String[] params ) {
        return SqlHelpers.sqlUpdateQuery( m_conPool, sqlStr, params );
    }

    /**
     * The preferred way of getting data from the db.
     * String.trim()'s the results.
     *
     * @param procedure The name of the procedure
     * @param params    The parameters of the procedure
     */
    public String[] sqlProcedure( String procedure, String[] params ) {
        return SqlHelpers.sqlProcedure( m_conPool, procedure, params );
    }

    /**
     * The preferred way of getting data to the db.
     *
     * @param procedure The name of the procedure
     * @param params    The parameters of the procedure
     * @return updateCount or -1 if error
     */
    public int sqlUpdateProcedure( String procedure, String[] params ) {
        return SqlHelpers.sqlUpdateProcedure( m_conPool, procedure, params );
    }

    public String sqlProcedureStr( String procedure, String[] params ) {
        return SqlHelpers.sqlProcedureStr( m_conPool, procedure, params );
    }

    public DocumentMapper getDocumentMapper() {
        return documentMapper;
    }

    public TemplateMapper getTemplateMapper() {
        return templateMapper;
    }

    public SMTP getSMTP() {
        try {
            return new SMTP( smtpServer, smtpPort, 30000 );
        } catch ( IOException e ) {
            return null;
        }
    }

    public ImcmsAuthenticatorAndUserMapper getImcmsAuthenticatorAndUserAndRoleMapper() {
        return imcmsAuthenticatorAndUserMapper;
    }

    /**
     * Parse doc replace variables with data, uses two vectors
     */
    public String replaceTagsInStringWithData( String htmlStr, java.util.Vector variables, java.util.Vector data ) {
        String[] foo = new String[variables.size()];
        String[] bar = new String[data.size()];
        return imcode.util.Parser.parseDoc( htmlStr, (String[])variables.toArray( foo ), (String[])data.toArray( bar ) );
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
        if (null == user) {
            throw new NullArgumentException( "user" ) ;
        }
        String langPrefix = user.getLanguageIso639_2() ;
        return getTemplate( langPrefix + "/" + directory + "/"
                            + adminTemplateName, user, variables );
    }

    /**
     * Parse doc replace variables with data , use template
     */
    public String getTemplateFromSubDirectoryOfDirectory( String adminTemplateName, UserDomainObject user, List variables,
                                                          String directory, String subDirectory ) {

        if (null == user) {
            throw new NullArgumentException( "user" ) ;
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
                List parseDocVariables = new ArrayList( variables.size() ) ;
                for ( Iterator iterator = variables.iterator(); iterator.hasNext(); ) {
                    String key = (String)iterator.next();
                    Object value = iterator.next();
                    context.put( key, value );
                    boolean isNotVelocityVariable = value instanceof String;
                    if (isNotVelocityVariable) {
                        parseDocVariables.add(key) ;
                        parseDocVariables.add(value) ;
                    }
                }
                variables = parseDocVariables ;
            }
            StringWriter stringWriter = new StringWriter();
            velocity.mergeTemplate( path, WebAppGlobalConstants.DEFAULT_ENCODING_WINDOWS_1252, context, stringWriter );
            String result = stringWriter.toString();
            if ( null != variables ) {
                result = Parser.parseDoc( result, (String[])variables.toArray( new String[variables.size()] ) );
            }
            return result;
        } catch ( Exception e ) {
            throw new RuntimeException( "getTemplate(\"" + path + "\") : "+e.getMessage(), e );
        }
    }

    private synchronized VelocityEngine createVelocityEngine( String languageIso639_2 ) throws Exception {
        VelocityEngine velocity = new VelocityEngine();
        log.debug( "createVelocityEngine" );
        velocity.setProperty( VelocityEngine.FILE_RESOURCE_LOADER_PATH, templatePath.getCanonicalPath() );
        velocity.setProperty( VelocityEngine.VM_LIBRARY, languageIso639_2 + "/gui.vm" );
        velocity.setProperty( VelocityEngine.VM_LIBRARY_AUTORELOAD, "true" );
        velocity.setProperty( VelocityEngine.RUNTIME_LOG_LOGSYSTEM_CLASS, "org.apache.velocity.runtime.log.SimpleLog4JLogSystem" );
        velocity.setProperty( "runtime.log.logsystem.log4j.category", "velocity" );
        velocity.init();
        return velocity;
    }

    public VelocityEngine getVelocityEngine( UserDomainObject user ) {
        try {
            String languageIso639_2 = user.getLanguageIso639_2();
            VelocityEngine velocityEngine = (VelocityEngine)velocityEngines.get(languageIso639_2) ;
            if ( velocityEngine == null ) {
                velocityEngine = createVelocityEngine( languageIso639_2 );
                velocityEngines.put(languageIso639_2, velocityEngine) ;
            }
            return velocityEngine;
        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }
    }

    public VelocityContext getVelocityContext( UserDomainObject user ) {
        VelocityContext context = new VelocityContext();
        // FIXME: This method needs an HttpServletRequest in, to get the context path from
        context.put( "contextPath", user.getCurrentContextPath() );
        context.put( "language", user.getLanguageIso639_2() );
        return context;
    }

    /**
     * @deprecated Ugly use {@link IMCServiceInterface#getTemplateFromDirectory(String,imcode.server.user.UserDomainObject,java.util.List,String)}
     *             or something else instead.
     */
    public File getExternalTemplateFolder( int meta_id, UserDomainObject user ) {
        int docType = getDocType( meta_id );
        String langPrefix = getUserLangPrefixOrDefaultLanguage( user );
        return new File( templatePath, langPrefix + "/" + docType + "/" );
    }

    /**
     * Return  templatehome.
     */
    public File getTemplatePath() {
        return templatePath;
    }

    /**
     * Return url-path to images.

     */
    public String getImageUrl() {
        return imageUrl;
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
        return imcmsPath;
    }

    public String getDefaultLanguageAsIso639_2() {
        return defaultLanguageAsIso639_2;
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

    public Map sqlQueryHash( String sqlQuery, String[] params ) {
        return SqlHelpers.sqlQueryHash( m_conPool, sqlQuery, params );
    }

    public Map sqlProcedureHash( String procedure, String[] params ) {
        return SqlHelpers.sqlProcedureHash( m_conPool, procedure, params );
    }

    /**
     * Send a procedure to the database and return a multi string array
     */
    public String[][] sqlProcedureMulti( String procedure, String[] params ) {
        return SqlHelpers.sqlProcedureMulti( m_conPool, procedure, params );
    }

    public String[][] sqlQueryMulti( String sqlQuery, String[] params ) {
        return SqlHelpers.sqlQueryMulti( m_conPool, sqlQuery, params );
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
        return documentMapper.userHasMoreThanReadPermissionOnDocument( user, documentMapper.getDocument( meta_id ) );
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

            if ( perms.length > 0
                 && set_id == 0		// User has full permission for this document
                 || ( set_id < 3 && ( ( set & permission ) > 0 ) )	// User has at least one of the permissions given.
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

        File f = new File( templatePath, "text/" + templateId + ".html" );

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
            File fileObj = new File( templatePath, "/text/demo/" + template_id + "." + suffixList[i] );
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
        return fileCache.getCachedFileString( new File( templatePath, "/text/" + template_id + ".html" ) );
    }

    /**
     * save demo template
     */
    public void saveDemoTemplate( int template_id, byte[] data, String suffix ) throws IOException {

        deleteDemoTemplate( template_id );

        FileOutputStream fw = new FileOutputStream( templatePath + "/text/demo/" + template_id + "." + suffix );
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
        File demoDir = new File( templatePath + "/text/demo/" );

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

        File demoTemplateDirectory = new File( new File( templatePath, "text" ), "demo" );
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
        return fileCache.getCachedFileString( new File( fortunePath, path ) );
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
            File file = new File( fortunePath, quoteListName );
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
        FileWriter writer = new FileWriter( new File( fortunePath, quoteListName ) );
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
            File file = new File( fortunePath, pollListName );
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
        FileWriter writer = new FileWriter( new File( fortunePath, pollListName ) );
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

    public void updateModifiedDatesOnDocumentAndItsParent( int metaId, Date dateTime ) {
        documentMapper.touchDocument( documentMapper.getDocument( metaId ) );
        documentMapper.sqlUpdateModifiedDatesOnDocumentAndItsParent( metaId, dateTime );
    }

    public Properties getLanguageProperties( UserDomainObject user ) {

        if ( langproperties_eng == null ) {
            initLangProperties( "eng" );
        }
        if ( langproperties_swe == null ) {
            initLangProperties( "swe" );
        }
        if ( "swe".equals( user.getLanguageIso639_2() ) ) {
            return langproperties_swe;
        }
        return langproperties_eng;
    }

    public File getFilePath() {
        return filePath;
    }

    public File getIncludePath() {
        return includePath;
    }

    public Collator getDefaultLanguageCollator() {
        try {
            return Collator.getInstance( new Locale( LanguageMapper.convert639_2to639_1( defaultLanguageAsIso639_2 ) ) );
        } catch ( LanguageMapper.LanguageNotSupportedException e ) {
            throw new RuntimeException( e );
        }
    }

}
