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
<% if( null != image ) { %>
    imageSrc = <%=image.getSrc()%><br>
    imageHeigth = <%=image.getHeight()%><br>
    imageWidth = <%=image.getWidth()%><br>
    imageAltText = <%=image.getAltText()%><br>
    imageLinkTarget = <%=image.getLinkTarget()%><br>
    imageLinkHref = <%=image.getLinkHref()%><br>
    imageSrcUrl = <%=image.getSrcUrl()%><br>
<%} else { %>
    <%= "No image found at index " + imageIndex + " in document with id " + documentId %>
<%}%>

<br><br>
All images used in the document:
<%
    SortedMap images = document.getImages();
    for (Iterator imagesEntries = images.entrySet().iterator(); imagesEntries.hasNext();) {
        Map.Entry entry = (Map.Entry) imagesEntries.next();
        Integer index = (Integer) entry.getKey();
        Image tempImage = (Image) entry.getValue();
        %><p>Image <%=index%>'s source is :<br> <%=tempImage.getSrc()%></p><%
    }
%>
</body>
</html>
