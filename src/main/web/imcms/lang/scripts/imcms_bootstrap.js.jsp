<%@ page
                 contentType="text/javascript"
                 pageEncoding="UTF-8"

%>
        Imcms.document = {"meta": <%=request.getParameter("meta_id")%>};

        $(document).ready(function () {
            new Imcms.Bootstrapper().bootstrap();
        });
