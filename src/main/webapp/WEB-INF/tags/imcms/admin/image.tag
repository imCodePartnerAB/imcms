<%@ tag import="org.apache.commons.text.StringEscapeUtils" %>
<%@ tag import="imcode.server.Imcms" %>
<%@ tag trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="imcms" uri="imcms" %>
<%@ attribute name="no" required="false" type="java.lang.Object" %><%-- old index name --%>
<%@ attribute name="index" required="false" %>
<%@ attribute name="document" required="false" %>
<%@ attribute name="style" required="false" %>
<%@ attribute name="pre" required="false" %>
<%@ attribute name="post" required="false" %>
<%@ attribute name="label" required="false" %>
<%@ attribute name="showlabel" required="false" type="java.lang.Boolean" %>
<%@ attribute name="showMode" required="false" description="Editor style: default|small" type="java.lang.String" %>

<%-- do not remove - it helps Idea to understand var types --%>
<%--@elvariable id="currentDocument" type="imcode.server.document.textdocument.TextDocumentDomainObject"--%>
<%--@elvariable id="imageService" type="com.imcode.imcms.domain.service.ImageService"--%>
<%--@elvariable id="image" type="com.imcode.imcms.domain.dto.ImageDTO"--%>
<%--@elvariable id="isEditMode" type="boolean"--%>
<%--@elvariable id="isPreviewMode" type="boolean"--%>
<%--@elvariable id="loopEntryRef" type="com.imcode.imcms.model.LoopEntryRef"--%>
<%--@elvariable id="language" type="java.lang.String"--%>
<%--@elvariable id="editOptions" type="com.imcode.imcms.domain.dto.RestrictedPermissionDTO"--%>
<%--@elvariable id="contextPath" type="java.lang.String"--%>
<%--@elvariable id="isDocNew" type="boolean"--%>
<%--@elvariable id="disableExternal" type="java.lang.Boolean"--%>
<%--@elvariable id="imagesPath" type="java.lang.String"--%>

<c:if test="${!isDocNew || editOptions.editImage}">
    <c:if test="${empty index}">
        <c:set var="index" value="${no}"/><%-- old attribute "no" support --%>
    </c:if>

    <c:set var="targetDocId" value="${empty document ? currentDocument.id : document}"/>

    <c:set var="imageContent">

        <c:set var="versionNo" value="${pageContext.request.getParameter('version-no')}"/>
        <c:choose>
            <c:when test="${versionNo ne null and isPreviewMode}">
                <c:set var="image" value="${imageService.getImage(targetDocId, index, versionNo, language, loopEntryRef)}"/>
            </c:when>
            <c:when test="${isEditMode or isPreviewMode}">
                <c:set var="image" value="${imageService.getImage(targetDocId, index, language, loopEntryRef)}"/>
            </c:when>
            <c:otherwise>
                <c:set var="image" value="${imageService.getPublicImage(targetDocId, index, language, loopEntryRef)}"/>
            </c:otherwise>
        </c:choose>

        <c:set var="imgPath" value="${image.generatedFilePath}"/>

        <c:set var="classes" value=""/>
        <c:set var="styles" value=""/>

        <c:if test="${image.spaceAround.top ne 0}">
            <c:set var="spaceTopStyle" value="margin-top:${image.spaceAround.top}px; "/>
        </c:if>
        <c:if test="${image.spaceAround.right ne 0}">
            <c:set var="spaceRightStyle" value="margin-right:${image.spaceAround.right}px; "/>
        </c:if>
        <c:if test="${image.spaceAround.bottom ne 0}">
            <c:set var="spaceBottomStyle" value="margin-bottom:${image.spaceAround.bottom}px; "/>
        </c:if>
        <c:if test="${image.spaceAround.left ne 0}">
            <c:set var="spaceLeftStyle" value="margin-left:${image.spaceAround.left}px; "/>
        </c:if>
        <c:set var="spaceStyles">${spaceTopStyle}${spaceRightStyle}${spaceBottomStyle}${spaceLeftStyle}</c:set>

        <c:set var="alignClass" value=""/>
        <c:if test="${'CENTER' eq image.align}"><c:set var="alignClass" value="imcms-image-align-center "/></c:if>
        <c:if test="${'LEFT' eq image.align}"><c:set var="alignClass" value="imcms-image-align-left "/></c:if>
        <c:if test="${'RIGHT' eq image.align}"><c:set var="alignClass" value="imcms-image-align-right "/></c:if>

        <c:if test="${not empty alignClass}">
            <c:set var="classes" value=" class=\"${alignClass}\""/>
        </c:if>

        <c:if test="${not empty spaceStyles}">
            <c:set var="styles" value=" style=\"${spaceStyles}\""/>
        </c:if>

        <c:set var="alt" value="${empty image.alternateText
            ? ' alt=\" \"'
            : ' alt=\"'.concat(StringEscapeUtils.escapeHtml4(image.alternateText)).concat('\"')}"
        />

        <c:choose>
            <c:when test="${empty image.linkUrl}">
                <c:set var="href" value=""/>
            </c:when>
            <c:otherwise>
                <c:choose>
                    <c:when test="${fn:startsWith(image.linkUrl, '/') || fn:startsWith(image.linkUrl, 'http')}">
                        <c:set var="href" value="${' href=\"'.concat(image.linkUrl).concat('\"')}"/>
                    </c:when>
                    <c:otherwise>
                        <c:set var="href" value="${' href=\"'.concat('//').concat(image.linkUrl).concat('\"')}"/>
                    </c:otherwise>
                </c:choose>
            </c:otherwise>
        </c:choose>

        <c:choose>
            <c:when test="${not empty imgPath}">
                ${pre}
                <c:if test="${not empty href}">
                    <a${href} target="_blank">
                </c:if>
                    <img src="${imagesPath}?path=${imgPath}"${classes}${styles}${alt}/>
                <c:if test="${not empty href}">
                    </a>
                </c:if>
                ${post}
            </c:when>
            <c:otherwise>
                ${pre}
                    <img src="${contextPath}/imcms/images/icon_missing_image.png"/>
                ${post}
            </c:otherwise>
        </c:choose>

    </c:set>

    <c:choose>
        <c:when test="${isEditMode && editOptions.editImage}">
	        <c:set var="isInternal" value="${disableExternal or document eq null or document eq currentDocument.id}"/>

	        <fmt:setLocale value="<%=Imcms.getUser().getLanguage()%>"/>
	        <fmt:setBundle basename="imcms" var="resource_property"/>
	        <c:set var="editorLabel">
		        <%-- variable can be set using another expression --%>
		        <c:choose>
			        <c:when test="${isInternal}">
				        <fmt:message key="editors/image/label" bundle="${resource_property}"/>
			        </c:when>
			        <c:otherwise>
				        <fmt:message key="editors/image/external_message" bundle="${resource_property}">
					        <%--replace {0} --%>
					        <fmt:param value="${document}"/>
				        </fmt:message>
			        </c:otherwise>
		        </c:choose>
	        </c:set>

            <c:set var="externalPart"
                   value="${(isInternal) ? '' : (' data-external=\"'.concat(document).concat('\"'))}"/>
            <c:set var="loopPart" value="${empty loopEntryRef ? ''
                    : ' data-loop-index=\"'.concat(loopEntryRef.loopIndex).concat('\" data-loop-entry-index=\"')
            .concat(loopEntryRef.loopEntryIndex).concat('\"')}"/>

            <c:if test="${not empty style}"><c:set var="style" value=" data-style=\"${style}\""/></c:if>

            <c:set var="isShowlabel" value="${empty showlabel ? 'true' : showlabel}"/>

	        <c:choose>
	            <c:when test="${showMode == 'small'}">
		            <div class="imcms-editor-area--small imcms-editor-area--image"
		                 data-doc-id="${targetDocId}"${externalPart}${style}
		                 data-lang-code="${language}" data-index="${no}"${loopPart}>
                        <div class="imcms-editor-body">
                            <div class="imcms-editor-area__content imcms-editor-content">${imageContent}</div>
                            <div class="imcms-editor-area__control-wrap imcms-editor-area__control-wrap--small">
                                <div class="imcms-editor-area__control-edit imcms-control imcms-control--edit imcms-control--image" data-label="${editorLabel}"></div>
                                <c:if test="${not empty label && isShowlabel}">
                                    <div class="imcms-editor-area__control-edit imcms-control imcms-control--edit imcms-control--info" data-label="${label}"></div>
                                </c:if>
                            </div>
                        </div>
		            </div>
	            </c:when>
		        <c:otherwise>
			        <div class="imcms-editor-area imcms-editor-area--image"
			             data-doc-id="${targetDocId}"${externalPart}${style}
			             data-lang-code="${language}" data-index="${no}"${loopPart}>
				        <c:if test="${not empty label && isShowlabel}">
					        <div class="imcms-editor-area__text-label">${label}</div>
				        </c:if>
                        <div class="imcms-editor-body">
                            <div class="imcms-editor-area__content imcms-editor-content">${imageContent}</div>
                            <div class="imcms-editor-area__control-wrap">
                                <div class="imcms-editor-area__control-edit imcms-control imcms-control--edit imcms-control--image" data-label="${editorLabel}"></div>
                            </div>
                        </div>
			        </div>
                </c:otherwise>
	        </c:choose>
        </c:when>
        <c:otherwise>
            <c:if test="${not empty imgPath}">${imageContent}</c:if>
        </c:otherwise>
    </c:choose>
</c:if>
