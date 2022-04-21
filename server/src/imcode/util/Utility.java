package imcode.util;

import com.imcode.db.handlers.SingleObjectHandler;
import com.imcode.imcms.api.ContentManagementSystem;
import com.imcode.imcms.api.DefaultContentManagementSystem;
import com.imcode.imcms.db.*;
import com.imcode.imcms.servlet.VerifyUser;
import com.imcode.imcms.servlet.beans.SessionInfoDTO;
import com.imcode.imcms.util.l10n.LocalizedMessage;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.document.DocumentDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.io.FileUtility;
import org.apache.commons.collections.*;
import org.apache.commons.collections.iterators.ObjectArrayIterator;
import org.apache.commons.collections.iterators.TransformIterator;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.UnhandledException;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.logging.log4j.CloseableThreadContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.*;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerConfigurationException;
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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Utility {

    public static final ResultSetHandler<String> SINGLE_STRING_HANDLER = new SingleObjectHandler<>(new StringFromRowFactory());
    public static final ResultSetHandler<String[]> STRING_ARRAY_HANDLER = new StringArrayResultSetHandler();
    public static final ResultSetHandler<List<String>> STRING_LIST_HANDLER = new StringListResultSetHandler();
    public static final ResultSetHandler<List<Integer>> INTEGER_LIST_HANDLER = new IntegerListResultSetHandler();
    public static final ResultSetHandler<String[][]> STRING_ARRAY_ARRAY_HANDLER = new StringArrayArrayResultSetHandler();
    public static final String IM_TOKEN = "im_token";
    public static final String IM_TOKEN_DATE = "im_token_date";

    private final static Logger log = LogManager.getLogger(Utility.class.getName());
    private final static String CONTENT_MANAGEMENT_SYSTEM_REQUEST_ATTRIBUTE = "com.imcode.imcms.ImcmsSystem";
    private final static LocalizedMessage ERROR__NO_PERMISSION = new LocalizedMessage("templates/login/no_permission.html/4");
    private static final String LOGGED_IN_USER = "logon.isDone";
    private static final Pattern DOMAIN_PATTERN = Pattern.compile("^.*?([^.]+?\\.[^.]+)$");
    private static final Pattern IP_PATTERN = Pattern.compile("^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$");
    private static final int STATIC_FINAL_MODIFIER_MASK = Modifier.STATIC | Modifier.FINAL;
    private static final Map<String, SessionInfoDTO> sessions = new HashMap<>();

    private Utility() {

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
            throw new IllegalArgumentException("Not a valid IPv4 address: " + ip);
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

    public static UserDomainObject getLoggedOnUser(HttpServletRequest req) {
        return (UserDomainObject) req.getSession().getAttribute(LOGGED_IN_USER);
    }

    public static int compareDatesWithNullFirst(Date date1, Date date2) {
        if (null == date1 && null == date2) {
            return 0;
        } else if (null == date1) {
            return -1;
        } else if (null == date2) {
            return +1;
        } else {
            return date1.compareTo(date2);
        }
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

    public static void removeNullValuesFromMap(Map map) {
        Collection values = map.values();
        for (Iterator iterator = values.iterator(); iterator.hasNext(); ) {
            if (null == iterator.next()) {
                iterator.remove();
            }
        }
    }

    public static String createQueryStringFromParameterMultiMap(MultiMap requestParameters) {
        Set requestParameterStrings = SetUtils.orderedSet(new HashSet());
        for (Iterator iterator = requestParameters.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry entry = (Map.Entry) iterator.next();
            String parameterName = (String) entry.getKey();
            Collection parameterValues = (Collection) entry.getValue();
            for (Iterator valuesIterator = parameterValues.iterator(); valuesIterator.hasNext(); ) {
                String parameterValue = (String) valuesIterator.next();
                requestParameterStrings.add(URLEncoder.encode(parameterName) + "="
                        + URLEncoder.encode(parameterValue));
            }
        }
        return StringUtils.join(requestParameterStrings.iterator(), "&");
    }

    public static Collection collectImageDirectories() {
        ImcmsServices service = Imcms.getServices();
        final File imagePath = service.getConfig().getImagePath();
        return FileUtility.collectRelativeSubdirectoriesStartingWith(imagePath);
    }

    public static Object firstElementOfSetByOrderOf(Set set, Comparator comparator) {
        SortedSet sortedSet = new TreeSet(comparator);
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

    public static int[] convertStringArrayToIntArray(String[] strings) {
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

    public static String formatUser(UserDomainObject user) {
        return StringEscapeUtils.escapeHtml(user.getLastName() + ", " + user.getFirstName() + " (" + user.getLoginName() + ")");
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

    public static String formatHtmlDatetime(Date datetime) {
        if (null == datetime) {
            return "";
        }
        DateFormat dateFormat = new SimpleDateFormat(DateConstants.DATE_FORMAT_STRING + "'&nbsp;'"
                + DateConstants.TIME_NO_SECONDS_FORMAT_STRING);
        return dateFormat.format(datetime);
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
        request.getRequestDispatcher("/imcms/" + user.getLanguageIso639_2() + "/login/index.jsp?" + VerifyUser.REQUEST_PARAMETER__NEXT_URL + "=" + URLEncoder.encode(loginTarget.toString())).forward(request, response);
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

    public static void invokeInternalErrorServletWith(HttpServletRequest request, HttpServletResponse response, Exception e) {
        if (request == null) {
            throw new UnhandledException(e);
        }

        request.setAttribute("javax.servlet.error.exception", e);
        final com.imcode.imcms.servlet.InternalError internalErrorServlet = new com.imcode.imcms.servlet.InternalError();
        try {
            internalErrorServlet.doGet(request, response);
        } catch (Exception e2) {
            log.error("Error in error handler.....", e2);
            log.error("Cause of error in error handler:", e);
            e.printStackTrace();
        }
    }

    public static boolean classIsSignedByCertificatesInKeyStore(Class clazz, KeyStore keyStore) {
        Object[] signers = clazz.getSigners();
        if (null == signers) {
            return false;
        }
        for (int i = 0; i < signers.length; i++) {
            Object signer = signers[i];
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

    public static Map getMapViewOfObjectPairArray(final Object[][] array) {
        return new ArrayMap(array, new ObjectPairToMapEntryTransformer());
    }

    public static String makeSqlStringFromDate(Date date) {
        if (null == date) {
            return null;
        }
        return new SimpleDateFormat(DateConstants.DATETIME_FORMAT_STRING).format(date);
    }

    public static Date parseDateFormat(DateFormat dateFormat, String dateString) {
        try {
            return dateFormat.parse(dateString);
        } catch (NullPointerException npe) {
            return null;
        } catch (ParseException pe) {
            return null;
        }
    }

    public static Object findMatch(Factory factory, Predicate predicate) {
        Object unique;
        do {
            unique = factory.create();
        } while (!predicate.evaluate(unique));
        return unique;
    }

    public static String numberToAlphaNumerics(long identityHashCode) {
        return Long.toString(identityHashCode, Character.MAX_RADIX);
    }

    public static Integer getInteger(Object object) {
        return null == object ? null : ((Number) object).intValue();
    }

    public static String escapeUrl(String imageUrl) {
        return URLEncoder.encode(imageUrl).replaceAll("%2F", "/").replaceAll("\\+", "%20");
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

    public static void writeXmlDocument(Document xmlDocument,
                                        StreamResult streamResult) {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            javax.xml.transform.Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource xmlSource = new DOMSource(xmlDocument);
            transformer.transform(xmlSource, streamResult);
        } catch (TransformerConfigurationException e) {
            throw new UnhandledException(e);
        } catch (TransformerException e) {
            throw new UnhandledException(e);
        }
    }

    public static void makeUserLoggedIn(HttpServletRequest req, HttpServletResponse res, UserDomainObject user) {

        if (null != user && !user.isDefaultUser() && !req.isSecure() && Imcms.getServices().getConfig().getSecureLoginRequired()) {
            return;
        }
        req.getSession().setAttribute(LOGGED_IN_USER, user);
        final HttpSession currentSession = req.getSession();
        final Date loginDate = null != user.getLastLoginDate() ? user.getLastLoginDate() : new Date();

        SessionInfoDTO sessionInfoDTO = new SessionInfoDTO();
        sessionInfoDTO.setUserId(user.getId());
        sessionInfoDTO.setSessionId(currentSession.getId());
        sessionInfoDTO.setUserAgent(req.getHeader("User-Agent"));
        sessionInfoDTO.setIp(req.getRemoteAddr());
        sessionInfoDTO.setLoginDate(loginDate);
        sessionInfoDTO.setExpireDate(DateUtils.addSeconds(loginDate, req.getSession().getMaxInactiveInterval()));
        sessions.put(currentSession.getId(), sessionInfoDTO);

        setUserToken(req, res, user);

        if (null != user) {
            // FIXME: Ugly hack to get the contextpath into DefaultImcmsServices.getVelocityContext()
            user.setCurrentContextPath(req.getContextPath());
        }
    }

    public static void makeUserLoggedOut(HttpServletRequest req) {
        HttpSession session = req.getSession();
        sessions.remove(session.getId());
        session.removeAttribute(LOGGED_IN_USER);
        removeTokenSession(req);
        session.invalidate();
    }

    public static ContentManagementSystem initRequestWithApi(ServletRequest request, UserDomainObject currentUser) {
	    try (CloseableThreadContext.Instance ignored = CloseableThreadContext.push("initRequestWithApi")) {
		    ImcmsServices service = Imcms.getServices();
		    ContentManagementSystem imcmsSystem = DefaultContentManagementSystem.create(service, currentUser, Imcms.getApiDataSource());
		    request.setAttribute(CONTENT_MANAGEMENT_SYSTEM_REQUEST_ATTRIBUTE, imcmsSystem);
		    return imcmsSystem;
	    }
    }

    public static ContentManagementSystem getContentManagementSystemFromRequest(ServletRequest request) {
        return (ContentManagementSystem) request.getAttribute(CONTENT_MANAGEMENT_SYSTEM_REQUEST_ATTRIBUTE);
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

    public static void setUserToken(HttpServletRequest request, HttpServletResponse response, UserDomainObject user) {
        if(!user.isDefaultUser()){
            String token = getContentManagementSystemFromRequest(request).getUserService().generateNewRememberCd(user);
            Utility.setTokenToSession(request, token);
            Utility.setRememberCdCookie(request, response, token);
        }
    }

    private static void setTokenToSession(HttpServletRequest request, String token) {
        final HttpSession currentSession = request.getSession();
        currentSession.setAttribute(IM_TOKEN, token);
        currentSession.setAttribute(IM_TOKEN_DATE, System.currentTimeMillis());
    }

    private static void removeTokenSession(HttpServletRequest request) {
        final HttpSession currentSession = request.getSession();
        currentSession.setAttribute(IM_TOKEN, "");
        currentSession.setAttribute(IM_TOKEN_DATE, "");
    }

    private static void setRememberCdCookie(HttpServletRequest request, HttpServletResponse response, String token) {
        Cookie cookie = new Cookie(IM_TOKEN, token);
        cookie.setMaxAge(60 * 60 * 2);
        cookie.setPath("/");

        setCookieDomain(request, cookie);
        response.addCookie(cookie);
    }

    public static void removeRememberCdCookie(HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie = new Cookie(IM_TOKEN, "");
        cookie.setMaxAge(0);
        cookie.setPath("/");

        setCookieDomain(request, cookie);
        response.addCookie(cookie);
    }

    public static void setCookieDomain(HttpServletRequest request, Cookie cookie) {
        String serverName = request.getServerName();
        if (!IP_PATTERN.matcher(serverName).matches()) {
            Matcher matcher = DOMAIN_PATTERN.matcher(serverName);

            if (matcher.matches()) {
                cookie.setDomain(serverName);
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

    public static List<SessionInfoDTO> getActiveSessions() {
        Date currentDate = new Date();
        return sessions.values().stream()
                .filter(info -> info.getExpireDate().after(currentDate))
                .collect(Collectors.toList());
    }

    private static class ObjectPairToMapEntryTransformer implements Transformer {
        public Object transform(Object input) {
            final Object[] pair = (Object[]) input;
            return new Map.Entry() {
                public Object getKey() {
                    return pair[0];
                }

                public Object getValue() {
                    return pair[1];
                }

                public Object setValue(Object value) {
                    throw new UnsupportedOperationException();
                }
            };
        }
    }

    private static class ArrayMap extends AbstractMap {

        private final Object[] array;
        private Transformer transformer;

        ArrayMap(Object[] array, Transformer transformer) {
            this.array = array;
            this.transformer = transformer;
        }

        public Set entrySet() {
            return new AbstractSet() {
                public int size() {
                    return array.length;
                }

                public Iterator iterator() {
                    return new TransformIterator(new ObjectArrayIterator(array), transformer);
                }
            };
        }
    }

}