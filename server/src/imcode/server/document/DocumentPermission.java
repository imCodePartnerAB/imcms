package imcode.server.document;

import org.apache.commons.lang.NullArgumentException;

public class DocumentPermission {

    private String name ;
    static final DocumentPermission EDIT_DOCUMENT_INFORMATION = new DocumentPermission( "editDocumentInformation" );
    static final DocumentPermission EDIT_PERMISSIONS = new DocumentPermission( "editPermissions" );
    static final DocumentPermission EDIT = new DocumentPermission( "edit" );

    public DocumentPermission( String name ) {
        if (null == name) {
            throw new NullArgumentException( "name" ) ;
        }
        this.name = name;
    }

    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof DocumentPermission ) ) {
            return false;
        }

        final DocumentPermission documentPermission = (DocumentPermission)o;

        return name.equals( documentPermission.name ) ;
    }

    public int hashCode() {
        return name.hashCode() ;
    }
}
