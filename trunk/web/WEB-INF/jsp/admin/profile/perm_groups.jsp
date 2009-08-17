<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>

<%@include file="/WEB-INF/jsp/admin/includes/taglibs.jsp"%>

<%@include file="/WEB-INF/jsp/admin/includes/common_variables.jsp" %>

<form:form action="${contextPath}/newadmin/profiles/permGroups"  commandName="pgc">
    <input type="hidden" name="strategy" id="strategyType" value=""/>
    <form:hidden path="selectedGroup"/>
    <table cellspacing="0" cellpadding="0" style="width: 570px;">
        <tr>
            <td>
                <a href="#groups" id="close_groups"><spring:message code="admin/profile/perm_groups/perm_group" /></a>
            </td>
        </tr>

        <tr>
            <td id="fold_groups">
                <table>
                    <tr>
                        <td>
                            <select id="permissionGroupsList" size="10" style="width:300px">
                                <c:forEach items="${raw.permissionGroups}" var="option">
                                    <c:set value="" var="selected"/>
                                    |<c:if test="${pgc.selectedGroup eq option.value}">
                                        <c:set value="selected" var="selected" />
                                    </c:if>
                                    <option value="${option.value}" ${selected}>${option.name}</option>
                                </c:forEach>
                            </select>
                        </td>

                        <td width="15%" valign="top">
                            <spring:message code="admin/controls/select" var="selectButtonMessage" htmlEscape="true"/>
                            <input id="selectButton" type="button" value="${selectButtonMessage}" class="imcmsFormBtn" />
                        </td>

                        <td />
                    </tr>
                    <tr>
                        <td><form:input path="name" /></td>

                        <td width="15%">
                            <spring:message code="admin/controls/add_new" var="addNewButtonMessage" />
                            <input id="addNewButton" type="button" value="${addNewButtonMessage}" class="imcmsFormBtn" />
                        </td>

                        <td width="15%">
                            <spring:message code="admin/controls/rename" var="renameButtonMessage" />
                            <input type="button" id="renameButton" value="${renameButtonMessage}"class="imcmsFormBtn" />
                        </td>
                    </tr>
                    <tr>
                        <td colspan="3"><spring:message code="admin/profile/perm_groups/system_denied_group" /></td>
                    </tr>
                </table>
            </td>
        </tr>

        <tr>
            <td height="20">${hrImage}</td>
        </tr>

        <tr>
            <td>
                <div id="partial_perm_group">
                    <%@include file="/WEB-INF/jsp/admin/profile/partial_perm_group.jsp"%>
                </div>
            </td>
        </tr>

        <tr>
            <td>

                <div>
                    <div style="float: left;">
                        <spring:message code="admin/controls/delete_group" var="deleteGroupButtonMessage" />
                        <input id="deleteGroupButton" type="button" class="imcmsFormBtn" value="${deleteGroupButtonMessage }" />

                        <spring:message code="admin/controls/clear" var="clearButtonMessage" />
                        <input id="clearButton" type="button" value="${clearButtonMessage}" class="imcmsFormBtn" />

                        <spring:message code="admin/controls/revert" var="revertButtonMessage" />
                        <input type="reset" value="${revertButtonMessage}" class="imcmsFormBtn" />
                    </div>

                    <div style="float: right;">
                        <spring:message code="admin/controls/save" var="saveButtonMessage" />
                        <input id="saveButton" type="button" value="${saveButtonMessage}" class="imcmsFormBtn" />
                    </div>
                </div>

            </td>
        </tr>
    </table>
</form:form>