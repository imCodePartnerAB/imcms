<%@ tag trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="imcms" uri="imcms" %>

<%@ attribute name="index" required="true" type="java.lang.Integer" %>
<%@ attribute name="document" required="false" type="java.lang.String" %>
<%@ attribute name="pre" required="false" type="java.lang.String" %>
<%@ attribute name="post" required="false" type="java.lang.String" %>
<%@ attribute name="attributes" required="false" type="java.lang.String" %>
<%@ attribute name="treeKey" required="false" type="java.lang.Integer" %>
<%@ attribute name="wrap" required="false" type="java.lang.String" %>
<%@ attribute name="label" required="false" %>
<%@ attribute name="showlabel" required="false" type="java.lang.Boolean" %>

<%@ variable name-given="menuItems" scope="NESTED" variable-class="java.util.Collection" %>

<%-- do not remove - it helps Idea to understand var types --%>
<%--@elvariable id="currentDocument" type="imcode.server.document.textdocument.TextDocumentDomainObject"--%>
<%--@elvariable id="isEditMode" type="boolean"--%>
<%--@elvariable id="isPreviewMode" type="boolean"--%>
<%--@elvariable id="targetDocId" type="java.lang.Integer"--%>
<%--@elvariable id="menuService" type="com.imcode.imcms.domain.service.MenuService"--%>
<%--@elvariable id="language" type="java.lang.String"--%>
<%--@elvariable id="editOptions" type="com.imcode.imcms.domain.dto.RestrictedPermissionDTO"--%>
<%--@elvariable id="isDocNew" type="boolean"--%>
<%--@elvariable id="disableExternal" type="java.lang.Boolean"--%>

<c:if test="${!isDocNew || editOptions.editMenu}">
    <c:set var="targetDocId" value="${empty document ? currentDocument.id : document}"/>
    <jsp:doBody var="tagContentBody"/>
    <c:set var="isTagBodyEmpty" value="${empty tagContentBody}"/>

    <c:choose>
        <c:when test="${isTagBodyEmpty}">
            <c:set var="menuItems" value="${
         (isEditMode && editOptions.editMenu || isPreviewMode)
         ? menuService.getVisibleMenuAsHtml(targetDocId, index, language, isNested, attributes, treeKey, wrap)
         : menuService.getPublicMenuAsHtml(targetDocId, index, language, isNested, attributes, treeKey, wrap)
         }" scope="request"/>
        </c:when>
        <c:otherwise>
            <c:set var="menuItems" value="${
        (isEditMode && editOptions.editMenu || isPreviewMode)
         ? menuService.getVisibleMenuItems(targetDocId, index, language, isNested)
         : menuService.getPublicMenuItems(targetDocId, index, language, isNested)
         }" scope="request"/>
        </c:otherwise>
    </c:choose>


    <c:set var="isShowlabel" value="${empty showlabel ? 'true' : showlabel}"/>

    <c:set var="menuContent">
        <c:if test="${not empty menuItems}">
            <c:choose>
                <c:when test="${not isTagBodyEmpty}">
                    ${pre}
                    <jsp:doBody/>
                    ${post}
                </c:when>
                <c:otherwise>
                    ${pre}
                    ${menuItems}
                    ${post}
                </c:otherwise>
            </c:choose>
        </c:if>
    </c:set>
    <c:remove var="menuItems"/>

    <c:choose>
        <c:when test="${isEditMode && editOptions.editMenu}">
            <c:set var="isInternal" value="${disableExternal or document eq null or document eq currentDocument.id}"/>
            <c:set var="editLabel"
                   value="${isInternal ? (language.equals('en') ? 'Menu Editor' : 'Redigera Meny') : 'This menu is edited on page '.concat(document)}"/>
            <c:set var="externalPart"
                   value="${(isInternal) ? '' : (' data-external=\"'.concat(document).concat('\" '))}"/>

            <div class="imcms-editor-area imcms-editor-area--menu" data-doc-id="${targetDocId}"${externalPart}
                 data-menu-index="${index}">
                <c:if test="${not empty label && isShowlabel}">
                    <div class="imcms-editor-area__text-label">${label}</div>
                </c:if>
                <div class="imcms-editor-area__content imcms-editor-content" data-doc-id="${targetDocId}"
                     data-menu-index="${index}">${menuContent}</div>
                    <%-- attributes used as unique identifier while reload --%>
                <div class="imcms-editor-area__control-wrap">
                    <div class="imcms-editor-area__control-edit imcms-control imcms-control--edit imcms-control--menu">
                        <div class="imcms-editor-area__control-title">${editLabel}</div>
                    </div>
                </div>
            </div>
        </c:when>
        <c:otherwise>${menuContent}</c:otherwise>
    </c:choose>
</c:if>
