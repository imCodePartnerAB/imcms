<%@ page import="imcode.server.Imcms" %><%@
	page import="imcode.server.document.DocumentDomainObject" %><%@
	page import="com.imcode.imcms.api.DocumentLanguage"%><%@
	page contentType="text/javascript" pageEncoding="UTF-8"	%><%

	Integer metaId = Integer.parseInt(request.getParameter("meta_id"));
	DocumentDomainObject document =  Imcms.getServices().getDocumentMapper().getDocument(metaId);
	Integer typeId = document.getDocumentTypeId();
	Integer id = document.getId();
	String label = document.getHeadline();
	DocumentLanguage language = document.getLanguage();
%>
        Imcms.isEditMode = <%= request.getParameterMap().containsKey("flags")
		 && Integer.valueOf(request.getParameter("flags")) > 0 %>;

        Imcms.document = {
            id: <%=id%>,
            meta: <%=metaId%>,
            type: <%=typeId%>,
            label: "<%=label%>"
        };
        Imcms.language = {
            name: "<%=language.getName()%>",
            nativeName: "<%=language.getNativeName()%>",
            code: "<%=language.getCode()%>"
        };

        $.ajaxSetup({cache: false});
        CKEDITOR.disableAutoInline = true;

        $(document).ready(function () {
            new Imcms.Bootstrapper().bootstrap(Imcms.isEditMode);
        });
