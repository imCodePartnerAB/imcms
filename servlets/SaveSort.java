import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;
import javax.servlet.*;
import javax.servlet.http.*;
import imcode.util.* ;
/**
  Save document sorting (date,name,manual)
  */
public class SaveSort extends HttpServlet {
	private final static String CVS_REV = "$Revision$" ;
	private final static String CVS_DATE = "$Date$" ;
	private final static String COPY_PREFIX_TEMPLATE = "copy_prefix.html";
	/**
	init()
	*/
	public void init( ServletConfig config ) throws ServletException {
		super.init( config ) ;
	}

	/**
	service()
	*/
	public void service( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
		String host 				= req.getHeader("Host") ;
		String imcserver 			= Utility.getDomainPref("adminserver",host) ;
		String start_url        	= Utility.getDomainPref( "start_url",host ) ;

		imcode.server.User user ;
		String htmlStr = "" ;
		String child_str = "" ;
		int meta_id ;
		Vector childs  = new Vector( ) ;
		Vector sort_no = new Vector( ) ;

		res.setContentType( "text/html" );
		ServletOutputStream out = res.getOutputStream( ) ;
		meta_id = Integer.parseInt( req.getParameter( "meta_id" ) ) ;
		int doc_menu_no = Integer.parseInt( req.getParameter( "doc_menu_no" ) ) ;
		child_str =  req.getParameter( "childs" ) ;


		// Check if user logged on
		if ( (user=Check.userLoggedOn(req,res,start_url))==null ) {
			return ;
		}

		if ( !IMCServiceRMI.checkDocAdminRights(imcserver,meta_id,user,262144 ) ) {	// Checking to see if user may edit this
			byte[] tempbytes ;
			tempbytes = AdminDoc.adminDoc(meta_id,meta_id,host,user,req,res) ;
			if ( tempbytes != null ) {
				out.write(tempbytes) ;
			}
			return ;
		}

		int start_pos = 0 ;
		int end_pos = 0 ;
		String temp_str = "" ;
		boolean end = false ;
		String childsThisMenu[] ;
		String values[] ;
/*
		while( end_pos != -1 ) {
			end_pos =	child_str.indexOf( ",",start_pos ) ;

			if( end_pos != -1 ) {
				temp_str = child_str.substring( start_pos,end_pos ) ;
				childs.addElement( temp_str ) ;
				start_pos = end_pos + 1 ;
			}
		}

		for( int i = 0 ; i < childs.size( )  ; i++ ) {
			temp_str = req.getParameter( childs.elementAt( i ).toString( ) ) ;
			if( temp_str != null )
				sort_no.addElement( temp_str ) ;
		}

		for( int i = childs.size( ) -1 ; i >= 0 ; i-- ) {
			temp_str = req.getParameter( childs.elementAt( i ).toString( ) ) ;
			if( temp_str == null )
				childs.removeElementAt( i ) ;
		}
*/
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd") ;
		Date dt = IMCServiceRMI.getCurrentDate(imcserver) ;
		String sqlStr = "update meta set date_modified = '"+dateformat.format(dt)+"' where meta_id = "+meta_id ;
		IMCServiceRMI.sqlUpdateQuery(imcserver,sqlStr) ;

		sqlStr = "select to_meta_id from childs where meta_id = " + meta_id ;
		String[] foo = IMCServiceRMI.sqlQuery(imcserver, sqlStr) ;

		for ( int i=0 ; i<foo.length ; ++i ) {
			temp_str = req.getParameter(foo[i]) ;
			if ( temp_str != null ) {
				childs.add(foo[i]) ;
				sort_no.add(temp_str) ;
			}
		}


		childsThisMenu = req.getParameterValues( "archiveDelBox" ) ;

		user.put("flags",new Integer(262144)) ;

		if( req.getParameter("sort")!=null ) {
			String sort_order  = req.getParameter("sort_order") ;
			IMCServiceRMI.sqlUpdateQuery(imcserver,"update text_docs set sort_order = "+sort_order+" where meta_id = "+meta_id) ;
			if ( childs.size() > 0 ) {
				IMCServiceRMI.saveManualSort( imcserver,meta_id,user,childs,sort_no ) ;
			}
		} else if( req.getParameter("delete")!=null ) {
		    if( childsThisMenu != null ) {
			IMCServiceRMI.deleteChilds( imcserver,meta_id,doc_menu_no,user,childsThisMenu ) ;
		    }
		} else if( req.getParameter("archive")!=null ) {
		    if( childsThisMenu != null ) {
			IMCServiceRMI.archiveChilds( imcserver,meta_id,user,childsThisMenu ) ;
		    }
		} else if( req.getParameter("copy")!=null ) {
		    if( childsThisMenu != null ) {
			String copyPrefix = IMCServiceRMI.parseDoc(imcserver, null, COPY_PREFIX_TEMPLATE, user.getLangPrefix());
			
			IMCServiceRMI.copyDocs( imcserver,meta_id,doc_menu_no,user,childsThisMenu,copyPrefix ) ;
		    }
		}

//		htmlStr = IMCServiceRMI.interpretTemplate( imcserver,meta_id,user ) ;
		byte[] tempbytes = AdminDoc.adminDoc(meta_id,meta_id,host,user,req,res) ;
		if ( tempbytes != null ) {
			out.write(tempbytes) ;
		}
//		return ;
//		out.println( htmlStr ) ;
	}
}
