<%@ page import="com.imcode.imcms.api.*" errorPage="error.jsp" %>

<%
    ContentManagementSystem imcmsSystem = ContentManagementSystem.fromRequest( request );
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
Status: The document is <% if (Document.STATUS_PUBLICATION_APPROVED != document.getStatus()) {%>not<%}%> approved for publication.
Created: <%=document.getCreatedDatetime()%>
Modified: <%=document.getModifiedDatetime()%>
Publication start datetime: <%=document.getPublicationStartDatetime()%>
Publication start datetime: <%=document.getPublicationStartDatetime()%>
Archived datetime: <%=document.getArchivedDatetime()%>
Publication end datetime: <%=document.getPublicationEndDatetime()%>
Visible in menu for unauthorized users: <%= document.isVisibleInMenusForUnauthorizedUsers() %>
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
