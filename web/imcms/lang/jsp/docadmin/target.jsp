<%@ page import="org.apache.commons.lang.StringEscapeUtils"%>
<% String target = (String)request.getAttribute( "target" ) ; %>
        <table border="0" cellspacing="0" cellpadding="0">
            <tr>
                <td><input type="radio" name="target" value="_self"<% if ("_self".equalsIgnoreCase( target ) || "".equals( target )) {%> checked<% target = null; }%>></td>
                <td class="imcmsAdmText" nowrap>&nbsp;<? install/htdocs/sv/jsp/docadmin/url_document.jsp/1003 ?> &nbsp;</td>
                <td><input type="radio" name="target" value="_blank"<% if ("_blank".equalsIgnoreCase( target )) {%> checked<% target = null; }%>></td>
                <td class="imcmsAdmText" nowrap>&nbsp;<? install/htdocs/sv/jsp/docadmin/url_document.jsp/1004 ?> &nbsp;</td>
                <td><input type="radio" name="target" value="_top"<% if ("_top".equalsIgnoreCase( target )) {%> checked<% target = null; }%>></td>
                <td class="imcmsAdmText" nowrap>&nbsp;<? install/htdocs/sv/jsp/docadmin/url_document.jsp/1005 ?> &nbsp;</td>
            </tr>
            <tr>
                <td><input type="radio" name="target"<% if (null != target) {%> checked<%}%>></td>
                <td class="imcmsAdmText" nowrap>&nbsp;<? install/htdocs/sv/jsp/docadmin/url_document.jsp/1006 ?> &nbsp;</td>
                <td colspan="4">
                    <input type="text" name="target" size="17" maxlength="50"
                        value="<% if (null != target) {%><%= StringEscapeUtils.escapeHtml( target ) %><%}%>">
                </td>
            </tr>
        </table>
