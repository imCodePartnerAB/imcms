<%@ page import="com.imcode.imcms.*"%>

<%!
    int metaId = 1001 ;
%>

The first field in document <%= metaId %> has content:<br>
<%
    ContentManagementSystem imcmsSystem = (ContentManagementSystem)request.getAttribute( RequestConstants.SYSTEM );
    DocumentService documentMapper = imcmsSystem.getDocumentService();
    TextDocument document = (TextDocument)documentMapper.getDocument(metaId) ;
    TextDocument.TextField textField = document.getTextField(2) ;
    out.println(textField.getHtmlFormattedText()) ;
%>
// end content