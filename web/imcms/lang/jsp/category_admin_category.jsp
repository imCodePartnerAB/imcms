<%@ page import="com.imcode.imcms.servlet.superadmin.AdminCategories,
                 imcode.server.document.CategoryDomainObject,
                 org.apache.commons.lang.StringEscapeUtils,
                 org.apache.commons.lang.ObjectUtils"%>
        <tr><td colspan="2">&nbsp;</td></tr>
        <tr>
		    <td width="80" height="24" class="imcmsAdmText" nowrap><? install/htdocs/sv/jsp/category_admin/category_name ?> &nbsp;</td>
            <td>
                <input type="text" name="<%= AdminCategories.PARAMETER__NAME %>" <% if (readonly) {%>readonly<%}%> size="30" maxlength="50" value="<%= StringEscapeUtils.escapeHtml((String)ObjectUtils.defaultIfNull(categoryToEdit.getName(),"")) %>">
                <% if (readonly) { %>
                    &nbsp;
                    <? install/htdocs/sv/jsp/category_admin/category_id ?>: <%= categoryToEdit.getId() %>
                <% } %>
            </td>
        </tr>
        <tr>
		    <td class="imcmsAdmText" nowrap><? install/htdocs/sv/jsp/category_admin/description ?> &nbsp;</td>
            <td>
            <textarea name="description" cols="30" <% if (readonly) {%>readonly<%}%> size="30" rows="2" style="width: 100%;">
<%= StringEscapeUtils.escapeHtml((String)ObjectUtils.defaultIfNull(categoryToEdit.getDescription(),"")) %></textarea>
        </td>
        </tr>
        <tr>
		    <td class="imcmsAdmText" nowrap><? install/htdocs/sv/jsp/category_admin/icon ?> &nbsp;</td>
            <td>
            <input type="text" name="icon" <% if (readonly) {%>readonly<%}%> size="30" maxlength="255" value="<%= StringEscapeUtils.escapeHtml((String)ObjectUtils.defaultIfNull(categoryToEdit.getImage(),"")) %>">
            &nbsp;
            <% if (!readonly) { %><input type="submit" class="imcmsFormBtnSmall" name="<%= AdminCategories.PARAMETER__BROWSE_FOR_IMAGE %>" value=" Browse "><% } %>
            &nbsp;
            <% String image = categoryToEdit.getImage() ;
                if (null == image) {
                    image = "" ;
                }
                image = image.trim() ;
                if (!"".equals(image)) {
                    %><img src="<%= StringEscapeUtils.escapeHtml(image) %>" border="0" align="top"><%
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
