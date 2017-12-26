package com.imcode.imcms.domain.dto;

import com.imcode.imcms.persistence.entity.Meta.DisabledLanguageShowMode;
import com.imcode.imcms.persistence.entity.Meta.DocumentType;
import com.imcode.imcms.persistence.entity.Meta.Permission;
import com.imcode.imcms.persistence.entity.Meta.PublicationStatus;
import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.util.Value;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.*;

import static com.imcode.imcms.persistence.entity.Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE;
import static com.imcode.imcms.persistence.entity.Meta.PublicationStatus.NEW;

@Data
@NoArgsConstructor
public class DocumentDTO implements Serializable {

    protected static final long serialVersionUID = -1197329246115859534L;

    protected Integer id;

    protected String title;

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

    protected DocumentDTO(DocumentDTO from) {
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

    public static DocumentDTO createEmpty() {
        return Value.with(new DocumentDTO(), documentDTO -> {
            documentDTO.title = "";
            documentDTO.target = "";
            documentDTO.alias = "";

            // common contents have to be set by service

            documentDTO.publicationStatus = NEW;
            documentDTO.disabledLanguageShowMode = SHOW_IN_DEFAULT_LANGUAGE;
            documentDTO.keywords = new HashSet<>();
            documentDTO.categories = new HashSet<>();
            documentDTO.roleIdToPermission = new HashMap<>();
            documentDTO.restrictedPermissions = new HashSet<>();

            final RestrictedPermissionDTO restricted1 = new RestrictedPermissionDTO();
            final RestrictedPermissionDTO restricted2 = new RestrictedPermissionDTO();
            restricted1.setPermission(Permission.RESTRICTED_1);
            restricted2.setPermission(Permission.RESTRICTED_2);

            documentDTO.restrictedPermissions.add(restricted1);
            documentDTO.restrictedPermissions.add(restricted2);

            documentDTO.published = new AuditDTO();
            documentDTO.archived = new AuditDTO();
            documentDTO.publicationEnd = new AuditDTO();
            documentDTO.modified = new AuditDTO();
            documentDTO.created = new AuditDTO();
            documentDTO.currentVersion = new AuditDTO();
            documentDTO.currentVersion.setId(Version.WORKING_VERSION_INDEX);
        });
    }

    public Set<RestrictedPermissionDTO> getRestrictedPermissions() {
        return (this.restrictedPermissions == null) ? null : new TreeSet<>(this.restrictedPermissions);
    }

}
