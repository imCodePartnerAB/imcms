import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;
import javax.servlet.*;
import javax.servlet.http.*;
import imcode.util.* ;
/**
  Save text in a document.
  */
public class SaveText extends HttpServlet {
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
		String text = "" ;
		String values[] ;
		int meta_id ;
		int txt_no = 0 ;
		int text_type = 0 ;
		boolean toHTMLSpecial ;


		res.setContentType( "text/html" );
		ServletOutputStream out = res.getOutputStream( );

		// get meta_id
		meta_id = Integer.parseInt( req.getParameter( "meta_id" ) ) ;
		// get text_no
		values = req.getParameterValues( "txt_no" ) ;
		if( values != null ) {
			txt_no = Integer.parseInt( values[0] ) ;
		}

		values = req.getParameterValues( "type" ) ;
		if( values != null ) {
			text_type = Integer.parseInt( values[0] ) ;
		}
		// get text
		values = req.getParameterValues( "text" ) ;
		if( values != null ) {
			text = values[0] ;
		}
		// get toHTML flag
//		values = req.getParameterValues( "format" ) ;
//		if( values != null ) {
//			log (values[0]) ;
//			if( values[0].equals( "_no_html" ) )
//				text_type = 0 ;
//			else
//				text_type = 1 ;
//		} //else
//			text_type = 0 ;

		// Get the session
		HttpSession session = req.getSession( true );

		// Does the session indicate this user already logged in?
		Object done = session.getValue( "logon.isDone" );  // marker object
		user = (imcode.server.User)done ;

		// Check if user logged on
		if( (user=Check.userLoggedOn( req,res,start_url ))==null ) {
			return ;
		}

		if ( !IMCServiceRMI.checkDocAdminRights(imcserver,meta_id,user,imcode.server.IMCConstants.PERM_DT_TEXT_EDIT_TEXTS ) ) {	// Checking to see if user may edit this
			byte[] tempbytes ;
			tempbytes = AdminDoc.adminDoc(meta_id,meta_id,host,user,req,res) ;
			if ( tempbytes != null ) {
				out.write(tempbytes) ;
			}
			return ;
		}

		user.put("flags",new Integer(imcode.server.IMCConstants.PERM_DT_TEXT_EDIT_TEXTS)) ;

		if( req.getParameter( "ok" )!=null ) {
			log("ok") ;
			IMCServiceRMI.saveText( imcserver,meta_id,user,txt_no,text,text_type ) ;
			SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd") ;
			Date dt = IMCServiceRMI.getCurrentDate(imcserver) ;

			String sqlStr = "update meta set date_modified = '"+dateformat.format(dt)+"' where meta_id = "+meta_id ;
			IMCServiceRMI.sqlUpdateQuery(imcserver,sqlStr);
		}

//        htmlStr = IMCServiceRMI.interpretTemplate(imcserver,meta_id,user) ;
		byte[] tempbytes = AdminDoc.adminDoc(meta_id,meta_id,host,user,req,res) ;
		if ( tempbytes != null ) {
			out.write(tempbytes) ;
		}
	}
}
