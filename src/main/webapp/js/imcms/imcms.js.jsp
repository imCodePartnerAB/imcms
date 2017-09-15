<%@ page import="com.imcode.imcms.api.DocumentLanguage" %>
    <%@ page import="com.imcode.imcms.servlet.Version" %>
    <%@ page import="imcode.server.Imcms "%>
    <%@ page import="imcode.server.document.DocumentDomainObject" %>
    <%@ page import="imcode.server.ImcmsConstants"%>
    <%@ page contentType="text/javascript" pageEncoding="UTF-8"	%>
    <%

    final Integer metaId = Integer.parseInt(request.getParameter("meta_id"));
	final DocumentDomainObject document =  Imcms.getServices().getDocumentMapper().getDocument(metaId);
	final DocumentLanguage language = document.getLanguage();

	final boolean isEditMode = (request.getParameterMap().containsKey("flags")
		 && Integer.valueOf(request.getParameter("flags")) > 0);

	final String imcmsVersion = Version.getImcmsVersion(getServletConfig().getServletContext());

	pageContext.setAttribute("version", imcmsVersion);
    pageContext.setAttribute("editModeFlags", ImcmsConstants.PERM_EDIT_DOCUMENT);
    pageContext.setAttribute("isEditMode", isEditMode);
    pageContext.setAttribute("isVersioningAllowed", Imcms.isVersioningAllowed());
    pageContext.setAttribute("language", language);
    pageContext.setAttribute("document", document);

%>
    Imcms = {
        flags: ${isEditMode ? editModeFlags : 0},
        contextPath: "${pageContext.request.contextPath}",
        version: "${version}",
        isEditMode: ${isEditMode},
        isVersioningAllowed: ${isVersioningAllowed},
        document: {
            id: ${document.id},
            type: ${document.documentTypeId},
            label: "${document.headline}"
        },
        language: {
            name: "${language.name}",
            nativeName: "${language.nativeName}",
            code: "${language.code}"
        }
    };
