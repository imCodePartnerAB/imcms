<%@ tag import="imcode.server.Imcms" %>
<%@ tag trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@ taglib prefix="imcms" uri="imcms" %>

<%@ attribute name="id" required="false" %>
<%@ attribute name="index" required="true" type="java.lang.Integer" %>
<%@ attribute name="document" type="java.lang.Integer" %>
<%@ attribute name="label" %>
<%@ attribute name="pre" %>
<%@ attribute name="post" %>
<%@ attribute name="showlabel" required="false" type="java.lang.Boolean" %>
<%@ attribute name="showMode" required="false" description="Editor style: default|small" type="java.lang.String" %>

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
	<c:if test="${empty id}">
		<c:choose>
			<c:when test="${empty document}">
				<c:set var="id" value="loop-${index}"/>
			</c:when>
			<c:otherwise>
				<c:set var="id" value="loop-${document}-${index}"/>
			</c:otherwise>
		</c:choose>
	</c:if>

    <c:set var="targetDocId" value="${empty document ? currentDocument.id : document}"/>

	<c:set var="versionNo" value="${pageContext.request.getParameter('version-no')}"/>
	<c:choose>
		<c:when test="${versionNo ne null and isPreviewMode}">
			<c:set var="loop" value="${loopService.getLoop(index, targetDocId, versionNo)}" scope="request"/>
		</c:when>
		<c:when test="${isEditMode or isPreviewMode}">
			<c:set var="loop" value="${loopService.getLoop(index, targetDocId)}" scope="request"/>
		</c:when>
		<c:otherwise>
			<c:set var="loop" value="${loopService.getLoopPublic(index, targetDocId)}" scope="request"/>
		</c:otherwise>
	</c:choose>

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

	        <fmt:setLocale value="<%=Imcms.getUser().getLanguage()%>"/>
	        <fmt:setBundle basename="imcms" var="resource_property"/>
	        <c:set var="editorLabel">
		        <%-- variable can be set using another expression --%>
		        <c:choose>
			        <c:when test="${isInternal}">
				        <fmt:message key="editors/loop/label" bundle="${resource_property}"/>
			        </c:when>
			        <c:otherwise>
				        <fmt:message key="editors/loop/external_message" bundle="${resource_property}">
					        <%--replace {0} --%>
					        <fmt:param value="${document}"/>
				        </fmt:message>
			        </c:otherwise>
		        </c:choose>
	        </c:set>

            <c:set var="externalPart"
                   value="${(isInternal) ? '' : (' data-external=\"'.concat(document).concat('\" '))}"/>

            <c:set var="isShowlabel" value="${empty showlabel ? 'true' : showlabel}"/>

	        <c:choose>
		        <c:when test="${showMode == 'small'}">

			        <div class="imcms-editor-area--small imcms-editor-area--loop"
						 id="${id}"
						 data-doc-id="${targetDocId}"${externalPart}
			             data-index="${index}">
						<div class="imcms-editor-body">
							<div class="imcms-editor-area__content imcms-editor-content"
								 data-doc-id="${targetDocId}"${externalPart}
								 data-index="${index}">${loopContent}</div>
							<div class="imcms-editor-area__control-wrap imcms-editor-area__control-wrap--small">
								<div class="imcms-editor-area__control-edit imcms-control imcms-control--edit imcms-control--loop" data-label="${editorLabel}"></div>
								<c:if test="${not empty label && isShowlabel}">
									<div class="imcms-editor-area__control-edit imcms-control imcms-control--edit imcms-control--info" data-label="${label}"></div>
								</c:if>
							</div>
						</div>
			        </div>
		        </c:when>
		        <c:otherwise>
					<div class="imcms-editor-area imcms-editor-area--loop"
						 id="${id}"
						 data-doc-id="${targetDocId}"${externalPart}
						 data-index="${index}">
						<c:if test="${not empty label && isShowlabel}">
							<div class="imcms-editor-area__text-label">${label}</div>
						</c:if>
						<div class="imcms-editor-body">
							<div class="imcms-editor-area__content imcms-editor-content"
								 data-doc-id="${targetDocId}"${externalPart}
								 data-index="${index}">${loopContent}</div>
							<div class="imcms-editor-area__control-wrap">
								<div class="imcms-editor-area__control-edit imcms-control imcms-control--edit imcms-control--loop" data-label="${editorLabel}"></div>
							</div>
						</div>
					</div>
		        </c:otherwise>
	        </c:choose>
        </c:when>
        <c:otherwise>
            ${loopContent}
        </c:otherwise>
    </c:choose>
</c:if>
