<%@ page
	
%><%@ include file="_editor_settings.jsp" %><%!

private void setCookie( String name, String value, HttpServletResponse response ) {
	Cookie cookie = new Cookie(name, value) ;
	cookie.setMaxAge(60*60*24*365) ;
	cookie.setDomain("/") ;
	response.addCookie(cookie) ;
}

private String getCookie( String name, HttpServletRequest request ) {
	String retVal = "" ;
	Cookie[] cookies = request.getCookies() ;
	if (cookies != null) {
		for (int i = 0; i < cookies.length; i++) {
			if (cookies[i].getName().equals(name)) {
				retVal = cookies[i].getValue() ;
				break ;
			}
		}
	}
	return retVal ;
}

%>