import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;
import javax.servlet.*;
import javax.servlet.http.*;

import imcode.util.* ;
import imcode.server.* ;
/**
  Save a framesetdocument.
	Shows a change_meta.html which calls SaveMeta
*/
public class SaveFrameset extends HttpServlet {
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
		IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface(req) ;
		String start_url        	= imcref.getStartUrl() ;
		String servlet_url        	= Utility.getDomainPref( "servlet_url",host ) ;

		imcode.server.user.UserDomainObject user ;
		String htmlStr = "" ;
		String submit_name = "" ;
		String values[] ;
		int meta_id ;
		int parent_meta_id ;

		res.setContentType( "text/html" );
		Writer out = res.getWriter( );

		// get meta_id
		meta_id = Integer.parseInt( req.getParameter( "meta_id" ) ) ;

		// get new_meta_id
		String tempStr = req.getParameter( "parent_meta_id" ) ;
		if( tempStr != null )
			parent_meta_id = Integer.parseInt( tempStr ) ;
		else
			parent_meta_id = meta_id ;

		// save form data
		imcode.server.Table doc = new imcode.server.Table( ) ;
		String frame_set =  req.getParameter( "frame_set" ) ;
		String[] tmpary = {
			"'",	"''"
		} ;
		frame_set = Parser.parseDoc(frame_set,tmpary) ;
		doc.addField( "frame_set",frame_set ) ;

		// Check if user logged on
		if( (user=Check.userLoggedOn( req,res,start_url ))==null ) {
			return ;
		}

		// Check if user has write rights
		if ( !imcref.checkDocAdminRights(meta_id,user,65536 ) ) {	// Checking to see if user may edit this
			String output = AdminDoc.adminDoc(meta_id,meta_id,user,req,res) ;
			if ( output != null ) {
				out.write(output) ;
			}
			return ;
		}

		if( req.getParameter("ok")!=null ) {	//User pressed ok on form in change_frameset_doc.html
			imcref.saveFrameset(meta_id,user,doc ) ;
			SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd") ;
			Date dt = imcref.getCurrentDate() ;
			String sqlStr = "update meta set date_modified = '"+dateformat.format(dt)+"' where meta_id = "+meta_id ;
			imcref.sqlUpdateQuery(sqlStr) ;

			String output = AdminDoc.adminDoc(meta_id,meta_id,user,req,res) ;
			if ( output != null ) {
			    out.write(output) ;
			}
			return ;
		}
	}
}

