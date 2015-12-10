<%@ page import="com.imcode.imcms.api.ContentManagementSystem" %>
<%@ page import="com.imcode.imcms.api.Document" %>
<%@ page import="com.imcode.imcms.api.DocumentService" %>
<%@ page import="com.imcode.imcms.api.TextDocument" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.SortedMap" %>
<%@ page errorPage="error.jsp" %>

<%!
	int documentId = 1001;
	int includeIndex = 1;
%>
<html>
<body>
<p>Include number <%= includeIndex %> in document <%= documentId %> has the content:</p>
<%
	ContentManagementSystem imcmsSystem = ContentManagementSystem.fromRequest(request);
	DocumentService documentService = imcmsSystem.getDocumentService();
	TextDocument document = documentService.getTextDocument(documentId);
	Document includedDocument = document.getInclude(includeIndex);
	if (null != includedDocument) {
%><p>Document <%= includedDocument.getId() %> which has the headline <%= includedDocument.getHeadline() %>
</p><%
} else {
%><p>No include <%= includeIndex %> in document <%= documentId %>
</p><%
	}
%>
<%
	SortedMap<Integer, Document> includes = document.getIncludes();
	if (!includes.isEmpty()) {
%>All the includes in the document:
<ul><%
	for (Map.Entry<Integer, Document> entry : includes.entrySet()) {
		Integer index = entry.getKey();
		Document tempIncludedDocument = entry.getValue();
%>
	<li>Include <%=index%> points to the document with id <%=tempIncludedDocument.getId()%>
	</li>
	<%
		}
	%></ul>
<%
} else {
%><p>No includes in the document.</p><%
	}
%>
</body>
</html>
