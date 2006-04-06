package com.imcode.imcms.servlet;

import imcode.server.Imcms;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class StartDoc extends HttpServlet {

    public void doGet( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
        int meta_id = Imcms.getServices().getSystemData().getStartDocument();
        req.getRequestDispatcher( "/servlet/GetDoc?meta_id="+meta_id ).forward( req, res );
    }

    protected void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        doGet( request, response );
    }
}
