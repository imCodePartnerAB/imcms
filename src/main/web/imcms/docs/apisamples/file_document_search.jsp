<%@ page import="com.imcode.imcms.api.*,
            java.util.*,
            java.io.IOException,
            org.apache.commons.lang.UnhandledException,
            javax.imageio.ImageIO,
            java.awt.image.BufferedImage"
        contentType="text/html; charset=UTF-8" %>
<html>
    <head>
        <title></title>
    </head>
    <body>
<%
    ContentManagementSystem contentManagementSystem = ContentManagementSystem.fromRequest( request );
    DocumentService documentService = contentManagementSystem.getDocumentService() ;
    Document[] documents = documentService.search( new LuceneParsedQuery( "doc_type_id:"+FileDocument.TYPE_ID )) ;
    for ( int i = 0; i < documents.length; i++ ) {
        FileDocument fileDocument = (FileDocument)documents[i] ;
        FileDocument.FileDocumentFile[] files = fileDocument.getFiles() ;
        Arrays.sort( files, new FileDocumentFileSizeComparator() );
        %>
        <%= fileDocument.getId() %>
        <ul><%
            for ( int j = 0; j < files.length; j++ ) {
                FileDocument.FileDocumentFile file = files[j];
                boolean isDefault = fileDocument.getDefaultFileId().equals( file.getId() ) ;
                BufferedImage image = ImageIO.read(file.getInputStream()) ; %>
                <li>
                    <% if (isDefault) { %><strong><%}%>
                    <%= file.getId() %>:
                    <%= file.getName() %> - <%= file.getContentType() %> - <%= file.getSize() %>
                    <% if (null != image) { %>
                        - Image-size: <%= image.getWidth() %>x<%=image.getHeight()%>
                    <% } %>
                    <% if (isDefault) { %></strong><%}%>
                </li>
            <% } %>
        </ul><%
    } %>
    </body>
</html>
<%!
    private static class FileDocumentFileSizeComparator implements Comparator {
        public int compare( Object o1, Object o2 ) {
            FileDocument.FileDocumentFile f1 = (FileDocument.FileDocumentFile)o1 ;
            FileDocument.FileDocumentFile f2 = (FileDocument.FileDocumentFile)o2 ;
            try {
                return (int)( f1.getSize() - f2.getSize() ) ;
            } catch ( IOException e ) {
                throw new UnhandledException( e ) ;
            }
        }
    }
%>
