package imcode.server ;

import java.io.* ;
import java.util.* ;
import java.text.Collator;

import imcode.server.parser.ParserParameters ;
import imcode.server.user.*;
import imcode.server.document.DocumentMapper;
import imcode.server.document.TemplateMapper;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.textdocument.TextDomainObject;
import imcode.server.document.textdocument.ImageDomainObject;
import imcode.server.db.ConnectionPool;
import imcode.util.net.SMTP;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.VelocityContext;

/**
 * Interface for the Imcode Net Server.
 */
public interface IMCServiceInterface {

    /** Verify a Internet/Intranet user. Data from any SQL Database. **/
    UserDomainObject verifyUser(String login, String password)
	;

    /**
       Retrieve a text-field
    **/
    TextDomainObject getText(int meta_id,int txt_no)
	;

    String parsePage( ParserParameters paramsToParse ) throws IOException ;

    // Save an image
    void saveImage(int meta_id,UserDomainObject user,int img_no,ImageDomainObject image)
	;

    /**
       Delete a document
    **/
    void deleteDocAll(int meta_id,UserDomainObject user)
	;

    void saveManualSort(int meta_id,imcode.server.user.UserDomainObject user,List childs, List sort_no, int menuNumber)
	;

    // archive childs
    void archiveChilds(int meta_id,UserDomainObject user,String[] childsThisMenu)
	;

    // List all archived docs
    //    String listArchive(int meta_id,imcode.server.user.User user)
    //;

    // check if url doc
    String isUrlDoc( int meta_id )
	;

    // check if url doc
    String isFramesetDoc( int meta_id )
	;

    // activate child to child table
    void activateChild(int meta_id,UserDomainObject user)
	;

    // Send a procedure to the database and return a string array
    public String[] sqlProcedure(String procedure, String[] params)
	;

    // get external template folder
    File getExternalTemplateFolder(int meta_id, UserDomainObject user)
	;

    void incrementSessionCounter();

    // set session counter
    void setSessionCounter(int value)  ;

    // set  session counter date
    void setSessionCounterDate(Date date)  ;

    // set  session counter date
    Date getSessionCounterDate()  ;

    // parsedoc use template
    public String getAdminTemplate( String adminTemplateName, UserDomainObject user, java.util.List tagsWithReplacements )  ;

    // parseExternaldoc use template
    public String getTemplateFromDirectory( String adminTemplateName, UserDomainObject user, java.util.List variables,
                                                 String directory )
	;

    // parseExternaldoc use template
    public String getTemplateFromSubDirectoryOfDirectory( String adminTemplateName, UserDomainObject user, java.util.List variables,
                                                               String directory, String subDirectory )
	;

    // get templatehome
    public String getTemplateData(int template_id)
	throws IOException ;

    // get templatehome
    public File getTemplatePath()
	;

    // get url-path to images
    public String getImageUrl()
	;

    // get file-path to imcmsimages
    public File getImcmsPath()
	;

    // get language prefix by id
    public String getLanguagePrefixByLangId ( int lang_id ); 

    // get language prefix for user
    public String getUserLangPrefixOrDefaultLanguage( UserDomainObject user )
    ;

    // get doctype
    public int getDocType(int meta_id)
	;

    // checkDocAdminRights
    public boolean checkDocAdminRights(int meta_id, UserDomainObject user)
	;

    // save template to disk
    public  int saveTemplate(String name,String file_name,byte[] data,boolean overwrite,String lang_prefix)
	;

    // get demo template data
    public Object[] getDemoTemplate(int template_id)
	throws IOException ;

    public boolean checkDocAdminRights(int meta_id, UserDomainObject user, int permissions)
	;

    public boolean checkDocAdminRightsAny(int meta_id, UserDomainObject user, int permissions)
	;

    // save demo template
    public void saveDemoTemplate(int template_id,byte [] data, String suffix) throws IOException
    ;

    // get server date
    public Date getCurrentDate()
    ;

    // get demotemplates
    public String[] getDemoTemplateIds()
    ;

    // delete demotemplate
    public void deleteDemoTemplate(int template_id) throws IOException
    ;

    public String getAdminButtons( UserDomainObject user, DocumentDomainObject document )  ;

    public SystemData getSystemData()  ;

    public void setSystemData(SystemData sd)  ;

    public String[][] getAllDocumentTypes(String langPrefixStr)  ;

    public String getFortune(String path) throws IOException ;

    public List getQuoteList(String quoteListName);

    public void setQuoteList(String quoteListName, List quoteList) throws IOException ;

    public List getPollList(String pollListName);

    public void setPollList(String pollListName, List pollList) throws IOException ;

    public int getSessionCounter();

    public String getSessionCounterDateAsString();

    /** Get all possible userflags **/
    public Map getUserFlags() ;
    /** Get all userflags for a single user **/
    public Map getUserFlags(UserDomainObject user) ;
    /** Get all userflags of a single type **/
    public Map getUserFlags(int type) ;
    /** Get all userflags for a single user of a single type **/
    public Map getUserFlags(UserDomainObject user, int type) ;

    public void setUserFlag(UserDomainObject user, String flagName);

    public void unsetUserFlag(UserDomainObject user, String flagName);

    /** Get an interface to the poll handling system **/
    public imcode.util.poll.PollHandlingSystem getPollHandlingSystem();

    /** Get an interface to the shopping order system **/
    public imcode.util.shop.ShoppingOrderSystem getShoppingOrderSystem() ;

    void updateModifiedDatesOnDocumentAndItsParent( int metaId, Date dateTime );

    void updateLogs( String logMessage );

    ConnectionPool getConnectionPool();

    DocumentMapper getDocumentMapper();

    ImcmsAuthenticatorAndUserMapper getImcmsAuthenticatorAndUserAndRoleMapper();

    String getDefaultLanguageAsIso639_2();

    Map sqlProcedureHash( String procedure, String[] params );

    int sqlUpdateProcedure( String procedure, String[] params );

    String sqlProcedureStr( String procedure, String[] params );

    int sqlUpdateQuery(String sqlStr, String[] params);

    void saveTreeSortIndex( int meta_id, UserDomainObject user, List childs, List sort_no, int menuNumber);

    String[][] sqlProcedureMulti(String procedure, String[] params);

    String[] sqlQuery(String sqlStr, String[] params);

    String sqlQueryStr(String sqlStr, String[] params);

    Map sqlQueryHash(String sqlStr, String[] params);

    String[][] sqlQueryMulti(String sqlstr, String[] params);

    TemplateMapper getTemplateMapper();

    SMTP getSMTP();

    public Properties getLanguageProperties(UserDomainObject user);

    File getFilePath();

    File getIncludePath();

    Collator getDefaultLanguageCollator();

    VelocityEngine getVelocityEngine(UserDomainObject user);

    VelocityContext getVelocityContext( UserDomainObject user );

    Config getConfig();
}
