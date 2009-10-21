<%@ page contentType="text/html; charset=UTF-8"
        import="org.apache.commons.lang.StringEscapeUtils,
                 imcode.server.document.DocumentDomainObject,
                 imcode.util.Utility,
                 imcode.util.Html,
                 imcode.server.user.UserDomainObject"%>
<%@ page import="org.apache.commons.lang.ObjectUtils"%>
<%@taglib prefix="vel" uri="imcmsvelocity"%>
<jsp:useBean id="listItemBean" class="com.imcode.imcms.servlet.beans.AdminManagerSubReportListItemBean" scope="request"/>
<%
    String imagesPath  = request.getContextPath()+"/imcms/"+Utility.getLoggedOnUser( request ).getLanguageIso639_2()+"/images/admin/" ;
    DocumentDomainObject document = listItemBean.getDocument() ;
    String linkedStatusIcon = Html.getLinkedStatusIconTemplate(document, Utility.getLoggedOnUser( request ), request );
    UserDomainObject user = Utility.getLoggedOnUser( request ) ;
%>
<vel:velocity>
<tr valign="top"<%= listItemBean.getIndex() % 2 != 1 ? " bgcolor=\"#ffffff\"" : "" %>><%
    String alias = document.getAlias();
    if ( alias != null ) { %>
    <td><a name="alias" href="$contextPath/<%= document.getAlias() %>"><%= StringEscapeUtils.escapeHtml(document.getAlias()) %></a></td>
    <% }else { %>
    <td>&nbsp;</td> <%}%>
    <td align="center"><%= linkedStatusIcon %></td>
    <td><img src="<%= imagesPath %>/1x1.gif" width="1" height="3"><br><%
		if (user.canEdit(document)) {
			%><a href="$contextPath/servlet/AdminDoc?meta_id=<%= document.getId() %>" title="AdminDoc?meta_id=<%= document.getId() %>"><%
		}
		%><%= document.getId() %><%
		if (user.canEdit(document)) {
			%></a><%
		} %></td>
    <td><img src="<%= imagesPath %>/1x1.gif" width="1" height="3"><br>
    <%= document.getDocumentType().getName().toLocalizedString(request) %></td>
    <td><img src="<%= imagesPath %>/1x1.gif" width="1" height="3"><br>
        <a href="$contextPath/<%= ObjectUtils.defaultIfNull(alias, document.getId())%>" target="<%= document.getTarget() %>"<%
				%> title="<%=ObjectUtils.defaultIfNull(alias, document.getId())%>"><%= StringEscapeUtils.escapeHtml(document.getHeadline()) %></a><br>
        <%= StringEscapeUtils.escapeHtml(document.getMenuText() ) %></td>
    <td align="left"><img src="<%= imagesPath %>/1x1.gif" width="1" height="3">
        <jsp:useBean id="expandableDatesBean" class="com.imcode.imcms.servlet.beans.AdminManagerExpandableDatesBean" scope="request"/>
        <jsp:setProperty name="expandableDatesBean" property="document" value="<%= document %>"/>
        <jsp:setProperty name="expandableDatesBean" property="expanded" value="<%= listItemBean.isExpanded() %>"/>
        <jsp:include page="admin_manager_expandable_dates.jsp"/>
    </td>
   </tr>
</vel:velocity>
