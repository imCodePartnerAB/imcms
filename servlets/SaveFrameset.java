import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;
import javax.servlet.*;
import javax.servlet.http.*;

import imcode.util.* ;
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
		String imcserver 			= Utility.getDomainPref("adminserver",host) ;
		String start_url        	= Utility.getDomainPref( "start_url",host ) ;
		String servlet_url        	= Utility.getDomainPref( "servlet_url",host ) ;

		imcode.server.User user ;
		String htmlStr = "" ;
		String submit_name = "" ;
		String values[] ;
		int meta_id ;
		int parent_meta_id ;

		res.setContentType( "text/html" );
		ServletOutputStream out = res.getOutputStream( );

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
		if ( !IMCServiceRMI.checkDocAdminRights(imcserver,meta_id,user,65536 ) ) {	// Checking to see if user may edit this
			byte[] tempbytes ;
			tempbytes = AdminDoc.adminDoc(meta_id,meta_id,host,user,req,res) ;
			if ( tempbytes != null ) {
				out.write(tempbytes) ;
			}
			return ;
		}

/*		if( req.getParameter("cancel")!=null ) {	//User pressed cancel on form in change_frameset_doc.html
			// check if browser_doc
			int old_meta_id = parent_meta_id ;
			String br_id = (String)req.getSession(false).getValue("browser_id") ;
			String sqlStr = "select top 1 to_meta_id from browser_docs join browsers on browsers.browser_id = browser_docs.browser_id where meta_id = "+parent_meta_id+" and '"+br_id+"' like user_agent" ;
			String tmp = IMCServiceRMI.sqlQueryStr(imcserver,sqlStr) ;
			if ( tmp != null && (!"".equals(tmp)) ) {
				parent_meta_id = Integer.parseInt(tmp) ;
			}
//			parent_meta_id = IMCServiceRMI.isBrowserDoc( imcserver,parent_meta_id,user ) ;
			if( old_meta_id != parent_meta_id ) {
				sqlStr = "select frame_set from frameset_docs where meta_id = " + meta_id ;
				htmlStr = IMCServiceRMI.sqlQueryStr(imcserver, sqlStr ) ;
			} else {
//				htmlStr = IMCServiceRMI.interpretTemplate( imcserver,parent_meta_id,user ) ;
				byte[] tempbytes = AdminDoc.adminDoc(meta_id,meta_id,host,user,req,res) ;
				if ( tempbytes != null ) {
					out.write(tempbytes) ;
				}
				return ;
			}
		}*/ /*else if( req.getParameter("metadata")!=null ) {		//User pressed metadata on form in change_frameset_doc.html
			//htmlStr = IMCServiceRMI.interpretAdminTemplate( imcserver,meta_id,user,"change_meta.html",7,parent_meta_id,0,0 ) ;
			htmlStr = imcode.util.MetaDataParser.parseMetaData(String.valueOf(meta_id), String.valueOf(parent_meta_id),user,host) ;
		} else*/ if( req.getParameter("ok")!=null ) {	//User pressed ok on form in change_frameset_doc.html
			IMCServiceRMI.saveFrameset( imcserver,meta_id,user,doc ) ;
			SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd") ;
			Date dt = IMCServiceRMI.getCurrentDate(imcserver) ;
			String sqlStr = "update meta set date_modified = '"+dateformat.format(dt)+"' where meta_id = "+meta_id ;
			IMCServiceRMI.sqlUpdateQuery(imcserver,sqlStr) ;

			// check if browser_doc
//			int old_meta_id = parent_meta_id ;
//			String br_id = (String)req.getSession(false).getValue("browser_id") ;
//			String sqlStr = "select top 1 to_meta_id from browser_docs join browsers on browsers.browser_id = browser_docs.browser_id where meta_id = "+parent_meta_id+" and '"+br_id+"' like user_agent" ;
//			String tmp = IMCServiceRMI.sqlQueryStr(imcserver,sqlStr) ;
//			if ( tmp != null && (!"".equals(tmp)) ) {
//				parent_meta_id = Integer.parseInt(tmp) ;
//			}
//			parent_meta_id = IMCServiceRMI.isBrowserDoc( imcserver,parent_meta_id,user ) ;
//			if( old_meta_id != parent_meta_id ) {
//				sqlStr = "select frame_set from frameset_docs where meta_id = " + meta_id ;
//				htmlStr = IMCServiceRMI.sqlQueryStr(imcserver, sqlStr ) ;
//			} else {
//				htmlStr = IMCServiceRMI.interpretTemplate( imcserver,parent_meta_id,user ) ;
				byte[] tempbytes = AdminDoc.adminDoc(meta_id,meta_id,host,user,req,res) ;
				if ( tempbytes != null ) {
					out.write(tempbytes) ;
				}
				return ;
//			}
		}
//		out.println( htmlStr ) ;
	}
}

