<%@ page import="com.imcode.imcms.api.*" errorPage="error.jsp" %>

<%
    ContentManagementSystem imcmsSystem = (ContentManagementSystem)request.getAttribute(RequestConstants.SYSTEM);
    DocumentService documentService = imcmsSystem.getDocumentService() ;
    int documentId = 1001 ;
    Document document = documentService.getDocument(documentId) ;
%>
<html>
<body>
<h1>Document information</h1>
<pre>
for document with id <%=documentId%>

Headline: "<%=document.getHeadline()%>"
Menu text: "<%=document.getMenuText()%>"
Menu image url: "<%=document.getMenuImageURL()%>"
Activated date/time: <%=document.getActivatedDatetime()%>
Archived date/Time: <%=document.getArchivedDatetime()%>
Archived flag: <%=document.getArchivedFlag()%>
Sections:
<%
     Section[] sections = document.getSections();
     for (int i = 0; i < sections.length; i++) {
         Section section = sections[i];
         %><%=section.getName()%><br><%
     }
%>
Language: "<%=document.getLanguage()%>"
Categories:
<%
    Category[] categories = document.getCategories();
    for (int i = 0; i < categories.length; i++) {
        Category category = categories[i];
        %><%=category.getName()%><br><%
    }
%>
Creator: <a href="mailto:<%=document.getCreator().getEmailAddress()%>"><%=document.getCreator().getLoginName()%></a>
Publisher:  <%= document.getPublisher() %>

If you know the type of the document, i.e. TextDocument, use the method getTextDocument() to retreive the document:
<%
    TextDocument textDocument = documentService.getTextDocument(documentId);
%>
Template: <%=textDocument.getTemplate()%>

</pre>

</body>
</html>
