<%@ page
		contentType="text/html; charset=UTF-8"
		import="com.imcode.imcms.api.DocumentLanguage"
		%>
<%@ page import="com.imcode.imcms.servlet.Version" %>
<%@ page import="imcode.server.Imcms" %>
<%@ page import="imcode.server.ImcmsConstants" %>
<%@ page import="imcode.server.document.DocumentDomainObject" %>
<%@ page import="imcode.server.user.UserDomainObject" %>
<%@ page import="org.apache.commons.lang3.StringUtils" %>
<%
	UserDomainObject user = (UserDomainObject) request.getAttribute("user");
	DocumentDomainObject document = (DocumentDomainObject) request.getAttribute("document");
	if (!user.canEdit(document)) return;
	Boolean canEditDocumentInfo = user.getPermissionSetFor(document).getEditDocumentInformation();
	boolean editMode = ("" + ImcmsConstants.PERM_EDIT_TEXT_DOCUMENT_TEXTS).equals(request.getParameter("flags"));
    boolean previewMode = false; // todo: fix preview mode definition
	String contextPath = request.getContextPath();
	String imcmsVersion = Version.getImcmsVersion(getServletConfig().getServletContext()).replace("imCMS", "<span>imCMS</span>");
	DocumentLanguage currentLanguage = Imcms.getUser().getDocGetterCallback().getLanguage();
%>
<div class="admin-panel reset">
	<div class="admin-panel-draggable"></div>
	<div class="admin-panel-content">
		<section id="languages" class="admin-panel-content-section  admin-panel-content-section-language">
			<div class="admin-panel-version"><%=imcmsVersion%>
			</div>
			<div class="admin-panel-language">
				<a href="<%=contextPath%>/servlet/GetDoc?meta_id=<%=document.getId()%>&lang=en"
				   title="English/English (default current)"
				   class="<%=currentLanguage.getCode().equals("en")?"active":""%>">
					<img src="<%=contextPath%>/images/ic_english.png" alt="" style="border:0;">
				</a>
				<a href="<%=contextPath%>/servlet/GetDoc?meta_id=<%=document.getId()%>&lang=sv"
				   title="Swedish/Svenska"
				   class="<%=currentLanguage.getCode().equals("sv")?"active":""%>">
					<img src="<%=contextPath%>/images/ic_swedish.png" alt="" style="border:0;">
				</a>
			</div>
		</section>
		<section id="read" data-mode="readonly" class="admin-panel-content-section <%=editMode||previewMode?"":"active"%>">
			<a href="<%=contextPath%>/<%=StringUtils.defaultString(document.getAlias(), String.valueOf(document.getId()))%>"
			   target="_self">
				<div class="admin-panel-button">
					<div class="admin-panel-button-image"></div>
					<span class="admin-panel-button-description">Public</span>
				</div>
			</a>
		</section>
		<section id="edit" data-mode="edit" class="admin-panel-content-section <%=editMode?"active":""%>">
			<a href="<%=contextPath%>/servlet/AdminDoc?meta_id=<%=document.getId()%>&flags=<%=ImcmsConstants.PERM_EDIT_TEXT_DOCUMENT_TEXTS%>"
               target="_self">
				<div class="admin-panel-button">
					<div class="admin-panel-button-image"></div>
					<span class="admin-panel-button-description">Edit</span>
				</div>
			</a>
		</section>
        <section id="preview" data-mode="preview" class="admin-panel-content-section <%=previewMode?"active":""%>">
            <a href="<%=contextPath%>/<%=StringUtils.defaultString(document.getAlias(), String.valueOf(document.getId()))%>"
               target="_self">
                <div class="admin-panel-button">
                    <div class="admin-panel-button-image"></div>
                    <span class="admin-panel-button-description">Preview</span>
                </div>
            </a>
        </section>
        <section id="publish" data-mode="publish" class="admin-panel-content-section">
            <a href="<%=contextPath%>/servlet/AdminDoc?meta_id=<%=document.getId()%>&flags=<%=ImcmsConstants.DISPATCH_FLAG__PUBLISH%>"
               target="_self">
                <div class="admin-panel-button">
                    <div class="admin-panel-button-image">Publish offline version</div>
                </div>
            </a>
        </section>
        <div class="admin-panel-content-separator"></div>
		<section id="info" data-mode="info"
				 class="admin-panel-content-section <%= canEditDocumentInfo?"":"admin-panel-content-section-disabled"%>">
			<a href="#" target="_self" onclick="<%= canEditDocumentInfo?"pageInfo();":""%> return false;">
				<div class="admin-panel-button">
					<div class="admin-panel-button-image"></div>
					<span class="admin-panel-button-description">Page info</span>
				</div>
			</a>
		</section>
		<section id="additionalInfo" data-mode="info"
				 class="admin-panel-content-section <%= canEditDocumentInfo?"":"admin-panel-content-section-disabled"%>">
			<a href="#" target="_self" onclick="return false;">
				<div class="admin-panel-button">
					<div>
						<span><%=document.getId()%></span> <%=document.getLifeCyclePhase().toString().substring(0, 1).toUpperCase()%>
					</div>
				</div>
			</a>
		</section>
		<div class="admin-panel-content-separator"></div>
		<section id="docs" data-mode="docs"
				 class="admin-panel-content-section <%= canEditDocumentInfo?"":"admin-panel-content-section-disabled"%>">
			<a href="#" target="_self" onclick="<%= canEditDocumentInfo?"Imcms.Admin.Panel.docs();":""%> return false;">
				<div class="admin-panel-button">
					<div class="admin-panel-button-image"></div>
					<span class="admin-panel-button-description">Documents</span>
				</div>
			</a>
		</section>
		<div class="admin-panel-content-separator"></div>
		<section id="admin" data-mode="admin" class="admin-panel-content-section">
			<a href="<%=contextPath%>/servlet/AdminManager" target="_self">
				<div class="admin-panel-button">
					<div class="admin-panel-button-image"></div>
					<span class="admin-panel-button-description">Admin</span>
				</div>
			</a>
		</section>
		<div class="admin-panel-content-separator"></div>
		<section id="logout" data-mode="logout" class="admin-panel-content-section">
			<a href="<%=contextPath%>/servlet/LogOut" target="_self">
				<div class="admin-panel-button">
					<div class="admin-panel-button-image"></div>
					<span class="admin-panel-button-description">Logout</span>
				</div>
			</a>
		</section>
	</div>
</div>
