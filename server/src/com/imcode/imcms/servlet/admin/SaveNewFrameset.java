package com.imcode.imcms.servlet.admin;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

import imcode.util.* ;
import imcode.server.* ;
import imcode.server.user.UserDomainObject;
/**
  Save a new framesetdocument.
  */
public class SaveNewFrameset extends HttpServlet {

	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface() ;
		String start_url        	= imcref.getStartUrl() ;

		int meta_id ;
		int new_meta_id ;

		res.setContentType("text/html");
		Writer out = res.getWriter();

		// get meta_id
		meta_id = Integer.parseInt(req.getParameter("meta_id")) ;

		// get new_meta_id
		new_meta_id = Integer.parseInt(req.getParameter("new_meta_id")) ;

		String frame_set =  req.getParameter("frame_set") ;

        // Check if user has write rights
        UserDomainObject user = Utility.getLoggedOnUser( req );
		if ( !imcref.checkDocAdminRights( meta_id, user) ) {
			log("User "+user.getUserId()+" was denied access to meta_id "+meta_id+" and was sent to "+start_url) ;
			String scheme = req.getScheme() ;
			String serverName = req.getServerName() ;
			int p = req.getServerPort() ;
			String port = ( p == 80 ) ? "" : ":" + p ;
			res.sendRedirect( scheme + "://" + serverName + port + start_url ) ;
			return ;
		}

		if (req.getParameter("cancel")!=null) {
			String output = AdminDoc.adminDoc(meta_id,meta_id,user,req,res) ;
			if ( output != null ) {
			    out.write(output) ;
			}
		} else {
			imcref.saveNewFrameset(new_meta_id,user,frame_set) ;
			String output = AdminDoc.adminDoc(new_meta_id,new_meta_id,user,req,res) ;
			if ( output != null ) {
			    out.write(output) ;
			}
		}
	}
}
