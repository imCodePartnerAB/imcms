import java.io.*;
import java.util.*;
import java.rmi.* ;
import java.rmi.registry.* ;
import javax.servlet.*;
import javax.servlet.http.*;
import imcode.util.Utility;
import imcode.external.diverse.* ;
import imcode.server.* ;
import imcode.util.* ;

public class ChatHelp extends ChatBase
{
    private final static String CVS_REV = "$Revision$" ;
    private final static String CVS_DATE = "$Date$" ;

    private final static String USER_TEMPLATE = "Conf_help_user.htm";
    private final static String ADMIN_TEMPLATE = "Conf_help_admin.htm";
    private final static String ADMIN_TEMPLATE2 = "Conf_help_admin2.htm";

    public void doPost(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException
    {

	// Lets validate the session, e.g has the user logged in to Janus?
	if (super.checkSession(req,res) == false)	return ;

	// Lets get all parameters for this servlet
	Properties params = this.getParameters(req) ;
	if (super.checkParameters(req, res, params) == false)
	    {
		return ;
	    }

	// Lets get the user object
	imcode.server.user.UserDomainObject user = super.getUserObj(req,res) ;
	if(user == null) return ;

	if ( isUserAuthorized( req, res, user ) )
	    {

		// Lets get serverinformation
		String host = req.getHeader("Host") ;


		// Lets detect which helparea the user wants

		// Lets get a VariableManager
		VariableManager vm = new VariableManager() ;

		String file = "";

		// Lets create the path to our html page
		if ( params.getProperty("HELP_MODE").equalsIgnoreCase("USER") )
		    {
			file = USER_TEMPLATE	;
		    }
		else if ( params.getProperty("HELP_MODE").equalsIgnoreCase("ADMIN") )
		    {

			//lets se if user has adminrights
			String metaId = getMetaId( req );
			IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface(req) ;
			if ( metaId != null && userHasAdminRights( imcref, Integer.parseInt( metaId ), user ) )
			    {
				file = ADMIN_TEMPLATE ;
				if( params.getProperty("HELP_AREA").equalsIgnoreCase("TEMPLATESPEC") )
				    {
					file = ADMIN_TEMPLATE2;
				    }
			    }
			else
			    {
				String header = "ConfHelp servlet. " ;
				String msg = params.toString() ;
				ChatError err = new ChatError( req, res, header, 6 );
				return ;
			    }
		    }

		//if( params.getProperty("HELP_SPEC").equalsIgnoreCase("SPEC") ) file = ADMIN_TEMPLATE2 ;
		//			this.sendHtml(req,res,vm, file) ;
		this.sendHtml(req,res,new Vector(), file, null) ;
		return ;

	    }
	else
	    {
		return;
	    }

    } //DoPost

    /**
       Collects all the parameters used by this servlet
    **/

    public Properties getParameters( HttpServletRequest req)
	throws ServletException, IOException
    {

	Properties params = super.getSessionParameters(req) ;

	// Lets get the EXTENDED SESSION PARAMETERS
	super.getExtSessionParameters(req, params) ;

	// Lets get our REQUESTPARAMETERS
	String helpInfo = (req.getParameter("helparea")==null) ? "" : (req.getParameter("helparea")) ;
	String helpMode = (req.getParameter("helpmode")==null) ? "" : (req.getParameter("helpmode")) ;
	//String helpSpec = (req.getParameter("helpspec")==null) ? "-1" : (req.getParameter("helpspec")) ;

	//params.setProperty("HELP_SPEC", helpSpec) ;
	params.setProperty("HELP_AREA", helpInfo) ;
	params.setProperty("HELP_MODE", helpMode) ;
	//log(req.getParameter("helpmode"));
	return params ;
    }

    /**
       Service method. Sends the user to the post method
    **/

    public void service (HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException
    {

	String action = req.getMethod() ;
	// log("Action:" + action) ;
	if(action.equals("POST"))
	    this.doPost(req,res) ;
	else
	    this.doPost(req,res) ;
    }


    /**
       Init
    **/

    public void init(ServletConfig config) throws ServletException
    {
	super.init(config);
    }

    /**
       Log function, will work for both servletexec and Apache
    **/

    public void log( String str)
    {
	super.log("ChatHelp: " +  str) ;
    }

} // End of class
