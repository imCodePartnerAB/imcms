import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import imcode.external.diverse.* ;
import java.rmi.* ;
import java.rmi.registry.* ;

//peter h�ller p� och jobbar i denna
//meningen �r att denna ska ladda framesetet och kolla 
//all n�dv�ndig data innan den g�r detta

public class ChatViewer extends ChatBase {

	String HTML_TEMPLATE ;         // the relative path from web root to where the servlets are

	public void doGet(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException
	{
		log("first line in doGet");
		
		// Lets validate the session, e.g has the user logged in to Janus?
		if (super.checkSession(req,res) == false)	return ;

		// Lets get the standard parameters and validate them
		// Properties params = super.getParameters(req) ;

		// Lets get the standard SESSION parameters and validate them
		Properties params = super.getSessionParameters(req) ;

		if (super.checkParameters(req, res, params) == false)
		{

			/*
			String header = "ConfViewer servlet. " ;
			String msg = params.toString() ;
			ConfError err = new ConfError(req,res,header,1) ;
			*/
			return;
		}

		// Lets get an user object
		imcode.server.User user = super.getUserObj(req,res) ;
		if(user == null) return ;

		if ( !isUserAuthorized( req, res, user ) )
		{
			return;
		}

		// Lets get the url to the servlets directory
		String servletHome = MetaInfo.getServletPath(req) ;

		// Lets get all parameters in a string which we'll send to every servlet in the frameset
		MetaInfo metaInfo = new MetaInfo() ;
		String paramStr = metaInfo.passMeta(params) ;
		log("params: "+paramStr);

		// Lets build the Responsepage
		VariableManager vm = new VariableManager() ;
		vm.addProperty("CONF_FORUM", servletHome + "ConfForum?" + paramStr);
		vm.addProperty("CONF_DISC_VIEW", servletHome + "ConfDiscView?" + paramStr ) ;
		this.sendHtml(req,res,vm, HTML_TEMPLATE) ;
		//log("Nu �r ConfViewer klar") ;
		return ;
	}

	/**
	Detects paths and filenames.
	*/

	public void init(ServletConfig config) throws ServletException
	{

		super.init(config);
		HTML_TEMPLATE = "Conf_set.htm" ;

		/*
		HTML_TEMPLATE = getInitParameter("html_template") ;

		if( HTML_TEMPLATE == null) {
		    Enumeration initParams = getInitParameterNames();
		    System.err.println("ConfReply: The init parameters were: ");
		    while (initParams.hasMoreElements()) {
		System.err.println(initParams.nextElement());
		    }
		    System.err.println("DiagramViewer: Should have seen one parameter name");
		    throw new UnavailableException (this,
		"Not given a path to the asp diagram files");
		}

		 // this.log("HtmlTemplate:" + getInitParameter("html_template")) ;
		  */

	}

	/**
	Log function, will work for both servletexec and Apache
	**/

	public void log( String str)
	{
		super.log(str) ;
		System.out.println("ConfViewer: " + str ) ;
	}
} // End of class
