<%@ page import="imcode.server.user.UserDomainObject,
                 imcode.util.Utility"%>
<%

    String language = (String) request.getAttribute("language") ;
    if (null == language) {
        UserDomainObject user = Utility.getLoggedOnUser( request );
        language = user.getLanguageIso639_2();
    }

    request.getRequestDispatcher( "/imcms/"+language+"/login/logged_out.jsp" ).forward( request, response );
%>
 