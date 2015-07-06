<%@ page import="imcode.server.Imcms"%><%@ page
                 contentType="text/javascript"
                 pageEncoding="UTF-8"

%>
        Imcms.document = {"meta": <%=request.getParameter("meta_id")%>};
        Imcms.language = {
            name: "<%=Imcms.getServices().getDocumentLanguages()
        .getByCode(request.getParameter("language").substring(0,2)).getName()%>"
        };

        $(document).ready(function () {
            new Imcms.Bootstrapper().bootstrap(<%=
            request.getParameterMap().containsKey("flags")&&
            Integer.valueOf(request.getParameter("flags"))>0
            %>);
        });
