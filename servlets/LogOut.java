import java.io.* ;
import java.util.* ;
import javax.servlet.* ;
import javax.servlet.http.* ;

import imcode.util.* ;
import imcode.server.* ;

public class LogOut extends HttpServlet {

    /**
	doGet()
	*/
	public void doGet ( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
		String host 				= req.getHeader("Host") ;
		IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface(req) ;
		String login_url        	= Utility.getDomainPref( "admin_url",host ) ;
		String start_url        	= imcref.getStartUrl() ;
		res.setContentType("text/html") ;
		ServletOutputStream out = res.getOutputStream () ;
		HttpSession session = req.getSession (true) ;
		Object done = session.getAttribute("logon.isDone");  // marker object
		imcode.server.User user = (imcode.server.User)done ;
		if ( user == null ) {
			res.sendRedirect(start_url) ;              
			return ;
		}
		Vector vec = new Vector() ;
		vec.add("#start#") ;
		vec.add(start_url) ;
		vec.add("#login#") ;
		vec.add(login_url) ;
		String htmlStr = imcref.parseDoc(vec,"logged_out.html",user.getLangPrefix()) ;
		session.invalidate() ;
		out.print(htmlStr) ;
	}
}
