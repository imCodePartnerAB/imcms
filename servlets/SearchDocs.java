import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.rmi.* ;
import java.rmi.registry.* ;
import imcode.util.* ;
/**
  Search documents
  */
public class SearchDocs extends HttpServlet {

	/**
	init()
	*/
	public void init( ServletConfig config ) throws ServletException {
		super.init( config ) ;
	}


	/**
	doPost()
	*/
	public void doPost( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
		String host 				= req.getHeader("Host") ;
		String imcserver 			= Utility.getDomainPref("adminserver",host) ;
		String start_url        	= Utility.getDomainPref( "start_url",host ) ;
		String servlet_url        	= Utility.getDomainPref( "servlet_url",host ) ;

		imcode.server.User user ; 
		String htmlStr = "" ;  

						String question_field = "" ;                           	
		String search_type = "" ;  
		String search_area = "" ;  
		String string_match = "" ;                                          	
		String values[] ;                           	
		int meta_id = 0 ;



		res.setContentType( "text/html" );
		PrintWriter out = res.getWriter( );

		// get meta_id
		//start_pos = Integer.parseInt(req.getParameter("meta_id")) ;

		// get question_field                         	
		values = req.getParameterValues( "question_field" ) ;   
		if( values != null ) question_field = values[0] ;

		// get search_type                           	
		values = req.getParameterValues( "search_type" ) ;   
		if( values != null ) search_type = values[0] ;

		// get search_area                         	
		values = req.getParameterValues( "search_area" ) ;   
		if( values != null ) search_area = values[0] ;

		// get string_match                         	
		values = req.getParameterValues( "string_match" ) ;   
		if( values != null ) string_match = values[0] ;                          	




		// Get the session
		HttpSession session = req.getSession( true );

		// Does the session indicate this user already logged in?
		Object done = session.getValue( "logon.isDone" );  // marker object
		user = (imcode.server.User)done ;

		if( done == null ) {
			// No logon.isDone means he hasn't logged in.
			// Save the request URL as the true target and redirect to the login page.
			session.putValue( "login.target",
							HttpUtils.getRequestURL( req ).toString( ) );
			String scheme = req.getScheme( );
			String serverName = req.getServerName( );
			int p = req.getServerPort( );
			String port = (p == 80) ? "" : ":" + p;
			res.sendRedirect( scheme + "://" + serverName + port + start_url ) ;              
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



		if( req.getParameter( "search" )!=null ) {
			Vector docs = IMCServiceRMI.searchDocs( imcserver,meta_id,user,question_field,search_type,
							string_match,search_area ) ;

       log("QUIZ=" + question_field + " SEARCH_TYPE=" + search_type + 
	       " SEARCH_MATCH=" + string_match + " SEARCH_AREA=" + search_area) ;
	   
	   int no_of_docs = docs.size( ) ;


			if( docs.size( ) == 0 ) {
				docs.addElement( " " ) ;
				docs.addElement( " " ) ;
				docs.addElement( "Inga dokument hittades!" ) ;
			}

		//	log( docs.toString()) ;
			
			int start = 0 ;
			if( docs.size( ) > 30 )
				start = 30 ;



							htmlStr  = "<HTML><BODY bgcolor=\"#FFFFFF\"><H2><CENTER>S&ouml;kresultat</H2></CENTER>" ;
			htmlStr += "<TABLE BORDER=0 WIDTH=\"75%\" align=\"center\">" ;
			for( int i = 0 ; i < docs.size( ) && i < 30 ; i+=3 ) {


				htmlStr += "<TR><TD>" + docs.elementAt( i ).toString( ) + "</TD>" ;
				htmlStr += "<TD>" ;
				if( no_of_docs > 0 ) {
					htmlStr += "<A HREF=\"" + servlet_url + "GetDoc?meta_id=" ;
					htmlStr += docs.elementAt( i ).toString( ) + "\">" ;
					htmlStr += docs.elementAt( i+1 ).toString( ) + "</A>" ;
				} else {
					htmlStr += "&nbsp;" ;
				}
				htmlStr += "</TD></TR>" ;

								String info_text = docs.elementAt( i+2 ).toString( ) ;

				if( info_text.length( ) == 0 ) {
					info_text = "" ;
				}
				htmlStr += "<TR><TD>&nbsp;</TD><TD>" + info_text  + "</TD></TR>" ;

			}
			htmlStr += "<TR><TD></TD><TD></TD></TR>" ;
			htmlStr += "<TR><TD></TD><TD></TD></TR>" ;
			htmlStr += "<TR><TD><H4>(1-10) " + no_of_docs / 3 + "</H4></TD><TD><H4>dokument hittades.</H4></TD></TR>" ;
			htmlStr += "</TABLE>\n" ;

			if( docs.size( ) > 30 ) {
				htmlStr += "<TABLE BORDER=0 WIDTH=\"75%\" align=\"center\">" ; 
				htmlStr += "<TR><TD>" ;
				/*
						  htmlStr += "<A HREF=\"" + servlet_url + "SearchDocsPrevPage?start=" + 0 + "&" ;
						  htmlStr += "question_field=" + question_field + "&" ;
						  htmlStr += "search_type="    + search_type + "&" ;
						  htmlStr += "search_area="    + search_area + "&" ;
						  htmlStr += "string_match="   +	string_match + "\">Föregående sida</A>" ;
						*/

								htmlStr += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" ;
				htmlStr += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" ;
				htmlStr += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</TD><TD>" ; 


								htmlStr += "<A HREF=\"" + servlet_url + "SearchDocsNextPage?start=" + start + "&" ;
				htmlStr += "question_field=" + question_field + "&" ;
				htmlStr += "search_type="    + search_type + "&" ;
				htmlStr += "search_area="    + search_area + "&" ;
				htmlStr += "string_match="   +	string_match + "\">Nästa sida</A>\n" ;

								htmlStr += "</TD></TR>" ;
				htmlStr += "</TABLE>" ;
			}
			htmlStr +=	"</BODY></HTML>" ;
		} else {
			// do something else

		}



		out.println( htmlStr ) ;



	}
}
