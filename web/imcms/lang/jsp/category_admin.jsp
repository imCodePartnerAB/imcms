<%@ page import="com.imcode.imcms.servlet.superadmin.AdminCategories,
                 com.imcode.imcms.api.Category,
                 imcode.server.document.CategoryDomainObject"%>
<%@taglib prefix="vel" uri="/WEB-INF/velocitytag.tld"%>
<vel:velocity>
<html>
<head>


<title><? install/htdocs/sv/jsp/category_admin/title ?></title>
<link rel="stylesheet" type="text/css" href="$contextPath/imcms/css/imcms_admin.css.jsp">
<script src="$contextPath/imcms/$language/scripts/imcms_admin.js" type="text/javascript"></script>

</head>
<body bgcolor="#FFFFFF" onLoad="focusField(1,'template')">
#gui_outer_start()
#gui_head("<? global/imcms_administration ?>")
<%
    AdminCategories.FormData formData = (AdminCategories.FormData)request.getAttribute(AdminCategories.ATTRIBUTE__FORM_DATA);
    StringBuffer messageToUser = new StringBuffer("");
    CategoryDomainObject categoryToEdit = formData.getCategoryToEdit() ;
%>

<table border="0" cellspacing="0" cellpadding="2" width="660" align="center">
<form name="head" action="AdminCategories" method="post">
    <tr>
        <td colspan="2" class="imcmsAdmText"><b class="white"><? install/htdocs/sv/jsp/category_admin/category_type ?>:</b></td>
    </tr>
    <tr>
    <td colspan="2">
        <input type="submit" class="imcmsFormBtn" name="<%= AdminCategories.PARAMETER_MODE__ADD_CATEGORY_TYPE %>" value="<? global/create ?>">
        <input type="submit" class="imcmsFormBtn" name="<%= AdminCategories.PARAMETER_MODE__EDIT_CATEGORY_TYPE %>" value="<? global/edit ?>" >
        <input type="submit" class="imcmsFormBtn" name="<%= AdminCategories.PARAMETER_MODE__DELETE_CATEGORY_TYPE %>" value="<? global/remove ?>"></td>
    </tr>
    <tr>
    <td colspan="2" class="imcmsAdmText"><b class="white"><? install/htdocs/sv/jsp/category_admin/category ?>:</b></td>
    </tr>
    <tr>
    <td colspan="2">
        <input type="submit" class="imcmsFormBtn" name="<%= AdminCategories.PARAMETER_MODE__ADD_CATEGORY %>" value="<? global/create ?>">
        <input type="submit" class="imcmsFormBtn" name="<%= AdminCategories.PARAMETER_MODE__EDIT_CATEGORY %>" value="<? global/edit ?>">
        <input type="submit" class="imcmsFormBtn" name="<%= AdminCategories.PARAMETER_MODE__DELETE_CATEGORY %>" value="<? global/remove ?>">
        <input type="submit" class="imcmsFormBtn" name="<%= AdminCategories.PARAMETER_MODE__VIEW_CATEGORY %>" value="<? global/view ?>">
    <td>
        <input type="button" value="<? global/help ?>" title="Open help" class="imcmsFormBtn" onClick="openHelpW(124)"></td>
    </tr>
</form>
</table>
#gui_mid()
<table border="0" cellspacing="0" cellpadding="2" width="660" align="center">
<form name="main" action="AdminCategories" method="post">
<%

    boolean inAddCategoryTypeMode = null != request.getParameter(AdminCategories.PARAMETER_MODE__ADD_CATEGORY_TYPE);
    boolean inEditCategoryTypeMode = null != request.getParameter(AdminCategories.PARAMETER_MODE__EDIT_CATEGORY_TYPE) ;
    boolean inDeleteCategoryTypeMode = null != request.getParameter(AdminCategories.PARAMETER_MODE__DELETE_CATEGORY_TYPE) ;
    boolean inAddCategoryMode = null != request.getParameter(AdminCategories.PARAMETER_MODE__ADD_CATEGORY) ;
    boolean inEditCategoryMode = null != request.getParameter(AdminCategories.PARAMETER_MODE__EDIT_CATEGORY) ;
    boolean inDeleteCategoryMode = null != request.getParameter(AdminCategories.PARAMETER_MODE__DELETE_CATEGORY) ;
    boolean inViewCategoryMode = null != request.getParameter(AdminCategories.PARAMETER_MODE__VIEW_CATEGORY);
    boolean pressedCancelButton = null != request.getParameter(AdminCategories.PARAMETER_BUTTON__CANCEL);
    boolean inDefaultMode = null != request.getParameter(AdminCategories.PARAMETER_MODE__DEFAULT) || pressedCancelButton;

    String defaultHeading = "<? install/htdocs/sv/jsp/category_admin/administer_categories ?>";
    String heading = defaultHeading ;
    if (inDefaultMode) {
        heading = defaultHeading ;
    } else if(inAddCategoryTypeMode){
        heading = "<? install/htdocs/sv/jsp/category_admin/create_category_type ?>";
    }else if(inEditCategoryTypeMode){
        heading = "<? install/htdocs/sv/jsp/category_admin/edit_category_type ?>";
    }else if(inDeleteCategoryTypeMode){
        heading = "<? install/htdocs/sv/jsp/category_admin/remove_category_type ?>";
    }else if(inAddCategoryMode){
        heading = "<? install/htdocs/sv/jsp/category_admin/create_category ?>";
    }else if(inEditCategoryMode){
        heading = "<? install/htdocs/sv/jsp/category_admin/edit_category ?>";
    }else if(inDeleteCategoryMode){
        heading = "<? install/htdocs/sv/jsp/category_admin/remove_category ?>";
    }else if(inViewCategoryMode) {
        heading = "<? install/htdocs/sv/jsp/category_admin/view_categories ?>";
    }
 %>
<tr>
    <td colspan="2">#gui_heading( "<%=heading%>" )</td>
</tr>
<tr>
    <td colspan="2">
    <table border="0" cellpadding="0" cellspacing="0">

    <% if (inDefaultMode || pressedCancelButton) { // default %>
        <input type="hidden" name="<%= AdminCategories.PARAMETER_MODE__DEFAULT %>" value="1">
        <tr>
            <td width="80" height="24" class="imcmsAdmText" nowrap>
                <? install/htdocs/sv/jsp/category_admin/select_function ?></td>
            <td>&nbsp;</td>
        </tr>
    <%} // ---------- add category type ------------
    else if(request.getParameter(AdminCategories.PARAMETER_MODE__ADD_CATEGORY_TYPE) != null ) {

        String categoryAddParameter = request.getParameter( AdminCategories.PARAMETER_CATEGORY_TYPE_ADD );
        boolean uniqueCategoryTypeName = formData.isUniqueCategoryTypeName();
        if( null != categoryAddParameter && !uniqueCategoryTypeName ) {
            messageToUser.append("<? install/htdocs/sv/jsp/category_admin/thereIsAlreadyaCategoryTypeWithTheName ?> \"");
            messageToUser.append(request.getParameter("name") + "\" ");
            messageToUser.append("<? install/htdocs/sv/jsp/category_admin/message_name_already_exists/3 ?>!");
        }
        %>
        <input type="hidden" name="<%= AdminCategories.PARAMETER_MODE__ADD_CATEGORY_TYPE %>" value="1">
        <tr>
            <td width="80" height="24" class="imcmsAdmText" nowrap><? global/Name ?></td>
            <td>&nbsp;</td>
        </tr>
        <tr>
            <td>&nbsp;</td>
            <td><input type="text" name="name" size="30" maxlength="50"></td>
        </tr>
        <tr>
            <td>&nbsp;</td>
            <td height="24" class="imcmsAdmText" nowrap>
             <input type="radio" name="<%=AdminCategories.PARAMETER_MAX_CHOICES%>" value="1" checked>&nbsp;<? install/htdocs/sv/jsp/category_admin/singel_choice ?></td>
        </tr>
        <tr>
            <td>&nbsp;</td>
             <td height="24" class="imcmsAdmText" nowrap>
              <input type="radio" name="<%=AdminCategories.PARAMETER_MAX_CHOICES%>" value="0" >&nbsp;<? install/htdocs/sv/jsp/category_admin/multi_choice ?></td>
        </tr>

        <%if( messageToUser.length() > 0 ) { %>
        <tr><td colspan="2">&nbsp;</td></tr>
        <tr>
            <td colspan="2" height="24" class="imcmsAdmText" ><font face="Verdana, Arial, Helvetica, sans-serif" size="1" color="red"> <%=messageToUser %> </font></td>
        </tr>
        <%}%>

    <% }  // ---------- edit category type ------------
    else if(request.getParameter(AdminCategories.PARAMETER_MODE__EDIT_CATEGORY_TYPE) != null ) {

        if( request.getParameter( AdminCategories.PARAMETER_CATEGORY_TYPE_SAVE ) != null && !formData.isUniqueCategoryTypeName() ) {
            messageToUser.append("<? install/htdocs/sv/jsp/category_admin/thereIsAlreadyaCategoryTypeWithTheName ?> \"");
            messageToUser.append(request.getParameter("name") + "\" ");
            messageToUser.append("<? install/htdocs/sv/jsp/category_admin/message_name_already_exists/3 ?>!");
        }
        %>

        <input type="hidden" name="<%= AdminCategories.PARAMETER_MODE__EDIT_CATEGORY_TYPE %>" value="1">
        <tr>
            <td width="80" height="24" class="imcmsAdmText" nowrap><? install/htdocs/sv/jsp/category_admin/category_type ?> &nbsp;</td>
            <td>
                <select name="<%= AdminCategories.PARAMETER_SELECT__CATEGORY_TYPE_TO_SHOW%>">
                    <%= AdminCategories.createHtmlOptionListOfCategoryTypes(formData.getCategoryTypeToEdit()) %>
                </select> &nbsp; <input type="submit" class="imcmsFormBtn" name="<%= AdminCategories.PARAMETER_BUTTON__SELECT_CATEGORY_TYPE_TO_SHOW_OR_REMOVE %>" value="<? global/select ?>"></td>
        </tr>

        <% if(formData.getCategoryTypeToEdit() != null ){  %>
            <tr>
                <td height="24" class="imcmsAdmText" nowrap><? install/htdocs/sv/jsp/category_admin/new_name ?> &nbsp;</td>
                <td><input type="text" name="name" size="30" maxlength="50" value="<%=formData.getCategoryTypeToEdit().getName()%>"></td>
            </tr>

            <tr>
                <td>&nbsp;</td>
                <td height="24" class="imcmsAdmText" nowrap>
                 <input type="radio" name="<%=AdminCategories.PARAMETER_MAX_CHOICES%>" value="1" <%= formData.getCategoryTypeToEdit().getMaxChoices() == 1 ?  "checked" : "" %> >
                   &nbsp;<? install/htdocs/sv/jsp/category_admin/singel_choice ?></td>
            </tr>
            <tr>
                <td>&nbsp;</td>
                 <td height="24" class="imcmsAdmText" nowrap>
                  <input type="radio" name="<%=AdminCategories.PARAMETER_MAX_CHOICES%>" value="0" <%= formData.getCategoryTypeToEdit().getMaxChoices() == 0 ? "checked" : "" %> >
                    &nbsp;<? install/htdocs/sv/jsp/category_admin/multi_choice ?></td>
            </tr>
        <%}%>

        <%if( messageToUser.length() > 0 ) { %>
        <tr><td colspan="2">&nbsp;</td></tr>
        <tr>
            <td colspan="2" height="24" class="imcmsAdmText" ><font face="Verdana, Arial, Helvetica, sans-serif" size="1" color="red"> <%=messageToUser %> </font></td>
        </tr>
        <%}%>

    <%} // ------ add category -------
    else if(request.getParameter(AdminCategories.PARAMETER_MODE__ADD_CATEGORY) != null) {

        if( request.getParameter("category_add") != null && !formData.getUniqueCategoryName() ) {
            messageToUser.append("<? install/htdocs/sv/jsp/category_admin/message_name_already_exists/1 ?> \"");
            messageToUser.append(request.getParameter("name") + "\" ");
            messageToUser.append("<? install/htdocs/sv/jsp/category_admin/message_name_already_exists/2 ?> \"" + categoryToEdit.getType().getName() + "\". ");
            messageToUser.append("<? install/htdocs/sv/jsp/category_admin/message_name_already_exists/3 ?>!");
        }
    %>


        <input type="hidden" name="<%= AdminCategories.PARAMETER_MODE__ADD_CATEGORY %>" value="1">
    <% if (null != categoryToEdit) {
        boolean readonly = false; %>

        <%@include file="category_admin_category.jsp"%>
    <% } %>

    <%if( messageToUser.length() > 0 ) { %>
    <tr><td colspan="2">&nbsp;</td></tr>
    <tr>
        <td colspan="2" height="24" class="imcmsAdmText" ><font face="Verdana, Arial, Helvetica, sans-serif" size="1" color="red"> <%=messageToUser %> </font></td>
    </tr>
    <%}
    }  // ---------- edit category ------------
    else if(null != request.getParameter(AdminCategories.PARAMETER_MODE__EDIT_CATEGORY)) {

        if( request.getParameter("category_save") != null && !formData.getUniqueCategoryName() ) {
            messageToUser.append("<? install/htdocs/sv/jsp/category_admin/message_name_already_exists/1 ?> \"");
            messageToUser.append(request.getParameter("name") + "\" ");
            messageToUser.append("<? install/htdocs/sv/jsp/category_admin/message_name_already_exists/2 ?> \"" + formData.getCategoryToEdit().getType().getName() + "\". ");
            messageToUser.append("<? install/htdocs/sv/jsp/category_admin/message_name_already_exists/3 ?>!");
        }
        %>
        <input type="hidden" name="<%= AdminCategories.PARAMETER_MODE__EDIT_CATEGORY %>" value="1">
        <input type="hidden" name="oldName" value="<%=formData.getCategoryToEdit() != null ? formData.getCategoryToEdit().getName() : "" %>">

        <tr>
            <td width="80" height="24" class="imcmsAdmText" nowrap><? install/htdocs/sv/jsp/category_admin/category_type ?> &nbsp;</td>
            <td>
                <select name="<%= AdminCategories.PARAMETER_SELECT__CATEGORY_TYPE_TO_SHOW %>">
                    <%= AdminCategories.createHtmlOptionListOfCategoryTypes(formData.getCategoryTypeToEdit()) %>
                </select> &nbsp; <input type="submit" class="imcmsFormBtn" name="<%= AdminCategories.PARAMETER_BUTTON__SELECT_CATEGORY_TYPE_TO_SHOW_OR_REMOVE %>" value="<? global/select ?>"></td>
        </tr>
        <% if (null != formData.getCategoryTypeToEdit()) { %>
        <tr>
            <td width="80" height="24" class="imcmsAdmText" nowrap><? install/htdocs/sv/jsp/category_admin/category ?> &nbsp;</td>
            <td>
                <select name="categories">
                    <%= AdminCategories.createHtmlOptionListOfCategoriesForOneType(formData.getCategoryTypeToEdit(), formData.getCategoryToEdit()) %>
                </select> &nbsp; <input type="submit" class="imcmsFormBtn" name="select_category_to_edit" value="<? global/select ?>"></td>
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
    else if(request.getParameter(AdminCategories.PARAMETER_MODE__VIEW_CATEGORY) != null) { %>
        <input type="hidden" name="<%= AdminCategories.PARAMETER_MODE__VIEW_CATEGORY %>" value="1">

        <tr>
            <td width="80" height="24" class="imcmsAdmText" nowrap><? install/htdocs/sv/jsp/category_admin/category_type ?> &nbsp;</td>
            <td>
                <select name="<%= AdminCategories.PARAMETER_SELECT__CATEGORY_TYPE_TO_SHOW %>">
                    <%= AdminCategories.createHtmlOptionListOfCategoryTypes(formData.getCategoryTypeToEdit()) %>
                </select> &nbsp; <input type="submit" class="imcmsFormBtn" name="<%= AdminCategories.PARAMETER_BUTTON__SELECT_CATEGORY_TYPE_TO_SHOW_OR_REMOVE %>" value="<? global/select ?>"></td>
        </tr>
        <% if(formData.getCategoryTypeToEdit() != null ){ %>
        <tr>
            <td  class="imcmsAdmText" nowrap><? install/htdocs/sv/jsp/category_admin/category ?> &nbsp;</td>
            <td>
                <select name="categories">
                    <%= AdminCategories.createHtmlOptionListOfCategoriesForOneType(formData.getCategoryTypeToEdit(), formData.getCategoryToEdit()) %>
                </select> &nbsp; <input type="submit" class="imcmsFormBtn" name="select_category_to_edit" value="<? global/select ?>"></td>
        </tr>
        <%}
        if(null != categoryToEdit) {
            boolean readonly = true ;%>
            <%@ include file="category_admin_category.jsp"%>
        <%}%>
    <%}  // ---------- delete category type------------
    else if(request.getParameter(AdminCategories.PARAMETER_MODE__DELETE_CATEGORY_TYPE) != null ) {
        if(formData.getNumberOfCategories() > 0 ) {
            messageToUser.append("<? install/htdocs/sv/jsp/category_admin/message_delete_category_type/1 ?> \"");
            messageToUser.append( formData.getCategoryTypeToEdit().getName() + "\" <? install/htdocs/sv/jsp/category_admin/message_delete_category_type/2 ?> ");
            messageToUser.append(formData.getNumberOfCategories() +"");
            messageToUser.append(" <? install/htdocs/sv/jsp/category_admin/message_delete_category_type/3 ?>. ");
            messageToUser.append("<? install/htdocs/sv/jsp/category_admin/message_delete_category_type/4 ?>!");
        } %>
        <input type="hidden" name="<%= AdminCategories.PARAMETER_MODE__DELETE_CATEGORY_TYPE %>" value="1">
        <tr>
            <td width="80" height="24" class="imcmsAdmText" nowrap><? install/htdocs/sv/jsp/category_admin/category_type ?> &nbsp;</td>
            <td>
                <select name="<%= AdminCategories.PARAMETER_SELECT__CATEGORY_TYPE_TO_SHOW %>">
                    <%= AdminCategories.createHtmlOptionListOfCategoryTypes(formData.getCategoryTypeToEdit()) %>
                </select> &nbsp; <input type="submit" class="imcmsFormBtn" name="<%= AdminCategories.PARAMETER_BUTTON__SELECT_CATEGORY_TYPE_TO_SHOW_OR_REMOVE %>" value="<? global/remove ?>"></td>
        </tr>
    <%if( messageToUser.length() > 0 ) { %>
    <tr><td colspan="2">&nbsp;</td></tr>
    <tr>
        <td colspan="2" height="24" class="imcmsAdmText" ><font face="Verdana, Arial, Helvetica, sans-serif" size="1" color="red"> <%=messageToUser %> </font></td>
    </tr>
    <%}

    } // ------------  delete category -------------------
    else if(request.getParameter(AdminCategories.PARAMETER_MODE__DELETE_CATEGORY) != null ) {
        if(formData.getDocumentsOfOneCategory() != null && formData.getDocumentsOfOneCategory().length > 0 ) {
            messageToUser.append("<? install/htdocs/sv/jsp/category_admin/message_delete_category/1 ?> \"");
            messageToUser.append( formData.getCategoryToEdit().getName() + "\" <? install/htdocs/sv/jsp/category_admin/message_delete_category/2 ?> ");
            messageToUser.append(formData.getDocumentsOfOneCategory().length +"");
            messageToUser.append(" <? global/document ?>. ");
            messageToUser.append("<? install/htdocs/sv/jsp/category_admin/message_delete_category/3 ?>?");
        } %>
        <input type="hidden" name="<%= AdminCategories.PARAMETER_MODE__DELETE_CATEGORY %>" value="1">
        <tr>
            <td width="80" height="24" class="imcmsAdmText" nowrap><? install/htdocs/sv/jsp/category_admin/category_type ?> &nbsp;</td>
            <td>
                <select name="<%= AdminCategories.PARAMETER_SELECT__CATEGORY_TYPE_TO_SHOW %>">
                    <%= AdminCategories.createHtmlOptionListOfCategoryTypes(formData.getCategoryTypeToEdit()) %>
                </select> &nbsp; <input type="submit" class="imcmsFormBtn" name="<%= AdminCategories.PARAMETER_BUTTON__SELECT_CATEGORY_TYPE_TO_SHOW_OR_REMOVE %>" value="<? global/select ?>"></td>
        </tr>
        <% if(formData.getCategoryTypeToEdit() != null ){ %>
        <tr>
            <td width="80" height="24" class="imcmsAdmText" nowrap><? install/htdocs/sv/jsp/category_admin/category ?> &nbsp;</td>
            <td>
                <select name="categories">
                    <%= AdminCategories.createHtmlOptionListOfCategoriesForOneType(formData.getCategoryTypeToEdit(), formData.getCategoryToEdit()) %>
                </select> &nbsp;<input type="submit" class="imcmsFormBtn" name="select_category_to_edit" value="<? global/select ?>"></td>
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
                <%}else if( inEditCategoryTypeMode && null != formData.getCategoryTypeToEdit() ) { %>
                    <input type="submit" class="imcmsFormBtn" name="<%=AdminCategories.PARAMETER_CATEGORY_TYPE_SAVE%>" value="<? global/save ?>" >
                <%}else if( inAddCategoryMode ) { %>
                    <input type="submit" class="imcmsFormBtn" name="category_add" value="<? global/create ?>" >
                <%}else if( inEditCategoryMode && null != formData.getCategoryToEdit() ) { %>
                    <input type="submit" class="imcmsFormBtn" name="category_save" value="<? global/save ?>" >
                <%}else if( inDeleteCategoryMode && null != formData.getCategoryToEdit() ) { %>
                    <input type="submit" class="imcmsFormBtn" name="category_delete" value="<? global/remove ?>" >
                <%}%>
        <%  }  %>

        <input type="submit" class="imcmsFormBtn" name="<%= AdminCategories.PARAMETER_BUTTON__CANCEL %>" value="<? global/back ?>"></td>
</tr>
</form>
</table>
#gui_bottom()
#gui_outer_end()
</body>
</html>
</vel:velocity>
