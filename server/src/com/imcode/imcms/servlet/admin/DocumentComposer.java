package com.imcode.imcms.servlet.admin;

/*
 * Created by IntelliJ IDEA.
 * User: kreiger
 * Date: 2004-feb-23
 * Time: 16:19:25
 */

import imcode.server.ApplicationServer;
import imcode.server.IMCConstants;
import imcode.server.IMCServiceInterface;
import imcode.server.document.*;
import imcode.server.user.ImcmsAuthenticatorAndUserMapper;
import imcode.server.user.UserDomainObject;
import imcode.util.DateConstants;
import imcode.util.MultipartHttpServletRequest;
import imcode.util.Utility;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.ObjectUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DocumentComposer extends HttpServlet {

    final static String URL_I15D_PAGE__PREFIX = "/imcms/";

    private final static String URL_I15D_PAGE__DOCINFO = "/jsp/docadmin/document_information.jsp";
    private final static String URL_I15D_PAGE__URLDOC = "/jsp/docadmin/url_document.jsp";
    private final static String URL_I15D_PAGE__HTMLDOC = "/jsp/docadmin/html_document.jsp";
    private static final String URL_I15D_PAGE__FILEDOC = "/jsp/docadmin/file_document.jsp";
    private static final String URL__BROWSER_DOCUMENT_COMPOSER = "BrowserDocumentComposer";

    private static final String MIME_TYPE__APPLICATION_OCTET_STREAM = "application/octet-stream";
    private static final String MIME_TYPE__UNKNOWN_DEFAULT = MIME_TYPE__APPLICATION_OCTET_STREAM;

    public static final String REQUEST_ATTR_OR_PARAM__DOCUMENT_SESSION_ATTRIBUTE_NAME = "document.sessionAttributeName";
    public static final String REQUEST_ATTR_OR_PARAM__NEW_DOCUMENT_PARENT_INFORMATION_SESSION_ATTRIBUTE_NAME = "newDocumentParentInformation.sessionAttributeName";

    public static final String REQUEST_ATTR_OR_PARAM__ACTION = "action";

    public static final String PARAMETER__URL_DOC__URL = "url";
    public static final String PARAMETER__HTML_DOC__HTML = "html";
    public static final String PARAMETER__FILE_DOC__FILE = "file";
    public static final String PARAMETER__FILE_DOC__MIME_TYPE = "mimetype";

    public static final String PARAMETER__GO_TO_IMAGE_BROWSE = "browseForMenuImage";
    public static final String PARAMETER__RETURNING_FROM_IMAGE_BROWSE = "returningFromImageBrowse";
    public static final String PARAMETER__IMAGE_BROWSE_ORIGINAL_ACTION = "imageBrowse.originalAction";

    public static final String ACTION__CREATE_NEW_DOCUMENT = "createNewDocument";
    public static final String ACTION__PROCESS_NEW_DOCUMENT_INFORMATION = "processNewDocumentInformation";
    public static final String ACTION__CREATE_NEW_URL_DOCUMENT = "createNewUrlDocument";
    public static final String ACTION__CREATE_NEW_HTML_DOCUMENT = "createNewHtmlDocument";
    public static final String ACTION__CREATE_NEW_FILE_DOCUMENT = "createNewFileDocument";
    public static final String ACTION__CREATE_NEW_BROWSER_DOCUMENT = "createNewBrowserDocument";

    public static final String ACTION__EDIT_DOCUMENT_INFORMATION = "editDocumentInformation";
    public static final String ACTION__PROCESS_EDITED_DOCUMENT_INFORMATION = "processEditedDocumentInformation";
    public static final String ACTION__EDIT_BROWSER_DOCUMENT = "editBrowserDocument";
    public static final String ACTION__EDITED_BROWSER_DOCUMENT = "editedBrowserDocument";

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest r, HttpServletResponse response) throws ServletException, IOException {
        MultipartHttpServletRequest request = new MultipartHttpServletRequest(r);

        String action = request.getParameter(REQUEST_ATTR_OR_PARAM__ACTION);
        request.setAttribute(REQUEST_ATTR_OR_PARAM__ACTION, action);

        NewDocumentParentInformation newDocumentParentInformation = (NewDocumentParentInformation) getObjectFromSessionWithKeyInRequest(request, REQUEST_ATTR_OR_PARAM__NEW_DOCUMENT_PARENT_INFORMATION_SESSION_ATTRIBUTE_NAME);

        DocumentDomainObject document = (DocumentDomainObject) getObjectFromSessionWithKeyInRequest(request, REQUEST_ATTR_OR_PARAM__DOCUMENT_SESSION_ATTRIBUTE_NAME);

        if (null != request.getParameter(PARAMETER__GO_TO_IMAGE_BROWSE)) {
            String parentInfoAttributeName = getSessionAttributeNameFromRequest(request, REQUEST_ATTR_OR_PARAM__NEW_DOCUMENT_PARENT_INFORMATION_SESSION_ATTRIBUTE_NAME);
            String sessionAttributeName = getSessionAttributeNameFromRequest(request, REQUEST_ATTR_OR_PARAM__DOCUMENT_SESSION_ATTRIBUTE_NAME);
            String originalAction = request.getParameter(PARAMETER__IMAGE_BROWSE_ORIGINAL_ACTION);
            String returningUrl = "DocumentComposer?" + PARAMETER__RETURNING_FROM_IMAGE_BROWSE + "=" + "true" + "&" + REQUEST_ATTR_OR_PARAM__NEW_DOCUMENT_PARENT_INFORMATION_SESSION_ATTRIBUTE_NAME + "=" + parentInfoAttributeName +"&" + REQUEST_ATTR_OR_PARAM__DOCUMENT_SESSION_ATTRIBUTE_NAME + "=" + sessionAttributeName + "&" + PARAMETER__IMAGE_BROWSE_ORIGINAL_ACTION + "=" + originalAction;
            request.getRequestDispatcher("ImageBrowse?" + ImageBrowse.PARAMETER__CALLER + "=" + java.net.URLEncoder.encode(returningUrl)).forward(request, response);
            return;
        }

        UserDomainObject user = Utility.getLoggedOnUser(request);

        if ( null != request.getParameter(PARAMETER__RETURNING_FROM_IMAGE_BROWSE)) {
            action = request.getParameter( PARAMETER__IMAGE_BROWSE_ORIGINAL_ACTION );
            request.setAttribute(REQUEST_ATTR_OR_PARAM__ACTION, action);
            String imageUrl = AdminDoc.getImageUri(request);
            if( ACTION__CREATE_NEW_DOCUMENT.equalsIgnoreCase(action) ){
                DocumentDomainObject newDocument = createCloneFromParent(newDocumentParentInformation);
                newDocument.setImage( imageUrl );
                addObjectToSessionAndSetSessionAttributeNameInRequest("newDocument", newDocument, request, REQUEST_ATTR_OR_PARAM__DOCUMENT_SESSION_ATTRIBUTE_NAME);
                forwardToDocinfoPage(request, response, user);
                return;
            } else {
                document.setImage( imageUrl );
            }
        }

        DocumentMapper documentMapper = ApplicationServer.getIMCServiceInterface().getDocumentMapper();
        if (ACTION__CREATE_NEW_DOCUMENT.equalsIgnoreCase(action)) {
            DocumentDomainObject newDocument = createCloneFromParent(newDocumentParentInformation);
            addObjectToSessionAndSetSessionAttributeNameInRequest("newDocument", newDocument, request, REQUEST_ATTR_OR_PARAM__DOCUMENT_SESSION_ATTRIBUTE_NAME);
            forwardToDocinfoPage(request, response, user);
        } else if (ACTION__PROCESS_NEW_DOCUMENT_INFORMATION.equalsIgnoreCase(action)) {
            processNewDocumentInformation(newDocumentParentInformation, request, response, user);
        } else if (ACTION__CREATE_NEW_URL_DOCUMENT.equalsIgnoreCase(action)) {
            UrlDocumentDomainObject newUrlDocument = (UrlDocumentDomainObject) document;
            newUrlDocument.setUrlDocumentUrl(request.getParameter(PARAMETER__URL_DOC__URL));
            newUrlDocument.setTarget(getTargetFromRequest(request));
            saveNewDocumentAndAddToMenuAndRemoveSessionAttributesAndRedirectToParent(newUrlDocument, newDocumentParentInformation, user, request, response);
        } else if (ACTION__CREATE_NEW_HTML_DOCUMENT.equalsIgnoreCase(action)) {
            HtmlDocumentDomainObject newHtmlDocument = (HtmlDocumentDomainObject) document;
            newHtmlDocument.setHtmlDocumentHtml(request.getParameter(PARAMETER__HTML_DOC__HTML));
            saveNewDocumentAndAddToMenuAndRemoveSessionAttributesAndRedirectToParent(newHtmlDocument, newDocumentParentInformation, user, request, response);
        } else if (ACTION__CREATE_NEW_FILE_DOCUMENT.equalsIgnoreCase(action)) {
            FileDocumentDomainObject newFileDocument = (FileDocumentDomainObject) document;
            FileItem fileItem = request.getParameterFileItem(PARAMETER__FILE_DOC__FILE);
            newFileDocument.setFileDocumentFilename(fileItem.getName());
            newFileDocument.setFileDocumentInputStream(fileItem.getInputStream());
            newFileDocument.setFileDocumentMimeType(getMimeTypeFromRequest(request));
            saveNewDocumentAndAddToMenuAndRemoveSessionAttributesAndRedirectToParent(newFileDocument, newDocumentParentInformation, user, request, response);
        } else if (ACTION__CREATE_NEW_BROWSER_DOCUMENT.equalsIgnoreCase( action )) {
            BrowserDocumentDomainObject newBrowserDocument = (BrowserDocumentDomainObject)document;
            saveNewDocumentAndAddToMenuAndRemoveSessionAttributesAndRedirectToParent( newBrowserDocument, newDocumentParentInformation, user, request, response );
        } else if (ACTION__EDITED_BROWSER_DOCUMENT.equalsIgnoreCase( action )) {
            BrowserDocumentDomainObject browserDocument = (BrowserDocumentDomainObject)document;
            try {
                documentMapper.saveDocument( browserDocument );
            } catch ( MaxCategoryDomainObjectsOfTypeExceededException e ) {
                throw new ServletException(e);
            }
            response.sendRedirect("AdminDoc?meta_id=" + document.getId()) ;
        } else if (ACTION__EDIT_DOCUMENT_INFORMATION.equalsIgnoreCase(action)) {
            forwardToDocinfoPage(request, response, user);
        } else if (ACTION__PROCESS_EDITED_DOCUMENT_INFORMATION.equalsIgnoreCase(action)) {
            setDocumentAttributesFromRequestParameters(document, request);
            try {
                documentMapper.saveDocument(document);
            } catch (MaxCategoryDomainObjectsOfTypeExceededException e) {
                throw new ServletException(e);
            }
            response.sendRedirect("AdminDoc?meta_id=" + document.getId()) ;
        } else if (ACTION__EDIT_BROWSER_DOCUMENT.equalsIgnoreCase( action )) {
            forwardToBrowserDocumentComposerPage( request, response, user);
        }
    }

    private DocumentDomainObject createCloneFromParent(NewDocumentParentInformation newDocumentParentInformation) throws ServletException {
        final IMCServiceInterface service = ApplicationServer.getIMCServiceInterface();
        final DocumentMapper documentMapper = service.getDocumentMapper();
        DocumentDomainObject newDocument;
        try {
            newDocument = (DocumentDomainObject) documentMapper.getDocument(newDocumentParentInformation.parentId).clone();
        } catch (CloneNotSupportedException e) {
            throw new ServletException(e);
        }
        return newDocument;
    }

    private String getMimeTypeFromRequest(MultipartHttpServletRequest request) {
        FileItem fileItem = request.getParameterFileItem(PARAMETER__FILE_DOC__FILE);
        String filename = fileItem.getName();
        final DocumentMapper documentMapper = ApplicationServer.getIMCServiceInterface().getDocumentMapper();
        Set predefinedMimeTypes = new HashSet(Arrays.asList(documentMapper.getAllMimeTypes()));
        String[] mimeTypes = request.getParameterValues(PARAMETER__FILE_DOC__MIME_TYPE);
        String mimeType = null;
        for (int i = 0; i < mimeTypes.length; i++) {
            mimeType = mimeTypes[i].trim();
            if (predefinedMimeTypes.contains(mimeType)) {
                break;
            }
            if ("".equals(mimeType)) {
                if (null != filename) {
                    mimeType = getServletContext().getMimeType(filename);
                }
            } else if (-1 == mimeType.indexOf('/')) {
                if ('.' != mimeType.charAt(0)) {
                    mimeType = '.' + mimeType;
                }
                mimeType = getServletContext().getMimeType('_' + mimeType);
            }
            if (null == mimeType || "".equals(mimeType)) {
                mimeType = MIME_TYPE__UNKNOWN_DEFAULT;
            }
        }
        return mimeType;
    }

    public void saveNewDocumentAndAddToMenuAndRemoveSessionAttributesAndRedirectToParent(DocumentDomainObject newDocument,
                                                                                         NewDocumentParentInformation newDocumentParentInformation,
                                                                                         UserDomainObject user,
                                                                                         HttpServletRequest request,
                                                                                         HttpServletResponse response) throws IOException {
        try {
            final IMCServiceInterface service = ApplicationServer.getIMCServiceInterface();
            final DocumentMapper documentMapper = service.getDocumentMapper();
            documentMapper.saveNewDocumentAndAddToMenu(newDocument, user, newDocumentParentInformation);

            removeObjectFromSessionWithKeyInRequest(request, REQUEST_ATTR_OR_PARAM__DOCUMENT_SESSION_ATTRIBUTE_NAME);
            removeObjectFromSessionWithKeyInRequest(request, REQUEST_ATTR_OR_PARAM__NEW_DOCUMENT_PARENT_INFORMATION_SESSION_ATTRIBUTE_NAME);

        } catch (MaxCategoryDomainObjectsOfTypeExceededException e) {
            throw new RuntimeException(e);
        } catch (DocumentMapper.DocumentAlreadyInMenuException e) {
            throw new RuntimeException(e);
        }
        response.sendRedirect("AdminDoc?meta_id=" + newDocumentParentInformation.parentId + "&flags="
                + IMCConstants.PERM_DT_TEXT_EDIT_MENUS);

    }

    private void processNewDocumentInformation(NewDocumentParentInformation newDocumentParentInformation,
                                               HttpServletRequest request, HttpServletResponse response,
                                               UserDomainObject user) throws ServletException, IOException {
        final IMCServiceInterface service = ApplicationServer.getIMCServiceInterface();
        final DocumentMapper documentMapper = service.getDocumentMapper();

        final DocumentDomainObject parent = documentMapper.getDocument(newDocumentParentInformation.parentId);
        int documentTypeId = newDocumentParentInformation.documentTypeId;
        final DocumentDomainObject newDocument = createDocumentOfTypeFromParent(documentTypeId, parent);

        setDocumentAttributesFromRequestParameters(newDocument, request);
        addObjectToSessionAndSetSessionAttributeNameInRequest("document", newDocument, request, REQUEST_ATTR_OR_PARAM__DOCUMENT_SESSION_ATTRIBUTE_NAME);

        request.setAttribute(REQUEST_ATTR_OR_PARAM__NEW_DOCUMENT_PARENT_INFORMATION_SESSION_ATTRIBUTE_NAME, getSessionAttributeNameFromRequest(request, REQUEST_ATTR_OR_PARAM__NEW_DOCUMENT_PARENT_INFORMATION_SESSION_ATTRIBUTE_NAME));
        newDocument.processNewDocumentInformation(this, newDocumentParentInformation, user, request, response);
    }

    private DocumentDomainObject createDocumentOfTypeFromParent(int documentTypeId, final DocumentDomainObject parent) throws ServletException {
        DocumentDomainObject newDocument;
        try {
            if (DocumentDomainObject.DOCTYPE_TEXT == documentTypeId) {
                newDocument = (DocumentDomainObject) parent.clone();
            } else {
                newDocument = DocumentDomainObject.fromDocumentTypeId(documentTypeId);
                newDocument.setDocumentInformation((DocumentDomainObject.DocumentProperties) parent.getDocumentInformation().clone());
            }
        } catch (CloneNotSupportedException e) {
            throw new ServletException(e);
        }
        return newDocument;
    }

    public void forwardToBrowserDocumentComposerPage( HttpServletRequest request, HttpServletResponse response,
                                                       UserDomainObject user ) throws IOException, ServletException {
        request.getRequestDispatcher( URL__BROWSER_DOCUMENT_COMPOSER ).forward( request, response );
    }

    public void forwardToCreateNewFileDocumentPage(HttpServletRequest request, HttpServletResponse response,
                                                   UserDomainObject user) throws IOException, ServletException {
        request.getRequestDispatcher(URL_I15D_PAGE__PREFIX + user.getLanguageIso639_2() + URL_I15D_PAGE__FILEDOC).forward(request, response);
    }

    public void forwardToCreateNewHtmlDocumentPage(HttpServletRequest request, HttpServletResponse response,
                                                   UserDomainObject user) throws IOException, ServletException {
        request.getRequestDispatcher(URL_I15D_PAGE__PREFIX + user.getLanguageIso639_2() + URL_I15D_PAGE__HTMLDOC).forward(request, response);

    }

    public void forwardToCreateNewUrlDocumentPage(HttpServletRequest request, HttpServletResponse response,
                                                  UserDomainObject user) throws ServletException, IOException {
        request.getRequestDispatcher(URL_I15D_PAGE__PREFIX + user.getLanguageIso639_2() + URL_I15D_PAGE__URLDOC).forward(request, response);
    }

    public static void addObjectToSessionAndSetSessionAttributeNameInRequest(final String sessionObjectName,
                                                                             final Object objectToAddToSession,
                                                                             HttpServletRequest request,
                                                                             final String sessionAttributeNameRequestAttributeName) {
        final String sessionAttributeName = DocumentComposer.class.getName() + "." + sessionObjectName + "."
                + System.currentTimeMillis();
        request.getSession().setAttribute(sessionAttributeName, objectToAddToSession);
        request.setAttribute(sessionAttributeNameRequestAttributeName, sessionAttributeName);
    }

    public static Object getObjectFromSessionWithKeyInRequest(HttpServletRequest request,
                                                              String requestAttributeOrParameterName) {
        String sessionAttributeName = getSessionAttributeNameFromRequest(request, requestAttributeOrParameterName);
        return request.getSession().getAttribute(sessionAttributeName);
    }

    public static void removeObjectFromSessionWithKeyInRequest(HttpServletRequest request,
                                                               String requestAttributeOrParameterName) {
        String sessionAttributeName = getSessionAttributeNameFromRequest(request, requestAttributeOrParameterName);
        request.getSession().removeAttribute(sessionAttributeName);
    }

    public static String getSessionAttributeNameFromRequest(HttpServletRequest request,
                                                            String requestAttributeOrParameterName) {
        String sessionAttributeName = (String) request.getAttribute(requestAttributeOrParameterName);
        if (null == sessionAttributeName) {
            sessionAttributeName = request.getParameter(requestAttributeOrParameterName);
        }
        return sessionAttributeName;
    }

    private void setDocumentAttributesFromRequestParameters(DocumentDomainObject document, HttpServletRequest request) {

        final IMCServiceInterface service = ApplicationServer.getIMCServiceInterface();
        final DocumentMapper documentMapper = service.getDocumentMapper();

        String headline = request.getParameter("meta_headline");
        document.setHeadline(headline);

        String menuText = request.getParameter("meta_text");
        document.setMenuText(menuText);

        String imageUrl = request.getParameter("meta_image");
        document.setImage(imageUrl);

        SimpleDateFormat dateFormat = new SimpleDateFormat(DateConstants.DATE_FORMAT_STRING);
        SimpleDateFormat timeFormat = new SimpleDateFormat(DateConstants.TIME_FORMAT_NO_SECONDS_STRING);

        Date activatedDatetime = parseDatetimeParameters(request, "activated_date", "activated_time", dateFormat,
                timeFormat);
        Date archivedDatetime = parseDatetimeParameters(request, "archived_date", "archived_time", dateFormat,
                timeFormat);

        document.setActivatedDatetime(activatedDatetime);
        document.setArchivedDatetime(archivedDatetime);

        document.removeAllSections();
        String[] sectionIds = request.getParameterValues("change_section");
        for (int i = 0; null != sectionIds && i < sectionIds.length; i++) {
            int sectionId = Integer.parseInt(sectionIds[i]);
            SectionDomainObject section = documentMapper.getSectionById(sectionId);
            document.addSection(section);
        }

        String languageIso639_2 = request.getParameter("lang_prefix");
        document.setLanguageIso639_2(languageIso639_2);

        document.removeAllCategories();
        String[] categoryIds = request.getParameterValues("categories");
        for (int i = 0; null != categoryIds && i < categoryIds.length; i++) {
            try {
                int categoryId = Integer.parseInt(categoryIds[i]);
                CategoryDomainObject category = documentMapper.getCategoryById(categoryId);
                document.addCategory(category);
            } catch (NumberFormatException ignored) {
                // OK, empty category id
            }
        }

        boolean visibleInMenuForUnauthorizedUsers = "1".equals(request.getParameter("show_meta"));
        document.setVisibleInMenuForUnauthorizedUsers(visibleInMenuForUnauthorizedUsers);

        boolean linkableByOtherUsers = "1".equals(request.getParameter("shared"));
        document.setLinkableByOtherUsers(linkableByOtherUsers);

        String keywordsString = request.getParameter("classification");
        String[] keywords = keywordsString.split("\\W+");
        document.setKeywords(keywords);

        boolean searchDisabled = "1".equals(request.getParameter("disable_search"));
        document.setSearchDisabled(searchDisabled);

        String target = getTargetFromRequest(request);
        document.setTarget(target);

        Date createdDatetime = (Date) ObjectUtils.defaultIfNull(parseDatetimeParameters(request, "date_created", "created_time", dateFormat, timeFormat), new Date());

        Date modifiedDatetime = (Date) ObjectUtils.defaultIfNull(parseDatetimeParameters(request, "date_modified", "modified_time", dateFormat, timeFormat), createdDatetime);

        document.setCreatedDatetime(createdDatetime);
        document.setModifiedDatetime(modifiedDatetime);

        UserDomainObject publisher = null;
        try {
            int publisherId = Integer.parseInt(request.getParameter("publisher_id"));
            ImcmsAuthenticatorAndUserMapper userAndRoleMapper = service.getImcmsAuthenticatorAndUserAndRoleMapper();
            publisher = userAndRoleMapper.getUser(publisherId);
        } catch (NumberFormatException ignored) {
// OK, no publisher
        }
        document.setPublisher(publisher);
    }

    private String getTargetFromRequest(HttpServletRequest request) {
        String[] possibleTargets = request.getParameterValues("target");
        String target = null;
        for (int i = 0; i < possibleTargets.length; i++) {
            target = possibleTargets[i];
            boolean targetIsPredefinedTarget
                    = "_self".equalsIgnoreCase(target)
                    || "_blank".equalsIgnoreCase(target)
                    || "_top".equalsIgnoreCase(target);
            if (targetIsPredefinedTarget) {
                break;
            }
        }
        return target;
    }

    private void forwardToDocinfoPage(HttpServletRequest request,
                                      HttpServletResponse response, UserDomainObject user) throws ServletException, IOException {
        request.getRequestDispatcher(URL_I15D_PAGE__PREFIX + user.getLanguageIso639_2() + URL_I15D_PAGE__DOCINFO).forward(request, response);
    }

    static Date parseDatetimeParameters(HttpServletRequest req, final String dateParameterName,
                                        final String timeParameterName, DateFormat dateformat,
                                        DateFormat timeformat) {
        String dateStr = req.getParameter(dateParameterName);
        String timeStr = req.getParameter(timeParameterName);

        Date date = null;
        try {
            date = dateformat.parse(dateStr);
        } catch (ParseException pe) {
            return null;
        } catch (NullPointerException npe) {
            return null;
        }

        Date time = null;
        try {
            timeformat.setTimeZone(TimeZone.getTimeZone("GMT"));
            time = timeformat.parse(timeStr);
        } catch (ParseException pe) {
            return date;
        } catch (NullPointerException npe) {
            return date;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MILLISECOND, (int) time.getTime());
        return calendar.getTime();
    }

    public static class NewDocumentParentInformation {

        public int parentId;
        public int parentMenuNumber;
        public int documentTypeId;
        public static final String MENU_NUMBER_PARAMETER_NAME = "doc_menu_no";
        public static final String DOCUMENT_TYPE_PARAMETER_NAME = "edit_menu";
        public static final String PARENT_ID_PARAMETER_NAME = "parent_meta_id";

        public NewDocumentParentInformation(HttpServletRequest request) throws NumberFormatException {
            parentMenuNumber = Integer.parseInt(request.getParameter(MENU_NUMBER_PARAMETER_NAME));
            parentId =
                    Integer.parseInt(request.getParameter(PARENT_ID_PARAMETER_NAME));
            documentTypeId = Integer.parseInt(request.getParameter(DOCUMENT_TYPE_PARAMETER_NAME));
        }

    }

}