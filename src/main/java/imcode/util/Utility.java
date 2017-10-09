package imcode.util;

import com.imcode.db.handlers.SingleObjectHandler;
import com.imcode.imcms.I18nMessage$;
import com.imcode.imcms.api.ContentManagementSystem;
import com.imcode.imcms.api.DefaultContentManagementSystem;
import com.imcode.imcms.db.BooleanFromRowFactory;
import com.imcode.imcms.db.StringArrayArrayResultSetHandler;
import com.imcode.imcms.db.StringArrayResultSetHandler;
import com.imcode.imcms.db.StringFromRowFactory;
import com.imcode.imcms.domain.component.TextContentFilter;
import com.imcode.imcms.servlet.VerifyUser;
import com.imcode.imcms.util.l10n.LocalizedMessage;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentTypeDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.io.FileUtility;
import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.SetUtils;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.lang.UnhandledException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.log4j.Logger;
import org.apache.log4j.NDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.*;
import javax.servlet.jsp.PageContext;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class Utility {

    public static final ResultSetHandler SINGLE_STRING_HANDLER = new SingleObjectHandler(new StringFromRowFactory());
    public static final ResultSetHandler SINGLE_BOOLEAN_HANDLER = new SingleObjectHandler(new BooleanFromRowFactory());
    public static final ResultSetHandler STRING_ARRAY_HANDLER = new StringArrayResultSetHandler();
    public static final ResultSetHandler STRING_ARRAY_ARRAY_HANDLER = new StringArrayArrayResultSetHandler();
    private final static Logger log = Logger.getLogger(Utility.class.getName());
    private final static String CONTENT_MANAGEMENT_SYSTEM_REQUEST_ATTRIBUTE = "com.imcode.imcms.ImcmsSystem";
    private final static LocalizedMessage ERROR__NO_PERMISSION = new LocalizedMessage("templates/login/no_permission.html/4");
    private static final String LOGGED_IN_USER = "logon.isDone";
    private static final Pattern DOMAIN_PATTERN = Pattern.compile("^.*?([^.]+?\\.[^.]+)$");
    private static final Pattern IP_PATTERN = Pattern.compile("^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$");
    private static final int STATIC_FINAL_MODIFIER_MASK = Modifier.STATIC | Modifier.FINAL;

    private static TextContentFilter textContentFilter;

    private Utility() {
    }

    public static TextContentFilter getTextContentFilter() {
        return textContentFilter;
    }

    /**
     * Method checks is interested string contains any element of collection.
     *
     * @param amIContainsAny  - string to be checked on containing
     * @param elementsToCheck - collection of elements for check
     * @return true iff string contains any element of collection
     */
    public static boolean containsAny(final String amIContainsAny, Collection<String> elementsToCheck) {
        return elementsToCheck.stream().anyMatch(amIContainsAny::contains);
    }

    /**
     * Transforms a long containing an ip into a String.
     */
    public static String ipLongToString(long ip) {
        return (ip >>> 24 & 255) + "." + (ip >>> 16 & 255) + "." + (ip >>> 8 & 255) + "."
                + (ip & 255);
    }

    /**
     * Transforms a String containing an ip into a long.
     *
     * @throws IllegalArgumentException if the input is not a valid IPv4 address.
     */
    public static long ipStringToLong(String ip) throws IllegalArgumentException {
        long ipInt = 0;
        StringTokenizer ipTok = new StringTokenizer(ip, ".");
        if (4 != ipTok.countTokens()) {
            throw new IllegalArgumentException("Not a valid IPv4 address.");
        }
        for (int exp = 3; ipTok.hasMoreTokens(); --exp) {
            int ipNum = Integer.parseInt(ipTok.nextToken());
            ipInt += ipNum * Math.pow(256, exp);
        }
        return ipInt;
    }

    /**
     * Make a HttpServletResponse non-cacheable
     */
    public static void setNoCache(HttpServletResponse res) {
        res.setHeader("Cache-Control", "no-cache; must-revalidate;");
        res.setHeader("Pragma", "no-cache;");
        res.setDateHeader("Expires", 0);
    }

    public static boolean isTextDocument(DocumentDomainObject document) {
        return DocumentTypeDomainObject.TEXT == document.getDocumentType();
    }

    public static UserDomainObject getLoggedOnUser(HttpServletRequest req) {
        return getLoggedOnUser(req.getSession());
    }

    public static UserDomainObject getLoggedOnUser(HttpSession session) {
        return (UserDomainObject) session.getAttribute(LOGGED_IN_USER);
    }

    public static void setDefaultHtmlContentType(HttpServletResponse res) {
        res.setContentType("text/html; charset=" + Imcms.DEFAULT_ENCODING);
    }

    public static void redirectToStartDocument(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.sendRedirect(req.getContextPath() + "/servlet/StartDoc");
    }

    public static boolean isValidEmail(String email) {
        return EmailValidator.getInstance().isValid(email);
    }

    public static Collection collectImageDirectories() {
        ImcmsServices service = Imcms.getServices();
        final File imagePath = service.getConfig().getImagePath();
        return FileUtility.collectRelativeSubdirectoriesStartingWith(imagePath);
    }

    public static String firstElementOfSetByOrderOf(Set<String> set, Comparator<String> comparator) {
        SortedSet<String> sortedSet = new TreeSet<>(comparator);
        sortedSet.addAll(set);
        return sortedSet.iterator().next();
    }

    public static boolean parameterIsSet(HttpServletRequest request, String parameter) {
        return null != request.getParameter(parameter);
    }

    public static int[] getParameterInts(HttpServletRequest request, String parameterName) {
        String[] parameterValues = request.getParameterValues(parameterName);
        if (null == parameterValues) {
            return new int[0];
        }
        return convertStringArrayToIntArray(parameterValues);
    }

    private static int[] convertStringArrayToIntArray(String[] strings) {
        int[] parameterInts = new int[strings.length];
        for (int i = 0; i < strings.length; i++) {
            parameterInts[i] = Integer.parseInt(strings[i]);
        }
        return parameterInts;
    }

    public static String getContents(String path, HttpServletRequest request,
                                     HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher requestDispatcher = request.getRequestDispatcher(path);
        if (null == requestDispatcher) {
            throw new ServletException("No RequestDispatcher for path \"" + path + "\". Maybe the path doesn't exist?");
        }

        HttpServletResponseWrapper collectingHttpServletResponse = new CollectingHttpServletResponse(response);
        requestDispatcher.include(request, collectingHttpServletResponse);
        return collectingHttpServletResponse.toString();
    }

    public static String formatDate(Date oneWeekAgo) {
        return new SimpleDateFormat(DateConstants.DATE_FORMAT_STRING).format(oneWeekAgo);
    }

    public static String getAbsolutePathToDocument(HttpServletRequest request, DocumentDomainObject document) {
        if (null == document) {
            return null;
        }
        return request.getContextPath() + getContextRelativePathToDocument(document);
    }

    public static String getContextRelativePathToDocument(DocumentDomainObject document) {
        if (null == document) {
            return null;
        }
        return getContextRelativePathToDocumentWithName(document.getName());
    }

    public static String getContextRelativePathToDocumentWithName(String name) {
        String documentPathPrefix = Imcms.getServices().getConfig().getDocumentPathPrefix();
        if (StringUtils.isBlank(documentPathPrefix)) {
            documentPathPrefix = "/";
        }
        return documentPathPrefix + name;
    }

    private static String formatHtmlDatetimeWithSpecial(Date datetime, String nullable) {
        return (null == datetime) ? nullable : newHtmlSimpleDateFormat(datetime);
    }

    private static String newHtmlSimpleDateFormat(Date dateTime) {
        return new SimpleDateFormat(DateConstants.DATE_FORMAT_STRING + "'&nbsp;'"
                + DateConstants.TIME_NO_SECONDS_FORMAT_STRING).format(dateTime);
    }

    public static String formatHtmlDatetime(Date datetime) {
        return formatHtmlDatetimeWithSpecial(datetime, "");
    }

    public static String formatDateTime(Date dateTime) {
        return formatDatetimeWithSpecial(dateTime, "-- --");
    }

    private static String formatDatetimeWithSpecial(Date dateTime, String ifNull) {
        return (null == dateTime) ? ifNull : newSimpleDateFormat(dateTime);
    }

    private static String newSimpleDateFormat(Date dateTime) {
        return new SimpleDateFormat(DateConstants.DATE_FORMAT_STRING + " "
                + DateConstants.TIME_NO_SECONDS_FORMAT_STRING).format(dateTime);
    }

    public static void forwardToLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        forwardToLogin(request, response, HttpServletResponse.SC_FORBIDDEN);
    }

    public static void forwardToLogin(HttpServletRequest request, HttpServletResponse response, int responseStatus) throws ServletException, IOException {
        UserDomainObject user = getLoggedOnUser(request);
        StringBuffer loginTarget = request.getRequestURL();
        String queryString = request.getQueryString();
        if (null != queryString) {
            loginTarget.append("?").append(queryString);
        }

        response.setStatus(responseStatus);
        request.setAttribute(VerifyUser.REQUEST_ATTRIBUTE__ERROR, ERROR__NO_PERMISSION);
        request.getRequestDispatcher("/imcms/" + user.getLanguageIso639_2() + "/login/index.jsp?"
                + VerifyUser.REQUEST_PARAMETER__NEXT_URL + "=" + URLEncoder.encode(loginTarget.toString(), Imcms.UTF_8_ENCODING))
                .forward(request, response);
    }

    public static String[] getParameterValues(HttpServletRequest request, String parameterName) {
        String[] parameterValues = request.getParameterValues(parameterName);
        if (null == parameterValues) {
            parameterValues = new String[0];
        }
        return parameterValues;
    }

    public static Date truncateDateToMinutePrecision(Date fieldValue) {
        if (null == fieldValue) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fieldValue);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    public static String getRequestURLWithoutPath(HttpServletRequest request) {
        String requestUrl = request.getRequestURL().toString();
        int requestUrlStartOfHost = requestUrl.indexOf("://") + 3;
        int requestUrlStartOfPath = requestUrl.indexOf('/', requestUrlStartOfHost);
        return StringUtils.left(requestUrl, requestUrlStartOfPath);
    }

    public static boolean throwableContainsMessageContaining(Throwable t, String s) {
        Throwable throwable = t;
        while (null != throwable) {
            String message = throwable.getMessage();
            log.debug(throwable + ": " + message);
            if (null != message && message.contains(s)) {
                return true;
            }
            throwable = throwable.getCause();
        }
        return false;
    }

    public static boolean classIsSignedByCertificatesInKeyStore(Class clazz, KeyStore keyStore) {
        Object[] signers = clazz.getSigners();
        if (null == signers) {
            return false;
        }
        for (Object signer : signers) {
            if (!(signer instanceof Certificate)) {
                return false;
            }
            Certificate certificate = (Certificate) signer;
            try {
                if (null == keyStore.getCertificateAlias(certificate)) {
                    return false;
                }
            } catch (KeyStoreException e) {
                throw new UnhandledException(e);
            }
        }
        return true;
    }

    public static String makeSqlStringFromDate(Date date) {
        if (null == date) {
            return null;
        }
        return new SimpleDateFormat(DateConstants.DATETIME_FORMAT_STRING).format(date);
    }

    public static String escapeUrl(String imageUrl) {
        try {
            return URLEncoder.encode(imageUrl, Imcms.UTF_8_ENCODING).replaceAll("%2F", "/").replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Date addDate(Date date, int i) {
        if (null == date) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, i);
        return calendar.getTime();
    }

    public static void outputXmlDocument(HttpServletResponse response, Document xmlDocument) throws IOException {
        response.setContentType("text/xml; charset=UTF-8");
        writeXmlDocument(xmlDocument, new StreamResult(response.getOutputStream()));
    }

    public static void writeXmlDocument(Document xmlDocument, StreamResult streamResult) {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            javax.xml.transform.Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource xmlSource = new DOMSource(xmlDocument);
            transformer.transform(xmlSource, streamResult);
        } catch (TransformerException e) {
            throw new UnhandledException(e);
        }
    }

    public static void makeUserLoggedIn(HttpServletRequest req, UserDomainObject user) {
        if (null != user && !user.isDefaultUser() && !req.isSecure() && Imcms.getServices().getConfig().getSecureLoginRequired()) {
            return;
        }
        req.getSession().setAttribute(LOGGED_IN_USER, user);
        if (null != user) {
            // todo FIXME: Ugly hack to get the contextpath into DefaultImcmsServices.getVelocityContext()
            user.setCurrentContextPath(req.getContextPath());
        }
    }

    public static void makeUserLoggedOut(HttpServletRequest req) {
        // req.getSession().removeAttribute(LOGGED_IN_USER);
        HttpSession session = req.getSession(false);
        if (session != null) session.invalidate();
    }

    public static ContentManagementSystem initRequestWithApi(ServletRequest request, UserDomainObject currentUser) {
        NDC.push("initRequestWithApi");
        ImcmsServices service = Imcms.getServices();
        ContentManagementSystem imcmsSystem = DefaultContentManagementSystem.create(service, currentUser, Imcms.getApiDataSource());
        request.setAttribute(CONTENT_MANAGEMENT_SYSTEM_REQUEST_ATTRIBUTE, imcmsSystem);
        NDC.pop();
        return imcmsSystem;
    }

    public static ContentManagementSystem getContentManagementSystemFromRequest(ServletRequest request) {
        return (ContentManagementSystem) request.getAttribute(CONTENT_MANAGEMENT_SYSTEM_REQUEST_ATTRIBUTE);
    }

    public static ContentManagementSystem getCMS(ServletRequest request) {
        return getContentManagementSystemFromRequest(request);
    }

    public static ContentManagementSystem getCMS(PageContext context) {
        return getCMS(context.getRequest());
    }

    public static String fallbackUrlDecode(String input, FallbackDecoder fallbackDecoder) {
        Pattern p = Pattern.compile("%[0-9a-zA-Z]{2}|.", Pattern.DOTALL);
        Matcher matcher = p.matcher(input);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        while (matcher.find()) {
            String match = matcher.group();
            if (match.length() == 1) {
                char c = match.charAt(0);
                if (c < 256) {
                    out.write(c);
                }
            } else {
                int byteValue = Integer.parseInt(match.substring(1), 16);
                out.write(byteValue);
            }
        }
        return fallbackDecoder.decodeBytes(out.toByteArray(), null);
    }

    public static ResourceBundle getResourceBundle(HttpServletRequest request) {
        return Imcms.getServices().getLocalizedMessageProvider().getResourceBundle(Utility.getLoggedOnUser(request).getLanguageIso639_2());
    }

    public static void removeRememberCdCookie(HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie = new Cookie("im_remember_cd", "");
        cookie.setMaxAge(0);
        cookie.setPath("/");

        setCookieDomain(request, cookie);
        response.addCookie(cookie);
    }

    private static void setCookieDomain(HttpServletRequest request, Cookie cookie) {
        String serverName = request.getServerName();
        if (!IP_PATTERN.matcher(serverName).matches()) {
            Matcher matcher = DOMAIN_PATTERN.matcher(serverName);

            if (matcher.matches()) {
                cookie.setDomain("." + matcher.group(1));
            }
        }
    }

    // collects a set of "public static final" constants from a class into a map,
    // which then can be exposed to an JSP as a scoped variable
    public static Map<String, Object> getConstants(Class<?> klass) {
        Map<String, Object> constants = new HashMap<>();
        for (Field field : klass.getFields()) {
            if ((field.getModifiers() & STATIC_FINAL_MODIFIER_MASK) == STATIC_FINAL_MODIFIER_MASK) {
                try {
                    constants.put(field.getName(), field.get(null));
                } catch (Exception ex) {
                    log.warn(ex.getMessage(), ex);
                }
            }
        }

        return constants;
    }

    public static String encodeUrl(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            log.warn(ex.getMessage(), ex);

            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    /**
     * Pass through to {@link com.imcode.imcms.I18nMessage$#i(String)}.
     *
     * @param key a localisation key
     * @return a localised message for the {@code key}
     */
    public static String i(String key) {
        return I18nMessage$.MODULE$.i(key);
    }

    /**
     * @param key  a localisation key
     * @param args format paramaters for the localisation message
     * @return a localised message for the {@code key} formatted with the parameters {@code args}
     */
    public static String f(String key, Object... args) {
        return new MessageFormat(i(key)).format(args);
    }

    @SuppressWarnings("unchecked")
    public static String createQueryStringFromParameterMultiMap(MultiMap requestParameters) {
        Set<String> requestParameterStrings = SetUtils.orderedSet(new HashSet());
        for (Object o : requestParameters.entrySet()) {
            Map.Entry entry = (Map.Entry) o;
            String parameterName = (String) entry.getKey();
            Collection<String> parameterValues = (Collection) entry.getValue();
            for (String parameterValue : parameterValues) {
                try {
                    requestParameterStrings.add(URLEncoder.encode(parameterName, Imcms.UTF_8_ENCODING) + "="
                            + URLEncoder.encode(parameterValue, Imcms.UTF_8_ENCODING));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
        return StringUtils.join(requestParameterStrings.iterator(), "&");
    }

    @Autowired
    public void init(TextContentFilter textContentFilter) {
        Utility.textContentFilter = textContentFilter;
    }
}
