import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.text.DateFormat ;
import java.text.SimpleDateFormat ;
import java.text.ParseException ;

import imcode.util.* ;
import imcode.server.* ;

import org.apache.log4j.Category;

/**
   Save new meta for a document.
*/
public class SaveNewMeta extends HttpServlet {
    private final static String CVS_REV = "$Revision$" ;
    private final static String CVS_DATE = "$Date$" ;

	private final static Category mainLog = Category.getInstance(IMCConstants.MAIN_LOG);
	private final static DateFormat logdateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS ") ;

    /**
       init()
    */
    public void init( ServletConfig config ) throws ServletException {
	super.init( config ) ;
    }

    /**
       doPost()
    */
    public void doPost( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
	String host	= req.getHeader("Host") ;
	String imcserver	= Utility.getDomainPref("adminserver",host) ;
	String start_url	= Utility.getDomainPref( "start_url",host ) ;
	String servlet_url	= Utility.getDomainPref( "servlet_url",host ) ;

	imcode.server.User user ;
	String htmlStr = "" ;
	String submit_name = "" ;
	String search_string = "" ;
	String text = "" ;
	String values[] ;
	int txt_no = 0 ;

	res.setContentType( "text/html" );
	Writer out = res.getWriter( );

	// redirect data
	String scheme = req.getScheme( );
	String serverName = req.getServerName( );
	int p = req.getServerPort( );
	String port = (p == 80) ? "" : ":" + p;

	/*
	  From now on, we get the form data.
	*/
	String [] metatable = {
	/*  Nullable			Nullvalue */
	    "shared",			"0",
	    "disable_search",	        "0",
	    "archive",			"0",
	    "show_meta",		"0",
	    "permissions",		"0",
	    "expand",			"1",
	    "help_text_id",		"1",
	    "status_id",		"1",
	    "lang_prefix",		"se",
	    "sort_position",	        "1",
	    "menu_position",	        "1",
	    "description",		null,
	    "meta_headline",	        null,
	    "meta_text",		null,
	    "meta_image",		null,
	    "frame_name",		"",
	    "target",			null
	} ;

	Properties metaprops = new Properties () ;
	String r_r[] = req.getParameterValues("roles_rights") ;
	String u_r[] = req.getParameterValues("user_rights") ;
	String parent_meta_id = req.getParameter("parent_meta_id") ;
	String doc_menu_no = req.getParameter("doc_menu_no") ;
	String doc_type = req.getParameter("doc_type") ;
	String date_today = req.getParameter("date_today") ;
	String time_now = req.getParameter("time_now") ;
	String classification = req.getParameter("classification") ;

	int parent_int = Integer.parseInt(parent_meta_id) ;

	HashMap inputMap = new HashMap() ;

	// Loop through all meta-table-properties
	// Adding them to a HashMap to be used as input
	// That way i can mutilate the values before all the
	// permissions are checked.
	for ( int i=0 ; i<metatable.length ; i+=2 ) {
	    inputMap.put(metatable[i],req.getParameter(metatable[i])) ;
	}

	// If target is set to '_other', it means the real target is in 'frame_name'.
	// In this case, set target to the value of frame_name.
	String target = (String)inputMap.get("target") ;
	String frame_name = (String)inputMap.get("frame_name") ;
	if ( "_other".equals(target) && frame_name != null && !"".equals(frame_name) ) {
	    inputMap.put("target",frame_name) ;
   	}
	inputMap.remove("frame_name") ;  // we only need to store frame_name in db column "target"

	SimpleDateFormat datetimeformat = new SimpleDateFormat("yyyy-MM-dd HH:mm") ;
	Date dt = IMCServiceRMI.getCurrentDate(imcserver) ;

	String activated_date = req.getParameter("activated_date") ;
	String activated_time = req.getParameter("activated_time") ;
	String activated_datetime = null ;
	if ( activated_date != null && activated_time != null ) {
	    activated_datetime = activated_date + ' ' + activated_time ;
	    try {
		datetimeformat.parse(activated_datetime) ;
	    } catch (ParseException ex) {
		activated_datetime = null ;
	    }
	} else {
	    activated_datetime = datetimeformat.format(dt) ;
	} // end of else


	String archived_date = req.getParameter("archived_date") ;
	String archived_time = req.getParameter("archived_time") ;
	String archived_datetime = null ;
	if ( archived_date != null && archived_time != null ) {
	    archived_datetime = archived_date + ' ' + archived_time ;
	    try {
		datetimeformat.parse(archived_datetime) ;
	    } catch (ParseException ex) {
		archived_datetime = null ;
	    }
	}
	
	
	
	for ( int i=0 ; i<metatable.length ; i+=2 ) {
	    String tmp = (String)inputMap.get(metatable[i]) ;
	    if ( tmp != null) {
			metaprops.setProperty(metatable[i],tmp) ;
	    } else {
			metaprops.setProperty(metatable[i],metatable[i+1]) ;
	    }
	}
	
	
	// Check if user logged on
	if( (user=Check.userLoggedOn( req,res,start_url ))==null ) {
	    return ;
	}

	String lang_prefix = IMCServiceRMI.sqlQueryStr(imcserver, "select lang_prefix from lang_prefixes where lang_id = "+user.getInt("lang_id")) ;

	// Fetch all doctypes from the db and put them in an option-list
	// First, get the doc_types the current user may use.
	String[] user_dt = IMCServiceRMI.sqlProcedure(imcserver,"GetDocTypesForUser "+parent_meta_id+","+user.getInt("user_id")+",'"+lang_prefix+"'") ;
	HashSet user_doc_types = new HashSet() ;

	// I'll fill a HashSet with all the doc-types the current user may use,
	// for easy retrieval.
	for ( int i=0 ; i<user_dt.length ; i+=2 ) {
	    user_doc_types.add(user_dt[i]) ;
	}

	// So... if the user may not create this particular doc-type... he's outta here!
	if ( !user_doc_types.contains(doc_type) ) {
	    String output = AdminDoc.adminDoc(parent_int,parent_int,host,user,req,res) ;
	    if ( output != null ) {
		out.write(output) ;
	    }
	    return ;
	}
	
		

	// Lets fix the date information (date_created, modified etc)
	metaprops.setProperty("date_modified",datetimeformat.format(dt)) ;
	metaprops.setProperty("date_created",datetimeformat.format(dt)) ;
	metaprops.setProperty("owner_id",String.valueOf(user.getInt("user_id"))) ;
	
	
	if( req.getParameter( "cancel" ) != null ) {
	    String output = AdminDoc.adminDoc(Integer.parseInt(parent_meta_id),Integer.parseInt(parent_meta_id),host,user,req,res) ;
	    if ( output != null ) {
		out.write(output) ;
	    }
	    return ;

	    // Lets add a new meta to the db
	} else if( req.getParameter( "ok" ) != null ) {

	    Enumeration propkeys = metaprops.propertyNames() ;

	    // Lets build the sql statement to add a new meta id
	    String sqlStr = "insert into meta (doc_type,activate,classification,activated_datetime,archived_datetime" ;
	    String sqlStr2 =")\nvalues ("+doc_type+",0,'',"+(null == activated_datetime ? "NULL" : "'"+activated_datetime+"'")+","+(null == archived_datetime ? "NULL" : "'"+archived_datetime+"'") ;
	    while ( propkeys.hasMoreElements() ) {
		String temp = (String)propkeys.nextElement() ;
		String val = metaprops.getProperty(temp) ;
		String [] vp = {
		    "'",	"''"
		} ;
		sqlStr += ","+temp ;
		sqlStr2 += ",'"+Parser.parseDoc(val,vp)+"'" ;
	    }
	    sqlStr += sqlStr2 + ")" ;
	    IMCServiceRMI.sqlUpdateQuery(imcserver,sqlStr) ;
	    String meta_id = IMCServiceRMI.sqlQueryStr(imcserver,"select @@IDENTITY") ;

	    // Save the classifications to the db
	    if ( classification != null ) {
		IMCServiceRMI.sqlUpdateProcedure(imcserver,"Classification_Fix "+meta_id+",'"+classification+"'") ;
	    }

	    IMCServiceRMI.sqlUpdateProcedure(imcserver,"InheritPermissions "+meta_id+","+parent_meta_id+","+doc_type) ;

	    // Lets add the sortorder to the parents childlist
	    sqlStr =	"declare @new_sort int\n" +
		"select @new_sort = max(manual_sort_order)+10 from childs where meta_id = "+parent_meta_id +" and menu_sort = "+doc_menu_no+"\n"+
		"if @new_sort is null begin set @new_sort = 500 end\n"+
		"insert into childs (meta_id, to_meta_id, menu_sort, manual_sort_order) values ("+parent_meta_id+","+meta_id+","+doc_menu_no+",@new_sort)\n" ;
	    IMCServiceRMI.sqlUpdateQuery(imcserver,sqlStr) ;
	    log (meta_id) ;

	    // Lets update the parents created_date
	    sqlStr  = "update meta\n" ;
	    sqlStr += "set date_modified = '" + metaprops.getProperty( "date_modified" ) + "'\n" ;
	    sqlStr += "where meta_id = " + parent_meta_id ;
	    IMCServiceRMI.sqlUpdateQuery( imcserver,sqlStr ) ;

		//lets log to mainLog the stuff done
		mainLog.info(logdateFormat.format(new java.util.Date())+"Document [" +meta_id +"] of type ["+doc_type+"] created on ["+parent_meta_id+"] by user: [" +user.getString("first_name").trim() + " " + user.getString("last_name").trim() + "]");
		
		//ok lets handle the the section stuff save to db and so on
		//lets start an see if we got any request to change the inherit one
		String section_id = req.getParameter("change_section");
		if (section_id == null) {
			//ok it vas null so lets try and get the inherit one
			section_id = req.getParameter("current_section_id");
		}
		//ok if we have one lets update the db
		if (section_id != null) {
			IMCServiceRMI.sqlUpdateProcedure(imcserver,"SectionAddCrossref " + meta_id +", " +section_id);
		}
				
				
	    // Here is the stuff we have to do for each individual doctype. All general tasks
	    // for all documenttypes is done now.

	    // BROWSER DOCUMENT
	    if( doc_type.equals("6") ) {
		sqlStr = "insert into browser_docs (meta_id, to_meta_id, browser_id) values ("+meta_id+","+parent_meta_id+",0)" ;
		IMCServiceRMI.sqlUpdateQuery(imcserver, sqlStr) ;
		Vector vec = new Vector () ;
		sqlStr = "select name,browsers.browser_id,to_meta_id from browser_docs join browsers on browsers.browser_id = browser_docs.browser_id where meta_id = "+meta_id+" order by value desc,name asc" ;
		Hashtable hash = IMCServiceRMI.sqlQueryHash(imcserver,sqlStr) ;
		String[] b_id = (String[])hash.get("browser_id") ;
		String[] nm = (String[])hash.get("name") ;
		String[] to = (String[])hash.get("to_meta_id") ;
		String bs = "" ;
		if ( b_id != null ) {
		    bs+="<table width=\"50%\" border=\"0\">" ;
		    for ( int i = 0 ; i<b_id.length ; i++ ) {
			String[] temparr = {" ","&nbsp;"} ;
			bs += "<tr><td>"+Parser.parseDoc(nm[i],temparr)+":</td><td><input type=\"text\" name=\"bid"+b_id[i]+"\" value=\""+(to[i].equals("0") ? "" : to[i])+"\"></td></tr>" ;
		    }
		    bs+="</table>" ;
		}
		vec.add("#browsers#") ;
		vec.add(bs) ;
		sqlStr = "select browser_id,name from browsers where browser_id not in (select browsers.browser_id from browser_docs join browsers on browsers.browser_id = browser_docs.browser_id where meta_id = "+meta_id+" ) order by value desc,name asc" ;
		hash = IMCServiceRMI.sqlQueryHash(imcserver,sqlStr) ;
		b_id = (String[])hash.get("browser_id") ;
		nm = (String[])hash.get("name") ;
		String nb = "" ;
		if ( b_id!=null ) {
		    for ( int i = 0 ; i<b_id.length ; i++ ) {
			nb += "<option value=\""+b_id[i]+"\">"+nm[i]+"</option>" ;
		    }
		}
		vec.add("#new_browsers#") ;
		vec.add(nb) ;
		vec.add("#new_meta_id#") ;
		vec.add(String.valueOf(meta_id)) ;
		log (String.valueOf(meta_id)) ;
		vec.add("#getDocType#") ;
		vec.add("<INPUT TYPE=\"hidden\" NAME=\"doc_type\" VALUE=\""+doc_type+"\">") ;
		vec.add("#DocMenuNo#") ;
		vec.add("") ;
		vec.add("#getMetaId#") ;
		vec.add(String.valueOf(parent_meta_id)) ;
		vec.add("#servlet_url#") ;
		vec.add(servlet_url) ;
		htmlStr = IMCServiceRMI.parseDoc(imcserver, vec, "new_browser_doc.html", lang_prefix) ;

		// FILE UP LOAD
	    } else if( doc_type.equals("8") ) {
		sqlStr = "select mime,mime_name from mime_types where lang_prefix = '"+lang_prefix+"' and mime != 'other'" ;
		String temp[] = IMCServiceRMI.sqlQuery(imcserver,sqlStr) ;
		Vector vec = new Vector() ;
		String temps = null ;
		for (int i = 0; i < temp.length; i+=2) {
		    temps += "<option value=\""+temp[i]+"\">"+temp[i+1]+"</option>" ;
		}
		sqlStr = "select mime,mime_name from mime_types where lang_prefix = '"+lang_prefix+"' and mime = 'other'" ;
		temp = IMCServiceRMI.sqlQuery(imcserver,sqlStr) ;
		temps += "<option value=\""+temp[0]+"\">"+temp[1]+"</option>" ;

		vec.add("#mime#") ;
		vec.add(temps) ;
		vec.add("#new_meta_id#") ;
		vec.add(String.valueOf(meta_id)) ;
		vec.add("#getMetaId#") ;
		vec.add(String.valueOf(parent_meta_id)) ;
		vec.add("#servlet_url#") ;
		vec.add(servlet_url) ;
		htmlStr = IMCServiceRMI.parseDoc(imcserver, vec, "new_fileupload.html", lang_prefix) ;
		//				htmlStr = IMCServiceRMI.interpretAdminTemplate( imcserver, meta_id,user,"new_fileupload.html",8,new_meta_id,0,doc_menu_no );
		out.write( htmlStr ) ;
		return ;

		// URL DOCUMENT
	    } else if( doc_type.equals("5") ) {
		Vector vec = new Vector() ;
		vec.add("#new_meta_id#") ;
		vec.add(String.valueOf(meta_id)) ;
		vec.add("#getMetaId#") ;
		vec.add(String.valueOf(parent_meta_id)) ;
		vec.add("#servlet_url#") ;
		vec.add(servlet_url) ;
		htmlStr = IMCServiceRMI.parseDoc(imcserver, vec, "new_url_doc.html", lang_prefix) ;
		out.write( htmlStr ) ;
		return ;

		// FRAMESET DOCUMENT
	    } else if( doc_type.equals("7") ) {
		Vector vec = new Vector() ;
		vec.add("#new_meta_id#") ;
		vec.add(String.valueOf(meta_id)) ;
		vec.add("#getMetaId#") ;
		vec.add(String.valueOf(parent_meta_id)) ;
		vec.add("#servlet_url#") ;
		vec.add(servlet_url) ;
		htmlStr = IMCServiceRMI.parseDoc(imcserver, vec, "new_frameset.html", lang_prefix) ;
		out.write( htmlStr ) ;
		return ;

		// EXTERNAL DOCUMENTS
	    } else if( Integer.parseInt(doc_type) > 100 ) {
		// check if external doc
		imcode.server.ExternalDocType ex_doc ;
		ex_doc = IMCServiceRMI.isExternalDoc( imcserver,Integer.parseInt(meta_id),user ) ;
		String paramStr = "?meta_id=" + meta_id + "&" ;
		paramStr += "parent_meta_id=" + parent_meta_id + "&" ;
		paramStr += "cookie_id=" + "1A" + "&action=new" ;
		res.sendRedirect( scheme + "://" + serverName + port + servlet_url + ex_doc.getCallServlet( ) + paramStr );
		return ;

		// TEXT DOCUMENT
	    } else if (doc_type.equals("2")) {
		//lets get the users greatest permission_set for this dokument
		final int perm_set = IMCServiceRMI.getUserHighestPermissionSet (imcserver,Integer.parseInt(meta_id), user.getInt("user_id"));
		//ok now lets see what to do with the templates
		sqlStr = "select template_id, sort_order,group_id,default_template_1,default_template_2 from text_docs where meta_id = " + parent_meta_id ;
		String temp[] = IMCServiceRMI.sqlQuery(imcserver,sqlStr) ;
		//ok now we have to setup the template too use
		if (perm_set == imcode.server.IMCConstants.DOC_PERM_SET_RESTRICTED_1)
		    {
			//ok restricted_1 permission lets see if we have a default template fore this one
			//and if so lets put it as the orinary template instead of the parents
			try
			    {
				int tempInt = Integer.parseInt(temp[3]);
				if(tempInt >= 0)
				    temp[0] = String.valueOf(tempInt);
			    }catch(NumberFormatException nfe)
				{

				    //there wasn't a number but we dont care, we just catch the exeption and moves on.
				}
		    }else if(perm_set == imcode.server.IMCConstants.DOC_PERM_SET_RESTRICTED_2)
			{ //ok we have a restricted_2 permission lets see if we have default template fore this one
			    //and if soo lets put it as ordinary instead of the parents
			    try
				{
				    int tempInt = Integer.parseInt(temp[4]);
				    if(tempInt >= 0)
					temp[0] = String.valueOf(tempInt);
				}catch(NumberFormatException nfe)
				    {
					//there wasn't a number but we dont care, we just catch the exeption and moves on.
				    }
			}
		//ok were set, lets update db
		sqlStr = "insert into text_docs (meta_id,template_id,sort_order,group_id,default_template_1,default_template_2) values ("+meta_id+","+temp[0]+","+temp[1]+","+temp[2]+","+temp[3]+","+temp[4]+")" ;
		IMCServiceRMI.sqlUpdateQuery(imcserver,sqlStr) ;


		// Lets check if we should copy the metaheader and meta text into text1 and text2.
		// There are 2 types of texts. 1= html text. 0= plain text. By
		// default were creating html texts.
		String copyMetaFlag = (req.getParameter("copyMetaHeader")==null) ? "0" : (req.getParameter("copyMetaHeader")) ;
		if( copyMetaFlag.equals("1") && doc_type.equals("2") ) {
		    String [] vp = {
			"'",	"''"
		    } ;

		    String mHeadline = Parser.parseDoc(metaprops.getProperty("meta_headline"),vp) ;
		    String mText = Parser.parseDoc(metaprops.getProperty("meta_text"),vp) ;

		    sqlStr = "insert into texts (meta_id,name,text,type) values ("+meta_id +", 1, '" + mHeadline + "', 1)" ;
		    IMCServiceRMI.sqlUpdateQuery(imcserver,sqlStr) ;
		    sqlStr = "insert into texts (meta_id,name,text,type) values ("+meta_id +", 2, '" + mText + "', 1)" ;
		    IMCServiceRMI.sqlUpdateQuery(imcserver,sqlStr) ;
		}

		// Lets activate the textfield
		sqlStr = "update meta set activate = 1 where meta_id = "+meta_id ;
		IMCServiceRMI.sqlUpdateQuery(imcserver,sqlStr) ;

		// Lets build the page
		String output = AdminDoc.adminDoc(Integer.parseInt(meta_id),Integer.parseInt(meta_id),host,user,req,res) ;
		if ( output != null ) {
		    out.write(output) ;
		}
		return ;
	    } // end text document

	}
	out.write(htmlStr) ;
    }

    public boolean contains (String[] array, String str) {	// Check whether a string array contains the specified string
	if ( array == null || str == null ) {
	    return false ;
	}
	for ( int i=0 ; i<array.length ; i++ ) {
	    if ( str.equals(array[i]) ) {
		return true ;
	    }
	}
	return false ;
    }
}
