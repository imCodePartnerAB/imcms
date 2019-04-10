package com.imcode.imcms.domain.dto;

import com.imcode.imcms.model.Category;
import com.imcode.imcms.model.CommonContent;
import com.imcode.imcms.model.Document;
import com.imcode.imcms.model.RestrictedPermission;
import com.imcode.imcms.persistence.entity.Meta;
import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.util.Value;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class DocumentDTO extends Document implements Cloneable {

    protected static final long serialVersionUID = -1197329246115859534L;

    protected Meta.DocumentType type;

    private Integer id;
    private String target;
    private String alias;
    private List<CommonContentDTO> commonContents;
    private Meta.PublicationStatus publicationStatus;
    private AuditDTO published;
    private AuditDTO archived;
    private AuditDTO publicationEnd;
    private AuditDTO modified;
    private AuditDTO created;
    private Meta.DisabledLanguageShowMode disabledLanguageShowMode;
    private AuditDTO currentVersion;
    private AuditDTO latestVersion;
    private Set<String> keywords;
    private boolean searchDisabled;
    //True is default value
    private boolean linkableByOtherUsers;
    private boolean linkableForUnauthorizedUsers;
    private Set<CategoryDTO> categories;
    private Set<RestrictedPermissionDTO> restrictedPermissions;
    private Map<String, String> properties;
    private Map<Integer, Meta.Permission> roleIdToPermission;

    public DocumentDTO(Document from) {
        super(from);
    }

    /**
     * Constructor for dynamic beans generators such as Jackson library,
     * it shows concrete types of abstract classes that should be used.
     * Don't use it directly.
     */
    @ConstructorProperties({"commonContents", "categories", "restrictedPermissions"})
    public DocumentDTO(List<CommonContentDTO> commonContents,
                       Set<CategoryDTO> categories,
                       Set<RestrictedPermissionDTO> restrictedPermissions) {
        this.categories = categories;
        this.restrictedPermissions = restrictedPermissions;
        this.commonContents = commonContents;
    }

    public List<CommonContent> getCommonContents() {
        return (commonContents == null) ? null : new ArrayList<>(commonContents);
    }

    public void setCommonContents(List<CommonContent> commonContents) {
        this.commonContents = (commonContents == null) ? null
                : commonContents.stream().map(CommonContentDTO::new).collect(Collectors.toList());
    }

    public Set<Category> getCategories() {
        return (categories == null) ? null : new HashSet<>(categories);
    }

    public void setCategories(Set<Category> categories) {
        this.categories = (categories == null) ? null
                : categories.stream().map(CategoryDTO::new).collect(Collectors.toSet());
    }

    public Set<RestrictedPermission> getRestrictedPermissions() {
        return (this.restrictedPermissions == null) ? null : new TreeSet<>(this.restrictedPermissions);
    }

    public void setRestrictedPermissions(Set<RestrictedPermission> restrictedPermissions) {
        this.restrictedPermissions = (restrictedPermissions == null) ? null
                : restrictedPermissions.stream()
                .map(RestrictedPermissionDTO::new)
                .collect(Collectors.toCollection(TreeSet::new));
    }

    @Override
    public DocumentDTO clone() {
        try {
            final DocumentDTO cloneDocumentDTO = (DocumentDTO) super.clone();

            cloneDocumentDTO.setId(null);
            cloneDocumentDTO.setAlias("");
            cloneDocumentDTO.setPublicationStatus(Meta.PublicationStatus.NEW);

            final AuditDTO version = Value.with(
                    new AuditDTO(), auditDTO -> auditDTO.setId(Version.WORKING_VERSION_INDEX)
            );

            cloneDocumentDTO.setCurrentVersion(version);
            cloneDocumentDTO.setCreated(new AuditDTO());
            cloneDocumentDTO.setModified(new AuditDTO());
            cloneDocumentDTO.setArchived(new AuditDTO());
            cloneDocumentDTO.setPublished(new AuditDTO());
            cloneDocumentDTO.setPublicationEnd(new AuditDTO());

            final List<CommonContent> copyCommonContent = cloneDocumentDTO.commonContents
                    .stream()
                    .map(CommonContentDTO::clone)
                    .collect(Collectors.toList());

            cloneDocumentDTO.setCommonContents(copyCommonContent);
            cloneDocumentDTO.setKeywords(new HashSet<>(cloneDocumentDTO.keywords));
            cloneDocumentDTO.setCategories(new HashSet<>(cloneDocumentDTO.categories));
            cloneDocumentDTO.setRestrictedPermissions(new HashSet<>(cloneDocumentDTO.restrictedPermissions));
            cloneDocumentDTO.setRoleIdToPermission(new HashMap<>(cloneDocumentDTO.roleIdToPermission));
            cloneDocumentDTO.setLinkableByOtherUsers(cloneDocumentDTO.linkableByOtherUsers);
            cloneDocumentDTO.setLinkableForUnauthorizedUsers(cloneDocumentDTO.linkableForUnauthorizedUsers);

            return cloneDocumentDTO;
        } catch (CloneNotSupportedException e) {
            return null; // must not happened
        }
    }
}
