import java.util.*;

import java.io.PrintWriter ;
import java.io.StringWriter ;
import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.File;
import java.util.ArrayList;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import javax.servlet.ServletException;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpUtils;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletOutputStream;
import imcode.util.* ;
import imcode.util.log.* ;
import imcode.server.* ;
import imcode.server.parser.ParserParameters;
import imcode.util.IMCServiceRMI;

import org.apache.log4j.Category;

/**
   Get a document = Parse data from database.
*/
public class GetDoc extends HttpServlet {
    private final static String CVS_REV = "$Revision$" ;
    private final static String CVS_DATE = "$Date$" ;

    private final static  int COOKIE_EXPIRE_TIME = 518400;
    private final static String SERVLET_CONFIG_FILE_NAME = "servlet.cfg";
    private final static DateFormat dateFormat = new SimpleDateFormat("'#date#'[yyyy-MM-dd] '#time#'[HH:mm:ss.SSS]") ;
    private static Category trackLog = Category.getInstance( IMCConstants.ACCESS_LOG ) ;
    private static Category log = Category.getInstance( GetDoc.class.getName() ) ;

    static {
	trackLog.info( "Track log started." );
	log.debug("Track log called for the first time. The logs name is "+IMCConstants.ACCESS_LOG  );
    }

    private static String createAccessLoggMessage(HttpSession session, String message, String ipNr, String referer) {
	StringBuffer strBuff = new StringBuffer( dateFormat.format(new Date()) );
	strBuff.append(" #session#["+session.getId()+"]" );
	strBuff.append(" #ipnr#["+ipNr+"]" );
	User user = (User)session.getAttribute("logon.isDone");  // marker object
	if ( user != null )
	    {
		strBuff.append(" #user#["+user.getUserId()+"]" );
		strBuff.append(" #lastdoc#["+user.getLastMetaId()+"]" );
	    }
	strBuff.append( " #document#["+message+"]");
	if (referer != null) {
	    String str = referer.substring(referer.indexOf("meta_id=")+8);
	    strBuff.append( " #referer#["+str+"]");
	}
	return strBuff.toString();
    }

    /**
       doGet()
    */
    public void doGet( HttpServletRequest req, HttpServletResponse res )	throws ServletException, IOException {

	String host				= req.getHeader("Host") ;
	String imcserver			= Utility.getDomainPref("userserver",host) ;
	int start_doc				= IMCServiceRMI.getDefaultHomePage(imcserver) ;

	HttpSession session = req.getSession( true );

	String htmlStr = "" ;
	int meta_id ;
	int parent_meta_id ;

	res.setContentType( "text/html" );
	ServletOutputStream out = res.getOutputStream();

	try {
	    meta_id = Integer.parseInt( req.getParameter( "meta_id" ) ) ;
	} catch ( NumberFormatException ex ) {
	    meta_id = start_doc ;
	    log.debug("Exception occured" + ex );
	}
	String tempstring = getDoc(meta_id,meta_id,host,req,res) ;
	if ( tempstring != null ) {
	    byte[] tempbytes = tempstring.getBytes("8859_1") ;
	    res.setContentLength(tempbytes.length) ;
	    out.write(tempbytes) ;
	}
	out.flush() ;
	out.close() ;

    }

    public static String getDoc (int meta_id, int parent_meta_id, String host, HttpServletRequest req, HttpServletResponse res) throws IOException {
	String imcserver			= Utility.getDomainPref("userserver",host) ;
	String start_url	= Utility.getDomainPref( "start_url",host ) ;
	String no_permission_url	= Utility.getDomainPref( "no_permission_url",host ) ;
	int start_doc					= IMCServiceRMI.getDefaultHomePage(imcserver) ;
	String servlet_url	= Utility.getDomainPref( "servlet_url",host ) ;
	File file_path			= Utility.getDomainPrefPath( "file_path", host ) ;

	String scheme = req.getScheme( ) ;
	String serverName = req.getServerName( ) ;
	int p = req.getServerPort( ) ;
	String port = (p == 80 || p == 443) ? "" : ":" + p ;

	HttpSession session = req.getSession(true) ;
	Object done = session.getAttribute("logon.isDone");  // marker object
	imcode.server.User user = (imcode.server.User)done ;

	if (done == null) {
	    // Check the name and password for validity
	    String ip = req.getRemoteAddr( ) ;
	    user = StartDoc.ipAssignUser( ip, host ) ;

	    // Valid login.  Make a note in the session object.
	    //			session = req.getSession( true );
	    if ( user == null ) {
		session.setAttribute("login.target", HttpUtils.getRequestURL(req).toString()+"?"+req.getQueryString());
		res.sendRedirect(start_url) ;
		return null ;
	    }
	    session.setAttribute( "logon.isDone", user );  // just a marker object

	    // get type of browser
	    String value = req.getHeader( "User-Agent" ) ;

	    if ( value == null ) {
		value = "" ;
	    }
	    session.setAttribute("browser_id",value) ;

	    StartDoc.incrementSessionCounter(imcserver,user,req) ;
	}

	String[] emp_ary = req.getParameterValues("emp") ;
	if (emp_ary != null) {
	    user.put("emphasize",emp_ary) ;
	}

	String sqlStr = "select doc_type from meta where meta_id = " + meta_id;
	String doc_type_str = null ;
	if( (doc_type_str = IMCServiceRMI.sqlQueryStr( imcserver,sqlStr )) == null ) {
	    return IMCServiceRMI.parseDoc( imcserver, null,"no_page.html",user.getLangPrefix() ) ;
	}

	// FIXME: One of the places that need fixing. Number one, we should put the no-permission-page
	// among the templates for the default-language. Number two, we should use just one function for
	// checking permissions. Number three, since the user obviously has logged in, give him the page in his own language!

	if ( !IMCServiceRMI.checkDocRights(imcserver,meta_id,user ) ) {
	    session.setAttribute("login.target", HttpUtils.getRequestURL(req).toString()+"?"+req.getQueryString());
	    String redirect = no_permission_url ;
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
	    Utility.redirect( req,res,ex_doc.getCallServlet( ) + paramStr ) ;
	    return null ;
	}

	// Log to accesslog
	trackLog.info(createAccessLoggMessage( session, meta_int.toString(), req.getRemoteAddr(),req.getHeader("Referer") ) );

	switch( Integer.parseInt(doc_type_str) ) {

	case 5:	//URL-doc
	    imcode.server.Table url_doc = IMCServiceRMI.isUrlDoc( imcserver,meta_id,user ) ;
	    String temp = url_doc.getString("url_ref") ;
	    if ( temp.indexOf("://")==-1 ) {
		temp = "http://"+temp ;
	    }
	    res.sendRedirect( temp ) ;
	    return null ;

	case 6:	//browser-doc
	    String br_id = (String)session.getAttribute("browser_id") ;
	    sqlStr = "select top 1 to_meta_id from browser_docs join browsers on browsers.browser_id = browser_docs.browser_id where meta_id = "+meta_id+" and '"+br_id+"' like user_agent order by value desc" ;
	    String tmp = IMCServiceRMI.sqlQueryStr(imcserver,sqlStr) ;
	    if ( tmp != null && (!"".equals(tmp)) ) {
		meta_id = Integer.parseInt(tmp) ;
	    }

	    Utility.redirect(req,res,"GetDoc?meta_id="+meta_id+"&parent_meta_id="+parent_meta_id) ;
	    return null ; //getDoc(meta_id,parent_meta_id,host,req,res) ;

	case 7:	//frameset-doc
	    String html_str_temp = IMCServiceRMI.isFramesetDoc( imcserver,meta_id,user ) ;
	    if( html_str_temp == null ) {
		throw new RuntimeException("Null-frameset encountered.") ;
	    }
	    htmlStr = html_str_temp ;
	    return htmlStr ;

	case 8:	//fileupload-doc
	    sqlStr = "select mime from fileupload_docs where meta_id = " + meta_id ;
	    String mimetype =	IMCServiceRMI.sqlQueryStr( imcserver,sqlStr ) ;
	    sqlStr = "select filename from fileupload_docs where meta_id = " + meta_id ;
	    String filename =	IMCServiceRMI.sqlQueryStr( imcserver,sqlStr ) ;
	    //			IMCServiceRMI.updateTrackLog( imcserver,parent_meta_id,meta_id,user ) ;
	    BufferedInputStream fr ;
	    try {
		fr = new BufferedInputStream( new FileInputStream( new File( file_path, String.valueOf( meta_id )+"_se" ) ) ) ;
	    } catch ( IOException ex ) {
		String lang_prefix = IMCServiceRMI.sqlQueryStr(imcserver, "select lang_prefix from lang_prefixes where lang_id = "+user.getInt("lang_id")) ;
		htmlStr = IMCServiceRMI.parseDoc( imcserver, null,"no_page.html",lang_prefix ) ;
		return htmlStr ;
	    }
	    int len = fr.available( ) ;
	    ServletOutputStream out = res.getOutputStream() ;
	    String range = req.getHeader("Range") ;
	    String content_type = mimetype ;
	    String content_disposition = "inline; filename="+filename ;
	    res.setContentLength( len ) ;
	    res.setContentType( content_type ) ;
	    res.setHeader( "Content-Disposition", content_disposition ) ;
	    try {
		int bytes_read = 0 ;
		byte buffer[] = new byte[32768] ;
		while( -1 != (bytes_read = fr.read( buffer )) ) {
		    out.write(buffer,0,bytes_read) ;
		}
	    } catch ( java.net.SocketException ex ) {
		log.debug("Exception occured" + ex );
	    }
	    fr.close() ;
	    out.flush() ;
	    out.close() ;
	    return null ;

	default:

	    String externalparam = null;
	    if (req.getParameter("externalClass") != null || req.getAttribute("externalClass") != null){
		String className;
		if (req.getParameter("externalClass") != null){
		    className = req.getParameter("externalClass");
		}else{
		    className = (String) req.getAttribute("externalClass");
		}
		try{
		    Class cl = Class.forName(className);
		    imcode.external.GetDocControllerInterface obj =(imcode.external.GetDocControllerInterface) cl.newInstance();
		    externalparam = obj.createString(req);
		}catch(Exception e)	{
		    StringWriter sw = new StringWriter() ;
		    e.printStackTrace(new PrintWriter(sw)) ;
		    externalparam = "<!-- Exception: "+sw.toString()+" -->" ;
		}
	    }

	    user.setTemplateGroup(-1) ;
	    ParserParameters paramsToParser = new ParserParameters(req.getParameter("template"),
								   req.getParameter("param"),
								   externalparam);

	    user.setLastMetaId( meta_id ) ;
	    String result = IMCServiceRMI.parsePage( imcserver,meta_id,user,0,paramsToParser ) ;
	    return result ;
	}
    }
}
