package imcode.server ;

import java.io.* ;
import java.util.* ;
import imcode.server.parser.ParserParameters ;
import imcode.readrunner.* ;

/**
 * Interface for the Imcode Net Server.
 */
public interface IMCServiceInterface {

    /** Verify a Internet/Intranet user. Data from any SQL Database. **/
    imcode.server.User verifyUser(String login, String password)
	;

    /** Get a user by user-id **/
    imcode.server.User getUserById(int userId)
	;


    /** Check if a user has a special admin role **/
    public boolean checkUserAdminrole ( int userId, int adminRole )
	;

    /**
       Save a text field
    **/
    void saveText(imcode.server.User user,int meta_id,int txt_no,IMCText text)
	;

    /**
       Retrieve a text-field
    **/
    IMCText getText(int meta_id,int txt_no)
	;

    String parsePage(DocumentRequest docReq, int flags, ParserParameters paramsToParse) throws IOException ;

    // Save an image
    void saveImage(int meta_id,User user,int img_no,imcode.server.Image image)
	;

    /**
       Delete a document
    **/
    void deleteDocAll(int meta_id,imcode.server.User user)
	;

    void addExistingDoc(int meta_id,imcode.server.User user,int existing_meta_id,int doc_menu_no)
	;

    void saveManualSort(int meta_id,imcode.server.User user,List childs, List sort_no, int menuNumber)
	;

    /**
       Remove children from a menu
    **/
    void deleteChilds(int meta_id,int menu,imcode.server.User user,String childsThisMenu[])
	;

    // archive childs
    void archiveChilds(int meta_id,imcode.server.User user,String childsThisMenu[])
	;

    /** Copy documents and insert them in a new textdocument and menu **/
    String[] copyDocs( int meta_id, int doc_menu_no,  User user, String[] childsThisMenu, String copyPrefix)  ;

    // save textdoc
    public void saveTextDoc(int meta_id,imcode.server.User user,imcode.server.Table doc)
	;

    // List all archived docs
    //    String listArchive(int meta_id,imcode.server.User user)
    //;

    // check if url doc
    String isUrlDoc(int meta_id,User user)
	;

    // Save a new frameset
    void saveNewFrameset(int meta_id,imcode.server.User user,String html)
	;

    // Save a frameset
    void saveFrameset(int meta_id,imcode.server.User user,String html)
	;

    // check if url doc
    String isFramesetDoc(int meta_id,User user)
	;

    // check if external doc
    ExternalDocType isExternalDoc(int meta_id,User user)
	;

    // activate child to child table
    void activateChild(int meta_id,User user)
	;

    // Send a procedure to the database and return a string array
    public String[] sqlProcedure(String procedure, String[] params)
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
    public String getLanguage()
	;

    // get doctype
    public int getDocType(int meta_id)
	;

    // checkDocAdminRights
    public boolean checkDocAdminRights(int meta_id, User user)
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

    // check if user can view document
    public boolean checkDocRights(int meta_id, User user)
	;

    public boolean checkDocAdminRights(int meta_id, User user, int permissions)
	;

    public boolean checkDocAdminRightsAny(int meta_id, User user, int permissions)
	;

    // delete template from db/disk
    public void deleteTemplate(int template_id)
	;

    // save demo template
    public int saveDemoTemplate(int template_id,byte [] data, String suffix) throws IOException
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
    public void deleteDemoTemplate(int template_id) throws IOException
    ;

    public String getMenuButtons(int meta_id, User user)  ;

    public String getMenuButtons(String meta_id, User user)  ;

    public SystemData getSystemData()  ;

    public void setSystemData(SystemData sd)  ;

    public String[] getDocumentTypesInList(String langPrefixStr)  ;

    public boolean checkUserDocSharePermission(User user, int meta_id)  ;

    public String getFortune(String path) throws IOException ;

    public String getSearchTemplate(String path) throws IOException ;

    public void touchDocument(int meta_id) ;

    public List getQuoteList(String quoteListName);

    public void setQuoteList(String quoteListName, List quoteList) throws IOException ;

    public List getPollList(String pollListName);

    public void setPollList(String pollListName, List pollList) throws IOException ;

    public imcode.server.parser.Document getDocument(int meta_id) ;

    public String[] getSections(int meta_id) ;

    public String getFilename(int meta_id) ;

    public boolean checkAdminRights(imcode.server.User user) ;
    public void setReadrunnerUserData(User user, ReadrunnerUserData rrUserData) ;

    public ReadrunnerUserData getReadrunnerUserData(User user) ;

    /**
       Retrieve the texts for a document
       @param meta_id The id of the document.
       @return A Map (Integer -> IMCText) with all the  texts in the document.
    **/
    public Map getTexts(int meta_id);


    public int getSessionCounter();

    public String getSessionCounterDate();

    /** Get all possible userflags **/
    public Map getUserFlags() ;
    /** Get all userflags for a single user **/
    public Map getUserFlags(User user) ;
    /** Get all userflags of a single type **/
    public Map getUserFlags(int type) ;
    /** Get all userflags for a single user of a single type **/
    public Map getUserFlags(User user, int type) ;

    public void setUserFlag(User user, String flagName);

    public void unsetUserFlag(User user, String flagName);

    Hashtable sqlProcedureHash( String procedure, String[] params );

    int sqlUpdateProcedure( String procedure, String[] params );

    String sqlProcedureStr( String procedure, String[] params );

    int sqlUpdateQuery(String sqlStr, String[] params);

    void saveTreeSortIndex( int meta_id, User user, List childs, List sort_no, int menuNumber);

    String[][] sqlProcedureMulti(String procedure, String[] params);

    String[] sqlQuery(String sqlStr, String[] params);

    String sqlQueryStr(String sqlStr, String[] params);

    Hashtable sqlQueryHash(String sqlStr, String[] params);

    String[][] sqlQueryMulti(String sqlstr, String[] params);

}
