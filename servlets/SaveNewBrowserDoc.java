import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import imcode.util.* ;
import imcode.server.* ;
/**
   Save a new browserdocument.
*/
public class SaveNewBrowserDoc extends HttpServlet {

    /**
       init()
    */
    public void init(ServletConfig config) throws ServletException {
	super.init(config) ;
    }

    /**
       doPost()
    */
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
	String host				= req.getHeader("Host") ;
        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface() ;
	String start_url	= imcref.getStartUrl() ;

	imcode.server.user.UserDomainObject user ;
	String htmlStr = "" ;

	res.setContentType("text/html");
	Writer out = res.getWriter();

	// get meta_id
	int parent_meta_id = Integer.parseInt(req.getParameter("meta_id")) ;

	// get new_meta_id
	int meta_id = Integer.parseInt(req.getParameter("new_meta_id")) ;

	// Get the session
	HttpSession session = req.getSession(true);

	// Does the session indicate this user already logged in?
	Object done = session.getAttribute("logon.isDone");  // marker object
	user = (imcode.server.user.UserDomainObject)done ;

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
	if ( !imcref.checkDocAdminRights( meta_id, user) ) {
	    log("User "+user.getUserId()+" was denied access to meta_id "+meta_id+" and was sent to "+start_url) ;
	    String scheme = req.getScheme() ;
	    String serverName = req.getServerName() ;
	    int p = req.getServerPort() ;
	    String port = ( p == 80 ) ? "" : ":" + p ;
	    res.sendRedirect( scheme + "://" + serverName + port + start_url ) ;
	    return ;
	}

	if( req.getParameter("cancel")!=null ) {
	    // FIXME: Move to SProc
	    String sqlStr = "delete from browser_docs where to_meta_id = 0 and meta_id = " + meta_id ;
	    imcref.sqlUpdateQuery( sqlStr ) ;
	    String output = AdminDoc.adminDoc(parent_meta_id,parent_meta_id,user,req,res) ;
	    if ( output != null ) {
		out.write(output) ;
	    }
	    return ;

	} else if( req.getParameter("ok")!=null ) {
	    Enumeration enum = req.getParameterNames() ;
	    while ( enum.hasMoreElements() ) {
		String param = (String)enum.nextElement() ;
		if ( param.indexOf("bid")==0 ) {
		    String bid = param.substring(3) ;
		    String to = req.getParameter(param) ;
		    String sqlStr = null ;
		    try {
			int t = Integer.parseInt(to) ;
			// FIXME: Move to SProc
			sqlStr = "update browser_docs set to_meta_id = "+t+" where meta_id = "+meta_id+" and browser_id = "+bid ;
		    } catch ( NumberFormatException ex ) {
			if ( !bid.equals("0") ) {
			    // FIXME: Move to SProc
			    sqlStr = "delete from browser_docs where meta_id = "+meta_id+" and browser_id = "+bid ;
			}
		    }
		    if ( sqlStr != null ) {
			imcref.sqlUpdateQuery( sqlStr ) ;
		    }
		}
	    }
	    imcref.activateChild(meta_id,user) ;

	    String output = AdminDoc.adminDoc(meta_id,meta_id,user,req,res) ;
	    if ( output != null ) {
		out.write(output) ;
	    }
	    return ;

	} else if ( req.getParameter("add_browsers")!=null) {
	    String[] browsers = req.getParameterValues("new_browsers") ;
	    if ( browsers != null ) {
		for ( int i=0 ; i<browsers.length ; i++ ) {
		    // FIXME: Move to SProc
		    String sqlStr = "insert into browser_docs (meta_id,to_meta_id,browser_id) values ("+meta_id+",0,"+browsers[i]+")" ;
		    imcref.sqlUpdateQuery(sqlStr) ;
		}
	    }
	    Vector vec = new Vector () ;
	    // FIXME: Move to SProc
	    String sqlStr = "select name,browsers.browser_id,to_meta_id from browser_docs join browsers on browsers.browser_id = browser_docs.browser_id where meta_id = "+meta_id+" order by value desc,name asc" ;
	    Hashtable hash = imcref.sqlQueryHash(sqlStr) ;
	    String[] b_id = (String[])hash.get("browser_id") ;
	    String[] nm = (String[])hash.get("name") ;
	    String[] to = (String[])hash.get("to_meta_id") ;
	    String bs = "" ;
	    if ( b_id != null ) {
		// FIXME: Move to template
		bs+="<table width=\"50%\" border=\"0\">" ;
		for ( int i = 0 ; i<b_id.length ; i++ ) {
		    String[] temparr = {" ","&nbsp;"} ;
		    // FIXME: Move to template
		    bs += "<tr><td>"+Parser.parseDoc(nm[i],temparr)+":</td><td><input type=\"text\" name=\"bid"+b_id[i]+"\" value=\""+(to[i].equals("0") ? "" : to[i])+"\"></td></tr>" ;
		}
		// FIXME: Move to template
		bs+="</table>" ;
	    }
	    vec.add("#browsers#") ;
	    vec.add(bs) ;
	    // FIXME: Move to SProc
	    sqlStr = "select browser_id,name from browsers where browser_id not in (select browsers.browser_id from browser_docs join browsers on browsers.browser_id = browser_docs.browser_id where meta_id = "+meta_id+" ) order by value desc,name asc" ;
	    hash = imcref.sqlQueryHash(sqlStr) ;
	    b_id = (String[])hash.get("browser_id") ;
	    nm = (String[])hash.get("name") ;
	    String nb = "" ;
	    if ( b_id!=null ) {
		for ( int i = 0 ; i<b_id.length ; i++ ) {
		    // FIXME: Move to template
		    nb += "<option value=\""+b_id[i]+"\">"+nm[i]+"</option>" ;
		}
	    }
	    vec.add("#new_browsers#") ;
	    vec.add(nb) ;
	    vec.add("#new_meta_id#") ;
	    vec.add(String.valueOf(meta_id)) ;
	    vec.add("#getDocType#") ;
	    // FIXME: Move to template
	    vec.add("<INPUT TYPE=\"hidden\" NAME=\"doc_type\" VALUE=\"6\">") ;
	    vec.add("#DocMenuNo#") ;
	    vec.add("") ;
	    vec.add("#getMetaId#") ;
	    vec.add(String.valueOf(parent_meta_id)) ;
	    String lang_prefix = user.getLangPrefix() ;

	    htmlStr = imcref.parseDoc( vec, "new_browser_doc.html", lang_prefix) ;
	}

	out.write(htmlStr) ;
    }
}
