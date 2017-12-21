package com.imcode.imcms.domain.dto;

import com.imcode.imcms.persistence.entity.Meta.DisabledLanguageShowMode;
import com.imcode.imcms.persistence.entity.Meta.DocumentType;
import com.imcode.imcms.persistence.entity.Meta.PublicationStatus;
import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.util.Value;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.*;

import static com.imcode.imcms.persistence.entity.Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE;
import static com.imcode.imcms.persistence.entity.Meta.PublicationStatus.NEW;

@Data
@NoArgsConstructor
public class DocumentDTO implements Serializable {

    private static final long serialVersionUID = -2317764204932918145L;

    private Integer id;

    private String title;

    private DocumentType type;

    private String target;

    private String alias;

    private List<CommonContentDTO> commonContents;

    private PublicationStatus publicationStatus;

    private AuditDTO published;

    private AuditDTO archived;

    private AuditDTO publicationEnd;

    private AuditDTO modified;

    private AuditDTO created;

    private DisabledLanguageShowMode disabledLanguageShowMode;

    private AuditDTO currentVersion;

    private Set<String> keywords;

    private boolean searchDisabled;

    private Set<CategoryDTO> categories;

    private Map<PermissionDTO, RestrictedPermissionDTO> restrictedPermissions;

    private Map<Integer, PermissionDTO> roleIdToPermission;

    private TextDocumentTemplateDTO template;

    public static DocumentDTO createNew(DocumentType type) {
        return Value.with(new DocumentDTO(), documentDTO -> {
            documentDTO.title = "";
            documentDTO.target = "";
            documentDTO.alias = "";
            documentDTO.type = type;

            // common contents have to be set by service

            documentDTO.publicationStatus = NEW;
            documentDTO.disabledLanguageShowMode = SHOW_IN_DEFAULT_LANGUAGE;
            documentDTO.keywords = new HashSet<>();
            documentDTO.categories = new HashSet<>();
            documentDTO.roleIdToPermission = new HashMap<>();
            documentDTO.restrictedPermissions = new HashMap<>();
            documentDTO.restrictedPermissions.put(PermissionDTO.RESTRICTED_1, new RestrictedPermissionDTO());
            documentDTO.restrictedPermissions.put(PermissionDTO.RESTRICTED_2, new RestrictedPermissionDTO());

            documentDTO.published = new AuditDTO();
            documentDTO.archived = new AuditDTO();
            documentDTO.publicationEnd = new AuditDTO();
            documentDTO.modified = new AuditDTO();
            documentDTO.created = new AuditDTO();
            documentDTO.currentVersion = new AuditDTO();
            documentDTO.currentVersion.setId(Version.WORKING_VERSION_INDEX);
            documentDTO.template = TextDocumentTemplateDTO.createDefault();
        });
    }
}
