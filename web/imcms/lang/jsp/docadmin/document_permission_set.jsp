<%@ page import="imcode.server.Imcms,
                 imcode.server.user.UserDomainObject,
                 imcode.util.Utility,
                 imcode.util.IdNamePair,
                 org.apache.commons.lang.ArrayUtils,
                 org.apache.commons.lang.StringEscapeUtils,
                 imcode.server.document.*,
                 java.util.Map,
                 java.util.Iterator,
                 imcode.util.HttpSessionUtils,
                 imcode.server.document.textdocument.TextDocumentDomainObject,
                 com.imcode.imcms.flow.Page,
                 com.imcode.imcms.flow.OkCancelPage,
                 com.imcode.imcms.flow.*"%>
<%@page contentType="text/html"%><%@taglib uri="/WEB-INF/velocitytag.tld" prefix="vel"%><%
    DocumentPermissionSetPage documentPermissionSetPage = (DocumentPermissionSetPage)Page.fromRequest(request) ;
    DocumentPermissionSetDomainObject documentPermissionSet = documentPermissionSetPage.getDocumentPermissionSet() ;
    UserDomainObject user = Utility.getLoggedOnUser(request) ;
%><vel:velocity>
<html>
<head>

<title><? templates/sv/permissions/define_permissions.html/1 ?></title>

<link rel="stylesheet" type="text/css" href="$contextPath/imcms/css/imcms_admin.css.jsp">

</head>
<body bgcolor="#FFFFFF">

#gui_outer_start()
#gui_head( "<? global/imcms_administration ?>" )

<table border="0" cellspacing="0" cellpadding="0">
<form method="POST" action="PageDispatcher">
<%= Page.htmlHidden( request ) %>
<tr>
	<td><input type="submit" class="imcmsFormBtn" name="<%= OkCancelPage.REQUEST_PARAMETER__CANCEL %>" value="<? global/cancel ?>"></td>
	<td>&nbsp;</td>
    <td><input type="button" value="<? global/help ?>" class="imcmsFormBtn" onClick="openHelpW(41)"></td>
</tr>
</table>

#gui_mid()

<table border="0" cellspacing="0" cellpadding="2" width="660" align="center">
<tr>
<% if (documentPermissionSetPage.isForNew()) { %>
	<td colspan="3">#gui_heading( "<? templates/sv/permissions/define_permissions.html/permissions_for_restricted ?> <%= documentPermissionSet.getTypeId() %> <? templates/sv/permissions/define_permissions.html/permissions_for_restricted/new ?>" )</td>
<% } else { %>
	<td colspan="3">#gui_heading( "<? templates/sv/permissions/define_permissions.html/permissions_for_restricted ?> <%= documentPermissionSet.getTypeId() %> <? templates/sv/permissions/define_permissions.html/permissions_for_restricted/this ?>" )</td>
<% } %>
</tr>
<tr>
	<td class="imcmsAdmText"><? templates/sv/permissions/define_permission_1.html/1 ?></td>
	<td colspan="2"><input type="checkbox" name="<%= DocumentPermissionSetPage.REQUEST_PARAMETER__EDIT_DOCUMENT_INFORMATION %>" value="1" <% if( documentPermissionSet.getEditDocumentInformation()) { %>checked<% } %>></td>
</tr>
<tr>
	<td class="imcmsAdmText"><? templates/sv/permissions/define_permission_4.html/1 ?></td>
	<td colspan="2"><input type="checkbox" name="<%= DocumentPermissionSetPage.REQUEST_PARAMETER__EDIT_PERMISSIONS %>" value="1" <% if (documentPermissionSet.getEditPermissions() ) { %>checked<% } %>></td>
</tr>
<% if (documentPermissionSet instanceof TextDocumentPermissionSetDomainObject) {
    TextDocumentPermissionSetDomainObject textDocumentPermissionSet = (TextDocumentPermissionSetDomainObject)documentPermissionSet ;
    %>
    <tr>
        <td class="imcmsAdmText"><? templates/sv/permissions/define_permission_2_65536.html/1 ?></td>
        <td colspan="2"><input type="checkbox" name="<%= DocumentPermissionSetPage.REQUEST_PARAMETER__EDIT_TEXTS %>" value="1" <% if ( textDocumentPermissionSet.getEditTexts() ) { %>checked<% } %>></td>
    </tr>
    <tr>
        <td class="imcmsAdmText"><? templates/sv/permissions/define_permission_2_131072.html/1 ?></td>
        <td colspan="2"><input type="checkbox" name="<%= DocumentPermissionSetPage.REQUEST_PARAMETER__EDIT_IMAGES %>" value="1" <% if ( textDocumentPermissionSet.getEditImages() ) { %>checked<% } %>></td>
    </tr>
    <tr>
        <td class="imcmsAdmText"><? templates/sv/permissions/define_permission_2_1048576.html/1 ?></td>
        <td colspan="2"><input type="checkbox" name="<%= DocumentPermissionSetPage.REQUEST_PARAMETER__EDIT_INCLUDES %>" value="1" <% if( textDocumentPermissionSet.getEditIncludes() ) { %>checked<% } %>></td>
    </tr>
    <tr>
        <td class="imcmsAdmText"><? templates/sv/permissions/define_permission_2_262144.html/1 ?></td>
        <td class="imcmsAdmText"><input type="checkbox" name="<%= DocumentPermissionSetPage.REQUEST_PARAMETER__EDIT_MENUS %>" value="1" <% if ( textDocumentPermissionSet.getEditMenus() ) { %>checked<% } %>><? templates/sv/permissions/define_permission_2_262144.html/1001 ?></td>
        <td>
            <select name="<%= DocumentPermissionSetPage.REQUEST_PARAMETER__ALLOWED_DOCUMENT_TYPE_IDS %>" size="6" multiple>
                <%  int[] allowedDocumentTypeIds = textDocumentPermissionSet.getAllowedDocumentTypeIds() ;
                    Map allDocumentTypes = Imcms.getServices().getDocumentMapper().getAllDocumentTypeIdsAndNamesInUsersLanguage( user ) ;
                    for ( Iterator iterator = allDocumentTypes.entrySet().iterator(); iterator.hasNext(); ) {
                        Map.Entry entry = (Map.Entry)iterator.next();
                        Integer documentTypeId = (Integer)entry.getKey();
                        String documentTypeName = (String)entry.getValue();
                        boolean allowedDocumentType = ArrayUtils.contains( allowedDocumentTypeIds, documentTypeId.intValue() ) ;
                        %><option value="<%= documentTypeId %>" <% if( allowedDocumentType ) { %>selected<% } %>><%= StringEscapeUtils.escapeHtml( documentTypeName ) %></option><%
                    }
                %>
            </select>
        </td>
    </tr>
    <tr>
        <td class="imcmsAdmText"><? templates/sv/permissions/define_permission_2_524288.html/1 ?></td>
        <td class="imcmsAdmText"><input type="checkbox" name="<%= DocumentPermissionSetPage.REQUEST_PARAMETER__EDIT_TEMPLATES %>" value="1" <% if( textDocumentPermissionSet.getEditTemplates() ) { %>checked<% } %>><? templates/sv/permissions/define_permission_2_524288.html/1001 ?></td>
        <td>
        <select name="<%= DocumentPermissionSetPage.REQUEST_PARAMETER__ALLOWED_TEMPLATE_GROUP_IDS %>" size="6" multiple>
            <%
                TemplateGroupDomainObject[] allTemplateGroups = Imcms.getServices().getTemplateMapper().getAllTemplateGroups() ;
                TemplateGroupDomainObject[] allowedTemplateGroups = textDocumentPermissionSet.getAllowedTemplateGroups() ;
                for ( int i = 0; i < allTemplateGroups.length; i++ ) {
                    TemplateGroupDomainObject templateGroup = allTemplateGroups[i];
                    boolean allowedTemplateGroup = ArrayUtils.contains(allowedTemplateGroups, templateGroup) ;
                    %><option value="<%= templateGroup.getId() %>" <% if( allowedTemplateGroup ) { %>selected<% } %>><%= templateGroup.getName() %></option><%
                }
            %>
        </select></td>
    </tr>
    <% if (documentPermissionSetPage.isForNew()) { %>
        <tr>
            <td class="imcmsAdmText"><? templates/sv/docinfo/default_templates.html/2 ?></td>
            <td class="imcmsAdmText">
                <select name="<%= DocumentPermissionSetPage.REQUEST_PARAMETER__DEFAULT_TEMPLATE_ID %>">
                    <option value=""><? templates/sv/docinfo/default_templates_1.html/2 ?></option>
                    <%
                        TemplateDomainObject[] allTemplates = Imcms.getServices().getTemplateMapper().getAllTemplates() ;
                        TemplateDomainObject defaultTemplate = textDocumentPermissionSet.getDefaultTemplate() ;
                        for ( int i = 0; i < allTemplates.length; i++ ) {
                            TemplateDomainObject template = allTemplates[i];
                            boolean selected = template.equals( defaultTemplate ) ;
                            %><option value="<%= template.getId() %>" <% if (selected) { %>selected<% } %>><%= StringEscapeUtils.escapeHtml( template.getName() )%></option><%
                        }
                    %>
                </select>
            </td>
        </tr>
    <% } %>
<% } else { %>
    <tr>
        <td class="imcmsAdmText"><? templates/sv/permissions/define_permission_5_65536.html/1 ?></td>
        <td colspan="2"><input type="checkbox" name="<%= DocumentPermissionSetPage.REQUEST_PARAMETER__EDIT %>" value="1" <% if( documentPermissionSet.getEdit() ) { %>checked<% } %>></td>
    </tr>
<% } %>
<tr>
	<td colspan="3">#gui_hr( "blue" )</td>
</tr>
<tr>
	<td colspan="3" align="right">
	<table border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td><input type="submit" class="imcmsFormBtn" name="<%= OkCancelPage.REQUEST_PARAMETER__OK %>" value="<? templates/sv/permissions/define_permissions.html/2004 ?>"></td>
		<td>&nbsp;</td>
		<td><input type="reset" class="imcmsFormBtn" name="Reset" value="<? templates/sv/permissions/define_permissions.html/2005 ?>"></td>
		<td>&nbsp;</td>
		<td><input type="submit" class="imcmsFormBtn" name="<%= OkCancelPage.REQUEST_PARAMETER__CANCEL %>" value="<? templates/sv/permissions/define_permissions.html/2006 ?>"></td>
	</tr>
	</table></td>
</tr>
</form>
</table>
#gui_bottom()
#gui_outer_end()

</body>
</html>
</vel:velocity>
