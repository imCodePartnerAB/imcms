<%@ page import="com.imcode.imcms.DocumentMapperBean,
                 com.imcode.imcms.TextDocumentBean,
                 com.imcode.imcms.WebAppConstants"%>
<%
    DocumentMapperBean documentMapper = (DocumentMapperBean)request.getAttribute(WebAppConstants.DOCUMENT_MAPPER_ATTRIBUTE_NAME) ;
    int metaId = 1001 ;
    TextDocumentBean document = documentMapper.getDocument(metaId) ;
    TextDocumentBean.TextField textField = document.getTextField(1) ;
    out.println(textField.getHtmlFormattedText()) ;
%>