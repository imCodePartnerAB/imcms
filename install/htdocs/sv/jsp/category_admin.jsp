<%@ page import="com.imcode.imcms.servlet.superadmin.AdminCategories,
                 com.imcode.imcms.api.Category,
                 imcode.server.document.CategoryDomainObject"%>


<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">

<title>Administer categories / category_typesr</title>
<link rel="STYLESHEET" href="@imcmscssurl@/imcms_admin_ns.css" type="text/css">
<script src="@imcmsscripturl@/imcms_admin.js" type="text/javascript"></script>

</head>
<body bgcolor="#FFFFFF" onLoad="focusField(1,'template')">

<script>
imcmsGui("outer_start", null);
imcmsGui("head", null);
</script>
<%
    AdminCategories.FormData adminCategoriesFormData = (AdminCategories.FormData)request.getAttribute(AdminCategories.ATTRIBUTE__FORM_DATA);
    StringBuffer messageToUser = new StringBuffer("");

%>
<table border="0" cellspacing="0" cellpadding="2" width="660" align="center">
<form name="head" action="AdminCategories" method="post">
    <tr>
        <td colspan="2" class="imcmsAdmText"><b class="white"><? install/htdocs/sv/jsp/category_admin/category_type ?>:</b></td>
    </tr>
    <tr>
    <td colspan="2">
        <input type="submit" class="imcmsFormBtn" name="add_category_type" value="<? global/create ?>">
        <input type="submit" class="imcmsFormBtn" name="edit_category_type" value="<? global/edit ?>" >
        <input type="submit" class="imcmsFormBtn" name="delete_category_type" value="<? global/remove ?>"></td>
    </tr>
    <tr>
    <td colspan="2" class="imcmsAdmText"><b class="white"><? install/htdocs/sv/jsp/category_admin/category ?>:</b></td>
    </tr>
    <tr>
    <td colspan="2">
        <input type="submit" class="imcmsFormBtn" name="add_category" value="<? global/create ?>">
        <input type="submit" class="imcmsFormBtn" name="edit_category" value="<? global/edit ?>">
        <input type="submit" class="imcmsFormBtn" name="delete_category" value="<? global/remove ?>">
        <input type="submit" class="imcmsFormBtn" name="view_category" value="<? global/view ?>">
    <td>
        <input type="button" value="<? global/help ?>" title="Open help" class="imcmsFormBtn" onClick="openHelpW(124)"></td>
    </tr>
</form>
</table>
<script>
imcmsGui("mid", null);
</script>
<table border="0" cellspacing="0" cellpadding="2" width="660" align="center">
<form name="main" action="AdminCategories" method="post">
<%
    String defaultHeading = "<? install/htdocs/sv/jsp/category_admin/administer_categories ?>" ;
    String createCategoryTypeHeading = "<? install/htdocs/sv/jsp/category_admin/create_category_type ?>" ;
    String editCategoryTypeHeading = "<? install/htdocs/sv/jsp/category_admin/edit_category_type ?>";
    String removeCategoryTypeHeading = "<? install/htdocs/sv/jsp/category_admin/remove_category_type ?>";
    String createCategoryHeading = "<? install/htdocs/sv/jsp/category_admin/create_category ?>";
    String editCategoryHeading = "<? install/htdocs/sv/jsp/category_admin/edit_category ?>";
    String removeCategoryHeading = "<? install/htdocs/sv/jsp/category_admin/remove_category ?>";
    String viewCategoriesHeading = "<? install/htdocs/sv/jsp/category_admin/view_categories ?>";
    String heading = request.getParameter("heading") != null ? request.getParameter("heading") : defaultHeading;

    if(request.getParameter("add_category_type") != null ){
        heading = createCategoryTypeHeading;
    }else if(request.getParameter("edit_category_type")!=null){
        heading = editCategoryTypeHeading;
    }else if(request.getParameter("delete_category_type")!=null){
        heading = removeCategoryTypeHeading;
    }else if(request.getParameter("add_category")!=null){
        heading = createCategoryHeading;
    }else if(request.getParameter("edit_category")!=null){
        heading = editCategoryHeading;
    }else if(request.getParameter("delete_category")!=null){
        heading = removeCategoryHeading;
    }else if(request.getParameter("view_category")!=null){
        heading = viewCategoriesHeading;
    }else if(request.getParameter("cancel")!=null){
        heading = defaultHeading;
    }
 %>
<tr>
    <td colspan="2"><script>imcHeading("<%=heading%>",656);</script></td>
</tr>
<tr>
    <td colspan="2">
    <table border="0" cellpadding="0" cellspacing="0">

    <% // ---- cancel --------------------
    if(request.getParameter("cancel") != null ) { %>
        <input type="hidden" name="adminMode" value="">
        <tr>
            <td width="80" height="24" class="imcmsAdmText" nowrap>
                <? install/htdocs/sv/jsp/category_admin/select_function ?></td>
            <td>&nbsp;</td>
        </tr>
    </table></td>
    </tr>

    <% } //------ add category type --------
    else if(request.getParameter("add_category_type") != null || request.getParameter("category_type_add") != null) {   %>

        <input type="hidden" name="adminMode" value="addCategoryTypeMode">
        <input type="hidden" name="heading" value="<%=createCategoryTypeHeading%> ">
        <tr>
            <td>&nbsp;</td>
            <td width="80" height="24" class="imcmsAdmText" nowrap><? global/Name ?></td>
            <td>&nbsp;</td>
        </tr>
        <tr>
            <td>&nbsp;</td>
            <td><script>
                writeFormField("TEXT","name",30,50,null,null);
            </script></td>
            <td>&nbsp;</td>
        </tr>
        <tr>
            <td>&nbsp;</td>
            <td width="80" height="24" class="imcmsAdmText" nowrap>
             <input type="radio" name="max_choices" value="1" checked>&nbsp;<? install/htdocs/sv/jsp/category_admin/singel_choice ?></td>
        </tr>
        <tr>
            <td>&nbsp;</td>
             <td width="80" height="24" class="imcmsAdmText" nowrap>
              <input type="radio" name="max_choices" value="0" >&nbsp;<? install/htdocs/sv/jsp/category_admin/multi_choice ?></td>
        </tr>
    </table></td>
    </tr>

    <% }  // ---------- edit category type ------------
    else if(request.getParameter("edit_category_type") != null || adminCategoriesFormData.getAdminMode().equals("editCategoryTypeMode") ) { %>

        <input type="hidden" name="adminMode" value="editCategoryTypeMode">
        <input type="hidden" name="heading" value="<%=editCategoryTypeHeading%>">
        <tr>
            <td width="80" height="24" class="imcmsAdmText" nowrap><? install/htdocs/sv/jsp/category_admin/category_type ?> &nbsp;</td>
            <td>
                <select name="<%= AdminCategories.PARAMETER_SELECT__SELECT_CATEGORY_TYPE%>">
                    <%= adminCategoriesFormData.getCategoryTypesOptionList() %>
                </select> &nbsp; <input type="submit" class="imcmsFormBtn" name="<%= AdminCategories.PARAMETER_BUTTON__SELECT_CATEGORY_TYPE %>" value="<? global/select ?>"></td>
        </tr>

        <% if(adminCategoriesFormData.getCategoryTypeToEdit() != null ){  %>
            <tr>
                <td height="24" class="imcmsAdmText" nowrap><? install/htdocs/sv/jsp/category_admin/new_name ?> &nbsp;</td>
                <td><script>
                    writeFormField("TEXT","name",30,50,null, "<%=adminCategoriesFormData.getCategoryTypeToEdit().getName()%>");
                </script></td>
            </tr>

            <tr>
                <td>&nbsp;</td>
                <td width="80" height="24" class="imcmsAdmText" nowrap>
                 <input type="radio" name="max_choices" value="1" <%= adminCategoriesFormData.getCategoryTypeToEdit().getMaxChoices() == 1 ?  "checked" : "" %> >
                   &nbsp;<? install/htdocs/sv/jsp/category_admin/singel_choice ?></td>
            </tr>
            <tr>
                <td>&nbsp;</td>
                 <td width="80" height="24" class="imcmsAdmText" nowrap>
                  <input type="radio" name="max_choices" value="0" <%= adminCategoriesFormData.getCategoryTypeToEdit().getMaxChoices() == 0 ? "checked" : "" %> >
                    &nbsp;<? install/htdocs/sv/jsp/category_admin/multi_choice ?></td>
            </tr>
        <%}%>
    </table></td>
    </tr>

    <%} // ------ add category -------
    else if(request.getParameter("add_category") != null || adminCategoriesFormData.getAdminMode().equals("addCategoryMode")) {

        if( request.getParameter("category_add") != null && !adminCategoriesFormData.getUniqueName() ) {
            messageToUser.append("Det finns redan en kategori med namn \"");
            messageToUser.append(request.getParameter("name") + "\" ");
            messageToUser.append("i kategoritypen \"" + adminCategoriesFormData.getCategoryTypeToEdit().getName() + "\". ");
            messageToUser.append("Var vänlig välj ett nytt namn!");
        }
        CategoryDomainObject categoryToEdit = adminCategoriesFormData.getCategoryToEdit();
    %>


        <input type="hidden" name="adminMode" value="addCategoryMode">
        <input type="hidden" name="heading" value="<%=createCategoryHeading%>">

        <tr>
		    <td width="80" height="24" class="imcmsAdmText" nowrap><? install/htdocs/sv/jsp/category_admin/category_name ?> &nbsp;</td>
            <td><script>
                writeFormField("TEXT","name",30,50,null, "<%= request.getParameter("name") != null && !adminCategoriesFormData.getUniqueName() ? request.getParameter("name") : "" %>" );
            </script></td>
        </tr>
        <tr><td colspan="2">&nbsp;</td></tr>
        <tr>
		    <td class="imcmsAdmText" nowrap><? install/htdocs/sv/jsp/category_admin/description ?> &nbsp;</td>
            <td><script>
                writeFormField("TEXTAREA","description",30,2,"100%",null);
            </script><%= request.getParameter("description") != null && !adminCategoriesFormData.getUniqueName() ? request.getParameter("description") : "" %></textarea></td>
        </tr>
        <tr><td colspan="2">&nbsp;</td></tr>
        <tr>
		    <td class="imcmsAdmText" nowrap><? install/htdocs/sv/jsp/category_admin/icon ?> &nbsp;</td>
            <td>
            <input type="text" name="icon" size="30" maxlength="255" value="<%= null != categoryToEdit ? categoryToEdit.getImage() : "" %>">
            &nbsp;
            <input type="submit" class="imcmsFormBtnSmall" name="browseForMenuImage" value=" Browse ">
            <input type="hidden" name="imageBrowse.originalAction" value="editDocumentInformation"/>
			</td>
        </tr>
        <tr><td colspan="2">&nbsp;</td></tr>
        <tr>
		    <td class="imcmsAdmText" nowrap><? install/htdocs/sv/jsp/category_admin/add_to_category_type ?> &nbsp;</td>
            <td><select name="<%= AdminCategories.PARAMETER__ADD_TO_CATEGORY_TYPE %>">
                    <%= adminCategoriesFormData.getCategoryTypesOptionList() %>
                </select></td>
        </tr>
    </table></td>
    </tr>
    <%if( messageToUser.length() > 0 ) { %>
    <tr><td colspan="2">&nbsp;</td></tr>
    <tr>
        <td colspan="2" height="24" class="imcmsAdmText" ><font face="Verdana, Arial, Helvetica, sans-serif" size="1" color="red"> <%=messageToUser %> </font></td>
    </tr>
    <%}


    }  // ---------- edit category ------------
    else if(request.getParameter("edit_category") != null || adminCategoriesFormData.getAdminMode().equals("editCategoryMode") ) {

        if( request.getParameter("category_save") != null && !adminCategoriesFormData.getUniqueName() ) {
            messageToUser.append("Det finns redan en kategori med namn \"");
            messageToUser.append(request.getParameter("name") + "\" ");
            messageToUser.append("i kategoritypen \"" + adminCategoriesFormData.getCategoryTypeToEdit().getName() + "\". ");
            messageToUser.append("Var vänlig välj ett nytt namn!");
        }
        %>
        <input type="hidden" name="adminMode" value="editCategoryMode">
        <input type="hidden" name="heading" value="<%=editCategoryHeading%>">
        <input type="hidden" name="adminMode" value="editCategoryMode">
        <input type="hidden" name="oldName" value="<%=adminCategoriesFormData.getCategoryToEdit() != null ? adminCategoriesFormData.getCategoryToEdit().getName() : "" %>">

        <tr>
            <td width="80" height="24" class="imcmsAdmText" nowrap><? install/htdocs/sv/jsp/category_admin/category_type ?> &nbsp;</td>
            <td>
                <select name="<%= AdminCategories.PARAMETER_SELECT__SELECT_CATEGORY_TYPE %>">
                    <%= adminCategoriesFormData.getCategoryTypesOptionList() %>
                </select> &nbsp; <input type="submit" class="imcmsFormBtn" name="<%= AdminCategories.PARAMETER_BUTTON__SELECT_CATEGORY_TYPE %>" value="<? global/select ?>"></td>
        </tr>
        <% if (null != adminCategoriesFormData.getCategoryTypeToEdit()) { %>
        <tr>
            <td width="80" height="24" class="imcmsAdmText" nowrap><? install/htdocs/sv/jsp/category_admin/category ?> &nbsp;</td>
            <td>
                <select name="categories">
                    <%= adminCategoriesFormData.getCategoriesOptionList() %>
                </select> &nbsp; <input type="submit" class="imcmsFormBtn" name="select_category_to_edit" value="<? global/select ?>"></td>
        </tr>
        <%}%>


        <% if(adminCategoriesFormData.getCategoryToEdit() != null ){  %>
        <tr>
            <td width="80" height="24" class="imcmsAdmText" nowrap><? install/htdocs/sv/jsp/category_admin/category_name ?> &nbsp;</td>
            <td><script>
                writeFormField("TEXT","name",30,50,null, "<%= adminCategoriesFormData.getCategoryToEdit().getName()%>");
            </script></td>
        </tr>
        <tr><td colspan="2"></td></tr>
        <tr>
		    <td class="imcmsAdmText" nowrap><? install/htdocs/sv/jsp/category_admin/description ?> &nbsp;</td>
            <td><script>
                writeFormField("TEXTAREA","description",30,2,"100%",null);
            </script><%= adminCategoriesFormData.getCategoryToEdit().getDescription()%></textarea></td>
        </tr>
        <tr><td colspan="2"></td></tr>
        <tr>
		    <td class="imcmsAdmText" nowrap><? install/htdocs/sv/jsp/category_admin/icon ?> &nbsp;</td>
            <td><script>
                writeFormField("TEXT","icon",30,255,null, "<%= adminCategoriesFormData.getCategoryToEdit().getImage()%>");
            </script>
            <%if(!adminCategoriesFormData.getCategoryToEdit().getImage().equals("")) { %>
                &nbsp;<input type="image" src="<%=adminCategoriesFormData.getCategoryToEdit().getImage() %>" name="" value="" border="0">
            <%} %></td>
        </tr>
        <tr><td colspan="2"></td></tr>
        <tr>
		    <td class="imcmsAdmText" nowrap><? install/htdocs/sv/jsp/category_admin/add_to_category_type ?> &nbsp;</td>
            <td><select name="<%= AdminCategories.PARAMETER__ADD_TO_CATEGORY_TYPE %>">
                    <%= adminCategoriesFormData.getCategoryTypesOptionList() %>
                </select></td>
        </tr>
       <%}%>
    </table></td>
    </tr>
    <%if( messageToUser.length() > 0 ) { %>
     <tr><td>&nbsp;</td></tr>
     <tr>
        <td colspan="2" height="24" class="imcmsAdmText" ><font face="Verdana, Arial, Helvetica, sans-serif" size="1" color="red"> <%=messageToUser %> </font></td>
     </tr>
     <%}
    }  // ---------- View category ------------
    else if(request.getParameter("view_category") != null || adminCategoriesFormData.getAdminMode().equals("showCategoryMode") ) { %>
        <input type="hidden" name="adminMode" value="showCategoryMode">
        <input type="hidden" name="heading" value="<%=viewCategoriesHeading%>">

        <tr>
            <td width="80" height="24" class="imcmsAdmText" nowrap><? install/htdocs/sv/jsp/category_admin/category_type ?> &nbsp;</td>
            <td>
                <select name="<%= AdminCategories.PARAMETER_SELECT__SELECT_CATEGORY_TYPE %>">
                    <%= adminCategoriesFormData.getCategoryTypesOptionList() %>
                </select> &nbsp; <input type="submit" class="imcmsFormBtn" name="<%= AdminCategories.PARAMETER_BUTTON__SELECT_CATEGORY_TYPE %>" value="<? global/select ?>"></td>
        </tr>
        <% if(adminCategoriesFormData.getCategoryTypeToEdit() != null ){ %>
        <tr>
            <td  class="imcmsAdmText" nowrap><? install/htdocs/sv/jsp/category_admin/category ?> &nbsp;</td>
            <td>
                <select name="categories">
                    <%= adminCategoriesFormData.getCategoriesOptionList() %>
                </select> &nbsp; <input type="submit" class="imcmsFormBtn" name="select_category_to_edit" value="<? global/select ?>"></td>
        </tr>
        <%}
        if(adminCategoriesFormData.getCategoryToEdit() != null ){  %>
        <tr>
            <td class="imcmsAdmText" nowrap><? install/htdocs/sv/jsp/category_admin/category_name ?> &nbsp;</td>
            <td><script>
                writeFormField("TEXT","name",30,50,null, "<%= adminCategoriesFormData.getCategoryToEdit().getName()%>");
            </script>&nbsp;ID: <%=adminCategoriesFormData.getCategoryToEdit().getId() %> </td>
        </tr>
        <tr><td colspan="2"></td></tr>
        <tr>
		    <td class="imcmsAdmText" nowrap><? install/htdocs/sv/jsp/category_admin/description ?> &nbsp;</td>
            <td><script>
                writeFormField("TEXTAREA","description",30,2,"100%",null);
            </script><%= adminCategoriesFormData.getCategoryToEdit().getDescription()%></textarea></td>
        </tr>
        <tr><td colspan="2"></td></tr>
        <tr>
		    <td class="imcmsAdmText" nowrap><? install/htdocs/sv/jsp/category_admin/icon ?> &nbsp;</td>
            <td><script>
                writeFormField("TEXT","icon",30,255,null, "<%= adminCategoriesFormData.getCategoryToEdit().getImage()%>");
            </script>
            <%if(!adminCategoriesFormData.getCategoryToEdit().getImage().equals("")) { %>
                &nbsp;<input type="image" src="<%=adminCategoriesFormData.getCategoryToEdit().getImage() %>" name="" value="" border="0">
            <%}%></td>
        </tr>
        <%}%>
    </table></td>
    </tr>

    <%}  // ---------- delete category type------------
    else if(request.getParameter("delete_category_type") != null || adminCategoriesFormData.getAdminMode().equals("deleteCategoryTypeMode") ) {
        if(adminCategoriesFormData.getNumberOfCategories() > 0 ) {
            messageToUser.append("<? install/htdocs/sv/jsp/category_admin/the_category_type ?> \"");
            messageToUser.append( adminCategoriesFormData.getCategoryTypeToEdit().getName() + "\" <? install/htdocs/sv/jsp/category_admin/contains ?> ");
            messageToUser.append(adminCategoriesFormData.getNumberOfCategories() +"");
            messageToUser.append(" <? install/htdocs/sv/jsp/category_admin/categories ?>. ");
            messageToUser.append("<? install/htdocs/sv/jsp/category_admin/remove_category_type_not_allowed ?>!");
        } %>
        <input type="hidden" name="adminMode" value="deleteCategoryTypeMode">
        <input type="hidden" name="heading" value="<%=removeCategoryTypeHeading%>">
        <tr>
            <td width="80" height="24" class="imcmsAdmText" nowrap><? install/htdocs/sv/jsp/category_admin/category_type ?> &nbsp;</td>
            <td>
                <select name="<%= AdminCategories.PARAMETER_SELECT__SELECT_CATEGORY_TYPE %>">
                    <%= adminCategoriesFormData.getCategoryTypesOptionList() %>
                </select> &nbsp; <input type="submit" class="imcmsFormBtn" name="<%= AdminCategories.PARAMETER_BUTTON__SELECT_CATEGORY_TYPE %>" value="<? global/remove ?>"></td>
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
    else if(request.getParameter("delete_category") != null || adminCategoriesFormData.getAdminMode().equals("deleteCategoryMode") ) {
        if(adminCategoriesFormData.getDocumentsOfOneCategory() != null && adminCategoriesFormData.getDocumentsOfOneCategory().length > 0 ) {
            messageToUser.append("<? install/htdocs/sv/jsp/category_admin/the_category ?> \"");
            messageToUser.append( adminCategoriesFormData.getCategoryToEdit().getName() + "\" <? install/htdocs/sv/jsp/category_admin/is_used_by ?> ");
            messageToUser.append(adminCategoriesFormData.getDocumentsOfOneCategory().length +"");
            messageToUser.append(" <? global/document ?>. ");
            messageToUser.append("<? install/htdocs/sv/jsp/category_admin/do_you_really_want_to ?>?");
        } %>
        <input type="hidden" name="adminMode" value="deleteCategoryMode">
        <input type="hidden" name="heading" value="<%=removeCategoryHeading%>">
        <tr>
            <td width="80" height="24" class="imcmsAdmText" nowrap><? install/htdocs/sv/jsp/category_admin/category_type ?> &nbsp;</td>
            <td>
                <select name="<%= AdminCategories.PARAMETER_SELECT__SELECT_CATEGORY_TYPE %>">
                    <%= adminCategoriesFormData.getCategoryTypesOptionList() %>
                </select> &nbsp; <input type="submit" class="imcmsFormBtn" name="<%= AdminCategories.PARAMETER_BUTTON__SELECT_CATEGORY_TYPE %>" value="<? global/select ?>"></td>
        </tr>
        <% if(adminCategoriesFormData.getCategoryTypeToEdit() != null ){ %>
        <tr>
            <td width="80" height="24" class="imcmsAdmText" nowrap><? install/htdocs/sv/jsp/category_admin/category ?> &nbsp;</td>
            <td>
                <select name="categories">
                    <%= adminCategoriesFormData.getCategoriesOptionList() %>
                </select> &nbsp;<input type="submit" class="imcmsFormBtn" name="select_category_to_edit" value="<? global/select ?>"></td>
        </tr>
        <tr><td colspan="2">&nbsp;</td></tr>
        <% }
        if(adminCategoriesFormData.getCategoryToEdit() != null) { %>
        <tr>
            <td width="80" height="24" class="imcmsAdmText" nowrap><? install/htdocs/sv/jsp/category_admin/category_name ?> &nbsp;</td>
            <td><script>
                writeFormField("TEXT","name",30,50,null, "<%= adminCategoriesFormData.getCategoryToEdit().getName()%>");
            </script></td>
        </tr>
        <tr><td colspan="2"></td></tr>
        <tr>
		    <td class="imcmsAdmText" nowrap><? install/htdocs/sv/jsp/category_admin/description ?> &nbsp;</td>
            <td><script>
                writeFormField("TEXTAREA","description",30,2,"100%",null);
            </script><%= adminCategoriesFormData.getCategoryToEdit().getDescription()%></textarea></td>
        </tr>
        <tr><td colspan="2"></td></tr>
        <tr>
		    <td class="imcmsAdmText" nowrap><? install/htdocs/sv/jsp/category_admin/icon ?> &nbsp;</td>
            <td><script>
                writeFormField("TEXT","icon",30,255,null, "<%= adminCategoriesFormData.getCategoryToEdit().getImage()%>");
            </script>
            <%if(!adminCategoriesFormData.getCategoryToEdit().getImage().equals("")) { %>
                &nbsp;<input type="image" src="<%=adminCategoriesFormData.getCategoryToEdit().getImage() %>" name="" value="" border="0">
            <%} %></td>
        </tr>
        <%}%>
        </table></td>
        </tr>
         <%if( messageToUser.length() > 0 ) { %>
        <tr><td colspan="2">&nbsp;</td></tr>
        <tr>
            <td colspan="2" height="24" class="imcmsAdmText" ><font face="Verdana, Arial, Helvetica, sans-serif" size="1" color="red"> <%=messageToUser %> </font></td>
        </tr>
        <%}
    }
    else { // default %>
        <input type="hidden" name="adminMode" value="">
        <tr>
            <td width="80" height="24" class="imcmsAdmText" nowrap>
                <? install/htdocs/sv/jsp/category_admin/select_function ?></td>
            <td>&nbsp;</td>
        </tr>
    </table></td>
    </tr>
    <%}%>

<tr>
    <td colspan="2"><script>hr("100%",656,"blue");</script></td>
</tr>
<tr>
    <td colspan="2" align="right">
        
        <% if(request.getParameter("add_category_type") != null) { %>
            <input type="submit" class="imcmsFormBtn" name="category_type_add" value="<? global/create ?>">

        <%}else if( request.getParameter(AdminCategories.PARAMETER_BUTTON__SELECT_CATEGORY_TYPE) != null && adminCategoriesFormData.getAdminMode().equals("editCategoryTypeMode") ) { %>
            <input type="submit" class="imcmsFormBtn" name="category_type_save" value="<? global/edit ?>" >

        <%}else if(request.getParameter("add_category") != null || request.getParameter("category_add") != null ) { %>
            <input type="submit" class="imcmsFormBtn" name="category_add" value="<? global/create ?>" >

        <%}else if( adminCategoriesFormData.getAdminMode().equals("editCategoryMode") && request.getParameter("select_category_to_edit") != null || request.getParameter("category_save") != null ) { %>
            <input type="submit" class="imcmsFormBtn" name="category_save" value="<? global/edit ?>" >

        <%}else if( adminCategoriesFormData.getAdminMode().equals("deleteCategoryMode") && request.getParameter("select_category_to_edit") != null  ) { %>
            <input type="submit" class="imcmsFormBtn" name="category_delete" value="<? global/remove ?>" >
        <%}%>

        <input type="submit" class="imcmsFormBtn" name="cancel" value="<? global/back ?>"></td>
</tr>
</form>
</table>
<script>
imcmsGui("bottom", null);
imcmsGui("outer_end", null);
</script>

</body>
</html>
