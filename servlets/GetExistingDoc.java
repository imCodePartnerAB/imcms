import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import imcode.util.* ;
import imcode.util.log.* ;

public class GetExistingDoc extends HttpServlet {

	/**
	init()
	*/
	public void init(ServletConfig config) throws ServletException {
		super.init(config) ;
	}

	/**
	doPost()
	*/
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String host 				= req.getHeader("Host") ;
		String imcserver 			= Utility.getDomainPref("adminserver",host) ;
		String start_url        	= Utility.getDomainPref( "start_url",host ) ;

		imcode.server.User user ; 
		String htmlStr = "" ;  
		String submit_name = "" ;                        	
		String values[] ;                           	
		int existing_meta_id = 0 ;

		res.setContentType("text/html");
		ServletOutputStream out = res.getOutputStream();

		// get meta_id
		int meta_id = Integer.parseInt(req.getParameter("meta_id")) ;

		// get submit
		values = req.getParameterValues("submit") ;   
		if (values != null)
			submit_name = values[0] ;

		// Get the session
		HttpSession session = req.getSession(true);

		// Does the session indicate this user already logged in?
		Object done = session.getValue("logon.isDone");  // marker object
		user = (imcode.server.User)done ;

		if (done == null) {
			// No logon.isDone means he hasn't logged in.
			// Save the request URL as the true target and redirect to the login page.
			String scheme = req.getScheme();
			String serverName = req.getServerName();
			int p = req.getServerPort();
			String port = (p == 80) ? "" : ":" + p;
			res.sendRedirect(scheme + "://" + serverName + port + start_url) ;              
			return ;
		}
		
		// doc menu no
		int doc_menu_no = 0 ;
		try {
		    doc_menu_no = Integer.parseInt(req.getParameter("doc_menu_no")) ;
		} catch (NumberFormatException ex) {
		    Log.getLog("errors").log(Log.ERROR, "\"doc_menu_no\" not found in GetExistingDoc.", ex) ;
		    return ;
		}

		user.put("flags",new Integer(262144)) ;
		
		// get existing doc                         	
		try {
			values = req.getParameterValues("existing_meta_id") ;   
			if (values != null)
				existing_meta_id = Integer.parseInt(values[0]) ;
		} catch ( NumberFormatException ex ) {
			byte [] tempbytes = AdminDoc.adminDoc(meta_id,meta_id,host,user,req,res) ;
			out.write ( tempbytes ) ;
			return ;
		}

		String sqlStr = "select shared from meta where meta_id = "+existing_meta_id ;
		
		String shared = IMCServiceRMI.sqlQueryStr(imcserver,sqlStr) ;

		String lang_prefix = IMCServiceRMI.sqlQueryStr(imcserver, "select lang_prefix from lang_prefixes where lang_id = "+user.getInt("lang_id")) ;

		// Fetch all doctypes from the db and put them in an option-list
		// First, get the doc_types the current user may use.
		String[] user_dt = IMCServiceRMI.sqlProcedure(imcserver,"GetDocTypesForUser "+meta_id+","+user.getInt("user_id")+",'"+lang_prefix+"'") ;
		HashSet user_doc_types = new HashSet() ;

		// I'll fill a HashSet with all the doc-types the current user may use,
		// for easy retrieval.
		for ( int i=0 ; i<user_dt.length ; i+=2 ) {
			user_doc_types.add(user_dt[i]) ;
		}

		sqlStr = "select doc_type from meta where meta_id = "+existing_meta_id ;
		
		String doc_type = IMCServiceRMI.sqlQueryStr(imcserver,sqlStr) ;

		// Add the document in menu if user is admin for the document OR the document is shared.
		if ((req.getParameter("ok")!=null) && user_doc_types.contains(doc_type) && ("1".equals(shared) || IMCServiceRMI.checkDocAdminRights(imcserver,existing_meta_id,user))) {
			IMCServiceRMI.addExistingDoc(imcserver,meta_id,user,existing_meta_id,doc_menu_no) ;
		}
//		htmlStr = IMCServiceRMI.interpretTemplate(imcserver,meta_id,user) ;
//		out.println(htmlStr) ;
	
		byte [] tempbytes = AdminDoc.adminDoc(meta_id,meta_id,host,user,req,res) ;
		out.write ( tempbytes ) ;

	}
}
