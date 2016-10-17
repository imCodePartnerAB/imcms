package com.imcode.imcms.servlet;


import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.impl.client.DefaultHttpClient;
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
            request.setAttribute("error-id", -1);
        }

        request.setAttribute("javax.servlet.error.exception" , null);

        ResourceBundle resourceBundle = Utility.getResourceBundle(request);
        Config.set(request, Config.FMT_LOCALIZATION_CONTEXT, new LocalizationContext(resourceBundle));

        request.getRequestDispatcher("/imcms/500.jsp").forward(request, response);
    }

    private void sendError(Throwable throwable, HttpServletRequest request, Integer userId) throws IOException {
        Throwable causeThrowable = throwable.getCause();
        String cause = causeThrowable == null
                ? throwable.getClass().getName() : ExceptionUtils.getRootCauseMessage(throwable);
        String messageString = throwable.getMessage();
        String message = messageString == null
                ? throwable.getClass().getSimpleName() : ExceptionUtils.getMessage(throwable);
        String stackTrace = ExceptionUtils.getStackTrace(throwable);
        Long hash = generateHash(message, cause, stackTrace);
        String errorUrl = request.getHeader("referer");

        String serverName = Imcms.getServerName();
        String jdbcUrl = Imcms.getServerProperties().getProperty("JdbcUrl");
        String databaseName = jdbcUrl.substring(jdbcUrl.lastIndexOf('/') + 1, jdbcUrl.contains("?")
                ? jdbcUrl.lastIndexOf('?') : jdbcUrl.length());

        List<NameValuePair> postParameters = new LinkedList<NameValuePair>();

        postParameters.add(new BasicNameValuePair("hash", hash.toString()));
        postParameters.add(new BasicNameValuePair("message", message));
        postParameters.add(new BasicNameValuePair("cause", cause));
        postParameters.add(new BasicNameValuePair("stack-trace", stackTrace));
        postParameters.add(new BasicNameValuePair("error-url", errorUrl));
        postParameters.add(new BasicNameValuePair("server-name", serverName));
        postParameters.add(new BasicNameValuePair("database-name", databaseName));
        postParameters.add(new BasicNameValuePair("user-id", userId.toString()));

        HttpPost httpPost = new HttpPost(Imcms.ERROR_LOGGER_URL);
        httpPost.setEntity(new UrlEncodedFormEntity(postParameters));
        HttpClient client = new DefaultHttpClient();
        HttpResponse response = client.execute(httpPost);
        HttpEntity entity = response.getEntity();

        String[] parameters = EntityUtils.toString(entity).split(";", 2);

        if (parameters[1].equals("new")) {
            LOGGER.error("Internal error has occurred: {errorId =" + parameters[0] + "; " + " userId =" + userId + "};");
        } else {
            LOGGER.info("Error with id " + parameters[0] + " is already reported");
        }

        request.setAttribute("error-id", parameters[0]);
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
