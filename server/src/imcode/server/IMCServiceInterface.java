/******************************************************************************************
* IMCServiceInterface.java                                                                *
* Copyright Magnum Software 1998,1999                                                     *
*-----------------------------------------------------------------------------------------*
* SYNOPSIS:                                                                               *
* Outline     : The interface for the Imcode Net Server services                          *
*-----------------------------------------------------------------------------------------*
* Author      : Magnus Isenberg : Magnum Software (c) 1998,1999                           *
*-----------------------------------------------------------------------------------------*
* PLATFORM    : PC/MAC/SOLARIS                             					              *
* ENVIRONMENT : WINDOWS 95/98/NT MacOS UNIX OS2 LINUX runs from command line.             *
* TOOLS       : JavaSoft JDK1.2, KAWA IDE                    				              *
* REFERENCE   : The Java Class Libraries 1 & 2             		                          *
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

import java.util.* ;


/**
  * <p<Interface for the Imcode Net Server.
  */
public interface IMCServiceInterface extends java.rmi.Remote {

	// Verify a Internet/Intranet user. Data from any SQL Database.
	imcode.server.User verifyUser(imcode.server.LoginUser login_user,String fieldNames[])
	throws java.rmi.RemoteException ;

	// Read an Imcode admin template file and output a HTML String object.
	String interpretAdminTemplate(int meta_id,imcode.server.User user,String template_name,int index,
		int value1,int value2,int value3)
	throws java.rmi.RemoteException ;

	// Save a text field
	void saveText(int meta_id,imcode.server.User user,int txt_no,String text,int toHTML)
	throws java.rmi.RemoteException ;

	byte[] parsePage(int meta_id, imcode.server.User user, int flags) throws java.io.IOException ;

	// Save a url
	void saveUrl(int meta_id,imcode.server.User user,imcode.server.Table doc)
	throws java.rmi.RemoteException ;


/*	// Save a doc
	int saveNewDoc(int meta_id,imcode.server.User user,imcode.server.Table doc,int doc_menu_no,
		int roles[],int user_rights[])
	throws java.rmi.RemoteException ;
*/
	// Save a doc
/*	void saveDoc(int meta_id,imcode.server.User user,imcode.server.Table doc,int roles[],int user_rights[])
	throws java.rmi.RemoteException ;
*/
	// Save an image
	void saveImage(int meta_id,User user,int img_no,imcode.server.Image image)
	throws java.rmi.RemoteException ;


	// Delete doc
/*	void deleteDoc(int meta_id,imcode.server.User user,int to_meta_id)
	throws java.rmi.RemoteException ;
*/
	// Delete all doc
	void deleteDocAll(int meta_id,imcode.server.User user)
	throws java.rmi.RemoteException ;

	// add existing doc
	void addExistingDoc(int meta_id,imcode.server.User user,int existing_meta_id,int doc_menu_no)
	throws java.rmi.RemoteException ;

	// save manual sort
	void saveManualSort(int meta_id,imcode.server.User user,java.util.Vector childs, java.util.Vector sort_no)
	throws java.rmi.RemoteException ;

	// delete childs
	void deleteChilds(int meta_id,int menu,imcode.server.User user,String childsThisMenu[])
	throws java.rmi.RemoteException ;

	// archive childs
	void archiveChilds(int meta_id,imcode.server.User user,String childsThisMenu[])
	throws java.rmi.RemoteException ;

	// save textdoc
	public void saveTextDoc(int meta_id,imcode.server.User user,imcode.server.Table doc)
	throws java.rmi.RemoteException ;

	// Save a new browser doc
	void saveNewBrowserDoc(int meta_id,imcode.server.User user,imcode.server.Table doc)
	throws java.rmi.RemoteException ;

	// Check if browser doc
	int isBrowserDoc(int meta_id,imcode.server.User user)
	throws java.rmi.RemoteException ;

	// Save a url_doc
	void saveUrlDoc(int meta_id,imcode.server.User user,imcode.server.Table doc)
	throws java.rmi.RemoteException ;

	// Save a new url_doc
	void saveNewUrlDoc(int meta_id,imcode.server.User user,imcode.server.Table doc)
	throws java.rmi.RemoteException ;

	// List all archived docs
	String listArchive(int meta_id,imcode.server.User user)
	throws java.rmi.RemoteException ;

	// check if url doc
	imcode.server.Table isUrlDoc(int meta_id,User user)
	throws java.rmi.RemoteException ;


	// Save a new frameset
	void saveNewFrameset(int meta_id,imcode.server.User user,imcode.server.Table doc)
	throws java.rmi.RemoteException ;

	// Save a frameset
	void saveFrameset(int meta_id,imcode.server.User user,imcode.server.Table doc)
	throws java.rmi.RemoteException ;

	// check if url doc
	String isFramesetDoc(int meta_id,User user)
	throws java.rmi.RemoteException ;

	// track user
	void updateTrackLog(int from_meta_id,int to_meta_id,imcode.server.User user)
	throws java.rmi.RemoteException ;

	// search docs
	Vector searchDocs(int meta_id,User user,String question_str,
		String search_type,String string_match,String search_area)
	throws java.rmi.RemoteException ;

	// get home page meta_id
	int getDefaultHomePage() throws java.rmi.RemoteException ;


	// check if external doc
	ExternalDocType isExternalDoc(int meta_id,User user)
	throws java.rmi.RemoteException ;


	// remove child from child table
	void removeChild(int meta_id,int parent_meta_id,imcode.server.User user)
	throws java.rmi.RemoteException ;

	// activate child to child table
	void activateChild(int meta_id,User user)
	throws java.rmi.RemoteException ;
	// make child inactive
	void inActiveChild(int meta_id,User user)
	throws java.rmi.RemoteException ;

/*
	// test if user is admin
	boolean userIsAdmin(int meta_id,User user)
	throws java.rmi.RemoteException ;

	// test if user is superadmin
	boolean userIsAdmin(User user)
	throws java.rmi.RemoteException ;
*/

	// Parse doc replace variables with data
	String  parseDoc(String htmlStr,java.util.Vector variables)
	throws java.rmi.RemoteException ;

	// Send a sqlquery to the database and return a string array
	String[] sqlQuery(String sqlQuery)
	throws java.rmi.RemoteException ;

	// Send a sqlquery to the database/set database and return a string array
	String[] sqlQuery(String sqlQuery,String catalog)
	throws java.rmi.RemoteException ;

	// Send a sql update query to the database
	void sqlUpdateQuery(String sqlStr) throws java.rmi.RemoteException ;


	// Send a sqlquery to the database and return a string
	String sqlQueryStr(String sqlQuery)
	throws java.rmi.RemoteException ;


	// Send a procedure to the database and return a string array
	public String[] sqlProcedure(String procedure)
	throws java.rmi.RemoteException ;

	// Send a procedure to the database and return a string
	public String sqlProcedureStr(String procedure)
	throws java.rmi.RemoteException ;

	// Send a update procedure to the database
	public void sqlUpdateProcedure(String procedure)
	throws java.rmi.RemoteException ;


	// Parse doc replace variables with data, uses two vectors
	String  parseDoc(String htmlStr,java.util.Vector variables,java.util.Vector data)
	throws java.rmi.RemoteException ;

	// Insert new texts
/*	void insertNewTexts(int meta_id,imcode.server.User user,int no_of_txt)
	throws java.rmi.RemoteException ;
*/
	// get number of textfields
	int getNoOfTxt(int meta_id,imcode.server.User user)
	throws java.rmi.RemoteException ;


/*	// Insert new images
	void insertNewImages(int meta_id,imcode.server.User user,int no_of_img)
	throws java.rmi.RemoteException ;
*/
	// get external template folder
	String getExternalTemplateFolder(int meta_id)
	throws java.rmi.RemoteException ;


	// get internal template folder
	String getInternalTemplateFolder(int meta_id) throws java.rmi.RemoteException ;

	// increment session counter
	int incCounter() throws java.rmi.RemoteException ;

	// get session counter
	int getCounter() throws java.rmi.RemoteException ;

	// set session counter
	int setCounter(int value) throws java.rmi.RemoteException ;


	// set  session counter date
	boolean setCounterDate(String date) throws java.rmi.RemoteException ;

	// set  session counter date
	String getCounterDate() throws java.rmi.RemoteException ;

/*
	// has user rights to edit document
	boolean userHasEditRights(int meta_id,imcode.server.User user)
	throws java.rmi.RemoteException ;
*/

	// Send a sqlquery to the database and return a string array and metadata
	String[] sqlQueryExt(String sqlQuery)
	throws java.rmi.RemoteException ;

	// Send a procedure to the database and return a string array
	public String[] sqlProcedureExt(String procedure)
	throws java.rmi.RemoteException ;


	// Send a sqlquery to the database and return a Hashtable
	public Hashtable sqlQueryHash(String sqlQuery)
	throws java.rmi.RemoteException ;

	// Send a procedure to the database and return a Hashtable
	public Hashtable sqlProcedureHash(String procedure)
	throws java.rmi.RemoteException ;

	// Create meta info
/*	public int saveNewMeta(int meta_id,imcode.server.Table doc)
	throws java.rmi.RemoteException ;
*/
	// Delete meta
/*	public boolean deleteMeta(int meta_id)
	throws java.rmi.RemoteException ;
*/

	// parsedoc use template
	public String  parseDoc(java.util.Vector variables,String admin_template_name,
		String lang_prefix) throws java.rmi.RemoteException ;


	// get templatehome
	public byte[] getTemplate(int template_id)
	throws java.io.IOException ;

	// get templatehome
	public String getTemplateHome()
	throws java.rmi.RemoteException ;

	// get imagehome
	public String getImageHome()
	throws java.rmi.RemoteException ;

	// get language
	public String getLanguage()
	throws java.rmi.RemoteException ;

	// get doctype
	public int getDocType(int meta_id)
	throws java.rmi.RemoteException ;

	// checkDocAdminRights
	public boolean checkDocAdminRights(int meta_id, User user)
	throws java.rmi.RemoteException ;

	// save template to disk
	public  int saveTemplate(String name,String file_name,byte[] data,boolean overwrite,String lang_prefix)
	throws java.rmi.RemoteException ;

	// get demo template data
	public Object[] getDemoTemplate(int template_id)
	throws java.io.IOException ;

	// check if user can view document
	public boolean checkDocRights(int meta_id, User user)
	throws java.rmi.RemoteException ;

	public boolean checkDocAdminRights(int meta_id, User user, int permissions)
	throws java.rmi.RemoteException ;

	public boolean checkDocAdminRightsAny(int meta_id, User user, int permissions)
	throws java.rmi.RemoteException ;

	// delete template from db/disk
	public void deleteTemplate(int template_id)
	throws java.rmi.RemoteException ;

	// change templatename
/*	public void changeTemplateName(int template_id,String new_name)
	throws java.rmi.RemoteException ;
*/

	// save demo template
	public int saveDemoTemplate(int template_id,byte [] data, String suffix)
	throws java.rmi.RemoteException ;


	// save templategroup
	public void saveTemplateGroup(String group_name,User user)
	throws java.rmi.RemoteException ;

	// delete templategroup
	public void deleteTemplateGroup(int group_id)
	throws java.rmi.RemoteException ;


	// save templategroup
	public void changeTemplateGroupName(int group_id,String new_name)
	throws java.rmi.RemoteException ;


	//  assign template to templategroups
/*	public void assignTemplate(int template_id,int group_id[])
	throws java.rmi.RemoteException ;
*/
	//  unassign template from templategroups
	public void unAssignTemplate(int template_id,int group_id[])
	throws java.rmi.RemoteException ;

	// Send a procedure to the database and return a multistring array
	public String[][] sqlProcedureMulti(String procedure)
	throws java.rmi.RemoteException ;

	// Send a sqlQuery to the database and return a multistring array
	public String[][] sqlQueryMulti(String sqlQuery)
	throws java.rmi.RemoteException ;

	// get server date
	public Date getCurrentDate()
	throws java.rmi.RemoteException ;


   // get demotemplates
   public String[] getDemoTemplateList()
    throws java.rmi.RemoteException ;

   // delete demotemplate
   public int deleteDemoTemplate(int template_id)
   	throws java.rmi.RemoteException ;

   public String getMenuButtons(int meta_id, User user)
	throws java.rmi.RemoteException ;

   public String getMenuButtons(String meta_id, User user)
    throws java.rmi.RemoteException ;

   public String getLanguage(String lang_id)
	throws java.rmi.RemoteException ;


    public SystemData getSystemData()     throws java.rmi.RemoteException ;

    public void setSystemData(SystemData sd)     throws java.rmi.RemoteException ;

    // Get the information for each selected metaid. Used by existing documents
    public Hashtable ExistingDocsGetMetaIdInfo( String[] meta_id)  throws java.rmi.RemoteException ;

     public String[] getDocumentTypesInList(String langPrefixStr) throws java.rmi.RemoteException ;

     public Hashtable getDocumentTypesInHash(String langPrefixStr) throws java.rmi.RemoteException  ;




}
