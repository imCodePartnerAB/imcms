import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import imcode.util.* ;
/**
  Display previous page in search result.
  */
public class SearchDocsPrevPage extends HttpServlet {

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
		String servlet_url        	= Utility.getDomainPref( "servlet_url",host ) ;

		imcode.server.User user ; 
		String htmlStr = "" ;  
		String submit_name = "" ;

		String question_field = "" ;                           	
		String search_type = "" ;  
		String search_area = "" ;  
		String string_match = "" ;                                          	
		String values[] ;  
		int meta_id = 0 ;                         	
		int start = 0 ;

		res.setContentType("text/html");
		PrintWriter out = res.getWriter();

		// get start
		start = Integer.parseInt(req.getParameter("start")) ;

		// get question_field                         	
		values = req.getParameterValues("question_field") ;   
		if (values != null) question_field = values[0] ;

		// get search_type                           	
		values = req.getParameterValues("search_type") ;   
		if (values != null) search_type = values[0] ;

		// get search_area                         	
		values = req.getParameterValues("search_area") ;   
		if (values != null) search_area = values[0] ;

		// get string_match                         	
		values = req.getParameterValues("string_match") ;   
		if (values != null) string_match = values[0] ;                          	



		// Get the session
		HttpSession session = req.getSession(true);

		// Does the session indicate this user already logged in?
		Object done = session.getValue("logon.isDone");  // marker object
		user = (imcode.server.User)done ;

		if (done == null) {
			// No logon.isDone means he hasn't logged in.
			// Save the request URL as the true target and redirect to the login page.      
			session.putValue("login.target",
				HttpUtils.getRequestURL(req).toString());
			String scheme = req.getScheme();
			String serverName = req.getServerName();
			int p = req.getServerPort();
			String port = (p == 80) ? "" : ":" + p;
			res.sendRedirect(scheme + "://" + serverName + port + start_url) ;              
			return ;
		}

		// Check if user has write rights
		/*	if ( !Check.userWriteRights(meta_id, user, start_url) ) {
		log("User "+user.getInt("user_id")+" was denied access to meta_id "+meta_id+" and was sent to "+start_url) ;			
		String scheme = req.getScheme() ;
		String serverName = req.getServerName() ;
		int p = req.getServerPort() ;
		String port = ( p == 80 ) ? "" : ":" + p ;
		res.sendRedirect( scheme + "://" + serverName + port + start_url ) ;
		return ;
		} */

			Vector docs = IMCServiceRMI.searchDocs(imcserver,meta_id,user,question_field,search_type,
			string_match,search_area) ;

		int no_of_docs = docs.size() ;

		if (docs.size() == 0) {
			docs.addElement(" " ) ;
			docs.addElement(" " ) ;
			docs.addElement("Inga dokument hittades!") ;
		}



		htmlStr  = "<HTML><BODY bgcolor=\"#FFFFFF\"><H2><CENTER>S&ouml;kresultat</H2></CENTER>" ;
		htmlStr += "<TABLE BORDER=0 WIDTH=\"75%\" align=\"center\">" ;
		for(int i = start ; i < docs.size() && i < start + 30 ; i+=3) {
			htmlStr += "<TR><TD>" + docs.elementAt(i).toString() + "</TD>" ;
			htmlStr += "<TD>" ;
			if (no_of_docs > 0) {
				htmlStr += "<A HREF=\"" + servlet_url + "GetDoc?meta_id=" ;
				htmlStr += docs.elementAt(i).toString() + "\">" ;
				htmlStr += docs.elementAt(i+1).toString() + "</A>" ;
			} else {
				htmlStr += "&nbsp;" ;
			}

			htmlStr += "</TD></TR>" ;

				String info_text = docs.elementAt(i+2).toString() ;

			if (info_text.length() == 0) {
				info_text = "" ;
			}
			htmlStr += "<TR><TD>&nbsp;</TD><TD>" + info_text  + "</TD></TR>" ;

		}


		int next_start = start + 30 ;
		if (next_start > docs.size() )
			next_start = docs.size() - 30 ;



		int prev_start = start - 30 ;

		log("Prev_start=" + prev_start) ;

		if (prev_start < -30 )
			prev_start = -30 ;


			htmlStr += "<TR><TD></TD><TD></TD></TR>" ;
		htmlStr += "<TR><TD></TD><TD></TD></TR>" ;
		htmlStr += "<TR><TD><H4>("+ (next_start / 3 - 9) + "-" + next_start / 3 + ") " + no_of_docs / 3 + "</H4></TD><TD><H4>dokument hittades.</H4></TD></TR>" ;
		htmlStr += "</TABLE>\n" ;


			htmlStr += "<TABLE BORDER=0 WIDTH=\"75%\" align=\"center\">" ; 
		htmlStr += "<TR><TD>" ;
		if ( prev_start != -30) { 
			htmlStr += "<A HREF=\"" + servlet_url + "SearchDocsPrevPage?start=" + prev_start + "&" ;
			htmlStr += "question_field=" + question_field + "&" ;
			htmlStr += "search_type="    + search_type + "&" ;
			htmlStr += "search_area="    + search_area + "&" ;
			htmlStr += "string_match="   +	string_match + "\">Föregående sida</A>&nbsp;&nbsp;" ;
			htmlStr += "</TD><TD>" ;
		} else {
			htmlStr += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" ;
			htmlStr += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" ;
			htmlStr += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</TD><TD>" ; 
		}



		htmlStr += "<A HREF=\"" + servlet_url + "SearchDocsNextPage?start=" + next_start  + "&" ;
		htmlStr += "question_field=" + question_field + "&" ;
		htmlStr += "search_type="    + search_type + "&" ;
		htmlStr += "search_area="    + search_area + "&" ;
		htmlStr += "string_match="   + string_match + "\">Nästa sida</A>\n" ;
		htmlStr += "</TD></TR>" ;
		htmlStr += "</TABLE>" ;
		htmlStr +=	"</BODY></HTML>" ;


		out.println(htmlStr) ;



	}
}
