package imcode.server.document;

import imcode.server.Imcms;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class DocumentPermissionSetDomainObject implements Serializable {

    public static final int TYPE_ID__FULL = 0 ;
    public static final int TYPE_ID__RESTRICTED_1 = 1 ;
    public static final int TYPE_ID__RESTRICTED_2 = 2 ;
    public static final int TYPE_ID__READ = 3 ;
    public static final int TYPE_ID__NONE = 4 ;

    public static final DocumentPermissionSetDomainObject NONE = new TextDocumentPermissionSetDomainObject( TYPE_ID__NONE ) {
        public boolean hasPermission( DocumentPermission permission ) {
            return false;
        }
    };

    public static final DocumentPermissionSetDomainObject READ = new TextDocumentPermissionSetDomainObject( TYPE_ID__READ ) {
        public boolean hasPermission( DocumentPermission permission ) {
            return false ;
        }
    };

    public static final DocumentPermissionSetDomainObject FULL = new TextDocumentPermissionSetDomainObject( TYPE_ID__FULL ) {
        public TemplateGroupDomainObject[] getAllowedTemplateGroups() {
            return Imcms.getServices().getTemplateMapper().getAllTemplateGroups() ;
        }

        public int[] getAllowedDocumentTypeIds() {
            return Imcms.getServices().getDocumentMapper().getAllDocumentTypeIds() ;
        }

        public boolean hasPermission( DocumentPermission permission ) {
            return true ;
        }
    };

    private static final String PERMISSION_SET_NAME__FULL = "Full";
    private static final String PERMISSION_SET_NAME__RESTRICTED_1 = "Restricted One";
    private static final String PERMISSION_SET_NAME__RESTRICTED_2 = "Restricted Two";
    private static final String PERMISSION_SET_NAME__READ = "Read";
    private static final String PERMISSION_SET_NAME__NONE = "None";

    private int typeId ;

    private Set permissions = new HashSet() ;

    public DocumentPermissionSetDomainObject( int typeId ) {
        if (TYPE_ID__FULL > typeId || TYPE_ID__NONE < typeId) {
            throw new IllegalArgumentException( "Invalid typeId: "+typeId ) ;
        }
        this.typeId = typeId;
    }

    void setPermission(DocumentPermission permission, boolean b) {
        if (b) {
            permissions.add( permission ) ;
        } else {
            permissions.remove( permission ) ;
        }
    }

    public boolean hasPermission(DocumentPermission permission) {
        return permissions.contains( permission );
    }

    public int getTypeId() {
        return typeId;
    }

    public String getType() {
        return getName( typeId );
    }

    private static String getName( int userPermissionSetId ) {
        String result = null;
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
        return hasPermission(DocumentPermission.EDIT_DOCUMENT_INFORMATION);
    }

    public void setEditDocumentInformation( boolean editDocumentInformation ) {
        setPermission( DocumentPermission.EDIT_DOCUMENT_INFORMATION, editDocumentInformation);
    }

    public boolean getEditPermissions() {
        return hasPermission( DocumentPermission.EDIT_PERMISSIONS ) ;
    }

    public void setEditPermissions( boolean editPermissions ) {
        setPermission( DocumentPermission.EDIT_PERMISSIONS, editPermissions );
    }

    public boolean getEdit() {
        return hasPermission( DocumentPermission.EDIT ) ;
    }

    public void setEdit( boolean edit ) {
        setPermission( DocumentPermission.EDIT, edit );
    }

    void setFromBits( DocumentDomainObject document, DocumentPermissionSetMapper documentPermissionSetMapper,
                             int permissionBits, boolean forNewDocuments ) {
        documentPermissionSetMapper.setDocumentPermissionSetFromBits( this, permissionBits) ;
    }

}
