<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>

<%@include file="/WEB-INF/jsp/admin/includes/taglibs.jsp"%>

<%@include file="/WEB-INF/jsp/admin/includes/common_variables.jsp"%>

<form:form action="${contextPath}/newadmin/profiles/profileNew" method="post" commandName="pnc">
    <table cellspacing="0" cellpadding="0" style="width: 570px;">
        <tr>
            <td />
            <td>
                <table width="100%">
                    <tr>
                    <td valign="top" rowspan="3">
                        <c:forEach items="${pnc.allProfileActions}" var="enum">
                            <spring:message code="admin/profile/profile_new/${fn:toLowerCase(enum)}" var="label" />
                            <form:radiobutton path="profileAction" label="${label}" value="${enum}" /><br />
                        </c:forEach>
                    </td>

                    <td valign="top" rowspan="2">
                        <spring:message code="admin/profile/profile_new/select_profile" /><br />

                        <form:select path="profile" multiple="false">
                            <form:options items="${raw.profiles}" itemValue="value" itemLabel="name" />
                        </form:select> <br />
                    </td>

                    <%--
               <spring:message code="admin/profile/profile_new/write_name" var="writeNameMessage" />
                    --%>
                    <td valign="top">
                        <form:input path="name" id="changedName" size="10"/><br />
                    </td>

                    <td rowspan="3" valign="top" align="right">
                        <spring:message code="admin/controls/ok" var="okButtonMessage" />
                        <input id="okButton" type="button" value="${okButtonMessage}" class="imcmsFormBtn" />
                    </td>
                    <tr>
                        <td rowspan="2">
                            <form:input path="name" id="newName" size="10"/>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <spring:message code="admin/profile/profile_new/programming_name" /> <br />
                            <spring:message code="admin/profile/profile_new/do_not_change" />
                        </td>
                    </tr>
                </table>
            </td>
        </tr>

        <tr>
            <td /><td height="20" style="width: 501px;">${hrImage}</td>
        </tr>

        <tr class="postInit">
            <%--
         Categories
            --%>
            <td valign="top">
                <a id="close_categories" href="#categories"><spring:message code="admin/terms/categories" /></a>
            </td>
            <td id="fold_categories">


                <%--
         Category types
         --%>
                <c:set var="id" value="category_types" />
                <c:set var="unselectedColumnTitle" value="admin/terms/category_type" />
                <c:set var="unselectedColumnsPath" value="unselectedCategoryTypes" />
                <c:set var="notSelectedPairs" value="${raw.categoryTypes}" />

                <c:set var="selectedColumnTitle" value="admin/profile/profile_new/show_in_page_prefs" />
                <c:set var="selectedColumnsPath" value="selectedCategoryTypes" />
                <c:set var="selectedPairs" value="${pcn.selectedCategoryTypes}" />

                <table>
                    <%@include
                        file="/WEB-INF/jsp/admin/includes/select_columns.jsp"%>
                </table>


                <%--
         Categories
         --%>
                <c:set var="id" value="categories" />
                <c:set var="unselectedColumnTitle" value="admin/terms/category" />
                <c:set var="unselectedColumnsPath" value="selectedCategories" />
                <c:set var="notSelectedPairs" value="${raw.categoryTypes}" />

                <c:set var="selectedColumnTitle" value="admin/profile/profile_new/set_on_page_when_created" />
                <c:set var="selectedColumnsPath" value="selectedCategories" />
                <c:set var="selectedPairs" value="${pcn.selectedCategories}" />

                <table>
                    <%@include
                        file="/WEB-INF/jsp/admin/includes/select_columns.jsp"%>
                </table>
            </td>
        </tr>

        <tr>
            <td /><td height="20" style="width: 501px;">${hrImage}</td>
        </tr>

        <tr class="postInit">
            <%--
         Template
            --%>
            <td valign="top">
                <a id="close_template" href="#template"><spring:message code="admin/terms/template_default" /></a>
            </td>
            <td id="fold_template">
                <form:select path="template" multiple="false">
                    <form:options items="${raw.templates}" itemValue="value" itemLabel="name" />
                </form:select>
            </td>
        </tr>

        <tr>
            <td /><td height="20" style="width: 501px;">${hrImage}</td>
        </tr>

        <tr class="postInit">
            <%--
         Roles
            --%>
            <td valign="top">
                <a id="close_roles" href="#roles"><spring:message code="admin/terms/roles" /></a>
            </td>
            <td id="fold_roles">
                <spring:nestedPath path="newRoleInProfile">
                    <table>
                        <%@include file="/WEB-INF/jsp/admin/includes/profile_roles/create_profile_role.jsp"%>

                        <c:set value="${pnc.rolesInProfile}" var="profileRoles" />
                        <c:set value="${true}" var="allowDelete" />
                        <%@include file="/WEB-INF/jsp/admin/includes/profile_roles/delete_profile_role.jsp"%>

                    </table>
                </spring:nestedPath>
            </td>
        </tr>

        <tr>
            <td /><td height="20" style="width: 501px;">${hrImage}</td>
        </tr>

        <tr>
            <td colspan="2">
                <div>

                    <div style="float: left;">
                        <spring:message  code="admin/controls/clear" var="clearButtonMessage" htmlEscape="true" />
                        <input type="button" id="cleraButton" value="${clearButtonMessage}" class="imcmsFormBtn">
                    </div>

                    <div style="float: right;" class="postInit">
                        <spring:message code="admin/controls/revert" var="revertButtonMessage" htmlEscape="true" />
                        <input type="button" id="revertButton" value="${revertButtonMessage}" class="imcmsFormBtn" />

                        <spring:message code="admin/controls/save_and_update_doc"var="saveAndUpdateDocButtonMessage" htmlEscape="true" />
                        <input type="button" id="saveAndUpdateDocButton" value="${saveAndUpdateDocButtonMessage}" class="imcmsFormBtn" />

                        <spring:message code="admin/controls/save" var="saveButtonMessage" htmlEscape="true" />
                        <input type="button" id="saveButton" value="${saveButtonMessage}" class="imcmsFormBtn" />
                    </div>
                </div>
            </td>
        </tr>
    </table>
</form:form>