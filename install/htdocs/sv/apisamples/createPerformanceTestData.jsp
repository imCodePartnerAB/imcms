<%@ page import="com.imcode.imcms.api.ContentManagementSystem,
                 com.imcode.imcms.api.RequestConstants,
                 com.imcode.imcms.api.DocumentService,
                 com.imcode.imcms.api.TextDocument"%>
<%
    String parentIdParamName = "parentId";
    String parentIdStr = request.getParameter(parentIdParamName);
    if( null != parentIdStr ) {
        int parentId = Integer.parseInt(parentIdStr);

        ContentManagementSystem imcmsSystem = (ContentManagementSystem)request.getAttribute(RequestConstants.SYSTEM);
        DocumentService documentService = imcmsSystem.getDocumentService() ;

        TextDocument helloWorldTextDocument = documentService.createNewTextDocument( parentId, 1 );
        helloWorldTextDocument.setHeadline("\'Hello World\' document");
        documentService.saveChanges(helloWorldTextDocument);

        TextDocument textDocumentWithSomeTexts = documentService.createNewTextDocument(parentId,1);
        textDocumentWithSomeTexts.setHeadline("Text document with some texts");
        textDocumentWithSomeTexts.setPlainTextField(1,"Einstein: A man's ethical behavior should be based effectually on sympathy, education, and social ties; no religious basis is necessary. Man would indeed be in a poor way if he had to be restrained by fear of punishment and hope of reward after death." );
        textDocumentWithSomeTexts.setPlainTextField(2,"Einstein: Everything should be made as simple as possible, but not simpler." );
        documentService.saveChanges(textDocumentWithSomeTexts);

        TextDocument oneMoreTextDocumentWithSomeOtherTexts = documentService.createNewTextDocument(parentId,1);
        oneMoreTextDocumentWithSomeOtherTexts.setHeadline("Another text document with some texts");
        oneMoreTextDocumentWithSomeOtherTexts.setPlainTextField(1,"Einstein: If A equals success, then the formula is: A = X + Y + Z, X is work. Y is play. Z is keep your mouth shut." );
        oneMoreTextDocumentWithSomeOtherTexts.setPlainTextField(2,"Einstein: Gravitation is not responsible for people falling in love." );
        documentService.saveChanges(oneMoreTextDocumentWithSomeOtherTexts);

        TextDocument textDocumentWithOneInclude = documentService.createNewTextDocument( parentId, 1 );
        textDocumentWithOneInclude.setHeadline("Text document with one include");
        textDocumentWithOneInclude.setInclude( 1, textDocumentWithSomeTexts );
        documentService.saveChanges(textDocumentWithOneInclude);

        TextDocument textDocumentWithTreeIncludes = documentService.createNewTextDocument( parentId, 1 );
        textDocumentWithTreeIncludes.setHeadline("Text document with tree includes");
        textDocumentWithTreeIncludes.setInclude(1, helloWorldTextDocument );
        textDocumentWithTreeIncludes.setInclude(2, textDocumentWithSomeTexts );
        textDocumentWithTreeIncludes.setInclude(3, oneMoreTextDocumentWithSomeOtherTexts );
        documentService.saveChanges(textDocumentWithTreeIncludes);
        %>
            Created the following documents:<br>
            'HelloWorld' document id = <%=helloWorldTextDocument.getId()%><br>
            Text document with some texts id = <%=textDocumentWithSomeTexts.getId()%><br>
            One more text document with some other texts id = <%=oneMoreTextDocumentWithSomeOtherTexts.getId()%><br>
            Text document with one include id = <%=textDocumentWithOneInclude.getId()%><br>
            Text document with tree includes id = <%=textDocumentWithTreeIncludes.getId()%><br>
         <%
    } else {
        %>
        <form action="createPerformanceTestData.jsp" method="get">
            <input name="parentId" type="text" value="1001">
            <input name="" type="submit">
        </form>
        <%
    }
%>
