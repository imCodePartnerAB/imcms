<%@ page import="java.io.PrintWriter,
                 com.imcode.imcms.api.NoPermissionException" isErrorPage="true" %>
<html>
<body>
<%
    if (null != exception) {
        %><h1>There was an error in one of the API sample pages.</h1><%
        %><pre><%
        if( exception instanceof NoPermissionException ){
            out.println( "You dont have the right permission to do this.");
        } else {
            out.println( "Exception type: " + exception.getClass().getName()  + "<br>" );
            out.println( "Message: " + exception.getMessage()  + "<br>" );
            out.println( "Stack trace");
            PrintWriter writer = new PrintWriter( out );
            exception.printStackTrace( writer );
        }
        %></pre><%
    } else {
        %><h1>No error.</h1><%
    }
%>
</body>
</html>