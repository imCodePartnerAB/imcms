<%@ page import="com.imcode.imcms.*"%>


<%
    ContentManagementSystem imcmsSystem = (ContentManagementSystem)request.getAttribute( RequestConstants.SYSTEM );
    DocumentService documentMapper = imcmsSystem.getDocumentService();
    int documentId = 1001 ;
    TextDocument document = (TextDocument)documentMapper.getDocument(documentId) ;

    int textFieldIndexInDocument = 1;
    String newHtmlText = "";
    document.setHtmlTextField( textFieldIndexInDocument, newHtmlText ) ;

    textFieldIndexInDocument = 2;
    String newPlainText = "";
    document.setPlainTextField( textFieldIndexInDocument, newPlainText ) ;
%>

<p>
This page has now changed the first and second fileds in the page <%=documentId%>.<br>
You can watch the changes by clicking on this link <a href="../servlet/GetDoc?meta_id=1001">document <%=documentId%></a>
</p>