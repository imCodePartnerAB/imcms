package imcode.server.document;

import com.imcode.imcms.domain.dto.PermissionDTO;
import imcode.server.Imcms;
import imcode.server.ImcmsConstants;
import imcode.util.LazilyLoadedObject;
import imcode.util.ShouldNotBeThrownException;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Unfortunate API design.
 * The app instantiates and uses ONLY {@link TextDocumentPermissionSetDomainObject} for ALL doc types.
 * The {@link #EDIT} permission is an alias for the {@link TextDocumentPermissionSetDomainObject#EDIT_TEXTS} permission.
 */
public class DocumentPermissionSetDomainObject implements Serializable, LazilyLoadedObject.Copyable, Cloneable {

    public static final DocumentPermissionSetDomainObject NONE = new TextDocumentPermissionSetDomainObject(PermissionDTO.NONE) {
        public boolean hasPermission(DocumentPermission permission) {
            return false;
        }
    };

    public static final DocumentPermissionSetDomainObject READ = new TextDocumentPermissionSetDomainObject(PermissionDTO.VIEW) {
        public boolean hasPermission(DocumentPermission permission) {
            return false;
        }
    };

    public static final DocumentPermissionSetDomainObject FULL = new TextDocumentPermissionSetDomainObject(PermissionDTO.EDIT) {
        public Set<Integer> getAllowedTemplateGroupIds() {
            return Imcms.getServices().getTemplateMapper().getAllTemplateGroupIds();
        }

        public Set<Integer> getAllowedDocumentTypeIds() {
            return DocumentTypeDomainObject.getAllDocumentTypeIdsSet();
        }

        public boolean hasPermission(DocumentPermission permission) {
            return true;
        }
    };

    /**
     * Permission to edit doc's content.
     * <p>
     * Content is doc type depended:
     * -text fields in Text docs
     * -file references in File doc
     * -etc.
     */
    public static final DocumentPermission EDIT = new DocumentPermission("edit");
    public static final DocumentPermission EDIT_DOCUMENT_INFORMATION = new DocumentPermission("editDocumentInformation");
    public static final DocumentPermission EDIT_PERMISSIONS = new DocumentPermission("editPermissions");
    public final static int EDIT_DOCINFO_PERMISSION_ID = ImcmsConstants.PERM_EDIT_DOCINFO;
    public final static int EDIT_PERMISSIONS_PERMISSION_ID = ImcmsConstants.PERM_EDIT_PERMISSIONS;
    public final static int EDIT_DOCUMENT_PERMISSION_ID = ImcmsConstants.PERM_EDIT_DOCUMENT;
    private static final String PERMISSION_SET_NAME__FULL = "Full";
    private static final String PERMISSION_SET_NAME__RESTRICTED_1 = "Restricted One";
    private static final String PERMISSION_SET_NAME__RESTRICTED_2 = "Restricted Two";
    private static final String PERMISSION_SET_NAME__READ = "Read";
    private static final String PERMISSION_SET_NAME__NONE = "None";
    private PermissionDTO type;
    private HashSet<DocumentPermission> permissions = new HashSet<>();

    public DocumentPermissionSetDomainObject(PermissionDTO typeId) {
        this.type = typeId;
    }

    private static String getName(PermissionDTO userPermissionSetId) {
        String result;
        if (PermissionDTO.EDIT.equals(userPermissionSetId)) {
            result = PERMISSION_SET_NAME__FULL;
        } else if (PermissionDTO.RESTRICTED_1.equals(userPermissionSetId)) {
            result = PERMISSION_SET_NAME__RESTRICTED_1;
        } else if (PermissionDTO.RESTRICTED_2.equals(userPermissionSetId)) {
            result = PERMISSION_SET_NAME__RESTRICTED_2;
        } else if (PermissionDTO.VIEW.equals(userPermissionSetId)) {
            result = PERMISSION_SET_NAME__READ;
        } else {
            result = PERMISSION_SET_NAME__NONE;
        }
        return result;
    }

    void setPermission(DocumentPermission permission, boolean b) {
        if (b) {
            permissions.add(permission);
        } else {
            permissions.remove(permission);
        }
    }

    public boolean hasPermission(DocumentPermission permission) {
        return permissions.contains(permission);
    }

    public PermissionDTO getType() {
        return type;
    }

    public String getTypeName() {
        return getName(type);
    }

    public String toString() {
        StringBuffer buff = new StringBuffer();
        buff.append(getTypeName());
        if (PermissionDTO.RESTRICTED_1.equals(type) || PermissionDTO.RESTRICTED_2.equals(type)) {
            buff.append(" (")
                    .append("editDocumentInformation=")
                    .append(getEditDocumentInformation())
                    .append(", ")
                    .append("editPermissions=")
                    .append(getEditPermissions())
                    .append(", ")
                    .append(")");
        }
        return buff.toString();
    }

    public boolean getEditDocumentInformation() {
        return hasPermission(EDIT_DOCUMENT_INFORMATION);
    }

    public void setEditDocumentInformation(boolean editDocumentInformation) {
        setPermission(EDIT_DOCUMENT_INFORMATION, editDocumentInformation);
    }

    public boolean getEditPermissions() {
        return hasPermission(EDIT_PERMISSIONS);
    }

    public void setEditPermissions(boolean editPermissions) {
        setPermission(EDIT_PERMISSIONS, editPermissions);
    }

    /**
     * todo: move to DocumentLoader - used only to initialize meta???
     *
     * @param permissionBits
     * @see com.imcode.imcms.mapping.DocumentLoader#loadMeta
     */
    public void setFromBits(int permissionBits) {
        setEditDocumentInformation(0 != (permissionBits & EDIT_DOCINFO_PERMISSION_ID));
        setEditPermissions(0 != (permissionBits & EDIT_PERMISSIONS_PERMISSION_ID));
        setEdit(0 != (permissionBits & EDIT_DOCUMENT_PERMISSION_ID));
    }

    public boolean getEdit() {
        return hasPermission(EDIT);
    }

    public void setEdit(boolean edit) {
        setPermission(EDIT, edit);
    }

    public LazilyLoadedObject.Copyable copy() {
        try {
            return clone();
        } catch (CloneNotSupportedException e) {
            throw new ShouldNotBeThrownException(e);
        }
    }

    protected DocumentPermissionSetDomainObject clone() throws CloneNotSupportedException {
        DocumentPermissionSetDomainObject clone = (DocumentPermissionSetDomainObject) super.clone();
        clone.permissions = (HashSet<DocumentPermission>) permissions.clone();
        return clone;
    }

    public boolean isEmpty() {
        return permissions.isEmpty();
    }
}
