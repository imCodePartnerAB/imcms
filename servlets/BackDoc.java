import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import imcode.util.* ;
import imcode.server.* ;

public class BackDoc extends HttpServlet {
	private final static String CVS_REV = "$Revision$" ;
	private final static String CVS_DATE = "$Date$" ;
	
	/**
	init()
	*/
	public void init( ServletConfig config ) throws ServletException {
		super.init( config ) ;
	}

	/**
	doGet()
	*/
	public void doGet( HttpServletRequest req, HttpServletResponse res )	throws ServletException, IOException {
	
		String host				= req.getHeader("Host") ;
		String imcserver			= Utility.getDomainPref("userserver",host) ;
		String start_url	= Utility.getDomainPref( "start_url",host ) ;
		String no_permission_url	= Utility.getDomainPref( "no_permission_url",host ) ;
		int start_doc					= IMCServiceRMI.getDefaultHomePage(imcserver) ;

		res.setContentType( "text/html" );
		Writer out = res.getWriter() ;

		User user ;
		if ( (user=Check.userLoggedOn(req,res,start_url))==null ) {
			return ;
		} 
		Stack history = (Stack)user.get("history") ;
		
		int tmp_meta_id = 0 ;
		int doc_type = 0 ;
		int meta_id = 0 ;

		if ( !history.empty() ) {
			tmp_meta_id = ((Integer)history.peek()).intValue() ;			// Get the top value
			doc_type = IMCServiceRMI.getDocType( imcserver,tmp_meta_id ) ;	// Get the doc_type
			if ( doc_type == 1 || doc_type == 2 ) {			// If we are on a text_doc,
				meta_id = ((Integer)history.pop()).intValue() ;			// Get the top value. If there are no more text_docs, we need to stay here.
			}			

			while ( !history.empty() ) {
				tmp_meta_id = ((Integer)history.pop()).intValue() ;			// Get the top value
				doc_type = IMCServiceRMI.getDocType( imcserver,tmp_meta_id ) ;	// Get the doc_type
				if ( doc_type == 1 || doc_type == 2 ) {
					meta_id = tmp_meta_id ;
					break ;
				}
			} 
			if ( meta_id != 0 ) {
				history.push(new Integer(meta_id)) ;
				String output = AdminDoc.adminDoc(meta_id,meta_id,host,user,req,res) ;
				if ( output != null ) {
					out.write(output) ;
				}
				return ;
			}
		}
		String output = GetDoc.getDoc(start_doc,start_doc,host,req,res) ;
		if ( output != null ) {
		    out.write(output) ;
		}
	}
}
