package imcode.server ;

import imcode.server.db.ConnectionPool;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentMapper;
import imcode.server.document.TemplateMapper;
import imcode.server.document.textdocument.TextDomainObject;
import imcode.server.parser.ParserParameters;
import imcode.server.user.ImcmsAuthenticatorAndUserMapper;
import imcode.server.user.UserDomainObject;
import imcode.util.net.SMTP;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.File;
import java.io.IOException;
import java.text.Collator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public interface ImcmsServices {

    /** Verify a Internet/Intranet user. Data from any SQL Database. **/
    UserDomainObject verifyUser(String login, String password)
	;

    /**
       Retrieve a text-field
    **/
    TextDomainObject getText(int meta_id,int txt_no)
	;

    String parsePage( ParserParameters paramsToParse ) throws IOException ;

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

    // Send a procedure to the database and return a string array
    String[] sqlProcedure(String procedure, String[] params)
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
    String getAdminTemplate( String adminTemplateName, UserDomainObject user, java.util.List tagsWithReplacements )  ;

    // parseExternaldoc use template
    String getTemplateFromDirectory( String adminTemplateName, UserDomainObject user, java.util.List variables,
                                                 String directory )
	;

    // parseExternaldoc use template
    String getTemplateFromSubDirectoryOfDirectory( String adminTemplateName, UserDomainObject user, java.util.List variables,
                                                               String directory, String subDirectory )
	;

    // get templatehome
    String getTemplateData(int template_id)
	throws IOException ;

    // get templatehome
    File getTemplatePath()
	;

    // get file-path to imcmsimages
    File getImcmsPath()
	;

    // get language prefix by id
    String getLanguagePrefixByLangId ( int lang_id );

    // get language prefix for user
    String getUserLangPrefixOrDefaultLanguage( UserDomainObject user )
    ;

    // get doctype
    int getDocType(int meta_id)
	;

    // checkDocAdminRights
    boolean checkDocAdminRights(int meta_id, UserDomainObject user)
	;

    // save template to disk
     int saveTemplate(String name,String file_name,byte[] data,boolean overwrite,String lang_prefix)
	;

    // get demo template data
    Object[] getDemoTemplate(int template_id)
	throws IOException ;

    boolean checkDocAdminRights(int meta_id, UserDomainObject user, int permissions)
	;

    boolean checkDocAdminRightsAny(int meta_id, UserDomainObject user, int permissions)
	;

    // save demo template
    void saveDemoTemplate(int template_id,byte [] data, String suffix) throws IOException
    ;

    // get server date
    Date getCurrentDate()
    ;

    // get demotemplates
    String[] getDemoTemplateIds()
    ;

    // delete demotemplate
    void deleteDemoTemplate(int template_id) throws IOException
    ;

    String getAdminButtons( UserDomainObject user, DocumentDomainObject document )  ;

    SystemData getSystemData()  ;

    void setSystemData(SystemData sd)  ;

    String[][] getAllDocumentTypes(String langPrefixStr)  ;

    String getFortune(String path) throws IOException ;

    List getQuoteList(String quoteListName);

    void setQuoteList(String quoteListName, List quoteList) throws IOException ;

    List getPollList(String pollListName);

    void setPollList(String pollListName, List pollList) throws IOException ;

    int getSessionCounter();

    String getSessionCounterDateAsString();

    /** Get all possible userflags **/
    Map getUserFlags() ;
    /** Get all userflags for a single user **/
    Map getUserFlags(UserDomainObject user) ;
    /** Get all userflags of a single type **/
    Map getUserFlags(int type) ;
    /** Get all userflags for a single user of a single type **/
    Map getUserFlags(UserDomainObject user, int type) ;

    void setUserFlag(UserDomainObject user, String flagName);

    void unsetUserFlag(UserDomainObject user, String flagName);

    /** Get an interface to the poll handling system **/
    imcode.util.poll.PollHandlingSystem getPollHandlingSystem();

    /** Get an interface to the shopping order system **/
    imcode.util.shop.ShoppingOrderSystem getShoppingOrderSystem() ;

    void updateMainLog( String logMessage );

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

    Properties getLanguageProperties(UserDomainObject user);

    File getFilePath();

    File getIncludePath();

    Collator getDefaultLanguageCollator();

    VelocityEngine getVelocityEngine(UserDomainObject user);

    VelocityContext getVelocityContext( UserDomainObject user );

    Config getConfig();
}
