<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>

<%@include file="/WEB-INF/jsp/admin/includes/taglibs.jsp"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<vel:velocity>
    <html>

        <jsp:include page="/WEB-INF/jsp/admin/includes/html_head.jsp" />

        <body>
            <form style="display: none;" action="#">
                <input type="hidden" id="contextPath" value="${fn:escapeXml(contextPath)}" />
                <input type="hidden" id="adminModul" value="${adminModul}" />
            </form>
            <div id="container">
                #gui_outer_start()
                #gui_head( "<spring:message code="${pageTitle}" htmlEscape="true" />" )

                #gui_mid()

                <jsp:include page="/WEB-INF/jsp/admin/${includeModul}.jsp"></jsp:include>

                #gui_bottom()
                #gui_outer_end()
                <div>&nbsp;</div>
            </div>
        </body>
    </html>

</vel:velocity>