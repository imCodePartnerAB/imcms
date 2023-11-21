<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags/imcms/ui" %>
<%@ page
        import="com.imcode.imcms.servlet.PasswordReset,
                com.imcode.imcms.util.l10n.LocalizedMessage"
        pageEncoding="UTF-8"
%>
<%!
    static final LocalizedMessage formInfo = new LocalizedMessage("passwordreset.identity_form_info");
    static final LocalizedMessage formLabelEmail = new LocalizedMessage("passwordreset.identity_form.lbl_identity");
    static final LocalizedMessage formSubmit = new LocalizedMessage("passwordreset.identity_form.submit");
%>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<fmt:setLocale value="${userLanguage}"/>
<fmt:setBundle basename="imcms" var="resource_property"/>
<html>
<head>
    <title><fmt:message key="passwordreset.title" bundle="${resource_property}"/></title>
    <link rel="stylesheet" type="text/css" href="${contextPath}/dist/imcms_admin.css">
    <link rel="stylesheet" type="text/css" href="${contextPath}/dist/imcms-imports_files.css">
    <link rel="stylesheet" type="text/css" href="${contextPath}/dist/modal_window/imcms-modal-admin.css">
    <script src="${contextPath}/imcms/js/imcms_admin.js" type="text/javascript"></script>
</head>
<body>
<div class="imcms-modal-admin">
    <div class="imcms-modal-admin-head">
        <a href="https://imcode.com" class="imcms-login__logo"></a>
        <div class="imcms-title imcms-head__title"><fmt:message key="passwordreset.title" bundle="${resource_property}"/></div>
    </div>
    <div class="imcms-modal-admin-body">
        <% LocalizedMessage error = (LocalizedMessage) request.getAttribute(PasswordReset.REQUEST_ATTR_VALIDATION_ERRORS);
            if (error != null) { %>
        <div class="imcms-field">
            <div class="imcms-error-msg imcms-modal-admin__error-msg">
                <fmt:message key="passwordreset.title.validation_errors" bundle="${resource_property}"/>
            </div>
            <div class="imcms-error-msg imcms-modal-admin__error-msg"><%= error.toLocalizedStringByIso639_1( (String) request.getAttribute("userLanguage") ) %>
            </div>
        </div>
        <% } %>
        <div class="imcms-field">
            <div class="imcms-title" style="word-break: break-word">
                <%= formInfo.toLocalizedStringByIso639_1((String) request.getAttribute("userLanguage")) %>
            </div>
        </div>
        <div class="imcms-field">
            <form method="POST" id="PasswordReset" action="<%= request.getContextPath() %>/servlet/PasswordReset">
                <input type="hidden" name="<%=PasswordReset.REQUEST_PARAM_OP%>"
                       value="<%=PasswordReset.Op.SEND_RESET_URL%>"/>
                <div class="imcms-field">
                    <div class="imcms-text-box">
                        <label for="<%=PasswordReset.REQUEST_USER_IDENTITY%>"
                               class="imcms-label imcms-text-box__label">
                            <%=formLabelEmail.toLocalizedStringByIso639_1((String) request.getAttribute("userLanguage"))%>
                        </label>
                        <input id="<%=PasswordReset.REQUEST_USER_IDENTITY%>"
                               name="<%=PasswordReset.REQUEST_USER_IDENTITY%>"
                               type="text"
                               class="imcms-input imcms-text-box__input">
                    </div>
                </div>

            </form>
        </div>
    </div>
    <div class="imcms-modal-admin-footer">
        <button type="submit"
                form="PasswordReset"
                class="imcms-button imcms-button--positive imcms-modal-admin-footer__button">
            <%=formSubmit.toLocalizedStringByIso639_1((String) request.getAttribute("userLanguage"))%>
        </button>
    </div>
</div>


</body>
</html>
