package com.imcode.imcms.servlet.superadmin;

import imcode.server.SanityCheck;
import imcode.server.DatabaseSanityCheck;
import imcode.server.Imcms;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

import com.imcode.imcms.db.DatabaseUtils;
import com.imcode.imcms.api.ContentManagementSystem;

public class SanityCheckServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {
        ContentManagementSystem cms = ContentManagementSystem.fromRequest(request);
        if (!cms.getCurrentUser().isSuperAdmin()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        
        DatabaseSanityCheck databaseSanityCheck = new DatabaseSanityCheck(Imcms.getServices().getDatabase(), DatabaseUtils.getWantedDdl());
        Collection<SanityCheck.Problem> problems = databaseSanityCheck.execute();
        response.setContentType("text/plain");
        PrintWriter writer = response.getWriter();
        for ( SanityCheck.Problem problem : problems ) {
            writer.println(problem.getSeverity()+": "+problem.getDescription());
        }

    }
}
