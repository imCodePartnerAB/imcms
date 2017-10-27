<%@ page contentType="text/html; charset=UTF-8"
         import="imcode.server.document.DocumentDomainObject,
                 imcode.server.user.UserDomainObject,
                 imcode.util.Utility,
                 org.apache.commons.lang3.ObjectUtils,
                 org.apache.commons.text.StringEscapeUtils" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags/imcms/ui" %>
<jsp:useBean id="listItemBean" class="com.imcode.imcms.servlet.beans.AdminManagerSubReportListItemBean" scope="request"/>
<%
    String imagesPath  = request.getContextPath()+"/imcms/"+Utility.getLoggedOnUser( request ).getLanguageIso639_2()+"/images/admin/" ;
    DocumentDomainObject document = listItemBean.getDocument() ;
    UserDomainObject user = Utility.getLoggedOnUser( request ) ;

%>
<tr valign="top"<%= listItemBean.getIndex() % 2 != 1 ? " bgcolor=\"#ffffff\"" : "" %>><%
    String alias = document.getAlias();
    if ( alias != null ) { %>
    <td><a name="alias"
           href="${contextPath}/<%= document.getAlias() %>"><%= StringEscapeUtils.escapeHtml4(document.getAlias()) %>
    </a></td>
    <% }else { %>
    <td>&nbsp;</td> <%}%>
    <td align="center"><ui:statusIcon lifeCyclePhase="<%=document.getLifeCyclePhase()%>"/></td>
    <td><img src="<%= imagesPath %>/1x1.gif" width="1" height="3"><br><%
		if (user.canEdit(document)) {
    %><a href="${contextPath}/servlet/AdminDoc?meta_id=<%= document.getId() %>"
         title="AdminDoc?meta_id=<%= document.getId() %>"><%
		}
		%><%= document.getId() %><%
		if (user.canEdit(document)) {
			%></a><%
		} %></td>
    <td><img src="<%= imagesPath %>/1x1.gif" width="1" height="3"><br>
    <%= document.getDocumentType().getName().toLocalizedString(request) %></td>
    <td><img src="<%= imagesPath %>/1x1.gif" width="1" height="3"><br>
        <a href="${contextPath}/<%= ObjectUtils.defaultIfNull(alias, document.getId())%>"
           target="<%= document.getTarget() %>"<%
				%> title="<%=ObjectUtils.defaultIfNull(alias, document.getId())%>"><%= StringEscapeUtils.escapeHtml4(document.getHeadline()) %></a><br>
        <%= StringEscapeUtils.escapeHtml4(document.getMenuText() ) %></td>
    <td align="left"><img src="<%= imagesPath %>/1x1.gif" width="1" height="3">
        <jsp:useBean id="expandableDatesBean" class="com.imcode.imcms.servlet.beans.AdminManagerExpandableDatesBean" scope="request"/>
        <jsp:setProperty name="expandableDatesBean" property="document" value="<%= document %>"/>
        <jsp:setProperty name="expandableDatesBean" property="expanded" value="<%= listItemBean.isExpanded() %>"/>
        <jsp:include page="admin_manager_expandable_dates.jsp"/>
    </td>
   </tr>
