package imcode.server.document;

import imcode.server.IMCConstants;

public class DocumentPermissionSetDomainObject {
    public static final String RESTRICTED_1 = "Restricted One";
    public static final String RESTRICTED_2 = "Restricted Two";
    public static final String FULL = "Full";
    public static final String READ = "Read";
    public static final String NONE = "None";

    final static int FULL_ID = 0;
    final static int RESTRICTED_1_ID = 1;
    final static int RESTRICTED_2_ID = 2;
    public final static int READ_ID = 3;
    final static int NONE_ID = 4;

    int permissionType = IMCConstants.DOC_PERM_SET_NONE;
    private boolean editHeadline;
    private boolean editDocumentInformation;
    private boolean editPermissions;
    private boolean editTexts;
    private boolean editMenues;
    private boolean editTemplates;
    private boolean editIncludes;
    private boolean editPictures;
    private DocumentDomainObject document;

    private String[] editMenuesNames;
    private String[] editTemplatesNames;

    public DocumentPermissionSetDomainObject( DocumentDomainObject document, int permissionType ) {
        this.permissionType = permissionType;
        this.document = document;
    }

    public int getPermissionType() {
        return permissionType;
    }

    public String getType() {
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

    public String toString() {
        StringBuffer buff = new StringBuffer();
        buff.append( getType() + " ("  );
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

    DocumentDomainObject getDocument() {
        return document;
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

    public String[] getEditableTamplateNames() {
        return editTemplatesNames;
    }

    public String[] getEditableMenuNames() {
        return editMenuesNames;
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

    void setEditableMenuNames( String[] names ) {
        this.editMenuesNames = names;
    }

    void setEditableTemplateGroupNames( String[] names ) {
        this.editTemplatesNames = names;
    }
}