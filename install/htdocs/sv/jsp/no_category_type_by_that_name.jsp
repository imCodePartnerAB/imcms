<%@ page import="java.io.PrintWriter"%>
<%@ page isErrorPage="true"%>
<html>
    <head>
        <title><? sv/jsp/no_category_type_by_that_name.jsp/1 ?></title>
    </head>
    <body>
    <? sv/jsp/no_category_type_by_that_name.jsp/2/1 ?> <%= request.getParameter("category_type_name") %>
    <pre>
    <% exception.printStackTrace(new PrintWriter(out)); %>
    </pre>
    </body>
</html>
