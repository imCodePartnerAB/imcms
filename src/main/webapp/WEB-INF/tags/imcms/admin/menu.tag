<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="imcms" uri="imcms" %>

<%@ attribute name="no" required="true" %>
<%@ attribute name="docId" required="false" %>
<%@ attribute name="label" required="false" %>
<%@ attribute name="pre" required="false" %>
<%@ attribute name="post" required="false" %>

<%@ variable name-given="menuItems" scope="NESTED" variable-class="java.util.Collection" %>

<%-- do not remove - it helps Idea to understand var types --%>
<%--@elvariable id="currentDocument" type="com.imcode.imcms.api.TextDocument"--%>
<%--@elvariable id="targetDoc" type="com.imcode.imcms.api.TextDocument"--%>
<%--@elvariable id="isEditMode" type="boolean"--%>

<c:set var="targetDoc" value="${empty docId ? currentDocument : (imcms:getDocument(docId, pageContext))}"/>
<c:set var="menuItems" value="${targetDoc.internal.getMenu(no).menuItemsVisibleToUserAsTree}" scope="request"/>
<c:set var="menuContent">${pre}<jsp:doBody/>${post}</c:set>
<c:remove var="menuItems"/>

<c:if test="${isEditMode}">
    <div class="imcms-editor-area imcms-editor-area--menu" data-doc-id="${targetDoc.id}" data-menu-id="${no}">
        <div class="imcms-editor-area__content imcms-editor-content">${menuContent}</div>
        <div class="imcms-editor-area__control-wrap">
            <div class="imcms-editor-area__control-edit imcms-control imcms-control--edit imcms-control--menu">
                <div class="imcms-editor-area__control-title">Menu Editor</div>
            </div>
        </div>
    </div>
</c:if>
<c:if test="${not isEditMode}">${menuContent}</c:if>
