import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import imcode.external.diverse.*;
import imcode.external.chat.*;

import imcode.util.* ;
import imcode.server.* ;

public class ChatLogin extends ChatBase {
    private final static String CVS_REV = "$Revision$" ;
    private final static String CVS_DATE = "$Date$" ;

    String LOGIN_HTML = "CLogin4.htm" ;	   // The login page
    String LOGIN_ERROR_HTML = "Chat_Error.htm";
    String ADD_USER_TEMPLATE = "Chat_Add_User.htm";

    public void doGet(HttpServletRequest req, HttpServletResponse res)throws ServletException, IOException{

	// Lets validate the session, e.g has the user logged in to Janus?
	if (super.checkSession(req,res) == false)	return ;
	HttpSession session = req.getSession(true);

	//lets get the ServletContext
	ServletContext myContext = getServletContext();

	// Lets get the standard parameters and validate them
	Properties params = super.getSessionParameters(req) ;
	if (super.checkParameters(req, res, params) == false) return ;

	// Lets get the user object
	imcode.server.user.UserDomainObject user = super.getUserObj(req,res) ;
	if(user == null) return ;

	String metaId = params.getProperty("META_ID");
	int meta_id = Integer.parseInt( metaId );
	if ( !isUserAuthorized( req, res, meta_id, user ) )	{
	    log("user not Authorized");
	    return;
	}

	//ok lets get the chat
	imcode.external.chat.Chat myChat = (imcode.external.chat.Chat)myContext.getAttribute("theChat"+metaId);

	// Lets get serverinformation

	String loginType = req.getParameter("login_type")==null?"":req.getParameter("login_type");

	if ( myChat == null ){
	    log("OBS m廛te skapa en ny Chat");
	    myChat = createChat(req, user, meta_id);
	    myContext.setAttribute("theChat"+metaId,myChat);
	    session.setAttribute("myChat",myChat);
	}

	if (session.getAttribute("theChatMember")!= null) {
	    res.sendRedirect("ChatViewer");
	    return;
	}

	//**** sets up the different loginpages  ****

	String userIdStr = ""+user.getUserId();
	int userIdInt = Integer.parseInt(userIdStr);

	//lets get the authorization types for this chat
	Vector rolV = myChat.getAuthorizations();
	//String[] roles = new String[3];
	//ok lets setup the booleans for the loginpage
	boolean loggedOnOk = false;
	boolean aliasBol = false;
	boolean imCmsRegBol = false;
	boolean selfRegBol = false;
	//sets up the booleans
	//Vector rolV = super.convert2Vector(roles);
	aliasBol	= rolV.contains("1");
	imCmsRegBol = rolV.contains("3");
	if (imCmsRegBol){
	    selfRegBol = rolV.contains("2");
	}

	//ok lets se if the user realy has loged in or if its a "User"	obs 2 = Conferense users
	if(userIdInt != 98){
	    loggedOnOk = true;
	}

	if (aliasBol && !imCmsRegBol){//only alias login
	    //log("alt 1");
	    LOGIN_HTML = "CLogin1.htm";
	}else if ( !aliasBol && imCmsRegBol ){//only registred imcms users
	    if(loggedOnOk){//ok the user has loged in so lets fill in he's name
		//log("alt 2");
		LOGIN_HTML = "CLogin2.htm";
	    }else{
		//log("alt 3");
		LOGIN_HTML = "CLogin3.htm";
	    }
	}else{
	    if (loggedOnOk)	{
		//log("alt 5");
		LOGIN_HTML = "CLogin5.htm" ;
	    }else{
		//log("alt 4");
		LOGIN_HTML = "CLogin4.htm" ;
	    }

	}

	//		LOGIN_HTML = "CLogin3.htm";
	String selfRegLink = "";
	if(selfRegBol){
	    if (!loggedOnOk){
		selfRegLink="<a href=\"#SERVLET_URL#ChatLogin?login_type=ADD_USER\">Registrera dig!</a>";
	    }
	}

	//obs m廛tefixas senare vilken mall som ska visas

	//*** end different login pages ****

	// ******** ADD USER PAGE *********
	// Lets generate the adduser page
	if(loginType.equalsIgnoreCase("ADD_USER")){

	    // Lets build the Responsepage to the loginpage
	    VariableManager vm = new VariableManager() ;
	    Vector userInfoV = new Vector(20) ; // a vector, bigger than the amount fields
	    vm = this.addUserInfo(vm,userInfoV) ;
	    vm.addProperty("SERVLET_URL", "") ;
	    //	sendHtml(req,res,vm, ADD_USER_TEMPLATE) ;
	    sendHtml(req,res,new Vector(), ADD_USER_TEMPLATE,null) ;
	    return ;
	}//end ADD USER PAGE


	// ********** LOGIN PAGE *********
	// Lets build the Responsepage to the loginpage

	//get chatname
	String chatName = myChat.getChatName();

	//Get all the rooms for the intended chat
	Vector roomsV = myChat.getAllChatGroupsIdAndNameV();

	//get the users username
	String userName = user.getLoginName();
	Vector tags = new Vector();
	tags.add("#CHAT_SELFREG_LINK#");	tags.add(selfRegLink);
	tags.add("#userName#");			tags.add(userName);
	tags.add("#chatName#");				tags.add(chatName);
	tags.add("#rooms#");				tags.add( createOptionCode("", roomsV) ) ;
	tags.add("#IMAGE_URL#");			tags.add(this.getExternalImageFolder( req, res ) );
	sendHtml(req,res,tags, LOGIN_HTML,null) ;

	//log("end doGet");
	return ;

    } // End doGet



    public void doPost(HttpServletRequest req, HttpServletResponse res)	throws ServletException, IOException{

	IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface(req) ;
	IMCPoolInterface chatref = IMCServiceRMI.getChatIMCPoolInterface(req) ;

	// Lets validate the session, e.g has the user logged in to Janus?
	if (super.checkSession(req,res) == false)	return ;
	HttpSession session = req.getSession(true);

	// Lets get the standard parameters and validate them
	Properties params = super.getSessionParameters(req) ;
	if (super.checkParameters(req, res, params) == false) return ;

	// Lets get the user object
	imcode.server.user.UserDomainObject user = super.getUserObj(req,res) ;
	if(user == null) return ;

	String metaId =	 params.getProperty("META_ID");
	int meta_id = Integer.parseInt( metaId );

	if ( !isUserAuthorized( req, res, meta_id, user ) )	{
	    return;
	}

	String loginType = (req.getParameter("login_type")==null) ? "" : (req.getParameter("login_type")) ;
	log("Logintype 酺 nu: " + loginType) ;

	// ************* ADD USER TO imCMS db (SELFREGISTRATION) **************
	//ok the usecase is to add a new user to imcms db so lets do it
	if(req.getParameter("saveNewUser")!= null )	{
	    log("Now runs add_user") ;

	    // Lets get the parameters from html page and validate them
	    Properties newUserParams = this.getNewUserParameters(req) ;

	    // Properties userParams = this.getNewUserParameters2(req) ;
	    if( this.checkUserParameters(newUserParams) == false){
		log("checkUserParameters(newUserParams) == false so return");
		return ;
	    }

	    // Lets validate the passwords. Error message will be generated in method
	    if( AdminUserProps.verifyPassword(newUserParams,req,res) == false) return ;

	    // Lets validate the phonenbr. Error message will be generated in method
      boolean result = true;
      try {
         String[] arr = {newUserParams.getProperty( "country_code" ), newUserParams.getProperty( "area_code" ), newUserParams.getProperty( "local_code" )};

         for( int i = 0; i < arr.length; i++ ) {
            Integer.parseInt( arr[i] );
         }

      } catch( NumberFormatException e ) {
         // log(e.getMessage()) ;
         AdminError2 err = new AdminError2( req, res, "", 63 );
         result =  false;
      } catch( NullPointerException e ) {
         // log(e.getMessage()) ;
         AdminError2 err = new AdminError2( req, res, "", 63 );
         result =  false;
      }
      if( result == false) return ;

	    // Lets validate the userparameters before the sql
	    newUserParams = super.verifyForSql(newUserParams) ;
	    String userName = newUserParams.getProperty("login_name") ;

	    // Lets check that the new username doesnt exists already in db
	    String userNameExists[] = imcref.sqlProcedure("FindUserName '" + userName + "'") ;

	    if(userNameExists != null )	{
		if(userNameExists.length > 0 )	{
		    String header = "ChatLogin servlet." ;
		    ChatError err = new ChatError(req,res,header,56, LOGIN_ERROR_HTML ) ;
		    return ;
		}
	    }

	    // Lets get the new UserId for the new user
	    String newUserId = imcref.sqlProcedureStr("GetHighestUserId" ) ;
	    if(newUserId == null){
		log("newUserId == null so return");
		return ;
	    }

	    // Lets build the users information into a string and add it to db

	    // Lets get the language id the user will have, or set the lang_id to 1
	    // as default
	    if( newUserParams.getProperty("lang_id") == null )
		newUserParams.setProperty("lang_id", "1") ;

	    newUserParams.setProperty("user_id", newUserId) ;
	    String[] procParams =  AdminUserProps.extractUpdateUserSprocParametersFromProperties(newUserParams) ;

      // Lets build the users information into a string and add it to db
      imcref.sqlUpdateProcedure("AddNewUser",  procParams) ;

      // Lets add a new phone number
	    StringBuffer phoneStr = new StringBuffer() ;
	    phoneStr.append("phoneNbrAdd " + newUserId) ;
	    phoneStr.append(", '" + newUserParams.getProperty("country_code") ) ;
	    phoneStr.append("', '" + newUserParams.getProperty("area_code") ) ;
	    phoneStr.append("', '" + newUserParams.getProperty("local_code") + "'" ) ;

	    imcref.sqlUpdateProcedure(phoneStr.toString()) ;

	    // Ok, lets get the roles the user will get when he is selfregistering  and
	    // add those roles to the user
	    String sqlSproc = "C_SelfRegRoles_GetAll2 " + params.getProperty("META_ID") ;
	    String sqlAnswer[] = chatref.sqlProcedure( sqlSproc ) ;

	    // First, get the langprefix
	    String langPrefix = user.getLangPrefix() ;

	    if(sqlAnswer != null )	{
		for(int i = 0; i<sqlAnswer.length; i+=2){
		    String aRoleId = sqlAnswer[i].toString() ;
		    // Lets check that the role id is still valid to use against
		    // the host system

		    String sqlCheckSproc = "RoleCheckConferenceAllowed '" + langPrefix +"', "+ aRoleId ;
		    // log("RoleCheckSQL: " + sqlCheckSproc) ;
		    String found = imcref.sqlProcedureStr(sqlCheckSproc) ;
		    //log("FoundRoleCheckSQL: " + found) ;
		    if (found != null )	{
			String addRoleSql = "AddUserRole " + newUserId + ", " + aRoleId ;
			//log("AddRoleSQL: " + addRoleSql) ;
			imcref.sqlUpdateProcedure(addRoleSql) ;
		    }
		}
	    }

	    // Ok, Lets add the users roles into db, first get the role his in the system with
	    String userId = ""+user.getUserId() ;

	    String usersRoles[] = imcref.sqlProcedure("GetUserRolesIDs " + userId ) ;
	    // log("SQL fr嶓a:" + "GetUserRolesIDs " + userId) ;
	    if(usersRoles != null )	{

		for(int i = 0; i<usersRoles.length; i+=2){
		    // Late change, fix so the superadminrole wont be copied to the new user
		    if(!usersRoles[i].toString().equals("1")){
			String addRoleSql = "AddUserRole " + newUserId + ", " + usersRoles[i].toString() ;
			imcref.sqlUpdateProcedure(addRoleSql) ;
		    }else{
			// log("Superadmin rollen kom inte med!") ;
		    }
		}
	    }else{  // nothing came back from getUserRoles
		String header = "ConfLogin servlet." ;
		ChatError err = new ChatError(req,res,header,58 ) ;
		log(header + err.getErrorMsg()) ;
		return ;
	    }
	    //h酺 ska vi kanske se till att logga in anv鄚daren i systemet
	    String paramStr = MetaInfo.passMeta(params) ;
	    //log("params: "+paramStr);

	    //lets send the user to chatloginpage
	    res.sendRedirect("ChatLogin?"+paramStr);
	    return ;
	} //end saveNewUser

	//get alias must bee here because if we logs into imcms then we have to cleare this
	String chatAlias = req.getParameter("alias").trim();
	log("chatAlias: " + chatAlias);

	//*********logs a user into imcms *****
	if(req.getParameter("loginToImCms")!=null){
	    //ok lets se if there is any user whith this id and pw
	    log("Ok, nu f顤s闥er vi verifiera logga in!") ;
	    Properties lparams = this.getLoginParams(req ,res) ;

	    // Ok, Lets check what the user has sent us. Lets verify the fields the user
	    // have had to write freetext in to verify that the sql questions wont go mad.
	    if (this.verifyLoginParams(req, res, lparams) == false) return ;
	    lparams = super.verifyForSql(lparams) ;
	    String userName = lparams.getProperty("LOGIN_NAME") ;
	    String password = lparams.getProperty("PASSWORD") ;

	    // Validate loginparams against DB
	    String sqlQ = "GetUserIdFromName '" + userName + "', '" + password + "'" ;
	    String userId = imcref.sqlProcedureStr( sqlQ ) ;
	    //log("Anv鄚darens id var: " + userId) ;

	    // Lets check that we found the user. Otherwise send unvailid username password
	    if( userId == null ) {
		String header = "ChatLogin servlet." ;
		ChatError err = new ChatError(req,res,header,50, LOGIN_ERROR_HTML ) ;
		log(header + err.getErrorMsg()) ;
		return ;
	    }

	    //ok lets create a user obj and put it into the session

	    imcode.server.user.UserDomainObject oldUser = user;
	    user = null;
	    user = allowUser( userName, password, imcref );
	    user.put("history",oldUser.get("history"));

	    session.setAttribute("logon.isDone", user );
	    chatAlias = null;

	}//end loginToImCms

	// ************* LOG A USER INTO A CHAT **************
	//ok the usecase is to login a user to a chatroom

	log("req.getParameter(login) = "+ req.getParameter("login"));

	if (req.getParameter("loginAlias") != null ||req.getParameter("loginUserName") != null)	{
	    log("ok lets get the chat");
	    //check if the intended chat already exists ServletContext
	    ServletContext  myContext = getServletContext();
	    Chat theChat =  (Chat) myContext.getAttribute("theChat"+metaId);

	    if ( theChat == null ){
		theChat = createChat(req, user, meta_id);
		myContext.setAttribute("theChat"+metaId,theChat);
		session.setAttribute("myChat",theChat);
	    }

	    //ok lets create the chatmember obj
	    imcode.external.chat.ChatMember myMember = theChat.createChatMember();
	    if (req.getParameter("loginAlias") != null)	{
		//ok we need to  see if the name alreadi is in use, and if so send error
		if (theChat.hasMemberName("Alias: "+chatAlias)) {
		    //	System.out.println("中中中中中中中中夕n use fix");
		}else {
		    myMember.setName("Alias: "+chatAlias);
		}
	    }else{
		myMember.setName(user.getLoginName());
	    }
	    myMember.setIPNr(req.getRemoteHost());
	    //lets get the rooom
	    String currentRoomId = req.getParameter("rooms");
	    int roomNr;
	    try{
		roomNr = Integer.parseInt(currentRoomId);
	    }catch(NumberFormatException nfe){
		log("the room was null so return");
		return;
	    }

	    ChatGroup myGroup = theChat.getChatGroup(roomNr);
	    myGroup.addNewGroupMember(myMember);

	    //ok lets add a enter msg
	    String libName = super.getTemplateLibName(chatref,theChat.getChatId()+"");
	    String enter_msg = imcref.parseExternalDoc(new Vector(), "enter_msg.html",user.getLangPrefix(), "103", libName);
	    String theDateTime = (super.getDateToday() +" : "+ super.getTimeNow());
	    ChatMsg newEnterMsg = new ChatMsg(enter_msg, "", CHAT_ENTER_LEAVE_INT,
					      CHAT_ENTER_LEAVE_INT,"", myMember.getName(), myMember.getUserId(), theDateTime.trim());
	    myGroup.addNewMsg(newEnterMsg);
	    log(myMember.toString());
	    session.setAttribute("theChatMember",myMember);
	    super.prepareChatBoardSettings(myMember, req, false);

	    //lets redirect to ChatViewer
	    String url = "ChatViewer"  ;
	    res.sendRedirect(url) ;
	    return ;
	}
	log("end doPost");
	return;
    }//end doPost






    /**
       Collects the registration parameters from the request object
    **/
    private Properties getNewUserParameters( HttpServletRequest req) throws ServletException, IOException{

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
	String email = (req.getParameter("email")==null) ? "" : (req.getParameter("email")) ;

	String cCode = (req.getParameter("country_code")==null) ? "" : (req.getParameter("country_code")) ;
	String aCode = (req.getParameter("area_code")==null) ? "" : (req.getParameter("area_code")) ;
	String lCode = (req.getParameter("local_code")==null) ? "" : (req.getParameter("local_code")) ;

	String user_type = (req.getParameter("user_type")==null) ? "3" : (req.getParameter("user_type")) ;
	String active = (req.getParameter("active")==null) ? "1" : (req.getParameter("active")) ;

	// Lets fix those fiels which arent mandatory
	// Lets fix those fiels which arent mandatory
	if( title.trim().equals("")) title = "--" ;
	if( company.trim().equals("")) company = "--" ;
	if( address.trim().equals("")) address = "--" ;
	if( city.trim().equals("")) city = "--" ;
	if( zip.trim().equals("")) zip = "--" ;
	if( country.trim().equals("")) country = "--" ;
	if( country_council.trim().equals("")) country_council = "--" ;
	if( email.trim().equals("")) email = "--" ;

	if( cCode.trim().equals("")) cCode = "00" ;
	if( aCode.trim().equals("")) aCode = "00" ;
	if( lCode.trim().equals("")) lCode = "00" ;

	userInfo.setProperty("login_name", login_name.trim()) ;
	userInfo.setProperty("password1", password1.trim()) ;
	userInfo.setProperty("password2", password2.trim()) ;
	userInfo.setProperty("first_name", first_name.trim()) ;
	userInfo.setProperty("last_name", last_name.trim()) ;
	userInfo.setProperty("title", title.trim()) ;
	userInfo.setProperty("company", company.trim()) ;

	userInfo.setProperty("address", address.trim()) ;
	userInfo.setProperty("city", city.trim()) ;
	userInfo.setProperty("zip", zip.trim()) ;
	userInfo.setProperty("country", country.trim()) ;
	userInfo.setProperty("country_council", country_council.trim()) ;
	userInfo.setProperty("email", email.trim()) ;

	userInfo.setProperty("country_code", cCode.trim()) ;
	userInfo.setProperty("area_code", aCode.trim()) ;
	userInfo.setProperty("local_code", lCode.trim()) ;

	userInfo.setProperty("user_type", user_type.trim()) ;
	userInfo.setProperty("active", active.trim()) ;

	// this.log("UserInfo:" + userInfo.toString()) ;
	return userInfo ;
    }//end new getNewUserParameters


    /**
       Adds the userInformation to the htmlPage. if an empty vector is sent as argument
       then an empty one will be created
    */
    private VariableManager addUserInfo(VariableManager vm, Vector v)
    {
	// Here is the order in the vector
	// [3, Rickard, tynne, Rickard, Larsson, programmerare, imcode,  Drakarve, Havdhem, 620 11, Sweden, Gotland,
	// rickard@imcode.com, 0, 1001, 0, 1]
	//(v.get(1)==null) ? "" : (req.getParameter("password1")) ;

	if(v.size() == 0 || v.size() < 14 )
	    for(int i = v.size(); i < 13; i++)
		v.add(i, "") ;


	vm.addProperty("LOGIN_NAME", v.get(1).toString()) ;
	vm.addProperty("PWD1", v.get(2).toString()) ;
	vm.addProperty("PWD2", v.get(2).toString()) ;
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
	return vm ;
    }


    /**
       The verifyLogin method verifies the login params from the requestobject
    **/

    private boolean verifyLoginParams(HttpServletRequest req, HttpServletResponse res,
				      Properties loginP) throws ServletException, IOException {

	// Ok, lets check the parameters
	if( MetaInfo.checkParameters(loginP) == false) {
	    // if(super.checkParameters(req, res, loginP) == false) {
	    String header = "ChatLogin servlet. " ;
	    ChatError err = new ChatError(req,res,header,50) ;
	    log(header + err.getErrorMsg()) ;
	    return false ;
	}
	return true ;

    } // verifyLoginParams

    /**
       The getLoginParams method gets the login params from the requstobject
    **/

    private Properties getLoginParams(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {
	Properties login = new Properties() ;
	// Lets get the parameters we know we are supposed to get from the request object
	String login_name = (req.getParameter("login_name")==null) ? "" : (req.getParameter("login_name")) ;
	String password1 = (req.getParameter("password")==null) ? "" : (req.getParameter("password")) ;
	login.setProperty("LOGIN_NAME", login_name.trim()) ;
	login.setProperty("PASSWORD", password1.trim()) ;
	return login ;
    }


    /**
       CheckUserparameters. Loops through the parameters and checks that they have
       been set to something
    */
    private boolean checkUserParameters(Properties aPropObj)
    {
	// Ok, lets check that the user has typed anything in all the fields
	Enumeration enumValues = aPropObj.elements() ;
	Enumeration enumKeys = aPropObj.keys() ;
	while((enumValues.hasMoreElements() && enumKeys.hasMoreElements()))
	    {
		Object oKeys = (enumKeys.nextElement()) ;
		Object oValue = (enumValues.nextElement()) ;
		String theVal = oValue.toString() ;
		if(theVal.equals(""))
		    return false ;
	    }

	return true ;
    } // checkUserParameters

    /**
       Test if user exist in the database
    */
    private imcode.server.user.UserDomainObject allowUser( String user_name, String passwd, IMCServiceInterface imcref ) throws IOException {
	return imcref.verifyUser(user_name,passwd);
    }

    /**
       Log function, will work for both servletexec and Apache
    **/

    public void log( String str)
    {
	super.log("ChatLogin: " + str) ;
	//System.out.println("ChatLogin: " + str ) ;
    }

} // End class
