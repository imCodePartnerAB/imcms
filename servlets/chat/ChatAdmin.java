import java.io.IOException;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Hashtable;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import imcode.external.diverse.VariableManager;
import imcode.external.diverse.Html;
import imcode.util.IMCServiceRMI;
import imcode.util.Utility;
import imcode.util.Parser;
import imcode.external.diverse.MetaInfo;


import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import imcode.external.diverse.* ;
import imcode.util.* ;

/**
 * Lists Chaterences who has debates that has requared dates. (create or modified)
 *
 * Html template in use:
 * AdminChat.html
 * AdminChat_list_tool.html
 * AdminChat_list.html
 * AdminChat_list_Chat_element.html
 * AdminChat_list_debate_element.html
 * Error.html
 *
 * Html parstags in use:
 * #META_ID#
 * #CONFERENCE_LIST#
 * #CONFERENCE#
 * #FORUM_LIST
 * #FORUM#
 * #DEBAT_LIST#
 * #DEBATE#
 *
 * stored procedures in use:
 * -
 *
 * @version 1.02 11 Nov 2000
 * @author Jerker Drottenmyr
 *
 */
public class ChatAdmin extends HttpServlet{

	private static final String TEMPLATE_CONF = "AdminChat.html";
	private static final String TEMPLATE_LIST_TOOL = "AdminChat_list_tool.html";
	private static final String TEMPLATE_LIST = "AdminChat_list.html";
	private static final String TEMPLATE_CONF_ELEMENT = "AdminChat_list_Chat_element.html";
	private static final String TEMPLATE_FORUM_ELEMENT = "AdminChat_list_forum_element.html";
	private static final String TEMPLATE_DEBATE_ELEMENT = "AdminChat_list_debate_element.html";
	private static final String TEMPLATE_ERROR = "Error.html";
	private static final String ERROR_HEADER = "AdminChat";

	//required date format
	private static final String DATE_FORMATE = "yyyy-MM-dd";

	// lets dispatches all requests to doPost()
	protected void doGet( HttpServletRequest request, HttpServletResponse response )
	throws ServletException, IOException
	{
		doPost( request, response );
	}

	protected void doPost( HttpServletRequest request, HttpServletResponse response )
	throws ServletException, IOException
	{

		String host = request.getHeader( "Host" );
		String imcserver = Utility.getDomainPref( "adminserver", host );
		String eMailServerMaster = Utility.getDomainPref( "servermaster_email", host );

		// lets get ready for errors
		String deafultLanguagePrefix = IMCServiceRMI.getLanguage( imcserver );

		// Lets validate the session
		if ( checkSession( request, response ) == false )
		{
			return ;
		}

		// Lets get an user object
		imcode.server.User user = getUserObj( request, response ) ;
		if(user == null)
		{
			sendErrorMessage( imcserver, eMailServerMaster, deafultLanguagePrefix , this.ERROR_HEADER, 1, response );
			return ;
		}

		// Lets verify that the user who tries to add a new user is an admin
		if (checkAdminRights( imcserver, user) == false)
		{
			sendErrorMessage( imcserver, eMailServerMaster, deafultLanguagePrefix , this.ERROR_HEADER, 2, response );
			return ;
		}

		/* User has right lets do the request */
		String languagePrefix = getLanguagePrefix( imcserver, user.getInt( "lang_id" ) );
		VariableManager vm = new VariableManager();

		/* lets get which request to do */
		// generate htmlpage for listing Chaterences
		if ( request.getParameter( "VIEW_CONF_LIST_TOOL" ) != null )
		{
			sendHtml( request, response, vm, this.TEMPLATE_LIST_TOOL );

			// generate list off Chaterences
		}
		else if ( request.getParameter( "VEIW_CONF_LIST" ) != null )
		{
			listChats( request, response, languagePrefix );

			// go to AdminManager
		}
		else if ( request.getParameter( "CANCEL" ) != null )
		{
			Utility.redirect( request, response, "AdminManager" );

			// go to htmlpage for listing Chaterences
		}
		else if ( request.getParameter( "CANCEL_CONF_LIST" ) != null )
		{
			Utility.redirect( request, response, "AdminChat" );

			// go to AdminChat page
		}
		else
		{
			sendHtml( request, response, vm, this.TEMPLATE_CONF );
		}

	}

	/**
	* check for right date form
	*/
	private boolean isDateInRightFormat( String date )
	{

		// Format the current time.
		SimpleDateFormat formatter = new SimpleDateFormat( this.DATE_FORMATE );

		try
		{
			formatter.parse( date );
		} catch ( ParseException  e )
		{
			return false;

		}

		return true;
	}

	/*
	*
	*/
	private void listChats( HttpServletRequest request, HttpServletResponse response, String languagePrefix )
	throws ServletException, IOException
	{
		String host = request.getHeader( "Host" );
		String imcserver = Utility.getDomainPref( "adminserver", host );
		String eMailServerMaster = Utility.getDomainPref( "servermaster_email", host );
		boolean noErrors = true;

		/*
		* 0 = startDate to endDate
		* 1 = all
		* 2 = all upp to endDate
		* 3 = all down to startDate
		*/
		int listByDateMode = 0;

		/*
		* 0 = all date !not in use
		* 1 = create date
		* 2 = modified date
		*/
		String listMode = request.getParameter( "LISTMOD" );
		String startDate = request.getParameter( "START_DATE" );
		String endDate = request.getParameter( "END_DATE" );

		/* lets se if any errors in requared fields or if some is missing */
		try {
			if ( listMode != null )
			{
				int  mode = Integer.parseInt( listMode );
				if ( !(mode == 1 || mode == 2 ) )
				{
					noErrors = false;
				}
			}
			else
			{
				noErrors = false;
			}
		} catch ( NumberFormatException e )
		{
			noErrors = false;
		}

		if ( startDate != null )
		{
			if ( startDate.length() > 0 )
			{
				if ( !isDateInRightFormat( startDate ) )
				{
					noErrors = false;
				}
			}
			else
			{
				startDate = "0"; // Stored Procedure expects 0 then no startDate
			}
		}
		else
		{
			noErrors = false; // no startDate field submited
		}

		if ( endDate != null )
		{
			if ( endDate.length() > 0 )
			{
				if ( !isDateInRightFormat( endDate ) )
				{
					noErrors = false;
				}
			}
			else
			{
				endDate = "0"; // Stored Procedure expects 0 then no endDate
			}
		}
		else
		{
			noErrors = false; // no endDate field submited
		}

		// lets generate response page
		if ( noErrors )
		{

			String ChatPoolServer = Utility.getDomainPref( "Chaterence_server", host );

			//lets get htmltemplate for Chaterencerow
			String htmlChatElement = IMCServiceRMI.parseDoc( imcserver, null, this.TEMPLATE_CONF_ELEMENT, languagePrefix );
			String htmlForumElement = IMCServiceRMI.parseDoc( imcserver, null, this.TEMPLATE_FORUM_ELEMENT, languagePrefix );
			String htmlDebateElement = IMCServiceRMI.parseDoc( imcserver, null, this.TEMPLATE_DEBATE_ELEMENT, languagePrefix );

			String[][] listOfChats = IMCServiceRMI.sqlQueryMulti( imcserver, "ListChats" );

			// lets create Chaterencelist
			StringBuffer ChaterencesListTag = new StringBuffer();

			Hashtable ChaterenceTags = new Hashtable();
			Hashtable forumTags = new Hashtable();
			Hashtable debateTags = new Hashtable();

			for ( int i = 0 ; i < listOfChats.length ; i++ )
			{

				String metaId = listOfChats[i][0];
				String sprocetForum = "AdminStatistics1 " + metaId + ", '" + startDate + "', '" + endDate + "', " + listMode;
				String[][] queryResultForum = ChatManager.getStatistics( ChatPoolServer, sprocetForum );

				//lets create forumList for this Chaterence
				StringBuffer forumList = new StringBuffer();

				for ( int j = 0 ; j < queryResultForum.length ; j++ )
				{

					String forumId = queryResultForum[j][0];
					String sprocetDebate = "AdminStatistics2 " + metaId + ", " + forumId + ", '" + startDate + "', '" + endDate + "', " + listMode;
					String[][] queryResultDebate = ChatManager.getStatistics( ChatPoolServer, sprocetDebate );

					// lets create debatelist for this forum
					StringBuffer debateList = new StringBuffer();
					for ( int k = 0 ; k < queryResultDebate.length ; k++ )
					{
						debateTags.put( "DEBATE", queryResultDebate[k][1] );
						debateTags.put( "DATE", queryResultDebate[k][2] );
					}

					forumTags.put("FORUM", queryResultForum[j][1] );
					forumTags.put("DEBATE_LIST", debateList.toString() );
					forumList.append( (Parser.parseTags( new StringBuffer( htmlForumElement ), '#', " <>\n\r\t", (java.util.Map)forumTags, true, 1 )).toString() );
				}

				if ( queryResultForum.length > 0 )
				{
					ChaterenceTags.put( "SERVLET_URL", MetaInfo.getServletPath( request ) );
					ChaterenceTags.put( "META_ID", metaId );
					ChaterenceTags.put( "CONFERENCE", listOfChats[i][1] );
					ChaterenceTags.put( "FORUM_LIST", forumList.toString() );
					ChaterencesListTag.append( (Parser.parseTags( new StringBuffer( htmlChatElement ), '#', " <>\n\r\t", (java.util.Map) ChaterenceTags, true, 1 )).toString() );
				}
			}

			//Lets generate the html page
			VariableManager vm = new VariableManager();
			vm.addProperty( "CONFERENCE_LIST", ChaterencesListTag.toString() );

			this.sendHtml( request, response, vm, this.TEMPLATE_LIST );

		}
		else
		{
			sendErrorMessage( imcserver, eMailServerMaster, languagePrefix , this.ERROR_HEADER, 10, response );
		}
	}

	//************   from Administrator class   *********************************************


	public boolean checkParameters(Properties aPropObj)
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
	} // checkparameters

	/**
	Returns an user object
	*/

	protected static imcode.server.User getUserObj(HttpServletRequest req,
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
			return null ;
	}


	/**
	Verifies that the user is logged in
	*/

	protected static boolean checkSession(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException
	{

		// Get the session
		HttpSession session = req.getSession(true);
		// Does the session indicate this user already logged in?
		Object done = session.getValue("logon.isDone");  // marker object
		imcode.server.User user = (imcode.server.User) done ;

		if (done == null)
		{
			// No logon.isDone means he hasn't logged in.

			// Lets get the login page
			String host 				= req.getHeader("Host") ;
			// String imcserver 			= Utility.getDomainPref("adminserver", host) ;
			String start_url        	= Utility.getDomainPref( "start_url",host ) ;

			// Save the request URL as the true target and redirect to the login page.
			session.putValue("login.target", HttpUtils.getRequestURL(req).toString());
			String serverName = MetaInfo.getServerName(req) ;
			String startUrl = Utility.getDomainPref( "start_url",host ) ;

			//String startUrl = RmiCon f.getLoginUrl() ;
			res.sendRedirect(serverName + startUrl) ;
			return false;
		}
		return true ;
	}

	/**
	CheckAdminRights, returns true if the user is an superadmin. Only an superadmin
	is allowed to create new users
	False if the user isn't an administrator.
	1 = administrator
	0 = superadministrator
	*/

	public static boolean checkAdminRights(String server, imcode.server.User user)
	{

		try
		{

			// Lets verify that the user who tries to add a new user is an SUPERADMIN
			RmiLayer imc = new RmiLayer(user) ;
			int currUser_id = user.getInt("user_id") ;
			String checkAdminSql = "CheckAdminRights " + currUser_id ;
			String[] roles = imc.execSqlProcedure(server, checkAdminSql) ;

			for(int i = 0 ; i< roles.length; i++ )
			{
				String aRole = roles[i] ;
				if(aRole.equalsIgnoreCase("0") )
					return true ;
			}
			return false ;
		} catch (IOException e)
		{
			//log("An error occured in CheckAdminRights") ;
			//log(e.getMessage() ) ;
		}
		return false ;
	} // checkAdminRights


	/**
	CheckAdminRights, returns true if the user is an admin.
	False if the user isn't an administrator
	*/

	protected static boolean checkAdminRights(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException
	{

		String host 				= req.getHeader("Host") ;
		String server 			= Utility.getDomainPref("adminserver",host) ;
		imcode.server.User user = getUserObj(req,res) ;
		if(user == null)
		{
			//this.log("CheckadminRights: an error occured, getUserObj") ;
			return false ;
		}
		else
			return checkAdminRights(server, user) ;
	}


	/**
	GetAdminTemplateFolder. Takes the userobject as argument to detect the language
	  from the user and and returns the base path to the internal folder, hangs on the
	  language prefix and an "/admin/" string afterwards...

	  Example : D:\apache\htdocs\templates\se\admin\
	*/
	public String getAdminTemplateFolder (String server, imcode.server.User user) throws ServletException, IOException
	{

		RmiLayer rmi = new RmiLayer(user) ;

		// Since our templates are located into the admin folder, we'll have to hang on admin
		String templateLib = rmi.getInternalTemplateFolder(server) ;

		// Lets get the users language id. Use the langid to get the lang prefix from db.
		String langId = user.getString("lang_id") ;
		String langPrefix = rmi.execSqlProcedureStr(server, "GetLangPrefixFromId " + langId) ;
		templateLib += langPrefix + "/admin/" ;
		//this.log("lang_id:" + langId) ;
		//this.log("langPrefix:" + langPrefix) ;
		//this.log("InternalTemplatePath:" + templateLib) ;
		return templateLib ;
	}



	/**
	SendHtml. Generates the html page to the browser.
	**/

	public String createHtml (HttpServletRequest req, HttpServletResponse res,
		VariableManager vm, String htmlFile) throws ServletException, IOException
	{

		// Lets get the path to the admin templates folder
		String host 				= req.getHeader("Host") ;
		String server 			= Utility.getDomainPref("adminserver",host) ;
		imcode.server.User user = getUserObj(req,res) ;
		String templateLib = this.getAdminTemplateFolder(server, user) ;

		/*
		RmiLayer rmi = new RmiLayer(user) ;

		// Since our templates are located into the admin folder, we'll have to hang on admin
		// String templateLib = MetaInfo.getInternalTemplateFolder() ;

		String templateLib = rmi.getInternalTemplateFolder(server) ;

		// Lets get the users language id. Use the langid to get the lang prefix from db.
		String langId = user.getString("lang_id") ;
		String langPrefix = rmi.execSQlProcedureStr(server, "GetLangPrefixFromId " + langId) ;
		templateLib += langPrefix + "/admin/" ;
		 // this.log("InternalTemplatePath:" + templateLib) ;
		*/

		// Lets add the server host
		String servletHome = MetaInfo.getServletHost(req) ;
		vm.addProperty("SERVLET_URL", MetaInfo.getServletPath(req))  ;
		vm.addProperty("SERVLET_URL2", MetaInfo.getServletPath(req))  ;

		HtmlGenerator htmlObj = new HtmlGenerator(templateLib, htmlFile) ;
		String html = htmlObj.createHtmlString(vm,req) ;
		return html ;
	}

	/**
	  SendHtml. Generates the html page to the browser.
	*/

	public void sendHtml (HttpServletRequest req, HttpServletResponse res,
		VariableManager vm, String htmlFile) throws ServletException, IOException
	{

		String str = this.createHtml(req, res, vm, htmlFile) ;
		HtmlGenerator htmlObj = new HtmlGenerator() ;
		htmlObj.sendToBrowser(req,res,str) ;
	}

	/**
	Log function. Logs the message to the log file and console
	*/

	public void log(String msg)
	{
		super.log(msg) ;
		System.out.println("Administrator: " + msg) ;
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
	Convert array to vector
	*/

	public Vector convert2Vector(String[] arr)
	{
		Vector rolesV  = new Vector() ;
		for(int i = 0; i<arr.length; i++)
			rolesV.add(arr[i]) ;
		return rolesV ;
	}

	public Vector getOneRow( String[][] multi, int row)
	{
		Vector v = new Vector() ;
		try
		{
			String[] theRow = multi[row] ;
			for(int i = 0 ; i < theRow.length ; i++ )
			{
				v.add(theRow[i]) ;
			}
			return v ;
		} catch(Exception e)
		{
			return v ;
		}
	} // getOneRow

	/**
	  Returns the nbr of rows in the multiarray
	**/
	public int getNbrOfRows( String[][] multi )
	{
		try
		{
			return multi.length ;
		} catch(Exception e)
		{
			return 0 ;
		}
	} // getNbrOfRows

	/**
	 * send error message
	 *
	 * @param server
	 * @param eMailServerMaster
	 * @param languagePrefix
	 * @param errorHeader
	 * @param errorCode is the code to loock upp in ErrMsg.ini file
	*/
	protected void sendErrorMessage( String imcserver, String eMailServerMaster,
		String languagePrefix, String errorHeader,
		int errorCode, HttpServletResponse response ) throws IOException
	{

		ErrorMessageGenerator errroMessage = new ErrorMessageGenerator( imcserver, eMailServerMaster,
			languagePrefix,	errorHeader, this.TEMPLATE_ERROR, errorCode );

		errroMessage.sendHtml( response );
	}

	/**
	 * get users language
	 *
	 * @param server
	 * @param langId from userObject
	*/
	protected String getLanguagePrefix( String server, int langId ) throws IOException
	{

		String sqlQ = "GetLangPrefixFromId " + String.valueOf( langId );
		String languagePrefix = IMCServiceRMI.sqlProcedureStr( server, sqlQ );
		return languagePrefix;
	}

}
