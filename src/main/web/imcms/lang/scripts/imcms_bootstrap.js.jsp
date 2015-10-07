<%@ page import="imcode.server.Imcms"%><%@ page import="imcode.server.document.DocumentDomainObject"%><%@ page
                 contentType="text/javascript"
                 pageEncoding="UTF-8"

%>
        <%
        Integer metaId = Integer.parseInt(request.getParameter("meta_id"));
        DocumentDomainObject document =  Imcms.getServices().getDocumentMapper().getDocument(metaId);
        Integer typeId = document.getDocumentTypeId();
        String label = document.getHeadline();
        %>
        Imcms.isEditMode = <%=
            request.getParameterMap().containsKey("flags")&&
            Integer.valueOf(request.getParameter("flags"))>0
            %>;
        Imcms.document = {
            "meta": <%=metaId%>,
            "type": <%=typeId%>,
            "label": "<%=label%>"
        };
        Imcms.language = {
            name: "<%=Imcms.getServices().getDocumentLanguages()
        .getByCode(request.getParameter("language").substring(0,2)).getName()%>"
        };

        Imcms.contextPath = "<%=request.getContextPath()%>";

        $.ajaxSetup({cache: false});
        CKEDITOR.disableAutoInline = true;

        $(document).ready(function () {
            new Imcms.Bootstrapper().bootstrap(Imcms.isEditMode);
        });
