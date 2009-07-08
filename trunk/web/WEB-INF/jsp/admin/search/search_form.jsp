<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
         import="com.imcode.imcms.web.admin.command.SearchDocumentsCommand.SearchType" %>
         
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="im" uri="imcms" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
<c:set var="imagesPath" value="${contextPath}/imcms/${locale.ISO3Language}/images/admin"/>
<c:set var="hrImage">
    <img src="${imagesPath}/1x1_cccccc.gif" width="100%" height="1"/>
</c:set>

<form:form id="searchForm" action="${pageContext.request.contextPath}/newadmin/search" method="post" commandName="search">
    <table cellspacing="0" cellpadding="0" style="width:570px;">
        <tr>
            <td colspan="2" valign="middle">
                <a id="toggleAll" href="#expand-all">
                    <spring:message code="admin/search/expand_collapse_all" htmlEscape="true"/>
                </a>
            </td>
        </tr>
        <tr>
            <td/><td height="20" style="width:501px;">${hrImage}</td>
        </tr>
        <tr>
            <td valign="middle" style="padding-right:15px;">
                <a id="close_persons" href="#persons">
                    <spring:message code="admin/search/persons" htmlEscape="true"/>
                </a>
            </td>
            <td id="fold_persons" style="${fold.personsCollapsed ? 'display:none;' : ''}">
                <input type="hidden" id="personsCollapsed" name="personsCollapsed" value="${fold.personsCollapsed}"/>
                
                <table cellspacing="0" cellpadding="0">
                    <c:set var="id" value="creators"/>
                    <c:set var="notSelectedPairs" value="${creators}"/>
                    <c:set var="selectedPairs" value="${searchCreators}"/>
                    <spring:message var="leftColumnTitle" code="admin/search/contains_creator" htmlEscape="true"/>
                    <spring:message var="rightColumnTitle" code="admin/search/use" htmlEscape="true"/>
                    <%@ include file="/WEB-INF/jsp/admin/search/select_columns.jspf" %>
                    
                    <tr><td colspan="3" height="15"/></tr>
                    
                    <c:set var="id" value="publishers"/>
                    <c:set var="notSelectedPairs" value="${publishers}"/>
                    <c:set var="selectedPairs" value="${searchPublishers}"/>
                    <spring:message var="leftColumnTitle" code="admin/search/contains_publisher" htmlEscape="true"/>
                    <spring:message var="rightColumnTitle" code="admin/search/use" htmlEscape="true"/>
                    <%@ include file="/WEB-INF/jsp/admin/search/select_columns.jspf" %>
                </table>
            </td>
        </tr>
        <tr><td/><td height="20">${hrImage}</td></tr>
        <tr>
            <td valign="middle" style="padding-right:15px;">
                <a id="close_profile" href="#profile">
                    <spring:message code="admin/search/profile" htmlEscape="true"/>
                </a>
            </td>
            <td id="fold_profile" style="${fold.profileCollapsed ? 'display:none;' : ''}">
                <input type="hidden" id="profileCollapsed" name="profileCollapsed" value="${fold.profileCollapsed}"/>
            
                <form:select path="template" cssStyle="width:200px;">
                    <option value="" ${empty search.template ? 'selected="selected"' : ''} >
                        <spring:message code="admin/search/no_template_selected" htmlEscape="true"/>
                    </option>
                    
                    <form:options items="${templates}" itemValue="name" itemLabel="name"/>
                </form:select>
            </td>
        </tr>
        <tr><td/><td height="20">${hrImage}</td></tr>
        <tr>
            <td valign="middle" style="padding-right:15px;">
                <a id="close_categories" href="#categories">
                    <spring:message code="admin/search/categories" htmlEscape="true"/>
                </a>
            </td>
            <td id="fold_categories" style="${fold.categoriesCollapsed ? 'display:none;' : ''}">
                <input type="hidden" id="categoriesCollapsed" name="categoriesCollapsed" value="${fold.categoriesCollapsed}"/>
            
                <table cellspacing="0" cellpadding="0">
                    <c:set var="id" value="categories"/>
                    <c:set var="notSelectedPairs" value="${categories}"/>
                    <c:set var="selectedPairs" value="${searchCategories}"/>
                    <spring:message var="leftColumnTitle" code="admin/search/contains_category" htmlEscape="true"/>
                    <spring:message var="rightColumnTitle" code="admin/search/use" htmlEscape="true"/>
                    <%@ include file="/WEB-INF/jsp/admin/search/select_columns.jspf" %>
                </table>
            </td>
        </tr>
        <tr><td/><td height="20">${hrImage}</td></tr>
        <tr>
            <td valign="middle" style="padding-right:15px;">
                <a id="close_role" href="#role">
                    <spring:message code="admin/search/role" htmlEscape="true"/>
                </a>
            </td>
            <td id="fold_role" style="${fold.roleCollapsed ? 'display:none;' : ''}">
                <input type="hidden" id="roleCollapsed" name="roleCollapsed" value="${fold.roleCollapsed}"/>
            
                <table cellspacing="0" cellpadding="0">
                    <tr>
                        <td>
                            <form:select path="role" cssStyle="width:200px;">
                                <option value="-1" ${search.role eq -1 ? 'selected="selected"' : ''} >
                                    <spring:message code="admin/search/not_selected" htmlEscape="true"/>
                                </option>
                                <form:options items="${roles}" itemValue="id" itemLabel="name" htmlEscape="true"/>
                            </form:select>
                        </td>
                        <td>
                            <form:checkbox id="roleNotOnPage" path="roleNotOnPage"/>
                            <label for="roleNotOnPage">
                                <spring:message code="admin/search/not_on_page" htmlEscape="true"/>
                            </label>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
        <tr><td/><td height="20">${hrImage}</td></tr>
        <tr>
            <td valign="middle" style="padding-right:15px;">
                <a id="close_status" href="#status">
                    <spring:message code="admin/search/status" htmlEscape="true"/>
                </a>
            </td>
            <td id="fold_status" style="${fold.statusCollapsed ? 'display:none;' : ''}">
                <input type="hidden" id="statusCollapsed" name="statusCollapsed" value="${fold.statusCollapsed}"/>
            
                <table cellspacing="0" cellpadding="0">
                    <tr>
                        <td style="padding-right:10px;">
                            <form:checkbox id="statusNew" path="statusNew"/>
                            <label for="statusNew"><spring:message code="admin/search/status/new" htmlEscape="true"/></label>
                        </td>
                        <td style="padding-right:10px;">
                            <form:checkbox id="statusApproved" path="statusApproved"/>
                            <label for="statusApproved"><spring:message code="admin/search/status/approved" htmlEscape="true"/></label>
                        </td>
                        <td style="padding-right:10px;">
                            <form:checkbox id="statusPublished" path="statusPublished"/>
                            <label for="statusPublished"><spring:message code="admin/search/status/published" htmlEscape="true"/></label>
                        </td>
                        <td style="padding-right:10px;">
                            <form:checkbox id="statusArchived" path="statusArchived"/>
                            <label for="statusArchived"><spring:message code="admin/search/status/archived" htmlEscape="true"/></label>
                        </td>
                        <td style="padding-right:10px;">
                            <form:checkbox id="statusExpired" path="statusExpired"/>
                            <label for="statusExpired"><spring:message code="admin/search/status/expired" htmlEscape="true"/></label>
                        </td>
                        <td style="padding-right:10px;">
                            <form:checkbox id="statusPublishingDenied" path="statusPublishingDenied"/>
                            <label for="statusPublishingDenied"><spring:message code="admin/search/status/publishing_denied" htmlEscape="true"/></label>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
        <tr><td/><td height="20">${hrImage}</td></tr>
        <tr>
            <td valign="middle" style="padding-right:15px;">
                <a id="close_dates" href="#dates">
                    <spring:message code="admin/search/dates" htmlEscape="true"/>
                </a>
            </td>
            <td id="fold_dates" style="${fold.datesCollapsed ? 'display:none;' : ''}">
                <input type="hidden" id="datesCollapsed" name="datesCollapsed" value="${fold.datesCollapsed}"/>
                <spring:message var="datePattern" code="admin/search/js_preset_date_pattern" htmlEscape="true"/>
                <input type="hidden" id="datePattern" value="${datePattern}"/>
                
                <table cellspacing="5" cellpadding="0" width="100%">
                    <tr>
                        <td></td>
                        <td><label><spring:message code="admin/search/preset_dates" htmlEscape="true"/></label></td>
                        <td><label><spring:message code="admin/search/preset_date/from" htmlEscape="true"/></label></td>
                        <td><label><spring:message code="admin/search/preset_date/to" htmlEscape="true"/></label></td>
                    </tr>
                    
                    <c:set var="id" value="creationRange"/>
                    <c:set var="dateRange" value="${search.creationRange}"/>
                    <c:set var="dateRangeName" value="Creation date"/>
                    <%@ include file="/WEB-INF/jsp/admin/search/date_range.jspf" %>
                    
                    <c:set var="id" value="changeRange"/>
                    <c:set var="dateRange" value="${search.changeRange}"/>
                    <c:set var="dateRangeName" value="Change date"/>
                    <%@ include file="/WEB-INF/jsp/admin/search/date_range.jspf" %>
                    
                    <c:set var="id" value="publishingRange"/>
                    <c:set var="dateRange" value="${search.publishingRange}"/>
                    <c:set var="dateRangeName" value="Publishing date"/>
                    <%@ include file="/WEB-INF/jsp/admin/search/date_range.jspf" %>
                    
                    <c:set var="id" value="archivedRange"/>
                    <c:set var="dateRange" value="${search.archivedRange}"/>
                    <c:set var="dateRangeName" value="Archived date"/>
                    <%@ include file="/WEB-INF/jsp/admin/search/date_range.jspf" %>
                    
                    <c:set var="id" value="expiredRange"/>
                    <c:set var="dateRange" value="${search.expiredRange}"/>
                    <c:set var="dateRangeName" value="Expired date"/>
                    <%@ include file="/WEB-INF/jsp/admin/search/date_range.jspf" %>
                </table>
            </td>
        </tr>
        <tr><td colspan="2" height="20">${hrImage}</td></tr>
        <tr>
            <td valign="middle" style="padding-right:15px;">
                <a id="close_misc" href="#misc">
                    <spring:message code="admin/search/misc" htmlEscape="true"/>
                </a>
            </td>
            <td id="fold_misc" style="${fold.miscCollapsed ? 'display:none;' : ''}">
                <input type="hidden" id="miscCollapsed" name="miscCollapsed" value="${fold.miscCollapsed}"/>
            
                <table cellspacing="0" cellpadding="0" width="100%">
                    <tr>
                        <td>
                            <form:checkbox id="parents" path="parents"/>
                            <label for="parents" style="margin-right:10px;">
                                <spring:message code="admin/search/has_parents" htmlEscape="true"/>
                            </label>
    
                            <form:checkbox id="noParents" path="noParents"/>
                            <label for="noParents" style="margin-right:10px;">
                                <spring:message code="admin/search/no_parents" htmlEscape="true"/>
                            </label>
    
                            <form:checkbox id="children" path="children"/>
                            <label for="children" style="margin-right:10px;">
                                <spring:message code="admin/search/has_children" htmlEscape="true"/>
                            </label>
    
                            <form:checkbox id="noChildren" path="noChildren"/>
                            <label for="noChildren">
                                <spring:message code="admin/search/no_children" htmlEscape="true"/>
                            </label>
                        </td>
                    </tr>
                    <tr><td height="20">${hrImage}</td></tr>
                    <tr>
                        <td>
                            <table cellspacing="0" cellpadding="0" width="100%"> 
                                <tr>
                                    <td>
                                        <label for="metaRangeFrom">
                                            <spring:message code="admin/search/meta_range/from" htmlEscape="true"/>
                                        </label>
                                        <form:input id="metaRangeFrom" path="metaRange.from" cssStyle="width:40px;"/>
                                    </td>
                                    <td>
                                        <label for="metaRangeTo">
                                            <spring:message code="admin/search/meta_range/to" htmlEscape="true"/>
                                        </label>
                                        <form:input id="metaRangeTo" path="metaRange.to" cssStyle="width:40px;"/>
                                    </td>
                                    <td>
                                        <label for="text" style="margin-right:10px;">
                                            <spring:message code="admin/search/text" htmlEscape="true"/>
                                        </label>
                                    </td>
                                    <td><form:input id="text" path="text" maxlength="255" cssStyle="width:200px;"/></td>
                                </tr>
                                <tr>
                                    <td colspan="4" height="20">${hrImage}</td>
                                </tr>
                                <tr>
                                    <td colspan="3" align="right">
                                        <label for="alias" style="margin-right:10px;">
                                            <spring:message code="admin/search/contains_in_alias" htmlEscape="true"/>:
                                        </label>
                                    </td>
                                    <td><form:input id="alias" path="alias" maxlength="255" cssStyle="width:200px;"/></td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
        <tr><td/><td height="20">${hrImage}</td></tr>
        <tr>
            <td></td>
            <td>
                <table cellspacing="0" cellpadding="0" width="100%">
                    <tr>
                        <td>
                            <spring:message var="clearText" code="admin/search/clear" htmlEscape="true"/>
                            <input id="clear" type="button" name="clearAction" value="${clearText}" class="imcmsFormBtn"/>
                        </td>
                        <td align="right">
                            <form:radiobutton id="searchType" path="searchType" value="<%= SearchType.SEARCH %>"/>
                            <label for="searchType">
                                <spring:message code="admin/search/search_type/search" htmlEscape="true"/>
                            </label>
    
                            <form:radiobutton id="addToSelection" path="searchType" value="<%= SearchType.ADD_TO_SELECTION %>"/>
                            <label for="addToSelection">
                                <spring:message code="admin/search/search_type/add_to_selection" htmlEscape="true"/>
                            </label>
    
                            <form:radiobutton id="searchInSelection" path="searchType" value="<%= SearchType.SEARCH_IN_SELECTION %>"/>
                            <label for="searchInSelection" style="margin-right:15px;">
                                <spring:message code="admin/search/search_type/search_in_selection" htmlEscape="true"/>
                            </label>
    
                            <spring:message var="searchText" code="admin/search/search" htmlEscape="true"/>
                            <input id="search" type="submit" name="searchAction" value="${searchText}" class="imcmsFormBtn"/>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
        <tr><td colspan="2" height="30"/></tr>
        
        <%@ include file="/WEB-INF/jsp/admin/search/search_results.jsp" %>
        
    </table>
</form:form>
