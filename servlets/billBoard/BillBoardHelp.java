import imcode.server.* ;
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import imcode.external.diverse.* ;

public class BillBoardHelp extends BillBoard {//ConfHelp

    private final static String USER_TEMPLATE = "BillBoard_help_user.htm";//Conf_help_user.htm
    private final static String ADMIN_TEMPLATE = "BillBoard_help_admin.htm";//Conf_help_admin.htm
    private final static String ADMIN_TEMPLATE2 = "BillBoard_help_admin2.htm";// Conf_help_admin2.htm

    public void doPost(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {

	// Lets validate the session, e.g has the user logged in to Janus?
	if (super.checkSession(req,res) == false)	return ;

	// Lets get all parameters for this servlet
	Properties params = this.getParameters(req) ;
        if (true == false) {
	    return ;
	}

	// Lets get the user object
	imcode.server.user.UserDomainObject user = super.getUserObj(req,res) ;
	if(user == null) return ;

	if ( isUserAuthorized( req, res, user ) ) {

	    // Lets get serverinformation

        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface() ;

	    // Lets get a VariableManager
	    VariableManager vm = new VariableManager() ;

	    String file = "";

	    // Lets create the path to our html page
	    if ( params.getProperty("HELP_MODE").equalsIgnoreCase("USER") ) {
		file = USER_TEMPLATE	;
	    } else if ( params.getProperty("HELP_MODE").equalsIgnoreCase("ADMIN") ) {

		//lets see if user has adminrights
		int metaId = getMetaId( req );
		if ( userHasAdminRights( imcref, metaId, user ) ) {
		    file = ADMIN_TEMPLATE ;
		    if( params.getProperty("HELP_AREA").equalsIgnoreCase("TEMPLATESPEC") ) {
			file = ADMIN_TEMPLATE2;
		    }
		} else {
		    String header = "BillBoardHelp servlet. " ;
		    new BillBoardError( req, res, header, 6 );
		    return ;
		}
	    }

	    //if( params.getProperty("HELP_SPEC").equalsIgnoreCase("SPEC") ) file = ADMIN_TEMPLATE2 ;
	    this.sendHtml(req,res,vm, file) ;
	    return ;

	} else {
	    return;
	}

    } //DoPost

    /**
       Collects all the parameters used by this servlet
    **/

    private Properties getParameters( HttpServletRequest req) {

	Properties params = MetaInfo.createPropertiesFromMetaInfoParameters(super.getBillBoardSessionParameters(req)) ;

	// Lets get the EXTENDED SESSION PARAMETERS
	super.addExtSessionParametersToProperties(req, params) ;

	// Lets get our REQUESTPARAMETERS
	String helpInfo = (req.getParameter("helparea")==null) ? "" : (req.getParameter("helparea")) ;
	String helpMode = (req.getParameter("helpmode")==null) ? "" : (req.getParameter("helpmode")) ;

	params.setProperty("HELP_AREA", helpInfo) ;
	params.setProperty("HELP_MODE", helpMode) ;
	log(req.getParameter("helpmode"));
	return params ;
    }

    /**
       Service method. Sends the user to the post method
    **/

    public void service (HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {

	String action = req.getMethod() ;
	// log("Action:" + action) ;
	if(action.equals("POST"))
	    this.doPost(req,res) ;
	else
	    this.doPost(req,res) ;
    }

    /**
       Log function, will work for both servletexec and Apache
    **/

    public void log( String msg) {
	super.log("BillBoardHelp: " +  msg) ;

    }

} // End of class
