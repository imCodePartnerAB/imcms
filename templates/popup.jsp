<%@ page language="java"
%><%


String meta_id = request.getParameter("meta_id");
String popup_meta_id = request.getParameter("popup_meta_id");

%>

<html> 
<head>
<title></title> 

<SCRIPT language=JavaScript>
<!--
var window_width = 600;
var window_height = 400;
var pUrl = "@servleturl@/GetDoc?meta_id=<%=popup_meta_id%>";
var sUrl = "@servleturl@/GetDoc?meta_id=<%=meta_id%>";
var sName = "pollWin";
if(screen.height < 700){ 
		var window_top = 0;
		var window_left = (screen.width-window_width)/2;
} else { 
		var window_top = (screen.height-window_height)/2;
		var window_left = (screen.width-window_width)/2;
}
popWindow = window.open(pUrl,sName,'resizable=yes,menubar=0,scrollbars=yes,width=' + window_width + ',height=' + window_height + ',top=' + window_top + ',left=' + window_left + '');
popWindow.focus();

document.location = sUrl;	

//-->
</SCRIPT>
</head>
<body></body>
</html> 
