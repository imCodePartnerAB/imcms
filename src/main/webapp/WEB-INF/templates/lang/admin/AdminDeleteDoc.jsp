<%@page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags/imcms/ui" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>

<c:set var="heading">
    <fmt:message key="templates/sv/AdminManager_adminTask_element.htm/15"/>
</c:set>
<ui:imcms_gui_start_of_page titleAndHeading="${heading}"/>

<form method="post" action="AdminDeleteDoc" name="DeleteDoc" onSubmit="return confirmDeleteDoc(); return false;">
<table border="0" cellspacing="0" cellpadding="2" width="400">
<tr>
    <td>
        <table border="0" cellspacing="0" cellpadding="0">
            <tr>
                <td><? templates/sv/AdminDeleteDoc.htm/4 ?></td>
                <td>&nbsp;&nbsp;</td>
                <td><input type="text" name="delete_meta_id" maxlength="9" size="9"></td>
                <td>&nbsp;&nbsp;</td>
                <td><input type="submit" class="imcmsFormBtnSmall" name="DELETE_DOC" value="<? templates/sv/AdminDeleteDoc.htm/2001 ?>"></td>
                <td></form></td>
</tr>
</table></td>
</tr>
</table>

<script language="JavaScript">
<!--
document.DeleteDoc.delete_meta_id.focus() ;

function confirmDeleteDoc() {
    return confirm("<? templates/sv/AdminDeleteDoc.htm/2010 ?>") ;
}
//-->
</script>

<ui:imcms_gui_end_of_page/>
