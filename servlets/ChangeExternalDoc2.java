import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import imcode.util.* ;
import imcode.server.* ;
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
		IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface(req) ;
		String start_url        	= imcref.getStartUrl() ;

		imcode.server.User user ; 
		String htmlStr = "" ;                         	
		int meta_id ;
		int parent_meta_id ;
		int txt_max = 0 ;
		int img_max = 0 ;                           	
		String values[] ; 

		res.setContentType( "text/html" );
		Writer out = res.getWriter( );
		meta_id = Integer.parseInt( req.getParameter( "meta_id" ) ) ;
		parent_meta_id = Integer.parseInt( req.getParameter( "parent_meta_id" ) ) ;

		// Check if user logged on
		if( (user=Check.userLoggedOn( req,res,start_url ))==null ) {
			return ;
		} 

		if ( !imcref.checkDocAdminRights(meta_id,user,65536 ) ) {	// Checking to see if user may edit this
			String output = AdminDoc.adminDoc(meta_id,meta_id,user,req,res) ;
			if ( output != null ) {
				out.write(output) ;
			}
			return ;
		}

		imcode.server.ExternalDocType ex_doc = imcref.isExternalDoc(meta_id,user ) ;

		if( req.getParameter("metadata")!=null ) {
			htmlStr = imcode.util.MetaDataParser.parseMetaData(String.valueOf(meta_id), String.valueOf(parent_meta_id),user,host) ;
			out.write( htmlStr ) ;
			return ;
		}
		String output = GetDoc.getDoc(parent_meta_id,parent_meta_id,req,res) ;
		out.write ( output ) ;

	}
}
