<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags/imcms/ui" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>

<html>
<head>
    <title><fmt:message key="templates/login/logged_out.html/1"/></title>

    <link rel="stylesheet" type="text/css" href="${contextPath}/imcms/css/imcms_admin.css">

</head>
<body bgcolor="#FFFFFF">

<ui:imcms_gui_outer_start/>
<c:set var="heading">
    <fmt:message key="templates/login/logged_out.html/2"/>
</c:set>
<ui:imcms_gui_head heading="${heading}"/>
<table border="0" cellspacing="0" cellpadding="0">
    <tr>
        <td>
            <table border="0" cellpadding="0" cellspacing="0">
                <form action="${contextPath}/servlet/StartDoc">
                    <tr>
                        <td><input type="Submit" value="<fmt:message key="templates/login/logged_out.html/2001"/>"
                                   class="imcmsFormBtn"
                                   style="width:90px"></td>
                    </tr>
                </form>
            </table>
        </td>
        <td>&nbsp;</td>
        <td>
            <table border="0" cellpadding="0" cellspacing="0">
                <form action="${contextPath}/login/">
                    <tr>
                        <td><input type="Submit" value="<fmt:message key="templates/login/logged_out.html/2002"/>"
                                   class="imcmsFormBtn"
                                   style="width:90px"></td>
                    </tr>
                </form>
            </table>
        </td>
    </tr>
</table>
<ui:imcms_gui_mid/>

<table border="0" cellspacing="0" cellpadding="2" width="310">
    <tr>
        <td align="center" class="imcmsAdmText"><fmt:message key="templates/login/logged_out.html/4"/></td>
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

</body>
</html>
