import java.io.*;
import java.util.*;
import java.text.* ;
import javax.servlet.*;
import javax.servlet.http.*;

import imcode.util.* ;

/**
  Adds a new document to a menu.
	Shows an empty metadata page, which calls SaveNewMeta
*/
public class AddDoc extends HttpServlet {

	/**
	 init()
	*/
	public void init ( ServletConfig config ) throws ServletException {
		super.init ( config ) ;
	}
/*
	public void doGet ( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
		doPost(req,res) ;
	}
*/
	/**
	doPost()
	*/
	public void doPost ( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
		String host 				= req.getHeader("Host") ;
		String imcserver 			= imcode.util.Utility.getDomainPref("adminserver",host) ;
		String start_url        	= imcode.util.Utility.getDomainPref( "start_url",host ) ;

		imcode.server.User user ;

		res.setContentType ( "text/html" );
		ServletOutputStream out = res.getOutputStream ( );
		String meta_id = req.getParameter ( "meta_id" ) ;
		int meta_id_int = Integer.parseInt(meta_id) ;
		String parent_meta_id = req.getParameter ( "parent_meta_id" ) ;
		String item_selected = req.getParameter ( "edit_menu" ) ;
		String doc_menu_no = req.getParameter ( "doc_menu_no" ) ;
		String doc_type = "2" ;

		// Check if user logged on
		if ( (user=Check.userLoggedOn(req,res,start_url))==null ) {
			return ;
		}



		// Lets get the language
		String lang_prefix = IMCServiceRMI.sqlQueryStr(imcserver, "select lang_prefix from lang_prefixes where lang_id = "+user.getInt("lang_id")) ;

		// Fetch all doctypes from the db and put them in an option-list
		// First, get the doc_types the current user may use.
		String[] user_dt = IMCServiceRMI.sqlProcedure(imcserver,"GetDocTypesForUser "+meta_id+","+user.getInt("user_id")+",'"+lang_prefix+"'") ;
		HashSet user_doc_types = new HashSet() ;

		// I'll fill a HashSet with all the doc-types the current user may use,
		// for easy retrieval.
		for ( int i=0 ; i<user_dt.length ; i+=2 ) {
			user_doc_types.add(user_dt[i]) ;
		}

		if ( !"0".equals(item_selected) && !user_doc_types.contains(item_selected) ) {
			byte[] tempbytes ;
			tempbytes = AdminDoc.adminDoc(meta_id_int,meta_id_int,host,user,req,res) ;
			if ( tempbytes != null ) {
				out.write(tempbytes) ;
			}
			return ;
		}

		// Lets detect the doctype were gonna add
		if ( item_selected.equals ( "2" ) ) {
//			htmlStr = IMCServiceRMI.interpretAdminTemplate ( imcserver, meta_id,user,"new_meta.html",2,0,0,doc_menu_no ) ;
			doc_type = "2" ;
		} else if ( item_selected.equals ( "8" ) ) {
//			htmlStr = IMCServiceRMI.interpretAdminTemplate ( imcserver, meta_id,user,"new_meta.html",8,0,0,doc_menu_no ) ;
			doc_type = "8" ;
		} else if ( item_selected.equals ( "6" ) ) {
//			htmlStr = IMCServiceRMI.interpretAdminTemplate ( imcserver, meta_id,user,"new_meta.html",6,0,0,doc_menu_no ) ;
			doc_type = "6" ;
		} else if ( item_selected.equals ( "7" ) ) {
//			htmlStr = IMCServiceRMI.interpretAdminTemplate ( imcserver, meta_id,user,"new_meta.html",7,0,0,doc_menu_no ) ;
			doc_type = "7" ;
		} else if ( item_selected.equals ( "0" ) ) { // its an existing document
			Vector vec = new Vector () ;
			vec.add("#meta_id#") ;
			vec.add(meta_id) ;
			vec.add("#doc_menu_no#") ;
			vec.add(doc_menu_no) ;

                        // Lets get todays date
                        SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd");
                        Date toDay = new Date() ;
                        vec.add("#now#") ;
                        vec.add(  formatter.format(toDay) ) ;

		// Lets parse the html page which consists of the add an existing doc
			out.print(IMCServiceRMI.parseDoc(imcserver,vec,"existing_doc.html",lang_prefix)) ;
			return ;

		} else if ( item_selected.equals ( "5" ) ) {
//			htmlStr = IMCServiceRMI.interpretAdminTemplate ( imcserver, meta_id,user,"new_meta.html",5,0,0,doc_menu_no ) ;
			doc_type = "5" ;
		} else {
			doc_type = item_selected ;
		//	htmlStr = IMCServiceRMI.interpretAdminTemplate ( imcserver, meta_id,user,"new_meta.html",doc_type,0,0,doc_menu_no ) ;
		}


		final int NORMAL 	= 0 ;
		final int CHECKBOX 	= 1 ;
		final int OPTION	= 2 ;

		String [] metatable = {
		/*  Nullable			Nullvalue */
			"shared",			"0",
			"disable_search",	"0",
			"archive",			"0",
			"show_meta",		"0",
//			"category_id",		"1",
			"permissions",		"1",
//			"expand",			"1",
//			"help_text_id",		"1",
//			"status_id",		"1",
//			"lang_prefix",		"se",
//			"sort_position",	"1",
//			"menu_position",	"1",
//			"description",		null,
			"meta_image",		null,
//			"classification",	"",
			"frame_name",		null,
			"target",			null,
		} ;

		int metatabletype[] = {
			CHECKBOX,
			CHECKBOX,
			CHECKBOX,
			CHECKBOX,
//			NORMAL,
			NORMAL,
//			NORMAL,
//			NORMAL,
//			NORMAL,
//			NORMAL,
//			NORMAL,
//			NORMAL,
//			NORMAL,
			NORMAL,
//			NORMAL,
			NORMAL,
			OPTION
		} ;


		// Lets get the meta information
		String sqlStr = "select * from meta where meta_id = "+meta_id ;
		Hashtable hash = IMCServiceRMI.sqlQueryHash(imcserver,sqlStr) ;

		// Lets get the html template file

		String htmlStr ;

		String advanced = "" ;

		if (IMCServiceRMI.checkDocAdminRights( imcserver, meta_id_int, user, 2 )) {
			advanced = "adv_" ;
		}

		if (item_selected.equals ( "2" )) {

			htmlStr = IMCServiceRMI.parseDoc(imcserver,null,advanced+"new_meta_text.html",lang_prefix ) ;
		} else {
			htmlStr = IMCServiceRMI.parseDoc(imcserver,null,advanced+"new_meta.html",lang_prefix ) ;
		}

		Vector vec = new Vector () ;
		String checks = "" ;
		for ( int i = 0 ; i<metatable.length ; i+=2 ) {
			String temp = ((String[])hash.get(metatable[i]))[0] ;
			String[] pd = {
				"<",	"&lt;",
				">",	"&gt;",
				"\"",	"&quot;",
				"&",	"&amp;"
			} ;
			temp = Parser.parseDoc(temp,pd) ;
			String tag = "#"+metatable[i]+"#" ;
			if ( metatabletype[i/2] == NORMAL ) {			// This is not a checkbox or an optionbox
				if ( htmlStr.indexOf(tag)==-1 ) {
					checks += "<input type=hidden name=\""+metatable[i]+"\" value=\""+temp+"\">" ;
				} else {
					vec.add(tag) ;							// Replace its corresponding tag
					vec.add(temp) ;
				}
			} else if ( metatabletype[i/2] == CHECKBOX ) {	// This is a checkbox
				if ( !temp.equals(metatable[i+1]) ) {	// If it is equal to the nullvalue, it must not appear (i.e. equal null)
					if ( htmlStr.indexOf(tag)==-1 ) {
						checks += "<input type=hidden name=\""+metatable[i]+"\" value=\""+temp+"\">" ;
					} else {
						vec.add(tag) ;
						vec.add("checked") ;
					}
				}
			} else if ( metatabletype[i/2] == OPTION ) {	// This is an optionbox
				if ( htmlStr.indexOf("#"+temp+"#")==-1 ) {	// There is no tag equal to the value of this
					if ( htmlStr.indexOf(tag)==-1 ) {
						checks += "<input type=hidden name=\""+metatable[i]+"\" value=\""+temp+"\">" ;
					} else {
						vec.add(tag) ;							// Replace its corresponding tag
						vec.add(temp) ;
					}
				} else {
					vec.add("#"+temp+"#") ;
					vec.add("checked") ;
				}
			}
		}

	// Lets add the standard meta information
		vec.add("#parent_meta_id#") ;
		vec.add(((String[])hash.get("meta_id"))[0]) ;

// Lets get the permission stuff and put it into hidden fields, we'll need them later
/*
	hash = IMCServiceRMI.sqlQueryHash(imcserver, "select role_id, set_id from roles_rights where meta_id = "+meta_id ) ;
	if (hash != null) {
		String[] role_id = (String[])hash.get("role_id") ;
		String[] permission_id = (String[])hash.get("permission_id") ;
		if ( role_id != null ) {
			for ( int i=0 ; i<role_id.length ; i++ ) {
				checks += "<input type=hidden name=\"roles_rights\" value=\""+role_id[i]+"_"+permission_id[i]+"\">" ;
			}
		}
	}
*/
	// Here i'll select all classification-strings and
	// concatenate them into one semicolon-separated string.
		sqlStr = "select code from classification c join meta_classification mc on mc.class_id = c.class_id where mc.meta_id = "+meta_id ;
		String[] classifications = IMCServiceRMI.sqlQuery(imcserver,sqlStr) ;
		String classification = "" ;
		if ( classifications.length > 0 ) {
			classification += classifications[0] ;
			for ( int i = 1 ; i<classifications.length ; ++i ) {
				classification += "; "+classifications[i] ;
			}
		}
		vec.add("#classification#") ;
		vec.add(classification) ;

// Lets fix the date information (date_created, modified etc)
		Date dt = IMCServiceRMI.getCurrentDate(imcserver) ;
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd") ;
//		checks += "<input type=hidden name=\"date_created\" value=\""+dateformat.format(dt)+"\">" ;
//		checks += "<input type=hidden name=\"date_modified\" value=\""+dateformat.format(dt)+"\">" ;
//		checks += "<input type=hidden name=\"activated_date\" value=\""+dateformat.format(dt)+"\">" ;

		vec.add("#activated_date#") ;
		vec.add(dateformat.format(dt)) ;
		dateformat = new SimpleDateFormat("HH:mm") ;
		vec.add("#activated_time#") ;
		vec.add(dateformat.format(dt)) ;

//		checks += "<input type=hidden name=\"activated_time\" value=\""+dateformat.format(dt)+"\">"	;

		vec.add("#checks#") ;
		vec.add(checks) ;

			// Lets add the document informtion, the creator etc
		vec.add("#doc_menu_no#") ;
		vec.add(doc_menu_no) ;
		vec.add("#doc_type#") ;
		vec.add(doc_type) ;
//		vec.add("#owner#") ;
//		vec.add(user.getString("first_name")+" "+user.getString("last_name")) ;

  // Lets parse the information and send it to the browser
		if (item_selected.equals ( "2" )) {
			out.print ( IMCServiceRMI.parseDoc(imcserver,vec,advanced+"new_meta_text.html",lang_prefix ) ) ;
		} else {
			out.print ( IMCServiceRMI.parseDoc(imcserver,vec,advanced+"new_meta.html",lang_prefix ) ) ;
		}

	}

}

