/*
 *
 * @(#)AdminRoleBelongings.java
 *
 *
 * 2000-10-09
 *
 * Copyright (c)
 *
 */

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import imcode.external.diverse.VariableManager;
import imcode.external.diverse.MetaInfo;
import imcode.util.Utility;
import imcode.util.IMCServiceRMI;

import imcode.server.* ;

/**
 * Takes care of administration of users by roles.
 *
 * Documents in use:
 *
 * doGet()
 * AdminRoleBelongings.html, start document
 *
 * doPost():
 * AdminRoleBelongings_edit.html, page for administrate users to roles
 * AdminRoleBelongings_activate.html, page for de/activate users
 *
 * Data in webserver config:
 * servermaster_email=abc@test.com
 *
 * stored procedures in use:
 * - RemoveUserFromRole
 * - RoleAdminGetAll
 * - RoleGetAllApartFromRole
 * - GetAllUsersInList
 * - GetUsersWhoBelongsToRole
 * - AddUserRole
 * - ChangeUserActiveStatus
 * - GetLangPrefixFromId
 *
 * @version 1.3 17 Oct 2000
 * @author Jerker Drottenmyr
 */

public class AdminRoleBelongings extends Administrator {
    private final static String CVS_REV = "$Revision$" ;
    private final static String CVS_DATE = "$Date$" ;

    private static final String HTML_ADMIN_ROLE_BELONGING = "AdminRoleBelongings.html";
    private static final String HTML_ADMIN_ROLE_BELONGING_EDIT = "AdminRoleBelongings_edit.html";
    private static final String HTML_ADMIN_ROLE_BELONGING_ACTIVATE = "AdminRoleBelongings_activate.html";
    private static final String HTML_ADMIN_ROLE_BELONGING_ERROR = "Error.html";

    /**
     * The GET method creates the html page when this side has been
     * redirected from somewhere else.
     */
    public void doGet(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {

	IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface(req) ;

	// Lets validate the session
	if ( super.checkSession( req, res ) == false ) {
	    return ;
	}

	// Lets get an user object
	imcode.server.user.UserDomainObject user = super.getUserObj(req,res) ;

	if(user == null) {

	    return ;
	}

	// Lets verify that the user who tries to add a new user is an admin
	if (imcref.checkAdminRights(user) == false) {

	    return ;
	}

	// Lets get all ROLES from DB
	String sqlQ = "RoleAdminGetAll";
	String[][] queryResult = imcref.sqlQueryMulti( sqlQ );
	log( "SQL: " + sqlQ );

	// Lets generate the html page
	String optionList = createListOfOptions( queryResult );

	VariableManager vm = new VariableManager();
	vm.addProperty("ROLES_MENU", optionList  );

	this.sendHtml( req, res, vm, AdminRoleBelongings.HTML_ADMIN_ROLE_BELONGING );
	return;
    }


    /**
     * POST
     **/
    public void doPost(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {

	IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface(req) ;

	String host = req.getHeader("host") ;
	String eMailServerMaster = Utility.getDomainPref( "servermaster_email", host );

	// Lets validate the session
	if (super.checkSession(req,res) == false) return ;

	// Lets get an user object
	imcode.server.user.UserDomainObject user = super.getUserObj(req,res) ;

	// lets get ready for errors
	String languagePrefix = user.getLangPrefix() ;
	String errorHeader = "AdminRoleBelongings";

	if(user == null) {
	    String header = "Error in AdminRoleBelongings." ;
	    String msg = "Couldnt create an user object."+ "<BR>" ;
	    this.log(header + msg) ;
	    AdminError err = new AdminError(req,res,header,msg) ;
	    return ;
	}

	// Lets check if the user is an admin, otherwise throw him out.
	if (imcref.checkAdminRights(user) == false) {
	    String header = "Error in AdminRoleBelongings." ;
	    String msg = "The user is not an administrator."+ "<BR>" ;
	    this.log(header + msg) ;
	    AdminError err = new AdminError(req,res,header,msg) ;

	    return ;
	}

	// *************** RETURN TO ADMIN ROLES *****************
	if( req.getParameter("CANCEL") != null) {
	    res.sendRedirect("AdminRoles") ;
	    return ;
	}

	// *************** RETURN TO ADMINMANAGER *****************
	if( req.getParameter("BELONGING_CANCEL") != null) {
	    doGet(req,res);
	}

	// *************** GENERATE THE USER BELONGING TO ROLE PAGE *****************
	if( req.getParameter("VIEW_USER_BELONGING_ROLE") != null) {

	    String roleId = req.getParameter("ROLE_ID") ;

	    if (roleId == null) {

		// no role choisen
		sendErrorMessage( imcref, eMailServerMaster, languagePrefix , errorHeader, 100, res );

		return ;
	    }

	    String userOptionListTag = getUserOptionListTag( roleId, imcref );

	    // Lets get all ROLES from DB

	    String curentRoleId = roleId;
	    try {
		Integer.parseInt( curentRoleId );
	    } catch ( NumberFormatException e ) {
		curentRoleId = "0";
	    }

	    String sqlQ = "RoleGetAllApartFromRole " + curentRoleId;
	    String[][] roleQueryResult = imcref.sqlQueryMulti( sqlQ );
	    log( "SQL: " + sqlQ );

	    String roleOptionList = createListOfOptions( roleQueryResult );
	    String curentRoleName = getRoleName( roleId, imcref );

	    // Lets generate the html page
	    VariableManager vm = new VariableManager();
	    vm.addProperty( "CURENT_ROLE_ID", roleId );
	    vm.addProperty( "CURENT_ROLE_NAME", curentRoleName );
	    vm.addProperty( "USER_MENU", userOptionListTag  );
	    vm.addProperty( "ROLES_MENU", roleOptionList );

	    this.sendHtml( req, res, vm, AdminRoleBelongings.HTML_ADMIN_ROLE_BELONGING_EDIT );

	    return;
	}

	// *************** GENERATE THE USER DE/ACTIVATE TO ROLE PAGE *****************
	if( req.getParameter("VIEW_USER_ACTIVATE") != null) {

	    String roleId = req.getParameter("ROLE_ID");

	    if (roleId == null) {

		// no role choisen
		sendErrorMessage( imcref, eMailServerMaster, languagePrefix, errorHeader, 100, res );

		return ;
	    }

	    String userOptionListTag = getUserOptionListTag( roleId, imcref );
	    String curentRoleName = getRoleName( roleId, imcref );

	    //Lets generate the html page
	    VariableManager vm = new VariableManager();
	    vm.addProperty( "CURENT_ROLE_ID", roleId );
	    vm.addProperty( "CURENT_ROLE_NAME", curentRoleName );
	    vm.addProperty( "USER_MENU", userOptionListTag  );

	    this.sendHtml( req, res, vm, AdminRoleBelongings.HTML_ADMIN_ROLE_BELONGING_ACTIVATE );

	    return;
	}

	// *************** REMOVE ROLE FROM USERS  **********
	if( req.getParameter( "BELONGING_REMOVE_ROLE" ) != null ) {
	    String curentRoleId = req.getParameter("CURENT_ROLE_ID");
	    String[] userIds = req.getParameterValues( "USER_ID" );

	    if ( curentRoleId == null || userIds == null ) {

		// no role choisen or/and no users
		sendErrorMessage( imcref, eMailServerMaster, languagePrefix, errorHeader, 101, res );
		return ;
	    }

	    for ( int i = 0 ; i < userIds.length ; i++ ) {
		removeUserFromRole( userIds[i], curentRoleId, imcref );
	    }

	    doGet(req,res);
	}

	// *************** ADD ROLE TO USERS  **********
	if( req.getParameter( "BELONGING_ADD_ROLE" ) != null ) {
	    String roleId = req.getParameter("ROLE_ID");
	    String[] userIds = req.getParameterValues( "USER_ID" );

	    if ( roleId == null || userIds == null ) {

		// no role choisen or/and no users
		sendErrorMessage( imcref, eMailServerMaster, languagePrefix, errorHeader, 102, res );
		return ;
	    }

	    for ( int i = 0 ; i < userIds.length ; i++ ) {

		addUserToRole( userIds[i], roleId, imcref );
	    }

	    doGet(req,res);
	}

	// *************** CHANGE ROLE x TO y FOR USERS  **********
	if( req.getParameter( "BELONGING_MOVE_ROLE" ) != null ) {
	    String roleId = req.getParameter("ROLE_ID");
	    String curentRoleId = req.getParameter("CURENT_ROLE_ID");
	    String[] userIds = req.getParameterValues( "USER_ID" );

	    if ( roleId == null || userIds == null || curentRoleId == null ) {
		// no role choisen or/and no users
		sendErrorMessage( imcref, eMailServerMaster, languagePrefix, errorHeader, 102, res );

		return ;
	    }

	    for ( int i = 0 ; i < userIds.length ; i++ ) {

		// remove old role
		removeUserFromRole( userIds[i], curentRoleId, imcref );
		addUserToRole( userIds[i], roleId, imcref );
	    }

	    doGet(req,res);
	}

	// *************** DEACTIVATE USERS  **********
	if( req.getParameter( "BELONGING_DEACTIVATE" ) != null ) {
	    String[] userIds = req.getParameterValues( "USER_ID" );

	    if ( userIds == null ) {
		// no user choisen
		sendErrorMessage( imcref, eMailServerMaster, languagePrefix, errorHeader, 101, res );
		return ;
	    }

	    for ( int i = 0 ; i < userIds.length ; i++ ) {
		setUsersActive( userIds[i], "0", imcref );
	    }

	    doGet(req,res);
	}

	// *************** ACTIVATE USERS  **********
	if( req.getParameter( "BELONGING_ACTIVATE" ) != null ) {
	    String[] userIds = req.getParameterValues( "USER_ID" );

	    if ( userIds == null ) {
		// no user choisen
		sendErrorMessage( imcref, eMailServerMaster, languagePrefix, errorHeader, 101, res );
		return ;
	    }

	    for ( int i = 0 ; i < userIds.length ; i++ ) {
		setUsersActive( userIds[i], "1", imcref );
	    }

	    doGet(req,res);
	}
    }


    /**
     * init
     */
    public void init(ServletConfig config) throws ServletException {
	super.init(config);
    }

    /**
     * creats list of options.
     * param options must be in order name, value, (selected)
     */
    protected String createListOfOptions( String[][] options ) {

	StringBuffer optionList = new StringBuffer();

	for ( int i = 0 ; i < options.length ; i++ ) {
	    boolean selected = options[i].length == 3;

	    optionList.append( createOption( options[i][0], options[i][1], selected ) );
	}

	return optionList.toString();
    }

    /**
     * creats list of options.
     * param options must be in order value, name
     */
    protected String createListOfOptions( String[][] options, boolean selected ) {

	StringBuffer optionList = new StringBuffer();

	for ( int i = 0 ; i < options.length ; i++ ) {

	    optionList.append( createOption( options[i][0], options[i][1], selected ) );
	}

	return optionList.toString();
    }


    /**
     * creats option.
     *
     */
    protected String createOption( String elementValue, String elementName, boolean selected ) {
	StringBuffer option = new StringBuffer();

	option.append( "<option value=\"" + elementValue + "\"" );
	if ( selected ) {
	    option.append( " selected" );
	}
	option.append( ">" + elementName + "</option>");

	return option.toString();
    }

    private void addUserToRole( String userId, String roleId, IMCServiceInterface imcref )
	throws IOException {
	// lets be certain that the update process works ( avoid error then row alredy exist )
	removeUserFromRole( userId, roleId, imcref );

	String sqlD = "AddUserRole " + userId + ", " + roleId;
	imcref.sqlUpdateQuery( sqlD );
	log( "SQL: " + sqlD );
    }

    private void removeUserFromRole( String userId, String roleId, IMCServiceInterface imcref )
	throws IOException {

	String sqlD = "RemoveUserFromRole " + userId + ", " + roleId;
	imcref.sqlUpdateQuery( sqlD );
	log( "SQL: " + sqlD );
    }

    private String getUserOptionListTag( String roleId, IMCServiceInterface imcref )
	throws IOException {
	String sqlQ = null;
	String[][] userQueryResult = null;
	String userOptionList = null;

	// lets get all user or users who has role role_id
	if ( roleId.equals( "ALL_USERS" ) ) {
	    sqlQ = "GetAllUsersInList";
	    userQueryResult = imcref.sqlQueryMulti( sqlQ );
	    userOptionList = createListOfOptions( userQueryResult );

	} else {

	    sqlQ = "GetUsersWhoBelongsToRole " + roleId;
	    userQueryResult = imcref.sqlQueryMulti( sqlQ );
	    userOptionList = createListOfOptions( userQueryResult, true );
	}
	log( "SQL: " + sqlQ );

	return userOptionList;
    }

    /**
     * returns name for roll or empty if all
     */
    private String getRoleName( String roleId, IMCServiceInterface imcref ) throws IOException {

	String roleName = "";

	if ( !( roleId.equalsIgnoreCase( "ALL_USERS" ) ) ) {
	    roleName = imcref.sqlProcedureStr( "RoleGetName " + roleId );
	}

	return roleName;
    }

    private void setUsersActive( String userId, String state, IMCServiceInterface imcref )
	throws IOException {

	String sqlD = "ChangeUserActiveStatus " + userId + ", " + state;
	imcref.sqlUpdateQuery( sqlD );
    }
}
