<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>

<%-- 
This message will be never displayed. This file must be included in such
way: 

<%@include file="/WEB-INF/jsp/admin/profile/permission_checkbox_variables.jsp"%>

and <spring:nestedPath/> should be initialized properly. See file
default_perm_groups.jsp. We need include taglibs.jsp file only for IDE.
E.g. without it Eclipse cannot find using taglibs and show warning near
specific tags.
--%>
<%@include file="/WEB-INF/jsp/admin/includes/taglibs.jsp"%>

<c:set var="readCheckbox">
    <spring:message code="admin/profile/perm_groups/read" var="readMessage" />
    <form:checkbox path="read" label="${readMessage}" />
</c:set>

<c:set var="pageInfoCheckbox">
    <spring:message code="admin/profile/perm_groups/page_info" var="pageInfoMessage" />
    <form:checkbox path="pageInfo" label="${pageInfoMessage}" />
</c:set>

<c:set var="publishCheckbox">
    <spring:message code="admin/profile/perm_groups/profile" var="profileMessage" />
    <form:checkbox path="profile" label="${profileMessage}" />
</c:set>

<c:set var="textCheckbox">
    <spring:message code="admin/profile/perm_groups/text" var="textMessage" />
    <form:checkbox path="text" label="${textMessage}" />
</c:set>

<c:set var="contentLoopCheckbox">
    <spring:message code="admin/profile/perm_groups/content_loop" var="contentLoopMessage" />
    <form:checkbox path="contentLoop" label="${contentLoopMessage}" />
</c:set>

<c:set var="adminTextCheckbox">
    <spring:message code="admin/profile/perm_groups/admin_text" var="adminTextMessage" />
    <form:checkbox path="adminText" label="${adminTextMessage}" />
</c:set>

<c:set var="externalLinkCheckbox">
    <spring:message code="admin/profile/perm_groups/external_link" var="externalLinkMessage" />
    <form:checkbox path="externalLink" label="${externalLinkMessage}" />
</c:set>

<c:set var="textdocCheckbox">
    <spring:message code="admin/profile/perm_groups/textdoc" var="textdocMessage" />
    <form:checkbox path="textdoc" label="${textdocMessage}" />
</c:set>

<c:set var="htmlCheckbox">
    <spring:message code="admin/profile/perm_groups/html" var="htmlMessage" />
    <form:checkbox path="html" label="${htmlMessage}" />
</c:set>

<c:set var="profileCheckbox">
    <spring:message code="admin/profile/perm_groups/profiles" var="profilesMessage" />
    <form:checkbox path="profile" label="${profilesMessage}" />
</c:set>

<c:set var="imageCheckbox">
    <spring:message code="admin/profile/perm_groups/image"
                    var="imageMessage" />
    <form:checkbox path="image" label="${imageMessage}" />
</c:set>

<c:set var="adminImageCheckbox">
    <spring:message code="admin/profile/perm_groups/admin_image" var="adminImageMessage" />
    <form:checkbox path="adminImage" label="${adminImageMessage}" />
</c:set>

<c:set var="internalLinkCheckbox">
    <spring:message code="admin/profile/perm_groups/internal_link" var="internalLinkMessage" />
    <form:checkbox path="internalLink" label="${internalLinkMessage}" />
</c:set>

<c:set var="fileCheckbox">
    <spring:message code="admin/profile/perm_groups/file" var="fileMessage" />
    <form:checkbox path="file" label="${fileMessage}" />
</c:set>

<c:set var="adminMenuCheckbox">
    <spring:message code="admin/profile/perm_groups/admin_menu" var="adminMenuMessage" />
    <form:checkbox path="adminMenu" label="${adminMenuMessage}" />
</c:set>