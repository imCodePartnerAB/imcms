import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import imcode.util.* ;
import imcode.server.* ;

/**
   Save text in a internalDocument.
*/
public class SaveText extends HttpServlet {
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

	IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface(req) ;
	String start_url = imcref.getStartUrl() ;

	res.setContentType( "text/html" );
	Writer out = res.getWriter( );

	// Check if user logged on
	imcode.server.user.User user ;
	if( (user=Check.userLoggedOn( req,res,start_url ))==null ) {
	    return ;
	}

	// get meta_id
	int meta_id = Integer.parseInt( req.getParameter( "meta_id" ) ) ;

	// Check if user has permission to be here
	if ( !imcref.checkDocAdminRights(meta_id,user,imcode.server.IMCConstants.PERM_DT_TEXT_EDIT_TEXTS ) ) {	// Checking to see if user may edit this
	    String output = AdminDoc.adminDoc(meta_id,meta_id,user,req,res) ;
	    if (output != null) {
		out.write(output) ;
	    }
	    return ;
	}

	// get text_no
	int txt_no = Integer.parseInt(req.getParameter( "txt_no" )) ;

	// get text
	String text_string = req.getParameter( "text" ) ;

	int text_format = Integer.parseInt(req.getParameter( "format_type" )) ;
	
	String text_type = req.getParameter("txt_type") ; // ex. pollrequest-1
	if ( text_type == null ){
		text_type = "";
	}

	IMCText text = new IMCText(text_string,text_format) ;

	user.put("flags",new Integer(imcode.server.IMCConstants.PERM_DT_TEXT_EDIT_TEXTS)) ;

	if( req.getParameter( "ok" )!=null ) {
	    imcref.saveText(user,meta_id,txt_no,text, text_type) ;
	}

	String output = AdminDoc.adminDoc(meta_id,meta_id,user,req,res) ;
	if (output != null) {
	    out.write(output) ;
	}
    }
}
