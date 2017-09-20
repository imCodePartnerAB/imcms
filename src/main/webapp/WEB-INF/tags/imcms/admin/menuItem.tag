<%-- do not remove - it helps Idea to understand var types --%>
<%--@elvariable id="treeMenuItem" type="imcode.server.document.textdocument.MenuItemDomainObject.TreeMenuItemDomainObject"--%>
<%--@elvariable id="currentDocument" type="com.imcode.imcms.api.TextDocument"--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="imcms" uri="imcms" %>

<%@ variable name-given="hasChildren" scope="NESTED" variable-class="java.lang.Boolean" %>
<c:set var="hasChildren" value="${treeMenuItem.subMenuItems.size() gt 0}" scope="request"/>

<%@ variable name-given="menuItem" scope="NESTED" variable-class="imcode.server.document.textdocument.MenuItemDomainObject" %>
<c:set var="menuItem" value="${treeMenuItem.menuItem}" scope="request"/>

<%@ variable name-given="docId" scope="NESTED" variable-class="java.lang.Integer" %>
<c:set var="docId" value="${menuItem.documentId}" scope="request"/>

<%@ variable name-given="isCurrent" scope="NESTED" variable-class="java.lang.Boolean" %>
<c:set var="isCurrent" value="${docId eq currentDocument.id}" scope="request"/>

<%@ variable name-given="pathToDocument" scope="NESTED" variable-class="java.lang.String" %>
<c:set var="pathToDocument" value="${imcms:getAbsolutePathToDocument(pageContext.request, menuItem.document)}" scope="request"/>

<%@ variable name-given="target" scope="NESTED" variable-class="java.lang.String" %>
<c:set var="target" value="${menuItem.document.target}" scope="request"/>

<%-- this done for nested menuLoop tags to pick up child documents --%>
<%@ variable name-given="menuItems" scope="NESTED" variable-class="java.util.Collection" %>
<c:set var="menuItems" value="${treeMenuItem.subMenuItems}" scope="request"/>

<jsp:doBody/>

<c:remove var="menuItem"/>
<c:remove var="hasChildren"/>
<c:remove var="docId"/>
<c:remove var="pathToDocument"/>
<c:remove var="target"/>
<c:remove var="isCurrent"/>
