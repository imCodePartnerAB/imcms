<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>

<%@include file="/WEB-INF/jsp/admin/includes/taglibs.jsp"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<jsp:include page="/WEB-INF/jsp/admin/includes/profile_roles/profile_roles_header.jsp" />

<spring:message code="admin/controls/delete" var="deleteButtonMessage" htmlEscape="true"/>

<c:forEach items="${profileRoles}" var="profileRole" varStatus="profileRoleStatus">
    <tr>
        <td width="150px">${profileRole.role}</td>
        <td width="150px">${profileRole.permissionGroup}</td>
        <td width="150px">${profileRole.profileOnCreateNew}</td>
        <td>
            <c:if test="${allowDelete}">
                <input type="button" id="deleteGroup${profileRoleStatus.index}" value="${deleteButtonMessage}" class="imcmsFormBtn" />
            </c:if>
        </td>
    </tr>
</c:forEach>