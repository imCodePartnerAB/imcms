import java.io.* ;
import java.util.* ;

import javax.servlet.*;
import javax.servlet.http.*;

import imcode.external.diverse.* ;
import imcode.util.* ;
import imcode.server.* ;
import imcode.readrunner.* ;

import org.apache.log4j.* ;

public class AdminUserProps extends Administrator {
    private final static String CVS_REV = "$Revision$" ;
    private final static String CVS_DATE = "$Date$" ;
	
	private final static String HTML_RESPONSE = "AdminUserResp.htm" ;
	private final static String HTML_RESPONSE_ADMIN_PART = "AdminUserResp_admin_part.htm" ;
	private final static String HTML_RESPONSE_SUPERADMIN_PART = "AdminUserResp_superadmin_part.htm" ;


    private static Category log = Logger.getInstance( AdminUserProps.class.getName() ) ;

	/**
     * GET
     **/
	
	public void doGet(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException
    {

	IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface(req) ;
		
	// Lets validate the session
	if (super.checkSession(req,res) == false)	return ;
	
	// Get the session
	HttpSession session = req.getSession(false);

	// Lets get an user object
	imcode.server.User user = super.getUserObj(req,res) ;
	
	if(user == null)  {
		String header = "Error in AdminCounter." ;
		String msg = "Couldnt create an user object."+ "<BR>" ;
		this.log(header + msg) ;
		AdminError err = new AdminError(req,res,header,msg) ;
		return ;
	}
	
	// check if user is a Useradmin, adminRole = 2
	boolean isUseradmin = imcref.checkUserAdminrole ( user.getUserId(), 2 );

	// check if user is a Superadmin, adminRole = 1
	boolean isSuperadmin = imcref.checkUserAdminrole ( user.getUserId(), 1 );
	
	
	//Lets get the prefered lang prefix
	String lang_prefix = user.getLangPrefix();
	
	//Lets get temporary values from session if there is some.
	//String[] tmp_userRoles = (String[])session.getAttribute("tempUserRoles");
	//String tmp_userType = (String)session.getAttribute("tempUserType");
	Properties tmp_userInfo = (Properties)session.getAttribute("tempUser");
	
	Vector tmp_phones  = (Vector)session.getAttribute("Ok_phoneNumbers");
	if ( tmp_phones == null){
		tmp_phones = new Vector();
	}
	
	//Vector theUserRolesV = new Vector();
	//if ( tmp_userRoles != null ) {
	//	theUserRolesV = new Vector(java.util.Arrays.asList(tmp_userRoles)) ;
	//}
	
	String login_name = "";
	String password1 = "" ; 
	String password2 = "" ; 
	String new_pwd1 = "" ;   //hidden fildes
	String new_pwd2 = "" ;   //hidden fildes
	String first_name = "" ; 
	String last_name = "" ; 
	String title = "" ; 
	String company = "" ; 
	String address = "" ; 
	String city = "" ; 
	String zip = "" ;
	String country = "" ; 
	String country_council = "" ; 
	String email = "" ;
	String language = "1" ; 
//	String userType= "1" ; 
//	String active = "1" ; 	
//	String userCreateDate = " " ; 	


	// ******* GENERATE AN ADD_USER PAGE **********
	
	if( req.getParameter("ADD_USER") != null ){
	
		// Lets check if the user is an admin, otherwise throw him out.
		//if (imcref.checkAdminRights(user) == false) {
		  if (!isSuperadmin && !isUseradmin ){
			String header = "Error in AdminCounter." ;
			String msg = "The user is not an administrator."+ "<BR>";
			this.log(header + msg) ;
			AdminError err = new AdminError(req,res,header,msg) ;
			return ;
		}
		
		//Lets set values from session if we have any
		if ( tmp_userInfo != null){  
			login_name = tmp_userInfo.getProperty("login_name");
			password1 = tmp_userInfo.getProperty("password1");
			password2 = tmp_userInfo.getProperty("password2");
			new_pwd1 = tmp_userInfo.getProperty("new_pwd1"); //hidden fildes
			new_pwd2 = tmp_userInfo.getProperty("new_pwd2"); //hidden fildes
			first_name = tmp_userInfo.getProperty("first_name");
			last_name = tmp_userInfo.getProperty("last_name");
			title = tmp_userInfo.getProperty("title");
			company = tmp_userInfo.getProperty("company");

			address = tmp_userInfo.getProperty("address");
			city = tmp_userInfo.getProperty("city");
			zip = tmp_userInfo.getProperty("zip");
			country = tmp_userInfo.getProperty("country");
			country_council = tmp_userInfo.getProperty("country_council");
			email = tmp_userInfo.getProperty("email");
			language = tmp_userInfo.getProperty("lang_id");
//			userType= tmp_userType;
//			active = tmp_userInfo.getProperty("active");	
//			userCreateDate = tmp_userInfo.getProperty("createDate") ;	
		}
		
		Vector vec = new Vector() ;		// hold tags and values to parse html page
		Writer out = res.getWriter();	// to write out html page
				
	//	VariableManager vm = new VariableManager() ;
		Html htm = new Html() ;
		
		
		vec.add("#LOGIN_NAME#") ; 	vec.add(login_name) ;
		vec.add("#PWD1#") ; 		vec.add(password1) ;
		vec.add("#PWD2#") ; 		vec.add( password2) ;
		
		vec.add("#NEW_PWD1#") ; 	vec.add(new_pwd1); //hidden fildes
		vec.add("#NEW_PWD2#") ; 	vec.add(new_pwd2); //hidden fildes
		
		vec.add("#FIRST_NAME#") ; 	vec.add(first_name) ;
		vec.add("#LAST_NAME#") ; 	vec.add(last_name) ;
		vec.add("#TITLE#") ; 		vec.add(title) ;
		vec.add("#COMPANY#") ; 		vec.add(company) ;

		vec.add("#ADDRESS#") ; 		vec.add(address) ;
		vec.add("#CITY#") ; 		vec.add(city) ;
		vec.add("#ZIP#") ; 			vec.add(zip) ;
		vec.add("#COUNTRY#") ; 		vec.add(country) ;
		vec.add("#COUNTRY_COUNCIL#") ; vec.add(country_council) ;
		vec.add("#EMAIL#") ; 		vec.add(email) ;
		

		vec.add("#ADMIN_TASK#") ; 	vec.add("ADD_USER") ;

		vec.add("#PHONE_ID#") ; 	vec.add("");
		vec.add("#COUNTRY_CODE#") ; vec.add("");
		vec.add("#AREA_CODE#") ; 	vec.add("");
		vec.add("#NUMBER#") ; 		vec.add("");


		// phoneslist
		String phones = htm.createHtmlCode("ID_OPTION", "" , tmp_phones ) ;
		vec.add("#PHONES_MENU#") ; 	vec.add(phones) ;
		
		
		// Lets add html for admin_part in AdminUserResp
    	vec.add("#ADMIN_PART#") ; 	vec.add( createAdminPartHtml(user, null, imcref, req, res, session ) );
    	
 
		// language id: lets set swedish as default
		String[] langList = imcref.sqlProcedure("GetLanguageList 'se'") ;
		Vector selectedLangV = new Vector() ;
		selectedLangV.add("1") ;
		vec.add("#LANG_TYPES#") ; 	vec.add(htm.createHtmlCode("ID_OPTION",selectedLangV, new Vector(java.util.Arrays.asList(langList)))) ;

		//store all data into the session
		session.setAttribute("Ok_phoneNumbers", tmp_phones);
		//session.setAttribute("RESET_usersArr", usersArr);
		//session.setAttribute("RESET_userCreateDate", " ");
		
		//session.setAttribute("RESET_langList", langList);
		//session.setAttribute("RESET_selectedLangV", selectedLangV);

		// Lets create the HTML page
		
		String outputString = imcref.parseDoc(vec, HTML_RESPONSE, user.getLangPrefix()) ;
    	out.write(outputString) ;
		
	//	this.sendHtml(req, res, vm, HTML_RESPONSE) ;
		return ;
	}
		
		
		
    // ******* GENERATE AN CHANGE_USER PAGE**********
    if( req.getParameter("CHANGE_USER") != null ){
	    	
    	log("Changeuser") ;
    	
   	 	// lets first try to get userId from the session if we has been redirectet from verifyUser 
    	String userToChangeId = getCurrentUserId(req,res);
		
    	// Lets check if the user has right to do changes  
    	// only if he is an superadmin, useradmin or if he try to change his own values 
    	// otherwise throw him out.
    	if (imcref.checkAdminRights(user) == false && !isUseradmin && !userToChangeId.equals(""+ user.getUserId() ) ){
    		String header = "Error in AdminCounter." ;
    		String msg = "The user has no rights to change user values."+ "<BR>";
    		this.log(header + msg) ;
    		AdminError err = new AdminError(req,res,header,msg) ;
    		return ;
    	}
		
			
		// get a user object by userToChangeId
		imcode.server.User userToChange = null;
		if( userToChangeId != ""){
			userToChange = imcref.getUserById(Integer.parseInt(userToChangeId));
		}	
    	
		
		//Lets set values from session if we have any
		if ( tmp_userInfo != null){  
			login_name = tmp_userInfo.getProperty("login_name");
			password1 = tmp_userInfo.getProperty("password1");
			password2 = tmp_userInfo.getProperty("password2");
			new_pwd1 = tmp_userInfo.getProperty("new_pwd1"); //hidden fildes
			new_pwd2 = tmp_userInfo.getProperty("new_pwd2"); //hidden fildes
			first_name = tmp_userInfo.getProperty("first_name");
			last_name = tmp_userInfo.getProperty("last_name");
			title = tmp_userInfo.getProperty("title");
			company = tmp_userInfo.getProperty("company");

			address = tmp_userInfo.getProperty("address");
			city = tmp_userInfo.getProperty("city");
			zip = tmp_userInfo.getProperty("zip");
			country = tmp_userInfo.getProperty("country");
			country_council = tmp_userInfo.getProperty("country_council");
			email = tmp_userInfo.getProperty("email");
			language = tmp_userInfo.getProperty("lang_id");
//			userType= tmp_userType;
//			active = tmp_userInfo.getProperty("active");	
//			userCreateDate = tmp_userInfo.getProperty("createDate") ;	
			
		}else{
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
			country_council = userToChange.getCountryCouncil();
			email = userToChange.getEmailAddress();
//			userType= tmp_userType;
			//active = tmp_userInfo.getProperty("active");	
			//userCreateDate = tmp_userInfo.getProperty("createDate") ;
		}
		
				
		// Lets get all users phone numbers from session if we have any
		// return value from db= phone_id, country_code, area_code, number, user_id
		Html htm = new Html() ;
    	String[][] phonesArr= imcref.sqlProcedureMulti("GetUserPhoneNumbers " + userToChange.getUserId()) ;
    	
		// Get a new Vector:  phone_id, countryCode,  areaCode,  number, user_id  ex. 10, 46, 498, 123456, 3
		Vector phoneNumbers = getPhonesArrayVector (phonesArr); 
    	
		if ( tmp_phones.size() > 0 ) {
			 phoneNumbers = tmp_phones;
		}
		
		// Get a new Vector: phone_id, countryCode areaCode number  ex. 10, 46 498 123456)
		Vector phonesV  = this.getPhonesVector(phoneNumbers);

    	String selected = "";
    	if (phonesV.size() > 0){
    		selected = (String)phonesV.get(0);
    	}
		
		//System.out.println("selected= " + selected);		

    	String phones = htm.createHtmlCode("ID_OPTION", selected , phonesV ) ;
		
    	
    	res.setContentType("text/html");

    	Writer out = res.getWriter();

    	
    	Vector vec = new Vector() ;
    
    	vec.add("#CURR_USER_ID#"); 	vec.add( userToChangeId );
    	vec.add("#LOGIN_NAME#"); 	vec.add(login_name);
		    
    
    	// Lets fix the password string to show just ****
    	vec.add("#PWD1#"); 			vec.add(doPasswordString(password1));
    	vec.add("#PWD2#"); 			vec.add(doPasswordString(password1));
		vec.add("#NEW_PWD1#"); 		vec.add(new_pwd1); 	//hidden fildes
		vec.add("#NEW_PWD2#"); 		vec.add(new_pwd2); 	//hidden fildes
    
    	vec.add("#FIRST_NAME#"); 	vec.add(first_name);
    	vec.add("#LAST_NAME#"); 	vec.add(last_name);
    	vec.add("#TITLE#"); 		vec.add(title);
    	vec.add("#COMPANY#"); 		vec.add(company);
    	vec.add("#ADDRESS#"); 		vec.add(address);
    	vec.add("#ZIP#"); 			vec.add(zip);
    	vec.add("#CITY#"); 			vec.add(city);	
    	vec.add("#COUNTRY_COUNCIL#"); 	vec.add(country_council);	
    	vec.add("#COUNTRY#"); 		vec.add(country);	
    	vec.add("#EMAIL#"); 		vec.add(email);	
    
    	vec.add("#BACK#"); 	
    	if ( null != (String)session.getAttribute("go_back") ){
    		vec.add((String)session.getAttribute("go_back"));
    	}else{
    		vec.add("");	
    	}
   	

    	//add the phone nr fields
    	vec.add("#PHONE_ID#"); 		vec.add("");
    	vec.add("#COUNTRY_CODE#"); 	vec.add("");
    	vec.add("#AREA_CODE#"); 	vec.add("");
    	vec.add("#NUMBER#"); 		vec.add("");
    	vec.add("#PHONES_MENU#"); 	vec.add(phones);	
    

    	// Lets add html for admin_part in AdminUserResp
    	vec.add("#ADMIN_PART#");
    	if ( isSuperadmin || ( isUseradmin && user.getUserId() != userToChange.getUserId() ) ){
    		vec.add( createAdminPartHtml(user, userToChange, imcref, req, res, session ) );
    	}else{
    		vec.add("");
    	}
    	
    	String adminTask = req.getParameter("adminTask");
    	if (adminTask == null) {
    		adminTask = "SAVE_CHANGED_USER";
    	}
    	vec.add("#ADMIN_TASK#"); 	vec.add(adminTask); 


    	// Lets get the the users language id
    	String[] langList = imcref.sqlProcedure("GetLanguageList 'se'") ; // FIXME: Get the correct language for the user
    	Vector selectedLangV = new Vector() ;
    	selectedLangV.add( ""+ userToChange.getLangId() ) ;
    	vec.add("LANG_TYPES");  vec.add( htm.createHtmlCode("ID_OPTION",selectedLangV, new Vector(java.util.Arrays.asList(langList) ) ) );


    	//store all data into the session
    	session.setAttribute("userToChange",userToChangeId);
       	session.setAttribute("Ok_phoneNumbers", phoneNumbers);
		//session.setAttribute("RESET_langList", langList);
    	//session.setAttribute("RESET_selectedLangV", selectedLangV);
		
		// Lets renove session we dont need anymore.
		try {
		    session.removeAttribute("tempUserRoles");
			session.removeAttribute("tempUseradminRoles");
			session.removeAttribute("tempUserType");
			session.removeAttribute("tempUser");
			//session.getAttribute("Ok_phoneNumbers");		

		}catch(IllegalStateException ise) {
		    log("session has been invalidated so no need to remove parameters");
		}
    
    	
    	String outputString = imcref.parseDoc(vec, HTML_RESPONSE, userToChange.getLangPrefix()) ;
    	out.write(outputString) ;	

    	return ;
    }
	
	
				
		
    } /** End GET **/
	
	
    /**
     * POST
     **/
    public void doPost(HttpServletRequest req, HttpServletResponse res)	throws ServletException, IOException {
	String host				= req.getHeader("Host") ;
	IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterfaceByHost(host) ;

	// Lets validate the session
	if (super.checkSession(req,res) == false) return ;
	
	HttpSession session = req.getSession(false);
	
	if(session == null) return;

	// Lets get an user object
	imcode.server.User user = super.getUserObj(req,res) ;
	if(user == null) {
	    String header = "Error in AdminCounter." ;
	    String msg = "Couldnt create an user object."+ "<BR>" ;
	    AdminError err = new AdminError(req,res,header,msg) ;
	    return ;
	}
	
	// check if user is a Superadmin, adminRole = 1
	boolean isSuperadmin = imcref.checkUserAdminrole ( user.getUserId(), 1 );
	
	// check if user is a Useradmin, adminRole = 2
	boolean isUseradmin = imcref.checkUserAdminrole ( user.getUserId(), 2 );

	boolean isAdmin = ( isSuperadmin || isUseradmin );
	
	
	// Lets check adminTask 
	String adminTask = req.getParameter("adminTask") ;
	if(adminTask == null)	adminTask = "" ;
	
	
	// Lets get the user which should be changed if we is not in ADD_USER mode
	String userToChangeId = "";
	if ( ! "ADD_USER".equals(adminTask) ) {
			userToChangeId = getCurrentUserId(req,res);
	} 
		
	// get a user object by userToChangeId
	imcode.server.User userToChange = null;
	if( userToChangeId != ""){
		userToChange = imcref.getUserById(Integer.parseInt(userToChangeId));
	}	
			
	Properties userInfoP = new Properties() ;
			    
	userInfoP = this.getParameters(req, imcref, user, userToChange);

	// Lets get all Userinformation and add it to html page
		
	VariableManager vm = new VariableManager() ;
	
	vm.addProperty("LOGIN_NAME", userInfoP.getProperty("login_name")) ;
	vm.addProperty("FIRST_NAME", userInfoP.getProperty("first_name")) ;
	vm.addProperty("LAST_NAME", userInfoP.getProperty("last_name")) ;
	vm.addProperty("TITLE", userInfoP.getProperty("title")) ;
	vm.addProperty("COMPANY", userInfoP.getProperty("company")) ;
	vm.addProperty("ADDRESS", userInfoP.getProperty("address")) ;
	vm.addProperty("CITY", userInfoP.getProperty("city")) ;
	vm.addProperty("ZIP", userInfoP.getProperty("zip")) ;
	vm.addProperty("COUNTRY", userInfoP.getProperty("country")) ;
	vm.addProperty("COUNTRY_COUNCIL", userInfoP.getProperty("country_council")) ;
	vm.addProperty("EMAIL", userInfoP.getProperty("email")) ;

		
	Html htm = new Html() ;
	

	//******* READRUNNER_SETTINGS BUTTON WAS PUNSCHED ***********	
	if (null != req.getParameter("READRUNNER_SETTINGS")) {
		
		String theUserType = req.getParameter("user_type");
		String[] theUserRoles = req.getParameterValues("roles");
		String[] theUseradminRoles = req.getParameterValues("useradmin_roles");
		
		if( null != theUserRoles ){
			session.setAttribute("tempUserRoles", theUserRoles);
		}
		if ( null != theUserType ){		
			session.setAttribute("tempUserType", theUserType);
		}
		if ( null != userInfoP ){
			session.setAttribute("tempUser", userInfoP );
		}
		if( null != theUseradminRoles ){
			session.setAttribute("tempUseradminRoles", theUseradminRoles);
		}
		
		res.sendRedirect("AdminUserReadrunner") ;
		return ;
	}
	
	//******** USERADMIN_STTINGS BUTTON WAS PUNSCHED ***********
	if ( null != req.getParameter("useradmin_settings") ) {
		
		String[] theUserRoles = req.getParameterValues("roles");
		String[] theUseradminRoles = req.getParameterValues("useradmin_roles");
		String theUserType = req.getParameter("user_type");
		
		if( null != theUserRoles ){
			session.setAttribute("tempUserRoles", theUserRoles);
		}
		if ( null != theUserType ){		
			session.setAttribute("tempUserType", theUserType);
		}
		if ( null != userInfoP ){
			session.setAttribute("tempUser", userInfoP );
		}
		if( null != theUseradminRoles ){
			session.setAttribute("tempUseradminRoles", theUseradminRoles);
		}
		
		res.sendRedirect("AdminUserUseradminSettings") ;
		return ;
	
    }

	//******* RESET_FORM BUTTON WAS PUNSCHED ***********
	//sets up the needed parameters and redirect back to AdminUserProps
	if ( req.getParameter("RESET_FORM") != null ) {
	
	    if(adminTask.equals("ADD_USER") ) {
			res.sendRedirect("AdminUserProps?ADD_USER=true&adminTask="+adminTask) ;
	    }else if (adminTask.equals("SAVE_CHANGED_USER") ) {
			res.sendRedirect("AdminUserProps?CHANGE_USER=true&adminTask="+adminTask) ;
	    }
		return;
	}

	
	
	//******** OK_PHONES or DELETE_PHONES or EDIT_PHONES  WAS PRESSED ***********
	if( req.getParameter("ok_phones") != null || req.getParameter("delete_phones") != null ||
	    req.getParameter("edit_phones") != null) {

		
		if( adminTask == null){
			 adminTask = "";
		}
		
		
		
		
		if (imcref.checkAdminRights(user) == false && !isUseradmin && !userToChangeId.equals(""+ user.getUserId() ) ){
			String header = "Error in AdminCounter." ;
			String msg = "The user has no rights to change user values."+ "<BR>";
			this.log(header + msg) ;
			AdminError err = new AdminError(req,res,header,msg) ;
			return ;
		}
		
		 
		
		// Lets all phonenumbers from the session
		// Get a new Vector:  phone_id, countryCode,  areaCode,  number, user_id  ex. 10, 46, 498, 123456, 3
		Vector  phoneNumbers = ( session.getAttribute("Ok_phoneNumbers") != null ) ? (Vector)session.getAttribute("Ok_phoneNumbers") : new Vector() ;
	
		log("test"+req.getParameter("edit_phones"));
		

		String selectedPhoneId = "";
		

				
		
		//******** OK_PHONES BUTTON WAS PRESSED **********
		if ( req.getParameter("ok_phones") != null ) { //adds or changes a phoneNr to the select list
		
			log("ok_phones in doPost");
			

		   	//lets get the phonenumber from the form and add it into Vectorn phonesV
		    if (! req.getParameter("area_code").equals("") &&
				!req.getParameter("local_code").equals("")) {
				
				boolean found = false;  // marker that we is going to edit a selected phone number 
				int tempId = 1;  // temporary phone id
				
				Enumeration enum = phoneNumbers.elements();
					
				while (enum.hasMoreElements()) {
			    	String[] temp = (String[]) enum.nextElement();
			    	if (temp[0].equals(req.getParameter("phone_id"))) {
						selectedPhoneId = temp[0];
						phoneNumbers.remove(temp);
						temp[1] = req.getParameter("country_code");
						temp[2] = req.getParameter("area_code");
						temp[3] = req.getParameter("local_code");
						phoneNumbers.addElement(temp);

						found =  true;
			    	}
			    	try {
						if (Integer.parseInt(temp[0]) >= tempId) {
				    		tempId = Integer.parseInt(temp[0]) + 1;
						}

			    	}catch(NumberFormatException ignored) {
					// ignored
			   	 	}

				}
				

				if (!found) {
			    	String[] temp = new String[5];
				    temp[0] = ""+tempId;
				    selectedPhoneId = temp[0];
				    temp[1] = req.getParameter("country_code");
				    temp[2] = req.getParameter("area_code");
				    temp[3] = req.getParameter("local_code");
				    temp[4] = userToChangeId;
				    phoneNumbers.addElement(temp);
				}

		    }

		} 
		// ********* end ok_phones *************************
		
		
		//********* EDIT_PHONES BUTTON WAS PRESSED ***********
		
		boolean found = false;
		
		if ( req.getParameter("edit_phones") != null ) {
	    	log("edit_phones");
			
			Enumeration enum = phoneNumbers.elements();

			while (enum.hasMoreElements() && !found) {
				String[] temp = (String[]) enum.nextElement();
				if (temp[0].equals(req.getParameter("user_phones"))) {
					vm.addProperty( "PHONE_ID", temp[0]);
					vm.addProperty( "COUNTRY_CODE", temp[1]);
					vm.addProperty( "AREA_CODE", temp[2]);
					vm.addProperty( "NUMBER", temp[3]);
					found = true;
				}
			}
		}
		
		if (!found) {
			vm.addProperty( "PHONE_ID", "");
			vm.addProperty( "COUNTRY_CODE", "");
			vm.addProperty( "AREA_CODE", "");
			vm.addProperty( "NUMBER", "");
		}

	    selectedPhoneId = req.getParameter("user_phones");
	    log("Number: "+selectedPhoneId);
	    
		//*********end edit_phones***********
		
		
		//****************DELETE_PHONES BUTTON WAS PRESSED ************
		
		if ( req.getParameter("delete_phones") != null ) {
	
		    log("lets delete_phones from templist");

		    Enumeration enum = phoneNumbers.elements();
		    found = false;
		    //		log("Size"+phoneNumbers.size());
		    while (enum.hasMoreElements() && !found) {
				String[] temp = (String[]) enum.nextElement();
				log(temp[0]+" == " +req.getParameter("user_phones"));
				if (temp[0].equals( req.getParameter("user_phones"))) {
				    phoneNumbers.remove(temp);
				    found =  true;
				}
		    }

		    
		    if (phoneNumbers.size() > 0) {
				String[] temp = (String[]) phoneNumbers.firstElement();
				selectedPhoneId = temp[0];
		    }
		}
	
		// ******** end delete_phones ***************
		
		
		
		String newPwd = userInfoP.getProperty("password1");
		boolean isChanged = false; 
		for ( int i=0; i < newPwd.length(); i++) {
	    	if( newPwd.charAt(i) != ("*").charAt(0) ) {
				isChanged = true;
	    	}
		}
		if ( isChanged ){
			//update hidden fields whith new password
			vm.addProperty("NEW_PWD1", userInfoP.getProperty("password1"));
			vm.addProperty("NEW_PWD2", userInfoP.getProperty("password2"));
		}else{
			vm.addProperty("NEW_PWD1", "");
			vm.addProperty("NEW_PWD2", "");
		}
		
		//update password fields with just ****
		vm.addProperty("PWD1", doPasswordString( userInfoP.getProperty("password1") ) );
		vm.addProperty("PWD2", doPasswordString( userInfoP.getProperty("password2") ) );
		
		// Lets add html for admin_part in AdminUserResp
		if ( isSuperadmin || ( isUseradmin && !userToChangeId.equals(""+ user.getUserId()) ) ){
		
		/*	
			vm.addProperty("ACTIVE",  "1" );	
			
			if ( "1".equals(userInfoP.getProperty("active") ) ){
				 	vm.addProperty("ACTIVE_FLAG", "checked" );
			}else{ 
				vm.addProperty("ACTIVE_FLAG", "" );
			}
		*/			
			vm.addProperty("ADMIN_PART", createAdminPartHtml(user, userToChange, imcref, req, res, session) );
	
		}else{
			vm.addProperty("ADMIN_PART", "");
		}
		

		
//		String selectedId = req.getParameter("selected_id") == null ? "" : req.getParameter("selected_id");
//		log("selected_id= "+ selectedPhoneId);

		// Get a new Vector: phone_id, countryCode areaCode number  ex. 10, 46 498 123456)
		Vector phonesV  = this.getPhonesVector(phoneNumbers);
	
		if (phonesV == null) {
			this.sendErrorMsg(req,res, "Add/edit user", "An eror occured!");
			return;
		}	

		String phones = htm.createHtmlCode("ID_OPTION", selectedPhoneId, phonesV ) ;
		log("phones stringen: "+phones);
		vm.addProperty("PHONES_MENU", phones  ) ;

/*
		// Lets get the the users language id
		String[] langList = (String[])session.getAttribute("RESET_langList");
		Vector selectedLangV = (Vector)session.getAttribute("RESET_selectedLangV");
		selectedLangV.add(userV.get(16).toString()) ;
		vm.addProperty("LANG_TYPES", htm.createHtmlCode("ID_OPTION",selectedLangV, new Vector(java.util.Arrays.asList(langList)))) ;
*/

		vm.addProperty("ADMIN_TASK", adminTask) ;
		vm.addProperty("CURR_USER_ID", userToChangeId) ;
		
		session.setAttribute("Ok_phoneNumbers", phoneNumbers);

		this.sendHtml(req, res, vm, HTML_RESPONSE) ;
		return;
	
	} 
	// end of ******** OK_PHONES or DELETE_PHONES or EDIT_PHONES  WAS PRESSED ***********
	
	


	// ******* SAVE NEW USER TO DB **********
	if( req.getParameter("SAVE_USER") != null && adminTask.equalsIgnoreCase("ADD_USER") ) {
	    log("Lets add a new user to db") ;

	    //get session
	    if(session == null) return;

		// Lets check if the user is an admin, otherwise throw him out.
		if (!isAdmin ){
		    String header = "Error in AdminUserProps." ;
		    String msg = "The user has no admin rights."+ "<BR>" ;
		    AdminError err = new AdminError(req,res,header,msg) ;
		    return ;
		}
		
		// Lets get the parameters from html page and validate them
	    Properties params = this.getParameters(req, imcref, user, null ) ;
	    params = this.validateParameters(params,req,res) ;
	    if(params == null) return ;	
		
		// if user has add a phone number we have to get the password from NEW_PWD1 parameter
		if ( ! ( ("").equals(req.getParameter("new_pwd1")))	){
			params.setProperty("password1", req.getParameter("new_pwd1")) ;
			params.setProperty("password2", req.getParameter("new_pwd2")) ;
		}
		

	    // Lets get the roles from htmlpage
	    Vector rolesV = this.getRolesParameters("roles", req, res) ;
	    if( rolesV == null) return ;

	    // Lets validate the password
	    if( UserHandler.verifyPassword(params,req,res) == false)	return ;



	    // Lets check that the new username doesnt exists already in db
	    String userName = params.getProperty("login_name") ;
	    String userNameExists[] = imcref.sqlProcedure("FindUserName '" + userName + "'") ;
	    if(userNameExists != null ) {
			if(userNameExists.length > 0 ) {
			    String header = "Error in AdminUserProps." ;
			    String msg = "The username already exists, please change the username."+ "<BR>" ;
			    this.log(header + msg) ;
			    AdminError err = new AdminError(req,res,header,msg) ;
			    return ;
			}
	    }

	    // Lets get the highest userId
	    String newUserId = getNewUserID(req,res) ;
	    if( newUserId == null) return ;

	    //Lets get phonenumbers from the session
	    Vector phonesV  = (Vector)session.getAttribute("Ok_phoneNumbers");
	    if (phonesV == null) {
			this.sendErrorMsg(req, res, "AdminUserProps:  Add new user", "An eror occured!");
			return;
	    }

	    // Lets build the users information into a string and add it to db
	    params.setProperty("user_id", newUserId) ;
	    String userStr = UserHandler.createUserInfoString(params) ;
	    log("AddNewUser " + userStr) ;
	    imcref.sqlUpdateProcedure("AddNewUser " + userStr) ;
		
		// Lets add Readrunner user data
		if ( null != session.getAttribute("tempRRUserData") ){
			ReadrunnerUserData rrUserData = new ReadrunnerUserData() ;
			rrUserData = (ReadrunnerUserData)session.getAttribute("tempRRUserData");
			User newUser = imcref.getUserById(Integer.parseInt(newUserId) ) ;
			imcref.setReadrunnerUserData(newUser, rrUserData) ;
		}

	    // Lets add the new users roles
	    for(int i = 0; i<rolesV.size(); i++) {
			String aRole = rolesV.elementAt(i).toString() ;
			imcref.sqlUpdateProcedure("AddUserRole " + newUserId + ", " + aRole) ;
	    }
		// always let user get the role Users
		String[] roleId = imcref.sqlProcedure ("GetRoleIdByRoleName Users");
		if ( roleId != null ){  
			imcref.sqlUpdateProcedure("AddUserRole " + newUserId + ", " + Integer.parseInt(roleId[0])) ;
		}
		
		// Lets get the useradmin_roles from htmlpage
	    Vector useradminRolesV = this.getRolesParameters("useradmin_roles", req, res) ;
	    
		// Lets add the new useradmin roles.
		for(int i = 0; i<useradminRolesV.size(); i++) {
			String aRole = useradminRolesV.elementAt(i).toString();
			imcref.sqlUpdateProcedure("AddUseradminPermissibleRoles  " + newUserId + ", " + aRole) ;
		}
	
		

	    //spara telefonnummer från listan
	    for(int i = 0; i<phonesV.size(); i++) {
			String[] aPhone = (String[])phonesV.elementAt(i);
			String sqlStr = "phoneNbrAdd " + newUserId + ", '" ;//userId
			sqlStr += aPhone[1] + "', '" + aPhone[2] + "', '" + aPhone[3] + "'" ;//country_code,area_code,number

			log("PhoneNrAdd: " + sqlStr);

			imcref.sqlUpdateProcedure(sqlStr) ;
	    }

	    this.goAdminUsers(req, res, session) ;
	    return ;
	}

	// ******** SAVE EXISTING USER TO DB ***************
	if( req.getParameter("SAVE_USER") != null && adminTask.equalsIgnoreCase("SAVE_CHANGED_USER")) {
	    log("******** SAVE EXISTING USER TO DB ***************");
//  HttpSession session = req.getSession(false);
	    if(session == null) return;
		
	    // Lets check that we have a user to be changed.
	    if (userToChange == null)	return ;
		
		
		// Lets check if the user is an admin or if he is going to change his own data, otherwise throw him out.
		if (!isAdmin && user.getUserId() != Integer.parseInt(userToChangeId) ) {
		    String header = "Error in AdminCounter." ;
		    String msg = "The user has no rights to administrate."+ "<BR>" ;
		    AdminError err = new AdminError(req,res,header,msg) ;
		    return ;
		}

	    // Lets get the parameters from html page and validate them
		
	    Properties params = this.getParameters(req, imcref, user, userToChange) ;
	    params.setProperty("user_id", userToChangeId) ;
		
		
		// Lets check if loginname is going to be changed and if so, 
		// lets check that the new loginname doesnt exists already in db
		String currentLogin = userToChange.getLoginName();
	    String newLogin = params.getProperty("login_name") ;
		if ( !newLogin.equalsIgnoreCase(currentLogin) ){
		    String userNameExists[] = imcref.sqlProcedure("FindUserName '" + newLogin + "'") ;
		    if(userNameExists != null ) {
				if(userNameExists.length > 0 ) {
				    String header = "Error in AdminUserProps." ;
				    String msg = "The username already exists, please change the username."+ "<BR>" ;
				    this.log(header + msg) ;
				    AdminError err = new AdminError(req,res,header,msg) ;
				    return ;
				}
		    }
		}

	    // Lets check the password. if its empty, then it wont be updated. get the
	    // old password from db and use that one instad
	    String currPwd = imcref.sqlProcedureStr("GetUserPassword " + userToChangeId ) ;
	    if( currPwd.equals("-1") ) {
			String header = "Fel! Ett lösenord kund inte hittas" ;
			String msg = "Lösenord kunde inte hittas"+ "<BR>" ;
			this.log(header + msg) ;
			AdminError err = new AdminError(req,res,header,msg) ;
			log("innan return i currPwd.equals");
			return ;
	    }
		
		// if user has add a phone number we have to get the password from NEW_PWD1 parameter
		if ( ! ( ("").equals(req.getParameter("new_pwd1")))	){
			params.setProperty("password1", req.getParameter("new_pwd1")) ;
			params.setProperty("password2", req.getParameter("new_pwd2")) ;
		}else{
			params.setProperty("password1", currPwd) ;
			params.setProperty("password2", currPwd) ;
		}


	    // Lets check if the password contains something. If it doesnt
	    // contain anything, then assume that the old one wont be updated
	    if( UserHandler.verifyPassword(params,req,res) == false)	return ;

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
		params = this.validateParameters(params,req,res) ;
	    if(params == null) return ;
		
	    // Lets get phonnumbers from the session
	    Vector phonesV  = (Vector)session.getAttribute("Ok_phoneNumbers");
	    if (phonesV == null) {
			this.sendErrorMsg(req, res, "AdminUserProps:  Edit user", "An eror occured!");
			return;
	    }
		
	    //radera telefonnummer
	    imcref.sqlUpdateProcedure("DelPhoneNr " + userToChangeId ) ;

	    // spara från listan, till databasen
	    for(int i = 0; i<phonesV.size(); i++) {
			String[] aPhone = (String[])phonesV.elementAt(i);
			String sqlStr = "phoneNbrAdd " + aPhone[4] + ", '" ;
			sqlStr += aPhone[1] + "', '" + aPhone[2] + "', '" + aPhone[3] + "'" ;//country_code,area_code,number

			imcref.sqlUpdateProcedure(sqlStr) ;
	    }

	    // Lets build the users information into a string and add it to db
	    String userStr = "UpdateUser " + UserHandler.createUserInfoString(params) ;
	    log("userSQL: " + userStr) ;
	    imcref.sqlUpdateProcedure(userStr) ;

		// if user isSuperadmin or
		// isUseradmin and not is going to change his own data 
		// then we have to take care of userroles and ReadRunner data
		if ( isSuperadmin || isUseradmin && user.getUserId() != userToChange.getUserId()){
	    	
			// Lets get the roles from htmlpage
	    	Vector rolesV = this.getRolesParameters("roles", req, res) ;
	    	if( rolesV == null) return ;
		
		
		    // Lets add the new users roles. but first, delete users current Roles
		    // and then add the new ones
			
			if ( isSuperadmin ){ // delete all userroles
		    	int roleId = -1;
				imcref.sqlUpdateProcedure("DelUserRoles " + userToChangeId + ", " + roleId ) ;
		    	
			}else{  // delete only roles that the useradmin has permission to administrate
				String[] rolesArr = imcref.sqlProcedure("GetUseradminPermissibleRoles " + user.getUserId() );
				for ( int i=0; i < rolesArr.length; i+=2 ){
					imcref.sqlUpdateProcedure("DelUserRoles " + userToChangeId + ", " + Integer.parseInt(rolesArr[i]) ) ;
				}  	
			}
			
			for(int i = 0; i<rolesV.size(); i++) {
				String aRole = rolesV.elementAt(i).toString();
				imcref.sqlUpdateProcedure("AddUserRole  " + userToChangeId + ", " + aRole) ;
		    }
			
			
			// always let user get the role Users
			String[] roleId = imcref.sqlProcedure ("GetRoleIdByRoleName Users");
			if ( roleId != null ){  
				imcref.sqlUpdateProcedure("AddUserRole " + userToChangeId + ", " + Integer.parseInt(roleId[0])) ;
			}
			
			// Lets get the useradmin_roles from htmlpage
	    	Vector useradminRolesV = this.getRolesParameters("useradmin_roles", req, res) ;
	    	if( useradminRolesV == null) return ;
		
		    // Lets add the new useradmin roles. but first, delete the current roles
		    // and then add the new ones
		    imcref.sqlUpdateProcedure("DeleteUseradminPermissibleRoles " + userToChangeId ) ;
		    for(int i = 0; i<useradminRolesV.size(); i++) {
				String aRole = useradminRolesV.elementAt(i).toString();
				imcref.sqlUpdateProcedure("AddUseradminPermissibleRoles  " + userToChangeId + ", " + aRole) ;
		    }
			
			// Lets add Readrunner user data
			if ( null != session.getAttribute("tempRRUserData") ){
				ReadrunnerUserData rrUserData = (ReadrunnerUserData)session.getAttribute("tempRRUserData");
				imcref.setReadrunnerUserData(userToChange, rrUserData) ;
			}
		}

	    this.goAdminUsers(req, res, session) ;
	    return ;
	}
	

	// ******** GO_BACK TO THE MENY ***************
	if( req.getParameter("GO_BACK") != null ) {

		String url = "AdminUser" ;
	
		if ( null != session.getAttribute("go_back") ){
	    	url =  (String)session.getAttribute("go_back");
		}
		this.removeSessionParams(req);
	    res.sendRedirect(url) ;
	    return ;
	}

	// ******** UNIDENTIFIED ARGUMENT TO SERVER ********
	this.log("Unidentified argument was sent!") ;
	doGet(req,res) ;
	return ;
	
    } // end HTTP POST

    
	
	/**
       Removes temporary parameters from the session
    */
    public void removeSessionParams(HttpServletRequest req)	throws ServletException, IOException {
	HttpSession session = req.getSession(false);
	if (session == null) return;
	try {
	    
	    
	    //session.removeAttribute("country_code");
	    //session.removeAttribute("area_code");
	    //session.removeAttribute("local_code");
	    //session.removeAttribute ("RESET_usersArr");
	   // session.removeAttribute ("RESET_userCreateDate");
	    session.removeAttribute ("Ok_phoneNumbers");
	   // session.removeAttribute ("RESET_langList");
	   // session.removeAttribute ("RESET_selectedLangV");
		session.removeAttribute("userToChange");
		session.removeAttribute("tempRRUserData");
		session.removeAttribute("go_back");

	}catch(IllegalStateException ise) {
	    log("session has been invalidated so no need to remove parameters");
	}
    }

    /**
       a error page will be generated, fore those times the user uses the backstep in
       the browser
    */
    private void sendErrorMsg(HttpServletRequest req, HttpServletResponse res, String header, String msg) throws ServletException, IOException {

	AdminError err = new AdminError(req,res, header, msg) ;
    }

    /**
       Returns a String, containing the newUserID. if something failes, a error page
       will be generated and null will be returned.
    */

    public String getNewUserID(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
	String host				= req.getHeader("Host") ;
	IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterfaceByHost(host) ;

	String newUserId = imcref.sqlProcedureStr("GetHighestUserId" ) ;

	if ( newUserId.equals("") ) {
	    String aHeader = "AddNewUser" ;
	    String msg = "SP: GetHighestUserId misslyckades" ;
	    AdminError err = new AdminError(req,res,aHeader, msg) ;
	    return null;
	}
	return newUserId ;

    } // End GetNewUserID


    /**
       Returns a Vector, containing the choosed roles from the html page. if Something
       failes, a error page will be generated and null will be returned.
    */

    public Vector getRolesParameters(String name, HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {

	// Lets get the roles
	// Vector rolesV = this.getRolesParameters(req) ;
	String[] roles = (req.getParameterValues(name)==null) ? new String[0] : (req.getParameterValues(name));
	Vector rolesV = new Vector(java.util.Arrays.asList(roles)) ;
	if(rolesV.size() == 0 && name.equals("roles")) { // user must get at least one user role 
	    String header = "Roles error" ;
	    String msg = "Ingen roll var vald." + "<BR>";
	    this.log("Error in checking roles") ;
	    AdminError err = new AdminError(req,res,header, msg) ;
	    return null;
	}
	//this.log("Roles:"+ rolesV.toString()) ;
	return rolesV ;

    } // End getRolesParameters

	
	/**
       Returns a Vector, containing the choosed phone numbers from the html page. 
    */

    public Vector getPhonesParameters(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {

		// Lets get the phones
		String[] phones = (req.getParameterValues("user_phones")==null) ? new String[0] : (req.getParameterValues("user_phones"));
		Vector phonesV = new Vector(java.util.Arrays.asList(phones)) ;
		return phonesV ;

    } // End getPhonesParameters

    
    /**
       Returns to the adminUsers meny
    */

    public void goAdminUsers(HttpServletRequest req, HttpServletResponse res, HttpSession session)
	throws ServletException, IOException {
	
		String url = "AdminUser" ;
	
		if ( null != session.getAttribute("go_back") ){
	    	url =  (String)session.getAttribute("go_back");
		}
		this.removeSessionParams(req);
	    res.sendRedirect(url) ;
    }

 


    

    /**
       Collects the parameters from the request object
    **/

    public Properties getParameters( HttpServletRequest req, IMCServiceInterface imcref, User user,  User userToChange) throws ServletException, IOException {

	Properties userInfo = new Properties() ;
	// Lets get the parameters we know we are supposed to get from the request object
	String login_name = (req.getParameter("login_name")==null) ? "" : (req.getParameter("login_name")) ;
	String password1 = (req.getParameter("password1")==null) ? "" : (req.getParameter("password1")) ;
	String password2 = (req.getParameter("password2")==null) ? "" : (req.getParameter("password2")) ;

	String first_name = (req.getParameter("first_name")==null) ? "" : (req.getParameter("first_name")) ;
	String last_name = (req.getParameter("last_name")==null) ? "" : (req.getParameter("last_name")) ;
	String title = (req.getParameter("title")==null) ? "" : (req.getParameter("title")) ;
	String company = (req.getParameter("company")==null) ? "" : (req.getParameter("company")) ;

	String address = (req.getParameter("address")==null) ? "" : (req.getParameter("address")) ;
	String city = (req.getParameter("city")==null) ? "" : (req.getParameter("city")) ;
	String zip = (req.getParameter("zip")==null) ? "" : (req.getParameter("zip")) ;
	String country = (req.getParameter("country")==null) ? "" : (req.getParameter("country")) ;
	String country_council = (req.getParameter("country_council")==null) ? "" : (req.getParameter("country_council")) ;
	String email = (req.getParameter("email")==null) ? "" : (req.getParameter("email"));
	String language = (req.getParameter("lang_id")==null) ? "1" : (req.getParameter("lang_id")) ;
	String user_type=(req.getParameter("user_type")==null) ? "" : (req.getParameter("user_type")) ;
	String active = (req.getParameter("active")==null) ? "0" : (req.getParameter("active")) ;

	
	//boolean isAdmin = checkAdminRights(user, imcref);
	
	// check if user is a Superadmin, adminRole = 1
	boolean isSuperadmin = imcref.checkUserAdminrole ( user.getUserId(), 1 );
	
	// check if user is a Useradmin, adminRole = 2
	boolean isUseradmin = imcref.checkUserAdminrole ( user.getUserId(), 2 );
	
	// if we are going to change a user
	if ( userToChange != null ){
	
		// and if user is not a Superadmin and is going to change his own userdata 
		// then lets get current values for the user
		if ( !isSuperadmin && userToChange.getUserId() == user.getUserId()  ){ 
			user_type = ""+ userToChange.getUserType();
			active = userToChange.isActive() ?  "1" : "0" ;	
		}
	}
	

	// Lets fix those fiels which arent mandatory
	if (req.getParameter("SAVE_USER") != null) {
	    if( title.trim().equals("")) title = "--" ;
	    if( company.trim().equals("")) company = "--" ;
	    if( address.trim().equals("")) address = "--" ;
	    if( city.trim().equals("")) city = "--" ;
	    if( zip.trim().equals("")) zip = "--" ;
	    if( country.trim().equals("")) country = "--" ;
	    if( country_council.trim().equals("")) country_council = "--" ;
	    if( email.trim().equals("")) email = "--" ;
	}
	
	userInfo.setProperty("login_name", login_name) ;
	userInfo.setProperty("password1", password1) ;
	userInfo.setProperty("password2", password2) ;
	userInfo.setProperty("first_name", first_name) ;
	userInfo.setProperty("last_name", last_name) ;
	userInfo.setProperty("title", title) ;
	userInfo.setProperty("company", company) ;

	userInfo.setProperty("address", address) ;
	userInfo.setProperty("city", city) ;
	userInfo.setProperty("zip", zip) ;
	userInfo.setProperty("country", country) ;
	userInfo.setProperty("country_council", country_council) ;
	userInfo.setProperty("email", email) ;
	userInfo.setProperty("lang_id", language) ;
	userInfo.setProperty("user_type", user_type) ;
	userInfo.setProperty("active", active) ;
	

	return userInfo ;
    }

    /**
       Returns a Properties, containing the user information from the html page. if Something
       failes, a error page will be generated and null will be returned.
    */

    public Properties validateParameters(Properties aPropObj, HttpServletRequest req,
					 HttpServletResponse res) throws ServletException, IOException {

		//	Properties params = this.getParameters(req) ;
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
	   adds the phoneNrId to the vector followed by the phoneNumber 
	   return a new Vector with elements formated like
		( phone_id, countryCode,  areaCode,  number, user_id  ex. 10, 46, 498, 123456, 3)
	*/
    public Vector getPhonesArrayVector(String[][] phoneNr) {
		Vector phonesArrV = new Vector();

		for(int i=0; i < phoneNr.length; i++) {
			phonesArrV.addElement(phoneNr[i]);
	    }
		return phonesArrV;
    }

 	
	/** return a new Vector with elements formated like
		( phone_id, countryCode areaCode number  ex. 10, 46 498 123456)
	*/
    public Vector getPhonesVector(Vector phonesArrV) {

		Vector phonesV = new Vector();
		Enumeration enum = phonesArrV.elements();

		while (enum.hasMoreElements()) {
			String[] tempPhone = (String[])enum.nextElement();
			String temp = "";
			for(int i=0; i < tempPhone.length-1; i++) {
				if (i== 0) {
					phonesV.addElement(tempPhone[i]);
			    }else{
				    temp += tempPhone[i] +" ";
				}
		    }
			phonesV.addElement(temp);							 
	    }
		return phonesV;
    }
	
	
	
	public String[] getPhoneNumberParts(String phoneNumber){
		String countryCode = "";
		String areaCode = "";
		String number = "";
				
		number = phoneNumber.substring( phoneNumber.lastIndexOf(" ") );
		phoneNumber = phoneNumber.substring(0, phoneNumber.lastIndexOf(" "));
		if ( phoneNumber.length() > 0 ){
			areaCode = phoneNumber.substring( phoneNumber.lastIndexOf(" ") );
			phoneNumber = phoneNumber.substring(0, phoneNumber.lastIndexOf(" "));
		}
		if ( phoneNumber.length() > 0 ){
			countryCode = phoneNumber;
		}
		return new String[] { countryCode , areaCode , number };
	}
		

	boolean checkAdminRights(User user, IMCServiceInterface imcref ){
		
		// check if user is a Superadmin, adminRole = 1
		boolean isSuperadmin = imcref.checkUserAdminrole ( user.getUserId(), 1 );
		
		// check if user is a Useradmin, adminRole = 2
		boolean isUseradmin = imcref.checkUserAdminrole ( user.getUserId(), 2 );
		
		if ( isSuperadmin || isUseradmin ) {
			return true;
		}
			
	/*	else if ( user.getUserId() == userToChange.getUserId() ){
		 	return true;
					
		}
	*/
		else{ return false;	}
	}
	
	
	
	/**
       Adds the userInformation to the htmlPage. if an empty vector is sent as argument
       then an empty one will be created
    **/
    public VariableManager addUserInfo(VariableManager vm, Vector v){
		// Here is the order in the vector
		// [3, Rickard, tynne, Rickard, Larsson, Drakarve, Havdhem, 620 11, Sweden, Gotland,
		// rickard@imcode.com, 0, 1001, 0, 1]
		//(v.get(1)==null) ? "" : (req.getParameter("password1")) ;

		if(v.size() == 0) {
			for(int i = 0; i < 20; i++)
			    v.add(i, "") ;
		}

		vm.addProperty("LOGIN_NAME", v.get(1).toString()) ;
		//	vm.addProperty("PWD1", v.get(2).toString()) ;
		//	vm.addProperty("PWD2", v.get(2).toString()) ;
		//	vm.addProperty("PWD1", "") ;
		//	vm.addProperty("PWD2", "") ;
		vm.addProperty("FIRST_NAME", v.get(3).toString()) ;
		vm.addProperty("LAST_NAME", v.get(4).toString()) ;
		vm.addProperty("TITLE", v.get(5).toString()) ;
		vm.addProperty("COMPANY", v.get(6).toString()) ;

		vm.addProperty("ADDRESS", v.get(7).toString()) ;
		vm.addProperty("CITY", v.get(8).toString()) ;
		vm.addProperty("ZIP", v.get(9).toString()) ;
		vm.addProperty("COUNTRY", v.get(10).toString()) ;
		vm.addProperty("COUNTRY_COUNCIL", v.get(11).toString()) ;
		vm.addProperty("EMAIL", v.get(12).toString()) ;

		// Lets fix the active flag
		// log("AKTIVIETE: " + v.get(16).toString()) ;
		vm.addProperty("ACTIVE", "1") ;
		if( v.get(18).equals("1") || v.get(18).equals(""))
		    vm.addProperty("ACTIVE_FLAG" , "CHECKED") ;
		else
		    vm.addProperty("ACTIVE_FLAG" , "") ;
		return vm ;
    }
	
	
	public void log( String str) {
		super.log(str) ;
		log.debug("AdminUserProps: " + str ) ;
    }
	

	
	// Create html for admin_part in AdminUserResp
	public String createAdminPartHtml ( User user, User userToChange, IMCServiceInterface imcref, HttpServletRequest req, HttpServletResponse res, HttpSession session){

		String html_admin_part = "";
		Vector vec_admin_part = new Vector() ;
		Html htm = new Html() ;
		
		String[] userRoles = null;
		String[] useradminRoles = null;
		String userType = null;
		Properties userInfo = null;
		
		Vector userRolesV = new Vector();  
		String rolesMenuStr = "";
		
		Vector useradminRolesV = new Vector();
		String rolesMenuUseradminStr = "";
		
		// check if user is a Superadmin, adminRole = 1
		boolean isSuperadmin = imcref.checkUserAdminrole ( user.getUserId(), 1 );
		
		if ( null != session.getAttribute("tempUser") ){
		
			//Lets get temporary values from session if there is some.
			userRoles = (String[])session.getAttribute("tempUserRoles");
			useradminRoles = (String[])session.getAttribute("tempUseradminRoles");
			userType = (String)session.getAttribute("tempUserType");
			userInfo = (Properties)session.getAttribute("tempUser");
		}
		
		// Lets get ROLES from DB
		String[] rolesArr = {};
		
		if ( isSuperadmin ) {
			rolesArr = imcref.sqlProcedure("GetAllRoles") ;
		}else{
			rolesArr = imcref.sqlProcedure("GetUseradminPermissibleRoles " + user.getUserId() );
		}
		Vector allRolesV  = new Vector(java.util.Arrays.asList(rolesArr)) ;
		
		
		
		//Lets get all ROLES from DB except of Useradmin and Superadmin
		Vector rolesV  = (Vector)allRolesV.clone();
		
		ListIterator listiter = rolesV.listIterator();
		while (listiter.hasNext() ){
			listiter.next();
			String rolename = listiter.next().toString();
	 		if ( rolename.equals("Superadmin") || rolename.equals("Useradmin") ){
				listiter.remove();
				listiter.previous();
				listiter.remove();
			}
		}
		
		
		// Lets get all USERTYPES from DB
		String[] usersArr = imcref.sqlProcedure("GetUserTypes "+ user.getLangPrefix() ) ;
		Vector userTypesV  = new Vector(java.util.Arrays.asList(usersArr)) ;
		
		
							
		
		if ( userToChange == null ){   // ADD_USER mode 
			
			
			// Lets get this users usertype from requerst object if we don´t have got them from session.
			if ( userType == null ){
				userType = ( req.getParameter("user_type") == null ) ? "" : req.getParameter("user_type");
			}
			log("userType:"+userType);
			
		
			// Lets get the information for users roles and put them in a vector
			// if we don´t have got any roles from session we try to get them from request object
			if ( userRoles == null ) {
				userRoles = (req.getParameterValues("roles")==null) ? new String[0] : req.getParameterValues("roles");
			}	
			userRolesV = new Vector(java.util.Arrays.asList(userRoles)) ;
						
			log("Size allRolesV: "+allRolesV.size());
			log("Size theUserRolesV: "+userRolesV.size());

			// Lets create html option for user roles
			rolesMenuStr = htm.createHtmlCode("ID_OPTION",userRolesV, allRolesV) ;
			
			
			if ( isSuperadmin ){
				// Lets get the information for usersadmin roles and put them in a vector
				// if we don´t have got any roles from session we try to get them from request object
				if ( useradminRoles == null ) {
					useradminRoles = (req.getParameterValues("useradmin_roles")==null) ? new String[0] : req.getParameterValues("useradmin_roles");
				}	
				useradminRolesV = new Vector(java.util.Arrays.asList(useradminRoles)) ;
						
				log("Size theUsersdminRolesV: "+useradminRolesV.size());

				// Lets create html option for useradmin roles
				rolesMenuUseradminStr = htm.createHtmlCode("ID_OPTION",useradminRolesV, rolesV) ;
			}
			
			
			
			// Lets get the active flag from the session if we have any
			String active = "1";
			if ( userInfo != null ){
				active = userInfo.getProperty("active");
			}

						
			String user_type = htm.createHtmlCode("ID_OPTION", userType, userTypesV ) ;
			
	
			vec_admin_part.add("#ACTIVE#"); 		vec_admin_part.add("1");
			vec_admin_part.add("#ACTIVE_FLAG#");
			if ( active.equals("1") ){
					vec_admin_part.add("checked");
			}else{
				vec_admin_part.add("");
			}
			
			vec_admin_part.add("#USER_CREATE_DATE#"); 	vec_admin_part.add(" ");
			vec_admin_part.add("#USER_TYPES#");  		vec_admin_part.add(user_type) ;
			vec_admin_part.add("#ROLES_MENU#");			vec_admin_part.add(rolesMenuStr);
			vec_admin_part.add("#ROLES_MENU_USERADMIN#");
			if ( isSuperadmin ){
					vec_admin_part.add(rolesMenuUseradminStr);
			}else{
				vec_admin_part.add("");
			}
		
		
		}else{   // CHANGE_USER mode
		
			String active = "";
		
			// if OK_PHONES or DELETE_PHONES or EDIT_PHONES  was pressed we have to get values from req object		
			if( req.getParameter("ok_phones") != null || req.getParameter("delete_phones") != null ||
	    		req.getParameter("edit_phones") != null) {
				
				if ( ("1").equals(req.getParameter("active") ) ){
					active = "1";
				}
			   
				userRoles = (req.getParameterValues("roles")==null) ? new String[0] : req.getParameterValues("roles");
				useradminRoles = (req.getParameterValues("useradmin_roles")==null) ? new String[0] : req.getParameterValues("useradmin_roles");
				userType = req.getParameter("user_type");

			}else{
			
				// Lets get this user usertype from DB if we don´t have got them from session.
				if ( userType == null){
					userType = imcref.sqlQueryStr("GetUserType " + userToChange.getUserId()) ;
				}
				
				
				// Lets get the information for users roles and put them in a vector
				// if we don´t have got any roles from session we try to get them from DB
				if ( userRoles == null ) {
					userRoles = imcref.sqlProcedure("GetUserRolesIds " + userToChange.getUserId()) ;
				}
				
				
				if ( isSuperadmin ){
					// Lets get the information for usersadmin roles and put them in a vector
					// if we don´t have got any roles from session we try to get them from DB
					if ( useradminRoles == null ) {
						useradminRoles = imcref.sqlProcedure("GetUseradminPermissibleRoles " + userToChange.getUserId()) ;
					}
				}
				
					
				active = (userToChange.isActive() == true ) ? "1" : "0";
			}
				
			
			// Lets create html option for user types
			String user_type = htm.createHtmlCode("ID_OPTION", userType, userTypesV ) ;			
		
			// Lets put the user roles in the vector 			
			userRolesV = new Vector(java.util.Arrays.asList(userRoles)) ;
			
			// Lets create html option for user roles
			rolesMenuStr = htm.createHtmlCode("ID_OPTION",userRolesV, allRolesV) ;
			
			if ( isSuperadmin ){
				// Lets put the roles that useradmin is allow to administrate in a vector
				useradminRolesV = new Vector(java.util.Arrays.asList(useradminRoles)) ;
			
				// Lets create html option for useradmin roles
				rolesMenuUseradminStr = htm.createHtmlCode("ID_OPTION",useradminRolesV, rolesV) ;
			}
			
			vec_admin_part.add("#ACTIVE#"); 		vec_admin_part.add("1");	
			vec_admin_part.add("#ACTIVE_FLAG#");
			if ( ("1").equals(active) ){
			 	vec_admin_part.add("checked");  
			}else{
				vec_admin_part.add("");
			}
			
			vec_admin_part.add("#USER_CREATE_DATE#"); 	vec_admin_part.add(userToChange.getCreateDate());
			vec_admin_part.add("#USER_TYPES#");  		vec_admin_part.add(user_type) ;
			vec_admin_part.add("#ROLES_MENU#");			vec_admin_part.add(rolesMenuStr);
			vec_admin_part.add("#ROLES_MENU_USERADMIN#");	
			if ( isSuperadmin ){
				vec_admin_part.add(rolesMenuUseradminStr);
			}else{
				vec_admin_part.add("");
			}
			
			
		}
		
		// lets parse and return the html_admin_part
		if ( isSuperadmin ){
			html_admin_part = imcref.parseDoc( vec_admin_part, HTML_RESPONSE_SUPERADMIN_PART,   user.getLangPrefix() );	
		}else{
			html_admin_part = imcref.parseDoc( vec_admin_part, HTML_RESPONSE_ADMIN_PART,   user.getLangPrefix() );	
		}
		return html_admin_part;
		
	}
	
	
	/**
       Returns a String, containing the userID in the request object.If something failes,
       a error page will be generated and null will be returned.
    */

    public String getCurrentUserId(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException  {

		
		String userId = req.getParameter("CURR_USER_ID") ;
	
		// Get the session
		HttpSession session = req.getSession(false);
		
		if (userId == null){
			// Lets get the userId from the Session Object.
			userId = (String)session.getAttribute("userToChange");
	//System.out.println("AdminUserProps-getCurrentUserId() userId= " + userId);
		}
		
		//	if (userId == null)
		//		userId = req.getParameter("CURR_USER_ID") ;
		//			if (userId == null || userId.startsWith("#")) {

		if (userId == null ) {
			String header = "ChangeUser error. " ;
			String msg = "No user_id was available." + "<BR>";
			this.log(header + msg) ;
			AdminError err = new AdminError(req,res,header, msg) ;
			return null;
		}else{
		    this.log("AnvändarId=" + userId) ;
		}
		return userId ;
	} // End getCurrentUserId


	public String doPasswordString (String pwd){
	// Lets fix the password string
		int len = pwd.length(); 
		pwd = "";
    	for ( int i=0; i < len; i++ ){
    		pwd += "*";
    	}
		return pwd;
	}
	
}
