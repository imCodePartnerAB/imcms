<%--@elvariable id="treeMenuItem" type="imcode.server.document.textdocument.MenuItemDomainObject.TreeMenuItemDomainObject"--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="imcms" uri="imcms" %>

<%@ variable name-given="hasChildren" scope="NESTED" variable-class="java.lang.Boolean" %>
<c:set var="hasChildren" value="${treeMenuItem.subMenuItems.size() gt 0}" scope="request"/>

<%@ variable name-given="menuItem" scope="NESTED" variable-class="imcode.server.document.textdocument.MenuItemDomainObject" %>
<c:set var="menuItem" value="${treeMenuItem.menuItem}" scope="request"/>

<jsp:doBody/>

<c:remove var="menuItem"/>
<c:remove var="hasChildren"/>
