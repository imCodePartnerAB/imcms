<%@ page import="com.imcode.imcms.api.*,
                 imcode.server.document.textdocument.TextDomainObject,
                 java.util.*,
                 org.apache.commons.lang.StringEscapeUtils" errorPage="error.jsp" %><%!
    int documentId = 1001 ;
    int textFieldIndex = 1 ;
%>
<html>
<body>
<h1>One text field</h1>
<h2>Text field <%= textFieldIndex %> in document <%= documentId %></h2>
<%
    ContentManagementSystem imcmsSystem = ContentManagementSystem.fromRequest( request );
    DocumentService documentService = imcmsSystem.getDocumentService();
    TextDocument document = documentService.getTextDocument(documentId) ;
    TextDocument.TextField textField = document.getTextField(textFieldIndex) ;
%><p><tt><%= StringEscapeUtils.escapeHtml( textField.getText() ) %></tt></p>
<p>which renders as</p>
<div style="border: 1px solid black;">
<%=textField.getHtmlFormattedText()%>
</div>
<h1>All the text fields used in the document</h1>
<%
    SortedMap texts = document.getTextFields();
    for (Iterator textFieldsEntries = texts.entrySet().iterator(); textFieldsEntries.hasNext();) {
        Map.Entry entry = (Map.Entry) textFieldsEntries.next();
        Integer index = (Integer) entry.getKey();
        TextDocument.TextField tempTextField = (TextDocument.TextField) entry.getValue();
        %><h2>Text field <%=index%></h2><p><tt><%=StringEscapeUtils.escapeHtml( tempTextField.getText() ) %></tt></p><%
    }
%>
</body>
</html>
