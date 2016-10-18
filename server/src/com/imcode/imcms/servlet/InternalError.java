package com.imcode.imcms.servlet;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.imcode.db.Database;
import com.imcode.db.commands.SqlQueryCommand;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.HttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.jstl.core.Config;
import javax.servlet.jsp.jstl.fmt.LocalizationContext;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
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

        try {
            sendError(exceptionFromRequest, request, user.getId());
        } catch (Exception e) {
            request.setAttribute("error-id", 0);
        }

        request.setAttribute("javax.servlet.error.exception" , null);

        ResourceBundle resourceBundle = Utility.getResourceBundle(request);
        Config.set(request, Config.FMT_LOCALIZATION_CONTEXT, new LocalizationContext(resourceBundle));

        request.getRequestDispatcher("/imcms/500.jsp").forward(request, response);
    }

    private void sendError(Throwable throwable, HttpServletRequest request, Integer userId) throws IOException {
        Database database = Imcms.getServices().getDatabase();

        Throwable causeThrowable = throwable.getCause();
        String cause = causeThrowable == null
                ? throwable.getClass().getName() : ExceptionUtils.getRootCauseMessage(throwable);
        String messageString = throwable.getMessage();
        String message = messageString == null
                ? throwable.getClass().getSimpleName() : messageString;
        String stackTrace = ExceptionUtils.getStackTrace(throwable);
        Long hash = generateHash(message, cause, stackTrace);

        String errorUrl = request.getHeader("referer");
        String userAgent = request.getHeader("user-agent");
        String headerAccept = request.getHeader("accept");
        String headerAcceptEncoding = request.getHeader("accept-encoding");
        String headerAcceptLanguage = request.getHeader("accept-language");

        String serverName = Imcms.getServerName();
        String jdbcUrl = Imcms.getServerProperties().getProperty("JdbcUrl");
        String databaseName = jdbcUrl.substring(jdbcUrl.lastIndexOf('/') + 1, jdbcUrl.contains("?")
                ? jdbcUrl.lastIndexOf('?') : jdbcUrl.length());
        String imcmsVersion = Version.getImcmsVersion(getServletContext());
        String databaseVersion = (String) database.execute(
                new SqlQueryCommand(
                        "SELECT CONCAT('major=', major, '; ', 'minor=', minor) FROM database_version",
                        null,
                        Utility.SINGLE_STRING_HANDLER
                )
        );

        List<NameValuePair> postParameters = new LinkedList<NameValuePair>();

        postParameters.add(new BasicNameValuePair("hash", hash.toString()));
        postParameters.add(new BasicNameValuePair("message", message));
        postParameters.add(new BasicNameValuePair("cause", cause));
        postParameters.add(new BasicNameValuePair("stack-trace", stackTrace));

        postParameters.add(new BasicNameValuePair("error-url", errorUrl));
        postParameters.add(new BasicNameValuePair("user-agent", userAgent));
        postParameters.add(new BasicNameValuePair("header-accept", headerAccept));
        postParameters.add(new BasicNameValuePair("header-accept-encoding", headerAcceptEncoding));
        postParameters.add(new BasicNameValuePair("header-accept-language", headerAcceptLanguage));

        postParameters.add(new BasicNameValuePair("server-name", serverName));
        postParameters.add(new BasicNameValuePair("database-name", databaseName));
        postParameters.add(new BasicNameValuePair("imcms-version", imcmsVersion));
        postParameters.add(new BasicNameValuePair("database-version", databaseVersion));

        postParameters.add(new BasicNameValuePair("user-id", userId.toString()));

        HttpPost httpPost = new HttpPost(Imcms.ERROR_LOGGER_URL);
        httpPost.setEntity(new UrlEncodedFormEntity(postParameters));
        HttpClient client =  HttpClientBuilder.create().build();
        HttpResponse response = client.execute(httpPost);
        HttpEntity entity = response.getEntity();

        JsonParser parser = new JsonParser();
        JsonObject jsonResponse = parser.parse(EntityUtils.toString(entity)).getAsJsonObject();

        String state = jsonResponse.get("state").getAsString();
        String errorId = jsonResponse.get("error_id").getAsString();
        if (state.equals("new")) {
            LOGGER.error("Internal error has occurred: {errorId =" + errorId + "; " + " userId =" + userId + "};");
        } else {
            LOGGER.info("Error with id " + errorId + " is already reported");
        }

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
