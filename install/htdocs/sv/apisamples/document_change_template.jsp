<%@ page import="com.imcode.imcms.api.*" errorPage="error.jsp" %>
<html>
<head>
<title>Delete a role named "Test role"</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</head>
<body>
<%
    ContentManagementSystem imcmsSystem = (ContentManagementSystem)request.getAttribute( RequestConstants.SYSTEM );
    DocumentService documentService = imcmsSystem.getDocumentService();
    TemplateService templateService = imcmsSystem.getTemplateService();
    int documentId = 1001 ;
    TextDocument document = documentService.getTextDocument(documentId) ;
    Template currentTemplate = document.getTemplate();
    Template[] templates = templateService.getPossibleTemplates( document );

    // take the first template that's not current
    Template newTemplate = null;
    int i = 0;
    while( newTemplate == null ){
        if( !templates[i].equals(currentTemplate) ) {
            newTemplate = templates[i];
        }
        i++;
    }

    if( newTemplate !=null ){
        document.setTemplate( newTemplate );
    }
    // don't forget to save your changes!
    documentService.saveChanges( document );
%>
<h3>Changing template on document <%=document.getId()%></h3>
The template the document has assigned, <br>
Before: <%= currentTemplate.toString()%>
After: <%= document.getTemplate().toString() %>
</body>
</html>