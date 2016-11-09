<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="im" uri="imcms" %>
<%@ page import="com.imcode.imcms.servlet.Version" %>
<%@ page import="imcode.server.ImcmsConstants" %>
<%@ page import="org.apache.commons.lang3.BooleanUtils" %>
<%@ page import="imcode.server.Imcms" %>
<%@ page import="com.imcode.imcms.api.DocumentVersionInfo" %>
<im:variables/>
<%
    if (!user.canEdit(document)) {
        return;
    }

    boolean editMode = ("" + ImcmsConstants.PERM_EDIT_TEXT_DOCUMENT_TEXTS).equals(request.getParameter("flags"));
    pageContext.setAttribute("isEditMode", editMode);

    boolean previewMode = BooleanUtils.toBoolean(request.getParameter(ImcmsConstants.REQUEST_PARAM__WORKING_PREVIEW));
    pageContext.setAttribute("isPreviewMode", previewMode);

    String imcmsVersion = Version.getImcmsVersion(getServletConfig().getServletContext())
            .replace("imCMS", "<span>imCMS</span>");
    pageContext.setAttribute("imcmsVersion", imcmsVersion);

    pageContext.setAttribute("permEditTextDocumentTexts", ImcmsConstants.PERM_EDIT_TEXT_DOCUMENT_TEXTS);
    pageContext.setAttribute("requestParamWorkingPreview", ImcmsConstants.REQUEST_PARAM__WORKING_PREVIEW);
    pageContext.setAttribute("dispatchFlagPublish", ImcmsConstants.DISPATCH_FLAG__PUBLISH);

    final DocumentVersionInfo documentVersionInfo = Imcms.getServices()
            .getDocumentMapper()
            .getDocumentVersionInfo(document.getId());

    final long workingVersionModifiedTime = documentVersionInfo.getWorkingVersion()
            .getModifiedDt()
            .getTime();

    final long defaultVersionModifiedTime = documentVersionInfo.getDefaultVersion()
            .getModifiedDt()
            .getTime();

    final boolean hasNewerVersion = (workingVersionModifiedTime > defaultVersionModifiedTime);
    pageContext.setAttribute("hasNewerVersion", hasNewerVersion);

    final boolean canEditDocInfo = user.getInternal()
            .getPermissionSetFor(document.getInternal())
            .getEditDocumentInformation();
    pageContext.setAttribute("canEditDocInfo", canEditDocInfo);

    final String documentLifeCyclePhase = document.getInternal()
            .getLifeCyclePhase()
            .toString()
            .substring(0, 1)
            .toUpperCase();
    pageContext.setAttribute("documentLifeCyclePhase", documentLifeCyclePhase);

    final boolean isVersioningAllowed = Imcms.isVersioningAllowed();
    pageContext.setAttribute("isVersioningAllowed", isVersioningAllowed);
%>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
<c:set var="currentLangCode" value="${user.internal.docGetterCallback.language.code}"/>
<c:set var="docAliasOrId" value="${document.alias ne null ? document.alias : document.id}"/>
<%-- todo: replace ugly urls using Linker --%>

<div class="admin-panel reset">
    <div class="admin-panel-draggable"></div>
    <div class="admin-panel-content">
        <section id="languages" class="admin-panel-content-section  admin-panel-content-section-language">
            <div class="admin-panel-version">${imcmsVersion}
            </div>
            <div class="admin-panel-language">
                <a href="${contextPath}/servlet/GetDoc?meta_id=${document.id}&lang=en"
                   title="English/English (default current)"
                   class="imcms-panel-safe-redirect${currentLangCode eq 'en' ? ' active' : ''}">
                    <img src="${contextPath}/images/ic_english.png" alt="" style="border:0;">
                </a>
                <a href="${contextPath}/servlet/GetDoc?meta_id=${document.id}&lang=sv" title="Swedish/Svenska"
                   class="imcms-panel-safe-redirect${currentLangCode eq 'sv' ? ' active' : ''}">
                    <img src="${contextPath}/images/ic_swedish.png" alt="" style="border:0;">
                </a>
            </div>
        </section>
        <section id="read" data-mode="readonly"
                 class="admin-panel-content-section${isEditMode or isPreviewMode ? "" : " active"}">
            <a href="${contextPath}/${docAliasOrId}" class="imcms-panel-safe-redirect" target="_self">
                <div class="admin-panel-button">
                    <div class="admin-panel-button-image"></div>
                    <span class="admin-panel-button-description">Public</span>
                </div>
            </a>
        </section>
        <section id="edit" data-mode="edit" class="admin-panel-content-section${isEditMode ? " active" : ""}">
            <a href="${contextPath}/servlet/AdminDoc?meta_id=${document.id}&flags=${permEditTextDocumentTexts}"
               class="imcms-panel-safe-redirect" target="_self">
                <div class="admin-panel-button">
                    <div class="admin-panel-button-image"></div>
                    <span class="admin-panel-button-description">Edit</span>
                </div>
            </a>
        </section>
        <c:if test="${isVersioningAllowed}">
            <section id="preview" data-mode="preview"
                     class="admin-panel-content-section${isPreviewMode ? " active" : ""}">
                <a href="${contextPath}/${docAliasOrId}?${requestParamWorkingPreview}=true"
                   class="imcms-panel-safe-redirect" target="_self">
                    <div class="admin-panel-button">
                        <div class="admin-panel-button-image"></div>
                        <span class="admin-panel-button-description">Preview</span>
                    </div>
                </a>
            </section>
            <section id="publish" data-mode="publish"
                     class="admin-panel-content-section${hasNewerVersion ? " has-version-changed" : ""}">
                <a href="${contextPath}/servlet/AdminDoc?meta_id=${document.id}&flags=${dispatchFlagPublish}"
                   class="imcms-panel-safe-redirect" target="_self">
                    <div class="admin-panel-button">
                        <div class="admin-panel-button-image">Publish offline version</div>
                    </div>
                </a>
            </section>
        </c:if>
        <div class="admin-panel-content-separator"></div>
        <section id="info" data-mode="info"
                 class="admin-panel-content-section${canEditDocInfo ? "" : " admin-panel-content-section-disabled"}">
            <a href="#" target="_self" onclick="${canEditDocInfo ? "pageInfo();" : ""}return false;">
                <div class="admin-panel-button">
                    <div class="admin-panel-button-image"></div>
                    <span class="admin-panel-button-description">Page info</span>
                </div>
            </a>
        </section>
        <section id="additionalInfo" data-mode="info"
                 class="admin-panel-content-section${canEditDocInfo ? "" : " admin-panel-content-section-disabled"}">
            <a href="#" target="_self" onclick="return false;">
                <div class="admin-panel-button">
                    <div>
                        <span>${document.id}</span>${documentLifeCyclePhase}
                    </div>
                </div>
            </a>
        </section>
        <div class="admin-panel-content-separator"></div>
        <section id="docs" data-mode="docs"
                 class="admin-panel-content-section${canEditDocInfo ? "" : " admin-panel-content-section-disabled"}">
            <a href="#" target="_self" onclick="${canEditDocInfo ? "Imcms.Admin.Panel.docs();" : ""}return false;">
                <div class="admin-panel-button">
                    <div class="admin-panel-button-image"></div>
                    <span class="admin-panel-button-description">Documents</span>
                </div>
            </a>
        </section>
        <div class="admin-panel-content-separator"></div>
        <section id="admin" data-mode="admin" class="admin-panel-content-section">
            <a href="${contextPath}/servlet/AdminManager" class="imcms-panel-safe-redirect" target="_self">
                <div class="admin-panel-button">
                    <div class="admin-panel-button-image"></div>
                    <span class="admin-panel-button-description">Admin</span>
                </div>
            </a>
        </section>
        <div class="admin-panel-content-separator"></div>
        <section id="logout" data-mode="logout" class="admin-panel-content-section">
            <a href="${contextPath}/servlet/LogOut" class="imcms-panel-safe-redirect" target="_self">
                <div class="admin-panel-button">
                    <div class="admin-panel-button-image"></div>
                    <span class="admin-panel-button-description">Logout</span>
                </div>
            </a>
        </section>
    </div>
</div>
