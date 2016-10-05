package com.imcode.imcms.servlet;

import com.imcode.db.Database;
import com.imcode.db.DatabaseException;
import com.imcode.db.commands.InsertIntoTableDatabaseCommand;
import com.imcode.db.commands.SqlUpdateCommand;
import imcode.server.Imcms;
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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Throwable exceptionFromRequest = (Throwable)request.getAttribute("javax.servlet.error.exception");
        UserDomainObject user = Utility.getLoggedOnUser(request);

        saveError(exceptionFromRequest, request, user.getId());

        String language = "eng";
        if ( null != user ) {
            language = user.getLanguageIso639_2();
        }
        request.setAttribute("javax.servlet.error.exception" , null);
        request.getRequestDispatcher("/imcms/" + language + "/jsp/internalerrorpage.jsp").forward(request, response);
    }

    private void saveError(Throwable throwable, HttpServletRequest request, Integer userId) {
        Throwable cause = throwable.getCause();
        String persistenceCause = cause == null
                ? throwable.getClass().getName() : ExceptionUtils.getRootCauseMessage(throwable);
        String message = throwable.getMessage();
        String persistenceMessage = message == null
                ? throwable.getClass().getSimpleName() : ExceptionUtils.getMessage(throwable);
        String persistenceStackTrace = ExceptionUtils.getStackTrace(throwable);
        Long errorId = generateErrorId(persistenceMessage, persistenceCause, persistenceStackTrace);
        Database database = Imcms.getServices().getDatabase();
        try {
            database.execute(new InsertIntoTableDatabaseCommand("errors", new Object[][]{
                    {"error_id", errorId},
                    {"message", persistenceMessage},
                    {"cause", persistenceCause},
                    {"stack_trace", persistenceStackTrace}
            }));
        } catch (DatabaseException ignored) {
        }

        database.execute(new SqlUpdateCommand( "INSERT INTO errors_users_crossref (error_id, user_id) VALUES(?,?) " +
                                                "ON DUPLICATE KEY UPDATE times=times+1, update_date=now()",
                                                new Object[]{errorId, userId} )
        );

        request.setAttribute("error-id", errorId);
    }

    private Long generateErrorId(String persistenceMessage, String persistenceCause, String persistenceStackTrace) {
        long errorId = persistenceMessage.hashCode();
        errorId += persistenceCause.hashCode();
        errorId += persistenceStackTrace.hashCode();
        return errorId;
    }

}
