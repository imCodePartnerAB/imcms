<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>

<%@include file="/WEB-INF/jsp/admin/includes/taglibs.jsp"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<tr>
    <td width="150px"><b><spring:message code="admin/terms/role" /></b></td>
    <td width="150px"><b><spring:message code="admin/terms/permission_group" /></b></td>
    <td width="150px"><b><spring:message code="admin/terms/profile_on_create_new" /></b></td>
    <td></td>
</tr>