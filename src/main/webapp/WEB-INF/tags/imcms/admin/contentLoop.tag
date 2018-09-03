${"<!--"}
<%@ tag trimDirectiveWhitespaces="true" %>
${"-->"}
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="imcms" uri="imcms" %>

<%@ attribute name="index" required="true" type="java.lang.Integer" %>
<%@ attribute name="document" type="java.lang.Integer" %>
<%@ attribute name="label" %>
<%@ attribute name="pre" %>
<%@ attribute name="post" %>

<%@ variable name-given="loop" scope="NESTED" variable-class="com.imcode.imcms.model.Loop" %>
<%@ variable name-given="loopIndex" scope="NESTED" variable-class="java.lang.Integer" %>

<%-- do not remove - it helps Idea to understand var types --%>
<%--@elvariable id="loop" type="com.imcode.imcms.model.Loop"--%>
<%--@elvariable id="currentDocument" type="imcode.server.document.textdocument.TextDocumentDomainObject"--%>
<%--@elvariable id="loopService" type="com.imcode.imcms.domain.service.LoopService"--%>
<%--@elvariable id="isEditMode" type="boolean"--%>
<%--@elvariable id="isPreviewMode" type="boolean"--%>
<%--@elvariable id="editOptions" type="com.imcode.imcms.domain.dto.RestrictedPermissionDTO"--%>
<%--@elvariable id="isDocNew" type="boolean"--%>
<%--@elvariable id="disableExternal" type="java.lang.Boolean"--%>

<c:if test="${!isDocNew || editOptions.editLoop}">
    <c:set var="targetDocId" value="${empty document ? currentDocument.id : document}"/>
    <c:set var="loop" value="${isEditMode || isPreviewMode
            ? loopService.getLoop(index, targetDocId) : loopService.getLoopPublic(index, targetDocId)}"
           scope="request"/>
    <c:set var="loopIndex" value="${index}" scope="request"/>

    <c:set var="loopContent" value=""/>
    <c:if test="${loop.entries.size() gt 0}">
        <c:set var="loopContent">
            ${pre}
            <jsp:doBody/>
            ${post}
        </c:set>
    </c:if>

    <c:remove var="loop"/>
    <c:remove var="loopIndex"/>

    <c:choose>
        <c:when test="${isEditMode && editOptions.editLoop}">
            <c:set var="isInternal" value="${disableExternal or document eq null or document eq currentDocument.id}"/>
            <c:set var="label" value="${isInternal ? 'Loop Editor' : 'This loop is edited on page '.concat(document)}"/>
            <c:set var="externalPart"
                   value="${(isInternal) ? '' : (' data-external=\"'.concat(document).concat('\" '))}"/>

            <div class="imcms-editor-area imcms-editor-area--loop" data-doc-id="${targetDocId}"${externalPart}
                 data-index="${index}">
                <div class="imcms-editor-area__content imcms-editor-content">${loopContent}</div>
                <div class="imcms-editor-area__control-wrap">
                    <div class="imcms-editor-area__control-edit imcms-control imcms-control--edit imcms-control--loop">
                        <div class="imcms-editor-area__control-title">${label}</div>
                    </div>
                </div>
            </div>
        </c:when>
        <c:otherwise>
            ${loopContent}
        </c:otherwise>
    </c:choose>
</c:if>