import imcode.server.* ;
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import imcode.external.diverse.* ;
import imcode.util.* ;

public class ConfForum extends Conference {
    private final static String ADMIN_LINK_TEMPLATE = "Conf_Forum_Admin_Link.htm";

    private String HTML_TEMPLATE ;
    private String HTML_TEMPLATE_EXT ;
    // will be placed.

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

	// Lets validate the session, e.g has the user logged in to Janus?
	if (super.checkSession(req,res) == false)	return ;

	String htmlFile = HTML_TEMPLATE ;
	if(req.getParameter("advancedView") != null) htmlFile = HTML_TEMPLATE_EXT ;

	// Lets get an user object
	imcode.server.User user = super.getUserObj(req,res) ;
	if(user == null) return ;

	if ( !isUserAuthorized( req, res, user ) ) {
	    return;
	}

	HttpSession session = req.getSession(false) ;
	String aMetaId = (String) session.getAttribute("Conference.meta_id") ;

	// Lets get serverinformation
	IMCPoolInterface confref = IMCServiceRMI.getConfIMCPoolInterface(req) ;

	// Lets get the information from DB
    String sqlAnswer[] = confref.sqlProcedure( "A_GetAllForum", new String[]{aMetaId} ) ;
	Vector forumV = super.convert2Vector(sqlAnswer) ;

	// Lets fill the select box
	String forumList = Html.createHtmlCode("ID_OPTION", "", forumV ) ;

	// Lets build the Responsepage
	VariableManager vm = new VariableManager() ;
	vm.addProperty( "FORUM_LIST", forumList ) ;
	vm.addProperty( "ADMIN_LINK_HTML", ADMIN_LINK_TEMPLATE );

	this.sendHtml(req,res,vm, htmlFile) ;
	return ;
    }

    public void service (HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
	this.doPost(req,res) ;
    }



    /**
       Detects paths and filenames.
    */
    public void init(ServletConfig config) throws ServletException {
	super.init(config);
	HTML_TEMPLATE = "Conf_Forum.htm" ;
	HTML_TEMPLATE_EXT = "Conf_Forum_ext.htm" ;
    } // End init

} // End of class
