package imcode.server.document;

import imcode.server.IMCConstants;

public class DocumentPermissionSetDomainObject {
    public static final String RESTRICTED_1 = "Restricted One";
    public static final String RESTRICTED_2 = "Restricted Two";
    public static final String FULL = "Full";
    public static final String READ = "Read";
    public static final String NONE = "None";

    int permissionType = IMCConstants.DOC_PERM_SET_NONE;
    private boolean editHeadline;
    private boolean editDocumentInformation;
    private boolean editPermissions;
    private boolean editTexts;
    private boolean etidPictures;
    private boolean editMenues;
    private boolean editTemplates;
    private boolean editIncludes;
    private boolean editPictures;

    public DocumentPermissionSetDomainObject( int permissionType ) {
        this( permissionType, false, false, false, false, false, false, false, false, false );
    }

    private DocumentPermissionSetDomainObject( int permissionType, boolean changeDocumentInformation, boolean changeAdvancedDocumentInformation, boolean changePermissionsForRoles, boolean changeText, boolean changeImages, boolean changeMenues, boolean changeTemplates, boolean changeIncludes, boolean editPictures ) {
        this.permissionType = permissionType;
        this.editHeadline = changeDocumentInformation;
        this.editDocumentInformation = changeAdvancedDocumentInformation;
        this.editPermissions = changePermissionsForRoles;
        this.editTexts = changeText;
        this.etidPictures = changeImages;
        this.editMenues = changeMenues;
        this.editTemplates = changeTemplates;
        this.editIncludes = changeIncludes;
        this.editPictures = editPictures;
    }

    public int getPermissionType() {
        return permissionType;
    }

    public String getName() {
        return getName( permissionType );
    }

    private static String getName( int userPermissionSetId ) {
        String result = null;
        switch( userPermissionSetId ) {
            case IMCConstants.DOC_PERM_SET_FULL:
                result = FULL;
                break;
            case IMCConstants.DOC_PERM_SET_RESTRICTED_1:
                result = RESTRICTED_1;
                break;
            case IMCConstants.DOC_PERM_SET_RESTRICTED_2:
                result = RESTRICTED_2;
                break;
            case IMCConstants.DOC_PERM_SET_READ:
                result = READ;
                break;
            default:
                result = NONE;
                break;
        }
        return result;
    }

    protected Object clone() throws CloneNotSupportedException {
        DocumentPermissionSetDomainObject clone = (DocumentPermissionSetDomainObject)super.clone();
        clone.permissionType = this.permissionType;
        clone.editHeadline = this.editHeadline;
        clone.editDocumentInformation = this.editDocumentInformation;
        clone.editPermissions = this.editPermissions;
        clone.editTexts = this.editTexts;
        clone.etidPictures = this.etidPictures;
        clone.editMenues = this.editMenues;
        clone.editTemplates = this.editTemplates;
        clone.editIncludes = this.editIncludes;
        return clone;
    }

    public String toString() {
        StringBuffer buff = new StringBuffer();
        buff.append( getName() + " ("  );
        buff.append( "editDocumentInformation=" + editDocumentInformation + ", " );
        buff.append( "editHeadline=" + editHeadline  + ", " );
        buff.append( "editIncludes=" + editIncludes  + ", " );
        buff.append( "editMenues=" + editMenues  + ", " );
        buff.append( "editPermissions=" + editPermissions + ", "  );
        buff.append( "editPictures=" + editPictures  + ", " );
        buff.append( "editTemplates=" + editTemplates  + ", " );
        buff.append( "editTexts=" + editTexts );
        buff.append( ")" );
        return buff.toString();
    }

    public boolean getEditHeadline() {
        return editHeadline;
    }

    public boolean getEditDocumentInformation() {
        return editDocumentInformation;
    }

    public boolean getEditPermissions() {
        return editPermissions;
    }

    public boolean getEditTexts() {
        return editTexts;
    }

    public boolean getEditMenues() {
        return editMenues;
    }

    public boolean getEditTemplates() {
        return editTemplates;
    }

    public boolean getEditIncludes() {
        return editIncludes;
    }

    public boolean getEditPictures() {
        return editPictures;
    }

    void setPermissionType( int permissionType ) {
        this.permissionType = permissionType;
    }

    void setEditHeadline( boolean editHeadline ) {
        this.editHeadline = editHeadline;
    }

    void setEditDocumentInformation( boolean editDocumentInformation ) {
        this.editDocumentInformation = editDocumentInformation;
    }

    void setEditPermissions( boolean editPermissions ) {
        this.editPermissions = editPermissions;
    }

    void setEditTexts( boolean editTexts ) {
        this.editTexts = editTexts;
    }

    void setEtidPictures( boolean etidPictures ) {
        this.etidPictures = etidPictures;
    }

    void setEditMenues( boolean editMenues ) {
        this.editMenues = editMenues;
    }

    void setEditTemplates( boolean editTemplates ) {
        this.editTemplates = editTemplates;
    }

    void setEditIncludes( boolean editIncludes ) {
        this.editIncludes = editIncludes;
    }

    void setEditPictures( boolean editPictures ) {
        this.editPictures = editPictures;
    }
}