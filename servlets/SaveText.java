import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;
import javax.servlet.*;
import javax.servlet.http.*;
import imcode.util.* ;
import imcode.server.* ;

import org.apache.log4j.Category ;

/**
   Save text in a document.
*/
public class SaveText extends HttpServlet {
    private final static String CVS_REV = "$Revision$" ;
    private final static String CVS_DATE = "$Date$" ;

    /** A Log4J Category **/
    private static Category log = Category.getInstance( IMCConstants.ERROR_LOG ) ;

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

	String host 	 = req.getHeader("Host") ;
	String imcserver = Utility.getDomainPref("adminserver",host) ;
	String start_url = Utility.getDomainPref( "start_url",host ) ;

	res.setContentType( "text/html" );
	Writer out = res.getWriter( );

	// Check if user logged on
	imcode.server.User user ;
	if( (user=Check.userLoggedOn( req,res,start_url ))==null ) {
	    return ;
	}

	// get meta_id
	int meta_id = Integer.parseInt( req.getParameter( "meta_id" ) ) ;

	// Check if user has permission to be here
	if ( !IMCServiceRMI.checkDocAdminRights(imcserver,meta_id,user,imcode.server.IMCConstants.PERM_DT_TEXT_EDIT_TEXTS ) ) {	// Checking to see if user may edit this
	    String output = AdminDoc.adminDoc(meta_id,meta_id,host,user,req,res) ;
	    if (output != null) {
		out.write(output) ;
	    }
	    return ;
	}

	// get text_no
	int txt_no = Integer.parseInt(req.getParameter( "txt_no" )) ;

	// get text
	String text_string = req.getParameter( "text" ) ;

	int text_type = Integer.parseInt(req.getParameter( "type" )) ;

	IMCText text = new IMCText(text_string,text_type) ;

	// Get the session
	HttpSession session = req.getSession( true );

	user.put("flags",new Integer(imcode.server.IMCConstants.PERM_DT_TEXT_EDIT_TEXTS)) ;

	if( req.getParameter( "ok" )!=null ) {
	    IMCServiceRMI.saveText( imcserver,user,meta_id,txt_no,text) ;
	}

	String output = AdminDoc.adminDoc(meta_id,meta_id,host,user,req,res) ;
	if (output != null) {
	    out.write(output) ;
	}
    }
}
