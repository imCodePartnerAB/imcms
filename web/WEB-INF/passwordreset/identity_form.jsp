<%@ page
	
	import="com.imcode.imcms.servlet.PasswordReset,
	        com.imcode.imcms.util.l10n.LocalizedMessage"
	
  pageEncoding="UTF-8"
%><%!

static final LocalizedMessage formInfo = new LocalizedMessage("passwordreset.identity_form_info");
static final LocalizedMessage formLabelEmail = new LocalizedMessage("passwordreset.identity_form.lbl_identity");
static final LocalizedMessage formSubmit = new LocalizedMessage("passwordreset.identity_form.submit");

%>
<jsp:include page="inc_header.jsp" flush="true"/>

<div style="width:310px;">
	<p>
		<%= formInfo.toLocalizedString(request) %>
	</p>

	<form method="POST" action="<%= request.getContextPath() %>/servlet/PasswordReset">
		<input type="hidden" name="<%=PasswordReset.REQUEST_PARAM_OP%>" value="<%=PasswordReset.Op.SEND_RESET_URL%>"/>
		
		<table border="0" cellspacing="0" cellpadding="0" width="100%">
		<tr>
			<td width="20%"><%=formLabelEmail.toLocalizedString(request)%></td>
			<td width="60%" style="padding: 0 10px;"><input type="text" name="<%=PasswordReset.REQUEST_USER_IDENTITY%>" style="width:100%;"></td>
			<td width="20%" align="right"><input type="submit" class="imcmsFormBtnSmall" value="<%=formSubmit.toLocalizedString(request)%>" /></td>
		</tr>
		</table>
	</form>
</div>

<jsp:include page="inc_footer.jsp" flush="true"/>