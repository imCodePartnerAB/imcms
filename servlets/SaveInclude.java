import javax.servlet.* ;
import javax.servlet.http.* ;

import imcode.server.* ;
import imcode.server.document.DocumentMapper;
import imcode.server.user.UserDomainObject;

import imcode.util.Utility ;
import imcode.util.IMCServiceRMI ;

import java.io.IOException ;
import java.io.Writer ;

import java.util.Vector ;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.apache.log4j.Category;

public class SaveInclude extends HttpServlet {

    private final static Category mainLog = Category.getInstance(IMCConstants.MAIN_LOG);
    private final static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS ") ;

    public void init (ServletConfig config) throws ServletException {
	super.init( config ) ;
    }

    public void doPost (HttpServletRequest req, HttpServletResponse res) throws ServletException, java.io.IOException {
	String host			= req.getHeader("Host") ;
        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface() ;
	String start_url	= imcref.getStartUrl() ;

	res.setContentType("text/html") ;

	Writer out = res.getWriter() ;

	imcode.server.user.UserDomainObject user ;

	String meta_id_str = req.getParameter("meta_id") ;
	int meta_id = Integer.parseInt(meta_id_str) ;

	// Check if the user logged on
	if ( (user = Utility.getLoggedOnUserOrRedirect(req,res,start_url )) == null ) {
	    return ;
	}

	// Check if user has permission to edit includes for this internalDocument
	if ( !imcref.checkDocAdminRights(meta_id,user,imcode.server.IMCConstants.PERM_DT_TEXT_EDIT_INCLUDES ) ) {	// Checking to see if user may edit this
	    sendPermissionDenied(imcref,out,meta_id,user) ;
	    return ;
	}

	String included_meta_id = req.getParameter("include_meta_id") ;

	String include_id = req.getParameter("include_id") ;
	if (included_meta_id != null && include_id != null) {
	    if ("".equals(included_meta_id.trim())) {
            DocumentMapper.sprocDeleteInclude( imcref, meta_id, Integer.parseInt(include_id) );
            mainLog.info(dateFormat.format(new java.util.Date())+"Include nr [" + include_id +	"] on ["+meta_id_str+"] removed by user: [" +user.getFullName() + "]");

	    } else {
		try {
		    int included_meta_id_int = Integer.parseInt(included_meta_id) ;

		    // Make sure the user has permission to share the included internalDocument
		    if (imcref.checkUserDocSharePermission(user,included_meta_id_int)) {
                DocumentMapper.sprocSetInclude( imcref, meta_id, Integer.parseInt(include_id), included_meta_id_int);
                mainLog.info(dateFormat.format(new java.util.Date())+"Include nr [" +include_id  +	"] on ["+meta_id_str+"] changed to ["+ included_meta_id+ "]  by user: [" +user.getFullName() + "]");
			} else {
			sendPermissionDenied(imcref,out,meta_id,user) ;
			return ;
		    }
		} catch (NumberFormatException ignored) {
		    sendBadId(imcref,out,meta_id,user) ;
		    return ;
		}
	    }
	}

	String tempstring = AdminDoc.adminDoc(meta_id,meta_id,user,req,res) ;
	if ( tempstring != null ) {
	    out.write(tempstring) ;
	}
	return ;
    }

    protected void sendPermissionDenied(IMCServiceInterface imcref, Writer out, int meta_id, UserDomainObject user) throws IOException {
	Vector vec = new Vector(2) ;
	vec.add("#meta_id#") ;
	vec.add(String.valueOf(meta_id)) ;
	String htmlStr = imcref.parseDoc(vec,"include_permission_denied.html",user.getLangPrefix()) ;
	out.write(htmlStr) ;
    }

    protected void sendBadId(IMCServiceInterface imcref, Writer out, int meta_id, UserDomainObject user) throws IOException {
	Vector vec = new Vector(2) ;
	vec.add("#meta_id#") ;
	vec.add(String.valueOf(meta_id)) ;
	String htmlStr = imcref.parseDoc(vec,"include_bad_id.html",user.getLangPrefix()) ;
	out.write(htmlStr) ;
    }

}
