package imcode.server;

import imcode.readrunner.ReadrunnerUserData;
import imcode.server.db.DBConnect;
import imcode.server.db.DatabaseService;
import imcode.server.db.sql.ConnectionPool;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentMapper;
import imcode.server.document.DatabaseAccessor;
import imcode.server.parser.ParserParameters;
import imcode.server.parser.TextDocumentParser;
import imcode.server.user.*;
import imcode.util.*;
import imcode.util.fortune.*;
import imcode.util.poll.PollHandlingSystem;
import imcode.util.poll.PollHandlingSystemImpl;
import imcode.util.shop.ShoppingOrderSystem;
import imcode.util.shop.ShoppingOrderSystemImpl;
import org.apache.log4j.Logger;

import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 Main services for the Imcode Net Server.
 Made final, since only a complete and utter moron would want to extend it.
 **/
final public class IMCService implements IMCServiceInterface, IMCConstants {
    public ConnectionPool getConnectionPool() {
        return m_conPool;
    }

    private DatabaseService m_databaseService;
    private final ConnectionPool m_conPool; // inet pool of connections
    private TextDocumentParser textDocParser;

    private File m_TemplateHome;           // template home
    private File m_IncludePath;
    private File m_FortunePath;
    private File m_FilePath;
    private String m_StartUrl;			   // start url
    private String m_ServletUrl;			   // servlet url
    private String m_ImageUrl;            // image folder
    private String m_Language = "";      // language
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

    static {
        mainLog.info( "Main log started." );
    }

    /**
     * Contructs an IMCService object.
     */
    public IMCService( DatabaseService databaseService, Properties props ) {
        super();
        m_databaseService = databaseService;
        m_conPool = databaseService.getConnectionPool();
        initMemberFields( props );
        initAuthenticatorsAndUserAndRoleMappers( props );
        initDocumentMapper();
    }

    private void initMemberFields( Properties props ) {
        sysData = getSystemDataFromDb();

        String templatePathString = props.getProperty( "TemplatePath" ).trim();
        m_TemplateHome = Utility.getAbsolutePathFromString( templatePathString );
        log.info( "TemplatePath: " + m_TemplateHome );

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

        m_ServletUrl = props.getProperty( "ServletUrl" ).trim(); //FIXME: Get from webserver, or get rid of if possible.
        log.info( "ServletUrl: " + m_ServletUrl );

        // FIXME: Get imageurl from webserver somehow. The user-object, perhaps?
        m_ImageUrl = props.getProperty( "ImageUrl" ).trim(); //FIXME: Get from webserver, or get rid of if possible.
        log.info( "ImageUrl: " + m_ImageUrl );

        m_Language = props.getProperty( "DefaultLanguage" ).trim(); //FIXME: Get from DB
        log.info( "DefaultLanguage: " + m_Language );

        String externalDocTypes = props.getProperty( "ExternalDoctypes" ).trim(); //FIXME: Get rid of, if possible.
        log.info( "ExternalDoctypes: " + externalDocTypes );

        StringTokenizer doc_types = new StringTokenizer( externalDocTypes, ";", false );
        m_ExDoc = new ExternalDocType[doc_types.countTokens()];
        try {
            for( int doc_count = 0; doc_types.hasMoreTokens(); ++doc_count ) {
                StringTokenizer tempStr = new StringTokenizer( doc_types.nextToken(), ":", false );
                String items[] = new String[tempStr.countTokens()];
                for( int i = 0; tempStr.hasMoreTokens(); ++i ) {
                    items[i] = tempStr.nextToken();
                }
                m_ExDoc[doc_count] = new ExternalDocType( Integer.parseInt( items[0] ), items[1], items[2], "" );
            }
        } catch( NoSuchElementException e ) {
            e.printStackTrace();
        }

        m_SessionCounter = m_databaseService.sproc_GetCurrentSessionCounter();
        m_SessionCounterDate = m_databaseService.sproc_GetCurrentSessionCounterDate();

        log.info( "SessionCounter: " + m_SessionCounter );
        log.info( "SessionCounterDate: " + m_SessionCounterDate );

        textDocParser = new TextDocumentParser( this, m_conPool, m_TemplateHome, m_IncludePath, m_ImageUrl, m_ServletUrl );

    }

    private void initDocumentMapper() {
        documentMapper = new DocumentMapper( this, imcmsAuthenticatorAndUserMapper );
    }

    private void initAuthenticatorsAndUserAndRoleMappers( Properties props ) {
        String externalAuthenticatorName = props.getProperty( "ExternalAuthenticator" );
        String externalUserAndRoleMapperName = props.getProperty( "ExternalUserAndRoleMapper" );

        Authenticator externalAuthenticator = null;
        UserAndRoleMapper externalUserAndRoleMapper = null;

        PrefixRemovedProperties authenticatorPropertiesSubset = new PrefixRemovedProperties( props, "ExternalAuthenticator." );
        PrefixRemovedProperties userAndRoleMapperPropertiesSubset = new PrefixRemovedProperties( props, "ExternalUserAndRoleMapper." );

        if( null != externalAuthenticatorName && null != externalUserAndRoleMapperName ) {
            log.info( "ExternalAuthenticator: " + externalAuthenticatorName );
            log.info( "ExternalUserAndRoleMapper: " + externalUserAndRoleMapperName );
            externalAuthenticator = initExternalAuthenticator( externalAuthenticatorName, authenticatorPropertiesSubset );
            externalUserAndRoleMapper = initExternalUserAndRoleMapper( externalUserAndRoleMapperName, userAndRoleMapperPropertiesSubset );
            if( null == externalAuthenticator || null == externalUserAndRoleMapper ) {
                log.error( "Failed to initialize both authenticator and user-and-role-documentMapper, using default implementations." );
                externalAuthenticator = null;
                externalUserAndRoleMapper = null;
            }
        } else if( null == externalAuthenticatorName && null == externalUserAndRoleMapperName ) {
            log.info( "ExternalAuthenticator not set." );
            log.info( "ExternalUserAndRoleMapper not set." );
        } else {
            log.error( "External authenticator and external usermapper should both be either set or not set. Using default implementation." );
            externalAuthenticatorName = null;
            externalUserAndRoleMapperName = null;
        }
        imcmsAuthenticatorAndUserMapper = new ImcmsAuthenticatorAndUserMapper( this );
        externalizedImcmsAuthAndMapper = new ExternalizedImcmsAuthenticatorAndUserMapper( imcmsAuthenticatorAndUserMapper, externalAuthenticator, externalUserAndRoleMapper, getDefaultLanguage() );
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
        if( userAuthenticates ) {
            result = user;
            mainLog.info( "->User '" + (login) + "' successfully logged in." );
        } else if( null == user ) {
            mainLog.info( "->User '" + (login) + "' failed to log in: User not found." );
        } else if( !user.isActive() ) {
            mainLog.info( "->User '" + (login) + "' failed to log in: User deactivated." );
        } else {
            mainLog.info( "->User '" + (login) + "' failed to log in: Wrong password." );
        }

        return result;
    }

    private UserAndRoleMapper initExternalUserAndRoleMapper( String externalUserAndRoleMapperName, Properties userAndRoleMapperPropertiesSubset ) {
        UserAndRoleMapper externalUserAndRoleMapper = null;
        if( null == externalUserAndRoleMapperName ) {
            externalUserAndRoleMapper = null;
        } else if( EXTERNAL_USER_AND_ROLE_MAPPER_LDAP.equalsIgnoreCase( externalUserAndRoleMapperName ) ) {
            try {
                externalUserAndRoleMapper = new LdapUserAndRoleMapper( userAndRoleMapperPropertiesSubset );
            } catch( LdapUserAndRoleMapper.LdapInitException e ) {
                log.error( "LdapUserAndRoleMapper could not be created, using default user and role documentMapper.", e );
            }
        } else {
            externalUserAndRoleMapper = (UserAndRoleMapper)createInstanceOfClass( externalUserAndRoleMapperName );
        }
        return externalUserAndRoleMapper;
    }

    private Authenticator initExternalAuthenticator( String externalAuthenticatorName, Properties authenticatorPropertiesSubset ) {
        Authenticator externalAuthenticator = null;
        if( null == externalAuthenticatorName ) {
            externalAuthenticator = null;
        } else if( EXTERNAL_AUTHENTICATOR_SMB.equalsIgnoreCase( externalAuthenticatorName ) ) {
            externalAuthenticator = new SmbAuthenticator( authenticatorPropertiesSubset );
        } else if( EXTERNAL_AUTHENTICATOR_LDAP.equalsIgnoreCase( externalAuthenticatorName ) ) {
            try {
                externalAuthenticator = new LdapUserAndRoleMapper( authenticatorPropertiesSubset );
            } catch( LdapUserAndRoleMapper.LdapInitException e ) {
                log.error( "LdapUserAndRoleMapper could not be created, using default user and role documentMapper.", e );
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
        } catch( Exception e ) {
            log.error( "Could not create instance of class '" + className + "'.", e );
        }
        return instance;
    }

    public UserDomainObject getUserById( int id ) {
        ImcmsAuthenticatorAndUserMapper userMapper = new ImcmsAuthenticatorAndUserMapper( this );
        UserDomainObject result = userMapper.getUser( id );
        return result;
    }

    // Fixme! public bolean addUser(User user) save a user in db
    //		  public bolean updateUser(User user) save a user in db


    //Check if user has a special adminRole
    public boolean checkUserAdminrole( int userId, int adminRole ) {
        String[] adminrole = sqlProcedure( "checkUserAdminrole ", new String[]{"" + userId, "" + adminRole} );
        if( adminrole.length > 0 ) {
            if( ("" + adminRole).equals( adminrole[0] ) ) {
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

        if( permissions.length == 0 ) {
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

        } catch( IOException e ) {
            log.error( e.toString() );
            return "";
        }

        int user_permission_set_id = Integer.parseInt( permissions[0] );
        int user_permission_set = Integer.parseInt( permissions[1] );

        // Replace #getMetaId# with meta_id

        imcode.util.AdminButtonParser doc_tags = new imcode.util.AdminButtonParser( new File( m_TemplateHome, lang_prefix + "/admin/adminbuttons/adminbutton" + doc_type + "_" ).toString(), ".html", user_permission_set_id, user_permission_set );

        doc_tags.put( "getMetaId", meta_id );
        Parser.parseTags( tempbuffer, '#', " <>\n\r\t", doc_tags, true, 1 );

        AdminButtonParser tags = new imcode.util.AdminButtonParser( new File( m_TemplateHome, lang_prefix + "/admin/adminbuttons/adminbutton_" ).toString(), ".html", user_permission_set_id, user_permission_set );

        tags.put( "getMetaId", meta_id );
        tags.put( "doc_buttons", tempbuffer.toString() );

        String doctypeStr = sqlQueryStr( "select type from doc_types where doc_type = " + doc_type );
        tags.put( "doc_type", doctypeStr );

        // if user is superadmin or useradmin lets add superadmin button
        if( checkAdminRights( user ) || checkUserAdminrole( user.getUserId(), 2 ) ) {
            tags.put( "superadmin", superadmin.toString() );
        } else {
            tags.put( "superadmin", "" );
        }

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
     Store the given IMCText in the DB.
     @param user		The user
     @param meta_id		The id of the page
     @param txt_no		The id of the text in the page.
     @param text		The text.
     @param text_type		The text_type

     Supported text_types is:

     pollquestion-n		      where n represent the questíon number in this document

     pollanswer-n-m		          where n represent the questíon number in this document
     and m represent the answer number in question number n

     pollpointanswer-n-m			  where n represent the questíon number in this document
     and m represent the answer number in question number n

     pollparameter-popup_frequency    default(0) when > 0 show this poll as a popup on every new session that is a multiple
     of the frequens.

     pollparameter-cookie			  default(0) user is allowed to fill in the poll more then once.
     (1) = set cookie, if cookie exist on client don't allow more answers from that computer.

     pollparameter-hideresults		  default(0) if 1 then we don't send result to browser only a confimation text.

     pollparameter-confirmation_text  message to send back to browser as confirmation of poll participation.

     pollparameter-email_recipients   email adress to reciver of result from free-text answers.

     pollparameter-result_template    template to use when return the result

     pollparameter-name			  name for this poll
     pollparameter-description		  description for this poll

     **/

    public void saveText( UserDomainObject user, int meta_id, int txt_no, IMCText text, String text_type ) {
        documentMapper.saveText( text, meta_id, txt_no, user, text_type );
    }

    /**
     Retrieve a text from the db.
     @param meta_id The id of the page.
     @param no      The id of the text in the page.
     @return The text from the db, or null if there was none.
     **/
    public IMCText getText( int meta_id, int no ) {
        return documentMapper.getText( meta_id, no );
    }

    /**
     * Save an imageref.
     */
    public void saveImage( int meta_id, UserDomainObject user, int img_no, imcode.server.Image image ) {
        String sqlStr = "";

        // create a db connection an get meta data
        DBConnect dbc = new DBConnect( m_conPool );
        dbc.getConnection();
        sqlStr = "select * from images where meta_id = " + meta_id + " and name = " + img_no;
        dbc.setSQLString( sqlStr );
        dbc.createStatement();
        if( dbc.executeQuery().size() > 0 ) {
            sqlStr = "update images";
            sqlStr += " set imgurl      = '" + image.getImageRef() + "'";
            sqlStr += ",width       = " + image.getImageWidth();
            sqlStr += ",height      = " + image.getImageHeight();
            sqlStr += ",border      = " + image.getImageBorder();
            sqlStr += ",v_space     = " + image.getVerticalSpace();
            sqlStr += ",h_space     = " + image.getHorizontalSpace();
            sqlStr += ",image_name  = '" + image.getImageName() + "'";
            sqlStr += ",target      = '" + image.getTarget() + "'";
            sqlStr += ",target_name = '" + image.getTargetName() + "'";
            sqlStr += ",align       = '" + image.getImageAlign() + "'";
            sqlStr += ",alt_text    = '" + image.getAltText() + "'";
            sqlStr += ",low_scr     = '" + image.getLowScr() + "'";
            sqlStr += ",linkurl     = '" + image.getImageRefLink() + "'";
            sqlStr += "	where meta_id = " + meta_id;
            sqlStr += " and name = " + img_no;

            dbc.setSQLString( sqlStr );
            dbc.createStatement();
            dbc.executeUpdateQuery();
            dbc.clearResultSet();

        } else {
            sqlStr = "insert into images (imgurl, width, height, border, v_space, h_space, image_name, target, target_name, align, alt_text, low_scr, linkurl, meta_id, name)" + " values('" + image.getImageRef() + "'";
            sqlStr += "," + image.getImageWidth();
            sqlStr += "," + image.getImageHeight();
            sqlStr += "," + image.getImageBorder();
            sqlStr += "," + image.getVerticalSpace();
            sqlStr += "," + image.getHorizontalSpace();
            sqlStr += ",'" + image.getImageName() + "'";
            sqlStr += ",'" + image.getTarget() + "'";
            sqlStr += ",'" + image.getTargetName() + "'";
            sqlStr += ",'" + image.getImageAlign() + "'";
            sqlStr += ",'" + image.getAltText() + "'";
            sqlStr += ",'" + image.getLowScr() + "'";
            sqlStr += ",'" + image.getImageRefLink() + "'";
            sqlStr += "," + meta_id;
            sqlStr += "," + img_no + ")";

            dbc.setSQLString( sqlStr );
            dbc.createStatement();
            dbc.executeUpdateQuery();
            dbc.clearResultSet();
        }

        this.updateLogs( "ImageRef " + img_no + " =" + image.getImageRef() + " in  " + "[" + meta_id + "] modified by user: [" + user.getFullName() + "]" );

        // close connection
        dbc.closeConnection();
        dbc = null;

    }

    /**
     * Delete a doc and all data related. Delete from db and file system.
     */
    /* Fixme:  delete doc from plugin db */
    public void deleteDocAll( int meta_id, UserDomainObject user ) {
        String sqlStr = "DocumentDelete " + meta_id;

        String filename = meta_id + "_se";
        File file = new File( m_FilePath, filename );
        //System.out.println("FilePath: " + file.toString()) ;

        //If meta_id is a file document we have to delete the file from file system
        if( file.exists() ) {
            file.delete();
        }

        // Create a db connection and execte sp DocumentDelete on meta_id
        DBConnect dbc = new DBConnect( m_conPool );
        dbc.getConnection();
        dbc.setSQLString( sqlStr );
        dbc.createStatement();
        dbc.executeUpdateQuery();
        this.updateLogs( "Document  " + "[" + meta_id + "] ALL deleted by user: [" + user.getFullName() + "]" );

        //close connection
        dbc.closeConnection();
        dbc = null;

    }

    /**
     * Add a existing doc.
     */
    public void addExistingDoc( int meta_id, UserDomainObject user, int existing_meta_id, int doc_menu_no ) {

        String sqlStr = "AddExistingDocToMenu  " + meta_id + ", " + existing_meta_id + ", " + doc_menu_no;
        int addDoc = sqlUpdateProcedure( sqlStr );

        if( 1 == addDoc ) {	// if existing doc is added to the menu
            this.updateLogs( "(AddExisting) Child links for [" + meta_id + "] updated by user: [" + user.getFullName() + "]" );
        }
    }

    public void saveManualSort( int meta_id, UserDomainObject user, java.util.Vector childs, java.util.Vector sort_no ) {
        String sqlStr = "";

        // create a db connection
        DBConnect dbc = new DBConnect( m_conPool );
        dbc.getConnection();

        //	 m_output.append("Childs"  + childs.toString() + "\n");
        //	 m_output.append("sort_no" + sort_no.toString() + "\n");


        // update child table
        for( int i = 0; i < childs.size(); i++ ) {
            sqlStr = "update childs\n";
            sqlStr += "set manual_sort_order = " + sort_no.elementAt( i ).toString() + "\n";
            sqlStr += "where meta_id = " + meta_id + " and \n";
            sqlStr += "to_meta_id=" + childs.elementAt( i ).toString();
            dbc.setSQLString( sqlStr );
            dbc.createStatement();
            dbc.executeUpdateQuery();
        }

        //		m_output.append(" Done \n");


        this.updateLogs( "Child manualsort for [" + meta_id + "] updated by user: [" + user.getFullName() + "]" );


        //close connection
        dbc.closeConnection();
        dbc = null;

    }

    /**
     * Delete childs from a menu.
     */
    public void deleteChilds( int meta_id, int menu, UserDomainObject user, String childsThisMenu[] ) {
        String sqlStr = "";
        String childStr = "[";
        // create a db connection an get meta data
        DBConnect dbc = new DBConnect( m_conPool );
        dbc.getConnection();

        for( int i = 0; i < childsThisMenu.length; i++ ) {
            sqlStr = "delete from childs\n";
            sqlStr += " where to_meta_id =" + childsThisMenu[i] + "\n";
            sqlStr += " and meta_id = " + meta_id;
            sqlStr += " and menu_sort = " + menu;


            //	sqlStr += "delete from meta where meta_id ="  + meta_id  + "\n" ;
            //  sqlStr += "delete from text_docs where meta_id ="  + meta_id  + "\n" ;
            //	sqlStr += "delete from texts where meta_id ="  + meta_id  + "\n" ;



            dbc.setSQLString( sqlStr );
            dbc.createStatement();
            dbc.executeUpdateQuery();

            childStr += childsThisMenu[i];
            if( i < childsThisMenu.length - 1 )
                childStr += ",";
        }
        childStr += "]";

        this.updateLogs( "Childs " + childStr + " from " + "[" + meta_id + "] deleted by user: [" + user.getFullName() + "]" );

        //close connection
        dbc.closeConnection();
        dbc = null;
    }

    /**
     Makes copies of the documents given in the String-array, and inserts them into the given document and menu.
     If one of the documents couldn't be copied for some reason, no documents are copied, and the uncopyable
     documents are returned.

     @param meta_id The document to insert into
     @param doc_menu_no The menu to insert into
     @param user The user
     @param childsThisMenu The id's to copy.

     @return A String array containing the meta-ids of uncopyable pages.
     **/
    public String[] copyDocs( int meta_id, int doc_menu_no, UserDomainObject user, String[] childsThisMenu, String copyPrefix ) {

        if( childsThisMenu != null && childsThisMenu.length > 0 ) {

            StringBuffer logchilds = new StringBuffer( childsThisMenu[0] );
            for( int i = 1; i < childsThisMenu.length; ++i ) {
                logchilds.append( "," + childsThisMenu[i] );
            }
            String[] uncopyable = sqlProcedure( "CheckForFileDocs", new String[]{logchilds.toString()} );
            if( uncopyable.length == 0 ) {
                sqlUpdateProcedure( "CopyDocs", new String[]{logchilds.toString(), "" + meta_id, "" + doc_menu_no, "" + user.getUserId(), copyPrefix} );
                this.updateLogs( "Childs [" + logchilds.toString() + "] on [" + meta_id + "] copied by user: [" + user.getFullName() + "]" );
            }
            return uncopyable;
        }
        return null;

    }

    /**
     * Archive childs for a menu.
     **/
    public void archiveChilds( int meta_id, UserDomainObject user, String childsThisMenu[] ) {
        String sqlStr = "";
        String childStr = "[";
        // create a db connection an get meta data
        DBConnect dbc = new DBConnect( m_conPool );
        dbc.getConnection();

        for( int i = 0; i < childsThisMenu.length; i++ ) {
            sqlStr = "update meta";
            sqlStr += " set archive = 1";
            sqlStr += " where meta_id =" + childsThisMenu[i] + "\n";

            dbc.setSQLString( sqlStr );
            dbc.createStatement();
            dbc.executeUpdateQuery();
            childStr += childsThisMenu[i];
            if( i < childsThisMenu.length - 1 )
                childStr += ",";
        }
        childStr += "]";

        this.updateLogs( "Childs " + childStr + " from " + "[" + meta_id + "] archived by user: [" + user.getFullName() + "]" );

        //close connection
        dbc.closeConnection();
        dbc = null;
    }

    /**
     * Check if url doc.
     */
    public imcode.server.Table isUrlDoc( int meta_id, UserDomainObject user ) {
        String sqlStr = "";
        imcode.server.Table url_doc;

        DBConnect dbc = new DBConnect( m_conPool );
        dbc.getConnection();

        sqlStr = "select doc_type from meta where meta_id = " + meta_id;
        dbc.setSQLString( sqlStr );
        dbc.createStatement();
        Vector vec_doc_type = dbc.executeQuery();
        dbc.clearResultSet();

        if( Integer.parseInt( vec_doc_type.elementAt( 0 ).toString() ) == 5 ) {
            sqlStr = "select * from url_docs where meta_id = " + meta_id;
            dbc.setSQLString( sqlStr );
            dbc.createStatement();
            url_doc = new Table( dbc.executeQuery() );
            url_doc.addFieldNames( dbc.getMetaData() );
            dbc.clearResultSet();
        } else
            url_doc = null;

        //close connection
        dbc.closeConnection();
        dbc = null;

        return url_doc;

    }

    /**
     * Save a new frameset.
     */
    public void saveNewFrameset( int meta_id, UserDomainObject user, imcode.server.Table doc ) {
        String sqlStr = "";

        // create a db connection an get meta data
        DBConnect dbc = new DBConnect( m_conPool );
        dbc.getConnection();


        // create new url doc
        sqlStr = "insert into frameset_docs(meta_id,frame_set)\n";
        sqlStr += "values(" + meta_id + ",'" + doc.getString( "frame_set" ) + "')";

        dbc.setSQLString( sqlStr );
        dbc.createStatement();
        dbc.executeUpdateQuery();

        // close connection
        dbc.closeConnection();
        dbc = null;

        this.activateChild( meta_id, user );

        this.updateLogs( "FramesetDoc [" + meta_id + "] created by user: [" + user.getFullName() + "]" );

    }

    /**
     * Save a frameset
     */
    public void saveFrameset( int meta_id, UserDomainObject user, imcode.server.Table doc ) {
        String sqlStr = "";

        // create a db connection an get meta data
        DBConnect dbc = new DBConnect( m_conPool );
        dbc.getConnection();


        // create new url doc
        sqlStr = "update frameset_docs\n";
        sqlStr += "set frame_set ='" + doc.getString( "frame_set" ) + "'\n";
        sqlStr += "where meta_id  = " + meta_id;

        dbc.setSQLString( sqlStr );
        dbc.createStatement();
        dbc.executeUpdateQuery();

        // close connection
        dbc.closeConnection();

        this.updateLogs( "FramesetDoc [" + meta_id + "] updated by user: [" + user.getFullName() + "]" );

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
        String sqlStr = "";
        Vector frame_set;
        String html_str = "";

        DBConnect dbc = new DBConnect( m_conPool );
        dbc.getConnection();

        sqlStr = "select doc_type from meta where meta_id = " + meta_id;
        dbc.setSQLString( sqlStr );
        dbc.createStatement();
        Vector vec_doc_type = dbc.executeQuery();
        dbc.clearResultSet();

        if( Integer.parseInt( vec_doc_type.elementAt( 0 ).toString() ) == 7 ) {
            sqlStr = "select frame_set from frameset_docs where meta_id = " + meta_id;
            dbc.setSQLString( sqlStr );
            dbc.createStatement();
            frame_set = dbc.executeQuery();
            dbc.clearResultSet();
            html_str = frame_set.elementAt( 0 ).toString();
        } else
            html_str = null;

        //close connection
        dbc.closeConnection();
        dbc = null;

        return html_str;

    }

    /**
     * Check if external doc.
     */
    public ExternalDocType isExternalDoc( int meta_id, UserDomainObject user ) {
        String sqlStr = "";
        ExternalDocType external_doc = null;

        DBConnect dbc = new DBConnect( m_conPool );
        dbc.getConnection();

        sqlStr = "select doc_type from meta where meta_id = " + meta_id;
        dbc.setSQLString( sqlStr );
        dbc.createStatement();
        Vector vec_doc_type = dbc.executeQuery();
        dbc.clearResultSet();

        int doc_type = Integer.parseInt( vec_doc_type.elementAt( 0 ).toString() );
        if( doc_type > 100 ) {
            for( int i = 0; i < m_ExDoc.length && m_ExDoc[i] != null; i++ )
                if( m_ExDoc[i].getDocType() == doc_type ) {
                    //		external_doc = new ExternalDocType(m_ExDoc[i].getDocType(),m_ExDoc[i].getCallServlet(),
                    //	m_ExDoc[i].getDocName(),m_ExDoc[i].getParamStr()) ;
                    external_doc = m_ExDoc[i];
                }
        }
        //close connection
        dbc.closeConnection();
        dbc = null;

        return external_doc;

    }

    /**
     * Activate child to child-table.
     */
    public void activateChild( int meta_id, imcode.server.user.UserDomainObject user ) {

        String sqlStr = "";

        DBConnect dbc = new DBConnect( m_conPool );
        dbc.getConnection();

        sqlStr = "update meta\n";
        sqlStr += "set activate=1\n";
        sqlStr += "where meta_id = " + meta_id;
        dbc.setSQLString( sqlStr );
        dbc.createStatement();
        dbc.executeUpdateQuery();

        this.updateLogs( "Child [" + meta_id + "] activated  " + "by user: [" + user.getFullName() + "]" );


        //close connection
        dbc.closeConnection();
        dbc = null;

    }

    /**
     Send a sqlquery to the database and return a string array.
     @deprecated Use {@link #sqlProcedure(String, String[])} instead.
     **/
    public String[] sqlQuery( String sqlQuery ) {

        Vector data = new Vector();

        DBConnect dbc = new DBConnect( m_conPool, sqlQuery );
        dbc.getConnection();
        dbc.createStatement();
        data = dbc.executeQuery();

        dbc.clearResultSet();
        dbc.closeConnection();
        dbc = null;

        if( data != null ) {
            String result[] = new String[data.size()];
            for( int i = 0; i < data.size(); i++ ) {
                result[i] = null != data.elementAt( i ) ? data.elementAt( i ).toString() : null;
            }
            return result;
        } else {
            return null;
        }
    }

    /**
     Send a sqlquery to the database and return a string
     @deprecated Use {@link #sqlProcedure(String, String[])} instead.
     **/
    public String sqlQueryStr( String sqlQuery ) {
        Vector data = new Vector();

        DBConnect dbc = new DBConnect( m_conPool, sqlQuery );
        dbc.getConnection();
        dbc.createStatement();
        data = dbc.executeQuery();

        dbc.clearResultSet();
        dbc.closeConnection();
        dbc = null;

        if( data.size() > 0 ) {
            return null != data.elementAt( 0 ) ? data.elementAt( 0 ).toString() : null;
        } else {
            return null;
        }
    }

    /**
     Send a sql update query to the database
     @deprecated Use {@link #sqlUpdateProcedure(String, String[])} instead.
     **/
    public void sqlUpdateQuery( String sqlStr ) {
        DBConnect dbc = new DBConnect( m_conPool );
        dbc.setSQLString( sqlStr );
        dbc.getConnection();
        dbc.createStatement();
        dbc.executeUpdateQuery();
        dbc.closeConnection();
    }

    /**
     Send a procedure to the database and return a string array
     @deprecated Use {@link #sqlProcedure(String, String[])} instead.
     **/
    public String[] sqlProcedure( String procedure ) {

        DBConnect dbc = new DBConnect( m_conPool );
        dbc.getConnection();
        dbc.setProcedure( procedure );
        // dbc.createStatement() ;
        Vector data = dbc.executeProcedure();

        dbc.clearResultSet();
        dbc.closeConnection();
        dbc = null;

        if( data != null ) {
            String result[] = new String[data.size()];
            for( int i = 0; i < data.size(); i++ ) {
                result[i] = null != data.elementAt( i ) ? data.elementAt( i ).toString() : null;
            }
            return result;
        }
        return null;
    }

    /**
     The preferred way of getting data from the db.
     String.trim()'s the results.
     @param procedure The name of the procedure
     @param params    The parameters of the procedure
     **/
    public String[] sqlProcedure( String procedure, String[] params ) {
        return sqlProcedure( procedure, params, true );
    }

    /**
     The preferred way of getting data from the db.
     @param procedure The name of the procedure.
     @param params    The parameters of the procedure.
     @param trim      Whether to String.trim() the results.
     **/
    public String[] sqlProcedure( String procedure, String[] params, boolean trim ) {
        Vector data = new Vector();

        DBConnect dbc = new DBConnect( m_conPool );
        dbc.setTrim( trim );
        dbc.getConnection();

        dbc.setProcedure( procedure, params );
        data = dbc.executeProcedure();
        dbc.clearResultSet();
        dbc.closeConnection();
        dbc = null;

        if( data != null ) {
            String result[] = new String[data.size()];
            for( int i = 0; i < data.size(); i++ ) {
                result[i] = null != data.elementAt( i ) ? data.elementAt( i ).toString() : null;
            }
            return result;
        }
        return null;
    }

    /**
     The preferred way of getting data to the db.
     @param procedure The name of the procedure
     @param params    The parameters of the procedure
     @return updateCount or -1 if error
     **/
    public int sqlUpdateProcedure( String procedure, String[] params ) {
        DBConnect dbc = new DBConnect( m_conPool );
        dbc.getConnection();
        dbc.setProcedure( procedure, params );
        int res = dbc.executeUpdateProcedure();
        dbc.clearResultSet();
        dbc.closeConnection();
        dbc = null;
        return res;
    }

    /**
     Send a procedure to the database and return a string.
     @deprecated Use {@link #sqlProcedureStr(String, String[])} instead.
     **/
    public String sqlProcedureStr( String procedure ) {
        Vector data;

        DBConnect dbc = new DBConnect( m_conPool );
        dbc.getConnection();
        dbc.setProcedure( procedure );
        data = dbc.executeProcedure();

        dbc.clearResultSet();
        dbc.closeConnection();
        dbc = null;

        if( data != null ) {

            if( data.size() > 0 ) {
                return null != data.elementAt( 0 ) ? data.elementAt( 0 ).toString() : null;
            } else {
                return null;
            }
        } else
            return null;
    }

    /**
     Send a procedure to the database and return a string.
     **/
    public String sqlProcedureStr( String procedure, String[] params ) {
        return sqlProcedureStr( procedure, params, true );
    }

    /**
     Send a procedure to the database and return a string.
     **/
    public String sqlProcedureStr( String procedure, String[] params, boolean trim ) {

        DBConnect dbc = new DBConnect( m_conPool );
        dbc.setTrim( trim );
        dbc.getConnection();
        dbc.setProcedure( procedure, params );
        List data = dbc.executeProcedure();

        dbc.clearResultSet();
        dbc.closeConnection();
        dbc = null;

        if( data != null ) {

            if( data.size() > 0 ) {
                Object obj = data.get( 0 );
                return null != obj ? obj.toString() : null;
            } else {
                return null;
            }
        } else
            return null;
    }

    /**
     Send a update procedure to the database
     @deprecated Use {@link #sqlUpdateProcedure(String, String[])} instead.
     **/
    public int sqlUpdateProcedure( String procedure ) {
        DBConnect dbc = new DBConnect( m_conPool );
        dbc.getConnection();
        dbc.setProcedure( procedure );
        int res = dbc.executeUpdateProcedure();
        dbc.clearResultSet();
        dbc.closeConnection();
        dbc = null;
        return res;
    }

    /**
     Send a sqlQuery to the database and return a Hastable
     */
    public Hashtable sqlQueryHash( String sqlQuery ) {

        DBConnect dbc = new DBConnect( m_conPool, sqlQuery );
        dbc.getConnection();
        dbc.createStatement();

        Vector data = dbc.executeQuery();
        String[] meta = dbc.getMetaData();

        int columns = dbc.getColumnCount();

        Hashtable result = new Hashtable( columns, 0.5f );

        dbc.clearResultSet();
        dbc.closeConnection();

        if( data.size() > 0 ) {

            for( int i = 0; i < columns; i++ ) {
                String temp_str[] = new String[data.size() / columns];
                int counter = 0;

                for( int j = i; j < data.size(); j += columns )
                    temp_str[counter++] = null != data.elementAt( j ) ? data.elementAt( j ).toString() : null;

                result.put( meta[i], temp_str );
            }

            return result;
        } else {
            return new Hashtable( 1, 0.5f );
        }

    }

    /**
     Send a procedure to the database and return a Hashtable
     @deprecated Use {@link #sqlProcedure(String, String[])} instead.
     **/
    public Hashtable sqlProcedureHash( String procedure ) {

        DBConnect dbc = new DBConnect( m_conPool );
        dbc.getConnection();
        dbc.setProcedure( procedure );

        Vector data = dbc.executeProcedure();
        String[] meta = dbc.getMetaData();
        int columns = dbc.getColumnCount();

        Hashtable result = new Hashtable( columns, 0.5f );

        dbc.clearResultSet();
        dbc.closeConnection();

        if( data.size() > 0 ) {

            for( int i = 0; i < columns; i++ ) {
                String temp_str[] = new String[data.size() / columns];
                int counter = 0;

                for( int j = i; j < data.size(); j += columns ) {
                    temp_str[counter++] = null != data.elementAt( j ) ? data.elementAt( j ).toString() : null;
                }
                result.put( meta[i], temp_str );
            }
            return result;
        } else {
            return new Hashtable( 1, 0.5f );
        }
    }

    /**
     Send a procedure to the database and return a 2-dimensional string array
     **/
    public String[][] sqlProcedureMulti( String procedure, String[] params ) {
        return sqlProcedureMulti( procedure, params, true );
    }

    /**
     Send a procedure to the database and return a 2-dimensional string array
     **/
    public String[][] sqlProcedureMulti( String procedure, String[] params, boolean trim ) {
        Vector data = new Vector();

        DBConnect dbc = new DBConnect( m_conPool );
        dbc.setTrim( trim );
        dbc.getConnection();
        if( params.length > 0 ) {
            StringBuffer procedureBuffer = new StringBuffer( procedure );
            procedureBuffer.append( " ?" );
            for( int i = 1; i < params.length; ++i ) {
                procedureBuffer.append( ",?" );
            }
            procedure = procedureBuffer.toString();
        }

        dbc.setProcedure( procedure, params );
        data = dbc.executeProcedure();
        int columns = dbc.getColumnCount();
        dbc.clearResultSet();
        dbc.closeConnection();
        dbc = null;

        if( data != null && columns > 0 ) {
            String result[][] = new String[data.size() / columns][columns];
            for( int i = 0; i < data.size(); i++ ) {
                result[i / columns][i % columns] = null != data.elementAt( i ) ? data.elementAt( i ).toString() : null;
            }
            return result;
        }
        return null;
    }

    /**
     Send a procedure to the database and return a 2-dimensional string array
     @deprecated Use {@link #sqlProcedureMulti(String, String[])} instead.
     **/
    public String[][] sqlProcedureMulti( String procedure ) {

        Vector data = new Vector();

        DBConnect dbc = new DBConnect( m_conPool );
        dbc.getConnection();
        dbc.setProcedure( procedure );

        data = dbc.executeProcedure();
        int columns = dbc.getColumnCount();

        if( columns == 0 )
            return new String[0][0];

        int rows = data.size() / columns;
        dbc.clearResultSet();
        dbc.closeConnection();

        String result[][] = new String[rows][columns];
        for( int i = 0; i < rows; i++ ) {
            for( int j = 0; j < columns; j++ ) {
                result[i][j] = null != data.elementAt( i * columns + j ) ? data.elementAt( i * columns + j ).toString() : null;
            }

        }

        return result;

    }

    /**
     Send a sqlquery to the database and return a multi string array
     */
    public String[][] sqlQueryMulti( String sqlQuery ) {

        Vector data = new Vector();

        DBConnect dbc = new DBConnect( m_conPool, sqlQuery );
        dbc.getConnection();
        dbc.createStatement();

        data = dbc.executeQuery();
        int columns = dbc.getColumnCount();

        if( columns == 0 )
            return new String[0][0];

        int rows = data.size() / columns;
        dbc.clearResultSet();
        dbc.closeConnection();

        String result[][] = new String[rows][columns];
        for( int i = 0; i < rows; i++ ) {
            for( int j = 0; j < columns; j++ ) {
                result[i][j] = null != data.elementAt( i * columns + j ) ? data.elementAt( i * columns + j ).toString() : null;
            }

        }
        return result;
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
    public String parseDoc( java.util.List variables, String admin_template_name, String lang_prefix ) {
        try {
            String htmlStr = fileCache.getCachedFileString( new File( m_TemplateHome, lang_prefix + "/admin/" + admin_template_name ) );
            if( variables == null ) {
                return htmlStr;
            }
            String[] foo = new String[variables.size()];
            return imcode.util.Parser.parseDoc( htmlStr, (String[])variables.toArray( foo ) );
        } catch( IOException ex ) {
            log.error( ex.toString() );
            return "";
        }
    }

    /**
     Parse doc replace variables with data , use template
     */
    public String parseExternalDoc( java.util.Vector variables, String external_template_name, String lang_prefix, String doc_type ) {
        try {
            String htmlStr = fileCache.getCachedFileString( new File( m_TemplateHome, lang_prefix + "/" + doc_type + "/" + external_template_name ) );
            if( variables == null ) {
                return htmlStr;
            }
            String[] foo = new String[variables.size()];
            return imcode.util.Parser.parseDoc( htmlStr, (String[])variables.toArray( foo ) );
        } catch( RuntimeException e ) {
            log.error( "parseExternalDoc(Vector, String, String, String): RuntimeException", e );
            throw e;
        } catch( IOException e ) {
            log.error( "parseExternalDoc(Vector, String, String, String): IOException", e );
            return "";
        }
    }

    /**
     Parse doc replace variables with data , use template
     */
    public String parseExternalDoc( java.util.Vector variables, String external_template_name, String lang_prefix, String doc_type, String templateSet ) {
        try {
            String htmlStr = fileCache.getCachedFileString( new File( m_TemplateHome, lang_prefix + "/" + doc_type + "/" + templateSet + "/" + external_template_name ) );
            if( variables == null ) {
                return htmlStr;
            }
            String[] foo = new String[variables.size()];
            return imcode.util.Parser.parseDoc( htmlStr, (String[])variables.toArray( foo ) );
        } catch( RuntimeException e ) {
            log.error( "parseExternalDoc(Vector, String, String, String): RuntimeException", e );
            throw e;
        } catch( IOException e ) {
            log.error( "parseExternalDoc(Vector, String, String, String): IOException", e );
            return "";
        }
    }

    /**
     @deprecated Ugly use {@link #parseExternalDoc(java.util.Vector variables, String external_template_name, String lang_prefix, String doc_type)}
     or something else instead.
     */
    public File getExternalTemplateFolder( int meta_id ) {
        Vector data = new Vector();

        DBConnect dbc = new DBConnect( m_conPool );
        String sqlStr = "select doc_type,lang_prefix from meta where meta_id = " + meta_id;
        dbc.setSQLString( sqlStr );
        dbc.getConnection();
        dbc.createStatement();
        data = dbc.executeQuery();

        dbc.clearResultSet();
        dbc.closeConnection();
        dbc = null;

        if( Integer.parseInt( data.elementAt( 0 ).toString() ) > 100 ) {
            return new File( m_TemplateHome, (data.elementAt( 1 ).toString() + "/" + data.elementAt( 0 ).toString() + "/") );
        } else {
            return new File( m_TemplateHome, (data.elementAt( 1 ).toString() + "/") );
        }
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
     * Return file-path to images.
     */
    public File getImagePath() {
        log.warn( "getImagePath() called, no implementation, returning null" );
        return null;
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
    public String getDefaultLanguage() {
        return m_Language;
    }

    /**
     * Increment session counter.
     */
    public synchronized int incCounter() {
        m_SessionCounter += 1;
        m_databaseService.sproc_SetSessionCounterValue( m_SessionCounter );
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
    public int setCounter( int value ) {
        m_SessionCounter = value;
        this.sqlUpdateProcedure( "SetSessionCounterValue '" + value + "'" );
        return m_SessionCounter;
    }

    /**
     * Set session counter date.
     */
    public boolean setCounterDate( String date ) {
        m_SessionCounterDate = date;
        this.sqlUpdateProcedure( "SetSessionCounterDate '" + date + "'" );
        return true;
    }

    /**
     * Get session counter date.
     */
    public String getCounterDate() {
        return m_SessionCounterDate;
    }

    /**
     get doctype
     */
    public int getDocType( int meta_id ) {
        DBConnect dbc = new DBConnect( m_conPool );
        dbc.getConnection();
        dbc.setProcedure( "GetDocType " + meta_id );
        Vector data = dbc.executeProcedure();
        dbc.clearResultSet();
        dbc.closeConnection();
        dbc = null;

        if( data != null ) {
            if( data.size() > 0 )
                return Integer.parseInt( data.elementAt( 0 ).toString() );
            else
                return 0;
        }

        return -1;
    }

    /**
     CheckAdminRights, returns true if the user is an superadmin. Only an superadmin
     is allowed to create new users
     False if the user isn't an administrator.
     1 = administrator
     0 = superadministrator
     */

    public boolean checkAdminRights( imcode.server.user.UserDomainObject user ) {

        // Lets verify that the user who tries to add a new user is an SUPER_ADMIN
        int currUser_id = user.getUserId();
        String checkAdminSql = "CheckAdminRights " + currUser_id;
        String[] roles = sqlProcedure( checkAdminSql );

        for( int i = 0; i < roles.length; i++ ) {
            String aRole = roles[i];
            if( aRole.equalsIgnoreCase( "0" ) )
                return true;
        }
        return false;
    } // checkAdminRights

    /**
     checkDocAdminRights
     */
    public boolean checkDocAdminRights( int meta_id, UserDomainObject user ) {
        DocumentDomainObject document = documentMapper.getDocument( meta_id );
        return documentMapper.hasAdminPermissions( document, user );
    }

    /**
     checkDocRights
     */
    public boolean checkDocRights( int meta_id, UserDomainObject user ) {
        try {
            DBConnect dbc = new DBConnect( m_conPool );
            dbc.getConnection();

            String sqlStr = "GetUserPermissionSet";
            String[] sqlAry = {String.valueOf( meta_id ), String.valueOf( user.getUserId() )};
            dbc.setProcedure( sqlStr, sqlAry );
            Vector perms = dbc.executeProcedure();
            dbc.clearResultSet();
            dbc.closeConnection();

            if( perms.size() > 0 && Integer.parseInt( (String)perms.elementAt( 0 ) ) < 4 ) {
                return true;
            } else {
                return false;
            }
        } catch( RuntimeException ex ) {
            log.error( "Exception in checkDocRights(int,User)", ex );
            throw ex;
        }
    }

    /**
     Checks to see if a user has any permission of a particular set of permissions for a document.
     @param meta_id	The document-id
     @param user		The user
     @param permission A bitmap containing the permissions.
     */
    public boolean checkDocAdminRightsAny( int meta_id, UserDomainObject user, int permission ) {
        try {
            DBConnect dbc = new DBConnect( m_conPool );
            dbc.getConnection();

            String sqlStr = "GetUserPermissionSet";
            String[] sqlAry = {String.valueOf( meta_id ), String.valueOf( user.getUserId() )};
            dbc.setProcedure( sqlStr, sqlAry );
            Vector perms = dbc.executeProcedure();
            dbc.clearResultSet();
            dbc.closeConnection();

            int set_id = Integer.parseInt( (String)perms.elementAt( 0 ) );
            int set = Integer.parseInt( (String)perms.elementAt( 1 ) );

            if( perms.size() > 0 && set_id == 0		// User has full permission for this document
                || (set_id < 3 && ((set & permission) > 0))	// User has at least one of the permissions given.
            ) {
                return true;
            } else {
                return false;
            }
        } catch( RuntimeException ex ) {
            log.error( "Exception in checkDocAdminRightsAny(int,User,int)", ex );
            throw ex;
        }
    }

    /**
     Checks to see if a user has a particular set of permissions for a document.
     @param meta_id      The document-id
     @param user		    The user
     @param permission	A bitmap containing the permissions.
     */
    public boolean checkDocAdminRights( int meta_id, UserDomainObject user, int permission ) {
        try {
            DBConnect dbc = new DBConnect( m_conPool );
            dbc.getConnection();
            String sqlStr = "GetUserPermissionSet";
            String[] sqlAry = {String.valueOf( meta_id ), String.valueOf( user.getUserId() )};
            dbc.setProcedure( sqlStr, sqlAry );
            Vector perms = dbc.executeProcedure();
            dbc.clearResultSet();
            dbc.closeConnection();

            if( perms.size() == 0 ) {
                return false;
            }

            int set_id = Integer.parseInt( (String)perms.elementAt( 0 ) );
            int set = Integer.parseInt( (String)perms.elementAt( 1 ) );

            if( set_id == 0		// User has full permission for this document
                || (set_id < 3 && ((set & permission) == permission))	// User has all the permissions given.
            ) {
                return true;
            } else {
                return false;
            }
        } catch( RuntimeException ex ) {
            log.error( "Exception in checkDocAdminRights(int,User,int)", ex );
            throw ex;
        }
    }

    /**
     Gets the users most privileged permission_set for the document.
     @param meta_id	The document-id
     @param user_id		The user_id
     @return the most privileged permission_set a user has for the document.

     */
    public int getUserHighestPermissionSet( int meta_id, int user_id ) {
        try {
            DBConnect dbc = new DBConnect( m_conPool );
            dbc.getConnection();
            String sqlStr = "GetUserPermissionSet";
            String[] sqlAry = {String.valueOf( meta_id ), String.valueOf( user_id )};
            dbc.setProcedure( sqlStr, sqlAry );
            Vector perms = dbc.executeProcedure();
            dbc.clearResultSet();
            dbc.closeConnection();

            if( perms.size() == 0 ) {
                return IMCConstants.DOC_PERM_SET_NONE;//nothing was returned so give no rights at all.
            }

            int set_id = Integer.parseInt( (String)perms.elementAt( 0 ) );

            switch( set_id ) {
                case IMCConstants.DOC_PERM_SET_FULL:         // User has full permission for this document
                case IMCConstants.DOC_PERM_SET_RESTRICTED_1: // User has restricted 1 permission for this document
                case IMCConstants.DOC_PERM_SET_RESTRICTED_2: // User has restricted 2 permission for this document
                case IMCConstants.DOC_PERM_SET_READ:         // User has only read permission for this document
                    return set_id;                          // We have a valid permission-set-id. Return it.

                default:                                     // We didn't get a valid permission-set-id.
                    return DOC_PERM_SET_NONE;               // User has no permission at all for this document
            }

        } catch( RuntimeException ex ) {
            log.error( "Exception in getUserHighestPermissionSet(int,int)", ex );
            throw ex;
        }
    }

    /**
     save template to disk
     */
    public int saveTemplate( String name, String file_name, byte[] template, boolean overwrite, String lang_prefix ) {
        String sqlStr = "";
        String file;
        String new_template_id = "";

        try {
            file = new String( template, "8859_1" );
        } catch( UnsupportedEncodingException e ) {
            return -2;
        }

        int no_of_txt = 0;
        int no_of_img = 0;
        int no_of_url = 0;

        for( int index = 0; (index = file.indexOf( "#txt", index )) != -1; no_of_txt++ )
            index += 4;
        for( int index = 0; (index = file.indexOf( "#img", index )) != -1; no_of_img++ )
            index += 4;
        for( int index = 0; (index = file.indexOf( "#url", index )) != -1; no_of_url++ )
            index += 4;

        // create connectionobject
        DBConnect dbc = new DBConnect( m_conPool );
        dbc.getConnection();


        // check if template exists
        sqlStr = "select template_id from templates\n";
        sqlStr += "where simple_name = '" + name + "'";
        dbc.setSQLString( sqlStr );
        dbc.createStatement();
        Vector template_id = dbc.executeQuery();
        dbc.clearResultSet();
        if( template_id.size() == 0 ) {

            // get new template_id
            sqlStr = "select max(template_id) + 1 from templates\n";
            dbc.setSQLString( sqlStr );
            dbc.createStatement();
            new_template_id = dbc.executeQuery().elementAt( 0 ).toString();
            dbc.clearResultSet();

            sqlStr = "insert into templates\n";
            sqlStr += "values (" + new_template_id + ",'" + file_name + "','" + name + "','" + lang_prefix + "'," + no_of_txt + "," + no_of_img + "," + no_of_url + ")";
            dbc.setSQLString( sqlStr );
            dbc.createStatement();
            dbc.executeUpdateQuery();
        } else { //update
            if( !overwrite ) {
                dbc.closeConnection();
                dbc = null;
                return -1;
            }
            new_template_id = template_id.elementAt( 0 ).toString();

            sqlStr = "update templates\n" + "set template_name = '" + file_name + "'," + "no_of_txt =" + no_of_txt + "," + "no_of_img =" + no_of_img + "," + "no_of_url =" + no_of_url + "where template_id = " + new_template_id;
            dbc.setSQLString( sqlStr );
            dbc.createStatement();
            dbc.executeUpdateQuery();
        }

        dbc.closeConnection();
        dbc = null;

        File f = new File( m_TemplateHome, "text/" + new_template_id + ".html" );

        try {
            FileOutputStream fw = new FileOutputStream( f );
            fw.write( template );
            fw.flush();
            fw.close();

        } catch( IOException e ) {
            return -2;
        }

        //  0 = OK
        // -1 = file exist
        // -2 = write error
        return 0;

    }

    /**
     get demo template
     */
    public Object[] getDemoTemplate( int template_id ) throws IOException {
        //String str = "" ;
        StringBuffer str = new StringBuffer();
        BufferedReader fr = null;
        String suffix = null;
        String[] suffixList = {"jpg", "jpeg", "gif", "png", "html", "htm"};

        for( int i = 0; i < suffixList.length; i++ ) { // Looking for a template with one of six suffixes
            File fileObj = new File( m_TemplateHome, "/text/demo/" + template_id + "." + suffixList[i] );
            long date = 0;
            long fileDate = fileObj.lastModified();
            if( fileObj.exists() && fileDate > date ) {
                // if a template was not properly removed, the template
                // with the most recens modified-date is returned
                date = fileDate;

                try {
                    fr = new BufferedReader( new InputStreamReader( new FileInputStream( fileObj ), "8859_1" ) );
                    suffix = suffixList[i];
                } catch( IOException e ) {
                    return null; //Could not read
                }
            } // end IF
        } // end FOR

        char[] buffer = new char[4096];
        try {
            int read;
            while( (read = fr.read( buffer, 0, 4096 )) != -1 ) {
                str.append( buffer, 0, read );
            }
        } catch( IOException e ) {
            return null;
        } catch( NullPointerException e ) {
            return null;
        }

        return new Object[]{suffix, str.toString().getBytes( "8859_1" )}; //return the buffer

    }

    /**
     get template
     */
    public byte[] getTemplateData( int template_id ) throws IOException {
        String str = "";

        BufferedReader fr;

        try {
            fr = new BufferedReader( new FileReader( m_TemplateHome + "/text/" + template_id + ".html" ) );
        } catch( FileNotFoundException e ) {
            log.info( "Failed to find template number " + template_id );
            return null;
        }

        try {
            int temp;
            while( (temp = fr.read()) != -1 ) {
                str += (char)temp;
            }
        } catch( IOException e ) {
            log.info( "Failed to read template number " + template_id );
            return null;
        }

        return str.getBytes( "8859_1" );
    }

    /**
     delete template from db/disk
     */
    public void deleteTemplate( int template_id ) {
        String sqlStr = "";

        // create connectiobject
        DBConnect dbc = new DBConnect( m_conPool );
        dbc.getConnection();


        // delete from database
        sqlStr = "delete from templates_cref\n";
        sqlStr += "where template_id = " + template_id + "\n";
        dbc.setSQLString( sqlStr );
        dbc.createStatement();
        dbc.executeUpdateQuery();



        // delete from database
        sqlStr = "delete from templates\n";
        sqlStr += "where template_id = " + template_id + "\n";
        dbc.setSQLString( sqlStr );
        dbc.createStatement();
        dbc.executeUpdateQuery();

        dbc.closeConnection();
        dbc = null;

        // test if template exists and delete it
        File f = new File( m_TemplateHome + "/text/" + template_id + ".html" );
        if( f.exists() ) {
            f.delete();
        }

    }

    /**
     save demo template
     */
    public int saveDemoTemplate( int template_id, byte[] data, String suffix ) {

        // save template demo

        // See if there are templete_id:s with other file-formats and delete them
        // WARNING: Uggly Code
        String[] suffixes = {"jpg", "jpeg", "gif", "png", "htm", "html"};
        for( int i = 0; i <= 5; i++ ) {
            File file = new File( m_TemplateHome + "/text/demo/" + template_id + "." + suffixes[i] );
            if( file.exists() )
                file.delete();
            // doesn't always delete the file, made sure the right template is
            // shown using the file-date & time in getDemoTemplate
        }

        try {
            FileOutputStream fw = new FileOutputStream( m_TemplateHome + "/text/demo/" + template_id + "." + suffix );
            fw.write( data );
            fw.close();
        } catch( IOException e ) {
            return -2;
        }

        return 0;

    }

    /**
     delete templategroup
     */
    public void deleteTemplateGroup( int group_id ) {
        String sqlStr = "";

        // create connectiobject
        DBConnect dbc = new DBConnect( m_conPool );
        dbc.getConnection();


        // change name
        sqlStr = "delete from templategroups\n";
        sqlStr += "where group_id = " + group_id + "\n";
        dbc.setSQLString( sqlStr );
        dbc.createStatement();
        dbc.executeUpdateQuery();
        dbc.closeConnection();
        dbc = null;

    }

    /**
     change templategroupname
     */
    public void changeTemplateGroupName( int group_id, String new_name ) {
        String sqlStr = "";

        // create connectiobject
        DBConnect dbc = new DBConnect( m_conPool );
        dbc.getConnection();


        // change name
        sqlStr = "update templategroups\n";
        sqlStr += "set group_name = '" + new_name + "'";
        sqlStr += "where group_id = " + group_id + "\n";
        dbc.setSQLString( sqlStr );
        dbc.createStatement();
        dbc.executeUpdateQuery();

        dbc.closeConnection();
        dbc = null;
    }

    /** get server date
     */
    public Date getCurrentDate() {
        return new Date();
    }

    final static FileFilter DEMOTEMPLATEFILTER = new FileFilter() {
        public boolean accept( File file ) {
            return file.length() > 0;
        }
    };

    // get demotemplates
    public String[] getDemoTemplateList() {
        File demoDir = new File( m_TemplateHome + "/text/demo/" );

        File[] file_list = demoDir.listFiles( DEMOTEMPLATEFILTER );

        String[] name_list = new String[file_list.length];

        if( file_list != null ) {
            for( int i = 0; i < name_list.length; i++ ) {
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
    public int deleteDemoTemplate( int template_id ) {

        File f = new File( m_TemplateHome + "/text/demo/" + template_id + ".html" );
        if( f.exists() ) {
            f.delete();
            return 0;
        }

        return -2;
    }

    /**
     *	Return  language. Returns the langprefix from the db. Takes a lang id
     as argument. Will return null if something goes wrong.
     Example: If the language id number for swedish is 1. then the call
     myObject.getLanguage("1") will return 'se'
     That is, provided that the prefix for swedish is 'se', which it isn't.
     Or rather, it shouldn't be.
     */
    public String getLanguage( String lang_id ) {
        return sqlProcedureStr( "GetLangPrefixFromId " + lang_id );
    }

    /** Fetch the systemdata from the db */
    protected SystemData getSystemDataFromDb() {

        SystemData sd = new SystemData();
        sd.setStartDocument( m_databaseService.sproc_StartDocGet() );
        sd.setSystemMessage( m_databaseService.sproc_SystemMessageGet() );
        sd.setServerMaster( m_databaseService.sproc_ServerMasterGet_name() );
        sd.setServerMasterAddress( m_databaseService.sproc_ServerMasterGet_address() );
        sd.setWebMaster( m_databaseService.sproc_WebMasterGet_name() );
        sd.setWebMasterAddress( m_databaseService.sproc_WebMasterGet_email() );

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
     * Returns an array with with all the documenttypes stored in the database
     * the array consists of pairs of id:, value. Suitable for parsing into select boxes etc.
     */
    public String[] getDocumentTypesInList( String langPrefixStr ) {
        return this.sqlProcedure( "GetDocTypes '" + langPrefixStr + "'" );
    }

    public boolean checkUserDocSharePermission( UserDomainObject user, int meta_id ) {
        return sqlProcedure( "CheckUserDocSharePermission " + user.getUserId() + "," + meta_id ).length > 0;
    }

    /**
     Return a file relative to the fortune-path.
     **/
    public String getFortune( String path ) throws IOException {
        return fileCache.getCachedFileString( new File( m_FortunePath, path ) );
    }

    /**
     Get a list of quotes

     @param quoteListName The name of the quote-List.

     @return the quote-List.
     **/
    public List getQuoteList( String quoteListName ) {
        List theList = new LinkedList();
        try {
            File file = new File( m_FortunePath, quoteListName );
            StringReader reader = new StringReader( fileCache.getUncachedFileString( file ) );
            QuoteReader quoteReader = new QuoteReader( reader );
            for( Quote quote; null != (quote = quoteReader.readQuote()); ) {
                theList.add( quote );
            }
            reader.close();
        } catch( IOException ignored ) {
            log.debug( "Failed to load quote-list " + quoteListName );
        }
        return theList;
    }

    /**
     Set a quote-list

     @param quoteListName The name of the quote-List.
     @param quoteList     The quote-List
     **/
    public void setQuoteList( String quoteListName, List quoteList ) throws IOException {
        FileWriter writer = new FileWriter( new File( m_FortunePath, quoteListName ) );
        QuoteWriter quoteWriter = new QuoteWriter( writer );
        Iterator quotesIterator = quoteList.iterator();
        while( quotesIterator.hasNext() ) {
            quoteWriter.writeQuote( (Quote)quotesIterator.next() );
        }
        writer.flush();
        writer.close();
    }

    /**
     @return a List of Polls
     **/
    public List getPollList( String pollListName ) {
        List theList = new LinkedList();
        try {
            File file = new File( m_FortunePath, pollListName );
            StringReader reader = new StringReader( fileCache.getUncachedFileString( file ) );
            PollReader pollReader = new PollReader( reader );
            for( Poll poll; null != (poll = pollReader.readPoll()); ) {
                theList.add( poll );
            }
            reader.close();
        } catch( IOException ignored ) {
            log.debug( "Failed to load poll-list " + pollListName );
        }
        return theList;
    }

    /**
     Set a poll-list

     @param pollListName The name of the poll-List.
     @param pollList     The poll-List
     **/
    public void setPollList( String pollListName, List pollList ) throws IOException {
        FileWriter writer = new FileWriter( new File( m_FortunePath, pollListName ) );
        PollWriter pollWriter = new PollWriter( writer );
        Iterator pollIterator = pollList.iterator();
        while( pollIterator.hasNext() ) {
            pollWriter.writePoll( (Poll)pollIterator.next() );
        }
        writer.flush();
        writer.close();
    }

    /**
     Return a file relative to the webapps. ex ../templates/se/admin/search/original
     **/
    public String getSearchTemplate( String path ) throws IOException {
        return fileCache.getCachedFileString( new File( m_TemplateHome, path ) );
    }

    /**
     @deprecated Ugly use something else.
     DOCME: Use what?
     */
    public File getInternalTemplateFolder( int meta_id ) {
        Vector data = new Vector();

        if( meta_id != -1 ) {
            DBConnect dbc = new DBConnect( m_conPool );
            String sqlStr = "select doc_type,lang_prefix from meta where meta_id = " + meta_id;
            dbc.setSQLString( sqlStr );
            dbc.getConnection();
            dbc.createStatement();
            data = dbc.executeQuery();

            dbc.clearResultSet();
            dbc.closeConnection();
            dbc = null;
            return new File( m_TemplateHome, data.elementAt( 1 ).toString() + "/" );

        } else {
            return m_TemplateHome;
        }
    }

    /**
     Retrieve the texts for a document
     @param meta_id The id of the document.
     @return A Map (Integer -> IMCText) with all the  texts in the document.
     **/
    public Map getTexts( int meta_id ) {

        // Now we'll get the texts from the db.
        String[] texts = sqlProcedure( "GetTexts", new String[]{String.valueOf( meta_id )}, false );
        Map textMap = new HashMap();
        Iterator it = Arrays.asList( texts ).iterator();
        while( it.hasNext() ) {
            try {
                it.next(); // the key, not needed
                String txt_no = (String)it.next();
                int txt_type = Integer.parseInt( (String)it.next() );
                String value = (String)it.next();
                textMap.put( txt_no, new IMCText( value, txt_type ) );
            } catch( NumberFormatException e ) {
                log.error( "SProc 'GetTexts " + meta_id + "' returned a non-number where a number was expected.", e );
                return null;
            }
        }
        return textMap;
    }

    /**
     Get the data for one document
     @param meta_id The id fore the wanted document
     @return a imcode.server.internalDocument.Document representation of the document, or null if there was none.
     @throws IndexOutOfBoundsException if there was no such document.
     **/
    public DocumentDomainObject getDocument( int meta_id ) throws IndexOutOfBoundsException {
        return documentMapper.getDocument( meta_id );
    }

    /**
     Get the readrunner-user-data for a user
     @param user The id of the user
     @return     The readrunner-user-data for a user, or null if the user had none.
     **/
    public ReadrunnerUserData getReadrunnerUserData( UserDomainObject user ) {
        int userId = user.getUserId();
        String[] dbData = sqlProcedure( "GetReadrunnerUserDataForUser", new String[]{String.valueOf( userId )} );
        if( 0 == dbData.length ) {
            // There was no readrunner-user-data
            return null;
        }

        // Create the ReadrunnerUserData-object
        ReadrunnerUserData rrUserData = new ReadrunnerUserData();
        try {
            // Fill it with data from the DB
            DateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
            rrUserData.setUses( Integer.parseInt( dbData[0] ) );
            rrUserData.setMaxUses( Integer.parseInt( dbData[1] ) );
            rrUserData.setMaxUsesWarningThreshold( Integer.parseInt( dbData[2] ) );
            if( null != dbData[3] ) {
                rrUserData.setExpiryDate( dateFormat.parse( dbData[3] ) );
            } else {
                rrUserData.setExpiryDate( null );
            }
            rrUserData.setExpiryDateWarningThreshold( Integer.parseInt( dbData[4] ) );
            rrUserData.setExpiryDateWarningSent( Integer.parseInt( dbData[5] ) != 0 );
            // Return it
            return rrUserData;
        } catch( NumberFormatException nfe ) {
            log.error( "GetReadrunnerUserData returned malformed integer-data.", nfe );
            throw nfe;
        } catch( ParseException pe ) {
            log.error( "GetReadrunnerUserData returned malformed date-data: '" + dbData[3] + "'", pe );
            throw new RuntimeException( "GetReadrunnerUserData returned malformed date-data: '" + dbData[3] + "'" );
        }
    }

    /**
     Set the readrunner-user-data for a user
     @param user       The user
     @param rrUserData The ReadrunnerUserData-object
     **/
    public void setReadrunnerUserData( UserDomainObject user, ReadrunnerUserData rrUserData ) {
        int userId = user.getUserId();

        String expiryDateString = null != rrUserData.getExpiryDate() ? new SimpleDateFormat( "yyyy-MM-dd" ).format( rrUserData.getExpiryDate() ) : null;

        String temp[] = {"" + userId, "" + rrUserData.getUses(), "" + rrUserData.getMaxUses(), "" + rrUserData.getMaxUsesWarningThreshold(), expiryDateString, "" + rrUserData.getExpiryDateWarningThreshold(), "" + rrUserData.getExpiryDateWarningSent()};
        for( int i = 0; i < temp.length; i++ ) {
            System.out.println( "temp[]= " + temp[i] );
        }

        sqlUpdateProcedure( "SetReadrunnerUserDataForUser", new String[]{"" + userId, "" + rrUserData.getUses(), "" + rrUserData.getMaxUses(), "" + rrUserData.getMaxUsesWarningThreshold(), expiryDateString, "" + rrUserData.getExpiryDateWarningThreshold(), rrUserData.getExpiryDateWarningSent() ? "1" : "0"} );
    }

    /** Used by the other getUserFlags*-methods to put the database-data in a Set **/
    private Map getUserFlags( String dbData[] ) {
        Map theFlags = new HashMap();

        for( int i = 0; i < dbData.length; i += 4 ) {
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
        DatabaseAccessor.sqlUpdateModifiedDatesOnDocumentAndItsParent( this, metaId, dateTime );
        DatabaseAccessor.sprocUpdateParentsDateModified( this, metaId );
    }

    public DocumentMapper getDocumentMapper() {
        return documentMapper;
    }

    public DatabaseService getDatabaseService() {
        return m_databaseService;
    }
}
