<%@ page import="com.imcode.imcms.api.*" errorPage="error.jsp" %>

<%!
    int documentId = 1001 ;
    int textFieldIndex = 2 ;
%>

Text field <%= textFieldIndex %> in document <%= documentId %> has content:<br>
<%
    ContentManagementSystem imcmsSystem = (ContentManagementSystem)request.getAttribute( RequestConstants.SYSTEM );
    DocumentService documentService = imcmsSystem.getDocumentService();
    TextDocument document = documentService.getTextDocument(documentId) ;
    TextDocument.TextField textField = document.getTextField(textFieldIndex) ;
    out.println(textField.getHtmlFormattedText()) ;
%>
// end content