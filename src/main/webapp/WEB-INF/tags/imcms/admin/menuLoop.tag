<%@ tag trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="imcms" uri="imcms" %>

<%-- do not remove - it helps Idea to understand var types --%>
<%--@elvariable id="menuItems" type="java.util.Collection<com.imcode.imcms.domain.dto.MenuItemDTO>"--%>
<%--@elvariable id="currentDocument" type="imcode.server.document.textdocument.TextDocumentDomainObject"--%>

<c:forEach var="treeMenuItem" items="${menuItems}">
    <c:set var="treeMenuItem" value="${treeMenuItem}" scope="request"/>

    <%-- this done for nested menuLoop tags to pick up child documents --%>
    <%@ variable name-given="menuItems" scope="NESTED" variable-class="java.util.Collection" %>
    <c:set var="menuItems" value="${treeMenuItem.children}" scope="request"/>

    <%@ variable name-given="hasChildren" scope="NESTED" variable-class="java.lang.Boolean" %>
    <c:set var="hasChildren" value="${menuItems.size() gt 0}" scope="request"/>

    <%@ variable name-given="menuItem" scope="NESTED"
                 variable-class="com.imcode.imcms.domain.dto.MenuItemDTO" %>
    <c:set var="menuItem" value="${treeMenuItem}" scope="request"/>

    <%@ variable name-given="isCurrent" scope="NESTED" variable-class="java.lang.Boolean" %>
    <c:set var="isCurrent" value="${menuItem.documentId eq currentDocument.id}" scope="request"/>

    <jsp:doBody/>

    <c:remove var="menuItem"/>
    <c:remove var="hasChildren"/>
    <c:remove var="isCurrent"/>
</c:forEach>
