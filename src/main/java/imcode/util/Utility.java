package imcode.util;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.imcode.db.handlers.SingleObjectHandler;
import com.imcode.imcms.api.ContentManagementSystem;
import com.imcode.imcms.components.CSRFTokenManager;
import com.imcode.imcms.db.BooleanFromRowFactory;
import com.imcode.imcms.db.StringArrayArrayResultSetHandler;
import com.imcode.imcms.db.StringArrayResultSetHandler;
import com.imcode.imcms.db.StringFromRowFactory;
import com.imcode.imcms.domain.component.TextContentFilter;
import com.imcode.imcms.domain.dto.ImageDTO;
import com.imcode.imcms.domain.dto.PhoneDTO;
import com.imcode.imcms.domain.dto.SessionInfoDTO;
import com.imcode.imcms.domain.dto.UserFormData;
import com.imcode.imcms.domain.service.LanguageService;
import com.imcode.imcms.model.Language;
import com.imcode.imcms.model.Phone;
import com.imcode.imcms.model.PhoneType;
import com.imcode.imcms.model.PhoneTypes;
import com.imcode.imcms.persistence.entity.User;
import com.imcode.imcms.servlet.ImageFetcher;
import com.imcode.imcms.servlet.VerifyUser;
import com.imcode.imcms.util.l10n.LocalizedMessage;
import imcode.server.Imcms;
import imcode.server.ImcmsConstants;
import imcode.server.ImcmsServices;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentTypeDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.io.FileUtility;
import lombok.SneakyThrows;
import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.SetUtils;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.lang.UnhandledException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.http.client.utils.URIBuilder;
import org.apache.logging.log4j.CloseableThreadContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.*;
import javax.servlet.jsp.PageContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.imcode.imcms.servlet.ImcmsSetupFilter.USER_LANGUAGE_IN_COOKIE_NAME;

@Component
public class Utility {

	public static final ResultSetHandler<String> SINGLE_STRING_HANDLER = new SingleObjectHandler<>(new StringFromRowFactory());
	public static final ResultSetHandler<Boolean> SINGLE_BOOLEAN_HANDLER = new SingleObjectHandler<>(new BooleanFromRowFactory());
	public static final ResultSetHandler<String[]> STRING_ARRAY_HANDLER = new StringArrayResultSetHandler();
	public static final ResultSetHandler<String[][]> STRING_ARRAY_ARRAY_HANDLER = new StringArrayArrayResultSetHandler();

	public static final Predicate<Date> isDateInFutureOrNull = date -> (date == null) || new Date().before(date);
	public static final Predicate<Date> isDateInFuture = date -> (date != null) && new Date().before(date);
	public static final Predicate<Date> isDateInPast = date -> (date != null) && new Date().after(date);
	public static final BiPredicate<Date, Date> isDateBefore = (date1, date2) -> (date1 != null) && (date2 != null) && date1.before(date2);

	private static final String CONTENT_MANAGEMENT_SYSTEM_REQUEST_ATTRIBUTE = "com.imcode.imcms.ImcmsSystem";
	private static final LocalizedMessage ERROR__NO_PERMISSION = new LocalizedMessage("templates/login/no_permission.html/4");
	private static final String LOGGED_IN_USER = "logon.isDone";

	private static final Pattern DOMAIN_PATTERN = Pattern.compile("^.*?([^.]+?\\.[^.]+)$");
	private static final Pattern IP_PATTERN = Pattern.compile("^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$");
	private static final int STATIC_FINAL_MODIFIER_MASK = Modifier.STATIC | Modifier.FINAL;

	private static final Logger log = LogManager.getLogger(Utility.class);
	private static final Logger logGDPR = LogManager.getLogger(ImcmsConstants.GDPR_LOG);

	private static final Map<String, SessionInfoDTO> sessions = new HashMap<>();

	private static TextContentFilter textContentFilter;
	private static ImcmsServices services;
	private static CSRFTokenManager csrfTokenManager;
	private static String storageImagePath;

	public Utility(TextContentFilter textContentFilter,
				   ImcmsServices services,
				   CSRFTokenManager csrfTokenManager,
				   @Qualifier("storageImagePath") String storageImagePath) {
		Utility.textContentFilter = textContentFilter;
		Utility.services = services;
		Utility.csrfTokenManager = csrfTokenManager;
		Utility.storageImagePath = storageImagePath;
	}

	public static TextContentFilter getTextContentFilter() {
		return textContentFilter;
	}

	/**
	 * String normalize, escapes special characters and whitespace.
	 *
	 * @param normalizeMe string to normalize
	 * @return normalized string
	 */
	public static String normalizeString(String normalizeMe) {
		String[][] specialCharacterReplacements = {
				{"\u00e5", "a"},// å
				{"\u00c5", "A"},
				{"\u00e4", "a"},// ä
				{"\u00c4", "A"},
				{"\u00f6", "o"},// ö
				{"\u00d6", "O"},
				{"\u00e9", "e"},// é
				{"\u00c9", "E"},
				{"\u00f8", "o"},// ø
				{"\u00d8", "O"},
				{"\u00e6", "ae"},// æ
				{"\u00c6", "AE"},
				{"\u0020", "_"} // space
		};

		normalizeMe = Normalizer.normalize(normalizeMe, Normalizer.Form.NFC);

		for (String[] replacement : specialCharacterReplacements) {
			normalizeMe = normalizeMe.replace(replacement[0], replacement[1]);
		}

		return normalizeMe;
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
	@Deprecated
	public static String ipLongToString(long ip) {
		return (ip >>> 24 & 255) + "." + (ip >>> 16 & 255) + "." + (ip >>> 8 & 255) + "."
				+ (ip & 255);
	}

	/**
	 * Transforms a String containing an ip into a long.
	 *
	 * @throws IllegalArgumentException if the input is not a valid IPv4 address.
	 */
	@Deprecated
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
		return DocumentTypeDomainObject.TEXT.equals(document.getDocumentType());
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
		res.sendRedirect(req.getContextPath() + "/");
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

	/**
	 * Also this method is using by clients
	 */
	public static int[] convertStringArrayToIntArray(String[] strings) {
		int[] parameterInts = new int[strings.length];
		for (int i = 0; i < strings.length; i++) {
			parameterInts[i] = Integer.parseInt(strings[i]);
		}
		return parameterInts;
	}

	public static String getAdminContents(String templateName, HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		final String templatePath = services.getAdminTemplatePath(templateName);
		return getContents(templatePath, request, response);
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

	/**
	 * @deprecated use {@link #extractDocumentIdentifier(String)} instead
	 */
	@Deprecated
	public static String getAbsolutePathToDocument(HttpServletRequest request, DocumentDomainObject document) {
		if (null == document) {
			return null;
		}
		return request.getContextPath() + getContextRelativePathToDocument(document);
	}

	/**
	 * @deprecated use {@link #extractDocumentIdentifier(String)} instead
	 */
	@Deprecated
	public static String getContextRelativePathToDocument(DocumentDomainObject document) {
		if (null == document) {
			return null;
		}
		return getContextRelativePathToDocumentWithName(document.getName());
	}

	/**
	 * @deprecated use {@link #extractDocumentIdentifier(String)} instead
	 */
	@Deprecated
	public static String getContextRelativePathToDocumentWithName(String name) {
		String documentPathPrefix = Imcms.getServices().getConfig().getDocumentPathPrefix();
		if (StringUtils.isBlank(documentPathPrefix)) {
			documentPathPrefix = "/";
		}
		return documentPathPrefix + name;
	}

	public static String extractDocumentIdentifier(String path){
		final String documentPathPrefix = Imcms.getServices().getConfig().getDocumentPathPrefix();

		String documentId = null;
		if (StringUtils.isNotBlank(documentPathPrefix) && path.startsWith(documentPathPrefix)){
			documentId = path.substring(documentPathPrefix.length());

			if (documentId.endsWith("/")) documentId = documentId.substring(0, documentId.length() - 1);
		}
		return documentId;
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
		forwardToLogin(request, response, responseStatus, request.getRequestURL());
	}

	public static void forwardToLogin(final HttpServletRequest request,
	                                  final HttpServletResponse response,
	                                  final int responseStatus,
	                                  final StringBuffer loginTarget) throws IOException, ServletException {

		String queryString = request.getQueryString();
		if (null != queryString) {
			loginTarget.append("?").append(queryString);
		}

		response.setStatus(responseStatus);
		request.setAttribute(VerifyUser.REQUEST_ATTRIBUTE__ERROR, ERROR__NO_PERMISSION);
		final String loginPathWithNextUrl = ImcmsConstants.API_PREFIX + ImcmsConstants.LOGIN_URL + "?"
				+ VerifyUser.REQUEST_PARAMETER__NEXT_URL + "="
				+ URLEncoder.encode(loginTarget.toString(), Imcms.UTF_8_ENCODING);

		request.getRequestDispatcher(loginPathWithNextUrl).forward(request, response);
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

	public static String escapeValue(String value) {
		return HtmlUtils.htmlEscape(value, Imcms.DEFAULT_ENCODING);
	}

	public static String escapeValue(String value, String... exceptSymbols) {
		String escapedValue = HtmlUtils.htmlEscape(value, Imcms.DEFAULT_ENCODING);
		for (String symbol : exceptSymbols) {
			escapedValue = escapedValue.replace(HtmlUtils.htmlEscape(symbol, Imcms.DEFAULT_ENCODING), symbol);
		}
		return escapedValue;
	}

	public static String unescapeValue(String value) {
		return HtmlUtils.htmlUnescape(value);
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

	public static void makeUserLoggedIn(HttpServletRequest req, HttpServletResponse res, UserDomainObject user) {
		if (null != user && !user.isDefaultUser() && !req.isSecure() && Imcms.getServices().getConfig().getSecureLoginRequired()) {
			return;
		}

		final HttpSession currentSession = req.getSession();
		final LocalDateTime loginDateTime = LocalDateTime.now();

		SessionInfoDTO sessionInfoDTO = new SessionInfoDTO();
		sessionInfoDTO.setUserId(user.getId());
		sessionInfoDTO.setSessionId(currentSession.getId());
		sessionInfoDTO.setUserAgent(req.getHeader("User-Agent"));
		sessionInfoDTO.setIp(req.getRemoteAddr());
		sessionInfoDTO.setLoginDate(loginDateTime);
		sessionInfoDTO.setExpireDate(loginDateTime.plusSeconds(req.getSession().getMaxInactiveInterval()));
		sessions.put(currentSession.getId(), sessionInfoDTO);

		csrfTokenManager.setUserToken(req, res);
		req.getSession().setAttribute(LOGGED_IN_USER, user);
	}

	public static void makeUserLoggedOut(HttpServletRequest req, HttpServletResponse res) {
		HttpSession session = req.getSession(false);
		if (session != null) {
			sessions.remove(session.getId());
			csrfTokenManager.deleteUserToken(req, res);
			session.invalidate();
		}
	}

	public static List<SessionInfoDTO> getActiveSessions() {
		final LocalDateTime currentDateTime = LocalDateTime.now();
		return sessions.values().stream()
				.filter(info -> info.getExpireDate().isAfter(currentDateTime))
				.collect(Collectors.toList());
	}

	public static boolean userHasActiveSession(Integer id){
		return getActiveSessions().stream().anyMatch(info -> info.getUserId() == id);
	}

	public static ContentManagementSystem initRequestWithApi(ServletRequest request, UserDomainObject currentUser) {
		try (CloseableThreadContext.Instance ignored = CloseableThreadContext.push("initRequestWithApi")) {
			ImcmsServices service = Imcms.getServices();
			ContentManagementSystem imcmsSystem = ContentManagementSystem.create(service, currentUser);
			request.setAttribute(CONTENT_MANAGEMENT_SYSTEM_REQUEST_ATTRIBUTE, imcmsSystem);
			return imcmsSystem;
		}
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

	public static String decodePathFromRequest(HttpServletRequest request, FallbackDecoder decoder) {
		String path = fallbackUrlDecode(request.getRequestURI(), decoder);
		return StringUtils.substringAfter(path, request.getContextPath());
	}

	/**
	 * @param path from request
	 * @return start document id if path empty
	 */
	public static String updatePathIfEmpty(String path) {
		if ("/".equals(path)) {
			path = "/" + services.getSystemData().getStartDocument();
		}
		return path;
	}

	public static Document xmlStringToDocument(String xml) {
		try {
			final DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			final InputSource src = new InputSource(new StringReader(xml));

			return builder.parse(src);
		} catch (ParserConfigurationException | IOException | SAXException e) {
			log.error("Error during parsing xml string", e);
		}
		return null;
	}

	public static void writeUserLanguageCookie(HttpServletResponse response, String langCode) {
		if (langCode == null) {
			return;
		}
		final Cookie newUserLanguageCookie = new Cookie(USER_LANGUAGE_IN_COOKIE_NAME, langCode);
		newUserLanguageCookie.setMaxAge(Integer.MAX_VALUE);
		newUserLanguageCookie.setPath("/");

		response.addCookie(newUserLanguageCookie);
	}

    public static LocalDateTime convertDateToLocalDateTime(Date date){
        if(date == null) return null;
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

	public static LocalDate convertDateToLocalDate(Date date){
		if(date == null) return null;
		return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}

	public static Date convertLocalDateToDate(LocalDate localDate){
		if(localDate == null) return null;
		return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
	}

	/**
	 * @return mapped langCode from Cookie to Language, if cookie not available get default language
	 */
	public static Language getUserLanguageFromCookie(Cookie[] cookies) {
		final LanguageService languageService = Imcms.getServices().getLanguageService();

		return Stream.ofNullable(cookies)
				.flatMap(Arrays::stream)
				.filter(cookie -> cookie.getName().equals(USER_LANGUAGE_IN_COOKIE_NAME))
				.map(Cookie::getValue)
				.filter(languageService::isLanguageAvailableByCode)
				.findFirst()
				.map(languageService::findByCode)
				.orElseGet(languageService::getDefaultLanguage);
	}

	public static boolean isMoreThanOneLanguageAvailable(){
		return Imcms.getServices().getLanguageService().getAvailableLanguages().size() > 1;
	}

	public static void logGDPR(int editableUserId, String message){
		int byId = Imcms.getUser().isDefaultUser() ? editableUserId : Imcms.getUser().getId();
		logGDPR.info(String.format("- User id: %d - Account id: %d - %s", byId, editableUserId, message));
	}

	/**
	 * Compare values of all variables of the two classes. The classes must be the same.
	 *
	 * @return field names with mismatched value.
	 */
	@SneakyThrows
	public static List<String> findFieldsWithMismatchedValue(Object o1, Object o2) {
		if(!o1.getClass().equals(o2.getClass())) return Collections.emptyList();

		final ArrayList<String> fieldNames = new ArrayList<>();

		final Field[] declaredFields1 = o1.getClass().getDeclaredFields();
		final Field[] declaredFields2 = o2.getClass().getDeclaredFields();
		for(int i=0; i<declaredFields1.length; i++){
			Field field1 = declaredFields1[i];
			field1.setAccessible(true);

			Field field2 = declaredFields2[i];
			field2.setAccessible(true);

			if(!Objects.equals(field1.get(o1), field2.get(o2))) fieldNames.add(field1.getName());
		}

		return fieldNames;
	}

	public static List<Phone> collectPhoneNumbers(UserFormData userData, User user) {

		final String[] userPhoneNumbers = userData.getUserPhoneNumber();
		final Integer[] userPhoneNumberTypes = userData.getUserPhoneNumberType();

		if ((userPhoneNumbers == null)
				|| (userPhoneNumberTypes == null)
				|| (userPhoneNumbers.length <= 0)
				|| (userPhoneNumberTypes.length <= 0)
				|| (userPhoneNumbers.length != userPhoneNumberTypes.length)) { // actually I don't know what to do if arrays have different length, however null and zero-length is fine
			return Collections.emptyList();
		}

		List<Phone> numbers = new ArrayList<>();

		for (int i = 0; i < userPhoneNumbers.length; i++) {
			try {
				final String userPhoneNumber = userPhoneNumbers[i];
				final PhoneType numberType = PhoneTypes.getPhoneTypeById(userPhoneNumberTypes[i]);
				numbers.add(new PhoneDTO(userPhoneNumber, user, numberType));

			} catch (Exception e) {
				log.error("Something wrong with phone numbers.", e);
			}
		}

		return numbers;
	}

	public static boolean isMobilePhoneNumberValid(String phoneNumber) {
		try {
			PhoneNumberUtil.getInstance().parse(phoneNumber, "");
			return true;
		} catch (NumberParseException e) {
			return false;
		}
	}

	@SneakyThrows
	public static String buildImageUrlPathRelativeToContextPath(ImageDTO imageDTO){
		if(StringUtils.isNotBlank(imageDTO.getGeneratedFilePath())){
			return new URIBuilder().setPath(storageImagePath).
					addParameter(ImageFetcher.PATH_PARAMETER, imageDTO.getGeneratedFilePath()).build().toString();
		}else{
			return null;
		}
	}

}
