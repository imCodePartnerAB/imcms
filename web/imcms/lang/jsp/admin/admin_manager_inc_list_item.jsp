<%@ page import="org.apache.commons.lang.StringEscapeUtils,
                 imcode.server.document.DocumentMapper,
                 imcode.server.document.DocumentDomainObject,
                 imcode.util.Utility,
                 imcode.server.Imcms,
                 imcode.util.Html"%><%@taglib prefix="vel" uri="/WEB-INF/velocitytag.tld"%>
<jsp:useBean id="listItemBean" class="com.imcode.imcms.servlet.superadmin.AdminManagerSubReportListItemBean" scope="request"/>
<%
    String imagesPath  = request.getContextPath()+"/imcms/"+Utility.getLoggedOnUser( request ).getLanguageIso639_2()+"/images/admin/" ;
    DocumentDomainObject document = listItemBean.getDocument() ;
    String linkedStatusIcon = Html.getLinkedStatusIconTemplate(document, Utility.getLoggedOnUser( request ), request );
%>
<vel:velocity>
<tr valign="top" <%= listItemBean.getIndex() % 2 == 1 ? "bgcolor=\"#ffffff\"" : "" %> >
    <td><img src="<%= imagesPath %>/1x1.gif" width="1" height="3"><br>
        <a href="$contextPath/servlet/GetDoc?meta_id=<%= document.getId() %>"><%= document.getId() %></a></td>
    <td align="center"><%= linkedStatusIcon %></td>
    <td><img src="<%= imagesPath %>/1x1.gif" width="1" height="3"><br>
        <a href="$contextPath/servlet/GetDoc?meta_id=<%= document.getId()%>"><%= StringEscapeUtils.escapeHtml(document.getHeadline()) %></a><br>
        <%= StringEscapeUtils.escapeHtml(document.getMenuText() ) %></td>
    <td align="left"><img src="<%= imagesPath %>/1x1.gif" width="1" height="3">
        <jsp:include page="admin_manager_expandable_dates_column.jsp"/>
    </td>
   </tr>
</vel:velocity>


