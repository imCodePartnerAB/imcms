<%@ page import="java.io.PrintWriter,
                 com.imcode.imcms.api.NoPermissionException" isErrorPage="true" %>
<html>
<body>
<h1>error.jsp : There was an error in one of the API sample pages.</h1>
<pre>
<%
    if( exception instanceof NoPermissionException ){
        out.println( "You dont have the right permission to do this.");
    } else {
        out.println( "Exception type: " + exception.getClass().getName() );
    }
    out.println( "Message: " + exception.getMessage() );
    exception.printStackTrace( new PrintWriter( out ) );
%>
</pre>
</body>
</html>