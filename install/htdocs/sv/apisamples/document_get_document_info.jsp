<%@ page import="com.imcode.imcms.api.*" errorPage="error.jsp" %>

<%
    ContentManagementSystem imcmsSystem = (ContentManagementSystem)request.getAttribute(RequestConstants.SYSTEM);
    DocumentService documentService = imcmsSystem.getDocumentService() ;
    int documentId = 1001 ;
    Document document = documentService.getTextDocument(documentId) ;

    Category[] categories = document.getCategories();

    StringBuffer categoriesBuffer = new StringBuffer();
    for ( int i = 0 ; i < categories.length ; i++ ) {
        if ( 0 != i ) {
            categoriesBuffer.append( ", " );
        }
        Category category = categories[i];
        categoriesBuffer.append('"').append(category).append('"') ;
    }

%>
<html>
<body>
<h1>Document information</h1>
<pre>
Headline: "<%=document.getHeadline()%>"
Menu text: "<%=document.getMenuText()%>"
Menu picture: "<%=document.getMenuImageURL()%>"
Language: "<%=document.getLanguage()%>"
Creator: <a href="mailto:<%=document.getCreator().getEmailAddress()%>"><%=document.getCreator().getLoginName()%></a>
Categories: <%= categoriesBuffer %>
</pre>
</body>
</html>
