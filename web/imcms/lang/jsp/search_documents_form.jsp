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
                 com.imcode.imcms.flow.Page"%>
<%
    SearchDocumentsPage searchDocumentsPage = (SearchDocumentsPage) Page.fromRequest(request) ;
    int documentsPerPage = searchDocumentsPage.getDocumentsPerPage() ;
    int status[] = {};
    String IMG_PATH  = request.getContextPath()+"/imcms/"+Utility.getLoggedOnUser( request ).getLanguageIso639_2()+"/images/admin/" ;
    String SORTORDER_OPTION_SELECTED;
%>

<%!
    boolean isSelected(int a, int[] values) {

        boolean found = false;
        for ( int i = 0; i < values.length; i++){
             found = a == values[i] ? true : false;
        }
        return found;
    }
%>

    <%= Page.htmlHidden(request) %>
    <table width="550" border="0" cellspacing="0">
        <tr>
            <td colspan="2">&nbsp;</td>
        </tr>
        <tr>
            <td height="24"><? web/imcms/lang/jsp/admin/admin_manager_search.jsp/2 ?></td>
            <td colspan="3"><input type="text" name="<%= SearchDocumentsPage.REQUEST_PARAMETER__QUERY_STRING %>" value="<%= StringEscapeUtils.escapeHtml("") %>" size="20" maxlength="255" style="width:300"></td>
        </tr>
        <tr>
            <td colspan="4"><img src="<%= IMG_PATH %>/1x1_cccccc.gif" width="100%" height="1" vspace="8"></td>
        </tr>
        <tr>
        <td height="20"><? web/imcms/lang/jsp/admin/admin_manager_search.jsp/3 ?></td>

        <td colspan="3">
        <select name="permission">
            <option value="CRE"><? web/imcms/lang/jsp/admin/admin_manager_search.jsp/permission_option1 ?>
            <option value="PUB"><? web/imcms/lang/jsp/admin/admin_manager_search.jsp/permission_option2 ?>
            <option value="MOD"><? web/imcms/lang/jsp/admin/admin_manager_search.jsp/permission_option3 ?>
        </select></td>
    </tr>
    <tr>
        <td height="24"><? web/imcms/lang/jsp/admin/admin_manager.jsp/16 ?></td>

        <td colspan="3">
        <table border="0" cellspacing="0" cellpadding="2">
        <tr>
            <td><input type="checkbox" name="<%= AdminManager.REQUEST_PARAMETER__STATUS %>" value="0" <%= isSelected(0, status) ? "checked" : "" %> ></td>
            <td><? web/imcms/lang/jsp/admin/admin_manager_search.jsp/4 ?> &nbsp;</td>
            <td><input type="checkbox" name="<%= AdminManager.REQUEST_PARAMETER__STATUS %>" value="2" <%= isSelected(2, status) ? "checked" : "" %> ></td>
            <td><? web/imcms/lang/jsp/admin/admin_manager_search.jsp/5 ?> &nbsp;</td>
            <td><input type="checkbox" name="<%= AdminManager.REQUEST_PARAMETER__STATUS %>" value="1" <%= isSelected(1, status) ? "checked" : "" %> ></td>
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
            <select name="date_type">
                <option value="PUB" <%= "a".equals("PUB")? "selected" : "" %> ><? web/imcms/lang/jsp/admin/admin_manager_search.jsp/datetype_option1 ?>
                <option value="PUBC" <%= "a".equals("PUBC")? "selected" : "" %> ><? web/imcms/lang/jsp/admin/admin_manager_search.jsp/datetype_option2 ?>
                <option value="CRE" <%= "a".equals("CRE")? "selected" : "" %> ><? web/imcms/lang/jsp/admin/admin_manager_search.jsp/datetype_option3 ?>
                <option value="ARC" <%= "a".equals("ARC")? "selected" : "" %> ><? web/imcms/lang/jsp/admin/admin_manager_search.jsp/datetype_option4 ?>
                <option value="MOD" <%= "a".equals("MOD")? "selected" : "" %> ><? web/imcms/lang/jsp/admin/admin_manager_search.jsp/datetype_option5 ?>
            </select></td>

        </tr>
        </table></td>
    </tr>
    <tr>
        <td height="24">&nbsp;</td>
        <td colspan="3">
        <table border="0" cellspacing="0" cellpadding="0">
        <tr>
            <td><input type="text" name="date_start" value="" size="10" maxlength="10" style="width:65"></td>
            <td nowrap>&nbsp; &nbsp; - &nbsp; &nbsp;</td>
            <td><input type="text" name="date_end" value="" size="10" maxlength="10" style="width:65"></td>
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
        <% SORTORDER_OPTION_SELECTED = "MOD" ; %>
        <select name="new_sortorder">
            <%@ include file="admin/admin_manager_inc_sortorder_select_option.jsp" %>
        </select></td>

        <td><? web/imcms/lang/jsp/admin/admin_manager_search.jsp/8 ?></td>
        <td>
        <select name="hits_per_page">
            <%
                Integer[] ranges = new Integer[] {
                    new Integer( 5 ),
                    new Integer( 10 ),
                    new Integer( 20 ),
                    new Integer( 100 ),
                    new Integer( 1000 ),
                } ;
            %>
            <%=
                Html.createOptionList(Arrays.asList(ranges), new Integer( documentsPerPage ), new Transformer() {
                    public Object transform( Object input ) {
                        return new String[] {""+input, ""+input} ;
                    }
                })
            %>
        </select></td>
    </tr>

    <tr>
        <td colspan="4"><img src="<%= IMG_PATH %>/1x1_cccccc.gif"
        width="100%" height="1" vspace="8"></td>
    </tr>
    <tr>
        <td>&nbsp;</td>
        <td colspan="3" align="right">
        <input type="submit" name="<%= SearchDocumentsPage.REQUEST_PARAMETER__SEARCH_BUTTON %>" value="<? global/Search ?>" class="imcmsFormBtn" style="width:100">
        <input type="reset" name="reset_btn" value="<? global/Reset ?>" class="imcmsFormBtn" style="width:100"></td>
    </tr>


    </table>