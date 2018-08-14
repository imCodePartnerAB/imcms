${"<!--"}
<%@ tag trimDirectiveWhitespaces="true" %>
${"-->"}
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="imcms" uri="imcms" %>
<%@ attribute name="no" required="false" type="java.lang.Object" %>
<%@ attribute name="index" required="false" %>
<%@ attribute name="document" required="false" %>
<%@ attribute name="placeholder" required="false" %>
<%@ attribute name="label" required="false" %>
<%@ attribute name="rows" required="false" %>
<%@ attribute name="mode" required="false" %>
<%@ attribute name="formats" required="false" %>
<%@ attribute name="pre" required="false" %>
<%@ attribute name="post" required="false" %>
<%@ attribute name="showlabel" required="false" %>

<c:if test="${!isDocNew || editOptions.editText}">
    <c:if test="${empty index}">
        <c:set var="index" value="${no}"/><%-- old attribute "no" support --%>
    </c:if>

    <c:set var="targetDocId" value="${empty document ? currentDocument.id : document}"/>

    <c:set var="textField" value="${isEditMode or isPreviewMode
         ? textService.getText(targetDocId, index, language, loopEntryRef)
         : textService.getPublicText(targetDocId, index, language, loopEntryRef)}"/>

    <c:set var="content" value="${textField.text}"/>

    <c:if test="${not empty content and (not isEditMode or not editOptions.editText) and mode ne 'write'}">${pre}${content}${post}</c:if>
    <c:if test="${isEditMode and editOptions.editText and mode ne 'read'}">

        <c:set var="loopData">
            <c:if test="${loopEntryRef ne null}"> data-loop-entry-ref.loop-entry-index="${loopEntryRef.loopEntryIndex}"
                data-loop-entry-ref.loop-index="${loopEntryRef.loopIndex}"</c:if>
        </c:set>

        <c:set var="tag">textarea wrap="hard"</c:set>
        <c:set var="tagEnd"></textarea></c:set>
        <c:if test="${empty formats}">
            <c:set var="tag">div</c:set>
            <c:set var="tagEnd"></div></c:set>
        </c:if>

        <c:if test="${'html'.equalsIgnoreCase(formats) or 'cleanhtml'.equalsIgnoreCase(formats)}">
            <c:if test="${'html'.equalsIgnoreCase(formats)}">
                <c:set var="format" value="HTML"/>
            </c:if>
            <c:if test="${'cleanhtml'.equalsIgnoreCase(formats)}">
                <c:set var="format" value="CLEAN_HTML"/>
            </c:if>
            <c:set var="content">${content.replaceAll('<', '&lt;').replaceAll('>', '&gt;').replaceAll('&lt;br /&gt;', '<br />')}</c:set>
            <c:if test="${not empty rows}">
                <c:set var="rowsData" value=" rows=\"${rows}\""/>
            </c:if>
        </c:if>

        <c:if test="${'text'.equalsIgnoreCase(formats)}">
            <c:set var="format" value="TEXT"/>
            <c:if test="${not empty rows}">
                <c:set var="rowsData" value=" rows=\"${rows}\""/>
            </c:if>
        </c:if>

        <c:set var="typeData" value="${empty format ? '' : ' data-type=\"'.concat(format).concat('\"')}"/>

        <div class="imcms-editor-area imcms-editor-area--text">
            <c:if test="${not empty label}">
                <div class="imcms-editor-area__text-label">${label}</div>
            </c:if>
            <div class="imcms-editor-area__text-toolbar"></div>
                ${pre}
            <${tag} class="imcms-editor-content imcms-editor-content--text" data-index="${index}"
            data-doc-id="${targetDocId}"
            data-lang-code="${language}"${rowsData}${typeData}${loopData}>${content}${tagEnd}
                ${post}
            <div class="imcms-editor-area__control-wrap">
                <div class="imcms-editor-area__control-edit imcms-control imcms-control--edit imcms-control--text">
                    <div class="imcms-editor-area__control-title">Text Editor</div>
                </div>
            </div>
        </div>
    </c:if>
</c:if>

<%-- do not remove - it helps Idea to understand var types --%>
<%--@elvariable id="currentDocument" type="com.imcode.imcms.api.TextDocument"--%>
<%--@elvariable id="isEditMode" type="boolean"--%>
<%--@elvariable id="isPreviewMode" type="boolean"--%>
<%--@elvariable id="textService" type="com.imcode.imcms.domain.service.TextService"--%>
<%--@elvariable id="loopEntryRef" type="com.imcode.imcms.model.LoopEntryRef"--%>
<%--@elvariable id="textField" type="com.imcode.imcms.model.Text"--%>
<%--@elvariable id="language" type="java.lang.String"--%>
<%--@elvariable id="editOptions" type="com.imcode.imcms.domain.dto.RestrictedPermissionDTO"--%>
<%--@elvariable id="isDocNew" type="boolean"--%>