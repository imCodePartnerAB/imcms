<%@ page import="com.imcode.imcms.api.*,
                 imcode.server.user.UserDomainObject" errorPage="error.jsp" %>
<%!
    int documentId = 1001 ;
    int imageIndex = 2 ;
    String image_src = "imCMSpower.gif";
    String image_name = "";
    int width = 0;
    int heigth = 0;
    int border = 0;
    int v_space = 0;
    int h_space = 0;
    String link_target = "_top";
    String link_targetname = "";
    String link_href = "";
    String align = "top";
    String alt_text = "This is altText";
    String low_src = "";


%>

<html>
<body>
Image <%= imageIndex %> in document <%= documentId %> has content:<br>
<%
    ContentManagementSystem imcmsSystem = (ContentManagementSystem)request.getAttribute( RequestConstants.SYSTEM );
    DocumentService documentService = imcmsSystem.getDocumentService();
    TextDocument document = documentService.getTextDocument(documentId) ;
    document.setImage(imageIndex, image_src, image_name, width, heigth, 
                        border, v_space, h_space, align,
                        link_target, link_targetname, link_href,
                         alt_text, low_src );

    Image image = document.getImage(imageIndex) ;
%>
imageRef = <%=image.getSrc()%><br>
imageHeigth = <%=image.getHeight()%><br>
imageWidth = <%=image.getWidth()%><br>
imageAltText = <%=image.getAltText()%><br>
imageSrcUrl = <%=image.getSrcUrl()%><br>
<%
%>
</body>
</html>

