<%@ page import="com.imcode.imcms.*"%>

<%!
    int documentId = 1001 ;
%>

The first field in document <%= documentId %> has content:<br>
<%
    ContentManagementSystem imcmsSystem = (ContentManagementSystem)request.getAttribute( RequestConstants.SYSTEM );
    DocumentService documentService = imcmsSystem.getDocumentService();
    TextDocument document = (TextDocument)documentService.getTextDocument(documentId) ;
    TextDocument.TextField textField = document.getTextField(2) ;
    out.println(textField.getHtmlFormattedText()) ;
%>
// end content