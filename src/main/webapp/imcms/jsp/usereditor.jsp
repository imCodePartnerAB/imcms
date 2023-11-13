<%@ page trimDirectiveWhitespaces="true" %>
<%@ page import="com.imcode.imcms.flow.OkCancelPage" %>
<%@ page import="imcode.server.user.PhoneNumberType" %>
<%@ page import="imcode.util.DateConstants" %>
<%@ page contentType="text/html; charset=UTF-8" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags/imcms/ui" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="imcms" uri="imcms" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
<%--@elvariable id="editedUser" type="imcode.server.user.UserDomainObject"--%>
<%--@elvariable id="uneditedUser" type="imcode.server.user.UserDomainObject"--%>
<%--@elvariable id="loggedOnUser" type="imcode.server.user.UserDomainObject"--%>
<%--@elvariable id="userEditorPage" type="com.imcode.imcms.servlet.superadmin.UserEditorPage"--%>
<%--@elvariable id="errorMessage" type="com.imcode.imcms.util.l10n.LocalizedMessage"--%>

<fmt:setLocale value="${userLanguage}"/>
<fmt:setBundle basename="imcms" var="resource_property"/>

<html>
<head>
    <title><fmt:message key="templates/sv/AdminUserResp.htm/1" bundle="${resource_property}"/></title>
    <script src="${contextPath}/dist/imcms_admin.js"></script>
    <script><jsp:include page="/imcms/js/imcms_config.js.jsp"/></script>
    <script src="${contextPath}/dist/userEditorOld.js"></script>
</head>
<body>
<div class="imcms-info-page imcms-info-page__edit-user">
    <input type="hidden" id="must-fill-mandatory-fields-text"
           value="<fmt:message key="templates/sv/AdminUserResp.htm/2/1" bundle="${resource_property}"/>"/>
    <input type="hidden" id="pass-verification-failed-text"
           value="<fmt:message key="templates/sv/AdminUserResp.htm/2/2" bundle="${resource_property}"/>"/>

    <div class="imcms-info-head">
        <a href="https://www.imcms.net/" class="imcms-info__logo"></a>
        <div class="imcms-title imcms-head__title"><fmt:message key="global/imcms_administration" bundle="${resource_property}"/></div>
    </div>
    <div class="imcms-info-body">
        <div class="imcms-field">
            <a href="${contextPath}/" class="imcms-button imcms-button--neutral imcms-info-body__button"><fmt:message
                    key="templates/sv/AdminUserResp.htm/2001" bundle="${resource_property}"/></a>
        </div>
        <c:if test="${errorMessage ne null}">
            <div class="imcms-field">
                <div class="imcms-error-msg imcms-login__error-msg">${errorMessage.toLocalizedString(pageContext.request)}</div>
            </div>
        </c:if>
        <form id="user-edit-form" method="post" action="${contextPath}/servlet/PageDispatcher">
            ${userEditorPage.htmlHidden(pageContext.request)}
            <div class="imcms-field">
                <div class="imcms-title"><fmt:message key="templates/sv/AdminUserResp.htm/5/1" bundle="${resource_property}"/></div>
            </div>
            <div><fmt:message key="templates/sv/AdminUserResp.htm/6" bundle="${resource_property}"/></div>

            <div class="imcms-field">
                <div class="imcms-text-box">
                    <label for="login-name" class="imcms-label imcms-text-box__label"><fmt:message
                            key="templates/sv/AdminUserResp.htm/8" bundle="${resource_property}"/></label>
                    <input id="login-name" type="text" name="login_name" class="imcms-input imcms-text-box__input"
                           maxlength="250" value="<c:out value='${editedUser.loginName}'/>">
                </div>
            </div>
            <div class="imcms-field">
                <div class="imcms-text-box">
                    <label for="password1" class="imcms-label imcms-text-box__label"><fmt:message
                            key="templates/sv/AdminUserResp.htm/10" bundle="${resource_property}"/></label>
                    <input id="password1" type="password" name="password1" class="imcms-input imcms-text-box__input"
                           maxlength="250" placeholder="<fmt:message key="templates/sv/AdminUserResp.htm/11" bundle="${resource_property}"/>">
                </div>
            </div>
            <div class="imcms-field">
                <div class="imcms-text-box">
                    <label for="password2" class="imcms-label imcms-text-box__label"><fmt:message
                            key="templates/sv/AdminUserResp.htm/1001" bundle="${resource_property}"/></label>
                    <input id="password2" type="password" name="password2" class="imcms-input imcms-text-box__input"
                           maxlength="250">
                </div>
            </div>
            <div class="imcms-field">
                <div class="imcms-text-box">
                    <label for="first-name" class="imcms-label imcms-text-box__label"><fmt:message
                            key="templates/sv/AdminUserResp.htm/14" bundle="${resource_property}"/></label>
                    <input id="first-name" class="imcms-input imcms-text-box__input" type="text" name="first_name"
                           maxlength="50" value="<c:out value='${editedUser.firstName}'/>">
                </div>
            </div>
            <div class="imcms-field">
                <div class="imcms-text-box">
                    <label for="last-name" class="imcms-label imcms-text-box__label"><fmt:message
                            key="templates/sv/AdminUserResp.htm/16" bundle="${resource_property}"/></label>
                    <input id="last-name" class="imcms-input imcms-text-box__input" type="text" name="last_name"
                           maxlength="50" value="<c:out value='${editedUser.lastName}'/>">
                </div>
            </div>
            <div class="imcms-field">
                <div class="imcms-text-box">
                    <label for="title" class="imcms-label imcms-text-box__label"><fmt:message
                            key="templates/sv/AdminUserResp.htm/18" bundle="${resource_property}"/></label>
                    <input id="title" class="imcms-input imcms-text-box__input" type="text" name="title"
                           maxlength="50" value="<c:out value='${editedUser.title}'/>">
                </div>
            </div>
            <div class="imcms-field">
                <div class="imcms-text-box">
                    <label for="company" class="imcms-label imcms-text-box__label"><fmt:message
                            key="templates/sv/AdminUserResp.htm/20" bundle="${resource_property}"/></label>
                    <input id="company" class="imcms-input imcms-text-box__input" type="text" name="company"
                           maxlength="50" value="<c:out value='${editedUser.company}'/>">
                </div>
            </div>
            <div class="imcms-field">
                <div class="imcms-text-box">
                    <label for="address" class="imcms-label imcms-text-box__label"><fmt:message
                            key="templates/sv/AdminUserResp.htm/22" bundle="${resource_property}"/></label>
                    <input id="address" class="imcms-input imcms-text-box__input" type="text" name="address"
                           maxlength="50" value="<c:out value='${editedUser.address}'/>">
                </div>
            </div>
            <div class="imcms-field">
                <div class="imcms-text-box">
                    <label for="zip" class="imcms-label imcms-text-box__label"><fmt:message
                            key="templates/sv/AdminUserResp.htm/24" bundle="${resource_property}"/></label>
                    <input id="zip" class="imcms-input imcms-text-box__input" type="text" name="zip"
                           maxlength="50" value="<c:out value='${editedUser.zip}'/>">
                </div>
            </div>
            <div class="imcms-field">
                <div class="imcms-text-box">
                    <label for="city" class="imcms-label imcms-text-box__label"><fmt:message
                            key="templates/sv/AdminUserResp.htm/25" bundle="${resource_property}"/></label>
                    <input id="city" class="imcms-input imcms-text-box__input" type="text" name="city"
                           maxlength="50" value="<c:out value='${editedUser.city}'/>">
                </div>
            </div>
            <div class="imcms-field">
                <div class="imcms-text-box">
                    <label for="province" class="imcms-label imcms-text-box__label"><fmt:message
                            key="templates/sv/AdminUserResp.htm/27" bundle="${resource_property}"/></label>
                    <input id="province" class="imcms-input imcms-text-box__input" type="text" name="county"
                           maxlength="50" value="<c:out value='${editedUser.province}'/>">
                </div>
            </div>
            <div class="imcms-field">
                <div class="imcms-text-box">
                    <label for="country" class="imcms-label imcms-text-box__label"><fmt:message
                            key="templates/sv/AdminUserResp.htm/29" bundle="${resource_property}"/></label>
                    <input id="country" class="imcms-input imcms-text-box__input" type="text" name="country"
                           maxlength="50" value="<c:out value='${editedUser.country}'/>">
                </div>
            </div>

            <input id="current-lang" type="hidden" value="${editedUser.language}">
            <div id="languages-select-container" data-text="<fmt:message key="templates/sv/AdminUserResp.htm/30" bundle="${resource_property}"/>"
                 class="imcms-field"><%-- content is set via js --%></div>

            <div class="imcms-field imcms-field--phone">
                <div class="imcms-text-box imcms-text-box--phone-box">
                    <label for="phone" class="imcms-label imcms-text-box__label"><fmt:message
                            key="templates/sv/AdminUserResp.htm/32" bundle="${resource_property}"/></label>
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
                            id="button-add-phone"><fmt:message key="templates/sv/AdminUserResp.htm/2004" bundle="${resource_property}"/></button>
                </div>

                <c:forEach var="phoneNumber" items="${editedUser.phoneNumbers}">
                    <div class="imcms-text-box imcms-text-box--phone-box imcms-text-box--existing-phone-box">
                        <label for="phone" class="imcms-label imcms-text-box__label"><fmt:message
                                key="templates/sv/AdminUserResp.htm/32" bundle="${resource_property}"/></label>
                        <div class="imcms-select imcms-select--phone-type" disabled="disabled">
                            <input type="hidden" name="user_phone_number_type" value="${phoneNumber.type.id}">
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
                               name="user_phone_number" disabled="disabled" value="${phoneNumber.number}">

                        <button class="imcms-button imcms-button--save" style="display: none;"
                                type="button"><fmt:message key="templates/sv/AdminUserResp.htm/2007"/></button>
                        <div class="imcms-phone-edit-buttons">
                            <div class="imcms-control imcms-control--edit"></div>
                            <div class="imcms-control imcms-control--remove"></div>
                        </div>
                    </div>
                </c:forEach>
            </div>

            <div class="imcms-field">
                <div class="imcms-text-box">
                    <label for="email" class="imcms-label imcms-text-box__label"><fmt:message
                            key="templates/sv/AdminUserResp.htm/36" bundle="${resource_property}"/></label>
                    <input id="email" class="imcms-input imcms-text-box__input" type="text" name="email"
                           maxlength="50" value="<c:out value='${editedUser.emailAddress}'/>">
                </div>
            </div>

            <c:if test="${editedUser.isSuperAdmin()}">
                <div class="imcms-field">
                    <div class="imcms-text-box">
                        <label for="ref" class="imcms-label imcms-text-box__label"><fmt:message
                                key="templates/sv/AdminUserResp.htm/38"/></label>
                        <input id="ref" class="imcms-input imcms-text-box__input" type="text" name="ref"
                                maxlength="50" value="<c:out value='${editedUser.ref}'/>">
                    </div>
                </div>

                <div class="imcms-field">
                    <div class="imcms-text-box">
                        <label for="activated" class="imcms-label imcms-text-box__label"><fmt:message
                                key="templates/sv/AdminUserResp_superadmin_part.htm/2" bundle="${resource_property}"/></label>
                        <input id="activated" type="checkbox" name="active" value="1"${editedUser.active ? 'checked' : ''}>
                        <c:if test="${editedUser.createDate ne null}">
                            &nbsp; <fmt:message key="templates/sv/AdminUserResp_superadmin_part.htm/12" bundle="${resource_property}"/>
                            &nbsp; <fmt:formatDate value="${editedUser.createDate}"
                                                   pattern="<%=DateConstants.DATETIME_FORMAT_STRING%>"/>
                        </c:if>
                    </div>
                </div>

                <div class="imcms-field">
                    <div class="imcms-text-box">
                        <label class="imcms-label imcms-text-box__label"><fmt:message
                                key="templates/sv/AdminUserResp_superadmin_part.htm/1001" bundle="${resource_property}"/></label>
                        <div class="imcms-roles-checkboxes">
                            <c:forEach var="role" items="${imcms:getUserRoles(editedUser)}">
                                <div class="imcms-checkbox imcms-checkboxes__checkbox">
                                    <input type="checkbox" name="roleIds" id="role-${role.id}" value="${role.id}"
                                           class="imcms-checkbox__checkbox"${role.checked ? ' checked="checked"':''}>
                                    <label for="role-${role.id}"
                                           class="imcms-label imcms-checkbox__label">${role.name}</label>
                                </div>
                            </c:forEach>
                        </div>
                    </div>
                </div>
            </c:if>
        </form>
        <div class="imcms-info-footer imcms-info-footer__user-edit">
            <button id="edit-user-cancel"
                    class="imcms-button imcms-button--negative imcms-info-footer__button"><fmt:message
                    key="templates/sv/AdminUserResp.htm/2009" bundle="${resource_property}"/></button>
            <button id="edit-user-reset"
                    class="imcms-button imcms-button--positive imcms-info-footer__button"><fmt:message
                    key="templates/sv/AdminUserResp.htm/2008" bundle="${resource_property}"/></button>
            <button id="edit-user-submit-button" type="submit" form="user-edit-form" name="<%= OkCancelPage.REQUEST_PARAMETER__OK %>"
                    class="imcms-button imcms-button--save imcms-info-footer__button"><fmt:message
                    key="templates/sv/AdminUserResp.htm/2007" bundle="${resource_property}"/></button>
        </div>
    </div>
</div>

</body>
</html>
