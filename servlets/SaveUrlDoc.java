import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;
import javax.servlet.*;
import javax.servlet.http.*;
import java.rmi.* ;
import java.rmi.registry.* ;
import imcode.util.* ;
/**
  Save an urldocument.
	Shows a change_meta.html which calls SaveMeta
*/
public class SaveUrlDoc extends HttpServlet {
	private final static String CVS_REV = "$Revision$" ;
	private final static String CVS_DATE = "$Date$" ;

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

		imcode.server.User user ;
		String htmlStr = "" ;
		String submit_name = "" ;
		String url_ref = "" ;
		String url_txt = "" ;
		String values[] ;
		int meta_id ;
		int parent_meta_id ;

		String ok = "" ;
		String cancel = "" ;
		String reset = "" ;
		String show_meta = "" ;

		res.setContentType( "text/html" );
		ServletOutputStream out = res.getOutputStream( );

		// get meta_id
		meta_id = Integer.parseInt( req.getParameter( "meta_id" ) ) ;

		// get parent_meta_id
		parent_meta_id = Integer.parseInt( req.getParameter( "parent_meta_id" ) ) ;

		// get urlref
		values = req.getParameterValues( "url_ref" ) ;
		if( values != null )
			url_ref = values[0] ;
		// get urltxt
/*
		values = req.getParameterValues( "url_txt" ) ;
		if( values != null )
			url_txt = values[0] ;
*/
		// save form data
		imcode.server.Table doc = new imcode.server.Table( ) ;

//		doc.addField( "url_ref",url_ref ) ;
//		doc.addField( "url_txt",url_txt ) ;

		String target = req.getParameter("target") ;
		if ( "_other".equals(target) ) {
			target = req.getParameter("frame_name") ;
		}

		// Get the session
		HttpSession session = req.getSession( true );

		// Does the session indicate this user already logged in?
		Object done = session.getValue( "logon.isDone" );  // marker object
		user = (imcode.server.User)done ;

		if( done == null ) {
			// No logon.isDone means he hasn't logged in.
			// Save the request URL as the true target and redirect to the login page.
			String scheme = req.getScheme( );
			String serverName = req.getServerName( );
			int p = req.getServerPort( );
			String port = (p == 80) ? "" : ":" + p;
			res.sendRedirect( scheme + "://" + serverName + port + start_url ) ;
			return ;
		}
		// Check if user has write rights
		if ( !IMCServiceRMI.checkDocAdminRights(imcserver, meta_id, user, 65536) ) {
			log("User "+user.getInt("user_id")+" was denied access to meta_id "+meta_id+" and was sent to "+start_url) ;
			String scheme = req.getScheme() ;
			String serverName = req.getServerName() ;
			int p = req.getServerPort() ;
			String port = ( p == 80 ) ? "" : ":" + p ;
			res.sendRedirect( scheme + "://" + serverName + port + start_url ) ;
			return ;
		}

/*		if( req.getParameter("cancel")!=null ) {
//			htmlStr = IMCServiceRMI.interpretTemplate( imcserver,parent_meta_id,user ) ;
			byte[] tempbytes = AdminDoc.adminDoc(parent_meta_id,parent_meta_id,host,user,req,res) ;
			if ( tempbytes != null ) {
				out.write(tempbytes) ;
			}
			return ;
		} else if( req.getParameter("metadata")!=null )
			//htmlStr = IMCServiceRMI.interpretAdminTemplate( imcserver,meta_id,user,"change_meta.html",5,parent_meta_id,0,0 ) ;
			htmlStr = imcode.util.MetaDataParser.parseMetaData(String.valueOf(meta_id), String.valueOf(parent_meta_id),user,host) ;
		else { */
//			IMCServiceRMI.saveUrlDoc( imcserver,meta_id,user,doc ) ;
			String sqlStr = "update url_docs set url_ref = '"+url_ref+"' where meta_id = "+meta_id ;
			IMCServiceRMI.sqlUpdateQuery(imcserver,sqlStr) ;
			sqlStr = "update meta set target = '"+target+"' where meta_id = "+meta_id ;
			IMCServiceRMI.sqlUpdateQuery(imcserver,sqlStr) ;
//			htmlStr = IMCServiceRMI.interpretTemplate( imcserver,parent_meta_id,user ) ;

			SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd") ;
			Date dt = IMCServiceRMI.getCurrentDate(imcserver) ;
			sqlStr = "update meta set date_modified = '"+dateformat.format(dt)+"' where meta_id = "+meta_id ;
			IMCServiceRMI.sqlUpdateQuery(imcserver,sqlStr) ;

			byte[] tempbytes = AdminDoc.adminDoc(meta_id,meta_id,host,user,req,res) ;
			if ( tempbytes != null ) {
				out.write(tempbytes) ;
			}
			return ;
		//}
		//out.print( htmlStr ) ;
	}
}
