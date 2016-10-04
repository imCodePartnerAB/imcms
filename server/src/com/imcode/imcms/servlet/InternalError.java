package com.imcode.imcms.servlet;

import com.imcode.db.Database;
import com.imcode.db.commands.InsertIntoTableDatabaseCommand;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
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

    private static Database database;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Throwable exceptionFromRequest = (Throwable)request.getAttribute("javax.servlet.error.exception");

        saveError(exceptionFromRequest);

        UserDomainObject user = Utility.getLoggedOnUser(request);
        String language = "eng";
        if ( null != user ) {
            language = user.getLanguageIso639_2();
        }
        request.getRequestDispatcher("/imcms/" + language + "/jsp/internalerrorpage.jsp").forward(request, response);
    }

    private void saveError(Throwable throwable) {
        Throwable cause = throwable.getCause();
        String persistenceCause = cause == null ?
                throwable.getClass().getSimpleName() : ExceptionUtils.getRootCauseMessage(throwable);
        String message = throwable.getMessage();
        String persistenceMessage = message == null ?
                throwable.getClass().getName() : ExceptionUtils.getMessage(throwable);
        String stackTrace = ExceptionUtils.getStackTrace(throwable);
        String persistencePlacement = getPlacement(stackTrace);
        database.execute(new InsertIntoTableDatabaseCommand("internal_error", new Object[][] {
                {"message", persistenceMessage},
                {"cause", persistenceCause},
                {"placement", persistencePlacement}
        }));
    }

    private String getPlacement(String stackTrace) {
        StringBuilder placement = new StringBuilder();

        int indexOf = stackTrace.indexOf("at ");

        if (indexOf != -1) {
            while (indexOf != -1) {
                placement.append(stackTrace.substring(indexOf, stackTrace.indexOf(')', indexOf) + 1));
                placement.append(";");
                indexOf = stackTrace.indexOf("at ", indexOf + 1);
            }
        } else {
            placement = null;
        }

        return placement == null ? null : placement.toString();
    }

    /**
     * You can set database only ones
     * @param database
     */
    public static void setDatabase(Database database) {
        if (InternalError.database == null) {
            InternalError.database = database;
        }
    }
}
