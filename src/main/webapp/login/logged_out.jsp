<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags/imcms/ui" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<html>
<head>
    <title><fmt:message key="templates/login/logged_out.html/1"/></title>

    <link rel="stylesheet" type="text/css" href="${contextPath}/imcms/css/imcms_admin.css">

</head>
<body bgcolor="#FFFFFF">

<table border="0" cellspacing="0" cellpadding="0" class="imcmsAdmTable" align="center">
    <tr>
        <td class="imcmsAdmTable">
            <table border="0" cellspacing="0" cellpadding="0">
                <tr>
                    <td class="imcmsAdmBgHead" colspan="6"><img
                            src="${contextPath}/imcms/lang/images/admin/1x1.gif" width="1" height="20"></td>
                </tr>
                <tr class="imcmsAdmBgHead">
                    <td colspan="2"><img src="${contextPath}/imcms/lang/images/admin/1x1.gif" width="1" height="1"></td>
                    <td nowrap><span class="imcmsAdmHeadingTop"><fmt:message
                            key="templates/login/logged_out.html/2"/></span></td>
                    <td align="right"><a href="http://www.imcms.net/" target="_blank"><img
                            src="${contextPath}/imcms/lang/images/admin/logo_imcms_admin.gif" width="100" height="20"
                            alt="www.imcms.net" border="0"></a></td>
                    <td colspan="2"><img src="${contextPath}/imcms/lang/images/admin/1x1.gif" width="1" height="1"></td>
                </tr>
                <tr>
                    <td colspan="6" class="imcmsAdmBgHead"><img
                            src="${contextPath}/imcms/lang/images/admin/1x1.gif" width="1" height="20"></td>
                </tr>
                <tr class="imcmsAdmBgHead">
                    <td colspan="2"><img src="${contextPath}/imcms/lang/images/admin/1x1.gif" width="1" height="1"></td>
                    <td colspan="2">
                        <table border="0" cellspacing="0" cellpadding="0">
                            <tr>
                                <td>
                                    <table border="0" cellpadding="0" cellspacing="0">
                                        <form action="${contextPath}/">
                                            <tr>
                                                <td><input type="Submit"
                                                           value="<fmt:message key="templates/login/logged_out.html/2001"/>"
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
                                                <td><input type="Submit"
                                                           value="<fmt:message key="templates/login/logged_out.html/2002"/>"
                                                           class="imcmsFormBtn"
                                                           style="width:90px"></td>
                                            </tr>
                                        </form>
                                    </table>
                                </td>
                            </tr>
                        </table>
                    </td>
                    <td colspan="2">
                        <img src="${contextPath}/imcms/images/1x1.gif" width="1" height="1">
                    </td>
                </tr>
                <tr>
                    <td class="imcmsAdmBgHead" colspan="6">
                        <img src="${contextPath}/imcms/images/1x1.gif" width="1" height="20">
                    </td>
                </tr>
                <tr>
                    <td height="10" class="imcmsAdmBorder">
                        <img src="${contextPath}/imcms/images/1x1.gif" width="1" height="1">
                    </td>
                    <td class="imcmsAdmBgCont" colspan="4">
                        <img src="${contextPath}/imcms/images/1x1.gif" width="1" height="1">
                    </td>
                    <td class="imcmsAdmBorder">
                        <img src="${contextPath}/imcms/images/1x1.gif" width="1" height="1">
                    </td>
                </tr>
                <tr class="imcmsAdmBgCont">
                    <td class="imcmsAdmBorder">
                        <img src="${contextPath}/imcms/images/1x1.gif" width="1" height="1">
                    </td>
                    <td>
                        <img src="${contextPath}/imcms/images/1x1.gif" width="1" height="1">
                    </td>
                    <td colspan="2">

                        <table border="0" cellspacing="0" cellpadding="2" width="310">
                            <tr>
                                <td align="center" class="imcmsAdmText"><fmt:message
                                        key="templates/login/logged_out.html/4"/></td>
                            </tr>
                        </table>
                    </td>
                    <td>
                        <img src="${contextPath}/imcms/images/1x1.gif" width="1" height="1">
                    </td>
                    <td class="imcmsAdmBorder">
                        <img src="${contextPath}/imcms/images/1x1.gif" width="1" height="1">
                    </td>
                </tr>
                <tr>
                    <td height="10" class="imcmsAdmBorder">
                        <img src="${contextPath}/imcms/images/1x1.gif" width="1" height="1">
                    </td>
                    <td colspan="4" class="imcmsAdmBgCont">
                        <img src="${contextPath}/imcms/images/1x1.gif" width="1" height="1">
                    </td>
                    <td class="imcmsAdmBorder">
                        <img src="${contextPath}/imcms/images/1x1.gif" width="1" height="1">
                    </td>
                </tr>
                <tr class="imcmsAdmBgCont">
                    <td>
                        <img src="${contextPath}/imcms/images/1x1.gif" width="1" height="1">
                    </td>
                    <td>
                        <img src="${contextPath}/imcms/images/1x1.gif" width="24" height="1">
                    </td>
                    <td colspan="2">
                        <img src="${contextPath}/imcms/images/1x1.gif" width="1" height="1">
                    </td>
                    <td>
                        <img src="${contextPath}/imcms/images/1x1.gif" width="24" height="1">
                    </td>
                    <td>
                        <img src="${contextPath}/imcms/images/1x1.gif" width="1" height="1">
                    </td>
                </tr>
            </table>
        <td align="right" valign="top" background="${contextPath}/imcms/lang/images/admin/imcms_admin_shadow_right.gif">
            <img src="${contextPath}/imcms/lang/images/admin/imcms_admin_shadow_right_top.gif" width="12" height="12"
                 alt="" border="0"></td>
    </tr>
    <tr>
        <td colspan="2">
            <table border="0" cellspacing="0" cellpadding="0" width="100%">
                <tr>
                    <td background="${contextPath}/imcms/lang/images/admin/imcms_admin_shadow_bottom.gif">
                        <img src="${contextPath}/imcms/lang/images/admin/imcms_admin_shadow_bottom_left.gif"
                             width="12" height="12" alt="" border="0"></td>
                    <td background="${contextPath}/imcms/lang/images/admin/imcms_admin_shadow_bottom.gif"
                        align="right">
                        <img src="${contextPath}/imcms/lang/images/admin/imcms_admin_shadow_bottom_right.gif"
                             width="12" height="12" alt="" border="0"></td>
                </tr>
            </table>
        </td>
    </tr>
</table>

</body>
</html>
