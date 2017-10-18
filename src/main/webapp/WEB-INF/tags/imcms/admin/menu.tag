<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="imcms" uri="imcms" %>

<%@ attribute name="index" required="true" type="java.lang.Integer" %>
<%@ attribute name="document" required="false" type="java.lang.String" %>
<%@ attribute name="pre" required="false" type="java.lang.String" %>
<%@ attribute name="post" required="false" type="java.lang.String" %>

<%@ variable name-given="menuItems" scope="NESTED" variable-class="java.util.Collection" %>

<%-- do not remove - it helps Idea to understand var types --%>
<%--@elvariable id="currentDocument" type="imcode.server.document.textdocument.TextDocumentDomainObject"--%>
<%--@elvariable id="isEditMode" type="boolean"--%>
<%--@elvariable id="targetDocId" type="java.lang.Integer"--%>
<%--@elvariable id="menuService" type="com.imcode.imcms.domain.service.api.MenuService"--%>

<c:set var="targetDocId"
       value="${empty document ? currentDocument.id : document}"/>
<c:set var="menuItems" value="${menuService.getPublicMenuItemsOf(index, targetDocId)}" scope="request"/>
<c:set var="menuContent">${pre}<jsp:doBody/>${post}</c:set>
<c:remove var="menuItems"/>

<c:if test="${isEditMode}">
    <div class="imcms-editor-area imcms-editor-area--menu" data-doc-id="${targetDocId}" data-menu-id="${index}">
        <div class="imcms-editor-area__content imcms-editor-content">${menuContent}</div>
        <div class="imcms-editor-area__control-wrap">
            <div class="imcms-editor-area__control-edit imcms-control imcms-control--edit imcms-control--menu">
                <div class="imcms-editor-area__control-title">Menu Editor</div>
            </div>
        </div>
    </div>
</c:if>
<c:if test="${not isEditMode}">${menuContent}</c:if>
