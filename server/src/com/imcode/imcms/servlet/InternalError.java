package com.imcode.imcms.servlet;

import com.imcode.db.Database;
import com.imcode.db.commands.InsertIntoTableDatabaseCommand;
import com.imcode.imcms.util.l10n.LocalizedMessage;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.exception.ExceptionUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by ruslan on 04.10.16.
 */
public class InternalError extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Throwable exceptionFromRequest = (Throwable)request.getAttribute("javax.servlet.error.exception");

        saveError(exceptionFromRequest, request);

        UserDomainObject user = Utility.getLoggedOnUser(request);
        String language = "eng";
        if ( null != user ) {
            language = user.getLanguageIso639_2();
        }
        request.setAttribute("javax.servlet.error.exception" , null);
        request.getRequestDispatcher("/imcms/" + language + "/jsp/internalerrorpage.jsp").forward(request, response);
    }

    private void saveError(Throwable throwable, HttpServletRequest request) {
        Throwable cause = throwable.getCause();
        String persistenceCause = cause == null
                ? throwable.getClass().getName() : ExceptionUtils.getRootCauseMessage(throwable);
        String message = throwable.getMessage();
        String persistenceMessage = message == null
                ? throwable.getClass().getSimpleName() : ExceptionUtils.getMessage(throwable);
        String stackTrace = ExceptionUtils.getStackTrace(throwable);
        String persistencePlacement = getPlacement(stackTrace);
        Database database = Imcms.getServices().getDatabase();
        database.execute(new InsertIntoTableDatabaseCommand("internal_error", new Object[][] {
                {"message", persistenceMessage},
                {"cause", persistenceCause},
                {"placement", persistencePlacement}
        }));
        request.setAttribute("message", persistenceMessage);
        request.setAttribute("cause", persistenceCause);
        //velocity
        request.setAttribute("placement", persistencePlacement == null
                ? persistencePlacement : persistencePlacement.replaceAll("\\(", "[").replaceAll("\\)", "]"));
    }

    private String getPlacement(String stackTrace) {
        String determiner = "at ";
        String separator = ";";

        StringBuilder placement = new StringBuilder();

        int indexOf = stackTrace.indexOf(determiner);

        if (indexOf != -1) {

            while (indexOf != -1) {
                String substring = stackTrace.substring(indexOf, stackTrace.indexOf(')', indexOf) + 1);
                placement.append(substring);
                placement.append(separator);
                indexOf = stackTrace.indexOf(determiner, indexOf + 1);
            }

            return placement.toString();
        }

        return null;
    }

}
