<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>

<%@include file="/WEB-INF/jsp/admin/includes/taglibs.jsp"%>

<%@include file="/WEB-INF/jsp/admin/includes/common_variables.jsp" %>

<form:form action="${contextPath}/newadmin/profiles/defaultPermGroups" commandName="pgc">
    <table cellspacing="0" cellpadding="0" style="width: 570px;">

        <c:forEach items="${pgc.groups}" var="permissionGroup" varStatus="groupStatus">
            <c:if test="${!empty permissionGroup}">
                <tr>
                    <td>
                        <a id="close_${groupStatus.index}" href="#${groupStatus.index}">
                        <spring:message code="${permissionGroup.name}" htmlEscape="true" /></a>
                    </td>
                </tr>

                <tr>
                    <td id="fold_${groupStatus.index}">

                        <spring:nestedPath path="groups[${groupStatus.index}]">
                            <form:hidden path="id" />
                            <form:hidden path="name"/>
                            <form:hidden path="default"/>

                            <%@include file="/WEB-INF/jsp/admin/profile/permission_checkbox_variables.jsp"%>
                        </spring:nestedPath>

                        <c:set value="16%" var="tdWidth" />
                        <table>
                            <tr>
                                <td width="${tdWidth}">${pageInfoCheckbox}</td>
                                <td width="${tdWidth}">${textCheckbox}</td>
                                <td width="${tdWidth}">${adminTextCheckbox}</td>
                                <td width="${tdWidth}">${externalLinkCheckbox}</td>
                                <td width="${tdWidth}">${textdocCheckbox}</td>
                                <td width="${tdWidth}">${htmlCheckbox}</td>
                            </tr>

                            <tr>
                                <td width="${tdWidth}">${profileCheckbox}</td>
                                <td width="${tdWidth}">${imageCheckbox}</td>
                                <td width="${tdWidth}">${adminImageCheckbox}</td>
                                <td width="${tdWidth}">${internalLinkCheckbox}</td>
                                <td width="${tdWidth}">${fileCheckbox}</td>
                                <td width="${tdWidth}">${adminMenuCheckbox}</td>
                            </tr>
                        </table>

                    </td>
                </tr>

                <tr>
                    <td height="20">${hrImage}</td>
                </tr>

            </c:if>
        </c:forEach>

        <tr>
            <td>

                <div style="float: left">
                    <spring:message code="admin/controls/clear" var="clearMessage" htmlEscape="true"/>
                    <input type="button" value="${clearMessage}" class="imcmsFormBtn" id="clearDefaultPermGroups" />

                    <spring:message code="admin/controls/revert" var="revertMessage" htmlEscape="true"/>
                    <input type="reset" value="${revertMessage}" class="imcmsFormBtn" />
                </div>

                <div style="float: right">
                    <spring:message code="admin/controls/save" var="saveMessage" htmlEscape="true"/>
                    <input id="saveDefaultPermGRoup" type="submit" class="imcmsFormBtn" value="${saveMessage}" />
                </div>

            </td>
        </tr>
    </table>
</form:form>