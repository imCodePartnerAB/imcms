<%@ page import="com.imcode.imcms.servlet.SearchDocumentsPage,
                 org.apache.commons.lang.StringEscapeUtils,
                 org.apache.commons.lang.StringUtils,
                 imcode.util.Html,
                 java.util.Arrays,
                 org.apache.commons.collections.Transformer,
                 imcode.server.document.DocumentMapper,
                 imcode.server.Imcms,
                 imcode.server.document.SectionDomainObject,
                 com.imcode.imcms.servlet.superadmin.AdminManager,
                 imcode.util.Utility,
                 com.imcode.imcms.flow.Page,
                 imcode.server.document.DocumentDomainObject,
                 org.apache.commons.lang.ArrayUtils,
                 imcode.util.ToStringPairArrayTransformer,
                 java.util.Set"%>
<%
    SearchDocumentsPage searchDocumentsPage = (SearchDocumentsPage) Page.fromRequest(request) ;
    int documentsPerPage = searchDocumentsPage.getDocumentsPerPage() ;
    int[] statusIds = searchDocumentsPage.getStatusIds() ;
    String IMG_PATH  = request.getContextPath()+"/imcms/"+Utility.getLoggedOnUser( request ).getLanguageIso639_2()+"/images/admin/" ;
%>

<%!
    boolean isSelected(int a, int[] values) {
        return ArrayUtils.contains( values, a ) ;
    }
%>

    <%= Page.htmlHidden(request) %>
    <table width="550" border="0" cellspacing="0">
        <tr>
            <td colspan="2">&nbsp;</td>
        </tr>
        <tr>
            <td height="24"><? web/imcms/lang/jsp/admin/admin_manager_search.jsp/2 ?></td>
            <td colspan="3"><input type="text" name="<%= SearchDocumentsPage.REQUEST_PARAMETER__QUERY_STRING %>" value="<%= StringEscapeUtils.escapeHtml(StringUtils.defaultString( searchDocumentsPage.getQueryString() ) ) %>" size="20" maxlength="255" style="width:300"></td>
        </tr>
        <tr>
            <td colspan="4"><img src="<%= IMG_PATH %>/1x1_cccccc.gif" width="100%" height="1" vspace="8"></td>
        </tr>
        <%
            SectionDomainObject[] sections = Imcms.getServices().getDocumentMapper().getAllSections() ;
            if (sections.length > 0) {
        %>
            <tr>
                <td><? web/imcms/lang/jsp/search_documents_form.jsp/sections ?></td>
                <td colspan="3">
                    <select name="<%= SearchDocumentsPage.REQUEST_PARAMETER__SECTION_ID %>" size="4" multiple>
                        <%
                           Set selectedSections = searchDocumentsPage.getSections() ;
                           for ( int i = 0; i < sections.length; i++ ) {
                                SectionDomainObject section = sections[i];
                                %><option value="<%= section.getId() %>" <% if (selectedSections.contains(section)) { %>selected<% } %>><%= StringEscapeUtils.escapeHtml( section.getName() ) %></option>
                        <% } %>
                    </select>
                </td>
            </tr>
            <tr>
                <td colspan="4"><img src="<%= IMG_PATH %>/1x1_cccccc.gif" width="100%" height="1" vspace="8"></td>
            </tr>
        <% } %>
        <tr>
            <td height="20"><? web/imcms/lang/jsp/admin/admin_manager_search.jsp/3 ?></td>

            <td colspan="3">
                <select name="<%= SearchDocumentsPage.REQUEST_PARAMETER__PERMISSION %>">
                    <% int userDocumentsRestriction = searchDocumentsPage.getUserDocumentsRestriction() ; %>
                    <option value="<%= SearchDocumentsPage.USER_DOCUMENTS_RESTRICTION__NONE %>" <%= SearchDocumentsPage.USER_DOCUMENTS_RESTRICTION__NONE == userDocumentsRestriction ? "selected" : "" %>></option>
                    <option value="<%= SearchDocumentsPage.USER_DOCUMENTS_RESTRICTION__DOCUMENTS_CREATED_BY_USER %>" <%= SearchDocumentsPage.USER_DOCUMENTS_RESTRICTION__DOCUMENTS_CREATED_BY_USER == userDocumentsRestriction ? "selected" : "" %>><? web/imcms/lang/jsp/admin/admin_manager_search.jsp/permission_option1 ?></option>
                    <option value="<%= SearchDocumentsPage.USER_DOCUMENTS_RESTRICTION__DOCUMENTS_PUBLISHED_BY_USER %>" <%= SearchDocumentsPage.USER_DOCUMENTS_RESTRICTION__DOCUMENTS_PUBLISHED_BY_USER == userDocumentsRestriction ? "selected" : "" %>><? web/imcms/lang/jsp/admin/admin_manager_search.jsp/permission_option2 ?></option>
                    <option value="<%= SearchDocumentsPage.USER_DOCUMENTS_RESTRICTION__DOCUMENTS_EDITABLE_BY_USER %>" <%= SearchDocumentsPage.USER_DOCUMENTS_RESTRICTION__DOCUMENTS_EDITABLE_BY_USER == userDocumentsRestriction ? "selected" : "" %>><? web/imcms/lang/jsp/admin/admin_manager_search.jsp/permission_option3 ?></option>
                </select>
            </td>
        </tr>
        <tr>
            <td height="24"><? web/imcms/lang/jsp/admin/admin_manager.jsp/16 ?></td>

            <td colspan="3">
            <table border="0" cellspacing="0" cellpadding="2">
            <tr>
                <td><input type="checkbox" name="<%= SearchDocumentsPage.REQUEST_PARAMETER__STATUS %>" value="<%= DocumentDomainObject.STATUS_NEW %>" <%= isSelected(DocumentDomainObject.STATUS_NEW, statusIds) ? "checked" : "" %> ></td>
                <td><? web/imcms/lang/jsp/admin/admin_manager_search.jsp/4 ?> &nbsp;</td>
                <td><input type="checkbox" name="<%= SearchDocumentsPage.REQUEST_PARAMETER__STATUS %>" value="<%= DocumentDomainObject.STATUS_PUBLICATION_APPROVED %>" <%= isSelected(DocumentDomainObject.STATUS_PUBLICATION_APPROVED, statusIds) ? "checked" : "" %> ></td>
                <td><? web/imcms/lang/jsp/admin/admin_manager_search.jsp/5 ?> &nbsp;</td>
                <td><input type="checkbox" name="<%= SearchDocumentsPage.REQUEST_PARAMETER__STATUS %>" value="<%= DocumentDomainObject.STATUS_PUBLICATION_DISAPPROVED %>" <%= isSelected(DocumentDomainObject.STATUS_PUBLICATION_DISAPPROVED, statusIds) ? "checked" : "" %> ></td>
                <td><? web/imcms/lang/jsp/admin/admin_manager_search.jsp/6 ?></td>
            </tr>
            </table></td>
        </tr>
        <tr>
            <td colspan="4"><img src="<%= IMG_PATH %>/1x1_cccccc.gif"
            width="100%" height="1" vspace="8"></td>
        </tr>
        <tr>

            <td height="24"><? global/Date ?></td>
            <td colspan="3">
            <table border="0" cellspacing="0" cellpadding="0">
            <tr>
                <td>
                <select name="<%= SearchDocumentsPage.REQUEST_PARAMETER__DATE_TYPE %>">
                    <option value="<%= SearchDocumentsPage.DATE_TYPE__PUBLICATION_START %>" <%= searchDocumentsPage.getDateTypeRestriction() == SearchDocumentsPage.DATE_TYPE__PUBLICATION_START ? "selected" : "" %>><? web/imcms/lang/jsp/admin/admin_manager_search.jsp/datetype_option1 ?>
                    <option value="<%= SearchDocumentsPage.DATE_TYPE__PUBLICATION_END %>" <%= searchDocumentsPage.getDateTypeRestriction() == SearchDocumentsPage.DATE_TYPE__PUBLICATION_END ? "selected" : "" %>><? web/imcms/lang/jsp/admin/admin_manager_search.jsp/datetype_option2 ?>
                    <option value="<%= SearchDocumentsPage.DATE_TYPE__CREATED %>" <%= searchDocumentsPage.getDateTypeRestriction() == SearchDocumentsPage.DATE_TYPE__CREATED ? "selected" : "" %>><? web/imcms/lang/jsp/admin/admin_manager_search.jsp/datetype_option3 ?>
                    <option value="<%= SearchDocumentsPage.DATE_TYPE__ARCHIVED %>" <%= searchDocumentsPage.getDateTypeRestriction() == SearchDocumentsPage.DATE_TYPE__ARCHIVED ? "selected" : "" %>><? web/imcms/lang/jsp/admin/admin_manager_search.jsp/datetype_option4 ?>
                    <option value="<%= SearchDocumentsPage.DATE_TYPE__MODIFIED %>" <%= searchDocumentsPage.getDateTypeRestriction() == SearchDocumentsPage.DATE_TYPE__MODIFIED ? "selected" : "" %>><? web/imcms/lang/jsp/admin/admin_manager_search.jsp/datetype_option5 ?>
                </select></td>

            </tr>
            </table></td>
        </tr>
        <tr>
            <td height="24">&nbsp;</td>
            <td colspan="3">
            <table border="0" cellspacing="0" cellpadding="0">
            <tr>
                <td><input type="text" name="<%= SearchDocumentsPage.REQUEST_PARAMETER__START_DATE %>" value="<%= searchDocumentsPage.getFormattedStartDate() %>" size="10" maxlength="10" style="width:7em"></td>
                <td nowrap>&nbsp; &nbsp; - &nbsp; &nbsp;</td>
                <td><input type="text" name="<%= SearchDocumentsPage.REQUEST_PARAMETER__END_DATE %>" value="<%= searchDocumentsPage.getFormattedEndDate() %>" size="10" maxlength="10" style="width:7em"></td>
                <td>&nbsp; &nbsp; (<? web/imcms/lang/jsp/admin/admin_manager_search.jsp/9 ?>)</td>
            </tr>
            </table></td>
        </tr>

        <tr>
            <td colspan="4"><img src="<%= IMG_PATH %>/1x1_cccccc.gif"
            width="100%" height="1" vspace="8"></td>
        </tr>
        <tr>
            <td height="24"><? web/imcms/lang/jsp/admin/admin_manager_search.jsp/7 ?></td>
            <td>
            <select name="<%= SearchDocumentsPage.REQUEST_PARAMETER__SORT_ORDER %>">
                <% request.setAttribute( "SORT", searchDocumentsPage.getSortOrder() ); %>
                <jsp:include page="admin/admin_manager_inc_sortorder_select_option.jsp" />
            </select></td>

            <td><? web/imcms/lang/jsp/admin/admin_manager_search.jsp/8 ?></td>
            <td>
                <select name="<%= SearchDocumentsPage.REQUEST_PARAMETER__DOCUMENTS_PER_PAGE %>">
                    <%
                        Integer[] ranges = new Integer[] {
                            new Integer( 5 ),
                            new Integer( 10 ),
                            new Integer( 20 ),
                            new Integer( 100 ),
                            new Integer( 1000 ),
                        } ;
                    %>
                    <%= Html.createOptionList(Arrays.asList(ranges), new Integer( documentsPerPage ), new ToStringPairArrayTransformer()) %>
                </select>
            </td>
        </tr>
        <tr>
            <td colspan="4"><img src="<%= IMG_PATH %>/1x1_cccccc.gif" width="100%" height="1" vspace="8"></td>
        </tr>
        <tr>
            <td>&nbsp;</td>
            <td colspan="3" align="right">
            <input type="submit" name="<%= SearchDocumentsPage.REQUEST_PARAMETER__SEARCH_BUTTON %>" value="<? global/Search ?>" class="imcmsFormBtn" style="width:100">
            <input type="reset" name="reset_btn" value="<? global/Reset ?>" class="imcmsFormBtn" style="width:100"></td>
        </tr>
    </table>