<%@ page import="com.imcode.imcms.api.*,
                 java.util.SortedMap,
                 java.util.Iterator,
                 java.util.Map" errorPage="error.jsp" %><%!
    int documentId = 1001 ;
    int includeIndex = 1 ;
%><html>
<body>
<p>Include number <%= includeIndex %> in document <%= documentId %> has the content:</p>
<%
    ContentManagementSystem imcmsSystem = ContentManagementSystem.fromRequest( request );
    DocumentService documentService = imcmsSystem.getDocumentService();
    TextDocument document = documentService.getTextDocument(documentId) ;
    Document includedDocument = document.getInclude(includeIndex) ;
    if (null != includedDocument) {
        %><p>Document <%= includedDocument.getId() %> which has the headline <%= includedDocument.getHeadline() %></p><%
    } else {
    %><p>No include <%= includeIndex %> in document <%= documentId %></p><%
    }
%>
<%
    SortedMap includes = document.getIncludes();
    if (!includes.isEmpty()) {
        %>All the includes in the document:
        <ul><%
        for (Iterator includeEntries = includes.entrySet().iterator(); includeEntries.hasNext();) {
            Map.Entry entry = (Map.Entry) includeEntries.next();
            Integer index = (Integer) entry.getKey();
            Document tempIncludedDocument = (Document) entry.getValue();
            %><li>Include <%=index%> points to the document with id <%=tempIncludedDocument.getId()%></li><%
        }
        %></ul><%
    } else {
        %><p>No includes in the document.</p><%
    }
%>
</body>
</html>
