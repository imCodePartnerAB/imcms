import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import imcode.external.diverse.* ;
import java.rmi.* ;
import java.rmi.registry.* ;

import imcode.external.chat.*;


//meningen är att denna ska ladda framesetet och kolla 
//all nödvändig data innan den gör detta

public class ChatViewer extends ChatBase {

	String HTML_TEMPLATE ;         // the relative path from web root to where the servlets are


	public void doPost(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException
	{
		log("doPost");
		doGet(req,res);
	}

	public void doGet(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException
	{
		log("first line in doGet &lt;");
	
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
		
		HttpSession ses = req.getSession(false);
		
		ses.putValue("ChatMember", super.getChat("1"));
		// Lets get the url to the servlets directory
		String servletHome = MetaInfo.getServletPath(req) ;

		// Lets get all parameters in a string which we'll send to every servlet in the frameset
		MetaInfo metaInfo = new MetaInfo() ;
		String paramStr = metaInfo.passMeta(params) ;
		log("params: "+paramStr);

		// Lets build the Responsepage
		VariableManager vm = new VariableManager() ;
		vm.addProperty("CHAT_MESSAGES", servletHome + "ChatBoard?" + paramStr);
		vm.addProperty("CHAT_CONTROL", servletHome + "ChatControl?" + paramStr ) ;
		this.sendHtml(req,res,vm, HTML_TEMPLATE) ;
		log("Nu är ChatViewer klar") ;
		return ;
	}


	/**
	Detects paths and filenames.
	*/

	public void init(ServletConfig config) throws ServletException
	{

		super.init(config);
		HTML_TEMPLATE = "Chat_Frameset.htm" ;
		
	
		
	}

	/**
	Log function, will work for both servletexec and Apache
	**/

	public void log( String str)
	{
		super.log(str) ;
		System.out.println("ChatViewer: " + str );
	}
	
} // End of class
