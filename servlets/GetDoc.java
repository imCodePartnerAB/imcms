/*
 *
 * @(#)GetDoc.java
 *
 *
 * Copyright (c)
 *
*/
import java.util.*;

import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.File;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpUtils;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletOutputStream;
/*
import javax.servlet.http.Cookie;
import com.cyscape.browserhawk.BrowserHawk;
import com.cyscape.browserhawk.BrowserInfo;
import com.cyscape.browserhawk.ExtendedBrowserInfo;
import com.cyscape.browserhawk.BrowserHawkException;
import com.cyscape.browserhawk.LicenseException;
*/
import imcode.util.* ;
import imcode.server.* ;
import imcode.util.IMCServiceRMI;

/**
  Get a document = Parse data from database.
*/
public class GetDoc extends HttpServlet {

	private static final int COOKIE_EXPIRE_TIME = 518400;

	private static ServletContext sc ;
	/**
	init()
	*/
	public void init( ServletConfig config ) throws ServletException {
		super.init( config ) ;
		sc = config.getServletContext() ;
	}

	/**
	doGet()
	*/
	public void doGet( HttpServletRequest req, HttpServletResponse res )	throws ServletException, IOException {

		if ( true /*isStatisticsCollected( res, req )*/ ) {

			String host 				= req.getHeader("Host") ;
			String imcserver 			= Utility.getDomainPref("userserver",host) ;
			String start_url        	= Utility.getDomainPref( "start_url",host ) ;
			String no_permission_url 	= Utility.getDomainPref( "no_permission_url",host ) ;
			int start_doc				= IMCServiceRMI.getDefaultHomePage(imcserver) ;
			String servlet_url       	= Utility.getDomainPref( "servlet_url",host ) ;
			String file_path 			= Utility.getDomainPref( "file_path", host ) ;

			HttpSession session = req.getSession( true );

			String htmlStr = "" ;
			int meta_id ;
			int parent_meta_id ;

			res.setContentType( "text/html" );
			ServletOutputStream out = res.getOutputStream( );

			try {
				meta_id = Integer.parseInt( req.getParameter( "meta_id" ) ) ;
			} catch ( NumberFormatException ex ) {
				meta_id = start_doc ;
			}

	/*		try {
				parent_meta_id = Integer.parseInt( req.getParameter( "parent_meta_id" ) ) ;
			} catch ( NumberFormatException ex ) {
				parent_meta_id = start_doc ;
			}
	*/
	/*		User user ;
			// Check if user logged on
			if ( (user=Check.userLoggedOn(req,res,start_url))==null ) {
				return ;
			}
	*/
			//int doc_type = IMCServiceRMI.getDocType( imcserver,meta_id ) ;

			byte[] tempbytes = getDoc(meta_id,meta_id,host,req,res) ;
			if ( tempbytes != null ) {
			    res.setContentLength(tempbytes.length) ;
			    out.write(tempbytes) ;
			}
			out.flush() ;
			out.close() ;

		} else {
			return;
		}
	}

	public static byte[] getDoc (int meta_id, int parent_meta_id, String host, HttpServletRequest req, HttpServletResponse res) throws IOException {
		String imcserver 			= Utility.getDomainPref("userserver",host) ;
		String start_url        	= Utility.getDomainPref( "start_url",host ) ;
		String no_permission_url 	= Utility.getDomainPref( "no_permission_url",host ) ;
		int start_doc					= IMCServiceRMI.getDefaultHomePage(imcserver) ;
		String servlet_url       	= Utility.getDomainPref( "servlet_url",host ) ;
		String file_path 			= Utility.getDomainPref( "file_path", host ) ;

		String scheme = req.getScheme( ) ;
		String serverName = req.getServerName( ) ;
		int p = req.getServerPort( ) ;
		String port = (p == 80 || p == 443) ? "" : ":" + p ;

		HttpSession session = req.getSession(true) ;
		Object done = session.getValue("logon.isDone");  // marker object
		imcode.server.User user = (imcode.server.User)done ;

		if (done == null) {
			// Check the name and password for validity
			String ip = req.getRemoteAddr( ) ;
			user = StartDoc.ipAssignUser( ip, host ) ;

			// Valid login.  Make a note in the session object.
		//			session = req.getSession( true );
			if ( user == null ) {
				session.putValue("login.target", HttpUtils.getRequestURL(req).toString()+"?"+req.getQueryString());
				res.sendRedirect(start_url) ;
				return null ;
			}
			session.putValue( "logon.isDone", user );  // just a marker object

		// get type of browser
			String value = req.getHeader( "User-Agent" ) ;

			if ( value == null ) {
				value = "" ;
			}
			session.putValue("browser_id",value) ;
			IMCServiceRMI.incCounter(imcserver) ;
			IMCServiceRMI.sqlUpdateProcedure( imcserver, "IncSessionCounter" ) ;

		  // No logon.isDone means he hasn't logged in.
		  // Save the request URL as the true target and redirect to the login page.
		  //log (HttpUtils.getRequestURL(req).toString()+"?"+req.getQueryString()) ;
		}

		String[] emp_ary = req.getParameterValues("emp") ;
		if (emp_ary != null) {
			for (int i = 0; i<emp_ary.length ; ++i) {
				sc.log("emp_ary: "+emp_ary[i]);
			}
			user.put("emphasize",emp_ary) ;
		}

		String sqlStr = "select doc_type from meta where meta_id = " + meta_id;
		String doc_type_str = null ;
		if( (doc_type_str = IMCServiceRMI.sqlQueryStr( imcserver,sqlStr )) == null ) {
			//		if ( imc.sqlQueryStr(sqlStr) == null ) {
			//			htmlStr = imc.parseDoc( null,"no_page.html","se" ) ;
			String lang_pf = IMCServiceRMI.sqlQueryStr(imcserver, "select lang_prefix from lang_prefixes where lang_id = "+user.getInt("lang_id")) ;
			return IMCServiceRMI.parseDoc( imcserver, null,"no_page.html",lang_pf ).getBytes("8859_1") ;
		}

		// FIXME One of the places that need fixing. Number one, we should put the no-permission-page
		// among the templates for the default-language. Number two, we should use just one function for
		// checking permissions. Number three, since the user obviously has logged in, give him the page in his own language!

		if ( !IMCServiceRMI.checkDocRights(imcserver,meta_id,user ) ) {
			session.putValue("login.target", HttpUtils.getRequestURL(req).toString()+"?"+req.getQueryString());
			String redirect = no_permission_url ;
			sc.log ("Redirecting to: "+redirect) ;
			res.sendRedirect( redirect ) ;
			return null ;
		}

		Stack history = (Stack)user.get("history") ;
		if ( history == null ) {
			history = new Stack() ;
			user.put("history",history) ;
		}
		Integer meta_int = new Integer(meta_id) ;
		if ( history.empty() || !history.peek().equals(meta_int) ) {
			history.push(meta_int) ;
		}

		// check if external doc
		imcode.server.ExternalDocType ex_doc = IMCServiceRMI.isExternalDoc( imcserver,meta_id,user ) ;
		//		imcode.server.ExternalDocType ex_doc = imc.isExternalDoc( meta_id,user ) ;
		String htmlStr = "" ;
		if( ex_doc != null ) {
			String paramStr = "?meta_id=" + meta_id + "&" ;
			paramStr += "parent_meta_id=" + parent_meta_id + "&" ;
			paramStr += "cookie_id=" + "1A" + "&" ;
			paramStr += "action=view" ;
			sc.log ( ex_doc.getCallServlet( ) + paramStr ) ;
			Utility.redirect( req,res,ex_doc.getCallServlet( ) + paramStr ) ;
			return null ;
		}
		//int doc_type = IMCServiceRMI.getDocType( imcserver,meta_id ) ;
		switch( Integer.parseInt(doc_type_str) ) {

		case 5:	//URL-doc
			imcode.server.Table url_doc = IMCServiceRMI.isUrlDoc( imcserver,meta_id,user ) ;
			// track user
			IMCServiceRMI.updateTrackLog( imcserver,parent_meta_id,meta_id,user ) ;
			String temp = url_doc.getString("url_ref") ;
			if ( temp.indexOf("://")==-1 ) {
				temp = "http://"+temp ;
			}
			sc.log ("Redirecting to: "+temp) ;
			res.sendRedirect( temp ) ;
			return null ;

		case 6:	//browser-doc
			String br_id = (String)req.getSession(false).getValue("browser_id") ;
			sqlStr = "select top 1 to_meta_id from browser_docs join browsers on browsers.browser_id = browser_docs.browser_id where meta_id = "+meta_id+" and '"+br_id+"' like user_agent order by value desc" ;
			String tmp = IMCServiceRMI.sqlQueryStr(imcserver,sqlStr) ;
			if ( tmp != null && (!"".equals(tmp)) ) {
				meta_id = Integer.parseInt(tmp) ;
			}
			user.archiveOff( ) ;
			IMCServiceRMI.updateTrackLog( imcserver,parent_meta_id,meta_id,user ) ;
			Utility.redirect(req,res,"GetDoc?meta_id="+meta_id+"&parent_meta_id="+parent_meta_id) ;
			return null ; //getDoc(meta_id,parent_meta_id,host,req,res) ;

		case 7:	//frameset-doc
			String html_str_temp = IMCServiceRMI.isFramesetDoc( imcserver,meta_id,user ) ;
			if( html_str_temp == null ) {
				throw new RuntimeException("Null-frameset encountered.") ;
			}
			// track user
			IMCServiceRMI.updateTrackLog( imcserver,parent_meta_id,meta_id,user ) ;
			htmlStr = html_str_temp ;
			return htmlStr.getBytes("8859_1") ;

		case 8:	//fileupload-doc
			sqlStr = "select mime from fileupload_docs where meta_id = " + meta_id ;
			String mimetype =	IMCServiceRMI.sqlQueryStr( imcserver,sqlStr ) ;
			sqlStr = "select filename from fileupload_docs where meta_id = " + meta_id ;
			String filename =	IMCServiceRMI.sqlQueryStr( imcserver,sqlStr ) ;
			IMCServiceRMI.updateTrackLog( imcserver,parent_meta_id,meta_id,user ) ;
			BufferedInputStream fr ;
			try {
				fr = new BufferedInputStream( new FileInputStream( new File( file_path, String.valueOf( meta_id )+"_se" ) ) ) ;
			} catch ( IOException ex ) {
				String lang_prefix = IMCServiceRMI.sqlQueryStr(imcserver, "select lang_prefix from lang_prefixes where lang_id = "+user.getInt("lang_id")) ;
				htmlStr = IMCServiceRMI.parseDoc( imcserver, null,"no_page.html",lang_prefix ) ;
				return htmlStr.getBytes("8859_1") ;
			}
			int len = fr.available( ) ;
			ServletOutputStream out = res.getOutputStream() ;
			String range = req.getHeader("Range") ;
			sc.log("Range: "+range) ;
			sc.log (String.valueOf(meta_id)+"Name: "+filename) ;
			String content_type = mimetype/*+"; name=\""+filename+"\""*/ ;
			String content_disposition = "inline; filename="+filename ;
			sc.log ("Content-Type: "+content_type) ;
			//sc.log ("Content-Disposition: "+content_disposition) ;
			res.setContentLength( len ) ;
			res.setContentType( content_type ) ;
			res.setHeader( "Content-Disposition", content_disposition ) ;
		/*
			int bytes_read = 0 ;
			byte buffer[] = new byte[len] ;

			while( bytes_read < len ) {
				bytes_read += fr.read( buffer,bytes_read,len-bytes_read ) ;
			}
			fr.close( ) ;
			return buffer ;
		*/
			try {
				int bytes_read = 0 ;
				byte buffer[] = new byte[32768] ;
				while( -1 != (bytes_read = fr.read( buffer )) ) {
					out.write(buffer,0,bytes_read) ;
				}
			} catch ( java.net.SocketException ex ) {

			}
			fr.close() ;
			out.flush() ;
			out.close() ;
			return null ;

		default:
			user.archiveOff( ) ;
			user.setTemplateGroup(-1) ;
			// track user
			IMCServiceRMI.updateTrackLog( imcserver,parent_meta_id,meta_id,user ) ;

			user.setLastMetaId( meta_id ) ;
			long time = System.currentTimeMillis() ;
			byte[] result = IMCServiceRMI.parsePage( imcserver,meta_id,user,0 ) ;
			sc.log("parsePage: "+(System.currentTimeMillis()-time)+" ms") ;
			return result ;
		}
	}


	/*
	 * set an cookie at client that statistics has been collected
	 *
	*/
/*
	private void setStatisticCookie( HttpServletResponse response ) {

		Cookie statisticCookie = new Cookie( "Statistics", "collected" );
		response.addCookie( statisticCookie );
		statisticCookie.setMaxAge( this.COOKIE_EXPIRE_TIME );
	}
*/
	/*
	 * collects statistics if not collected.
	 * The statistics is collected if sessionvalue statisticsCollected and or cookie Statistics is present.
	 * If not collected, end the response immediately so that servlet is redy for the response from client.
	 *
	*/
/*
	private boolean isStatisticsCollected( HttpServletResponse response, HttpServletRequest request )
						throws ServletException, IOException {

		String host = request.getHeader("Host") ;
		String imcserver = Utility.getDomainPref("userserver",host) ;
		HttpSession session = request.getSession( true );

		if ( session.getValue( "statisticsCollected" ) !=  null ) {

			return true;

		} else {

			
//			must set statisticsCollected as soon as possible because this
//			servlet may bee called by this client before request is computed
			

			session.putValue( "statisticsCollected", "true" );

			Cookie[] cookies = request.getCookies();
			boolean cookieExist = false;
			String outString = "";
			Cookie statisticCookie = null;

			// lets se if our cookie exist
			for ( int i = 0 ; i < cookies.length ; i++ ) {
				if ( cookies[i].getName().equals( "Statistics" ) ) {
					cookieExist = true;
				}
			}

			if ( cookieExist ) {

				session.putValue( "statisticsCollected", "true" );
				setStatisticCookie( response ); //lets update expire date

				return true;

			} else {
				BrowserInfo browser = null;
				ArrayList sqls = new ArrayList();

				try  {
					browser = BrowserHawk.getBrowserInfo( request );

					ExtendedBrowserInfo extendedBrowserInfo = null;
					if ( browser.getJavaScript() ) {

						extendedBrowserInfo = BrowserHawk.getExtendedBrowserInfo( request, response, 0 ,false , null, "Collecting", "Test" );

						// lets get the statistics that can be collected
						if ( extendedBrowserInfo == null )  {

							
//							 Javascript sent to client.
//							 Lets get out of her to be ready to recive the info
							

							session.removeValue( "statisticsCollected" );
							return false;

						} else if ( extendedBrowserInfo != ExtendedBrowserInfo.UNSUPPORTED ) {

							if ( extendedBrowserInfo != ExtendedBrowserInfo.JAVASCRIPT_DISABLED ) {

								sqls.add( "AddStatistics 'javaScript'" );

								int width = extendedBrowserInfo.getWidth();
								int height = extendedBrowserInfo.getHeight();
								int colorDepth = extendedBrowserInfo.getColorDepth();

								if ( width != -1 && height != -1 ) {
									sqls.add( "AddScreenStatistics " + width + ", " + height + ", " + colorDepth );
								}

								if ( extendedBrowserInfo.getJavaEnabled() ) {
									sqls.add( "AddStatistics 'java'" );
								}

								String connectionType = extendedBrowserInfo.getConnectionType();
								if ( connectionType != null ) {
									sqls.add( "AddStatistics '" + connectionType + "'" );
								}
							}
						}
					}

					getBrowserStatistics( browser, sqls );
					storeBrowserStatistics( imcserver, sqls );
					session.putValue( "statisticsCollected", "true" );
					setStatisticCookie( response );

				} catch ( LicenseException ex ) {
					log ( "Browser information not available, license problem." );
				} catch ( BrowserHawkException e )  {
					log( "Browser information not available, exception: "+e.getMessage() );
				}

				return true;
			}
		}
	}
*/
	/*
	 * stores statistics in db
	 *
	 * param sqls list of strings containing storedprocedures
	*/
/*
	private void storeBrowserStatistics( String server, ArrayList sqls ) throws IOException {

		sqls.add("AddStatisticsCount") ;
		for ( int i = 0 ; i < sqls.size() ; i++ ) {
			IMCServiceRMI.sqlUpdateQuery( server, (String)sqls.get( i ) );
		}
	}
*/
	/*
	 * collects statistics from BrowserInfo and stores it in sqls
	*/
/*
	private void getBrowserStatistics( BrowserInfo browser, ArrayList sqls ) {

		//browser statistics
		String browserName = browser.getBrowser();
		String version = browser.getFullversion();
		String platform = browser.getPlatform();
		String os = browser.getOSDetails();
		sqls.add( "AddBrowserStatistics '" + platform + " " +  os + "', '" + browserName + "', '" + version +  "'");

		//javaScript version
		if ( browser.getJavaScript() ) {
			double javaScriptVersion = browser.getJavaScriptVer();
			sqls.add( "AddVersionStatistics 'javaScript', '" + javaScriptVersion + "'");
		}
		//javaapplet support
		if ( browser.getJavaApplets() ) {
			sqls.add( "AddStatistics 'javaApplet'" );
		}

		//the most likely preference for the visitor's spoken language
		String language = browser.getLanguage();
		sqls.add( "AddStatistics " + language );

		//does the browser support VBScript
		if ( browser.getVBScript() ) {
			sqls.add( "AddStatistics 'VBScript'" );
		}

		//does the browser support SSl
		if ( browser.getSSL() ) {
			sqls.add( "AddStatistics 'SSL'" );
			if ( browser.getSSLActive() ) {
				int sSLKeySize = browser.getSSLKeySize();
				sqls.add( "AddStatistics 'SSLKeySize: " + sSLKeySize + "'" );
			}
		}
	}
*/
}
