package com.imcode.imcms.servlet.conference;

import com.imcode.imcms.servlet.superadmin.AdminError2;
import com.imcode.imcms.servlet.superadmin.AdminUserProps;
import imcode.util.Html;
import imcode.external.diverse.MetaInfo;
import imcode.external.diverse.VariableManager;
import imcode.server.ApplicationServer;
import imcode.server.IMCServiceInterface;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentMapper;
import imcode.server.user.UserDomainObject;
import imcode.server.user.ImcmsAuthenticatorAndUserMapper;
import imcode.util.Utility;
import imcode.util.Html;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Properties;
import java.util.Vector;

/**
 * The class used to generate login pages, and administrate users page
 * <pre>
 * TEMPLATES: The following html files and fragments are used by this servlet.
 * <p/>
 * Conf_admin_user.htm : Used to generate a selection list of users
 * Conf_admin_user_resp.htm : Used to administrate a user
 * Conf_Login.htm : Html file used to prompt the user for username / password (usermode)
 * Conf_Add_User.htm : Html file used to add a new user (adminmode)
 * Conf_Login_Error.htm : Html file used to generate a login failure. (adminmode)
 * </pre>
 *
 * @author Rickard Larsson
 * @version 1.0
 *          Date : 2000-06-16
 */

public class ConfLogin extends Conference {

    private final static String USER_UNADMIN_LINK_TEMPLATE = "conf_user_unadmin_link.htm";

    private String LOGIN_HTML = "conf_login.htm";	   // The login page
    private String CREATE_HTML = "conf_add_user.htm";   // The create new user page
    private String LOGIN_ERROR_HTML = "conf_login_error.htm";  // The error
    // page used for specialized messages to user
    private String ADMIN1_HTML = "conf_admin_user.htm";
    private String ADMIN2_HTML = "conf_admin_user_resp.htm";
    private String ADD_USER_OK_HTML = "conf_login_add_ok.htm";

    /**
     * <pre>
     * Generates html pages used to handle users. Login, add new users, administrate users
     * <p/>
     * PARAMETERS:
     * login_type : Flag used to generate a html page. Default is no value at all.
     * If the value is missing, a login page will be generated. Case-insensitive. </LI>
     * <p/>
     * Expected values
     * ADMIN_USER : Generates html page used to administrate users.
     * ADD_USER : Generates html page used to add new users
     * <p/>
     * Example: /ConfLogin?login_type=ADMIN_USER
     * <p/>
     * TAGS:
     * #USERS_MENU# : Inserts an optionlist with all the conference  users
     * Example: /ConfLogin?login_type=ADMIN_USER
     * <p/>
     * </pre>
     */

    public void doGet( HttpServletRequest req, HttpServletResponse res ) throws IOException {

        // Lets get the standard parameters and validate them
        MetaInfo.Parameters params = super.getConferenceSessionParameters( req );

        int testMetaId = params.getMetaId();
        UserDomainObject user = Utility.getLoggedOnUser( req );
        if ( !isUserAuthorized( res, testMetaId, user, req ) ) {
            return;
        }

        String loginType = ( req.getParameter( "login_type" ) == null ) ? "" : ( req.getParameter( "login_type" ) );
        //log("Logintype är nu: " + loginType) ;

        // Lets get serverinformation
        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();

        // ******** ADD USER PAGE *********
        // Lets generate the adduser page
        if ( loginType.equalsIgnoreCase( "ADD_USER" ) ) {
            // Lets build the Responsepage to the loginpage
            VariableManager vm = new VariableManager();
            Vector userInfoV = new Vector( 20 ); // a vector, bigger than the amount fields
            vm = this.addUserInfo( vm, userInfoV );
            vm.addProperty( "SERVLET_URL", "" );
            sendHtml( req, res, vm, CREATE_HTML );
            return;
        }

        // ******** ADMINISTRATE USERS PAGE *********
        // Lets generate the adduser page
        if ( loginType.equalsIgnoreCase( "ADMIN_USER" ) ) {
            // Lets check that the user is an administrator
            if ( super.userHasAdminRights( imcref, Integer.parseInt( "" + params.getMetaId() ), user ) == false ) {
                String header = "ConfAdmin servlet. ";
                new ConfError( req, res, header, 6, user );
                return;
            }

            // Lets get all users in this conference from db
            String[] usersArr = imcref.sqlProcedure( "A_GetAllConfUsersInList", new String[]{"" + params.getMetaId()} );
            Vector usersV = super.convert2Vector( usersArr );
            VariableManager vm = new VariableManager();
            String usersOption = Html.createOptionList( "", usersV );
            vm.addProperty( "USERS_MENU", usersOption );
            vm.addProperty( "UNADMIN_LINK_HTML", USER_UNADMIN_LINK_TEMPLATE );
            this.sendHtml( req, res, vm, ADMIN1_HTML );
            return;
        }

        // ********** LOGIN PAGE *********
        // Lets build the Responsepage to the loginpage
        VariableManager vm = new VariableManager();

        vm.addProperty( "SERVLET_URL", "" );
        vm.addProperty( "#IMAGE_URL#", this.getExternalImageFolder( req ) );
        sendHtml( req, res, vm, LOGIN_HTML );
        return;
    } // End doGet

    /**
     * <PRE>
     * Parameter	Händelse	parameter värde
     * login_type	Utförs om login_type OCH submit har skickats. Verifierar inloggning i konferensen.	LOGIN
     * login_type	Adderar en användare in i Janus user db och till konferensens db	ADD_USER
     * login_type	Sparar en användares användarnivå till konferens db	SAVE_USER
     * Reacts on the actions sent.
     * <p/>
     * PARAMETERS:
     * login_type : Flag used to detect selected acion. Case insensitive
     * <p/>
     * Expected values
     * LOGIN : Verifies a user login to the conference
     * ADD_USER : Adds a new user in the db
     * SAVE_USER	: Saves a users level to the db
     * <p/>
     * </PRE>
     */

    public void doPost( HttpServletRequest req, HttpServletResponse res ) throws IOException {

        // Lets get the standard parameters and validate them
        MetaInfo.Parameters params = super.getConferenceSessionParameters( req );

        int testMetaId = params.getMetaId();
        UserDomainObject user = Utility.getLoggedOnUser( req );
        if ( !isUserAuthorized( res, testMetaId, user, req ) ) {
            return;
        }

        // Lets get the loginType
        String loginType = ( req.getParameter( "login_type" ) == null ) ? "" : ( req.getParameter( "login_type" ) );

        // Lets get serverinformation
        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();
        ImcmsAuthenticatorAndUserMapper userMapper = ApplicationServer.getIMCServiceInterface().getImcmsAuthenticatorAndUserAndRoleMapper();

        // ************* VERIFY LOGIN TO CONFERENCE **************
        // Ok, the user wants to login
        if ( loginType.equalsIgnoreCase( "login" ) /* && req.getParameter("submit") != null */ ) {
            //	log("Ok, nu försöker vi verifiera logga in!") ;
            Properties lparams = this.getLoginParams( req );

            // Ok, Lets check what the user has sent us. Lets verify the fields the user
            // have had to write freetext in to verify that the sql questions wont go mad.
            lparams = super.verifyForSql( lparams );
            String userName = lparams.getProperty( "LOGIN_NAME" );
            String password = lparams.getProperty( "PASSWORD" );

            // Validate loginparams against DB
            String userId = imcref.sqlProcedureStr( "GetUserIdFromName", new String[]{userName, password} );

            // Lets check that we the found the user. Otherwise send unvailid username password
            if ( userId == null ) {
                String header = "ConfLogin servlet.";
                ConfError err = new ConfError( req, res, header, 50, LOGIN_ERROR_HTML, user );
                log( header + err.getErrorMsg() );
                return;
            }else{
                user =  userMapper.getUser(Integer.parseInt(userId));
            }

            // Ok, we found the user, lets verify that the user is a member of this conference
            boolean foundUserInConf = userIsMemberOfConference(params.getMetaId(), user.getId(), imcref );

            // Ok, The user is not a user in this conference, lets check if he has
            // the right to be a member.
            boolean okToLogIn = false;
            if ( !foundUserInConf  ) {
                log( "Ok, the user is not a member here, lets find out if he could be" );
                DocumentMapper documentMapper = imcref.getDocumentMapper();
                DocumentDomainObject document = documentMapper.getDocument( params.getMetaId() );
                addAllConferenceSelfRegRolesToUser(user, params.getMetaId()+"", imcref);
                //renew the user object
                user = userMapper.getUser(Integer.parseInt(user.getId()+""));
                okToLogIn = documentMapper.userHasMoreThanReadPermissionOnDocument( user, document);
                log( "Ok, let the user in and let him be a member: " + okToLogIn );
            }

            if ( !foundUserInConf  && !okToLogIn ) {
                String header = "ConfLogin servlet.";
                ConfError err = new ConfError( req, res, header, 50, LOGIN_ERROR_HTML, user );
                log( header + err.getErrorMsg() + "\n the user exists, but is not a member in this conference" );
                return;
            }

            // Valid login.  Make a note in the session object.
            // Get session
            HttpSession session = req.getSession( true );
            session.setAttribute( "logon.isDone", user );  // just a marker object
            user.setLoginType( "verify" );

            //  Lets update the users sessionobject with a ok login to the conference
            //	Send him to the manager with the ability to get in
            log( "Ok, nu förbereder vi användaren på att logga in" );
            if ( !super.prepareUserForConf( req, res, params, user ) ) {
                log( "Error in prepareUserFor Conf" );
            }
            return;
        }

        // ************* ADD USER TO CONFERENCE **************
        if ( loginType.equalsIgnoreCase( "ADD_USER" ) ) {
            // log("Now runs add_user") ;

            // Lets get the parameters from html page and validate them
            Properties userParams = this.getNewUserParameters( req );
            // Properties userParams = this.getNewUserParameters2(req) ;
            if ( !this.checkUserParameters( userParams ) ) {
                String header = "ConfLogin servlet.";
                ConfError err = new ConfError( req, res, header, 51, LOGIN_ERROR_HTML, user );
                log( header + err.getErrorMsg() );
                return;
            }

            // Lets validate the passwords. Error message will be generated in method
            if ( !AdminUserProps.verifyPassword( userParams.getProperty( "password1" ), userParams.getProperty( "password2" ), req, res ) ) {
                return;
            }

            // Lets validate the phonenbr. Error message will be generated in method
            boolean result = true;
            try{
               Integer.parseInt( userParams.getProperty( "phone" ).replaceAll("[\\- ]", "") );

            } catch ( NumberFormatException e ) {
                // log(e.getMessage()) ;
                new AdminError2( req, res, "", 63 );
                result = false;
            } catch ( NullPointerException e ) {
                // log(e.getMessage()) ;
                new AdminError2( req, res, "", 63 );
                result = false;
            }
            if ( result == false ) {
                return;
            }


            // Lets validate the userparameters before the sql
            userParams = super.verifyForSql( userParams );
            String userName = userParams.getProperty( "login_name" );

            // Lets check that the new username doesnt exists already in db
            String[] userNameExists = imcref.sqlProcedure( "FindUserName", new String[]{userName} );

            if ( userNameExists != null ) {
                if ( userNameExists.length > 0 ) {
                    String header = "ConfLogin servlet.";
                    new ConfError( req, res, header, 56, LOGIN_ERROR_HTML, user );
                    return;
                }
            }

            // Lets get the new UserId for the new user
            String newUserId = imcref.sqlProcedureStr( "GetHighestUserId", new String[0] );
            if ( newUserId == null ) {
                String header = "ConfLogin servlet.";
                new ConfError( req, res, header, 61, LOGIN_ERROR_HTML, user );
                return;
            }

            // Lets build the users information into a string and add it to db

            // Lets get the language id the user will have, or set the lang_id to 1
            // as default
            if ( userParams.getProperty( "lang_id" ) == null ) {
                userParams.setProperty( "lang_id", "1" );
            }

            userParams.setProperty( "user_id", newUserId );

            String[] procParams = AdminUserProps.extractUpdateUserSprocParametersFromProperties( userParams );

            // Lets build the users information into a string and add it to db
            imcref.sqlUpdateProcedure( "AddNewUser", procParams );

            // Ok, lets get the roles the user will get when he is selfregistering  and
            // add those roles to the user
            addAllConferenceSelfRegRolesToUser(user, params.getMetaId()+"", imcref );

            // Ok, Lets add the users roles into db, first get the role his in the system with
            String userId = "" + user.getId();

            String[] usersRoles = imcref.sqlProcedure( "GetUserRolesIDs", new String[]{userId} );

            if ( usersRoles != null ) {
                for ( int i = 0; i < usersRoles.length; i += 2 ) {
                    // Late change, fix so the superadminrole wont be copied to the new user
                    if ( !usersRoles[i].toString().equals( "1" ) ) {
                        imcref.sqlUpdateProcedure( "AddUserRole", new String[]{newUserId, usersRoles[i].toString()} );
                    }
                }
            } else {  // nothing came back from getUserRoles
                String header = "ConfLogin servlet.";
                ConfError err = new ConfError( req, res, header, 58, user );
                log( header + err.getErrorMsg() );
                return;
            }

            // Lets add the new user in the conference db as well
            // ConfUsersAdd	@user_id int,	@conf_id int,	@aFirstName char(25),	@aLastName char(30)
            int metaId = params.getMetaId();
            String fName = userParams.getProperty( "first_name" );
            String lName = userParams.getProperty( "last_name" );

            imcref.sqlUpdateProcedure( "A_ConfUsersAdd", new String[]{newUserId, "" + metaId, fName, lName} );

            String header = "ConfLogin servlet.";
            new ConfError( req, res, header, 55, ADD_USER_OK_HTML, user );
            return;
        }

        // ******* CHANGE_USER **********
        if ( req.getParameter( "CHANGE_USER" ) != null ) {
            // Lets check that the user is an administrator
            if ( super.userHasAdminRights( imcref, Integer.parseInt( "" + params.getMetaId() ), user ) == false ) {
                String header = "ConfAdmin servlet. ";
                new ConfError( req, res, header, 6, user );
                return;
            }

            // Lets get the user which should be changed
            String userId = this.getCurrentUserId( req, res, user );
            if ( userId == null ) {
                return;
            }

            // Lets get all Userinformation from Janus db
            String[] userInfo = imcref.sqlProcedure( "GetUserInfo", new String[]{userId} );

            // Lets get the selected users userlevel
            String level = imcref.sqlProcedureStr( "A_ConfUsersGetUserLevel", new String[]{
                "" + params.getMetaId(), userId
            } );
            String levelStatus = "";
            if ( level.equalsIgnoreCase( "1" ) ) {
                levelStatus = "checked";
            }
            level = "EXPERT";

            Vector userV = this.convert2Vector( userInfo );
            VariableManager vm = new VariableManager();
            vm = this.addUserInfo( vm, userV );

            // Lets create the HTML page
            vm.addProperty( "USER_LEVEL", level );
            vm.addProperty( "USER_LEVEL_STATUS", levelStatus );
            vm.addProperty( "CURR_USER_ID", userId );
            this.sendHtml( req, res, vm, ADMIN2_HTML );
            return;
        }

        //******* SAVE AN EXISTING USERS SETTINGS ***********
        if ( req.getParameter( "SAVE_USER" ) != null ) {
            // log("Ok , lets save an existing user") ;
            // Lets check that the user is an administrator
            if ( super.userHasAdminRights( imcref, Integer.parseInt( "" + params.getMetaId() ), user ) == false ) {
                String header = "ConfAdmin servlet. ";
                new ConfError( req, res, header, 6, user );
                return;
            }

            // Lets get the parameters from html page and validate them

            String userLevel = req.getParameter( "user_level" );
            if ( userLevel == null ) {
                userLevel = "NORMAL";
            }
            if ( userLevel.equalsIgnoreCase( "NORMAL" ) ) {
                userLevel = "0";
            } else {
                userLevel = "1";
            }

            // Lets get the userId were changing properties on from the request Object.
            String userId = this.getCurrentUserId( req, res, user );
            if ( userId == null ) {
                return;
            }

            // Lets add the new information into the conf user db

            imcref.sqlUpdateProcedure( "A_ConfUsersSetUserLevel", new String[]{
                "" + params.getMetaId(), userId, userLevel
            } );

            String url = "ConfLogin?login_type=admin_user";
            res.sendRedirect( url );
            return;
        }

        // ***** RETURN TO ADMIN MANAGER *****
        if ( loginType.equalsIgnoreCase( "GoBack" ) ) {
            String url = "ConfLogin?login_type=admin_user";
            res.sendRedirect( url );
            return;
        }
    } // end HTTP POST

    /**
     * Collects the parameters from the request object
     */

    private Properties getNewUserParameters( HttpServletRequest req ) {

        Properties userInfo = new Properties();
        // Lets get the parameters we know we are supposed to get from the request object
        String login_name = ( req.getParameter( "login_name" ) == null ) ? "" : ( req.getParameter( "login_name" ) );
        String password1 = ( req.getParameter( "password1" ) == null ) ? "" : ( req.getParameter( "password1" ) );
        String password2 = ( req.getParameter( "password2" ) == null ) ? "" : ( req.getParameter( "password2" ) );

        String first_name = ( req.getParameter( "first_name" ) == null ) ? "" : ( req.getParameter( "first_name" ) );
        String last_name = ( req.getParameter( "last_name" ) == null ) ? "" : ( req.getParameter( "last_name" ) );
        String title = ( req.getParameter( "title" ) == null ) ? "" : ( req.getParameter( "title" ) );
        String company = ( req.getParameter( "company" ) == null ) ? "" : ( req.getParameter( "company" ) );

        String address = ( req.getParameter( "address" ) == null ) ? "" : ( req.getParameter( "address" ) );
        String city = ( req.getParameter( "city" ) == null ) ? "" : ( req.getParameter( "city" ) );
        String zip = ( req.getParameter( "zip" ) == null ) ? "" : ( req.getParameter( "zip" ) );
        String country = ( req.getParameter( "country" ) == null ) ? "" : ( req.getParameter( "country" ) );
        String country_council = ( req.getParameter( "country_council" ) == null )
                                 ? "" : ( req.getParameter( "country_council" ) );
        String email = ( req.getParameter( "email" ) == null ) ? "" : ( req.getParameter( "email" ) );

        String phone = ( req.getParameter( "phone" ) == null ) ? "" : ( req.getParameter( "phone" ) );

        String active = ( req.getParameter( "active" ) == null ) ? "1" : ( req.getParameter( "active" ) );

        // Lets fix those fiels which arent mandatory
        // Lets fix those fiels which arent mandatory
        if ( title.trim().equals( "" ) ) {
            title = "--";
        }
        if ( company.trim().equals( "" ) ) {
            company = "--";
        }
        if ( address.trim().equals( "" ) ) {
            address = "--";
        }
        if ( city.trim().equals( "" ) ) {
            city = "--";
        }
        if ( zip.trim().equals( "" ) ) {
            zip = "--";
        }
        if ( country.trim().equals( "" ) ) {
            country = "--";
        }
        if ( country_council.trim().equals( "" ) ) {
            country_council = "--";
        }
        if ( email.trim().equals( "" ) ) {
            email = "--";
        }


        userInfo.setProperty( "login_name", login_name.trim() );
        userInfo.setProperty( "password1", password1.trim() );
        userInfo.setProperty( "password2", password2.trim() );
        userInfo.setProperty( "first_name", first_name.trim() );
        userInfo.setProperty( "last_name", last_name.trim() );
        userInfo.setProperty( "title", title.trim() );
        userInfo.setProperty( "company", company.trim() );

        userInfo.setProperty( "address", address.trim() );
        userInfo.setProperty( "city", city.trim() );
        userInfo.setProperty( "zip", zip.trim() );
        userInfo.setProperty( "country", country.trim() );
        userInfo.setProperty( "country_council", country_council.trim() );
        userInfo.setProperty( "email", email.trim() );
        userInfo.setProperty( "phone", phone.trim() );

        userInfo.setProperty( "active", active.trim() );

        // this.log("UserInfo:" + userInfo.toString()) ;
        return userInfo;
    }

    /**
     * CheckUserparameters. Loops through the parameters and checks that they have
     * been set to something
     */
    private boolean checkUserParameters( Properties aPropObj ) {
        // Ok, lets check that the user has typed anything in all the fields
        if ( aPropObj.values().contains( "" ) ) {
            return false;
        }
        return true;
    } // checkUserParameters

    /**
     * The getLoginParams method gets the login params from the requstobject
     */

    private Properties getLoginParams( HttpServletRequest req ) {
        Properties login = new Properties();
        // Lets get the parameters we know we are supposed to get from the request object
        String login_name = ( req.getParameter( "login_name" ) == null ) ? "" : ( req.getParameter( "login_name" ) );
        String password1 = ( req.getParameter( "password" ) == null ) ? "" : ( req.getParameter( "password" ) );
        login.setProperty( "LOGIN_NAME", login_name.trim() );
        login.setProperty( "PASSWORD", password1.trim() );
        return login;
    }

    /**
     * Log function, will work for both servletexec and Apache
     */

    public void log( String str ) {
        super.log( str );
        System.out.println( "ConfLogin: " + str );
    }

    /**
     * Adds the userInformation to the htmlPage. if an empty vector is sent as argument
     * then an empty one will be created
     */
    private VariableManager addUserInfo( VariableManager vm, Vector v ) {
        // Here is the order in the vector
        // [3, Rickard, tynne, Rickard, Larsson, programmerare, imcode,  Drakarve, Havdhem, 620 11, Sweden, Gotland,
        // rickard@imcode.com, 0, 1001, 0, 1]
        //(v.get(1)==null) ? "" : (req.getParameter("password1")) ;

        if ( v.size() == 0 || v.size() < 14 ) {
            for ( int i = v.size(); i < 13; i++ ) {
                v.add( i, "" );
            }
        }

        vm.addProperty( "LOGIN_NAME", v.get( 1 ).toString() );
        vm.addProperty( "PWD1", v.get( 2 ).toString() );
        vm.addProperty( "PWD2", v.get( 2 ).toString() );
        vm.addProperty( "FIRST_NAME", v.get( 3 ).toString() );
        vm.addProperty( "LAST_NAME", v.get( 4 ).toString() );
        vm.addProperty( "TITLE", v.get( 5 ).toString() );
        vm.addProperty( "COMPANY", v.get( 6 ).toString() );

        vm.addProperty( "ADDRESS", v.get( 7 ).toString() );
        vm.addProperty( "CITY", v.get( 8 ).toString() );
        vm.addProperty( "ZIP", v.get( 9 ).toString() );
        vm.addProperty( "COUNTRY", v.get( 10 ).toString() );
        vm.addProperty( "COUNTRY_COUNCIL", v.get( 11 ).toString() );
        vm.addProperty( "EMAIL", v.get( 12 ).toString() );
        return vm;
    }

    /**
     * Returns a String, containing the userID in the request object.If something failes,
     * a error page will be generated and null will be returned.
     */

    private String getCurrentUserId( HttpServletRequest req, HttpServletResponse res, UserDomainObject user ) throws IOException {

        // Lets get the userId from the request Object.
        String userId = req.getParameter( "user_id" );
        if ( userId == null ) {
            String header = "ConfLogin servlet.";
            ConfError err = new ConfError( req, res, header, 59, LOGIN_ERROR_HTML, user );
            this.log( err.getErrorString() );
            return null;
        } else {
            return userId;
        }

    } // End getCurrentUserId

} // End class
