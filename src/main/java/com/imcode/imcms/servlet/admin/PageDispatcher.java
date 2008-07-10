package com.imcode.imcms.servlet.admin;

import imcode.server.Imcms;
import imcode.util.Utility;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.imcode.imcms.flow.Page;
import com.imcode.util.MultipartHttpServletRequest;

public class PageDispatcher extends HttpServlet {

    public void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        doPost( request, response );
    }

    public void doPost( HttpServletRequest r, HttpServletResponse response ) throws ServletException, IOException {
        r.setCharacterEncoding( Imcms.DEFAULT_ENCODING );
        MultipartHttpServletRequest request = new MultipartHttpServletRequest( r );

        Page page = Page.fromRequest(request) ;
        if ( null != page ) {
            page.dispatch( request, response );
        } else {
            Utility.redirectToStartDocument( request, response );
        }
    }
}