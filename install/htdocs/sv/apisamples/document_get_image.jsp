<%@ page import="com.imcode.imcms.api.*" errorPage="error.jsp" %>
<%!
    int documentId = 1001 ;
    int imageIndex = 2 ;
%>

<html>
<body>
Image <%= imageIndex %> in document <%= documentId %> has content:<br>
<%
    ContentManagementSystem imcmsSystem = (ContentManagementSystem)request.getAttribute( RequestConstants.SYSTEM );
    DocumentService documentService = imcmsSystem.getDocumentService();
    TextDocument document = documentService.getTextDocument(documentId) ;
    Image image = document.getImage(imageIndex) ;
%>
imageSrc = <%=image.getSrc()%><br>
imageHeigth = <%=image.getHeight()%><br>
imageWidth = <%=image.getWidth()%><br>
imageAltText = <%=image.getAltText()%><br>
imageLinkTarget = <%=image.getLinkTarget()%><br>
imageLinkHref = <%=image.getLinkHref()%><br>
imageSrcUrl = <%=image.getSrcUrl()%><br>
<%
%>
</body>
</html>
