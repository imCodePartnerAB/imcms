package com.imcode.imcms.servlet;

import com.google.gson.Gson;
import imcode.util.Utility;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class SessionsInfo extends HttpServlet {

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final Gson gson = new Gson();
        String jsonResponse = gson.toJson(Utility.getActiveSessions());

        res.setContentType("application/json");
        res.setCharacterEncoding("utf-8");

        PrintWriter out = res.getWriter();
        out.print(jsonResponse);
    }
}

