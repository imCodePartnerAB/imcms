<%@ page import="com.imcode.imcms.api.*,
                 imcode.server.user.UserDomainObject" errorPage="error.jsp" %>
<%
    int documentId = 1001 ;
    int imageIndex = 2 ;

    Image image = new Image();
    //image.setSrc( "1002" );
    image.setSrc( "imCMSpower.gif" );
    image.setAltText( "Powered by imCMS!" );
%>

<html>
<body>
Image <%= imageIndex %> in document <%= documentId %> has content:<br>
<%
    ContentManagementSystem imcmsSystem = ContentManagementSystem.fromRequest( request );
    DocumentService documentService = imcmsSystem.getDocumentService();
    TextDocument document = documentService.getTextDocument(documentId) ;
    document.setImage( imageIndex, image );

    // Don't forget to save changes!
    documentService.saveChanges( document );
%>
imageSrc = <%=image.getSrc(request.getContextPath())%><br>
imageHeigth = <%=image.getHeight()%><br>
imageWidth = <%=image.getWidth()%><br>
imageAltText = <%=image.getAltText()%><br>
imageSize = <%=image.getSize()%><br>
</body>
</html>

