package com.imcode.imcms;

import imcode.server.IMCConstants;

public class DocumentPermissionSet {

    private static final String NAME_READ = "Read";
    private static final String NAME_NONE = "None";
    private static final String NAME_FULL = "Full";
    public static final String NAME_RESTRICTED_1 = "Restricted One";
    public static final String NAME_RESTRICTED_2 = "Restricted Two";

    public final static DocumentPermissionSet FULL = new DocumentPermissionSet( IMCConstants.DOC_PERM_SET_FULL, true, true, true, true, true, true, true, true );
    public final static DocumentPermissionSet READ = new DocumentPermissionSet( IMCConstants.DOC_PERM_SET_READ );
    public final static DocumentPermissionSet NONE = new DocumentPermissionSet( IMCConstants.DOC_PERM_SET_NONE );

    private final static DocumentPermissionSet RESTRICTED_1 = new DocumentPermissionSet( IMCConstants.DOC_PERM_SET_RESTRICTED_1 );
    private final static DocumentPermissionSet RESTRICTED_2 = new DocumentPermissionSet( IMCConstants.DOC_PERM_SET_RESTRICTED_2 );

    private String permissonTypeStr;
    private int permissionType = IMCConstants.DOC_PERM_SET_NONE;
    private boolean changeDocumentInformation;
    private boolean changeAdvancedDocumentInformation;
    private boolean changePermissionsForRoles;
    private boolean changeText;
    private boolean changeImages;
    private boolean changeMenues;
    private boolean changeTemplates;
    private boolean changeIncludes;

    private DocumentPermissionSet( int permissionType ) {
        this( permissionType, false, false, false, false, false, false, false, false );
    }

    private DocumentPermissionSet( int permissionType, boolean changeDocumentInformation, boolean changeAdvancedDocumentInformation, boolean changePermissionsForRoles, boolean changeText, boolean changeImages, boolean changeMenues, boolean changeTemplates, boolean changeIncludes ) {
        this.permissonTypeStr = getName( permissionType );
        this.permissionType = permissionType;
        this.changeDocumentInformation = changeDocumentInformation;
        this.changeAdvancedDocumentInformation = changeAdvancedDocumentInformation;
        this.changePermissionsForRoles = changePermissionsForRoles;
        this.changeText = changeText;
        this.changeImages = changeImages;
        this.changeMenues = changeMenues;
        this.changeTemplates = changeTemplates;
        this.changeIncludes = changeIncludes;
    }

    public String getPermissonType() {
        return permissonTypeStr;
    }

    public String toString() {
        return getPermissonType();
    }

    public boolean isChangeDocumentInformation() {
        return changeDocumentInformation;
    }

    public boolean isChangeAdvancedDocumentInformation() {
        return changeAdvancedDocumentInformation;
    }

    public boolean isChangePermissionsForRoles() {
        return changePermissionsForRoles;
    }

    public boolean isChangeText() {
        return changeText;
    }

    public boolean isChangeImages() {
        return changeImages;
    }

    public boolean isChangeMenues() {
        return changeMenues;
    }

    public boolean isChangeTemplates() {
        return changeTemplates;
    }

    public boolean isChangeIncludes() {
        return changeIncludes;
    }

    static DocumentPermissionSet get( int userPermissionSetId ) {
        DocumentPermissionSet result = null;
        switch( userPermissionSetId ) {
            case IMCConstants.DOC_PERM_SET_FULL:
                result = FULL;
                break;
            case IMCConstants.DOC_PERM_SET_RESTRICTED_1:
                result = new DocumentPermissionSet( IMCConstants.DOC_PERM_SET_RESTRICTED_1 );
                break;
            case IMCConstants.DOC_PERM_SET_RESTRICTED_2:
                result = new DocumentPermissionSet( IMCConstants.DOC_PERM_SET_RESTRICTED_2 );
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

    private static String getName( int userPermissionSetId ) {
        String result = null;
        switch( userPermissionSetId ) {
            case IMCConstants.DOC_PERM_SET_FULL:
                result = NAME_FULL;
                break;
            case IMCConstants.DOC_PERM_SET_RESTRICTED_1:
                result = NAME_RESTRICTED_1;
                break;
            case IMCConstants.DOC_PERM_SET_RESTRICTED_2:
                result = NAME_RESTRICTED_2;
                break;
            case IMCConstants.DOC_PERM_SET_READ:
                result = NAME_READ;
                break;
            default:
                result = NAME_NONE;
                break;
        }
        return result;
    }

    public boolean equals( Object o ) {
        if( this == o )
            return true;
        if( !(o instanceof DocumentPermissionSet) )
            return false;

        final DocumentPermissionSet documentPermissionSet = (DocumentPermissionSet)o;

        if( changeAdvancedDocumentInformation != documentPermissionSet.changeAdvancedDocumentInformation )
            return false;
        if( changeDocumentInformation != documentPermissionSet.changeDocumentInformation )
            return false;
        if( changeImages != documentPermissionSet.changeImages )
            return false;
        if( changeIncludes != documentPermissionSet.changeIncludes )
            return false;
        if( changeMenues != documentPermissionSet.changeMenues )
            return false;
        if( changePermissionsForRoles != documentPermissionSet.changePermissionsForRoles )
            return false;
        if( changeTemplates != documentPermissionSet.changeTemplates )
            return false;
        if( changeText != documentPermissionSet.changeText )
            return false;
        if( permissionType != documentPermissionSet.permissionType )
            return false;
        if( !permissonTypeStr.equals( documentPermissionSet.permissonTypeStr ) )
            return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = permissonTypeStr.hashCode();
        result = 29 * result + permissionType;
        result = 29 * result + (changeDocumentInformation ? 1 : 0);
        result = 29 * result + (changeAdvancedDocumentInformation ? 1 : 0);
        result = 29 * result + (changePermissionsForRoles ? 1 : 0);
        result = 29 * result + (changeText ? 1 : 0);
        result = 29 * result + (changeImages ? 1 : 0);
        result = 29 * result + (changeMenues ? 1 : 0);
        result = 29 * result + (changeTemplates ? 1 : 0);
        result = 29 * result + (changeIncludes ? 1 : 0);
        return result;
    }

    protected Object clone() throws CloneNotSupportedException {
        DocumentPermissionSet clone = (DocumentPermissionSet)super.clone();
        clone.permissonTypeStr = this.permissonTypeStr;
        clone.permissionType = this.permissionType;
        clone.changeDocumentInformation = this.changeDocumentInformation;
        clone.changeAdvancedDocumentInformation = this.changeAdvancedDocumentInformation;
        clone.changePermissionsForRoles = this.changePermissionsForRoles;
        clone.changeText = this.changeText;
        clone.changeImages = this.changeImages;
        clone.changeMenues = this.changeMenues;
        clone.changeTemplates = this.changeTemplates;
        clone.changeIncludes = this.changeIncludes;
        return clone;
    }
}