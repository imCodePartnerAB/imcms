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
   Main services for the Imcode Net Server.
   Made final, since only a complete and utter moron would want to extend it.
**/
final public class IMCService extends UnicastRemoteObject implements IMCServiceInterface, IMCConstants {
    
    imcode.server.InetPoolManager m_conPool ; // inet pool of connections
    String m_TemplateHome ;           // template home
    String m_IncludePath ;
    int m_DefaultHomePage ;        // default home page
    String m_ServletUrl  ; 			   // servlet url
    String m_ImageFolder ;            // image folder
    String m_Language          = "" ;      // language
    String m_serverName        = "" ;      // servername

    int m_FileCacheSize = 0 ;
    //boolean m_PrintLogToWindow = false ;  // flag - if true -> print log to app. window
    
    SystemData sysData ;
    
    ExternalDocType m_ExDoc[] ;
    String m_SessionCounterDate = "" ;
    int m_SessionCounter = 0 ;
    int m_NoOfTemplates  ;
    Template m_Template[] ;
    
    CacheLRU fileCache = new CacheLRU(m_FileCacheSize) ;
    
    final static Perl5Util    perl5util = new Perl5Util() ;
    final static Perl5Compiler patComp = new Perl5Compiler() ;
    final static PatternCache patCache = new PatternCacheLRU(50, patComp) ;


    final static Pattern OBSOLETE_MENU_PATTERN = patCache.getPattern("[\\r\\n]\\s*menu\\s+no=(\\d+)\\s+rows=(\\d+)\\s+table_col=(\\d+)\\s*",Perl5Compiler.READ_ONLY_MASK) ;
    //                                                                newline     menu    no=123456    rows=123456    table_col=123456

    final static Pattern HASHTAG_PATTERN = patCache.getPattern("#[^#\"<> \\t\\r\\n]+#",Perl5Compiler.READ_ONLY_MASK) ;
    //                                                          # none of the above #

    final static Pattern HASHTAGNUMBER_PATTERN = patCache.getPattern("(\\d+)#$", Perl5Compiler.READ_ONLY_MASK) ;
    //                                                                123456#

    final static Pattern MENU_PATTERN = patCache.getPattern("<\\?imcms:menu(?:\\s+no=\"(\\d+)\")?\\?>(.*?)<\\?\\/imcms:menu\\?>", Perl5Compiler.SINGLELINE_MASK|Perl5Compiler.READ_ONLY_MASK) ;

    final static Pattern MENULOOP_PATTERN = patCache.getPattern("<\\?imcms:menuloop\\?>(.*?)<\\?\\/imcms:menuloop\\?>", Perl5Compiler.SINGLELINE_MASK|Perl5Compiler.READ_ONLY_MASK) ;

    final static Pattern MENUITEM_PATTERN = patCache.getPattern("<\\?imcms:menuitem\\?>(.*?)<\\?\\/imcms:menuitem\\?>", Perl5Compiler.SINGLELINE_MASK|Perl5Compiler.READ_ONLY_MASK) ;

    final static Pattern MENUITEMHIDE_PATTERN = patCache.getPattern("<\\?imcms:menuitemhide\\?>(.*?)<\\?\\/imcms:menuitemhide\\?>", Perl5Compiler.SINGLELINE_MASK|Perl5Compiler.READ_ONLY_MASK) ;

    final static Pattern MENUITEMHIDETAG_PATTERN = patCache.getPattern("<\\?\\/?imcms:menuitemhide\\?>", Perl5Compiler.READ_ONLY_MASK) ;

    final static Pattern IMCMS_TAG_PATTERN = patCache.getPattern("<\\?imcms:(\\w+)(.*?)\\?>", Perl5Compiler.READ_ONLY_MASK) ;
    final static Pattern IMCMS_TAG_ATTRIBUTES_PATTERN = patCache.getPattern("\\s*(\\w+)\\s*=\\s*([\"'])(.*?)\\2", Perl5Compiler.READ_ONLY_MASK) ;

    final static Pattern HTML_PREBODY_PATTERN = patCache.getPattern("^.*?<[Bb][Oo][Dd][Yy].*?>", Perl5Compiler.SINGLELINE_MASK|Perl5Compiler.READ_ONLY_MASK) ;
    final static Pattern HTML_POSTBODY_PATTERN = patCache.getPattern("<\\/[Bb][Oo][Dd][Yy]>.*$", Perl5Compiler.SINGLELINE_MASK|Perl5Compiler.READ_ONLY_MASK) ;

    final static Pattern TR_START_PATTERN = patCache.getPattern("^(\\<tr[^>]*?\\>)",Perl5Compiler.CASE_INSENSITIVE_MASK|Perl5Compiler.READ_ONLY_MASK) ;
    final static Pattern TR_STOP_PATTERN = patCache.getPattern("(\\<\\/tr\\>)\\s*$",Perl5Compiler.CASE_INSENSITIVE_MASK|Perl5Compiler.READ_ONLY_MASK) ;
    final static Pattern TD_START_PATTERN = patCache.getPattern("^(\\<td[^>]*?\\>)",Perl5Compiler.CASE_INSENSITIVE_MASK|Perl5Compiler.READ_ONLY_MASK) ;
    final static Pattern TD_STOP_PATTERN = patCache.getPattern("(\\<\\/td\\>)\\s*$",Perl5Compiler.CASE_INSENSITIVE_MASK|Perl5Compiler.READ_ONLY_MASK) ;

    final static Pattern MENU_NO_PATTERN = patCache.getPattern("#doc_menu_no#",Perl5Compiler.READ_ONLY_MASK) ;

    // OK, so this is simple, ugly, and prone to give a lot of errors.
    // Very good. Very good. Know something? NO SOUP FOR YOU!
    final static Pattern HTML_TAG_PATTERN = patCache.getPattern("<[^>]+?>",Perl5Compiler.READ_ONLY_MASK) ;

    //final static Perl5Substitution EMPHASIZE_SUBSTITUTION = new Perl5Substitution("<b><em><!--emphasized-->$1<!--/emphasized--></em></b>", Perl5Substitution.INTERPOLATE_ALL) ;

    final static Substitution NULL_SUBSTITUTION = new StringSubstitution("") ;

    SimpleDateFormat DATETIMEFORMAT = new SimpleDateFormat("yyyy-MM-ddHH:mm") ;

    SimpleDateFormat SQL_DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd") ;

    Log log = Log.getLog("server") ;

    
    /**
     * <p>Contructs an IMCService object.
     */
    //	public IMCService(ConnectionPool conPool,javax.swing.JTextArea output,String serverName)
    public IMCService(imcode.server.InetPoolManager conPool,Properties props) throws java.rmi.RemoteException {
	super();
	m_conPool    = conPool ;
	    
  	sysData = getSystemDataFromDb() ;
	    
	m_TemplateHome      = props.getProperty("TemplatePath") ;
	log.log(Log.INFO, "TemplatePath: " + m_TemplateHome) ;
	    
	m_IncludePath       = props.getProperty("IncludePath") ;
	log.log(Log.INFO, "IncludePath: " + m_IncludePath) ;
	    
	try {
	    m_FileCacheSize     = Integer.parseInt(props.getProperty("FileCacheSize")) ;
	} catch (NumberFormatException ignored) {
	}
	log.log(Log.INFO, "FileCacheSize: " + m_FileCacheSize) ;
	    
	try {
	    m_DefaultHomePage   = Integer.parseInt(props.getProperty("StartDocument")) ;    //FIXME: Get from DB
	} catch (NumberFormatException ex) {
	    throw new RuntimeException ("No StartDocument given in properties-file.") ;
	}
	log.log(Log.INFO, "StartDocument: " + m_DefaultHomePage) ;
	    
	m_ServletUrl        = props.getProperty("ServletUrl") ; //FIXME: Get from webserver, or get rid of if possible.
	log.log(Log.INFO, "ServletUrl: " + m_ServletUrl) ;
	    
	// FIXME: Get imageurl from webserver somehow. The user-object, perhaps?
	m_ImageFolder       = props.getProperty("ImageUrl") ; //FIXME: Get from webserver, or get rid of if possible.
	log.log(Log.INFO, "ImageUrl: " + m_ImageFolder) ;
	    
	String externalDocTypes  = props.getProperty("ExternalDoctypes") ; //FIXME: Get rid of, if possible.
	log.log(Log.INFO, "ExternalDoctypes: " + externalDocTypes) ;
	    
	m_Language          = props.getProperty("DefaultLanguage") ; //FIXME: Get from DB
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

    public byte[] parsePage (int meta_id, User user, int flags) throws IOException {
	return parsePage(meta_id,user,flags,2) ;
    }

    public byte[] parsePage (int meta_id, User user, int flags, int includelevel) throws IOException {

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
		log.log(Log.ERROR, "parsePage: user "+user_id+" has nonexistent language "+user.getInt("lang_id")) ;
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
		log.log(Log.ERROR, "parsePage: GetUserPermissionset returned null") ;
		return ("GetUserPermissionset returned null").getBytes("8859_1") ;
	    }
	    //log.log(Log.WILD, "Setting permissionstate", null) ;

	    int user_set_id = Integer.parseInt((String)user_permission_set.elementAt(0)) ;
	    int user_perm_set = Integer.parseInt((String)user_permission_set.elementAt(1)) ;
	    int currentdoc_perms = Integer.parseInt((String)user_permission_set.elementAt(2)) ;


	    boolean textmode = false ;
	    boolean imagemode = false ;
	    boolean menumode = false ;
	    boolean templatemode = false ;
	    boolean includemode = false ;

	    if (flags > 0) {

		textmode     = (flags & PERM_DT_TEXT_EDIT_TEXTS)      != 0 && (user_set_id == 0
									       || (user_perm_set & PERM_DT_TEXT_EDIT_TEXTS) != 0) ;
		imagemode    = (flags & PERM_DT_TEXT_EDIT_IMAGES)     != 0 && (user_set_id == 0
									       || (user_perm_set & PERM_DT_TEXT_EDIT_IMAGES) != 0) ;
		menumode     = (flags & PERM_DT_TEXT_EDIT_MENUS)      != 0 && (user_set_id == 0
									       || (user_perm_set & PERM_DT_TEXT_EDIT_MENUS) != 0) ;
		templatemode = (flags & PERM_DT_TEXT_CHANGE_TEMPLATE) != 0 && (user_set_id == 0
									       || (user_perm_set & PERM_DT_TEXT_CHANGE_TEMPLATE) != 0) ;
		includemode  = (flags & PERM_DT_TEXT_EDIT_INCLUDES)   != 0 && (user_set_id == 0
									       || (user_perm_set & PERM_DT_TEXT_EDIT_INCLUDES) != 0 ) ;
	    }

	    dbc.setProcedure("GetIncludes",String.valueOf(meta_id)) ;
	    Vector included_docs = (Vector)dbc.executeProcedure() ;
	    dbc.clearResultSet() ;

	    dbc.setProcedure("GetTextDocData",String.valueOf(meta_id)) ;
	    Vector text_docs = (Vector)dbc.executeProcedure() ;
	    dbc.clearResultSet() ;
	    if ( text_docs == null ) {
		dbc.closeConnection() ;			// Close connection to db.
		log.log(Log.ERROR, "parsePage: GetTextDocData returned null") ;
		return "parsePage: GetTextDocData returned null".getBytes("8859_1") ;
	    }

	    if ( text_docs.size() == 0 ) {
		dbc.closeConnection() ;			// Close connection to db.
		log.log(Log.ERROR, "parsePage: GetTextDocData returned nothing") ;
		return "parsePage: GetTextDocData returned nothing".getBytes("8859_1") ;
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
		log.log(Log.ERROR, "parsePage: GetTexts returned null") ;
		return ("GetTexts returned null").getBytes("8859_1") ;
	    }

	    //log.log(Log.WILD, "Getting images.", null) ;

	    // Get the images from the db
	    // sqlStr = "select '#img'+convert(varchar(5), name)+'#',name,imgurl,linkurl,width,height,border,v_space,h_space,image_name,align,alt_text,low_scr,target,target_name from images where meta_id = " + meta_id ;
	    //	   				0                                    1    2      3       4     5      6      7       8       9          10    11       12      13     14

	    sqlStr = "select date_modified, meta_headline, meta_image from meta where meta_id = " + meta_id ;
	    dbc.setSQLString(sqlStr);
	    Vector meta = (Vector)dbc.executeQuery() ;
	    dbc.clearResultSet() ;

	    if ( meta == null ) {
		dbc.closeConnection() ;			// Close connection to db.
		log.log(Log.ERROR, "parsePage: Query for date_modified returned null") ;
		return ("Query for date_modified returned null").getBytes("8859_1") ;
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
		log.log(Log.ERROR, "parsePage: GetChilds returned null") ;
		return ("GetChilds returned null").getBytes("8859_1") ;
	    }

	    int child_cols = dbc.getColumnCount() ;
	    int child_rows = childs.size() / child_cols ;
	    dbc.clearResultSet() ;

	    dbc.setProcedure("GetImgs",String.valueOf(meta_id)) ;
	    Vector images = (Vector)dbc.executeProcedure() ;
	    if ( images == null ) {
		dbc.closeConnection() ;			// Close connection to db.
		log.log(Log.ERROR, "parsePage: GetImgs returned null") ;
		return ("GetImgs returned null").getBytes("8859_1") ;
	    }

	    dbc.closeConnection() ;			// Close connection to db.

	    String emphasize_string = getCachedFileString(m_TemplateHome + lang_prefix +"/admin/emphasize.html") ;

	    Perl5Matcher patMat = new Perl5Matcher() ;

	    Perl5Substitution emphasize_substitution = new Perl5Substitution(emphasize_string) ;

	    Properties tags = new Properties() ;	// A properties object to hold the results from the db...

	    //log.log(Log.WILD, "Processing texts.", null) ;
	    if ( textmode ) {	// Textmode
		Iterator it = texts.iterator() ;
		while ( it.hasNext() ) {
		    String key = (String)it.next() ;
		    String txt_no = (String)it.next() ;
		    String txt_type = (String)it.next() ;
		    String value = (String)it.next() ;
		    if ( value.length()>0 ) {
			// FIXME: Get imageurl from webserver somehow. The user-object, perhaps?
			value = "<img src=\""
			    + m_ImageFolder
			    + "red.gif\" border=\"0\">&nbsp;"
			    + value
			    + "<a href=\""
			    + m_ServletUrl
			    + "ChangeText?meta_id="
			    + meta_id
			    + "&txt="
			    + txt_no
			    + "&type="
			    + txt_type
			    + "\"><img src=\""
			    + m_ImageFolder
			    + "txt.gif\" border=\"0\"></a>" ;
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

		    if (emp!=null) {
			// for each string to emphasize
			for (int i = 0 ; i < emp.length ; ++i) {
			    value = org.apache.oro.text.regex.Util.substitute(
									      patMat,
									      patCache.getPattern("("+Perl5Compiler.quotemeta(emp[i])+")",Perl5Compiler.CASE_INSENSITIVE_MASK),
									      emphasize_substitution,
									      value,
									      org.apache.oro.text.regex.Util.SUBSTITUTE_ALL
									      ) ;
			}
		    }

		    if ( value.length()>0 ) {
			tags.setProperty(key,value) ;
		    }
		}
	    }

	    //log.log(Log.WILD, "Processing images.", null) ;
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
		StringBuffer value = new StringBuffer (96) ;
		if ( !"".equals(imgurl) ) {
		    if ( !"".equals(linkurl) ) {
			value.append("<a href=\""+linkurl+"\"") ;
			if ( target.equals("_other") ) {
			    value.append(" target=\""+target_name+"\">") ;
			} else if ( !"".equals(target) ) {
			    value.append(" target=\""+target+"\">") ;
			}
		    }

		    value.append("<img src=\""+m_ImageFolder+imgurl+"\"") ; // FIXME: Get imageurl from webserver somehow. The user-object, perhaps?
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
			// FIXME: Get imageurl from webserver somehow. The user-object, perhaps?
			value.append("<a href=\"ChangeImage?meta_id="+meta_id+"&img="+imgnumber+"\"><img src=\""+m_ImageFolder+"txt.gif\" border=\"0\"></a>") ;
		    }
		    tags.setProperty(imgtag,value.toString()) ;
		}
	    }

	    /*
	      OK.. we will now make a LinkedList for the entire page.
	      This LinkedList, menus, will contain one item for each menu on the page.
	      These items will also be instances of LinkedList.
	      These LinkedLists will in turn each hold one Properties for each item in each menu.
	      These Properties will hold the tags, and the corresponding data, that will go in each menuitem.
	    */
	    HashMap menus = new HashMap () ;	// Map to contain all all the menus on the page.
	    LinkedList currentMenu = null ;
	    int old_menu = -1 ;
	    java.util.Date now = new java.util.Date() ;

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
		    archived_date = DATETIMEFORMAT.parse(child_archived_date_time) ;
		} catch ( java.text.ParseException ex ) {
		}

		try {
		    activate_date = DATETIMEFORMAT.parse(child_activated_date_time) ;
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

		    //props.setProperty("#sortBox#",sortBox) ;
		    //props.setProperty("#archiveDelBox#",archiveDelBox) ;

		    if ( "0".equals(child_admin) ) {
			// FIXME: Get imageurl from webserver somehow. The user-object, perhaps?
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

		props.setProperty("#adminStart#",admin_start) ;
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

		props.setProperty("#childMetaImage#",child_meta_image) ;
		props.setProperty("#childMetaHeadline#",child_meta_headline) ;
		props.setProperty("#childMetaText#",child_meta_text) ;
		props.setProperty("#childCreatedDate#",child_date_created) ;

		// Put the data in the proper tags.
		props.setProperty("#/menuitemlink#", "</a>"+admin_stop) ;
		props.setProperty("#menuitemlink#", admin_start+"<a href="+href+">") ;
		props.setProperty("#menuitemheadline#", child_meta_headline) ;
		props.setProperty("#menuitemtext#", child_meta_text) ;
		props.setProperty("#menuitemdatecreated#", child_date_created) ;
		props.setProperty("#menuitemdatemodified#", child_date_modified) ;
		props.setProperty("#menuitemimage#", child_meta_image) ;

		currentMenu.add(props) ;	// Add the Properties for this menuitem to the current menus list.
	    }

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

	    String meta_image = meta.get(2).toString() ;
	    if (!"".equals(meta_image)) {
		meta_image = "<img src=\""+meta_image+"\" border=\"0\">" ;
	    }
	    tags.setProperty("#metaImage#",                         meta_image) ;
	    tags.setProperty("#sys_message#",			sysData.getSystemMessage()) ;
	    tags.setProperty("#servlet_url#",			m_ServletUrl) ;
	    tags.setProperty("#webMaster#",				sysData.getWebMaster()) ;
	    tags.setProperty("#webMasterEmail#",		sysData.getWebMasterAddress()) ;
	    tags.setProperty("#serverMaster#",			sysData.getServerMaster()) ;
	    tags.setProperty("#serverMasterEmail#",		sysData.getServerMasterAddress()) ;

	    tags.setProperty("#addDoc*#","") ;
	    tags.setProperty("#saveSortStart*#","") ;
	    tags.setProperty("#saveSortStop*#","") ;

	    if ( imagemode ) {	// imagemode
		// FIXME: Get imageurl from webserver somehow. The user-object, perhaps?
		tags.setProperty("#img*#",				"<a href=\"ChangeImage?meta_id="+meta_id+"&img=#img_no#\"><img src=\""+m_ImageFolder+"bild.gif\" border=\"0\"><img src=\""+m_ImageFolder+"txt.gif\" border=\"0\"></a>") ;
		numberedtags.setProperty("#img*#","#img_no#") ;
	    }
	    if ( textmode ) {	// Textmode
		// FIXME: Get imageurl from webserver somehow. The user-object, perhaps?
		tags.setProperty("#txt*#",				"<img src=\""+m_ImageFolder+"red.gif\" border=\"0\">&nbsp;<a href=\""+m_ServletUrl+"ChangeText?meta_id="+meta_id+"&txt=#txt_no#&type=1\"><img src=\""+m_ImageFolder+"txt.gif\" border=\"0\"></a>") ;
		numberedtags.setProperty("#txt*#","#txt_no#") ;
	    }

	    // Give the user a row of buttons if he is privileged enough.
	    if ( checkDocAdminRights(meta_id,user) && flags >= 0 ) {
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

	    // Now load the files specified in "toload", and place them in "tags"
	    //System.out.println("Loading template-files.") ;
	    //log.log(Log.WILD,"Loading template-files.",null) ;

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
	    StringBuffer templatebuffer = new StringBuffer(getCachedFileString(m_TemplateHome + "text/" + template_id + ".html")) ;

	    // Check file for tags
	    String template = templatebuffer.toString() ;
	    StringBuffer result = new StringBuffer(template.length()+16384) ; // This value is the amount i expect the document to increase in size.

	    MenuParserSubstitution menuparsersubstitution = new MenuParserSubstitution(menus,menumode,tags) ;
	    HashTagSubstitution hashtagsubstitution = new HashTagSubstitution(tags,numberedtags) ;
	    ImcmsTagSubstitution imcmstagsubstitution = new ImcmsTagSubstitution(user,meta_id,included_docs,includemode,includelevel) ;

	    LinkedList parse = new LinkedList() ;
	    perl5util.split(parse,"/<!-(-\\/?)IMSCRIPT-->/i",template) ;
	    Iterator pit = parse.iterator() ;
	    boolean parsing = false ;
	    //log.log(Log.WILD, "Entering parseloop.") ;

	    // Well. Here we have it. The main parseloop.
	    // The Inner Sanctum of imCMS. Have fun.
	    while ( pit.hasNext() ) {
		// So, let's jump in and out of blocks delimited by <!--IMSCRIPT--> and <!--/IMSCRIPT-->
		String nextbit = (String)pit.next() ;
		if (nextbit.equals("-/")) { // We matched <!--/IMSCRIPT-->
		    parsing = false ;       // So, we're not parsing.
		    continue ;
		} else if (nextbit.equals("-")) { // We matched <!--IMSCRIPT-->
		    parsing = true ;              // So let's get to parsing.
		    continue ;
		}
		if (!parsing) {
		    result.append(nextbit) ;
		    continue ;
		}

		// String nextbit now contains the bit to parse. (Within the imscript-tags.)

		// Parse the new-style menus.
		// Aah... the magic of OO...
		nextbit = org.apache.oro.text.regex.Util.substitute(patMat,MENU_PATTERN, menuparsersubstitution,nextbit,org.apache.oro.text.regex.Util.SUBSTITUTE_ALL) ;

		// Parse the obsolete menus.
		// You know... the ones that suck so bad it isn't even funny anymore...
		// Just look what happens when you have something that isn't properly delimited.
		// Without this, we could get something similar to efficiency into this so-called parser.
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

		// Parse the <?imcms:tags?>
		nextbit = org.apache.oro.text.regex.Util.substitute(patMat,IMCMS_TAG_PATTERN,imcmstagsubstitution,nextbit,org.apache.oro.text.regex.Util.SUBSTITUTE_ALL) ;

		// Parse the hashtags
		nextbit = org.apache.oro.text.regex.Util.substitute(patMat,HASHTAG_PATTERN,hashtagsubstitution,nextbit,org.apache.oro.text.regex.Util.SUBSTITUTE_ALL) ;

		// So, append the result from this loop-iteration to the result.
		result.append(nextbit) ;
	    } // end while (pit.hasNext()) // End of the main parseloop

	    String returnresult = result.toString() ;

	    /*
	      So, it is here i shall have to put my magical markupemphasizing code.
	      First, i'll split the html (returnresult) on html-tags, and then go through every non-tag part and parse it for keywords to emphasize,
	      and then i'll puzzle it together again. Whe-hey. This will be fun. Not to mention fast. Oh yes, siree.
	    */
	    if (emp!=null) {
		StringBuffer emphasized_result = new StringBuffer(returnresult.length()) ;
		PatternMatcherInput emp_input = new PatternMatcherInput(returnresult) ;
		int last_html_offset = 0 ;
		int current_html_offset = 0 ;
		String non_html_tag_string = null ;
		String html_tag_string = null ;
		while (patMat.contains(emp_input,HTML_TAG_PATTERN)) {
		    current_html_offset = emp_input.getMatchBeginOffset() ;
		    non_html_tag_string = result.substring(last_html_offset,current_html_offset) ;
		    last_html_offset = emp_input.getMatchEndOffset() ;
		    html_tag_string = result.substring(current_html_offset,last_html_offset) ;
		    non_html_tag_string = emphasizeString(non_html_tag_string,emp,emphasize_substitution,patMat) ;
		    // for each string to emphasize
		    emphasized_result.append(non_html_tag_string) ;
		    emphasized_result.append(html_tag_string) ;
		}
		non_html_tag_string = result.substring(last_html_offset) ;
		non_html_tag_string = emphasizeString(non_html_tag_string,emp,emphasize_substitution,patMat) ;
		emphasized_result.append(non_html_tag_string) ;
		returnresult = emphasized_result.toString() ;
	    }

	    return returnresult.getBytes("8859_1") ;
	} catch (RuntimeException ex) {
	    log.log(Log.ERROR, "Error occurred during parsing.",ex ) ;
	    return ex.toString().getBytes("8859_1") ;
	}
    }

    private String emphasizeString(String str,
				   String[] emp,
				   Substitution emphasize_substitution,
				   PatternMatcher patMat) {
	for (int i = 0 ; i < emp.length ; ++i) {
	    str = org.apache.oro.text.regex.Util.substitute(patMat,
							    patCache.getPattern("("+Perl5Compiler.quotemeta(emp[i])+")",Perl5Compiler.CASE_INSENSITIVE_MASK),
							    emphasize_substitution,
							    str,
							    org.apache.oro.text.regex.Util.SUBSTITUTE_ALL
							    ) ;
	}
	return str ;
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
		    String qm = Perl5Compiler.quotemeta(numbertag) ; // FIXME: Run quotemeta on them before putting them in numberedtags, instead of doing it every iteration.
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

    /**
       Fetch a file from the cache, if it hasn't changed on disc.
    */
    protected String getCachedFileString(String filename) throws IOException {
	return getCachedFileString(new File(filename)) ;
    }

    /**
       Fetch a file from the cache, if it hasn't changed on disc.
    */
    protected String getCachedFileString(File file) throws IOException {
	
	if (m_FileCacheSize > 0) {
	    Object[] file_and_date = (Object[])(fileCache.getElement(file)) ; // Get the cached file, if any.
	    if (file_and_date == null || file.lastModified() > ((Long)file_and_date[1]).longValue() ) {
		// No (new) file found?
		String temp = loadFile(file).toString() ; // Load it.
		fileCache.addElement(file, new Object[] {temp,new Long(System.currentTimeMillis())}) ;  // Cache it.
		return temp ;
	    }
	    return (String)file_and_date[0] ;
	} else {
	    return loadFile(file).toString() ;
	}
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

    protected class ImcmsTagSubstitution implements Substitution {

	User user ;
	int implicitInclude = 1 ;
	int meta_id ;
	boolean includemode ;
	int includelevel ;

	HashMap included_docs = new HashMap() ;

	/**
	   @param user           The user
	   @param meta_id        The document-id
	   @param included_list  A list of (include-id, included-meta-id, ...)
	   @param includemode    Whether to include the admin-template instead of the included document.
	   @param includelevel   The number of levels of recursion we've gone through.
	**/
	ImcmsTagSubstitution (User user, int meta_id, List included_list, boolean includemode, int includelevel) {
	    this.user = user ;
	    this.meta_id = meta_id ;
	    this.includemode = includemode ;
	    this.includelevel = includelevel ;
	    for (Iterator i = included_list.iterator(); i.hasNext() ;) {
		included_docs.put(i.next(), i.next()) ;
	    }
	}

	public void appendSubstitution( StringBuffer sb, MatchResult matres, int sc, String originalInput, PatternMatcher patMat, Pattern pat) {
	    String tagname = matres.group(1) ;
	    if (!"include".equals(tagname)) {
		sb.append(matres.group(0)) ;
		return ;
	    }
	    String tagattributes = matres.group(2) ;
	    PatternMatcherInput pminput = new PatternMatcherInput(tagattributes) ;
	    int no = 0 ;
	    while(patMat.contains(pminput,IMCMS_TAG_ATTRIBUTES_PATTERN)) {
		MatchResult attribute_matres = patMat.getMatch() ;
		String imcmstagattributename = attribute_matres.group(1) ;
		String imcmstagattributevalue = attribute_matres.group(3) ;
		if ("file".equals(imcmstagattributename)) {
		    try {
			sb.append(getCachedFileString(new File(m_IncludePath, imcmstagattributevalue))) ;
		    }
		    catch (IOException ignored) {}
		    return ;
		} else if ("document".equals(imcmstagattributename)) {
		    try {
			if (includelevel>0) {
			    int included_meta_id = Integer.parseInt(imcmstagattributevalue) ;
			    String document = new String(parsePage(included_meta_id,user,-1,includelevel-1),"8859_1") ;
			    document = org.apache.oro.text.regex.Util.substitute(patMat,HTML_PREBODY_PATTERN,NULL_SUBSTITUTION,document) ;
			    document = org.apache.oro.text.regex.Util.substitute(patMat,HTML_POSTBODY_PATTERN,NULL_SUBSTITUTION,document) ;
			    sb.append(document) ;
			}
		    }
		    catch (NumberFormatException ignored) {}
		    catch (IOException ignored) {}
		    return ;
		} else if ("no".equals(imcmstagattributename)) {
		    try {
			no = Integer.parseInt(imcmstagattributevalue) ;
			break;
		    }
		    catch (NumberFormatException ignored) {}
		}
	    }
	    if (no == 0) {
		no = implicitInclude++ ;
	    }
	    try {
		if (includemode) {
		    String included_meta_id_str = (String)included_docs.get(String.valueOf(no)) ;
		    sb.append(imcode.util.Parser.parseDoc(getCachedFileString(new File(m_TemplateHome, user.getLangPrefix()+"/admin/change_include.html")),
							  new String[] {
							      "#meta_id#",         String.valueOf(meta_id),
							      "#servlet_url#",     m_ServletUrl,
							      "#include_id#",      String.valueOf(no),
							      "#include_meta_id#", included_meta_id_str == null ? "" : included_meta_id_str
							  }
							  )) ;
		} else {
		    if (includelevel>0) {
			int included_meta_id = Integer.parseInt((String)included_docs.get(String.valueOf(no))) ;
			String document = new String(parsePage(included_meta_id,user,-1,includelevel-1),"8859_1") ;
			document = org.apache.oro.text.regex.Util.substitute(patMat,HTML_PREBODY_PATTERN,NULL_SUBSTITUTION,document) ;
			document = org.apache.oro.text.regex.Util.substitute(patMat,HTML_POSTBODY_PATTERN,NULL_SUBSTITUTION,document) ;
			sb.append(document) ;
		    }
		}
	    }
	    catch (NumberFormatException ignored) {}
	    catch (IOException ignored) {}
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
       I sincerely apologize for this,
       but i create this method only to remove the old stupid parser from the main block of the parseloop.
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
	log.log(Log.WILD, "Starting to parse an obsolete menu on offset "+sbindex) ;
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
	log.log(Log.WILD, "Read the "+menu_param[1]+" rows of the menu") ;
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
	    String menubuff_str = "" ;
	    if (menumode) {
		log.log(Log.WILD, "We don't seem to have a menu... got null.") ;
		menubuff_str = "<!-- inserted by imcms --><tr><td>"+getMenuModePrefix(patMat,menu_param[0],tags)+"</td></tr><!-- empty menu --><tr><td>"+getMenuModeSuffix(tags)+"</td></tr><!-- /inserted by imcms -->" ;
	    }
	    sb.replace( menurowsindex, sbindex,menubuff_str) ;
	    return ;
	}
	// Get an iterator over the elements in the current menu
	Iterator menuit = currentMenu.iterator() ;
	StringBuffer menubuff = new StringBuffer() ;
	String menurowstr = menu_rows[0] ;
	// If the "rows"-attribute of this menu is larger than 1, we need the second row too.
	// Note that if there is only one row, we add the #adminStop# after parsing for <tr><td>
	if ( menu_rows.length>1 ) {
	    menurowstr += "<!-- menuitem 2nd row -->#adminStop#"+menu_rows[1]+"<!-- /menuitem 2nd row -->" ;
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
	    log.log(Log.WILD, "Using the menu's own tr.") ;
	    menurowstr = org.apache.oro.text.regex.Util.substitute(patMat,TR_START_PATTERN,NULL_SUBSTITUTION,menurowstr) ;
	}
	if ( patMat.contains(menurowstr,TR_STOP_PATTERN) ) {
	    trstop = patMat.getMatch().group(1) + "<!-- t /tr -->\r\n" ;
	    log.log(Log.WILD, "Using the menu's own /tr.") ;
	    menurowstr = org.apache.oro.text.regex.Util.substitute(patMat,TR_STOP_PATTERN,NULL_SUBSTITUTION,menurowstr) ;
	}
	if ( patMat.contains(menurowstr,TD_START_PATTERN) ) {
	    tdstart = "\r\n<!-- t td -->"+patMat.getMatch().group(1) ;
	    log.log(Log.WILD, "Using the menu's own td.") ;
	    menurowstr = org.apache.oro.text.regex.Util.substitute(patMat,TD_START_PATTERN,NULL_SUBSTITUTION,menurowstr) ;
	}
	if ( patMat.contains(menurowstr,TD_STOP_PATTERN) ) {
	    tdstop = patMat.getMatch().group(1)+"<!-- t /td -->\r\n" ;
	    log.log(Log.WILD, "Using the menu's own /td.") ;
	    menurowstr = org.apache.oro.text.regex.Util.substitute(patMat,TD_STOP_PATTERN,NULL_SUBSTITUTION,menurowstr) ;
	}

	/** End of added 010212 **/
	//// Make sure we add tags for the html-tags for inactive and archived documents,
	//menurowstr = "#adminStart#"+menurowstr ;
	// Add #adminStop# to the end, if there is only one line.
	// Note that if there is more than one line, we do it before
	// all the regexing for <tr><td>
	if ( menu_rows.length==1 ) {
	    menurowstr = "#adminStart#"+menurowstr+"#adminStop#" ;
	} else {
	    menurowstr = "#adminStart#"+menurowstr ;
	}
	final Pattern HASHTAG_PATTERN = patCache.getPattern("#[^#\"<> \\t\\r\\n]+#") ;
	// for each element of the menu...
	log.log(Log.WILD, "Starting to parse the "+currentMenu.size()+" items of the menu." ) ;
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
	    log.log(Log.WILD, "Parsing the individual tags of one menuitem.") ;
	    String menurow = org.apache.oro.text.regex.Util.substitute(patMat,HASHTAG_PATTERN,mapsubstitution,menurowstr,org.apache.oro.text.regex.Util.SUBSTITUTE_ALL) ;

	    menubuff.append(menurow+tdstop) ;    // OK... one row done. Append it to the menubuffer and end the cell.
	    ++rowcount ;    // And, of course... increase the rowcount.
	    if ( rowcount%menu_param[2]==0 ) {	// If next row is a new tablerow...
		menubuff.append(trstop) ;   // append </tr>, or the equivalent from the template.
	    }
	}
	String menubuff_str = menubuff.toString() ;

	if (menumode) {
	    log.log(Log.WILD, "We're in 'menumode'") ;
	    menubuff_str = "<tr><td>"+getMenuModePrefix(patMat,menu_param[0],tags)+"</td></tr><!-- menu -->"+menubuff_str+"<!-- /menu --><tr><td>"+getMenuModeSuffix(tags)+"</td></tr>" ;
	}
	log.log(Log.WILD, "We're finished with this menu."+sbindex) ;
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

		String tempbuffer_filename = lang_prefix + "/admin/adminbuttons/adminbuttons"+doc_type+".html" ;
		String templatebuffer_filename = lang_prefix + "/admin/adminbuttons/adminbuttons.html" ;
		String superadmin_filename = lang_prefix + "/admin/adminbuttons/superadminbutton.html" ;

		tempbuffer = new StringBuffer(getCachedFileString(new File(m_TemplateHome, tempbuffer_filename))) ;
		templatebuffer = new StringBuffer(getCachedFileString(new File(m_TemplateHome, templatebuffer_filename))) ;
		superadmin = new StringBuffer(getCachedFileString(new File(m_TemplateHome, superadmin_filename))) ;

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

    protected StringBuffer loadFile(File file) {
	StringBuffer tempbuffer = new StringBuffer() ;
	try {
	    char[] charbuffer = new char[16384] ;
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
	    String htmlStr = getCachedFileString(new File(m_TemplateHome,lang_prefix+"/admin/"+admin_template_name)) ;
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
		&& set_id == 0 		// User has full permission for this document
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

	    if (set_id == 0 		// User has full permission for this document
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
     * 	<p>Return  language. Returns the langprefix from the db. Takes a lang id
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


} // END CLASS IMCService
