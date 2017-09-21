<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="imcms" uri="imcms" %>

<%@ attribute name="classes" required="false" %>

<c:set var="classes" value="${empty classes ? '' : ' class=\"'.concat(classes).concat('\"')}"/>

<%-- do not remove - it helps Idea to understand var types --%>
<%--@elvariable id="menuItem" type="imcode.server.document.textdocument.MenuItemDomainObject"--%>

<c:set var="docId" value="${menuItem.documentId}"/>
<c:set var="pathToDocument" value="${imcms:getAbsolutePathToDocument(pageContext.request, menuItem.document)}"/>
<c:set var="target" value="${menuItem.document.target}"/>

<a id="${docId}"${classes} href="${pathToDocument}" target="${target}"><jsp:doBody/></a>
