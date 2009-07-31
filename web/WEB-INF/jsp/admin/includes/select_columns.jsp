<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/admin/includes/taglibs.jsp"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<tr>
    <td>
        <spring:message code="${unselectedColumnTitle}" /><br/>
        <select id="unselected_${id}" size="10" style="width:200px;" multiple="multiple">
            <c:forEach items="${notSelectedPairs}" var="pair" >
                <option value="${pair.value}" title="${fn:escapeXml(pair.name)}">
                    <spring:message code="${pair.name}"/>
                </option>
            </c:forEach>
        </select>
    </td>

    <td align="center" width="70">
        <input type="button" id="add_${id}" value="&raquo;" class="imcmsFormBtn" style="width:30px;"/><br/><br/>
        <input type="button" id="remove_${id}" value="&laquo;" class="imcmsFormBtn" style="width:30px;"/>
    </td>

    <td>
        <spring:message code="${selectedColumnTitle}"/> <br/>
        <select id="selected_${id}" size="10" style="width:200px;" multiple="multiple">
            <c:forEach items="${selectedPairs}" var="pair">
                <option value="${pair.value}" title="${fn:escapeXml(pair.name)}">
                    <spring:message code="${pair.name}" />
                </option>
            </c:forEach>
        </select>

        <c:forEach var="pair" items="${selectedPairs}">
            <input type="hidden" id="${id}_${pair.value}" name="${id}" value="${pair.value}"/>
        </c:forEach>
    </td>
</tr>