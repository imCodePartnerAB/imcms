<%@ page import="com.imcode.imcms.flow.Page,
                 com.imcode.imcms.servlet.SearchDocumentsPage,
                 imcode.server.Imcms,
                 imcode.server.document.DocumentTypeDomainObject,
                 imcode.server.document.LifeCyclePhase,
                 imcode.server.document.SectionDomainObject,
                 imcode.server.user.UserDomainObject,
                 imcode.util.Html,
                 com.imcode.imcms.util.l10n.LocalizedMessage,
                 imcode.util.ToDoubleObjectStringPairTransformer,
                 imcode.util.Utility,
                 imcode.util.jscalendar.JSCalendar,
                 org.apache.commons.lang.ArrayUtils"%>
<%@ page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@ page import="org.apache.commons.lang.StringUtils"%>
<%@ page import="java.util.Arrays"%>
<%@ page import="java.util.Set"%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%
    SearchDocumentsPage searchDocumentsPage = (SearchDocumentsPage) Page.fromRequest(request) ;
    int documentsPerPage = searchDocumentsPage.getDocumentsPerPage() ;
    String[] phases = searchDocumentsPage.getPhases() ;
    int[] documentTypeIds = searchDocumentsPage.getDocumentTypeIds();
    UserDomainObject user = Utility.getLoggedOnUser( request );
    String IMG_PATH  = request.getContextPath()+"/imcms/"+user.getLanguageIso639_2()+"/images/admin/" ;
    JSCalendar jsCalendar = searchDocumentsPage.getJSCalender(request);
    String calendarButtonTitle = "<? web/imcms/lang/jscalendar/show_calendar_button ?>";

%>
    <%= Page.htmlHidden(request) %>
</form>
<form method="post" action="<%= request.getContextPath() %>/servlet/SearchDocuments">
    <%= Page.htmlHidden(request) %>
    <table border="0" cellspacing="0" cellpadding="2" width="656">
        <tr>
            <td width="120" height="24"><? web/imcms/lang/jsp/admin/admin_manager_search.jsp/2 ?></td>
            <td width="536" colspan="3"><input type="text" name="<%= SearchDocumentsPage.REQUEST_PARAMETER__QUERY_STRING %>" value="<%= StringEscapeUtils.escapeHtml(StringUtils.defaultString( searchDocumentsPage.getQueryString() ) ) %>" size="20" maxlength="255" style="width:100%"></td>
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
        <% if (!user.isDefaultUser()) { %>
        <tr>
            <td height="20"><? web/imcms/lang/jsp/admin/admin_manager_search.jsp/3 ?></td>

            <td colspan="3">
                <select name="<%= SearchDocumentsPage.REQUEST_PARAMETER__USER_RESTRICTION %>">
                    <% String userDocumentsRestriction = searchDocumentsPage.getUserDocumentsRestriction() ; %>
                    <option value="<%= SearchDocumentsPage.USER_DOCUMENTS_RESTRICTION__NONE %>" <%= SearchDocumentsPage.USER_DOCUMENTS_RESTRICTION__NONE.equals( userDocumentsRestriction ) ? "selected" : "" %>></option>
                    <option value="<%= SearchDocumentsPage.USER_DOCUMENTS_RESTRICTION__DOCUMENTS_CREATED_BY_USER %>" <%= SearchDocumentsPage.USER_DOCUMENTS_RESTRICTION__DOCUMENTS_CREATED_BY_USER.equals( userDocumentsRestriction ) ? "selected" : "" %>><? web/imcms/lang/jsp/admin/admin_manager_search.jsp/permission_option1 ?></option>
                    <option value="<%= SearchDocumentsPage.USER_DOCUMENTS_RESTRICTION__DOCUMENTS_PUBLISHED_BY_USER %>" <%= SearchDocumentsPage.USER_DOCUMENTS_RESTRICTION__DOCUMENTS_PUBLISHED_BY_USER.equals( userDocumentsRestriction ) ? "selected" : "" %>><? web/imcms/lang/jsp/admin/admin_manager_search.jsp/permission_option2 ?></option>
                </select>
            </td>
        </tr>
        <tr>
            <td height="24"><? web/imcms/lang/jsp/admin/admin_manager.jsp/16 ?></td>

            <td colspan="3">
            <table border="0" cellspacing="0" cellpadding="2">
            <tr>
                <%
                    LifeCyclePhase[] allPhases = new LifeCyclePhase[] {
                        LifeCyclePhase.NEW,
                        LifeCyclePhase.DISAPPROVED,
                        LifeCyclePhase.APPROVED,
                        LifeCyclePhase.PUBLISHED,
                        LifeCyclePhase.ARCHIVED,
                        LifeCyclePhase.UNPUBLISHED
                    };
                    for ( int i = 0; i < allPhases.length; i++ ) {
                        LifeCyclePhase phase = allPhases[i];
                        %><td><input id="phase_<%= phase %>" type="checkbox" name="<%= SearchDocumentsPage.REQUEST_PARAMETER__PHASE %>" value="<%= phase %>"
                        <%= ArrayUtils.contains( phases, "" + phase ) ? "checked" : "" %> ></td>
                        <td><label for="phase_<%= phase %>"><%= new LocalizedMessage( "web/imcms/lang/jsp/admin/admin_manager_search.jsp/phase/" + phase ).toLocalizedString( request ) %></label></td><%
                    }
                %>
            </tr>
            </table></td>
        </tr>
        <tr>
            <td height="24"><? web/imcms/lang/jsp/admin/admin_manager.jsp/21 ?></td>

            <td colspan="3">
            <table border="0" cellspacing="0" cellpadding="2">
            <tr>
                <%
                    DocumentTypeDomainObject[] documentTypes = new DocumentTypeDomainObject[] {
                        DocumentTypeDomainObject.TEXT,
                        DocumentTypeDomainObject.FILE,
                        DocumentTypeDomainObject.URL,
                        DocumentTypeDomainObject.HTML,
                        DocumentTypeDomainObject.BROWSER
                    };
                    for ( int i = 0; i < documentTypes.length; i++ ) {
                        DocumentTypeDomainObject documentType = documentTypes[i] ;
                        %><td><input id="type_<%= documentType.getId() %>" type="checkbox" name="<%= SearchDocumentsPage.REQUEST_PARAMETER__DOCUMENT_TYPE_ID %>" value="<%= documentType.getId() %>"
                        <%= ArrayUtils.contains( documentTypeIds, documentType.getId() ) ? "checked" : "" %> ></td>
                        <td><label for="type_<%= documentType.getId() %>"><%= documentType.getName().toLocalizedString( request ) %></label></td><%
                    }
                %>
            </tr>
            </table></td>
        </tr>
        <tr>
            <td colspan="4"><img src="<%= IMG_PATH %>/1x1_cccccc.gif" width="100%" height="1" vspace="8"></td>
        </tr>
        <% } %>
        <tr>

            <td height="24"><? global/Date ?></td>
            <td colspan="3">
            <table border="0" cellspacing="0" cellpadding="0">
            <tr>
                <td>
                <select name="<%= SearchDocumentsPage.REQUEST_PARAMETER__DATE_TYPE %>">
                    <option value="<%= SearchDocumentsPage.DATE_TYPE__PUBLICATION_START %>" <%= SearchDocumentsPage.DATE_TYPE__PUBLICATION_START.equals(searchDocumentsPage.getDateTypeRestriction()) ? "selected" : "" %>><? web/imcms/lang/jsp/admin/admin_manager_search.jsp/publication_start ?></option>
                    <option value="<%= SearchDocumentsPage.DATE_TYPE__PUBLICATION_END %>" <%= SearchDocumentsPage.DATE_TYPE__PUBLICATION_END.equals( searchDocumentsPage.getDateTypeRestriction() ) ? "selected" : "" %>><? web/imcms/lang/jsp/admin/admin_manager_search.jsp/publication_end ?></option>
                    <option value="<%= SearchDocumentsPage.DATE_TYPE__CREATED %>" <%= SearchDocumentsPage.DATE_TYPE__CREATED.equals( searchDocumentsPage.getDateTypeRestriction() ) ? "selected" : "" %>><? web/imcms/lang/jsp/admin/admin_manager_search.jsp/created ?></option>
                    <option value="<%= SearchDocumentsPage.DATE_TYPE__ARCHIVED %>" <%= SearchDocumentsPage.DATE_TYPE__ARCHIVED.equals( searchDocumentsPage.getDateTypeRestriction() ) ? "selected" : "" %>><? web/imcms/lang/jsp/admin/admin_manager_search.jsp/archived ?></option>
                    <option value="<%= SearchDocumentsPage.DATE_TYPE__MODIFIED %>" <%= SearchDocumentsPage.DATE_TYPE__MODIFIED.equals( searchDocumentsPage.getDateTypeRestriction() ) ? "selected" : "" %>><? web/imcms/lang/jsp/admin/admin_manager_search.jsp/modified ?></option>
                </select></td>

            </tr>
            </table></td>
        </tr>
        <tr>
            <td height="24">&nbsp;</td>
            <td colspan="3">
            <table border="0" cellspacing="0" cellpadding="0">
            <tr>
                <td><input type="text" id="<%= SearchDocumentsPage.REQUEST_PARAMETER__START_DATE %>" name="<%= SearchDocumentsPage.REQUEST_PARAMETER__START_DATE %>" value="<%= searchDocumentsPage.getFormattedStartDate() %>" size="10" maxlength="10" style="width:7em">
                <%= jsCalendar.getInstance(SearchDocumentsPage.REQUEST_PARAMETER__START_DATE, null).getButton(calendarButtonTitle) %>
                <td nowrap>&nbsp; &nbsp; - &nbsp; &nbsp;</td>
                <td><input type="text" id="<%= SearchDocumentsPage.REQUEST_PARAMETER__END_DATE %>" name="<%= SearchDocumentsPage.REQUEST_PARAMETER__END_DATE %>" value="<%= searchDocumentsPage.getFormattedEndDate() %>" size="10" maxlength="10" style="width:7em">
                <%= jsCalendar.getInstance(SearchDocumentsPage.REQUEST_PARAMETER__END_DATE, null).getButton(calendarButtonTitle) %></td>
                <td>&nbsp; &nbsp; (<? web/imcms/lang/jsp/admin/admin_manager_search.jsp/9 ?>)</td>
            </tr>
            </table></td>
        </tr>

        <tr>
            <td colspan="4"><img src="<%= IMG_PATH %>/1x1_cccccc.gif" width="100%" height="1" vspace="8"></td>
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
                    <%= Html.createOptionList(Arrays.asList(ranges), new Integer( documentsPerPage ), new ToDoubleObjectStringPairTransformer()) %>
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