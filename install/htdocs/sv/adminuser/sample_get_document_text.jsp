<%@ page import="com.imcode.imcms.DocumentMapperBean,
                 com.imcode.imcms.TextDocumentBean,
                 com.imcode.imcms.WebAppConstants,
                 com.imcode.imcms.DocumentBean"%>
<%
    DocumentMapperBean documentMapper = (DocumentMapperBean)request.getAttribute(WebAppConstants.DOCUMENT_MAPPER_ATTRIBUTE_NAME) ;
    int metaId = 1001 ;
    TextDocumentBean document = (TextDocumentBean)documentMapper.getDocument(metaId) ;
    TextDocumentBean.TextField textField = document.getTextField(1) ;
    out.println(textField.getHtmlFormattedText()) ;
%>