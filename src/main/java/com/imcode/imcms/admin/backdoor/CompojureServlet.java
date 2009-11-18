package com.imcode.imcms.admin.backdoor;

import clojure.lang.Var;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;

public class CompojureServlet extends HttpServlet {

    @Override
    public void service(ServletRequest r, ServletResponse servletResponse) throws ServletException, IOException {
        /*
        ClojureNamespace cn = new ClojureNamespace("com.imcode.imcms.compojure", "com/imcode/imcms/compojure");
        ClojureUtils.load(cn);
        Var serviceFn = ClojureUtils.var(cn, "service");

        try {
            serviceFn.invoke(servletRequest, servletResponse);
        } catch (Exception e) {
            throw new ServletException(e);
        }
        */
        r.getRequestDispatcher("/test").forward((HttpServletRequest)r, (HttpServletResponse)servletResponse);
    }
}
