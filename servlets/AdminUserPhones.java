import java.io.* ;
import javax.servlet.*;
import javax.servlet.http.*;
//import java.awt.* ;
import java.util.* ;
import imcode.external.diverse.* ;
import imcode.util.* ;

public class AdminUserPhones  extends Administrator {
	String HTML_IP_SNIPPET ;             	
	String HTML_TEMPLATE ;	
   String ADD_IP_TEMPLATE ;
 	String WARN_DEL_IP_TEMPLATE ;

/**
   The GET method creates the html page when this side has been
   redirected from somewhere else.
**/

public void doGet(HttpServletRequest req, HttpServletResponse res)
  throws ServletException, IOException {

  String host 				= req.getHeader("Host") ;
  String server 			= Utility.getDomainPref("adminserver",host) ;

// Lets validate the session
 	if (super.checkSession(req,res) == false)	return ;

// Lets get an user object
  imcode.server.User user = super.getUserObj(req,res) ;
  if(user == null) {
    String header = "Error in AdminUserPhones." ;
    String msg = "Couldnt create an user object."+ "<BR>" ;
    this.log(header + msg) ;
    AdminError2 err = new AdminError2(req,res,header,msg) ;
    return ;
  }

// Lets get the user id we are working on
	String userId = this.getCurrentUserId(req,res) ;

//    HttpSession session = req.getSession(false) ;
//    String userId = null ;
//    if(session != null  ) {
  	  // session.putValue("AdminUser.user_id", userId ) ;
//  	  userId = (String) session.getValue("AdminUser.user_id") ;
//    }

// ********** GENERATE THE PHONES PAGE *********
     String phone_list = "adminuserphonelist.htm" ;
     String phone_page = "adminuserphone.htm" ;
     VariableManager vm = new VariableManager() ;

   // Lets get the phonenumbers for the user
  //	 String userId = this.getCurrentUserId(req,res) ;
     RmiLayer rmi = new RmiLayer(user) ;
     String[][] multi= rmi.execProcedureMulti(server, "GetUserPhoneNumbers " + userId) ;
    // Vector v =  this.getOneRow(multi, 1) ;
    // log("Multisize: " + multi.length ) ;

   // Lets build the variables for each record
      Vector tags = new Vector() ;
      tags.add("PHONE_ID") ;
      tags.add("COUNTRY_CODE") ;
      tags.add("AREA_CODE") ;
      tags.add("NUMBER") ;
      tags.add("USER_ID") ;
   // Lets parse each record and put it in a string
      String recs = "" ;
      int nbrOfRows = getNbrOfRows(multi) ;
      for (int counter = 0 ; counter< nbrOfRows ; counter++) {
          Vector aRecV = this.getOneRow(multi, counter) ;
          VariableManager vmRec = new VariableManager() ;
          vmRec.merge(tags, aRecV) ;
          vmRec.addProperty("RECORD_COUNTER" , "" + counter) ;
         // log(vmRec.toString()) ;
          recs += this.createHtml(req,res,vmRec, phone_list) ;
         // log(recs) ;
      }

   // Lets generate the html page
      vm.addProperty("ALL_USER_PHONES", recs) ;
      String userName = rmi.execSqlProcedureStr(server,"GetUserNames " + userId + ", 5") ;
      vm.addProperty("USER_NAME", userName) ;
      this.sendHtml(req,res,vm, phone_page) ;

} // End doGet

/**
	POST
**/

public void doPost(HttpServletRequest req, HttpServletResponse res)
  throws ServletException, IOException {

  String host 				= req.getHeader("Host") ;
  String server 			= Utility.getDomainPref("adminserver",host) ;

// Lets validate the session
	if (super.checkSession(req,res) == false) return ;

// Lets get an user object
  imcode.server.User user = super.getUserObj(req,res) ;
  if(user == null) {
    String header = "Error in AdminUserPhones." ;
    String msg = "Couldnt create an user object."+ "<BR>" ;
    this.log(header + msg) ;
    AdminError err = new AdminError(req,res,header,msg) ;
    return ;
  }
 /*
  // Lets get the user id we are working on
    HttpSession session = req.getSession(false) ;
    String theUserId = null ;
    if(session != null  ) {
    	  // session.putValue("AdminUser.user_id", userId ) ;
    	  theUserId = (String) session.getValue("AdminUser.user_id") ;
    }
   */
    String theUserId  = this.getCurrentUserId(req,res) ;

  // ******* GENERATE THE ADD A NEW PHONENBR PAGE TO USER **********
  if( req.getParameter("ADD_NEW_PHONE_PAGE") != null) {

  // Lets get all USERS from DB
     RmiLayer rmi = new RmiLayer(user) ;
     String[] usersArr = rmi.execSqlProcedure(server, "GetAllUsersInList") ;
     Vector usersV  = super.convert2Vector(usersArr) ;
     Html ht = new Html() ;
     String usersOption = ht.createHtmlCode("ID_OPTION", "", usersV ) ;

  // Lets generate the html page
     VariableManager vm = new VariableManager() ;
     vm.addProperty("USERS_LIST", usersOption  ) ;
     this.sendHtml(req,res,vm, "adminuserphoneadd.htm") ;
     return ;
 	}

 // *************** RETURN TO ADMINMANAGER *****************
	if( req.getParameter("CANCEL_PHONE") != null) {

   // Dirty...
  // Lets check if we have session variable set
    HttpSession session = req.getSession(false) ;
    String whereToReturn = null ;
    if(session != null  ) {
    	  whereToReturn = (String) session.getValue("UserChangePrefs.goBack") ;
        session.removeValue("UserChangePrefs.goBack") ;
    }

    if( whereToReturn != null && whereToReturn.equals("1") )
       res.sendRedirect(MetaInfo.getServletPath(req) + "UserChangePrefs?changeUser=on") ;
    res.sendRedirect(MetaInfo.getServletPath(req) + "AdminUser?CHANGE_USER=on") ;


    	return ;
	}

  // ******* RETURN TO THE NORMAL ADMIN PHONES PAGE **********
  if( req.getParameter("CANCEL_ADD_PHONE") != null ) {
      log("CancelAddPhone") ;
      res.sendRedirect(MetaInfo.getServletPath(req) + "AdminUserPhones?ADD_NEW_PHONE_PAGE=on") ;
      return ;
  }

// ******* ADD A NEW PHONE NBR TO DB **********
  if( req.getParameter("ADD_PHONE_NBR") != null) {
    log("Now's add a new phone number") ;

// Lets get the parameters from html page and validate them
    Properties params = this.getAddParameters(req) ;
    String userId = user.getString("user_id") ;
    //log("PARAMS: " + params.toString()) ;
    params = this.validateParameters(params,req,res) ;
    if(params == null) return ;

// Lets build the users information into a string and add it to db
    String sqlStr = "phoneNbrAdd " + theUserId + ", '" ;
    sqlStr += params.get("COUNTRY_CODE") + "', '" + params.get("AREA_CODE") + "', '" ;
    sqlStr += params.get("LOCAL_CODE") + "'"  ;
    log("PhoneNbrAdd sql: " + sqlStr ) ;

    RmiLayer rmi = new RmiLayer(user) ;
    rmi.execSqlUpdateProcedure(server, sqlStr) ;
    res.sendRedirect(MetaInfo.getServletPath(req) + "AdminUserPhones?ADD_NEW_PHONE_PAGE=on") ;
    return ;
	}


 // ******** SAVE AN EXISTING PHONE NBR TO DB ***************

  if( req.getParameter("RESAVE_PHONES") != null) {

	// Lets get all the ip_access id:s
  	String[] reSavesIds = this.getEditedFields(req) ;
  	RmiLayer rmi = new RmiLayer(user) ;

	// Lets resave all marked ip-accesses.
		if( reSavesIds != null ) {
			for(int i = 0 ; i < reSavesIds.length ; i++ ){
				log("ResaveId: " +  reSavesIds[i]) ;
        String tmpId = reSavesIds[i] ;
        // Lets get all edited fields for that ip-access
        String phoneId = req.getParameter("PHONE_ID_" + tmpId) ;
        String aUserId = req.getParameter("PHONE_USER_ID_" + tmpId) ;

        String country = req.getParameter("COUNTRY_CODE_" + tmpId) ;
        String area = req.getParameter("AREA_CODE_" + tmpId) ;
 				String nbr = req.getParameter("NUMBER_" + tmpId) ;

        String sqlQ = "PhoneNbrUpdate " + aUserId + ", " + phoneId + ", '" ;
        sqlQ += country + "', '" + area + "', '" + nbr + "'" ;
        // log("UpdatePhoneSql: " + sqlQ) ;
    		rmi.execSqlUpdateProcedure(server, sqlQ) ;
  		}
		}

	 	this.doGet(req, res) ;
    return ;
  }

  // ***** GENERATE THE LAST DELETE IP-ACCESS WARNING PAGE  **********
/*
  if( req.getParameter("IP_WARN_DELETE") != null ) {

  // Lets get the parameters from html page and validate them
  	String[] deleteIds = this.getEditedIpAccesses(req) ;
		session = req.getSession(false) ;
    if(session != null ) {
    	Enumeration enumNames  = req.getParameterNames() ;
      while( enumNames.hasMoreElements() ) {
         String paramName = (String) (enumNames.nextElement()) ;
				 String arr[] = req.getParameterValues(paramName) ;
         //if(arr.length == 1) { // its a string
         //  log("Det är en sträng: " + arr[0]) ;
         //  session.putValue("IP." + paramName, arr[0]) ;
         //} else { // its an array
         //		log("Det är en array: " + arr[0]) ;
         // 	session.putValue("IP." + paramName , arr) ;
      	// }
      }
    } else { 
       	String header = "Delete IP-Access error" ;
  		  String msg = "A session could not be created. Please try again + " + "<BR>";
	 	  	this.log("Error in IP-access delete") ;
  	  	AdminError err = new AdminError(req,res,header, msg) ;
	 	  	return ;
		}  

 		// Lets generate the last warning html page	
 		VariableManager vm = new VariableManager() ;
    this.sendHtml(req,res,vm, WARN_DEL_IP_TEMPLATE) ;
   	return ;
  }  
 */

 // ******** DELETE A PHONE NBR FROM DB ***************
 if( req.getParameter("DELETE_PHONES") != null) {
 	 HttpSession session = req.getSession(false) ;
    if(session != null ) {
     	log("Ok, delete a phonenbr: " + session.toString()) ;

      String[] deleteIds = getEditedFields(req) ;
			RmiLayer rmi = new RmiLayer(user) ;

	  // Lets resave all marked ip-accesses.
		  if( deleteIds != null ) {
			 for(int i = 0 ; i < deleteIds.length ; i++ ){
			  String tmpId = deleteIds[i] ;
        // Lets get all edited fields for that ip-access
        String phoneId = req.getParameter("PHONE_ID_" + tmpId) ;
        //String aUserId = req.getParameter("PHONE_USER_ID_" + tmpId) ;
         //String country = req.getParameter("COUNTRY_CODE_" + tmpId) ;
        //String area = req.getParameter("AREA_CODE_" + tmpId) ;
 				//String nbr = req.getParameter("NUMBER_" + tmpId) ;

        String sqlQ = "PhoneNbrDelete " + phoneId ;
    		rmi.execSqlUpdateProcedure(server, sqlQ) ;
  		 }
		  }
    } else {
       	String header = "Delete phone number error" ;
	 	  	this.log("Error in adminUserPhones delete") ;
        String msg = "Could not delete phone id "  ;
  	  	AdminError err = new AdminError(req,res,header, msg) ;
	 	  	return ;
		}
  	this.doGet(req, res) ;
    return ;
  }

} // end HTTP POST



	/**
	 Returns all fields for a post with. if were trying to get a row outside the 
   available nbr of rows, an emty vector is returned
	**/
  


/**
  Collects the parameters from the request object for the add function 
**/
public Properties getAddParameters( HttpServletRequest req) throws ServletException, IOException {
    
  Properties info = new Properties() ;
  // Lets get the parameters we know we are supposed to get from the request object
 // String user_id = (req.getParameter("USER_ID")==null) ? "" : (req.getParameter("USER_ID")) ;
  String country = (req.getParameter("country_code")==null) ? "" : (req.getParameter("country_code")) ;
  String area = (req.getParameter("area_code")==null) ? "" : (req.getParameter("area_code")) ;
  String nbr = (req.getParameter("local_code")==null) ? "" : (req.getParameter("local_code")) ;

 // info.setProperty("USER_ID", user_id.trim()) ;
  info.setProperty("COUNTRY_CODE", country.trim()) ;
  info.setProperty("AREA_CODE", area.trim()) ;
  info.setProperty("LOCAL_CODE", nbr.trim()) ;
  return info ;
}
  
/**
	Collects the parameters used to delete a reply
**/

	public String[] getEditedFields( HttpServletRequest req )
		throws ServletException, IOException {

	// Lets get the standard discussion_id to delete
		String[] replyId = (req.getParameterValues("EDIT_PHONE_ID")) ;
		return replyId ;
	}


/**
	Returns a Properties, containing the user information from the html page. if Something
	failes, a error page will be generated and null will be returned.
*/
	
public Properties validateParameters(Properties aPropObj, HttpServletRequest req,
	HttpServletResponse res) throws ServletException, IOException {

	 	if(checkParameters(aPropObj) == false) {
	 	  String header = "Checkparameters error" ;
  	        String msg = "Samtliga fält var inte korrekt ifyllda." + "<BR>";
	 	  this.log("Error in checkingparameters") ;
  	        AdminError err = new AdminError(req,res,header, msg) ;
	 	  return null;
	 	} 
	 	return aPropObj ;

} // end checkParameters


				
/**
	Init: Detects paths and filenames.


public void init(ServletConfig config) throws ServletException {
  super.init(config);
  HTML_TEMPLATE = "AdminIpAccess.htm";
  HTML_IP_SNIPPET = "AdminIpAccessList.htm";
  ADD_IP_TEMPLATE = "AdminIpAccess_Add.htm";
	WARN_DEL_IP_TEMPLATE = "AdminIpAccess_Delete2.htm" ;
}
 */
public void log( String str) {
	super.log(str) ;
	System.out.println("AdminUserPhones: " + str ) ;	
}
			
/**
	Returns a String, containing the userID in the request object.If something failes,
	a error page will be generated and null will be returned.
*/

public String getCurrentUserId(HttpServletRequest req, HttpServletResponse res)
	  throws ServletException, IOException {

// Lets get the userId from the request Object.
  		String userId = req.getParameter("user_id") ;
		if (userId == null)
			userId = req.getParameter("CURR_USER_ID") ;
     	if (userId == null) {
     		HttpSession session = req.getSession(false) ;
  			if(session != null  ) {
 				// session.putValue("AdminUser.user_id", userId ) ;
  	   		userId = (String) session.getValue("AdminUser.user_id") ;
    		}
      }

      if (userId == null || userId.startsWith("#")) {
      	String header = "AdminUserPhones error. " ;
  	  		AdminError2 err = new AdminError2(req,res,header, 51) ;
	 		return null;
		}
		return userId ;
			
  } // End getCurrentUserId  


} // End of class