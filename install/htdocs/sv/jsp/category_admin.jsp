<%@ page import="com.imcode.imcms.servlet.superadmin.AdminCategories,
                 com.imcode.imcms.api.Category"%>


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
    AdminCategories.AdminCategoriesBean adminCategoriesFormBean;
    adminCategoriesFormBean = (AdminCategories.AdminCategoriesBean)request.getAttribute("admincategoriesbean");
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
       <!-- <input type="submit" class="imcmsFormBtn" name="move_category" value="Koppla till typ"></td>-->
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
<% String heading = request.getParameter("heading") != null ? request.getParameter("heading") : "<? install/htdocs/sv/jsp/category_admin/administer_categories ?>";
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

    <% } //------ add category type --------
    else if(request.getParameter("add_category_type") != null || request.getParameter("category_type_add") != null) {   %>

        <input type="hidden" name="adminMode" value="addCategoryTypeMode">
        <input type="hidden" name="heading" value="<? install/htdocs/sv/jsp/category_admin/create_category_type ?>">
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
    <% }  // ---------- edit category type ------------
    else if(request.getParameter("edit_category_type") != null || adminCategoriesFormBean.getAdminMode().equals("editCategoryTypeMode") ) {
        %>
        <input type="hidden" name="adminMode" value="editCategoryTypeMode">
        <input type="hidden" name="heading" value="<? install/htdocs/sv/jsp/category_admin/edit_category_type ?>">
        <tr>
            <td width="80" height="24" class="imcmsAdmText" nowrap><? install/htdocs/sv/jsp/category_admin/category_type ?> &nbsp;</td>
            <td>
                <select name="category_type">
                    <%= adminCategoriesFormBean.getCategoryTypesOptionList() %>
                </select> &nbsp; <input type="submit" class="imcmsFormBtn" name="select_category_type_to_edit" value="<? global/select ?>"></td>
        </tr>

        <% if(adminCategoriesFormBean.getCategoryTypeToEdit() != null ){  %>
            <tr>
                <td height="24" class="imcmsAdmText" nowrap><? install/htdocs/sv/jsp/category_admin/new_name ?> &nbsp;</td>
                <td><script>
                    writeFormField("TEXT","name",30,50,null, "<%=adminCategoriesFormBean.getCategoryTypeToEdit().getName()%>");
                </script></td>
            </tr>

            <tr>
                <td>&nbsp;</td>
                <td width="80" height="24" class="imcmsAdmText" nowrap>
                 <input type="radio" name="max_choices" value="1" <%= adminCategoriesFormBean.getCategoryTypeToEdit().getMaxChoices() == 1 ?  "checked" : "" %> >
                   &nbsp;<? install/htdocs/sv/jsp/category_admin/singel_choice ?></td>
            </tr>
            <tr>
                <td>&nbsp;</td>
                 <td width="80" height="24" class="imcmsAdmText" nowrap>
                  <input type="radio" name="max_choices" value="0" <%= adminCategoriesFormBean.getCategoryTypeToEdit().getMaxChoices() == 0 ? "checked" : "" %> >
                    &nbsp;<? install/htdocs/sv/jsp/category_admin/multi_choice ?></td>
            </tr>
        <%}%>
    <% } // ------ add category -------
    else if(request.getParameter("add_category") != null || adminCategoriesFormBean.getAdminMode().equals("addCategoryMode")) {  %>
        <input type="hidden" name="adminMode" value="addCategoryMode">
        <input type="hidden" name="heading" value="<? install/htdocs/sv/jsp/category_admin/create_category ?>">

        <tr>
		    <td width="80" height="24" class="imcmsAdmText" nowrap><? install/htdocs/sv/jsp/category_admin/category_name ?> &nbsp;</td>
            <td><script>
                writeFormField("TEXT","name",30,50,null, null);
            </script></td>
        </tr>
        <tr><td colspan="2"></td></tr>
        <tr>
		    <td class="imcmsAdmText" nowrap><? install/htdocs/sv/jsp/category_admin/description ?> &nbsp;</td>
            <td><script>
                writeFormField("TEXTAREA","description",30,2,"100%",null);
            </script></textarea></td>
        </tr>
        <tr><td colspan="2"></td></tr>
        <tr>
		    <td class="imcmsAdmText" nowrap><? install/htdocs/sv/jsp/category_admin/icon ?> &nbsp;</td>
            <td><script>
                writeFormField("TEXT","icon",30,255,null, null);
            </script></td>
        </tr>
         <tr><td colspan="2"></td></tr>
        <tr>
		    <td class="imcmsAdmText" nowrap><? install/htdocs/sv/jsp/category_admin/select_category_type ?> &nbsp;</td>
            <td><select name="category_type">
                    <%= adminCategoriesFormBean.getCategoryTypesOptionList() %>
                </select></td>
        </tr>


    <% }  // ---------- edit category ------------
    else if(request.getParameter("edit_category") != null || adminCategoriesFormBean.getAdminMode().equals("editCategoryMode") ) {
    %>
        <input type="hidden" name="adminMode" value="editCategoryMode">
        <input type="hidden" name="heading" value="<? install/htdocs/sv/jsp/category_admin/edit_category ?>">

        <tr>
            <td width="80" height="24" class="imcmsAdmText" nowrap><? install/htdocs/sv/jsp/category_admin/category_type ?> &nbsp;</td>
            <td>
                <select name="category_type">
                    <%= adminCategoriesFormBean.getCategoryTypesOptionList() %>
                </select> &nbsp; <input type="submit" class="imcmsFormBtn" name="select_category_type_to_edit" value="<? global/select ?>"></td>
        </tr>
        <% if(adminCategoriesFormBean.getCategoryTypeToEdit() != null ){ %>
        <tr>
            <td width="80" height="24" class="imcmsAdmText" nowrap><? install/htdocs/sv/jsp/category_admin/category ?> &nbsp;</td>
            <td>
                <select name="categories">
                    <%= adminCategoriesFormBean.getCategoriesOptionList() %>
                </select> &nbsp; <input type="submit" class="imcmsFormBtn" name="select_category_to_edit" value="<? global/select ?>"></td>
        </tr>
        <%}%>


        <% if(adminCategoriesFormBean.getCategoryToEdit() != null ){  %>
        <tr>
            <td width="80" height="24" class="imcmsAdmText" nowrap><? install/htdocs/sv/jsp/category_admin/category_name ?> &nbsp;</td>
            <td><script>
                writeFormField("TEXT","name",30,50,null, "<%= adminCategoriesFormBean.getCategoryToEdit().getName()%>");
            </script></td>
        </tr>
        <tr><td colspan="2"></td></tr>
        <tr>
		    <td class="imcmsAdmText" nowrap><? install/htdocs/sv/jsp/category_admin/description ?> &nbsp;</td>
            <td><script>
                writeFormField("TEXTAREA","description",30,2,"100%",null);
            </script><%= adminCategoriesFormBean.getCategoryToEdit().getDescription()%></textarea></td>
        </tr>
        <tr><td colspan="2"></td></tr>
        <tr>
		    <td class="imcmsAdmText" nowrap><? install/htdocs/sv/jsp/category_admin/icon ?> &nbsp;</td>
            <td><script>
                writeFormField("TEXT","icon",30,255,null, "<%= adminCategoriesFormBean.getCategoryToEdit().getImage()%>");
            </script>
            <%if(!adminCategoriesFormBean.getCategoryToEdit().getImage().equals("")) { %>
                &nbsp;<input type="image" src="<%=adminCategoriesFormBean.getCategoryToEdit().getImage() %>" name="" value="" border="0">
            <%} %></td>
        </tr>
        <tr><td colspan="2"></td></tr>
        <tr>
		    <td class="imcmsAdmText" nowrap><? install/htdocs/sv/jsp/category_admin/add_to_category_type ?> &nbsp;</td>
            <td><select name="category_type">
                    <%= adminCategoriesFormBean.getCategoryTypesOptionList() %>
                </select></td>
        </tr>
        <%}%>

    <% }  // ---------- delete category type------------
    else if(request.getParameter("delete_category_type") != null || adminCategoriesFormBean.getAdminMode().equals("deleteCategoryTypeMode") ) {
        if(adminCategoriesFormBean.getNumberOfCategories() > 0 ) {
            messageToUser.append("<? install/htdocs/sv/jsp/category_admin/the_category_type ?> \"");
            messageToUser.append( adminCategoriesFormBean.getCategoryTypeToEdit().getName() + "\" <? install/htdocs/sv/jsp/category_admin/contains ?> ");
            messageToUser.append(adminCategoriesFormBean.getNumberOfCategories() +"");
            messageToUser.append(" <? install/htdocs/sv/jsp/category_admin/categories ?>. ");
            messageToUser.append("<? install/htdocs/sv/jsp/category_admin/remove_category_type_not_allowed ?>!");
        }
    %>
        <input type="hidden" name="adminMode" value="deleteCategoryTypeMode">
        <input type="hidden" name="heading" value="<? install/htdocs/sv/jsp/category_admin/remove_category_type ?>">
        <tr>
            <td width="80" height="24" class="imcmsAdmText" nowrap><? install/htdocs/sv/jsp/category_admin/category_type ?> &nbsp;</td>
            <td>
                <select name="category_type">
                    <%= adminCategoriesFormBean.getCategoryTypesOptionList() %>
                </select> &nbsp; <input type="submit" class="imcmsFormBtn" name="select_category_type_to_edit" value="<? global/remove ?>"></td>
        </tr>
        <tr><td colspan="2">&nbsp;</td></tr>
        <tr>
            <td colspan="2" height="24" class="imcmsAdmText" ><font face="Verdana, Arial, Helvetica, sans-serif" size="1" color="red"> <%=messageToUser %> </font></td>
        </tr>

    <% } // ------------  delete category -------------------
    else if(request.getParameter("delete_category") != null || adminCategoriesFormBean.getAdminMode().equals("deleteCategoryMode") ) {
        if(adminCategoriesFormBean.getDocumentsOfOneCategory() != null && adminCategoriesFormBean.getDocumentsOfOneCategory().length > 0 ) {
            messageToUser.append("<? install/htdocs/sv/jsp/category_admin/the_category ?> \"");
            messageToUser.append( adminCategoriesFormBean.getCategoryToEdit().getName() + "\" <? install/htdocs/sv/jsp/category_admin/is_used_by ?> ");
            messageToUser.append(adminCategoriesFormBean.getDocumentsOfOneCategory().length +"");
            messageToUser.append(" <? global/document ?>. ");
            messageToUser.append("<? install/htdocs/sv/jsp/category_admin/do_you_really_want_to ?>?");
        }
    %>
        <input type="hidden" name="adminMode" value="deleteCategoryMode">
        <input type="hidden" name="heading" value="<? install/htdocs/sv/jsp/category_admin/remove_category ?>">
        <tr>
            <td width="80" height="24" class="imcmsAdmText" nowrap><? install/htdocs/sv/jsp/category_admin/category_type ?> &nbsp;</td>
            <td>
                <select name="category_type">
                    <%= adminCategoriesFormBean.getCategoryTypesOptionList() %>
                </select>&nbsp;<input type="submit" class="imcmsFormBtn" name="select_category_type_to_edit" value="<? global/select ?>"></td>
        </tr>
        <% if(adminCategoriesFormBean.getCategoryTypeToEdit() != null ){ %>
        <tr>
            <td width="80" height="24" class="imcmsAdmText" nowrap><? install/htdocs/sv/jsp/category_admin/category ?> &nbsp;</td>
            <td>
                <select name="categories">
                    <%= adminCategoriesFormBean.getCategoriesOptionList() %>
                </select> &nbsp;<input type="submit" class="imcmsFormBtn" name="select_category_to_edit" value="<? global/select ?>"></td>
        </tr>
        <tr><td colspan="2">&nbsp;</td></tr>
        <% }
        if(adminCategoriesFormBean.getCategoryToEdit() != null) { %>
        <tr>
            <td width="80" height="24" class="imcmsAdmText" nowrap><? install/htdocs/sv/jsp/category_admin/category_name ?> &nbsp;</td>
            <td><script>
                writeFormField("TEXT","name",30,50,null, "<%= adminCategoriesFormBean.getCategoryToEdit().getName()%>");
            </script></td>
        </tr>
        <tr><td colspan="2"></td></tr>
        <tr>
		    <td class="imcmsAdmText" nowrap><? install/htdocs/sv/jsp/category_admin/description ?> &nbsp;</td>
            <td><script>
                writeFormField("TEXTAREA","description",30,2,"100%",null);
            </script><%= adminCategoriesFormBean.getCategoryToEdit().getDescription()%></textarea></td>
        </tr>
        <tr><td colspan="2"></td></tr>
        <tr>
		    <td class="imcmsAdmText" nowrap><? install/htdocs/sv/jsp/category_admin/icon ?> &nbsp;</td>
            <td><script>
                writeFormField("TEXT","icon",30,255,null, "<%= adminCategoriesFormBean.getCategoryToEdit().getImage()%>");
            </script>
            <%if(!adminCategoriesFormBean.getCategoryToEdit().getImage().equals("")) { %>
                &nbsp;<input type="image" src="<%=adminCategoriesFormBean.getCategoryToEdit().getImage() %>" name="" value="" border="0">
            <%} %></td>
        </tr>
        <tr><td colspan="2"></td></tr>
        <tr>
		    <td class="imcmsAdmText" nowrap><? install/htdocs/sv/jsp/category_admin/add_to_category_type ?> &nbsp;</td>
            <td><select name="category_type">
                    <%= adminCategoriesFormBean.getCategoryTypesOptionList() %>
                </select></td>
        </tr>
        <tr><td colspan="2">&nbsp;</td></tr>
        <tr>
            <td colspan="2" height="24" class="imcmsAdmText" ><font face="Verdana, Arial, Helvetica, sans-serif" size="1" color="red"> <%=messageToUser %> </font></td>
        </tr>
        <%}%>

    <% }
    else { // default %>
        <input type="hidden" name="adminMode" value="">
        <tr>
            <td width="80" height="24" class="imcmsAdmText" nowrap>
                <? install/htdocs/sv/jsp/category_admin/select_function ?></td>
            <td>&nbsp;</td>
        </tr>
    <%}%>
    
    </table></td>
</tr>

<tr>
    <td colspan="2"><script>hr("100%",656,"blue");</script></td>
</tr>
<tr>
    <td colspan="2" align="right">
        
        <% if(request.getParameter("add_category_type") != null) { %>
            <input type="submit" class="imcmsFormBtn" name="category_type_add" value="<? global/create ?>">
        <%}else if( request.getParameter("select_category_type_to_edit") != null && adminCategoriesFormBean.getAdminMode().equals("editCategoryTypeMode") ) { %>
            <input type="submit" class="imcmsFormBtn" name="category_type_save" value="<? global/edit ?>" >
        <%}else if(request.getParameter("add_category") != null || request.getParameter("category_add") != null ) { %>
            <input type="submit" class="imcmsFormBtn" name="category_add" value="<? global/create ?>" >
        <%}else if( request.getParameter("select_category_to_edit") != null && adminCategoriesFormBean.getAdminMode().equals("editCategoryMode")) { %>
            <input type="submit" class="imcmsFormBtn" name="category_save" value="<? global/edit ?>" >

        <%}else if( request.getParameter("select_category_to_edit") != null && adminCategoriesFormBean.getAdminMode().equals("deleteCategoryMode") ) { %>
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
