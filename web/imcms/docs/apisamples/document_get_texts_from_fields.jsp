<%@ page import="com.imcode.imcms.api.*,
                 imcode.server.document.textdocument.TextDomainObject,
                 java.util.*" errorPage="error.jsp" %>

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
All the text fields used in the document:
<%
    SortedMap texts = document.getTextFields();
    for (Iterator textFieldsEntries = texts.entrySet().iterator(); textFieldsEntries.hasNext();) {
        Map.Entry entry = (Map.Entry) textFieldsEntries.next();
        Integer index = (Integer) entry.getKey();
        TextDocument.TextField tempTextField = (TextDocument.TextField) entry.getValue();
        %><p>Text field <%=index%> has content:<br> <%=tempTextField.getHtmlFormattedText()%></p><%
    }
%>
