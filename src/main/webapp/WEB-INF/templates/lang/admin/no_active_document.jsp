<%@page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags/imcms/ui" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title><? templates/sv/no_active_document.html/1 ?></title>


    <link rel="stylesheet" type="text/css" href="${contextPath}/dist/imcms_admin.css">


</head>
<body bgcolor="#FFFFFF">

<ui:imcms_gui_outer_start/>
<c:set var="heading">
    <fmt:message key="templates/sv/no_active_document.html/2"/>
</c:set>
<ui:imcms_gui_head heading="${heading}"/>
<table border="0" cellspacing="0" cellpadding="0">
    <tr>
        <td>
            <table border="0" cellpadding="0" cellspacing="0">
                <form action="${contextPath}/">
                    <tr>
                        <td><input type="Submit" value="<? templates/sv/no_active_document.html/3 ?>"
                                   class="imcmsFormBtn"></td>
                    </tr>
                </form>
            </table>
        </td>
        <td>&nbsp;</td>
        <td>
            <table border="0" cellpadding="0" cellspacing="0">
                <div>
                    <tr>
                        <td><button type="Submit" id="backBtn" style="display: none;"
                                    class="imcmsFormBtn"><fmt:message key="templates/sv/no_active_document.html/4"/></button></td>
                    </tr>
                </div>
            </table>
        </td>
    </tr>
</table>
<ui:imcms_gui_mid/>

<table border="0" cellspacing="0" cellpadding="2" width="310">
    <tr>
        <td align="center" class="imcmsAdmText"><b><? templates/sv/no_active_document.html/5 ?>!</b><br>
        </td>
    </tr>
</table>
<ui:imcms_gui_bottom/>
<ui:imcms_gui_outer_end/>


<script language="JavaScript">
    <!--
    if (document.forms[1]) {
        var f = document.forms[1];
        if (f.elements[0]) {
            f.elements[0].blur();
        }
    }
    //-->
</script>
<script>
	window.onload = function () {
		const history = window.history;
		const backBtn = document.getElementById('backBtn');
		//need to check whether is first page in history is ours
		if (history.length > 2)
			backBtn.style.display = "inline";
		backBtn.addEventListener('click', () => history.back());
	}
</script>

</body>
</html>
