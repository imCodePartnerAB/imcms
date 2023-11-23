package com.imcode.imcms.filters;

import com.imcode.imcms.servlet.GetDoc;
import com.imcode.imcms.servlet.ImcmsSetupFilter;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.document.DocumentDomainObject;
import imcode.util.Utility;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

import static imcode.server.ImcmsConstants.API_VIEW_DOC_PATH;

/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 06.09.18.
 */
public class UrlHandlerFilter implements Filter {

    public void destroy() {
        // noop
    }

    /**
     * When request path matches a physical or mapped resource then processes request normally.
     * Otherwise threats a request as a document request.
     */
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws ServletException, IOException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        final ImcmsServices services = Imcms.getServices();

        final String path = Utility.updatePathIfEmpty(Utility.decodePathFromRequest(request, Imcms.getDefaultFallbackDecoder()));
        final Set resourcePaths = request.getSession().getServletContext().getResourcePaths(path);

        if (resourcePaths == null || resourcePaths.size() == 0) {
            final String documentIdString = Utility.extractDocumentIdentifier(path);
            final String langCode = Imcms.getUser().getDocGetterCallback().getLanguage().getCode();
            final DocumentDomainObject document = services.getDocumentMapper().getVersionedDocument(documentIdString, langCode, request);

            request.setAttribute("contextPath", request.getContextPath());
            request.setAttribute("language", langCode);

            if (null != document) {
                if (Utility.isTextDocument(document)) {
                    final boolean isDocumentId = documentIdString.equals(String.valueOf(document.getId()));
                    //do not use document.getAlias() because documentIdString and alias may not equal because of language
                    final String newPath = API_VIEW_DOC_PATH + "/" + (isDocumentId ? document.getId() : documentIdString);
                    request.getRequestDispatcher(newPath).forward(request, response);
                } else {
                    GetDoc.viewDoc(document, request, response);
                }

                return;
            }
        }
        chain.doFilter(request, response);
    }

    public void init(FilterConfig config) {
        // noop
    }

}
