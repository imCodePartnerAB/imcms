<%@ tag trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="imcms" uri="imcms" %>

<%@ attribute name="classes" required="false" %>

<c:set var="classes" value="${empty classes ? '' : ' class=\"'.concat(classes).concat('\"')}"/>

<%-- do not remove - it helps Idea to understand var types --%>
<%--@elvariable id="menuItem" type="com.imcode.imcms.domain.dto.MenuItemDTO"--%>
<%--@elvariable id="contextPath" type="com.imcode.imcms.domain.dto.MenuItemDTO"--%>

<c:set var="docId" value="${menuItem.documentId}"/>
<c:set var="pathToDocument" value="${contextPath}${menuItem.link}"/>
<c:set var="target" value="${menuItem.target}"/>

<a id="${docId}"${classes} href="${pathToDocument}" target="${target}">
    <jsp:doBody/>
</a>
