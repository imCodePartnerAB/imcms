import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import imcode.util.* ;
/**
  Return user from externaldoc editing or open metawindow for externaldoc.
	Shows a change_meta.html which calls SaveMeta
*/
public class ChangeExternalDoc2 extends HttpServlet {
	private final static String CVS_REV = "$Revision$" ;
	private final static String CVS_DATE = "$Date$" ;
	/**
	init()
	*/
	public void init( ServletConfig config ) throws ServletException {
		super.init( config ) ;
	}

	public void doGet( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
		doPost(req,res) ;
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
		int meta_id ;
		int parent_meta_id ;
		int txt_max = 0 ;
		int img_max = 0 ;                           	
		String values[] ; 

		res.setContentType( "text/html" );
		ServletOutputStream out = res.getOutputStream( );
		meta_id = Integer.parseInt( req.getParameter( "meta_id" ) ) ;
		parent_meta_id = Integer.parseInt( req.getParameter( "parent_meta_id" ) ) ;

		// Check if user logged on
		if( (user=Check.userLoggedOn( req,res,start_url ))==null ) {
			return ;
		} 

		if ( !IMCServiceRMI.checkDocAdminRights(imcserver,meta_id,user,65536 ) ) {	// Checking to see if user may edit this
			byte[] tempbytes ;
			tempbytes = AdminDoc.adminDoc(meta_id,meta_id,host,user,req,res) ;
			if ( tempbytes != null ) {
				out.write(tempbytes) ;
			}
			return ;
		}

/*		// Check if user has write rights
		if ( !Check.userWriteRights(meta_id, user, start_url) ) {
			log("User "+user.getInt("user_id")+" was denied access to meta_id "+meta_id+" and was sent to "+start_url) ;			
			String scheme = req.getScheme() ;
			String serverName = req.getServerName() ;
			int p = req.getServerPort() ;
			String port = ( p == 80 ) ? "" : ":" + p ;
			res.sendRedirect( scheme + "://" + serverName + port + start_url ) ;
			return ;
		}
*/
		imcode.server.ExternalDocType ex_doc = IMCServiceRMI.isExternalDoc( imcserver,meta_id,user ) ;

		if( req.getParameter("metadata")!=null ) {
//			htmlStr = IMCServiceRMI.interpretAdminTemplate( imcserver, meta_id,user,"change_meta.html",ex_doc.getDocType( ),parent_meta_id,0,0 ) ;
			htmlStr = imcode.util.MetaDataParser.parseMetaData(String.valueOf(meta_id), String.valueOf(parent_meta_id),user,host) ;
			out.println( htmlStr ) ;
			return ;
		}
		//if( req.getParameter("cancel")!=null ) {
//			htmlStr = IMCServiceRMI.interpretTemplate( imcserver, parent_meta_id,user ) ;
			byte [] tempbytes = GetDoc.getDoc(parent_meta_id,parent_meta_id,host,req,res) ;
			out.write ( tempbytes ) ;
		//}

	}
}
