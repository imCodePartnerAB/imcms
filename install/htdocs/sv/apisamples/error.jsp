<%@ page import="java.io.PrintWriter"%>
<%@page isErrorPage="true"  %>

<h2>This is an sample error page</h2>
Received an exception of type <%= exception.getClass() %><br>
<br>
Message: <%=exception.getMessage()%> <br>
<br>
Stack trace: <%exception.printStackTrace( new PrintWriter(out) ); %> <br>