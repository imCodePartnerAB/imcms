/******************************************************************************************
 * IMCServiceInterface.java                                                                *
 * Copyright Magnum Software 1998,1999                                                     *
 *-----------------------------------------------------------------------------------------*
 * SYNOPSIS:                                                                               *
 * Outline     : The interface for the Imcode Net Server services                          *
 *-----------------------------------------------------------------------------------------*
 * Author      : Magnus Isenberg : Magnum Software (c) 1998,1999                           *
 *-----------------------------------------------------------------------------------------*
 * PLATFORM    : PC/MAC/SOLARIS					              *
 * ENVIRONMENT : WINDOWS 95/98/NT MacOS UNIX OS2 LINUX runs from command line.             *
 * TOOLS       : JavaSoft JDK1.2, KAWA IDE				              *
 * REFERENCE   : The Java Class Libraries 1 & 2		                          *
 *               Getting Staring Using RMI (www.javasoft.com)                              *
 * Thanks to   : Andreas Bengtsson : Software Engineer : Entra Memtek Education AB         *
 *             : Hasse Brattberg   : Software Engineer : Entra Memtek Education AB         *
 *             : Roger Larsson     : HTML Programmer   : Visby Interactive Studio AB       *
 *-----------------------------------------------------------------------------------------*
 * Last Update : 17:00 17-02-1999                                                          *
 *-----------------------------------------------------------------------------------------*
 * REVISION HISTORY :                                                                      *
 * 17-02-1999 : MI  : First Written                                                        *
 ******************************************************************************************/
package imcode.server ;

import java.io.* ;
import java.util.* ;
import imcode.server.parser.ParserParameters ;

/**
 * Interface for the Imcode Net Server.
 */
public interface IMCServiceInterface {

    final static String CVS_REV = "$Revision$" ;
    final static String CVS_DATE = "$Date$" ;

    // Verify a Internet/Intranet user. Data from any SQL Database.
    imcode.server.User verifyUser(imcode.server.LoginUser login_user,String fieldNames[])
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

    void saveManualSort(int meta_id,imcode.server.User user,java.util.Vector childs, java.util.Vector sort_no)
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

    // Check if browser doc
    int isBrowserDoc(int meta_id,imcode.server.User user)
	;

    // Save a url_doc
    void saveUrlDoc(int meta_id,imcode.server.User user,imcode.server.Table doc)
	;

    // Save a new url_doc
    void saveNewUrlDoc(int meta_id,imcode.server.User user,imcode.server.Table doc)
	;

    // List all archived docs
    //    String listArchive(int meta_id,imcode.server.User user)
    //;

    // check if url doc
    imcode.server.Table isUrlDoc(int meta_id,User user)
	;

    // Save a new frameset
    void saveNewFrameset(int meta_id,imcode.server.User user,imcode.server.Table doc)
	;

    // Save a frameset
    void saveFrameset(int meta_id,imcode.server.User user,imcode.server.Table doc)
	;

    // check if url doc
    String isFramesetDoc(int meta_id,User user)
	;

    // search docs
    Vector searchDocs(int meta_id,User user,String question_str,
		      String search_type,String string_match,String search_area)
	;

    // get home page meta_id
    int getDefaultHomePage()  ;

    // check if external doc
    ExternalDocType isExternalDoc(int meta_id,User user)
	;

    // remove child from child table
    void removeChild(int meta_id,int parent_meta_id,imcode.server.User user)
	;

    // activate child to child table
    void activateChild(int meta_id,User user)
	;
    // make child inactive
    void inActiveChild(int meta_id,User user)
	;

    // Parse doc replace variables with data
    String  parseDoc(String htmlStr,java.util.Vector variables)
	;

    // Send a sqlquery to the database and return a string array
    String[] sqlQuery(String sqlQuery)
	;

    // Send a sqlquery to the database/set database and return a string array
    String[] sqlQuery(String sqlQuery,String catalog)
	;

    // Send a sql update query to the database
    void sqlUpdateQuery(String sqlStr)  ;

    // Send a sqlquery to the database and return a string
    String sqlQueryStr(String sqlQuery)
	;

    // Send a procedure to the database and return a string array
    public String[] sqlProcedure(String procedure)
	;

    // Send a procedure to the database and return a string array
    public String[] sqlProcedure(String procedure, String[] params)
	;

    // Send a procedure to the database and return a string
    public String sqlProcedureStr(String procedure)
	;

    // Send a update procedure to the database
    public int sqlUpdateProcedure(String procedure)
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
    boolean setCounterDate(String date)  ;

    // set  session counter date
    String getCounterDate()  ;

    // Send a sqlquery to the database and return a string array and metadata
    String[] sqlQueryExt(String sqlQuery)
	;

    // Send a procedure to the database and return a string array
    public String[] sqlProcedureExt(String procedure)
	;

    // Send a sqlquery to the database and return a Hashtable
    public Hashtable sqlQueryHash(String sqlQuery)
	;

    // Send a procedure to the database and return a Hashtable
    public Hashtable sqlProcedureHash(String procedure)
	;

    // parsedoc use template
    public String  parseDoc(java.util.Vector variables,String admin_template_name,
			    String lang_prefix)  ;

    // parseExternaldoc use template
    public String parseExternalDoc(java.util.Vector variables, String external_template_name, String lang_prefix, String doc_type)
	;

    // parseExternaldoc use template
    public String parseExternalDoc(java.util.Vector variables, String external_template_name, String lang_prefix, String doc_type, String templateSet)
	;

    // get templatehome
    public byte[] getTemplateData(int template_id)
	throws IOException ;

    // get templatehome
    public File getTemplateHome()
	;

    // get imagehome
    public String getImageHome()
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
    public int saveDemoTemplate(int template_id,byte [] data, String suffix)
	;

    // save templategroup
    public void saveTemplateGroup(String group_name,User user)
	;

    // delete templategroup
    public void deleteTemplateGroup(int group_id)
	;

    // save templategroup
    public void changeTemplateGroupName(int group_id,String new_name)
	;

    //  unassign template from templategroups
    public void unAssignTemplate(int template_id,int group_id[])
	;

    // Send a procedure to the database and return a multistring array
    public String[][] sqlProcedureMulti(String procedure)
	;

    // Send a sqlQuery to the database and return a multistring array
    public String[][] sqlQueryMulti(String sqlQuery)
	;

    // get server date
    public Date getCurrentDate()
	;

    // get demotemplates
    public String[] getDemoTemplateList()
	;

    // delete demotemplate
    public int deleteDemoTemplate(int template_id)
	;

    public String getMenuButtons(int meta_id, User user)  ;

    public String getMenuButtons(String meta_id, User user)  ;

    public String getLanguage(String lang_id)  ;

    public SystemData getSystemData()  ;

    public void setSystemData(SystemData sd)  ;

    // Get the information for each selected metaid. Used by existing documents
    // Wow. Wonderful methodname. Indeed. Just beautiful.
    public Hashtable ExistingDocsGetMetaIdInfo( String[] meta_id)   ;

    public String[] getDocumentTypesInList(String langPrefixStr)  ;

    public Hashtable getDocumentTypesInHash(String langPrefixStr)   ;

    public boolean checkUserDocSharePermission(User user, int meta_id)  ;

    public String getInclude(String path) throws IOException ;

    public String getFortune(String path) throws IOException ;

    public String getSearchTemplate(String path) throws IOException ;

    public File getInternalTemplateFolder(int meta_id) ;

    public void touchDocument(int meta_id, java.util.Date date) ;

    public void touchDocument(int meta_id) ;

    public List getQuoteList(String quoteListName) throws IOException ;

    public void setQuoteList(String quoteListName, List quoteList) throws IOException ;

    public List getPollList(String pollListName) throws IOException ;

    public void setPollList(String pollListName, List pollList) throws IOException ;

    public imcode.server.parser.Document getDocument(int meta_id) ;

    public String getSection(int meta_id) ;

    public String getFilename(int meta_id) ;

    public Template getTemplate(int meta_id) ;
}
