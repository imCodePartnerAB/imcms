package imcode.server.document;

import imcode.server.IMCConstants;

public class DocumentPermissionSetDomainObject {

    public final static int FULL = IMCConstants.DOC_PERM_SET_FULL ;
    public final static int RESTRICTED_1 = IMCConstants.DOC_PERM_SET_RESTRICTED_1 ;
    public final static int RESTRICTED_2 = IMCConstants.DOC_PERM_SET_RESTRICTED_2 ;
    public final static int READ = IMCConstants.DOC_PERM_SET_READ ;
    public final static int NONE = IMCConstants.DOC_PERM_SET_NONE ;

    private static final String PERMISSION_SET_NAME_RESTRICTED_1 = "Restricted One";
    private static final String PERMISSION_SET_NAME_RESTRICTED_2 = "Restricted Two";
    private static final String PERMISSION_SET_NAME_FULL = "Full";
    private static final String PERMISSION_SET_NAME_READ = "Read";
    private static final String PERMISSION_SET_NAME_NONE = "None";

    int permissionType = NONE;
    private boolean editHeadline;
    private boolean editDocumentInformation;
    private boolean editPermissions;
    private boolean editTexts;
    private boolean editMenus;
    private boolean editTemplates;
    private boolean editIncludes;
    private boolean editPictures;

    private String[] editMenuesNames;
    private String[] editTemplatesNames;

    public DocumentPermissionSetDomainObject( int permissionType ) {
        this.permissionType = permissionType;
    }

    public int getPermissionType() {
        return permissionType;
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

        buff.append( getType() ) ;
        switch ( permissionType ) {
            case RESTRICTED_1:
            case RESTRICTED_2:
                buff.append( " (" )
                        .append( "editDocumentInformation=" + editDocumentInformation + ", " )
                        .append( "editHeadline=" + editHeadline + ", " )
                        .append( "editIncludes=" + editIncludes + ", " )
                        .append( "editMenus=" + editMenus + ", " )
                        .append( "editPermissions=" + editPermissions + ", " )
                        .append( "editPictures=" + editPictures + ", " )
                        .append( "editTemplates=" + editTemplates + ", " )
                        .append( "editTexts=" + editTexts )
                        .append( ")" );
                break;
        }
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

    public boolean getEditMenus() {
        return editMenus;
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

    public String[] getEditableTemplateNames() {
        return editTemplatesNames;
    }

    public String[] getEditableMenuNames() {
        return editMenuesNames;
    }

    public void setPermissionType( int permissionType ) {
        this.permissionType = permissionType;
    }

    public void setEditHeadline( boolean editHeadline ) {
        this.editHeadline = editHeadline;
    }

    public void setEditDocumentInformation( boolean editDocumentInformation ) {
        this.editDocumentInformation = editDocumentInformation;
    }

    public void setEditPermissions( boolean editPermissions ) {
        this.editPermissions = editPermissions;
    }

    public void setEditTexts( boolean editTexts ) {
        this.editTexts = editTexts;
    }

    public void setEditMenus( boolean editMenus ) {
        this.editMenus = editMenus;
    }

    public void setEditTemplates( boolean editTemplates ) {
        this.editTemplates = editTemplates;
    }

    public void setEditIncludes( boolean editIncludes ) {
        this.editIncludes = editIncludes;
    }

    public void setEditPictures( boolean editPictures ) {
        this.editPictures = editPictures;
    }

    public void setEditableMenuNames( String[] names ) {
        this.editMenuesNames = names;
    }

    public void setEditableTemplateGroupNames( String[] names ) {
        this.editTemplatesNames = names;
    }

}