import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

import imcode.util.* ;
import imcode.server.* ;
import imcode.server.user.UserDomainObject;
/**
  Return user from externaldoc editing or open metawindow for externaldoc.
	Shows a change_meta.html which calls SaveMeta
*/
public class ChangeExternalDoc2 extends HttpServlet {

    public void doGet( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
		doPost(req,res) ;
	}

	/**
	doPost()
	*/
	public void doPost( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface() ;

		String htmlStr;
		int meta_id ;
		int parent_meta_id ;

		res.setContentType( "text/html" );
		Writer out = res.getWriter( );
		meta_id = Integer.parseInt( req.getParameter( "meta_id" ) ) ;
		parent_meta_id = Integer.parseInt( req.getParameter( "parent_meta_id" ) ) ;

		UserDomainObject user=Utility.getLoggedOnUser( req );
		if ( !imcref.checkDocAdminRights(meta_id,user,65536 ) ) {	// Checking to see if user may edit this
			String output = AdminDoc.adminDoc(meta_id,meta_id,user,req,res) ;
			if ( output != null ) {
				out.write(output) ;
			}
			return ;
		}

		if( req.getParameter("metadata")!=null ) {
			htmlStr = imcode.util.MetaDataParser.parseMetaData(String.valueOf(meta_id), String.valueOf(parent_meta_id),user, null) ;
			out.write( htmlStr ) ;
			return ;
		}
		String output = GetDoc.getDoc(parent_meta_id,parent_meta_id,req,res) ;
		out.write ( output ) ;

	}
}
