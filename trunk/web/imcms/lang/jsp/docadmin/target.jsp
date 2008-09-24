<%@ page
	
	import="org.apache.commons.lang.StringEscapeUtils"
    contentType="text/html; charset=UTF-8"    
	
%><%

String target = (String)request.getAttribute( "target" ) ;

%>
<table border="0" cellspacing="0" cellpadding="0">
<tr>
	<td><input type="radio" name="target" id="target0" value="_self"<%
	if ("_self".equalsIgnoreCase( target ) || "".equals( target )) {
		%> checked<%
		target = null;
	} %>></td>
	<td class="imcmsAdmText" nowrap>&nbsp;<label for="target0"><? install/htdocs/sv/jsp/docadmin/url_document.jsp/1003 ?></label> &nbsp;</td>
	<td><input type="radio" name="target" id="target1" value="_blank"<%
	if ("_blank".equalsIgnoreCase( target )) {
		%> checked<%
		target = null;
	} %>></td>
	<td class="imcmsAdmText" nowrap>&nbsp;<label for="target1"><? install/htdocs/sv/jsp/docadmin/url_document.jsp/1004 ?></label> &nbsp;</td>
	<td><input type="radio" name="target" id="target2" value="_top"<%
	if ("_top".equalsIgnoreCase( target )) {
		%> checked<%
		target = null;
	} %>></td>
	<td class="imcmsAdmText" nowrap>&nbsp;<label for="target2"><? install/htdocs/sv/jsp/docadmin/url_document.jsp/1005 ?></label> &nbsp;</td>
</tr>
<tr>
	<td><input type="radio" name="target" id="target3"<%
	if (null != target) {
		%> checked<%
	} %> onClick="if (document.getElementById) document.getElementById('target4').focus();"></td>
	<td class="imcmsAdmText" nowrap>&nbsp;<label for="target3"><? install/htdocs/sv/jsp/docadmin/url_document.jsp/1006 ?></label> &nbsp;</td>
	<td colspan="4"><input type="text" name="target" id="target4" size="17" maxlength="50" value="<%
	if (null != target) {
		%><%= StringEscapeUtils.escapeHtml( target ) %><%
	} %>"></td>
</tr>
</table>
