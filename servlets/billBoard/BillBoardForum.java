
import imcode.server.*;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import imcode.external.diverse.*;

/**
 * Html template in use:
 * BillBoard_Section.htm
 * BillBoard_Section_ext.htm
 * BillBoardSectionSnippet.htm not in use for the moment
 * <p/>
 * Html parstags in use:
 * #SECTION_LIST#
 * #ADMIN_LINK_HTML#
 * <p/>
 * stored procedures in use:
 * B_GetAllSection
 * 
 * @author Rickard Larsson REBUILD TO BillBoardForum BY Peter Östergren
 * @version 1.2 20 Aug 2001
 */

public class BillBoardForum extends BillBoard {//ConfForum

    private final static String ADMIN_LINK_TEMPLATE = "BillBoard_Section_Admin_Link.htm";//Conf_Forum_Admin_Link.htm

    private final static String HTML_TEMPLATE = "BillBoard_Section.htm";
    private final static String HTML_TEMPLATE_EXT= "BillBoard_Section_ext.htm" ;

	public void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

		// Lets validate the session, e.g has the user logged in to Janus?
		if (super.checkSession(req,res) == false)	return ;

		String htmlFile = HTML_TEMPLATE ;
		if(req.getParameter("advancedView") != null) htmlFile = HTML_TEMPLATE_EXT ;

		// 	log("Parametrar var: " + params.toString()) ;

		// Lets get an user object
		imcode.server.user.UserDomainObject user = super.getUserObj(req,res) ;
		if(user == null) return ;

		if ( !isUserAuthorized( req, res, user ) ) {
			return;
		}

		HttpSession session = req.getSession(false) ;
		String aMetaId = (String) session.getAttribute("BillBoard.meta_id") ;

		// Lets get serverinformation
        IMCPoolInterface billref = ApplicationServer.getIMCPoolInterface();

		// Lets get the information from DB
        String sqlAnswer[] = billref.sqlProcedure("B_GetAllSection", new String[]{aMetaId});
		Vector sectionV = super.convert2Vector(sqlAnswer) ;

		// Lets fill the select box
		String sectionList = Html.createHtmlOptionList( "", sectionV ) ;

		// Lets build the Responsepage
		VariableManager vm = new VariableManager() ;
		vm.addProperty( "SECTION_LIST", sectionList ) ;
		vm.addProperty( "ADMIN_LINK_HTML", ADMIN_LINK_TEMPLATE );

		this.sendHtml(req,res,vm, htmlFile) ;

		return ;
	}

	public void service (HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {

		String action = req.getMethod() ;

        if (action.equals("POST")) {
            this.doPost(req, res);
        } else {
            this.doPost(req, res);
		}
	}

	/**
	Log function, will work for both servletexec and Apache
	**/

	public void log( String msg) {
		super.log("BillBoardForum: " + msg) ;
	}

} // End of class
