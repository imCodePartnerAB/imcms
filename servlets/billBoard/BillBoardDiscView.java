/*
 *
 * @(#)BillBoardDiscView.java
 *
 * 
 *
 * Copyright (c)
 *
*/

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
 * BillBoard_Disc_View.htm
 *
 * Html parstags in use:
 * #BILLBOARD_DISC#
 * #BILLBOARD_REPLY#
 * 
 * stored procedures in use:
 * - 
 *
 * @version 1.2 20 Aug 2001
 * @author Rickard Larsson REBUILD TO BillBoardDiscView BY Peter Östergren
 *
*/

public class BillBoardDiscView extends BillBoard {//ConfDiscView
	private final static String CVS_REV = "$Revision$" ;
	private final static String CVS_DATE = "$Date$" ;

	String HTML_TEMPLATE ;         // the relative path from web root to where the servlets are

	public void doGet(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException
	{
		//log("START BillBoardDiscView doGet");

		// Lets validate the session, e.g has the user logged in to Janus?
		if (super.checkSession(req,res) == false)
		{	
			log("checkSession = false so return");
			return ;
		}

		// Lets get the standard parameters and validate them
		// Properties params = super.getParameters(req) ;

		// Lets get the standard SESSION parameters and validate them
		Properties params = super.getSessionParameters(req) ;

		if (super.checkParameters(req, res, params) == false)
		{

			String header = "BillBoardViewer servlet. " ;
			String msg = params.toString() ;
			BillBoardError err = new BillBoardError(req,res,header,1) ;
			log("checkParameters = false so return");
			return;
		} 

		// Lets get an user object  
		imcode.server.User user = super.getUserObj(req,res) ;
		if(user == null) return ;

		if ( !isUserAuthorized( req, res, user ) )
		{
			log("isUserAuthorized = false so return");
			return;
		}

		// Lets get the url to the servlets directory
		String servletHome = MetaInfo.getServletPath(req) ;
		
		// Lets build the Responsepage
		VariableManager vm = new VariableManager() ;
		
		
		// Lets get all parameters in a string which we'll send to every
		// servlet in the frameset
		MetaInfo metaInfo = new MetaInfo() ;
		String paramStr = metaInfo.passMeta(params) ;
		String extParam="";
		//now we have an ugly ugly ugly part, I'm gonna rewrite it some time but untill then its ugly
		if (req.getParameter("MAIL_SENT") != null)
		{
			extParam = "&MAIL_SENT=OK";
		}
		
		if (req.getParameter("ADDTYPE")!= null)
		{
			paramStr += "&ADDTYPE="+req.getParameter("ADDTYPE");
		}

		if (req.getParameter("PREVIEWMODE")!=null)
		{
			extParam += "&PREVIEWMODE=OK";
		}
		if (req.getParameter("DISCPREV")!=null)
		{
			vm.addProperty("BILLBOARD_DISC", servletHome + "BillBoardAdd?ADD=ok" + paramStr) ;
		}else
		{
			vm.addProperty("BILLBOARD_DISC", servletHome + "BillBoardDisc?" + paramStr) ;
		
		}
		
		
		vm.addProperty("BILLBOARD_REPLY", servletHome + "BillBoardReply?" + paramStr+extParam) ;


		this.sendHtml(req,res,vm, HTML_TEMPLATE) ;
		//	log("Nu är BillBoardDiscView klar") ;  
		return ;
	}


	/**
	Detects paths and filenames.
	*/

	public void init(ServletConfig config) throws ServletException
	{
		super.init(config);
		HTML_TEMPLATE = "BillBoard_Disc_View.htm" ;//Conf_Disc_View.htm


	}

	/**
	Log function, will work for both servletexec and Apache
	**/

	public void log( String msg)
	{
		super.log("BillBoardDiscView: " + msg) ;

	}
} // End of class
