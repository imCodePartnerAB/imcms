<%@ page pageEncoding="UTF-8"%>
<%@ page import="com.imcode.imcms.util.l10n.LocalizedMessageFormat" %>
<%@ page import="com.imcode.imcms.servlet.PasswordReset" %>
<%@ page import="imcode.server.Imcms" %>
<jsp:include page="inc_header.jsp" flush="true"/>

<div style="width:310px;">
	<%= new LocalizedMessageFormat("passwordreset.confirmation.email_sent",
					request.getParameter(PasswordReset.REQUEST_USER_IDENTITY),
					Imcms.getServices().getSystemData().getServerMasterAddress()).toLocalizedString(request)
	%>
</div>

<jsp:include page="inc_footer.jsp" flush="true"/>