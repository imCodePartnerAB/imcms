package com.imcode.imcms.servlet.superadmin;

import imcode.util.Html;
import imcode.external.diverse.VariableManager;
import imcode.server.ApplicationServer;
import imcode.server.IMCConstants;
import imcode.server.IMCServiceInterface;
import imcode.server.user.ImcmsAuthenticatorAndUserMapper;
import imcode.server.user.RoleDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import imcode.util.Html;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

public class AdminUserProps extends Administrator {

    private final static String HTML_RESPONSE = "AdminUserResp.htm";
    private final static String HTML_RESPONSE_ADMIN_PART = "AdminUserResp_admin_part.htm";
    private final static String HTML_RESPONSE_SUPERADMIN_PART = "AdminUserResp_superadmin_part.htm";

    private static Logger log = Logger.getLogger( AdminUserProps.class.getName() );
    private static final String REQUEST_PARAMETER__LOGIN_NAME = "login_name";
    private static final String REQUEST_PARAMETER__PASSWORD1 = "password1";
    private static final String REQUEST_PARAMETER__FIRST_NAME = "first_name";
    private static final String REQUEST_PARAMETER__LAST_NAME = "last_name";
    private static final String REQUEST_PARAMETER__TITLE = "title";
    private static final String REQUEST_PARAMETER__COMPANY = "company";
    private static final String REQUEST_PARAMETER__ADDRESS = "address";
    private static final String REQUEST_PARAMETER__CITY = "city";
    private static final String REQUEST_PARAMETER__ZIP = "zip";
    private static final String REQUEST_PARAMETER__COUNTRY = "country";
    private static final String REQUEST_PARAMETER__COUNTY_COUNCIL = "country_council";
    private static final String REQUEST_PARAMETER__EMAIL = "email";
    private static final String REQUEST_PARAMETER__LANGUAGE_ID = "lang_id";
    private static final String REQUEST_PARAMETER__ACTIVE = "active";
    private static final String REQUEST_PARAMETER__PASSWORD2 = "password2";
    private static final String REQUEST_PARAMETER__ROLES = "roles";

    public void doGet( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {

        // check if user is a Useradmin, adminRole = 2
        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();
        UserDomainObject user = Utility.getLoggedOnUser( req );

        HttpSession session = req.getSession( false );
        Properties tmp_userInfo = (Properties)session.getAttribute( "tempUser" );

        Vector tmp_phones = (Vector)session.getAttribute( "Ok_phoneNumbers" );
        if ( tmp_phones == null ) {
            tmp_phones = new Vector();
        }

        String[] phonetypesA = imcref.sqlProcedure( "GetPhonetypes", new String[]{"" + user.getLangId()} );

        // Get a new Vector:  phonetype_id, typename
        Vector phoneTypesV = new Vector( java.util.Arrays.asList( phonetypesA ) );

        if ( req.getParameter( "ADD_USER" ) != null ) {

            // Lets check if the user is an admin, otherwise throw him out.
            if ( !user.isSuperAdmin() && !user.isUserAdmin() ) {
                showErrorPageUserNotAnAdministrator( req, res, imcref, user );
                return;
            }

            showAddUserPage( tmp_userInfo, res, phoneTypesV, tmp_phones, user, imcref, req, session );
            return;
        }

        if ( req.getParameter( "CHANGE_USER" ) != null ) {
            log.debug( "Changeuser" );

            // lets first try to get userId from the session if we has been redirectet from authenticate
            String userToChangeId = getCurrentUserId( req, res, imcref, user );

            // Lets check if the user has right to do changes
            // only if he is an superadmin, useradmin or if he try to change his own values
            // otherwise throw him out.
            if ( !user.isSuperAdmin() && !user.isUserAdmin()
                 && !userToChangeId.equals( "" + user.getId() ) ) {
                showErrorPageUserHasNoRightsToChangeUserValues( req, res, imcref, user );
                return;
            }

            showChangeUserPage( userToChangeId, imcref, tmp_userInfo, tmp_phones, user, res, session, phoneTypesV, user.isSuperAdmin(), user.isUserAdmin(), req );
            return;
        }

    }

    private void showChangeUserPage( String userToChangeId, IMCServiceInterface imcref, Properties tmp_userInfo,
                                     Vector tmp_phones, UserDomainObject user, HttpServletResponse res,
                                     HttpSession session, Vector phoneTypesV, boolean superadmin, boolean useradmin,
                                     HttpServletRequest req ) throws IOException {
        // get a user object by userToChangeId
        UserDomainObject userToChange = null;
        if ( null != userToChangeId ) {
            userToChange = imcref.getImcmsAuthenticatorAndUserAndRoleMapper().getUser( Integer.parseInt( userToChangeId ) );
        }

        String login_name ;
        String password1 ;
        String new_pwd1 = "" ;   //hidden fildes
        String new_pwd2 = "";   //hidden fildes
        String first_name ;
        String last_name ;
        String title  ;
        String company ;
        String address ;
        String city ;
        String zip ;
        String country ;
        String country_council ;
        String email ;

        //Lets set values from session if we have any
        if ( tmp_userInfo != null ) {
            login_name = tmp_userInfo.getProperty( "login_name" );
            password1 = tmp_userInfo.getProperty( "password1" );
            new_pwd1 = tmp_userInfo.getProperty( "new_pwd1" ); //hidden fildes
            new_pwd2 = tmp_userInfo.getProperty( "new_pwd2" ); //hidden fildes
            first_name = tmp_userInfo.getProperty( "first_name" );
            last_name = tmp_userInfo.getProperty( "last_name" );
            title = tmp_userInfo.getProperty( "title" );
            company = tmp_userInfo.getProperty( "company" );

            address = tmp_userInfo.getProperty( "address" );
            city = tmp_userInfo.getProperty( "city" );
            zip = tmp_userInfo.getProperty( "zip" );
            country = tmp_userInfo.getProperty( "country" );
            country_council = tmp_userInfo.getProperty( "country_council" );
            email = tmp_userInfo.getProperty( "email" );

        } else {
            login_name = userToChange.getLoginName();
            password1 = userToChange.getPassword();

            first_name = userToChange.getFirstName();
            last_name = userToChange.getLastName();
            title = userToChange.getTitle();
            company = userToChange.getCompany();

            address = userToChange.getAddress();
            city = userToChange.getCity();
            zip = userToChange.getZip();
            country = userToChange.getCountry();
            country_council = userToChange.getCountyCouncil();
            email = userToChange.getEmailAddress();
        }


        // Lets get all users phone numbers from session if we have any else we get them from db
        // return value from db= phone_id, number, user_id, phonetype_id, typename
        String[][] phonesArr = imcref.sqlProcedureMulti( "GetUserPhoneNumbers", new String[]{
            "" + userToChange.getId()
        } );

        // Get a new Vector:  phone_id, number, user_id, phonetype_id, typename  ex. 10, 46 498 123456, 3, 1
        Vector phoneNumbers = getPhonesArrayVector( phonesArr );

        if ( tmp_phones.size() > 0 ) {
            phoneNumbers = tmp_phones;
        }

        // Get a new Vector: phone_id, (typename) number   ex.  { 10, (Hem) 46 498 123456 }
        Vector phonesV = this.getPhonesVector( phoneNumbers, "" + user.getLangId(), imcref );

        String selected = "";
        if ( phonesV.size() > 0 ) {
            selected = (String)phonesV.get( 0 );
        }

        //System.out.println("selected= " + selected);

        String phones = Html.createOptionList( selected, phonesV );

        Utility.setDefaultHtmlContentType( res ); // set content type
        Writer out = res.getWriter();

        Vector vec = new Vector();

        vec.add( "#CURR_USER_ID#" );
        vec.add( userToChangeId );
        vec.add( "#LOGIN_NAME#" );
        vec.add( login_name );


        // Lets fix the password string to show just ****
        vec.add( "#PWD1#" );
        vec.add( doPasswordString( password1 ) );
        vec.add( "#PWD2#" );
        vec.add( doPasswordString( password1 ) );
        vec.add( "#NEW_PWD1#" );
        vec.add( new_pwd1 ); 	//hidden fildes
        vec.add( "#NEW_PWD2#" );
        vec.add( new_pwd2 ); 	//hidden fildes

        vec.add( "#FIRST_NAME#" );
        vec.add( first_name );
        vec.add( "#LAST_NAME#" );
        vec.add( last_name );
        vec.add( "#TITLE#" );
        vec.add( title );
        vec.add( "#COMPANY#" );
        vec.add( company );
        vec.add( "#ADDRESS#" );
        vec.add( address );
        vec.add( "#ZIP#" );
        vec.add( zip );
        vec.add( "#CITY#" );
        vec.add( city );
        vec.add( "#COUNTRY_COUNCIL#" );
        vec.add( country_council );
        vec.add( "#COUNTRY#" );
        vec.add( country );
        vec.add( "#EMAIL#" );
        vec.add( email );

        vec.add( "#NEXT_URL#" );
        if ( null != session.getAttribute( "next_url" ) ) {
            vec.add( session.getAttribute( "next_url" ) );
        } else {
            vec.add( "" );
        }


        //add the phone nr fields
        vec.add( "#PHONE_ID#" );
        vec.add( "" );
        vec.add( "#COUNTRY_CODE#" );
        vec.add( "" );
        vec.add( "#AREA_CODE#" );
        vec.add( "" );
        vec.add( "#NUMBER#" );
        vec.add( "" );

        // phonetype list
        String phonetypes = Html.createOptionList( "1", phoneTypesV );
        vec.add( "#PHONETYPES_MENU#" );
        vec.add( phonetypes );

        vec.add( "#PHONES_MENU#" );
        vec.add( phones );


        // Lets add html for admin_part in AdminUserResp
        vec.add( "#ADMIN_PART#" );
        if ( superadmin || ( useradmin && user.getId() != userToChange.getId() ) ) {
            vec.add( createAdminPartHtml( user, userToChange, imcref, req, session ) );
        } else {
            vec.add( "" );
        }

        String adminTask = req.getParameter( "adminTask" );
        if ( adminTask == null ) {
            adminTask = "SAVE_CHANGED_USER";
        }
        vec.add( "#ADMIN_TASK#" );
        vec.add( adminTask );

        String languagesHtmlOptionList = getLanguagesHtmlOptionList( user, imcref, userToChange );
        vec.add( "#LANG_TYPES#" );
        vec.add( languagesHtmlOptionList );


        //store all data into the session
        session.setAttribute( "userToChange", userToChangeId );
        session.setAttribute( "Ok_phoneNumbers", phoneNumbers );

        // Lets renove session we dont need anymore.
        try {
            session.removeAttribute( "tempUserRoles" );
            session.removeAttribute( "tempUseradminRoles" );
            session.removeAttribute( "tempUser" );
            //session.getAttribute("Ok_phoneNumbers");

        } catch ( IllegalStateException ise ) {
            log.debug( "session has been invalidated so no need to remove parameters" );
        }

        String outputString = imcref.getAdminTemplate( HTML_RESPONSE, user, vec );
        out.write( outputString );
    }

    private String getLanguagesHtmlOptionList( UserDomainObject user, IMCServiceInterface imcref,
                                               UserDomainObject userToChange ) {
        // Lets get the the users language id
        String userLanguage = user.getLanguageIso639_2();
        String[] langList = imcref.sqlProcedure( "GetLanguageList", new String[]{userLanguage} );
        Vector selectedLangV = new Vector();
        selectedLangV.add( "" + userToChange.getLangId() );
        String languagesHtmlOptionList = Html.createOptionList( new Vector( Arrays.asList( langList ) ), selectedLangV );
        return languagesHtmlOptionList;
    }

    private void showErrorPageUserHasNoRightsToChangeUserValues( HttpServletRequest req, HttpServletResponse res,
                                                                 IMCServiceInterface imcref, UserDomainObject user ) throws IOException {
        String header = "Error in AdminUserProps. ";
        Properties langproperties = imcref.getLanguageProperties( user );
        String msg = langproperties.getProperty( "error/servlet/AdminUser/user_have_no_permission" ) + "<br>";
        log.debug( header + "- user have no permission to change user values" );
        new AdminError( req, res, header, msg );
    }

    private void showAddUserPage( Properties tmp_userInfo, HttpServletResponse res, Vector phoneTypesV,
                                  Vector tmp_phones, UserDomainObject user, IMCServiceInterface imcref,
                                  HttpServletRequest req, HttpSession session ) throws IOException {
        String login_name = "";
        String password1 = "";
        String password2 = "";
        String new_pwd1 = "";   //hidden fildes
        String new_pwd2 = "";   //hidden fildes
        String first_name = "";
        String last_name = "";
        String title = "";
        String company = "";
        String address = "";
        String city = "";
        String zip = "";
        String country = "";
        String country_council = "";
        String email = "";

        //Lets set values from session if we have any
        if ( tmp_userInfo != null ) {
            login_name = tmp_userInfo.getProperty( "login_name" );
            password1 = tmp_userInfo.getProperty( "password1" );
            password2 = tmp_userInfo.getProperty( "password2" );
            new_pwd1 = tmp_userInfo.getProperty( "new_pwd1" ); //hidden fildes
            new_pwd2 = tmp_userInfo.getProperty( "new_pwd2" ); //hidden fildes
            first_name = tmp_userInfo.getProperty( "first_name" );
            last_name = tmp_userInfo.getProperty( "last_name" );
            title = tmp_userInfo.getProperty( "title" );
            company = tmp_userInfo.getProperty( "company" );

            address = tmp_userInfo.getProperty( "address" );
            city = tmp_userInfo.getProperty( "city" );
            zip = tmp_userInfo.getProperty( "zip" );
            country = tmp_userInfo.getProperty( "country" );
            country_council = tmp_userInfo.getProperty( "country_council" );
            email = tmp_userInfo.getProperty( "email" );
        }

        Vector vec = new Vector();		// hold tags and values to parse html page
        Utility.setDefaultHtmlContentType( res ); // set content type
        Writer out = res.getWriter();	// to write out html page

        //	VariableManager vm = new VariableManager() ;

        vec.add( "#LOGIN_NAME#" );
        vec.add( login_name );
        vec.add( "#PWD1#" );
        vec.add( password1 );
        vec.add( "#PWD2#" );
        vec.add( password2 );

        vec.add( "#NEW_PWD1#" );
        vec.add( new_pwd1 ); //hidden fildes
        vec.add( "#NEW_PWD2#" );
        vec.add( new_pwd2 ); //hidden fildes

        vec.add( "#FIRST_NAME#" );
        vec.add( first_name );
        vec.add( "#LAST_NAME#" );
        vec.add( last_name );
        vec.add( "#TITLE#" );
        vec.add( title );
        vec.add( "#COMPANY#" );
        vec.add( company );

        vec.add( "#ADDRESS#" );
        vec.add( address );
        vec.add( "#CITY#" );
        vec.add( city );
        vec.add( "#ZIP#" );
        vec.add( zip );
        vec.add( "#COUNTRY#" );
        vec.add( country );
        vec.add( "#COUNTRY_COUNCIL#" );
        vec.add( country_council );
        vec.add( "#EMAIL#" );
        vec.add( email );

        vec.add( "#ADMIN_TASK#" );
        vec.add( "ADD_USER" );

        vec.add( "#PHONE_ID#" );
        vec.add( "" );
        vec.add( "#NUMBER#" );
        vec.add( "" );

        // phonetype list
        String phonetypes = Html.createOptionList( "1", phoneTypesV );
        vec.add( "#PHONETYPES_MENU#" );
        vec.add( phonetypes );

        // Lets add html for admin_part in AdminUserResp
        vec.add( "#ADMIN_PART#" );
        vec.add( createAdminPartHtml( user, null, imcref, req, session ) );

        String languagesHtmlOptionList = getLanguagesHtmlOptionList( user, imcref, user );
        vec.add( "#LANG_TYPES#" );
        vec.add( languagesHtmlOptionList );

        //store all data into the session
        session.setAttribute( "Ok_phoneNumbers", tmp_phones );

        // Lets create the HTML page
        String outputString = imcref.getAdminTemplate( HTML_RESPONSE, user, vec );
        out.write( outputString );
    }

    private void showErrorPageUserNotAnAdministrator( HttpServletRequest req, HttpServletResponse res,
                                                      IMCServiceInterface imcref, UserDomainObject user ) throws IOException {
        String header = "Error in AdminUserProps. ";
        Properties langproperties = imcref.getLanguageProperties( user );
        String msg = langproperties.getProperty( "error/servlet/global/no_administrator" ) + "<br>";
        log.debug( header + "- user is not an administrator" );
        new AdminError( req, res, header, msg );
    }

    public void doPost( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {

        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();

        HttpSession session = req.getSession( false );
        if ( session == null ) {
            return;
        }

        // check if user is a Superadmin, adminRole = 1
        UserDomainObject user = Utility.getLoggedOnUser( req );

        boolean isAdmin = ( user.isSuperAdmin() || user.isUserAdmin() );


        // Lets check adminTask
        String adminTask = req.getParameter( "adminTask" );
        if ( adminTask == null ) {
            adminTask = "";
        }


        // Lets get the user which should be changed if we is not in ADD_USER mode
        String userToChangeIdStr = null;

        // if we are processing a user template then userToChange is equal to user
        if ( req.getParameter( "userTemplate" ) != null && "SAVE_CHANGED_USER".equals( adminTask ) ) {
            userToChangeIdStr = "" + user.getId();
        }
        // if we are processing a admin template
        if ( req.getParameter( "userTemplate" ) == null && !"ADD_USER".equals( adminTask ) ) {
            userToChangeIdStr = getCurrentUserId( req, res, imcref, user );
        }

        // get a user object by userToChangeId
        UserDomainObject userFromRequest = this.getUserFromRequest( req );
        imcode.server.user.UserDomainObject userToChange = null;
        if ( null != userToChangeIdStr ) {
            int userToChangeId = Integer.parseInt( userToChangeIdStr );
            userToChange = imcref.getImcmsAuthenticatorAndUserAndRoleMapper().getUser( userToChangeId );
            userFromRequest.setId( userToChange.getId() );
            if ( !user.isSuperAdmin() ) {
                userFromRequest.setActive( userToChange.isActive() );
            }
        }

        String password2 = req.getParameter( REQUEST_PARAMETER__PASSWORD2 );

        // Lets get all phonetypes from db
        String[] phonetypesA = imcref.sqlProcedure( "GetPhonetypes", new String[]{"" + user.getLangId()} );

        // Get a new Vector:  phonetype_id, typename
        Vector phoneTypesV = new Vector( java.util.Arrays.asList( phonetypesA ) );

        // Lets get all Userinformation and add it to html page

        VariableManager vm = new VariableManager();

        vm.addProperty( "LOGIN_NAME", userFromRequest.getLoginName() );
        vm.addProperty( "FIRST_NAME", userFromRequest.getFirstName() );
        vm.addProperty( "LAST_NAME", userFromRequest.getLastName() );
        vm.addProperty( "TITLE", userFromRequest.getTitle() );
        vm.addProperty( "COMPANY", userFromRequest.getCompany() );
        vm.addProperty( "ADDRESS", userFromRequest.getAddress() );
        vm.addProperty( "CITY", userFromRequest.getCity() );
        vm.addProperty( "ZIP", userFromRequest.getZip() );
        vm.addProperty( "COUNTRY", userFromRequest.getCountry() );
        vm.addProperty( "COUNTRY_COUNCIL", userFromRequest.getCountyCouncil() );
        vm.addProperty( "EMAIL", userFromRequest.getEmailAddress() );
        vm.addProperty( "LANG_TYPES", getLanguagesHtmlOptionList( user, imcref, userFromRequest ) );

        if ( null != req.getParameter( "useradmin_settings" ) ) {

            String[] theUserRoles = req.getParameterValues( "roles" );
            String[] theUseradminRoles = req.getParameterValues( "useradmin_roles" );

            if ( null != theUserRoles ) {
                session.setAttribute( "tempUserRoles", theUserRoles );
            }
            if ( null != theUseradminRoles ) {
                session.setAttribute( "tempUseradminRoles", theUseradminRoles );
            }
            session.setAttribute( "tempUser", userFromRequest );

            res.sendRedirect( "AdminUserUseradminSettings" );
            return;

        }

        //sets up the needed parameters and redirect back to AdminUserProps
        if ( req.getParameter( "RESET_FORM" ) != null ) {

            if ( adminTask.equals( "ADD_USER" ) ) {
                res.sendRedirect( "AdminUserProps?ADD_USER=true&adminTask=" + adminTask );
            } else if ( adminTask.equals( "SAVE_CHANGED_USER" ) ) {
                res.sendRedirect( "AdminUserProps?CHANGE_USER=true&adminTask=" + adminTask );
            }
            return;
        }

        if ( req.getParameter( "ok_phones" ) != null || req.getParameter( "delete_phones" ) != null
             || req.getParameter( "edit_phones" ) != null ) {

            phoneHandling( adminTask, user, userToChangeIdStr, imcref, req, res, session, vm, phoneTypesV, userFromRequest, password2, userToChange );
            return;

        }

        if ( req.getParameter( "SAVE_USER" ) != null && adminTask.equalsIgnoreCase( "ADD_USER" ) ) {
            addUser( session, req, userFromRequest, password2, res, imcref, user );
            return;
        }

        if ( req.getParameter( "SAVE_USER" ) != null && adminTask.equalsIgnoreCase( "SAVE_CHANGED_USER" ) ) {
            changeExistingUser( session, userToChange, isAdmin, user, Integer.parseInt( userToChangeIdStr ), imcref, req, res, userFromRequest, password2 );
            return;
        }

        if ( req.getParameter( "CANCEL" ) != null ) {

            String url = "AdminUser"; // default if we are processing a admin template

            if ( null != session.getAttribute( "next_url" ) ) {
                url = (String)session.getAttribute( "next_url" );
            }

            this.removeSessionParams( req );
            res.sendRedirect( url );
            return;
        }

        log.debug( "Unidentified argument was sent!" );
        doGet( req, res );
    } // end HTTP POST

    private void changeExistingUser( HttpSession session, UserDomainObject userFromDatabase,
                                     boolean admin, UserDomainObject user, int userToChangeId,
                                     IMCServiceInterface imcref, HttpServletRequest req, HttpServletResponse res,
                                     UserDomainObject userFromRequest, String password2 ) throws IOException {
        log.debug( "SAVE EXISTING USER TO DB" );

        if ( session == null ) {
            return;
        }

        // Lets check that we have a user to be changed.
        if ( userFromDatabase == null ) {
            return;
        }

        // Lets check if the user is an admin or if he is going to change his own data, otherwise throw him out.
        if ( !admin && user.getId() != userToChangeId ) {
            String header = "Error in AdminUserProps. ";
            Properties langproperties = imcref.getLanguageProperties( user );
            String msg = langproperties.getProperty( "error/servlet/AdminUserProps/user_has_no_admin_permission" )
                         + "<br>";
            new AdminError( req, res, header, msg );
            return;
        }

        /* Lets check if loginname is going to be changed and if so,
           lets check that the new loginname don't already exists in db
        */
        String currentLogin = userFromDatabase.getLoginName();
        Properties langproperties = imcref.getLanguageProperties( user );
        String msg = langproperties.getProperty( "error/servlet/AdminUserProps/username_already_exists" ) + "<br>";
        String newLogin;
        if ( null != req.getParameter( "login_name" ) ) {
            newLogin = userFromRequest.getLoginName();
        } else {
            // we are processing data from a user template where users login name will be the same
            // like his email. And then, if users current login name not is equal to his current email
            // we dont make any changes on his login name,  only on his email.

            newLogin = req.getParameter( "email" );
            if ( !userFromDatabase.getEmailAddress().equalsIgnoreCase( currentLogin ) ) {
                newLogin = currentLogin;
            }
            msg = langproperties.getProperty( "error/servlet/AdminUserProps/username_or_email_already_exists" ) + "<br>";
        }

        // check that the changed login name don´t already exists
        if ( !newLogin.equalsIgnoreCase( currentLogin ) ) {
            ImcmsAuthenticatorAndUserMapper userMapper = ApplicationServer.getIMCServiceInterface().getImcmsAuthenticatorAndUserAndRoleMapper();
            if ( null != userMapper.getUser( newLogin ) ) {
                String header = "Error in AdminUserProps.";
                log.debug( header + "- username already exists" );
                new AdminError( req, res, header, msg );
                return;
            }
        }

        userFromRequest.setLoginName( newLogin );

        //lets get the current password for user
        String currPwd = userFromDatabase.getPassword();
        if ( currPwd.equals( "" ) ) {
            String header = "Error in AdminUserProps ";
            msg = langproperties.getProperty( "error/servlet/AdminUserProps/password_missing" ) + "<br>";
            log.debug( header + "- password is missing" );
            new AdminError( req, res, header, msg );
            log.debug( "innan return i currPwd.equals" );
            return;
        }

        // Lets check the password. if its empty, then it wont be updated. get the
        // old password from db and use that one instad

        String newPwd = userFromRequest.getPassword();
        boolean isChanged = false;
        for ( int i = 0; i < newPwd.length(); i++ ) {
            if ( newPwd.charAt( i ) != '*' ) {
                isChanged = true;
            }
        }
        if ( !isChanged ) {
            userFromRequest.setPassword( currPwd );
            password2 = currPwd;
        }

        // when user has add a phone number in admin interface,
        // we have to get the password from NEW_PWD1 parameter
        if ( null != req.getParameter( "new_pwd1" ) && !( "" ).equals( req.getParameter( "new_pwd1" ) ) ) {
            userFromRequest.setPassword( req.getParameter( "new_pwd1" ) );
        }

        // If we are processing data from a user template and user is going to change his password
        // lets check if old password is valid ( if we have got any value from html-page)
        if ( req.getParameter( "password_current" ) != null && isChanged ) {
            if ( !currPwd.equals( req.getParameter( "password_current" ) ) ) {
                String header = "Error in AdminUserProps ";
                msg = langproperties.getProperty( "error/servlet/AdminUserProps/verify_old_password" ) + "<br>";
                log.debug( header + "- could not verify old password " );
                new AdminError( req, res, header, msg );
                log.debug( "innan return i currPwd.equals" );
                return;
            }

        }

        // Lets validate the password
        if ( !verifyPassword( userFromRequest.getPassword(), password2, req, res ) ) {
            return;
        }

        if ( !this.validateParameters( req, res, user ) ) {
            return;
        }

        //Lets get phonenumbers from the session if we have a session Attribute
        Vector phonesV = (Vector)session.getAttribute( "Ok_phoneNumbers" );


        // save phone number
        //if we are processing data from a admin template
        if ( null == req.getParameter( "userTemplate" ) ) {

            //save phone numbers from phonesV  ( phonesV : id, number, user_id, phonetype_id )
            if ( null != phonesV && phonesV.size() > 0 ) {

                updateUserPhones(userFromRequest, phonesV);
            }

        } else {
            // We are processing data from a user template
            // Get all phone numbers for user
            String[][] phoneNbr = imcref.sqlProcedureMulti( "GetUserPhoneNumbers", new String[]{"" + userToChangeId} );

            // Get workPhoneId and mobilePhoneId
            String workPhoneId = "";
            String mobilePhoneId = "";
            if ( phoneNbr != null ) {
                for ( int i = 0; i < phoneNbr.length; i++ ) {
                    if ( ( "2" ).equals( phoneNbr[i][3] ) ) {
                        workPhoneId = phoneNbr[i][0];
                    }
                    if ( ( "3" ).equals( phoneNbr[i][3] ) ) {
                        mobilePhoneId = phoneNbr[i][0];
                    }
                }
            }

            String workPhone = req.getParameter( "workphone" );
            String mobilePhone = req.getParameter( "mobilephone" );
            // set new workphone
            if ( null != workPhoneId ) {
                userFromRequest.setWorkPhone(workPhone);

            }

            // set new mobilephone
            if ( null !=  mobilePhoneId )  {
                userFromRequest.setMobilePhone(mobilePhone);
            }
        }

        ImcmsAuthenticatorAndUserMapper imcmsAuthenticatorAndUserAndRoleMapper = imcref.getImcmsAuthenticatorAndUserAndRoleMapper();

        // if we are processing data from a admin template and
        // if user isSuperadmin or
        // isUseradmin and not is going to change his own data
        // then we have to take care of userroles
        if ( null == req.getParameter( "userTemplate" )
             && ( ( user.isSuperAdmin() || user.isUserAdmin() && user.getId() != userFromDatabase.getId() ) ) ) {

            // Lets get the roles from htmlpage
            int[] roleIdsFromRequest = this.getRoleIdsFromRequest( "roles", req, res, imcref, user );

            // Lets add the new users roles. but first, delete users current Roles
            // and then add the new ones

            if ( user.isSuperAdmin() ) { // delete all userroles
                int roleId = -1;
                imcref.sqlUpdateProcedure( "DelUserRoles", new String[]{"" + userToChangeId, "" + roleId} );

            } else {  // delete only roles that the useradmin has permission to administrate
                String[] rolesArr = imcref.sqlProcedure( "GetUseradminPermissibleRoles", new String[]{
                    "" + user.getId()
                } );
                for ( int i = 0; i < rolesArr.length; i += 2 ) {
                    imcref.sqlUpdateProcedure( "DelUserRoles", new String[]{"" + userToChangeId, rolesArr[i]} );
                }
            }
            boolean useradminRoleIsSelected = false;
            for ( int i = 0; i < roleIdsFromRequest.length; i++ ) {
                int roleId = roleIdsFromRequest[i];
                RoleDomainObject role = imcmsAuthenticatorAndUserAndRoleMapper.getRoleById( roleId );
                userFromRequest.addRole( role );
                if ( role.equals( RoleDomainObject.USERADMIN ) ) {
                    useradminRoleIsSelected = true;
                }
            }


            // Lets add the new useradmin roles. but first, delete the current roles
            // and then add the new ones
            // but only if role Useradmin is selected

            imcref.sqlUpdateProcedure( "DeleteUseradminPermissibleRoles", new String[]{"" + userToChangeId} );

            if ( useradminRoleIsSelected ) {

                // Lets get the useradmin_roles from htmlpage
                int[] useradminRolesV = this.getRoleIdsFromRequest( "useradmin_roles", req, res, imcref, user );

                // Lets add the new useradmin roles.
                addUserAdminRoles( imcref, userToChangeId, useradminRolesV );
            }
        }

        imcmsAuthenticatorAndUserAndRoleMapper.updateUser( userFromRequest.getLoginName(), userFromRequest );

        this.goNext( req, res, session );
    }

    private void addUser( HttpSession session, HttpServletRequest req, UserDomainObject userFromRequest,
                          String password2, HttpServletResponse res, IMCServiceInterface imcref, UserDomainObject user ) throws IOException {
        log.debug( "Lets add a new user to db" );

        //get session
        if ( session == null ) {
            return;
        }

        // if user has add a phone number we have to get the password from NEW_PWD1 parameter
        if ( StringUtils.isNotEmpty( req.getParameter( "new_pwd1" ) ) ) {
            userFromRequest.setPassword( req.getParameter( "new_pwd1" ) );
            password2 = req.getParameter( "new_pwd2" );
        }

        int[] roleIdsFromRequest = this.getRoleIdsFromRequest( "roles", req, res, imcref, user );

        // Lets validate the password
        if ( !verifyPassword( userFromRequest.getPassword(), password2, req, res ) ) {
            return;
        }

        // Lets check that the new username doesnt exists already in db
        String userName;
        Properties langproperties = imcref.getLanguageProperties( user );
        String msg = langproperties.getProperty( "error/servlet/AdminUserProps/username_already_exists" ) + "<br>";
        if ( null != req.getParameter( "login_name" ) ) {
            userFromRequest.getLoginName();
        } else {
            userName = req.getParameter( "email" );
            userFromRequest.setLoginName( userName );
            msg = langproperties.getProperty( "error/servlet/AdminUserProps/username_or_email_already_exists" )
                  + "<br>";
        }

        ImcmsAuthenticatorAndUserMapper imcmsAuthenticatorAndUserAndRoleMapper = imcref.getImcmsAuthenticatorAndUserAndRoleMapper();
        if ( null != imcmsAuthenticatorAndUserAndRoleMapper.getUser( userFromRequest.getLoginName() ) ) {
            String header = "Error in AdminUserProps. ";
            log.debug( header + "- username already exists" );
            new AdminError( req, res, header, msg );
            return;
        }

        if ( !validateParameters( req, res, user ) ) {
            return;
        }

        //Lets get phonenumbers from the session if we have a session Attribute
        Vector phonesV = (Vector)session.getAttribute( "Ok_phoneNumbers" );

        boolean useradminRoleIsSelected = false;
        for ( int i = 0; i < roleIdsFromRequest.length; i++ ) {
            int roleId = roleIdsFromRequest[i];
            RoleDomainObject role = imcmsAuthenticatorAndUserAndRoleMapper.getRoleById( roleId );
            userFromRequest.addRole( role );
            if ( role.equals( RoleDomainObject.USERADMIN ) ) {
                useradminRoleIsSelected = true;
            }
        }

        imcmsAuthenticatorAndUserAndRoleMapper.addUser( userFromRequest );

        if ( useradminRoleIsSelected ) {
            // Lets get the useradmin_roles from htmlpage
            int[] userAdminRoleIdsFromRequest = this.getRoleIdsFromRequest( "useradmin_roles", req, res, imcref, user );
            // Lets add the new useradmin roles.
            addUserAdminRoles( imcref, userFromRequest.getId(), userAdminRoleIdsFromRequest );
        }



        // save phone number
        //if we are processing data from a admin template
        if ( null == req.getParameter( "userTemplate" ) ) {
            //save phone number from phonesV  ( phonesV : id, number, user_id, phonetype_id )
            if ( null != phonesV && phonesV.size() > 0 ) {
                for ( int i = 0; i < phonesV.size(); i++ ) {
                    String[] aPhone = (String[])phonesV.elementAt( i );

                    imcref.sqlUpdateProcedure( "PhoneNbrAdd", new String[]{
                        "" + userFromRequest.getId(), aPhone[1], aPhone[3]
                    } );
                }
            }
            // we are processing data from a user template
        } else {

            String workPhone = req.getParameter( "workphone" );
            String mobilePhone = req.getParameter( "mobilephone" );

            if ( !( "" ).equals( workPhone ) ) {
                int phoneNumberType = 2;
                ImcmsAuthenticatorAndUserMapper.staticSprocPhoneNbrAdd( imcref, userFromRequest.getId(), workPhone, phoneNumberType );
            }
            if ( !( "" ).equals( mobilePhone ) ) {
                int phoneNumberType = 3;
                ImcmsAuthenticatorAndUserMapper.staticSprocPhoneNbrAdd( imcref, userFromRequest.getId(), workPhone, phoneNumberType );
            }
        }

        //if we are processing data from a user template we have to login the new user
        if ( null != req.getParameter( "userTemplate" ) ) {
            String nexturl = "VerifyUser?name=" + userFromRequest.getLoginName();
            nexturl += "&passwd=" + userFromRequest.getPassword();

            if ( null != req.getParameter( "next_meta" ) ) {
                nexturl += "&next_meta=" + req.getParameter( "next_meta" );

            } else if ( null != req.getParameter( "next_url" ) ) {
                nexturl += "&next_url=" + req.getParameter( "next_url" );
            }
            res.sendRedirect( nexturl );
            return;

        } else {
            this.goNext( req, res, session );
        }
        return;
    }

    private void phoneHandling( String adminTask, UserDomainObject user, String userToChangeId,
                                IMCServiceInterface imcref, HttpServletRequest req, HttpServletResponse res,
                                HttpSession session, VariableManager vm, Vector phoneTypesV,
                                UserDomainObject userFromRequest, String password2,
                                UserDomainObject userToChange ) throws IOException {
        if ( adminTask == null ) {
            adminTask = "";
        }

        if ( user.isSuperAdmin() == false && !user.isUserAdmin()
             && !userToChangeId.equals( "" + user.getId() ) ) {
            String header = "Error in AdminUserProps.";
            Properties langproperties = imcref.getLanguageProperties( user );
            String msg = langproperties.getProperty( "error/servlet/AdminUser/user_have_no_permission" ) + "<br>";
            log.debug( header + "- user have no permission to change user values" );
            new AdminError( req, res, header, msg );
            return;
        }



        // Lets get all phonenumbers from the session
        // Get a new Vector:  phone_id, number, user_id, phonetype_id, typename  ex. 10, 46 498 123456, 3, 1, Hem
        Vector phoneNumbers = ( session.getAttribute( "Ok_phoneNumbers" ) != null )
                              ? (Vector)session.getAttribute( "Ok_phoneNumbers" ) : new Vector();

        log.debug( "test" + req.getParameter( "edit_phones" ) );

        String selectedPhoneId ;



        //******** OK_PHONES BUTTON WAS PRESSED **********
        if ( req.getParameter( "ok_phones" ) != null ) { //adds or changes a phoneNr to the select list

            log.debug( "ok_phones in doPost" );

            //lets get the phonenumber from the form and add it into Vectorn phonesV

            boolean found = false;  // marker that we is going to edit a selected phone number
            int tempId = 1;  // temporary phone id

            Enumeration enum = phoneNumbers.elements();

            while ( enum.hasMoreElements() ) {
                String[] temp = (String[])enum.nextElement();
                if ( temp[0].equals( req.getParameter( "phone_id" ) ) ) {
                    selectedPhoneId = temp[0];
                    phoneNumbers.remove( temp );
                    temp[1] = req.getParameter( "local_code" );
                    temp[3] = req.getParameter( "phonetype" );
                    phoneNumbers.addElement( temp );
                    found = true;
                }
                try {
                    if ( Integer.parseInt( temp[0] ) >= tempId ) {
                        tempId = Integer.parseInt( temp[0] ) + 1;
                    }

                } catch ( NumberFormatException ignored ) {
                    // ignored
                }

            }

            if ( !found ) {
                String[] temp = new String[5];
                temp[0] = "" + tempId;
                selectedPhoneId = temp[0];
                temp[1] = req.getParameter( "local_code" );
                temp[2] = userToChangeId;
                temp[3] = req.getParameter( "phonetype" );
                phoneNumbers.addElement( temp );
            }

        }
        // ********* end ok_phones *************************


        //********* EDIT_PHONES BUTTON WAS PRESSED ***********

        boolean found = false;
        String phonetypes_id = "";

        if ( req.getParameter( "edit_phones" ) != null ) {
            log.debug( "edit_phones" );

            Enumeration enum = phoneNumbers.elements();

            while ( enum.hasMoreElements() && !found ) {
                String[] temp = (String[])enum.nextElement();
                if ( temp[0].equals( req.getParameter( "user_phones" ) ) ) {
                    vm.addProperty( "PHONE_ID", temp[0] );
                    vm.addProperty( "NUMBER", temp[1] );
                    phonetypes_id = temp[3];
                    found = true;
                }
            }
        }

        if ( !found ) {
            vm.addProperty( "PHONE_ID", "" );
            vm.addProperty( "NUMBER", "" );
            phonetypes_id = "1";
        }

        // phonetype list
        String phonetypes = Html.createOptionList( phonetypes_id, phoneTypesV );
        vm.addProperty( "PHONETYPES_MENU", phonetypes );

        selectedPhoneId = req.getParameter( "user_phones" );
        log.debug( "Number: " + selectedPhoneId );

        //*********end edit_phones***********


        //****************DELETE_PHONES BUTTON WAS PRESSED ************

        if ( req.getParameter( "delete_phones" ) != null ) {

            log.debug( "lets delete_phones from templist" );

            Enumeration enum = phoneNumbers.elements();
            found = false;
            //		log("Size"+phoneNumbers.size());
            while ( enum.hasMoreElements() && !found ) {
                String[] temp = (String[])enum.nextElement();
                log.debug( temp[0] + " == " + req.getParameter( "user_phones" ) );
                if ( temp[0].equals( req.getParameter( "user_phones" ) ) ) {
                    phoneNumbers.remove( temp );
                    found = true;
                }
            }

            if ( phoneNumbers.size() > 0 ) {
                String[] temp = (String[])phoneNumbers.firstElement();
                selectedPhoneId = temp[0];
            }
        }

        // ******** end delete_phones ***************


        String newPwd = userFromRequest.getPassword();
        boolean isChanged = false;
        for ( int i = 0; i < newPwd.length(); i++ ) {
            if ( newPwd.charAt( i ) != ( "*" ).charAt( 0 ) ) {
                isChanged = true;
            }
        }
        if ( isChanged ) {
            //update hidden fields whith new password
            vm.addProperty( "NEW_PWD1", userFromRequest.getPassword() );
            vm.addProperty( "NEW_PWD2", password2 );
        } else {
            vm.addProperty( "NEW_PWD1", "" );
            vm.addProperty( "NEW_PWD2", "" );
        }

        //update password fields with just ****
        vm.addProperty( "PWD1", doPasswordString( userFromRequest.getPassword() ) );
        vm.addProperty( "PWD2", doPasswordString( password2 ) );

        // Lets add html for admin_part in AdminUserResp
        if ( user.isSuperAdmin() || ( user.isUserAdmin() && !userToChangeId.equals( "" + user.getId() ) ) ) {

            vm.addProperty( "ADMIN_PART", createAdminPartHtml( user, userToChange, imcref, req, session ) );

        } else {
            vm.addProperty( "ADMIN_PART", "" );
        }

        // Get a new Vector: phone_id, (typename) number    ex. { 10, (Hem) 46 498 123456 }
        Vector phonesV = this.getPhonesVector( phoneNumbers, "" + user.getLangId(), imcref );

        if ( phonesV == null ) {
            this.sendErrorMsg( req, res, "Add/edit user", "An eror occured!" );
            return;
        }

        // update Vector phonesV from Vector phonesNumber: phone_id, (typename) number   ex.  { 10, (Hem) 46 498 123456 }
        phonesV = this.getPhonesVector( phoneNumbers, "" + user.getLangId(), imcref );

        // add phones list
        String phones = Html.createOptionList( selectedPhoneId, phonesV );
        log.debug( "phones stringen: " + phones );
        vm.addProperty( "PHONES_MENU", phones );

        vm.addProperty( "ADMIN_TASK", adminTask );
        vm.addProperty( "CURR_USER_ID", ( userToChangeId == null ? "" : userToChangeId ) );

        session.setAttribute( "Ok_phoneNumbers", phoneNumbers );

        this.sendHtml( req, res, vm, HTML_RESPONSE );
        return;
    }

    private void addUserAdminRoles( IMCServiceInterface imcref, int userIdToAddUserAdminRolesTo,
                                    int[] useradminRoleIds ) {
        ImcmsAuthenticatorAndUserMapper imcmsAuthenticatorAndUserMapper = imcref.getImcmsAuthenticatorAndUserAndRoleMapper();
        for ( int i = 0; i < useradminRoleIds.length; i++ ) {
            int roleId = useradminRoleIds[i];
            RoleDomainObject role = imcmsAuthenticatorAndUserMapper.getRoleById( roleId );
            if ( !RoleDomainObject.SUPERADMIN.equals( role ) && !RoleDomainObject.USERADMIN.equals( role ) ) {
                imcref.sqlUpdateProcedure( "AddUseradminPermissibleRoles", new String[]{
                    "" + userIdToAddUserAdminRolesTo, "" + role.getId()
                } );
            }
        }
    }

    /**
     * Removes temporary parameters from the session
     */
    private void removeSessionParams( HttpServletRequest req ) {
        HttpSession session = req.getSession( false );
        if ( session == null ) {
            return;
        }
        try {
            session.removeAttribute( "Ok_phoneNumbers" );
            session.removeAttribute( "userToChange" );
            session.removeAttribute( "tempRRUserData" );
            session.removeAttribute( "next_url" );
            session.removeAttribute( "tempUser" );
            session.removeAttribute( "tempUserRoles" );
            session.removeAttribute( "tempUseradminRoles" );
        } catch ( IllegalStateException ise ) {
            log.debug( "session has been invalidated so no need to remove parameters" );
        }
    }

    /**
     * a error page will be generated, fore those times the user uses the backstep in
     * the browser
     */
    private void sendErrorMsg( HttpServletRequest req, HttpServletResponse res, String header, String msg ) throws IOException {
        new AdminError( req, res, header, msg );
    }

    /**
     * Returns a Vector, containing the choosed roles from the html page. if Something
     * failes, a error page will be generated and null will be returned.
     */
    private int[] getRoleIdsFromRequest( String name, HttpServletRequest req, HttpServletResponse res,
                                         IMCServiceInterface imcref, UserDomainObject user ) throws IOException {
        // Lets get the roles
        String[] roleIdStrings = ( req.getParameterValues( name ) == null )
                                 ? new String[0] : ( req.getParameterValues( name ) );

        Vector rolesV = new Vector( java.util.Arrays.asList( roleIdStrings ) );
        if ( rolesV.size() == 0 && name.equals( "roles" ) ) { // user must get at least one user role
            String header = "Error in AdminUserProps ";
            Properties langproperties = imcref.getLanguageProperties( user );
            String msg = langproperties.getProperty( "error/servlet/AdminUserProps/no_role_selected" ) + "<br>";
            log.debug( header + "- no role selected" );
            new AdminError( req, res, header, msg );
            return null;
        }
        int[] roleIds = new int[roleIdStrings.length];
        for ( int i = 0; i < roleIdStrings.length; i++ ) {
            String roleIdString = roleIdStrings[i];
            roleIds[i] = Integer.parseInt( roleIdString );
        }
        return roleIds;
    }

    /**
     * Redirect to next Url
     */
    private void goNext( HttpServletRequest req, HttpServletResponse res, HttpSession session ) throws IOException {
        String nexturl = "AdminUser";  // default if we are processing a admin template

        if ( null != req.getParameter( "next_url" ) ) {
            nexturl = req.getParameter( "next_url" );
        }

        // lets session-object override req-object
        if ( null != session.getAttribute( "next_url" ) ) {
            nexturl = (String)session.getAttribute( "next_url" );
        }

        // lets next_meta override next_url if we got both
        if ( null != req.getParameter( "next_meta" ) ) {
            nexturl = "GetDoc?meta_id=" + req.getParameter( "next_meta" );
        }

        this.removeSessionParams( req );

        res.sendRedirect( nexturl );
    }

    private UserDomainObject getUserFromRequest( HttpServletRequest req ) {

        UserDomainObject userFromRequest = new UserDomainObject();
        userFromRequest.setLoginName( req.getParameter( REQUEST_PARAMETER__LOGIN_NAME ) );
        userFromRequest.setPassword( req.getParameter( REQUEST_PARAMETER__PASSWORD1 ) );
        userFromRequest.setFirstName( req.getParameter( REQUEST_PARAMETER__FIRST_NAME ) );
        userFromRequest.setLastName( req.getParameter( REQUEST_PARAMETER__LAST_NAME ) );
        userFromRequest.setTitle( req.getParameter( REQUEST_PARAMETER__TITLE ) );
        userFromRequest.setCompany( req.getParameter( REQUEST_PARAMETER__COMPANY ) );
        userFromRequest.setAddress( req.getParameter( REQUEST_PARAMETER__ADDRESS ) );
        userFromRequest.setCity( req.getParameter( REQUEST_PARAMETER__CITY ) );
        userFromRequest.setZip( req.getParameter( REQUEST_PARAMETER__ZIP ) );
        userFromRequest.setCountry( req.getParameter( REQUEST_PARAMETER__COUNTRY ) );
        userFromRequest.setCountyCouncil( req.getParameter( REQUEST_PARAMETER__COUNTY_COUNCIL ) );
        userFromRequest.setEmailAddress( req.getParameter( REQUEST_PARAMETER__EMAIL ) );
        userFromRequest.setLangId( Integer.parseInt( req.getParameter( REQUEST_PARAMETER__LANGUAGE_ID ) ) );
        userFromRequest.setActive( null != req.getParameter( REQUEST_PARAMETER__ACTIVE ) );

        return userFromRequest;
    }

    /**
     * Returns a Properties, containing the user information from the html page. if Something
     * failes, a error page will be generated and null will be returned.
     */

    private boolean validateParameters( HttpServletRequest req, HttpServletResponse res,
                                        UserDomainObject user ) throws IOException {

        if ( !assertRequiredFieldsFilledIn( req ) ) {

            String header = "Error in AdminUserProps ";
            Properties langproperties = ApplicationServer.getIMCServiceInterface().getLanguageProperties( user );
            String msg = langproperties.getProperty( "error/servlet/AdminUserProps/vaidate_form_parameters" ) + "<br>";
            log.debug( header + "Error in checkingparameters" );
            new AdminError( req, res, header, msg );
            return false;
        }
        return true;

    } // end validateParameters

    private boolean assertRequiredFieldsFilledIn( HttpServletRequest req ) {
        String[] requiredFields = {
            REQUEST_PARAMETER__LOGIN_NAME,
            REQUEST_PARAMETER__FIRST_NAME,
            REQUEST_PARAMETER__LAST_NAME,
            REQUEST_PARAMETER__PASSWORD1,
            REQUEST_PARAMETER__PASSWORD2,
        };
        for ( int i = 0; i < requiredFields.length; i++ ) {
            String requiredField = requiredFields[i];
            String requiredFieldValue = req.getParameter( requiredField );
            if ( StringUtils.isBlank( requiredFieldValue ) ) {
                return false;
            }
        }

        // Only validate roles if request contains a parameter "roles", when user is edit his own properties it don't.
        String[] rolesParameterValues = req.getParameterValues( REQUEST_PARAMETER__ROLES );
        if ( null != rolesParameterValues && 0 == rolesParameterValues.length ) {
            return false;
        }

        return true;
    }

    /**
     * adds the phoneNrId to the vector followed by the phoneNumber
     * return a new Vector with elements formated like
     * ( phone_id, number, user_id, phonetype_id, typename   ex. { 10, 46 498 123456, 3, 1 } )
     */
    private Vector getPhonesArrayVector( String[][] phoneNr ) {
        Vector phonesArrV = new Vector();

        for ( int i = 0; i < phoneNr.length; i++ ) {
            phonesArrV.addElement( phoneNr[i] );
        }
        return phonesArrV;
    }

    /**
     * return a new Vector with elements formated like
     * ( phone_id, (typename) number,   ex. { 10, (Hem) 46 498 123456 } )
     * input vector  ex. 10, 46 498 123456, 3, 1
     */
    private Vector getPhonesVector( Vector phonesArrV, String lang_id, IMCServiceInterface imcref ) {

        Vector phonesV = new Vector();
        Enumeration enum = phonesArrV.elements();
        while ( enum.hasMoreElements() ) {
            String[] tempPhone = (String[])enum.nextElement();
            String[] typename = imcref.sqlProcedure( "GetPhonetypeName", new String[]{tempPhone[3], lang_id} );
            String temp = "(" + typename[0] + ") " + tempPhone[1];

            phonesV.addElement( tempPhone[0] );
            phonesV.addElement( temp );
        }
        return phonesV;
    }

    // Create html for admin_part in AdminUserResp
    private String createAdminPartHtml( UserDomainObject user, UserDomainObject userToChange,
                                        IMCServiceInterface imcref, HttpServletRequest req, HttpSession session ) {

        String html_admin_part;
        Vector vec_admin_part = new Vector();

        String[] userRoles = null;
        String[] useradminRoles = null;
        Properties userInfo = null;

        Vector userRolesV ;
        String rolesMenuStr;

        Vector useradminRolesV ;
        String rolesMenuUseradminStr = "";

        // check if user is a Superadmin, adminRole = 1

        if ( null != session.getAttribute( "tempUser" ) ) {

            //Lets get temporary values from session if there is some.
            userRoles = (String[])session.getAttribute( "tempUserRoles" );
            useradminRoles = (String[])session.getAttribute( "tempUseradminRoles" );
            userInfo = (Properties)session.getAttribute( "tempUser" );
        }

        // Lets get ROLES from DB
        String[] rolesArr ;

        if ( user.isSuperAdmin() ) {
            rolesArr = imcref.sqlProcedure( "GetAllRoles", new String[0] );
        } else {
            rolesArr = imcref.sqlProcedure( "GetUseradminPermissibleRoles", new String[]{"" + user.getId()} );
        }
        for ( int i = 0; i < rolesArr.length; i++ ) {
            rolesArr[i] = rolesArr[i].trim();
        }
        Vector allRolesV = new Vector( java.util.Arrays.asList( rolesArr ) );

        //Lets get all ROLES from DB except of Useradmin and Superadmin
        Vector rolesV = (Vector)allRolesV.clone();

        ListIterator listiter = rolesV.listIterator();
        while ( listiter.hasNext() ) {
            listiter.next();
            String rolename = listiter.next().toString();
            if ( "Superadmin".equalsIgnoreCase( rolename ) || "Useradmin".equalsIgnoreCase( rolename ) ) {
                listiter.remove();
                listiter.previous();
                listiter.remove();
            }
        }

        if ( userToChange == null ) {   // ADD_USER mode

            // Lets get the information for users roles and put them in a vector
            // if we don´t have got any roles from session we try to get them from request object
            if ( userRoles == null ) {
                userRoles = ( req.getParameterValues( "roles" ) == null )
                            ? new String[0] : req.getParameterValues( "roles" );
            }
            userRolesV = new Vector( java.util.Arrays.asList( userRoles ) );

            // Lets create html option for user roles
            rolesMenuStr = Html.createOptionList( allRolesV, userRolesV );

            if ( user.isSuperAdmin() ) {
                // Lets get the information for usersadmin roles and put them in a vector
                // if we don´t have got any roles from session we try to get them from request object
                if ( useradminRoles == null ) {
                    useradminRoles = ( req.getParameterValues( "useradmin_roles" ) == null )
                                     ? new String[0] : req.getParameterValues( "useradmin_roles" );
                }
                useradminRolesV = new Vector( java.util.Arrays.asList( useradminRoles ) );

                // Lets create html option for useradmin roles
                rolesMenuUseradminStr = Html.createOptionList( rolesV, useradminRolesV );
            }



            // Lets get the active flag from the session if we have any
            String active = "1";
            if ( userInfo != null ) {
                active = userInfo.getProperty( "active" );
            }

            vec_admin_part.add( "#ACTIVE#" );
            vec_admin_part.add( "1" );
            vec_admin_part.add( "#ACTIVE_FLAG#" );
            if ( active.equals( "1" ) ) {
                vec_admin_part.add( "checked" );
            } else {
                vec_admin_part.add( "" );
            }

            vec_admin_part.add( "#USER_CREATE_DATE#" );
            vec_admin_part.add( "&nbsp;" );
            vec_admin_part.add( "#ROLES_MENU#" );
            vec_admin_part.add( rolesMenuStr );
            vec_admin_part.add( "#ROLES_MENU_USERADMIN#" );
            if ( user.isSuperAdmin() ) {
                vec_admin_part.add( rolesMenuUseradminStr );
            } else {
                vec_admin_part.add( "" );
            }

        } else {   // CHANGE_USER mode

            String active = "";

            // if OK_PHONES or DELETE_PHONES or EDIT_PHONES  was pressed we have to get values from req object
            if ( req.getParameter( "ok_phones" ) != null || req.getParameter( "delete_phones" ) != null
                 || req.getParameter( "edit_phones" ) != null ) {

                if ( ( "1" ).equals( req.getParameter( "active" ) ) ) {
                    active = "1";
                }

                userRoles = ( req.getParameterValues( "roles" ) == null )
                            ? new String[0] : req.getParameterValues( "roles" );
                useradminRoles = ( req.getParameterValues( "useradmin_roles" ) == null )
                                 ? new String[0] : req.getParameterValues( "useradmin_roles" );

            } else {

                // Lets get the information for users roles and put them in a vector
                // if we don´t have got any roles from session we try to get them from DB
                if ( userRoles == null ) {
                    userRoles = imcref.sqlProcedure( "GetUserRolesIds", new String[]{"" + userToChange.getId()} );
                }

                if ( user.isSuperAdmin() ) {
                    // Lets get the information for usersadmin roles and put them in a vector
                    // if we don´t have got any roles from session we try to get them from DB
                    if ( useradminRoles == null ) {
                        useradminRoles = imcref.sqlProcedure( "GetUseradminPermissibleRoles", new String[]{
                            "" + userToChange.getId()
                        } );
                    }
                }

                active = ( userToChange.isActive() ) ? "1" : "0";
            }


            // Lets put the user roles in the vector
            userRolesV = new Vector( java.util.Arrays.asList( userRoles ) );

            // Lets create html option for user roles
            rolesMenuStr = Html.createOptionList( allRolesV, userRolesV );

            if ( user.isSuperAdmin() ) {
                // Lets put the roles that useradmin is allow to administrate in a vector
                useradminRolesV = new Vector( java.util.Arrays.asList( useradminRoles ) );

                // Lets create html option for useradmin roles
                rolesMenuUseradminStr = Html.createOptionList( rolesV, useradminRolesV );
            }

            vec_admin_part.add( "#ACTIVE#" );
            vec_admin_part.add( "1" );
            vec_admin_part.add( "#ACTIVE_FLAG#" );
            if ( ( "1" ).equals( active ) ) {
                vec_admin_part.add( "checked" );
            } else {
                vec_admin_part.add( "" );
            }

            vec_admin_part.add( "#USER_CREATE_DATE#" );
            vec_admin_part.add( userToChange.getCreateDate() );
            vec_admin_part.add( "#ROLES_MENU#" );
            vec_admin_part.add( rolesMenuStr );
            vec_admin_part.add( "#ROLES_MENU_USERADMIN#" );
            if ( user.isSuperAdmin() ) {
                vec_admin_part.add( rolesMenuUseradminStr );
            } else {
                vec_admin_part.add( "" );
            }

        }

        // lets parse and return the html_admin_part
        if ( user.isSuperAdmin() ) {
            html_admin_part = imcref.getAdminTemplate( HTML_RESPONSE_SUPERADMIN_PART, user, vec_admin_part );
        } else {
            html_admin_part = imcref.getAdminTemplate( HTML_RESPONSE_ADMIN_PART, user, vec_admin_part );
        }
        return html_admin_part;

    }

    /**
     * Returns a String, containing the userID in the request object.If something failes,
     * a error page will be generated and null will be returned.
     */

    private String getCurrentUserId( HttpServletRequest req, HttpServletResponse res, IMCServiceInterface imcref,
                                     UserDomainObject user ) throws IOException {

        String userId = req.getParameter( "CURR_USER_ID" );

        // Get the session
        HttpSession session = req.getSession( false );

        if ( userId == null ) {
            // Lets get the userId from the Session Object.
            userId = (String)session.getAttribute( "userToChange" );
        }

        if ( userId == null ) {
            String header = "Error in AdminUserProps ";
            Properties langproperties = imcref.getLanguageProperties( user );
            String msg = langproperties.getProperty( "error/servlet/AdminUser/user_to_change_id_missing" ) + "<br>";
            log.debug( header + "- user id for user to change was missing" );
            new AdminError( req, res, header, msg );
            return null;
        } else {
            log.debug( "userToChangeId =  " + userId );
        }
        return userId;
    } // End getCurrentUserId

    private String doPasswordString( String pwd ) {
        // Lets fix the password string
        int len = pwd.length();
        pwd = "";
        for ( int i = 0; i < len; i++ ) {
            pwd += "*";
        }
        return pwd;
    }

    /**
     * Validates the password. Password must contain at least 4 characters
     * Generates an errorpage and returns false if something goes wrong
     */

    public static boolean verifyPassword( String password1, String password2, HttpServletRequest req,
                                          HttpServletResponse res ) throws IOException {

        String header ;

        if ( !password1.equals( password2 ) ) {
            header = req.getServletPath();
            new AdminError2( req, res, header, 52 );
            return false;
        }

        if ( password1.length() < IMCConstants.PASSWORD_MINIMUM_LENGTH ) {
            header = req.getServletPath();
            new AdminError2( req, res, header, 53 );
            return false;
        }

        return true;

    } // End verifyPassword

    /**
     * Creates a sql parameter array used to run sproc updateUserPhones
     */
    public static String[] extractUpdateUserSprocParametersFromProperties( Properties props ) {

        Logger log = Logger.getLogger( AdminUserProps.class );
        log.debug( "extractUpdateUserSprocParametersFromProperties + props: " + props.toString() );

        String[] params = {
            props.getProperty( "user_id" ), ( props.getProperty( "login_name" ) ).trim(),
            ( props.getProperty( "password1" ) ).trim(), ( props.getProperty( "first_name" ) ).trim(),
            ( props.getProperty( "last_name" ) ).trim(), ( props.getProperty( "title" ) ).trim(),
            ( props.getProperty( "company" ) ).trim(), ( props.getProperty( "address" ) ).trim(),
            ( props.getProperty( "city" ) ).trim(), ( props.getProperty( "zip" ) ).trim(),
            ( props.getProperty( "country" ) ).trim(), ( props.getProperty( "country_council" ) ).trim(),
            ( props.getProperty( "email" ) ).trim(), "0", "1001", "0", props.getProperty( "lang_id" ),
            props.getProperty( "active" )
        };

        return params;
    }

    private void updateUserPhones(UserDomainObject userToChange, Vector phonesV){
        final int PHONE_TYPE_OTHER_PHONE = 0;
        final int PHONE_TYPE_HOME_PHONE = 1;
        final int PHONE_TYPE_WORK_PHONE = 2;
        final int PHONE_TYPE_WORK_MOBILE = 3;
        final int PHONE_TYPE_FAX_PHONE = 4;


        userToChange.setOtherPhone("");
        userToChange.setHomePhone("");
        userToChange.setWorkPhone("");
        userToChange.setFaxPhone("");
        userToChange.setMobilePhone("");


        for ( int i = 0; i < phonesV.size(); i++ ) {
            String[] aPhone = (String[])phonesV.elementAt( i );

            switch(Integer.parseInt(aPhone[3])){

                case PHONE_TYPE_OTHER_PHONE:
                   userToChange.setOtherPhone(aPhone[1]);
                   break;
                case PHONE_TYPE_HOME_PHONE:
                    userToChange.setHomePhone(aPhone[1]);
                    break;
                case PHONE_TYPE_WORK_PHONE:
                    userToChange.setWorkPhone(aPhone[1]);
                    break;
                case PHONE_TYPE_WORK_MOBILE:
                    userToChange.setMobilePhone(aPhone[1]);
                    break;
                case PHONE_TYPE_FAX_PHONE:
                    userToChange.setFaxPhone(aPhone[1]);
                    break;
            }
        }
    }

}
