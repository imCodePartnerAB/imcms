<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="imcms" uri="imcms" %>

<%--@elvariable id="menu" type="imcode.server.document.textdocument.MenuDomainObject"--%>
<%@ variable name-given="treeMenuItem" scope="NESTED" variable-class="imcode.server.document.textdocument.MenuItemDomainObject.TreeMenuItemDomainObject" %>

<c:forEach var="treeMenuItem" items="${menu.menuItemsVisibleToUserAsTree}">
    <c:set var="treeMenuItem" value="${treeMenuItem}" scope="request"/>
    <jsp:doBody/>
    <c:remove var="treeMenuItem"/>
</c:forEach>
