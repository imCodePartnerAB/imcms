
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;

import imcode.external.diverse.*;
import imcode.server.*;
import imcode.server.user.User;
import imcode.util.*;

import org.apache.log4j.*;
import com.imcode.imcms.RequestConstants;

public class AdminUser extends Administrator {
   private final static String HTML_TEMPLATE = "AdminChangeUser.htm";
   private static Category log = Logger.getInstance( AdminUser.class.getName() );
   private String CHANGE_EXTERNAL_USER_URL = "/adminuser/changeexternaluser.jsp";
    public final static String USER_LOGIN_NAME = "loginname";

    /**
    The GET method creates the html page when this side has been
    redirected from somewhere else.
    **/

   public void doGet( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {

      IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface( req );

      // Lets validate the session
      if( super.checkSession( req, res ) == false )
         return;

      // Lets get an user object
      User user = super.getUserObj( req, res );
      if( user == null ) {
         String header = "Error in AdminCounter.";
         String msg = "Couldnt create an user object." + "<BR>";
         this.log( header + msg );
         new AdminError( req, res, header, msg );
         return;
      }

      // check if user is a Useradmin, adminRole = 2
      boolean isUseradmin = imcref.checkUserAdminrole( user.getUserId(), 2 );

      // check if user is a Superadmin, adminRole = 1
      boolean isSuperadmin = imcref.checkUserAdminrole( user.getUserId(), 1 );

      // Lets verify that the user is an admin, otherwise throw him out.
      if( !isSuperadmin && !isUseradmin ) {
         String header = "Error in AdminCounter.";
         String msg = "The user is not an administrator." + "<BR>";
         this.log( header + msg );
         new AdminError( req, res, header, msg );
         return;
      }

      VariableManager vm = new VariableManager();
      Html ht = new Html();

      // Lets get the category from the request Object.
      String category = req.getParameter( "category" );
      if( category == null ) {
         category = ""; // all category
      }

      if( ("All_Choice").equals( category ) ) {
         category = "-1";
      }

      String searchString = (req.getParameter( "search" ) == null) ? "_z_" : req.getParameter( "search" );

      String lang_prefix = user.getLangPrefix();

      // Lets get all USERTYPES from DB
      String[] userTypes = imcref.sqlProcedure( "GetUserTypes " + lang_prefix );
      Vector userTypesV = new Vector( java.util.Arrays.asList( userTypes ) );
      String user_type = ht.createHtmlCode( "ID_OPTION", category, userTypesV );
      vm.addProperty( "USER_TYPES", user_type );


      String show = ("null".equals( req.getParameter( "showall" ) )) ? "1" : "0";

      // Lets get all USERS from DB with firstname or lastname or login name like the searchString
      // but not USER = 'user'
      String param = category + ", " + searchString + ", " + user.getUserId() + ", " + show;
      String[] usersArr = imcref.sqlProcedure( "GetCategoryUsers " + param );

      Vector usersV = new Vector( java.util.Arrays.asList( usersArr ) );
      String usersOption = ht.createHtmlCode( "ID_OPTION", "", usersV );
      vm.addProperty( "USERS_MENU", usersOption );

      //create the page
      this.sendHtml( req, res, vm, HTML_TEMPLATE );

   } // End doGet

   /**
    POST
    **/

   public void doPost( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {

      IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface( req );

      // Lets validate the session
      if( super.checkSession( req, res ) == false )
         return;

      // Get the session
      HttpSession session = req.getSession( false );

      //lets clear old session attribute
      try {
         session.removeAttribute( "userToChange" );
         session.removeAttribute( "next_url" );
         session.removeAttribute( "Ok_phoneNumbers" );

      } catch( IllegalStateException ise ) {
         log( "session has been invalidated so no need to remove parameters" );
      }

      // Lets get an user object
      imcode.server.user.User user = super.getUserObj( req, res );
      if( user == null ) {
         String header = "Error in AdminCounter.";
         String msg = "Couldnt create an user object." + "<BR>";
         this.log( header + msg );
         new AdminError( req, res, header, msg );
         return;
      }

      // check if user is a Useradmin, adminRole = 2
      boolean isUseradmin = imcref.checkUserAdminrole( user.getUserId(), 2 );

      // check if user is a Superadmin, adminRole = 1
      boolean isSuperadmin = imcref.checkUserAdminrole( user.getUserId(), 1 );


      // Lets check if the user is an admin, otherwise throw him out.
      if( !isSuperadmin && !isUseradmin ) {
         String header = "Error in AdminCounter.";
         String msg = "The user is not an administrator." + "<BR>";
         this.log( header + msg );
         new AdminError( req, res, header, msg );
         return;
      }

      if( req.getParameter( "searchstring" ) != null ) {
         res.sendRedirect( "AdminUser?search=" + req.getParameter( "searchstring" ) + "&category=" + req.getParameter( "user_categories" ) + "&showall=" + req.getParameter( "showall" ) );
         return;
      }

      if( req.getParameter( "ADD_USER" ) != null ) {
         redirectAddUser( res );
      } else if( req.getParameter( "CHANGE_USER" ) != null ) {

         String userToChangeId = getCurrentUserId( req, res );
         User userToChange = imcref.getUserById( Integer.parseInt( userToChangeId ) );

         if( !userToChange.isImcmsExternal() ) {
            redirectChangeUser( req, res, imcref, user, isUseradmin, session, userToChangeId );
         }
         else {
            // req.setAttribute( RequestConstants.USER_LOGIN_NAME, userToChange.getLoginName() );
            String queryString =
               "?" + java.net.URLEncoder.encode(USER_LOGIN_NAME, "UTF-8") +
               "=" + java.net.URLEncoder.encode(userToChange.getLoginName(), "UTF-8") ;
            RequestDispatcher rd = req.getRequestDispatcher( CHANGE_EXTERNAL_USER_URL + queryString );
            rd.forward( req, res );
         }
      } else if( req.getParameter( "DELETE_USER" ) != null ) {
      } else if( req.getParameter( "GO_BACK" ) != null ) {
         res.sendRedirect( "AdminManager" );
      } else {
         doGet( req, res );
      }
   } // end HTTP POST

   private void redirectChangeUser( HttpServletRequest req, HttpServletResponse res,
                                    IMCServiceInterface imcref,
                                    User user,
                                    boolean useradmin,
                                    HttpSession session, String userToChangeId ) throws ServletException, IOException {
      // ******* GENERATE AN CHANGE_USER PAGE**********
      log( "Change_User" );

      // return if we don´t get a user
      if( userToChangeId != null ) {
         // Lets check if the user has right to do changes
         // only if he is an superadmin, useradmin or if he try to change his own values
         // otherwise throw him out.
         if( imcref.checkAdminRights( user ) == false && !useradmin && !userToChangeId.equals( "" + user.getUserId() ) ) {
            String header = "Error in AdminCounter.";
            String msg = "The user has no rights to change user values." + "<BR>";
            this.log( header + msg );
            new AdminError( req, res, header, msg );
         } else {
            // get a user object by userToChangeId
            session.setAttribute( "userToChange", userToChangeId );
            // Lets redirect to AdminUserProps and get the HTML page to change a user.
            res.sendRedirect( "AdminUserProps?CHANGE_USER=true" );
         }
      }
   }

   private void redirectAddUser( HttpServletResponse res ) throws IOException {
      log( "Add_User" );

      VariableManager vm = new VariableManager();
      Html htm = new Html();

      // Lets redirect to AdminUserProps and get the HTML page to add a new user.
      res.sendRedirect( "AdminUserProps?ADD_USER=true" );
   }




   // ************************* NEW FUNCTIONS *************


   /**
    Returns a String, containing the userID in the request object.If something failes,
    a error page will be generated and null will be returned.
    */

   public String getCurrentUserId( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {


      String userId = req.getParameter( "user_Id" );

      // Get the session
      HttpSession session = req.getSession( false );

      if( userId == null ) {
         // Lets get the userId from the Session Object.
         userId = (String)session.getAttribute( "userToChange" );

      }

      //	if (userId == null)
      //		userId = req.getParameter("CURR_USER_ID") ;
      //			if (userId == null || userId.startsWith("#")) {

      if( userId == null ) {
         String header = "ChangeUser error. ";
         String msg = "No user_id was available." + "<BR>";
         this.log( header + msg );
         new AdminError( req, res, header, msg );
         return null;
      } else {
         this.log( "AnvändarId=" + userId );
      }
      //System.out.println("AdminUser-getCurrentUserId() userId= " + userId);

      return userId;
   } // End getCurrentUserId


   public void log( String str ) {
      super.log( str );
      log.debug( "AdminUser: " + str );
   }


}
