package imcode.server ;

import org.apache.oro.util.* ;
import org.apache.oro.text.* ;
import org.apache.oro.text.regex.* ;
import org.apache.oro.text.perl.* ;
import java.sql.*;
import java.rmi.server.* ;
import java.sql.Date ;
import java.io.*;
import java.util.*;
import imcode.server.* ;
import java.text.Collator ;
import java.text.SimpleDateFormat ;

import imcode.util.log.* ;

/**
  * <p>Main services for the Imcode Net Server.
  */
public class IMCService extends UnicastRemoteObject implements IMCServiceInterface {
	//	ConnectionPool m_conPool ;             // our pool of connections
	imcode.server.InetPoolManager m_conPool ; // inet pool of connections
	String m_TemplateHome ;           // template home
	int m_DefaultHomePage ;        // default home page
	String m_ServletUrl  ; 			   // servlet url
	String m_ImageFolder ;            // image folder
	String m_ExternalDocTypes  = "" ;      // external docs
	String m_StartUrl          = "" ;      // start url
	String m_Language          = "" ;      // language
	String m_WebMaster         = "" ;      // webmaster
	String m_WebMasterEmail    = "" ;      // webmaster email
	String m_ServerMaster      = "" ;      // servmaster
	String m_ServerMasterEmail = "" ;      // servmaster email
	String m_serverName        = "" ;      // servername
	boolean m_PrintLogToWindow = false ;  // flag - if true -> print log to app. window

	ExternalDocType m_ExDoc[]  = new ExternalDocType[20] ;
	String m_SessionCounterDate = "" ;
	int m_SessionCounter = 0 ;
	int m_NoOfTemplates  ;
	Template m_Template[] ;

    final static CacheLRU fileCache = new CacheLRU() ;

    final static Perl5Util    perl5util = new Perl5Util() ;
    final static Perl5Compiler patComp = new Perl5Compiler() ;
    final static PatternCache patCache = new PatternCacheLRU(50, patComp) ;

    final static Pattern OBSOLETE_MENU_PATTERN = patCache.getPattern("[\\r\\n]\\s*menu\\s+no=(\\d+)\\s+rows=(\\d+)\\s+table_col=(\\d+)\\s*",Perl5Compiler.READ_ONLY_MASK) ;
    //                                                                newline  ws menu ws no=123456 ws rows=123456 ws table_col=123456 ws
    
    final static Pattern OBSOLETE_MENU_ROW_PATTERN = patCache.getPattern("[^\\n\\r]*(?:\\r\\n|\\n|\\r)",Perl5Compiler.READ_ONLY_MASK) ;
    //                                                                    nonewline* newline

    final static Pattern HASHTAG_PATTERN = patCache.getPattern("#[^#\"<> \\t\\r\\n]+#",Perl5Compiler.READ_ONLY_MASK) ;
    //                                                          # none of the above #

    final static Pattern HASHTAGNUMBER_PATTERN = patCache.getPattern("(\\d+)#$", Perl5Compiler.READ_ONLY_MASK) ;
    //                                                                123456#

    final static Pattern MENU_PATTERN = patCache.getPattern("<\\?imcms:menu(?:\\s+no=\"(\\d+)\")?\\?>(.*?)<\\?\\/imcms:menu\\?>", Perl5Compiler.SINGLELINE_MASK|Perl5Compiler.READ_ONLY_MASK) ;
    
    final static Pattern MENULOOP_PATTERN = patCache.getPattern("<\\?imcms:menuloop\\?>(.*?)<\\?\\/imcms:menuloop\\?>", Perl5Compiler.SINGLELINE_MASK|Perl5Compiler.READ_ONLY_MASK) ;

    final static Pattern MENUITEM_PATTERN = patCache.getPattern("<\\?imcms:menuitem\\?>(.*?)<\\?\\/imcms:menuitem\\?>", Perl5Compiler.SINGLELINE_MASK|Perl5Compiler.READ_ONLY_MASK) ;

    final static Pattern MENUITEMHIDE_PATTERN = patCache.getPattern("<\\?imcms:menuitemhide\\?>(.*?)<\\?\\/imcms:menuitemhide\\?>", Perl5Compiler.SINGLELINE_MASK|Perl5Compiler.READ_ONLY_MASK) ;

    final static Pattern MENUITEMHIDETAG_PATTERN = patCache.getPattern("<\\?\\/?imcms:menuitemhide\\?>", Perl5Compiler.READ_ONLY_MASK) ;

    final static Pattern TR_START_PATTERN = patCache.getPattern("^(\\<tr[^>]*?\\>)",Perl5Compiler.CASE_INSENSITIVE_MASK|Perl5Compiler.READ_ONLY_MASK) ;
    final static Pattern TR_STOP_PATTERN = patCache.getPattern("(\\<\\/tr\\>)\\s*$",Perl5Compiler.CASE_INSENSITIVE_MASK|Perl5Compiler.READ_ONLY_MASK) ;
    final static Pattern TD_START_PATTERN = patCache.getPattern("^(\\<td[^>]*?\\>)",Perl5Compiler.CASE_INSENSITIVE_MASK|Perl5Compiler.READ_ONLY_MASK) ;
    final static Pattern TD_STOP_PATTERN = patCache.getPattern("(\\<\\/td\\>)\\s*$",Perl5Compiler.CASE_INSENSITIVE_MASK|Perl5Compiler.READ_ONLY_MASK) ;

    final static Pattern MENU_NO_PATTERN = patCache.getPattern("#doc_menu_no#",Perl5Compiler.READ_ONLY_MASK) ;

    final static Substitution EMPHASIZE_SUBSTITUTION = new Perl5Substitution("<b><em><!--emphasized-->$1<!--/emphasized--></em></b>") ;

    final static Substitution NULL_SUBSTITUTION = new StringSubstitution("") ;

    SimpleDateFormat dateparser = new SimpleDateFormat("yyyy-MM-ddHH:mm") ;

    Log log = Log.getLog("server") ;


	/**
	* <p>Contructs an IMCService object.
	*/
	//	public IMCService(ConnectionPool conPool,javax.swing.JTextArea output,String serverName)
	public IMCService(imcode.server.InetPoolManager conPool,Properties props)
	throws java.rmi.RemoteException {
		super();
		m_conPool    = conPool ;

		m_TemplateHome      = props.getProperty("TemplatePath") ;
		m_DefaultHomePage   = Integer.parseInt(props.getProperty("StartDocument")) ;    //FIXME: Get from DB
		m_ServletUrl        = props.getProperty("ServletUrl") ; //FIXME: Get from webserver, or get rid of if possible.
		m_ImageFolder       = props.getProperty("ImageUrl") ; //FIXME: Get from webserver, or get rid of if possible.
		m_ExternalDocTypes  = props.getProperty("ExternalDoctypes") ; //FIXME: Get rid of, if possible.
		m_StartUrl          = props.getProperty("StartUrl") ; //FIXME: Get from webserver, or get rid of if possible.
		m_Language          = props.getProperty("DefaultLanguage") ; //FIXME: Get from DB
		m_WebMaster         = props.getProperty("WebmasterName") ; //FIXME: Get from DB
		m_WebMasterEmail    = props.getProperty("WebmasterAddress") ; //FIXME: Get from DB
		m_ServerMaster      = props.getProperty("ServermasterName") ; //FIXME: Get from DB
		m_ServerMasterEmail = props.getProperty("ServermasterAddress") ; //FIXME: Get from DB

		log.log(Log.INFO, "TemplatePath: " + m_TemplateHome) ;
		log.log(Log.INFO, "StartDocument: " + m_DefaultHomePage) ;
		log.log(Log.INFO, "ServletUrl: " + m_ServletUrl) ;
		log.log(Log.INFO, "ImageUrl: " + m_ImageFolder) ;
		log.log(Log.INFO, "ExternalDoctypes: " + m_ExternalDocTypes) ;
		log.log(Log.INFO, "StartUrl: " + m_StartUrl) ;
		log.log(Log.INFO, "DefaultLanguage: " + m_Language) ;
		log.log(Log.INFO, "WebmasterName: " + m_WebMaster) ;
		log.log(Log.INFO, "WebmasterAddress: " + m_WebMasterEmail) ;
		log.log(Log.INFO, "ServermasterName: " + m_ServerMaster) ;
		log.log(Log.INFO, "ServermasterAddress: " + m_ServerMasterEmail) ;



	StringTokenizer doc_types = new StringTokenizer(m_ExternalDocTypes,";",false) ;
		int doc_count = 0 ;
		String items[] = new String[5] ;

		try {
			while ( doc_types.hasMoreTokens() ) {
				StringTokenizer tempStr = new StringTokenizer(doc_types.nextToken(),":",false)  ;

				int i = 0 ;
				while ( tempStr.hasMoreTokens() )
					items[i++] = tempStr.nextToken() ;
				m_ExDoc[doc_count++] = new ExternalDocType(Integer.parseInt(items[0]),items[1],items[2],"") ;

			}
		}
		catch(NoSuchElementException e) {
			e.printStackTrace() ;
		}


		try {
		    m_SessionCounter     = Integer.parseInt(this.sqlProcedureStr("GetCurrentSessionCounter")) ;
		    m_SessionCounterDate = this.sqlProcedureStr("GetCurrentSessionCounterDate") ;
		    m_NoOfTemplates      = this.sqlProcedureInt("GetNoOfTemplates") ;
		} catch ( NumberFormatException ex ) {
		    log.log(Log.CRITICAL, "Failed to get SessionCounter from db.", ex) ;
		    throw ex ;
		}

		m_Template = new Template[m_NoOfTemplates] ;

		log.log(Log.INFO, "SessionCounter: "+m_SessionCounter) ;
		log.log(Log.INFO, "SessionCounterDate: "+m_SessionCounterDate) ;
		log.log(Log.INFO, "TemplateCount: "+m_NoOfTemplates) ;
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
		dbc.closeConnection() ;
		dbc = null ;
		return user ;
	}

    public byte[] parsePage (int meta_id, User user, int flags) throws IOException {

	try {
	long totaltime = System.currentTimeMillis() ;
	String meta_id_str = String.valueOf(meta_id) ;
	int user_id = user.getInt("user_id") ;
	String user_id_str = String.valueOf(user_id) ;
	//log.log(Log.WILD, "Starting parsing of page for meta_id "+meta_id_str+", user "+user_id_str+", flags "+flags, null) ;

	DBConnect dbc = new DBConnect(m_conPool) ;
	//log.log(Log.WILD, "Getting connection", null) ;
	dbc.getConnection() ;
	dbc.createStatement() ;

	String lang_prefix = null ;
	// Get the users language prefix
	dbc.setTrim(true) ;
	String sqlStr = "select lang_prefix from lang_prefixes where lang_id = "+user.getInt("lang_id") ;	// Find language
	dbc.setSQLString(sqlStr);
	Vector data = (Vector)dbc.executeQuery() ;
	if ( data.size() > 0 ) {
	    lang_prefix = data.elementAt(0).toString() ;
	} else {
	    dbc.closeConnection() ;
	    return ("No language!").getBytes("8859_1") ;
	}
	//dbc.createStatement() ;
	dbc.clearResultSet() ;
	dbc.setTrim(false) ;

	String[] sqlAry = {
	    meta_id_str,
	    user_id_str
	} ;

	//log.log(Log.WILD, "Getting permissions", null) ;
	dbc.setProcedure("GetUserPermissionSet (?,?)",sqlAry) ;
	Vector user_permission_set = (Vector)dbc.executeProcedure() ;
	dbc.clearResultSet() ;
	if ( user_permission_set == null ) {
	    dbc.closeConnection() ;			// Close connection to db.
	    return ("GetUserPermissionset returned null").getBytes("8859_1") ;
	}
	//log.log(Log.WILD, "Setting permissionstate", null) ;

	int user_set_id = Integer.parseInt((String)user_permission_set.elementAt(0)) ;
	int user_perm_set = Integer.parseInt((String)user_permission_set.elementAt(1)) ;
	int currentdoc_perms = Integer.parseInt((String)user_permission_set.elementAt(2)) ;

	final boolean textmode 		= (flags &  65536) != 0 && (user_set_id == 0 || (user_perm_set &  65536) != 0) ;
	final boolean imagemode 	= (flags & 131072) != 0 && (user_set_id == 0 || (user_perm_set & 131072) != 0) ;
	final boolean menumode 		= (flags & 262144) != 0 && (user_set_id == 0 || (user_perm_set & 262144) != 0) ;
	final boolean templatemode      = (flags & 524288) != 0 && (user_set_id == 0 || (user_perm_set & 524288) != 0) ;


	dbc.setProcedure("GetTextDocData",String.valueOf(meta_id)) ;
	Vector text_docs = (Vector)dbc.executeProcedure() ;
	dbc.clearResultSet() ;
	if ( text_docs == null ) {
	    dbc.closeConnection() ;			// Close connection to db.
	    return ("GetTextDocData returned null").getBytes("8859_1") ;
	}

	String template_id = (String)text_docs.remove(0) ;
	String simple_name = (String)text_docs.remove(0) ;
	int sort_order = Integer.parseInt((String)text_docs.remove(0)) ;
	String group_id = (String)text_docs.remove(0) ;

	log.log(Log.WILD, "Got templateinfo. TemplateId: "+template_id, null) ;

	Vector doc_types_vec = null ;
	if (menumode) {
	    // I'll retrieve a list of all doc-types the user may create.
	    sqlStr = "GetDocTypesForUser (?,?,?)" ;
	    String[] sqlAry2 = {
		String.valueOf(meta_id),
		String.valueOf(user.getInt("user_id")),
		lang_prefix
	    } ;
	    dbc.setProcedure(sqlStr,sqlAry2) ;
	    doc_types_vec = (Vector)dbc.executeProcedure() ;
	    dbc.clearResultSet() ;
	}

	Vector templategroups = null ;
	Vector templates = null ;
	Vector groupnamevec = null ;

	int selected_group = user.getTemplateGroup() ;
	if (templatemode) {
	    sqlStr = "GetTemplategroupsForUser (?,?)" ;
	    String[] sqlAry2 = {
		String.valueOf(meta_id),
		String.valueOf(user.getInt("user_id"))
	    } ;
	    dbc.setProcedure(sqlStr,sqlAry2) ;
	    templategroups = (Vector)dbc.executeProcedure() ;
	    dbc.clearResultSet() ;
	    // do templatemode queries


	    if ( selected_group == -1 ) {
		selected_group = Integer.parseInt(group_id) ;
	    }

	    sqlStr = "GetTemplatesInGroup";
	    dbc.setProcedure(sqlStr,String.valueOf(selected_group)) ;
	    templates = (Vector)dbc.executeProcedure() ;
	    dbc.clearResultSet() ;

	    sqlStr = "select group_name from templategroups where group_id = " + selected_group ;
	    dbc.setSQLString(sqlStr);
	    groupnamevec = (Vector)dbc.executeQuery() ;
	    dbc.clearResultSet() ;

	}

	String[] emp = (String[])user.get("emphasize") ;

	// Now we'll get the texts from the db.
	//log.log(Log.WILD, "Starting texts.", null) ;
	dbc.setProcedure("GetTexts",String.valueOf(meta_id)) ;
	Vector texts = (Vector)dbc.executeProcedure() ;
	dbc.clearResultSet() ;

	if ( texts == null ) {
	    dbc.closeConnection() ;			// Close connection to db.
	    return ("GetTexts returned null").getBytes("8859_1") ;
	}

	//log.log(Log.WILD, "Getting images.", null) ;

	// Get the images from the db
	// sqlStr = "select '#img'+convert(varchar(5), name)+'#',name,imgurl,linkurl,width,height,border,v_space,h_space,image_name,align,alt_text,low_scr,target,target_name from images where meta_id = " + meta_id ;
	//	   				0                                    1    2      3       4     5      6      7       8       9          10    11       12      13     14

	sqlStr = "select date_modified, meta_headline from meta where meta_id = " + meta_id ;
	dbc.setSQLString(sqlStr);
	Vector meta = (Vector)dbc.executeQuery() ;
	dbc.clearResultSet() ;

	if ( meta == null ) {
	    dbc.closeConnection() ;			// Close connection to db.
	    return ("Query for date_modified returned null").getBytes("8859_1") ;
	}

	sqlStr  = "select value from sys_data where sys_id = 3 and type_id = 3" ;
	dbc.setSQLString(sqlStr);
	Vector sys_data = (Vector)dbc.executeQuery() ;
	dbc.clearResultSet() ;

	if ( sys_data == null || sys_data.size() == 0) {
	    dbc.closeConnection() ;			// Close connection to db.
	    return ("Query for sys_message returned null or nothing").getBytes("8859_1") ;
	}

	//log.log(Log.WILD, "Got docinfo. Getting childs.", null) ;

	// Here we have the most timeconsuming part of parsing the page.
	// Selecting all the documents with permissions from the DB
	sqlStr = "getChilds (?,?)" ;
	//String[] sqlAry = {String.valueOf(meta_id),String.valueOf(user.getInt("user_id"))} ;
	dbc.setProcedure(sqlStr,sqlAry) ;
	Vector childs = (Vector)dbc.executeProcedure() ;

	if ( childs == null ) {
	    dbc.closeConnection() ;			// Close connection to db.
	    return ("GetChilds returned null").getBytes("8859_1") ;
	}

	int child_cols = dbc.getColumnCount() ;
	int child_rows = childs.size() / child_cols ;
	dbc.clearResultSet() ;

	dbc.setProcedure("GetImgs",String.valueOf(meta_id)) ;
	Vector images = (Vector)dbc.executeProcedure() ;
	if ( images == null ) {
	    dbc.closeConnection() ;			// Close connection to db.
	    return ("GetImgs returned null").getBytes("8859_1") ;
	}

	dbc.closeConnection() ;			// Close connection to db.

	Properties tags = new Properties() ;	// A properties object to hold the results from the db...

	//log.log(Log.WILD, "Processing texts.", null) ;
	long texttime = System.currentTimeMillis() ;
	if ( textmode ) {	// Textmode
	    Iterator it = texts.iterator() ;
	    while ( it.hasNext() ) {
		String key = (String)it.next() ;
		String txt_no = (String)it.next() ;
		String txt_type = (String)it.next() ;
		String value = (String)it.next() ;
		if ( value.length()>0 ) {
		    value = "<img src=\""+m_ImageFolder+"red.gif\" border=\"0\">&nbsp;"+value+"<a href=\""+m_ServletUrl+"ChangeText?meta_id="+meta_id+"&txt="+txt_no+"&type="+txt_type+"\"><img src=\""+m_ImageFolder+"txt.gif\" border=\"0\"></a>" ;
		    tags.setProperty(key,value) ;
		}
	    }
	} else {	// Not Textmode
	    Iterator it = texts.iterator() ;
	    while ( it.hasNext() ) {
		String key = (String)it.next() ;
		String txt_no = (String)it.next() ;
		String txt_type = (String)it.next() ;
		String value = (String)it.next() ;
		if ( value.length()>0 ) {
		    tags.setProperty(key,value) ;
		}
	    }
	}
	texttime = System.currentTimeMillis()-texttime ;

	//log.log(Log.WILD, "Processing images.", null) ;
	long imagetime = System.currentTimeMillis() ;
	int images_cols = dbc.getColumnCount() ;
	int images_rows = images.size() / images_cols ;
	dbc.clearResultSet() ;
	Iterator imit = images.iterator() ;
	while ( imit.hasNext() ) {
	    String imgtag = (String)imit.next() ;
	    String imgnumber = (String)imit.next() ;
	    String imgurl = (String)imit.next() ;
	    String linkurl = (String)imit.next() ;
	    String width = (String)imit.next() ;
	    String height = (String)imit.next() ;
	    String border = (String)imit.next() ;
	    String vspace = (String)imit.next() ;
	    String hspace = (String)imit.next() ;
	    String image_name = (String)imit.next() ;
	    String align = (String)imit.next() ;
	    String alt = (String)imit.next() ;
	    String lowscr = (String)imit.next() ;
	    String target = (String)imit.next() ;
	    String target_name = (String)imit.next() ;
	    StringBuffer value = new StringBuffer (64) ;
	    if ( !"".equals(imgurl) ) {
		if ( !"".equals(linkurl) ) {
		    value.append("<a href=\""+linkurl+"\"") ;
		    if ( target.equals("_other") ) {
			value.append(" target=\""+target_name+"\">") ;
		    } else if ( !"".equals(target) ) {
			value.append(" target=\""+target+"\">") ;
		    }
		}

		value.append("<img src=\""+imgurl+"\"") ;
		if ( !"0".equals(width) ) {
		    value.append(" width=\""+width+"\"") ;
		}
		if ( !"0".equals(height) ) {
		    value.append(" height=\""+height+"\"") ;
		}
		value.append(" border=\""+border+"\"") ;

		if ( !"0".equals(vspace) ) {
		    value.append(" vspace=\""+vspace+"\"") ;
		}
		if ( !"0".equals(hspace) ) {
		    value.append(" hspace=\""+hspace+"\"") ;
		}
		if ( !"".equals(image_name) ) {
		    value.append(" name=\""+image_name+"\"") ;
		}
		if ( !"".equals(alt) ) {
		    value.append(" alt=\""+alt+"\"") ;
		}
		if ( !"".equals(lowscr) ) {
		    value.append(" lowscr=\""+lowscr+"\"") ;
		}
		if ( !"".equals(align) && !"none".equals(align)) {
		    value.append(" align=\""+align+"\"") ;
		}
		if ( !"".equals(linkurl) || imagemode ) {
		    value.append("></a>") ;
		} else {
		    value.append(">") ;
		}
		if ( imagemode ) {	// Imagemode...
		    value.append("<a href=\"ChangeImage?meta_id="+meta_id+"&img="+imgnumber+"\"><img src=\""+m_ImageFolder+"txt.gif\" border=\"0\"></a>") ;
		}
		tags.setProperty(imgtag,value.toString()) ;
	    }
	}
	imagetime = System.currentTimeMillis()-imagetime ;

	/*
	  OK.. we will now make a LinkedList for the entire page.
	  This LinkedList, menus, will contain one item for each menu on the page.
	  These items will also be instances of LinkedList.
	  These LinkedLists will in turn each hold one Properties for each item in each menu.
	  These Properties will hold the tags, and the corresponding data, that will go in each menuitem.
	*/
	//time = System.currentTimeMillis() ;
	HashMap menus = new HashMap () ;	// Map to contain all all the menus on the page.
	LinkedList currentMenu = null ;
	int old_menu = -1 ;
	java.util.Date now = new java.util.Date() ;

	long menutime = System.currentTimeMillis() ;
	Iterator childIt = childs.iterator() ;
	while ( childIt.hasNext() ) {
	    // The menuitemproperties are retrieved in the following order:
	    // to_meta_id,
	    // c.menu_sort,
	    // manual_sort_order,
	    // doc_type,
	    // archive,
	    // target,
	    // date_created,
	    // date_modified,
	    // meta_headline,
	    // meta_text,
	    // meta_image,
	    // frame_name,
	    // activated_date+activated_time,
	    // archived_date+archived_time
	    // 0 if admin
	    // filename
	    String child_meta_id             = (String)childIt.next() ; // The meta-id of the child
	    String child_menu_sort           = (String)childIt.next() ; // What menu in the page the child is in.
	    String child_manual_sort_order   = (String)childIt.next() ; // What order the document is sorted in in the menu, using sort-order 2 (manual sort)
	    String child_doc_type            = (String)childIt.next() ; // The doctype of the child.
	    String child_archive             = (String)childIt.next() ; // Child is considered archived?
	    String child_target              = (String)childIt.next() ; // The target for this document.
	    String child_date_created        = (String)childIt.next() ; // The datetime the child was created.
	    String child_date_modified       = (String)childIt.next() ; // The datetime the child was modified.
	    String child_meta_headline       = (String)childIt.next() ; // The headline of the child.
	    String child_meta_text           = (String)childIt.next() ; // The subtext for the child.
	    String child_meta_image          = (String)childIt.next() ; // An optional imageurl for this document.
	    String child_frame_name          = (String)childIt.next() ; // An optional imageurl for this document.
	    String child_activated_date_time = (String)childIt.next() ; // The datetime the document is activated.
	    String child_archived_date_time  = (String)childIt.next() ; // The datetime the document is activated.
	    String child_admin               = (String)childIt.next() ; // "0" if the user may admin it.
	    String child_filename            = (String)childIt.next() ; // The filename, if it is a file-doc.

	    // System.out.println((String)childs.get(i*child_cols+0)+" "+(String)childs.get(i*child_cols+1)+" "+(String)childs.get(i*child_cols+7)) ;

	    int menuno = Integer.parseInt(child_menu_sort) ;
	    if ( menuno != old_menu ) {	//If we come upon a new menu...
		old_menu = menuno ;
		currentMenu = new LinkedList() ;	// We make a new Menu,
		menus.put(new Integer(menuno), currentMenu) ;			// and add it to the page.
	    }

	    java.util.Date archived_date = null ;
	    java.util.Date activate_date = null ;

	    try {
		archived_date = dateparser.parse(child_archived_date_time) ;
	    } catch ( java.text.ParseException ex ) {
	    }

	    try {
		activate_date = dateparser.parse(child_activated_date_time) ;
	    } catch ( java.text.ParseException ex ) {
	    }

	    boolean inactive = false ;
	    boolean archived = false ;

	    if ( activate_date != null && activate_date.compareTo(now) >= 0 ) {// If activated_date is greater than or equal to now
		if ( !menumode ) {														// and we're not in menumode...
		    continue ;																// ...don't include this menuitem
		} else {
		    inactive = true ;
		}
	    }

	    if ( (archived_date != null && archived_date.compareTo(now) <= 0)	// If archived_date is smaller than or equal to now
		 || "1".equals(child_archive) ) {	// or archive is set
		if ( !menumode ) {										// and we're not in menumode...
		    continue ;																// ...don't include this menuitem
		} else {
		    archived = true ;
		}
	    }

	    Properties props = new Properties () ;	// New Properties to hold the tags for this menuitem.

	    String admin_start = "" ;
	    String admin_stop = "" ;
	    if ( menumode ) {
		String sortBox = "<input type=\"text\" name=\""+child_meta_id+"\" value=\""+child_manual_sort_order+"\" size=\"4\" maxlength=\"4\">" ;
		String archiveDelBox = "<input type=\"checkbox\" name=\"archiveDelBox\" value=\""+child_meta_id+"\">" ;

		props.setProperty("#sortBox#",sortBox) ;
		props.setProperty("#archiveDelBox#",archiveDelBox) ;

		if ( "0".equals(child_admin) ) {
		    admin_stop+="&nbsp;<a href=\"AdminDoc?meta_id="+child_meta_id+"\"><img src=\""+m_ImageFolder+"txt.gif\" border=\"0\"></a>" ;
		}

		if (sort_order == 2) {
		    admin_start += sortBox ;
		}
		admin_start += archiveDelBox ;

	    }

	    String archive_start = "" ;
	    String archive_stop = "" ;

	    if ( inactive ) {
		archive_start+="<em><i>" ;
		archive_stop+="</i></em>" ;
	    }

	    if ( archived ) {
		archive_start="<strike>"+archive_start ;
		archive_stop+="</strike>" ;
	    }

	    //props.setProperty("#adminStart#",admin_start) ;
	    props.setProperty("#adminStop#",admin_stop) ;
	    //props.setProperty("#to_meta_id#",to_meta_id) ;
	    //props.setProperty("#manual_sort_order#",child_manual_sort_order) ;
	    if ( "_other".equals(child_target) ) {
		child_target = (String)child_frame_name ;
	    }
	    if ( child_target.length() != 0 ) {
		child_target = " target=\""+child_target+"\"" ;
	    }

	    // If this doc is a file, we'll want to put in the filename
	    // as an escaped translated path
	    // For example: /servlet/GetDoc/filename.ext?meta_id=1234
	    //                             ^^^^^^^^^^^^^

	    if ( child_filename != null && "8".equals(child_doc_type) ) {
		child_filename = "/"+java.net.URLEncoder.encode(child_filename) ;
	    } else {
		child_filename = "" ;
	    }

	    if ( child_meta_headline.length() == 0 ) {
		child_meta_headline = "&nbsp;" ;
	    }

	    child_meta_headline = archive_start+child_meta_headline+archive_stop ;
	    if ( !"".equals(child_meta_image) ) {
		child_meta_image = "<img src=\""+child_meta_image+"\" border=\"0\">" ;
	    }

	    String href = "\"GetDoc"+child_filename+"?meta_id="+child_meta_id+"\""+child_target ;
	    props.setProperty("#getChildRef#",href) ;

	    props.setProperty("#metaImage#",child_meta_image) ;
	    props.setProperty("#childMetaImage#",child_meta_image) ;
	    props.setProperty("#childMetaHeadline#",child_meta_headline) ;
	    props.setProperty("#childMetaText#",child_meta_text) ;
	    props.setProperty("#childCreatedDate#",child_date_created) ;

	    // Put the data in the proper tags.
	    props.setProperty("#menuitemlink#", admin_start+"<a href="+href+">") ;
	    props.setProperty("#/menuitemlink#", "</a>"+admin_stop) ;
	    props.setProperty("#menuitemheadline#", child_meta_headline) ;
	    props.setProperty("#menuitemtext#", child_meta_text) ;
	    props.setProperty("#menuitemdatecreated#", child_date_created) ;
	    props.setProperty("#menuitemdatemodified#", child_date_modified) ;
	    props.setProperty("#menuitemimage#", child_meta_image) ;

	    currentMenu.add(props) ;	// Add the Properties for this menuitem to the current menus list.
	}
	menutime = System.currentTimeMillis()-menutime ;

	//log.log(Log.WILD, "Getting templateinfo.", null) ;

	// I need a list of tags that have numbers that need to be parsed in in their data.
	Properties numberedtags = new Properties () ;

	// I also need a list of files to load, and their corresponding tag...
	Properties toload = new Properties () ;

	// Oh! I need a set of tags to be replaced in the templatefiles we'll load...
	Properties temptags = new Properties () ;

	// Put tags and corresponding data in Properties
	tags.setProperty("#userName#",				user.getString("first_name").trim()+" "+user.getString("last_name").trim()) ;
	tags.setProperty("#session_counter#",		String.valueOf(m_SessionCounter)) ;
	tags.setProperty("#session_counter_date#",	m_SessionCounterDate) ;
	tags.setProperty("#lastDate#",				meta.get(0).toString()) ;
	tags.setProperty("#metaHeadline#",			meta.get(1).toString()) ;
	tags.setProperty("#sys_message#",			(String)sys_data.get(0)) ;
	tags.setProperty("#servlet_url#",			m_ServletUrl) ;
	tags.setProperty("#webMaster#",				m_WebMaster) ;
	tags.setProperty("#webMasterEmail#",		m_WebMasterEmail) ;
	tags.setProperty("#serverMaster#",			m_ServerMaster) ;
	tags.setProperty("#serverMasterEmail#",		m_ServerMasterEmail) ;

	tags.setProperty("#addDoc*#","") ;
	tags.setProperty("#saveSortStart*#","") ;
	tags.setProperty("#saveSortStop*#","") ;

	if ( imagemode ) {	// imagemode
	    tags.setProperty("#img*#",				"<a href=\"ChangeImage?meta_id="+meta_id+"&img=#img_no#\"><img src=\""+m_ImageFolder+"bild.gif\" border=\"0\"><img src=\""+m_ImageFolder+"txt.gif\" border=\"0\"></a>") ;
	    numberedtags.setProperty("#img*#","#img_no#") ;
	}
	if ( textmode ) {	// Textmode
	    tags.setProperty("#txt*#",				"<img src=\""+m_ImageFolder+"red.gif\" border=\"0\">&nbsp;<a href=\""+m_ServletUrl+"ChangeText?meta_id="+meta_id+"&txt=#txt_no#&type=1\"><img src=\""+m_ImageFolder+"txt.gif\" border=\"0\"></a>") ;
	    numberedtags.setProperty("#txt*#","#txt_no#") ;
	}

	if ( checkDocAdminRights(meta_id,user) ) {
	    tags.setProperty("#adminMode#",getMenuButtons(meta_id,user)) ;
	}

	if ( templatemode ) {	//Templatemode! :)

	    String group_name ;
	    if (!groupnamevec.isEmpty()) {
		group_name = (String)groupnamevec.elementAt(0) ;
	    } else {
		group_name = "" ;
	    }

	    StringBuffer templatelist = new StringBuffer() ;
	    // Make a HTML option-list of them...
	    while ( !templates.isEmpty() ) {
		String temp_id = (String)templates.remove(0) ;
		templatelist.append("<option value=\""+temp_id) ;
		if (temp_id.equals(template_id)) {
		    templatelist.append("\" selected>"+templates.remove(0)+"</option>") ;
		} else {
		    templatelist.append("\">"+templates.remove(0)+"</option>") ;
		}
	    }

	    // Fetch all templategroups the user may use.
	    StringBuffer grouplist = new StringBuffer() ;

	    // Make a HTML option-list of the templategroups
	    while ( !templategroups.isEmpty() ) {
		String temp_id = (String)templategroups.remove(0) ;
		grouplist.append("<option value=\""+temp_id) ;
		if (selected_group == Integer.parseInt(temp_id)) {
		    grouplist.append("\" selected>"+templategroups.remove(0)+"</option>") ;
		} else {
		    grouplist.append("\">"+templategroups.remove(0)+"</option>") ;
		}
	    }

	    temptags.setProperty("#getDocType#","") ;
	    temptags.setProperty("#DocMenuNo#","") ;
	    temptags.setProperty("#group#",group_name) ;
	    temptags.setProperty("#getTemplateGroups#", grouplist.toString()) ;
	    temptags.setProperty("#simple_name#",simple_name) ;
	    temptags.setProperty("#getTemplates#",templatelist.toString()) ;

	    // Put templateadmintemplate in list of files to load.
	    toload.setProperty("#changePage#",m_TemplateHome + lang_prefix + "/admin/inPage_admin.html") ;
	}  // if (templatemode)

	temptags.setProperty("#servlet_url#",m_ServletUrl) ;

	if ( menumode ) {

	    // I'll put the doc-types in a html-list
	    Iterator dt_it = doc_types_vec.iterator() ;
	    StringBuffer doc_types_sb = new StringBuffer(256) ;
	    while ( dt_it.hasNext() ) {
		String dt = (String)dt_it.next() ;
		String dtt = (String)dt_it.next() ;
		doc_types_sb.append("<option value=\"") ;
		doc_types_sb.append(dt) ;
		doc_types_sb.append("\">") ;
		doc_types_sb.append(dtt) ;
		doc_types_sb.append("</option>") ;
	    }
	    // Add an option for an existing doc, too
	    String existing_doc_filename = m_TemplateHome + lang_prefix + "/admin/existing_doc_name.html" ;
	    String existing_doc_name = null ;

	    existing_doc_name = getCachedFileString(existing_doc_filename) ;

	    if (doc_types_vec != null && doc_types_vec.size() > 0) {
		doc_types_sb.append("<option value=\"0\">"+existing_doc_name+"</option>") ;
	    }
			
	    // List of files to load, and tags to parse them into
	    toload.setProperty("addDoc",m_TemplateHome + lang_prefix + "/admin/add_doc.html") ;
	    toload.setProperty("saveSortStart",m_TemplateHome + lang_prefix + "/admin/sort_order.html") ;
	    toload.setProperty("saveSortStop",m_TemplateHome + lang_prefix + "/admin/archive_del_button.html") ;
	    toload.setProperty("sort_button",m_TemplateHome + lang_prefix + "/admin/sort_button.html") ;

	    // Some tags to parse in the files we'll load.
	    temptags.setProperty("#doc_types#",doc_types_sb.toString()) ;	// The doc-types.
	    temptags.setProperty("#sortOrder"+sort_order+"#","checked") ;	// The sortorder for this document.
	} // if (menumode)

	temptags.setProperty("#getMetaId#",String.valueOf(meta_id)) ;

	//System.out.println("Got some misc interesting data in "+(System.currentTimeMillis()-time)+" ms") ;
	// Now load the files specified in "toload", and place them in "tags"
	//time = System.currentTimeMillis() ;
	//System.out.println("Loading template-files.") ;
	//log.log(Log.WILD,"Loading template-files.",null) ;

	Perl5Matcher patMat = new Perl5Matcher() ;

	MapSubstitution temptagsmapsubstitution = new MapSubstitution(temptags, false) ;
	
	try {
	    char[] charbuffer = new char[4096] ;
	    StringBuffer templatebuffer = new StringBuffer() ;
	    Enumeration propenum = toload.propertyNames() ;
	    while ( propenum.hasMoreElements() ) {
		
		String filetag = (String)propenum.nextElement() ;
		String templatebufferfilename = toload.getProperty(filetag) ;
		String templatebufferstring = getCachedFileString(templatebufferfilename) ;
				// Humm... Now we must replace the tags in the loaded files too.
		templatebufferstring = org.apache.oro.text.regex.Util.substitute(patMat,HASHTAG_PATTERN,temptagsmapsubstitution,templatebufferstring,org.apache.oro.text.regex.Util.SUBSTITUTE_ALL) ;

		tags.setProperty(filetag,templatebufferstring) ;
		templatebuffer.setLength(0) ;
	    }
	} catch(IOException e) {
	    // this.updateLogs("An error occurred reading the file" + e );
	    log.log(Log.ERROR, "An error occurred reading file during parsing.", e) ;
	    return ("Error occurred reading file during parsing.\n"+e).getBytes("8859_1") ;
	}
	//log.log(Log.WILD, "Loaded and parsed other templatefiles.", null) ;

	if ( menumode ) {	//Menumode! :)

	    // Make a Properties of all tags that contain numbers, and what the number is supposed to replace
	    // in the tag's corresponding data
	    // I.e. "tags" contains the data to replace the numbered tag, but you probably want that number
	    // to be inserted somewhere in that data.
	    // BTW, "*" represents the number in the tag.
	    //numberedtags.setProperty("#addDoc*#","#doc_menu_no#") ;
	    //numberedtags.setProperty("#saveSortStart*#","#doc_menu_no#") ;

	    String savesortstop = tags.getProperty("saveSortStop") ;
	    // We must display the sortbutton, which we read into the tag "#sort_button#"
	    savesortstop = tags.getProperty("sort_button")+savesortstop ;
	    tags.setProperty("saveSortStop",savesortstop) ;
	} else {	// Not menumode...
	    tags.setProperty("saveSortStop",			"") ;
	} // if (menumode)

	// Now... let's load the template!
	// Get templatedir and read the file.
	StringBuffer templatebuffer = loadFile(m_TemplateHome + "text/" + template_id + ".html") ;

	// Check file for tags
	String template = templatebuffer.toString() ;
	StringBuffer result = new StringBuffer(template.length()+8192) ;

	MenuParserSubstitution menuparsersubstitution = new MenuParserSubstitution(menus,menumode,tags) ;
	HashTagSubstitution hashtagsubstitution = new HashTagSubstitution(tags,numberedtags) ;
	LinkedList parse = new LinkedList() ;
	perl5util.split(parse,"/<!-(-\\/?)IMSCRIPT-->/i",template) ;
	Iterator pit = parse.iterator() ;
	boolean parsing = false ;
	//log.log(Log.WILD, "Entering parseloop.") ;
	long menuparsetime = 0 ;
	long oldmenutime = 0 ;
	long tagtime = 0 ;
	long time = System.currentTimeMillis() ;
	while ( pit.hasNext() ) {
	    String nextbit = (String)pit.next() ;
	    if (nextbit.equals("-/")) {
		parsing = false ;
		continue ;
	    } else if (nextbit.equals("-")) {
		parsing = true ;
		continue ;
	    }
	    if (!parsing) {
		result.append(nextbit) ;
		continue ;
	    }
	    // Parse the menus. Aah... the magic of OO...
	    long thistime = System.currentTimeMillis() ;
	    nextbit = org.apache.oro.text.regex.Util.substitute(patMat,MENU_PATTERN, menuparsersubstitution,nextbit,org.apache.oro.text.regex.Util.SUBSTITUTE_ALL) ;
	    menuparsetime += (System.currentTimeMillis()-thistime) ;
	    // (String) nextbit contains the next bit to parse.
	    // Let's first check for the obsolete menus. You know... the ones that suck so bad it isn't even funny anymore...
	    thistime = System.currentTimeMillis() ;
	    PatternMatcherInput pmin = new PatternMatcherInput(nextbit) ;
	    StringBuffer sbtemp = new StringBuffer(nextbit) ;
	    while (patMat.contains(pmin, OBSOLETE_MENU_PATTERN)) {
		MatchResult matres = patMat.getMatch() ;
		int [] menu_param = { Integer.parseInt(matres.group(1)), Integer.parseInt(matres.group(2)), Integer.parseInt(matres.group(3)) } ;
		int endoffset = matres.endOffset(0) ;
		obsoleteMenuParser(sbtemp,matres.beginOffset(0), endoffset, menu_param, menus, menumode, sort_order, patMat,tags) ;
		String newinput = sbtemp.toString() ;
		pmin.setInput(newinput, endoffset, newinput.length()-endoffset ) ;
	    }
	    nextbit = sbtemp.toString() ;
	    oldmenutime += (System.currentTimeMillis()-thistime) ;
	    thistime = System.currentTimeMillis() ;
	    // Now let's substitute all the hashtags.
	    nextbit = org.apache.oro.text.regex.Util.substitute(patMat,HASHTAG_PATTERN,hashtagsubstitution,nextbit,org.apache.oro.text.regex.Util.SUBSTITUTE_ALL) ;
	    tagtime += (System.currentTimeMillis()-thistime) ;
	    result.append(nextbit) ;
	} // end while (pit.hasNext())
	time = (System.currentTimeMillis()-time) ;
	String returnresult = result.toString() ;
		
	if (emp!=null) {
	    // for each string to emphasize
	    for (int i = 0 ; i < emp.length ; ++i) {
		returnresult = org.apache.oro.text.regex.Util.substitute(patMat,patCache.getPattern(Perl5Compiler.quotemeta(emp[i])),EMPHASIZE_SUBSTITUTION,returnresult,org.apache.oro.text.regex.Util.SUBSTITUTE_ALL) ;
	    }
	}
	
	log.log(Log.DEBUG, ""+meta_id+": "+(System.currentTimeMillis()-totaltime)+" Txt: "+texttime+" Img: "+imagetime+" Mnu: "+menutime+" Prs: "+time+" Mnuprs: "+menuparsetime+" OMnuprs: "+oldmenutime+" Tgs: "+tagtime) ;
	return returnresult.getBytes("8859_1") ;
	} catch (RuntimeException ex) {
	    log.log(Log.ERROR, "Error occurred during parsing.",ex ) ;
	    return ex.toString().getBytes("8859_1") ;
	}
    }

    private String hashTagHandler(PatternMatcher patMat, Properties tags, Properties numberedtags) {
	MatchResult matres = patMat.getMatch() ;
	String tag = matres.group(0) ;
	String tagdata = tags.getProperty(tag) ;	// Get value of tag from hash
	if ( tagdata == null ) {
	    if (patMat.contains(tag,HASHTAGNUMBER_PATTERN) ) {
		String numbertag ;
		matres = patMat.getMatch() ;
		String tagnumber = matres.group(1) ;
		String tagexp = tag.substring(0,matres.beginOffset(0))+"*#" ;
		tagdata = tags.getProperty(tagexp) ;
		if (tagdata == null) {
		    tagdata = "" ;
		} else if ( (numbertag = numberedtags.getProperty(tagexp))!=null ) {	// Is it a numbered tag which has data to insert the number in? (Is the four first chars listed in "numberedtags"?) Get the tag to replace with the number.
		    String qm = Perl5Compiler.quotemeta(numbertag) ; // FIXME: Run quotemeta on them before putting them in numberedtags
		    tagdata = org.apache.oro.text.regex.Util.substitute(patMat,patCache.getPattern(qm),new StringSubstitution(tagnumber),tagdata,org.apache.oro.text.regex.Util.SUBSTITUTE_ALL) ;
		}
	    } else {
		tagdata = "" ;
	    }
	}
	return tagdata ;
    }

    private String getMenuModePrefix(PatternMatcher patMat, int menu_id, Properties tags) {
	String temp = tags.getProperty("addDoc") +
	    tags.getProperty("saveSortStart") ;

	return org.apache.oro.text.regex.Util.substitute(patMat,MENU_NO_PATTERN,new StringSubstitution(""+menu_id),temp,org.apache.oro.text.regex.Util.SUBSTITUTE_ALL) ;
    }

    private String getMenuModeSuffix(Properties tags) {
	    return tags.getProperty("saveSortStop") ;
    }

    /**

       Invoked when you have found a block of data that is within menu-tags.

    */
    protected String menuParser (String input, PatternMatcher patMat,  Map menus, int[] implicitMenus, boolean menumode, Properties tags) {
	try {
	    MatchResult menuMatres = patMat.getMatch() ;
	    StringBuffer result = new StringBuffer() ; // FIXME: Optimize size?
	    // Get the id of the menu
	    int menu_id = 0 ;
	    try {
		menu_id = Integer.parseInt(menuMatres.group(1)) ;
	    } catch (NumberFormatException ex) {
		menu_id = implicitMenus[0]++ ;
	    }

	    // Get the linked list that is the menu
	    LinkedList currentMenu = getMenuById(menus,menu_id) ;
	    // Get the data between the menutags.
	    String menutemplate = menuMatres.group(2) ;
	    String menustarttemplate = "" , menustoptemplate = "" ;
	    String looptemplate ;
	    // Check if the looptags are present
	    if (patMat.contains(menutemplate,MENULOOP_PATTERN)) {
		MatchResult menuloopMatres = patMat.getMatch() ;
		// Get the data between the looptags.
		looptemplate = menuloopMatres.group(1) ;
		menustarttemplate = menutemplate.substring(0,menuloopMatres.beginOffset(0)) ;
		menustoptemplate = menutemplate.substring(menuloopMatres.endOffset(0)) ;
	    } else {
		// No looptags are present. The whole menu will loop.
		looptemplate = menutemplate ;
	    }
	    // Create a list of menuitemtemplates
	    LinkedList menuitemtemplatelist = new LinkedList() ;

	    // Loop over the list and insert the menuitemtemplates
	    PatternMatcherInput pmin = new PatternMatcherInput(looptemplate) ;
	    while (patMat.contains(pmin, MENUITEM_PATTERN)) {
		MatchResult menuitemMatres = patMat.getMatch() ;
		String menuitemtemplate = menuitemMatres.group(1) ;
		menuitemtemplatelist.add(menuitemtemplate) ;
	    }

	    if (menuitemtemplatelist.isEmpty()) { // Well, were there any menuitemtags present?
		menuitemtemplatelist.add(looptemplate) ; // No? Use the looptemplate. (Which will be the entire menu if the looptags are missing.)
	    }

	    if (currentMenu != null && currentMenu.size() > 0) {
		// Now i begin outputting the results.
		result.append(menustarttemplate) ;
		// Create an iterator over the menuitemtemplates
		Iterator mitit = menuitemtemplatelist.iterator() ;

		// Loop over the menus
		MapSubstitution mapsubstitution = new MapSubstitution() ;
		for (Iterator mit = currentMenu.iterator() ; mit.hasNext() ; ) {
		    // Make sure we loop over the templates.
		    if (!mitit.hasNext()) {
			mitit = menuitemtemplatelist.iterator() ;
		    }
	    
		    String menuitemtemplate = (String)mitit.next() ;
		    Properties menuitemprops = (Properties)mit.next() ;
		    // Now i need to replace all tags in this template.
		    mapsubstitution.setMap(menuitemprops, true) ;
		    String menuitemresult = org.apache.oro.text.regex.Util.substitute(patMat,HASHTAG_PATTERN,mapsubstitution,menuitemtemplate,org.apache.oro.text.regex.Util.SUBSTITUTE_ALL) ;
		    menuitemresult = org.apache.oro.text.regex.Util.substitute(patMat,MENUITEMHIDETAG_PATTERN, NULL_SUBSTITUTION, menuitemresult,org.apache.oro.text.regex.Util.SUBSTITUTE_ALL) ;
		    result.append(menuitemresult) ;
		}
		// If we still have menuitemtemplates left, loop over them, and hide everything that is supposed to be hidden.
		while (mitit.hasNext()) {
		    String menuitemresult = org.apache.oro.text.regex.Util.substitute(patMat,MENUITEMHIDE_PATTERN, NULL_SUBSTITUTION, (String)mitit.next(), org.apache.oro.text.regex.Util.SUBSTITUTE_ALL) ;
		    result.append(menuitemresult) ;
		}
		result.append(menustoptemplate) ;
	    }
	    String resultstring = result.toString() ;
	    if (menumode) { // If in menumode, make sure to include all the stuff from the proper admintemplates.
		resultstring = getMenuModePrefix(patMat,menu_id,tags)+resultstring+getMenuModeSuffix(tags) ;
	    }
	    return resultstring ;
	} catch ( RuntimeException ex ) {
	    log.log(Log.ERROR, "Error during parsing.", ex) ;
	    return null ;
	}
    }

    protected class MapSubstitution implements Substitution {

	Map map ;
	boolean removeNulls ;
	
	public MapSubstitution() {

	}

	public MapSubstitution(Map map, boolean removeNulls) {
	    this.map = map ;
	    this.removeNulls = removeNulls ;
	}

	public void setMap (Map map, boolean removeNulls) {
	    this.map = map ;
	    this.removeNulls = removeNulls ;
	}

	public Map getMap () {
	    return map ;
	}

	public void appendSubstitution( StringBuffer sb, MatchResult matres, int sc, String originalInput, PatternMatcher patMat, Pattern pat) {
	    String match = matres.group(0) ;
	    String replace = (String)map.get(match) ;
	    if (replace == null ) {
		if (removeNulls) {
		    replace = "" ;
		} else {
		    replace = match ;
		}
	    }
	    sb.append(replace) ;
	}

    }
	
    protected String getCachedFileString(String filename) throws IOException {
	String temp = null ;
	if (null == (temp = (String)(fileCache.getElement(filename)))) {
	    temp = loadFile(filename).toString() ;
	    fileCache.addElement(filename,temp) ;
	}
	return temp ;
    }

    protected class MenuParserSubstitution implements Substitution {

	Map menus ;
	boolean menumode ;
	Properties tags ;
	int[] implicitMenus = {1} ;

	public MenuParserSubstitution (Map menus,boolean menumode, Properties tags ) {
	    this.menumode = menumode ;
	    this.menus = menus ;
	    this.tags = tags ;
	}

	public void appendSubstitution( StringBuffer sb, MatchResult matres, int sc, String originalInput, PatternMatcher patMat, Pattern pat) {
	    sb.append(menuParser(originalInput,patMat,menus,implicitMenus,menumode,tags)) ;
	}

    }

    protected class HashTagSubstitution implements Substitution {

	Properties tags ;
	Properties numberedtags ;

	public HashTagSubstitution (Properties tags, Properties numberedtags) {
	    this.tags = tags ;
    	    this.numberedtags = numberedtags ;
	}

	public void appendSubstitution( StringBuffer sb, MatchResult matres, int sc, String originalInput, PatternMatcher patMat, Pattern pat) {
	    sb.append(hashTagHandler(patMat,tags,numberedtags)) ;
	}

    }
    

    private LinkedList getMenuById(Map menus, int id) {
	return (LinkedList)menus.get(new Integer(id)) ;
    }


    /**
       I sincerely apologize for this, but i create this method only to remove the old stupid parser from the main block of the parseloop.
       This method and way of parsing a template must die, as soon as possible.

       @param sb The stringbuffer to work on
       @param sbindex The start of the menu in the stringbuffer.
       @param reindex The end of the first row of the stringbuffer. (Or the start of the next line, i don't remember.) At least i think that's correct...
       @param menu_param The three ints of the menu. no, rows, and table_col
       @param menus The HashMap containing all the menus.
       @param menumode A boolean detailing whether or not we are in menu-admin-mode
       @param sort_order The magic number that tells us which sort-order we are using.
       @param tags Don't ask... this contains the other tags to parse in the page. Used for getMenuModePrefix
     */
    private void obsoleteMenuParser (StringBuffer sb, int sbindex, int reindex, int[] menu_param, HashMap menus, boolean menumode, int sort_order, PatternMatcher patMat, Properties tags) {
	int menurowsindex = sbindex ;   // We'll store away the index of the start of the menu.
	sbindex = reindex ;
	// Now we'll read each row... so we'll need some storagespace...
	String[] menu_rows = new String[menu_param[1]] ;	//Allocate an array to hold the menurows								
	StringBuffer tmpsb = new StringBuffer() ;
	// for each row in the template...
	for ( int foo=0 ; foo<menu_param[1] ; ++foo ) {
	    char d ;
	    while ( Character.isWhitespace(sb.charAt(sbindex)) ) {
		++sbindex ;	//Skip whitespace...
	    }
	    while ( (d = sb.charAt(sbindex++)) != '\n' && d != '\r' ) {	// Read a line
		tmpsb.append(d) ;
	    }
	    menu_rows[foo] = tmpsb.toString()+"\r\n" ;	// Store the line away... Note that "\r\n" is the standard html (,http, and dos) end-of-line.
	    tmpsb.setLength(0) ;						// Clear the stringbuffer
	}

	//sb.replace(menurowsindex,sbindex,"") ;	// Remove the lines
	//sbindex = menurowsindex ;

	// Hohum... Now we should finally have the template rows of the menu in an array...
	// Now to another problem... parsing the individual bastards.
	// OK... so i have learned that the first two lines are used, seemingly as one string,
	// and the second two are not used... Sigh...
	// First thing i would do if i could would be to redesign these damned templates!
	// And i will one day, so help me Phil, lord of heck!
	LinkedList currentMenu = getMenuById(menus,menu_param[0]) ;
	if ( currentMenu == null ) {
	    sb.replace( menurowsindex, sbindex,"") ;
	    return ;
	}
	// Get an iterator over the elements in the current menu
	Iterator menuit = currentMenu.iterator() ;
	StringBuffer menubuff = new StringBuffer() ;
	String menurowstr = menu_rows[0] ;
	// If the "rows"-attribute of this menu is larger than 1, we need the second row too.
	// Note that if there is only one row, we add the #adminStop# after parsing for <tr><td>
	if ( menu_rows.length>1 ) {
	    menurowstr += "#adminStop#"+menu_rows[1] ;
	}
	// OK, menurowstr now contains a row of the menu, with all the tags and stuff.
	// Now we need to check if it starts with <tr> or <td>
	// and do something about it.
	// These patterns are supposed to match <(/)tr whatever> and <(/)td whatever> at end and beginning of the string.
	String trstart = "\r\n<!-- tr --><tr>" ;   // Html-tag for start of tablerow (menurow)
	String trstop = "</tr><!-- /tr -->\r\n" ;   // Html-tag for end of tablerow (menurow)
	String tdstart = "\r\n<!-- td --><td valign=\"top\">" ;    // Html-tag for start of table-cell (menuelement)
	String tdstop = "</td><!-- /td -->\r\n" ;   // Html-tag for end of table-cell (menuelement)

	/** Added 010212 **/
	if ( patMat.contains(menurowstr,TR_START_PATTERN) ) {
	    trstart = "\r\n<!-- t tr -->"+patMat.getMatch().group(1) ;
	    menurowstr = org.apache.oro.text.regex.Util.substitute(patMat,TR_START_PATTERN,NULL_SUBSTITUTION,menurowstr) ;
	}
	if ( patMat.contains(menurowstr,TR_STOP_PATTERN) ) {
	    trstop = patMat.getMatch().group(1) + "<!-- t /tr -->\r\n" ;
	    menurowstr = org.apache.oro.text.regex.Util.substitute(patMat,TR_STOP_PATTERN,NULL_SUBSTITUTION,menurowstr) ;
	}
	if ( patMat.contains(menurowstr,TD_START_PATTERN) ) {
	    tdstart = "\r\n<!-- t td -->"+patMat.getMatch().group(1) ;
	    menurowstr = org.apache.oro.text.regex.Util.substitute(patMat,TD_START_PATTERN,NULL_SUBSTITUTION,menurowstr) ;
	}
	if ( patMat.contains(menurowstr,TD_STOP_PATTERN) ) {
	    tdstop = patMat.getMatch().group(1)+"<!-- t /td -->\r\n" ;
	    menurowstr = org.apache.oro.text.regex.Util.substitute(patMat,TD_STOP_PATTERN,NULL_SUBSTITUTION,menurowstr) ;
	}

	/** End of added 010212 **/
	//// Make sure we add tags for the html-tags for inactive and archived documents,
	//menurowstr = "#adminStart#"+menurowstr ;
	// Add #adminStop# to the end, if there is only one line.
	// Note that if there is more than one line, we do it before
	// all the regexing for <tr><td>
	if ( menu_rows.length==1 ) {
	    menurowstr += "#adminStop#" ;
	}
	final Pattern HASHTAG_PATTERN = patCache.getPattern("#[^#\"<> \\t\\r\\n]+#") ;
	// for each element of the menu...
	MapSubstitution mapsubstitution = new MapSubstitution() ;
	for ( int rowcount = 0 ; menuit.hasNext() ; ) {
	    if ( rowcount % menu_param[2]==0 ) {	// If this is a new tablerow... (menu_param[2] contains the number of columns per row)
		menubuff.append(trstart) ;      // append tag for new row: "<TR>", or whatever was already used in the template.
	    }
	    menubuff.append(tdstart) ;	// Begin new cell: "<TD>", or whatever was already used in the template.
				// Here we are going to output one menuitem.
				// All data is stored in a Properties, remember?
	    //StringBuffer menurow = new StringBuffer(menurowstr) ;	// Allocate some workroom
	    Properties props = (Properties)menuit.next() ;	// Retrieve the tags and data for this menuitem...

	    mapsubstitution.setMap(props, true) ;
	    String menurow = org.apache.oro.text.regex.Util.substitute(patMat,HASHTAG_PATTERN,mapsubstitution,menurowstr,org.apache.oro.text.regex.Util.SUBSTITUTE_ALL) ;

	    menubuff.append(menurow+tdstop) ;    // OK... one row done. Append it to the menubuffer and end the cell.
	    ++rowcount ;    // And, of course... increase the rowcount.
	    if ( rowcount%menu_param[2]==0 ) {	// If next row is a new tablerow...
		menubuff.append(trstop) ;   // append </tr>, or the equivalent from the template.
	    }
	}
	String menubuff_str = menubuff.toString() ;

	if (menumode) {
	    menubuff_str = "<tr><td>"+getMenuModePrefix(patMat,menu_param[0],tags)+"</td></tr><!-- menu -->"+menubuff_str+"<!-- /menu --><tr><td>"+getMenuModeSuffix(tags)+"</td></tr>" ;
	}
	// Yay! One menu done. Insert into the pagebuffer...
	sb.replace( menurowsindex, sbindex,menubuff_str) ;
	//sb.insert(sbindex,menubuff_str) ;
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

		String tempbuffer_filename = m_TemplateHome + lang_prefix + "/admin/adminbuttons/adminbuttons"+doc_type+".html" ;
		String templatebuffer_filename = m_TemplateHome + lang_prefix + "/admin/adminbuttons/adminbuttons.html" ;
		String superadmin_filename = m_TemplateHome + lang_prefix + "/admin/adminbuttons/superadminbutton.html" ;

		tempbuffer = new StringBuffer(getCachedFileString(tempbuffer_filename)) ;
		templatebuffer = new StringBuffer(getCachedFileString(templatebuffer_filename)) ;
		superadmin = new StringBuffer(getCachedFileString(superadmin_filename)) ;
		
	    } catch(IOException e) {
		this.updateLogs("An error occurred reading the file" + e );
		System.out.println("An error occurred reading the file" + e) ;
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
	    System.out.println("Error occurred while parsing the adminbuttons.") ;
	    ex.printStackTrace(System.out) ;
	    return null ;
	}
    }

    /**
       Returns the menubuttonrow
    */
    public String getMenuButtons(int meta_id, User user) {
	return getMenuButtons(String.valueOf(meta_id),user) ;
    }
    
    protected StringBuffer loadFile(String file) {
	StringBuffer tempbuffer = new StringBuffer() ;
	try {
	    char[] charbuffer = new char[4096] ;
	    BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file),"8859_1"));
	    // Load the file
	    int chars_read = 0 ;
	    while (-1 < (chars_read = br.read(charbuffer))) {
		tempbuffer.append(charbuffer,0,chars_read) ;
	    }
	    br.close();
	} catch (IOException ex) {
	    log.log(Log.ERROR, "File not found during parsing.", ex) ;
	    tempbuffer.append(ex.getMessage()) ;
	}
	return tempbuffer ;
    }

	/**
	* <p>Replace a variable in a template with data.
	*/
	public static String replaceTag(String tag, int tagStart, String str, String insertStr) {

		String temp_str1 = str.substring(0, tagStart);
		String temp_str2 = str.substring(tagStart+(tag.length()), str.length());
		str  = temp_str1 ;
		str += insertStr;
		str += temp_str2 ;

		return str;
	}


	/**
	* <p>Find a tag in a template file.
	*/
	public static int findTag(String str,  String tag) {
		int i = str.indexOf(tag) ;
		return i ;
	}


	/**
	 set logflag
	  */
	public void setLogFlag(boolean flag) {
		m_PrintLogToWindow = flag ;
	}


	/**
	* <p>Find a variable and replace it with data from SQL database,system or other source.
	*/
	public String findVariable(String str,int index,imcode.server.User user,Table meta,Vector child_status,
		Vector child_meta_headlines,Vector child_meta_texts,Vector child_created_dates,Vector childs,Vector texts,int current_menu,
		Vector user_roles,Vector admin_roles,Vector categories,Vector all_processings,Table text_doc,
		Vector help_texts,Table language,Vector languages, Vector templates,Vector all_roles,
		Vector all_categories,Vector urlRefs,Vector urlTexts,Vector childs_manual_sort,
		Vector childs_menu_sort,Table images[],int txt_max,int img_max,Vector text_types ,
		Vector urlTargets, Vector urlTargetNames,Vector child_targets,Vector child_doc_types) {
		Vector tempVec = new Vector(10) ;
		int i = 0 ;
		String temp_str1 = "" ;
		String temp_str2 = "" ;
		String temp_str3 = "" ;

		if ( str.indexOf("#menu") != -1 )
			return "" ;

		int varCount = 0 ;
		int p = 0 ;
		while ( (p = str.indexOf("#",p) ) != -1 ) {
			varCount++ ;
			p++ ;
		}
		varCount = varCount / 2 ;



		boolean finished = false ;
		int varFound = 0 ;

		for ( int n = 0 ; n < varCount ; n++ ) {


			// ******************************* VARIABLES *****************************************
			for ( int m = 1 ; m < 10 ; m++ ) {
				String tmpStr = "#addDoc" + m + "#" ;
				if ( (i = IMCService.findTag(str,tmpStr)) != -1 ) {
					if ( user.getBoolean("admin_mode") ) {
						str = IMCService.replaceTag(tmpStr,i,str,
							interpretAdminTemplateX(meta.getInt("meta_id"),user,"add_doc.html",m,txt_max,img_max,0,
							meta,text_doc,templates,child_meta_headlines,child_meta_texts,child_created_dates,
							childs,childs_menu_sort,childs_manual_sort,child_status,texts,urlRefs,urlTexts,
							images,user_roles,all_roles,admin_roles,categories,all_categories,
							all_processings,help_texts,languages,text_types,urlTargets,urlTargetNames,
							child_targets,child_doc_types)) ;
					} else {
						str = IMCService.replaceTag(tmpStr,i,str,"") ;
					}
					if ( ++varFound == varCount )
						return str + "\n" ;
				}
			}



			if ( (i = IMCService.findTag(str,"#adminMode#")) != -1 ) {
				//USER RIGHTS
				String sqlStr  = "select permission_id from user_rights,users\n" ;
				sqlStr += "where user_rights.meta_id = " + meta.getInt("meta_id")  + "\n";
				sqlStr += "and user_rights.user_id = users.user_id\n" ;
				sqlStr += "and users.user_id = " + user.getInt("user_id") ;

				DBConnect dbc = new DBConnect(m_conPool) ;
				dbc.getConnection() ;
				dbc.setSQLString(sqlStr) ;
				dbc.createStatement() ;
				Vector permissions = (Vector)dbc.executeQuery() ;
				dbc.clearResultSet() ;


				boolean user_admin_rights = false ;
				for ( int pm = 0 ; pm < permissions.size() ; pm++ )
					if ( Integer.parseInt(permissions.elementAt(pm).toString()) > 1 )
						user_admin_rights = true ;


					// ROLES RIGHTS
					sqlStr  = "select permission_id from roles_rights,users,user_roles_crossref\n" ;
					sqlStr += "where roles_rights.meta_id = " + meta.getInt("meta_id")  + "\n";
					sqlStr += "and roles_rights.role_id = user_roles_crossref.role_id" + "\n" ;
					sqlStr += "and users.user_id = user_roles_crossref.user_id" + "\n" ;
					sqlStr += "and users.user_id = " + user.getInt("user_id") ;

					dbc.setSQLString(sqlStr) ;
					dbc.createStatement() ;
					Vector role_permissions = (Vector)dbc.executeQuery() ;
					dbc.clearResultSet() ;


					boolean role_admin_rights = false ;
					for ( int pm = 0 ; pm < role_permissions.size() && !role_admin_rights ; pm++ )
						if ( Integer.parseInt(role_permissions.elementAt(pm).toString()) > 1 )
							role_admin_rights = true ;


						// is user superadmin?
						sqlStr  = "select role_id from users,user_roles_crossref\n" ;
						sqlStr += "where users.user_id = user_roles_crossref.user_id\n" ;
						sqlStr += "and user_roles_crossref.role_id = 0\n" ;
						sqlStr += "and users.user_id = " + user.getInt("user_id") ;
						dbc.setSQLString(sqlStr);
						dbc.createStatement() ;
						Vector super_admin_vec = (Vector)dbc.executeQuery() ;
						dbc.clearResultSet() ;
						dbc.closeConnection() ;
						dbc = null ;

						boolean super_admin = false ;
						if ( super_admin_vec.size() > 0 )
							super_admin = true ;



						if ( //user_admin_rights ||
						role_admin_rights || super_admin ) {
							if ( user.getBoolean("admin_mode") )
								str = IMCService.replaceTag("#adminMode#",i,str,"<img src=\"" + m_ImageFolder + "admin_off.gif\" border=\"0\">") ; // avsl_adm.jpg
							else
								str = IMCService.replaceTag("#adminMode#",i,str,"<img src=\"" + m_ImageFolder  + "admin_on.gif\" border=\"0\">") ;	// admin.jpg
						} else
							str = IMCService.replaceTag("#adminMode#",i,str,"&nbsp;") ;

						if ( ++varFound == varCount )
							return str + "\n" ;
			}


			if ( (i = IMCService.findTag(str,"#webMaster#")) != -1 ) {
				str = IMCService.replaceTag("#webMaster#",i,str,m_WebMaster) ;
				if ( ++varFound == varCount )
					return str + "\n" ;
			}


			if ( (i = IMCService.findTag(str,"#webMasterEmail#")) != -1 ) {
				str = IMCService.replaceTag("#webMasterEmail#",i,str,m_WebMasterEmail) ;
				if ( ++varFound == varCount )
					return str + "\n" ;
			}


			if ( (i = IMCService.findTag(str,"#serverMaster#")) != -1 ) {
				str = IMCService.replaceTag("#serverMaster#",i,str,m_ServerMaster) ;
				if ( ++varFound == varCount )
					return str + "\n" ;
			}


			if ( (i = IMCService.findTag(str,"#serverMasterEmail#")) != -1 ) {
				str = IMCService.replaceTag("#serverMasterEmail#",i,str,m_ServerMasterEmail) ;
				if ( ++varFound == varCount )
					return str + "\n" ;
			}



			if ( (i = IMCService.findTag(str,"#adminEmail#")) != -1 ) {
				String sqlStr  = "select email from user_rights,users\n" ;
				sqlStr += "where user_rights.meta_id = " + meta.getInt("meta_id")  + "\n";
				sqlStr += "and user_rights.permission_id = 99\n" ;
				sqlStr += "and user_rights.user_id = users.user_id\n" ;



				DBConnect dbc = new DBConnect(m_conPool) ;
				dbc.getConnection() ;
				dbc.setSQLString(sqlStr) ;
				dbc.createStatement() ;
				Vector admin_email = (Vector)dbc.executeQuery() ;
				dbc.clearResultSet() ;

				//close connection
				dbc.closeConnection() ;
				dbc = null ;


				if ( admin_email.size() == 0 ) {
					admin_email.addElement("") ;
				}

				str = IMCService.replaceTag("#adminEmail#",i,str,
					admin_email.elementAt(0).toString().trim() ) ;
				if ( ++varFound == varCount )
					return str + "\n" ;
			}

			if ( (i = IMCService.findTag(str,"#adminName#")) != -1 ) {
				String sqlStr  = "select first_name,last_name from user_rights,users\n" ;
				sqlStr += "where user_rights.meta_id = " + meta.getInt("meta_id")  + "\n";
				sqlStr += "and user_rights.permission_id = 99\n" ;
				sqlStr += "and user_rights.user_id = users.user_id\n" ;



				DBConnect dbc = new DBConnect(m_conPool) ;
				dbc.getConnection() ;
				dbc.setSQLString(sqlStr) ;
				dbc.createStatement() ;
				Vector admin_name = (Vector)dbc.executeQuery() ;
				dbc.clearResultSet() ;

				//close connection
				dbc.closeConnection() ;
				dbc = null ;


				if ( admin_name.size() == 0 ) {
					admin_name.addElement("") ;
					admin_name.addElement("") ;
				}

				str = IMCService.replaceTag("#adminName#",i,str,
					admin_name.elementAt(0).toString().trim() + " " + admin_name.elementAt(1).toString().trim()) ;
				if ( ++varFound == varCount )
					return str + "\n" ;
			}


			if ( (i = IMCService.findTag(str,"#activated_date#")) != -1 ) {
				str = IMCService.replaceTag("#activated_date#",i,str,meta.getString("activated_date")) ;
				if ( ++varFound == varCount )
					return str + "\n" ;
			}


			if ( (i = IMCService.findTag(str,"#activated_time#")) != -1 ) {
				str = IMCService.replaceTag("#activated_time#",i,str,meta.getString("activated_time")) ;
				if ( ++varFound == varCount )
					return str + "\n" ;
			}



			if ( (i = IMCService.findTag(str,"#archive_button#")) != -1 ) {
				if ( !user.getBoolean("archive_mode") )
					str = IMCService.replaceTag("#archive_button#",i,str,
						interpretAdminTemplateX(meta.getInt("meta_id"),user,"archive_button.html",1,0,0,0,
						meta,text_doc,templates,child_meta_headlines,child_meta_texts,child_created_dates,
						childs,childs_menu_sort,childs_manual_sort,child_status,texts,urlRefs,urlTexts,
						images,user_roles,all_roles,admin_roles,categories,all_categories,
						all_processings,help_texts,languages,text_types,urlTargets,urlTargetNames,
						child_targets,child_doc_types)) ;

				else
					str = IMCService.replaceTag("#archive_button#",i,str,
						interpretAdminTemplateX(meta.getInt("meta_id"),user,"archive_off_button.html",1,0,0,0,
						meta,text_doc,templates,child_meta_headlines,child_meta_texts,child_created_dates,
						childs,childs_menu_sort,childs_manual_sort,child_status,texts,urlRefs,urlTexts,
						images,user_roles,all_roles,admin_roles,categories,all_categories,
						all_processings,help_texts,languages,text_types,urlTargets,urlTargetNames,
						child_targets,child_doc_types)) ;

				//		 str = "" ;
				if ( ++varFound == varCount )
					return str + "\n" ;
			}



			if ( (i = IMCService.findTag(str,"#archived#")) != -1 ) {
				str = IMCService.replaceTag("#archived#",i,str,meta.getBoolean("archive") ? " CHECKED" : "" ) ;
				if ( ++varFound == varCount )
					return str + "\n" ;
			}


			if ( (i = IMCService.findTag(str,"#archived_date#")) != -1 ) {
				str = IMCService.replaceTag("#archived_date#",i,str,meta.getString("archived_date")) ;
				if ( ++varFound == varCount )
					return str + "\n" ;
			}


			if ( (i = IMCService.findTag(str,"#archived_time#")) != -1 ) {
				str = IMCService.replaceTag("#archived_time#",i,str,meta.getString("archived_time")) ;
				if ( ++varFound == varCount )
					return str + "\n" ;
			}


			if ( (i = IMCService.findTag(str,"#archiveDelBox#")) != -1 ) {
				if ( user.getBoolean("admin_mode") ) {


					String tmpStr  = "<input type=checkbox name=\"archiveDelBox\"" ;
					tmpStr +=  " value=\"" + childs.elementAt(index-1).toString() + "\">";
					str = IMCService.replaceTag("#archiveDelBox#",i,str,tmpStr) ;
				} else
					str = IMCService.replaceTag("#archiveDelBox#",i,str,"") ;

				if ( ++varFound == varCount )
					return str + "\n" ;
			}


			if ( (i= IMCService.findTag(str,"#changeMeta#")) != -1 ) {
				if ( user.getBoolean("admin_mode") )
					str = IMCService.replaceTag("#changeMeta#",i,str,
						interpretAdminTemplateX(meta.getInt("meta_id"),user,"change_meta_url.html",1,txt_max,img_max,0,
						meta,text_doc,templates,child_meta_headlines,child_meta_texts,child_created_dates,
						childs,childs_menu_sort,childs_manual_sort,child_status,texts,urlRefs,urlTexts,
						images,user_roles,all_roles,admin_roles,categories,all_categories,
						all_processings,help_texts,languages,text_types,urlTargets,urlTargetNames,
						child_targets,child_doc_types)) ;
				else
					str = IMCService.replaceTag("#changeMeta#",i,str,"") ;
				if ( ++varFound == varCount )
					return str + "\n" ;
			}



			if ( (i= IMCService.findTag(str,"#changePage#")) != -1 ) {
				if ( user.getBoolean("admin_mode") )
					str = IMCService.replaceTag("#changePage#",i,str,     // change_doc_url.html
						interpretAdminTemplateX(meta.getInt("meta_id"),user,"inPage_admin.html",1,txt_max,img_max,0,
						meta,text_doc,templates,child_meta_headlines,child_meta_texts,child_created_dates,
						childs,childs_menu_sort,childs_manual_sort,child_status,texts,urlRefs,urlTexts,
						images,user_roles,all_roles,admin_roles,categories,all_categories,
						all_processings,help_texts,languages,text_types,urlTargets,urlTargetNames,
						child_targets,child_doc_types)) ;
				else
					str = IMCService.replaceTag("#changePage#",i,str,"") ;
				if ( ++varFound == varCount )
					return str + "\n" ;
			}

			if ( (i = IMCService.findTag(str,"#childMetaHeadline#")) != -1 ) {
				str = IMCService.replaceTag("#childMetaHeadline#",i,str,child_meta_headlines.elementAt(index-1).toString()) ;
				if ( ++varFound == varCount )
					return str + "\n" ;
			}


			if ( (i = IMCService.findTag(str,"#childMetaText#")) != -1 ) {
				str = IMCService.replaceTag("#childMetaText#",i,str,child_meta_texts.elementAt(index-1).toString()) ;
				if ( ++varFound == varCount )
					return str + "\n" ;
			}

			if ( (i = IMCService.findTag(str,"#childMetaImage#")) != -1 ) {
				String sqlStr  = "select meta_image from meta\n" ;
				sqlStr += "where meta_id = " + childs.elementAt(index-1).toString() ;

				DBConnect dbc = new DBConnect(m_conPool) ;
				String child_meta_image = dbc.sqlQueryStr(sqlStr) ;
				dbc.closeConnection() ;
				dbc = null ;
				str = IMCService.replaceTag("#childMetaImage#",i,str,child_meta_image) ;
				if ( ++varFound == varCount )
					return str + "\n" ;
			}


			if ( (i = IMCService.findTag(str,"#child_no#")) != -1 ) {
				str = IMCService.replaceTag("#child_no#",i,str,Integer.toString(index)) ;
				if ( ++varFound == varCount )
					return str + "\n" ;
			}


			if ( (i = IMCService.findTag(str,"#classification#")) != -1 ) {
				str = IMCService.replaceTag("#classification#",i,str,meta.getString("classification")) ;
				if ( ++varFound == varCount )
					return str + "\n" ;
			}


			if ( (i = IMCService.findTag(str,"#session_counter#")) != -1 ) {
				str = IMCService.replaceTag("#session_counter#",i,str,"" + m_SessionCounter) ;
				if ( ++varFound == varCount )
					return str + "\n" ;
			}


			if ( (i = IMCService.findTag(str,"#session_counter_date#")) != -1 ) {
				str = IMCService.replaceTag("#session_counter_date#",i,str,m_SessionCounterDate) ;
				if ( ++varFound == varCount )
					return str + "\n" ;
			}



			if ( (i = IMCService.findTag(str,"#createdDate#")) != -1 ) {
				str = IMCService.replaceTag("#createdDate#",i,str,meta.getString("date_created")) ;
				if ( ++varFound == varCount )
					return str + "\n" ;
			}


			if ( (i = IMCService.findTag(str,"#childCreatedDate#")) != -1 ) {
				str = IMCService.replaceTag("#childCreatedDate#",i,str,child_created_dates.elementAt(index-1).toString()) ;
				if ( ++varFound == varCount )
					return str + "\n" ;
			}


			if ( (i = IMCService.findTag(str,"#description#")) != -1 ) {
				str = IMCService.replaceTag("#description#",i,str,meta.getString("description")) ;
				if ( ++varFound == varCount )
					return str + "\n" ;
			}


			if ( (i = IMCService.findTag(str,"#!disable_search#")) != -1 ) {
				str = IMCService.replaceTag("#!disable_search#",i,str,meta.getBoolean("disable_search") ? "" : " CHECKED" );
				if ( ++varFound == varCount )
					return str + "\n" ;
			}


			if ( (i = IMCService.findTag(str,"#disable_search#")) != -1 ) {
				str = IMCService.replaceTag("#disable_search#",i,str,meta.getBoolean("disable_search") ? " CHECKED" : "" );
				if ( ++varFound == varCount )
					return str + "\n" ;
			}


			if ( (i = IMCService.findTag(str,"#doc_menu_no#")) != -1 ) {
				str = IMCService.replaceTag("#doc_menu_no#",i,str,Integer.toString(index)) ;
				if ( ++varFound == varCount )
					return str + "\n" ;
			}



			if ( (i = IMCService.findTag(str,"#expanded#")) != -1 ) {
				str = IMCService.replaceTag("#expanded#",i,str,meta.getBoolean("expand") ? " CHECKED" : "") ;
				if ( ++varFound == varCount )
					return str + "\n" ;
			}


			if ( (i = IMCService.findTag(str,"#external_doctypes#")) != -1 ) {
				String option_list = "" ;
				for ( int x = 0 ; x < m_ExDoc.length && m_ExDoc[x] != null ; x++ )
					option_list += "<option value=\"external_doc:" + m_ExDoc[x].getDocType() + "\">" + m_ExDoc[x].getDocName() + "\n" ;
				str = IMCService.replaceTag("#external_doctypes#",i,str,option_list) ;
				if ( ++varFound == varCount )
					return str + "\n" ;
			}



			if ( (i = IMCService.findTag(str,"#frame_set#")) != -1 ) {
				String sqlStr = "" ;
				DBConnect dbc = new DBConnect(m_conPool) ;
				dbc.getConnection() ;
				sqlStr  = "select frame_set from frameset_docs where meta_id = " + meta.getInt("meta_id") ;
				dbc.setSQLString(sqlStr) ;
				dbc.createStatement() ;
				Vector frame_set = (Vector)dbc.executeQuery() ;
				dbc.clearResultSet() ;

				//close connection
				dbc.closeConnection() ;
				dbc = null ;

				str = IMCService.replaceTag("#frame_set#",i,str,frame_set.elementAt(0).toString()) ;
				if ( ++varFound == varCount )
					return str + "\n" ;
			}


			if ( (i = IMCService.findTag(str,"#getChildMetaId#")) != -1 ) {
				if ( index - 1 < childs.size() )
					str = IMCService.replaceTag("#getChildMetaId#",i,str,childs.elementAt(index-1).toString()) ;
				else
					str = "" ;
				if ( ++varFound == varCount )
					return str + "\n" ;
			}

			// /servlet/GetDoc?meta_id=
			if ( (i = IMCService.findTag(str,"#getChildRef#")) != -1 ) {
				if ( index - 1 < childs.size() ) {
					String temp   = child_targets.elementAt(index-1).toString() ;

					String target = temp.substring(0,temp.indexOf("/")) ;
					String frame_name = "" ;
					if (temp.indexOf("/") < temp.length() )
						frame_name = temp.substring(temp.indexOf("/") + 1,temp.length()) ;
					if (frame_name.length() > 0)
						target = frame_name ;
					String doc_servlet = (user.getBoolean("admin_mode") && !(child_doc_types.elementAt(index-1).toString().equals("2") || child_doc_types.elementAt(index-1).toString().equals("1"))) ? "AdminDoc" : "GetDoc" ;
					str = IMCService.replaceTag("#getChildRef#",i,str, "\"" + m_ServletUrl +
						doc_servlet+"?meta_id=" + childs.elementAt(index-1).toString() +
						"&parent_meta_id=" + meta.getString("meta_id") + "\"" + " target=\"" + target + "\"") ;

				} else
					str = IMCService.replaceTag("#getChildRef#",i,str, "") ;

				if ( ++varFound == varCount )
					return str + "\n" ;
			}

			// childtargets
			if ( (i = IMCService.findTag(str,"#getChildTarget#")) != -1 ) {

				if ( index - 1 < child_targets.size() && !user.getBoolean("admin_mode") )
					str = IMCService.replaceTag("#getChildTarget#",i,str, " target=\"" +
						child_targets.elementAt(index-1).toString() + "\"") ;
				else
					str = IMCService.replaceTag("#getChildTarget#",i,str, "") ;

				if ( ++varFound == varCount )
					return str + "\n" ;
			}


			if ( (i = IMCService.findTag(str,"#getMetaId#")) != -1 ) {
				str = IMCService.replaceTag("#getMetaId#",i,str,meta.getString("meta_id")) ;
				if ( ++varFound == varCount )
					return str + "\n" ;
			}



			if ( (i = IMCService.findTag(str,"#getMetaImage#")) != -1 ) {
				str = IMCService.replaceTag("#getMetaImage#",i,str,meta.getString("meta_image")) ;
				if ( ++varFound == varCount )
					return str + "\n" ;
			}


			if ( (i = IMCService.findTag(str,"#helpRef#")) != -1 ) {
				str = IMCService.replaceTag("#helpRef#",i,str,"\"" + m_ServletUrl +"GetDoc?meta_id=100\"") ;
				if ( ++varFound == varCount )
					return str + "\n" ;
			}


			if ( (i = IMCService.findTag(str,"#helpText#")) != -1 ) {
				str = IMCService.replaceTag("#helpText#",i,str,help_texts.elementAt(meta.getInt("help_text_id")).toString()) ;
				if ( ++varFound == varCount )
					return str + "\n" ;
			}

			if ( (i = IMCService.findTag(str,"#homePageRef#")) != -1 ) {
				str = IMCService.replaceTag("#homePageRef#",i,str,"\"" + m_ServletUrl + "GetDoc?meta_id=" + m_DefaultHomePage + "\"" + " target=\"_top\"") ;
				if ( ++varFound == varCount )
					return str + "\n" ;
			}


			if ( (i = IMCService.findTag(str,"#!html#")) != -1 ) {
				if ( Integer.parseInt(text_types.elementAt(index-1).toString()) == 0 )
					str = IMCService.replaceTag("#!html#",i,str," CHECKED");
				else
					str = IMCService.replaceTag("#!html#",i,str,"") ;
				if ( ++varFound == varCount )
					return str + "\n" ;
			}


			if ( (i = IMCService.findTag(str,"#html#")) != -1 ) {
				if ( Integer.parseInt(text_types.elementAt(index-1).toString()) == 1 )
					str = IMCService.replaceTag("#html#",i,str," CHECKED");
				else
					str = IMCService.replaceTag("#html#",i,str, "");
				if ( ++varFound == varCount )
					return str + "\n" ;
			}



			if ( (i = IMCService.findTag(str,"#img_max#")) != -1 ) {
				str = IMCService.replaceTag("#img_max#",i,str,Integer.toString(index)) ;
				if ( ++varFound == varCount )
					return str + "\n" ;
			}


			if ( (i = IMCService.findTag(str,"#img_no#")) != -1 ) {
				str = IMCService.replaceTag("#img_no#",i,str,Integer.toString(index)) ;
				if ( ++varFound == varCount )
					return str + "\n" ;
			}


			if ( (i = IMCService.findTag(str,"#lang#")) != -1 ) {
				str = IMCService.replaceTag("#lang#",i,str,language.getString(meta.getString("lang_prefix"))) ;
				if ( ++varFound == varCount )
					return str + "\n" ;
			}


			if ( (i = IMCService.findTag(str,"#lastDate#")) != -1 ) {
				str = IMCService.replaceTag("#lastDate#",i,str,meta.getObject("date_modified").toString()) ;
				if ( ++varFound == varCount )
					return str + "\n" ;
			}


			if ( (i = IMCService.findTag(str,"#lastPageRef#")) != -1 ) {
				str = IMCService.replaceTag("#lastPageRef#",i,str,"\"" + m_ServletUrl + "GetDoc?meta_id=" + user.getInt("last_page") + "\"") ;
				if ( ++varFound == varCount )
					return str + "\n" ;
			}





			if ( (i = IMCService.findTag(str,"#metaHeadline#")) != -1 ) {
				str = IMCService.replaceTag("#metaHeadline#",i,str,meta.getString("meta_headline")) ;
				if ( ++varFound == varCount )
					return str + "\n" ;
			}


			if ( (i = IMCService.findTag(str,"#metaImage#")) != -1 ) {
				//		str = IMCService.replaceTag("#metaImage#",i,str,"<IMG SRC= " + "\"" + meta.getString("meta_image") + "\">") ;
				str = IMCService.replaceTag("#metaImage#",i,str, meta.getString("meta_image")) ;
				if ( ++varFound == varCount )
					return str + "\n" ;
			}


			if ( (i = IMCService.findTag(str,"#metaText#")) != -1 ) {
				str = IMCService.replaceTag("#metaText#",i,str,meta.getString("meta_text")) ;
				if ( ++varFound == varCount )
					return str + "\n" ;
			}

			if ( (i = IMCService.findTag(str,"#newMetaId#")) != -1 ) {
				// create a db connection
				DBConnect dbc = new DBConnect(m_conPool,"select max(meta_id) from meta") ;
				dbc.getConnection() ;
				dbc.createStatement() ;
				Vector highestMetaId = (Vector)dbc.executeQuery() ;
				dbc.clearResultSet() ;
				dbc.closeConnection() ;
				dbc = null ;

				int nextMetaId = Integer.parseInt(highestMetaId.elementAt(0).toString()) + 1 ;
				str = IMCService.replaceTag("#newMetaId#",i,str,Integer.toString(nextMetaId)) ;

				if ( ++varFound == varCount )
					return str + "\n" ;
			}



			if ( (i = IMCService.findTag(str,"#owner#")) != -1 ) {
				String sqlStr  = "select first_name,last_name from user_rights,users\n" ;
				sqlStr += "where user_rights.meta_id = " + meta.getInt("meta_id")  + "\n";
				sqlStr += "and user_rights.permission_id = 99\n" ;
				sqlStr += "and user_rights.user_id = users.user_id\n" ;



				DBConnect dbc = new DBConnect(m_conPool) ;
				dbc.getConnection() ;
				dbc.setSQLString(sqlStr) ;
				dbc.createStatement() ;
				Vector owner = (Vector)dbc.executeQuery() ;
				dbc.clearResultSet() ;

				//close connection
				dbc.closeConnection() ;
				dbc = null ;


				if ( owner.size() == 0 ) {
					owner.addElement("-") ;
					owner.addElement("") ;
				}

				str = IMCService.replaceTag("#owner#",i,str,
					owner.elementAt(0).toString().trim() + " " + owner.elementAt(1).toString().trim()) ;
				if ( ++varFound == varCount )
					return str + "\n" ;
			}




			if ( (i = IMCService.findTag(str,"#process#")) != -1 ) {
				str = IMCService.replaceTag("#process#",i,str,all_processings.elementAt(meta.getInt("processing_id")).toString()) ;
				if ( ++varFound == varCount )
					return str + "\n" ;
			}


			for ( int m = 1 ; m < 10 ; m++ ) {
				if ( (i = IMCService.findTag(str,"#saveSortStart" + m + "#")) != -1 ) {
					temp_str1 = str.substring(0,i) ;
					temp_str2 = str.substring(i+16,str.length()) ;
					str  = temp_str1 ;
					str += temp_str2 ;


					// get childs for this menu
					String sqlStr  = "select to_meta_id from meta,childs" ;
					sqlStr +=	" where meta.meta_id = childs.to_meta_id" ;
					sqlStr += " and childs.meta_id =" + meta.getInt("meta_id") ;
					sqlStr += " and childs.menu_sort = " + m ;
					sqlStr += " and meta.archive=" + user.getInt("archive_mode") ;

					DBConnect dbc = new DBConnect(m_conPool,sqlStr) ;
					dbc.getConnection() ;
					dbc.createStatement() ;
					Vector childsThisMenu = (Vector)dbc.executeQuery() ;
					dbc.clearResultSet() ;
					dbc.closeConnection() ;
					dbc = null ;

					if ( user.getBoolean("admin_mode") && childsThisMenu.size() > 0 ) {
						str += "<FORM METHOD=POST ACTION=\"" + m_ServletUrl + "SaveSort\">\n" ;

						//	if (text_doc.getInt("sort_order") == 2) {
						str += "\n<INPUT TYPE=HIDDEN NAME=childs VALUE=\"" ;
						for ( int c = 0 ; c < childsThisMenu.size() ; c++ )
							str += childsThisMenu.elementAt(c).toString() + "," ;
						str += "-1\">\n" ;

						str += "<INPUT TYPE=HIDDEN NAME=meta_id VALUE=\"" + meta.getString("meta_id") + "\">\n";
						str += "<INPUT TYPE=HIDDEN NAME=doc_menu_no VALUE=\""+m+"\">\n";
						//	}
					}

					if ( ++varFound == varCount )
						return str + "\n" ;

				}
			}

			for ( int m = 1 ; m < 10 ; m++ ) {
				if ( (i = IMCService.findTag(str,"#saveSortStop" + m + "#")) != -1 ) {
					temp_str1 = str.substring(0,i) ;
					temp_str2 = str.substring(i+15,str.length()) ;
					str  = temp_str1 ;
					str += temp_str2 ;


					// get childs for this menu
					String sqlStr  = "select to_meta_id from meta,childs" ;
					sqlStr +=	" where meta.meta_id = childs.to_meta_id" ;
					sqlStr += " and childs.meta_id =" + meta.getInt("meta_id") ;
					sqlStr += " and childs.menu_sort = " + m ;
					sqlStr += " and meta.archive=" + user.getInt("archive_mode") ;

					DBConnect dbc = new DBConnect(m_conPool,sqlStr) ;
					dbc.getConnection() ;
					dbc.createStatement() ;
					Vector childsThisMenu = (Vector)dbc.executeQuery() ;
					dbc.clearResultSet() ;
					dbc.closeConnection() ;
					dbc = null ;

					if ( childsThisMenu.size() > 0 ) {
						if ( user.getBoolean("admin_mode") && text_doc.getInt("sort_order") == 2 ) {
							str += interpretAdminTemplateX(meta.getInt("meta_id"),user,"sort_button.html",m,0,0,0,
								meta,text_doc,templates,child_meta_headlines,child_meta_texts,child_created_dates,
								childs,childs_menu_sort,childs_manual_sort,child_status,texts,urlRefs,urlTexts,
								images,user_roles,all_roles,admin_roles,categories,all_categories,
								all_processings,help_texts,languages,text_types,urlTargets,urlTargetNames,
								child_targets,child_doc_types) ;

							//	str += "<INPUT TYPE=SUBMIT VALUE=\"Registrera Sortering\" NAME=\"reg_sort" + m + "\">&nbsp;" ;
						}
						if ( user.getBoolean("admin_mode") ) {
							str += interpretAdminTemplateX(meta.getInt("meta_id"),user,"archive_del_button.html",0,0,0,0,
								meta,text_doc,templates,child_meta_headlines,child_meta_texts,child_created_dates,
								childs,childs_menu_sort,childs_manual_sort,child_status,texts,urlRefs,urlTexts,
								images,user_roles,all_roles,admin_roles,categories,all_categories,
								all_processings,help_texts,languages,text_types,urlTargets,urlTargetNames,
								child_targets,child_doc_types) ;



							//	 str += "<INPUT TYPE=SUBMIT VALUE=\"Ta bort ikryssad\" NAME=\"del_checked\">&nbsp;" ;
							//	 	 str += "<INPUT TYPE=SUBMIT VALUE=\"Arkivera ikryssad\" NAME=\"archived_checked\">" ;
							str += "\n</FORM>" ;

						}
					}
					if ( ++varFound == varCount )
						return str + "\n" ;

				}
			}

			if ( (i = IMCService.findTag(str,"#servlet_url#")) != -1 ) {
				str = IMCService.replaceTag("#servlet_url#",i,str,m_ServletUrl) ;
				if ( ++varFound == varCount )
					return str + "\n" ;
			}




			if ( (i = IMCService.findTag(str,"#shared#")) != -1 ) {
				str = IMCService.replaceTag("#shared#",i,str,meta.getBoolean("shared") ? " CHECKED" : "" ) ;
				if ( ++varFound == varCount )
					return str + "\n" ;
			}


			if ( (i = IMCService.findTag(str,"#!show_meta#")) != -1 ) {
				str = IMCService.replaceTag("#!show_meta#",i,str,meta.getBoolean("show_meta") ? "" : " CHECKED" );
				if ( ++varFound == varCount )
					return str + "\n" ;
			}


			if ( (i = IMCService.findTag(str,"#show_meta#")) != -1 ) {
				str = IMCService.replaceTag("#show_meta#",i,str,meta.getBoolean("show_meta") ? " CHECKED" : "" );
				if ( ++varFound == varCount )
					return str + "\n" ;
			}


			if ( (i = IMCService.findTag(str,"#sort_button_no#")) != -1 ) {
				str = IMCService.replaceTag("#sort_button_no#",i,str, Integer.toString(index));
				if ( ++varFound == varCount )
					return str + "\n" ;
			}

			if ( (i = IMCService.findTag(str,"#sortBox#")) != -1 ) {
				if ( user.getBoolean("admin_mode") && (index - 1) < childs.size()
					&& text_doc.getInt("sort_order") == 2 ) {
					String tmpStr =  "<input type=text name=" + childs.elementAt(index-1).toString() ;
					tmpStr += " value=\"" + childs_manual_sort.elementAt(index-1).toString() + "\"";
					tmpStr += " size=4 maxlength=4>" ;
					str = IMCService.replaceTag("#sortBox#",i,str,tmpStr) ;
				} else
					str = IMCService.replaceTag("#sortBox#",i,str,"") ;

				if ( ++varFound == varCount )
					return str + "\n" ;
			}




			if ( (i = IMCService.findTag(str,"#template#")) != -1 ) {
				str = IMCService.replaceTag("#template#",i,str,text_doc.getString("template_name")) ;
				if ( ++varFound == varCount )
					return str + "\n" ;
			}



			if ( (i = IMCService.findTag(str,"#simple_name#")) != -1 ) {
				str = IMCService.replaceTag("#simple_name#",i,str,text_doc.getString("simple_name")) ;
				if ( ++varFound == varCount )
					return str + "\n" ;
			}


			if ( (i = IMCService.findTag(str,"#sys_message#")) != -1 ) {
				// get childs for this menu
				String sqlStr  = "select value from sys_data" ;
				sqlStr +=	" where sys_id = 3" ;
				sqlStr += "    and type_id = 3" ;


				DBConnect dbc = new DBConnect(m_conPool,sqlStr) ;
				dbc.getConnection() ;
				dbc.createStatement() ;
				String sys_message = ((Vector)dbc.executeQuery()).elementAt(0).toString() ;
				dbc.clearResultSet() ;
				dbc.closeConnection() ;
				dbc = null ;

				str = IMCService.replaceTag("#sys_message#",i,str,sys_message) ;
				if ( ++varFound == varCount )
					return str + "\n" ;
			}


			if ( (i = IMCService.findTag(str,"#text_template#")) != -1 ) {
				str = IMCService.replaceTag("#text_template#",i,str,text_doc.getString("text_template_name")) ;
				if ( ++varFound == varCount )
					return str + "\n" ;
			}


			if ( (i = IMCService.findTag(str,"#txt#")) != -1 ) {
				if ( index - 1 < texts.size() ) {

					if ( Integer.parseInt(text_types.elementAt(index-1).toString()) != 1 )
						str = IMCService.replaceTag("#txt#",i,str,HTMLConv.removeBR(texts.elementAt(index-1).toString())) ;
					else
						str = IMCService.replaceTag("#txt#",i,str,texts.elementAt(index-1).toString()) ;

				} else
					str = "" ;
				if ( ++varFound == varCount )
					return str + "\n" ;
			}


			if ( (i = IMCService.findTag(str,"#txt_max#")) != -1 ) {
				str = IMCService.replaceTag("#txt_max#",i,str,Integer.toString(index)) ;
				if ( ++varFound == varCount )
					return str + "\n" ;
			}


			if ( (i = IMCService.findTag(str,"#txt_no#")) != -1 ) {
				str = IMCService.replaceTag("#txt_no#",i,str,Integer.toString(index)) ;
				if ( ++varFound == varCount )
					return str + "\n" ;
			}

			if ( (i = IMCService.findTag(str,"#text_type#")) != -1 ) {

				str = IMCService.replaceTag("#text_type#",i,str,text_types.elementAt(index-1).toString()) ;
				if ( ++varFound == varCount )
					return str + "\n" ;
			}



			if ( (i = IMCService.findTag(str,"#userEmail#")) != -1 ) {
				str = IMCService.replaceTag("#userEmail#",i,str, "<A HREF=\"mailto:" + user.getString("email").trim() + "\">" + user.getString("email").trim() + "</A>") ;
				if ( ++varFound == varCount )
					return str + "\n" ;
			}


			if ( (i = IMCService.findTag(str,"#userName#")) != -1 ) {
				str = IMCService.replaceTag("#userName#",i,str,user.getString("first_name").trim() + " " + user.getString("last_name").trim()) ;
				if ( ++varFound == varCount )
					return str + "\n" ;
			}



			if ( (i = IMCService.findTag(str,"#url_no#")) != -1 ) {
				str = IMCService.replaceTag("#url_no#",i,str,Integer.toString(index)) ;
				if ( ++varFound == varCount )
					return str + "\n" ;
			}





			// ************************* ADMIN VARIABLES *****************************
			// #imgRef#
			if ( (i = IMCService.findTag(str,"#imgRef#")) != -1 ) {
				if ( images[index-1] != null ) {

					if ( images[index-1].getString("imgurl").indexOf("/") != -1 )
						str = IMCService.replaceTag("#imgRef#",i,str,images[index-1].getString("imgurl")) ;
					else
						str = IMCService.replaceTag("#imgRef#",i,str,m_ImageFolder + images[index-1].getString("imgurl")) ;
				} else
					return "" ;
				if ( ++varFound == varCount )
					return str + "\n" ;
			}


			// #imgRefLink#
			if ( (i = IMCService.findTag(str,"#imgRefLink#")) != -1 ) {
				if ( images[index-1] != null )
					str = IMCService.replaceTag("#imgRefLink#",i,str,images[index-1].getString("linkurl")) ;
				else
					str += "" ;
				if ( ++varFound == varCount )
					return str + "\n" ;
			}



			// #imgName#
			if ( (i = IMCService.findTag(str,"#imgName#")) != -1 ) {
				if ( images[index-1] != null )
					str = IMCService.replaceTag("#imgName#",i,str,images[index-1].getString("image_name")) ;
				else
					return "" ;

				if ( ++varFound == varCount )
					return str + "\n" ;
			}



			// #imgWidth#
			if ( (i = IMCService.findTag(str,"#imgWidth#")) != -1 ) {
				if ( images[index-1] != null )
					str = IMCService.replaceTag("#imgWidth#",i,str,images[index-1].getString("width")) ;
				else
					return "" ;
				if ( ++varFound == varCount )
					return str + "\n" ;
			}

			// #imgHeight#
			if ( (i = IMCService.findTag(str,"#imgHeight#")) != -1 ) {
				if ( images[index-1] != null )
					str = IMCService.replaceTag("#imgHeight#",i,str,images[index-1].getString("height")) ;
				else
					return "" ;
				if ( ++varFound == varCount )
					return str + "\n" ;
			}



			// #imgBorder#
			if ( (i = IMCService.findTag(str,"#imgBorder#")) != -1 ) {
				if ( images[index-1] != null )
					str = IMCService.replaceTag("#imgBorder#",i,str,images[index-1].getString("border")) ;
				else
					return "" ;
				if ( ++varFound == varCount )
					return str + "\n" ;
			}


			// #imgAltText#
			if ( (i = IMCService.findTag(str,"#imgAltText#")) != -1 ) {
				if ( images[index-1] != null )
					str = IMCService.replaceTag("#imgAltText#",i,str,images[index-1].getString("alt_text")) ;
				else
					return "" ;
				if ( ++varFound == varCount )
					return str + "\n" ;
			}


			// #imgLowScr#
			if ( (i = IMCService.findTag(str,"#imgLowScr#")) != -1 ) {
				if ( images[index-1] != null )
					str = IMCService.replaceTag("#imgLowScr#",i,str,images[index-1].getString("low_scr")) ;
				else
					return "" ;

				if ( ++varFound == varCount )
					return str + "\n" ;
			}


			// #imgVerticalSpace#
			if ( (i = IMCService.findTag(str,"#imgVerticalSpace#")) != -1 ) {
				if ( images[index-1] != null )
					str = IMCService.replaceTag("#imgVerticalSpace#",i,str,images[index-1].getString("v_space")) ;
				else
					return "" ;

				if ( ++varFound == varCount )
					return str + "\n" ;
			}

			// #imgHorizontalSpace#
			if ( (i = IMCService.findTag(str,"#imgHorizontalSpace#")) != -1 ) {
				if ( images[index-1] != null )
					str = IMCService.replaceTag("#imgHorizontalSpace#",i,str,images[index-1].getString("h_space")) ;
				else
					return "" ;

				if ( ++varFound == varCount )
					return str + "\n" ;
			}

			// #imgGetAligns#
			String align[] = { "#none_selected#","#baseline_selected#","#top_selected#",
									 "#middle_selected#","#bottom_selected#","#texttop_selected#",
									 "#absmiddle_selected#","#absbottom_selected#",
									 "#left_selected#","#right_selected#"} ;

			String align_type[] = { "none","baseline","top","middle","bottom","texttop",
										  "absmiddle","absbottom","left","right"} ;

			for ( int a = 0 ; a < 10 ; a++ ) {
				if ( (i = IMCService.findTag(str,align[a])) != -1 ) {
					if ( images[index-1] != null ) {
						tempVec.addElement(str.substring(0,i)) ;
						tempVec.addElement(str.substring(i+align[a].length(),str.length())) ;
						str  = tempVec.elementAt(0).toString() ;

						if ( align_type[a].equals(images[index-1].getString("align")) )
							str += "SELECTED" ;
						else
							str += "" ;


						str += tempVec.elementAt(1).toString() ;
						tempVec.clear() ;
					} else
						return "" ;

					if ( ++varFound == varCount )
						return str + "\n" ;
				}
			}




			// #imgTarget#
			String target[] = { "#top_checked#","#blank_checked#","#parent_checked#",
													 "#self_checked#","#other_checked#"} ;

			String target_type[] = { "_top","_blank","_parent","_self","_other"} ;

			for ( int a = 0 ; a < 5 ; a++ ) {
				if ( (i = IMCService.findTag(str,target[a])) != -1 ) {
					if ( images[index-1] != null ) {
						tempVec.addElement(str.substring(0,i)) ;
						tempVec.addElement(str.substring(i+target[a].length(),str.length())) ;
						str  = tempVec.elementAt(0).toString() ;

						if ( target_type[a].equals(images[index-1].getString("target")) )
							str += "SELECTED" ;
						else
							str += "" ;

						str += tempVec.elementAt(1).toString() ;
						tempVec.clear() ;
					} else
						return "" ;

					if ( ++varFound == varCount )
						return str + "\n" ;
				}
			}



			// #urlRef#
			if ( (i = IMCService.findTag(str,"#urlRef#")) != -1 ) {
				if ( index -1 < urlRefs.size() && urlRefs.size() > 0 )
					str = IMCService.replaceTag("#urlRef#",i,str,urlRefs.elementAt(index-1).toString()) ;
				else
					return "" ;

				if ( ++varFound == varCount )
					return str + "\n" ;
			}



			// #target_name#
			if ( (i = IMCService.findTag(str,"#target_name#")) != -1 ) {
				if ( images[index-1] != null )
					str = IMCService.replaceTag("#target_name#",i,str,images[index-1].getString("target_name")) ;
				else
					return "" ;

				if ( ++varFound == varCount )
					return str + "\n" ;
			}



			// #urlTxt#
			if ( (i = IMCService.findTag(str,"#urlTxt#")) != -1 ) {
				if ( index -1 < urlTexts.size() && urlTexts.size() > 0 )
					str = IMCService.replaceTag("#urlTxt#",i,str,urlTexts.elementAt(index-1).toString()) ;
				else
					return "" ;

				if ( ++varFound == varCount )
					return str + "\n" ;
			}



			// url doc txt
			if ( (i = IMCService.findTag(str,"#url_doc_txt#")) != -1 ) {

				String sqlStr = "" ;
				DBConnect dbc = new DBConnect(m_conPool) ;
				dbc.getConnection() ;
				sqlStr  = "select url_txt from url_docs where meta_id = " + meta.getInt("meta_id") ;
				dbc.setSQLString(sqlStr) ;
				dbc.createStatement() ;
				Vector url_txt = (Vector)dbc.executeQuery() ;
				dbc.clearResultSet() ;

				//close connection
				dbc.closeConnection() ;
				dbc = null ;

				str = IMCService.replaceTag("#url_doc_txt#",i,str,url_txt.elementAt(0).toString()) ;
				if ( ++varFound == varCount )
					return str + "\n" ;

			}


			// url doc ref
			if ( (i = IMCService.findTag(str,"#url_doc_ref#")) != -1 ) {

				String sqlStr = "" ;
				DBConnect dbc = new DBConnect(m_conPool) ;
				dbc.getConnection() ;
				sqlStr  = "select url_ref from url_docs where meta_id = " + meta.getInt("meta_id") ;
				dbc.setSQLString(sqlStr) ;
				dbc.createStatement() ;
				Vector url_txt = (Vector)dbc.executeQuery() ;
				dbc.clearResultSet() ;

				//close connection
				dbc.closeConnection() ;
				dbc = null ;

				str = IMCService.replaceTag("#url_doc_ref#",i,str,url_txt.elementAt(0).toString()) ;
				if ( ++varFound == varCount )
					return str + "\n" ;

			}



			// ************************** END ADMINVARIABLES ****************************



			// NEW
			if ( (i = IMCService.findTag(str,"#getRoles#")) != -1 ) {
				//	 String sqlStr = "select role_id,role_name from roles order by role_id" ;
				// String u_roles[] = this.sqlQuery(sqlStr) ;


				temp_str1 = str.substring(0,i) ;
				temp_str2 = str.substring(i+14,str.length()) ;
				str  = temp_str1 ;
				for ( int j = 0 ; j < all_roles.size() ; j+=2 ) {
					str += "<option value=\"" + all_roles.elementAt(j).toString() + "\">" ;
					str += all_roles.elementAt(j+1).toString() +"\n" ;
				}
				str += temp_str2 ;

				if ( ++varFound == varCount )
					return str + "\n" ;
			}






			if ( (i = IMCService.findTag(str,"#getUserRoles#")) != -1 ) {
				temp_str1 = str.substring(0,i) ;
				temp_str2 = str.substring(i+14,str.length()) ;
				str  = temp_str1 ;
				for ( int j = 0 ; j < user_roles.size() ; j+=2 ) {
					str += "<option value=\"" + user_roles.elementAt(j) + "\">" ;
					str += user_roles.elementAt(j+1).toString() +"\n" ;
				}
				str += temp_str2 ;

				if ( ++varFound == varCount )
					return str + "\n" ;
			}


			if ( (i = IMCService.findTag(str,"#getAdminRoles#")) != -1 ) {
				temp_str1 = str.substring(0,i) ;
				temp_str2 = str.substring(i+15,str.length()) ;
				str  = temp_str1 ;
				for ( int j = 0 ; j < admin_roles.size() ; j+=2 ) {
					str += "<option value=\"" + admin_roles.elementAt(j) + "\">" ;
					str += admin_roles.elementAt(j+1).toString() +"\n" ;
				}
				str += temp_str2 ;

				if ( ++varFound == varCount )
					return str + "\n" ;
			}


			if ( (i = IMCService.findTag(str,"#getCategories#")) != -1 ) {
				temp_str1 = str.substring(0,i) ;
				temp_str2 = str.substring(i+15,str.length()) ;
				str  = temp_str1 ;
				for ( int j = 0 ; j < categories.size() ; j+=2 ) {
					str += "<option value=\"" + categories.elementAt(j) + "\">" ;
					str += categories.elementAt(j+1).toString() +"\n" ;
				}
				str += temp_str2 ;

				if ( ++varFound == varCount )
					return str + "\n" ;
			}


			if ( (i = IMCService.findTag(str,"#getAllProcessings#")) != -1 ) {
				temp_str1 = str.substring(0,i) ;
				temp_str2 = str.substring(i+19,str.length()) ;
				str  = temp_str1 ;
				for ( int j = 0 ; j < all_processings.size() ; j+=2 ) {
					str += "<option value=\"" + all_processings.elementAt(j) + "\">" ;
					str += all_processings.elementAt(j+1).toString() +"\n" ;
				}
				str += temp_str2 ;

				if ( ++varFound == varCount )
					return str + "\n" ;
			}


			if ( (i = IMCService.findTag(str,"#getLanguages#")) != -1 ) {
				temp_str1 = str.substring(0,i) ;
				temp_str2 = str.substring(i+14,str.length()) ;
				str  = temp_str1 ;
				for ( int j = 0 ; j < languages.size() ; j+=2 ) {
					str += "<option value=\"" + languages.elementAt(j) + "\">" ;
					str += languages.elementAt(j+1).toString() +"\n" ;
				}
				str += temp_str2 ;

				if ( ++varFound == varCount )
					return str + "\n" ;
			}



			// get group
//			if ( (i = IMCService.findTag(str,"#group#")) != -1 ) {
//			str = IMCService.replaceTag("#group#",i,str,text_doc.getString("group_name")) ;
//			if ( ++varFound == varCount )
//			return str + "\n" ;

//			}

			String selected_group = "" ;
			int grp_id = user.getTemplateGroup() ;
			if ( grp_id != -1 ) {
				String sqlStr  = "select group_name from templategroups\n" ;
				sqlStr += "where group_id = " + grp_id ;
				DBConnect dbc = new DBConnect(m_conPool) ;
				dbc.getConnection() ;
				dbc.setSQLString(sqlStr);
				dbc.createStatement() ;
				selected_group = ((Vector)dbc.executeQuery()).elementAt(0).toString() ;
				dbc.clearResultSet() ;
				dbc.closeConnection() ;
				dbc = null ;
			} else {
				selected_group = "" ;
			}
			// get templates groups
			if ( (i = IMCService.findTag(str,"#getTemplateGroups#")) != -1 ) {
				//				String sqlStr = "" ;

				DBConnect dbc = new DBConnect(m_conPool) ;
				dbc.getConnection() ;

				// get lang prefix
//					 sqlStr  = "select lang_prefix from users,lang_prefixes\n" ;
//				 sqlStr += "where users.lang_id = lang_prefixes.lang_id\n" ;
//				 sqlStr += "and user_id =" + user.getInt("user_id") ;
//				 dbc.setSQLString(sqlStr);
//				 dbc.createStatement() ;
//				 Vector lang_prefix = (Vector)dbc.executeQuery().clone() ;
//				 dbc.clearResultSet() ;


				// get template_groups
				String sqlStr  = "select group_id,group_name from templategroups\n" ;
				sqlStr += "order by group_name" ;
				dbc.setSQLString(sqlStr);
				dbc.createStatement() ;
				Vector template_groups = (Vector)dbc.executeQuery() ;
				dbc.clearResultSet() ;
				dbc = null ;

				// read group_id from database first time

//								if (user.getTemplateGroup() == -1) {
//				// get group id
//				sqlStr  = "select templategroups.group_id from text_docs,templates,templates_cref,templategroups\n" ;
//				sqlStr += "where text_docs.template_id = templates.template_id\n" ;
//				sqlStr += "and text_docs.meta_id = "  + meta.getString("meta_id") ;
//				sqlStr += "and templates_cref.group_id = templategroups.group_id\n" ;
//				sqlStr += "and templates.template_id = templates_cref.template_id\n" ;
//				dbc.setSQLString(sqlStr) ;
//				dbc.createStatement() ;
//				Vector group_id = (Vector)dbc.executeQuery().clone() ;
//				dbc.clearResultSet() ;
//				if (group_id.size() > 0) {// template not assigned to group
//				grp_id = Integer.parseInt(group_id.elementAt(0).toString()) ;
//				sqlStr  = "select group_name from templategroups\n" ;
//				sqlStr += "where group_id = " + grp_id ;
//				dbc.setSQLString(sqlStr);
//				dbc.createStatement() ;
//				selected_group = ((Vector)dbc.executeQuery().clone()).elementAt(0).toString() ;
//				dbc.clearResultSet() ;
//				dbc.closeConnection() ;
//				dbc = null ;
//				}
//				}
				temp_str1 = str.substring(0,i) ;
				temp_str2 = str.substring(i+19,str.length()) ;
				str  = temp_str1;
				for ( int j = 0 ; j < template_groups.size() ; j+=2 ) {
					if ( (template_groups.elementAt(j+1).toString()).equals(selected_group) )
						str += "<option value=\"" + template_groups.elementAt(j).toString() + "\" selected>" ;
					else
						str += "<option value=\"" + template_groups.elementAt(j).toString() + "\" >" ;


					str += template_groups.elementAt(j+1).toString() +"</option>\n" ;
				}
				str += temp_str2 ;

				if ( ++varFound == varCount )
					return str + "\n" ;

			}

			// get group
			if ( (i = IMCService.findTag(str,"#group#")) != -1 ) {
				str = IMCService.replaceTag("#group#",i,str,
				//text_doc.getString("group_name")
			selected_group) ;

				if ( ++varFound == varCount )
					return str + "\n" ;

			}
			if ( (i = IMCService.findTag(str,"#getTemplates#")) != -1 ) {
				if ( grp_id != -1 ) {
					temp_str1 = str.substring(0,i) ;
					temp_str2 = str.substring(i+14,str.length()) ;
					str  = temp_str1;
					for ( int j = 0 ; j < templates.size() ; j+=2 ) {
						if ( (templates.elementAt(j+1).toString()).equals(text_doc.getString("simple_name")) ) {
							str += "<option value=\"" + templates.elementAt(j).toString() + "\" selected>" ;
						} else {
							str += "<option value=\"" + templates.elementAt(j).toString() + "\" >" ;
						}
						str += templates.elementAt(j+1).toString() +"</option>\n" ;
					}
					str += temp_str2 ;
				}

				if ( ++varFound == varCount )
					return str + "\n" ;
			}

			if ( (i = IMCService.findTag(str,"#getMenuTemplates#")) != -1 ) {
				temp_str1 = str.substring(0,i) ;
				temp_str2 = str.substring(i+18,str.length()) ;
				str  = temp_str1 ;
				for ( int j = 0 ; j < templates.size() ; j+=2 ) {
					if ( (templates.elementAt(j+1).toString()).equals(text_doc.getString("menu_template_name")) )
						str += "<option value=\"" + templates.elementAt(j) + "\" selected>" ;
					else
						str += "<option value=\"" + templates.elementAt(j) + "\" >" ;

					str += templates.elementAt(j+1).toString() +"\n" ;
				}
				str += temp_str2 ;

				if ( ++varFound == varCount )
					return str + "\n" ;
			}


			if ( (i = IMCService.findTag(str,"#getTextTemplates#")) != -1 ) {
				temp_str1 = str.substring(0,i) ;
				temp_str2 = str.substring(i+18,str.length()) ;
				str  = temp_str1 ;
				for ( int j = 0 ; j < templates.size() ; j+=2 ) {
					if ( (templates.elementAt(j+1).toString()).equals(text_doc.getString("text_template_name")) )
						str += "<option value=\"" + templates.elementAt(j) + "\" selected>" ;
					else
						str += "<option value=\"" + templates.elementAt(j) + "\" >" ;

					str += templates.elementAt(j+1).toString() +"\n" ;
				}
				str += temp_str2 ;

				if ( ++varFound == varCount )
					return str + "\n" ;
			}

			// target
			String destination[] = {"_self","_new","_top","_other"} ;
			for ( int j = 0 ; j < destination.length ; j++ ) {
				String tag = "#target" + destination[j] + "#" ;

				if ( (i = IMCService.findTag(str,tag)) != -1 ) {
					temp_str1 = str.substring(0,i) ;

					if ( meta.getString("target").equals(destination[j]) )
						temp_str3 = " CHECKED" ;
					else
						temp_str3 = ""  ;

					if ( temp_str3.length() == 0 )
						if ( !meta.getString("target").equals("_self") &&
							!meta.getString("target").equals("_new") &&
							!meta.getString("target").equals("_top")	&&
							!meta.getString("target").equals("_other") )
							temp_str3 = " CHECKED" ;


						temp_str2 = str.substring(i+tag.length(),str.length()) ;
						str  = temp_str1 ;
						str += temp_str3 ;
						str += temp_str2 ;

						if ( ++varFound == varCount )
							return str + "\n" ;
				}
			}


			// target
			//	String destination[] = {"_self","_new","_top","_other"} ;
			for ( int j = 0 ; j < destination.length ; j++ ) {
				String tag = "#url_target" + destination[j] + "#" ;

				if ( (i = IMCService.findTag(str,tag)) != -1 ) {
					temp_str1 = str.substring(0,i) ;
					if ( index-1 < urlTargetNames.size() ) {
						if ( urlTargets.elementAt(index-1).toString().equals(destination[j]) )
							temp_str3 = " CHECKED" ;
						else
							temp_str3 = ""  ;
					} else
						temp_str3 = ""  ;

					temp_str2 = str.substring(i+tag.length(),str.length()) ;
					str  = temp_str1 ;
					str += temp_str3 ;
					str += temp_str2 ;

					if ( ++varFound == varCount )
						return str + "\n" ;
				}
			}



			if ( (i = IMCService.findTag(str,"#frame_name#")) != -1 ) {
				str = IMCService.replaceTag("#frame_name#",i,str,meta.getString("frame_name")) ;
				if ( ++varFound == varCount )
					return str + "\n" ;
			}




			if ( (i = IMCService.findTag(str,"#url_frame_name#")) != -1 ) {
				if ( index -1 < urlTargetNames.size() && urlTargetNames.size() > 0 )
					str = IMCService.replaceTag("#url_frame_name#",i,str,urlTargetNames.elementAt(index-1).toString()) ;
				if ( ++varFound == varCount )
					return str + "\n" ;
			}


			// sortorder
			for ( int j = 1 ; j <= 4 ; j++ ) {
				if ( (i = IMCService.findTag(str,"#sortOrder" + j +"#")) != -1 ) {
					temp_str1 = str.substring(0,i) ;
					if ( text_doc.getInt("sort_order") == j )
						temp_str3 = " CHECKED" ;
					else
						temp_str3 = ""  ;

					temp_str2 = str.substring(i+12,str.length()) ;
					str  = temp_str1 ;
					str += temp_str3 ;
					str += temp_str2 ;

					if ( ++varFound == varCount )
						return str + "\n" ;
				}
			}


			if ( (i = IMCService.findTag(str,"#docType#")) != -1 ) {
				tempVec.addElement(str.substring(0,i)) ;
				if ( meta.getInt("doc_type") == 1 )
					tempVec.addElement("Meny") ;
				if ( meta.getInt("doc_type") == 2 )
					tempVec.addElement("Text") ;
				tempVec.addElement(str.substring(i+9,str.length())) ;
				str  = tempVec.elementAt(0).toString() ;
				str += tempVec.elementAt(1).toString() ;
				str += tempVec.elementAt(2).toString() ;
				tempVec.clear() ;

				if ( ++varFound == varCount )
					return str + "\n" ;
			}


			if ( (i = IMCService.findTag(str,"#getHelpTexts#")) != -1 ) {
				tempVec.addElement(str.substring(0,i)) ;
				tempVec.addElement(str.substring(i+14,str.length())) ;
				str  = tempVec.elementAt(0).toString() ;
				for ( int j = 0 ; j < help_texts.size() ; j+=2 ) {
					str += "<option value=\"" + help_texts.elementAt(j) + "\">" ;
					str += help_texts.elementAt(j+1).toString() +"\n" ;
				}
				str += tempVec.elementAt(1).toString() ;
				tempVec.clear() ;

				if ( ++varFound == varCount )
					return str + "\n" ;
			}





			if ( (i = IMCService.findTag(str,"#getAllRoles#")) != -1 ) {
				tempVec.addElement(str.substring(0,i)) ;
				tempVec.addElement(str.substring(i+13,str.length())) ;
				str  = tempVec.elementAt(0).toString() ;
				for ( int j = 0 ; j < all_roles.size() ; j+=2 ) {
					str += "<option value=\"" + all_roles.elementAt(j) + "\">" ;
					str += all_roles.elementAt(j+1).toString() +"\n" ;
				}
				str += tempVec.elementAt(1).toString() ;
				tempVec.clear() ;

				if ( ++varFound == varCount )
					return str + "\n" ;
			}


			if ( (i = IMCService.findTag(str,"#user_rights#")) != -1 ) {



				String sqlStr = "" ;
				sqlStr  = "select user_id,login_name from users order by login_name" ;
				DBConnect dbc = new DBConnect(m_conPool) ;
				dbc.getConnection() ;
				dbc.setSQLString(sqlStr) ;
				dbc.createStatement() ;
				Vector all_users = (Vector)dbc.executeQuery() ;
				dbc.clearResultSet() ;

				//close connection
				dbc.closeConnection() ;
				dbc = null ;

				String htmlStr = "" ;
				int counter = 0 ;



				htmlStr += "<TABLE BORDER=0 width=\"100%\" align=\"center\">\n" ;

				htmlStr += "<TH>Läsa" ;

				//htmlStr += "<TH>Arkivera" ;
				htmlStr += "<TH>Skriva" ;

//				  htmlStr += "<TH>Radera Meta" ;
//				  htmlStr += "<TH>Skapa Meta" ;
//				  htmlStr += "<TH>Ändra Skapa Datum" ;
//				  htmlStr += "<TH>Skapa Användare" ;
//				  htmlStr += "<TH>Tilldela rättigheter" ;




				for ( int c = 0 ; c < all_users.size() ; c+=2 ) {

					htmlStr += "<TR>\n" ;

					htmlStr += "<TD>" ;
					htmlStr += "<input type=\"checkbox\" name=\"user_read_" + counter + "\"" ;
					htmlStr += " value=\"" + all_users.elementAt(c).toString() + "\">";
					htmlStr += "</TD>" ;


//					 	  	htmlStr += "<TD>" ;
//					 	   	htmlStr += "&nbsp;<input type=\"checkbox\" name=\"user_archive_" + counter + "\"" ;
//					 	   	htmlStr += " value=\"" + all_users.elementAt(c).toString() + "\">";
//					 	   	htmlStr += "</TD>" ;

					htmlStr += "<TD>" ;
					htmlStr += "&nbsp;<input type=\"checkbox\" name=\"user_write_" + counter + "\"" ;
					htmlStr += " value=\"" + all_users.elementAt(c).toString() + "\">";
					htmlStr += "</TD>" ;


//					 	   	htmlStr += "<TD>" ;
//					 	   	htmlStr += "&nbsp;<input type=\"checkbox\" name=\"user_delete_meta_" + counter + "\"" ;
//					 	   	htmlStr += " value=\"" + all_users.elementAt(c).toString() + "\">";
//					 	   	htmlStr += "</TD>" ;


//					 	   	htmlStr += "<TD>" ;
//					 	   	htmlStr += "&nbsp;<input type=\"checkbox\" name=\"user_create_meta_" + counter + "\"" ;
//					 	   	htmlStr += " value=\"" + all_users.elementAt(c).toString() + "\">";
//					 	   	htmlStr += "</TD>" ;



//					 	   	htmlStr += "<TD>" ;
//					 	   	htmlStr += "&nbsp;<input type=\"checkbox\" name=\"user_change_date_" + counter + "\"" ;
//					 	   	htmlStr += " value=\"" + all_users.elementAt(c).toString() + "\">";
//					 	   	htmlStr += "</TD>" ;



//					 	   	htmlStr += "<TD>" ;
//					 	   	htmlStr += "&nbsp;<input type=\"checkbox\" name=\"user_create_user_" + counter + "\"" ;
//					 	   	htmlStr += " value=\"" + all_users.elementAt(c).toString() + "\">";
//					 	   	htmlStr += "</TD>" ;



//					 	   	htmlStr += "<TD>" ;
//					 	   	htmlStr += "&nbsp;<input type=\"checkbox\" name=\"user_give_rights_" + "\"" ;
//					 	   	htmlStr += " value=\"" + all_users.elementAt(c).toString() + "\">";
//					 	   	htmlStr += "</TD>" ;


					htmlStr += "<TD>" ;
					htmlStr += "&nbsp;<input type=\"hidden\" name=\"user_" + counter +  "\"" ;
					htmlStr += " value=\"" + all_users.elementAt(c).toString() +  "\">";
					htmlStr += "</TD>" ;




					htmlStr += "<TD>" + all_users.elementAt(c+1).toString() + "</TD>" ;
					htmlStr += "</TR>\n" ;
					counter++ ;
				}


				htmlStr += "</TABLE>\n" ;
				htmlStr += "<input type=\"hidden\" name=\"no_of_users\" value=\"" +
					all_users.size() / 2 + "\">\n" ;

				str = IMCService.replaceTag("#user_rights#",i,str,htmlStr) ;

				if ( ++varFound == varCount )
					return str + "\n" ;
			}



			if ( (i = IMCService.findTag(str,"#roles_rights#")) != -1 ) {



				String sqlStr = "" ;
				sqlStr  = "select role_id,role_name from roles where role_id > 0 order by role_name" ;
				DBConnect dbc = new DBConnect(m_conPool) ;
				dbc.getConnection() ;
				dbc.setSQLString(sqlStr) ;
				dbc.createStatement() ;
				Vector roles = (Vector)dbc.executeQuery() ;
				dbc.clearResultSet() ;



				// IS READ SET
				sqlStr  = "select role_id from roles_rights\n" ;
				sqlStr += "where meta_id = " + meta.getInt("meta_id") + "\n" ;
				sqlStr += "and permission_id = 1\n" ;
				//sqlStr += "and role_id = 2" ;
				dbc.setSQLString(sqlStr) ;
				dbc.createStatement() ;
				Vector isReadSet = (Vector)dbc.executeQuery() ;
				dbc.clearResultSet() ;


				// IS WRITE SET
				sqlStr  = "select role_id from roles_rights\n" ;
				sqlStr += "where meta_id = " + meta.getInt("meta_id") + "\n" ;
				sqlStr += "and permission_id = 3\n" ;
				// sqlStr += "and role_id = 1" ;
				dbc.setSQLString(sqlStr) ;
				dbc.createStatement() ;
				Vector isWriteSet = (Vector)dbc.executeQuery() ;
				dbc.clearResultSet() ;


				//close connection
				dbc.closeConnection() ;
				dbc = null ;

				String htmlStr = "" ;
				int counter = 0 ;



				htmlStr += "<TABLE BORDER=0 width=\"100%\" align=\"center\">\n" ;

				htmlStr += "<TH>Läsa" ;

				 //htmlStr += "<TH>Arkivera" ;
				htmlStr += "<TH>Skriva" ;

//				  htmlStr += "<TH>Radera Meta" ;
//				  htmlStr += "<TH>Skapa Meta" ;
//				  htmlStr += "<TH>Ändra Skapa Datum" ;
//				  htmlStr += "<TH>Skapa Användare" ;
//				  htmlStr += "<TH>Tilldela rättigheter" ;




				for ( int c = 0 ; c < roles.size() ; c+=2 ) {

					htmlStr += "<TR>\n" ;

					htmlStr += "<TD>" ;
					htmlStr += "<input type=\"checkbox\" name=\"role_read_" + counter + "\"" ;
					htmlStr += " value=\"" + roles.elementAt(c).toString() + "\"" ;
					if ( isReadSet.size() > 0 ) {
						for ( int k = 0 ; k < isReadSet.size() ; k++ )
							if ( isReadSet.elementAt(k).toString().equals(roles.elementAt(c).toString()) )
								htmlStr += " checked " ;
					}
					htmlStr += ">";
					htmlStr += "</TD>" ;


//					 	  	htmlStr += "<TD>" ;
//					 	   	htmlStr += "&nbsp;<input type=\"checkbox\" name=\"role_archive_" + counter + "\"" ;
//					 	   	htmlStr += " value=\"" + roles.elementAt(c).toString() + "\">";
//					 	   	htmlStr += "</TD>" ;

					htmlStr += "<TD>" ;
					htmlStr += "&nbsp;<input type=\"checkbox\" name=\"role_write_" + counter + "\"" ;
					htmlStr += " value=\"" + roles.elementAt(c).toString() + "\"" ;
					if ( isWriteSet.size() > 0 ) {
						for ( int k = 0 ; k < isWriteSet.size() ; k++ )
							if ( isWriteSet.elementAt(k).toString().equals(roles.elementAt(c).toString()) )
								htmlStr += " checked " ;

					}
					htmlStr += ">";
					htmlStr += "</TD>" ;


//					 	   	htmlStr += "<TD>" ;
//					 	   	htmlStr += "&nbsp;<input type=\"checkbox\" name=\"role_delete_meta_" + counter + "\"" ;
//					 	   	htmlStr += " value=\"" + roles.elementAt(c).toString() + "\">";
//					 	   	htmlStr += "</TD>" ;


//					 	   	htmlStr += "<TD>" ;
//					 	   	htmlStr += "&nbsp;<input type=\"checkbox\" name=\"role_create_meta_" + counter + "\"" ;
//					 	   	htmlStr += " value=\"" + roles.elementAt(c).toString() + "\">";
//					 	   	htmlStr += "</TD>" ;



//					 	   	htmlStr += "<TD>" ;
//					 	   	htmlStr += "&nbsp;<input type=\"checkbox\" name=\"role_change_date_" + counter + "\"" ;
//					 	   	htmlStr += " value=\"" + roles.elementAt(c).toString() + "\">";
//					 	   	htmlStr += "</TD>" ;



//					 	   	htmlStr += "<TD>" ;
//					 	   	htmlStr += "&nbsp;<input type=\"checkbox\" name=\"role_create_user_" + counter + "\"" ;
//					 	   	htmlStr += " value=\"" + roles.elementAt(c).toString() + "\">";
//					 	   	htmlStr += "</TD>" ;



//					 	   	htmlStr += "<TD>" ;
//					 	   	htmlStr += "&nbsp;<input type=\"checkbox\" name=\"role_give_rights_" + "\"" ;
//					 	   	htmlStr += " value=\"" + roles.elementAt(c).toString() + "\">";
//					 	   	htmlStr += "</TD>" ;


					htmlStr += "<TD>" ;
					htmlStr += "&nbsp;<input type=\"hidden\" name=\"role_" + counter +  "\"" ;
					htmlStr += " value=\"" + roles.elementAt(c).toString() +  "\">";
					htmlStr += "</TD>" ;




					htmlStr += "<TD>" + roles.elementAt(c+1).toString() + "</TD>" ;
					htmlStr += "</TR>\n" ;
					counter++ ;
				}


				htmlStr += "</TABLE>\n" ;
				htmlStr += "<input type=\"hidden\" name=\"no_of_roles\" value=\"" +
					roles.size() / 2 + "\">\n" ;

				str = IMCService.replaceTag("#roles_rights#",i,str,htmlStr) ;

				if ( ++varFound == varCount )
					return str + "\n" ;
			}












			if ( (i = IMCService.findTag(str,"#getAllCategories#")) != -1 ) {
				tempVec.addElement(str.substring(0,i)) ;
				tempVec.addElement(str.substring(i+18,str.length())) ;
				str  = tempVec.elementAt(0).toString() ;
				for ( int j = 0 ; j < all_categories.size() ; j+=2 ) {
					str += "<option value=\"" + all_categories.elementAt(j) + "\">" ;
					str += all_categories.elementAt(j+1).toString() +"\n" ;
				}
				str += tempVec.elementAt(1).toString() ;
				tempVec.clear() ;

				if ( ++varFound == varCount )
					return str + "\n" ;
			}









			if ( (i = IMCService.findTag(str,"#getAllUrlRefs#")) != -1 ) {
				tempVec.addElement(str.substring(0,i)) ;
				tempVec.addElement(str.substring(i+15,str.length())) ;
				str  = tempVec.elementAt(0).toString() ;
				for ( int j = 0 ; j < urlRefs.size() ; j+=2 ) {
					str += "<option value=\"" + urlRefs.elementAt(j) + "\">" ;
					str += urlRefs.elementAt(j+1).toString() +"\n" ;
				}
				str += tempVec.elementAt(1).toString() ;
				tempVec.clear() ;

				if ( ++varFound == varCount )
					return str + "\n" ;
			}


			if ( (i = IMCService.findTag(str,"#dateToday#")) != -1 ) {
				java.util.Calendar cal = java.util.Calendar.getInstance() ;

				String year  = Integer.toString(cal.get(Calendar.YEAR)) ;
				int month = Integer.parseInt(Integer.toString(cal.get(Calendar.MONTH))) + 1;
				int day   = Integer.parseInt(Integer.toString(cal.get(Calendar.DAY_OF_MONTH))) ;
				int hour  = Integer.parseInt(Integer.toString(cal.get(Calendar.HOUR))) ;
				int min   = Integer.parseInt(Integer.toString(cal.get(Calendar.MINUTE))) ;


				String dateToDay  = year + "-";
				dateToDay += month < 10 ? "0" + Integer.toString(month) : Integer.toString(month) ;
				dateToDay += "-" ;
				dateToDay += day < 10 ? "0" + Integer.toString(day) : Integer.toString(day)  + " ";


				tempVec.addElement(str.substring(0,i)) ;
				tempVec.addElement(dateToDay) ;
				tempVec.addElement(str.substring(i+11,str.length())) ;
				str  = tempVec.elementAt(0).toString() ;
				str += tempVec.elementAt(1).toString() ;
				str += tempVec.elementAt(2).toString() ;
				tempVec.clear() ;


				if ( ++varFound == varCount )
					return str + "\n" ;
			}


			if ( (i = IMCService.findTag(str,"#date#")) != -1 ) {

				tempVec.addElement(str.substring(0,i)) ;
				tempVec.addElement(ImcodeDate.getDateToDayDelim()) ;
				tempVec.addElement(str.substring(i+6,str.length())) ;
				str  = tempVec.elementAt(0).toString() ;
				str += tempVec.elementAt(1).toString() ;
				str += tempVec.elementAt(2).toString() ;
				tempVec.clear() ;


				if ( ++varFound == varCount )
					return str + "\n" ;
			}


			if ( (i = IMCService.findTag(str,"#time#")) != -1 ) {

				tempVec.addElement(str.substring(0,i)) ;
				tempVec.addElement(ImcodeDate.getTimeNowDelim()) ;
				tempVec.addElement(str.substring(i+6,str.length())) ;
				str  = tempVec.elementAt(0).toString() ;
				str += tempVec.elementAt(1).toString() ;
				str += tempVec.elementAt(2).toString() ;
				tempVec.clear() ;


				if ( ++varFound == varCount )
					return str + "\n" ;
			}




			// #txt#
			boolean text_found = false ;
			for ( int t = 1 ; t < texts.size() + 1 && !text_found ; t++ ) {
				String tempStr = "#txt" + Integer.toString(t)  + "#" ;
				if ( (i = IMCService.findTag(str,tempStr)) != -1 ) {
					text_found = true ;
					tempVec.addElement(str.substring(0,i)) ;
					if ( t - 1 < texts.size() )
						tempVec.addElement(texts.elementAt(t-1)) ;
					else
						tempVec.addElement("&nbsp;") ;

					tempVec.addElement(str.substring(i+tempStr.length(),str.length())) ;
					if ( user.getBoolean("admin_mode") ) {
						str  = tempVec.elementAt(0).toString()  ;
						str += interpretAdminTemplateX(meta.getInt("meta_id"),user,"before_change_text.html",t,0,0,0,
							meta,text_doc,templates,child_meta_headlines,child_meta_texts,child_created_dates,
							childs,childs_menu_sort,childs_manual_sort,child_status,texts,urlRefs,urlTexts,
							images,user_roles,all_roles,admin_roles,categories,all_categories,
							all_processings,help_texts,languages,text_types,urlTargets,urlTargetNames,
							child_targets,child_doc_types) ;
						str += tempVec.elementAt(1).toString() ;
//						str += interpretAdminTemplateX(meta.getInt("meta_id"),user,"after_change_text.html",t,0,0,0,
//						  			meta,text_doc,templates,child_meta_headlines,child_meta_texts,child_created_dates,
//						  			childs,childs_menu_sort,childs_manual_sort,child_status,texts,urlRefs,urlTexts,
//						  			images,user_roles,all_roles,admin_roles,categories,all_categories,
//						  			all_processings,help_texts,languages,text_types,urlTargets,urlTargetNames,
//						  			child_targets) ;
						str += interpretAdminTemplateX(meta.getInt("meta_id"),user,"change_text_url.html",t,0,0,0,
							meta,text_doc,templates,child_meta_headlines,child_meta_texts,child_created_dates,
							childs,childs_menu_sort,childs_manual_sort,child_status,texts,urlRefs,urlTexts,
							images,user_roles,all_roles,admin_roles,categories,all_categories,
							all_processings,help_texts,languages,text_types,urlTargets,urlTargetNames,
							child_targets,child_doc_types) ;
					} else {
						str  = tempVec.elementAt(0).toString() ;
						str += tempVec.elementAt(1).toString() ;
					}

					str += tempVec.elementAt(2).toString() ;
					tempVec.clear() ;

					if ( ++varFound == varCount )
						return str + "\n" ;
				}
			}





			// #img#
			boolean  image_found = false ;

			for ( int j = 1 ; j < images.length  + 1 && !image_found; j++ ) {
				String tempStr = "#img" + j + "#" ;

				if ( (i = IMCService.findTag(str,tempStr)) != -1 ) {

					if ( images[j-1] == null )
						return "" ;


					// 	if (images[j-1].getString("imgurl").length() > 0) {

					// find image start tag
					boolean tag_found = false ;
					int k ;
					for ( k = i ; k >= 0 && !tag_found; k-- )
						if ( str.charAt(k) == '<' ) {
							tempVec.addElement(str.substring(0,k)) ;
							tag_found = true ;
						}

						tempVec.addElement(str.substring(k+1,i)) ;



						// find image end tag
						tag_found = false ;

						for ( k = i ; k <= str.length() && !tag_found; k++ )
							if ( str.charAt(k) == '>' ) {
								tempVec.addElement(str.substring(i+tempStr.length(),k+1)) ;
								tag_found = true ;
							}


							if ( k + 1 < str.length() )
								tempVec.addElement(str.substring(k,str.length())) ;
							else
								tempVec.addElement("") ;



//								m_output.append("#0 " + tempVec.elementAt(0).toString() + "\n") ;
//							 	m_output.append("#1 " + tempVec.elementAt(1).toString() + "\n") ;
//							 	m_output.append("#2 " + tempVec.elementAt(2).toString() + "\n") ;
//							 	m_output.append("#3 " + tempVec.elementAt(3).toString() + "\n") ;


							str = "" ;


							if ( true ) {
								//if(images[j-1].getString("imgurl").length() > 0 ||  user.getBoolean("admin_mode")) {
								str += tempVec.elementAt(0).toString() ;
								// str += "\"" ;


								// admin
								if ( user.getBoolean("admin_mode") )
									str += "<A HREF=\"" + m_ServletUrl + "ChangeImage?meta_id=" + meta.getInt("meta_id") + "&img=" + j + "\">" ;

								if ( images[j-1].getString("linkurl").length() > 0 && !user.getBoolean("admin_mode") ) {
									str += "<A HREF=\"" ;

									str += images[j-1].getString("linkurl") + "\" " ;

									if ( images[j-1].getString("target").equals("_other") )
										str += " target=\""  + images[j-1].getString("target_name")     + "\">" ;
									else
										str += " target=\""  + images[j-1].getString("target")     + "\">" ;
								}


								if ( images[j-1].getString("imgurl").length() > 0 || user.getBoolean("admin_mode") ) {
									str += tempVec.elementAt(1).toString() ;
									str += "\"" ;


									if ( images[j-1].getString("imgurl").indexOf("/") == -1 )
										str += m_ImageFolder ;
								}
							}

							if ( images[j-1].getString("imgurl").length() == 0 && user.getBoolean("admin_mode") )
								//if (user.getBoolean("admin_mode"))
								str += "bild.gif\" border=\"0\"" ;
							else
								if ( images[j-1].getString("imgurl").length() > 0 )
						str += images[j-1].getString("imgurl") + "\"" ;



					//	if (true) {
					if ( images[j-1].getString("imgurl").length() > 0 || user.getBoolean("admin_mode") ) {


						if ( images[j-1].getInt("width") != 0 )
							str += " width=\""   + images[j-1].getString("width")      + "\"" ;


						if ( images[j-1].getInt("height") != 0 )
							str += " height=\""  + images[j-1].getString("height")     + "\"" ;


						if ( images[j-1].getInt("border") != 0 )
							str += " border=\""  + images[j-1].getString("border")     + "\"" ;
						if ( images[j-1].getInt("v_space") != 0 )
							str += " vspace=\""  + images[j-1].getString("v_space")    + "\"" ;
						if ( images[j-1].getInt("h_space") != 0 )
							str += " hspace=\""  + images[j-1].getString("h_space")    + "\"" ;
						if ( images[j-1].getString("image_name").length() > 0 )
							str += " name=\""    + images[j-1].getString("image_name") + "\"" ;
						if ( !images[j-1].getString("align").equals("none") )
							str += " align=\""   + images[j-1].getString("align")      + "\"" ;
						if ( images[j-1].getString("alt_text").length() > 0 )
							str += " alt=\""     + images[j-1].getString("alt_text")   + "\"" ;
						if ( images[j-1].getString("low_scr").length() > 0 )
							str += " lowscr=\""  + images[j-1].getString("low_scr")    + "\"" ;



						str += tempVec.elementAt(2).toString() ;


						if ( images[j-1].getString("linkurl").length() > 0 )
							str += "</A>" ;


					}



					if ( user.getBoolean("admin_mode") )
						str += "</A>" ;

					str += tempVec.elementAt(3).toString() ;

					tempVec.clear() ;
					image_found = true ;

					if ( ++varFound == varCount )
						return str + "\n" ;
				}
			}



			// #url#
			boolean url_found = false ;
			for ( int t = 1 ; t < urlRefs.size() + 1 && !url_found ; t++ ) {
				String tempStr = "#url" + Integer.toString(t)  + "#" ;
				if ( (i = IMCService.findTag(str,tempStr)) != -1 ) {
					url_found = true ;
					tempVec.addElement(str.substring(0,i)) ;
					if ( t - 1 < urlRefs.size() ) {
						if ( urlTargets.elementAt(t-1).toString().equals("_other") )
							tempVec.addElement("<A HREF=\"" + urlRefs.elementAt(t-1) + "\" "
								+  "target=\"" + urlTargetNames.elementAt(t-1).toString() + "\" >"
								+ urlTexts.elementAt(t-1).toString() + "</A>") ;

						else
							tempVec.addElement("<A HREF=\"" + urlRefs.elementAt(t-1) + "\" "
								+  "target=\"" + urlTargets.elementAt(t-1).toString() + "\" >"
								+ urlTexts.elementAt(t-1).toString() + "</A>") ;
					}

					tempVec.addElement("") ;

					tempVec.addElement(str.substring(i+tempStr.length(),str.length())) ;
					if ( user.getBoolean("admin_mode") ) {
						str  = tempVec.elementAt(0).toString()  ;
						str += tempVec.elementAt(1).toString() ;
						str += interpretAdminTemplateX(meta.getInt("meta_id"),user,"change_url_url.html",t,0,0,0,
							meta,text_doc,templates,child_meta_headlines,child_meta_texts,child_created_dates,
							childs,childs_menu_sort,childs_manual_sort,child_status,texts,urlRefs,urlTexts,
							images,user_roles,all_roles,admin_roles,categories,all_categories,
							all_processings,help_texts,languages,text_types,urlTargets,urlTargetNames,
							child_targets,child_doc_types) ;
					} else {
						str  = tempVec.elementAt(0).toString() ;
						str += tempVec.elementAt(1).toString() ;
					}

					str += tempVec.elementAt(2).toString() ;
					tempVec.clear() ;

					if ( ++varFound == varCount )
						return str + "\n" ;
				}
			}




			if ( (i = IMCService.findTag(str,"#currentAdmin#")) != -1 ) {
				boolean found = false ;
				int num_of_roles = ((Vector)user.getObject("user_roles")).size() ;
				for ( int c = 0 ; c < num_of_roles && !found ; c++ )
					if ( (Integer.parseInt((((Vector)user.getObject("user_roles")).elementAt(c)).toString()) ) == 1 )
						found = true ;

					// USER RIGHTS
					String sqlStr  = "select permission_id from user_rights,users\n" ;
					sqlStr += "where user_rights.meta_id = " + meta.getInt("meta_id")  + "\n";
					sqlStr += "and user_rights.user_id = users.user_id\n" ;
					sqlStr += "and users.user_id = " + user.getInt("user_id") ;

					DBConnect dbc = new DBConnect(m_conPool) ;
					dbc.getConnection() ;
					dbc.setSQLString(sqlStr) ;
					dbc.createStatement() ;
					Vector permissions = (Vector)dbc.executeQuery() ;
					dbc.clearResultSet() ;


					boolean user_admin_rights = false ;
					for ( int pm = 0 ; pm < permissions.size() ; pm++ )
						if ( Integer.parseInt(permissions.elementAt(pm).toString()) > 1 )
							user_admin_rights = true ;


						// ROLES RIGHTS
						sqlStr  = "select permission_id from roles_rights,users,user_roles_crossref\n" ;
						sqlStr += "where roles_rights.meta_id = " + meta.getInt("meta_id")  + "\n";
						sqlStr += "and roles_rights.role_id = user_roles_crossref.role_id" + "\n" ;
						sqlStr += "and users.user_id = user_roles_crossref.user_id" + "\n" ;
						sqlStr += "and users.user_id = " + user.getInt("user_id") ;

						dbc.setSQLString(sqlStr) ;
						dbc.createStatement() ;
						Vector role_permissions = (Vector)dbc.executeQuery() ;
						dbc.clearResultSet() ;


						boolean role_admin_rights = false ;
						for ( int pm = 0 ; pm < role_permissions.size() ; pm++ )
							if ( Integer.parseInt(role_permissions.elementAt(pm).toString()) > 1 )
								role_admin_rights = true ;



							// is user superadmin?
							sqlStr  = "select role_id from users,user_roles_crossref\n" ;
							sqlStr += "where users.user_id = user_roles_crossref.user_id\n" ;
							sqlStr += "and user_roles_crossref.role_id = 0\n" ;
							sqlStr += "and users.user_id = " + user.getInt("user_id") ;
							dbc.setSQLString(sqlStr);
							dbc.createStatement() ;
							Vector super_admin_vec = (Vector)dbc.executeQuery() ;
							dbc.clearResultSet() ;
							dbc.closeConnection() ;
							dbc = null ;

							boolean super_admin = false ;
							if ( super_admin_vec.size() > 0 )
								super_admin = true ;




							if (
							// found && (user_admin_rights ||
							( role_admin_rights || super_admin) ) {
								tempVec.addElement(str.substring(0,i)) ;
								tempVec.addElement(str.substring(i+14,str.length())) ;
								str  = tempVec.elementAt(0).toString() ;
								if ( user.getBoolean("admin_mode") )
									str += "\"" + m_ServletUrl + "GetDoc?meta_id=" + meta.getInt("meta_id") + "\"" ;
								else
									str += "\"" + m_ServletUrl + "AdminDoc?meta_id=" + meta.getInt("meta_id") + "\"" ;
								str += tempVec.elementAt(1).toString() ;
								tempVec.clear() ;
							} else
								str = "" ;

							if ( ++varFound == varCount )
								return str + "\n" ;
			}


			// browsercontroll
			if ( meta.getInt("doc_type") == 6 ) {
				String sqlStr  = "select to_meta_id,browser from browser_docs\n" ;
				sqlStr += "where meta_id = " + meta.getInt("meta_id") ;

				DBConnect dbc = new DBConnect(m_conPool) ;
				Vector browser_info = (Vector)dbc.sqlQuery(sqlStr) ;
				dbc.closeConnection() ;
				dbc = null ;


				String browsers[] = {"ns3_pc","ns4_pc","ns5_pc",
							  "msie3_pc","msie4_pc","msie5_pc","other_pc",
							  "ns3_mac","ns4_mac","ns5_mac",
							  "msie3_mac","msie4_mac","msie5_mac","other_mac" } ;

				for ( int j = 0 ; j < browsers.length ; j++ ) {
					String tag =  "#" + browsers[j] + "#" ;


					if ( (i = IMCService.findTag(str,tag)) != -1 ) {
						temp_str1 = str.substring(0,i) ;

						temp_str3 = "" ;
						for ( int b = 0 ; b < browser_info.size() ; b+=2 )
							if ( browsers[j].equals(browser_info.elementAt(b+1).toString().trim()) &&
								Integer.parseInt(browser_info.elementAt(b).toString()) != -1 )
								temp_str3 = browser_info.elementAt(b).toString() ;


							temp_str2 = str.substring(i+tag.length(),str.length()) ;


							str  = temp_str1 ;
							str += temp_str3 ;
							str += temp_str2 ;

							if ( ++varFound == varCount )
								return str + "\n" ;
					}
				}

			}


		}

		str += "\n" ;


		return  str ;
	}




	/**
	* <p>Read a admin template file an interpret and output a HTML String objec
	*/
	public String interpretAdminTemplateX(int meta_id,User user,String admin_template_name,
		int index,int value1,int value2,int value3,
		Table meta,Table text_doc,Vector templates,Vector child_meta_headlines,
		Vector child_meta_texts,Vector child_created_dates,Vector childs,
		Vector childs_menu_sort,Vector childs_manual_sort,Vector child_status,
		Vector texts,Vector urlRefs,Vector urlTexts,Table images[],Vector user_roles,
		Vector all_roles,Vector admin_roles,Vector categories,Vector all_categories,
		Vector all_processings,Vector help_texts,Vector languages,Vector text_types,
		Vector urlTargets , Vector urlTargetNames , Vector child_targets, Vector child_doc_types) {

		Vector v = new Vector() ;
		int num ;
		int current = 0 ;
		String s = "" ;
		String tempStr = "" ;
		String InputFile = "" ;
		String htmlStr = "" ;
		Table language = new Table();
		String sqlStr = "" ;


		//	Vector processings = new Vector() ;

		String template_folder = "" ;
		int current_menu = 0 ;




		// get lang_prefix
		DBConnect dbc = new DBConnect(m_conPool,sqlStr) ;
		dbc.getConnection() ;
		sqlStr  = "select lang_prefix from users,lang_prefixes\n" ;
		sqlStr += "where users.lang_id = lang_prefixes.lang_id\n" ;
		sqlStr += "and users.user_id = " + user.getInt("user_id") ;

		dbc.setSQLString(sqlStr) ;
		dbc.createStatement() ;
		Vector lang_prefix = (Vector)dbc.executeQuery() ;
		dbc.clearResultSet() ;

		// close connection
		dbc.closeConnection() ;
		dbc = null ;



		// template folder
		template_folder  = m_TemplateHome ;
		//template_folder += meta.getString("lang_prefix") + "/admin/" ;
		template_folder += lang_prefix.elementAt(0).toString() + "/admin/" ;



		try {
			String fileLine;
			//you could pass the filename to the servlet as a parameter.
			InputFile = template_folder + admin_template_name ;
			// Get the  file specified by InputFile
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(InputFile)));
			//while there are still lines in the file, get-em.
			while ( (fileLine = br.readLine())!= null ) {
				//add each line to the vector, each line will have a CRLF
				tempStr = fileLine.trim() ;
				if ( tempStr.length() > 0 )
					v.addElement(tempStr);
			}
			//IMPORTANT!!!! - CLOSE THE STREAM!!!!!
			br.close();
		}	catch(IOException e) {
			this.updateLogs("An error occurred reading the file" + e);
		}


		num = v.size();
		int counter = 0 ;


		while ( current < num ) {

			if ( (v.elementAt(current).toString()).startsWith("#txt_max#") ) {
				counter = value1 ;
				current++ ;
			}
			if ( (v.elementAt(current).toString()).startsWith("#img_max#") ) {
				counter = value2 ;
				current++ ;
			}


			if ( (v.elementAt(current).toString()).startsWith("#getDocType#") ) {
				htmlStr += "<BR>\n<INPUT TYPE=HIDDEN name =\"doc_type\" value=" +  index + ">" ;
				htmlStr += "<BR>\n" ;
				current++ ;
			}

			if ( (v.elementAt(current).toString()).startsWith("#DocMenuNo#") ) {
				htmlStr += "<BR>\n<INPUT TYPE=HIDDEN name =\"doc_menu_no\" value=" +  value3 + ">" ;
				htmlStr += "<BR>\n" ;
				current++ ;
			}


			if ( s.startsWith("#menu") )
				current_menu = (int)v.elementAt(current).toString().charAt(5) ;

			if ( (v.elementAt(current).toString()).startsWith("#repeat#") ) {
				current++ ;
				tempStr = "" ;
				while ( !(v.elementAt(current).toString()).startsWith("#end#") )
					tempStr	+= v.elementAt(current++).toString() ;


				boolean end = false ;
				String temp = "" ;

				for ( int i = 1 ; i <= counter; i++ ) {
					temp = findVariable(tempStr,i,user,meta,child_status,
						child_meta_headlines,child_meta_texts,child_created_dates,childs,texts,current_menu,
						user_roles,admin_roles,categories,all_processings,text_doc,help_texts,language,
						languages,templates,all_roles,all_categories,urlRefs,urlTexts,childs_manual_sort,
						childs_menu_sort,images,0,0,text_types,urlTargets,urlTargetNames,
						child_targets,child_doc_types) ;

					htmlStr += temp ;

				}
			} else
				htmlStr += findVariable(v.elementAt(current).toString(),index,user,meta,child_status,
					child_meta_headlines,child_meta_texts,child_created_dates,childs,texts,current_menu,
					user_roles,admin_roles,categories,all_processings,text_doc,help_texts,language,
					languages,templates,all_roles,all_categories,urlRefs,urlTexts,childs_manual_sort,
					childs_menu_sort,images,0,0,text_types,urlTargets,urlTargetNames,
					child_targets,child_doc_types) ;


			current++ ;
		}


		return htmlStr ;
	}


	/**
	* <p>Read a admin template file an interpret and output a HTML String object.
	*/
	public String interpretAdminTemplate(int meta_id,User user,String admin_template_name,
		int index,int value1,int value2,int value3) {
		Vector v = new Vector() ;
		int num ;
		int current = 0 ;
		String s = "" ;
		String tempStr = "" ;
		String InputFile = "" ;
		String htmlStr = "" ;
		Table meta ;
		Table text_doc = new Table() ;
		Table language = new Table();
		Vector texts  = new Vector() ;
		Vector text_types = new Vector() ;
		Vector childs = new Vector() ;
		Vector languages = new Vector() ;
		Vector child_meta_headlines  = new Vector() ;
		Vector child_meta_texts      = new Vector() ;
		Vector child_created_dates   = new Vector() ;
		Vector child_status          = new Vector() ;
		Vector child_targets         = new Vector() ;
		Vector help_texts            = new Vector() ;
		Vector tempVec               = new Vector() ;
		Vector user_roles = new Vector() ;
		Vector admin_roles = new Vector() ;
		Vector categories = new Vector() ;
		Vector processings = new Vector() ;
		String template_name = "" ;
		String template_folder = "" ;
		String sqlStr = "" ;
		Vector templates = new Vector() ;
		Vector all_roles = new Vector() ;
		Vector all_categories = new Vector() ;
		Vector all_processings = new Vector() ;
		Vector urlRefs = new Vector() ;
		Vector urlTexts = new Vector() ;
		Vector urlTargets = new Vector() ;
		Vector urlTargetNames = new Vector() ;

		Vector childs_menu_sort = new Vector() ;
		Vector childs_manual_sort = new Vector() ;
		Table template_data = new Table() ;
		Vector child_doc_types = new Vector() ;

		Vector child_show_meta = new Vector() ;
		Vector child_archive   = new Vector() ;
		Vector child_activate  = new Vector() ;


		Vector child_activate_date = new Vector() ;
		Vector child_archive_date  = new Vector() ;

		Table images[] = new Table[100] ;
		int current_menu = 0 ;
		int no_of_txt = 0 ;
		int no_of_img = 0 ;
		int no_of_url = 0 ;


		// create a db connection an get meta data
		sqlStr = "select * from meta where meta_id = " + meta_id ;
		DBConnect dbc = new DBConnect(m_conPool,sqlStr) ;
		dbc.getConnection() ;
		dbc.createStatement() ;
		meta = new Table(dbc.executeQuery()) ;
		meta.addFieldNames(dbc.getMetaData()) ;
		dbc.clearResultSet() ;


		// quick fix
		if (value2 != 0) {
			sqlStr = "select date_created from meta where meta_id = " + value2 ;
			dbc.setSQLString(sqlStr) ;
			dbc.createStatement() ;
			Vector real_created_date = (Vector)dbc.executeQuery() ;
			dbc.clearResultSet() ;
			meta.addField("date_created", real_created_date.elementAt(0).toString()) ;
		}



		// get lang_prefix
		sqlStr  = "select lang_prefix from users,lang_prefixes\n" ;
		sqlStr += "where users.lang_id = lang_prefixes.lang_id\n" ;
		sqlStr += "and users.user_id = " + user.getInt("user_id") ;

		dbc.setSQLString(sqlStr) ;
		dbc.createStatement() ;
		Vector lang_prefix = (Vector)dbc.executeQuery() ;
		dbc.clearResultSet() ;



		// template folder
		template_folder  = m_TemplateHome ;
		//template_folder += meta.getString("lang_prefix") + "/admin/" ;
		template_folder += lang_prefix.elementAt(0).toString() + "/admin/" ;



		if ( meta.getInt("doc_type") != 5 && meta.getInt("doc_type") != 6 &&
			meta.getInt("doc_type") != 7 && meta.getInt("doc_type") != 8
			&& meta.getInt("doc_type") < 100 ) {
			// get text_doc field data
			sqlStr  = "select meta_id,template_name,sort_order,group_name,simple_name from text_docs,templates,templategroups,templates_cref\n" ;
			sqlStr += "where meta_id = " + meta.getInt("meta_id") + "\n" ;
			sqlStr += "and text_docs.template_id = templates.template_id\n" ;
			sqlStr += "and templates_cref.group_id = templategroups.group_id\n" ;
			sqlStr += "and templates.template_id = templates_cref.template_id\n" ;

			dbc.setSQLString(sqlStr) ;
			dbc.createStatement() ;
			text_doc = new Table(dbc.executeQuery()) ;
			text_doc.addFieldNames(dbc.getMetaData()) ;
			dbc.clearResultSet() ;


			// get text,image and url max
			sqlStr  = "select no_of_txt,no_of_img,no_of_url from templates\n" ;
			sqlStr += "where template_name = '" + text_doc.getString("template_name") + "'";
			dbc.setSQLString(sqlStr) ;
			dbc.createStatement() ;
			template_data = new Table(dbc.executeQuery()) ;
			template_data.addFieldNames(dbc.getMetaData()) ;
			dbc.clearResultSet() ;




			// read group_id from database first time
			int grp_id = user.getTemplateGroup() ;

			if (user.getTemplateGroup() == -1) {
				// get group id
				sqlStr  = "select templategroups.group_id from text_docs,templates,templates_cref,templategroups\n" ;
				sqlStr += "where text_docs.template_id = templates.template_id\n" ;
				sqlStr += "and text_docs.meta_id = "  + meta_id ;
				sqlStr += "and templates_cref.group_id = templategroups.group_id\n" ;
				sqlStr += "and templates.template_id = templates_cref.template_id\n" ;
				dbc.setSQLString(sqlStr) ;
				dbc.createStatement() ;
				Vector group_id = (Vector)dbc.executeQuery() ;
				dbc.clearResultSet() ;
				grp_id = Integer.parseInt(group_id.elementAt(0).toString()) ;
			}




			// get templates
			if ( grp_id != 0) {
				sqlStr  = "select templates.template_id,simple_name from templates,templategroups,templates_cref\n" ;
				sqlStr += "where templategroups.group_id = " + grp_id + "\n";
				sqlStr += "and templategroups.group_id = templates_cref.group_id\n" ;
				sqlStr += "and templates.template_id = templates_cref.template_id\n" ;
			} else {
				sqlStr  = "select templates.template_id,simple_name from templates\n" ;
			}
			sqlStr += "order by templates.template_id" ;
			dbc.setSQLString(sqlStr) ;
			dbc.createStatement() ;
			templates = (Vector)dbc.executeQuery() ;
			dbc.clearResultSet() ;


			// get template name
			template_name    = text_doc.getString("template_name") ;
			no_of_txt        = template_data.getInt("no_of_txt") ;
			no_of_img        = template_data.getInt("no_of_img") ;
			no_of_url        = template_data.getInt("no_of_url") ;



			// is user superadmin?
			sqlStr  = "select role_id from users,user_roles_crossref\n" ;
			sqlStr += "where users.user_id = user_roles_crossref.user_id\n" ;
			sqlStr += "and user_roles_crossref.role_id = 0\n" ;
			sqlStr += "and users.user_id = " + user.getInt("user_id") ;
			dbc.setSQLString(sqlStr);
			dbc.createStatement() ;
			Vector super_admin_vec = (Vector)dbc.executeQuery() ;
			dbc.clearResultSet() ;

			boolean super_admin = false ;
			if ( super_admin_vec.size() > 0 )
				super_admin = true ;




			// get child_list
			sqlStr  = "select menu_sort,manual_sort_order,date_created," ;
			sqlStr += "to_meta_id,status_id,target,show_meta," ;
			sqlStr += "meta.archive,meta.activate,meta.meta_headline,meta.meta_text," ;
			sqlStr += "meta.activated_date + ' ' + activated_time," ;
			sqlStr += "meta.archived_date + ' ' + archived_time,doc_type\n" ;
			sqlStr += "from meta,childs\n" ;
			sqlStr += "where meta.meta_id = childs.to_meta_id and childs.meta_id = " ;
			sqlStr += meta.getInt("meta_id") + "\n" ;


			if ( text_doc.getInt("sort_order") == 1 )
				sqlStr += " order by menu_sort,meta.meta_headline\n" ;
			if ( text_doc.getInt("sort_order") == 2 )
				sqlStr += " order by menu_sort,childs.manual_sort_order DESC\n" ;
			if ( text_doc.getInt("sort_order") == 3 )
				sqlStr += " order by menu_sort,meta.date_created DESC\n" ;

			dbc.setSQLString(sqlStr);
			dbc.createStatement() ;
			Vector child_list = (Vector)dbc.executeQuery() ;
			dbc.clearResultSet() ;



			// get child roles_rights
			sqlStr  = "select role_id,roles_rights.meta_id,permission_id from roles_rights,childs\n" ;
			sqlStr += "where roles_rights.meta_id = childs.to_meta_id\n" ;
			sqlStr += "and childs.meta_id = " + meta.getInt("meta_id") + "\n" ;
			dbc.setSQLString(sqlStr);
			dbc.createStatement() ;
			Vector child_permissions = (Vector)dbc.executeQuery() ;
			dbc.clearResultSet() ;


			// get role for current user
			sqlStr = "select role_id from user_roles_crossref where user_id = " + user.getInt("user_id") ;
			dbc.setSQLString(sqlStr);
			dbc.createStatement() ;
			Vector current_roles = (Vector)dbc.executeQuery() ;
			dbc.clearResultSet() ;


			// select childs with right permission,attribute etc
			int number_of_childs = child_list.size() / 14 ;
			Collator collator = Collator.getInstance() ;
			for(int i = 0 ; i < child_list.size() ; i+=14) {


				//find permission
				boolean found = false ;
				boolean read_permission = false ;

				if (!super_admin) {
					for(int p = 0 ; p < child_permissions.size() && !found ; p+=3) {
						if (child_permissions.elementAt(p+1).toString().equals(child_list.elementAt(i+3).toString())) {


							for(int r = 0 ; r < current_roles.size() ; r++) {
								if (child_permissions.elementAt(p).toString().equals(current_roles.elementAt(r).toString()) &&
									Integer.parseInt(child_permissions.elementAt(p+2).toString()) > 0) {
									read_permission = found = true ;

								}
							}
						}
					}
				} else
					read_permission = true ;



				String activate_date   = Util.convertDate(child_list.elementAt(i+11).toString(),false) ;
				String date_now        = Util.convertDate(ImcodeDate.getFullDateDelim(),false) ;
				String archive_date    = Util.convertDate(child_list.elementAt(i+12).toString(),true) ;





				boolean show_not_active = false ;
				boolean active = false ;
				boolean archive_now = false ;
				boolean show_meta = false ;

				if (child_list.elementAt(i+6).toString().equals("1")) show_meta   = true ;
				if (child_list.elementAt(i+7).toString().equals("1")) archive_now = true ;


				// OMG! What we have here is a work of pure evil!
				if ((collator.compare(activate_date,date_now) >= 0 || collator.compare(archive_date,date_now) <=0 || archive_now) && user.getBoolean("admin_mode") ) {
					show_not_active = true ;

				}


				//	    m_output.append("Headline=" + child_list.elementAt(i+9).toString() + "\n") ;
				//		m_output.append("Activate_date=" + activate_date + "\n") ;
				//		m_output.append("archive_date=" + archive_date + "\n") ;
				//		m_output.append("date_now=" + date_now + "\n") ;

				if (collator.compare(activate_date,date_now) <= 0 && collator.compare(archive_date,date_now) >=0) {
					active = true ;
				}

				if ((active && (!archive_now || user.getBoolean("archive_mode")) && read_permission) || show_not_active || show_meta ) {

					childs_menu_sort.addElement(child_list.elementAt(i).toString()) ;
					childs_manual_sort.addElement(child_list.elementAt(i+1).toString()) ;
					child_created_dates.addElement(child_list.elementAt(i+2).toString()) ;
					childs.addElement(child_list.elementAt(i+3).toString()) ;
					child_doc_types.addElement(child_list.elementAt(i+13).toString()) ;
					child_status.addElement(child_list.elementAt(i+4).toString()) ;
					child_targets.addElement(child_list.elementAt(i+5).toString()) ;

					child_show_meta.addElement(child_list.elementAt(i+6).toString()) ;
					child_archive.addElement(child_list.elementAt(i+7).toString()) ;
					child_activate.addElement(child_list.elementAt(i+8).toString()) ;

					if (show_not_active) {
						child_meta_headlines.addElement("<I>" + child_list.elementAt(i+9).toString() + "</I>") ;
					} else if (archive_now && user.getBoolean("archive_mode")) {
						child_meta_headlines.addElement("<I><B>" + child_list.elementAt(i+9).toString() + "</B></I>") ;
					} else {
						child_meta_headlines.addElement(child_list.elementAt(i+9).toString() ) ;
					}


					child_meta_texts.addElement(child_list.elementAt(i+10).toString()) ;
					child_activate_date.addElement(child_list.elementAt(i+11).toString()) ;
					child_archive_date.addElement(child_list.elementAt(i+12).toString()) ;
				}

			}


			// get texts
			sqlStr  = "select text from texts where meta_id=" ;
			sqlStr += meta.getInt("meta_id") ;
			sqlStr += "order by name" ;
			dbc.setSQLString(sqlStr) ;
			dbc.createStatement() ;
			texts = (Vector)dbc.executeQuery() ;
			dbc.clearResultSet() ;

			// get text_types
			sqlStr  = "select type from texts where meta_id=" ;
			sqlStr += meta.getInt("meta_id") ;
			sqlStr += "order by name" ;
			dbc.setSQLString(sqlStr) ;
			dbc.createStatement() ;
			text_types = (Vector)dbc.executeQuery() ;
			dbc.clearResultSet() ;


			// imageref data
			for ( int i = 1 ; i < no_of_img + 1 ; i++ ) {
				sqlStr  = "select * from images where meta_id = " + meta.getInt("meta_id") ;
				sqlStr += " and name = " + i  ;

				dbc.setSQLString(sqlStr) ;
				dbc.createStatement() ;
				tempVec = (Vector)dbc.executeQuery() ;
				if ( tempVec.size() > 0 ) {
					images[i-1] = new Table() ;
					images[i-1].addFieldData(tempVec) ;
					images[i-1].addFieldNames(dbc.getMetaData()) ;
				}

				tempVec.clear() ;
				dbc.clearResultSet() ;
			}

		}	// end if not doc_type = 5,6,7,8 and external

		// get user roles
		/*	sqlStr  = "select roles.role_id,role_name from roles,user_roles where " ;
		sqlStr += "meta_id = " + meta.getInt("meta_id") ;
		sqlStr += " and roles.role_id=user_roles.role_id order by roles.role_id" ;
		dbc.setSQLString(sqlStr) ;
		dbc.createStatement() ;
		user_roles = (Vector)dbc.executeQuery().clone() ;
		dbc.clearResultSet() ; */


		// get all roles
		sqlStr  = "select role_id,role_name from roles where role_id > 0 order by role_name" ;
		dbc.setSQLString(sqlStr) ;
		dbc.createStatement() ;
		all_roles = (Vector)dbc.executeQuery() ;
		dbc.clearResultSet() ;




		// get admin roles
		/*	sqlStr  = "select roles.role_id,role_name from roles,admin_roles where " ;
		sqlStr += "meta_id = " + meta.getInt("meta_id") ;
		sqlStr += " and roles.role_id=admin_roles.role_id order by roles.role_id" ;
		dbc.setSQLString(sqlStr) ;
		dbc.createStatement() ;
		admin_roles = (Vector)dbc.executeQuery().clone() ;
		dbc.clearResultSet() ; */



		// get categories
		sqlStr  = "select " + "categories" + ".category_id,";
		sqlStr += "categories" + ".category_name" ;
		sqlStr += " from meta," + "categories"  ;
		sqlStr += " where meta.category_id = categories"  ;
		sqlStr += ".category_id" ;
		sqlStr += " and meta_id=" + meta.getInt("meta_id") ;
		dbc.setSQLString(sqlStr) ;
		dbc.createStatement() ;
		categories = (Vector)dbc.executeQuery() ;
		dbc.clearResultSet() ;


		// get all categories
		sqlStr   = "select category_id,category_name from categories"  ;
		sqlStr  += " order by category_id" ;
		dbc.setSQLString(sqlStr) ;
		dbc.createStatement() ;
		all_categories = (Vector)dbc.executeQuery() ;
		dbc.clearResultSet() ;



		// get all processings
		sqlStr   = "select processing_id,processing_name from processings" ;
		sqlStr  += " order by processing_id" ;
		dbc.setSQLString(sqlStr) ;
		dbc.createStatement() ;
		all_processings = (Vector)dbc.executeQuery() ;
		dbc.clearResultSet() ;

		// get help texts
		sqlStr   = "select help_text_id,help_text from help_texts" ;
		sqlStr  += " order by help_text_id" ;
		dbc.setSQLString(sqlStr) ;
		dbc.createStatement() ;
		help_texts = (Vector)dbc.executeQuery() ;
		dbc.clearResultSet() ;

		// get languages
		sqlStr   = "select lang_prefix,language from languages"  ;
		dbc.setSQLString(sqlStr) ;
		dbc.createStatement() ;
		languages = (Vector)dbc.executeQuery() ;
		dbc.clearResultSet() ;
		for ( int i = 0 ; i < languages.size() ; i+=2 )
			language.addField(languages.elementAt(i).toString(),languages.elementAt(i+1)) ;



		// close connection
		dbc.closeConnection() ;
		dbc = null ;


		try
	   {
			String fileLine;
			//you could pass the filename to the servlet as a parameter.
			InputFile = template_folder + admin_template_name ;
			// Get the  file specified by InputFile
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(InputFile)));
			//while there are still lines in the file, get-em.
			while ( (fileLine = br.readLine())!= null ) {
				//add each line to the vector, each line will have a CRLF
				tempStr = fileLine.trim() ;
				if ( tempStr.length() > 0 )
					v.addElement(tempStr);
			}

			//IMPORTANT!!!! - CLOSE THE STREAM!!!!!
			br.close();
			}		catch(IOException e)		{
				 this.updateLogs("An error occurred reading the file" + e );
			}


		num = v.size();
		int counter = 0 ;


		while ( current < num ) {



			if ( (v.elementAt(current).toString()).startsWith("#txt_max#") ) {
				counter = no_of_txt ;
				current++ ;
			}
			if ( (v.elementAt(current).toString()).startsWith("#img_max#") ) {
				counter = no_of_img ;
				current++ ;
			}

			int idx = v.elementAt(current).toString().indexOf("#new_meta_id#") ;
			if ( idx != -1 ) {
				htmlStr += IMCService.replaceTag("#new_meta_id#",idx,
					v.elementAt(current).toString(),Integer.toString(value1)) ;
				current++ ;
			}


			idx = v.elementAt(current).toString().indexOf("#getParentMetaId#") ;
			if ( idx != -1 ) {
				htmlStr += IMCService.replaceTag("#getParentMetaId#",idx,
					v.elementAt(current).toString(),Integer.toString(value1)) ;
				current++ ;
			}

			idx = v.elementAt(current).toString().indexOf("#getRealMetaId#") ;
			if ( idx != -1 ) {
				htmlStr += IMCService.replaceTag("#getRealMetaId#",idx,
					v.elementAt(current).toString(),Integer.toString(value2)) ;
				current++ ;
			}



			if ( (v.elementAt(current).toString()).startsWith("#getDocType#") ) {
				htmlStr += "<BR>\n<INPUT TYPE=HIDDEN name=\"doc_type\" value=" +  index + ">" ;
				htmlStr += "<BR>\n" ;
				current++ ;
			}

			if ( (v.elementAt(current).toString()).startsWith("#DocMenuNo#") ) {
				htmlStr += "<BR>\n<INPUT TYPE=HIDDEN name=\"doc_menu_no\" value=" +  value3 + ">" ;
				htmlStr += "<BR>\n" ;
				current++ ;
			}


			if ( s.startsWith("#menu") )
				current_menu = (int)v.elementAt(current).toString().charAt(5) ;

			if ( (v.elementAt(current).toString()).startsWith("#repeat#") ) {
				current++ ;
				tempStr = "" ;
				while ( !(v.elementAt(current).toString()).startsWith("#end#") )
					tempStr	+= v.elementAt(current++).toString() ;


				boolean end = false ;
				String temp = "" ;

				for ( int i = 1 ; i <= counter; i++ ) {
					temp = findVariable(tempStr,i,user,meta,child_status,
						child_meta_headlines,child_meta_texts,child_created_dates,childs,texts,current_menu,
						user_roles,admin_roles,categories,all_processings,text_doc,help_texts,language,
						languages,templates,all_roles,all_categories,urlRefs,urlTexts,childs_manual_sort,
						childs_menu_sort,images,0,0,text_types,urlTargets,urlTargetNames,
						child_targets,child_doc_types) ;

					htmlStr += temp ;

				}
			} else
				htmlStr += findVariable(v.elementAt(current).toString(),index,user,meta,child_status,
					child_meta_headlines,child_meta_texts,child_created_dates,childs,texts,current_menu,
					user_roles,admin_roles,categories,all_processings,text_doc,help_texts,language,
					languages,templates,all_roles,all_categories,urlRefs,urlTexts,childs_manual_sort,
					childs_menu_sort,images,0,0,text_types,urlTargets,urlTargetNames,
					child_targets,child_doc_types) ;


			current++ ;
		}


		return htmlStr ;
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

		this.updateLogs("Text " + txt_no + 	" in  " + "[" + meta_id + "] modified by user: [" +
			user.getString("first_name").trim() + " " + user.getString("last_name").trim() + "]") ;

	}





	/**
	* <p>Save an urlref.
	*/
	public void saveUrl(int meta_id,User user,imcode.server.Table doc) {
		String sqlStr = "" ;

		// create a db connection an get meta data
		DBConnect dbc = new DBConnect(m_conPool) ;
		dbc.getConnection() ;


		// update url
		sqlStr  = "update urls\n" ;
		sqlStr += "set url_ref ='" + doc.getString("url_ref") + "'" ;
		sqlStr += ",url_txt ='" + doc.getString("url_txt") + "'" ;
		sqlStr += ",frame_name ='" + doc.getString("frame_name") + "'" ;
		sqlStr += ",target ='" + doc.getString("destination") + "'" ;
		sqlStr += " where meta_id = " + meta_id ;
		sqlStr += " and name = " + doc.getString("url_no")  ;


		dbc.setSQLString(sqlStr) ;
		dbc.createStatement() ;
		dbc.executeUpdateQuery() ;

		// close connection
		dbc.closeConnection() ;
		dbc = null ;

		this.updateLogs("Url " + doc.getInt("url_no") + 	" in  " + "[" + meta_id + "] modified by user: [" +
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
	* <p>Get number of textfields.
	*/
	public int getNoOfTxt(int meta_id,imcode.server.User user) {
		String sqlStr = "" ;

		DBConnect dbc = new DBConnect(m_conPool) ;
		dbc.getConnection() ;


		// get template
		sqlStr = "select template_id from text_docs where meta_id = " + meta_id ;
		dbc.setSQLString(sqlStr) ;
		dbc.createStatement() ;
		Vector template_id = (Vector)dbc.executeQuery() ;
		dbc.clearResultSet() ;



		// get max text number
		sqlStr  = "select no_of_txt from templates where template_id = " ;
		sqlStr += template_id.elementAt(0).toString()  ;
		dbc.setSQLString(sqlStr) ;
		dbc.createStatement() ;
		Vector vec_max_text_no = (Vector)dbc.executeQuery() ;
		dbc.clearResultSet() ;





		int max_text_no = Integer.parseInt(vec_max_text_no.elementAt(0).toString()) ;

		//close connection
		dbc.closeConnection() ;
		dbc = null ;

		return max_text_no ;


	}


	/**
	* <p>Insert new images.
	*/
	public void insertNewImages(int meta_id,imcode.server.User user,int no_of_img) {
		String sqlStr = "" ;

		DBConnect dbc = new DBConnect(m_conPool) ;
		dbc.getConnection() ;


		// get max image number
		sqlStr = "select count(name) from images where meta_id = " + meta_id ;
		dbc.setSQLString(sqlStr) ;
		dbc.createStatement() ;
		String vec_count_images_no = ((Vector)dbc.executeQuery()).elementAt(0).toString() ;
		dbc.clearResultSet() ;

		int max_images_no = 0 ;
		if (!vec_count_images_no.equals("0")) {
			sqlStr = "select max(name) from images where meta_id = " + meta_id ;
			dbc.setSQLString(sqlStr) ;
			dbc.createStatement() ;
			Vector vec_max_images_no = (Vector)dbc.executeQuery() ;
			dbc.clearResultSet() ;
			max_images_no = Integer.parseInt(vec_max_images_no.elementAt(0).toString()) ;
		}




		// add imageref to database
		for ( int t = max_images_no + 1 ; t < max_images_no + no_of_img + 1 ; t++ ) {
			sqlStr  = "insert into images(meta_id,width,height,border,v_space,h_space," ;
			sqlStr += "name,image_name,target,target_name,align,alt_text,low_scr,imgurl,linkurl)\n" ;
			sqlStr += "values(" + meta_id + ",0,0,0,0,0,"  + t + "," ;
			sqlStr += "'','_self','','_top','',''," ;
			sqlStr +=  "'','')" ;
			dbc.setSQLString(sqlStr) ;
			dbc.createStatement() ;
			dbc.executeUpdateQuery() ;
		}


		//close connection
		dbc.closeConnection() ;
		dbc = null ;

		this.updateLogs("Image data inserted [" + meta_id + "]  by user: [" +
			user.getString("first_name").trim() + " " +
			user.getString("last_name").trim() + "]") ;

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
		String sqlStr = "" ;

		// create a db connection an get meta data
		DBConnect dbc = new DBConnect(m_conPool) ;
		dbc.getConnection() ;

		sqlStr  = "delete from childs where to_meta_id ="  + meta_id   + "\n";
		sqlStr += "delete from childs where meta_id ="  + meta_id   + "\n";

		sqlStr += "delete from text_docs where meta_id ="  + meta_id  + "\n" ;
		sqlStr += "delete from texts where meta_id ="  + meta_id  + "\n" ;
		sqlStr += "delete from images where meta_id ="  + meta_id  + "\n" ;
		sqlStr += "delete from roles_rights where meta_id ="  + meta_id  + "\n" ;
		sqlStr += "delete from user_rights where meta_id ="  + meta_id  + "\n" ;
		sqlStr += "delete from url_docs where meta_id ="  + meta_id  + "\n" ;
		sqlStr += "delete from browser_docs where meta_id ="  + meta_id  + "\n" ;
		sqlStr += "delete from fileupload_docs where meta_id ="  + meta_id  + "\n" ;
		sqlStr += "delete from frameset_docs where meta_id ="  + meta_id  + "\n" ;

		sqlStr += "delete from meta where meta_id ="  + meta_id  + "\n" ;


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
		// 	 m_output.append("sort_no" + sort_no.toString() + "\n");


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
	 * <p>Archive childs for a menu.
	*/
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
	* <p>Save a new browser doc.
	*/
	public void saveNewBrowserDoc(int meta_id,imcode.server.User user,imcode.server.Table doc) {
		String sqlStr = "" ;
		String browsers[] = {"ns3_pc","ns4_pc","ns5_pc","msie3_pc","msie4_pc","msie5_pc","other_pc",
								"ns3_mac","ns4_mac","ns5_mac","msie3_mac","msie4_mac","msie5_mac","other_mac"} ;
		int to_meta_id  ;

		DBConnect dbc = new DBConnect(m_conPool) ;
		dbc.getConnection() ;

		for ( int i = 0 ; i < browsers.length ; i++ ) {
			to_meta_id = doc.getInt(browsers[i]) ;

			//if (to_meta_id != -1)
			sqlStr  = "insert into browser_docs(meta_id,to_meta_id,browser)\n" ;
			sqlStr += "values(" + meta_id + "," + to_meta_id ;
			sqlStr += ",'" + browsers[i] + "')";

			dbc.setSQLString(sqlStr) ;
			dbc.createStatement() ;
			dbc.executeUpdateQuery() ;
			//}
		}

		this.activateChild(meta_id,user) ;

		this.updateLogs("Browser doc created by user: [" +
			user.getString("first_name").trim() + " " +
			user.getString("last_name").trim() + "]") ;

		//close connection
		dbc.closeConnection() ;
		dbc = null ;


	}


	/**
	* <p>Save a browser doc.
	*/
    /*
	public void saveBrowserDoc(int meta_id,imcode.server.User user,imcode.server.Table doc) {
		String sqlStr = "" ;
		String browsers[] = {"ns3_pc","ns4_pc","ns5_pc","msie3_pc","msie4_pc","msie5_pc","other_pc",
								"ns3_mac","ns4_mac","ns5_mac","msie3_mac","msie4_mac","msie5_mac","other_mac"} ;
		String to_meta_id = "" ;

		DBConnect dbc = new DBConnect(m_conPool) ;
		dbc.getConnection() ;


		for ( int i = 0 ; i < browsers.length ; i++ ) {
			to_meta_id = doc.getString(browsers[i]) ;


			if ( to_meta_id != null ) {
				sqlStr  = "update browser_docs\n" ;
				sqlStr += "set to_meta_id = " + Integer.parseInt(to_meta_id) + "\n";
				sqlStr += "where meta_id = " + meta_id + " and " ;
				sqlStr += "browser = '" + browsers[i]  + "'";


				dbc.setSQLString(sqlStr) ;
				dbc.createStatement() ;
				dbc.executeUpdateQuery() ;
			}
		}


		this.updateLogs("Browser doc updated by user: [" +
			user.getString("first_name").trim() + " " +
			user.getString("last_name").trim() + "]") ;

		//close connection
		dbc.closeConnection() ;
		dbc = null ;
	}
    */

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

		this.updateLogs("UrlDoc [" + meta_id + 	"] modified by user: [" +
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

		this.updateLogs("UrlDoc [" + meta_id + 	"] created by user: [" +
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

		this.updateLogs("FramesetDoc [" + meta_id + 	"] created by user: [" +
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

		this.updateLogs("FramesetDoc [" + meta_id + 	"] updated by user: [" +
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

		this.updateLogs("Child [" + meta_id + 	"] removed from " + parent_meta_id +
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

		this.updateLogs("Child [" + meta_id + 	"] activated  " +
			"by user: [" + user.getString("first_name").trim() + " " + user.getString("last_name").trim() + "]") ;


		//close connection
		dbc.closeConnection() ;
		dbc = null ;


	}

	/**
	* <p>InActivate child from child-table.
	*/
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

		this.updateLogs("Child [" + meta_id + 	"] made inactive  " +
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

		String[] foo = new String[variables.size()] ;
		return imcode.util.Parser.parseDoc(htmlStr,(String[])variables.toArray(foo)) ;
	}


	/**
	Parse doc replace variables with data, uses two vectors
	*/
	public String  parseDoc(String htmlStr,java.util.Vector variables,java.util.Vector data) {
		String[] foo = new String[variables.size()] ;
		String[] bar = new String[data.size()] ;
		return imcode.util.Parser.parseDoc(htmlStr,(String[])variables.toArray(foo),(String[])data.toArray(bar)) ;
	}


	/**
	Parse doc replace variables with data , use template
	*/
	public String parseDoc(java.util.Vector variables,String admin_template_name,
		String lang_prefix) {
		int v_start ;
		String temp_str1 = "" ;
		String temp_str2 = "" ;
		String template_folder = "" ;
		String htmlStr = "" ;
		String InputFile = "" ;
		boolean all_not_found = true ;

		// template folder
		template_folder  = m_TemplateHome ;
		template_folder += lang_prefix + "/admin/" ;

		try {
			String fileLine;
			//you could pass the filename to the servlet as a parameter.
			InputFile = template_folder + admin_template_name ;
			// Get the  file specified by InputFile
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(InputFile),"8859_1"));
			//while there are still lines in the file, get-em.
			StringBuffer sb = new StringBuffer () ;
			int tempchar ;
			while ( (tempchar = br.read())!= -1 ) {
				sb.append((char)tempchar) ;
			}
			htmlStr = sb.toString() ;

			//IMPORTANT!!!! - CLOSE THE STREAM!!!!!
			br.close();
		} catch(IOException e) {
			this.updateLogs("An error occurred reading the file" + e );
		}

		if ( variables == null ) {
			return htmlStr ;
		}

		String[] foo = new String[variables.size()] ;
		return imcode.util.Parser.parseDoc(htmlStr,(String[])variables.toArray(foo)) ;
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
	}


	/**
	  checkDocRights
	  */
	public boolean checkDocRights(int meta_id, User user) {
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
	}

	/**
		Checks to see if a user has any permission of a particular set of permissions for a document.
		@param meta_id	The document-id
		@param user		The user
		@param			A bitmap containing the permissions.
	*/
	public boolean checkDocAdminRightsAny (int meta_id, User user, int permission) {
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
				&& set_id == 0 		// User has full permission for this document
				|| (set_id < 3 && ((set & permission) > 0))	// User has at least one of the permissions given.
			) {
			return true ;
		} else {
			return false ;
		}
	}

	/**
		Checks to see if a user has a particular set of permissions for a document.
		@param meta_id      The document-id
		@param user		    The user
		@param permissions	A bitmap containing the permissions.
	*/
	public boolean checkDocAdminRights (int meta_id, User user, int permission) {

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
				&& set_id == 0 		// User has full permission for this document
				|| (set_id < 3 && ((set & permission) == permission))	// User has all the permissions given.
			) {
			return true ;
		} else {
			return false ;
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

		File f = new File(m_TemplateHome  + "/text/" + new_template_id + ".html") ;

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
				System.out.println("*****>>> File: " + fileObj.getName() + "  filedate " + fileDate);
				if (fileObj.exists() && fileDate>date)
				{
					System.out.println("New latest " + fileObj.getName());
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



	// get demotemplates
	public String[] getDemoTemplateList() {
		File demoDir = new File(m_TemplateHome  + "/text/demo/" ) ;

		String file_list[] = demoDir.list() ;

		if (file_list != null) {
			for(int i = 0 ; i < file_list.length ; i++)
				file_list[i] = file_list[i].substring(0,file_list[i].indexOf(".")) ;
		} else {
			return new String[0];

		}

		return file_list;

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
	* 	<p>Return  language. Returns the langprefix from the db. Takes a lang id
		as argument. Will return null if something goes wrong
	*/
	public String getLanguage(String lang_id) {
		return sqlProcedureStr("GetLangPrefixFromId " + lang_id) ;
	}



} // END CLASS IMCService
