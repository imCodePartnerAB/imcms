package imcode.server.document;

import imcode.server.IMCServiceInterface;
import imcode.server.ApplicationServer;
import imcode.util.FileInputStreamSource;

import java.io.File;

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
        String[] sqlResult = service.sqlQuery( "SELECT filename, mime, created_as_image FROM fileupload_docs WHERE meta_id = ?",
                                               new String[]{"" + document.getId()} );
        if ( sqlResult.length > 0 ) {
            document.setFilename( sqlResult[0] );
            document.setMimeType( sqlResult[1] );
            document.setCreatedAsImage( 0 != Integer.parseInt( sqlResult[2] ) );
            document.setInputStreamSource( new FileInputStreamSource( getUploadedFile( document ) ) );
        }
    }

    private File getUploadedFile( final FileDocumentDomainObject document ) {
        File file = new File( service.getFilePath(), "" + document.getId() );
        if ( !file.exists() ) {
            // FIXME: deprecated
            file = new File( service.getFilePath(), "" + document.getId() + "_se" );
        }
        return file;
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
