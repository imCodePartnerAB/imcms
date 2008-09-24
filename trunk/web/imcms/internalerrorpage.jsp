<%@ page import="imcode.server.user.UserDomainObject,
                 imcode.util.Utility" %>
<%
    UserDomainObject user = Utility.getLoggedOnUser(request);
    String language = "eng";
    if ( null != user ) {
        language = user.getLanguageIso639_2();
    }
    request.getRequestDispatcher("/imcms/" + language + "/jsp/internalerrorpage.jsp").forward(request, response);
%>