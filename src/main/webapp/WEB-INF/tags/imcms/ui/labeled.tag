<%@ taglib prefix="ui" tagdir="/WEB-INF/tags/imcms/ui" %>
<%@attribute name="idref" required="true" %>
<%@attribute name="key" required="true" %>
<tr>
<td class="imcmsAdmText">
<ui:label idref="${idref}" key="${key}"/>
</td>
<td>
<jsp:doBody/>        
</td>
</tr>
