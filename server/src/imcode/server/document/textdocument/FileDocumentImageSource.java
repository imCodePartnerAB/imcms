package imcode.server.document.textdocument;

import imcode.server.document.DocumentDomainObject;
import imcode.server.document.FileDocumentDomainObject;
import imcode.server.document.DocumentReference;
import imcode.util.Utility;
import imcode.util.io.EmptyInputStreamSource;
import imcode.util.io.InputStreamSource;

import java.util.Date;

public class FileDocumentImageSource extends ImageSource {
    private DocumentReference fileDocumentReference;

    public FileDocumentImageSource( DocumentReference fileDocumentReference ) {
        this.fileDocumentReference = fileDocumentReference;
        DocumentDomainObject document = fileDocumentReference.getDocument( );
        if ( !( document instanceof FileDocumentDomainObject ) ) {
            throw new IllegalArgumentException( "Not a file document: " + document.getId( ) );
        }
    }

    public InputStreamSource getInputStreamSource( ) {
        if ( null == getFileDocument() ) {
            return new EmptyInputStreamSource( );
        }
        return getFileDocument( ).getDefaultFile( ).getInputStreamSource( );
    }

    public FileDocumentDomainObject getFileDocument( ) {
        return (FileDocumentDomainObject)fileDocumentReference.getDocument( );
    }

    public String getUrlPathRelativeToContextPath( ) {
        return Utility.getContextRelativePathToDocument( getFileDocument() ) ;
    }

    public String toStorageString( ) {
        if (isEmpty()) {
            return "" ;
        }
        return ""+getFileDocument().getId() ;
    }

    public int getTypeId( ) {
        return ImageSource.IMAGE_TYPE_ID__FILE_DOCUMENT;
    }

    public Date getModifiedDatetime( ) {
        if (isEmpty()) {
            return null ;
        }
        return getFileDocument().getModifiedDatetime( );
    }

    public String getName() {
        return Integer.toString(fileDocumentReference.getDocumentId());
    }

}
