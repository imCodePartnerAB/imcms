<%@ page language="java"
	import="org.apache.oro.util.*, org.apache.oro.text.*, org.apache.oro.text.regex.*, org.apache.oro.text.perl.*, java.io.*, java.util.*, java.text.*, java.net.*, javax.servlet.*, javax.servlet.http.*, imcode.external.diverse.*, imcode.util.*, imcode.server.*"
%><%
/* *******************************************************************
 *           INIT                                                    *
 ******************************************************************* */

final String FRAMESET_TEMPLATE  = "/user/_chat_frameset.html" ;

	/* This is the default frameset for the chat.
     This must be there if you access the "chat.jsp" whitout the show parameter.
		 To access another framset, you add "chat.jsp?show=my_new_chat" to the URL.
		 If "FRAMESET_TEMPLATE" is "/user/_chat_frameset.html", this page will then
     open "/user/_chat_frameset_my_new_chat.html".
     It simply adds an underscore character and the show parameter. */

final String DEFAULT_CHAT_PARAM  = "chat" ;

	/* You can use this name in the show-parameter even if you
     don't have a corresponding page for it.
     When this is used, the default FRAMESET_TEMPLATE is opened.
     ie: if this param is "default", you can use both "chat.jsp"
         and "chat.jsp?show=default" to open the default FRAMESET_TEMPLATE.

/* *******************************************************************
 *           FUNCTIONS                                               *
 ******************************************************************* */

String sFile1 = FRAMESET_TEMPLATE.substring(0, FRAMESET_TEMPLATE.lastIndexOf(".")) ;
String sFile2 = FRAMESET_TEMPLATE.substring(FRAMESET_TEMPLATE.lastIndexOf("."), FRAMESET_TEMPLATE.length()) ;
String file   = sFile1 + sFile2 ;

String show   = request.getParameter("show") ;  // for selected chat & for dummy-frame (empty)
String color  = (request.getParameter("color") != null) ? request.getParameter("color") : "ffffff" ; // for dummy-frame

boolean isFrameset = true ;

if (show != null && show.equals("empty")) {
	/* It's the dummy frame */
	isFrameset = false ;
} else if (show != null && !show.equals("chat")) {
	/* It's a frameset template other then the default */
	file = sFile1 + "_" + show + sFile2 ;
}

String errMess = null ;

/* *******************************************************************
 *           DOIT                                                    *
 ******************************************************************* */

if (isFrameset) {

	File webRoot    = imcode.server.WebAppGlobalConstants.getInstance().getAbsoluteWebAppPath() ;
	String filePath = file.substring(0, file.lastIndexOf("/")) ;
	String fileName = file.substring(file.lastIndexOf("/") + 1, file.length()) ;

	/* get full path */

	File fn = new File(fileName) ;
	fn = new File (new File(webRoot + filePath),fn.getName()) ;

	if (fn.exists()) {

		/* Read file and show it */

		String fileSrc  = "" ;
		String fileLine = "" ;
		String tempStr  = "";

		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fn))) ;

		while ((fileLine = br.readLine()) != null) {
			tempStr = fileLine + "\n" ;
			if (tempStr.length() > 0) {
				fileSrc += tempStr ;
			}
		}
		br.close() ;
		out.print(fileSrc) ;

	} else {
		errMess    = "<? install/htdocs/sv/jsp/chat.jsp/1/1 ?>" ;
		isFrameset = false ;
	}
}
if (!isFrameset) { %>
<html>
<head>
<title></title>


</head>
<body bgcolor="#<%= color %>">
<% if (errMess != null) { %><h3 style="font-family: Verdana,sans-serif; color:#999999"><%= errMess %></h3><% } %>&nbsp;
</body>
</html><%
} %>