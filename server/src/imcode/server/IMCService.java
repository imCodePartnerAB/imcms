package imcode.server ;

import org.apache.oro.util.* ;
import org.apache.oro.text.* ;
import org.apache.oro.text.regex.* ;
import org.apache.oro.text.perl.* ;
import java.sql.*;
import java.sql.Date ;
import java.io.*;
import java.util.*;
import java.text.Collator ;
import java.text.SimpleDateFormat ;
import java.net.URL ;
import java.net.MalformedURLException ;

import imcode.server.* ;
import imcode.server.parser.* ;
import imcode.util.log.* ;
import imcode.util.FileCache ;

/**
   Main services for the Imcode Net Server.
   Made final, since only a complete and utter moron would want to extend it.
**/
final public class IMCService implements IMCServiceInterface, IMCConstants {
    
    private final imcode.server.InetPoolManager m_conPool ; // inet pool of connections
    private TextDocumentParser textDocParser ;
    
    private String m_TemplateHome ;           // template home
    private String m_IncludePath ;
    private int m_DefaultHomePage ;        // default home page
    private String m_ServletUrl  ;			   // servlet url
    private String m_ImageFolder ;            // image folder
    private String m_Language          = "" ;      // language
    private String m_serverName        = "" ;      // servername

    private SystemData sysData ;
    
    private ExternalDocType m_ExDoc[] ;
    private String m_SessionCounterDate = "" ;
    private int m_SessionCounter = 0 ;
    private int m_NoOfTemplates  ;
    
    private FileCache fileCache = new FileCache() ;

    private Log log = Log.getLog("server") ;
    
    /**
     * <p>Contructs an IMCService object.
     */
    //	public IMCService(ConnectionPool conPool,javax.swing.JTextArea output,String serverName)
    public IMCService(imcode.server.InetPoolManager conPool,Properties props) {
	super();
	m_conPool    = conPool ;
	    
	sysData = getSystemDataFromDb() ;
	    
	m_TemplateHome      = props.getProperty("TemplatePath").trim() ;
	log.log(Log.INFO, "TemplatePath: " + m_TemplateHome) ;
	    
	m_IncludePath       = props.getProperty("IncludePath").trim() ;
	log.log(Log.INFO, "IncludePath: " + m_IncludePath) ;
	    
	try {
	    m_DefaultHomePage   = Integer.parseInt(props.getProperty("StartDocument").trim()) ;    //FIXME: Get from DB
	} catch (NumberFormatException ex) {
	    throw new RuntimeException ("No StartDocument given in properties-file.") ;
	} catch (NullPointerException ex) {
	    throw new RuntimeException ("No StartDocument given in properties-file.") ;
	}

	log.log(Log.INFO, "StartDocument: " + m_DefaultHomePage) ;
	    
	m_ServletUrl        = props.getProperty("ServletUrl").trim() ; //FIXME: Get from webserver, or get rid of if possible.
	log.log(Log.INFO, "ServletUrl: " + m_ServletUrl) ;
	    
	// FIXME: Get imageurl from webserver somehow. The user-object, perhaps?
	m_ImageFolder       = props.getProperty("ImageUrl").trim() ; //FIXME: Get from webserver, or get rid of if possible.
	log.log(Log.INFO, "ImageUrl: " + m_ImageFolder) ;
	    
	String externalDocTypes  = props.getProperty("ExternalDoctypes").trim() ; //FIXME: Get rid of, if possible.
	log.log(Log.INFO, "ExternalDoctypes: " + externalDocTypes) ;
	    
	m_Language          = props.getProperty("DefaultLanguage").trim() ; //FIXME: Get from DB
	log.log(Log.INFO, "DefaultLanguage: " + m_Language) ;
	    
	    
	StringTokenizer doc_types = new StringTokenizer(externalDocTypes,";",false) ;
	m_ExDoc = new ExternalDocType[doc_types.countTokens()] ;
	try {
	    for (int doc_count=0; doc_types.hasMoreTokens() ; ++doc_count) {
		StringTokenizer tempStr = new StringTokenizer(doc_types.nextToken(),":",false)  ;
		String items[] = new String[tempStr.countTokens()] ;
		for ( int i=0 ; tempStr.hasMoreTokens() ; ++i ) {
		    items[i] = tempStr.nextToken() ;
		}
		m_ExDoc[doc_count] = new ExternalDocType(Integer.parseInt(items[0]),items[1],items[2],"") ;
	    }
	}
	catch(NoSuchElementException e) {
	    e.printStackTrace() ;
	}
	    
	    
	try {
	    m_SessionCounter     = Integer.parseInt(this.sqlProcedureStr("GetCurrentSessionCounter")) ;
	    m_SessionCounterDate = this.sqlProcedureStr("GetCurrentSessionCounterDate") ;
	    //m_NoOfTemplates      = this.sqlProcedureInt("GetNoOfTemplates") ;
	} catch ( NumberFormatException ex ) {
	    log.log(Log.CRITICAL, "Failed to get SessionCounter from db.", ex) ;
	    throw ex ;
	}
	    
	//m_Template = new Template[m_NoOfTemplates] ;
	    
	log.log(Log.INFO, "SessionCounter: "+m_SessionCounter) ;
	log.log(Log.INFO, "SessionCounterDate: "+m_SessionCounterDate) ;
	//log.log(Log.INFO, "TemplateCount: "+m_NoOfTemplates) ;

	textDocParser = new TextDocumentParser(this, m_conPool,new File(m_TemplateHome),new File(m_IncludePath),m_ImageFolder,m_ServletUrl) ;
    }
    
    
    public int getSessionCounter() {
	return m_SessionCounter ;
    }
    
    public String getSessionCounterDate() {
	return m_SessionCounterDate ;
    }

    /**
     * <p>Get me page _id.
     */
    public int getDefaultHomePage() {
	return m_DefaultHomePage ;
    }
    
    /**
     * <p>Verify a Internet/Intranet user. User data retrived from SQL Database.
     */
    public imcode.server.User verifyUser(LoginUser login_user,String fieldNames[]) {
	String sqlStr = "" ;
	User user = new User() ;
	DBConnect dbc = new DBConnect(m_conPool,login_user.createLoginQuery()) ;
	dbc.getConnection() ;
	dbc.createStatement() ;
	Vector user_data = (Vector)dbc.executeQuery() ;
	dbc.clearResultSet() ;


	// if resultSet > 0 a user is found
	if ( user_data.size() > 0 ) {
	    user.setFields(fieldNames,user_data) ;
	    // add roles to user
	    sqlStr  = "select role_id from user_roles_crossref where user_id = " ;
	    sqlStr += user.getInt("user_id") ;
	    dbc.setSQLString(sqlStr) ;
	    dbc.createStatement() ;
	    Vector user_roles = (Vector)dbc.executeQuery() ;
	    dbc.clearResultSet() ;
	    user.addObject("user_roles",user_roles) ;

	    String login_password_from_db = user.getString(login_user.getLoginPasswordFieldName()).trim() ;
	    String login_password_from_form = login_user.getLoginPassword() ;

	    if ( login_password_from_db.equals(login_password_from_form) && user.getBoolean("active"))
		this.updateLogs(new java.util.Date() + "->User "	 + login_user.getLoginName()
				+ " succesfully logged in.") ;
	    else if (!user.getBoolean("active") ) {
		this.updateLogs(new java.util.Date() + "->User "	 + (login_user.getLoginName()).trim()
				+ " tried to logged in: User deleted!") ;
		dbc.closeConnection() ;
		dbc = null ;
		return null ;
	    } else {
		this.updateLogs(new java.util.Date() + "->User "	 + (login_user.getLoginName()).trim()
				+ " tried to logged in: Wrong password!") ;
		dbc.closeConnection() ;
		dbc = null ;
		return null ;
	    }

	} else {
	    this.updateLogs(new java.util.Date() + "->User " + login_user.getLoginName()
			    + " tried to logged in: User not found!") ;
	    dbc.closeConnection() ;
	    dbc = null ;
	    return null ;
	}

	// Get the users language prefix
	String lang_prefix = null ;
	sqlStr = "select lang_prefix from lang_prefixes where lang_id = "+user.getLangId() ;	// Find language
	dbc.setSQLString(sqlStr) ;
	dbc.createStatement() ;
	Vector lang_prefix_data = (Vector)dbc.executeQuery() ;
	if ( lang_prefix_data.size() > 0 ) {
	    lang_prefix = lang_prefix_data.elementAt(0).toString() ;
	    user.put("lang_prefix",lang_prefix) ;
	}

	dbc.closeConnection() ;
	dbc = null ;

	return user ;
    }

    public byte[] parsePage (int meta_id, User user, int flags,String template) throws IOException {
	return textDocParser.parsePage(meta_id,user,flags,1,template) ;
    }

    /**
       Returns the menubuttonrow
    */
    public String getMenuButtons(String meta_id, User user) {
	try {
	    // Get the users language prefix
	    String lang_prefix = null ;
	    String sqlStr = "select lang_prefix from lang_prefixes where lang_id = "+user.getInt("lang_id") ;	// Find language
	    DBConnect dbc = new DBConnect(m_conPool,sqlStr) ;
	    dbc.getConnection() ;
	    dbc.createStatement() ;
	    Vector data = (Vector)dbc.executeQuery() ;
	    if ( data.size() > 0 ) {
		lang_prefix = data.elementAt(0).toString() ;
	    }
	    dbc.clearResultSet() ;

	    // Find out what permissions the user has
	    sqlStr = "GetUserPermissionSet (?,?)" ;
	    String[] sqlAry = {String.valueOf(meta_id),String.valueOf(user.getInt("user_id"))} ;
	    dbc.setProcedure(sqlStr,sqlAry) ;
	    Vector permissions = (Vector)dbc.executeProcedure() ;
	    dbc.clearResultSet() ;
	    dbc.closeConnection() ;

	    if (permissions.size() == 0) {
		return "" ;
	    }

	    StringBuffer tempbuffer = null ;
	    StringBuffer templatebuffer = null ;
	    StringBuffer superadmin = null ;
	    int doc_type = getDocType(Integer.parseInt(meta_id)) ;
	    try {

		String tempbuffer_filename = lang_prefix + "/admin/adminbuttons/adminbuttons"+doc_type+".html" ;
		String templatebuffer_filename = lang_prefix + "/admin/adminbuttons/adminbuttons.html" ;
		String superadmin_filename = lang_prefix + "/admin/adminbuttons/superadminbutton.html" ;

		tempbuffer = new StringBuffer(fileCache.getCachedFileString(new File(m_TemplateHome, tempbuffer_filename))) ;
		templatebuffer = new StringBuffer(fileCache.getCachedFileString(new File(m_TemplateHome, templatebuffer_filename))) ;
		superadmin = new StringBuffer(fileCache.getCachedFileString(new File(m_TemplateHome, superadmin_filename))) ;

	    } catch(IOException e) {
		log.log(Log.ERROR, "An error occurred reading adminbuttonfile", e );
		return null ;
	    }

	    int user_permission_set_id = Integer.parseInt((String)permissions.elementAt(0)) ;
	    int user_permission_set = Integer.parseInt((String)permissions.elementAt(1)) ;

	    // Replace #getMetaId# with meta_id
	    String doctype = dbc.sqlQueryStr("select type from doc_types where doc_type = "+doc_type) ;

	    imcode.util.AdminButtonParser doc_tags = new imcode.util.AdminButtonParser(m_TemplateHome + lang_prefix + "/admin/adminbuttons/adminbutton"+doc_type+"_", ".html",user_permission_set_id,user_permission_set) ;

	    doc_tags.put("getMetaId",meta_id) ;

	    imcode.util.Parser.parseTags(tempbuffer,'#'," <>\n\r\t",(Map)doc_tags,true,1) ;

	    imcode.util.AdminButtonParser tags = new imcode.util.AdminButtonParser(m_TemplateHome + lang_prefix + "/admin/adminbuttons/adminbutton_", ".html",user_permission_set_id,user_permission_set) ;

	    tags.put("getMetaId",meta_id) ;
	    tags.put("doc_buttons",tempbuffer.toString()) ;
	    tags.put("doc_type",doctype) ;

	    Vector temp = (Vector)dbc.sqlQuery("select user_id from user_roles_crossref where role_id = 0 and user_id = "+user.getInt("user_id")) ;

	    if ( temp.size() > 0 ) {
		tags.put("superadmin",superadmin.toString()) ;
	    } else {
		tags.put("superadmin","") ;
	    }

	    imcode.util.Parser.parseTags(templatebuffer,'#'," <>\n\r\t",(Map)tags,true,1) ;

	    return templatebuffer.toString() ;
	} catch ( RuntimeException ex ) {
	    log.log(Log.ERROR,"Error occurred while parsing the adminbuttons.",ex) ;
	    return null ;
	}
    }

    /**
       Returns the menubuttonrow
    */
    public String getMenuButtons(int meta_id, User user) {
	return getMenuButtons(String.valueOf(meta_id),user) ;
    }

    public void saveText(int meta_id,imcode.server.User user,int txt_no,String text,int toHTMLSpecial) {
	String sqlStr = "" ;
	Table meta ;
	String htmlStr = "" ;

	// create a db connection an get meta data
	sqlStr = "delete from texts where meta_id = " + meta_id
	    +" and name = "+txt_no ;
	DBConnect dbc = new DBConnect(m_conPool,sqlStr) ;
	dbc.getConnection() ;
	dbc.createStatement() ;
	dbc.executeUpdateQuery() ;
	dbc.clearResultSet() ;

	// update text
	sqlStr  = "insert into texts (text,type,meta_id,name)" ;

	if ( toHTMLSpecial == 0)
	    text =  imcode.server.HTMLConv.toHTMLSpecial(text) ;

	// allways convert character >= 160
	text =  imcode.server.HTMLConv.toHTML(text) ;

	sqlStr += " values('" + text + "',"
	    + toHTMLSpecial + ","+meta_id+","+txt_no+")"  ;

	dbc.setSQLString(sqlStr) ;
	dbc.createStatement() ;
	dbc.executeUpdateQuery() ;

	// close connection
	dbc.closeConnection() ;
	dbc = null ;

	this.updateLogs("Text " + txt_no +	" in  " + "[" + meta_id + "] modified by user: [" +
			user.getString("first_name").trim() + " " + user.getString("last_name").trim() + "]") ;

    }

    /**
     * <p>Save an imageref.
     */
    public void saveImage(int meta_id,User user,int img_no,imcode.server.Image image) {
	String sqlStr = "" ;
	Table meta ;


	// create a db connection an get meta data
	DBConnect dbc = new DBConnect(m_conPool) ;
	dbc.getConnection() ;
	sqlStr = "select * from images where meta_id = "+meta_id+" and name = "+img_no ;
	dbc.setSQLString(sqlStr) ;
	dbc.createStatement() ;
	if (((Vector)dbc.executeQuery()).size() > 0) {
	    sqlStr  = "update images" ;
	    sqlStr += " set imgurl      = '" + image.getImageRef() + "'" ;
	    sqlStr += ",width       = " + image.getImageWidth() ;
	    sqlStr += ",height      = " + image.getImageHeight() ;
	    sqlStr += ",border      = " + image.getImageBorder() ;
	    sqlStr += ",v_space     = " + image.getVerticalSpace() ;
	    sqlStr += ",h_space     = " + image.getHorizontalSpace() ;
	    sqlStr += ",image_name  = '" + image.getImageName() + "'" ;
	    sqlStr += ",target      = '" + image.getTarget() + "'" ;
	    sqlStr += ",target_name = '" + image.getTargetName() + "'" ;
	    sqlStr += ",align       = '" + image.getImageAlign() + "'" ;
	    sqlStr += ",alt_text    = '" + image.getAltText() + "'" ;
	    sqlStr += ",low_scr     = '" + image.getLowScr() + "'" ;
	    sqlStr += ",linkurl     = '" + image.getImageRefLink()  + "'" ;
	    sqlStr += "	where meta_id = " + meta_id ;
	    sqlStr += " and name = " + img_no ;

	    dbc.setSQLString(sqlStr) ;
	    dbc.createStatement() ;
	    dbc.executeUpdateQuery() ;
	    dbc.clearResultSet() ;

	} else {
	    sqlStr  = "insert into images (imgurl, width, height, border, v_space, h_space, image_name, target, target_name, align, alt_text, low_scr, linkurl, meta_id, name)"
		+ " values('" + image.getImageRef() + "'" ;
	    sqlStr += "," + image.getImageWidth() ;
	    sqlStr += "," + image.getImageHeight() ;
	    sqlStr += "," + image.getImageBorder() ;
	    sqlStr += "," + image.getVerticalSpace() ;
	    sqlStr += "," + image.getHorizontalSpace() ;
	    sqlStr += ",'" + image.getImageName() + "'" ;
	    sqlStr += ",'" + image.getTarget() + "'" ;
	    sqlStr += ",'" + image.getTargetName() + "'" ;
	    sqlStr += ",'" + image.getImageAlign() + "'" ;
	    sqlStr += ",'" + image.getAltText() + "'" ;
	    sqlStr += ",'" + image.getLowScr() + "'" ;
	    sqlStr += ",'" + image.getImageRefLink()  + "'" ;
	    sqlStr += "," + meta_id ;
	    sqlStr += "," + img_no+")" ;

	    dbc.setSQLString(sqlStr) ;
	    dbc.createStatement() ;
	    dbc.executeUpdateQuery() ;
	    dbc.clearResultSet() ;
	}




	this.updateLogs("ImageRef " + img_no + " =" + image.getImageRef() +
			" in  " + "[" + meta_id + "] modified by user: [" +
			user.getString("first_name").trim() + " " + user.getString("last_name").trim() + "]") ;

	// close connection
	dbc.closeConnection() ;
	dbc = null ;

    }

    /**
     * <p>Save template -> text_docs, sort
     */
    public void saveTextDoc(int meta_id,imcode.server.User user,imcode.server.Table doc) {
	String sqlStr = "" ;

	DBConnect dbc = new DBConnect(m_conPool) ;
	dbc.getConnection() ;


	sqlStr  = "update text_docs\n" ;
	sqlStr += "set template_id= "  + doc.getString("template") ;
	sqlStr += ", group_id= " + doc.getString("group_id") ;
	sqlStr += " where meta_id = " + meta_id ;
	dbc.setSQLString(sqlStr) ;
	dbc.createStatement() ;
	dbc.executeUpdateQuery() ;



	//close connection
	dbc.closeConnection() ;
	dbc = null ;


	this.updateLogs("Text docs  [" + meta_id + "] updated by user: [" +
			user.getString("first_name").trim() + " " +
			user.getString("last_name").trim() + "]") ;


    }

    /**
     * <p>Delete a doc and all data related.
     */
    public void deleteDocAll(int meta_id,imcode.server.User user) {
	String sqlStr = "DocumentDelete " + meta_id ;

	// create a db connection an get meta data
	DBConnect dbc = new DBConnect(m_conPool) ;
	dbc.getConnection() ;
	dbc.setSQLString(sqlStr) ;
	dbc.createStatement() ;
	dbc.executeUpdateQuery() ;
	this.updateLogs("Document  " + "[" + meta_id + "] ALL deleted by user: [" +
			user.getString("first_name").trim() + " " + user.getString("last_name").trim() + "]") ;

	//close connection
	dbc.closeConnection() ;
	dbc = null ;

    }

    /**
     * <p>Add a existing doc.
     */
    public void addExistingDoc(int meta_id,User user,int existing_meta_id,int doc_menu_no) {
	String sqlStr = "" ;
	int newSortNo ;



	// create a db connection an get meta data
	DBConnect dbc = new DBConnect(m_conPool) ;
	dbc.getConnection() ;

	// test if this is the first child
	sqlStr = "select to_meta_id from childs where meta_id =" +  meta_id + " and menu_sort = " + doc_menu_no ;
	dbc.setSQLString(sqlStr) ;
	dbc.createStatement() ;
	Vector child_test = (Vector)dbc.executeQuery() ;
	dbc.clearResultSet() ;

	if ( child_test.size() > 0 ) {

	    // update child table
	    sqlStr  = "select max(manual_sort_order) from childs\n" ;
	    sqlStr += "where meta_id = " + meta_id + " and menu_sort = " + doc_menu_no ;
	    dbc.setSQLString(sqlStr) ;
	    dbc.createStatement() ;
	    Vector max_sort_no = (Vector)dbc.executeQuery() ;
	    if ( max_sort_no.size() > 0 )
		newSortNo = Integer.parseInt(max_sort_no.elementAt(0).toString())  + 10 ;
	    else
		newSortNo = 500 ;
	    dbc.clearResultSet() ;
	} else
	    newSortNo = 500 ;

	sqlStr  = "insert into childs(meta_id,to_meta_id,menu_sort,manual_sort_order)\n" ;
	sqlStr += "values(" + meta_id + "," + existing_meta_id + "," + doc_menu_no + "," + newSortNo + ")" ;
	dbc.setSQLString(sqlStr) ;
	dbc.createStatement() ;
	dbc.executeUpdateQuery() ;
	this.updateLogs("(AddExisting) Child links for [" + meta_id + "] updated by user: [" +
			user.getString("first_name").trim() + " " +
			user.getString("last_name").trim() + "]") ;

	//close connection
	dbc.closeConnection() ;
	dbc = null ;

    }



    /**
     * <p>Save manual sort.
     */
    public void saveManualSort(int meta_id,User user,java.util.Vector childs,
			       java.util.Vector sort_no) {
	String sqlStr = "" ;

	// create a db connection
	DBConnect dbc = new DBConnect(m_conPool) ;
	dbc.getConnection() ;

	//	 m_output.append("Childs"  + childs.toString() + "\n");
	//	 m_output.append("sort_no" + sort_no.toString() + "\n");


	// update child table
	for ( int i = 0 ; i < childs.size() ; i++ ) {
	    sqlStr  = "update childs\n" ;
	    sqlStr += "set manual_sort_order = " + sort_no.elementAt(i).toString() + "\n" ;
	    sqlStr += "where meta_id = " + meta_id + " and \n" ;
	    sqlStr += "to_meta_id=" + childs.elementAt(i).toString() ;
	    dbc.setSQLString(sqlStr) ;
	    dbc.createStatement() ;
	    dbc.executeUpdateQuery() ;
	}

	//		m_output.append(" Done \n");


	this.updateLogs("Child manualsort for [" + meta_id + "] updated by user: [" +
			user.getString("first_name").trim() + " " +
			user.getString("last_name").trim() + "]") ;


	//close connection
	dbc.closeConnection() ;
	dbc = null ;

    }



    /**
     * <p>Delete childs from a menu.
     */
    public void deleteChilds(int meta_id,int menu,User user,String childsThisMenu[]) {
	String sqlStr = "" ;
	String childStr = "[" ;
	// create a db connection an get meta data
	DBConnect dbc = new DBConnect(m_conPool) ;
	dbc.getConnection() ;

	for ( int i = 0  ; i < childsThisMenu.length ; i++ ) {
	    sqlStr  = "delete from childs\n" ;
	    sqlStr += " where to_meta_id ="  + childsThisMenu[i]   + "\n" ;
	    sqlStr += " and meta_id = " + meta_id  ;
	    sqlStr += " and menu_sort = "+ menu ;


	    //	sqlStr += "delete from meta where meta_id ="  + meta_id  + "\n" ;
	    //  sqlStr += "delete from text_docs where meta_id ="  + meta_id  + "\n" ;
	    //	sqlStr += "delete from texts where meta_id ="  + meta_id  + "\n" ;



	    dbc.setSQLString(sqlStr) ;
	    dbc.createStatement() ;
	    dbc.executeUpdateQuery() ;

	    childStr += childsThisMenu[i] ;
	    if ( i < childsThisMenu.length -1 )
		childStr += "," ;
	}
	childStr += "]" ;

	this.updateLogs("Childs " + childStr + " from " +
			"[" + meta_id + "] deleted by user: [" +
			user.getString("first_name").trim() + " " + user.getString("last_name").trim() + "]") ;

	//close connection
	dbc.closeConnection() ;
	dbc = null ;
    }

    /**
       Makes copies of the documents given in the String-array, and inserts them into the given document and menu.
       @param meta_id The document to insert into
       @param doc_menu_no The menu to insert into
       @param user The user
       @param childsThisMenu The id's to copy.
    **/
    public void copyDocs( int meta_id, int doc_menu_no,  User user, String[] childsThisMenu) {

	if (childsThisMenu != null && childsThisMenu.length > 0) {
	    StringBuffer childs = new StringBuffer("CopyDocs '"+childsThisMenu[0]) ;

	    for (int i=1; i<childsThisMenu.length; ++i) {
		childs.append(",").append(childsThisMenu[i]) ;
	    }

	    childs.append("',"+meta_id+","+doc_menu_no+","+user.getUserId()) ;
	    sqlUpdateProcedure(childs.toString()) ;

	}

    }

    /**
     * Archive childs for a menu.
     **/
    public void archiveChilds(int meta_id,User user,String childsThisMenu[]) {
	String sqlStr = "" ;
	String childStr = "[" ;
	// create a db connection an get meta data
	DBConnect dbc = new DBConnect(m_conPool) ;
	dbc.getConnection() ;

	for ( int i = 0  ; i < childsThisMenu.length ; i++ ) {
	    sqlStr  = "update meta" ;
	    sqlStr += " set archive = 1" ;
	    sqlStr += " where meta_id ="  + childsThisMenu[i]   + "\n";


	    dbc.setSQLString(sqlStr) ;
	    dbc.createStatement() ;
	    dbc.executeUpdateQuery() ;
	    childStr += childsThisMenu[i] ;
	    if ( i < childsThisMenu.length -1 )
		childStr += "," ;
	}
	childStr += "]" ;

	this.updateLogs("Childs " + childStr + " from " +
			"[" + meta_id + "] archived by user: [" +
			user.getString("first_name").trim() + " " + user.getString("last_name").trim() + "]") ;

	//close connection
	dbc.closeConnection() ;
	dbc = null ;
    }

    /**
     * <p>Check if browser doc.                                                                     *
     */
    public int isBrowserDoc(int meta_id,imcode.server.User user) {
	String sqlStr = "" ;
	int to_meta_id ;

	to_meta_id = meta_id ;


	DBConnect dbc = new DBConnect(m_conPool) ;
	dbc.getConnection() ;

	sqlStr = "select doc_type from meta where meta_id = " + meta_id ;
	dbc.setSQLString(sqlStr) ;
	dbc.createStatement() ;
	Vector vec_doc_type = (Vector)dbc.executeQuery() ;
	dbc.clearResultSet() ;



	if ( Integer.parseInt(vec_doc_type.elementAt(0).toString()) == 6 ) {
	    sqlStr  = "select to_meta_id from browser_docs where meta_id = " + meta_id ;
	    sqlStr += " and browser = '" + user.getBrowserStr() + "'";
	    dbc.setSQLString(sqlStr) ;
	    dbc.createStatement() ;
	    Vector vec_to_meta_id = (Vector)dbc.executeQuery() ;
	    dbc.clearResultSet() ;
	    to_meta_id = Integer.parseInt(vec_to_meta_id.elementAt(0).toString()) ;


	    if (to_meta_id == -1) {
		sqlStr  = "select to_meta_id from browser_docs where meta_id = " + meta_id ;
		sqlStr += " and browser = 'other_" + user.getBrowserInfo()[2] + "'" ;
		dbc.setSQLString(sqlStr) ;
		dbc.createStatement() ;
		vec_to_meta_id = (Vector)dbc.executeQuery() ;
		dbc.clearResultSet() ;
		to_meta_id = Integer.parseInt(vec_to_meta_id.elementAt(0).toString()) ;
	    }
	}


	//close connection
	dbc.closeConnection() ;
	dbc = null ;

	return to_meta_id ;
    }


    /**
     * <p>Save an url document.
     */
    public void saveUrlDoc(int meta_id,User user,imcode.server.Table doc) {
	String sqlStr = "" ;

	// create a db connection an get meta data
	DBConnect dbc = new DBConnect(m_conPool) ;
	dbc.getConnection() ;


	// update url doc
	sqlStr  = "update url_docs\n" ;
	sqlStr += "set url_ref ='" + doc.getString("url_ref") + "'" ;
	sqlStr += ",url_txt ='" + doc.getString("url_txt") + "'" ;
	sqlStr += " where meta_id = " + meta_id ;

	dbc.setSQLString(sqlStr) ;
	dbc.createStatement() ;
	dbc.executeUpdateQuery() ;

	// close connection
	dbc.closeConnection() ;
	dbc = null ;

	this.updateLogs("UrlDoc [" + meta_id +	"] modified by user: [" +
			user.getString("first_name").trim() + " " + user.getString("last_name").trim() + "]") ;

    }


    /**
     * <p>Save a new url document.
     */
    public void saveNewUrlDoc(int meta_id,User user,imcode.server.Table doc) {
	String sqlStr = "" ;

	// create a db connection an get meta data
	DBConnect dbc = new DBConnect(m_conPool) ;
	dbc.getConnection() ;


	// create new url doc
	sqlStr  = "insert into url_docs(meta_id,frame_name,target,url_ref,url_txt,lang_prefix)\n" ;
	sqlStr += "values(" + meta_id + ",'" + doc.getString("frame_name") + "','" ;
	sqlStr += doc.getString("destination") + "','" ;
	sqlStr += doc.getString("url_ref") + "','" ;
	sqlStr += doc.getString("url_txt") ;
	sqlStr += "','se')" ;


	dbc.setSQLString(sqlStr) ;
	dbc.createStatement() ;
	dbc.executeUpdateQuery() ;

	// close connection
	dbc.closeConnection() ;
	dbc = null ;

	this.activateChild(meta_id,user) ;

	this.updateLogs("UrlDoc [" + meta_id +	"] created by user: [" +
			user.getString("first_name").trim() + " " + user.getString("last_name").trim() + "]") ;

    }




    /**
     * <p>List all archived docs.
     */
    public String listArchive(int meta_id,User user) {
	String sqlStr = "" ;
	String htmlStr = "" ;
	Vector child_meta_headlines = new Vector() ;
	Vector childs = new Vector() ;

	// create a db connection an get meta data
	DBConnect dbc = new DBConnect(m_conPool) ;
	dbc.getConnection() ;

	// get child meta_headline
	sqlStr  = "select meta.meta_headline from meta,childs " ;
	sqlStr += "where meta.meta_id = childs.to_meta_id and childs.meta_id = " ;
	sqlStr +=  meta_id ;
	sqlStr += " and meta.archive=1" ;

	dbc.setSQLString(sqlStr);
	dbc.createStatement() ;
	child_meta_headlines = (Vector)dbc.executeQuery() ;
	dbc.clearResultSet() ;


	// get childs
	sqlStr  = "select to_meta_id from meta,childs" ;
	sqlStr +=	" where meta.meta_id = childs.to_meta_id" ;
	sqlStr += " and childs.meta_id =" + meta_id ;
	sqlStr += " and meta.archive=1" ;

	dbc.setSQLString(sqlStr) ;
	dbc.createStatement() ;
	childs = (Vector)dbc.executeQuery() ;
	dbc.clearResultSet() ;

	// close connection
	dbc.closeConnection() ;
	dbc = null ;

	htmlStr += "<HTML><HEAD><TITLE>Janusarkivet</TITLE></HEAD><BODY>\n" ;
	htmlStr += "<LINK href=\"../css/CSS-MALL/janus.css\" rel=stylesheet type=text/css>\n" ;
	htmlStr += "<SPAN class=rubrik1>\n" ;
	htmlStr += "<CENTER><BR>" ;
	htmlStr += "<IMG SRC=\"" + m_ImageFolder + "arkivet.gif\" width=\"500\" height=\"27\">\n" ;
	htmlStr += "<BR><BR><TABLE border=0 width=* cellpadding=0 cellspacing=8>" ;
	htmlStr += "<TR><TD valign=\"top\" width=\"*\">" ;

	for ( int i = 0 ; i < childs.size() ; i++ ) {
	    htmlStr += "<input type=checkbox name=\"archiveBox\" value=" ;
	    htmlStr += "\"" + childs.elementAt(i).toString() + "\">";
	    htmlStr += "<IMG SRC=\"" + m_ImageFolder + "pil2.gif\" width=\"7\" height=\"10\">" ;
	    htmlStr += child_meta_headlines.elementAt(i).toString() + "<BR>\n";
	}
	htmlStr += "</TD></TR>\n" ;
	htmlStr += "</TABLE></CENTER></SPAN>\n" ;
	htmlStr += "</BODY></HTML>" ;

	return htmlStr ;
    }


    /**
     * <p>Check if url doc.
     */
    public imcode.server.Table isUrlDoc(int meta_id,User user) {
	String sqlStr = "" ;
	int to_meta_id ;
	imcode.server.Table url_doc ;

	to_meta_id = meta_id ;


	DBConnect dbc = new DBConnect(m_conPool) ;
	dbc.getConnection() ;

	sqlStr = "select doc_type from meta where meta_id = " + meta_id ;
	dbc.setSQLString(sqlStr) ;
	dbc.createStatement() ;
	Vector vec_doc_type = (Vector)dbc.executeQuery() ;
	dbc.clearResultSet() ;


	if ( Integer.parseInt(vec_doc_type.elementAt(0).toString()) == 5 ) {
	    sqlStr  = "select * from url_docs where meta_id = " + meta_id ;
	    dbc.setSQLString(sqlStr) ;
	    dbc.createStatement() ;
	    url_doc = new Table(dbc.executeQuery()) ;
	    url_doc.addFieldNames(dbc.getMetaData()) ;
	    dbc.clearResultSet() ;
	} else
	    url_doc = null ;

	//close connection
	dbc.closeConnection() ;
	dbc = null ;

	return url_doc ;

    }

    /**
     * <p>Save a new frameset.
     */
    public void saveNewFrameset(int meta_id,User user,imcode.server.Table doc) {
	String sqlStr = "" ;

	// create a db connection an get meta data
	DBConnect dbc = new DBConnect(m_conPool) ;
	dbc.getConnection() ;


	// create new url doc
	sqlStr  = "insert into frameset_docs(meta_id,frame_set)\n" ;
	sqlStr += "values(" + meta_id + ",'" + doc.getString("frame_set") + "')" ;


	dbc.setSQLString(sqlStr) ;
	dbc.createStatement() ;
	dbc.executeUpdateQuery() ;

	// close connection
	dbc.closeConnection() ;
	dbc = null ;

	this.activateChild(meta_id,user) ;

	this.updateLogs("FramesetDoc [" + meta_id +	"] created by user: [" +
			user.getString("first_name").trim() + " " + user.getString("last_name").trim() + "]") ;

    }

    /**
     * <p>Save a frameset
     */
    public void saveFrameset(int meta_id,User user,imcode.server.Table doc) {
	String sqlStr = "" ;

	// create a db connection an get meta data
	DBConnect dbc = new DBConnect(m_conPool) ;
	dbc.getConnection() ;


	// create new url doc
	sqlStr  = "update frameset_docs\n";
	sqlStr += "set frame_set ='" + doc.getString("frame_set") + "'\n";
	sqlStr += "where meta_id  = " + meta_id  ;


	dbc.setSQLString(sqlStr) ;
	dbc.createStatement() ;
	dbc.executeUpdateQuery() ;

	// close connection
	dbc.closeConnection() ;

	this.updateLogs("FramesetDoc [" + meta_id +	"] updated by user: [" +
			user.getString("first_name").trim() + " " + user.getString("last_name").trim() + "]") ;

    }


    /**
     * <p>Update logs.
     */
    public void updateLogs(String event) {
	String sqlStr = "" ;

	java.util.Calendar cal = java.util.Calendar.getInstance() ;

	String year  = Integer.toString(cal.get(Calendar.YEAR)) ;
	int month = Integer.parseInt(Integer.toString(cal.get(Calendar.MONTH))) + 1;
	int day   = Integer.parseInt(Integer.toString(cal.get(Calendar.DAY_OF_MONTH))) ;
	int hour  = Integer.parseInt(Integer.toString(cal.get(Calendar.HOUR))) ;
	int min   = Integer.parseInt(Integer.toString(cal.get(Calendar.MINUTE))) ;
	int sec   = Integer.parseInt(Integer.toString(cal.get(Calendar.SECOND))) ;

	String dateToDay  = year + "-" ;
	dateToDay += month < 10 ? "0" + Integer.toString(month) : Integer.toString(month) ;
	dateToDay += "-" ;
	dateToDay += day < 10 ? "0" + Integer.toString(day) : Integer.toString(day)  + " " ;
	dateToDay += " " ;
	dateToDay += hour < 10 ? "0" + Integer.toString(hour) : Integer.toString(hour) ;
	dateToDay += ":" ;
	dateToDay += min < 10 ? "0" + Integer.toString(min) : Integer.toString(min) ;
	dateToDay += ":" ;
	dateToDay += sec < 10 ? "0" + Integer.toString(min) : Integer.toString(sec) ;
	dateToDay += ".000" ;

	DBConnect dbc = new DBConnect(m_conPool) ;
	dbc.getConnection() ;

	sqlStr  = "insert into main_log(log_datetime,event)\n" ;
	sqlStr += "values('" +  dateToDay + "','" + event + "')\n" ;

	dbc.setSQLString(sqlStr) ;
	dbc.createStatement() ;
	dbc.executeUpdateQuery() ;

	//close connection
	dbc.closeConnection() ;
	dbc = null ;

    }


    /**
     * <p>Update track log.
     */
    public void updateTrackLog(int from_meta_id,int to_meta_id,imcode.server.User user) {
	String sqlStr = "" ;

	int cookie_id = user.getInt("user_id") ;

	java.util.Calendar cal = java.util.Calendar.getInstance() ;

	String year  = Integer.toString(cal.get(Calendar.YEAR)) ;
	int month = Integer.parseInt(Integer.toString(cal.get(Calendar.MONTH))) + 1;
	int day   = Integer.parseInt(Integer.toString(cal.get(Calendar.DAY_OF_MONTH))) ;
	int hour  = Integer.parseInt(Integer.toString(cal.get(Calendar.HOUR))) ;
	int min   = Integer.parseInt(Integer.toString(cal.get(Calendar.MINUTE))) ;
	int sec   = Integer.parseInt(Integer.toString(cal.get(Calendar.SECOND))) ;

	String dateToDay  = year + "-" ;
	dateToDay += month < 10 ? "0" + Integer.toString(month) : Integer.toString(month) ;
	dateToDay += "-" ;
	dateToDay += day < 10 ? "0" + Integer.toString(day) : Integer.toString(day)  + " " ;
	dateToDay += " " ;
	dateToDay += hour < 10 ? "0" + Integer.toString(hour) : Integer.toString(hour) ;
	dateToDay += ":" ;
	dateToDay += min < 10 ? "0" + Integer.toString(min) : Integer.toString(min) ;
	dateToDay += ":" ;
	dateToDay += sec < 10 ? "0" + Integer.toString(min) : Integer.toString(sec) ;
	dateToDay += ".000" ;

	DBConnect dbc = new DBConnect(m_conPool) ;
	dbc.getConnection() ;

	sqlStr  = "insert into track_log(user_id,log_datetime,from_meta_id,to_meta_id,cookie_id)\n" ;
	sqlStr += "values(" + user.getInt("user_id") + ",'"
	    +  dateToDay + "'," + from_meta_id +  "," + to_meta_id + "," + cookie_id +")\n" ;

	dbc.setSQLString(sqlStr) ;
	dbc.createStatement() ;
	dbc.executeUpdateQuery() ;

	//close connection
	dbc.closeConnection() ;
	dbc = null ;

    }




    /**
     * <p>Check if frameset doc.                                                                        *
     */
    public String isFramesetDoc(int meta_id,User user) {
	String sqlStr = "" ;
	Vector frame_set = new Vector() ;
	String html_str = "" ;

	DBConnect dbc = new DBConnect(m_conPool) ;
	dbc.getConnection() ;

	sqlStr = "select doc_type from meta where meta_id = " + meta_id ;
	dbc.setSQLString(sqlStr) ;
	dbc.createStatement() ;
	Vector vec_doc_type = (Vector)dbc.executeQuery() ;
	dbc.clearResultSet() ;


	if ( Integer.parseInt(vec_doc_type.elementAt(0).toString()) == 7 ) {
	    sqlStr  = "select frame_set from frameset_docs where meta_id = " + meta_id ;
	    dbc.setSQLString(sqlStr) ;
	    dbc.createStatement() ;
	    frame_set = (Vector)dbc.executeQuery() ;
	    dbc.clearResultSet() ;
	    html_str = frame_set.elementAt(0).toString() ;
	} else
	    html_str = null ;

	//close connection
	dbc.closeConnection() ;
	dbc = null ;

	return html_str ;

    }

    /**
     * <p>Search docs.
     */
    public Vector searchDocs(int meta_id,User user,String question_str,
			     String search_type,String string_match,String search_area) {

	// search_area : all,not_archived,archived

	String sqlStr = "" ;
	Vector tokens = new Vector() ;
	Vector meta_docs = new Vector() ;

	String match = "%" ;

	if ( string_match.equals("match") )
	    match = "" ;


	StringTokenizer parser = new StringTokenizer(question_str.trim()," ") ;
	while ( parser.hasMoreTokens() )
	    tokens.addElement(parser.nextToken()) ;


	DBConnect dbc = new DBConnect(m_conPool) ;
	dbc.getConnection() ;

	if ( !search_type.equals("atc_icd10") ) {

	    // text fields                 // texts.meta_id
	    if (tokens.size() > 0)
		sqlStr  += "select distinct meta.meta_id,meta.meta_headline,meta.meta_text from texts,meta where (" ;
	    for ( int i = 0 ; i < tokens.size() ; i++ ) {
		sqlStr += " text like  '%" + tokens.elementAt(i).toString() + match + "'"  ;
		if ( i < tokens.size() -1 )
		    sqlStr += " " + search_type + " " ;
	    }

	    sqlStr += ") " ;

	    if (tokens.size() > 0) {
		sqlStr += " and meta.meta_id = texts.meta_id" ;
		sqlStr += " and meta.activate = 1 and meta.disable_search = 0\n" ;
	    }

	    if (search_area.equals("not_archived")) {
		sqlStr += " and meta.archive = 0" ;
	    }

	    if (search_area.equals("archived")) {
		sqlStr += " and meta.archive = 1" ;
	    }



	    if ( tokens.size() > 0 ) {
		sqlStr += "\n union \n" ;
	    }


	    // meta_headline
	    if (tokens.size() > 0)
		sqlStr  += "select distinct meta_id,meta_headline,meta_text from meta where " ;
	    for ( int i = 0 ; i < tokens.size() ; i++ ) {
		sqlStr += " (meta_headline like  '%" + tokens.elementAt(i).toString() + match + "' " ;
		sqlStr += " or meta_text like  '%" + tokens.elementAt(i).toString() + match + "' " ;
		sqlStr += " or classification like '%" + tokens.elementAt(i).toString() + match + "') " ;

		if ( i < tokens.size() -1 )
		    sqlStr += " " + search_type + " " ;
	    }


	    sqlStr += " and activate = 1 and disable_search = 0\n" ;

	    if (search_area.equals("not_archived")) {
		sqlStr += " and meta.archive = 0" ;
	    }

	    if (search_area.equals("archived")) {
		sqlStr += " and meta.archive = 1" ;
	    }



	    if ( tokens.size() > 0 ) {
		sqlStr += " order by meta.meta_id" ;
		dbc.setSQLString(sqlStr) ;
		dbc.createStatement() ;
		meta_docs = (Vector)dbc.executeQuery() ;

		dbc.clearResultSet() ;
	    }


	} else {
	    sqlStr  = "select distinct meta_id,meta_headline,meta_text from meta where " ;
	    sqlStr += "classification = '" + question_str + "'";
	    sqlStr += " and activate = 1 and disable_search = 0\n" ;

	    if (search_area.equals("not_archived")) {
		sqlStr += " and meta.archive = 0" ;
	    }

	    if (search_area.equals("archived")) {
		sqlStr += " and meta.archive = 1" ;
	    }

	    dbc.setSQLString(sqlStr) ;
	    dbc.createStatement() ;
	    meta_docs = (Vector)dbc.executeQuery() ;
	    dbc.clearResultSet() ;
	}


	//close connection
	dbc.closeConnection() ;
	dbc = null ;

	return meta_docs ;

    }


    /**
     * <p>Check if external doc.
     */
    public ExternalDocType isExternalDoc(int meta_id,User user) {
	String sqlStr = "" ;
	ExternalDocType external_doc = null ;



	DBConnect dbc = new DBConnect(m_conPool) ;
	dbc.getConnection() ;

	sqlStr = "select doc_type from meta where meta_id = " + meta_id ;
	dbc.setSQLString(sqlStr) ;
	dbc.createStatement() ;
	Vector vec_doc_type = (Vector)dbc.executeQuery() ;
	dbc.clearResultSet() ;

	int doc_type = Integer.parseInt(vec_doc_type.elementAt(0).toString()) ;
	if ( doc_type > 100 ) {
	    for ( int i = 0 ; i < m_ExDoc.length && m_ExDoc[i] != null ; i++ )
		if ( m_ExDoc[i].getDocType() == doc_type ) {
		    //		external_doc = new ExternalDocType(m_ExDoc[i].getDocType(),m_ExDoc[i].getCallServlet(),
		    //	m_ExDoc[i].getDocName(),m_ExDoc[i].getParamStr()) ;
		    external_doc = m_ExDoc[i] ;
		}
	}
	//close connection
	dbc.closeConnection() ;
	dbc = null ;

	return external_doc ;

    }


    /**
     * <p>Remove child from child-table.
     */
    public void removeChild(int meta_id,int parent_meta_id,User user) {
	String sqlStr = "" ;


	DBConnect dbc = new DBConnect(m_conPool) ;
	dbc.getConnection() ;

	sqlStr  = "delete from childs where meta_id = " + parent_meta_id ;
	sqlStr += "and to_meta_id = " + meta_id ;
	dbc.setSQLString(sqlStr) ;
	dbc.createStatement() ;
	dbc.executeUpdateQuery() ;

	this.updateLogs("Child [" + meta_id +	"] removed from " + parent_meta_id +
			"by user: [" + user.getString("first_name").trim() + " " + user.getString("last_name").trim() + "]") ;


	//close connection
	dbc.closeConnection() ;
	dbc = null ;


    }



    /**
     * <p>Activate child to child-table.
     */
    public void activateChild(int meta_id,imcode.server.User user) {

	String sqlStr = "" ;

	DBConnect dbc = new DBConnect(m_conPool) ;
	dbc.getConnection() ;

	sqlStr  = "update meta\n" ;
	sqlStr += "set activate=1\n" ;
	sqlStr += "where meta_id = " + meta_id ;
	dbc.setSQLString(sqlStr) ;
	dbc.createStatement() ;
	dbc.executeUpdateQuery() ;

	this.updateLogs("Child [" + meta_id +	"] activated  " +
			"by user: [" + user.getString("first_name").trim() + " " + user.getString("last_name").trim() + "]") ;


	//close connection
	dbc.closeConnection() ;
	dbc = null ;


    }

    /**
       Deactivate (sigh) child from child-table.
    **/
    public void inActiveChild(int meta_id,imcode.server.User user) {

	String sqlStr = "" ;

	DBConnect dbc = new DBConnect(m_conPool) ;
	dbc.getConnection() ;

	sqlStr  = "update meta\n" ;
	sqlStr += "set activate=0\n" ;
	sqlStr += "where meta_id = " + meta_id ;
	dbc.setSQLString(sqlStr) ;
	dbc.createStatement() ;
	dbc.executeUpdateQuery() ;

	this.updateLogs("Child [" + meta_id +	"] made inactive  " +
			"by user: [" + user.getString("first_name").trim() + " " + user.getString("last_name").trim() + "]") ;


	//close connection
	dbc.closeConnection() ;
	dbc = null ;


    }

    /**
     * <p>Send a sqlquery to the database and return a string array.
     */
    public String[] sqlQuery(String sqlQuery) {

	Vector data = new Vector() ;

	DBConnect dbc = new DBConnect(m_conPool,sqlQuery) ;
	dbc.getConnection() ;
	dbc.createStatement() ;
	data = (Vector)dbc.executeQuery() ;

	dbc.clearResultSet() ;
	dbc.closeConnection() ;
	dbc = null ;

	if ( data != null ) {
	    String result[] = new String[data.size()] ;
	    for ( int i = 0 ; i < data.size() ; i++ )
		result[i] = data.elementAt(i).toString() ;


	    data = null ;
	    return result ;
	} else
	    return null ;
    }


    /**
     * <p>Send a sqlquery to the database and return a string array.
     */
    public String[] sqlQuery(String sqlQuery,String catalog) {

	Vector data = new Vector() ;

	DBConnect dbc = new DBConnect(m_conPool,sqlQuery) ;
	dbc.getConnection() ;
	dbc.createStatement() ;
	data = (Vector)dbc.executeQuery(catalog) ;

	dbc.clearResultSet() ;
	dbc.closeConnection() ;
	dbc = null ;

	if ( data != null ) {
	    String result[] = new String[data.size()] ;
	    for ( int i = 0 ; i < data.size() ; i++ )
		result[i] = data.elementAt(i).toString() ;

	    data = null ;
	    return result ;
	} else
	    return null ;
    }


    /**
     * <p>Send a sqlquery to the database and return a string
     */
    public String sqlQueryStr(String sqlQuery) {
	Vector data = new Vector() ;

	DBConnect dbc = new DBConnect(m_conPool,sqlQuery) ;
	dbc.getConnection() ;
	dbc.createStatement() ;
	data = (Vector)dbc.executeQuery() ;

	dbc.clearResultSet() ;
	dbc.closeConnection() ;
	dbc = null ;

	if ( data.size() > 0 )
	    return data.elementAt(0).toString() ;
	else
	    return null ;
    }

    /**
     * <p>Send a sql update query to the database
     */
    public void sqlUpdateQuery(String sqlStr) {
	DBConnect dbc = new DBConnect(m_conPool,sqlStr) ;
	dbc.getConnection() ;
	dbc.createStatement() ;
	dbc.executeUpdateQuery();
	dbc.closeConnection() ;
	dbc = null ;
    }


    /**
     * <p>Send a procedure to the database and return a string array
     */
    public String[] sqlProcedure(String procedure) {

	Vector data = new Vector() ;

	DBConnect dbc = new DBConnect(m_conPool) ;
	dbc.getConnection() ;
	dbc.setProcedure(procedure) ;
	// dbc.createStatement() ;
	data = (Vector)dbc.executeProcedure() ;

	dbc.clearResultSet() ;
	dbc.closeConnection() ;
	dbc = null ;

	if ( data != null ) {
	    String result[] = new String[data.size()] ;
	    for ( int i = 0 ; i < data.size() ; i++ )
		result[i] = data.elementAt(i).toString() ;


	    data = null ;
	    return result ;
	} else
	    return null ;
    }


    /**
     * <p>Send a procedure to the database and return a string.
     */
    public String sqlProcedureStr(String procedure) {
	Vector data = new Vector() ;

	DBConnect dbc = new DBConnect(m_conPool) ;
	dbc.getConnection() ;
	dbc.setProcedure(procedure) ;
	//dbc.createStatement() ;
	data = (Vector)dbc.executeProcedure() ;

	dbc.clearResultSet() ;
	dbc.closeConnection() ;
	dbc = null ;

	if (data != null) {

	    if ( data.size() > 0)
		return data.elementAt(0).toString() ;
	    else
		return null ;

	} else
	    return null ;
    }


    /**
     * <p>Send a procedure to the database and return a int.
     */
    public int sqlProcedureInt(String procedure) {
	Vector data = new Vector() ;

	DBConnect dbc = new DBConnect(m_conPool) ;
	dbc.getConnection() ;
	dbc.setProcedure(procedure) ;
	//dbc.createStatement() ;
	data = (Vector)dbc.executeProcedure() ;

	dbc.clearResultSet() ;
	dbc.closeConnection() ;
	dbc = null ;
	if ( data != null )
	    return Integer.parseInt(data.elementAt(0).toString()) ;
	else
	    return -1 ;
    }




    /**
     * <p>Send a update procedure to the database
     */
    public void sqlUpdateProcedure(String procedure) {
	DBConnect dbc = new DBConnect(m_conPool) ;
	dbc.getConnection() ;
	dbc.setProcedure(procedure) ;
	dbc.executeUpdateProcedure();
	dbc.closeConnection() ;
	dbc = null ;
    }



    /**
       Parse doc replace variables with data
    */
    public String  parseDoc(String htmlStr,java.util.Vector variables) {
	try {
	    String[] foo = new String[variables.size()] ;
	    return imcode.util.Parser.parseDoc(htmlStr,(String[])variables.toArray(foo)) ;
	} catch ( RuntimeException ex ) {
	    log.log(Log.ERROR,"parseDoc(String,Vector): RuntimeException", ex );
	    throw ex ;
	}
    }


    /**
       Parse doc replace variables with data, uses two vectors
    */
    public String  parseDoc(String htmlStr,java.util.Vector variables,java.util.Vector data) {
	try {
	    String[] foo = new String[variables.size()] ;
	    String[] bar = new String[data.size()] ;
	    return imcode.util.Parser.parseDoc(htmlStr,(String[])variables.toArray(foo),(String[])data.toArray(bar)) ;
	} catch ( RuntimeException ex ) {
	    log.log(Log.ERROR,"parseDoc(String,Vector,Vector): RuntimeException", ex );
	    throw ex ;
	}
    }


    /**
       Parse doc replace variables with data , use template
    */
    public String parseDoc(java.util.Vector variables, String admin_template_name, String lang_prefix) {
	try {
	    String htmlStr = fileCache.getCachedFileString(new File(m_TemplateHome,lang_prefix+"/admin/"+admin_template_name)) ;
	    if (variables == null) {
		return htmlStr ;
	    }
	    String[] foo = new String[variables.size()] ;
	    return imcode.util.Parser.parseDoc(htmlStr,(String[])variables.toArray(foo)) ;
	} catch ( RuntimeException e ) {
	    log.log(Log.ERROR,"parseDoc(Vector, String, String): RuntimeException", e );
	    throw e ;
	} catch ( IOException e ) {
	    log.log(Log.ERROR,"parseDoc(Vector, String, String): IOException", e );
	    return "" ;
	}
    }
    
    


    /**
     * <p>Return the external doctypes templates folder.
     */
    public String getExternalTemplateFolder(int meta_id) {
	Vector data = new Vector() ;
	String folder = "" ;

	DBConnect dbc = new DBConnect(m_conPool) ;
	String sqlStr = "select doc_type,lang_prefix from meta where meta_id = " + meta_id ;
	dbc.setSQLString(sqlStr) ;
	dbc.getConnection() ;
	dbc.createStatement() ;
	data = (Vector)dbc.executeQuery() ;

	dbc.clearResultSet() ;
	dbc.closeConnection() ;
	dbc = null ;

	if ( Integer.parseInt(data.elementAt(0).toString()) > 100 )
	    folder = m_TemplateHome + data.elementAt(1).toString() +
		"/" + data.elementAt(0).toString() + "/" ;
	else
	    folder = m_TemplateHome + data.elementAt(1).toString() + "/" ;


	return folder ;
    }


    /**
     * <p>Return  templatehome.
     */
    public String getTemplateHome() {
	return m_TemplateHome ;
    }


    /**
     * <p>Return  imagehome.
     */
    public String getImageHome() {
	return m_ImageFolder ;
    }


    /**
     * <p>Return  language.
     */
    public String getLanguage() {
	return m_Language ;
    }





    /**
     * <p>Get internal template folder.
     */
    public String getInternalTemplateFolder(int meta_id) {
	Vector data = new Vector() ;
	String folder = "" ;

	if ( meta_id != -1 ) {
	    DBConnect dbc = new DBConnect(m_conPool) ;
	    String sqlStr = "select doc_type,lang_prefix from meta where meta_id = " + meta_id ;
	    dbc.setSQLString(sqlStr) ;
	    dbc.getConnection() ;
	    dbc.createStatement() ;
	    data = (Vector)dbc.executeQuery() ;

	    dbc.clearResultSet() ;
	    dbc.closeConnection() ;
	    dbc = null ;
	    folder = m_TemplateHome + data.elementAt(1).toString() + "/" ;

	} else
	    folder = m_TemplateHome ;

	return folder ;
    }


    /**
     * <p>Increment session counter.
     */
    public	int incCounter() {
	m_SessionCounter += 1 ;
	return m_SessionCounter ;
    }


    /**
     * <p>Get session counter.
     */
    public	int getCounter() {
	return m_SessionCounter ;
    }


    /**
     * <p>Set session counter.
     */
    public	int setCounter(int value) {
	m_SessionCounter = value ;
	return m_SessionCounter ;
    }


    /**
     * <p>Set session counter date.
     */
    public	boolean setCounterDate(String date) {
	m_SessionCounterDate = date ;
	this.sqlUpdateProcedure("SetSessionCounterDate '" + date + "'") ;
	return true ;
    }


    /**
     * <p>Get session counter date.
     */
    public	String getCounterDate() {
	return m_SessionCounterDate ;
    }

    /**
       Remove elements from a vector.
    */
    private Vector removeElement(Vector vec,int elements) {
	Vector tempVec = new Vector() ;
	for ( int i = 0 ; i  < vec.size() ; i+=(elements+1) )
	    tempVec.addElement(vec.elementAt(i+elements)) ;
	return tempVec ;
    }


    /**
       Send a sqlQuery to the database and return a string array
       Array[0]                 = number of field in the record
       Array[1]   - array[n]    = metadata
       Array[n+1] - array[size] = data
    */
    public String[] sqlQueryExt(String sqlQuery) {

	DBConnect dbc = new DBConnect(m_conPool,sqlQuery) ;
	dbc.getConnection() ;
	dbc.createStatement() ;
	Vector data = (Vector)dbc.executeQuery() ;

	String[] meta = (String[])dbc.getMetaData() ;

	if ( data.size() > 0 ) {
	    String result[] = new String[data.size() + dbc.getColumnCount() + 1] ;

	    // no of fields
	    result[0] = dbc.getColumnCount() + "" ;

	    // meta
	    int i = 0 ;
	    for ( i = 0 ; i < dbc.getColumnCount() ; i++ )
		result[i+1] = meta[i] ;

	    // data
	    for ( int j = 0 ; j < data.size() ; j++ )
		result[j+i+1] = data.elementAt(j).toString() ;

	    dbc.clearResultSet() ;
	    dbc.closeConnection() ;
	    dbc = null ;
	    data = null ;
	    meta = null ;
	    return result ;
	} else {
	    dbc.clearResultSet() ;
	    dbc.closeConnection() ;
	    dbc = null ;
	    data = null ;
	    meta = null ;
	    return null  ;
	}

    }

    /**
       Send a procedure to the database and return a string array
       Array[0]                 = number of field in the record
       Array[1]   - array[n]    = metadata
       Array[n+1] - array[size] = data
    */
    public String[] sqlProcedureExt(String procedure) {

	DBConnect dbc = new DBConnect(m_conPool) ;
	dbc.getConnection() ;
	dbc.setProcedure(procedure) ;


	Vector data = (Vector)dbc.executeProcedure() ;
	String[] meta = (String[])dbc.getMetaData() ;

	if ( data != null && data.size() > 0 ) {


	    String result[] = new String[data.size() + dbc.getColumnCount() + 1] ;

	    // no of fields
	    result[0] = dbc.getColumnCount() + "" ;

	    // meta
	    int i = 0 ;
	    for ( i = 0 ; i < dbc.getColumnCount() ; i++ )
		result[i+1] = meta[i] ;

	    // data
	    for ( int j = 0 ; j < data.size() ; j++ )
		result[j+i+1] = data.elementAt(j).toString() ;




	    dbc.clearResultSet() ;
	    dbc.closeConnection() ;
	    dbc = null ;
	    data = null ;
	    meta = null ;
	    return result ;
	} else {
	    dbc.clearResultSet() ;
	    dbc.closeConnection() ;
	    dbc = null ;
	    data = null ;
	    meta = null ;
	    return null  ;
	}

    }


    /**
       Send a sqlQuery to the database and return a Hastable
    */
    public Hashtable sqlQueryHash(String sqlQuery) {

	DBConnect dbc = new DBConnect(m_conPool,sqlQuery) ;
	dbc.getConnection() ;
	dbc.createStatement() ;

	Vector data = (Vector)dbc.executeQuery() ;
	String[] meta = (String[])dbc.getMetaData() ;

	int columns = dbc.getColumnCount() ;

	Hashtable result = new Hashtable(columns,0.5f) ;


	dbc.clearResultSet() ;
	dbc.closeConnection() ;



	if ( data.size() > 0 ) {

	    for ( int i = 0 ; i < columns ; i++ ) {
		String temp_str[] = new String[data.size() / columns] ;
		int counter = 0 ;

		for ( int j =  i ; j < data.size()  ; j+=columns )
		    temp_str[counter++] = data.elementAt(j).toString() ;;

		result.put(meta[i],temp_str) ;
	    }


	    return result ;
	} else {
	    return new Hashtable(1,0.5f)   ;
	}

    }





    /**
       Send a procedure to the database and return a Hashtable
    */
    public Hashtable sqlProcedureHash(String procedure) {

	DBConnect dbc = new DBConnect(m_conPool) ;
	dbc.getConnection() ;
	dbc.setProcedure(procedure) ;



	Vector data = (Vector)dbc.executeProcedure() ;
	String[] meta = (String[])dbc.getMetaData() ;
	int columns = dbc.getColumnCount() ;


	Hashtable result = new Hashtable(columns,0.5f) ;

	dbc.clearResultSet() ;
	dbc.closeConnection() ;


	if ( data.size() > 0 ) {

	    for ( int i = 0 ; i < columns ; i++ ) {
		String temp_str[] = new String[data.size() / columns] ;
		int counter = 0 ;


		for ( int j =  i ; j < data.size()  ; j+=columns )
		    temp_str[counter++] = data.elementAt(j).toString() ;



		result.put(meta[i],temp_str) ;
	    }


	    return result ;
	} else {
	    return new Hashtable(1,0.5f)   ;
	}


    }


    /**
       Send a procedure to the database and return a multi string array
    */
    public String[][] sqlProcedureMulti(String procedure) {


	Vector data = new Vector() ;

	DBConnect dbc = new DBConnect(m_conPool) ;
	dbc.getConnection() ;
	dbc.setProcedure(procedure) ;



	data = (Vector)dbc.executeProcedure() ;
	int columns = dbc.getColumnCount() ;

	if (columns == 0)
	    return new String[0][0] ;


	int rows = data.size() / columns ;
	dbc.clearResultSet() ;
	dbc.closeConnection() ;




	String result[][] = new String[rows][columns] ;
	for(int i = 0 ; i < rows ; i++) {
	    for(int j = 0 ; j < columns ; j++) {
		result[i][j] = data.elementAt(i * columns +  j).toString() ;
	    }

	}


	return result ;




    }



    /**
       Send a sqlquery to the database and return a multi string array
    */
    public String[][] sqlQueryMulti(String sqlQuery) {


	Vector data = new Vector() ;

	DBConnect dbc = new DBConnect(m_conPool,sqlQuery) ;
	dbc.getConnection() ;
	dbc.createStatement() ;


	data = (Vector)dbc.executeQuery() ;
	int columns = dbc.getColumnCount() ;

	if (columns == 0)
	    return new String[0][0] ;


	int rows = data.size() / columns ;
	dbc.clearResultSet() ;
	dbc.closeConnection() ;




	String result[][] = new String[rows][columns] ;
	for(int i = 0 ; i < rows ; i++) {
	    for(int j = 0 ; j < columns ; j++) {
		result[i][j] = data.elementAt(i * columns +  j).toString() ;
	    }

	}


	return result ;




    }




    /*
      /**
	 restart server
    */
    /*
      public void restartServer() {
      ImcServer.imc_server.restartServer();
      }
    */

    /**
       get doctype
    */
    public int getDocType(int meta_id) {
	DBConnect dbc = new DBConnect(m_conPool) ;
	dbc.getConnection() ;
	dbc.setProcedure("GetDocType " + meta_id) ;
	Vector data = (Vector)dbc.executeProcedure() ;
	dbc.clearResultSet() ;
	dbc.closeConnection() ;
	dbc = null ;

	if ( data != null ) {
	    if ( data.size() > 0)
		return Integer.parseInt(data.elementAt(0).toString()) ;
	    else
		return 0 ;
	}

	return -1 ;
    }


    /**
       checkDocAdminRights
    */
    public boolean checkDocAdminRights(int meta_id, User user) {
	try {
	    DBConnect dbc = new DBConnect(m_conPool) ;
	    dbc.getConnection() ;

	    String sqlStr = "GetUserPermissionSet (?,?)" ;
	    String[] sqlAry = {String.valueOf(meta_id),String.valueOf(user.getInt("user_id"))} ;
	    dbc.setProcedure(sqlStr,sqlAry) ;
	    Vector perms = (Vector)dbc.executeProcedure() ;
	    dbc.clearResultSet() ;
	    dbc.closeConnection() ;

	    if (perms.size() > 0 && Integer.parseInt((String)perms.elementAt(0)) < 3 ) {
		return true ;
	    } else {
		return false ;
	    }
	} catch (RuntimeException ex) {
	    log.log(Log.ERROR, "Exception in checkDocAdminRights(int,User)",ex) ;
	    throw ex ;
	}
    }


    /**
       checkDocRights
    */
    public boolean checkDocRights(int meta_id, User user) {
	try {
	    DBConnect dbc = new DBConnect(m_conPool) ;
	    dbc.getConnection() ;

	    String sqlStr = "GetUserPermissionSet (?,?)" ;
	    String[] sqlAry = {String.valueOf(meta_id),String.valueOf(user.getInt("user_id"))} ;
	    dbc.setProcedure(sqlStr,sqlAry) ;
	    Vector perms = (Vector)dbc.executeProcedure() ;
	    dbc.clearResultSet() ;
	    dbc.closeConnection() ;

	    if (perms.size() > 0 && Integer.parseInt((String)perms.elementAt(0)) < 4 ) {
		return true ;
	    } else {
		return false ;
	    }
	} catch (RuntimeException ex) {
	    log.log(Log.ERROR, "Exception in checkDocRights(int,User)",ex) ;
	    throw ex ;
	}
    }

    /**
       Checks to see if a user has any permission of a particular set of permissions for a document.
       @param meta_id	The document-id
       @param user		The user
       @param			A bitmap containing the permissions.
    */
    public boolean checkDocAdminRightsAny (int meta_id, User user, int permission) {
	try {
	    DBConnect dbc = new DBConnect(m_conPool) ;
	    dbc.getConnection() ;

	    String sqlStr = "GetUserPermissionSet (?,?)" ;
	    String[] sqlAry = {String.valueOf(meta_id),String.valueOf(user.getInt("user_id"))} ;
	    dbc.setProcedure(sqlStr,sqlAry) ;
	    Vector perms = (Vector)dbc.executeProcedure() ;
	    dbc.clearResultSet() ;
	    dbc.closeConnection() ;

	    int set_id = Integer.parseInt((String)perms.elementAt(0)) ;
	    int set = Integer.parseInt((String)perms.elementAt(1)) ;

	    if (perms.size() > 0
		&& set_id == 0		// User has full permission for this document
		|| (set_id < 3 && ((set & permission) > 0))	// User has at least one of the permissions given.
		) {
		return true ;
	    } else {
		return false ;
	    }
	} catch (RuntimeException ex) {
	    log.log(Log.ERROR, "Exception in checkDocAdminRightsAny(int,User,int)",ex) ;
	    throw ex ;
	}
    }

    /**
       Checks to see if a user has a particular set of permissions for a document.
       @param meta_id      The document-id
       @param user		    The user
       @param permissions	A bitmap containing the permissions.
    */
    public boolean checkDocAdminRights (int meta_id, User user, int permission) {
	try {
	    DBConnect dbc = new DBConnect(m_conPool) ;
	    dbc.getConnection() ;
	    String sqlStr = "GetUserPermissionSet (?,?)" ;
	    String[] sqlAry = {String.valueOf(meta_id),String.valueOf(user.getInt("user_id"))} ;
	    dbc.setProcedure(sqlStr,sqlAry) ;
	    Vector perms = (Vector)dbc.executeProcedure() ;
	    dbc.clearResultSet() ;
	    dbc.closeConnection() ;

	    if (perms.size() == 0) {
		return false ;
	    }

	    int set_id = Integer.parseInt((String)perms.elementAt(0)) ;
	    int set = Integer.parseInt((String)perms.elementAt(1)) ;

	    if (set_id == 0		// User has full permission for this document
		|| (set_id < 3 && ((set & permission) == permission))	// User has all the permissions given.
		) {
		return true ;
	    } else {
		return false ;
	    }
	} catch (RuntimeException ex) {
	    log.log(Log.ERROR, "Exception in checkDocAdminRights(int,User,int)",ex) ;
	    throw ex ;
	}
    }

	/**
       Gets the users most privileged permission_set for the document.
       @param meta_id      	The document-id
       @param user_id		The user_id
	   @return the most privileged permission_set a user has for the document.
       
    */
    public int getUserHighestPermissionSet (int meta_id, int user_id)
	{
		try{
			DBConnect dbc = new DBConnect(m_conPool) ;
			dbc.getConnection() ;
			String sqlStr = "GetUserPermissionSet (?,?)" ;
			String[] sqlAry = {String.valueOf(meta_id),String.valueOf(user_id)} ;
			dbc.setProcedure(sqlStr,sqlAry) ;
			Vector perms = (Vector)dbc.executeProcedure() ;
			dbc.clearResultSet() ;
			dbc.closeConnection() ;

			if (perms.size() == 0){
				return IMCConstants.DOC_PERM_SET_NONE ;//nothing was returned so give no rights at all.
			}

			int set_id = Integer.parseInt((String)perms.elementAt(0)) ;

			if (set_id == IMCConstants.DOC_PERM_SET_FULL){
				return	IMCConstants.DOC_PERM_SET_FULL;	// User has full permission for this document
			}else if (set_id == IMCConstants.DOC_PERM_SET_RESTRICTED_1){
				return	IMCConstants.DOC_PERM_SET_RESTRICTED_1;	// User has restricted 1 permission for this document
			}else if (set_id == IMCConstants.DOC_PERM_SET_RESTRICTED_2){
				return	IMCConstants.DOC_PERM_SET_RESTRICTED_2;	// User has restricted 2 permission for this document
			}else if (set_id == IMCConstants.DOC_PERM_SET_READ){
				return	IMCConstants.DOC_PERM_SET_READ;	// User has only read permission for this document
			}else{
				return DOC_PERM_SET_NONE; //the user has no permission at all for this document
			}
		} catch (RuntimeException ex){
			log.log(Log.ERROR, "Exception in getUserHighestPermissionSet(int,int)",ex) ;
			throw ex ;
		}
	}

    /**
       save template to disk
    */
    public  int saveTemplate(String name, String file_name, byte[] template, boolean overwrite,String lang_prefix) {
	BufferedOutputStream out ;
	String sqlStr = "" ;
	String file ;
	String new_template_id = "";

	try {
	    file = new String(template,"8859_1") ;
	} catch(UnsupportedEncodingException e) {
	    return -2 ;
	}

	int no_of_txt = 0 ;
	int no_of_img = 0 ;
	int no_of_url = 0 ;

	for ( int index=0 ; (index = file.indexOf("#txt",index))!=-1 ; no_of_txt++ )
	    index += 4 ;
	for ( int index=0 ; (index = file.indexOf("#img",index))!=-1 ; no_of_img++ )
	    index += 4 ;
	for ( int index=0 ; (index = file.indexOf("#url",index))!=-1 ; no_of_url++ )
	    index += 4 ;

	// create connectionobject
	DBConnect dbc = new DBConnect(m_conPool) ;
	dbc.getConnection() ;


	// check if template exists
	sqlStr  = "select template_id from templates\n" ;
	sqlStr += "where simple_name = '" + name + "'" ;
	dbc.setSQLString(sqlStr);
	dbc.createStatement() ;
	Vector  template_id = (Vector)dbc.executeQuery() ;
	dbc.clearResultSet() ;
	if (template_id.size() == 0) {

	    // get new template_id
	    sqlStr = "select max(template_id) + 1 from templates\n" ;
	    dbc.setSQLString(sqlStr);
	    dbc.createStatement() ;
	    new_template_id = ((Vector)dbc.executeQuery()).elementAt(0).toString() ;
	    dbc.clearResultSet() ;


	    sqlStr  = "insert into templates\n" ;
	    sqlStr += "values (" + new_template_id + ",'"+file_name+"','"+name +
		"','"+ lang_prefix + "'," + no_of_txt+","+no_of_img+","+no_of_url+")" ;
	    dbc.setSQLString(sqlStr);
	    dbc.createStatement() ;
	    dbc.executeUpdateQuery() ;
	} else { //update
	    if (!overwrite) {
		dbc.closeConnection() ;
		dbc = null ;
		return -1;
	    }
	    new_template_id = template_id.elementAt(0).toString() ;

	    sqlStr  = "update templates\n"
		+ "set template_name = '" + file_name + "',"
		+ "no_of_txt =" + no_of_txt + ","
		+ "no_of_img =" + no_of_img + ","
		+ "no_of_url =" + no_of_url
		+ "where template_id = " + new_template_id ;
	    dbc.setSQLString(sqlStr);
	    dbc.createStatement() ;
	    dbc.executeUpdateQuery() ;
	}

	dbc.closeConnection() ;
	dbc = null ;

	File f = new File(m_TemplateHome, "text/" + new_template_id + ".html") ;

	//		if (!f.exists())
	//			overwrite = true ;

	// save template data
	//		if (overwrite) {
	try {
	    FileOutputStream fw = new FileOutputStream(f) ;
	    fw.write(template) ;
	    fw.flush() ;
	    fw.close() ;

	} catch(IOException e) {
	    return -2 ;
	}
	//		} else {
	//			return -1;
	//
	//		}



	// save template demo
	/*  if ( demo_template != null) {
	    if (demo_template.length > 0) {
	    try {
	    FileOutputStream fw = new FileOutputStream(m_TemplateHome + lang_prefix + "/text/demo/" + file_name) ;
	    fw.write(demo_template) ;
	    fw.close() ;
	    } catch(IOException e) {
	    return -2 ;
	    }
	    }
	    }*/


	//  0 = OK
	// -1 = file exist
	// -2 = write error
	return 0 ;

    }




    /**
       get demo template
    */
    public Object[] getDemoTemplate(int  template_id) throws IOException {
	//String str = "" ;
	StringBuffer str = new StringBuffer() ;
	BufferedReader fr = null;
	String suffix = null;
	String[] suffixList =
	{"jpg","jpeg","gif","png","html","htm"};

	for(int i=0;i<=5;i++)
	    { // Looking for a template with one of six suffixes
		String path = m_TemplateHome + "/text/demo/" + template_id + "." + suffixList[i];
		File fileObj = new File(path);
		long date = 0;
		long fileDate = fileObj.lastModified();
		if (fileObj.exists() && fileDate>date)
		    {
			// if a template was not properly removed, the template
			// with the most recens modified-date is returned
			date = fileDate;

			try {
			    fr = new BufferedReader(new InputStreamReader(new FileInputStream(fileObj),"8859_1")) ;
			    suffix = suffixList[i];
			} catch(IOException e) {
			    return null ; //Could not read
			}
		    } // end IF
	    } // end FOR

	char[] buffer = new char[4096] ;
	try {
	    int read ;
	    while ( (read = fr.read(buffer,0,4096)) != -1 ) {
		str.append(buffer,0,read) ;
	    }
	} catch(IOException e) {
	    return null ;
	}
	catch(NullPointerException e) {
	    return null ;
	}



	return new Object[] {suffix , str.toString().getBytes("8859_1")} ; //return the buffer




    }



    /**
       get template
    */
    public byte[] getTemplate(int  template_id) throws IOException {
	String str = "" ;

	BufferedReader fr ;

	try {
	    fr = new BufferedReader( new FileReader(m_TemplateHome  + "/text/" + template_id + ".html")) ;
	} catch(FileNotFoundException e) {
	    log.log(Log.INFO, "Failed to find template number "+template_id) ;
	    return null ;
	}

	try {
	    int temp ;
	    while ((temp = fr.read())!=-1) {
		str+=(char)temp;
	    }
	} catch(IOException e) {
	    log.log(Log.INFO, "Failed to read template number "+template_id) ;
	    return null ;
	}

	return str.getBytes("8859_1") ;
    }


    /**
       delete template from db/disk
    */
    public  void deleteTemplate(int template_id) {
	String sqlStr = "" ;

	// create connectiobject
	DBConnect dbc = new DBConnect(m_conPool) ;
	dbc.getConnection() ;


	// delete from database
	sqlStr  = "delete from templates_cref\n" ;
	sqlStr += "where template_id = " + template_id + "\n" ;
	dbc.setSQLString(sqlStr);
	dbc.createStatement() ;
	dbc.executeUpdateQuery() ;



	// delete from database
	sqlStr  = "delete from templates\n" ;
	sqlStr += "where template_id = " + template_id + "\n" ;
	dbc.setSQLString(sqlStr);
	dbc.createStatement() ;
	dbc.executeUpdateQuery() ;



	dbc.closeConnection() ;
	dbc = null ;

	// test if template exists and delete it
	File f = new File(m_TemplateHome  + "/text/" + template_id + ".html") ;
	if (f.exists()) {
	    f.delete() ;
	}



    }

    /**
       save demo template
    */
    public int saveDemoTemplate(int template_id,byte [] data, String suffix) {

	// save template demo

	// See if there are templete_id:s with other file-formats and delete them
	// WARNING: Uggly Code
	String[] suffixes = {"jpg","jpeg","gif","png","htm","html"};
	for(int i=0;i<=5;i++) {
	    File file = new File(m_TemplateHome + "/text/demo/" + template_id + "." + suffixes[i]);
	    if(file.exists())
		file.delete();
	    // doesn't always delete the file, made sure the right template is
	    // shown using the file-date & time in getDemoTemplate
	}

	try {
	    FileOutputStream fw = new FileOutputStream(m_TemplateHome  + "/text/demo/" + template_id + "." + suffix) ;
	    fw.write(data) ;
	    fw.close() ;
	} catch(IOException e) {
	    return -2 ;
	}

	return 0 ;

    }

    /**
       save templategroup
    */
    public void saveTemplateGroup(String group_name,User user) {
	String sqlStr = "" ;


	// create connectiobject
	DBConnect dbc = new DBConnect(m_conPool) ;
	dbc.getConnection() ;


	// get lang prefix
	sqlStr  = "select lang_prefix from users,lang_prefixes\n" ;
	sqlStr += "where users.lang_id = lang_prefixes.lang_id\n" ;
	sqlStr += "and user_id =" + user.getInt("user_id") ;
	dbc.setSQLString(sqlStr);
	dbc.createStatement() ;
	String lang_prefix = ((Vector)dbc.executeQuery()).elementAt(0).toString() ;
	dbc.clearResultSet() ;


	// get new group_id
	sqlStr = "select max(group_id) + 1 from templategroups\n" ;
	dbc.setSQLString(sqlStr);
	dbc.createStatement() ;
	String new_group_id = ((Vector)dbc.executeQuery()).elementAt(0).toString() ;
	dbc.clearResultSet() ;



	// change name
	sqlStr  = "insert into templategroups\n" ;
	sqlStr += "values(" + new_group_id + ",'" + lang_prefix + "','" + group_name + "')" ;
	dbc.setSQLString(sqlStr);
	dbc.createStatement() ;
	dbc.executeUpdateQuery() ;

	dbc.closeConnection() ;
	dbc = null ;
    }

    /**
       delete templategroup
    */
    public void deleteTemplateGroup(int group_id) {
	String sqlStr = "" ;

	// create connectiobject
	DBConnect dbc = new DBConnect(m_conPool) ;
	dbc.getConnection() ;


	// change name
	sqlStr  = "delete from templategroups\n" ;
	sqlStr += "where group_id = " + group_id + "\n" ;
	dbc.setSQLString(sqlStr);
	dbc.createStatement() ;
	dbc.executeUpdateQuery() ;
	dbc.closeConnection() ;
	dbc = null ;

    }


    /**
       change templategroupname
    */
    public void changeTemplateGroupName(int group_id,String new_name) {
	String sqlStr = "" ;

	// create connectiobject
	DBConnect dbc = new DBConnect(m_conPool) ;
	dbc.getConnection() ;


	// change name
	sqlStr  = "update templategroups\n" ;
	sqlStr += "set group_name = '" + new_name + "'" ;
	sqlStr += "where group_id = " + group_id + "\n" ;
	dbc.setSQLString(sqlStr);
	dbc.createStatement() ;
	dbc.executeUpdateQuery() ;

	dbc.closeConnection() ;
	dbc = null ;
    }

    /**
       unassign template from templategroups
    */
    public void unAssignTemplate(int template_id,int group_id[]) {
	String sqlStr = "" ;

	// create connectiobject
	DBConnect dbc = new DBConnect(m_conPool) ;
	dbc.getConnection() ;


	// delete current refs
	for( int i = 0 ; i < group_id.length ; i++) {
	    sqlStr  = "delete from templates_cref\n" ;
	    sqlStr += "where template_id = " + template_id ;
	    sqlStr += "and group_id = " + group_id[i] ;
	    dbc.setSQLString(sqlStr);
	    dbc.createStatement() ;
	    dbc.executeUpdateQuery() ;
	}


	dbc.closeConnection() ;
	dbc = null ;

    }



    /** get server date
     */
    public java.util.Date getCurrentDate() {
	return  new java.util.Date() ;
    }


    final static FileFilter DEMOTEMPLATEFILTER = new FileFilter () {
	    public boolean accept (File file) {
		return file.length() > 0 ;
	    }
	} ;


    // get demotemplates
    public String[] getDemoTemplateList() {
	File demoDir = new File(m_TemplateHome  + "/text/demo/" ) ;

	File[] file_list = demoDir.listFiles(DEMOTEMPLATEFILTER) ;

	String[] name_list = new String[file_list.length] ;
		
	if (file_list != null) {
	    for(int i = 0 ; i < name_list.length ; i++) {
		String filename = file_list[i].getName() ;
		int dot = filename.indexOf(".") ;
		name_list[i] = dot > -1 ? filename.substring(0,dot) : filename ;
	    }
	} else {
	    return new String[0];

	}

	return name_list;

    }



    // delete demotemplate
    public int deleteDemoTemplate(int template_id) {

	File f = new File(m_TemplateHome  + "/text/demo/" + template_id + ".html") ;
	if (f.exists()) {
	    f.delete() ;
	    return 0;
	}

	return -2 ;
    }




    /**
     *	<p>Return  language. Returns the langprefix from the db. Takes a lang id
     as argument. Will return null if something goes wrong.
     Example: If the language id number for swedish is 1. then the call
     myObject.getLanguage("1") will return 'se'
     That is, provided that the prefix for swedish is 'se', which it isn't.
     Or rather, it shouldn't be.
    */
    public String getLanguage(String lang_id) {
	return sqlProcedureStr("GetLangPrefixFromId " + lang_id) ;
    }

    /** Fetch the systemdata from the db */
    protected SystemData getSystemDataFromDb() {

	/** Fetch everything from the DB */
	String serverMaster[] = this.sqlProcedure("ServerMasterGet") ;
	String webMaster[] = this.sqlProcedure("WebMasterGet") ;
	String systemMessage = this.sqlProcedureStr("SystemMessageGet") ;

	/** Create a new SystemData object */
	SystemData sd = new SystemData() ;

	/** Store everything in the object */

	sd.setSystemMessage(systemMessage) ;

	if (serverMaster.length > 0) {
	    sd.setServerMaster(serverMaster[0]) ;
	    if (serverMaster.length > 1) {
		sd.setServerMasterAddress(serverMaster[1]) ;
	    }
	}
	if (webMaster.length > 0) {
	    sd.setWebMaster(webMaster[0]) ;
	    if (webMaster.length > 1) {
		sd.setWebMasterAddress(webMaster[1]) ;
	    }
	}

	return sd ;
    }

    public SystemData getSystemData () {
	return sysData ;
    }


    public void setSystemData (SystemData sd) {
	sysData = sd ;
	String sqlStr = "WebMasterSet '"+sd.getWebMaster()+"','"+sd.getWebMasterAddress()+"'" ;
	sqlUpdateProcedure(sqlStr) ;

	sqlStr = "ServerMasterSet '"+sd.getServerMaster()+"','"+sd.getServerMasterAddress()+"'" ;
	sqlUpdateProcedure(sqlStr) ;

	sqlStr = "SystemMessageSet '"+sd.getSystemMessage()+"'" ;
	sqlUpdateProcedure(sqlStr) ;
    }

    /**
       Returns the information for each meta id passed as argument.
    */
    public Hashtable ExistingDocsGetMetaIdInfo(String[] meta_id) {

	// Lets build a comma separed string to send to the sproc
	StringBuffer sBuf = new StringBuffer() ;
	for( int i = 0; i< meta_id.length; i++ ) {
	    sBuf.append(meta_id[i])  ;
	    if(i != meta_id.length)
		sBuf.append(",") ;
	}

	DBConnect dbc = new DBConnect(m_conPool) ;
	dbc.getConnection() ;
	dbc.setProcedure("ExistingDocsGetSelectedMetaIds ", sBuf.toString() ) ;
	Vector data = (Vector)dbc.executeProcedure() ;
	String[] meta = (String[])dbc.getMetaData() ;
	int columns = dbc.getColumnCount() ;
	dbc.clearResultSet() ;
	dbc.closeConnection() ;
	dbc = null ;

	// Lets build the result into an hashtable
	Hashtable result = new Hashtable(columns,0.5f) ;
	if ( data.size() > 0 ) {
	    for ( int i = 0 ; i < columns ; i++ ) {
		String temp_str[] = new String[data.size() / columns] ;
		int counter = 0 ;
		for ( int j =  i ; j < data.size()  ; j+=columns )
		    temp_str[counter++] = data.elementAt(j).toString() ;
		result.put(meta[i],temp_str) ;
	    }
	    return result ;

	} else {
	    return new Hashtable(1,0.5f)   ;
	}
    }


    /**
     * Returns an array with with all the documenttypes stored in the database
     * the array consists of pairs of id:, value. Suitable for parsing into select boxes etc.
     */
    public String[] getDocumentTypesInList(String langPrefixStr) {
	return this.sqlProcedure("GetDocTypes '" + langPrefixStr + "'" ) ;
    }

    /**
     * Returns an hashtable with with all the documenttypes stored in the database
     * the hashtable consists of pairs of id:, value.
     */
    public Hashtable getDocumentTypesInHash(String langPrefixStr) {
	return this.sqlQueryHash("GetDocTypes '" + langPrefixStr +  "'") ;
    }

    public boolean checkUserDocSharePermission(User user, int meta_id) {
	return sqlProcedure("CheckUserDocSharePermission "+user.getUserId()+","+meta_id).length>0 ;
    }

    /**
       Return a file relative to the include-path.
    **/
    public String getInclude(String path) throws IOException {
	return fileCache.getCachedFileString(new File(m_IncludePath,path)) ;
    }

} // END CLASS IMCService
