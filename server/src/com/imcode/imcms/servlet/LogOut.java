package com.imcode.imcms.servlet;

import imcode.util.Utility;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LogOut extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Utility.makeUserLoggedOut(request);
        new VerifyUser.GoToLoginSuccessfulPageCommand().dispatch(request, response);
    }
}
