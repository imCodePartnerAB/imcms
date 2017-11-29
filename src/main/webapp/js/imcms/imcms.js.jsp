<%@ page
     import="com.imcode.imcms.api.DocumentLanguage"
     import="com.imcode.imcms.servlet.Version"
     import="imcode.server.Imcms"
     import="imcode.server.document.DocumentDomainObject"
     contentType="text/javascript"
     pageEncoding="UTF-8"

     %><%

    final Integer metaId = Integer.parseInt(request.getParameter("meta_id"));
	final DocumentDomainObject document =  Imcms.getServices().getDocumentMapper().getDocument(metaId);
	final DocumentLanguage language = document.getLanguage();

	final boolean isEditMode = (request.getParameterMap().containsKey("flags")
		 && Integer.valueOf(request.getParameter("flags")) > 0);

	final String imcmsVersion = Version.getImcmsVersion(getServletConfig().getServletContext());

	pageContext.setAttribute("version", imcmsVersion);
    pageContext.setAttribute("isEditMode", isEditMode);
    pageContext.setAttribute("isVersioningAllowed", Imcms.isVersioningAllowed());
    pageContext.setAttribute("language", language);
    pageContext.setAttribute("document", document);
    pageContext.setAttribute("imagesPath", Imcms.getServices().getConfig().getImagePath());

%>
    Imcms = {
        contextPath: "${pageContext.request.contextPath}",
        imagesPath: "${imagesPath}",
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
