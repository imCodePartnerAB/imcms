<%@ page import="com.imcode.imcms.api.*,
                 java.util.Map,
                 java.util.Iterator,
                 java.util.Collection,
                 imcode.server.document.textdocument.TextDomainObject,
                 java.util.Set" errorPage="error.jsp" %>

<%!
    int documentId = 1001 ;
    int textFieldIndex = 1 ;
%>

Text field <%= textFieldIndex %> in document <%= documentId %> has content:<br>
<%
    ContentManagementSystem imcmsSystem = (ContentManagementSystem)request.getAttribute( RequestConstants.SYSTEM );
    DocumentService documentService = imcmsSystem.getDocumentService();
    TextDocument document = documentService.getTextDocument(documentId) ;
    TextDocument.TextField textField = document.getTextField(textFieldIndex) ;
    out.println(textField.getHtmlFormattedText()) ;
%>

<br><br>
All the text fields content:
<%
    TextDocument.TextField[] texts = document.getTextFields();
    for (int i = 0; i < texts.length; i++) {
       TextDocument.TextField tempTextField = texts[i];
       %><p>Text field <%=tempTextField.getIndex()%> has content:<br> <%=tempTextField.getHtmlFormattedText()%></p><%
    }
%>