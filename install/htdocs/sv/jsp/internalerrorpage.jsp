<%@ page isErrorPage="true" import="java.io.*"%>
<html>
<head>
<title><? sv/jsp/internalerrorpage.jsp/1 ?></title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</head>
<body>
<H1><? sv/jsp/internalerrorpage.jsp/2 ?></H1>
<p><? sv/jsp/internalerrorpage.jsp/3 ?>
</p>
<p><? sv/jsp/internalerrorpage.jsp/4 ?> </p>
<p><? sv/jsp/internalerrorpage.jsp/5 ?> </p>
<h2><? sv/jsp/internalerrorpage.jsp/6 ?></h2>
<pre>
<? sv/jsp/internalerrorpage.jsp/7 ?>
<%
    Integer errorCode = (Integer)request.getAttribute("javax.servlet.error.status_code");
    if( null != errorCode ) {
        out.println( errorCode );
    }
%>
<? sv/jsp/internalerrorpage.jsp/8 ?>
<%
    String errorMessage = (String)request.getAttribute("javax.servlet.error.message");
    if( null != errorMessage ) {
        out.println( errorMessage );
    }
%>
<? sv/jsp/internalerrorpage.jsp/9 ?>
<%
    Throwable exceptionFromRequest = (Throwable)request.getAttribute("javax.servlet.error.exception");
    if( null != exceptionFromRequest ) {
        exceptionFromRequest.printStackTrace(new PrintWriter(out));
    }
%>
<? sv/jsp/internalerrorpage.jsp/10 ?>
<%
    if( null != exception ) {
        exception.printStackTrace(new PrintWriter(out));
    }
%>
</pre>
</body>
</html>
