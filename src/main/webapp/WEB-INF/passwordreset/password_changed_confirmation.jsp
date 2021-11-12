<%@ page
		pageEncoding="UTF-8"
		import="com.imcode.imcms.util.l10n.LocalizedMessage"
%>
<%!

	static final LocalizedMessage confirmation = new LocalizedMessage("passwordreset.confirmation.password_changed");
	static final LocalizedMessage lnkLoginPage = new LocalizedMessage("passwordreset.link.login_page");

%>
<jsp:include page="inc_header.jsp" flush="true"/>

<div class="imcms-modal-admin-body">
	<div class="imcms-field">
		<div class="imcms-title">
			<%= confirmation.toLocalizedStringByIso639_1((String) request.getAttribute("userLanguage")) %>
		</div>
	</div>
	<div class="imcms-field">
		<div class="imcms-title">
			<a class="imcms-button imcms-button--neutral imcms-info-body__button" href="${contextPath}/login">
				<%= lnkLoginPage.toLocalizedStringByIso639_1((String) request.getAttribute("userLanguage")) %>
			</a>
		</div>
	</div>
</div>

<jsp:include page="inc_footer.jsp" flush="true"/>