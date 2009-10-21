<%@ page import="java.io.PrintWriter, org.apache.commons.lang.StringEscapeUtils"%>
<%@ page isErrorPage="true"%>
<html>
    <head>
        <title><? install/htdocs/sv/jsp/no_category_type_by_that_name.jsp/1 ?></title>
    </head>
    <body>
    <? install/htdocs/sv/jsp/no_category_type_by_that_name.jsp/2/1 ?> <%= StringEscapeUtils.escapeHtml(request.getParameter("category_type_name")) %>
    <pre>
    <% exception.printStackTrace(new PrintWriter(out)); %>
    </pre>
    </body>
</html>
