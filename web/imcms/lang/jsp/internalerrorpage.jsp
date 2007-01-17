<%@ page isErrorPage="true" import="java.io.*, org.apache.commons.lang.StringEscapeUtils"%>
<%@ page contentType="text/html; charset=UTF8" %>  
<html>
<head>
<title><? install/htdocs/sv/jsp/internalerrorpage.jsp/1 ?></title>

</head>
<body>
<H1><? install/htdocs/sv/jsp/internalerrorpage.jsp/2 ?></H1>
<p><? install/htdocs/sv/jsp/internalerrorpage.jsp/3 ?>
</p>
<p><? install/htdocs/sv/jsp/internalerrorpage.jsp/4 ?> </p>
<p><? install/htdocs/sv/jsp/internalerrorpage.jsp/5 ?> </p>
<h2><? install/htdocs/sv/jsp/internalerrorpage.jsp/6 ?></h2>
<pre>
<? install/htdocs/sv/jsp/internalerrorpage.jsp/7 ?>
<%
    Integer errorCode = (Integer)request.getAttribute("javax.servlet.error.status_code");
    if( null != errorCode ) {
        out.println( errorCode );
    }
%>
<? install/htdocs/sv/jsp/internalerrorpage.jsp/8 ?>
<%
    String errorMessage = (String)request.getAttribute("javax.servlet.error.message");
    if( null != errorMessage ) {
        out.println( StringEscapeUtils.escapeHtml(errorMessage) );
    }
%>
<? install/htdocs/sv/jsp/internalerrorpage.jsp/exception ?>
<%
    Throwable exceptionFromRequest = (Throwable)request.getAttribute("javax.servlet.error.exception");
    if( null != exceptionFromRequest ) {
        StringWriter writer = new StringWriter();
        exceptionFromRequest.printStackTrace(new PrintWriter(writer));
        out.println(StringEscapeUtils.escapeHtml(writer.toString()));
    }
%>
</pre>
</body>
</html>
