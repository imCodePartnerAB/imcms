package com.imcode.imcms.domain.dto;

import com.imcode.imcms.model.Document;
import com.imcode.imcms.persistence.entity.Meta.Permission;
import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.util.Value;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;

import static com.imcode.imcms.persistence.entity.Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE;
import static com.imcode.imcms.persistence.entity.Meta.PublicationStatus.NEW;

@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class DocumentDTO extends Document implements Serializable {

    protected static final long serialVersionUID = -1197329246115859534L;

    public DocumentDTO(Document from) {
        super(from);
    }

    public static DocumentDTO createEmpty() {
        return Value.with(new DocumentDTO(), documentDTO -> {
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

}
