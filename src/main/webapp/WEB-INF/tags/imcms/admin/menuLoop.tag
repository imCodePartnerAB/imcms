<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="imcms" uri="imcms" %>

<%-- do not remove - it helps Idea to understand var types --%>
<%--@elvariable id="menuItems" type="java.util.Collection"--%>
<%@ variable name-given="treeMenuItem" scope="NESTED" variable-class="imcode.server.document.textdocument.MenuItemDomainObject.TreeMenuItemDomainObject" %>

<c:forEach var="treeMenuItem" items="${menuItems}">
    <c:set var="treeMenuItem" value="${treeMenuItem}" scope="request"/>
    <jsp:doBody/>
    <c:remove var="treeMenuItem"/>
</c:forEach>
