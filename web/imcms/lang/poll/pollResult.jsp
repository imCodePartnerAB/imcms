<%@ page language="java"
%><%


String meta_id = request.getParameter("meta_id");
String template = request.getParameter("template"); 

%>

<html>
<head>
<title></title>
 
<SCRIPT LANGUAGE="JavaScript">
<!--
var window_width = 600;
var window_height = 400;
var sUrl = "<%= request.getContextPath() %>/servlet/GetDoc?meta_id=<%=meta_id%>&template=<%=template%>";
var sName = "resultWin";
if(screen.height < 700){ 
 var window_top = 0;
 var window_left = (screen.width-window_width)/2;
} else { 
 var window_top = (screen.height-window_height)/2;
 var window_left = (screen.width-window_width)/2;
}
popWindow = window.open(sUrl,sName,'resizable=yes,menubar=0,scrollbars=yes,width=' + window_width + ',height=' + window_height + ',top=' + window_top + ',left=' + window_left + '');
popWindow.focus();

window.close();
 
//-->
</SCRIPT>
 
</head>
<body>
</body>
</html>

 