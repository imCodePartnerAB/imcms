<%@ page import="com.imcode.imcms.api.*,
                 java.util.SortedMap,
                 java.util.Iterator,
                 java.util.Map" errorPage="error.jsp" %><%!
    int documentId = 1001 ;
    int imageIndex = 3 ;
%><html>
<body>
<%
    ContentManagementSystem imcmsSystem = ContentManagementSystem.fromRequest( request );
    DocumentService documentService = imcmsSystem.getDocumentService();
    TextDocument document = documentService.getTextDocument(documentId) ;
    Image image = document.getImage(imageIndex) ;
    String contextPath = request.getContextPath();

    if( !image.isEmpty() ) { %>
        Image <%= imageIndex %> in document <%= documentId %> has content:<br>
        imageSrc = <%=image.getSrc(contextPath)%><br>
        imageHeigth = <%=image.getHeight()%><br>
        imageWidth = <%=image.getWidth()%><br>
        imageAltText = <%=image.getAltText()%><br>
        imageLinkTarget = <%=image.getLinkTarget()%><br>
        imageLinkHref = <%=image.getLinkHref()%><br>
        imageSize = <%= image.getSize() %><br>
<%}%>

<br><br>
All images used in the document:
<%
    SortedMap images = document.getImages();
    for (Iterator imagesEntries = images.entrySet().iterator(); imagesEntries.hasNext();) {
        Map.Entry entry = (Map.Entry) imagesEntries.next();
        Integer index = (Integer) entry.getKey();
        Image tempImage = (Image) entry.getValue();
        %><p>Image <%=index%>:<br>
            imageSrc = <%=tempImage.getSrc(contextPath)%><br>
            imageHeigth = <%=tempImage.getHeight()%><br>
            imageWidth = <%=tempImage.getWidth()%><br>
            imageAltText = <%=tempImage.getAltText()%><br>
            imageLinkTarget = <%=tempImage.getLinkTarget()%><br>
            imageLinkHref = <%=tempImage.getLinkHref()%><br>
            imageSize = <%= tempImage.getSize() %><br>
        </p><%
    }
%>
</body>
</html>
