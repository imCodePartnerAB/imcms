package imcode.server.document;

import imcode.server.ImcmsServices;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class DocumentPermissionSetDomainObject implements Serializable {

    public static final int TYPE_ID__FULL = 0;
    public static final int TYPE_ID__RESTRICTED_1 = 1;
    public static final int TYPE_ID__RESTRICTED_2 = 2;
    public static final int TYPE_ID__READ = 3;
    public static final int TYPE_ID__NONE = 4;

    public static final DocumentPermissionSetDomainObject NONE = new TextDocumentPermissionSetDomainObject( TYPE_ID__NONE ) {
        public boolean hasPermission( DocumentPermission permission ) {
            return false;
        }
    };

    public static final DocumentPermissionSetDomainObject READ = new TextDocumentPermissionSetDomainObject( TYPE_ID__READ ) {
        public boolean hasPermission( DocumentPermission permission ) {
            return false;
        }
    };

    public static final DocumentPermissionSetDomainObject FULL = new TextDocumentPermissionSetDomainObject( TYPE_ID__FULL ) {
        public TemplateGroupDomainObject[] getAllowedTemplateGroups( ImcmsServices services ) {
            return services.getTemplateMapper().getAllTemplateGroups();
        }

        public int[] getAllowedDocumentTypeIds() {
            return DocumentTypeDomainObject.getAllDocumentTypeIds();
        }

        public boolean hasPermission( DocumentPermission permission ) {
            return true;
        }
    } ;

    static final DocumentPermission EDIT = new DocumentPermission( "edit" );

    private static final String PERMISSION_SET_NAME__FULL = "Full";
    private static final String PERMISSION_SET_NAME__RESTRICTED_1 = "Restricted One";
    private static final String PERMISSION_SET_NAME__RESTRICTED_2 = "Restricted Two";
    private static final String PERMISSION_SET_NAME__READ = "Read";
    private static final String PERMISSION_SET_NAME__NONE = "None";

    private int typeId;

    private Set permissions = new HashSet();
    static final DocumentPermission EDIT_DOCUMENT_INFORMATION = new DocumentPermission( "editDocumentInformation" );
    static final DocumentPermission EDIT_PERMISSIONS = new DocumentPermission( "editPermissions" );

    public DocumentPermissionSetDomainObject( int typeId ) {
        if ( TYPE_ID__FULL > typeId || TYPE_ID__NONE < typeId ) {
            throw new IllegalArgumentException( "Invalid typeId: " + typeId );
        }
        this.typeId = typeId;
    }

    void setPermission( DocumentPermission permission, boolean b ) {
        if ( b ) {
            permissions.add( permission );
        } else {
            permissions.remove( permission );
        }
    }

    public boolean hasPermission( DocumentPermission permission ) {
        return permissions.contains( permission );
    }

    public int getTypeId() {
        return typeId;
    }

    public String getType() {
        return getName( typeId );
    }

    private static String getName( int userPermissionSetId ) {
        String result ;
        switch ( userPermissionSetId ) {
        case TYPE_ID__FULL:
               result = PERMISSION_SET_NAME__FULL;
               break;
        case TYPE_ID__RESTRICTED_1:
               result = PERMISSION_SET_NAME__RESTRICTED_1;
               break;
        case TYPE_ID__RESTRICTED_2:
               result = PERMISSION_SET_NAME__RESTRICTED_2;
               break;
        case TYPE_ID__READ:
               result = PERMISSION_SET_NAME__READ;
               break;
        default:
               result = PERMISSION_SET_NAME__NONE;
               break;
        }
        return result;
    }

    public String toString() {
        StringBuffer buff = new StringBuffer();
        buff.append( getType() );
        switch ( typeId ) {
        case TYPE_ID__RESTRICTED_1:
        case TYPE_ID__RESTRICTED_2:
             buff.append( " (" )
                     .append( "editDocumentInformation=" + getEditDocumentInformation() + ", " )
                     .append( "editPermissions=" + getEditPermissions() + ", " )
                     .append( ")" );
             break;
        }
        return buff.toString();
    }

    public boolean getEditDocumentInformation() {
        return hasPermission( EDIT_DOCUMENT_INFORMATION );
    }

    public void setEditDocumentInformation( boolean editDocumentInformation ) {
        setPermission( EDIT_DOCUMENT_INFORMATION, editDocumentInformation );
    }

    public boolean getEditPermissions() {
        return hasPermission( EDIT_PERMISSIONS );
    }

    public void setEditPermissions( boolean editPermissions ) {
        setPermission( EDIT_PERMISSIONS, editPermissions );
    }

    void setFromBits( DocumentDomainObject document, DocumentPermissionSetMapper documentPermissionSetMapper,
                               int permissionBits, boolean forNewDocuments ) {
        documentPermissionSetMapper.setDocumentPermissionSetFromBits( this, permissionBits ) ;
    }

    public boolean getEdit() {
        return hasPermission( EDIT );
    }

    public void setEdit( boolean edit ) {
        setPermission( EDIT, edit );
    }
}
