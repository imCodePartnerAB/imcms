package imcode.util ;
// Test
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class Check extends Object {

    static public imcode.server.User userLoggedOn (HttpServletRequest req, HttpServletResponse res, String start_url) throws IOException {

		HttpSession session = req.getSession(true) ;
		imcode.server.User user = (imcode.server.User) session.getAttribute("logon.isDone") ;

		if (user == null) {
		//	session.setAttribute("login.target", HttpUtils.getRequestURL(req).toString()) ;

			String scheme = req.getScheme() ;
			String serverName = req.getServerName() ;
			int p = req.getServerPort() ;
			String port = ( p == 80 ) ? "" : ":" + p ;
			res.sendRedirect( scheme + "://" + serverName + port + start_url ) ;
			return null;
		}
		return user;
	}
}
