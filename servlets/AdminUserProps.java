import java.io.* ;
import java.util.* ;

import javax.servlet.*;
import javax.servlet.http.*;

import imcode.external.diverse.* ;
import imcode.util.* ;
import imcode.server.* ;

public class AdminUserProps extends Administrator
{
    private final static String CVS_REV = "$Revision$" ;
    private final static String CVS_DATE = "$Date$" ;

    /**
     * POST
     **/

    public void doPost(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException
    {

	String host				= req.getHeader("Host") ;
	IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterfaceByHost(host) ;

	// Lets validate the session
	if (super.checkSession(req,res) == false) return ;

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
	if (imcref.checkAdminRights(user) == false)
	    {
		String header = "Error in AdminCounter." ;
		String msg = "The user is not an administrator."+ "<BR>" ;
		this.log(header + msg) ;
		AdminError err = new AdminError(req,res,header,msg) ;
		return ;
	    }

	// Lets check which button was pushed
	String adminTask = req.getParameter("adminTask") ;
	this.log("Argument till server:" + adminTask) ;
	if(adminTask == null)	adminTask = "" ;




	//******* RESET_FORM BUTTON WAS PUNSCHED ***********
	//sets up the needed parameters and redirect back to AdminUser
	if ( req.getParameter("RESET_FORM") != null )
	    {
		log("RESET_FORM");
		HttpSession session = req.getSession(false);
		if(session == null) return;

		String userId = (String)session.getAttribute("RESET_userId");
		if(userId == null)
		    {
			this.sendErrorMsg(req,res, "Add/edit user", "An eror occured!");
			return;
		    }

		res.sendRedirect("AdminUser?CHANGE_USER=true&user_Id="+userId+"&adminTask="+adminTask) ;
		return;
	    }

	//******** ok_phones button **********
	//sets up the needed parameters and redirect back to AdminUser
	if ( req.getParameter("ok_phones") != null )
	    { //adds or changes a phoneNr to the select list

		HttpSession session = req.getSession(false);
		if(session == null) return;

		String userId = session.getAttribute("RESET_userId") == null ? "":(String) session.getAttribute("RESET_userId");
		//måste fixa så att all anv. data sparas undan i servleten
		log("ok_phones");


		session.setAttribute("ok_phones_params", this.getParameters(req));
		session.setAttribute("ok_phones_roles", getRolesParameters(req,res)==null ? new Vector() : getRolesParameters(req,res));

		//lets get the phonenumber from the form
		session.setAttribute("country_code",req.getParameter("country_code"));
		session.setAttribute("area_code",req.getParameter("area_code"));
		session.setAttribute("local_code",req.getParameter("local_code"));

		//add it into Vectorn
		//gets all phonenumbers from the session
		//		log("phone_id = "+req.getParameter("phone_id"))	;
		String selectedId="";
		if (! req.getParameter("area_code").equals("") &&
		    !req.getParameter("local_code").equals(""))
		    {
			Vector phonesV  = (Vector)session.getAttribute("Ok_phoneNumbers");
			if (phonesV == null)
			    {
				this.sendErrorMsg(req,res, "Add/edit user", "An eror occured!");
				return;
			    }

			Enumeration enum = phonesV.elements();
			int tempId = 1;
			boolean found = false;
			while (enum.hasMoreElements())
			    {
				String[] temp = (String[]) enum.nextElement();
				log(temp[0]+" == " +req.getParameter("phone_id"));
				if (temp[0].equals(req.getParameter("phone_id")))
				    {
					selectedId = temp[0];
					phonesV.remove(temp);
					temp[1] = req.getParameter("country_code");
					temp[2] = req.getParameter("area_code");
					temp[3] = req.getParameter("local_code");
					phonesV.addElement(temp);

					found =  true;
				    }
				try
				    {
					if (Integer.parseInt(temp[0]) >= tempId)
					    {
						tempId = Integer.parseInt(temp[0]) + 1;
					    }

				    }catch(NumberFormatException nfe)
					{
					    log("NumberFormatException");
					}

			    }

			if (!found)
			    {
				String[] temp = new String[5];
				temp[0] = ""+tempId;
				selectedId = temp[0];
				temp[1] = req.getParameter("country_code");
				temp[2] = req.getParameter("area_code");
				temp[3] = req.getParameter("local_code");
				temp[4] = userId;
				phonesV.addElement(temp);
			    }

		    }

		res.sendRedirect("AdminUser?ok_phones=true&user_Id="+userId+"&selected_id="+selectedId+"&adminTask="+adminTask) ;
		return;
	    }

	//********* edit_phones button***********
	//sets up the needed parameters and redirect back to AdminUser
	if ( req.getParameter("edit_phones") != null )
	    {
		log("edit_phones");
		HttpSession session = req.getSession(false);

		String userId = (String)session.getAttribute("RESET_userId");

		session.setAttribute("ok_phones_params", this.getParameters(req));
		session.setAttribute("ok_phones_roles", getRolesParameters(req,res)==null ? new Vector() : getRolesParameters(req,res));

		String selectedNr = req.getParameter("user_phones");
		//		log("Number: "+selectedNr);
		res.sendRedirect("AdminUser?edit_phones="+selectedNr+"&user_Id="+userId+"&selected_id="+selectedNr+"&adminTask="+adminTask) ;
		return;
	    }

	//*********************delete_phonesbutton************
	//sets up the needed parameters and redirect back to AdminUser
	if ( req.getParameter("delete_phones") != null )
	    {
		HttpSession session = req.getSession(false);
		if(session == null) return;

		String userId = (String)session.getAttribute("RESET_userId");

		log("lets delete_phones from templist");

		Vector phonesV  = (Vector)session.getAttribute("Ok_phoneNumbers");
		if (phonesV == null)
		    {
			this.sendErrorMsg(req, res, "Add/edit user", "An eror occured!");
			return;
		    }

		Enumeration enum = phonesV.elements();
		boolean found = false;
		//		log("Size"+phonesV.size());
		while (enum.hasMoreElements() && !found)
		    {
			String[] temp = (String[]) enum.nextElement();
			log(temp[0]+" == " +req.getParameter("user_phones"));
			if (temp[0].equals( req.getParameter("user_phones")))
			    {
				phonesV.remove(temp);
				found =  true;
			    }
		    }

		String selectedNr = "";
		if (phonesV.size() > 0)
		    {
			String[] temp = (String[]) phonesV.firstElement();
			selectedNr = temp[0];
		    }

		session.setAttribute("Ok_phoneNumbers", phonesV);
		session.setAttribute("ok_phones_params", this.getParameters(req));
		session.setAttribute("ok_phones_roles", getRolesParameters(req,res)==null ? new Vector() : getRolesParameters(req,res));

		res.sendRedirect("AdminUser?delete_phones=true&user_Id="+userId+"&selected_id="+selectedNr+"&adminTask="+adminTask) ;
	    }


	// ******* SAVE NEW USER TO DB **********
	if( req.getParameter("SAVE_USER") != null && adminTask.equalsIgnoreCase("ADD_USER") )
	    {
		log("Lets add a new user to db") ;

		// Lets get the parameters from html page and validate them
		Properties params = this.getParameters(req) ;
		params = this.validateParameters(params,req,res) ;
		if(params == null) return ;

		//get session
		HttpSession session = req.getSession(false);
		if(session == null) return;

		// Lets get the roles from htmlpage
		Vector rolesV = this.getRolesParameters(req, res) ;
		if( rolesV == null) return ;

		// Lets validate the password
		if( UserHandler.verifyPassword(params,req,res) == false)	return ;



		// Lets check that the new username doesnt exists already in db
		String userName = params.getProperty("login_name") ;
		String userNameExists[] = imcref.sqlProcedure("FindUserName '" + userName + "'") ;
		if(userNameExists != null )
		    {
			if(userNameExists.length > 0 )
			    {
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
		if (phonesV == null)
		    {
			this.sendErrorMsg(req, res, "Add/edit user", "An eror occured!");
			return;
		    }

		// Lets build the users information into a string and add it to db
		params.setProperty("user_id", newUserId) ;
		String userStr = UserHandler.createUserInfoString(params) ;
		log("AddNewUser " + userStr) ;
		imcref.sqlUpdateProcedure("AddNewUser " + userStr) ;

		// Lets add the new users roles
		for(int i = 0; i<rolesV.size(); i++)
		    {
			String aRole = rolesV.elementAt(i).toString() ;
			imcref.sqlUpdateProcedure("AddUserRole " + newUserId + ", " + aRole) ;
		    }

		//spara telefonnummer från listan
		for(int i = 0; i<phonesV.size(); i++)
		    {
			String[] aPhone = (String[])phonesV.elementAt(i);
			String sqlStr = "phoneNbrAdd " + newUserId + ", '" ;//userId
			sqlStr += aPhone[1] + "', '" + aPhone[2] + "', '" + aPhone[3] + "'" ;//country_code,area_code,number

			log("PhoneNrAdd: " + sqlStr);

			imcref.sqlUpdateProcedure(sqlStr) ;
		    }

		this.goAdminUsers(req, res) ;
		return ;
	    }

	// ******** SAVE EXISTING USER TO DB ***************
	if( req.getParameter("SAVE_USER") != null && adminTask.equalsIgnoreCase("SAVE_CHANGED_USER"))
	    {
		log("******** SAVE EXISTING USER TO DB ***************");
		HttpSession session = req.getSession(false);
		if(session == null) return;

		// Lets get the userId from the request Object.
		String userId = this.getCurrentUserId(req,res) ;
		if (userId == null)	return ;

		// Lets get the parameters from html page and validate them
		Properties params = this.getParameters(req) ;
		params.setProperty("user_id", userId) ;

		// Lets check the password. if its empty, then it wont be updated. get the
		// old password from db and use that one instad
		String currPwd = imcref.sqlProcedureStr("GetUserPassword " + userId ) ;
		if( currPwd.equals("-1") )
		    {
			String header = "Fel! Ett lösenord kund inte hittas" ;
			String msg = "Lösenord kunde inte hittas"+ "<BR>" ;
			this.log(header + msg) ;
			AdminError err = new AdminError(req,res,header,msg) ;
			log("innan return i currPwd.equals");
			return ;
		    }

		if(params.getProperty("password1").equals(""))
		    {
			params.setProperty("password1", currPwd) ;
			params.setProperty("password2", currPwd) ;
		    }

		// Ok, Lets validate all fields
		params = this.validateParameters(params,req,res) ;
		if(params == null) return ;

		// Lets get the roles from htmlpage
		Vector rolesV = this.getRolesParameters(req, res) ;
		if( rolesV == null) return ;

		// rolesV = this.getRolesParameters(req, res) ;
		if( rolesV == null) return ;

		// Lets check if the password contains something. If it doesnt
		// contain anything, then assume that the old one wont be updated
		if( UserHandler.verifyPassword(params,req,res) == false)	return ;

		// Lets get phonnumbers from the session
		Vector phonesV  = (Vector)session.getAttribute("Ok_phoneNumbers");
		if (phonesV == null)
		    {
			this.sendErrorMsg(req, res, "Add/edit user", "An eror occured!");
			return;
		    }

		// Lets build the users information into a string and add it to db
		String userStr = "UpdateUser " + UserHandler.createUserInfoString(params) ;
		log("userSQL: " + userStr) ;
		imcref.sqlUpdateProcedure(userStr) ;

		// Lets add the new users roles. but first, delete users current Roles
		// and then add the new ones
		imcref.sqlUpdateProcedure("DelUserRoles " + userId ) ;
		for(int i = 0; i<rolesV.size(); i++)
		    {
			String aRole = rolesV.elementAt(i).toString();
			imcref.sqlUpdateProcedure("AddUserRole  " + userId + ", " + aRole) ;
		    }

		//radera telefonnummer
		imcref.sqlUpdateProcedure("DelPhoneNr " + userId ) ;

		// spara från listan, till databasen



		for(int i = 0; i<phonesV.size(); i++)
		    {
			String[] aPhone = (String[])phonesV.elementAt(i);
			String sqlStr = "phoneNbrAdd " + aPhone[4] + ", '" ;//userId
			sqlStr += aPhone[1] + "', '" + aPhone[2] + "', '" + aPhone[3] + "'" ;//country_code,area_code,number

			imcref.sqlUpdateProcedure(sqlStr) ;

		    }

		this.goAdminUsers(req, res) ;
		return ;
	    }

	// ******** GO_BACK TO THE MENY ***************
	if( req.getParameter("GO_BACK") != null )
	    {
		this.removeSessionParams(req);
		String url = "AdminUser" ;
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
    public void removeSessionParams(HttpServletRequest req)	throws ServletException, IOException
    {
	HttpSession session = req.getSession(false);
	if (session == null) return;
	try
	    {
		session.removeAttribute("ok_phones_params");
		session.removeAttribute("ok_phones_roles");
		session.removeAttribute("country_code");
		session.removeAttribute("area_code");
		session.removeAttribute("local_code");
		session.removeAttribute ("RESET_userId");
		session.removeAttribute ("RESET_allRolesV");
		session.removeAttribute ("RESET_usersArr");
		session.removeAttribute ("RESET_userCreateDate");
		session.removeAttribute ("Ok_phoneNumbers");
		session.removeAttribute ("RESET_langList");
		session.removeAttribute ("RESET_selectedLangV");

	    }catch(IllegalStateException ise)
		{
		    log("session has been invalidated so no need to remove parameters");
		}
    }

    /**
       a error page will be generated, fore those times the user uses the backstep in
       the browser
    */
    private void sendErrorMsg(HttpServletRequest req, HttpServletResponse res, String header, String msg) throws ServletException, IOException
    {

	AdminError err = new AdminError(req,res, header, msg) ;
    }

    /**
       Returns a String, containing the newUserID. if something failes, a error page
       will be generated and null will be returned.
    */

    public String getNewUserID(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
	String host				= req.getHeader("Host") ;
	IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterfaceByHost(host) ;

	String newUserId = imcref.sqlProcedureStr("GetHighestUserId" ) ;

	if ( newUserId.equals("") )
	    {
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

    public Vector getRolesParameters(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException
    {

	// Lets get the roles
	// Vector rolesV = this.getRolesParameters(req) ;
	String[] roles = (req.getParameterValues("roles")==null) ? new String[0] : (req.getParameterValues("roles"));
	Vector rolesV = new Vector(java.util.Arrays.asList(roles)) ;
	if(rolesV.size() == 0)
	    {
		String header = "Roles error" ;
		String msg = "Ingen roll var vald." + "<BR>";
		this.log("Error in checking roles") ;
		AdminError err = new AdminError(req,res,header, msg) ;
		return null;
	    }
	//this.log("Roles:"+ rolesV.toString()) ;
	return rolesV ;

    } // End getRolesParameters


    public void log( String str)
    {
	super.log(str) ;
	System.out.println("AddNewUser: " + str ) ;
    }

    /**
       Returns to the adminUsers meny
    */

    public void goAdminUsers(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException
    {
	this.removeSessionParams(req);
	res.sendRedirect("AdminUser") ;
    }

    /**
       Returns a String, containing the userID in the request object.If something failes,
       a error page will be generated and null will be returned.
    */

    public String getCurrentUserId(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException
    {

	// Lets get the userId from the request Object.
	String userId = req.getParameter("user_Id") ;
	if (userId == null)
	    {
		userId = req.getParameter("CURR_USER_ID") ;
	    }

	if (userId == null )
	    {
		String header = "AdminUserProps error. " ;
		String msg = "No user_id was available." + "<BR>";
		this.log(header + msg) ;
		AdminError err = new AdminError(req,res,header, msg) ;
		return null;
	    }
	else
	    this.log("AnvändarId=" + userId) ;
	return userId ;

    } // End getCurrentUserId


    /**
       Service method. Sends the user to the post method
    **/

    public void service (HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException
    {

	String action = req.getMethod() ;
	// log("Action:" + action) ;
	this.doPost(req,res) ;
    }


    /**
       Collects the parameters from the request object
    **/

    public Properties getParameters( HttpServletRequest req) throws ServletException, IOException
    {

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
	String user_type=(req.getParameter("user_type")==null) ? "" : (req.getParameter("user_type")) ;
	String active = (req.getParameter("active")==null) ? "0" : (req.getParameter("active")) ;
	String language = (req.getParameter("lang_id")==null) ? "1" : (req.getParameter("lang_id")) ;

	// Lets fix those fiels which arent mandatory
	if (req.getParameter("SAVE_USER") != null)
	    {
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
	userInfo.setProperty("user_type", user_type) ;
	userInfo.setProperty("active", active) ;
	userInfo.setProperty("lang_id", language) ;

	return userInfo ;
    }

    /**
       Returns a Properties, containing the user information from the html page. if Something
       failes, a error page will be generated and null will be returned.
    */

    public Properties validateParameters(Properties aPropObj, HttpServletRequest req,
					 HttpServletResponse res) throws ServletException, IOException
    {

	//	Properties params = this.getParameters(req) ;
	if(checkParameters(aPropObj) == false)
	    {
		String header = "Checkparameters error" ;
		String msg = "Samtliga fält var inte korrekt ifyllda." + "<BR>";
		this.log("Error in checkingparameters") ;
		AdminError err = new AdminError(req,res,header, msg) ;
		return null;
	    }
	return aPropObj ;

    } // end checkParameters
}
