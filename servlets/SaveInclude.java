import javax.servlet.* ;
import javax.servlet.http.* ;

import imcode.server.IMCConstants ;
import imcode.server.User ;

import imcode.util.Check ;
import imcode.util.Utility ;
import imcode.util.IMCServiceRMI ;

public class SaveInclude extends HttpServlet {

    public void init (ServletConfig config) throws ServletException {
	super.init( config ) ;
    }

    public void doPost (HttpServletRequest req, HttpServletResponse res) throws ServletException, java.io.IOException {
	String host 			= req.getHeader("Host") ;
	String imcserver 		= imcode.util.Utility.getDomainPref("adminserver",host) ;
	String start_url        	= imcode.util.Utility.getDomainPref( "start_url",host ) ;

	res.setContentType("text/html") ;

	ServletOutputStream out = res.getOutputStream() ;

	imcode.server.User user ;

	String meta_id_str = req.getParameter("meta_id") ;
	int meta_id = Integer.parseInt(meta_id_str) ;

	// Check if the user logged on
	if ( (user = Check.userLoggedOn(req,res,start_url )) == null ) {
	    return ;
	}

	// Check if user has write rights
	if ( !IMCServiceRMI.checkDocAdminRights(imcserver,meta_id,user,imcode.server.IMCConstants.PERM_DT_TEXT_EDIT_INCLUDES ) ) {	// Checking to see if user may edit this
	    byte[] tempbytes ;
	    tempbytes = AdminDoc.adminDoc(meta_id,meta_id,host,user,req,res) ;
	    if ( tempbytes != null ) {
		out.write(tempbytes) ;
	    }
	    return ;
	}

	if ( req.getParameter( "ok" ) != null ) {
	    String included_meta_id = req.getParameter("include_meta_id") ;
	    String include_id = req.getParameter("include_id") ;
	    if (included_meta_id != null && include_id != null) {
		IMCServiceRMI.sqlUpdateProcedure(imcserver,"SetInclude "+meta_id_str+","+include_id+","+included_meta_id) ; 
	    }
	    byte[] tempbytes = AdminDoc.adminDoc(meta_id,meta_id,host,user,req,res) ;
	    if ( tempbytes != null ) {
		out.write(tempbytes) ;
	    }
	    return ;
	}
    }

}
