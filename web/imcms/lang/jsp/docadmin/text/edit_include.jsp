<%@ page import="org.apache.commons.lang.StringEscapeUtils,
                 imcode.server.document.DocumentDomainObject"%>
<%@page contentType="text/html"%><%@taglib uri="/WEB-INF/velocitytag.tld" prefix="vel" %>
<%
    String label = (String)request.getAttribute( "label" );
    DocumentDomainObject includingDocument = (DocumentDomainObject)request.getAttribute( "includingDocument" ) ;
    Integer includedDocumentId = (Integer)request.getAttribute( "includedDocumentId" ) ;
    boolean validId = null != includedDocumentId;
    String includedDocumentIdString = validId ? ""+includedDocumentId : "" ;
    Integer includeIndex = (Integer)request.getAttribute( "includeIndex" ) ;

%><vel:velocity>
<span class="imcms_label">
    <% if (validId) { %>
        <a href="$contextPath/servlet/GetDoc?meta_id=<%= includedDocumentIdString %>" class="imcms_label">
    <% } %>
    <%= StringEscapeUtils.escapeHtml( label )%>
    <% if (validId) { %>
        </a>
    <% } %>
</span>
<form action="SaveInclude" method="POST">
    <input type="HIDDEN" name="meta_id" value="<%= includingDocument.getId() %>">
    <input type="HIDDEN" name="include_id" value="<%= includeIndex %>">
    <table border="0" cellspacing="0" cellpadding="3" bgcolor="#f5f5f7" style="border: 1px solid #e1ded9">
        <tr bgcolor="#20568D">
            <td colspan="2" align="center"><span style="font: bold 9pt Tahoma,Verdana,sans-serif; color:#ffffff"><? templates/sv/change_include.html/1 ?></span></td>
        </tr>
        <tr>
            <td height="35">
                <input type="text" name="include_meta_id" value="<%= includedDocumentIdString %>" size="6" maxlength="6" style="font: 9pt Tahoma,Verdana,sans-serif">
            </td>
            <td align="right">
                <input type="submit" name="ok" value=" <? templates/sv/change_include.html/2001 ?> " style="font: 9pt Tahoma,Verdana,sans-serif; height:22">
            </td>
        </tr>
        <tr>
            <% if (validId) { %>
            <td colspan="1" align="center">
                <a href="$contextPath/servlet/GetDoc?meta_id=<%= includedDocumentId %>" target="_blank"><span style="font: x-small Verdana,Geneva,sans-serif; color:#000099; text-decoration:none"><? templates/sv/change_include.html/1001 ?></span></a>
            </td>
            <% } %>
            <td>&nbsp;<a href="@documentationurl@/GetDoc?meta_id=53" target="_blank"><img src="$contextPath/imcms/$language/images/admin/btn_help_round.gif" width="19" height="21" border="0" alt="<? templates/sv/change_include.html/2002 ?>" align="absmiddle"></a></td>
        </tr>
    </table>
</form>
</vel:velocity>
