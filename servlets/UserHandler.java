
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;

import imcode.external.diverse.*;
import imcode.util.*;
import imcode.server.*;
import org.apache.log4j.Logger;

public class UserHandler /*extends Administrator*/ {
   private final static String CVS_REV = "$Revision$";
   private final static String CVS_DATE = "$Date$";

   /**
    Creates hea sql string string used to run sproc updateUser
    **/

   public static String[] createUserInfoString( Properties props ) {

      Logger log = Logger.getLogger( "UserHandler" );
      log.debug( "createUserInfoString + props: " + props.toString()) ;

      String[] params = {
      props.getProperty( "user_id" ) ,
      (props.getProperty( "login_name" )).trim() ,
      (props.getProperty( "password1" )).trim() ,
      (props.getProperty( "first_name" )).trim() ,
      (props.getProperty( "last_name" )).trim() ,
      (props.getProperty( "title" )).trim() ,
      (props.getProperty( "company" )).trim() ,
      (props.getProperty( "address" )).trim() ,
      (props.getProperty( "city" )).trim() ,
      (props.getProperty( "zip" )).trim() ,
      (props.getProperty( "country" )).trim() ,
      (props.getProperty( "country_council" )).trim() ,
      (props.getProperty( "email" )).trim() ,
      "1" , //todo
      "1001",
      "0",
      props.getProperty( "lang_id" ),
      props.getProperty( "user_type" ),
      props.getProperty( "active" ) };
      return params;
   }

   /**
    Validates the password. Password must contain at least 4 characters
    Generates an errorpage and returns false if something goes wrong
    */

   public static boolean verifyPassword( Properties prop, HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {

      String pwd1 = prop.getProperty( "password1" );
      String pwd2 = prop.getProperty( "password2" );
      String header = "Verify password error";
      String msg = "";

      if( !pwd1.equals( pwd2 ) ) {
         header = req.getServletPath();
         AdminError2 err = new AdminError2( req, res, header, 52 );
         //log(header + err.getErrorMsg()) ;
         return false;
      }

      if( pwd1.length() < 4 ) {
         header = req.getServletPath();
         AdminError2 err = new AdminError2( req, res, header, 53 );
         //log(header + err.getErrorMsg()) ;
         return false;
      }

      return true;

   } // End verifyPassword

} // End of class
