<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="imcms" uri="imcms" %>

<%-- do not remove - it helps Idea to understand var types --%>
<%--@elvariable id="loop" type="com.imcode.imcms.domain.dto.LoopDTO"--%>

<%@ variable name-given="entryNo" scope="NESTED" variable-class="java.lang.Integer" %>
<%@ variable name-given="loopItem" scope="NESTED" variable-class="com.imcode.imcms.domain.dto.LoopEntryDTO" %>

<c:forEach var="loopEntry" items="${loop.entries}" varStatus="status">
    <c:if test="${loopEntry.enabled}">
        <c:set var="loopItem" value="${loopEntry}" scope="request"/>
        <c:set var="entryNo" value="${loopItem.no}" scope="request"/>

        <jsp:doBody/>

        <c:remove var="loopItem"/>
        <c:remove var="entryNo"/>
    </c:if>
</c:forEach>
