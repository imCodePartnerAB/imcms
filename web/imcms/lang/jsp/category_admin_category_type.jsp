<%@ page    import="com.imcode.imcms.servlet.superadmin.AdminCategories,
                 imcode.server.document.CategoryTypeDomainObject"
            contentType="text/html; charset=UTF-8"%>
<%
    CategoryTypeDomainObject categoryTypeToEdit = (CategoryTypeDomainObject)request.getAttribute( "categoryType" ) ;
            %><tr>
                <td width="110" height="24"><? global/Name ?></td>
                <td><input type="text" name="name" size="50" maxlength="128" value="<%=categoryTypeToEdit.getName()%>"></td>
            </tr>
            <tr>
                <td>&nbsp;</td>
                <td>
							<table border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td height="24"><input type="radio" id="<%=AdminCategories.PARAMETER__MAX_CHOICES%>1" name="<%=AdminCategories.PARAMETER__MAX_CHOICES%>" value="1" <%= categoryTypeToEdit.getMaxChoices() == 1 ?  "checked" : "" %> ></td>
								<td>&nbsp;&nbsp;</td>
								<td><label for="<%=AdminCategories.PARAMETER__MAX_CHOICES%>1"><? install/htdocs/sv/jsp/category_admin/singel_choice ?></label></td>
							</tr>
							</table></td>
            </tr>
            <tr>
                <td>&nbsp;</td>
                 <td>
							<table border="0" cellspacing="0" cellpadding="0" style="margin-bottom:5px;">
							<tr>
								<td height="24"><input type="radio" id="<%=AdminCategories.PARAMETER__MAX_CHOICES%>0" name="<%=AdminCategories.PARAMETER__MAX_CHOICES%>" value="0" <%= categoryTypeToEdit.getMaxChoices() == 0 ? "checked" : "" %> ></td>
								<td>&nbsp;&nbsp;</td>
								<td><label for="<%=AdminCategories.PARAMETER__MAX_CHOICES%>0"><? install/htdocs/sv/jsp/category_admin/multi_choice ?></label></td>
							</tr>
							</table></td>
            </tr>
            <tr>
                <td><label for="<%=AdminCategories.PARAMETER__INHERITED%>"><? install/htdocs/sv/jsp/category_admin/inherited ?></label></td>
                <td><input type="checkbox" id="<%=AdminCategories.PARAMETER__INHERITED%>" name="<%=AdminCategories.PARAMETER__INHERITED%>" value="1" <%= categoryTypeToEdit.isInherited() ? "checked" : "" %> ></td>
            </tr>
            <tr>
                <td><label for="<%=AdminCategories.PARAMETER__IMAGE_ARCHIVE%>"><? install/htdocs/sv/jsp/category_admin/image_archive ?></label></td>
                <td><input type="checkbox" id="<%=AdminCategories.PARAMETER__IMAGE_ARCHIVE%>" name="<%=AdminCategories.PARAMETER__IMAGE_ARCHIVE%>" value="1" <%= categoryTypeToEdit.isImageArchive() ? "checked" : "" %> ></td>
            </tr>
