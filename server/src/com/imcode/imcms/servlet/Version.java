package com.imcode.imcms.servlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Version extends HttpServlet {

    private final static String VERSION_FILE = "version.txt";
    private final static int BUFFER_LENGTH = 32768;

    public void doGet( HttpServletRequest req, HttpServletResponse res ) throws IOException {

        InputStream in = this.getServletContext().getResourceAsStream( "/WEB-INF/" + VERSION_FILE );

        OutputStream out = res.getOutputStream();

        byte[] buffer = new byte[BUFFER_LENGTH];
        int length;
        while ( -1 != ( length = in.read( buffer, 0, BUFFER_LENGTH ) ) ) {
            out.write( buffer, 0, length );
        }
    }
}
