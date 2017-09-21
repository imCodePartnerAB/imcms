<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="imcms" uri="imcms" %>

<%@ attribute name="no" required="true" %>
<%@ attribute name="document"%>
<%@ attribute name="label"%>
<%@ attribute name="pre"%>
<%@ attribute name="post"%>

<%@ variable name-given="loopEntries" scope="NESTED" variable-class="java.util.Collection" %>

<%-- do not remove - it helps Idea to understand var types --%>
<%--@elvariable id="currentDocument" type="com.imcode.imcms.api.TextDocument"--%>
<%--@elvariable id="targetDoc" type="com.imcode.imcms.api.TextDocument"--%>
<%--@elvariable id="isEditMode" type="boolean"--%>

<c:set var="targetDoc" value="${empty document ? currentDocument : (imcms:getDocument(document, pageContext))}"/>
<%--<c:set var="loopEntries" value="${targetDoc.internal.getMenu(no).menuItemsVisibleToUserAsTree}" scope="request"/>--%>
<c:set var="loopContent">${pre}<jsp:doBody/>${post}</c:set>
<c:remove var="loopContent"/>

<c:if test="${isEditMode}">
    <div class="imcms-editor-area imcms-editor-area--loop" data-doc-id="${targetDoc.id}" data-loop-id="${no}">
        <div class="imcms-editor-area__content imcms-editor-content">${loopContent}</div>
        <div class="imcms-editor-area__control-wrap">
            <div class="imcms-editor-area__control-edit imcms-control imcms-control--edit imcms-control--loop">
                <div class="imcms-editor-area__control-title">Loop Editor</div>
            </div>
        </div>
    </div>
</c:if>
<c:if test="${not isEditMode}">${loopContent}</c:if>
