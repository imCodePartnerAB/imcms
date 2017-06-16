<%@ page
	
	import="com.imcode.imcms.servlet.PasswordReset,
	        com.imcode.imcms.util.l10n.LocalizedMessage"
	
  pageEncoding="UTF-8"
  
%><%!

static final LocalizedMessage formInfo = new LocalizedMessage("passwordreset.password_reset_form_info");
static final LocalizedMessage formLabelPassword = new LocalizedMessage("passwordreset.password_reset_form.lbl_password");
static final LocalizedMessage formLabelPasswordCheck = new LocalizedMessage("passwordreset.password_reset_form.lbl_password_check");
static final LocalizedMessage formSubmit = new LocalizedMessage("passwordreset.password_reset_form.submit");

%><jsp:include page="inc_header.jsp" flush="true"/>

<div style="width:310px;">
	<p>
		<%=formInfo.toLocalizedString(request)%>
	</p>
	
	<form method="POST" action="<%= request.getContextPath() %>/servlet/PasswordReset">
		<input type="hidden" name="<%=PasswordReset.REQUEST_PARAM_OP%>" value="<%=PasswordReset.Op.SAVE_NEW_PASSWORD%>"/>
		<input type="hidden" name="<%=PasswordReset.REQUEST_PARAM_RESET_ID%>" value="<%=request.getParameter(PasswordReset.REQUEST_PARAM_RESET_ID)%>"/>
	
		<table border="0" cellspacing="0" cellpadding="0" width="100%">
		<tr>
			<td width="50%" style="white-space:nowrap;"><%=formLabelPassword.toLocalizedString(request)%></td>
			<td width="50%" style="padding: 0 10px;"><input type="password" name="<%=PasswordReset.REQUEST_PARAM_PASSWORD%>" style="width:100%;"></td>
		</tr>
		<tr>
			<td style="white-space:nowrap;"><%=formLabelPasswordCheck.toLocalizedString(request)%></td>
			<td style="padding: 0 10px;"><input type="password" name="<%=PasswordReset.REQUEST_PARAM_PASSWORD_CHECK%>" style="width:100%;"></td>
		</tr>
		<tr>
			<td colspan="2" align="right" style="padding-top:10px;"><input type="submit" class="imcmsFormBtnSmall" value="<%=formSubmit.toLocalizedString(request)%>"/></td>
		</tr>
		</table>
	</form>
	
	<%--<p>--%>
	<%--Secure password tips:--%>
	<%--<ul>--%>
		<%--<li>Use at least 8 characters, a combination of numbers and letters is best.</li>--%>
		<%--<li>Do not use the same password you have used with us previously.</li>--%>
		<%--<li>Do not use dictionary words, your name, e-mail address, or other personal information that can be easily obtained.</li>--%>
		<%--<li>Do not use the same password for multiple online accounts.</li>--%>
	<%--</ul>--%>
</div>

<jsp:include page="inc_footer.jsp" flush="true"/>