package com.imcode.imcms.api;

import com.imcode.imcms.domain.dto.PermissionDTO;
import com.imcode.util.CountingIterator;
import imcode.server.Imcms;
import imcode.server.document.DocumentPermissionSetDomainObject;
import imcode.server.document.TemplateGroupDomainObject;
import imcode.server.document.TextDocumentPermissionSetDomainObject;

import java.util.List;
import java.util.Set;

public class DocumentPermissionSet {

    /**
     * @deprecated Use {@link DocumentPermissionSetType#FULL}
     */
    @Deprecated
    public final static int FULL = PermissionDTO.EDIT.getId();
    /**
     * @deprecated Use {@link DocumentPermissionSetType#RESTRICTED_1}
     */
    @Deprecated
    public final static int RESTRICTED_1 = PermissionDTO.RESTRICTED_1.getId();
    /**
     * @deprecated Use {@link DocumentPermissionSetType#RESTRICTED_2}
     */
    @Deprecated
    public final static int RESTRICTED_2 = PermissionDTO.RESTRICTED_2.getId();
    /**
     * @deprecated Use {@link DocumentPermissionSetType#READ}
     */
    @Deprecated
    public final static int READ = PermissionDTO.VIEW.getId();
    /**
     * @deprecated Use {@link DocumentPermissionSetType#NONE}
     */
    @Deprecated
    public final static int NONE = PermissionDTO.NONE.getId();

    private DocumentPermissionSetDomainObject internalDocPermSet;

    public DocumentPermissionSet(DocumentPermissionSetDomainObject internalDocPermSet) {
        this.internalDocPermSet = internalDocPermSet;
    }

    /**
     * @deprecated No replacement, don't use. Will be removed in 4.0 or later.
     */
    public String getType() {
        return internalDocPermSet.getTypeName();
    }

    public String toString() {
        return internalDocPermSet.toString();
    }

    public boolean getEditDocumentInformationPermission() {
        return internalDocPermSet.getEditDocumentInformation();
    }

    public void setEditDocumentInformationPermission(boolean b) {
        internalDocPermSet.setEditDocumentInformation(b);
    }

    public boolean getEditRolePermissionsPermission() {
        return internalDocPermSet.getEditPermissions();
    }

    public void setEditRolePermissionsPermission(boolean b) {
        internalDocPermSet.setEditPermissions(b);
    }

    public boolean getEditTextsPermission() {
        if (internalDocPermSet instanceof TextDocumentPermissionSetDomainObject) {
            return ((TextDocumentPermissionSetDomainObject) internalDocPermSet).getEditTexts();
        }

        return false;
    }

    public void setEditTextsPermission(boolean b) {
        if (internalDocPermSet instanceof TextDocumentPermissionSetDomainObject) {
            ((TextDocumentPermissionSetDomainObject) internalDocPermSet).setEditTexts(b);
        }
    }

    public boolean getEditIncludesPermission() {
        if (internalDocPermSet instanceof TextDocumentPermissionSetDomainObject) {
            return ((TextDocumentPermissionSetDomainObject) internalDocPermSet).getEditIncludes();
        }
        return false;
    }

    public void setEditIncludesPermission(boolean b) {
        if (internalDocPermSet instanceof TextDocumentPermissionSetDomainObject) {
            ((TextDocumentPermissionSetDomainObject) internalDocPermSet).setEditIncludes(b);
        }
    }

    public boolean getEditPicturesPermission() {
        if (internalDocPermSet instanceof TextDocumentPermissionSetDomainObject) {
            return ((TextDocumentPermissionSetDomainObject) internalDocPermSet).getEditImages();
        }
        return false;
    }

    public void setEditPicturesPermission(boolean b) {
        if (internalDocPermSet instanceof TextDocumentPermissionSetDomainObject) {
            ((TextDocumentPermissionSetDomainObject) internalDocPermSet).setEditImages(b);
        }
    }

    public boolean getEditMenusPermission() {
        if (internalDocPermSet instanceof TextDocumentPermissionSetDomainObject) {
            return ((TextDocumentPermissionSetDomainObject) internalDocPermSet).getEditMenus();
        }

        return false;
    }

    public void setEditMenusPermission(boolean b) {
        if (internalDocPermSet instanceof TextDocumentPermissionSetDomainObject) {
            ((TextDocumentPermissionSetDomainObject) internalDocPermSet).setEditMenus(b);
        }
    }

    public String[] getEditableTemplateGroupNames() {
        if (internalDocPermSet instanceof TextDocumentPermissionSetDomainObject) {
            Set<Integer> allowedTemplateGroupIds = ((TextDocumentPermissionSetDomainObject) internalDocPermSet).getAllowedTemplateGroupIds();
            List<TemplateGroupDomainObject> allowedTemplateGroups = Imcms.getServices().getTemplateMapper().getTemplateGroups(allowedTemplateGroupIds);
            String[] templateGroupNames = new String[allowedTemplateGroupIds.size()];
            for (CountingIterator<TemplateGroupDomainObject> iterator = new CountingIterator<>(allowedTemplateGroups.iterator()); iterator.hasNext(); )
            {
                TemplateGroupDomainObject templateGroup = iterator.next();
                templateGroupNames[iterator.getCount() - 1] = templateGroup.getName();
            }
            return templateGroupNames;
        }
        return new String[]{};
    }

    public void setEditPermissionsPermission(boolean b) {
        internalDocPermSet.setEditPermissions(b);
    }
}