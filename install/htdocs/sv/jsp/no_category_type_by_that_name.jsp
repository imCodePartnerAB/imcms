<%@ page import="java.io.PrintWriter"%>
<%@ page isErrorPage="true"%>
<html>
    <head>
        <title>Ogiltigt kategorinamn</title>
    </head>
    <body>
    Det finns ingen kategori-typ med namnet <%= request.getParameter("category_type_name") %>
    <pre>
    <% exception.printStackTrace(new PrintWriter(out)); %>
    </pre>
    </body>
</html>
