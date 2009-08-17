<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>

<%@include file="/WEB-INF/jsp/admin/includes/taglibs.jsp"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<jsp:include page="/WEB-INF/jsp/admin/includes/profile_roles/profile_roles_header.jsp" />

<tr>

    <td width="150px">
        <form:select path="role" multiple="false" cssStyle="width:145px">
            <form:options items="${raw.roles}" itemValue="value" itemLabel="name" />
        </form:select>
    </td>

    <td width="150px">
        <form:select path="permissionGroup" multiple="false" cssStyle="width:145px">
            <form:options items="${raw.permissionGroups}" itemValue="value" itemLabel="name" />
        </form:select>
    </td>

    <td width="150px">
        <form:select path="profileOnCreateNew" multiple="false" cssStyle="width:145px">
            <form:options items="${raw.profilesOnCreateNew}" itemValue="value" itemLabel="name" />
        </form:select>
    </td>

    <td></td>
</tr>