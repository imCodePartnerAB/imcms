<%@ page import="com.imcode.imcms.*"%>
<%
    ImcmsSystem imcmsSystem = (ImcmsSystem)request.getAttribute( RequestConstants.SYSTEM );
    DocumentMapperBean documentMapper = imcmsSystem.getDocumentMapper();
    int metaId = 1001 ;
    TextDocumentBean document = (TextDocumentBean)documentMapper.getDocument(metaId) ;
    TextDocumentBean.TextField textField = document.getTextField(1) ;
    out.println(textField.getHtmlFormattedText()) ;
%>