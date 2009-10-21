<%@ page import="imcode.util.Utility,
                 imcode.server.user.UserDomainObject"%>
<%
    UserDomainObject user = Utility.getLoggedOnUser( request );
    request.getRequestDispatcher( "/imcms/"+user.getLanguageIso639_2()+"/login/" ).forward( request, response );
%>
 