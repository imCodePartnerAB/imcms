import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import imcode.external.diverse.*;
import imcode.util.* ;

public class ChatCreator extends ChatBase
{
	String HTML_TEMPLATE ;

	/**
	The POST method creates the html page when this side has been
	redirected from somewhere else.
	**/

	public void doPost(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException
	{

		// Lets validate the session, e.g has the user logged in to Janus?
		if (super.checkSession(req,res) == false)	return ;

		// Lets get the standard parameters and validate them
		Properties params = super.getSessionParameters(req) ;
		if (super.checkParameters(req, res, params) == false) return ;

		// Lets get the new conference parameters
		Properties chatParams = this.getNewChatParameters(req) ;
		if (super.checkParameters(req, res, chatParams) == false) return ;

		// Lets get an user object
		imcode.server.User user = super.getUserObj(req,res) ;
		if(user == null) return ;

		if ( !isUserAuthorized( req, res, user ) )
		{
			return;
		}

		String action = req.getParameter("action") ;
		if(action == null)
		{
			action = "" ;
			String header = "ChatCreator servlet. " ;
			ChatError err = new ChatError(req,res,header,3) ;
			log(header + err.getErrorMsg()) ;
			return ;
		}

		// Lets get serverinformation
		String host = req.getHeader("Host") ;
		String imcServer = Utility.getDomainPref("userserver",host) ;
		String chatPoolServer = Utility.getDomainPref("chat_server",host) ;

		// ********* NEW ********
		if(action.equalsIgnoreCase("ADD_CHAT"))
		{
			log("OK, nu skapar vi Chatten") ;

			// Added 000608
			// Ok, Since the chat db can be used from different servers
			// we have to check when we add a new conference that such an meta_id
			// doesnt already exists.
/*			RmiConf rmi = new RmiConf(user) ;
			String metaId = params.getProperty("META_ID") ;
			String foundMetaId = rmi.execSqlProcedureStr(chatPoolServer, "FindMetaId " + metaId) ;
			if(!foundMetaId.equals("1"))
			{
				action = "" ;
				String header = "ChatCreator servlet. " ;
				ChatError err = new ChatError(req,res,header,90) ;
				log(header + err.getErrorMsg()) ;
				return ;
			}

			// Lets add a new Chat to DB
			// AddNewChat @meta_id int, @chatName varchar(255)


			String chatName = chatParams.getProperty("CHAT_NAME") ;
			// String sortType = "1" ;	// Default value, unused so far
			String sqlQ = "AddNewChat " + metaId + ", '" + chatName + "'" ;
			log("AddNewChat sql:" + sqlQ ) ;
			rmi.execSqlUpdateProcedure(chatPoolServer, sqlQ) ;

			// Lets add a new forum to the conference
			// AddNewForum @meta_id int, @forum_name varchar(255), @archive_mode char, @archive_time int
			String newFsql = "AddNewForum " + metaId +", '" + chatParams.getProperty("FORUM_NAME") + "', ";
			newFsql += "'A' , 30" ;
			//newFsql += "'" + chatParams.getProperty("ARCHIVE_MODE") + "', " ;
			//newFsql += chatParams.getProperty("ARCHIVE_TIME")	;
			log("AddNewForum sql:" + newFsql ) ;
			rmi.execSqlUpdateProcedure(chatPoolServer, newFsql) ;

			// Lets get the administrators user_id
			String user_id = user.getString("user_id") ;

			// Lets get the recently added forums id
			String forum_id = rmi.execSqlProcedureStr(chatPoolServer, "GetFirstForum " + metaId) ;

			// Lets add this user into the conference if hes not exists there before were
			// adding the discussion
			String confUsersAddSql = "ChatUsersAdd "+ user_id +", "+ metaId +", '"+ user.getString("first_name") + "', '";
			confUsersAddSql += user.getString("last_name") + "'";
			rmi.execSqlUpdateProcedure(chatPoolServer, confUsersAddSql) ;

			// Ok, were done creating the conference. Lets tell Janus system to show this child.
			rmi.activateChild(imcServer, metaId) ;

			// Ok, Were done adding the conference, Lets go back to the Manager
			String loginPage = MetaInfo.getServletPath(req) + "ChatLogin?login_type=login" ;
			res.sendRedirect(loginPage) ;

			return ;
*/		
		}

	} // End POST


	/**
	The GET method creates the html page when this side has been
	redirected from somewhere else.
	**/

	public void doGet(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException
	{

		// Lets validate the session, e.g has the user logged in to Janus?
		if (super.checkSession(req,res) == false)	return ;

		// Lets get the standard parameters and validate them
		Properties params = super.getSessionParameters(req) ;
		if (super.checkParameters(req, res, params) == false) return ;

		// Lets get an user object
		imcode.server.User user = super.getUserObj(req,res) ;
		if(user == null) return ;

		if ( !isUserAuthorized( req, res, user ) )
		{
			return;
		}

		String action = req.getParameter("action") ;
		if(action == null)
		{
			action = "" ;
			String header = "ChatCreator servlet. " ;
			ChatError err = new ChatError(req,res,header,3) ;
			log(header + err.getErrorMsg()) ;
			return ;
		}

		// ********* NEW ********
		if(action.equalsIgnoreCase("NEW"))
		{
			// Lets build the Responsepage to the loginpage
			VariableManager vm = new VariableManager() ;
			vm.addProperty("SERVLET_URL", MetaInfo.getServletPath(req)) ;
			sendHtml(req,res,vm, HTML_TEMPLATE) ;
			return ;
		}
	} // End doGet


	/**
	Collects the parameters from the request object
	**/

	protected Properties getNewChatParameters( HttpServletRequest req) throws ServletException, IOException
	{
		log("Parameter Names: "+req.getParameterNames());
		
		Properties chatP = new Properties() ;
		String chat_name = (req.getParameter("chat_name")==null) ? "" : (req.getParameter("chat_name")) ;
	//	String forum_name = (req.getParameter("forum_name")==null) ? "" : (req.getParameter("forum_name")) ;
		//	String archive_mode = (req.getParameter("archive_mode")==null) ? "" : (req.getParameter("archive_mode")) ;
		//	String archive_time = (req.getParameter("archive_time")==null) ? "" : (req.getParameter("archive_time")) ;
		//	String disc_header = (req.getParameter("disc_header")==null) ? "" : (req.getParameter("disc_header")) ;
		//	String disc_text = (req.getParameter("disc_text")==null) ? "" : (req.getParameter("disc_text")) ;

		chatP.setProperty("CHAT_NAME", chat_name.trim()) ;
		//	chatP.setProperty("FORUM_NAME", forum_name.trim()) ;
		//	confP.setProperty("ARCHIVE_MODE", archive_mode.trim()) ;
		//	confP.setProperty("ARCHIVE_TIME", archive_time.trim()) ;
		//	confP.setProperty("DISC_HEADER", disc_header.trim()) ;
		//	confP.setProperty("DISC_TEXT", disc_text.trim()) ;

		//	this.log("Chat paramters:" + confP.toString()) ;
		return chatP ;
	}

	/**
	Detects paths and filenames.
	*/

	public void init(ServletConfig config) throws ServletException
	{
		super.init(config);
		HTML_TEMPLATE = "createChat.HTM" ;

		// log("Nu init vi") ;
		/*
		HTML_TEMPLATE = getInitParameter("html_template") ;

		if( HTML_TEMPLATE == null ) {
		Enumeration initParams = getInitParameterNames();
		System.err.println("ChatCreator: The init parameters were: ");
		while (initParams.hasMoreElements()) {
		System.err.println(initParams.nextElement());
		}
		System.err.println("ChatCreator: Should have seen one parameter name");
		throw new UnavailableException (this,
		"Not given a path to the asp diagram files");
		}

		log("HTML_TEMPLATE:" + HTML_TEMPLATE ) ;
		*/
	} // End of INIT

	/**
	Log function, will work for both servletexec and Apache
	**/

	public void log( String str)
	{
		super.log(str) ;
		System.out.println("ChatCreator: " + str ) ;
	}


} // End class
