
import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import imcode.external.diverse.*;
import imcode.util.*;
import imcode.server.*;
import imcode.server.user.User;
import imcode.readrunner.*;

import org.apache.log4j.*;

public class AdminUserProps extends Administrator {
   private final static String HTML_RESPONSE = "AdminUserResp.htm";
   private final static String HTML_RESPONSE_ADMIN_PART = "AdminUserResp_admin_part.htm";
   private final static String HTML_RESPONSE_SUPERADMIN_PART = "AdminUserResp_superadmin_part.htm";

   private static Logger log = Logger.getLogger( AdminUserProps.class.getName() );

   public void doGet( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {

      // Lets validate the session
      if( super.checkSession( req, res ) == false )
         return;

      // Get the session
      HttpSession session = req.getSession( false );

      // Lets get an user object
      imcode.server.user.User user = super.getUserObj( req, res );

      if( user == null ) {
         String header = "Error in AdminCounter.";
         String msg = "Couldnt create an user object." + "<BR>";
         this.log( header + msg );
         new AdminError( req, res, header, msg );
         return;
      }

      IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface( req );

      // check if user is a Useradmin, adminRole = 2
      boolean isUseradmin = imcref.checkUserAdminrole( user.getUserId(), 2 );

      // check if user is a Superadmin, adminRole = 1
      boolean isSuperadmin = imcref.checkUserAdminrole( user.getUserId(), 1 );

      //Lets get temporary values from session if there is some.
      //String[] tmp_userRoles = (String[])session.getAttribute("tempUserRoles");
      //String tmp_userType = (String)session.getAttribute("tempUserType");
      Properties tmp_userInfo = (Properties)session.getAttribute( "tempUser" );

      Vector tmp_phones = (Vector)session.getAttribute( "Ok_phoneNumbers" );
      if( tmp_phones == null ) {
         tmp_phones = new Vector();
      }

      String[] phonetypesA = imcref.sqlProcedure( "GetPhonetypes " + user.getLangId() );

      // Get a new Vector:  phonetype_id, typename
      Vector phoneTypesV = new Vector( java.util.Arrays.asList( phonetypesA ) );

      // ******* GENERATE AN ADD_USER PAGE **********

      if( req.getParameter( "ADD_USER" ) != null ) {

         // Lets check if the user is an admin, otherwise throw him out.
         //if (imcref.checkAdminRights(user) == false) {
         if( !isSuperadmin && !isUseradmin ) {
            showErrorPageUserNotAnAdministrator( req, res );
            return;
         }

         showAddUserPage( tmp_userInfo, res, phoneTypesV, tmp_phones, user, imcref, req, session );
         return;
      }

      // ******* GENERATE AN CHANGE_USER PAGE**********
      if( req.getParameter( "CHANGE_USER" ) != null ) {

         log( "Changeuser" );

         // lets first try to get userId from the session if we has been redirectet from authenticate
         String userToChangeId = getCurrentUserId( req, res );

         // Lets check if the user has right to do changes
         // only if he is an superadmin, useradmin or if he try to change his own values
         // otherwise throw him out.
         if( imcref.checkAdminRights( user ) == false && !isUseradmin && !userToChangeId.equals( "" + user.getUserId() ) ) {
            showErrorPageUserHasNoRightsToChangeUserValues( req, res );
            return;
         }

         showChangeUserPage( userToChangeId, imcref, tmp_userInfo, tmp_phones, user, res, session, phoneTypesV, isSuperadmin, isUseradmin, req );
         return;
      }


   }

   private void showChangeUserPage( String userToChangeId, IMCServiceInterface imcref, Properties tmp_userInfo, Vector tmp_phones, User user, HttpServletResponse res, HttpSession session, Vector phoneTypesV, boolean superadmin, boolean useradmin, HttpServletRequest req ) throws IOException {
      // get a user object by userToChangeId
      User userToChange = null;
      if( userToChangeId != "" ) {
         userToChange = imcref.getUserById( Integer.parseInt( userToChangeId ) );
      }

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
      if( tmp_userInfo != null ) {
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

      } else {
         login_name = userToChange.getLoginName();
         password1 = userToChange.getPassword();
         password2 = userToChange.getPassword();

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
         //			userType= tmp_userType;
         //active = tmp_userInfo.getProperty("active");
         //userCreateDate = tmp_userInfo.getProperty("createDate") ;
      }


      // Lets get all users phone numbers from session if we have any else we get them from db
      // return value from db= phone_id, number, user_id, phonetype_id, typename
      Html htm = new Html();
      String[][] phonesArr = imcref.sqlProcedureMulti( "GetUserPhoneNumbers " + userToChange.getUserId() );

      // Get a new Vector:  phone_id, number, user_id, phonetype_id, typename  ex. 10, 46 498 123456, 3, 1
      Vector phoneNumbers = getPhonesArrayVector( phonesArr );

      if( tmp_phones.size() > 0 ) {
         phoneNumbers = tmp_phones;
      }

      // Get a new Vector: phone_id, (typename) number   ex.  { 10, (Hem) 46 498 123456 }
      Vector phonesV = this.getPhonesVector( phoneNumbers, "" + user.getLangId(), imcref );

      String selected = "";
      if( phonesV.size() > 0 ) {
         selected = (String)phonesV.get( 0 );
      }

      //System.out.println("selected= " + selected);

      String phones = htm.createHtmlCode( "ID_OPTION", selected, phonesV );

      res.setContentType( "text/html" ); // set content type
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
      if( null != (String)session.getAttribute( "next_url" ) ) {
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
      String phonetypes = htm.createHtmlCode( "ID_OPTION", "1", phoneTypesV );
      vec.add( "#PHONETYPES_MENU#" );
      vec.add( phonetypes );

      vec.add( "#PHONES_MENU#" );
      vec.add( phones );


      // Lets add html for admin_part in AdminUserResp
      vec.add( "#ADMIN_PART#" );
      if( superadmin || (useradmin && user.getUserId() != userToChange.getUserId()) ) {
         vec.add( createAdminPartHtml( user, userToChange, imcref, req, res, session ) );
      } else {
         vec.add( "" );
      }

      String adminTask = req.getParameter( "adminTask" );
      if( adminTask == null ) {
         adminTask = "SAVE_CHANGED_USER";
      }
      vec.add( "#ADMIN_TASK#" );
      vec.add( adminTask );


      // Lets get the the users language id
      String[] langList = imcref.sqlProcedure( "GetLanguageList 'se'" ); // FIXME: Get the correct language for the user
      Vector selectedLangV = new Vector();
      selectedLangV.add( "" + userToChange.getLangId() );
      vec.add( "LANG_TYPES" );
      vec.add( htm.createHtmlCode( "ID_OPTION", selectedLangV, new Vector( Arrays.asList( langList ) ) ) );


      //store all data into the session
      session.setAttribute( "userToChange", userToChangeId );
      session.setAttribute( "Ok_phoneNumbers", phoneNumbers );
      //session.setAttribute("RESET_langList", langList);
      //session.setAttribute("RESET_selectedLangV", selectedLangV);

      // Lets renove session we dont need anymore.
      try {
         session.removeAttribute( "tempUserRoles" );
         session.removeAttribute( "tempUseradminRoles" );
         session.removeAttribute( "tempUserType" );
         session.removeAttribute( "tempUser" );
         //session.getAttribute("Ok_phoneNumbers");

      } catch( IllegalStateException ise ) {
         log( "session has been invalidated so no need to remove parameters" );
      }


      String outputString = imcref.parseDoc( vec, HTML_RESPONSE, userToChange.getLangPrefix() );
      out.write( outputString );
   }

   private void showErrorPageUserHasNoRightsToChangeUserValues( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
      String header = "Error in AdminCounter.";
      String msg = "The user has no rights to change user values." + "<BR>";
      this.log( header + msg );
      new AdminError( req, res, header, msg );
   }

   private void showAddUserPage( Properties tmp_userInfo, HttpServletResponse res, Vector phoneTypesV, Vector tmp_phones, User user, IMCServiceInterface imcref, HttpServletRequest req, HttpSession session ) throws IOException {
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
      if( tmp_userInfo != null ) {
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
      res.setContentType( "text/html" ); // set content type
      Writer out = res.getWriter();	// to write out html page

      //	VariableManager vm = new VariableManager() ;
      Html htm = new Html();


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
      String phonetypes = htm.createHtmlCode( "ID_OPTION", "1", phoneTypesV );
      vec.add( "#PHONETYPES_MENU#" );
      vec.add( phonetypes );

      // phoneslist
      String phones = htm.createHtmlCode( "ID_OPTION", "", tmp_phones );
      vec.add( "#PHONES_MENU#" );
      vec.add( phones );


      // Lets add html for admin_part in AdminUserResp
      vec.add( "#ADMIN_PART#" );
      vec.add( createAdminPartHtml( user, null, imcref, req, res, session ) );


      // language id: lets set swedish as default
      String[] langList = imcref.sqlProcedure( "GetLanguageList 'se'" );
      Vector selectedLangV = new Vector();
      selectedLangV.add( "1" );
      vec.add( "#LANG_TYPES#" );
      vec.add( htm.createHtmlCode( "ID_OPTION", selectedLangV, new Vector( Arrays.asList( langList ) ) ) );

      //store all data into the session
      session.setAttribute( "Ok_phoneNumbers", tmp_phones );

      // Lets create the HTML page
      String outputString = imcref.parseDoc( vec, HTML_RESPONSE, user.getLangPrefix() );
      out.write( outputString );
   }

   private void showErrorPageUserNotAnAdministrator( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
      String header = "Error in AdminCounter.";
      String msg = "The user is not an administrator." + "<BR>";
      this.log( header + msg );
      new AdminError( req, res, header, msg );
   }
   /** End GET **/


   /**
    * POST
    **/
   public void doPost( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
      String host = req.getHeader( "Host" );
      IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterfaceByHost( host );

      // Lets validate the session
      if( super.checkSession( req, res ) == false )
         return;

      HttpSession session = req.getSession( false );

      if( session == null )
         return;

      // Lets get an user object
      imcode.server.user.User user = super.getUserObj( req, res );
      if( user == null ) {
         String header = "Error in AdminCounter.";
         String msg = "Couldnt create an user object." + "<BR>";
         new AdminError( req, res, header, msg );
         return;
      }

      // check if user is a Superadmin, adminRole = 1
      boolean isSuperadmin = imcref.checkUserAdminrole( user.getUserId(), 1 );

      // check if user is a Useradmin, adminRole = 2
      boolean isUseradmin = imcref.checkUserAdminrole( user.getUserId(), 2 );

      boolean isAdmin = (isSuperadmin || isUseradmin);


      // Lets check adminTask
      String adminTask = req.getParameter( "adminTask" );
      if( adminTask == null )
         adminTask = "";


      // Lets get the user which should be changed if we is not in ADD_USER mode
      String userToChangeId = "";

      // if we are processing a user template then userToChange is equal to user
      if( req.getParameter( "userTemplate" ) != null && "SAVE_CHANGED_USER".equals( adminTask ) ) {
         userToChangeId = "" + user.getUserId();
      }
      // if we are processing a admin template
      if( req.getParameter( "userTemplate" ) == null && !"ADD_USER".equals( adminTask ) ) {
         userToChangeId = getCurrentUserId( req, res );
      }

      // get a user object by userToChangeId
      imcode.server.user.User userToChange = null;
      if( userToChangeId != "" ) {
         userToChange = imcref.getUserById( Integer.parseInt( userToChangeId ) );
      }


      Properties userInfoP = new Properties();

      userInfoP = this.getParameters( req, imcref, user, userToChange );

      // Lets get all phonetypes from db
      String[] phonetypesA = imcref.sqlProcedure( "GetPhonetypes " + user.getLangId() );

      // Get a new Vector:  phonetype_id, typename
      Vector phoneTypesV = new Vector( java.util.Arrays.asList( phonetypesA ) );

      // Lets get all Userinformation and add it to html page

      VariableManager vm = new VariableManager();

      vm.addProperty( "LOGIN_NAME", userInfoP.getProperty( "login_name" ) );
      vm.addProperty( "FIRST_NAME", userInfoP.getProperty( "first_name" ) );
      vm.addProperty( "LAST_NAME", userInfoP.getProperty( "last_name" ) );
      vm.addProperty( "TITLE", userInfoP.getProperty( "title" ) );
      vm.addProperty( "COMPANY", userInfoP.getProperty( "company" ) );
      vm.addProperty( "ADDRESS", userInfoP.getProperty( "address" ) );
      vm.addProperty( "CITY", userInfoP.getProperty( "city" ) );
      vm.addProperty( "ZIP", userInfoP.getProperty( "zip" ) );
      vm.addProperty( "COUNTRY", userInfoP.getProperty( "country" ) );
      vm.addProperty( "COUNTRY_COUNCIL", userInfoP.getProperty( "country_council" ) );
      vm.addProperty( "EMAIL", userInfoP.getProperty( "email" ) );


      Html htm = new Html();


      //******* READRUNNER_SETTINGS BUTTON WAS PUNSCHED ***********
      if( null != req.getParameter( "READRUNNER_SETTINGS" ) ) {

         String theUserType = req.getParameter( "user_type" );
         String[] theUserRoles = req.getParameterValues( "roles" );
         String[] theUseradminRoles = req.getParameterValues( "useradmin_roles" );

         if( null != theUserRoles ) {
            session.setAttribute( "tempUserRoles", theUserRoles );
         }
         if( null != theUserType ) {
            session.setAttribute( "tempUserType", theUserType );
         }
         if( null != userInfoP ) {
            session.setAttribute( "tempUser", userInfoP );
         }
         if( null != theUseradminRoles ) {
            session.setAttribute( "tempUseradminRoles", theUseradminRoles );
         }

         res.sendRedirect( "AdminUserReadrunner" );
         return;
      }

      //******** USERADMIN_STTINGS BUTTON WAS PUNSCHED ***********
      if( null != req.getParameter( "useradmin_settings" ) ) {

         String[] theUserRoles = req.getParameterValues( "roles" );
         String[] theUseradminRoles = req.getParameterValues( "useradmin_roles" );
         String theUserType = req.getParameter( "user_type" );

         if( null != theUserRoles ) {
            session.setAttribute( "tempUserRoles", theUserRoles );
         }
         if( null != theUserType ) {
            session.setAttribute( "tempUserType", theUserType );
         }
         if( null != userInfoP ) {
            session.setAttribute( "tempUser", userInfoP );
         }
         if( null != theUseradminRoles ) {
            session.setAttribute( "tempUseradminRoles", theUseradminRoles );
         }

         res.sendRedirect( "AdminUserUseradminSettings" );
         return;

      }

      //******* RESET_FORM BUTTON WAS PUNSCHED ***********
      //sets up the needed parameters and redirect back to AdminUserProps
      if( req.getParameter( "RESET_FORM" ) != null ) {

         if( adminTask.equals( "ADD_USER" ) ) {
            res.sendRedirect( "AdminUserProps?ADD_USER=true&adminTask=" + adminTask );
         } else if( adminTask.equals( "SAVE_CHANGED_USER" ) ) {
            res.sendRedirect( "AdminUserProps?CHANGE_USER=true&adminTask=" + adminTask );
         }
         return;
      }



      //******** OK_PHONES or DELETE_PHONES or EDIT_PHONES  WAS PRESSED ***********
      if( req.getParameter( "ok_phones" ) != null || req.getParameter( "delete_phones" ) != null || req.getParameter( "edit_phones" ) != null ) {


         if( adminTask == null ) {
            adminTask = "";
         }


         if( imcref.checkAdminRights( user ) == false && !isUseradmin && !userToChangeId.equals( "" + user.getUserId() ) ) {
            String header = "Error in AdminCounter.";
            String msg = "The user has no rights to change user values." + "<BR>";
            this.log( header + msg );
            new AdminError( req, res, header, msg );
            return;
         }



         // Lets get all phonenumbers from the session
         // Get a new Vector:  phone_id, number, user_id, phonetype_id, typename  ex. 10, 46 498 123456, 3, 1, Hem
         Vector phoneNumbers = (session.getAttribute( "Ok_phoneNumbers" ) != null) ? (Vector)session.getAttribute( "Ok_phoneNumbers" ) : new Vector();

         log( "test" + req.getParameter( "edit_phones" ) );


         String selectedPhoneId = "";



         //******** OK_PHONES BUTTON WAS PRESSED **********
         if( req.getParameter( "ok_phones" ) != null ) { //adds or changes a phoneNr to the select list

            log( "ok_phones in doPost" );

            //lets get the phonenumber from the form and add it into Vectorn phonesV

            boolean found = false;  // marker that we is going to edit a selected phone number
            int tempId = 1;  // temporary phone id

            Enumeration enum = phoneNumbers.elements();

            while( enum.hasMoreElements() ) {
               String[] temp = (String[])enum.nextElement();
               if( temp[0].equals( req.getParameter( "phone_id" ) ) ) {
                  selectedPhoneId = temp[0];
                  phoneNumbers.remove( temp );
                  temp[1] = req.getParameter( "local_code" );
                  temp[3] = req.getParameter( "phonetype" );
                  phoneNumbers.addElement( temp );
                  found = true;
               }
               try {
                  if( Integer.parseInt( temp[0] ) >= tempId ) {
                     tempId = Integer.parseInt( temp[0] ) + 1;
                  }

               } catch( NumberFormatException ignored ) {
                  // ignored
               }

            }


            if( !found ) {
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

         if( req.getParameter( "edit_phones" ) != null ) {
            log( "edit_phones" );

            Enumeration enum = phoneNumbers.elements();

            while( enum.hasMoreElements() && !found ) {
               String[] temp = (String[])enum.nextElement();
               if( temp[0].equals( req.getParameter( "user_phones" ) ) ) {
                  vm.addProperty( "PHONE_ID", temp[0] );
                  vm.addProperty( "NUMBER", temp[1] );
                  phonetypes_id = temp[3];
                  found = true;
               }
            }
         }

         if( !found ) {
            vm.addProperty( "PHONE_ID", "" );
            vm.addProperty( "NUMBER", "" );
            phonetypes_id = "1";
         }

         // phonetype list
         String phonetypes = htm.createHtmlCode( "ID_OPTION", phonetypes_id, phoneTypesV );
         vm.addProperty( "PHONETYPES_MENU", phonetypes );

         selectedPhoneId = req.getParameter( "user_phones" );
         log( "Number: " + selectedPhoneId );

         //*********end edit_phones***********


         //****************DELETE_PHONES BUTTON WAS PRESSED ************

         if( req.getParameter( "delete_phones" ) != null ) {

            log( "lets delete_phones from templist" );

            Enumeration enum = phoneNumbers.elements();
            found = false;
            //		log("Size"+phoneNumbers.size());
            while( enum.hasMoreElements() && !found ) {
               String[] temp = (String[])enum.nextElement();
               log( temp[0] + " == " + req.getParameter( "user_phones" ) );
               if( temp[0].equals( req.getParameter( "user_phones" ) ) ) {
                  phoneNumbers.remove( temp );
                  found = true;
               }
            }


            if( phoneNumbers.size() > 0 ) {
               String[] temp = (String[])phoneNumbers.firstElement();
               selectedPhoneId = temp[0];
            }
         }

         // ******** end delete_phones ***************


         String newPwd = userInfoP.getProperty( "password1" );
         boolean isChanged = false;
         for( int i = 0; i < newPwd.length(); i++ ) {
            if( newPwd.charAt( i ) != ("*").charAt( 0 ) ) {
               isChanged = true;
            }
         }
         if( isChanged ) {
            //update hidden fields whith new password
            vm.addProperty( "NEW_PWD1", userInfoP.getProperty( "password1" ) );
            vm.addProperty( "NEW_PWD2", userInfoP.getProperty( "password2" ) );
         } else {
            vm.addProperty( "NEW_PWD1", "" );
            vm.addProperty( "NEW_PWD2", "" );
         }

         //update password fields with just ****
         vm.addProperty( "PWD1", doPasswordString( userInfoP.getProperty( "password1" ) ) );
         vm.addProperty( "PWD2", doPasswordString( userInfoP.getProperty( "password2" ) ) );

         // Lets add html for admin_part in AdminUserResp
         if( isSuperadmin || (isUseradmin && !userToChangeId.equals( "" + user.getUserId() )) ) {

            /*
               vm.addProperty("ACTIVE",  "1" );

               if ( "1".equals(userInfoP.getProperty("active") ) ){
                      vm.addProperty("ACTIVE_FLAG", "checked" );
               }else{
                  vm.addProperty("ACTIVE_FLAG", "" );
               }
            */
            vm.addProperty( "ADMIN_PART", createAdminPartHtml( user, userToChange, imcref, req, res, session ) );

         } else {
            vm.addProperty( "ADMIN_PART", "" );
         }



         //		String selectedId = req.getParameter("selected_id") == null ? "" : req.getParameter("selected_id");
         //		log("selected_id= "+ selectedPhoneId);

         // Get a new Vector: phone_id, (typename) number    ex. { 10, (Hem) 46 498 123456 }
         Vector phonesV = this.getPhonesVector( phoneNumbers, "" + user.getLangId(), imcref );

         if( phonesV == null ) {
            this.sendErrorMsg( req, res, "Add/edit user", "An eror occured!" );
            return;
         }

         // update Vector phonesV from Vector phonesNumber: phone_id, (typename) number   ex.  { 10, (Hem) 46 498 123456 }
         phonesV = this.getPhonesVector( phoneNumbers, "" + user.getLangId(), imcref );

         // add phones list
         String phones = htm.createHtmlCode( "ID_OPTION", selectedPhoneId, phonesV );
         log( "phones stringen: " + phones );
         vm.addProperty( "PHONES_MENU", phones );


         /*
              // Lets get the the users language id
              String[] langList = (String[])session.getAttribute("RESET_langList");
              Vector selectedLangV = (Vector)session.getAttribute("RESET_selectedLangV");
              selectedLangV.add(userV.get(16).toString()) ;
              vm.addProperty("LANG_TYPES", htm.createHtmlCode("ID_OPTION",selectedLangV, new Vector(java.util.Arrays.asList(langList)))) ;
         */

         vm.addProperty( "ADMIN_TASK", adminTask );
         vm.addProperty( "CURR_USER_ID", userToChangeId );

         session.setAttribute( "Ok_phoneNumbers", phoneNumbers );

         this.sendHtml( req, res, vm, HTML_RESPONSE );
         return;

      }
      // end of ******** OK_PHONES or DELETE_PHONES or EDIT_PHONES  WAS PRESSED ***********



      // ******* SAVE NEW USER TO DB **********
      if( req.getParameter( "SAVE_USER" ) != null && adminTask.equalsIgnoreCase( "ADD_USER" ) ) {
         log( "Lets add a new user to db" );

         //get session
         if( session == null )
            return;

         /*	// Lets check if the user is an admin, otherwise throw him out.
            if (!isAdmin ){
                String header = "Error in AdminUserProps." ;
                String msg = "The user has no admin rights."+ "<BR>" ;
                AdminError err = new AdminError(req,res,header,msg) ;
                return ;
            }
         */
         // Lets get the parameters from html page and validate them
         Properties params = this.getParameters( req, imcref, user, null );


         // if user has add a phone number we have to get the password from NEW_PWD1 parameter
         if( req.getParameter( "new_pwd1" ) != null && !("").equals( req.getParameter( "new_pwd1" ) ) ) {
            params.setProperty( "password1", req.getParameter( "new_pwd1" ) );
            params.setProperty( "password2", req.getParameter( "new_pwd2" ) );
         }


         // Lets get the roles from htmlpage
         Vector rolesV = this.getRolesParameters( "roles", req, res );
         if( rolesV == null )
            return;

         // Lets validate the password
         if( verifyPassword( params, req, res ) == false )
            return;



         // Lets check that the new username doesnt exists already in db
         String userName;
         String msg = "The username already exists, please change the username." + "<BR>";
         if( null != req.getParameter( "login_name" ) ) {
            userName = params.getProperty( "login_name" );
         } else {
            userName = req.getParameter( "email" );
            params.setProperty( "login_name", userName );
            msg = "The username or email already exists, please change." + "<BR>";
         }

         if( !checkExistingUserName( imcref, params ) ) {
            String header = "Error in AdminUserProps.";
            this.log( header + msg );
            new AdminError( req, res, header, msg );
            return;
         }


         params = this.validateParameters( params, req, res );
         if( params == null )
            return;

         //Lets get phonenumbers from the session if we have a session Attribute
         Vector phonesV = (Vector)session.getAttribute( "Ok_phoneNumbers" );

         String newUserId = addNewUser( imcref, params );

         // Lets add Readrunner user data
         if( null != session.getAttribute( "tempRRUserData" ) ) {
            ReadrunnerUserData rrUserData = new ReadrunnerUserData();
            rrUserData = (ReadrunnerUserData)session.getAttribute( "tempRRUserData" );
            User newUser = imcref.getUserById( Integer.parseInt( newUserId ) );
            imcref.setReadrunnerUserData( newUser, rrUserData );
         }

         // Lets add the new users roles
         String roleId[] = imcref.sqlProcedure( "GetRoleIdByRoleName Useradmin" );
         boolean isSelected = false;
         for( int i = 0; i < rolesV.size(); i++ ) {
            String aRole = rolesV.elementAt( i ).toString();
            imcref.sqlUpdateProcedure( "AddUserRole " + newUserId + ", " + aRole );
            if( aRole.equals( roleId[0] ) ) {
               isSelected = true; // role Useradmin is selected
            }
         }

         // always let user get the role Users
         roleId = imcref.sqlProcedure( "GetRoleIdByRoleName Users" );
         if( roleId != null ) {
            imcref.sqlUpdateProcedure( "AddUserRole " + newUserId + ", " + Integer.parseInt( roleId[0] ) );
         }

         if( isSelected ) {  // role Useradmin is selected

            // Lets get the useradmin_roles from htmlpage
            Vector useradminRolesV = this.getRolesParameters( "useradmin_roles", req, res );

            // Lets add the new useradmin roles.
            for( int i = 0; i < useradminRolesV.size(); i++ ) {
               String aRole = useradminRolesV.elementAt( i ).toString();
               imcref.sqlUpdateProcedure( "AddUseradminPermissibleRoles  " + newUserId + ", " + aRole );
            }
         }


         // save phone number
         //if we are processing data from a admin template
         if( null == req.getParameter( "userTemplate" ) ) {
            //save phone number from phonesV  ( phonesV : id, number, user_id, phonetype_id )
            if( null != phonesV && phonesV.size() > 0 ) {
               for( int i = 0; i < phonesV.size(); i++ ) {
                  String[] aPhone = (String[])phonesV.elementAt( i );
                  String sqlStr = "phoneNbrAdd " + newUserId + ", '";//userId
                  sqlStr += aPhone[1] + "', '" + aPhone[3] + "'";//number, phonetype_id

                  log( "PhoneNrAdd: " + sqlStr );

                  imcref.sqlUpdateProcedure( sqlStr );
               }
            }
            // we are processing data from a user template
         } else {

            String workPhone = req.getParameter( "workphone" );
            String mobilePhone = req.getParameter( "mobilephone" );
            String sqlStr;

            if( !("").equals( workPhone ) ) {
               sqlStr = "phoneNbrAdd " + newUserId + ", '";//userId
               sqlStr += workPhone + "', 2";//number, phonetype_id
               log( "PhoneNrAdd: " + sqlStr );
               imcref.sqlUpdateProcedure( sqlStr );
            }
            if( !("").equals( mobilePhone ) ) {
               sqlStr = "phoneNbrAdd " + newUserId + ", '";//userId
               sqlStr += mobilePhone + "', 3";//number, phonetype_id
               log( "PhoneNrAdd: " + sqlStr );
               imcref.sqlUpdateProcedure( sqlStr );
            }
         }

         //if we are processing data from a user template we have to login the new user
         if( null != req.getParameter( "userTemplate" ) ) {
            String nexturl = "VerifyUser?name=" + params.getProperty( "login_name" );
            nexturl += "&passwd=" + params.getProperty( "password1" );

            if( null != req.getParameter( "next_meta" ) ) {
               nexturl += "&next_meta=" + req.getParameter( "next_meta" );

            } else if( null != req.getParameter( "next_url" ) ) {
               nexturl += "&next_url=" + req.getParameter( "next_url" );
            }
            res.sendRedirect( nexturl );
            return;

         } else {
            this.goNext( req, res, session );
         }
         return;
      }


      // ******** SAVE EXISTING USER TO DB ***************
      if( req.getParameter( "SAVE_USER" ) != null && adminTask.equalsIgnoreCase( "SAVE_CHANGED_USER" ) ) {
         log( "******** SAVE EXISTING USER TO DB ***************" );

         if( session == null )
            return;

         // Lets check that we have a user to be changed.
         if( userToChange == null )
            return;

         // Lets check if the user is an admin or if he is going to change his own data, otherwise throw him out.
         if( !isAdmin && user.getUserId() != Integer.parseInt( userToChangeId ) ) {
            String header = "Error in AdminCounter.";
            String msg = "The user has no rights to administrate." + "<BR>";
            new AdminError( req, res, header, msg );
            return;
         }

         // Lets get the parameters from html page and validate them

         Properties params = this.getParameters( req, imcref, user, userToChange );
         params.setProperty( "user_id", userToChangeId );


         /* Lets check if loginname is going to be changed and if so,
            lets check that the new loginname don't already exists in db
         */
         String currentLogin = userToChange.getLoginName();
         String msg = "The username already exists, please change the username." + "<BR>";
         String newLogin;
         if( null != req.getParameter( "login_name" ) ) {
            newLogin = params.getProperty( "login_name" );

         } else {
            // we are processing data from a user template where users login name will be the same
            // like his email. And then, if users current login name not is equal to his current email
            // we dont make any changes on his login name,  only on his email.

            newLogin = req.getParameter( "email" );
            if( !userToChange.getEmailAddress().equalsIgnoreCase( currentLogin ) ) {
               newLogin = currentLogin;
            }
            msg = "The username or email already exists, please change." + "<BR>";
         }

         // check that the changed login name don´t already exists
         if( !newLogin.equalsIgnoreCase( currentLogin ) ) {
            String userNameExists[] = imcref.sqlProcedure( "FindUserName '" + newLogin + "'" );
            if( userNameExists != null ) {
               if( userNameExists.length > 0 ) {
                  String header = "Error in AdminUserProps.";
                  this.log( header + msg );
                  new AdminError( req, res, header, msg );
                  return;
               }
            }
         }
         // update params property with login_name so DB will be updated.
         params.setProperty( "login_name", newLogin );



         //lets get the current password for user
         String currPwd = userToChange.getPassword();
         if( currPwd.equals( "" ) ) {
            String header = "Fel! Ett lösenord kund inte hittas";
            msg = "Lösenord kunde inte hittas" + "<BR>";
            this.log( header + msg );
            new AdminError( req, res, header, msg );
            log( "innan return i currPwd.equals" );
            return;
         }

         // Lets check the password. if its empty, then it wont be updated. get the
         // old password from db and use that one instad

         String newPwd = params.getProperty( "password1" );
         boolean isChanged = false;
         for( int i = 0; i < newPwd.length(); i++ ) {
            if( newPwd.charAt( i ) != ("*").charAt( 0 ) ) {
               isChanged = true;
            }
         }
         if( !isChanged ) {
            params.setProperty( "password1", currPwd );
            params.setProperty( "password2", currPwd );
         }


         // when user has add a phone number in admin interface,
         // we have to get the password from NEW_PWD1 parameter
         if( null != req.getParameter( "new_pwd1" ) && !("").equals( req.getParameter( "new_pwd1" ) ) ) {
            params.setProperty( "password1", req.getParameter( "new_pwd1" ) );
            params.setProperty( "password2", req.getParameter( "new_pwd2" ) );
         }

         // If we are processing data from a user template and user is going to change his password
         // lets check if old password is valid ( if we have got any value from html-page)
         if( req.getParameter( "password_current" ) != null && isChanged ) {
            if( !currPwd.equals( req.getParameter( "password_current" ) ) ) {
               String header = "Fel! Verifiering av lösenord";
               msg = "Kunde ej verifiera gammalt lösenord" + "<BR>";
               this.log( header + msg );
               new AdminError( req, res, header, msg );
               log( "innan return i currPwd.equals" );
               return;
            }

         }

         // Lets validate the password
         if( verifyPassword( params, req, res ) == false )
            return;

         // Ok, Lets validate all fields
         /*
            Enumeration enumValues = params.elements() ;
            Enumeration enumKeys = params.keys() ;
            while((enumValues.hasMoreElements() && enumKeys.hasMoreElements())) {
                Object oKeys = (enumKeys.nextElement()) ;
                Object oValue = (enumValues.nextElement()) ;
                System.out.println("oKeys= " + oKeys.toString() + " oValue= " + oValue.toString() );
            }
         */
         params = this.validateParameters( params, req, res );
         if( params == null )
            return;



         //Lets get phonenumbers from the session if we have a session Attribute
         Vector phonesV = (Vector)session.getAttribute( "Ok_phoneNumbers" );


         // save phone number
         //if we are processing data from a admin template
         if( null == req.getParameter( "userTemplate" ) ) {

            //save phone numbers from phonesV  ( phonesV : id, number, user_id, phonetype_id )
            if( null != phonesV && phonesV.size() > 0 ) {

               //First delete existing phone number from db
               imcref.sqlUpdateProcedure( "DelPhoneNr " + userToChangeId );

               // Then save all number from session into db ( phonesV : id, number, user_id, phonetype_id )
               for( int i = 0; i < phonesV.size(); i++ ) {
                  String[] aPhone = (String[])phonesV.elementAt( i );
                  String sqlStr = "phoneNbrAdd " + userToChangeId + ", '";//userId
                  sqlStr += aPhone[1] + "', '" + aPhone[3] + "'";//number, phonetype_id

                  log( "PhoneNrAdd: " + sqlStr );

                  imcref.sqlUpdateProcedure( sqlStr );
               }
            }

         } else {
            // We are processing data from a user template
            // Get all phone numbers for user
            String[][] phoneNbr = imcref.sqlProcedureMulti( "GetUserPhoneNumbers " + userToChangeId );

            // Get workPhoneId and mobilePhoneId
            String workPhoneId = "";
            String mobilePhoneId = "";
            if( phoneNbr != null ) {
               for( int i = 0; i < phoneNbr.length; i++ ) {
                  if( ("2").equals( phoneNbr[i][3] ) ) {
                     workPhoneId = phoneNbr[i][0];
                  }
                  if( ("3").equals( phoneNbr[i][3] ) ) {
                     mobilePhoneId = phoneNbr[i][0];
                  }
               }
            }

            String workPhone = req.getParameter( "workphone" );
            String mobilePhone = req.getParameter( "mobilephone" );
            // add new workphone
            if( ("").equals( workPhoneId ) && !("").equals( workPhone ) ) {

               String sqlStr = "phoneNbrAdd " + userToChangeId + ", '"; // user_id
               sqlStr += workPhone + "', 2";//number, phonetype_id
               log( "PhoneNrAdd: " + sqlStr );
               imcref.sqlUpdateProcedure( sqlStr );

               // uppdate a workphone
            } else if( !("").equals( workPhoneId ) && !("").equals( workPhone ) ) {

               String sqlStr = "phoneNbrUpdate " + userToChangeId + ", " + workPhoneId + ", '"; // user_id , phone_id
               sqlStr += workPhone + "', 2";//number , phoneType
               imcref.sqlUpdateProcedure( sqlStr );

               // delete a workphone
            } else if( !("").equals( workPhoneId ) && ("").equals( workPhone ) ) {

               String sqlStr = "phoneNbrDelete " + workPhoneId + "'"; // phone_id
               imcref.sqlUpdateProcedure( sqlStr );
            }


            // add new mobilephone
            if( ("").equals( mobilePhoneId ) && !("").equals( mobilePhone ) ) {

               String sqlStr = "phoneNbrAdd " + userToChangeId + ", '"; // user_id
               sqlStr += mobilePhone + "', 3";//number, phonetype_id
               log( "PhoneNrAdd: " + sqlStr );
               imcref.sqlUpdateProcedure( sqlStr );

               // uppdate a mobilephone
            } else if( !("").equals( mobilePhoneId ) && !("").equals( mobilePhone ) ) {

               String sqlStr = "phoneNbrUpdate " + userToChangeId + ", " + mobilePhoneId + ", '"; // user_id , phone_id
               sqlStr += mobilePhone + "', 3";//number , phoneType
               imcref.sqlUpdateProcedure( sqlStr );

               // delete a mobilephone
            } else if( !("").equals( mobilePhoneId ) && ("").equals( mobilePhone ) ) {
               String sqlStr = "phoneNbrDelete " + mobilePhoneId + "'"; // phone_id
               imcref.sqlUpdateProcedure( sqlStr );
            }
         }



         // Lets build the users information into a string and add it to db
         String[] procParam = extractUpdateUserSprocParametersFromProperties( params );
         log( "userSQL: " + Arrays.asList( procParam ) );
         imcref.sqlUpdateProcedure( "UpdateUser", procParam );


         // if we are processing data from a admin template and
         // if user isSuperadmin or
         // isUseradmin and not is going to change his own data
         // then we have to take care of userroles and ReadRunner data
         if( null == req.getParameter( "userTemplate" ) && ((isSuperadmin || isUseradmin && user.getUserId() != userToChange.getUserId())) ) {

            // Lets get the roles from htmlpage
            Vector rolesV = this.getRolesParameters( "roles", req, res );
            if( rolesV == null )
               return;


            // Lets add the new users roles. but first, delete users current Roles
            // and then add the new ones

            if( isSuperadmin ) { // delete all userroles
               int roleId = -1;
               imcref.sqlUpdateProcedure( "DelUserRoles", new String[]{userToChangeId, "" + roleId} );

            } else {  // delete only roles that the useradmin has permission to administrate
               String[] rolesArr = imcref.sqlProcedure( "GetUseradminPermissibleRoles " + user.getUserId() );
               for( int i = 0; i < rolesArr.length; i += 2 ) {
                  imcref.sqlUpdateProcedure( "DelUserRoles", new String[]{userToChangeId, rolesArr[i]} );
               }
            }
            String roleId[] = imcref.sqlProcedure( "GetRoleIdByRoleName", new String[]{"Useradmin"} );
            boolean isSelected = false;
            for( int i = 0; i < rolesV.size(); i++ ) {
               String aRole = rolesV.elementAt( i ).toString();
               imcref.sqlUpdateProcedure( "AddUserRole", new String[]{userToChangeId, aRole} );
               if( aRole.equals( roleId[0] ) ) {
                  isSelected = true; // role Useradmin is selected
               }
            }


            // always let user get the role Users
            roleId = imcref.sqlProcedure( "GetRoleIdByRoleName Users" );
            if( roleId != null ) {
               imcref.sqlUpdateProcedure( "AddUserRole " + userToChangeId + ", " + Integer.parseInt( roleId[0] ) );
            }



            // Lets add the new useradmin roles. but first, delete the current roles
            // and then add the new ones
            // but only if role Useradmin is selected

            imcref.sqlUpdateProcedure( "DeleteUseradminPermissibleRoles " + userToChangeId );

            if( isSelected ) { // role Useradmin is selected

               // Lets get the useradmin_roles from htmlpage
               Vector useradminRolesV = this.getRolesParameters( "useradmin_roles", req, res );
               if( useradminRolesV == null )
                  return;

               for( int i = 0; i < useradminRolesV.size(); i++ ) {
                  String aRole = useradminRolesV.elementAt( i ).toString();
                  imcref.sqlUpdateProcedure( "AddUseradminPermissibleRoles  " + userToChangeId + ", " + aRole );
               }
            }

            // Lets add Readrunner user data
            if( null != session.getAttribute( "tempRRUserData" ) ) {
               ReadrunnerUserData rrUserData = (ReadrunnerUserData)session.getAttribute( "tempRRUserData" );
               imcref.setReadrunnerUserData( userToChange, rrUserData );
            }
         }

         this.goNext( req, res, session );
         return;
      }


      // ******** GO_BACK TO THE MENY ***************
      if( req.getParameter( "CANCEL" ) != null ) {

         String url = "AdminUser"; // default if we are processing a admin template

         if( null != session.getAttribute( "next_url" ) ) {
            url = (String)session.getAttribute( "next_url" );
         }


         this.removeSessionParams( req );
         res.sendRedirect( url );
         return;
      }

      // ******** UNIDENTIFIED ARGUMENT TO SERVER ********
      this.log( "Unidentified argument was sent!" );
      doGet( req, res );
      return;

   } // end HTTP POST

   private String addNewUser( IMCServiceInterface imcref, Properties params ) {
      String newUserId = imcref.sqlProcedureStr( "GetHighestUserId" );

      // Lets build the users information into a string and add it to db
      params.setProperty( "user_id", newUserId );
      String[] procParams = extractUpdateUserSprocParametersFromProperties( params );
      log( "AddNewUser " + Arrays.asList( procParams ).toString() );
      imcref.sqlUpdateProcedure( "AddNewUser", procParams );
      return newUserId;
   }


   /**
    Removes temporary parameters from the session
    */
   public void removeSessionParams( HttpServletRequest req ) throws ServletException, IOException {
      HttpSession session = req.getSession( false );
      if( session == null )
         return;
      try {


         //session.removeAttribute("country_code");
         //session.removeAttribute("area_code");
         //session.removeAttribute("local_code");
         //session.removeAttribute ("RESET_usersArr");
         // session.removeAttribute ("RESET_userCreateDate");
         session.removeAttribute( "Ok_phoneNumbers" );
         // session.removeAttribute ("RESET_langList");
         // session.removeAttribute ("RESET_selectedLangV");
         session.removeAttribute( "userToChange" );
         session.removeAttribute( "tempRRUserData" );
         session.removeAttribute( "next_url" );
         session.removeAttribute( "tempUser" );
         session.removeAttribute( "tempUserRoles" );
         session.removeAttribute( "tempUserType" );
         session.removeAttribute( "tempUseradminRoles" );

      } catch( IllegalStateException ise ) {
         log( "session has been invalidated so no need to remove parameters" );
      }
   }

   /**
    a error page will be generated, fore those times the user uses the backstep in
    the browser
    */
   private void sendErrorMsg( HttpServletRequest req, HttpServletResponse res, String header, String msg ) throws ServletException, IOException {

      new AdminError( req, res, header, msg );
   }


   /**
    Returns a Vector, containing the choosed roles from the html page. if Something
    failes, a error page will be generated and null will be returned.
    */

   public Vector getRolesParameters( String name, HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {

      // Lets get the roles
      // Vector rolesV = this.getRolesParameters(req) ;
      String[] roles = (req.getParameterValues( name ) == null) ? new String[0] : (req.getParameterValues( name ));
      Vector rolesV = new Vector( java.util.Arrays.asList( roles ) );
      if( rolesV.size() == 0 && name.equals( "roles" ) ) { // user must get at least one user role
         String header = "Roles error";
         String msg = "Ingen roll var vald." + "<BR>";
         this.log( "Error in checking roles" );
         new AdminError( req, res, header, msg );
         return null;
      }
      //this.log("Roles:"+ rolesV.toString()) ;
      return rolesV;

   } // End getRolesParameters


   /**
    Returns a Vector, containing the choosed phone numbers from the html page.
    */

   public Vector getPhonesParameters( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {

      // Lets get the phones
      String[] phones = (req.getParameterValues( "user_phones" ) == null) ? new String[0] : (req.getParameterValues( "user_phones" ));
      Vector phonesV = new Vector( java.util.Arrays.asList( phones ) );
      return phonesV;

   } // End getPhonesParameters


   /**
    Redirect to next Url
    */

   public void goNext( HttpServletRequest req, HttpServletResponse res, HttpSession session ) throws ServletException, IOException {

      String nexturl = "AdminUser";  // default if we are processing a admin template


      if( null != req.getParameter( "next_url" ) ) {
         nexturl = req.getParameter( "next_url" );
      }

      // lets session-object override req-object
      if( null != session.getAttribute( "next_url" ) ) {
         nexturl = (String)session.getAttribute( "next_url" );
      }

      // lets next_meta override next_url if we got both
      if( null != req.getParameter( "next_meta" ) ) {
         nexturl = "GetDoc?meta_id=" + req.getParameter( "next_meta" );
      }

      this.removeSessionParams( req );

      res.sendRedirect( nexturl );
   }


   /**
    Collects the parameters from the request object
    **/

   public Properties getParameters( HttpServletRequest req, IMCServiceInterface imcref, User user, User userToChange ) throws ServletException, IOException {

      Properties userInfo = new Properties();
      // Lets get the parameters we know we are supposed to get from the request object
      String login_name = (req.getParameter( "login_name" ) == null) ? "" : (req.getParameter( "login_name" ));
      String password1 = (req.getParameter( "password1" ) == null) ? "" : (req.getParameter( "password1" ));
      String password2 = (req.getParameter( "password2" ) == null) ? "" : (req.getParameter( "password2" ));

      String first_name = (req.getParameter( "first_name" ) == null) ? "" : (req.getParameter( "first_name" ));
      String last_name = (req.getParameter( "last_name" ) == null) ? "" : (req.getParameter( "last_name" ));
      String title = (req.getParameter( "title" ) == null) ? "" : (req.getParameter( "title" ));
      String company = (req.getParameter( "company" ) == null) ? "" : (req.getParameter( "company" ));

      String address = (req.getParameter( "address" ) == null) ? "" : (req.getParameter( "address" ));
      String city = (req.getParameter( "city" ) == null) ? "" : (req.getParameter( "city" ));
      String zip = (req.getParameter( "zip" ) == null) ? "" : (req.getParameter( "zip" ));
      String country = (req.getParameter( "country" ) == null) ? "" : (req.getParameter( "country" ));
      String country_council = (req.getParameter( "country_council" ) == null) ? "" : (req.getParameter( "country_council" ));
      String email = (req.getParameter( "email" ) == null) ? "" : (req.getParameter( "email" ));
      String language = (req.getParameter( "lang_id" ) == null) ? "1" : (req.getParameter( "lang_id" ));
      String user_type = (req.getParameter( "user_type" ) == null) ? "" : (req.getParameter( "user_type" ));
      String active = (req.getParameter( "active" ) == null) ? "0" : (req.getParameter( "active" ));


      //boolean isAdmin = checkAdminRights(user, imcref);

      // check if user is a Superadmin, adminRole = 1
      boolean isSuperadmin = imcref.checkUserAdminrole( user.getUserId(), 1 );

      // if we are going to change a user
      if( userToChange != null ) {

         // and if user is not a Superadmin and is going to change his own userdata
         // then lets get current values for the user
         if( !isSuperadmin && userToChange.getUserId() == user.getUserId() ) {
            user_type = "" + userToChange.getUserType();
            active = userToChange.isActive() ? "1" : "0";
         }
      }


      // Lets fix those fields which arent mandatory
      if( req.getParameter( "SAVE_USER" ) != null ) {
         if( title.trim().equals( "" ) )
            title = "--";
         if( company.trim().equals( "" ) )
            company = "--";
         if( address.trim().equals( "" ) )
            address = "--";
         if( city.trim().equals( "" ) )
            city = "--";
         if( zip.trim().equals( "" ) )
            zip = "--";
         if( country.trim().equals( "" ) )
            country = "--";
         if( country_council.trim().equals( "" ) )
            country_council = "--";
         if( email.trim().equals( "" ) )
            email = "--";

      }

      userInfo.setProperty( "login_name", login_name );
      userInfo.setProperty( "password1", password1 );
      userInfo.setProperty( "password2", password2 );
      userInfo.setProperty( "first_name", first_name );
      userInfo.setProperty( "last_name", last_name );
      userInfo.setProperty( "title", title );
      userInfo.setProperty( "company", company );

      userInfo.setProperty( "address", address );
      userInfo.setProperty( "city", city );
      userInfo.setProperty( "zip", zip );
      userInfo.setProperty( "country", country );
      userInfo.setProperty( "country_council", country_council );
      userInfo.setProperty( "email", email );
      userInfo.setProperty( "lang_id", language );
      userInfo.setProperty( "user_type", user_type );
      userInfo.setProperty( "active", active );


      return userInfo;
   }

   /**
    Returns a Properties, containing the user information from the html page. if Something
    failes, a error page will be generated and null will be returned.
    */

   public Properties validateParameters( Properties aPropObj, HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {

      //	Properties params = this.getParameters(req) ;
      if( checkParameters( aPropObj ) == false ) {

         String header = "Checkparameters error";
         String msg = "Samtliga fält var inte korrekt ifyllda." + "<BR>";
         this.log( "Error in checkingparameters" );
         new AdminError( req, res, header, msg );
         return null;
      }
      return aPropObj;


   } // end validateParameters


   /**
    adds the phoneNrId to the vector followed by the phoneNumber
    return a new Vector with elements formated like
    ( phone_id, number, user_id, phonetype_id, typename   ex. { 10, 46 498 123456, 3, 1 } )
    */
   public Vector getPhonesArrayVector( String[][] phoneNr ) {
      Vector phonesArrV = new Vector();

      for( int i = 0; i < phoneNr.length; i++ ) {
         phonesArrV.addElement( phoneNr[i] );
      }
      return phonesArrV;
   }


   /** return a new Vector with elements formated like
    ( phone_id, (typename) number,   ex. { 10, (Hem) 46 498 123456 } )
    input vector  ex. 10, 46 498 123456, 3, 1
    */
   public Vector getPhonesVector( Vector phonesArrV, String lang_id, IMCServiceInterface imcref ) {

      Vector phonesV = new Vector();
      Enumeration enum = phonesArrV.elements();
      while( enum.hasMoreElements() ) {
         String[] tempPhone = (String[])enum.nextElement();
         String[] typename = imcref.sqlProcedure( "GetPhonetypeName " + tempPhone[3] + ", " + lang_id );
         String temp = "(" + typename[0] + ") " + tempPhone[1];

         phonesV.addElement( tempPhone[0] );
         phonesV.addElement( temp );
      }
      return phonesV;
   }


   boolean checkAdminRights( User user, IMCServiceInterface imcref ) {

      // check if user is a Superadmin, adminRole = 1
      boolean isSuperadmin = imcref.checkUserAdminrole( user.getUserId(), 1 );

      // check if user is a Useradmin, adminRole = 2
      boolean isUseradmin = imcref.checkUserAdminrole( user.getUserId(), 2 );

      if( isSuperadmin || isUseradmin ) {
         return true;
      }

      /*	else if ( user.getUserId() == userToChange.getUserId() ){
             return true;

         }
      */
      else {
         return false;
      }
   }


   public void log( String str ) {
      super.log( str );
      log.debug( "AdminUserProps: " + str );
   }


   // Create html for admin_part in AdminUserResp
   public String createAdminPartHtml( User user, User userToChange, IMCServiceInterface imcref, HttpServletRequest req, HttpServletResponse res, HttpSession session ) {

      String html_admin_part = "";
      Vector vec_admin_part = new Vector();
      Html htm = new Html();

      String[] userRoles = null;
      String[] useradminRoles = null;
      String userType = null;
      Properties userInfo = null;

      Vector userRolesV = new Vector();
      String rolesMenuStr = "";

      Vector useradminRolesV = new Vector();
      String rolesMenuUseradminStr = "";

      // check if user is a Superadmin, adminRole = 1
      boolean isSuperadmin = imcref.checkUserAdminrole( user.getUserId(), 1 );

      if( null != session.getAttribute( "tempUser" ) ) {

         //Lets get temporary values from session if there is some.
         userRoles = (String[])session.getAttribute( "tempUserRoles" );
         useradminRoles = (String[])session.getAttribute( "tempUseradminRoles" );
         userType = (String)session.getAttribute( "tempUserType" );
         userInfo = (Properties)session.getAttribute( "tempUser" );
      }

      // Lets get ROLES from DB
      String[] rolesArr = {};

      if( isSuperadmin ) {
         rolesArr = imcref.sqlProcedure( "GetAllRoles" );
      } else {
         rolesArr = imcref.sqlProcedure( "GetUseradminPermissibleRoles " + user.getUserId() );
      }
      Vector allRolesV = new Vector( java.util.Arrays.asList( rolesArr ) );



      //Lets get all ROLES from DB except of Useradmin and Superadmin
      Vector rolesV = (Vector)allRolesV.clone();

      ListIterator listiter = rolesV.listIterator();
      while( listiter.hasNext() ) {
         listiter.next();
         String rolename = listiter.next().toString();
         if( rolename.equals( "Superadmin" ) || rolename.equals( "Useradmin" ) ) {
            listiter.remove();
            listiter.previous();
            listiter.remove();
         }
      }


      // Lets get all USERTYPES from DB
      String[] usersArr = imcref.sqlProcedure( "GetUserTypes " + user.getLangPrefix() );
      Vector userTypesV = new Vector( java.util.Arrays.asList( usersArr ) );


      if( userToChange == null ) {   // ADD_USER mode


         // Lets set default usertype to 1 "Authenticated user" if we don´t have got any from session.
         if( userType == null ) {
            userType = "1";
         }
         log( "userType:" + userType );




         // Lets get the information for users roles and put them in a vector
         // if we don´t have got any roles from session we try to get them from request object
         if( userRoles == null ) {
            userRoles = (req.getParameterValues( "roles" ) == null) ? new String[0] : req.getParameterValues( "roles" );
         }
         userRolesV = new Vector( java.util.Arrays.asList( userRoles ) );

         log( "Size allRolesV: " + allRolesV.size() );
         log( "Size theUserRolesV: " + userRolesV.size() );

         // Lets create html option for user roles
         rolesMenuStr = htm.createHtmlCode( "ID_OPTION", userRolesV, allRolesV );


         if( isSuperadmin ) {
            // Lets get the information for usersadmin roles and put them in a vector
            // if we don´t have got any roles from session we try to get them from request object
            if( useradminRoles == null ) {
               useradminRoles = (req.getParameterValues( "useradmin_roles" ) == null) ? new String[0] : req.getParameterValues( "useradmin_roles" );
            }
            useradminRolesV = new Vector( java.util.Arrays.asList( useradminRoles ) );

            log( "Size theUsersdminRolesV: " + useradminRolesV.size() );

            // Lets create html option for useradmin roles
            rolesMenuUseradminStr = htm.createHtmlCode( "ID_OPTION", useradminRolesV, rolesV );
         }



         // Lets get the active flag from the session if we have any
         String active = "1";
         if( userInfo != null ) {
            active = userInfo.getProperty( "active" );
         }


         String user_type = htm.createHtmlCode( "ID_OPTION", userType, userTypesV );


         vec_admin_part.add( "#ACTIVE#" );
         vec_admin_part.add( "1" );
         vec_admin_part.add( "#ACTIVE_FLAG#" );
         if( active.equals( "1" ) ) {
            vec_admin_part.add( "checked" );
         } else {
            vec_admin_part.add( "" );
         }

         vec_admin_part.add( "#USER_CREATE_DATE#" );
         vec_admin_part.add( "&nbsp;" );
         vec_admin_part.add( "#USER_TYPES#" );
         vec_admin_part.add( user_type );
         vec_admin_part.add( "#ROLES_MENU#" );
         vec_admin_part.add( rolesMenuStr );
         vec_admin_part.add( "#ROLES_MENU_USERADMIN#" );
         if( isSuperadmin ) {
            vec_admin_part.add( rolesMenuUseradminStr );
         } else {
            vec_admin_part.add( "" );
         }


      } else {   // CHANGE_USER mode

         String active = "";

         // if OK_PHONES or DELETE_PHONES or EDIT_PHONES  was pressed we have to get values from req object
         if( req.getParameter( "ok_phones" ) != null || req.getParameter( "delete_phones" ) != null || req.getParameter( "edit_phones" ) != null ) {

            if( ("1").equals( req.getParameter( "active" ) ) ) {
               active = "1";
            }

            userRoles = (req.getParameterValues( "roles" ) == null) ? new String[0] : req.getParameterValues( "roles" );
            useradminRoles = (req.getParameterValues( "useradmin_roles" ) == null) ? new String[0] : req.getParameterValues( "useradmin_roles" );
            userType = req.getParameter( "user_type" );

         } else {

            // Lets get this user usertype from DB if we don´t have got them from session.
            if( userType == null ) {
               userType = imcref.sqlQueryStr( "GetUserType " + userToChange.getUserId() );
            }


            // Lets get the information for users roles and put them in a vector
            // if we don´t have got any roles from session we try to get them from DB
            if( userRoles == null ) {
               userRoles = imcref.sqlProcedure( "GetUserRolesIds " + userToChange.getUserId() );
            }


            if( isSuperadmin ) {
               // Lets get the information for usersadmin roles and put them in a vector
               // if we don´t have got any roles from session we try to get them from DB
               if( useradminRoles == null ) {
                  useradminRoles = imcref.sqlProcedure( "GetUseradminPermissibleRoles " + userToChange.getUserId() );
               }
            }


            active = (userToChange.isActive() == true) ? "1" : "0";
         }


         // Lets create html option for user types
         String user_type = htm.createHtmlCode( "ID_OPTION", userType, userTypesV );

         // Lets put the user roles in the vector
         userRolesV = new Vector( java.util.Arrays.asList( userRoles ) );

         // Lets create html option for user roles
         rolesMenuStr = htm.createHtmlCode( "ID_OPTION", userRolesV, allRolesV );

         if( isSuperadmin ) {
            // Lets put the roles that useradmin is allow to administrate in a vector
            useradminRolesV = new Vector( java.util.Arrays.asList( useradminRoles ) );

            // Lets create html option for useradmin roles
            rolesMenuUseradminStr = htm.createHtmlCode( "ID_OPTION", useradminRolesV, rolesV );
         }

         vec_admin_part.add( "#ACTIVE#" );
         vec_admin_part.add( "1" );
         vec_admin_part.add( "#ACTIVE_FLAG#" );
         if( ("1").equals( active ) ) {
            vec_admin_part.add( "checked" );
         } else {
            vec_admin_part.add( "" );
         }

         vec_admin_part.add( "#USER_CREATE_DATE#" );
         vec_admin_part.add( userToChange.getCreateDate() );
         vec_admin_part.add( "#USER_TYPES#" );
         vec_admin_part.add( user_type );
         vec_admin_part.add( "#ROLES_MENU#" );
         vec_admin_part.add( rolesMenuStr );
         vec_admin_part.add( "#ROLES_MENU_USERADMIN#" );
         if( isSuperadmin ) {
            vec_admin_part.add( rolesMenuUseradminStr );
         } else {
            vec_admin_part.add( "" );
         }


      }

      // lets parse and return the html_admin_part
      if( isSuperadmin ) {
         html_admin_part = imcref.parseDoc( vec_admin_part, HTML_RESPONSE_SUPERADMIN_PART, user.getLangPrefix() );
      } else {
         html_admin_part = imcref.parseDoc( vec_admin_part, HTML_RESPONSE_ADMIN_PART, user.getLangPrefix() );
      }
      return html_admin_part;

   }


   /**
    Returns a String, containing the userID in the request object.If something failes,
    a error page will be generated and null will be returned.
    */

   public String getCurrentUserId( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {


      String userId = req.getParameter( "CURR_USER_ID" );

      // Get the session
      HttpSession session = req.getSession( false );

      if( userId == null ) {
         // Lets get the userId from the Session Object.
         userId = (String)session.getAttribute( "userToChange" );
      }

      if( userId == null ) {
         String header = "ChangeUser error. ";
         String msg = "No user_id was available." + "<BR>";
         this.log( header + msg );
         new AdminError( req, res, header, msg );
         return null;
      } else {
         this.log( "AnvändarId=" + userId );
      }
      return userId;
   } // End getCurrentUserId


   public String doPasswordString( String pwd ) {
      // Lets fix the password string
      int len = pwd.length();
      pwd = "";
      for( int i = 0; i < len; i++ ) {
         pwd += "*";
      }
      return pwd;
   }

   /**
    Validates the username. Returns true if the login_name doesnt exists.
    Returns false if the username exists
    */
   private static boolean checkExistingUserName( IMCServiceInterface imcref, Properties prop ) {
      boolean result = true;
      String userName = prop.getProperty( "login_name" );
      String userNameExists[] = imcref.sqlProcedure( "FindUserName '" + userName + "'" );
      if( userNameExists != null ) {
         if( userNameExists.length > 0 ) {
            result = false;
         }
      }
      return result;
   } // CheckExistingUserName

   /**
    Validates the password. Password must contain at least 4 characters
    Generates an errorpage and returns false if something goes wrong
    */

   static boolean verifyPassword( Properties prop, HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {

      String pwd1 = prop.getProperty( "password1" );
      String pwd2 = prop.getProperty( "password2" );
      String header = "Verify password error";

      if( !pwd1.equals( pwd2 ) ) {
         header = req.getServletPath();
         new AdminError2( req, res, header, 52 );
         return false;
      }

      if( pwd1.length() < 4 ) {
         header = req.getServletPath();
         new AdminError2( req, res, header, 53 );
         return false;
      }

      return true;

   } // End verifyPassword

   /**
    Creates a sql parameter array used to run sproc updateUser
    **/
   static String[] extractUpdateUserSprocParametersFromProperties( Properties props ) {

      Logger log = Logger.getLogger( AdminUserProps.class );
      log.debug( "extractUpdateUserSprocParametersFromProperties + props: " + props.toString() );

      String[] params = {props.getProperty( "user_id" ), (props.getProperty( "login_name" )).trim(), (props.getProperty( "password1" )).trim(), (props.getProperty( "first_name" )).trim(), (props.getProperty( "last_name" )).trim(), (props.getProperty( "title" )).trim(), (props.getProperty( "company" )).trim(), (props.getProperty( "address" )).trim(), (props.getProperty( "city" )).trim(), (props.getProperty( "zip" )).trim(), (props.getProperty( "country" )).trim(), (props.getProperty( "country_council" )).trim(), (props.getProperty( "email" )).trim(), "0", "1001", "0", props.getProperty( "lang_id" ), props.getProperty( "user_type" ), props.getProperty( "active" )};

      return params;
   }

}
