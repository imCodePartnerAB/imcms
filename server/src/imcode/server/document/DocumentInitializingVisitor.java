package imcode.server.document;

import imcode.server.ApplicationServer;
import imcode.server.IMCServiceInterface;
import imcode.util.FileInputStreamSource;

class DocumentInitializingVisitor extends DocumentVisitor {

    IMCServiceInterface service = ApplicationServer.getIMCServiceInterface();

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
        String[][] sqlResult = service.sqlQueryMulti( "SELECT variant_name, filename, mime, created_as_image, default_variant FROM fileupload_docs WHERE meta_id = ?",
                                                      new String[]{"" + document.getId()} );
        for ( int i = 0; i < sqlResult.length; i++ ) {
            String[] sqlRow = sqlResult[i];

            String variantName = sqlRow[0];
            FileDocumentDomainObject.FileDocumentFile fileDocumentFile = new FileDocumentDomainObject.FileDocumentFile();
            fileDocumentFile.setFilename( sqlRow[1] );
            fileDocumentFile.setMimeType( sqlRow[2] );
            fileDocumentFile.setCreatedAsImage( 0 != Integer.parseInt( sqlRow[3] ) );
            fileDocumentFile.setInputStreamSource( new FileInputStreamSource( DocumentStoringVisitor.getFileForFileDocument( document.getId(), variantName ) ) );
            document.addFileVariant( variantName, fileDocumentFile );
            if ( 0 != Integer.parseInt( sqlRow[4] ) ) {
                document.setDefaultFileVariantName( variantName );
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
