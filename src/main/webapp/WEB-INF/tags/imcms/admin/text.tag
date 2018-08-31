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
<%@ attribute name="rows" required="false" type="java.lang.Integer" %>
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
    <c:set var="filteringPolicy" value="${textField.htmlFilteringPolicy}"/>

    <c:if test="${not empty content and (not isEditMode or not editOptions.editText) and mode ne 'write'}">${pre}${content}${post}</c:if>

    <c:if test="${isEditMode and editOptions.editText and mode ne 'read'}">

        <c:set var="loopData">
            <c:if test="${loopEntryRef ne null}"> data-loop-entry-ref.loop-entry-index="${loopEntryRef.loopEntryIndex}"
                data-loop-entry-ref.loop-index="${loopEntryRef.loopIndex}"</c:if>
        </c:set>

        <c:set var="tag">${'textarea wrap="hard"'}</c:set>
        <c:set var="tagClose">${'>'}</c:set>
        <c:set var="tagEnd">${'</textarea>'}</c:set>

        <c:if test="${empty formats}">
            <c:set var="type" value="${textField.type}"/>

            <c:if test="${'editor'.equalsIgnoreCase(type)}">
                <c:if test="${'unset'.equalsIgnoreCase(filteringPolicy)}">
                    <c:set var="filteringPolicy" value="RESTRICTED"/>
                </c:if>

                <c:set var="tag">div</c:set>
                <c:set var="tagClose">${'>'}</c:set>
                <c:set var="tagEnd">${'</div>'}</c:set>
            </c:if>

            <c:if test="${'text'.equalsIgnoreCase(type)}">
                <c:if test="${'unset'.equalsIgnoreCase(filteringPolicy)}">
                    <c:set var="filteringPolicy" value="ALLOW_ALL"/>
                </c:if>
                <c:set var="format" value="TEXT_FROM_EDITOR"/>
            </c:if>
            <c:if test="${'html'.equalsIgnoreCase(type)}">
                <c:if test="${'unset'.equalsIgnoreCase(filteringPolicy)}">
                    <c:set var="filteringPolicy" value="ALLOW_ALL"/>
                </c:if>
                <c:set var="content">${content.replaceAll('<', '&lt;').replaceAll('>', '&gt;').replaceAll('&lt;br /&gt;', '<br />')}</c:set>
                <c:set var="format" value="HTML_FROM_EDITOR"/>
            </c:if>
        </c:if>

        <c:if test="${not empty formats}">
            <c:if test="${not empty rows}">
                <c:set var="rowsData" value=" rows=\"${rows}\""/>

                <c:if test="${rows eq 1}">
                    <c:set var="tag">${'input type="text"'}</c:set>
                    <c:set var="tagClose">${' value=\"'}</c:set>
                    <c:set var="tagEnd">${'\"/>'}</c:set>
                </c:if>
            </c:if>

            <c:if test="${'text'.equalsIgnoreCase(formats)}">
                <c:set var="format" value="TEXT"/>
            </c:if>

            <c:if test="${not 'text'.equalsIgnoreCase(formats)}"><%-- means 'html' and any other even wrong format --%>
                <c:set var="format" value="HTML"/>
                <c:if test="${'unset'.equalsIgnoreCase(filteringPolicy)}">
                    <c:set var="filteringPolicy" value="ALLOW_ALL"/>
                </c:if>
                <c:set var="content">${content.replaceAll('<', '&lt;').replaceAll('>', '&gt;').replaceAll('&lt;br /&gt;', '<br />')}</c:set>
            </c:if>
        </c:if>

        <c:set var="typeData" value="${empty format ? '' : ' data-type=\"'.concat(format).concat('\"')}"/>
        <c:set var="filterType"> data-html-filtering-policy="${filteringPolicy}"</c:set>

        <c:set var="isInternal" value="${document eq null or document eq currentDocument.id}"/>
        <c:set var="editingLabel"
               value="${isInternal ? 'Text Editor' : 'This text is edited on page '.concat(document)}"/>
        <c:set var="externalPart"
               value="${(isInternal) ? '' : (' data-external=\"'.concat(document).concat('\" '))}"/>

        ${pre}
        <div class="imcms-editor-area imcms-editor-area--text">
            <c:if test="${not empty label}">
                <div class="imcms-editor-area__text-label">${label}</div>
            </c:if>
            <div class="imcms-editor-area__text-toolbar"></div>
            <${tag} class="imcms-editor-content imcms-editor-content--text" data-index="${index}"${externalPart}
            data-doc-id="${targetDocId}"${rowsData}${typeData}${loopData}${filterType}
            data-lang-code="${language}"${tagClose}${content}${tagEnd}
            <div class="imcms-editor-area__control-wrap">
                <div class="imcms-editor-area__control-edit imcms-control imcms-control--edit imcms-control--text">
                    <div class="imcms-editor-area__control-title">${editingLabel}</div>
                </div>
            </div>
        </div>
        ${post}
    </c:if>
</c:if>

<%-- do not remove - it helps Idea to understand var types --%>
<%--@elvariable id="currentDocument" type="com.imcode.imcms.api.TextDocument"--%>
<%--@elvariable id="isEditMode" type="boolean"--%>
<%--@elvariable id="isPreviewMode" type="boolean"--%>
<%--@elvariable id="textService" type="com.imcode.imcms.domain.service.TextService"--%>
<%--@elvariable id="loopEntryRef" type="com.imcode.imcms.model.LoopEntryRef"--%>
<%--@elvariable id="type" type="com.imcode.imcms.model.Text.Type"--%>
<%--@elvariable id="textField" type="com.imcode.imcms.model.Text"--%>
<%--@elvariable id="language" type="java.lang.String"--%>
<%--@elvariable id="editOptions" type="com.imcode.imcms.domain.dto.RestrictedPermissionDTO"--%>
<%--@elvariable id="isDocNew" type="boolean"--%>