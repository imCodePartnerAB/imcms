import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import imcode.util.* ;
import imcode.server.* ;
import imcode.server.user.UserDomainObject;

public class BackDoc extends HttpServlet {

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
        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface() ;
	String start_url	= imcref.getStartUrl() ;
	String no_permission_url	= Utility.getDomainPref( "no_permission_url" ) ;
	// Find the start-page
	int start_doc = imcref.getSystemData().getStartDocument() ;

	res.setContentType( "text/html" );
	Writer out = res.getWriter() ;

	UserDomainObject user ;
	if ( (user=Check.userLoggedOn(req,res,start_url))==null ) {
	    return ;
	}
	String top = req.getParameter("top");
	Stack history = (Stack)user.get("history") ;

	int tmp_meta_id = 0 ;
	int doc_type = 0 ;
	int meta_id = 0 ;

	if ( !history.empty() ) {

		if ( top == null ){
			// pop the first value from the history stack and true it away
			// because that is the current meta_id
	    	tmp_meta_id = ((Integer)history.peek()).intValue() ;			// Get the top value
	    	doc_type = imcref.getDocType(tmp_meta_id ) ;	// Get the doc_type

	    	if ( doc_type == 1 || doc_type == 2 ) {			// If we are on a text_doc,
				meta_id = ((Integer)history.pop()).intValue() ;			// Get the top value. If there are no more text_docs, we need to stay here.
	    	}
	    }

	    while ( !history.empty() ) {
			tmp_meta_id = ((Integer)history.pop()).intValue() ;			// Get the top value
			doc_type = imcref.getDocType(tmp_meta_id ) ;	// Get the doc_type

			if ( doc_type == 1 || doc_type == 2 ) {
		    	meta_id = tmp_meta_id ;
		    	break ;
			}
	    }
	    if ( meta_id != 0 ) {
	//	history.push(new Integer(meta_id)) ;
		user.put("history",history) ;
		String output = AdminDoc.adminDoc(meta_id,meta_id,user,req,res) ;
		if ( output != null ) {
		    out.write(output) ;
		}
		return ;
	    }
	}
	String output = GetDoc.getDoc(start_doc,start_doc,req,res) ;
	if ( output != null ) {
	    out.write(output) ;
	}
    }
}
