import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import imcode.external.diverse.* ;
import java.rmi.* ;
import java.rmi.registry.* ;
import imcode.util.* ;

/**
 * 
 *
 * Html template in use:
 * BillBoard_Section.htm
 * BillBoard_Section_ext.htm
 * BillBoardSectionSnippet.htm not in use for the moment
 *
 * Html parstags in use:
 * #SECTION_LIST#
 * #ADMIN_LINK_HTML#
 * 
 * stored procedures in use:
 * B_GetAllSection
 *
 * @version 1.2 20 Aug 2001
 * @author Rickard Larsson REBUILD TO BillBoardForum BY Peter Östergren
 *
*/

public class BillBoardForum extends BillBoard {//ConfForum

	private final static String ADMIN_LINK_TEMPLATE = "BillBoard_Section_Admin_Link.htm";//Conf_Forum_Admin_Link.htm

	String HTML_TEMPLATE ;
	String HTML_TEMPLATE_EXT ;
	String A_HREF_HTML ;   // The code snippet where the aHref list with all discussions
	// will be placed.

	public void doPost(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException 
	{
		//log("START BillBoardForum doPost");

		//log( "Forum" );
		// Lets validate the session, e.g has the user logged in to Janus?
		if (super.checkSession(req,res) == false)	return ;

		// Lets get the standard parameters and validate them
		Properties params = super.getSessionParameters(req) ;
		if (super.checkParameters(req, res, params) == false) {
			
			String header = "BillBoardForum servlet. " ;
			String msg = params.toString() ;
			BillBoardError err = new BillBoardError(req,res,header,1) ;
			
			return;
		}


		String htmlFile = HTML_TEMPLATE ;
		if(req.getParameter("advancedView") != null) htmlFile = HTML_TEMPLATE_EXT ;

		// 	log("Parametrar var: " + params.toString()) ;

		// Lets get an user object
		imcode.server.User user = super.getUserObj(req,res) ;
		if(user == null) return ;

		if ( !isUserAuthorized( req, res, user ) ) {
			return;
		}

		RmiConf rmi = new RmiConf(user) ;
		HttpSession session = req.getSession(false) ;
		String aMetaId = (String) session.getValue("BillBoard.meta_id") ;
		String aSectionId = (String) session.getValue("BillBoard.section_id") ;
		String discIndex = params.getProperty("DISC_INDEX") ;

		// Lets get the url to the servlets directory
		String servletHome = MetaInfo.getServletPath(req) ;

		// Lets get serverinformation
		String host = req.getHeader("Host") ;
		String imcServer = Utility.getDomainPref("userserver",host) ;
		String confPoolServer = Utility.getDomainPref("billboard_server",host) ;

		// Lets get the information from DB
		String sqlStoredProc = "B_GetAllSection " + aMetaId ;
		String sqlAnswer[] = rmi.execSqlProcedure(confPoolServer, sqlStoredProc ) ;
		Vector sectionV = super.convert2Vector(sqlAnswer) ;

		// Lets fill the select box
		String sectionList = Html.createHtmlCode("ID_OPTION", "", sectionV ) ;

		// Lets build the Responsepage
		VariableManager vm = new VariableManager() ;
		vm.addProperty( "SECTION_LIST", sectionList ) ;
		vm.addProperty( "ADMIN_LINK_HTML", this.ADMIN_LINK_TEMPLATE );

		this.sendHtml(req,res,vm, htmlFile) ;
		//log("ConfForum OK") ;
		return ;
	}

	public void service (HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {

		String action = req.getMethod() ;
		//	log("Action:" + action) ;
		if(action.equals("POST")) {
			this.doPost(req,res) ;
		}
		else {
			this.doPost(req,res) ;
		}
	}



	/**
	Detects paths and filenames.
	*/

		public void init(ServletConfig config) throws ServletException {
		super.init(config);
		HTML_TEMPLATE = "BillBoard_Section.htm" ;//Conf_Forum.htm
		HTML_TEMPLATE_EXT = "BillBoard_Section_ext.htm" ;//Conf_Forum_ext.htm
		A_HREF_HTML = "BillBoardSectionSnippet.htm" ;//ConfForumSnippet.htm
	} // End init

	/**
	Log function, will work for both servletexec and Apache
	**/

	public void log( String msg) {
		super.log("BillBoardForum: " + msg) ;
		
	}
} // End of class
