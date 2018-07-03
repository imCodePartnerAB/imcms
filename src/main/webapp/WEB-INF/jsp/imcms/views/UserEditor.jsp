${"<!--"}
<%@ page trimDirectiveWhitespaces="true" %>
${"-->"}
<%@ page import="imcode.server.user.PhoneNumberType" %>
<%@ page import="imcode.util.DateConstants" %>
<%@ page contentType="text/html; charset=UTF-8" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags/imcms/ui" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="imcms" uri="imcms" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
<%--@elvariable id="editedUser" type="imcode.server.user.UserDomainObject"--%>
<%--@elvariable id="loggedOnUser" type="imcode.server.user.UserDomainObject"--%>
<%--@elvariable id="errorMessage" type="com.imcode.imcms.util.l10n.LocalizedMessage"--%>

<html>
<head>
    <title><fmt:message key="templates/sv/AdminUserResp.htm/1"/></title>
    <link rel="stylesheet" type="text/css" href="${contextPath}/imcms/css/imcms_admin.css">
    <link rel="stylesheet" type="text/css" href="${contextPath}/css/imcms-imports_files.css">
    <link rel="stylesheet" type="text/css" href="${contextPath}/css/imcms-edit-user-page.css">
    <imcms:ifAdmin>
        <script>
            <jsp:include page="/js/imcms/imcms_config.js.jsp"/>
        </script>
        <script src="${contextPath}/js/imcms/imcms_main.js" data-main="${contextPath}/js/imcms/new_admin/userEditor.js"
                data-name="imcms"></script>
    </imcms:ifAdmin>
</head>
<body>
<div class="imcms-info-page imcms-info-page__edit-user">

    <input type="hidden" id="must-fill-mandatory-fields-text"
           value="<fmt:message key="templates/sv/AdminUserResp.htm/2/1"/>"/>
    <input type="hidden" id="pass-verification-failed-text"
           value="<fmt:message key="templates/sv/AdminUserResp.htm/2/2"/>"/>

    <div class="imcms-info-head">
        <a href="https://www.imcms.net/" class="imcms-info__logo"></a>
        <div class="imcms-title imcms-head__title"><fmt:message key="global/imcms_administration"/></div>
    </div>
    <div class="imcms-info-body">
        <div class="imcms-field">
            <a href="${contextPath}/" class="imcms-button imcms-button--neutral imcms-info-body__button"><fmt:message
                    key="templates/sv/AdminUserResp.htm/2001"/></a>
        </div>
        <c:if test="${errorMessage ne null}">
            <div class="imcms-field">
                <div class="imcms-error-msg imcms-login__error-msg">${errorMessage.toLocalizedString(pageContext.request)}</div>
            </div>
        </c:if>
        <form id="user-edit-form" method="post" action="${contextPath}/api/user/${editedUser eq null?'create':'edit'}">
            <div class="imcms-field">
                <div class="imcms-title"><fmt:message key="templates/sv/AdminUserResp.htm/5/1"/></div>
            </div>
            <div><fmt:message key="templates/sv/AdminUserResp.htm/6"/></div>

            <div class="imcms-field">
                <div class="imcms-text-box">
                    <label for="login-name" class="imcms-label imcms-text-box__label"><fmt:message
                            key="templates/sv/AdminUserResp.htm/8"/></label>
                    <input id="login-name" type="text" name="login" class="imcms-input imcms-text-box__input"
                           maxlength="50" value="<c:out value='${editedUser.loginName}'/>">
                </div>
            </div>
            <div class="imcms-field">
                <div class="imcms-text-box">
                    <label for="password1" class="imcms-label imcms-text-box__label"><fmt:message
                            key="templates/sv/AdminUserResp.htm/10"/></label>
                    <input id="password1" type="password" name="password" class="imcms-input imcms-text-box__input"
                           maxlength="50" placeholder="<fmt:message key="templates/sv/AdminUserResp.htm/11"/>">
                </div>
            </div>
            <div class="imcms-field">
                <div class="imcms-text-box">
                    <label for="password2" class="imcms-label imcms-text-box__label"><fmt:message
                            key="templates/sv/AdminUserResp.htm/1001"/></label>
                    <input id="password2" type="password" name="password2" class="imcms-input imcms-text-box__input"
                           maxlength="50">
                </div>
            </div>
            <div class="imcms-field">
                <div class="imcms-text-box">
                    <label for="first-name" class="imcms-label imcms-text-box__label"><fmt:message
                            key="templates/sv/AdminUserResp.htm/14"/></label>
                    <input id="first-name" class="imcms-input imcms-text-box__input" type="text" name="firstName"
                           maxlength="50" value="<c:out value='${editedUser.firstName}'/>">
                </div>
            </div>
            <div class="imcms-field">
                <div class="imcms-text-box">
                    <label for="last-name" class="imcms-label imcms-text-box__label"><fmt:message
                            key="templates/sv/AdminUserResp.htm/16"/></label>
                    <input id="last-name" class="imcms-input imcms-text-box__input" type="text" name="lastName"
                           maxlength="50" value="<c:out value='${editedUser.lastName}'/>">
                </div>
            </div>
            <div class="imcms-field">
                <div class="imcms-text-box">
                    <label for="title" class="imcms-label imcms-text-box__label"><fmt:message
                            key="templates/sv/AdminUserResp.htm/18"/></label>
                    <input id="title" class="imcms-input imcms-text-box__input" type="text" name="title"
                           maxlength="50" value="<c:out value='${editedUser.title}'/>">
                </div>
            </div>
            <div class="imcms-field">
                <div class="imcms-text-box">
                    <label for="company" class="imcms-label imcms-text-box__label"><fmt:message
                            key="templates/sv/AdminUserResp.htm/20"/></label>
                    <input id="company" class="imcms-input imcms-text-box__input" type="text" name="company"
                           maxlength="50" value="<c:out value='${editedUser.company}'/>">
                </div>
            </div>
            <div class="imcms-field">
                <div class="imcms-text-box">
                    <label for="address" class="imcms-label imcms-text-box__label"><fmt:message
                            key="templates/sv/AdminUserResp.htm/22"/></label>
                    <input id="address" class="imcms-input imcms-text-box__input" type="text" name="address"
                           maxlength="50" value="<c:out value='${editedUser.address}'/>">
                </div>
            </div>
            <div class="imcms-field">
                <div class="imcms-text-box">
                    <label for="zip" class="imcms-label imcms-text-box__label"><fmt:message
                            key="templates/sv/AdminUserResp.htm/24"/></label>
                    <input id="zip" class="imcms-input imcms-text-box__input" type="text" name="zip"
                           maxlength="50" value="<c:out value='${editedUser.zip}'/>">
                </div>
            </div>
            <div class="imcms-field">
                <div class="imcms-text-box">
                    <label for="city" class="imcms-label imcms-text-box__label"><fmt:message
                            key="templates/sv/AdminUserResp.htm/25"/></label>
                    <input id="city" class="imcms-input imcms-text-box__input" type="text" name="city"
                           maxlength="50" value="<c:out value='${editedUser.city}'/>">
                </div>
            </div>
            <div class="imcms-field">
                <div class="imcms-text-box">
                    <label for="province" class="imcms-label imcms-text-box__label"><fmt:message
                            key="templates/sv/AdminUserResp.htm/27"/></label>
                    <input id="province" class="imcms-input imcms-text-box__input" type="text" name="province"
                           maxlength="50" value="<c:out value='${editedUser.province}'/>">
                </div>
            </div>
            <div class="imcms-field">
                <div class="imcms-text-box">
                    <label for="country" class="imcms-label imcms-text-box__label"><fmt:message
                            key="templates/sv/AdminUserResp.htm/29"/></label>
                    <input id="country" class="imcms-input imcms-text-box__input" type="text" name="country"
                           maxlength="50" value="<c:out value='${editedUser.country}'/>">
                </div>
            </div>

            <div id="languages-select-container" data-text="<fmt:message key="templates/sv/AdminUserResp.htm/30"/>"
                 class="imcms-field"><%-- content is set via js --%></div>

            <div class="imcms-field imcms-field--phone">
                <div class="imcms-text-box imcms-text-box--phone-box">
                    <label for="phone" class="imcms-label imcms-text-box__label"><fmt:message
                            key="templates/sv/AdminUserResp.htm/32"/></label>
                    <div id="phone-type-select" class="imcms-select imcms-select--phone-type">
                        <input id="phone-type-selected" type="hidden" value="<%=PhoneNumberType.OTHER.getId()%>">
                        <div class="imcms-drop-down-list imcms-select__drop-down-list">
                            <div class="imcms-drop-down-list__select-item">
                                <span class="imcms-drop-down-list__select-item-value"><%=PhoneNumberType.OTHER.getName().toLocalizedString(request)%></span>
                                <button class="imcms-button imcms-button--drop-down imcms-drop-down-list__button"
                                        type="button"></button>
                            </div>
                            <div class="imcms-drop-down-list__items">
                                <c:forEach var="phoneType" items="<%=PhoneNumberType.getAllPhoneNumberTypes()%>">
                                    <div class="imcms-drop-down-list__item"
                                         data-value="${phoneType.id}">${phoneType.name.toLocalizedString(pageContext.request)}</div>
                                </c:forEach>
                            </div>
                        </div>
                    </div>
                    <input id="phone" class="imcms-input imcms-text-box__input imcms-input--phone" type="text"
                           maxlength="50">
                    <button class="imcms-button imcms-button--positive imcms-button--add-phone"
                            id="button-add-phone"><fmt:message key="templates/sv/AdminUserResp.htm/2004"/></button>
                </div>

                <c:forEach var="phoneNumber" items="${editedUser.phoneNumbers}">
                    <div class="imcms-text-box imcms-text-box--phone-box imcms-text-box--existing-phone-box">
                        <label for="phone" class="imcms-label imcms-text-box__label"><fmt:message
                                key="templates/sv/AdminUserResp.htm/32"/></label>
                        <div class="imcms-select imcms-select--phone-type" disabled="disabled">
                            <input type="hidden" name="userPhoneNumberType" value="${phoneNumber.type.id}">
                            <div class="imcms-drop-down-list imcms-select__drop-down-list">
                                <div class="imcms-drop-down-list__select-item">
                                    <span class="imcms-drop-down-list__select-item-value">${phoneNumber.type.name.toLocalizedString(pageContext.request)}</span>
                                    <button class="imcms-button imcms-button--drop-down imcms-drop-down-list__button"
                                            type="button"></button>
                                </div>
                                <div class="imcms-drop-down-list__items"><c:forEach var="phoneType"
                                                                                    items="<%=PhoneNumberType.getAllPhoneNumberTypes()%>">
                                    <div class="imcms-drop-down-list__item"
                                         data-value="${phoneType.id}">${phoneType.name.toLocalizedString(pageContext.request)}</div>
                                </c:forEach></div>
                            </div>
                        </div>
                        <input class="imcms-input imcms-text-box__input imcms-input--phone" type="text" maxlength="50"
                               name="userPhoneNumber" disabled="disabled" value="${phoneNumber.number}">

                        <button class="imcms-button imcms-button--save" style="display: none;"
                                type="button"><fmt:message key="templates/sv/AdminUserResp.htm/2007"/></button>
                        <div class="imcms-control imcms-control--remove"></div>
                        <div class="imcms-control imcms-control--edit"></div>
                    </div>
                </c:forEach>
            </div>

            <div class="imcms-field">
                <div class="imcms-text-box">
                    <label for="email" class="imcms-label imcms-text-box__label"><fmt:message
                            key="templates/sv/AdminUserResp.htm/36"/></label>
                    <input id="email" class="imcms-input imcms-text-box__input" type="text" name="email"
                           maxlength="50" value="<c:out value='${editedUser.emailAddress}'/>">
                </div>
            </div>

            <div class="imcms-field">
                <div class="imcms-text-box">
                    <label for="activated" class="imcms-label imcms-text-box__label"><fmt:message
                            key="templates/sv/AdminUserResp_superadmin_part.htm/2"/></label>
                    <input id="activated" type="checkbox" name="active"
                           value="1"${empty editedUser or editedUser.active ? 'checked' : ''}>
                    <c:if test="${editedUser.createDate ne null}">
                        &nbsp; <fmt:message key="templates/sv/AdminUserResp_superadmin_part.htm/12"/>
                        &nbsp; <fmt:formatDate value="${editedUser.createDate}"
                                               pattern="<%=DateConstants.DATETIME_FORMAT_STRING%>"/>
                    </c:if>
                </div>
            </div>

            <c:if test="${editedUser eq null or loggedOnUser.canEditRolesFor(editedUser)}">
                <div class="imcms-field">
                    <div class="imcms-title"><fmt:message
                            key="templates/sv/AdminUserResp_superadmin_part.htm/3/1"/></div>
                </div>
                <div class="imcms-field">
                    <div class="imcms-checkboxes imcms-field__checkboxes">
                        <div class="imcms-title imcms-checkboxes__title"><fmt:message
                                key="templates/sv/AdminUserResp_superadmin_part.htm/1001"/></div>
                        <span><fmt:message key="templates/sv/AdminUserResp_superadmin_part.htm/10"/></span>
                        <c:forEach var="role" items="${imcms:getUserRoles(editedUser)}">
                            <div class="imcms-checkbox imcms-checkboxes__checkbox">
                                <input type="checkbox" name="roleIds" id="role-${role.id}" value="${role.id}"
                                       class="imcms-checkbox__checkbox"${role.checked ? ' checked="checked"':''}>
                                <label for="role-${role.id}"
                                       class="imcms-label imcms-checkbox__label">${role.name}</label>
                            </div>
                        </c:forEach>
                    </div>
                    <c:if test="${loggedOnUser.superAdmin and editedUser.superAdmin}">
                        <div class="imcms-checkboxes imcms-field__checkboxes">
                            <div class="imcms-title imcms-checkboxes__title"><fmt:message
                                    key="templates/sv/AdminUserResp_superadmin_part.htm/8"/></div>
                            <span><fmt:message key="templates/sv/AdminUserResp_superadmin_part.htm/11"/></span>
                            <c:forEach var="role" items="${imcms:getUserAdministratedRoles(editedUser)}">
                                <div class="imcms-checkbox imcms-checkboxes__checkbox">
                                    <input type="checkbox" name="userAdminRoleIds" id="admin-role-${role.id}"
                                           value="${role.id}"
                                           class="imcms-checkbox__checkbox"${role.checked ? ' checked="checked"':''}>
                                    <label for="admin-role-${role.id}"
                                           class="imcms-label imcms-checkbox__label">${role.name}</label>
                                </div>
                            </c:forEach>
                        </div>
                    </c:if>
                </div>
            </c:if>
            <div class="imcms-info-footer imcms-info-footer__user-edit">
                <button id="edit-user-submit-button" type="submit"
                        class="imcms-button imcms-button--save imcms-info-footer__button"><fmt:message
                        key="templates/sv/AdminUserResp.htm/2007"/></button>
                <button type="submit" class="imcms-button imcms-button--positive imcms-info-footer__button"><fmt:message
                        key="templates/sv/AdminUserResp.htm/2008"/></button>
                <button type="submit" class="imcms-button imcms-button--negative imcms-info-footer__button"><fmt:message
                        key="templates/sv/AdminUserResp.htm/2009"/></button>
            </div>
        </form>
    </div>
</div>

</body>
</html>
