package com.imcode.imcms.model;

import com.imcode.imcms.domain.dto.*;
import com.imcode.imcms.persistence.entity.Meta.DisabledLanguageShowMode;
import com.imcode.imcms.persistence.entity.Meta.DocumentType;
import com.imcode.imcms.persistence.entity.Meta.Permission;
import com.imcode.imcms.persistence.entity.Meta.PublicationStatus;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 11.01.18.
 */
@Data
@NoArgsConstructor
public abstract class Document {

    protected Integer id;
    @Setter(AccessLevel.NONE)
    protected DocumentType type;
    protected String target;
    protected String alias;
    protected List<CommonContentDTO> commonContents;
    protected PublicationStatus publicationStatus;
    protected AuditDTO published;
    protected AuditDTO archived;
    protected AuditDTO publicationEnd;
    protected AuditDTO modified;
    protected AuditDTO created;
    protected DisabledLanguageShowMode disabledLanguageShowMode;
    protected AuditDTO currentVersion;
    protected Set<String> keywords;
    protected boolean searchDisabled;
    protected Set<CategoryDTO> categories;
    protected Set<RestrictedPermissionDTO> restrictedPermissions;
    protected Map<Integer, Permission> roleIdToPermission;

    protected Document(Document from) {
        id = from.id;
        type = from.type; // not sure
        target = from.target;
        alias = from.alias;
        commonContents = from.commonContents;
        publicationStatus = from.publicationStatus;
        published = from.published;
        archived = from.archived;
        publicationEnd = from.publicationEnd;
        modified = from.modified;
        created = from.created;
        disabledLanguageShowMode = from.disabledLanguageShowMode;
        currentVersion = from.currentVersion;
        keywords = from.keywords;
        searchDisabled = from.searchDisabled;
        categories = from.categories;
        restrictedPermissions = from.restrictedPermissions;
        roleIdToPermission = from.roleIdToPermission;
    }

    public Set<RestrictedPermissionDTO> getRestrictedPermissions() {
        return (this.restrictedPermissions == null) ? null : new TreeSet<>(this.restrictedPermissions);
    }

    @SuppressWarnings("unused") // used on client side
    public DocumentStatus getDocumentStatus() {
        if (PublicationStatus.NEW.equals(publicationStatus)) {
            return DocumentStatus.IN_PROCESS;

        } else if (PublicationStatus.DISAPPROVED.equals(publicationStatus)) {
            return DocumentStatus.DISAPPROVED;

        } else if (isAuditDateInPast(archived)) {
            return DocumentStatus.ARCHIVED;

        } else if (isAuditDateInPast(publicationEnd)) {
            return DocumentStatus.PASSED;

        } else if (PublicationStatus.APPROVED.equals(publicationStatus) && isAuditDateInPast(published)) {
            return DocumentStatus.PUBLISHED;

        } else if (PublicationStatus.APPROVED.equals(publicationStatus) && isAuditDateInFuture(published)) {
            return DocumentStatus.PUBLISHED_WAITING;

        } else if (PublicationStatus.APPROVED.equals(publicationStatus) && isNullAuditDate(published)) {
            return DocumentStatus.IN_PROCESS;

        } else { // should newer happen
            return DocumentStatus.PUBLISHED;
        }
    }

    private boolean isNullAuditDate(AuditDTO auditToCheck) {
        return (auditToCheck == null) || (auditToCheck.getFormattedDate() == null);
    }

    private boolean isAuditDateInPast(AuditDTO auditToCheck) {
        return (auditToCheck != null)
                && (auditToCheck.getFormattedDate() != null)
                && new Date().after(auditToCheck.getFormattedDate());
    }

    private boolean isAuditDateInFuture(AuditDTO auditToCheck) {
        return (auditToCheck != null)
                && (auditToCheck.getFormattedDate() != null)
                && new Date().before(auditToCheck.getFormattedDate());
    }

}
