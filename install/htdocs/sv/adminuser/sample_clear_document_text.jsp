<%@ page import="com.imcode.imcms.DocumentMapperBean,
                 com.imcode.imcms.TextDocumentBean,
                 com.imcode.imcms.WebAppConstants,
                 com.imcode.imcms.DocumentBean"%>


<%
    DocumentMapperBean documentMapper = (DocumentMapperBean)request.getAttribute(WebAppConstants.DOCUMENT_MAPPER_ATTRIBUTE_NAME) ;
    int metaId = 1001 ;
    TextDocumentBean document = (TextDocumentBean)documentMapper.getDocument(metaId) ;

    int textFieldIndexInDocument = 1;
    String newHtmlText = "";
    document.setHtmlTextField( textFieldIndexInDocument, newHtmlText ) ;

    textFieldIndexInDocument = 2;
    String newPlainText = "";
    document.setPlainTextField( textFieldIndexInDocument, newPlainText ) ;
%>

<p>
This page has now changed the first and second fileds in the page <%=metaId%>.<br>
You can watch the changes by clicking on this link <a href="../servlet/GetDoc?meta_id=1001">document <%=metaId%></a>
</p>