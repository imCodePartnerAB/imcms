import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import imcode.external.diverse.*;
import imcode.util.* ;

/**
 *
 * Html template in use:
 * BILLBOARD_CREATOR.HTM
 *
 * Html parstags in use:
 * #BILLBOARD_NAME#
 * #SECTION_NAME#
 *
 * stored procedures in use:
 * B_AddNewBillBoard
 * B_AddNewSection

 *
 * @version 1.2 20 Aug 2001
 * @author Rickard Larsson, Jerker Drottenmyr, REBUILD TO BillBoardCreator BY Peter Östergren
*/

public class BillBoardCreator extends BillBoard
{//BillBoardCreator
	String HTML_TEMPLATE ;

	/**
	The POST method creates the html page when this side has been
	redirected from somewhere else.
	**/

	public void doPost(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException
	{
		
		//log("START BillBoardCreator doPost");
		// Lets validate the session, e.g has the user logged in to Janus?
		if (super.checkSession(req,res) == false)	return ;

		// Lets get the standard parameters and validate them
		Properties params = super.getSessionParameters(req) ;
		if (super.checkParameters(req, res, params) == false) return ;

		// Lets get the new conference parameters
		Properties confParams = this.getNewConfParameters(req) ;
		if (super.checkParameters(req, res, confParams) == false) return ;

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
			String header = "BillBoardCreator servlet. " ;
			ConfError err = new ConfError(req,res,header,3) ;
			log(header + err.getErrorMsg()) ;
			return ;
		}

		// Lets get serverinformation
		String host = req.getHeader("Host") ;
		String imcServer = Utility.getDomainPref("userserver",host) ;
		String confPoolServer = Utility.getDomainPref("billboard_server",host) ;

		// ********* NEW ********
		if(action.equalsIgnoreCase("ADD_BILLBOARD"))
		{
			//log("OK, nu skapar vi anslagstavlan") ;

			// Added 000608
			// Ok, Since the billboard db can be used from different servers
			// we have to check when we add a new billboard that such an meta_id
			// doesnt already exists.
			RmiConf rmi = new RmiConf(user) ;
			String metaId = params.getProperty("META_ID") ;
			String foundMetaId = rmi.execSqlProcedureStr(confPoolServer, "B_FindMetaId " + metaId) ;
			if(!foundMetaId.equals("1"))
			{
				action = "" ;
				String header = "BillBoardCreator servlet. " ;
				BillBoardError err = new BillBoardError(req,res,header,90) ;
				log(header + err.getErrorMsg()) ;
				return ;
			}

			// Lets add a new billboard to DB
			// AddNewConf @meta_id int, @billboardName varchar(255)


			String confName = confParams.getProperty("BILLBOARD_NAME") ;//CONF_NAME
			// String sortType = "1" ;	// Default value, unused so far
			String sqlQ = "B_AddNewBillBoard " + metaId + ", '" + confName + "'" ;//AddNewConf
			//log("B_AddNewBillBoard sql:" + sqlQ ) ;
			rmi.execSqlUpdateProcedure(confPoolServer, sqlQ) ;

			// Lets add a new section to the billBoard
			// B_AddNewSection @meta_id int, @section_name varchar(255), @archive_mode char, @archive_time int
			String newFsql = "B_AddNewSection " + metaId +", '" + confParams.getProperty("SECTION_NAME") + "', ";//AddNewForum
			newFsql += "'A' , 30, 14" ;
			//newFsql += "'" + confParams.getProperty("ARCHIVE_MODE") + "', " ;
			//newFsql += confParams.getProperty("ARCHIVE_TIME")	;
			//log("B_AddNewSection sql:" + newFsql ) ;
			rmi.execSqlUpdateProcedure(confPoolServer, newFsql) ;

			// Lets get the administrators user_id
			String user_id = user.getString("user_id") ;

			// Ok, were done creating the billBoard. Lets tell Janus system to show this child.
			rmi.activateChild(imcServer, metaId) ;

			// Ok, Were done adding the billBoard, Lets go back to the Manager
			String loginPage = MetaInfo.getServletPath(req) + "BillBoardLogin?login_type=login" ;
			res.sendRedirect(loginPage) ;
			return ;
		}

	} // End POST


	/**
	The GET method creates the html page when this side has been
	redirected from somewhere else.
	**/

	public void doGet(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException
	{
		//log("START BillBoardCreator doGet");
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
			String header = "BillBoardCreator servlet. " ;
			ConfError err = new ConfError(req,res,header,3) ;
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

	protected Properties getNewConfParameters( HttpServletRequest req) throws ServletException, IOException
	{

		Properties confP = new Properties() ;
		String billBoard_name = (req.getParameter("billBoard_name")==null) ? "" : (req.getParameter("billBoard_name")) ;//conference_name
		String section_name = (req.getParameter("section_name")==null) ? "" : (req.getParameter("section_name")) ;//forum_name
	
		confP.setProperty("BILLBOARD_NAME", billBoard_name.trim()) ;
		confP.setProperty("SECTION_NAME", section_name.trim()) ;
		
		//log("BillBoard paramters:" + confP.toString()) ;
		return confP ;
	}

	/**
	Detects paths and filenames.
	*/

	public void init(ServletConfig config) throws ServletException
	{
		super.init(config);
		HTML_TEMPLATE = "BILLBOARD_CREATOR.HTM" ;//CONF_CREATOR.HTM

	} // End of INIT

	/**
	Log function, will work for both servletexec and Apache
	**/

	public void log( String msg)
	{
		super.log("BillBoardCreator: " + msg ) ;
	}


} // End class
