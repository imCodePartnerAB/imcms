package imcode.server.parser ;

import java.io.* ;
import java.util.* ;
import java.text.* ;

import org.apache.oro.text.regex.* ;

import imcode.server.* ;
import imcode.util.* ;
import imcode.util.log.* ;

public class TextDocumentParser implements imcode.server.IMCConstants {
	private final static String CVS_REV = "$Revision$" ;
	private final static String CVS_DATE = "$Date$" ;

    private Log log = Log.getLog("server") ;
    private FileCache fileCache = new FileCache() ;

    private final static org.apache.oro.text.perl.Perl5Util perl5util = new org.apache.oro.text.perl.Perl5Util() ; // Internally synchronized

    private static Pattern HASHTAG_PATTERN  = null ;
    private static Pattern MENU_PATTERN  = null ;
    private static Pattern OBSOLETE_MENU_PATTERN = null ;
    private static Pattern IMCMS_TAG_PATTERN  = null ;
    private static Pattern TR_START_PATTERN  = null ;
    private static Pattern TR_STOP_PATTERN  = null ;
    private static Pattern TD_START_PATTERN  = null ;
    private static Pattern TD_STOP_PATTERN  = null ;
    private static Pattern MENU_NO_PATTERN  = null ;
    private static Pattern HTML_TAG_PATTERN  = null ;

    static {
	Perl5Compiler patComp = new Perl5Compiler() ;
	try {
	    // OK, so this is simple, ugly, and prone to give a lot of errors.
	    // Very good. Very good. Know something? NO SOUP FOR YOU!
	    HTML_TAG_PATTERN = patComp.compile("<[^>]+?>",Perl5Compiler.READ_ONLY_MASK) ;

	    IMCMS_TAG_PATTERN = patComp.compile("<\\?imcms:([-\\w]+)(.*?)\\?>", Perl5Compiler.READ_ONLY_MASK) ;
	    TR_START_PATTERN = patComp.compile("^(\\<tr[^>]*?\\>)",Perl5Compiler.CASE_INSENSITIVE_MASK|Perl5Compiler.READ_ONLY_MASK) ;
	    TR_STOP_PATTERN = patComp.compile("(\\<\\/tr\\>)\\s*$",Perl5Compiler.CASE_INSENSITIVE_MASK|Perl5Compiler.READ_ONLY_MASK) ;
	    TD_START_PATTERN = patComp.compile("^(\\<td[^>]*?\\>)",Perl5Compiler.CASE_INSENSITIVE_MASK|Perl5Compiler.READ_ONLY_MASK) ;
	    TD_STOP_PATTERN = patComp.compile("(\\<\\/td\\>)\\s*$",Perl5Compiler.CASE_INSENSITIVE_MASK|Perl5Compiler.READ_ONLY_MASK) ;
	    MENU_NO_PATTERN = patComp.compile("#doc_menu_no#",Perl5Compiler.READ_ONLY_MASK) ;
	    HASHTAG_PATTERN = patComp.compile("#[^#\"<> \\t\\r\\n]+#",Perl5Compiler.READ_ONLY_MASK) ;
	    MENU_PATTERN = patComp.compile("<\\?imcms:menu(?:\\s+no=\"(\\d+)\")?\\?>(.*?)<\\?\\/imcms:menu\\?>", Perl5Compiler.SINGLELINE_MASK|Perl5Compiler.READ_ONLY_MASK) ;
	    OBSOLETE_MENU_PATTERN = patComp.compile("[\\r\\n]\\s*menu\\s+no=(\\d+)\\s+rows=(\\d+)\\s+table_col=(\\d+)\\s*",Perl5Compiler.READ_ONLY_MASK) ;
	} catch (MalformedPatternException ignored) {
	    // I ignore the exception because i know that these patterns work, and that the exception will never be thrown.
	    Log log = Log.getLog("server") ;
	    log.log(Log.CRITICAL, "Danger, Will Robinson!") ;
	}
    }

    IMCService serverObject ;
    InetPoolManager connPool ;
    File templatePath ;
    File includePath ;
    String imageUrl ;
    String servletUrl ;

    public TextDocumentParser(IMCService serverobject, InetPoolManager connpool, File templatepath, File includepath, String imageurl, String servleturl) {
	this.connPool = connpool ;
	this.templatePath = templatepath ;
	this.includePath = includepath ;
	this.imageUrl = imageurl ;
	this.servletUrl = servleturl ;
	this.serverObject = serverobject ;
    }

    public byte[] parsePage (int meta_id, User user, int flags, String template_name) throws IOException{
		return parsePage(meta_id,user,flags,1,template_name) ;
	}

	public byte[] parsePage (int meta_id, User user, int flags, int includelevel,String template_name) throws IOException{
	try {
	    long totaltime = System.currentTimeMillis() ;
	    String meta_id_str = String.valueOf(meta_id) ;
	    int user_id = user.getInt("user_id") ;
	    String user_id_str = String.valueOf(user_id) ;

	    DBConnect dbc = new DBConnect(connPool) ;
	    dbc.getConnection() ;
	    dbc.createStatement() ;

	    String lang_prefix  = user.getLangPrefix() ;	// Find language

	    String[] sqlAry = {
		meta_id_str,
		user_id_str
	    } ;

	    dbc.setProcedure("GetUserPermissionSet (?,?)",sqlAry) ;
	    Vector user_permission_set = (Vector)dbc.executeProcedure() ;
	    dbc.clearResultSet() ;
	    if ( user_permission_set == null ) {
		dbc.closeConnection() ;			// Close connection to db.
		log.log(Log.ERROR, "parsePage: GetUserPermissionset returned null") ;
		return ("GetUserPermissionset returned null").getBytes("8859_1") ;
	    }

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
		if (template_name != null){
			//lets validate that the template exists before we changes the original one
			dbc.setProcedure("GetTemplateId "+template_name);
			Vector vectT = (Vector)dbc.executeProcedure();
			if (vectT.size() > 0){
				try	{
					int temp_template = Integer.parseInt( (String)vectT.get(0) );
					if(temp_template > 0)
						template_id = temp_template+"";	
				}catch(NumberFormatException nfe){
						//do nothing, we keep the original template 
				}
			}
		}
		
	    String simple_name = (String)text_docs.remove(0) ;
	    int sort_order = Integer.parseInt((String)text_docs.remove(0)) ;
	    String group_id = (String)text_docs.remove(0) ;

	    Vector doc_types_vec = null ;
	    String sqlStr = null ;
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
	    dbc.setProcedure("GetTexts",String.valueOf(meta_id)) ;
	    Vector texts = (Vector)dbc.executeProcedure() ;
	    dbc.clearResultSet() ;

	    if ( texts == null ) {
		dbc.closeConnection() ;			// Close connection to db.
		log.log(Log.ERROR, "parsePage: GetTexts returned null") ;
		return ("GetTexts returned null").getBytes("8859_1") ;
	    }

	    // Get the images from the db
	    // sqlStr = "select '#img'+convert(varchar(5), name)+'#',name,imgurl,linkurl,width,height,border,v_space,h_space,image_name,align,alt_text,low_scr,target,target_name from images where meta_id = " + meta_id ;
	    //					0                    1    2      3       4     5      6      7       8       9          10    11       12      13     14

	    sqlStr = "select date_modified, meta_headline, meta_image from meta where meta_id = " + meta_id ;
	    dbc.setSQLString(sqlStr);
	    Vector meta = (Vector)dbc.executeQuery() ;
	    dbc.clearResultSet() ;

	    if ( meta == null ) {
		dbc.closeConnection() ;			// Close connection to db.
		log.log(Log.ERROR, "parsePage: Query for date_modified returned null") ;
		return ("Query for date_modified returned null").getBytes("8859_1") ;
	    }

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

	    File admintemplate_path = new File(templatePath,  "/" +lang_prefix + "/admin/") ;

	    String emphasize_string = fileCache.getCachedFileString(new File(admintemplate_path, "emphasize.html")) ;

	    Perl5Compiler patComp = new Perl5Compiler() ;
	    Perl5Matcher patMat = new Perl5Matcher() ;

	    Perl5Substitution emphasize_substitution = new Perl5Substitution(emphasize_string) ;

	    Properties tags = new Properties() ;	// A properties object to hold the results from the db...
	    HashMap textMap = new HashMap() ;
	    HashMap imageMap = new HashMap() ;

	    Iterator it = texts.iterator() ;
	    while ( it.hasNext() ) {
		String key = (String)it.next() ;
		String txt_no = (String)it.next() ;
		String txt_type = (String)it.next() ;
		String value = (String)it.next() ;
		if ( textmode ) {	// Textmode
		    if ( value.length()>0 ) {
			value = "<img src=\""
			    + imageUrl
			    + "red.gif\" border=\"0\">&nbsp;"
			    + value
			    + "<a href=\""
			    + servletUrl
			    + "ChangeText?meta_id="
			    + meta_id
			    + "&txt="
			    + txt_no
			    + "&type="
			    + txt_type
			    + "\"><img src=\""
			    + imageUrl
			    + "txt.gif\" border=\"0\"></a>" ;
			tags.setProperty(key,value) ;
			textMap.put(txt_no,value);
		    }
		} else {	// Not Textmode
		    if (emp!=null) {
			value = emphasizeString(value,emp,emphasize_substitution,patMat) ;
		    }
		    
		    if ( value.length()>0 ) {
			tags.setProperty(key,value) ;
			textMap.put(txt_no,value) ;
		    }
		}
	    }

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

		    value.append("<img src=\""+imageUrl+imgurl+"\"") ; // FIXME: Get imageurl from webserver somehow. The user-object, perhaps?
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
			value.append("<a href=\"ChangeImage?meta_id="+meta_id+"&img="+imgnumber+"\"><img src=\""+imageUrl+"txt.gif\" border=\"0\"></a>") ;
		    }
		}
		tags.setProperty(imgtag,value.toString()) ;
		imageMap.put(imgnumber,value.toString()) ;
	    }

	    /*
	      OK.. we will now make a LinkedList for the entire page.
	      This LinkedList, menus, will contain one item for each menu on the page.
	      These items will also be instances of LinkedList.
	      These LinkedLists will in turn each hold one Properties for each item in each menu.
	      These Properties will hold the tags, and the corresponding data, that will go in each menuitem.
	    */
	    HashMap menus = new HashMap () ;	// Map to contain all the menus on the page.
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
		String child_frame_name          = (String)childIt.next() ; // The target fram for this document. Supposed to be replaced by 'target'.
		String child_activated_date_time = (String)childIt.next() ; // The datetime the document is activated.
		String child_archived_date_time  = (String)childIt.next() ; // The datetime the document is archived.
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

		SimpleDateFormat DATETIMEFORMAT = new SimpleDateFormat("yyyy-MM-ddHH:mm") ;

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
			admin_stop+="&nbsp;<a href=\"AdminDoc?meta_id="+child_meta_id+"\"><img src=\""+imageUrl+"txt.gif\" border=\"0\"></a>" ;
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
		
		props.setProperty("#childMetaId#",child_meta_id) ;
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


	    // I need a list of tags that have numbers that need to be parsed in in their data.
	    Properties numberedtags = new Properties () ;

	    // I also need a list of files to load, and their corresponding tag...
	    Properties toload = new Properties () ;

	    // Oh! I need a set of tags to be replaced in the templatefiles we'll load...
	    Properties temptags = new Properties () ;

	    // Put tags and corresponding data in Properties
	    tags.setProperty("#userName#",				user.getString("first_name").trim()+" "+user.getString("last_name").trim()) ;
	    tags.setProperty("#session_counter#",		String.valueOf(serverObject.getSessionCounter())) ;
	    tags.setProperty("#session_counter_date#",	serverObject.getSessionCounterDate()) ;
	    tags.setProperty("#lastDate#",				meta.get(0).toString()) ;
	    tags.setProperty("#metaHeadline#",			meta.get(1).toString()) ;

	    String meta_image = meta.get(2).toString() ;
	    if (!"".equals(meta_image)) {
		meta_image = "<img src=\""+meta_image+"\" border=\"0\">" ;
	    }
	    tags.setProperty("#metaImage#",                         meta_image) ;
	    tags.setProperty("#sys_message#",			serverObject.getSystemData().getSystemMessage()) ;
	    tags.setProperty("#servlet_url#",			servletUrl) ;
	    tags.setProperty("#webMaster#",				serverObject.getSystemData().getWebMaster()) ;
	    tags.setProperty("#webMasterEmail#",		serverObject.getSystemData().getWebMasterAddress()) ;
	    tags.setProperty("#serverMaster#",			serverObject.getSystemData().getServerMaster()) ;
	    tags.setProperty("#serverMasterEmail#",		serverObject.getSystemData().getServerMasterAddress()) ;

	    tags.setProperty("#addDoc*#","") ;
	    tags.setProperty("#saveSortStart*#","") ;
	    tags.setProperty("#saveSortStop*#","") ;

	    if ( imagemode ) {	// imagemode
		// FIXME: Get imageurl from webserver somehow. The user-object, perhaps?
		tags.setProperty("#img*#",				"<a href=\"ChangeImage?meta_id="+meta_id+"&img=#img_no#\"><img src=\""+imageUrl+"bild.gif\" border=\"0\"><img src=\""+imageUrl+"txt.gif\" border=\"0\"></a>") ;
		numberedtags.setProperty("#img*#","#img_no#") ;
	    }
	    if ( textmode ) {	// Textmode
		// FIXME: Get imageurl from webserver somehow. The user-object, perhaps?
		tags.setProperty("#txt*#",				"<img src=\""+imageUrl+"red.gif\" border=\"0\">&nbsp;<a href=\""+servletUrl+"ChangeText?meta_id="+meta_id+"&txt=#txt_no#&type=1\"><img src=\""+imageUrl+"txt.gif\" border=\"0\"></a>") ;
		numberedtags.setProperty("#txt*#","#txt_no#") ;
	    }

	    // Give the user a row of buttons if he is privileged enough.
	    if ( serverObject.checkDocAdminRights(meta_id,user) && flags >= 0 ) {
		tags.setProperty("#adminMode#",serverObject.getMenuButtons(meta_id,user)) ;
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
		toload.setProperty("#changePage#",(new File(admintemplate_path,"inPage_admin.html")).getPath()) ;
	    }  // if (templatemode)

	    temptags.setProperty("#servlet_url#",servletUrl) ;

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
		String existing_doc_filename = (new File(admintemplate_path, "existing_doc_name.html")).getPath() ;
		String existing_doc_name = null ;

		existing_doc_name = fileCache.getCachedFileString(new File(existing_doc_filename)) ;

		if (doc_types_vec != null && doc_types_vec.size() > 0) {
		    doc_types_sb.append("<option value=\"0\">"+existing_doc_name+"</option>") ;
		}

		// List of files to load, and tags to parse them into
		toload.setProperty("addDoc",(new File(admintemplate_path,"add_doc.html")).getPath()) ;
		toload.setProperty("saveSortStart",(new File(admintemplate_path,"sort_order.html")).getPath()) ;
		toload.setProperty("saveSortStop",(new File(admintemplate_path,"archive_del_button.html")).getPath()) ;
		toload.setProperty("sort_button",(new File(admintemplate_path,"sort_button.html")).getPath()) ;

		// Some tags to parse in the files we'll load.
		temptags.setProperty("#doc_types#",doc_types_sb.toString()) ;	// The doc-types.
		temptags.setProperty("#sortOrder"+sort_order+"#","checked") ;	// The sortorder for this document.
	    } // if (menumode)

	    temptags.setProperty("#getMetaId#",String.valueOf(meta_id)) ;

	    // Now load the files specified in "toload", and place them in "tags"
	    //System.out.println("Loading template-files.") ;

	    imcode.server.parser.MapSubstitution temptagsmapsubstitution = new imcode.server.parser.MapSubstitution(temptags, false) ;

	    try {
		StringBuffer templatebuffer = new StringBuffer() ;
		Enumeration propenum = toload.propertyNames() ;
		while ( propenum.hasMoreElements() ) {

		    String filetag = (String)propenum.nextElement() ;
		    String templatebufferfilename = toload.getProperty(filetag) ;
		    String templatebufferstring = fileCache.getCachedFileString(new File(templatebufferfilename)) ;
				// Humm... Now we must replace the tags in the loaded files too.
		    templatebufferstring = org.apache.oro.text.regex.Util.substitute(patMat,HASHTAG_PATTERN,temptagsmapsubstitution,templatebufferstring,org.apache.oro.text.regex.Util.SUBSTITUTE_ALL) ;

		    tags.setProperty(filetag,templatebufferstring) ;
		    templatebuffer.setLength(0) ;
		}
	    } catch(IOException e) {
		log.log(Log.ERROR, "An error occurred reading file during parsing.", e) ;
		return ("Error occurred reading file during parsing.\n"+e).getBytes("8859_1") ;
	    }

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
	    StringBuffer templatebuffer = new StringBuffer(fileCache.getCachedFileString(new File(templatePath,"text/" + template_id + ".html"))) ;

	    // Check file for tags
	    String template = templatebuffer.toString() ;
	    StringBuffer result = new StringBuffer(template.length()+16384) ; // This value is the amount i expect the document to increase in size.

	    imcode.server.parser.MenuParserSubstitution menuparsersubstitution = new imcode.server.parser.MenuParserSubstitution(menus,menumode,tags) ;
	    imcode.server.parser.HashTagSubstitution hashtagsubstitution = new imcode.server.parser.HashTagSubstitution(tags,numberedtags) ;
	    imcode.server.parser.ImcmsTagSubstitution imcmstagsubstitution = new imcode.server.parser.ImcmsTagSubstitution(this,user,meta_id,
															   templatePath,servletUrl,
															   included_docs,includemode,includelevel,includePath,
															   textMap,textmode,
															   imageMap,imagemode,imageUrl) ;

	    LinkedList parse = new LinkedList() ;
	    perl5util.split(parse,"/(<!--\\/?IMSCRIPT-->)/i",template) ;
	    Iterator pit = parse.iterator() ;
	    boolean parsing = false ;

	    // Well. Here we have it. The main parseloop.
	    // The Inner Sanctum of imCMS. Have fun.
	    while ( pit.hasNext() ) {
		// So, let's jump in and out of blocks delimited by <!--IMSCRIPT--> and <!--/IMSCRIPT-->
		String nextbit = (String)pit.next() ;
		if (nextbit.equalsIgnoreCase("<!--/IMSCRIPT-->")) { // We matched <!--/IMSCRIPT-->
		    parsing = false ;       // So, we're not parsing.
		    continue ;
		} else if (nextbit.equalsIgnoreCase("<!--IMSCRIPT-->")) { // We matched <!--IMSCRIPT-->
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
	    if (emp!=null) { // If we have something to emphasize...
		StringBuffer emphasized_result = new StringBuffer(returnresult.length()) ; // A StringBuffer to hold the result
		PatternMatcherInput emp_input = new PatternMatcherInput(returnresult) ;    // A PatternMatcherInput to match on
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
		} // while 
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
	
	Perl5Compiler empCompiler = new Perl5Compiler() ;
	// for each string to emphasize
	for (int i = 0 ; i < emp.length ; ++i) {
	    try {
		Pattern empPattern = empCompiler.compile("("+Perl5Compiler.quotemeta(emp[i])+")",Perl5Compiler.CASE_INSENSITIVE_MASK) ;
		str = org.apache.oro.text.regex.Util.substitute( // Threadsafe
								patMat,
								empPattern,
								emphasize_substitution,
								str,
								org.apache.oro.text.regex.Util.SUBSTITUTE_ALL
								) ;
	    } catch (MalformedPatternException ex) {
		log.log(Log.WARNING, "Dynamic Pattern-compilation failed in IMCService.emphasizeString(). Suspected bug in jakarta-oro Perl5Compiler.quotemeta(). The String was '"+emp[i]+"'",ex) ;
	    }
	}
	return str ;
    }

    private String getMenuModePrefix(PatternMatcher patMat, int menu_id, Properties tags) {
	String temp = tags.getProperty("addDoc") +
	    tags.getProperty("saveSortStart") ;

	return org.apache.oro.text.regex.Util.substitute(patMat,MENU_NO_PATTERN,new StringSubstitution(""+menu_id),temp,org.apache.oro.text.regex.Util.SUBSTITUTE_ALL) ;
    }

    private String getMenuModeSuffix(Properties tags) {
	return tags.getProperty("saveSortStop") ;
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
	    menu_rows[foo] = tmpsb.toString()+"\r\n" ;	// Store the line away... Note that "\r\n" is the standard html (as well as http and dos) end-of-line.
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
	    String menubuff_str = "" ;
	    if (menumode) {
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
	Substitution NULL_SUBSTITUTION = new StringSubstitution("") ;

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
	    menurowstr = "#adminStart#"+menurowstr+"#adminStop#" ;
	} else {
	    menurowstr = "#adminStart#"+menurowstr ;
	}
	// for each element of the menu...
	imcode.server.parser.MapSubstitution mapsubstitution = new imcode.server.parser.MapSubstitution() ;
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


}
