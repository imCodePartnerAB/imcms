<%@ page import="imcode.server.Imcms"%><%@ page
                 contentType="text/javascript"
                 pageEncoding="UTF-8"

%>
        <%
        Integer metaId = Integer.parseInt(request.getParameter("meta_id"));
        Integer typeId = Imcms.getServices().getDocumentMapper().getDocument(metaId).getDocumentTypeId();
        %>
        Imcms.isEditMode = <%=
            request.getParameterMap().containsKey("flags")&&
            Integer.valueOf(request.getParameter("flags"))>0
            %>;
        Imcms.document = {
            "meta": <%=metaId%>,
            "type": <%=typeId%>
        };
        Imcms.language = {
            name: "<%=Imcms.getServices().getDocumentLanguages()
        .getByCode(request.getParameter("language").substring(0,2)).getName()%>"
        };

        $(document).ready(function () {
            new Imcms.Bootstrapper().bootstrap(Imcms.isEditMode);
        });
