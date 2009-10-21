<%@ page language="java" contentType="text/html; charset=UTF-8"
   pageEncoding="UTF-8"%>

<%@include file="/WEB-INF/jsp/admin/includes/taglibs.jsp"%>

<c:forEach items="${actions}" var="action">
   <spring:message code="admin/change_several/${fn:toLowerCase(action)}"
      htmlEscape="true" var="actionMessage" />
   <form:radiobutton path="action" value="${action.name}"
      label="${actionMessage}" />
</c:forEach>