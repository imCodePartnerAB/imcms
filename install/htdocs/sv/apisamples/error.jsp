<%@ page import="java.io.PrintWriter,
                 com.imcode.imcms.api.NoPermissionException"%>
<%@page isErrorPage="true"  %>

<h2>There was an error in one of the API sample pages.</h2>
<pre>
<%
    if( exception instanceof NoPermissionException ){
        out.println( "You dont have the right permission to do this. <br><br>");
    } else {
        if( exception instanceof NullPointerException ) {
            out.println( "A NullPointerException occurred<br>");
            out.println( "The reason could be that you are not logged in.<br><br>");
        }
        out.println( "Exception type: " + exception.getClass().getName()  + "<br>" );
        out.println( "Message: " + exception.getMessage()  + "<br>" );
        out.println( "Stack trace");
        PrintWriter writer = new PrintWriter( out );
        exception.printStackTrace( writer );
    }

%>
</pre>