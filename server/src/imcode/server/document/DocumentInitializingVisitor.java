package imcode.server.document;

import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.util.FileInputStreamSource;

class DocumentInitializingVisitor extends DocumentVisitor {

    ImcmsServices service = Imcms.getServices();

    public void visitBrowserDocument( BrowserDocumentDomainObject document ) {
        String sqlStr = "SELECT to_meta_id, browser_id FROM browser_docs WHERE meta_id = ?";
        String[][] sqlResult = service.sqlQueryMulti( sqlStr, new String[]{"" + document.getId()} );
        for ( int i = 0; i < sqlResult.length; i++ ) {
            String[] sqlRow = sqlResult[i];
            int toMetaId = Integer.parseInt( sqlRow[0] );
            int browserId = Integer.parseInt( sqlRow[1] );
            BrowserDocumentDomainObject.Browser browser = service.getDocumentMapper().getBrowserById( browserId );
            document.setBrowserDocumentId( browser, toMetaId );
        }
    }

    public void visitFileDocument( FileDocumentDomainObject document ) {
        String[][] sqlResult = service.sqlQueryMulti( "SELECT variant_name, filename, mime, created_as_image, default_variant FROM fileupload_docs WHERE meta_id = ? ORDER BY default_variant DESC, variant_name",
                                                      new String[]{"" + document.getId()} );
        for ( int i = 0; i < sqlResult.length; i++ ) {
            String[] sqlRow = sqlResult[i];

            String fileId = sqlRow[0];
            FileDocumentDomainObject.FileDocumentFile file = new FileDocumentDomainObject.FileDocumentFile();
            file.setFilename( sqlRow[1] );
            file.setMimeType( sqlRow[2] );
            file.setCreatedAsImage( 0 != Integer.parseInt( sqlRow[3] ) );
            file.setInputStreamSource( new FileInputStreamSource( DocumentStoringVisitor.getFileForFileDocument( document.getId(), fileId ) ) );
            document.addFile( fileId, file );
            boolean isDefaultFile = 0 != Integer.parseInt( sqlRow[4] );
            if ( isDefaultFile ) {
                document.setDefaultFileId( fileId );
            }
        }
    }

    public void visitHtmlDocument( HtmlDocumentDomainObject htmlDocument ) {
        String sqlStr = "SELECT frame_set FROM frameset_docs WHERE meta_id = ?";
        String html = service.sqlQueryStr( sqlStr, new String[]{"" + htmlDocument.getId()} );
        htmlDocument.setHtml( html );
    }

    public void visitUrlDocument( UrlDocumentDomainObject document ) {
        String url = service.sqlQueryStr( "SELECT url_ref FROM url_docs WHERE meta_id = ?",
                                          new String[]{"" + document.getId()} );
        document.setUrl( url );
    }
}
