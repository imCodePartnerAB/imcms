package com.imcode.imcms.servlet.admin;

import com.imcode.imcms.flow.Page;
import imcode.server.WebAppGlobalConstants;
import imcode.util.MultipartHttpServletRequest;
import imcode.util.Utility;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class PageDispatcher extends HttpServlet {

    public void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        doPost( request, response );
    }

    public void doPost( HttpServletRequest r, HttpServletResponse response ) throws ServletException, IOException {
        r.setCharacterEncoding( WebAppGlobalConstants.DEFAULT_ENCODING_WINDOWS_1252 );
        MultipartHttpServletRequest request = new MultipartHttpServletRequest( r );

        Page page = Page.fromRequest(request) ;
        if ( null != page ) {
            page.dispatch( request, response );
        } else {
            Utility.redirectToStartDocument( request, response );
        }
    }
}