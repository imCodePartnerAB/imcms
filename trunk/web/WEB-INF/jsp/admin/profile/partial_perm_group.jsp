<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>

<%@include file="/WEB-INF/jsp/admin/includes/taglibs.jsp"%>

<%-- Nested path for full page --%>
<c:set value="${pgc.groups[0]}" var="thisGroup" />
<c:set value="groups[0]" var="nestedPath" />

<%-- Nested path for partialPage(NO tag form:form) --%>
<c:set value="partial" var="pageTypePartial" />
<c:if test="${ pageType eq pageTypePartial }">
    <c:set value="pgc.groups[0]" var="nestedPath" />
</c:if>

<c:if test="${ not empty thisGroup }"> 
    <spring:nestedPath path="${nestedPath}">
        <table width="100%">
            <tr>
                <td>
                    <a href="#permissions" id="close_permissions"> <spring:message code="admin/profile/perm_groups/perm_group" />:
                    ${thisGroup.name} </a>
                </td>
            </tr>

            <tr>
                <td id="fold_permissions">
                    <%@include file="/WEB-INF/jsp/admin/profile/permission_checkbox_variables.jsp" %>

                    <table width="100%">
                        <tr>
                            <td width="16%">
                                <b><spring:message code="admin/profile/perm_groups/read" /></b>
                            </td>
                            <td width="16%">
                                <b><spring:message code="admin/profile/perm_groups/meta_data" /></b>
                            </td>
                            <td width="32%" colspan="2">
                                <b><spring:message code="admin/profile/perm_groups/text_document" /></b>
                            </td>
                            <td width="32%" colspan="2">
                                <b><spring:message code="admin/profile/perm_groups/new_documents_links" /></b>
                            </td>
                        </tr>

                        <tr>
                            <td valign="top" width="16%">${readCheckbox}</td>

                            <td valign="top" width="16%">
                                ${pageInfoCheckbox}<br />
                                ${profileCheckbox}<br />
                                ${profileCheckbox}
                            </td>

                            <td valign="top" width="16%">
                                ${textCheckbox}<br />
                                ${imageCheckbox}<br />
                                ${contentLoopCheckbox}
                            </td>

                            <td valign="top" width="16%">
                                ${adminTextCheckbox}<br />
                                ${adminImageCheckbox}
                            </td>

                            <td valign="top" width="16%">
                                ${textdocCheckbox}<br />
                                ${fileCheckbox}<br />
                                ${externalLinkCheckbox}
                            </td>

                            <td valign="top" width="16%">
                                ${internalLinkCheckbox} <br />
                                ${htmlCheckbox}<br />
                                ${adminMenuCheckbox}
                            </td>
                        </tr>
                    </table>

                </td>
            </tr>

            <tr>
                <td height="20">${hrImage}</td>
            </tr>

        </table>
    </spring:nestedPath>
</c:if>