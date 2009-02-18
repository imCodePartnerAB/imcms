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
								<td height="24"><input type="radio" name="<%=AdminCategories.PARAMETER__MAX_CHOICES%>" value="1" <%= categoryTypeToEdit.getMaxChoices() == 1 ?  "checked" : "" %> ></td>
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
								<td height="24"><input type="radio" name="<%=AdminCategories.PARAMETER__MAX_CHOICES%>" value="0" <%= categoryTypeToEdit.getMaxChoices() == 0 ? "checked" : "" %> ></td>
								<td>&nbsp;&nbsp;</td>
								<td><? install/htdocs/sv/jsp/category_admin/multi_choice ?></td>
							</tr>
							</table></td>
            </tr>
            <tr>
                <td><? install/htdocs/sv/jsp/category_admin/inherited ?></td>
                <td><input type="checkbox" name="<%=AdminCategories.PARAMETER__INHERITED%>" value="1" <%= categoryTypeToEdit.isInherited() ? "checked" : "" %> ></td>
            </tr>
            <tr>
                <td><? install/htdocs/sv/jsp/category_admin/image_archive ?></td>
                <td><input type="checkbox" name="<%=AdminCategories.PARAMETER__IMAGE_ARCHIVE%>" value="1" <%= categoryTypeToEdit.isImageArchive() ? "checked" : "" %> ></td>
            </tr>
