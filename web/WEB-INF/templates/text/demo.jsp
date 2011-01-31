<%@ page
	
	contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"

%><%@taglib prefix="imcms" uri="imcms"
%><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" 
%><%@taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"
%><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<imcms:variables/>
<head>
	
	<title><c:out value="${document.headline}"/> - Powered by imCMS from imCode Partner AB</title>

<style type="text/css">
/*<![CDATA[*/
BODY { height: 100%; margin: 10px; text-align:center; background-color: #f0f0ff; }
H1 { margin-bottom:1em; font: bold medium Verdana,Geneva,sans-serif; color:#009; }
TD, .text { font: x-small Verdana,Geneva,sans-serif; text-align:left; color:#000; }
.small { font: xx-small Verdana,Geneva,sans-serif; color:#000; }
PRE, TT { font: x-small "Courier New", Courier, monospace; color:#888; }
A:link    { color:#009; }
A:visited { color:#009; }
A:active  { color:#c00; }
A:hover   { color:#00f; }
LI { padding-bottom:5px; }
#container {
	width: 770px;
	margin: 0 auto;
	border-width: 1px 2px 2px 1px;
	border-style: solid;
	border-color: #ccc #000 #000 #ccc;
	background-color: #fff;
}
.adminPanelTable {
	margin: 0 auto !important;
}
#changePageTable {
	margin: 0 auto !important;
}
/*]]>*/
</style>

</head>
<body>


<div id="container">
	<table border="0" cellspacing="0" cellpadding="5">
	<tr>
		<td valign="top">
		<table border="0" cellspacing="0" cellpadding="0" style="width:100%;">
		<tr>
			<td colspan="3" style="padding-bottom:15px;"><imcms:include url="@documentationwebappurl@/servlet/GetDoc?meta_id=1054&template=imcmsDemoTop"/></td>
		</tr>
		<tr valign="top">
			<td style="width:200px; padding-right:15px;"><imcms:include url="@documentationwebappurl@/servlet/GetDoc?meta_id=1054&template=imcmsDemoLeft"/></td>
	
			<td>
			<imcms:text no="1" label="Text (Rubrik)" pre='<h1>' post='</h1>' formats="text" rows="2" />
			<imcms:text no='2' label='<br/>Text' pre='<div class="text">' post='</div>' />
			<imcms:menu no='1' label='<br/><br/>Meny (punktlista)'>
			<ul>
			<imcms:menuloop>
				<imcms:menuitem>
					<li style="color:green;"><imcms:menuitemlink><c:out value="${menuitem.document.headline}"/></imcms:menuitemlink></li>
				</imcms:menuitem>
				<imcms:menuitem>
					<imcms:menuitemhide>
						<li style="color:red;"><imcms:menuitemlink><c:out value="${menuitem.document.headline}"/></imcms:menuitemlink></li>
					</imcms:menuitemhide>
				</imcms:menuitem>
			</imcms:menuloop>
			</ul>
			</imcms:menu>
			<imcms:include url="@documentationwebappurl@/servlet/GetDoc?meta_id=1054&template=imcmsDemoContent" pre='<hr/>' post='<hr/>'/>
			<imcms:image no='3' label='Bild' pre='<br/><br/>' post='<br/>'/><br/>
			<imcms:include no='1' label='Dynamisk inkludering 1'/>
			</td>
	
			<td style="width:150px; padding-left:10px;"><imcms:include url="@documentationwebappurl@/servlet/GetDoc?meta_id=1054&template=imcmsDemoRight"/></td>
		</tr>
		</table></td>
	</tr>
	<tr>
		<td valign="bottom" style="text-align:center;"><imcms:admin/>
		<imcms:include url="@documentationwebappurl@/servlet/GetDoc?meta_id=1054&template=imcmsDemoBottom"/></td>
	</tr>
	</table>
</div>


</body>
</html>
