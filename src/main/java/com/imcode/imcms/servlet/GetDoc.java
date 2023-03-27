package com.imcode.imcms.servlet;

import imcode.server.*;
import imcode.server.document.*;
import imcode.server.document.FileDocumentDomainObject.FileDocumentFile;
import imcode.server.kerberos.KerberosLoginResult;
import imcode.server.kerberos.KerberosLoginStatus;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.oro.text.perl.Perl5Util;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static imcode.server.ImcmsConstants.API_VIEW_DOC_PATH;

/**
 * Retrieves document by metaId.
 */
public class GetDoc extends HttpServlet {

    public static final String REQUEST_PARAMETER__FILE_ID = "file_id";
    private final static Logger TRACK_LOG = LogManager.getLogger(ImcmsConstants.ACCESS_LOG);
    private final static Logger LOG = LogManager.getLogger(GetDoc.class.getName());
    private final static String NO_ACTIVE_DOCUMENT_URL = "no_active_document.jsp";
    private static final String HTTP_HEADER_REFERRER = "Referer";// Note, intended misspelling of "Referrer", according to the HTTP spec.
    private static final long serialVersionUID = -5473146465111395039L;

    /**
     * Renders document.
     *
     */
    private static void viewDoc(String documentId, HttpServletRequest req,
                                HttpServletResponse res) throws IOException {
        final DocumentDomainObject document = getDocument(documentId, req);

        if (null == document) {
            res.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        viewDoc(document, req, res);
    }

    private static DocumentDomainObject getDocument(String documentId, HttpServletRequest req) {
        final String langCode = Imcms.getUser().getDocGetterCallback().getLanguage().getCode();
        return Imcms.getServices().getDocumentMapper().getVersionedDocument(documentId, langCode, req);
    }

    /**
     * This method is called from viewDoc and from ImcmsSetupFilter.handleDocumentUrl only.
     *
     * @see ImcmsSetupFilter
     */
    @SneakyThrows
    // fixme: what a mess! rewrite!
    public static void viewDoc(DocumentDomainObject document, HttpServletRequest req, HttpServletResponse res) {
        if (null == document) {
            res.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        final ImcmsServices imcmsServices = Imcms.getServices();
        final UserDomainObject user = Utility.getLoggedOnUser(req);

        if (!user.canAccess(document)) {
            if (imcmsServices.getConfig().isSsoEnabled() && user.isDefaultUser()) {
                KerberosLoginResult loginResult = imcmsServices.getKerberosLoginService().login(req, res);

                if (loginResult.getStatus() == KerberosLoginStatus.SUCCESS) {
                    viewDoc(document, req, res); // fixme: wtf recursive call is doing here??
                }
                return;
            }

            Utility.forwardToLogin(req, res);
            return;
        }

        if ((!document.isPublished() || document.isInWasteBasket()) && !user.canEdit(document)) {
            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
            Utility.setDefaultHtmlContentType(res);

            final String adminTemplatePath = imcmsServices.getAdminTemplatePath(NO_ACTIVE_DOCUMENT_URL);
            req.getRequestDispatcher(adminTemplatePath).forward(req, res);
            return;
        }

        setReVisitsAndLog(document, req, res);

        final DocumentTypeDomainObject documentType = document.getDocumentType();

        if (DocumentTypeDomainObject.TEXT.equals(documentType)) {
            getTextDoc(document, req, res);

        } else if (DocumentTypeDomainObject.URL.equals(documentType)) {
            getUrlDoc((UrlDocumentDomainObject) document, res);

        } else if (DocumentTypeDomainObject.HTML.equals(documentType)) {
            getHtmlDoc((HtmlDocumentDomainObject) document, res);

        } else if (DocumentTypeDomainObject.FILE.equals(documentType)) {
            getFileDoc((FileDocumentDomainObject) document, req, res);
        }
    }

    private static void getTextDoc(DocumentDomainObject document, HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        Utility.setDefaultHtmlContentType(res);
        req.getRequestDispatcher(API_VIEW_DOC_PATH + "/" + document.getId()).forward(req, res);
    }

    private static void getUrlDoc(UrlDocumentDomainObject document, HttpServletResponse res) throws IOException {
        String urlRef = document.getUrl();
        res.sendRedirect(urlRef);
    }

    private static void getHtmlDoc(HtmlDocumentDomainObject document, HttpServletResponse res) throws IOException {
        Utility.setDefaultHtmlContentType(res);
        String htmlDocumentData = document.getHtml();
        res.getWriter().write(htmlDocumentData);
    }

    private static void getFileDoc(FileDocumentDomainObject document, HttpServletRequest req, HttpServletResponse res) throws IOException {
        final String fileId = req.getParameter(REQUEST_PARAMETER__FILE_ID);
        final FileDocumentFile file = document.getFileOrDefault(fileId);

        if (file == null) {
            res.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        try (InputStream inputStream = file.getInputStreamSource().getInputStream()) {
            try (ServletOutputStream out = res.getOutputStream()) {
                try {
                    final int len = inputStream.available();
                    setResponseContentAttributes(res, file, len);
                    IOUtils.copy(inputStream, out);
                } catch (SocketException ex) {
                    LOG.debug("Exception occurred", ex);
                }
            }
        } catch (IOException ex) {
            res.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private static void setResponseContentAttributes(HttpServletResponse res, FileDocumentFile file, int len) {

        final String filename = file.getFilename();
        final String contentDisposition = "attachment; filename=\"" + filename + "\"";
        final String mimeType = file.getMimeType();

        res.setContentLength(len);
        res.setContentType(mimeType);
        res.setHeader("Content-Disposition", contentDisposition);
	    res.setStatus(HttpServletResponse.SC_OK);
    }

    // fixme: not sure this crap is needed at all
    private static void setReVisitsAndLog(DocumentDomainObject document, HttpServletRequest req, HttpServletResponse res) {
        final HttpSession session = req.getSession(true);
        final ImcmsServices imcmsServices = Imcms.getServices();
        final UserDomainObject user = Utility.getLoggedOnUser(req);
        final String referrer = req.getHeader(HTTP_HEADER_REFERRER);
        final Perl5Util perlUtil = new Perl5Util();
        DocumentDomainObject referringDoc = null;

        if (null != referrer && perlUtil.match("/meta_id=(\\d+)/", referrer)) {
            int referringMetaId = Integer.parseInt(perlUtil.group(1));
            referringDoc = imcmsServices.getDocumentMapper().getDocument(referringMetaId);
        }
        final DocumentRequest documentRequest = new DocumentRequest(imcmsServices, user, document, referringDoc, req, res);

        final Cookie[] cookies = req.getCookies();
        final Map<String, String> cookieHash = new HashMap<>();

        for (int i = 0; cookies != null && i < cookies.length; ++i) {
            Cookie currentCookie = cookies[i];
            cookieHash.put(currentCookie.getName(), currentCookie.getValue());
        }

        // Log to accesslog
        TRACK_LOG.info(documentRequest);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        doGet(req, res);
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String documentId = req.getParameter("meta_id");
        viewDoc(documentId, req, res);
    }
}