<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="classes" required="false" %>

<c:set var="classes" value="${classes ne null ? '' : ' class=\"'.concat(classes).concat('\"')}"/>

<%-- do not remove - it helps Idea to understand var types --%>
<%--@elvariable id="docId" type="java.lang.Integer"--%>
<%--@elvariable id="pathToDocument" type="java.lang.String"--%>
<%--@elvariable id="target" type="java.lang.String"--%>

<a id="${docId}"${classes} href="${pathToDocument}" target="${target}"><jsp:doBody/></a>
