import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import imcode.external.diverse.* ;
import java.rmi.* ;
import java.rmi.registry.* ;


/**
 *
 *
 * Html template in use:
 * BillBoard_set.htm
 *
 * Html parstags in use:
 * #BILLBOARD_SECTION#
 * #BILLBOARD_DISC_VIEW#
 * stored procedures in use:
 * -
 *
 * @version 1.2 20 Aug 2001
 * @author Rickard Larsson, Jerker Drottenmyr REBUILD TO BillBoardViewer BY Peter Östergren
 *
*/

public class BillBoardViewer extends BillBoard {//ConfViewer

	String HTML_TEMPLATE ;         // the relative path from web root to where the servlets are

	public void doGet(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException 
	{
		//log("START BillBoardViewer doGet");

		// Lets validate the session, e.g has the user logged in to Janus?
		if (super.checkSession(req,res) == false)	return ;

		// Lets get the standard parameters and validate them
		// Properties params = super.getParameters(req) ;

		// Lets get the standard SESSION parameters and validate them
		Properties params = super.getSessionParameters(req) ;

		if (super.checkParameters(req, res, params) == false) {

			
			String header = "BillBoardViewer servlet. " ;
			String msg = params.toString() ;
			BillBoardError err = new BillBoardError(req,res,header,1) ;
			log("BillBoardViewer error checkParameters == false");
			return;
		}

		// Lets get an user object
		imcode.server.User user = super.getUserObj(req,res) ;
		if (user == null)
		{
			log("user = null so return");
		 	return ;
		}

		if ( !isUserAuthorized( req, res, user ) ) 
		{
			log("user not Authorized so return");
			return;
		}

		// Lets get the url to the servlets directory
		String servletHome = MetaInfo.getServletPath(req) ;

		// Lets get all parameters in a string which we'll send to every servlet in the frameset
		MetaInfo metaInfo = new MetaInfo() ;
		String paramStr = metaInfo.passMeta(params) ;

		// Lets build the Responsepage
		VariableManager vm = new VariableManager() ;
		vm.addProperty("BILLBOARD_SECTION", servletHome + "BillBoardForum?" + paramStr);
		vm.addProperty("BILLBOARD_DISC_VIEW", servletHome + "BillBoardDiscView?" + paramStr ) ;
		this.sendHtml(req,res,vm, HTML_TEMPLATE) ;
		//log("Nu är BillBoardViewer klar") ;
		return ;
	}

	/**
	Detects paths and filenames.
	*/

		public void init(ServletConfig config) throws ServletException {

		super.init(config);
		HTML_TEMPLATE = "BillBoard_set.htm" ;//Conf_set.htm

	}

	/**
	Log function, will work for both servletexec and Apache
	**/

	public void log( String msg) {
		super.log("BillBoardViewer: " + msg) ;
		
	}
} // End of class
