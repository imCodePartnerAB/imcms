<%@ page import="com.imcode.imcms.*"%>

<%!
    int metaId = 1001 ;
%>

The first field in document <%= metaId %> has content:<br>
<%
    ContentManagementSystem imcmsSystem = (ContentManagementSystem)request.getAttribute( RequestConstants.SYSTEM );
    DocumentService documentMapper = imcmsSystem.getDocumentMapper();
    TextDocumentBean document = (TextDocumentBean)documentMapper.getDocument(metaId) ;
    TextDocumentBean.TextField textField = document.getTextField(2) ;
    out.println(textField.getHtmlFormattedText()) ;
%>
// end content