package com.imcode.imcms.servlet;

import com.imcode.db.Database;
import com.imcode.db.commands.InsertIntoTableDatabaseCommand;
import com.imcode.db.commands.SqlQueryCommand;
import com.imcode.db.commands.SqlUpdateCommand;
import com.imcode.db.exceptions.IntegrityConstraintViolationException;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.jstl.core.Config;
import javax.servlet.jsp.jstl.fmt.LocalizationContext;
import java.io.IOException;
import java.util.ResourceBundle;

/**
 * Created by ruslan on 04.10.16.
 */
public class InternalError extends HttpServlet {

    private final static Logger LOGGER = Logger.getLogger(InternalError.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Throwable exceptionFromRequest = (Throwable) request.getAttribute("javax.servlet.error.exception");

        UserDomainObject user = Utility.getLoggedOnUser(request);

        saveError(exceptionFromRequest, request, user.getId());

        request.setAttribute("javax.servlet.error.exception" , null);

        ResourceBundle resourceBundle = Utility.getResourceBundle(request);
        Config.set(request, Config.FMT_LOCALIZATION_CONTEXT, new LocalizationContext(resourceBundle));

        request.getRequestDispatcher("/imcms/500.jsp").forward(request, response);
    }

    private void saveError(Throwable throwable, HttpServletRequest request, Integer userId) {
        Throwable cause = throwable.getCause();
        String persistenceCause = cause == null
                ? throwable.getClass().getName() : ExceptionUtils.getRootCauseMessage(throwable);
        String message = throwable.getMessage();
        String persistenceMessage = message == null
                ? throwable.getClass().getSimpleName() : ExceptionUtils.getMessage(throwable);
        String persistenceStackTrace = ExceptionUtils.getStackTrace(throwable);
        Long hash = generateHash(persistenceMessage, persistenceCause, persistenceStackTrace);

        ImcmsServices imcref = Imcms.getServices();
        Database database = imcref.getDatabase();

        Long errorId;
        try {
            errorId = (Long) database.execute(new InsertIntoTableDatabaseCommand("errors", new Object[][]{
                    {"hash", hash},
                    {"message", persistenceMessage},
                    {"cause", persistenceCause},
                    {"stack_trace", persistenceStackTrace}
            }));
        } catch (IntegrityConstraintViolationException e) {
            errorId = Long.parseLong( (String)
                    database.execute(
                            new SqlQueryCommand(
                                "SELECT errors.error_id FROM errors WHERE hash = ? ",
                                new Object[]{ hash },
                                Utility.SINGLE_STRING_HANDLER
                            )
                    )
            );
            LOGGER.info("Error with id " + errorId + " is already reported");
        }

        String url = request.getHeader("referer");
        database.execute(new SqlUpdateCommand( "INSERT INTO errors_users_crossref (error_id, user_id, url) VALUES(?,?,?) " +
                                                "ON DUPLICATE KEY UPDATE times=times+1, update_date=now()",
                                                new Object[]{errorId, userId, url} )
        );

        LOGGER.info("Internal error has occurred: {errorId =" + errorId + "; " + " userId =" + userId + "};");

        request.setAttribute("error-id", errorId);
    }

    private Long generateHash(String persistenceMessage,
                              String persistenceCause,
                              String persistenceStackTrace) {
        long hashCode = persistenceMessage.hashCode();
        hashCode += persistenceCause.hashCode();
        hashCode += persistenceStackTrace.hashCode();
        return hashCode;
    }

}
