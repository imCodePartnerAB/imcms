package imcode.server;

import imcode.server.db.ConnectionPool;
import imcode.server.document.*;
import imcode.server.parser.ParserParameters;
import imcode.server.parser.TextDocumentParser;
import imcode.server.parser.AdminButtonParser;
import imcode.server.user.*;
import imcode.server.db.*;
import imcode.util.*;
import imcode.util.fortune.*;
import imcode.util.poll.PollHandlingSystem;
import imcode.util.poll.PollHandlingSystemImpl;
import imcode.util.shop.ShoppingOrderSystem;
import imcode.util.shop.ShoppingOrderSystemImpl;
import org.apache.log4j.Logger;

import imcode.util.FileCache;
import imcode.util.fortune.*;
import org.apache.oro.text.perl.Perl5Util;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

final public class IMCService implements IMCServiceInterface, IMCConstants {

    public ConnectionPool getConnectionPool() {
        return m_conPool;
    }

    private final ConnectionPool m_conPool;
    private TextDocumentParser textDocParser;

    private File m_TemplateHome;           // template home
    private File m_IncludePath;
    private File m_FortunePath;
    private File m_ImagePath;             //  folder  /images
    private File m_ImcmsPath;             //  folder  /imcms
    private File m_FilePath;
    private String m_StartUrl;
    private String m_ImageUrl;            //  folder  /images
    private String m_ImcmsUrl;            //  folder  /imcms
    private String defaultLanguageAsIso639_2 ;
    private static final int DEFAULT_STARTDOCUMENT = 1001;

    private SystemData sysData;

    private ExternalDocType m_ExDoc[];
    private String m_SessionCounterDate = "";
    private int m_SessionCounter = 0;

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

    static {
        mainLog.info("Main log started.");
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

        String templatePathString = props.getProperty( "TemplatePath" ).trim();
        m_TemplateHome = Utility.getAbsolutePathFromString( templatePathString );
        log.info( "TemplatePath: " + m_TemplateHome );

        String imagePathString = props.getProperty( "ImagePath" ).trim();
        m_ImagePath = imcode.util.Utility.getAbsolutePathFromString( imagePathString );
        log.info( "ImagePath: " + m_ImagePath );

        String imcmsPathString = props.getProperty( "ImcmsPath" ).trim();
        m_ImcmsPath = imcode.util.Utility.getAbsolutePathFromString( imcmsPathString );
        log.info( "ImcmsPath: " + m_ImcmsPath );

        String includePathString = props.getProperty( "IncludePath" ).trim();
        m_IncludePath = Utility.getAbsolutePathFromString( includePathString );
        log.info( "IncludePath: " + m_IncludePath );

        String fortunePathString = props.getProperty( "FortunePath" ).trim();
        m_FortunePath = Utility.getAbsolutePathFromString( fortunePathString );
        log.info( "FortunePath: " + m_FortunePath );

        String filePathString = props.getProperty( "FilePath" ).trim();
        m_FilePath = Utility.getAbsolutePathFromString( filePathString );
        log.info( "FilePath: " + m_FilePath );

        m_StartUrl = props.getProperty( "StartUrl" ).trim(); //FIXME: Get from webserver, or get rid of if possible.
        log.info( "StartUrl: " + m_StartUrl );

        // FIXME: Get imageurl from webserver somehow. The user-object, perhaps?
        m_ImageUrl = props.getProperty( "ImageUrl" ).trim(); //FIXME: Get from webserver, or get rid of if possible.
        log.info( "ImageUrl: " + m_ImageUrl );

        m_ImcmsUrl = props.getProperty( "ImcmsUrl" ).trim(); //FIXME: Get from webserver, or get rid of if possible.
        log.info( "ImcmsUrl: " + m_ImcmsUrl );

        defaultLanguageAsIso639_2 = props.getProperty( "DefaultLanguage" ).trim(); //FIXME: Get from DB
        try {
            if(defaultLanguageAsIso639_2.length() < 3){
                defaultLanguageAsIso639_2 = LanguageMapper.convert639_1to639_2( defaultLanguageAsIso639_2 );
            }
        } catch ( LanguageMapper.LanguageNotSupportedException e1 ) {
            log.fatal( "Configured default language " + defaultLanguageAsIso639_2 + " is not supported either." );
            defaultLanguageAsIso639_2 = null;
        }
        log.info( "DefaultLanguage: " + defaultLanguageAsIso639_2 );

        String externalDocTypes = props.getProperty( "ExternalDoctypes" ).trim(); //FIXME: Get rid of, if possible.
        log.info( "ExternalDoctypes: " + externalDocTypes );

        StringTokenizer doc_types = new StringTokenizer( externalDocTypes, ";", false );
        m_ExDoc = new ExternalDocType[doc_types.countTokens()];
        try {
            for ( int doc_count = 0; doc_types.hasMoreTokens(); ++doc_count ) {
                StringTokenizer tempStr = new StringTokenizer( doc_types.nextToken(), ":", false );
                String items[] = new String[tempStr.countTokens()];
                for ( int i = 0; tempStr.hasMoreTokens(); ++i ) {
                    items[i] = tempStr.nextToken();
                }
                m_ExDoc[doc_count] = new ExternalDocType( Integer.parseInt( items[0] ), items[1]);
            }
        } catch ( NoSuchElementException e ) {
            e.printStackTrace();
        }

        try {
            m_SessionCounter = Integer.parseInt( this.sqlProcedureStr( "GetCurrentSessionCounter", new String[0] ) );
            m_SessionCounterDate = this.sqlProcedureStr( "GetCurrentSessionCounterDate", new String[0] );
        } catch ( NumberFormatException ex ) {
            log.fatal( "Failed to get SessionCounter from db.", ex );
            throw ex;
        }

        log.info( "SessionCounter: " + m_SessionCounter );
        log.info( "SessionCounterDate: " + m_SessionCounterDate );

        textDocParser = new TextDocumentParser( this, m_TemplateHome, m_IncludePath, m_ImageUrl );

    }

    private void initDocumentMapper() {
        documentMapper = new DocumentMapper( this, imcmsAuthenticatorAndUserMapper );
    }

    private void initTemplateMapper() {
        templateMapper = new TemplateMapper( this ) ;
    }

    private void initAuthenticatorsAndUserAndRoleMappers( Properties props ) {
        String externalAuthenticatorName = props.getProperty( "ExternalAuthenticator" );
        String externalUserAndRoleMapperName = props.getProperty( "ExternalUserAndRoleMapper" );

        Authenticator externalAuthenticator = null;
        UserAndRoleMapper externalUserAndRoleMapper = null;

        PrefixRemovedProperties authenticatorPropertiesSubset = new PrefixRemovedProperties( props, "ExternalAuthenticator." );
        PrefixRemovedProperties userAndRoleMapperPropertiesSubset = new PrefixRemovedProperties( props, "ExternalUserAndRoleMapper." );

        if ( null != externalAuthenticatorName && null != externalUserAndRoleMapperName ) {
            log.info( "ExternalAuthenticator: " + externalAuthenticatorName );
            log.info( "ExternalUserAndRoleMapper: " + externalUserAndRoleMapperName );
            externalAuthenticator = initExternalAuthenticator( externalAuthenticatorName, authenticatorPropertiesSubset );
            externalUserAndRoleMapper = initExternalUserAndRoleMapper( externalUserAndRoleMapperName, userAndRoleMapperPropertiesSubset );
            if ( null == externalAuthenticator || null == externalUserAndRoleMapper ) {
                log.error( "Failed to initialize both authenticator and user-and-role-internalDocumentMapper, using default implementations." );
                externalAuthenticator = null;
                externalUserAndRoleMapper = null;
            }
        } else if ( null == externalAuthenticatorName && null == externalUserAndRoleMapperName ) {
            log.info( "ExternalAuthenticator not set." );
            log.info( "ExternalUserAndRoleMapper not set." );
        } else {
            log.error( "External authenticator and external usermapper should both be either set or not set. Using default implementation." );
            externalAuthenticatorName = null;
            externalUserAndRoleMapperName = null;
        }
        imcmsAuthenticatorAndUserMapper = new ImcmsAuthenticatorAndUserMapper( this );
        externalizedImcmsAuthAndMapper = new ExternalizedImcmsAuthenticatorAndUserMapper( imcmsAuthenticatorAndUserMapper, externalAuthenticator, externalUserAndRoleMapper, getDefaultLanguageAsIso639_2() );
        externalizedImcmsAuthAndMapper.synchRolesWithExternal();
    }

    public int getSessionCounter() {
        return m_SessionCounter;
    }

    public String getSessionCounterDate() {
        return m_SessionCounterDate;
    }

    /**
     * Verify a Internet/Intranet user. User data retrived from SQL Database.
     */
    public UserDomainObject verifyUser( String login, String password ) {
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

        return result;
    }

    private UserAndRoleMapper initExternalUserAndRoleMapper( String externalUserAndRoleMapperName, Properties userAndRoleMapperPropertiesSubset ) {
        UserAndRoleMapper externalUserAndRoleMapper = null;
        if ( null == externalUserAndRoleMapperName ) {
            externalUserAndRoleMapper = null;
        } else if ( EXTERNAL_USER_AND_ROLE_MAPPER_LDAP.equalsIgnoreCase( externalUserAndRoleMapperName ) ) {
            try {
                externalUserAndRoleMapper = new LdapUserAndRoleMapper( userAndRoleMapperPropertiesSubset );
            } catch ( LdapUserAndRoleMapper.LdapInitException e ) {
                log.error( "LdapUserAndRoleMapper could not be created, using default user and role internalDocumentMapper.", e );
            }
        } else {
            externalUserAndRoleMapper = (UserAndRoleMapper)createInstanceOfClass( externalUserAndRoleMapperName );
        }
        return externalUserAndRoleMapper;
    }

    private Authenticator initExternalAuthenticator( String externalAuthenticatorName, Properties authenticatorPropertiesSubset ) {
        Authenticator externalAuthenticator = null;
        if ( null == externalAuthenticatorName ) {
            externalAuthenticator = null;
        } else if ( EXTERNAL_AUTHENTICATOR_SMB.equalsIgnoreCase( externalAuthenticatorName ) ) {
            externalAuthenticator = new SmbAuthenticator( authenticatorPropertiesSubset );
        } else if ( EXTERNAL_AUTHENTICATOR_LDAP.equalsIgnoreCase( externalAuthenticatorName ) ) {
            try {
                externalAuthenticator = new LdapUserAndRoleMapper( authenticatorPropertiesSubset );
            } catch ( LdapUserAndRoleMapper.LdapInitException e ) {
                log.error( "LdapUserAndRoleMapper could not be created, using default user and role internalDocumentMapper.", e );
            }
        } else {
            externalAuthenticator = (Authenticator)createInstanceOfClass( externalAuthenticatorName );
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

    public UserDomainObject getUserById( int id ) {
        ImcmsAuthenticatorAndUserMapper userMapper = new ImcmsAuthenticatorAndUserMapper( this );
        UserDomainObject result = userMapper.getUser( id );
        return result;
    }

    //Check if user has a special adminRole
    public boolean checkUserAdminrole( int userId, int adminRole ) {
        String[] adminrole = sqlProcedure( "checkUserAdminrole ", new String[]{"" + userId, "" + adminRole} );
        if ( adminrole.length > 0 ) {
            if ( ( "" + adminRole ).equals( adminrole[0] ) ) {
                return true;
            }
        }
        return false;
    }

    public String parsePage( DocumentRequest documentRequest, int flags, ParserParameters paramsToParse ) throws IOException {
        return textDocParser.parsePage( documentRequest, flags, paramsToParse );
    }

    /**
     Returns the menubuttonrow
     */
    public String getMenuButtons( String meta_id, UserDomainObject user ) {
        // Get the users language prefix
        String lang_prefix = user.getLangPrefix();

        // Find out what permissions the user has
        String[] permissions = sqlProcedure( "GetUserPermissionSet", new String[]{String.valueOf( meta_id ), String.valueOf( user.getUserId() )} );

        if ( permissions.length == 0 ) {
            return "";
        }

        StringBuffer tempbuffer = null;
        StringBuffer templatebuffer = null;
        StringBuffer superadmin = null;
        int doc_type = getDocType( Integer.parseInt( meta_id ) );
        try {

            String tempbuffer_filename = lang_prefix + "/admin/adminbuttons/adminbuttons" + doc_type + ".html";
            String templatebuffer_filename = lang_prefix + "/admin/adminbuttons/adminbuttons.html";
            String superadmin_filename = lang_prefix + "/admin/adminbuttons/superadminbutton.html";

            tempbuffer = new StringBuffer( fileCache.getCachedFileString( new File( m_TemplateHome, tempbuffer_filename ) ) );
            templatebuffer = new StringBuffer( fileCache.getCachedFileString( new File( m_TemplateHome, templatebuffer_filename ) ) );
            superadmin = new StringBuffer( fileCache.getCachedFileString( new File( m_TemplateHome, superadmin_filename ) ) );

        } catch ( IOException e ) {
            log.error( e.toString() );
            return "";
        }

        int user_permission_set_id = Integer.parseInt( permissions[0] );
        int user_permission_set = Integer.parseInt( permissions[1] );

        imcode.server.parser.AdminButtonParser doc_tags = new imcode.server.parser.AdminButtonParser( new File( m_TemplateHome, lang_prefix + "/admin/adminbuttons/adminbutton" + doc_type + "_" ).toString(), ".html", user_permission_set_id, user_permission_set );

        doc_tags.put( "getMetaId", meta_id );
        Parser.parseTags( tempbuffer, '#', " <>\n\r\t", doc_tags, true, 1 );

        AdminButtonParser tags = new imcode.server.parser.AdminButtonParser( new File( m_TemplateHome, lang_prefix + "/admin/adminbuttons/adminbutton_" ).toString(), ".html", user_permission_set_id, user_permission_set );

        tags.put( "getMetaId", meta_id );
        tags.put( "doc_buttons", tempbuffer.toString() );


        // if user is superadmin or useradmin lets add superadmin button
        if ( checkAdminRights( user ) || checkUserAdminrole( user.getUserId(), 2 ) ) {
            tags.put( "superadmin", superadmin.toString() );
        } else {
            tags.put( "superadmin", "" );
        }

        String doctypeStr = sqlQueryStr("select type from doc_types where doc_type = ?", new String[]{"" + doc_type});
        tags.put("doc_type", doctypeStr);

        Parser.parseTags( templatebuffer, '#', " <>\n\r\t", tags, true, 1 );

        return templatebuffer.toString();
    }

    /**
     Returns the menubuttonrow
     */
    public String getMenuButtons( int meta_id, UserDomainObject user ) {
        return getMenuButtons( String.valueOf( meta_id ), user );
    }

    /**
     Retrieve a text from the db.
     @param meta_id The id of the page.
     @param no      The id of the text in the page.
     @return The text from the db, or null if there was none.
     **/
    public TextDocumentTextDomainObject getText( int meta_id, int no ) {
        return documentMapper.getText( meta_id, no );
    }

    /**
     * Save an imageref.
     */
    public void saveImage( int meta_id, UserDomainObject user, int img_no, imcode.server.Image image ) {
        String[] imageData = sqlQuery("select * from images where meta_id = ? and name = ?", new String[]{"" + meta_id, "" + img_no});
        String sqlStr;
        if (imageData.length > 0) {
            sqlStr = "update images\n"
            +"set imgurl  = ?, \n"
            +"width       = ?, \n"
            +"height      = ?, \n"
            +"border      = ?, \n"
            +"v_space     = ?, \n"
            +"h_space     = ?, \n"
            +"image_name  = ?, \n"
            +"target      = ?, \n"
            +"target_name = ?, \n"
            +"align       = ?, \n"
            +"alt_text    = ?, \n"
            +"low_scr     = ?, \n"
            +"linkurl     = ?  \n"
            +"where meta_id = ? \n"
            +"and name = ? \n";

        } else {
            sqlStr = "insert into images (imgurl, width, height, border, v_space, h_space, image_name, target, target_name, align, alt_text, low_scr, linkurl, meta_id, name)"
                    + " values(?,?,?, ?,?,?, ?,?,?, ?,?,?, ?,?,?)";
        }
        sqlUpdateQuery(sqlStr, new String[]{
            image.getImageRef(),
            "" + image.getImageWidth(),
            "" + image.getImageHeight(),
            "" + image.getImageBorder(),
            "" + image.getVerticalSpace(),
            "" + image.getHorizontalSpace(),
            image.getImageName(),
            image.getTarget(),
            image.getTargetName(),
            image.getImageAlign(),
            image.getAltText(),
            image.getLowScr(),
            image.getImageRefLink(),
            "" + meta_id,
            "" + img_no
        });


        this.updateLogs("ImageRef " + img_no + " =" + image.getImageRef() +
                " in  " + "[" + meta_id + "] modified by user: [" +
                user.getFullName() + "]");
    }


    /**
     * Delete a doc and all data related. Delete from db and file system.
     * Fixme:  delete doc from plugin db
     */
    public void deleteDocAll( int meta_id, UserDomainObject user ) {

        String filename = meta_id + "_se";
        File file = new File(m_FilePath, filename);

        //If meta_id is a file document we have to delete the file from file system
        if (file.exists()) {
            file.delete();
        }

        // Create a db connection and execte sp DocumentDelete on meta_id
        sqlUpdateProcedure("DocumentDelete", new String[]{""+meta_id});
        this.updateLogs("Document  " + "[" + meta_id + "] ALL deleted by user: [" +
                user.getFullName() + "]");
    }

    public void addExistingDoc( int meta_id, UserDomainObject user, int existing_meta_id, int doc_menu_no ) throws DocumentMapper.DocumentAlreadyInMenuException {
        DocumentMapper.addDocumentToMenu( this, user, meta_id, doc_menu_no, existing_meta_id );
    }

    public void saveManualSort(int meta_id, UserDomainObject user, List childs,
                               List sort_no, int menuNumber) {
        String columnName = "manual_sort_order";
        saveChildSortOrder(columnName, childs, sort_no, meta_id, user, menuNumber);
    }

    public void saveTreeSortIndex(int meta_id, UserDomainObject user, List childs, List sort_no, int menuNumber) {
        String columnName = "tree_sort_index";
        for (ListIterator iterator = sort_no.listIterator(); iterator.hasNext();) {
            String menuItemTreeSortKey = (String) iterator.next();
            Perl5Util perl5util = new Perl5Util();
            menuItemTreeSortKey = perl5util.substitute("s/\\D+/./g", menuItemTreeSortKey);
            iterator.set(menuItemTreeSortKey);
        }
        saveChildSortOrder(columnName, childs, sort_no, meta_id, user, menuNumber);
    }

    private void saveChildSortOrder(String columnName, List childs, List sort_no, int meta_id, UserDomainObject user, int menuNumber) {
        for (int i = 0; i < childs.size(); i++) {
            String columnValue = sort_no.get(i).toString();
            String to_meta_id = childs.get(i).toString();
            String sql = "update childs set " + columnName + " = ? WHERE meta_id = ? and to_meta_id = ? and menu_sort = ?";
            sqlUpdateQuery(sql, new String[]{columnValue, "" + meta_id, to_meta_id, "" + menuNumber});
        }

        updateLogs("Child manualsort for [" + meta_id + "] updated by user: [" +
                user.getFullName() + "]");
    }


    /**
     Makes copies of the documents given in the String-array, and inserts them into the given internalDocument and menu.
     If one of the documents couldn't be copied for some reason, no documents are copied, and the uncopyable
     documents are returned.

     @param meta_id The internalDocument to insert into
     @param doc_menu_no The menu to insert into
     @param user The user
     @param childsThisMenu The id's to copy.

     @return A String array containing the meta-ids of uncopyable pages.
     **/
    public String[] copyDocs( int meta_id, int doc_menu_no, UserDomainObject user, String[] childsThisMenu, String copyPrefix ) {

        if ( childsThisMenu != null && childsThisMenu.length > 0 ) {

            StringBuffer logchilds = new StringBuffer( childsThisMenu[0] );
            for ( int i = 1; i < childsThisMenu.length; ++i ) {
                logchilds.append( "," + childsThisMenu[i] );
            }
            String[] uncopyable = sqlProcedure( "CheckForFileDocs", new String[]{logchilds.toString()} );
            if ( uncopyable.length == 0 ) {
                sqlUpdateProcedure( "CopyDocs", new String[]{logchilds.toString(), "" + meta_id, "" + doc_menu_no, "" + user.getUserId(), copyPrefix} );
                this.updateLogs( "Childs [" + logchilds.toString() + "] on [" + meta_id + "] copied by user: [" + user.getFullName() + "]" );
            }
            return uncopyable;
        }
        return null;

    }

    public void deleteChilds( int meta_id, int doc_menu_no, UserDomainObject user, String[] childsThisMenu ) {
        DocumentMapper.deleteChilds( this, meta_id, doc_menu_no, user, childsThisMenu );
    }

    /**
     * Archive childs for a menu.
     */
    public void archiveChilds(int meta_id, UserDomainObject user, String childsThisMenu[]) {
        StringBuffer childStr = new StringBuffer('[');

        for (int i = 0; i < childsThisMenu.length; i++) {
            String sqlStr = "update meta\n"
                    + "set archive = 1\n"
                    + "where meta_id = ?";

            sqlUpdateQuery(sqlStr, new String[]{childsThisMenu[i]});
            childStr.append(childsThisMenu[i]);
            if (i < childsThisMenu.length - 1)
                childStr.append(',');
        }
        childStr.append(']');

        this.updateLogs("Childs " + childStr + " from " +
                "[" + meta_id + "] archived by user: [" +
                user.getFullName() + "]");
    }

    /**
     * Check if url doc.
     */
    public String isUrlDoc(int meta_id, UserDomainObject user) {
        String url_ref = null;
        if (DocumentDomainObject.DOCTYPE_URL == getDocType(meta_id)) {
            String sqlStr = "select url_ref from url_docs where meta_id = ?";
            url_ref = sqlQueryStr(sqlStr, new String[]{"" + meta_id});
        }

        return url_ref;
    }

    /**
     * Save a new frameset.
     */
    public void saveNewFrameset(int meta_id, UserDomainObject user, String html) {
        String sqlStr = "insert into frameset_docs (meta_id,frame_set) values(?,?)";

        sqlUpdateQuery(sqlStr, new String[] {""+meta_id, html});

        activateChild(meta_id, user);

        updateLogs("FramesetDoc [" + meta_id + "] created by user: [" +
                user.getFullName() + "]");
    }

    /**
     * Save a frameset
     */
    public void saveFrameset(int meta_id, UserDomainObject user, String html) {
        String sqlStr = "update frameset_docs set frame_set = ? where meta_id = ?";

        sqlUpdateQuery(sqlStr, new String[]{html,""+meta_id});

        this.updateLogs("FramesetDoc [" + meta_id + "] updated by user: [" +
                user.getFullName() + "]");
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
    public String isFramesetDoc( int meta_id, UserDomainObject user ) {

        String htmlStr = null;
        if ( DocumentDomainObject.DOCTYPE_HTML == getDocType( meta_id ) ) {
            String sqlStr = "select frame_set from frameset_docs where meta_id = ?";
            htmlStr = sqlQueryStr( sqlStr, new String[]{"" + meta_id} );
        }
        return htmlStr;

    }

    /**
     * Check if external doc.
     */
    public ExternalDocType isExternalDoc( int meta_id, UserDomainObject user ) {
        ExternalDocType external_doc = null;

        int doc_type = getDocType( meta_id );
        if ( doc_type > 100 ) {
            for ( int i = 0; i < m_ExDoc.length && m_ExDoc[i] != null; i++ )
                if ( m_ExDoc[i].getDocType() == doc_type ) {
                    external_doc = m_ExDoc[i];
                }
        }
        return external_doc;
    }

    /**
     * Activate child to child-table.
     */
    public void activateChild( int meta_id, imcode.server.user.UserDomainObject user ) {

        String sqlStr = "update meta set activate = 1 where meta_id = ?";
        sqlUpdateQuery( sqlStr, new String[]{"" + meta_id} );

        this.updateLogs( "Child [" + meta_id + "] activated  " + "by user: [" + user.getFullName() + "]" );

    }

    public String[] sqlQuery(String sqlQuery, String[] parameters) {
        return SqlHelpers.sqlQuery(m_conPool, sqlQuery, parameters);
    }

    public String sqlQueryStr(String sqlStr, String[] params) {
        return SqlHelpers.sqlQueryStr(m_conPool, sqlStr, params);
    }

    /**
     * Send a sql update query to the database
     */
    public int sqlUpdateQuery(String sqlStr, String[] params) {
        return SqlHelpers.sqlUpdateQuery(m_conPool, sqlStr, params);
    }

    /**
     * The preferred way of getting data from the db.
     * String.trim()'s the results.
     *
     * @param procedure The name of the procedure
     * @param params    The parameters of the procedure
     */
    public String[] sqlProcedure(String procedure, String[] params) {
        return sqlProcedure(procedure, params, true);
    }

    public String[] sqlProcedure( String procedure, String[] params, boolean trim ) {
        return SqlHelpers.sqlProcedure(m_conPool,procedure,params,trim) ;
    }

    /**
     * The preferred way of getting data to the db.
     *
     * @param procedure The name of the procedure
     * @param params    The parameters of the procedure
     * @return updateCount or -1 if error
     */
    public int sqlUpdateProcedure(String procedure, String[] params) {
        return SqlHelpers.sqlUpdateProcedure(m_conPool, procedure, params);
    }

    public String sqlProcedureStr(String procedure, String[] params) {
        return SqlHelpers.sqlProcedureStr(m_conPool, procedure, params);
    }

    public DocumentMapper getDocumentMapper() {
        return documentMapper ;
    }

    public TemplateMapper getTemplateMapper() {
        return templateMapper ;
    }

    public ImcmsAuthenticatorAndUserMapper getUserAndRoleMapper() {
        return imcmsAuthenticatorAndUserMapper;
    }

    /**
     Parse doc replace variables with data, uses two vectors
     */
    public String parseDoc( String htmlStr, java.util.Vector variables, java.util.Vector data ) {
        String[] foo = new String[variables.size()];
        String[] bar = new String[data.size()];
        return imcode.util.Parser.parseDoc( htmlStr, (String[])variables.toArray( foo ), (String[])data.toArray( bar ) );
    }

    /**
     Parse doc replace variables with data , use template
     */
    public String parseDoc(java.util.List variables, String admin_template_name, UserDomainObject user) {
        // FIXME Fugly workaround
        String langPrefix = getUserLangPrefixOrDefaultLanguage(user) ;
        try {
            String htmlStr = fileCache.getCachedFileString( new File( m_TemplateHome, langPrefix + "/admin/" + admin_template_name ) );
            if ( variables == null ) {
                return htmlStr;
            }
            String[] foo = new String[variables.size()];
            return imcode.util.Parser.parseDoc(htmlStr, (String[]) variables.toArray(foo));
        } catch (IOException ex) {
            log.error(ex.toString(), ex);
            return "";
        }
    }

    /**
     Parse doc replace variables with data , use template
     */
    public String parseExternalDoc(java.util.List variables, String external_template_name, UserDomainObject user, String doc_type) {
        // FIXME Fugly workaround
        String langPrefix = getUserLangPrefixOrDefaultLanguage(user) ;
        try {
            String htmlStr = fileCache.getCachedFileString( new File( m_TemplateHome, langPrefix + "/" + doc_type + "/" + external_template_name ) );
            if ( variables == null ) {
                return htmlStr;
            }
            String[] foo = new String[variables.size()];
            return imcode.util.Parser.parseDoc( htmlStr, (String[])variables.toArray( foo ) );
        } catch ( RuntimeException e ) {
            log.error("parseExternalDoc(List, String, String, String): RuntimeException", e);
            throw e;
        } catch ( IOException e ) {
            log.error("parseExternalDoc(List, String, String, String): IOException", e);
            return "";
        }
    }

    /**
     Parse doc replace variables with data , use template
     */
    public String parseExternalDoc(java.util.List variables, String external_template_name, UserDomainObject user, String doc_type, String templateSet) {

        String langPrefix = this.getUserLangPrefixOrDefaultLanguage(user) ;
        
        try {
            String htmlStr = fileCache.getCachedFileString( new File( m_TemplateHome, langPrefix + "/" + doc_type + "/" + templateSet + "/" + external_template_name ) );
            if ( variables == null ) {
                return htmlStr;
            }
            String[] foo = new String[variables.size()];
            return imcode.util.Parser.parseDoc( htmlStr, (String[])variables.toArray( foo ) );
        } catch ( RuntimeException e ) {
            log.error( "parseExternalDoc(List, String, String, String): RuntimeException", e );
            throw e;
        } catch ( IOException e ) {
            log.error( "parseExternalDoc(List, String, String, String): IOException", e );
            throw new RuntimeException(e);
        }
    }

    /**
     * @deprecated Ugly use {@link IMCServiceInterface#parseExternalDoc(java.util.List,String,UserDomainObject,String)}
     *             or something else instead.
     */
    public File getExternalTemplateFolder(int meta_id, UserDomainObject user) {
        int docType = getDocType(meta_id) ;
        String langPrefix = getUserLangPrefixOrDefaultLanguage(user) ;
        return new File(m_TemplateHome, langPrefix + "/" + docType + "/");
    }

    /**
     * Return  templatehome.
     */
    public File getTemplateHome() {
        return m_TemplateHome;
    }

    /**
     * Return url-path to images.
     */
    public String getImageUrl() {
        return m_ImageUrl;
    }

    /**
     * Return url-path to imcmsimages.
     */
    public String getImcmsUrl() {
        return m_ImcmsUrl;
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
        return m_ImcmsPath;
    }

    /**
     * Return  starturl.
     */
    public String getStartUrl() {
        return m_StartUrl;
    }

    /**
     * Return  language.
     */
    public String getDefaultLanguageAsIso639_1() {
        try {
            return LanguageMapper.convert639_2to639_1(defaultLanguageAsIso639_2);
        } catch (LanguageMapper.LanguageNotSupportedException e) {
            log.fatal("No ISO 639-1 representation for the default language. ("+defaultLanguageAsIso639_2+")") ;
            return null;
        }
    }

    public String getDefaultLanguageAsIso639_2() {
        return defaultLanguageAsIso639_2 ;
    }

    // get language prefix for user
    public String getUserLangPrefixOrDefaultLanguage( UserDomainObject user ) {
        String lang_prefix = this.getDefaultLanguageAsIso639_2();
        if ( user != null){
            return user.getLangPrefix();
        }else{
            return lang_prefix;
        }
    }


    /**
     * Increment session counter.
     */
    public int incCounter() {
        m_SessionCounter += 1;
        sqlUpdateProcedure("IncSessionCounter", new String[0]);
        return m_SessionCounter;
    }

    /**
     * Get session counter.
     */
    public int getCounter() {
        return m_SessionCounter;
    }

    /**
     * Set session counter.
     */
    public int setCounter(int value) {
        m_SessionCounter = value;
        this.sqlUpdateProcedure("SetSessionCounterValue", new String[]{"" + value});
        return m_SessionCounter;
    }

    /**
     * Set session counter date.
     */
    public void setCounterDate(String date) {
        m_SessionCounterDate = date;
        this.sqlUpdateProcedure("SetSessionCounterDate", new String[]{date});
    }

    /**
     * Get session counter date.
     */
    public String getCounterDate() {
        return m_SessionCounterDate;
    }

    public Hashtable sqlQueryHash(String sqlQuery, String[] params) {
        return SqlHelpers.sqlQueryHash(m_conPool,sqlQuery, params);
    }

    public Hashtable sqlProcedureHash(String procedure, String[] params) {
        return SqlHelpers.sqlProcedureHash(m_conPool, procedure, params);
    }

    /**
     * Send a procedure to the database and return a multi string array
     */
    public String[][] sqlProcedureMulti(String procedure, String[] params) {
        return SqlHelpers.sqlProcedureMulti(m_conPool, procedure, params);
    }

    public String[][] sqlQueryMulti(String sqlQuery, String[] params) {
        return SqlHelpers.sqlQueryMulti(m_conPool, sqlQuery, params);
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
     CheckAdminRights, returns true if the user is an superadmin. Only an superadmin
     is allowed to create new users
     False if the user isn't an administrator.
     1 = administrator
     0 = superadministrator
     */
    public boolean checkAdminRights( imcode.server.user.UserDomainObject user ) {
        String[][] roles = sqlProcedureMulti( "CheckAdminRights", new String[]{"" + user.getUserId()} );

        for ( int i = 0; i < roles.length; i++ ) {
            String roleId = roles[i][1];
            if ( roleId.equalsIgnoreCase( "0" ) )
                return true;
        }
        return false;
    } // checkAdminRights

    /**
     checkDocAdminRights
     */
    public boolean checkDocAdminRights( int meta_id, UserDomainObject user ) {
        return documentMapper.hasEditPermission( meta_id, user );
    }

    /**
     * checkDocRights
     */
    public boolean checkDocRights(int meta_id, UserDomainObject user) {
        try {
            String[] perms = sqlProcedure("GetUserPermissionSet", new String[]{String.valueOf(meta_id), String.valueOf(user.getUserId())});

            if (perms.length > 0 && Integer.parseInt(perms[0]) < 4) {
                return true;
            } else {
                return false;
            }
        } catch (RuntimeException ex) {
            log.error("Exception in checkDocRights(int,User)", ex);
            throw ex;
        }
    }

    /**
     * Checks to see if a user has any permission of a particular set of permissions for a document.
     *
     * @param meta_id    The document-id
     * @param user       The user
     * @param permission A bitmap containing the permissions.
     */
    public boolean checkDocAdminRightsAny(int meta_id, UserDomainObject user, int permission) {
        try {
            String[] perms = sqlProcedure("GetUserPermissionSet", new String[]{String.valueOf(meta_id), String.valueOf(user.getUserId())});

            int set_id = Integer.parseInt(perms[0]);
            int set = Integer.parseInt(perms[1]);

            if (perms.length > 0
                    && set_id == 0		// User has full permission for this document
                    || (set_id < 3 && ((set & permission) > 0))	// User has at least one of the permissions given.
            ) {
                return true;
            } else {
                return false;
            }
        } catch (RuntimeException ex) {
            log.error("Exception in checkDocAdminRightsAny(int,User,int)", ex);
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
    public boolean checkDocAdminRights(int meta_id, UserDomainObject user, int permission) {
        try {
            String[] perms = sqlProcedure("GetUserPermissionSet", new String[]{String.valueOf(meta_id), String.valueOf(user.getUserId())});

            if (perms.length == 0) {
                return false;
            }

            int set_id = Integer.parseInt(perms[0]);
            int set = Integer.parseInt(perms[1]);

            if (set_id == 0		// User has full permission for this document
                    || (set_id < 3 && ((set & permission) == permission))	// User has all the permissions given.
            ) {
                return true;
            } else {
                return false;
            }
        } catch (RuntimeException ex) {
            log.error("Exception in checkDocAdminRights(int,User,int)", ex);
            throw ex;
        }
    }

    /**
     * Gets the users most privileged permission_set for the document.
     *
     * @param meta_id The document-id
     * @param user_id The user_id
     * @return the most privileged permission_set a user has for the document.
     */
    public int getUserHighestPermissionSet(int meta_id, int user_id) {
        try {
            String[] perms = sqlProcedure("GetUserPermissionSet", new String[]{String.valueOf(meta_id), String.valueOf(user_id)});

            if (perms.length == 0) {
                return IMCConstants.DOC_PERM_SET_NONE;//nothing was returned so give no rights at all.
            }

            int set_id = Integer.parseInt(perms[0]);

            switch (set_id) {
                case IMCConstants.DOC_PERM_SET_FULL:         // User has full permission for this document
                case IMCConstants.DOC_PERM_SET_RESTRICTED_1: // User has restricted 1 permission for this document
                case IMCConstants.DOC_PERM_SET_RESTRICTED_2: // User has restricted 2 permission for this document
                case IMCConstants.DOC_PERM_SET_READ:         // User has only read permission for this document
                    return set_id;                          // We have a valid permission-set-id. Return it.

                default:                                     // We didn't get a valid permission-set-id.
                    return DOC_PERM_SET_NONE;               // User has no permission at all for this document
            }

        } catch (RuntimeException ex) {
            log.error("Exception in getUserHighestPermissionSet(int,int)", ex);
            throw ex;
        }
    }

    /**
     * save template to disk
     */
    public int saveTemplate(String name, String file_name, byte[] template, boolean overwrite, String lang_prefix) {
        String sqlStr = "";

        // check if template exists
        sqlStr = "select template_id from templates where simple_name = ?";
        String templateId = sqlQueryStr(sqlStr, new String[]{name});
        if (null == templateId) {

            // get new template_id
            sqlStr = "select max(template_id) + 1 from templates\n";
            templateId = sqlQueryStr(sqlStr, new String[0]);

            sqlStr = "insert into templates values (?,?,?,?,0,0,0)";
            sqlUpdateQuery(sqlStr, new String[]{templateId,file_name,name,lang_prefix});
        } else { //update
            if (!overwrite) {
                return -1;
            }

            sqlStr = "update templates set template_name = ? where template_id = ?";
            sqlUpdateQuery(sqlStr, new String[]{file_name, templateId});
        }

        File f = new File(m_TemplateHome, "text/" + templateId + ".html");

        try {
            FileOutputStream fw = new FileOutputStream(f);
            fw.write(template);
            fw.flush();
            fw.close();

        } catch (IOException e) {
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
    public Object[] getDemoTemplate(int template_id) throws IOException {
        //String str = "" ;
        StringBuffer str = new StringBuffer();
        BufferedReader fr = null;
        String suffix = null;
        String[] suffixList =
                {"jpg", "jpeg", "gif", "png", "html", "htm"};

        for (int i = 0; i < suffixList.length; i++) { // Looking for a template with one of six suffixes
            File fileObj = new File(m_TemplateHome, "/text/demo/" + template_id + "." + suffixList[i]);
            long date = 0;
            long fileDate = fileObj.lastModified();
            if (fileObj.exists() && fileDate > date) {
                // if a template was not properly removed, the template
                // with the most recens modified-date is returned
                date = fileDate;

                try {
                    fr = new BufferedReader(new InputStreamReader(new FileInputStream(fileObj), "8859_1"));
                    suffix = suffixList[i];
                } catch (IOException e) {
                    return null; //Could not read
                }
            } // end IF
        } // end FOR

        char[] buffer = new char[4096];
        try {
            int read;
            while ((read = fr.read(buffer, 0, 4096)) != -1) {
                str.append(buffer, 0, read);
            }
            fr.close();
        } catch (IOException e) {
            return null;
        } catch (NullPointerException e) {
            return null;
        }

        return new Object[]{suffix, str.toString().getBytes("8859_1")}; //return the buffer
    }

    /**
     * get template
     */
    public byte[] getTemplateData(int template_id) throws IOException {
        String str = "";

        BufferedReader fr;

        try {
            fr = new BufferedReader(new FileReader(m_TemplateHome + "/text/" + template_id + ".html"));
        } catch (FileNotFoundException e) {
            log.info("Failed to find template number " + template_id);
            return null;
        }

        try {
            int temp;
            while ((temp = fr.read()) != -1) {
                str += (char) temp;
            }
        } catch (IOException e) {
            log.info("Failed to read template number " + template_id);
            return null;
        }

        return str.getBytes("8859_1");
    }

    /**
     save demo template
     */
    public void saveDemoTemplate( int template_id, byte[] data, String suffix ) throws IOException {

            deleteDemoTemplate(template_id);

            FileOutputStream fw = new FileOutputStream(m_TemplateHome + "/text/demo/" + template_id + "." + suffix);
            fw.write(data);
            fw.flush();
            fw.close();
    }

    /** get server date
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
        File demoDir = new File( m_TemplateHome + "/text/demo/" );

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
    public void deleteDemoTemplate(int template_id) throws IOException {

        File demoTemplateDirectory = new File( new File( m_TemplateHome, "text" ), "demo" );
        File[] demoTemplates = demoTemplateDirectory.listFiles();
        for ( int i = 0; i < demoTemplates.length; i++ ) {
            File demoTemplate = demoTemplates[i];
            String demoTemplateFileName = demoTemplate.getName() ;
            if (demoTemplateFileName.startsWith(template_id+".")) {
                if(!demoTemplate.delete()){
                    throw new IOException("fail to deleate");
                }
            }
        }
    }

    /**
     * Fetch the systemdata from the db
     */
    private SystemData getSystemDataFromDb() {

        /** Fetch everything from the DB */
        String startDocument = sqlProcedureStr("StartDocGet", new String[0]);
        String serverMaster[] = sqlProcedure("ServerMasterGet", new String[0]);
        String webMaster[] = sqlProcedure("WebMasterGet", new String[0]);
        String systemMessage = sqlProcedureStr("SystemMessageGet", new String[0]);

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
     Return a file relative to the webapps. ex ../templates/swe/admin/search/original
     **/
    public String getSearchTemplate( String path ) throws IOException {
        return fileCache.getCachedFileString( new File( m_TemplateHome, path ) );
    }


    /**
     Retrieve the texts for a internalDocument
     @param meta_id The id of the internalDocument.
     @return A Map (Integer -> TextDocumentTextDomainObject) with all the  texts in the internalDocument.
     **/
    public Map getTexts( int meta_id ) {

        // Now we'll get the texts from the db.
        String[] texts = sqlProcedure( "GetTexts", new String[]{String.valueOf( meta_id )}, false );
        Map textMap = new HashMap();
        Iterator it = Arrays.asList( texts ).iterator();
        while ( it.hasNext() ) {
            try {
                it.next(); // the key, not needed
                String txt_no = (String)it.next();
                int txt_type = Integer.parseInt( (String)it.next() );
                String value = (String)it.next();
                textMap.put( txt_no, new TextDocumentTextDomainObject( value, txt_type ) );
            } catch ( NumberFormatException e ) {
                log.error( "SProc 'GetTexts " + meta_id + "' returned a non-number where a number was expected.", e );
                return null;
            }
        }
        return textMap;
    }

    /**
     Get the data for one internalDocument
     @param meta_id The id fore the wanted internalDocument
     @return a imcode.server.internalDocument.Document representation of the internalDocument, or null if there was none.
     @throws IndexOutOfBoundsException if there was no such internalDocument.
     **/
    public DocumentDomainObject getDocument( int meta_id ) throws IndexOutOfBoundsException {
        return documentMapper.getDocument( meta_id );
    }

    /**
     Set a user flag
     **/
    public void setUserFlag( UserDomainObject user, String flagName ) {
        int userId = user.getUserId();

        sqlUpdateProcedure( "SetUserFlag", new String[]{"" + userId, flagName} );
    }

    /**
     * Returns an array with with all the documenttypes stored in the database
     * the array consists of pairs of id:, value. Suitable for parsing into select boxes etc.
     */
    public String[][] getDocumentTypesInList(String langPrefixStr) {
        return sqlProcedureMulti("GetDocTypes", new String[]{langPrefixStr});
    }

    public boolean checkUserDocSharePermission(UserDomainObject user, int meta_id) {
        return sqlProcedure("CheckUserDocSharePermission", new String[]{"" + user.getUserId(), "" + meta_id}).length > 0;
    }

    /**
     Unset a user flag
     **/
    public void unsetUserFlag( UserDomainObject user, String flagName ) {
        int userId = user.getUserId();

        sqlUpdateProcedure( "UnsetUserFlag", new String[]{"" + userId, flagName} );
    }

    /**
     * Return a file relative to the fortune-path.
     */
    public String getFortune(String path) throws IOException {
        return fileCache.getCachedFileString(new File(m_FortunePath, path));
    }

    /**
     * Get a list of quotes
     *
     * @param quoteListName The name of the quote-List.
     * @return the quote-List.
     */
    public List getQuoteList(String quoteListName) {
        List theList = new LinkedList();
        try {
            File file = new File(m_FortunePath, quoteListName);
            StringReader reader = new StringReader(fileCache.getUncachedFileString(file));
            QuoteReader quoteReader = new QuoteReader(reader);
            for (Quote quote; null != (quote = quoteReader.readQuote());) {
                theList.add(quote);
            }
            reader.close();
        } catch (IOException ignored) {
            log.debug("Failed to load quote-list " + quoteListName);
        }
        return theList;
    }

    /**
     * Set a quote-list
     *
     * @param quoteListName The name of the quote-List.
     * @param quoteList     The quote-List
     */
    public void setQuoteList(String quoteListName, List quoteList) throws IOException {
        FileWriter writer = new FileWriter(new File(m_FortunePath, quoteListName));
        QuoteWriter quoteWriter = new QuoteWriter(writer);
        Iterator quotesIterator = quoteList.iterator();
        while (quotesIterator.hasNext()) {
            quoteWriter.writeQuote((Quote) quotesIterator.next());
        }
        writer.flush();
        writer.close();
    }


    /**
     * @return a List of Polls
     */
    public List getPollList(String pollListName) {
        List theList = new LinkedList();
        try {
            File file = new File(m_FortunePath, pollListName);
            StringReader reader = new StringReader(fileCache.getUncachedFileString(file));
            PollReader pollReader = new PollReader(reader);
            for (Poll poll; null != (poll = pollReader.readPoll());) {
                theList.add(poll);
            }
            reader.close();
        } catch (IOException ignored) {
            log.debug("Failed to load poll-list " + pollListName);
        }
        return theList;
    }

    /**
     * Set a poll-list
     *
     * @param pollListName The name of the poll-List.
     * @param pollList     The poll-List
     */
    public void setPollList(String pollListName, List pollList) throws IOException {
        FileWriter writer = new FileWriter(new File(m_FortunePath, pollListName));
        PollWriter pollWriter = new PollWriter(writer);
        Iterator pollIterator = pollList.iterator();
        while (pollIterator.hasNext()) {
            pollWriter.writePoll((Poll) pollIterator.next());
        }
        writer.flush();
        writer.close();
    }

    /**
     * @return the filename for a fileupload-document, or null if the document isn't a fileupload-docuemnt. *
     */
    public String getFilename(int meta_id) {
        return sqlProcedureStr("GetFileName", new String[]{"" + meta_id});
    }

    /**
     Get all possible userflags
     **/
    public Map getUserFlags() {
        String[] dbData = sqlProcedure("GetUserFlags", new String[0]);

        return getUserFlags( dbData );
    }

    /**
     Get all userflags for a single user
     **/
    public Map getUserFlags( UserDomainObject user ) {
        int userId = user.getUserId();
        String[] dbData = sqlProcedure( "GetUserFlagsForUser", new String[]{String.valueOf( userId )} );

        return getUserFlags( dbData );
    }

    /**
     Get all userflags of a single type
     **/
    public Map getUserFlags( int type ) {
        String[] dbData = sqlProcedure( "GetUserFlagsOfType", new String[]{String.valueOf( type )} );

        return getUserFlags( dbData );
    }

    /**
     Get all userflags for a single user of a single type
     **/
    public Map getUserFlags( UserDomainObject user, int type ) {
        int userId = user.getUserId();
        String[] dbData = sqlProcedure( "GetUserFlagsForUserOfType", new String[]{String.valueOf( userId ), String.valueOf( type )} );

        return getUserFlags( dbData );
    }

    /** Used by the other getUserFlags*-methods to put the database-data in a Set **/
    private Map getUserFlags( String dbData[] ) {
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
        documentMapper.sqlUpdateModifiedDatesOnDocumentAndItsParent( metaId, dateTime );
    }

}
