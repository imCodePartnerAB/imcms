<%@ page import="com.imcode.imcms.servlet.superadmin.AdminCategories,
                 com.imcode.imcms.api.Category,
                 imcode.server.document.CategoryDomainObject"%>
<%@taglib prefix="vel" uri="/WEB-INF/velocitytag.tld"%>
<vel:velocity>

#gui_start_of_page( "<? templates/sv/AdminManager_adminTask_element.htm/17 ?>" "AdminManager" "back" 124 "focusField(1,'template')" )

<%
AdminCategories.AdminCategoriesPage adminCategoriesPage = (AdminCategories.AdminCategoriesPage)request.getAttribute(AdminCategories.ATTRIBUTE__FORM_DATA);
String mode = adminCategoriesPage.getMode() ;
StringBuffer messageToUser = new StringBuffer("");

CategoryDomainObject categoryToEdit = adminCategoriesPage.getCategoryToEdit() ;

boolean inAddCategoryTypeMode    = AdminCategories.PARAMETER_MODE__ADD_CATEGORY_TYPE.equals( mode );
boolean inEditCategoryTypeMode   = AdminCategories.PARAMETER_MODE__EDIT_CATEGORY_TYPE.equals( mode );
boolean inDeleteCategoryTypeMode = AdminCategories.PARAMETER_MODE__DELETE_CATEGORY_TYPE.equals( mode );
boolean inAddCategoryMode        = AdminCategories.PARAMETER_MODE__ADD_CATEGORY.equals( mode );
boolean inEditCategoryMode       = AdminCategories.PARAMETER_MODE__EDIT_CATEGORY.equals( mode );
boolean inDeleteCategoryMode     = AdminCategories.PARAMETER_MODE__DELETE_CATEGORY.equals( mode );
boolean inViewCategoryMode       = AdminCategories.PARAMETER_MODE__VIEW_CATEGORY.equals( mode );
boolean pressedCancelButton      = null != request.getParameter(AdminCategories.PARAMETER_BUTTON__CANCEL) ;
boolean inDefaultMode            = AdminCategories.PARAMETER_MODE__DEFAULT.equals( mode ) || pressedCancelButton;

String defaultHeading = "<? install/htdocs/sv/jsp/category_admin/administer_categories ?>";
String heading = defaultHeading ;
if (inDefaultMode) {
	heading = defaultHeading ;
} else if(inAddCategoryTypeMode) {
	heading = "<? install/htdocs/sv/jsp/category_admin/create_category_type ?>";
} else if(inEditCategoryTypeMode) {
	heading = "<? install/htdocs/sv/jsp/category_admin/edit_category_type ?>";
} else if(inDeleteCategoryTypeMode) {
	heading = "<? install/htdocs/sv/jsp/category_admin/remove_category_type ?>";
} else if(inAddCategoryMode) {
	heading = "<? install/htdocs/sv/jsp/category_admin/create_category ?>";
} else if(inEditCategoryMode) {
	heading = "<? install/htdocs/sv/jsp/category_admin/edit_category ?>";
} else if(inDeleteCategoryMode) {
	heading = "<? install/htdocs/sv/jsp/category_admin/remove_category ?>";
} else if(inViewCategoryMode) {
	heading = "<? install/htdocs/sv/jsp/category_admin/view_categories ?>";
}
%>
<table border="0" cellspacing="0" cellpadding="2" width="400">
<form name="head" action="AdminCategories" method="post">
<tr>
	<td colspan="2" class="imcmsAdmText"><b class="lighterBlue"><? install/htdocs/sv/jsp/category_admin/category_type ?>:</b></td>
</tr>
<tr>
	<td colspan="2">
	<input type="submit" class="imcmsFormBtn<%=
	inAddCategoryTypeMode ? "Disabled" : "" %>" name="<%=
	AdminCategories.PARAMETER_MODE__ADD_CATEGORY_TYPE %>" value="<? global/create ?>">
	<input type="submit" class="imcmsFormBtn<%=
	inEditCategoryTypeMode ? "Disabled" : "" %>" name="<%=
	AdminCategories.PARAMETER_MODE__EDIT_CATEGORY_TYPE %>" value="<? global/edit ?>" >
	<input type="submit" class="imcmsFormBtn<%=
	inDeleteCategoryTypeMode ? "Disabled" : "" %>" name="<%=
	AdminCategories.PARAMETER_MODE__DELETE_CATEGORY_TYPE %>" value="<? global/remove ?>"></td>
</tr>
<tr>
	<td colspan="2" class="imcmsAdmText"><b class="lighterBlue"><? install/htdocs/sv/jsp/category_admin/category ?>:</b></td>
</tr>
<tr>
	<td colspan="2">
	<input type="submit" class="imcmsFormBtn<%=
	inAddCategoryMode ? "Disabled" : "" %>" name="<%=
	AdminCategories.PARAMETER_MODE__ADD_CATEGORY %>" value="<? global/create ?>">
	<input type="submit" class="imcmsFormBtn<%=
	inEditCategoryMode ? "Disabled" : "" %>" name="<%=
	AdminCategories.PARAMETER_MODE__EDIT_CATEGORY %>" value="<? global/edit ?>">
	<input type="submit" class="imcmsFormBtn<%=
	inDeleteCategoryMode ? "Disabled" : "" %>" name="<%=
	AdminCategories.PARAMETER_MODE__DELETE_CATEGORY %>" value="<? global/remove ?>">
	<input type="submit" class="imcmsFormBtn<%=
	inViewCategoryMode ? "Disabled" : "" %>" name="<%=
	AdminCategories.PARAMETER_MODE__VIEW_CATEGORY %>" value="<? global/view ?>"></td>
</tr>
<tr>
	<td colspan="2">&nbsp;</td>
</tr>
</form>
</table>


<table border="0" cellspacing="0" cellpadding="2" width="660" align="center">
<form name="main" action="AdminCategories" method="post">
<%
 %>
<tr>
    <td colspan="2">#gui_heading( "<%=heading%>" )</td>
</tr>
<tr>
    <td colspan="2">
    <table border="0" cellpadding="0" cellspacing="0" width="656">

    <% if (inDefaultMode || pressedCancelButton) { // default %>
        <input type="hidden" name="<%= AdminCategories.PARAMETER_MODE__DEFAULT %>" value="1">
        <tr>
            <td width="110" height="24" class="imcmsAdmText" nowrap>
                <? install/htdocs/sv/jsp/category_admin/select_function ?></td>
            <td>&nbsp;</td>
        </tr>
    <%} // ---------- add category type ------------
    else if( inAddCategoryTypeMode ) {

        String categoryAddParameter = request.getParameter( AdminCategories.PARAMETER_CATEGORY_TYPE_ADD );
        boolean uniqueCategoryTypeName = adminCategoriesPage.isUniqueCategoryTypeName();
        if( null != categoryAddParameter && !uniqueCategoryTypeName ) {
            messageToUser.append("<? install/htdocs/sv/jsp/category_admin/thereIsAlreadyaCategoryTypeWithTheName ?> \"");
            messageToUser.append(request.getParameter("name") + "\" ");
            messageToUser.append("<? install/htdocs/sv/jsp/category_admin/message_name_already_exists/3 ?>!");
        }
        %>
        <input type="hidden" name="<%= AdminCategories.PARAMETER_MODE__ADD_CATEGORY_TYPE %>" value="1">
        <tr>
            <td width="110" height="24"><? global/Name ?></td>
            <td><input type="text" name="name" size="30" maxlength="50"></td>
        </tr>
        <tr>
            <td>&nbsp;</td>
            <td>
						<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td height="24"><input type="radio" name="<%=AdminCategories.PARAMETER_MAX_CHOICES%>" value="1" checked></td>
							<td>&nbsp;&nbsp;</td>
							<td><? install/htdocs/sv/jsp/category_admin/singel_choice ?></td>
						</tr>
						</table></td>
        </tr>
        <tr>
            <td>&nbsp;</td>
             <td>
						<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td height="24"><input type="radio" name="<%=AdminCategories.PARAMETER_MAX_CHOICES%>" value="0" ></td>
							<td>&nbsp;&nbsp;</td>
							<td><? install/htdocs/sv/jsp/category_admin/multi_choice ?></td>
						</tr>
						</table></td>
        </tr>

        <%if( messageToUser.length() > 0 ) { %>
        <tr><td colspan="2">&nbsp;</td></tr>
        <tr>
            <td colspan="2" height="24" class="imcmsAdmText" ><font face="Verdana, Arial, Helvetica, sans-serif" size="1" color="red"> <%=messageToUser %> </font></td>
        </tr>
        <%}%>

    <% }  // ---------- edit category type ------------
    else if( inEditCategoryTypeMode ) {

        if( request.getParameter( AdminCategories.PARAMETER_CATEGORY_TYPE_SAVE ) != null && !adminCategoriesPage.isUniqueCategoryTypeName() ) {
            messageToUser.append("<? install/htdocs/sv/jsp/category_admin/thereIsAlreadyaCategoryTypeWithTheName ?> \"");
            messageToUser.append(request.getParameter("name") + "\" ");
            messageToUser.append("<? install/htdocs/sv/jsp/category_admin/message_name_already_exists/3 ?>!");
        }
        %>

        <input type="hidden" name="<%= AdminCategories.PARAMETER_MODE__EDIT_CATEGORY_TYPE %>" value="1">
        <tr>
            <td width="110" height="24" class="imcmsAdmText" nowrap><? install/htdocs/sv/jsp/category_admin/category_type ?> &nbsp;</td>
            <td>
						<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td>
                <select name="<%= AdminCategories.PARAMETER_SELECT__CATEGORY_TYPE_TO_SHOW%>">
                    <%= AdminCategories.createHtmlOptionListOfCategoryTypes(adminCategoriesPage.getCategoryTypeToEdit()) %>
                </select></td>
							<td>&nbsp;&nbsp;</td>
							<td><input type="submit" class="imcmsFormBtnSmall" name="<%= AdminCategories.PARAMETER_BUTTON__SELECT_CATEGORY_TYPE_TO_SHOW_OR_REMOVE %>" value="<? global/select ?>"></td>
						</tr>
						</table></td>
        </tr>

        <% if(adminCategoriesPage.getCategoryTypeToEdit() != null ){  %>
            <tr>
                <td height="24" class="imcmsAdmText" nowrap><? install/htdocs/sv/jsp/category_admin/new_name ?> &nbsp;</td>
                <td><input type="text" name="name" size="30" maxlength="50" value="<%=adminCategoriesPage.getCategoryTypeToEdit().getName()%>"></td>
            </tr>

            <tr>
                <td>&nbsp;</td>
                <td>
							<table border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td height="24"><input type="radio" name="<%=AdminCategories.PARAMETER_MAX_CHOICES%>" value="1" <%= adminCategoriesPage.getCategoryTypeToEdit().getMaxChoices() == 1 ?  "checked" : "" %> ></td>
								<td>&nbsp;&nbsp;</td>
								<td><? install/htdocs/sv/jsp/category_admin/singel_choice ?></td>
							</tr>
							</table></td>
            </tr>
            <tr>
                <td>&nbsp;</td>
                 <td>
							<table border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td height="24"><input type="radio" name="<%=AdminCategories.PARAMETER_MAX_CHOICES%>" value="0" <%= adminCategoriesPage.getCategoryTypeToEdit().getMaxChoices() == 0 ? "checked" : "" %> ></td>
								<td>&nbsp;&nbsp;</td>
								<td><? install/htdocs/sv/jsp/category_admin/multi_choice ?></td>
							</tr>
							</table></td>
            </tr>
        <%}%>

        <%if( messageToUser.length() > 0 ) { %>
        <tr><td colspan="2">&nbsp;</td></tr>
        <tr>
            <td colspan="2" height="24" class="imcmsAdmText" ><font face="Verdana, Arial, Helvetica, sans-serif" size="1" color="red"> <%=messageToUser %> </font></td>
        </tr>
        <%}%>

    <%} // ------ add category -------
    else if(inAddCategoryMode) {

        if( AdminCategories.createHtmlOptionListOfCategoryTypes(null).equals("") ){
            messageToUser.append("<? install/htdocs/sv/jsp/category_admin/message_create_category_type_first ?> ");
            inAddCategoryMode = false;
        }
        if( request.getParameter("category_add") != null && !adminCategoriesPage.getUniqueCategoryName() ) {
            messageToUser.append("<? install/htdocs/sv/jsp/category_admin/message_name_already_exists/1 ?> \"");
            messageToUser.append(request.getParameter("name") + "\" ");
            messageToUser.append("<? install/htdocs/sv/jsp/category_admin/message_name_already_exists/2 ?> \"" + categoryToEdit.getType().getName() + "\". ");
            messageToUser.append("<? install/htdocs/sv/jsp/category_admin/message_name_already_exists/3 ?>!");
        }
        %>


        <input type="hidden" name="<%= AdminCategories.PARAMETER_MODE__ADD_CATEGORY %>" value="1">
        <% if (null != categoryToEdit) {
            boolean readonly = false;
            if( !AdminCategories.createHtmlOptionListOfCategoryTypes(null).equals("") ){ %>
                <%@include file="category_admin_category.jsp"%>
            <%}
        }

        if( messageToUser.length() > 0 ) { %>
            <tr><td colspan="2">&nbsp;</td></tr>
            <tr>
                <td colspan="2" height="24" class="imcmsAdmText" ><font face="Verdana, Arial, Helvetica, sans-serif" size="1" color="red"> <%=messageToUser %> </font></td>
            </tr>
        <% }
    }  // ---------- edit category ------------
    else if(inEditCategoryMode) {

        if( request.getParameter("category_save") != null && !adminCategoriesPage.getUniqueCategoryName() ) {
            messageToUser.append("<? install/htdocs/sv/jsp/category_admin/message_name_already_exists/1 ?> \"");
            messageToUser.append(request.getParameter("name") + "\" ");
            messageToUser.append("<? install/htdocs/sv/jsp/category_admin/message_name_already_exists/2 ?> \"" + adminCategoriesPage.getCategoryToEdit().getType().getName() + "\". ");
            messageToUser.append("<? install/htdocs/sv/jsp/category_admin/message_name_already_exists/3 ?>!");
        }
        %>
        <input type="hidden" name="<%= AdminCategories.PARAMETER_MODE__EDIT_CATEGORY %>" value="1">
        <input type="hidden" name="oldName" value="<%=adminCategoriesPage.getCategoryToEdit() != null ? adminCategoriesPage.getCategoryToEdit().getName() : "" %>">

        <tr>
            <td width="110" height="24" class="imcmsAdmText" nowrap><? install/htdocs/sv/jsp/category_admin/category_type ?> &nbsp;</td>
            <td>
						<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td>
                <select name="<%= AdminCategories.PARAMETER_SELECT__CATEGORY_TYPE_TO_SHOW %>">
                    <%= AdminCategories.createHtmlOptionListOfCategoryTypes(adminCategoriesPage.getCategoryTypeToEdit()) %>
                </select></td>
							<td>&nbsp;&nbsp;</td>
							<td><input type="submit" class="imcmsFormBtnSmall" name="<%= AdminCategories.PARAMETER_BUTTON__SELECT_CATEGORY_TYPE_TO_SHOW_OR_REMOVE %>" value="<? global/select ?>"></td>
						</tr>
						</table></td>
        </tr>
        <% if (null != adminCategoriesPage.getCategoryTypeToEdit()) { %>
        <tr>
            <td width="110" height="24" class="imcmsAdmText" nowrap><? install/htdocs/sv/jsp/category_admin/category ?> &nbsp;</td>
            <td>
						<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td>
                <select name="categories">
                    <%= AdminCategories.createHtmlOptionListOfCategoriesForOneType(adminCategoriesPage.getCategoryTypeToEdit(), adminCategoriesPage.getCategoryToEdit()) %>
                </select></td>
							<td>&nbsp;&nbsp;</td>
							<td><input type="submit" class="imcmsFormBtnSmall" name="select_category_to_edit" value="<? global/select ?>"></td>
						</tr>
						</table></td>
        </tr>
        <%}%>


        <%
            if(categoryToEdit != null ){
                boolean readonly = false;
            %>
                <%@include file="category_admin_category.jsp"%>
       <%}%>
    <%if( messageToUser.length() > 0 ) { %>
     <tr><td>&nbsp;</td></tr>
     <tr>
        <td colspan="2" height="24" class="imcmsAdmText" ><font face="Verdana, Arial, Helvetica, sans-serif" size="1" color="red"> <%=messageToUser %> </font></td>
     </tr>
     <%}
    }  // ---------- View category ------------
    else if(inViewCategoryMode) { %>
        <input type="hidden" name="<%= AdminCategories.PARAMETER_MODE__VIEW_CATEGORY %>" value="1">

        <tr>
            <td width="110" height="24" class="imcmsAdmText" nowrap><? install/htdocs/sv/jsp/category_admin/category_type ?> &nbsp;</td>
            <td>
						<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td>
                <select name="<%= AdminCategories.PARAMETER_SELECT__CATEGORY_TYPE_TO_SHOW %>">
                    <%= AdminCategories.createHtmlOptionListOfCategoryTypes(adminCategoriesPage.getCategoryTypeToEdit()) %>
                </select></td>
							<td>&nbsp;&nbsp;</td>
							<td><input type="submit" class="imcmsFormBtnSmall" name="<%= AdminCategories.PARAMETER_BUTTON__SELECT_CATEGORY_TYPE_TO_SHOW_OR_REMOVE %>" value="<? global/select ?>"></td>
						</tr>
						</table></td>
        </tr>
        <% if(adminCategoriesPage.getCategoryTypeToEdit() != null ){ %>
        <tr>
            <td  class="imcmsAdmText" nowrap><? install/htdocs/sv/jsp/category_admin/category ?> &nbsp;</td>
            <td>
						<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td>
                <select name="categories">
                    <%= AdminCategories.createHtmlOptionListOfCategoriesForOneType(adminCategoriesPage.getCategoryTypeToEdit(), adminCategoriesPage.getCategoryToEdit()) %>
                </select></td>
							<td>&nbsp;&nbsp;</td>
							<td><input type="submit" class="imcmsFormBtnSmall" name="select_category_to_edit" value="<? global/select ?>"></td>
						</tr>
						</table></td>
        </tr>
        <%}
        if(null != categoryToEdit) {
            boolean readonly = true ;%>
            <%@ include file="category_admin_category.jsp"%>
        <%}%>
    <%}  // ---------- delete category type------------
    else if(inDeleteCategoryTypeMode) {
        if(adminCategoriesPage.getNumberOfCategories() > 0 ) {
            messageToUser.append("<? install/htdocs/sv/jsp/category_admin/message_delete_category_type/1 ?> \"");
            messageToUser.append( adminCategoriesPage.getCategoryTypeToEdit().getName() + "\" <? install/htdocs/sv/jsp/category_admin/message_delete_category_type/2 ?> ");
            messageToUser.append(adminCategoriesPage.getNumberOfCategories() +"");
            messageToUser.append(" <? install/htdocs/sv/jsp/category_admin/message_delete_category_type/3 ?>. ");
            messageToUser.append("<? install/htdocs/sv/jsp/category_admin/message_delete_category_type/4 ?>!");
        } %>
        <input type="hidden" name="<%= AdminCategories.PARAMETER_MODE__DELETE_CATEGORY_TYPE %>" value="1">
        <tr>
            <td width="110" height="24" class="imcmsAdmText" nowrap><? install/htdocs/sv/jsp/category_admin/category_type ?> &nbsp;</td>
            <td>
						<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td>
                <select name="<%= AdminCategories.PARAMETER_SELECT__CATEGORY_TYPE_TO_SHOW %>">
                    <%= AdminCategories.createHtmlOptionListOfCategoryTypes(adminCategoriesPage.getCategoryTypeToEdit()) %>
                </select></td>
							<td>&nbsp;&nbsp;</td>
							<td><input type="submit" class="imcmsFormBtnSmall" name="<%= AdminCategories.PARAMETER_BUTTON__SELECT_CATEGORY_TYPE_TO_SHOW_OR_REMOVE %>" value="<? global/remove ?>"></td>
						</tr>
						</table></td>
        </tr>
    <%if( messageToUser.length() > 0 ) { %>
    <tr><td colspan="2">&nbsp;</td></tr>
    <tr>
        <td colspan="2" height="24" class="imcmsAdmText" ><font face="Verdana, Arial, Helvetica, sans-serif" size="1" color="red"> <%=messageToUser %> </font></td>
    </tr>
    <%}

    } // ------------  delete category -------------------
    else if(inDeleteCategoryMode) {
        if(adminCategoriesPage.getDocumentsOfOneCategory() != null && adminCategoriesPage.getDocumentsOfOneCategory().length > 0 ) {
            messageToUser.append("<? install/htdocs/sv/jsp/category_admin/message_delete_category/1 ?> \"");
            messageToUser.append( adminCategoriesPage.getCategoryToEdit().getName() + "\" <? install/htdocs/sv/jsp/category_admin/message_delete_category/2 ?> ");
            messageToUser.append(adminCategoriesPage.getDocumentsOfOneCategory().length +"");
            messageToUser.append(" <? global/document ?>. ");
            messageToUser.append("<? install/htdocs/sv/jsp/category_admin/message_delete_category/3 ?>?");
        } %>
        <input type="hidden" name="<%= AdminCategories.PARAMETER_MODE__DELETE_CATEGORY %>" value="1">
        <tr>
            <td width="110" height="24" class="imcmsAdmText" nowrap><? install/htdocs/sv/jsp/category_admin/category_type ?> &nbsp;</td>
            <td>
						<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td>
                <select name="<%= AdminCategories.PARAMETER_SELECT__CATEGORY_TYPE_TO_SHOW %>">
                    <%= AdminCategories.createHtmlOptionListOfCategoryTypes(adminCategoriesPage.getCategoryTypeToEdit()) %>
                </select></td>
							<td>&nbsp;&nbsp;</td>
							<td><input type="submit" class="imcmsFormBtnSmall" name="<%= AdminCategories.PARAMETER_BUTTON__SELECT_CATEGORY_TYPE_TO_SHOW_OR_REMOVE %>" value="<? global/select ?>"></td>
						</tr>
						</table></td>
        </tr>
        <% if(adminCategoriesPage.getCategoryTypeToEdit() != null ){ %>
        <tr>
            <td width="110" height="24" class="imcmsAdmText" nowrap><? install/htdocs/sv/jsp/category_admin/category ?> &nbsp;</td>
            <td>
						<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td>
                <select name="categories">
                    <%= AdminCategories.createHtmlOptionListOfCategoriesForOneType(adminCategoriesPage.getCategoryTypeToEdit(), adminCategoriesPage.getCategoryToEdit()) %>
                </select></td>
							<td>&nbsp;&nbsp;</td>
							<td><input type="submit" class="imcmsFormBtnSmall" name="select_category_to_edit" value="<? global/select ?>"></td>
						</tr>
						</table></td>
        </tr>
        <tr><td colspan="2">&nbsp;</td></tr>
        <% }
        if(null != categoryToEdit) {
            boolean readonly = true ;%>
            <%@ include file="category_admin_category.jsp"%>
        <%}%>
         <%if( messageToUser.length() > 0 ) { %>
        <tr><td colspan="2">&nbsp;</td></tr>
        <tr>
            <td colspan="2" height="24" class="imcmsAdmText" ><font face="Verdana, Arial, Helvetica, sans-serif" size="1" color="red"> <%=messageToUser %> </font></td>
        </tr>
        <%}
    } %>
</table></td>
</tr>

<tr>
    <td colspan="2">#gui_hr( "blue" )</td>
</tr>
<tr>
    <td colspan="2" align="right">

        <%
            if (!inDefaultMode) {
                if( inAddCategoryTypeMode ) { %>
                    <input type="submit" class="imcmsFormBtn" name="<%=AdminCategories.PARAMETER_CATEGORY_TYPE_ADD%>" value="<? global/create ?>">
                <%}else if( inEditCategoryTypeMode && null != adminCategoriesPage.getCategoryTypeToEdit() ) { %>
                    <input type="submit" class="imcmsFormBtn" name="<%=AdminCategories.PARAMETER_CATEGORY_TYPE_SAVE%>" value="<? global/save ?>" >
                <%}else if( inAddCategoryMode ) { %>
                    <input type="submit" class="imcmsFormBtn" name="category_add" value="<? global/create ?>" >
                <%}else if( inEditCategoryMode && null != adminCategoriesPage.getCategoryToEdit() ) { %>
                    <input type="submit" class="imcmsFormBtn" name="category_save" value="<? global/save ?>" >
                <%}else if( inDeleteCategoryMode && null != adminCategoriesPage.getCategoryToEdit() ) { %>
                    <input type="submit" class="imcmsFormBtn" name="category_delete" value="<? global/remove ?>" >
                <%}%>
        <%  }  %>

        </td>
</tr>
</form>
</table>
#gui_bottom()
#gui_outer_end()
</body>
</html>
</vel:velocity>
