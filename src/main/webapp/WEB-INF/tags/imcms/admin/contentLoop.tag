<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="imcms" uri="imcms" %>

<%@ attribute name="no" required="true" type="java.lang.Integer" %>
<%@ attribute name="document" type="java.lang.Integer" %>
<%@ attribute name="label"%>
<%@ attribute name="pre"%>
<%@ attribute name="post"%>

<%@ variable name-given="loop" scope="NESTED" variable-class="com.imcode.imcms.domain.dto.LoopDTO" %>
<%@ variable name-given="loopNo" scope="NESTED" variable-class="java.lang.Integer" %>

<%-- do not remove - it helps Idea to understand var types --%>
<%--@elvariable id="loop" type="com.imcode.imcms.domain.dto.LoopDTO"--%>
<%--@elvariable id="currentDocument" type="com.imcode.imcms.api.TextDocument"--%>
<%--@elvariable id="loopService" type="com.imcode.imcms.domain.service.LoopService"--%>
<%--@elvariable id="isEditMode" type="boolean"--%>

<c:set var="targetDocId" value="${empty document ? currentDocument.id : document}"/>
<c:set var="loop" value="${loopService.getLoop(no, targetDocId)}" scope="request"/>
<c:set var="loopNo" value="${no}" scope="request"/>

<c:if test="${loop.entries.size() gt 0}">
    <c:set var="loopContent">${pre}<jsp:doBody/>${post}</c:set>
</c:if>

<c:remove var="loop"/>
<c:remove var="loopNo"/>

<c:if test="${isEditMode}">
    <div class="imcms-editor-area imcms-editor-area--loop" data-doc-id="${targetDocId}" data-loop-index="${no}">
        <div class="imcms-editor-area__content imcms-editor-content">${loopContent}</div>
        <div class="imcms-editor-area__control-wrap">
            <div class="imcms-editor-area__control-edit imcms-control imcms-control--edit imcms-control--loop">
                <div class="imcms-editor-area__control-title">Loop Editor</div>
            </div>
        </div>
    </div>
</c:if>
<c:if test="${not isEditMode}">${loopContent}</c:if>
