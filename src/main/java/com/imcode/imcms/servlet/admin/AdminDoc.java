package com.imcode.imcms.servlet.admin;

import com.imcode.imcms.api.ContentManagementSystem;
import com.imcode.imcms.api.Document;
import com.imcode.imcms.api.DocumentLanguage;
import com.imcode.imcms.flow.ChangeDocDefaultVersionPageFlow;
import com.imcode.imcms.flow.DispatchCommand;
import com.imcode.imcms.flow.PageFlow;
import com.imcode.imcms.mapping.DocumentMapper;
import com.imcode.imcms.servlet.DocumentHistory;
import imcode.server.DocumentRequest;
import imcode.server.Imcms;
import imcode.server.ImcmsConstants;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.FileDocumentDomainObject;
import imcode.server.document.HtmlDocumentDomainObject;
import imcode.server.document.UrlDocumentDomainObject;
import imcode.server.parser.ParserParameters;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * Handles admin panel commands.
 */
public class AdminDoc extends HttpServlet {

    public static final String PARAMETER__DISPATCH_FLAGS = "flags";
    private static final String PARAMETER__META_ID = "meta_id";
    private static final long serialVersionUID = 4907353106118924203L;

    private static void adminDoc(int metaId, UserDomainObject user, HttpServletRequest req,
                                 HttpServletResponse res) throws IOException, ServletException {

        DocumentHistory.from(req.getSession()).pushIfNotYet(metaId);

        final DocumentLanguage language = Imcms.getUser().getDocGetterCallback().getLanguage();
        final DocumentDomainObject document = Imcms.getServices().getDocumentMapper().getWorkingDocument(metaId, language);

        if (null == document) {
            res.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        if (!user.canEdit(document) || user.isDefaultUser()) {
            req.getRequestDispatcher("/login").forward(req, res);
            return;
        }

        req.setAttribute("isEditMode", "true");
        final String newPath = "/api/viewDoc/" + metaId;
        req.getRequestDispatcher(newPath).forward(req, res);
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        doPost(req, res);
    }

    /**
     * Creates a document page flow and dispatches request to that flow.
     * <p>
     * If flow can not be created or an user is not allowed to edit a document adminDoc is called.
     */
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        final int metaId = Integer.parseInt(req.getParameter(PARAMETER__META_ID));
        int flags = Integer.parseInt(Objects.toString(req.getParameter(PARAMETER__DISPATCH_FLAGS), "0"));

        final DocumentMapper documentMapper = Imcms.getServices().getDocumentMapper();
        final UserDomainObject user = Utility.getLoggedOnUser(req);
        final DocumentDomainObject document = documentMapper.getDocument(metaId);

        if (!user.canEdit(document)) {
            flags = 0;
        }

        final PageFlow pageFlow = createFlow(req, document, flags, user);

        if (null != pageFlow && user.canEdit(document)) {
            pageFlow.dispatch(req, res);

        } else {
            Utility.setDefaultHtmlContentType(res);
            adminDoc(metaId, user, req, res);
        }
    }

    /**
     * @param document document associated with a flow.
     * @param flags    command flags.
     * @param user     user
     * @return new page flow
     */
    private PageFlow createFlow(HttpServletRequest req, DocumentDomainObject document, int flags, UserDomainObject user) {
        RedirectToDocumentCommand returnCommand = new RedirectToDocumentCommand(document);

        PageFlow pageFlow = null;
        if (ImcmsConstants.DISPATCH_FLAG__DOCINFO_PAGE == flags && user.canEditDocumentInformationFor(document)) {
            pageFlow = new EditDocPageFlow(document);
        } else if (ImcmsConstants.DISPATCH_FLAG__DOCUMENT_PERMISSIONS_PAGE == flags && user.canEditPermissionsFor(document)) {
            pageFlow = new EditDocPageFlow(document);
        } else if (document instanceof HtmlDocumentDomainObject && ImcmsConstants.DISPATCH_FLAG__EDIT_HTML_DOCUMENT == flags) {
            pageFlow = new EditDocPageFlow(document);
        } else if (document instanceof UrlDocumentDomainObject && ImcmsConstants.DISPATCH_FLAG__EDIT_URL_DOCUMENT == flags) {
            pageFlow = new EditDocPageFlow(document);
        } else if (document instanceof FileDocumentDomainObject && ImcmsConstants.DISPATCH_FLAG__EDIT_FILE_DOCUMENT == flags) {
            pageFlow = new EditDocPageFlow(document);
        } else if (ImcmsConstants.DISPATCH_FLAG__PUBLISH == flags) {
            pageFlow = new ChangeDocDefaultVersionPageFlow(document, returnCommand, new DocumentMapper.PublishWorkingVersionCommand());
        } else if (ImcmsConstants.DISPATCH_FLAG__SET_DEFAULT_VERSION == flags) {
            try {
                Integer no = Integer.parseInt(req.getParameter("no"));
                pageFlow = new ChangeDocDefaultVersionPageFlow(document, returnCommand, new DocumentMapper.SetDefaultDocumentVersionCommand(no));
            } catch (Exception e) {
                throw new AssertionError(e);
            }
        }
        return pageFlow;
    }

    private static class EditDocPageFlow extends PageFlow {

        protected DocumentDomainObject document;

        // todo: editor tag arg: perms/info/profile/etc
        private EditDocPageFlow(DocumentDomainObject document) {
            super(null);
            this.document = document;
        }

        @Override
        public void dispatch(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            //String docAdminUrl = request.getContextPath() + "/imcms/docadmin?meta_id=" + request.getParameter("meta_id");
            DocumentRequest documentRequest = new DocumentRequest(Imcms.getServices(), Imcms.getUser(), document, null, request, response);
            ParserParameters parserParameters = new ParserParameters(documentRequest);

            parserParameters.setFlags(56568);

            ParserParameters.putInRequest(parserParameters);

            ContentManagementSystem cms = ContentManagementSystem.fromRequest(request);
            Document document = cms.getDocumentService().getDocument(this.document.getId());

            request.setAttribute("document", document);
            request.setAttribute("user", cms.getCurrentUser());

            request.getRequestDispatcher("/WEB-INF/jsp/admin/edit_document.jsp").forward(request, response);
        }

        @Override
        protected void dispatchToFirstPage(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        }

        @Override
        protected void dispatchOk(HttpServletRequest request, HttpServletResponse response, String page) throws IOException, ServletException {
        }

        @Override
        protected void dispatchFromPage(HttpServletRequest request, HttpServletResponse response, String page) throws IOException, ServletException {
        }
    }

    private static class RedirectToDocumentCommand implements DispatchCommand {

        private final DocumentDomainObject document;

        RedirectToDocumentCommand(DocumentDomainObject document) {
            this.document = document;
        }

        public void dispatch(HttpServletRequest request, HttpServletResponse response) throws IOException {
            response.sendRedirect("AdminDoc?meta_id=" + document.getId());
        }
    }
}
