package imcode.server ;

import java.io.* ;
import java.util.* ;

import imcode.server.parser.ParserParameters ;
import imcode.server.user.*;
import imcode.server.document.TextDocumentTextDomainObject;
import imcode.server.document.DocumentMapper;
import imcode.server.db.ConnectionPool;

import imcode.readrunner.* ;


/**
 * Interface for the Imcode Net Server.
 */
public interface IMCServiceInterface {

    /** Verify a Internet/Intranet user. Data from any SQL Database. **/
    UserDomainObject verifyUser(String login, String password)
	;

    /** Get a user by user-id **/
    UserDomainObject getUserById(int userId)
	;


    /** Check if a user has a special admin role **/
    public boolean checkUserAdminrole ( int userId, int adminRole )
	;

    /**
       Save a text field
    **/
    void saveText(UserDomainObject user,int meta_id,int txt_no,TextDocumentTextDomainObject text, String text_type)
	;

    /**
       Retrieve a text-field
    **/
    TextDocumentTextDomainObject getText(int meta_id,int txt_no)
	;

    String parsePage(DocumentRequest docReq, int flags, ParserParameters paramsToParse) throws IOException ;

    // Save an image
    void saveImage(int meta_id,UserDomainObject user,int img_no,imcode.server.Image image)
	;

    /**
       Delete a internalDocument
    **/
    void deleteDocAll(int meta_id,UserDomainObject user)
	;

    void addExistingDoc(int meta_id,UserDomainObject user,int existing_meta_id,int doc_menu_no) ;

    void saveManualSort(int meta_id,imcode.server.user.UserDomainObject user,List childs, List sort_no, int menuNumber)
	;

    // archive childs
    void archiveChilds(int meta_id,UserDomainObject user,String childsThisMenu[])
	;

    /** Copy documents and insert them in a new textdocument and menu **/
    String[] copyDocs( int meta_id, int doc_menu_no,  UserDomainObject user, String[] childsThisMenu, String copyPrefix)  ;

    // List all archived docs
    //    String listArchive(int meta_id,imcode.server.user.User user)
    //;

    // check if url doc
    String isUrlDoc(int meta_id,UserDomainObject user)
	;

    // Save a new frameset
    void saveNewFrameset(int meta_id,UserDomainObject user,String html)
	;

    // Save a frameset
    void saveFrameset(int meta_id,UserDomainObject user,String html)
	;

    // check if url doc
    String isFramesetDoc(int meta_id,UserDomainObject user)
	;

    // check if external doc
    ExternalDocType isExternalDoc(int meta_id,UserDomainObject user)
	;

    // activate child to child table
    void activateChild(int meta_id,UserDomainObject user)
	;

    // Send a procedure to the database and return a string array
    public String[] sqlProcedure(String procedure, String[] params)
	;

    public String[] sqlProcedure( String procedure, String[] params, boolean trim )
    ;

    // Parse doc replace variables with data, uses two vectors
    String  parseDoc(String htmlStr,java.util.Vector variables,java.util.Vector data)
	;

    // get external template folder
    File getExternalTemplateFolder(int meta_id)
	;

    // increment session counter
    int incCounter()  ;

    // get session counter
    int getCounter()  ;

    // set session counter
    int setCounter(int value)  ;

    // set  session counter date
    void setCounterDate(String date)  ;

    // set  session counter date
    String getCounterDate()  ;

    // parsedoc use template
    public String  parseDoc(java.util.List variables,String admin_template_name,
			    String lang_prefix)  ;

    // parseExternaldoc use template
    public String parseExternalDoc(java.util.List variables, String external_template_name, String lang_prefix, String doc_type)
	;

    // parseExternaldoc use template
    public String parseExternalDoc(java.util.List variables, String external_template_name, String lang_prefix, String doc_type, String templateSet)
	;

    // get templatehome
    public byte[] getTemplateData(int template_id)
	throws IOException ;

    // get templatehome
    public File getTemplateHome()
	;

    // get url-path to images
    public String getImageUrl()
	;

    // Return url-path to imcmsimages.
    public String getImcmsImageUrl();

    // get file-path to imcmsimages
    public File getImcmsImagePath()
	;

    // get starturl
    public String getStartUrl()
	;

    // get language
    public String getDefaultLanguageAsIso639_1()
	;

    // get doctype
    public int getDocType(int meta_id)
	;

    // checkDocAdminRights
    public boolean checkDocAdminRights(int meta_id, UserDomainObject user)
	;

    //get greatest permission_set
    public int getUserHighestPermissionSet (int meta_id, int user_id)
	;

    // save template to disk
    public  int saveTemplate(String name,String file_name,byte[] data,boolean overwrite,String lang_prefix)
	;

    // get demo template data
    public Object[] getDemoTemplate(int template_id)
	throws IOException ;

    // check if user can view internalDocument
    public boolean checkDocRights(int meta_id, UserDomainObject user)
	;

    public boolean checkDocAdminRights(int meta_id, UserDomainObject user, int permissions)
	;

    public boolean checkDocAdminRightsAny(int meta_id, UserDomainObject user, int permissions)
	;

    // delete template from db/disk
    public void deleteTemplate(int template_id)
	;

    // save demo template
    public int saveDemoTemplate(int template_id,byte [] data, String suffix)
	;

    // delete templategroup
    public void deleteTemplateGroup(int group_id)
	;

    // save templategroup
    public void changeTemplateGroupName(int group_id,String new_name)
	;

    // get server date
    public Date getCurrentDate()
	;

    // get demotemplates
    public String[] getDemoTemplateList()
	;

    // delete demotemplate
    public void deleteDemoTemplate(int template_id)
	;

    public String getMenuButtons(int meta_id, UserDomainObject user)  ;

    public String getMenuButtons(String meta_id, UserDomainObject user)  ;

    public SystemData getSystemData()  ;

    public void setSystemData(SystemData sd)  ;

    public String[][] getDocumentTypesInList(String langPrefixStr)  ;

    public boolean checkUserDocSharePermission(UserDomainObject user, int meta_id)  ;

    public String getFortune(String path) throws IOException ;

    public String getSearchTemplate(String path) throws IOException ;

    public void touchDocument(int meta_id) ;

    public List getQuoteList(String quoteListName);

    public void setQuoteList(String quoteListName, List quoteList) throws IOException ;

    public List getPollList(String pollListName);

    public void setPollList(String pollListName, List pollList) throws IOException ;

    public imcode.server.document.DocumentDomainObject getDocument(int meta_id) ;

    public boolean checkAdminRights(UserDomainObject user) ;
    public void setReadrunnerUserData(UserDomainObject user, ReadrunnerUserData rrUserData) ;

    public ReadrunnerUserData getReadrunnerUserData(UserDomainObject user) ;

    /**
       Retrieve the texts for a internalDocument
       @param meta_id The id of the internalDocument.
       @return A Map (Integer -> TextDocumentTextDomainObject) with all the  texts in the internalDocument.
    **/
    public Map getTexts(int meta_id);


    public int getSessionCounter();

    public String getSessionCounterDate();

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

    void deleteChilds( int meta_id, int doc_menu_no, UserDomainObject user, String[] childsThisMenu );

    void updateLogs( String logMessage );

    ConnectionPool getConnectionPool();

    DocumentMapper getDocumentMapper();

    ImcmsAuthenticatorAndUserMapper getUserAndRoleMapper();

    String getDefaultLanguageAsIso639_2();

    Hashtable sqlProcedureHash( String procedure, String[] params );

    int sqlUpdateProcedure( String procedure, String[] params );

    String sqlProcedureStr( String procedure, String[] params );

    int sqlUpdateQuery(String sqlStr, String[] params);

    void saveTreeSortIndex( int meta_id, UserDomainObject user, List childs, List sort_no, int menuNumber);

    String[][] sqlProcedureMulti(String procedure, String[] params);

    String[] sqlQuery(String sqlStr, String[] params);

    String sqlQueryStr(String sqlStr, String[] params);

    Hashtable sqlQueryHash(String sqlStr, String[] params);

    String[][] sqlQueryMulti(String sqlstr, String[] params);

    public String getFilename(int meta_id) ;
}
