<%@ page language="java" contentType="text/html; charset=UTF-8"
   pageEncoding="UTF-8"%>

<%@include file="/WEB-INF/jsp/admin/includes/taglibs.jsp"%>
<%@include file="/WEB-INF/jsp/admin/includes/common_variables.jsp"%>

<form:form action="${contextPath}/newadmin/changeSeveral" method="post" commandName="csc">
   <spring:message var="datePattern" code="admin/common/js_preset_date_pattern" htmlEscape="true"/>
   <spring:message var="timePattern" code="admin/common/js_preset_time_pattern" htmlEscape="true"/>
   <spring:message var="delimiter" code="admin/common/js_date_time_delimiter" htmlEscape="true"/>
   <input type="hidden" id="dateTimePattern" value="${datePattern}${delimiter}${timePattern}"/>
   <input type="hidden" id="dateTimeDelimiter" value="${delimiter}"/>
   
   <table cellspacing="0" cellpadding="0" style="width: 570px;">
      <tr>
         <td colspan="2">
            <a href="#expand-all" id="toggleAll"><spring:message code="admin/change_several/expand_colapse_all" /></a>
         </td>
      </tr>

      <tr>
         <td /><td height="20" style="width: 501px;">${hrImage}</td>
      </tr>

      <tr>
         <td>
            <a id="close_profile" href="#profile"><spring:message code="admin/terms/profile" /></a>
         </td>
         <td id="fold_profile">
            <spring:nestedPath path="profile">
            <c:set value="${csc.simpleActions}" var="actions" />

            <%@include file="/WEB-INF/jsp/admin/change_several/radio_actions.jsp"%>

            <form:select path="value">
               <form:options items="${raw.profiles}" itemValue="value" itemLabel="name" />
            </form:select>
            </spring:nestedPath>
         </td>
      </tr>

      <tr>
         <td /><td height="20" style="width: 501px;">${hrImage}</td>
      </tr>

      <tr>
         <td>
            <a id="close_role" href="#role"><spring:message code="admin/change_several/roles_on_this_page_only" /></a>
         </td>
         <td id="fold_role">
            <spring:nestedPath path="roleOnPage">
            
            <c:set value="${csc.roleActions}" var="actions" />
            <%@include file="/WEB-INF/jsp/admin/change_several/radio_actions.jsp"%>
            <br />

            <spring:nestedPath path="value">
            <table>
               <%@ include file="/WEB-INF/jsp/admin/includes/profile_roles/create_profile_role.jsp"%>
            </table>
            </spring:nestedPath>
            
            </spring:nestedPath>
         </td>
      </tr>

      <tr>
         <td /><td height="20" style="width: 501px;">${hrImage}</td>
      </tr>

      <tr>
         <td>
            <a id="close_category" href="#category"><spring:message code="admin/terms/categories" /> </a>
         </td>
         <td id="fold_category">
            <spring:nestedPath path="categories">
            
            <c:set value="${csc.categoryActions}" var="actions" />
            <%@include
               file="/WEB-INF/jsp/admin/change_several/radio_actions.jsp"%>
            <br />

            <c:set value="categories.value" var="id" />
         
            <c:set value="admin/change_several/select" var="unselectedColumnTitle" />
            <c:set value="${raw.categoryTypes}" var="notSelectedPairs" />

            <c:set value="admin/change_several/use" var="selectedColumnTitle" />
            <c:set value="${csc.categories.value.selectedColumns}" var="selectedPairs" />
            
            <table>
               <%@include file="/WEB-INF/jsp/admin/includes/select_columns.jsp"%>
            </table>
            
            </spring:nestedPath>
         </td>
      </tr>

      <tr>
         <td /><td height="20" style="width: 501px;">${hrImage}</td>
      </tr>

      <tr>
         <td>
            <a id="close_status" href="#sstatus"><spring:message code="admin/terms/status" /></a>
         </td>
         <td id="fold_status">
            <c:set value="${csc.simpleActions}" var="actions" />
         
            <table>
               <tr>
                  <spring:nestedPath path="publicationStatus">
                  
                  <td>
                     <%@include file="/WEB-INF/jsp/admin/change_several/radio_actions.jsp"%>
                  </td>
                  <td colspan="3">
                     <form:select path="value" cssStyle="width:110px">
                        <form:options items="${raw.publicationStatuses}" itemValue="value" itemLabel="name" />
                     </form:select>
                  </td>
                  
                  </spring:nestedPath>
               </tr>
               
               <tr>
                  <spring:nestedPath path="publicationDateTime">
                  
                  <td><%@include  file="/WEB-INF/jsp/admin/change_several/radio_actions.jsp"%></td>
                  <td width="110"><spring:message code="admin/change_several/publication_date_time" /></td>
                  <td><form:input path="value.date" size="8" /></td>
                  <td>
                     <form:input path="value.time" size="5" />
                     <input type="image" id="${nestedPath}Btn" src="${contextPath}/imcms/jscalendar/images/img.gif" style="cursor:pointer;cursor:hand;" />
                  </td>
                     
                  </spring:nestedPath>
               </tr>
               
               <tr>
                  <spring:nestedPath path="archiveDateTime">
                  
                  <td><%@include file="/WEB-INF/jsp/admin/change_several/radio_actions.jsp"%></td>
                  <td width="110"><spring:message code="admin/change_several/archive_date_time" /></td>
                  <td><form:input path="value.date" size="8" /></td>
                  <td>
                     <form:input path="value.time" size="5" />
                     <input type="image"  id="${nestedPath}Btn" src="${contextPath}/imcms/jscalendar/images/img.gif" style="cursor:pointer;cursor:hand;"/>   
                  </td>
                     
                  </spring:nestedPath>
               </tr>
               
               <tr>
                  <spring:nestedPath path="expiredDateTime">
                  
                  <td><%@include file="/WEB-INF/jsp/admin/change_several/radio_actions.jsp"%></td>
                  <td width="110"><spring:message code="admin/change_several/expired_date_time" /></td>
                  <td><form:input path="value.date" size="8" /></td>
                  <td>
                     <form:input path="value.time" size="5" />
                     <input type="image"  id="${nestedPath}Btn" src="${contextPath}/imcms/jscalendar/images/img.gif" style="cursor:pointer;cursor:hand;"/>
                  </td>
                  
                  </spring:nestedPath>
               </tr>
            </table>
         </td>
      </tr>

      <tr>
         <td /><td height="20" style="width: 501px;">${hrImage}</td>
      </tr>

      <tr>
         <td>
            <a id="close_stats" href="#stats"><spring:message code="admin/terms/stats" /></a></td>
         <td id="fold_stats">
            <table>
               <tr>
                  <spring:nestedPath path="publishOnDateTime">
                  
                  <td><%@include file="/WEB-INF/jsp/admin/change_several/radio_actions.jsp"%></td>
                  <td width="110"><spring:message code="admin/change_several/published_on" /></td>
                  <td><form:input path="value.date" size="8" /></td>
                  <td>
                     <form:input path="value.time" size="5" />
                     <input type="image" id="${nestedPath}Btn" src="${contextPath}/imcms/jscalendar/images/img.gif" style="cursor:pointer;cursor:hand;"/>
                  </td>
                     
                  </spring:nestedPath>
               </tr>
               <tr>
                  <spring:nestedPath path="lastChangeDateTime">
                  
                  <td><%@include file="/WEB-INF/jsp/admin/change_several/radio_actions.jsp"%></td>
                  <td width="110"><spring:message code="admin/change_several/last_change_on" /></td>
                  <td><form:input path="value.date" size="8" /></td>
                  <td>
                     <form:input path="value.time" size="5" />
                     <input type="image" id="${nestedPath}Btn" src="${contextPath}/imcms/jscalendar/images/img.gif" style="cursor:pointer;cursor:hand;"/>
                  </td>
                     
                  </spring:nestedPath>
               </tr>
            </table>
         </td>
      </tr>

      <tr>
         <td /> <td height="20" style="width: 501px;">${hrImage}</td>
      </tr>

      <tr>
         <td>
            <a id="close_persons" href="#persons"><spring:message code="admin/terms/persons" /></a>
         </td>
         <td id="fold_persons">
            <spring:nestedPath path="persons">
            
            <table>
               <tr>
                  <td colspan="2">
                     <%@include file="/WEB-INF/jsp/admin/change_several/radio_actions.jsp"%>
                  </td>
               </tr>
               
               <tr>
                  <td><spring:message code="admin/terms/creator" /></td>
                  <td><spring:message code="admin/terms/publisher" /></td>
               </tr>
               
               <tr>
                  <td>
                     <form:select path="value.creator">
                        <option value="-1"><spring:message code="admin/change_several/do_not_change" /></option>
                        <form:options items="${raw.creators}" itemValue="value" itemLabel="name" />
                     </form:select>
                  </td>
                  <td>
                     <form:select path="value.publisher">
                        <option value="-1"><spring:message code="admin/change_several/do_not_change" /></option>
                        <form:options items="${raw.publishers}" itemValue="value" itemLabel="name" />
                     </form:select>
                  </td>
               </tr>
            </table>
            
         </spring:nestedPath>
         </td>
      </tr>

      <tr>
         <td /> <td height="20" style="width: 501px;">${hrImage}</td>
      </tr>

      <tr>
         <td>
            <a id="close_text" href="#text"> <spring:message code="admin/change_several/change_text" /></a>
         </td>
         <td id="fold_text">
            <spring:nestedPath path="text">
            
            <table>
               <tr>
                  <td colspan="3">
                     <%@include file="/WEB-INF/jsp/admin/change_several/radio_actions.jsp"%>
                  </td>
               </tr>
               
               <spring:nestedPath path="value">
               
               <tr>
                  <td>
                     <spring:message code="admin/change_several/change_text_in_textfield" />
                  </td>
                  <td>
                     <form:select path="chnageIn" cssStyle="width:140px">
                        <form:options items="${raw.textFields}" itemValue="value" itemLabel="name" />
                     </form:select>
                  </td>
                  <td><form:input path="changeInNo" size="6" /></td>
               </tr>
               
               <tr>
                  <td>
                     <spring:message code="admin/change_several/use_text_in" />
                  </td>
                  <td>
                     <form:select path="useTextIn" cssStyle="width:140px">
                        <form:options items="${raw.textFields}" itemValue="value" itemLabel="name" />
                     </form:select>
                  </td>
                  <td><form:input path="useTextInNo" size="6" /></td>
               </tr>
               
               <tr>
                  <td />
                  <td colspan="2">
                     <c:forEach items="${csc.textEditModes}" var="textEditMode">
                        <spring:message code="admin/change_several/${fn:toLowerCase(textEditMode)}" htmlEscape="true" var="textEditModeMessage" />
                        <form:radiobutton path="textEditMode" value="${textEditMode}" label="${textEditModeMessage}" />
                     </c:forEach>
                  </td>
               </tr>
               
               <tr>
                  <td />
                  <td colspan="2"><form:textarea path="text" /></td>
               </tr>
               
               </spring:nestedPath>
            </table>
            
         </spring:nestedPath>
         </td>
      </tr>

      <tr>
         <td />
         <td height="20" style="width: 501px;">${hrImage}</td>
      </tr>

      <tr>
         <td colspan="2">
            <div>
               <div style="float: left">
                  <spring:message code="admin/controls/clear" htmlEscape="true" var="clearButtonMessage" />
                  <input type="submit" id="clearButton" value="${clearButtonMessage}" class="imcmsFormBtn" />
               </div>
               
               <div style="float: right">
                  <spring:message code="admin/controls/change" htmlEscape="true" var="changeButtonMessage" />
                  <input type="button" id="changeButton" value="${changeButtonMessage}" class="imcmsFormBtn" />
               </div>
            </div>
         </td>
      </tr>
   </table>
</form:form>