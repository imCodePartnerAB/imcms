<%@ page import="com.imcode.imcms.flow.DocumentPermissionsPage,
                 com.imcode.imcms.flow.EditDocumentInformationPageFlow,
                 com.imcode.imcms.flow.Page,
                 com.imcode.imcms.flow.PageFlow,
                 imcode.server.Imcms,
                 imcode.server.document.DocumentDomainObject,
                 imcode.server.document.DocumentPermissionSetTypeDomainObject,
                 imcode.server.document.RoleIdToDocumentPermissionSetTypeMappings,
                 imcode.server.document.TemplateDomainObject,
                 imcode.server.document.textdocument.TextDocumentDomainObject,
                 imcode.server.user.RoleDomainObject,
                 imcode.server.user.RoleId"%>
<%@ page import="imcode.server.user.UserDomainObject"%>
<%@ page import="imcode.util.Html"%>
<%@ page import="imcode.util.Utility"%>
<%@ page import="org.apache.commons.collections.CollectionUtils"%>
<%@ page import="org.apache.commons.collections.Predicate"%>
<%@ page import="java.util.Arrays"%>
<%@ page import="java.util.Iterator"%>
<%@ page import="java.util.SortedSet"%>
<%@ page import="java.util.TreeSet"%><%@ page import="imcode.server.user.ImcmsAuthenticatorAndUserAndRoleMapper, java.util.List"%>
<%@page contentType="text/html; charset=UTF-8"%><%@taglib uri="imcmsvelocity" prefix="vel"%><%!
    String formatRolePermissionRadioButton( DocumentPermissionSetTypeDomainObject radioButtonDocumentPermissionSetType, UserDomainObject user, DocumentPermissionSetTypeDomainObject documentPermissionSetType,
                                            RoleId roleId,
                                            DocumentDomainObject document ) {
        boolean checked = documentPermissionSetType == radioButtonDocumentPermissionSetType;
        String name = "role_" + roleId.intValue();
        String value = "" + radioButtonDocumentPermissionSetType;
        if (user.canSetDocumentPermissionSetTypeForRoleIdOnDocument( radioButtonDocumentPermissionSetType, roleId, document )) {
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
<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/imcms/css/imcms_admin.css.jsp" />
<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/imcms/css/imcms4_admin.css.jsp" />
<script type="text/javascript" src="<%= request.getContextPath() %>/imcms/swe/scripts/imcms4_admin_script.js.jsp"></script>

</head>
<body bgcolor="#FFFFFF">

#gui_outer_start()
#gui_head( "<? global/imcms_administration ?>" )

<form method="POST" action="<%= request.getContextPath() %>/servlet/PageDispatcher">
<%= Page.htmlHidden( request ) %>
    <table border="0" cellspacing="0" cellpadding="0">
        <tr>
            <td><input type="submit" name="<%= PageFlow.REQUEST_PARAMETER__CANCEL_BUTTON %>" class="imcmsFormBtn" value="<? templates/sv/docinfo/change_meta_rights.html/2001 ?>"></td>
            <td>&nbsp;</td>
            <td><input type="button" value="<? templates/sv/docinfo/change_meta_rights.html/2002 ?>" title="<? templates/sv/docinfo/change_meta_rights.html/2003 ?>" class="imcmsFormBtn" onClick="openHelpW('Privileges')"></td>
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
                    ImcmsAuthenticatorAndUserAndRoleMapper userMapper = Imcms.getServices().getImcmsAuthenticatorAndUserAndRoleMapper();
                    SortedSet allRoles = new TreeSet(Arrays.asList(userMapper.getAllRoles())) ;
                    CollectionUtils.filter(allRoles, new Predicate() {
                        public boolean evaluate(Object object) {
                            RoleDomainObject role = (RoleDomainObject) object ;
                            return !RoleId.SUPERADMIN.equals(role.getId()) ;
                        }
                    });
                    RoleIdToDocumentPermissionSetTypeMappings roleIdsMappedToDocumentPermissionSetTypessionSetTypes = document.getRoleIdsMappedToDocumentPermissionSetTypes() ;
                    RoleIdToDocumentPermissionSetTypeMappings.Mapping[] mappings = roleIdsMappedToDocumentPermissionSetTypessionSetTypes.getMappings();
                    for ( int i = 0; i < mappings.length; i++ ) {
                        RoleIdToDocumentPermissionSetTypeMappings.Mapping entry = mappings[i];
                        RoleId roleId = entry.getRoleId();
                        RoleDomainObject role = userMapper.getRole(roleId) ;
                        if (null != role ) {
                            DocumentPermissionSetTypeDomainObject documentPermissionSetType = entry.getDocumentPermissionSetType() ;
                            if ( DocumentPermissionSetTypeDomainObject.NONE.equals(documentPermissionSetType) || RoleId.SUPERADMIN.equals(roleId) ) {
                                continue ;
                            }
                            allRoles.remove( role ) ;
                            %>
                            <tr align="center">
                                <td height="22" class="imcmsAdmText" align="left"><% if (user.hasRoleId( roleId )) { %>*<% } else { %>&nbsp;<% } %></td>
                                <td class="imcmsAdmText" align="left"><%= role.getName() %></td>
                                <td><%= formatRolePermissionRadioButton( DocumentPermissionSetTypeDomainObject.NONE, user, documentPermissionSetType, roleId, document ) %></td>
                                <td><%= formatRolePermissionRadioButton( DocumentPermissionSetTypeDomainObject.READ, user, documentPermissionSetType, roleId, document ) %></td>
                                <td><%= formatRolePermissionRadioButton( DocumentPermissionSetTypeDomainObject.RESTRICTED_2, user, documentPermissionSetType, roleId, document ) %></td>
                                <td><%= formatRolePermissionRadioButton( DocumentPermissionSetTypeDomainObject.RESTRICTED_1, user, documentPermissionSetType, roleId, document ) %></td>
                                <td><%= formatRolePermissionRadioButton( DocumentPermissionSetTypeDomainObject.FULL, user, documentPermissionSetType, roleId, document ) %></td>
                            </tr>
                            <%
                        }
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
                    <% if (document instanceof TextDocumentDomainObject) { %>
                        <input type="submit" class="imcmsFormBtnSmall" name="define_new_set_1" value="<? templates/sv/permissions/new_set_1_button.html/2001 ?>">
                    <% } %>
                </td>
            </tr>
            <% if (user.canDefineRestrictedTwoFor(document)) { %>
                <tr>
                    <td>
                        <? templates/sv/permissions/set_2_button.html/1 ?>
                    </td>
                    <td>
                        <input type="submit" class="imcmsFormBtnSmall" name="define_set_2" value="<? templates/sv/permissions/set_2_button.html/2001 ?>">
                        <% if (document instanceof TextDocumentDomainObject) { %>
                            <input type="submit" class="imcmsFormBtnSmall" name="define_new_set_2" value="<? templates/sv/permissions/new_set_2_button.html/2001 ?>">
                        <% } %>
                    </td>
                </tr>
            <% } %>
            <% if (user.isSuperAdminOrHasFullPermissionOn(document)) { %>
                <tr>
                    <td class="imcmsAdmText">
                        <? templates/sv/permissions/sets_precedence.html/precedence ?>
                    </td>
                    <td class="imcmsAdmText">
                        <input type="CHECKBOX" id="setsPrecedenceCb" name="<%= DocumentPermissionsPage.REQUEST_PARAMETER__RESTRICTED_ONE_MORE_PRIVILEGED_THAN_RESTRICTED_TWO %>" value="1" <% if (document.isRestrictedOneMorePrivilegedThanRestrictedTwo()) { %>checked<% } %>>
                        <label for="setsPrecedenceCb"><? templates/sv/permissions/sets_precedence.html/1001 ?></label>
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
                            String defaultTemplateId = textDocument.getDefaultTemplateName();
                            List<TemplateDomainObject> allTemplates = Imcms.getServices().getTemplateMapper().getAllTemplates() ;
                            for ( TemplateDomainObject template : allTemplates ) {
                                %><%= Html.option( ""+template.getName(), template.getName(), defaultTemplateId != null && template.getName().equals(defaultTemplateId) )%><%
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
                    <td><input type="CHECKBOX" id="showMetaCb" name="show_meta" value="1" <% if (document.isLinkedForUnauthorizedUsers()) {%>checked<% } %>></td>
                    <td class="imcmsAdmText">&nbsp;<label for="showMetaCb"><? templates/global/pageinfo/ShowLinkToUnuthorizedUser ?></label></td>
                </tr>
                <tr>
                    <td><input type="CHECKBOX" id="linkableCb" name="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__LINKABLE_BY_OTHER_USERS %>" value="1" <% if (document.isLinkableByOtherUsers()) {%>checked<% } %>></td>
                    <td class="imcmsAdmText">&nbsp;<label for="linkableCb"><? templates/global/pageinfo/share ?></label></td>
                </tr>
                </table>
                </td>
                <td class="imcmsAdmText" nowrap><? global/Created_by ?>&nbsp;<i><%= Utility.formatUser(userMapper.getUser(document.getCreatorId())) %></i></td>
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
</form>
#gui_bottom()
#gui_outer_end()
<%
    String adminButtons = Html.getAdminButtons( user, document, request, response ) ;
    if (!"".equals( adminButtons )) {
        %><center><%= adminButtons %></center><%
    }
%>
</body>
</html>
</vel:velocity>
