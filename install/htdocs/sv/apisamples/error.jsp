<%@ page import="java.io.PrintWriter,
                 com.imcode.imcms.api.NoPermissionException,
                 com.imcode.imcms.api.NotLoggedInException"%>
<%@page isErrorPage="true"  %>

<h2>error.jsp : There was an error in one of the API sample pages.</h2>

<pre>
<%
    if( exception instanceof NoPermissionException ){
        out.println( "You dont have the right permission to do this. <br><br>");
    } else if (exception instanceof NotLoggedInException)  {
        out.println("You are not logged in. <br><br>");
    } else {
        out.println( "Exception type: " + exception.getClass().getName()  + "<br>" );
        out.println( "Message: " + exception.getMessage()  + "<br>" );
        out.println( "Stack trace");
        PrintWriter writer = new PrintWriter( out );
        exception.printStackTrace( writer );
    }

%>
</pre>