import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import imcode.util.* ;
/**
  Save a new urldocument.
  */
public class SaveNewUrlDoc extends HttpServlet {
	private final static String CVS_REV = "$Revision$" ;
	private final static String CVS_DATE = "$Date$" ;
	/**
	init()
	*/
	public void init(ServletConfig config) throws ServletException {
		super.init(config) ;
	}


	/**
	doPost()
	*/
	public void doPost(HttpServletRequest req, HttpServletResponse res)	throws ServletException, IOException {
		String host 				= req.getHeader("Host") ;
		String imcserver 			= Utility.getDomainPref("adminserver",host) ;
		String start_url        	= Utility.getDomainPref( "start_url",host ) ;

		imcode.server.User user ; 
		String htmlStr = "" ;  

		res.setContentType("text/html");
		ServletOutputStream out = res.getOutputStream();

		// get meta_id
		String meta_id = req.getParameter("meta_id") ;
		// get new_meta_id
		String new_meta_id = req.getParameter("new_meta_id") ; 
		// get url_ref                         	
		String url_ref = req.getParameter("url_ref") ;   

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

		String target = req.getParameter("target") ;
		if ( "_other".equals(target) ) {
			target = req.getParameter("frame_name") ;
		}

		if (req.getParameter("cancel")!=null) {
//			htmlStr = IMCServiceRMI.interpretTemplate(imcserver,Integer.parseInt(meta_id),user) ;
			byte[] tempbytes = AdminDoc.adminDoc(Integer.parseInt(meta_id),Integer.parseInt(meta_id),host,user,req,res) ;
			if ( tempbytes != null ) {
				out.write(tempbytes) ;
			}
			return ;
//			out.print(htmlStr) ;
//			return ;
		}
		//IMCServiceRMI.saveNewUrlDoc(imcserver,new_meta_id,user,doc) ;
		String sqlStr = "insert into url_docs (meta_id, frame_name,target,url_ref,url_txt,lang_prefix)\n"+
			"values ("+new_meta_id+",'','','"+url_ref+"','','se')\n"+
			"update meta set activate = 1, target = '"+target+"' where meta_id = "+new_meta_id ;
		log (sqlStr) ;
		IMCServiceRMI.sqlUpdateQuery(imcserver,sqlStr) ;

//		htmlStr = IMCServiceRMI.interpretTemplate(imcserver,Integer.parseInt(meta_id),user) ;
		byte[] tempbytes = AdminDoc.adminDoc(Integer.parseInt(new_meta_id),Integer.parseInt(new_meta_id),host,user,req,res) ;
		if ( tempbytes != null ) {
			out.write(tempbytes) ;
		}
//		return ;
//		out.print(htmlStr) ;
	}
}
