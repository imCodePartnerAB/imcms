<%@ page import="imcode.server.document.DocumentDomainObject,
                 com.imcode.imcms.util.l10n.LocalizedMessage,
                 imcode.util.Utility,
                 org.apache.commons.lang.StringEscapeUtils,
                 java.util.List"%>
<%@page contentType="text/html; charset=UTF-8" %>
<jsp:useBean id="subreport" scope="request" class="com.imcode.imcms.servlet.beans.AdminManagerSubreport"/>
<%
    String imagesPath = request.getContextPath()+"/imcms/"+Utility.getLoggedOnUser( request ).getLanguageIso639_2()+"/images/admin/" ;
    LocalizedMessage subreportHeading = subreport.getHeading() ;
    List documents = subreport.getDocuments() ;
%>

<table border="0" cellspacing="0" cellpadding="2" width="656">
    <tr>
        <td colspan="2"><img src="<%= imagesPath %>/1x1.gif" width="1" height="25"></td>
    </tr>
    <tr>
        <td><span class="imcmsAdmHeading" ><%= StringEscapeUtils.escapeHtml( subreportHeading.toLocalizedString( request ) ) %><br>(<%= documents.size() %> <? web/imcms/lang/jsp/admin/admin_manager.jsp/10 ?>)</span></td>
        <td align="right">
            <table border="0" cellspacing="0" cellpadding="0">
                <tr>
                    <% if ( subreport.isExpanded() ) { %>
                        <input type="hidden" name="<%= subreport.getName() %>_expand" value="1">
                        <td><input type="submit" class="imcmsFormBtnSmall" name="<%= subreport.getName() %>_unexpand" value="<? web/imcms/lang/jsp/admin/admin_manager.jsp/12 ?> &raquo;"></td>
                    <%}else{ %>
                        <td><input type="submit" class="imcmsFormBtnSmall" name="<%= subreport.getName() %>_expand" value="<? web/imcms/lang/jsp/admin/admin_manager.jsp/11 ?> &raquo;"></td>
                    <%}%>
                </tr>
            </table>
        </td>
    </tr>
    <tr>
        <td colspan="2"><img src="<%= imagesPath %>/1x1_20568d.gif" width="100%" height="1" vspace="8"></td>
    </tr>
    <tr>
        <td colspan="2">
            <table border="0" cellspacing="0" cellpadding="2" width="100%">
                <tr valign="bottom">
                    <td><b><? global/Page_alias ?>&nbsp;</b></td>
                    <td width="40"><b><? web/imcms/lang/jsp/admin/admin_manager.jsp/16 ?></b>&nbsp;</td>
                    <td><b><? web/imcms/lang/jsp/admin/admin_manager.jsp/15 ?></b></td>
                    <td><b><? web/imcms/lang/jsp/admin/admin_manager.jsp/21 ?></b></td>
                    <td><b><? web/imcms/lang/jsp/admin/admin_manager.jsp/17 ?>/<? web/imcms/lang/jsp/admin/admin_manager.jsp/18 ?></b></td>
                    <td align="right" nowrap>
                        <input type="submit" class="imcmsFormBtnSmall" value="<? web/imcms/lang/jsp/admin/admin_manager_search.jsp/7 ?>">
                        &nbsp;
                        <select name="<%= subreport.getName() %>_sortorder" onChange="this.form.submit();">
                            <% request.setAttribute( "SORT", subreport.getSortorder() ); %>
                            <jsp:include page="admin_manager_inc_sortorder_select_option.jsp" />
                        </select>
                    </td>
                </tr>
                <tr>
                    <td colspan="6"><img src="<%= imagesPath %>/1x1_cccccc.gif" width="100%" height="1"></td>
                </tr>

               <%
                    for (int i = 0; i < documents.size() && subreport.isBelowMaxDocumentCount(i); i++) {
                        boolean expand = i < 2 || subreport.isExpanded() ;
                        DocumentDomainObject document = (DocumentDomainObject) documents.get(i); %>
                <jsp:useBean id="listItemBean" class="com.imcode.imcms.servlet.beans.AdminManagerSubReportListItemBean" scope="request" />
                <jsp:setProperty name="listItemBean" property="expanded" value="<%= expand %>"/>
                <jsp:setProperty name="listItemBean" property="index" value="<%= i %>"/>
                <jsp:setProperty name="listItemBean" property="document" value="<%= document %>"/>

                <jsp:include page="admin_manager_inc_list_item.jsp"/>

                <% } %>
            </table>
        </td>
    </tr>
    <%
        String searchQueryString = subreport.getSearchQueryString();
        if ( null != searchQueryString && subreport.isAboveMaxDocumentCount( documents.size() ) ) {
    %>
        <tr>
            <td colspan="4" align="center"><img src="<%= imagesPath %>/1x1.gif" height="20" width="1"><br>
                <a href="<%= request.getContextPath() %>/servlet/AdminManager?show=search&<%= searchQueryString %>"><? web/imcms/lang/jsp/admin/admin_manager.jsp/19 ?></a>
            </td>
        </tr>
        <form name="seachForm99"></form>
    <%}%>
</table>