<%@ page import="com.imcode.imcms.api.*,
                 java.util.SortedMap,
                 java.util.Iterator,
                 java.util.Map" errorPage="error.jsp" %>

<%!
    int documentId = 1001 ;
    int includeIndex = 1 ;
%>

Include number <%= includeIndex %> in document <%= documentId %> has the content:<br>
<%
    ContentManagementSystem imcmsSystem = (ContentManagementSystem)request.getAttribute( RequestConstants.SYSTEM );
    DocumentService documentService = imcmsSystem.getDocumentService();
    TextDocument document = documentService.getTextDocument(documentId) ;
    Document includedDocument = document.getInclude(includeIndex) ;
    if (null != includedDocument) {
    %>Document <%= includedDocument.getId() %> which has the headline <%= includedDocument.getHeadline() %><%
    } else {
    %>No include <%= includeIndex %> in document <%= documentId %><%
    }
%>

<br><br>
All the includes in the document:
<%
    SortedMap includes = document.getIncludes();
    for (Iterator includeEntries = includes.entrySet().iterator(); includeEntries.hasNext();) {
        Map.Entry entry = (Map.Entry) includeEntries.next();
        Integer index = (Integer) entry.getKey();
        Document tempIncludedDocument = (Document) entry.getValue();
        %><p>Include <%=index%> points to the document with id:<br> <%=tempIncludedDocument.getId()%></p><%
    }
%>

// end content
