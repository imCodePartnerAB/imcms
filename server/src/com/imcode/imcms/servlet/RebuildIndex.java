package com.imcode.imcms.servlet;

import imcode.server.Imcms;
import imcode.server.document.index.AlreadyRebuildingIndexException;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RebuildIndex extends HttpServlet {

    public void doGet( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {

        UserDomainObject user = Utility.getLoggedOnUser( req );

        if ( !user.isSuperAdmin() ) {
            res.sendError( HttpServletResponse.SC_FORBIDDEN );
            return;
        }

        try {
            Imcms.getServices().getDefaultDocumentMapper().getDocumentIndex().rebuild() ;
            res.getOutputStream().println( "Started rebuild of index." );
        } catch( AlreadyRebuildingIndexException arie ) {
            res.getOutputStream().println( "Already rebuilding index." );
        }
    }
}
