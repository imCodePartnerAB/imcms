/*
 *
 * @(#)Chat.java
 *
 *
 * Copyright (c)
 *
*/

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import imcode.external.diverse.* ;
import imcode.util.*;
import imcode.util.IMCServiceRMI;
import imcode.util.Parser;

import imcode.external.chat.*;

/**
 * superclas for chat servlets.
 *
 * Html template in use:
 * Chat_Admin_Button.htm????
 *
 * Html parstags in use:
 * #ADMIN_TYPE#???
 * #TARGET#???
 *
 * stored procedures in use:
 * -
 *
 *
*/


public class ChatBase extends HttpServlet {

	private final static String CVS_REV = "$Revision$" ;
	private final static String CVS_DATE = "$Date$" ;
	private final static String ADMIN_BUTTON_TEMPLATE = "Chat_Admin_Button.htm";
	private final static String UNADMIN_BUTTON_TEMPLATE = "Chat_Unadmin_Button.htm";
	
	public final static int CHAT_ALLA_INT = 0;
	public final static int CHAT_ENTER_LEAVE_INT = -32;
	public final static String LEAVE_MSG = "lämnar rummet";
	public final static String ENTER_MSG = "stiger in";


	/**
	*	Does the things that has to bee done only ones
	*/
	public void init(ServletConfig config)
	throws ServletException
	{
		super.init(config);
		//log("init");
	}

	/**
	Collects the parameters from the request object
	**/

	protected Properties getNewChatParameters( HttpServletRequest req) throws ServletException, IOException
	{
		Properties chatP = new Properties();

		String chatName = (req.getParameter("chatName")==null) ? "" : req.getParameter("chatName");
		String permission = (req.getParameter("permission")==null) ? "3" : (req.getParameter("permission"));
		String updateTime = ( req.getParameter("updateTime")==null ) ? "20" : (req.getParameter("updateTime"));		
		String reload = (req.getParameter("reload")==null ) ? "2" :(req.getParameter("reload"));
		String inOut = (req.getParameter("inOut")==null ) ? "2" :(req.getParameter("inOut"));
		String privat = (req.getParameter("private")==null ) ? "2" :(req.getParameter("private"));
		String publik = (req.getParameter("public")==null ) ? "2" :(req.getParameter("public"));
		String dateTime = (req.getParameter("dateTime")==null ) ? "2" :(req.getParameter("dateTime"));
		String font = (req.getParameter("font")==null ) ? "2" :(req.getParameter("font"));

		chatP.setProperty("chatName", chatName.trim());
		chatP.setProperty("permission",permission.trim());
		chatP.setProperty("updateTime",updateTime);
		chatP.setProperty("reload",reload.trim());
		chatP.setProperty("inOut",inOut.trim());
		chatP.setProperty("privat",privat.trim());
		chatP.setProperty("publik",publik.trim());
		chatP.setProperty("dateTime",dateTime.trim());
		chatP.setProperty("font",font.trim());

		return chatP ;
	}
	
	public String getParamString(Properties propp)
	{
		StringBuffer buff = new StringBuffer("");
		Enumeration enum = propp.propertyNames();
		while (enum.hasMoreElements())
		{
			String st= (String) enum.nextElement();
			
			buff.append(st+"="+propp.getProperty(st,""));
		}
		return buff.toString();
	}

	
	/**
	Returns the metaId from a request object, if not found, we will
	get the one from our session object. If still not found then null is returned.
	*/

	public String getMetaId (HttpServletRequest req)
	throws ServletException, IOException
	{

		String metaId = req.getParameter("meta_id") ;
		if( metaId == null )
		{
			HttpSession session = req.getSession(false) ;
			if (session != null)
			{
				metaId =	(String) session.getValue("Chat.meta_id") ;
			}
		}
		if( metaId == null)
		{
			log("No meta_id could be found! Error in Chat.class") ;
			return null ;
		}
		return metaId ;
	}


	/**
	Returns the ForumId from a request object, if not found, we will
	get the one from our session object. If still not found then null is returned.

	*/
	public String getForumId (HttpServletRequest req)
	throws ServletException, IOException
	{

		String forumId = req.getParameter("forum_id") ;
		if( forumId == null )
		{
			HttpSession session = req.getSession(false) ;
			if (session != null)
			{
				forumId =	(String) session.getValue("Chat.forum_id") ;
			}
		}
		if( forumId == null)
		{
			log("No forum_id could be found! Error in Chat.class") ;
			return null ;
		}
		return forumId ;
	}



	/**
	Collects all information from the user object. To get information from
	the userobject.
	* userObject.getString(String theKey)
	* userObject.getInt(String theKey)
	* userObject.getBoolean(String theKey)

	**/

	public Properties getUserParameters(imcode.server.User user)
	{
		Properties userParams= new Properties() ;
		userParams.setProperty("USER_ID", user.getString("user_id")) ;
		userParams.setProperty("LOGIN_NAME", user.getString("login_name")) ;
		userParams.setProperty("LOGIN_PASSWORD", user.getString("login_password")) ;
		userParams.setProperty("FIRST_NAME", user.getString("first_name")) ;
		userParams.setProperty("LAST_NAME", user.getString("last_name")) ;
		userParams.setProperty("ADDRESS", user.getString("address")) ;
		userParams.setProperty("CITY", user.getString("city")) ;
		userParams.setProperty("ZIP", user.getString("zip")) ;
		userParams.setProperty("COUNTRY", user.getString("country")) ;
		userParams.setProperty("COUNTY_COUNCIL", user.getString("county_council")) ;
		userParams.setProperty("EMAIL", user.getString("email")) ;
		userParams.setProperty("ADMIN_MODE", user.getString("admin_mode")) ;
		userParams.setProperty("LAST_PAGE", user.getString("last_page")) ;
		userParams.setProperty("ARCHIVE_MODE", user.getString("archive_mode")) ;
		userParams.setProperty("USER_TYPE", user.getString("user_type")) ;
		userParams.setProperty("LOGIN_TYPE", user.getLoginType()) ;

		//userParams.setProperty("LANG_ID", user.getString("lang_id")) ;

		// log("GetUserParameters: " + userParams.toString()) ;
		return userParams ;
	}


	protected synchronized Properties getChatParams(String[] propps)
	{
		Properties chatP = new Properties();
		//chatP.setProperty("chatName", chatName.trim());
		//chatP.setProperty("permission",permission.trim());
		chatP.setProperty("updateTime",propps[1]);
		chatP.setProperty("reload",propps[2]);
		chatP.setProperty("inOut",propps[3]);
		chatP.setProperty("privat",propps[4]);
		chatP.setProperty("publik",propps[5]);
		chatP.setProperty("dateTime",propps[6]);
		chatP.setProperty("font",propps[7]);
		return chatP;		
	}

	/**
	Returns an user object. If an error occurs, an errorpage will be generated.
	*/

	protected imcode.server.User getUserObj(HttpServletRequest req,
		HttpServletResponse res) throws ServletException, IOException
	{

		if(checkSession(req,res) == true)
		{

			// Get the session
			HttpSession session = req.getSession(true);
			// Does the session indicate this user already logged in?
			Object done = session.getValue("logon.isDone");  // marker object
			imcode.server.User user = (imcode.server.User) done ;

			return user ;
		}
		else
		{
			String header = "Chat servlet." ;
			ChatError err = new ChatError(req,res,header, 2) ;
			log(err.getErrorMsg()) ;
			return null ;
		}
	}

	// *************** LETS HANDLE THE SESSION META PARAMETERS *********************


	/**
	Collects the standard parameters from the session object
	**/

	public Properties getSessionParameters( HttpServletRequest req)
	throws ServletException, IOException
	{


		// Get the session
		HttpSession session = req.getSession(true);
		String metaId = (	(String) session.getValue("Chat.meta_id")==null) ? "" : ((String) session.getValue("Chat.meta_id")) ;
		String parentId = (	(String) session.getValue("Chat.parent_meta_id")==null) ? "" : ((String) session.getValue("Chat.parent_meta_id")) ;
		String cookieId = (	(String) session.getValue("Chat.cookie_id")==null) ? "" : ((String) session.getValue("Chat.cookie_id")) ;

		Properties reqParams= new Properties() ;
		reqParams.setProperty("META_ID", metaId) ;
		reqParams.setProperty("PARENT_META_ID", parentId) ;
		reqParams.setProperty("COOKIE_ID", cookieId) ;

		return reqParams ;
	}


	/**
	Collects the EXTENDED parameters from the session object. As extended paramters are we
	counting:

	Chat.forum_id
	Chat.discussion_id

	@Parameter: Properties params, if a properties object is passed, we will fill the
	object with the extended paramters, otherwise we will create one.
	**/

	public Properties getExtSessionParameters( HttpServletRequest req, Properties params)
	throws ServletException, IOException
	{

		// Get the session
		HttpSession session = req.getSession(true);
		String forumId = (	(String) session.getValue("Chat.forum_id")==null) ? "" : ((String) session.getValue("Chat.forum_id")) ;
		String discId = (	(String) session.getValue("Chat.disc_id")==null) ? "" : ((String) session.getValue("Chat.disc_id")) ;

		if( params == null)
			params = new Properties() ;
		params.setProperty("FORUM_ID", forumId) ;
		params.setProperty("DISC_ID", discId) ;
		return params ;
	}



	/**
	Verifies that the user has logged in. If he hasnt, he will be redirected to
	an url which we get from a init file name conference.
	*/

	protected boolean checkSession(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException
	{

		// Get the session
		HttpSession session = req.getSession(true);
		// Does the session indicate this user already logged in?
		Object done = session.getValue("logon.isDone");  // marker object
		imcode.server.User user = (imcode.server.User) done ;

		// Lets get serverinformation
		String host = req.getHeader("Host") ;
		String imcServer = Utility.getDomainPref("userserver",host) ;
		String ChatPoolServer = Utility.getDomainPref("chat_server",host) ;

		if (done == null)
		{
			// No logon.isDone means he hasn't logged in.
			// Save the request URL as the true target and redirect to the login page.
			session.putValue("login.target", HttpUtils.getRequestURL(req).toString());
			String serverName = MetaInfo.getServerName(req) ;
			String startUrl = RmiConf.getLoginUrl(host) ;
			res.sendRedirect(serverName + startUrl) ;

			//this.log("Server: " + serverName) ;
			//this.log("startUrl: " + startUrl) ;
			//this.log("A user had not logged in. He was sent to " + serverName + startUrl) ;
			return false;
		}
		return true ;
	}

	// *************** LETS HANDLE THE STANDARD META PARAMETERS *********************


	/**
	Collects the parameters from the request object
	**/

	public Properties getParameters( HttpServletRequest req)
	throws ServletException, IOException
	{

		MetaInfo mInfo = new MetaInfo() ;
		return mInfo.getParameters(req) ;
	}

	/**
	check the meta Parameters
	*/

	public boolean checkParameters(HttpServletRequest req,HttpServletResponse res)
	throws ServletException, IOException
	{
		MetaInfo mInfo = new MetaInfo() ;
		Properties params = mInfo.getParameters(req) ;
		if( mInfo.checkParameters(params) == false)
		{
			log("checkParameters had a null value") ;
			return false;
		}
		return true ;
	}

	public boolean checkParameters(HttpServletRequest req,HttpServletResponse res,
		Properties params) throws ServletException, IOException
	{

		MetaInfo mInfo = new MetaInfo() ;
		if( mInfo.checkParameters(params) == false)
		{
			
			log("checkParameters had a null value") ;
			return false;
		}
		return true ;
	}

	// *************************** END OF META PARAMETER FUNCTIONS *****************


	// *************************** ADMIN RIGHTS FUNCTIONS **************************

	protected boolean getAdminRights(String server, String metaId, imcode.server.User user)
	{

	
		try {
			return userHasAdminRights( server, Integer.parseInt( metaId ), user );
		} catch ( IOException e )
		{
			log("GetAdminRights failed!!!") ;
			return false ;
		}

	} // End GetAdminRights

	/**
	CheckAdminRights, returns true if the user is an superadmin. Only an superadmin
	is allowed to create new users
	False if the user isn't an administrator.
	1 = administrator
	0 = superadministrator
	*/

	protected boolean checkAdminRights(String server, imcode.server.User user)
	{


		// Lets verify that the user who tries to add a new user is an SUPERADMIN
		RmiLayer imc = new RmiLayer(user) ;
		int currUser_id = user.getInt("user_id") ;
		String checkAdminSql = "CheckAdminRights " + currUser_id ;
		String[] roles = imc.execSqlProcedure(server, checkAdminSql) ;
		boolean returnValue = false;
		for(int i = 0 ; i< roles.length; i++ )
		{
			String aRole = roles[i] ;
			if(aRole.equalsIgnoreCase("0") )
				returnValue = true ;
		}
		return returnValue ;

	} // checkAdminRights

	/**
	CheckAdminRights, returns true if the user is an admin.
	False if the user isn't an administrator
	*/

	protected boolean checkAdminRights(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException
	{

		// Lets get serverinformation
		String host = req.getHeader("Host") ;
		String imcServer = Utility.getDomainPref("userserver",host) ;
		String ChatPoolServer = Utility.getDomainPref("chat_server",host) ;


		imcode.server.User user = getUserObj(req,res) ;
		if(user == null)
		{
			this.log("CheckadminRights: an error occured, getUserObj") ;
			return false ;
		}
		else
			return checkAdminRights(imcServer, user) ;
	}

	/**
	CheckAdminRights, returns true if the user is an admin.
	False if the user isn't an administrator
	*/

	protected boolean checkDocRights(String server, String meta_id, imcode.server.User user)
	throws ServletException, IOException
	{
		return RmiConf.checkDocRights(server, meta_id, user) ;
	}

	// *********************** GETEXTERNAL TEMPLATE FUNCTIONS *********************

	/**
	Gives the folder to the root external folder,Example /templates/se/102/
	*/

	public File getExternalTemplateRootFolder (HttpServletRequest req)
	throws ServletException, IOException
	{

		// Lets get serverinformation
		String host = req.getHeader("Host") ;
		String imcServer = Utility.getDomainPref("userserver",host) ;
		String ChatPoolServer = Utility.getDomainPref("chat_server",host) ;

		String metaId = this.getMetaId(req) ;
		if( metaId == null)
		{
			log("No meta_id could be found! Error in Chat.class") ;
			throw new IllegalArgumentException() ;
		}
		return MetaInfo.getExternalTemplateFolder(imcServer, metaId) ;

	}


	/**
	Gives the folder where All the html templates for a language are located.
	This method will call its helper method getTemplateLibName to get the
	name of the folder which contains the templates for a certain meta id
	*/

	public File getExternalTemplateFolder (HttpServletRequest req)
	throws ServletException, IOException
	{
		String externalTemplateLib = "" ;
		String metaId = this.getMetaId(req) ;
		if( metaId == null)
		{
			log("No meta_id could be found! Error in Chat.class") ;
			throw new IllegalArgumentException() ;
		}
		// Lets get serverinformation
		String host = req.getHeader("Host") ;
		String imcServer = Utility.getDomainPref("userserver",host) ;
		String confPoolServer = Utility.getDomainPref("chat_server",host) ;
		return new File( this.getExternalTemplateFolder(imcServer, metaId), this.getTemplateLibName(confPoolServer, metaId)) ;
		// return this.getExternalTemplateFolder(imcServer, metaId) ;
	}

	/**
	Gives the folder where All the html templates for a language are located.
	This method will call its helper method getTemplateLibName to get the
	name of the folder which contains the templates for a certain meta id
	*/

	public File getExternalTemplateFolder (String server, String metaId )
	throws ServletException, IOException
	{
		if( metaId == null)
		{
			log("No meta_id could be found! Error in Chat.class") ;
			throw new IllegalArgumentException() ;
		}
		return MetaInfo.getExternalTemplateFolder(server, metaId) ;
	}


	/**
	Returns the foldername where the templates are situated for a certain metaid.
	**/
	protected String getTemplateLibName(String server, String meta_id)
	throws ServletException, IOException
	{
		// RmiConf aRmiObj = new RmiConf() ;
		String sqlQ = "C_GetTemplateLib " + meta_id ;
		String libName = RmiConf.execSqlProcedureStr(server, sqlQ) ;
		if( libName == null)
		{
			libName = "original" ;
			//log(sqlQ + ": fungerar inte!") ;
		}
		libName += "/" ;
		return libName  ;

	} // End of getTemplateLibName




	/**
	Collects the parameters from the request object. This function will get all the possible
	parameters this servlet will be able to get. If a parameter wont be found, the session
	parameter will be used instead, or if no such parameter exist in the session object,
	a key with no value = "" will be used instead.
	Since this method is used. it means
	that this servlet will take more arguments than the standard ones.
	**/

	public Properties getRequestParameters( HttpServletRequest req)
	throws ServletException, IOException
	{

		Properties reqParams = new Properties() ;

		// Lets get our own variables. We will first look for the discussion_id
		//	 in the request object, if not found, we will get the one from our session object
		String confForumId = req.getParameter("forum_id") ;
		// log("Nytt ForumId är: " + confForumId) ;
		if( confForumId == null )
		{
			HttpSession session = req.getSession(false) ;
			if (session != null)
			{
				confForumId =	(String) session.getValue("Chat.forum_id") ;
			}
		}
		reqParams.setProperty("FORUM_ID", confForumId) ;
		return reqParams ;
	}



	//************************ END GETEXTERNAL TEMPLATE FUNCTIONS ***************

	/**
	SendHtml. Generates the html page to the browser. Uses the templatefolder
	by taking the metaid from the request object to determind the templatefolder.
	Will by default handle maximum 3 servletadresses.
	*/

	public void sendHtml (HttpServletRequest req, HttpServletResponse res,
		VariableManager vm, String htmlFile) throws ServletException, IOException
	{

		String metaId = this.getMetaId(req) ;
		if (metaId == null)
		{
			log("NO metaid could be found in the passed request object") ;
			String header = "Chat servlet. " ;
			ChatError err = new ChatError(req,res,header,5) ;
			return ;
		}

		// Lets get the TemplateFolder  and the foldername used for this certain metaid
		File templateLib = this.getExternalTemplateFolder(req) ;
		//String templateLib = this.getExternalTemplateFolder(imcServer, metaId) ;
		
		// Lets add 3 server hostadresses
		String servletPath = MetaInfo.getServletPath(req) ;

		// Lets get the path to the imagefolder.
		String imagePath = this.getExternalImageFolder(req, res) ;
		
		vm.addProperty("IMAGE_URL", imagePath);
		vm.addProperty("SERVLET_URL", servletPath);

		// log("Before HTmlgenerator: ") ;
		HtmlGenerator htmlObj = new HtmlGenerator(templateLib, htmlFile) ;
		String html = htmlObj.createHtmlString(vm,req) ;
		
		htmlObj.sendToBrowser(req,res,html) ;
		//log("after sendToBrowser: ") ;

	}

	/**
	Log function. Logs the message to the log file and console
	*/

	public void log(String msg)
	{
		if(msg == null)msg="the msg who come in to ChatBase.log was was null";
		super.log(""+msg) ;
		System.out.println( msg ) ;

	}

	/**
	Date function. Returns the current date and time in the swedish style
	*/


	public static String getDateToday()
	{
		java.util.Calendar cal = java.util.Calendar.getInstance() ;

		String year  = Integer.toString(cal.get(Calendar.YEAR)) ;
		int month = Integer.parseInt(Integer.toString(cal.get(Calendar.MONTH))) + 1;
		int day   = Integer.parseInt(Integer.toString(cal.get(Calendar.DAY_OF_MONTH))) ;
		int hour  = Integer.parseInt(Integer.toString(cal.get(Calendar.HOUR_OF_DAY))) ;
		int min   = Integer.parseInt(Integer.toString(cal.get(Calendar.MINUTE))) ;

		String dateToDay  = year ;
		dateToDay += "-" ;
		dateToDay += month < 10 ? "0" + Integer.toString(month) : Integer.toString(month) ;
		dateToDay += "-" ;
		dateToDay += day < 10 ? "0" + Integer.toString(day) : Integer.toString(day) ;

		return dateToDay ;
	}

	/**
	Date function. Returns the current time in the swedish style
	*/

	public static String getTimeNow()
	{
		java.util.Calendar cal = java.util.Calendar.getInstance() ;

		int hour  = Integer.parseInt(Integer.toString(cal.get(Calendar.HOUR_OF_DAY))) ;
		int min   = Integer.parseInt(Integer.toString(cal.get(Calendar.MINUTE))) ;
		int sec   = Integer.parseInt(Integer.toString(cal.get(Calendar.SECOND))) ;

		String timeNow  = "" ;
		timeNow += hour < 10 ? "0" + Integer.toString(hour) : Integer.toString(hour) ;
		timeNow += ":" ;
		timeNow += min < 10 ? "0" + Integer.toString(min) : Integer.toString(min) ;
		timeNow += ":" ;
		timeNow += sec < 10 ? "0" + Integer.toString(sec) : Integer.toString(sec) ;
		// timeNow += ".000" ;

		return timeNow ;
	}


	/**
	Converts array to vector
	*/

	public Vector convert2Vector(String[] arr)
	{
		Vector rolesV  = new Vector() ;
		for(int i = 0; i<arr.length; i++)
			rolesV.add(arr[i]) ;
		return rolesV ;
	}

	public synchronized Vector convert2Vector(String[][] arr)
	{
		Vector rolesV  = new Vector() ;
		for(int i = 0; i<arr.length; i++){
			for(int e=0;e<arr[i].length;e++){
				rolesV.add(arr[i][e]);	
			}
		}
		return rolesV ;
	}
	
	
	// ****************** GetImageFolder Functions *********************

	/**
	Gives the folder where All the html templates for a language are located.
	This method will call its helper method getTemplateLibName to get the
	name of the folder which contains the templates for a certain meta id
	*/

	public String getExternalImageFolder (HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
	{
		imcode.server.User user = getUserObj(req,res) ;
		String metaId = this.getMetaId(req) ;
		if( metaId == null)
		{
			log("No meta_id could be found! Error in Chat.class") ;
			return "No meta_id could be found!" ;
		}

		// Lets get serverinformation
		String host = req.getHeader("Host") ;
		String imcServer = Utility.getDomainPref("userserver",host) ;
		String confPoolServer = Utility.getDomainPref("chat_server",host) ;

		String extFolder = this.getExternalImageFolder(imcServer, metaId, user) ;
		return extFolder += this.getTemplateLibName(confPoolServer, metaId) ;
	}


	/**
	Returns the folder where this templates are situated for a certain metaid.
	**/
	protected String getExternalImageFolder(String server, String meta_id,imcode.server.User user ) throws ServletException, IOException
	{
		RmiConf rmi = new RmiConf(user) ;
		// Ok, Lets get the language for the system
		String imageLib = rmi.getExternalImageFolder(server, meta_id) ;
		return imageLib ;
	} // End of getImageLibName


	/**
	Returns the foldername where the templates are situated for a certain metaid.
	**/
/*	protected String getImageLibName(String server, String meta_id ) throws ServletException, IOException
	{
		String sqlQ = "GetTemplateLib " + meta_id ;
		String libName = "" + RmiConf.execSqlProcedureStr(server, sqlQ) + "/";
		return libName ;

	} // End of getImageLibName
*/
	/**
	Returns the foldername where the templates are situated for a certain metaid.
	**/
	protected String getInternalImageFolder(String server)	 throws ServletException, IOException
	{
		return RmiConf.getInternalImageFolder(server) ;

	} // End of getInternalImageFolder


	/**
	Examines a text, and watches for ' signs, which will extended with another ' sign
	*/
	public String verifySqlText(String str )//in use by verifyForSql
	{
		StringBuffer buf =  new StringBuffer(str) ;
		// log("Innan: " + str) ;
		char apostrof = '\'' ;
		for(int i = 0 ; i < buf.length() ; i++)
		{
			//log(""+ buf.charAt(i)) ;
			if (buf.charAt(i) == apostrof )
			{
				buf.insert(i,apostrof) ;
				i+=1 ;
			}
		}
		str = buf.toString() ;
		// log("Efter: " + str) ;
		return str ;

	} // End CreateAdminHtml


	/**
	Checks for illegal sql parameters.
	**/
	public Properties verifyForSql(Properties aPropObj)//in use by ChatManager
	{
		// Ok, Lets find all apostrofes and if any,add another one
		Enumeration enumValues = aPropObj.elements() ;
		Enumeration enumKeys = aPropObj.keys() ;
		while((enumValues.hasMoreElements() && enumKeys.hasMoreElements()))
		{
			Object oKeys = (enumKeys.nextElement()) ;
			Object oValue = (enumValues.nextElement()) ;
			String theVal = oValue.toString() ;
			String theKey = oKeys.toString() ;
			aPropObj.setProperty(theKey, verifySqlText(theVal)) ;
		}
		// log(aPropObj.toString()) ;
		return aPropObj ;
	} // verifyForSql

	

	/**
	 * checks if user is authorized
	 * @param req
	 * @param res is used if error (send user to conference_starturl )
	 * @param user
	*/

	//används av bla ChatViewer
	protected boolean isUserAuthorized( HttpServletRequest req, HttpServletResponse res, imcode.server.User user )
	throws ServletException, IOException
	{

		// Lets get serverinformation
		String host = req.getHeader( "Host" ) ;
		String imcServer = Utility.getDomainPref( "userserver", host ) ;

		HttpSession session = req.getSession( true );

		//lets get if user authorized or not
		boolean authorized = true;

		//OBS "Chat.meta_id" ska bytas ut mot en konstant senare
		String stringMetaId = (String)session.getValue( "Chat.meta_id" );
		if ( stringMetaId == null )
		{
			authorized = false;
			//lets send unauthorized users out
			String serverName = MetaInfo.getServerName(req) ;
			String startUrl = RmiConf.getLoginUrl(host) ;
			res.sendRedirect(serverName + startUrl) ;
		}
		else
		{
			int metaId = Integer.parseInt( stringMetaId );
			authorized = isUserAuthorized( req, res, metaId, user );
		}
		
		
		return authorized;
	}

	/**
	 * checks if user is authorized
	 * @param req is used for collecting serverinfo and session
	 * @param res is used if error (send user to conference_starturl )
	 * @param metaId conference metaId
	 * @param user
	*/
	protected boolean isUserAuthorized( HttpServletRequest req, HttpServletResponse res, int metaId, imcode.server.User user )
	throws ServletException, IOException
	{

		// Lets get serverinformation
		String host = req.getHeader( "Host" ) ;
		String imcServer = Utility.getDomainPref( "userserver", host ) ;

		HttpSession session = req.getSession( true );

		//is user authorized?
		boolean authorized = IMCServiceRMI.checkDocRights( imcServer, metaId, user );

		//lets send unauthorized users out
		if ( !authorized )
		{
			String serverName = MetaInfo.getServerName(req) ;
			String startUrl = RmiConf.getLoginUrl(host) ;
			res.sendRedirect(serverName + startUrl) ;
		}
		
		return authorized;
	}

	/**
	 * check if user has right to edit
	 * @param imcServer rmi
	 * @param metaId metaId for conference
	 * @param user
	*/
	protected boolean userHasRightToEdit( String imcServer, int metaId,
		imcode.server.User user ) throws java.io.IOException
	{

		return ( IMCServiceRMI.checkDocRights( imcServer, metaId, user ) &&
			IMCServiceRMI.checkDocAdminRights( imcServer, metaId, user ) );
	}

	/**
	 * check if user is admin and has rights to edit
	 * @param imcServer rmi
	 * @param metaId metaId for conference
	 * @param user
	*/
	protected boolean userHasAdminRights( String imcServer, int metaId,
		imcode.server.User user ) throws java.io.IOException
	{
		return ( IMCServiceRMI.checkDocAdminRights( imcServer, metaId, user ) &&
			IMCServiceRMI.checkDocAdminRights( imcServer, metaId, user, 65536 ) );

	}

	
	//**************** does the setup for chatboard  **********************
	//lets get the settings for the chat and convert them
	//and add them into HashTable and add it into the session
	//ugly it should moves into the ChatMember obj, but i haven't got the time to do it now
	public synchronized Hashtable prepareChatBoardSettings(Chat member, HttpServletRequest req, boolean bool)
	{
		//now we sets up the settings for this chat
		HttpSession session = req.getSession(true);
		
		Hashtable hash = (Hashtable)session.getValue("ChatBoardHashTable");
		if (hash == null)
		{
		 	hash = new Hashtable();
		}
		Properties settings = member.getChatParameters();	
		//log("settings "+settings);
		//lets convert them
		boolean onOff = false;

		//sets up show datTime or not
		String dateTime = "dateTime";		
		if(settings.getProperty(dateTime).equals("2"))
		{
			onOff = false;
		}else{
			if (bool)
			{
				if (settings.getProperty(dateTime).equals("3"))
				{
					onOff = req.getParameter(dateTime) == null ? false : true;
				}else{
					onOff = true;	 
				}

			}else{
				onOff = true;
			}		
		}
		//log("1dateTime = "+onOff);
		hash.put("dateTimeBoolean", new Boolean(onOff));


		// onOff = req.getParameter("") == null ? false : true;

		//sets up show public msg or not
		String publik = "publik";
		if(settings.getProperty(publik).equals("2"))
		{
			onOff = false;
		}else
		{
			if (bool)
			{
				if(settings.getProperty(publik).equals("3"))
				{
					onOff = req.getParameter(publik) == null ? false : true;
				}else
				{
					onOff = true;
				}
			}else
			{
				onOff = true;
			}		
		}
		//log("1publik = "+onOff);
		hash.put("publicMsgBoolean", new Boolean(onOff));


		//sets up show private msg or not
		String privat = "privat";
		if(settings.getProperty(privat).equals("2"))
		{
			onOff = false;
		}else
		{
			if (bool)
			{
				if (settings.getProperty(privat).equals("3"))
				{
					onOff = req.getParameter(privat) == null ? false : true;
				}else
				{
					onOff = true;
				}				
			}else
			{
				onOff = true;
			}		
		}
		//log("1privat = "+onOff);
		hash.put("privateMsgBoolean", new Boolean(onOff));



		//sets up show entrense and exits, or not
		String inOut = "inOut";		
		if(settings.getProperty(inOut).equals("2"))
		{
			onOff = false;
		}else
		{
			if (bool)
			{
				if (settings.getProperty(inOut).equals("3"))
				{
					onOff = req.getParameter(inOut) == null ? false : true;
				}else
				{
					onOff = true;
				}				
			}else
			{
				onOff = true;
			}		
		}
		//log("1inOut = "+onOff);
		hash.put("inOutBoolean", new Boolean(onOff));



		//sets up autoreload on off
		String reload = "reload";
		String updateTime = "updateTime";
		String timeStr = "30";
		
		if(settings.getProperty(reload).equals("2"))
		{
			onOff = false;
			timeStr = "30";
		}else
		{
			if (bool)
			{
				if (settings.getProperty(reload).equals("3"))
				{
					onOff = true;
					timeStr = settings.getProperty(updateTime);
					//log("¤¤¤= "+timeStr);
				}else
				{
					onOff = false;
					timeStr = settings.getProperty(updateTime);
					//log("¤¤¤= "+timeStr);
				}				
			}else
			{	
				onOff = true;
				timeStr = settings.getProperty(updateTime);
				//log("¤¤¤= "+timeStr);
			}
		}
		try
		{
			hash.put("reloadInteger", new Integer(timeStr));
		}catch(NumberFormatException nfe)
		{
			log(nfe.getMessage());
			hash.put("reloadInteger", new Integer("30"));
		}
		
		//log("reloadBoolean = "+onOff);
		hash.put("reloadBoolean", new Boolean(onOff));


		//ok first time we dont have the font size so lets set it to 3
		int size = 3;
		String fontSizeInteger = "fontSizeInteger";
		//log(fontSizeInteger+"= "+((Integer)hash.get(fontSizeInteger)));
		if (settings.getProperty("font").equals("3"))
		{
			if (req.getParameter("fontInc") != null)
			{
				Integer temp = (Integer)hash.get(fontSizeInteger);
				size = temp.intValue();
				size++;
				if(size > 7) size = 7;
			}else if (req.getParameter("fontDec") != null)
			{
				Integer temp = (Integer)hash.get(fontSizeInteger);
				size = temp.intValue();
				size--;
				if(size < 1)size = 1;
			}else
			{
				size = 3;
			}
		}else
		{
			size = 3;
		}
		Integer inten = new Integer(size);
		hash.put(fontSizeInteger, inten);
		

		return	hash;	

	}//end prepareSettings
	
	
	//*****************cleares the session from all chat params ***************
	//the only ones left is
	//logon_isDone and browser_id
	
	public void cleanUpSessionParams(HttpSession session)
	{
		String[] namnen = session.getValueNames();
		for(int i=0; i< namnen.length; i++)
		{
			if(namnen[i].equals("logon.isDone") || namnen[i].equals("browser_id"))
			{
				//do nothing
			}else
			{
				session.removeValue(namnen[i]);
			}
		}
	}


} // End class
