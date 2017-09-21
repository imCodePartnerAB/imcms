<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="imcms" uri="imcms" %>

<%-- do not remove - it helps Idea to understand var types --%>
<%--@elvariable id="loop" type="com.imcode.imcms.api.Loop"--%>
<%@ variable name-given="loopEntry" scope="NESTED" variable-class="java.util.Map.Entry" %>

<c:forEach var="loopEntry" items="${loop.entries}">
    <c:set var="loopEntry" value="${loopEntry}" scope="request"/>
    <jsp:doBody/>
    <c:remove var="loopEntry"/>
</c:forEach>
