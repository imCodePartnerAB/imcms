import java.io.* ;
import java.net.* ;
import java.util.* ;

import javax.servlet.* ;
import javax.servlet.http.* ;

import imcode.util.* ;

import org.apache.log4j.Category;

public class UrlDocTest extends HttpServlet {
	private final static String CVS_REV = "$Revision$" ;
	private final static String CVS_DATE = "$Date$" ;
	
	private static Category log = Category.getInstance(UrlDocTest.class.getName());

	public void init(ServletConfig config) throws ServletException {
		super.init(config) ;
	}

	public void doGet ( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
		String host 				= req.getHeader("Host") ;
		String imcserver 			= Utility.getDomainPref("adminserver",host) ;
		String start_url        	= Utility.getDomainPref( "start_url",host ) ;

		String sqlStr = "select meta_id, url_ref from url_docs order by meta_id" ;
		Hashtable hash = IMCServiceRMI.sqlQueryHash(imcserver,sqlStr) ;
		String [] meta_id = (String[])hash.get("meta_id") ;
		String [] url_ref = (String[])hash.get("url_ref") ;
		res.setContentType("text/html") ;
		ServletOutputStream out = res.getOutputStream() ;

		imcode.server.User user ;
		// Check if user logged on
		if( (user=Check.userLoggedOn( req,res,start_url ))==null ) {
			return ;
		}
		// Is user superadmin?

		sqlStr  = "select role_id from users,user_roles_crossref\n" ;
		sqlStr += "where users.user_id = user_roles_crossref.user_id\n" ;
		sqlStr += "and user_roles_crossref.role_id = 0\n" ;
		sqlStr += "and users.user_id = " + user.getInt("user_id") ;

		if ( IMCServiceRMI.sqlQuery(imcserver,sqlStr).length == 0 ) {
			Utility.redirect(req,res,start_url) ;
			return ;
		}

		String lang_prefix = IMCServiceRMI.sqlQueryStr(imcserver, "select lang_prefix from lang_prefixes where lang_id = "+user.getInt("lang_id")) ;

		out.print(IMCServiceRMI.parseDoc(imcserver,null,"UrlDocTestHead.html",lang_prefix)) ;
		out.flush() ;
		for ( int i = 0; meta_id != null && i<meta_id.length ; i++ ) {
			String found = "green", reached = "green", ok = "red" ;
			String tmp = url_ref[i] ;
			URL url = null ;
			String reslt = null ;
			try {
				if ( tmp.indexOf("://") == -1 ) {
					tmp = "http://"+url_ref[i] ;
					if ( tmp.length()==7 ) {
						url_ref[i] = "&nbsp;" ;
						throw new UnknownHostException () ;
					}
				}
				url = new URL (tmp) ;
				/*
				conn = (HttpURLConnection) url.openConnection() ;
				conn.setRequestMethod("HEAD") ;
				conn.setRequestProperty("Host",url.getHost()) ;
				conn.connect() ;
				*/
			//	log ("Host: "+url.getHost()) ;
				reslt = testUrl(url) ;
			} catch ( UnknownHostException ex ) {
			//	log (url_ref[i]+":"+ex.getMessage()+"\r\n") ;
				found = "red" ;
				reached = "red" ;
			} catch ( MalformedURLException ex ) {
			//	log (url_ref[i]+":"+ex.getMessage()+"\r\n") ;
				found = "red" ;
				reached = "red" ;
			} catch ( IOException ex ) {
			//	log (url_ref[i]+":"+ex.getMessage()+"\r\n") ;
				reached = "red" ;
			}
			if ( reached.equals("green") ) {
				//int result = conn.getResponseCode() ;
				int result = status(reslt) ;
				if ( result == 200 ) {
					ok = "green" ;
				} else if ( result >= 300 && result < 400 ) {
					int lindex = reslt.indexOf("\r\nLocation: ") ;
					if ( lindex != -1 ) {
						url_ref[i] = reslt.substring(lindex+12,reslt.indexOf("\r\n",lindex+12)).trim() ;
						//log("Redirect to: "+url_ref[i]) ;
						i-- ;
						continue ;
					}
				} else if ( result >= 400 && result < 500 ) {
					ok = "red" ;
				} else {
					ok = "red" ;
			//		log ("GET "+url.getHost()+":"+reslt.trim()) ;
				}
			}

			Vector vec = new Vector () ;
			vec.add("#meta_id#") ;
			vec.add("<a href=\"AdminDoc?meta_id="+meta_id[i]+"\" target=\"_new\">"+meta_id[i]+"</a>") ;
			vec.add("#url#") ;
			String foo = url_ref[i] ;
			if ( foo.indexOf(":/") == -1) {
				foo = "http://"+foo ;
			}
			if (url_ref[i].length()>60) {
				url_ref[i] = url_ref[i].substring(0,27)+" ... "+url_ref[i].substring(url_ref[i].length()-27) ;
			}
			String href = "<a href=\""+foo+"\" target=\"_new\">"+url_ref[i]+"</a>" ;
			vec.add(href) ;
			vec.add("#found#") ;
			vec.add(found) ;
			vec.add("#reached#") ;
			vec.add(reached) ;
			vec.add("#ok#") ;
			vec.add(ok) ;
			out.print(IMCServiceRMI.parseDoc(imcserver,vec,"UrlDocTestRow.html",lang_prefix)) ;
			out.flush() ;
		}
		out.print(IMCServiceRMI.parseDoc(imcserver,null,"UrlDocTestTail.html",lang_prefix)) ;
	}

	private String testUrl (URL url) throws IOException {
		int port = url.getPort() == -1 ? 80 : url.getPort() ;
		StringBuffer result = new StringBuffer() ;
		try {
			Socket sock = new Socket(url.getHost(),port) ;
			sock.setSoTimeout(5000) ;
			PrintStream out = new PrintStream(sock.getOutputStream()) ;
			BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream(),"8859_1")) ;
			String path = "".equals(url.getPath()) ? "/" : url.getPath() ;
			String cmd = "GET " + path + " HTTP/1.0\r\nHost: "+url.getHost()+"\r\n\r\n" ;
			out.print(cmd) ;
			String line ;
			while ( (line = in.readLine()) != null && line.length() > 0 ) {
				result.append(line+"\r\n") ;
			}
			sock.close() ;
		} catch ( SocketException ex ) {
			log ("SocketException in UrlDocTest, connecting to "+url.toString()+" : "+ex.getMessage()) ;
		}
		return result.toString() ;
	}

	private int status (String str) {
		try {
			if ( str.indexOf("HTTP/")==-1 ) {
				return 0 ;
			}
			StringTokenizer st = new StringTokenizer(str) ;
			st.nextToken() ;
			return Integer.parseInt(st.nextToken()) ;
		} catch ( NumberFormatException ex ) {
			log.debug("Exception occured" + ex );	   
			return 0 ;
		} catch ( NullPointerException ex ) {
			log.debug("Exception occured" + ex );	   
			return 0 ;
		}
	}

}
