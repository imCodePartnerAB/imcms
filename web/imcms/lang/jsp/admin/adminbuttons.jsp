<%@ page
	
	import="imcode.server.document.DocumentDomainObject,
	        imcode.server.document.DocumentPermissionSetDomainObject,
	        imcode.server.document.TextDocumentPermissionSetDomainObject,
	        imcode.server.document.textdocument.TextDocumentDomainObject,
	        imcode.server.user.UserDomainObject,
	        imcode.util.Html,
	        imcode.util.Utility, org.apache.commons.lang.StringUtils, org.apache.commons.lang.StringEscapeUtils, imcode.server.document.DocumentTypeDomainObject"
	
	contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"
	
%><%@ taglib uri="imcmsvelocity" prefix="vel"
%><%

UserDomainObject user = (UserDomainObject)request.getAttribute("user") ;
DocumentDomainObject document = (DocumentDomainObject)request.getAttribute("document") ;
DocumentPermissionSetDomainObject documentPermissionSet = user.getPermissionSetFor( document ) ;

boolean isTextDocument = (DocumentTypeDomainObject.TEXT == document.getDocumentType()) ;

%><vel:velocity>
<div id="adminPanelDiv">
	<table border="0" cellspacing="0" cellpadding="2" class="adminPanelTable">
	<tr>
		<td class="adminPanelTd1">
		<table border="0" cellspacing="0" cellpadding="0" style="width:100%;">
		<tr>
			<td id="adminPanelTd1_1" width="25%" nowrap="nowrap">
			<span class="adminPanelLogo" ondblclick="window.open('http://www.imcms.net/')"><? templates/sv/adminbuttons/adminbuttons.html/1 ?> &nbsp;</span></td>
			<td id="adminPanelTd1_2" width="50%" align="center" nowrap="nowrap">
			<span class="adminPanelText">
			<span title="<? web/imcms/lang/jsp/admin/adminbuttons.jsp/title_id ?>"><b>Id:</b> <%= document.getId() %></span> &nbsp;
			<span title="<? web/imcms/lang/jsp/admin/adminbuttons.jsp/title_alias ?>"><b>Alias:</b> <%=
			(null != document.getAlias()) ? "<span title=\"" + StringEscapeUtils.escapeHtml(document.getAlias()) + "\">" + StringUtils.abbreviate(document.getAlias(), (isTextDocument ? 37 : 23)) + "</span>" : "-" %></span><%
			if (!isTextDocument) { %> &nbsp;
			<span title="<? web/imcms/lang/jsp/admin/adminbuttons.jsp/title_type ?>"><b><? templates/sv/adminbuttons/adminbuttons.html/1001 ?>:</b>
			<%= document.getDocumentTypeName().toLocalizedString( request ) %></span><%
			} %> &nbsp;</span></td>
			<td id="adminPanelTd1_3" width="25%" align="right"><%= Html.getLinkedStatusIconTemplate( document, user, request ) %></td>
		</tr>
		</table></td>
	</tr>
	<tr>
		<td class="adminPanelTd2" align="center" nowrap="nowrap">
		<a href="$contextPath/servlet/BackDoc" id="admHrefBackdoc"><img src="$contextPath/imcms/$language/images/admin/adminbuttons/foregaende.gif"<%
				%> alt="<? templates/sv/adminbuttons/adminbuttons.html/2001 ?>"<%
				%> title="<? templates/sv/adminbuttons/adminbuttons.html/2001 ?>" id="admBtnBackdoc" border="0" /></a><%
        if (user.canEdit( document )) {
            %><a href="<%= Utility.getAbsolutePathToDocument( request, document ) %>" id="admHrefNormal"><img src="$contextPath/imcms/$language/images/admin/adminbuttons/normal.gif"<%
				  %> alt="<? templates/sv/adminbuttons/adminbutton2_0.html/2001 ?>"<%
				  %> title="<? templates/sv/adminbuttons/adminbutton2_0.html/2001 ?>" id="admBtnNormal" border="0" /></a><%
        }
        if( document instanceof TextDocumentDomainObject) {
            TextDocumentPermissionSetDomainObject textDocumentPermissionSet = (TextDocumentPermissionSetDomainObject)documentPermissionSet ;
            if( textDocumentPermissionSet.getEditTexts() ) {
                %><a href="$contextPath/servlet/AdminDoc?meta_id=<%= document.getId() %>&amp;flags=65536" id="admHrefText"><img src="$contextPath/imcms/$language/images/admin/adminbuttons/btn_text.gif"<%
              %> alt="<? templates/sv/adminbuttons/adminbutton2_65536.html/2001 ?>"<%
              %> title="<? templates/sv/adminbuttons/adminbutton2_65536.html/2001 ?>" id="admBtnText" border="0" /></a><%
            }
            if( textDocumentPermissionSet.getEditImages() ) {
                %><a href="$contextPath/servlet/AdminDoc?meta_id=<%= document.getId() %>&amp;flags=131072" id="admHrefBild"><img src="$contextPath/imcms/$language/images/admin/adminbuttons/btn_image.gif"<%
              %> alt="<? templates/sv/adminbuttons/adminbutton2_131072.html/2001 ?>"<%
              %> title="<? templates/sv/adminbuttons/adminbutton2_131072.html/2001 ?>" id="admBtnBild" border="0" /></a><%
            }
            if( textDocumentPermissionSet.getEditMenus() ) {
                %><a href="$contextPath/servlet/AdminDoc?meta_id=<%= document.getId() %>&amp;flags=262144" id="admHrefLank"><img src="$contextPath/imcms/$language/images/admin/adminbuttons/meny.gif"<%
              %> alt="<? templates/sv/adminbuttons/adminbutton2_262144.html/2001 ?>"<%
              %> title="<? templates/sv/adminbuttons/adminbutton2_262144.html/2001 ?>" id="admBtnLank" border="0" /></a><%
            }
            if( textDocumentPermissionSet.getEditTemplates() ) {
                %><a href="$contextPath/servlet/AdminDoc?meta_id=<%= document.getId() %>&amp;flags=524288" id="admHrefUtseende"><img src="$contextPath/imcms/$language/images/admin/adminbuttons/utseende.gif"<%
              %> alt="<? templates/sv/adminbuttons/adminbutton2_524288.html/2001 ?>"<%
              %> title="<? templates/sv/adminbuttons/adminbutton2_524288.html/2001 ?>" id="admBtnUtseende" border="0" /></a><%
            }
            if( textDocumentPermissionSet.getEditIncludes() ) {
                %><a href="$contextPath/servlet/AdminDoc?meta_id=<%= document.getId() %>&amp;flags=1048576" id="admHrefInclude"><img src="$contextPath/imcms/$language/images/admin/adminbuttons/include.gif"<%
              %> alt="<? templates/sv/adminbuttons/adminbutton2_1048576.html/2001 ?>"<%
              %> title="<? templates/sv/adminbuttons/adminbutton2_1048576.html/2001 ?>" id="admBtnInclude" border="0" /></a><%
            }
        } else {
            if( documentPermissionSet.getEdit() ) {
                %><a href="$contextPath/servlet/AdminDoc?meta_id=<%= document.getId() %>&amp;flags=65536" id="admHrefRedigera"><img src="$contextPath/imcms/$language/images/admin/adminbuttons/redigera.gif"<%
              %> alt="<? templates/sv/adminbuttons/adminbutton7_65536.html/2001 ?>"<%
              %> title="<? templates/sv/adminbuttons/adminbutton7_65536.html/2001 ?>" id="admBtnRedigera" border="0" /></a><%
            }
        }
        if( documentPermissionSet.getEditDocumentInformation() ) {
                %><a href="$contextPath/servlet/AdminDoc?meta_id=<%= document.getId() %>&amp;flags=1" id="admHrefDokinfo"><img src="$contextPath/imcms/$language/images/admin/adminbuttons/dokinfo.gif"<%
            %> alt="<? templates/sv/adminbuttons/adminbutton_1.html/2001 ?>"<%
            %> title="<? templates/sv/adminbuttons/adminbutton_1.html/2001 ?>" id="admBtnDokinfo" border="0" /></a><%
        }
        if( documentPermissionSet.getEditPermissions() ) {
                %><a href="$contextPath/servlet/AdminDoc?meta_id=<%= document.getId() %>&amp;flags=4" id="admHrefRattigheter"><img src="$contextPath/imcms/$language/images/admin/adminbuttons/rattigheter.gif"<%
            %> alt="<? templates/sv/adminbuttons/adminbutton_4.html/2001 ?>"<%
            %> title="<? templates/sv/adminbuttons/adminbutton_4.html/2001 ?>" id="admBtnRattigheter" border="0" /></a><%
        }
        if ( !user.isDefaultUser() ) {
            %><a href="$contextPath/servlet/LogOut" id="admHrefLoggaut"><img src="$contextPath/imcms/$language/images/admin/adminbuttons/loggaut.gif"<%
            %> alt="<? templates/sv/adminbuttons/adminbuttons.html/2002 ?>"<%
            %> title="<? templates/sv/adminbuttons/adminbuttons.html/2002 ?>" id="admBtnLoggaut" border="0" /></a><%
        }
        if ( user.canAccessAdminPages() ) {
            %><a href="$contextPath/servlet/AdminManager" target="_blank" id="admHrefAdmin"><img src="$contextPath/imcms/$language/images/admin/adminbuttons/admin.gif"<%
            %> alt="<? templates/sv/adminbuttons/superadminbutton.html/2001 ?>"<%
            %> title="<? templates/sv/adminbuttons/superadminbutton.html/2001 ?>" id="admBtnAdmin" border="0" /></a><%
        }
        %><a href="@documentationurl@/Help?name=HelpStart&amp;lang=$language" target="_blank" onclick="openHelpW('HelpStart'); return false"  id="admHrefHelp"><img src="$contextPath/imcms/$language/images/admin/adminbuttons/help.gif"<%
            %> alt="<? templates/sv/adminbuttons/adminbuttons.html/2003 ?>"<%
            %> title="<? templates/sv/adminbuttons/adminbuttons.html/2003 ?>" id="admBtnHelp" border="0" /></a></td>
	</tr>
	</table>
</div>
</vel:velocity>