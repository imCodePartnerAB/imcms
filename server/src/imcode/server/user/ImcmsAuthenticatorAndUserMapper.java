package imcode.server.user;

import org.apache.log4j.Logger;
import imcode.server.IMCServiceInterface;

public class ImcmsAuthenticatorAndUserMapper implements UserMapper, Authenticator {

   private IMCServiceInterface service;
   private final Logger mainLog;

   public ImcmsAuthenticatorAndUserMapper( IMCServiceInterface service, Logger mainLog ) {
      this.service = service;
      this.mainLog = mainLog;
   }

   public boolean authenticate( String loginName, String password ) {
      boolean userExistsAndPasswordIsCorrect = false ;
      imcode.server.user.User user = getUser( loginName );
      if (null != user) {
         String login_password_from_db = user.getPassword();
         String login_password_from_form = password;

         if( login_password_from_db.equals( login_password_from_form ) && user.isActive() ) {
            mainLog.info( "->User " + loginName + " succesfully logged in." );
            userExistsAndPasswordIsCorrect = true ;
         } else if( !user.isActive() ) {
            mainLog.info( "->User " + (loginName) + " tried to logged in: User deleted!" );
            userExistsAndPasswordIsCorrect  = false;
         } else {
            mainLog.info( "->User " + (loginName) + " tried to logged in: Wrong password!" );
            userExistsAndPasswordIsCorrect = false;
         }
      }

      return userExistsAndPasswordIsCorrect ;
   }

   public imcode.server.user.User getUser( String loginName) {
      loginName = loginName.trim();

      imcode.server.user.User result = null;
      String[] user_data = service.sqlProcedure( "GetUserByLogin", new String[]{loginName} );

      // if resultSet > 0 a result is found
      if( user_data.length > 0 ) {

         result = new imcode.server.user.User();

         result.setUserId( Integer.parseInt( user_data[0] ) );
         result.setLoginName( user_data[1] );
         result.setPassword( user_data[2].trim() );
         result.setFirstName( user_data[3] );
         result.setLastName( user_data[4] );
         result.setTitle( user_data[5] );
         result.setCompany( user_data[6] );
         result.setAddress( user_data[7] );
         result.setCity( user_data[8] );
         result.setZip( user_data[9] );
         result.setCountry( user_data[10] );
         result.setCountyCouncil( user_data[11] );
         result.setEmailAddress( user_data[12] );
         result.setLangId( Integer.parseInt( user_data[13] ) );
         result.setUserType( Integer.parseInt( user_data[15] ) );
         result.setActive( 0 != Integer.parseInt( user_data[16] ) );
         result.setCreateDate( user_data[17] );
         result.setLangPrefix( user_data[14] );
         result.setImcmsExternal( 0 != Integer.parseInt( user_data[18] ));

         String[][] phoneNbr = service.sqlProcedureMulti( "GetUserPhoneNumbers " + user_data[0] );
         String workPhone = "";
         String mobilePhone = "";
         String homePhone = "";

         if( phoneNbr != null ) {
            for( int i = 0; i < phoneNbr.length; i++ ) {
               if( ("2").equals( phoneNbr[i][3] ) ) {
                  workPhone = phoneNbr[i][1];
               } else if( ("3").equals( phoneNbr[i][3] ) ) {
                  mobilePhone = phoneNbr[i][1];
               } else if( ("1").equals( phoneNbr[i][3] ) ) {
                  homePhone = phoneNbr[i][1];
               }
            }
         }
         result.setWorkPhone( workPhone );
         result.setMobilePhone( mobilePhone );
         result.setHomePhone( homePhone );



      } else {
         mainLog.info( "->User " + (loginName) + " tried to logged in: User not found!" );
         result = null;
      }

      return result;
   }

   /**
    @return An object representing the user with the given id.
    **/
   public User getUser( int userId ) {
      String[] user_data = service.sqlProcedure( "GetUserInfo ", new String[]{"" + userId} );
      User result = getUser( user_data[1] );
      return result;
   }



   public void update( String loginName, imcode.server.user.User newUserData ) {
      imcode.server.user.User imcmsUser = getUser( loginName );

      imcode.server.user.User tempUser = (imcode.server.user.User)newUserData.clone();
      tempUser.setUserId( imcmsUser.getUserId() );

      String updateUserPRCStr = "updateuser";
      String[] params = {
         String.valueOf(tempUser.getUserId()),
         loginName,
         tempUser.getPassword(),
         tempUser.getFirstName(),
         tempUser.getLastName(),
         tempUser.getTitle(),
         tempUser.getCompany(),
         tempUser.getAddress(),
         tempUser.getCity(),
         tempUser.getZip(),
         tempUser.getCountry(),
         tempUser.getCountyCouncil(),
         tempUser.getEmailAddress(),
         "0",
         "1001",
         "0",
         String.valueOf(tempUser.getLangId()),
         String.valueOf( tempUser.getUserType() ),
         tempUser.isActive()?"1":"0" };


      service.sqlUpdateProcedure( updateUserPRCStr, params );
   }
}