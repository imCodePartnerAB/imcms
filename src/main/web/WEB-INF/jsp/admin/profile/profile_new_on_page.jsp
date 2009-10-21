<%@ page language="java" contentType="text/html; charset=UTF-8"
   pageEncoding="UTF-8"%>

<%@include file="/WEB-INF/jsp/admin/includes/taglibs.jsp"%>

<%@include file="/WEB-INF/jsp/admin/includes/common_variables.jsp"%>

<form:form action="${contextPath}/newadmin/profiles/profileNewOnPage" commandName="pnpc">

   <table cellspacing="0" cellpadding="0" style="width: 570px;">
      <tr>
         <td>
         <table>
            <tr>
               <td width="150px"><spring:message code="admin/terms/name" /></td>
               <td width="150px"></td>
               <td width="150px"><spring:message code="admin/terms/profile_on_create_new" /></td>
            </tr>
            
            <tr>
               <td width="150px">
                  ${pnpc.name} <form:hidden path="name"/>
               </td>
               
               <td width="150px"></td>
               <td width="150px">
                  ${pnpc.profileOnCreateNew}
                  <form:hidden path="profileOnCreateNew" />
               </td>
            </tr>
         </table>
         </td>
         <td>
            <spring:message code="admin/controls/change_profile_on_page" htmlEscape="true" var="changeProfileOnPageButtonMessage" />
            <input type="button" id="changeProfileOnPageButton" value="${changeProfileOnPageButtonMessage}" class="imcmsFormBtn" />
         </td>
      </tr>

      <tr>
         <td colspan="2" height="20">${hrImage}</td>
      </tr>

      <tr>
         <td>
            <a id="close_profile" href="#roles_in_profile"><spring:message code="admin/profile/profile_new_on_page/roles_in_profile" /></a>
         </td>
         <td>
            <spring:message code="admin/controls/profile_administration" htmlEscape="true" var="profileAdministrationButtonMessage" />
            <input type="button" id="profileAdminstrationButton" value="${profileAdministrationButtonMessage}" class="imcmsFormBtn" />
         </td>
      </tr>

      <tr>
         <td id="fold_profile">
            <table>
               <c:set value="${pnpc.rolesInProfile}" var="profileRoles" />
               <%@include file="/WEB-INF/jsp/admin/includes/profile_roles/delete_profile_role.jsp"%>
            </table>
         </td>
         <td></td>
      </tr>

      <tr>
         <td colspan="2" height="20">${hrImage}</td>
      </tr>

      <tr>
         <td>
            <a id="close_page" href="#roles_on_page"><spring:message code="admin/profile/profile_new_on_page/added_roles_on_this_page_only" /></a>
         </td>
         <td>
            <spring:message code="admin/controls/show_hide_add_roles" htmlEscape="true" var="showHideAddRolesButtonMessage" />
            <input type="button" id="showHideAddRoles" value="${showHideAddRolesButtonMessage}" class="imcmsFormBtn" />
         </td>
      </tr>
      
      <tr id="fold_page">
         <td>
            <table>
               <spring:nestedPath path="newRoleOnPage">
                  <%@include file="/WEB-INF/jsp/admin/includes/profile_roles/create_profile_role.jsp"%>
               </spring:nestedPath>
            </table>
            <br />
            
            <table>
               <c:set value="${pnpc.rolesOnPage}" var="profileRoles" />
               <%@include file="/WEB-INF/jsp/admin/includes/profile_roles/delete_profile_role.jsp"%>
            </table>
         </td>
         <td>
            <spring:message code="admin/controls/create" htmlEscape="true" var="createButtonMessage" />
            <input type="button" id="createButton" value="${createButtonMessage}" class="imcmsFormBtn" />
         </td>
      </tr>
   </table>
</form:form>