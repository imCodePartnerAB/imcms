<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.imcode.imcms.flow.Page, com.imcode.imcms.servlet.admin.LinkEditPage, java.util.concurrent.LinkedBlockingQueue, org.apache.commons.lang.StringEscapeUtils, com.imcode.imcms.servlet.admin.EditLink"%><%@page contentType="text/html"%> 
<html>
    <head>
        <title>Edit link</title>
    </head>
    <body>
        <form action="<%= request.getContextPath() %>/servlet/PageDispatcher">
            <%= Page.htmlHidden(request) %>
            <% LinkEditPage linkEditPage = Page.fromRequest(request);
               EditLink.Link link = linkEditPage.getLink(); %>
            <input type="text" name="<%= LinkEditPage.Parameter.HREF %>" value="<%= StringEscapeUtils.escapeHtml(link.getHref()) %>" />
            <input type="text" name="<%= LinkEditPage.Parameter.TITLE %>" value="<%= StringEscapeUtils.escapeHtml(link.getTitle()) %>" />
            <% if (linkEditPage.isTargetEditable()) { %>
            <input type="text" name="<%= LinkEditPage.Parameter.TARGET %>" value="<%= StringEscapeUtils.escapeHtml(link.getTarget()) %>" />
            <% } %>
            <input type="submit" name="ok" value="OK"/>
            <input type="submit" name="cancel" value="Cancel"/>
        </form>
    </body>
</html>
