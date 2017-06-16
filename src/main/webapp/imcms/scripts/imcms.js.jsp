<%@ page import="com.imcode.imcms.api.DocumentLanguage" %><%@
	page import="imcode.server.Imcms" %><%@
	page import="imcode.server.document.DocumentDomainObject"%><%@
	page contentType="text/javascript" pageEncoding="UTF-8"	%><%

	final Integer metaId = Integer.parseInt(request.getParameter("meta_id"));
	final DocumentDomainObject document =  Imcms.getServices().getDocumentMapper().getDocument(metaId);
	final DocumentLanguage language = document.getLanguage();
	final boolean isEditMode = (request.getParameterMap().containsKey("flags")
		 && Integer.valueOf(request.getParameter("flags")) > 0);
%>
    Imcms = {
        isEditMode: <%= isEditMode %>,
        isVersioningAllowed: <%= Imcms.isVersioningAllowed() %>,
        document: {
            id: <%=document.getId()%>,
            meta: <%=metaId%>,
            type: <%=document.getDocumentTypeId()%>,
            label: "<%=document.getHeadline()%>"
        },
        language: {
            name: "<%=language.getName()%>",
            nativeName: "<%=language.getNativeName()%>",
            code: "<%=language.getCode()%>"
        }
    };
