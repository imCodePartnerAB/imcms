package com.imcode.imcms.servlet.admin;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Handles admin panel commands.
 */
public class AdminDoc extends HttpServlet {

    private static final long serialVersionUID = 4907353106118924203L;

    public static final String PARAMETER__DISPATCH_FLAGS = "flags"; // remove?

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        doPost(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        req.getRequestDispatcher("/api/editDoc").forward(req, res);
    }
}
