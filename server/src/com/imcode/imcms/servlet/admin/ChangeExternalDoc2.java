package com.imcode.imcms.servlet.admin;

import com.imcode.imcms.servlet.GetDoc;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentMapper;
import imcode.server.document.DocumentPermissionSetDomainObject;
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
        ImcmsServices imcref = Imcms.getServices() ;

		int meta_id ;

		Utility.setDefaultHtmlContentType( res );
		Writer out = res.getWriter( );
		meta_id = Integer.parseInt( req.getParameter( "meta_id" ) ) ;

		UserDomainObject user=Utility.getLoggedOnUser( req );
        DocumentMapper documentMapper = imcref.getDocumentMapper();
        DocumentDomainObject document = documentMapper.getDocument( meta_id );
        DocumentPermissionSetDomainObject permissionSet = user.getPermissionSetFor( document );

        if ( permissionSet.getEdit() ) {
			String output = AdminDoc.adminDoc(meta_id, user,req,res) ;
			if ( output != null ) {
				out.write(output) ;
			}
			return ;
		}

		GetDoc.getDoc(meta_id, req,res) ;
	}
}
