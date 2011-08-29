<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/jsp/image_archive/includes/taglibs.jsp" %>
<c:url var="eraseUrl" value="/web/archive/image/${image.id}/erase"/>
<form action="${eraseUrl}" method="post" style="text-align:center;margin-top:40px;">
    <input id="delete" type="hidden" name="delete" value=""/>
    
    <h3>
        <spring:message code="archive.imageCard.eraseConfirm" htmlEscape="false" arguments="<br/>"/>
    </h3><br/>
    
    <c:if test="${not empty image.metaIds}">
        <archive:params var="infoParams">
            <archive:param value="${archive:join(image.metaIds, ', ')}"/>
            <archive:param value="<br/>"/>
        </archive:params>

        <span><spring:message code="archive.imageCard.eraseConfirmInfo" arguments="${infoParams}"/></span><br/><br/>
    </c:if>

    <br/>
    
    <c:set var="noOnclick" value="$('#delete').val('no');"/>
    <spring:message var="noText" code="archive.imageCard.no" htmlEscape="true"/>
    <input type="submit" value="${noText}" onclick="${fn:escapeXml(noOnclick)}" class="btnBlue small"/>
    <c:set var="yesOnclick" value="$('#delete').val('yes');"/>
    <spring:message var="yesText" code="archive.imageCard.yes" htmlEscape="true"/>
    <input type="submit" value="${yesText}" onclick="${fn:escapeXml(yesOnclick)}" class="btnBlue small"/>
</form>
