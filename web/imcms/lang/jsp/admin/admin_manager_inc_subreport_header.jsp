<%@ page import="com.imcode.imcms.servlet.superadmin.AdminManager,
                 java.util.HashMap"%>

<table border="0" cellspacing="0" cellpadding="2" width="656" align="center">
<tr>
    <td colspan="2"><img src="<%= IMG_PATH %>/1x1.gif" width="1" height="25"></td>
</tr>
<form method="post" name="subreport" action="AdminManager">
<input type="hidden" name="list_type" value="<%= LIST_TYPE %>" >
<input type="hidden" name="<%= AdminManager.REQUEST_PARAMETER__SHOW %>" value="<%= TAB_TO_SHOW %>" >
<%if ( expand_listMap.containsKey(AdminManager.LIST_TYPE__list_new_not_approved) ) { %>
<input type="hidden" name="list_new_not_approved_current_expand" value="<%= expand_listMap.get(AdminManager.LIST_TYPE__list_new_not_approved).toString() %>" >
<%}if ( expand_listMap.containsKey(AdminManager.LIST_TYPE__list_documents_archived_less_then_one_week) ) { %>
<input type="hidden" name="list_documents_archived_less_then_one_week_current_expand" value="<%= expand_listMap.get(AdminManager.LIST_TYPE__list_documents_archived_less_then_one_week).toString() %>" >
<%}if ( expand_listMap.containsKey(AdminManager.LIST_TYPE__list_documents_publication_end_less_then_one_week) ) { %>
<input type="hidden" name="list_documents_publication_end_less_then_one_week_current_expand" value="<%= expand_listMap.get(AdminManager.LIST_TYPE__list_documents_publication_end_less_then_one_week).toString() %>" >
<%}if ( expand_listMap.containsKey(AdminManager.LIST_TYPE__list_documents_not_changed_in_six_month) ) { %>
<input type="hidden" name="list_documents_not_changed_in_six_month_current_expand" value="<%= expand_listMap.get(AdminManager.LIST_TYPE__list_documents_not_changed_in_six_month).toString() %>" >
<%}if ( expand_listMap.containsKey(AdminManager.LIST_TYPE__list_documents_changed) ) { %>
<input type="hidden" name="list_documents_changed_current_expand" value="<%= expand_listMap.get(AdminManager.LIST_TYPE__list_documents_changed).toString() %>" >
<%}%>
<tr>
    <td nowrap><span class="imcmsAdmHeading" ><%= subreport_heading %> (<%= documents_found %> <? web/imcms/lang/jsp/admin/admin_manager.jsp/10 ?>)</span></td>
    <td align="right">
    <table border="0" cellspacing="0" cellpadding="0">
    <tr>
        <% if ( expand_listMap.get( LIST_TYPE ).toString().equals("expand") ) { %>
            <td><input type="submit" class="imcmsFormBtnSmall" style="width:70" name="hideAll" value="<? web/imcms/lang/jsp/admin/admin_manager.jsp/12 ?> &raquo;"></td>
        <%}else{ %>
            <td><input type="submit" class="imcmsFormBtnSmall" style="width:70" name="showAll" value="<? web/imcms/lang/jsp/admin/admin_manager.jsp/11 ?> &raquo;"></td>
        <%}%>
    </tr>
    </table></td>
</tr>
<tr>
    <td colspan="2"><img src="<%= IMG_PATH %>/1x1_20568d.gif" width="100%" height="1" vspace="8"></td>
</tr>
<tr>
    <td colspan="2">
    <table border="0" cellspacing="0" cellpadding="2" width="100%">
    <tr valign="bottom">
        <td><b><? web/imcms/lang/jsp/admin/admin_manager.jsp/15 ?></b></td>
        <td><b><? web/imcms/lang/jsp/admin/admin_manager.jsp/16 ?></b>&nbsp;</td>
        <td><b><? web/imcms/lang/jsp/admin/admin_manager.jsp/17 ?>/<? web/imcms/lang/jsp/admin/admin_manager.jsp/18 ?></b></td>
        <td align="right">
            <table border="0" cellspacing="0" cellpadding="0">
            <%if ( current_sortorderMap.containsKey(AdminManager.LIST_TYPE__list_new_not_approved) ) { %>
                <input type="hidden" name="list_new_not_approved_current_sortorder" value="<%= current_sortorderMap.get(AdminManager.LIST_TYPE__list_new_not_approved).toString() %>" >
            <%}if ( current_sortorderMap.containsKey(AdminManager.LIST_TYPE__list_documents_archived_less_then_one_week) ) { %>
                <input type="hidden" name="list_documents_archived_less_then_one_week_current_sortorder" value="<%= current_sortorderMap.get(AdminManager.LIST_TYPE__list_documents_archived_less_then_one_week).toString() %>" >
            <%}if ( current_sortorderMap.containsKey(AdminManager.LIST_TYPE__list_documents_publication_end_less_then_one_week) ) { %>
                <input type="hidden" name="list_documents_publication_end_less_then_one_week_current_sortorder" value="<%= current_sortorderMap.get(AdminManager.LIST_TYPE__list_documents_publication_end_less_then_one_week).toString() %>" >
            <%}if ( current_sortorderMap.containsKey(AdminManager.LIST_TYPE__list_documents_not_changed_in_six_month) ) { %>
                <input type="hidden" name="list_documents_not_changed_in_six_month_current_sortorder" value="<%= current_sortorderMap.get(AdminManager.LIST_TYPE__list_documents_not_changed_in_six_month).toString() %>" >
            <%}if ( current_sortorderMap.containsKey(AdminManager.LIST_TYPE__list_documents_changed) ) { %>
                <input type="hidden" name="list_documents_changed_current_sortorder" value="<%= current_sortorderMap.get(AdminManager.LIST_TYPE__list_documents_changed).toString() %>" >
            <%}%>

            <tr>
                <td nowrap><? web/imcms/lang/jsp/admin/admin_manager_search.jsp/7 ?>:&nbsp;</td>
                <td>
                <select name="new_sortorder" onChange="this.form.submit();">
                    <% request.setAttribute( "SORT", current_sortorderMap.get( LIST_TYPE ).toString() ); %>
                    <jsp:include page="admin_manager_inc_sortorder_select_option.jsp" />
                </select></td>
            </tr>
            </table></td>
    </tr>
    <tr>
        <td colspan="4"><img src="<%= IMG_PATH %>/1x1_cccccc.gif" width="100%" height="1"></td>
    </tr>
</form>