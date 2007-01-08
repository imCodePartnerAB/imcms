<%@ page import="com.imcode.imcms.api.*,
                 javax.activation.DataSource,
                 java.io.*,
                 java.util.Date" errorPage="error.jsp" %>

<%
    ContentManagementSystem imcmsSystem = ContentManagementSystem.fromRequest( request );
    DocumentService documentService = imcmsSystem.getDocumentService() ;

    int parentId = 1001;
    int menuIndex = 1;
    TextDocument parentDocument = documentService.getTextDocument( parentId );

    TextDocument textDocument = documentService.createNewTextDocument( parentDocument ) ;
    textDocument.setHeadline( "Textdocument created from API" );
    textDocument.setPlainTextField( 1, "Test text field" );
    textDocument.setPublicationStartDatetime( new Date() );
    textDocument.setPublicationStatus(Document.PublicationStatus.APPROVED);
    documentService.saveChanges( textDocument );
    parentDocument.getMenu( menuIndex ).addDocument( textDocument );
    documentService.saveChanges( parentDocument );
%>
Created a text document with id "<a href="<%= request.getContextPath() %>/servlet/GetDoc?meta_id=<%= textDocument.getId() %>"><%= textDocument.getId() %></a>"
with link from the document with id "<a href="<%= request.getContextPath() %>/servlet/GetDoc?meta_id=<%= parentId %>"><%= parentId %></a>".<br>

<%
    UrlDocument urlDocument = documentService.createNewUrlDocument( parentDocument );
    urlDocument.setHeadline( "URL-document created from API" );
    urlDocument.setUrl( "www.imcode.com" );
    urlDocument.setPublicationStartDatetime( new Date() );
    urlDocument.setPublicationStatus(Document.PublicationStatus.APPROVED);
    documentService.saveChanges( urlDocument );

    parentDocument.getMenu( menuIndex ).addDocument( urlDocument );
    documentService.saveChanges( parentDocument );
%>

Created an url document with id "<a href="<%= request.getContextPath() %>/servlet/GetDoc?meta_id=<%= urlDocument.getId() %>"><%= urlDocument.getId() %></a>"
with link from the document with id "<a href="<%= request.getContextPath() %>/servlet/GetDoc?meta_id=<%= parentId %>"><%= parentId %></a>".<br>

<%
    FileDocument fileDocument = documentService.createNewFileDocument( parentDocument ) ;
    fileDocument.setHeadline( "Filedocument created from API");
    //FileDocument.FileDocumentFile file = new FileDocument.FileDocumentFile( new FileDataSource("file.txt"));
    FileDocument.FileDocumentFile file = new FileDocument.FileDocumentFile( new StringDataSource("This is the contents of the file."));
    fileDocument.addFile( "text", file );
    fileDocument.setPublicationStartDatetime( new Date() );
    fileDocument.setPublicationStatus(Document.PublicationStatus.APPROVED);
    documentService.saveChanges( fileDocument );

    parentDocument.getMenu( menuIndex ).addDocument( fileDocument );
    documentService.saveChanges( parentDocument );
%>
Created a file document with id "<a href="<%= request.getContextPath() %>/servlet/GetDoc?meta_id=<%= fileDocument.getId() %>"><%= fileDocument.getId() %></a>"
with link from the document with id "<a href="<%= request.getContextPath() %>/servlet/GetDoc?meta_id=<%= parentId %>"><%= parentId %></a>".<br>

<%!
    private static class StringDataSource implements DataSource {

        private String string;
        private byte[] buffer ;

        StringDataSource( String string ) {
            this.string = string;
        }

        private void createBuffer() throws IOException {
            if (null == buffer) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                Writer writer =  new OutputStreamWriter( stream );
                writer.write( string );
                writer.close();
                buffer = stream.toByteArray() ;
            }
        }

        public InputStream getInputStream() throws IOException {
            createBuffer();
            return new ByteArrayInputStream( buffer ) ;
        }

        public OutputStream getOutputStream() throws IOException {
            return null;
        }

        public String getContentType() {
            return "text/plain" ;
        }

        public String getName() {
            return "string.txt" ;
        }

    }
%>
