<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="im" uri="imcms" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
<c:set var="imagesPath" value="${contextPath}/imcms/${locale.ISO3Language}/images/admin"/>
<c:choose>
    <c:when test="${search.search}">
        <tr id="searchResults">
            <td colspan="2">
                <span class="imcmsAdmHeading">
                    <spring:message code="admin/search/search_results" htmlEscape="true"/>
                </span>
            </td>
        </tr>
        <tr>
            <td colspan="2"><img src="${imagesPath}/1x1_20568d.gif" width="100%" height="1" vspace="8"></td>
        </tr>
        <tr>
            <td colspan="2" class="imcmsAdmText">
                <b><spring:message code="admin/search/found" htmlEscape="true"/>:</b>
                &nbsp;&nbsp;${fn:length(documents)}
            </td>
        </tr>
        <tr>
            <td colspan="2"><img src="${imagesPath}/1x1.gif" width="1" height="15"></td>
        </tr>
        <c:if test="${not empty documents}">
            <tr>
                <td colspan="2">
                    <table cellspacing="5" cellpadding="0" width="100%">
                        <tr>
                            <td class="imcmsAdmText">
                                <b><spring:message code="global/page_alias" htmlEscape="true"/>&nbsp;</b>
                            </td>
                            <td width="50" class="imcmsAdmText">
                                <b><spring:message code="global/heading_status" htmlEscape="true"/>&nbsp;</b>
                            </td>
                            <td width="40" class="imcmsAdmText">
                                <b><spring:message code="admin/search/document_id" htmlEscape="true"/></b>
                            </td>
                            <td width="50" class="imcmsAdmText">
                                <b><spring:message code="admin/search/document_type" htmlEscape="true"/></b>
                            </td>
                            <td class="imcmsAdmText">
                                <b><spring:message code="admin/search/document_headline" htmlEscape="true"/></b>
                            </td>
                        </tr>
                        <tr>
                            <td colspan="5"><img src="${imagesPath}/1x1_cccccc.gif" width="100%" height="1"></td>
                        </tr>
                        <c:forEach var="document" items="${documents}" varStatus="status">
                            <tr valign="top" style="${status.index % 2 eq 0 ? 'background-color:#fff;' : ''}">
                                <td>
                                    <c:if test="${document.alias ne null}">
                                        <c:url var="aliasUrl" value="/${document.alias}"/>
                                        
                                        <a href="${fn:escapeXml(aliasUrl)}" title="${fn:escapeXml(document.alias)}">
                                            <c:out value="${document.alias}"/>
                                        </a>
                                    </c:if>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${im:canEditDocumentInformation(document, pageContext)}">
                                            <c:url var="editUrl" value="/newadmin/search/edit">
                                                <c:param name="meta_id" value="${document.id}"/>
                                            </c:url>
                                            <a href="${fn:escapeXml(editUrl)}">
                                                <im:statusIcon document="${document}"/>
                                            </a>
                                        </c:when>
                                        <c:otherwise>
                                            <im:statusIcon document="${document}"/>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${im:canEditDocument(document, pageContext)}">
                                            <c:url var="editUrl" value="/servlet/AdminDoc">
                                                <c:param name="meta_id" value="${document.id}"/>
                                            </c:url>
                                            <a href="${fn:escapeXml(editUrl)}"><c:out value="${document.id}"/></a>
                                        </c:when>
                                        <c:otherwise><c:out value="${document.id}"/></c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <spring:message code="${document.documentType.name.languageKey}" htmlEscape="true"/>
                                </td>
                                <td>
                                    <c:url var="docUrl" value="/${document.alias ne null ? document.alias : document.id}"/>
                                    <a href="${fn:escapeXml(docUrl)}" target="${document.target}">
                                        <c:out value="${document.headline}"/>
                                    </a>
                                </td>
                            </tr>
                        </c:forEach>
                    </table>
                </td>
            </tr>
        </c:if>
    </c:when>
    <c:otherwise>
        <tr id="searchResults">
            <td colspan="2"></td>
        </tr>
    </c:otherwise>
</c:choose>
