
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import imcode.external.diverse.*;
import imcode.util.* ;

//f�rsta g�ngen vi kommer hit har vi doGet parametern  action=new
// 

public class ChatManager extends ChatBase
{
	private final static String CVS_REV = "$Revision$" ;
	private final static String CVS_DATE = "$Date$" ;
	String HTML_TEMPLATE ;

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
		Properties params = super.getParameters(req) ;
		if (super.checkParameters(req, res, params) == false) return ;

		// Lets get an user object
		imcode.server.User user = super.getUserObj(req,res) ;
		if(user == null) return ;

		int testMetaId = Integer.parseInt( params.getProperty("META_ID") );
		if ( !isUserAuthorized( req, res, testMetaId, user ) )
		{
			return;
		}

		String action = req.getParameter("action") ;
		//log("ChatManager is in action...") ;
		if(action == null)
		{
			action = "" ;
			String header = "ChatManager servlet. " ;
			ChatError err = new ChatError(req,res,header,3) ;
			log(header + err.getErrorMsg()) ;
			return ;
		}

		// ********* NEW ********
		//i'ts here we end up when we creates a new chatlink
		if(action.equalsIgnoreCase("NEW"))
		{
			//log("Lets add a chat");
			HttpSession session = req.getSession(false) ;
			if (session != null)
			{
				// log("Ok nu s�tter vi metav�rdena");
				session.putValue("Chat.meta_id", params.getProperty("META_ID")) ;
				session.putValue("Chat.parent_meta_id", params.getProperty("PARENT_META_ID")) ;
				session.putValue("Chat.cookie_id", params.getProperty("COOKIE_ID")) ;
			}

			String url = MetaInfo.getServletPath(req) + "ChatCreator?action=NEW" ;
			//log("Redirect till:" + url) ;
			res.sendRedirect(url) ;
			return ;
		}

		// ********* VIEW ********
		if(action.equalsIgnoreCase("VIEW"))
		{

			// Lets get userparameters
			Properties userParams = super.getUserParameters(user) ;
			String metaId = params.getProperty("META_ID") ;
			String userId = userParams.getProperty("USER_ID") ;
			RmiConf rmi = new RmiConf(user) ;

			// Lets detect which type of user we got
			String userType = userParams.getProperty("USER_TYPE") ;
			String loginType = userParams.getProperty("LOGIN_TYPE") ;
				//log("Usertype:" + userType) ;
				//log("loginType:" + userType) ;

			// We got 3 usertypes: 0= specialusers, 1=normal, 2=confernce
			// We got 3 logintypes: "Extern"=web users, "ip_access"= people from a certain ip nbr
			
			// and "verify" = people who has logged into the system
			
			// Lets store  the standard metavalues in his session object
				HttpSession session = req.getSession(false) ;
				if (session != null)
				{
					// log("Ok nu s�tter vi metav�rdena");
					session.putValue("Chat.meta_id", params.getProperty("META_ID")) ;
					session.putValue("Chat.parent_meta_id", params.getProperty("PARENT_META_ID")) ;
					session.putValue("Chat.cookie_id", params.getProperty("COOKIE_ID")) ;
					session.putValue("Chat.viewedDiscList", new Properties()) ;
					//log("OK, nu s�tter vi viewedDiscList") ;
				}

String loginPage = MetaInfo.getServletPath(req) + "ChatLogin?login_type=login" ;
				//log("Redirect till:" + loginPage) ;
				res.sendRedirect(loginPage) ;
				return ;
		
		} // End of View

		// ********* CHANGE ********
		if(action.equalsIgnoreCase("CHANGE"))
		{
			MetaInfo mInfo = new MetaInfo() ;
			String url = MetaInfo.getServletPath(req) + "ChangeExternalDoc2?"
				+ mInfo.passMeta(params) + "&metadata=meta" ;
			//log("Redirects to:" + url) ;
			res.sendRedirect(url) ;
			return ;
		} // End if

		// ********* STATISTICS OBS. NOT USED IN PROGRAM, ONLY FOR TEST ********
		if(action.equalsIgnoreCase("STATISTICS"))
		{

			// Lets get serverinformation
			String host = req.getHeader("Host") ;
			String imcServer = Utility.getDomainPref("userserver",host) ;
			String ChatPoolServer = Utility.getDomainPref("conference_server",host) ;
			//log("confpoolserver " + ChatPoolServer ) ;
			String metaId = req.getParameter("meta_id") ;
			String frDate = req.getParameter("from_date") ;
			String toDate = req.getParameter("to_date") ;
			String mode = req.getParameter("list_mode") ;

			// Lets fix the date stuff
			if( frDate.equals("0")) frDate  = "1991-01-01 00:00" ;
			if( toDate.equals("0")) toDate  = "2070-01-01 00:00" ;
			if( mode == null) mode  = "1" ;

			StringBuffer sql = new StringBuffer() ;
			sql.append("C_AdminStatistics1" + " " + metaId + ", '" + frDate + "', '" );
			sql.append(toDate + "', " + mode) ;
			//log("C_AdminStatistics sql: " + sql.toString()) ;
			String[][] arr = ChatManager.getStatistics(ChatPoolServer, sql.toString()) ;

			//log("C_AdminStatistics sql: " + arr.length) ;
		} // End if


	} // End doGet


	/**
	Log function, will work for both servletexec and Apache
	**/

	public void log( String str)
	{
		super.log("ChatManager: " + str ) ;
		//System.out.println("ChatManager: " + str ) ;
	}

	/**
	Statistics function. Used By AdminManager system
	**/

	public static String[][] getStatistics (String confServer,String sproc)
	throws ServletException, IOException
	{

		String[][] arr = RmiConf.execProcedureMulti(confServer,sproc) ;
		//log("AdminStatistics sql: " + arr.length) ;
		return arr ;
	}






} // End of class
