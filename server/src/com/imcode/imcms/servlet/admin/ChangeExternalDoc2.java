package com.imcode.imcms.servlet.admin;

import com.imcode.imcms.servlet.GetDoc;
import imcode.server.ApplicationServer;
import imcode.server.IMCServiceInterface;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
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

		Utility.setDefaultHtmlContentType( res );
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

		String output = GetDoc.getDoc(parent_meta_id,parent_meta_id,req,res) ;
		out.write ( output ) ;

	}
}
