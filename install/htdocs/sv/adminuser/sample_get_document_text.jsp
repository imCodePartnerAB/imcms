<%@ page import="com.imcode.imcms.DocumentMapperBean,
                 com.imcode.imcms.DocumentBean,
                 com.imcode.imcms.WebAppConstants"%>
<%
    DocumentMapperBean documentMapper = (DocumentMapperBean)request.getAttribute(WebAppConstants.DOCUMENT_MAPPER_ATTRIBUTE_NAME) ;
    int metaId = 1001 ;
    DocumentBean document = documentMapper.getDocument(metaId) ;
    DocumentBean.TextField textField = document.getTextField(1) ;
    out.println(textField.getHtmlFormattedText()) ;
%>