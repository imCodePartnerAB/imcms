<%@ page
	
	import="imcode.server.document.DocumentDomainObject,
	        org.apache.commons.lang.StringEscapeUtils"
	
	contentType="text/html; charset=UTF-8"
	
%><%@taglib uri="imcmsvelocity" prefix="vel"
%><%

String label = (String)request.getAttribute( "label" );
DocumentDomainObject includingDocument = (DocumentDomainObject)request.getAttribute( "includingDocument" ) ;
Integer includedDocumentId = (Integer)request.getAttribute( "includedDocumentId" ) ;
boolean validId = null != includedDocumentId;
String includedDocumentIdString = validId ? ""+includedDocumentId : "" ;
Integer includeIndex = (Integer)request.getAttribute( "includeIndex" ) ;

%><vel:velocity>
<style type="text/css">
/*<![CDATA[*/
INPUT.imcmsFormBtnSmall {
	background-color: #20568D;
	color: #fff;
	font: 10px Tahoma, Arial, sans-serif;
	border: 2px outset #668DB6;
	border-color: #668DB6 #000 #000 #668DB6;
	padding: 0 2px;
	cursor: pointer;
}
.changePageButton {
	background-color: #e2e2e4;
	font: 10px Tahoma, Arial, sans-serif;
	color: #000;
	padding: 0 4px;
	border-width: 1px;
	border-style: outset;
	border-color: #ccc #666 #666 #ccc;
	cursor: pointer;
}
/*]]>*/
</style>

<span class="imcms_label"><%
if (validId) {
	%><a href="$contextPath/servlet/GetDoc?meta_id=<%= includedDocumentIdString %>" class="imcms_label"><%
}
%><%= StringEscapeUtils.escapeHtml( label ) %><%
if (validId) {
	%></a><%
} %></span>

<form action="SaveInclude" method="post">
<input type="hidden" name="meta_id" value="<%= includingDocument.getId() %>" />
<input type="hidden" name="include_id" value="<%= includeIndex %>" />
<table border="0" cellspacing="0" cellpadding="3" style="border: 1px solid #e1ded9; background-color:#f5f5f7;">
<tr bgcolor="#20568D">
	<td align="center"><span style="font: bold 9pt Tahoma,Verdana,sans-serif; color:#fff"><? templates/sv/change_include.html/1 ?></span></td>
	<td align="right"><input type="button" value="<? global/help ?>" title="<? global/openthehelppage ?>" class="imcmsFormBtnSmall" onclick="openHelpW('Include'); return false" /></td>
</tr>
<tr>
	<td height="35"><input type="text" name="include_meta_id" value="<%= includedDocumentIdString %>" size="6" maxlength="6" style="font: 9pt Tahoma,Verdana,sans-serif" /></td>
	<td align="right"><input type="submit" name="ok" value=" <? templates/sv/change_include.html/2001 ?> " class="changePageButton" /></td>
</tr><%
if (validId) { %>
<tr>
	<td colspan="1" align="center">
	<a href="$contextPath/servlet/GetDoc?meta_id=<%= includedDocumentId %>" target="_blank"><span style="font: x-small Verdana,Geneva,sans-serif; color:#000099; text-decoration:none"><? templates/sv/change_include.html/1001 ?></span></a></td>
</tr><%
} %>
</table>
</form>
</vel:velocity>
