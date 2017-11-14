package com.imcode.imcms.domain.dto;

import com.imcode.imcms.persistence.entity.Meta.DisabledLanguageShowMode;
import com.imcode.imcms.persistence.entity.Meta.DocumentType;
import com.imcode.imcms.persistence.entity.Meta.PublicationStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
public class DocumentDTO implements Serializable {

    private static final long serialVersionUID = -2317764204932918145L;

    private int id;

    private String title;

    private DocumentType type;

    private String target;

    private String alias;

    private List<LanguageDTO> languages;

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

    private Set<RestrictedPermissionDTO> permissions;

    private Set<RoleDTO> roles;

    private int template;

    private int childTemplate;

}
