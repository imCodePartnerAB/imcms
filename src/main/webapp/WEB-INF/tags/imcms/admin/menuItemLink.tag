<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="classes" required="false" %>

<c:set var="classes" value="${classes ne null ? '' : ' class=\"'.concat(classes).concat('\"')}"/>

<a id="${docId}"${classes} href="${pathToDocument}" target="${target}"><jsp:doBody/></a>
