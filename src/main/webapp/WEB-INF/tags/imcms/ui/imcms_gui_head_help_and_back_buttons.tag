<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="helpDocName" %>
<%@ attribute name="formAction" %>
<%@ attribute name="formName" %>
<%@ attribute name="cancelButtonName" %>

<!-- gui_head_help_and_back_buttons -->
<table border="0" cellspacing="0" cellpadding="0">
    <form method="post" action="$formAction">
        <tr>
            <td>
                <input type="submit" class="imcmsFormBtn" name="$cancelButtonName"
                       value="<fmt:message key="global/back"/>">
            </td>
            <c:if test="${not empty helpDocName}">
                <td>&nbsp;</td>
                <td>
                    <input type="button" value="<? global/help ?>" title="<? global/openthehelppage ?>"
                           class="imcmsFormBtn" onClick="openHelpW('$helpDocName')">
                </td>
            </c:if>
        </tr>
    </form>
</table>
<!-- /gui_head_help_and_back_buttons -->
