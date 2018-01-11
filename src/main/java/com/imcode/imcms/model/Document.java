package com.imcode.imcms.model;

import com.imcode.imcms.domain.dto.AuditDTO;
import com.imcode.imcms.domain.dto.CategoryDTO;
import com.imcode.imcms.domain.dto.CommonContentDTO;
import com.imcode.imcms.domain.dto.RestrictedPermissionDTO;
import com.imcode.imcms.persistence.entity.Meta;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 11.01.18.
 */
@Getter
@Setter
@NoArgsConstructor
public abstract class Document {

    protected Integer id;
    protected String title;
    @Setter(AccessLevel.NONE)
    protected Meta.DocumentType type;
    protected String target;
    protected String alias;
    protected List<CommonContentDTO> commonContents;
    protected Meta.PublicationStatus publicationStatus;
    protected AuditDTO published;
    protected AuditDTO archived;
    protected AuditDTO publicationEnd;
    protected AuditDTO modified;
    protected AuditDTO created;
    protected Meta.DisabledLanguageShowMode disabledLanguageShowMode;
    protected AuditDTO currentVersion;
    protected Set<String> keywords;
    protected boolean searchDisabled;
    protected Set<CategoryDTO> categories;
    protected Set<RestrictedPermissionDTO> restrictedPermissions;
    protected Map<Integer, Meta.Permission> roleIdToPermission;

    protected Document(Document from) {
        id = from.id;
        title = from.title;
//        type = from.type; // not sure
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

}
