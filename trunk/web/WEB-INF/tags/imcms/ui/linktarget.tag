<%@ tag import="org.apache.commons.lang.StringEscapeUtils"%><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@attribute name="name" required="true" %>
<%@attribute name="target" required="true" %>
<table border="0" cellspacing="0" cellpadding="0" id="${name}">
    <tr>
        <td><input type="radio" name="${name}" id="target_self" value="_self"<%
        if ("_self".equalsIgnoreCase( target ) || "".equals( target )) {
            %> checked<%
            target = null;
        } %>></td>
        <td class="imcmsAdmText">&nbsp;<label for="target_self"><fmt:message key="install/htdocs/sv/jsp/docadmin/document_information.jsp/1015"/></label>&nbsp;</td>
        <td><input type="radio" name="${name}" id="target_blank" value="_blank"<%
        if ("_blank".equalsIgnoreCase( target ) ) {
            %> checked<%
            target = null;
        } %>></td>
        <td class="imcmsAdmText">&nbsp;<label for="target_blank"><fmt:message key="install/htdocs/sv/jsp/docadmin/document_information.jsp/1016"/></label>&nbsp;</td>
        <td><input type="radio" name="${name}" id="target_top" value="_top"<%
        if ("_top".equalsIgnoreCase( target ) ) {
            %> checked<%
            target = null;
        } %>></td>
        <td class="imcmsAdmText">&nbsp;<label for="target_blank"><fmt:message key="install/htdocs/sv/jsp/docadmin/document_information.jsp/1017"/></label>&nbsp;</td>
        <td><input type="radio" name="${name}" id="target_other" value="" <% if (null != target) { %> checked<% } %>></td>
        <td class="imcmsAdmText">&nbsp;<label for="target_other"><fmt:message key="install/htdocs/sv/jsp/docadmin/document_information.jsp/1018"/></label>&nbsp;</td>
        <td>
        <input type="text" name="${name}" size="9" maxlength="20" style="width:120"
               value="<%
        if (null != target) {
            %><%= StringEscapeUtils.escapeHtml( target ) %><%
        } %>"></td>
    </tr>
</table>
