import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.rmi.* ;
import java.rmi.registry.* ;

import imcode.util.* ;
/**
  Edit text in a document.
  */
public class ChangeText extends HttpServlet {

	/**
	init()
	*/
	public void init(ServletConfig config) throws ServletException {
		super.init(config) ;
	}

	/**
	doGet()
	*/
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String host 				= req.getHeader("Host") ;
		String imcserver 			= Utility.getDomainPref("adminserver",host) ;
		String start_url        	= Utility.getDomainPref( "start_url",host ) ;
		String servlet_url       	= Utility.getDomainPref( "servlet_url",host ) ;

		imcode.server.User user ; 
		String htmlStr = "" ;     
		int meta_id ;
		int txt_no ;
		int text_type = 0 ;

		res.setContentType("text/html");
		ServletOutputStream out = res.getOutputStream();
		meta_id = Integer.parseInt(req.getParameter("meta_id")) ;
		txt_no = Integer.parseInt(req.getParameter("txt")) ;
		text_type = Integer.parseInt(req.getParameter("type")) ;

		// Get the session
		HttpSession session = req.getSession(true);

		// Does the session indicate this user already logged in?
		Object done = session.getValue("logon.isDone");  // marker object
		user = (imcode.server.User)done ;

		if (done == null) {
			// No logon.isDone means he hasn't logged in.
			String scheme = req.getScheme();
			String serverName = req.getServerName();
			int p = req.getServerPort();
			String port = (p == 80) ? "" : ":" + p;
			res.sendRedirect(scheme + "://" + serverName + port + start_url) ;              
			return ;
		}

		// Check if user has write rights
		if ( !IMCServiceRMI.checkDocAdminRights(imcserver,meta_id,user,65536 ) ) {	// Checking to see if user may edit this
			byte[] tempbytes ;
			tempbytes = AdminDoc.adminDoc(meta_id,meta_id,host,user,req,res) ;
			if ( tempbytes != null ) {
				out.write(tempbytes) ;
			}
			return ;
		}

//		htmlStr = IMCServiceRMI.interpretAdminTemplate(imcserver,meta_id,user,"change_text.html",txt_no,text_type,0,0) ;          	

		String sqlStr = "select text from texts where meta_id = "+meta_id+" and name = "+txt_no ;
		String temp = IMCServiceRMI.sqlQueryStr(imcserver,sqlStr) ;
		if ( temp == null ) {
			temp = "" ;
		}
		if ( "".equals(temp) ) {
			text_type = 2 ;
		}
		
		Vector vec = new Vector() ;
		if ( (text_type & 1) != 0 ) {
			vec.add("#html#") ;
			vec.add("checked") ;
		} else {
			String [] tags = {"\r",	"",
							  "\n", "",
							  "<BR>",	"\r\n"
							 } ;
			temp = Parser.parseDoc(temp,tags) ;
			vec.add("#!html#") ;
			vec.add("checked") ;
		}
		vec.add("#type#") ;
		vec.add(String.valueOf(text_type)) ;
		vec.add("#txt#") ;
		vec.add(temp) ;
		vec.add("#meta_id#") ;
		vec.add(String.valueOf(meta_id)) ;
		vec.add("#servlet_url#") ;
		vec.add(servlet_url) ;
		vec.add("#txt_no#") ;
		vec.add(String.valueOf(txt_no)) ;
		String lang_prefix = IMCServiceRMI.sqlQueryStr(imcserver, "select lang_prefix from lang_prefixes where lang_id = "+user.getInt("lang_id")) ;
		out.print(IMCServiceRMI.parseDoc(imcserver,vec,"change_text.html",lang_prefix)) ;
	}
}
