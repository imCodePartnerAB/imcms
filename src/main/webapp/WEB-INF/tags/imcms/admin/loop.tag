<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="imcms" uri="imcms" %>

<%-- do not remove - it helps Idea to understand var types --%>
<%--@elvariable id="loop" type="com.imcode.imcms.api.Loop"--%>
<%--@elvariable id="loopNo" type="java.lang.Integer"--%>
<%--@elvariable id="loopDoc" type="com.imcode.imcms.api.TextDocument"--%>

<%@ variable name-given="entryNo" scope="NESTED" variable-class="java.lang.Integer" %>
<%@ variable name-given="loopItem" scope="NESTED" variable-class="com.imcode.imcms.api.TextDocument.LoopItem " %>
<%@ variable name-given="loopEntryRef" scope="NESTED" variable-class="com.imcode.imcms.mapping.container.LoopEntryRef  " %>

<c:forEach var="loopEntry" items="${loop.entries}" varStatus="status">
    <c:set var="entryNo" value="${status.index}" scope="request"/>
    <c:set var="loopItem" value="${imcms:createLoopItem(loopEntry, loopNo, loopDoc.internal)}" scope="request"/>
    <c:set var="loopEntryRef" value="${loopItem.loopEntryRef}" scope="request"/>

    <jsp:doBody/>

    <c:remove var="loopItem"/>
    <c:remove var="loopEntryRef"/>
    <c:remove var="entryNo"/>
</c:forEach>
