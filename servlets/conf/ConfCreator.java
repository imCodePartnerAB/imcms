import imcode.server.* ;
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import imcode.external.diverse.*;
import imcode.util.* ;

public class ConfCreator extends Conference {
    private final static String CVS_REV = "$Revision$" ;
    private final static String CVS_DATE = "$Date$" ;
    String HTML_TEMPLATE = "Conf_Creator.htm" ;

    /**
       The POST method creates the html page when this side has been
       redirected from somewhere else.
    **/

    public void doPost(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {

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

	if ( !isUserAuthorized( req, res, user ) ) {
	    return;
	}

	String action = req.getParameter("action") ;
	if(action == null) {
	    action = "" ;
	    String header = "ConfCreator servlet. " ;
	    ConfError err = new ConfError(req,res,header,3) ;
	    log(header + err.getErrorMsg()) ;
	    return ;
	}

	// Lets get serverinformation
	IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface(req) ;
	IMCPoolInterface confref = IMCServiceRMI.getConfIMCPoolInterface(req) ;

	// ********* NEW ********
	if(action.equalsIgnoreCase("ADD_CONF")) {
	    log("OK, nu skapar vi konferens") ;

	    // Added 000608
	    // Ok, Since the conference db can be used from different servers
	    // we have to check when we add a new conference that such an meta_id
	    // doesnt already exists.
	    String metaId = params.getProperty("META_ID") ;
	    String foundMetaId = confref.sqlProcedureStr("A_FindMetaId " + metaId) ;
	    if(!foundMetaId.equals("1")) {
		action = "" ;
		String header = "ConfCreator servlet. " ;
		ConfError err = new ConfError(req,res,header,90) ;
		log(header + err.getErrorMsg()) ;
		return ;
	    }

	    // Lets add a new Conference to DB
	    // AddNewConf @meta_id int, @confName varchar(255)
	    String confName = confParams.getProperty("CONF_NAME") ;
	    // String sortType = "1" ;	// Default value, unused so far
	    String sqlQ = "A_AddNewConf " + metaId + ", '" + confName + "'" ;
	    confref.sqlUpdateProcedure(sqlQ) ;

	    // Lets add a new forum to the conference
	    String newFsql = "A_AddNewForum " + metaId +", '" + confParams.getProperty("FORUM_NAME") + "', ";
	    newFsql += "'A' , 30" ;
	    confref.sqlUpdateProcedure(newFsql) ;

	    // Lets get the administrators user_id
	    String user_id = ""+user.getUserId() ;

	    // Lets get the recently added forums id
	    String forum_id = confref.sqlProcedureStr("A_GetFirstForum " + metaId) ;

	    // Lets add this user into the conference if hes not exists there before were
	    // adding the discussion
	    String confUsersAddSql = "A_ConfUsersAdd "+ user_id +", "+ metaId +", '"+ user.getFirstName() + "', '"
		+ user.getLastName() + "'";
	    confref.sqlUpdateProcedure(confUsersAddSql) ;

	    // Ok, were done creating the conference. Lets tell the system to show this child.
	    imcref.activateChild(Integer.parseInt(metaId), user) ;

	    // Ok, Were done adding the conference, Lets go back to the Manager
	    String loginPage = "ConfLogin?login_type=login" ;
	    res.sendRedirect(loginPage) ;
	    return ;
	}

    } // End POST


    /**
       The GET method creates the html page when this side has been
       redirected from somewhere else.
    **/

    public void doGet(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {

	// Lets validate the session, e.g has the user logged in to Janus?
	if (super.checkSession(req,res) == false)	return ;

	// Lets get the standard parameters and validate them
	Properties params = super.getSessionParameters(req) ;
	if (super.checkParameters(req, res, params) == false) return ;

	// Lets get an user object
	imcode.server.User user = super.getUserObj(req,res) ;
	if(user == null) return ;

	if ( !isUserAuthorized( req, res, user ) ) {
	    return;
	}

	String action = req.getParameter("action") ;
	if(action == null) {
	    action = "" ;
	    String header = "ConfCreator servlet. " ;
	    ConfError err = new ConfError(req,res,header,3) ;
	    log(header + err.getErrorMsg()) ;
	    return ;
	}

	// ********* NEW ********
	if(action.equalsIgnoreCase("NEW")) {
	    // Lets build the Responsepage to the loginpage
	    VariableManager vm = new VariableManager() ;
	    vm.addProperty("SERVLET_URL", "") ;
	    sendHtml(req,res,vm, HTML_TEMPLATE) ;
	    return ;
	}
    } // End doGet


    /**
       Collects the parameters from the request object
    **/

    protected Properties getNewConfParameters( HttpServletRequest req) throws ServletException, IOException {

	Properties confP = new Properties() ;
	String conf_name = (req.getParameter("conference_name")==null) ? "" : (req.getParameter("conference_name")) ;
	String forum_name = (req.getParameter("forum_name")==null) ? "" : (req.getParameter("forum_name")) ;

	confP.setProperty("CONF_NAME", conf_name.trim()) ;
	confP.setProperty("FORUM_NAME", forum_name.trim()) ;
	return confP ;
    }

    /**
       Detects paths and filenames.
    */

    public void init(ServletConfig config) throws ServletException {
	super.init(config);

    } // End of INIT

    /**
       Log function, will work for both servletexec and Apache
    **/

    public void log( String str) {
	super.log(str) ;
	System.out.println("ConfCreator: " + str ) ;
    }


} // End class
