import java.io.* ;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.* ;
import imcode.external.diverse.* ;
import imcode.util.* ;

public class AdminUser extends Administrator
{
	private final static String CVS_REV = "$Revision$" ;
	private final static String CVS_DATE = "$Date$" ;

	String HTML_TEMPLATE ;
	String HTML_RESPONSE ;

	/**
	The GET method creates the html page when this side has been
	redirected from somewhere else.
	**/

	public void doGet(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException
	{

		String host 				= req.getHeader("Host") ;
		String server 			= Utility.getDomainPref("adminserver",host) ;

		//Checks if it was a redirect från AdminUserProps
		if ( req.getParameter("CHANGE_USER") != null ||
			req.getParameter("ok_phones") != null || 
			req.getParameter("delete_phones") != null ||
			req.getParameter("edit_phones") != null)
		{
			this.doPost(req,res);
			return;	
		}

		// Lets validate the session
		if (super.checkSession(req,res) == false)	return ;

		// Lets get an user object
		imcode.server.User user = super.getUserObj(req,res) ;
		if(user == null)
		{
			String header = "Error in AdminCounter." ;
			String msg = "Couldnt create an user object."+ "<BR>" ;
			this.log(header + msg) ;
			AdminError err = new AdminError(req,res,header,msg) ;
			return ;
		}

		// Lets verify that the user who tries to add a new user is an admin
		if (super.checkAdminRights(server, user) == false)
		{
			String header = "Error in AdminCounter." ;
			String msg = "The user is not an administrator."+ "<BR>" ;
			this.log(header + msg) ;
			AdminError err = new AdminError(req,res,header,msg) ;
			return ;
		}

		VariableManager vm = new VariableManager() ;
		Html ht = new Html() ;
		RmiLayer rmi = new RmiLayer(user) ;

		// Lets get the category from the request Object.
		String category = req.getParameter("user_categories") ;
		if (category == null) category = "1" ; // 1 is the ordinary user
		// log("Category: " + category) ;

		String lang_prefix = (String)user.get("lang_prefix");
		if(lang_prefix == null)lang_prefix="se";

		// Lets get all USERTYPES from DB
		String[] userTypes = rmi.execSqlQuery(server, "GetUserTypes "+lang_prefix) ;
		Vector userTypesV  = /* super. */ convert2Vector(userTypes) ;
		String user_type = ht.createHtmlCode("ID_OPTION", category, userTypesV ) ;
		vm.addProperty("USER_TYPES", user_type  ) ;

		// Lets get all USERS from DB
		String[] usersArr = rmi.execSqlQuery(server, "GetCategoryUsers " + category) ;
		Vector usersV  = /* super. */ convert2Vector(usersArr) ;
		String usersOption = ht.createHtmlCode("ID_OPTION", "", usersV ) ;
		vm.addProperty("USERS_MENU", usersOption  ) ;
		
		//create the page
		this.sendHtml(req,res,vm, HTML_TEMPLATE) ;

	} // End doGet

	/**
	POST
	**/

	public void doPost(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException
	{

		String host 				= req.getHeader("Host") ;
		String server 			= Utility.getDomainPref("adminserver",host) ;

		// Lets validate the session
		if (super.checkSession(req,res) == false)	return ;

		// Lets get an user object
		imcode.server.User user = super.getUserObj(req,res) ;
		if(user == null)
		{
			String header = "Error in AdminCounter." ;
			String msg = "Couldnt create an user object."+ "<BR>" ;
			this.log(header + msg) ;
			AdminError err = new AdminError(req,res,header,msg) ;
			return ;
		}

		// Lets check if the user is an admin, otherwise throw him out.
		if (super.checkAdminRights(server, user) == false)
		{
			String header = "Error in AdminCounter." ;
			String msg = "The user is not an administrator."+ "<BR>" ;
			this.log(header + msg) ;
			AdminError err = new AdminError(req,res,header,msg) ;
			return ;
		}

		// Lets set up the servlet to send the next request to:
		String theServlet = "AdminUserProps?adminTask=" ;
		
		//Lets get the prefered lang prefix
		String lang_prefix = (String)user.get("lang_prefix");
		if(lang_prefix == null)lang_prefix="se";


		// ******* GENERATE AN ADD_USER PAGE **********
		if( req.getParameter("ADD_USER") != null )
		{
			VariableManager vm = new VariableManager() ;
			Html htm = new Html() ;

			//lets get the session to store all data in incase of reset
			HttpSession session = req.getSession(false);
			
			// Lets get all ROLES from DB
			RmiLayer rmi = new RmiLayer(user) ;
			String[] rolesArr = rmi.execSqlQuery(server, "GetAllRoles") ;
			Vector allRolesV  = /* super. */ convert2Vector(rolesArr) ;
			String opt = htm.createHtmlCode("ID_OPTION", "", allRolesV ) ;
			vm.addProperty("ROLES_MENU", opt  ) ;

			// Lets send empty fields to the page
			// a vector, bigger than the amount fields
			Vector userInfoV = new Vector(20) ; 
			vm = this.addUserInfo(vm,userInfoV) ;
			vm.addProperty("ADMIN_TASK", "ADD_USER") ;
			
			vm.addProperty( "PHONE_ID", "");
			vm.addProperty( "COUNTRY_CODE", "");
			vm.addProperty( "AREA_CODE", "");
			vm.addProperty( "NUMBER", "");
			
			
			//put empty string in pwd-fields
			vm.addProperty("PWD1", "");
			vm.addProperty("PWD2","");
			
			//empty phoneslist
			Vector phonesV  = new Vector();
			String phones = htm.createHtmlCode("ID_OPTION", "" , phonesV ) ;
			vm.addProperty("PHONES_MENU", phones  ) ;
						
			// Lets get all USERTYPES from DB
			String[] usersArr = rmi.execSqlQuery(server, "GetUserTypes "+lang_prefix) ;
			Vector usersV  = /* super. */ convert2Vector(usersArr) ;
			String user_type = htm.createHtmlCode("ID_OPTION", "1", usersV ) ;
			vm.addProperty("USER_TYPES", user_type  ) ;

			//empty createdate
			vm.addProperty("USER_CREATE_DATE", " " ) ;

			// language id: lets set swedish as default
			String[] langList = rmi.execSqlProcedure(server, "GetLanguageList 'se'") ; 
			Vector selectedLangV = new Vector() ;
			selectedLangV.add("1") ;  
			vm.addProperty("LANG_TYPES", htm.createHtmlCode("ID_OPTION",selectedLangV, /* super. */ convert2Vector(langList))) ;

			//store all data into the session
			session.putValue("RESET_allRolesV", allRolesV);			
			session.putValue("RESET_usersArr", usersArr);			
			session.putValue("RESET_userCreateDate", " ");			
			session.putValue("Ok_phoneNumbers", phonesV);				
			session.putValue("RESET_langList", langList);
			session.putValue("RESET_selectedLangV", selectedLangV);

			// Lets create the HTML page
			this.sendHtml(req, res, vm, HTML_RESPONSE) ;
			this.log("ADD_USER är färdig") ;
			return ;
		}
		
		// ******* CHANGE_USER **********
		if( req.getParameter("CHANGE_USER") != null )
		{
			log("Changeuser") ;
			
			// Lets get the user which should be changed
			String userId = this.getCurrentUserId(req,res) ;
			if (userId == null)	return ;

			RmiLayer imc = new RmiLayer(user) ;

			//lets get the session to store all data in incase of reset
			HttpSession session = req.getSession(false);					

			// Lets get all Userinformation and add it to html page
			String[] userInfo = imc.execSqlProcedure(server, "GetUserInfo " + userId) ;
			
			Vector userV = /* this. */ convert2Vector(userInfo) ;			
			
			VariableManager vm = new VariableManager() ;
			Html htm = new Html() ;
			vm = this.addUserInfo(vm,userV) ;
			
			//put empty string in pwd-fields
			vm.addProperty("PWD1", "");
			vm.addProperty("PWD2", "");

			// Lets get the information for users roles
			// Lets collect the current rolesIds for the user
			String[] theUserRoles = imc.execSqlProcedure(server, "GetUserRolesIds " + userId) ;
			Vector theUserRolesV = /* this. */ convert2Vector(theUserRoles) ;			

			// Lets get all roles from the database
			String[] allRoles = imc.execSqlProcedure(server, "GetAllRoles") ;
			Vector allRolesV = /* this. */ convert2Vector(allRoles) ;			
			String rolesMenuStr = htm.createHtmlCode("ID_OPTION",theUserRolesV, allRolesV) ;
			
			// Lets get this users usertype
			String theUsersType = imc.execSqlQueryStr(server, "GetUserType " + userId) ;
			
			// Lets get all USERTYPES from DB
			String[] usersArr = imc.execSqlQuery(server, "GetUserTypes "+lang_prefix) ;
			Vector usersV  = /* super. */ convert2Vector(usersArr) ;
			String user_type = htm.createHtmlCode("ID_OPTION", theUsersType, usersV ) ;
			vm.addProperty("USER_TYPES", user_type  ) ;

			// Lets get the users create date
			String userCreateDate = imc.execSqlProcedureStr(server, "GetUserCreateDate " + userId) ;
			vm.addProperty("USER_CREATE_DATE", "" + userCreateDate  ) ;

			// Lets fix all users phone numbers from DB
			String[][] phonesArr= imc.execProcedureMulti(server, "GetUserPhoneNumbers " + userId) ;		
			Vector phoneNumbers = getPhonesArrayVector (phonesArr);
			Vector phonesV  = this.getPhonesVector(phoneNumbers); 			
			
			String selected = "";
			if (phonesV.size() > 0)
			{
				selected = (String)phonesV.get(0);
			}
			
			String phones = htm.createHtmlCode("ID_OPTION", selected , phonesV ) ;	
			vm.addProperty("PHONES_MENU", phones  ) ;

			
			// Lets get the the users language id
			String[] langList = imc.execSqlProcedure(server, "GetLanguageList 'se'") ; // FIXME: Get the correct language for the user
			Vector selectedLangV = new Vector() ;
			selectedLangV.add(userV.get(16).toString()) ;
			vm.addProperty("LANG_TYPES", htm.createHtmlCode("ID_OPTION",selectedLangV, /* super. */ convert2Vector(langList))) ;

			//the phone nr fields
			vm.addProperty( "PHONE_ID", "");
			vm.addProperty( "COUNTRY_CODE", "");
			vm.addProperty( "AREA_CODE", "");
			vm.addProperty( "NUMBER", "");	
			
			// Lets create the HTML page
			String adminTask = req.getParameter("adminTask");
			if (adminTask == null)
			{
				adminTask = "SAVE_CHANGED_USER";
			}
			vm.addProperty("ADMIN_TASK", adminTask) ;
			vm.addProperty("CURR_USER_ID", userId) ;
			vm.addProperty("ROLES_MENU", htm.createHtmlCode("ID_OPTION",theUserRolesV, allRolesV)) ;
			
			//store all data into the session
			session.putValue("RESET_userId",userId);
			session.putValue("RESET_allRolesV", allRolesV);
			session.putValue("RESET_usersArr", usersArr);
			session.putValue("RESET_userCreateDate", userCreateDate);
			session.putValue("Ok_phoneNumbers", phoneNumbers);	
			session.putValue("RESET_langList", langList);
			session.putValue("RESET_selectedLangV", selectedLangV);
			
			
			this.sendHtml(req, res, vm, HTML_RESPONSE) ;

			return ;
		}

		//******** ok_phones or delete_phones or edit_phones ***********
		if( req.getParameter("ok_phones") != null || req.getParameter("delete_phones") != null ||
			req.getParameter("edit_phones") != null)
		{
			log("ok_phones in doPost");

			//lets get the session to get all data in incase of reset
			HttpSession session = req.getSession(false);	
			Properties params =(Properties) session.getValue("ok_phones_params");		

			// Lets get the user which should be changed
			String userId = req.getParameter("user_Id");

			if (userId == null)	return ;//kolla om det blir null eller fnuttfnutt	
			//	String[] userInfo  = (String[]) session.getValue("RESET_userInfo");	
			String[] userInfo = new String[20];
			// Lets get all Userinformation and add it to html page
			// Lets get the parameters we know we are supposed to get from the params object
			userInfo[0] = userId;
			userInfo[1]	= params.getProperty("login_name") ;
			userInfo[2]	= params.getProperty("password1");

			userInfo[3] = params.getProperty("first_name") ;
			userInfo[4] = params.getProperty("last_name") ;
			userInfo[5] = params.getProperty("title") ;
			userInfo[6] = params.getProperty("company") ;

			userInfo[7] = params.getProperty("address") ;
			userInfo[8] = params.getProperty("city");
			userInfo[9] = params.getProperty("zip") ;
			userInfo[10] = params.getProperty("country") ;
			userInfo[11]  = params.getProperty("country_council") ;
			userInfo[12]  	= params.getProperty("email") ;
			//	userInfo[13] = "0"; //??????
			//	userInfo[14] = "1001"; //??????
			//	userInfo[15] = "1"; // ???????
			userInfo[16]  	= params.getProperty("lang_id");
			userInfo[17]  	= params.getProperty("user_type");
			userInfo[18] 	= params.getProperty("active") ;
			//	userInfo[19] = "1999-01-01 12:00:00.0"; // ??????


			Vector userV = /* this. */ convert2Vector(userInfo) ;	

			VariableManager vm = new VariableManager() ;
			Html htm = new Html() ;
			vm = this.addUserInfo(vm,userV) ;
			
			//update passwordfields
			vm.addProperty("PWD1", params.getProperty("password1") == null ? "": params.getProperty("password1"));
			vm.addProperty("PWD2",params.getProperty("password2") == null ? "": params.getProperty("password2"));
			
			// Lets get the information for users roles
			// Lets collect the current rolesIds for the user
			Vector theUserRolesV = (Vector)session.getValue("ok_phones_roles");	
			if(	theUserRolesV == null) return;	

			// Lets get all roles from the database
			Vector allRolesV = (Vector)session.getValue("RESET_allRolesV");	
			if (	allRolesV == null) 	
			{
				AdminError err = new AdminError(req,res,"Add/edit user", "An eror occured!") ;
				return ;
			}	
			
			log("Size allRolesV: "+allRolesV.size());
			log("Size theUserRolesV: "+theUserRolesV.size());
			
			String rolesMenuStr = htm.createHtmlCode("ID_OPTION",theUserRolesV, allRolesV) ;		

			// Lets get this users usertype
			String theUsersType = params.getProperty("user_type");		
			log("theUsersType:"+theUsersType);
			// Lets get all USERTYPES from DB
			String[] usersArr = (String[]) session.getValue("RESET_usersArr");

			Vector usersV  = /* super. */ convert2Vector(usersArr) ;
			String user_type = htm.createHtmlCode("ID_OPTION", theUsersType, usersV ) ;
			vm.addProperty("USER_TYPES", user_type  ) ;

			// Lets get the users create date
			String userCreateDate = (String) session.getValue("RESET_userCreateDate");

			vm.addProperty("USER_CREATE_DATE", "" + userCreateDate  ) ;

			// Lets fix all users phone numbers from the session
			Vector  phoneNumbers = (Vector)session.getValue("Ok_phoneNumbers");
			Vector phonesV  = this.getPhonesVector(phoneNumbers); 			
			log("test"+req.getParameter("edit_phones"));
			
			boolean found = false;	
			if (req.getParameter("edit_phones")!= null)
			{
			//	Vector phonesV  = (Vector)session.getValue("Ok_phoneNumbers");
				Enumeration enum = phoneNumbers.elements();
						
				while (enum.hasMoreElements() && !found)
				{
					String[] temp = (String[]) enum.nextElement();
					if (temp[0].equals(req.getParameter("edit_phones")))
					{	
						vm.addProperty( "PHONE_ID", temp[0]);
						vm.addProperty( "COUNTRY_CODE", temp[1]);
						vm.addProperty( "AREA_CODE", temp[2]);
						vm.addProperty( "NUMBER", temp[3]);
						found = true;
					}				
				}
			}
			if (!found)
			{
				vm.addProperty( "PHONE_ID", "");
				vm.addProperty( "COUNTRY_CODE", "");
				vm.addProperty( "AREA_CODE", "");
				vm.addProperty( "NUMBER", "");	
			}
			String selectedId = req.getParameter("selected_id") == null ? "" : req.getParameter("selected_id");
			log("selected_id= "+ selectedId);
			
			String phones = htm.createHtmlCode("ID_OPTION", selectedId, phonesV ) ;
			log("phones stringen: "+phones);
			vm.addProperty("PHONES_MENU", phones  ) ;



			// Lets get the the users language id
			String[] langList = (String[])session.getValue("RESET_langList");
			Vector selectedLangV = (Vector)session.getValue("RESET_selectedLangV");
			selectedLangV.add(userV.get(16).toString()) ;
			vm.addProperty("LANG_TYPES", htm.createHtmlCode("ID_OPTION",selectedLangV, /* super. */ convert2Vector(langList))) ;

			// Lets create the HTML page
			String adminTask = req.getParameter("adminTask");
			if(adminTask == null) adminTask = "";
			vm.addProperty("ADMIN_TASK", adminTask) ;
			vm.addProperty("CURR_USER_ID", userId) ;
			vm.addProperty("ROLES_MENU", htm.createHtmlCode("ID_OPTION",theUserRolesV, allRolesV)) ;			

			this.sendHtml(req, res, vm, HTML_RESPONSE) ;

			return ;
		}		


		//******* DELETE A USER ***********
		if( req.getParameter("DELETE_USER") != null )
		{
			/*		// Lets get the userId from the request Object.
			String userId = this.getCurrentUserId(req,res) ;
			if (userId == null)	return ;
			RmiLayer imc = new RmiLayer(user) ;

			// Lets delete the user
			//this.log("Nu tas följande användare bort: " + userId ) ;
			imc.execSqlUpdateProcedure(server, "DelUser " + userId ) ;
			//this.log("DELETE_USER är färdig") ;
			doGet(req, res) ;
			*/return;
		}

		// ***** RETURN TO ADMIN MANAGER *****

		if( req.getParameter("GO_BACK") != null )
		{
			String url = MetaInfo.getServletPath(req) ;
			url += "AdminManager" ;
			res.sendRedirect(url) ;
			return ;
		}

		doGet(req,res) ;

	} // end HTTP POST




	// ************************* NEW FUNCTIONS *************


	/**
	Returns a String, containing the userID in the request object.If something failes,
	a error page will be generated and null will be returned.
	*/

	public String getCurrentUserId(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException
	{

		// Lets get the userId from the request Object.
		String userId = req.getParameter("user_Id") ;
		//	if (userId == null)
		//		userId = req.getParameter("CURR_USER_ID") ;
		//			if (userId == null || userId.startsWith("#")) {

		if (userId == null )
		{
			String header = "ChangeUser error. " ;
			String msg = "No user_id was available." + "<BR>";
			this.log(header + msg) ;
			AdminError err = new AdminError(req,res,header, msg) ;
			return null;
		}
		else
			this.log("AnvändarId=" + userId) ;
		return userId ;

	} // End getCurrentUserId

	//adds the phoneNrId to the vector followed by the phoneNumber
	public Vector getPhonesArrayVector(String[][] phoneNr)
	{
		Vector phonesArrV = new Vector();

		for(int i=0; i < phoneNr.length; i++)
		{

			phonesArrV.addElement(phoneNr[i]);

		}
		return phonesArrV;
	}	

	public Vector getPhonesVector(Vector phonesArrV)
	{

		Vector phonesV = new Vector();
		Enumeration enum = phonesArrV.elements();

		while (enum.hasMoreElements())
		{
			String[] tempPhone = (String[])enum.nextElement();
			String temp = "";
			for(int i=0; i < tempPhone.length-1; i++)
			{

				if (i== 0)
				{
					phonesV.addElement(tempPhone[i]);							
				}else
				{
					temp += tempPhone[i] +" ";					
				}
			}
			phonesV.addElement(temp);

		}
		return phonesV;
	}


	/**
	Adds the userInformation to the htmlPage. if an empty vector is sent as argument
	then an empty one will be created
	**/
	public VariableManager addUserInfo(VariableManager vm, Vector v)
	{
		// Here is the order in the vector
		// [3, Rickard, tynne, Rickard, Larsson, Drakarve, Havdhem, 620 11, Sweden, Gotland,
		// rickard@imcode.com, 0, 1001, 0, 1]
		//(v.get(1)==null) ? "" : (req.getParameter("password1")) ;

		if(v.size() == 0)
		{
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

	

	/**
	Lets get the Userparameters
	*/

	public Vector getUserParameters(HttpServletRequest req) throws ServletException, IOException
	{
		// Lets get the users from the multichoicebox
		String[] roles = (req.getParameterValues("AllUsers")==null) ? new String[0] : (req.getParameterValues("AllUsers"));
		return /* super. */ convert2Vector(roles) ;
	}


	/**
	Init: Detects paths and filenames.
	*/

	public void init(ServletConfig config) throws ServletException
	{

		super.init(config);
		HTML_TEMPLATE = "AdminChangeUser.htm";
		HTML_RESPONSE = "AdminUserResp.htm";

	}

	public void log( String str)
	{
		super.log(str) ;
		System.out.println("AdminUser: " + str ) ;
	}


} 
