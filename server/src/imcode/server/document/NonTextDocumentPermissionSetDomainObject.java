package imcode.server.document;

public class NonTextDocumentPermissionSetDomainObject extends DocumentPermissionSetDomainObject {

    static final DocumentPermission EDIT = new DocumentPermission( "edit" );

    public NonTextDocumentPermissionSetDomainObject( int typeId ) {
        super( typeId );
    }

    public boolean getEdit() {
        return hasPermission( EDIT ) ;
    }

    public void setEdit( boolean edit ) {
        setPermission( EDIT, edit );
    }

    void setFromBits( DocumentDomainObject document, DocumentPermissionSetMapper documentPermissionSetMapper,
                      int permissionBits, boolean forNewDocuments ) {
        documentPermissionSetMapper.setNonTextDocumentPermissionSetFromBits( this, permissionBits ) ;
    }
}
