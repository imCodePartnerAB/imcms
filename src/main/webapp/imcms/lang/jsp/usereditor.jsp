<%@ page import="com.imcode.imcms.flow.OkCancelPage" %>
<%@ page import="com.imcode.imcms.servlet.superadmin.UserEditorPage" %>
<%@ page import="imcode.util.DateConstants" %>
<%@ page contentType="text/html; charset=UTF-8" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags/imcms/ui" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<html>
<head>
    <title><fmt:message key="templates/sv/AdminUserResp.htm/1"/></title>
    <link rel="stylesheet" type="text/css" href="${contextPath}/imcms/css/imcms_admin.css">
    <script src="${contextPath}/js/imcms/imcms_admin.js" type="text/javascript"></script>
    <script language="javascript">
        <!--
        function evalPrepareAdd() {
            // Lets check that those fields which are mandatory
            var valFieldsOk = true;
            if (document.forms[0].login_name.value === "") valFieldsOk = false;

            if (!valFieldsOk) {
                alert("<fmt:message key="templates/sv/AdminUserResp.htm/2/1"/>");
                return false
            }

            if (document.forms[0].password1.value !== document.forms[0].password2.value) {
                document.forms[0].password1.value = "";
                document.forms[0].password2.value = "";
                document.forms[0].password1.focus();
                alert("<fmt:message key="templates/sv/AdminUserResp.htm/2/2"/>");
                return false
            }

            return true
        }

        function activateUseradmin_roles() {
            if (document.forms[0].<%= UserEditorPage.REQUEST_PARAMETER__USER_ADMIN_ROLE_IDS %>) {
                var list = document.forms[0].<%= UserEditorPage.REQUEST_PARAMETER__ROLE_IDS %>;
                document.forms[0].<%= UserEditorPage.REQUEST_PARAMETER__USER_ADMIN_ROLE_IDS %>.disabled = true;
                for (i = 0; i < list.length; i++) {
                    if (list.options[i].text === "Useradmin" && list.options[i].selected) {
                        document.forms[0].<%= UserEditorPage.REQUEST_PARAMETER__USER_ADMIN_ROLE_IDS %>.disabled = false;
                    }
                }
            }
        }

        //-->
    </script>

</head>
<body bgcolor="#FFFFFF"
      onLoad="focusField(0,'<%= UserEditorPage.REQUEST_PARAMETER__LOGIN_NAME %>'); activateUseradmin_roles(); return true">


<ui:imcms_gui_outer_start/>
<c:set var="heading">
    <fmt:message key="global/imcms_administration"/>
</c:set>
<ui:imcms_gui_head heading="${heading}"/>

<form method="post" action="${contextPath}/servlet/PageDispatcher">
    ${userEditorPage.htmlHidden(pageContext.request)}
    <table border="0" cellspacing="0" cellpadding="0">
        <tr>
            <td><input type="submit" class="imcmsFormBtn" name="<%= OkCancelPage.REQUEST_PARAMETER__CANCEL %>"
                       value="<fmt:message key="templates/sv/AdminUserResp.htm/2001"/>"></td>
            <td>&nbsp;</td>
            <td><input type="button" value="<fmt:message key="templates/sv/AdminUserResp.htm/2002"/>"
                       title="<fmt:message key="templates/sv/AdminUserResp.htm/2003"/>" class="imcmsFormBtn"
                       onClick="openHelpW('UserEdit')"></td>
        </tr>
    </table>
    <ui:imcms_gui_mid/>

    <table border="0" cellspacing="0" cellpadding="0" width="660" align="center">
        <tr>
            <td colspan="2">
                <c:set var="heading">
                    <fmt:message key="templates/sv/AdminUserResp.htm/5/1"/>
                </c:set>
                <ui:imcms_gui_heading heading="${heading}"/>
            </td>
        </tr>
        <tr>
            <td colspan="2" class="imcmsAdmText">
                <fmt:message key="templates/sv/AdminUserResp.htm/6"/></td>
        </tr>
        <tr>
            <td colspan="2"><ui:imcms_gui_hr wantedcolor="cccccc"/></td>
        </tr>
        <tr>
            <td class="imcmsAdmText"><fmt:message key="templates/sv/AdminUserResp.htm/8"/></td>
            <td><input type="text" name="<%= UserEditorPage.REQUEST_PARAMETER__LOGIN_NAME %>" size="25" maxlength="50"
                       value="<c:out value='${editedUser.loginName}'/>"></td>
        </tr>
        <tr>
            <td class="imcmsAdmText" nowrap><fmt:message key="templates/sv/AdminUserResp.htm/10"/> <span
                    class="imcmsAdmDim"><fmt:message key="templates/sv/AdminUserResp.htm/11"/></span> &nbsp;
            </td>
            <td>
                <table border="0" cellspacing="0" cellpadding="0">
                    <tr>
                        <td><input type="password" name="<%= UserEditorPage.REQUEST_PARAMETER__PASSWORD1 %>" size="16"
                                   maxlength="15" value=""></td>
                        <td class="imcmsAdmText" nowrap>&nbsp; <fmt:message key="templates/sv/AdminUserResp.htm/1001"/>
                            &nbsp;
                        </td>
                        <td><input type="password" name="<%= UserEditorPage.REQUEST_PARAMETER__PASSWORD2 %>" size="16"
                                   maxlength="15" value=""></td>
                    </tr>
                </table>
            </td>
        </tr>
        <tr>
            <td class="imcmsAdmText"><fmt:message key="templates/sv/AdminUserResp.htm/14"/></td>
            <td><input type="text" name="<%= UserEditorPage.REQUEST_PARAMETER__FIRST_NAME %>" size="25" maxlength="25"
                       value="<c:out value='${editedUser.firstName}'/>"></td>
        </tr>
        <tr>
            <td class="imcmsAdmText"><fmt:message key="templates/sv/AdminUserResp.htm/16"/></td>
            <td><input type="text" name="<%= UserEditorPage.REQUEST_PARAMETER__LAST_NAME %>" size="25" maxlength="30"
                       value="<c:out value='${editedUser.lastName}'/>"></td>
        </tr>
        <tr>
            <td class="imcmsAdmText"><fmt:message key="templates/sv/AdminUserResp.htm/18"/></td>
            <td><input type="text" name="<%= UserEditorPage.REQUEST_PARAMETER__TITLE %>" size="25" maxlength="30"
                       value="<c:out value='${editedUser.title}'/>"></td>
        </tr>
        <tr>
            <td class="imcmsAdmText"><fmt:message key="templates/sv/AdminUserResp.htm/20"/></td>
            <td><input type="text" name="<%= UserEditorPage.REQUEST_PARAMETER__COMPANY %>" size="25" maxlength="30"
                       value="<c:out value='${editedUser.company}'/>"></td>
        </tr>
        <tr>
            <td class="imcmsAdmText"><fmt:message key="templates/sv/AdminUserResp.htm/22"/></td>
            <td><input type="text" name="<%= UserEditorPage.REQUEST_PARAMETER__ADDRESS %>" size="25" maxlength="30"
                       value="<c:out value='${editedUser.address}'/>"></td>
        </tr>
        <tr>
            <td class="imcmsAdmText"><fmt:message key="templates/sv/AdminUserResp.htm/24"/></td>
            <td>
                <table border="0" cellspacing="0" cellpadding="0">
                    <tr>
                        <td><input type="text" name="<%= UserEditorPage.REQUEST_PARAMETER__ZIP %>" size="7"
                                   maxlength="30" value="<c:out value='${editedUser.zip}'/>"></td>
                        <td>&nbsp;</td>
                        <td><input type="text" name="<%= UserEditorPage.REQUEST_PARAMETER__CITY %>" size="25"
                                   maxlength="30" value="<c:out value='${editedUser.city}'/>">
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
        <tr>
            <td class="imcmsAdmText"><fmt:message key="templates/sv/AdminUserResp.htm/27"/></td>
            <td><input type="text" name="<%= UserEditorPage.REQUEST_PARAMETER__DISTRICT %>" size="25" maxlength="30"
                       value="<c:out value='${editedUser.province}'/>"></td>
        </tr>
        <tr>
            <td class="imcmsAdmText"><fmt:message key="templates/sv/AdminUserResp.htm/29"/></td>
            <td><input type="text" name="<%= UserEditorPage.REQUEST_PARAMETER__COUNTRY %>" size="25" maxlength="30"
                       value="<c:out value='${editedUser.country}'/>"></td>
        </tr>
        <tr>
            <td class="imcmsAdmText"><fmt:message key="templates/sv/AdminUserResp.htm/30"/></td>
            <td><select
                    name="<%= UserEditorPage.REQUEST_PARAMETER__LANGUAGE %>">${userEditorPage.createLanguagesHtmlOptionList(loggedOnUser, editedUser)}
            </select></td>
        </tr>
        <tr>
            <td colspan="2"><ui:imcms_gui_hr wantedcolor="cccccc"/></td>
        </tr>
        <tr>
            <td class="imcmsAdmText"><fmt:message key="templates/sv/AdminUserResp.htm/32"/></td>
            <td>
                <table border="0" cellspacing="0" cellpadding="0">
                    <tr>
                        <td>
                            <select name="<%= UserEditorPage.REQUEST_PARAMETER__PHONE_NUMBER_TYPE_ID %>" size="1">
                                ${userEditorPage.createPhoneTypesHtmlOptionList(loggedOnUser, userEditorPage.currentPhoneNumber.type)}
                            </select></td>
                        <td>&nbsp;</td>
                        <td><input type="text" name="<%= UserEditorPage.REQUEST_PARAMETER__EDITED_PHONE_NUMBER %>"
                                   size="16" maxlength="25" value="${userEditorPage.currentPhoneNumber}"></td>
                        <td>&nbsp;</td>
                        <c:if test="${empty param['addUser']}">
                            <td><input type="submit" class="imcmsFormBtnSmall"
                                       value="<fmt:message key="templates/sv/AdminUserResp.htm/2004"/>"
                                       name="<%= UserEditorPage.REQUEST_PARAMETER__ADD_PHONE_NUMBER %>"></td>
                        </c:if>
                    </tr>
                </table>
            </td>
        </tr>
        <c:if test="${empty param['addUser']}">
            <tr>
                <td class="imcmsAdmText">&nbsp;</td>
                <td>
                    <table border="0" cellspacing="0" cellpadding="0">
                        <tr>
                            <td>
                                <select size="1" name="<%= UserEditorPage.REQUEST_PARAMETER__SELECTED_PHONE_NUMBER %>">
                                        ${userEditorPage.getUserPhoneNumbersHtmlOptionList(pageContext.request)}
                                </select></td>
                            <td>&nbsp;</td>
                            <td><input type="submit" class="imcmsFormBtnSmall"
                                       name="<%= UserEditorPage.REQUEST_PARAMETER__EDIT_PHONE_NUMBER %>"
                                       value="<fmt:message key="templates/sv/AdminUserResp.htm/2005"/>"></td>
                            <td>&nbsp;</td>
                            <td><input type="submit" class="imcmsFormBtnSmall"
                                       name="<%= UserEditorPage.REQUEST_PARAMETER__REMOVE_PHONE_NUMBER %>"
                                       value="<fmt:message key="templates/sv/AdminUserResp.htm/2006"/>"></td>
                        </tr>
                    </table>
                </td>
            </tr>
        </c:if>

        <tr>
            <td class="imcmsAdmText"><fmt:message key="templates/sv/AdminUserResp.htm/36"/></td>
            <td><input type="text" name="<%= UserEditorPage.REQUEST_PARAMETER__EMAIL %>" size="50" maxlength="50"
                       value="<c:out value='${editedUser.emailAddress}'/>"></td>
        </tr>
        <tr>
            <td colspan="2"><ui:imcms_gui_hr wantedcolor="blue"/></td>
        </tr>
        <tr>
            <td class="imcmsAdmText"><fmt:message key="templates/sv/AdminUserResp_superadmin_part.htm/2"/></td>
            <td>
                <table border="0" cellspacing="0" cellpadding="0">
                    <tr>
                        <td><input type="checkbox" name="active" value="1"${editedUser.active ? 'checked' : ''}></td>
                        <td class="imcmsAdmText" nowrap>&nbsp;
                            <c:if test="${editedUser.createDate ne null}">
                                &nbsp; <fmt:message key="templates/sv/AdminUserResp_superadmin_part.htm/12"/>
                                &nbsp; <fmt:formatDate value="${editedUser.createdDate}"
                                                       pattern="<%=DateConstants.DATETIME_FORMAT_STRING%>"/>
                            </c:if>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
        <c:if test="${loggedOnUser.canEditRolesFor(userEditorPage.getUneditedUser())}">
        <tr>
            <td colspan="2">&nbsp;<br>
                <c:set var="heading">
                    <fmt:message key="templates/sv/AdminUserResp_superadmin_part.htm/3/1"/>
                </c:set>
                <ui:imcms_gui_heading heading="${heading}"/>
            </td>
        </tr>
        <tr valign="top">
            <td class="imcmsAdmText" nowrap>
                <fmt:message key="templates/sv/AdminUserResp_superadmin_part.htm/1001"/> &nbsp;
            </td>
            <td>
                <table border="0" cellspacing="0" cellpadding="0">
                    <tr valign="top">
                        <td>
                            <select name="<%= UserEditorPage.REQUEST_PARAMETER__ROLE_IDS %>" size="5"
                                    onchange="activateUseradmin_roles(); return true;"
                                    multiple>${userEditorPage.createRolesHtmlOptionList(pageContext.request)}</select>
                        </td>
                        <c:if test="${loggedOnUser.isSuperAdmin()}">
                            <td>&nbsp;</td>
                            <td class="imcmsAdmText" nowrap><fmt:message
                                    key="templates/sv/AdminUserResp_superadmin_part.htm/8"/></td>
                            <td>&nbsp;</td>
                            <td>
                                <select name="<%= UserEditorPage.REQUEST_PARAMETER__USER_ADMIN_ROLE_IDS %>" size="5"
                                        multiple>${userEditorPage.createUserAdminRolesHtmlOptionList()}</select>
                            </td>
                        </c:if>
                    </tr>
                    <tr valign="top">
                        <td class="imcmsAdmDim"><fmt:message
                                key="templates/sv/AdminUserResp_superadmin_part.htm/10"/></td>
                        <c:if test="${loggedOnUser.isSuperAdmin()}">
                            <td colspan="3">&nbsp;</td>
                            <td class="imcmsAdmDim"><fmt:message
                                    key="templates/sv/AdminUserResp_superadmin_part.htm/11"/></td>
                        </c:if>
                    </tr>
                </table>
            </td>
        </tr>
        </c:if>
        <tr>
            <td colspan="2"><ui:imcms_gui_hr wantedcolor="blue"/></td>
        </tr>
        <tr>
            <td colspan="2">
                <table border="0" cellspacing="0" cellpadding="0" width="656">
                    <tr>
                        <td class="imcmsAdmComment"><fmt:message key="templates/sv/AdminUserResp.htm/40"/></td>
                        <td align="right">
                            <table border="0" cellspacing="0" cellpadding="0">
                                <tr>
                                    <c:if test="${errorMessage ne null}">
                                        <td><span
                                                class="error">${errorMessage.toLocalizedString(pageContext.request)}</span>
                                        </td>
                                        <td>&nbsp;</td>
                                    </c:if>
                                    <td><input type="submit" class="imcmsFormBtn"
                                               name="<%= OkCancelPage.REQUEST_PARAMETER__OK %>"
                                               value="<fmt:message key="templates/sv/AdminUserResp.htm/2007"/>"
                                               onClick="if( !evalPrepareAdd() ) return false;"></td>
                                    <td>&nbsp;</td>
                                    <td><input type="submit" class="imcmsFormBtn"
                                               value="<fmt:message key="templates/sv/AdminUserResp.htm/2008"/>"></td>
                                    <td>&nbsp;</td>
                                    <td><input type="submit" class="imcmsFormBtn"
                                               name="<%= OkCancelPage.REQUEST_PARAMETER__CANCEL %>"
                                               value="<fmt:message key="templates/sv/AdminUserResp.htm/2009"/>"></td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
    </table>
</form>
<ui:imcms_gui_bottom/>
<ui:imcms_gui_outer_end/>
</body>
</html>
