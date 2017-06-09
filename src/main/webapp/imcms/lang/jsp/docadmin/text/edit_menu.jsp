<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page

		import="imcode.server.document.textdocument.TextDocumentDomainObject,
				imcode.server.parser.ParserParameters,
				imcode.server.user.UserDomainObject"

		pageEncoding="UTF-8"

%>
<%

	ParserParameters parserParameters = ParserParameters.fromRequest(request);
	TextDocumentDomainObject document = (TextDocumentDomainObject) parserParameters.getDocumentRequest().getDocument();
	Integer menuIndex = (Integer) request.getAttribute("menuIndex");
	String label = (String) request.getAttribute("label");
	Integer defaultUserCount = (Integer) request.getAttribute("defaultUserCount");
	Integer userCount = (Integer) request.getAttribute("userCount");
	String content = (String) request.getAttribute("content");
	UserDomainObject user = parserParameters.getDocumentRequest().getUser();

	int metaId = document.getId();
	String cp = request.getContextPath();
	String lang = user.getLanguageIso639_2();

	pageContext.setAttribute("docId", metaId);
	pageContext.setAttribute("menuNo", menuIndex);
%>

<c:url value='/imcms/docadmin/menu?docId=${docId}&menuNo=${menuNo}"' var="editorUrl"/>

<a href="${editorUrl}" class="imcms_label">
	<%= label %> [<%= defaultUserCount %>/<%= userCount %>]&nbsp;
	<img src="<%= cp %>/imcms/<%= lang %>/images/admin/red.gif" alt="edit menu <%= menuIndex%>" align="bottom"
		 style="border:0 !important;"/></a>
<%= content %>
<%--<a href="${editorUrl}">--%>
<%--<img src="<%= cp %>/imcms/<%= lang %>/images/admin/ico_txt.gif" alt="edit menu <%= menuIndex%>"--%>
<%--style="border:0 !important;"/></a>--%>
