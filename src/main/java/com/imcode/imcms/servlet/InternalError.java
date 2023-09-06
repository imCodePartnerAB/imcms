package com.imcode.imcms.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Stream;

import static java.util.Optional.ofNullable;
import static org.apache.commons.lang.StringEscapeUtils.escapeHtml;
import static org.apache.commons.lang3.StringUtils.defaultString;

public class InternalError extends HttpServlet {

    private final static Logger LOGGER = LogManager.getLogger(InternalError.class);
    private final static String ERROR_500_VIEW_URL = "/imcms/500.jsp";
    private final static String DEFAULT_RESPONSE = "N/A";
    private static final long serialVersionUID = 4218112557790098610L;

    private Properties properties;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        final ServletContext servletContext = config.getServletContext();
        final ApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
        this.properties = ctx.getBean("imcmsProperties", Properties.class);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String attributeName = "javax.servlet.error.exception";
        Object exception = request.getAttribute(attributeName);
        request.setAttribute(attributeName, null);

        int userId = Optional.ofNullable(Utility.getLoggedOnUser(request))
                .map(UserDomainObject::getId)
               .orElse(2);
        String errorId;
        try {
            String responseJson = sendError((Throwable) exception, request, userId);
            errorId = logError(responseJson, (Throwable) exception, userId);
        } catch (Exception e) {

            LOGGER.error("Exception when sending an error to ICM", e);

            errorId = "0";
            logError((Throwable) exception, errorId, userId);
        }

        request.setAttribute("errorId", errorId);
        request.getRequestDispatcher(ERROR_500_VIEW_URL).forward(request, response);
    }

    private String sendError(Throwable exception, HttpServletRequest request, int userId) throws Exception {
        Map<String, String> exceptionInfo = parse(exception);
        Map<String, String> requestInfo = parse(request);

        setInfo(request, exceptionInfo, requestInfo);

        String errorLoggerUrl = ofNullable(properties.getProperty("ErrorLoggerUrl"))
                .filter(url -> !url.isEmpty())
                .orElse(Imcms.ERROR_LOGGER_URL);

        String jdbcUrl = properties.getProperty("JdbcUrl");
        String dbName = jdbcUrl.substring(jdbcUrl.lastIndexOf("/"),
                jdbcUrl.contains("?") ? jdbcUrl.lastIndexOf('?') : jdbcUrl.length());

        HttpClient httpClient = createHttpClient();

        List<NameValuePair> params = Form.form()

                .add("hash", exceptionInfo.get("hash"))
                .add("message", exceptionInfo.get("message"))
                .add("cause", exceptionInfo.get("cause"))
                .add("stack-trace", exceptionInfo.get("stackTrace"))

                .add("error-url", defaultString(requestInfo.get("errorUrl"), DEFAULT_RESPONSE))
                .add("user-agent", requestInfo.get("userAgent"))
                .add("header-accept", requestInfo.get("headerAccept"))
                .add("header-accept-encoding", requestInfo.get("headerAcceptEncoding"))
                .add("header-accept-language", requestInfo.get("headerAcceptLanguage"))
                .add("server-name", requestInfo.get("serverName"))

                .add("database-name", dbName)

                .add("imcms-version", Version.getImcmsVersion(getServletContext()))
                .add("database-version", defaultString(Version.getRequiredDbVersion(), DEFAULT_RESPONSE))

                .add("user-id", String.valueOf(userId))

                .build();

        HttpPost post = new HttpPost(errorLoggerUrl);

        post.setEntity(new UrlEncodedFormEntity(params, StandardCharsets.UTF_8));

        return EntityUtils.toString(httpClient.execute(post).getEntity());
    }

    private void setInfo(HttpServletRequest request, Map<String, String> exceptionInfo, Map<String, String> headersInfo) {
        request.setAttribute("message", escapeHtml(exceptionInfo.get("message")));
        request.setAttribute("cause", escapeHtml(exceptionInfo.get("cause")));
        request.setAttribute("stackTrace", escapeHtml(exceptionInfo.get("stackTrace")));
        request.setAttribute("errorUrl", defaultString(headersInfo.get("errorUrl"), "unknown"));
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
        Map<String, String> requestInfo = new HashMap<>();

        requestInfo.put("errorUrl", request.getHeader("referer"));
        requestInfo.put("userAgent", request.getHeader("user-agent"));
        requestInfo.put("headerAccept", request.getHeader("accept"));
        requestInfo.put("headerAcceptEncoding", request.getHeader("accept-encoding"));
        requestInfo.put("headerAcceptLanguage", request.getHeader("accept-language"));
        requestInfo.put("serverName", request.getServerName());

        return requestInfo;
    }

    private String logError(String response, Throwable exception, int userId) throws IOException {
        HashMap<String, Object> responseMap = new ObjectMapper().readValue(response, HashMap.class);
        String errorId = (String) responseMap.get("error_id");
        String state = (String) responseMap.get("state");
        if (state.equals("new")) {
            logError(exception, errorId, userId);
        } else {
            LOGGER.info("Error with id " + errorId + " is already reported");
        }
        return errorId;
    }

    private void logError(Throwable exception, String errorId, int userId) {
        LOGGER.error("Internal error has occurred: {errorId =" + errorId + "; " + " userId =" + userId + "};", exception);
    }

    private HttpClient createHttpClient() throws Exception {

        HttpClientBuilder builder = HttpClientBuilder.create();

        SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, (x509Certificates, str) -> true).build();
        builder.setSSLContext(sslContext);

        HostnameVerifier hostnameVerifier = NoopHostnameVerifier.INSTANCE;

        SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext, hostnameVerifier);
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", sslSocketFactory)
                .build();

        PoolingHttpClientConnectionManager connMgr = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        builder.setConnectionManager(connMgr);

        return builder.build();

    }

    private static class Form {

        private List<NameValuePair> params = new LinkedList<>();

        private Form() {
        }

        public static Form form() {
            return new Form();
        }

        public Form add(String name, String value) {
            params.add(new BasicNameValuePair(name, value));
            return this;
        }

        public List<NameValuePair> build() {
            return params;
        }

    }

}
