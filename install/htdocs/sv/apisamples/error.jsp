<%@ page import="java.io.PrintWriter,
                 com.imcode.imcms.api.NoPermissionException"%>
<%@page isErrorPage="true"  %>

<head>
<title>Error</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</head>

<h2>An error uccured in one of the API sample pages.</h2>
<%
    if( exception instanceof NoPermissionException ){
        out.println( "You dont have the right permission to do this. <br><br>");
    } else {
        if( exception instanceof NullPointerException ) {
            out.println( "A NullPointerException uccured<br>");
            out.println( "It could be because of that you are not logged in.<br><br>");
        }
        out.println( "Exception type: " + exception.getClass().getName()  + "<br>" );
        out.println( "Message: " + exception.getMessage()  + "<br>" );
        out.println( "Stack trace");
        PrintWriter writer = new PrintWriter( out );
        exception.printStackTrace( writer );
    }
%>
</body>
</html>