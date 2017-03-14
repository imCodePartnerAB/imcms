<%@ page import="com.imcode.imcms.api.DocumentLanguage" %><%@
	page import="imcode.server.Imcms" %><%@
	page import="imcode.server.document.DocumentDomainObject"%><%@
	page contentType="text/javascript" pageEncoding="UTF-8"	%><%

	Integer metaId = Integer.parseInt(request.getParameter("meta_id"));
	DocumentDomainObject document =  Imcms.getServices().getDocumentMapper().getDocument(metaId);
	Integer typeId = document.getDocumentTypeId();
	Integer id = document.getId();
	String label = document.getHeadline();
	DocumentLanguage language = document.getLanguage();
	boolean isEditMode = (request.getParameterMap().containsKey("flags")
		 && Integer.valueOf(request.getParameter("flags")) > 0);
%>
    Imcms = {
        isEditMode: <%= isEditMode %>,
        isVersioningAllowed: <%= Imcms.isVersioningAllowed() %>,
        document: {
            id: <%=id%>,
            meta: <%=metaId%>,
            type: <%=typeId%>,
            label: "<%=label%>"
        },
        language: {
            name: "<%=language.getName()%>",
            nativeName: "<%=language.getNativeName()%>",
            code: "<%=language.getCode()%>"
        }
    };
