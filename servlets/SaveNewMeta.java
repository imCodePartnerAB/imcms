import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.text.SimpleDateFormat ;

import imcode.util.* ;
/**
  Save new meta for a document.
  */
public class SaveNewMeta extends HttpServlet {
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
		String host 				= req.getHeader("Host") ;
		String imcserver 			= Utility.getDomainPref("adminserver",host) ;
		String start_url        	= Utility.getDomainPref( "start_url",host ) ;
		String servlet_url        	= Utility.getDomainPref( "servlet_url",host ) ;

		imcode.server.User user ;
		String htmlStr = "" ;
		String submit_name = "" ;
		String search_string = "" ;
		String text = "" ;
		String values[] ;
		int txt_no = 0 ;

		res.setContentType( "text/html" );
		ServletOutputStream out = res.getOutputStream( );

		// redirect data
		String scheme = req.getScheme( );
		String serverName = req.getServerName( );
		int p = req.getServerPort( );
		String port = (p == 80) ? "" : ":" + p;

		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd") ;
		SimpleDateFormat timeformat = new SimpleDateFormat("HH:mm") ;
		Date dt = IMCServiceRMI.getCurrentDate(imcserver) ;


		/*
		From now on, we get the form data.
		*/
		String [] metatable = {
		/*  Nullable			Nullvalue */
			"shared",			"0",
			"disable_search",	"0",
			"archive",			"0",
			"show_meta",		"0",
//			"category_id",		"1",
			"permissions",		"0",
			"expand",			"1",
			"help_text_id",		"1",
			"status_id",		"1",
			"lang_prefix",		"se",
			"sort_position",	"1",
			"menu_position",	"1",
			"description",		null,
			"meta_headline",	null,
			"meta_text",		null,
			"meta_image",		null,
//			"date_created",		null,
//			"date_modified",	null,
			"activated_date",	dateformat.format(dt),
			"activated_time",	timeformat.format(dt),
			"archived_date",	"",
			"archived_time",	"",
			"frame_name",		null,
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

		for ( int i=0 ; i<metatable.length ; i+=2 ) {
			String tmp = req.getParameter(metatable[i]) ;
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
			byte[] tempbytes ;
			tempbytes = AdminDoc.adminDoc(parent_int,parent_int,host,user,req,res) ;
			if ( tempbytes != null ) {
				out.write(tempbytes) ;
			}
			return ;
		}

		// Lets fix the date information (date_created, modified etc)
		metaprops.setProperty("date_modified",dateformat.format(dt)) ;
		metaprops.setProperty("date_created",dateformat.format(dt)) ;
		metaprops.setProperty("owner_id",String.valueOf(user.getInt("user_id"))) ;
		/*		String tempStr = req.getParameter( "meta_headline" ) ;
		for( int i = 0 ; i < tempStr.length( ) ; i++ ) {
		if( tempStr.charAt( i ) == '\"' ) {
		meta_headline += "&quot;" ;
		} else {
		meta_headline += tempStr.charAt( i ) ;
		}
		}

		if( !destination.equals( "_other" ) ) {
		doc.addField( "destination",destination ) ;
		} else {
		doc.addField( "destination",frame_name ) ;
		}
		*/
		// Check if user logged on
		if( (user=Check.userLoggedOn( req,res,start_url ))==null ) {
			return ;
		}

		if( req.getParameter( "cancel" ) != null ) {
			log ("Pressed cancel...") ;
//			htmlStr = IMCServiceRMI.interpretTemplate( imcserver,Integer.parseInt(parent_meta_id),user ) ;
			byte[] tempbytes = AdminDoc.adminDoc(Integer.parseInt(parent_meta_id),Integer.parseInt(parent_meta_id),host,user,req,res) ;
			if ( tempbytes != null ) {
				out.write(tempbytes) ;
			}
			return ;

	// Lets add a new meta to the db
		} else if( req.getParameter( "ok" ) != null ) {
			log ("Pressed ok...") ;

			//			int new_meta_id = IMCServiceRMI.saveNewDoc( imcserver,meta_id,user,doc,doc_menu_no,roles_rights,user_rights ) ;

			Enumeration propkeys = metaprops.propertyNames() ;

	// Lets get the new meta id from db
			String sqlStr =	"select max(meta_id)+1 from meta\n" ;
			String meta_id = IMCServiceRMI.sqlQueryStr(imcserver,sqlStr) ;
			// log ("OK 1") ;

	// Lets build the sql statement to add a new meta id
		sqlStr = "insert into meta (meta_id,doc_type,activate,classification" ;
			String sqlStr2 =")\nvalues ("+meta_id+","+doc_type+",0,''" ;
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
			// log ("OK 2") ;

			// Save the classifications to the db
			if ( classification != null ) {
				IMCServiceRMI.sqlUpdateProcedure(imcserver,"Classification_Fix "+meta_id+",'"+classification+"'") ;
			}

	// Lets add the userpermissioninformation into db
			//sqlStr = "insert into user_rights (permission_id,user_id,meta_id) values (99,"+user.getInt("user_id")+","+meta_id+")" ;
			//IMCServiceRMI.sqlUpdateQuery(imcserver,sqlStr) ;
			// log ("OK 3") ;


	/*
			sqlStr 	= "insert into doc_permission_sets\n"
					+ "select "+meta_id+", ndps.set_id, ndps.permission_id from new_doc_permission_sets ndps where meta_id = "+parent_meta_id+"\n"

					+ "insert into doc_permission_sets_ex\n"
					+ "select "+meta_id+", ndps.set_id, ndps.permission_id, ndps.permission_data from new_doc_permission_sets_ex ndps where meta_id = "+parent_meta_id+"\n"

					+ "insert into new_doc_permission_sets\n"
					+ "select "+meta_id+", ndps.set_id, ndps.permission_id from new_doc_permission_sets ndps where meta_id = "+parent_meta_id+"\n"

					+ "insert into new_doc_permission_sets_ex\n"
					+ "select "+meta_id+", ndps.set_id, ndps.permission_id, ndps.permission_data from new_doc_permission_sets_ex ndps where meta_id = "+parent_meta_id+"\n"

					+ "insert into roles_rights\n"
					+ "select role_id, "+meta_id+",set_id from roles_rights where meta_id = "+parent_meta_id ;
			IMCServiceRMI.sqlUpdateQuery(imcserver,sqlStr) ;
	*/
			IMCServiceRMI.sqlUpdateProcedure(imcserver,"InheritPermissions "+meta_id+","+parent_meta_id+","+doc_type) ;		
		//	log ("OK 1") ;

	// Lets add the sortorder to the parents childlist
			sqlStr = 	"declare @new_sort int\n" +
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

	// Here is the stuff we have to do for each individual doctype. All general tasks
  // for all documenttypes is done now.


	//	log ("Doctype: " + doc_type) ;

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
				out.println( htmlStr ) ;
				return ;

	 // URL DOCUMENT
			} else if( doc_type.equals("5") ) {
				htmlStr = IMCServiceRMI.interpretAdminTemplate( imcserver, Integer.parseInt(parent_meta_id),user,"new_url_doc.html",5,Integer.parseInt(meta_id),0,Integer.parseInt(doc_menu_no) );
				out.println( htmlStr ) ;
				return ;

	 // FRAMESET DOCUMENT
			} else if( doc_type.equals("7") ) {
				htmlStr = IMCServiceRMI.interpretAdminTemplate( imcserver, Integer.parseInt(parent_meta_id),user,"new_frameset.html",7,Integer.parseInt(meta_id),0,Integer.parseInt(doc_menu_no) );
				out.println( htmlStr ) ;
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
				sqlStr = "select template_id, sort_order,group_id from text_docs where meta_id = " + parent_meta_id ;
				String temp[] = IMCServiceRMI.sqlQuery(imcserver,sqlStr) ;
				sqlStr = "insert into text_docs (meta_id,template_id,sort_order,group_id) values ("+meta_id+","+temp[0]+","+temp[1]+","+temp[2]+")" ;
				IMCServiceRMI.sqlUpdateQuery(imcserver,sqlStr) ;


	  // Lets check if we should copy the metaheader and meta text into text1 and text2.
	  // There are 2 types of texts. 1= html text. 0= plain text. By
	  // default were creating html texts.
				String copyMetaFlag = (req.getParameter("copyMetaHeader")==null) ? "0" : (req.getParameter("copyMetaHeader")) ;
				if( copyMetaFlag.equals("1") && doc_type.equals("2") ) {
				    String [] vp = {
					"'",	"''"
				    } ;
					String mHeadline = metaprops.getProperty("meta_headline") ;
					String mText = metaprops.getProperty("meta_text") ;

					mHeadline = Parser.parseDoc(mHeadline,vp) ;
					mText = Parser.parseDoc(mText,vp) ;

					sqlStr = "insert into texts (meta_id,name,text,type) values ("+meta_id +", 1, '" + mHeadline + "', 1)" ;
					IMCServiceRMI.sqlUpdateQuery(imcserver,sqlStr) ;
					sqlStr = "insert into texts (meta_id,name,text,type) values ("+meta_id +", 2, '" + mText + "', 1)" ;
					IMCServiceRMI.sqlUpdateQuery(imcserver,sqlStr) ;
				}

	  // Lets activate the textfield
				sqlStr = "update meta set activate = 1 where meta_id = "+meta_id ;
				IMCServiceRMI.sqlUpdateQuery(imcserver,sqlStr) ;

			// Lets build the page
				byte[] tempbytes = AdminDoc.adminDoc(Integer.parseInt(meta_id),Integer.parseInt(meta_id),host,user,req,res) ;
				if ( tempbytes != null )
					out.write(tempbytes) ;
				return ;
			} // end text document

   // ADVANCED MODE, this code will never be when we start using the new mode
   // It was used before when we had an advanced button on the plain meta page
   // Remarked by Rickard 2000-08-25

		} /*else if( req.getParameter( "advanced" )!=null ) {
			log ("Pressed advanced...") ;
			//		int new_meta_id = IMCServiceRMI.saveNewDoc( imcserver,meta_id,user,doc,0,roles_rights,user_rights) ;
			//		htmlStr = IMCServiceRMI.interpretAdminTemplate( imcserver,new_meta_id,user,"adv_new_meta.html",doc.getInt( "doc_type" ),meta_id,0,doc_menu_no ) ;

			final int NORMAL 	= 0 ;
			final int CHECKBOX 	= 1 ;
			final int OPTION	= 2 ;

			int metatabletype[] = {
				CHECKBOX,
				CHECKBOX,
				CHECKBOX,
				CHECKBOX,
				NORMAL,
				NORMAL,
				NORMAL,
				NORMAL,
				NORMAL,
				NORMAL,
				NORMAL,
				NORMAL,
				NORMAL,
				NORMAL,
				NORMAL,
				NORMAL,
				NORMAL,
				NORMAL,
				NORMAL,
				NORMAL,
				NORMAL,
				NORMAL,
				NORMAL,
				NORMAL,
				OPTION
			} ;
			String lang_prefix = IMCServiceRMI.sqlQueryStr(imcserver, "select lang_prefix from lang_prefixes where lang_id = "+user.getInt("lang_id")) ;

			htmlStr = IMCServiceRMI.parseDoc(imcserver,null,"adv_new_meta.html",lang_prefix ) ;
			Vector vec = new Vector () ;
			String checks = "" ;
			for ( int i = 0 ; i<metatable.length ; i+=2 ) {
				String temp = metaprops.getProperty(metatable[i]) ;
				String[] pd = {
					"&",	"&amp;",
					"<",	"&lt;",
					">",	"&gt;",
					"\"",	"&quot;"
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

			vec.add("#checks#") ;
			vec.add( checks ) ;
			vec.add("#parent_meta_id#") ;
			vec.add(parent_meta_id) ;
			vec.add("#doc_menu_no#") ;
			vec.add(doc_menu_no) ;
			vec.add("#doc_type#") ;
			vec.add(doc_type) ;
			vec.add("#owner#") ;
			vec.add(user.getString("first_name")+" "+user.getString("last_name")) ;
//			vec.add("#date_today#") ;
//			vec.add(date_today) ;
//			vec.add("#time_now#") ;
//			vec.add(time_now) ;
			Hashtable hash = IMCServiceRMI.sqlQueryHash(imcserver,"select role_id, role_name from roles where role_id > 0") ;
			String roles_rights = IMCServiceRMI.parseDoc(imcserver,null,"roles_rights_table_head.html",lang_prefix ) ;
			String [] role_id = (String[])hash.get("role_id") ;
			String [] role_name = (String[])hash.get("role_name") ;

			for ( int i=0 ; i<role_id.length ; i++ ) {
				Vector vec2 = new Vector() ;
				vec2.add("#role_name#") ;
				vec2.add(role_name[i]) ;
				vec2.add("#1#") ;
				if ( contains(r_r, role_id[i]+"_1") ) {
					vec2.add("<input type=checkbox name=\"roles_rights\" value=\""+role_id[i]+"_1\" checked>") ;
				} else {
					vec2.add("<input type=checkbox name=\"roles_rights\" value=\""+role_id[i]+"_1\">") ;
				}
				vec2.add("#3#") ;
				if ( contains(r_r, role_id[i]+"_3") ) {
					vec2.add("<input type=checkbox name=\"roles_rights\" value=\""+role_id[i]+"_3\" checked>") ;
				} else {
					vec2.add("<input type=checkbox name=\"roles_rights\" value=\""+role_id[i]+"_3\">") ;
				}
				roles_rights += IMCServiceRMI.parseDoc(imcserver,vec2,"roles_rights_table_row.html",lang_prefix ) ;
			}

			roles_rights += IMCServiceRMI.parseDoc(imcserver,null,"roles_rights_table_tail.html",lang_prefix ) ;
			vec.add("#roles_rights#") ;
			vec.add(roles_rights) ;
			htmlStr = IMCServiceRMI.parseDoc(imcserver,vec,"adv_new_meta.html",lang_prefix ) ;

		} */
		out.print(htmlStr) ;
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




