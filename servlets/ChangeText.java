import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.rmi.* ;
import java.rmi.registry.* ;

import imcode.util.* ;
import imcode.server.IMCText ;
/**
  Edit text in a document.
  */
public class ChangeText extends HttpServlet {
	private final static String CVS_REV = "$Revision$" ;
	private final static String CVS_DATE = "$Date$" ;

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
		String host				= req.getHeader("Host") ;
		String imcserver			= Utility.getDomainPref("adminserver",host) ;
		String start_url	= Utility.getDomainPref( "start_url",host ) ;
		String servlet_url	= Utility.getDomainPref( "servlet_url",host ) ;

		imcode.server.User user ;

		res.setContentType("text/html");
		ServletOutputStream out = res.getOutputStream();
		int meta_id = Integer.parseInt(req.getParameter("meta_id")) ;
		int txt_no = Integer.parseInt(req.getParameter("txt")) ;

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
		    byte[] tempbytes = AdminDoc.adminDoc(meta_id,meta_id,host,user,req,res) ;
		    if ( tempbytes != null) {
			out.write(tempbytes) ;
		    }
		    return ;
		}

		IMCText text = IMCServiceRMI.getText(imcserver,meta_id,txt_no) ;

		if ( null == text) {
		    text = new IMCText("",IMCText.TEXT_TYPE_PLAIN) ;
		}

		String[] tags = {
		    "&", "&amp;",
		    "<", "&lt;",
		    ">", "&gt;",
		    "\"", "&quot;",
		    "'", "&apos;",
		} ;
		String text_string = Parser.parseDoc(text.getText(),tags) ;

		Vector vec = new Vector() ;
		if ( text.getType() == IMCText.TEXT_TYPE_HTML ) {
			vec.add("#html#") ;
			vec.add("checked") ;
		} else {
			vec.add("#!html#") ;
			vec.add("checked") ;
		}
		vec.add("#type#") ;
		vec.add(String.valueOf(text.getType())) ;
		vec.add("#txt#") ;
		vec.add(text_string) ;
		vec.add("#meta_id#") ;
		vec.add(String.valueOf(meta_id)) ;
		vec.add("#servlet_url#") ;
		vec.add(servlet_url) ;
		vec.add("#txt_no#") ;
		vec.add(String.valueOf(txt_no)) ;
		out.print(IMCServiceRMI.parseDoc(imcserver,vec,"change_text.html",user.getLangPrefix())) ;
	}
}
