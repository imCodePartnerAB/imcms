package com.imcode.imcms.filters;

import com.imcode.imcms.servlet.GetDoc;
import com.imcode.imcms.servlet.ImcmsSetupFilter;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.document.DocumentDomainObject;
import imcode.util.FallbackDecoder;
import imcode.util.Utility;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
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

        final String workaroundUriEncoding = services.getConfig().getWorkaroundUriEncoding();
        final FallbackDecoder fallbackDecoder = new FallbackDecoder(
                Charset.forName(Imcms.DEFAULT_ENCODING),
                (null != workaroundUriEncoding) ? Charset.forName(workaroundUriEncoding) : Charset.defaultCharset()
        );

	    final String path = Utility.updatePathIfEmpty(Utility.decodePathFromRequest(request, fallbackDecoder));

	    final Set resourcePaths = request.getSession().getServletContext().getResourcePaths(path);

	    if (resourcePaths == null || resourcePaths.size() == 0) {
		    final String documentIdString = ImcmsSetupFilter.getDocumentIdString(services, path);
		    final String langCode = Imcms.getUser().getDocGetterCallback().getLanguage().getCode();
		    final DocumentDomainObject document = services.getDocumentMapper()
				    .getVersionedDocument(documentIdString, langCode, request);

		    if (document != null) {
			    final Map<String, String> aliases = document.getAliases();
			    final Optional<String> requestedAlias = aliases.values().stream().filter(documentIdString::equalsIgnoreCase).findAny();

			    if (requestedAlias.isPresent() && document.getMeta().getDefaultLanguageAliasEnabled()) {
				    final String defaultLanguageCode = Imcms.getServices().getLanguageService().getDefaultLanguage().getCode();
				    final String defaultAlias = aliases.get(defaultLanguageCode);
				    if (Objects.equals(requestedAlias.get(), defaultAlias)) {
					    handleUrl(request, response, langCode, document);
				    } else {
					    response.sendError(HttpServletResponse.SC_NOT_FOUND);
				    }
			    } else {
				    handleUrl(request, response, langCode, document);
			    }
			    return;
		    }
	    }
	    chain.doFilter(request, response);
    }

	private void handleUrl(HttpServletRequest request, HttpServletResponse response, String langCode, DocumentDomainObject document) throws ServletException, IOException {
		request.setAttribute("contextPath", request.getContextPath());
		request.setAttribute("language", langCode);

		if (Utility.isTextDocument(document)) {

			final String newPath = API_VIEW_DOC_PATH + "/" + document.getId();
			request.getRequestDispatcher(newPath).forward(request, response);

		} else {
			GetDoc.viewDoc(document, request, response);
		}
	}

    public void init(FilterConfig config) {
        // noop
    }

}
