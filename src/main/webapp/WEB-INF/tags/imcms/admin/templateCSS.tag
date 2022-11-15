<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag body-content="empty" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="imcms" uri="imcms" %>
<%@ tag import="com.imcode.imcms.domain.dto.TemplateCSSVersion" %>


<%@ attribute name="document" required="false" %>

<c:set var="targetDocId" value="${empty document ? currentDocument.id : document}"/>

<c:set var="templateCSS" value="${templateCSSService.get(currentDocument.templateName, TemplateCSSVersion.ACTIVE)}"/>

<style id="templateCSS">${templateCSS}</style>

<%--@elvariable id="currentDocument" type="imcode.server.document.textdocument.TextDocumentDomainObject"--%>
<%--@elvariable id="templateCSSService" type="com.imcode.imcms.domain.service.TemplateCSSService"--%>
<%--@elvariable id="templateCSSService" type="com.imcode.imcms.domain.dto.TemplateCSSVersion"--%>
