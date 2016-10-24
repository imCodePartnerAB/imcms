package com.imcode.imcms.servlet;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.imcode.db.Database;
import com.imcode.db.commands.SqlQueryCommand;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.HttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.jstl.core.Config;
import javax.servlet.jsp.jstl.fmt.LocalizationContext;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by ruslan on 04.10.16.
 */
public class InternalError extends HttpServlet {

    private final static Logger LOGGER = Logger.getLogger(InternalError.class);

    private final String DEFAULT_RESPONSE = "N/A";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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

    private void sendError(Throwable throwable, HttpServletRequest request, Integer userId) throws Exception {
        Database database = Imcms.getServices().getDatabase();

        Throwable causeThrowable = throwable.getCause();
        String cause = causeThrowable == null
                ? throwable.getClass().getName() : ExceptionUtils.getRootCauseMessage(throwable);
        String messageString = throwable.getMessage();
        String message = messageString == null
                ? throwable.getClass().getSimpleName() : messageString;
        String stackTrace = ExceptionUtils.getStackTrace(throwable);
        Long hash = generateHash(message, cause, stackTrace);
        String errorUrl = StringUtils.defaultString(request.getHeader("referer"), DEFAULT_RESPONSE);
        String userAgent = request.getHeader("user-agent");
        String headerAccept = request.getHeader("accept");
        String headerAcceptEncoding = request.getHeader("accept-encoding");
        String headerAcceptLanguage = request.getHeader("accept-language");

        String serverName = StringUtils.defaultString(Imcms.getServerName(), DEFAULT_RESPONSE);
        String databaseName = StringUtils.defaultString(Imcms.getServerProperties().getProperty("DBName"), DEFAULT_RESPONSE);
        String imcmsVersion = Version.getImcmsVersion(getServletContext());
        String databaseVersion = (String) database.execute(
                new SqlQueryCommand(
                        "SELECT CONCAT(major, '.', minor) FROM database_version",
                        null,
                        Utility.SINGLE_STRING_HANDLER
                )
        );

        String errorLoggerUrl = Imcms.getServerProperties().getProperty("ErrorLoggerUrl");
        if (errorLoggerUrl == null || errorLoggerUrl.isEmpty()) {
            errorLoggerUrl = Imcms.ERROR_LOGGER_URL;
        }

        List<NameValuePair> params = Form.form()

                        .add("hash", hash.toString())
                        .add("message", message)
                        .add("cause", cause)
                        .add("stack-trace", stackTrace)

                        .add("error-url", errorUrl)
                        .add("user-agent", userAgent)
                        .add("header-accept", headerAccept)
                        .add("header-accept-encoding", headerAcceptEncoding)
                        .add("header-accept-language", headerAcceptLanguage)

                        .add("server-name", serverName)
                        .add("database-name", databaseName)
                        .add("imcms-version", imcmsVersion)
                        .add("database-version", databaseVersion)

                        .add("user-id", userId.toString())

                        .build();

        HttpPost httpPost = new HttpPost(errorLoggerUrl);
        httpPost.setEntity(new UrlEncodedFormEntity(params));

        HttpClient client = createHttpClient();

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

    private HttpClient createHttpClient() throws Exception {

        HttpClientBuilder builder = HttpClientBuilder.create();

        SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
            public boolean isTrusted(X509Certificate[] x509Certificates, String str) throws CertificateException {
                return true;
            }
        }).build();
        builder.setSslcontext(sslContext);

        HostnameVerifier hostnameVerifier = SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;

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

        private List<NameValuePair> params = new LinkedList<NameValuePair>();

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
