<%@ page import="com.imcode.imcms.api.*,
                 java.util.SortedMap,
                 java.util.Iterator,
                 java.util.Map" errorPage="error.jsp" %>
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
<% if( image.isEmpty() ) { %>
    <%= "No image found at index " + imageIndex + " in document with id " + documentId %>
<%} else { %>
    imageSrc = <%=image.getSrc()%><br>
    imageHeigth = <%=image.getHeight()%><br>
    imageWidth = <%=image.getWidth()%><br>
    imageAltText = <%=image.getAltText()%><br>
    imageLinkTarget = <%=image.getLinkTarget()%><br>
    imageLinkHref = <%=image.getLinkHref()%><br>
    imageSrcUrl = <%=image.getSrcUrl()%><br>
<%}%>

<br><br>
All images used in the document:
<%
    SortedMap images = document.getImages();
    for (Iterator imagesEntries = images.entrySet().iterator(); imagesEntries.hasNext();) {
        Map.Entry entry = (Map.Entry) imagesEntries.next();
        Integer index = (Integer) entry.getKey();
        Image tempImage = (Image) entry.getValue();
        %><p>Image <%=index%>:<br> <%=tempImage.getSrc()%>
            imageSrc = <%=tempImage.getSrc()%><br>
            imageHeigth = <%=tempImage.getHeight()%><br>
            imageWidth = <%=tempImage.getWidth()%><br>
            imageAltText = <%=tempImage.getAltText()%><br>
            imageLinkTarget = <%=tempImage.getLinkTarget()%><br>
            imageLinkHref = <%=tempImage.getLinkHref()%><br>
            imageSrcUrl = <%=tempImage.getSrcUrl()%><br>
        </p><%
    }
%>
</body>
</html>
