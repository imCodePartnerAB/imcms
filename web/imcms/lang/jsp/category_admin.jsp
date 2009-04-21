<%@ page import="com.imcode.imcms.servlet.superadmin.AdminCategories,
                 imcode.server.document.CategoryDomainObject,
                 imcode.server.document.CategoryTypeDomainObject"
        contentType="text/html; charset=UTF-8" %>
<%@taglib prefix="vel" uri="imcmsvelocity"%>
<vel:velocity><%

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
<html>
<head>
<title><? templates/sv/AdminManager_adminTask_element.htm/17 ?></title>

<link rel="stylesheet" type="text/css" href="$contextPath/imcms/css/imcms_admin.css.jsp">
<script src="$contextPath/imcms/$language/scripts/imcms_admin.js.jsp" type="text/javascript"></script>

</head>
<body onLoad="focusField('main','template')">

#gui_outer_start()
#gui_head( "<? templates/sv/AdminManager_adminTask_element.htm/17 ?>" )

<table border="0" cellspacing="0" cellpadding="2" width="660" align="center">
<form action="AdminManager">
<tr>
	<td>
	<input type="submit" value="<? global/back ?>" title="<? global/back ?>" class="imcmsFormBtn">
	<input type="button" value="<? global/help ?>" title="<? global/help ?>" class="imcmsFormBtn" onClick="openHelpW('CategoryAdmin')"></td>
</tr>
</form>
<tr>
	<td class="imcmsAdmText"><b class="white"><? install/htdocs/sv/jsp/category_admin/category_type ?>:</b></td>
</tr>
<form name="head" action="AdminCategories" method="post">
<tr>
	<td>
	<input type="submit" class="imcmsFormBtnSub<%=
	inAddCategoryTypeMode ? "Disabled\" disabled" : "\"" %> name="<%=
	AdminCategories.PARAMETER_MODE__ADD_CATEGORY_TYPE %>" value="<? global/create ?>">
	<input type="submit" class="imcmsFormBtnSub<%=
	inEditCategoryTypeMode ? "Disabled\" disabled" : "\"" %> name="<%=
	AdminCategories.PARAMETER_MODE__EDIT_CATEGORY_TYPE %>" value="<? global/edit ?>" >
	<input type="submit" class="imcmsFormBtnSub<%=
	inDeleteCategoryTypeMode ? "Disabled\" disabled" : "\"" %> name="<%=
	AdminCategories.PARAMETER_MODE__DELETE_CATEGORY_TYPE %>" value="<? global/remove ?>"></td>
</tr>
<tr>
	<td class="imcmsAdmText"><b class="white"><? install/htdocs/sv/jsp/category_admin/category ?>:</b></td>
</tr>
<tr>
	<td>
	<input type="submit" class="imcmsFormBtnSub<%=
	inAddCategoryMode ? "Disabled\" disabled" : "\"" %> name="<%=
	AdminCategories.PARAMETER_MODE__ADD_CATEGORY %>" value="<? global/create ?>">
	<input type="submit" class="imcmsFormBtnSub<%=
	inEditCategoryMode ? "Disabled\" disabled" : "\"" %> name="<%=
	AdminCategories.PARAMETER_MODE__EDIT_CATEGORY %>" value="<? global/edit ?>">
	<input type="submit" class="imcmsFormBtnSub<%=
	inDeleteCategoryMode ? "Disabled\" disabled" : "\"" %> name="<%=
	AdminCategories.PARAMETER_MODE__DELETE_CATEGORY %>" value="<? global/remove ?>">
	<input type="submit" class="imcmsFormBtnSub<%=
	inViewCategoryMode ? "Disabled\" disabled" : "\"" %> name="<%=
	AdminCategories.PARAMETER_MODE__VIEW_CATEGORY %>" value="<? global/view ?>"></td>
</tr>
</form>
</table>

#gui_mid()

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

    <%
        CategoryTypeDomainObject categoryTypeToEdit = adminCategoriesPage.getCategoryTypeToEdit();
        if (inDefaultMode || pressedCancelButton) { // default %>
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
        <% request.setAttribute( "categoryType", new CategoryTypeDomainObject( 0,"",1,true, false) );
         %><jsp:include page="category_admin_category_type.jsp"/>

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
                    <%= AdminCategories.createHtmlOptionListOfCategoryTypes(categoryTypeToEdit) %>
                </select></td>
							<td>&nbsp;&nbsp;</td>
							<td><input type="submit" class="imcmsFormBtnSmall" name="<%= AdminCategories.PARAMETER_BUTTON__SELECT_CATEGORY_TYPE_TO_SHOW_OR_REMOVE %>" value="<? global/select ?>"></td>
						</tr>
						</table></td>
        </tr>

        <% if(categoryTypeToEdit != null ){
            request.setAttribute( "categoryType", categoryTypeToEdit );
            %><jsp:include page="category_admin_category_type.jsp"/><%
        }

        if( messageToUser.length() > 0 ) { %>
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
                <%@include file="category_admin_category.included.jsp"%>
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
                    <%= AdminCategories.createHtmlOptionListOfCategoryTypes(categoryTypeToEdit) %>
                </select></td>
							<td>&nbsp;&nbsp;</td>
							<td><input type="submit" class="imcmsFormBtnSmall" name="<%= AdminCategories.PARAMETER_BUTTON__SELECT_CATEGORY_TYPE_TO_SHOW_OR_REMOVE %>" value="<? global/select ?>"></td>
						</tr>
						</table></td>
        </tr>
        <% if (null != categoryTypeToEdit) { %>
        <tr>
            <td width="110" height="24" class="imcmsAdmText" nowrap><? install/htdocs/sv/jsp/category_admin/category ?> &nbsp;</td>
            <td>
						<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td>
                <select name="categories">
                    <%= AdminCategories.createHtmlOptionListOfCategoriesForOneType(categoryTypeToEdit, adminCategoriesPage.getCategoryToEdit()) %>
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
                <%@include file="category_admin_category.included.jsp"%>
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
                    <%= AdminCategories.createHtmlOptionListOfCategoryTypes(categoryTypeToEdit) %>
                </select></td>
							<td>&nbsp;&nbsp;</td>
							<td><input type="submit" class="imcmsFormBtnSmall" name="<%= AdminCategories.PARAMETER_BUTTON__SELECT_CATEGORY_TYPE_TO_SHOW_OR_REMOVE %>" value="<? global/select ?>"></td>
						</tr>
						</table></td>
        </tr>
        <% if(categoryTypeToEdit != null ){ %>
        <tr>
            <td  class="imcmsAdmText" nowrap><? install/htdocs/sv/jsp/category_admin/category ?> &nbsp;</td>
            <td>
						<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td>
                <select name="categories">
                    <%= AdminCategories.createHtmlOptionListOfCategoriesForOneType(categoryTypeToEdit, adminCategoriesPage.getCategoryToEdit()) %>
                </select></td>
							<td>&nbsp;&nbsp;</td>
							<td><input type="submit" class="imcmsFormBtnSmall" name="select_category_to_edit" value="<? global/select ?>"></td>
						</tr>
						</table></td>
        </tr>
        <%}
        if(null != categoryToEdit) {
            boolean readonly = true ;%>
            <%@ include file="category_admin_category.included.jsp"%>
        <%}%>
    <%}  // ---------- delete category type------------
    else if(inDeleteCategoryTypeMode) {
        if(adminCategoriesPage.getNumberOfCategories() > 0 ) {
            messageToUser.append("<? install/htdocs/sv/jsp/category_admin/message_delete_category_type/1 ?> \"");
            messageToUser.append( categoryTypeToEdit.getName() + "\" <? install/htdocs/sv/jsp/category_admin/message_delete_category_type/2 ?> ");
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
                    <%= AdminCategories.createHtmlOptionListOfCategoryTypes(categoryTypeToEdit) %>
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
                    <%= AdminCategories.createHtmlOptionListOfCategoryTypes(categoryTypeToEdit) %>
                </select></td>
							<td>&nbsp;&nbsp;</td>
							<td><input type="submit" class="imcmsFormBtnSmall" name="<%= AdminCategories.PARAMETER_BUTTON__SELECT_CATEGORY_TYPE_TO_SHOW_OR_REMOVE %>" value="<? global/select ?>"></td>
						</tr>
						</table></td>
        </tr>
        <% if(categoryTypeToEdit != null ){ %>
        <tr>
            <td width="110" height="24" class="imcmsAdmText" nowrap><? install/htdocs/sv/jsp/category_admin/category ?> &nbsp;</td>
            <td>
						<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td>
                <select name="categories">
                    <%= AdminCategories.createHtmlOptionListOfCategoriesForOneType(categoryTypeToEdit, adminCategoriesPage.getCategoryToEdit()) %>
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
            <%@ include file="category_admin_category.included.jsp"%>
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
<%
if (!inDefaultMode &&
		(
			inAddCategoryTypeMode ||
			(inEditCategoryTypeMode && null != categoryTypeToEdit) ||
			inAddCategoryMode ||
			(inEditCategoryMode && null != adminCategoriesPage.getCategoryToEdit()) ||
			(inDeleteCategoryMode && null != adminCategoriesPage.getCategoryToEdit())
		)
	) { %>
<tr>
	<td colspan="2">#gui_hr( "blue" )</td>
</tr>
<tr>
	<td colspan="2" align="right"><%
	if (!inDefaultMode) {
		if ( inAddCategoryTypeMode ) { %>
	<input type="submit" class="imcmsFormBtn"<%
			%> name="<%= AdminCategories.PARAMETER_CATEGORY_TYPE_ADD%>" value="<? global/create ?>"><%
		} else if ( inEditCategoryTypeMode && null != categoryTypeToEdit ) { %>
	<input type="submit" class="imcmsFormBtn"<%
			%> name="<%= AdminCategories.PARAMETER_CATEGORY_TYPE_SAVE%>" value="<? global/save ?>" ><%
		} else if ( inAddCategoryMode ) { %>
	<input type="submit" class="imcmsFormBtn"<%
			%> name="category_add" value="<? global/create ?>" ><%
		} else if ( inEditCategoryMode && null != adminCategoriesPage.getCategoryToEdit() ) { %>
	<input type="submit" class="imcmsFormBtn"<%
			%> name="category_save" value="<? global/save ?>" ><%
		} else if ( inDeleteCategoryMode && null != adminCategoriesPage.getCategoryToEdit() ) { %>
	<input type="submit" class="imcmsFormBtn"<%
			%> name="category_delete" value="<? global/remove ?>" ><%
		}
	}  %></td>
</tr><%
} %>
</form>
</table>
#gui_bottom()
#gui_outer_end()
</body>
</html>
</vel:velocity>
