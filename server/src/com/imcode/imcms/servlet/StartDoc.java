package com.imcode.imcms.servlet;

import imcode.server.ApplicationServer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class StartDoc extends HttpServlet {

    public void doGet( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
        int meta_id = ApplicationServer.getIMCServiceInterface().getSystemData().getStartDocument();
        res.sendRedirect( "GetDoc?meta_id=" + meta_id );
    }
    
}
