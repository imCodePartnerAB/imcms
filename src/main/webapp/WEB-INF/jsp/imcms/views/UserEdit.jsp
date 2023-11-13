${"<!--"}
<%@ page trimDirectiveWhitespaces="true" %>
${"-->"}
<%@ page import="com.imcode.imcms.model.PhoneTypes" %>
<%@ page import="imcode.util.DateConstants" %>
<%@ page contentType="text/html; charset=UTF-8" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags/imcms/ui" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="imcms" uri="imcms" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="C" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
<%--@elvariable id="editedUser" type="com.imcode.imcms.domain.dto.UserFormData"--%>
<%--@elvariable id="loggedOnUser" type="imcode.server.user.UserDomainObject"--%>
<%--@elvariable id="errorMessage" type="com.imcode.imcms.util.l10n.LocalizedMessage"--%>
<%--@elvariable id="isBlockedNow" type="boolean"--%>

<fmt:setLocale value="${userLanguage}"/>
<fmt:setBundle basename="imcms" var="resource_property"/>

<html>
<head>
    <title><fmt:message key="templates/sv/AdminUserResp.htm/1" bundle="${resource_property}"/></title>
        <script>
            <jsp:include page="/imcms/js/imcms_config.js.jsp"/>
        </script>
        <script src="${contextPath}/dist/userEditorNew.js"></script>
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
        <div class="imcms-control imcms-control--remove imcms-info-head__close"></div>
    </div>
    <div class="imcms-info-body">
        <div class="imcms-field">
            <a href="${contextPath}/" class="imcms-button imcms-button--neutral imcms-info-body__button"><fmt:message
                    key="templates/sv/AdminUserResp.htm/2001" bundle="${resource_property}"/></a>
        </div>
        <%--@elvariable id="errorMessages" type="java.util.List"--%>
        <c:forEach var="errorMessageKey" items="${errorMessages}">
            <div class="imcms-field">
                <div class="imcms-error-msg imcms-login__error-msg"><fmt:message key="${errorMessageKey}" bundle="${resource_property}"/></div>
            </div>
        </c:forEach>
        <form id="user-edit-form" method="post" action="${contextPath}/api/user/edit">
            <input type="hidden" name="id" value="${editedUser.id}">
            <div class="imcms-field">
                <div class="imcms-title"><fmt:message key="templates/sv/AdminUserResp.htm/5/1" bundle="${resource_property}"/></div>
            </div>
            <div><fmt:message key="templates/sv/AdminUserResp.htm/6" bundle="${resource_property}"/></div>

            <div class="imcms-field">
                <div class="imcms-text-box">
                    <label for="login-name" class="imcms-label imcms-text-box__label"><fmt:message
                            key="templates/sv/AdminUserResp.htm/8" bundle="${resource_property}"/></label>
                    <input id="login-name" type="text" name="login" class="imcms-input imcms-text-box__input"
                           maxlength="250" value="<c:out value='${editedUser.login}'/>">
                </div>
            </div>
            <div class="imcms-field">
                <div class="imcms-text-box">
                    <label for="password" class="imcms-label imcms-text-box__label"><fmt:message
                            key="templates/sv/AdminUserResp.htm/10" bundle="${resource_property}"/></label>
                    <input id="password" type="password" name="password" class="imcms-input imcms-text-box__input"
                           maxlength="250" placeholder="<fmt:message key="templates/sv/AdminUserResp.htm/11" bundle="${resource_property}"/>"
                           value="<c:out value='${editedUser.password}'/>">
                </div>
            </div>
            <div class="imcms-field">
                <div class="imcms-text-box">
                    <label for="password2" class="imcms-label imcms-text-box__label"><fmt:message
                            key="templates/sv/AdminUserResp.htm/1001" bundle="${resource_property}"/></label>
                    <input id="password2" type="password" name="password2" class="imcms-input imcms-text-box__input"
                           maxlength="250" value="${editedUser.password}">
                </div>
            </div>
            <div class="imcms-field">
                <div class="imcms-text-box">
                    <label for="first-name" class="imcms-label imcms-text-box__label"><fmt:message
                            key="templates/sv/AdminUserResp.htm/14" bundle="${resource_property}"/></label>
                    <input id="first-name" class="imcms-input imcms-text-box__input" type="text" name="firstName"
                           maxlength="50" value="<c:out value='${editedUser.firstName}'/>">
                </div>
            </div>
            <div class="imcms-field">
                <div class="imcms-text-box">
                    <label for="last-name" class="imcms-label imcms-text-box__label"><fmt:message
                            key="templates/sv/AdminUserResp.htm/16" bundle="${resource_property}"/></label>
                    <input id="last-name" class="imcms-input imcms-text-box__input" type="text" name="lastName"
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
                    <input id="province" class="imcms-input imcms-text-box__input" type="text" name="province"
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

            <input id="current-lang" type="hidden" value="${editedUser.langCode}">
            <div id="languages-select-container" data-text="<fmt:message key="templates/sv/AdminUserResp.htm/30" bundle="${resource_property}"/>"
                 class="imcms-field"><%-- content is set via js --%></div>

            <div class="imcms-field imcms-field--phone">
                <div class="imcms-text-box imcms-text-box--phone-box">
                    <label for="phone" class="imcms-label imcms-text-box__label"><fmt:message
                            key="templates/sv/AdminUserResp.htm/32" bundle="${resource_property}"/></label>
                    <div id="phone-type-select" class="imcms-select imcms-select--phone-type">
                        <input id="phone-type-selected" type="hidden" value="<%=PhoneTypes.OTHER.getId()%>">
                        <div class="imcms-drop-down-list imcms-select__drop-down-list">
                            <div class="imcms-drop-down-list__select-item">
                                <span class="imcms-drop-down-list__select-item-value"><fmt:message key="templates/sv/AdminUserResp.htm/32/0" bundle="${resource_property}"/></span>
                                <button class="imcms-button imcms-button--drop-down imcms-drop-down-list__button"
                                        type="button"></button>
                            </div>
                            <div class="imcms-drop-down-list__items">
                                <c:forEach var="phoneType" items="<%=PhoneTypes.values()%>">
                                    <div class="imcms-drop-down-list__item" data-value="${phoneType.id}">
		                                    <fmt:message key="templates/sv/AdminUserResp.htm/32/${phoneType.id}" bundle="${resource_property}"/>
                                    </div>
                                </c:forEach>
                            </div>
                        </div>
                    </div>
                    <input id="phone" class="imcms-input imcms-text-box__input imcms-input--phone" type="text"
                           maxlength="50">
                    <button class="imcms-button imcms-button--positive imcms-button--add-phone"
                            id="button-add-phone"><fmt:message key="templates/sv/AdminUserResp.htm/2004" bundle="${resource_property}"/></button>
                </div>

                <c:forEach var="phoneNumber" items="${editedUser.userPhoneNumber}" varStatus="i">
                    <div class="imcms-text-box imcms-text-box--phone-box imcms-text-box--existing-phone-box">
                        <label for="phone" class="imcms-label imcms-text-box__label"><fmt:message
                                key="templates/sv/AdminUserResp.htm/32" bundle="${resource_property}"/></label>
                        <div class="imcms-select imcms-select--phone-type" disabled="disabled">
                            <input type="hidden" name="userPhoneNumberType"
                                   value="${editedUser.userPhoneNumberType[i.index]}">
                            <div class="imcms-drop-down-list imcms-select__drop-down-list">
                                <div class="imcms-drop-down-list__select-item">
                                    <spring:eval var="userPhoneType"
                                                 expression="T(com.imcode.imcms.model.PhoneTypes).getPhoneTypeById(editedUser.userPhoneNumberType[i.index])"/>
                                    <span class="imcms-drop-down-list__select-item-value">${userPhoneType.name}</span>
                                    <button class="imcms-button imcms-button--drop-down imcms-drop-down-list__button"
                                            type="button"></button>
                                </div>
                                <div class="imcms-drop-down-list__items"><c:forEach var="phoneType"
                                                                                    items="<%=PhoneTypes.values()%>">
                                    <div class="imcms-drop-down-list__item"
                                         data-value="${phoneType.id}">${phoneType.name}</div>
                                </c:forEach></div>
                            </div>
                        </div>
                        <input class="imcms-input imcms-text-box__input imcms-input--phone" type="text" maxlength="50"
                               name="userPhoneNumber" disabled="disabled" value="${phoneNumber}">

                        <button class="imcms-button imcms-button--save" style="display: none;"
                                type="button"><fmt:message key="templates/sv/AdminUserResp.htm/2007" bundle="${resource_property}"/></button>
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
                           maxlength="50" value="<c:out value='${editedUser.email}'/>">
                </div>
            </div>

            <div class="imcms-field">
                <div class="imcms-text-box">
                    <label for="ref" class="imcms-label imcms-text-box__label"><fmt:message
                            key="templates/sv/AdminUserResp.htm/38" bundle="${resource_property}"/></label>
                    <input id="ref" class="imcms-input imcms-text-box__input" type="text" name="ref"
                           maxlength="50" value="<c:out value='${editedUser.ref}'/>">
                </div>
            </div>


            <div class="imcms-field">
                <div class="imcms-text-box">
                    <label class="imcms-label imcms-text-box__label"><fmt:message
                            key="webapp/imcms/lang/jsp/blocked_datetime" bundle="${resource_property}"/></label>
                    <c:choose>
                        <c:when test="${editedUser.blockedDate ne null and isBlockedNow}">
                            &nbsp; <fmt:message key="templates/sv/AdminUserResp_superadmin_part.htm/13" bundle="${resource_property}"/>
                            &nbsp; <fmt:formatDate value="${editedUser.blockedDate}"
                                                   pattern="<%=DateConstants.DATETIME_FORMAT_STRING%>"/>

                            &nbsp;&nbsp; <input id="flagControlBlocking" type="checkbox" name="flagOfBlocking" ${isBlockedNow ? ' checked="checked"':''}>

                            &nbsp;&nbsp;  <label for="flagControlBlocking" class="imcms-label imcms-text-box__label"><fmt:message
                                key="webapp/imcms/lang/jsp/unblock" bundle="${resource_property}"/></label>
                        </c:when>
                        <c:otherwise>
                            &nbsp; <fmt:message key="templates/sv/AdminUserResp_superadmin_part.htm/14" bundle="${resource_property}"/>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>

            <div class="imcms-field">
                <div class="imcms-text-box">
                    <label class="imcms-label imcms-text-box__label"><fmt:message
                            key="webapp/imcms/lang/jsp/attempts_loggedin" bundle="${resource_property}"/></label>
                    &nbsp; <fmt:message key="${editedUser.attempts}"/>
                    <input class="imcms-input imcms-text-box__input" type="hidden" name="attempts"
                           value="<c:out value='${editedUser.attempts}'/>">
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
			        <label for="twoFactoryAuthenticationEnabled" class="imcms-label imcms-text-box__label"><fmt:message
					        key="templates/sv/AdminUserResp_superadmin_part.htm/15"
					        bundle="${resource_property}"/></label>
			        <input id="twoFactoryAuthenticationEnabled" type="checkbox" name="twoFactoryAuthenticationEnabled"
			               value="1"${editedUser.twoFactoryAuthenticationEnabled ? 'checked' : ''}>
		        </div>
	        </div>

            <div class="imcms-field">
                <div class="imcms-text-box">
                    <label class="imcms-label imcms-text-box__label"><fmt:message
                            key="templates/sv/AdminUserResp_superadmin_part.htm/1001" bundle="${resource_property}"/></label>
                    <div class="imcms-roles-checkboxes">
                        <c:forEach var="role" items="${imcms:getNewUserRoles(editedUser)}">
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
        </form>
        <div class="imcms-info-footer imcms-info-footer__user-edit">
            <button id="edit-user-cancel" class="imcms-button imcms-button--negative imcms-info-footer__button">
                <fmt:message key="templates/sv/AdminUserResp.htm/2009" bundle="${resource_property}"/></button>
            <button id="edit-user-properties" class="imcms-button imcms-button--positive imcms-info-footer__button">
                <fmt:message key="templates/sv/AdminUserResp.htm/2010" bundle="${resource_property}"/></button>
            <button id="edit-user-reset" class="imcms-button imcms-button--positive imcms-info-footer__button">
                <fmt:message key="templates/sv/AdminUserResp.htm/2008" bundle="${resource_property}"/></button>
            <button id="edit-user-submit-button" type="submit" form="user-edit-form"
                    class="imcms-button imcms-button--save imcms-info-footer__button"><fmt:message
                    key="templates/sv/AdminUserResp.htm/2007" bundle="${resource_property}"/></button>
        </div>
    </div>
</div>

</body>
</html>
