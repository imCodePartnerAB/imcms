/*
 * Created by IntelliJ IDEA.
 * User: kreiger
 * Date: 2004-mar-26
 * Time: 16:33:01
 */
package imcode.server.document;

import imcode.server.IMCConstants;

public class DocumentPermissionSetDomainObject {

    public final static int FULL = IMCConstants.DOC_PERM_SET_FULL;
    public final static int RESTRICTED_1 = IMCConstants.DOC_PERM_SET_RESTRICTED_1;
    public final static int RESTRICTED_2 = IMCConstants.DOC_PERM_SET_RESTRICTED_2;
    public final static int READ = IMCConstants.DOC_PERM_SET_READ;
    public final static int NONE = IMCConstants.DOC_PERM_SET_NONE;
    private static final String PERMISSION_SET_NAME_RESTRICTED_1 = "Restricted One";
    private static final String PERMISSION_SET_NAME_RESTRICTED_2 = "Restricted Two";
    private static final String PERMISSION_SET_NAME_FULL = "Full";
    private static final String PERMISSION_SET_NAME_READ = "Read";
    private static final String PERMISSION_SET_NAME_NONE = "None";

    private int permissionType = NONE;

    private boolean editDocumentInformation;
    private boolean editPermissions;
    private boolean edit;

    private boolean forNewDocuments ;

    private int[] allowedDocumentTypeIds;

    public DocumentPermissionSetDomainObject( int permissionType ) {
        this.permissionType = permissionType;
    }

    public int getPermissionType() {
        return permissionType;
    }

    public void setPermissionType( int permissionType ) {
        this.permissionType = permissionType;
    }

    public String getType() {
        return getName( permissionType );
    }

    private static String getName( int userPermissionSetId ) {
        String result = null;
        switch ( userPermissionSetId ) {
            case FULL:
                result = PERMISSION_SET_NAME_FULL;
                break;
            case RESTRICTED_1:
                result = PERMISSION_SET_NAME_RESTRICTED_1;
                break;
            case RESTRICTED_2:
                result = PERMISSION_SET_NAME_RESTRICTED_2;
                break;
            case READ:
                result = PERMISSION_SET_NAME_READ;
                break;
            default:
                result = PERMISSION_SET_NAME_NONE;
                break;
        }
        return result;
    }

    public String toString() {
        StringBuffer buff = new StringBuffer();
        buff.append( getType() );
        switch ( permissionType ) {
            case RESTRICTED_1:
            case RESTRICTED_2:
                buff.append( " (" )
                        .append( "editDocumentInformation=" + editDocumentInformation + ", " )
                        .append( "editPermissions=" + editPermissions + ", " )
                        .append( ")" );
                break;
        }
        return buff.toString();
    }

    public boolean getEditDocumentInformation() {
        return editDocumentInformation;
    }

    public void setEditDocumentInformation( boolean editDocumentInformation ) {
        this.editDocumentInformation = editDocumentInformation;
    }

    public boolean getEditPermissions() {
        return editPermissions;
    }

    public void setEditPermissions( boolean editPermissions ) {
        this.editPermissions = editPermissions;
    }

    public boolean getEdit() {
        return edit;
    }

    public void setEdit( boolean edit ) {
        this.edit = edit;
    }

    public void setFromBits( DocumentDomainObject document, DocumentPermissionSetMapper documentPermissionSetMapper,
                             int permissionBits ) {
        documentPermissionSetMapper.setDocumentPermissionSetFromBits( document, this, permissionBits) ;
    }

    public void setAllowedDocumentTypeIds( int[] allowedDocumentTypeIds ) {
        this.allowedDocumentTypeIds = allowedDocumentTypeIds;
    }

    public int[] getAllowedDocumentTypeIds() {
        return allowedDocumentTypeIds;
    }

    public boolean isForNewDocuments() {
        return forNewDocuments;
    }

    public void setForNewDocuments( boolean forNewDocuments ) {
        this.forNewDocuments = forNewDocuments;
    }

}
