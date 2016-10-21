package com.imcode.imcms.servlet;


import com.fasterxml.jackson.databind.ObjectMapper;
import imcode.server.Imcms;
import imcode.util.Utility;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Stream;

import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.defaultString;

public class InternalError extends HttpServlet {

    private final String ERROR_500_VIEW_URL = "/imcms/500.jsp";

    private final String DEFAULT_RESPONSE = "N/A";

    private final static Logger LOGGER = Logger.getLogger(InternalError.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String attributeName = "javax.servlet.error.exception";
        Object exception = request.getAttribute(attributeName);
        request.setAttribute(attributeName, null);

        String userId = Utility.getLoggedOnUser(request).getId() + "";

        try {
            String responseJson = sendError((Throwable) exception, request, userId);
            String errorId = logError(responseJson, userId);
            request.setAttribute("errorId", errorId);
        } catch (Exception e) {
            request.setAttribute("errorId", 0);
        }

        request.getRequestDispatcher(ERROR_500_VIEW_URL).forward(request, response);
    }

    private String sendError(Throwable exception, HttpServletRequest request, String userId) throws Exception {
        Properties serverProperties = Imcms.getServerProperties();

        Map<String, String> exceptionInfo = parse(exception);

        Map<String, String> headersInfo = parse(request);

        String errorLoggerUrl = ofNullable(serverProperties.getProperty("ErrorLoggerUrl"))
                                    .map(url -> !url.isEmpty() ? url : Imcms.ERROR_LOGGER_URL)
                                    .orElse(Imcms.ERROR_LOGGER_URL);

        return Request.Post(errorLoggerUrl)
                .bodyForm(
                    Form.form()

                        .add("hash", exceptionInfo.get("hash"))
                        .add("message", exceptionInfo.get("message"))
                        .add("cause", exceptionInfo.get("cause"))
                        .add("stack-trace", exceptionInfo.get("stackTrace"))

                        .add("error-url", defaultString(headersInfo.get("errorUrl"), DEFAULT_RESPONSE))
                        .add("user-agent", headersInfo.get("userAgent"))
                        .add("header-accept", headersInfo.get("headerAccept"))
                        .add("header-accept-encoding", headersInfo.get("headerAcceptEncoding"))
                        .add("header-accept-language", headersInfo.get("headerAcceptLanguage"))

                        .add("server-name", defaultString(serverProperties.getProperty("ServerName"), DEFAULT_RESPONSE))
                        .add("database-name", defaultString(serverProperties.getProperty("DBName"), DEFAULT_RESPONSE))
                        .add("imcms-version", Version.getImcmsVersion(getServletContext()))
                        .add("database-version", defaultString(Version.getRequiredDbVersion(), DEFAULT_RESPONSE))

                        .add("user-id", userId)

                        .build()
                )
                .execute()
                .returnContent()
                .asString();
    }

    private Map<String, String> parse(Throwable exception) {
        Map<String, String> exceptionInfo = new HashMap<>();

        String cause = ofNullable(exception.getCause())
                .map(ExceptionUtils::getRootCauseMessage)
                .orElse(exception.getClass().getName());
        exceptionInfo.put("cause", cause);

        String message = ofNullable(exception.getMessage())
                .map(String::new)
                .orElse(exception.getClass().getSimpleName());
        exceptionInfo.put("message", message);

        String stackTrace = ExceptionUtils.getStackTrace(exception);
        exceptionInfo.put("stackTrace", stackTrace);

        Long hash = Stream.of(cause, message, stackTrace)
                .mapToLong(String::hashCode)
                .sum();
        exceptionInfo.put("hash", hash.toString());

        return exceptionInfo;
    }

    private Map<String, String> parse(HttpServletRequest request) {
        Map<String, String> headersInfo = new HashMap<>();

        headersInfo.put("errorUrl", request.getHeader("referer"));
        headersInfo.put("userAgent", request.getHeader("user-agent"));
        headersInfo.put("headerAccept", request.getHeader("accept"));
        headersInfo.put("headerAcceptEncoding", request.getHeader("accept-encoding"));
        headersInfo.put("headerAcceptLanguage", request.getHeader("accept-language"));

        return headersInfo;
    }

    private String logError(String response, String userId) throws IOException {
        HashMap<String, Object> responseMap = new ObjectMapper().readValue(response, HashMap.class);
        String errorId = (String) responseMap.get("error_id");
        String state = (String) responseMap.get("state");
        if (state.equals("new")) {
            LOGGER.error("Internal error has occurred: {errorId =" + errorId + "; " + " userId =" + userId + "};");
        } else {
            LOGGER.info("Error with id " + errorId + " is already reported");
        }
        return errorId;
    }

}
