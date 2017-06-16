<%@ page import="com.imcode.imcms.servlet.superadmin.AdminCategories,
                 imcode.server.document.CategoryDomainObject,
                 org.apache.commons.lang3.StringEscapeUtils,
                org.apache.commons.lang3.ObjectUtils"%><%
boolean showLine  = (categoryToEdit != null && categoryToEdit.getName() != null) ;
String subHeading = inDeleteCategoryMode ? "<? install/htdocs/sv/jsp/category_admin/remove_category ?>" : "<? install/htdocs/sv/jsp/category_admin/edit_category ?>" ;
%><%
				if (showLine) { %>
        <tr>
					<td colspan="2">&nbsp;<br><br>
					#gui_heading( "<%= subHeading %> &nbsp;&quot;<%=
							StringEscapeUtils.escapeHtml4((String)ObjectUtils.defaultIfNull(categoryToEdit.getName(),""))
							%>&quot;" )</td>
				</tr><%
				} %>
        <tr>
		    <td width="110" height="24" class="imcmsAdmText" nowrap><? install/htdocs/sv/jsp/category_admin/category_name ?> &nbsp;</td>
            <td>
						<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td><input type="text" name="<%= AdminCategories.PARAMETER__NAME %>"<%= readonly ? " readonly" : "" %> size="50" maxlength="128" value="<%=
							StringEscapeUtils.escapeHtml4((String)ObjectUtils.defaultIfNull(categoryToEdit.getName(),""))
							%>"></td><%
							if (readonly) { %>
							<td>&nbsp;&nbsp;</td>
							<td><? install/htdocs/sv/jsp/category_admin/category_id ?>: <%= categoryToEdit.getId() %></td><%
							} %>
						</tr>
						</table></td>
        </tr>
        <tr>
		    <td class="imcmsAdmText" nowrap><? install/htdocs/sv/jsp/category_admin/description ?> &nbsp;</td>
            <td>
            <textarea name="description" cols="30" <% if (readonly) {%>readonly<%}%> size="30" rows="3" style="width:100%; overflow:auto;">
<%= StringEscapeUtils.escapeHtml4((String)ObjectUtils.defaultIfNull(categoryToEdit.getDescription(),"")) %></textarea>
        </td>
        </tr>
        <tr>
		    <td class="imcmsAdmText" nowrap><? install/htdocs/sv/jsp/category_admin/icon ?> &nbsp;</td>
            <td>
            <input type="text" name="icon" <% if (readonly) {%>readonly<%}%> size="30" maxlength="255" value="<%= StringEscapeUtils.escapeHtml4((String)ObjectUtils.defaultIfNull(categoryToEdit.getImageUrl(),"")) %>">
            &nbsp;
            <% if (!readonly) { %><input type="submit" class="imcmsFormBtnSmall" name="<%= AdminCategories.PARAMETER__BROWSE_FOR_IMAGE %>" value=" Browse "><% } %>
            &nbsp;
            <% String image = categoryToEdit.getImageUrl() ;
                if (null == image) {
                    image = "" ;
                }
                image = image.trim() ;
                if (!"".equals(image)) {
                    %><img src="<%= StringEscapeUtils.escapeHtml4(image) %>" border="0" align="top"><%
                }
            %>
			</td>
        </tr>
        <% if (!readonly) { %>
        <tr>
		    <td class="imcmsAdmText" nowrap><? install/htdocs/sv/jsp/category_admin/add_to_category_type ?> &nbsp;</td>
            <td><select name="<%= AdminCategories.PARAMETER_SELECT__CATEGORY_TYPE_TO_ADD_TO %>">
                    <%= AdminCategories.createHtmlOptionListOfCategoryTypes(categoryToEdit.getType()) %>
                </select></td>
        </tr>
        <% } %>
