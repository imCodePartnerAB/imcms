import java.io.*;
import java.util.*;
import java.text.DateFormat ;
import java.text.SimpleDateFormat ;
import java.text.ParseException ;
import javax.servlet.*;
import javax.servlet.http.*;

import imcode.util.* ;
import imcode.server.* ;

import org.apache.log4j.Category;

/**
  Save meta from metaform.
  */
public class SaveMeta extends HttpServlet {
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
		String host				= req.getHeader("Host") ;
		String imcserver			= Utility.getDomainPref("adminserver",host) ;
		String start_url	= Utility.getDomainPref( "start_url",host ) ;
		String servlet_url	= Utility.getDomainPref( "servlet_url",host ) ;

		imcode.server.User user ;

		// Check if user logged on
		if( (user=Check.userLoggedOn( req,res,start_url ))==null ) {
			return ;
		}

		res.setContentType( "text/html" );
		ServletOutputStream out = res.getOutputStream();

		String meta_id = req.getParameter( "meta_id" ) ;
		int meta_id_int = Integer.parseInt(meta_id) ;

		if ( !IMCServiceRMI.checkDocAdminRightsAny(imcserver,meta_id_int,user,7 ) ) {	// Checking to see if user may edit this
		    byte[] tempbytes = AdminDoc.adminDoc(meta_id_int,meta_id_int,host,user,req,res) ;
		    if ( tempbytes != null ) {
			out.write(tempbytes) ;
		    }
		    return ;
		}

		Properties metaprops = new Properties () ;

		String classification = req.getParameter("classification") ;

		// Hey, hey! Watch as i fetch the permission-set set (pun intended) for each role!
		String[][] role_permissions = IMCServiceRMI.sqlProcedureMulti(imcserver, "GetRolesDocPermissions "+meta_id) ;

		// Now watch as i fetch the permission_set for the user...
		String[] current_permissions = IMCServiceRMI.sqlProcedure(imcserver, "GetUserPermissionSet "+meta_id+", "+user.getInt("user_id")) ;
		int user_set_id = Integer.parseInt(current_permissions[0]) ;	// The users set_id

		// Check if the user has any business in here whatsoever.
		if ( user_set_id > 2 ) {
		    byte[] tempbytes = AdminDoc.adminDoc(meta_id_int,meta_id_int,host,user,req,res) ;
		    if ( tempbytes != null ) {
			out.write(tempbytes) ;
		    }
		    return ;
		}
		int user_perm_set = Integer.parseInt(current_permissions[1]) ;	// The users permission_set for that id
		int currentdoc_perms = Integer.parseInt(current_permissions[2]) ;	// The docspecific permissions for this doc.

		// Now i'll loop through the db-results, and read the values
		// for each roles set_id this user may change from the form.
		// Then set the new value for each.

		Properties temp_permission_settings = new Properties() ;

		for ( int i = 0 ; i < role_permissions.length ; ++i ) {
		    String role_set_id_str = role_permissions[i][2] ; // Get the old set_id for this role from the db
		    int role_set_id = Integer.parseInt(role_set_id_str) ;
		    String role_id = role_permissions[i][0] ;                    // Get the role_id from the db
		    String new_set_id_str = req.getParameter("role_"+role_id) ;  // Check the value from the form
		    if ( new_set_id_str == null ) {  // If a new set_id for this role didn't come from the form
			continue ;							     // skip to the next role.
		    }
		    int new_set_id = Integer.parseInt(new_set_id_str) ;
		    if	( (
			   // May the user edit permissions at all?
			   user_set_id == 0				// If user has set_id == 0...
				|| (user_perm_set & 4) != 0)	// ...or the user may edit permissions for this document

			  // May the user set this particular permission-set?
			  && user_set_id <= new_set_id

			  // May the user edit the permissions for this particular role?
			  && user_set_id <= role_set_id	// If user potentially has a more privileged set_id than the role...
			  &&	(user_set_id != 1			// If user has set_id == 1 (that is , != 0 && != 2)
				 ||	(role_set_id != 2			// ...he may not change set_id for a role with set_id 2..
					 && new_set_id != 2)			// ...and he may not set set_id to 2 for any role...
				 || (currentdoc_perms&1) != 0// ...unless set_id 1 is more privileged than set_id 2 for this document.
				 )
			  ) {

				// We used to save to the db immediately. Now we do it a little bit differently to make it possible to store stuff in the session instead of the db.
				// IMCServiceRMI.sqlUpdateProcedure(imcserver, "SetRoleDocPermissionSetId "+role_id+","+meta_id+","+new_set_id) ;
			temp_permission_settings.setProperty(String.valueOf(role_id),String.valueOf(new_set_id)) ;
		    } else {
			log ("User "+user.getInt("user_id")+" with set_id "+user_set_id+" and permission_set "+user_perm_set+" was denied permission to change set_id for role "
			     +role_id+" from "+role_set_id+" to "+new_set_id+" on meta_id "+meta_id) ;
		    }
		}

		/*
		  Now we're going to start accepting the input form fields.
		  This table keeps track of all the fields we may encounter.
		  The "nullvalue" is there to support checkboxes, which,
		  if not checked, report null.
		  So, if the checkboxes do not appear, we know that we should enter
		  the "nullvalue" found here, into the db.
		*/
		// NOTE! This table matches the one below. Don't go changing one without changing the other.
		// FIXME: They should be merged into one table.
		String [] metatable = {
		/*  Nullable			Nullvalue */
		    "shared",	     "0",
		    "disable_search","0",
		    "archive",       "0",
		    "show_meta",     "0",
		    "permissions",   "0",
		    "meta_headline", null,
		    "meta_text",     null,
		    "meta_image",    null,
		    "activated_datetime",null,
		    "archived_datetime", null,
		    "frame_name",    null,
		    "target",	     null
		} ;

		final int metatable_cols = 2 ;

		// I'll make a table to keep track of
		// what is the least privileged (highest)
		// set_id you may have, to be able to change
		// each property. Roles with set_ids 1 and 2
		// still need explicit permissions, so this is
		// mainly for fleshing out what only a user with
		// "full" (0) may do. (Change whether set-id 1 is
		// more privileged. "permissions")
		// I use a bitmask here to specify what permissions
		// are required for each.
		// 0 == Unreachable
		// 1 == Something on the "simple docinfo"-page
		// 2 == Something on the "advanced docinfo"-page
		// 3 == 1|2
		// 4 == Something on the "rights/permissions"-page
		// 5 == 1|4
		// 6 == 2|4
		// 7 == 1|2|4

		// NOTE! This table matches the one above. Don't go changing one without changing the other.
		// FIXME: They should be merged into one table.
		int[] metatable_restrictions = {
		    //	set_id,	permission_bitmask
			2,	6,		//"shared",
			2,      2,		//"disable_search",
			2,      2,		//"archive",
			2,	6,		//"show_meta",
			0,	4,		//"permissions",
			2,	7,		//"meta_headline",
			2,	3,		//"meta_text",
			2,	3,		//"meta_image",
			2,	2,		//"activated_datetime",
			2,	2,		//"archived_datetime",
			2,	2,		//"frame_name",
			2,	2		//"target"
		} ;

		HashMap inputMap = new HashMap() ;
		// Loop through all meta-table-properties
		// Adding them to a HashMap to be used as input
		// That way i can mutilate the values before all the
		// permissions are checked.
		for ( int i=0 ; i<metatable.length ; i+=metatable_cols ) {
		    inputMap.put(metatable[i],req.getParameter(metatable[i])) ;
		}

		// Here's some mutilation!
		// activated_date and activated_time need to be merged, and likewise with archived_date and archived_time

		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm") ;

		String activated_date = req.getParameter("activated_date") ;
		String activated_time = req.getParameter("activated_time") ;
		String activated_datetime = null ;
		if ( activated_date != null && activated_time != null ) {
		    activated_datetime = activated_date + ' ' + activated_time ;
		    try {
			dateformat.parse(activated_datetime) ;
		    } catch (ParseException ex) {
			activated_datetime = null ;
		    }
		}

		String archived_date = req.getParameter("archived_date") ;
		String archived_time = req.getParameter("archived_time") ;
		String archived_datetime = null ;
		if ( archived_date != null && archived_time != null ) {
		    archived_datetime = archived_date + ' ' + archived_time ;
		    try {
			dateformat.parse(archived_datetime) ;
		    } catch (ParseException ex) {
			archived_datetime = null ;
		    }
		}

		// Loop through all meta-table-properties
		// Checking permissions as we go.
		// All alterations of the inputdata must happen before this
		for ( int i=0 ; i<metatable.length ; i+=metatable_cols ) {
			String tmp = (String)inputMap.get(metatable[i]) ;
			if (	user_set_id > metatable_restrictions[i]						// Check on set_id if user is allowed to set this particular property.
				||	(user_set_id > 0										// If user not has full access (0)...
				&& ((user_perm_set & metatable_restrictions[i+1]) == 0) // check permission-bitvector for the users set_id.
				)
				) {
				continue ;
			}
			if ( tmp != null) {
				metaprops.setProperty(metatable[i],tmp) ;	// If it is found, set it.
			} else {
				tmp = metatable[i+1] ;
								// FIXME: If it is null, that could mean the user
								// emptied the field. This leads to the property
								// not being updated, and left unchanged!
								// _Should_ be ok, since for checkboxes null is valid. (Means false)
								// For fields other than checkboxes and radiobuttons null would be bad.
				if ( tmp != null ) {
					metaprops.setProperty(metatable[i],tmp) ;	// If it is not found, set it to the nullvalue. (For checkboxes, which do not appear if they are not checked.)
				}
			}
		}
		//ok here we fetch the settings fore the default_template 1 & 2
		String temp_default_template_1 = req.getParameter("default_template_set_1") == null ? "-1" : req.getParameter("default_template_set_1");
		String temp_default_template_2 = req.getParameter("default_template_set_2") == null ? "-1" : req.getParameter("default_template_set_2");
		String[] temp_default_templates = {temp_default_template_1, temp_default_template_2};

		// Set modified-date to now...
		Date dt = IMCServiceRMI.getCurrentDate(imcserver) ;
		metaprops.setProperty("date_modified",dateformat.format(dt)) ;

		// It's like this... people make changes on the page, and then they forget to press "save"
		// before they press one of the "define-permission" buttons, and then their settings are lost.
		// I will fix this by storing the settings in a temporary variable in the user object.
		// This variable will be an array of four objects. In order:
		// A String, containing the meta-id of the page.
		// A Properties, containing the docinfo for the page. (db-column, value)
		// A Properties, containing the permission_sets for the roles. (role_id, set_id)
		// A String[], containing default_template 1 and 2
		// We also need a name for this temporary variable... i think i shall call it... (Drumroll, please...) "temp_perm_settings" !
		//

		if ( req.getParameter("define_set_1") != null ) {	// If user want's to edit permission-set 1
			user.put("temp_perm_settings",new Object[] {String.valueOf(meta_id),metaprops,temp_permission_settings,temp_default_templates}) ;
			out.print(MetaDataParser.parsePermissionSet(meta_id_int,user,host,1,false)) ;
			return ;
		} else if ( req.getParameter("define_set_2") != null ) {	// If user want's to edit permission-set 2
			user.put("temp_perm_settings",new Object[] {String.valueOf(meta_id),metaprops,temp_permission_settings,temp_default_templates}) ;
			out.print(MetaDataParser.parsePermissionSet(meta_id_int,user,host,2,false)) ;
			return ;
		} else if ( req.getParameter("define_new_set_1") != null ) {
			user.put("temp_perm_settings",new Object[] {String.valueOf(meta_id),metaprops,temp_permission_settings,temp_default_templates}) ;
			out.print(MetaDataParser.parsePermissionSet(meta_id_int,user,host,1,true)) ;
			return ;
		} else if ( req.getParameter("define_new_set_2") != null ) {
			user.put("temp_perm_settings",new Object[] {String.valueOf(meta_id),metaprops,temp_permission_settings,temp_default_templates}) ;
			out.print(MetaDataParser.parsePermissionSet(meta_id_int,user,host,2,true)) ;
			return ;
		} else if ( req.getParameter("add_roles") != null ) {		// The user wants to give permissions to roles that have none.
			String[] roles_no_rights = req.getParameterValues("roles_no_rights") ;
			for ( int i = 0 ; roles_no_rights!=null && i<roles_no_rights.length ; ++i ) {
				temp_permission_settings.setProperty(roles_no_rights[i],"3") ;
			}
			user.put("temp_perm_settings",new Object[] {String.valueOf(meta_id),metaprops,temp_permission_settings,temp_default_templates}) ;
			out.print(MetaDataParser.parseMetaPermission(meta_id,meta_id,user,host,"change_meta_rights.html")) ;
			return ;
		}


		// From now on we enter stuff into the db.

		// Here i'll construct an sql-query that will update all docinfo
		// the user is allowed to change.
		//ok lets start and get the default templates
		String tempStr = req.getParameter("default_template_set_1");
		String template1 = "-1";
		String template2 = "-1";
		if (tempStr != null)
		{
			template1 = req.getParameter("default_template_set_1").equals("") ? "-1" : req.getParameter("default_template_set_1");
		}
		tempStr = req.getParameter("default_template_set_2");
		if (tempStr != null)
		{
			template2 = req.getParameter("default_template_set_2").equals("")? "-1":req.getParameter("default_template_set_2");
		}
		String sqlStr = "activated_datetime = "+(null == activated_datetime ? "NULL" : "'"+activated_datetime+"'")+
		    ",archived_datetime = "+(null == archived_datetime ? "NULL" : "'"+archived_datetime+"'") + ",";

		Enumeration propkeys = metaprops.propertyNames() ;
		while ( propkeys.hasMoreElements() ) {
			String temp = (String)propkeys.nextElement() ;
			String val = metaprops.getProperty(temp) ;
			String [] vp = {
					"'",	"''"
				} ;
			sqlStr += temp +" = '"+Parser.parseDoc(val,vp)+"' " ;
			if ( propkeys.hasMoreElements() ) {
				sqlStr += ", " ;
			}
		}

		for (int i=0; i<role_permissions.length; ++i) {
			String role_id = (String)role_permissions[i][0] ;
			String new_set_id = temp_permission_settings.getProperty(role_id) ;
			if (new_set_id == null) {
			    continue ;
			}
			IMCServiceRMI.sqlUpdateProcedure(imcserver, "SetRoleDocPermissionSetId "+role_id+","+meta_id+","+new_set_id) ;
		}

		if ( sqlStr.length() > 0 ) {

			sqlStr = "update meta set " +sqlStr+ " where meta_id = "+meta_id ;
			IMCServiceRMI.sqlUpdateQuery(imcserver,sqlStr) ;
		}

		// Save the classifications to the db
		if ( classification != null ) {
			IMCServiceRMI.sqlUpdateProcedure(imcserver,"Classification_Fix "+meta_id+",'"+classification+"'") ;
		}

		//ok lets save the default templates
		//log("test sql: UpdateDefaultTemplates "+meta_id+",'"+template1+"','"+template2+"'");
		IMCServiceRMI.sqlUpdateProcedure(imcserver, "UpdateDefaultTemplates '"+meta_id+"','"+template1+"','"+template2+"'") ;


		// Update the date_modified for all parents.
		IMCServiceRMI.sqlUpdateProcedure(imcserver, "UpdateParentsDateModified "+meta_id) ;
		
		///**************** section index word stuff *****************
		//ok lets handle the the section stuff save to db and so on
		//lets start an see if we got any request to change the inherit one
		String section_id = req.getParameter("change_section");
		if (section_id == null) {
			//ok it vas null so lets try and get the inherit one
			section_id = req.getParameter("current_section_id");
		}
		//ok if we have one lets update the db
		if (section_id != null) {
			IMCServiceRMI.sqlUpdateProcedure(imcserver,"add_section_crossref " + meta_id +", " +section_id);
		}
		//**************** end section index word stuff *************

		// Let's split this joint!
		byte[] tempbytes = AdminDoc.adminDoc(meta_id_int,meta_id_int,host,user,req,res) ;
		if ( tempbytes != null ) {
		    out.write(tempbytes) ;
		}

		//lets log to mainlog that the user done stuff
		mainLog.info(logdateFormat.format(new java.util.Date())+"Metadata on ["+meta_id+"] updated by user: [" +user.getString("first_name").trim() + " " + user.getString("last_name").trim() + "]");

		return ;
	}

	public boolean contains (String[] array, String str) {
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

	/**
	Log function. Logs the message to the log file and console
	*/

	public void log(String msg) {
		super.log(msg) ;
		System.out.println("SaveMeta: " + msg) ;
	}


}
