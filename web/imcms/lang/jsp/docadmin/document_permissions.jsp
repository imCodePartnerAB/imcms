<%@ page import="imcode.server.document.DocumentDomainObject,
                 imcode.server.user.RoleDomainObject,
                 imcode.server.Imcms,
                 java.util.*,
                 imcode.server.user.UserDomainObject,
                 imcode.util.Utility,
                 imcode.util.Html,
                 imcode.server.document.DocumentPermissionSetDomainObject,
                 imcode.util.HttpSessionUtils,
                 com.imcode.imcms.flow.*,
                 imcode.server.document.TemplateDomainObject,
                 imcode.server.document.textdocument.TextDocumentDomainObject,
                 com.imcode.imcms.flow.Page"%>
<%@page contentType="text/html"%><%@taglib uri="/WEB-INF/velocitytag.tld" prefix="vel"%><%!
    String formatRolePermissionRadioButton( int radioButtonPermissionSetId, UserDomainObject user, int permissionSetId,
                                            RoleDomainObject role,
                                            DocumentDomainObject document ) {
        boolean checked = permissionSetId == radioButtonPermissionSetId;
        String name = "role_" + role.getId();
        String value = "" + radioButtonPermissionSetId;
        if (user.canSetPermissionSetIdForRoleOnDocument( radioButtonPermissionSetId, role, document )) {
            return Html.radio(name, value, checked ) ;
        } else {
            return checked ? Html.hidden( name, value )+"X" : "O" ;
        }
    }
%><%

    DocumentPermissionsPage documentPermissionsPage = (DocumentPermissionsPage)Page.fromRequest(request) ;
    DocumentDomainObject document = documentPermissionsPage.getDocument() ;
    UserDomainObject user = Utility.getLoggedOnUser( request );
%><vel:velocity>
<html>
<head>
<title><? templates/sv/docinfo/change_meta_rights.html/1 ?></title>
<link rel="stylesheet" type="text/css" href="$contextPath/imcms/css/imcms_admin.css.jsp">
</head>
<body bgcolor="#FFFFFF">

#gui_outer_start()
#gui_head( "<? global/imcms_administration ?>" )

<form method="POST" action="PageDispatcher">
<%= Page.htmlHidden( request ) %>
    <table border="0" cellspacing="0" cellpadding="0">
        <tr>
            <td><input type="submit" name="<%= PageFlow.REQUEST_PARAMETER__CANCEL_BUTTON %>" class="imcmsFormBtn" value="<? templates/sv/docinfo/change_meta_rights.html/2001 ?>"></td>
            <td>&nbsp;</td>
            <td><input type="button" value="<? templates/sv/docinfo/change_meta_rights.html/2002 ?>" title="<? templates/sv/docinfo/change_meta_rights.html/2003 ?>" class="imcmsFormBtn" onClick="openHelpW(84)"></td>
        </tr>
    </table>
#gui_mid()

    <table border="0" cellspacing="0" cellpadding="2" width="660" align="center">
        <tr>
            <td colspan="2">#gui_heading( "<? templates/sv/docinfo/change_meta_rights.html/5/1 ?> <%= document.getId() %>" )</td>
        </tr>
        <tr>
            <td class="imcmsAdmText"><? templates/sv/docinfo/change_meta_rights.html/1001 ?></td>
            <td class="imcmsAdmText">
                <table border="0" cellspacing="0" cellpadding="0" width="90%">
                <tr align="center">
                    <td class="imcmsAdmText" colspan="2" align="left"><? templates/sv/permissions/roles_rights_table_head.html/1 ?></td>
                    <td class="imcmsAdmText" width="15%"><? templates/sv/permissions/roles_rights_table_head.html/2 ?></td>
                    <td class="imcmsAdmText" width="15%"><? templates/sv/permissions/roles_rights_table_head.html/3 ?></td>
                    <td class="imcmsAdmText" width="15%"><? templates/sv/permissions/roles_rights_table_head.html/4 ?></td>
                    <td class="imcmsAdmText" width="15%"><? templates/sv/permissions/roles_rights_table_head.html/5 ?></td>
                    <td class="imcmsAdmText" width="15%"><? templates/sv/permissions/roles_rights_table_head.html/6 ?></td>
                </tr>
                <%
                    SortedSet allRoles = new TreeSet(Arrays.asList(Imcms.getServices().getImcmsAuthenticatorAndUserAndRoleMapper().getAllRoles())) ;
                    Map rolesMappedToPermissionSetIds = document.getRolesMappedToPermissionSetIds() ;
                    for ( Iterator iterator = rolesMappedToPermissionSetIds.entrySet().iterator(); iterator.hasNext(); ) {
                        Map.Entry entry = (Map.Entry)iterator.next();
                        RoleDomainObject role = (RoleDomainObject)entry.getKey();
                        int permissionSetId = ((Integer)entry.getValue()).intValue() ;
                        if (DocumentPermissionSetDomainObject.TYPE_ID__NONE == permissionSetId) {
                            continue ;
                        }
                        allRoles.remove( role ) ;
                        if (role.equals( RoleDomainObject.SUPERADMIN )) {
                            continue ;
                        }
                        %>
                        <tr align="center">
                            <td height="22" class="imcmsAdmText" align="left"><% if (user.hasRole( role )) { %>*<% } else { %>&nbsp;<% } %></td>
                            <td class="imcmsAdmText" align="left"><%= role.getName() %></td>
                            <td><%= formatRolePermissionRadioButton( DocumentPermissionSetDomainObject.TYPE_ID__NONE, user, permissionSetId, role, document ) %></td>
                            <td><%= formatRolePermissionRadioButton( DocumentPermissionSetDomainObject.TYPE_ID__READ, user, permissionSetId, role, document ) %></td>
                            <td><%= formatRolePermissionRadioButton( DocumentPermissionSetDomainObject.TYPE_ID__RESTRICTED_2, user, permissionSetId, role, document ) %></td>
                            <td><%= formatRolePermissionRadioButton( DocumentPermissionSetDomainObject.TYPE_ID__RESTRICTED_1, user, permissionSetId, role, document ) %></td>
                            <td><%= formatRolePermissionRadioButton( DocumentPermissionSetDomainObject.TYPE_ID__FULL, user, permissionSetId, role, document ) %></td>
                        </tr>
                        <%
                    } %>
                </table>
            </td>
        </tr>
        <tr>
            <td class="imcmsAdmText"><? templates/sv/docinfo/change_meta_rights.html/7 ?></td>
            <td>
            <table border="0" cellspacing="0" cellpadding="2">
                <tr>
                    <td align="right"><input type="submit" class="imcmsFormBtnSmall" name="add_roles" value="<? templates/sv/docinfo/change_meta_rights.html/2004 ?>" style="width:130" ></td>
                    <td class="imcmsAdmDim">&nbsp; <? templates/sv/docinfo/change_meta_rights.html/1002 ?></td>
                </tr>
                <tr>
                    <td colspan="2">
                        <select name="<%= DocumentPermissionsPage.REQUEST_PARAMETER__ROLES_WITHOUT_PERMISSIONS %>" size="4" multiple>
                            <%
                                for ( Iterator iterator = allRoles.iterator(); iterator.hasNext(); ) {
                                    RoleDomainObject role = (RoleDomainObject)iterator.next();
                                    %><%= Html.option( ""+role.getId(), role.getName(), false ) %><%
                                }
                            %>
                        </select>
                    </td>
                </tr>
            </table>
        </tr>
        <% if (user.canDefineRestrictedOneFor( document )) { %>
            <tr>
                <td colspan="2">&nbsp;<br>#gui_heading( "<? templates/sv/docinfo/change_meta_rights.html/9/1 ?>" )</td>
            </tr>
            <tr>
                <td class="imcmsAdmText"><? templates/sv/permissions/set_1_button.html/1 ?></td>
                <td>
                    <input type="submit" class="imcmsFormBtnSmall" name="define_set_1" value="<? templates/sv/permissions/set_1_button.html/2001 ?>">
                    <input type="submit" class="imcmsFormBtnSmall" name="define_new_set_1" value="<? templates/sv/permissions/new_set_1_button.html/2001 ?>">
                </td>
            </tr>
            <% if (user.canDefineRestrictedTwoFor(document)) { %>
                <tr>
                    <td>
                        <? templates/sv/permissions/set_2_button.html/1 ?>
                    </td>
                    <td>
                        <input type="submit" class="imcmsFormBtnSmall" name="define_set_2" value="<? templates/sv/permissions/set_2_button.html/2001 ?>">
                        <input type="submit" class="imcmsFormBtnSmall" name="define_new_set_2" value="<? templates/sv/permissions/new_set_2_button.html/2001 ?>">
                    </td>
                </tr>
            <% } %>
            <% if (user.isSuperAdminOrHasFullPermissionOn(document)) { %>
                <tr>
                    <td class="imcmsAdmText">
                        <? templates/sv/permissions/sets_precedence.html/precedence ?>
                    </td>
                    <td class="imcmsAdmText">
                        <input type="CHECKBOX" name="<%= DocumentPermissionsPage.REQUEST_PARAMETER__RESTRICTED_ONE_MORE_PRIVILEGED_THAN_RESTRICTED_TWO %>" value="1" <% if (document.isRestrictedOneMorePrivilegedThanRestrictedTwo()) { %>checked<% } %>>
                        <? templates/sv/permissions/sets_precedence.html/1001 ?>
                    </td>
                </tr>
            <% } %>
        <% } %>
        <% if (document instanceof TextDocumentDomainObject) {
            TextDocumentDomainObject textDocument = (TextDocumentDomainObject)document ; %>
            <tr>
                <td colspan="2">#gui_hr( "cccccc" )</td>
            </tr>
            <tr>
                <td class="imcmsAdmText"><? templates/sv/docinfo/default_templates.html/2 ?></td>
                <td class="imcmsAdmText">
                    <select name="<%= DocumentPermissionsPage.REQUEST_PARAMETER__DEFAULT_TEMPLATE_ID %>">
                        <option value=""><? templates/sv/docinfo/default_templates_1.html/2 ?></option>
                        <%
                            TemplateDomainObject defaultTemplate = textDocument.getDefaultTemplate();
                            TemplateDomainObject[] allTemplates = Imcms.getServices().getTemplateMapper().getAllTemplates() ;
                            for ( int i = 0; i < allTemplates.length; i++ ) {
                                TemplateDomainObject template = allTemplates[i];
                                %><%= Html.option( ""+template.getId(), template.getName(), template.equals( defaultTemplate ))%><%
                            } %>
                    </select>
                </td>
            </tr>
        <% } %>
        <tr>
            <td colspan="2">#gui_hr( "blue" )</td>
        </tr>
        <tr>
            <td class="imcmsAdmText"><? templates/sv/docinfo/change_meta_rights.html/12 ?></td>
            <td>
            <table border="0" cellspacing="0" cellpadding="0" width="100%">
            <tr>
                <td>
                <table border="0" cellspacing="0" cellpadding="0">
                <tr>
                    <td><input type="CHECKBOX" name="show_meta" value="1" <% if (document.isVisibleInMenusForUnauthorizedUsers()) {%>checked<% } %>></td>
                    <td class="imcmsAdmText">&nbsp;<? templates/global/pageinfo/ShowLinkToUnuthorizedUser ?></td>
                </tr>
                <tr>
                    <td><input type="CHECKBOX" name="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__LINKABLE_BY_OTHER_USERS %>" value="1" <% if (document.isLinkableByOtherUsers()) {%>checked<% } %>></td>
                    <td class="imcmsAdmText">&nbsp;<? templates/global/pageinfo/share ?></td>
                </tr>
                </table>
                </td>
                <td class="imcmsAdmText" nowrap><? templates/sv/docinfo/change_meta_rights.html/1004 ?>&nbsp;<i><%= Utility.formatUser(document.getCreator()) %></i></td>
            </tr>
            </table></td>
        </tr>
        <tr>
            <td colspan="2">#gui_hr( "blue" )</td>
        </tr>
        <tr>
            <td>&nbsp;</td>
            <td align="right">
            <table border="0" cellspacing="0" cellpadding="0">
            <tr>
                <td><input type="SUBMIT" name="<%= PageFlow.REQUEST_PARAMETER__OK_BUTTON %>" class="imcmsFormBtn" value="<? templates/sv/docinfo/change_meta_rights.html/2005 ?>"></td>
                <td>&nbsp;</td>
                <td><input type="RESET" name="reset" class="imcmsFormBtn" value="<? templates/sv/docinfo/change_meta_rights.html/2006 ?>"></td>
                <td>&nbsp;</td>
                <td><input type="submit" name="<%= PageFlow.REQUEST_PARAMETER__CANCEL_BUTTON %>" class="imcmsFormBtn" value="<? templates/sv/docinfo/change_meta_rights.html/2007 ?>"></td>
            </tr>
            </table></td>
        </tr>
    </table>
<form>
#gui_bottom()
#gui_outer_end()
</body>
</html>
</vel:velocity>
